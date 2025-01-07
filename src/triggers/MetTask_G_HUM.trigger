/*******************************************************************************************************************************
Trigger Name    : MetTask_G_HUM 
Version         : 1.0
Created On      : 11/10/2016
Function        : Trigger execution sequence logic for MET_Task__c object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ranjeeth Nagishetty                            11/10/2016                 original version
*******************************************************************************************************************************/

trigger MetTask_G_HUM on MET_Task__c (before insert,before update)
{
     try
     {          
        HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getInstance('MET_Task__c');
        if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c )
        {
            if(Trigger.isBefore) 
            {    
                if(Trigger.isInsert)  
                {
                    CheckDuplicatesOnTask_H_HUM.checkForDuplicateTasks(Trigger.New);
                }
                if(Trigger.isUpdate)
                { 
                    CheckDuplicatesOnTask_H_HUM.checkForDuplicateTaskOnUpdate(Trigger.New,Trigger.oldMap);
                }
            }
        }
    }
    catch(Exception ex)
    {
        HUMExceptionHelper.logErrors(ex, 'MetTask_G_HUM', 'METask_G_HUM');
    }
  
}