/********************************************************************************************************************************************************************************
  Apex Class Name  : METEnrollmentControllerExtension_T_HUM
  Version          : 1.0 
  Created Date     : Aug 28 2020
  Function         : Test Class for METEnrollmentControllerExtension
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Mohammed Noor                                         08/28/2020                 Original Version
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
@isTest
private class METEnrollmentControllerExtension_T_HUM 
{
    /*
    * Method Name : dataSetup
    * Description : Create common test Data
    * Return type : void
    * Paramater   : NA
    */
    
    @testSetup static void prepareTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('METUser','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test METUser');
        
        Case caseRecord = New Case();
        caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord.Status = 'In Progress';        
        caseRecord.ownerId = oUser.Id;        
        caseRecord.Origin = 'Inbound Call';
        insert caseRecord;
        
        MET_Task__c oMETTask = new MET_Task__c();
        oMETTask.Name = 'Test Task';
        oMETTask.Is_Active__c = true;
        oMETTask.Case_Subtype__c = 'Routine';
        insert oMETTask;
        System.assertEquals(oMETTask.Name,'Test Task');
        
        MET_Action__c oAction = new MET_Action__c();
        oAction.Name = 'Test Action';
        oAction.Is_Active__c = true;
        insert oAction;
        System.assertEquals(oAction.Name,'Test Action');

        MET_Source__c oSource = new MET_Source__c();
        oSource.Name = 'Test Source';
        oSource.Is_Active__c = true; 
        oSource.Launch_EMME__c = true;
        insert oSource; 
        System.assertEquals(oSource.Name,'Test Source');
        
        Task oTask = new Task();
        oTask.recordtypeID = Schema.SObjectType.Task.getRecordTypeInfosByName().get('MET Task').getRecordTypeId();
        oTask.whatId = caseRecord.Id;
        oTask.Type = GLOBAL_CONSTANT_HUM.METTASKTYPE;
        oTask.MET_Task__c = oMETTask.Id;
        oTask.MET_Action__c = oAction.Id;
        oTask.MET_Source__c = oSource.Id;
        oTask.status = 'Completed';
        oTask.ownerId = oUser.Id;
        insert oTask;        
    }
    
    /*
    * Method Name : testMETEnrollmentControllerExtension
    * Description : Method to test METEnrollmentControllerExtension methods
    * Return type : void
    * Paramater   : NA
    */   
    testMethod static void testMETEnrollmentControllerExtension()
    {
       User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test METUser'];
        System.assertEquals('test METUser',oUser.LastName);
        Case oCase = [Select Id from Case Limit 1];
        system.runAs(oUser)
        {
            Test.startTest();
            PageReference oMETPage = Page.METEnrollment_VF_HUM;
            Test.setCurrentPage(oMETPage);
            ApexPages.StandardController oStdCon = new ApexPages.StandardController(oCase);
            METEnrollmentControllerExtension oExtn = new METEnrollmentControllerExtension(oStdCon);
            oExtn.fetchMetTaskList();
            oExtn.jsonMetTask = 'testStr';
            system.assertEquals(String.isNotBlank(oExtn.jsonMetTaskList),true);
            
            //Test the catch blocks
            METEnrollmentControllerExtension oExtn1 = new METEnrollmentControllerExtension(null);
            oExtn1.fetchMetTaskList();
            system.assertEquals(String.isBlank(oExtn1.jsonMetTaskList),true);
            Test.stopTest();
        }
    }
}