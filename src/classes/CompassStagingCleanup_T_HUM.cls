/***************************************************************************************************************************************************************************
@Apex Class Name  : CompassStagingCleanup_T_HUM
@Version          : 1.0
@Created Date     : August 12, 2019
@Function         : Test Class for CompassStagingCleanup_T_HUM
@Test Class       : 
****************************************************************************************************************************************************************************
Modification Log:
* Developer Name            Code Review             Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Rakesh Patni                                  08/12/2019                  Original Version
***************************************************************************************************************************************************************************/
@isTest
private class CompassStagingCleanup_T_HUM 
{
	/*
    * Method Name : setUpTestData
    * Description : This method creates test data For test Class
    * Return type : None
    * Paramater   : None
    */
    @testSetup static void setUpTestData()
    {   	
    	Group oNewGroup = null;
        oNewGroup = new Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oNewGroup;
        system.assert(oNewGroup.Type=='Queue');

        QueueSObject oNewQueue = new QueueSObject(QueueId = oNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;
         system.assertEquals(oNewQueue.SObjectType,'Work_Queue_Setup__c');

        User oCCSUser = HUMTestDataHelperTest.createTestUser('QuickStartSpecialist','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.IsActive,true);
        
        system.runAs(oCCSUser)
        {
    	List<Compass_Staging__c> lstQSMentorLinks = new List<Compass_Staging__c>();
    	lstQSMentorLinks.add(new Compass_Staging__c(name=String.valueOf(Datetime.now().getTime())+UserInfo.getUserId(),Document_ID__c='testId',Document_Name__c='testName',Document_URL__c='https://www.salesforce.com'));       	
    	lstQSMentorLinks.add(new Compass_Staging__c(name=String.valueOf(Datetime.now().getTime())+UserInfo.getUserId()+'Orphan',Document_ID__c='testId2',Document_Name__c='testName2',Document_URL__c='https://www.google.com'));
    	
    	insert lstQSMentorLinks;
    	system.assert(lstQSMentorLinks[1].name.length()>18);
    	Test.setCreatedDate(lstQSMentorLinks[0].id,DateTime.now().addMonths(-4));
        }
    }
    /*
    * Method Name : testBatchClass
    * Description : This method tests the Batch Class
    * Return type : None
    * Paramater   : None
    */
    @isTest
    static void testBatchClass()
    {
    	test.starttest();
    	CompassStagingCleanup_B_HUM compSch = new CompassStagingCleanup_B_HUM();
    	String sch = '0 0 2 ? * SAT';
    	String jobId = System.schedule('Purge Compass Staging Test',sch,compSch);
    	Database.executeBatch(new CompassStagingCleanup_B_HUM());
    	test.stoptest();
    	System.assertEquals(1,[Select count() from Compass_Staging__c]);
    }
}