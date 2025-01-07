/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_S_HUM.cls 
Created Date     : June 22, 2017
Function         : Executes SOQL in support of the Quick Start Console Component
Test Class       : QuickStart_T_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
* Amar Gadewar            -----                 06/22/2017                 Original Version
* Amar Gadewar									08/15/2017				   Defect fix - Removing the Default Inbound Call as Origin
* Dinesh Subramaniyan		140323				10/23/2017				   REQ - 328932 SF - TECH - Redesign Quick Start Error Messages (RF)
* Amar Gadewar									10/23/2017				   REQ - 335169 -  Defect 322899 fix for Medicare Id
* Subhamay Mandal           165717              11/09/2017                 REQ - 335028 Ability to prefill the quick start case comment fields based on the Classification and Intent selection (CRM)
* Amar Gadewar				172615				01/19/2017				   REQ - 350502 Ability of a quick start to create a case on an existing interaction created by someone else(CRM)
* Amar Gadewar									01/29/2017				   Fixing the Attempt to de-reference a null object
* Amar Gadewar				188566				02/23/2018				   REQ - 350501 - Error Message on Quick Start
* Subhamay Mandal		 					    02/25/2018 				   REQ - 351011 Ability to use Business Configuration model for Quick Start pretext for making changes within Quick Start pre-text (CRM)
* Subhamay Mandal		   					  	03/01/2018				   REQ - 355820 Ability in Quick Start to allow CCS reps to create multiple cases on the single interaction for certain Classification and Intent (CRM)
* Subhamay Mandal		   					  	04/06/2018				   REQ - 359992 and REQ - 360019. UI change(plus and logo) and Performace improvement
* Darshan Sharma		   					  	04/27/2018				   REQ - 361223 Ability to update Mentor Links in QuickStart via Business Configuration (CRM)
* Subhamay Mandal							    05/17/2018				   REQ - 363405 SPLUNK Logs custom field validation error in Quick Start
* Darshan Sharma				                08/20/2018 		           REQ - 372751 PCC - Reference Documents (RF)
* Darshan Sharma			                    10/01/2018		           REQ - 374820: CTCI Model Update (RF+CRM)
* Asish   Behera								09/24/2018 		   		   REQ - 375046 Quality - Ability to display an Info Message at the top of QS if the Work Queue does not have Pretext (RF+CRM)
* Darshan Sharma								10/04/2018 		   		   REQ - 377121 Quality - Ability to disable the Case Creation in QS if the user does not have permissions to Create a Case for the Member in CRM (CRM)
* Darshan Sharma								11/30/2018				   REQ - 378965 PCC - Mentor Documents Set 1 (RF)
* Subhamay Mandal								01/17/2019				   REQ - 383735 AHT - Update - Ability to Identify if a Case with a Classification was created within the Past 14 Days in QS. (CRM)
* Subhamay Mandal								02/08/2019				   REQ - 383977	Quality - Ability to display the AutoLogging Message for additional Classification and Intents (CRM)
* Subhamay Mandal								03/15/2019  			   REQ - 389663 AHT - RSO - Add Frequently Used Classification and Intent to Classification Dropdown (CRM)
* Amar Gadewar									04/01/2019				   REQ - 388904 - AHT – RSO – Guidance Center Pretext (Set 1) (CRM)	 
* Harish Gowda							        	09/02/2019				   REQ - 406901 Quality - OGO - Ability to NOT allow a user to select BOTH the G&A Rights Given and Complaint dropdowns, in QUICKSTART, to Yes (CRM)
* Harish Gowda							        	09/03/2019				   REQ - 406902 Quality - OGO - Ability to auto select G&A Rights Given field in Quick Start for certain classification and intent combinations (CRM)
* Harish Gowda							        	09/04/2019				   REQ - 406903 Quality - OGO - Ability to auto select Complaint in Quick Start for certain classification and intent combinations (CRM)
* Harish Gowda							        	09/01/2019				   REQ - 409549 Quality - Add Informational Message above Callback Number (CRM) .
* Kiran Kumar									09/09/2019 				   REQ: 397251: added methods for RxRf quickstart functioinality
* Prathyu Tushar					        	09/27/2019				   Quickstart label for Pharmville User to disply Classification Type Name.
* Melkisan Selvaraj								11/14/2019					REQ - 409548
* Subhamay Mandal                               05/12/2020                 1083375 PR00091574 - MF 1 - QS 3.0 - AHT - Ability to create task from Quick Start (CRM)
* Amar Gadewar                                  07/05/2020                 User Story 1247438: PR00091574 - MF 1 - SF - TECH - QS 3.0 Code Improvement and Knowledge cleanup (RF)
* Amar Gadewar                                  10/09/2020                 User Story 1541640: PR00091574 - MF 1 - Quality - Quick Start Code Improvement and Issue Fixes (CRM)
* Kavya Bondada									01/05/2021				   User Story 1745160: PR00091574 - MF 1 - AHT - Ability to configure Info/Warning Message on Quick Start using Business Configuration (CRM)
* Samantha Tennyson				                07/06/2021		           User Story 2454006: T1PRJ0001827 - MF 1 - AHT/Quality - QS 3.0 - Ability to display Pretext in Grievance and Appeals section based on Grievance/Appeal Reason selection (CRM)
* Amar Gadewar                                  07/12/2021                 User Story 2451271: T1PRJ0001827 - MF 1 - Quality/FCR - Ability to display "Recommended Actions" in Quick Start (CRM)
* Amar Gadewar                                  07/22/2021                 User Story 2498859: T1PRJ0001827 - MF 1 - AHT/Quality - Ability to display alert in Quick Start if the Rep is creating a case with potential duplicate grievance (CRM)
************************************************************************************************************************************************************************************/
public class QuickStart_S_HUM
{
    public static final List<String> lstComplaintsYes =  new List<String> {'Yes - Medicare Part C','Yes - Medicare Part D','Yes - Medicaid'};
    /*
    * Gets the User's Current Queue
    * 
    * @return string: User's Current Queue
    */  
    public static User evaluateUserQueue()
    {
        User oUser = QuickStart_D_HUM.queryUserQueue();
        return oUser;
    }
    
