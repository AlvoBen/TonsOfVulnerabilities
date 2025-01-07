/*******************************************************************************************************************************
Apex Class Name :  UnknownProviderSearch_LD_HUM
Version: 1.0
Created On      : 06/14/2021
Function        : TData layer class for ProviderSearch_LC_HUM 
Test Class      : ProviderSearch_LT_HUM
        Modification Log
Developer Name           Code Review                      Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Saikumatr Boga                                         06/14/2021                Intial Version
*********************************************************************************************************************************************************************************************/
Public without sharing Class UnknownProviderSearch_LD_HUM {
    

    Public Static String sPlatformSOSLQuery;
    Public Static map<String,list<ConsumerID__c>> mapAconsumerAccount;
    Public Static set<String> setAccIds ;
    Public Static String sPlatformSoftPhoneSOQLQuery;
    Public Static String sPlatformSOQLQuery;
    @testVisible private static Set<String> setConsumerIds = New Set <String>();
    @testVisible private static integer iSearchLimit = HUMConstants.HUMConstantOne;
    Public Static String sProviderRecordTypeId = NULL;
    
    /**
    * Method to search in SFDC when integration does ot return any result.
    * 
    * @param    None
    * @return   void 
    */
    Public Static list<Account> requestListAccountUsingSFDC(HUMProviderSearchWrapperHelper oSearchWrapper) 
    {
        
        list<Account> lstMembers = new list<Account>();
        createPlatformQueries(oSearchWrapper);
        lstMembers = Database.Query(sPlatformSOQLQuery);
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
    * This method will be used search consumer id objet
    * 
    * @param    None
    * @return   Object 
    */
    private static void queryConsumerIDObject()
    {
        String sConsumerQuery = '';
        list<ConsumerID__c> lstConsumers ;
        sConsumerQuery = 'Select Account__c,Consumer_ID__c FROM ConsumerID__c WHERE ' ;
        sConsumerQuery += 'ID_Type__c = \'' + string.escapeSingleQuotes(Label.HumServiceTaxIDtag) + '\' AND ETL_Record_Deleted__c = false '+
            + ' AND Account__c IN : setAccIds';
        
        lstConsumers = Database.Query(sConsumerQuery);
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
                lstTempConsumers= new list<ConsumerID__c>();
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
    * This method will be used search consumer id objet
    * 
    * @param    None
    * @return   Object 
    */
    
    private static void searchConsumerIDObject(HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        String sKey = oSearchWrapper.sTaxID;
        String sConsumerQuery = '';
        list<ConsumerID__c> lstConsumers = new list<ConsumerID__c>();
        sConsumerQuery = 'SELECT Id, Name, Account__c, Consumer_External_ID__c, createddate, Consumer_ID__c, ID_Type__c,  ETL_Record_Deleted__c FROM ConsumerID__c where Consumer_ID__c = :sKey ORDER BY createddate DESC Limit 1';
        lstConsumers = Database.query(sConsumerQuery);
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
            'ShippingStreet, ShippingCity, shippingPostalCode, createddate, ShippingStatecode, Taxonomy_Code__c, Phone_Ext__c, phone,'+
            'Enterprise_Id__c, Provider_Classification__c, DBA__c, Individual_First_Name__c, Individual_Last_Name__c, Degree__c, Source_Platform_Code__c,'+
            ' NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c From Account Where (RecordType.DeveloperName ='+'\''+ HUMConstants.HumUnknownProviderRecordType+'\')';
        
        sPlatformSoftPhoneSOQLQuery = 'Select Id, Name, RecordType.Name,Description,Primary_Specialty__c, Birthdate__c, Gender__c,'+
            'BillingPostalCode, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet, BillingCity,'+
            'ShippingStreet, ShippingCity, shippingPostalCode, createddate, ShippingStatecode, Taxonomy_Code__c, Phone_Ext__c, phone,'+
            'Enterprise_Id__c, Provider_Classification__c, DBA__c, Individual_First_Name__c, Individual_Last_Name__c, Degree__c, Source_Platform_Code__c,'+
            'NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c From Account Where (RecordType.DeveloperName ='+'\''+ HUMConstants.HumUnknownProviderRecordType+'\')';
        
       mapAconsumerAccount = new map<String,list<ConsumerID__c>>();

        if(String.isNotBlank(oSearchWrapper.sNPI))  
        {
            sPlatformSOQLQuery += ' AND NPI_ID__c = \'' + oSearchWrapper.sNPI.trim()+ '\' '; 
            sPlatformSoftPhoneSOQLQuery += ' AND NPI_ID__c= \'' + oSearchWrapper.sNPI.trim()+ '\' '; 
            sPlatformSOSLQuery += ' AND NPI_ID__c= \'' + oSearchWrapper.sNPI.trim()+ '\' '; 
        }
        
        if(String.IsNotBlank(oSearchWrapper.sTaxID))
        {
            searchConsumerIDObject(oSearchWrapper);
            if(!mapAconsumerAccount.isEmpty()) setConsumerIds = mapAconsumerAccount.keySet();
            sPlatformSOQLQuery += ' AND ID IN : setConsumerIds ';
            sPlatformSoftPhoneSOQLQuery += ' AND ID IN : setConsumerIds ';    
        }
        
        sPlatformSOQLQuery += ' ORDER BY RecordType.Name, createddate DESC Limit '+iSearchLimit;
        sPlatformSOSLQuery += ' ORDER BY RecordType.Name, createddate DESC Limit '+iSearchLimit;
        sPlatformSOSLQuery += ' )';
        sPlatformSoftPhoneSOQLQuery += ' ORDER BY RecordType.Name, createddate DESC Limit '+iSearchLimit;
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