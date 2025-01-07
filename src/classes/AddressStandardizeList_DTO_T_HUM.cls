/*****************************************************************************************************************************************************************************
    @Apex Class Name  : AddressStandardizeList_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : May 14th, 2020
    @Function         : Test class for AddressStandardizeList_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  14/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class AddressStandardizeList_DTO_T_HUM
{
    /*
    * This method verifies DTO structure for AddressStandardize
    * @param        None
    * @return       None
    */
    static testMethod void testDTOAddressStandardize(){
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.runAs(oCCSUser)
        { 
            Test.StartTest();
            
            AddressStandardizeList_DTO_HUM objAddressStandardizeList = new AddressStandardizeList_DTO_HUM();
                objAddressStandardizeList.calloutErrored = true;
            	objAddressStandardizeList.serviceCalloutError = 'Error';
            
            AddressStandardizeResponse_DTO_HUM objStandardizeAddress = new AddressStandardizeResponse_DTO_HUM();
            AddressStandardizeResponse_DTO_HUM.StandardizeAddressResponse objStandardizeAddressResponse = new
            	AddressStandardizeResponse_DTO_HUM.StandardizeAddressResponse();    
            objStandardizeAddressResponse.AddressLine1 = '9022 SHELBYVILLE RD APT 2';
            objStandardizeAddressResponse.ChangeIndicator = 'Change';
            objStandardizeAddressResponse.City = 'LOUISVILLE';
            objStandardizeAddressResponse.CountyID = '111';
            objStandardizeAddressResponse.CountyName = 'JEFFERSON';
            objStandardizeAddressResponse.Deliverable = 'test';
            objStandardizeAddressResponse.Latitude = '38.2468857';
            objStandardizeAddressResponse.Longitude = '-85.583009'; 
            objStandardizeAddressResponse.ReturnCode = 'test';
            objStandardizeAddressResponse.StateCode = 'KY';
            objStandardizeAddressResponse.StateID = '21';
            objStandardizeAddressResponse.SuccessFlag = 'Y';
            objStandardizeAddressResponse.Valid = 'true';
            objStandardizeAddressResponse.ZipCode = '40222';
            objStandardizeAddressResponse.ZipCodePlus = '5106';
            objStandardizeAddress.StandardizeAddressResponse = objStandardizeAddressResponse;
            objAddressStandardizeList.addressStandardizedResponse = objStandardizeAddress;
            String jsonString = '{"Fault":{"faultcode":"Code-127.1.1","faultstring":"testString","detail":{"transactionInfo":{"transaction-id":"1655241399","transaction-client":"10.232.9.94","URL-in":"http://127.0.0.1:8316/OSAServiceREST/isOSA","processor-name":"JSONServicesGateway","processor-type":"MultiprotocolGateway"},"errorInfo":{"error-code":"0x00d30003","error-message":"InternalServerError","error-subcode":"0x00d30003","error_headers":"0x00d30003","error_protocol_reason_phrase":"0x00d3000","error_protocol_response":"0x00d3000"}}}}';
            objAddressStandardizeList.faultResponseDTO = FaultCode_DTO_HUM.parse(jsonString);
            System.assertNotEquals(null, objAddressStandardizeList);
            
            Test.stopTest();    
        }
    }
}