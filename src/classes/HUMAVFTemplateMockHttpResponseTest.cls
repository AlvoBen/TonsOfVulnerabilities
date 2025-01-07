/**************************************************************************** 
Apex Class Name  : HUMAVFTemplateMockHttpResponseTest
Version          : 1.0  
Created Date     : December 05 2014 
Description      : This class is used as MockHttpResponse for HUMAVFTemplateService
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date             Description
*----------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam        23024                    12/05/2014        Original Version
* Shruthi Karanth                                  08/02/2106		 Changed response from xml to json
*****************************************************************************************************************/
@isTest
                        
global class HUMAVFTemplateMockHttpResponseTest implements HttpCalloutMock {
    // Implement this interface method
    global HTTPResponse respond(HTTPRequest req) 
    {        
        // Create a fake response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/xml');
        String strXml='';        
        String sBodyContect = req.getBody(); 
                
        strXml = '{"TemplateResponse": {"Errors": {"Error": [{"Code": "00000","Description": "SUCCESSFUL UPDATE TO TMRAVF1"}]}}}';
                             
        res.setBody(strXml);
        res.setStatusCode(200);
        return res;        
    }
}