/****************************************************************************************
Apex Class Name  : HUMUpdateCaseServiceResponseHelperTest
Version          : 1.0 
Created Date     : September 29 2014
Function         : Test class to HUMUpdateCaseServiceResponseHelper Apex class
Modification Log :
 *  Developer       Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Vishal Verma       20294               12/23/2014              Original Version
 *******************************************************************************************/
@isTest
private class HUMUpdateCaseServiceResponseHelperTest
{

     /*
     * Method Name : testHUMUpdateCaseServiceResponseHelperr
     * Description : Test method for HUMUpdateCaseServiceResponseHelper 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testHUMUpdateCaseServiceResponseHelperr()
    {
        
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assert(u.Id != null);
        system.runAs(u)
        {
            Test.startTest();
                HUMUpdateCaseServiceResponseHelper humUpdateCaserRespHelper = new HUMUpdateCaseServiceResponseHelper();
                humUpdateCaserRespHelper.updateServiceResponse = null;
                system.assert(humUpdateCaserRespHelper.updateServiceResponse == null);
            Test.StopTest();
        }
    }
}