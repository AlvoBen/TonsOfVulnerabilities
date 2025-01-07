/***********************************************************************************************************************
Apex Trigger Name	: HUMContentDocumentTrigger 
Version          	: 1.0
Created Date  	    : Nov/09/2022
Function            : Trigger execution sequence logic for Standard ContentDocument object
************************************************************************************************************************
Modification Log:
 * Developer Name            	Code Review   Date           Description
 *------------------------------------------------------------------------------------------------------------
 * Vijaya Lakshmi Tummala                 	  11/09/2022     Original Version-Case Attachment -Feature-3033033
 * 															 UserStory -3775271 Creating Case Attachments in Lightning
 * Gowthami Thota							  02/28/2023	User Story 4313616: T1PRJ0170850 - INC2169863 - Lightning- Case Mgt- Attachments
 															FIX for Communities, CDO & Wellness Coaching		
************************************************************************************************************************/
trigger HUMContentDocumentTrigger on ContentDocument (before insert,before update,before delete) 
	{
	    String sIsRequired ='False';
		String sProfileName ='';
		String profileName = HumUtilityHelper.getCurrentUserProfileName();
        if(GLOBAL_CONSTANT_LH_HUM.lstAvailableProfiles.contains(profileName)){ 
		List<Attachment_Trigger_Setting__mdt> lstSettings = [Select IsExecutionRequired__c,ProfileName__c FROM Attachment_Trigger_Setting__mdt Limit 1];
		if(lstSettings != Null && !lstSettings.isEmpty())
		{
			sProfileName = lstSettings[0].ProfileName__c;
			sIsRequired = lstSettings[0].IsExecutionRequired__c;
		}   
		if(Trigger.isBefore)
		{   			 
			List<Profile> lstProfiles = [Select Name From Profile Where Id =: UserInfo.getProfileID()];
			String sProfName = '';
			If(lstProfiles != Null && !lstProfiles.isEmpty())
			{
				sProfName = lstProfiles[0].Name;				
			}
			If(!sIsRequired.EqualsIgnoreCase('True') || !sProfileName.EqualsIgnoreCase(sProfName))
			{
				//Execute update Events
				if(Trigger.isUpdate)
				{
					HUMContentDocumentTriggerHelper.onBeforeUpdate(Trigger.old, Trigger.new, Trigger.newMap, Trigger.oldMap);
				}
			}
			if(Trigger.isDelete)
			{
				HUMContentDocumentTriggerHelper.onBeforeDelete(Trigger.old);
			}
		}
		}
  
}