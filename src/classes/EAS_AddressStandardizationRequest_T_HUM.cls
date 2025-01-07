/**************************************************************************** 
Apex Class Name  : EAS_AddressStandardizationRequest_T_HUM 
Version          : 1.0  
Created Date     : November 15, 2016
Function         : test class for EAS_AddressStandardizationRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Adil Mohammed           27449                11/15/2016                  Original Version
***************************************************************************************************/
@isTest
private Class EAS_AddressStandardizationRequest_T_HUM
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
     * Method Name : testAddressStandardizationRequest
     * Description : Test method for EAS_AddressStandardizationRequest_T_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testAddressStandardizationRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Test.setMock(HttpCalloutMock.class, new EASService_MockResponse_T_HUM());               
        system.runAs(oUser)
        {
          Test.StartTest();
      EAS_AddressStandardizationRequest_H_HUM oRequest = new EAS_AddressStandardizationRequest_H_HUM();
      EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM oAddressRequest = new EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM ();
      oAddressRequest.AddressLine1 = '8900 shelbyville road';  
      oAddressRequest.AddressLine2 = 'apt 5';  
      oAddressRequest.City = 'louisville';
      oAddressRequest.StateCode = 'ky';
      oAddressRequest.ZipCode = '41105';
      oRequest.StandardizeAddressRequest = oAddressRequest;
          Test.StopTest();
        }
    }  
}