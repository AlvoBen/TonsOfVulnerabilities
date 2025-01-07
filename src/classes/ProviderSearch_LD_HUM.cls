/*******************************************************************************************************************************
Apex Class Name : ProviderSearch_LD_HUM
Version         : 1.0
Created On      : 09/25/2020
Function        : TData layer class for ProviderSearch_LC_HUM 
Test Class      : ProviderSearch_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Siddhesh Sinkar                                          09/22/2020                 created LWC compatible methods .
* Pradeep Dani                                             10/14/2020                 added logic for internal search
* Ashish Kumar                                             11/06/2020                 Refactored the code
* Ashish Kumar                                             03/03/2021                 Refactored the code
* Kajal Namdev                                             05/07/2021                 Replace HUMConstants with GLOBAL_SEARCH_CONSTANT_LH_HUM class and added limit, order by in queries
* Ashish Kumar                                             06/30/2021                 Added record type filter in getInteractions method
* Ashish Kumar                                             01/10/2021                 Changing constants reference
* Visweswararao J                                           07/20/2023                 User Story 4832620: T1PRJ1097507- NULL NULL Error when creating Interaction With Unknown Provider About Member
* Deepak khandelwal                                         09/27/2023                DF 8135 Fix
* Santhi Mandava                                          11/04/2023                   US4970205 - Unknown agent creation - Switch implemented
* Santhi Mandava                                          03/06/2024                   User Story 5312358: T1PRJ0891742 - C01- Consumer Mgt - Ltng- VIT Remediation Search pages and unknown Group Creation    
*********************************************************************************************************************************************************************************************/

