/************************************************************************************************************************************************************************************
Apex Class Name : HUMProviderSearchControllerV2
Version         : 1.0
Created On      : 04/01/2014
Function        : Class contains methods for search on Accounts based on the parameters passed from the visual force page
                  and passes the search results back to the visualforce page as well as method to create a new case record.
Test Class      : HUMProviderSearchControllerV2Test

Modification Log: 
* Version   Developer Name                Code Review                        Date                       Description
*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*   2.0     Manish Kumar                  25261                              08/05/2015                 Original Version
*   2.1     Ranjeeth Nagishetty           25261                              08/05/2015                 Async calls
*   2.2     Harshith Mandya               25519                              08/19/2015                 Incorporated Review Comments
*   2.3     Ranjeeth Nagishetty           25265                              08/22/2015                 Incorporated rajasekar changes related to CA tickets
*   2.4     Sharan Shanmugam              29738                              02/05/2016                 Fixed defect #214831.0001
*   2.5     Rajesh Keswani                                                   02/12/2016                 Fixed defect #215901.0001
*   2.6     Harshith Mandya                                                  02/16/2016                 Fixed defect #216333.0001
*   2.7     Shiva Pasumarty               24637                              04/07/2016                 Fixed defect #221821.0001
*   2.8     Shiva Pasumarty               25555                              04/21/2016                 Fixed defect #221821.0001
*   2.9     Gargey Sharma                 47056                              10/27/2016                 REQ - 270080 : EPSS Conversion - SOAP to REST
*   3.0     Santhi Mandava                71723                              01/03/2017                 Changed parse service reponse method signature.
*   3.1     Shruthi Karanth               84636                              04/14/2017                 Softphone Optimization to skip DPaaS callout when coming through softphone routing
*   3.2     Raja Sekhar Reddy MV          85218                              04/17/2017                 Removed special characters in sDBA field
*   3.3     Gargey Sharma                 88231                              04/13/2017                 REQ - 304161 : Removed combinational validation from search queries
*   3.4     Mohammed Noor                 102175                             06/14/2017                 Fix for Production issue in Provider Search
*   3.5     Harika Devi Kalidindi         107425                             07/03/2017                 REQ - 322588 : Added Null check On SDba name at line 575 
*   3.4     Prafull Verma                 118513/118912                      08/09/2017                 REQ - 326174 Production issue in Provider Search for NPIID
*   3.5     Mohammed Noor                 125478                             09/05/2017                 Fix for Defect# 313958
*   3.6     Santhi Mandava                262341                             07/26/2018                 Implemented platform encryption for account fields.
*   3.7     Praveen Kumar Parimi          318940                             12/11/2018                 Implemented Limit for Platform Queries to avoid the Governor limits that are being hit.
*   3.8     Deepesh Rao                                                      01/16/2019                 REQ-379000 Update Account Name-Provider   
*   3.9     Santhi Mandava                                                   01/18/2019                 Replaced Name__c with Name
*   4.0     Suraj Patil                   332229                             01/29/2019                 Defect - 399185 NPIId and TaxID population on change of Account Name
*   4.1     Prudhvi Pamarthi                                                 03/11/2019                 Defect Fix - 406765
*   4.2     Deepankar Binjola             341599                             03/01/2019                 REQ - 386854 - The info in the Provider And unkonwn Provider pop up are Not cleared
*   4.3     Santhi Mandava                                                   04/04/2019                 Removed dpaas variables
*   4.4     Anjani Vaddadi                                                   11/02/2021                 REQ - 1914227 - Sonar Qube Remidiation/Consumer Management (Avoid Untrusted/Un-escaped Variables in DML Query)
*   4.5     Daniel Laratta                                                   02/24/2021                 User Story 1875439: T1PRJ0001827 - MF 1 - AHT - Ability to select "Interacting With and About" button on the Provider Search tab (RF)
*   4.6     Daniel Laratta                                                   04/14/2021                 User Story 2184307: T1PRJ0001827 - MF 1 - AHT - Ability to select "Interacting With and About" button on the Provider Search tab (RF)
*   4.7     Atia Uzma                                                        04/29/2021                 User Story 2230288: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 04/16/2021, 05/14/2021 and 05/28/2021 releases (CRM)
*   4.8     Ajay Chakradhar                                        	         05/31/2021                 REQ - 1755482 - Replacing constants to improve performance
*   4.9     Ajay Chakradhar										             10/13/2021			        US 2712261 : Global Const and HumConst files changes for Performance improvement 
*   5.0     Tharun Madishetti                                                09/13/2023                 US-4971857: T1PRJ0891339- 2023 Change Request (Est ID: 781) - Add Shipping State and Shipping Zipcode to the result section of provider
*   5.1     Thaurn Madishetti                                                09/25/2023                 DF-8148: Fix
*************************************************************************************************************************************************************************/