    /*
    * Gets the User's Current Queue
    * 
    * @param string: User's Current Queue 
    * @return Work_Queue_Setup__c: User's Current Work Queue
    */
    public static Work_Queue_Setup__c populateUserQueue(string sQueueName)
    {
        Work_Queue_Setup__c oQueue = new Work_Queue_Setup__c();
        List<Work_Queue_Setup__c> lstQueriedQueue = QuickStart_D_HUM.queryQueueByName(sQueueName);
        if(!lstQueriedQueue.isEmpty()) {
            oQueue = lstQueriedQueue.get(0);
        }
        return oQueue;
    }
   
    /*
    * Assigns the Case's Classification and Intent fields from the values selected
    * 
    * @param Case: New Case to assign to
    * @param string: The selected Classification's ID
    * @param string: The selected Intent's ID
    * @param string: The selected CTCI's ID
    * @see If the save is successful, the selected values may be reflected on the Case Edit screen
    */
    public static void assignCaseClassificationIntent(Case oNewCase, string sClassificationSelect, string sIntentSelect, String sIdCTCI)
    {   
        oNewCase.CTCI_List__c = sIdCTCI;
        oNewCase.Classification_Id__c = sClassificationSelect;
        oNewCase.Intent_Id__c = sIntentSelect;
    }  
    
    /*
    * Assigns the new Case's Queue-related values when associating Quick Start to a record
    * 
    * @param Case: New Case to assign to 
    * @param Work_Queue_Setup__c: User's Current Work Queue
    * @see The User's Work Queue values may be populated on the Case Edit page
    */
    public static void assignCaseQueue(Case oNewCase, Work_Queue_Setup__c oUserWorkQueue)
    {
        oNewCase.Classification_Type__c = oUserWorkQueue.Classification_Type__r.Name;
        oNewCase.Service_Center__c = oUserWorkQueue.Service_Center__c;
        oNewCase.Department__c = oUserWorkQueue.Department__c;
    }    
    
