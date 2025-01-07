/********************************************************************************************************************************************************
Apex Class Name  : GLOBAL_CONSTANT_CH_HUM
Version          : 1.0 
Created Date     : 03/09/2021
Test Class       : 
Function         : Constants declaration here that are going to be used across the org.
Modification Log :       
* Developer                       Code review              Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Jasmeen Shangari                                      03/09/2021            Original Version
* Jasmeen Shangari                                      05/18/2021            REQ - 1800806 - Added constant for Wellness profile 
* Moshitha Gunasekaran									05/23/2021			  US1801017 Made changes for Digital messaging
* Prudhvi Pamarthi                                      05/22/2021             US#1800508 - Added constants for Associated forms and POA sections
* Moshitha Gunasekaran									06/04/2021			  US1801017 Made changes for Member Case View
* Moshitha Gunasekaran									08/04/2021			  US2495909 Made changes to add space in Interaction Origin
*******************************************************************************************************************************************************/
public Class GLOBAL_CONSTANT_CH_HUM
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
    public static final List<String> POLICY_MEMBER_FIELDS = New List<String>{'Effective / End Date', 'Member ID','Product','Group','Plan Name'};
    
    public static final String sWellnessCoachProfile = 'Humana Wellness Coach'; 
    public static final String sWellnessManagerProfile = 'Humana Wellness Manager'; 
    public static final String sCoachWorkTask = 'Coach Work Task'; 
    public static final String sCoachMemberTask = 'Member Action Item';

    //Added constant for Chatter Service
    public static final String FEEDITEM = 'FeedItem';
    public static final String FEEDCOMMENT = 'FeedComment';
    public static final String FeedElementErrorMessage = 'Please pass valid value for FeedElementType.';
    public static final String CarePlanErrorMessage = 'Please pass valid value for CarePlanType.';
    public static final String PersonIdErrorMessage = 'PersonId passed is not valid in CRM.';
    public static final String CoachingInteractionRecordType = 'WellnessCoachingInteraction';
    public static final String IncomingDigitalOrigin = 'Incoming Digital';
    public static final String CoachingCreatedQueue = 'Wellness Humana Wellness Solutions';
    public static final String DatapowerUserName = 'PowerAPI Data';   
    public static final String CoachingAdministrationGroup = 'CoachingAdministration';
    public static final String CoachingSubject = 'Subject:- Wellness Coaching Feed ';
    public static final String sCarePlanRecord = 'CarePlan'; 

    //For Power of Attorney
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String POA_CONSTRECORD = 'Record';
    Public Static final Integer HTTP_ACCEPTED_HUM = 202;
    Public Static final String POA_VALIDMEMBERTYPES = 'Member-Id-Base;';
    Public Static final String POA_VALIDPLATFORMS = 'LV;';
    Public Static final Integer VOB_ServiceTimeOut_HUM = 120;
    //For SOQL limits
    public static final Integer iLimitOne = 1;
    //For Associated forms
    public static final string sEexcutorOfEstate  = 'Executor of Estate';
    public static final string sConsentForPHI  = 'Consent for PHI';
    public static final string sMedicalChild  = 'Medical Child Support Order';
    public static final string sGuardian  = 'Legal Guardian';
    public static final string sGo365  = 'Go365 Medicaid Guardian';
    public static final string sPowerOfAttorney = 'Power of Attorney';
    public Static final String HUM_NO_ACTIVERECORD = 'No active record found';
    Public Static final String  AOR= 'Appointment of Representative';
    Public Static final String VOB_FAULT_CODE = 'faultcode';  
    Public Static final String HUMDateFormat = 'MM/dd/YYYY';
    Public Static final Map<String, String> DOCUMENT_TYPE= New Map<String, String>
        {
        'PWD' => 'Password'
        };

}