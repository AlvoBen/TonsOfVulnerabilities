/****************************************************************************************
Apex Class Name  : HUMGetCaseInfoResponseHelperTest
Version          : 1.0 
Created Date     : September 29 2014
Function         : Test class to HUMGetCaseInfoResponseHelper Apex class
Modification Log :
 *  Developer       Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Sagar Tapadia      18313               09/29/2014              Original Version
 *******************************************************************************************/
@isTest
private class HUMGetCaseInfoResponseHelperTest {

     /*
     * Method Name : testHUMGetCaseInfoResponseHelper
     * Description : Test method for HUMGetCaseInfoResponseHelper 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testHUMGetCaseInfoResponseHelper()
    {
        
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assert(u.Id != null);
        system.runAs(u)
        {
        	Test.startTest();
        		HUMGetCaseInfoResponseHelper humGetCaserRespHelper = new HUMGetCaseInfoResponseHelper();
        		humGetCaserRespHelper.AccountName = 'Test';
        		system.assert(humGetCaserRespHelper.AccountName != null);
        	Test.StopTest();
        }
    }
}