    /*
    * Assigns the new Case's values when associating Quick Start to a Policy Member record
    * 
    * @param Case: New Case to assign to 
    * @param string: Record ID selected for association from Quick Start
    * @param string: User's current Queue    
    * @see Account, Policy Member and Interaction values may be populated on the Case Edit page
    */
    public static void assignPolicyMemberValues(Case oNewCase, string sFocusedObjId, string sInteractionId, boolean bIsHPCase)
    {
        if(String.isNotBlank(sFocusedObjId))
        {
            List<Policy_Member__c> lstCurrentPolMem = QuickStart_D_HUM.queryActivePolicyMember(sFocusedObjId);
            
            if(!lstCurrentPolMem.isEmpty())
            {
                oNewCase.Policy_Member__c =  lstCurrentPolMem[0].Id;
                String sActiveAccount = lstCurrentPolMem[0].Member__c;
                
                List<Account> lstCurrentAcct = QuickStart_D_HUM.queryActiveAccount(sActiveAccount);
                if(!lstCurrentAcct.isEmpty())
                {
                    if(bIsHPCase)
                    {
                        oNewCase.RecordTypeId = HUMUtilityHelper.getHPCaseRecordType(lstCurrentAcct[0].RecordType.Name);
                    }
                    else
                    {
                        oNewCase.RecordTypeId = HUMUtilityHelper.getCaseRecordType(lstCurrentAcct[0].RecordType.Name);
                    }
              
                    oNewCase.AccountId = lstCurrentAcct[0].Id;
                }
               
            }
            assignCaseInteraction(oNewCase,sInteractionId);
        }
    }
	    
    /*
    * Assigns the new Case's values when associating Quick Start to an Account record
    * 
    * @param oNewCase: New Case to assign to 
    * @param string: Record ID selected for association from Quick Start
    * @param string: User's current Queue    
    * @see Account and Interaction values may be populated on the Case Edit page
    */
    public static void assignAccountValues(Case oNewCase, string sFocusedObjId, string sInteractionId, boolean bIsHPCase)
    {
        if(String.isNotBlank(sFocusedObjId))
        {
            List<Account> lstCurrentAcct = QuickStart_D_HUM.queryActiveAccount(sFocusedObjId);
            if(!lstCurrentAcct.isEmpty())
            {
                Account oCurrentAccount = lstCurrentAcct[0];
                oNewCase.AccountId = oCurrentAccount.Id;
                
                String sAcctRecordTypeName = oCurrentAccount.RecordType.Name;
                if(bIsHPCase)
                {
                    oNewCase.RecordTypeId = HUMUtilityHelper.getHPCaseRecordType(sAcctRecordTypeName);
                }
                else
                {
                    oNewCase.RecordTypeId = HUMUtilityHelper.getCaseRecordType(sAcctRecordTypeName);
                }
                oNewCase.NPI_ID__c = oCurrentAccount.NPI_ID__c;
                
                if(!oCurrentAccount.ConsumerIDs__r.isEmpty()) {
                    oNewCase.Tax_ID__c = oCurrentAccount.ConsumerIDs__r[0].Consumer_ID__c;
                }
                assignDefaultValueforUnKnownMemberCase(oNewCase);   
            }
            assignCaseInteraction(oNewCase,sInteractionId);
        }
    }
    
    /*
    * Assign's the Case's Interaction-related values
    * 
    * @param Case: New Case to assign to
    * @param String: Current User's ID
    * @see Interaction values may be populated on the Case Edit page
    */
    public static void assignCaseInteraction(Case oNewCase,string sInteractionId)
    {
        if(String.isNotBlank(sInteractionId))
        {
            List<Interaction_Member__c> lstActiveIntMember = QuickStart_D_HUM.queryActiveIntMem(oNewCase.AccountId, sInteractionId);
            if(!lstActiveIntMember.isEmpty())
            {
                oNewCase.Interaction_ID__c = sInteractionId;
                oNewCase.Origin = lstActiveIntMember[0].Interaction__r.Interaction_Origin__c;
                oNewCase.Interacting_With_Type__c = lstActiveIntMember[0].Interaction__r.Interacting_With_Type__c;
                oNewCase.Interacting_With__c = lstActiveIntMember[0].Interaction__r.Interacting_With__c;
                oNewCase.Interacting_With_Name__c = lstActiveIntMember[0].Interaction__r.Caller_Name__c;
                oNewCase.Interacting_About_Type__c = lstActiveIntMember[0].Interacting_About_Type__c;           
            }           
        }
    }

