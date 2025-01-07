/**************************************************************************** 
Apex Class Name  : EASService_MockResponse_T_HUM 
Version          : 1.0  
Created Date     : November 15, 2016
Function         : Mock Response Class for Enterprise Address Standardization service
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Adil Mohammed                                11/15/2016                  Original Version
***************************************************************************************************/
@isTest
global class EASService_MockResponse_T_HUM implements HttpCalloutMock {
    // Implement this interface method
    global HTTPResponse respond(HTTPRequest req) {        
        // Create a mock response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        String strjson='';        
        String sBodyContect = req.getBody();        
        strjson = '';  
        res.setBody(strjson);
        res.setStatusCode(200);
        return res;        
    }
}