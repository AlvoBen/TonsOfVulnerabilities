/*****************************************************************************************************************************************************************************
    @Apex Class Name  : AddressStandardizeRequest_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : May 13th, 2020
    @Function         : Test class for AddressStandardizeRequest_DTO_T_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  13/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class AddressStandardizeRequest_DTO_T_HUM
{
    /*
    * This method verifies request structure for OSA Service
    * @param        None
    * @return       None
    */
    static testMethod void testRequestOSAService(){
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.runAs(oCCSUser)
        { 
            Test.StartTest();
            
            AddressStandardizeRequest_DTO_HUM objAddressStandardize = new AddressStandardizeRequest_DTO_HUM();
            AddressStandardizeRequest_DTO_HUM.StandardizeAddressRequest objStandardizeAddressRequest = new AddressStandardizeRequest_DTO_HUM.StandardizeAddressRequest();
            objStandardizeAddressRequest.AddressLine1 ='9022 Shelbyville Road';
            objStandardizeAddressRequest.AddressLine2 = 'Apt 2';
            objStandardizeAddressRequest.City ='Louisville';
            objStandardizeAddressRequest.IncludeDPV = true;
            objStandardizeAddressRequest.IncludeEWS = true;
            objStandardizeAddressRequest.IncludeGeoCode = true;
            objStandardizeAddressRequest.IncludeInputOnError = true;
            objStandardizeAddressRequest.IncludeValidationDetails = true ;
            objStandardizeAddressRequest.StateCode = 'KY';
            objStandardizeAddressRequest.ZipCode = '40221';
            objAddressStandardize.StandardizeAddressRequest = objStandardizeAddressRequest;
            objAddressStandardize = AddressStandardizeRequest_DTO_HUM.parse('{"StandardizeAddressRequest":{"ZipCode":"40221","StateCode":"KY","IncludeValidationDetails":false,"IncludeInputOnError":false,"IncludeGeoCode":true,"IncludeEWS":false,"IncludeDPV":false,"City":"Louisville","AddressLine2":"Apt 2","AddressLine1":"9022 Shelbyville Road"}}');            
            System.assertNotEquals(null, objAddressStandardize);
          	
            Test.stopTest();
        }
    }
}