/*******************************************************************************************************************************
Apex Class Name : WorkQueueSetup_H_HUM
Version         : 1.0
Created On      : 12/16/2014
Function        : Used for trigger functionality.Updates ownerid field with queue name. shows error messages if user enters wrong queue name.
Test Class      : WorkQueueSetup_T_HUM

Modification Log: 
* Developer Name                  Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* SuryaKumari Medicherla           19954                    12/16/2014                  Original Version
* SuryaKumari Medicherla           25440                    04/26/2014                  added checkBeforeDisable Method to show error message if work queue setup record has any open cases/tasks associated to it
* Atia Uzma                                                 06/21/2021                  User Story 2348383: T1PRJ0001827 - MF 1 - Case Transfer Assist - Business Configuration enhancements (CRM)
******************************************************************************************************************************/
public with sharing class WorkQueueSetup_H_HUM
{ 
    private Map<String,String> mapGroupQueue = new Map<String, String>();
   
    /**
    * Method is used to show error message if user enters wrong queue name, and updates owner with queue name.
    *
    * @param List<Work_Queue_Setup__c>      lstWQSRecords
    * @param Map<Id, Work_Queue_Setup__c>   oldWQSMap   
    * @return                               List<Work_Queue_Setup__c>
    */   
    public List<Work_Queue_Setup__c> checkBeforeDisable(List<Work_Queue_Setup__c> lstWQSRecords, Map<Id, Work_Queue_Setup__c> oldWQSMap)
    {
        String sStausCancelled = GLOBAL_CONSTANT_HUM.HUM_CLOSED ;
        String sStausClosed = GLOBAL_CONSTANT_HUM.HUM_CANCELLED ;
        try
        {
            List<String> lstDisableIds =  new List<String>();
            Set<String> setWorkQueueName = new Set<String>();
            List<Work_Queue_Setup__c> lstWQS = new List<Work_Queue_Setup__c>();
            List<Work_Queue_Setup__c> lstUpdatedWQS = new List<Work_Queue_Setup__c>();
            
            for(Work_Queue_Setup__c oNewWQS : lstWQSRecords)
            {
                Work_Queue_Setup__c oOldWQS = oldWQSMap.get(oNewWQS.Id);
                if(oNewWQS.IsActive__c == false && oOldWQS.IsActive__c == true)
                {
                    setWorkQueueName.add(oNewWQS.Name);
                    lstWQS.add(oNewWQS);
                }
            }
			List<Case> lstOpenCase = [Select id, Owner_Queue__c FROM Case Where (Created_By_Queue__c = :setWorkQueueName OR LastModifiedby_Queue__c = :setWorkQueueName OR Owner_Queue__c = :setWorkQueueName) AND isClosed = false limit 10000];
			
			List<Task> lstOpenTask = [Select id, Owner_Queue__c FROM Task Where (Created_By_Queue__c = :setWorkQueueName OR LastModifiedby_Queue__c = :setWorkQueueName OR Owner_Queue__c = :setWorkQueueName) AND isClosed = false limit 10000];
			
			
            //moved the entire code present here to a new method activateDeactivateWorkQueue
            lstUpdatedWQS = activateDeactivateWorkQueue(lstOpenCase,lstOpenTask,lstWQS, setWorkQueueName);
            if(lstUpdatedWQS != NULL && lstUpdatedWQS.size() > 0) return lstUpdatedWQS;
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'WorkQueueSetup_H_HUM', 'checkEnteredQueue');
        }
        return null;
    }
    
    /**
    * Method is used to activate / deactivate the Work Queue record.
    *
    * @param Map<Id,AggregateResult>        mapOpenCases
    * @param Map<Id,AggregateResult>        mapTaskCounts
    * @param List<Work_Queue_Setup__c>      lstWQS
    * @return                               List<Work_Queue_Setup__c>
    */
    public List<Work_Queue_Setup__c> activateDeactivateWorkQueue(List<Case> lstOpenCase, List<Task> lstOpenTask ,List<Work_Queue_Setup__c> lstWQS, set<String> setWorkQueueName)
    {
        Boolean bDeactivated = true; 
        List<Work_Queue_Setup__c> lstUpdatedWQS = new List<Work_Queue_Setup__c>();
        String strNewId;
        set<String> errorWQS = new set<String>();

		for(case cas : lstOpenCase)
		{
   			if(setWorkQueueName.contains(cas.Owner_Queue__c)) 
   			{
   				errorWQS.add(cas.Owner_Queue__c);
   			}
   		}
   			
   		for(Task tas : lstOpenTask)
		{
   			if(setWorkQueueName.contains(tas.Owner_Queue__c)) 
   			{
   				errorWQS.add(tas.Owner_Queue__c);
   			}
   		}
   		
		for(Work_Queue_Setup__c oWQSNew : lstWQS)
    	{
	        bDeactivated = true;
	        
			if(errorWQS.contains(oWQSNew.Name))
			{
				oWQSNew.adderror(System.Label.HUMWorkQueueSetupErrorMessgae);
	            bDeactivated = false;	
			}
    		
    		if(bDeactivated)    lstUpdatedWQS.add(oWQSNew);
    	}
        return lstUpdatedWQS;
    }
    
    /*
     * Method Name   :    removeQueueValue
     * Description   :    This method is used to show error message if user enters wrong queue name, and updates owner with queue name.
     * Return Type   :    void
     * Parameters    :    List<Work_Queue_Setup__c>
     */     
    public void removeQueueValue(List<Work_Queue_Setup__c> lstWQSUpdatedRecords)
    {
        try
        {
            Map<Id,Work_Queue_Setup__c> mapDeactivatedRecords = new Map<Id,Work_Queue_Setup__c>();
            for(Work_Queue_Setup__c oWQS : lstWQSUpdatedRecords)
            {
                mapDeactivatedRecords.put(oWQS.id,oWQS);
            }
            List<User> lstUsers = [Select id, Current_Queue__c from User where Current_Queue__c In : mapDeactivatedRecords.keySet()];
            for(User oUser : lstUsers)
            {
                oUser.Current_Queue__c = '';
            }
            update lstUsers;
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'WorkQueueSetup_H_HUM', 'removeQueueValue');
        }
    }
    
    /*
     * Method Name   :    inactivateWQSRecords
     * Description   :    Checks for inactivate Work_Queue_Setup__c records and makes the associted Case_Transfer_Assist__c inactive 
     * Return Type   :    void
     * Parameters    :    List<Work_Queue_Setup__c> , Map<Id,Work_Queue_Setup__c>
     */   
    public void inactivateWQSRecords(List<Work_Queue_Setup__c> TriggerNew, Map<Id,Work_Queue_Setup__c> oldMap)
    {
        Map<id,Work_Queue_Setup__c> WQSMap = new Map<id,Work_Queue_Setup__c>();
        for(Work_Queue_Setup__c oWQS : TriggerNew){
            Work_Queue_Setup__c oldRecord = oldMap.get(oWQS.id);
            if(oldRecord.IsActive__c && oldRecord.IsActive__c!=oWQS.IsActive__c){
                WQSMap.put(oWQS.id,oWQS);
            }
        }
        
        if(!WQSMap.isEmpty()){
            List<Case_Transfer_Assist__c> lstUpdate = new List<Case_Transfer_Assist__c>();
            for(Case_Transfer_Assist__c cta : [SELECT id,Is_Active__c FROM Case_Transfer_Assist__c WHERE Work_Queue_Setup__c IN :WQSMap.keyset() AND Is_Active__c =: True]){
                cta.Is_Active__c = false;
                lstUpdate.add(cta);
            }
            if(!lstUpdate.isEmpty()){
                update lstUpdate;   
            }
        }
    }
}