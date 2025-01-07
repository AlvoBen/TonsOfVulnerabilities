/****************************************************************************************
Apex Class Name  : CaseTransferSuccessMockResponse_T_HUM
Version          : 1.0 
Created Date     : 10-01-2019
Function         : Test class to CaseTransferService_S_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Ajay Chakradhar                                 10/01/2019              Original Version     
***********************************************************************************************************/
@istest
global class CaseTransferSuccessMockResponse_T_HUM implements HttpCalloutMock 
{
    global HTTPResponse respond(HTTPRequest req) 
    {
        // Creating a fake success response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        res.setBody('{"TransferCaseResponse":{"Header":{"Source":{"SourceID":"","SourceDesc":"","Channel":""},"TransactionDetails":{"TransactionTime":"2019-01-21T03:13:58.007396-05:00","TransactionType":"CASE_TRANSFER_REQ"},"ResultSet":{"StatusCode":"1001","StatusDescription":"Success-CaseTransferInProgress"}}}}');
        res.setStatusCode(200);
        return res;
    }
}