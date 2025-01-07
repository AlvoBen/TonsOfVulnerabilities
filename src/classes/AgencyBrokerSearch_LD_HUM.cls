/*******************************************************************************************************************************
Apex Class Name : AgencyBrokerSearch_LD_HUM
Version         : 1.0
Created On      : 11/2/2020
Function        : This class is used to fetch results from Salesforce Database
Test Class      : 

Modification Log: 
 * Version Number             Developer Name              Code Review                Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*      1.0                      Rahul Krishan                                      	11/2/2020                     Initial Version                                                                                                                       Original Version
*      2.0                      Ashish Kumar                                       	11/24/2020                    Initial Version                                                                                                                           Original Version
*      3.0                      Kajal Namdev                                       	05/07/2021                    Replace HUMConstants with GLOBAL_SEARCH_CONSTANT_LH_HUM class and added query limit and ordr by in queries
*      4.0                      Ankima                                             	06/18/2021                    Checkmarx fix  and added query limit and ordr by in queries
*      5.0                      Nilanjana Sanyal                  					10/26/2022                    US-3813080: HUMExceptionHelper remediation for lightning consumer Management classes                                                                                                            
*      6.0                      Santhi Mandava                                      11/04/2023                    US4970205 - Unknown agent creation - Switch implemented
***************************************************************************************************************************************************************************/

