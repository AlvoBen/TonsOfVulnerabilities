/********************************************************************************************************************************************************
Apex Class Name 	: PhoneBookConstants_H_HUM
Version				: 1.0 
Created Date     	: 15th March, 2021
Function         	: Constants declaration here that are going to be used across for PhoneBook Classic and lightning view.
Modification Log 	: 
* Developer               			           Code review         Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Shreya Agrawal/Isha Gupta/Navajit Sarkar                       03/15/2021            Original Version
* Arpit Jain                                                     07/12/2021            US-2357061 : IVR Call End or Transfer related constants added
*Sahil Verma/Navajit Sarkar                                      07/12/2021            User story -2287945 added related constants
*Isha Gupta                                                      08/03/2021            User story -2484206,2484212 added related constants
*Harshada Kamble                                                 08/16/2022            User story -3701398,3653389 added related constants
*Harshada Kamble                                                 09/30/2022            User Story -3791679: added related constants
*Harshada Kamble                                                 01/06/2023            User Story -3849706 & 3944555: added related constants
*Harshada Kamble                                                 01/06/2023            User Story -3959734: added related constants
*Harshada Kamble                                                 01/06/2023            User Story -3944531: added related constants
*Harshada Kamble                                                 02/03/2023            US 3980680: T1PRJ0036776: RxE - Save Campaign name and keycode to CRM interactions
*Harshada Kamble                                                 02/03/2023            US 3979946: T1PRJ0036776: PCC VOC Survey Transfer Identification 
*Harshada Kamble/Anil Pavithran                                  03/05/2023            User Story 4144165: T1PRJ0307696: CRM Service: Format is incorrect/Unable to authenticate - DOB Format
*Harshada Kamble/Anil Pavithran                                  03/09/2023            US 2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
*Sivaprakash Rajendran                                          04/27/2023            US 4040542: T1PRJ0036776: Enterprise - Caller Type Population with Phonebook - Classic (Genesys)
*Mayur Pardeshi                                                  06/16/2023				User Story 4734084: T1PRJ0036776: PCC Lightning - Second Member Information Is Not Being Updated on 'Interacting About' field from Watson in a Multimember auth call (Genesys)
*Mayur Pardeshi                                                  06/16/2023				User Story 4726311: T1PRJ0036776: PCC Classic - Second Member Information Is Not Being Updated on 'Interacting About' field from Watson in a Multimember auth call (Genesys)
*Harshada Kamble                                                 06/22/2023             User Story 4696940: T1PRJ0036776: Classic - Multi-member transfer call handling
*Harshada Kamble                                                 06/22/2023             User Story 4707880: T1PRJ0036776: Lightning - Multi-member transfer call handling - Provider (Genesys)
*Mayur Pardeshi                                                  07/05/2023				User Story 4599393: T1PRJ0036776: Ability To Create Interactions in Genesys when calls are placed to Producer Services
*Harshada Kamble                                                 08/10/2023             User Story 4954802: T1PRJ0307696: INC2463199 Watson VOC data error for AGENT_CALL_START_TIME (Genesys)
*Kiran kotni                                                     08/18/2023             US 4863232: T1PRJ0036776: RxE - Lightning - Save Campaign Keycode & Campaign name data to existing column "interaction :campaign name and Keycode" - Genesys
*Harshada Kamble                                                 01/25/2024             User Story 5602717: T1PRJ0036776: Pass NPI & Tax ID from Watson IVR to Genesys to CRM Salesforce Service
*Mayur Pardeshi                                                  02/05/2024            User Story 5668340: T1PRJ0036776: Alphabetize Phone Book
**********************************************************************************************************************************/