    /*
    * Assigns general Case fields
    * 
    * @param Case: New Case to assign to
    * @param string: User's current Queue
    * @see Values assigned here may be populated on the Case Edit page
    */
    public static void assignCaseDefaults(Case oNewCase, string sUserQueue)
    {
        oNewCase.Status = HUMConstants.CASE_STATUS;
        oNewCase.Type = HUMConstants.CASE_TYPE;
        oNewCase.Owner_Queue__c = sUserQueue;
        oNewCase.Case_Owner__c = UserInfo.getName();
        oNewCase.Origin = HUMConstants.CASE_ORIGIN;
        oNewCase.Call_Benefit_Category__c = null;
    }    
    
   /*
    * Assigns the Default Vales for Unknown Member Case for GA Rights Given and Compaint drop down values
    * 
    * @param Case: New Case to assign to
    */  
    private static void assignDefaultValueforUnKnownMemberCase(Case oNewCase)
    {
        Map<ID, Schema.RecordTypeInfo> mapRecordType = Schema.SObjectType.Case.getRecordTypeInfosById();
        
        if(null != mapRecordType && null != mapRecordType.get(oNewCase.recordTypeId) && String.isNotBlank(mapRecordType.get(oNewCase.recordTypeId).getName())
         && mapRecordType.get(oNewCase.recordTypeId).getName().containsIgnoreCase(HUMConstants.HUMUnknownCase))
        {
            if(String.isBlank(oNewCase.G_A_Rights_Given__c)) oNewCase.G_A_Rights_Given__c = HUMConstants.GA_RIGHTS_GIVEN;
            
            if(String.isBlank(oNewCase.Complaint__c)) oNewCase.Complaint__c = HUMConstants.COMPLAINT;
        }
    }                       
    
    /*
    * Verifies that no other Cases for this Interaction have the same CTCI List value
    * 
    * @param Case: New Case to assign to
    * @return boolean: Returns true if a duplicate was found (negative scenario)
    * @see If true, Quick Start will display the associated page message
    */
    public static boolean verifyNoDuplicClassIntent(Case oNewCase)
    {
        boolean bHasDuplicate = false;

        List<Case> lstExistingCases = QuickStart_D_HUM.queryCasesByActiveInteraction(oNewCase);
        bHasDuplicate = !lstExistingCases.isEmpty();
        
        return bHasDuplicate;
    }
    
    /*
    * Verifies that no other Cases for this Classifiaction in las 14 days
    * 
    * @param Case: New Case to assign to
    * @return boolean: Returns true if a duplicate was found (negative scenario)
    * @see If true, Quick Start will display the associated page message
    */
    public static boolean verifyNoDuplicClassification(Case oNewCase)
    {
        boolean bHasDuplicate = false;
        if(oNewCase.Interacting_With_Type__c == HUMConstants.MEMBER || oNewCase.Interacting_With_Type__c == HUMConstants.MEMBERCAREGIVER)
        {
        List<Case> lstExistingCases = QuickStart_D_HUM.queryCasesByClassification(oNewCase);
        bHasDuplicate = !lstExistingCases.isEmpty();
        }
        return bHasDuplicate;
    } 
       
    /*
    * Facilitates insertion of the Associated Case
    * 
    * @param Case: Case to insert
    * @return Boolean: Returns false if no errors occurred during the save
    * @see: The Case Edit page will open for the newly inserted Case
    */
    public static String insertNewCase(Case oNewCase)
    {   
        String sErrorDidNotSave = QuickStart_D_HUM.insertAssociatedCase(oNewCase);
        return sErrorDidNotSave;
    }    
    
