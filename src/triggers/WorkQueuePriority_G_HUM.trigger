/***************************************************************************************
Apex Trigger Name  : HUMWorkQueuePriorityTrigger
Version          : 1.0
Created Date     : August 07 2016
Function         : Trigger execution sequence logic for validating the input and updating the work queue
 ****************************************************************************************
Modification Log:
 * Developer Name            Code Review                      Date                       Description
 *------------------------------------------------------------------------------------------------------------
 * Sharan Shanmugam                                      08/07/2016                   Original Version
 ***************************************************************************************************/

trigger WorkQueuePriority_G_HUM on Work_Queue_Priority__c (before insert, before update, after update) 
{
    if(Trigger.isBefore) 
    {    
        if(Trigger.isUpdate || Trigger.isInsert) 
        {
            WorkQueuePriority_H_HUM.validateInput(Trigger.New);
        }
    }
    
    if(Trigger.isAfter)
    {
        if(Trigger.isUpdate && WorkQueuePriority_H_HUM.bValidInput)
        {
            WorkQueuePriority_H_HUM.updateWorkQueueSetup(Trigger.New);        
        }   
    }
    
}