public with sharing class PhoneBookConstants_H_HUM 
{
    Public Static final String CALLER_MEMBER_VALUE='Member';
    Public Static final String CALLER_PROVIDER_VALUE='Provider';
    Public Static final String CALLER_GROUP_VALUE='Group';
    Public Static final String CALLER_AGENT_VALUE='Agent';
    Public Static final String CALLER_AGENT_BROKER_VALUE='Agent/Broker';
    Public Static final String CALLER_UNKNOWN_MEMBER_VALUE ='Unknown Member';
    Public Static final String CALLER_UNKNOWN_GROUP_VALUE ='Unknown Group';
    Public Static final String CALLER_UNKNOWN_PROVIDER_VALUE ='Unknown Provider';
    Public Static final String CALLER_UNKNOWN_AGENT_BROKER_VALUE ='Unknown Agent/Broker';
    Public Static final String CALLER_UNKNOWN ='Unknown';
    Public Static final String DEFAULT_VAL='Transfer Phone Book';
    Public Static final String SELECT_VAL='Select';
    Public Static final String ASO_IND_VAL='ASO_IND';
    Public Static final String BUS_SEGMENT_IND_VAL='BUS_SEGMENT_IND';
    Public Static final String HDHP_IND_VAL='HDHP_IND';
    Public Static final String INQA_DOB_VAL='INQA_DOB';
    Public Static final String INQA_GRP_ID_VAL='INQA_GRP_ID';
    Public Static final String INQA_PLTFRM_CD_VAL='INQA_PLTFRM_CD';
    Public Static final String MAJOR_LOB_VAL='MAJOR_LOB';
    Public Static final String MEMBER_ID_VAL='MEMBER_ID';
    Public Static final String MTV_BL5_VAL='MTV_BL5';
    Public Static final String MTV_BL7_VAL='MTV_BL7';
    Public Static final String PROD_TYPE_CODE_VAL='PROD_TYPE_CODE';
    Public Static final String SELLING_LEDGER_VAL='SELLING_LEDGER';
    Public Static final String STATE_OF_ISSUE_VAL='STATE_OF_ISSUE';
    Public Static final String VENDOR_ASOFDATE_VAL='VENDOR_ASOFDATE';
    Public Static final String VENDOR_SUB_GROUPID_VAL='VENDOR_SUB_GROUPID';
    Public Static final String SOFTPHONE_VAL='Softphone';
    Public Static final String DATANAME_VAL='DataName';
    Public Static final String DATAVALUE_VAL='DataValue';
    Public Static final String INQR_TYP_VAL='INQR_TYP';
    Public Static final String M_VAL='M';
    Public Static final String A_VAL='A';
    Public Static final String B_VAL='B';
    Public Static final String P_VAL='P';
    Public Static final String E_VAL='E';
    Public Static final String G_VAL='G';
    Public Static final String U_VAL='U';
    Public Static final String INQA_TYP_VAL='INQA_TYP';
    Public Static final String DOB_VAR {
        get {
            List<FieldDefinition> dobField = [Select DurableId from FieldDefinition where EntityDefinition.QualifiedApiName = 'Account' AND QualifiedApiName = 'Birthdate__c'];
            return dobField[0].DurableId.right(dobField[0].DurableId.length()-8);
        }
    }
    //Public Static final String DOB_VAR='00NF000000CBqWV';
    Public Static final String INQA_VAL='INQA';
    Public Static final String CALLER_BROKER_VALUE='Broker';
    Public Static final String CALLER_EMPLOYER_VALUE='Employer';
    Public Static final String CS_1799704_GENESYS_LEVEL1='1799704_Genesys_Level1';
    Public Static final String CS_1799704_GENESYS_LEVEL2='1799704_Genesys_Level2';
    Public Static final String CS_1799704_GENESYS_LEVEL3='1799704_Genesys_Level3';
    Public Static final String STATUS_UPDATED='status_updated';
    Public Static final String LOGGED_OUT='logged_out';
    Public Static final String INTERACTION='interaction';
    Public Static final String CONNECTED='connected';
    Public Static final String CAT_ROUTING_STATUS='routingStatus';
    Public Static final String CAT_VIEW='view';
    Public Static final String ACW_COMPLETED='acw_completed';
    Public Static final String ACW_REQUIRED='acw_required';
    Public Static final String DISCONNECTED='disconnected';
    Public Static final String CAT_CHANGE='change';
    Public Static final String CAT_CONNECT='connect';
    Public Static final String INBOUND='Inbound';
    Public Static final String OUTBOUND='Outbound';
    Public Static final String INQR_FIRST='INQR_FIRST';
    Public Static final String INQA_FIRST='INQA_FIRST';
    Public Static final String INQR_LAST='INQR_LAST';
    Public Static final String INQA_LAST='INQA_LAST';
    Public Static final String SURVEY_ID='SURVEY_ID';
   	Public Static final String Call_Catg_Desc='Call_Catg_Desc';
    public static final String SERVICE_GETCALLDATA = 'GetCallData';
    public static final String SERVICE_ALLPHONEBOOKRULES ='GetAllPhoneBookRules';
    public static final String SERVICE_ROUTEXML ='RouteSoftphoneXml';
    public static final String SERVICE_SAVECALLDATA ='SaveCallData';
    public static final String CALLBACK_GETCALLDATA = 'getprocessResponse';
    public static final String CALLBACK_DEFAULTPHONEBOOK = 'processResponseGetDefaultPhonebook';
    public static final String CALLBACK_ALLPHONEBOOKRULES ='processResponsegetAllPhoneBookRules';
    public static final String CALLBACK_CALLTRANSFER ='processCallTransferResponse';
    public static final String STATUS = 'status';
    public static final String  LOGOUT='logout';
    public static final String LOGIN = 'login';
    public static final String STATION = 'station';
    public static final String NGroupID_VAL ='NGroupID';
    public static final String Demo_VAL ='Demo';
    public static final String QueueName = 'Queue Name';
    public static final String CallReason = 'Call Reason';
    public static final String Authentication_IND = 'AUTHENTICATION_IND';
    public static final String AGENT_CALL_START_TIME = 'AGENT_CALL_START_TIME';
    public static final String AGENT_CALL_END_TIME = 'AGENT_CALL_END_TIME';
    public static final String CALL_TYPE = 'CALL_TYPE';
    public static final String MODULE_NAME = 'MODULE_NAME';
    public static final String USER_ID = 'USER_ID';
    public static final String USER_NAME = 'USER_NAME';
    public static final String Genesys = 'Genesys';
    public static final String UUID = 'UUID';
    public static final String ISMANUAL_TRANSFER = 'ISMANUAL_TRANSFER';
    public static final String NUMBER_OF_TRANSFERS = 'NUMBER_OF_TRANSFERS';
    public static final String TRANSFER_STIM = 'TRANSFER_STIM';
    public static final String TRANSFER_NUMBER = 'TRANSFER_NUMBER';
    public static final String TRANSFER_TYPE = 'TRANSFER_TYPE';
    public static final String TRANSFER_ETIM = 'TRANSFER_ETIM';
    public static final String respdisconnectSaveCallData = 'respdisconnectSaveCallData';
    public static final String respConsultBlindSaveCallData = 'respConsultBlindSaveCallData';
    public static final String isConsultBlindClicked = 'isConsultBlindClicked';
    public static final String isTransferredCall = 'isTransferredCall';
    public static final String INQR_DOB = 'INQR_DOB';
    public static final String INQR_ID = 'INQR_ID';
    public static final String INQA_ID = 'INQA_ID';
    public static final String IVR_CALLER_TYPE = 'IVR_CALLER_TYPE';
    public static final List<String> SWITCH_LIST = New List<String>{'2357061','2230090','2484363','2230000','2230077','2546987','2704639','2760576','2895841','2876026','2649919','2191493','3701398','3653389','3791679','3849706','3959734','3944531','3980680','3979946','4144165','2760646','4461937','4734084','4696940','4707880','4599393','4954802','4863232','5602717','5668340'};
    public static final String AUTH_SWITCH = '2287945';

    Public Static final String Secure_Transfer = 'Secure Transfer';
    Public Static final String Member_ID_Value = 'Member_ID_Value__c';
    Public Static final String Member_Dependent_Code = 'Member_Dependent_Code__c';
    Public Static final String Enterprise_ID = 'Enterprise_ID__c';
    Public Static final String Policy_Platform = 'Policy_Platform__c';
    Public Static final String SubscriberIdBase = 'SubscriberIdBase';

    Public Static final Integer Constant_100_HUM = 100;
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202;
    Public Static final String VOB_FAULT_CODE = 'faultcode';

    public static final String MEMBER_PERS_GEN_KEY ='MEMBER_PERS_GEN_KEY';
    public static final String DependentCode='DependentCode';
    public static final String INQA_PLTFRM_CD ='INQA_PLTFRM_CD';
    public static final String INQR_PLTFRM_CD ='INQR_PLTFRM_CD';
    public static final String INQR_GEN_KEY ='INQR_GEN_KEY';

    public static final String MEDICAREID_HUM = 'MedicareID' ;
    public static final String SSN_HUM = 'SSN' ;
    public static final String MEMBERIDBASE_HUM = 'Member-Id-Base';
    public static final String MEDICADEID_HUM = 'Medicaid-Id' ;
    public static final string UNKNOWN_MEMBER_MEDICAIDID = 'MedicaidID';
    public static final string SURVEY_TRANSFER_NUMBER = 'SurveyTransferNumber';
    public static final String CRMS_685_PCC_Access_HUM ='CRMS_685_PCC_Customer_Service_Access';
    public static final String CB_PLATFORM_CD ='CB';
    public static final string CBIS_ALT_ID = 'CBIS ALT ID';
    public static final String WATSON_TRANSFER_TS ='WATSON_TRANSFER_TS';
    public static final String IsSoftPhoneIndicator ='Y';
    public static final String Inbound_Call= 'Inbound Call';
    public static final String S_VAL='S';
    public static final String PPI_VAL='PPI';
    public static final String CREATE_AUTO_INTERACTION_FAIL_LOG ='CRM auto interaction creation failed for UUID: {0} with Error message: {1}';
    public static final String CREATE_AUTO_INTERACTION_MEMBER_FAIL_LOG ='CRM auto Interaction Member creation failed for UUID: {0} with Error message: {1}';
    public static final String CALLER_TYPE_NOT_HANDLED_LOG = 'Blank interaction created for UUID: {0} as caller type code: {1} is not handled.';
    public static final String INTERACTING_WITH_ACCOUNT_ID_NOT_FOUND_LOG = 'Interaction created without Interacting With details for UUID: {0} as either no matching record found or multiple matching record found for Interacting With caller type code: {1}.';
    public static final String INTERACTING_ABOUT_ACCOUNT_ID_NOT_FOUND_LOG = 'Interaction created without Interacting About details for UUID: {0} as either no matching record found or multiple matching record found for Interacting About caller type code: {1}.';
    public static final String updateCampaignNameAndKeycodeSwitch = '3980680';
    public static final String updateCampaignNameAndKeycodeSwitchLD = 'Switch_3980680';
    public static final String updateInteractionAttributeSwitch = '3979946';
    public static final String MemberDOBFormatSwitch ='4144165';
    public static final String multiMemberAuthSwitch = '2760646';
    public static final String watsonCallSecondMemberSwitch = '4726311';
    public static final String INTERACTING_ABOUT_CALLER_TYP_NOT_FOUND_LOG = 'Interacting About caller type code not found in authenticated call data for UUID: {0} and Interacting With caller type code: {1}.';
    public static final String callerTypePopulationSwitch ='4040542'; //US#4040542-Added for Caller Type Population with Phonebook - Classic.
    public static final String PHONEBOOK_INTERACTING_WITH_TYPE_MAPPING ='Interacting With Type'; //US#4040542-Added for Caller Type Population with Phonebook.
    public static final String transferCallSwitchClassic = '4696940';
    public static final String INQR_LAST_VALUE='Individual_Last_Name__c_SOFTPHONE';
    public static final String watsonProviderCallSwitch = '5602717';
    public static final String INQR_NPI_VALUE='InteractingWithNPIId';
    public static final String INQR_NPI = 'INQR_NPI';
    public static final String alphabetizeCallReasonsSwitch = '5668340';

}