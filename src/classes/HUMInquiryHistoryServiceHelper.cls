/**********************************************************************************************************************************************
Apex Class Name  : HUMInquiryHistoryServiceHelper
Version          : 1.0
Created Date     : April 03 2015
Function         : Invoke the Inquiry history webservice
Test Class       : HUMInquiryHistoryServiceHelperTest 
Modification Log: 
 * Developer Name         Code Review                    Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma            22273                        04/03/2015                   Original Version     
 * Vishal Verma            22273                        04/10/2015                   Added extra null check at line 215 
 * Vishal Verma            23559                        06/02/2015                   Added code to fetch dependent informations. 
 * Vishal Verma            23559                        06/10/2015                   Added code for adding Policy member for dependents.
 * Vishal Verma            24818                        07/16/2015                   Defect 187185.0001 - Moved the code out of for loop to avoid adding duplicate records. (Line 355 , 366 , 371).                                                             
 * Tanushree Hazari        26433                        09/16/2015                   Changed the mapping of Category and Reason fields in constructWrapperHelper() method
                                                                                     as part of CA ticket 5220296
 * Tanushree Hazari        28298                        11/24/2015                   Changed the mapping of Case Origin field in constructWrapperHelper() method
                                                                                     as part of CA ticket 5131899.
 * Praveen Kumar Parimi    43503                        10/12/2016                   Changed the mapping of Case Origin field to MediaTypeCodeDescription as part of CA ticket 5131899. 
 * Praveen Kumar Parimi    252845                       07/05/2018                   Changed nameValuePair.FilterType and nameValuePair.FilterValue as part of REQ - 366135 aka CA 7458629.
 * Vijay Gurusamy		   282145		                09/05/2018                   REQ - 367015 - Change the filter for Inquire history servide call.
 * Satyam Malviya          282382                       09/06/2018                   REQ -371242 -  SF-TECH- Accessibility to see all Cases on CCP/GCP Legacy History section
 * Santhosh Ganji                                       04/18/2019                   REQ - 383696 - MF 7- Display all the records in Legacy History section for HP Users
 * Prudhvi Pamarthi                                     06/04/2021                   US2270790 Global Constant Class Changes
 *************************************************************************************************************************************************************************************/
