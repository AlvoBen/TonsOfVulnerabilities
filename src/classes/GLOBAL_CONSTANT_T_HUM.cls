/****************************************************************************************************************************************************
Apex Class Name  : GLOBAL_CONSTANT_T_HUM
Version          : 1.0
Created Date     : FEB 10 2016
Function         : Test class for GLOBAL_CONSTANT_HUM
******************************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Ranjeeth Nagishetty         29646                    02/10/2016                   Original Version
* Sivaprakash Rajendran                                03/19/2024                   US-0037556 - For for APEX Test Coverage.
*****************************************************************************************************************************************************/
@isTest
private class GLOBAL_CONSTANT_T_HUM
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
            GLOBAL_CONSTANT_HUM oGlobCons = new GLOBAL_CONSTANT_HUM();
            Test.stopTest();
            System.assertNotEquals(null, oGlobCons, 'Object oGlobCons should not be null after instantiation.');
        }
    }
}