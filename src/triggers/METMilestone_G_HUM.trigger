/*******************************************************************************************************************************
Trigger Name    : METMilestone_G_HUM
Version         : 1.0
Created On      : 03/09/2018
Function        : Trigger execution sequence logic on MET_Milestone__c for duplicate record check.

Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Manoj Chaurasia        202594                   03/09/2018                   REQ - 354470: To avoid duplicate Milestone records entry
*******************************************************************************************************************************/

trigger METMilestone_G_HUM on MET_Milestone__c (before insert, before update) {
    try
    {
        
        HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('MET_Milestone__c');
        if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c)
        {
            if(Trigger.isBefore)
            {
                if(Trigger.isInsert)
                {
                    CheckDuplicatesMETMilestone_H_HUM.checkForDuplicateMETMilestoneOnInsert(Trigger.New);
                }
                if(Trigger.isUpdate)
                {
                    CheckDuplicatesMETMilestone_H_HUM.checkForDuplicateMETMilestoneOnUpdate(Trigger.New,Trigger.oldMap);
                }
            }       
        }
    }
    catch(Exception e)
    {
        HUMExceptionHelper.logErrors(e, 'METMilestone_G_HUM', 'METMilestone_G_HUM');
    }
}