/*******************************************************************************************************************************
Apex Class Name : HUMCaseInteractionTrigger 
Version         : 1.0
Created On      : 06/27/2014
Function        : This class serves as Trigger on the Case Interaction Object.
Test Classes	: HUMCaseInteractionTriggerHelperTest.cls                  
Modification Log: 
* Modification Id 		Developer Name         Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0					Ninad Patil            16484                            06/27/2014                 Original Version
* 1.1					Ninad Patil			   17100`                           08/05/2014                 Resubmitting After coding standard correction.
* 1.2					Harshith Mandya											18/11/2016				   Defect 248994.0001  - Populate lookup fields on Interaction object 
*******************************************************************************************************************************/
trigger HUMCaseInteractionTrigger on Case_Interaction__c (before insert,after insert) 
{
	if(Trigger.isBefore)
    {
    	if(trigger.isInsert)
    	{
    		HUMCaseInteractionTriggerHelper.onBeforeInsert(trigger.new,trigger.isBefore,trigger.isInsert);
    	}
    }
	else if(trigger.isAfter)
	{
        if(trigger.isInsert)
        {
        	HUMCaseInteractionTriggerHelper.updateCreatedByQueue(trigger.new);
        }
    }
}