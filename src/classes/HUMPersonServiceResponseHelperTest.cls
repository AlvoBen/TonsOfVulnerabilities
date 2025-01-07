/***************************************************************************************************************************************************************************
Apex Class Name  : HUMPersonServiceResponseHelperTest
Version          : 1.0
Created Date     : September 29 2014
Function         : Test Class for HUMPersonServiceResponseHelper
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name        Code Review                Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* krishna Tadikonda                         09/29/2014                   Original Version
****************************************************************************************************************************************************************************/

@isTest
private class HUMPersonServiceResponseHelperTest
{
    /*
    * Method name : testHUMPersonServiceResponseHelper
    * Description : This method is used to test HUMPersonServiceResponseHelper.
    * Return Type : Void
    * Parameter   : NA 
    */
    
    private static testMethod void testHUMPersonServiceResponseHelper()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
        	Test.startTest();
        		HUMPersonServiceResponseHelper hUMPersonServiceRespHelper = new HUMPersonServiceResponseHelper();
        		hUMPersonServiceRespHelper.Member = 'Member';
        		System.assert(hUMPersonServiceRespHelper.Member != null);
        	Test.stopTest();
        }
    }
}