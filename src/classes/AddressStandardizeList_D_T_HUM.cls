/*****************************************************************************************************************************************************************************
    @Apex Class Name  : AddressStandardizeList_D_T_HUM
    @Version          : 1.0
    @Created Date     : May 12th, 2020
    @Function         : Test class for AddressStandardizeList_D_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  12/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class AddressStandardizeList_D_T_HUM
{
    /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
  @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs(oCCSUser) 
        {
            HUM_Webservice_Callout__c objAddressStandardize = new HUM_Webservice_Callout__c();
            objAddressStandardize.Name = 'Enterprise_Address_Service';
            objAddressStandardize.Active__c = True;
            objAddressStandardize.Enable_Service_Tester__c = True;
            objAddressStandardize.Certificate_Name__c = 'Humana_GuidanceCare_neptune';
            objAddressStandardize.Content_Type__c = 'application/json';
            objAddressStandardize.Request_Type__c = 'GET';
            objAddressStandardize.Timeout__c = '120000';
            objAddressStandardize.End_Point_URL__c = 'https://dev-crmservicews.humana.com/EnterpriseAddressService_V1.0/AddressStandardization';
            insert objAddressStandardize;
            system.assertequals('Enterprise_Address_Service',objAddressStandardize.Name);
        }
    }
    
    /*
    * Method to test success response for AddressStandardize
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessAddressStandardize()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            String sAddressStandardizeRequest = '{"StandardizeAddressRequest":{"ZipCode":"40221","StateCode":"KY","IncludeValidationDetails":false,"IncludeInputOnError":false,"IncludeGeoCode":true,"IncludeEWS":false,"IncludeDPV":false,"City":"Louisville","AddressLine2":"Apt 2","AddressLine1":"9022 Shelbyville Road"}}';
          	Test.setMock(HttpCalloutMock.class, new AddressStandSuccessMockResponse_T_HUM());
            String result = AddressStandardizeList_D_HUM.retrieveAddrStdzResult(sAddressStandardizeRequest);
            System.assertNotEquals(null, result);
            Test.StopTest();
        }
    }
    
    /*
    * Method to test failure response for AddressStandardize
    * @param        None
    * @return       None
    */
    private static testmethod void testFailureAddressStandardize()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest(); 
            String sAddressStandardizeRequest = '{"StandardizeAddressRequest":{"ZipCode":""}}';
          	Test.setMock(HttpCalloutMock.class, new AddressStandFailureMockResponse_T_HUM());
            String result = AddressStandardizeList_D_HUM.retrieveAddrStdzResult(sAddressStandardizeRequest);
            System.assertNotEquals(null, result);    
            Test.StopTest();
        }
    }
    /*
    * Method to test exception for AddressStandardize
    * @param        None
    * @return       None
    */
    private static testmethod void testExceptionAddressStandardize()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest(); 
            String sAddressStandardizeRequest = '{"StandardizeAddressRequest":""}';
            String result = AddressStandardizeList_D_HUM.retrieveAddrStdzResult(sAddressStandardizeRequest);
            System.assertNotEquals(null, result); 
            Test.StopTest();
        }
    }
}