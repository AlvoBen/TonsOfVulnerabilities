/************************************************************************************************************************************************
Apex Class Name :  CaseRecommendationAction_S_HUM 
Version          : 1.0
Created Date     : Jun 23 2020
Function         : This class is used to for providing Case Recommendation for OGO Cases.
Test Class       : CaseRecommendationAction_T_HUM
*************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name                       Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Vinay Lingegowda                     06/24/2020                 Original Version
* 1.1                       Vijaykumar                           07/24/2020                 REQ - 1275884 - trigger auto clone case for Accept Recommendation
* 1.2                       Vinay Lingegowda                     07/24/2020                 US-1278736 and US-1275884 - PR00091574-IR-50-50 OGO-Link Original and cloned cases bi-directionally
* 1.3                       Vinay Lingegowda                     08/03/2020                 DF-1258 Fix.
* 1.4 			            Vinay Lingegowda                     08/11/2020                 USER STORY 1376901- INC0404513 - Update set case owner as owner queue for recommendation routing					
* 1.5                       Subhamay Mandal                      08/27/2020                 User Story 1293633: PR00091574-IR 5050-OGO - Recommendation actions on re-opened cases (reject )-(CRM)
* 1.6                       Subhamay Mandal                      08/28/2020                 User Story 1293633: SIT Observation
* 1.7                       Subhamay Mandal                      09/01/2020                 User Story 1293633: SIT Observation to update Work Queue View and Topic
* 1.8                       Subhamay Mandal                      09/03/2020                 User Story 1293633: SIT Observation to update Work Queue View
* 1.9		                Vijaykumar			                 09/04/2020	                User Story 1363774: SIT Observation		
* 2.0 			            Vinay Lingegowda                     16/11/2020                 USER STORY 1628353 PR00091574 - IR 5050 - OGO - Add a new View "Clarabridge Recommendations" for Re-opened cases (CRM)					
* 2.1                       Kavya Bondada                        11/18/2020                 User Story 1641595: PR00091574 - MF 1 - Quality - Ability for Rep to select "Was the Grievance Resolved during the call?" on Quick Start/Case Page (RF)
* 2.2                       Mithra Bharadwaj                                                US# 1990288    PR00094232 - 2021 MF6 - **P9 - Security** - Sharing  Remediate Checkmarx Security Scan results 
* 2.3                       Amar Gadewar                         05/14/2021                 User Story 2215231: T1PRJ0001827 - MF 1 - RF Clean Up - Was the Grievance Resolved during the Call? (RF)
* 2.4					    Mohammed Noor                        07/01/2021          		US2273201 - Case Management Checkmarx Security violations  
****************************************************************************************************************************************************************************/

