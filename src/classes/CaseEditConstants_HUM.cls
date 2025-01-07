/************************************************************************************************************************************************
  Apex Class Name  : CaseEditConstants_HUM
  Version          : 1.0 
  Created Date     : AUG 13 2021
  Function         : Class to hold the Constants used in the CaseEdit page.
  Test Class       : 
  * Developer Name            Code Review                 Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------
  * Gowthami Thota                                		08/13/2021	                 Original Version 
  * Samantha Tennyson                                   12/08/2021	                 Defect Fix - User Story 2786833: T1PRJ0001827 - MF 1 - AHT - Ability to automatically create a case when an ID Card Request is successfully submitted (RF)
  * Prasuna Pattabhi 								    08/24/2023				  US: 4412371 -Market Credentialing Task Fields
 *****************************************************************************************************************************************************************************/
public class CaseEditConstants_HUM {
    
    Public Static Final String AUTOROUTING_COMPLETED = 'Completed';
    Public Static final String AUTOROUTING_PARTD = 'Evaluate Part D';
    Public Static final String AUTOROUTING_ExpeditedAppeal = 'Evaluate Expedited Appeal';
     Public Static final String POLICY_MEMBER_ERROR = 'Policy Member Not matching with the Account';
     Public Static final String CASEEDIT_NONE_DISP_HUM = '---None--';
    Public Static final List<String> Pharmacy_Record_Type_Name_LogCode_Population = New List<String>{'HP Member Case','HP Agent/Broker Case','HP Group Case','HP Provider Case','HP Unknown Case'};
    public static final string CRM_STOP_MILESTONE = '898';
    public static final string TRR_UI700_MS_PATTERN = '18';
    public static final string TRR_REJ_MS_PATTERN = '19';
    public static final string TRR_UI_SERIES_STOP_MS = '1803';
     public Static final String HUMCaseTypeSIU = 'SIU';
    public Static final String HUMCaseTypePPI= 'PPI';
    public Static final String HUMCaseTypeCorrespondence ='Correspondence Inquiry';
    public Static final String HUMCaseSubTypeRoutine=null;
    Public Static final String HUMPharmacyIntType = 'Pharmacy';
    public static final String IVRCASETYPE = 'IVR Afterhours';
    Public Static final String HUMProspectiveCustomerIntType = 'Prospective Customer';
    Public Static final String HUMAgentIntType = 'Agent';
    Public Static final String CASEEDIT_INTENT_SEPERATOR=')';
    Public Static final String CASEEDIT_CLASSIFICATION_SEPERATOR='_';
    Public Static final List<String> Pharmacy_Record_Type_Name = New List<String>{'Closed HP Agent/Broker Case','Closed HP Group Case','Closed HP Member Case','Closed HP Provider Case','Closed HP Unknown Case','HP Agent/Broker Case','HP Group Case','HP Member Case','HP Provider Case','HP Unknown Case'};
    public static final string CLAIM_NUMBER='Claim Number';
    public static final List<String> AUTOSELECTGA_SERVICEMODELTYPES = new List<String>{'Humana Pharmacy','Humana Specialty Pharmacy'}; 
    Public Static final String None = 'None';
    public static final List<String> AutoRouteWQs = new List<String>{'Louisville RSO Calls Support', 'LV Partner Site RSO Calls C3', 'LV Partner Site RSO Calls Results Co', 'LV Partner Site RSO Calls Xerox', 'LV Partner Site RSOCalls Teleperformance','Group Service Operations Calls'}; 
    Public Static final String NONPHARMACY_USER_HUM = 'No';
    Public Static String CLOSEDHP_MEMBERCASE_HUM = 'Closed HP Member Case';
    public static final String LOGCODEHSP_CLASSIFICATIONTYPE = 'HP Specialty Calls';
    Public Static final String MEDICARE_STATUS_COMPLETED_HUM = 'Completed';
    Public Static final String MEDICARE_STATUS_CANCELLED_HUM = 'Cancelled';
    Public Static final String RSO_HP_PermissionSet_Name= 'CRMS_206_CCSHumanaPharmacyAccess';
    public static final string HP_MEMBERCASE_HUM = 'HP Member Case';
    public static final string MEMBERCASE_HUM = 'Member Case';
    Public Static final String CASECOMMENT_MEMBERNOTFOUND_HUM = 'The above CRM case comment  was not sent to the Order Management System (OMS)- also known as ePost/ANS.  Error Reason: This member does not have an Order Management System (OMS)- also known as ePost/ ANS- account.  This note is not saved to ePost Rx, but saved in CRM.  You can either close this tab, or click on the ‘Exit without Saving’ button on the case to continue.';
    Public Static final String CASECOMMENT_FAULTERROR_HUM = 'The above CRM case comment  was not sent to the Order Management System (OMS)- also known as ePost/ANS.  Error Reason: The Order Management System (OMS)—also known as ePost/ ANS- is currently unavailable.  This note is not saved to ePost Rx, but saved in CRM.  You can either close this tab or click on the ‘Exit without Saving’ button on the case to continue.';
    Public Static Final String Grievance_Appeals_Permissionset_Name = 'CRMS_400_Grievance_Appeals';
    public static final string WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT = 'Louisville RSO Oral Grievance Oversight';
    Public Static final String RTIChannelIVR = 'IVR';
    Public Static final String PROVIDER_TYPE= 'Provider';
    Public Static final String HUMUnknownProviderIntType = 'Unknown-Provider';
    Public Static final String MEMBER = 'Member';
    Public Static final String CASEEDIT_INT_ABOUT_TYPE_UNKNOWN_MEMBER = 'Unknown-Member';
    Public Static final String HUMGroupIntType = 'Group';
    Public Static final String CASESTATUS_HUM='In Progress';
    Public Static final String ETL_API_ACCESS = 'ETL API Access';
    Public Static final String sMHKTaskOrigin = 'Correspondence';
    public static final String MHK_CASE_TYPE = 'MHK Dispute Task';
    Public Static final String HUMUnknownAgentIntType = 'Unknown-Agent';
    Public Static final String HUMUnknownGroupIntType = 'Unknown-Group';
    Public Static final String CASEEDIT_STATUS_HUM = 'Status';
    Public Static final String CASEEDIT_CLOSED_HUM = 'Closed';  
    Public Static final String CASEEDIT_CANCELLED_HUM = 'Cancelled';  
    Public Static final String CASEEDIT_UNKNOWN_MEMBER_HUM = 'Unknown Member';
    Public Static final String MEDICARE_CASE = 'Medicare Case';
    Public Static final String CLOSED_MEDICARE_CASE = 'Closed Medicare Case';
    Public Static final String MEDICARECALLS_PERMISSIONSET_HUM = 'CRMS_684_Medicare_Customer_Service_Access';
    Public Static final String MEDICARECALLS_ISSUE_HUM = 'Medicare Calls Issue';
    Public Static final String MEDICARECALLS_RESO_HUM = 'Medicare Calls Resolution';
    Public Static final String MEDICARE_POLICY = 'MED';
    public static final string TOPIC_ORAL_GRIEVANCE = 'Oral Grievance';
    Public Static final String ATTESTATION_TEMPLATE_NAME = 'ATVF007';
    Public Static final String CASE_RSO_ENROLLMENT_OWNER = 'Louisville RSO Enrollment';
    Public Static final String CASE_STATUS_PENDING_RESPONSE = 'Pending - Response';
    public static final string PCPTEMPLATEUNIQUEID ='d884d160-77c0-4d1b-95aa-edf8c81f3a59';
    public static final string GOODCAUSEUNIQUEID = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
    public static final string GOODCAUSECLASSIFICATION = 'Billing';
    public static final string GOODCAUSEINTENT = 'Reinstatement Review';
    public static final string AUTOROUTING_PCP = 'PCP Reassignment';
    public static final string AUTOROUTING_GOODCAUSE = 'Evaluate Good Cause';
    public static final string AUTOROUTING_GOODCAUSETIE = 'Termed in Error';
    public static final List<String> GoodCauseAutoRouteClassificationTypes = new List<String>{'Calls (RSO)','Guidance Center','Web Chat', 'RCC Web Chat'};
    Public Static Final String AUTOROUTING_EVALUATE = 'Evaluate';
    public static final string CLAIMS_MANUAL_ENTRY ='Claims Manual Entry';
    public static final string CRM_CON= 'CRM';
    Public Static final String CREDITABLECOVERAGECASESUBTYPE = 'Creditable Coverage';
    Public Static final String CASE_CLASSIFICATION_TYPE_CALLRSO = 'Calls (RSO)';
    Public Static final String LOGCODEHSP_SERVICEMODELTYPE = 'Humana Pharmacy';
    Public Static final String PRODUCT_TYPE_MAPD = 'MAPD';
    Public Static final String PRODUCT_TYPE_MA = 'MA';
    Public Static final String PRODUCT_TYPE_PDP = 'PDP';
    public static final string sExpCalssification = 'G&A';
    public static final string sExpIntent = 'Expedited';
    public static final string sPartDCalssification = 'Rx';
    public static final string sPartDIntent = 'Part D Redetermination';
    public static final string GOODCAUSEGROUPNAME = 'Louisville Premium Payment Recovery PGQ';
    public static final string GOODCAUSEQUEUE = 'Louisville Premium Payment Recovery';
    public static final string TRR_ORIGIN = 'CRMTRR';
    public static final string TRR_CASE_TYPE = 'TRR Inquiry';
    public static final string TRR_CASE_SUBTYPE = 'TRR';
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final String CASEEDIT_WORK_TASK_HUM = 'Work Task';
    public static final string MEDICAID = 'Yes - Medicaid';
    public static final List<String> lstAutoRouteStatus = New List<String>{'Evaluate Good Cause','Evaluate Expedited Appeal','PCP Reassignment','Termed in Error','Evaluate Part D'};
    Public Static final String CorrespondenceInq = 'Correspondence Inquiry';
	Public Static final String Enroll_Onboard_PERMISSIONSET_HUM = 'CRM_Enroll_and_Onboard_Access';
	
