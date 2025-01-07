/**************************************************************************** 
Apex Class Name  : ImagingServiceGetClaimURLResponse_T_HUM 
Version          : 1.0  
Created Date     : February 17, 2016
Function         : test class for ImagingServiceGetClaimURLResponse_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             30118             02/17/2016                    Original Version
* Prasanthi Kandula         211792            03/30/2018                    REQ - 301521 - Updated code to incorporate changes done for Imaging Service V2
***************************************************************************************************/
@isTest
private Class ImagingServiceGetClaimURLResponse_T_HUM 
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
     * Method Name : testImagingServiceGetClaimURLResponse
     * Description : Test method for ImagingServiceGetClaimURLResponse_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testImagingServiceGetClaimURLResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
            Test.StartTest();
            ImagingServiceGetClaimURLResponse_H_HUM oResponse = new ImagingServiceGetClaimURLResponse_H_HUM();
            ImagingServiceGetClaimURLResponse_H_HUM.GetClaimURLsResponse oGetClaims = new ImagingServiceGetClaimURLResponse_H_HUM.GetClaimURLsResponse();
            ImagingServiceGetClaimURLResponse_H_HUM.ClaimURLs oClaimURL = new ImagingServiceGetClaimURLResponse_H_HUM.ClaimURLs();
            Test.StopTest(); 
        }
    }  
}