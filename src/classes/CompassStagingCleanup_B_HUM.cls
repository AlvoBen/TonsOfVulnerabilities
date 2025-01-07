/***************************************************************************************************************************************************************************
@Apex Class Name  : CompassStagingCleanup_B_HUM
@Version          : 1.0
@Created Date     : August 11, 2019
@Function         : Class to purge Quick Start Compass Staging records from SFDC.
@Test Class       : CompassStagingCleanup_T_HUM
****************************************************************************************************************************************************************************
Modification Log:
* Developer Name            Code Review             Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Rakesh Patni                                  08/11/2019                  Original Version
***************************************************************************************************************************************************************************/

global class CompassStagingCleanup_B_HUM implements schedulable, Database.Batchable<sObject>
{
    private String sQuery;
    /**
	* This method is to execute the Scheduler the batch process
	* 
	* @param    na
	* @return   na
	*/   
    public void execute(SchedulableContext sc)
    {
    	Database.executeBatch(new CompassStagingCleanup_B_HUM(),2000);
    }
    /**
	* Constructor for CompassStagingCleanup_B_HUM class
	* 
	* @param    na
	* @return   na
	*/
    global CompassStagingCleanup_B_HUM()
    {
        sQuery = 'SELECT id from Compass_Staging__c where createddate!=today';
    }
    /**
	* Interface method to query the Compass Staging records
	* 
	* @param    BC          	Database.BatchableContext
	* @return   Database.QueryLocator
	*/
    global Database.QueryLocator start(Database.BatchableContext BC)
    {
        return Database.getQueryLocator(sQuery);
    }
    /**
	* Interface method to processa and delete the Compass Staging batch records
	* 
	* @param    BC          	Database.BatchableContext
	* @param    alertRecords    alerts record batch
	* @return   void
	*/
     global void execute(Database.BatchableContext BC, List<sObject> stagingRecords)
    {
    	try
    	{
            if(Compass_Staging__c.sObjectType.getDescribe().isDeletable())
            {
                List<Database.DeleteResult> delMentors = Database.Delete(stagingRecords);
    			HUMExceptionHelper.processDeleteResults(delMentors ,stagingRecords ,'CompassStagingCleanup_B_HUM','execute','Compass_Staging__c');	
            }    		        		        	
    	}
    	catch(Exception e)
    	{
    		HUMExceptionHelper.logErrors(e, 'CompassStagingCleanup_B_HUM', 'execute');
    	}
    }  
     /**
	* Interface method to indicate the completion of batch
	* 
	* @param    BC          Database.BatchableContext
	* @return   void
	*/
    global void finish(Database.BatchableContext BC)
    {
    	
    }
}