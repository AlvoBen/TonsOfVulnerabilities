/*******************************************************************************************************************************
Apex Trigger Name : HumanaChatTranscriptTrigger 
Version           : 1.0
Created On        : 06/01/2023
Function          : This serves as Trigger on the Humana_Chat_Transcript__c object.
Test Classes      : HumanaChatTranscriptTriggerHelperTest                 
Modification Log: 
* Modification ID           Developer Name           Code Review                 Date                       Description
*---------------------------------------------------------------------------------------------------------------------------
* 1.0                       Sivaprakash Rajendran                                06/01/2023               Original Version
*******************************************************************************************************************************/
trigger HumanaChatTranscriptTrigger on Humana_Chat_Transcript__c (before insert,after insert, before update) {
    //This logic is used to bypass the trigger logic whenits called from other Testclasses 
    if(HumanaChatTranscriptTriggerHelper.byPassTrigger){
        //Bypass trigger
        return;
    }

    if(Trigger.isInsert && Trigger.isBefore) HumanaChatTranscriptTriggerHelper.onBeforeInsert(Trigger.new);
    if(Trigger.isUpdate && Trigger.isBefore) HumanaChatTranscriptTriggerHelper.onBeforeUpdate(Trigger.new, Trigger.OldMap);

}