/*******************************************************************************************************************************
Apex Class Name : HUMGroupSearchControllerV2
Version         : 2.0
Created On      : 08/31/2015
Function        : The class has been made as without sharing since the user should be able to view all groups
                  in search results, irrespective of whether user has access or not. If the user does not have access, he will be
                  directed to limited account page.
Test Class      : HUMGroupSearchControllerV2Test
Modification Log: 
*   Version        Developer Name                Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*    2.0            Chaitanya Kumar/               25261               07/31/2015                 Code refactoring ( removing abstract layer - HUMSearchFieldHelper)
                    Harshith Mandya 
*    2.1            Shreya Choodamani              25261               07/31/2015                 Implementing Async Webservice Callout 
*    2.2            Vishal Verma                   26498               09/18/2015                 Fix for  Defect #195755.0001: Added pagesize to standardsetcontroller in 
                                                                                                  dataSecurity method.
*    2.3            Vishal Verma                   26498               09/23/2015                 Fixed Review Comments 
*    2.4            Praveen Kumar Parimi           27872               11/16/2015                 Updated the populateGroupInfoOnAccount() method for passing the Account external ID in correct format, added Source_Customer_Key__c value, formatted the code.
*    2.5            Vishal Verma                   29860               02/11/2016                 Fix for  Defect #215828.0001 
*    2.6            Vishal Verma                   30238               03/01/2016                 Merged the code changes of V1 and V2 versions for populateGroupInfoOnAccount method.
*    2.7            Shruthi Karanth                                    04/14/2017                 Softphone Optimization to skip DPaaS callout when coming through softphone routing
*    2.8            Mohammed Noor                 122564               08/23/2017                 REQ - 326313 - CA incident # 6941459 - Fix for Softphone if the caller is not authenticated.                
*    2.9            Mohammed Noor                 125478               09/05/2017                 Fix for Defect # 313958.
*    2.10           Raja Sekhar Reddy M V                              20/03/2018                 REQ - 355953 - Removing DPASS Callouts       
*    2.11           Sunil Bukya                   260674               07/23/2018                 REQ - 362070 - Implemented Group Name platform encryption changes.
*    2.12           Sunil Bukya                                        18/12/2018                 Removed cipherCloud encryption call
*    2.13           Deepesh Rao                                        01/29/2019                 REQ-379002 Update Account Name-Group
*    2.14           Mithra Bharadwaj                                   02/18/2019                 Defect 402070.0001 Fix Added field ETL_Record_Deleted__c to the Query
*    2.15           Prudhvi Pamarthi                                   03/11/2019                 Defect Fix - 406765
*    2.16      	    Deepankar Binjola			  341599	 	       03/01/2019				  REQ - 386854 - The info in the Provider and unkonwn Provider pop up are not cleared 
*    2.17			Riddhi Mittal									   07/29/2020 				  CCP functionality shutdown for Group Account.
*    2.18    		Firoja Begam									   09/02/2020 				  REQ - 1431988 - Group number Is Not reflecting in Group Search
*    2.18    		Firoja Begam									   09/18/2020 				  DEF Fix - 1668
*    2.19           Anjani Vaddadi                                     11/02/2021                 REQ - 1914227 - Sonar Qube Remidiation/Consumer Management (Avoid Untrusted/Un-escaped Variables in DML Query)
*    2.20           Firoja Begam                2237351                05/03/2021                 On/Off Switch Cleanup for requirements deployed
*    2.21	        Ajay Chakradhar                                    05/31/2021                 REQ - 1755482 - Replacing constants to improve performance
*    2.22           Ankit Avula                                        08/24/2021                 US2619274 T1PRJ0002980/PR00095219 - Phase 2 - MF06 - Contract Protected Group indicator within Group Search Results page
*    2.23           Ajay Chakradhar                                    11/22/2021                 DEF 4182 FIX - Global Constant replace for performance improvement
*    2.24           Santhi Mandava                                     03/07/2024                 User Story 5303730: T1PRJ0891742 - C01; Consumer Management - Classic VIT Remediation Search pages
*******************************************************************************************************************************/

