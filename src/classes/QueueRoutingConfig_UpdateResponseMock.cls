/*
  Apex Class Name  : QueueRoutingConfig_UpdateResponseMock   
  Version          : 1.0 
  Created Date     : OCT 25 2017
  Function         : Mock Response for Create method in Omni Routing config update  
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Syed Mubarak T                                      11/30/2017                      Original Version
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

@isTest
global class QueueRoutingConfig_UpdateResponseMock implements HttpCalloutMock
{
   global HTTPResponse respond(HTTPRequest req)
   {
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        String strjson='{ "UpdateResponse":{ "Records":{ "Result":[ { "Id":"0K9L00000008OQJKA2", "Success":"true" } ] } } }';
        res.setbody(strjson);
        res.setStatusCode(200);
        return res;
   }    
}