/*******************************************************************************************************************************
Apex Class Name : HUMUpdateWorkQueueSetupTriggerHelper
Version         : 1.0
Created On      : 12/16/2014
Function        : Used for trigger functionality.Updates ownerid field with queue name. shows error messages if user enters wrong queue name.
Test Class      : HUMUpdateWorkQueueSetupTriggerHelperTest

Modification Log: 
* Developer Name                  Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* SuryaKumari Medicherla           19954                    12/16/2014                  Original Version

******************************************************************************************************************************/
public with sharing class HUMUpdateWorkQueueSetupTriggerHelper
{ 
    private Map<String,String> mapGroupQueue = new Map<String, String>();
    /*
     * Method Name   :    checkEnteredQueue
     * Description   :    This method is used to show error message if user enters wrong queue name, and updates owner with queue name.
     * Return Type   :    void
     * Parameters    :    List<Work_Queue_Setup__c>
     */     
    public void checkEnteredQueue(List<Work_Queue_Setup__c> lstWQSRecords)
    {
        try
        {
        Integer iLimit = Integer.Valueof(System.label.HUMWorkQueueSetupLimit);
        List<Group> lstQueues = [Select Name,id from Group where Type = 'Queue' limit :iLimit ];
        List<Work_Queue_Setup__c> lstNewWQSRecords = new List<Work_Queue_Setup__c>();
        for(Group oGrp : lstQueues)
        {
            mapGroupQueue.put(oGrp.Name, oGrp.Id);
        }
        String sWorkQueueName;
        for(Work_Queue_Setup__c oWQS : lstWQSRecords)
        {
            sWorkQueueName = oWQS.Name;
            if(mapGroupQueue.get(sWorkQueueName) == NULL)
            {
                oWQS.adderror(System.label.HumWorkQueueErrorMessage);
            }
            else
            {
                oWQS.OwnerId = mapGroupQueue.get(sWorkQueueName);
            }
        }
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'HUMUpdateWorkQueueSetupTriggerHelper', 'checkEnteredQueue');
        }
    }
}