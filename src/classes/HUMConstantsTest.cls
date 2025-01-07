/****************************************************************************************************************************************************
Apex Class Name  : HUMConstantsTest
Version          : 1.0
Created Date     : Aug 4 2015
Function         : Test class for HUMConstants
******************************************************************************************************************************************************

Modification Log: 

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Shreya Choodamani         25261                  08/04/2015                   Original Version
* Avinash Choubey           29247                  01/25/2016                   Adding Test Setup method
*****************************************************************************************************************************************************/
@isTest
private class HUMConstantsTest
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
            HUMConstants humCons = new HUMConstants();
            Test.stopTest();
        }
    }
}