    /*
    * Method to fetch QS pretext data for Clasiffication and Intent combination
    * 
    * @param String: selected classification value
    * @param String: selected intent value
    * @param Id: Classification Type
    * @return List: Pretext data
    *
    * @see: Pretext layout on QS window
    */
    public static String fetchQSPretextData(String sClassification, String sIntent, Id idClassificationType) {
        List<Quick_Start_Pretext_Section__c> lstofPreTextSections = QuickStart_D_HUM.queryPretextSectionData(sClassification,sIntent, idClassificationType);        
        String sPreTextdata = QuickStart_H_HUM.parsePreTextData(lstofPreTextSections);
        return sPreTextdata;
    }
        
    /*
    * Method to get Classification and Intent values for Call (RSO)
    * @param String: selected ClassificationType for user queue 
    * @return Map<Id, ClassificationIntent_DTO>
    *
    * @see: Pretext layout on QS window
    */
    public static Map<String, QuickStartCI_DTO_HUM> getQueueClassificationIntent(String sQueueClassificationType) {
        Map<String, QuickStartCI_DTO_HUM> mapQueueCI = new Map<String, QuickStartCI_DTO_HUM>();
        Id sCurrentId;
        Id sPreviousId;
        Map<Id, QuickStartCI_DTO_HUM.Intent_DTO_HUM> mapIntentInfo = new Map<Id, QuickStartCI_DTO_HUM.Intent_DTO_HUM>();
        CTCI_Junction__c ctciPrevious = null;
        for(CTCI_Junction__c ctci : QuickStart_D_HUM.queryCTCIByClassificationTypeCI(sQueueClassificationType)) {
            sCurrentId = ctci.Classification__c;
            if(String.isBlank(sPreviousId)) {
                mapIntentInfo.put(ctci.Intent__c, new QuickStartCI_DTO_HUM.Intent_DTO_HUM(ctci));
                sPreviousId = sCurrentId;
                ctciPrevious = ctci;
            } else if(sCurrentId != sPreviousId) {
                mapQueueCI.put(sPreviousId, new QuickStartCI_DTO_HUM(ctciPrevious, mapIntentInfo));
                
                mapIntentInfo = new Map<Id, QuickStartCI_DTO_HUM.Intent_DTO_HUM>();
                ctciPrevious = ctci;
                sPreviousId = sCurrentId;
                mapIntentInfo.put(ctci.Intent__c, new QuickStartCI_DTO_HUM.Intent_DTO_HUM(ctci));
            } else {
                mapIntentInfo.put(ctci.Intent__c, new QuickStartCI_DTO_HUM.Intent_DTO_HUM(ctci));
            }
        }
        if(ctciPrevious != null) {
            mapQueueCI.put(sCurrentId, new QuickStartCI_DTO_HUM(ctciPrevious, mapIntentInfo));
        }
        
        return mapQueueCI;
    }
    
    /*
    * Fetch all Mentor Link doc on Quickstart page load
    * @param Id: Classification Type
    * @param String: Business Group
    * @return Map<String, List<customLink__c>>
    */
    public static Map<String,List<customLink__c>> fetchAllMentorLink(Id idClassificationType, String sBusinessGroup){
        
        Map<String,List<CustomLink__c>> mapCIMentor= new Map<String,List<CustomLink__c>>();
        
        List<CustomLink__c> lstAllMentorLink = QuickStart_D_HUM.queryAllCustomLink(idClassificationType,sBusinessGroup);
        String ClassificationName;
        String IntentName;
        for(CustomLink__c mentordoc : lstAllMentorLink)
        {
            
            ClassificationName = mentordoc.Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification__r.Name;
            IntentName = mentordoc.Quick_Start_Pretext__r.CTCI_List_lkp__r.Intent__r.Name;
                
            if(string.isBlank(ClassificationName) && string.isBlank(IntentName))
            {
                ClassificationName = Constants_C_AHT_HUM.REFERENCE_DOCUMENT_CI;
                IntentName = Constants_C_AHT_HUM.REFERENCE_DOCUMENT_CI;
            }
                
            if(mapCIMentor.containskey(ClassificationName+'-'+IntentName))
            {
                List<CustomLink__c> lstmentorlink = mapCIMentor.get(ClassificationName+'-'+IntentName);
                lstmentorlink.add(mentordoc);
                mapCIMentor.put(ClassificationName+'-'+IntentName,lstmentorlink);
            }
            else
            {
                mapCIMentor.put(ClassificationName+'-'+IntentName,new List<customLink__c> {mentordoc});
            }
        }
        
        return mapCIMentor;
    }
    
