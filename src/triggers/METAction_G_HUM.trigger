/*******************************************************************************************************************************
Trigger Name : METAction_G_HUM 
Version         : 1.0
Created On      : 11/10/2016
Function        : Trigger execution sequence logic for MET_Action__c object
                  
Modification Log: 
* Developer Name              Code Review              Date               Description
*------------------------------------------------------------------------------------------------------------------------------
* Prasanthi Kandula              48595               11/10/2016         Original Version
*******************************************************************************************************************************/

trigger METAction_G_HUM on MET_Action__c (before insert,before update)
{
    try
    {
        HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('MET_Action__c');
        if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c )
        {
            if(Trigger.isBefore) 
            {    
                if(Trigger.isInsert) 
                {
                    CheckDuplicatesOnAction_H_HUM.checkForDuplicateActionsOnInsert(Trigger.New);
                }
                if(Trigger.isUpdate)
                {
                    CheckDuplicatesOnAction_H_HUM.CheckForDuplicateActionsOnUpdate(Trigger.New,Trigger.oldMap);
                }
            }
        }
    }
    catch(Exception e)
    {
         HUMExceptionHelper.logErrors(e, 'METAction_G_HUM ', 'METAction_G_HUM');
    }
}