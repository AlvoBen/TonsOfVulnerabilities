/*****************************************************************************************************************************************************************************
    @Apex Class Name  : AddressStandardizeResponse_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : May 14th, 2020
    @Function         : Test class for AddressStandardizeResponse_DTO_T_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  14/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class AddressStandardizeResponse_DTO_T_HUM
{
    /*
    * This method verifies response structure for AddressStandardize
    * @param        None
    * @return       None
    */
    static testMethod void testResponseOSAService(){
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.runAs(oCCSUser)
        { 
            Test.StartTest();
            
            AddressStandardizeResponse_DTO_HUM objAddressStandardize = new AddressStandardizeResponse_DTO_HUM();
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
            objAddressStandardize.StandardizeAddressResponse = objStandardizeAddressResponse;
            objAddressStandardize = AddressStandardizeResponse_DTO_HUM.parse('{ "StandardizeAddressResponse":{ "SuccessFlag":"Y", "AddressLine1":"9022 SHELBYVILLE RD APT 2", "City":"LOUISVILLE", "StateCode":"KY", "ZipCode":"40222", "StateID":"21", "ZipCodePlus":"5106", "CountyName":"JEFFERSON", "CountyID":"111", "Longitude":"-85.583009", "Latitude":"38.2468857", "Valid":"true", "ChangeIndicator":"Change" } }');
            System.assertNotEquals(null, objAddressStandardize);
            /*
            OSAServiceResponse_DTO_HUM objOSAServiceResponse = new OSAServiceResponse_DTO_HUM();
            List<OSAServiceResponse_DTO_HUM.isOSAResponse> lstIsOSAResponse = new List<OSAServiceResponse_DTO_HUM.isOSAResponse>();
            OSAServiceResponse_DTO_HUM.IsOSAResponse objIsOSAResponse = new OSAServiceResponse_DTO_HUM.IsOSAResponse();
            objIsOSAResponse.PlanID = 'S5884-151-000-2020';
            objIsOSAResponse.OSA = 'true';
            lstIsOSAResponse.add(objIsOSAResponse);
            objOSAServiceResponse = OSAServiceResponse_DTO_HUM.parse('{ "isOSAResponse":[ { "PlanID":"S5884-151-000-2020", "OSA":"true" } ] }');
            System.assertNotEquals(null, objOSAServiceResponse);
            */
            Test.stopTest();
        }
    }
}