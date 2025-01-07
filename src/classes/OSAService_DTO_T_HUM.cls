/*****************************************************************************************************************************************************************************
    @Apex Class Name  : OSAService_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : May 14th, 2020
    @Function         : Test class for OSAService_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  14/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class OSAService_DTO_T_HUM
{
    /*
    * This method verifies DTO structure for OSA Service
    * @param        None
    * @return       None
    */
    static testMethod void testRequestOSAService(){
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.runAs(oCCSUser)
        { 
            Test.StartTest();
            
            OSAService_DTO_HUM objOSAService = new OSAService_DTO_HUM();
            objOSAService.calloutErrored = true;
            objOSAService.serviceCalloutError = 'Error';
            OSAServiceResponse_DTO_HUM objOSAServiceResponse = new OSAServiceResponse_DTO_HUM();
            List<OSAServiceResponse_DTO_HUM.isOSAResponse> lstIsOSAResponse = new List<OSAServiceResponse_DTO_HUM.isOSAResponse>();
            OSAServiceResponse_DTO_HUM.IsOSAResponse objIsOSAResponse = new OSAServiceResponse_DTO_HUM.IsOSAResponse();
            objIsOSAResponse.PlanID = 'S5884-151-000-2020';
            objIsOSAResponse.OSA = 'true';
            lstIsOSAResponse.add(objIsOSAResponse);
            objOSAServiceResponse.isOSAResponse = lstIsOSAResponse;
            objOSAService.osaServiceResponse = objOSAServiceResponse;
            String jsonString = '{"Fault":{"faultcode":"Code-127.1.1","faultstring":"testString","detail":{"transactionInfo":{"transaction-id":"1655241399","transaction-client":"10.232.9.94","URL-in":"http://127.0.0.1:8316/OSAServiceREST/isOSA","processor-name":"JSONServicesGateway","processor-type":"MultiprotocolGateway"},"errorInfo":{"error-code":"0x00d30003","error-message":"InternalServerError","error-subcode":"0x00d30003","error_headers":"0x00d30003","error_protocol_reason_phrase":"0x00d3000","error_protocol_response":"0x00d3000"}}}}';
            objOSAService.faultResponseDTO = FaultCode_DTO_HUM.parse(jsonString);
            System.assertNotEquals(null, objOSAService);
            
            Test.stopTest();    
        }
    }
}