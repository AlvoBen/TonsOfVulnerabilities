/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_C_AHT_HUM 
Created Date     : January 20, 2017
Function         : Handles Initialization and Routes Assignment, Saving and Support Functions for the Quick Start Console Component
Test Class       : QuickStart_T_AHT_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
* Cody A. Sanders         -----                 03/10/2017                 Quick Start MVP - Associate to Policy, Display Essential Fields, Open Case Edit and Case Comment Transfer
* Cody A. Sanders         -----                 03/24/2017                 Included additional Work Queue related fields
* Cody A. Sanders         -----                 03/27/2017                 Updated page messaging to account for error types
* Cody A. Sanders         -----                 04/26/2017                 Updated for new CTCI model and added Associate to Account and Mentor Document functionality
* Piyusha Babar           -----                 07/25/2017                 REQ - 323539 -Ability of a quick start to allow creation of multiple cases with similar Claim Classification on the current interaction
* Subhamay Mandal         -----                 07/31/2017                 REQ - 325458 Ability of a quick start page to be responsive when selecting top 5 And non top 5 classification And Intent (RF)
* Subhamay Mandal         -----                 03/08/2017                 REQ - 324829 Ability of quick start to display Validation errors on quick start page as per CRM Standards (RF)
* Amar Gadewar                                  08/09/2017                 REQ - 323549 - Error Handling for Quick Start
* Amar Gadewar                                  08/15/2017                 Defect fix - Removing the Default Inbound Call as Origin
* Amar Gadewar                                  08/23/2017                 Defect fix on Quick Start for Medicare Call Part C or Part D
* Siddharth Mehta                               08/24/2017                 REQ - 327581 Ability of a Quick Start to give error message when queue is not selected (RF)
* Piyusha Babar                                 08/28/2017                 REQ - 327583 Ability of a quick start to create a case on an existing interaction created by someone else(RF)
* Subhamay Mandal                               10/02/2017                 REQ - 329803 Ability to prefill the quick start case comment fields based on the Classification And Intent selection
* Subhamay Mandal                               12/28/2017                 REQ - 350689 Pretext Content updates (RF)
* Subhamay Mandal                               01/12/2018                 REQ - 350392 - Business Configuration model for Quick Start pretext (RF)
* Asish Behera                                  01/17/2018                 REQ - 350517 Adding reference document in QS window.
* Subhamay Mandal                               02/20/2018                 REQ - 354335 Ability in Quick Start to allow CCS reps to create multiple cases on the single interaction for certain Classification and Intent (RF)
* Subhamay Mandal                               03/20/2018                 REQ - 357688 Technical Story: RF Quick Start White Screen Issue - (RF) and REQ - 357686 Update QuickStart window UI (RF)
* Darshan Sharma                                04/05/2018                 REQ - 361047 - Ability to update Mentor Links in QuickStart via Business Configuration
* Subhamay Mandal                               05/01/2018                 Fix for medium security vulnerabilities from checkmarx
* Darshan Sharma                                05/02/2018                 REQ - 361223 Ability to update Mentor Links in QuickStart via Business Configuration (CRM)
* Subhamay Mandal                               05/10/2018                 REQ - 361223 update NON AHT reference with AHT reference
* Subhamay Mandal                               05/16/2018                 REQ - 361876 AHTQuality - Preset the Complaint Fields
* Subhamay Mandal                               06/27/2018                 REQ - 367101 AHT - Add Frequently Used Classification and Intent to Classification Dropdown (RF)
* Darshan Sharma                                07/12/2018                 REQ - 369793 Quality - Ability to display the AutoLogging Message for additional Classification and Intents (RF+CRM)
* Darshan Sharma                                07/20/2018                 REQ - 370598 Quality - Ability to disable the Case Creation in QS if the user does not have permissions to Create a Case for the Member in CRM (RF)
* Darshan Sharma                                07/27/2018                 REQ - 371255 Quality - Disable Frequently Used C&I Header and All Classifications headers from being selected in Classification Dropdown in QS (RF)
* Subhamay Mandal                               09/04/2018                 REQ - 372704 Quality - Ability to Identify if a Case with a Classification was created within the Past 14 Days in QS. (RF)
* Subhamay Mandal                               09/04/2018                 REQ - 371313 PCC - Update PCC Rules within QuickStart (RF)- Remove Ghost Call chk box.
* Subhamay Mandal                               09/14/2018                 REQ - 371313 PCC - Adjust backend code to support PCC, RF and Non Rf users in QS
* Subhamay Mandal                               09/20/2018                 REQ - 372704 PCC - Bypass this functionality for Provider user
* Darshan Sharma                                10/01/2018                 REQ - 374820: CTCI Model Update (RF+CRM)
* Asish   Behera                                09/17/2018                 REQ - 375046 Quality - Ability to display an Info Message at the top of QS if the Work Queue does not have Pretext (RF+CRM)
* Subhamay Mandal                               11/29/2018                 REQ - 379745 Quality - Quality - Reset Mentor Documents when Intent is set to None (RF+CRM)
* Darshan Sharma                                11/30/2018                 REQ - 378965 PCC - Mentor Documents Set 1 (RF)
* Amar Gadewar                                  01/22/2019                 REQ - 383509 SF - TECH - Update PCC Quick Start Rules without 655 Permission Set. (RF)
* Subhamay Mandal                               01/23/2019                 REQ - 383554 User Story - Track usage of associate resolution (RF).  
* Subhamay Mandal                               01/23/2019                 REQ - 383554 User Story - Update logic to exclude PCC user.
* Darshan Sharma                                01/17/2019                 REQ - 383747 Quality - Update Quick Start+ Label (RF)
* Amar Gadewar                                  02/22/2019                 REQ - 383928 - SF - TECH - Clean Up AHT Components , User Voice and Quick Start Enhancement (RF)
* Subhamay Mandal                               05/22/2019                 REQ - 397111 Quality - OGO - Display Soft Warning Messages in QuickStart (RF)    
* Subhamay Mandal                               05/23/2019                 REQ - 397111 Code cleanup and improve performance
* Subhamay Mandal                               05/30/2019                 REQ - 387576, REQ - 387577, REQ - 388197 - OGO stories on Compplaint and GnA Rights Given on QS
* Subhamay Mandal                               06/14/2019                 REQ - 402004 SF - TECH - Quick Start Performance Enhancements (RF)
* Subhamay Mandal                               06/25/2019                 REQ - 402804 Quality - Add Informational Message above Callback Number (RF)
* Prudhvi Pamarthi                              07/15/2019                 REQ - 396335
* Rakesh Patni                                  08/20/2019                 REQ - 406104. Launch Compass Funcitonality
* Melkisan Selvaraj                             11/14/2019                 REQ - 409548.
* Subhamay Mandal                               01/28/2020                 731580|729526|729566 PR00091574 - Code merged for 2/28
* Amar Gadewar                                  02/04/2020                 USER STORY 731580 - PR00091574 - MF 1 - Tech - Quick Start Performance Enhancements (CRM)
* Subhamay Mandal                               03/15/2020                 938292 QS 3.0 - AHT - Ability to create task from Quick Start (RF)
* Santhi Mandava                                05/02/2020                 User Story 938955: PR00091574 - Update Case Information Section to display "What is Call About" 
                                                                            information instead of Classification and Intent fields (RF)
