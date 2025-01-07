/***************************************************************************************************************************************************
Apex Class Name  : OmniQueueEdit_T_HUM
Version          : 1.0
Created Date     : 11/01/2017
Function         : Test Class for OmniQueueEdit_X_HUM
****************************************************************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                       Description
----------------------------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor              144378                 11/01/2017                Original Version 
* Mohammed Noor              144993                 11/06/2017                Correct the Controller Extension Name as per standards.
* Syed Mubarak T                                    12/04/2017                Modified Code for Omni Routing Configuration
* Syed Mubarak T                                    03/01/2019                Modified for Omni COnfiuration Item changes code coverage
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
* Pooja Kumbhar										05/11/2022				Improving Code coverage
****************************************************************************************************************************************************/

@isTest
private class OmniQueueEdit_T_HUM {
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert objUser;
        
        User objUser2 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert objUser2;
        
        User objUser3 = HUMTestDataHelperTest.createTestUser('User3','Customer Care Specialist');
        insert objUser3;
        
        PermissionSet perm = [SELECT Name, Label, Id FROM PermissionSet where name = :GLOBAL_CONSTANT_HUM.METENROLLMENTMEDICAREEDIT_PERMISSIONSET_HUM Limit 1];
        insert new PermissionSetAssignment(AssigneeId = objUser2.id, PermissionSetId = perm.id);
        insert new PermissionSetAssignment(AssigneeId = objUser3.id, PermissionSetId = perm.id);
        
        PermissionSet perm1 = [SELECT Name, Label, Id FROM PermissionSet where name = 'CRMS_605_BizConfig_MedicareEnrollment_Edit' Limit 1];
        insert new PermissionSetAssignment(AssigneeId = objUser.id, PermissionSetId = perm1.id);
        insert new PermissionSetAssignment(AssigneeId = objUser2.id, PermissionSetId = perm1.id);
        insert new PermissionSetAssignment(AssigneeId = objUser3.id, PermissionSetId = perm1.id);
        
        system.assertEquals(objUser.lastname=='test User1',true);
        
