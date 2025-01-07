/************************************************************************************************************************************************
  Apex Class Name  : CaseDetail_C_HUM
  Version          : 1.0 
  Created Date     : OCT 25 2017
  Function         : Class to hold the Case Detail Page redirection and floating links visibility functionality. 
                     This page overrides the view button on case. If the user possesses the valid permission set , he/she gets redirected to the the relevant case Detail VF Page. Otherwise the user would see the standard case detail screen. 
  Test Class       : 
  * Version                   Developer Name            Code Review                 Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------
  * 1.0                        Rakesh Patni              146796                  06/20/2017                  Original Version 
  * 1.1                        Srivinas Damera           188503                  2/22/2018                   REQ - 335172 - Removal of Permission Set to Access Custom Case Page  
  * 2.0                        Prafull Verma             181027                  02/06/2018                  Added Redirect logic for Appeal case override.
  * 3.0                        Srivinas Damera           199956                  3/15/2018                   REQ � 357689 - Redirect Out of box case detail page for RPA
  * 3.1                        Bharath Rajappan          214134                  04/12/2018                  REQ - 361043 - Add the process floating link for Medicare case Creditable Coverage
  * 3.2                        Bharath Rajappan                                  05/05/2018                  REQ - 360169 - Redirect to case Edit
  * 3.3                        Bharath Rajappan                                  05/12/2018                  REQ - 359179 - Disable change case owner button for creditable coverage 
  * 3.4                        Dinesh Subramaniyan                               05/16/2018                  REQ - 361879 - Add Medicare ID to Member Case Page (RF) 
  * 3.5                        Bharath Rajappan                                  05/18/2018                  REQ - 361964 - creditable coverage show process section and floating link
  * 3.6                        Satyam Malviya                                    06/05/2018                  REQ - 363542-SF - TECH - Rollback Case Detail Page      
  * 3.7                        Sunil Bukya               251880                  06/17/2018                  REQ - 366900 - Implemented Member_Id__c platform encryption changes.
  * 3.8                        Bharath Rajappan                                  07/11/2018                  REQ - 367804 Creditable Coverage - Ability for CRM to disable Case Edit and Case Detail page buttons
  * 3.9                        Darshan Sharma                                    07/20/2018                  REQ-370566 Quality - Remove ability to Close Case if the User is not the Case Owner (RF)
  * 4.0                        Darshan Sharma                                    08/24/2018                  Defect379843.000 - REQ-370566 Quality - Remove ability to Close Case if the User Is Not the Case Owner (RF)
  * 4.1                        Asish Behera                                      08/26/2018                  REQ - 373413 AHT - Ability to Notify User of Logged Attachments (RF)
  * 4.2                        Lakshmi Vutukuri                                  9/04/2018                   REQ-361157-  Complaint tracking fields 
  * 4.3                        Darshan Sharma                                    09/09/2018                  REQ - 374857 Quality - Ability for Case Owner to be updated when a HP Supervisor closes a Case. (RF)
  * 4.5                        Darshan Sharma                                    10/17/2018                  REQ - 375071 Quality - Remove ability to Close Case if the User is not the Case Owner (CRM)
  * 4.6                        Melkisan Selvaraj        317009                   12/06/2018                  Made Changes in SOQL query of Attachment log
  * 4.7                        Prathyu Tushar           324032                   01/02/2019                  REQ - 380419: Auto Rotue Case for Madicare Appeal and Expedited Process 
  * 4.8                        Darshan Sharma                                    11/29/2018                  REQ - 378732 AHT - Ability to Notify User of Logged Attachments (CRM)
  * 4.9                        Prudhvi Pamarthi                                  02/06/2019                  REQ - 361155 - Not allowing case creation on Legacy Deleted policies
  * 5.0                        Lakshmi Madduri                                   02/15/2019                  REQ-385259
  * 5.1                        Subhamay Mandal                                   02/11/2019                  REQ - 383946 AHT - B&E - View Case Creation Date on Case Detail section (RF)  
  * 5.2                        Subhamay Mandal                                   02/12/2019                  REQ - 383946 Add Rapid Force permission.  
  * 5.3                        Praveen Kumar Parimi                              03/08/2019                  REQ - 387561 aka CA Ticket - 8109263 - HP Provider Case detail not showing up after creation.
  * 5.3                        Pradeep Dani             350910                   04/12/2019                  REQ - 396202 - Removing changes for Live agent case chat key association.
  * 5.4                        Santhosh Ganji                                    04/19/2019                  REQ - 386935 - Case Detail Restrictions (MED vs non-MED cases)—READ ONLY features
  * 5.5                        Amar Gadewar                                      05/02/2019                  REQ - 397162 - AHT - Enrollment - View Date/Time Opened field on Case Detail section (CRM)
  * 5.6                        Santhi Mandava                                    04/15/2019                  Replaced "Member_Id_Value__c" with Name
  * 5.7                        Prathyu Tushar           356946                   05/22/2019                  Defect# 417759.0001, adding caseComments and interaction in redirectcasedetail based on recordtype.
  * 5.8                        Santhosh Ganji                                    05/24/2019                  REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
  * 5.9                        AshokKumar Nutalapati                             07/08/2019                  REQ - 402833: Automatically Record Order Details To Case: Add Logging Component To Create Order Page (CRMS)
  * 6.0                        Prathyu Tushar                                    07/18/2019                  REQ - 396302: removing persmissionset check To move ID - 27 logging To Mainstream. 
  * 7.0                        Rakesh Patni                                      08/20/2019                  REQ - 406104. Launch Compass Functionality
  * 7.1                        Subhamay Mandal                                   09/19/2019                  REQ - 408295 Quality - OGO - Disable Change Case Owner button when the case is routed to 'Louisville RSO Oral Grievance Oversight' WQ (RF)
  * 7.2                        Amar Gadewar                                      11/13/2019                  REQ - 415955 Quality - OGO - Disable Change Case Owner and Save and Transfer button when the case is routed to 'Louisville RSO Oral Grievance Oversight' WQ (CRM)
  * 7.3                        Vijaya Koriprolu                                  11/18/2019                  REQ-383958 added functionality for adding claim numbers on case edit page
  * 7.4                        Mithra Bharadwaj                                                              REQ-414518 fixed issue with logged count
  * 7.5                        Moshitha Gunasekaran                              02/04/2020                  US752867 - New TRR Templates Process Section 
  * 7.6                        Vinay lingegowda                                  03/04/2020                  USER STORY 856241 PR00091574 - MF 1 - Quality - PCP Template Updates (CRM)
  * 7.7                        Vinay Lingegowda                                  03/19/2020                  US-857398 AHT/Quality - Medicare Good Cause Reinstatement Template Auto Routing and AHT Updates (CRM)
  * 7.8                        Mohammed Noor                                     03/26/2020                  US#975731 - Add DEAA OnOff Switch changes
  * 7.9                        Mohammed Noor                                    04/21/2020                  US#1020879 - Remove DEAA OnOff Switch
  * 8.0                        Mohammed Noor                                    06/08/2020                  Changes done for CR674 - US1034997, US1034977 and US1035031
  * 8.1                        Himalay Patel                                    06/24/2020                  REQ - 939336  Display Logged 'Invoice Request' Process on Case Detail Page (RxRF) 
  * 8.2                        VijayaKumar                                      06/29/2020                  USER STORY 1203975 PR00095159 - IR 50-50 OGO - MVP - CRM Action page layout to provide feed back for ODM returned Case
  * 8.3                        Amit Agarwal                                     07/06/2020                  User Story 1122108: PR00091574 - MF 1 - Quality - OGO - Ability to display Resolution Type and Resolution Date fields on Medicare Case Detail Page (CRM)
  * 8.4                        Amit Agarwal                                     07/06/2020                  User Story 1122091: PR00091574 - MF 1 - Quality - OGO - Ability to display Resolution Type and Resolution Date fields on Closed Medicare Case Detail Page (CRM)
  * 8.5                        Vinay Lingegowda                                 07/09/2020                  USER STORY 1203975 PR00095159 - IR 50-50 OGO - MVP - CRM Action page layout to provide feed back for ODM returned Case Defect Fix- 1042
  * 8.6                        Subhamay Mandal                                  07/15/2020                  User Story 1204162. Added On/OFF Switch
  * 8.7                        Amit Agarwal                                     07/23/2020                  User Story 1122322: PR00091574 - MF 1 - Quality - OGO - Disable Close Case button for Customer Care Supervisor if the case is routed to "Louisville RSO Oral Grievance Oversight" WQ (CRM)
  * 8.8                        Vinay Lingegowda                                 07/23/2020                  USER STORY 1278792 PR00091574-IR-50-50-OGO- disable close case option for actionable cases
  * 8.9                        Vinay Lingegowda                                 08/03/2020                  DF-1245 Fix.
  * 9.0                        Suresh Kokkeragadda                              08/15/2020                  User Story 1317726: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 7/30 , 8/14 and 8/28 releases (CRM)
  * 9.1                        Vinay Lingegowda                                 08/24/2020                  User Story 1314983: PR00091574- IR 5050-OGO -Open case in Edit mode to set complaint fields (Scrubber Action-Accept on open cases) -(CRM)
  * 9.2                        Vijaykumar                                       08/24/2020                  User Story 1363774: PR00091574 - IR 5050-OGO- Display case initiated date and case initiated Age on medicare/member case details page (CRM)
  * 9.3                        Vijaykumar                                       09/04/2020                  User Story 1363774: PR00091574 - IR 5050-OGO- Display case initiated date and case initiated Age on medicare/member case details page (CRM) 
  * 9.4                        Darshan MS                                       09/08/2020                  DEF-1492 FIX
  * 9.5                        Akshay pai                                      09/16/2020                   REQ - 1382511 Agent & Employer Portals: Transcript attachment to Agent or Group Details Pages
  * 9.6                        Vijaykumar                                      09/10/2020                   User Story 1395924: PR00091574- IR 5050- OGO -Add a Yes indicator - for original case (CRM)
  * 9.7                        Vinay Lingegowda                                10/10/2020                  USER STORY 1544779 PR00091574 - IR 5050 - OGO - Tech Debt- Remove switch for OGO on/off(CRM)
  * 9.8                        Himalay Patel                                    10/14/2020                   REQ - 1145316  Display Completed 'Humana Pharmacy Web Issues' Process on Case Detail Page (RxRF)    
  * 9.5                        Akshay pai                                       09/16/2020                  REQ - 1382511 Agent & Employer Portals: Transcript attachment to Agent or Group Details Pages
  * 9.6                        Vijaykumar                                       09/10/2020                  User Story 1395924: PR00091574- IR 5050- OGO -Add a Yes indicator - for original case (CRM)
  * 9.7                        Vinay Lingegowda                                 10/10/2020                  USER STORY 1544779 PR00091574 - IR 5050 - OGO - Tech Debt- Remove switch for OGO on/off(CRM)
  * 9.8                        Himalay Patel                                    10/14/2020                  REQ - 1145316  Display Completed 'Humana Pharmacy Web Issues' Process on Case Detail Page (RxRF)
  * 9.9                        Santhi Mandava                                   01/29/2021                  User Story 1791226: PR00094254 - MF 4- AEP Performance - SF Limits - Sync CPU Time Limit Analysis - Case Details
  * 10.0                       Santhi Mandava                                   01/29/2021                  User Story 1083065: PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case
 *  10.1                       Shreya Agrawal/Isha Gupta						01/30/2021			        PhoneBook Implementation
 *  10.2                       Vinay Lingegowda                                 03/17/2020                  USER STORY 2044469 IR 5050 - OGO - Optimize Accept recommendation actions for queues with clone & link feature (CRM)
 *  10.3                        Mithra Bharadwaj                                                            US#1522825 SonarQube Changes
 *  10.4                       Kavya Bondada                                    04/14/2021                  User Story 2187149: T1PRJ0001827 - MF 1 - Quality - Auto route QAA cases to the G&A team (CRM)
 *  10.5					   Mohammed Noor									04/28/2021					USER STORY 2241808: DF-2708: REGRESSION: Case Detail Page- Incomplete Process Medicare Part D Redetermination
 *  10.6                       Kavya Bondada                                    04/14/2021                  User Story 2187149: T1PRJ0001827 - MF 1 - Quality - Auto route QAA cases to the G&A team (CRM) Observations Fixes 
 *  10.7                       Amar Gadewar                                     05/21/2021                  User Story 1874589: T1PRJ0001827 - MF 1 - AHT - "Medicaid PAR Provider Not Accepting" template auto routing (CRM)
 *  10.8                       Amar Gadewar                                     06/17/2021                  User Story 2389201: T1PRJ0001827 - MF 1 - Quality - Ability to exclude FL LTC QAA Complaint cases from auto routing process (CRM)
 *  10.9                       Sunil Kumar Reddy Y                              07/05/2021                  User Story 2393208: T1PRJ0001827 - MF 1 - Quality - 'Medicaid PAR Provider Not Accepting' Template Routing Rules Update (CRM)
 *  11.1                       Atia Uzma                                        07/29/2021                  User Story 2498842: T1PRJ0001827 - MF 1 - AHT - Ability to Reopen and Edit closed cases (RF)
 *  11.2                       Mohammed Noor                                    08/19/2021                  USER STORY 1801197 - PR00094010 -STRIDES MVP MF 6 - Unified Search Button Case page
*	11.3					   Gowthami Thota									08/02/2021					US - 2543348 - T1PRJ0002606/PR00094268  - CR01 CSI 1922  - Performance improvement of Case detail and Edit page 
*   11.4					   Mohammed Noor                                    09/23/2021                  Changes for USER STORY 2712408
  * 11.5                       Sahil Verma                                      10/14/2021                  Show popup on Case/Plan/Detail page on click of Save button on Interaction Log Panel as part of US - 2564484
  * 11.6                       Bhumika S                                        02/02/2022                  User Story 3010498: T1PRJ0314829 - MF 1 - Quality - Ability to move cases from an unknown to a known Member (RF)
  * 11.7                       AshokKumar Nutalapati         		            05/09/2022				    User Story 2935001: T1PRJ0200895 - MF 1 - Inactivate Rx: Accessing Completed Process and Case (CRMS) (ID# 163)
*   11.8					   Kiran Kumar										06/07/2022					User Story 2310106:  CR 008LS- ESS ID 305- Add an Escalation Indicator in CRM Service
*   11.9					   Prasuna Pattabhi									07/28/2022					User Story 3518085 : View Case Logged Information from Lightning
*   12.0					   Kiran Kumar B									10/07/2022					User Story 3627766: Auto-route the Medicaid PAR Provider Not Accepting template for IL-LTSS, OH and LA (Classic)
*   12.1                       Ajay Chakradhar                                  12/07/2022                  User Story 3888816 : T1PRJ0316976 - SF - MHK provider disputes (est 634) - CRM DCN field for MHK Dispute Task Case Type (Classic)
*   12.2                       Aaron Speakman                                   04/13/2023                  User Story 4091016: T1PRJ0272555 INC2059619 CRM Service (Salesforce.com) - Auto-routing MCD cases not able to close case# 1000332040944
*****************************************************************************************************************************************************************************/

