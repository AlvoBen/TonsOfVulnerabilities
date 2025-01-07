/*******************************************************************************************************************************
Apex Class Name : HUMUpdateWorkQueueSetupTriggerHelperTest
Version         : 1.0
Created On      : 12/16/2014
Function        : Used for trigger functionality.updates ownerid field with queue name. shows error messages if user enters wrong queue name.

Modification Log: 
* Developer Name                  Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* SuryaKumari Medicherla          19954                     12/16/2014                  Original Version
* Pradeepkumar Dani				  98235						06/06/2017                  Classification_Type_Old__c Clean up
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
******************************************************************************************************************************/
@isTest
private class HUMUpdateWorkQueueSetupTriggerHelperTest
{
    /*
    * Method Name : updateOwnerId
    * Description : Test method to create a work queue setup
    * Return type : Void
    * Paramater   : N/A
    */
    static testMethod void updateOwnerId() 
    {
        //Create test user
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Test';
        insert oUser;
        System.assert(oUser.id != null);
        
        system.runAs(oUser)
        {
            Group oGrp=new Group(Name='Test Queue', Type='QUEUE');
            insert oGrp;
            System.assert(oGrp.id!= null);
            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject;  
            System.assert(oQueueObject.id!= null);
            
			Classification_Type__c ct = new Classification_Type__c(Name = 'Test', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
			insert ct;
			system.assertEquals(ct.Name, 'Test');

            Work_Queue_Setup__c oWQS1 = new Work_Queue_Setup__c();
            oWQS1.Name='Test';
            oWQS1.Service_Center__c='Test';
            oWQS1.Department__c='Test';
            oWQS1.Classification_Type__c = ct.Id;
            oWQS1.OwnerId= oGrp.id;
            insert oWQS1;
            System.assert(oWQS1.id!= null);
			
			Work_Queue_Setup__c oWQS2 = new Work_Queue_Setup__c();
            oWQS2.Name='Test Queue1';
            oWQS2.Service_Center__c='Test';
            oWQS2.Department__c='Test';
            oWQS2.Classification_Type__c = ct.Id;
            oWQS2.OwnerId=  oGrp.id;
            insert oWQS2;
            
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
            oWQS.Name= NULL;
            oWQS.Service_Center__c='Test';
            oWQS.Department__c='Test';
            oWQS.Classification_Type__c = ct.Id;
            oWQS.OwnerId= oGrp.id;
            insert oWQS;
            
            List<Work_Queue_Setup__c> lstWQSRecords= new List<Work_Queue_Setup__c>();
            lstWQSRecords.add(oWQS);
            lstWQSRecords.add(oWQS1);
            lstWQSRecords.add(oWQS2);
            
            HUMUpdateWorkQueueSetupTriggerHelper hwqst = new HUMUpdateWorkQueueSetupTriggerHelper();
            
            hwqst.checkEnteredQueue(lstWQSRecords);

        }
    }
}