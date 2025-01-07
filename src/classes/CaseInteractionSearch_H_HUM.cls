/******************************************************************************************************************
Apex class Name    : CaseInteractionSearch_H_HUM
Version            : 1.0
Function           : This class is for Data Access Layer for for Interaction related activities
Created On         : Jan 20 2019
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Pallavi Ravishankar                           03/07/2019           Original Version
*   2.0            Prasanthi Kandula            352555           04/15/2019           Added logic to create accounts if entity type is 
                                                                                      provider for interacting with/about 
*   3.0            Lakshmi Vutukuri                              04/23/2019           Added methods for Unknown member creation
*   4.0            Prasanthi Kandula            355010           05/07/2019           Updated logic to handle unknown Member And Provider data from service
*   5.0            Pallavi Ravishankar                           05/08/2019           REQ - 377486 - UpdateInteraction CaseInteraction changes  
*   6.0            Prasanthi Kandula            357233           05/17/2019           Updated SOQL to SOSL on Member_id__c object to improve performance
*   7.0            Seema Kaulgi                                  07/04/2019           REQ - 403811 GBO Watson chat (Group)
*   8.0            Asish Behera                                  07/09/2019           Changed Private to Public for few class that is referred in CreateCaseRequest_H_HUM
*   9.0            Asish Behera                                  08/21/2019           Changed groupId check
*   10.0           Asish Behera                                  08/21/2019           Null pointher exceptions handled.
*   11.0           Asish Behera                                  09/10/2019           map containskey added to fix null pointer issue
*   12.0           Asish Behera                                  03/13/2020           Watson unknown Provider issue fix.
*   13.0           Asish Behera                                  05/20/2020           PR00090399 -TECH -SF -MF 1 - CA ticket - 8996403 - Wrong DOB format 
*   14.0           Moshitha Gunasekaran                          01/08/2021           US1534952 - INC0462428 - CRM Service - Incorrect NPI Populating on CRM Case Upon transfer from MHK / NPI Not Populating on CRM Case
*   15.0	       Sagar MN					 02/10/2021	      US1867250 - Added ETL Record deleted flag to queries
*   16.0           Vaishnavi Manda                               03/25/2021           INC0934323 - Added method to filter member records based on name And updated query for member scenario
*   17.0           Sagar MN                                     05/12/2021           US2226920 MF6 Checkmarx scan issue fixes
*   18.0           Sagar MN                                     05/20/2021           INC1060956 Added Meicaid-Id Id Type for Member Case Creation V4
*   19.0           Sagar MN                                     05/21/2021           MF6 - INC1014026 - intermittent 500 internal server error from CRM service for interaction creation
*   20.0           Moshitha Gunasekaran                         07/05/2021           US2446677 - Salesforce to Softphone Case Interaction Web Services 
*   21.0           Moshitha Gunasekaran		                    11/09/2021		     Made changes to add additional parameters logic for member for CBIS
------------------------------------------------------------------------------------------------------------------- */
Public with sharing class CaseInteractionSearch_H_HUM 
{
    private static set<string> setMemberIDTypes = new set<string>{GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM,GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM,GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM,GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM,GLOBAL_CONSTANT_HUM.UNKNOWN_MEMBER_MEDICAIDID, 'CBIS ALT ID'};
    private static final string ACCOUNT_ENTERPRISEID='EnterprisePersonID';
    private static final string ACCOUNT_AGENT_AGENTID = 'AgentID';
    private static final string ACCOUNT_PROVIDER_PROVIDERID = 'ProviderID';
    private static final string ACCOUNT_PROVIDER_NPIID = GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID;
    private static final string ACCOUNT_GROUP_GROUPNUM = 'GroupNumber';
    private static final string ACCOUNT_SOURCE_CODE = 'PlatformCode';
    private static final string ACCOUNT_GROUP_CUSTGENKEY = 'CustomerGenKey';
    private static string sIntAbout ='Interacting_About';
    private static string sIntWith = 'Interacting_With';
    private static map<string, string> mapAcctIDTypes = new map<string, string>
                                    {
                                        ACCOUNT_ENTERPRISEID=>'Enterprise_ID__c',
                                        ACCOUNT_AGENT_AGENTID=>'Agent_ID__c',
                                        ACCOUNT_PROVIDER_PROVIDERID=>'Enterprise_ID__c',
                                        ACCOUNT_PROVIDER_NPIID=>'NPI_ID__c',
                                        ACCOUNT_GROUP_GROUPNUM=>'Group_Number__c',
                                        ACCOUNT_SOURCE_CODE=>'Source_Platform_Code__c',
                                        ACCOUNT_GROUP_CUSTGENKEY=>'Enterprise_ID__c'
                                    };
    private static Map<String,Account> accountMapToUpsertProv = new Map<String,Account>();
    private static Map<String,Account> accountMapToUpsertGroup = new Map<String,Account>();
    public static Map<String,List<Account>> unknownAccountMapToUpsert = new Map<String,List<Account>>(); 
    public static Map<String,List<Account>> accountMapToUpsert = new Map<String,List<Account>>();  
    public static Map<String,List<Account>> groupaccountMapToUpsert = new Map<String,List<Account>>();  
    public static Boolean bisUnique=false; 
    public static boolean blninsertunknowngrp = true;  
    private static Map<Integer,String> mapProviderId = new Map<Integer,String>();
    private static Map<Integer,String> mapGroupId = new Map<Integer,String>();
    private static Integer iProviderKey=0; 
    private static Integer iGroupKey=0; 
    private Static Map<String ,Account> unknownAccountMap= New Map<String ,Account>();
    public static Map<String,String> mapunknownProvider = new map<string,string>();
    public static Map<String,String> mapunknownGroup = new map<string,string>();
    Private static string groupIDCheck = '';
    private static List<Account> providerAcct = new List<Account>();
    private static List<Account> GroupAcct = new List<Account>();
    public static map<String,map<String,String>> mapUnknwnMemberFrmService = new map<String,map<String,String>>();
    public static List<String> lstAcctId = new List<String>();
    public static map<string,map<string,string>> unknwnMbrRequest = new map<string,map<string,string>>();
    private static integer iSearchLimit = 20;
    Static final Map<String,String> mapRecordtypeInfo= New map<String,String>
    {'UM' => HUMConstants__c.getInstance('UNKNOWN_MEMBER').Stringvalue__c
    };
    public with sharing Class CreateUnknownMembers_DTO_HUM
    {   
    
        public String MemberInteractingId{get;set;}
        public String AccId{get;set;} 
        public String MemberInteractingIdType{get;set;}
        public String AccntextrnlID{get;set;}
 
    }
    
   /**
    * createMapInquireCriteria
    * <p>
    * Method is used to create Map for Inquire about and Inquire for criteria
    *
    * @param     list<CreateInteractionRequest_DTO_HUM.EntityIdentifierDTO> request
    * @return    map<string, map<string,set<string>>> => map of Interaction type to the Interaction ids and id types                  
    */ 
    
    @TestVisible
    public static map<string, map<string,set<string>>> createMapInquireCriteria(list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> request, string sIntType)
    { 
        map<string, map<string,set<string>>> mapIDValue =new map<string, map<string,set<string>>>();        
        map<string, set<string>> mapMemberIDValues = new map<string, set<string>>();
        map<string, string> mapGrpPlatformCode = new map<string, string>();
        string entityType = '';
         String medicaidIdType = GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM;
        for(CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intWithAboutDTO : request)
        {
        if(intWithAboutDTO.IdType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWN_MEMBER_MEDICAIDID)) intWithAboutDTO.IdType= medicaidIdType;
            if(intWithAboutDTO != null && string.isNotBlank(intWithAboutDTO.EntityType) && String.isNotBlank(intWithAboutDTO.ID) && string.isNotBlank(intWithAboutDTO.IDType))                
            { 
                // memberID__C object population check
        entityType = intWithAboutDTO.EntityType;
                if(intWithAboutDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM))
                {              
                    //for all membe types - MedicareID, SSN, Medicaid, Enterprise,member id              
                    if(mapMemberIDValues.containsKey(intWithAboutDTO.IdType))
                    { mapMemberIDValues.get(intWithAboutDTO.IdType).add(intWithAboutDTO.ID.toUppercase());} 
                    else 
                    { mapMemberIDValues.put(intWithAboutDTO.IdType,new set<string>{intWithAboutDTO.ID.toUppercase()});}                              
                }
            }
            if(intWithAboutDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM)) //For Provider
            {
                if(intWithAboutDTO.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE) || intWithAboutDTO.IDType.equalsIgnoreCase(ACCOUNT_PROVIDER_NPIID) || intWithAboutDTO.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TAXIDTAG_PROVIDERSEARCH_HUM))
                {        
                              
                    getProviderAccountMap(intWithAboutDTO, sIntType);                   
                }  
            } 
            if(intWithAboutDTO != null && string.isNotBlank(intWithAboutDTO.EntityType) && String.isBlank(intWithAboutDTO.ID) && string.isNotBlank(intWithAboutDTO.IDType) && intWithAboutDTO.IDType == 'UnknownMember')
            {
                unknwnMbrRequest.put(sIntType, new map<string,string>{intWithAboutDTO.IDType=>intWithAboutDTO.ID});
            }
            
            //Added for watson - For member do check above code
            if(intWithAboutDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM )) //For Group
            {
                if(intWithAboutDTO.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPEID)) 
                {        
                    
                    getGroupAccountMap(intWithAboutDTO, sIntType);                   
                }  
            }
            
        }
        if(mapMemberIDValues!=null && !mapMemberIDValues.isEmpty())
        {
            mapIDValue.put(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM, mapMemberIDValues);
            if(entityType == GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPE)
            mapIDValue.put(GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPE, mapMemberIDValues);
        } 
        return mapIDValue;
    } 
    
    /*
    * calltoUpsertGroupAccounts
    * <p>
    * This method is used to upsert accounts
    * @param   
    * @return   Map<String,Map<String,String>> key is Interacting With/About and value(salesforce account id))
    */
    public static Map<String,Map<String,String>> calltoUpsertGroupAccounts() 
    {
        try
        {
            Map<String,List<Account>> mapGroupresultAccount = new Map<String,List<Account>>();

            Map<String,Map<String,String>> mapFinalGroupData = new Map<String,Map<String,String>>();
            
            groupIDCheck = GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPEID;
            if(groupaccountMapToUpsert !=null && !groupaccountMapToUpsert.isEmpty())
            {
               
                mapGroupresultAccount = groupaccountMapToUpsert;
                
            } 
            Map<String,Map<String,String>> mapUnknownAccts = new Map<String,Map<String,String>>();
             
			
            if(unknownAccountMapToUpsert!=null && !unknownAccountMapToUpsert.isEmpty())
            {
                mapUnknownAccts = insertUnknownAccounts(mapGroupId,unknownAccountMapToUpsert); 
            }
            
            mapFinalGroupData = prepareFinalMap(mapUnknownAccts,mapGroupresultAccount);
            
            
            return mapFinalGroupData ;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseInteractionSearch_H_HUM','calltoUpsertGroupAccounts');
            return null; 
        }
    }
          
    /**
    * getMapMemberDependentCode
    * <p>
    * Method is used to create Map for Inquire about and Inquire With criteria Member Dependent code
    * @param     list<CreateInteractionRequest_DTO_HUM.EntityIdentifierDTO> request
    * @return    map<string, map<string,set<string>>> map of interacting type to the member id and dependent code values                    
    */     
    @TestVisible
    public static map<string, map<string,set<string>>> getMapMemberDependentCode(list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> request, string sIntType)
    { 
        map<string,set<string>> mapDependentCode = new map<string, set<string>>();
        map<string, map<string, set<string>>> mapMemberDependentCode = new map<string, map<string, set<string>>>();
        for(CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intWithAboutDTO : request)
        {
            if(intWithAboutDTO != null && string.isNotBlank(intWithAboutDTO.EntityType) && String.isNotBlank(intWithAboutDTO.ID) && string.isNotBlank(intWithAboutDTO.IDType))                
            { 
                // memberID__C object population check and Checking for IdValueType as'Member-Id-Base'
                if(intWithAboutDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM) && (intWithAboutDTO.IdType.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM)))
                {
                    if(string.isNotBlank(intWithAboutDTO.MemberIdDependentCode)) // map of membr id base, member dependent code
                    {
                        if(mapDependentCode.containsKey(intWithAboutDTO.ID.toUppercase())) 
                        {    mapDependentCode.get(intWithAboutDTO.ID.toUppercase()).add(intWithAboutDTO.MemberIdDependentCode);    }
                        else
                        {    mapDependentCode.put(intWithAboutDTO.ID.toUppercase(),new set<string>{intWithAboutDTO.MemberIdDependentCode});    }
                    } 
                }           
            }   
        }
        if(mapDependentCode!=null && !mapDependentCode.isEmpty())
        {
            mapMemberDependentCode.put(sIntType, mapDependentCode);
        }
        return mapMemberDependentCode;        
    }
    
    /*
    * locateMembers
    * <p>
    * This method is used to locate Members based on input Interacting with and Interacting about details 
    *  
    * @param    mapIntAboutCriteria(Input Interacting About values), mapIntWithCriteria(Input Interacting With values), bSearchUnknown (Unknown Search Flag)  
    * @return   list<Member_Id__c>                  
    */
    @TestVisible
    public static list<Member_Id__c> locateMembers(map<string,set<string>> mapIntAboutCriteria, map<string,set<string>> mapIntWithCriteria, map<string, map<string,set<string>>> mapMemberDependentCode)
    {
        list<Member_Id__c> lstMemberRecs = new list<Member_Id__c>();
        list<Account> lstAccountRecs = new list<Account>();
        lstMemberRecs.addAll(getMemberRecords(mapIntAboutCriteria,mapIntWithCriteria));
        return lstMemberRecs;
    } 
    
    /*
    * GetCallAccountDetails
    * <p>
    * This method is used to get Account details for InteractingWith adnd Interacting About for Interaction 
    *  
    * @param    CallInteractionRequest_DTO_HUM(Input values), Interaction__c(Interaction record)
    */
    public static void GetCallAccountDetails(CallInteractionRequest_DTO_HUM interactRequest, Interaction__c oCallInteraction)
    {
        boolean isWithAboutSame = false;
        boolean isProviderType = false;
        boolean isAgentType = false;
        map<string,string> mapinteraction;
        Interaction_Member__c oIntAbtMember = new Interaction_Member__c();
        map<string, string> mapInteractingWithAccount = new map<string, string>();
        map<string, string> mapInteractingAboutAccount = new map<string, string>();
        CreateAgentInteraction_H_HUM oAgentInteraction = new CreateAgentInteraction_H_HUM();
        
        //If InteractingAbout and InteractingWith are same then assign boolean flag to avoid duplicate calls to service and query
        if(((string.isNotBlank(interactRequest.InteractingWithType) && string.isNotBlank(interactRequest.InteractingAboutType) && interactRequest.InteractingWithType.equalsIgnoreCase(interactRequest.InteractingAboutType))) && 
            (((string.isNotBlank(interactRequest.InteractingAboutID) && string.isNotBlank(interactRequest.InteractingWithID) && interactRequest.InteractingWithID.equalsIgnoreCase(interactRequest.InteractingAboutID)) && 
            ((string.isNotBlank(interactRequest.InteractingAboutDepCode) && string.isNotBlank(interactRequest.InteractingWithDepCode) && interactRequest.InteractingWithDepCode.equalsIgnoreCase(interactRequest.InteractingAboutDepCode))||(string.isBlank(interactRequest.InteractingAboutDepCode) && string.isBlank(interactRequest.InteractingWithDepCode)))) || 
            (string.isNotBlank(interactRequest.InteractingWithGenkey) && string.isNotBlank(interactRequest.InteractingAboutGenkey) && long.valueOf(interactRequest.InteractingWithGenkey) !=0 && long.valueOf(interactRequest.InteractingAboutGenkey) !=0 && interactRequest.InteractingWithGenkey.equalsIgnoreCase(interactRequest.InteractingAboutGenkey)) ||
            (string.isNotBlank(interactRequest.InteractingWithNPIID) && string.isNotBlank(interactRequest.InteractingAboutNPIID) && interactRequest.InteractingWithNPIID.equalsIgnoreCase(interactRequest.InteractingAboutNPIID))))
        {
            isWithAboutSame = true;
        }

        //Retrieve Member records by passing Id or GenKey
        if(string.isNotBlank(interactRequest.InteractingWithType) && interactRequest.InteractingWithType.equalsIgnoreCase('Member'))
        {
            mapinteraction = getCallMemberAccountDetails(interactRequest.InteractingWithID, interactRequest.InteractingWithDepCode, interactRequest.InteractingWithGenkey, interactRequest.InteractingWithFirstName, interactRequest.InteractingWithLastName, interactRequest.InteractingWithPlatformCd, interactRequest.IsAuthenticated);
            //If account is found then assign values to Interacting With and Interacting About if both values are same
            if(null != mapinteraction && !mapinteraction.keyset().isEmpty() && !mapinteraction.values().isEmpty())
            {
                if (Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isUpdateable()) oCallInteraction.Interacting_With__c = new list<string>(mapinteraction.keyset())[0];
                if (Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isUpdateable()) oCallInteraction.Interacting_With_type__c = new list<string>(mapinteraction.values())[0];
                if(isWithAboutSame)
                {
                    if (Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()) oIntAbtMember.Interaction__c = oCallInteraction.Id;
                    if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()) oIntAbtMember.Interacting_About__c = new list<string>(mapinteraction.keyset())[0];
                    if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()) oIntAbtMember.Interacting_About_Type__c = new list<string>(mapinteraction.values())[0];
                }
            }
        }
        //Retrieve Provider records by passing TaxId or NPIID
        else if(string.isNotBlank(interactRequest.InteractingWithType) && interactRequest.InteractingWithType.equalsIgnoreCase('Provider'))
        {
            //If account is found then set the Provider type value
            if(string.isNotBlank(interactRequest.InteractingWithID) || string.isNotBlank(interactRequest.InteractingWithNPIID))
            {
                getCallProviderAccountDetails(interactRequest.InteractingWithID, interactRequest.InteractingWithNPIID, interactRequest.InteractingWithFirstName, interactRequest.InteractingWithLastName, sIntWith);
                isProviderType = true;
            }
        }
        //Retrieve Agent records by passing AgentId
        else if(string.isNotBlank(interactRequest.InteractingWithType) && interactRequest.InteractingWithType.equalsIgnoreCase('Agent'))
        {
            if(string.isNotBlank(interactRequest.InteractingWithID))
            {
                oAgentInteraction.PrepareCallAgentAccountMap(interactRequest.InteractingWithID);
                isAgentType = true;
            }
        }
        //Retrieve Group Records by passing GroupNumber
        else if (string.isNotBlank(interactRequest.InteractingWithType) && interactRequest.InteractingWithType.equalsIgnoreCase('Group'))
        {
            if(string.isNotBlank(interactRequest.InteractingWithID))
            {
                mapinteraction = getCallGroupAccountDetails(interactRequest.InteractingWithID, interactRequest.InteractingWithFirstName, interactRequest.InteractingWithLastName, sIntWith);
                //If account is found then assign values to Interacting With and Interacting About if both values are same
                if(null != mapinteraction && !mapinteraction.keyset().isEmpty() && !mapinteraction.values().isEmpty())
                {
                    if (Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isUpdateable()) oCallInteraction.Interacting_With__c = new list<string>(mapinteraction.keyset())[0];
                    if (Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isUpdateable()) oCallInteraction.Interacting_With_type__c = new list<string>(mapinteraction.values())[0];
                    if(isWithAboutSame)
                    {
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()) oIntAbtMember.Interaction__c = oCallInteraction.Id;
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()) oIntAbtMember.Interacting_About__c = new list<string>(mapinteraction.keyset())[0];
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()) oIntAbtMember.Interacting_About_Type__c = new list<string>(mapinteraction.values())[0];
                    }
                }
            }
        }

        //If InteractingWith and About are not same then make another call to retieve records
        if(!isWithAboutSame)
        {
            //Retrieve Member records
            if(string.isNotBlank(interactRequest.InteractingAboutType) && interactRequest.InteractingAboutType.equalsIgnoreCase('Member'))
            {
                mapinteraction = getCallMemberAccountDetails(interactRequest.InteractingAboutID, interactRequest.InteractingAboutDepCode, interactRequest.InteractingAboutGenkey, interactRequest.InteractingAboutFirstName, interactRequest.InteractingAboutLastName, interactRequest.InteractingAboutPlatformCd, interactRequest.IsAuthenticated);
                if(null != mapinteraction && !mapinteraction.keyset().isEmpty() && !mapinteraction.values().isEmpty())
                {
                    if (Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()) oIntAbtMember.Interaction__c = oCallInteraction.Id;
                    if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()) oIntAbtMember.Interacting_About__c = new list<string>(mapinteraction.keyset())[0];
                    if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()) oIntAbtMember.Interacting_About_Type__c = new list<string>(mapinteraction.values())[0];
                }
            }
            //Retrieve Provider records
            else if(string.isNotBlank(interactRequest.InteractingAboutType) && interactRequest.InteractingAboutType.equalsIgnoreCase('Provider'))
            {
                if(string.isNotBlank(interactRequest.InteractingAboutID) || string.isNotBlank(interactRequest.InteractingAboutNPIID))
                {
                    getCallProviderAccountDetails(interactRequest.InteractingAboutID, interactRequest.InteractingAboutNPIID, interactRequest.InteractingAboutFirstName, interactRequest.InteractingAboutLastName, sIntAbout);
                    isProviderType = true;
                }
            }
            //Retrieve Agent records
            else if(string.isNotBlank(interactRequest.InteractingAboutType) && interactRequest.InteractingAboutType.equalsIgnoreCase('Agent'))
            {
                if(string.isNotBlank(interactRequest.InteractingAboutID))
                {
                    oAgentInteraction.PrepareCallAgentAccountMap(interactRequest.InteractingAboutID);
                    isAgentType = true;
                }
            }
            //Retrieve Group Records
            else if (string.isNotBlank(interactRequest.InteractingAboutType) && interactRequest.InteractingAboutType.equalsIgnoreCase('Group'))
            {
                if(string.isNotBlank(interactRequest.InteractingAboutID))
                {
                    mapinteraction = getCallGroupAccountDetails(interactRequest.InteractingAboutID, interactRequest.InteractingAboutFirstName, interactRequest.InteractingAboutLastName, sIntAbout);
                    if(null != mapinteraction && !mapinteraction.keyset().isEmpty() && !mapinteraction.values().isEmpty())
                    {
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()) oIntAbtMember.Interaction__c = oCallInteraction.Id;
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()) oIntAbtMember.Interacting_About__c = new list<string>(mapinteraction.keyset())[0];
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()) oIntAbtMember.Interacting_About_Type__c = new list<string>(mapinteraction.values())[0];
                    }
                }
            }
        }

        //If there is Provider Type in Interacting With or About then upsert the retieved account from EPSS service
        //and get the account details after upsert. This call is made last as service call should be made before committing
        //any database call like upsert or update
        if(isProviderType)
        {
            map<String,map<String,String>> mapProviderDetails = new map<String,map<String,String>>();
            set<string> setAccountId = new set<string>();
            //Retieved Provider account from EPSS is upserted
            mapProviderDetails.putAll(calltoUpsertAccounts());
            //Retieved Provider account for With account
            if(mapProviderDetails.containskey(sIntWith)) mapInteractingWithAccount.putAll(mapProviderDetails.get(sIntWith));
            //Retieved Provider account for About account
            if(mapProviderDetails.containskey(sIntAbout)) mapInteractingAboutAccount.putAll(mapProviderDetails.get(sIntAbout));
            //If Account values are not empty then put in set to retieve the account details based on Id
            if(!mapInteractingWithAccount.Values().isEmpty()) setAccountId.addAll(mapInteractingWithAccount.Values());
            if(!mapInteractingAboutAccount.Values().isEmpty()) setAccountId.addAll(mapInteractingAboutAccount.Values());
            if(setAccountId != null && !setAccountId.isEmpty()) {
                //Account details are retrieved and assigned to With and About of Interaction
                for(Account a: [Select Id,recordtypeId,recordtype.name from Account where id in :setAccountId AND ETL_Record_Deleted__c = false])
                {
                    string acctid = a.Id;
                    if(!mapInteractingWithAccount.keyset().isEmpty() && ((mapInteractingWithAccount.containsKey(interactRequest.InteractingWithID) && mapInteractingWithAccount.get(interactRequest.InteractingWithID).equalsIgnoreCase(a.Id)) || 
                    (mapInteractingWithAccount.containsKey(interactRequest.InteractingWithNPIID) && mapInteractingWithAccount.get(interactRequest.InteractingWithNPIID).equalsIgnoreCase(a.Id))))
                    {
                        if (Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isUpdateable()) oCallInteraction.Interacting_With__c = a.Id;
                        if (Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isUpdateable()) oCallInteraction.Interacting_With_type__c = a.recordtype.name;
                        if(isWithAboutSame)
                        {
                            if (Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()) oIntAbtMember.Interaction__c = oCallInteraction.Id;
                            if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()) oIntAbtMember.Interacting_About__c =  a.Id;
                            if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()) oIntAbtMember.Interacting_About_Type__c = a.recordtype.name;
                        }
                    }
                    else if(!mapInteractingAboutAccount.keyset().isEmpty() && ((mapInteractingAboutAccount.containsKey(interactRequest.InteractingAboutID) && mapInteractingAboutAccount.get(interactRequest.InteractingAboutID).equalsIgnoreCase(a.Id)) || 
                        (mapInteractingAboutAccount.containsKey(interactRequest.InteractingAboutNPIID) && mapInteractingAboutAccount.get(interactRequest.InteractingAboutNPIID).equalsIgnoreCase(a.Id)) ))
                    {
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()) oIntAbtMember.Interaction__c = oCallInteraction.Id;
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()) oIntAbtMember.Interacting_About__c =  a.Id;
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()) oIntAbtMember.Interacting_About_Type__c = a.recordtype.name;
                    }
                }
           }
        }
        //If there is Agent Type in Interacting With or About then upsert the retieved account from Producer service
        //and get the account details after upsert. This call is made last as service call should be made before committing
        //any database call like upsert or update
        if(isAgentType)
        {
            map<String,Account> mapAgentDetails = new map<String,Account>();
            //Retieved Agent account from Producer is upserted
            mapAgentDetails.putAll(oAgentInteraction.CreateCallAccountRecords());
            if(null != mapAgentDetails && mapAgentDetails.containsKey(interactRequest.InteractingWithID))
            {
                //Account details are retrieved and assigned to With and About of Interaction
                Account oWithAccount = mapAgentDetails.get(interactRequest.InteractingWithID);
                if(null != oWithAccount)
                {
                    if (Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isUpdateable()) oCallInteraction.Interacting_With__c = oWithAccount.Id;
                    if (Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isUpdateable()) oCallInteraction.Interacting_With_type__c = 'Agent';
                    if(isWithAboutSame)
                    {
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()) oIntAbtMember.Interaction__c = oCallInteraction.Id;
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()) oIntAbtMember.Interacting_About__c =  oWithAccount.Id;
                        if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()) oIntAbtMember.Interacting_About_Type__c = 'Agent';
                    }
                }
            }
            //Account details are retrieved and assigned to About of Interaction if With is different Id
            if(!isWithAboutSame && null != mapAgentDetails && mapAgentDetails.containsKey(interactRequest.InteractingAboutID))
            {
                Account oAboutAccount = mapAgentDetails.get(interactRequest.InteractingAboutID);
                if(null != oAboutAccount)
                {
                    if (Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()) oIntAbtMember.Interaction__c = oCallInteraction.Id;
                    if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()) oIntAbtMember.Interacting_About__c =  oAboutAccount.Id;
                    if (Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()) oIntAbtMember.Interacting_About_Type__c = 'Agent';
                }
            }
        }

        //If Interacting About Member values is available then record is inserted
        if(oIntAbtMember != null && string.isNotBlank(oIntAbtMember.Interacting_About__c))
        {
            insert oIntAbtMember;
        }
        //Update Interacting With values
        if(oCallInteraction != null)
        {
            update oCallInteraction;
        }
        
    }

    /*
    * getCallMemberAccountDetails
    * <p>
    * This method is used to get member Account details for InteractingWith and Interacting About for Interaction 
    * @returntype map<string,string>
    * @param    accountId, memberGenKey, accountFirstName, accountLastName, IsAuthenticate
    */
    private static map<string,string> getCallMemberAccountDetails(string accountId, string acctDepCode, string memberGenKey, string accountFirstName, string accountLastName, string platformCd, boolean IsAuthenticate)
    {
        map<string,string> mapinteraction = new map<string,string>();
        boolean isMemberFound = false;
        //If the call is authenticated then GenKey will be passed and value will be retrieved based on Genkey
        if(isAuthenticate && string.isNotBlank(memberGenKey) && long.valueOf(memberGenKey) !=0)
        {
            List<Account> lstAcct = [SELECT Id, Mbr_Gen_Key__c,RecordTypeId, RecordType.Name , LastName, FirstName from Account where Mbr_Gen_Key__c = :memberGenKey and ETL_Record_Deleted__c = false limit 1];
            if(null != lstAcct && !lstAcct.isEmpty())
            {
                isMemberFound = true;
                mapinteraction.put(lstAcct[0].Id,lstAcct[0].RecordType.Name);

            }
        }
        //If Member is not found based on Genkey the Member record will be retrieved based on MemberId 
        if(!isMemberFound)
        {
            if(string.isNotBlank(accountId))
            { 
                list<Member_Id__c> lstMemberRecs = getCallMemberRecords(accountId, acctDepCode, accountFirstName, accountLastName, platformCd, IsAuthenticate);
                if(null != lstMemberRecs && !lstMemberRecs.isEmpty() && lstMemberRecs.size() == 1)
                {
                    mapinteraction.put(lstMemberRecs[0].Policy_Member__r.Member__c, lstMemberRecs[0].Policy_Member__r.Member__r.RecordType.Name);
                }

            }
        }
        return mapinteraction;

    }

    /*
    * getCallProviderAccountDetails
    * <p>
    * This method is used to get Provider Account details for InteractingWith and Interacting About for Interaction 
    * @param    accountId, npiId, accountFirstName, accountLastName, interactingType
    */
    private static void getCallProviderAccountDetails(string accountId, string npiID, string accountFirstName, string accountLastName, string interactingType)
    {
        map<string,string> mapinteraction = new map<string,string>();
        map<string,string> interactingAccountsMap = new map<string,string>();
        //Wrapper Object is created to call Common method for get provider details
        CaseServiceRestInteraction_DTO_HUM interactionWrapObj  = new CaseServiceRestInteraction_DTO_HUM();
        interactionWrapObj.sIntID = accountId;
        interactionWrapObj.sFirstName = accountFirstName;
        interactionWrapObj.sLastname = accountLastName;
        interactionWrapObj.sTaxId = accountId;
        interactionWrapObj.sNPIID = npiID;
        accountMapToUpsertProv = new Map<String,Account>();
        //If account is available then the records are stored in map to upsert it later once all the service calls are done for the transaction
        List<Account> lstAccount = new List<Account>();
        accountMapToUpsertProv.putAll( new CaseServiceProvider_H_HUM().searchInEpassService(interactionWrapObj,interactingType));
        if(accountMapToUpsertProv.get(interactingType)!=null)
        {
            lstAccount.add(accountMapToUpsertProv.get(interactingType));
            accountMapToUpsert.put(interactingType,lstAccount);
        }
    }

    /*
    * getCallGroupAccountDetails
    * <p>
    * This method is used to get Group Account details for InteractingWith and Interacting About for Interaction 
    * @returntype map<string,string>
    * @param    accountId, accountFirstName, accountLastName, interactingType
    */
    private static map<string,string> getCallGroupAccountDetails(string accountId, string accountFirstName, string accountLastName, string interactingType)
    {
        map<string,string> mapinteraction = new map<string,string>();
        map<string,string> interactingAccountsMap = new map<string,string>();
        //Wrapper Object is created to call Common method for getting Group details
        CaseServiceRestInteraction_DTO_HUM interactionWrapObj  = new CaseServiceRestInteraction_DTO_HUM();
        interactionWrapObj.sIntID = accountId;
        interactionWrapObj.sFirstName = accountFirstName;
        interactionWrapObj.sLastname = accountLastName;
        interactionWrapObj.sGroupID = accountId;
        //Call Group Query to retrieve the values based on Group Number
        map<string, Account> mapGroupDetails = new CaseServiceGroup_H_HUM().queryGroupAccount(interactionWrapObj,interactingType);
        if(null != mapGroupDetails && !mapGroupDetails.values().isEmpty() && mapGroupDetails.containskey(interactingType))
        {
            Account oWithAccount = mapGroupDetails.get(interactingType);
            mapinteraction.put(oWithAccount.Id, oWithAccount.recordtype.name);
        }
        
        return mapinteraction;

    } 

    /*
    * getCallMemberRecords
    * <p>
    * This method is used to get Member records based on MemberId from SOSL query for InteractingWith and Interacting About for Interaction 
    * @returntype list<Member_Id__c> 
    * @param    accountId, accountFirstName, accountLastName
    */
    @TestVisible
    private static list<Member_Id__c> getCallMemberRecords(string accountId, string acctDepCode, string accountFirstName, string accountLastName, string platformCd, boolean IsAuthenticate)
    {
        list<Member_Id__c> lstMemberRecs = new list<Member_Id__c>(); 
        string memDependCode;
        string memberID;
        boolean bSwitch_2854267 =  HUMUtilityHelper.isCRMFunctionalityON('2854267');
        if(bSwitch_2854267)
        {
        if(string.isNotBlank(platformCd) && platformCd.equalsIgnoreCase('CB'))
        {
            memDependCode  = acctDepCode;
            memberID = accountId;
        }
        else
        {
            if(!IsAuthenticate)
        {
                memberID = accountId;
		memDependCode = acctDepCode;
            }
            else
            {
                if(accountId.length() > 9 && accountId.length() < 13 && (accountId.substring(0, 1).equalsIgnoreCase('H') || accountId.isNumeric()))
                {
            memDependCode = accountId.substring(9,accountId.length());
            memberID = accountId.substring(0,(accountId.length()-2));
        }
                else
                {
            memberID = accountId;
                    memDependCode = acctDepCode;
                    }
                }
            }
        }
        else
        {
            //If AccountId passed is more than 10 then it consists of MemberId base and dependent code so need to retrieve seperate to hit query
            if(accountId.length() > 10)
            {
                memDependCode = accountId.substring(9,accountId.length());
                memberID = accountId.substring(0,(accountId.length()-2));
            }
            else
            {
                memberID = accountId;
            }
        }
        
        string sMemIdQuery = 'FIND \'' + string.escapeSingleQuotes(memberID) + '\' IN NAME FIELDS RETURNING Member_Id__c(Name,Member_Dependent_Code__c ,'+
                            'Policy_Member__r.Member__r.Id, Policy_Member__r.ETL_Record_Deleted__c,Policy_Member__r.Member__c, Policy_Member__r.Member__r.FirstName, '+
                            'Policy_Member__r.Member__r.LastName, Policy_Member__r.Member__r.RecordTypeId, Policy_Member__r.Member__r.RecordType.Name  Where Type__c in :setMemberIDTypes and Policy_Member__r.ETL_Record_Deleted__c = false ';
        if(String.isNotBlank(memDependCode))
        {
            sMemIdQuery += ' AND Member_Dependent_Code__c = \'' + string.escapeSingleQuotes(memDependCode) + '\' Limit ' + iSearchLimit+')';
        }
        else
        {
            sMemIdQuery += ' Limit ' + iSearchLimit+')';
        }
        //Retrieve the Member ID records    
        list<list<Member_Id__c>> searchList = search.query(sMemIdQuery);
        list<Member_Id__c> lstMembers = ((List<Member_Id__c>)searchList[0]);
        if(lstMembers != null && !lstMembers.isEmpty())
        {
            for(Member_Id__c obj : lstMembers)
            {
                if(memberID.startsWithIgnoreCase(obj.Name))
                {
                    //If FirstName and LastName is passed the value is matched to find correct record
                    if(string.isNotBlank(accountFirstName) && string.isNotBlank(accountLastName))
                    {
                        if(accountFirstName.trim().equalsIgnoreCase((obj.Policy_Member__r.Member__r.FirstName).trim()) &&
                            accountLastName.trim().equalsIgnoreCase((obj.Policy_Member__r.Member__r.LastName).trim()))
                        {
                            lstMemberRecs.add(obj);
                            break;      
                        }
                    }
                    else
                    {
                        lstMemberRecs.add(obj);
                    }
                }
            }
        }

        return lstMemberRecs;
    }

    /*
    * getMemberRecords
    * <p>
    * This method is used to retrieve the Member_ID__c records 
    *  
    * @param    mapIntAboutCriteria(Input Interacting About values), mapIntWithCriteria(Input Interacting With values)  
    * @return   list<Member_Id__c>                  
    */
    @TestVisible
    private static list<Member_Id__c> getMemberRecords(map<string,set<string>> mapIntAboutCriteria, map<string,set<string>> mapIntWithCriteria)
    {        
        list<Member_Id__c> lstMemberRecs = new list<Member_Id__c>(); 
        string sMemberQuery ='';
        set<string> setIdValues = new set<string>();    
        set<string> setIDTypes = new set<string>();
        list<set<string>> lstIntIDValuesList= new list<set<string>>();
        String sSearchKey = '';
        if(mapIntAboutCriteria!=null && !mapIntAboutCriteria.isEmpty())
        {            
            for(string sType : mapIntAboutCriteria.keySet())
            {
                if(setMemberIDTypes.contains(sType)) setIDTypes.add(sType);
                lstIntIDValuesList.add(mapIntAboutCriteria.get(sType));  //add all IntAbout ID values.  
            }
        }
        
        if(mapIntWithCriteria!=null && !mapIntWithCriteria.isEmpty())
        {            
            for(string sType : mapIntWithCriteria.keySet())
            {
                 if(setMemberIDTypes.contains(sType)) setIDTypes.add(sType);
                lstIntIDValuesList.add(mapIntWithCriteria.get(sType));  //add all IntWith ID values   
            }
        } 
        // adding intwith annd intabout to one single set of ids        
        for(set<string> sIdVal : lstIntIDValuesList)
        {
            setIdValues.addAll(sIdVal);
            for(String sVal : sIdVal)
            {
                sSearchKey = sSearchKey + ' OR ' + sVal +'*' ;
            }
        }        
        sSearchKey = sSearchKey.substring(4,sSearchKey.length());
        sMemberQuery = 'FIND \'' + String.escapeSingleQuotes(sSearchKey)  + '\' IN NAME FIELDS RETURNING Member_Id__c(ID, name, Type__c, Policy_Member__r.Member__c,Member_Dependent_Code__c, Policy_Member__r.Member__r.name ' 
                         +',Policy_Member__r.Member__r.firstname,Policy_Member__r.Member__r.lastname, Policy_Member__r.Member__r.Birthdate__c, Policy_Member__r.Member__r.PersonBirthdate'
                         +   ' WHERE Type__c IN :  setIDTypes and Policy_Member__r.Member__r.ETL_Record_Deleted__c = false)';

        if(setIdValues.size()>0)
        {   
            //Retrieve the Member ID records    
            List<List<Member_Id__c>>searchList = search.query(sMemberQuery);
            List<Member_Id__c> tempMemberList = searchList[0];
            If(tempMemberList != Null && !tempMemberList.isEmpty())
            {
                For(Member_Id__c obj : tempMemberList)
                {
                    If(setIdValues.contains(obj.Name))
                    {
                        lstMemberRecs.Add(obj);
                    }
                }
            }
        }       
        return lstMemberRecs;           
    }
    
    /*
    * getMatchingRecords
    * <p>
    * This method is used to match the Member ID records retrieved against the input Interacting About and Interaction with values. Also populate the matching 
    * Accounts in the final results Map. 
    * @param    lstMemberRecs(List of Member ID Recs), mapIntWithAboutCriteria(Input Interacting About/With values), mapMemberDependentCode(Map of dependent code values)  
                String Interacting Type 
    * @return   map<string,string> Map of Interacting Id to Salesforce Account Ids                
    */
    @TestVisible
    public static map<string,string> getMatchingRecords(list<Member_Id__c>lstMemberRecs, map<string,set<string>> mapIntWithAboutCriteria, map<string, map<string,set<string>>> mapMemberDependentCode, String sIntType)
    {  
        set<string> setMemberAccts = new set<string>();       
        map<string,set<string>> mapIntMemberCriteria = new map<string,set<string>>();
        map<string, String> mapMemberAccount=new map<string, string>();
        if(mapIntWithAboutCriteria != null & !mapIntWithAboutCriteria.isEmpty())
        {
            for(string memType:mapIntWithAboutCriteria.keySet())
            {
                if(setMemberIDTypes.contains(memType))
                mapIntMemberCriteria.put(memType, mapIntWithAboutCriteria.get(memType).clone());
            }
        }
       
        if(lstMemberRecs!=NULL && !lstMemberRecs.isEmpty())
        {
            map<string, set<string>> mapIntDependentCode = new map<string, set<string>>();
            set<string> setIntMemberVal= new set<string>();
            if(mapMemberDependentCode!=null && mapMemberDependentCode.containsKey(sIntType)) mapIntDependentcode = mapMemberDependentCode.get(sIntType);
            set<string> setDependentCodeVal = new set<string>();
        
            for(Member_ID__c member:lstMemberRecs)
            {
                setIntMemberVal = new set<string>();                      
                if(mapIntMemberCriteria.containsKey(member.Type__c))  setIntMemberVal = mapIntWithAboutCriteria.get(member.Type__c);                          
                if(member.Type__c.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM))  //For Member-Id-Base
                {
                    
                    setDependentCodeVal = new set<string>();
                    if(mapIntDependentcode.containsKey(member.name)) setDependentCodeVal = mapIntDependentCode.get(member.name);                            
                    if(setIntMemberVal.contains(member.name) && mapIntDependentCode!=null && mapIntDependentCode.containsKey(member.name)
                    && mapIntDependentCode.get(member.name).contains(member.Member_Dependent_Code__c))
                    {
                      if(!mapMemberAccount.containsKey(member.name))                        
                      { mapMemberAccount.put(member.name,member.Policy_Member__r.Member__c);}     
                    }
                }
                else
                {
                    if(setIntMemberVal.contains(member.name))
                    {
                        if(!mapMemberAccount.containsKey(member.name))                        
                        { mapMemberAccount.put(member.name,member.Policy_Member__r.Member__c);}     
                    }
                }              
            }
        }    
        return mapMemberAccount;   
    }
                    
    //This method is used to filter memeber records based on firstname and lastname
   public static map<string,string> getMatchingRecordsWithName(list<Member_Id__c>lstMemberRecs, map<string,set<string>> mapIntWithAboutCriteria, String sIntType, List<InteractingAccount_DTO_HUM> listMemberIntAc)
    {  
        set<string> setMemberAccts = new set<string>();       
        map<string,set<string>> mapIntMemberCriteria = new map<string,set<string>>();
        map<string, String> mapMemberAccount=new map<string, string>();
        if(mapIntWithAboutCriteria != null & !mapIntWithAboutCriteria.isEmpty())
        {
            for(string memType:mapIntWithAboutCriteria.keySet())
            {
                if(setMemberIDTypes.contains(memType))
                mapIntMemberCriteria.put(memType, mapIntWithAboutCriteria.get(memType).clone());
            }
        }
       
        if(lstMemberRecs!=NULL && !lstMemberRecs.isEmpty())
        {
            map<string, set<string>> mapIntDependentCode = new map<string, set<string>>();
            set<string> setIntMemberVal= new set<string>();
        
            for(Member_ID__c member:lstMemberRecs)
            {
                setIntMemberVal = new set<string>();                      
                if(mapIntMemberCriteria.containsKey(member.Type__c))  setIntMemberVal = mapIntWithAboutCriteria.get(member.Type__c);                          
                if(member.Type__c.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM))  //For Member-Id-Base
                {
                    if(setIntMemberVal.contains(member.name))
                    {	
                             boolean firstNameMatch = listMemberIntAc.get(0).FirstName.trim().equalsIgnoreCase((member.Policy_Member__r.Member__r.FirstName).trim());
							boolean lastNameMatch = listMemberIntAc.get(0).LastName.trim().equalsIgnoreCase((member.Policy_Member__r.Member__r.LastName).trim());                                                          
                            
                            if(!firstNameMatch || !lastNameMatch)
                            {
                               continue;                                                                               
                            }
                        if(!mapMemberAccount.containsKey(member.name))                        
                        { 
                            mapMemberAccount.put(member.name,member.Policy_Member__r.Member__c);
                        	break;
                        }     
                    }
                }              
            }
        }    
        return mapMemberAccount;   
    }
   
   /*
    * createMapAcctIDValuesByType
    * <p>
    * This method is used to populate values in mapAcctIDValuesbyType. mapAcctIDValuesbyType will have the key as API Name   
    * of the Entity IDType and set of Entity IDValues under each IDType.
    * @param    map<string,set<string>>(map of Input IDs by type from Input request).
    * @return   map<string,String> Map of Interacting Id to Salesforce Account Ids   
    */
    @TestVisible    
    public static map<string,String> createMapAcctIDValuesByType( map<string,set<string>> mapIntWithAbt)
    {                          
        list<Account> lstAccountRecs = new list<Account>();
        map<string, String> mapEnterpriseAccount=new map<string, string>(); 
        map<string,set<string>> mapAcctIDValuesbyType = new map<string,set<string>>();        
        if(mapAcctIDValuesbyType.containsKey(mapAcctIDTypes.get(ACCOUNT_ENTERPRISEID)))
            mapAcctIDValuesbyType.get(mapAcctIDTypes.get(ACCOUNT_ENTERPRISEID)).addAll(mapIntWithAbt.get(ACCOUNT_ENTERPRISEID).clone());
        else
            mapAcctIDValuesbyType.put(mapAcctIDTypes.get(ACCOUNT_ENTERPRISEID), mapIntWithAbt.get(ACCOUNT_ENTERPRISEID).clone());  
        if(mapAcctIDValuesbyType!=null && !mapAcctIDValuesbyType.isEmpty())
        {           
            lstAccountRecs.addAll(getAccountRecords(mapAcctIDValuesbyType));
        }  
        if(lstAccountRecs!=null && !lstAccountRecs.isEmpty())
        {
            mapEnterpriseAccount = getMatchingAccounts(lstAccountRecs, mapIntWithAbt);
        }                                                                 
        return mapEnterpriseAccount;       
    } 
    
    /*
    * getAccountRecords
    * <p>
    * This method is used to query the accounts for Input EnterpriseIDs. 
    *  
    * @param    mapAcctIDValuesbyType(map of Input IDs by type from Input request).
    * @return   list<Account>
    */
    @TestVisible
    private static list<Account> getAccountRecords(map<string,set<string>> mapAcctIDValuesbyType)
    {
        list<Account> lstAccounts = new list<Account>();        
        string sAccountQuery = 'SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c FROM Account WHERE';        
        string sWhereClause = '';
        map<string, list<string>> mapSearchValues = new map<string, list<string>>();
        
        if(mapAcctIDValuesbyType!=null && !mapAcctIDValuesbyType.isEmpty())
        {
            integer i = 0;
            for(string sIDType : mapAcctIDValuesbyType.keySet())
            {
                list<string> lstStrValues = new list<string>();
                for(string sValue : mapAcctIDValuesbyType.get(sIDType))
                {
                    lstStrValues.add('\''+String.escapeSingleQuotes(sValue)+'\'');
                }
                mapSearchValues.put(sIDType, lstStrValues);
            }
              
            if(mapSearchValues!=null && !mapSearchValues.isEmpty())
            {               
                for(String sIDType : mapSearchValues.keySet())
                {
                    
                    if(i==0)
                    {
                        i++;                        
                        sWhereClause += ' ' + String.escapeSingleQuotes(sIDType) + ' IN ' + mapSearchValues.get(sIDType);
                    }
                    else                        
                        sWhereClause += ' OR ' + String.escapeSingleQuotes(sIDType) + ' IN ' + mapSearchValues.get(sIDType);   
                }
            }
           sWhereClause +=' AND ETL_Record_Deleted__c = false'; 
            if(String.isNotBlank(sWhereClause))
            {
                sAccountQuery += sWhereClause;
                 
                lstAccounts = Database.Query(sAccountQuery); 
            }          
        }        
        return lstAccounts;
    }
  
   /*
    * getMatchingAccounts
    * <p>
    * This method is used to match the Accounts retrieved against the input Interacting About and Interaction with values. Also populate the matching 
    * Accounts in the final results Map. 
    * @param    lstAccountRecs(List of Accounts), map<string,set<string>>(Input Interacting About/With values)   
    * @return   map<string, String> Map of Interacting Id to Salesforce Account Ids        
    */
    @TestVisible
    private static map<string, String> getMatchingAccounts(list<Account> lstAccountRecs,  map<string,set<string>> mapMemberIntWithAbt)
    {
        map<string, String> mapEnterpriseAccount=new map<string, string>();
        set<string> setIntAccts = new set<string>(); 
        boolean bMatchMembers = false;  
        if(mapMemberIntWithAbt!=null && mapMemberIntWithAbt.containsKey(ACCOUNT_ENTERPRISEID))   bMatchMembers = true; 
        for(Account accountRec : lstAccountRecs) 
        {
            if(accountRec.recordtype.Name.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM) && bMatchMembers)
            {
                if(mapMemberIntWithAbt.get(ACCOUNT_ENTERPRISEID).contains(accountRec.Enterprise_ID__c))                    
                    mapEnterpriseAccount.put(accountRec.Enterprise_ID__c,accountRec.Id); 
            }
        }
        return mapEnterpriseAccount;
    } 
    
    /*
    * createUnknownAccWrapper
    * <p>
    * This method is used to match the Accounts retrieved against the input Interacting About and Interaction with values. Also populate the matching 
    * Accounts in the final results Map. 
    * @param    lstAccountRecs(List of Accounts), map<string,set<string>>(Input Interacting About/With values),sIntType (Interaction type)   
    * @return   Void                  
    */
    @TestVisible
    public static map<string,List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>> createUnknownAccWrapper(map<string, string> mapIntWithAbtAccount,list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> lstIntWithAbt, String sIntType )
    {
      map<string,List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>> mapIntWithAboutUnknownCriteria = new map<string,List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>>();
      for(CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO oIntWithAbt: lstIntWithAbt)
               {
                  if(!mapIntWithAbtAccount.containsKey(oIntWithAbt.ID))
                  {
                    if(oIntWithAbt.entityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM))
                     {
                     if(mapIntWithAboutUnknownCriteria.containsKey(sIntType))
                     {mapIntWithAboutUnknownCriteria.get(sIntType).add(oIntWithAbt);}
                     else{mapIntWithAboutUnknownCriteria.put(sIntType,new list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>{oIntWithAbt});}
                    }
                    //watson
                    if(oIntWithAbt.entityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM ))
                     {
                     if(mapIntWithAboutUnknownCriteria.containsKey(sIntType))
                     {mapIntWithAboutUnknownCriteria.get(sIntType).add(oIntWithAbt);}
                     else{mapIntWithAboutUnknownCriteria.put(sIntType,new list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>{oIntWithAbt});}
                    }
                  }
               }
    return mapIntWithAboutUnknownCriteria ;
    }
 
   /*
    * getInteractionWrapObj
    * <p>
    *  Method is used to get values of particular Interaction type.
    * @param    CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO  interactionWithAbout, string interactingType
    * @return   CaseServiceRestInteraction_DTO_HUM
    */ 
    public static CaseServiceRestInteraction_DTO_HUM getInteractionWrapObj(CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO interactionWithAbout, string interactingType)
    {
        CaseServiceRestInteraction_DTO_HUM  interactionWrapObj =new CaseServiceRestInteraction_DTO_HUM();
        interactionWrapObj =new CaseServiceRestInteraction_DTO_HUM();      
        interactionWrapObj.sIntID = interactionWithAbout.ID;
        interactionWrapObj.sIntIDType = interactionWithAbout.IDType;
        interactionWrapObj.sIntType = interactionWithAbout.EntityType;
        interactionWrapObj.sAccountName = interactionWithAbout.AccountName;
        interactionWrapObj.sFirstName = interactionWithAbout.FirstName;
        interactionWrapObj.sLastname = interactionWithAbout.LastName;
        interactionWrapObj.sBillingState = interactionWithAbout.State;
        interactionWrapObj.sStateCode = evaluateStateCode(interactionWithAbout.State);
        interactionWrapObj.sProviderClassification = interactionWithAbout.ProviderClassification;     
        interactionWrapObj.sInteractingZipCode = interactionWithAbout.ZipCode;
        interactionWrapObj.sInteractingHomePhone = interactionWithAbout.HomePhone;
        interactionWrapObj.sInteractingEmailID = interactionWithAbout.EmailID;
        interactionWrapObj.sPlatformCode = interactionWithAbout.PlatformCode;
        if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID)
        {
            interactionWrapObj.sTaxId = interactionWithAbout.ID;
        }
        else if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID)
        {                           
            interactionWrapObj.sNPIID = interactionWithAbout.ID;
        }
        else if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_AGENTID)
        {
            interactionWrapObj.sAgentId = interactionWithAbout.ID;
        }
        else if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPEID)
        {
            interactionWrapObj.sGroupID = interactionWithAbout.ID;
            
        }
        return interactionWrapObj;
    }   
    
    /*
    * evaluateStateCode
    * <p>
    * Method is used to evaluate StateCode.
    * @param  string stateCode
    * @return string
    */ 
    Public static string evaluateStateCode(string stateCode)
    {
        //Check if the input state code is available in the Picklist Options, else overwrite it 'OT' for 'Other State code'
        if(!string.isBlank(stateCode) && !stateCode.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.OTHER_STATE_CODE))
        {
            Schema.DescribeFieldResult fieldResult = Account.PERSONMAILINGSTATECODE.getDescribe();
            List<Schema.PicklistEntry> picklistentries = fieldResult.getPickListValues();
            Boolean stateFound = false;                    
            for(Schema.PicklistEntry picklistentry : picklistentries)
            {
                if(picklistEntry.isActive() && picklistEntry.getValue().equalsIgnoreCase(stateCode))
                {
                    stateFound = true;
                }       
            }
            if(!stateFound)
            { 
                stateCode = GLOBAL_CONSTANT_HUM.CASESERVICE_OTHER_STATE_CODE;       
            }
        }
        return stateCode;
    } 
    
   /*
    * getProviderAccountMap
    * <p>
    * This method is used to match the Accounts retrieved against the input Interacting About and Interaction with values. Also populate the matching 
    * Accounts in the final results Map. 
    * @param    CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO ointeractionwithAbt, string interactingType
    * @return   Void                  
    */
    public static void getProviderAccountMap(CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO ointeractionwithAbt, string interactingType)
    {
        try
        {
            CaseServiceRestInteraction_DTO_HUM interactionWrapObj  = new CaseServiceRestInteraction_DTO_HUM();
            interactionWrapObj = getInteractionWrapObj(ointeractionwithAbt, interactingType);
            accountMapToUpsertProv = new Map<String,Account>();
            if(interactionWrapObj.sIntIDType != GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE)
            {
                if(accountMapToUpsert.containsKey(interactingType))
                {
                    accountMapToUpsertProv = new CaseServiceProvider_H_HUM().searchInEpassService(interactionWrapObj,interactingType);
                    if(!accountMapToUpsertProv.isEmpty()) 
                    { 
                        accountMapToUpsert.get(interactingType).add(accountMapToUpsertProv.values());
                    }
                }
                else
                {
                    List<Account> lstAccount = new List<Account>();
                    accountMapToUpsertProv.putAll( new CaseServiceProvider_H_HUM().searchInEpassService(interactionWrapObj,interactingType));
                    if(accountMapToUpsertProv.get(interactingType)!=null)
                    {
                        lstAccount.add(accountMapToUpsertProv.get(interactingType));
                        accountMapToUpsert.put(interactingType,lstAccount);
                    }
                }
            }
            if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE||accountMapToUpsert.isEmpty()||
                (!accountMapToUpsert.isEmpty() && accountMapToUpsert.get(interactingType)==null)||
                (CaseServiceProvider_H_HUM.bisMultipleAcct!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(interactingType)!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(interactingType)))
            {
                prepareUnknownAccts(interactionWrapObj,interactingType);    
            }
             if(providerAcct!=null && !providerAcct.isEmpty()) {
                upsert providerAcct;
                mapunknownProvider.put(interactingType,providerAcct[0].id);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseInteractionSearch_H_HUM','getProviderAccountMap');
        }
    }   
    
    /*
    * getGroupAccountMap
    * <p>
    * This method is used to match the Accounts retrieved against the input Interacting About and Interaction with values. Also populate the matching 
    * Accounts in the final results Map. 
    * @param    CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO ointeractionwithAbt, string interactingType
    * @return   Void                  
    */
    public static void getGroupAccountMap(CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO ointeractionwithAbt, string interactingType)
    {
        try
        {
            CaseServiceRestInteraction_DTO_HUM interactionWrapObj  = new CaseServiceRestInteraction_DTO_HUM();
            interactionWrapObj = getInteractionWrapObj(ointeractionwithAbt, interactingType);
            accountMapToUpsertGroup = new Map<String,Account>();
            
            if(interactionWrapObj.sIntIDType != GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE)
            {
                if(groupaccountMapToUpsert.containsKey(interactingType))
                {
                    
                    accountMapToUpsertGroup = new CaseServiceGroup_H_HUM().queryGroupAccount(interactionWrapObj,interactingType);
                    if(!accountMapToUpsertGroup.isEmpty()) 
                    { 
                        
                        groupaccountMapToUpsert.get(interactingType).add(accountMapToUpsertGroup.values());
                    }
                }
                else
                {
                    
                    List<Account> lstAccount = new List<Account>();
                    accountMapToUpsertGroup.putAll( new CaseServiceGroup_H_HUM().queryGroupAccount(interactionWrapObj,interactingType));
                    
                    if(accountMapToUpsertGroup.get(interactingType)!=null)
                    {                        
                        lstAccount.add(accountMapToUpsertGroup.get(interactingType));
                        groupaccountMapToUpsert.put(interactingType,lstAccount);
                       
                    }
                }
            }
            if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE || groupaccountMapToUpsert.isEmpty()||
                (!groupaccountMapToUpsert.isEmpty() && groupaccountMapToUpsert.get(interactingType)==null)||
                (CaseServiceGroup_H_HUM.bisMultipleAcct!=null && CaseServiceGroup_H_HUM.bisMultipleAcct.get(interactingType)!=null && CaseServiceGroup_H_HUM.bisMultipleAcct.get(interactingType)))
            {
                
                prepareUnknownGroupAccts(interactionWrapObj,interactingType);    
            }
            
             if(GroupAcct !=null && !GroupAcct.isEmpty()) {
			 
               if (Schema.sObjectType.Account.isCreateable() && Schema.sObjectType.Account.isUpdateable()) {  	
                upsert GroupAcct;
                mapunknownGroup.put(interactingType,GroupAcct[0].id);
            }
			
           }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseInteractionSearch_H_HUM','getGroupAccountMap');
        }
    }   
      
    
   /*
    * prepareUnknownAccts
    * <p>
    * This method is used to prepare the unknown accounts for those service did not return response/internal accounts does not exist 
    * @param    CaseServiceRestInteraction_DTO_HUM interactionWrapObj,String interactingType
    * @return   Void                  
    */
    private static void prepareUnknownAccts(CaseServiceRestInteraction_DTO_HUM interactionWrapObj,String interactingType)
    {
        Account existingAccountId;
        Boolean bunknwnProvider = false;
        if(interactionWrapObj.sIntIDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE) && String.isBlank(interactionWrapObj.sIntID))
        {
            Account oAcct = new Account();
            bunknwnProvider = true;
            oAcct = CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj);
            providerAcct.add(oAcct);
        }
        if(String.isNotBlank(interactionWrapObj.sIntID))
        {
            //Moving all wrapper object for Provider search to pass in EPSS service
            existingAccountId=locateProviderByParams(interactionWrapObj);
        }
        Boolean isError = false;
        if(!bunknwnProvider && ((string.isBlank(interactionWrapObj.sIntID) && (interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE ))||
            accountMapToUpsert.isEmpty()||
            (!accountMapToUpsert.isEmpty() && accountMapToUpsert.get(interactingType)==null)||
            (CaseServiceProvider_H_HUM.bisMultipleAcct!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(interactingType)!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(interactingType))))
        {
            if(unknownAccountMapToUpsert.containsKey(interactingType))
            {
                unknownAccountMapToUpsert.get(interactingType).add(CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj));
                mapProviderId.put(iProviderKey,interactionWrapObj.sIntID+'-'+interactingType+'-'+interactionWrapObj.sIntIDType);
            }
            else
            {
                List<Account> lstAccount = new List<Account>();
                lstAccount.add(CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj));
                unknownAccountMapToUpsert.put(interactingType,lstAccount);
                mapProviderId.put(iProviderKey,interactionWrapObj.sIntID+'-'+interactingType+'-'+interactionWrapObj.sIntIDType);
            }
        }
      else
        {
            isError = true;
        }  
        iProviderKey++;
    }
    
    /*
    * prepareUnknownGroupAccts
    * <p>
    * This method is used to prepare the unknown accounts for those service did not return response/internal accounts does not exist 
    * @param    CaseServiceRestInteraction_DTO_HUM interactionWrapObj,String interactingType
    * @return   Void                  
    */
    private static void prepareUnknownGroupAccts(CaseServiceRestInteraction_DTO_HUM interactionWrapObj,String interactingType)
    {
        groupIDCheck = GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPEID;
        Account existingAccountId;
        Boolean bunknwnGroup = false;
        if(interactionWrapObj.sIntIDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPEID) && String.isBlank(interactionWrapObj.sIntID))
        {
            Account oAcct = new Account();
            bunknwnGroup = true;
            oAcct = CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj);
            GroupAcct.add(oAcct);
            
        }
        if(String.isNotBlank(interactionWrapObj.sIntID))
        {
            //Moving all wrapper object for Provider search to pass in EPSS service
            existingAccountId=locateProviderByParams(interactionWrapObj);
        }
        Boolean isError = false;
        if(!bunknwnGroup && ((string.isBlank(interactionWrapObj.sIntID) && (interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE))||
            accountMapToUpsert.isEmpty()||
            (!accountMapToUpsert.isEmpty() && accountMapToUpsert.get(interactingType)==null)||
            (CaseServiceGroup_H_HUM.bisMultipleAcct!=null && CaseServiceGroup_H_HUM.bisMultipleAcct.get(interactingType)!=null && CaseServiceGroup_H_HUM.bisMultipleAcct.get(interactingType))))
            {
            if(unknownAccountMapToUpsert.containsKey(interactingType))
            {
                unknownAccountMapToUpsert.get(interactingType).add(CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj));
                mapGroupId.put(iGroupKey,interactionWrapObj.sIntID+'-'+interactingType+'-'+interactionWrapObj.sIntIDType);
            }
            else
            {
                List<Account> lstAccount = new List<Account>();
                lstAccount.add(CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj));
                unknownAccountMapToUpsert.put(interactingType,lstAccount);
                
                mapGroupId.put(iGroupKey,interactionWrapObj.sIntID+'-'+interactingType+'-'+interactionWrapObj.sIntIDType);
            }
        }
      else
        {
            isError = true;
        }  
        iGroupKey++;
    }
    
    
   /*
    * locateProviderByParams
    * <p>
    * This method returns the Account Record for the respective UNknown Provider If any. 
    * @param    CaseServiceRestInteraction_DTO_HUM interactionwrapObj
    * @return   Account
    */
    Public static Account locateProviderByParams(CaseServiceRestInteraction_DTO_HUM interactionwrapObj)
    {
        List<Account> oProviderAccount = new List<Account>();
        Account providerId = new Account();
        //query to search for the provider account
        Map<String ,String> requestMap=new Map<String,String>();
        requestMap = new CaseServiceProvider_H_HUM().serviceInputMap(interactionwrapObj);
        oProviderAccount=new CaseServiceProvider_H_HUM().queryAccounts(requestMap);
        if(oProviderAccount!=null && oProviderAccount.size()>0)
        {
            providerId = oProviderAccount[0];
        }
        return providerId;
    }
    
   /*
    * calltoUpsertAccounts
    * <p>
    * This method is used to upsert accounts
    * @param   
    * @return   Map<String,Map<String,String>> key is Interacting With/About and value will be(Key(Tax/Npi Id) value(salesforce account id))
    */
    public static Map<String,Map<String,String>> calltoUpsertAccounts()
    {
        try
        {
            Map<String,List<Account>> mapresultAccount = new Map<String,List<Account>>();
            Map<String,Map<String,String>> mapFinalProviderData = new Map<String,Map<String,String>>();
           
            if(accountMapToUpsert!=null && !accountMapToUpsert.isEmpty())
            {
                mapresultAccount = upsertAccountRec(accountMapToUpsert);
               
                if(mapresultAccount!=null && !mapresultAccount.isEmpty())
                {
                    upsertconsumerIds(accountMapToUpsert); 
               }
            } 
            Map<String,Map<String,String>> mapUnknownAccts = new Map<String,Map<String,String>>();
            if(unknownAccountMapToUpsert!=null && !unknownAccountMapToUpsert.isEmpty())
            {
                mapUnknownAccts = insertUnknownAccounts(mapProviderId,unknownAccountMapToUpsert); 
            }
            mapFinalProviderData = prepareFinalMap(mapUnknownAccts,mapresultAccount);
            return mapFinalProviderData;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseInteractionSearch_H_HUM','calltoUpsertAccounts');
            return null; 
        }
    }
    
    /*
    * prepareFinalMap
    * <p>
    * This method is used to prepare the final map to create interaction and interaction members
    * @param  Map<String,Map<String,String>> mapUnknownAccts, Map<String,List<Account>> mapresultAccount 
    * @return   Map<String,Map<String,String>> key being Interactiong With/About and value being map<tax/npi id, relevant salesforce accountid>
    */
    Public static Map<String,Map<String,String>> prepareFinalMap(Map<String,Map<String,String>> mapUnknownAccts, Map<String,List<Account>> mapresultAccount)
    {
        try
        {
            Map<String,Map<String,String>> mapProviderAccts = new Map<String,Map<String,String>>();
            Map<String,Map<String,String>> mapFinalProviderData = new Map<String,Map<String,String>>();
            Map<String,Map<String,String>> mapTemp = new Map<String,Map<String,String>>();
            mapTemp = prepareProviderAcctsMap(sIntWith,mapresultAccount);
            if(mapTemp !=null)
                mapProviderAccts.putAll(mapTemp );
            mapTemp = new Map<String,Map<String,String>>();
            mapTemp = prepareProviderAcctsMap(sIntAbout ,mapresultAccount);
            if(mapTemp !=null)
                mapProviderAccts.putAll(mapTemp);
            
            mapFinalProviderData.putAll(mapProviderAccts);
            if(mapUnknownAccts!=null && !mapUnknownAccts.isEmpty())
            {
                for(String skey : mapUnknownAccts.keyset())
                {
                    if(mapFinalProviderData!=null && mapFinalProviderData.containsKey(skey))
                    {
                        /*map<string,string> temp = new Map<String,string>();
                        temp = mapUnknownAccts.get(skey);*/
                        mapFinalProviderData.get(skey).putAll(mapUnknownAccts.get(skey));
                    }
                    else
                    {
                        /*map<string,string> temp = new Map<String,string>();
                        temp = mapUnknownAccts.get(skey);*/
                        mapFinalProviderData.put(skey, mapUnknownAccts.get(skey));
                    }
                }
            }
            
            return mapFinalProviderData;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseInteractionSearch_H_HUM','prepareFinalMap');
            return null; 
        }
    }
    
    /*
    * prepareProviderAcctsMap
    * <p>
    * This method is used to prepare the map with provider accounts
    * @param  String interactionType,Map<String,List<Account>> resultAccountLst
    * @return   Map<String,Map<String,String>> key being Interactiong With/About and value being map<tax/npi id, relevant salesforce accountid>
    */    
    public static Map<String,Map<String,String>> prepareProviderAcctsMap(String interactionType,Map<String,List<Account>> resultAccountLst)
    {
        Map<String,Map<String,String>> mapproviderAccts = new Map<String,Map<String,String>>();
        Map<String,String> mapInteractingWithAbout = CaseServiceProvider_H_HUM.mapReturnValues.get(interactionType);
       
        Map<String,String> mapInteractingWithAboutGroup = new Map<String,String>();
         //if(groupIDCheck != ''){
       if(CaseServiceGroup_H_HUM.mapReturnValues.containskey(interactionType)){
        mapInteractingWithAboutGroup = CaseServiceGroup_H_HUM.mapReturnValues.get(interactionType);
       
        }

        if(mapInteractingWithAbout != null)
        {
            
            for(String sExternalId : mapInteractingWithAbout.keyset())
            {
                if(resultAccountLst.containskey(interactionType)){
                    for(Account accRec: resultAccountLst.get(interactionType))
                    {
                        if(accRec.Account_External_Id__c == sExternalId)
                        {
                            if(mapproviderAccts.containsKey(interactionType))
                            {
                                mapproviderAccts.get(interactionType).put(mapInteractingWithAbout.get(sExternalId),accRec.id);
                            }
                            else
                            {
                                mapproviderAccts.put(interactionType, new Map<String,String>{mapInteractingWithAbout.get(sExternalId)=>accRec.id});
                            }
                        }
                    }
                }
                
            }
            return mapproviderAccts;
        }
        //for watson
        else if(mapInteractingWithAboutGroup != null && !mapInteractingWithAboutGroup.isEmpty())
        {
            
            for(String sExternalId : mapInteractingWithAboutGroup .keyset())
            {
				// map containskey added to fix null pointer issue
				if(resultAccountLst.containskey(interactionType)){
					for(Account accRec: resultAccountLst.get(interactionType))
					{
						if(accRec.Account_External_Id__c == sExternalId)
						{
							if(mapproviderAccts.containsKey(interactionType))
							{
								mapproviderAccts.get(interactionType).put(mapInteractingWithAboutGroup.get(sExternalId),accRec.id);
							}
							else
							{
								mapproviderAccts.put(interactionType, new Map<String,String>{mapInteractingWithAboutGroup.get(sExternalId)=>accRec.id});
							}
						}
					}
				}
            }
            
            return mapproviderAccts;
        }
        
        else
        {
           
            return null;
        }
    }
    

    /*
    * insertUnknownAccounts
    * <p>
    * This method is used to insert unknown accounts when the service did not return any data or no record exists internally
    * @param (Map<Integer,String> mapProviderID, Map<String,List<Account>> mapUnknownAccounts
    * @return   Map<String,Map<String,String>> key is Interacting With/About and value will be(Key(Tax/Npi Id) value(salesforce account id))
    */    
    Public static Map<String,Map<String,String>> insertUnknownAccounts(Map<Integer,String> mapProviderID, Map<String,List<Account>> mapUnknownAccounts)
    {
        List<Account> lstAccts = new List<Account>();
        Map<Integer,String> mapAccountIds = new Map<Integer,String>();
        Map<String,Map<String,String>> finalUnknownProviderMap = new Map<String,Map<String,String>>();
        Map<String,String> mapConsumers = new Map<String,String>();
        Set<ConsumerID__c> uniqueConsumerSet = new Set<ConsumerID__c>();
        if(mapUnknownAccounts!=null && !mapUnknownAccounts.isEmpty())
        {
            if(mapUnknownAccounts.get(sIntWith) != null)
            lstAccts.addAll(mapUnknownAccounts.get(sIntWith));
            if(mapUnknownAccounts.get(sIntAbout) != null)
            lstAccts.addAll(mapUnknownAccounts.get(sIntAbout));
        }
        
        Boolean isError=false;
        list<Database.saveResult> saveResult;
		//added by asish, changes insert to update to fix watson unknown provider issue.
		list<Database.UpsertResult> upsertResult;

        if(lstAccts!=null && !lstAccts.isEmpty())
        {
			
            if(Schema.sObjectType.Account.isUpdateable() && Schema.sObjectType.Account.isCreateable())
            {
            	upsertResult = Database.upsert(lstAccts, false);
        	}
			
        }
        isError = HUMExceptionHelper.processUpsertResults(upsertResult, lstAccts, 'HUMUnknownAccountController', 'processSaveResults', 'Account');
       
        if(!isError)
        {
            for(Account accountRec:lstAccts)
            {
                if(accountRec.Account_External_Id__c == null && accountRec.id!=null)
                    accountRec.Account_External_Id__c = HUMConstants__c.getInstance('UNKNOWN_PROVIDER_EXTID').Stringvalue__c + accountRec.id;
            }
			
            if (Schema.sObjectType.Account.isUpdateable()) {
            saveResult = Database.update(lstAccts, false);
            isError = HUMExceptionHelper.processSaveResults(saveResult, lstAccts, 'HUMUnknownAccountController', 'processSaveResults', 'Account');
            }
			
            for(Integer i=0;i<lstAccts.size();i++)
            {
                mapAccountIds.put(i,String.valueOf(lstAccts[i].id));    
            }
           
            if(mapProviderID !=null)
            {
                for(Integer i=0;i<mapProviderID.size();i++)
                {
                    String sProviderId = mapProviderID.get(i);
                    List<String> lstProv = sProviderId.split('-');
                    String AccountId = mapAccountIds.get(i);
                    String sIDType = lstProv[2];
                    if(finalUnknownProviderMap.containsKey(lstProv[1]))
                        finalUnknownProviderMap.get(lstProv[1]).put(lstProv[0],AccountId);
                    else
                        finalUnknownProviderMap.put(lstProv[1], new Map<String,String>{lstProv[0]=>AccountId});
                    if(sIDType == 'TaxID')
                        mapConsumers.put(AccountId,lstProv[0]);    
                }
            }
            
            for(String sAccId : mapConsumers.keySet())
            {
                String sTaxId = mapConsumers.get(sAccId);
                uniqueConsumerSet.add(assignConsumerIds(sTaxId, sAccId, GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID));
            }
            if(uniqueConsumerSet!=null)
            {
                CreateCaseRest_D_HUM.consumerLstUpsert.addAll(uniqueConsumerSet);
            }
            if(CreateCaseRest_D_HUM.consumerLstUpsert!=null && !CreateCaseRest_D_HUM.consumerLstUpsert.isEmpty())
            {
                HUMInteractionMemberDmlHelper.upsertConsumerIDofAccount(CreateCaseRest_D_HUM.consumerLstUpsert);
            }
            
            return finalUnknownProviderMap;
        }
        return null;
    }

        
   /*
    * upsertAccountRec
    * <p>
    * This method is used to upsert account records
    * @param Map<String,List<Account>> accountMap
    * @return Map<String,List<Account>>
    */  
    public static Map<String,List<Account>> upsertAccountRec(Map<String,List<Account>> accountMap)
    {
       
        Map<String,List<Account>> mapProviderAccounts = new Map<String,List<Account>>();
        List<Account> lstOrderedAccounts = new List<Account>();
        //List<Account> uniqueAccountLst = new List<Account>();
        Boolean binteractingWithPresent = false;
        Boolean bUniqueWithAbout = false;
        String sIntWithExternalId;
        //List<Account> lstInteracting = new List<Account>();
        //lstDummy = accountMap.get('Interacting_With');
        
        if(!accountMap.isEmpty()  && accountMap.get(sIntWith)!=null && !accountMap.get(sIntWith).isEmpty())
        {
            
            lstorderedAccounts.addAll(accountMap.get(sIntWith));
            binteractingWithPresent = true;
            sIntWithExternalId = lstorderedAccounts[0].Account_External_ID__c;
            
            
        }
        if(!accountMap.isEmpty() && accountMap.get(sIntAbout)!=null && !accountMap.get(sIntAbout).isEmpty())
        {
            for(Account oAccRec : accountMap.get(sIntAbout))
            {
                if(binteractingWithPresent && String.isNotBlank(sIntWithExternalId))  
                {
                    
                    if(oAccRec.Account_External_ID__c == sIntWithExternalId)
                    {
                        bUniqueWithAbout = true;   
                    }
                    else
                       lstorderedAccounts.add(oAccRec);
                }
                else
                   lstorderedAccounts.add(oAccRec);  
            }
        }
        
        lstorderedAccounts = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstorderedAccounts);
        
        if(!lstorderedAccounts.isEmpty() && lstorderedAccounts !=null)
        {
            List<Account> lstTempAccount = new List<Account>();
            if(binteractingWithPresent)
            {
                lstTempAccount.add(lstorderedAccounts[0]);
                mapProviderAccounts.put(sIntWith,lstTempAccount);    
            }
            if(!lstorderedAccounts .isEmpty())
            {
                lstTempAccount = new List<Account>();
                
                for(integer i=0;i<lstorderedAccounts.size();i++)
                {
                   
                    if(bUniqueWithAbout)
                    {
                        
                        lstTempAccount.add(lstorderedAccounts[0]);
                    }
                    else
                    {
                        lstTempAccount.add(lstorderedAccounts[i]);
                    }
                    bUniqueWithAbout = false;
                    
                }
                 mapProviderAccounts.put(sIntAbout,lstTempAccount); 
            }
        }
        
        return mapProviderAccounts;
    }

    /*
    * upsertconsumerIds
    * <p>
    * This method is used to upsert consumerid records
    * @param Map<String,List<Account>> accountMapToUpsert
    * @return Map<String,String>
    */     
    public static Map<String,String> upsertconsumerIds(Map<String,List<Account>> accountMapToUpsert)
    {
       
        try
        {
            Set<String> setUniqueExtrnalId = new Set<String>();
            Map<String,List<String>> fetchTaxIdMap = new Map<String,List<String>>();
            Map<String,List<String>> mapAllTaxIds = new Map<String,List<String>>();
            Set<ConsumerID__c> uniqueConsumerSet=new Set<ConsumerID__c>();
            for(String Interactiontype : accountMapToUpsert.keySet())
            {
                
                for(Account oAccRec : accountMapToUpsert.get(Interactiontype))
                {
                    setUniqueExtrnalId.add(oAccRec.Account_External_ID__c);
                }
          }
              fetchTaxIdMap.putAll(CaseServiceProvider_H_HUM.mapTaxIDs);              
              List<Account> lstAccount = [select id,Account_External_ID__c from Account where Account_External_ID__c in:setUniqueExtrnalId AND ETL_Record_Deleted__c = false]; 
              for(Account oAcc : lstAccount)
              {
                  for(String sAcctExtId : fetchTaxIdMap.keyset())    
                  {
                      if(oAcc.Account_External_ID__c == sAcctExtId)
                      {
                          mapAllTaxIds.put(oAcc.id,fetchTaxIdMap.get(sAcctExtId));
                      }
                  }
              }
              
              if(!mapAllTaxIds.isEmpty() && mapAllTaxIds!=null)
              {
                  for(String sAccId: mapAllTaxIds.keyset())
                  {
                      for(String sTaxId:mapAllTaxIds.get(sAccId))
                      {
                          uniqueConsumerSet.add(assignConsumerIds(sTaxId,sAccId,GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID));
                      }
                  }
              }
           
            if(uniqueConsumerSet!=null)
            {
                CreateCaseRest_D_HUM.consumerLstUpsert.addAll(uniqueConsumerSet);
            }
            if(CreateCaseRest_D_HUM.consumerLstUpsert!=null && !CreateCaseRest_D_HUM.consumerLstUpsert.isEmpty())
            {
                HUMInteractionMemberDmlHelper.upsertConsumerIDofAccount(CreateCaseRest_D_HUM.consumerLstUpsert);
            }
            return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseServiceRest_H_HUM','upsertconsumerIds');
            return null; 
        }
    }

    /*
    * assignConsumerIds
    * <p>
    * This method is used to map instance for consumerid object
    * @param String sTaxId, String sAccountId, string sType
    * @return ConsumerID__c
    */      
     public static ConsumerID__c assignConsumerIds(String sTaxId, String sAccountId, string sType)
    {
        ConsumerID__c consumerRec = new ConsumerID__c();
        consumerRec.Account__c = sAccountId;
        consumerRec.Consumer_ID__c = sTaxId;
        consumerRec.ID_Type__c = sType;
        consumerRec.Consumer_External_ID__c = CreateCaseRest_H_HUM.getConsumerExternalID(sTaxId,sAccountId);
        return consumerRec;
    }
    
    /*
    * Method name : createInteractingMembers
    * Description : This method will be used create Interacting Members for interaction.
    * Return Type : boolean
    * Parameter   : list<Interaction_Member__c> lstInteractionMembers , String Id
    */    
    @TestVisible
    public static boolean createInteractingMembers(list<Interaction_Member__c> lstInteractionMembers, String intId)
    {
      boolean bIsIntMemberError = false;
	  
      for (Interaction_Member__c omem: lstInteractionMembers)
            {
              omem.Interaction__c =intId;
            }
            
            if(!lstInteractionMembers.isEmpty())
            {
			
              if(Schema.sObjectType.Interaction_Member__c.isCreateable())
              {
              Database.SaveResult[] lstResultIntMembers = Database.Insert(lstInteractionMembers, false); 
              bIsIntMemberError = HUMExceptionHelper.processSaveResults(lstResultIntMembers, lstInteractionMembers, 'CreateInteractionWithCaseRest_D_HUM', 'insertInteractionAndIntMembers', 'Interaction_Member__c');
              }
			  
            }   
      return bIsIntMemberError;
    }
    
    /*
    * Method name : createCaseInteractionRecords
    * Description : This method will be used create case interaction records for associating case to interaction.
    * Return Type : List<String>
    * Parameter   : List<CaseDetailResponse_DTO_V4_HUM> , String 
    */    
    @TestVisible
    public static List<String> createCaseInteractionRecords(List<CaseDetailResponse_DTO_V4_HUM> lstCaseResponses, String intId, list<case> lstCases)
    {
      List<Case_Interaction__c> lstCaseInt = new list<Case_Interaction__c>();
      list<String> lstCaseNumbers = new list<String>();
      if(lstCaseResponses!=null & !lstCaseResponses.isEmpty())
      {
        
        for(CaseDetailResponse_DTO_V4_HUM oCaseDTO: lstCaseResponses)
        {
           if(String.isNotBlank(oCaseDTO.caseId) && String.isNotBlank(intId))
           {
               Case_Interaction__c oCaseInt = new Case_Interaction__c();
               oCaseInt.Case__c =  oCaseDTO.caseId;
               oCaseInt.Interaction__c = intId;
               lstCaseInt.add(oCaseInt);
               lstCaseNumbers.add(oCaseDTO.CaseNumber);
           }
        }
      }
      if(lstCases!=null & !lstCases.isEmpty())
      {       
        for(Case oCase : lstCases)
        {           
            Case_Interaction__c oCaseInt = new Case_Interaction__c();
            oCaseInt.Case__c =  oCase.Id;
            oCaseInt.Interaction__c = intId;
            if(oCaseInt.case__C!=null && ocaseInt.Interaction__c!=null) lstCaseInt.add(oCaseInt);
        }
      }
      if(!lstCaseInt.isEmpty() && lstCaseInt!=null) Database.SaveResult[] lstResultCaseInts = Database.Insert(lstCaseInt, false); 
      return lstCaseNumbers;
    }
   
   /*
    * Method name : getErrorLogInstance
    * Description : This method will be used retuen the response instance with error related fields.
    * Return Type : CreateInteractionResponse_DTO_HUM
    * Parameter   : String , String , Boolean
    */        
    public static CreateInteractionResponse_DTO_HUM getErrorLogInstance(String sErrorCode,String sErrorMessage,Boolean sSuccess )
    {    
        CreateInteractionResponse_DTO_HUM  intResponseObj = new CreateInteractionResponse_DTO_HUM();
        CreateInteractionResponse_DTO_HUM.CreateInteractionInfo interactionInfo = new CreateInteractionResponse_DTO_HUM.CreateInteractionInfo();
        interactionInfo.Success = sSuccess;
        interactionInfo.ErrorMessage =sErrorMessage;
        interactionInfo.ErrorCode = sErrorCode;   
        intResponseObj.CreateInteractionInfo = interactionInfo;
        return intResponseObj;
    } 
    
    /*
    * Method name : getCaseErrorLogs
    * Description : This method will be used get Case Error Logs 
    * Return Type : List<CaseDetailResponse_DTO_V4_HUM>
    * Parameter   : List<CaseDetailResponse_DTO_V4_HUM>  
    */    
    @TestVisible
    public static List<CaseDetailResponse_DTO_V4_HUM> getCaseErrorLogs(List<CaseDetailResponse_DTO_V4_HUM> lstCaseResponses)
    {
      List<CaseDetailResponse_DTO_V4_HUM> lstCaseErrorLogs = new List<CaseDetailResponse_DTO_V4_HUM>();
      if(lstCaseResponses!=null & !lstCaseResponses.isEmpty())
      {
        for(CaseDetailResponse_DTO_V4_HUM oCaseDTO: lstCaseResponses)
        {
           if(!oCaseDTO.Success) lstCaseErrorLogs.add(oCaseDTO);
        }
      } 
      return lstCaseErrorLogs;
    }   


 /*
    * Method name : getUknownMemberMaps 
    * Description :Thi method is to call create unknown member accounts method and return the final map
    * Return Type : map<String,map<String,String>>
    * Parameter   : map<string,List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>>
    */
     Public static map<String,map<String,String>> getUknownMemberMaps(map<string,List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>> mapIntWithAboutUnknownCriteria)
     {
     
      map<String,String> unknownAccountWithMap = new map<String,String>();
      map<String,String> unknownAccountAboutMap = new map<String,String>();
      
      //final map returning for unknown account creation
      map<String,map<String,String>> FinalUnknownAccountMap = new Map<String,map<String,String>>();
     
        list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> InteractionwithvaluesforUnknown = new list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
        list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> InteractionAboutvaluesforUnknown = new list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
          
           if(!mapIntWithAboutUnknownCriteria.isEmpty()&& mapIntWithAboutUnknownCriteria.containsKey('Interacting_With'))
            {  
                InteractionwithvaluesforUnknown.addAll(mapIntWithAboutUnknownCriteria.get('Interacting_With'));
                unknownAccountWithMap = createUnknownAccounts(InteractionwithvaluesforUnknown,sIntWith);
            }
            if(!mapIntWithAboutUnknownCriteria.isEmpty()&& mapIntWithAboutUnknownCriteria.containsKey('Interacting_About'))
            {   
                InteractionAboutvaluesforUnknown.addAll(mapIntWithAboutUnknownCriteria.get('Interacting_About'));
                unknownAccountAboutMap = createUnknownAccounts (InteractionAboutvaluesforUnknown,sIntAbout);
            }
       
         if(unknownAccountWithMap!= null) FinalUnknownAccountMap.put('Interacting_With',unknownAccountWithMap );
         if(unknownAccountWithMap!= null) FinalUnknownAccountMap.put('Interacting_About',unknownAccountAboutMap); 
        
       return FinalUnknownAccountMap; 
     }
     
     /*
    * Method name : createUnknownAccounts 
    * Description :Thi method is to create unknown member accounts
    * Return Type :map<String,String>
    * Parameter   : list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> , interactingType
    */ 
   Private static map<String,String> createUnknownAccounts (list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> request, string sInteractingType)
   {
     map<String,String> mapUnknownmemAccts = new map<String,String>();   
     ID recordTypeID = mapRecordtypeInfo.containsKey('UM') ? HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(),mapRecordtypeInfo.get('UM')) : null;
     ID GrouprecordTypeID = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Group').getRecordTypeId();
     List<Account> lstunknwnAccFrmReq = new List<Account>();
     list<CreateUnknownMembers_DTO_HUM> lstunknownmemberDTO = new list<CreateUnknownMembers_DTO_HUM>();
     list<Account> lstunknwnAccounts= new list<Account>();
     Schema.sObjectField schemaExternalId = Account.Account_External_ID__c;
	 
       //looping through the list of wrapper ,checking entity type and creating  account
       for(CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intWithAboutDTO : request)
       {  
      
                Account oAccount = new Account();
                oAccount.recordTypeID=recordTypeID;
                String interactingId = '';
                String interactingIdType = '';  
          if(intWithAboutDTO != null && string.isNotBlank(intWithAboutDTO.EntityType) && string.isNotBlank(intWithAboutDTO.IDType) 
             && (intWithAboutDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM) || (intWithAboutDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM))) )               
            {            
                             if(intWithAboutDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM))
                             oAccount.recordTypeID=GrouprecordTypeID; 
                             interactingId = intWithAboutDTO.ID;
                             oAccount.firstName = intWithAboutDTO.FirstName;
                             oAccount.lastName = intWithAboutDTO.LastName;
                             oAccount.personmailingstreet = intWithAboutDTO.AddressLine1;
                             oAccount.BirthDate__c = String.isNotBlank(intWithAboutDTO.DOB)?HUMUtilityHelper.convertDateformat(intWithAboutDTO.DOB):'';
                             oAccount.personmailingcity = intWithAboutDTO.City;
                             oAccount.PersonMailingStateCode = intWithAboutDTO.State;
                             oAccount.personmailingpostalcode = intWithAboutDTO.ZipCode;
                             oAccount.General_Account__c = True;
                             oAccount.Tenant_Id__c = '00';
                             oAccount.ETL_Record_Deleted__c = false;
                             interactingIdType = intWithAboutDTO.IDType;
                             String accountExternalId = '';
                             
                            if(interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM && !String.isBlank(interactingId))
                            {
                                accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SSN + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase());                        
                            }
                            else if (interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM && !String.isBlank(interactingId))
                            {
                                accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_MEDICARE + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase()); 
                            }
                            else if (interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM && !String.isBlank(interactingId))
                            {
                                accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_ENTID + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase());                
                            }
                            else if (interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM && !String.isBlank(interactingId))
                            {
                                accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_MEMBERIDBASE + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase());                
                            }
                            else if (interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM && !String.isBlank(interactingId))
                            {
                                accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_MEDICAID + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase());                
                            }                    
                            else if (!String.isBlank(interactingId))
                            {
                                accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_UKEY + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase());
                            }                
                             else if (interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM ) 
                            {
                                accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC;                       
                            }
                            else if ((interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNMEMBER_HUM) && String.isBlank(interactingId))
                            {
                                accountExternalId = '';                       
                            }
                           oAccount.Account_External_ID__c = accountExternalId ;
                           boolean bAcctExist = false;
                           if(accountExternalId != GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC && String.isNotBlank(accountExternalId))
                            {
                              String queryAcct = 'Select Id from Account where ETL_Record_Deleted__c = false AND Account_External_ID__c = \'' + string.escapeSingleQuotes(accountExternalId ) + '\' LIMIT 1';
                              List<Account> objAcct = (List<Account>)Database.Query(queryAcct);
                               if(!objAcct.isEmpty() && objAcct[0].Id != null && String.isNotBlank(objAcct[0].Id))
                                    { 
                                         CreateUnknownMembers_DTO_HUM unknownmemberDTO = new CreateUnknownMembers_DTO_HUM();   
                                         unknownmemberDTO.MemberInteractingId = interactingId;
                                         unknownmemberDTO.AccId = objAcct[0].Id;
                                         unknownmemberDTO.MemberInteractingIdType = interactingIdType;
                                         unknownmemberDTO.AccntextrnlID = accountExternalId ;
                                         lstunknownmemberDTO.add(unknownmemberDTO);
                                         mapUnknownmemAccts.put(interactingId,objAcct[0].Id);
                                         bAcctExist = true;
                                    }
                            }
                              if (!bAcctExist)
                               {   
                                  CreateUnknownMembers_DTO_HUM unknownmemberDTO = new CreateUnknownMembers_DTO_HUM();
                                  lstunknwnAccounts.add(oAccount);
                                  if(string.isNotBlank(interactingId) && !interactingIdType.equalsIgnoreCase('UnknownMember'))
                                  {
                                       unknownmemberDTO.MemberInteractingId = interactingId;
                                       unknownmemberDTO.MemberInteractingIdType = interactingIdType;
                                       unknownmemberDTO.AccntextrnlID = accountExternalId ;
                                       lstunknownmemberDTO.add(unknownmemberDTO);
                                  }
                               }
                           }
      }
	  
      if(Schema.sObjectType.Account.isUpdateable() && Schema.sObjectType.Account.isCreateable())
            {
     		 Database.upsertResult[] result = Database.upsert(lstunknwnAccounts,false);
            }
		
        if(lstunknwnAccounts !=null && !lstunknwnAccounts.isEmpty())
        {
           Integer iCount=0;
           for(Account accountRec : lstunknwnAccounts)
            {
                if(String.isBlank(accountRec.Account_External_Id__c) && accountRec.id!=null) 
                {
                    accountRec.Account_External_Id__c = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC + accountRec.id;
                    lstunknwnAccFrmReq.add(accountRec); 
                    lstAcctId.add(accountRec.id); 
                    if(mapUnknwnMemberFrmService.get(sInteractingType) != null &&!mapUnknwnMemberFrmService.get(sInteractingType).isEmpty())
                    mapUnknwnMemberFrmService.get(sInteractingType).put('UnknownMbr'+iCount,accountRec.Id); 
                    else {
                    mapUnknwnMemberFrmService.put(sInteractingType,new map<string,string>{'UnknownMbr'+iCount=>accountRec.Id});
                    }
                }
            }
      }
      if(lstunknwnAccFrmReq != null && !lstunknwnAccFrmReq.isEmpty())
      {
           list<Database.saveResult> saveResult = Database.update(lstunknwnAccFrmReq, false);
      }
     for(Account oAcc : lstunknwnAccounts)
       {
         for (CreateUnknownMembers_DTO_HUM unknownDTOlst : lstunknownmemberDTO)
         {
          if (unknownDTOlst.AccntextrnlID  == oAcc.Account_External_ID__c) 
           unknownDTOlst.AccId = oAcc.Id  ;
           mapUnknownmemAccts.put(unknownDTOlst.MemberInteractingId,unknownDTOlst.AccId);
         }
       }
       if(lstunknownmemberDTO!=null)
           insertConsumerTaxIds(lstunknownmemberDTO);
       return mapUnknownmemAccts;
      
      }  
      
    /*
    * Method Name   :    insertConsumerTaxIds
    * Description   :    Insert the Consumer TAXIDs based on the Account ID
    * Return Type   :    List<ConsumerId__c>
    * Parameters    :    list<CreateUnknownMembers_DTO_HUM>
    */
    @testVisible private static void insertConsumerTaxIds(list<CreateUnknownMembers_DTO_HUM> insertconsumerlst)
    {   
       try
       {
        Boolean bAsynchProcess = false;
        list<ConsumerId__c> objConsumerlst = new list<ConsumerId__c>();
		
        for(CreateUnknownMembers_DTO_HUM consumerlstDTO: insertconsumerlst)
        {
        ConsumerId__c objConsumer = new ConsumerId__c();
        String accountExternalId = HUMConstants__c.getInstance('UNKNOWN_MEMBER_EXTID').Stringvalue__c;
        if(String.isNotBlank(consumerlstDTO.MemberInteractingIdType) && consumerlstDTO.MemberInteractingIdType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM))
        {
            consumerlstDTO.MemberInteractingIdType = GLOBAL_CONSTANT_HUM.CASESERVICE_HUMANAID_HUM;
        }
        objConsumer.Consumer_Id__c = consumerlstDTO.MemberInteractingId.toUppercase();
        objConsumer.ID_Type__c = consumerlstDTO.MemberInteractingIdType;
        objConsumer.Account__c = consumerlstDTO.AccId;
        String sPipeCharacter = HUMConstants__c.getInstance('PIPE_CHARACTER').StringValue__c;
        String externalId = '';
        if(consumerlstDTO.MemberInteractingIdType== GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM)
        {
            externalId = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_SSN + consumerlstDTO.AccId;
        }
        else if(consumerlstDTO.MemberInteractingIdType== GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM)
        {
            externalId = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_MEDICARE + consumerlstDTO.AccId;
        }
        else if(consumerlstDTO.MemberInteractingIdType== GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM)
        {
            externalId = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_MEDICAID + consumerlstDTO.AccId;
        }
        else
        {
            externalId = accountExternalId + consumerlstDTO.MemberInteractingIdType+ sPipeCharacter + consumerlstDTO.AccId;   
        }
        objConsumer.Consumer_External_Id__c = externalId;                                     
        objConsumerlst.add(objConsumer);
        }
        Schema.sObjectField schemaExternalId = ConsumerId__c.Consumer_External_Id__c;
		
        if(Schema.sObjectType.ConsumerId__c.isUpdateable() && Schema.sObjectType.ConsumerId__c.isCreateable()) 
        {
        Database.upsertResult[] consumerResult = Database.upsert(objConsumerlst,schemaExternalId, false);
       
        for (Database.upsertResult ur : consumerResult )
        {
         if(!ur.isSuccess())
          {
             if(bAsynchProcess)
             {
                 CreateCaseRest_D_HUM.logError(ur.getErrors(), 'insertConsumerTaxIds', 'ConsumerID__c', ur.getId());
             }
             else
             {
                 throw new HUMCustomException('Issue with Unknown Member ID Update in Consumer');
             }
         } 
       }
       }
	   
    }
    catch(exception ex)
    {
      HUMExceptionHelper.logErrors(ex,'CaseInteractionSearch_H_HUM','insertConsumerTaxIds');
    }
   }  
}