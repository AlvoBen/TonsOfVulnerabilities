/*****************************************************************************************
Apex class Name    : HUMChangeCaseOwnerController
Version            : 1.0
Function           : Controller to VF Page:: HumChangeCaseOwner.
                    This class is used to drive the custom functionality of change owner on Cases.
Created Date       : 22 May 2014
Test Class         : HUMChangeCaseOwnerControllerTest

    Modification Log
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------                
 *   Sagar Tapadia                17093               22/05/2014          Original Version
 *   SuryaKumari Medicherla       17093               05/06/2014          Added saveTaskOrCase method to implement save functionality.
 *   Mrudula Jaddu                17093               07/02/2014          Changed Service center and department Functionality
 *   Sagar Tapadia                17093               08/11/2014          Added logic for QMS Referral
 *   SuryaKumari  Medicherla      17559               08/26/2014          Added logic for cases without interaction with and interaction with name 
 *   SuryaKumari  Medicherla      18230               09/29/2014          Added logic to change OwnerId field value while case transformation.Defect:156158
 *   Mrudula Jaddu                19256               11/14/2014          Added logic to show error message for non existing Queues 
 *   Mrudula Jaddu                19591               11/27/2014          REQ -72764. Classification and Intent are mandatory for case transfer.
 *   SuryaKumari Medicherla       19911               12/10/2014          Code modified to replace custom settings with custom object (REQ-72809)
 *   SuryaKumari Medicherla       20149               31/12/2014          Code added to for REQ-95898
 *   SuryaKumari Medicherla       20161               01/01/2015          Code modified to remove duplicate topic values
 *   SuryaKumari Medicherla       20226               07/01/2015          Code modified to fix defect:167546                 
 *   SuryaKumari Medicherla       20393               19/01/2015          Code modified to fix defect:168838  
 *   SuryaKumari Medicherla       20425               21/01/2015          Added method populateClassificationName to fix defect:168836              
 *   SuryaKumari Medicherla       20518               26/01/2015          Code modified to fix defect:169292
 *   SuryaKumari Medicherla       20574               28/01/2015          removed method populateClassificationName to fix defect:168836
 *   SuryaKumari Medicherla       20634               29/01/2015          Code added as per REQ-95898 change for defect:170485(displaying G&A and Texas Letter sections as per Edit Page).
 *   SuryaKumari Medicherla       20855               11/02/2015          Code modified to fix defect:169682.0001 
 *   SuryaKumari Medicherla       21223               02/26/2015          Code modified to fix Issue number 146 and business priority 0(not able to get users part of queues even after selecting service center and department)
 *   SuryaKumari Medicherla       21614               18/03/2015          Alphabetical Order issue fix(Issue Number 41)
 *   Praveen Kumar Parimi                             06/22/2015          Added call to Decryption for decrypting the values and call the QMS Service
 *   Praveen Kumar Parimi         24290               06/25/2015          Modified the location of isRunningTest to fix the code coverage.
 *   Praveen Kumar Parimi         26451               09/16/2015          Code Modified to add logic and Utility callout for Case Comments Encryption.
 *   Praveen Kumar Parimi         26451               09/29/2015          Code Modified to Change the access modifiers of the methods. 
 *   Praveen Kumar Parimi         26451               10/05/2015          Code Modified as per the latest coding standards.
 *   Vishal Verma                 30508               03/15/2016          Defect 216758.0001 : Passing the Comment entered from UI while transferring the Case to QMS .
 *   Vishal Verma                 30548               03/17/2016          Defect 216758.0001 :Decrypting the Case Comments entered from UI before passing it to QMS.
 *   Shreya Choodamani            26132               04/27/2016          Added boolean variable for RSO Medicare logic for rendering Reminder message to left of Comments (REQ - 241737) 
 *   Shikhar Mehta                30033               06/21/2016          REQ - 237000 : EIP fix , removed dpass calls for decryption.
 *   Vishal Verma                 33088               07/18/2016          Defect 233692.0001: Time stamp issue , Changed System.now to String.valueof(System.now) to show date in User's Timezone
 *   Prachi Bhasin                33337               07/18/2016          Changes made as part of REQ- 270349
 *   Shikhar Mehta                38512               08/23/2016          REQ - 95982 
 *   Shikhar Mehta                38512               08/31/2016          Defect# 239494 and Defect # 239817
 *   Shikhar Mehta                38512               09/07/2016          Defect# 240351
 *   Shikhar Mehta                38512               09/13/2016          Rolled back the changes to show the inactive users in the user picklist
 *   Kritika Popat                                    09/16/2016          Defect related to case owner
 *   Shikhar Mehta                                    09/22/2016          Req - 202270
 *   Sharan Shanmugam                                 09/26/2016          Removed None from Topic dropdown as part of REQ - 02442
 *   Harika Devi Kalidindi        42104               10/04/2016          REQ - 283105 : showing only Active users names on case/task transfer in a drop-down
 *   Vishal Verma                 44327               10/25/2016          Defect# 245402
 *   Shikhar Mehta                                    10/26/2016          REQ - 290098 MF3 RollBack
 *   Samridh Manucha                                  11/03/2016          Defect# 247086.0001- Populating value for sPublicGroupName Variable 
 *   Harshith Mandya                                  11/07/2016          Made changes wrt WQ enhancement
 *   Vamsi Kondragunta            46929               11/08/2016          Defect fix for #247086.0001
 *   Vamsi Kondragunta            47288               11/09/2016          Defect fix for #247086.0001
 *   Vishal Verma                 49536               11/28/2016          REQ - 289407 : CA Ticket - 6242901 : Show error message when the Classification and/or intent are not populated on Case.
 *   Praveen Kumar Parimi         50918               12/06/2016          Changes made as part of CA 6412051 aka REQ 290998. 
 *   Vamsi Kondragunta            59634               02/01/2017          Changes for Expected defect..REQ - 296903
 *   Harshith Mandya                                  02/21/2017          REQ - 301510 : CA Ticket - Fixing the OWner Queue, Service Center and Department Mismatch
 *   Santhi Mandava               71723               01/03/2017          Updated method "createQMSRequestBody" to include provide address,state,city and zipcode
 *   Santhi Mandava               74574               03/09/2017          Updated method "createQMSRequestBody" to include provide DBA
 *   Harshith Mandya                                  03/16/2017          Defect Fix # 262925.0001 - User Not Found. Owner Queue was blank.
 *   Praveen Kumar Parimi         78292               03/22/2017          Code fix for REQ - 304963 to encrypt QMS Case Comments.
 *   Ranjeeth Nagishetty                              03/28/2017          REQ - 306318:SF - TECH - Change Case Owner :Changing into CTCI data model
 *   Sumit Sharma                 89120               05/03/2017          Modified codes for REQ - 307653
 *   Shreya Choodamani            90487               04/28/2017          REQ#: 311397 Updated the code to mimic the behavior of CCSpecilist for HP Specialist
 *   Rajesh Keswani               102229              06/14/2017          REQ - 318371 - MF 15-- Case Comments-- ADD-- 'Case Edit' & 'Case Transfer' conditional requirements
 *   Pallavi R                    110756              07/04/2017          REQ - 322573 - changed logic for Medicare case commnt entry  
 *   Vishal Verma                 114200              07/27/2017          REQ - 323542 : Exception handling   
 *   Pallavi R                    116812              08/04/2017          REQ - 322573 - Updated code as per requirement updates for validations
 *   Vishal Verma                                     08/09/2017          REQ - 324819: CMO CR 5257 dynamic dropdown for log code
 *   Sharan Shanmugam                                 08/09/2017          REQ - 324814 : Save Case Comment to CRM during ePost failure
 *   Santhi Mandava                                   03/16/2018          REQ - 356030 : Removed case comments ciphercloud encryption.
 *   Ananya Biswas                259008              07/18/2018          REQ - 367014 : Log code story
 *   Anurag Shah                  267567              07/20/2018          REQ -  : Added a logic to determine page section according to PDP Pilot access and Record Type
 *   Vijaya Koriprolu             268583           	  08/13/2018           Implemented REQ - 366118 - ability to deactivate a topic name even when there are open cases associated with the topic. 
 *   Dinesh Subramaniyan 				  			  11/14/2018		 SF - TECH - Convert QMSReferralService to Async                                                                                                                                                                   
 *   Prudhvi Pamarthi                                 11/29/2018          REQ - 378899 Humana Pharmacy Specialist - should not have the ability to transfer a case assigned to another user
 *	 Dinesh Subramaniyan							  02/18/2019			REQ - 386017 - SF - TECH- Incident 8034560
*	 Ajay Chakradhar							  	  03/12/2019			REQ - 378712 Case Transfer Service 
*   Rakesh Patni				  344548			  03/15/2019			Defect-407269.0001
 *	 Ajay Chakradhar							  	  04/01/2019			Defect-408918.0001
*	Dinesh Subramaniyan								  04/16/2019			REQ - 396407 - Incident 8193169 - Prod CRM - Warm case transfer Is Not working
*	Santhosh Ganji   								  04/22/2019			REQ - 379035 - MF 8-- Change Queue Owner page: limit the insurance WQV selections for HP profile
*   Vinay Lingegowda                                  07/04/2019            REQ - 403653 AHT - All Users - Ability to change Case Priority on the Change Case Owner page (RF)
*   Manikanta				      40875               08/21/2019            REQ - 396234 - Ability for CRMS to systematically display the HP case record type and Case Comment section
*   Harish Gowda				                      01/29/2019            USER STORY 729540 -PR00091574 - MF 1 - Quality - Reassign case to Sender (RF).
*   Harish Gowda				                      01/29/2019            USER STORY 729532 -PR00091574 - MF 1 - Quality - Reassign case to creator (RF).
*   Sunil Kumar Reddy Y				      			  06/24/2020            User Story 1200920: PR00091574 - MF 1 - AHT - Ability to auto-populate Service Center and Department on Case Transfer page (RF)
*   Sunil Kumar Reddy Y				      			  06/24/2020            User Story 1276136: PR00091574 - MF 1 - AHT - Ability to auto-populate Service Center and Department on Case Transfer page (CRM)
*   Subhamay Mandal				                  	  07/20/2020            User Story 1267788, 1268549
*   Amar Gadewar                                      08/10/2020            DF-1321 Bug 1370084: User Story: 1276136 - Service center and Department fields are auto populated when Classification Type is changed on case details screen upon case transfer.
*   Sunil Kumar Reddy Y				      			  08/11/2020            DF-1323 Fix
*   Suresh Kokkeragadda                               07/29/2020            User Story 1317726: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 7/30 , 8/14 and 8/28 releases (CRM)
*   Subhamay Mandal				                      09/04/2020            User Story 1421130: PR00091574- IR 5050-Alert agent on permission issue while re-assigning case to creator/sender (RF)
*   Subhamay Mandal				                      09/17/2020            User Story 1421130: Testfull Observation.
*   Subhamay Mandal				                      09/21/2020            User Story 1421130: Testfull Observation 1.
*   Subhamay Mandal                                   10/12/2020            User Story 1540279: PR00091574-IR 5050 - pre-populate re-assign to sender/creator user based on work queue access (RF)
*   Vijaykumar										  10/19/2020			User Story 1442197: PR00091574 - IR 5050 - ability to capture clicks on re-assign to creator/sender (RF)
*   Kavya Bondada                                     11/18/2020            User Story 1641595: PR00091574 - MF 1 - Quality - Ability for Rep to select "Was the Grievance Resolved during the call?" on Quick Start/Case Page (RF)
*   Samantha Tennyson                                 11/20/2020            User Story 1641595: PR00091574 - MF 1 - Quality - Ability for Rep to select "Was the Grievance Resolved during the call?" on Quick Start/Case Page (RF)
*   Gourab Tewary                                     01/15/2021            USER STORY 1750379 PR00091574 - IR 5050 - Update/Fix previous tracking fields on case transfers within same departments (RF)
*   Kavya Bondada                                     01/20/2021            User Story 1745816: PR00091574 - MF 1 - Quality - Display guidance on Case Transfer page on selection of Topic (CRM)
*   Subhamay Mandal                                   01/25/2021            USER STORY 1750379 Defect Fix DF-2314
*   Vijaykumar                                        01/27/2021            USER STORY 1750377 PR00091574 -IR5050 - hashtags case transfers for no comments/ max char limit comment (RF)
*   Gourab Tewary                                     01/27/2021            USER STORY 1750379 Defect DF-2318 Fix
*   Vijaykumar                                        01/29/2021            USER STORY 1750377 Defect Fix DF-2335
*   Moshitha Gunasekaran                              02/08/2021            User Story 1083065: PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case
*   Gourab				                              02/12/2021 		    USER STORY 1745089PR00091574 - IR 5050 - Add a Transfer Reason in Case Transfer page (CRM)
*   Subhamay Mandal                                   02/17/2021            USER STORY 1745061 DF-2404 fix
*   Subhamay Mandal                                   02/22/2021            USER STORY 2005006 DF-2416 fix Return to Sender button click.
*   Atia Uzma                                         02/19/2021            User Story 1956960: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 11/20/2020 , 02/12/2021, 03/05/2021 and 3/19/2021 releases (CRM)
*   Subhamay Mandal                                   02/22/2021            USER STORY 2005006 DF-2416 added 3/5 fix.
*   Atia Uzma                                         03/01/2021            User Story 2040620: T1PRJ0001827 -MF 1 - Quality - Display guidance on Case Transfer page on selection of Service Center And Department (CRM)
*   Saikumar Boga                                     03/11/2021            US 1979274 CR18 - CRM to MHK Transfer for Unknown Members
*   Atia Uzma                                         05/05/2021            User Story 2230288: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 04/16/2021, 05/14/2021 and 05/28/2021 releases (CRM)
*   Kiran Bhuvanagiri								  05/07/2021			User Story 2173252: Add Log Code to Case Transfer Page for HSP Users (CRMS) 
*   Amar Gadewar                                      05/14/2021            User Story 2215231: T1PRJ0001827 - MF 1 - RF Clean Up - Was the Grievance Resolved during the Call? (RF)
*   Moshitha Gunasekaran							  05/20/2021			INC 0974251: Made changes to fix null exception issue
*   Ashok Kumar Nutalapati							  06/04/2021			User Story 2334476: T1PRJ0002517 - MF 5 - Switch Clean Up - Add Log Code to Case Transfer Page for HSP Users (CRMS) (ID# 142)
*   Atia Uzma                                         06/21/2021            User Story 2348383: T1PRJ0001827 -MF 1 - Case Transfer Assist - Business Configuration enhancements (CRM)
*   Prasuna Pattabhi                                  08/11/2021            US 2548413 sf-main/src/classes/HUMChangeCaseOwnerController.cls
*   Tummala Vijaya Lakshmi							  08/27/2021			USER STORY 2578061  -T1PRJ0002980/PR00095219 - MF 5 - Contracted Protected Data for Q/V App	
*   Gowthami Thota                                    09/15/2021              Case Management Checkmarx Security violations - Case Service
*   Prudhvi Pamarthi                                  09/21/2021            US#2422184 Decommission the CRM to QMS Code
*   Tummala Vijaya Lakshmi							  09/30/2021			USER STORY 2578061  -T1PRJ0002980/PR00095219 - MF 5 - Contracted Protected Data for Q/V App	-CPD switch
*   Jasmeen Shangari								  13/01/2022			USER STORY 3027976 - Fix Queue View issue for MHK Task Type cases
*   Kiran Kumar B                                     06/07/2022            USER STORY 2310106 T1PRJ0003805/T1PRJ0184373 - CR 008LS- ESS ID 305- Add an Escalation Indicator in CRM Service
*   Kiran Patil                                       06/12/2023            USER STORY 4285092: T1PRJ0891339 CRM to Macess routing rules for correspondence.
**************************************************************************************************************************************************************************************************************************************************************************/
public with sharing class HUMChangeCaseOwnerController
{
    private Set<string> setServiceCenter = new Set<string>();
    private Set<string> setDepartment = new Set<string>();
    private Integer iLimitValue = GLOBAL_CONSTANT_HUM.LIMITVALUE_HUM;
    Private Map<String, Work_Queue_Setup__c> mapWorkQueueSetup = New Map<String, Work_Queue_Setup__c>([Select Name,id,Classification_Type__r.Name,Classification_Type__c, CokY__c, Department__c, DST__c, General__c, HO_Dental__c, HO_Medical__c,Contract_Protected_Data__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId,Work_Queue_Guidance__c from Work_Queue_Setup__c 
                                                                                                       Where IsActive__c = TRUE limit :iLimitValue]);
    private Id recId = NULL;
    private Boolean bCase = false;
    private Boolean bGroupexists = false;
    private Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
    @TestVisible private Map<String,Work_Queue_Setup__c> mapServCenDeptWorkQueueSetup;
    private Set<String> setPharRecType = new Set<String>(GLOBAL_CONSTANT_HUM.Pharmacy_Record_Type_Name);
    private List<PermissionSetAssignment> lstRSOHPPermissionSet;  
    public MedicareCallsCaseComments_H_HUM oMedCalls = new MedicareCallsCaseComments_H_HUM();
    private list<string> lstLogCodes = null;
    public Boolean bPharmacyUserSupervisor {get; set;}
    public boolean bcaseTransferAssistInfo {get; set;}
    public Boolean bIsClear = false;
    public Boolean bDisableReturnToCreator{get; set;}
    public Boolean bNoAccessToCreator{get; set;}
    public Boolean bNoAccessToSender{get; set;}
    public String sNoReturnToCreatorSenderMessage{get; set;}

    public Id cid{get;set;}
    public String sHashTag{get; set;}
    public Boolean bDisplayGuidance {get; set;}
    public String sGuidance {get;set;}
    
    public Boolean bDisplayTopicGuidance {Get; Set;}
    Public String sTopicGuidance {Get;Set;}
    Public Boolean bswitch_US2578074 {Get; Set;}
    
    public string sCaseDCN {get;set;}
    public string sCaseDocType {get;set;}
    public boolean bNoMacessCase {get;set;}
	Public Boolean bSwitch_US4285092 {Get; Set;}
	
    /**
    * Constructor for HUMChangeCaseOwnerController
    *
    */
    public HUMChangeCaseOwnerController()
    {  
        bswitch_US2578074 = HUMUtilityHelper.isCRMFunctionalityON('2578074');
        this.bDisableReturnToCreator = False;
        this.bNoAccessToCreator = false;
        this.bNoAccessToSender = false;
        this.sNoReturnToCreatorSenderMessage = '';
        this.sHashTag ='';
        bDisplayGuidance = false;
        
        bDisplayTopicGuidance = false;
        bcaseTransferAssistInfo = false;
        bMedicareCalls = false;
        bCheckMedicareCCPermission = false;
        oCaseRec = new case();
        mapServCenDeptWorkQueueSetup = new Map<String,Work_Queue_Setup__c>();
        bShowTopic = true;
        bTopicMan = false;
        lstRSOHPPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name=:GLOBAL_CONSTANT_HUM.RSO_HP_PermissionSet_Name LIMIT 1];
        
        bSwitch_1414643 =  HUMUtilityHelper.isCRMFunctionalityON('1414643');
        bSwitch_US4285092 =  HUMUtilityHelper.isCRMFunctionalityON('4285092');
        bRSOHPFlag = false;
        prePopulateCaseDetails();
        oCaseRec.Transfer_Reason__c = GLOBAL_CONSTANT_HUM.TRANSFER_PER_PROCESS;
        checkRecordType();
        bVisible = true;
        bCIError = true;
        bPharmacyUserSupervisor = true;
        
        readyForePostCommit = false;
        sHiddenCaseCommentMemberNotFound = GLOBAL_CONSTANT_HUM.CASECOMMENT_MEMBERNOTFOUND_HUM;
        sHiddenCaseCommentFaultError = GLOBAL_CONSTANT_HUM.CASECOMMENT_FAULTERROR_HUM;
        checkCCMedicareCalls();  
        lstLogCodes = Pharmacy_H_HUM.fetchLogCode(oCaseRec.Classification_Type__c);
        bQMSFlag = false;

        bisQMSErrorExist = false;
        bNoQMSFlag = false;
        bCTransfer = false;
        hPharmacyUser = false;
        if(Pharmacy_H_HUM.isPharmacyUser() != GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM)  hPharmacyUser = true;  

        if(lstRSOHPPermissionSet!=null && !lstRSOHPPermissionSet.isEmpty())
        {
            if(setPharRecType.contains(oCaseRec.RecordType.name))
            {
                hPharmacyUser = true;
            }
        }
        bIsClear =  String.isNotBlank(ApexPages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.STRING_CLEAR)) ? true : false;

        if(!bIsClear
             && String.isNotBlank(oCaseRec.CTCI_List__c) && 
             String.isNotBlank(oCaseRec.Policy_Member__c) && 
             String.isNotBlank(oCaseRec.Policy_Member__r.Product__c) && 
             String.isNotBlank(oCaseRec.Policy_Member__r.Product_Type__c) &&
             String.isNotBlank(oCaseRec.Classification_Type__c) &&
             oCaseRec.Classification_Type__c.equals(oCaseRec.CTCI_List__r.Classification_Type__r.Name)
        ){
            checkCaseTransferAssist(oCaseRec);
        }

        this.bDisableReturnToCreator = isDisableReturnToCreator();
        determineReturnToCreatorSender(oCaseRec.Created_By_Queue__c, oCaseRec.Previous_Owner_Queue__c);
        bShowEsclIndicator = false;
        bShowEsclIndSwitch = HUMUtilityHelper.isCRMFunctionalityON('2310106');
        if(bShowEsclIndSwitch){
            showEsclIndicator();
        }
        checkForErrors();
		
		if(bSwitch_1414643 && string.isNotBlank(sCaseType) && sCaseType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MHK_CASE_TYPE)) 
        {
            sServiceCenter = GLOBAL_CONSTANT_HUM.Service_Center_Provider;
            sDepartment = GLOBAL_CONSTANT_HUM.Department_Correspondence_Task_MHK;
            if(string.isBlank(oCaseRec.G_A_Rights_Given__c) && bGAndASectionRender) 
            {
                oCaseRec.G_A_Rights_Given__c = GLOBAL_CONSTANT_HUM.NO_Complaint_GARights;
            }
            if (string.isBlank(oCaseRec.Complaint__c) && bGAndASectionRender)
            {
                oCaseRec.Complaint__c = GLOBAL_CONSTANT_HUM.NO_Complaint_GARights;
            }
        }		
    }
    public Boolean bCTransfer {get;set;}
    public Boolean bNoQMSFlag {get;set;}
    public String sOwnerName {get;set;}
    public String sCaseNumber {get;set;}
    public Id sOwnerId {get;set;}
    public String sServiceCenter {get;set;}
    public String sDepartment {get;set;}
    public String sTopic {get;set;}  
    public Boolean bShowTopic{get;set;}
    public Boolean bVisible {get;set;}
    public String sCaseComments{get;set;}
    public case oCaseRec{get;set;}
    public Boolean bGAndASectionRender{get;set;}
    public Boolean bTexasRender{get;set;}
    public Boolean bisErrorExist{get;set;}
    public Boolean bTopicMan{get;set;}
    public String sGrpQueueName{get;set;}
    public String sSelectedWorkQueue {get;set;}
    public String sPublicGroupName{get;set;}  
    public Boolean bIsRSOMedicarePolicy {get;set;}    
    public Boolean bCIError {get;set;} 
    public String sCode {get;set;}
    public String sCodeHidden {get;set;} 
    public Boolean bIsPharmacyUser {get;set;}
    public Boolean bIsLogCodelistAvailable{ get; set; }
    public Boolean readyForePostCommit {get;set;}
    public boolean bMedicareCalls{get;set;}
    public Policy__c policy{get;set;}
    public boolean bCheckMedicareCCPermission{get;set;}
    public string sMedCallsIssueCC{get;set;}
    public string sMedCallsResCC{get;set;}
    public boolean bMediCCUpdate{get;set;}
    public boolean bMediCCIssUpdate{get;set;}
    public boolean bMediCCResUpdate {get;set;}
    public integer iCountMediIssueCC{get;set;}
    public integer iCountMediResCC{get;set;}
    public String sMedicareIssInit {get;set;}
    public String sMedicareResInit {get;set;}
    public integer iCountMediIssueCCInit{get;set;}
    public integer iCountMediResCCInit{get;set;}
    public String sHiddenCaseComment {get;set;}
    public String sHiddenCaseCommentMemberNotFound {get;set;}
    public String sHiddenCaseCommentFaultError {get;set;}
    public boolean bQMSFlag {get;set;}
    public Boolean bisQMSErrorExist{get;set;}
    public List<String> sCenters = new List<String>{GLOBAL_CONSTANT_HUM.SERVICE_CENTER_HUMANA_PHARMACY,GLOBAL_CONSTANT_HUM.SERVICE_CENTER_RX};
    public List<String> wqueues = new List<String>{GLOBAL_CONSTANT_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,GLOBAL_CONSTANT_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,GLOBAL_CONSTANT_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public List<String> wTopics = new List<String>{GLOBAL_CONSTANT_HUM.TOPIC_SPECIAL_INVESTIGATIONS,GLOBAL_CONSTANT_HUM.TOPIC_ORAL_GRIEVANCE,GLOBAL_CONSTANT_HUM.NONE_HUM};
    public boolean hPharmacyUser {get;set;} 
    public boolean bRSOHPFlag{get;set;}
    public boolean bIsPreviousQueueBlank {get;set;}
    public string sCaseType {get;set;}
    public boolean bSwitch_1414643 {get;set;}
    public Boolean bShowEsclIndicator{get;set;}
    public Boolean bShowEsclIndSwitch{get;set;}
    public string sEsclIndOnload;
    /**
    * Method to fetch details and whether remainder message should be shown or not   
    * @param  NULL
    * @return Void
    */
    private void prePopulateCaseDetails()
    {
        recId = ApexPages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.ID2_HUM);
        If(recId != Null)
        {
            oCaseRec = [Select Id,DCN__c,Doc_Type__c,Policy_Member__r.Product__c,Policy_Member__r.Product_Type__c ,Policy_Member__r.Product_Type_Code__c,Classification_Type__c,Interacting_About_Type__c,
                            Origin,Owner_Queue__c,Complaint_Type__c,account.Enterprise_ID__c,accountid, Recordtypeid,Recordtype.Name, Tenant_Id__c, G_A_Rights_Given__c, G_A_Reason__c, 
                            Language_Preference__c,Texas_Complaint_Letter_Requested__c, Complaint__c,Topic__c, Complaint_Reason__c, Behavioral_Health__c, Interacting_With_Name__c,
                            Work_Queue_View_Name__c, CaseNumber,CTCI_List__c, Interacting_With__c, OwnerId, DST_Case__c, CoKY_Case__c, HO_Dental_Case__c, HO_Medical_Case__c, Status,
                            General_Case__c,User__c,  CTCI_List__r.Intent__c,CTCI_List__r.Intent__r.name,Policy_Member__c,Policy_Member__r.Policy__c,CTCI_List__r.Classification__c,
                            CTCI_List__r.Classification__r.name,CTCI_List__r.Classification_Type__r.Name,Policy_Member__r.Member__r.RecordType.Name, Type,
                            Priority, Created_By_Queue__c, Previous_Owner_Queue__c, CreatedBy.Id,CreatedBy.IsActive, CreatedBy.Name, CreatedBy.Current_Queue__c,Case_Owner_Queue__c, Case_Owner_Queue_Name__c,Transfer_Reason__c,Contract_Protected_Case__c, CTCI_List__r.Service_Model_Type__c, Escalation_Indicator__c,Escalation_Indicator_Date__c From Case Where Id = :recId];
            
            sCaseDCN = oCaseRec.DCN__c;
    		sCaseDocType = oCaseRec.Doc_Type__c;
			bNoMacessCase = false;
    
            sCaseNumber = oCaseRec.CaseNumber;
            sCaseType = oCaseRec.Type;
            sEsclIndOnload = oCaseRec.Escalation_Indicator__c;
            //REQ - 241737: Showing Reminder message only when Policy is not available or only RSO Medicare policy is being transferred
            bIsRSOMedicarePolicy = string.IsBlank(oCaseRec.Policy_Member__c) || oCaseRec.Policy_Member__r.Member__r.RecordType.Name == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_UNKNOWNMEMBER_HUM || HUMGetBusinessDescription.getPMBusinessDescription(oCaseRec.Policy_Member__c) ? true : false;
            bIsPharmacyUser= Pharmacy_H_HUM.isPharmacyUser() == GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM ? false : true;
            
            if((!bIsPharmacyUser) && (setPharRecType.contains(oCaseRec.RecordType.name)))
            {
                bIsPharmacyUser = Pharmacy_H_HUM.isPDPPilotUser(); 
            }
           
         if(!((oCaseRec.Recordtype.Name).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICARE_CASE)))
            {
                if (oCaseRec.Policy_Member__r.Policy__c!= null)
                {  
                    this.policy = [SELECT Major_Lob__c,Policy__c.Product__c ,Policy__c.Product__r.Product_Type__c, Policy__c.Product__r.Segment_Type__c,Policy__c.Product__r.ProductCode from Policy__c WHERE id =:oCaseRec.Policy_Member__r.Policy__c LIMIT 1];
                }
            }
            boolean bRSOPermissionSetFlag = false;
            if(lstRSOHPPermissionSet!=null && !lstRSOHPPermissionSet.isEmpty())
            {
                bRSOPermissionSetFlag = True;
                if((!bIsPharmacyUser) && (setPharRecType.contains(oCaseRec.RecordType.name)))
                {
                    bIsPharmacyUser = true;
                }
                else
                {
                    bIsPharmacyUser = False;
                }
                bRSOHPFlag = bIsPharmacyUser;
            }
            bIsLogCodelistAvailable = Pharmacy_H_HUM.fetchLogCodeForBothHPCCS(oCaseRec.Classification_Type__c,bRSOPermissionSetFlag,setPharRecType.contains(oCaseRec.RecordType.name)).size() > 0;
            
            /* Changes for US 2173252 - Add Log Code to Case Transfer Page */
            if (oCaseRec.Classification_Type__c == GLOBAL_CONSTANT_HUM.LOGCODEHSP_CLASSIFICATIONTYPE && ((bIsLogCodelistAvailable && oCaseRec.CTCI_List__r.Service_Model_Type__c != GLOBAL_CONSTANT_HUM.LOGCODEHSP_SERVICEMODELTYPE) || oCaseRec.RecordType.name != GLOBAL_CONSTANT_HUM.HP_MEMBERCASE_HUM)) {
                bIsLogCodelistAvailable = false;
            }
            
            bIsPreviousQueueBlank = String.isBlank(oCaseRec.Previous_Owner_Queue__c);

           
        }
    }
     /**
    * <p>
    * Method to check if the case is elible to get pre populated with service center and department based on CTA object.
    * @param  oCaseRec
    * @Return Void
    */
   private void checkCaseTransferAssist(Case oCaseRec)
   {
        List<Case_Transfer_Assist__c> lstCasetransferAssit = [SELECT Id, Work_Queue_Setup__r.Name 
                                                        FROM Case_Transfer_Assist__c WHERE Is_Active__c=true AND CTCI_List__c=:oCaseRec.CTCI_List__c
                                                        AND Product__r.Name=:oCaseRec.Policy_Member__r.Product__c
                                                        AND Product__r.Major_LOB__c=:oCaseRec.Policy_Member__r.Product_Type__c
                                                        AND Product__r.Product_Type__c=:oCaseRec.Policy_Member__r.Product_Type_Code__c
                                                        AND CTCI_List__r.IsActive__c=true AND Work_Queue_Setup__r.IsActive__c=true Limit 1];
        
        if(lstCasetransferAssit != null && !lstCasetransferAssit.isEmpty()){
              
                setServiceCenterAndDepartment(lstCasetransferAssit[0].Work_Queue_Setup__r.Name);
                if(String.isNotBlank(sServiceCenter) && String.isNotBlank(sDepartment)) {
                    bcaseTransferAssistInfo = true;
                }
                
        }
   }
   
    /**
    * <p>
    * Method to check the record type of case and setting flags which will be used for rendering
    * @param  NULL
    * @Return Void
    */
   private void checkRecordType()
    {
        bGAndASectionRender = false;
        bTexasRender = false; 
        if(oCaseRec.Recordtype.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_AGENTBROKERCASE_HUM) || oCaseRec.Recordtype.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_GROUPCASE_HUM) || oCaseRec.Recordtype.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_UNKNOWNCASE_HUM) || (oCaseRec.Recordtype.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICARE_CASE) && oCaseRec.Interacting_About_Type__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_UNKNOWNMEMBER_HUM )))
        {
            bGAndASectionRender = false;
            bTexasRender = false;
        }
        else if(oCaseRec.Recordtype.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_PROVIDERCASE_HUM))
        {
            bGAndASectionRender = true;
            bTexasRender = false;   
        }
        else if(oCaseRec.Recordtype.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM) || (oCaseRec.Recordtype.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICARE_CASE) && oCaseRec.Interacting_About_Type__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEMBER_RECORDTYPE_HUM )))
        {
            bGAndASectionRender = true;
            bTexasRender = true; 
        }
        
    }

    /**
    * Method to determine error message should be shown
    *
    * @param     NULL
    */
   
    @testvisible
    public void checkForErrors()
    {
        //REQ - 289407 : CA Ticket - 6242901 : Show error message when the Classification and/or intent are not populated on Case
        bCIError = oCaseRec.CTCI_List__r.Classification__c!= NULL &&  oCaseRec.CTCI_List__r.Intent__c != NULL ? true : false;
        if(!bCIError) ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, system.label.CIError_ChangeCase_HUM));
        
        if(oCaseRec.OwnerId != Userinfo.getUserId() && (oCaseRec.OwnerId.getSobjectType().getDescribe().getName().equals(GLOBAL_CONSTANT_HUM.USER_HUM)))
        {
            if(checkUserProfile())
            {
                bVisible = false;
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, System.label.HUMCaseTransferError));    
            }
        }
        if (oCaseRec.Interacting_With__c == NULL && String.isBlank(oCaseRec.Interacting_With_Name__c))
        {
            bVisible = false;
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, System.label.HUMSelectInteractionWith));
        }
        if(bcaseTransferAssistInfo){
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, system.label.CHANGECASEOWNER_CASETRANSFERASSSIT_INFO_HUM));
        }
    }                                                                                                                                                                                                                                   

    /**
    * Returns Boolean 
    * <p>
    * Method Boolean whether current user is Specialist profile user
    *
    * @param     NULL
    */
    public Boolean checkUserProfile()
    {
        Id userProfileId = userinfo.getProfileId();
        String sPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();
        if(sPharmacyUser == System.Label.PharmacyUserSpecialistYes_HUM)
            bPharmacyUserSupervisor = false;
        Id CCSProfileId = [Select id from Profile where Name =: GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_CCSPECIALISTPROFILE_HUM].id;
        if ((userProfileId == CCSProfileId)|| (sPharmacyUser == System.Label.PharmacyUserSpecialistYes_HUM))    return true;
        return false;
    }

    /**
    * Returns all service centers present in the system
    * <p>
    * Method to return all service centers present in the system
    *
    * @param     NULL
    * @return    List <SelectOption>                                                                                   
    */
    public List <SelectOption> getServiceCenters() 
    {
        setServiceCenter = new Set<String>();
        List<SelectOption> lstOptions = new List<SelectOption>();
        List<String> lstServiceValues = new List<String>();
        for (String sWorkQueue : mapWorkQueueSetup.keySet()) 
        {
            if(getCaseIndicatorStatus(sWorkQueue))  
            {
              if(!hPharmacyUser)
              { 
                setServiceCenter.add(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c); 
                mapServCenDeptWorkQueueSetup.put(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c +'#' + mapWorkQueueSetup.get(sWorkQueue).Department__c,mapWorkQueueSetup.get(sWorkQueue));
              }
              else
              {  
                if(wqueues.contains(mapWorkQueueSetup.get(sWorkQueue).Name) || sCenters.contains(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c))
                {
                  setServiceCenter.add(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c); 
                  mapServCenDeptWorkQueueSetup.put(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c +'#' + mapWorkQueueSetup.get(sWorkQueue).Department__c,mapWorkQueueSetup.get(sWorkQueue));
                }
              }
            }          
        }
        lstServiceValues = HUMUtilityHelper.doSort(setServiceCenter);
        for(integer i=0; i < lstServiceValues.size(); i++)
        {
            lstOptions.add(new SelectOption(lstServiceValues[i], lstServiceValues[i]));
        }
        if (!lstOptions.isEmpty()) 
        {
            lstOptions.add(0, new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));
        }
        else
        {
            lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));    
        }
        
        return lstOptions;
    }

    /**
    * Returns Boolean
    * <p>
    * Method to return case indicator based on queue
    *
    * @param     String     sQueuename
    * @return    Boolean                                                                                   
    */
    public boolean getCaseIndicatorStatus(String sQueueName)
    {
        	if(bswitch_US2578074)
            {
        return (mapWorkQueueSetup.get(sQueueName).General__c && oCaseRec.General_Case__c ||
                mapWorkQueueSetup.get(sQueueName).Contract_Protected_Data__c && oCaseRec.Contract_Protected_Case__c == '1' ||
                mapWorkQueueSetup.get(sQueueName).DST__c && oCaseRec.DST_Case__c ||
                mapWorkQueueSetup.get(sQueueName).HO_Dental__c && oCaseRec.HO_Dental_Case__c ||   
                mapWorkQueueSetup.get(sQueueName).HO_Medical__c && oCaseRec.HO_Medical_Case__c ||
                mapWorkQueueSetup.get(sQueueName).CokY__c &&  oCaseRec.CoKY_Case__c) &&
                NULL != mapWorkQueueSetup.get(sQueueName).TenantID__c && NULL != oCaseRec.Tenant_Id__c &&
                mapWorkQueueSetup.get(sQueueName).TenantID__c.equals(oCaseRec.Tenant_Id__c);
                } else{
                return (mapWorkQueueSetup.get(sQueueName).General__c && oCaseRec.General_Case__c ||
                mapWorkQueueSetup.get(sQueueName).General__c && oCaseRec.Contract_Protected_Case__c == '1' ||
                mapWorkQueueSetup.get(sQueueName).DST__c && oCaseRec.DST_Case__c ||
                mapWorkQueueSetup.get(sQueueName).HO_Dental__c && oCaseRec.HO_Dental_Case__c ||   
                mapWorkQueueSetup.get(sQueueName).HO_Medical__c && oCaseRec.HO_Medical_Case__c ||
                mapWorkQueueSetup.get(sQueueName).CokY__c &&  oCaseRec.CoKY_Case__c) &&
                NULL != mapWorkQueueSetup.get(sQueueName).TenantID__c && NULL != oCaseRec.Tenant_Id__c &&
                mapWorkQueueSetup.get(sQueueName).TenantID__c.equals(oCaseRec.Tenant_Id__c);
    
            }
    }

    /**
    * Returns all Departments present in the system
    * <p>
    * Method to return all departments present in the system
    *
    * @param     NULL
    * @return    List <SelectOption>                                                                                   
    */
    public list <Selectoption> getDepartments()
    {
        setDepartment = new Set<String>();
        List<SelectOption> lstOptions = new List<SelectOption>();
        List<String> lstDepValues = new List<String>();
        for(string sWorkQueue : mapWorkQueueSetup.keySet())
        {
            if(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c.equalsIgnoreCase(sServiceCenter) &&
                    !string.isBlank(mapWorkQueueSetup.get(sWorkQueue).Department__c))
            {     
                if(getCaseIndicatorStatus(sWorkQueue))
                {
                  if(!hPharmacyUser) 
                  { 
                    setDepartment.add(mapWorkQueueSetup.get(sWorkQueue).Department__c);
                  }
                  else
                  {
                    if(wqueues.contains(mapWorkQueueSetup.get(sWorkQueue).Name) || sCenters.contains(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c))
                    {
                      setDepartment.add(mapWorkQueueSetup.get(sWorkQueue).Department__c);
                    }
                  }
                }
            }
        }
        lstDepValues = HUMUtilityHelper.doSort(setDepartment);
        for(integer i=0; i < lstDepValues.size(); i++)
        {
            lstOptions.add(new SelectOption(lstDepValues[i], lstDepValues[i]));
        }
        if (lstOptions.isEmpty()) 
        {
            lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));
        }
        else
        {
            lstOptions.add(0, new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));    
        }
        
        return lstOptions;
    } 

    /**
    * Returns list <Selectoption>
    * <p>
    * Method to return all topics related to work queue
    *
    * @param     NULL
    * @return    List <SelectOption>                                                                                   
    */
    public list <Selectoption> getTopics()
    {
        this.bTopicMan = false;
        String sWorkQueueSetUpName=null;
        Set<String> setTopic = new Set<String>();
        List<SelectOption> lstOptions = new List<SelectOption>();
        List<String> lstTopicValues = new List<String>();
        lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM, GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM));  
        if((String.isBlank(sServiceCenter) && String.isBlank(sDepartment)) || (!sServiceCenter.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) 
        && sDepartment.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM)))   return lstOptions;
        lstOptions.remove(0);
        for(string sWorkQueue : mapWorkQueueSetup.keySet())
        {
            if(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c.equalsIgnoreCase(sServiceCenter) &&
                    mapWorkQueueSetup.get(sWorkQueue).Department__c.equalsIgnoreCase(sDepartment))
            {
                sWorkQueueSetUpName = sWorkQueue;
                break;
            }
        }
        List<Work_Queue_Junction__c> lstWorkQueueJunction = [select Work_Queue_Setup_Name__c, Work_Queue_Topic_Name__r.Name from Work_Queue_Junction__c where 
        Work_Queue_Setup_Name__c =: sWorkQueueSetUpName and Work_Queue_Topic_Name__r.IsActive__c = true];
        if(!lstWorkQueueJunction.isEmpty())
        {
            for(Work_Queue_Junction__c oWQJ : lstWorkQueueJunction)
            {
               if(!hPharmacyUser) 
               {  
                 setTopic.add(oWQJ.Work_Queue_Topic_Name__r.Name); 
               }
               else
               {
                 if(sCenters.contains(sServiceCenter))
                 {
                   setTopic.add(oWQJ.Work_Queue_Topic_Name__r.Name); 
                 }
                 else
                 { 
                   if(wTopics.contains(oWQJ.Work_Queue_Topic_Name__r.Name)) 
                   {
                   setTopic.add(oWQJ.Work_Queue_Topic_Name__r.Name); 
                   }
                 }
               }
            }
            lstTopicValues = HUMUtilityHelper.doSort(setTopic);
            for(integer i=0; i < lstTopicValues.size(); i++)
            {
                lstOptions.add(new SelectOption(lstTopicValues[i], lstTopicValues[i]));
            }
        }  
        if (lstOptions.isEmpty()) 
        {
            lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM, GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM));
            bShowTopic = true;
        }
        else
        {
            lstOptions.add(0, new SelectOption(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM, GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM));
            bShowTopic = false;
        }          
        return lstOptions;
    }    
    /**
    * Returns void
    * <p>
    * Method to show topic level guidance 
    *
    * @param    NULL 
    * @return    Void                                                                                   
    */
    public void displayTopicGuidance()
    {
        bDisplayTopicGuidance = false;
        String sWorkQueueSetUpName = sSelectedWorkQueue.replace('_',' ');
            if(String.isNotBlank(sTopic) && !sTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM))
            { 
                Work_Queue_Junction__c oWQJ = [select Work_Queue_Setup_Name__r.Name, Work_Queue_Topic_Name__r.Name,Work_Queue_Topic_Guidance__c from Work_Queue_Junction__c where 
                Work_Queue_Setup_Name__r.Name =: sWorkQueueSetUpName and Work_Queue_Topic_Name__r.Name=:sTopic and Work_Queue_Topic_Name__r.IsActive__c = true LIMIT 1];

                if(oWQJ != null){
                    sTopicGuidance= oWQJ.Work_Queue_Topic_Guidance__c;
                    if(!String.isBlank(sTopicGuidance)){
                    bDisplayTopicGuidance = true;
                    } 
                }       
            }
        
    }    

    /**
    * Returns PageReference
    * <p>
    * Method to return url when user clicks on cancel 
    *
    * @param     NULL
    * @return    PageReference                                                                                   
    */
    public pagereference caseDetailCancel()
    {
        PageReference oPageRef = new Pagereference('/' + recId);
        oPageRef.setRedirect(true);
        return oPageRef;
    }

     /*
    * This method saves the HP case comment record
    * @param   NA
    * @return  void
    */
    public pageReference saveHPCaseComment()
    {
        
        string bRedirect = '';
        PageReference pageRef;
        caseComment casecom;
        string bErrorCaseComment = '';
        List<CaseComment> lstCaseComment = new List<CaseComment>();
        if (ApexPages.currentpage().getParameters().containsKey('redirectFlag'))
        {
            bRedirect = ApexPages.currentpage().getParameters().get('redirectFlag');
        }
        if (ApexPages.currentpage().getParameters().containsKey('errorCaseComment'))
        {
            bErrorCaseComment = ApexPages.currentpage().getParameters().get('errorCaseComment');
        }
        
        if(bErrorCaseComment == 'true')
        {
            sHiddenCaseComment = sHiddenCaseCommentMemberNotFound;  
        }
        else if(bErrorCaseComment == 'false')
        {
            sHiddenCaseComment = sHiddenCaseCommentFaultError;  
        }
        
        if(string.isNotBlank(sHiddenCaseComment)) 
        {
            casecom = new caseComment(parentId = oCaseRec.id, commentBody = sHiddenCaseComment);
            if(casecom != null) lstCaseComment.add(casecom);
        }
        
        if(string.isNotBlank(sCaseComments) && string.isNotBlank(sCodeHidden)) 
        {
            casecom = new caseComment(parentId = oCaseRec.id, commentbody='('+sCodeHidden+') '+sCaseComments);
            if(casecom != null) lstCaseComment.add(casecom);
        }
        if(String.isNotBlank(sHashTag)) {
            lstCaseComment.add(new CaseComment(parentId = oCaseRec.id, commentbody = sHashTag));
            sHashTag = '';
        }
        if(!lstCaseComment.isEmpty())
        {
            insert lstCaseComment; 
            if(bRedirect == 'true')
            {
                pageRef = new PageReference('/'+oCaseRec.id);
                pageRef.setRedirect(true);
            }
        }
        return pageRef;
    }
    /**
    * Returns PageReference
    * <p>
    * Method to return url when user clicks on clear 
    *
    * @param     NULL
    * @return    PageReference                                                                                   
    */
    public pagereference clear()
    {
        PageReference oPageRef = new PageReference('/apex/HUMChangeCaseOwner');
        oPageRef.getParameters().put(GLOBAL_CONSTANT_HUM.ID3_HUM, recId);
        oPageRef.getParameters().put(GLOBAL_CONSTANT_HUM.STRING_CLEAR,GLOBAL_CONSTANT_HUM.STRING_TRUE);
        oPageRef.setRedirect(true);        
        return oPageRef;
    }
    /**
    * Returns PageReference
    * <p>
    * Method to return url when user clicks on clear 
    *
    * @param     NULL
    * @return    PageReference                                                                                   
    */
    public pagereference redirectToCasePage()
    {
         PageReference oPageRef = new Pagereference('/' + recId);            
         oPageRef.setRedirect(true);
         return oPageRef; 
    }
    /* Method to poulate queue name and also to Show WQ Guidance Information
    *
    * @param     NULL
    * @return    void 
    */
    public void populateQueueName()
    {
        bDisplayGuidance = false;
        try
        {
            Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
            if(mapServCenDeptWorkQueueSetup.containsKey(sServiceCenter + '#' + sDepartment))
            {
                oWQS = mapServCenDeptWorkQueueSetup.get(sServiceCenter + '#' + sDepartment);
                sSelectedWorkQueue = oWQS.Work_Queue_Name__c;
                sPublicGroupName= oWQS.Public_Group_Name__c;
 
                    if(!sDepartment.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM)){
                        sGuidance= oWQS.Work_Queue_Guidance__c;
                        if(!String.isBlank(sGuidance)){
                            bDisplayGuidance = true;
                        }
                    }
 
            }
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'HUMChangeCaseOwnerController', 'populateQueueName');
        }        
    }

    /**
    * Returns PageReference
    * <p>
    * Method to assign default values to case 
    *
    * @param     NULL
    * @return    void                                                                                   
    */
    @testVisible private void assignFixedValuesToCase()
    {	
        if(String.isBlank(oCaseRec.User__c))
        {
            Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
            if(mapServCenDeptWorkQueueSetup.containsKey(sServiceCenter +'#' + sDepartment))
            {
                oWQS = mapServCenDeptWorkQueueSetup.get(sServiceCenter + '#' + sDepartment);
                sSelectedWorkQueue = oWQS.Work_Queue_Name__c;
                sPublicGroupName= oWQS.Public_Group_Name__c;
            }
            
            sGrpQueueName = sSelectedWorkQueue.replace('_',' ');
            Group oGrp = NULL;  
            if(Schema.sObjectType.Case.fields.Classification_Type__c.isCreateable() && Schema.sObjectType.Case.fields.Classification_Type__c.isUpdateable()){ oCaseRec.Classification_Type__c =  oWQS.Classification_Type__r.Name; }
            if(Schema.sObjectType.Case.fields.Owner_Queue__c.isCreateable() && Schema.sObjectType.Case.fields.Owner_Queue__c.isUpdateable()) { oCaseRec.Owner_Queue__c = sGrpQueueName; }
            if(Schema.sObjectType.Case.fields.Service_Center__c.isCreateable() && Schema.sObjectType.Case.fields.Service_Center__c.isUpdateable()){ oCaseRec.Service_Center__c = sServiceCenter; }
            if(Schema.sObjectType.Case.fields.Department__c.isCreateable() && Schema.sObjectType.Case.fields.Department__c.isUpdateable()) { oCaseRec.Department__c = sDepartment; }
            bGroupexists = false;

            if(String.isNotBlank(sTopic) && !sTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM))
            {
                oCaseRec.topic__c = sTopic;
            }
            else if(String.isBlank(sTopic) || sTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM))
            {
                if(Schema.sObjectType.Case.fields.topic__c.isCreateable() && Schema.sObjectType.Case.fields.topic__c.isUpdateable()){ oCaseRec.topic__c = ''; }
            }

            if(String.isBlank(sOwnerId) && NULL != oCaseRec)
            {
                if (NULL != oWQS.OwnerId)  
                {
                     if(Schema.sObjectType.Case.fields.Case_Owner__c.isCreateable() && Schema.sObjectType.Case.fields.Case_Owner__c.isUpdateable()){ oCaseRec.Case_Owner__c = sGrpQueueName; }
                     if(Schema.sObjectType.Case.fields.OwnerId.isCreateable() && Schema.sObjectType.Case.fields.OwnerId.isUpdateable()){ oCaseRec.OwnerId = oWQS.OwnerId;}
                }
            }
            else if(!String.isBlank(sOwnerId) && NULL != oCaseRec)
            {   
                if(Schema.sObjectType.Case.fields.OwnerId.isCreateable() && Schema.sObjectType.Case.fields.OwnerId.isUpdateable()){ oCaseRec.OwnerId = sOwnerId; }
                if(Schema.sObjectType.Case.fields.Case_Owner__c.isCreateable() && Schema.sObjectType.Case.fields.Case_Owner__c.isUpdateable()){ oCaseRec.Case_Owner__c = sOwnerName; }
            }        
        }
        else
        { 
            if(Schema.sObjectType.Case.fields.User__c.isCreateable() && Schema.sObjectType.Case.fields.User__c.isUpdateable()){ oCaseRec.User__c = oCaseRec.User__c; }
            if(Schema.sObjectType.Case.fields.Status.isCreateable() && Schema.sObjectType.Case.fields.Status.isUpdateable()){ oCaseRec.Status ='Pending - Transfer'; }
            if(Schema.sObjectType.Case.fields.Transferred_From__c.isCreateable() && Schema.sObjectType.Case.fields.Transferred_From__c.isUpdateable()){ oCaseRec.Transferred_From__c=oCaseRec.OwnerId; }
            if(Schema.sObjectType.Case.fields.Transferred_Date__c.isCreateable() && Schema.sObjectType.Case.fields.Transferred_Date__c.isUpdateable()){ oCaseRec.Transferred_Date__c= System.Now(); }
        }  
    }

    /**
    * Returns Security access of the user to the case
    * <p>
    * Method to check the security
    *
    * @param     String : Id of the user to which the case is trnasferred.
    * @param     List<Id> : List of Case Id , it will be always be 1 but we have it in a list as the UserRecordAccess query accepts it as a string.
    * @return    boolean 
    */
    private boolean checkSecurity(String sUserId , List<Id> lstCaseId)
    {
      List <UserRecordAccess> lstUserRecordAcess = [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =:sUserId AND RECORDID IN : lstCaseId];
      return (!lstUserRecordAcess.isEmpty() && lstUserRecordAcess[0].HASREADACCESS) ? true : false;
    }

    /**
    * Returns PageReference
    * <p>
    * Method to validate criteria and show error on page
    *
    * @param     NULL
    * @return    PageReference 
    */
    public Pagereference showError()
    {
        PageReference oPageRef = new Pagereference('/' + recId);
        try
        {
            List<GroupMember> UserQueueName = new List<GroupMember>();
            if(String.isNotBlank(oCaseRec.User__c))
            {
                UserQueueName = [select Id from GroupMember where UserorGroupId =: oCaseRec.User__c];

                if(!checkSecurity(oCaseRec.User__c, new List<Id>{oCaseRec.Id})) // Check if the user has necessary access before transfer
                {
                    List<User> transferredUser = [Select Name from User where Id = :oCaseRec.User__c limit 1];
                    if(!transferredUser.isEmpty())
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, system.label.Cannot_WarmTrasfer_HUM +' '+oCaseRec.CaseNumber +' '+ system.label.Because_WarmTrasfer_HUM +' '+ transferredUser[0].Name+' ' +system.label.Another_WarmTrasfer_HUM));
                    }
                    return null;
                }
                if(UserQueueName.Size() == 0) // Check if the user is added to Public Group before transfer
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, System.label.CHANGECASEOWNER_CASETASKUSER_HUM));
                    return null;
                }
                List <Case> CaseList = [Select Id from Case where User__c=:oCaseRec.User__c];
                if(CaseList.size()>=25 && String.isNotBlank(oCaseRec.User__c))
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, System.label.CHANGECASEOWNER_CASETASKUSER25_HUM));
                    return null;
                } 
            } 
            else
            {
                if (sServiceCenter.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM))
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, System.label.CHANGECASEOWNER_CASETASKSERVICECENTER_HUM));
                    return null;
                }
                else if(!sServiceCenter.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) && sDepartment.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM))
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, System.label.CHANGECASEOWNER_CASETASKDEPARTMENTNOTIFICATION_HUM));
                    return null;
                }
                if(String.isBlank(sOwnerId) && (String.isNotBlank(sOwnerName)))
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, System.label.CHANGECASEOWNER_USERNOTFOUND_HUM));
                    return null;
                }
                if (!sServiceCenter.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) && !sDepartment.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM))
                {
                    if(!bShowTopic && (sTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM)))
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, System.label.CHANGECASEOWNER_CASETASKTOPICNOTIFICATION_HUM));
                        return null;
                    }
                }
                if (oCaseRec.G_A_Rights_Given__c == NULL && oCaseRec.Complaint__c == NULL && bGAndASectionRender == true)
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, System.label.CHANGECASEOWNER_GANDARIGHTSGIVEN_HUM));
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, System.label.CHANGECASEOWNER_COMPLAINTERROR_HUM));
                    return null;
                }
                if (oCaseRec.G_A_Rights_Given__c == NULL && oCaseRec.Complaint__c != NULL && bGAndASectionRender == true)
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, System.label.CHANGECASEOWNER_GANDARIGHTSGIVEN_HUM));
                    return null;
                }
                if (oCaseRec.Complaint__c == NULL && oCaseRec.G_A_Rights_Given__c != NULL && bGAndASectionRender == true)
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, System.label.CHANGECASEOWNER_COMPLAINTERROR_HUM));
                    return null;
                }
                if ((oCaseRec.Interacting_About_Type__c == GLOBAL_CONSTANT_HUM.RECORD_TYPE_NAME_UNKNOWN_MEMBER || !String.isNotBlank(oCaseRec.Policy_Member__c)) && (sServiceCenter.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.KNOWNPROVIDER_CASE_SERVICE) && sDepartment.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Department_Correspondence_MHK)))
                {
            
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, System.label.CHANGECASEOWNER_INTERACTINGABOUTTYPEUNKNOWNMEMBER_HUM));
                    return null;
                }
            }
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMChangeCaseOwnerController', 'showError');
            return null;
        }
        return oPageRef;
    }

    /**
    * Returns PageReference
    * <p>
    * Method to save case and task
    *
    * @param     NULL
    * @return    PageReference 
    */
    @testvisible
    public Pagereference saveTaskOrCase()
    {
        boolean bCheckMediCC = true;
        readyForePostCommit = false;
        boolean bError = false;
        boolean bNoMHKCase= false;
        List<Case_Transfer_Queue_and_View__mdt> QueueViewData = new List<Case_Transfer_Queue_and_View__mdt>();
        try
        {
            bisErrorExist=false;
            bisQMSErrorExist = false;
            bcase=false;
            Pagereference oPageReference = showError(); 
            if(oPageReference == NULL)
            {    
                bisErrorExist=true;
                return null;
            }
            
           if(bMedicareCalls == true && bCheckMedicareCCPermission == true && !bRSOHPFlag) 
             {
               if(bMediCCUpdate)
               {
                if(iCountMediIssueCCInit >= 10 && iCountMediIssueCCInit <= 2000 && iCountMediResCCInit >= 10 && iCountMediResCCInit <= 2000) bMediCCUpdate =true;
                else bMediCCUpdate = false;
               }
               if(String.isNotBlank(sHashTag)) {
                  oMedCalls.sHashTagComment = sHashTag;
                  sHashTag = '';
               }
               bCheckMediCC = oMedCalls.createUpdateCaseCommentRecord(sMedCallsIssueCC, sMedCallsResCC, oCaseRec.Id,  iCountMediIssueCC, iCountMediResCC, oCaseRec.status , bMediCCUpdate,bMediCCIssUpdate, bMediCCResUpdate, true);               
             }
             
             //US#1083065 When MHK Task is transferred from CRM to MHK if there is no Case Comments then error should be displayed
            if(bSwitch_1414643 && string.isNotBlank(sServiceCenter) && sServiceCenter.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Service_Center_Provider) && string.isNotBlank(sDepartment) && sDepartment.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Department_Correspondence_Task_MHK) 
            && string.isNotBlank(sCaseType) && sCaseType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MHK_CASE_TYPE) && string.isBlank(sCaseComments) )
            {
                list<CaseComment> lstCaseComments = [SELECT Id,CommentBody, CreatedById,CreatedBy.Name,CreatedDate, ParentId FROM CaseComment where ParentId =: oCaseRec.Id and CreatedBy.Name !=: GLOBAL_CONSTANT_HUM.DatapowerUserName];
                if(lstCaseComments == null || (lstCaseComments != null && lstCaseComments.isEmpty()))
                {
                    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR, GLOBAL_CONSTANT_HUM.CommentsErrorMsg);
                    ApexPages.addMessage(myMsg);
                    bisErrorExist = true;
                    bNoMHKCase = true;
                }    
            }
            
            //US#1083065 When Case Type other than MHK Task is transferred to 'Correspondence Task To MHK' Department and 'Provider' Service Center then error message will be displayed
            if(bSwitch_1414643 && string.isNotBlank(sServiceCenter) && sServiceCenter.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Service_Center_Provider) && string.isNotBlank(sDepartment) && sDepartment.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Department_Correspondence_Task_MHK) 
            && ((string.isNotBlank(sCaseType) && !sCaseType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MHK_CASE_TYPE)) || string.isBlank(sCaseType)))
            {
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR, GLOBAL_CONSTANT_HUM.NonMHKTaskErrorMsg);
                ApexPages.addMessage(myMsg);
                bisErrorExist = true;
                bNoMHKCase = true; 
            } 
            
            if( bSwitch_US4285092 && string.isNotBlank(sServiceCenter) && sServiceCenter.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Business)
             && string.isNotBlank(sDepartment) && sDepartment.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Correspondence_CRM_to_Macess)
             && string.isNotBlank(sCaseType) && sCaseType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.HUMCaseTypeCorrespondence))
             {
                if(string.isBlank(sCaseDCN) || string.isBlank(sCaseDocType))
                { 
                    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.WARNING, 'Case cannot be transferred to Macess due to no DCN or Doc Type on the Case.  Case should be transferred in CRM Service');
                    ApexPages.addMessage(myMsg);
                    bisErrorExist = true;
                    bNoMacessCase = true;
                    return null;
                }
             }
             if(bSwitch_1414643)
             {
                 if (NULL != oCaseRec && !bNoMHKCase)
                 {
                    assignFixedValuesToCase();   
                 }    
             }
             else
             {
                assignFixedValuesToCase();
             }     
             if(bCheckMediCC)
              {
                 if(String.isBlank(oCaseRec.User__c))
                 {
                    //bQMSFlag = true;       
                    if(String.isNotBlank(sSelectedWorkQueue) && sSelectedWorkQueue.equals(System.label.CHANGECASEOWNER_QMSQUEUE_HUM)|| sSelectedWorkQueue.equals(System.label.CHANGECASEOWNER_MARKETHOQMSQUEUE_HUM))
                    {
                        List<Case_Action_Association__c> lstCaseAssosciation = [Select Id,Case_Action__r.Name,(Select Process_Question__c,Process_Question_Response__c from Case_Action_Responses__r) from Case_Action_Association__c Where Case_Action__r.Name='QAA' and Case__c =: oCaseRec.Id order by LastModifiedDate DESC LIMIT 1];
                    //Check if a QAA Process is present
                        if(!lstCaseAssosciation.isEmpty())
                        {
                            bQMSFlag = true;
                        }
                        else
                        {
                            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING,System.label.CHANGECASEOWNER_QAAMESSAGE_HUM));
                            bCase = true;
                            bisQMSErrorExist = true;
                        }
                    }
                }

            }else
            {
                            bCase = true;
                            bisQMSErrorExist = true;
            }
               
            //REQ - 378712 Case Transfer Service
            if(sServiceCenter == Label.Service_Center_Louisville && (sDepartment == Label.Department_Grievance_and_Appeals || sDepartment ==  Label.Department_GA_Correspondence_Screening))
            {   
                if(Label.Case_Transfer_Service_Flag == '1' && bCheckMediCC)
                {
                    bNoQMSFlag = true;
                }    
            }
            //REQ - 378712 Case Transfer Service  

             if(((string.isNotBlank(sCaseComments) && string.isBlank(sCode)) || (string.isNotBlank(sCode) && string.isBlank(sCaseComments))) && bIsPharmacyUser && bIsLogCodelistAvailable)
            {
                ApexPages.Message myMsg;
                if(string.isBlank(sCode)) myMsg = new ApexPages.Message(ApexPages.Severity.ERROR,'Humana Pharmacy Log Code:  You must enter a value');
                if(string.isBlank(sCaseComments)) myMsg = new ApexPages.Message(ApexPages.Severity.ERROR,'Case Comments:  You must enter a value');
                ApexPages.addMessage(myMsg);
                bisErrorExist = true;
            } 

            if(schema.SobjectType.case.fields.Escalation_Indicator_Date__c.isUpdateable() && bShowEsclIndSwitch)
            {   String escIndDate = !string.isBlank(oCaseRec.Escalation_Indicator_Date__c) ? oCaseRec.Escalation_Indicator_Date__c : '' ;
                if(oCaseRec.Escalation_Indicator__c != null && oCaseRec.Escalation_Indicator__c != sEsclIndOnload)
                    oCaseRec.Escalation_Indicator_Date__c = escIndDate +'\n'+system.now().format('MM/dd/yyyy hh:mm:ss a') + ' - '+oCaseRec.Escalation_Indicator__c;
            }
            
            

          if((NULL != oCaseRec && !bCase && !bGroupexists) || (NULL != oCaseRec && bNoQMSFlag) || 
             (bSwitch_1414643 && NULL != oCaseRec && !bNoMHKCase) || 
             (bSwitch_US4285092 && NULL != oCaseRec && !bNoMacessCase))
            { 
                Database.upsertResult upsertResult = Database.upsert(oCaseRec, true);
                bError = HUMExceptionHelper.processUpsertResults(new List<Database.upsertResult>{upsertResult},
                new List<SObject>{oCaseRec},'HUMChangeCaseOwnerController', 'saveTaskOrCase', 'Case');
                if(!bError&& bCheckMediCC)
                {
                    if(bIsPharmacyUser && bIsLogCodelistAvailable && (string.isNotBlank(sCaseComments) && string.isNotBlank(sCode)))
                    {
                         readyForePostCommit = true;
                    }
                    else
                    {   
                        if(String.isNotBlank(sCaseComments) || String.isNotBlank(sHashTag))
                        {
                            if(String.isNotBlank(sHashTag)) { 
                                 String sCaseCommentsHTC = sCaseComments + sHashTag;
                                Integer iNewCaseCommentLength = sCaseCommentsHTC.length();
                                if(iNewCaseCommentLength <= 2000){
                                    sCaseComments += sHashTag;
                                } else if(iNewCaseCommentLength > 2000) {
                                    CaseComment sHTComments = new CaseComment();
                                    if(Schema.SObjectType.CaseComment.fields.CommentBody.isCreateable()){ sHTComments.CommentBody = sHashTag; }
                                    if(Schema.SObjectType.CaseComment.fields.ParentId.isCreateable()){ sHTComments.ParentId = oCaseRec.Id; }
                                    insert sHTComments;
                                }
                            }

                            CaseComment oComment = new CaseComment();
                            if(Schema.SObjectType.CaseComment.fields.ParentId.isCreateable()){ oComment.ParentId = oCaseRec.Id; }
                            if(Schema.SObjectType.CaseComment.fields.CommentBody.isCreateable()){ oComment.CommentBody = sCaseComments; }                      
                            Database.SaveResult saveCaseComment = Database.insert(oComment,false);                   
    
                                if (!saveCaseComment.isSuccess()) 
                                {
                                     for (Database.Error err : saveCaseComment.getErrors()) 
                                     {                               
                                       if(err.getMessage().contains(GLOBAL_CONSTANT_HUM.CASECOMMENT_SIZE_EXCEPTION_HUM))
                                       {                                                           
                                            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.WARNING,system.Label.CASECOMMENT_LENGTHWARNING_HUM);
                                            ApexPages.addMessage(myMsg);
                                            bisErrorExist = true;                                     
                                       }
                                     }
                                }                                                                         
                          }          
                          
                }
                
            }
        }
        }
        catch (Exception ex)
        {
            if(ex.getTypeName().equalsIgnoreCase('System.DmlException'))
            {
                ApexPages.addMessage(new ApexPages.Message(Apexpages.Severity.ERROR, ex.getdmlMessage(0)));
            }
            else
            {
                if(bError)    HUMExceptionHelper.logErrors(ex, 'HUMChangeCaseOwnerController', 'saveTaskOrCase');
            }
                bisErrorExist = true;
        }
        return null;        
    }

    /**
    * Returns list of log node
    * <p>
    * Method to create list of log notes 
    *
    * @param  NA
    * @return list<SelectOption> 
    */
    public list<SelectOption> getCaseCommentOptions()
    {
        list<SelectOption> lstSortBy = new list<SelectOption>();
        lstSortBy.add(new SelectOption('', ''));
        for(String code: lstLogCodes)
        {
            lstSortBy.add(new SelectOption(code, code));
        }
        return lstSortBy;
        
    }
    
     /*
    * This method is to populate Log Node Code.
    * @param  NA
    * @return Pagereference    
    */
    public PageReference populateHiddentVal()
    {
        sCodeHidden = sCode;
        return null;
    }  
    
    /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the Get log Note service response
    *
    * @param  List<String> List of Continuation Labels
    * @param  Object State of Continuation
    * @return Object
    */
    
    public static object parseLogNoteResponse(List < string > labels, object State)
    {
        RightSourceEditMemberResponse_DTO_HUM dtoRes;
        boolean successFlag;
        String sNoFaultResponse = '';
        String sFaultResponse = '';
        List<String> lstString = new List<String>();
        try
        {
            HttpResponse logNoteResponse = Continuation.getResponse(labels[0]);
            if (logNoteResponse != null && !logNoteResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)  && ((logNoteResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || logNoteResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {   
                dtoRes = (RightSourceEditMemberResponse_DTO_HUM) System.JSON.deserialize(logNoteResponse.getBody(), RightSourceEditMemberResponse_DTO_HUM.class);
                if (null != dtoRes && null != dtoRes.EditMemberResponse && dtoRes.EditMemberResponse.ErrorDescription != GLOBAL_CONSTANT_HUM.HUM_MEMBER_NOT_FOUND)
                {
                    if (dtoRes != null && dtoRes.EditMemberResponse != null && dtoRes.EditMemberResponse.UID == '')
                    {
                        successFlag = true;
                        sNoFaultResponse = 'true';
                    }
                    else if (dtoRes != null && dtoRes.EditMemberResponse != null && string.isNotBlank(dtoRes.EditMemberResponse.UID))
                    {
                        Error_Log__c errorLog = new Error_Log__c(Error_Code__c = 'Sever Down Exception',
                            Error_Message__c = dtoRes.EditMemberResponse.UID,
                            Request_Sent__c = true,
                            class_name__c = 'HUMChangeCaseOwnerController',
                            Method_Name__c = 'parseLogNoteResponse',
                            Type__c = 'Integration Error');
                        if (errorLog != null) insert errorLog;
                        successFlag = false;
                        sNoFaultResponse = 'false';
                    }
                }
                else
                {
                    sFaultResponse = 'true';
                }
            }
            else
            {
                // Fault Code
                sFaultResponse = Pharmacy_H_HUM.logHpFaultError(logNoteResponse,'HUMChangeCaseOwnerController', 'parseLogNoteResponse', State != null ? String.valueof(State) : '').contains('Member not found') ? 'true' : 'false';
            }
            lstString.add(sNoFaultResponse);
            lstString.add(sFaultResponse);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMChangeCaseOwnerController', 'parseLogNoteResponse');
        }
        return lstString;
    }
    
    /**
    * Returns continuation object
    * <p>
    * Method to call continuation serive for log note.
    *
    * @param sComment                      String
    * @param sEnterpriseId                 String
    * @param sNetworkUserId                String
    * @param sRecordId                     String
    * @param sCode                         String
    * @param caseId                        String
    * @return                              continuation
    */
    @RemoteAction
    public static Continuation sendRequestLogNotes(String sComment, String sEnterpriseId , String sNetworkUserId , String sRecordId, String sCode, String caseId)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            list<string> stateList = new list<string>();
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'parseLogNoteResponse';
            con.State = sRecordId;
            HttpRequest httpEditGetMemberReqObj = PharmacyCaseComment_C_HUM.builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(PharmacyCaseComment_C_HUM.RequestLogNotes(sComment,sEnterpriseId,sNetworkUserId , sCode)));
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMChangeCaseOwnerController', 'sendRequestLogNotes');
            return null;
        }
    }
    
     /*
     * This method to check if this is a Medcare call case - REQ - 300142
     * @param    NA
     * @return   void
     */
   public void checkCCMedicareCalls()
    {
    if((!(oCaseRec.Recordtype.Name).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICARE_CASE)) &&(!((oCaseRec.Recordtype.Name).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE))))
     {
        if (oCaseRec.Policy_Member__c != null && String.isNotBlank(oCaseRec.Policy_Member__c))
         { 
                bMedicareCalls = oMedCalls.checkMedicareCare(oCaseRec.Origin, policy.Product__r.ProductCode, policy.Major_Lob__c, policy.Product__r.Product_Type__c, policy.Product__r.Segment_Type__c);
                list<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :UserInfo.getUserId() AND PermissionSet.Name = : GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM];
                if(lstPMAssign != null && !lstPMAssign.isEmpty()) bCheckMedicareCCPermission = true;
                if(bMedicareCalls == true && bCheckMedicareCCPermission == true) getCCMedicareCalls();
        }
     }
    }
    
    /*
     * This method to get case comments in case of Medicare Calls
     * @param    NA
     * @return   void
     */
    public void getCCMedicareCalls()
    {
        bMediCCIssUpdate = true;
        bMediCCResUpdate = true;
        map<Id, CaseComment> mapMediCC = new map<Id, CaseComment>([SELECT Id, CommentBody FROM CaseComment WHERE ParentId =: oCaseRec.Id]);
        list<CaseCommentType_Junction__c> lstCCTJunc = [SELECT Id, Name,CaseCommentType__r.Description__c,CaseCommentType__r.Name, CaseComment__c FROM CaseCommentType_Junction__c WHERE CaseComment__c IN : mapMediCC.keyset() order by createdDate ASC];
        if(lstCCTJunc != null && !lstCCTJunc.isEmpty())
        {        
            for(CaseCommentType_Junction__c oCCTJ : lstCCTJunc)
            {
                if(oCCTJ.CaseCommentType__r.Name == GLOBAL_CONSTANT_HUM.MEDICARECALLS_ISSUE_HUM && mapMediCC.containsKey(Id.valueOf(oCCTJ.CaseComment__c)))
                {
                   sMedicareIssInit = mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).CommentBody;
                }
                if(oCCTJ.CaseCommentType__r.Name == GLOBAL_CONSTANT_HUM.MEDICARECALLS_RESO_HUM && mapMediCC.containsKey(Id.valueOf(oCCTJ.CaseComment__c)))
                {
                   sMedicareResInit = mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).CommentBody;
                }
            }         
           if(String.isNotBlank(sMedicareIssInit) && String.isNotBlank(sMedicareResInit)) bMediCCUpdate = true;
           else bMediCCUpdate = false;
        }
        else bMediCCUpdate = false;
    }
     /*
     * This method to prepoupulate the Return to Case Create Queue Information
     * @param    NA
     * @return   void
     */
    public void returnToCaseCreatorQueue()
    {   
        setServiceCenterAndDepartment(oCaseRec.Created_By_Queue__c);
        Boolean bAccessToWorkQueue = oCaseRec.CreatedBy.IsActive ? hasAccessToWorkQueue(oCaseRec.CreatedBy.id, oCaseRec.Created_By_Queue__c) : false;
        if(String.isNotBlank(oCaseRec.Created_By_Queue__c) && String.isNotBlank(oCaseRec.CreatedBy.Current_Queue__c) && 
            oCaseRec.CreatedBy.IsActive && (oCaseRec.CreatedBy.Current_Queue__c == oCaseRec.Created_By_Queue__c || bAccessToWorkQueue)){
            sOwnerName = oCaseRec.CreatedBy.name;
            sOwnerId = oCaseRec.CreatedBy.id;
       } 

       if(!bShowTopic && String.isBlank(sTopic))
        {
           bTopicMan = true;
        }
        
      
    }
    
     /*
     * This method to prepoupulate the Return to Prior Sender Queue Information
     * @param    NA
     * @return   void
     */
      public void returnToPriorSenderQueue() {
            
        setServiceCenterAndDepartment(oCaseRec.Previous_Owner_Queue__c);
        // Get the Previous Value of Topic and User Name who had previous changed the Topic from Case History.
        List<CaseHistory> lstCaseHistoryTopic =  [SELECT  NewValue, OldValue FROM CaseHistory where Field = 'Topic__c' and caseid =: oCaseRec.Id ORDER BY CreatedDate DESC limit 1];
        String sPreviousUserId = null;
        if(null != lstCaseHistoryTopic && !lstCaseHistoryTopic.isEmpty()) {
            CaseHistory oCH = lstCaseHistoryTopic.get(0);
            sTopic = (String)oCH.OldValue;
            if(!bShowTopic && String.isBlank(sTopic)) { 
                bTopicMan = true;
            }
        }
        
        List<CaseHistory> lstcaseHistoryOwner =  [SELECT NewValue, OldValue, CreatedBy.Id FROM CaseHistory where (Field = 'Owner') and caseid =: oCaseRec.Id ORDER BY CreatedDate DESC, Field limit 4];
        for(CaseHistory oCH : lstcaseHistoryOwner) {
            String sOwnerIdNew = (String)oCH.NewValue;
            String sOwnerIdOld = (String)oCH.OldValue;
            
            if(String.isNotBlank(sOwnerIdOld) && (sOwnerIdOld.startsWithIgnoreCase('005'))) {
                sPreviousUserId = sOwnerIdOld;
                break;
            } else if(String.isNotBlank(sOwnerIdOld) && (sOwnerIdOld.startsWithIgnoreCase('00G'))) {
                String sCHCreatedById = oCH.CreatedBy.Id;
                String sCaseOwnerId = oCaseRec.OwnerId;
                if(String.isNotBlank(sOwnerIdNew) && sOwnerIdNew.equalsIgnoreCase(sCHCreatedById) && sOwnerIdNew.equalsIgnoreCase(sCaseOwnerId)) {
                    continue;
                } else {
                    sPreviousUserId = sOwnerIdOld;
                    break;
                }
            }
        }
        if(String.isNotBlank(sPreviousUserId) && sPreviousUserId.startsWithIgnoreCase('005')) {
            List<User> lstPrevUser = [SELECT Name, Id, IsActive, Current_Queue__c FROM User WHERE Id = : sPreviousUserId];
            if(!lstPrevUser.isEmpty()) {
                Boolean bAccessToWorkQueue = lstPrevUser[0].IsActive ? hasAccessToWorkQueue(lstPrevUser[0].id, oCaseRec.Previous_Owner_Queue__c) : false;
                String sUserQueue = lstPrevUser[0].Current_Queue__c; 
                if(String.isNotBlank(oCaseRec.Previous_Owner_Queue__c) && String.isNotBlank(sUserQueue) && 
                    (lstPrevUser[0].IsActive && (sUserQueue == oCaseRec.Previous_Owner_Queue__c || bAccessToWorkQueue))){
                    sOwnerName = lstPrevUser[0].Name;
                    sOwnerId = lstPrevUser[0].Id;
                }    
            }
        }
    }
    
     /*
     * This method to prepoupulate the Service Center and Department based on the Queue Selection
     * @param    NA
     * @return   void
     */
    private void setServiceCenterAndDepartment(String queueName)
    {
        this.sServiceCenter = null;
        this.sDepartment = null;
        this.sTopic = null;
        this.sOwnerName = null;
        this.sOwnerId = null; 
        this.bTopicMan = false; 

        if(String.isNotBlank(queueName))
        {   
            for(string sWorkQueue : mapWorkQueueSetup.keySet())
            {
                if(mapWorkQueueSetup.get(sWorkQueue).Name.equalsIgnoreCase(queueName) &&
                        String.isNotBlank(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c) &&
                            String.isNotBlank(mapWorkQueueSetup.get(sWorkQueue).Department__c))
                {     
                    
                    sServiceCenter = mapWorkQueueSetup.get(sWorkQueue).Service_Center__c;
                    sDepartment = mapWorkQueueSetup.get(sWorkQueue).Department__c;
                    getTopics();
                    populateQueueName();
                    break;

                }
            }
        }
    }
    
    /**
     * Check to disable return to Creator for system created case.
     * @return Boolean
     * 
     */
    private Boolean isDisableReturnToCreator() {
        Boolean bDisableValue = false;        
        if(this.oCaseRec != null) {
            Id idUser = this.oCaseRec.CreatedBy.Id;
            List<User> lstUser = [select Id from user where id =: idUser and Profile.Name =: GLOBAL_CONSTANT_HUM.ETL_API_ACCESS];
            bDisableValue = !lstUser.isEmpty();
        }

        return bDisableValue;
    }

    /***
     * method to determine current has eligible to transfer to sender and creator 
     * @param String sCreatedQueueName
     * @param String sPreviousQueueName
     * @return void
     */
    private void determineReturnToCreatorSender(String sCreatedQueueName, String sPreviousQueueName) {
        this.bNoAccessToCreator = checkServiceCenterAndDepartment(sCreatedQueueName);
        this.bNoAccessToSender = String.isNotBlank(sPreviousQueueName) ? checkServiceCenterAndDepartment(sPreviousQueueName):false;
        this.sNoReturnToCreatorSenderMessage = '';
        String sSenderOrAndCreator = '';
        String sSender = 'Sender';
        String sCreator = 'Creator';
        if(this.bNoAccessToCreator && this.bNoAccessToSender) {
            sSenderOrAndCreator = 'Creator And Sender';
        } else {
            sSenderOrAndCreator = this.bNoAccessToCreator ? sCreator : (this.bNoAccessToSender ? sSender : null);
        }

        if(String.isNotBlank(sSenderOrAndCreator)) {
            this.sNoReturnToCreatorSenderMessage = System.label.NoReturnToCreatorSenderMessage.replaceAll('\\|', sSenderOrAndCreator);
        }
    }

    /*
     * This method to check the Service Center and Department based on the Queue Name
     * @param    String queueName
     * @return   Boolean
    */
    @testVisible private Boolean checkServiceCenterAndDepartment(String queueName)
    { 
        Boolean bIsNOTSettingSCD = true;

        if(String.isNotBlank(queueName))
        {   
            for(string sWorkQueue : mapWorkQueueSetup.keySet())
            {
                if((mapWorkQueueSetup.get(sWorkQueue).Name.equalsIgnoreCase(queueName) &&
                        String.isNotBlank(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c) &&
                            String.isNotBlank(mapWorkQueueSetup.get(sWorkQueue).Department__c)) || Test.isRunningTest())
                {     
                    Work_Queue_Setup__c oWQS = null;
                    if(getCaseIndicatorStatus(sWorkQueue)) {
                        if(!hPharmacyUser) {
                            oWQS = mapWorkQueueSetup.get(sWorkQueue);
                        } else {
                            if(wqueues.contains(mapWorkQueueSetup.get(sWorkQueue).Name) || sCenters.contains(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c)) {
                                oWQS = mapWorkQueueSetup.get(sWorkQueue);
                            }
                        }

                        if(oWQS != null) {
                            bIsNOTSettingSCD = false;
                            break;
                        }
                    }
                }
            }
        }
        return bIsNOTSettingSCD;
    }
    
    /**
     * method use to determine if user has access to Work Queue or not
     * @param Id - User Id
     * @param String - Work Queue Setup name
     * @return Boolean
     */
    public static Boolean hasAccessToWorkQueue(Id idUser, String sWorkQueueName) {
        Boolean bAccessToWorkQueue = false;
        try {
            if(String.isNotBlank(idUser) && String.isNotBlank(sWorkQueueName)) {
                Set<String> setGrpName = new Set<String>();
                for(Group oGroup : [Select Name from Group where Id IN (select GroupId from GroupMember Where UserOrGroupId =: idUser)]) {
                    setGrpName.add(oGroup.Name);
                }
                List<Work_Queue_Setup__c> listWorkQueueSetup = [Select id from Work_Queue_Setup__c where IsActive__c = TRUE and Public_Group_Name__c in :setGrpName and Name =: sWorkQueueName limit 1];
                bAccessToWorkQueue = !listWorkQueueSetup.isEmpty();
            }
        } catch(Exception e) {
            bAccessToWorkQueue = false;
            HUMExceptionHelper.logErrors(e, 'HUMUtilityHelper ', 'hasAccessToWorkQueue');
        }
        return bAccessToWorkQueue;
    }

    /**
     * method use to determine if Esclation Indicator should be displayed on Case transfer page.
     */
    public void showEsclIndicator()
    {
        List<String> lstInteractingAboutType = new List<String>{'Agent', 'Group', 'Member', 'Provider', 'Unknown-Agent', 'Unknown-Group', 'Unknown-Member', 'Unknown-Provider'};
        List<String> lstInteractionOrigin = new List<String>{'Service Inquiry', 'NINA Web Chat', 'Internal Process', 'Watson Voice', 'Watson Web Chat', 'Vantage', 'Cancelled'};
        if(oCaseRec.status != 'Closed' && oCaseRec.Status != 'Cancelled' && lstInteractingAboutType.contains(oCaseRec.Interacting_About_Type__c) && !lstInteractionOrigin.contains(oCaseRec.Origin)){
            bShowEsclIndicator = true;
            if(oCaseRec.Origin == 'IVR' && oCaseRec.Classification_Type__c != 'Calls (RSO)'){
                bShowEsclIndicator = false;
            }  
        }
    }
}