* Subhamay Mandal                               05/22/2020                 1118384 PR00091574 - MF 1 - Quality - Display Classification and Intent combination in Bold in "What is the call about?" field on Quick Start (RF)
* Amar Gadewar                                  06/18/2020                 User Story 1203874: PR00091574 - MF 1 - RF Clean up (RF)
* Amar Gadewar                                  06/18/2020                 User Story 1198471: PR00091574 - MF 1 - SF - TECH - QS 3.0 Issue Fixes and Code improvements (RF)
* Amar Gadewar                                  07/05/2020                 User Story 1247438: PR00091574 - MF 1 - SF - TECH - QS 3.0 Code Improvement and Knowledge cleanup (RF)
* Kavya Bondada                                 08/03/2020                 User Story 1318072: PR00091574 - MF 1 - INC0239694 Fix - Task should not be created from Quick Start unless Create Task button is checked (CRM)
* Amar Gadewar                                  08/28/2020                 User Story 1386444: PR00091574 - MF 1 - Rollback "What is the Call About?" feature in Quick Start (RF)
* Samantha Tennyson                             12/28/2020                 User Story 1745934: Prevent Complaint/G&A Rights Given fields to be selected on Cases with specific Classification and Intent Combinations (CRM)
* Samantha Tennyson								04/01/2021		    	   User Story 1986952: T1PRJ0001827 - MF 1 - AHT/Quality - QS 3.0 - Ability to display Pretext in Grievance and Appeals section based on Grievance/Appeal Reason selection (RF)
* Samantha Tennyson				   				06/10/2021	               User Story 2372808: T1PRJ0001827 - MF 1 - Quality/FCR - Ability to display "Recommended Actions" in Quick Start (RF)
* Amar Gadewar                                  06/28/2021                 User Story 2406575: T1PRJ0001827 - MF 1 - AHT/Quality - Ability to display alert in Quick Start if the Rep is creating a case with potential duplicate grievance (RF)
* Sunil Kumar Reddy Y                           07/26/2021                 User Story 2498928: T1PRJ0001827 - MF 1 - AHT/Quality - Display Informational Message when Case is available in History with Same Classification (RF)
* Jayesh Solanki				                08/30/2021		           User Story 2696518: T1PRJ0001827 - MF 1 - AHT/Quality - Highlight fields with errors in Quick Start (RF)
* Kavya Bondada				                    10/11/2021		           User Story 2737375: T1PRJ0001827 - MF 1 - Quality - Ability to capture Verbal Consent Obtained and making it required for certain case types (RF)
************************************************************************************************************************************************************************************/
public with sharing class QuickStart_C_AHT_HUM 
{  
    public static final List<String> lstComplaintsYes =  new List<String> {'Yes - Medicare Part C','Yes - Medicare Part D','Yes - Medicaid'};
    /*
    * Constructor
    * 
    * @see Quick Start form with Classification dropdown values
    */
    public QuickStart_C_AHT_HUM()
    {
        this.oNewCase = new Case();
        
        this.bErrorNoSave = false;
        this.bErrorDuplicClassIntent = false;
        this.bShowCaseCommentPanel = true;
        this.bRecordAccess = true;

        this.sUserQueue = QuickStart_S_AHT_HUM.evaluateUserQueue();
        this.bProviderUser = false;
        this.bAllowMultipleCase = false;
        this.bDisplayRecommendedActionsIcon = false;
        
        this.bNeverComplaint = false; 
        this.bNeverGA = false; 

        this.bShowDuplicateGrievance = false;
        this.bShowDupClassification = false;
        
        this.isPolicyProductType = false;
       
        if (String.isNotEmpty(this.sUserQueue.Current_Queue__c))
        {
            this.oUserWorkQueue = QuickStart_S_AHT_HUM.populateUserQueue(sUserQueue.Current_Queue__c);
            idClassificationType = this.oUserWorkQueue.Classification_Type__c;

            String sBusinessGroup = QuickStart_S_AHT_HUM.checkPreTextAndBusinessGroup(idClassificationType);
           
            if(null != sBusinessGroup)
            {
                bProviderUser = (sBusinessGroup.contains('Provider'));
                bPreTextConfigured = true;
                this.sQSLabel = Constants_C_AHT_HUM.QUICKSTARTLABEL + ' ' +sBusinessGroup;
                
            } else {
                bPreTextConfigured = false;
                this.sQSLabel = Constants_C_AHT_HUM.QUICKSTARTLABEL;
            }
            
            this.mapQueueCI = QuickStart_S_AHT_HUM.getQueueClassificationIntent(this.oUserWorkQueue.Classification_Type__c);
            this.lstFrequentlyUserCIOption = QuickStart_S_AHT_HUM.getFrequentlyUsedCI(idClassificationType);
            this.mapCIMentor = QuickStart_S_AHT_HUM.fetchAllMentorLink(idClassificationType,sBusinessGroup);
            this.lstRFDocumentLink = this.mapCIMentor.get(Constants_C_AHT_HUM.REFERENCE_DOCUMENT_CI+'-'+Constants_C_AHT_HUM.REFERENCE_DOCUMENT_CI);
            
        }
        bHasHPPermSet = false;
        bIsHPCase = false;
        bHasHPPermSet = HUMUtilityHelper.hasUserPharmacy206Access();
        this.bCreateTask = false;
        this.sTaskDueDate = null;
        this.oNewTask = new Task(RecordTypeId = Constants_C_AHT_HUM.RECORDTYPE_ID_NOTIFICATION_TASK, Type = Constants_C_AHT_HUM.TASK_NOTIFICATION_TYPE);
    }
    
    public Id idClassificationType{get;set;}
  @testVisible   private Map<String,List<CustomLink__c>> mapCIMentor{get;set;}
  @testVisible   private Map<String, QuickStartCI_DTO_AHT_HUM> mapQueueCI{get;set;}
  @testVisible   private Work_Queue_Setup__c oUserWorkQueue{get;set;}
  @testVisible   private User sUserQueue{get;set;}
  @testVisible   private Boolean bAllowMultipleCase{get;set;}
  @testVisible   private List<SelectOption> lstFrequentlyUserCIOption{get;set;}
    
    //HP Case Creation Functionality
    @testVisible private boolean bHasHPPermSet;
    public boolean isPolicyProductType{get;set;}
    
    public Case oNewCase {get;set;}
    public Task oNewTask {get;set;}
    public String sTaskDueDate{get; set;}
    public Boolean bCreateTask{get; set;}
    
    public Boolean bProviderUser{get; set;}
    
    public boolean bErrorNoSave {get;set;}
    public boolean bErrorDuplicClassIntent {get;set;}
    public string  sErrorMessage {get;set;}
    
    public string sSavedCaseId {get;set;}    
    public string sSavedTaskId {get;set;}
    
    public string sClassificationSelect {get;set;}
    public string sIntentSelect {get;set;}

    public id sFocusedObjId {get;set;}
    public String sInteractionId {get;set;}
    
    public boolean bShowDocumentPanel {get;set;}
    public List<CustomLink__c> lstDocumentLink {get;set;}
    public List<CustomLink__c> lstRFDocumentLink {get;set;}
    
    
    public Boolean bShowCaseCommentPanel{get; set;}
    public String sPreTextdata{get;set;}

    public String sInformationalMessage{get; set;}
    public String sSoftWarningMessage{get; set;}
    public String sOGOFieldSetup{get; set;}
    public String sHeaderMessageType{get; set;}
    public String sHeaderMessage{get; set;}
    public Boolean bNeverComplaint {get; set;}
    public Boolean bNeverGA {get; set;}
    
    public List<String> lstqspRecActions {get; set;}
    public Map<String,List<String>> mapRAiconImages{get;set;}
    public Boolean bDisplayRecommendedActionsIcon {get; set;}
    
    public Boolean bRecordAccess {get; set;}
    public Boolean bPreTextConfigured {get; set;}
 
    public String sQSLabel {get;set;}
    
    public Boolean bIsHPCase{get;set;}
    
    public String sPretextGAJSON{get;set;}
    public String sOGOFieldValue{get;set;}
    public String sOGOReasonValue{get;set;}

    public String sAccountId{get;set;}
    public Boolean bShowDuplicateGrievance{get;set;}
    public Boolean bShowDupClassification {get;set;}
    public List<DuplicatecaseWrapper> listOfClassObj{get;set;}
    public List<DuplicatecaseWrapper> lstDupClassificationCases{get;set;}
    /*
    * Get Classification dropdown options by User's Classification Type
    * 
    * @return List<SelectOption> with Classifications
    * @see Classification dropdown options
    */  
    public List<SelectOption> getLstClassificationOptions()
    {
        this.bDisplayRecommendedActionsIcon = false;
        List<SelectOption> lstClassificationOptions = new List<SelectOption>();
        lstClassificationOptions.add(new SelectOption(HUMConstants.HUMNone, HUMConstants.HUMNoneDisplay));
        if(!this.bProviderUser && null != lstFrequentlyUserCIOption && !lstFrequentlyUserCIOption.isEmpty()) {
            lstClassificationOptions.add(new SelectOption('', Constants_C_AHT_HUM.SELECT_FREQUENTLY_CI, true));         
            
            lstClassificationOptions.addAll(lstFrequentlyUserCIOption);
            
            lstClassificationOptions.add(new SelectOption('', Constants_C_AHT_HUM.SELECT_ALL_CLASSIFICATIONS, true));
        }
        
        if(null != mapQueueCI) {
            
            for(String sKeyClassification : this.mapQueueCI.keySet())
            {
                QuickStartCI_DTO_AHT_HUM oClassificationIntent = this.mapQueueCI.get(sKeyClassification);
                lstClassificationOptions.add(new SelectOption(sKeyClassification, oClassificationIntent.sClassificationLabel));
            }
            
        }
        
        

        return lstClassificationOptions;
    }
    
    /*
    * Gets Intent dropdown options by selected Classification
    * 
    * @return List<SelectOption> with Intents
    * @see Intent dropdown options
    */
    public List<SelectOption> getLstIntentOptions()
    {
        this.sIntentSelect = null;
        List<SelectOption> lstIntentOptions = new List<SelectOption>();
        lstIntentOptions.add(new SelectOption(HUMConstants.HUMNone, HUMConstants.HUMNoneDisplay));
        if(String.isNotBlank(this.sClassificationSelect) && this.sClassificationSelect.contains(HUMConstants.sHiphan)) {
            String[] arrCI = this.sClassificationSelect.split(HUMConstants.sHiphan);
            this.sClassificationSelect = arrCI[0];
            this.sIntentSelect = arrCI[1];
        }
        
        if(null != mapQueueCI&& this.mapQueueCI.containsKey(this.sClassificationSelect)) {
            QuickStartCI_DTO_AHT_HUM oClassificationIntent = this.mapQueueCI.get(this.sClassificationSelect);
            if(oClassificationIntent != null) {
                for(String sKeyIntent : oClassificationIntent.mapIntentInfo.keySet())   {
                    lstIntentOptions.add(new SelectOption(sKeyIntent, oClassificationIntent.mapIntentInfo.get(sKeyIntent).sIntentLabel));
                }
            }           
        }
        
        return lstIntentOptions;
    }
    /*
    * Gets Verbal Consent Obtained  dropdown options 
    * 
    * @return List<SelectOption> with Verbal Consent Obtained
    * @see Verbal Consent Obtained dropdown options
    */
    public List<SelectOption> getlstVerbalConsentOptions()
    {
        List<Schema.PicklistEntry> lstVerbalConsentPicklist = Case.Verbal_Consent_Obtained__c.getDescribe().getPicklistValues();
        List<SelectOption> lstVerbalConsentOptions = New List<SelectOption>();
        for (Schema.PicklistEntry p : lstVerbalConsentPicklist)
        {
            
                lstVerbalConsentOptions.add(New SelectOption(p.getValue(), p.getLabel()));
        }
        return lstVerbalConsentOptions;
    }


    /*
    * Assigns the Case values, inserts the Case and provides the values needed by JavaScript to open the Case Edit page
    * 
    * @see After this method executes successfully, the Case Edit page will open and Quick Start will reset
    */
    public void assignCaseValues()
    {
        resetFlags();
        try
        {
            this.bRecordAccess = QuickStart_S_AHT_HUM.fetchRecordAccess(this.sFocusedObjId);

            if(this.bRecordAccess)
            {
                if(null != mapQueueCI && 
                    String.isNotBlank(sClassificationSelect) && sClassificationSelect != HUMConstants.HUMNone  && 
                        String.isNotBlank(sIntentSelect) && sIntentSelect != HUMConstants.HUMNone &&
                            mapQueueCI.containsKey(sClassificationSelect)) 
                {
                
                    QuickStartCI_DTO_AHT_HUM selectedClassification = this.mapQueueCI.get(sClassificationSelect);

                    if(null != selectedClassification && String.isNotBlank(selectedClassification.sClassificationLabel))
                    {    
                        
                        String sClassification = selectedClassification.sClassificationLabel;
                        Map <Id, QuickStartCI_DTO_AHT_HUM.Intent_DTO_AHT_HUM> mapSelectClassIntentInfo = selectedClassification.mapIntentInfo;

                        if(null !=  mapSelectClassIntentInfo && mapSelectClassIntentInfo.containsKey(sIntentSelect)) 
                        {

                            QuickStartCI_DTO_AHT_HUM.Intent_DTO_AHT_HUM selectedIntent = mapSelectClassIntentInfo.get(sIntentSelect);

                            if(null != selectedIntent && String.isNotBlank(selectedIntent.sIntentLabel) && String.isNotBlank(selectedIntent.idCTCI))
                            {
                                String sIntent = selectedIntent.sIntentLabel;
                                String sCTCIId = selectedIntent.idCTCI;
                                if(bHasHPPermSet){
                                    bIsHPCase = HUMUtilityHelper.isHPClassificationIntent(sClassification, sIntent);
                                }
                                resetFlags();
                                assignGeneralValues(sCTCIId);
                                assignValuesByObject();
                                runSaveRules(sClassification, sIntent);
                                saveCase();
                                saveTask();
                            } else {
                                String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                                this.sErrorMessage =  'Quick Start Intent Label not found for selected  Classification  :' +sClassification + ' &  Intent :' +sIntentSelect +  ' Classification Type : ' +  sClassificationName;
                                this.bErrorNoSave = true;
                                throw new QuickStartCustomException(sErrorMessage);
                            }
                        } else {
                            String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                            this.sErrorMessage =  'Quick Start Intent Map not Found for  Classification  :' +sClassification + ' Classification Type : ' +  sClassificationName;
                            this.bErrorNoSave = true;
                            throw new QuickStartCustomException(sErrorMessage);
                        }
                    } else {
                        String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                        this.sErrorMessage =  'Quick Start Label / Name not found for Classification  :'+sClassificationSelect + ' Classification Type : ' +  sClassificationName;
                        this.bErrorNoSave = true;
                        throw new QuickStartCustomException(sErrorMessage);
                    }
                   
                } else {
                   String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                   this.sErrorMessage =  'Quick Start Map Queue is NULL or Invalid selected Classification  :'+sClassificationSelect +' &  Intent :'+sIntentSelect + ' Classification Type : ' +  sClassificationName;
                    this.bErrorNoSave = true;
                    throw new QuickStartCustomException(sErrorMessage);
                }
            }
            else
            {
                this.sErrorMessage =  Label.QUICKSTART_PERMISSIONMSG_HUM;
                this.bErrorNoSave = true;
            }
        }
        catch(Exception exceptionToLog)
        {
            bErrorNoSave = true;

            String sExceptionType = exceptionToLog.getTypeName();
        
            if (sExceptionType.equalsIgnoreCase(HUMConstants__c.getInstance('CUSTOM_EXCEPTION').StringValue__c)) 
            {
                HUMCustomException customException = (HUMCustomException) exceptionToLog;
                this.sErrorMessage = exceptionToLog.getMessage();
            } else {
                HUMExceptionHelper.logErrors(exceptionToLog, 'QuickStart_C_AHT_HUM', 'assignCaseValues');
            } 
        } 
    }
    /*
    * Resets error flags
    * 
    */  
    private void resetFlags()
    {
        this.bErrorNoSave = false;
        this.sErrorMessage = '';
        this.bErrorDuplicClassIntent = false;
        
        this.oNewCase.Id = null;
        this.oNewCase.Policy_Member__c = null;
        this.oNewCase.AccountId = null;

        this.oNewTask.Id = null;
        this.oNewTask.OwnerID = null;
        this.oNewTask.Task_Owner__c = null;
        this.sSavedTaskId=null;
    }
    
    /*
    * Performs general Case field assignments
    * 
    */
    private void assignGeneralValues(String sCTCIId)
    {
        QuickStart_S_AHT_HUM.assignCaseDefaults(this.oNewCase, this.sUserQueue.Current_Queue__c);
        QuickStart_S_AHT_HUM.assignCaseQueue(this.oNewCase, this.oUserWorkQueue);
        QuickStart_S_AHT_HUM.assignCaseClassificationIntent(this.oNewCase, this.sClassificationSelect, this.sIntentSelect, sCTCIId);
    }
    
    /*
    * Determine the object being association with and routing Case assignment accordingly
    * 
    */
    private void assignValuesByObject()
    {   
        String sSaveObjectType = sFocusedObjId.getSobjectType().getDescribe().getName();
        
        if(sSaveObjectType == HUMConstants.OBJECT_POLICYMEMBER) {
            QuickStart_S_AHT_HUM.assignPolicyMemberValues(this.oNewCase, this.sFocusedObjId, this.sInteractionId, bIsHPCase);
        } else if(sSaveObjectType == HUMConstants.OBJECT_ACCOUNT) {
            QuickStart_S_AHT_HUM.assignAccountValues(this.oNewCase, this.sFocusedObjId, this.sInteractionId, bIsHPCase);
        }
    }
    
    /*
    * Executes Quick Start specific server-side validation before inserting the Case
    * 
    */
    private void runSaveRules(String sClassification, String sIntent)
    {
        Boolean bisClaimsCall = Constants_C_AHT_HUM.CLAIM_CLASSIFICATION.equalsIgnoreCase(sClassification);
        
        Boolean bAllowMultipleCase = this.bAllowMultipleCase;
              
        if(!bisClaimsCall && !bAllowMultipleCase) {
            this.bErrorDuplicClassIntent = QuickStart_S_AHT_HUM.verifyNoDuplicClassIntent(this.oNewCase);
        }  
    }       

    /*
    * Inserts the Case and provides the Case ID for JavaScript to open Case Edit
    * 
    * @see Success: the Case Edit page opens for the new Case; Failure: Quick Start displays a page message
    */ 
    private void saveCase()
    {   
        if(!bErrorDuplicClassIntent)
        {
           
            this.sErrorMessage = QuickStart_S_AHT_HUM.insertNewCase(this.oNewCase);
            this.bErrorNoSave = String.isNotEmpty(this.sErrorMessage);
            if(!this.bErrorNoSave) {
                this.sSavedCaseId = this.oNewCase.Id;
            }
        }
    }
    
    /*
    * Inserts the Task and provides the Case ID for JavaScript to open Task Edit(lazy load)
    * 
    * @see Success: the Task Edit page opens for the new Task; Failure: 
    */
    @testVisible
    private void saveTask() {
        if(!this.bProviderUser && this.bCreateTask && String.isNotBlank(this.sSavedCaseId)) {
            QuickStart_S_AHT_HUM.assginTaskDefaults(this.oNewTask, this.sSavedCaseId, this.sTaskDueDate, this.oUserWorkQueue, this.sUserQueue);
            this.sErrorMessage = QuickStart_S_AHT_HUM.insertNewTask(this.oNewTask);
            this.bErrorNoSave = String.isNotEmpty(this.sErrorMessage);
            if(!this.bErrorNoSave) {
                this.sSavedTaskId = this.oNewTask.Id;
            }
        }
    }
    /*
    * Resets the Quick Start page
    * 
    * @return PageReference of new page
    * @see Quick Start refreshes
    */
    public PageReference reset()
    {
        PageReference oNewPage = new PageReference(system.currentPageReference().getURL());
        oNewPage.setRedirect(true);
        return oNewPage;
    } 

    /*
    * Populates the Panel of Document Links by Classification and Intent
    * 
    * @see The Related Documents panel may appear
    */
    public void populateDocumentLinks()
    {
        try 
        {
            resetFlags();
            assignedDefaultValues();
            
            if(null != mapQueueCI && 
                String.isNotBlank(sClassificationSelect) && sClassificationSelect != HUMConstants.HUMNone  && 
                String.isNotBlank(sIntentSelect) && sIntentSelect != HUMConstants.HUMNone &&
                mapQueueCI.containsKey(sClassificationSelect)) 
            {
    
                QuickStartCI_DTO_AHT_HUM selectedClassification = this.mapQueueCI.get(sClassificationSelect);
    
                if(null != selectedClassification && String.isNotBlank(selectedClassification.sClassificationLabel))
                {    
                    String sClassification = selectedClassification.sClassificationLabel;
                    Map <Id, QuickStartCI_DTO_AHT_HUM.Intent_DTO_AHT_HUM> mapSelectClassIntentInfo = selectedClassification.mapIntentInfo;
    
                    if(null !=  mapSelectClassIntentInfo && mapSelectClassIntentInfo.containsKey(sIntentSelect)) 
                    {
                        QuickStartCI_DTO_AHT_HUM.Intent_DTO_AHT_HUM selectedIntent = mapSelectClassIntentInfo.get(sIntentSelect);
                        if(null != selectedIntent && String.isNotBlank(selectedIntent.sIntentLabel) && String.isNotBlank(selectedIntent.idCTCI))
                        {
                            String sIntent = selectedIntent.sIntentLabel;

                            if(null != mapCIMentor &&  mapCIMentor.containskey(sClassification+'-'+sIntent)) {
                                    
                                this.lstDocumentLink = this.mapCIMentor.get(sClassification+'-'+sIntent);
                                this.bShowDocumentPanel = !this.lstDocumentLink.isEmpty();
                            }
                    
                            // check for pretext data
                            this.sPreTextdata = QuickStart_S_AHT_HUM.fetchQSPretextData(sClassification, sIntent, idClassificationType);
                            this.bShowCaseCommentPanel = String.isBlank(sPreTextdata);
                    
                            // OGO setup data
                            QuickStartCI_DTO_AHT_HUM.AdditionalInfo_DTO_AHT_HUM oAdditionalInfo = QuickStart_S_AHT_HUM.fetchQSPAdditionalInfo(sClassification, sIntent, idClassificationType);
                    
                            if(null != oAdditionalInfo) 
                            {
                                this.sInformationalMessage = oAdditionalInfo.sInformationalMessage;
                                this.sSoftWarningMessage = oAdditionalInfo.sSoftWarningMessage;
                                this.sOGOFieldSetup = oAdditionalInfo.sOGOFieldSetup;
                                this.bAllowMultipleCase = oAdditionalInfo.bAllowMultipleCase;
                                this.sHeaderMessageType = oAdditionalInfo.sHeaderMessageType;
                                this.sHeaderMessage = oAdditionalInfo.sHeaderMessage;
                                this.bNeverComplaint = oAdditionalInfo.bNeverComplaint; 
                                this.bNeverGA = oAdditionalInfo.bNeverGA; 
                                
								If(String.isNotBlank(oAdditionalInfo.sRecommendActions)) {
									oAdditionalInfo.sRecommendActions = oAdditionalInfo.sRecommendActions.replaceAll('; ',';');
									lstqspRecActions = oAdditionalInfo.sRecommendActions.split(';');
									mapRAiconImages = QuickStart_S_AHT_HUM.getRecommendDocumentDetails(lstqspRecActions);
									
									if(!mapRAiconImages.isEmpty()){
										this.bDisplayRecommendedActionsIcon = true;
									}
								}
                            }
                            if(bHasHPPermSet)
                            {
                                bIsHPCase = HUMUtilityHelper.isHPClassificationIntent(sClassification, sIntent);
                            }
                        } 
                        else 
                        {
                            String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                            this.sErrorMessage =  'Quick Start Intent Label not found for selected  Classification  :'+sClassification +' &  Intent :'+sIntentSelect + ' Classification Type : ' +  sClassificationName;
                           
                            throw new QuickStartCustomException(sErrorMessage);
                        }
                    } 
                    else 
                    {
                        String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                        this.sErrorMessage =  'Quick Start Intent Map not Found for  Classification  :'+sClassification +' Classification Type : ' +  sClassificationName;
                        
                        throw new QuickStartCustomException(sErrorMessage);
                    }
                } 
                else 
                {
                    String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                    this.sErrorMessage =  'Quick Start Label / Name not found for Classification  :'+sClassificationSelect +' Classification Type : ' +  sClassificationName;
    
                    throw new QuickStartCustomException(sErrorMessage);
                }                
            }
            
        } catch(Exception exceptionToLog) {
        
             HUMExceptionHelper.logErrors(exceptionToLog, 'QuickStart_C_AHT_HUM', 'populateDocumentLinks');
             
        }
    }
    /**
    * Assigned default values to attributes which are used in VF page interaction
    * @return void  
    */
    private void assignedDefaultValues() {
        this.lstDocumentLink = null;
        this.bShowDocumentPanel = false;
        this.sPreTextdata = null;
        this.bShowCaseCommentPanel = true;
        this.bAllowMultipleCase = false;
        this.sInformationalMessage = null;
        this.sSoftWarningMessage = null;
        this.sOGOFieldSetup = null;
        this.sHeaderMessageType = null;
        this.sHeaderMessage = null;
        this.bNeverComplaint = false; 
        this.bNeverGA = false; 
        this.bDisplayRecommendedActionsIcon = false;
        this.bShowDuplicateGrievance = false;
        this.bShowDupClassification = false;
	    this.isPolicyProductType = false;
    }
    
    /**
    * method to retrieve pretext data for GA section
    * return void
    */
    public void getAFPretextGA() {
        try { 
            if(String.isNotBlank(sOGOFieldValue) && String.isNotBlank(sOGOReasonValue))
        		sPretextGAJSON = QuickStart_S_AHT_HUM.getPretextGAJSON(sOGOFieldValue, sOGOReasonValue);
        }catch(Exception exceptionToLog) {
             HUMExceptionHelper.logErrors(exceptionToLog, 'QuickStart_C_AHT_HUM', 'getAFPretextGA');    
        }
    }

    /**
    *  Check if this is duplicate Greivance submitted past 30 days
    * return void
    */
    public void checkDuplicateGrievance(){
        try { 
            this.bShowDuplicateGrievance = false;
            if(String.isNotBlank(this.sAccountId) && String.isNotBlank(this.sClassificationSelect) 
            && String.isNotBlank(this.sIntentSelect) && String.isNotBlank(this.oNewCase.Complaint__c)
                && lstComplaintsYes.contains(this.oNewCase.Complaint__c)) {
                
                List<Case> lstCases = QuickStart_S_AHT_HUM.verifyNoDuplicateGrievance(this.sAccountId,this.sClassificationSelect,this.sIntentSelect,this.oNewCase.Complaint__c);
                listOfClassObj = assignWrapperData(lstCases);
                }
            
            if(!listOfClassObj.isEmpty()){
                this.bShowDuplicateGrievance = true;
            } 
            
        }catch(Exception e) {
            HUMExceptionHelper.logErrors(e, 'QuickStart_C_AHT_HUM', 'checkDuplicateGrievance');    
       }
        
    }

    public void checkDuplicateClassification(){
        try{
            this.bShowDupClassification = false;
            if(String.isNotBlank(this.sAccountId) && String.isNotBlank(this.sClassificationSelect)){
                List<Case> lstCases = QuickStart_S_AHT_HUM.verifyNoDuplicateClassification(this.sAccountId,this.sClassificationSelect);
                this.lstDupClassificationCases =  assignWrapperData(lstCases);
            }
            if(!lstDupClassificationCases.isEmpty()){
                this.bShowDupClassification = true;
            } 
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'QuickStart_C_AHT_HUM', 'checkDuplicateClassification');    
        }
    }

    
    /* Check the Complaint value is matching with Mediacare part C and Part D field 
     * along with Policy and Product Type*/
     public void fetchPolicyMember(){
        try{
            this.isPolicyProductType = false;
            String complaintVal = this.oNewCase.Complaint__c;
            isPolicyProductType = QuickStart_S_AHT_HUM.verifyPolicyProduct(this.sIntentSelect,complaintVal);
            }
        catch(Exception e){
	    HUMExceptionHelper.logErrors(e, 'QuickStart_C_AHT_HUM', 'fetchPolicyMember');    
     	}
     }
    
    
    
     /*
    * This is used generate comma seperated case numbers
    * @return List<DuplicateCaseWrapper>>
    */ 
    public static List<DuplicatecaseWrapper> assignWrapperData(List<Case> lstCases){
        List<DuplicatecaseWrapper> lstWrapData = new List<DuplicatecaseWrapper>();
            if(!lstCases.isEmpty()) {
                Integer totalCaseNumber = lstCases.size();
                Integer count = 0;
                for(Case c : lstCases){
                    count += 1;
                    DuplicatecaseWrapper wrap = new DuplicatecaseWrapper();
                    wrap.Title = totalCaseNumber == count ? c.CaseNumber : c.CaseNumber+',';
                    wrap.Id = c.Id;
                    lstWrapData.add(wrap);
                }
            }
        return lstWrapData;
    }
    
    public class QuickStartCustomException extends Exception { }
}