/************************************************************************************************************************************************************************************    
Apex Class Name :     WorkItems_LC_HUM 
Created Date     : 02/07/2023
Function         :    
**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Shaik Mujeebur Rahaman                        02/07/2023                US-4129681 T1PRJ0170850 - MF 22334 - Lightning - Today's Task (My Due Work)
* Shaik Mujeebur Rahaman                        03/04/2023                US-4424960 T1PRJ0170850 - MF 22334 - Lightning -My Due Work Component Overlays 
************************************************************************************************************************************************************************************/

Public Class WorkItems_LC_HUM {
    Public Static Integer iOverDueCount{Get;Set;}
    Public Static Integer iNearDueCount{Get;Set;}
    
    Public WorkItems_LC_HUM()
    {
        init();
    }
    
    Public void init()
    {
        iOverDueCount = 0;
        iNearDueCount = 0;
    }
    Public Static void populateWorkItems()
    {
        Try
        {
            getOverdueTasks();
            getNeardueTasks();
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'WorkItems_LC_HUM', 'populateWorkItems');
        }
    }
    
    @AuraEnabled
    Public Static List<DueWrapper> getOverdueTasks(){
        
        List<String> lstValidCaseStatus = New List<String>{GLOBAL_CONSTANT_HUM.TASKLISTVIEW_INPROGRESS_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDING_HUM};
            List<String> lstValidTaskStatus = New List<String>{GLOBAL_CONSTANT_HUM.TASKLISTVIEW_INPROGRESS_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGTASK_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGRESPONSE_HUM};
                Integer dataLimit = GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_SIX_HUM; // Data Limit , currently set to six
        Integer queryLimit = 3 * dataLimit;
        List<DueWrapper> overdueWrapper = New List<DueWrapper>();
        
        List<Task> overdueTasks = [Select Id,task_number__c,ActivityDate , createdDate, Description  from Task where OwnerId= 
                                   :userinfo.getuserid() And ActivityDate <= :System.Today() And Status In :lstValidTaskStatus order by ActivityDate ASC , Createddate ASC  limit : queryLimit];
        
        List<Id> taskIds = new List<Id>();
        for(Task tsk: overdueTasks){
            taskIds.add(tsk.Id);
        }
        List<Task_Field_History__c> listOfTaskFieldH =  [SELECT ID,CreatedDate,SObjFieldName__c,SObjFieldValue__c,SObjID__c 
                                                         FROM Task_Field_History__c WHERE  SObjFieldName__c = 'Description' 
                                                         And SObjID__c IN :taskIds ORDER BY LastModifiedDate DESC ];
        
        List<Case> overdueCases = [Select Id ,Follow_Up_Due_Date__c , CaseNUmber , createdDate from Case where ownerid=:userinfo.getuserid() And Follow_Up_Due_Date__c <= :System.Today() And Status IN :lstValidCaseStatus order by Follow_Up_Due_Date__c ASC , Createddate ASC limit : queryLimit];
        List<CaseComment> overdueCasesComments = [SELECT Id, CommentBody, ParentId, FORMAT(CreatedDate) FROM CaseComment where Parent.ownerid=:userinfo.getuserid() ORDER BY CreatedDate DESC];
        
        For (Task overdue: overdueTasks){
            overdueWrapper.add(New DueWrapper(overdue.Id, overdue.task_number__c, overdue.ActivityDate));
        }
        For (Case overdue: overdueCases){
            overdueWrapper.add(New DueWrapper(overdue.Id, overdue.CaseNumber, overdue.Follow_Up_Due_Date__c));
        }
        iOverDueCount=overdueWrapper.size();
        //Comment Section Overlay
        For (DueWrapper odWrapper: overdueWrapper) {
            odWrapper.comment = 'No Comment found';
            For (CaseComment overdueCc: overdueCasesComments) {
                If (odWrapper.Id == overdueCc.ParentId) {
                    odWrapper.comment = 'Last Case comment created on ' + overdueCc.CreatedDate.format('MM/dd/YYYY\' \'hh:mm\' \'a') + '. The comment is "' + overdueCc.CommentBody + '"';
                    break;           
                }                                               
            }
            For (Task overdueTsk: overdueTasks) {
                for (Task_Field_History__c tfh: listOfTaskFieldH) {
                    If (odWrapper.Id == tfh.SObjID__c) {
                        If (tfh.SObjFieldValue__c != null){
                            odWrapper.comment = 'Last Taskcomment created on ' + tfh.CreatedDate.format('MM/dd/YYYY\' \'hh:mm\' \'a') + '. The comment is "' + tfh.SObjFieldValue__c + '"';
                            break;
                        }
                    }
                }
            }
        }
        Return overdueWrapper;
    }
    
    @AuraEnabled
    Public Static List<DueWrapper> getNeardueTasks(){
        List<String> lstValidCaseStatus = New List<String>{GLOBAL_CONSTANT_HUM.TASKLISTVIEW_INPROGRESS_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDING_HUM};
            List<String> lstValidTaskStatus = New List<String>{GLOBAL_CONSTANT_HUM.TASKLISTVIEW_INPROGRESS_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGTASK_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGRESPONSE_HUM};
                Integer dataLimit = GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_SIX_HUM; // Data Limit , currently set to six
        Integer queryLimit = 3 * dataLimit;
        
        List<DueWrapper> neardueWrapper = New List<DueWrapper>();
        
        List<Task> neardueTasks = [Select Id,task_number__c,ActivityDate , createdDate, Description  from Task where OwnerId=
                                   :userinfo.getuserid() And ActivityDate > :System.Today() And  ActivityDate <= :System.Today() + GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_THREE_HUM And Status In :lstValidTaskStatus order by ActivityDate ASC , Createddate ASC  limit : queryLimit];
        List<Id> taskIds = new List<Id>();
        for(Task tsk: neardueTasks){
            taskIds.add(tsk.Id);
        }
        List<Task_Field_History__c> listOfTaskFieldH =  [SELECT ID,CreatedDate,SObjFieldName__c,SObjFieldValue__c,SObjID__c 
                                                         FROM Task_Field_History__c WHERE  SObjFieldName__c = 'Description' 
                                                         And SObjID__c IN :taskIds ORDER BY LastModifiedDate DESC ];
        
        
        List<Case> neardueCases = [Select Id ,Follow_Up_Due_Date__c , CaseNUmber , createdDate from Case where ownerid=
                                   :userinfo.getuserid() And Follow_Up_Due_Date__c > :System.Today() And Follow_Up_Due_Date__c <= :System.Today() + GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_THREE_HUM  And Status In :lstValidCaseStatus order by Follow_Up_Due_Date__c ASC , Createddate ASC  limit : queryLimit];
        
        List < CaseComment > neardueCasesComments = [SELECT Id, CommentBody, ParentId, FORMAT(CreatedDate) FROM CaseComment where Parent.ownerid=:userinfo.getuserid() ORDER BY CreatedDate DESC];    
        
        For (Task neardue: neardueTasks){
            neardueWrapper.add(New DueWrapper(neardue.Id, neardue.task_number__c, neardue.ActivityDate));
        }
        For (Case neardue: neardueCases){
            neardueWrapper.add(New DueWrapper(neardue.Id, neardue.CaseNUmber, neardue.Follow_Up_Due_Date__c));
        }
        iNearDueCount=neardueWrapper.size();
        
        For (DueWrapper ndWrapper: neardueWrapper) {
            ndWrapper.comment = 'No Comment found';
            For (CaseComment neardueCc: neardueCasesComments) {
                If (ndWrapper.Id == neardueCc.ParentId) {
                    ndWrapper.comment = 'Last  Case comment created on ' + neardueCc.CreatedDate.format('MM/dd/YYYY\' \'hh:mm\' \'a') + '. The comment is "' + neardueCc.CommentBody + '"';
                    break;           
                }
            }
            For (Task neardueTsk: neardueTasks) {
                for (Task_Field_History__c tfh: listOfTaskFieldH) {
                    If (ndWrapper.Id == tfh.SObjID__c) {
                        If (tfh.SObjFieldValue__c != null){
                            ndWrapper.comment = 'Last Task comment created on ' + tfh.CreatedDate.format('MM/dd/YYYY\' \'hh:mm\' \'a') + '. The comment is "' + tfh.SObjFieldValue__c + '"';
                            break;
                        }
                    }
                }
            }
        }
        Return neardueWrapper;
    }
    
    Public Class DueWrapper {
        @AuraEnabled
        Public String Id; 
        @AuraEnabled
        Public String field1 {Get; Set;} 
        @AuraEnabled
        Public DateTime field2 {Get; Set;}
        @AuraEnabled
        Public String comment { Get; Set; }
        
        Public DueWrapper(String Id, String field1, DateTime field2) {
            this.Id = Id;
            this.field1 = field1;
            this.field2 = field2;
            this.comment = comment;
        }
    }
}