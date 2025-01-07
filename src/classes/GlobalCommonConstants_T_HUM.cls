/****************************************************************************************************************************************************
Apex Class Name  : GlobalCommonConstants_T_HUM
Version          : 1.0
Created Date     : MAY 20 2021
Function         : Test class for GlobalCommonConstants_HUM
******************************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                           		05/05/2021	         		Original Version REQ- 2184294
*****************************************************************************************************************************************************/
@isTest
private class GlobalCommonConstants_T_HUM
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
            GlobalCommonConstants_HUM oGlobCons = new GlobalCommonConstants_HUM();
            Test.stopTest();
        }
    }
}