/*******************************************************************************************************************************
Apex Class Name : HUMUpdateTaskHelper
Version         : 1.0
Created On      : 6/17/2014
Function        : Used for trigger functionality. Populates the queueview name whenever a task is inserted or updated based on queueview and filters.
Test Class      : HUMUpdateTaskHelperTest  

Modification Log
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Mrudula Jaddu                17093                    6/17/2014                  Original Version
* Mrudula Jaddu                17267                    8/12/2014                  modified dueDate code   
* Mrudula Jaddu                17878                    9/12/2014                  Modified fields as part of defect 154260   
* Mrudula Jaddu                17996                    9/18/2014                  Modified Queue assignment to other view as part of defect 154980 
* Manish Kumar                 18365                    10/07/2014                 Code fix according to defefct#157139
* Mrudula Jaddu                18386                    10/08/2014                 Code fix according to defefct#157139
* Mrudula Jaddu                18792                    10/27/2014                 Code fix according to defefct#151415
* Mrudula Jaddu                18887                    10/31/2014                 Modified taskQueues Method
* Mrudula Jaddu                20092                    12/26/2014                 Modified code for REQ-95896
* Santhi Mandava               20517                    01/23/2015                 Fixed defect #164139   
* Santhi Mandava               21968                    23/03/2015                 As per REQ-02635 removed future call out related logic and updated code to include 
newly created MD relationship fields and refactored existing code.
* Asha Deepika Koyyana         21968                    25/03/2015                 As per REQ-02104 Modified the method updateTaskQueues   .
* Manuel Johnson               29553                    01/28/2016                 REQ - 215092: Set Previous Owner Queue on Task creation
* Cody Sanders                 26409                    02/03/2016                 For REQ-194306, removed code for taskDueDateError() method. This validation has been moved to Task Validation Rule'If_Changed_Due_Date_Today_or_Future'.
* Prachi Bhasin                                         07/18/2016                 Changes made as part of REQ- 270349  
* Avinash Choubey              34551                    08/04/2016                 defect 235634 fix    
* Harshith Mandya                                       09/25/2016                 Modified codes for WQ Enhancement changes
* Vamsi Kondragunta            44606                    10/21/2016                 Changes for Req:72833
* Harshith Mandya                                       10/25/2016                 Modified codes for WQ Enhancement changes
* Avinash Choubey              46567                    11/07/2016                 defect 247444 fix
* Sharan Shanmugam             47343                    11/10/2016                 Modified code to fix the routing 
* Sharan Shanmugam                                      11/28/2016                 Defect Fix # 249892.0001 - Updating limit of query to fetch all the QVIs related to View
* Harshith Mandya              50014                    11/30/2016                 Implemented REQ - 290990 -  removed all lookup related fields from 
Case/Task/Interaction/Attachment to Work Queue Setup and Queue View
* Harshith Mandya              50905                    12/07/2016                 Defect Fix #252024.0001      
* Harshith Mandya                                       12/27/2016                 REQ - 296640 - Removing query from getmatchedViews and moving it to HUMUpdateCaseTaskHelper
* Gargey Sharma                77750                    03/21/2017                 REQ - 304949 : Update Task to save new CTCI Classification and Intent Values  
* Pallavi R                    98201                    05/31/2017                 REQ 318433 
* Anurag Chaturvedi 									08/09/2018				   REQ 367734 Add Task Type to CRM Service Platform Report types                                                             
* Shaliesh Mali								            02/11/2019				   Defect - 400200 Fixed
* Shailesh Mali											04/12/2019                 REQ - 392842, CA Ticket (8116599) fixed.
* Mohammed Noor                              			04/26/2021            	   US2163321 - Case Management Checkmarx Security violations - Case History & Case Misc
* Dinesh S                                              06/01/2021                 Defect-3173 Fix
* Mohammed Noor										    08/06/2021				   US2498249 - Add new Group Number field for QVI.
* Nikhil Malhotra								        09/15/2022				   REQ - 3605480 T1PRJ0342659 / SF / MF1 PCO - Create Last Touchpoint and  Appointment Date Fields on the Lead Record  
* Nikhil Malhotra 										11/11/2022				   DF - 6604 Fixing Log touchpoint Date logic
* Lakshmi Madduri										02/22/2024				   US-5604142 PCO App Deprecation
******************************************************************************************************************************/