public without sharing class ProviderSearch_LD_HUM 
{
    public static Boolean bIsNameSearch = false;
    public static String sPlatformSOSLQuery;
    public static map<String,list<ConsumerID__c>> mapAconsumerAccount;
    public static set<String> setAccIds ;
    public static Boolean isSoftPhoneSearch = false;
    public static String sPlatformSoftPhoneSOQLQuery;
    public static String sPlatformSOQLQuery;
    @testVisible private static Set<String> setConsumerIds = new Set <String>();
    @testVisible private static integer iSearchLimit = GLOBAL_SEARCH_CONSTANT_LH_HUM.Member_SearchLimit;
    public static String sProviderRecordTypeId = NULL;
    @testVisible private static integer iConsumerSearchLimit = GLOBAL_SEARCH_CONSTANT_LH_HUM.Member_SearchLimit;
    Public static String sLimitValue = String.valueOf(system.label.iPaginator);
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    
    /**
    * Method to search in SFDC when integration does ot return any result.
    * 
    * @param    None
    * @return   void 
    */
    public static list<Account> requestListAccountUsingSFDC(HUMProviderSearchWrapperHelper oSearchWrapper) 
    {
        list<Account> lstMembers = new list<Account>();
        createPlatformQueries(oSearchWrapper);
        if(isSoftPhoneSearch)
        {
            lstMembers = Database.Query(sPlatformSoftPhoneSOQLQuery); // NOPMD - Required restructuring of the query with global variables, which is not possible now.
        }
        else if(bIsNameSearch)
        {
            list<list<sObject>> searchList = search.query(sPlatformSOSLQuery );
            lstMembers = FilterSOSLResults(((list<Account>)searchList[0]),lstMembers, oSearchWrapper);
        }
        else
        {
            lstMembers = Database.Query(sPlatformSOQLQuery); // NOPMD - Required restructuring of the query with global variables, which is not possible now.
        }
        
        setAccIds = new Set<String>();
        mapAconsumerAccount = new map<string, list<ConsumerID__c>>();
        for(Account obj :lstMembers )
        {
            setAccIds.Add(Obj.Id);
            mapAconsumerAccount.put(Obj.Id, new list<ConsumerID__c>() );
        }
        queryConsumerIDObject();        
        return lstMembers;
    }   
    
        /**
    * This method will be used fetch interaction items in ProviderSearch_LC_HUM
    * 
    * @param    String
    * @return   List<Interaction__c> 
    */
    public static list<Interaction__c> getInteractionItems(String accountId)
    {
       
        return [select id, Name, LastModifiedDate, Interacting_With__r.Name, Interacting_With_type__c, 
        (SELECT Interacting_About__r.Name FROM Interaction_Members__r)
        FROM Interaction__c WHERE (Interacting_With__c =: accountId 
               OR Interacting_With__r.Account_External_ID__c =: accountId.escapeHtml4()) AND recordTypeId IN (select Id from RecordType where Name='CRMInteractions') ORDER BY LastModifiedDate DESC LIMIT 4];
    }

           /**
    * This method will be used fetch case items in ProviderSearch_LC_HUM
    * 
    * @param    String
    * @return   List<Case> 
    */
    public static list<Case> getCaseItems(String accountId, List<String> caseStatus)
    {
        return [SELECT id, CaseNumber, CTCI_List__r.Classification__r.Name, CTCI_List__r.Intent__r.Name,
        Status, CreatedDate FROM Case WHERE (AccountId =: accountId OR Account.Account_External_ID__c =:accountId.escapeHtml4()) AND status IN:caseStatus ORDER BY CreatedDate DESC LIMIT 4 ];
    }

    /**
    * This method will be used search consumer id objet
    * 
    * @param    None
    * @return   Object 
    */
    private static void queryConsumerIDObject()
    {
        String sConsumerQuery = '';
        string humServiceTaxIDtag = string.escapeSingleQuotes(Label.HumServiceTaxIDtag);
        list<ConsumerID__c> lstConsumers ;
        sConsumerQuery = 'Select Account__c,Consumer_ID__c FROM ConsumerID__c WHERE ID_Type__c =:humServiceTaxIDtag AND ETL_Record_Deleted__c = false AND Account__c IN : setAccIds ORDER BY CreatedDate DESC LIMIT :limitValue';        
        lstConsumers = Database.Query(sConsumerQuery); // NOPMD already fixed untrusted / unescaped variables in above line
        prepareConsumerIDMap(lstConsumers);
    }
    
    
    /**
    * This method will be used prepare a map with account id and corresponding consumer records. 
    * This map will be used to display consumer ids in case of platform encryption
    * @param    list<ConsumerID__c>
    * @return   Void
    */
    @TestVisible private static void prepareConsumerIDMap(list<ConsumerID__c> lstConsumers )
    {
        Integer i;
        if(lstConsumers != Null && !lstConsumers.IsEmpty())
        {
            list<ConsumerID__c> lstTempConsumers = new list<ConsumerID__c>();
            for(i =0 ;i<=lstConsumers.size()-1;i++)
            {
                lstTempConsumers = new list<ConsumerID__c>();
                if(mapAconsumerAccount.containskey(lstConsumers[i].Account__c))
                {
                    lstTempConsumers = mapAconsumerAccount.get(lstConsumers[i].Account__c);
                    lstTempConsumers.Add(lstConsumers[i]);                   
                }
                else
                {
                    lstTempConsumers.Add(lstConsumers[i]);
                }                     
                mapAconsumerAccount.put(lstConsumers[i].Account__c,lstTempConsumers); 
            }             
        }
    }
    
    
    /**
    * This method will be used to filter account sosl results
    * 
    * @param list<Account> SOSL results
    * @param list<Account> Filtered results
    * @return   Void
    */
    @testVisible private static list<Account> FilterSOSLResults(list<Account> lstTempAccounts ,list<Account> lstMembers, HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        Boolean bIsMatching  = true;
        Set<String> setAccIds = new set<String>();
        if(lstTempAccounts != Null && !lstTempAccounts.IsEmpty())
        {
            For(Account obj : lstTempAccounts)
            {
                bIsMatching = true;
                if(String.IsNotBlank(oSearchWrapper.sFirstName) && (String.IsBlank(obj.Individual_First_Name__c) || (String.IsNotBlank(obj.Individual_First_Name__c) && !obj.Individual_First_Name__c.startsWithIgnoreCase(oSearchWrapper.sFirstName)))) bIsMatching = false;
                if(String.IsNotBlank(oSearchWrapper.sLastName) && (String.IsBlank(obj.Individual_Last_Name__c) || (String.IsNotBlank(obj.Individual_Last_Name__c) && !obj.Individual_Last_Name__c.startsWithIgnoreCase(oSearchWrapper.sLastName)))) bIsMatching = false;
                if(String.IsNotBlank(oSearchWrapper.sFacilityName) && (String.IsBlank(obj.Name) || (String.IsNotBlank(obj.Name) && !obj.Name.startsWithIgnoreCase(oSearchWrapper.sFacilityName)))) bIsMatching = false;
                if(bIsMatching)
                {           
                    lstMembers.Add(obj);
                }
            }
        }
        return lstMembers;
    }
    
    
    private static void searchConsumerIDObject(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        String sKey = oSearchWrapper.sTaxID+'*';
        String sConsumerQuery = '';
        list<ConsumerID__c> lstConsumers = new list<ConsumerID__c>();
        
        sConsumerQuery = 'FIND \'' + String.escapeSingleQuotes(sKey)  +'\' IN ALL FIELDS RETURNING ConsumerID__c (Account__c,Consumer_ID__c  Where ID_Type__c = \'' + string.escapeSingleQuotes(Label.HumServiceTaxIDtag) + '\''+' AND ETL_Record_Deleted__c = false Order by createddate desc,Account__c Limit ' +iConsumerSearchLimit +')';
        list<list<sObject>> searchList = search.query(sConsumerQuery);
        
        list<ConsumerID__c> lstTempConsumers = ((list<ConsumerID__c>)searchList[0]);
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
        prepareConsumerIDMap(lstConsumers);
    }
    
    
    
    /**
    * Method to create queries in case of platform encryption
    * 
    * @param    None
    * @return   void 
    */
    private static void createPlatformQueries(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        sPlatformSOQLQuery = 'Select Id, Name, RecordType.Name,Description,Primary_Specialty__c, Birthdate__c, Gender__c,'+
            'BillingPostalCode, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet, BillingCity,'+
            'ShippingStreet, ShippingCity, shippingPostalCode, ShippingStatecode, Taxonomy_Code__c, Phone_Ext__c, phone,'+
            'Enterprise_Id__c, Provider_Classification__c, DBA__c, Individual_First_Name__c, Individual_Last_Name__c, Degree__c, Source_Platform_Code__c,'+
            ' NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c From Account Where (RecordType.DeveloperName='+'\''+GLOBAL_SEARCH_CONSTANT_LH_HUM.HumProviderRecordType+'\''+
            +' OR RecordType.DeveloperName ='+'\''+ GLOBAL_SEARCH_CONSTANT_LH_HUM.HumUnknownProviderRecordType+'\')';
        
        sPlatformSoftPhoneSOQLQuery = 'Select Id, Name, RecordType.Name,Description,Primary_Specialty__c, Birthdate__c, Gender__c,'+
            'BillingPostalCode, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet, BillingCity,'+
            'ShippingStreet, ShippingCity, shippingPostalCode, ShippingStatecode, Taxonomy_Code__c, Phone_Ext__c, phone,'+
            'Enterprise_Id__c, Provider_Classification__c, DBA__c, Individual_First_Name__c, Individual_Last_Name__c, Degree__c, Source_Platform_Code__c,'+
            'NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c From Account Where (RecordType.DeveloperName='+'\''+GLOBAL_SEARCH_CONSTANT_LH_HUM.HumProviderRecordType+'\''+
            +' OR RecordType.DeveloperName ='+'\''+ GLOBAL_SEARCH_CONSTANT_LH_HUM.HumUnknownProviderRecordType+'\')';
        
        mapAconsumerAccount = new map<String,list<ConsumerID__c>>();
        String sProviderClassification = '';
        String sKey  = '';
        
        if(((String.isNotBlank(oSearchWrapper.sFirstName) && String.isNotBlank(oSearchWrapper.sLastName)) 
            || String.isNotBlank(oSearchWrapper.sFacilityName)))
        {
            bIsNameSearch =true;
            if(String.isNotBlank(oSearchWrapper.sFacilityName))
            {
                sKey = oSearchWrapper.sFacilityName.trim() +'*';
                sProviderClassification  = '(Provider_Classification__c = \'' + GLOBAL_SEARCH_CONSTANT_LH_HUM.sFacilityprovider + '\' OR Provider_Classification__c ='+'\''+GLOBAL_SEARCH_CONSTANT_LH_HUM.sGroupprovider+'\''+')';
                sPlatformSoftPhoneSOQLQuery += ' AND Name = ' + string.escapeSingleQuotes(oSearchWrapper.sFacilityName) + '\' AND '+sProviderClassification; 
                
                sPlatformSOSLQuery  = 'FIND \'' + string.escapeSingleQuotes(sKey) + '\' IN NAME FIELDS RETURNING Account(Id,Name,RecordType.Name,Description,Primary_Specialty__c,'+
                    +'BillingPostalCode,BillingCity, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet, Birthdate__c, Gender__c,'+
                    'ShippingStreet, ShippingCity, shippingPostalCode, ShippingStatecode, Taxonomy_Code__c, Phone_Ext__c, phone,'+
                    'Enterprise_Id__c, Provider_Classification__c, DBA__c, Individual_First_Name__c, Individual_Last_Name__c, Degree__c, Source_Platform_Code__c,'+
                    +'NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c Where (RecordType.DeveloperName='+'\''+GLOBAL_SEARCH_CONSTANT_LH_HUM.HumProviderRecordType+'\''+
                    +' OR RecordType.DeveloperName ='+'\''+ GLOBAL_SEARCH_CONSTANT_LH_HUM.HumUnknownProviderRecordType+'\')' ;
            }
            else
            {
                sKey = oSearchWrapper.sFirstName.trim()+'* AND '+oSearchWrapper.sLastName.trim() +'*';
                sProviderClassification = 'Provider_Classification__c = \'' + GLOBAL_SEARCH_CONSTANT_LH_HUM.sIndividualprovider+ '\'';
                sPlatformSoftPhoneSOQLQuery += ' AND Individual_First_Name__c = \'' + string.escapeSingleQuotes(oSearchWrapper.sFirstName) + '\' AND Individual_Last_Name__c = \'' + string.escapeSingleQuotes(oSearchWrapper.sLastName) + '\' AND '+sProviderClassification;
                
                sPlatformSOSLQuery  = 'FIND \'' + string.escapeSingleQuotes(sKey) + '\' IN ALL FIELDS RETURNING Account(Id,Name,RecordType.Name,Description,Primary_Specialty__c,'+
                    +'BillingPostalCode,BillingCity, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet, Birthdate__c, Gender__c,'+
                    'ShippingStreet, ShippingCity, shippingPostalCode, ShippingStatecode, Taxonomy_Code__c, Phone_Ext__c, phone,'+
                    'Enterprise_Id__c, Provider_Classification__c, DBA__c, Individual_First_Name__c, Individual_Last_Name__c, Degree__c, Source_Platform_Code__c,'+
                    +'NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c Where (RecordType.DeveloperName='+'\''+GLOBAL_SEARCH_CONSTANT_LH_HUM.HumProviderRecordType+'\''+
                    +' OR RecordType.DeveloperName ='+'\''+ GLOBAL_SEARCH_CONSTANT_LH_HUM.HumUnknownProviderRecordType+'\')' ;
            }
            
            if(String.isNotBlank(sProviderClassification)) sPlatformSOSLQuery +=  ' AND '+sProviderClassification  ;
        }
        
        if(String.isNotBlank(oSearchWrapper.sNPI))  
        {
            sPlatformSOQLQuery += ' AND NPI_ID__c = \'' + String.escapeSingleQuotes(oSearchWrapper.sNPI.trim())+ '\' '; 
            sPlatformSoftPhoneSOQLQuery += ' AND NPI_ID__c= \'' + String.escapeSingleQuotes(oSearchWrapper.sNPI.trim())+ '\' '; 
            sPlatformSOSLQuery += ' AND NPI_ID__c= \'' + String.escapeSingleQuotes(oSearchWrapper.sNPI.trim())+ '\' '; 
        }
        if(String.isNotBlank(oSearchWrapper.sState) && !(oSearchWrapper.sState).equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.NONE) && oSearchWrapper.sState != '-None-')  
        {
            sPlatformSOQLQuery += ' AND BillingStateCode = \''+ String.escapeSingleQuotes(oSearchWrapper.sState) +'\'';
            sPlatformSOSLQuery += ' AND BillingStateCode =\''+ String.escapeSingleQuotes(oSearchWrapper.sState)+'\'';
        }
        if(String.isNotBlank(oSearchWrapper.sPostalCode))  
        {
            sPlatformSOQLQuery += ' AND (BillingPostalCode LIKE \'' + String.escapeSingleQuotes(oSearchWrapper.sPostalCode) +'%'+'\')';            
            sPlatformSOSLQuery += ' AND (BillingPostalCode LIKE \'' + String.escapeSingleQuotes(oSearchWrapper.sPostalCode) +'%'+'\')';            
        }
        if(String.IsNotBlank(oSearchWrapper.sTaxID))
        {
            searchConsumerIDObject(oSearchWrapper);
            if(!mapAconsumerAccount.isEmpty()) setConsumerIds = mapAconsumerAccount.keySet();
            sPlatformSOQLQuery += ' AND ID IN : setConsumerIds ';
            sPlatformSOSLQuery += ' AND ID IN : setConsumerIds ';
            sPlatformSoftPhoneSOQLQuery += ' AND ID IN : setConsumerIds ';
        }
        sPlatformSOQLQuery += ' ORDER BY RecordType.Name ASC Limit '+iSearchLimit;
        sPlatformSOSLQuery += ' ORDER BY RecordType.Name ASC Limit '+iSearchLimit;
        sPlatformSOSLQuery += ' )';
        sPlatformSoftPhoneSOQLQuery += ' ORDER BY RecordType.Name ASC Limit '+iSearchLimit;
    }
    
    /*
* Method Name : insertDataFromExternalSystem
* Description : This method will be used when data is coming from external system.
* Return type : String(ID)
* Parameter   : String  -Account Json version
*/
    public static String insertDataFromExternalSystem(Account selectedAccount) 
    {
        try 
        {   
            Account newAcc = selectedAccount;
            newAcc = getEmailForAgentAccount(newAcc);
            list<Account> lstexacc = [Select Id, Name from Account WHERE  Account_External_Id__C = :newAcc.Account_External_ID__c Limit 1];
            //Conditional Logic that checks the Group Record Type and Insert or Just return the ID          
            if (newAcc != null)
            {   

                    if (lstexacc.size() > 0)
                    {
                        return lstexacc[0].id;
                    }            
                    else if(lstexacc.size() == 0)
                    {
                    List<Account> lstAccounts= new List<Account>();
                    lstAccounts.add(newAcc);
                    if(lstAccounts.size()>0)
                    { 
                        upsert lstAccounts; 
                        if(lstAccounts!=null) return lstAccounts[0].id;
                    }
                }
            }
        } 
        catch (Exception e) 
        {   
            HUMExceptionHelper.logErrors(e,'HUMSearchComponentBaseHelper', 'insertDataFromExternalSystem');
        }
        return null;
    }
    
    /*
    * Method Name : upsertConsumerTaxIds
    * Description : Method to upsert Consumer Tax Id records for an Account.
    * Return type : boolean
    * Parameter   : ID,map<String,String>
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
            HUMExceptionHelper.logErrors(e, 'HUMSearchComponentBaseHelper', 'upsertConsumerTaxIds');
        }
        return false;  
    }
    /*
    * Method Name : getEmailForAgentAccount
    * Description : Checks if Account is Agent, if so changes Email to null if invalid
    * Return type : Account
    * Paramater   : Account
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
    * Method to Insert provider Account from EPSS service
    * @param    ConsumerIds,accountJson,externalId
    * @return   AccountId 
    */
    public static String insertProviderAccount(String consumerIds,String accountJson , String externalId)
    {
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('5161867');
        Account newAcc = (Account) JSON.Deserialize(accountJson, Account.Class);
        newAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(newAcc.RecordTypeId).getRecordTypeId();
        newAcc.Name=newAcc.Individual_First_Name__c +' '+newAcc.Individual_Last_Name__c;
        if(objSwitch?.IsON__c == true){
            newAcc.General_Account__c = true; 
            newAcc.Tenant_Id__c = HUMConstants__c.getInstance('TENANTID_GEN').StringValue__c; 
        }
        if( newAcc.Name ==null || String.isempty(newAcc.Name)){
            newAcc.Name=newAcc.DBA__c;
        }
        
        String idProvider = insertDataFromExternalSystem(newAcc);
        map<String,String> mapNormalAndEncryptValues = new  map<String,String>();
        if(String.IsNotBlank(ConsumerIds))
        {                               
            list<String> lstTaxID = ConsumerIds.split(',');
            for(String sTaxId : lstTaxID)
            {   
                if(String.isNotBlank(sTaxId))
                {
                    sTaxId= sTaxId.removeStart('[').removeStart('"').removeEnd('"').removeEnd(']').trim();
                    mapNormalAndEncryptValues.put(sTaxId,sTaxId);
                }
            }
        }
        upsertConsumerTaxIds(idProvider, mapNormalAndEncryptValues);
        list<ConsumerID__c> lstConsumerId = [select Consumer_ID__c from ConsumerID__c where Account__r.id =: idProvider ORDER BY CreatedDate DESC LIMIT :limitValue ]; 
        string  taxId = lstConsumerId.size()>0 ? lstConsumerId[0].Consumer_ID__c : '';
        //Resetting the UI values after inserting the Provider from the service.
        return idProvider;
    }
    
    /**
    * Method to assign the values and store in MAP
    * 
    * @param    map<String, String>
    * @return   map<String, String> 
    */
    @testVisible 
    public static List<HUMCustomSearchHelper> getProviderWrapper(list<Account> lstMembers)
    {
        List<HUMCustomSearchHelper> lstProviderAccounts = new List<HUMCustomSearchHelper>();
        
        for(Account oAcc : lstMembers){
            HUMCustomSearchHelper oWrap = new HUMCustomSearchHelper();
            oWrap.sMemberId = oAcc.Id;
            oWrap.sEnterpriseID = oAcc.Enterprise_Id__c;
            oWrap.sExtID = oAcc.Account_External_ID__c;
            oWrap.sDBA = String.isBlank(oAcc.DBA__c) ? oAcc.Name : oAcc.DBA__c;
            oWrap.sClassification = oAcc.Provider_Classification__c ;
            oWrap.sFirstName = oAcc.Individual_First_Name__c;
            oWrap.sLastName = oAcc.Individual_Last_Name__c;
            oWrap.sDegree = oAcc.Degree__c;
            oWrap.sPlatform = oAcc.Source_Platform_Code__c;
            oWrap.sBirthdate = oAcc.Birthdate__c ;
            oWrap.sGender = oAcc.Gender__c;
            oWrap.sAddress = oAcc.BillingStreet;
            oWrap.sPostalCode = oAcc.BillingPostalCode;
            oWrap.sState = oAcc.BillingStatecode;
            oWrap.sCity = oAcc.BillingCity;            
            oWrap.sPhone = oAcc.phone;
            oWrap.sPhoneExtn = oAcc.Phone_Ext__c;
            oWrap.sNPI = oAcc.NPI_ID__c;            
            oWrap.sSpeciality= oAcc.Description;
            oWrap.sTaxmonycode = oAcc.Taxonomy_Code__c;         
            oWrap.sServiceState = oAcc.ShippingStatecode;
            oWrap.sServicezip = oAcc.shippingPostalCode;
            oWrap.sServiceCity = oAcc.ShippingCity;
            oWrap.sServiceaddress = oAcc.ShippingStreet;
            oWrap.sPend = oAcc.RecordType.Name;
            
            set<string> setTaxIds = new set<string>();
            for(ConsumerID__c oTaxId: mapAconsumerAccount.get(oAcc.Id)){
                setTaxIds.add(oTaxId.Consumer_ID__c);
            }
            oWrap.sTaxID = string.join(new list<string>(setTaxIds), ',').removeEnd(',');  
            
            lstProviderAccounts.add(oWrap);
        }
        return lstProviderAccounts;
    }
}