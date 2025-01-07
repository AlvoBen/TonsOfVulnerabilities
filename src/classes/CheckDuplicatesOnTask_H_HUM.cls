/*****************************************************************************************************
 Apex Class Name  : CheckDuplicatesOnTask_H_HUM
 Version   : 1.0 
 Created Date  : 11/10/2016
 Function  : Holds the logic for trigger events on Task Object
 Test Class  :   CheckDuplicatesOnTask_T_HUM
 
 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
*  Ranjeeth Nagishetty                           11/10/2016                    REQ-290092 :Holds the logic for trigger events on Task Object
                                                                               On Insert or Update, if the combination of Name and Case_Subtype__c are not unique, do not save the record.
*******************************************************************************************************/

public class CheckDuplicatesOnTask_H_HUM
{

    /**
    *  Checks for duplicate Task records with same task name and Case subtype
    *  @name checkForDuplicateTasks
    *  @param lstTaskRecords Holds the list of all Taks records
    *  @return void
    *  @throws NA
    */
    public static void checkForDuplicateTasks(List<MET_Task__c> lstTaskRecords)
    {
       Map<String,MET_Task__c> mapMetTask = new Map<String,MET_Task__c>();
       Map<String,MET_Task__c> mappicklist = new Map<String,MET_Task__c>();
       
       for(MET_Task__c oTsk : lstTaskRecords)
        {
            mapMetTask.put(oTsk.Name.toUpperCase(),oTsk );
            mappicklist.put(oTsk.Case_Subtype__c, oTsk );
        }
        
        fetchvalue(mapMetTask,mappicklist);
    } 
    
    /**
    *  Checks for duplicate task records with same Taks name and case subtype
    *  @name checkForDuplicateTaskOnUpdate
    *  @param lstTaskRecords Holds the list of all Task records
    *  @return void
    *  @throws NA
    */
    public static void checkForDuplicateTaskOnUpdate(List<MET_Task__c> lstNewTaskRecords,Map<Id,MET_Task__c> mapOldActionRecords)
    {
        Map<String,MET_Task__c> mapMetTask= new Map<String,MET_Task__c>();
        Map<String,MET_Task__c> mappicklist = new Map<String,MET_Task__c>();
        
        for(MET_Task__c oTask : lstNewTaskRecords)
        {
            if(String.isNotBlank(oTask.Name) && (oTask.Name.toUpperCase() != mapOldActionRecords.get(oTask.Id).Name.toUpperCase() || !oTask.Case_Subtype__c.equalsIgnoreCase(mapOldActionRecords.get(oTask.Id).Case_Subtype__c)) )
            {
              mapMetTask.put(oTask.Name.toUpperCase(),oTask );
              mappicklist.put(oTask.Case_Subtype__c, oTask );
            }
           
        }
        
        fetchvalue(mapMetTask,mappicklist);
    } 
       
    /**
    *  Checks for Fetch value of the records of task and if it matchs the task name and Casesubtype then throws error
    *  @name fetchvalue
    *  @param Map<String,MET_Task__c> ,Map<String,MET_Task__c> 
    *  @return void
    *  @throws NA
    */
    private static void fetchvalue( Map<String,MET_Task__c> mapMetTask, Map<String,MET_Task__c> mappicklist)
    {
        for(MET_Task__c oTasks : [SELECT id, Case_Subtype__c ,name from MET_Task__c where Name IN :mapMetTask.keyset() and Case_Subtype__c  in: mappicklist.keyset()]) 
        {     
           mapMetTask.get(oTasks.Name.toUpperCase()).addError(Label.UNIQUERECORD_TASK);
        }
    }
    
}