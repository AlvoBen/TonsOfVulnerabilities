/*******************************************************************************************************************************
Apex Class Name : EhubDocumentInsertResponseGenerator 
Version         : 1.0
Created On      : 10/27/2018     
Function        : Mock Webservice Class

  Modification Log: 
 * Developer Name              Code Review                    Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Srinivas Damera                                            10/27/2018                 Original Version    
 *******************************************************************************************************************************/
@isTest
global class EhubDocumentInsertResponseGenerator implements HttpCalloutMock 
{
    global HTTPResponse respond(HTTPRequest req) 
    {
        // Create a fake response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        res.setBody('{ "AddDocument":{ "AddDocumentResponse":{ "Action":"http://test.com", "AddDocumentResultDTO":{ "Status":"Success", "DocId":"999999", "Version":"1", "StatusDescription":"Document added successfully." } } } }');
        res.setStatusCode(200);
        return res;
    }
}