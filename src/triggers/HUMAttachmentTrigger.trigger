/***************************************************************************************
Apex Trigger Name  : HUMAttachmentTrigger 
Version          : 1.0
Created Date     : May 24 2014
Function         : Trigger execution sequence logic for Standard Attachment object
 ****************************************************************************************
Modification Log:
 * Developer Name            Code Review                      Date                       Description
 *------------------------------------------------------------------------------------------------------------
 * Chaitanya Kumar V         17248                           05/24/2014                   Original Version
 * Vishal Verma              21654                           03/09/2014                   Added After Insert conditions for the trigger
 * Santhi Mandava                                            11/29/2018                   Added conditions for before update to avoid execution 
                                                                                          while migrating attachments from cipher to platform
  ***************************************************************************************************/


trigger HUMAttachmentTrigger on Attachment (before insert,before update,before delete , after insert)
{    
    String sIsRequired ='False';
    String sProfileName ='';
    List<Attachment_Trigger_Setting__mdt> lstSettings = [Select IsExecutionRequired__c,ProfileName__c FROM Attachment_Trigger_Setting__mdt Limit 1];
    if(lstSettings != Null && !lstSettings.isEmpty())
    {
        sProfileName = lstSettings[0].ProfileName__c;
        sIsRequired = lstSettings[0].IsExecutionRequired__c;
    }
    
    //Execute all before trigger events
    if(Trigger.isBefore) 
    {    
        //Execute Insert Events
        
        if(Trigger.isInsert) HUMAttachmentTriggerHelper.onBeforeInsert(Trigger.New);
        
        List<Profile> lstProfiles = [Select Name From Profile Where Id =: UserInfo.getProfileID()];
        String sProfName = '';
        If(lstProfiles != Null && !lstProfiles.isEmpty())
        {
            sProfName = lstProfiles[0].Name;
        }
        If(!sIsRequired.EqualsIgnoreCase('True') || !sProfileName.EqualsIgnoreCase(sProfName))
        {
            //Execute update Events
            if(Trigger.isUpdate) HUMAttachmentTriggerHelper.onBeforeUpdate(Trigger.New, Trigger.old, Trigger.newMap, Trigger.oldMap);
        }
        
        //Execute Delete Events
        if(Trigger.isDelete) HUMAttachmentTriggerHelper.onBeforeDelete(Trigger.old);        
    }
    //Execute all After trigger events
    if(Trigger.isAfter)
    {
        //Execute Insert Events
        if(Trigger.isInsert) HUMAttachmentTriggerHelper.onAfterInsert(Trigger.New);
    }
}