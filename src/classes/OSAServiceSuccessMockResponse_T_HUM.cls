/****************************************************************************************
Apex Class Name  : OSAServiceMockSuccessResponse_T_HUM
Version          : 1.0 
Created Date     : 11-05-2020
Function         : Test class to OSAService_D_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Ajay Chakradhar                                 11/05/2020              Original Version     
***********************************************************************************************************/
@istest
global class OSAServiceSuccessMockResponse_T_HUM implements HttpCalloutMock 
{
    global HTTPResponse respond(HTTPRequest req) 
    {
        // Creating a fake success response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        res.setBody('{"isOSAResponse":[{"PlanID":"S5884-151-000-2020", "OSA":"true" }]}');
        res.setStatusCode(200);
        return res;
    }
}