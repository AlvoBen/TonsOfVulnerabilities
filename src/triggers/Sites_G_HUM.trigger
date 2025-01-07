/*******************************************************************************************************************************
Apex Trigger Name : Sites_G_HUM
Version           : 1.0
Created On        : 08/11/2019
Function          : This is trigger for Sites object.
                
Modification Log: 
* Developer Name           Code Reveiw                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj                                    08/09/2019                 Original Version
*******************************************************************************************************************************/
trigger Sites_G_HUM on Softphone_Sites__c(before insert, before update, before delete) 
{
    try
    { 
        HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('Sites__c');
        if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c == true )
        {
            if(Trigger.isBefore && Trigger.isDelete)
            {
                SitesTriggerHandler_H_HUM.processBeforeDelete(Trigger.Old);
            }
            if(Trigger.isBefore && (Trigger.isUpdate || Trigger.isInsert))
            {
                SitesTriggerHandler_H_HUM.processBeforeUpdateAndInsert(Trigger.new);
            }
        }
    }
    catch(Exception ex)
    {
        HUMExceptionHelper.logErrors(ex, 'Sites_G_HUM', 'Sites_G_HUM');
    }
}