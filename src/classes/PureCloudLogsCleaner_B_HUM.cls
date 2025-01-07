/*****************************************************************************************************************************************************************************
@Apex Class Name  : PureCloudLogsCleaner_B_HUM
@Version          : 1.0
@Created Date     : 03/01/2021
@Function         : Class to purge pureCloudCallLogs
@Test Class       : PureCloudLogsCleaner_T_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Code review              Date                         Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Nikhil Malhotra          17552                 	03/01/2021                   Original Version 
******************************************************************************************************************************************************************************/
Public with sharing class PureCloudLogsCleaner_B_HUM implements Database.Batchable<sObject>,Schedulable
{
    public String sQuery = '';
    
    public PureCloudLogsCleaner_B_HUM()
    {
        sQuery = 'Select ID from SoftPhone_Call_Log__c where CreatedDate < LAST_N_DAYS:4';
    }
 
    public Database.QueryLocator start(Database.BatchableContext context)
    {
        return Database.getQueryLocator(sQuery);
    }
 
    public void execute(SchedulableContext ctx) 
    {
        Database.executeBatch(new PureCloudLogsCleaner_B_HUM());
    }
    
    public void execute(Database.BatchableContext context,List<SoftPhone_Call_Log__c> callLogsList)
    {
        try
        {
            if(SoftPhone_Call_Log__c.sObjectType.getDescribe().isDeletable())
            {
                list<Database.DeleteResult> delCallLogsList = Database.delete(callLogsList,false);
                HUMExceptionHelper.processDeleteResults(delCallLogsList ,callLogsList ,'PureCloudLogsCleaner_B_HUM','execute','SoftPhone_Call_Log__c');
            }
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PureCloudLogsCleaner_B_HUM','execute');
        }
    }
    
    public void finish(Database.BatchableContext bc)
    {
    }
}