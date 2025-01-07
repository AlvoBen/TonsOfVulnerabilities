/************************************************************************************************************************
Apex class Name    : HumConstants_LWC
Version            : 1.0
Function           : Apex Class to hold all Constants used across Case/Task LWC components.
Created On         : December 29 2021
Test Class         : HumConstants_LWC_T_HUM

    Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Vijaya Lakshmi Tummala                 			12/29/2021          Original Version
*   Pooja Kumbhar									12/22/2022			US 3863264 - T1PRJ0170850 - Lightning - RCC/Medicare (MET) Enrollment sections Case Edit Page
*   Pooja Kumbhar									02/03/2023			US 4180008 - T1PRJ0170850- Lightning- Case management- Change Case Owner Missing Functionalities					
*   Pooja Kumbhar	                                06/29/2023          US4626269 - T1PRJ0865978 - C06- Case Management - OI : T1PRJ0865978 - C06, Case Management, OI (Medicare and Medicaid Other Insurance )Template, Auto Set CI's and Disable Case Edit Page buttons
*   Nirmal Garg										12/18/2023			Cardinals signed off on Copado - NULL Release
*   Santhi Mandava                                  12/19/2023          Strikers signed off on Copado - NULL Release
*   Jonathan Dickinson								12/19/2023			Lego signed off on Copado - NULL Release
*   Manjit SIngh									12/19/2023			Bobcats signed off on Copado - NULL Release
*	Alvaro Madrid									12/19/2023			Dragons signed off on Copado - NULL Release
*   Dimple Sharma									12/19/2023			Surge signed off on Copado - NULL Release
*	Varun Joijode									12/20/2023			Titans signed off on Copado - NULL Release
*  	Vinoth Lak										12/20/2023			OAKS - Storefront signed off on Copado - NULL Release
***************************************************************************************************************************/

public class HumConstants_LWC {
    // apex class ChangeCaseOwner_C_HUM_LWC referred constants
    Public Static final Integer LIMITVALUE_HUM = 3500;
    Public Static final List<String> Pharmacy_Record_Type_Name = New List<String>{'Closed HP Agent/Broker Case','Closed HP Group Case','Closed HP Member Case','Closed HP Provider Case','Closed HP Unknown Case','HP Agent/Broker Case','HP Group Case','HP Member Case','HP Provider Case','HP Unknown Case'};
    Public Static final String RSO_HP_PermissionSet_Name= 'CRMS_206_CCSHumanaPharmacyAccess';
    Public Static final String NONPHARMACY_USER_HUM = 'No';
    public static final string SERVICE_CENTER_HUMANA_PHARMACY = 'Humana Pharmacy';
    public static final string SERVICE_CENTER_RX = 'Rx';
    public static final string WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY = 'Provider Payment Integrity';
    public static final string WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION = 'Louisville Other Insurance Investigation';
    public static final string WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT = 'Louisville RSO Oral Grievance Oversight';
    public static final string WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS = 'LV Medicare Member Communications';
    public static final string TOPIC_SPECIAL_INVESTIGATIONS = 'Special Investigations Unit (SIU) Referrals';
    public static final string TOPIC_ORAL_GRIEVANCE = 'Oral Grievance';
    Public Static final String NONE_HUM = 'None';
    Public Static final String NONEDISPLAY_HUM = '---None--';
    Public Static final String CHANGECASEOWNER_SELECTTOPIC_HUM = 'Select a Topic';
    
    
    // apex class HUMQueueSelection_LWC referred constants
    Public Static final String EMPTY_STRING = '';
    public static final string HUMSingleSpaceValue = ' ';
    public static final string HUMDefaultMentorFunction = 'Contact Center RSO';
    public static final string HUMDefaultMentorSubFunction = 'Individual Medicare';
    Public Static final String HUMObjectType = 'objectType';
    Public Static final String OBJECT_CASE = 'Case';
    Public Static final String OBJECT_TASK = 'Task';
    Public Static final String HUMNextPage = 'nextPage';
    Public Static final String HUMParentObjectId = 'ParentObjectId';
    Public Static final String HUM_Object_Policy = 'Policy__C';    
    Public Static final String HUMCaseStandardURL = 'sStandardUrlForTab';
    Public Static final String HUMQueueYesForEdit = 'YesForEdit';
    Public Static final String HUMQueueYesForAttachment = 'YesForAttachement';
    Public Static final String HUMQueueYesForInteraction = 'YesForInteraction';
    Public Static final String HUMQueueYesFromAccountRelatedList = 'YesFromAccountRelatedList';
    Public Static final String HUMcaseRecordType = 'caseRecordType';
    Public Static final String HUMAccRecordTypeName = 'Account RecordType';
    Public Static final String HUMQueueYesFromCaseHistory = 'YesFromCaseHistory';
    Public Static final String HUMApexinURL = '/apex/';
    Public Static final String HUMWhatId = 'whatId';
    Public Static final String HUMId = 'Id';
    Public Static final String HUMsmallId = 'id';
    Public Static final String WHAT_ID = 'what_id';
    Public Static final String HUMRecordType = 'RecordType';
    Public Static final String HUMChatIdParameter = '&chatId';
    Public Static final String HUMWebChatText = 'Web Chat';
    Public Static final String HUMSearchTypeMember = 'Member';
    Public Static final String HUMDotCom = '.com/';
    Public Static final String HUMCaseURLInteraction = '&interactionId=';
    Public Static final String CASE_STATUS='In Progress';
    Public Static final String CASE_TYPE='Customer Inquiry';
    Public Static final String CASE_ORIGIN='Inbound Call';
    
