/***************************************************************************************************************************
Apex Class Name : HUMUpdateTaskHelperForCloseCase
Version         : 1.0
Created On      : 03/19/2015 
Function        : Used for trigger functionality. Populates the "Open_Work_Task_Count" of case when ever a open task created or updated for that case.
Test Class      : HUMUpdateTaskHelperTest  

Modification Log: 
* Developer Name              Code Review                Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------
* SuryaKumari Medicherla      22085                   03/19/2015                  Original Version
* SuryaKumari Medicherla      23361                   05/11/2015                  Added code to check whether lstCaseId is not null or before querying, and checking whether string is not blank or not??
* Vamsi Kondragunta           44606                   10/24/2016                  Changes for Req:72833
* Vishal Verma                46667                   11/07/2016                  REQ - 289372 : Data Model Updates for Case and Task
* Vamsi Kondragunta           48130                   11/15/2016                  Defect Fix #248179
* Prudhvi Pamarthi            241409                  06/11/2018                  Fix for CA ticket -7052179 - Case History Is Not working correctly on CRM
* Pallavi Ravishankar         268909                  07/31/2018                  Added Logic for Udating Task count for task type = extension request 
* Pooja Kumbhar										  04/21/2021				  REQ - 2220675 - Case Management Checkmarx Security violations - Task Misc
******************************************************************************************************************************/
public with sharing class HUMUpdateTaskHelperForCloseCase
{    
    @testVisible private static Boolean  bIsAlreadyInserted = false;
    @testVisible private static Boolean  bIsAlreadyUpdated = false;
    private String sClosed = HUMConstants__c.getInstance('HUMClosed').StringValue__c;
    private String sWorkTask = HUMConstants__c.getInstance('HUMWorkTask').StringValue__c;
    private String sExtReqTask  = HUMConstants.HUMExtensionTask;
     /*
     * Method Name   :    CountofOpenTask
     * Description   :    This method is used to increment or decrement Open_Work_Task_Count when ever task is updated
     * Return Type   :    void
     * Parameters    :    List<Task>, Map<Id, Task>, List<Id>
     */     
    public void CountofOpenTask(List<Task> lstTaskNewValues, Map<Id, Task> mapOldTasks, List<Id> lstCaseId)
    {
        List<Case> lstUpdateNew = new List<Case>(); 
        Case oCase = NULL;
        Boolean bUpdate = false;
        String sWhatId = '';

        if (!bIsAlreadyUpdated) 
        {
            bIsAlreadyUpdated = true;
        }
        else
        {
            return;
        }
        try
        {
            if(lstCaseId != NULL || !lstCaseId.isEmpty())
            {
                Map<Id, Case> mapCase = new Map<Id, Case>([select Id, (Select Id From Tasks Where Type = :sWorkTask and Status != :sClosed), Open_Work_Task_Count__c from Case Where Id IN : lstCaseId]);
                Map<Id, Case> mapTotalCase = new Map<Id, Case>([select Id, (Select Id From Tasks Where Type = :sWorkTask), Open_Work_Task_Count__c from Case Where Id IN : lstCaseId]);
                Map<Id, Case> mapExtCase = new Map<Id, Case>([select Id, (Select Id From Tasks Where Type = :sExtReqTask and Status != :sClosed), Open_ExtensionTask_Count__c, Open_Work_Task_Count__c from Case Where Id IN : lstCaseId]);
                Map<Id, Case> mapTotalExtCase = new Map<Id, Case>([select Id, (Select Id From Tasks Where Type = :sExtReqTask), Open_ExtensionTask_Count__c, Open_Work_Task_Count__c from Case Where Id IN : lstCaseId]);
                for(Task objNewTask : lstTaskNewValues) 
                {
                    Task objOldTask = mapOldTasks.get(objNewTask.Id);
                    sWhatId = objNewTask.WhatId; 
                    if((!String.isBlank(objNewTask.Status) && (!objNewTask.Status.equals(objOldTask.Status))) || 
                    ((!String.isBlank(objNewTask.Type) && !objNewTask.Type.equals(objOldTask.Type)))) 
                    {
                        if(objOldTask.Type.equals(sWorkTask) || objNewTask.Type.equals(sWorkTask) || objOldTask.Type.equals(sExtReqTask) ||objNewTask.Type.equals(sExtReqTask))
                        {
                            bUpdate =  true;    
                            oCase =  mapCase.get(sWhatId);  
                            if(Schema.SObjectType.Case.fields.Open_Work_Task_Count__c.isUpdateable()) oCase.Open_Work_Task_Count__c = mapCase.containskey(sWhatId) ? returnMaxValue(mapCase.get(sWhatId).Tasks.Size()) : 0; 
                            if(Schema.SObjectType.Case.fields.Total_Work_Task_Count__c.isUpdateable()) oCase.Total_Work_Task_Count__c = mapTotalCase.containsKey(sWhatId) ? returnMaxValue(mapTotalCase.get(sWhatId).Tasks.Size()) : 0; 
                            if(Schema.SObjectType.Case.fields.Open_ExtensionTask_Count__c.isUpdateable()) oCase.Open_ExtensionTask_Count__c = mapExtCase.containskey(sWhatId) ? returnMaxValue(mapExtCase.get(sWhatId).Tasks.Size()) : 0; 
                            if(Schema.SObjectType.Case.fields.Total_ExtensionTask_Count__c.isUpdateable()) oCase.Total_ExtensionTask_Count__c = mapTotalExtCase.containsKey(sWhatId) ? returnMaxValue(mapTotalExtCase.get(sWhatId).Tasks.Size()) : 0;  
                            lstUpdateNew.add(oCase);
                        }
                    }
                }       
                if(bUpdate)    update lstUpdateNew;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMUpdateTaskHelperForCloseCase', 'CountofOpenTask');
        } 
    }

    /*
     * Method Name   :    CountofOpenTask
     * Description   :    This method is used to pop up an error on task record whenever duedate is passed.
     * Return Type   :    void
     * Parameters    :    List<Task>, List<Id>
     */     
    public void CountofOpenTaskForNew(List<Task> lstTaskNewValues, List<Id> lstCaseId)
    {
        if (!bIsAlreadyInserted) 
        {
            bIsAlreadyInserted = true;
        }
        else
        {
            return;
        }
        List<Case> lstUpdateNew = new List<Case>(); 
        Case oCase = NULL; 
        String sWhatId = '';

        try
        {
            if(lstCaseId != NULL || !lstCaseId.isEmpty())
            {
                Map<Id, Case> mapCase = new Map<Id, Case>([select Id, (Select Id From Tasks Where Type = :sWorkTask and Status != :sClosed), Open_Work_Task_Count__c from Case Where Id IN : lstCaseId]); 
                Map<Id, Case> mapTotalCase = new Map<Id, Case>([select Id, (Select Id From Tasks Where Type = :sWorkTask ), Open_Work_Task_Count__c from Case Where Id IN : lstCaseId]); 
                Map<Id, Case> mapExtCase = new Map<Id, Case>([select Id, (Select Id From Tasks Where Type = :sExtReqTask and Status != :sClosed), Open_ExtensionTask_Count__c,Open_Work_Task_Count__c from Case Where Id IN : lstCaseId]); 
                Map<Id, Case> mapTotalExtCase = new Map<Id, Case>([select Id, (Select Id From Tasks Where Type = :sExtReqTask ), Open_Work_Task_Count__c from Case Where Id IN : lstCaseId]); 
                for(Task objNewTask : lstTaskNewValues) 
                {
                    sWhatId = objNewTask.WhatId;
                    if(!String.isBlank(objNewTask.Status) && !String.isBlank(objNewTask.Type) && (objNewTask.Type.equals(sWorkTask)||objNewTask.Type.equals(sExtReqTask)))
                    {
                        oCase =  mapCase.get(sWhatId);
                        if(Schema.SObjectType.Case.fields.Open_Work_Task_Count__c.isUpdateable()) oCase.Open_Work_Task_Count__c = mapCase.containskey(sWhatId) ? returnMaxValue(mapCase.get(sWhatId).Tasks.Size()) : 0; 
                        if(Schema.SObjectType.Case.fields.Total_Work_Task_Count__c.isUpdateable()) oCase.Total_Work_Task_Count__c = mapTotalCase.containsKey(sWhatId) ? returnMaxValue(mapTotalCase.get(sWhatId).Tasks.Size()) : 0; 
                        if(Schema.SObjectType.Case.fields.Open_ExtensionTask_Count__c.isUpdateable()) oCase.Open_ExtensionTask_Count__c = mapExtCase.containskey(sWhatId) ? returnMaxValue(mapExtCase.get(sWhatId).Tasks.Size()) : 0; 
                        if(Schema.SObjectType.Case.fields.Total_ExtensionTask_Count__c.isUpdateable()) oCase.Total_ExtensionTask_Count__c = mapTotalExtCase.containsKey(sWhatId) ? returnMaxValue(mapTotalExtCase.get(sWhatId).Tasks.Size()) : 0; 
                        lstUpdateNew.add(oCase);
                    }
                }   
                update lstUpdateNew;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMUpdateTaskHelperForCloseCase', 'CountofOpenTaskForNew');
        } 
    }
     
    /*
     * This method used to get the Max Count for Open Work Task and Total WOrk Task   
     * @ Param    :    Integer  
     * @ Return   :    Integer
     */
    private Integer returnMaxValue(Integer iCount)
    {
        if(iCount> GLOBAL_CONSTANT_HUM.NUMBER_MAXWORKTASK_HUM)
        {
            return GLOBAL_CONSTANT_HUM.NUMBER_MAXWORKTASK_HUM;
        }
        return iCount;
    }  
}