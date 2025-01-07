/****************************************************************************************
Apex Class Name  : HUMUpdateCaseServiceRequestHelperTest
Version          : 1.0 
Created Date     : September 29 2014
Function         : Test class to HUMUpdateCaseServiceRequestHelper Apex class
Modification Log :
 *  Developer       Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Vishal Verma      20294                12/23/2014              Original Version
 *******************************************************************************************/
@isTest
private class HUMUpdateCaseServiceRequestHelperTest
{

     /*
     * Method Name : testHUMUpdateCaseServiceRequestHelper
     * Description : Test method for HUMUpdateCaseServiceRequestHelper 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testHUMUpdateCaseServiceRequestHelper()
    {
        
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assert(u.Id != null);
        system.runAs(u)
        {
            Test.startTest();
                HUMUpdateCaseServiceRequestHelper humUpdateCaserReqHelper = new HUMUpdateCaseServiceRequestHelper();
                humUpdateCaserReqHelper.CaseUpdateRequest = null;
                system.assert(humUpdateCaserReqHelper.CaseUpdateRequest == null);
            Test.StopTest();
        }
    }
}