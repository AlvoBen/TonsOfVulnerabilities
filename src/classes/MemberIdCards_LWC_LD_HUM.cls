/*******************************************************************************************************************************
Apex Class Name : MemberIdCards_LWC_LD_HUM.cls
Version         : 1.0
Created On      : 9th Feb 2022
Function        : Data Class to fetch Member Id Card Information.
TestClass       : 

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi                              02/09/2022                   Initial Version
* Anuradha Gajbhe                                       05/09/2022                   User Story 3205329: ID cards adding Product Type Code to the order cart within the left panel of the Ordering ID Card Screen.
* Anuradha Gajbhe                                       12/05/2022                   User Story 3906892: Lightning - Member ID Cards - Home Office.
* Anuradha Gajbhe                                       02/02/2023                   User Story 4060591: Lightning - ID Cards - RCC Auto create Case for ID Cards: Ability to automatically create a case when an ID Card Request is successfully submitted.
* Anuradha Gajbhe                                       04/03/2023                   User Story 4400042: Lightning - Contact Servicing: Contract Protected Data (CPD)/SDUP Gaps.
* Raj Paliwal                                           04/04/2023                   User Story 4369943: Lightning - Contact Servicing-ID Cards- Add PCP Link on Member ID Card Page.
* Raj Paliwal                                           04/06/2023                   Defect Fix: 7493.
* Sagar G                                               05/07/2023                   User Story 4739984: T1PRJ0865978 - MF 4374416 - C10; Contact Servicing: Switch Clean-up for US 4400042
* Anuradha Gajbhe                                       07/14/2023                   US: 4325820: RCC Auto create Case for State ID Cards: Ability to automatically create a case when a State ID Card Request is successfully submitted.(Lightning)
* Raj Paliwal	                                        07/14/2023                   US: 4272710: Ability to request a State ID Card from the ID Card Managment Page(Lightning)
* Vishal Shinde                                         07/14/2023                   User Story 3891752: CRM to populate Previous State ID Card Requests (Lightning)
* Disha Dhole                                           08/10/2023                   USer Story 4791169 CarePlus - Lightning - Restrict the ability to resend CarePlus Dental ID Cards
*****************************************************************************************************************************************/
public with sharing class MemberIdCards_LWC_LD_HUM 
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
      Public Static final String HUMSourceCode = 'CRM';
      Public Static final String HUMStateIdCardInd = 'Y';
      @TestVisible public static ID curentUserID;
      @TestVisible Public Static Boolean bIsMemberAHomeOfficeAcct;
      @TestVisible Public Static Boolean bIsNonHOUser;
      @TestVisible Public Static Boolean bisHomeOfficeAllUser;
      @TestVisible Public Static Boolean bisHomeOfficeMedicalUser;
      @TestVisible Public Static Boolean bisHomeOfficeDentalUser;
      public Static Boolean bHomeOficeMessage{get;set;}
      
      public static Boolean bIdCardPCPTabSwitch {
        get{
        return HUMUtilityHelper.isCRMFunctionalityON('4369943');
        }
        set;
      }
    
    /**
    * Returns Member Ids Name of the policy member.
    * Exception is handled form the calling method  *
    * 
    *
    * @param    sPolMemId       policy member ID.
    * @return  string  Member ID name
    */
   
    @AuraEnabled(cacheable=true)
    public static string getMemIdWithoutDepCode(String polMemId)
    {
        String NameFmt;
        string sMemIdWithoutDepCode ='';
        MemberPlan oPolMem=[SELECT Id, Name, MemberId FROM  MemberPlan WHERE Id = :polMemId];
        if (oPolMem.Name != null && oPolMem.Name != ''){
            if (oPolMem.Name.length() > 9){
                NameFmt = oPolMem.Name.Substring(0,oPolMem.Name.length()- 2);
            }
            else{
                NameFmt = oPolMem.Name;
            } 
        }
        sMemIdWithoutDepCode = (oPolMem.Name != null && oPolMem.Name != '') ? NameFmt : '';
        return sMemIdWithoutDepCode;
    }
     
    /**
    * Returns the policy members linked to the subscriber.  
    * Exception is handled form the calling method  *
    * This method returns SOQL result of policy member.
    *
    * @param   sPolicyMemberId- subscriber  policy member ID , sPolicyId - Policy linked to the subscriber
    * @return  List of MemberPlan who are related to the subscriber sPolicyMemberId
    */
    
    @AuraEnabled
    public static List<MemberPlan> getActivePolicyMembersData1(String sPolicyMemberId, String sPolicyId) {
		return [SELECT id, Name,  Member.PersonMailingStreet, Member.PersonMailingCity, Member.PersonMailingStateCode,
			Member.PersonMailingPostalCode, Member.Name, Subscriberid, Member.Birthdate__c, RelationshipToSubscriber, Segment_Indicator__c , Product_Type__c
			FROM MemberPlan WHERE planId = :sPolicyId AND ETL_Record_Deleted__c = False AND (Subscriberid = :sPolicyMemberId OR id = : sPolicyMemberId OR MemberId =: sPolicyMemberId) AND Member_Coverage_Status__c =: VOB_ACTIVEMEMBERSTATUS_HUM];
    }
    
    @AuraEnabled
        public static Object getPoliciesMed(String recId){
        String sRecId = recId;
        List<MemberPlan> lstPolMem = [SELECT Id, Name,Member_Coverage_Status__c, MemberId, Member.Enterprise_Id__c, Subscriberid, Policy_Platform__c, Product__c, Product_Type__c,Product_Type_Code__c,Segment_Indicator__c, 
        GroupNumber, Member.Name, planId, Plan.Payer.Source_Customer_Key__c, Plan.EffectiveFrom,
        Member.Home_Office_Account__c, Alternate_Description__c,Member_Plan_External_Id__c, Plan.Home_Office_Account__c,Plan.Policy_Group_Number__c, Issue_state__c, Medicaid_Id__c
        FROM  MemberPlan WHERE Member_Coverage_Status__c != 'Termed' AND ETL_Record_Deleted__c = False and 
        Policy_Platform__c NOT IN ('CB','CS','FM','PR') and Product_Type__c NOT IN ('OHBD','POSD','OPPD','SUPPD','OCHD','SUPD') AND Product__c NOT IN ('LIF','STI','VIS') AND Plan.Payer.Source_Customer_Key__c !='684668' 
        AND ( Product__c !='DEN' OR (NOT (Alternate_Description__c LIKE '%PED DEN%'))) AND
        MemberId = :sRecId and Product__c = 'MED'];
        
            HUMPolicyWrapperHelper objPolicyWrapper = new HUMPolicyWrapperHelper();
            objPolicyWrapper.mapRecordAccess = new Map<Id, Boolean>();
            objPolicyWrapper.mapPolicyPlans = new Map<Id, Id>();
            Map<Id, MemberPlan> mapMemberPlans = new Map<Id, MemberPlan>();
            Set<Id> setIds = new Set<Id>();

                if(lstPolMem != Null && !lstPolMem.isEmpty()){
                    objPolicyWrapper.lstMemberPlans = lstPolMem;
                    objPolicyWrapper.bIdCardPCPTabSwitch = bIdCardPCPTabSwitch;
                    for(MemberPlan obj : lstPolMem){
                        setIds.Add(obj.PlanId);
                        objPolicyWrapper.mapPolicyPlans.put(obj.Id,obj.PlanId);
                    }
                    if(setIds != Null && !setIds.isEmpty()){
                        for(UserRecordAccess userMemPlanAccess : [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : setIds LIMIT 200])
                        {                    
                            objPolicyWrapper.mapRecordAccess.put(userMemPlanAccess.RECORDID,userMemPlanAccess.HASREADACCESS);
                        }
                    }
                }
                return objPolicyWrapper;
        
	} 
        
    @AuraEnabled
    	public static object getPoliciesDen(String recId){
        String sRecId = recId;
        
        List<MemberPlan> lstPolMem = [SELECT Id, Name,Member_Coverage_Status__c, MemberId, Member.Enterprise_Id__c, Subscriberid, Policy_Platform__c, Product__c, Product_Type__c,Product_Type_Code__c,Segment_Indicator__c, GroupNumber, Issue_state__c, Medicaid_Id__c, Member.Name, planId, Plan.Payer.Source_Customer_Key__c, Plan.EffectiveFrom, Member.Home_Office_Account__c, Alternate_Description__c,Member_Plan_External_Id__c,Plan.Home_Office_Account__c,Plan.Policy_Group_Number__c FROM  MemberPlan WHERE Member_Coverage_Status__c != 'Termed' AND ETL_Record_Deleted__c = False and Policy_Platform__c NOT IN ('CB','CS','FM','PR') and Product_Type__c NOT IN ('OHBD','POSD','OPPD','SUPPD','OCHD','SUPD') AND Product__c NOT IN ('LIF','STI','VIS') AND Plan.Payer.Source_Customer_Key__c !='684668' AND ( Product__c !='DEN' OR (NOT (Alternate_Description__c LIKE '%PED DEN%'))) AND MemberId = :sRecId and Product__c = 'DEN'];
        
            HUMPolicyWrapperHelper objPolicyWrapper = new HUMPolicyWrapperHelper();
            objPolicyWrapper.mapRecordAccess = new Map<Id, Boolean>();
            objPolicyWrapper.mapPolicyPlans = new Map<Id, Id>();
            Map<Id, MemberPlan> mapMemberPlans = new Map<Id, MemberPlan>();
            Set<Id> setIds = new Set<Id>();
        
                if(lstPolMem != Null && !lstPolMem.isEmpty()){
                    objPolicyWrapper.lstMemberPlans = lstPolMem;
                    objPolicyWrapper.bIdCardPCPTabSwitch = bIdCardPCPTabSwitch;
                    for(MemberPlan obj : lstPolMem){
                        setIds.Add(obj.PlanId);
                        objPolicyWrapper.mapPolicyPlans.put(obj.Id,obj.PlanId);
                    }
                    if(setIds != Null && !setIds.isEmpty()){
                        for(UserRecordAccess userMemPlanAccess : [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : setIds LIMIT 200])
                        {
                            objPolicyWrapper.mapRecordAccess.put(userMemPlanAccess.RECORDID,userMemPlanAccess.HASREADACCESS);
                        }
                    }
                }
            return objPolicyWrapper;
        
	} 

    @AuraEnabled
    public static List<string> DentalCarePlusMTVGrpNumResLst(){
        List<string> grpNoLst = new List<string>();
        try 
        {
            List<IDCards_GroupNumber_Restriction_List__c> lst = IDCards_GroupNumber_Restriction_List__c.getall().values();
            for(IDCards_GroupNumber_Restriction_List__c obj : lst){
                grpNoLst.add(obj.GroupNumber__c);
            } 
        } catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_LD_HUM', 'DentalCarePlusMTVGrpNumResLst');
        }
        return grpNoLst;
    }
    
	
    	/**
     * Returns CICMSUpdateRequest_LDTO_HUM  object that can then be utilized for ordering Id card.
     * This method is to create the request Structure for Order Id card Service .
     *
     * @param List<String> List Member Id   
     * @return  CICMSUpdateRequest_LDTO_HUM  request structure of the wrapper class CICMSUpdateRequest_LDTO_HUM 
     */
    @AuraEnabled
    public static CICMSUpdateRequest_LDTO_HUM buildStateOrderIdCardRequest(String medicaidID, String issueStateCode, String idCardReqstReason)
    {
        CICMSUpdateRequest_LDTO_HUM oRequest = new CICMSUpdateRequest_LDTO_HUM();
        CICMSUpdateRequest_LDTO_HUM.demographicUpdateRequestDetail StateorderIDCard = new CICMSUpdateRequest_LDTO_HUM.demographicUpdateRequestDetail();
        CICMSUpdateRequest_LDTO_HUM.CommonFieldsDetail CmmRequest = new CICMSUpdateRequest_LDTO_HUM.CommonFieldsDetail();
        CICMSUpdateRequest_LDTO_HUM.MemberFieldsDetail memRequest = new CICMSUpdateRequest_LDTO_HUM.MemberFieldsDetail();
        
        CmmRequest.MedicaidID = medicaidID;
        CmmRequest.IssueStateCode = issueStateCode;
        CmmRequest.SourceCode = HUMSourceCode;

        memRequest.StateIdCardChgInd = HUMStateIdCardInd;
        memRequest.StateIdCardReqstInd = HUMStateIdCardInd;
        memRequest.IdCardReqstReason = idCardReqstReason;
		
        StateorderIDCard.CommonFields = CmmRequest;
        StateorderIDCard.MemberFields = memRequest;

        oRequest.demographicUpdateRequest = StateorderIDCard;

        return oRequest;   
    } 

    /**
     * Returns CICMSInquiryRequest_LDTO_HUM  object that can then be utilized for ordering Id card.
     * This method is to create the request Structure for Order Id card Service .
     *
     * @param String medicaidID, String issueStateCode, String StateIdCardReqstInd   
     * @return  CICMSInquiryRequest_LDTO_HUM  request structure of the wrapper class CICMSInquiryRequest_LDTO_HUM 
     */
    @AuraEnabled
    public static CICMSInquiryRequest_LDTO_HUM buildInquiryServiceRequest(String medicaidID, String issueStateCode, String StateIdCardReqstInd)
    {
        CICMSInquiryRequest_LDTO_HUM oRequest = new CICMSInquiryRequest_LDTO_HUM();
        CICMSInquiryRequest_LDTO_HUM.demographicInquiryRequestDetail StateInquiry = new CICMSInquiryRequest_LDTO_HUM.demographicInquiryRequestDetail();
        StateInquiry.MedicaidID=medicaidID;
        StateInquiry.IssueStateCode=issueStateCode;
        StateInquiry.SourceCode = HUMSourceCode;
        StateInquiry.StateIdCardReqstInd = StateIdCardReqstInd;
        oRequest.demographicInquiryRequest = StateInquiry;
        return oRequest;
        
    }
    
    /**
     * Returns HIDCMSOrderIdCardRequest_DTO_HUM  object that can then be utilized for ordering Id card.
     * This method is to create the request Structure for Order Id card Service .
     *
     * @param List<String> List Member Id   
     * @return  HIDCMSOrderIdCardRequest_DTO_HUM  request structure of the wrapper class HIDCMSOrderIdCardRequest_DTO_HUM 
     */
    @AuraEnabled
    public static HIDCMSOrderIdCardRequest_DTO_HUM buildOrderIdCardREquest(List<String> lstMemberId)
    {
        map<Id, MemberPlan> mapMemPols;
        HIDCMSOrderIdCardRequest_DTO_HUM oRequest = new HIDCMSOrderIdCardRequest_DTO_HUM();
        List<HIDCMSOrderIdCardRequest_DTO_HUM.memberDetail> lstMemRequest = new List<HIDCMSOrderIdCardRequest_DTO_HUM.memberDetail>();
        HIDCMSOrderIdCardRequest_DTO_HUM.OrderMemberIdCardDetail orderIDCard = new HIDCMSOrderIdCardRequest_DTO_HUM.OrderMemberIdCardDetail();
        set<Id> setMemSubscriberIds = new set<Id>();
        list<MemberPlan> lstMembers = [Select Id, Subscriberid from MemberPlan where Id=: lstMemberId];
        for (MemberPlan pObj : lstMembers){ if (pObj.Subscriberid != null) setMemSubscriberIds.add(pObj.Subscriberid); }
             mapMemPols = new map<Id, MemberPlan>([SELECT Id, Name,Member_Id_Base__c,plan.Product__r.ProductCode, GroupNumber,
                                                   Subscriberid, Member_Plan_External_Id__c, Policy_Platform__c, Memberid,
                                                   Plan.Payer.Source_Customer_Key__c, Plan.Payer.Enterprise_id__c,
                                                   Member.Mbr_Gen_Key__c, Member.Enterprise_id__c, Member.Birthdate__c,
                                                   SubscriberPlanId__r.Memberid, SubscriberPlanId__r.Member.Enterprise_id__c,
                                                   SubscriberPlanId__r.Member.Mbr_Gen_Key__c FROM MemberPlan
                                                   Where(Id = :lstMemberId OR Id = : setMemSubscriberIds)]);
           
        for (string sId : lstMemberId)
        {
            string memId;
            MemberPlan polMemObj = mapMemPols.get(sId);
            HIDCMSOrderIdCardRequest_DTO_HUM.memberDetail memRequest = new HIDCMSOrderIdCardRequest_DTO_HUM.memberDetail();
            if(polMemObj != null && polMemObj.Name != '')
            {
                memId = polMemObj.Name.Substring(0,polMemObj.Name.length()- 2);
            }
            else
            {
                memId = '';
            }

            memRequest.CustCovSeqNbr = fetchCustCovSeqNbr(polMemObj.Member_Plan_External_Id__c);
            memRequest.CustGenKey = polMemObj.plan.payer.Enterprise_id__c;
            memRequest.DateOfBirth = polMemObj.Member.Birthdate__c;
            memRequest.GroupNbr = polMemObj.Plan.Payer.Source_Customer_Key__c;
            memRequest.MemberGenKey = polMemObj.Member.Mbr_Gen_Key__c;
            memRequest.MemberPersonId = polMemObj.Member.Enterprise_id__c;
            memRequest.MemberId = memId;
            memRequest.PlanType = polMemObj.plan.Product__r.ProductCode;
            memRequest.Platform = (Platform_Mapping_HUM.containsKey(polMemObj.Policy_Platform__c) ? Platform_Mapping_HUM.get(polMemObj.Policy_Platform__c) : polMemObj.Policy_Platform__c);
            memRequest.SubscriberGenKey = (polMemObj.Subscriberid == null ? polMemObj.Member.Mbr_Gen_Key__c : polMemObj.SubscriberPlanId__r.Member.Mbr_Gen_Key__c);
            memRequest.SubscriberPersonId = (polMemObj.Subscriberid == null ? polMemObj.Member.Enterprise_id__c : polMemObj.SubscriberPlanId__r.Member.Enterprise_id__c);
            memRequest.SubscriberId = (polMemObj.Subscriberid == null ? memId : ((mapMemPols.get(polMemObj.Id).Member_Id_Base__c != null && mapMemPols.get(polMemObj.Id).Member_Id_Base__c != '' ? mapMemPols.get(polMemObj.Id).Member_Id_Base__c : '')));
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
    @AuraEnabled
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
     * Returns HIDCMSMemberSearchRequest_DTO_HUM object.
     * This method is to prepare the inputs for member level search servivce  .
     *
     * @param String  grpNumber
     * @param String  memberId 
     * @return  HIDCMSMemberSearchRequest_DTO_HUM 
     */
    @AuraEnabled
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
    @AuraEnabled
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
    @AuraEnabled
    public Static String tranformDate(String sDate)
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
    @AuraEnabled
    public static Boolean getIdCardRequestInL7Days(List<MemberPlan> sPolicyMember,String sInteractionId) {
        List<Case> lstExistingCasesIDCardReq    = new List<Case>();
        set<id> lstPolId = new set<id>();
        set<id> lstPolMemberId = new set<id>();
        for(MemberPlan obj : sPolicyMember){
            lstPolId.add(obj.id);
            lstPolMemberId.add(obj.Memberid);
        }
            lstExistingCasesIDCardReq = [SELECT Id FROM Case
                                      WHERE Member_Plan_Id__c IN :lstPolId AND AccountId IN :lstPolMemberId
                                      AND  CTCI_List__r.Classification__r.Name =: Constants_C_AHT_HUM.FULLFILLMENT_CLASSIFICATION 
                                      AND  CTCI_List__r.Intent__r.Name =: Constants_C_AHT_HUM.ICACR_INTENT 
                                      AND Interaction_ID__c != :sInteractionId 
                                      AND  Member_Plan_Id__r.Policy__r.Segment_Indicator__c IN ('Individual-Medicare','LargeGroup-Medicare','Individual-Medicaid')
                                      AND Status != :HUMStatusCancelled
                                      AND createddate = LAST_N_DAYS:7];
        return !lstExistingCasesIDCardReq.isEmpty();      
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
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_LD_HUM', 'queryActiveCTCI');
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
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_LD_HUM', 'queryCaseClassType');
        }
        return caseNew;
    }

    public class HUMPolicyWrapperHelper{
        @AuraEnabled
        public List<MemberPlan> lstMemberPlans;
        @AuraEnabled
        public Map<Id, Boolean> mapRecordAccess;
        @AuraEnabled
        public Map<Id, Id> mapPolicyPlans;
        @AuraEnabled
        public Boolean bIdCardPCPTabSwitch;
    }
}