/*******************************************************************************************************************************
Apex Class Name : ProviderSearch_LC_HUM
Version         : 1.0
Created On      : 09/25/2020
Function        : This Class serves as a controller for provider search page. 
Test Class      : ProviderSearch_LT_HUM
Modification Log: 
Version Number                 Developer Name           Code Review                     Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*	1.0						Siddhesh Sinkar                                          09/22/2020                 created LWC compatible methods .
* 	2.0						Pradeep Dani                                             10/14/2020                 added logic for internal search
* 	3.0						Ashish Kumar                                             11/06/2020                 Refactored the code
* 	4.0						Ashish Kumar                                             03/03/2021                 Refactored the code
* 	5.0						Ankima Srivastava                                        06/11/2021                 Checkmarx change related to with sharing
* 	6.0						SaiKumar Boga                                            06/14/2021                 Added Method For Create Unknown Provider Account LWC
*	7.0						Ashish Kumar                                             01/10/2021                 Changing constants reference   
* 	8.0						Nilanjana Sanyal                       					 10/26/2022                 US-3813080: HUMExceptionHelper remediation for lightning consumer Management classes                                       
*   9.0                     SravanKumar Ch                                           10/11/2023                 US 4885646:Provider Search in Related Accounts Section           
*   10.0                    Santhi Mandava                                           03/06/2024                 User Story 5312358: T1PRJ0891742 - C01- Consumer Mgt - Ltng- VIT Remediation Search pages and unknown Group Creation    
*********************************************************************************************************************************************************************************************/


