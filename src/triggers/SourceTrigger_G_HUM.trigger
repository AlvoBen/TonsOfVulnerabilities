/**************************************************************
Apex Trigger: SourceTrigger_G_HUM
Description: Trigger on Source object
Created By: Prachi Bhasin
Created On:  11/16/2016

Modification Log :
-----------------------------------------------------------------------------
* Developer Name              Code Review              Date               Description
* ----------------------------------------------------------------------------                 
* Prachi Bhasin                 49268                 11/16/2016              Original version
* Anupama Tavva                 49268                 11/24/2016              REQ - 290277-Displays an error message if the source name already exists.
**************************************************************/

trigger SourceTrigger_G_HUM on MET_Source__c (before insert,before update)
{
 try
 {
    HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('MET_Source__c');
    if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c )
    {
      if(Trigger.isBefore) 
      {    
       if(Trigger.isInsert) 
       {
        CheckDuplicatesOnSource_H_HUM.checkForDuplicateSourcesOnInsert(Trigger.New);
       }
       if(Trigger.isUpdate)
       {
        CheckDuplicatesOnSource_H_HUM.checkForDuplicateSourcesOnUpdate(Trigger.New,Trigger.oldMap);
       }
      }
    }
 }
 catch(Exception e)
 {
   HUMExceptionHelper.logErrors(e, 'SourceTrigger_G_HUM', 'SourceTrigger_G_HUM');
 }
}