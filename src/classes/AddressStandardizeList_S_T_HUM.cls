/*****************************************************************************************************************************************************************************
    @Apex Class Name  : AddressStandardizeList_S_T_HUM
    @Version          : 1.0
    @Created Date     : May 13th, 2020
    @Function         : Test class for AddressStandardizeList_S_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  13/05/2020                    Original Version
* Firoja Begam                                     05/01/2021                    US1755360 - EAS Service side changes - CR1876 (CR1808 Part 2) - SF
******************************************************************************************************************************************************************************/
@isTest
private class AddressStandardizeList_S_T_HUM
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
            objAddressStandardize.Name = 'Enterprise_Address_Service_V2';
            objAddressStandardize.Active__c = True;
            objAddressStandardize.Enable_Service_Tester__c = True;
            objAddressStandardize.Certificate_Name__c = 'Humana_GuidanceCare_neptune';
            objAddressStandardize.Content_Type__c = 'application/json';
            objAddressStandardize.Request_Type__c = 'GET';
            objAddressStandardize.Timeout__c = '120000';
            objAddressStandardize.End_Point_URL__c = 'https://dev-crmservicews.humana.com/EnterpriseAddressService_V1.0';
            objAddressStandardize.Service_Name__c = '/AddressStandardization';
            insert objAddressStandardize;
            system.assertequals('Enterprise_Address_Service_V2',objAddressStandardize.Name);
            system.assertequals('Humana_GuidanceCare_neptune', objAddressStandardize.Certificate_Name__c);

            CRMFunctionality_ONOFF_Switch__c OSBONOFF= new CRMFunctionality_ONOFF_Switch__c();
            OSBONOFF.IsON__c=true;
            OSBONOFF.Name='1755360';
            OSBONOFF.Story_Title__c='PR00093155 - EAS Service side changes';
            Insert OSBONOFF;
            system.assertequals('1755360',OSBONOFF.Name);
            system.assertequals(true, OSBONOFF.IsON__c);
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
          	HttpRequest request = new HttpRequest();
            request = AddressStandardizeList_S_HUM.getAddressStandardizeRequest(sAddressStandardizeRequest);
            System.assertNotEquals(null, request);    
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
            String sAddressStandardizeRequest = '{""}';
          	HttpRequest request = new HttpRequest();
            request = AddressStandardizeList_S_HUM.getAddressStandardizeRequest(sAddressStandardizeRequest);
            System.assertEquals(null, request);
            Test.StopTest();
        }
    } 
}