    /*
    * Fetch Frequently Used Classification and Intent
    * @param Id: Classification Type
    * @return List<SelectOption>
    */
    public static List<SelectOption> getFrequentlyUsedCI(Id idClassificationType) {
        List<SelectOption> lstFrequentlyUsedCIOption = new List<SelectOption>();
        for(Quick_Start_Pretext__c qspFrequentlyUsedCI : QuickStart_D_HUM.queryFrequentlyUsedCI(idClassificationType)) {
            String strValue = qspFrequentlyUsedCI.CTCI_List_lkp__r.Classification__c + HUMConstants.sHiphan + qspFrequentlyUsedCI.CTCI_List_lkp__r.Intent__c;
            String strLabel = qspFrequentlyUsedCI.CTCI_List_lkp__r.Classification__r.Name +' :: '+ qspFrequentlyUsedCI.CTCI_List_lkp__r.Intent__r.Name;
            lstFrequentlyUsedCIOption.add(new SelectOption(strValue, strLabel));
        }
        
        return lstFrequentlyUsedCIOption;
    }
    
    /*
     * Query UserRecordAccess to check record Access
     * @return boolean
     */
    public static boolean fetchRecordAccess(String recordId)
    {
        boolean bRecordAccess = false;
        List<UserRecordAccess> lstUserRecordAccess = QuickStart_D_HUM.queryUserRecordAccess(recordId);
            
            if(!lstUserRecordAccess.isEmpty())
            {
                bRecordAccess = lstUserRecordAccess[0].HasReadAccess;
            }
        return bRecordAccess;
    }
    
     /*
     * check Pretext configured for current workqueue
     * @param Boolean
     * @return boolean
     */
    public static String  checkPreTextAndBusinessGroup(String sClassficationTypeId)
    {
        return  QuickStart_D_HUM.getBusinessGroup(sClassficationTypeId);
    }
    
    /*
    * Method to fetch additional information for Clasiffication and Intent combination
    * 
    * @param String: selected classification value
    * @param String: selected intent value
    * @param Id: Classification Type
    * @return String: Qucik Start Pretext information
    *
    * @see: Pretext layout on QS window
    */
    public static QuickStartCI_DTO_HUM.AdditionalInfo_DTO_HUM fetchQSPAdditionalInfo(String sClassification, String sIntent, Id idClassificationType) {
        QuickStartCI_DTO_HUM.AdditionalInfo_DTO_HUM oAdditionalInfo;
        
        List<Quick_Start_Pretext__c> lstQSPAdditionalInfo = QuickStart_D_HUM.queryQSPAdditionalInfo(sClassification, sIntent, idClassificationType);
        if(!lstQSPAdditionalInfo.isEmpty()) {
            Quick_Start_Pretext__c tempQSP = lstQSPAdditionalInfo.get(0);
            oAdditionalInfo = new QuickStartCI_DTO_HUM.AdditionalInfo_DTO_HUM(tempQSP);
        }
        return oAdditionalInfo;
    }
    
