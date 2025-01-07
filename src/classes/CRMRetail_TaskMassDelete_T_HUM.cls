/************************************************************************************************************************************************************************************       
Class Name       : CRMRetail_TaskMassDelete_T_HUM 
Created Date     : 25 February 2022
Function         : Test class for CRMRetail_TaskMassDelete_H_HUM
**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Vivek Sharma                               02/25/2022                User Story 3118168: T1PRJ0154546 MF9 Storefront - Mass Delete Task Records & New List View
* Lakshmi Madduri							 02/23/2024                US-5604142 PCO App Deprecation
************************************************************************************************************************************************************************************/
@isTest
private class CRMRetail_TaskMassDelete_T_HUM {
	/*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData() 
    {
        User objCRMRetailUser = HUMTestDataHelperTest.createTestUser('UserCRMRetail','CRMRetail User');
        system.assertEquals(objCRMRetailUser.LastName,'test UserCRMRetail'); 
        
        insert objCRMRetailUser;
        system.assertNotEquals(objCRMRetailUser.Id ,null);  
        
        System.runAs (objCRMRetailUser) 
        {  
            Id recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.OwnerId = objCRMRetailUser.id;
            oAcc.FirstName = 'Test';
            oAcc.LastName = 'Account';
            oAcc.Enterprise_Id__c = '123456';
            oAcc.recordtypeid = recordTypeId;
            insert oAcc;
            system.assertEquals(oAcc.LastName,'Account');
            List<Task> tskList = new List<Task>();
            Id crmRecordTypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get('CRMRetail Task').getRecordTypeId();
            Task t = new Task();
            t.CRMRetail_Automated__c = true;
            t.RecordTypeId=crmRecordTypeId;
            t.WhatId = oAcc.id;
            t.Subject= 'sub';
            t.OwnerId=objCRMRetailUser.id;
            t.ActivityDate=Date.Today();
            t.Status='Not Started';
            tskList.add(t);
            
            Task t1 = new Task();
            t1.CRMRetail_Automated__c = false;
            t1.RecordTypeId=crmRecordTypeId;
            t1.WhatId = oAcc.id;
            t1.Subject= 'sub';
            t1.OwnerId=objCRMRetailUser.id;
            t1.ActivityDate=Date.Today();
            tskList.add(t1);
            
            insert tskList;
            tskList[1].Status='In Progress';
            update tskList[1];
            system.assertEquals(tskList.size(),2);
        } 
    }
    /**
    * Returns void
    * <p>
    * This method is used for validating getTaskRecords method
    *
    * @return  Void
    * @see
    */
    @isTest static void testProcessGetTaskRecords()
    {
        User testUser = [SELECT Id, Name FROM User WHERE LastName = 'test UserCRMRetail'];
        system.assertEquals(testUser.Name , 'test UserCRMRetail');
        
        List<List<String>> eligibleItemListListToPass = new List<List<String>>();
        List<Task> eligibleTaskList = [SELECT Id,CreatedById, RecordType.DeveloperName, Status FROM Task WHERE Status = 'Not Started'];
        system.assertEquals(eligibleTaskList.size() , 1);
        
        List<List<String>> nonEligibleItemListListToPass = new List<List<String>>();
        List<Task> nonEligibleTaskList = [SELECT Id,CreatedById,RecordTypeId, WhatId, RecordType.DeveloperName, Status FROM Task WHERE Status = 'In Progress'];
        system.assertEquals(nonEligibleTaskList.size() , 1);
        
        Task t1 = new Task();
        t1.CRMRetail_Automated__c = false;
        t1.RecordTypeId=nonEligibleTaskList[0].RecordTypeId;
        t1.WhatId = nonEligibleTaskList[0].WhatId;
        t1.Subject= 'sub';
        t1.ActivityDate=Date.Today();
        insert t1;
        t1.Status='In Progress';
        update t1;
	    system.assertEquals(t1.Status , 'In Progress');   
        nonEligibleTaskList.add(t1);
        
        for(Task tskObj: eligibleTaskList){
            List<String> objList = new List<String>{tskObj.Id};
            eligibleItemListListToPass.add(objList);
        }
        for(Task tskObj: nonEligibleTaskList){
            List<String> objList = new List<String>{tskObj.Id};
            nonEligibleItemListListToPass.add(objList);
        }
        system.runAs(testUser)
        {            
            test.startTest();
            CRMRetail_TaskMassDelete_H_HUM.processTaskRecords(eligibleItemListListToPass);
            List<Task> taskList1 = [SELECT Id,CreatedById, RecordType.DeveloperName, Status FROM Task WHERE Status = 'Not Started'];
            system.assertEquals(taskList1.size() , 0);
            
            CRMRetail_TaskMassDelete_H_HUM.processTaskRecords(nonEligibleItemListListToPass);
            List<Task> taskList2 = [SELECT Id,CreatedById, RecordType.DeveloperName, Status FROM Task WHERE Status = 'In Progress'];
            system.assertEquals(taskList2.size() , 2);
            
            nonEligibleItemListListToPass.add(new List<String>{'TestString'});
            CRMRetail_TaskMassDelete_H_HUM.processTaskRecords(nonEligibleItemListListToPass);
            List<Task> taskList3 = [SELECT Id,CreatedById, RecordType.DeveloperName, Status FROM Task WHERE Status = 'In Progress'];
            system.assertEquals(taskList3.size() , 2);
            
            CRMRetail_TaskMassDelete_H_HUM.processTaskRecords(new List<List<String>>());
            List<Task> taskList4 = [SELECT Id,CreatedById, RecordType.DeveloperName, Status FROM Task WHERE Status = 'In Progress'];
            system.assertEquals(taskList4.size() , 2);
            test.stopTest();
        }
    }
}