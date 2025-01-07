/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_S_AHT_HUM 
Created Date     : January 20, 2017
Function         : Executes business logic and assignment functions for the Quick Start Console Component
Test Class       : QuickStart_T_AHT_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
* Cody A. Sanders            -----              03/10/2017                 Quick Start MVP - Associate to Policy, Display Essential Fields, Open Case Edit and Case Comment Transfer
* Cody A. Sanders         -----                 03/24/2017                 Included additional Work Queue related fields
* Cody A. Sanders         -----                 04/26/2017                 Updated for new CTCI model and added Associate to Account and Mentor Document functionality
* Amar Gadewar                                  06/07/2017                 For Unknown Member Account, default the values to "N" if they are not entered by the user
* Subhamay Manal                                07312017                   REQ - 325458 Ability of a quick start page to be responsive when selecting top 5 and non top 5 classification and Intent (RF)
* Amar Gadewar                                  08/15/2017                 Defect fix - Removing the Default Inbound Call as Origin
* Siddharth Mehta                               08/24/2017                 REQ - 327581 Ability of a Quick Start to give error message when queue is not selected (RF)
* Piyusha Babar                                 08/28/2017                 REQ - 327583 Ability of a quick start to create a case on an existing interaction created by someone else(RF)
* Subhamay Mandal                               10/02/2017                 REQ - 329803 Ability to prefill the quick start case comment fields based on the Classification and Intent selection
* Dinesh Subramaniyan    140323                 10/23/2017                 REQ - 328932 SF - TECH - Redesign Quick Start Error Messages (RF)
* Amar Gadewar           140475                 10/23/2017                 REQ - 335169 -  Defect 322899 fix for Medicare Id
* Subhamay Mandal                               01/12/2018                 REQ - 350392 - Business Configuration model for Quick Start pretext (RF)
* Subhamay Mandal                               02/20/2018                 REQ - 354335 Ability in Quick Start to allow CCS reps to create multiple cases on the single interaction for certain Classification and Intent (RF)
* Subhamay Mandal                               03/20/2018                 REQ - 357688 Technical Story: RF Quick Start White Screen Issue - (RF) and REQ - 357686 Update QuickStart window UI (RF)
* Darshan Sharma                                04/05/2018                 REQ - 361047 - Ability to update Mentor Links in QuickStart via Business Configuration
* Darshan Sharma                                05/02/2018                 REQ - 361223 Ability to update Mentor Links in QuickStart via Business Configuration (CRM)
* Subhamay Mandal                               05/16/2018                 REQ - 361876 AHTQuality - Preset the Complaint Fields
* Subhamay Mandal                               06/27/2018                 REQ - 367101 AHT - Add Frequently Used Classification and Intent to Classification Dropdown (RF)
* Darshan Sharma                                07/12/2018                 REQ - 369793 Quality - Ability to display the AutoLogging Message for additional Classification and Intents (RF+CRM)
* Darshan Sharma                07/20/2018         REQ - 370598 Quality - Ability to disable the Case Creation in QS if the user does not have permissions to Create a Case for the Member in CRM (RF)
* Darshan Sharma                                08/20/2018                 REQ - 372751 - PCC - Reference Documents (RF)
* Subhamay Mandal                               09/04/2018                 REQ - 372704 Quality - Ability to Identify if a Case with a Classification was created within the Past 14 Days in QS. (RF)
* Subhamay Mandal                               09/14/2018                 REQ - 371313 PCC - Adjust backend code to support PCC, RF and Non Rf users in QS
* Darshan Sharma                                10/01/2018                 REQ - 374820: CTCI Model Update (RF+CRM)
* Asish   Behera                                09/24/2018                 REQ - 375046 Quality - Ability to display an Info Message at the top of QS if the Work Queue does not have Pretext (RF+CRM)
* Darshan Sharma                                11/19/2018                 REQ - 378991 AHT - Update - Ability to Identify if a Case with a Classification was created within the Past 14 Days in QS. (RF)
* Darshan Sharma                                11/30/2018                 REQ - 378965 PCC - Mentor Documents Set 1 (RF)
* Amar Gadewar                                  01/22/2019                 REQ - 383509 SF - TECH - Update PCC Quick Start Rules without 655 Permission Set. (RF)
* Subhamay Mandal                               01/23/2019                 REQ - 383554 User Story - Track usage of associate resolution (RF).  
* Amar Gadewar                                  04/11/2019                 REQ - 388904 - Code reallignment
* Subhamay Mandal                               05/22/2019                 REQ - 397111 Quality - OGO - Display Soft Warning Messages in QuickStart (RF)    
* Subhamay Mandal                               05/23/2019                 REQ - 397111 Code cleanup and improve performance
* Subhamay Mandal                               05/30/2019                 REQ - 387576, REQ - 387577, REQ - 388197 - OGO stories on Compplaint and GnA Rights Given on QS
* Subhamay Mandal                               06/14/2019                 REQ - 402004 SF - TECH - Quick Start Performance Enhancements (RF)
* Rakesh Patni                                  08/20/2019                 REQ - 406104. Launch Compass Funcitonality
* Melkisan Selvaraj                             11/14/2019                 REQ - 409548.
* Subhamay Mandal                               03/15/2020                 938292 QS 3.0 - AHT - Ability to create task from Quick Start (RF)
* Santhi Mandava                                05/02/2020                 User Story 938955: PR00091574 - Update Case Information Section to display "What is Call About" information instead of Classification and Intent fields (RF)
* Amar Gadewar                                  06/18/2020                 User Story 1203874: PR00091574 - MF 1 - RF Clean up (RF)
* Amar Gadewar                                  07/05/2020                 User Story 1247438: PR00091574 - MF 1 - SF - TECH - QS 3.0 Code Improvement and Knowledge cleanup (RF)
* Amar Gadewar                                  08/28/2020                 User Story 1386444: PR00091574 - MF 1 - Rollback "What is the Call About?" feature in Quick Start (RF)
* Sunil Kumar Reddy Y                           10/09/2020                 User Story 1474768: PR00091574 - MF 1 - Quality - Quick Start Code Improvement and Issue Fixes (RF)
* Samantha Tennyson								03/01/2021		    	   User Story 1986952: T1PRJ0001827 - MF 1 - AHT/Quality - QS 3.0 - Ability to display Pretext in Grievance and Appeals section based on Grievance/Appeal Reason selection (RF)
* Samantha Tennyson				   				06/14/2021	               User Story 2372808: T1PRJ0001827 - MF 1 - Quality/FCR - Ability to display "Recommended Actions" in Quick Start (RF)
* Amar Gadewar                                  06/28/2021                 User Story 2406575: T1PRJ0001827 - MF 1 - AHT/Quality - Ability to display alert in Quick Start if the Rep is creating a case with potential duplicate grievance (RF)
* Sunil Kumar Reddy Y                           07/26/2021                 User Story 2498928: T1PRJ0001827 - MF 1 - AHT/Quality - Display Informational Message when Case is available in History with Same Classification (RF)
* Jayesh Solanki				                08/30/2021		           User Story 2696518: T1PRJ0001827 - MF 1 - AHT/Quality - Highlight fields with errors in Quick Start (RF)
************************************************************************************************************************************************************************************/
public class QuickStart_S_AHT_HUM
{
    public static final String PRODUCT_TYPE_MES = 'MES';
    public static final String PRODUCT_TYPE_MEF = 'MEF';
    public static final String PRODUCT_TYPE_MEP = 'MEP';
    public static final String PRODUCT_TYPE_MGP = 'MGP';
    public static final String PRODUCT_TYPE_MGR = 'MGR';
    public static final String PRODUCT_TYPE_MPD = 'MPD';
    public static final String PRODUCT_TYPE_MRO = 'MRO';
    public static final String PRODUCT_TYPE_MRP = 'MRP';
    
    
    public static final List<String> lstComplaintsYes =  new List<String> {'Yes - Medicare Part C','Yes - Medicare Part D','Yes - Medicaid'};
    /*
    * Gets the User's Current Queue
    * 
    * @return string: User's Current Queue
    */  
    public static User evaluateUserQueue()
    {
        User oUser = QuickStart_D_AHT_HUM.queryUserQueue();
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
        List<Work_Queue_Setup__c> lstQueriedQueue = QuickStart_D_AHT_HUM.queryQueueByName(sQueueName);
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
            List<Policy_Member__c> lstCurrentPolMem = QuickStart_D_AHT_HUM.queryActivePolicyMember(sFocusedObjId);
            
            if(!lstCurrentPolMem.isEmpty())
            {
                oNewCase.Policy_Member__c =  lstCurrentPolMem[0].Id;
                String sActiveAccount = lstCurrentPolMem[0].Member__c;
                
                List<Account> lstCurrentAcct = QuickStart_D_AHT_HUM.queryActiveAccount(sActiveAccount);
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
            List<Account> lstCurrentAcct = QuickStart_D_AHT_HUM.queryActiveAccount(sFocusedObjId);
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
            List<Interaction_Member__c> lstActiveIntMember = QuickStart_D_AHT_HUM.queryActiveIntMem(oNewCase.AccountId, sInteractionId);
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

        List<Case> lstExistingCases = QuickStart_D_AHT_HUM.queryCasesByActiveInteraction(oNewCase);
        bHasDuplicate = !lstExistingCases.isEmpty();
        
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
        String sErrorDidNotSave = QuickStart_D_AHT_HUM.insertAssociatedCase(oNewCase);
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
        List<Quick_Start_Pretext_Section__c> lstofPreTextSections = QuickStart_D_AHT_HUM.queryPretextSectionData(sClassification,sIntent, idClassificationType);        
        String sPreTextdata = QuickStart_H_AHT_HUM.parsePreTextData(lstofPreTextSections);
        return sPreTextdata;
    }
        
    /*
    * Method to get Classification and Intent values for Call (RSO)
    * @param String: selected ClassificationType for user queue 
    * @return Map<Id, ClassificationIntent_DTO>
    *
    * @see: Pretext layout on QS window
    */
    public static Map<String, QuickStartCI_DTO_AHT_HUM> getQueueClassificationIntent(String sQueueClassificationType) {
        Map<String, QuickStartCI_DTO_AHT_HUM> mapQueueCI = new Map<String, QuickStartCI_DTO_AHT_HUM>();
        Id sCurrentId;
        Id sPreviousId;
        Map<Id, QuickStartCI_DTO_AHT_HUM.Intent_DTO_AHT_HUM> mapIntentInfo = new Map<Id, QuickStartCI_DTO_AHT_HUM.Intent_DTO_AHT_HUM>();
        CTCI_Junction__c ctciPrevious = null;
        for(CTCI_Junction__c ctci : QuickStart_D_AHT_HUM.queryCTCIByClassificationTypeCI(sQueueClassificationType)) {
            sCurrentId = ctci.Classification__c;
            if(String.isBlank(sPreviousId)) {
                mapIntentInfo.put(ctci.Intent__c, new QuickStartCI_DTO_AHT_HUM.Intent_DTO_AHT_HUM(ctci));
                sPreviousId = sCurrentId;
                ctciPrevious = ctci;
            } else if(sCurrentId != sPreviousId) {
                mapQueueCI.put(sPreviousId, new QuickStartCI_DTO_AHT_HUM(ctciPrevious, mapIntentInfo));
                
                mapIntentInfo = new Map<Id, QuickStartCI_DTO_AHT_HUM.Intent_DTO_AHT_HUM>();
                ctciPrevious = ctci;
                sPreviousId = sCurrentId;
                mapIntentInfo.put(ctci.Intent__c, new QuickStartCI_DTO_AHT_HUM.Intent_DTO_AHT_HUM(ctci));
            } else {
                mapIntentInfo.put(ctci.Intent__c, new QuickStartCI_DTO_AHT_HUM.Intent_DTO_AHT_HUM(ctci));
            }
        }
        if(ctciPrevious != null) {
            mapQueueCI.put(sCurrentId, new QuickStartCI_DTO_AHT_HUM(ctciPrevious, mapIntentInfo));
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
        
         List<CustomLink__c> lstAllMentorLink = QuickStart_D_AHT_HUM.queryAllCustomLink(idClassificationType,sBusinessGroup);
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
        for(Quick_Start_Pretext__c qspFrequentlyUsedCI : QuickStart_D_AHT_HUM.queryFrequentlyUsedCI(idClassificationType)) {
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
        List<UserRecordAccess> lstUserRecordAccess = QuickStart_D_AHT_HUM.queryUserRecordAccess(recordId);
            
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
        return  QuickStart_D_AHT_HUM.getBusinessGroup(sClassficationTypeId);
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
    public static QuickStartCI_DTO_AHT_HUM.AdditionalInfo_DTO_AHT_HUM fetchQSPAdditionalInfo(String sClassification, String sIntent, Id idClassificationType) {
        QuickStartCI_DTO_AHT_HUM.AdditionalInfo_DTO_AHT_HUM oAdditionalInfo;
        
        List<Quick_Start_Pretext__c> lstQSPAdditionalInfo = QuickStart_D_AHT_HUM.queryQSPAdditionalInfo(sClassification, sIntent, idClassificationType);
        if(!lstQSPAdditionalInfo.isEmpty()) {
            Quick_Start_Pretext__c tempQSP = lstQSPAdditionalInfo.get(0);
            oAdditionalInfo = new QuickStartCI_DTO_AHT_HUM.AdditionalInfo_DTO_AHT_HUM(tempQSP);
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
        String sErrorDidNotSave = QuickStart_D_AHT_HUM.insertAssociatedTask(oNewTask);
        return sErrorDidNotSave;
    }
    
    /**
    * Get the Pretext GA Details and convert it to JSON
    * @param String - sOGOFieldValue
    * @param String - sOGOReasonValue
    * @return String
    */
     
    public static String getPretextGAJSON(String sOGOFieldValue,String sOGOReasonValue) {
        List<Quick_Start_Pretext_Section__c> lstofPreTextSections = QuickStart_D_AHT_HUM.queryPretextGASectionData(sOGOFieldValue, sOGOReasonValue);
        String sPretextGAJSON = QuickStart_H_AHT_HUM.parsePreTextData(lstofPreTextSections);
        return sPretextGAJSON;
    }	

    /*
    * This is used to retrieve all icon documents
    * @param  Map<String,QuickStartCI_DTO_AHT_HUM> - QSP map
    * @return Map<String,List<String>> - Icons with document details
    */    
    public static Map<String,List<String>> getRecommendDocumentDetails(List<String> lstqspRecActions)
    {
        List<Document> lstDocuments = QuickStart_D_AHT_HUM.getRecommendedActionsDocuments();
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
    * This is used to get the Cases created in last 14 days having same Classification 

    */ 
    public static List<Case> verifyNoDuplicateClassification(String sAccountId, String sClassificationSelect){
        List<Case> caseList = QuickStart_D_AHT_HUM.queryCasesByClassification(sAccountId,sClassificationSelect);
        return caseList;
    }
   /*
    *This method gets if any Cases existing in last N days having same C/I and Complaint Value set

    */ 
    public static List<Case> verifyNoDuplicateGrievance(String sAccountId, String sClassificationSelect, String sIntentSelect, String sComplaint){
        List<Case> caseList = QuickStart_D_AHT_HUM.getCasesByLastNDays(sAccountId,sClassificationSelect,sIntentSelect,sComplaint);
        return caseList;
    }
    
    public static boolean verifyPolicyProduct(String policyId, String complaintVal){
       boolean  isPolicyProductType = false;
        List<Policy_Member__c> policyList = QuickStart_D_AHT_HUM.queryPolicyByClassification(policyId);
         for(Policy_Member__c pm : policyList){
            
               if(
                    ((pm.Product_Type_Code__c == GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_PDP) && (complaintVal.equals(GLOBAL_CONSTANT_HUM.MEDICAREPART_C) ||(complaintVal.equals(GLOBAL_CONSTANT_HUM.MEDICAID))))
                    ||
                    ((pm.Product_Type_Code__c == GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MAPD) && complaintVal.equals(GLOBAL_CONSTANT_HUM.MEDICAID)) 
              	    ||
                    ((pm.Product_Type_Code__c == GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MA) && (complaintVal.equals(GLOBAL_CONSTANT_HUM.MEDICAREPART_D) ||(complaintVal.equals(GLOBAL_CONSTANT_HUM.MEDICAID))))
                    ||
                    ((pm.Product_Type_Code__c =='' || pm.Product_Type_Code__c == null ) && (pm.Product_Type__c == PRODUCT_TYPE_MEF ||  
                                                      pm.Product_Type__c == GLOBAL_CONSTANT_HUM.MAJOR_LOB_MER || pm.Product_Type__c == PRODUCT_TYPE_MEP||
                                                      pm.Product_Type__c == PRODUCT_TYPE_MES || pm.Product_Type__c ==PRODUCT_TYPE_MGP || 
                                                      pm.Product_Type__c == PRODUCT_TYPE_MGR ||  pm.Product_Type__c == PRODUCT_TYPE_MPD || pm.Product_Type__c == PRODUCT_TYPE_MRO ||
                                                      pm.Product_Type__c == PRODUCT_TYPE_MRP ||  pm.Product_Type__c == GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_PDP)
                     								  &&  complaintVal.equals(GLOBAL_CONSTANT_HUM.MEDICAID) )
                    ||
                    ((pm.Product_Type__c == GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCD) && (complaintVal.equals(GLOBAL_CONSTANT_HUM.MEDICAREPART_D) ||(complaintVal.equals(GLOBAL_CONSTANT_HUM.MEDICAREPART_C))))
                    ||
                    ((complaintVal.equals(GLOBAL_CONSTANT_HUM.MEDICAREPART_D) ||(complaintVal.equals(GLOBAL_CONSTANT_HUM.MEDICAREPART_C)) || complaintVal.equals(GLOBAL_CONSTANT_HUM.MEDICAID)) &&
                     (!(pm.Product_Type__c == PRODUCT_TYPE_MEF || pm.Product_Type__c == GLOBAL_CONSTANT_HUM.MAJOR_LOB_MER ||  pm.Product_Type__c == PRODUCT_TYPE_MEP ||  
                        pm.Product_Type__c == PRODUCT_TYPE_MES || pm.Product_Type__c == PRODUCT_TYPE_MGP || pm.Product_Type__c == PRODUCT_TYPE_MGR || pm.Product_Type__c == PRODUCT_TYPE_MPD ||
                        pm.Product_Type__c == PRODUCT_TYPE_MRO || pm.Product_Type__c == PRODUCT_TYPE_MRP ||  pm.Product_Type__c == GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_PDP || 
                        pm.Product_Type__c ==GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCD))) 
                    
                  ) 
                {
                    isPolicyProductType = true;
                }else{
                    isPolicyProductType = false;
                }
    }
        return isPolicyProductType;
    }
}