/**********************************************************************************************************************************************
Apex Class Name  : HUMInquiryHistoryServiceHelper_V1
Version          : 1.0
Created Date     : Feb 06th 2017
Function         : Invoke the Inquiry history webservice asynchronously
Test Class       : HUMInquiryHistoryServiceHelperTest_v1 
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
                                                                                      as part of CA ticket 5131899
 * Prasanthi Kandula       71716                        02/07/2017                   REQ 300069 - Synchronous callout to Async changes 
 * Pallavi Ravishankar     71716                        02/14/2017                   REQ 300069 - Synchronous callout to Async changes   
 * Sharan Shanmugam								 		06/02/2017			 		 REQ - 303395 - Modified FilterValue and FilterType nodes in request for humana pharmacy and non-pharmacy users 
 * Vishal Verma            106668                       06/30/2017                   REQ - 322595 : CA Ticket -6880202 : Added security for HP Home office.
 * Praveen Kumar Parimi    144882                       11/06/2017                   REQ - 338377 : CA Ticket -7110503 : Fix for CCP Legacy history is not showing case origin.
 * Saurabh Kaple           162078                       01/09/2018                   REQ - 338410 - Inquiry Search Service- Use Genkey from platform instead of MBE genkey conversion 
 * Satyam Malviya          26300                        07/27/2018                   REQ -371242 -  SF-TECH- Accessibility to see all Cases on CCP/GCP Legacy History section on all pages 
 * Vijay Gurusamy          272865                       08/03/2018                   REQ - 367015 - Change the filter for Inquire history servide call.
 * Santhosh Ganji                                       04/18/2019                   REQ - 383696 - MF 7- Display all the records in Legacy History section for HP Users
 * Prudhvi Pamarthi                                     06/04/2021                   US2270790 Global Constant Class Changes
 *************************************************************************************************************************************************************************************/
