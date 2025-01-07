/****************************************************************************************
Apex Class Name  : HUMGetCaseInfoRequestHelperTest
Version          : 1.0 
Created Date     : September 29 2014
Function         : Test class to HUMGetCaseInfoRequestHelper Apex class
Modification Log :
 *  Developer       Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Sagar Tapadia     18313                09/29/2014              Original Version      
 *******************************************************************************************/
@isTest
private class HUMGetCaseInfoRequestHelperTest {

     /*
     * Method Name : testHUMGetCaseInfoRequestHelper
     * Description : Test method for HUMGetCaseInfoRequestHelper
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testHUMGetCaseInfoRequestHelper() {
        
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assert(u.Id != null);
        system.runAs(u)
        {
        	Test.StartTest();
        		HUMGetCaseInfoRequestHelper humGestCaseReqHelper = new HUMGetCaseInfoRequestHelper();
        		humGestCaseReqHelper.InteractionId = '1234';
        		system.assert(humGestCaseReqHelper.InteractionId != null);
        	TEst.StopTest();
        }
    }
    
    
}