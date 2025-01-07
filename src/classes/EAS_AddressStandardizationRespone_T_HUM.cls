/**************************************************************************** 
Apex Class Name  : EAS_AddressStandardizationRespone_T_HUM 
Version          : 1.0  
Created Date     : November 15, 2016
Function         : test class for EAS_AddressStandardizationRespone_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Adil Mohammed                         	  11/15/2016                   Original Version
***************************************************************************************************/
@isTest
private Class EAS_AddressStandardizationRespone_T_HUM
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
     * Method Name : testAddressStandardizationResponse
     * Description : Test method for EAS_AddressStandardizationRespone_T_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testAddressStandardizationResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Test.setMock(HttpCalloutMock.class, new EASService_MockResponse_T_HUM());               
        system.runAs(oUser)
        {
          Test.StartTest();
            EAS_AddressStandardizationRespone_H_HUM oAddressResponse = new EAS_AddressStandardizationRespone_H_HUM();
            EAS_AddressStandardizationRespone_H_HUM.EAS_AddressStandardResponse_H_HUM oAddressStandard = new EAS_AddressStandardizationRespone_H_HUM.EAS_AddressStandardResponse_H_HUM();
          Test.StopTest();
        }
    }  
}