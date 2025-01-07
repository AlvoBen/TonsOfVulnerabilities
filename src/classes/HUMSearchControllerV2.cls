/*******************************************************************************************************************************
Apex Class Name : HUMSearchControllerV2 
Version         : 1.0
Created On      : 05/21/2014
Function        : This Class serves as a controller for Main search page. It coordinates data flow between 
                  main search page as well as individual search component and Interaction Log.
Test Class      : HUMSearchControllerV2Test
Modification Log: 
* Developer Name           Code Review                      Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya          25261                      07/31/2015                  Removed abstraction layer - extending HUMSearchFieldHelper
* Vishal Verma                                        11/09/2015                  REQ - 237001 : Removed Interaction Encrytion
* Harshith Mandya                                     02/10/2016                  Defect fix #215717.0001
* Cody Sanders                                        01/26/2017                  REQ - 298750 :- Inbound Call by default on Interaction Log
* Subhamay Mandal                                     02/13/2017                  REQ - 301404 : Close all console tabs and see only blank search page when click on reset all
* Subhamay Mandal                                     02/17/2017                  REQ - 300133 : add new populateInteractionWithAndAboutDetails to merge Interaction With, About and open Account detail page
* Prafull Verma         84636                         04/05/2017                  REQ - 306322 : SF - TECH - Softphone routing optimization
* Amar Gadewar                                        05/11/2017                   REQ - 313446 :- Populating the interaction origin as inbound call by default(CRM)
* Mohammed Noor         96594                         05/18/2017                  REQ - 311432 Softphone / CRM Results for All Family Members by searching using Member Id only and launch optimization
* Prafull Verma         118513                        08/09/2017                 REQ - 326174 Production issue in Provider Search for NPIID
* Mohammed Noor         125478                        09/05/2017                 Fix for Defect# 313958
* Mohammed Noor         126055                        09/06/2017                 Fix for Defect # 313759.
* Pallavi R             201881                        03/20/2018                 REQ - 357639 SF - TECH- MA Post Search Functionality
* Pallavi R                                           06/05/2018                 REQ - 363592 - Rollback Resolution Search tab Changes
* Santhi Mandava        262341                        07/26/2018                 Implemented platform encryption for account fields.
* Mukul Kumar                                          09/27/2018                 REQ - 373386 - Display Password Modifications
* Isha Gupta                                          03/29/2021                 Added IVR data in Interaction Record
* Moshitha Gunasekaran                                04/20/2021                 US#2163329 - Removed PopulateUnknownMemberDetails method for checkmarx issues
* Ajay Chakradhar                                     05/31/2021                 REQ - 1755482 - Replacing constants to improve performance
* Navajit Sarkar/Sahil Verma                          07/12/2021                 User story -2287945 : Adding Authentication Data in the Interaction Record
* Ankit A                                             08/10/2021                 User story -2153743 : Adding Get Interaction Button in the Interaction Log Section and Fetching the Latest Interactions
* Ankit Avula                                         09/13/2021                 US-2672878 - T1PRJ0003278 - MF02 - Adding Logic to Get Interaction button on Interaction log section
* Sahil Verma                                         09/02/2021                 US-2230000 - Save and Continue button Functionality and Interaction auto population functionality added
* Ajay Chakradhar                                     11/22/2021                 DEF 4182 FIX - Global Constant replace for performance improvement
* Ajay Chakradhar                                     08/17/2022                 USER STORY 2860577 : T1PRJ0100059 - MF 3 - Agnostic Customer Record Type
* Harshada Kamble/Anil Pavithran                      03/09/2023                 US 2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
*********************************************************************************************************************************************************************************************/
public with sharing class HUMSearchControllerV2 
{ 
    Private String TAB_NAME_MEMBER = 'MemberSearch';
    Private String TAB_NAME_ENROLLMENT = 'SearchEnrollment';
    Private String SEARCH_MEMBER = 'Member';
    Private String SEARCH_PROVIDER = 'Provider';
    Private String SEARCH_GROUP = 'Group';
    Private String SEARCH_AGENCY = 'Agency';
    Private String SEARCH_AGENT = 'Agent';
    Private String SEARCH_ENROLLMENT = 'Enrollment';
    Private String INTWITHFNAME = 'iWithFirstName';
    Private String INTABOUTTYPE = 'iAboutType';
    Private String INTWITHTYPE = 'iWithType';
    Private String FIRSTNAME = 'firstName';
    Private String LASTNAME = 'lastName';
    Private String CALLTYPE = 'callerType';
    Private String UNIQUEID = 'uuID';
    Private String INTABTIDSUFFIX = 'sIntAboutSuffixId';
    Private String INTWITHIDSUFFIX = 'sIntWithSuffixId';
    Private String INTERACTIONID = 'interactionId'; 
    Private String SURVEYID = 'surveyId'; 
    Private String CALL_CATEGORY_DESC = 'callCategoryDesc';
    Private String AMP = '&';
    Private String EQUAL = '=';
    Private String UNDEFINED = 'undefined';
    Private String MEMBER = 'Member';
    Private String AUTHINDICATOR_CONST = 'authIndicator'; // US#2287945 added new  constant
    Private String autoInteractionIDvalue = 'autoInteractionIDvalue';
   
    private static final Map<String,String> mapAgentCodes = new Map<String,String>
                                                            {
                                                                'Agency' => 'Agent',
                                                                'Provider' => 'Provider',
                                                                'Group' => 'Group',
                                                                'Member' => 'Member',
                                                                'Agnostic Customer' => 'Agnostic Customer'
                                                            };
    public static boolean bPopup{get; set;}
    private set<String> searchTypes = new set<String>();
    //Private Map to Store type of of Search for Soft Phone Routing
    private static final Map<String,String> mapSoftPhoneSearchCodes = new Map<String,String> 
    {	'M' => 'Member','P' => 'Provider','A' => 'Agency','G' => 'Group',
        'E' => 'Group','UM' => 'Member','UP' => 'Provider','UA' => 'Agency',
        'UG' => 'Group','UE' => 'Group',null => ''
    };
    //Private Map to Store codes for Tab to be focussed 
    private static final Map<String,String> mapSoftPhoneFocusCodes = new Map<String,String> 
    {
        'M' => 'MemberSearch','P' => 'ProviderSearch','A' => 'AgencyBrokerSearch','G' => 'GroupSearch',
        'E' => 'GroupSearch','UM' => 'MemberSearch','UP' => 'ProviderSearch','UA' => 'AgencyBrokerSearch',
        'UG' => 'GroupSearch','UE' => 'GroupSearch',null => 'MemberSearch'
    };

    //Place holder for SoftPhone Data
    private HUMSoftPhoneRoutingHelper softPhoneWrapper = new HUMSoftPhoneRoutingHelper();

    //Storing record Id of record selected on the search page
    public string sResultRecordId = '';

    //To check if a user is a genesys user or not
    public Boolean isGenesysUser{get; set;}

    /*
     * Method Name   :   HUMSearchControllerV2
     * Description   :   Constructor - Initialize Variables 
     * Return Type   :   void
     * Parameters    :   NA
     */
    public HUMSearchControllerV2()
    {
       initializeVariables();
    }
    public String searchTypesJson {get;set;}
    public Interaction__c ofetchInteraction{get;set;}
    //Store the reference to Interaction Log controller
    public transient HUMInteractionLogComponentControllerV2 interactionLogController {get;set;}
    //Interaction with Salesforce ID from Page
    public transient ID strInteractionWithID{get;set;}
    // Intereaction About Salesforce ID from Page
    public transient ID strInteractionAboutID{get;set;}
    // Search Conext Name - Member , Group,Agent , Employee etc
    public transient String strSearchContextName{get;set;}
    // Check if Data is from External System.Set to true if yes
    public transient boolean bIsDataFromExternalSystem {get;set;}
    /*JSON version of the data retrieved from External system. This Data will be later seriazed 
      into respective Salesforce Class type
     */
    public transient String strJsonAccObject {get;set;}
    // Interaction Salesforce ID when Link is clicked on the individual search component page
    public transient ID strInteractionID {get;set;}
    // Store the Individual Component References in the map
    public Map<String, HUMSearchComponentBaseHelperV2> searchControllerMap {get;set;}
    // Store the Base Component References in the map
    public transient HUMSearchComponentBaseHelperV2 providerReference {get;set;}
    // Tab to be focussed
    public String sTabToBeFocussed{get; set;}
    //Store SoftPhone Json String
    public string transferJsonObject {get;set;}
    //Store the Member ID
    public String sMemberID{get; set;}
    //Unknown Account ID
    public transient Id idUnknownAccount {get;set;}
    //Store Serialized version of List of Consumer ID SObjects
    public transient String sConsumerIDList {get;set;}
    //Identify Member Account type
    public String sMemberAccType {get;set;}
    public String  mapRecordHaveAccess{get;set;}
    public Boolean Switch_2230000 {
        get{
           return  PhoneBook_H_HUM.isCRMFunctionalityON('2230000');
        }set;
    }
    
     //Variables for acknowledge popup values 
    Public Boolean bAckDisplayPopup {Get; Set;}
    Public String pass{Get;Set;} 
    Public String secQuestion {Get;Set;}
    Public String secAns {Get;Set;}
    Public String sInteractionAbout{Get;Set;}
    public boolean showCallCatergoryAlert {get;set;}
    //US#2287945 variable to work with authentication checkbox
    public boolean authIndicator{Get;Set;}
    public boolean gnaCallCategoryMessageOnOff = HUMUtilityHelper.isCRMFunctionalityON('2082644');
    public boolean stopPopulateInt {get;set;}
    public boolean hideGetInteraction {get;set;}
    
    /*
     * Method Name   :   setComponentControllerMap
     * Description   :   Store all the search component references . This will be called by respective components   
     * Return Type   :   void
     * Parameters    :   String , HUMSearchComponentBaseHelperSearchUpdate
     */
    public void setComponentControllerMap(String key, HUMSearchComponentBaseHelperV2 compController)
    {         
        if(searchControllerMap == null) searchControllerMap = new Map<String, HUMSearchComponentBaseHelperV2>();
        searchControllerMap.put(key,compController); 
    }

    /*
     * Method Name   :    initializeVariables
     * Description   :    Initialize Variables
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void initializeVariables() 
    {
        string sessionId = 'HUMSearchV2'+UserInfo.getUserId();
        stopPopulateInt = false;
        hideGetInteraction = false;
        if (Cache.Session.contains(sessionId)) {
        	hideGetInteraction = true;
        }
        sTabToBeFocussed = TAB_NAME_MEMBER;
        isGenesysUser = PhoneBook_H_HUM.isGenesysUser() && PhoneBook_H_HUM.isCRMFunctionalityON('2357061');
    } 

    /*
     * Method Name   :    switchTab 
     * Description   :    Set the tab ID to be focussed
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void switchTab() 
    {
        sTabToBeFocussed = TAB_NAME_ENROLLMENT;
    }

    /*
     * Method Name   :    getThis 
     * Description   :    Store the Current Page Instance
     * Return Type   :    HUMSearchController
     * Parameters    :    NA
     */
    public HUMSearchControllerV2 getThis() 
    {
        return this;
    }

    /*
     * Method Name   :    updateAuth()
     * Description   :    This method is called to update the authCheck variable.   
     * return Type   :    Void
     * Parameters    :    None
     */
    public void updateAuthCheck(){
        //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
        if(interactionLogController.multiMemberAuthSwitch)
        {
            if(interactionLogController.oHUMIntMembers.Authenticated__c == true)interactionLogController.authCheck=true;
            else interactionLogController.authCheck=false; 
        }
        else
        {
            if(interactionLogController.oFetchInteraction.Authenticated__c == true)interactionLogController.authCheck=true;
            else interactionLogController.authCheck=false; 
        }
    }
    
    

    /*
     * Method Name   :    populateInteractionWithDetails
     * Description   :    Populate Interaction with Details 
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void populateInteractionWithDetails()
    {
        //Check If data is From External System
        Id memberID = getMemberDetailsFromExternalSystem();
        //Get the Salesforce ID from the Page and Pass on to the Interaction Log Controller if data is not from External system
        if(memberID != null)
        {
            
            interactionLogController.populateInteractionWithDetails(memberID,mapAgentCodes.get(strSearchContextName));
        }
        else    
        {
            interactionLogController.populateInteractionWithDetails(strInteractionWithID,mapAgentCodes.get(strSearchContextName));
        }
    }

    /*
     * Method Name   :    populateInteractionAboutDetails
     * Description   :    Populate Interacting About Details
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void populateInteractionAboutDetails() 
    {   
        
        
        //Check If data is From External System
        Id memberID = getMemberDetailsFromExternalSystem();
        //Get the Salesforce ID from the Page and Pass on to the Interaction Log Controller if data is not from External system
        if(memberID != null)
        {
            interactionLogController.populateInteractionAboutDetails(memberID,mapAgentCodes.get(strSearchContextName));
        }
        else
        {
            interactionLogController.populateInteractionAboutDetails(strInteractionAboutID,mapAgentCodes.get(strSearchContextName));
        }
        updateAuthCheck(); //US#2287945 update the authcheck variable to update the UI
    }

    /*
     * Method Name   :    populateInteractionDetails 
     * Description   :    Populate Complete Interaction details
     * Return Type   :    void
     * Parameters    :    NA
     */  
    public void populateInteractionDetails() 
    {
        //Check If data is From External System
        Id memberID = getMemberDetailsFromExternalSystem();
        //Get the Salesforce ID from the Page and Pass on to the Interaction Log Controller if data is not from External system
        if(memberID != null)
        {
            interactionLogController.populateInteractionDetails(memberID,mapAgentCodes.get(strSearchContextName));
        }
        else
        {
            interactionLogController.populateInteractionDetails(strInteractionID,mapAgentCodes.get(strSearchContextName));
        }
        updateAuthCheck(); //US#2287945 update the authcheck variable to update the UI
    }

    /*
     * Method Name   :    getMemberDetailsFromExternalSystem 
     * Description   :    Get the ids if data is from External System .Route the logic based on the type of Search
     * Return Type   :    ID
     * Parameters    :    NA
     */           
    public Id getMemberDetailsFromExternalSystem() 
    { 
        try 
        {
            if(bIsDataFromExternalSystem != null && bIsDataFromExternalSystem)
            {               
                /*Check the Context of Search and Call relevant method to upsert data into Salesforce 
                            Data is passed as a JSON String which is later deserialized into specific search context data Type
                 */
                if(strSearchContextName == SEARCH_PROVIDER)
                {
                    return getProviderIDAfterUpsert();
                }
                else if(strSearchContextName == SEARCH_GROUP)
                {
                    return getGroupIDAfterupsert();
                }
                else if(strSearchContextName == SEARCH_AGENCY  || strSearchContextName ==  SEARCH_AGENT)
                { 
                    return getAgentIDAfterUpsert();
                }
                else if(strSearchContextName == SEARCH_MEMBER)
                { 
                    return getMemberIDAfterUpsert();
                }
            } 
        }
        catch(Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchControllerV2', 'getMemberDetailsFromExternalSystem');
        }
        return null;
    }

    /*
     * Method Name   :    getProviderIDAfterUpsert
     * Description   :    Get the provider Account ID after upserting data sent from external system
     * Return Type   :    ID
     * Parameters    :    NA
     */     
    private ID  getProviderIDAfterUpsert() 
    {
        HUMProviderSearchControllerV2 providerSearchController = (HUMProviderSearchControllerV2)searchControllerMap.get(SEARCH_PROVIDER);
        //Get Encrypted Tax Id values before upsert of Account
        Map<String,String> mapNormalAndEncryptValues = new  Map<String,String>();
        if(String.IsNotBlank(sConsumerIDList))
        {                               
            List<String> lstTaxID = sConsumerIDList.split(',');
            for(integer i=0 ; i < lstTaxID.size() ; i++)
            {
                mapNormalAndEncryptValues.put(lstTaxID[i].trim(),lstTaxID[i].trim());
            }
        }
        ID idProvider = providerSearchController.insertDataFromExternalSystem(strJsonAccObject);
        if(idProvider == null) 
        {
            providerSearchController.bIsProviderError = true;
        }
        else
        {
            //Upsert Consumer Tax Ids
            boolean isSuccess = providerSearchController.upsertConsumerTaxIds(idProvider,mapNormalAndEncryptValues);
            if(!isSuccess) providerSearchController.bIsProviderError=true;
        }
        return idProvider; 
    }

    /*
     * Method Name   :    getGroupIDAfterUpsert
     * Description   :    Get the Group Account ID after upserting data sent from external system
     * Return Type   :    ID
     * Parameters    :    NA
     */     
    private ID getGroupIDAfterUpsert() 
    {
        HUMGroupSearchControllerV2 groupController = (HUMGroupSearchControllerV2)searchControllerMap.get(SEARCH_GROUP);
        ID idGroup = groupController.insertDataFromExternalSystem(strJsonAccObject);
        if(idGroup == null) groupController.bIsGroupError = true;
        return idGroup; 
    }


    /*
     * Method Name   :    getAgentIDAfterUpsert
     * Description   :    Get the Agent Account ID after upserting data sent from external system
     * Return Type   :    ID
     * Parameters    :    NA
     */     
    private ID  getAgentIDAfterUpsert() 
    {
      HUMAgencySearchControllerV2 agencyController = (HUMAgencySearchControllerV2)searchControllerMap.get(SEARCH_AGENCY);
        //Get Encrypted Tax Id values before upsert of Account
        Map<String,String> mapNormalAndEncryptValues = new  Map<String,String>();

        if(!String.isEmpty(sConsumerIDList))
        {                               
            List<String> lstTaxID = sConsumerIDList.split(',');
            for(integer i=0 ; i < lstTaxID.size() ; i++)
            {
                mapNormalAndEncryptValues.put(lstTaxID[i].trim(),lstTaxID[i].trim());
            }
        }

        ID idAgency = agencyController.insertDataFromExternalSystem(strJsonAccObject);
        if(idAgency == null) 
        {
            agencyController.bIsAgencyError = true;
        }
        else
        {
            //Upsert Consumer Tax Ids
            boolean isSuccess = agencyController.upsertConsumerTaxIds(idAgency,mapNormalAndEncryptValues);
            if(!isSuccess) agencyController.bIsAgencyError = true;
        }
        return idAgency; 
    }

    /*
     * Method Name   :    getMemberIDAfterUpsert
     * Description   :    Get the Member Account ID after upserting data sent from external system
     * Return Type   :    ID
     * Parameters    :    NA
     */     
    private ID  getMemberIDAfterUpsert() 
    {       
        HUMMemberSearchEnrollmentControllerV2 memberEnrollmentController = (HUMMemberSearchEnrollmentControllerV2)searchControllerMap.get(SEARCH_ENROLLMENT);
        ID idMember = memberEnrollmentController.insertDataFromExternalSystem(strJsonAccObject);
        
        if(idMember == null) memberEnrollmentController.bIsEnrollmentError = true;
        return idMember;
        
        return null;
    }

    /*
     * Method Name   :    softPhoneRoutingSearch 
     * Description   :    Do Auto search based on the parameters passed from Soft phone
     * Return Type   :    void
     * Parameters    :    NA
     */ 
    public void softPhoneRoutingSearch() 
    {   
        
        try  
        {               
            if(!String.ISBLANK(transferJsonObject)) {
                string sessionId = 'HUMSearchV2'+UserInfo.getUserId();
                if(transferJsonObject.contains(system.label.SoftphoneURLContains)){
                    hideGetInteraction = true;
                    Cache.Session.put(sessionId, '');
                }
                else if (Cache.Session.contains(sessionId)) {
                    hideGetInteraction = true;
                }
                //Deserialize JSon String to Key Value Pairs of URL Parameters
                Map<String,String> mapSoftPhoneParameters = getURLKeyValuePairs(transferJsonObject); 
                softPhoneWrapper.mapfieldCipherMapping = new Map<String,String>();
                softPhoneWrapper.mapEncryptedValues = new Map<String,String>();  
                //mapSoftPhoneParameters.get(INTWITHFNAME);
                if(mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance( 'FirstName').Value__C)) softPhoneWrapper.mapfieldCipherMapping.put(SOFTPHONE_PRPID_HUM__c.getInstance( 'FirstName').Value__C, mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'FirstName').Value__C)); 
                  softPhoneWrapper.sIntAboutFirstName =mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'FirstName').Value__C);
                                  
                if(mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('LastName').Value__C)) softPhoneWrapper.mapfieldCipherMapping.put(SOFTPHONE_PRPID_HUM__c.getInstance( 'LastName').Value__C,  mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('LastName').Value__C));
                  softPhoneWrapper.sIntAboutLastName = mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('LastName').Value__C);
                if(mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('MemberId').Value__C)) softPhoneWrapper.mapfieldCipherMapping.put(SOFTPHONE_PRPID_HUM__c.getInstance( 'MemberId').Value__C,  mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('MemberId').Value__C));
                   softPhoneWrapper.sIntAboutID = mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('MemberId').Value__C);
                if(mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('DOB').Value__C))      softPhoneWrapper.mapfieldCipherMapping.put(SOFTPHONE_PRPID_HUM__c.getInstance( 'DOB').Value__C,  mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('DOB').Value__C));
                   softPhoneWrapper.sIntAboutDOB = mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('DOB').Value__C);
                
                softPhoneWrapper.sIntAboutType = mapSoftPhoneParameters.get(INTABOUTTYPE);
                
                if(mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('FirstName2').Value__C)) softPhoneWrapper.mapfieldCipherMapping.put(SOFTPHONE_PRPID_HUM__c.getInstance( 'FirstName2').Value__C,  mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('FirstName2').Value__C)); 
                    softPhoneWrapper.sIntWithFirstName = mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('FirstName2').Value__C);
                if(mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('LastName2').Value__C)) softPhoneWrapper.mapfieldCipherMapping.put(SOFTPHONE_PRPID_HUM__c.getInstance( 'LastName2').Value__C,  mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('LastName2').Value__C));
                    softPhoneWrapper.sIntWithLastName = mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('LastName2').Value__C);
                if(mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('MemberId2').Value__C)) softPhoneWrapper.mapfieldCipherMapping.put(SOFTPHONE_PRPID_HUM__c.getInstance( 'MemberId2').Value__C,  mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('MemberId2').Value__C));
                    softPhoneWrapper.sIntWithID = mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('MemberId2').Value__C);
                if(mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('DOB2').Value__C)) softPhoneWrapper.mapfieldCipherMapping.put(SOFTPHONE_PRPID_HUM__c.getInstance( 'DOB2').Value__C,  mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('DOB2').Value__C)); 
                   softPhoneWrapper.sIntWithDOB = mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('DOB2').Value__C);
                //US 2082644 start logic Added the logic for Call Category and process for GNA and PermissionSet
                if(mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance( 'CallCategory').Value__C)) softPhoneWrapper.mapfieldCipherMapping.put(SOFTPHONE_PRPID_HUM__c.getInstance( 'CallCategory').Value__C, mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'CallCategory').Value__C)); 
                  softPhoneWrapper.sCallCategory =mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'CallCategory').Value__C);
                if(softPhoneWrapper.sCallCategory == System.Label.US2082644GNACallCategoryLabel && gnaCallCategoryMessageOnOff && UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(System.Label.US2082644_CRMS_690_PS_Label)){
                    showCallCatergoryAlert = true;
                }
                //US 2082644 end logic
                
                //Agency
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('AccountName').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('AccountName').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('AccountName').Value__c));
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('AccountName2').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('AccountName2').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('AccountName2').Value__c));
                
                //Group
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber').Value__c));
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber2').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber2').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber2').Value__c));
                
                //Provider
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName').Value__c));
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName2').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName2').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName2').Value__c));
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__c));
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c));
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId').Value__c));
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__c));
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('NpiId').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('NpiId').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('NpiId').Value__c));
                if (mapSoftPhoneParameters.containsKey(SOFTPHONE_PRPID_HUM__c.getInstance('NpiId2').Value__c)) softPhoneWrapper.mapEncryptedValues.put(SOFTPHONE_PRPID_HUM__c.getInstance('NpiId2').Value__c,mapSoftPhoneParameters.get(SOFTPHONE_PRPID_HUM__c.getInstance('NpiId2').Value__c));
                             
                
                softPhoneWrapper.sIntWithType = mapSoftPhoneParameters.get(INTWITHTYPE);
                softPhoneWrapper.sInteractionFirstName = mapSoftPhoneParameters.get(FIRSTNAME);
                softPhoneWrapper.sInteractionLastName = mapSoftPhoneParameters.get(LASTNAME);
                softPhoneWrapper.sInteractionCallType = mapSoftPhoneParameters.get(CALLTYPE);
                softPhoneWrapper.sInteractionUUID = mapSoftPhoneParameters.get(UNIQUEID);
                softPhoneWrapper.sIntAboutSuffixId = mapSoftPhoneParameters.get(INTABTIDSUFFIX);
                softPhoneWrapper.sIntWithSuffixId = mapSoftPhoneParameters.get(INTWITHIDSUFFIX);
                softPhoneWrapper.sInteractionId = mapSoftPhoneParameters.get(INTERACTIONID);
                softPhoneWrapper.sSurveyId = mapSoftPhoneParameters.get(SURVEYID); 
                softPhoneWrapper.sCallCatDesc = mapSoftPhoneParameters.get(CALL_CATEGORY_DESC);
		        softPhoneWrapper.sAuthIndicator = mapSoftPhoneParameters.get(AUTHINDICATOR_CONST)=='Y'?true:false; //US#2287945 set the wrapper variable as per the authentication indicator
                softPhoneWrapper.autoInteractionId = mapSoftPhoneParameters.get(autoInteractionIDvalue); //US#2230000 Auto Interaction creation and population
          
                searchTypes = new Set<String>();
                //Route the business logic based on the URL Parameters 
                routeSearchLogic(mapSoftPhoneParameters);
                searchTypesJson = Json.serialize(searchTypes);

                //Populate Interaction Details .Exeptions are handled by this methods catch block.
               if(stopPopulateInt == false){
                populateSoftPhoneInteractionDetails();
            }
        }
          }
        catch(Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchControllerV2', 'softPhoneRoutingSearch');
        }   
        
        
    }
 
    /*
     * Method Name   :    routeSearchLogic 
     * Description   :    Route the logic to the respective method based on the search type
     * Return Type   :    void
     * Parameters    :    NA
     */ 
    private void routeSearchLogic(Map<String,String> mapSoftPhoneParameters)
    {

        if(!String.ISBLANK(softPhoneWrapper.sIntAboutType) || !String.ISBLANK(softPhoneWrapper.sIntWithType)) 
        {
            //Tab to be focussed
            String getTabName = !String.ISBLANK(softPhoneWrapper.sIntWithType) ? softPhoneWrapper.sIntWithType : softPhoneWrapper.sIntAboutType;
            stabTobeFocussed = !String.ISBLANK(getTabName) ? mapSoftPhoneFocusCodes.get(getTabName) : TAB_NAME_MEMBER;                
            softPhoneWrapper.sIntWithType = mapSoftPhoneSearchCodes.get(softPhoneWrapper.sIntWithType);
            softPhoneWrapper.sIntAboutType = mapSoftPhoneSearchCodes.get(softPhoneWrapper.sIntAboutType);

            //Do Auto Search based on Search Type . Exceptions are handled by this methods catch block    
            if(softPhoneWrapper.sIntAboutType.equals(SEARCH_MEMBER) || softPhoneWrapper.sIntWithType.equals(SEARCH_MEMBER))
            {
                populateMemberDetails(mapSoftPhoneParameters);
                searchTypes.add('M');
                
            }
            else
            {
                HUMMemberSearchControllerV2 memberSearch = (HUMMemberSearchControllerV2) searchControllerMap.get(SEARCH_MEMBER);
                // Flushing the data which was fetching during previous search
                memberSearch.oSearchWrapper = new HUMMemberSearchWrapperHelper();
                memberSearch.setAccount = NULL;
                if(memberSearch.mapRecordData != null && !memberSearch.mapRecordData.isEmpty()){memberSearch.mapRecordData.clear(); }
            }                      
            
            if(softPhoneWrapper.sIntAboutType.equals(SEARCH_PROVIDER) || softPhoneWrapper.sIntWithType.equals(SEARCH_PROVIDER))
            { 
                populateProviderDetails(mapSoftPhoneParameters);
                 searchTypes.add('P');
               
            }
            else
            {
                HUMProviderSearchControllerV2 providerSearch = (HUMProviderSearchControllerV2) searchControllerMap.get(SEARCH_PROVIDER);
                // Flushing the data which was fetching during previous search
                providerSearch.oSearchWrapper = new HUMProviderSearchWrapperHelper();
                providerSearch.setAccount = new ApexPages.StandardSetController(new List<Account>());
                if(NULL != providerSearch.lstProviderAccounts)   providerSearch.lstProviderAccounts.clear();
                providerSearch.mapRecordData.clear();                           
            }                   
            
            if(softPhoneWrapper.sIntAboutType.equalsIgnoreCase(SEARCH_GROUP) || softPhoneWrapper.sIntWithType.equals(SEARCH_GROUP))
            {                
                populateGroupDetails(mapSoftPhoneParameters);
                 searchTypes.add('G');
            }
            else
            {               
                HUMGroupSearchControllerV2 groupSearch = (HUMGroupSearchControllerV2) searchControllerMap.get(SEARCH_GROUP);
                // Flushing the data which was fetching during previous search
                groupSearch.oSearchWrapper = new HUMGroupSearchWrapperHelper();
                groupSearch.bExternalResults = null;
                groupSearch.resElementFuture = null;
                groupSearch.responseDTO = null;
                groupSearch.setAccount = new ApexPages.StandardSetController(new List<Account>());
                if(groupSearch.mapRecordData != null && !groupSearch.mapRecordData.isEmpty()){ groupSearch.mapRecordData.clear(); }        
            }
            
            if(softPhoneWrapper.sIntAboutType.equalsIgnoreCase(SEARCH_AGENCY) || softPhoneWrapper.sIntWithType.equals(SEARCH_AGENCY) )
            {
                populateAgentDetails(mapSoftPhoneParameters);
                 searchTypes.add('A');
            }
            else
            {               
                HUMAgencySearchControllerV2 agencySearch = (HUMAgencySearchControllerV2) searchControllerMap.get(SEARCH_AGENCY);
                // Flushing the data which was fetching during previous search
                agencySearch.bExternalResults = null;
                agencySearch.oSearchWrapper =  new HUMAgencySearchWrapperHelper();       
                agencySearch.isInvalidAgencyName = false;
                agencySearch.setAccount = new ApexPages.StandardSetController(new List<Account>());
                if(agencySearch.mapRecordData != null && !agencySearch.mapRecordData.isEmpty()){agencySearch.mapRecordData.clear(); }
            }     

        }   

    }

    /*
     * Method Name   :    populateSoftPhoneInteractionDetails 
     * Description   :    Populate Ineraction details from URL in case of Soft Phone Routing
     * Return Type   :    void
     * Parameters    :    NA
     */ 
    private void populateSoftPhoneInteractionDetails()
    {
        //Populate Interaction Details
        String sFirstName = !String.ISBLANK(softPhoneWrapper.sInteractionFirstName) ? softPhoneWrapper.sInteractionFirstName : '';
        String sLastName = !String.ISBLANK(softPhoneWrapper.sInteractionLastName) ? softPhoneWrapper.sInteractionLastName : '';
        //US 2082644 the below lines of code will set the sCallCategory value.
        string sCallCategory = !String.ISBLANK(softPhoneWrapper.sCallCategory) ? softPhoneWrapper.sCallCategory : '';
        interactionLogController.oFetchInteraction = new Interaction__c();
        interactionLogController.oFetchInteraction.Caller_Name__c = sFirstName + ' ' + sLastName;
        // For Soft Phone always default the value to Inbound
        interactionLogController.oFetchInteraction.interaction_origin__c = HUMConstants__c.getInstance('INBCALL').StringValue__c;
        interactionLogController.oFetchInteraction.uuid__c = !String.ISBLANK(softPhoneWrapper.sInteractionUUID) ? softPhoneWrapper.sInteractionUUID : null; 
        interactionLogController.oFetchInteraction.Genesys_Interaction_ID__c = !String.ISBLANK(softPhoneWrapper.sInteractionId) ? softPhoneWrapper.sInteractionId : null; 
        interactionLogController.oFetchInteraction.Survey_ID__c = !String.ISBLANK(softPhoneWrapper.sSurveyId) ? softPhoneWrapper.sSurveyId : null; 
        interactionLogController.oFetchInteraction.Call_Category_Description__c = !String.ISBLANK(softPhoneWrapper.sCallCatDesc) ? softPhoneWrapper.sCallCatDesc : null; 
	    interactionLogController.oFetchInteraction.Authenticated__c = softPhoneWrapper.sAuthIndicator; //US#2287945 set the authentication variable to update the UI
        interactionLogController.authCheck = softPhoneWrapper.sAuthIndicator; //US#2287945set the authentication variable to update the UI
        interactionLogController.mapMemberInteractionType = new map<id, String>();
        interactionLogController.mapMemberInteraction =  new map<id, Interaction_Member__c>();
        interactionLogController.lstSelectOptionIntAbt = new  List<SelectOption>();
        interactionLogController.oHUMIntMembers = new Interaction_Member__c();        
        interactionLogController.bSavedOrUnsavesMember = true;
        interactionLogController.bIntCreationStatus = false;
        interactionLogController.bDisplayPopUp = false;
        interactionLogController.bRenderErrorMsg = false;
        interactionLogController.bShowUpdate = false;
        interactionLogController.mapRecordHaveAccess = new map<Id,boolean>();
        interactionLogController.bGetAccess = false;
        interactionLogController.bCaseInteraction  = false;
        interactionLogController.sSelectedValue = '';
        //US#2230000 Auto Interaction Creation and Population
        if(softPhoneWrapper.autoInteractionId != '' && PhoneBook_H_HUM.isCRMFunctionalityON('2230000')){
           interactionLogController.sInteractionId = softPhoneWrapper.autoInteractionId;
            interactionLogController.getInteractionDetails();
        }
        //US 2082644 the below lines of code will set the call category value to GNA.
        if(showCallCatergoryAlert != null && showCallCatergoryAlert && gnaCallCategoryMessageOnOff && UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(System.Label.US2082644_CRMS_690_PS_Label)){
	        interactionLogController.oFetchInteraction.Call_Category__c = sCallCategory;
        }
    }



    /*
     * Method Name   :    populateMemberDetails 
     * Description   :    Populate member search data and do the search based on parameters sent by Soft phone
     * Return Type   :    void
     * Parameters    :    Map<String,String>
     */ 
    private void populateMemberDetails(Map<String,String> mapSoftPhoneParameters) 
    {
        if(mapSoftPhoneParameters != null) 
        {        
            HUMMemberSearchControllerV2 memberSearch = (HUMMemberSearchControllerV2) searchControllerMap.get(SEARCH_MEMBER);   
            
            if(softPhoneWrapper.sIntWithType.equals(SEARCH_MEMBER))
            {
                String sMemberFirstName = SOFTPHONE_PRPID_HUM__c.getInstance('FirstName2').Value__c;
                String sMemberLastName = SOFTPHONE_PRPID_HUM__c.getInstance('LastName2').Value__c;
                String sMemberDOB = SOFTPHONE_PRPID_HUM__c.getInstance('DOB2').Value__c;
                String sMemberId = SOFTPHONE_PRPID_HUM__c.getInstance('MemberId2').Value__c;
                
                memberSearch.oSearchWrapper.sFirstName = !String.ISBLANK(softPhoneWrapper.sIntWithFirstName) ? mapSoftPhoneParameters.get(sMemberFirstName) : '';
                memberSearch.oSearchWrapper.sLastName = !String.ISBLANK(softPhoneWrapper.sIntWithLastName) ? mapSoftPhoneParameters.get(sMemberLastName) : '';
                memberSearch.oSearchWrapper.sBirthDate = !String.ISBLANK(softPhoneWrapper.sIntWithDOB) ? mapSoftPhoneParameters.get(sMemberDOB) : '';
                memberSearch.oSearchWrapper.sMemberID = !String.ISBLANK(softPhoneWrapper.sIntWithID) ? mapSoftPhoneParameters.get(sMemberId) : '';
                memberSearch.oSearchWrapper.sSuffix = !String.ISBLANK(softPhoneWrapper.sIntWithSuffixId) ? softPhoneWrapper.sIntWithSuffixId : '';

            }

            if(softPhoneWrapper.sIntAboutType.equals(SEARCH_MEMBER))
            {
                String sMemberFirstName = SOFTPHONE_PRPID_HUM__c.getInstance('FirstName').Value__c;
                String sMemberLastName = SOFTPHONE_PRPID_HUM__c.getInstance('LastName').Value__c;
                String sMemberDOB = SOFTPHONE_PRPID_HUM__c.getInstance('DOB').Value__c;
                String sMemberId = SOFTPHONE_PRPID_HUM__c.getInstance('MemberId').Value__c;
                
                memberSearch.oSearchWrapper.sFirstName = !String.ISBLANK(softPhoneWrapper.sIntAboutFirstName) ? mapSoftPhoneParameters.get(sMemberFirstName) : '';
                memberSearch.oSearchWrapper.sLastName = !String.ISBLANK(softPhoneWrapper.sIntAboutLastName) ? mapSoftPhoneParameters.get(sMemberLastName) : '';
                memberSearch.oSearchWrapper.sBirthDate = !String.ISBLANK(softPhoneWrapper.sIntAboutDOB) ? mapSoftPhoneParameters.get(sMemberDOB) : '';
                memberSearch.oSearchWrapper.sMemberID = !String.ISBLANK(softPhoneWrapper.sIntAboutID) ? mapSoftPhoneParameters.get(sMemberId) : '';
                memberSearch.oSearchWrapper.sSuffix = !String.ISBLANK(softPhoneWrapper.sIntAboutSuffixId) ? softPhoneWrapper.sIntAboutSuffixId : '';
            }
            if(softPhoneWrapper.mapfieldCipherMapping.size()>0) memberSearch.oSearchWrapper.mapfieldCipherMapping = softPhoneWrapper.mapFieldCipherMapping;
            memberSearch.oSearchwrapper.sIntAboutType = !String.ISBLANK(softPhoneWrapper.sIntAboutType) ? softPhoneWrapper.sIntAboutType :'';
        }
    }
        
    /*
     * Method Name   :    populateProviderDetails 
     * Description   :    Populate Provider search data and do the search based on Parameters sent by Soft phone
     * Return Type   :    void
     * Parameters    :    Map<String,String>
     */ 
    private void populateProviderDetails(Map<String,String> mapSoftPhoneParameters) 
    {
        if(mapSoftPhoneParameters != null) 
        {
            HUMProviderSearchControllerV2 providerSearch = (HUMProviderSearchControllerV2) searchControllerMap.get(SEARCH_PROVIDER);  

            if(softPhoneWrapper.sIntWithType.equals(SEARCH_PROVIDER))
            {
                String sProviderFirstName = SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName2').Value__c;
                String sProviderLastName = SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c;
                String sProviderTaxId = SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__c;
                String sProviderNPiId = SOFTPHONE_PRPID_HUM__c.getInstance('NpiId2').Value__c;

                providerSearch.oSearchWrapper.sFirstName = mapSoftPhoneParameters.containsKey(sProviderFirstName)? mapSoftPhoneParameters.get(sProviderFirstName) : '';
                providerSearch.oSearchWrapper.sLastName = mapSoftPhoneParameters.containsKey(sProviderLastName)? mapSoftPhoneParameters.get(sProviderLastName) : '';                   
                providerSearch.oSearchWrapper.staxID = mapSoftPhoneParameters.containsKey(sProviderTaxId)? mapSoftPhoneParameters.get(sProviderTaxId) : '';
                providerSearch.oSearchWrapper.sNPI =   mapSoftPhoneParameters.containsKey(sProviderNPiId)? mapSoftPhoneParameters.get(sProviderNPiId) : '';
            }
            
            if(softPhoneWrapper.sIntAboutType.equals(SEARCH_PROVIDER))
            {
                String sProviderFirstName = SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName').Value__c;
                String sProviderLastName = SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__c;
                String sProviderTaxId = SOFTPHONE_PRPID_HUM__c.getInstance('TaxId').Value__c;
                String sProviderNPiId = SOFTPHONE_PRPID_HUM__c.getInstance('NpiId').Value__c;
                providerSearch.oSearchWrapper.sFirstName = mapSoftPhoneParameters.containsKey(sProviderFirstName)? mapSoftPhoneParameters.get(sProviderFirstName) : '';
                providerSearch.oSearchWrapper.sLastName = mapSoftPhoneParameters.containsKey(sProviderLastName)? mapSoftPhoneParameters.get(sProviderLastName) : '';                
                providerSearch.oSearchWrapper.staxID = mapSoftPhoneParameters.containsKey(sProviderTaxId)? mapSoftPhoneParameters.get(sProviderTaxId) : '';            
                providerSearch.oSearchWrapper.sNPI =   mapSoftPhoneParameters.containsKey(sProviderNPiId)? mapSoftPhoneParameters.get(sProviderNPiId) : '';
            }
            if(softPhoneWrapper.mapEncryptedValues.size() > 0) providerSearch.oSearchWrapper.mapEncryptedValues = softPhoneWrapper.mapEncryptedValues;
           // providerSearch.search();
        }   
    }
        
    /*
     * Method Name   :    populateGroupDetails 
     * Description   :    Populate Group search data and do the search based on Parameters sent by Soft phone
     * Return Type   :    void
     * Parameters    :    Map<String,String>
     */ 
    private void populateGroupDetails(Map<String,String> mapSoftPhoneParameters) 
    {
        if(mapSoftPhoneParameters != null) 
        {
            HUMGroupSearchControllerV2 groupSearch = (HUMGroupSearchControllerV2) searchControllerMap.get(SEARCH_GROUP);  

            if(softPhoneWrapper.sIntWithType.equals(SEARCH_GROUP))
            {
                String sGroupFirstName = SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName2').Value__c;
                String sGroupLastName = SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c;
                String sGroupNumber = SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber2').Value__c;
                groupSearch.oSearchWrapper.sFirstName = mapSoftPhoneParameters.containsKey(sGroupFirstName)? mapSoftPhoneParameters.get(sGroupFirstName) : '';
                groupSearch.oSearchWrapper.sLastName= mapSoftPhoneParameters.containsKey(sGroupLastName)? mapSoftPhoneParameters.get(sGroupLastName) : '';                  
                groupSearch.oSearchWrapper.sGroupNumber = mapSoftPhoneParameters.containsKey(sGroupNumber)? mapSoftPhoneParameters.get(sGroupNumber) : '';            
            }

            if(softPhoneWrapper.sIntAboutType.equals(SEARCH_GROUP))
            {
                String sGroupFirstName = SOFTPHONE_PRPID_HUM__c.getInstance('IFirstName').Value__c;
                String sGroupLastName = SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__c;
                String sGroupNumber = SOFTPHONE_PRPID_HUM__c.getInstance('GroupNumber').Value__c;
                groupSearch.oSearchWrapper.sFirstName = mapSoftPhoneParameters.containsKey(sGroupFirstName)? mapSoftPhoneParameters.get(sGroupFirstName) : '';
                groupSearch.oSearchWrapper.sLastName= mapSoftPhoneParameters.containsKey(sGroupLastName)? mapSoftPhoneParameters.get(sGroupLastName) : '';                    
                groupSearch.oSearchWrapper.sGroupNumber = mapSoftPhoneParameters.containsKey(sGroupNumber)? mapSoftPhoneParameters.get(sGroupNumber) : '';             
            }
            
            if(softPhoneWrapper.mapEncryptedValues.size() > 0) groupSearch.oSearchWrapper.mapEncryptedValues = softPhoneWrapper.mapEncryptedValues;
           // groupSearch.groupSearch();
        }   
    }

    /*
     * Method Name   :    populateAgentDetails 
     * Description   :    Populate Agent/Broker search data and do the search based on Parameters sent by Soft phone
     * Return Type   :    void
     * Parameters    :    Map<String,String>
     */ 
    private void populateAgentDetails(Map<String,String> mapSoftPhoneParameters) 
    {
        if(mapSoftPhoneParameters != null) 
        {

            HUMAgencySearchControllerV2 agencySearch = (HUMAgencySearchControllerV2) searchControllerMap.get(SEARCH_AGENCY);  

            if(softPhoneWrapper.sIntWithType.equals(SEARCH_AGENCY))
            {
                String sAgencyName = SOFTPHONE_PRPID_HUM__c.getInstance('AccountName2').Value__c;
                String sAgencyLastName = SOFTPHONE_PRPID_HUM__c.getInstance('ILastName2').Value__c;
                String sTaxId = SOFTPHONE_PRPID_HUM__c.getInstance('TaxId2').Value__c;
                agencySearch.oSearchWrapper.sAgencyName = mapSoftPhoneParameters.containsKey(sAgencyName)? mapSoftPhoneParameters.get(sAgencyName) : '';
                agencySearch.oSearchWrapper.sLastName = mapSoftPhoneParameters.containsKey(sAgencyLastName)? mapSoftPhoneParameters.get(sAgencyLastName) : ''; 
                agencySearch.oSearchWrapper.sTaxID = mapSoftPhoneParameters.containsKey(sTaxId)? mapSoftPhoneParameters.get(sTaxId) : '';
                agencySearch.oSearchWrapper.sAgentID = mapSoftPhoneParameters.containsKey('sIntWithAgentId')? mapSoftPhoneParameters.get('sIntWithAgentId') : '';      
            }

           if(softPhoneWrapper.sIntAboutType.equals(SEARCH_AGENCY))
            {
                String sAgencyName = SOFTPHONE_PRPID_HUM__c.getInstance('AccountName').Value__c;
                String sAgencyLastName = SOFTPHONE_PRPID_HUM__c.getInstance('ILastName').Value__c;
                String sTaxId = SOFTPHONE_PRPID_HUM__c.getInstance('TaxId').Value__c;
                agencySearch.oSearchWrapper.sAgencyName = mapSoftPhoneParameters.containsKey(sAgencyName)? mapSoftPhoneParameters.get(sAgencyName) : '';
                agencySearch.oSearchWrapper.sLastName = mapSoftPhoneParameters.containsKey(sAgencyLastName)? mapSoftPhoneParameters.get(sAgencyLastName) : ''; 
                agencySearch.oSearchWrapper.sTaxID = mapSoftPhoneParameters.containsKey(sTaxId)? mapSoftPhoneParameters.get(sTaxId) : '';
                agencySearch.oSearchWrapper.sAgentID = mapSoftPhoneParameters.containsKey('sIntAboutAgentId')? mapSoftPhoneParameters.get('sIntAboutAgentId') : '';                                                      
            }
           if(softPhoneWrapper.mapEncryptedValues.size() > 0) agencySearch.oSearchWrapper.mapEncryptedValues = softPhoneWrapper.mapEncryptedValues;
                    
            //agencySearch.searchAgency();
        }   
    }

    /*
     * Method Name   :    populateUnknownAccountDetail
     * Description   :    Populate unknown account detail on the page based on the id passed
     * Return Type   :    void
     * Parameters    :    NA
     */ 
    public void populateUnknownAccountDetail() 
    {
        try 
        {
            if(strSearchContextName == SEARCH_PROVIDER)
            {
                HUMProviderSearchControllerV2 providerSearchController = (HUMProviderSearchControllerV2)searchControllerMap.get(SEARCH_PROVIDER);
                providerSearchController.buildUnknownAccountMap(idUnknownAccount);
            }
            else if(strSearchContextName == SEARCH_GROUP)
            {
                HUMGroupSearchControllerV2 groupController = (HUMGroupSearchControllerV2)searchControllerMap.get(SEARCH_GROUP);
                groupController.buildUnknownAccountMap(idUnknownAccount);
            }
            else if(strSearchContextName == SEARCH_AGENCY)
            { 
                HUMAgencySearchControllerV2 agencyController = (HUMAgencySearchControllerV2)searchControllerMap.get(SEARCH_AGENCY);
                agencyController.buildUnknownAccountMap(idUnknownAccount);
            }
            else if(strSearchContextName == SEARCH_MEMBER)
            { 
                HUMMemberSearchEnrollmentControllerV2  memberController = (HUMMemberSearchEnrollmentControllerV2 )searchControllerMap.get(SEARCH_ENROLLMENT);
                memberController.buildUnknownAccountMap(idUnknownAccount);
            }
        }
        catch(Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchControllerV2', 'populateUnknownAccountDetail');
        }
    }

