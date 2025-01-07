/****************************************************************************************************************************************************
Apex Class Name  : HumConstants_T_LWC
Version          : 1.0
Created Date     : FEB 15 2022
Function         : Test class for HumConstants_LWC
******************************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Vijaya Lakshmi Tummala                             2/15/2022                   Original Version
*****************************************************************************************************************************************************/
@isTest
private class HumConstants_T_LWC
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
            HumConstants_LWC  oGlobCons = new HumConstants_LWC ();
            HumConstants_LWC.MEDICARE_LOB_PRODUCTTYPE_LIST();
            Test.stopTest();
        }
    }
}