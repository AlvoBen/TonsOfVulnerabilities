/**************************************************************************** 
Apex Class Name  : ImagingServiceGetClaimURLRequest_T_HUM 
Version          : 1.0  
Created Date     : February 17, 2016
Function         : test class for ImagingServiceGetClaimURLRequest_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        30118             02/17/2016                    Original Version
***************************************************************************************************/
@isTest
private Class ImagingServiceGetClaimURLRequest_T_HUM 
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
     * Method Name : testImagingServiceGetClaimURLRequest
     * Description : Test method for ImagingServiceGetClaimURLRequest_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testImagingServiceGetClaimURLRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            ImagingServiceGetClaimURLRequest_H_HUM oRequest = new ImagingServiceGetClaimURLRequest_H_HUM();
          	system.assert(oRequest.ClaimNumber != null);
          Test.StopTest(); 
        }
    }  
}