/*******************************************************************************************************************************
Apex Trigger Name : SoftphoneLinks_G_HUM
Version           : 1.0
Created On        : 08/11/2019
Function          : This is trigger for Sites object.
                
Modification Log: 
* Developer Name           Code Reveiw                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj                                    08/09/2019                 Original Version
*******************************************************************************************************************************/
trigger SoftphoneLinks_G_HUM on SoftphoneLinks__c (before insert, before update, before delete) 
{
    try
    { 
        HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('SoftphoneLinks__c');
        
        if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c == true )
        {
            if(Trigger.isBefore&& Trigger.isDelete)
            {
                SoftphoneLinksTriggerHandler_H_HUM.processBeforeDelete(Trigger.Old);
            }
            if(Trigger.isBefore&& Trigger.isInsert)
            {
                SoftphoneLinksTriggerHandler_H_HUM.processBeforeInsert(Trigger.new);
            }
            if(Trigger.isBefore && Trigger.isUpdate && TriggerAvoidRecursion_H_HUM.run)
            {
                SoftphoneLinksTriggerHandler_H_HUM.processBeforeUpdate(Trigger.new, Trigger.oldMap);                
            }           
        }
    }
    catch(Exception ex)
    {
        HUMExceptionHelper.logErrors(ex, 'SoftphoneLinks_G_HUM', 'SoftphoneLinks_G_HUM');
    }
}