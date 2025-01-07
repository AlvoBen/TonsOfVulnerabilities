/***************************************************************************************************************************************************************************
Apex Class Name  : DEAAProcess_Case_T_HUM
Version          : 1.0
Created Date     : Jan 30, 2020
Function         : This Test class is used to close the status and insert comments for expiration date greater than today's date.
****************************************************************************************************************************************************************************
Modification Log:

 * Developer Name                Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Moshitha Gunasekaran                                     01/30/2020                  Original Version 
**************************************************************************************************************************************************************/
@isTest
private class DEAAProcess_Case_T_HUM {
	
	 @testSetup static void testData()
     {
        User usr = HUMTestDataHelperTest.createTestUser('DEAAProcessBatch','Customer Care Specialist');
        insert usr;
        system.assertEquals(usr.lastname=='test DEAAProcessBatch',true);
        system.runAs(usr)
        { 
            Account oAccount = new Account();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.Name == 'New Case Account');
            
            Classification_Type__c classificationType = new Classification_Type__c(name = 'Enrollment (RSO)', Is_Active__c = true);
             insert classificationType ;
             system.assertNotEquals(classificationType, null , 'classificationType  not created');
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'DEAA Plan Change';
           
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'DEAA Plan Change',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Grace Period Expired';
           
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'Grace Period Expired',true);
            
            List<CTCI_Junction__c> listctciJunction = new List<CTCI_Junction__c>();
             CTCI_Junction__c ctciJunction  =  new CTCI_Junction__c(Classification__c =  classificationRecDec.ID, Classification_Type__c = classificationType.ID, Intent__c = intentRec.ID , IsActive__c = true);
             listctciJunction.Add(ctciJunction);
             insert listctciJunction ;
             system.assertNotEquals(listctciJunction, null ,'CTCIJunctionlist  not created'); 

            Case oCase = new Case(AccountId=oAccount.Id,General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today().addDays(-1), Case_Owner__c='Louisville RSO Enrollment', Owner_Queue__c='Louisville RSO Enrollment', Work_Queue_View_Name__c='Other',Service_Center__c ='Louisville',Department__c='Retail Service Operations (RSO) Enrollment', status='In Progress', Origin='DEAA');
            insert oCase;
            Case oCase1 = new Case(AccountId=oAccount.Id,General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today().addDays(-1), Case_Owner__c='Louisville RSO Enrollment', Owner_Queue__c='Louisville RSO Enrollment', Work_Queue_View_Name__c='Other',Service_Center__c ='Louisville',Department__c='Retail Service Operations (RSO) Enrollment', status='In Progress', Origin='DEAA');
            insert oCase1;
            Case oCase2 = new Case(AccountId=oAccount.Id,General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today().addDays(-1), Case_Owner__c='Louisville RSO Enrollment', Owner_Queue__c='Louisville RSO Enrollment', Work_Queue_View_Name__c='Other',Service_Center__c ='Louisville',Department__c='Retail Service Operations (RSO) Enrollment', status='In Progress', Origin='DEAA');
            insert oCase2;
            Task oTask = new Task(WhatId = oCase2.id, Task_Owner__c = 'Louisville RSO Enrollment', Owner_Queue__c = 'Louisville RSO Enrollment',Work_Queue_View_Name__c = 'Other', Service_Center__c = 'Louisville', Department__c = 'Retail Service Operations (RSO) Enrollment', status = 'In Progress' ,Description = 'Hi Comment' );
            insert oTask;               
            system.assert(oTask.Owner_Queue__c == 'Louisville RSO Enrollment');
            system.assert(oCase.Interacting_With_Type__c == 'Group');
        }
     }
     
     /*
    * This method is used to call DEAAProcess_Case_B_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testOverAll()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test DEAAProcessBatch' LIMIT 1];
       system.assertEquals(oUser.lastname=='test DEAAProcessBatch',true);
         
        System.runAs (oUser) 
        {
            Test.StartTest();
            DEAAProcess_Case_B_HUM deaaProcessBatch = new DEAAProcess_Case_B_HUM();
            deaaProcessBatch.sQuery = 'Select Id, CaseNumber, AccountId, Type, Status,Subtype__c, Origin, Due_Date__c, Complaint_Type__c, Case_Owner__c, Owner_Queue__c, G_A_Rights_Given__c FROM Case where IsClosed = false and Origin = :originDEAA and Due_Date__C < today';
            Database.executeBatch(deaaProcessBatch);
            Test.stopTest(); 
            
        }
    }
    
}