/**************************************************************************** 
Apex Class Name  : CaseProcessRedirect_T_HUM
Version          : 1.0  
Created Date     : 09/01/2020
Function         : Test class for CaseProcessRedirect_C_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* AshokKumar Nutalapati                         10/14/2020                 Original Version
***************************************************************************************************/

@isTest
private class CaseProcessRedirect_T_HUM
{
    
    /*
    * Method Name : dataSetup
    * Description : Create common test Data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        Account oAccount = new Account(Name='TestAccount1');
        insert oAccount;
        system.assertNotEquals(oAccount.Id, NULL,'Value not matched');          
        Case oCase = new Case(AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c=UserInfo.getUserName(), Work_Queue_View_Name__c='Cincinati CRU', status='In Progress');
        insert oCase;
    }
    
    /*
    * Method Name : createRecords
    * Description : Method to assign values to CaseProcessRedirect_T_HUM
    * Return type : void
    * Paramater   : NA
    */   
    testMethod static void createRecords()
    {
        User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('test User1',oUser.LastName);
        List<Case> lstCases = [Select id, CaseNumber from case limit 1];
        system.runAs(oUser)
        {
        
            Test.startTest();
            ApexPages.currentPage().getParameters().put('CaseId', lstCases[0].id);
            ApexPages.currentPage().getParameters().put('TicketRequired', 'true');
            ApexPages.currentPage().getParameters().put('CaseComment', 'Test');
            CaseProcessRedirect_C_HUM oProcess =new CaseProcessRedirect_C_HUM();
            oProcess.CaseNumber = lstCases[0].CaseNumber;
            Test.stopTest();
        }
    }
}