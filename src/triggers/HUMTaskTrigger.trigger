/*******************************************************************************************************************************
Apex Trigger Name : HUMTaskTrigger
Version         : 1.0
Created On      : 06/17/2014
Function        : This  serves as Trigger on the Task object.

Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Mrudula Jaddu                                   06/17/2014                 original version
* SuryaKumari            17191                    07/02/2014                 Queue Assignment Functionality
* Manish Kumar           18901                    10/31/2014                 Email Notification stopped on task creation : D159675
* Santhi Mandava         20517                    01/23/2015                 Fixed defect #164139
* Kritika Popat          21275                    02/23/2015                 Modified line 66-70 as part of REQ-01699
* SuryaKumari Medicherla 21374                    03/09/2015                 Removed HUMUpdateCaseCommentHandler class instantiation.
* Santhi Mandava         21968                    23/03/2015                 As per REQ-02635, removed future call out logic and refactored existing code.
* SuryaKumari Medicherla 22085                    31/03/2015                 For REQ-72835,Calling HUMUpdateTaskHelperForCloseCase helper methods to modify "Open Work Task Count" value of Case.
* SuryaKumari Medicherla                          10/04/2015                 Code Modified to fix test class failures
* Cody Sanders           26409                    02/03/2016                 For REQ-194306, removed code for taskDueDateError() method call. This validation has been moved to Task Validation Rule 'If_Changed_Due_Date_Today_or_Future'.
* Vamsi Kondragunta      44606                    10/24/2016				 Changes for Req:72833.
* Praveen Kumar Parimi   104777                   06/22/2017                 As part of REQ - 321265- Implementation of Trigger Switch.
* Amol Patil									  05/16/2018				 As part of REQ - 358953 - Added logic for task insertion 
* Anurag Chaturvedi 							  08/09/2018				 As part of REQ - 367734 - Added logic for updating Activity Type with Task Type.
* Shaliesh Mali								      02/11/2019				 Defect - 400200 Fixed
* Shaliesh Mali								      04/01/2019				 REQ - 392842 -- to fix CA Ticket - 8116599
* Lakshmi Madduri									06/18/2019				 CDO Implementation
* Nikhil Malhotra                                 09/08/2021                 REQ - 2581966 Adding Record Type check on Task Trigger to byPass 'CDO_Task' records.
* Nikhil Malhotra								  09/15/2022				 REQ - 3605480 T1PRJ0342659 / SF / MF1 PCO - Create Last Touchpoint and  Appointment Date Fields on the Lead Record  
* Nikhil Malhotra 								  11/11/2022				 DF - 6604 Fixing Log touchpoint Date logic
* Lakshmi Madduri								  02/22/2024				 US-5604142 PCO App Deprecation
*******************************************************************************************************************************/