        system.runAs(objUser)
        {             
            //create OmniQueue
            Group oQueue = new Group();
            oQueue.name = 'Test omni Queue';
            oQueue.type = 'Queue';
            insert oQueue;
             
            //Create StaginQueue
            Group sQueue = new Group();
            sQueue.name = 'Test omni Queue_Stg';
            sQueue.type = 'Queue';
            insert sQueue;
             
             
            GroupMember userData = new GroupMember();
            userData.GroupId = oQueue.Id;
            userData.UserOrGroupId = objUser.Id;
            insert userData;
             
            userData = new GroupMember();
            userData.GroupId = oQueue.Id;
            userData.UserOrGroupId = objUser2.Id;
            insert userData;
            
            Omni_Routing_Configuration__c routingConfigData =  new Omni_Routing_Configuration__c();
           routingConfigData.Name = 'RoutingConfigTest';
           routingConfigData.Is_Active__c = true;
           routingConfigData.Push_Time_Out__c = 100;
           routingConfigData.QueueRoutingConfig_Id__c = null;
           routingConfigData.Routing_Priority__c = 1;
           routingConfigData.Configuration_Item_Name__c = 'Medicare_Enrollment';
           insert routingConfigData;
		   
		   Omni_Routing_Configuration__c routingConfigData1 =  new Omni_Routing_Configuration__c();
           routingConfigData1.Name = 'RoutingConfigTest2';
           routingConfigData1.Is_Active__c = true;
           routingConfigData1.Push_Time_Out__c = 100;
           routingConfigData1.QueueRoutingConfig_Id__c = null;
           routingConfigData1.Routing_Priority__c = 1;
           routingConfigData1.Configuration_Item_Name__c = 'Medicare_Enrollment';
           insert routingConfigData1;
             
            OmniQueue__c omniData = new OmniQueue__c();
            omniData.Name = 'Test Omni Queue';
            omniData.Maximum_Cases__c = 50;
            omniData.Omni_Queue_ID__c = oQueue.Id;
            omniData.Staging_Queue_ID__c = sQueue.Id;
            omniData.Staging_Queue_Name__c = sQueue.Name;
            omniData.Routing_Configuration_Name__c = routingConfigData.Id;
            insert omniData;   
            
            Omni_Configuration_Item__c oc = new Omni_Configuration_Item__c();
            oc.Business_Area__c = 'Medicare Enrollment';
            oc.Name = 'Medicare_Enrollment';
            oc.Maximum_Limit__c = 48000;
            oc.Maximum_No_of_Work_Items__c = 999;
            oc.Work_Item__c = 'Case';
            oc.Default_Queue_Name__c = sQueue.Name;
            oc.Default_Queue_Id__c = sQueue.Id;
            oc.Overflow_Assignee_Queue__c = sQueue.Name;
            oc.Overflow_Assignee_Queue_Id__c = sQueue.Id;
            insert oc;
             
        }
    }
    
    /*
    * This method is used to test the Create funtionality
    * @param   None
    * @return  None
    */
    static testMethod void testNewOmniQueue()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User1'];
        User testUser2 = [Select Id,Name, Current_Queue__C from User where LastName='test User2'];
        User testUser3 = [Select Id,Name, Current_Queue__C from User where LastName='test User3'];
        System.runAs(testUser)
        {
            Test.startTest();           
            Test.setCurrentPage(page.OmniQueueEdit_VF_HUM);
            OmniQueue__c newQueue = new OmniQueue__c();
            Omni_Routing_Configuration__c oRConfig = [SELECT Id,Name,Routing_Priority__c,Push_Time_Out__c,Is_Active__c FROM Omni_Routing_Configuration__c WHERE Name = 'RoutingConfigTest'];
            ApexPages.StandardController scon = new ApexPages.StandardController(newQueue);                 
            OmniQueueEdit_X_HUM oQueueCon = new OmniQueueEdit_X_HUM(scon);
            
            
            
            System.assertEquals(oQueueCon.omniQueueObj.Maximum_Cases__c, 50);
            
            oQueueCon.getQueueMembers();            
            System.assertEquals(oQueueCon.lstUsers.isEmpty(), true);
            
            System.assertEquals(oQueueCon.viewUserLookup, false);
            oQueueCon.getopenUserLookup();
            System.assertEquals(oQueueCon.viewUserLookup, true);
            oQueueCon.getopenUserLookup();
            System.assertEquals(oQueueCon.viewUserLookup, false);
            
            oQueueCon.getAvailUserList();
            System.assertEquals(String.isNotBlank(oQueueCon.userJsonString), true);
            
            oQueueCon.omniQueueObj.Name = '';
            oQueueCon.omniQueueObj.Maximum_Cases__c = 50000;
            oQueueCon.omniQueueObj.Routing_Configuration_Name__c = oRConfig.Id;
            oQueueCon.getRemainingThreshold();
            oQueueCon.validateInput();
            
            oQueueCon.omniQueueObj.Name = 'abcdef';
            oQueueCon.validateInput();
            oQueueCon.omniQueueObj.Maximum_Cases__c = 500;   
            
            oQueueCon.addUsersJson = JSON.serialize(new List<User>{testUser3});
            oQueueCon.bNameError = true;
            oQueueCon.bMaxCaseError = true;
            oQueueCon.bRoutingConfigError = true;
            oQueueCon.saveQueuesAndUsers();
            
            oQueueCon.bNameError = false;
            oQueueCon.bMaxCaseError = false;
            oQueueCon.bRoutingConfigError = false;
            oQueueCon.saveSelectedUsers();
            oQueueCon.omniQueueObj.Name = 'ghijkl';
            oQueueCon.saveQueuesAndUsers();
            
            System.assertNotEquals(oQueueCon.omniQueueObj.Staging_Queue_ID__c, NULL);
            System.assertNotEquals(oQueueCon.omniQueueObj.Omni_Queue_ID__c, NULL);
            System.assertNotEquals(oQueueCon.omniQueueObj.Staging_Queue_Name__c, NULL);
            System.assertEquals(oQueueCon.bError, false);  
            
            oQueueCon.saveCustom();
            System.assertEquals(oQueueCon.bError, false);
            System.assertNotEquals(oQueueCon.omniQueueObj.Id, NULL);
            
            oQueueCon.save();
            
            oQueueCon.addUsersToQueue(new set<ID>{testUser.Id, testUser2.Id});
            
            oQueueCon.addUsersToQueue(new set<ID>{});
            
            oQueueCon.userToRemove = testUser2.Id;
            oQueueCon.removeUser();
            oQueueCon.removeUsersFromQueue(new List<User>{testUser});
            
            oQueueCon.omniQueueObj.Name = '123';
            oQueueCon.saveQueuesAndUsers();
            Test.stopTest();               
        }   
    } 
    
    /*
    * This method is used to test the Create funtionality
    * @param   None
    * @return  None
    */
    static testMethod void testOmniQueueUpdate()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User1'];
        User testUser2 = [Select Id,Name, Current_Queue__C from User where LastName='test User2'];      
        System.runAs(testUser)
        {
            Test.startTest();           
            OmniQueue__c oData = [SELECT ID, Name, Maximum_Cases__c, Routing_Configuration_Name__c, Omni_Queue_ID__c, Staging_Queue_ID__c, Staging_Queue_Name__c FROM OmniQueue__c 
                                  WHERE Name = 'Test omni Queue']; 
            system.assertEquals(oData != null,true);
            Test.setCurrentPage(page.OmniQueueEdit_VF_HUM);
            OmniQueue__c newQueue = new OmniQueue__c();
            ApexPages.StandardController scon = new ApexPages.StandardController(oData); 
            ApexPages.currentPage().getParameters().put('Id',oData.Id);                
            OmniQueueEdit_X_HUM oQueueCon = new OmniQueueEdit_X_HUM(scon);
            
            oQueueCon.getQueueMembers();
            System.assertEquals(oQueueCon.lstUsers.size(), 2);
            
            oQueueCon.omniQueueObj.Name = 'Test omni Queue Updt';
            oQueueCon.omniQueueObj.Maximum_Cases__c = 400;                        
            
            oQueueCon.userToRemove = testUser2.Id;
            oQueueCon.removeUser();
            
            oQueueCon.saveQueuesAndUsers();
            System.assertEquals(oQueueCon.bError, false);
            System.assertEquals(oQueueCon.omniQueueObj.Name, 'Test omni Queue Updt');
            System.assertEquals(oQueueCon.omniQueueObj.Staging_Queue_Name__c, 'Test omni Queue Updt_Stg');
            
            oQueueCon.saveCustom();
            System.assertEquals(oQueueCon.bError, false);
            
            oQueueCon.omniQueueObj.Name = '';
            oQueueCon.saveQueuesAndUsers();
            Test.stopTest();               
        }   
    }
	
	static testMethod void testNewOmniQueueException()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User1'];
        User testUser2 = [Select Id,Name, Current_Queue__C from User where LastName='test User2'];
        User testUser3 = [Select Id,Name, Current_Queue__C from User where LastName='test User3'];
        System.runAs(testUser2)
        {
            Test.startTest(); 
            OmniQueue__c newQueue1 = new OmniQueue__c();
            ApexPages.StandardController scon1 = new ApexPages.StandardController(newQueue1);

            OmniQueueEdit_X_HUM oQueueCon1 = new OmniQueueEdit_X_HUM(scon1);
            Test.setCurrentPage(page.OmniQueueEdit_VF_HUM);
            OmniQueue__c newQueue = new OmniQueue__c();
            
            Omni_Routing_Configuration__c oRConfig = [SELECT Id,Name,Routing_Priority__c,Push_Time_Out__c,Is_Active__c FROM Omni_Routing_Configuration__c WHERE Name = 'RoutingConfigTest'];

            Omni_Routing_Configuration__c oRConfig2 = [SELECT Id,Name,Routing_Priority__c,Push_Time_Out__c,Is_Active__c FROM Omni_Routing_Configuration__c WHERE Name = 'RoutingConfigTest2'];            
            
            ApexPages.StandardController scon = new ApexPages.StandardController(newQueue);
                            
            OmniQueueEdit_X_HUM oQueueCon = new OmniQueueEdit_X_HUM(scon);
            oQueueCon.checkDisableEdit();
            System.assertEquals(oQueueCon.omniQueueObj.Maximum_Cases__c, 50);
            
            oQueueCon.getQueueMembers();            
            System.assertEquals(oQueueCon.lstUsers.isEmpty(), true);
            
            System.assertEquals(oQueueCon.viewUserLookup, false);
            oQueueCon.getopenUserLookup();
            System.assertEquals(oQueueCon.viewUserLookup, true);
            oQueueCon.getopenUserLookup();
            System.assertEquals(oQueueCon.viewUserLookup, false);
            
            oQueueCon.getAvailUserList();
            System.assertEquals(String.isNotBlank(oQueueCon.userJsonString), true);
            
            oQueueCon.omniQueueObj.Name = '';
            oQueueCon.omniQueueObj.Maximum_Cases__c = 50000;
            oQueueCon.omniQueueObj.Routing_Configuration_Name__c = oRConfig.Id;
            oQueueCon.getRemainingThreshold();
            oQueueCon.validateInput();
            
            oQueueCon.omniQueueObj.Name = 'abcdef';
            oQueueCon.validateInput();
            oQueueCon.omniQueueObj.Maximum_Cases__c = 500;   
            
            oQueueCon.addUsersJson = JSON.serialize(new List<User>{testUser});
            oQueueCon.bNameError = true;
            oQueueCon.bMaxCaseError = true;
            oQueueCon.bRoutingConfigError = true;
            oQueueCon.saveQueuesAndUsers();
            
            oQueueCon.bNameError = false;
            oQueueCon.bMaxCaseError = false;
            oQueueCon.bRoutingConfigError = false;
            oQueueCon.saveSelectedUsers();
            oQueueCon.omniQueueObj.Name = 'ghijkl';
            oQueueCon.omniQueueObj.Routing_Configuration_Name__c = oRConfig2.Id;
            oQueueCon.saveQueuesAndUsers();
            
            System.assertNotEquals(oQueueCon.omniQueueObj.Staging_Queue_ID__c, NULL);
            System.assertNotEquals(oQueueCon.omniQueueObj.Omni_Queue_ID__c, NULL);
            System.assertNotEquals(oQueueCon.omniQueueObj.Staging_Queue_Name__c, NULL);
            System.assertEquals(oQueueCon.bError, false);  
            
            oQueueCon.saveCustom();
            System.assertEquals(oQueueCon.bError, false);
            System.assertNotEquals(oQueueCon.omniQueueObj.Id, NULL);
            
            oQueueCon.save();
            
            oQueueCon.addUsersToQueue(new set<ID>{testUser.Id, testUser2.Id});
            
            oQueueCon.addUsersToQueue(new set<ID>{});
            
            oQueueCon.userToRemove = testUser2.Id;
            oQueueCon.removeUser();
            oQueueCon.removeUsersFromQueue(new List<User>{testUser});
            
            Test.stopTest();    

        }
    }
    
    static testMethod void testOmniQueueUpdate2()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User1'];
        User testUser2 = [Select Id,Name, Current_Queue__C from User where LastName='test User2'];      
        System.runAs(testUser)
        {
            Test.startTest();           
            OmniQueue__c oData = [SELECT ID, Name, Maximum_Cases__c, Routing_Configuration_Name__c, Omni_Queue_ID__c, Staging_Queue_ID__c, Staging_Queue_Name__c FROM OmniQueue__c 
                                  WHERE Name = 'Test omni Queue']; 
            Omni_Routing_Configuration__c  oRC=[Select Id from Omni_Routing_Configuration__c  where Name = 'RoutingConfigTest2'];
            system.assertEquals(oData != null,true);
            Test.setCurrentPage(page.OmniQueueEdit_VF_HUM);
            OmniQueue__c newQueue = new OmniQueue__c();
            ApexPages.StandardController scon = new ApexPages.StandardController(oData); 
            ApexPages.currentPage().getParameters().put('Id',oData.Id);                
            OmniQueueEdit_X_HUM oQueueCon = new OmniQueueEdit_X_HUM(scon);
               oQueueCon.saveQueuesAndUsers();
            oQueueCon.getQueueMembers();
            System.assertEquals(oQueueCon.lstUsers.size(), 2);
            
            oQueueCon.omniQueueObj.Name = 'Test omni Queue Updt';
            oQueueCon.omniQueueObj.Maximum_Cases__c = 400;                        
            
            oQueueCon.userToRemove = testUser2.Id;
            oQueueCon.removeUser();
            
            oQueueCon.omniQueueObj.Routing_Configuration_Name__c = oRC.Id;
            //oQueueCon.bSetupError = false;
            
            oQueueCon.saveQueuesAndUsers();
            
            System.assertEquals(oQueueCon.bError, false);
            System.assertEquals(oQueueCon.omniQueueObj.Name, 'Test omni Queue Updt');
            System.assertEquals(oQueueCon.omniQueueObj.Staging_Queue_Name__c, 'Test omni Queue Updt_Stg');
            
            
            oQueueCon.saveCustom();
            System.assertEquals(oQueueCon.bError, false);
            
            oQueueCon.omniQueueObj.Routing_Configuration_Name__c = null;
            oQueueCon.saveQueuesAndUsers();
            
            Test.stopTest();               
        }   
    }
    
    static testMethod void testOmniQueueUpdate3()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User1'];
        User testUser2 = [Select Id,Name, Current_Queue__C from User where LastName='test User2'];      
        System.runAs(testUser)
        {
            Test.startTest();           
            OmniQueue__c oData = [SELECT ID, Name, Maximum_Cases__c, Routing_Configuration_Name__c, Omni_Queue_ID__c, Staging_Queue_ID__c, Staging_Queue_Name__c FROM OmniQueue__c 
                                  WHERE Name = 'Test omni Queue']; 
            Omni_Routing_Configuration__c  oRC=[Select Id from Omni_Routing_Configuration__c  where Name = 'RoutingConfigTest2'];
            system.assertEquals(oData != null,true);
            Test.setCurrentPage(page.OmniQueueEdit_VF_HUM);
            OmniQueue__c newQueue = new OmniQueue__c();
            ApexPages.StandardController scon = new ApexPages.StandardController(oData); 
            ApexPages.currentPage().getParameters().put('Id',oData.Id);                
            OmniQueueEdit_X_HUM oQueueCon = new OmniQueueEdit_X_HUM(scon);
            
            oQueueCon.getQueueMembers();
            System.assertEquals(oQueueCon.lstUsers.size(), 2);
         
            oQueueCon.omniQueueObj.Name = 'Test omni Queue Updt';
            oQueueCon.omniQueueObj.Maximum_Cases__c = null;                        
            
            oQueueCon.userToRemove = testUser2.Id;
            oQueueCon.removeUser();
            
            oQueueCon.omniQueueObj.Routing_Configuration_Name__c = oRC.Id;
            //oQueueCon.bSetupError = false;
            
            oQueueCon.saveQueuesAndUsers();
            
            //System.assertEquals(oQueueCon.bError, false);
            System.assertEquals(oQueueCon.omniQueueObj.Name, 'Test omni Queue Updt');
            System.assertEquals(oQueueCon.omniQueueObj.Staging_Queue_Name__c, 'Test omni Queue Updt_Stg');
            
            
            oQueueCon.saveCustom();
            //System.assertEquals(oQueueCon.bError, false);
            oQueueCon.omniQueueObj.Name = '';
            oQueueCon.saveQueuesAndUsers();
            
            oQueueCon.omniQueueObj.Routing_Configuration_Name__c = null;
            oQueueCon.validateThreshold(40000,10000);
            oQueueCon.saveQueuesAndUsers();
            
            Test.stopTest();               
        }   
    }
    
    static testMethod void testvalidateThreshold()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();           
            OmniQueue__c oData = [SELECT ID, Name, Maximum_Cases__c, Routing_Configuration_Name__c, Omni_Queue_ID__c, Staging_Queue_ID__c, Staging_Queue_Name__c FROM OmniQueue__c 
                                  WHERE Name = 'Test omni Queue']; 
            Omni_Routing_Configuration__c  oRC=[Select Id from Omni_Routing_Configuration__c  where Name = 'RoutingConfigTest2'];
            system.assertEquals(oData != null,true);
            Test.setCurrentPage(page.OmniQueueEdit_VF_HUM);
            OmniQueue__c newQueue = new OmniQueue__c();
            ApexPages.StandardController scon = new ApexPages.StandardController(oData); 
            ApexPages.currentPage().getParameters().put('Id',oData.Id);                
            OmniQueueEdit_X_HUM oQueueCon = new OmniQueueEdit_X_HUM(scon);
             oQueueCon.validateThreshold(40000,10000);
            oQueueCon.valDuplicateExist('Test omni Queue');
    }
    }                  
}