    /**
    * assign task values from UI
    * @param Task oNewTask
    * @param String sSavedCaseId
    * @param String sTaskDueDate
    * @param Work_Queue_Setup__c oUserWorkQueue
    * @return void
    */
    public static void assginTaskDefaults(Task oNewTask, String sSavedCaseId, String sTaskDueDate, Work_Queue_Setup__c oUserWorkQueue, User oUserQueue){
        oNewTask.WhatId = sSavedCaseId;
        oNewTask.OwnerID = UserInfo.getUserId();
        oNewTask.ActivityDate = String.isNotBlank(sTaskDueDate)? Date.parse(sTaskDueDate) : null;
        oNewTask.Service_Center__c = oUserWorkQueue.Service_Center__c;
        oNewTask.Department__c =  oUserWorkQueue.Department__c;
        oNewTask.Owner_Queue__c = oUserWorkQueue.Name;
        oNewTask.Task_Owner__c = String.isNotBlank(oUserWorkQueue.Name)? oUserWorkQueue.Name : UserInfo.getName();
        oNewTask.QueueOrUserId__c = String.isNotBlank(oUserWorkQueue.Public_Group_Queue_Id__c)? oUserWorkQueue.Public_Group_Queue_Id__c : UserInfo.getUserId();
        if(oNewTask.Type == HumConstants.HUMExtensionTask) {            
            oNewTask.Task_Owner__c = UserInfo.getName();
            oNewTask.QueueOrUserId__c = UserInfo.getUserId();
        }
    }
    
    /**
    * save a new task record with Case
    * @param Task
    * @return void
    */
    public static String insertNewTask(Task oNewTask) {
        String sErrorDidNotSave = QuickStart_D_HUM.insertAssociatedTask(oNewTask);
        return sErrorDidNotSave;
    }
    
    /**
    * Get the Pretext GA Details and convert it to JSON
    * @param String - sOGOFieldValue
    * @param String - sOGOReasonValue
    * @return String
    */
    public static String getPretextGAJSON(String sOGOFieldValue,String sOGOReasonValue) {
        List<Quick_Start_Pretext_Section__c> lstofPreTextSections = QuickStart_D_HUM.queryPretextGASectionData(sOGOFieldValue, sOGOReasonValue);
        String sPretextGAJSON = QuickStart_H_HUM.parsePreTextData(lstofPreTextSections);
        return sPretextGAJSON;
    }	

    /*
    * This is used to retrieve all icon documents
    * @param  Map<String,QuickStartCI_DTO_HUM> - QSP map
    * @return Map<String,List<String>> - Icons with document details
    */    
    public static Map<String,List<String>> getRecommendDocumentDetails(List<String> lstqspRecActions)
    {
        List<Document> lstDocuments = QuickStart_D_HUM.getRecommendedActionsDocuments();
        Map<String,List<String>> mapIconImages = new Map<String,List<String>>();
        String sDocId; String sDocDesc;
        
        if(!lstqspRecActions.isEmpty() && !lstDocuments.isEmpty()){
            for(String sQSPrecActionName : lstqspRecActions)
            {
                sDocId=''; sDocDesc='';
                for(Document objDoc : lstDocuments)
                {
                    if(sQSPrecActionName == objDoc.Name) 
                    {	
                        sDocId = objDoc.id;
                        if (String.isNotBlank(objDoc.Description))
                        	sDocDesc = objDoc.Description;
                        break;
                    }
                } 
                mapIconImages.put(sQSPrecActionName,new List<String>{sDocId, sDocDesc});
            }
        }
        return mapIconImages;
    }

    /*
    * This is used to get the Cases created in last 30 days having same Classification and Intent and having same Complaint value in last 30 days
    * @param  String - Accountid
    * @param  String - Classification Id
    * @param  String - Intent Id
    * @param  String - Complaint value
    * @return List<DuplicateCaseWrapper>>
    */ 
    public static List<DuplicatecaseWrapper> assignWrapperData(String sAccountId, String sClassificationSelect,String sIntentSelect,String sComplaint){

        List<DuplicatecaseWrapper> lstWrapData = new List<DuplicatecaseWrapper>();

        if(String.isNotBlank(sAccountId) && String.isNotBlank(sClassificationSelect) 
            && String.isNotBlank(sIntentSelect) && String.isNotBlank(sComplaint)
                && lstComplaintsYes.contains(sComplaint)) {
                
                List<Case> lstCases = QuickStart_D_HUM.getCasesByLastNDays(sAccountId,sClassificationSelect,sIntentSelect,sComplaint);

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
            }

        
        return lstWrapData;
    }
}