/*
     * Method Name   :    getURLKeyValuePairs
     * Description   :    Dissect the Query String and return key value pairs
     * Return Type   :    Map<String,String>
     * Parameters    :    String
     */ 
public  Map<String,String> getURLKeyValuePairs(String sUrlString) 
    { 
        Map<String, String> mapQueryKeyValues = new Map<String, String>();
        if(String.ISNOTBLANK(sURlString)) 
        {
            List<String>  listKeyValuePairs = sUrlString.split(AMP);
            for (String key : listKeyValuePairs) 
            {
                Integer iKey = key.indexOf(EQUAL);
                //In case of softphone routing URL is Base 64 encoded . In query string parameter value is undefined
                boolean isEncodedvalue = (key.substring(iKey + 1) == UNDEFINED ) ? true:false;
                if(isEncodedValue)
                {
                    List<String>  lstDecodedvalues = (EncodingUtil.Base64Decode(key.substring(0, iKey))).toString().split(AMP);
                    for(String decodedKey : lstDecodedvalues) 
                    {
                        Integer idecodedKey = decodedKey.indexOf(EQUAL);
                        mapQueryKeyValues.put(decodedKey.substring(0, idecodedKey),EncodingUtil.URLdecode(decodedKey.substring(idecodedKey + 1),'UTF-8'));
                    }
                }
                else
                {
                    mapQueryKeyValues.put(key.substring(0, iKey),EncodingUtil.URLdecode(key.substring(iKey + 1),'UTF-8'));
                } 
            }
        }
        return mapQueryKeyValues;
    }
    
    /*
     * Method Name   :    closePopUp
     * Description   :    Close the pop up on interaction
     * Return Type   :    NA
     * Parameters    :    NA
    */
    public void closePopup() 
    {        
        try
        {  
            bPopup = false;  
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMSearchControllerV2','closePopup');
        }
    }
    
    /*
    * Method Name: resetAll
    * Description: reset search page for all tabs and Interaction log section
    * Return type: PageReference
    * Paramater  : NA
    */ 
    public PageReference resetAll() {
        PageReference pageRef = new PageReference(System.currentPageReference().getURL());
        
        pageRef.setRedirect(false);
        
        return pageRef;
    }

    /*
    * Method Name: populateInteractionWithAndAboutDetails
    * Description: populate interaction with and about details information
    * Return type: NA
    * Paramater  : NA
    */     
    public void populateInteractionWithAndAboutDetails() {
        // call for interaction with details
        populateInteractionWithDetails();
        
        // call for interaction about details
        populateInteractionAboutDetails();
        
        // interaction user click
        populateInteractionDetails();
	    updateAuthCheck(); //US#2287945 update the authcheck variable
    }
     /*
    * Method Name: showAckPopup
    * Description: Retrived password information to display on PopUp
    * Return type: NA
    * Paramater  : NA
    */   
    public void showAckPopup()
        { 
           try
           { 
           bAckDisplayPopup = false;
           list<Account> oAcc = [SELECT Id,Account_Security_Answer__c, Account_Security_Question__c, Account_Security_Access__c,recordtype.name,Account_Security_EndDate__c FROM Account where id=:sInteractionAbout limit 1] ;
           if(oAcc.size() >0)
            { 
               if(System.now()<=oAcc[0].Account_Security_EndDate__c && oAcc[0].recordtype.name==MEMBER)
                {
                   pass=oAcc[0].Account_Security_Access__c ;          
                   secQuestion = oAcc[0].Account_Security_Question__c;
                   secAns = oAcc[0].Account_Security_Answer__c; 
                   bAckDisplayPopup = (pass==null || pass=='')?false:true;
                }
            }
            } 
           catch(exception e)
            {
                HUMExceptionHelper.logErrors(e,'HUMSearchControllerV2','showAckPopup');
            }
        }
     /*
    * Method Name: closeAckPopup
    * Description: To close pop up
    * Return type: NA
    * Paramater  : NA
    */ 
    public PageReference closeAckPopup() {     
         bAckDisplayPopup = false;    
         return null;
    }
     /*
    * Method Name: saveAndContPopup
    * Description: To open pop up on Save and Continue
    * Return type: NA
    * Paramater  : NA
    */ 
    public PageReference saveAndContPopup() {     
        bPopUp = true;    
        return null;
   }
}