/****************************************************************************************************************************************************
Apex Class Name  : CaseEditConstants_T_HUM
Version          : 1.0
Created Date     : AUG 16 2021
Function         : Test class for CaseEditConstants_HUM
******************************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Gowthami Thota                                  08/16/2021                  Original Version
*****************************************************************************************************************************************************/
@isTest
private class CaseEditConstants_T_HUM
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Paramater   : None
    */
    @testSetup static void setUpTestData()
    {
       User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.lastname=='test User1',true);
    }
   
    /*
    * Method Name : testConstantsInstantiation
    * Description : Test method to instantiate constants
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testConstantsInstantiation()
    {
        User u = [select id, name from user where lastname='test User1' limit 1];
        System.runAs(u)
        {
            Test.startTest();
            CaseEditConstants_HUM oCaseEditConstants = new CaseEditConstants_HUM();
            CaseEditConstants_HUM.MEDICARE_LOB_PRODUCTTYPE_LIST();
            Test.stopTest();
        }
    }
}