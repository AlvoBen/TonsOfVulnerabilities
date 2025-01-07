/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_C_HUM.cls 
Created Date     : June 22, 2017
Function         : Executes SOQL in support of the Quick Start Console Component
Test Class       : QuickStart_T_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
* Amar Gadewar            -----                 06/22/2017                 Original Version
* Piyusha Babar                            		07/25/2017                 REQ - 323539 -Ability of a quick start to allow creation of multiple cases with similar Claim Classification on the current interaction
* Amar Gadewar									08/09/2017				   REQ - 323549 - Error Handling for Quick Start
* Amar Gadewar									08/15/2017				   Defect fix - Removing the Default Inbound Call as Origin
* Amar Gadewar									08/23/2017				   Defect fix on Quick Start for Medicare Call Part C or Part D
* Subhamay Mandal          165717               11/09/2017                 REQ - 335028 Ability to prefill the quick start case comment fields based on the Classification and Intent selection (CRM)
* Amar Gadewar									01/29/2018				   Fixing the attempt to de-reference a null object
* Amar Gadewar			   188566				02/23/2018				   REQ - 350501 - Error Message on Quick Start
* Subhamay Mandal		 					    02/25/2018 				   REQ - 351011 Ability to use Business Configuration model for Quick Start pretext for making changes within Quick Start pre-text (CRM)
* Subhamay Mandal		   					  	03/01/2018				   REQ - 355820 Ability in Quick Start to allow CCS reps to create multiple cases on the single interaction for certain Classification and Intent (CRM)
* Dinesh Subramaniyan							03/08/2018				   REQ - 357443	Ability in Quick Start to access Reference Documents (CRM)
* Subhamay Mandal		   					  	04/06/2018				   REQ - 359992 and REQ - 360019. UI change(plus and logo) and Performace improvement
* Darshan Sharma		   					  	04/27/2018				   REQ - 361223 Ability to update Mentor Links in QuickStart via Business Configuration (CRM)
* Subhamay Mandal							    05/01/2018				   Fix for medium security vulnerabilities from checkmarx
* Subhamay Mandal							    05/17/2018				   REQ - 363405 SPLUNK Logs custom field validation error in Quick Start 
* Darshan Sharma			                    10/01/2018		           REQ - 374820: CTCI Model Update (RF+CRM)
* Asish   Behera								09/17/2018 		   		   REQ - 375046 Quality - Ability to display an Info Message at the top of QS if the Work Queue does not have Pretext (RF+CRM)
* Darshan Sharma								10/04/2018 		   		   REQ - 377121 Quality - Ability to disable the Case Creation in QS if the user does not have permissions to Create a Case for the Member in CRM (CRM)
* Subhamay Mandal								11/29/2018 		   		   REQ - 379745 Quality - Quality - Reset Mentor Documents when Intent is set to None (RF+CRM)
* Darshan Sharma								11/30/2018				   REQ - 378965 PCC - Mentor Documents Set 1 (RF)
* Subhamay Mandal								01/17/2019				   REQ - 383735 AHT - Update - Ability to Identify if a Case with a Classification was created within the Past 14 Days in QS. (CRM)
* Subhamay Mandal								02/08/2019				   REQ - 383977	Quality - Ability to display the AutoLogging Message for additional Classification and Intents (CRM)
* Subhamay Mandal								03/13/2019				   REQ - 383747: CRM QS Fix for updated RSO Calls (RSO) value.
* Subhamay Mandal								03/15/2019  			   REQ - 389663 AHT - RSO - Add Frequently Used Classification and Intent to Classification Dropdown (CRM)
* Amar Gadewar									04/01/2019				   REQ - 388904 - AHT � RSO � Guidance Center Pretext (Set 1) (CRM)
* Amar Gadewar				358974				06/07/2019				   REQ - 399946 - Quality - RCC - Update Quick Start+ Label (CRM)
* Subhamay Mandal								06/25/2019				   REQ - 401380 PCC - Update PCC Rules within QuickStart (CRM)
* Prudhvi Pamarthi                              07/15/2019                 REQ - 396335
* Harish Gowda							        	09/02/2019				   REQ - 406901 Quality - OGO - Ability to NOT allow a user to select BOTH the G&A Rights Given and Complaint dropdowns, in QUICKSTART, to Yes (CRM)
* Harish Gowda							        	09/03/2019				   REQ - 406902 Quality - OGO - Ability to auto select G&A Rights Given field in Quick Start for certain classification and intent combinations (CRM)
* Harish Gowda							        	09/04/2019				   REQ - 406903 Quality - OGO - Ability to auto select Complaint in Quick Start for certain classification and intent combinations (CRM)
* Harish Gowda							        	09/01/2019				   REQ - 409549 Quality - Add Informational Message above Callback Number (CRM) .
* Prathyu Tushar					        		09/27/2019				   Quickstart label for Pharmville User to disply Classification Type Name.
* Harish Gowda							        11/13/2019				   REQ - 415956 Quality - OGO - Display Soft Warning Messages in QuickStart (CRM) .
* Melkisan Selvaraj                             11/14/2019                 REQ - 409548.
* Amar Gadewar                                  12/2/2019                  REQ - 416714 SF Tech - Quick Start fine tuning for performance enhancements (CRM) 
* Subhamay Mandal                               12/26/2019                 731580 Tech - Quick Start Performance Enhancements (CRM)
* Amar Gadewar									01/13/2020				   USER STORY 731580 PR00091574 - MF 1 - Tech - Quick Start Performance Enhancements (CRM)
* Vinay Lingegowda                              04/29/2020                 USER STORY 1038807/1038796
* Subhamay Mandal                               06/04/2020                 User Story 1083375. UAT observation
* Amar Gadewar                                  07/05/2020                 User Story 1247438: PR00091574 - MF 1 - SF - TECH - QS 3.0 Code Improvement and Knowledge cleanup (RF)
* Kavya Bondada                                 08/03/2020                 User Story 1318072: PR00091574 - MF 1 - INC0239694 Fix - Task should not be created from Quick Start unless Create Task button is checked (CRM)
* Amar Gadewar                                  10/09/2020                 User Story 1541640: PR00091574 - MF 1 - Quality - Quick Start Code Improvement and Issue Fixes (CRM)
* Kavya Bondada									01/05/2021				   User Story 1745160: PR00091574 - MF 1 - AHT - Ability to configure Info/Warning Message on Quick Start using Business Configuration (CRM)
* Samantha Tennyson				                12/28/2020		           User Story 1745934: PR00091574 - MF 1 - Quality - Prevent Complaint/G&A Rights Given fields to be selected on Cases with specific Classification and Intent Combinations (CRM)
* Samantha Tennyson				                07/06/2021		           User Story 2454006: T1PRJ0001827 - MF 1 - AHT/Quality - QS 3.0 - Ability to display Pretext in Grievance and Appeals section based on Grievance/Appeal Reason selection (CRM)
* Amar Gadewar                                  07/12/2021                 User Story 2451271: T1PRJ0001827 - MF 1 - Quality/FCR - Ability to display "Recommended Actions" in Quick Start (CRM)
* Amar Gadewar                                  07/22/2021                 User Story 2498859: T1PRJ0001827 - MF 1 - AHT/Quality - Ability to display alert in Quick Start if the Rep is creating a case with potential duplicate grievance (CRM)
************************************************************************************************************************************************************************************/
public with sharing class QuickStart_C_HUM 
{  
    /*
    * Constructor
    * 
    * @see Quick Start form with Classification dropdown values
    */
    public QuickStart_C_HUM()
    {
        this.oNewCase = new Case();
        
        this.bErrorNoSave = false;
        this.bErrorDuplicClassIntent = false;
        this.bShowCaseCommentPanel = true;
        this.bRecordAccess = true;

        this.sUserQueue = QuickStart_S_HUM.evaluateUserQueue();
        this.bProviderUser = false;
        this.bAllowMultipleCase = false;
        this.bDisplayRecommendedActionsIcon = false;
        
        this.bNeverComplaint = false; 
        this.bNeverGA = false; 

        this.bShowDuplicateGrievance = false;
       
        if (String.isNotEmpty(this.sUserQueue.Current_Queue__c))
        {
            this.oUserWorkQueue = QuickStart_S_HUM.populateUserQueue(sUserQueue.Current_Queue__c);
            idClassificationType = this.oUserWorkQueue.Classification_Type__c;

            String sBusinessGroup = QuickStart_S_HUM.checkPreTextAndBusinessGroup(idClassificationType);
            if(null != sBusinessGroup)
            {
                bProviderUser = (sBusinessGroup.contains('Provider'));
                bPreTextConfigured = true;
                this.sQSLabel = Constants_C_AHT_HUM.QUICKSTARTLABEL + ' ' +sBusinessGroup;
                
            } else {
                bPreTextConfigured = false;
                this.sQSLabel = Constants_C_AHT_HUM.QUICKSTARTLABEL;
            }
            
            this.bErrorDuplicClassification = false;

            this.mapQueueCI = QuickStart_S_HUM.getQueueClassificationIntent(this.oUserWorkQueue.Classification_Type__c);
            this.lstFrequentlyUserCIOption = QuickStart_S_HUM.getFrequentlyUsedCI(idClassificationType);
            this.mapCIMentor = QuickStart_S_HUM.fetchAllMentorLink(idClassificationType,sBusinessGroup);
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
  @testVisible   private Map<String, QuickStartCI_DTO_HUM> mapQueueCI{get;set;}
  @testVisible   private Work_Queue_Setup__c oUserWorkQueue{get;set;}
  @testVisible   private User sUserQueue{get;set;}
  @testVisible   private Boolean bAllowMultipleCase{get;set;}
  @testVisible   private List<SelectOption> lstFrequentlyUserCIOption{get;set;}
    
    //HP Case Creation Functionality
    @testVisible private boolean bHasHPPermSet; 
    
    public Case oNewCase {get;set;}
    public Task oNewTask {get;set;}
    public String sTaskDueDate{get; set;}
    public Boolean bCreateTask{get; set;}
    
    public Boolean bProviderUser{get; set;}
    
    public boolean bErrorDuplicClassification{get; set;}
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
    public static List<DuplicatecaseWrapper> listOfClassObj{get;set;}
    
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
                QuickStartCI_DTO_HUM oClassificationIntent = this.mapQueueCI.get(sKeyClassification);
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
            QuickStartCI_DTO_HUM oClassificationIntent = this.mapQueueCI.get(this.sClassificationSelect);
            if(oClassificationIntent != null) {
                for(String sKeyIntent : oClassificationIntent.mapIntentInfo.keySet())   {
                    lstIntentOptions.add(new SelectOption(sKeyIntent, oClassificationIntent.mapIntentInfo.get(sKeyIntent).sIntentLabel));
                }
            }           
        }
        
        return lstIntentOptions;
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
	    this.bRecordAccess = QuickStart_S_HUM.fetchRecordAccess(this.sFocusedObjId);
            if(this.bRecordAccess)
            {
               
                if(null != mapQueueCI && 
                    String.isNotBlank(sClassificationSelect) && sClassificationSelect != HUMConstants.HUMNone  && 
                        String.isNotBlank(sIntentSelect) && sIntentSelect != HUMConstants.HUMNone &&
                            mapQueueCI.containsKey(sClassificationSelect)) {
				
                    QuickStartCI_DTO_HUM selectedClassification = this.mapQueueCI.get(sClassificationSelect);

                    if(null != selectedClassification && String.isNotBlank(selectedClassification.sClassificationLabel))
                    {    
                        
                        String sClassification = selectedClassification.sClassificationLabel;
                        Map <Id, QuickStartCI_DTO_HUM.Intent_DTO_HUM> mapSelectClassIntentInfo = selectedClassification.mapIntentInfo;

                        if(null !=  mapSelectClassIntentInfo && mapSelectClassIntentInfo.containsKey(sIntentSelect)) 
                        {

                            QuickStartCI_DTO_HUM.Intent_DTO_HUM selectedIntent = mapSelectClassIntentInfo.get(sIntentSelect);

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
                                this.sErrorMessage =  'Quick Start Intent Label not found for selected  Classification  :'+sClassification +
                                                        ' &  Intent :'+sIntentSelect + 
                                                        ' Classification Type : ' +  sClassificationName;
                                this.bErrorNoSave = true;
                                throw new QuickStartCustomException(sErrorMessage);
                            }
                        } else {
                            String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                            this.sErrorMessage =  'Quick Start Intent Map not Found for  Classification  :'+sClassification +
                                                    ' Classification Type : ' +  sClassificationName;
                            this.bErrorNoSave = true;
                            throw new QuickStartCustomException(sErrorMessage);
                        }
                    } else {
                        String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                        this.sErrorMessage =  'Quick Start Label / Name not found for Classification  :'+sClassificationSelect +
                                                    ' Classification Type : ' +  sClassificationName;
                        this.bErrorNoSave = true;
                        throw new QuickStartCustomException(sErrorMessage);
                    }
                   
                } else {
                   String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                   this.sErrorMessage =  'Quick Start Map Queue is NULL or Invalid selected Classification  :'+sClassificationSelect +
                                                    ' &  Intent :'+sIntentSelect + 
                                                    ' Classification Type : ' +  sClassificationName;
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
                HUMExceptionHelper.logErrors(exceptionToLog, 'QuickStart_C_HUM', 'assignCaseValues');
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
		QuickStart_S_HUM.assignCaseDefaults(this.oNewCase, this.sUserQueue.Current_Queue__c);
		QuickStart_S_HUM.assignCaseQueue(this.oNewCase, this.oUserWorkQueue);
        QuickStart_S_HUM.assignCaseClassificationIntent(this.oNewCase, this.sClassificationSelect, this.sIntentSelect, sCTCIId);
    }
    
    /*
    * Determine the object being association with and routing Case assignment accordingly
    * 
    */
    private void assignValuesByObject()
    {   
        String sSaveObjectType = sFocusedObjId.getSobjectType().getDescribe().getName();
        
        if(sSaveObjectType == HUMConstants.OBJECT_POLICYMEMBER) {
            QuickStart_S_HUM.assignPolicyMemberValues(this.oNewCase, this.sFocusedObjId, this.sInteractionId, bIsHPCase);
        } else if(sSaveObjectType == HUMConstants.OBJECT_ACCOUNT) {
            QuickStart_S_HUM.assignAccountValues(this.oNewCase, this.sFocusedObjId, this.sInteractionId, bIsHPCase);
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
            this.bErrorDuplicClassIntent = QuickStart_S_HUM.verifyNoDuplicClassIntent(this.oNewCase);
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
            Boolean bCheckDuplicateClassification = false;
            if(!this.bErrorDuplicClassification && !this.bProviderUser) {
                bCheckDuplicateClassification = QuickStart_S_HUM.verifyNoDuplicClassification(this.oNewCase);
            }
            
            this.bErrorDuplicClassification = bCheckDuplicateClassification;
            if(!bCheckDuplicateClassification) {
                this.sErrorMessage = QuickStart_S_HUM.insertNewCase(this.oNewCase);
                this.bErrorNoSave = String.isNotEmpty(this.sErrorMessage);
                if(!this.bErrorNoSave) {
                    this.sSavedCaseId = this.oNewCase.Id;
                }
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
            QuickStart_S_HUM.assginTaskDefaults(this.oNewTask, this.sSavedCaseId, this.sTaskDueDate, this.oUserWorkQueue, this.sUserQueue);
            this.sErrorMessage = QuickStart_S_HUM.insertNewTask(this.oNewTask);
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
				
                QuickStartCI_DTO_HUM selectedClassification = this.mapQueueCI.get(sClassificationSelect);

                if(null != selectedClassification && String.isNotBlank(selectedClassification.sClassificationLabel))
                {    
                    
                    String sClassification = selectedClassification.sClassificationLabel;
                    Map <Id, QuickStartCI_DTO_HUM.Intent_DTO_HUM> mapSelectClassIntentInfo = selectedClassification.mapIntentInfo;

                    if(null !=  mapSelectClassIntentInfo && mapSelectClassIntentInfo.containsKey(sIntentSelect)) 
                    {

                        QuickStartCI_DTO_HUM.Intent_DTO_HUM selectedIntent = mapSelectClassIntentInfo.get(sIntentSelect);

                        if(null != selectedIntent && String.isNotBlank(selectedIntent.sIntentLabel) && String.isNotBlank(selectedIntent.idCTCI))
                        {
                            String sIntent = selectedIntent.sIntentLabel;
        
                            if(null != mapCIMentor &&  mapCIMentor.containskey(sClassification+'-'+sIntent)) {
                                
                                this.lstDocumentLink = this.mapCIMentor.get(sClassification+'-'+sIntent);
                                this.bShowDocumentPanel = !this.lstDocumentLink.isEmpty();
                            }
                            
                            // check for pretext data
                            this.sPreTextdata = QuickStart_S_HUM.fetchQSPretextData(sClassification, sIntent, idClassificationType);
                            this.bShowCaseCommentPanel = String.isBlank(sPreTextdata);
                            // OGO setup data
                            QuickStartCI_DTO_HUM.AdditionalInfo_DTO_HUM oAdditionalInfo = QuickStart_S_HUM.fetchQSPAdditionalInfo(sClassification, sIntent, idClassificationType);
                            if(null != oAdditionalInfo) {
                                this.sInformationalMessage = oAdditionalInfo.sInformationalMessage;
                                this.sSoftWarningMessage = oAdditionalInfo.sSoftWarningMessage;
                                this.sOGOFieldSetup = oAdditionalInfo.sOGOFieldSetup;
                                
                                this.bAllowMultipleCase = oAdditionalInfo.bAllowMultipleCase;
                                this.sHeaderMessageType = oAdditionalInfo.sHeaderMessageType;
                                this.sHeaderMessage = oAdditionalInfo.sHeaderMessage;
                                this.bNeverComplaint = oAdditionalInfo.bNeverComplaint;
                                this.bNeverGA = oAdditionalInfo.bNeverGA;
                                if(String.isNotBlank(oAdditionalInfo.sRecommendActions)) {
                                    oAdditionalInfo.sRecommendActions = oAdditionalInfo.sRecommendActions.replaceAll('; ',';');
                                    lstqspRecActions = oAdditionalInfo.sRecommendActions.split(';');
                                    mapRAiconImages = QuickStart_S_HUM.getRecommendDocumentDetails(lstqspRecActions);
                                    
                                    if(!mapRAiconImages.isEmpty()){
                                        this.bDisplayRecommendedActionsIcon = true;
                                    }
                                }
                                if(bHasHPPermSet){
                                    bIsHPCase = HUMUtilityHelper.isHPClassificationIntent(sClassification, sIntent);
                                }
                            }
                        } else {
                                String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                                this.sErrorMessage =  'Quick Start Intent Label not found for selected  Classification  :'+sClassification +
                                                        ' &  Intent :'+sIntentSelect + 
                                                        ' Classification Type : ' +  sClassificationName;
                                
                                throw new QuickStartCustomException(sErrorMessage);
                        }
                    } else {
                        String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                        this.sErrorMessage =  'Quick Start Intent Map not Found for  Classification  :'+sClassification +
                                                ' Classification Type : ' +  sClassificationName;
                        
                        throw new QuickStartCustomException(sErrorMessage);

                    }
                } else {
                    String sClassificationName = this.oUserWorkQueue.Classification_Type__r.Name;
                    this.sErrorMessage =  'Quick Start Label / Name not found for Classification  :'+sClassificationSelect +
                                                ' Classification Type : ' +  sClassificationName;
                    
                    throw new QuickStartCustomException(sErrorMessage);                        

                }
            }
            
		} catch(Exception exceptionToLog) {
    	
             HUMExceptionHelper.logErrors(exceptionToLog, 'QuickStart_C_HUM', 'populateDocumentLinks');
             
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
    }
    
    /**
    * method to retrieve pretext data for GA section
    * return void
    */    
    public void getAFPretextGA() {
        try { 
            if(String.isNotBlank(sOGOFieldValue) && String.isNotBlank(sOGOReasonValue))
        		sPretextGAJSON = QuickStart_S_HUM.getPretextGAJSON(sOGOFieldValue, sOGOReasonValue);
        }catch(Exception exceptionToLog) {
             HUMExceptionHelper.logErrors(exceptionToLog, 'QuickStart_C_HUM', 'getAFPretextGA');    
        }
    }

    /**
    *  Check if this is duplicate Greivance submitted past 30 days
    * return void
    */
    public void checkDuplicateGrievance(){
        try { 
            this.bShowDuplicateGrievance = false;
            
            listOfClassObj = QuickStart_S_HUM.assignWrapperData(this.sAccountId,this.sClassificationSelect,this.sIntentSelect,this.oNewCase.Complaint__c);
            if(!listOfClassObj.isEmpty()){
                this.bShowDuplicateGrievance = true;
            } 
            
        }catch(Exception e) {
            HUMExceptionHelper.logErrors(e, 'QuickStart_C_HUM', 'checkDuplicateGrievance');    
       }
        
    }
    public class QuickStartCustomException extends Exception  { }   
  
}