trigger HUMTaskTrigger on Task (before insert, before update,after insert,after update)
{
    try
    {
        Set<String> SetAllowedProfileNames = new Set<String>();
        Set<ID> SetAllowedProfileIds = new Set<ID>();
        
        for(HUMTaskNotAllowedProfiles__c objHumTaskTrgProf : HUMTaskNotAllowedProfiles__c.getAll().values())
        {
            SetAllowedProfileNames.add(objHumTaskTrgProf.name);
        }
        for(Profile objProfile : [select Name ,ID from Profile where Name in :SetAllowedProfileNames])
        {
            SetAllowedProfileIds.add(objProfile.ID);
        }
        HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('Task');
        if(NULL != SetAllowedProfileIds && SetAllowedProfileIds.size() > 0 && objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c)
        {
            if(!SetAllowedProfileIds.contains(UserInfo.GetProfileID()))
            {
                HUMQueueViewBatchSettings__c objSetup = HUMQueueViewBatchSettings__c.getValues('QueueViewBatchSetup');     
                if(objSetup != null &&  objSetup.TriggerExecutionRequired__c)
                {
                    Id taskNotAllowedRecordTypeId;
                    List<Task> newTaskList = new List<Task>(); // Please use (newTaskList) instead of (Trigger.new) (REQ - 2581966)
                    Map<Id, Task> oldTaskMap = new Map<Id, Task>(); // Please use (oldTaskMap) instead of (Trigger.oldMap) (REQ - 2581966)
                    Set<String> setNotAllowedRecordTypesName = new Set<String>();
                    Set<Id> setNotAllowedRecordTypesId =  new Set<Id>();
                    for(HUM_TASK_RecordType_Bypass_For_Trigger__mdt objhumTaskRecordType : HUM_TASK_RecordType_Bypass_For_Trigger__mdt.getAll().values())
                    {
                        setNotAllowedRecordTypesName.add(objhumTaskRecordType.DeveloperName);
                    }
                    if(setNotAllowedRecordTypesName != null && !setNotAllowedRecordTypesName.isEmpty())
                    {
                        for(String notAllowedRecordTypeName : setNotAllowedRecordTypesName)
                        {
                            taskNotAllowedRecordTypeId = SObjectType.Task.getRecordTypeInfosByDeveloperName().get(notAllowedRecordTypeName).getRecordTypeId();
                            setNotAllowedRecordTypesId.add(taskNotAllowedRecordTypeId);
                        }
                    }
                    for(Task objTask : Trigger.new)
                    {
                        if(!setNotAllowedRecordTypesId.Contains(objTask.RecordTypeId))
                        {
                            newTaskList.add(objTask);
                        }
                    }
                    if(Trigger.oldMap != null)
                    {
                        for(Task objTask : Trigger.oldMap.values())
                        {
                            if(!setNotAllowedRecordTypesId.Contains(objTask.RecordTypeId))
                            {
                                oldTaskMap.put(objTask.Id,objTask);
                            }
                        }
                    }
                    HUMUpdateTaskHelper oHandler = new HUMUpdateTaskHelper();        
                    List<Task> lstTasks = oHandler.populateListOfTasks(newTaskList);
                    List<Id> lstCaseIds = oHandler.populateListOfCaseIds(newTaskList);
                    Set<Id> setOwnerId = oHandler.populateSetOfOwnerIds(newTaskList);
                    
                    if(trigger.isBefore)
                    {
                        if(trigger.isUpdate)
                        {
                            oHandler.updateTaskQueues(newTaskList, oldTaskMap,lstCaseIds);
                            oHandler.updateActivityField(newTaskList);
                        }
                        else
                        {
                            oHandler.taskQueues(newTaskList,lstCaseIds);
                            oHandler.updateActivityField(newTaskList);
                            
                        }
                        if(!lstTasks.isEmpty())
                        {
                            oHandler.populateData(lstTasks,lstCaseIds);
                            oHandler.populateViewName(newTaskList,true,null);
                        }
                    }
                    else
                    {
                        HUMUpdateTaskHelperForCloseCase oCaseClose = new HUMUpdateTaskHelperForCloseCase();
                        if(trigger.isUpdate)
                        {
                            HUMTaskHistoryHelper oTaskHistoryHelper = new HUMTaskHistoryHelper();
                            oTaskHistoryHelper.insertTrackHistory(newTaskList, oldTaskMap); 
                            oCaseClose.CountofOpenTask(newTaskList, oldTaskMap,lstCaseIds);
                            oHandler.CountofOpenTotalTaskForParent(newTaskList, oldTaskMap);
                        }
                        else if(trigger.isInsert)
                        {
                            oCaseClose.CountofOpenTaskForNew(newTaskList,lstCaseIds);
                            HUMTaskHistoryHelper oTaskHistoryHelper = new HUMTaskHistoryHelper();
                            oTaskHistoryHelper.insertTrackHistoryForNewTask(newTaskList);
                        }
                    }    
                }
            } 
        }
    }
    catch(Exception ex)
    {
        HUMExceptionHelper.logErrors(ex, 'HUMTaskTrigger', 'HUMTaskTrigger');
    }
    
}