   // GLOBAL_CONSTANT_HUM----------------------

    Public Static final String TASKLISTVIEW_INPROGRESS_HUM = 'In Progress';
    Public Static final String TASKLISTVIEW_PENDING_HUM = 'Pending';
    Public Static final String TASKLISTVIEW_PENDINGTASK_HUM = 'Pending - Task';
    Public Static final String TASKLISTVIEW_PENDINGRESPONSE_HUM = 'Pending - Response';
    Public Static final String TASKLISTVIEW_PENDINGTRANSFER_HUM = 'Pending - Transfer';
    Public Static final Integer TASKLISTVIEW_HUMWORKQUEUESETUPLIMIT_HUM = 5000;
    Public Static final String TASKLISTVIEW_ASSIGNED_HUM = 'Assigned to me';
    Public Static final String TASKLISTVIEW_DUESPACEDATE_HUM = 'Due Date';
    Public Static final String PHARMACYSPECIALIST_USER_HUM = 'Yes - Specialist';
    Public Static final String PHARMACYSUPERVISOR_USER_HUM = 'Yes - Supervisor';
    Public Static final String HUM_CLOSED = 'Closed';
    Public Static final String HUM_CANCELLED = 'Cancelled';
    Public Static final String TASKLISTVIEW_UNASSIGNED_HUM = 'UnAssigned';
    Public Static final String General = 'General';
    Public Static final String WORKQUEUE_APP_HOMEOFFICEINDICATORDENTAL_HUM = 'Home Office Dental';
    Public Static final String HOAllOther = 'Home Office All Other';
    Public Static final String ContractProtectedData = 'Contract Protected Data';
    Public Static final String TASKLISTVIEW_REGULAR_HUM = 'regular';
    Public Static final String TASKLISTVIEW_ALL_HUM = 'All';
    Public Static final String TASKLISTVIEW_ERRORQUEUENOACCESS_HUM = 'You don\'t have access to Queues';
    Public Static final String TASKLISTVIEW_OBJECTCASE_HUM = 'Case';
    Public Static final String TASKLISTVIEW_OBJECTTASK_HUM = 'Task';
    Public Static final String CASE_CONSTANT_HUM = 'Case';
    Public Static final String TASKLISTVIEW_SFDCCASEID_HUM = '500';
    Public Static final String CASEMGMT_DESCRIPTION_HUM = 'Description';
    Public Static final String TASKLISTVIEW_STRINGNUMERICZEROZEROFIVE_HUM  = '005';
    Public Static final String TASKLISTVIEW_QUEUE_HUM = 'Queue';
    Public Static final String TASKLISTVIEW_AGETASKSONLY_HUM = 'Age - Tasks Only';
    Public Static final String TASKLISTVIEW_AGE_HUM = 'Age';
    Public Static final String TASKLISTVIEW_AGECASESONLY_HUM = 'Age - Cases Only';
    Public Static final String TASKLISTVIEW_PRIORITYONLY_HUM ='Priority';
    Public Static final String TASKLISTVIEW_PRIORITYCASEONLY_HUM ='Priority - Cases Only';
    Public Static final String TASKLISTVIEW_PRIORITYTSKONLY_HUM ='Priority - Tasks Only';
    Public Static final String TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM = 'Priority - Critical & High Only';
    Public Static final String TASKLISTVIEW_PRIORITYNORMALONLY_HUM = 'Priority - Normal Only';
    Public Static final String TASKLISTVIEW_AGETASKSONCASE_HUM = 'Age - Cases and Tasks by oldest Case Age';
    Public Static final String CASEMGMT_GROUPID_INITIAL_HUM = '00G';
    Public Static final String HUM_ALLOTHERVIEW = 'Other';
    Public Static final String TASKLISTVIEW_OTHER_HUM = 'Other';
    public static final string Customer_Care_Supervisor = 'Customer Care Supervisor';
    Public Static final String TASKLISTVIEW_CASES_HUM = 'Cases';
    Public Static final String TASKLISTVIEW_TASKS_HUM = 'Tasks';
    Public Static final String AGE_HUM = 'Age';
    Public Static final String TASKLISTVIEW_PRIORITY_HUM = 'Priority';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_Intent_HUM = 'QAA';
    Public Static final List<String> WEB_CHAT_WQUEUES = New List<String>{'Humana Pharmacy Web Chat','HP Clinical Services Web Chat'};
    Public Static final String CASE_RSO_ENROLLMENT_OWNER = 'Louisville RSO Enrollment';
    Public static final string WORK_QUEUE_LV_Screening = 'LV G and A and Correspondence Screening';
    Public static final string WORK_QUEUE_PCP_Assignment_Research = 'PCP Assignment Research';
    Public static final string GOODCAUSEQUEUE = 'Louisville Premium Payment Recovery';
    Public Static final String CHANGECASEOWNER_MEMBERCASE_HUM = 'Member Case';
    Public Static final String MEDICARE_CASE = 'Medicare Case';
    public static final string HP_UNKNOWNCASE_HUM = 'HP Unknown Case';
    Public Static final String CHANGECASEOWNER_UNKNOWNCASE_HUM = 'Unknown Case';
    Public Static final String CASEEDIT_WORK_TASK_HUM = 'Work Task';
    Public Static final String CASEEDIT_CLOSED_HUM = 'Closed';
    public static final string MedExpTEMPLATEUNIQUEID = '95C90830-3133';
    public static final string PartDTEMPLATEUNIQUEID = 'BC929CD6-E06A';
    Public Static final String ATTESTATION_TEMPLATE_NAME = 'ATVF007';
    public static final string GOODCAUSEUNIQUEID = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
    public static final string PCPTEMPLATEUNIQUEID ='d884d160-77c0-4d1b-95aa-edf8c81f3a59';
    public static final string GOODCAUSEGROUPNAME = 'Louisville Premium Payment Recovery PGQ';
    public static final string PCPGROUPNAME = 'PCP Assignment Research PGQ';
    public static final string Group_LV_Correspondence_Screening_PGQ = 'LV GA and Correspondence Screening PGQ';
    public static final string Group_LV_Correspondence_Screening_PGVE = 'LV GA and Correspondence Screening E PGV';
    public static final string Group_LV_Correspondence_Screening_PGVP = 'LV GA and Correspondence Screening P PGV';    
    public static final string MEDICAID = 'Yes - Medicaid';
    public static final string MEDICAREPART_C = 'Yes - Medicare Part C';
    public static final string MEDICAREPART_D = 'Yes - Medicare Part D';    
    Public Static final String MEDICARECALLS_PERMISSIONSET_HUM = 'CRMS_684_Medicare_Customer_Service_Access';
    public static final string GOODCAUSECLASSIFICATION = 'Billing';
    public static final string GOODCAUSEINTENT = 'Reinstatement Review';
    public static final List<String> GoodCauseAutoRouteClassificationTypes = new List<String>{'Calls (RSO)','Guidance Center','Web Chat', 'RCC Web Chat'};
    Public Static final String PRODUCT_TYPE_PDP = 'PDP';
    Public Static final String HumHPRecType = 'HP';
    public static final string WEB_CHAT_ORIGIN = 'Web Chat';
    Public Static final String MEDICARE_POLICY = 'MED';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_Classification_HUM = 'Complaints or Compliments';
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
    Public Static final String HUMClOSED = 'Closed';
	Public Static final String HUM_CASEEDIT_CLOSED = 'Closed';    
    Public Static final String MEMBER = 'Member';
    Public Static final String CLOSED_MEDICARE_CASE = 'Closed Medicare Case';
    Public Static final String CASE_STATUS_PENDING_RESPONSE = 'Pending - Response';
    Public Static final String HYPHEN_HUM = '-';
    Public Static final String IA_MORE_THAN_10 = 'IA_MORE_THAN_10';
    Public Static final String IA_6_TO_10 = 'IA_6_TO_10';
    Public Static final String IA_UPTO_5 = 'IA_UPTO_5';
    Public Static final String PDPPilot_PermissionSet_Name= 'CRMS_205_CCSPDPPharmacyPilot';
    Public Static final String MEDICARECALLS_MAPD_HUM = 'MAPD';
    Public Static final String MEDICARECALLS_PDP_HUM = 'PDP';
    Public Static final String MEDICARECALLS_MA_HUM = 'MA';
    Public Static final String MEDICARECALLS_SEGMENTMCR_HUM = 'MCR';
  
