/*******************************************************************************************************************************
Apex Class Name : HUMAgencySearchControllerV2
Version         : 2.0
Created On      : 07/31/2015
Function        : Class contains methods for search on Agents/Brokers based on the parameters passed from the visual force page
                  and passes the search results back to the visualforce page.
Test Class      : HUMAgencySearchControllerV2Test

Modification Log: 
 * Version Number             Developer Name              Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 *     2.0                    Shreya Choodamani             25261                    07/22/2015                 Original Version
 *     2.1                    Harshith Mandya               25261                    07/31/2015                 Refactoring and dpass callout
 *     2.2                    Chaitanya Vegendla            25261                    07/31/2015                 Refactoring and dpass callout
 *     2.3                    Ranjeeth Nagishetty           25519                    08/19/2015                 Incorporated Review Comments
 *     2.4                    Vishal Verma                                           09/11/2015                 Defect : 198993.0001 Assigned value to sFirstName from sAgencyName in searchAgency method for broker search. 
 *     2.5                    Prerna Prasad                 27412                    05/13/2016                 Defect : 225671.0001 Validation of Special Characters in the Agency Name 
 *     2.6                    Prerna Prasad                                          05/16/2016                 Defect : 225671.0001 Validation of Special Characters in the Agency Name adding same logic for Last Name
 *     2.7                    Prerna Prasad                 27597                    05/17/2016                 Defect#225671.0001 refactoring the searchAgency() 
 *     2.7                    Gargey Sharma                 39951                    09/19/2016                 Defect#237756.0001 Broker Search with Agent ID Is resulting in records
 *     2.7                    Shruthi Karanth               84636                    04/14/2017                 Softphone Optimization to skip DPaaS callout when coming through softphone routing
 *     2.8                    Mohammed Noor                122564                    08/23/2017                 REQ - 326313 - CA incident # 6941459 - Fix for Softphone if the caller is not authenticated.
 *     2.9                    Mohammed Noor                125478                    09/05/2017                 Fix for Defect# 313958.
 *     3.0                    Suresh Gottipati                                       07/30/2018                 Implemented platform encryption changes
 *     3.1                    Suresh Gottipati                                       12/12/2018                 Fixed Single Quotes issue                
 *     3.2                    Santhi Mandava                                         01/10/2019                 Fixed defect# 398127.0001
 *     3.3                    Deepesh Rao                                            01/29/2019                 REQ-379001 Update Account Name-Agent
 *     3.4                    Deepesh Rao                  334904                    02/06/2019                 Defect 400308-REQ - 379001_Select button not showing for some Agent IDs-Fix
 *     3.5                    Prudhvi Pamarthi                                       03/11/2019                 Defect Fix - 406765
 *     3.6                    Deepankar Binjola            341599                    03/01/2019                 REQ - 386854 - The info in the Provider And unkonwn Provider pop up are Not cleared  
 *     3.7                    Santhi Mandava               348723                    04/02/2019                 Cleanup cipher cloud references
 *     3.8                    Anjani Vaddadi                                         11/02/2021                 REQ - 1914227 - Sonar Qube Remidiation/Consumer Management (Avoid Untrusted/Un-escaped Variables in DML Query)
 *     3.9                    Ajay Chakradhar                                        05/31/2021                 REQ - 1755482 - Replacing constants to improve performance
 *     4.0                    Ajay Chakradhar										 10/13/2021			        US 2712261 : Global Const and HumConst files changes for Performance improvement 
******************************************************************************************************************************************************************************/
public with sharing class HUMAgencySearchControllerV2 extends HUMSearchComponentBaseHelperV2
{   
    //Variable to hold the class name
    private String sClassName ='HUMAgencySearchControllerV2 ';
    
    @testvisible private Boolean bAgencyTimedout = false;
    
    //Variable to hold the class name
    private String sMethodName = ''; 
    
    //Variable to hold the IsFailReqd parameter
    private boolean bIsfailreqd = false;
    
    //Private map to hold the encrypted results
    @testvisible private Map<string, string> mapEncryptedResult = new Map<String,String>();
      
    //Variable to hold the request to the webservice
    @testvisible private AsyncHUMAgencySearchService.SearchProducerResponse_elementFuture resElementFuture = NULL;
    
    //Variable to hold the details of the end point URL
    @testvisible private AsyncHUMAgencySearchService.AsyncBasicHttpBinding_IProducer requestService = NULL;
    
    //Variable to hold the response returned by the webservice
    @testvisible private HUMAgencySearchService.ProducerSearchResult resEle = NULL;
    
    @testvisible private HUMAgencySearchService.ArrayOfProducer ProducerList = NULL;
    
    @testvisible private AsyncHUMAgencySearchService.AsyncBasicHttpBinding_IProducer asyncProd = NULL;
    @testvisible private Boolean bIsNameSearch ;
    @testvisible String sPlatformSOQLQuery = '';
    @testvisible String sPlatformSoftPhoneSOQLQuery = '';
    @testvisible String sPlatformSOSLQuery = '';
    @testvisible public Map<String,List<ConsumerID__c>> mapConsumerAccount {get;set;}
    @testVisible private Set<String> setConsumerIds = new Set <String>();
    @testVisible private List<String> lstAccIds = new List<String>();
    @testVisible private Boolean isSoftPhoneSearch = false;
    @testVisible public Boolean bIsUnknownProvider{get;set;}
    @testVisible private integer iSearchLimit = 100;
    public Boolean bIsSearchPage{get;set;}
    public Boolean bIsPlatFormEncryption{get;set;}
    public String idGroup{set;get;} 
    public String mapOfAgents{set;get;}
    public String taxId{get;set;}
    public String consumerIds{get;set;}
    Private String sHUM_Agency_Account_Record_Type = 'Agent_Broker';
    Private String sHUM_UnknownAgency_Account_Record_Type = 'Unknown_Agent_Broker';
    Private Integer iListSizePageMemSearch = 10;
    Private String sHUMStateDefaultValue = 'None';
    Private Integer iHUMAgencyObjectSize = 100;
    Private Integer iHUMAgencyIntialization = 0;
    Private String sHUMSearch_returned_more_than_100_entries = 'Your search returned more than 100 entries. Please refine your search.';
    Private Integer iHUMMaxDateYear = 4000;
    Private Integer iHUMMinDateYear = 1700;
    Private String sEXT_ID_AGENT = 'AGN|00|SOLAR|';
    Private String sTENANTID_GEN = '00';
    /*
     * Method Name : HUMAgencySearchController 
     * Description : Constructor for the class 
     * Return type : NA
     * Paramater   : NA
     */
    public HUMAgencySearchControllerV2() 
    {
        bIsAgencyError = false; 
        bIsUnknownAgencySearch = false;  
        isInvalidAgencyName = false; 
        oSearchWrapper = new HUMAgencySearchWrapperHelper();   
    }

    //Boolean to check if an error thrown is for Agency search. This is used in Softphone call popup.
    public transient boolean bIsAgencyError{get;set;}
    
    //Boolean to check if only internal search is to be done
    public transient boolean bIsUnknownAgencySearch{get;set;}
    
    //Boolean to indicate that the results are from external system
    public boolean bExternalResults{get;set;}
    
    //Map to hold the TaxId
    public Map<Integer,String> mapTaxId{get;set;}
    
    //List Variable to hold the value of the account list
    public  List<Account> accList {get;set;}
    
    //AgencyWrapper variable
    public HUMAgencySearchWrapperHelper oSearchWrapper { get; set; }
    
    public List<Account> lstSObjects 
    {
        get 
        {
            if (setAccount != null)
            {
                return (List <Account> ) setAccount.getRecords();
            }
            else
            {
                return new List<Account>();
            }
        }
        set;
    }
    
    //To get the list of state values
    public List<selectOption> Agencystatevalue
    {
        get
        {
            return HUMUtilityHelper.getstateValue();
        }
        set;
    }
     
    //To capture errors
    public List<Error_Log__c> lstLogErrors
    {
        get 
        {
            if(lstLogErrors == null)
            {
                return new List<Error_Log__c>();
            }
            else
            { 
                return lstLogErrors;
            }
        }
        set;
    }
    
    public HUM_Webservice_Callout__c objWebserviceCallout = HUM_Webservice_Callout__c.getInstance('AgencySearchService');
    
    public HUM_Dpass_Callout__c objDPAASCallout = HUM_Dpass_Callout__c.getInstance('DPAAS AGENCY');
    
    public Boolean isInvalidAgencyName { get; set; }
     
    
    
    /*  
     * Method name : searchAgency
     * Description : This method will be called on clicking the search button on Agency search page.
     * Return Type : Pagereference
     * Parameter   : NA
     */ 
    public continuation searchAgency() 
    {
        bIsUnknownProvider =false; 
        bIsNameSearch =false;
        if(oSearchWrapper.sAgentType == ConsumerManagementConstants_HUM.HUMAll && String.isNotBlank(oSearchWrapper.sAgencyName)) 
        {
            oSearchWrapper.sFirstName = oSearchWrapper.sAgencyName;
        }
        else
        {
            oSearchWrapper.sFirstName = '';
        }
        Continuation oCon;
        accList = new List<Account> ();
        mapEncryptedResult = new Map<String, String>();   
        sMethodName = 'searchAgency';
        setAccount = new ApexPages.StandardSetController(new List<Account>()); 
        resElementFuture = NULL;
        
        // Creating continuation object and callback method
        Integer iTIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCallout.Timeout__c)/1000; 
        HUMAgencySearchService.Producer prod = new HUMAgencySearchService.Producer();
        
         // Flushing the data which was fetching during previous search
        if(!mapRecordData.isEmpty()){mapRecordData.clear(); }
        try
        {
            if(!validateSearchAgency())
            {
                //Validate search page for errors and warnings
                if(bIsUnknownAgencySearch != true || bIsUnknownAgencySearch == null)  
                {
                    if ( asyncProd == null ) 
                            asyncProd = new AsyncHUMAgencySearchService.AsyncBasicHttpBinding_IProducer(objWebserviceCallout.End_Point_URL__c,objWebserviceCallout.Certificate_Name__c,
                            Integer.valueOf(objWebserviceCallout.Timeout__c));
                    prod = callProdObject();  
                    
                    if(objWebserviceCallout != null)
                    {         
                        if(objWebserviceCallout.Active__c)
                        {
                            oCon = new Continuation(iTIMEOUT_INT_SECS);
                            oCon.continuationMethod = 'processResponse';
                            
                            if(!Test.isRunningTest())
                                resElementFuture = asyncProd.beginSearchProducer(oCon,prod,false);
                        }                
                    }
                }
                else
                {
                    queryAgencyAccount();
                }  
                if(lstLogErrors.size() > 0 ){ HUMExceptionHelper.saveLog(lstLogErrors,true);}   
            }
            else
            {
                return null;
            }   
        }
        catch(Exception e)
        {                        
            bIsAgencyError = true;
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors= HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
        return oCon;
    }
    
    public Continuation searchAgencySoftPhone() 
    {
        isSoftPhoneSearch = true;
        Continuation con = searchAgency();
        isSoftPhoneSearch = false;
        return con;
    }

     /*  
     * Method name : insertAgentAccount
     * Description : Insert Agent account coming from external system
     * Return Type : Void
     * Parameter   : NA
     */

    public void insertAgentAccount()
    {
        HUMSearchComponentBaseHelperV2 insertAgentAccount = new HUMSearchComponentBaseHelperV2();
        idGroup = insertAgentAccount.insertDataFromExternalSystem(mapOfAgents);
       
        HUMSearchComponentBaseHelperV2 InsertConsumerRec = new HUMSearchComponentBaseHelperV2();
        Map<String,String> mapNormalAndEncryptValues = new  Map<String,String>();
        if(String.IsNotBlank(ConsumerIds))
        {                               
            List<String> lstTaxID = ConsumerIds.split(',');
            for(integer i=0 ; i < lstTaxID.size() ; i++)
            {
                mapNormalAndEncryptValues.put(lstTaxID[i].trim(),lstTaxID[i].trim());
            }
        }
        InsertConsumerRec.upsertConsumerTaxIds(idGroup,mapNormalAndEncryptValues);  
        //Resetting the UI values after inserting the Agent from the service.
        Reset();
        
    }
    
     /*  
     * Method name : validateSearchAgency
     * Description : Validate search page for errors and warnings
     * Return Type : Void
     * Parameter   : NA
     */
    
    public Boolean validateSearchAgency()
    {
        bExternalResults=false;
        bAgencyTimedout = false;
        isInvalidAgencyName = false; 
        bIsAgencyError = false;        
        //If none of the search parameters are filled by the user, an error message is displayed to the user
        if(!HUMUtilityHelper.validateAgencySearch(oSearchWrapper.sAgencyName,oSearchWrapper.sAgentId,oSearchWrapper.sTaxId,oSearchWrapper.sLastName,oSearchWrapper.sState))
        {
            bIsAgencyError = true;
            return bIsAgencyError;
        }
        else if((HUMUtilityHelper.validateAgencyName(oSearchWrapper.sAgencyName) || HUMUtilityHelper.validateAgencyName(oSearchWrapper.sLastName)))
        {
            bIsAgencyError = true;
            isInvalidAgencyName = true;
        }        
        return bIsAgencyError;
    }
    
     /*  
     * Method name : doInternalQuery
     * Description : Query Internally and populate results
     * Return Type : Void
     * Parameter   : NA
     */
     
     private void doInternalQuery()
     {
        bIsUnknownProvider = true; 
        String sQry = 'Select Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity, BillingState, BillingCountry, BillingAddress,';
        sQry += 'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,Id,BillingPostalCode,ETL_Record_Deleted__c,(Select Consumer_ID__c from ConsumerIDs__r)'; 
        sQry += 'from Account where (RecordType.DeveloperName=' + '\'' + sHUM_Agency_Account_Record_Type + '\'';
                    sQry += ' OR RecordType.DeveloperName ='+'\''+ sHUM_UnknownAgency_Account_Record_Type+'\''+')';           
        
        
        sQry += ' AND Agent_ID__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentId) + '\'';
        If (oSearchWrapper.sAgentType == ConsumerManagementConstants_HUM.HUMAll) oSearchWrapper.sAgentType = ConsumerManagementConstants_HUM.HUMBroker;
        sQry += ' AND Agent_Type__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentType) + '\'';
        setAccount = new ApexPages.StandardSetController(Database.getQueryLocator(sQry));
        setAccount.setPageSize(Integer.valueof(iListSizePageMemSearch));
     }
    
    /*  
     * Method name : callProdObject
     * Description : This method will return the Prod object that is being passed as a parameter to the webservice callout
     * Return Type : HUMAgencySearchService.Producer
     * Parameter   : NA
     */
    private HUMAgencySearchService.Producer callProdObject()
    {
        HUMAgencySearchService.Producer prod = new HUMAgencySearchService.Producer();
            
        if(oSearchWrapper.sAgentType == ConsumerManagementConstants_HUM.HUMAll)
        {
            if(!String.isBlank(oSearchWrapper.sLastName) && !String.isBlank(oSearchWrapper.sAgencyName))
            {
                prod.Name = oSearchWrapper.sLastName + ',' + oSearchWrapper.sAgencyName;
            }
            else if(String.isBlank(oSearchWrapper.sLastName) && !String.isBlank(oSearchWrapper.sAgencyName))
            {
                prod.Name = ',' + oSearchWrapper.sAgencyName;
            }
        }
        else if(oSearchWrapper.sAgentType == ConsumerManagementConstants_HUM.HUMAgency)
        {
            prod.Name = oSearchWrapper.sAgencyName;
        }            
        prod.ProducerType = oSearchWrapper.sAgentType;
        prod.SysAssgnNbr = oSearchWrapper.sAgentId;
        prod.TaxNbr = oSearchWrapper.sTaxID;
        prod.StateCode = oSearchWrapper.sState;
        return prod;    
    }
    
    private void createPlatformQieries()
    {
        sPlatformSOQLQuery = 'Select Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity, BillingState, BillingCountry,';
            sPlatformSOQLQuery += 'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,Id,BillingPostalCode,ETL_Record_Deleted__c From Account Where (RecordType.DeveloperName='+'\''+ sHUM_Agency_Account_Record_Type+'\''+
                +' OR RecordType.DeveloperName ='+'\''+ sHUM_UnknownAgency_Account_Record_Type+'\')';
                
        sPlatformSoftPhoneSOQLQuery = 'Select Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity, BillingState, BillingCountry,';
            sPlatformSoftPhoneSOQLQuery += 'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,Id,BillingPostalCode,ETL_Record_Deleted__c From Account Where (RecordType.DeveloperName='+'\''+sHUM_Agency_Account_Record_Type+'\''+
                +' OR RecordType.DeveloperName ='+'\''+ sHUM_UnknownAgency_Account_Record_Type+'\')';
        String sKey  = '';
        If(String.isNotBlank(oSearchWrapper.sLastName) || String.isNotBlank(oSearchWrapper.sFirstName) || String.isNotBlank(oSearchWrapper.sAgencyName))    
        {
            bIsNameSearch =true;
            if(oSearchWrapper.sAgentType == ConsumerManagementConstants_HUM.HUMAll)
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
                sPlatformSOSLQuery  = 'FIND \'' + string.escapeSingleQuotes(sKey) + '\' IN ALL FIELDS RETURNING Account(Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity, BillingState, BillingCountry,'+
                +'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,Id,BillingPostalCode,ETL_Record_Deleted__c Where (RecordType.DeveloperName='+'\''+sHUM_Agency_Account_Record_Type+'\''+
                +' OR RecordType.DeveloperName ='+'\''+ sHUM_UnknownAgency_Account_Record_Type+'\')';
            }    
            else if(oSearchWrapper.sAgentType == ConsumerManagementConstants_HUM.HUMAgency)
            {
                if(String.isNotBlank(oSearchWrapper.sAgencyName)) sKey = oSearchWrapper.sAgencyName + '*';
                sPlatformSOSLQuery  = 'FIND \'' + string.escapeSingleQuotes(sKey) + '\' IN NAME FIELDS RETURNING Account(Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity, BillingState, BillingCountry,'+
                +'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,Id,BillingPostalCode,ETL_Record_Deleted__c Where (RecordType.DeveloperName='+'\''+sHUM_Agency_Account_Record_Type+'\''+
                +' OR RecordType.DeveloperName ='+'\''+ sHUM_UnknownAgency_Account_Record_Type+'\')';
            }
            If(isSoftPhoneSearch)
            {
                If(string.isNotBlank(oSearchWrapper.sLastName))
                {
                    sPlatformSoftPhoneSOQLQuery += ' AND Individual_Last_Name__c = \'' + string.escapeSingleQuotes(oSearchWrapper.sLastName) + '\'';
                    If(string.isNotBlank(oSearchWrapper.sFirstName))
                    {
                        sPlatformSoftPhoneSOQLQuery += ' AND Individual_First_Name__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sFirstName) + '\'';
                    }
                }
                else if(string.isNotBlank(oSearchWrapper.sAgencyName))
                {
                    sPlatformSoftPhoneSOQLQuery += ' AND Name = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgencyName) + '\'';
                }
            }
            sPlatformSOSLQuery  = 'FIND \'' + string.escapeSingleQuotes(sKey) + '\' IN ALL FIELDS RETURNING Account(Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity, BillingState, BillingCountry,'+
                +'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,Id,BillingPostalCode,ETL_Record_Deleted__c Where (RecordType.DeveloperName='+'\''+sHUM_Agency_Account_Record_Type+'\''+
                +' OR RecordType.DeveloperName ='+'\''+ sHUM_UnknownAgency_Account_Record_Type+'\')';
        }

        if(!String.isBlank(oSearchWrapper.sAgentType)) 
        {
            if(oSearchWrapper.sAgentType == ConsumerManagementConstants_HUM.HUMAll) oSearchWrapper.sAgentType = ConsumerManagementConstants_HUM.HUMBroker;
            sPlatformSOSLQuery += ' AND Agent_Type__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentType) + '\'';
            sPlatformSOQLQuery += ' AND Agent_Type__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentType) + '\'';
        }
        if(String.isNotBlank(oSearchWrapper.sAgentId))
        {
            sPlatformSOSLQuery += ' AND Agent_ID__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentId) + '\'';
            sPlatformSOQLQuery += ' AND Agent_ID__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentId) + '\'';
            sPlatformSoftPhoneSOQLQuery += ' AND Agent_ID__c = \'' +string.escapeSingleQuotes(oSearchWrapper.sAgentId) + '\'';
        }
        if(String.isNotBlank(oSearchWrapper.sState) && oSearchWrapper.sState != sHUMStateDefaultValue) 
        {
            sPlatformSOSLQuery += ' AND BillingStateCode = \'' +string.escapeSingleQuotes(oSearchWrapper.sState) + '\'';
            sPlatformSOQLQuery += ' AND BillingStateCode = \'' +string.escapeSingleQuotes(oSearchWrapper.sState) + '\'';
        }
        if(String.isNotBlank(oSearchWrapper.sTaxID)) 
        {
            searchConsumerIDObject();
            if(!mapconsumerAccount.isEmpty()) setConsumerIds = mapconsumerAccount.keySet();
            sPlatformSOQLQuery += ' AND ID IN : setConsumerIds ';
            sPlatformSOSLQuery += ' AND ID IN : setConsumerIds ';
            sPlatformSoftPhoneSOQLQuery += ' AND ID IN : setConsumerIds ';            
        }
        sPlatformSOSLQuery += ' Limit '+ iSearchLimit +')';
        sPlatformSOQLQuery += ' Limit '+ iSearchLimit ;
        sPlatformSoftPhoneSOQLQuery += ' Limit '+ iSearchLimit ;  
    }
    
    private void searchConsumerIDObject()
    {
        String sKey = oSearchWrapper.sTaxID+'*';
        String sConsumerQuery = '';
        List<ConsumerID__c> lstConsumers = new List<ConsumerID__c>();

        sConsumerQuery = 'FIND \'' + string.escapeSingleQuotes(sKey)  +'\' IN ALL FIELDS RETURNING ConsumerID__c (Account__c,Consumer_ID__c  Where ID_Type__c = \'' + string.escapeSingleQuotes(Label.HumServiceTaxIDtag) + '\''+' AND ETL_Record_Deleted__c = false Order by createddate desc,Account__c Limit ' +iSearchLimit +')';
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
        prepareConsumerIDMap(lstConsumers);
    }
    
     private void queryConsumerIDObject()
    {
        String sConsumerQuery = '';
        List<ConsumerID__c> lstConsumers ;
        sConsumerQuery = 'Select Account__c,Consumer_ID__c FROM ConsumerID__c WHERE ' ;
        sConsumerQuery += 'ID_Type__c = \'' + string.escapeSingleQuotes(Label.HumServiceTaxIDtag) + '\' AND ETL_Record_Deleted__c = false '+
                        + ' AND Account__c IN : lstAccIds';
        
        lstConsumers = Database.Query(sConsumerQuery );
        prepareConsumerIDMap(lstConsumers);
    }
    
   @testvisible private void prepareConsumerIDMap(List<ConsumerID__c> lstConsumers )
    {
        Integer i;
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
    }
    
   @testvisible private List<Account> FilterSOSLResults(List<Account> lstTempAccounts ,List<Account> lstMembers)
    {
        Boolean bIsMatching  = true;
        If(lstTempAccounts != Null && !lstTempAccounts.IsEmpty())
        {
            For(Account obj : lstTempAccounts)
            {
                bIsMatching = true;
                
                if(oSearchWrapper.sAgentType == ConsumerManagementConstants_HUM.HUMAgency)
                {
                    If(String.IsNotBlank(oSearchWrapper.sAgencyName) && (String.IsBlank(obj.Name) || (String.IsNotBlank(obj.Name) && !obj.Name.startsWithIgnoreCase(oSearchWrapper.sAgencyName)))) bIsMatching = false;
                }
                else
                {
                    If(String.IsNotBlank(oSearchWrapper.sFirstName) && (String.IsBlank(obj.Individual_First_Name__c) || (String.IsNotBlank(obj.Individual_First_Name__c) && !obj.Individual_First_Name__c.startsWithIgnoreCase(oSearchWrapper.sFirstName)))) bIsMatching = false;
                    If(String.IsNotBlank(oSearchWrapper.sLastName) && (String.IsBlank(obj.Individual_Last_Name__c) || (String.IsNotBlank(obj.Individual_Last_Name__c) && !obj.Individual_Last_Name__c.startsWithIgnoreCase(oSearchWrapper.sLastName)))) bIsMatching = false;
                }
                If(bIsMatching)
                {           
                    lstMembers.Add(obj);
                }
            }
        }
        return lstMembers;
    }
    
    /*
    * Method Name   :    startInternalSearch
    * Description   :    This Method is called after DPaaS callout to start the internal search
    * Return Type   :    Object
    * Parameters    :    NA
    */
    public void startInternalSearch()
    {
        string sQry = '';
        List<Id> lstAcntId = new List<Id>();
        bExternalResults = false;
        mapConsumerAccount = new Map<string,List<ConsumerID__c>>();
        lstAccIds = new List<String>();
        List<Account> lstMembers = new List<Account>();
        createPlatformQieries();     
        If(isSoftPhoneSearch)
        {
            lstMembers = Database.Query(sPlatformSoftPhoneSOQLQuery);
        }
        else if(bIsNameSearch)
        {
           List<List<sObject>> searchList = search.query(sPlatformSOSLQuery );
           lstMembers = FilterSOSLResults(((List<Account>)searchList[0]),lstMembers);
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
        queryConsumerIDObject();
        if((String.isBlank(oSearchWrapper.sTaxID)) || (!(String.isBlank(oSearchWrapper.sTaxID)) && ! lstAccIds.isEmpty())) 
        {
            setAccount = new ApexPages.StandardSetController(lstMembers);
            setAccount.setPageSize(Integer.valueof(Label.ListSizePageMemSearch));
        } 
        else 
        {
            setAccount = new ApexPages.StandardSetController(new List<Account>());
        }

        if(mapRecordData.IsEmpty() && lstSObjects.isEmpty())
        {
            throw new HUMCustomException(system.label.HUMAgentSearchNoResultFound);
        }
        if((mapRecordData != null && mapRecordData.size() > iHUMAgencyObjectSize) || lstSObjects.size() > iHUMAgencyObjectSize)
        {
            throw new HUMCustomException(sHUMSearch_returned_more_than_100_entries);
        }
        if(bAgencyTimedout)
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.EPSS_Timedout_Results_CRMS,true);
            bIsAgencyError = true;
        }
    }
    
    /*  
     * Method name : throwErrorNoResult
     * Description : This method is used to throw error message(s) on screen
     * Return Type : String
     * Parameter   : NA 
     */
    @testvisible private string throwErrorNoResult()
    {
        sMethodName = 'throwErrorNoResult';
        try
        {
            if(mapRecordData.IsEmpty() && lstSObjects.isEmpty())
            {
                throw new HUMCustomException(system.label.HUMAgentSearchNoResultFound);
            }
            
            if((mapRecordData != null && mapRecordData.size() > iHUMAgencyObjectSize) || lstSObjects.size() > iHUMAgencyObjectSize)
            {
                throw new HUMCustomException(sHUMSearch_returned_more_than_100_entries);
            }
        }
        catch(Exception e)
        {
            bIsAgencyError = true;
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
        }
        return null;
    }
    
    /*  
     * Method name : queryAgencyAccount
     * Description : This method will be called when integration does not return any result to do an internal search within SFDC
     * Return Type : Continuation
     * Parameter   : NA 
     */
    public Void queryAgencyAccount()
    {
        try
        {
            sMethodName = 'queryAgencyAccount';
            if((String.isNotBlank(oSearchWrapper.sAgentId)) && ((String.isBlank(oSearchWrapper.sLastName))) && (String.isBlank(oSearchWrapper.sAgencyName)) 
              && (oSearchWrapper.sState == sHUMStateDefaultValue) && (String.isBlank(oSearchWrapper.sTaxID)))
            {
                doInternalQuery();
                throwErrorNoResult();
            }
            else
            {
                    startInternalSearch();
            }
        }
        catch(Exception e)
        {
            bIsAgencyError = true;
            HUMExceptionHelper.bRestrictLog =true;
            lstLogErrors= HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
    }
    
    /*  
     * Method name : Reset
     * Description : This method will be called on clicking reset button on Agency Search.
     * Return Type : pagereference 
     * Parameter   : NA 
     */ 
    Public pagereference Reset()
    {
        sMethodName = 'Reset';
        bExternalResults = null;
        oSearchWrapper.sAgencyName = null;
        oSearchWrapper.sLastName = null;
        oSearchWrapper.sFirstName = null;
        oSearchWrapper.sState = null;
        oSearchWrapper.sAgentType = null;
        oSearchWrapper.sAgentId = null;
        oSearchWrapper.sTaxID = null;
        isInvalidAgencyName = false;
        setAccount = new ApexPages.StandardSetController(new List<Account>());

        if(mapRecordData != null && !mapRecordData.isEmpty()){mapRecordData.clear(); }

        return null;
    }
    
    /*  
     * Method name : callService
     * Description : This method return the response of the webservice callout
     * Return Type : HUMAgencySearchService.ProducerSearchResult 
     * Parameter   : NA 
     */ 
    public HUMAgencySearchService.ProducerSearchResult callService()
    {
        sMethodName = 'callService';
        try
        {
            if(!Test.isRunningTest())
            return resElementFuture.getValue();
        }
        catch(Exception e)
        {
            bIsAgencyError = true;
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog =true;
            lstLogErrors= HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
        return null;
    }
   
    /*  
     * Method name : processResponse
     * Description : This method is the callback method called after the execution of the web service callout
     * Return Type : NULL 
     * Parameter   : NA 
     */
    public Object processResponse() 
    {  
        sMethodName = 'processResponse';
        try
        {
            HUMAgencySearchService.ProducerSearchResult resEle;
            if(bIsUnknownAgencySearch != true) 
            {
                resEle = new HUMAgencySearchService.ProducerSearchResult();
                resEle = callService();
            }   
            
            //Setting pagination & counter for pageblock table
            iCounter = iHUMAgencyIntialization;
            iPagesize = Integer.valueof(iListSizePageMemSearch);
            
            if(resEle == null || bIsUnknownAgencySearch == true)            
            {  
                if(!(String.isBlank(oSearchWrapper.sAgentId)) && ((String.isBlank(oSearchWrapper.sLastName))) && (String.isBlank(oSearchWrapper.sAgencyName)) 
                  && (oSearchWrapper.sState == sHUMStateDefaultValue) && (String.isBlank(oSearchWrapper.sTaxID)))
                {
                    doInternalQuery();
                }
                else
                {
                    startInternalSearch();
                }      
            }                
            else            
            {                
                bExternalResults = true;
                createAccountList(resEle.ProducerList);            
            }   
                
            //Adding exceptions which are not thrown on UI into Error Log object 
            if(lstLogErrors.size() > 0) HUMExceptionHelper.saveLog(lstLogErrors,true);   
            
            //If no search results are returned both from integration and salesforce, error message is shown to the user
            if(mapRecordData.IsEmpty() && lstSObjects.isEmpty())
            {
                throw new HUMCustomException(system.label.HUMAgentSearchNoResultFound);
            }
            
            if((mapRecordData != null && mapRecordData.size() > iHUMAgencyObjectSize) || lstSObjects.size() > iHUMAgencyObjectSize)
            {
                throw new HUMCustomException(sHUMSearch_returned_more_than_100_entries);
            }
        }
        catch(CalloutException e)
        {
            bAgencyTimedout = true;
            bIsAgencyError = true;
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
        }
        catch(Exception e)
        {
            bIsAgencyError = true;
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
            return null;
        }
        return null; 
    }
    
    /*Method Name   :    createAccountList -     
     * Description   :   Create list of accounts from integration results to use in the page
     * Return Type   :   void    
     * Parameters    :   Object Type of Agency service     
     */
    public void  createAccountList(HUMAgencySearchService.ArrayOfProducer agencyInfoList)
    {
        try
        {
            sMethodName = 'createAccountList';
            Integer i=0;               
            Map<Integer,Account> mapAcc = new Map<Integer,Account>();
            Map<Integer,List<ConsumerID__c>> mapConsumerID = new Map<Integer,List<ConsumerID__c>>();
            HUMAgencySearchService.Producer agencyInfo = null;  
            Account acc = null;
            mapConsumerTaxIds = new Map<Integer,String>();  
            for(integer j = 0; j < agencyInfoList.Producer.size(); j++)
            {
                agencyInfo =  agencyInfoList.Producer[j]; 

                if(((oSearchWrapper.sAgentType == ConsumerManagementConstants_HUM.HUMAll 
                        && agencyInfo.ProducerType != ConsumerManagementConstants_HUM.HUMAgency)  
                        || oSearchWrapper.sAgentType == ConsumerManagementConstants_HUM.HUMAgency)
                        && (oSearchWrapper.sState == sHUMStateDefaultValue ||agencyInfo.StateCode == oSearchWrapper.sState))
                {
                    acc = mapAccountfields(agencyInfo);
                    if(acc != null)
                    {
                        mapConsumerTaxIds.put(i,agencyInfo.TaxNbr);                                
                        mapAcc.put(i,acc);
                        i++;
                    }
                }
            }          
            mapRecordData = mapAcc;
        }
        catch(Exception e)
        {            
            bIsAgencyError = true;
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
        } 
    }

    /*Method Name   :    mapAccountfields -     
     * Description   :   Creates an account object by mapping fields from integration result
     * Return Type   :   Account  
     * Parameters    :   HUMIntAgencySearchService.Producer     
     */
    public Account mapAccountfields(HUMAgencySearchService.Producer agencyInfo)
    {
        try
        {
            sMethodName = 'mapAccountfields';
            Date dHireDate =  ((agencyInfo.HireDate != null) && (agencyInfo.HireDate.year() > iHUMMaxDateYear) 
                                            || (agencyInfo.HireDate.year() < iHUMMinDateYear)) ? null : agencyInfo.HireDate.date();                
            Date dBaseDate =  ((agencyInfo.BaseDate != null) && (agencyInfo.BaseDate.year() > iHUMMaxDateYear) 
                                            || (agencyInfo.BaseDate.year() < iHUMMinDateYear)) ? null : agencyInfo.BaseDate.date();
            
            String sName = String.isBlank(agencyInfo.Name)? agencyInfo.FirstName + ' '+ agencyInfo.LastName : agencyInfo.Name;
            Account acc = populateAgencyInfoOnAccount(agencyInfo);           
            acc.Producer_Base_Date__c = dBaseDate;
            acc.Producer_Hire_Date__c = dHireDate;  
            acc.Name = sName;                
            return acc; 
        }
        catch(Exception e)
        {            
            bIsAgencyError = true;
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
            return null;
        }                       
    }
    
    /*Method Name   :   populateAgencyInfoOnAccount -     
    * Description   :   Populates Agency Information on Account Object
    * Return Type   :   Account  
    * Parameters    :   HUMIntAgencySearchService.Producer      
    */
    private Account populateAgencyInfoOnAccount(HUMAgencySearchService.Producer agencyInfo)
    {
        Account acc = new Account(recordTypeId = HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(),ConsumerManagementConstants_HUM.AGENTBROKER_HUM),
                                Individual_First_Name__c = agencyInfo.FirstName,Individual_Last_Name__c = agencyInfo.LastName,
                                Agent_ID__c = agencyInfo.SysAssgnNbr,
                                Agent_Type__c = agencyInfo.ProducerType == ConsumerManagementConstants_HUM.HUMAgency?agencyInfo.ProducerType:ConsumerManagementConstants_HUM.HUMBroker,
                                Phone = agencyInfo.PrimaryPhone,
                                Fax = agencyInfo.FaxNbr,Work_Email__c = agencyInfo.EmailId,
                                Producer_Termed__c = agencyInfo.IsTermed,
                                DBA__c = agencyInfo.Dba,
                                Gender__c = agencyInfo.Gender,Producer_Status__c = agencyInfo.Status,
                                Birthdate__c = agencyInfo.DateOfBirth != null ? String.ValueOf(agencyInfo.DateOfBirth.format('MM/dd/yyyy')) : null,
                                Producer_Status_Date__c = agencyInfo.StatusDate.date(),
                                Name_Suffix__c = agencyInfo.SuffixName,Middle_Name__c = agencyInfo.MiddleInitialName,
                                Enterprise_ID__c = agencyInfo.SysAssgnNbr,                    
                                Account_External_ID__c = sEXT_ID_AGENT + agencyInfo.SysAssgnNbr, 
                                BillingStreet = agencyInfo.BusinessAddress.AddressLine1, BillingCity = agencyInfo.BusinessAddress.CityName,
                                BillingPostalCode = agencyInfo.BusinessAddress.ZipCode,
                                ShippingStreet = agencyInfo.ShippingAddress.AddressLine1, ShippingCity = agencyInfo.ShippingAddress.CityName,
                                ShippingPostalCode = agencyInfo.ShippingAddress.ZipCode, ShippingStateCode = agencyInfo.ShippingAddress.StateCode,
                                BillingStateCode = agencyInfo.StateCode, General_Account__c = true, Tenant_Id__c = sTENANTID_GEN);
         accList.add(acc);
         return acc;
     }
    
     /*Method Name   :    buildUnknownAccountMap -     
     * Description   :   Builds the map which is used to display in search results once unknown account is created
     * Return Type   :   void   
     * Parameters    :   Account Id     
     */
    public void buildUnknownAccountMap(Id iAccntId)
    {
        try
        {           
            sMethodName = 'buildUnknownAccountMap';
            bIsUnknownProvider =true; 
            bExternalResults = false;
            String sQry = 'Select Individual_Last_Name__c,Individual_First_Name__c,Name,BillingStreet, BillingCity,BillingPostalCode,ETL_Record_Deleted__c, '+
                        + 'RecordType.Name,BillingState, BillingCountry, BillingAddress,Phone,Agent_ID__c,id,Agent_Type__c,'+
                        + '(Select Consumer_ID__c from ConsumerIDs__r) from Account where Id=:iAccntId LIMIT 1';
            
            setAccount = new ApexPages.StandardSetController(Database.getQueryLocator(sQry));
            setAccount.setPageSize(Integer.valueof(iListSizePageMemSearch));            
        }
        catch(Exception e)
        {            
            bIsAgencyError = true;
            HUMExceptionHelper.logErrors(e, sClassName, sMethodName);
        } 
    }
    
    /*Method Name    : fieldCipherMapping
     * Description   :  This Method is called when search member is driven by Softphone 
     *                  and as input this method accept Wrapper data and return actual fieldname and Cipher text mapping 
     * Return Type   :  Map<String, String>
     * Parameters    :  HUMMemberSearchWrapperHelper oSearchWrapperwithCipher
     */  
    @testvisible private Map<String, String> fieldCipherMapping(HUMAgencySearchWrapperHelper oSearchWrapperwithCipher)
    {
        Map<String, String> accFieldCipher = new Map<String,String>();
       
        if (NULL != oSearchWrapperwithCipher && NULL !=oSearchWrapperwithCipher.mapEncryptedValues ) {
            //Interacting With Details
            if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__C)!=null) 
                accFieldCipher.put('Consumer_ID__c' ,oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__C));
            if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('AccountName2').Value__C)!=null) 
                accFieldCipher.put('Name' ,oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('AccountName2').Value__C));
            if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__C)!=null) 
                accFieldCipher.put('Individual_Last_Name__c' ,oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__C));
            
            //Interacting About Details
            if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId').Value__C)!=null ||
            oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('AccountName').Value__C)!=null ||
            oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__C)!=null)
            {
                accFieldCipher = new Map<String,String>();
                if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId').Value__C)!=null) 
                    accFieldCipher.put('Consumer_ID__c' ,oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'TaxId').Value__C));
                if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('AccountName').Value__C)!=null) 
                    accFieldCipher.put('Name' ,oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'AccountName').Value__C));
                if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__C)!=null) 
                accFieldCipher.put('Individual_Last_Name__c' ,oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'ILastName').Value__C));          
            }
        }
        return accFieldCipher;
    }  
}