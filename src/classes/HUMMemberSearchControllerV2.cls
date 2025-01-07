/*******************************************************************************************************************************
Apex Class Name : HUMMemberSearchControllerV2
Version         : 2.0
Created On      : 04/01/2014
Function        : Main class for searching Member and returning results based on the parameters 
                  passed from the visual force page.Class is having without sharing as part of security setting,
                  so that CCS Associate should be able to view few values for those records which he don't have access to.
Test Class      : HUMMemberSearchControllerV2Test                
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    2.0           Chaitanya Kumar/            25261                 31/07/2015                 Code refactoring ( removing abstract layer - HUMSearchFieldHelper)
                    Harshith Mandya 
*    2.1            Shreya Choodamani          25261                 31/07/2015                 Implementing Async Webservice Callout  
*    2.1            Mansih Kumar               25261                 08/05/2015                 Removed UserReadAccess SOQL and accommodated in Account query.
*    2.2            Harshith  Mandya           25261                 08/07/2015                 Implemented switch for "Dpaas" service
*    2.3            Prasanthi Kandula          46228                 10/13/2016                 REQ - 222755 Updated the query on Member Id object to not display legacy deleted policy member id's    
*    2.4            Subhamay Mandal                                  04/05/2017                 REQ - 305673 Display active policy on search page    
*    2.5            Prafull Verma              84636                 04/05/2017                  REQ - 306322 : SF - TECH - Softphone routing optimization
*    2.4            Subhamay Mandal                                  04/20/2017                 Defect fix for 274085.0001     
*    2.5            Subhamay Mandal                                  05/01/2017                 REQ - 311389 Ability to open an account and Policy tab when Multiple Member Account search results are displayed on Member Search Page
*    2.6            Mohammed Noor              96594                 05/10/2017                 REQ - 311432 Softphone / CRM Results for All Family Members by searching using Member Id only and launch optimization.
*    2.7            Mohammed Noor              97992                 05/31/2017                 Defect fix for 290648.0001
*    2.8            Mohammed Noor              99521                 06/05/2017                 Defect fix for 291013.0001
*    2.9            Prafull Verma              99782                 06/05/2017                 Modified for REQ - 304829
*    3.0            Amar Gadewar                                     08/01/2017                 REQ - 323549 - SF - TECH - If single Result adding as a Parameter. 
*    3.1            Mohammed Noor              122564                 08/23/2017                 REQ - 326313 - CA incident # 6941459 - Fix for Softphone if the caller is not authenticated.
*    3.2            Amar Gadewar                                     11/2/2017                  REQ - 335234 - Removing the strPOAMemberAccountId page parameter on load
*    3.3            Santhi Mandava             238641                05/16/2017                 REQ - 362027 Implemented platform encryption for member_Id__c
*    3.4            Santhi Mandava             262341                07/17/2018                 Implemented platform encryption for custom search related account fields
*    3.5            Moshitha Gunasekaran       262118                06/28/2018                 REQ - 350472 ID Number field on the Unknown Account tab
*    3.6            Syed Mubarak               275424                08/27/2018                 REQ - 359222 Dependent code through softphone - UI issue fix 
*   3.7            Santhi Mandava             289465                09/21/2018                 Implemented SOSL for softphone
*    3.7            Lakshmi Madduri            289399                9/05/2018                  REQ - 367752 Added Service call for PID Search
*    3.9            Santhi Mandava                                   10/01/2018                 REQ - 376416 Implemented warning message incase of name search
*    4.0            Charan K                                         10/4/2018                  Defect fix for 385810.0001 
*    4.1            Santhi Mandava                                   11/01/2018                 Implemented SOSL for softphone
*    4.2            Santhi Mandava                                   11/01/2018                 Implemented platform encryption for Account.Name field
*    4.3            Melkisan Selvaraj          340805                02/27/2019                 REQ - 367741 - Ability to view middle initial
*    4.4            Santhi Mandava             348723                04/02/2019                 Cleanup cipher cloud references
*    4.5            Santhi Mandava             349610                04/06/2019                 Replaced Member_Id_Value__c with Name field
*    4.6            Mithra Bharadwaj                                                            REQ - 402761 - Changes to show error messages when invalid data in Case Edit Member search
*    4.7            Prudhvi Pamarthi                                 07/15/2019                 REQ - 396335
*    4.8            Santhi Mandava                                   09/04/2019                 REQ - 408313 included member icons logic
*    4.9            Santhi Mandava                                                              REQ - 409718 - Implemented medicare supplement icon logic 
*    5.0            Amar Gadewar                                     11/13/2019                 Defect Fix on Quality REQ - 411303 - 2020 New Member Indicator (CRM)
*    5.1            Santhi Mandava                                   11/15/2019                 REQ - 415960 Implemented configurable member icon changes
*    5.2            Amar Gadewar                                     01/28/2020                 USER STORY 729633 PR00091574 - MF 1 - Quality - All Users - Display Icons on Search Page (CRM)
*    5.3            Jasmeen Shangari								 02/07/2020                 USER STORY -754855 - Removed RecordType check for CaseEdit Page
*    5.4            Sagar MN                                         02/12/2020                 USER STORY -777419 - Added Fix for Account Name Search on Case Edit page provided by Seema for req 402756
*    5.5            Sagar MN                                         02/20/2020                 Defect Fix Df:188 warning message updated on account search in case edit page
*    5.6            Amit Agarwal                                     06/25/2020                 User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (RF)
*    5.7			Riddhi Mittal									 07/06/2020					Switchonoff functionality for suffix And add suffix on member search page(Merge my changes in 7_30_2020 release branch)
*    5.8            Anjani Vaddadi                                   11/02/2021                 REQ - 1914227 - Sonar Qube Remidiation/Consumer Management (Avoid Untrusted/Un-escaped Variables in DML Query)
*    5.9            Anjani Vaddadi                                   02/26/2021                 User Story#-1260069 -T1PRJ0003372 - MF 1 - FR1.1 Veteran Member Icon
*    6.0            Ankit Avula                                      03/28/2021                 User Story 1987365 Contract Protected Data Implementation
*    6.1            Atia Uzma                                        05/21/2021                 User Story 2325460: T1PRJ0001827 - MF 1 - Quality - Ability to display "Standardized Greeting" message on the Search Page (RF)
*    6.2            Firoja Begam                2237351              06/02/2021                 On/Off Switch Cleanup for requirements deployed
*    6.3	        Ajay Chakradhar                                  06/03/2021                 REQ - 1755482 - Replacing constants to improve performance
*    6.4	        Ajay Chakradhar                                  08/17/2022                 USER STORY 2860577 - T1PRJ0100059- MF 3 - Agnostic Customer Record Type
*******************************************************************************************************************************************************************/
public without sharing class HUMMemberSearchControllerV2 extends HUMSearchComponentBaseHelperV2
{
    @testVisible private string sSearchQuery ='';
    @testVisible private string sConsumerSearchQuery ='';
    private recordType oMemberRecordType = NULL;
    @testVisible private Set<ID> setAccMemberId = new Set<ID>();
    @testVisible private Set<ID> setAccConsumerId = new Set<ID>();
    @testVisible private Set<ID> setAccIdForGrpNumber = new Set <ID>();
    @testVisible private Set<ID> setAccIdFormemberId = new Set <ID>();
    @testVisible private integer iSearchLimit = 101;
    @testVisible private integer iConsumerSearchLimit = 101;
    private List<Account> lstFinalSearchResult = new List<Account>();
    @testVisible private String sSortDirection='';    
    @testVisible private Map<string, string> mapEncryptedResult = new Map<String,String>();
    private Map<Id, Account> mapAccounts = new Map<Id, Account>(); 
    
    private string sSoftPhoneMemberId ='';
    private string sSoftPhonePlatformSearchQuery ='';
    private String platformNameSearchQuery = '';
    private string sSoftPhonePlatformNameSearchQuery = '';
    @testVisible private Boolean bIsNameSearch = false;
    private String platformSearchQuery = '';
    Public Boolean suffixonoff {get; set;}
    Public Boolean contractprotecteddataonoff {get;set;}
    // Wrapper Instance to store Member Search details  
    public HUMMemberSearchWrapperHelper oSearchWrapper{get; set;}
    
    public ID memberID {get; set;}
    
    public transient boolean bIsMemberError{get; set;}
    
    public Map<Id, Boolean> mapRecordHaveAccess {get; set;}
    
    public Boolean bAccountLookup{get;set;}
    
    public Boolean clicksource {get; set {clicksource= value;}  }
    public Boolean clickfromCaseEdit {get{ return true;} set;}
    //Added new variable for PID
    public String sourcePersonId;
    public String sEnterpriseId;
    private MemberSearchv2_S_HUM oServiceMembPlus=new MemberSearchv2_S_HUM();
    
    public List<selectOption> lstMemberStateValue
    {
        get{ return HUMUtilityHelper.getStateValue(); }
        set;
    }
    
    public List<Account> lstAccount
    {
        get
        {
            if(NULL != setAccount)
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
        
    //Boolean variable to identify if the search need to be done using only Member ID (Relationship query through Member)
    public Boolean memberIdSearch;
    
    //Boolean variable to identify if the search is through Softphone or Manual
    public Boolean isSoftPhoneSearch;
    
    //Boolean variables to display error messages from client side input validations.
    public Boolean memberIdErr {get; set;}
    public Boolean memberIdLengthErr {get; set;}
    public Boolean suffixErr {get; set;}
    public Boolean suffixLengthErr {get; set;}
    public Boolean fNameError {get; set;}
    public Boolean lNameError {get; set;}
    public Boolean lNameLengthErr {get; set;}
    public Boolean birthDateErr {get; set;}
    public Boolean postalCodeErr {get; set;}
    public Boolean groupNumberErr {get; set;}
    public Boolean phoneErr {get; set;}
    public Boolean phoneLengthErr {get; set;}
    public Boolean birthDateGreaterErr {get; set;}
    public Boolean unKnownMemberCheck{get;set;}
    Public MemberIcons_DTO_HUM objIconsVisibility{get;set;}
    Public Integer tdSearchResultWidth{get;set;}
    Public Integer tdIconsWidth{get;set;}
    public Boolean blntoomanyentries {get;set;}
    public Boolean bagnosticRectypeswitch {get; set;}
    Private String sPID_MEMBERNOTAVIALBLE ='Member is not found for this PID number';
    Private String sHUMStateDefaultValue = 'None';
    Private String sHumSearchSortExpressionASC = 'ASC';
    Private String sHumSearchSortExpressionDESC = 'DESC';
    Private String OBJECT_ACCOUNT_API_FIRSTNAME = 'FirstName';
    Private String OBJECT_ACCOUNT_API_LASTNAME = 'LastName';
    Private String HUMAccountBirthDate = 'Birthdate__c';
    Private String HUMName = 'Name';
    Private String HUMConsumerTaxID = 'Consumer_ID__c';
    Private String HumSearchMemberIdAlphaNumericValidation = 'Please enter only alpha-numeric values for Id field';
    Private String HumSearchMemberIdCharacterValidation = 'Member Id must be between 3 and 17 Characters';
    Private String HUMSearchSuffixCharacterValidation = 'Member ID suffix must be 2-3 characters.';
    Private String HumSearchSuffixNumericValidation = 'Member ID suffix must be numeric.';
    Private String HumSearchFirstNameAlphaNumericValidation = 'The only special characters that can be used are an apostrophe, a period, a dash, a comma or an ampersand in the First Name field.';
    Private String HumSearchLastNameAlphaNumericValidation = 'The only special characters that can be used are an apostrophe, a period, a dash or a comma in the Last Name field.';
    Private String HumSearchLastNameCharacterValidation = 'Last Name must be at least 2 characters';
    Private String HumMemberSearchBirthDateValidation = 'Please Enter Valid Date in the Format MM/DD/YYYY in Birth Date field';
    Private String HUMSearchBirthDateValidation = 'Birth Date cannot be a future date';
    Private String HumZipcodeCharacterLimit = 'Zip code must be 5 digits numeric value';
    Private String HumSearchGroupNumberAlphaNumericValidation = 'Please enter only alpha-numeric values for Group Number field';
    Private String HumPhoneValidationForMember = 'Please enter only Numbers for phone field.';
    Private String HumPhoneCharacterLimit = 'Phone numbers must be 10 digits';
    
    
    public Boolean bCallGreetings
    {
        get{ 
            return HUMUtilityHelper.isCRMFunctionalityON('2325460');
        } 
        set;
    }

    /*
    * Method Name   :    getThis
    * Description   :    This method is used for getting current reference of the class 
    * Return Type   :    Returns instance of HUMMemberSearchController class
    * Parameters    :    NA
    */
    public HUMMemberSearchControllerV2 getThis()
    {        
        return this;
    }
    
    /*
    * Method Name   :    Constructor
    * Description   :    This method is used for initializing variables
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public HUMMemberSearchControllerV2()
    {        
         oSearchWrapper = new HUMMemberSearchWrapperHelper();
         resetIconVariables();
    }
    
    /*
    * Method Name   :    checkResultSizeLimit
    * Description   :    This method is used for checking maximum/minimum search result size 
                         in member search page   
    * Return Type   :    void
    * Parameters    :    NA
    */
    public void checkResultSizeLimit()
    {
        try
        {          
            
            
            if((setAccount.getResultSize() == 0 || NULL == setAccount) && NULL !=clicksource && clicksource && blntoomanyentries!=true) 
            { 
                throw new HUMCustomException(system.Label.HUMMedicareSearchCriteriaViolation); 
            }
            else if((setAccount.getResultSize() == 0 || NULL == setAccount) && blntoomanyentries!=true) throw new HUMCustomException(System.Label.HUMMemberSearchCriteriaViolation);
	  
        }
        catch(Exception e)
        {
            bIsMemberError = true;
            HUMExceptionHelper.logErrors(e, 'HUMMemberSearchControllerV2', 'checkResultSizeLimit');
            return;
        }
    }
    
    
    /*
     * Method Name   :  buildQuery
     * Description   :  This Method is used to :
     *                  a.  Encrypt Account, Member_Id fields
     *                  b.  Build search query
     * Return Type   :  void
     * Parameters    :  NA
     */ 
    private void buildQuery()
    { 
	
	// On/Off switch functionality for suffix and contact protected data on member search page

         suffixonoff= HUMUtilityHelper.isCRMFunctionalityON('755829');
         contractprotecteddataonoff = HUMUtilityHelper.isCRMFunctionalityON('2037674');
         bagnosticRectypeswitch = HUMUtilityHelper.isCRMFunctionalityON('2860577');
		 
        bIsNameSearch =false;
        // Fetching record type if for "Member" accouts via describe call
        Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
        Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
        Schema.RecordTypeInfo oMemberRecordType =  accountRecordTypeMap.get(ConsumerManagementConstants_HUM.MEMBER);
        Schema.RecordTypeInfo oCustomerRecordType =  accountRecordTypeMap.get(ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER);
        Schema.RecordTypeInfo oUnknownMemberRecordType =  accountRecordTypeMap.get(ConsumerManagementConstants_HUM.UNKNOWN_MEMBER_HUM);
        List<String> lstRecordTypeId = new List<String>();
        String sMemberId = oSearchWrapper.sMemberId + '*';
        String sNameKey ='';
        
        if(bagnosticRectypeswitch)
       		 lstRecordTypeId = new List<String>{'\''+oMemberRecordType.getRecordTypeId()+'\'','\''+oUnknownMemberRecordType.getRecordTypeId()+'\'','\''+oCustomerRecordType.getRecordTypeId()+'\''}; 
        else
        	 lstRecordTypeId = new List<String>{'\''+oMemberRecordType.getRecordTypeId()+'\'','\''+oUnknownMemberRecordType.getRecordTypeId()+'\''};                       
        if(!memberIdSearch)
        {
            //Adding the switch contractprotecteddataonoff for adding a new field Contract_Protected_Data__c in the query 
            if(contractprotecteddataonoff){  
		        if(suffixonoff)
		        {
                    sSearchQuery = 'SELECT ID, Name, FirstName, LastName,middlename,Individual_First_Name__c,Individual_Last_Name__c,Suffix, RecordType.Name, Birthdate__c,Deceased_Date__c, PersonEmail, '+
                                   + 'PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState, Contract_Protected_Data__c,' +
                                   + 'PersonMailingPostalCode, PersonHomePhone, UserRecordAccess.HasReadAccess, Home_Office_Account__c, ETL_Record_Deleted__c, Veteran_Status__c ' +
                                   + 'From Account Where RecordtypeId IN ' + lstRecordTypeId ;
		        }
            }
            else{
                if(suffixonoff)
		        {
                    sSearchQuery = 'SELECT ID, Name, FirstName, LastName,middlename,Individual_First_Name__c,Individual_Last_Name__c,Suffix, RecordType.Name, Birthdate__c,Deceased_Date__c, PersonEmail, '+
                                   + 'PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState, ' +
                                   + 'PersonMailingPostalCode, PersonHomePhone, UserRecordAccess.HasReadAccess, Home_Office_Account__c, ETL_Record_Deleted__c, Veteran_Status__c ' +
                                   + 'From Account Where RecordtypeId IN ' + lstRecordTypeId ;
		        }
            }
            
            if(!string.isEmpty(oSearchWrapper.sFirstName) && NULL != mapEncryptedResult.get('FirstName')) sSearchQuery += ' AND ' + mapEncryptedResult.get('FirstName');
            if(!string.isEmpty(oSearchWrapper.sLastName) && NULL != mapEncryptedResult.get('LastName')) sSearchQuery += ' AND ' + mapEncryptedResult.get('LastName');
            if(!string.isBlank(oSearchWrapper.sBirthdate) && NULL != mapEncryptedResult.get('Birthdate__c'))    sSearchQuery += ' AND ' + mapEncryptedResult.get('Birthdate__c');
            if(!String.isEmpty(oSearchWrapper.sState) && !(oSearchWrapper.sState).equalsIgnoreCase(sHUMStateDefaultValue))    sSearchQuery += ' AND PersonMailingStateCode = \'' +string.escapeSingleQuotes(oSearchWrapper.sState) + '\'';
            if(!String.isBlank(oSearchWrapper.sPhone) && NULL != mapEncryptedResult.get('PersonHomePhone'))    sSearchQuery += ' AND ' + mapEncryptedResult.get('PersonHomePhone');
            if(!String.isBlank(oSearchWrapper.sPostalCode) && NULL != mapEncryptedResult.get('PersonMailingPostalCode'))    sSearchQuery += ' AND ' + mapEncryptedResult.get('PersonMailingPostalCode');
            
            if(string.isNotBlank(oSearchWrapper.sFirstName)) sNameKey = oSearchWrapper.sFirstName + '*';
            if(string.isNotBlank(oSearchWrapper.sLastName))
            {
                If(String.IsNotBlank(sNameKey))
                {
                    sNameKey += ' AND '+oSearchWrapper.sLastName+'*'; 
                }
                else
                {
                    sNameKey = oSearchWrapper.sLastName + '*';
                }
            }
            If(String.IsNotBlank(sNameKey))
            {
                //Adding the switch contractprotecteddataonoff for adding a new field Contract_Protected_Data__c in the query 
                if(contractprotecteddataonoff){
			        if(suffixonoff)
			        {
                        platformNameSearchQuery = 'FIND \'' + String.escapeSingleQuotes(sNameKey) +'\' IN NAME FIELDS RETURNING Account(ID, Name, FirstName, LastName, middlename,'+
                                                    + 'Individual_First_Name__c,Individual_Last_Name__c, RecordType.Name,Suffix, Birthdate__c,Deceased_Date__c, PersonEmail,PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState, ' +
                                                    + 'PersonMailingPostalCode, PersonHomePhone, Home_Office_Account__c, Contract_Protected_Data__c, ETL_Record_Deleted__c, Veteran_Status__c ' +
                                                    + ' Where RecordtypeId IN ' + lstRecordTypeId ;
			        }
			   }
               else{
                   if(suffixonoff)
			        {
                        platformNameSearchQuery = 'FIND \'' + String.escapeSingleQuotes(sNameKey) +'\' IN NAME FIELDS RETURNING Account(ID, Name, FirstName, LastName, middlename,'+
                                                    + 'Individual_First_Name__c,Individual_Last_Name__c, RecordType.Name,Suffix, Birthdate__c,Deceased_Date__c, PersonEmail,PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState, ' +
                                                    + 'PersonMailingPostalCode, PersonHomePhone, Home_Office_Account__c, ETL_Record_Deleted__c, Veteran_Status__c ' +
                                                    + ' Where RecordtypeId IN ' + lstRecordTypeId ;
			        }
               }
                if(!string.isBlank(oSearchWrapper.sBirthdate) && NULL != mapEncryptedResult.get('Birthdate__c'))    platformNameSearchQuery += ' AND ' + mapEncryptedResult.get('Birthdate__c');
                if(!String.isEmpty(oSearchWrapper.sState) && !(oSearchWrapper.sState).equalsIgnoreCase(sHUMStateDefaultValue))    platformNameSearchQuery += ' AND PersonMailingStateCode = \'' +string.escapeSingleQuotes(oSearchWrapper.sState) + '\'';
                if(!String.isBlank(oSearchWrapper.sPhone) && NULL != mapEncryptedResult.get('PersonHomePhone')) platformNameSearchQuery += ' AND ' + mapEncryptedResult.get('PersonHomePhone');
                if(!String.isBlank(oSearchWrapper.sPostalCode) && NULL != mapEncryptedResult.get('PersonMailingPostalCode'))    platformNameSearchQuery += ' AND ' + mapEncryptedResult.get('PersonMailingPostalCode');
                bIsNameSearch =true;
            }
            if(String.IsNotBlank(sNameKey))
            {
                sConsumerSearchQuery =  platformNameSearchQuery;
            }
            else
            {
                sConsumerSearchQuery =  sSearchQuery;
            }
        }
        else
        {
            buildMemberIDQueries(lstRecordTypeId);
        }        
    }
    
    /*
    * Method Name   :    buildMemberIDQueries
    * Description   :    This method is used to build member id queiries 
    * Return Type   :    void
    * Parameters    :    NA
    */
    private void buildMemberIDQueries(List<String> lstRecordTypeId)
    {
        String sMemberId = oSearchWrapper.sMemberId + '*';
		if(suffixonoff)
		{
        platformSearchQuery = 'FIND \'' + String.escapeSingleQuotes(sMemberId) + '\' IN NAME FIELDS RETURNING Member_Id__c(Name,Member_Dependent_Code__c ,Policy_Member__r.Member__r.ID,'
                                +  'Policy_Member__r.Member__r.Name, Policy_Member__r.Member__r.FirstName, Policy_Member__r.Member__r.LastName, Policy_Member__r.Member__r.middlename,Policy_Member__r.Member__r.Suffix, Policy_Member__r.Member__r.Individual_First_Name__c,'
                                +  'Policy_Member__r.Member__r.Individual_Last_Name__c, Policy_Member__r.Member__r.RecordType.Name, Policy_Member__r.Member__r.RecordType.Id,Policy_Member__r.Member__r.Birthdate__c, Policy_Member__r.Member__r.Deceased_Date__c,'
                                +  'Policy_Member__r.Member__r.PersonEmail, Policy_Member__r.Member__r.PersonMailingStreet, Policy_Member__r.Member__r.PersonMailingCity, '
                                +  'Policy_Member__r.Member__r.PersonMailingStateCode, Policy_Member__r.Member__r.PersonMailingState, Policy_Member__r.Member__r.PersonMailingPostalCode, '
                                +  'Policy_Member__r.Member__r.PersonHomePhone, Policy_Member__r.Member__r.Home_Office_Account__c, Policy_Member__r.Member__r.ETL_Record_Deleted__c,Policy_Member__r.ETL_Record_Deleted__c, '
                                +  'Policy_Member__r.Member__r.RecordTypeId, Policy_Member__r.Member__r.Veteran_Status__c Where Policy_Member__c != NULL AND Policy_Member__r.Member__r.RecordtypeId IN ' + lstRecordTypeId + ' AND Policy_Member__r.ETL_Record_Deleted__c=false';
								
		}
		
        if(!isSoftPhoneSearch && !String.isBlank(oSearchWrapper.sSuffix))  
        {
           platformSearchQuery += ' AND Member_Dependent_Code__c = \'' +  String.escapeSingleQuotes(oSearchWrapper.sSuffix) + '\' Limit ' + iSearchLimit +')';
        }
        else
        {
           platformSearchQuery += ' Limit ' + iSearchLimit + ')';
        }
    }
           
    /*
     * Method Name   :  searchManual
     * Description   :  This Method is called for Manual Search from UI(both for Search button click and hit of Enter key). This will
     *                  a.  set the 'isSoftPhoneSearch' to false to identify the Manual Search
     *                  b.  call the dataSearch() method to continue with the Member search     
     * Return Type   :  void
     * Parameters    :  NA
     */
    public void searchManual()
    {                                            
        try
        {
            mapEncryptedResult = new Map<String,String>();
            clearList();   
            if(NULL !=clicksource && clicksource && !HUMUtilityHelper.validateInputDataCombinationsMedicare(oSearchWrapper,clicksource))
            {
                bIsMemberError = true;
                iCounter = 0;            
                return;             
            }
            else if(!HUMUtilityHelper.validateInputDataCombinations(oSearchWrapper))
            {
                bIsMemberError = true;
                iCounter = 0;            
                return;
            }
             if(!String.isBlank(oSearchWrapper.sPID) && ((!String.isBlank(oSearchWrapper.sMemberId)) || (!String.isBlank(oSearchWrapper.sGroupNumber)) || (!String.isBlank(oSearchWrapper.sFirstName)) || (!String.isBlank(oSearchWrapper.sLastName))
           || (!String.isBlank(oSearchWrapper.sBirthdate)) || (!String.isBlank(oSearchWrapper.sPhone)) || (!String.isBlank(oSearchWrapper.sPostalCode)) 
           || (!String.isBlank(oSearchWrapper.sState)||(oSearchWrapper.sState).equalsIgnoreCase(system.label.HUMSPhoneMappingObj_None))))
           {
            throw new HUMCustomException(System.Label.HUMMemberSearchCriteriaViolation);
           }   
        isSoftPhoneSearch = false;                         
        dataSearch();       
        }       
        catch(Exception ex)
        {
            bIsMemberError = true;
            HUMExceptionHelper.logErrors(ex, 'HUMMemberSearchControllerV2', 'searchManual');
        } 
    }    
    
    /*
     * Method Name   :  datasearchSoftPhone
     * Description   :  This Method is called for Softphone Search. This will
     *                  a.  set the 'isSoftPhoneSearch' to true to identify that the search is from a softphone.
     *                  b.  call the dataSearch() method to continue with the Member search     
     * Return Type   :  void
     * Parameters    :  NA
     */    
    public void datasearchSoftPhone()
    {    
        try
        {   
            mapEncryptedResult = new Map<String,String>();
            clearList();
            unKnownMemberCheck = false;

            if(HUMUtilityHelper.validateInputDataCombinations(oSearchWrapper)) 
            {     
                isSoftPhoneSearch = true;          
                dataSearch();
                SearchConsumer();
            }
        }
        catch(exception ex)
        {
            bIsMemberError = true;
            HUMExceptionHelper.logErrors(ex, 'HUMMemberSearchControllerV2', 'datasearchSoftPhone'); 
        }        
    }
     
    /*
     * Method Name   :  dataSearch
     * Description   :  This Method is called for both Manual and Softphone searches. This will     
     *                  a.  Populate the mapEncryptedResult Map
     *                  b.  Set the memberIdSearch boolean variable     
     * Return Type   :  void
     * Parameters    :  NA
     */    
     public void dataSearch()
     {                                      
       mapEncryptedResult = HUMUtilityHelper.getQueryMapSoftphone(FieldCipherMaping(oSearchWrapper));
       memberIdSearch = false;
       if(isSoftPhoneSearch && !String.isBlank(oSearchWrapper.sMemberId)) memberIdSearch = true;
       if(!isSoftPhoneSearch && (!String.isBlank(oSearchWrapper.sMemberId)) && String.isBlank(oSearchWrapper.sGroupNumber) && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sLastName)
           && String.isBlank(oSearchWrapper.sBirthdate) && String.isBlank(oSearchWrapper.sPhone) && String.isBlank(oSearchWrapper.sPostalCode) 
           && (String.isBlank(oSearchWrapper.sState)||(oSearchWrapper.sState).equalsIgnoreCase(system.label.HUMSPhoneMappingObj_None)))  memberIdSearch = true;        
       dataSearchContinue();
     }
    
    /*
     * Method Name   :  dataSearchContinue
     * Description   :  This Method is called to build the query
     *                  search Account record with the following criteria :
     *                  a.  Account.FirstName,Account.LastName,Account.BirthDate,Account.State
     *                  b.  Account.Phone,Account.Group Number,Account.Postal code,Account.email
     *                  c.  Any account with child policy member records with child member id records with matching Member_ID__c.Name
     * Return Type   :  void
     * Parameters    :  NA
     */    
     public void dataSearchContinue()
     {        
        setAccIdForGrpNumber = new Set<ID>();
        List < Policy_Member__c > lstPolicyMember = new List < Policy_Member__c >();
        List<Account> lstAccountMember = new List<Account>();
        List<Account> lstAccounts = new List<Account>();
        Set<Id> lstPolicyIds = new Set<Id>();
        List<Policy__c> lstPolicy = new List<Policy__c>();     
        buildQuery();
        if (!String.isBlank(oSearchWrapper.sGroupNumber) && !memberIdSearch)
        {
           String sQuery = 'Select id, Group_Number__c from Account Where ' + mapEncryptedResult.get(System.Label.HUMGroupNumber);
           lstAccounts = (List<Account>)HUMWithoutSharingQueryHelper.getListOfsObjects(sQuery, NULL, NULL, NULL); 
           Set<Id> setIds = new Set<Id>();
           for(Account oacc : lstAccounts)  
           {
               setIds.add(oacc.id);
           }
           
           String sQueryforPolicy = 'Select id from Policy__c where Group_Name__c in :';
           lstPolicy  = (List<Policy__c>)HUMWithoutSharingQueryHelper.getListOfsObjects(sQueryforPolicy, setIds, NULL, NULL);
           
           for(Policy__c oPolicy : lstPolicy)
           {
               lstPolicyIds.add(oPolicy.id);
           }
           String sQueryForMembers = 'Select Member__r.Id FROM Policy_member__c Where Policy__c in :'; 
           lstPolicyMember  = (List<Policy_Member__c>)HUMWithoutSharingQueryHelper.getListOfsObjects(sQueryForMembers, lstPolicyIds, NULL, NULL);
            
            if (!lstPolicyMember.isEmpty())
            {
                for (Policy_Member__c oPolMem :lstPolicyMember)
                {
                    setAccIdForGrpNumber.add(oPolMem.Member__r.Id);
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
        if(clicksource == true)
        checkResultSizeLimit();  //Changed to display comments in case edit account lookup page.
    }
    
    /*
     * Method Name   :  doQuery
     * Description   :  This Method is  used to :
                        a. Build query for MemberID
                        b. Execute DB query
     * Return Type   :  void
     * Parameters    :  NA
     */
    private void doQuery()
    {
        setAccIdFormemberId = new Set<ID>();
        if (!String.isBlank(oSearchWrapper.sMemberId)&&(NULL != mapEncryptedResult.get('Name')))
        {
            setAccIdFormemberId = createSearchQuery(mapEncryptedResult.get('Name')); 
            sSearchQuery += ' AND ID IN : setAccIdFormemberId ';
            platformNameSearchQuery += ' AND ID IN : setAccIdFormemberId ';
        }  
        getEligibleAccountList(NULL);
    }
    
    /*
     * Method Name   :  doRelationshipQuery
     * Description   :  This Method is  used to :
                        a. Build query for fetching Account details using only MemberID
                        b. Execute DB query                        
     * Return Type   :  void
     * Parameters    :  NA
     */
    private void doRelationshipQuery()
    {
        List<Member_Id__c> lstMembers = new List<Member_Id__c>();            
        lstMembers = getMemberIdQueryResults(platformSearchQuery);
        if(lstMembers.isEmpty() && isSoftPhoneSearch)
        {
            if(!String.isBlank(oSearchWrapper.sFirstName) || !String.isBlank(oSearchWrapper.sLastName) || !String.isBlank(oSearchWrapper.sBirthdate) 
               || !String.isBlank(oSearchWrapper.sPhone) || !String.isBlank(oSearchWrapper.sPostalCode) || !String.isBlank(oSearchWrapper.sGroupNumber)
               || (!String.isBlank(oSearchWrapper.sState) && !(oSearchWrapper.sState).equalsIgnoreCase(system.label.HUMSPhoneMappingObj_None))
               || (!String.isBlank(oSearchWrapper.sPID)))
            {
                memberIdSearch = false;              
                //clear the MemberID from Wrapper. This is used for display in UI
                oSearchWrapper.sMemberId=''; 
                oSearchWrapper.sSuffix = ''; 

                //call the query again with all the input parameters
                sSearchQuery = '';                  
                dataSearchContinue();
            }
            else
            {                       
                dataSecurity(mapAccounts.values());
            }                                                                            
        }
        else
        {                           
            if(isSoftPhoneSearch && memberIdSearch)
            {
                //clear the inputs other than MemberID from Wrapper. This is used for display in UI
                oSearchWrapper.sFirstName = '';
                oSearchWrapper.sLastName = '';
                oSearchWrapper.sBirthdate = '';
                oSearchWrapper.sPhone = '';
                oSearchWrapper.sPostalCode = '';
                oSearchWrapper.sGroupNumber = '';
                oSearchWrapper.sState = system.label.HUMSPhoneMappingObj_None;
                oSearchWrapper.sSuffix = ''; 
            }
            
            for (Member_Id__c oMemId: lstMembers)
            {                                     
               IF(oMemId.Policy_Member__c != Null  && oMemId.Policy_Member__r.Member__c != Null) mapAccounts.put(oMemId.Policy_Member__r.Member__r.ID, createAccountObj(oMemId));               
            }    
            dataSecurity(mapAccounts.values());
        }         
    }
    
    /*
     * Method Name   :  updateQuery
     * Description   :  This Method is used to add sorting for given SOQL query.
     * Return Type   :  String 
     * Parameters    :  String
     */
    @testVisible private String addSortOptionToSOQLQuery(String sTempQuery,String sOrderBy)
    {
        if(sTempQuery.contains(sOrderBy))
        {
            List<String> lstString = sTempQuery.split(sOrderBy);
            sSortDirection = String.isEmpty(sSortDirection) ? sHumSearchSortExpressionASC : sSortDirection;
            sTempQuery = lstString[0] + sOrderBy +' '+sSortDirection + ' Limit ' + iSearchLimit;
        }
        else
        {
            sSortDirection = String.isEmpty(sSortDirection) ? sHumSearchSortExpressionASC : sSortDirection;
            sTempQuery+= sOrderBy +' '+sSortDirection + ' Limit ' + iSearchLimit;
        }
        return sTempQuery;
    }
    
    /*
     * Method Name   :  getSOSLResults
     * Description   :  This Method is used to extract data based on the given query.
     * Return Type   :  List<Member_Id__c>
     * Parameters    :  String 
     */
    private List<Member_Id__c> getMemberIdQueryResults(String searchQuery)
    {
        List<Member_Id__c> lstMembers = new List<Member_Id__c>();
        List<List<sObject>> searchList = search.query(searchQuery);
        
        lstMembers = ((List<Member_Id__c>)searchList[0]);
        lstMembers  = filterSOSLResults(lstMembers);
        return lstMembers ;
    }
    
    /*
     * Method Name   :  filterSOSLResults
     * Description   :  This Method is used to filter member id SOSL query results.
     * Return Type   :  List<Member_Id__c>
     * Parameters    :  List<Member_Id__c>
     */
    private List<Member_Id__c> filterSOSLResults(List<Member_Id__c> lstMembers)
    {
        List<Member_Id__c> lstMembersFinal = new List<Member_Id__c>();
        For(Member_Id__c obj : lstMembers)
        {
            String sMemId = obj.Name;
            If(String.IsNotBlank(sMemId) && sMemId.startsWithIgnoreCase(oSearchWrapper.sMemberId))
            {           
                lstMembersFinal.Add(obj);
            }
        }
        return lstMembersFinal;
    }

    /*
     * Method Name   :  createAccoutObj
     * Description   :  This Method is used to create the Account Objects instances
     * Return Type   :  Account
     * Parameters    :  Member_Id__c Object instance
     */
    @testvisible private Account createAccountObj(Member_Id__c memObj)
    {
        Account acctObj = new Account();        
        acctObj.ID = memObj.Policy_Member__r.Member__r.ID;
        acctObj.Name = memObj.Policy_Member__r.Member__r.Name;
        acctObj.FirstName = memObj.Policy_Member__r.Member__r.FirstName;
        acctObj.LastName = memObj.Policy_Member__r.Member__r.LastName;
        acctObj.middlename = memObj.Policy_Member__r.Member__r.middlename;
		if(suffixonoff){
		acctObj.Suffix = memObj.Policy_Member__r.Member__r.Suffix;
		}
        acctObj.Individual_First_Name__c = memObj.Policy_Member__r.Member__r.Individual_First_Name__c;
        acctObj.Individual_Last_Name__c = memObj.Policy_Member__r.Member__r.Individual_Last_Name__c;
        acctObj.RecordTypeId = memObj.Policy_Member__r.Member__r.RecordTypeId;
        acctObj.Birthdate__c = memObj.Policy_Member__r.Member__r.Birthdate__c;
        acctObj.Deceased_Date__c = memObj.Policy_Member__r.Member__r.Deceased_Date__c ;
        acctObj.PersonEmail = memObj.Policy_Member__r.Member__r.PersonEmail;
        acctObj.PersonMailingStreet = memObj.Policy_Member__r.Member__r.PersonMailingStreet;
        acctObj.PersonMailingCity = memObj.Policy_Member__r.Member__r.PersonMailingCity;
        acctObj.PersonMailingStateCode = memObj.Policy_Member__r.Member__r.PersonMailingStateCode;
        acctObj.PersonMailingState = memObj.Policy_Member__r.Member__r.PersonMailingState;
        acctObj.PersonMailingPostalCode = memObj.Policy_Member__r.Member__r.PersonMailingPostalCode;
        acctObj.PersonHomePhone = memObj.Policy_Member__r.Member__r.PersonHomePhone;        
        acctObj.Home_Office_Account__c = memObj.Policy_Member__r.Member__r.Home_Office_Account__c;
        acctObj.Veteran_Status__c = memObj.Policy_Member__r.Member__r.Veteran_Status__c;
        acctObj.ETL_Record_Deleted__c = memObj.Policy_Member__r.Member__r.ETL_Record_Deleted__c;
        RecordType recType = new RecordType();
        recType.id = memObj.Policy_Member__r.Member__r.RecordTypeId;
        rectype.name = memObj.Policy_Member__r.Member__r.RecordType.Name;
        acctObj.recordtype = recType;         
        
        return acctObj;
    }
    
    /*
     * Method Name   :  createAccountObjConsumer
     * Description   :  This Method is used to create the Account Objects instances
     * Return Type   :  Account
     * Parameters    :  ConsumerID__c Object instance
     */
    @testvisible private Account createAccountObjConsumer(ConsumerID__c consumerObj)
    {
        Account acctObj = new Account();        
        acctObj.ID = consumerObj.Account__r.ID;
        acctObj.Name = consumerObj.Account__r.Name;
        acctObj.FirstName = consumerObj.Account__r.FirstName;
        acctObj.LastName = consumerObj.Account__r.LastName;
        acctObj.Middlename = consumerObj.Account__r.Middlename;
		if(suffixonoff){
		acctObj.Suffix = consumerObj.Account__r.Suffix;
		}
        acctObj.Individual_First_Name__c = consumerObj.Account__r.Individual_First_Name__c;
        acctObj.Individual_Last_Name__c = consumerObj.Account__r.Individual_Last_Name__c;
        acctObj.RecordTypeId = consumerObj.Account__r.RecordTypeId;
        acctObj.Birthdate__c = consumerObj.Account__r.Birthdate__c;
        acctObj.Deceased_Date__c = consumerObj.Account__r.Deceased_Date__c ;
        acctObj.PersonEmail = consumerObj.Account__r.PersonEmail;
        acctObj.PersonMailingStreet = consumerObj.Account__r.PersonMailingStreet;
        acctObj.PersonMailingCity = consumerObj.Account__r.PersonMailingCity;
        acctObj.PersonMailingStateCode = consumerObj.Account__r.PersonMailingStateCode;
        acctObj.PersonMailingState = consumerObj.Account__r.PersonMailingState;
        acctObj.PersonMailingPostalCode = consumerObj.Account__r.PersonMailingPostalCode;
        acctObj.PersonHomePhone = consumerObj.Account__r.PersonHomePhone;        
        acctObj.Home_Office_Account__c = consumerObj.Account__r.Home_Office_Account__c;
        acctObj.ETL_Record_Deleted__c = consumerObj.Account__r.ETL_Record_Deleted__c;
        RecordType recType = new RecordType();
        recType.id = consumerObj.Account__r.RecordTypeId;
        rectype.name = consumerObj.Account__r.RecordType.Name;
        acctObj.recordtype = recType;         
        return acctObj;
    }
        
    /*
     * Method Name   :  createSearchQuery
     * Description   :  This Method is called from dataSearch() when ID field value is entered on screen.
     * Return Type   :  Set of accounts ID
     * Parameters    :  Member Id
     */
    @testvisible private Set<ID> createSearchQuery(String sMemId)
    {
        List<Member_Id__c> lstMemberId = new List<Member_Id__c>();
        String sMemIdQuery = '';  
        String sMemberId = oSearchWrapper.sMemberId + '*';
        sMemIdQuery = 'FIND \'' + string.escapeSingleQuotes(sMemberId) + '\' IN NAME FIELDS RETURNING Member_Id__c(Name,Member_Dependent_Code__c ,Policy_Member__r.Member__r.Id, Policy_Member__r.ETL_Record_Deleted__c, Policy_Member__r.Member__r.Veteran_Status__c Where Policy_Member__r.ETL_Record_Deleted__c = false ';
        if(!String.isBlank(oSearchWrapper.sSuffix))
        {
            sMemIdQuery += ' AND Member_Dependent_Code__c = \'' + string.escapeSingleQuotes(oSearchWrapper.sSuffix) + '\' Limit ' + iSearchLimit+')';
        }
        else
        {
            sMemIdQuery += ' Limit ' + iSearchLimit+')';
        }
        lstMemberId = getMemberIdQueryResults(sMemIdQuery);
        for (Member_Id__c oMemId: lstMemberId)
        {
            setAccMemberId.add(oMemId.Policy_Member__r.Member__r.Id);
        }
        return setAccMemberId;
    }

     /*
     * Method Name   :  createSearchQueryConsumer
     * Description   :  This Method is called from dataSearch() when ID field value is entered on screen.
     * Return Type   :  Set of accounts ID
     * Parameters    :  Consumer Id
     */
    @testvisible private Set<ID> createSearchQueryConsumer(String sConsumerId)
    {
        List<ConsumerID__c> lstConsumerId = new List<ConsumerID__c>();    
        String sConsId = oSearchWrapper.sMemberId + '*';
        String sConsIdQuery = 'FIND \'' + string.escapeSingleQuotes(sConsId) + '\' IN ALL FIELDS RETURNING ConsumerID__c(Consumer_ID__c,Account__c ,Account__r.ID, Account__r.ETL_Record_Deleted__c Where Account__r.ETL_Record_Deleted__c = false Limit ' + iConsumerSearchLimit + ' )';
        List<List<sObject>> searchList = search.query(sConsIdQuery);
        lstConsumerId = ((List<ConsumerID__c>)searchList[0]);
        lstConsumerId = filterSOSLResultsConsumer(lstConsumerId);
        for (ConsumerID__c oConsumerId: lstConsumerId)
        {
            setAccConsumerId.add(oConsumerId.Account__r.Id);
        }
        return setAccConsumerId;
    }
    
     /*
     * Method Name   :  getEligibleAccountList
     * Description   :  This Method is used for returning results to Member search screen
     * Return Type   :  void
     * Parameters    :  String - Sort direction value
     */
    @testvisible private void getEligibleAccountList(string sSortDirection)
    {   
        List<Account> lstTempAccounts;
        String sQuery = '';
        if(!memberIdSearch)
        {            
            If(bIsNameSearch)
            {
                platformNameSearchQuery += ' Limit ' + iSearchLimit + ')';
                List<List<sObject>> searchList = search.query(platformNameSearchQuery);
                lstTempAccounts = ((List<Account>)searchList[0]);
                List<Account> lstMembersFinal = new List<Account>();
                Boolean bIsMatching  = true;
                String sIndFirstName ='';
                String sIndLastName = '';
                For(Account obj : lstTempAccounts)
                {
                    bIsMatching = true;
                    sIndFirstName = obj.Individual_First_Name__c;
                    sIndLastName = obj.Individual_Last_Name__c;
                    If(String.IsNotBlank(oSearchWrapper.sFirstName))
                    {
                         If(String.IsBlank(sIndFirstName) || (String.IsNotBlank(sIndFirstName) && !obj.Individual_First_Name__c.startsWithIgnoreCase(oSearchWrapper.sFirstName))) bIsMatching = false;
                    }
                    If(String.IsNotBlank(oSearchWrapper.sLastName))
                    {
                       If(String.IsBlank(sIndLastName) || (String.IsNotBlank(sIndLastName) && !obj.Individual_Last_Name__c.startsWithIgnoreCase(oSearchWrapper.sLastName))) bIsMatching = false;
                    }
                    If(bIsMatching)
                    {           
                        lstMembersFinal.Add(obj);
                    }
                }
                dataSecurity(lstMembersFinal);
		
                blntoomanyentries = false;               
                If(lstTempAccounts.size() > 100  && lstMembersFinal.isEmpty()){
                blntoomanyentries = true;                
                addErrorMessageToPage(Label.HUMSearch_returned_more_than_100_entries);}
                if(blntoomanyentries == false){
            
                        //if(setAccount.getResultSize() == 0) throw new HUMCustomException(System.Label.HUMMemberSearchCriteriaViolation); 
                }
                
               
            }
            else
            {
                sSearchQuery += ' Limit ' + iSearchLimit;
                dataSecurity(HUMWithoutSharingQueryHelper.getListOfsObjects(sSearchQuery, NULL, setAccMemberId, setAccIdForGrpNumber));
            }    
        }
        else
        {
            doRelationshipQuery();
        }          
    }
   
    /*
     * Method Name   :    reset
     * Description   :    This method is used to clear the search on Accounts
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void reset()
    {
        oSearchWrapper = new HUMMemberSearchWrapperHelper();
        unKnownMemberCheck = false;
        clearList();
        resetIconVariables();
    }
    
    /*
     * Method Name   :    clearList
     * Description   :    This method is used to clear the various lists
     * Return Type   :    void
     * Parameters    :    NA
     */
    @testVisible private void clearList()
    {
        if(setAccIdForGrpNumber != NULL)    setAccIdForGrpNumber.clear();
        if(setAccMemberId != NULL)    setAccMemberId.clear();
        if(setAccConsumerId != NULL)    setAccConsumerId.clear();
        setAccount = NULL;
        // Flushing the data which was fetching during previous search
        if(mapRecordData != null && !mapRecordData.isEmpty()){mapRecordData.clear(); }
        if(mapAccounts != null && !mapAccounts.isEmpty()){mapAccounts.clear();}
        if(lstFinalSearchResult != null && !lstFinalSearchResult.isEmpty()){lstFinalSearchResult.clear();}
        iConsumerSearchLimit = 101;
    }
     
    /*
    * Method Name   :    viewData
    * Description   :    This Method is for sorting of data by Last Name after search criterion is met for member search screen.                   
    * Return Type   :    void
    * Parameters    :    NA
    */
    public void viewData()
    {
        sSortDirection = String.isEmpty(sSortDirection) ? sHumSearchSortExpressionASC : sSortDirection;
        sSortDirection = sSortDirection.equalsIgnoreCase(sHumSearchSortExpressionASC) ? sHumSearchSortExpressionDESC : sHumSearchSortExpressionASC ;
        getEligibleAccountList(sSortDirection);
        checkResultSizeLimit();
    }
    
    /*
    * Method Name   :    dataSecurity
    * Description   :    This Method is for Security purpose. Used to display fewer records to External users
    * Return Type   :    void
    * Parameters    :    List<Account>
    */
    @testVisible private void dataSecurity(List<Account> lstMemberAccount)
    {
        resetIconVariables();
        List <ID> lstAccountList = new List<ID>();
        mapRecordHaveAccess = new Map<Id, Boolean>();
        List<AccountComparisionHelper_H_HUM> lstAccountsToSort = new List<AccountComparisionHelper_H_HUM>();
        for(Integer i = 0 ; i < lstMemberAccount.size() ; i++)
        {
            lstAccountList.add(lstMemberAccount.get(i).Id);
            lstAccountsToSort.Add(New AccountComparisionHelper_H_HUM(lstMemberAccount[i]));
        }
        List <UserRecordAccess> lstUserRecordAcess = [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : lstAccountList];
        for(UserRecordAccess userAccess : lstUserRecordAcess)
        {
            mapRecordHaveAccess.put(userAccess.RECORDID, userAccess.HASREADACCESS);
        }
                        
        lstFinalSearchResult = new List<Account>();
        lstMemberAccount = sortMemberSearchResults(lstAccountsToSort);
        lstFinalSearchResult.addAll(lstMemberAccount);
        
        // code to remove the 101th record
        if(!lstMemberAccount.isEmpty() && lstMemberAccount.size() > 100)
        {
            lstMemberAccount.remove(100);
        }
        if(memberIdSearch != Null && memberIdSearch)
            setAccount = new ApexPages.StandardSetController(lstMemberAccount.deepClone(true, true, true));
        else
            setAccount = new ApexPages.StandardSetController(lstMemberAccount);
            
        if(!lstMemberAccount.isEmpty() && lstMemberAccount.size() == 1)
        {
            Account memberAccount = lstMemberAccount.get(0);
            ApexPages.currentPage().getParameters().put('strMemberAccountId', memberAccount.Id);
            getMemberIcons(memberAccount);
            
        }
        setAccount.setPageSize(Integer.valueof(Label.ListSizePageMemSearch));
    }
    
    private List<Account> sortMemberSearchResults(List<AccountComparisionHelper_H_HUM> lstAccountsToSort)
    {
        sSortDirection = String.isBlank(sSortDirection) ? sHumSearchSortExpressionASC : sSortDirection;
        If(lstAccountsToSort.size() > 1) lstAccountsToSort.sort();
        
        List<Account> lstSortedList = new List<Account>();
        List<Account> lstLegacyDelAccts = new List<Account>();
        if(clicksource == true)
        {
            If(sSortDirection == sHumSearchSortExpressionASC)
            {
                For(Integer i = 0 ; i < lstAccountsToSort.size() ; i++)
                {
                    if(lstAccountsToSort[i].objAccount.Etl_Record_Deleted__c == False)
                        lstSortedList.add(lstAccountsToSort[i].objAccount);
                    else
                        lstLegacyDelAccts.add(lstAccountsToSort[i].objAccount);
                }
                lstSortedList.AddAll(lstLegacyDelAccts);
            }
            else
            {
                For(Integer i = lstAccountsToSort.size()-1 ; i >= 0 ; i--)
                {
                    if(lstAccountsToSort[i].objAccount.Etl_Record_Deleted__c == False)
                        lstSortedList.add(lstAccountsToSort[i].objAccount);
                    else
                        lstLegacyDelAccts.add(lstAccountsToSort[i].objAccount);
                }
                lstSortedList.AddAll(lstLegacyDelAccts);
            } 
        }
        else
        {
            If(sSortDirection == sHumSearchSortExpressionASC)
            {
                For(Integer i = 0 ; i < lstAccountsToSort.size() ; i++)
                {
                    lstSortedList.add(lstAccountsToSort[i].objAccount);
                }
            }
            else
            {
                For(Integer i = lstAccountsToSort.size()-1 ; i >= 0 ; i--)
                {
                    lstSortedList.add(lstAccountsToSort[i].objAccount);
                }
            }
        }
        return lstSortedList;
    }
        
    /*Method Name    : FieldCipherMaping
     * Description   :  This Method Is called when search member Is driven by Softphone 
     *                  And as input this method accept Wrapper data And return actual fieldname And Cipher text mapping 
     * Return Type   :  Map<String, String>
     * Parameters    :  HUMMemberSearchWrapperHelper oSearchWrapperwithCipher
     */  
     @testvisible private Map<String, String> FieldCipherMaping(HUMMemberSearchWrapperHelper oSearchWrapperwithCipher)
     {
        Map<String, String> accFieldCipher = new Map<String,String>();                
        
        if(isSoftPhoneSearch)
        {                 
            if (NULL != oSearchWrapperwithCipher && NULL !=oSearchWrapperwithCipher.mapfieldCipherMapping) 
            {
                if(oSearchWrapperwithCipher.sIntAboutType.equals(ConsumerManagementConstants_HUM.MEMBER)) 
                { 
                    accFieldCipher.put(OBJECT_ACCOUNT_API_FIRSTNAME,oSearchWrapperwithCipher.mapfieldCipherMapping.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'FirstName').Value__C));
                    accFieldCipher.put(OBJECT_ACCOUNT_API_LASTNAME, oSearchWrapperwithCipher.mapfieldCipherMapping.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'LastName').Value__C));
                    accFieldCipher.put(HUMAccountBirthDate , oSearchWrapperwithCipher.mapfieldCipherMapping.get(SOFTPHONE_PRPID_HUM__c.getInstance('DOB').Value__C));
                    accFieldCipher.put(HUMName, oSearchWrapperwithCipher.mapfieldCipherMapping.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'MemberId').Value__C));
                }
                else
                {
                    accFieldCipher.put(OBJECT_ACCOUNT_API_FIRSTNAME,oSearchWrapperwithCipher.mapfieldCipherMapping.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'FirstName2').Value__C));
                    accFieldCipher.put(OBJECT_ACCOUNT_API_LASTNAME, oSearchWrapperwithCipher.mapfieldCipherMapping.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'LastName2').Value__C));
                    accFieldCipher.put(HUMAccountBirthDate , oSearchWrapperwithCipher.mapfieldCipherMapping.get(SOFTPHONE_PRPID_HUM__c.getInstance('DOB2').Value__C));
                    accFieldCipher.put(HUMName, oSearchWrapperwithCipher.mapfieldCipherMapping.get(SOFTPHONE_PRPID_HUM__c.getInstance( 'MemberId2').Value__C));
                }           
            }
            sSoftPhoneMemberId = accFieldCipher.get(HUMName);
        }
        else
        {
            if(!String.isEmpty(oSearchWrapperwithCipher.sFirstName)) 
            accFieldCipher.put(OBJECT_ACCOUNT_API_FIRSTNAME, oSearchWrapperwithCipher.sFirstName);
                                    
            if(!String.isEmpty(oSearchWrapperwithCipher.sLastName)) 
            accFieldCipher.put(OBJECT_ACCOUNT_API_LASTNAME, oSearchWrapperwithCipher.sLastName);
            
            if(!String.isEmpty(oSearchWrapperwithCipher.sBirthdate)) 
            accFieldCipher.put(HUMAccountBirthDate, oSearchWrapperwithCipher.sBirthdate);
            
            if(!String.isEmpty(oSearchWrapperwithCipher.sMemberId))
            accFieldCipher.put(HUMName, oSearchWrapperwithCipher.sMemberId);
            
            if(!String.isEmpty(oSearchWrapperwithCipher.sMemberId))
            accFieldCipher.put(HUMConsumerTaxID, oSearchWrapperwithCipher.sMemberId);                        
        
            if(!String.isBlank(oSearchWrapperwithCipher.sPhone)) 
            accFieldCipher.put('PersonHomePhone', oSearchWrapperwithCipher.sPhone);
                  
            if(!String.isBlank(oSearchWrapperwithCipher.sPostalCode))
             accFieldCipher.put('PersonMailingPostalCode', oSearchWrapperwithCipher.sPostalCode);           
            
            if (!String.isBlank(oSearchWrapperwithCipher.sGroupNumber))
             accFieldCipher.put(System.Label.HUMGroupNumber, oSearchWrapperwithCipher.sGroupNumber);                              
        }   
       return accFieldCipher;
     }
     
     /*Method Name   : dataErrors
     * Description   :  This Method is called when member search is triggered from UI (Click of search button or hit of ENTER key)      
     * Return Type   :  Void
     * Parameters    :  None
     */
     public void dataErrors()
     {              
        try
        {
            if(memberIdErr) addErrorMessageToPage(HumSearchMemberIdAlphaNumericValidation);
        
            if(memberIdLengthErr) addErrorMessageToPage(HumSearchMemberIdCharacterValidation);
        
            if(suffixErr) addErrorMessageToPage(HumSearchSuffixNumericValidation);
        
            if(suffixLengthErr) addErrorMessageToPage(HUMSearchSuffixCharacterValidation);
        
            if(fNameError) addErrorMessageToPage(HumSearchFirstNameAlphaNumericValidation);
        
            if(lNameError) addErrorMessageToPage(HumSearchLastNameAlphaNumericValidation);
            
            if(lNameLengthErr) addErrorMessageToPage(HumSearchLastNameCharacterValidation);
            
                        
            if(birthDateErr) addErrorMessageToPage(HumMemberSearchBirthDateValidation);
            
            if(birthDateGreaterErr) addErrorMessageToPage(HUMSearchBirthDateValidation);
                    
            if(postalCodeErr) addErrorMessageToPage(HumZipcodeCharacterLimit);
            
            if(groupNumberErr) addErrorMessageToPage(HumSearchGroupNumberAlphaNumericValidation);
            
            if(phoneErr) addErrorMessageToPage(HumPhoneValidationForMember);
            
            if(phoneLengthErr) addErrorMessageToPage(HumPhoneCharacterLimit);
                    
            if(bIsMemberError) clearList();
        }
        catch(Exception ex)
        {
            bIsMemberError = true;
            HUMExceptionHelper.logErrors(ex, 'HUMMemberSearchControllerV2', 'dataErrors');
        } 
    }
    
     /*Method Name   :  addErrorMessageToPage
     * Description   :  This Method is used to add the validation error meessages on the Page.      
     * Return Type   :  Void
     * Parameters    :  Error Message string
     */
     public void  addErrorMessageToPage(String errorMessage)
     {
        bIsMemberError = true;
        HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning, errorMessage, true);  
     }     
     
     /*
     * Method Name   :  SearchConsumer
     * Description   :  This Method is called for Manual Search from UI(both for Search button click and hit of Enter key) on complete of Search Manual method    
     * Return Type   :  void
     * Parameters    :  NA
     */  
     public void SearchConsumer()
     {
        try
        {
            if(unKnownMemberCheck)
            {
                if(NULL != lstFinalSearchResult && !lstFinalSearchResult.isEmpty())
                {
                    iConsumerSearchLimit = iConsumerSearchLimit - lstFinalSearchResult.size();
                }
                BuildQueryConsumer();
                List<ConsumerID__c> lstConsumers = new List<ConsumerID__c>();
                if(memberIdSearch)
                {
                    List<List<sObject>> searchList = search.query(sConsumerSearchQuery);
                    lstConsumers = ((List<ConsumerID__c>)searchList[0]);
                    lstConsumers = filterSOSLResultsConsumer(lstConsumers);
                    if(lstConsumers.isEmpty())
                    {
                        if(NULL != lstFinalSearchResult && !lstFinalSearchResult.isEmpty())
                        {
                            dataSecurity(lstFinalSearchResult);     
                        }     
                    }
                    else
                    {
                        if(isSoftPhoneSearch)
                        {
                            //clear the inputs other than MemberID from Wrapper. This is used for display in UI
                            oSearchWrapper.sFirstName = '';
                            oSearchWrapper.sLastName = '';
                            oSearchWrapper.sBirthdate = '';
                            oSearchWrapper.sPhone = '';
                            oSearchWrapper.sPostalCode = '';
                            oSearchWrapper.sGroupNumber = '';
                            oSearchWrapper.sState = system.label.HUMSPhoneMappingObj_None;
                        }   
                        for (ConsumerID__c oConsumerId: lstConsumers)
                        {                                       
                            mapAccounts.put(oConsumerId.ID, createAccountObjConsumer(oConsumerId));               
                        }
                        dataSecurity(mapAccounts.values());
                    }  
                } 
            }
            else
            {
                dataSecurity(lstFinalSearchResult);
            }  
            checkResultSizeLimit();     
        }
        catch(Exception ex)
        {
            bIsMemberError = true;
            HUMExceptionHelper.logErrors(ex, 'HUMMemberSearchControllerV2', 'searchConsumer');
        }    
     } 
    /* 
     * Method Name   :  filterSOSLResults
     * Description   :  This Method is used to filter member id SOSL query results.
     * Return Type   :  List<ConsumerID__c>
     * Parameters    :  List<ConsumerID__c>
     */
    private List<ConsumerID__c> filterSOSLResultsConsumer(List<ConsumerID__c> lstConsumers)
    {
        List<ConsumerID__c> lstConsumersFinal = new List<ConsumerID__c>();
        for(ConsumerID__c obj : lstConsumers)
        {
            String sConsId = obj.Consumer_ID__c;
            If(sConsId.startsWithIgnoreCase(oSearchWrapper.sMemberId))
            {           
                lstConsumersFinal.Add(obj);
            }
        }
        return lstConsumersFinal;
    }

                        /*
     * Method Name   :  buildQueryConsumer
     * Description   :  This Method is used to :
     *                  a. Search Name 
     *                  b.  Build search query for consumer
     * Return Type   :  void
     * Parameters    :  NA
     */ 
     private void BuildQueryConsumer()
     {
        if(!memberIdSearch)
        {  
            if (!String.isBlank(oSearchWrapper.sMemberId)&&(NULL != mapEncryptedResult.get('Consumer_ID__c')))
            {
                Set<ID> setAccIdFormemberId = new Set<ID>();
                setAccIdFormemberId = createSearchQueryConsumer(mapEncryptedResult.get('Consumer_ID__c'));
                                
                List<Account> lstConsumerDetails = null;
                if(bIsNameSearch)
                {
                    sConsumerSearchQuery += ' AND ID IN : setAccIdFormemberId )';
                    List<List<sObject>> searchList = search.query(sConsumerSearchQuery);
                    lstConsumerDetails = ((List<Account>)searchList[0]);
                }
                else
                {
                    sConsumerSearchQuery += ' AND ID IN : setAccIdFormemberId ';
                    lstConsumerDetails = (List<Account>)HUMWithoutSharingQueryHelper.getListOfsObjects(sConsumerSearchQuery, NULL, setAccConsumerId, NULL);
                }
                if(null != lstConsumerDetails && !lstConsumerDetails.isEmpty())
                {
                    dataSecurity(lstConsumerDetails);
                }
                else
                {
                    dataSecurity(lstFinalSearchResult);
                }
            }
            else
            {
                dataSecurity(lstFinalSearchResult);
            }
        }
        else
        {  
            String sConsId = oSearchWrapper.sMemberId + '*';
			if(suffixonoff)
			{
            sConsumerSearchQuery = 'FIND \'' + string.escapeSingleQuotes(sConsId) + '\' IN ALL FIELDS RETURNING ConsumerID__c(Consumer_ID__c, Account__r.ID, Account__r.Name, Account__r.FirstName, Account__r.LastName,Account__r.Suffix, Account__r.Middlename,'
                        + 'Account__r.Individual_First_Name__c, Account__r.Individual_Last_Name__c,Account__r.RecordTypeId, Account__r.RecordType.Name, Account__r.Birthdate__c, Account__r.Deceased_Date__c, Account__r.PersonEmail, Account__r.PersonMailingStreet, '
                        + 'Account__r.PersonMailingCity, Account__r.PersonMailingStateCode, Account__r.PersonMailingState, Account__r.PersonMailingPostalCode, Account__r.PersonHomePhone, '
                        + 'Account__r.Home_Office_Account__c, Account__r.ETL_Record_Deleted__c, Account__r.Veteran_Status__c Where Account__r.ETL_Record_Deleted__c = false and ID_Type__C in (\'MedicareID\',\'Medicaid-Id\', \'HumanaId\', \'SSN\', \'CBIS ALT ID\') Limit ' + iConsumerSearchLimit +')';
						
			}
        }
     }
     /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the MBE plus continuation request
    *
    * @return continuation object
    */
    public Continuation invokeMBEPlusService()
    {
        sourcePersonId = osearchwrapper.sPID;
        return oServiceMembPlus.sendRequestMBEPIDService(this);
        
    }
   
    /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the service response
    *
    * @return Object
    */
    public Object parseMBEPlusResponse()
    {
        return oServiceMembPlus.processMBEPIDRequest(this);
    } 
    /*
        * Method Name   :    buildEnterpriseIDQuery
        * Description   :    This method is used to get Records based on enterprise ID 
        * Return Type   :    void
        * Parameters    :    NA
        */   
    public void buildEnterpriseIDQuery()
    {
        try
        {
            if(HUMUtilityHelper.isCRMFunctionalityON('1426106')) {
                suffixonoff = HUMUtilityHelper.isCRMFunctionalityON('755829'); //Prod Incident Fix US1426106
            }
            String enterpriseID = sEnterpriseId;
            List<Account> accountList=new List<Account>();
            clearList();
            memberIdSearch = false;
            if(enterpriseID!=null && String.isNotBlank(enterpriseID))
            {
			if(suffixonoff)
			{
				accountList = [SELECT ID,Name,FirstName, LastName, Middlename,Suffix, Individual_First_Name__c,Individual_Last_Name__c, Birthdate__c,Deceased_Date__c,PersonEmail,PersonMailingStreet,
                               PersonMailingCity, PersonMailingStateCode, PersonMailingState, PersonMailingPostalCode,PersonHomePhone,RecordType.Name, 
                               ETL_Record_Deleted__c,Enterprise_ID__c, Home_Office_Account__c, Veteran_Status__c, UserRecordAccess.HasReadAccess FROM Account WHERE Enterprise_ID__c =:enterpriseID];
							   
			}
                if(accountList!=null &&!accountList.isEmpty())
                {
                    dataSecurity(accountList);
                }
                else
                {
                    addErrorMessageToPage(sPID_MEMBERNOTAVIALBLE); 
                }
            }
            else
            {
                addErrorMessageToPage(sPID_MEMBERNOTAVIALBLE); 
            }
        }
        catch(Exception ex)
        {
              bIsMemberError = true;
              HUMExceptionHelper.logErrors(ex, 'HUMMemberSearchControllerV2', 'buildEnterpriseIDQuery');
        } 
    }   
    
    /**
    * Executes icons logic
    *@param : NA
    * @return void
    */
    private void getMemberIcons(Account memberAccount)
    {
        map<Integer,Integer> mapIconsWidths = new map<Integer,Integer>{
        0 => 100,
        1 => 97,
        2 => 95,
        3 => 92,
        4 => 90,
        5 => 88};
        
        MemberIcons_C_HUM objClass = new MemberIcons_C_HUM();
        objClass.sRecordId = memberAccount.Id;
        objClass.sPageName = 'Search';
        objClass.sBirthDate = memberAccount.Birthdate__c;
        objClass.sDeceasedDate = memberAccount.Deceased_Date__c;
         objClass.sVeteranStatus = memberAccount.Veteran_Status__c;
        objClass.scurrInteractionId = '';
        objClass.getMemberIconStatus();
        
        objIconsVisibility = objClass.objIcons;
        If(objIconsVisibility.lstMemberIcons != Null && !objIconsVisibility.lstMemberIcons.isEmpty()) 
        {
           tdSearchResultWidth = mapIconsWidths.get(objIconsVisibility.lstMemberIcons.size());
           tdIconsWidth = 100 - tdSearchResultWidth;
        }
    }  
    
    private void resetIconVariables()
    {
        objIconsVisibility = New MemberIcons_DTO_HUM();
        tdSearchResultWidth = 100;
        tdIconsWidth = 0;
    }  
}