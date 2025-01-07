/*******************************************************************************************************************************
Apex Class Name : CoachMemberSearch_C_HUM
Version         : 1.0
Created On      : 03/08/2021
Function        : This class is used for performing the Lightning Member Search
Test Class      : CoachMemberSearch_T_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Jasmeen Shangari                                  03/08/2021                Original Version
*    1.1           Jasmeen Shangari                                  04/16/2021                Fix for Defect-2851
*******************************************************************************************************************************************************************/
public with sharing class CoachMemberSearch_C_HUM 
{   
    public static Map<string, string> mapEncryptedResult = new Map<String,String>();
    public static Boolean memberIdSearch  = false;
    public static Boolean bIsNameSearch  = false;
    public static CoachMemberSearchWrapper_DTO_HUM oMemberSearchWrapper{get; set;}
    public static Map<Id, Boolean> mapRecordHaveAccess {get; set;}
    public static String sourcePersonId;
    public static String sEnterpriseId;

    @TestVisible private static string sSearchQuery ='';
    @TestVisible private static string sConsumerSearchQuery   ='';
    @TestVisible private static Set<ID> setAccMemberId = new Set<ID>();
    
    private static string platformNameSearchQuery  ='';
    private static string platformSearchQuery  ='';
    private static integer iSearchLimit = HUMConstants.Member_SearchLimit;
    public static List<Account> lstFinalSearchResult = new List<Account>();
    public static Map<Id, Account> mapAccounts = new Map<Id, Account>(); 
    private static Set<ID> setAccIdForGrpNumber = new Set <ID>();
    private static Set<ID> setAccIdFormemberId = new Set <ID>();
    private static Set<ID> setAccConsumerId = new Set<ID>();
    private static integer iConsumerSearchLimit = HUMConstants.Member_SearchLimit;
    @TestVisible
    private static String sRequestLabelPID='';
       
   /*
    * Method Name   :    Constructor
    * Description   :    This method is used for initializing variables
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public CoachMemberSearch_C_HUM()
    {        
         oMemberSearchWrapper = new CoachMemberSearchWrapper_DTO_HUM();
    }

    /*
    * Method Name   :    searchRecords
    * Description   :    This method is used for returning Account Records Found
    * Return Type   :    List<Account>
    * Parameters    :    CoachMemberSearchWrapper_DTO_HUM
    */
    @AuraEnabled
    public static List<Account> searchRecords(CoachMemberSearchWrapper_DTO_HUM formMemberSearchWrapper) 
    {
        List<Account> lstAccounts = new List<Account>();
        Set<Id> lstPolicyIds = new Set<Id>();
        List<Policy__c> lstPolicy = new List<Policy__c>();  
        setAccIdForGrpNumber = new Set<ID>();
        List < MemberPlan > lstPolicyMember = new List < MemberPlan >(); 
        List<Account> lstAccountMember = new List<Account>();   
        oMemberSearchWrapper = formMemberSearchWrapper;
        memberIdSearch = false; 
        mapEncryptedResult = CoachMemberSearchUtility_H_HUM.getQueryMapSoftphone(CoachMemberSearch_D_HUM.FieldCipherMaping(oMemberSearchWrapper));  
        if((String.isNotBlank(oMemberSearchWrapper.sMemberId)) && String.isBlank(oMemberSearchWrapper.sGroupNumber) && String.isBlank(oMemberSearchWrapper.sFirstName) && String.isBlank(oMemberSearchWrapper.sLastName)
           && String.isBlank(oMemberSearchWrapper.sBirthdate) && String.isBlank(oMemberSearchWrapper.sPhone) && String.isBlank(oMemberSearchWrapper.sPostalCode) 
           && (String.isBlank(oMemberSearchWrapper.sState)||(oMemberSearchWrapper.sState).equalsIgnoreCase(system.label.HUMSPhoneMappingObj_None)))  
           memberIdSearch = true;   
        buildQuery();       
        if (String.isNotBlank(oMemberSearchWrapper.sGroupNumber) && !memberIdSearch)
        {            
           String sQuery = 'Select id, Group_Number__c from Account Where ' + mapEncryptedResult.get(System.Label.HUMGroupNumber);
           lstAccounts = (List<Account>)CoachMemberSearch_D_HUM.getListOfsObjects(sQuery, NULL, NULL, NULL);           
           Set<Id> setIds = new Set<Id>();
           for(Account oacc : lstAccounts)  
           {
               setIds.add(oacc.id);
           }      
           String sQueryforPolicy = 'Select id from Policy__c where Group_Name__c in :';
           lstPolicy  = (List<Policy__c>)CoachMemberSearch_D_HUM.getListOfsObjects(sQueryforPolicy, setIds, NULL, NULL);            
           for(Policy__c oPolicy : lstPolicy)
           {
               lstPolicyIds.add(oPolicy.id);
           }
           String sQueryForMembers = 'Select MemberId FROM MemberPlan Where Policy__c in :'; 
           lstPolicyMember  = (List<MemberPlan>)CoachMemberSearch_D_HUM.getListOfsObjects(sQueryForMembers, lstPolicyIds, NULL, NULL);                     
            if (!lstPolicyMember.isEmpty())
            {
                for (MemberPlan oPolMem :lstPolicyMember)
                {
                    setAccIdForGrpNumber.add(oPolMem.MemberId);
                }
            }           
            sSearchQuery += ' AND ID IN : setAccIdForGrpNumber ';
            platformNameSearchQuery += ' AND ID IN : setAccIdForGrpNumber ';     
        }
        if(!memberIdSearch) 
        { 
            doQuery();
        }
        else
        { 
            doRelationshipQuery();
        }               
        if(oMemberSearchWrapper.sUnknownMemCheck)
        {
            CoachMemberSearch_D_HUM.searchConsumer(oMemberSearchWrapper,lstFinalSearchResult,memberIdSearch,sConsumerSearchQuery,bIsNameSearch,mapEncryptedResult);
        }
        return lstFinalSearchResult;
    } 
    /*
    * Method Name   :    buildQuery
    * Description   :    This method is used for buildQuery
    * Return Type   :    void
    * Parameters    :    NA
    */  
    @testvisible private static void buildQuery()
    {
        bIsNameSearch =false;              
        Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
        Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
        Schema.RecordTypeInfo oMemberRecordType =  accountRecordTypeMap.get(HUMConstants.HumMemberRecordType);
        Schema.RecordTypeInfo oUnknownMemberRecordType =  accountRecordTypeMap.get(HUMConstants.UNKNOWN_MEMBER);
        List<String> lstRecordTypeId = new List<String>();
        String sMemberId = oMemberSearchWrapper.sMemberId + '*';
        String sNameKey ='';
        if(oMemberSearchWrapper.sUnknownMemCheck)
        {
            lstRecordTypeId = new List<String>{'\''+oUnknownMemberRecordType.getRecordTypeId()+'\''};     
        }
        else
        {  
          lstRecordTypeId = new List<String>{'\''+oMemberRecordType.getRecordTypeId()+'\'','\''+oUnknownMemberRecordType.getRecordTypeId()+'\''};     
        }
        if(!memberIdSearch)
        {              
            if(string.isNotBlank(oMemberSearchWrapper.sFirstName)) 
                sNameKey = oMemberSearchWrapper.sFirstName + '*';

            if(string.isNotBlank(oMemberSearchWrapper.sLastName))
            {
                if(String.IsNotBlank(sNameKey))
                {
                    sNameKey += ' AND '+oMemberSearchWrapper.sLastName+'*'; 
                }
                else
                {
                    sNameKey = oMemberSearchWrapper.sLastName + '*';
                }
            }
            if(String.IsNotBlank(sNameKey))
            {
                platformNameSearchQuery = 'FIND \'' + String.escapeSingleQuotes(sNameKey) +'\' IN NAME FIELDS RETURNING Account(ID, Name, FirstName, LastName, middlename,'+
                                + 'Individual_First_Name__c,Individual_Last_Name__c, RecordType.Name,Suffix, Birthdate__c,Deceased_Date__c, PersonEmail,PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState, ' +
                                + 'PersonMailingPostalCode, PersonHomePhone, Home_Office_Account__c, ETL_Record_Deleted__c ' +
                                + ' Where RecordtypeId IN ' + lstRecordTypeId ;
                
                if(string.isNotBlank(oMemberSearchWrapper.sBirthdate) && NULL != mapEncryptedResult.get(GLOBAL_CONSTANT_CH_HUM.sBirthdate))    platformNameSearchQuery += ' AND ' + mapEncryptedResult.get(GLOBAL_CONSTANT_CH_HUM.sBirthdate);
                if(String.isNotBlank(oMemberSearchWrapper.sState) && !(oMemberSearchWrapper.sState).equalsIgnoreCase(HUMConstants.HUMStateDefaultValue))    platformNameSearchQuery += ' AND PersonMailingStateCode = \'' +string.escapeSingleQuotes(oMemberSearchWrapper.sState) + '\'';
                if(String.isNotBlank(oMemberSearchWrapper.sPhone) && NULL != mapEncryptedResult.get(GLOBAL_CONSTANT_CH_HUM.sPersonHomePhone)) platformNameSearchQuery += ' AND ' + mapEncryptedResult.get(GLOBAL_CONSTANT_CH_HUM.sPersonHomePhone);
                if(String.isNotBlank(oMemberSearchWrapper.sPostalCode) && NULL != mapEncryptedResult.get(GLOBAL_CONSTANT_CH_HUM.sPersonMailingPostalCode ))    platformNameSearchQuery += ' AND ' + mapEncryptedResult.get(GLOBAL_CONSTANT_CH_HUM.sPersonMailingPostalCode);
                bIsNameSearch =true;                
                 sConsumerSearchQuery =  platformNameSearchQuery;
            }
            else{
                sSearchQuery = 'SELECT ID, Name, FirstName, LastName,middlename,Individual_First_Name__c,Individual_Last_Name__c,Suffix, RecordType.Name, Birthdate__c,Deceased_Date__c, PersonEmail, '+
                                       + 'PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState, ' +
                                       + 'PersonMailingPostalCode, PersonHomePhone, UserRecordAccess.HasReadAccess, Home_Office_Account__c, ETL_Record_Deleted__c ' +
                                       + 'From Account Where RecordtypeId IN ' + lstRecordTypeId ;
                                       
                
                 if(String.isNotBlank(oMemberSearchWrapper.sFirstName) && NULL != mapEncryptedResult.get('FirstName')) sSearchQuery += ' AND ' + mapEncryptedResult.get('FirstName');
                if(String.isNotBlank(oMemberSearchWrapper.sLastName) && NULL != mapEncryptedResult.get('LastName')) sSearchQuery += ' AND ' + mapEncryptedResult.get('LastName');
                if(string.isNotBlank(oMemberSearchWrapper.sBirthdate) && NULL != mapEncryptedResult.get('Birthdate__c'))    sSearchQuery += ' AND ' + mapEncryptedResult.get('Birthdate__c');
                if(String.isNotBlank(oMemberSearchWrapper.sState) && !(oMemberSearchWrapper.sState).equalsIgnoreCase(HUMConstants.HUMStateDefaultValue))    sSearchQuery += ' AND PersonMailingStateCode = \'' +string.escapeSingleQuotes(oMemberSearchWrapper.sState) + '\'';
                if(String.isNotBlank(oMemberSearchWrapper.sPhone) && NULL != mapEncryptedResult.get('PersonHomePhone'))    sSearchQuery += ' AND ' + mapEncryptedResult.get('PersonHomePhone');
                if(String.isNotBlank(oMemberSearchWrapper.sPostalCode) && NULL != mapEncryptedResult.get('PersonMailingPostalCode'))    sSearchQuery += ' AND ' + mapEncryptedResult.get('PersonMailingPostalCode');

                sConsumerSearchQuery =  sSearchQuery;
            }
        }
         else
        {       
            buildMemberIDQueries(lstRecordTypeId);
        }       
    }
    
    /*
    * Method Name   :    doQuery
    * Description   :    This method is used for doQuery
    * Return Type   :    void
    * Parameters    :    NA
    */ 
   @TestVisible private static void doQuery()
    {
        setAccIdFormemberId = new Set<ID>();
        if (String.isNotBlank(oMemberSearchWrapper.sMemberId)&&(NULL != mapEncryptedResult.get(GLOBAL_CONSTANT_CH_HUM.sName)))
        {
            setAccIdFormemberId = CoachMemberSearch_D_HUM.createSearchQuery(mapEncryptedResult.get(GLOBAL_CONSTANT_CH_HUM.sName),oMemberSearchWrapper); 
            sSearchQuery += ' AND ID IN : setAccIdFormemberId ';
            platformNameSearchQuery += ' AND ID IN : setAccIdFormemberId ';
        }  
        getEligibleAccountList();
    }   
     /*
     * Method Name   :  getEligibleAccountList
     * Description   :  This Method is used for returning results to Member search screen
     * Return Type   :  void
     * Parameters    :  String - Sort direction value
     */
    @testvisible private static void getEligibleAccountList()
    {   
        List<Account> lstTempAccounts;
        String sQuery = '';
        if(!memberIdSearch)
        {   
            if(bIsNameSearch)
            {  
                platformNameSearchQuery += 'Order By RecordType.Name Limit 250)';//keeping to 250 as we have name filtering
                List<List<sObject>> searchList = search.query(platformNameSearchQuery);
                lstTempAccounts = ((List<Account>)searchList[0]);
                List<Account> lstMembersFinal = new List<Account>();
                Boolean bIsMatching  = true;
                String sIndFirstName ='';
                String sIndLastName = '';
                for(Account obj : lstTempAccounts)
                {
                    bIsMatching = true;
                    sIndFirstName = obj.Individual_First_Name__c;
                    sIndLastName = obj.Individual_Last_Name__c;
                    if(String.IsNotBlank(oMemberSearchWrapper.sFirstName))
                    {
                       if(String.IsBlank(sIndFirstName) || (String.IsNotBlank(sIndFirstName) && !obj.Individual_First_Name__c.startsWithIgnoreCase(oMemberSearchWrapper.sFirstName))) bIsMatching = false;
                    }
                    if(String.IsNotBlank(oMemberSearchWrapper.sLastName))
                    {
                       if(String.IsBlank(sIndLastName) || (String.IsNotBlank(sIndLastName) && !obj.Individual_Last_Name__c.startsWithIgnoreCase(oMemberSearchWrapper.sLastName))) bIsMatching = false;
                    }
                    if(bIsMatching)
                    {                   
                        lstMembersFinal.Add(obj);
                    }
                    if(lstMembersFinal.size() == (iSearchLimit - 1 ) ) break; //break the loop if we reached UI result limit of 100.
                }
                lstFinalSearchResult = CoachMemberSearch_D_HUM.dataSecurity(lstMembersFinal);              
            }
             else
            {
                sSearchQuery += ' Order By RecordType.Name Limit ' + iSearchLimit;
                lstFinalSearchResult = CoachMemberSearch_D_HUM.dataSecurity(CoachMemberSearch_D_HUM.getListOfsObjects(sSearchQuery, NULL, setAccMemberId, setAccIdForGrpNumber));   
            }    
        }    
    }       
        
     /*
    * Method Name   :    buildMemberIDQueries
    * Description   :    This method is used to build member id queiries 
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testvisible private static void buildMemberIDQueries(List<String> lstRecordTypeId)
    {
        String sMemberId = oMemberSearchWrapper.sMemberId + '*';
        platformSearchQuery = 'FIND \'' + String.escapeSingleQuotes(sMemberId) + '\' IN ALL FIELDS '+
            'RETURNING MemberPlan(Id,MemberId,Name,Member_Dependent_Code__c,Member_Id_Base__c,member.Name,member.FirstName,member.LastName,member.middlename,member.Suffix,'+
            'member.Individual_First_Name__c,member.Individual_Last_Name__c,member.RecordTypeId,member.Birthdate__c,member.Deceased_Date__c ,'+
            'member.PersonEmail,member.PersonMailingStreet,member.PersonMailingCity,member.PersonMailingStateCode,member.PersonMailingState,'+
            'member.PersonMailingPostalCode,member.PersonHomePhone,member.Home_Office_Account__c,member.ETL_Record_Deleted__c,'+
            'member.RecordType.Name Limit 101),'+
            'Account(ID,Name, FirstName, LastName, middlename, Individual_First_Name__c,Individual_Last_Name__c,'+
            'RecordType.Name, RecordType.Id,Birthdate__c, PersonEmail, PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, '+
            'PersonMailingState, PersonMailingPostalCode, PersonHomePhone, Home_Office_Account__c, ETL_Record_Deleted__c, RecordTypeId '+
            'where RecordTypeId IN ' + lstRecordTypeId + ' AND ETL_Record_Deleted__c=false';                        
        platformSearchQuery += ' Limit ' + iSearchLimit + ')';        
    }
    
     /*
     * Method Name   :  doRelationshipQuery
     * Description   :  This Method is  used to :
                        a. Build query for fetching Account details using only MemberID
                        b. Execute DB query                        
     * Return Type   :  void
     * Parameters    :  NA
     */
    private static void doRelationshipQuery()
    {       
        List<List<sObject>> lstReturnedResults = new List<List<sObject>>();
        lstReturnedResults =  CoachMemberSearch_D_HUM.getMemberIdQueryResults(platformSearchQuery);
        List<MemberPlan> lstMemPlan = New List<MemberPlan>();
        List<Account> lstAcc = New List<Account>();
        mapAccounts = new Map<Id, Account>();
        if(!lstReturnedResults.IsEmpty())
        {
            lstMemPlan = ((List<MemberPlan>)lstReturnedResults[0]);
            if(lstReturnedResults.size() > 1) lstAcc = ((List<Account>)lstReturnedResults[1]);
        } 
        
        for(MemberPlan oMemPlan : lstMemPlan)
        {
            if(((string.isNotBlank(oMemberSearchWrapper.sSuffix) && oMemPlan.Member_Dependent_Code__c == oMemberSearchWrapper.sSuffix) || string.isBlank(oMemberSearchWrapper.sSuffix))
               && oMemPlan.Member_Id_Base__c == oMemberSearchWrapper.sMemberId )
            {
                mapAccounts.put(oMemPlan.MemberId, CoachMemberSearch_D_HUM.createAccountObj(oMemPlan));
            }
        }
        
        lstFinalSearchResult = CoachMemberSearch_D_HUM.dataSecurity(mapAccounts.values());               
    }   

      /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the MBE plus continuation request
    *
    * @return continuation object
    */
     @AuraEnabled(continuation=true cacheable=true)
    public static Continuation getInvokeMBEPlusService2(String sPIdVal)
    { 
        return CoachMemberSearch_S_HUM.getInvokeMBEPlusService2(sPIdVal);
    }   
    /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the service response
    *
    * @return Object
    */
      @AuraEnabled( continuation=true cacheable=true)
    public static Object getparseMBEPlusResponse2(List<String> labels)
    {   
        Object objectReturn;
        objectReturn = CoachMemberSearch_S_HUM.getparseMBEPlusResponse2(labels);
        sEnterpriseId = (String)objectReturn;        
        CoachMemberSearch_D_HUM.buildEnterpriseIDQuery(sEnterpriseId);        
        if(!lstFinalSearchResult.isEmpty())
            return lstFinalSearchResult;
        else
            return null;  
    }   
   /*
    * Method name : getCustomFieldsToDisplay
    * Description : This method will help determine fields that are visible to Coach for PolicyMember 
    * Return Type : 
    * Parameter   : NA
    */
    @AuraEnabled
    public static List<string> getCustomFieldsToDisplay(String sInput, String sScreenType) 
    {        
        List<String> retPSLst = GLOBAL_CONSTANT_CH_HUM.POLICY_MEMBER_FIELDS ;
        return retPSLst;
    }      
    
    /*
     * Method name : getStateValue
     * Description : This method will return the list of State values from Contact.MailingStateCode field
     * Return Type : list<selectOption>
     * Parameter   : NA
     */
    @AuraEnabled(cacheable=true)
    public static map<string,string> getStateValues()
    {        
        map<string,string> stateValuesMap = new map<string,string>();
        list<Schema.PicklistEntry> lstAllValues = Contact.fields.MailingStateCode.getDescribe().getPicklistValues();
        stateValuesMap.put(system.label.HUMSPhoneMappingObj_None, (system.label.HUMSPhoneMappingObj_Hy+ system.label.HUMSPhoneMappingObj_None+system.label.HUMSPhoneMappingObj_Hy));
        for(Schema.PicklistEntry state : lstAllValues) stateValuesMap.put(state.getLabel(),state.getValue());
        return stateValuesMap;
    }    
}