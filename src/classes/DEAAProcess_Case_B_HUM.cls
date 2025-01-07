/***************************************************************************************************************************************************************************
Apex Class Name  : DEAAProcess_Case_B_HUM
Version          : 1.0
Created Date     : Jan 30, 2020
Function         : This class is used to close the status and insert comments for expiration date greater than today's date.
Test Class       : DEAAProcess_Case_T_HUM
****************************************************************************************************************************************************************************
Modification Log:

 * Developer Name                Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Moshitha Gunasekaran                                     01/30/2020                  Original Version
 * Mohammed Noor                                            03/16/2020                  Fix CheckMarx code scan issues. 
**************************************************************************************************************************************************************/

global With sharing class DEAAProcess_Case_B_HUM implements Database.Batchable<sObject>,Database.AllowsCallouts {
	list<case> lstCase = new list<case>();
	global string sQuery;
	public DateTime startTime = System.now();
	public DateTime endTime = System.now().addHours(1);
	
	
	/*
       * Returns open cases that needs to be evaluated
       * <p>
       * Method to execute pre logic
       *
       * @param     BC                      Database.BatchableContext
       * @return    Database.QueryLocator            
    	*/     
    global Database.QueryLocator start(Database.BatchableContext BC)
    { 
    	string ERROR_APPLICATION = HUMConstants__c.getInstance('ERROR_APPLICATION') != null ? HUMConstants__c.getInstance('ERROR_APPLICATION').StringValue__c : '';
    	Database.QueryLocator dbQueryLocator = null;
    	try
	  	{
	  		Integer iJobs = [Select count() from AsyncApexJob where JobType ='BatchApex'
									AND Status = 'Holding'
									AND Id != : BC.getJobId()
									];
					
	        // Apex flex Queue can hold 100 batches on hold
	        if (iJobs > 100) dbQueryLocator = null;
	        else
	        {
		    	string originDEAA = GLOBAL_CONSTANT_HUM.DEAA_Origin;
		    	sQuery = 'Select Id, CaseNumber, AccountId, Type, Status,Subtype__c, Origin, Due_Date__c, Complaint__c, Case_Owner__c, Owner_Queue__c, G_A_Rights_Given__c FROM Case where IsClosed = false and Origin = :originDEAA and Due_Date__C < today';
		    	dbQueryLocator = Database.getQueryLocator(sQuery);
				 
	        }
	  	}
        catch(Exception e)
		{
            Error_Log__c oErrorLog = new Error_Log__c(Error_Message__c = e.getMessage(), class_name__c = 'DEAAProcess_Case_B_HUM', Method_Name__c = 'start', Object_Type__c = 'Case', Type__c = ERROR_APPLICATION);
            insert oErrorLog;
            system.abortjob(BC.getJobID());
            dbQueryLocator = null;
        }
        return dbQueryLocator;
	}
	
	/**
    * This method will be called when this class will be called using System.enqueueJob method
    * 
    * @param    context          QueueableContext instance
  * @return   na
    */
  global void execute(Database.BatchableContext BC, list<case> listCase) 
  { 
  	if(endTime > System.now())
  	{
  		if(listCase != null && !listCase.isEmpty())
  		{
  			DEAA_Process_H_HUM.UpdateCaseDEAABatch(listCase);
  		}
  	}
  	else
    {
       	system.abortjob(BC.getJobID());
    }
        
 }
  
   /**
  * Interface method to indicate the completion of batch
  * 
  * @param    BC          Database.BatchableContext
  * @return   void
  */
    global void finish(Database.BatchableContext BC)
    { }
}