    Public Static Map<String, Set<String>> MEDICARE_LOB_PRODUCTTYPE_LIST()
    {
       Map<String, Set<String>> medicareLOBProductTypeList= New Map<String,Set<String>>{                                                                          
                                                                             'MEF' => new Set<String>{'MA', 'MAPD'},
                                                                             'MEP' => new Set<String>{'MA', 'MAPD'},
                                                                             'MER' => new Set<String>{'MA', 'MAPD','Empty'},
                                                                             'MGP' => new Set<String>{'MA', 'MAPD'},
                                                                             'MGR' => new Set<String>{'MA', 'MAPD'},
                                                                             'MPD' => new Set<String>{'PDP'},
                                                                             'MRO' => new Set<String>{'MAPD'},
                                                                             'PDP' => new Set<String>{'PDP'}
                                                                            };
      Return medicareLOBProductTypeList;                                                                            
    }
    Public Static final String CASEEDIT_VALIDATION_EXP_HUM = 'FIELD_CUSTOM_VALIDATION_EXCEPTION';
    Public Static final String CASECOMMENT_SIZE_EXCEPTION_HUM = 'data value too large';
    Public Static final map<String,String> CASE_RECORD_TYPES = New map<String,String>
    {'Member Case' => 'HP Member Case',
        'Provider Case' => 'HP Provider Case',
        'Group Case' => 'HP Group Case',
        'Agent/Broker Case' => 'HP Agent/Broker Case',
        'Unknown Case' => 'HP Unknown Case'
    };
    Public Static final map<String,String> HP_CASE_RECORD_TYPES = New map<String,String>
    {'HP Member Case' => 'Member Case',
        'HP Provider Case' => 'Provider Case',
        'HP Group Case' => 'Group Case',
        'HP Agent/Broker Case' => 'Agent/Broker Case',
        'HP Unknown Case' => 'Unknown Case'
    };
    Public Static final String MAJOR_LOB_MCD = 'MCD';
    Public Static final String SEGMENT_TYPE_MCD = 'MCD';
    Public Static final String SEGMENT_TYPE_MCR = 'MCR';
    Public Static final String HumHPRecType = 'HP';
    public static final string HUM_MEMBER_NOT_FOUND = 'Member not found';
    Public Static final String MEDICARECALLS_CASEORIGIN_HUM = 'Inbound Call';
    Public Static final String NONEDISPLAY_HUM = '---None--';
    Public Static final String ATTESTATION_DISCLAIMERNAME = 'ATVF_FinalDisclaimer';
    Public Static final String ATTESTATION_DISCLAIMERYESVALUE = 'Yes';
    Public Static final String INTERACTINGWITHTYPE_MEMBER = 'Member';
    Public Static final String MEMBERCAREGIVER = 'Member Representative or Caregiver';
    Public Static final String HUM_ALLOTHERVIEW = 'Other';
    public static final string GCRTERMEDPLAN = 'GCRTermedPlanYesNo';
    Public Static final String MAJOR_LOB_MER = 'MER';
    public static final string SERVICEFUNDCHECK = 'Did you receive a Service Fund edit when attempting to change the member\'s PCP';
    public static final string PCPGROUPNAME = 'PCP Assignment Research PGQ';
    public static final string TRR_REJ_STOP_MS = '1910';
    //Added for CaseEdit_CE_HUM
    Public Static final String HUMMedicare = 'MedicareID';
    Public Static final String HUMWebChatText = 'Web Chat';
    Public Static final String WEBCHAT_DEFAULT_LIVEAGENT_NAME = 'liveagent Site Guest User';
    Public Static final String HUMExtensionTask = 'Extension Request';
    Public Static final String HUMMEMBER_ID_TYPE = 'Member-Id-Base';
    Public Static final String HUMChangeCaseOwner = 'HUMChangeCaseOwner';
    Public Static final String HUMHPSpecialistProfile = 'Humana Pharmacy Specialist';
    public static final string HUMDefaultMentorFunction = 'Contact Center RSO';
    public static final string HUMDefaultMentorSubFunction = 'Individual Medicare';
    Public Static final String EMPTY_STRING = '';
    public static final String Demographic_Update = 'Demographic Update';
    public static final String RCC_WebChat_Users = 'Web Retail Service Operations RSO Chat';
    public static final String Enrollment_Onboard_Welcome_Call = 'Enroll & Onboard (E&O) Welcome Call';
    public static final String Case_Classification_Communication  = 'Communication';
    public static final String Case_intent_Welcome_Call = 'Welcome Call';
    public static final String Segment_Individual_Medicare = 'Individual-Medicare';
    public static final String MCD_OWNER_Q = 'Market National Network Ops';
    public static final String MCD_Classification = 'Provider Requests';
    public static final String MCD_Intent = 'LA Medicaid Cred Workflow';
}