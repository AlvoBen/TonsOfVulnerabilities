/**
 * @description       : 
 * @author            : Chetan Sindhe
 * @group             : 
 * @last modified on  : 11-09-2020
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   11-05-2020   Chetan Sindhe                         Initial Version
**/
@istest
public with sharing class SearchMemberFailureMockResponse_T_HUM implements HttpCalloutMock {
    
    public HTTPResponse respond(HTTPRequest req) 
    {
        // Create a fake failure response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        res.setBody('{"Fault":{"faultcode":"Code-127.1.1","detail":{"transactionInfo":{"transaction-id":"1655241399","transaction-client":"10.232.9.94","URL-in":"http://127.0.0.1:8316/OSAServiceREST/isOSA","processor-name":"JSONServicesGateway","processor-type":"MultiprotocolGateway"},"errorInfo":{"error-code":"0x00d30003","error-message":"InternalServerError","error-subcode":"0x00d30003"}}}}');
        res.setStatusCode(200);
        return res;
    }

}