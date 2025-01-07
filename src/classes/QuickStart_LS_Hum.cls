/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_LS_HUM.cls 
Created Date     : March 2, 2022
Function         : Executes SOQL in support of the Quick Start Console Component
Test Class       : QuickStart_LT_HUM

**Modification Log
* Developer Name          Code Review #               Date                       Description
* Jasmeen Shangari            -----                 03/02/2022                 Original Version
* Pooja Kumbhar						  			    08/16/2022  		       US:3272640 and US:3272641 - Reference and Mentor document section
* Pooja Kumbhar									    08/22/2022				   US:3272634 - Lightning - Quick Start - RCC Specific - G&A Complaint Section
* Pooja Kumbhar										09/12/2022				   US:3705153 - Lightning - Quick Start - RCC Specific - Task Information Section 
* Jasmeen Shangari									09/27/2022				   US:3230697 - Check to see if current user is RSOHP user and selected CI belongs to HP
* Disha Dole 										20/01/2023 		           US:4085171 - T1PRJ0865978 - C06, Lightning-Case Management- Quick Start-Associate to Policy, alignment & display issue fixes
************************************************************************************************************************************************************************************/

public with sharing class QuickStart_LS_Hum {
    public static final List<String> lstComplaintsYes =  new List<String> {'Yes - Medicare Part C','Yes - Medicare Part D','Yes - Medicaid'};
    /*
    * Gets the User's Current Queue
    * 
    * @return string: User's Current Queue
    */  
    public static User evaluateUserQueue()
    {
        User oUser = QuickStart_LD_HUM.queryUserQueue();
        return oUser;
    }
    
    /*
    * Gets the User's Current Queue
    * @param string: User's Current Queue 
    * @return Work_Queue_Setup__c: User's Current Work Queue
    */
    public static Work_Queue_Setup__c populateUserQueue(string sQueueName)
    {
        Work_Queue_Setup__c oQueue = new Work_Queue_Setup__c();
        List<Work_Queue_Setup__c> lstQueriedQueue = QuickStart_LD_HUM.queryQueueByName(sQueueName);
        if(!lstQueriedQueue.isEmpty()) {
            oQueue = lstQueriedQueue.get(0);
        }
        return oQueue;
    }

     /*
     * check Pretext configured for current workqueue
     * @param String: selected Classification Type
     * @return String: Business Group associated with ClassificationType
     */
    public static String checkPreTextAndBusinessGroup(String sClassficationTypeId)
    {
        return  QuickStart_LD_HUM.getBusinessGroup(sClassficationTypeId);
    }        

    /*
    * Method to get Classification and Intent values for Call (RSO)
    * @param String: selected ClassificationType for user queue 
    * @return Map<Id, ClassificationIntent_DTO>
    *
    * @see: Pretext layout on QS window
    */
    public static Map<String, QuickStartCTCI_LDTO_HUM> getQueueClassificationIntent(String sQueueClassificationType) {
        Map<String, QuickStartCTCI_LDTO_HUM> mapQueueCI = new Map<String, QuickStartCTCI_LDTO_HUM>();
        Id sCurrentId;
        Id sPreviousId;
        Map<Id, QuickStartCTCI_LDTO_HUM.Intent_DTO_HUM> mapIntentInfo = new Map<Id, QuickStartCTCI_LDTO_HUM.Intent_DTO_HUM>();
        CTCI_Junction__c ctciPrevious = null;
        for(CTCI_Junction__c ctci : QuickStart_LD_HUM.queryCTCIByClassificationTypeCI(sQueueClassificationType)) {
            sCurrentId = ctci.Classification__c;
            if(String.isBlank(sPreviousId)) {
                mapIntentInfo.put(ctci.Intent__c, new QuickStartCTCI_LDTO_HUM.Intent_DTO_HUM(ctci));
                sPreviousId = sCurrentId;
                ctciPrevious = ctci;
            } else if(sCurrentId != sPreviousId) {
                mapQueueCI.put(sPreviousId, new QuickStartCTCI_LDTO_HUM(ctciPrevious, mapIntentInfo));                
                mapIntentInfo = new Map<Id, QuickStartCTCI_LDTO_HUM.Intent_DTO_HUM>();
                ctciPrevious = ctci;
                sPreviousId = sCurrentId;
                mapIntentInfo.put(ctci.Intent__c, new QuickStartCTCI_LDTO_HUM.Intent_DTO_HUM(ctci));
            } else {
                mapIntentInfo.put(ctci.Intent__c, new QuickStartCTCI_LDTO_HUM.Intent_DTO_HUM(ctci));
            }
        }
        if(ctciPrevious != null) {
            mapQueueCI.put(sCurrentId, new QuickStartCTCI_LDTO_HUM(ctciPrevious, mapIntentInfo));
        }
        
        return mapQueueCI;
    }    

    /*
    * Fetch Frequently Used Classification and Intent
    * @param Id: Classification Type
    * @return Map<String,String>
    */
    public static Map<String,String> getFrequentlyUsedCI(Id idClassificationType) {
        Map<String,String> lstFrequentlyUsedCIOption = new Map<String,String>();
        for(Quick_Start_Pretext__c qspFrequentlyUsedCI : QuickStart_LD_HUM.queryFrequentlyUsedCI(idClassificationType)) {
            String strValue = qspFrequentlyUsedCI.CTCI_List_lkp__r.Classification__c + HUMConstants.sHiphan + qspFrequentlyUsedCI.CTCI_List_lkp__r.Intent__c;
            String strLabel = qspFrequentlyUsedCI.CTCI_List_lkp__r.Classification__r.Name +' :: '+ qspFrequentlyUsedCI.CTCI_List_lkp__r.Intent__r.Name;
            lstFrequentlyUsedCIOption.put(strValue, strLabel);
        }
        
        return lstFrequentlyUsedCIOption;
    }

    /*
    * Return picklist values for MedicarePartC or PartD field
    * @param : Empty
    * @return Map<String,String>: List of picklist value for MedicarePartC or PartD field
    */    
    public static Map<String,String> getMedicarePartCDPicklist()
    {
        return  QuickStart_LD_HUM.getPicklistValue();
    }     
     		
    /*
    * Method to fetch additional information for Clasiffication and Intent combination
    * 
    * @param Id: selected classification Id
    * @param Id: selected intent Id
    * @param Id: Classification Type
    * @param String: Classification Name	
    * @param String: Intent Name	
    * @return String: Qucik Start Pretext information
    *
    * @see: Pretext layout on QS window
    */
    public static QuickStart_LDTO_HUM.CommonInfo_DTO_HUM fetchQSPAdditionalInfo(Id idClassification, Id idIntent, Id idClassificationType, String sClassification, String sIntent) {
        QuickStart_LDTO_HUM.AdditionalInfo_DTO_HUM oAdditionalInfo;
        QuickStart_LDTO_HUM.CommonInfo_DTO_HUM oCommonInfo;
        List<String> lstqspRecActions;
        Map<String,List<String>> mapRAiconImages;
        Boolean bIsHPCase = false;
        List<Quick_Start_Pretext__c> lstQSPAdditionalInfo = QuickStart_LD_HUM.queryQSPAdditionalInfo(idClassification, idIntent, idClassificationType);

        //Check to validate if selected Classification & Intent combination belongs to HP cases
        if(HUMUtilityHelper.hasUserPharmacy206Access()){
            bIsHPCase = QuickStart_LH_Hum.isHPClassificationIntent(idClassification, idIntent,idClassificationType);
        }

        if(!lstQSPAdditionalInfo.isEmpty()) 
        {
            Quick_Start_Pretext__c tempQSP = lstQSPAdditionalInfo.get(0);
            oAdditionalInfo = new QuickStart_LDTO_HUM.AdditionalInfo_DTO_HUM(tempQSP);
            if(String.isNotBlank(oAdditionalInfo.sRecommendActions)) 
            {
                oAdditionalInfo.sRecommendActions = oAdditionalInfo.sRecommendActions.replaceAll('; ',';');
                lstqspRecActions = oAdditionalInfo.sRecommendActions.split(';');
                mapRAiconImages = getRecommendDocumentDetails(lstqspRecActions);
                if(!mapRAiconImages.isEmpty())
                {
                    oAdditionalInfo.mapRAiconImages = mapRAiconImages;
                }
            }                                 
        }  
        
        oCommonInfo = new QuickStart_LDTO_HUM.CommonInfo_DTO_HUM(oAdditionalInfo, bIsHPCase);                          
        return oCommonInfo;
    } 

	/*
    * This is used to retrieve all icon documents
    * @param  Map<String,QuickStartCI_DTO_HUM> - QSP map
    * @return Map<String,List<String>> - Icons with document details
    */    
    public static Map<String,List<String>> getRecommendDocumentDetails(List<String> lstqspRecActions)
    {
        List<Document> lstDocuments = QuickStart_LD_HUM.getRecommendedActionsDocuments();
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
                       // sDocId = objDoc.id; Commended by Surendra09/05
                       sDocId = URL.getSalesforceBaseURL().toExternalForm()+'/servlet/servlet.FileDownload?file='+objDoc.id;
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
    *This method gets if any Cases existing in last N days having same C/I and Complaint Value set

    */ 
    public static List<Case> verifyNoDuplicateGrievance(String accountID,String sClassificationSelect, String sIntentSelect, String sComplaint){
        List<Case> caseList = QuickStart_LD_HUM.getCasesByLastNDays(accountID,sClassificationSelect,sIntentSelect,sComplaint);
	
        return caseList;
    }
    /*
    * Fetch all Mentor and Reference Link doc on Quickstart page load
    * @param Id: Classification Type
    * @param String: Business Group
    * @return Map<String, List<customLink__c>>
    */
    public static Map<String,List<customLink__c>> fetchAllMentorLink(Id idClassificationType, String sBusinessGroup){
        
        Map<String,List<CustomLink__c>> mapCIMentor= new Map<String,List<CustomLink__c>>();
        
        List<CustomLink__c> lstAllMentorLink = QuickStart_LD_HUM.queryAllCustomLink(idClassificationType,sBusinessGroup);
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
	
	/**
    * assign task values from UI
    * @param Task oNewTask
    * @param String sSavedCaseId
    * @param String sTaskDueDate
    * @param Work_Queue_Setup__c oUserWorkQueue
    * @return void
    */
    public static void assginTaskDefaults(Task oNewTask, String sSavedCaseId, String sTaskDueDate, string serviceCenter, string department, string workqueuename, string username,string userId){
        oNewTask.WhatId = sSavedCaseId;
        oNewTask.OwnerID = userId;
        oNewTask.ActivityDate = String.isNotBlank(sTaskDueDate)? Date.valueOf(sTaskDueDate) : null;
        oNewTask.Service_Center__c = serviceCenter;
        oNewTask.Department__c =  department;
        oNewTask.Owner_Queue__c = workqueuename;
        oNewTask.Task_Owner__c = String.isNotBlank(workqueuename)? workqueuename : username;
        oNewTask.QueueOrUserId__c = userId;
        if(oNewTask.Type == HumConstants.HUMExtensionTask) {            
            oNewTask.Task_Owner__c = username;
            oNewTask.QueueOrUserId__c = userId;
        }
    }
    
     /**
    * save a new task record with Case
    * @param Task
    * @return void
    */
    public static String insertNewTask(Task oNewTask, string sTaskComment) {
        String sErrorDidNotSave = QuickStart_LD_HUM.insertAssociatedTask(oNewTask,sTaskComment);
        return sErrorDidNotSave;
    }
    
}