/*******************************************************************************************************************************
Apex Trigger Name : SoftPhoneAgent_G_HUM 
Version           : 1.0
Created On        : 08/09/2019
Function          : This is trigger for Agent object.
                
Modification Log: 
* Developer Name           Code Reveiw                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Rakesh Patni                                       08/09/2019                 Original Version
*******************************************************************************************************************************/
trigger SoftPhoneAgent_G_HUM on Softphone_Agent__c(before insert, before update, after insert, after update, before delete, after delete)
{
    try
    { 
        HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('Agent__c');
        if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c == true )
        {
            if(Trigger.isUpdate && Trigger.isAfter)
            {    
                AgentTriggerHandler_H_HUM.updateNoOfAgentsSoftLink(Trigger.new, Trigger.OldMap);
            }
            if(Trigger.isInsert && Trigger.isAfter)
            {    
                AgentTriggerHandler_H_HUM.updateNoOfAgentsSoftLink(Trigger.new, null);
            }
            if(Trigger.isDelete && Trigger.isAfter)
            {    
                AgentTriggerHandler_H_HUM.updateNoOfAgentsSoftLink(Trigger.old, null);
            }
            
            if((trigger.isInsert || trigger.IsUpdate)  && Trigger.isBefore ) 
            {                          
                AgentTriggerHandler_H_HUM.processBeforeInsertAndUpdate(trigger.new);
            }
            
            if(trigger.isDelete  && Trigger.isBefore ) 
            {                          
                AgentTriggerHandler_H_HUM.processBeforeDelete(trigger.old);
            }
        }
    }
    catch(Exception ex)
    {
        HUMExceptionHelper.logErrors(ex, 'SoftPhoneAgent_G_HUM', 'SoftPhoneAgent_G_HUM');
    }
}