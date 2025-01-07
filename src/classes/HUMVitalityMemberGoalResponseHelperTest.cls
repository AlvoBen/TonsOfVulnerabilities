/**************************************************************************** 
Apex Class Name  : HUMVitalityMemberGoalResponseHelperTest
Version          : 1.0  
Created Date     : July 30 2015 
Description      : This class is used as test class for HUMVitalityMemberGoalResponseHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Gargey Sharma			    25149                  30/07/2014                    Original Version
***************************************************************************************************/
@isTest
private Class HUMVitalityMemberGoalResponseHelperTest
{

    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
    }
    
    /*
     * Method Name : testHUMVitalityMemberGoalResponseHelperTest
     * Description : Test method for HUMVitalityMemberGoalResponseHelper
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testHUMVitalityMemberGoalResponseHelperTest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.assertNotEquals(oUser,null,'​Value not matched');
        system.runAs(oUser)
        {
          Test.StartTest();
            HUMVitalityMemberGoalResponseHelper VitalityServiceMemGoalRes = new HUMVitalityMemberGoalResponseHelper();
            VitalityServiceMemGoalRes.sGoalId = 'QTS3';
            system.assert(VitalityServiceMemGoalRes.sGoalId!= null);
          Test.StopTest();
        }
    }  
}