public without sharing class AgencyBrokerSearch_LD_HUM
{

    @testvisible private static String sClassName ='AgencyBrokerSearch_LD_HUM';
    @testvisible private static Boolean bAgencyTimedout = false;
    @testvisible private static String sMethodName = '';     
    @testvisible private static Boolean bIsNameSearch = false;
    @testvisible static String sPlatformSOQLQuery = ''; 
    @testvisible static String sPlatformSOSLQuery = ''; 
    @testvisible public static Map<String,List<ConsumerID__c>> mapConsumerAccount {get;set;}
    @testVisible private static Set<String> setConsumerIds = new Set <String>();
    @testVisible private static List<String> lstAccIds = new List<String>();
    @testVisible public static Boolean bIsUnknownProvider{get;set;} 
    @testVisible private static integer iSearchLimit = GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencyObjectSize;
    @testVisible private static transient boolean bIsAgencyError{get;set;}
    @testvisible private static List<Account> lstSObjects = new List<Account>();
    @testVisible private static List<String> lstRecordTypeId = new List<String>();
    Public static String sLimitValue = String.valueOf(system.label.iPaginator);
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    
    
    /*  
     * Method name : queryAgencyAccount
     * Description : This method will be called when integration does not return any result to do an internal search within SFDC
     * Return Type : void
     * Parameter   : NA 
     */
    
    public static AgencyBrokerSearch_LC_HUM.wrapperclass queryAgencyAccount(AgencyBrokerSearch_DTO_HUM oSearchWrapper)
    {
        AgencyBrokerSearch_LC_HUM.wrapperclass objWrapperclass = new AgencyBrokerSearch_LC_HUM.wrapperclass();
        try
        {	
            Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
            Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
            Schema.RecordTypeInfo oAgencyRecordType =  accountRecordTypeMap.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_Agency_Account_Record_Type_Name);
            Schema.RecordTypeInfo oUnknownAgencyRecordType =  accountRecordTypeMap.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_UnknownAgency_Account_Record_Type_Name);
            lstRecordTypeId.add('\''+oAgencyRecordType.getRecordTypeId()+'\'');
            lstRecordTypeId.add('\''+oUnknownAgencyRecordType.getRecordTypeId()+'\'');
                
            sMethodName = 'queryAgencyAccount'; 
                     
            if((String.isNotBlank(oSearchWrapper.sAgentId)) && ((String.isBlank(oSearchWrapper.sLastName))) && (String.isBlank(oSearchWrapper.sAgencyName)) 
              && (oSearchWrapper.sState == '' || oSearchWrapper.sState == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMStateDefaultValue) && (String.isBlank(oSearchWrapper.sTaxID)))
            {	
                objWrapperclass = performAgentIdSearch(oSearchWrapper);
            }
           else
            {
               objWrapperclass = queryInternalAgencyAccounts(oSearchWrapper);
            }
            
            throwErrorNoResult(objWrapperclass);
            return objWrapperclass;
        }
        catch(Exception e)
        {
            bIsAgencyError = true;
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
            return Null;
        }
    }
    
    
    /*  
     * Method name : performAgentIdSearch
     * Description : Query Internally and populate results
     * Return Type : Void
     * Parameter   : NA
     */
     
     @testvisible private static AgencyBrokerSearch_LC_HUM.wrapperclass performAgentIdSearch(AgencyBrokerSearch_DTO_HUM oSearchWrapper)
     {
        AgencyBrokerSearch_LC_HUM.wrapperclass objWrapperclass = new AgencyBrokerSearch_LC_HUM.wrapperclass(); 
        bIsUnknownProvider = true;   
        String sQry = 'Select Id,Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity, BillingState, BillingCountry, BillingAddress,';
        sQry += 'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,Account_External_ID__c,BillingPostalCode,ETL_Record_Deleted__c,(Select Consumer_ID__c from ConsumerIDs__r)'; 
        sQry += 'from Account where RecordTypeId IN' + lstRecordTypeId;
        sQry += ' AND Agent_ID__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentId) + '\'';
        If (oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue1) oSearchWrapper.sAgentType = GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue3;
        sQry += ' AND Agent_Type__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentType) + '\'';   
        sQry += ' ORDER BY CreatedDate DESC LIMIT '+ sLimitValue;
        AgencyBrokerSearch_LD_HUM.lstSObjects = (List<Account>)Database.query(sQry);
        objWrapperclass.lstSObjects = AgencyBrokerSearch_LD_HUM.lstSObjects;
         Map<String,List<string>> mapOfaccIdTaxIds = new Map<String,List<string>>();
         List<String> lstTaxIds = new List<String>();
         for(Account acc : AgencyBrokerSearch_LD_HUM.lstSObjects)
            {
                    for(ConsumerID__c objConsumer : acc.ConsumerIDs__r)
                    {                       
                        if(!mapOfaccIdTaxIds.isEmpty())
                        {
                            lstTaxIds = mapOfaccIdTaxIds.get(acc.Account_External_ID__c);
                        }
                        if(lstTaxIds.isEmpty())
                        {
                            
                            mapOfaccIdTaxIds.put(acc.Account_External_ID__c,new List<String>{objConsumer.Consumer_ID__c});
                        }
                        else {
                            lstTaxIds.add(objConsumer.Consumer_ID__c);
                        }                
                    } 
            } 

         objWrapperclass.sTaxIds = JSON.serialize(mapOfaccIdTaxIds);
        return objWrapperclass;
     }
    
    /*
    * Method Name   :    queryInternalAgencyAccounts
    * Description   :    This Method is called after callout to start the internal search
    * Return Type   :    Object
    * Parameters    :    NA
    */
    
    public static AgencyBrokerSearch_LC_HUM.wrapperclass queryInternalAgencyAccounts(AgencyBrokerSearch_DTO_HUM oSearchWrapper)
    {
        AgencyBrokerSearch_LC_HUM.wrapperclass objWrapperclass = new AgencyBrokerSearch_LC_HUM.wrapperclass();
        string sQry = '';
        List<Id> lstAcntId = new List<Id>();
        mapConsumerAccount = new Map<string,List<ConsumerID__c>>();
        lstAccIds = new List<String>();
        List<Account> lstMembers = new List<Account>();
        createPlatformQueries(oSearchWrapper);
        if(bIsNameSearch) 
        {	
           list<list<sObject>> searchList = search.query(sPlatformSOSLQuery);
           lstMembers = FilterSOSLResults(((list<Account>)searchList[0]),lstMembers,oSearchWrapper);
        }
        else
        {       
            lstMembers = Database.Query(sPlatformSOQLQuery);
        }
        List<ConsumerID__c> lstTemp = new List<ConsumerID__c>();
        mapConsumerAccount = new Map<string,List<ConsumerID__c>>();
        For(Account obj :lstMembers )
        {
            lstAccIds.Add(Obj.Id);
            lstTemp = new List<ConsumerID__c>();
            mapConsumerAccount.put(Obj.Id,lstTemp );
        }                      
        mapConsumerAccount = queryConsumerIDObject();
        List<String> lstTaxIds = new List<String>();
        Map<String,List<string>> mapOfaccIdTaxIds = new Map<String,List<string>>();
        if(mapConsumerAccount != null && !mapConsumerAccount.isEmpty())
        {
            for(Account acc : lstMembers)
            {
                if(mapConsumerAccount.containskey(acc.Id))
                {
                    for(ConsumerID__c objConsumer : mapConsumerAccount.get(acc.Id))
                    {
                        lstTaxIds = mapOfaccIdTaxIds.containskey(acc.Account_External_Id__c) ? mapOfaccIdTaxIds.get(acc.Account_External_Id__c) : lstTaxIds;
                        if(lstTaxIds == null || lstTaxIds.isEmpty())
                        {
                            mapOfaccIdTaxIds.put(acc.Account_External_Id__c,new List<String>{objConsumer.Consumer_ID__c});
                        }
                        else {
                            lstTaxIds.add(objConsumer.Consumer_ID__c);
                        }                
                    }           
                }
            }
        }
        objWrapperclass.lstSObjects = lstMembers;
        objWrapperclass.sTaxIds = JSON.serialize(mapOfaccIdTaxIds);

        if(bAgencyTimedout)
        {
            bIsAgencyError = true;
        }
        
        return objWrapperclass;
    }
    
    /*  
     * Method name : createPlatformQueries
     * Description : This method will generate a SOQL and SOSL based on  parameters in the search wrapper
     * Return Type : 
     * Parameter   : 
     */
    
    @testvisible private static void createPlatformQueries(AgencyBrokerSearch_DTO_HUM oSearchWrapper)
    {	

		Map<string,List<ConsumerID__c>> mapconsumerAccount = new Map<string,List<ConsumerID__c>>();
        String sKey  = '';
        String commonQueryConditions = '';
        
        sPlatformSOQLQuery = 'Select Id,Individual_Last_Name__c, Individual_First_Name__c, Name, Account_External_ID__c,BillingStreet, BillingCity, BillingState, BillingCountry,';
        sPlatformSOQLQuery += 'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,BillingPostalCode,ETL_Record_Deleted__c From Account Where RecordTypeId IN '+lstRecordTypeId;
                
        if(String.isNotBlank(oSearchWrapper.sLastName) || String.isNotBlank(oSearchWrapper.sFirstName) || String.isNotBlank(oSearchWrapper.sAgencyName))    
        {
            bIsNameSearch =true;
            if(oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue1)
            {    
                if(string.isNotBlank(oSearchWrapper.sFirstName)) sKey = oSearchWrapper.sFirstName + '*';
                if(string.isNotBlank(oSearchWrapper.sLastName))
                {
                    If(String.IsNotBlank(sKey))
                    {
                        sKey += ' AND '+oSearchWrapper.sLastName+'*'; 
                    }
                    else
                    {
                        sKey = oSearchWrapper.sLastName + '*';
                    }
                }
                sPlatformSOSLQuery  = 'FIND \'' + string.escapeSingleQuotes(sKey) + '\' IN ALL FIELDS RETURNING Account(Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity,BillingStateCode, BillingState, BillingCountry,'+
                +'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,Id,BillingPostalCode,ETL_Record_Deleted__c,Account_External_ID__c Where (RecordType.DeveloperName='+'\''+GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_Agency_Account_Record_Type+'\''+
                +' OR RecordType.DeveloperName ='+'\''+ GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_UnknownAgency_Account_Record_Type+'\')';
            }    
            else if(oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue2)
            {
                if(String.isNotBlank(oSearchWrapper.sAgencyName)) sKey = oSearchWrapper.sAgencyName + '*';
                sPlatformSOSLQuery  = 'FIND \'' + string.escapeSingleQuotes(sKey) + '\' IN NAME FIELDS RETURNING Account(Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity,BillingStateCode, BillingState, BillingCountry,'+
                +'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,Id,BillingPostalCode,ETL_Record_Deleted__c,Account_External_ID__c Where (RecordType.DeveloperName='+'\''+GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_Agency_Account_Record_Type+'\''+
                +' OR RecordType.DeveloperName ='+'\''+ GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_UnknownAgency_Account_Record_Type+'\')';
            }
        }

        if(String.isNotBlank(oSearchWrapper.sAgentType)) 
        {
            if(oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue1)
            {
                commonQueryConditions += ' AND Agent_Type__c = \'' +GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue3 + '\'';
            }
            else
            {
                commonQueryConditions += ' AND Agent_Type__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentType) + '\'';
            }
        }
        if(String.isNotBlank(oSearchWrapper.sAgentId))
            commonQueryConditions += ' AND Agent_ID__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentId) + '\'';
        
        if(String.isNotBlank(oSearchWrapper.sState) && oSearchWrapper.sState != GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMStateDefaultValue) 
        { 
            commonQueryConditions += ' AND BillingStateCode = \'' +string.escapeSingleQuotes(oSearchWrapper.sState) + '\'';
        }
        
        if(String.isNotBlank(oSearchWrapper.sTaxID)) 
        {
            mapconsumerAccount = searchConsumerIDObject(oSearchWrapper);
            if(!mapconsumerAccount.isEmpty()) setConsumerIds = mapconsumerAccount.keySet();
            commonQueryConditions += ' AND ID IN : setConsumerIds ';       
        }
        sPlatformSOSLQuery += commonQueryConditions + ' ORDER BY CreatedDate DESC Limit '+ iSearchLimit +')';
        sPlatformSOQLQuery += commonQueryConditions + ' ORDER BY CreatedDate DESC Limit '+ iSearchLimit ;
    }
    
    /*  
     * Method name : searchConsumerIDObject
     * Description : This method queries on Consumer IDs based on Tax ID
     * Return Type : void
     * Parameter   : N/A
     */
    
    @testvisible private static Map<string,List<ConsumerID__c>> searchConsumerIDObject(AgencyBrokerSearch_DTO_HUM oSearchWrapper)
    {
		Map<string,List<ConsumerID__c>> mapconsumerAccount = new Map<string,List<ConsumerID__c>>();
        String sKey = oSearchWrapper.sTaxID+'*';
        String sConsumerQuery = '';
        List<ConsumerID__c> lstConsumers = new List<ConsumerID__c>();

        sConsumerQuery = 'FIND \'' + sKey  +'\' IN ALL FIELDS RETURNING ConsumerID__c (Account__c,Consumer_ID__c  Where ID_Type__c = \'' + string.escapeSingleQuotes(Label.HumServiceTaxIDtag) + '\''+' AND ETL_Record_Deleted__c = false Order by createddate desc,Account__c Limit ' +iSearchLimit +')';
        List<List<sObject>> searchList = search.query(sConsumerQuery);
        List<ConsumerID__c> lstTempConsumers = ((List<ConsumerID__c>)searchList[0]);
        Boolean bIsMatching  = true;
        
        For(ConsumerID__c obj : lstTempConsumers)
        {
            bIsMatching = true;
            If(!obj.Consumer_ID__c.startsWithIgnoreCase(oSearchWrapper.sTaxID)) bIsMatching = false;
            If(bIsMatching)
            {           
                lstConsumers.Add(obj);
            }
        }
        mapconsumerAccount = prepareConsumerIDMap(lstConsumers);
        return mapconsumerAccount;
    }
    
    /*  
     * Method name : queryConsumerIDObject
     * Description : This method queries on Consumer IDs based on Account IDs
     * Return Type : 
     * Parameter   : 
     */
    
    @testvisible private static Map<string,List<ConsumerID__c>> queryConsumerIDObject()
    {
        String sConsumerQuery = '';
        Map<string,List<ConsumerID__c>> mapconsumerAccount = new Map<string,List<ConsumerID__c>>();
        List<ConsumerID__c> lstConsumers ;
        sConsumerQuery = 'Select Account__c,Consumer_ID__c FROM ConsumerID__c WHERE ' ;
        sConsumerQuery += 'ID_Type__c = \'' + string.escapeSingleQuotes(Label.HumServiceTaxIDtag) + '\' AND ETL_Record_Deleted__c = false '+
                        + ' AND Account__c IN : lstAccIds ORDER BY CreatedDate DESC LIMIT '+ sLimitValue;
        
        lstConsumers = Database.Query(sConsumerQuery );
        mapconsumerAccount = prepareConsumerIDMap(lstConsumers);
        return mapconsumerAccount;
    }
    
    /*  
     * Method name : prepareConsumerIDMap
     * Description : This method prepares a map of Consumer IDs and Account IDs
     * Return Type : 
     * Parameter   : 
     */
    
    @testvisible private static Map<string,List<ConsumerID__c>> prepareConsumerIDMap(List<ConsumerID__c> lstConsumers )
    { 
        Integer i;
        Map<string,List<ConsumerID__c>> mapconsumerAccount = new Map<string,List<ConsumerID__c>>();
        if(lstConsumers != Null && !lstConsumers.IsEmpty())
        {
            List<ConsumerID__c> lstTempConsumers = new List<ConsumerID__c>();
            
            for(i =0 ;i<=lstConsumers.size()-1;i++) 
            {
                lstTempConsumers = new List<ConsumerID__c>();
                if(mapconsumerAccount.containskey(lstConsumers[i].Account__c))
                {
                    lstTempConsumers = mapconsumerAccount.get(lstConsumers[i].Account__c);
                    lstTempConsumers.Add(lstConsumers[i]);                   
                }
                else
                {
                    lstTempConsumers.Add(lstConsumers[i]);
                }                     
                mapconsumerAccount.put(lstConsumers[i].Account__c,lstTempConsumers);  
            }             
        }
        return mapconsumerAccount;
    }
   
    /*  
     * Method name : FilterSOSLResults
     * Description : This method filters the search result based on Agency Type and search conditions
     * Return Type : 
     * Parameter   : 
     */   
    @testvisible private static List<Account> FilterSOSLResults(List<Account> lstTempAccounts ,List<Account> lstMembers,AgencyBrokerSearch_DTO_HUM oSearchWrapper)
    {
        Boolean bIsMatching  = true;
        If(lstTempAccounts != Null && !lstTempAccounts.IsEmpty())
        {	
            For(Account obj : lstTempAccounts)
            {
                bIsMatching = true;
                
                if(oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue2)
                {
                    If(String.IsNotBlank(oSearchWrapper.sAgencyName) && (String.IsBlank(obj.Name) || (String.IsNotBlank(obj.Name) && !obj.Name.startsWithIgnoreCase(oSearchWrapper.sAgencyName))))
                    {	
                        bIsMatching = false;
                    }                        
                }
                else
                {
                    If(String.IsNotBlank(oSearchWrapper.sFirstName) && (String.IsBlank(obj.Individual_First_Name__c) || (String.IsNotBlank(obj.Individual_First_Name__c) && !obj.Individual_First_Name__c.startsWithIgnoreCase(oSearchWrapper.sFirstName)))) 
                    {
                        bIsMatching = false;
                    }
                    If(String.IsNotBlank(oSearchWrapper.sLastName) && (String.IsBlank(obj.Individual_Last_Name__c) || (String.IsNotBlank(obj.Individual_Last_Name__c) && !obj.Individual_Last_Name__c.startsWithIgnoreCase(oSearchWrapper.sLastName))))
                    {
                        bIsMatching = false;
                    }
                }
                If(bIsMatching)
                {           
                    lstMembers.Add(obj);
                }
            }
        }
        return lstMembers;
    }
    //upsert logic
       /**
        * Method to insertAgentAccount 
        * @param    ConsumerIds,accountJson,externalId
        * @return   AccountId 
        */
    public static String insertAgentAccount(String consumerIds,String accountJson , String externalId)
    {
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('5161867');
        Account newAcc = (Account) JSON.Deserialize(accountJson, Account.Class); 
        newAcc.Account_External_ID__c =  externalId.replace('&#124;','|');
        if(objSwitch?.IsON__c == true){
            newAcc.General_Account__c = true; 
            newAcc.Tenant_Id__c = HUMConstants__c.getInstance('TENANTID_GEN').StringValue__c;
        } 
        String idAgent = insertDataFromExternalSystem(newAcc);
        map<String,String> mapNormalAndEncryptValues = new  map<String,String>();
        if(String.IsNotBlank(ConsumerIds))
        {         
            List<String> taxIds = (List<String>)System.JSON.Deserialize(ConsumerIds, List<String>.Class); 
            for(integer i=0 ; i < taxIds.size() ; i++)
            {
                mapNormalAndEncryptValues.put(taxIds[i].trim(),taxIds[i].trim());
            }
        }
        upsertConsumerTaxIds(idAgent, mapNormalAndEncryptValues);
        list<ConsumerID__c> lstConsumerId = [select Consumer_ID__c from ConsumerID__c where Account__r.id =: idAgent ORDER BY CreatedDate DESC Limit :limitValue];     
        string  taxId = lstConsumerId.size()>0 ? lstConsumerId[0].Consumer_ID__c : '';
        return idAgent;   
    }
    /**
    * Method to insertDataFromExternalSystem 
    * @param    Account
    * @return   AccountId 
    */
    public static String insertDataFromExternalSystem(Account selectedAccount) 
    {
        String sreturnId;
        try 
        {   
            Account newAcc = selectedAccount;
            newAcc = getEmailForAgentAccount(newAcc);
            List < Account > upsertAccounts = new List < Account >();                
            upsertAccounts= HUMInteractionMemberDmlHelper.upsertAccountForInteractionID(new List<Account>{newAcc});                
            if (upsertAccounts!=null) {
                sreturnId =  upsertAccounts[0].id;
            }            
        } 
        catch (Exception e) 
        {   
            HUMExceptionHelper.logErrors(e,'AgencyBrokerSearch_LD_HUM', 'insertDataFromExternalSystem');            
        }
        return sreturnId;
    }

    /**
    * Method to getEmailForAgentAccount 
    * @param    Account
    * @return   Account 
    */
    public Static Account getEmailForAgentAccount(Account newAcc)
    {            
        if(newAcc.recordTypeId == Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgentBrokerRecordTypeName).getRecordTypeId())
        {
            if(!HUMUtilityHelper.valdiateEmail(newAcc.Work_Email__c.trim()))
            {
                newAcc.Work_Email__c = null;  
            }            
        }
        return newAcc;            
    }
    /**
    * Method to upsertConsumerTaxIds 
    * @param    Id,Map<String,String>
    * @return   boolean 
    */
    public static  boolean upsertConsumerTaxIds(ID accID,map<String,String> mapNormalAndEncryptedIds ) 
    {
        try
        {           
            if(accID !=null && mapNormalAndEncryptedIds !=null && mapNormalAndEncryptedIds.keyset().size()>0)
            {   
                list<ConsumerId__c> lstConsumerRecords = HUMUtilityHelper.getEncryptedConsumerIds(accID,mapNormalAndEncryptedIds);
                if(lstConsumerRecords!=null)
                {
                    lstConsumerRecords= HUMInteractionMemberDMLHelper.upsertConsumerIDofAccount(lstConsumerRecords);
                    if (lstConsumerRecords!=null) return true; 
                }                                           
            }
        }
        Catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'AgencyBrokerSearch_LD_HUM', 'upsertConsumerTaxIds');
        }
        return false;  
    }
    
    /*  
     * Method name : throwErrorNoResult
     * Description : This method is used to throw error message(s) on screen
     * Return Type : String
     * Parameter   : NA 
     */
    
    @testvisible private static string throwErrorNoResult(AgencyBrokerSearch_LC_HUM.wrapperclass objWrappercls)
    {
        sMethodName = 'throwErrorNoResult';
        try
        { 
            if(objWrappercls.lstSObjects.isEmpty())
            {
                throw new HUMCustomException(system.label.HUMAgentSearchNoResultFound);
            }
            
            if(objWrappercls.lstSObjects.size() > GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencyObjectSize)
            {
                throw new HUMCustomException(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMSearch_returned_more_than_100_entries);
            }
            
        }
        catch(Exception e)
        {
            bIsAgencyError = true;
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
        }
        return null;
    }
    
    
    public static List<Error_Log__c> lstLogErrors
    {
        get 
        {
            if(lstLogErrors == null)
                return new List<Error_Log__c>();
            else
                return lstLogErrors;
        }
        set;
    }
    }