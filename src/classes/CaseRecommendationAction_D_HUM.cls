/************************************************************************************************************************************************
Apex Class Name :  CaseRecommendationAction_D_HUM 
Version          : 1.0
Created Date     : Jun 29 2020
Function         : This class is used to for providing Case Recommendation for OGO Cases.
Test Class       :
*************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name                       Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Vinay Lingegowda                     06/29/2020                 Original Version
* 1.1                       Vijaykumar                           07/24/2020                 REQ - 1275884 - trigger auto clone case for Accept Recommendation
* 1.2                       Vinay Lingegowda                     07/24/2020                 US-1278736 - PR00091574-IR-50-50 OGO-Link Original and cloned cases bi-directionally
* 1.3                       Vinay Lingegowda                     08/11/2020                 USER STORY 1376901- INC0404513 - Update set case owner as owner queue for recommendation routing
* 1.4                       Subhamay Mandal                      09/01/2020                 User Story 1293633: SIT Observation to update Work Queue View and Topic
* 1.5                       Mithra Bharadwaj                                                US# 1990288    PR00094232 - 2021 MF6 - **P9 - Security** - Sharing  Remediate Checkmarx Security Scan results 
* 1.6					    Mohammed Noor                        07/01/2021          		US2273201 - Case Management Checkmarx Security violations  
****************************************************************************************************************************************************************************/

