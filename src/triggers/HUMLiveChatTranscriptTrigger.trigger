/*******************************************************************************************************************************
Apex Trigger Name : HUMLiveChatTranscriptTrigger 
Version           : 1.0
Created On        : 09/09/2014
Function          : This serves as Trigger on the LiveChatTranscript object.
Test Classes      : HUMLiveChatTranscriptTriggerHelperTest                 
Modification Log: 
* Modification ID 			Developer Name           Code Review                 Date                       Description
*---------------------------------------------------------------------------------------------------------------------------
* 1.0						Ninad Patil              18240 		                   09/09/2014               Original Version
* 2.0					    Sagar Tapadia			 20293					       01/12/2015			    Updated code as per new changes related to encryption of transcript body.
* 3.0						Sagar Tapadia			 23562						   05/28/2015				CA5027355:Removed After trigger logic.
* 4.0						Rajesh Chandgothia	     152418						   11/29/2017			     REQ - 333809 Associate Interaction to New Case
* 5.0                       Vandana Chaudhari  		 220488	 	                   04/24/2018                REQ - 355781 CR788: Web Chat Auto Create Case (CMO CR5548)
* 6.0                       Vandana Chaudhari  		     	 	                   05/17/2018                REQ - 355781 CR788: Web Chat Auto Create Case (CMO CR5548)
* 7.0 			 			Joel George											   12/12/2019				Unsecure Chat				
* 8.0            			Joel George                                            06/26/2020          		Chat Transcript Service update  
* 9.0            			Luke P. Ceci                                           03/24/2021          		Sonar Qube Security Fixes 
* 10.0                      Sivaprakash Rajendran                                  08/08/2023               US4609720-Update URL value for the Async Chat Transcript.
*******************************************************************************************************************************/
trigger HUMLiveChatTranscriptTrigger on LiveChatTranscript(before insert,after insert, before update) 
{
    List<LiveChatTranscript> lstValidLiveChat = HUMLiveChatTranscriptTriggerHelper.validateLiveChats(Trigger.new);
    
    if(lstValidLiveChat.IsEmpty() ) {
        if(Trigger.isBefore && (Trigger.isInsert || Trigger.isUpdate)) {
            if(HUMUtilityHelper.isCRMFunctionalityON(HUMConstants.SWITCH_US4609720)){
                HUMLiveChatTranscriptTriggerHelper.updateURLForAsycnChatBeforeInsert(Trigger.new);
            }
        }
    }else{
        if(Trigger.isInsert && Trigger.isBefore) HUMLiveChatTranscriptTriggerHelper.onBeforeInsert(lstValidLiveChat);
        if(Trigger.isInsert && Trigger.isAfter) HUMLiveChatTranscriptTriggerHelper.onAfterInsert(lstValidLiveChat);
        if(Trigger.isUpdate && Trigger.isBefore) HUMLiveChatTranscriptTriggerHelper.onBeforeUpdate(lstValidLiveChat, Trigger.OldMap);
    }

}