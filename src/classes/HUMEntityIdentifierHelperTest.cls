/****************************************************************************************
Apex Class Name  : HUMEntityIdentifierHelperTest
Version          : 1.0 
Created Date     : September 29 2014
Function         : Test class to HUMEntityIdentifierHelper
Modification Log :
 *  Developer       Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Sagar Tapadia      18313              09/29/2014              Original Version 
 *******************************************************************************************/
@isTest
private class HUMEntityIdentifierHelperTest {

     /*
     * Method Name : testHUMEntityIdentifierHelper
     * Description : Test method to test HUMEntityIdentifierHelper
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testHUMEntityIdentifierHelper ()
    {

        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assert(u.Id != null);
        system.runAs(u)
        {
        	Test.startTest();
        		HUMEntityIdentifierHelper humentIdntHelper = new HUMEntityIdentifierHelper();
        		humentIdntHelper.EntityType = '123';
        		system.assert(humentIdntHelper.EntityType != null);
        	Test.StopTest();
        }
    }
}