public with sharing class CaseRecommendationAction_D_HUM{
    
    /**
    * Get case record details
    * @pram String
    * @return Case
    */
    public static Case getCaseRecord(String sCaseId) {
        Case oCase = null;
        if(String.isNotBlank(sCaseId)) {
             oCase = [Select Id, CaseNumber, Complaint__c, Autoroute_Status__c, Complaint_Reason__c, Complaint_Type__c, Topic__c, Recordtype.Name, Work_Queue_View_Name__c, Owner_Queue__c, 
                            OwnerId, Status, Policy_Member__c, Created_By_Queue__c, Previous_Owner_Queue__c, Previous_Work_Queue_View__c, CreatedBy.Current_Queue__c, Transferred_From__c From Case Where Id = :sCaseId];
        }
        
        return oCase;
        
    }
    /*
    * Inserts the the Associated Task
    * 
    * @param Task: Task to insert
    * @return String: Returns null if no errors occurred during the save
    */  
    public static String insertTask(Task oNewTask) {
        String sFeedbackComments = oNewTask.Description;
        if(Schema.SObjectType.Task.fields.Description.isCreateable())	oNewTask.Description = '';

        String sHasSaveError = '';
        database.SaveResult oSaveSuccess = database.insert(oNewTask, false);
        if(!oSaveSuccess.isSuccess()) {
            for(Database.Error err : oSaveSuccess.getErrors()) {
                String sErroFieldLabels = '';
                for(String sErrorField : err.getFields()) {
                    Schema.SObjectField sTemplabel = Constants_C_AHT_HUM.MAP_TASK_FIELDS.get(sErrorField);
                    if(sTemplabel != null) {
                        sErroFieldLabels += sTemplabel.getDescribe().getLabel() +', ';  
                    }
                }
                if(sErroFieldLabels.length() > 1) {
                    sErroFieldLabels = sErroFieldLabels.substring(0, sErroFieldLabels.trim().length() -1);
                }
                if(String.isNotBlank(sErroFieldLabels)) {
                    sHasSaveError += sErroFieldLabels +': '+ err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';
                } else {
                    sHasSaveError += err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';   
                }
            }
        } else {
            if(String.isNotBlank(sFeedbackComments)) {
                Task_Field_History__c oTaskFH = new Task_Field_History__c ();
                if(Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isCreateable())    oTaskFH.SObjID__c = oNewTask.Id;
                if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isCreateable())  oTaskFH.SObjFieldName__c = GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM;
                if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isCreateable())    oTaskFH.SObjFieldValue__c= sFeedbackComments;        
                if(Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isCreateable())    oTaskFH.Created_At__c = System.now(); 
                Database.SaveResult insertResult1 =Database.insert(oTaskFH,false);
            }
        }
        return sHasSaveError;
    }

    /*
    * Inserts the the Associated Case
    * 
    * @param Task: Case to Update
    * @return String: Returns null if no errors occurred during the save
    */ 
    public static String updateRecommendCase(Case oCaseRec) {
        String sHasSaveError = '';
        database.SaveResult oSaveSuccess = database.update(oCaseRec, false);        
        if(!oSaveSuccess.isSuccess()) {
            for(Database.Error err : oSaveSuccess.getErrors()) {
                String sErroFieldLabels = '';
                for(String sErrorField : err.getFields()) {
                    Schema.SObjectField sTemplabel = Constants_C_AHT_HUM.MAP_CASE_FIELDS.get(sErrorField);
                    if(sTemplabel != null) {
                        sErroFieldLabels += sTemplabel.getDescribe().getLabel() +', ';  
                    }
                }
                if(sErroFieldLabels.length() > 1) {
                    sErroFieldLabels = sErroFieldLabels.substring(0, sErroFieldLabels.trim().length() -1);
                }
                if(String.isNotBlank(sErroFieldLabels)) {
                    sHasSaveError += sErroFieldLabels +': '+ err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';
                } else {
                    sHasSaveError += err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';   
                }
            }
        }
        return sHasSaveError;
    }

	 /*
    * Inserts the the close Case
    * @param Case
    * @return case
    */ 
    public static Case saveClonedCase(Case oNewCaseRec) { 
        String sHasSaveError = '';
        database.SaveResult oSaveSuccess = database.insert(oNewCaseRec, false);
        if(!oSaveSuccess.isSuccess()) {
            for(Database.Error err : oSaveSuccess.getErrors()) {
                String sErroFieldLabels = '';
                for(String sErrorField : err.getFields()) {
                    Schema.SObjectField sTemplabel = Constants_C_AHT_HUM.MAP_CASE_FIELDS.get(sErrorField);
                    if(sTemplabel != null) {
                        sErroFieldLabels += sTemplabel.getDescribe().getLabel() +', ';  
                    }
                }
                if(sErroFieldLabels.length() > 1) {
                    sErroFieldLabels = sErroFieldLabels.substring(0, sErroFieldLabels.trim().length() -1);
                }
                if(String.isNotBlank(sErroFieldLabels)) {
                    sHasSaveError += sErroFieldLabels +': '+ err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';
                } else {
                    sHasSaveError += err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';   
                }
            }
        }
        return  oNewCaseRec;
    }
    /**
    * Get Interaction records for original case to clone case
    * @pram String
    * @return 
    */
    public static List<Case_Interaction__c> getInteractionRecord(String sOrigCaseId) {
        List<Case_Interaction__c> lstCaseInteraction = [SELECT Id,Interaction__c FROM Case_Interaction__c WHERE case__c = : sOrigCaseId];
        return lstCaseInteraction;  
    }

    /**
    * Clone interaction from original case to clone case
    * @pram String
    * @return 
    */
    public static String saveCloneInteraction(list<Case_Interaction__c> lstCloneCaseInteraction) { 
        String sHasSaveError = '';
        database.SaveResult[] oSaveSuccess = database.insert(lstCloneCaseInteraction, false);
        for(Database.SaveResult saveSucc : oSaveSuccess) {
            if(!saveSucc.isSuccess()) {
                for(Database.Error err : saveSucc.getErrors()) {
                    String sErroFieldLabels = '';
                    for(String sErrorField : err.getFields()) {
                        Schema.SObjectField sTemplabel = GLOBAL_CONSTANT_HUM.MAP_CASEINTERACTION_FIELDS.get(sErrorField);
                        if(sTemplabel != null) {
                            sErroFieldLabels += sTemplabel.getDescribe().getLabel() +', ';  
                        }
                    }
                    if(sErroFieldLabels.length() > 1) {
                        sErroFieldLabels = sErroFieldLabels.substring(0, sErroFieldLabels.trim().length() -1);
                    }
                    if(String.isNotBlank(sErroFieldLabels)) {
                        sHasSaveError += sErroFieldLabels +': '+ err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';
                    } else {
                        sHasSaveError += err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';   
                    }
                }
            }
        }
        return  sHasSaveError;
    }
    /**
    * Get work queue id
    * @return Id
    */
    public static Id getWorkQueueId()
    {
        Id wQId = Null;
        Work_Queue_Setup__C oWQS = [Select Id from Work_Queue_Setup__C where Name =:GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT AND IsActive__c = TRUE];
        if(oWQS != Null) wQId = oWQS.Id;
        return wQId;
    }
    /**
    * Get work queue
    * @return Id
    */
    public static Id getWorkQ(String sOwnerQueue)
    {
        Id wQId = Null;
        Group oGroup = [select Id from Group where  Type = 'Queue' AND NAME =: sOwnerQueue];
        if(oGroup != Null) wQId = oGroup.Id;
        return wQId;
    }
    /**
    * Link original case to clone case
    * @pram Case_Junction__c linkCasesRec
    * @return String
    */
    public static String saveCaseLinkRecords(Case_Junction__c linkCasesRec) {
        String sHasSaveError = '';
        Database.SaveResult oSaveSuccess = database.insert(linkCasesRec, false);
        if(!oSaveSuccess.isSuccess()) {
            for(Database.Error err : oSaveSuccess.getErrors()) {
                String sErroFieldLabels = '';
                for(String sErrorField : err.getFields()) {
                    Schema.SObjectField sTemplabel = GLOBAL_CONSTANT_HUM.MAP_CASELINK_FIELDS.get(sErrorField);
                    if(sTemplabel != null) {
                        sErroFieldLabels += sTemplabel.getDescribe().getLabel() +', ';  
                    }
                }
                if(sErroFieldLabels.length() > 1) {
                    sErroFieldLabels = sErroFieldLabels.substring(0, sErroFieldLabels.trim().length() -1);
                }
                if(String.isNotBlank(sErroFieldLabels)) {
                    sHasSaveError += sErroFieldLabels +': '+ err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';
                } else {
                    sHasSaveError += err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';   
                }
            }
        }
        return sHasSaveError;
    }
}