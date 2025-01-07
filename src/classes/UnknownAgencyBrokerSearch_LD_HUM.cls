/*******************************************************************************************************************************
Apex Class Name :  UnknownAgencyBrokerSearch_LD_HUM
Version: 1.0
Created On      : 07/13/2021
Function        : This class Is used to fetch results from Salesforce Database
Test Class      : AgencyBrokerSearch_LT_HUM

Modification Log:  
* Version Number             Developer Name              Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------------------------------------
*      1.0                     Sai Kumar Boga                                      07/13/2021                      Initial Version                                                                                                                       Original Version
*      2.0                     Sai Kumar Boga                                      09/02/2021                    US 2654138 Checkmark scan issue with sharing 
*      3.0                     Nilanjana Sanyal                   				   10/26/2022                    US-3813080: HUMExceptionHelper remediation for lightning consumer Management classes
****************************************************************************************************************************************************************/

Public with sharing Class UnknownAgencyBrokerSearch_LD_HUM
{

    @testvisible private static String sClassName ='unknownAgencyBrokerSearch_LD_HUM';
    @testvisible private static Boolean bAgencyTimedout = false;
    @testvisible private static String sMethodName = '';     
    @testvisible private static Boolean bIsNameSearch = false;
    @testvisible static String sPlatformSOQLQuery = ''; 
    @testvisible static String sPlatformSOSLQuery = '';
    @testvisible static String sKey = ''; 
    Public Static Map<String,List<ConsumerID__c>> mapConsumerAccount {Get;Set;}
    @testVisible private static Set<String> setConsumerIds = New Set <String>();
    @testVisible private static List<String> lstAccIds = New List<String>();
    @testVisible public static Boolean bIsUnknownProvider{get;set;} 
    @testVisible private static integer iSearchLimit = GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencyObjectSize;
    @testVisible private static transient boolean bIsAgencyError{get;set;}
    @testvisible private static List<Account> lstSObjects = New List<Account>();
    @testVisible private static List<String> lstRecordTypeId = New List<String>();
    Public Static String sLimitValue = String.valueOf(System.label.iPaginator);
    Public Static Integer limitValue = Integer.valueOf(System.label.iPaginator);
    @testVisible private static integer iCreateSearchLimit = GLOBAL_SEARCH_CONSTANT_LH_HUM.Unknown_SearchLimit;
    
    
    
    /*  
     * Method name : queryAgencyAccount
     * Description : This method will be called when integration does Not return any result to do an internal search within SFDC
     * Return Type : void
     * Parameter   : NA
     */
    
    Public Static AgencyBrokerSearch_LC_HUM.wrapperclass queryAgencyAccount(AgencyBrokerSearch_DTO_HUM oSearchWrapper)
    {
        AgencyBrokerSearch_LC_HUM.wrapperclass objWrapperclass = New AgencyBrokerSearch_LC_HUM.wrapperclass();
        Try
        {	
            
            Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
            Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
            Schema.RecordTypeInfo oAgencyRecordType =  accountRecordTypeMap.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_Agency_Account_Record_Type_Name);
            Schema.RecordTypeInfo oUnknownAgencyRecordType =  accountRecordTypeMap.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_UnknownAgency_Account_Record_Type_Name);
            lstRecordTypeId.add('\''+oAgencyRecordType.getRecordTypeId()+'\'');
            lstRecordTypeId.add('\''+oUnknownAgencyRecordType.getRecordTypeId()+'\'');
                
            sMethodName = GLOBAL_SEARCH_CONSTANT_LH_HUM.QueryAgentMethod;
                     
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
        sQry += ' ORDER BY CreatedDate DESC LIMIT '+ iCreateSearchLimit;
        
        UnknownAgencyBrokerSearch_LD_HUM.lstSObjects = (List<Account>)Database.query(sQry);
        objWrapperclass.lstSObjects = UnknownAgencyBrokerSearch_LD_HUM.lstSObjects;
         Map<String,List<string>> mapOfaccIdTaxIds = new Map<String,List<string>>();
         
         
         List<String> lstTaxIds = new List<String>();
         for(Account acc : UnknownAgencyBrokerSearch_LD_HUM.lstSObjects)
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
        lstMembers = Database.Query(sPlatformSOQLQuery);
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
                    for(ConsumerID__c objConsumer : mapConsumerAccount.get(acc.Id))
                    {
                        lstTaxIds = mapOfaccIdTaxIds.get(acc.Account_External_Id__c);
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
        
        String commonQueryConditions = '';
        
        sPlatformSOQLQuery = 'Select Id,Individual_Last_Name__c, Individual_First_Name__c, Name, Account_External_ID__c,BillingStreet, BillingCity, BillingState, BillingCountry,';
        sPlatformSOQLQuery += 'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,BillingPostalCode,ETL_Record_Deleted__c From Account Where RecordTypeId IN '+lstRecordTypeId;
       
                
        if(String.isNotBlank(oSearchWrapper.sLastName) || String.isNotBlank(oSearchWrapper.sFirstName) || String.isNotBlank(oSearchWrapper.sAgencyName))    
        {
            bIsNameSearch =true;
            if(oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue3)
            {    
                
                sKey = oSearchWrapper.sFirstName;
                sPlatformSOQLQuery = 'SELECT Id, Individual_Last_Name__c, Individual_First_Name__c, Name, Account_External_ID__c, BillingStreet, BillingCity, BillingState, BillingCountry, BillingPostalCode From Account Where Individual_First_Name__c = :sKey ORDER BY createddate DESC Limit 1';
                
            }    
            else if(oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue2)
            {
                sKey = oSearchWrapper.sAgencyName;
                sPlatformSOQLQuery = 'SELECT Id, Individual_Last_Name__c, Individual_First_Name__c, Name, Account_External_ID__c, BillingStreet, BillingCity, BillingState, BillingCountry, BillingPostalCode From Account Where Name = :sKey ORDER BY createddate DESC Limit 1';
               
                 
            }
        }
        if(String.isNotBlank(oSearchWrapper.sAgentId))
            commonQueryConditions += ' AND Agent_ID__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentId) + '\'';
        
        
        if(String.isNotBlank(oSearchWrapper.sTaxID)) 
        {
            mapconsumerAccount = searchConsumerIDObject(oSearchWrapper);
            if(!mapconsumerAccount.isEmpty()) setConsumerIds = mapconsumerAccount.keySet();
            commonQueryConditions += ' AND ID IN : setConsumerIds ';
            
        }
   
        if(!bIsNameSearch){
        sPlatformSOQLQuery += commonQueryConditions + ' ORDER BY CreatedDate DESC Limit '+ iCreateSearchLimit ;
        }
        
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
        String sKey = oSearchWrapper.sTaxID;
        String sConsumerQuery = '';
        list<ConsumerID__c> lstConsumers = new list<ConsumerID__c>();
        sConsumerQuery = 'SELECT Id, Name, Account__c, Consumer_External_ID__c, createddate, Consumer_ID__c, ID_Type__c,  ETL_Record_Deleted__c FROM ConsumerID__c where Consumer_ID__c = :sKey ORDER BY createddate DESC Limit 1';
        lstConsumers = Database.query(sConsumerQuery);
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
                        + ' AND Account__c IN : lstAccIds';
        
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
                lstTempConsumers= new List<ConsumerID__c>();
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