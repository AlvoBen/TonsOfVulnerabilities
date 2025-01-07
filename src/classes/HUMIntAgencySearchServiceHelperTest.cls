/**************************************************************************** 
Apex Class Name  : HUMIntAgencySearchServiceHelperTest 
Version          : 1.0  
Created Date     : October 20, 2014 
Description      : This class is used as test class for HUMIntAgencySearchServiceHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shruthi Karanth                                  10/20/2014                     Original Version
***************************************************************************************************/

@isTest
private Class HUMIntAgencySearchServiceHelperTest 
{
    static testMethod void testHUMIntAgencySearchServiceHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUser1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        System.runas(user) 
        {
            Test.StartTest();
            	HUMIntAgencySearchServiceHelper intAEHistory = new HUMIntAgencySearchServiceHelper();
            	HUMIntAgencySearchServiceHelper.ArrayOfstring serviceTrck = new HUMIntAgencySearchServiceHelper.ArrayOfstring();
            	system.assert(serviceTrck.string_x ==  null);
            Test.stopTest();
        }
    }
}