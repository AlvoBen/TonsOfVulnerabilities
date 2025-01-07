/**************************************************************************** 
Apex Class Name  : TrackServiceMockErrorResponse_T_HUM 
Version          : 1.0  
Created Date     : July 21, 2017
Function         : Mock Response Class for Track service to test Failure Condition
Test Class       : TrackService_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Lalit Jain		      114278             07/21/2017                    Original Version
***************************************************************************************************/
@isTest
global class TrackServiceMockErrorResponse_T_HUM implements HttpCalloutMock 
{
    /**
	* Interface method to form the mock response
	*
	* @param    req      Request from test class
	* @return   res		 Mock response
	*/
    global HTTPResponse respond(HTTPRequest req) 
    {        
        // Create a mock response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        String strjson='';        
        String sBodyContect = req.getBody(); 
        if(req.getEndpoint() == HUM_Webservice_Callout__c.getValues('TrackService_WriteTrack').End_Point_URL__c)
        {
        	strjson = '{ "Fault":{ "faultcode":"\n          Code-60..1", "faultstring":"\n          error_service_name :\"TrackService |\n          error_service_method :  |\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"287219727", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/TrackService_V1.0/WriteTrack", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"INPUT RECPNT TRACKING ID MUST BE LESS  THAN ZERO  ", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';  
        }
        res.setBody(strjson);
        res.setStatusCode(200);
        return res;        
    }
}