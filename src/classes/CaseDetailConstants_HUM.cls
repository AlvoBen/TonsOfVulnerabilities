/************************************************************************************************************************************************
  Apex Class Name  : CaseDetailConstants_HUM
  Version          : 1.0 
  Created Date     : OCT 30 2017
  Function         : Class to hold the Constants used in the various Case Detail pages.
  Test Class       : 
  * Developer Name            Code Review                 Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------
  * Rakesh Patni              146796                  10/30/2017                  Original Version 
  * Pradeepkumar Dani         146796                  11/20/2017                  Added constants for indivisual components. 
  * Pradeepkumar Dani         153242                  12/01/2017                  Updated to add constants for Case History component
  * Pradeepkumar Dani         157391                  12/14/2017                  Attachment Datatable updates
  * Srivinas Damera           188503                  2/22/2018                   REQ - 335172 - Removal of Permission Set to Access Custom Case Page  
  * Prafull Verma             181027                  02/06/2018                  Added constant for Appeal cases.
  * Srivinas Damera           199956                  03/15/2018                  REQ � 357689 - Redirect Out of box case detail page for RPA
  * Srivinas Damera           215199                  04/18/2018                  REQ - 360903 CA Ticket 7458982
  * Darshan Sharma				      05/08/2018		  REQ-361925 AHT - Ability to have have Jump links on the Case Edit Page (RF)
  * Satyam Malviya                                    06/05/2018                  REQ - 363542-SF - TECH - Rollback Case Detail Page  
  * Dinesh Subramaniyan							      08/01/2018					REQ - 363343 - CR 5742 - Add Seconds to Date/Time Stamp in CRM Service 
  * Lakshmi Vutukuri                                  9/4/2018                     REQ- 361157- complaint tracking fields    
  * Joel George										 12/03/2018						Changed the Id from CCP/GCP to Related Information  
  * Pallavi Ravishankar                              01/17/2019                     REQ - 369799# Archive logging attachments in CRM
  * Moshitha Gunasekaran                             02/04/2020                   US752867 - New TRR Templates Process Section
  * Sai Kumar Boga                                   05/28/2020                   US - 892853 - Fixed CA Ticket - 8910137 - Error message "URL no longer exists" in the interaction screen from a case using softphone
  * Atia Uzma                                        07/29/2021                   User Story 2498842: T1PRJ0001827 - MF 1 - AHT - Ability to Reopen and Edit closed cases (RF)
  * Gowthami Thota									                 07/29/2021					          US - 2543348 - T1PRJ0002606/PR00094268  - CR01 CSI 1922  - Performance improvement of Case detail and Edit page
  * Vijaya Lakshmi Tummala                 	 		 10/17/2022     			Original Version-Case Attachment -Feature-3033033
 * 																			                    	UserStory -3775271 Creating Case Attachments in Lightning		
  *****************************************************************************************************************************************************************************/

  public with sharing class CaseDetailConstants_HUM
  {
      public static final string sAgentRType ='Agent/Broker Case';
      public static final string sClosedAgentRType ='Closed Agent/Broker Case';
      public static final string sHPAgentRType ='HP Agent/Broker Case';
      public static final string sClosedHPAgentRType ='Closed HP Agent/Broker Case';  
      
      public static final string sGroupRType ='Group Case';
      public static final string sClosedGroupRType ='Closed Group Case';
      public static final string sHPGroupRType ='HP Group Case';
      public static final string sClosedHPGroupRType ='Closed HP Group Case';
      
      public static final string sProviderRType ='Provider Case';
      public static final string sClosedProviderRType ='Closed Provider Case';
      public static final string sHPProviderRType ='HP Provider Case';
      public static final string sClosedHPProviderRType ='Closed HP Provider Case';
      
      public static final string sUnknownRType ='Unknown Case';
      public static final string sClosedUnknownRType ='Closed Unknown Case';
      public static final string sHPUnknownRType ='HP Unknown Case';
      public static final string sClosedHPUnknownRType ='Closed HP Unknown Case';
      
      public static final string sMemberRType ='Member Case';
      public static final string sClosedMemberRType ='Closed Member Case';
      public static final string sHPMemberRType ='HP Member Case';
      public static final string sClosedHPMemberRType ='Closed HP Member Case';
      
      public static final string sMedicareRType ='Medicare Case';
      public static final string sClosedMedicareRType ='Closed Medicare Case';
      
      public static final string sAgentPageName = 'CaseDetail_Agent_VF_HUM';
      public static final string sGroupPageName = 'CaseDetail_Group_VF_HUM';
      public static final string sProviderPageName = 'CaseDetail_Provider_VF_HUM';
      public static final string sUnknownPageName = 'CaseDetail_Unknown_VF_HUM';
      public static final string sMemberPageName = 'CaseDetail_Member_VF_HUM';
      public static final string sPharmacyPageName = 'CaseDetail_Pharmacy_VF_HUM';
      public static final string sMedicarePageName = 'CaseDetail_Medicare_VF_HUM';
      
      public static final string sfloatLink1 = 'Case Detail';
      public static final string sfloatLink2 = 'Related Accounts';
      public static final string sfloatLink3 = 'MET Enrollment';
      public static final string sfloatLink4 = 'Additional Information';
      public static final string sfloatLink5 = 'G&A/Complaints';
      public static final string sfloatLink6 = 'Texas Complaint Letter';
      public static final string sfloatLink7 = 'Case Comments';
      public static final string sfloatLink8 = 'Logged Information';
      public static final string sfloatLink9 = 'Related Information';
      public static final string sfloatLink10 = 'Process';
      public static final string sfloatLink11 = 'Attachments';
      public static final string sfloatLink12 = 'Task Hierarchy';
      public static final string sfloatLink13 = 'Linked Cases';
      public static final string sfloatLink14 = 'Compass';
      public static final string sfloatLink15 = 'Articles';
      public static final string sfloatLink16 = 'Interaction';
      public static final string sfloatLink17 = 'System Information';
      public static final string sfloatLink18 = 'Case History';
      public static final string sfloatLink19 = 'Live Chat Transcripts';
      public static final string sfloatLink20 = 'Case Information';
      public static final string sfloatLink21 = 'Medicare Calls Case Comments';
      public static final string sfloatLink22 = 'TRR Process';	
      
      public static final string HUMCreated = 'Created';
      public static final string HUMChanged = 'Changed ';
      public static final string HUMFrom = ' from ';
      public static final string HUMTo = ' to ';
      public static final string HUMDateTimeFormat = 'MM/dd/yyyy hh:mm a';
      public static final string sCaseOwnerApi = 'Owner';
      public static final string sCaseOwnerLabel = 'Case Owner';
      public static final string sHistoryDeleted = 'Deleted ';
      public static final string sHistoryIn = ' in ';
      public static final string sHUMDateTimeSecondsFormat = 'MM/dd/yyyy hh:mm:ss a'; 
          
      public static final string sLastModifiedbyQueueField = 'LastModifiedby_Queue__c';
      public static final string sClinicalIntake = 'Clinical Intake';
      public static final String sEMPTY = '';
      Public Static final String sSrcUp = ',srcUp,';
      Public Static final String sForwardSlash = '/';
      Public static final string sComma = ',';
      public static final string sActionSeparator = '---';
      public static final string sEdit = 'Edit';
      public static final string sDel = 'Del';
      
      public static final string sLoggedInfoViewAction = 'View,openScreesnhotSubtab,';
      public static final string sLoggedInfoDelAction = 'Del,deleteLogAttachments,';
      public static final string sLoggedInfoProfileSup = 'HUMAgencyCCSupervisor';
      
      public static final string sArticleDetach = 'Detach,';
      public static final string sArticleDeleteRecord = 'deleteRecord,';
      public static final string sArticleKavVersion = '?kavVersion=';
      public static final string sArticleCaseIdURLParam = '&amp;popup=true&amp;caseId=';
      
      public static final string sProcessCaseIdURLParam = ',openActionTab,';
      public static final string sTRRProcessCaseIdURLParam = ',openTRRActionTab,';
      public static final string sCompassOpenURL = ',openURL,';
      
      public static final string surl ='/apex/';
      public static final string sparam = '?id=';
      public static final string pSetName = 'CRMS_RPA_OriginalCaseDetailPage';
    public static final string sCsertComplaints ='CRMS_CSERT_Complaints';
      public static final string sRedirectPage = 'CaseDetailRedirect_VF_HUM';
      public static final string sAgent = 'Agent';
      public static final string sProvider = 'Provider';
      public static final string sGroup = 'Group';
      public static final string sUnknown = 'Unknown';
      public static final string sMedicare = 'Medicare';
      public static final string sPharmacy = 'Pharmacy';
      public static final string sMember = 'Member';
  
      public static final string sBoldTagOpen = 'amp;lt;bamp;gt;';
      public static final string sBoldTagClose = 'amp;lt;/bamp;gt;';
      public static final string sFieldStatus = 'Status';
      public static final string sIdPattern = '[A-Za-z0-9]{18}';
  
      public static final string sOpenCaseComments = ',openCaseComments,';
      public static final string sCaseCommentsParam = '/e?parent_id=';
      public static final string sCaseCommentsDel = ',confirmDelete_caseCommentsDataTable,';
  
      public static final string sAttURL = '/servlet/servlet.FileDownload?file=';
      public static final string sAttLightURL = '/sfc/servlet.shepherd/version/download/';
      public static final string sAttDel = ',confirmDelete_caseAttachmentsDataTable,';
      public static final string sOpenAtt = ',fnOpenAttachment,';
      public static final string sAttObject = 'Attachment__c';
      public static final string sLiveAgentSpecialistPermSet = 'Live_Agent_Specialist';            
      
      //REQ - 352468 - Case resolution history
      public static final string sCaseResolution='CaseResolution';
    
    //REQ - 360903- CA Ticket 7458982    
      Public Static final String ATTACHMENT_FILENAME_APOSTOPHE='APOSTROPHE';
             
      //REQ - 369799# Archive logging attachments in CRM 
      Public Static final String sOpenArchAtt = ',fnOpenArchiveAttachment,';
      
      public static final string sInProgress = 'In Progress';
      public static final string sClosed = 'Closed';

      Public Static Final String AUTOROUTING_COMPLETED = 'Completed';
      Public Static final string AUTOROUTING_PARTD = 'Evaluate Part D';
      public static final string AUTOROUTING_ExpeditedAppeal = 'Evaluate Expedited Appeal';
      Public Static final String POLICY_MEMBER_ERROR = 'Policy Member Not matching with the Account';
      Public Static final String CASEEDIT_NONE_DISP_HUM = '---None--';
      public static final List<String> Pharmacy_Record_Type_Name_LogCode_Population = New List<String>{'HP Member Case','HP Agent/Broker Case','HP Group Case','HP Provider Case','HP Unknown Case'};
  	  public static final string CRM_STOP_MILESTONE = '898';
      public static final string TRR_UI700_MS_PATTERN = '18';
      public static final string TRR_REJ_MS_PATTERN = '19';
      public static final string TRR_UI_SERIES_STOP_MS = '1803';
	    public Static final String HUMCaseTypeSIU = 'SIU';
      public Static final String HUMCaseTypePPI= 'PPI';
      public Static final String HUMCaseTypeCorrespondence ='Correspondence Inquiry';
      public Static final String HUMCaseSubTypeRoutine=null;
      Public Static final String HUMPharmacyIntType = 'Pharmacy';
      Public Static final String MEDICARE_STATUS_COMPLETED_HUM = 'Completed';
      public static final String IVRCASETYPE = 'IVR Afterhours';
      Public Static final String HUMProspectiveCustomerIntType = 'Prospective Customer';
      Public Static final String HUMAgentIntType = 'Agent';
      Public Static final String CASEEDIT_INTENT_SEPERATOR=')';
      
      Public Static Final String Grievance_Appeals_Permissionset_Name = 'CRMS_400_Grievance_Appeals';
      //--------------------------------------------REQ - 386935  ---------------------------------------------------------//
    public static final string WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY = 'Provider Payment Integrity';
    public static final string WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION = 'Louisville Other Insurance Investigation';
    public static final string WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT = 'Louisville RSO Oral Grievance Oversight';
    public static final string WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS = 'LV Medicare Member Communications';
    Public Static final List<String> WEB_CHAT_WQUEUES = New List<String>{'Humana Pharmacy Web Chat','HP Clinical Services Web Chat'};
	  Public Static final String METENROLLMENTMEDICAREEDIT_PERMISSIONSET_HUM = 'CRMS_630_MedicareElectionTracking_EnrollmentEdit';
    Public Static final String CRMSERVICESYSTEMNAME_HUM = 'CRM';
	  public static final string Group_LV_Correspondence_Screening_PGQ = 'LV GA and Correspondence Screening PGQ';
    public static final string Group_LV_Correspondence_Screening_PGVE = 'LV GA and Correspondence Screening E PGV';
    public static final string Group_LV_Correspondence_Screening_PGVP = 'LV GA and Correspondence Screening P PGV';
	  public static final string GOODCAUSEGROUPNAME = 'Louisville Premium Payment Recovery PGQ';
	  public static final string PCPGROUPNAME = 'PCP Assignment Research PGQ';
	  Public Static final String HumHPRecType = 'HP';
	  public static final string WEB_CHAT_ORIGIN = 'Web Chat'; 
    Public Static final String MEDICARE_POLICY = 'MED';
    Public Static final String sMHKTaskOrigin = 'Correspondence';
    public static final String MHK_CASE_TYPE = 'MHK Dispute Task';
    public static final string CLAIMS_MANUAL_ENTRY ='Claims Manual Entry';
    public static final string TRR_ORIGIN = 'CRMTRR';
    Public Static final String CASE_RSO_ENROLLMENT_OWNER = 'Louisville RSO Enrollment';
    Public Static final String CASE_STATUS_PENDING_RESPONSE = 'Pending - Response';
    Public Static final String INTERACTINGWITHTYPE_MEMBER = 'Member';
    Public Static final String CASEEDIT_UNKNOWN_MEMBER_HUM = 'Unknown Member';
    Public Static final String MEMBERCAREGIVER = 'Member Representative or Caregiver';
    public static final string GOODCAUSEQUEUE = 'Louisville Premium Payment Recovery';
    Public Static final String HUMMedicare = 'MedicareID';

    //Added for TaskHeirarchyCase_C_HUM 
    Public Static final String CASEMGMT_DATE_TIME_FORMAT = 'MM/dd/yyyy HH:mm a';
    Public Static final String MET_TASK_RECORDTYPE_HUM = 'MET_Task';
    Public Static final String CASEMGMT_DESCRIPTION_HUM = 'Description';
    Public Static final String CASEMGMT_TASKID_HUM =  'taskId';
    Public Static final String NONPHARMACY_USER_HUM = 'No';
      
    //Added for LinkedCases_C_HUM
    Public Static final String Sort_Expression_DESC_HUM = 'DESC';
    Public Static final String Sort_Expression_ASC_HUM = 'ASC';
    Public Static final Integer NUMBER_CONSTANT_ZERO_HUM = 0;
    Public Static final Integer NUMBER_CONSTANT_ONE_HUM = 1;
    Public Static final Integer PageSize_HUM = 5;
    Public Static final String CREATEDDATE_HUM = 'dCreatedDate';
    Public Static final Integer NUMBER_CONSTANT_SEVENTHOUSAND_HUM = 7000;
      
    //Added for CaseAttachemnts
    	public static final String sUS1334690 = '1334690';
    	public Static final String sUS1334714 = '1334714';
    	public static final string sMRMViewerURL = 'MRM_HUMREC_VIEWER';
    	public static final string sDocSourceMRM = 'MRM';
    	Public Static final String EHUB_URL_DOCID = '/apex/EhubDocumentViewer_VF_HUM?DocId=';
      Public Static final String LWC = 'LWC';
      Public Static final String  MRCALLOUT_SERVICE_NAME = 'EhubAddDocumentService';
      Public Static final String MRSYSCODE='CC';
      Public Static final String  MRDOCID = '1';
      Public Static final String CASE_SUBTYPE_INCOMPLETE_DE_SNP = 'Incomplete DE SNP';
      Public Static final String MEDICARE_CASE = 'Medicare Case';
    	Public Static final String EHUB_URL_VID = '&VId=';
      //Added for Related Information
		  public static final string sMsgDefCode = 'MsgDefCode';
		  Public Static final String HUMGetClaimSummary_V3 = 'ClaimsService_GetClaimsSummary_V3';
	  //Added for Case comments
		  Public Static final String CASECOMMENT_MEMBERNOTFOUND_HUM = 'The above CRM case comment  was not sent to the Order Management System (OMS)- also known as ePost/ANS.  Error Reason: This member does not have an Order Management System (OMS)- also known as ePost/ ANS- account.  This note is not saved to ePost Rx, but saved in CRM.  You can either close this tab, or click on the ‘Exit without Saving’ button on the case to continue.';
    	Public Static final String CASECOMMENT_FAULTERROR_HUM = 'The above CRM case comment  was not sent to the Order Management System (OMS)- also known as ePost/ANS.  Error Reason: The Order Management System (OMS)—also known as ePost/ ANS- is currently unavailable.  This note is not saved to ePost Rx, but saved in CRM.  You can either close this tab or click on the ‘Exit without Saving’ button on the case to continue.';      
      Public Static final String RSO_HP_PermissionSet_Name= 'CRMS_206_CCSHumanaPharmacyAccess';
      Public Static final String PDPPilot_PermissionSet_Name= 'CRMS_205_CCSPDPPharmacyPilot';
      Public Static final String CASECOMMENT_PHARMACY_MEMBERNOTFOUND_HUM = 'This member does not have an Order Management System (OMS) - also known as ePost/ANS  - account. The Case Comment is not saved to ePost Rx, but saved in CRM';
    	Public Static final String CASECOMMENT_PHARMACY_FAULTERROR_HUM =  'The Order Management System (OMS) - also known as ePost/ANS  - is currently unavailable. The Case Comment is not saved to ePost Rx, but saved in CRM';
      Public Static final List<String> Pharmacy_Record_Type_Name = New List<String>{'Closed HP Agent/Broker Case','Closed HP Group Case','Closed HP Member Case','Closed HP Provider Case','Closed HP Unknown Case','HP Agent/Broker Case','HP Group Case','HP Member Case','HP Provider Case','HP Unknown Case'};
    	//Added for Compass section
		public static final string HUMCompassButtonRF = 'Launch Compass';
		public static final string HUMCompassButtonNonRF = 'Search Compass';
    //Added for process section
     public static final string TRR_FIELD_REPLYCD = 'Reply Code';
     public static final string TRR_FIELD_EFFDATE = 'Effective Date';
     public static final string TRR_FIELD_PROCDATSTMP = 'Processing Date Stamp';
     public static final string TRR_TEMPLATE_NAME = 'TRR_Template';
     Public Static final String BACKSLASH_HUM = '/';
      
    public static final String LOGCODEHSP_CLASSIFICATIONTYPE = 'HP Specialty Calls';
	  public Static final String LOGCODEHSP_SERVICEMODELTYPE = 'Humana Pharmacy';
	  public Static String CLOSEDHP_MEMBERCASE_HUM = 'Closed HP Member Case';
	  public static final string HP_MEMBERCASE_HUM = 'HP Member Case';

  }