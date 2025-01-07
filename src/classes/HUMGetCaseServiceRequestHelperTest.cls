/****************************************************************************************
Apex Class Name  : HUMGetCaseServiceRequestHelperTest
Version          : 1.0 
Created Date     : September 29 2014
Function         : Test class to HUMGetCaseServiceRequestHelper Apex class
Modification Log :
 *  Developer       Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Vishal Verma       20294               12/23/2014              Original Version
 *******************************************************************************************/
@isTest
private class HUMGetCaseServiceRequestHelperTest
{

     /*
     * Method Name : testHUMGetCaseServiceRequestHelper
     * Description : Test method for HUMGetCaseServiceRequestHelper 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testHUMGetCaseServiceRequestHelper()
    {
        
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assert(u.Id != null);
        system.runAs(u)
        {
            Test.startTest();
                HUMGetCaseServiceRequestHelper humGetCaserReqHelper = new HUMGetCaseServiceRequestHelper();
                humGetCaserReqHelper.getCaseInfo = false;
                system.assert(humGetCaserReqHelper.getCaseInfo == false);
            Test.StopTest();
        }
    }
}