public with sharing class CaseRecommendationAction_S_HUM{

    /**
    * Method to fetch case details.   
    * @param  String 
    * @return Case
    */
    public static Case getCaseDetails(String sCaseId) {
    
        Case oCase = CaseRecommendationAction_D_HUM.getCaseRecord(sCaseId);
        return oCase;
    }

    /**
     * Populate Task details
     * @param Case
     * @return Task
     */
    public static Task populateTaskDetails(Case oCaseRec) {
        Task oTask = new Task();
        if(Schema.SObjectType.Task.fields.RecordTypeId.isCreateable())  oTask.RecordTypeId = GLOBAL_CONSTANT_HUM.RECORDTYPE_ID_FEEDBACK_TASK;
        if(Schema.SObjectType.Task.fields.WhatId.isCreateable())    oTask.WhatId = oCaseRec.id;
        if(Schema.SObjectType.Task.fields.OwnerID.isCreateable())    oTask.OwnerID = UserInfo.getUserId();
        if(Schema.SObjectType.Task.fields.ActivityDate.isCreateable())    oTask.ActivityDate = Date.today();
        if(Schema.SObjectType.Task.fields.Priority.isCreateable())    oTask.Priority = GLOBAL_CONSTANT_HUM.NORMAL_HUM;
        if(Schema.SObjectType.Task.fields.Status.isCreateable())    oTask.Status =GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM;
        if(Schema.SObjectType.Task.fields.Owner_Queue__c.isCreateable())    oTask.Owner_Queue__c = oCaseRec.Owner_Queue__c;
        if(Schema.SObjectType.Task.fields.Task_Owner__c.isCreateable())    oTask.Task_Owner__c= UserInfo.getName();
        if(Schema.SObjectType.Task.fields.Created_By_Queue__c.isCreateable())    oTask.Created_By_Queue__c= oCaseRec.Owner_Queue__c;
        if(Schema.SObjectType.Task.fields.CallDisposition.isCreateable())    oTask.CallDisposition = oCaseRec.Work_Queue_View_Name__c;
        return oTask;
    }

    /**
    * save a new task record with Case
    * @param Task
    */
    public static String insertNewTask(Task oNewTask) {
        String sErrorDidNotSave = CaseRecommendationAction_D_HUM.insertTask(oNewTask);
        return sErrorDidNotSave;
    }

    /**
    * assign Case values 
    * @param String oFeedbackType
    * @return void
    */
    public static void assginCaseValues(Case oCaseRec, Boolean bSetComplaint, String sSetTopic){
        if(bSetComplaint) {
            oCaseRec.Complaint__c = 'No';
            oCaseRec.Complaint_Reason__c = null;
            oCaseRec.Complaint_Type__c = null;  
                      
        }

        if(String.isNotBlank(sSetTopic) && !sSetTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM)) {
            oCaseRec.Topic__c = sSetTopic;
        }
        oCaseRec.Case_Owner__c = oCaseRec.Owner_Queue__c;
        oCaseRec.OwnerId = CaseRecommendationAction_D_HUM.getWorkQ(oCaseRec.Owner_Queue__c);
    }
    /**
    * assign Case values back to user
    * @param Case oCaseRec
    * @return void
    */
    public static void assginCaseBackToUser(Case oCaseRec){
        User currentUser = [select id, current_queue__c,Profile.Name from User where id = :Userinfo.getUserId() LIMIT 1];
        String sPreviousQwnerQueue = oCaseRec.Previous_Owner_Queue__c;
        String sPreviousWorkQueueView = oCaseRec.Previous_Work_Queue_View__c;
        String sPreviousUserId = oCaseRec.Transferred_From__c;
        List<User> lstUser = [select id, Name, current_queue__c from User where IsActive = true and Id =: sPreviousUserId limit 1];
        List<Work_Queue_Setup__c> lstWQS = [SELECT Name, Service_Center__c, Department__c, Classification_Type__c, Classification_Type__r.Name from Work_Queue_Setup__c where Name =: sPreviousQwnerQueue AND IsActive__c = TRUE];
        List<CaseHistory> lstCaseHistory =  [SELECT NewValue, OldValue FROM CaseHistory where Field = 'Topic__c' and caseid =: oCaseRec.Id ORDER BY CreatedDate DESC limit 1];
        // set old case to Closed status
        oCaseRec.Status = GLOBAL_CONSTANT_HUM.HUM_CLOSED;
        // track old case value
        oCaseRec.LastModifiedby_Queue__c = currentUser.current_queue__c;
        oCaseRec.Previous_Owner_Queue__c = oCaseRec.Owner_Queue__c;
        oCaseRec.Previous_Work_Queue_View__c = oCaseRec.Work_Queue_View_Name__c;
        oCaseRec.Previous_Case_Owner_Role__c = currentUser.Profile.Name;
        if(!lstCaseHistory.isEmpty()) {
            String sNewTopic = (String)lstCaseHistory.get(0).NewValue;
            String sOldTopic = (String)lstCaseHistory.get(0).OldValue;
            if(String.isNotBlank(sNewTopic) && (sNewTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TOPIC_ORAL_GRIEVANCE_INTERNAL)|| sNewTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TOPIC_CLARABRIDGE_ORAL_GRIEVANCE_INTERNAL))) {
                oCaseRec.Topic__c = String.isNotBlank(sOldTopic) ? sOldTopic : '';
                oCaseRec.Work_Queue_View_Name__c = sPreviousWorkQueueView;
            } 
        }
        if(String.isNotBlank(sPreviousQwnerQueue)) {
            oCaseRec.Case_Owner__c = sPreviousQwnerQueue;
            oCaseRec.Owner_Queue__c = sPreviousQwnerQueue;
        }
        if(!lstWQS.isEmpty()) {
            Work_Queue_Setup__c oWorkQueueSetup = lstWQS.get(0);
            oCaseRec.Department__c = oWorkQueueSetup.Department__c;
            oCaseRec.Service_Center__c = oWorkQueueSetup.Service_Center__c;
            if(String.isNotBlank(oWorkQueueSetup.Classification_Type__c)) {
                oCaseRec.Classification_Type__c = oWorkQueueSetup.Classification_Type__r.name;
            } 
        }
        if(!lstUser.isEmpty()) {
            oCaseRec.OwnerId = lstUser.get(0).Id;
            oCaseRec.Case_Owner__c = lstUser.get(0).Name;
            oCaseRec.TRANSFERRED_FROM__C = null;
        } else {
            List<Group> lstGroup = [select id, Name from Group where Name =: sPreviousQwnerQueue and Type = 'Queue'];
            if(!lstGroup.isEmpty()) {
                oCaseRec.OwnerId = lstGroup.get(0).Id;
            }
        }
    } 
 
    /**
    * Update a original case record
    * @param case
    */
    public static String updateCaseFields(Case oCaseRec) {
        String sErrorDidNotSave = CaseRecommendationAction_D_HUM.updateRecommendCase(oCaseRec);
        return sErrorDidNotSave;
    }

    /**
    * Create Clone case and create required related information for Clone case record
    * @param String sOrigCaseRec
    * @param String sSetTopic
    * @return case
    */
    public static Case cloneCaseRecord(String sOrigCaseRec, String sSetTopic){
        Case oCloneCase = new Case();
        String sErrorMessage='';
        oCloneCase = assginNewCaseValues(sOrigCaseRec, sSetTopic);
        if(oCloneCase.Id != null) {
            List<Case_Interaction__c> lstOrigCaseInteraction = CaseRecommendationAction_D_HUM.getInteractionRecord(sOrigCaseRec);
            if(lstOrigCaseInteraction != null && !lstOrigCaseInteraction.isEmpty()){
                sErrorMessage = assigCloneInteraction(lstOrigCaseInteraction, oCloneCase.Id);
            }
            if(String.isBlank(sErrorMessage)) {
                sErrorMessage = linkClonedCase(sOrigCaseRec, oCloneCase.id);
                if(String.isNotBlank(sErrorMessage)) {
                    throw new CaseRecommendationAction_C_HUM.CaseRecommendationCustomException(sErrorMessage);
                }
            }else {
                    throw new CaseRecommendationAction_C_HUM.CaseRecommendationCustomException(sErrorMessage); 
            } 
        }
        return oCloneCase;
    }
    /**
    * Assign and create Clone case record
    * @param String sOldCaseRecId
    * @param String sSetTopic
    * @return case
    */
    public static Case assginNewCaseValues(String sOldCaseRecId, String sSetTopic){
        Case oNewCaseRec = new Case();
        List<Schema.FieldSetMember> lstCaseFldSet= SObjectType.case.FieldSets.Clone_Case_For_OGO.getFields();
        String sQuery ='SELECT ';
        for(Schema.FieldSetMember fldSet :lstCaseFldSet) {
            sQuery +=fldSet.getFieldPath() + ', ';
        }
        sQuery += 'id from case where id =: sOldCaseRecId';
        Case oOldCaseRec = Database.query(String.escapeSingleQuotes(sQuery));
        oNewCaseRec = oOldCaseRec.clone();
        if(oOldCaseRec.G_A_Rights_Given__c != GLOBAL_CONSTANT_HUM.NO_Complaint_GARights && Schema.SObjectType.Case.fields.G_A_Rights_Given__c.isCreateable()){
            oNewCaseRec.G_A_Rights_Given__c = null;
        }
        
        if(Schema.SObjectType.Case.fields.Department__c.isCreateable())   oNewCaseRec.Department__c = GLOBAL_CONSTANT_HUM.RSO_ORAL_GRIEVANCE_OVERSIGHT_TEAM;
        if(Schema.SObjectType.Case.fields.Service_Center__c.isCreateable())   oNewCaseRec.Service_Center__c= GLOBAL_CONSTANT_HUM.SERVICE_CENTER_LOUISVILLE;
        if(Schema.SObjectType.Case.fields.Autoroute_Status__c.isCreateable())   oNewCaseRec.Autoroute_Status__c = GLOBAL_CONSTANT_HUM.ROUTED_PENDING_ACCEPT;
        if(Schema.SObjectType.Case.fields.Case_Initiated_Date__c.isCreateable())   oNewCaseRec.Case_Initiated_Date__c = oOldCaseRec.CreatedDate.date();
        assginCaseValues(oNewCaseRec, true, sSetTopic);
        return CaseRecommendationAction_D_HUM.saveClonedCase(oNewCaseRec);
    }
    /**
    * Create linking between Original and clone case records
    * @param String sOrignalCaseId
    * @param String sClonedCaseId
    * @return String
    */
    public static String linkClonedCase(String sOrignalCaseId, String sClonedCaseId) {
        String sErrorDidNotCreateCaseLink='';
        Case_Junction__c oCaseJunRec = new Case_Junction__c();
        if(Schema.SObjectType.Case_Junction__c.fields.Case_Master__c.isCreateable())   oCaseJunRec.Case_Master__c = sOrignalCaseId;
        if(Schema.SObjectType.Case_Junction__c.fields.Case__c.isCreateable())   oCaseJunRec.Case__c = sClonedCaseId;
        if(Schema.SObjectType.Case_Junction__c.fields.IsCloneCase__c.isCreateable())   oCaseJunRec.IsCloneCase__c = true;
        if(Schema.SObjectType.Case_Junction__c.fields.Work_Queue__c.isCreateable())   oCaseJunRec.Work_Queue__c = CaseRecommendationAction_D_HUM.getWorkQueueId();
        sErrorDidNotCreateCaseLink = CaseRecommendationAction_D_HUM.saveCaseLinkRecords(oCaseJunRec);
        return sErrorDidNotCreateCaseLink;
    }
    /**
    * Clone Interation from Original case to clone case record
    * @param List Case_Interaction__c
    * @param String sCloneCaseId
    * @return String
    */
    public static String assigCloneInteraction(List<Case_Interaction__c> lstCaseInteraction, String sCloneCaseId){
        List<Case_Interaction__c> lstCloneCaseInteraction = new List<Case_Interaction__c>();
        String sErrorDidNotCreateInteraction ='';
        for(Case_Interaction__c caseInteraction: lstCaseInteraction){
            Case_Interaction__c oCaseInter = new Case_Interaction__c();
            if(Schema.SObjectType.Case_Interaction__c.fields.case__c.isCreateable())   oCaseInter.case__c=sCloneCaseId;                           
            if(Schema.SObjectType.Case_Interaction__c.fields.Interaction__c.isCreateable())   oCaseInter.Interaction__c=caseInteraction.Interaction__c;                
            lstCloneCaseInteraction.add(oCaseInter);
        }
        sErrorDidNotCreateInteraction = CaseRecommendationAction_D_HUM.saveCloneInteraction(lstCloneCaseInteraction);
        return sErrorDidNotCreateInteraction;
    }
}