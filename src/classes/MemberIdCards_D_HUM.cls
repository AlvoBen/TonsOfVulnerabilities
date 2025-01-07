/*******************************************************************************************************************************
Apex Class Name : MemberIdCards_D_HUM.cls
Version         : 1.0
Created On      : 6th March 2018
Function        : Data Class to fetch Member Id Card Information.
TestClass       : 

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Joel George                 207348                12/20/2016                Original Version (REQ 357335)
* Rajesh Keswani              207348                04/02/2018                Possibility to show entries based on Parent records
* Melkisan Selvaraj           220731                05/04/2018                REQ-350311  Added ID Card/Debit Card section
* Rakesh Patni                207348                05/18/2018               Rquest ID card section updates as part of 6/21 release 
* Pradeep Dani                234619                05/28/2018                Date format update - defect#360377.0001 
* Rakesh Patni                236471                05/30/2018                Defect fix 360456.0001. Modified criteria for future dated policies
* Pradeep Dani                237721                06/01/2018                Story updated to show only active policies
* Pradeep Dani                240035                06/06/2018                361592.0001 Fix - Request ID Card
* Prerna Prasad               240144                06/06/2018                Updated RequestStructureMemberLevelService defect#362214.0001
* Santhi Mandava                                    08/03/2018                Implemented decryption logic for BirthDate__C incase of ciphercloud encryption
* Sunil Bukya                 268461                08/20/2018                 REQ - 367786 - Implemented Member ID fields platform encryption changes.
* Subhamay Mandal                   02/18/2019                 REQ - 386033 Display Additional Warning Message on ID Card Mgmt Page (RF)
* Sunil Bukya                                       12/20/2018                Removed cipherCloud encryption call
* Amar Gadewar                                      04/16/2019                REQ - 396366 Quality - RCC/Pharmacy - Display Additional Warning Message on ID Card Mgmt Page (RF).
* Praveen K Parimi                                  04/10/2019                REQ - 396201 aka CA Ticket - 8067455 - Filter out ETL Deleted records and not display on UI.
* Santhi Mandava                                    04/15/2019                Replaced "Member_Id_Value__c" with Name
* Harish Gowda                                      08/21/2019                REQ - 408204 Quality - RCC/Pharmacy - Display Additional Warning Message on ID Card Mgmt Page (CRM)
* Atia Uzma                                         09/28/2020                User Story 1563575: PR00091574 - MF 1 - Quality - Display updated information messages on the ID Card Management page to reflect accurate guidance as per the revised business process (CRM)
* Atia Uzma                                         02/25/2021               User Story 1956960: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup for 11/20/2020 , 02/12/2021, 03/05/2021 and 3/19/2021 releases (CRM)
* Satyanarayana chenna                              04/09/2021                 US 1918663 PR00094226 - CR AEP readiness - Low Security Issues 
* Pallavi Shewale                                   04/30/2021                User Story 2230075:T1PRJ0002606 - 2021 MF1 -SF - TECH HUmana Constants clean up Member
* Samantha Tennyson                                 10/28/2021                User Story 2786833: T1PRJ0001827 - MF 1 - AHT - Ability to automatically create a case when an ID Card Request Is successfully submitted (RF)
* Tharun Madishetti                                 09/26/2022                US-3468584: T1PRJ0054938 - FR0 9.07 CRM Service must deactivate the function to trigger an ID Card for members who are on the OH Medicaid Pending Plan
*****************************************************************************************************************************************/
public with sharing class MemberIdCards_D_HUM 
{

      Public Static final String MEMBERIDCARDMGT_MEMBERIDBASE_HUM = 'Member-Id-Base';
      Public Static final String  VOB_ACTIVEMEMBERSTATUS_HUM = 'Active';
      Public Static final map<String,String> Platform_Mapping_HUM = New map<String,String>{'LV' => 'CAS','EM' => 'MTV','CB' => 'CB'};
      Public Static final Integer NUMBER_CONSTANT_FOUR_HUM = 4;
      Public Static final String MEMBERIDCARD_HSA = 'HSA';
      Public Static final Integer IDCARDMGT_FROMDATEPERIOD_HUM = -18;
      Public Static final Integer NUMBER_CONSTANT_THREE_HUM = 3;
      Public Static final Map<String,String> Month_Format_HUM = New Map<String,String>{'JAN'=>'01','FEB'=>'02','MAR'=>'03','APR'=>'04','MAY'=>'05','JUN'=>'06','JUL'=>'07','AUG'=>'08','SEP'=>'09','OCT'=>'10','NOV'=>'11','DEC'=>'12'};
      Public Static final String CLOSEDCASE_ZEROVALUE_HUM = '0';
      Public Static final String BACKSLASH_HUM = '/';
      Public Static final String HUMStatusCancelled = 'Cancelled';
    
    /**
    * Returns Policy_Member__C object that can then be utilized for showing Policy member Information.  
    * Exception is handled form the calling method  *
    * This method returns SOQL result of policy member.
    *
    * @param    sPolMemId       policy member ID.
    * @return  Policy_Member__c    return the policy member object
    */
    public Policy_Member__C getPolicyMemberData(String sPolMemId)
    {
            return[SELECT Id, Name,Status__c, Member__c, Member__r.Enterprise_Id__c, Subscriber__c, Policy_Platform__c, Product__c, Product_Type__c,Product_Type_Code__c,Segment_Indicator__c, 
                Group_Number__c, Member__r.Name, Policy__r.Group_Name__r.Source_Customer_Key__c, Policy__r.Coverage_Plan_Effective_Date__c,
                Member__r.Home_Office_Account__c, Policy_Member_External_ID__c,(SELECT Id, Name, Type__c FROM Member_Ids__r WHERE Type__c =:MEMBERIDCARDMGT_MEMBERIDBASE_HUM)
                FROM  Policy_Member__c WHERE Id = :sPolMemId];
    }
    /**
    * Returns Member Ids Name of the policy member.
    * Exception is handled form the calling method  *
    * 
    *
    * @param    sPolMemId       policy member ID.
    * @return  string  Member ID name
    */
    public string getMemIdWithoutDepCode(Policy_Member__c oPolMem)
    {
        string sMemIdWithoutDepCode ='';
            sMemIdWithoutDepCode = (oPolMem.Member_Ids__r != null && !oPolMem.Member_Ids__r.isEmpty()) ? oPolMem.Member_Ids__r.get(0).Name : '';
        return sMemIdWithoutDepCode;
    }
    /**
    * Returns the policy members linked to the subscriber.  
    * Exception is handled form the calling method  *
    * This method returns SOQL result of policy member.
    *
    * @param   sPolicyMemberId- subscriber  policy member ID , sPolicyId - Policy linked to the subscriber
    * @return  List of Policy_Member__c who are related to the subscriber sPolicyMemberId
    */
    public static List<Policy_Member__C> getActivePolicyMembersData(String sPolicyMemberId, String sPolicyId) {
        return [SELECT id, Member__r.PersonMailingStreet, Member__r.PersonMailingCity, Member__r.PersonMailingStateCode,
            Member__r.PersonMailingPostalCode, Member__r.Name, Subscriber__c, Member__r.Birthdate__c, Relationship__c
            FROM Policy_Member__c WHERE Policy__c = :sPolicyId AND ETL_Record_Deleted__c = False AND (Subscriber__c = :sPolicyMemberId OR id = : sPolicyMemberId) AND Status__c =: VOB_ACTIVEMEMBERSTATUS_HUM];
    }
     
    /**
    * Returns true or false for effective date greater than today 
    *
    * @param    objPolMem       policy member object.
    * @return  Boolean    return true or false for effective date greater than today
    */
    public Boolean processEffectiveDate(Policy_Member__c objPolMem)
    {
        boolean retObj = false;
        if(!(objPolMem.Status__c.equalsIgnoreCase('Future')))
        {
            retObj = true;
        }
        return retObj;
    }
    
    /**
     * Returns HIDCMSOrderIdCardRequest_DTO_HUM  object that can then be utilized for ordering Id card.
     * This method is to create the request Structure for Order Id card Service .
     *
     * @param List<String> List Member Id   
     * @return  HIDCMSOrderIdCardRequest_DTO_HUM  request structure of the wrapper class HIDCMSOrderIdCardRequest_DTO_HUM 
     */
    public static HIDCMSOrderIdCardRequest_DTO_HUM buildOrderIdCardREquest(List<String> lstMemberId)
    {
        map<Id, Policy_Member__c> mapMemPols;
        HIDCMSOrderIdCardRequest_DTO_HUM oRequest = new HIDCMSOrderIdCardRequest_DTO_HUM();
        List<HIDCMSOrderIdCardRequest_DTO_HUM.memberDetail> lstMemRequest = new List<HIDCMSOrderIdCardRequest_DTO_HUM.memberDetail>();
        HIDCMSOrderIdCardRequest_DTO_HUM.OrderMemberIdCardDetail orderIDCard = new HIDCMSOrderIdCardRequest_DTO_HUM.OrderMemberIdCardDetail();
        set<Id> setMemSubscriberIds = new set<Id>();
        list<Policy_Member__c> lstMembers = [Select Id, Subscriber__c from Policy_Member__c where Id=: lstMemberId];
        for (Policy_Member__c pObj : lstMembers){ if (pObj.Subscriber__c != null) setMemSubscriberIds.add(pObj.Subscriber__c); }
             mapMemPols = new map<Id, Policy_Member__c>([SELECT Id, Subscriber__c, Policy_Member_External_ID__c, Policy_Platform__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Policy__r.Product__r.ProductCode, Group_Number__c, Subscriber__r.Name, Member__c, Member__r.Mbr_Gen_Key__c, Member__r.Enterprise_id__c, Subscriber__r.Member__c, subscriber__r.Member__r.Enterprise_id__c, subscriber__r.Member__r.Mbr_Gen_Key__c, POlicy__r.Group_Name__r.Enterprise_id__c, Member__r.Birthdate__c, (SELECT Id, Name , Type__c FROM Member_Ids__r WHERE Type__c ='Member-Id-Base') FROM Policy_Member__c WHERE(Id = :lstMemberId OR Id = : setMemSubscriberIds)]);
           
        for (string sId : lstMemberId)
        {
            string memId;
            Policy_Member__c polMemObj = mapMemPols.get(sId);
            HIDCMSOrderIdCardRequest_DTO_HUM.memberDetail memRequest = new HIDCMSOrderIdCardRequest_DTO_HUM.memberDetail();
                memId = (polMemObj.Member_Ids__r != null && !polMemObj.Member_Ids__r.isEmpty()) ? polMemObj.Member_Ids__r.get(0).Name : '';
 
            memRequest.CustCovSeqNbr = fetchCustCovSeqNbr(polMemObj.Policy_Member_External_Id__c);
            memRequest.CustGenKey = polMemObj.POlicy__r.Group_Name__r.Enterprise_id__c;
            memRequest.DateOfBirth = polMemObj.Member__r.Birthdate__c;
            memRequest.GroupNbr = polMemObj.Policy__r.Group_Name__r.Source_Customer_Key__c;
            memRequest.MemberGenKey = polMemObj.Member__r.Mbr_Gen_Key__c;
            memRequest.MemberPersonId = polMemObj.Member__r.Enterprise_id__c;
            memRequest.MemberId = memId;
            memRequest.PlanType = polMemObj.Policy__r.Product__r.ProductCode;
            memRequest.Platform = (Platform_Mapping_HUM.containsKey(polMemObj.Policy_Platform__c) ? Platform_Mapping_HUM.get(polMemObj.Policy_Platform__c) : polMemObj.Policy_Platform__c);
            memRequest.SubscriberGenKey = (polMemObj.Subscriber__c == null ? polMemObj.Member__r.Mbr_Gen_Key__c : polMemObj.Subscriber__r.Member__r.Mbr_Gen_Key__c);
            memRequest.SubscriberPersonId = (polMemObj.Subscriber__c == null ? polMemObj.Member__r.Enterprise_id__c : polMemObj.Subscriber__r.Member__r.Enterprise_id__c);
                memRequest.SubscriberId = (polMemObj.Subscriber__c == null ? memId : ((mapMemPols.get(polMemObj.Subscriber__c).Member_Ids__r != null && !mapMemPols.get(polMemObj.Subscriber__c).Member_Ids__r.isEmpty()) ? mapMemPols.get(polMemObj.Subscriber__c).Member_Ids__r.get(0).Name : ''));
            lstMemRequest.add(memRequest);
        }  
        orderIDCard.memberDto = lstMemRequest;
        oRequest.OrderMemberIdCard = orderIDCard;

        return oRequest;
    }
    
    /**
     * Returns String CustCovSeqNbr for ordering Id card.
     * This method is to create the fetch CustCovSeqNbrfrom Policy member external id  .
     *
     * @param String  Extenal Member Id   
     * @return  Strign 
     */
    public static String fetchCustCovSeqNbr(String sExternalMemberId)
    {
        String sReturnString = sExternalMemberId;
        if (String.isNotBlank(sExternalMemberId))
        {
            List<String> lstExtenalId = sExternalMemberId.split('\\|');
            if (!lstExtenalId.isEmpty() && lstExtenalId.size() > NUMBER_CONSTANT_FOUR_HUM)
            {
                sReturnString = lstExtenalId.get(NUMBER_CONSTANT_FOUR_HUM);
            }
        }
        return sReturnString;
    }
    
    /**
    * Returns true or false if the Product is of Type HSA and the Account is non Home Office
    *
    * @param   objPolMem        Policy Member Object.
    * @return  Boolean          Returns true if Product Type is HSA and Account is non Home Office
    */
    public Boolean isHSANonHomeOffice(Policy_Member__c objPolMem) 
    {
        Boolean bIsHSANonHomeOffice = false;
        if (objPolMem.Product_Type__c == MEMBERIDCARD_HSA && objPolMem.Member__r.Home_Office_Account__c != true) 
        {
            bIsHSANonHomeOffice = true;
        }
        return bIsHSANonHomeOffice;
    }
    
    /**
     * Returns HIDCMSMemberSearchRequest_DTO_HUM object.
     * This method is to prepare the inputs for member level search servivce  .
     *
     * @param String  grpNumber
     * @param String  memberId 
     * @return  HIDCMSMemberSearchRequest_DTO_HUM 
     */
    public static HIDCMSMemberSearchRequest_DTO_HUM RequestStructureMemberLevelService(String grpNumber, String memberId)
    {
        HIDCMSMemberSearchRequest_DTO_HUM requestObj = new HIDCMSMemberSearchRequest_DTO_HUM();
        HIDCMSMemberSearchRequest_DTO_HUM.MemberSearchRequestDTO requestDTO = new HIDCMSMemberSearchRequest_DTO_HUM.MemberSearchRequestDTO();
        requestDTO.GroupNumber = grpNumber;
        requestDTO.FromDate = system.today().addMonths(IDCARDMGT_FROMDATEPERIOD_HUM).toStartOfMonth().format();
        requestDTO.ToDate = system.today().format();
        requestDTO.MemberID = memberId;
        requestObj.MemberSearchRequest = requestDTO;
        return requestObj;
    }
    
    /**
     * Returns MemberLevelSearch_DTO_HUM object.
     * This method is parse the response of member level search servivce  .
     *    
     * @param HIDCMSMemberSearchResponse_DTO_HUM  oResponse 
     * @return  MemberLevelSearch_DTO_HUM 
     */
    public static  MemberLevelSearch_DTO_HUM prepareMemberLevelSerchResponse(HIDCMSMemberSearchResponse_DTO_HUM oResponse)
    {
        List<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO> ParentList = new List<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO>();
        MemberLevelSearch_DTO_HUM tempDTO;
        HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchResponse memberSearchRes = oResponse.MemberSearchResponse;
        if (memberSearchRes.IsError !='true')
        {
            list<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO> memSerDTO = memberSearchRes.MemberSearchDetail;
            Map<string, list<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO>> mapOfDTO = new Map<string, list<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO>>();
            list<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO> listMemberDTO;
            if (memSerDTO != null && memSerDTO.size() > 0)
            {
                for (HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO temObj : memSerDTO)
                {
                    if (String.isNotBlank(temObj.RequestDate)) temObj.RequestDate = tranformDate(temObj.RequestDate);
                    if (string.isNotBlank(temObj.FirstName) && string.isNotBlank(temObj.LastName) && mapOfDTO.get(temObj.FirstName + ',' + temObj.LastName + ',' + temObj.CardRequestDate) != null)
                    {
                        mapOfDTO.get(temObj.FirstName + ',' + temObj.LastName + ',' + temObj.CardRequestDate).add(temobj);
                    }
                    else if (string.isNotBlank(temObj.FirstName) && string.isNotBlank(temObj.LastName) && string.isNotBlank(temObj.CardRequestDate))
                    {
                        listMemberDTO = new list<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO>();
                        listMemberDTO.add(temObj);
                        mapOfDTO.put(temObj.FirstName + ',' + temObj.LastName + ',' + temObj.CardRequestDate, listMemberDTO);
                        ParentList.add(temObj);
                    }
                    else {
                        listMemberDTO = new list<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO>();
                        listMemberDTO.add(temObj);
                        mapOfDTO.put(temObj.FirstName + ',' + temObj.LastName + ',' + temObj.CardRequestDate, listMemberDTO);
                        ParentList.add(temObj);
                    }
                }
            }

            tempDTO = new MemberLevelSearch_DTO_HUM();
            tempDTO.mapMemberLevelDTO = mapOfDTO;
            tempDTO.parentNodes = ParentList;

        }
        return tempDTO;
    }

    /**
    * Returns String
    * This method is parse the date .
    *
    * @param String   DD MMM YYYY
    * @return  String MM/DD/YYYY
    */
    private Static String tranformDate(String sDate)
    {
        String sFormattedDate = sDate;
        if (String.isNotBlank(sDate))
        {
            List<String> lstDate = sDate.split(' ');
            if (lstDate != null && lstDate.size() == NUMBER_CONSTANT_THREE_HUM)
            {
                if (MONTH_FORMAT_HUM.containsKey(lstDate[1]))
                {
                    String sDay = lstDate[0].length() > 1 ? lstDate[0] : CLOSEDCASE_ZEROVALUE_HUM + lstDate[0];
                    sFormattedDate = MONTH_FORMAT_HUM.get(lstDate[1]) + BACKSLASH_HUM + sDay + BACKSLASH_HUM + lstDate[2];
                }
            }
        }
        return sFormattedDate;
    }
    
    
    

    /**
    * Method will check last 7days cases for ID Card Request.
    * @param String: sPolicyMemberId
    * @param String: sAccountId
    * @param String: sInteractionId
    * @return Boolean 
    */
    public static Boolean getIdCardRequestInL7Days(List<Policy_Member__c> sPolicyMember,String sInteractionId) {
        List<Case> lstExistingCasesIDCardReq    = new List<Case>();
		set<id> lstPolId = new set<id>();
		set<id> lstPolMemberId = new set<id>();
		for(Policy_Member__C obj : sPolicyMember){
			lstPolId.add(obj.id);
			lstPolMemberId.add(obj.Member__c);
		}
			lstExistingCasesIDCardReq = [SELECT Id FROM Case
                                      WHERE Policy_Member__c IN :lstPolId AND AccountId IN :lstPolMemberId
                                      AND  CTCI_List__r.Classification__r.Name =: Constants_C_AHT_HUM.FULLFILLMENT_CLASSIFICATION 
                                      AND  CTCI_List__r.Intent__r.Name =: Constants_C_AHT_HUM.ICACR_INTENT 
                                      AND Interaction_ID__c != :sInteractionId 
                                      AND  Policy_Member__r.Policy__r.Segment_Indicator__c IN ('Individual-Medicare','LargeGroup-Medicare','Individual-Medicaid')
                                      AND Status != :HUMStatusCancelled
                                      AND createddate = LAST_N_DAYS:7];
        return !lstExistingCasesIDCardReq.isEmpty();      
    }
    
    /*
    * Queries the user's current queue
    * 
    * @return string: The current User's Record
    */  
    public static User queryUserQueue()
    { 
       User oUser = [SELECT Current_Queue__c 
                            FROM User 
                            WHERE Id = :UserInfo.getUserId()];
       return oUser;
    }

    /*
    * Queries the CTCI Junction object for Classification Types
    * @return CTCI_Junction__c: return Classifcation and Intent based on Classification Type
    * @see The queried record may appear on the Case Edit page
    */
    public static List<CTCI_Junction__c> queryActiveCTCI(String sClassificationType)
    {
        List<CTCI_Junction__c> lstCtci = new List<CTCI_Junction__c>();
        try{
            String sClassification = 'Fulfillment'; String sIntent = 'ID Card/Access Card Request';
            
            lstCtci = [select id, Classification_Type__c, Classification__c, Intent__c from CTCI_Junction__c where Classification_Type__r.name = :sClassificationType AND Classification__r.Name =: sClassification AND Intent__r.Name =: sIntent and IsActive__c = true];
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_D_HUM', 'queryActiveCTCI');
        }
        return lstCtci;       
    }  
    
     /*
    * Queries the Case Details
    * 
    * @return Object: Case
    */  
    public static Case queryCaseDetails(Id caseId){
        Case caseNew;
        try{
            caseNew = [Select id, Classification_Type__c, Interacting_With_Type__c from Case where Id=:caseId];
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_D_HUM', 'queryCaseClassType');
        }
        return caseNew;
    }
    
    /**
    * Method will check required Group number is available in custom label or not.
    * @param String: sPolMemId
    * @return Boolean 
    */
    public static Boolean checkPendingPlan(String sPolMemId){
        boolean checkGrpNo = false;
        try{
            Policy_Member__c pom = [SELECT Id, Group_Number__c FROM  Policy_Member__c WHERE Id = :sPolMemId];
            if(pom != null && pom.Group_Number__c != null){
                String groupNumber = pom.Group_Number__c;
                String customGroupNumber = System.Label.ID_Card_Pending_Plans;            
                if(customGroupNumber.contains(groupNumber))
                    checkGrpNo = true;
                else
                    checkGrpNo = false;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_D_HUM', 'checkPendingPlan');
        }
        return checkGrpNo;
    }
   
}