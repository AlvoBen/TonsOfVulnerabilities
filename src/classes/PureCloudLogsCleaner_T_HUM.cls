/*****************************************************************************************************************************************************************************
@Apex Class Name  : PureCloudLogsCleaner_T_HUM
@Version          : 1.0
@Created Date     : 03/01/2021
@Function         : Test Class for PureCloudLogsCleaner_B_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Code review              Date                         Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Nikhil Malhotra                           	03/02/2021                   Original Version 
******************************************************************************************************************************************************************************/
@isTest
public class PureCloudLogsCleaner_T_HUM 
{
    public static string CRON_EXP = '0 0 5 * * ? *';
    
    //Method to create tesData
	@testSetup
    static void setup()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
    	u.LastName = 'TestUser';
        insert u;
		System.assertEquals(u.id != null, true);        
        list<SoftPhone_Call_Log__c> callLogs = new list<SoftPhone_Call_Log__c>();
        System.runAs(u)
        {
            for(integer i=0; i<100; i++)
            {
                SoftPhone_Call_Log__c callLog = new SoftPhone_Call_Log__c();
                callLogs.add(callLog);
            }
            insert callLogs;
            System.assertEquals(callLogs.size() == 100, true);
            for(SoftPhone_Call_Log__c callLog : callLogs)
            {
                Test.setCreatedDate(callLog.id, DateTime.now().addDays(-10));
            }
        }
    }
    
    //Method to test Batch job
    public static testmethod void testDeleteLogs()
    {
        User u = [Select id from User where lastName='TestUser' LIMIT 1];
        System.runAs(u)
        {
            Test.startTest();
            Database.Querylocator sQueryLoc;
	        Database.BatchableContext BC;
            list<SoftPhone_Call_Log__c> pureCloudLogsList = new list<SoftPhone_Call_Log__c>();
            PureCloudLogsCleaner_B_HUM pureCloudLogsCleaner = new PureCloudLogsCleaner_B_HUM(); 
            sQueryLoc = pureCloudLogsCleaner.Start(BC);
	        Database.QueryLocatorIterator QIT = sQueryLoc.Iterator();
	        while(QIT.hasNext())
	        {
	            SoftPhone_Call_Log__c logs = (SoftPhone_Call_Log__c)QIT.next();
	            pureCloudLogsList.add(logs);
	        }
	        pureCloudLogsCleaner.execute(BC, pureCloudLogsList);
            pureCloudLogsCleaner.finish(BC);
            list<SoftPhone_Call_Log__c> pureCloudLogsTestList = [Select Id from SoftPhone_Call_Log__c where ownerId =: u.Id];
            system.assertEquals(pureCloudLogsTestList.size() == 0, true);
            Test.stopTest();
        }
    }
    
    //Method to schedule job
    private static testmethod void testScheduling()
    {
        User u = [SELECT Id FROM User WHERE lastName = 'TestUser' LIMIT 1];
        system.runAs(u)
        {
            Test.startTest();
            string sJobId = System.Schedule('My Test Scheduler', CRON_EXP, new PureCloudLogsCleaner_B_HUM());
            
            // Get the information from the CronTrigger API object
            CronTrigger ct = [SELECT Id, CronExpression, TimesTriggered, NextFireTime FROM CronTrigger WHERE id = :sjobId];
            
            // Verify the expressions are the same
            System.assertEquals(PureCloudLogsCleaner_T_HUM.CRON_EXP, ct.CronExpression);
            Test.stopTest();
        }
    }
}