public with sharing class ProviderSearch_LC_HUM
{
    
    @testVisible
    public static String sRequestLabelPID='';
    /**
    * Method to call EPS Service for provider search
    * 
    * @param    ProviderSearch_LC_HUM.providerformData searchFormData
    * @return   Continuation 
    */
    @AuraEnabled(continuation=true cacheable=true)
    public static Continuation search(ProviderSearch_LC_HUM.providerformData searchFormData)
    {	
        Continuation oCon ;
        try 
        { 
            oCon=ProviderSearch_LS_HUM.sendProviderContinuationRequest(searchFormData);
 	
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ProviderSearch_LC_HUM', 'search');
        }
        return oCon; 
    }
    
    @AuraEnabled(cacheable=true)
    public static String searchUnknown(ProviderSearch_LC_HUM.providerformData searchFormData)
    {
        HUMProviderSearchWrapperHelper oSearchWrapper = new HUMProviderSearchWrapperHelper();
        oSearchWrapper.sTaxID = searchFormData.sTaxID;
        oSearchWrapper.sNPI = searchFormData.sNPI;
        oSearchWrapper.sFacilityName = searchFormData.sFacilityName;
        oSearchWrapper.sFirstName =  searchFormData.sFirstName;
        oSearchWrapper.sLastName = searchFormData.sLastName;
        oSearchWrapper.sState = searchFormData.sState;
        oSearchWrapper.sPostalCode = searchFormData.sPostalCode;
        oSearchWrapper.sSpeciality = searchFormData.sSpeciality;

        list<Account> lstMembers = ProviderSearch_LD_HUM.requestListAccountUsingSFDC(oSearchWrapper);
        String sResponse = JSON.serialize(ProviderSearch_LD_HUM.getProviderWrapper(lstMembers));
        return sResponse;
    }

        /**
    * Method to call createUnknownProviderSearch for UnknownProvider
    * 
    * @param    ProviderSearch_LC_HUM.providerformData searchFormData
    * @return   String 
    */
    @AuraEnabled
    public static String createUnknownProviderSearch(ProviderSearch_LC_HUM.providerformData searchFormData)        
    {
        HUMProviderSearchWrapperHelper oSearchWrapper = new HUMProviderSearchWrapperHelper();
        oSearchWrapper.sTaxID = String.IsNotBlank(searchFormData.sTaxID)? String.escapeSingleQuotes(searchFormData.sTaxID) : searchFormData.sTaxID;
        oSearchWrapper.sNPI = String.IsNotBlank(searchFormData.sNPI)? String.escapeSingleQuotes(searchFormData.sNPI) : searchFormData.sNPI;
        oSearchWrapper.sFacilityName = String.IsNotBlank(searchFormData.sFacilityName)? String.escapeSingleQuotes(searchFormData.sFacilityName) : searchFormData.sFacilityName;
        oSearchWrapper.sFirstName =  String.IsNotBlank(searchFormData.sFirstName)? String.escapeSingleQuotes(searchFormData.sFirstName) : searchFormData.sFirstName;
        oSearchWrapper.sLastName = String.IsNotBlank(searchFormData.sLastName)? String.escapeSingleQuotes(searchFormData.sLastName) : searchFormData.sLastName;
        oSearchWrapper.sState = String.IsNotBlank(searchFormData.sState)? String.escapeSingleQuotes(searchFormData.sState) : searchFormData.sState;
        oSearchWrapper.sPostalCode = String.IsNotBlank(searchFormData.sPostalCode)? String.escapeSingleQuotes(searchFormData.sPostalCode) : searchFormData.sPostalCode; 
        oSearchWrapper.sSpeciality = String.IsNotBlank(searchFormData.sSpeciality)? String.escapeSingleQuotes(searchFormData.sSpeciality) : searchFormData.sSpeciality;
        list<Account> lstMembers = UnknownProviderSearch_LD_HUM.requestListAccountUsingSFDC(oSearchWrapper);
        String sResponse = JSON.serialize(UnknownProviderSearch_LD_HUM.getProviderWrapper(lstMembers));
        return sResponse;
    }
    /**
    * Callback method for EPSS response
    * 
    * @param    List<String> labels, Object state
    * @return   String 
    */
    @AuraEnabled
    public static String processResponse(List<String> labels, Object state)
    {   
        String sResponse;
        try
        {
            sResponse=ProviderSearch_LS_HUM.requestProcessResponse(labels,state);
        }       
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ProviderSearch_LC_HUM', 'processResponse');
        }
        return sResponse;
    }
    
    /**
    * Method to Insert provider Account from EPSS service
    * @param    ConsumerIds,accountJson,externalId
    * @return   AccountId 
    */
    @AuraEnabled
    public static String insertProviderAccount(String consumerIds,String accountJson , String externalId) 
    {    
        String sIdProvider;
        try{    
            sIdProvider=ProviderSearch_LD_HUM.insertProviderAccount(consumerIds,accountJson,externalId);
        }
        catch(Exception ex)
        {	
            HUMExceptionHelper.logErrors(ex, 'ProviderSearch_LC_HUM', 'insertProviderAccount');
        }
        return sIdProvider;
    }
    
    /**
    * Method to Insert provider Account from EPSS service for Provider Lookup
    * @param    ConsumerIds,accountJson,externalId
    * @return   Account record
    */
    @AuraEnabled
    public static Account retrieveAccountForProiderLookup(String consumerIds,String accountJson , String externalId) 
    {   
        List<Account> lstAccount;
        try{
            string sIdProvider=ProviderSearch_LD_HUM.insertProviderAccount(consumerIds,accountJson,externalId);
            lstAccount = [SELECT Id, Name From Account WHERE Id =: sIdProvider WITH SECURITY_ENFORCED];    
            
        }
        catch(Exception ex)
        {	
            HUMExceptionHelper.logErrors(ex, 'ProviderSearch_LC_HUM', 'insertAccountForProviderLookup');
        }
        return lstAccount[0];
    }      
     
    /*Method to get Interaction details 
     @param accountId 
     */
    @AuraEnabled
    public static list<Interaction__c> getInteractionList(String accountId)
    {
        list<Interaction__c> retLst;
        try
        {
            if(string.isNotBlank(accountId))
            {
                retLst = ProviderSearch_LD_HUM.getInteractionItems(accountId);
            }            
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ProviderSearch_LC_HUM', 'getInteractionList');
        }
        return retLst;
    }

    /* method to get Cases related to selected Account 
    @param accountID
    */
    @AuraEnabled
    public static list<Case> getAccountCaseList(String accountId)
    { 
        list<Case> lstCase;
        try
        {
            if(string.isNotBlank(accountId))
            {	
                List<String> caseStatus = new List<String>{'In Progress','Pending - Response','Pending - Transfer'};
                lstCase =ProviderSearch_LD_HUM.getCaseItems(accountId,caseStatus);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ProviderSearch_LC_HUM', 'getAccountCaseList');
        }
        return lstCase;
    }
 
    /**
    * Method to populate speciality dropdown on search screen
    * 
    * @param    None
    * @return   list<selectOption> 
    */
    @AuraEnabled(cacheable = true)
    public static map<String,String> getSpeciality()  
    {   
        map<string,String> listOptions = new map<String,String>();
        listOptions.put(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.NONE ,'-' +GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.NONE + '-');
        map<String, HumProviderSpecialtyMapping__c> Mapspecilaity = HumProviderSpecialtyMapping__c.getAll();  
        list<string> lstSpeciality = new list<string>(); 
        lstSpeciality.addAll(Mapspecilaity.keySet());
        lstSpeciality.sort();
        string sSpec = '';
        for (String sspeciality: lstSpeciality) 
        {
            sSpec = Mapspecilaity.get(sspeciality).Name;
            listOptions.put(sSpec,sSpec);
        }
        return listOptions ; 
    }
    /**
    * wrapper class to handle AuraEnabled Variables
    */
    public class providerformData
    {    
        @AuraEnabled
        public String sTaxID {get;set;} 
        
        @AuraEnabled
        public String sNPI {get;set;}
        
        @AuraEnabled
        public String sFacilityName {get;set;}
        
        @AuraEnabled
        public String sFirstName {get;set;}
        
        @AuraEnabled
        public String sLastName{get;set;}
        
        @AuraEnabled
        public String sState {get;set;}
        
        @AuraEnabled
        public String sPostalCode {get;set;}
        
        @AuraEnabled
        public String sSpeciality {get;set;}
        
        @AuraEnabled
        public Boolean sUnknownProviderSearch {get; set;}
    }  
}