public with sharing class CaseDetail_C_HUM
{
    private Case caseRec;
    private map<string,string> mapRecordTypePages;
    public Boolean bMedicareCalls{ get; set; }
    public String sMedicareID {get; set;}
    private String sUserProfile;
    public boolean MedExpButtonDisableValue {get;set;}
    public boolean bQueueUser {get;set;} 

    
    public Boolean isMedExpProcessAttached{get;set;} 
    public Boolean isMedExpProcessAttachedPart{get;set;} 
    @testVisible private Boolean isGoodCauseProcessAttached{get;set;} 
    @testVisible private Boolean isPCPProcessAttached{get;set;} 
    @testVisible private Boolean isMedicaidPARProviderProcessAttached{get;set;}
    @testVisible private Boolean isInvoiceRequestAttached{get;set;} 
    @testVisible private Boolean isCreditableCoverageProcesstAttached{get;set;} 
    public boolean bdisableOwnerCloseCase {get; set;}
    public Integer iLoggedInattachmentCount {get; set;}
    public Boolean bCsertComplaints{get;set;}
    public Boolean isDisplay {get;set;}
    public Boolean bdisableButton {get; set;}
    public Boolean bnotClosedCase {get; set;}
    public Boolean blegacyDeleteCheck;
    public Boolean bIsMedicareUser{get; set;}
    public Boolean bIsPharmacyUser{get;set;}
    public Boolean CaseReadOnly{get;set;}
    public Boolean bmedicarecase{get;set;}
    public Set<String> wqueues = new Set<String>{CaseDetailConstants_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,CaseDetailConstants_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,CaseDetailConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,CaseDetailConstants_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public string sCaseComments{get;set;}
    public string sinteractionId{get;set;}
    public Set<String> chatQueues = new Set<String>(CaseDetailConstants_HUM.WEB_CHAT_WQUEUES);
    public Boolean isFromPharmLogging{get;set;}
    //Launch Compass Functionality
    public static String sFunction{get;set;}
    public static String sSubFunction{get;set;}
    public Boolean isRapidForceApp {get;set;}
    public Boolean bDisableButtonOGO{get; set;}
    public Boolean bIsInvoiceRequestAttached{get; set;}
    public Boolean bIsPharmacyWebIssuesAttached{get; set;}
    public Boolean bIsInactivateRxAttached{get; set;}
    public boolean bExpPDPLinkAvailable {get; set;}
    public boolean bExpFlag_US1034997 {get; set;}
    public boolean bPartDFlag_US1034977 {get; set;}
    public Boolean bRecomQueueView{get; set;} 
    public Boolean bHasNoFeedbackTask{get; set;} 
    public Boolean bDisableActionButton {get; set;} 
    public boolean bOGOScrubberUser{get;set;}
    public boolean bIsMHKTask{get;set;}
    private boolean bMHKTaskSwitch{get;set;}
    public boolean bMHKDCNSwitch{get;set;}
    public boolean bIsMHKDCN{get;set;}
    
    //US#2564484	
    public static boolean bPopup{get; set;}
    public Boolean bIsOGOLinked{get; set;}
    public Boolean bIsOriginal{get; set;}    
    private List<Template_Submission__c> lstTemplateSubmissions;
    private List<GroupMember> lstGrpMems;
    private List<PermissionSetAssignment> lstUserPermissions;
    public Boolean bDisableEditCaseBtn{get;set;}
    public Boolean bDisableCloseCaseBtn{get;set;}
    public Boolean bDisableTransferCaseBtn{get;set;}
	public Boolean bCaseUnifiedSearch {get; set;}
    public String sCoveoMessage {get; set;}
    public Boolean bSwitch_3010498 {get;set;}
    public Boolean bChatTranscript_US3792216{get;set;}
    public boolean bShowEsclIndicator{get;set;}
    public boolean bShowEsclIndicatorDate{get;set;}
    public Boolean bShowEsclIndSwitch{get;set;}
	private static final string COVEO_CRMPAGENAME = 'L_CRMS_Case_Detail';
	private static final String ChromeBrowser = 'Chrome/';
	private static final String EdgeBrowser = 'Edg';

    //To check if a user is a genesys user or not
    public Boolean isGenesysUser{
        get
        {
            Id callcenterid = [SELECT CallCenterId FROM User WHERE id = :UserInfo.getUserId()].CallCenterId;
            if(callcenterid != null){
                return true;
            }else{
                return false;
            }
        }
        set;
    }
    
    public Boolean phoneBookSwitch1726016 
    {
        get
        {
            return HUMUtilityHelper.isCRMFunctionalityON('1726016_CallTransferToPhoneBook');
        }
        set;
    }

    public Boolean reopenAndEditSwith{
        get
        {
            Boolean reopenAndEditCheck = false;
            if( HUMUtilityHelper.isCRMFunctionalityON('2498842') && UtilityHelper_H_AHT_HUM.isRapidForceUser()){
                reopenAndEditCheck = true;
            }
            return reopenAndEditCheck; 
        }
        set;
    }
    
    /*   
     * Constructor
     * @param  NA
     * @return NA 
     */
    public CaseDetail_C_HUM(ApexPages.standardController std)
    {   
        isFromPharmLogging = false;
        String sValue = ApexPages.currentPage().getParameters().get('caseComment');
        If(String.IsNotBlank(sValue)) {
            isFromPharmLogging = true;
        }
        currentUser = new User();
        bMedicareCalls = false;
        bdisableOwnerCloseCase = false;
        lstUserPermissions = [SELECT Id,PermissionSetId,PermissionSet.Name FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId()];
        this.bIsMedicareUser = checkUserPermission(CaseDetailConstants_HUM.METENROLLMENTMEDICAREEDIT_PERMISSIONSET_HUM);
        
        currentUser = [Select id, UserPermissionsKnowledgeUser, UserPermissionsLiveAgentUser,Profile.Name,Current_Queue__c, Mentor_Sub_Function__c from User where id = :UserInfo.getUserId()];
        sUserProfile = currentUser.Profile.Name;
        pfName = Label.HUMAgencyCCSupervisor;
        
        isArticleVisible = false;
        isDisplay = true;
        bIsPharmacyUser = false; 
        CaseReadonly = true;
        bmedicarecase = false;
        this.bRecomQueueView = false; 
        this.bHasNoFeedbackTask = false;
        this.bDisableActionButton = false;
        this.bOGOScrubberUser = false;
        this.bIsOGOLinked = false;
        this.bIsOriginal = false;
       
        isArticleVisible = checkUserPermission(CaseDetailConstants_HUM.sLiveAgentSpecialistPermSet);

        caseRec = (Case)std.getRecord();
            caseRec = [SELECT Id,OwnerId,recordtype.name,Policy_Member__r.Policy__c,Origin,Type,Policy_Member__c,Policy_Member__r.Policy__r.Major_Lob__c,Policy_Member__r.Policy__r.Product__r.ProductCode, Policy_Member__r.Policy__r.Product__r.Major_LOB__c, AutoRoute_Status__c, Owner_Queue__c,Created_By_Queue__c,
            Policy_Member__r.Policy__r.Product__c,Product__c,Policy_Member__r.Policy__r.Product__r.Product_Type__c,Policy_Member__r.Policy__r.Product__r.Segment_Type__c,Case_Owner__c,Guided_Process_Flag__c,status,AccountId,Work_Queue_View_Name__c,Account.Name,Account.RecordType.Name,Account.ETL_Record_Deleted__c,Policy_Member__r.ETL_Record_Deleted__c,			
            Policy_Member__r.Segment_Indicator__c,Policy_Member__r.Issue_State__c,Policy_Member__r.Product_Type__c,Policy_Member__r.Group_Number__c,
                   Policy_Member__r.Product__c, Policy_Member__r.Policy__r.Source_Market__c, Interacting_With_Type__c, Interacting_About_Type__c,  Interacting_With__r.name, Casenumber,Classification_Type__c, Escalation_Indicator__c,Escalation_Indicator_Date__c,
            (SELECT Id,Attachment_Type__c FROM Attachment_Logs__r WHERE IsDeleted = false And Source_System__c in (:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CRM,:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CRM_LIGHTNING) And Case__c !=null limit 500)
            FROM case WHERE id=:caseRec.id];
        List<String> lstPublicGroups = new List<String>{CaseDetailConstants_HUM.Group_LV_Correspondence_Screening_PGQ, CaseDetailConstants_HUM.Group_LV_Correspondence_Screening_PGVE, CaseDetailConstants_HUM.Group_LV_Correspondence_Screening_PGVP,CaseDetailConstants_HUM.GOODCAUSEGROUPNAME,CaseDetailConstants_HUM.PCPGROUPNAME};
        lstPublicGroups.addAll(CaseAutoRouteConstant_C_HUM.QAAGroupNames);
        lstPublicGroups.add(CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP);
        lstPublicGroups.add(CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP_KY);
        lstPublicGroups.add(CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP_IL);
        lstGrpMems = [Select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId()   and group.type='Regular' and Group.Name IN : lstPublicGroups];
        lstTemplateSubmissions = [SELECT Template__r.Unique_Template_Name__c,Template__r.Description__c FROM Template_Submission__c WHERE Submission_Owner__r.Object_Owner_ID__c =:caseRec.Id];
        CheckCaseTemplates();
        disableChangeOwnerCreditableCoverage();

        sMedicareID ='';       
        if(caseRec.Policy_Member__c !=null && caseRec.Policy_Member__r.Policy__c !=null){
            MedicareCallsCaseComments_H_HUM oMedCalls = new MedicareCallsCaseComments_H_HUM();
            bMedicareCalls = oMedCalls.checkMedicareCare(caseRec.Origin, caseRec.Policy_Member__r.policy__r.Product__r.ProductCode, caseRec.Policy_Member__r.policy__r.Major_Lob__c, caseRec.Policy_Member__r.policy__r.Product__r.Product_Type__c, caseRec.Policy_Member__r.policy__r.Product__r.Segment_Type__c);
            if(bMedicareCalls)
            {
                List<Member_ID__c> listMemberIds = [SELECT Id,Name FROM Member_ID__c WHERE Policy_Member__c =: caseRec.Policy_Member__c AND Type__c =: CaseDetailConstants_HUM.HUMMedicare limit 1];
                if(!listMemberIds.isEmpty()) 
                sMedicareID = listMemberIds[0].Name;
            }
        }
        String pname = ApexPages.currentPage().getUrl();
        String pageName = ApexPages.currentPage().getUrl().substringBetween(CaseDetailConstants_HUM.sUrl,'?');
        if(!pageName.containsIgnoreCase(CaseDetailConstants_HUM.sRedirectPage))
        {
            // This is to make sure that the floating links creation happens only when the Case Detail VF Page open and not on load of the CaseDetailRedirect page     
            createFloatingList();
            removeFloaters(pageName);            
        }
        else{
            constructRecordTypePageMap();           
        }
        if(!Test.isRunningTest()){
            isMedExpProcessAttachedExp();
            isMedExpProcessAttachedPartD();
            isMedExpProcessAttachedGoodCause();
            isPCPProcessAttached();
            isInvoiceRequestAttached();
            isMedicaidPARProviderProcessAttached();
        }
            isQAAProcessAttached();
        if((sUserProfile == Label.HUMUtilityCSS || (sUserProfile == Label.PHARMACY_SPECIALIST_PROFILE_NAME && !UtilityHelper_H_AHT_HUM.checkCustomPermissionAccess(Constants_C_AHT_HUM.PHARMACY_SUPERVISOR_HUM))) && caseRec.OwnerId != UserInfo.getUserId() )
        {
            bdisableOwnerCloseCase = true;
        }
        this.bDisableButtonOGO = disableButtonOGOCase(caseRec.Owner_Queue__c);
        if(bdisableOwnerCloseCase == false && sUserProfile == Label.HUMAgencyCCSupervisor){
            bdisableOwnerCloseCase =  this.bDisableButtonOGO;
        }
        
        bCsertComplaints = checkUserPermission(CaseDetailConstants_HUM.sCsertComplaints);

        bdisableButton = verifyLegacyDelete();
        bnotClosedCase = false;
        if(blegacyDeleteCheck && caseRec.status!='Closed') bnotClosedCase = true; 
        
        if(sUserProfile == Label.PHARMACY_SPECIALIST_PROFILE_NAME)       bIsPharmacyUser  = true;
        
        if(!caseRec.recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType))
        {
          if(bIsPharmacyUser && ((chatQueues.contains(caseRec.Owner_Queue__c) && caseRec.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN) || caseRec.Product__c == CaseDetailConstants_HUM.MEDICARE_POLICY))
          {
            bmedicarecase = true;
            if(wqueues.contains(caseRec.Owner_Queue__c) || wqueues.contains(caseRec.Created_By_Queue__c) || (chatQueues.contains(caseRec.Owner_Queue__c) && caseRec.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN))
            {
              CaseReadonly = false;
            }
          }
        }
        fetchUserCompassDetails();
        isRapidForceApp = HUMUtilityHelper.IsRapidForceAPP();
        this.bRecomQueueView = UtilityHelper_H_AHT_HUM.isRecommendedQueueView(caseRec.Work_Queue_View_Name__c);
            this.bHasNoFeedbackTask = UtilityHelper_H_AHT_HUM.hasCaseFeedback(caseRec.id);
            this.bDisableActionButton = UtilityHelper_H_AHT_HUM.disableActionButton(caseRec.Owner_Queue__c, caseRec.Case_Owner__c);
            if(bRecomQueueView && bHasNoFeedbackTask && (sUserProfile == Label.CaseOgoProfile_HUM || sUserProfile == Label.HUMAgencyCCSupervisor) && caseRec.Status == 'In Progress' && caseRec.Owner_Queue__c == Label.caseDetailOGOQueue_HUM && isOGOScrubberUser() ){
                this.bdisableOwnerCloseCase = true;
                this.bOGOScrubberUser = true;
                this.bDisableButtonOGO =true;
        }
        
        bExpFlag_US1034997 = HUMUtilityHelper.isCRMFunctionalityON('1034997');  
        bPartDFlag_US1034977 = HUMUtilityHelper.isCRMFunctionalityON('1034977');
        bChatTranscript_US3792216 = HUMUtilityHelper.isCRMFunctionalityON('3792216');
        bExpPDPLinkAvailable = false;
        if(caseRec.Policy_Member__c != NULL)    
        {
            if(lstTemplateSubmissions != NULL && !lstTemplateSubmissions.isEmpty())
            {
                bExpPDPLinkAvailable = false;
            }
            else {
                bExpPDPLinkAvailable = HUMUtilityHelper.verifyExpOrPartDLinkExists(caseRec.Id, caseRec.status, caseRec.recordType.Name,caseRec,true);
            }
        }
         checkOGORelation(caseRec.Id);
                
        bIsMHKTask = false;
        bMHKTaskSwitch = HUMUtilityHelper.isCRMFunctionalityON('1414643');
        if(bMHKTaskSwitch && caseRec.recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.sMember) && caseRec.Origin == CaseDetailConstants_HUM.sMHKTaskOrigin && caseRec.Type == CaseDetailConstants_HUM.MHK_CASE_TYPE)
        {
            bIsMHKTask = true;
        }
		string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');		
        bCaseUnifiedSearch = UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible() && HUMUtilityHelper.isCRMFunctionalityON('1801197') && String.isNotBlank(sBrowserContext) && (sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser));
        if(bCaseUnifiedSearch)
        {
            sCoveoMessage = createCoveoMessage();
        }

        this.bSwitch_3010498 = HUMUtilityHelper.isCRMFunctionalityON('3010498');
	    bShowEsclIndicator = false;
        bShowEsclIndSwitch = HUMUtilityHelper.isCRMFunctionalityON('2310106');
        if(bShowEsclIndSwitch){
            showEsclIndicatorAndDate();
        }
	    bIsMHKDCN = false;
        bMHKDCNSwitch = HUMUtilityHelper.isCRMFunctionalityON('3888816');
        if(bMHKDCNSwitch && caseRec.Origin == 'Correspondence' && caseRec.Type == 'MHK Dispute Task')
        {
        	bIsMHKDCN = true;    
        }	
    }

    public list<string> lst_FloatId{get;set;}
    public User currentUser{ get; private set; }
    public boolean isArticleVisible{ get; private set; }
    public string pfName{ get; set; }

    /*
     * This method is used to check if any case template attached or not
     * @param  NA
     * @return void
     */
    @testVisible private void CheckCaseTemplates()
    {
        isMedExpProcessAttached = false;
        isMedExpProcessAttachedPart = false;
        isGoodCauseProcessAttached = false;
        isPCPProcessAttached = false;
        isInvoiceRequestAttached = false;
        isCreditableCoverageProcesstAttached = false;
        isMedicaidPARProviderProcessAttached = false;
        bIsInactivateRxAttached = false;
        for(Template_Submission__c ObjTemp : lstTemplateSubmissions)
        {
            switch on ObjTemp.Template__r.Unique_Template_Name__c  
            {
                when '95C90830-3133' {
                    isMedExpProcessAttached = true;
                }
                when 'BC929CD6-E06A' {
                    isMedExpProcessAttachedPart = true;
                }
                when 'E0A13562-89EE-48B8-8EBA-E485C6583FF1' {
                    isGoodCauseProcessAttached = true;
                }
                when 'd884d160-77c0-4d1b-95aa-edf8c81f3a59' {
                    isPCPProcessAttached = true;
                }
                when 'ATVF007' {
                    isCreditableCoverageProcesstAttached = true;
                }
                when 'deeef952-9f63-49ce-97d0-c7eb3204b519' {
                    isMedicaidPARProviderProcessAttached = true;
                }
            }
            if(ObjTemp.Template__r.Description__c == 'Invoice Request') isInvoiceRequestAttached = true;
            if(ObjTemp.Template__r.Description__c == 'Inactivate Rx') bIsInactivateRxAttached = true;
        }
    }

    /*
     * This method is used to check if logged in use has given permission set access.
     * @param  String - sPermissionSetName - Permission set name
     * @return Boolean
     */
    public Boolean checkUserPermission(String sPermissionSetName)
    {
        Boolean bHasPermission = false;
        For(PermissionSetAssignment objPerSetAssignment : lstUserPermissions)
        {
            if(objPerSetAssignment.PermissionSet.Name == sPermissionSetName)
            {
                bHasPermission = true;
                break;
            }
        }
        return bHasPermission;
    }

    /*
     * This method Is to add create a map of case record type v/s the page to which user would be redirected 
     * @param  NA
     * @return void
     */
    public void constructRecordTypePageMap()
    {
        mapRecordTypePages = new map<string,string>();
        mapRecordTypePages.put(CaseDetailConstants_HUM.sAgentRType,CaseDetailConstants_HUM.sAgentPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sClosedAgentRType,CaseDetailConstants_HUM.sAgentPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sClosedHPAgentRType,CaseDetailConstants_HUM.sAgentPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sHPAgentRType,CaseDetailConstants_HUM.sAgentPageName);

        mapRecordTypePages.put(CaseDetailConstants_HUM.sClosedGroupRType,CaseDetailConstants_HUM.sGroupPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sClosedHPGroupRType,CaseDetailConstants_HUM.sGroupPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sGroupRType,CaseDetailConstants_HUM.sGroupPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sHPGroupRType,CaseDetailConstants_HUM.sGroupPageName);

        mapRecordTypePages.put(CaseDetailConstants_HUM.sHPProviderRType,CaseDetailConstants_HUM.sProviderPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sClosedProviderRType,CaseDetailConstants_HUM.sProviderPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sHPProviderRType,CaseDetailConstants_HUM.sProviderPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sProviderRType,CaseDetailConstants_HUM.sProviderPageName);

        mapRecordTypePages.put(CaseDetailConstants_HUM.sClosedHPUnknownRType,CaseDetailConstants_HUM.sUnknownPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sClosedUnknownRType,CaseDetailConstants_HUM.sUnknownPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sHPUnknownRType,CaseDetailConstants_HUM.sUnknownPageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sUnknownRType,CaseDetailConstants_HUM.sUnknownPageName);

        mapRecordTypePages.put(CaseDetailConstants_HUM.sMemberRType,CaseDetailConstants_HUM.sMemberPageName);   
        mapRecordTypePages.put(CaseDetailConstants_HUM.sClosedMemberRType,CaseDetailConstants_HUM.sMemberPageName); 

        mapRecordTypePages.put(CaseDetailConstants_HUM.sClosedHPMemberRType,CaseDetailConstants_HUM.sPharmacyPageName);//pharmacy
        mapRecordTypePages.put(CaseDetailConstants_HUM.sHPMemberRType,CaseDetailConstants_HUM.sPharmacyPageName);//pharmacy
        mapRecordTypePages.put(CaseDetailConstants_HUM.sClosedHPProviderRType,CaseDetailConstants_HUM.sProviderPageName);//Pharmacy

        mapRecordTypePages.put(CaseDetailConstants_HUM.sMedicareRType,CaseDetailConstants_HUM.sMedicarePageName);
        mapRecordTypePages.put(CaseDetailConstants_HUM.sClosedMedicareRType,CaseDetailConstants_HUM.sMedicarePageName);
        
    }

    /*
     * This method Is to create the list of floating links which are visible at the top of the customized case Detail VF pages 
     * @param  NA
     * @return void
     */
    public void createFloatingList()
    {
        lst_FloatId = new list<string>();
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink1);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink2);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink3);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink4);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink5);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink6);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink7);
        
        iLoggedInattachmentCount = 0;
        for(Attachment_Log__c objAttLog : caseRec.Attachment_Logs__r)
        {
            if(objAttLog.Attachment_Type__c != CaseDetailConstants_HUM.CLAIMS_MANUAL_ENTRY)
            {
                iLoggedInattachmentCount = iLoggedInattachmentCount+1;
            }
        }

        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink8+' ['+String.valueOf(iLoggedInattachmentCount) + ']');
        
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink9);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink10);
        if(String.isNotBlank(caseRec.Origin) && caseRec.Origin.equalsIgnoreCase(CaseDetailConstants_HUM.TRR_ORIGIN))
        {
            lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink22);
        }
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink11);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink12);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink13);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink14);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink15);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink16);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink17);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink18);
        lst_FloatId.add(CaseDetailConstants_HUM.sfloatLink19);
    }

    /*
     * This method Is to filter the floating links which are not relevant to the record type/Page. 
     * @param  string
     * @return void
     */
    public void removeFloaters(String recType)
    {
        List<integer> lstLinkIndices = new list<integer>();
        if(recType.containsIgnoreCase(CaseDetailConstants_HUM.sAgent) || recType.containsIgnoreCase(CaseDetailConstants_HUM.sGroup))
        {          
            lstLinkIndices = new list<integer>{2,4,5,9,14};
        }
        if(recType.containsIgnoreCase(CaseDetailConstants_HUM.sUnknown))
        {
            lstLinkIndices = new list<integer>{2,5,14,18}; 
        }
        if(recType.containsIgnoreCase(CaseDetailConstants_HUM.sProvider))
        {
            lstLinkIndices = new list<integer>{2,5,9,14,18};        
        }
        if(recType.containsIgnoreCase(CaseDetailConstants_HUM.sMember) || recType.containsIgnoreCase(CaseDetailConstants_HUM.sPharmacy))
        {
            lstLinkIndices = new list<integer>{2};          
        }
        if(recType.containsIgnoreCase(CaseDetailConstants_HUM.sMedicare))
        {
            lstLinkIndices = new list<integer>{5,8,14,18};          
        } 
        if (!isArticleVisible && (sUserProfile != Label.HUMAgencyCCSupervisor))
        {
            Set<integer> setFloaters = new set<integer>();
            setFloaters.addAll(lstLinkIndices);
            setFloaters.add(14);
            lstLinkIndices.clear();
            lstLinkIndices.addAll(setFloaters);
            lstLinkIndices.sort();
        }
        if(lstLinkIndices != null && !lstLinkIndices.isEmpty())    
        {
            for(integer i = lstLinkIndices.size()-1; i>=0; i--)
            {
                if(lstLinkIndices[i]< lst_FloatId.size())
                {
                    lst_FloatId.remove(lstLinkIndices[i]);
                }
            }
        } 
    }

     /*
    * This method Is to redirect the user to the correct VF page 
    * @param  NA
    * @return PageReference
    */
    public pagereference redirectToRecType()
    {   
        PageReference pg = null;   
        sCaseComments = ApexPages.currentPage().getParameters().get('caseComment');  
        sinteractionId = ApexPages.currentPage().getParameters().get('interactionId');    
        string sCloneOGOCaseId = ApexPages.currentPage().getParameters().get('CloneCaseId');
        List<PermissionSetAssignment> lstPSA = [Select id from PermissionSetAssignment where PermissionSetId IN (Select id from PermissionSet where name=: CaseDetailConstants_HUM.pSetName) and AssigneeId =: UserInfo.getUserId()];
        if(!lstPSA.isEmpty()&&lstPSA.size() > 0)
        {
              pg = New Pagereference('/'+caseRec.id+'?nooverride=1'); //for RPA users                     
        }
        else
        {
            pg = New Pagereference(CaseDetailConstants_HUM.surl + mapRecordTypePages.get(caseRec.recordtype.name) + CaseDetailConstants_HUM.sparam + caseRec.id);
            if(sCaseComments != NULL && sCaseComments !='') pg.getParameters().put('caseComment', string.escapeSingleQuotes(sCaseComments));
            if(sinteractionId != NULL && sinteractionId !='') pg.getParameters().put('interactionId', string.escapeSingleQuotes(sinteractionId));          
            if(sCloneOGOCaseId != NULL && sCloneOGOCaseId !='') pg.getParameters().put('CloneCaseId', sCloneOGOCaseId);
            
            if(this.bSwitch_3010498  && UtilityHelper_H_AHT_HUM.isRapidForceUser()){
                String sMoveTo = ApexPages.currentPage().getParameters().get('moveto');
                if(String.isNotBlank(sMoveTo)) pg.getParameters().put('moveto', sMoveTo);
            }            
        }
        pg.setredirect(true);
        return pg;
    }
    
    /*
    * This method is used when user clicks on the Edit Button on the Case Detail VF page 
    * @param  NA
    * @return PageReference
    */
    public pagereference referEdit()
    {
        If (caseRec.recordtype.name.containsIgnoreCase('Close')) {
            Pagereference pg = Page.CaseClosed_Edit_VF_HUM;
            pg.getParameters().put('id',caseRec.id);
            pg.getParameters().put('buttonName',Apexpages.currentPage().getParameters().get('reopenAndEditParam'));
            pg.setredirect(true);
            Return pg;
        }
        Pagereference pg = page.HUMCaseEditButtonOverride;
        pg.getParameters().put('id',caseRec.id);
        pg.getParameters().put('isATTVF',Apexpages.currentPage().getParameters().get('ATTVFParam'));
        pg.getParameters().put('isExp',Apexpages.currentPage().getParameters().get('ExpParam'));
        pg.getParameters().put('isPart',Apexpages.currentPage().getParameters().get('PartParam'));
        pg.getParameters().put('isPCP',Apexpages.currentPage().getParameters().get('PCPParam'));
        pg.getParameters().put('isGoodCause',Apexpages.currentPage().getParameters().get('GoodCauseParam'));
        pg.getParameters().put('isQAA',Apexpages.currentPage().getParameters().get('QAAParam'));
        pg.getParameters().put('isMedicaidPARProvider',Apexpages.currentPage().getParameters().get('MedicaidPARProviderParam'));
	pg.getParameters().put('isEnrollmentWelcomeCall',Apexpages.currentPage().getParameters().get('EnrollmentWelcomeCallParam'));    
        pg.setredirect(true);
        return pg;
    }
    
    /*
    * This method is used when user clicks on the Change case Owner Button on the Case Detail VF page 
    * @param  NA
    * @return PageReference
    */
    public pagereference changeCaseOwner()
    {
        Pagereference pg = Page.HUMChangeCaseOwner;
        pg.getParameters().put('id',caseRec.id);
        pg.setredirect(true);
        return pg;
    }
    
    public pagereference UnKnownToKnownMember()
    {
        Pagereference pg = Page.UnKnownToKnownMember_VF_HUM;
        pg.getParameters().put('id',caseRec.id);
        pg.setredirect(true);
        return pg;
    }

    /* 
    * This method is used when user clicks on the Action Button on the Case Detail VF page 
    * @param  NA
    * @return PageReference
    */
    public pagereference caseRecmdAction()
    {
        Pagereference pg = Page.CaseRecommendationAction_VF_HUM;
        pg.getParameters().put('id',caseRec.id);
        pg.setredirect(true);
        return pg;
    }
    
    /*
    * This method is used when user clicks on the Case Close Button on the Case Detail VF page 
    * @param  NA
    * @return PageReference
    */
    public pagereference closeCase()
    {
        Pagereference pg = Page.HUMCaseCloseErrorPage;
        pg.getParameters().put('id',caseRec.id);
        pg.setredirect(true);
        return pg;
    }

    /*
    * This method is used disbale the change case owner button for creditable coverage criteria
    * @param  NA
    * @return PageReference
    */   
    private void disableChangeOwnerCreditableCoverage()
    {       
       /*
       Disable Change Case owner,Edit, Close Case Button when
           a)Guided Process flag is True
           b)Case Owner is "Louisville RSO Enrollment"
           c) Attestation Form is attached
       */      
       Boolean bcreditableCoverageDisableBtn = (isCreditableCoverageProcesstAttached && caseRec.Case_Owner__c  == CaseDetailConstants_HUM.CASE_RSO_ENROLLMENT_OWNER && caseRec.Guided_Process_Flag__c == true && caseRec.status == CaseDetailConstants_HUM.CASE_STATUS_PENDING_RESPONSE) ? true : false;

       if(bcreditableCoverageDisableBtn) {

            bDisableCloseCaseBtn = true;
            bDisableEditCaseBtn = true;
            bDisableTransferCaseBtn = true;
       }
    } 
    
    /*
     * Method Name   :    getVerifyAccountType
     * Description   :    Send the boolean value to display the ButtonSection on vf page 
     * Return Type   :    boolean
     * Parameters    :    NA
     */
     public Boolean getVerifyAccountType()
     {
         try
            {
                 if(caseRec.AccountId != Null)
                 {
                    if(caseRec.Account.RecordType.Name == CaseDetailConstants_HUM.INTERACTINGWITHTYPE_MEMBER || caseRec.Account.RecordType.Name == CaseDetailConstants_HUM.CASEEDIT_UNKNOWN_MEMBER_HUM || caseRec.Account.RecordType.Name == CaseDetailConstants_HUM.MEMBERCAREGIVER)
                    {
                        isDisplay = true;
                    }
                    else
                    {
                        isDisplay = false;
                    }
                 }
           }
         catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CaseEdit_CE_HUM', 'getVerifyAccountType');
        }
         return  isDisplay;
     }
     
    /*
    * This method Is to disable fields and buttons for medicare appeal process
    * @param  NA
    * @return void
    */
    public void isMedExpProcessAttachedExp()
    {     
        integer iCount = 0;
        For(GroupMember objGrpMem : lstGrpMems)
        {
            if(objGrpMem.Group.Name == 'LV GA and Correspondence Screening PGQ' || objGrpMem.Group.Name == 'LV GA and Correspondence Screening E PGV')
            {
                iCount = iCount + 1;
            }
        }
        if(iCount == 2)
        {
            bQueueUser = false;
        }
        else
        {
            bQueueUser = true;
        }
        boolean bFlag = false;
        If (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == 'LV G and A and Correspondence Screening'){
            bFlag = True;
        }
        boolean bMedExpProcessAttachedBtn = (isMedExpProcessAttached && bQueueUser && caseRec.status != 'Closed' && bFlag) ? true : false; 

        if(bMedExpProcessAttachedBtn) {
            bDisableCloseCaseBtn = true;
            bDisableEditCaseBtn = true;
            bDisableTransferCaseBtn = true;
       }           
    } 
    /*
    * This method Is to disable fields and buttons for QAA Proces
    * @param  NA
    * @return void
    */
    public void isQAAProcessAttached()
    {    
        String pgqName ='';
        if (null != caseRec.Policy_Member__r.Segment_Indicator__c && CaseAutoRouteConstant_C_HUM.LST_MEDICAREPLANS.contains(caseRec.Policy_Member__r.Segment_Indicator__c)) {
            pgqName=CaseAutoRouteConstant_C_HUM.QAAGroupPlanValue.get('Medicare');
        }else if(null != caseRec.Policy_Member__r.Issue_State__c && CaseAutoRouteConstant_C_HUM.LST_MEDICAID_STATES.contains(caseRec.Policy_Member__r.Issue_State__c)){
            pgqName=CaseAutoRouteConstant_C_HUM.QAAGroupPlanValue.get(caseRec.Policy_Member__r.Issue_State__c);
        }
        bQueueUser = true;
        For(GroupMember objGrpMem : lstGrpMems)
        {
            if(objGrpMem.Group.Name == pgqName)
            {
                bQueueUser = false;
                break;
            }
        } 
        boolean bFlag = false;
        If (caseRec.AutoRoute_Status__c == 'Completed' &&  CaseAutoRouteConstant_C_HUM.QAAQueues.contains(caseRec.Owner_Queue__c)){
            bFlag = True;
        }
        
        boolean bQAAProcessDisableBtn = (checkProcess(caseRec.id) && bQueueUser && bFlag && caseRec.AutoRoute_Status__c == 'Completed' && caseRec.status != 'Closed' && UtilityHelper_H_AHT_HUM.isQAAAutoRoute(caseRec)) ? true : false; 

        if(bQAAProcessDisableBtn) {
            bDisableCloseCaseBtn = true;
            bDisableTransferCaseBtn = true;
       }

        
    } 
    
    
    /*
    * This method Is to disable fields and buttons for Medicare Part-D process
    * @param  NA
    * @return void
    */
    public void isMedExpProcessAttachedPartD()
    {       
        integer iCount = 0;
        For(GroupMember objGrpMem : lstGrpMems)
        {
            if(objGrpMem.Group.Name == 'LV GA and Correspondence Screening PGQ' || objGrpMem.Group.Name == 'LV GA and Correspondence Screening P PGV')
            {
                iCount = iCount + 1;
            }
        }
        if(iCount == 2)
        {
            bQueueUser = false;
        }
        else
        {
            bQueueUser = true;
        }
        boolean bFlag = false;
        If (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == 'LV G and A and Correspondence Screening'){
            bFlag = True;
        }

        boolean bMedExpProcessAttachedPartDisableBtn = (isMedExpProcessAttachedPart && bQueueUser  && caseRec.status != 'Closed' && bFlag) ? true : false;                            

        if(bMedExpProcessAttachedPartDisableBtn) {
            bDisableCloseCaseBtn = true;
            bDisableEditCaseBtn = true;
            bDisableTransferCaseBtn = true;
       }
    }   
    /*
    * This method Is to disable fields and buttons for medicare appeal process
    * @param  NA
    * @return void
    */
    public void isMedExpProcessAttachedGoodCause()
    {     
        bQueueUser = true;
        For(GroupMember objGrpMem : lstGrpMems)
        {
            if(objGrpMem.Group.Name == CaseDetailConstants_HUM.GOODCAUSEGROUPNAME)
            {
                bQueueUser = false;
                break;
            }
        }
        boolean bFlag = false;
        If (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == CaseDetailConstants_HUM.GOODCAUSEQUEUE){
            bFlag = True;
        }
        if(isGoodCauseProcessAttached){
            boolean bGoodCauseProcessAttachedDisableBtn = (bQueueUser && caseRec.status != 'Closed' && bFlag);

            if(bGoodCauseProcessAttachedDisableBtn) {
                bDisableCloseCaseBtn = true;
                bDisableEditCaseBtn = true;
                bDisableTransferCaseBtn = true;
           }
        }
    }   
    /*
    * This method Is to check PCP Template is attached
    * @param  NA
    * @return void
    */
    public void isPCPProcessAttached()
    {   
        bQueueUser = true;
        For(GroupMember objGrpMem : lstGrpMems)
        {
            if(objGrpMem.Group.Name == CaseDetailConstants_HUM.PCPGROUPNAME)
            {
                bQueueUser = false;
                break;
            }
        }
        boolean bFlag = false;
        If (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == 'PCP Assignment Research'){
            bFlag = True;
        }
        if(isPCPProcessAttached){
            boolean bPCPProdcessDisableBtn = (bQueueUser && caseRec.status != 'Closed' && bFlag);

            if(bPCPProdcessDisableBtn) {
                bDisableCloseCaseBtn = true;
                bDisableEditCaseBtn = true;
                bDisableTransferCaseBtn = true;
           }
            
            
        }
   }  

   /*
    * This method Is to check "Medicaid PAR Provider Not Accepting" template  is attached
    * @param  NA
    * @return void
    */
    public void isMedicaidPARProviderProcessAttached()
    {   
        String sGroupName='';
        List<Template_Submission_Data__c> lstStateFlowValue = UtilityHelper_H_AHT_HUM.getTemplateData(CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_UNIQUEID,CaseAutoRouteConstant_C_HUM.LST_MEDICAIDPAR_FIELDS,caseRec);
        UtilityHelper_H_AHT_HUM.TemplateSubmissionData oTempSubData = UtilityHelper_H_AHT_HUM.getTemplateSubmissionWrapper(lstStateFlowValue);
        if(oTempSubData!=null){
            if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_KY){ 
                if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
                    sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP;
                }   
                else sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP_KY;
            }
			else if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_IL_LTSS){
                    sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP_IL;
			}					
            else sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP;
        }
        bQueueUser = true;
        For(GroupMember objGrpMem : lstGrpMems)
        {
            if(objGrpMem.Group.Name == sGroupName)
            {
                bQueueUser = false;
                break;
            }
        }
        boolean bFlag = false;

        if (caseRec.AutoRoute_Status__c == 'Completed' && (caseRec.Owner_Queue__c == CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE || caseRec.Owner_Queue__c == CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE_KY  || caseRec.Owner_Queue__c == CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE_IL)){
            bFlag = true;
        }
        if(isMedicaidPARProviderProcessAttached && bQueueUser && caseRec.status != 'Closed' && bFlag){
            bDisableCloseCaseBtn = true;
            bDisableEditCaseBtn = true;
            bDisableTransferCaseBtn = true;
        }
   }  

   /*
    * This method Is to check Invoice Request Template is attached
    * @param  NA
    * @return void
    */
    public void isInvoiceRequestAttached()
    {   
        boolean bFlag = false;
        //Uncomment when auto transfer is fixed if (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == 'Humana Pharmacy Calls'){ 
            bFlag = True;
        //}
        if(isInvoiceRequestAttached){
            bIsInvoiceRequestAttached = (caseRec.status != 'Closed' && bFlag); 
        }
   }    
    /**  
    * This method is to check whether edit and clone buttons should be disabled.
    *
    * @return   Boolean
    */
    public Boolean verifyLegacyDelete()
    {
       bdisableButton = false;
       blegacyDeleteCheck = false;

       if (caseRec.AccountId != Null)
        {
            if(caseRec.Account.ETL_Record_Deleted__c == true)
            {
                blegacyDeleteCheck = true;
            }
            else if (caseRec.Policy_Member__c != Null)
            {
                if(caseRec.Policy_Member__r.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
            }
        }
        if(blegacyDeleteCheck && caseRec.Status=='Closed') bdisableButton = true;
        return bdisableButton;
     } 
    
     /*   
     * Method Name: fetchUserCompassDetails
     * Description: This method is used for fetching User's Mentor Function and SubFunction
     * @param  NA
     * @return NA 
     */
    private void fetchUserCompassDetails()
    {
        sFunction = HUMUtilityHelper.fetchUserMentorFunction(currentUser);
        if(!String.isBlank(currentUser.Mentor_Sub_Function__c))
            sSubFunction = currentUser.Mentor_Sub_Function__c;                    
    }   
    
    /**
    * Method is used to identify if the case is OGOoriginal case or OGOCloned case
    * @param String - Case ID
    * @return   NA
    */
    @testVisible private void checkOGORelation(String sCaseID) {
        this.bIsOriginal = false;
        this.bIsOGOLinked = false;
        List<Case_Junction__c> lstCaseJunction =[select Case_Master__c, Case__c, IsCloneCase__c from Case_Junction__c where (Case_Master__c=:sCaseId OR Case__c=:sCaseId) AND IsCloneCase__c = true limit 1];
        if(!lstCaseJunction.isEmpty()) {
            this.bIsOGOLinked = true;
            String sCJCaseMaster = lstCaseJunction.get(0).Case_Master__c;
            if(sCaseID.equalsIgnoreCase(sCJCaseMaster)) {
                this.bIsOriginal = true;
            }            
        }
    }
    
    /**
    * Disable button on Case if transfer to OGO department and user has CRMS_400_Grievance_Appeals
    * @param String - sCaseWQ
    * return Boolean
    */
    public Boolean disableButtonOGOCase(String sCaseWQ) {
        Boolean bDisableButtonOGO = false;
        try {
            Boolean bHasPermission400GA = checkUserPermission(CaseDetailConstants_HUM.Grievance_Appeals_Permissionset_Name);
            
            if(!bHasPermission400GA && String.isNotBlank(sCaseWQ) && sCaseWQ.equalsIgnoreCase(CaseDetailConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT)) {
                bDisableButtonOGO = true;
            }
        } catch(Exception ex) {
            bDisableButtonOGO = false;
        }
        return bDisableButtonOGO;
    }

    /**
    * Method is used to identify if the logged in user is having OGO Scrubber and OGO Permission Set. 
    * <p>
    * @return   boolean
    */
    public boolean isOGOScrubberUser()
    {
        Boolean bOGOScrubberAccess = false;
        if(checkUserPermission('CRMS_401_OGO_Scrubber_Access') && checkUserPermission('CRMS_400_Grievance_Appeals'))
        {
            bOGOScrubberAccess = true;
        }
        return bOGOScrubberAccess;
    }
    
    /*
    * This method checks for QAA process attached to the case record
    * @param   caserecord id
    * @return  true Or false
    */
    public boolean checkProcess(id CaseId){
        Boolean bSuccess = false;
      
            List<Case_Action_Association__c> lstCaseActionAssociation = [SELECT Id, Name, Case__r.CaseNUmber, Case_Action__r.name, Action__c FROM Case_Action_Association__c where Case_Action__r.Name = 'QAA' and Case__c = :CaseId];
            bSuccess =  (!lstCaseActionAssociation.IsEmpty())? true : false ;
        
        return bSuccess;
    }		 
	
	/**
    * Method is used to the message JSON for the Coveo Unified Search method
    * @param    None
    * @return   NA
    */
    @testVisible
    private string createCoveoMessage()
    {
        String sMsgJSON = null;
        list<Case_Interaction__c> lstCaseInt = [SELECT Id, Case__c, Interaction__c, Interaction__r.Name FROM Case_Interaction__c WHERE Case__c = :caseRec.Id];
        if(caseRec!=null)
        {
            UnifiedSearch_Case_DTO_HUM.InteractionDetails_DTO oInt = new UnifiedSearch_Case_DTO_HUM.InteractionDetails_DTO();
            oInt.intWithType = String.isNotBlank(caseRec.Interacting_With_Type__c) ? caseRec.Interacting_With_Type__c : '';
            oInt.intWith = String.isNotBlank(caseRec.Interacting_With__r.name) ? caseRec.Interacting_With__r.name : '';
            oInt.CaseNumber = String.isNotBlank(caseRec.casenumber) ? caseRec.casenumber : '';

            UnifiedSearch_Case_DTO_HUM.Context_DTO oContext  = new UnifiedSearch_Case_DTO_HUM.Context_DTO();
            oContext.MajorLOB = String.isNotBlank(caseRec.Policy_Member__r.Product_Type__c) ? caseRec.Policy_Member__r.Product_Type__c : '';
            oContext.MinorLOB = String.isNotBlank(caseRec.Policy_Member__r.Product__c) ? caseRec.Policy_Member__r.Product__c : '';
            oContext.IntrtWithType = String.isNotBlank(caseRec.Interacting_With_Type__c) ? caseRec.Interacting_With_Type__c : '';
            oContext.IntrtAbtType = String.isNotBlank(caseRec.Interacting_About_Type__c) ? caseRec.Interacting_About_Type__c : '';
            oContext.Market = String.isNotBlank(caseRec.Policy_Member__r.Policy__r.Source_Market__c) ? caseRec.Policy_Member__r.Policy__r.Source_Market__c : '';
            oContext.CRMPageName = COVEO_CRMPAGENAME;
            oContext.CaseNumber = String.isNotBlank(caseRec.casenumber) ? caseRec.casenumber : '';
            oContext.IntrNumber = lstCaseInt!=null && !lstCaseInt.isEmpty() && String.isNotBlank(lstCaseInt[0].Interaction__r.Name) ? lstCaseInt[0].Interaction__r.Name: '';            

            UnifiedSearch_Case_DTO_HUM oWrap = new UnifiedSearch_Case_DTO_HUM();
            oWrap.searchParams = '';
            oWrap.coveoTabName = String.isNotBlank(caseRec.Account.Name) ? caseRec.Account.Name : '';
            oWrap.InteractionDetails = oInt;
            oWrap.context = oContext;
            sMsgJSON = JSON.serialize(oWrap);
        }        
        return sMsgJSON;
    }
    
    /* Method Name   :    closePopUp
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
            HUMExceptionHelper.logErrors(e,'PolicyMemberDetail_CE_HUM','closePopup');
        }
    }

    /* Method Name  :    saveAndContPopup
    * Description   :    Open the pop up on interaction
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public PageReference saveAndContPopup() { 
        bPopUp = true;    
        return null;
   } 

    /*
     * This method is used to check if Esclation indicator and esclation date need to be show on UI
     * @param  NA
     * @return void
     */
    public void showEsclIndicatorAndDate(){
        bShowEsclIndicatorDate = false;
        List<String> lstInteractingAboutType = new List<String>{'Agent', 'Group', 'Member', 'Provider', 'Unknown-Agent', 'Unknown-Group', 'Unknown-Member', 'Unknown-Provider'};
        List<String> lstInteractionOrigin = new List<String>{'Service Inquiry', 'NINA Web Chat', 'Internal Process', 'Watson Voice', 'Watson Web Chat','Vantage', 'Cancelled'};
        if(lstInteractingAboutType.contains(caseRec.Interacting_About_Type__c) && !lstInteractionOrigin.contains(caseRec.Origin)){
            bShowEsclIndicator = true;
            if(caseRec.Origin == 'IVR' && caseRec.Classification_Type__c != 'Calls (RSO)'){
                bShowEsclIndicator = false;
            }  
        }
        if(bShowEsclIndicator){
            bShowEsclIndicatorDate =  String.isNotBlank(caseRec.Escalation_Indicator_Date__c) ? true : false;
        }
    }
	 
}