public with sharing virtual class HUMInquiryHistoryServiceHelper_v1
{
    Set <String> setExtenalId = new Set <String>(); 
    Set<String> setGroups = new Set<String>(); 
    List<Policy_Member__c> lstPolicyMember = new List<Policy_Member__c>(); 
    Set<Id> setSubscriberPolicyMem = new Set<Id>();
    Set<Id> setDependentPolicyMem = new Set<Id>(); 
    
    Private string sMinDate = String.ValueOf(HUMConstants__c.getInstance('MIN_DATE_TIME').StringValue__c);
    Private string sZero = '0';
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
    Private Integer iConstThree = 3;
    Private Integer iConstFour = 4;
    Private Integer iConstFive = 5;
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
    public HUMInqTempUriOrgAsyncService.GetInquiriesResponse_elementFuture initiateInquiryHistoryService(Continuation oCon,Account oAcc, boolean bFamilyMem, String sPolMemberId , String sStartDate , String sEndDate , String sRefereceId)
    {
        // Parse the String date to Date time , if Start date is not passed in filter assign Min Date 
        DateTime dtStartDate = String.isNotBlank(sStartDate) ?  datetime.newInstance((date.parse(sStartDate)).year(), (date.parse(sStartDate)).month(),(date.parse(sStartDate)).day()) : dateTime.valueOf(sMinDate); 
        
        // Parse the String date to Date time , if End date is not passed in filter assign Today
        DateTime dtEndDate = String.isNotBlank(sEndDate) ?  datetime.newInstance((date.parse(sEndDate)).year(), (date.parse(sEndDate)).month(),(date.parse(sEndDate)).day()) : System.now(); 
                                
        sRefereceId = String.isNotBlank(sRefereceId) ? sRefereceId.trim() : sZero; 
        return constructService(oCon, oAcc,bFamilyMem, sPolMemberId , dtStartDate , dtEndDate , sRefereceId); 
    }
    
    /*
     * Method Name : constructService
     * Description : This function is used for construct required data structures before calling the service
     * Return type : list<HUMInquiryHistoryWrapperHelper>
     * Paramater   : Account , boolean , String , DateTime , DateTime , String
     */
     private HUMInqTempUriOrgAsyncService.GetInquiriesResponse_elementFuture constructService(Continuation oCon,Account oAcc, boolean bFamilyMem, String sPolMemberId , DateTime dtStartDate , DateTime dtEndDate , String sRefereceId)
     {  
        HUMInqSchemasDataContractOrgService.ArrayOfEntityIdentifier arrayOfEntityIdentifier = new HUMInqSchemasDataContractOrgService.ArrayOfEntityIdentifier(); 
        arrayOfEntityIdentifier.EntityIdentifier = new List <HUMInqSchemasDataContractOrgService.EntityIdentifier>(); 
        HUMInqSchemasDataContractOrgService.EntityIdentifier entityIdentifierFor = new HUMInqSchemasDataContractOrgService.EntityIdentifier();
        
        buildQuery(oAcc.Id,oAcc.recordType.Name, sPolMemberId); 
        
       if(oAcc.Mbr_Gen_Key__c != null)
       {
            entityIdentifierFor = createEntityIdentifierFor(oAcc.Mbr_Gen_Key__c ,entityIdentifierInput(oAcc.recordType.Name), sGenKey);
       }
       else
       {
             entityIdentifierFor = createEntityIdentifierFor(oAcc.Enterprise_ID__c ,entityIdentifierInput(oAcc.recordType.Name) ,sEnterpriseID );
       } 
        arrayOfEntityIdentifier.EntityIdentifier.add(entityIdentifierFor); 
        if(bFamilyMem) 
        {
            List <HUMInqSchemasDataContractOrgService.EntityIdentifier> lstFamilyMember = fetchDependents();
            if(lstFamilyMember != null && !lstFamilyMember.isEmpty() && lstFamilyMember.size() > 0) {arrayOfEntityIdentifier.EntityIdentifier.addall(lstFamilyMember);}
        } 
        return invokeService(oCon,oAcc.recordType.Name,arrayOfEntityIdentifier,entityIdentifierFor,dtStartDate,dtEndDate,sRefereceId);          
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
        if(listGroupMember != null && !listGroupMember.isEmpty())  {setGroups.addAll(fetchRelatedGroups(setGroupId,listGroupMember));} 
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
       entityIdentifierInput.IdType =  sIdType;
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
            for (Policy_Member__c oPolMem: [Select Id, Policy__c,Policy_Member_External_ID__c, Member__c, Member__r.recordType.Name, Member__r.Enterprise_ID__c , Member__r.Mbr_Gen_Key__c from Policy_Member__c where Id In :setDependentPolicyMem Or Subscriber__c IN: setDependentPolicyMem LIMIT :iInquirySearchLimit]) 
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
    private HUMInqTempUriOrgAsyncService.GetInquiriesResponse_elementFuture invokeService (Continuation oCon, String sAccRT,HUMInqSchemasDataContractOrgService.ArrayOfEntityIdentifier arrayOfEntityIdentifier , 
                                        HUMInqSchemasDataContractOrgService.EntityIdentifier entityIdentifierFor , DateTime dtFrDt , DateTime dtEnDt ,  String  sRfId)
    {  
        HUM_Webservice_Callout__c cstCalloutSetting = HUM_Webservice_Callout__c.getInstance('RetrieveInquiry');
              
        HUMInqSchemasDataContractOrgService.InquirySearchResultDTO sObjInquiryOutput = new HUMInqSchemasDataContractOrgService.InquirySearchResultDTO();  
        
        HUMInqSchemasDataContractOrgService.InquirySearchRequestDTO sObjInquiryInput = new HUMInqSchemasDataContractOrgService.InquirySearchRequestDTO(); 
        
        HUMInqSchemasDataContractOrgService.NameValuePair nameValuePair = new HUMInqSchemasDataContractOrgService.NameValuePair();  
        //If PDP or PH + Insurance
        if (Pharmacy_H_HUM.isPDPPilotUser() || (Pharmacy_H_HUM.isPharmacyUser() != GlobalCommonConstants_HUM.STRING_NO))
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
        arrayOfNameValuePair.nameValuePair = new list<HUMInqSchemasDataContractOrgService.NameValuePair>{nameValuePair}; 

        sObjInquiryInput.SearchCriteria = constructInquirySearchCriteria(constructInquirySearchReturnCriteria(), 
                                                                    arrayOfNameValuePair,arrayOfEntityIdentifier, entityIdentifierFor , dtFrDt , dtEnDt , sRfId);
        sObjInquiryInput.ClientInfo = constructClientMetaData(); 

        HUMInqTempUriOrgAsyncService.Asyncsoap inqService = new HUMInqTempUriOrgAsyncService.Asyncsoap(); 
        inqService.clientCertName_x = cstCalloutSetting.certificate_name__c;  
        if(String.isNotBlank(cstCalloutSetting.Timeout__c)) inqService.timeout_x = Integer.valueof(cstCalloutSetting.Timeout__c);  
        HUMInqTempUriOrgAsyncService.GetInquiriesResponse_elementFuture oeleFuture = new HUMInqTempUriOrgAsyncService.GetInquiriesResponse_elementFuture();
        if(cstCalloutSetting.Active__c) 
        {
            
            oeleFuture  =  inqservice.beginGetInquiries(oCon,sObjInquiryInput);

        }
       return oeleFuture; 
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
    public list <HUMInquiryHistoryWrapperHelper> constructWrapper (String sAccRecType, HUMInqSchemasDataContractOrgService.InquirySearchResultDTO sObjInquiryOutput )
    {                               
        list <HUMInquiryHistoryWrapperHelper> inqList = new list <HUMInquiryHistoryWrapperHelper>();
        HUMInqSchemasDataContractOrgService.InquiryDTO[] lstInquiryDTO = new HUMInqSchemasDataContractOrgService.InquiryDTO[]{};

        if (sObjInquiryOutput != null && sObjInquiryOutput.InquirySearchDetails != null) 
        {            
            lstInquiryDTO = sObjInquiryOutput.InquirySearchDetails.InquiryDTO;

            if (lstInquiryDTO != null && !lstInquiryDTO.isEmpty() && lstInquiryDTO.size() < iInquirySearchLimit) 
            {        
                for (HUMInqSchemasDataContractOrgService.InquiryDTO inqDto: lstInquiryDTO) 
                {   
                    if (sAccRecType == sMemRecType) 
                    {  
                        boolean bAddResponse = false;
                        for (String sExternalId: setExtenalId)
                        {  
                            if (sExternalId != null) 
                            {                               
                                list <String> lstExternalIdSplit = sExternalId.split('\\|'); 
                                {   
                                   if (lstExternalIdSplit[iConstThree] == String.Valueof(inqDto.CustomerGenKey) && 
                                       lstExternalIdSplit[iConstFour] == String.Valueof(inqDto.CustomerCoverageSequenceNumber) && 
                                       lstExternalIdSplit[iConstFive] == String.valueof(Date.Valueof(inqDto.CoveragePlanEffectiveDate)))
                                     {   
                                         bAddResponse = true;
                                        
                                     }
                                }
                            }
                        }
              
                        if(bAddResponse)inqList.add(constructWrapperHelper(inqDto)); 
                    }
                    else
                    {
                        inqList.add(constructWrapperHelper(inqDto)); 
                    }
                }
            }
        } 
        return inqList; 
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