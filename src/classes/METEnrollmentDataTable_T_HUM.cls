/*********************************************************************************************************
Apex Class Name : METEnrollmentDataTable_T_HUM 
Version         : 1.0
Created On      : 11/30/2016
Function        : Test class for METEnrollmentDataTable_C_HUM 

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Rakesh Patni              153362                     11/30/2017                  Original version
* Manoj Chaurasia           202594                     03/29/2018                REQ - 354470 Ability to display TAS combination in CRM
************************************************************************************************************/
@isTest
private with sharing class METEnrollmentDataTable_T_HUM 
{
    /**
    *  This method creates test data for test class
    *  @name setUpTestData
    *  @param NA
    *  @return NA
    *  @throws NA
    *  
    */
    @testSetUp static void setuptestdate()  
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname,'test User1');
        system.runAs(oUser)
        {
            Account acc1 = HUMTestDataHelperTest.getAccount();
            acc1.Name = 'Test Account Cases1';
            acc1.Birthdate__c='01/15/1951';
            acc1.Enterprise_ID__c = '1005447107';
            insert acc1;        
            system.assertEquals(acc1.Name == 'Test Account Cases1',true);            
            String metTaskType = 'Medicare Case';
            Id metRecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(metTaskType).getRecordTypeId();            
            Case oCase = new Case(  AccountId=acc1.Id,
                                    recordTypeId = metRecordTypeId,                                   
                                    General_Case__c = true,
                                    Interacting_With_Type__c = 'Group',
                                    Interacting_With__c=acc1.Id, 
                                    Due_Date__c=Date.Today(), 
                                    Case_Owner__c=UserInfo.getUserName(), 
                                    Owner_Queue__c='Cincinnati CRU1', 
                                    Work_Queue_View_Name__c='Cincinati CRU1',
                                    Service_Center__c ='Cincinnati',
                                    Department__c='CRU1', 
                                    status='In Progress',
                                    Subtype__c='Routine');
            insert oCase;            
            MET_Task__c oTask = new MET_Task__c();
            oTask.Name = 'Test Task';
            oTask.Is_Active__c = true;
            oTask.Case_Subtype__c = 'Routine';
            insert oTask;
            System.assertEquals(oTask.Name,'Test Task');           
            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = 'Test Action';
            oAction.Is_Active__c = true;
            insert oAction;
            System.assertEquals(oAction.Name,'Test Action');
        
            MET_Source__c oSource = new MET_Source__c();
            oSource.Name = 'Test Source';
            oSource.Is_Active__c = true; 
            insert oSource; 
            System.assertEquals(oSource.Name,'Test Source');
            String metTaskRecType = 'MET Task';
            Id taskRecordTypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get(metTaskRecType).getRecordTypeId();            
            Task t = new task();
            t.Priority = 'High';
            t.Status = 'In Proress';
            t.whatid = oCase.id;
            t.MET_Task__c = oTask.id;
            t.MET_Action__c = oAction.id;
            t.MET_Source__c = oSource.id;
            t.recordtypeid = taskRecordTypeId;
            insert t;
        }       
    }
    /**
    *  This method is the testmethod for MET functionality
    *  @name setUpTestData
    *  @param NA
    *  @return NA
    *  @throws NA
    *  
    */
    static testmethod void testMETDataTable()
    {
        User usr = [Select id from User where name = 'test User1' limit 1];
        system.runAs(usr)
        {
            Test.startTest();
            Case caseRec = [Select id,status from case where Subtype__c = 'Routine' LIMIT 1];
            system.assertEquals(caseRec.status,'In Progress');
            METEnrollmentDataTable_C_HUM metObj = new METEnrollmentDataTable_C_HUM();
            METEnrollmentDataTable_C_HUM.getMETRecords(caseRec.id); 
            Test.stopTest();
        }
    }   
}