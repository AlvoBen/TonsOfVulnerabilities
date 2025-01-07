/******************************************************************************************************************************
Apex Class Name  : HUMCaseActionRelatedListControllerTest
Version          : 1.0 
Created Date     : MAY 19 2015
Function         : Test class for class HUMProcessRelatedListController

Modification Log :
 *    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *    Manish Kumar       23321               05/19/2015           Original version.
 *********************************************************************************************************************************/
 
@isTest
private class HUMProcessRelatedListControllerTest
{
    /*
    * Method Name   :    overallTestMethod
    * Description   :    This method is used for overall testing for HUMProcessRelatedListControllerTest
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void overallTestMethod()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            Template__c objTemplate = new Template__c(Description__c='New Test template');
            insert objTemplate;
            System.assertNotEquals(objTemplate.Id, NULL, 'Value not matched');
            
            Account oAccount = new Account(Name='TestAccount1');
            insert oAccount;
            system.assertNotEquals(oAccount.Id, NULL,'Value not matched');
            
            Case oCase = new Case(AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c=UserInfo.getUserName(), Work_Queue_View_Name__c='Cincinati CRU', status='In Progress');
            insert oCase;
            System.assertNotEquals(oCase.Id, NULL, 'Value not matched');
            
            Template_Submission_Owner__c oTSO = new Template_Submission_Owner__c(Object_Owner_ID__c=oCase.Id, Object_Owner_Type__c='Case');
            insert oTSO;
            System.assertNotEquals(oTSO.Id, NULL, 'Value not matched');
            
            Template_Submission__c objTS = new Template_Submission__c(Template__c=objTemplate.Id, Submission_Owner__c=oTSO.Id, Version__c=123);
            insert objTS;
            System.assertNotEquals(objTS.Id, NULL, 'Value not matched');
            
            List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name='HUMExistingProcess',StringValue__c='Existing process'));
            lstConstants.add(new HUMConstants__c(Name='HUMNewProcess',StringValue__c='New process'));
            lstConstants.add(new HUMConstants__c(Name='HUMClosed',StringValue__c='Closed'));
            lstConstants.add(new HUMConstants__c(Name='HUMStatusCancelled',StringValue__c='Cancelled'));
            lstConstants.add(new HUMConstants__c(Name='ZERO',IntegerValue__c=0));
            lstConstants.add(new HUMConstants__c(Name='HUMLastIndex',IntegerValue__c=15));
            lstConstants.add(new HUMConstants__c(Name='HUMConstantOne',IntegerValue__c=1));
            lstConstants.add(new HUMConstants__c(Name='STATUS',StringValue__c='status'));
            insert lstConstants;
            System.assertEquals(lstConstants.size(), 8, 'Value not matched');
            
            ApexPages.StandardController standardController = new ApexPages.StandardController(oCase);
            
            Test.startTest();
                HUMProcessRelatedListController objExten = new HUMProcessRelatedListController(standardController);
                objExten.deleteInvalidSubmission();
            Test.stopTest();
        }
    }
}