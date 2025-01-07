/*******************************************************************************************************************************
Apex Class Name : HUMArticleFeedbackTrigger 
Version         : 1.3
Created On      : 05/14/2014
Function        : This trigger is used post back on chatter feed received on articles with Feedback

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Amit Sinha                           05/14/2014              Created 
 * Shruthi Karanth                      05/16/2014              Moved the DML operation outside for loop
 * Shruthi Karanth                      06/11/2014              Moved the code to helper class
 ****************************************************************************************************************************/

trigger HUMArticleFeedbackTrigger on Article_Feedback__c (after update) 
{
	if(Trigger.isAfter && Trigger.isUpdate)
	{
		HUMArticleFeedbackHelper.afterInsertArticleFeedback(trigger.new, trigger.OldMap);
	}
}