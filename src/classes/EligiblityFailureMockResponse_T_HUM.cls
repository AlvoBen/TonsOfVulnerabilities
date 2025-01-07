/****************************************************************************************
Apex Class Name  : EligiblityFailureMockResponse_T_HUM
Version          : 1.0 
Created Date     : 25-06-2020
Function         : Test class to EligiblityRequest_D_HUM Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Riddhi Mittal                                 25/06/2020              Original Version     
***********************************************************************************************************/
@istest
global class EligiblityFailureMockResponse_T_HUM implements HttpCalloutMock {

     global HTTPResponse respond(HTTPRequest req) 
    {
        // Create a fake failure response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        res.setBody('{"Fault":{"faultcode":"Code-127.1.1","detail":{"transactionInfo":{"transaction-id":"1655241399","transaction-client":"10.232.9.94","URL-in":"http://127.0.0.1:8316/OSAServiceREST/isOSA","processor-name":"JSONServicesGateway","processor-type":"MultiprotocolGateway"},"errorInfo":{"error-code":"0x00d30003","error-message":"InternalServerError","error-subcode":"0x00d30003"}}}}');
        res.setStatusCode(200);
        return res;
    }
}