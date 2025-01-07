/*******************************************************************************************************************************
Apex Name : TaskCommentsDataTable_LD_HUM
Function    : This class is main class for task comment component

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Shailesh Bagade                                        22/06/2022                  initial version 
* Shailesh Bagade                                        28/07/2022                  US-3560805 Close Tasks/Activity Section of Case Details 
* Prasuna Pattabhi                                       08/24/2023                  US-4412371 MCD Fields 
* Prasuna Pattabhi                                      10/04/23                 US 4412371 Market Credentialing Task Fields Switch Changes
*********************************************************************************************************************************/

public with sharing class TaskCommentsDataTable_LD_HUM {
        
    public static string NO_DUE_DATE = 'No due date';
    public static string TASK_STATUS_CLOSED = 'Closed';
    public static string userProfileName;
    public static string TASKOBJ = 'Task';
    public static string TASK_FIELD_HISTORY = 'Task_Field_History__c';
    /*
    * Method Name : getCurrentUserProfileName
    * Description : get current user profile name.
    * Return type : String
    * Parameter   : NA
    */  
    @AuraEnabled 
    public static String getCurrentUserProfileName()
    {
        Id profileId=userinfo.getProfileId();
        return [Select Id,Name from Profile where Id=:profileId].Name;
    }
    /**
    * Method to get list of case comments
    *  @name generateTaskComments
    *  @param String objID
    *  @return String
    */     
    @AuraEnabled
    public static String generateTaskComments(String objID)
    {
        List<Task_Field_History__c> listOfTaskFieldH =  [SELECT ID,Created_At__c,SObjFieldName__c,SObjFieldValue__c,CreatedBy.Name,
                                                    ParentFieldHistory_Id__c,SObjID__c, LastModifiedDate, LastModifiedBy.Name , CreatedDate
                                                    from Task_Field_History__c WHERE  SObjFieldName__c In ('Description')
                                                    and SObjID__c =:objID ORDER BY LastModifiedDate DESC  ];

        return getTaskCommentWrapper(listOfTaskFieldH);
    }
    
	/**
    * Method to get check user access for dropdown to task comment
    *  @name checkPermissionForCommentDropDown
    *  @param 
    *  @return Boolean
    */  
    public static Boolean checkPermissionForCommentDropDown()
    {	
        userProfileName=CaseCommentsDataTable_LD_HUM.getCurrentUserProfileName();
        if(userProfileName == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.SYSTEM_ADMIN ||
            userProfileName == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CUSTOMER_CARE_SUPERVISOR|| 
            Pharmacy_LH_HUM.isPharmacyUser() == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.YES_SUPERVISOR ||
            userProfileName == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.HUMANA_PHARMACY_SPECIALIST || 
            userProfileName == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CUSTOMER_CARE_SPECIALIST )
        {
            return true;
            

        }
        return false;
    }

    /*
    * Method Name   :    getTaskCommentWrapper
    * Description   :    This feches the wrapper of task comments
    * Return Type   :    String
    * Parameters    :    List<Task_Field_History__c>
    */
    private static String getTaskCommentWrapper(List<Task_Field_History__c> lstTaskComments)
    {   
        try {
            
            TaskComments_DTO_HUM caseCommentWrapper= new TaskComments_DTO_HUM();
            
            if(lstTaskComments !=NULL )
            {   
                    //Method to check for Task comment Drop down visibilty, and edit and delete functionality
                    Boolean bDropDownVisible = checkPermissionForCommentDropDown();
                    
                    Boolean bUpdatable  = Schema.getGlobalDescribe().get(TASK_FIELD_HISTORY).getDescribe().isUpdateable();
                    Boolean bDeletable   = Schema.getGlobalDescribe().get(TASK_FIELD_HISTORY).getDescribe().isDeletable();
                    
                    List<TimelineCaseComments_DTO_HUM> lstCaseWrap = new List<TimelineCaseComments_DTO_HUM>();
                
                    for(Task_Field_History__c oTaskComment :lstTaskComments )
                    {
                        TimelineCaseComments_DTO_HUM oTaskWrap = new TimelineCaseComments_DTO_HUM();
                        oTaskWrap.caseCommentId = oTaskComment.Id;
                        oTaskWrap.LogNoteCode = oTaskComment.LastModifiedBy.Name ;
                        oTaskWrap.LogNoteDate = (oTaskComment.CreatedDate!=null) ?  oTaskComment.CreatedDate.format('h:mm a') + ' | ' + oTaskComment.CreatedDate.format('MM/dd/yyyy') :'';
                        oTaskWrap.LogNoteMessage =oTaskComment.SObjFieldValue__c;
                        oTaskWrap.LogNoteUser = oTaskComment.CreatedBy.Name;
                        oTaskWrap.LogNoteLastModified = (oTaskComment.LastModifiedDate!=null) ?  oTaskComment.LastModifiedDate.format('h:mm a') + ' | ' + oTaskComment.LastModifiedDate.format('MM/dd/yyyy') :'';
                        oTaskWrap.isDropDownVisible = bDropDownVisible;
                        
                        if(bDropDownVisible)
                        {
                            oTaskWrap.canEditTask = oTaskComment.CreatedBy.Id == UserInfo.GetUserId() && bUpdatable;
                            oTaskWrap.canCreateTask = oTaskComment.CreatedBy.Id == UserInfo.GetUserId() && bDeletable && userProfileName != GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CUSTOMER_CARE_SPECIALIST ; 
                        }

                        if(!oTaskWrap.canEditTask && !oTaskWrap.canCreateTask)
                        {
                            oTaskWrap.isDropDownVisible=false;
                        }
                        
                        lstCaseWrap.add(oTaskWrap);
                    }
                    
                    caseCommentWrapper.lTaskCommentDTO = lstCaseWrap;
                    caseCommentWrapper.sProfileName = getCurrentUserProfileName();
                    
                    return JSON.serialize(caseCommentWrapper);
            }
            else
            {
                return null;
            }
        } catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'TaskCommentsDataTable_LD_HUM', 'getTaskCommentWrapper'); 
            throw new HUMCustomException(ex.getMessage()); 
        }
    }

    /*
    * Method Name   :    getTaskRelatedToCase
    * Description   :    This fmethod fetches all the task related to case
    * Return Type   :    String
    * Parameters    :    String objID
    */
    @AuraEnabled
    public static String getTaskRelatedToCase(String objID)
    {
        TaskComments_DTO_HUM taskCommentWrapper= new TaskComments_DTO_HUM();                  
       String paramTaskId = objID;
        List<Task> lstTask = new List<Task>();
        if(String.isNotBlank(paramTaskId) && Task.SObjectType.getDescribe().isAccessible()){
            lstTask = [select id, WhatId,Task_Number__c, ActivityDate,Status,Priority, Type, LastModifiedDate, LastModifiedBy.name from task where whatId =:paramTaskId order by ActivityDate NULLS LAST];
        }
        Set<Id> setOfTaskId = new set<Id>();

        for(Task oTask : lstTask){
            setOfTaskId.add(oTask.Id);
        }
        if(lstTask.size() > 0){

            Boolean bEditable  = Schema.getGlobalDescribe().get(TASKOBJ).getDescribe().isUpdateable();
            Boolean bCreatable  = Schema.getGlobalDescribe().get(TASKOBJ).getDescribe().isCreateable();
            Boolean bStatusUpdatable = Schema.SObjectType.Task.fields.Status.isUpdateable();
            
            List<TimelineCaseComments_DTO_HUM> lstCaseWrap = new List<TimelineCaseComments_DTO_HUM>();
            List<Task_Field_History__c> taskComments;

            if(Task_Field_History__c.SObjectType.getDescribe().isAccessible()){
                taskComments =  [SELECT ID,Created_At__c,SObjFieldName__c,SObjFieldValue__c,CreatedBy.Name,
                                                            ParentFieldHistory_Id__c,SObjID__c, LastModifiedDate, LastModifiedBy.Name , CreatedDate
                                                            from Task_Field_History__c WHERE  SObjFieldName__c In ('Description')
                                                            and SObjID__c IN :setOfTaskId ORDER BY LastModifiedDate ASC  ];
            }
        
            map<String, String> mapTaskIdToTaskComment  = new map<String, String>();
            for(Task_Field_History__c taskH : taskComments){
                mapTaskIdToTaskComment.put(taskH.SObjID__c, taskH.SObjFieldValue__c);
            }

            for(Task myTask : lstTask){

                TimelineCaseComments_DTO_HUM oTaskWrap = new TimelineCaseComments_DTO_HUM();
                oTaskWrap.caseCommentId = myTask.Id;
                oTaskWrap.LogNoteCode = myTask.LastModifiedBy.Name ;
                if(myTask.ActivityDate!=null){
                    DateTime acdatetime = myTask.ActivityDate;
                    oTaskWrap.LogNoteDate =  acdatetime.formatGMT('MMM dd, yyyy');
                }
                else {
                    oTaskWrap.LogNoteDate = NO_DUE_DATE;
                }
                oTaskWrap.LogNoteMessage ='You have an <strong>'+ myTask.status + ' ' + myTask.Type+ '</strong>  with <strong>'+ myTask.Priority + '</strong> priority';
                oTaskWrap.LogNoteUser = myTask.Task_Number__c;
                oTaskWrap.LogNoteLastModified = (myTask.LastModifiedDate!=null) ?  myTask.LastModifiedDate.format('h:mm a') + ' | ' + myTask.LastModifiedDate.format('MM/dd/yyyy') :'';
                oTaskWrap.isDropDownVisible = true;
                //canEditTask - if user can edit task
                oTaskWrap.canEditTask = bEditable && myTask.Status != TASK_STATUS_CLOSED;
                oTaskWrap.LogNoteComment = mapTaskIdToTaskComment.get(myTask.Id);
                //canCreateTask - user can dependent task
                oTaskWrap.canCreateTask = bCreatable;
                 //check whether user can close the task
                 oTaskWrap.canCloseTask = bStatusUpdatable && bEditable && myTask.Status != TASK_STATUS_CLOSED;
                
                lstCaseWrap.add(oTaskWrap);
            }
            taskCommentWrapper.lTaskCommentDTO = lstCaseWrap;
            taskCommentWrapper.sProfileName = getCurrentUserProfileName();
            
            return JSON.serialize(taskCommentWrapper);
        }
        else{
            taskCommentWrapper.lTaskCommentDTO = new List<TimelineCaseComments_DTO_HUM>();
            taskCommentWrapper.sProfileName = getCurrentUserProfileName();
            return JSON.serialize(taskCommentWrapper);
        }

    }

    /*
    * Method Name   :    saveTaskCommentData
    * Description   :    This method is used to save the task comment
    * Return Type   :    String
    * Parameters    :    String sTaskId, String taskCommentData
    */
    @AuraEnabled
    public static String saveTaskCommentData(String sTaskId, String taskCommentData)
    {
        try {
            List<Task_Field_History__c> lstTaskComment = (List<Task_Field_History__c>) JSON.deserialize(taskCommentData, List<Task_Field_History__c>.class);
            List<Task_Field_History__c> lstComments= new List<Task_Field_History__c>();

            for(Task_Field_History__c oTaskH: lstTaskComment)
            {
                oTaskH.SObjID__c = sTaskId;
                oTaskH.SObjFieldName__c = GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM;
                oTaskH.Created_At__c = System.now();
                lstComments.add(oTaskH);
            }
            Lib_Security_LH_HUm.assertCreate(lstComments);
            insert lstComments;

            return GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CASECOMMENT_INSERTED;

        } catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'TaskCommentsDataTable_LD_HUM', 'saveTaskCommentData');
            throw new HUMCustomException(ex.getMessage()); 
        }
    }
    

    /*
    * Method Name   :    closeTask
    * Description   :    This method is used to close the task 
    * Return Type   :    String
    * Parameters    :    String taskId
    */
    @AuraEnabled
    public static String closeTask(String taskId){
        string paramTaskId = taskId;
        
        try { 
            Task sTaskRec;
            String taskNumber;
            if(String.isNotBlank(paramTaskId) && Task.SObjectType.getDescribe().isAccessible()) {
                sTaskRec = [select Id,Task_Number__c,Status from Task where Id =:paramTaskId limit 1];
            }
            if(sTaskRec !=NULL){
                taskNumber=sTaskRec.Task_Number__c;
            
                                    
                if(paramTaskId != null && sTaskRec.Status != Label.HUMTaskStatusClosed){            
                    sTaskRec.Status = Label.HUMTaskStatusClosed;
                    if(Schema.SObjectType.Task.fields.Status.isUpdateable() && Schema.getGlobalDescribe().get(TASKOBJ).getDescribe().isUpdateable()){
                        update(sTaskRec);
                    }
                } 
                return taskNumber;  
            }  
            return null;     
        } 
        catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'TaskCommentsDataTable_LD_HUM', 'closeTask'); 
            throw new HUMCustomException(e.getMessage()); 
        }
        
    } 
    
    /*
    * Method Name   :    updateTaskCommentData
    * Description   :    This method is used to update the task comment
    * Return Type   :    Boolean
    * Parameters    :    String sTaskId, String taskCommentData
    */
    public static Boolean updateTaskCommentData(String sTaskId, String taskCommentData)
    {
        try {
            
            Task_Field_History__c oTaskH = new Task_Field_History__c();
            oTaskH.Id = sTaskId;
            oTaskH.SObjFieldValue__c =  taskCommentData;     
            oTaskH.SObjFieldName__c = GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM;
            
            Database.SaveResult sr =Database.update(oTaskH);
            return sr.isSuccess() ? true : false;

        } catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'TaskCommentsDataTable_LD_HUM', 'updateTaskCommentData');  
            return false; 
        }

    }
    /*
    * Class used to hold the TimelienCaseComment and profile
    */
    class TaskComments_DTO_HUM{
        public List<TimelineCaseComments_DTO_HUM> lTaskCommentDTO;
        public String sProfileName;
    }

    class TimelineCaseComments_DTO_HUM{
        @AuraEnabled public String LogNoteCode = '';
        @AuraEnabled public String LogNoteDate = ''; 
        @AuraEnabled public String LogNoteKey = '';  
        @AuraEnabled public String LogNoteMessage = ''; 
        @AuraEnabled public String LogNoteComment = '';  
        @AuraEnabled public String LogNoteUser = '';
        @AuraEnabled public String LogNoteLastModified = ''; 
        @AuraEnabled public String caseCommentId='';
        @AuraEnabled public Boolean isDropDownVisible= false;
        @AuraEnabled public Boolean canEditTask= false;
        @AuraEnabled public Boolean canCreateTask= false;
        @AuraEnabled public Boolean canCloseTask= false;
    }

    /*
        * Method Name : credentialingClosable
        * Description : Check if the task can be closed or not
        * Return type : boolean
        * Parameter   : String caseId
    */  
    @AuraEnabled 
    public static boolean credentialingClosable(Id Id){
        boolean isClosable = false;
        if(HUMUtilityHelper.isCRMFunctionalityON('4412371')){
        Id caseId;
        List<Task> lstTask = [select WhatId,Credentialing_Task__c,Task_Completion_Date__c from Task where Id=:Id];
        if(!lstTask.isEmpty()){
            caseId = lstTask[0].WhatId;
            List<Case> lstCase = [select id,Owner_Queue__c,Classification_Id__r.name , Intent_Id__r.name from Case Where Id=:caseId];
            if(!lstCase.isEmpty()){
                if(lstCase[0].Owner_Queue__c == CaseEditConstants_HUM.MCD_OWNER_Q && 
                lstCase[0].Classification_Id__r.name == CaseEditConstants_HUM.MCD_Classification && 
                lstCase[0].Intent_Id__r.name == CaseEditConstants_HUM.MCD_Intent &&
                (String.isBlank(lstTask[0].Credentialing_Task__c) ||lstTask[0].Task_Completion_Date__c == null)){
                    isClosable = true;
                }
            }
        }  
        }    
        return isClosable;
    }
}