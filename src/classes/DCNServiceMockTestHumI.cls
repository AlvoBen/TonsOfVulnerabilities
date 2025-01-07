/**************************************************************************** 
Apex Class Name  : DCNServiceMockTestHumI
Version          : 1.0  
Created Date     : September 22 2015 
Description      : This class is used as MockHttpResponse for DCNServiceHumI
****************************************************************************
Modification Log:

* Developer Name             Code Review      Date              Description
*----------------------------------------------------------------------------------------------------------------
* Gargey Sharma 			   26705        09/22/2015        Original Version 
*****************************************************************************************************************/
@isTest                        
global class DCNServiceMockTestHumI implements HttpCalloutMock 
{
    /*Method Name : respond
    * Description : This method is used create Mock Response for DCN Service
    * Return type : HTTPResponse     
    * Paramater   : HTTPRequest
    */
    // Implement this interface method
    global HTTPResponse respond(HTTPRequest req) 
    {        
        // Create a Mock response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        String sBodyContent = req.getBody();
        String strJSON = '{ "Success":"true", "ErrorMsg":"", "DCNList":["BE0105060215139453", "BE0105060215139454", "BE0105060215139455"] }';
        res.setBody(strJSON );
        system.debug(res.getbody());
        res.setStatusCode(200);
        return res;        
    }
}