/***********************************************************************************************************************
Apex Trigger Name	: HUMContentVersionTrigger 
Version          	: 1.0
Created Date  	    : Oct/17/2022
Function            : Trigger execution sequence logic for Standard ContentVersion object
************************************************************************************************************************
Modification Log:
 * Developer Name            	Code Review   Date           Description
 *------------------------------------------------------------------------------------------------------------
 * Vijaya Lakshmi Tummala                 	  10/17/2022     Original Version-Case Attachment -Feature-3033033
 * 															 UserStory -3775271 Creating Case Attachments in Lightning
 * Gowthami Thota							  02/28/2023	 US4313616: T1PRJ0170850 - INC2169863 - Lightning- Case Mgt- Attachments
 															 FIX for Communities, CDO & Wellness Coaching
************************************************************************************************************************/
trigger HUMContentVersionTrigger on ContentVersion (after insert)
	{	
		String profileName = HumUtilityHelper.getCurrentUserProfileName();
        if(GLOBAL_CONSTANT_LH_HUM.lstAvailableProfiles.contains(profileName)){
				HUMContentVersionTriggerHelper.onAfterInsert(Trigger.New);
		}
		
	}