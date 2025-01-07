/****************************************************************************************
Apex Class Name  : HUMGetCaseServiceResponseHelperTest
Version          : 1.0 
Created Date     : September 29 2014
Function         : Test class to HUMGetCaseServiceResponseHelper Apex class
Modification Log :
 *  Developer       Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Vishal Verma      20294                12/23/2014              Original Version
 *******************************************************************************************/
@isTest
private class HUMGetCaseServiceResponseHelperTest
{

     /*
     * Method Name : testHUMGetCaseServiceRequestHelper
     * Description : Test method for HUMGetCaseServiceResponseHelper 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testHHUMGetCaseServiceResponseHelper()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assert(u.Id != null);
        system.runAs(u)
        {
            Test.startTest();
                HUMGetCaseServiceResponseHelper humGetCaserRespHelper = new HUMGetCaseServiceResponseHelper();
                humGetCaserRespHelper.caseCount = 0;
                system.assert(humGetCaserRespHelper.caseCount == 0);
            Test.StopTest();
        }
    }
}