/************************************************************************************************************************************************
Apex Class Name  : OmniTimer_T_HUM
Version          : 1.0
Created Date     : 2nd Feb 2019
Function         : Test class for OmniTimer_C_HUM
* Version		Developer Name		Code Review		Date         Description
------------------------------------------------------------------------------------------------------------------------------
* 1.0			Pradeep Dani		350910			4/11/2019    Original Version
*****************************************************************************************************************************************************************************/

@isTest private class OmniTimer_T_HUM
{
	/**
	* Method to create data setup.
	*
	* @return                 void
	*/ 
	@testSetup static void setUpTestData()
	{
		User testUser = HUMTestDataHelperTest.createTestUser('Pharmacy_User', 'Humana Pharmacy Specialist');
		testUser.Current_Queue__c = 'Humana Pharmacy Calls';
		testUser.UserPermissionsLiveAgentUser = true;
		insert testUser;
		system.assertEquals(testUser.Current_Queue__c == 'Humana Pharmacy Calls', true);
        
		Account oAcc = HUMTestDataHelperTest.getAccount();
		oAcc.name = 'AccTest';
		oAcc.enterprise_id__C = '1037402785';
		oAcc.OwnerId = testUser.Id;
		insert oAcc;
		system.assertEquals(oAcc.Id <> NULL, true);

		Case oCase = HUMTestDataHelperTest.getCase();
		oCase.AccountId = oAcc.Id;
		oCase.ChatKey__c = 'Test';
		oCase.Status = 'New';
		oCase.Origin = 'Web Chat';
		oCase.Classification_Type__c = 'Humana Pharmacy Calls';
		oCase.Department__c = 'Calls';
		oCase.Service_Center__c = 'Humana Pharmacy';
		oCase.Owner_Queue__c = 'Humana Pharmacy Calls';
		oCase.Case_Owner__c = testUser.Name;
		oCase.OwnerId = testUser.Id;
		insert oCase;
		system.assertEquals(oCase.Id <> NULL, true);

		Work_Queue_Setup__c objWorkQueue = new Work_Queue_Setup__c();
		objWorkQueue.Name = 'Humana Pharmacy Calls';
		objWorkQueue.Service_Center__c = 'Humana Pharmacy';
		objWorkQueue.Department__c = 'Calls';
		objWorkQueue.IsActive__c = TRUE;
		insert objWorkQueue;
        system.assertEquals(objWorkQueue.Name == 'Humana Pharmacy Calls', true);
        
        Work_Queue_Setup__c objWorkQueue2 = new Work_Queue_Setup__c();
		objWorkQueue2.Name = 'Humana Pharmacy Web Chat';
		objWorkQueue2.Service_Center__c = 'Humana Pharmacy';
		objWorkQueue2.Department__c = 'Calls';
		objWorkQueue2.IsActive__c = TRUE;
		insert objWorkQueue2;
        system.assertEquals(objWorkQueue2.Name == 'Humana Pharmacy Web Chat', true);
	}

	/**
	* Method to test normal case transfer.
	*
	* @return                 void
	*/
	@isTest static void getCaseDetails()
	{
        string result;
        boolean res;
		Account objAcc = [SELECT Id FROM Account WHERE enterprise_id__c = '1037402785' LIMIT 1];
		User objUser = [SELECT Id, Name FROM User WHERE LastName = 'test Pharmacy_User'];
		Case objCase = [Select Id, OwnerId, Status, Owner_Queue__c, Case_Owner__c, Classification_Type__c, Department__c, Service_Center__c, ChatKey__c from Case where ChatKey__c = 'Test' limit 1];
        System.runAs(objUser) {
			Test.startTest();
			OmniTimer_C_HUM omni = new OmniTimer_C_HUM();
			result = OmniTimer_C_HUM.transferCase(objAcc.Id, 'Test');
            res = result.contains('SUCCESS_');
            System.Assert(res,true);
			Test.stopTest();
		}
	}

