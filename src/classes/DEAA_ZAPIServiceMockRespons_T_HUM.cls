/**************************************************************************** 
Apex Class Name  : DEAA_ZAPIServiceMockRespons_T_HUM
Version          : 1.0  
Created Date     : December 04 2019 
Description      : This class is used as MockHttpResponse for DEAA_Process_H_HUM
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date             Description
*----------------------------------------------------------------------------------------------------------------
* Mohammed Noor                                     22/03/2018        Original Version
*****************************************************************************************************************/
@isTest
global class DEAA_ZAPIServiceMockRespons_T_HUM implements HttpCalloutMock
{
    // Implement this interface method
    global HTTPResponse respond(HTTPRequest req) 
    {
        // Optionally, only send a mock response for a specific endpoint
        // and method.
        System.assertEquals('https://dev-crmservicews.humana.com/TemplateService_V2.0/SavePartDOptOut', req.getEndpoint());
        System.assertEquals('POST', req.getMethod());
        
        // Create a fake response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        String Response11 = '{ "SavePartDOptOutResponse":{ "CaseNumber":"1000000262663", "Type":"INFORMATION", "Code":"CICMSIN003", "Description":"CASE STATUS & OPT-OUT-IND UPDATE SUCCESSFUL." } }';
        res.setBody(Response11);
        res.setStatusCode(200);
        return res;
    }
}