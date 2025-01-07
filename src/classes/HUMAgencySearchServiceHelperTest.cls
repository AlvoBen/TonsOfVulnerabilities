/**************************************************************************** 
Apex Class Name  : HUMAgencySearchServiceHelperTest 
Version          : 2.0  
Created Date     : August 5, 2015 
Description      : This class is used as test class for HUMAgencySearchServiceArray
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shreya Choodamani                                  08/05/2014                     Original Version
***************************************************************************************************/

@isTest
private Class HUMAgencySearchServiceHelperTest 
{
    static testMethod void testHUMIntAgencySearchServiceHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUser1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        System.runas(user) 
        {
            Test.StartTest();
                HUMAgencySearchServiceHelper intAEHistory = new HUMAgencySearchServiceHelper();
                HUMAgencySearchServiceHelper.ArrayOfstring serviceTrack = new HUMAgencySearchServiceHelper.ArrayOfstring();
                system.assert(serviceTrack.string_x ==  null);
            Test.stopTest();
        }
    }
}