public with sharing class HUMUpdateTaskHelper
{    
    private Map<Id, Case> mapCases = new Map<Id, Case>();
    private List<Task> lstTasksUpdate = new List<Task>();
    private boolean bNoView = true;
    private List<Queue_View__c> lstMatchedQueuesViews = new List<Queue_View__c>();
    private String sClosed = GLOBAL_CONSTANT_HUM.HUM_CLOSED;
    private String sWorkTask = GLOBAL_CONSTANT_HUM.CASEEDIT_WORK_TASK_HUM;
    public static Boolean avoidTriggerRecursion = true;
    public Set<String> setQueues = new Set<String>();
    
    /*
* This method is used to pop up an error on task record whenever duedate is passed.
* @ Param    :    List<Task>
* @ Return   :    none
*/     
    public void taskDueDateError(List<Task> lstTaskRecords)
    { 
        if(HUMDueDateHelper.bSkipFlag) return;
        
        for(Task oTaskRec : lstTaskRecords)
        {
            if(oTaskRec.ActivityDate < Date.Today()) oTaskRec.adderror(System.label.UPDATETASKHELPER_DUEDATEERROR_HUM);
        }    
    }
    
    /*
* This method used to populate the case fields data on task   
* @ Param    :    List<Task> 
* @ Param    :    List<Id>  List of Case Id
* @ Return   :    none
*/
    public void populateData(List<Task> lstTaskRecords,List<Id> lstCaseIds)
    {    
        List<Id> lstCaseId = new List<Id>();
        Integer iLimit = Limits.getLimitQueryRows(); 
        mapCases = new Map<Id, Case>([select Id, Type, Subtype__c, CTCI_List__r.Classification__r.Name,CTCI_List__r.Classification__c, CTCI_List__r.Intent__c,Funding_Type__c, Plan_Issue_State__c, Interacting_With_Type__c,State_Mem_Resid_Add__c,CTCI_List__r.Intent__r.Name,
                                      Plan_Segment_Size__c, Employer_Group_Name__c,Group_Number__c,Policy_Platform__c, Product_Type__c, Previous_Case_Owner_Role__c,Behavioral_Health__c,
                                      Provider_Shipping_State_Code__c,Group_Billing_State_Code__c,Policy_Product_Code__c,Complaint_Type__c,Contract_Protected_Case__c from case Where Id in :lstCaseIds Limit: iLimit ]);
        Try
        {            
            for(Task oTaskRec : lstTaskRecords)
            {
                Case oCaseRecord = mapCases.get(oTaskRec.WhatId);
                if(null == oCaseRecord) continue;
                
                oTaskRec.Funding_Type__c = oCaseRecord.Funding_Type__c;
                oTaskRec.Interacting_With_Type__c  = oCaseRecord.Interacting_With_Type__c;
                oTaskRec.Plan_Issue_State__c  = oCaseRecord.Plan_Issue_State__c;
                oTaskRec.Plan_Segment_Size__c  = oCaseRecord.Plan_Segment_Size__c;
                oTaskRec.Policy_Platform__c  = oCaseRecord.Policy_Platform__c;                
                oTaskRec.Product_Type__c  = oCaseRecord.Product_Type__c; 
                if( null != oCaseRecord.CTCI_List__r.Classification__c ) 
                {                  
                    oTaskRec.Classification__c  = oCaseRecord.CTCI_List__r.Classification__r.Name;
                }
                if( null != oCaseRecord.CTCI_List__r.Intent__c ) 
                {                  
                    oTaskRec.Intent__c  = oCaseRecord.CTCI_List__r.Intent__r.name;
                }    
                oTaskRec.Type__c  = oCaseRecord.Type;
                oTaskRec.Subtype__c  = oCaseRecord.Subtype__c;
                oTaskRec.Employer_Group_Name__c  = oCaseRecord.Employer_Group_Name__c;
                oTaskRec.Group_Number__c  = oCaseRecord.Group_Number__c;                      				                      
                oTaskRec.State_Mem_Resid_Add__c  = oCaseRecord.State_Mem_Resid_Add__c;
                oTaskRec.Behavioral_Health__c = oCaseRecord.Behavioral_Health__c;
                oTaskRec.Complaint_Type__c = oCaseRecord.Complaint_Type__c;
                oTaskRec.Provider_Shipping_State_Code__c = oCaseRecord.Provider_Shipping_State_Code__c;
                oTaskRec.Group_Billing_State_Code__c = oCaseRecord.Group_Billing_State_Code__c;
                oTaskRec.Policy_Product_Code__c = oCaseRecord.Policy_Product_Code__c;
                oTaskRec.Contract_Protected_Case__c = oCaseRecord.Contract_Protected_Case__c;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors( e,'HUMUpdateTaskHelper', 'populateData');
        }        
    }
    
    /*
* This method used for setting the setQueueNames
* @ Param    :    List<Task>
* @ Return   :    none
*/	
    public void setQueue(List<Task> lstTaskRecords){
        if(lstTaskRecords.size()>0){
            for(Task taskObj : lstTaskRecords){
                /* TaskOwner is different with QwnerQueue only when there is user value entered in the user field on task edit page.
* In all other scenario, TaskOwner and OwnerQueue always be the same. */
                setQueues.add(taskObj.Task_Owner__c); 
                setQueues.add(taskObj.Owner_Queue__c); 
            }
        }    
    }
    /*
* This is the main method used for getting the work queue view of case Record
* @ Param    :    List<Task>
* @ Param    :    Boolean to identift if the menthis is onvoked from Trigger
* @ Param    :    Set<String> Work Queue Names
* @ Return   :    none
*/        
    public void populateViewName(List<Task> lstTaskRecords,Boolean isTrigger,Set<String> setQueueNames)
    {   
        //to run condition only for trigger. Below logic not required for batch class, Batch handling the logic to set the QueueName.
        if(isTrigger){   
            //calling the setQueue() method, to set queueName
            setQueue(lstTaskRecords);
            if(!setQueues.isEmpty()){
                setQueueNames = new Set<String>();
                setQueueNames.addAll(setQueues);
            }   
        }
        //Sending the false value to execute IF condition of initializeListAndMaps() Method
        HUMUpdateCaseTaskHelper.initializeListAndMaps(false,setQueueNames);
        try
        { 
            for(Task oTaskRec : lstTaskRecords) 
            {     
                if(oTaskRec.Status != GLOBAL_CONSTANT_HUM.UPDATETASKHELPER_CLOSED_HUM && oTaskRec.Status != GLOBAL_CONSTANT_HUM.UPDATETASKHELPER_COMPLETED_HUM)
                {          
                    oTaskRec.Work_Queue_View_Name__c = GLOBAL_CONSTANT_HUM.UPDATETASKHELPER_ALLOTHERVIEW_HUM; 
                    lstMatchedQueuesViews = getMatchedViews(oTaskRec);
                    
                    if(lstMatchedQueuesViews.size() > 1)
                    {
                        Map<String, String> mapOfPriority = HUMUpdateCaseTaskHelper.mapOfQueuePriority.get(oTaskRec.Owner_Queue__c);
                        oTaskRec.Work_Queue_View_Name__c = HUMUpdateCaseTaskHelper.getMatcheViewBasedOnPriority(lstMatchedQueuesViews,mapOfPriority ,1 );
                    }
                    else if(lstMatchedQueuesViews.size() == 1)
                    {
                        oTaskRec.Work_Queue_View_Name__c = lstMatchedQueuesViews[0].Queue_View_Name__c;
                    }
                    else if(bNoView)
                    {
                        oTaskRec.Work_Queue_View_Name__c = '';
                    }
                    
                    bNoView = true;
                    lstMatchedQueuesViews.clear(); 
                }
            }
            if(!isTrigger)
            {
                Database.UpsertResult[] lstResults = Database.upsert(lstTaskRecords,false);
                HUMExceptionHelper.isBatchApex = true;
                HUMExceptionHelper.processUpsertResults(lstResults,lstTaskRecords,'WorkQueueViewNameBatch','','Task');
            }
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUpdateCaseHelper', 'populateViewName');
        }
    }
    
    /*
* This method is used to get the matched views based on case details.
* @ Param    :    Task
* @ Return   :    List<Queue_View__c> 
*/
    private List<Queue_View__c> getMatchedViews(Task oTaskRec)
    {
        List<Queue_View__c> lstMatchedViews = new List<Queue_View__c>();
        
        if(String.isNotBlank(oTaskRec.Owner_Queue__c) && String.isNotBlank(oTaskRec.Service_Center__c) && String.isNotBlank(oTaskRec.Department__c))
        {
            List<Queue_View__c> lstTemp = HUMUpdateCaseTaskHelper.mapViews.get(oTaskRec.Owner_Queue__c);
            If( lstTemp != Null)
            {
                for(Queue_View__c oQueueView : lstTemp )
                {
                    if (oTaskRec.Owner_Queue__c.equals(oQueueView.Work_Queue_Name__r.Name) && 
                        oTaskRec.Service_Center__c.equals(oQueueView.Work_Queue_Name__r.Service_Center__c) &&
                        oTaskRec.Department__c.equals(oQueueView.Work_Queue_Name__r.Department__c)) 
                    {
                        bNoView = false;
                        if(HUMUpdateCaseTaskHelper.mapViewItems.containsKey(oQueueView.Id) && !HUMUpdateCaseTaskHelper.mapViewItems.get(oQueueView.Id).isEmpty())
                        {
                            if(HUMUpdateCaseTaskHelper.checkFilterConditions(oQueueView ,oTaskRec,HUMUpdateCaseTaskHelper.mapViewItems.get(oQueueView.Id),null)) 
                            {
                                lstMatchedViews.Add(oQueueView);
                            }
                        }
                    }
                }
            }
            else
            {
                bNoView = false;
            }
        }
        return lstMatchedViews;
    }
    
    /*
* This method is used to update the owner queue and task owner fields of tasks when reopening a case
* @ Param    :    List<Task>
* @ Param    :    Map<Id, Task>
* @ Param    :    List<Id> List of Case Id
* @ Return   :    List<Queue_View__c> 
*/     
    public void updateTaskQueues(List<Task> lstTaskNewValues, Map<Id, Task> mapOldTasks,List<Id> lstCaseId)
    {
        try
        {
            String sWhatId = '';
            String sUserName = Userinfo.getName(); 
            Task objOldTask = new Task();
            List<Case> lstUpdateNew = new List<Case>();                    
            String sCurrentQueue = [select current_queue__c from User where id = :Userinfo.getUserId()].current_queue__c;            
            
            Map<Id, Case> mapCase = new Map<Id, Case>();
            if(lstCaseId != null && !lstCaseId.isEmpty()) mapCase = new Map<Id, Case>([select Id, Case_Owner__c,Owner_Queue__c  from Case Where Id in :lstCaseId]);
            
            for(Task objNewTask : lstTaskNewValues) 
            {
                objOldTask = mapOldTasks.get(objNewTask.Id);
                sWhatId = objNewTask.whatId;
                
                if(objOldTask.Task_Owner__c != NULL)
                {
                    if(!String.isEmpty(objOldTask.Task_Owner__c) && objOldTask.Task_Owner__c.equals(sUserName))
                    {
                        objNewTask.LastModifiedby_Queue__c = objOldTask.Owner_Queue__c;
                        objNewTask.Previous_Owner_Queue__c = objOldTask.Owner_Queue__c;
                    }
                    else if(!objOldTask.Task_Owner__c.equals(sUserName) && mapCase.get(sWhatId).Case_Owner__C.equals(sUserName))
                    {
                        objNewTask.LastModifiedby_Queue__c = mapCase.get(sWhatId).Owner_Queue__c;
                        objNewTask.Previous_Owner_Queue__c = mapCase.get(sWhatId).Owner_Queue__c;
                    }
                    else if(!String.isEmpty(objOldTask.Task_Owner__c) && !objOldTask.Task_Owner__c.equals(sUserName))
                    {
                        objNewTask.LastModifiedby_Queue__c = sCurrentQueue;
                        objNewTask.Previous_Owner_Queue__c = sCurrentQueue;
                    }
                }    
            }  
        }  
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUpdateTaskHelper', 'updateTaskQueues');
        }   
    }
    
    /*
* This method is used to update the last modified by queue and created by queue fields when creating a task
* @ Param    :    List<Task>
* @ Param    :    List<Id> List of Case Id
* @ Return   :    None
*/
    public void taskQueues(List<Task> lstTaskNewValues,List<Id> lstCaseId)
    {
        try
        {
            String sCurrentQueue = [select current_queue__c from User where id = :Userinfo.getUserId()].current_queue__c; 
            String sUserName = Userinfo.getName();
            String sWhatId = '';
            
            Map<Id, Case> mapCase = new Map<Id, Case>([select Id, Case_Owner__c, Owner_Queue__c from Case Where Id in :lstCaseId]);            
            
            for(Task objNewTask : lstTaskNewValues)
            {
                sWhatId = objNewTask.whatId;
                
                if(!String.isBlank(mapCase.get(sWhatId).Case_Owner__c))
                {                    
                    objNewTask.Created_By_Queue__c =  (mapCase.get(sWhatId).Case_Owner__c.equals(sUserName) ? mapCase.get(sWhatId).Owner_Queue__c : sCurrentQueue);
                    objNewTask.LastModifiedby_Queue__c = (mapCase.get(sWhatId).Case_Owner__c.equals(sUserName) ? objNewTask.Created_By_Queue__c : sCurrentQueue);
                    objNewTask.Previous_Owner_Queue__c = (mapCase.get(sWhatId).Case_Owner__c.equals(sUserName) ? mapCase.get(sWhatId).Owner_Queue__c : sCurrentQueue);
                }
            }
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUpdateTaskHelper', 'taskQueues');
        } 
    }    
    
    /*
* This method is used to create a list of tasks comprised of the Trigger's new values.
* @ Param     :    List<Task>
* @ Return    :    List<Task>
*/  
    public List<Task> populateListOfTasks(List<Task>lstTaskNewValues)
    {
        List<Task> lstReturnTasks = new List<Task>();
        for(Task oTask : lstTaskNewValues)
        {
            
            if(oTask.whatid != null && oTask.whatid.getSObjectType().getDescribe().getName() == GLOBAL_CONSTANT_HUM.UPDATETASKHELPER_OBJECTCASE_HUM)
            {
                lstReturnTasks.add(oTask);
            }
        }
        return lstReturnTasks;
    }
    
    /*
* This method is used to create a list of Case Ids for the Cases the Trigger's Task's relate to.
* @ Param     :    List<Task>
* @ Return    :    List<Id> List of Case Id
*/  
    public List<Id> populateListOfCaseIds(List<Task>lstTaskNewValues)
    {
        List<Id> lstReturnCaseIds = new List<Id>();
        for(Task oTask : lstTaskNewValues)
        {
            
            if(oTask.whatid != null && oTask.whatid.getSObjectType().getDescribe().getName() == GLOBAL_CONSTANT_HUM.UPDATETASKHELPER_OBJECTCASE_HUM)
            {
                lstReturnCaseIds.Add(oTask.WhatId);
            }
        }
        
        return lstReturnCaseIds;
    }
    
    /*
* This method is used to create a Set of Case Ids for the Cases the Trigger's Task's relate to.
* @ Param     :    List<Task>
* @ Return    :    Set<Id> Set of Owner Ids
*/  
    public Set<Id> populateSetOfOwnerIds(List<Task> lstTaskNewValues)
    {
        Set<Id> setReturnOwnerIds = new Set<Id>();
        
        for(task oTask : lstTaskNewValues)
        {
            setReturnOwnerIds.add(oTask.OwnerId);       
        }     
        
        return setReturnOwnerIds;
    }          
    
    /*
* This method is used to update the each Tasks Counts for Task
* @ Param    :    List<Task>
* @ Param    :    Map<Id, Task>
*/  
    
    Public void CountofOpenTotalTaskForParent(List<Task> lstTaskNewValues, Map<Id, Task> mapOldTasks)
    {
        
        Set<Id> setTaskIds = new Set<Id>();
        List<HUM_Task_Heirarchy__c> lstTaskHierarchyIds = new List<HUM_Task_Heirarchy__c>();
        
        for(task tsk:lstTaskNewValues)
        {
            if(tsk.status != mapOldTasks.get(tsk.id).status && tsk.status == sClosed)
                setTaskIds.add(tsk.Id);
        }
        
        HUM_Task_Heirarchy__c tempTaskH;
        for(HUM_Task_Heirarchy__c tskH : [select id, HUM_Dependent_TaskID__c, HUM_Parent_TaskID__c from HUM_Task_Heirarchy__c where HUM_Dependent_TaskID__c in: setTaskIds])
        {
            tempTaskH = new HUM_Task_Heirarchy__c();
            if(Schema.SObjectType.HUM_Task_Heirarchy__c.fields.Id.isAccessible())	tempTaskH.Id = tskH.Id;
            lstTaskHierarchyIds.add(tempTaskH);
        }
        
        if(lstTaskHierarchyIds.size() > 0)
        {
            update lstTaskHierarchyIds;
        }
        
    }
    /*
* This method is used to update the Activity Type with the task type
* @ Param    :    List<Task>
*/ 
    public void updateActivityField(List<Task> lstTskNewValue)
    {
        List<Task> taskList = new  List<Task>();
        for (task tsk : lstTskNewValue)
        {
            tsk.Activity_Type__c = tsk.Type;
            taskList.add(tsk);
        }
    }
}