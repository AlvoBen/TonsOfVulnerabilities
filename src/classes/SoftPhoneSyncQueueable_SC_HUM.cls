/*******************************************************************************************************************************
Apex Class Name     : SoftPhoneSyncQueueable_SC_HUM 
Version             : 1.0
Created Date        : June 12 2020
Function            : scheduler class to schedule SoftPhoneSyncQueueable_C_HUM
Test Class 			: SoftPhoneSyncQueueable_T_HUM

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Akshay Pai                                    		06/12/2020                  Initial Version
*******************************************************************************************************************************/
public with sharing Class SoftPhoneSyncQueueable_SC_HUM Implements Schedulable
{
    /**
        *  @name execute
        *  @param SchedulableContext
        *  @return NA
        *  @throws NA
    */
    public void execute(SchedulableContext ctx)
    {
        List<Softphone_Agent__c> listAgent = new List<Softphone_Agent__c>();
        Set<Softphone_Agent__c> agentsToUpdateSet = new Set<Softphone_Agent__c>();
		Set<Softphone_Agent__c> agentsFailedToInsertSet = new Set<Softphone_Agent__c>();
        
        SoftPhoneSyncQueueable_S_HUM job = new SoftPhoneSyncQueueable_S_HUM(listAgent,agentsToUpdateSet,agentsFailedToInsertSet);
        System.enqueueJob(job);
    }
}