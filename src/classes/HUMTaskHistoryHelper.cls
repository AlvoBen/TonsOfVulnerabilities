/*******************************************************************************************************************************
Apex Class Name : HUMTaskHistoryHelper
Version         : 1.0
Created On      : 02/23/2015
Function        : It is called in HUMTaskTrigger and is used to insert records to Task_Field_History__c object 
                  whenever Task is modified.
Test Class      : HUMTaskHistoryHelperTest  

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Kritika Popat               21275                     02/23/2015                  Original Version
* Amol Patil                                            05/16/2018					Req - 358953 added insertTrackHistoryForNewTask
* Amol Patil                                            08/03/2018					Req - 367846 Modified insertTrackHistoryForNewTask
* Pooja Kumbhar											04/08/2021			        User story 2163293: T1PRJ0002736 / PR00094232 - 2021 MF6 - Case Management Checkmarx Security violations - Task Edit 
*******************************************************************************************************************************/
 
public with sharing class HUMTaskHistoryHelper
{
    
    @TestVisible static Boolean  bIsAlreadyInserted = false;
    DateTime dtTimeOfInsertion = System.now();
    private Map<String, HUMObject_History_Metadata__c> mapFieldHistoryMetadata = new Map<String, HUMObject_History_Metadata__c>([Select name, Tracked_Field__c , Parent_Tracked_Object__c from HUMObject_History_Metadata__c]);
     
    /*
    * Method Name   :   insertTrackHistory
    * Description   :   Used For trigger HUMTaskHistoryTracker. Used to insert record in Task_Field_History__c whenever Task is modified.
    * Return Type   :   void
    * Parameters    :   List<Task>, Map<Id,Task>
    */ 
    public void insertTrackHistory(List<Task> lstNewValues, Map<Id,Task> mapOldMap)
    {   
        if(!bIsAlreadyInserted)
        {
            bIsAlreadyInserted = true;
        }
        else return;

        try
        {
            List<Task_Field_History__c> lstTaskFieldHistory = new List<Task_Field_History__c>();

            for (Task oTaskNew : lstNewValues)
            {
                Task oTaskOld = mapOldmap.get(oTaskNew.Id);
                
                for (String sHisObj : mapFieldHistoryMetadata.keySet()) 
                {  
                    if(mapFieldHistoryMetadata.get(sHisObj).Parent_Tracked_Object__c.equals(System.Label.HUMTrackedTaskObject))
                    {
                        String sFieldName = mapFieldHistoryMetadata.get(sHisObj).Tracked_Field__c;
                        
                        if (oTaskNew.get(sFieldName) != oTaskOld.get(sFieldName))
                        {
                            String sOldValue = String.valueOf(oTaskOld.get(sFieldName));
                            Task_Field_History__c oTaskFldHstry = new Task_Field_History__c();
                            if(Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isCreateable()) oTaskFldHstry.SObjID__c = oTaskNew.Id; 
                            if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isCreateable()) oTaskFldHstry.SObjFieldValue__c = sOldValue; 
                            if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isCreateable()) oTaskFldHstry.SObjFieldName__c = sFieldName; 
                            if(Schema.SObjectType.Task_Field_History__c.fields.Name.isCreateable()) oTaskFldHstry.Name = mapFieldHistoryMetadata.get(sHisObj).Parent_Tracked_Object__c; 
                            
                            List<Task_Field_History__c> lstForParentID = [select Id from Task_Field_History__c where SObjID__c =: oTaskNew.Id and SObjFieldName__c =: sFieldName order by Created_At__c DESC LIMIT 1 ]; 
                            
                            if(!(lstForParentID.isEmpty())) oTaskFldHstry.ParentFieldHistory_Id__c = lstForParentID[HUMConstants.ZERO].id;
                            lstTaskFieldHistory.add(oTaskFldHstry);
                        } 
                    }
                }
            }
            
            if (!lstTaskFieldHistory.isEmpty())
            {
                for(Task_Field_History__c oTFH : lstTaskFieldHistory)
                {
                    if(Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isCreateable()) oTFH.Created_At__c = dtTimeOfInsertion; 
                }
              
                database.insert(lstTaskFieldHistory);
              
               
            }
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMTaskHistoryHelper', 'insertTrackHistory');
        }
    }
	
	/*
    * Method Name   :   insertTrackHistoryForNewTask
    * Description   :   Used For trigger HUMTaskHistoryTracker. Used to insert record in Task_Field_History__c whenever New Task is inserted.
    * Return Type   :   void
    * Parameters    :   List<Task>
    */
    public void insertTrackHistoryForNewTask(List<Task> lstNewValues)
    { 
       list<Task_Field_History__c> lstTaskFieldHstry = new list<Task_Field_History__c>();
	try
        {
       if(!lstNewValues.isEmpty()){
       for(Task taskRec : lstNewValues){
           for (String sHisObj : mapFieldHistoryMetadata.keySet()) 
                {
                String sFieldName = mapFieldHistoryMetadata.get(sHisObj).Tracked_Field__c;
                Task_Field_History__c oTaskFldHstry =new Task_Field_History__c();
                if(Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isCreateable()) oTaskFldHstry.SObjID__c = taskRec.Id; 
                if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isCreateable()) oTaskFldHstry.SObjFieldValue__c = null; 
                if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isCreateable()) oTaskFldHstry.SObjFieldName__c = sFieldName; 
                if(Schema.SObjectType.Task_Field_History__c.fields.Name.isCreateable()) oTaskFldHstry.Name = 'Task';
                if(Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isCreateable()) oTaskFldHstry.Created_At__c = dtTimeOfInsertion; 
                lstTaskFieldHstry.add(oTaskFldHstry);
                }
        }
       }
      
       if(!lstTaskFieldHstry.isEmpty()){
               database.insert(lstTaskFieldHstry);
              
             }
         
        }
        catch (Exception e)
        {
            system.debug('exception>>: '+e.getMessage());
            HUMExceptionHelper.logErrors(e,'HUMTaskHistoryHelper', 'insertTrackHistoryForNewTask');
        }
    }
}