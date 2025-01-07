/******************************************************************************************************************************
Apex 
Class Name : HUMWorkQueueSetupTrigger
Version         : 1.0
Created On      : 12/15/2014
Function        : This serves as Trigger on the Work_Queue_Setup__c object.

Modification Log: 
* Developer Name                  Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* SuryaKumari Medicherla         19954                      12/16/2014                  Original Version
* SuryaKumari Medicherla         25440                      04/28/2015                  Code Modified for REQ-168974(Calling checkBeforeDisable method to check whether the work queue setup has any open cases/tasks)
* Seema Kaulgi                                				02/26/2019                  REQ - 386743 MIR - Create and update SLA values in Work Queue Setup record
* Atia Uzma                                                 06/21/2021                  User Story 2348383: T1PRJ0001827 - MF 1 - Case Transfer Assist - Business Configuration enhancements (CRM)
******************************************************************************************************************************/
trigger WorkQueueSetup_G_HUM on Work_Queue_Setup__c (before insert, before update, after update) 
{
    WorkQueueSetup_H_HUM oWQSHelper = new WorkQueueSetup_H_HUM();
    List<Work_Queue_Setup__c> lstDeactivated = new List<Work_Queue_Setup__c>();
    HUMTriggerSwitch__c oHTS = HUMTriggerSwitch__c.getValues('Work Queue Setup');
    if(oHTS != NULL && oHTS.Exeute_Trigger__c){
        if(trigger.isUpdate){
            lstDeactivated = oWQSHelper.checkBeforeDisable(Trigger.new, Trigger.oldMap);
            if(NULL != lstDeactivated && lstDeactivated.size() > 0)    oWQSHelper.removeQueueValue(lstDeactivated);
        }
        
        if((trigger.isUpdate || trigger.isInsert) && trigger.isBefore)
    	{
	        HUMUpdateWorkQueueSetupTriggerHelper oWQSHelper = new HUMUpdateWorkQueueSetupTriggerHelper();
	        oWQSHelper.checkEnteredQueue(Trigger.new);
    	}
    }
    if(trigger.isAfter && trigger.isUpdate){
        oWQSHelper.inactivateWQSRecords(Trigger.new, Trigger.oldMap);
    }
    
}