public with sharing class HUMProviderSearchControllerV2 extends HUMSearchComponentBaseHelperV2
{
    private string sQuery ='';
    private List<Id> lstAcntId = new List<Id>();
    private String sProviderRecordTypeId = NULL;
    @testVisible private boolean bepsstimedout= false ;
    @testVisible Map<String, String> mapEncryptedResult = NULL;
    private Boolean bSizeException = false;
    public string sEPSSReq = '';
    @testVisible private Boolean isSoftPhoneSearch;
    private Boolean bIsNameSearch{get;set;}
    public Map<String,List<ConsumerID__c>> mapAconsumerAccount {get;set;}// = new Map<String,List<ConsumerID__c>>();
    @testVisible private Set<String> setConsumerIds = new Set <String>();
    String sPlatformSOQLQuery = '';
    String sPlatformSoftPhoneSOQLQuery = '';
    String sPlatformSOSLQuery = '';
    public string mapofProvider{get;set;}
    public String consumerIds{get;set;}
    public String taxId{set;get;}
    public String npiId{set;get;}
    public ID idProvider{get;set;}
    public Boolean bIsSearchPage{get;set;}
    public boolean bShippingswitch_4971857 {get;set;}
    @testVisible private Set<String> setAccIds = new Set <String>();
    @testVisible public Boolean bIsUnknownProvider{get;set;}
    @testVisible private static integer iSearchLimit = 101;
    @testVisible private static integer iConsumerSearchLimit = 101;
    
    public HUMProviderSearchControllerV2()
    {
        bIsNameSearch =false;
        oSearchWrapper = new HUMProviderSearchWrapperHelper();
        mapStateCodeValue=populateMapForState();
        bShippingswitch_4971857 = HUMUtilityHelper.isCRMFunctionalityON('4971857');
    }
    
    public Map<String, String> getMapEncryptedResult()
    {
        return this.mapEncryptedResult;
    }
    
    public List<Account> lstSObjects 
    {
        get
        {
            if (setAccount != null)
            {
               
                return (List<Account> ) setAccount.getRecords();
            }
            else
            {
                return new List<Account>();
            }
        }
        set;
    }
    
    public List<selectOption> lstProviderStatevalue
    {   
        get
        {      
                return HUMUtilityHelper.getstateValue();  
        }
        set;
    }
  
    public Map<String,String> mapStateCodeValue { get; set; }
    public List<HUMCustomSearchHelper> lstProviderAccounts { get; set;}
    public HUMProviderSearchWrapperHelper oSearchWrapper {get;set;}
    public boolean bSearchflag { get; set; }
    public transient boolean bIsProviderError { get; set; }
    public boolean bExternalResults { get; set; }
    public boolean bIsExternalWarningExist{get;set;}
    Private String sLastName = 'sLastName';
    Private String sTaxID = 'sTaxID';
    Private String sNPI = 'sNPI';
    Private String sFirstName = 'sFirstName';
    Private String sFacilityName = 'sFacilityName';
    Private String sPostalCode = 'sPostalCode';
    Private String sSpeciality = 'sSpeciality';
    Private String sHiphan = '-';
    Private Static String NONE = 'None';
    Private Static String HumProviderRecordType = 'Provider';
    Private Static String HumUnknownProviderRecordType = 'Unknown_Provider';
    Private Static String sIndividualprovider ='I';
    Private Static String sFacilityprovider ='F';
    Private Static String sGroupprovider ='G';

   
    /**
    * Method to populate speciality dropdown on search screen
    * 
    * @param    None
    * @return   List<selectOption> 
    */
    public static List<selectOption> getSpeciality()  
    {   
        List<selectOption> lstOptions = new List<selectOption>();
        lstOptions.add(new selectOption(NONE ,'- ' +NONE + ' -'));
        Map<String, HumProviderSpecialtyMapping__c> Mapspecilaity = HumProviderSpecialtyMapping__c.getAll();  
        List<string> lstSpeciality = new list<string>(); 
        lstSpeciality.addAll(Mapspecilaity.keySet());
        lstSpeciality.sort();
        string sSpec = '';
        for (String sspeciality: lstSpeciality) 
        {
            sSpec = Mapspecilaity.get(sspeciality).Name;
            lstOptions.add(new SelectOption(sSpec , sSpec ));
        }
        return lstOptions ; 
    }
      /**
    * Method to Insert provider Account from EPSS service
    * @param    None
    * @return   None 
    */
    public void insertProviderAccount()
    {
       HUMSearchComponentBaseHelperV2 InsertProviderAccount = new HUMSearchComponentBaseHelperV2();
       idProvider = InsertProviderAccount.insertDataFromExternalSystem(MapofProvider);
        Map<String,String> mapNormalAndEncryptValues = new  Map<String,String>();
        if(String.IsNotBlank(ConsumerIds))
        {                               
            List<String> lstTaxID = ConsumerIds.split(',');
            for(integer i=0 ; i < lstTaxID.size() ; i++)
            {
                mapNormalAndEncryptValues.put(lstTaxID[i].trim(),lstTaxID[i].trim());
            }
        }
        InsertProviderAccount.upsertConsumerTaxIds(idProvider,mapNormalAndEncryptValues);
        List<ConsumerID__c> lstConsumerId = [select Consumer_ID__c from ConsumerID__c where Account__r.id =: idProvider];     
         taxId = lstConsumerId.size()>0 ? lstConsumerId[0].Consumer_ID__c : '';
         //Resetting the UI values after inserting the Provider from the service.
        reset();
    }        
    
    /**
    * Method to call EPS Service for provider search
    * 
    * @param    None
    * @return   Continuation 
    */
    public Continuation search()
    {
        bIsNameSearch =false;
        bepsstimedout= false;
        bIsUnknownProvider =false; 
        bIsExternalWarningExist = false;
        mapEncryptedResult = new Map<String, String>();
        Continuation oCon = new Continuation(GlobalCommonConstants_HUM.SERVICETIMEOUT);
        try 
        {
            @testVisible Map<String, String> mapRequestDto = new Map<String, String>();
            mapRequestDto = serviceInputs(mapRequestDto);
            mapRecordData.clear();
            if(NULL != lstSObjects  && !lstSObjects.isEmpty())    setAccount = NULL;
            //Data validation for Combinational search field & for individual field. If case of violation execution haults.
            if(!HUMUtilityHelper.validateCombinations(oSearchWrapper)) 
            {
                bIsProviderError = true;
                return null;
            }
            ProviderSearch_H_HUM oProvSearch = new ProviderSearch_H_HUM();
            sEPSSReq = oCon.addHttpRequest(oProvSearch.getEPSSReq(mapRequestDto));
            oCon.continuationMethod ='processResponse';
        }
        catch(Exception ex)
        {
            bIsProviderError = true;
            HUMExceptionHelper.logErrors(ex, 'HUMProviderSearchControllerV2', 'search');
        }
        return oCon;
    }
    
    /**
    * Callback method for EPSS response
    * 
    * @param    None
    * @return   Object 
    */
    public Object processResponse()
    {
        try
        {
            HttpResponse response = Continuation.getResponse(sEPSSReq);
            if(response != null && response.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || response.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM)
            {
                ProviderSearch_H_HUM oProvSearch = new ProviderSearch_H_HUM();
                lstProviderAccounts = oProvSearch.parseServiceResponse(response,true);
                if((NULL != lstProviderAccounts && !lstProviderAccounts.isEmpty()))
                {
                    bExternalResults = true;
                    getAccountProviderList(lstProviderAccounts);
                    if(mapRecordData != NULL && mapRecordData.size() > 100)
                    {
                       throw new HUMCustomException(System.Label.HUMSearch_returned_more_than_100_entries);
                    }
                }
            }
            else 
            { 
                bIsExternalWarningExist = true;
            }
        }
        catch(CalloutException e)
        {
            if(e.getMessage().contains(system.label.HUMSearch_Error_Response_is_Huge))
            {
                bSizeException = true;
            }   
            else
            {
                bepsstimedout = true;
            }
            HUMExceptionHelper.logErrors(e,'HUMProviderSearchControllerV2', 'processResponse');
        }
        catch(Exception ex)
        {
            bIsProviderError = true;
            HUMExceptionHelper.bRestrictLog = true;
            HUMExceptionHelper.bShowErrorOnPage = true;
            HUMExceptionHelper.logErrors(ex, 'HUMProviderSearchControllerV2', 'processResponse');
        }
        return null;
    }
    
    /**
    * Method to search provider data in SFDC
    * 
    * @param    None
    * @return   Continuation 
    */
    public Void searchSFDC()
    {
        try
        {
            isSoftPhoneSearch= false;
            bExternalResults = false;
            mapRecordData.clear();
            if(NULL != lstSObjects  && !lstSObjects.isEmpty())    setAccount = NULL;
            
            //Data validation for Combinational search field & for individual field. If violations execution haults.
            if(!HUMUtilityHelper.validateCombinations(oSearchWrapper)) 
            {
                bIsProviderError = true;
            }
            else
            {
                processResponseSFDC();
            }
        }
        catch(Exception ex)
        {
            bIsProviderError = true;
            HUMExceptionHelper.logErrors(ex, 'HUMProviderSearchControllerV2', 'searchSFDC');
        }
    }
    
    /**
    * Callback method from salesforce to populate result into wrapper class
    * 
    * @param    None
    * @return   Object 
    */
    public Object processResponseSFDC()
    {
        try
        {
            //Search in SFDC when Data is not found in EPSS
            bIsProviderError = false;
            searchInSFDC();
            if(bSizeException || bepsstimedout)
            {
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,system.label.HUMSearch_Response_is_Huge,true);
                bIsProviderError = true;
            }
        }
        catch(CalloutException e)
        {
            HUMExceptionHelper.bRestrictLog = false;
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.logErrors(e,'HUMProviderSearchControllerV2', 'processResponseSFDC');
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.Web_Service_Callout_Timeout_Error,true);
            bIsProviderError = true;
            return null;
        }
        catch(Exception ex)
        {
            bIsProviderError = false;
            HUMExceptionHelper.logErrors(ex, 'HUMProviderSearchControllerV2', 'processResponseSFDC');
        }
        return NULL;
    }
    
    /**
    * Method to assign the values and store in MAP
    * 
    * @param    Map<String, String>
    * @return   Map<String, String> 
    */
    @testVisible private Map<String, String> serviceInputs(Map<String, String> mapRequestDto)
    {
        try 
        {
            mapRequestDto.put(sLastName, oSearchWrapper.sLastname);
            mapRequestDto.put(sTaxID, oSearchWrapper.sTaxID);
            mapRequestDto.put(sNPI, oSearchWrapper.sNPI);
            mapRequestDto.put(sFirstName, oSearchWrapper.sFirstName);
            mapRequestDto.put(sFacilityName, oSearchWrapper.sFacilityName);
            mapRequestDto.put(sPostalCode, oSearchWrapper.sPostalCode);
            if(!(oSearchWrapper.sState).equalsIgnoreCase(NONE))  mapRequestDto.put('sState', oSearchWrapper.sState);
            if(!(oSearchWrapper.sSpeciality).equalsIgnoreCase(NONE)) 
            {
                HumProviderSpecialtyMapping__c oSpecialityCustSetting = HumProviderSpecialtyMapping__c.getValues(oSearchWrapper.sSpeciality );
                if(oSpecialityCustSetting != NULL) 
                {
                    oSearchWrapper.sSpeciality = oSpecialityCustSetting.EpssSpec_Code__c;
                    mapRequestDto.put(sSpeciality, oSearchWrapper.sSpeciality); 
                }
            }
        }
        catch(Exception e)
        {   
            bIsProviderError = true;
            HUMExceptionHelper.logErrors(e, 'HUMProviderSearchControllerV2', 'serviceInputs');
        }
        return mapRequestDto;
    }
    
    /**
    * This method will be called if provider search initiated from softphone.
    * 
    * @param    None
    * @return   Object 
    */
    public void searchInSFDCFromSoftPhone() 
    {
        isSoftPhoneSearch = true;
        searchInSFDC();
    }
    
    
    private void searchConsumerIDObject()
    {
        String sKey = oSearchWrapper.sTaxID+'*';
        String sConsumerQuery = '';
        List<ConsumerID__c> lstConsumers = new List<ConsumerID__c>();

        sConsumerQuery = 'FIND \'' + string.escapeSingleQuotes(sKey) +'\' IN ALL FIELDS RETURNING ConsumerID__c (Account__c,Consumer_ID__c  Where ID_Type__c = \'' + string.escapeSingleQuotes(Label.HumServiceTaxIDtag) + '\''+' AND ETL_Record_Deleted__c = false Order by createddate desc,Account__c Limit ' +iConsumerSearchLimit +')';
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
    
    /**
    * This method will be used search consumer id objet
    * 
    * @param    None
    * @return   Object 
    */
    private void queryConsumerIDObject()
    {
        String sConsumerQuery = '';
        List<ConsumerID__c> lstConsumers ;
        sConsumerQuery = 'Select Account__c,Consumer_ID__c FROM ConsumerID__c WHERE ' ;
        sConsumerQuery += 'ID_Type__c = \'' + string.escapeSingleQuotes(Label.HumServiceTaxIDtag) + '\' AND ETL_Record_Deleted__c = false '+
                        + ' AND Account__c IN : setAccIds';
        
        lstConsumers = Database.Query(sConsumerQuery );
        prepareConsumerIDMap(lstConsumers);
    }
    
    /**
    * This method will be used prepare a map with account id and corresponding consumer records. 
    * This map will be used to display consumer ids in case of platform encryption
    * @param    List<ConsumerID__c>
    * @return   Void
    */
   @TestVisible private void prepareConsumerIDMap(List<ConsumerID__c> lstConsumers )
    {
        Integer i;
        if(lstConsumers != Null && !lstConsumers.IsEmpty())
        {
            List<ConsumerID__c> lstTempConsumers = new List<ConsumerID__c>();
            
            for(i =0 ;i<=lstConsumers.size()-1;i++)
            {
                lstTempConsumers = new List<ConsumerID__c>();
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
    * @param List<Account> SOSL results
    * @param List<Account> Filtered results
    * @return   Void
    */
  @testVisible private List<Account> FilterSOSLResults(List<Account> lstTempAccounts ,List<Account> lstMembers)
    {
        Boolean bIsMatching  = true;
        setAccIds = new set<String>();
        If(lstTempAccounts != Null && !lstTempAccounts.IsEmpty())
        {
            For(Account obj : lstTempAccounts)
            {
                bIsMatching = true;
                If(String.IsNotBlank(oSearchWrapper.sFirstName) && (String.IsBlank(obj.Individual_First_Name__c) || (String.IsNotBlank(obj.Individual_First_Name__c) && !obj.Individual_First_Name__c.startsWithIgnoreCase(oSearchWrapper.sFirstName)))) bIsMatching = false;
                If(String.IsNotBlank(oSearchWrapper.sLastName) && (String.IsBlank(obj.Individual_Last_Name__c) || (String.IsNotBlank(obj.Individual_Last_Name__c) && !obj.Individual_Last_Name__c.startsWithIgnoreCase(oSearchWrapper.sLastName)))) bIsMatching = false;
                If(String.IsNotBlank(oSearchWrapper.sFacilityName) && (String.IsBlank(obj.Name) || (String.IsNotBlank(obj.Name) && !obj.Name.startsWithIgnoreCase(oSearchWrapper.sFacilityName)))) bIsMatching = false;
                If(bIsMatching)
                {           
                    lstMembers.Add(obj);
                }
            }
        }
        return lstMembers;
    }
    
    
    /**
    * Method to search in SFDC when integration does ot return any result.
    * 
    * @param    None
    * @return   void 
    */
    public void searchInSFDC() 
    {
        try 
        {
             bIsUnknownProvider =false; 
             bIsNameSearch =false;
             bIsExternalWarningExist = false;
       
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
            setAccIds = new Set <String>();
            List<ConsumerID__c> lstTemp = new List<ConsumerID__c>();
            mapAconsumerAccount = new Map<string,List<ConsumerID__c>>();
            For(Account obj :lstMembers )
            {
                setAccIds.Add(Obj.Id);
                lstTemp = new List<ConsumerID__c>();
                mapAconsumerAccount.put(Obj.Id,lstTemp );
            }
            queryConsumerIDObject();
            setAccount = new ApexPages.StandardSetController(lstMembers);
            setAccount.setPageSize(Integer.valueof(Label.ListSizePageMemSearch)); 
            List<Account> lstAccountTemp = new List<Account>();
            lstAccountTemp.addAll((List<Account>)setAccount.getRecords());
           
           if( lstMembers.size() == 0)
           {
               if(bIsExternalWarningExist)    
               {
                   throw new HUMCustomException(System.Label.HUMProviderWarning);
               }
               else if(!bSizeException)
               {
                   throw new HUMCustomException(System.Label.Hum_Provider_results);    
               }
           } 
           if( lstMembers.size() > 100) throw new HUMCustomException(System.Label.HUMSearch_returned_more_than_100_entries); 
        }
        catch(Exception e)
        {
            bIsProviderError = true;
            HUMExceptionHelper.logErrors(e, 'HUMProviderSearchControllerV2', 'searchInSFDC');
        } 
    }
    
    /**
    * Method to create queries in case of platform encryption
    * 
    * @param    None
    * @return   void 
    */
    private void createPlatformQieries()
    {
        sPlatformSOQLQuery = 'Select Id, Name, RecordType.Name, Individual_First_Name__c,Description,Primary_Specialty__c,'+
                'BillingPostalCode, Individual_Last_Name__c, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet, '+
                'ShippingPostalCode, ShippingState, ShippingStateCode,'+
                ' NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c From Account Where (RecordType.DeveloperName='+'\''+HumProviderRecordType+'\''+
                +' OR RecordType.DeveloperName ='+'\''+ HumUnknownProviderRecordType+'\')';
                
        sPlatformSoftPhoneSOQLQuery = 'Select Id, Name, RecordType.Name, Individual_First_Name__c,Description,Primary_Specialty__c,'+
                'BillingPostalCode, Individual_Last_Name__c, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet, '+
                'ShippingPostalCode, ShippingState, ShippingStateCode,'+
                ' NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c From Account Where (RecordType.DeveloperName='+'\''+HumProviderRecordType+'\''+
                +' OR RecordType.DeveloperName ='+'\''+ HumUnknownProviderRecordType+'\')';
               
        mapAconsumerAccount = new Map<String,List<ConsumerID__c>>();
        String sProviderClassification = '';
        String sKey  = '';
        if(((String.isNotEmpty(oSearchWrapper.sFirstName) && String.isNotEmpty(oSearchWrapper.sLastName)) || String.isNotEmpty(oSearchWrapper.sFacilityName)))
        {
            bIsNameSearch =true;
            if(String.isNotEmpty(oSearchWrapper.sFacilityName))
            {
                sKey = oSearchWrapper.sFacilityName.trim() +'*';
                sProviderClassification  = '(Provider_Classification__c = \'' + sFacilityprovider + '\' OR Provider_Classification__c ='+'\''+sGroupprovider+'\''+')';
                sPlatformSoftPhoneSOQLQuery += ' AND Name = ' + string.escapeSingleQuotes(oSearchWrapper.sFacilityName) + '\' AND '+sProviderClassification; 
                
                sPlatformSOSLQuery  = 'FIND \'' + string.escapeSingleQuotes(sKey) + '\' IN NAME FIELDS RETURNING Account(Id,Name,RecordType.Name, Individual_First_Name__c,Description,Primary_Specialty__c,'+
                +'BillingPostalCode, Individual_Last_Name__c, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet, '+
                +'ShippingPostalCode, ShippingState, ShippingStateCode,'+
                +'NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c Where (RecordType.DeveloperName='+'\''+HumProviderRecordType+'\''+
                +' OR RecordType.DeveloperName ='+'\''+ HumUnknownProviderRecordType+'\')' ;
            }
            else
            {
                sKey = oSearchWrapper.sFirstName.trim()+'* AND '+oSearchWrapper.sLastName.trim() +'*';
                sProviderClassification = 'Provider_Classification__c = \'' + sIndividualprovider+ '\'';
                sPlatformSoftPhoneSOQLQuery += ' AND Individual_First_Name__c = \'' + string.escapeSingleQuotes(oSearchWrapper.sFirstName) + '\' AND Individual_Last_Name__c = \'' + string.escapeSingleQuotes(oSearchWrapper.sLastName) + '\' AND '+sProviderClassification;
                
                sPlatformSOSLQuery  = 'FIND \'' + string.escapeSingleQuotes(sKey) + '\' IN ALL FIELDS RETURNING Account(Id,Name, RecordType.Name, Individual_First_Name__c,Description,Primary_Specialty__c,'+
                +'BillingPostalCode, Individual_Last_Name__c, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet, '+
                +'ShippingPostalCode, ShippingState, ShippingStateCode,'+
                +'NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c Where (RecordType.DeveloperName='+'\''+HumProviderRecordType+'\''+
                +' OR RecordType.DeveloperName ='+'\''+ HumUnknownProviderRecordType+'\')' ;
            }
            
            if(String.isNotBlank(sProviderClassification)) sPlatformSOSLQuery +=  ' AND '+sProviderClassification  ;
        }
        if(String.isNotEmpty(oSearchWrapper.sNPI))  
        {
            sPlatformSOQLQuery += ' AND NPI_ID__c = \'' + string.escapeSingleQuotes(oSearchWrapper.sNPI.trim())+ '\' '; 
            sPlatformSoftPhoneSOQLQuery += ' AND NPI_ID__c= \'' + string.escapeSingleQuotes(oSearchWrapper.sNPI.trim())+ '\' '; 
            sPlatformSOSLQuery += ' AND NPI_ID__c= \'' + string.escapeSingleQuotes(oSearchWrapper.sNPI.trim())+ '\' '; 
        }
        if(!(oSearchWrapper.sState).equalsIgnoreCase(NONE))  
        {
            sPlatformSOQLQuery += ' AND BillingStateCode = \''+ String.escapeSingleQuotes(oSearchWrapper.sState) +'\'';
            sPlatformSOSLQuery += ' AND BillingStateCode =\''+ String.escapeSingleQuotes(oSearchWrapper.sState)+'\'';
        }
        if(String.isNotEmpty(oSearchWrapper.sPostalCode))  
        {
            sPlatformSOQLQuery += ' AND (BillingPostalCode LIKE \'' + string.escapeSingleQuotes(oSearchWrapper.sPostalCode) +'%'+'\')';            
            sPlatformSOSLQuery += ' AND (BillingPostalCode LIKE \'' + string.escapeSingleQuotes(oSearchWrapper.sPostalCode) +'%'+'\')';            
        }
        If(String.IsNotBlank(oSearchWrapper.sTaxID))
        {
            searchConsumerIDObject();
            if(!mapAconsumerAccount.isEmpty()) setConsumerIds = mapAconsumerAccount.keySet();

            sPlatformSOQLQuery += ' AND ID IN : setConsumerIds ';
            sPlatformSOSLQuery += ' AND ID IN : setConsumerIds ';
            sPlatformSoftPhoneSOQLQuery += ' AND ID IN : setConsumerIds ';
        }
        sPlatformSOQLQuery += ' ORDER BY createddate desc Limit '+iSearchLimit;
        sPlatformSOSLQuery += ' ORDER BY createddate desc Limit '+iSearchLimit;
        sPlatformSOSLQuery += ' )';
        sPlatformSoftPhoneSOQLQuery += ' ORDER BY createddate desc Limit '+iSearchLimit;
    }

    /**
    * Method is called when user click on Search Unknown Account button it makes an local search in salesforce
    * 
    * @param    None
    * @return   void 
    */
    public void searchUnknownAccounts() 
    {
        try 
        {
            bIsExternalWarningExist = false;
            //Data validation for Combination search field & for individual field. If violations execution haults.
            if(NULL != lstSObjects  && !lstSObjects.isEmpty())    setAccount = NULL;
            if(!HUMUtilityHelper.validateCombinations(oSearchWrapper))
            {
                bIsProviderError = true;
                return;
            }
            searchInSFDC();    
        }
        catch(exception ex) 
        { 
            bIsProviderError = true;
            HUMExceptionHelper.logErrors(ex,'HUMProviderSearchController', 'searchUnknownAccounts');
        }
    }
    
    /**
    * Method is used to clear data in input fields and result
    * 
    * @param    None
    * @return   void 
    */
    public void Reset() 
    {
        try 
        {
            oSearchWrapper = new HUMProviderSearchWrapperHelper();
            setAccount = new ApexPages.StandardSetController(new List<Account>());
            if(NULL != lstProviderAccounts)   lstProviderAccounts.clear();
            mapRecordData.clear();
        }
        catch(exception ex)
        { 
            bIsProviderError = true;
            HUMExceptionHelper.logErrors(ex,'HUMProviderSearchControllerV2', 'Reset');
        }
    }
    
    /**
    * Method to Creates an account object by mapping fields from integration result
    * 
    * @param    list<HUMCustomSearchHelper>
    * @return   void 
    */
    public void getAccountProviderList(list<HUMCustomSearchHelper> Wrapperdata)
    {
        try
        {   
            sProviderRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(Label.HumProviderRecordType).getRecordTypeId(); 
            Map<Integer, Account> mapAcc = new Map<Integer, Account>();
            mapConsumerTaxIds = new Map<Integer,String>();
            for(Integer j = 0 ; j < Wrapperdata.size() ; j++)
            {                
                mapAcc.put(j, getAccount(Wrapperdata[j])); 
                mapConsumerTaxIds.put(j,Wrapperdata[j].sTaxID);
            }
           iCounter = 0;
           iPagesize = Integer.valueof(Label.HumProviderPaginationSize);
           mapRecordData = mapAcc;
        }
        catch(Exception ex)
        {
            bIsProviderError = true;
            HUMExceptionHelper.logErrors(ex,'HUMProviderSearchControllerV2', 'getAccountProviderList');
        }
    }
    
    /**
    * Method is used to Create list of accounts from integration results to use in the page
    * 
    * @param    HUMCustomSearchHelper
    * @return   Account 
    */
    @testVisible private Account getAccount(HUMCustomSearchHelper Wrapperdata)
    {
       String specialChars = System.Label.HUMSpecialCharprovider;        
        if(!Pattern.matches(specialChars, Wrapperdata.sLastname))
        {
            for(integer i=0; i<specialChars.split('|').size(); i++)
                Wrapperdata.sLastname=Wrapperdata.sLastname.replace(specialChars.split('|')[i], '');
        }
        if(string.isnotblank(Wrapperdata.sDba) && !Pattern.matches(specialChars, Wrapperdata.sDba) )
        {
           for(integer i=0; i<specialChars.split('|').size(); i++)
           Wrapperdata.sDba=Wrapperdata.sDba.replace(specialChars.split('|')[i], '');
        }
       
       String sName = ((Wrapperdata.sFirstname !=NULL)? Wrapperdata.sFirstname:'') + ((Wrapperdata.sLastname !=NULL)? Wrapperdata.sLastname :'');
       Account oAcc = new Account(Name = sName, recordtypeid = ID.valueof(sProviderRecordTypeId),         
                          NPI_ID__c = Wrapperdata.sNPI, Description = Wrapperdata.sSpeciality, 
                          Account_External_ID__c = Wrapperdata.sExtID, BillingStreet = Wrapperdata.sAddress,
                          BillingStatecode = Wrapperdata.sState, BillingPostalCode = Wrapperdata.sPostalCode,
                          Billingcity = Wrapperdata.sCity, Provider_Classification__c= Wrapperdata.sClassification,
                          Birthdate__c = changeDateStringFormat(Wrapperdata.sBirthdate),Gender__c = Wrapperdata.sGender,
                          Taxonomy_Code__c = wrapperdata.sTaxmonycode, Enterprise_ID__c= wrapperdata.sEnterpriseID ,
                          Degree__c=wrapperdata.sDegree, Tenant_Id__c = label.HUMExtMemAccountTenantID,
                          Source_Platform_Code__c =wrapperdata.sPlatform,DBA__c = wrapperdata.sDba,
                          phone =wrapperdata.sphone,Phone_Ext__c=wrapperdata.sPhoneExtn ,
                          ShippingStatecode =Wrapperdata.sServiceState , shippingPostalCode = Wrapperdata.sServicezip ,
                          ShippingCity= Wrapperdata.sServiceCity ,ShippingStreet = wrapperdata.sServiceaddress,
                          Individual_Last_Name__c = Wrapperdata.sLastname, Individual_First_Name__c = Wrapperdata.sFirstname,
                          General_Account__c =true );

        return oAcc;
    }
    
    /**
    * Method is used to change the date string format from'yyyy-MM-dd' to 'MM/dd/yyyy'
    * 
    * @param    string
    * @return   string 
    */
    @testVisible private String changeDateStringFormat(String sdob)
     {
         String sModifiedDOB='';
         List<String> lstdobValues=new List<String>();
         
         if(sdob != NULL)
         {
             lstdobValues =  sdob.split(sHiphan);
             if(lstdobValues.size() == 3) sModifiedDOB=  lstdobValues[1]+'/'+lstdobValues[2]+'/'+lstdobValues[0];
         }
         return sModifiedDOB;
     }
    
    /**
    * Method is used to Create list of accounts for  Unknown Account
    * 
    * @param    Id
    * @return   void 
    */
    public void  buildUnknownAccountMap(Id iAccntId)
    {
        try
        {
            bExternalResults = false;     
            bIsUnknownProvider =true; 
            String sQry ='Select Id, Name, Individual_First_Name__c, Primary_Specialty__c,ETL_Record_Deleted__c, '+
                       'BillingPostalCode,RecordType.Name,Description, Individual_Last_Name__c, BillingState, BillingStreet,BillingStateCode, '+
                       'ShippingPostalCode, ShippingState, ShippingStateCode,'+
                       ' NPI_ID__c, RecordTypeid '+
                     + ',(Select Consumer_ID__c from ConsumerIDs__r) from Account where Id=:iAccntId';
            setAccount = new ApexPages.StandardSetController(Database.getQueryLocator(sQry));
            setAccount.setPageSize(Integer.valueof(Label.ListSizePageMemSearch));
            
        }
        catch(Exception ex)
        {
             bIsProviderError = true;
             HUMExceptionHelper.logErrors(ex, 'HUMProviderSearchControllerV2', 'buildUnknownAccountMap');
        }
    }
    
    /**
    * Method is used to populate map with state code as a key and state as value which is coming from'HUM_Part_D_State_Mapping__c' custom setting.
    * 
    * @param    None
    * @return   Map<String, String> 
    */
    @testVisible private Map<String,String> populateMapForState(){
        Map<String,String> mapStateCode = new Map<String,String>();
        for(HUM_Part_D_State_Mapping__c custObjState: HUM_Part_D_State_Mapping__c.getAll().values()){
            if(String.isNotBlank(custObjState.State_Code__c) && String.isNotBlank(custObjState.State__c)     )
                mapStateCode.put(custObjState.State_Code__c,custObjState.State__c);
        }
        mapStateCode.put(NULL,'');
        mapStateCode.put(' ','');
        return mapStateCode;
    }
    
    /*Method Name    : fieldCipherMapping
     * Description   :  This Method is called when search member is driven by Softphone 
     *                  and as input this method accept Wrapper data and return actual fieldname and Cipher text mapping 
     * Return Type   :  Map<String, String>
     * Parameters    :  HUMMemberSearchWrapperHelper oSearchWrapperwithCipher
     */  
     @testvisible private Map<String, String> fieldCipherMapping(HUMProviderSearchWrapperHelper oSearchWrapperwithCipher)
    {
        Map<String, String> accFieldCipher = new Map<String,String>();
       
        if (NULL != oSearchWrapperwithCipher && NULL !=oSearchWrapperwithCipher.mapEncryptedValues ) {
            //Interacting About Details
            if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId').Value__C)!=null)
                accFieldCipher.put(System.Label.HUMConsumerTaxID,oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'TaxId').Value__C));
            if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName').Value__c)!=null)
                accFieldCipher.put('Individual_First_Name__c', oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName').Value__c));
            if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__c)!=null)
                accFieldCipher.put('Individual_Last_Name__c', oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__c));
            if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('NpiId').Value__c)!=null)
                accFieldCipher.put(System.Label.HUMAccountNPIID, oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('NpiId').Value__c));
            
            //Interacting With Details
            if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__C)!=null ||
               oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName2').Value__c)!=null ||
               oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c)!=null ||
               oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('NpiId2').Value__c)!=null )
            {
                accFieldCipher = new Map<String,String>();
                if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__C)!=null)
                    accFieldCipher.put(System.Label.HUMConsumerTaxID,oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__C));
                if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName2').Value__c)!=null)
                    accFieldCipher.put('Individual_First_Name__c', oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName2').Value__c));
                if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c)!=null)
                    accFieldCipher.put('Individual_Last_Name__c', oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c));
                if(oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('NpiId2').Value__c)!=null)
                    accFieldCipher.put(System.Label.HUMAccountNPIID, oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('NpiId2').Value__c));
            }
        }
        return accFieldCipher;
    }  
}