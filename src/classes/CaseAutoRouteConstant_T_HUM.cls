/****************************************************************************************************************************************************
Apex Class Name  : CaseAutoRouteConstant_T_HUM
Version          : 1.0
Created Date     : MAR 17 2021
Function         : Test class for CaseAutoRouteConstant_C_HUM
******************************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Kavya Bondada                                  03/17/2021                  Original Version
*****************************************************************************************************************************************************/
@isTest
private class CaseAutoRouteConstant_T_HUM
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
            CaseAutoRouteConstant_C_HUM oCaseAutoRouteConstants = new CaseAutoRouteConstant_C_HUM();
            Test.stopTest();
        }
    }
}