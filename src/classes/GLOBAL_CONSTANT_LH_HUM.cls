/********************************************************************************************************************************************************
Apex Class Name  : GLOBAL_CONSTANT_LH_HUM
Version          : 1.0 
Created Date     : 09/18/2020
Test Class       : GLOBAL_CONSTANT_T_HUM
Function         : Constants declaration here that are going to be used across the org.
Modification Log :       
* Developer                       Code review              Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Joel Oommen George                                    09/18/2020            Original Version
* Ankima Srivastava                                     03/05/2021            US 1895067 - Verify Demographics
* Ashish KUmar											03/22/2021			  Added constant iLimitOne for using in SOQL
* Ankima Srivastava                                     04/07/2021            Added constant whiteColorCode to show background color
* Kajal Namdev                                          05/07/2021            Added constants using in lightning classes
* Ankima Srivastava										06/21/2021			  Added constants for Member Plan Eligibility
* Ankima Srivastava										07/01/2021			  Added constants for MTV member plan remark 
* Ankima Srivastava										08/09/2021			  Modified constant for MTM - Eligibility 
* Surendra Vemulapalli                                  09/07/2021            Line#238 added constant MTVx link
* Ankima Srivastava										09/30/2021			  Added constant for Product type
* Ankima Srivastava										10/07/2021			  Added constant for Eligibility by Product type
* Firoja Begam										    18/01/2021			  Added constant to Replace LIS with Extra help
* Gowthami Thota                                        02/28/2023            Added constant for profiles to execute COntentVersion trigger
*******************************************************************************************************************************************************/
public Class GLOBAL_CONSTANT_LH_HUM
{
    public Static final String selectMEDPolicy = 'selectMEDpolicy';
    public Static final String sPolicies = 'Policies';
    public Static final String sMed = 'MED';
    public Static final String sActive = 'Active';
    public Static final String sCRMStory = '738134';
    public Static final String sMedicarePartC = 'Medicare Part C';
    public Static final String sMedicarePartD = 'Medicare Part D';
    public Static final String sFalse = 'false';
    public Static final String sTrue = 'true';
    public Static final String sMember = 'Member';
    public Static final String sPharmacyMember = 'PharmacyMember';
    public Static final String sRccMember = 'RccMember';
    public Static final String sGboMember = 'GboMember';
    public Static final String sProviderMember = 'ProviderMember';
    public Static final String sConsumerId = 'Consumer_ID__c';
    public Static final String sPersonHomePhone = 'PersonHomePhone';
    public Static final String sPersonMailingPostalCode = 'PersonMailingPostalCode';
    public Static final String sFirstName = 'FirstName';
    public Static final String sLastName = 'LastName';
    public Static final String sBirthdate = 'Birthdate__c';
    public Static final String sName = 'Name';
    public Static final String sPidError = 'No Records found for PID provided';
    public Static final String sCallBackMethod = 'getparseMBEPlusResponse';
    public Static final String sMbePlus = 'MBEPlusService';
    
    public Static final String sGroupNumberField='Group_Number__c';
    public Static final String sGroupSourceCustomerKeyField='Source_Customer_Key__c';

    public Static final Integer Member_SearchLimit = 101;
    public Static final Integer HUMConstantZerovalue = 0;
     //string for searchfieldshelper for Encryption
    public Static final String OBJECT_ACCOUNT_API_FIRSTNAME = 'FirstName';

    //Constants for Group Detail Division functionality
    public Static final Integer MaxRecordToFetch = 100;
    public Static final Integer GroupDivisonMax = 6;
    
    //string for searchfieldshelper for Encryption
    public Static final String OBJECT_ACCOUNT_API_LASTNAME = 'LastName';
    public Static final String HUMName = 'Name';
    public Static final String HUMAccountBirthDate = 'Birthdate__c';
    public Static final String HUMConsumerTaxID = 'Consumer_ID__c';
    public Static final String HumMemberRecordType = 'Member';
    public Static final String HUM_UnknownMember_Account_Record_Type = 'Unknown_Member'; 
    public Static final String HUMStateDefaultValue = 'None';
    public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';
    public Static final String HUM_PCP_SER_CONNECTION = 'Connection';
   
	/*****GD:09/27/2020: Code Starts******/
    //Added constants for HUMAgencyBrokerSearch controller class
    public static final string HUMSearchAgencyBrokerMethod = 'searchAgencyBroker';
    public Static final String HUM_Agency_Account_Record_Type_Name = 'Agent/Broker';//Value not present in Mercury and Test- IMP - Label not present???
    public Static final String HUM_UnknownAgency_Account_Record_Type_Name = 'Unknown Agent/Broker';
    /*******GD:09/27/2020: Code Ends*******/
    public Static final string OSB_Indicator_value = 'O';
    
    //For UserAssociatedInformation_LS_HUM
    public static final string sEexcutorOfEstate  = 'Executor of Estate';
    public static final string sConsentForPHI  = 'Consent for PHI';
    public static final string sMedicalChild  = 'Medical Child Support Order';
    public static final string sGuardian  = 'Legal Guardian';
    public static final string sGo365  = 'Go365 Medicaid Guardian';
    public static final string sPowerOfAttorney = 'Power of Attorney';
    
    //For SOQL limits
    public static final Integer iLimitOne = 1;

    //For Verify Demographics continuation
    public static final integer cont = 120;
    public static final integer daysCheck = 90;
	
    //For color coding in Interaction/Cases count
    public static final string whiteColorCode  = 'white';
	Public Static final String HUMAgencySearchAgentTypeValue1 = 'All';
	Public Static final String HUMAgencySearchAgentTypeValue2 = 'Agency';
	Public Static final Integer HUMAgencyObjectSize = 100;
	Public Static final String HUMSearch_returned_more_than_100_entries = 'Your search returned more than 100 entries. Please refine your search.';
	Public Static final String HUMAgencySearchAgentTypeValue3 = 'Broker';
	Public Static final String HUM_Agency_Account_Record_Type = 'Agent_Broker';//Value not present in Mercury and Test- IMP - Label not present???
	Public Static final String HUM_UnknownAgency_Account_Record_Type = 'Unknown_Agent_Broker';//Value not present in Mercury and Test- IMP - Label not present???
	Public Static final String HUMAgentBrokerRecordTypeName = 'Agent/Broker';
	Public Static final Integer HUMMaxDateYear = 4000;
	Public Static final Integer HUMMinDateYear = 1700;
	Public Static final String EXT_ID_AGENT = 'AGN|00|SOLAR|';
	Public Static final String TENANTID_GEN = '00';
	Public Static final String HUMAEHistSvc = 'AEHistoryService';
	Public Static final String HUMSearchEnrollmentAEHistory = 'HUMSearchEnrollmentAEHistory';
	Public Static final String HUMAEHistReqHeader = 'CCPAEREQST';
	Public Static final Integer ListSizePageMemSearch = 10;
	Public Static final String HUMEESLegacyMemberSearchHelper_Coverages_PlatformMemberCoverage = 'Coverages:PlatformMemberCoverage';
	Public Static final String sLV_Value= 'LV';
	Public Static final String sZeroZeroOne = '001';
	Public Static final String sZeroOne = '01';
	Public Static final String sDummyBenValue = 'XXXXXXXX';
    Public Static final Integer HTTP_OK = 200;
    Public Static final Integer HTTP_ACCEPTED = 202;
	Public Static final String sYESValue = 'Y';
    Public Static final String sNoValue = 'N';
    Public Static final String sDayValue = 'D';
    Public Static final String sMonthValue = 'M';
	Public Static final String sFMValue = 'FM';
    Public Static final String sIMValue = 'IM';
    Public Static final String sEMvalue = 'EM';
    Public Static final String sGDvalue = 'GD';
    Public Static final String sHourValue = 'H';
	Public Static final String sMTV_Value = 'MTV';
    Public Static final String sCI_Value = 'CI';
    Public Static final String sCAS_Value = 'CAS';
    Public Static final String sEM_Value= 'EM';
	Public Static final String HUM_UnknownGroup_Account_Record_Type = 'Unknown_Group';
	Public Static final String HUM_Group_Account_Record_Type = 'Group';
	Public Static final String UNKNOWN_MEMBER = 'Unknown Member';
	Public Static final Integer HUMConstantZero = 0;
	Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
	Public Static final String HUM_Member_Search_Fields_Not_Entered = 'Please enter value for at least one of the Search fields for Member'; //Value not present in Mercury and Test- IMP - Label not present???
	Public Static final String HumErrorBirthDateSearchMedicare = 'Please search using Birth Date + any of the following secondary criteria: ID, First Name + Last Name';
	Public Static final String HumErrorBirthDateSearchCombination = 'Please Enter First Name and Last Name with Birth Date field';
	Public Static final String HumErrorZipCodeSearchMedicare = 'Please search using Zip Code + any of the following secondary criteria: ID, First Name + Last Name';
    Public Static final String HumErrorPostalCodeSearchCombination = 'Please enter First Name and Last Name with Zip Code field';
	Public Static final String HumErrorStateSearchMedicare = 'Please search using State + any of the following secondary criteria: ID, First Name + Last Name';
	Public Static final String HumErrorStateSearchCombination = 'Please Enter First Name and Last Name with State field';
	Public Static final String HumErrorFirstNameSearchMedicare = 'Please search using First Name + any of the following secondary criteria: Last Name, ID, Birth Date, State, or Zip Code';
	Public Static final String HumErrorLastNameSearchMedicare = 'Please search using Last Name + any of the following secondary criteria: First Name, ID, Birth Date, State, or Zip Code';
	Public Static final String HumSearchSuffixMemberIdValidation = 'ID field is required when a Suffix is present.';
	Public Static final String HumErrorBirthDateSearch = 'Please search using Birth Date + any of the following secondary criteria: ID, Group Number, Phone, First Name + Last Name';
	Public Static final String HumErrorZipCodeSearch = 'Please search using Zip Code + any of the following secondary criteria: ID, Group Number, Phone, First Name + Last Name';
	Public Static final String HumErrorStateSearch = 'Please search using State + any of the following secondary criteria: ID, Group Number, Phone, First Name + Last Name';
	Public Static final String HumErrorFirstNameSearch = 'Please search using First Name + any of the following secondary criteria: Last Name, ID, Group Number, Phone, Birth Date, State, or Zip Code';
	Public Static final String HumErrorLastNameSearch = 'Please search using Last Name + any of the following secondary criteria: First Name, ID, Group Number, Phone, Birth Date, State, or Zip Code';
	Public Static final String HumProviderRecordType = 'Provider';
	Public Static final String HumUnknownProviderRecordType = 'Unknown_Provider';
	Public Static final String sFacilityprovider ='F';
	Public Static final String sGroupprovider ='G';
	Public Static final String sIndividualprovider ='I';
	Public Static final String sTaxID = 'sTaxID';
    Public Static final String sNPI = 'sNPI';
    Public Static final String sFacilityName = 'sFacilityName';
    Public Static final String sPostalCode = 'sPostalCode';
    Public Static final String sSpeciality = 'sSpeciality';
	Public Static final Map<String, String> DOCUMENT_TYPE= New Map<String, String>{'PWD' => 'Password'};
	Public Static final String HUMDateFormat = 'MM/dd/YYYY';
	Public Static final Integer HTTP_OK_HUM = 200;
	Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
	Public Static final String VOB_FAULT_CODE = 'faultcode';
	Public Static final String  VOB_PLATFORM_LV_HUM = 'LV';
	Public Static final String  VOB_PLATFORM_EM_HUM = 'EM';
	Public Static final String  VOB_CURRENT_HUM = 'Current';
	Public Static final String  VOB_PAST_HUM = 'Past';
	Public Static final String  VOB_FUTURE_HUM = 'Future';
	Public Static final String NONPHARMACY_USER_HUM = 'No';
	Public Static final String MEDICARE_POLICY = 'MED'; 
	Public Static final String PRODUCT_TYPE_MA = 'MA';
	Public Static final String PRODUCT_TYPE_PDP = 'PDP';
	Public Static final String MEDICARECALLS_PERMISSIONSET_HUM = 'CRMS_684_Medicare_Customer_Service_Access';
	Public Static final String POA_CONSTRECORD = 'Record';
	Public Static final String POA_VALIDPLATFORMS = 'LV;';
	Public Static final String POA_VALIDMEMBERTYPES = 'Member-Id-Base;';
	Public Static final Integer VOB_ServiceTimeOut_HUM = 120; 
	public Static final String HUM_NO_ACTIVERECORD = 'No active record found';
	Public Static final String  Go365_Medicaid_Guardian = 'Go365 Medicaid Guardian';
	Public Static final String  AOR= 'Appointment of Representative';
	Public Static final String  Power_Of_Attorney = 'Power of Attorney';
	public Static final String  Consent_for_PHI= 'Consent for PHI';
	Public Static final String Content_type_HUM = 'content-type';
	Public Static final String Connection_HUM = 'Connection';
	Public Static final String Keep_alive_HUM = 'keep-alive';
	Public Static final String SOAPAction_HUM = 'SOAPAction';
	public Static final String PARSEINQUIRYSUMMARY = 'parseInquirySummary';
	public static final Integer TimeOutStatusCode = 2000;
	public Static final String TimeoutErrorMsg = 'The timeout was reached, and the server did not get a chance to respond. Service Name: Data Retention Inquiry Summary';
	Public Static final String PRODUCT_TYPE_MAPD = 'MAPD';
	public static final string PRODUCT_TYPE_MGP = 'MGP';
	public static final string PRODUCT_TYPE_MGR = 'MGR';
	public static final string PRODUCT_TYPE_MGF = 'MGF';
    public static final string PRODUCT_TYPE_MPD = 'MPD';
    
    public static final string PRODUCT_TYPE_MER = 'MER';
    public static final string PRODUCT_TYPE_MEP = 'MEP';
    public static final string PRODUCT_TYPE_MEF = 'MEF';
    public static final string PRODUCT_TYPE_MRO = 'MRO';
    public static final string PRODUCT_TYPE_MES = 'MES';
    public static final string PRODUCT_TYPE_MGS = 'MGS';
    public static final string PRODUCT_TYPE_MCD = 'MCD';

    public static final string PRODUCT_TYPE_MGO = 'MGO';
    public static final string PRODUCT_TYPE_MRP = 'MRP';
    public static final string PRODUCT_TYPE_MSL = 'MSL';
    public static final string PRODUCT_TYPE_MSP = 'MSP';
    public static final string PRODUCT_TYPE_RSK = 'RSK';
    public static final string PRODUCT_TYPE_SUP = 'SUP';

	Public Static final String SYSTEM_ADMINISTRATOR_PROFILE_HUM = 'System Administrator';
	Public Static final String DEPLOYMENT_PROFILE_HUM = 'Deployment'; 
	Public Static final String PHARMACYSPECIALIST_PROFILE_HUM = 'Humana Pharmacy Specialist';
	Public Static final String MAJOR_LOB_MES = 'MES';
	Public static final string sPersonAccount = 'Person Account';
	public static final string sBusinessAccount = 'Business Account';
	Public Static final String INTERACTIONID_HUM = '&interactionId=';
	Public Static final Integer NUMBER_CONSTANT_MINUSONE_HUM = -1; 
	Public Static final Integer NUMBER_CONSTANT_FIFTEEN_HUM = 15;
	Public Static final Integer NUMBER_CONSTANT_THIRTYTHREE_HUM = 33;
    //For MTV Member Plan Remark
    Public Static final String MEMBER = 'Member';
    public static final Integer TwoYearOldDate = -2;
    //For Member Plan Eligibility
    Public Static final String ESRD = 'EndStageRenalDisease';
    Public Static final String LTSS = 'LongTermSupportServices';
    //For LIS label upgrade to Extra Help
    //Public Static final String LIS = 'LowIncomeSubsidy';
    Public Static final String LIS = 'ExtraHelp';
    Public Static final String SNP = 'SpecialNeedsPlan';
    Public Static final String MTM = 'MedicationTherapy';
    Public Static final String Eligible = 'Eligible';
    Public Static final String Effective_Date = 'EffectiveDate';
    Public Static final String End_Date = 'EndDate';
    Public Static final String Percent_Copay = 'Level';
    Public Static final String SNPType = 'Type';
    Public Static final String HUMEligibleYes = 'Yes';
    Public Static final String HUMEligibleNo = 'No';
    Public Static final String HUMCharY = 'Y';
    Public Static final String HUMCharN = 'N';
    Public Static final String HUMNoValue = 'No Value';
    Public Static final String HUM_RSS_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_RSS_SER_CONECTION = 'Connection';
    Public Static final String HUM_RSS_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_RSS_SER_SOAPACTION = 'SOAPAction';
    Public Static final String POA_SERVICENAME = 'CIMedicareMultipleMemberDetails';
    Public Static final String Do_Not_Contact = 'Do Not Contact';
    Public Static final String Completed = 'SOAPAction';
    Public Static final String MTMCompleted = 'Completed';

    Public static final string coverageStatus = 'Termed,Future,Active';
 Public static final string sMTVLink = 'MTVx#&;';
  //Added for ContentVersion trigger execution
    Public Static final List<String> lstAvailableProfiles = New List<String>{'Customer Care Specialist','Customer Care Supervisor','Humana Pharmacy Specialist','ETL API Access'};
}