public with sharing virtual class HUMInquiryHistoryServiceHelper 
{
    Set <String> setExtenalId = new Set <String>(); // Set of External Id , used to display response for Member Type Account
    Set<String> setGroups = new Set<String>(); // Set of Groups to which the logged In User belongs
    List<Policy_Member__c> lstPolicyMember = new List<Policy_Member__c>(); // Policy Member list
    Set<Id> setSubscriberPolicyMem = new Set<Id>();
    Set<Id> setDependentPolicyMem = new Set<Id>(); 
    
    Private string sMinDate = String.ValueOf(HUMConstants__c.getInstance('MIN_DATE_TIME').StringValue__c);
    Private string sZero = String.ValueOf(HUMConstants__c.getInstance('ZERO').StringValue__c);
    Private Integer iInquirySearchLimit =Integer.ValueOf(HUMConstants__c.getInstance('INQUIRY_SEARCH_LIMIT').IntegerValue__c);
    Private string sMemRecType = String.ValueOf(HUMConstants__c.getInstance('ACCOUNT_MEMBER_TYPE').StringValue__c);
    Private string sProviderRecType = String.ValueOf(HUMConstants__c.getInstance('PROVIDER_RECORDTYPE').StringValue__c);
    Private string sLaunchGCPCCP_KM = String.ValueOf(HUMConstants__c.getInstance('LAUNCH_GCPCCP_KM').StringValue__c);
    Private string sLaunchGCPCCP_KP = String.ValueOf(HUMConstants__c.getInstance('LAUNCH_GCPCCP_KP').StringValue__c);
    Private string sLaunchGCPCCP_KA = String.ValueOf(HUMConstants__c.getInstance('LAUNCH_GCPCCP_KA').StringValue__c);
    Private string sLaunchGCPCCP_KE = String.ValueOf(HUMConstants__c.getInstance('LAUNCH_GCPCCP_KE').StringValue__c);
    Private string sAgentRecType = String.ValueOf(HUMConstants__c.getInstance('AGENT_RECORDTYPE').StringValue__c);
    Private string sGroupRecType = String.ValueOf(HUMConstants__c.getInstance('ACCOUNT_GROUP_TYPE').StringValue__c);
    Private string sHomeOfficeAll = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_ALL').StringValue__c);
    Private string sHomeOfficeMed = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_MEDICAL').StringValue__c);
    Private string sHomeOfficeDen = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_DENTAL').StringValue__c);
    Private static string sMedicalProductsKeywords = String.ValueOf(HUMConstants__c.getInstance('MEDICAL_PRODUCT_KEYWORDS').StringValue__c);
    Private static string sGCP = String.ValueOf(HUMConstants__c.getInstance('GCP').StringValue__c);
    Private static string sGNA = String.ValueOf(HUMConstants__c.getInstance('GNA').StringValue__c);
    Private static string sINQA = String.ValueOf(HUMConstants__c.getInstance('INQA').StringValue__c);
    Private Integer iConstThree = Integer.ValueOf(HUMConstants__c.getInstance('HUMConstantThree').IntegerValue__c);
    Private Integer iConstFour = Integer.ValueOf(HUMConstants__c.getInstance('HUMConstantFour').IntegerValue__c);
    Private Integer iConstFive = Integer.ValueOf(HUMConstants__c.getInstance('HUMConstantFive').IntegerValue__c);
    Private static string sDentalProductsSet1 = String.ValueOf(HUMConstants__c.getInstance('DENTAL_PRODUCT_KEYWORDS_SET1').StringValue__c);
    Private static string sDentalProductsSet2 = String.ValueOf(HUMConstants__c.getInstance('DENTAL_PRODUCT_KEYWORDS_SET2').StringValue__c);
    Private static string sSemicolon = String.ValueOf(HUMConstants__c.getInstance('SEMICOLON').StringValue__c);
    Private string sSDR =String.ValueOf(HUMConstants__c.getInstance('SDR').StringValue__c);
    Private static string sGenKey =String.ValueOf(HUMConstants__c.getInstance('GENKEY').StringValue__c);
    Public Static final String sEnterpriseId = 'EnterpriseId';
    Public Static final String shpHomeOffice = 'Humana Pharmacy Home Office';
    Public Static final String sInquiryFilterType = 'CRD_TYPE_DESC';
    Public Static final String sInquiryNonHPfilterType = 'Non-RightSource CRD Type';
    
    
    
    
    /* 
     * Method Name : initiateInquiryHistoryService
     * Description : This function is used for intiate the Webservice and send the response back
     * Return type : list<HUMInquiryHistoryWrapperHelper>
     * Paramater   : Account , boolean , String , String , String , String
     */
    public list<HUMInquiryHistoryWrapperHelper> initiateInquiryHistoryService(Account oAcc, boolean bFamilyMem, String sPolMemberId , String sStartDate , String sEndDate , String sRefereceId)
    {
        // Parse the String date to Date time , if Start date is not passed in filter assign Min Date 
        DateTime dtStartDate = String.isNotBlank(sStartDate) ?  datetime.newInstance((date.parse(sStartDate)).year(), (date.parse(sStartDate)).month(),(date.parse(sStartDate)).day()) : dateTime.valueOf(sMinDate); 
        
        // Parse the String date to Date time , if End date is not passed in filter assign Today
        DateTime dtEndDate = String.isNotBlank(sEndDate) ?  datetime.newInstance((date.parse(sEndDate)).year(), (date.parse(sEndDate)).month(),(date.parse(sEndDate)).day()) : System.now(); 
                                
        sRefereceId = String.isNotBlank(sRefereceId) ? sRefereceId.trim() : sZero; //If Referece Id is blank assign 0 to it
        return constructService(oAcc,bFamilyMem, sPolMemberId , dtStartDate , dtEndDate , sRefereceId); // Invoke the Inquiry history service
    }
    
    /*
     * Method Name : constructService
     * Description : This function is used for construct required data structures before calling the service
     * Return type : list<HUMInquiryHistoryWrapperHelper>
     * Paramater   : Account , boolean , String , DateTime , DateTime , String
     */
     private List<HUMInquiryHistoryWrapperHelper> constructService(Account oAcc, boolean bFamilyMem, String sPolMemberId , DateTime dtStartDate , DateTime dtEndDate , String sRefereceId)
     {
        
        HUMInqSchemasDataContractOrgService.ArrayOfEntityIdentifier arrayOfEntityIdentifier = new HUMInqSchemasDataContractOrgService.ArrayOfEntityIdentifier(); // Initiate the WSDL
        HUMInqSchemasDataContractOrgService.EntityIdentifier entityIdentifierFor = new HUMInqSchemasDataContractOrgService.EntityIdentifier();
        
        arrayOfEntityIdentifier.EntityIdentifier = new List <HUMInqSchemasDataContractOrgService.EntityIdentifier>(); // Array of Entity Identifier
        
        buildQuery(oAcc.Id,oAcc.recordType.Name, sPolMemberId); // Build neccessary lists to be re used
        
        if(oAcc.Mbr_Gen_Key__c != null)
        {
           entityIdentifierFor = createEntityIdentifierFor(oAcc.Mbr_Gen_Key__c ,entityIdentifierInput(oAcc.recordType.Name), sGenKey);
        }
        else
        {
            entityIdentifierFor = createEntityIdentifierFor(oAcc.Enterprise_ID__c ,entityIdentifierInput(oAcc.recordType.Name) ,sEnterpriseID );
        } 
        arrayOfEntityIdentifier.EntityIdentifier.add(entityIdentifierFor); // Add entityIdentifierFor
        if(bFamilyMem) // If All Family Member is clicked then Set Additional EntityIdentifier for all related policy
        {
            List <HUMInqSchemasDataContractOrgService.EntityIdentifier> lstFamilyMember = fetchDependents();
            if(lstFamilyMember != null && !lstFamilyMember.isEmpty() && lstFamilyMember.size() > 0) {arrayOfEntityIdentifier.EntityIdentifier.addall(lstFamilyMember);}
        } 
        return invokeService(oAcc.recordType.Name,arrayOfEntityIdentifier,entityIdentifierFor,dtStartDate,dtEndDate,sRefereceId);   // return the webservice response         
    }
    
    /*
     * Method Name : buildQuery
     * Description : Method is used to query on different objects and maintain them in a list. These records are static records and needs to be used at multiple places in the class
     * Return type : void
     * Paramater   : String String , String
     */
    private void buildQuery(String sAcctId , String sRecType , string sPolMemberId)
    {
        Set<Id> setGroupId = new Set<Id>(); 
        for(GroupMember grpMem : [Select Id, GroupId,Group.Name , UserOrGroupId From GroupMember where UserOrGroupId = : UserInfo.getUserId()])
        {
            setGroups.add(grpMem.Group.Name);
            setGroupId.add(grpMem.GroupId);
        }
        
        list<Id> listGroup = new list<Id>();
        for(Group grp : [Select Id from Group where Type =: system.label.HUM_Regular limit : iInquirySearchLimit ])
        {
            listGroup.add(grp.Id);
        }
        list<GroupMember> listGroupMember = new list<GroupMember>();
        for (GroupMember m : [Select Id, Group.Name , GroupId, UserOrGroupId From GroupMember where UserOrGroupId IN : listGroup limit : iInquirySearchLimit])
        {
            {listGroupMember.add(m);}
        }
        if(listGroupMember != null && !listGroupMember.isEmpty())  {setGroups.addAll(fetchRelatedGroups(setGroupId,listGroupMember));} // Set of all the group name which the logged in user belongs.
        if (sRecType == sMemRecType)
        {
            lstPolicyMember = [Select Id, Subscriber__c ,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c = : sAcctId LIMIT : iInquirySearchLimit];
            constructPolicyList(sPolMemberId);
        }
    }

    /*
     * Method Name : fetchRelatedGroups
     * Description : Method is used to get related Group members 
     * Return type : Set<String>
     * Paramater   : Set<Id> , list<GroupMember>
     */
    private static Set<String> fetchRelatedGroups(Set<Id> grpId , list<GroupMember> lstGrpMembers)
    {
        Set<String> groupName = new Set<String>(); 
        Set<Id> setGrpId = new set<Id>();
        for(GroupMember grpMember : lstGrpMembers)
        {
            if(grpId.contains(grpMember.UserOrGroupId)) 
            {
                groupName.add(grpMember.Group.Name);
                setGrpId.add(grpMember.GroupId);
            }
        }  
        if(!setGrpId.isEmpty()) { groupName.addall(fetchRelatedGroups(setGrpId,lstGrpMembers)); }
        return groupName;     
    }
        
    /*
     * Method Name : createEntityIdentifierFor
     * Description : Method is used to create EntityIdentifier
     * Return type : HUMInqSchemasDataContractOrgService.EntityIdentifier 
     * Paramater   : String ,String 
     */
    private HUMInqSchemasDataContractOrgService.EntityIdentifier createEntityIdentifierFor(String sEntId , String sRecType , String sIdType)
    {
       HUMInqSchemasDataContractOrgService.EntityIdentifier entityIdentifierInput = new HUMInqSchemasDataContractOrgService.EntityIdentifier();
       entityIdentifierInput.EntityType = sRecType;
       entityIdentifierInput.IdType = sIdType;
       entityIdentifierInput.IdSource = sSDR;
       entityIdentifierInput.IdValue = sEntId;
       return entityIdentifierInput;
    }
    
    /*
     * Method Name : entityIdentifierInput
     * Description : Method is used to get the identifier type based on record type name
     * Return type : String
     * Paramater   : String
     */
    private String entityIdentifierInput (String sRecType)
    {
        map<String,String> mapEntityIdentifier = new map<String,String>
        {
            sMemRecType => sLaunchGCPCCP_KM,
            sProviderRecType => sLaunchGCPCCP_KP,
            sAgentRecType => sLaunchGCPCCP_KA,
            sGroupRecType => sLaunchGCPCCP_KE  
        };
        return mapEntityIdentifier.containskey(sRecType) ? mapEntityIdentifier.get(sRecType) : sRecType;
    }
    
    /*
     * Method Name : constructPolicyList
     * Description : Method is used to construct Policy list
     * Return type : void
     * Paramater   :  Id
     */
    @testVisible private void constructPolicyList(Id polMembId)
    {
        boolean bHomeOfficeAll = Pharmacy_H_HUM.isPharmacyUser() == GlobalCommonConstants_HUM.STRING_NO ? setGroups.contains(sHomeOfficeAll) : setGroups.contains(shpHomeOffice) ;                                                 
        
        Set<String> setKeywords = getTypeKeywords(setGroups.contains(sHomeOfficeMed),setGroups.contains(sHomeOfficeDen));
        for (Policy_Member__c oPolMem: lstPolicyMember)  
        {
            boolean bAddRecords = false;
            if(polMembId == null)
            {
                bAddRecords = (!oPolMem.Policy__r.Group_Name__r.Home_Office_Account__c || bHomeOfficeAll) ? true :(!setKeywords.contains(oPolMem.Policy__r.Product__r.ProductCode));
            }
            else if(polMembId != null && oPolMem.Id == polMembId)
            {
                bAddRecords = (!oPolMem.Policy__r.Group_Name__r.Home_Office_Account__c || bHomeOfficeAll) ? true :(!setKeywords.contains(oPolMem.Policy__r.Product__r.ProductCode));
            }
            if(bAddRecords)
            {               
                setExtenalId.add(oPolMem.Policy_Member_External_ID__c);
                if(oPolMem.Subscriber__c != null)
                {
                    setDependentPolicyMem.add(oPolMem.Subscriber__c);
                }
                else 
                {
                    setSubscriberPolicyMem.add(oPolMem.Id);
                }
            }
        }
    }
    
    /*
     * Method Name : fetchDependents
     * Description : Method is used to fetch dependent information
     * Return type : list<HUMInqSchemasDataContractOrgService.EntityIdentifier>
     * Paramater   : NA
     */
    private list<HUMInqSchemasDataContractOrgService.EntityIdentifier> fetchDependents()
    {
        List<HUMInqSchemasDataContractOrgService.EntityIdentifier> lstEntityIdentifierInputFor = new List<HUMInqSchemasDataContractOrgService.EntityIdentifier>();
        Set<Policy_Member__c> lstFamilyPolicies = new Set<Policy_Member__c>();
        if(!setDependentPolicyMem.isEmpty())
        {
            for (Policy_Member__c oPolMem: [Select Id, Policy__c,Policy_Member_External_ID__c, Member__c, Member__r.recordType.Name, Member__r.Enterprise_ID__c , Member__r.Mbr_Gen_Key__c  from Policy_Member__c where Id In :setDependentPolicyMem Or Subscriber__c IN: setDependentPolicyMem LIMIT :iInquirySearchLimit]) 
            {
                lstFamilyPolicies.add(oPolMem);
            }
        }
         if(!setSubscriberPolicyMem.isEmpty())
        {
            for (Policy_Member__c oPolMem: [Select Id, Policy__c,Policy_Member_External_ID__c, Member__c, Member__r.recordType.Name, Member__r.Enterprise_ID__c , Member__r.Mbr_Gen_Key__c from Policy_Member__c where Subscriber__c!= null And Subscriber__c IN: setSubscriberPolicyMem LIMIT :iInquirySearchLimit]) 
            {
                lstFamilyPolicies.add(oPolMem);
            }
        }
        
        for(Policy_Member__c oPolMem : lstFamilyPolicies)
        {
            setExtenalId.add(oPolMem.Policy_Member_External_ID__c);
            if(oPolMem.Member__r.Mbr_Gen_Key__c != null)
            { 
                lstEntityIdentifierInputFor.add(createEntityIdentifierFor(oPolMem.Member__r.Mbr_Gen_Key__c ,entityIdentifierInput(oPolMem.Member__r.recordType.Name),sGenKey));
            }
            else
            {
                lstEntityIdentifierInputFor.add(createEntityIdentifierFor(oPolMem.Member__r.Enterprise_ID__c ,entityIdentifierInput(oPolMem.Member__r.recordType.Name),sEnterpriseID));
            }
        }
        return  lstEntityIdentifierInputFor;
    }
    
    /*
     * Method Name : getTypeKeywords
     * Description : Method is used to get the Keywords for Dental and Medical Product
     * Return type : Set<String>
     * Paramater   : boolean,boolean
     */
    @testVisible private static Set<String> getTypeKeywords(boolean bMedical, boolean bDental){
        Set<String> setKeywords = new Set<String>();
        
        if(!bMedical)
        {
            setKeywords.addall(sMedicalProductsKeywords.split(sSemicolon));
        }
        if(!bDental)
        {
            String sDenProds = sDentalProductsSet1 + sDentalProductsSet2;
            setKeywords.addall(sDenProds.split(sSemicolon));
        }
        return setKeywords;
    }
    
    /*
     * Method Name : invokeService
     * Description : Invoke the webservice
     * Return type : list<HUMInquiryHistoryWrapperHelper>
     * Paramater   : String,HUMInqSchemasDataContractOrgService.ArrayOfEntityIdentifier, HUMInqSchemasDataContractOrgService.EntityIdentifier , DateTime , DateTime , String
     */
    private list <HUMInquiryHistoryWrapperHelper> invokeService (String sAccRT,HUMInqSchemasDataContractOrgService.ArrayOfEntityIdentifier arrayOfEntityIdentifier , 
                                        HUMInqSchemasDataContractOrgService.EntityIdentifier entityIdentifierFor , DateTime dtFrDt , DateTime dtEnDt ,  String  sRfId)
    {  
        HUM_Webservice_Callout__c cstCalloutSetting = HUM_Webservice_Callout__c.getInstance('RetrieveInquiry');
              
        HUMInqSchemasDataContractOrgService.InquirySearchResultDTO sObjInquiryOutput = new HUMInqSchemasDataContractOrgService.InquirySearchResultDTO();  //  Response Structure of web service
        
        HUMInqSchemasDataContractOrgService.InquirySearchRequestDTO sObjInquiryInput = new HUMInqSchemasDataContractOrgService.InquirySearchRequestDTO(); // Request Structure of web service
        
        HUMInqSchemasDataContractOrgService.NameValuePair nameValuePair = new HUMInqSchemasDataContractOrgService.NameValuePair(); // Name Value pair        
        //If PDP or PH + Insurance
        if (Pharmacy_H_HUM.isPDPPilotUser() || Pharmacy_H_HUM.isPharmacyUser() != GlobalCommonConstants_HUM.STRING_NO)
        {
            nameValuePair.FilterType = '';
            nameValuePair.FilterValue = '';
        }
        //Check if Non Pharmacy
        else if (Pharmacy_H_HUM.isPharmacyUser() == GlobalCommonConstants_HUM.STRING_NO) {
            nameValuePair.FilterType = sInquiryFilterType;
            nameValuePair.FilterValue = sInquiryNonHPfilterType;
        }
        HUMInqSchemasDataContractOrgService.ArrayOfNameValuePair arrayOfNameValuePair = new HUMInqSchemasDataContractOrgService.ArrayOfNameValuePair();
        arrayOfNameValuePair.nameValuePair = new list<HUMInqSchemasDataContractOrgService.NameValuePair>{nameValuePair}; // Add the value to arrayOfNameValuePair

        sObjInquiryInput.SearchCriteria = constructInquirySearchCriteria(constructInquirySearchReturnCriteria(), // Reference Id ,Start Date and End Date  are set 
                                                                    arrayOfNameValuePair,arrayOfEntityIdentifier, entityIdentifierFor , dtFrDt , dtEnDt , sRfId);
        sObjInquiryInput.ClientInfo = constructClientMetaData(); // Construct the client meta data

        HUMInqTempUriOrgService.soap inqService = new HUMInqTempUriOrgService.soap(); // Soap method initiated 
        inqService.clientCertName_x = cstCalloutSetting.certificate_name__c;  //  Certificate Name from Custom Settings
        if(String.isNotBlank(cstCalloutSetting.Timeout__c)) inqService.timeout_x = Integer.valueof(cstCalloutSetting.Timeout__c);  //  Timeout from Custom Settings
        if(cstCalloutSetting.Active__c) sObjInquiryOutput = inqservice.GetInquiries(sObjInquiryInput); // call the webservice
        
        return constructWrapper(sAccRT,sObjInquiryOutput); // Return the response 
    }
    
    /*
     * Method Name : constructInquirySearchReturnCriteria
     * Description : This function is used to construct Inquiry Search Return Criteria for the webservice request
     * Return type : HUMInqSchemasDataContractOrgService.InquirySearchReturnCriteria
     * Paramater   : NA
     */
    private static HUMInqSchemasDataContractOrgService.InquirySearchReturnCriteria constructInquirySearchReturnCriteria()
    {
        HUMInqSchemasDataContractOrgService.InquirySearchReturnCriteria inquirySearchReturn = new HUMInqSchemasDataContractOrgService.InquirySearchReturnCriteria();
        inquirySearchReturn.ContactDetails = false;
        inquirySearchReturn.InquiryDetails = false;
        inquirySearchReturn.InquiryNotes = false;
        inquirySearchReturn.TaskNotes = false;
        inquirySearchReturn.TaskList = false;
        inquirySearchReturn.AttachmentHeaders = false;
        inquirySearchReturn.AttachmentDetails = false;
        return inquirySearchReturn;
    }
    
    /*
     * Method Name : constructClientMetaData
     * Description : This function is used to construct Client Meta Data DTO for the webservice request
     * Return type : HUMInqSchemasDataContractOrgService.ClienMetaDataDTO
     * Paramater   : NA
     */
    private static HUMInqSchemasDataContractOrgService.ClientMetaDataDTO constructClientMetaData()
    {
        HUMInqSchemasDataContractOrgService.ClientMetaDataDTO clientMetaData = new HUMInqSchemasDataContractOrgService.ClientMetaDataDTO();
        clientMetaData.ClientName = sGCP;
        clientMetaData.ApplicationTabView = sGNA;
        clientMetaData.ApplicationUser = Userinfo.getUserId(); 
        clientMetaData.ApplicationRequester = Userinfo.getUserName();
        return clientMetaData;
    }
    
    /*
     * Method Name : constructInquirySearchCriteria
     * Description : This function is used to construct Inquiry Search  Criteria for the webservice request
     * Return type : HUMInqSchemasDataContractOrgService.InquirySearchCriteriaDTO
     * Paramater   : HUMInqSchemasDataContractOrgService.InquirySearchReturnCriteria , HUMInqSchemasDataContractOrgService.ArrayOfNameValuePair , HUMInqSchemasDataContractOrgService.ArrayOfEntityIdentifier, 
                     HUMInqSchemasDataContractOrgService.EntityIdentifier , DateTime, DateTime  ,String
     */    
    private static HUMInqSchemasDataContractOrgService.InquirySearchCriteriaDTO constructInquirySearchCriteria(HUMInqSchemasDataContractOrgService.InquirySearchReturnCriteria inquirySearchReturn,
                                                                                                               HUMInqSchemasDataContractOrgService.ArrayOfNameValuePair arrayOfNameValuePair,
                                                                                                               HUMInqSchemasDataContractOrgService.ArrayOfEntityIdentifier arrayOfEntityIdentifier,
                                                                                                               HUMInqSchemasDataContractOrgService.EntityIdentifier entityIdentifierFor ,
                                                                                                               DateTime dtfDate, DateTime dttDate, String sRefId)
    {
        HUMInqSchemasDataContractOrgService.InquirySearchCriteriaDTO inquirySearchCriteria = new HUMInqSchemasDataContractOrgService.InquirySearchCriteriaDTO();
        
        inquirySearchCriteria.ContactId = Long.valueOf(sRefId);
        inquirySearchCriteria.InquireAboutCriteriaList = arrayOfEntityIdentifier;
        inquirySearchCriteria.InqForAboutMatchRule = sINQA;
        inquirySearchCriteria.AlternateInqAboutID = '';
        inquirySearchCriteria.StartDate = dtfDate;
        inquirySearchCriteria.EndDate = dttDate;
        inquirySearchCriteria.InquireForCriteria = entityIdentifierFor;
        inquirySearchCriteria.SearchFilters = arrayOfNameValuePair;
        inquirySearchCriteria.IncludeItems = inquirySearchReturn;
        return inquirySearchCriteria;
    }
    
    /*
     * Method Name : constructWrapper
     * Description : This function is used to construct the wrapper list
     * Return type : list<HUMInquiryHistoryWrapperHelper>
     * Paramater   : String,HUMInqSchemasDataContractOrgService.InquirySearchResultDTO 
     */
    @testVisible private list <HUMInquiryHistoryWrapperHelper> constructWrapper (String sAccRecType, HUMInqSchemasDataContractOrgService.InquirySearchResultDTO sObjInquiryOutput )
    {                               
        list <HUMInquiryHistoryWrapperHelper> inqList = new list <HUMInquiryHistoryWrapperHelper>();
        HUMInqSchemasDataContractOrgService.InquiryDTO[] lstInquiryDTO = new HUMInqSchemasDataContractOrgService.InquiryDTO[]{};
        if (sObjInquiryOutput != null && sObjInquiryOutput.InquirySearchDetails != null) 
        {            
            lstInquiryDTO = sObjInquiryOutput.InquirySearchDetails.InquiryDTO;
            if (lstInquiryDTO != null && !lstInquiryDTO.isEmpty() && lstInquiryDTO.size() < iInquirySearchLimit) // Null check and Limit check 
            {               
                for (HUMInqSchemasDataContractOrgService.InquiryDTO inqDto: lstInquiryDTO) 
                {   
                    if (sAccRecType == sMemRecType) // For Member Record Type additional filter of CustomerGenKey,CustomerCoverageSequenceNumber,CoveragePlanEffectiveDate 
                    {  
                        boolean bAddResponse = false;
                        for (String sExternalId: setExtenalId) 
                        {
                            if (sExternalId != null) 
                            {                                     
                                list <String> lstExternalIdSplit = sExternalId.split('\\|'); // Split the external Id Field 
                                {
                                    if (lstExternalIdSplit[iConstThree] == String.Valueof(inqDto.CustomerGenKey) && // Match CustomerGenKey with third value of external field
                                            lstExternalIdSplit[iConstFour] == String.Valueof(inqDto.CustomerCoverageSequenceNumber) && // Match CustomerCoverageSequenceNumber with fourth value of external field
                                            lstExternalIdSplit[iConstFive] == String.valueof(Date.Valueof(inqDto.CoveragePlanEffectiveDate))) // Match CoveragePlanEffectiveDate with fifth value of external field
                                    {   
                                         bAddResponse = true;
                                        
                                    }
                                }
                            }
                        }
                        if(bAddResponse)inqList.add(constructWrapperHelper(inqDto)); // Add response to wrapper
                    }
                    else
                    {
                        inqList.add(constructWrapperHelper(inqDto)); // Add response to wrapper
                    }
                }
            }
        }    
        return inqList; // return the wrapper
    }
    
    /*
     * Method Name : constructWrapperHelper
     * Description : This function is used to construct the HUMInquiryHistoryWrapperHelper by parsing the webservice response
     * Return type : HUMInquiryHistoryWrapperHelper
     * Paramater   : HUMInqSchemasDataContractOrgService.InquiryDTO
     */
    @testVisible private HUMInquiryHistoryWrapperHelper constructWrapperHelper(HUMInqSchemasDataContractOrgService.InquiryDTO inqDto)
    {
        HUMInquiryHistoryWrapperHelper inqWrap = new HUMInquiryHistoryWrapperHelper();
        inqWrap.ReferenceId = String.valueOf(inqDto.ContactId);
        inqWrap.Type = inqDto.InquiryType;
        inqWrap.Category = inqDto.CategoryCodeDescription;
        inqWrap.Product = inqDto.PolicyType;
        inqWrap.Disposition = inqDto.DispositionCodeDescription;
        inqWrap.InquiringFor = inqDto.InqRFirstName +' '+ inqDto.InqRLastName;
        inqWrap.InquiringForType = inqDto.InqRTypeCode;
        inqWrap.InquiringAbout = inqDto.InqAFirstName +' '+ inqDto.InqALastName;
        inqWrap.Status = inqDto.StatusCodeDescription;
        inqWrap.Priority = inqDto.PriorityCode;
        inqWrap.DateTimeOpened = Date.valueof(inqDto.CreatedTimestamp);
        inqWrap.DateTimeClosed = Date.valueof(inqDto.InquiryCloseTimestamp);
        inqWrap.OwnerTeam = inqDto.OwnerTeamName;
        inqWrap.Reason = inqDto.ReasonCodeDescription;
        inqWrap.OwnerDepartment = inqDto.OwnerDepartmentName;
        inqWrap.CustomerGenKey = String.ValueOf(inqDto.CustomerGenKey);
        inqWrap.CoveragePlanEffectiveDate = String.valueOf(inqDto.CoveragePlanEffectiveDate);
        inqWrap.CustomerCoverageSequenceNumber = String.ValueOf(inqDto.CustomerCoverageSequenceNumber);
        
        if(inqDto.Contact != NULL)
        {
            if(inqDto.Contact.MediaTypeCodeDescription!= NULL)    inqWrap.CaseOrigin = String.valueOf(inqDto.Contact.MediaTypeCodeDescription);
        }
         return inqWrap;
    }
}