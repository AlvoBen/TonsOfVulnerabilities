/**************************************************************************** 
Apex Class Name  : HUMVitalityResponseHelperTest
Version          : 1.0  
Created Date     : June 29 2015 
Description      : This class is used as test class for HUMVitalityResponseHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri    24953                   29/06/2014                    Original Version
***************************************************************************************************/
@isTest
private Class HUMVitalityResponseHelperTest
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
     * Method Name : HUMVitalityResponseHelperTest
     * Description : Test method for HUMVitalityResponseHelper
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testHUMVitalityResponseHelperTest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.assertNotEquals(oUser,null,'​Value not matched');
        system.runAs(oUser)
        {
          Test.StartTest();
            HUMVitalityResponseHelper VitalityServiceRes = new HUMVitalityResponseHelper();
            
            VitalityServiceRes.swebRegistered = '';
            
          Test.StopTest();
        }
    }  
}