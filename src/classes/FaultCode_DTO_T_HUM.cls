/*****************************************************************************************************************************************************************************
    @Apex Class Name  : FaultCode_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : May 12th, 2020
    @Function         : Test class for FaultCode_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  12/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class FaultCode_DTO_T_HUM {
	/*
    * Method to test FaultCode_DTO_HUM
    * @param        None
    * @return       None
    */
    private static testmethod void testFaultCode()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.runAs(oCCSUser)
        { 
            Test.StartTest();
            
            String jsonString = '{"Fault":{"faultcode":"Code-127.1.1","faultstring":"testString","detail":{"transactionInfo":{"transaction-id":"1655241399","transaction-client":"10.232.9.94","URL-in":"http://127.0.0.1:8316/OSAServiceREST/isOSA","processor-name":"JSONServicesGateway","processor-type":"MultiprotocolGateway"},"errorInfo":{"error-code":"0x00d30003","error-message":"InternalServerError","error-subcode":"0x00d30003","error_headers":"0x00d30003","error_protocol_reason_phrase":"0x00d3000","error_protocol_response":"0x00d3000"}}}}';
            HttpResponse res = new HttpResponse();
        	res.setBody('{"Fault":{"faultcode":"Code-127.1.1","faultstring":"testString","detail":{"transactionInfo":{"transaction-id":"1655241399","transaction-client":"10.232.9.94","URL-in":"http://127.0.0.1:8316/OSAServiceREST/isOSA","processor-name":"JSONServicesGateway","processor-type":"MultiprotocolGateway"},"errorInfo":{"error-code":"0x00d30003","error-message":"InternalServerError","error-subcode":"0x00d30003","error_headers":"0x00d30003","error_protocol_reason_phrase":"0x00d3000","error_protocol_response":"0x00d3000"}}}}');
        	JSONParser parser = JSON.createParser(res.getBody());
            FaultCode_DTO_HUM.consumeObject(parser);
            FaultCode_DTO_HUM.parse(jsonString);
            System.assertNotEquals(null, parser);
            
            Test.StopTest();
        }
    }
}