     //HumConstants-------------------------
     
     Public Static final String HUMWorkTask = 'Work Task';
     Public Static final String HUMExtensionLabel = 'Extension Task';
     Public Static final String HUMExtensionTask = 'Extension Request';
     Public Static final String PHARMACYSPECIALIST_PROFILE_HUM = 'Humana Pharmacy Specialist';
    
    //testclass
     public Static final String MIR_PERMISSIONSET_HUM = 'CRMS_1100_MemberIssueResolution';
     Public Static final String MEDICARECALLS_ISSUE_HUM = 'Medicare Calls Issue';
     Public Static final String MEDICARECALLS_RESO_HUM = 'Medicare Calls Resolution';
     Public Static final String PRODUCT_TYPE_MA = 'MA';
     Public Static final String SEGMENT_TYPE_MCD = 'MCD';
     Public Static final String SEGMENT_TYPE_MCR = 'MCR';
     Public Static final String MAJOR_LOB_MER = 'MER';
    Public Static final String MAJOR_LOB_MCD = 'MCD';
    Public Static final String MAJOR_LOB_MES = 'MES';
     public static final string AUTOROUTING_PCP = 'PCP Reassignment';
     Public Static final String HUM_COMPLETED = 'Completed';
     Public Static final String USER_HUM = 'User';
    Public Static final String PRODUCT_TYPE_MAPD = 'MAPD';
   //constants of CaseTaskView_H_HUM_LWC
    Public Static final String CHANGECASEOWNER_PROVIDERCASE_HUM = 'Provider Case';
    Public Static final String CHANGECASEOWNER_AGENTBROKERCASE_HUM = 'Agent/Broker Case';
    Public Static final String CHANGECASEOWNER_GROUPCASE_HUM = 'Group Case';
    Public Static final String CHANGECASEOWNER_UNKNOWNMEMBER_HUM = 'Unknown Member';
    public static final string Resolution_Comment = 'ASSOCIATE RESOLUTIONS:';
    Public Static final String MEDICARECALLS_CASEORIGIN_HUM = 'Inbound Call';
    public static final string autoRouteStatus_EvalExp = 'Evaluate Expedited Appeal';
    Public Static final String MEMBER_RECORDTYPE_HUM = 'Member';
    public static final string autoRouteStatus_EvalPartD = 'Evaluate Part D';
    public static final string GCRTERMEDPLAN = 'GCRTermedPlanYesNo';
    public static final string AUTOROUTING_GOODCAUSE = 'Evaluate Good Cause';
    public static final string AUTOROUTING_GOODCAUSETIE = 'Termed in Error'; 
	public static final string SERVICEFUNDCHECK = 'Did you receive a Service Fund edit when attempting to change the member\'s PCP';    
    public static final string HUMDateTimeFormat = 'MM/dd/yyyy hh:mm a';
    Public Static final String sSrcUp = ',srcUp,';  
	
	// MET constnats
	 Public Static final String METTASKTYPE ='MET Task';
	 Public Static final String MET_TASK_RECORDTYPE_HUM = 'MET_Task';
	 Public Static final String MEDICARE_STATUS_CANCELLED_HUM = 'Cancelled';
	 
	 //ETL Profile
     Public Static final String ETL_API_ACCESS = 'ETL API Access';
}