	/**
	* Method to test HP Web Chat case transfer.
	*
	* @return                 void
	*/
	@isTest static void getCaseDetailsPGQ()
	{
        string result;
        boolean res;
		Account objAcc = [SELECT Id FROM Account WHERE enterprise_id__c = '1037402785' LIMIT 1];
		User objUser = [SELECT Id, Name FROM User WHERE LastName = 'test Pharmacy_User'];
		Case objCase = [Select Id, OwnerId, Status, Owner_Queue__c, Case_Owner__c, Classification_Type__c, Department__c, Service_Center__c, ChatKey__c from Case where ChatKey__c = 'Test' limit 1];
		Group newGroup1 = new Group(Name = 'Humana Pharmacy Web Chat PGQ');
		Group newGroup2 = new Group(Name = 'Humana Pharmacy Web Chat');
        insert newGroup1;
        insert newGroup2;
		GroupMember gmr1 = new GroupMember();
		gmr1.GroupId = newGroup1.Id;
		gmr1.UserOrGroupId = objUser.Id;
		insert gmr1;
        GroupMember gmr2 = new GroupMember();
		gmr2.GroupId = newGroup2.Id;
		gmr2.UserOrGroupId = objUser.Id;
		insert gmr2;
		System.runAs(objUser) 
		{
			Test.startTest();
			OmniTimer_C_HUM omni = new OmniTimer_C_HUM();
			result = OmniTimer_C_HUM.transferCase(objAcc.Id, 'Test');
            res = result.contains('SUCCESS');
            System.Assert(res,true);
			Test.stopTest();
		}
	}

	/**
	* Method to test negative scenario of tranfer case.
	*
	* @return                 void
	*/
	@isTest static void getCaseDetailsNegative()
	{
		User objUser = [SELECT Id, Name FROM User WHERE LastName = 'test Pharmacy_User'];
		System.runAs(objUser) 
		{
            string result;
			Test.startTest();
			OmniTimer_C_HUM omni = new OmniTimer_C_HUM();
			result = OmniTimer_C_HUM.transferCase(null, null);
            System.AssertEquals(result,'FAIL');
            Test.stopTest();
        }
    }
    /**
    * Method to test negative scenario of tranfer case.
    *
    * @return                 void
    */
    @isTest static void getCaseDetailsNoAccidNoChatId()
    {
        User objUser = [SELECT Id, Name FROM User WHERE LastName = 'test Pharmacy_User'];
        Account objAcc = [SELECT Id FROM Account WHERE enterprise_id__c = '1037402785' LIMIT 1];
        System.runAs(objUser) 
        {
            string result;
            Test.startTest();
            OmniTimer_C_HUM omni = new OmniTimer_C_HUM();
            result = OmniTimer_C_HUM.transferCase(null, 'test');
            System.AssertEquals(result,'FAIL');

            result = OmniTimer_C_HUM.transferCase(objAcc.Id, null);
            System.AssertEquals(result,'FAIL');
            Test.stopTest();
        }
    }
    /**
    * Method to test negative scenario of tranfer case.
    *
    * @return                 void
    */
    @isTest static void getCaseDetailsNoUserId()
    {
        
        Account objAcc = [SELECT Id FROM Account WHERE enterprise_id__c = '1037402785' LIMIT 1];
        Case objCase = [Select Id, OwnerId, Status, Owner_Queue__c, Case_Owner__c, Classification_Type__c, Department__c, Service_Center__c, ChatKey__c from Case where ChatKey__c = 'Test' limit 1];
  
            string result;
            Test.startTest();
            OmniTimer_C_HUM omni = new OmniTimer_C_HUM();
            result = OmniTimer_C_HUM.transferCase(objAcc.Id, 'test');
            System.AssertEquals(result,'FAIL');
            Test.stopTest();

    }
    
    /**
    * Method to test negative scenario of tranfer case.
    *
    * @return                 void
    */
    @isTest static void getCaseDetailsNoCase()
    {
         User objUser = [SELECT Id, Name FROM User WHERE LastName = 'test Pharmacy_User'];
        Account objAcc = [SELECT Id FROM Account WHERE enterprise_id__c = '1037402785' LIMIT 1];
        System.runAs(objUser) 
        {
            string result;
            Test.startTest();
            OmniTimer_C_HUM omni = new OmniTimer_C_HUM();
            result = OmniTimer_C_HUM.transferCase(objAcc.Id, 'testing');
            System.AssertEquals(result,'FAIL');
            Test.stopTest();
        }
    }
    
}