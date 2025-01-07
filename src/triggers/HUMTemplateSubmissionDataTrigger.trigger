/*******************************************************************************************************************************
Triger Name    : HUMTemplateSubmissionDataTrigger
Version         : 1.0
Created On      : 03/25/2019
Function        : Trigger for Template_Submission_data__c  object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor                               03/25/2019                 original version
*******************************************************************************************************************************/

trigger HUMTemplateSubmissionDataTrigger on Template_Submission_Data__c (after insert) 
{
	try
	{
		HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('TemplateSubmissionData');		
        if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c == true )
        {
        	if(trigger.isAfter)
            {            	
                TemplateSubmissionDataHandler.evaluateOmniEligibility(trigger.new);
            }      
        }
	}
	catch(exception ex)
	{
		HUMExceptionHelper.logErrors(ex, 'HUMTemplateSubmissionDataTrigger', 'HUMTemplateSubmissionDataTrigger');
	}	
}