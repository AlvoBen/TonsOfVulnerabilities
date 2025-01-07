/**************************************************************************** 
Apex Class Name  : MTVRemarksServiceGetClaimsRequest_LT_HUM
Version          : 1.0  
Created Date     : 06/01/2022
Function         : test class for MTVRemarksServiceGetClaimsRequest_LH_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
***************************************************************************************************/
@isTest
private Class MTVRemarksServiceGetClaimsRequest_LT_HUM 
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
     * Method Name : testMTVRemarksServiceGetClaimsRequest
     * Description : Test method for MTVRemarksServiceGetClaimsRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testMTVRemarksServiceGetClaimsRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            MTVRemarksServiceGetClaimsRequest_LH_HUM oRequest = new MTVRemarksServiceGetClaimsRequest_LH_HUM();
            system.assert(oRequest!=null);
          Test.StopTest();
        }
    }  
    
}