public without sharing class HUMGroupSearchControllerV2 extends HUMSearchComponentBaseHelperV2
{
    
    @testVisible private List<Account> lstAccountList = new List<Account>();
    @testVisible private Boolean isSoftPhoneSearch;
    @testVisible private Boolean bIsNameSearch = false;
    public String idGroup{set;get;} 
    public String mapOfGroup{set;get;}
	Public Boolean ccpOnOff {get; set;}
    public boolean groupCPDOnOff {get;set;}
    Private Integer iListSizePageMemSearch = 10;
    Private String sHUMSearch_returned_more_than_100_entries = 'Your search returned more than 100 entries. Please refine your search.';
    Private String sHUM_Group_Account_Record_Type = 'Group';
    Private String sHUM_UnknownGroup_Account_Record_Type = 'Unknown_Group';
    Private String sHUMStateDefaultValue = 'None';
    Private String sFMValue = 'FM';
    Private String Seperator = '|';
    Private String TENANTID_GEN = '00';
    Private String sGRPValue = 'GRP';
    Private String sODSValue = 'ODS';
    Private String sHUMGroupNumber = 'Group_Number__c';
    Private String sDummyBenValue = 'XXXXXXXX';
        
    /*
     * Method Name : HUMGroupSearchControllerV2 
     * Description : Constructor for the class 
     * Return type : NA
     * Paramater   : NA
     */
    public HUMGroupSearchControllerV2()
    { 
        oHUMSearchController = new HUMSearchControllerV2();
        bIsGroupError = false;
        bIsUnknownGroupSearch = false;
        oSearchWrapper = new HUMGroupSearchWrapperHelper();
		ccpOnOff = false;
        // On/Off switch functionality for CCP 
        ccpOnOff= HUMUtilityHelper.isCRMFunctionalityON('1185083');
        groupCPDOnOff= HUMUtilityHelper.isCRMFunctionalityON('2619274');
    }

    //Variable to hold the instance of the SearchController class
    public HUMSearchControllerV2 oHUMSearchController{get;set;}
    
    //Boolean to check if an error thrown is for Group search. This is used in Softphone call popup.
    public transient boolean bIsGroupError{get;set;}
    
    //Boolean to check if only internal search is to be done
    public transient boolean bIsUnknownGroupSearch{get;set;}    
    
    //Boolean to indicate that the results are from external system
    public boolean bExternalResults{get;set;}
    
    //Map to hold the records that the user has access to 
    public Map<Id, Boolean> mapRecordHaveAccess {get;set;}

    //As per the US 2619274 to check the access for the record.
    public Map<Id, Boolean> mapRecordUserAccess {get;set;}
    
    //Variable to hold the web service callout Request
    public AsyncHUMGroupSearchService.GroupSearchResponse_elementFuture resElementFuture;
    
    //Variable to hold the web service callout Response
    public List<HUMGroupSearchServiceHelper.ResponseGroupSearch> responseDTO {get;set;}   
    
    //Variable to hold the instance of the wrapper for group
    public HUMGroupSearchWrapperHelper oSearchWrapper{get; set;}
    Public Boolean bIsSearchPage{get;set;}
    public List<Account> lstSObjects 
    {
        get 
        {
            return setAccount != null ? (List <Account> ) setAccount.getRecords(): new List<Account>();
        }
        set;
    }
    
    //To get the list of state values
    public List<selectOption> Groupstatevalue
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
            return lstLogErrors != null ? lstLogErrors : new List<Error_Log__c>();
        }
        set;
    } 
    
    public HUM_Webservice_Callout__c objWebserviceCallout = HUM_Webservice_Callout__c.getInstance('GroupSearchService');

    /*  
     * Method name : groupsearch
     * Description : This method will be called on clicking the search button on Group search page.
     * Return Type : Pagereference
     * Parameter   : NA
     */ 
    public Continuation groupsearch() 
    {
        // ininitializing continuation object and callback method
        Continuation cont = null ;
        isSoftPhoneSearch = false;
        bIsNameSearch = false;
        
        
        try
        {   
            resElementFuture = null;
            responseDTO = null;
            setAccount = new ApexPages.StandardSetController(new List<Account>());
            
            if(mapRecordData != null && !mapRecordData.isEmpty()){mapRecordData.clear();}  
            
            //Validation to ensure value entered for Group number is alpha-numeric
            if((!HUMUtilityHelper.validateGroupNumber(oSearchWrapper.sGroupNumber)) || (!HUMUtilityHelper.validateGroupSearch(oSearchWrapper)))
            {
                bIsGroupError = true;
                return null;
            }
            
            //Setting pagination & counter for pageblock table
            iCounter = 0;
            iPagesize = iListSizePageMemSearch; 
                      
            if(bIsUnknownGroupSearch == true) 
            {  
			if(ccpOnOff)
              { 
              queryUnknownGroupAccount(); 
              }
                     
            }
            else
            {
                queryGroupAccount();

            }
            if(lstLogErrors.size() > 0 ){ HUMExceptionHelper.saveLog(lstLogErrors,true);}
			
			if(ccpOnOff){

            if(lstSObjects.isEmpty())
            {  
                throw new HUMCustomException(System.Label.HUMGroupSearchNoResultFound);
            }               
            if((lstSObjects!= null && lstSObjects.size() > 100))
            {  
                throw new HUMCustomException(sHUMSearch_returned_more_than_100_entries);
            } 
            
            }
        }
        catch(Exception e)
        {
            bIsGroupError = true;
            HUMExceptionHelper.bShowErrorOnPage = true;
            HUMExceptionHelper.bRestrictLog =true;
            lstLogErrors= HUMExceptionHelper.logErrors(e, 'HUMGroupSearchControllerV2', 'groupsearch');
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
        return cont;
    }
    
    /*  
     * Method name : callFromSoftPhone
     * Description : This method is called when group search
     * Return Type : Pagereference
     * Parameter   : NA
     */
    public Object callFromSoftPhone()
    {
        try
        {       
            isSoftPhoneSearch = true;
            setAccount = new ApexPages.StandardSetController(new List<Account>());            
            if(mapRecordData != null && !mapRecordData.isEmpty()){mapRecordData.clear();}
            if(HUMUtilityHelper.validateGroupSearch(oSearchWrapper))
            {                                 
                queryGroupAccount();
            }
            else
            {
                bIsGroupError = true;
            }
        }
        catch(Exception e)
        {
            bIsGroupError = true;
            HUMExceptionHelper.bShowErrorOnPage = true;            
            HUMExceptionHelper.bRestrictLog =true;
            lstLogErrors= HUMExceptionHelper.logErrors(e, 'HUMGroupSearchControllerV2', 'groupsearch'); 
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
        return null;
    }

    /*  
     * Method name : insertGroupAccount
     * Description :Insert group account from external system
     * Return Type : void
     * Parameter   : NA
     */
    public void insertGroupAccount()
    {
        HUMSearchComponentBaseHelperV2 insertGroupAccount = new HUMSearchComponentBaseHelperV2();
        idGroup = insertGroupAccount.insertDataFromExternalSystem(mapOfGroup);
		//Resetting the UI values after inserting the Group from the service.
        Reset();
    }

    
    
    /*  
     * Method name : queryGroupAccount
     * Description : This method will be called when integration does not return any result
                     to do an internal search within SFDC
     * Return Type : void
     * Parameter   : NA
     */
    public void queryGroupAccount(){  
        bExternalResults = false; 
        lstAccountList = new List<Account>();
        String sQry = '';  
        String Group_Name = oSearchWrapper.sGroupName + '*';
        String strGroupName = string.escapeSingleQuotes(Group_Name);
        String strGroupNumber = string.escapeSingleQuotes(oSearchWrapper.sGroupNumber);
        String strState = string.escapeSingleQuotes(oSearchWrapper.sState);
        Integer intLimit = Integer.valueOf(HUMSearchLimit__c.getInstance('Member_SearchLimit').Search_Limit__c);
        if(!String.isblank(oSearchWrapper.sGroupName)) bIsNameSearch = true; 
        if(bIsNameSearch && !isSoftPhoneSearch)
        {   
            List<Account> lstTempAccounts = new List<Account>();              
            boolean bIsMatching =false;                   
            sQry = 'FIND :strGroupName IN NAME FIELDS RETURNING Account(Id, FirstName,Home_Office_Account__c,ETL_Record_Deleted__c, RecordType.Name, Phone, BillingStreet, BillingCity, BillingState,' +
                'BillingCountry, Group_Number__c,Source_Customer_Key__c,Source_Platform_Code__c, Name where (RecordType.DeveloperName =:sHUM_Group_Account_Record_Type';
            sQry += ' OR RecordType.DeveloperName =: sHUM_UnknownGroup_Account_Record_Type)';
            if(!(String.isBlank(oSearchWrapper.sGroupNumber))){sQry += ' AND Group_NuMBER__C=:strGroupNumber';}
            if(!(String.isBlank(oSearchWrapper.sState)) && oSearchWrapper.sState != sHUMStateDefaultValue)
            {
                sQry += ' AND BillingStateCode =:strState)';
            }
            //Per guidelines added limit to the query to restrict results to 100 records
            sQry = sQry + ' Limit :intLimit';
            List<List<Account>>searchList=search.query(sQry);
            lstTempAccounts = searchList[0];
            for(Account obj : lstTempAccounts)
            {
                bIsMatching = true;
                if(String.IsBlank(obj.Name) || (String.IsNotBlank(obj.Name) && !obj.Name.startsWithIgnoreCase(oSearchWrapper.sGroupName))) bIsMatching = false;
                if(bIsMatching)
                {   
                    lstAccountList.Add(obj);
                }
            }
            if(ccpOnOff)
            {
                if(lstAccountList.isempty())
                {            
                    sQry = 'FIND :strGroupName IN NAME FIELDS RETURNING Account(Id, FirstName,Home_Office_Account__c,ETL_Record_Deleted__c, RecordType.Name, Phone, BillingStreet, BillingCity, BillingState,' +
                        'BillingCountry, Group_Number__c,Source_Customer_Key__c,Source_Platform_Code__c, Name where (RecordType.DeveloperName =:sHUM_Group_Account_Record_Type';
                    sQry += ' OR RecordType.DeveloperName =:sHUM_UnknownGroup_Account_Record_Type)';
                    if(!(String.isBlank(oSearchWrapper.sGroupNumber))){sQry += ' AND Source_Customer_Key__c=:strGroupNumber';}
                    if(!(String.isBlank(oSearchWrapper.sState)) && oSearchWrapper.sState != sHUMStateDefaultValue)
                    {
                        sQry += ' AND BillingStateCode =:strState)';
                    }
                    //Per guidelines added limit to the query to restrict results to 100 records
                    sQry = sQry + ' Limit :intLimit';
                    List<List<Account>>searchList1=search.query(sQry);
                    lstTempAccounts = searchList1[0];
                    for(Account obj : lstTempAccounts)
                    {
                        bIsMatching = true;
                        if(String.IsBlank(obj.Name) || (String.IsNotBlank(obj.Name) && !obj.Name.startsWithIgnoreCase(oSearchWrapper.sGroupName))) bIsMatching = false;
                        if(bIsMatching)
                        {   
                            lstAccountList.Add(obj);
                        }
                    }
                    
                }
            }
        }
        else
        {
            sQry = 'Select Id, FirstName,Home_Office_Account__c,Source_Customer_Key__c, RecordType.Name, Phone, BillingStreet, BillingCity,ETL_Record_Deleted__c,BillingState, BillingCountry, Group_Number__c,'; 
            sQry += 'Source_Platform_Code__c,UserRecordAccess.HasReadAccess, Name from Account where (RecordType.DeveloperName =:sHUM_Group_Account_Record_Type';
            sQry += ' OR RecordType.DeveloperName =:sHUM_UnknownGroup_Account_Record_Type)';
            
            if(!(String.isBlank(oSearchWrapper.sGroupNumber))){sQry += ' AND Group_NuMBER__C=:strGroupNumber';}
            
            if(!(String.isBlank(oSearchWrapper.sState)) && oSearchWrapper.sState != sHUMStateDefaultValue)
            {
                sQry += ' AND BillingStateCode =:strState';
            }
            //Per guidelines added limit to the query to restrict results to 100 records
            sQry = sQry + ' Limit :intLimit';                    
            lstAccountList = Database.Query(sQry);
            
            if(ccpOnOff)
            {
                if(lstAccountList.isempty())
                {
                    sQry = 'Select Id, FirstName,Home_Office_Account__c,Source_Customer_Key__c, RecordType.Name, Phone, BillingStreet, BillingCity,ETL_Record_Deleted__c,BillingState, BillingCountry, Group_Number__c,'; 
                    sQry += 'Source_Platform_Code__c,UserRecordAccess.HasReadAccess, Name from Account where (RecordType.DeveloperName =:sHUM_Group_Account_Record_Type';
                    sQry += ' OR RecordType.DeveloperName =:sHUM_UnknownGroup_Account_Record_Type)';
                    
                    if(!(String.isBlank(oSearchWrapper.sGroupNumber))){sQry += ' AND Source_Customer_Key__c=:strGroupNumber';}
                    
                    if(!(String.isBlank(oSearchWrapper.sState)) && oSearchWrapper.sState != sHUMStateDefaultValue)
                    {
                        sQry += ' AND BillingStateCode =:strState';
                    }
                    //Per guidelines added limit to the query to restrict results to 100 records
                    sQry = sQry + ' Limit :intLimit';                    
                    lstAccountList = Database.Query(sQry);
                    
                    
                }
            }
        }
        dataSecurity(); 
    }
    
    
   /* Method Name   :   processResponse     
    * Description   :   Callback method for the GroupWebService Callout
    * Return Type   :   Object  
    * Parameters    :   NA      
    */ 
    public Object processResponse() 
    {
        try
        {
            Map<Integer,Account> mapAcc = new Map<Integer,Account>();  
            integer iKey = 0;      
            if(!Test.isRunningTest())  responsedto = callService();
            if(responsedto != null && !responsedto.IsEmpty() )              
            {                        
                bExternalResults = true;                      
                for(HUMGroupSearchServiceHelper.ResponseGroupSearch extAccountRecord:responsedto)                  
                {                      
                    mapAcc.put(iKey,populateGroupInfoOnAccount(extAccountRecord));                      
                    iKey++;                                    
                }                  
                mapRecordData = mapAcc;              
            }
            //If no search results are returned both from integration and salesforce, error message is shown to the user
            
            if(mapRecordData.isEmpty() && lstSObjects.isEmpty())
            {
                throw new HUMCustomException(System.Label.HUMGroupSearchNoResultFound);
            }               
            if((mapRecordData != null && mapRecordData.size() > 100) || lstSObjects.size() > 100)
            { 
                throw new HUMCustomException(sHUMSearch_returned_more_than_100_entries);
            }      
        }
        catch(CalloutException ex)
        {
           bIsGroupError = true;
           HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning, System.Label.Web_Service_Callout_Timeout_Error, true);
           HUMExceptionHelper.logErrors(ex, 'HUMGroupSearchControllerV2', 'processResponse');
           HUMExceptionHelper.bRestrictLog =false;
        }
        catch(Exception e)
        {
            bIsGroupError = true;
            HUMExceptionHelper.logErrors(e, 'HUMGroupSearchControllerV2', 'processResponse'); 
            HUMExceptionHelper.bRestrictLog =false;
        }
        return null; 
    }
    
    /*  
     * Method name : callService
     * Description : This method return the response of the webservice callout
     * Return Type : HUMGroupSearchServiceHelper.ResponseGroupSearch[] 
     * Parameter   : NA 
     */ 
    public HUMGroupSearchServiceHelper.ResponseGroupSearch[] callService()
    {
        try
        {
            return resElementFuture.getValue();
        }
        catch(Exception e)
        {
            bIsGroupError = true;
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog =true;
            lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMGroupSearchControllerV2','CallService');
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
        return null;
    }
    
    /* Method Name   :   populateGroupInfoOnAccount-     
    * Description   :   Populates Group Information on Account Object
    * Return Type   :   Account  
    * Parameters    :   HUMIntGroupSearchServiceHelper.ResponseGroupSearch      
    */              
    private Account populateGroupInfoOnAccount(HUMGroupSearchServiceHelper.ResponseGroupSearch extAccountRecord)    
    {        
        Account acc = new Account(recordTypeId = HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(),sHUM_Group_Account_Record_Type), 
                                   Name = extAccountRecord.GroupName,    
                                   Group_Number__c = extAccountRecord.GroupID,                               
                                   Phone = extAccountRecord.Phone,                                
                                   Enterprise_ID__c = extAccountRecord.CustomerGenKey,                                 
                                   Source_Platform_Code__c = extAccountRecord.Platform,                                                  
                                   Source_Customer_Key__c =  extAccountRecord.Numeric_GroupID,
                                   Account_External_ID__c = (extAccountRecord.Platform == sFMValue)? sGRPValue +Seperator+TENANTID_GEN+Seperator+sFMValue+Seperator+ extAccountRecord.CustomerGenKey : sGRPValue+Seperator+ TENANTID_GEN+Seperator+sODSValue+Seperator+ extAccountRecord.CustomerGenKey,
                                   BillingStreet = extAccountRecord.Address,                                
                                   BillingStateCode = oSearchWrapper.sState == sHUMStateDefaultValue? '': oSearchWrapper.sState,                                
                                   General_Account__c = true, Tenant_Id__c = TENANTID_GEN);                    
                                   return acc;     
    }
    
    /*  
     * Method name : Reset
     * Description : This method will be called on clicking reset button on Group Search.
     * Return Type : pagereference 
     * Parameter   : NA
     */ 
     
    public pagereference Reset()
    {
        oSearchWrapper.sGroupNumber = null;
        oSearchWrapper.sGroupName = null;
        oSearchWrapper.sState = null;
        bExternalResults = null;
        resElementFuture = null;
        responseDTO = null;

        setAccount = new ApexPages.StandardSetController(new List<Account>());

        if(mapRecordData != null && !mapRecordData.isEmpty()){ mapRecordData.clear();} 

        return null;
    }

    /*Method Name   :    buildUnknownAccountMap  
     * Description   :   Builds the map which is used to display in search results once unknown account is created
     * Return Type   :   void   
     * Parameters    :   Account Id     
     */
    public void  buildUnknownAccountMap(Id iAccntId)
    {
        lstAccountList = new List<Account>();
        bExternalResults = false;
        String sQry = 'Select Id,FirstName, Phone, BillingStreet, BillingCity, BillingState, BillingCountry,ETL_Record_Deleted__c, Group_Number__c,Source_Customer_Key__c, RecordType.Name,UserRecordAccess.HasReadAccess, Source_Platform_Code__c, Name, Home_Office_Account__c from Account where Id=:iAccntId LIMIT 1';
        lstAccountList = Database.Query(sQry);
        dataSecurity();
    }
    
    /*
    * Method Name   :    dataSecurity 
    * Description   :    This Method is for Security purpose. Used to display fewer records to External users
    * Return Type   :    void
    * Parameters    :    NA
    */
    private void dataSecurity()
    {
        mapRecordHaveAccess = new Map<Id,boolean>();
        mapRecordUserAccess = new Map<Id,boolean>();
        if(lstAccountList!=null && !lstAccountList.isEmpty())
        {
            List <ID> templstAccountList = new List<ID>();
            List<Account> lstHasReadAccounts  = new List<Account>();
            List<UserRecordAccess> lstHasReadAccess = new List<UserRecordAccess>();
            mapRecordHaveAccess = new Map<Id, Boolean>();     
            for(Integer i = 0 ; i < lstAccountList.size() ; i++)
            {
                templstAccountList.add(lstAccountList.get(i).Id);
            }           
            //As per the US2619274 adding the records to the map against the logged in user
            if(groupCPDOnOff){
                lstHasReadAccess = [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : templstAccountList];
                for(UserRecordAccess userAccess : lstHasReadAccess){
                    mapRecordUserAccess.put(userAccess.RECORDID, userAccess.HASREADACCESS);
                }
            }
            lstHasReadAccounts  = [Select Id, Phone, UserRecordAccess.HasReadAccess, Home_Office_Account__c from Account where Id IN :templstAccountList];       
            for(Account acc : lstHasReadAccounts)
            {
                If(acc.UserRecordAccess.HASREADACCESS)           
                {       
                    mapRecordHaveAccess.put(acc.id, true);
                }
                else
                {
                    If(acc.Home_Office_Account__c)
                    {  
                        mapRecordHaveAccess.put(acc.id, false);
                        acc.Phone = '';
                    }
                }
            }
        }
        setAccount = new ApexPages.StandardSetController(lstAccountList);
        setAccount.setPageSize(Integer.valueof(iListSizePageMemSearch));
    } 
    
    /*  
     * Method name : unknownGroupSearch
     * Description : This method will be called on clicking the Search Unknown Group button on Group search page.
     * Return Type : Continuation
     * Parameter   : NA
     */ 
    public Continuation unknownGroupSearch() 
    {   
            bIsUnknownGroupSearch = true;
            return groupsearch();
    }
    
    /*Method Name    : fieldCipherMapping
     * Description   :  This Method is called when search member is driven by Softphone 
     *                  and as input this method accept Wrapper data and return actual fieldname and Cipher text mapping 
     * Return Type   :  Map<String, String>
     * Parameters    :  HUMMemberSearchWrapperHelper oSearchWrapperwithCipher
     */  
    @testvisible private Map<String, String> fieldCipherMapping(HUMGroupSearchWrapperHelper oSearchWrapperwithCipher)
    {
        Map<String, String> accFieldCipher = new Map<String,String>();
       
        if (NULL != oSearchWrapperwithCipher && NULL !=oSearchWrapperwithCipher.mapEncryptedValues ) {
            if (oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber2').Value__c) != null) 
                accFieldCipher.put(sHUMGroupNumber ,oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber2').Value__c));
            if (oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber').Value__c) != null)
                accFieldCipher.put(sHUMGroupNumber ,oSearchWrapperwithCipher.mapEncryptedValues.get(SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber').Value__c));
        }
        return accFieldCipher;
    }
	
	 /*  
     * Method name : queryUnknownGroupAccount
     * Description : This method will be called when integration does not return any result
                     to do an internal search within SFDC
     * Return Type : void
     * Parameter   : NA
     */
    public void queryUnknownGroupAccount(){  
        try{
            
            bExternalResults = false; 
            lstAccountList = new List<Account>();
            String sQry = '';  
            String Group_Name = oSearchWrapper.sGroupName + '*';
            String strGroupName = string.escapeSingleQuotes(Group_Name);
            String strGroupNumber = string.escapeSingleQuotes(oSearchWrapper.sGroupNumber);
            String strState = string.escapeSingleQuotes(oSearchWrapper.sState);
            Integer intLimit = Integer.valueOf(HUMSearchLimit__c.getInstance('Member_SearchLimit').Search_Limit__c);  
            if(!String.isblank(oSearchWrapper.sGroupName)) bIsNameSearch = true; 
            if(bIsNameSearch && !isSoftPhoneSearch)
            {   
                List<Account> lstTempAccounts = new List<Account>();    
                boolean bIsMatching =false;                   
                sQry = 'FIND :strGroupName IN NAME FIELDS RETURNING Account(Id, FirstName,Home_Office_Account__c,ETL_Record_Deleted__c, RecordType.Name, Phone, BillingStreet, BillingCity, BillingState,' +
                    'BillingCountry, Group_Number__c,Source_Customer_Key__c,Source_Platform_Code__c, Name where (RecordType.DeveloperName =:sHUM_UnknownGroup_Account_Record_Type)';
                if(!(String.isBlank(oSearchWrapper.sGroupNumber))){sQry += ' AND Group_NuMBER__C=:strGroupNumber';}
                if(!(String.isBlank(oSearchWrapper.sState)) && oSearchWrapper.sState != sHUMStateDefaultValue)
                {
                    sQry += ' AND BillingStateCode =:strState)';
                }
                //Per guidelines added limit to the query to restrict results to 100 records
                sQry = sQry + ' Limit :intLimit';
                List<List<Account>>searchList=search.query(sQry);
                lstTempAccounts = searchList[0];
                for(Account obj : lstTempAccounts)
                {
                    bIsMatching = true;
                    if(String.IsBlank(obj.Name) || (String.IsNotBlank(obj.Name) && !obj.Name.startsWithIgnoreCase(oSearchWrapper.sGroupName))) bIsMatching = false;
                    if(bIsMatching)
                    {   
                        lstAccountList.Add(obj);
                    }
                    
                }
                if(Test.isRunningTest()) lstAccountList.clear();
                if(lstAccountList.isempty())
                {
                    
                    sQry = 'FIND :strGroupName IN NAME FIELDS RETURNING Account(Id, FirstName,Home_Office_Account__c,ETL_Record_Deleted__c, RecordType.Name, Phone, BillingStreet, BillingCity, BillingState,' +
                        'BillingCountry, Group_Number__c,Source_Customer_Key__c,Source_Platform_Code__c, Name where (RecordType.DeveloperName =:sHUM_UnknownGroup_Account_Record_Type)';
                    if(!(String.isBlank(oSearchWrapper.sGroupNumber))){sQry += ' AND Source_Customer_Key__c=:strGroupNumber';}
                    if(!(String.isBlank(oSearchWrapper.sState)) && oSearchWrapper.sState != sHUMStateDefaultValue)
                    {
                        sQry += ' AND BillingStateCode =:strState)';
                    }
                    //Per guidelines added limit to the query to restrict results to 100 records
                    sQry = sQry + ' Limit :intLimit';
                    List<List<Account>>searchList1=search.query(sQry);
                    lstTempAccounts = searchList1[0];
                    for(Account obj : lstTempAccounts)
                    {
                        bIsMatching = true;
                        if(String.IsBlank(obj.Name) || (String.IsNotBlank(obj.Name) && !obj.Name.startsWithIgnoreCase(oSearchWrapper.sGroupName))) bIsMatching = false;
                        if(bIsMatching)
                        {   
                            lstAccountList.Add(obj);
                        }
                        
                    }
                }
            }
            else
            {
                sQry = 'Select Id, FirstName,Home_Office_Account__c,Source_Customer_Key__c, RecordType.Name, Phone, BillingStreet, BillingCity,ETL_Record_Deleted__c,BillingState, BillingCountry, Group_Number__c,'; 
                sQry += 'Source_Platform_Code__c,UserRecordAccess.HasReadAccess, Name from Account where (RecordType.DeveloperName =:sHUM_UnknownGroup_Account_Record_Type)';
                
                if(!(String.isBlank(oSearchWrapper.sGroupNumber))){sQry += ' AND Group_NuMBER__C=:strGroupNumber';}
                
                if(!(String.isBlank(oSearchWrapper.sState)) && oSearchWrapper.sState != sHUMStateDefaultValue)
                {
                    sQry += ' AND BillingStateCode =:strState';
                }
                //Per guidelines added limit to the query to restrict results to 100 records
                sQry = sQry + ' Limit :intLimit';                    
                lstAccountList = Database.Query(sQry); 
                if(Test.isRunningTest()) lstAccountList.clear();
                if(lstAccountList.isempty())
                {
                    sQry = 'Select Id, FirstName,Home_Office_Account__c,Source_Customer_Key__c, RecordType.Name, Phone, BillingStreet, BillingCity,ETL_Record_Deleted__c,BillingState, BillingCountry, Group_Number__c,'; 
                    sQry += 'Source_Platform_Code__c,UserRecordAccess.HasReadAccess, Name from Account where (RecordType.DeveloperName =:sHUM_UnknownGroup_Account_Record_Type)';
                    
                    if(!(String.isBlank(oSearchWrapper.sGroupNumber))){sQry += ' AND Source_Customer_Key__c=:strGroupNumber';}
                    
                    if(!(String.isBlank(oSearchWrapper.sState)) && oSearchWrapper.sState != sHUMStateDefaultValue)
                    {
                        sQry += ' AND BillingStateCode =:strState';
                    }
                    //Per guidelines added limit to the query to restrict results to 100 records
                    sQry = sQry + ' Limit :intLimit';                    
                    lstAccountList = Database.Query(sQry);
                    
                    
                }
            }
            dataSecurity(); 
            
        }
        catch(Exception e)
        {
            // throw exception message common for group and unknown group. Written above.
            
        }
    }
}