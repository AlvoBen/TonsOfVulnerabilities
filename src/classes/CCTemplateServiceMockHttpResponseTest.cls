/**************************************************************************** 
Apex Class Name  : CCTemplateServiceMockHttpResponseTest
Version          : 1.0  
Created Date     : March 22 2018 
Description      : This class is used as MockHttpResponse for CreditableCoverage_S_HUM
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date             Description
*----------------------------------------------------------------------------------------------------------------
* sumit kumar                                       22/03/2018        Original Version
*****************************************************************************************************************/
@isTest
                        
global class CCTemplateServiceMockHttpResponseTest implements HttpCalloutMock {
            
    public String type;

    global CCTemplateServiceMockHttpResponseTest(final String type){
             this.type = type;
    }
    
      // Implement this interface method
    global HTTPResponse respond(HTTPRequest req) {
        
       // Create a mock response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        String strjson='';        
        String sBodyContect = req.getBody(); 
        if(req.getEndpoint() == HUM_Webservice_Callout__c.getValues('CCTemplate').End_Point_URL__c)
        {
            if(type=='INFORMATION'){
                strjson = '{"SaveCCTemplateResponse" :{"CRMCaseNumber":"2314569874562","Type":"Information","ReturnCode":"CICMSIN001","Description":"Data Transfer is successful"}}';  
            }
            if(type=='ERROR'){
                     
                   strjson  = '{"Fault": {"faultcode": "Code-104.1.1","faultstring": "error_service_name : TemplateService_V2 | error_service_method : SaveCCTemplate  fault_message : ","detail":{"transactionInfo": {"transaction-id": "703918735","transaction-client": "127.0.0.1","URL-in": "http://127.0.0.1:8317/TemplateService_V2.0/SaveCCTemplate","processor-name": "JSONServicesGatewayNonXML","processor-type": "Multiprotocol Gateway"},"errorInfo":{"error-code": "0x00d30003","error-headers": "HTTP1.1 500 Internal Server Error X-Backside-Transport: OK OK Content-Type: text Connection: close","error-message": "SAME CASE NUMBER PASSED FROM CRM","error-protocol-reason-phrase": "","error-protocol-response": "0","error-subcode": "0x00d30003"}}}}';
     
            }
        }
        res.setBody(strjson);
        res.setStatusCode(200);
        return res; 
    }           
   
}