/*********************************************************************************************************
Apex Class Name : METEnrollmentCaseDetailTable_LT_HUM 
Created On      : 11/2/2022
Function        : Test class for METEnrollmentCaseDetailTable_LC_HUM 

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Pooja Kumbhar                                  		11/2/2022                  Original version
************************************************************************************************************/
@isTest
Private class METEnrollmentCaseDetailTable_LT_HUM {
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
            t.MET_CancelledBydate__c = date.today();
            t.MET_CancelledByUser__c = oUser.id;
            insert t;
            
            Task t1 = new task();
            t1.Priority = 'High';
            t1.Status = 'In Proress';
            t1.whatid = oCase.id;
            t1.MET_Task__c = oTask.id;
            t1.MET_Action__c = oAction.id;
            t1.MET_Source__c = oSource.id;
            t1.recordtypeid = taskRecordTypeId;
            t1.MET_CancelledBydate__c = date.today();
            insert t1;
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
            METEnrollmentCaseDetailTable_LC_HUM metObj = new METEnrollmentCaseDetailTable_LC_HUM();
            METEnrollmentCaseDetailTable_LC_HUM.getMETRecords(caseRec.id); 
            METEnrollmentCaseDetailTable_LC_HUM.getMETRecords(''); 
            Test.stopTest();
        }
    }   
}