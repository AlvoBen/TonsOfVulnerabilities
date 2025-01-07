/****************************************************************************************************************************************************
Apex Class Name  : CaseManagement_Constants_T_HUM
Version          : 1.0
Created Date     : MAY 28, 2021
Function         : Test class for CaseManagement_Constants_HUM
******************************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Pooja Kumbhar                                     06/07/2021                   User Story 2270747: T1PRJ0002606/PR00094268 - MF1 - Architectural Remediation - Case Management Global Constant Class Changes - Case History 
*****************************************************************************************************************************************************/
@isTest
private class CaseManagement_Constants_T_HUM
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Paramater   : None
    */
    @testSetup static void setUpTestData()
    {
       User u = HUMTestDataHelperTest.createTestUser('ConstantUser1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.lastname=='test ConstantUser1',true);
    }
   
    /*
    * Method Name : testCaseManagementConstantsInstantiation
    * Description : Test method to instantiate constants
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testCaseManagementConstantsInstantiation()
    {
        User u = [select id, name from user where lastname='test ConstantUser1' limit 1];
        System.runAs(u)
        {
            Test.startTest();
            CaseManagement_Constants_HUM oGlobCons = new CaseManagement_Constants_HUM();
            Test.stopTest();
        }
    }
}