/*
  Apex Class Name  : OmniQueueUserDataTable_T_HUM   
  Version          : 1.0 
  Created Date     : OCT 25 2017
  Function         : Test class for OmniQueueUserDataTable_CE_HUM
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Syed Mubarak T             141054                    10/25/2017                      Original Version
* 1.1						Syed Mubarak T										 12/04/2017						 Deployment Issue fix
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

@isTest
private class OmniQueueUserDataTable_T_HUM
{

    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert objUser;
        User objUser2 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert objUser2;
		PermissionSet ps = [SELECT ID From PermissionSet WHERE Name = 'CRMS_605_BizConfig_MedicareEnrollment_Edit'];
		insert new PermissionSetAssignment(AssigneeId = objUser2.id, PermissionSetId = ps.Id );
        system.assertEquals(objUser.lastname=='test User1',true);
        system.runAs(objUser)
        {
             Group queueData = new Group();
             queueData.name = 'Test omni Queue';
             queueData.type = 'Queue';
             insert queueData;
             
             GroupMember userData = new GroupMember();
             userData.GroupId = queueData.Id;
             userData.UserOrGroupId = objUser.Id;
             insert userData;
             
            OmniQueue__c omniData = new OmniQueue__c();
            omniData.Name = 'Test Omni Queue';
            omniData.Omni_Queue_ID__c = queueData.Id;
            insert omniData;
             
        }
    }
    
    static testMethod void testOmniUsersDataTable()
    {
            User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User2'];
            System.runAs(testUser)
            {
                Test.startTest();
                OmniQueue__c oData = [SELECT Id,Omni_Queue_ID__c  FROM OmniQueue__c];
                system.assertEquals(oData != null,true);
                Test.setCurrentPage(page.OmniQueueUserDataTable_VF_HUM);
                ApexPages.StandardController scon = new ApexPages.StandardController(oData);
                 ApexPages.currentPage().getParameters().put('Id',oData.Id);
                 OmniQueueUserDataTable_X_HUM oQueue = new OmniQueueUserDataTable_X_HUM(scon);
                 oQueue.fetchOmniUsers();
                
            }
    }

}