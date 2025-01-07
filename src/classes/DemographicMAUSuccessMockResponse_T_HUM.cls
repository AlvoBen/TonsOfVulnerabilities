/****************************************************************************************
Apex Class Name  : DemographicMAUSuccessMockResponse_T_HUM
Version          : 1.0 
Created Date     : 29-06-2020
Function         : Test class to DemographicMedicalAddress_S_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Ajay Chakradhar                                 29/06/2020              Original Version     
***********************************************************************************************************/
@istest
global class DemographicMAUSuccessMockResponse_T_HUM implements HttpCalloutMock 
{
    global HTTPResponse respond(HTTPRequest req) 
    {
        // Creating a fake success response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        res.setBody('{"TemplateResponse":{"Errors":{"Error":[{"Code":"00000","Description":"SUCCESSFUL UPDATE TO TMR AVF2"}]}}}');
        res.setStatusCode(200);
        return res;
    }
}