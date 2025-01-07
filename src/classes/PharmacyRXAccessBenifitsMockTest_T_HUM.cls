/**************************************************************************** 
Apex Class Name  : PharmacyRXAccessBenefits_Wrapper_HUM   
Version          : 1.0  
Created Date     : May 6, 2021
Function         : This Class is mock up test class for service response
******************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Suraj Patil     				               05/06/2021                 REQ - 1003582 - 1011772 - 1694036
***************************************************************************************************/
public class PharmacyRXAccessBenifitsMockTest_T_HUM implements HttpCalloutMock  {
    public String json = '{"KMDSearchResults": {"Documents":    [{"FileType": "pdf","Href": "https://test-dctm.humana.com/Mentor/Web/v.aspx/PaidTimeOff(PTO)?chronicleID=090009298081dcc3&dl=&searchID=km-search-link&row=0","Title": "Paid Time Off (PTO)"},{"FileType": "doc","Href": "https://test-dctm.humana.com/Mentor/Web/v.aspx/B-54601?chronicleID=090009298081d240&dl=&searchID=km-search-link&row=0","Title": "B-54601"},{"FileType": "","Href": "https://test-dctm.humana.com/Mentor/Web/v.aspx/?chronicleID=&dl=&searchID=km-search-link&row=0","Title": ""},{"FileType": "html","Href": "https://test-dctm.humana.com/Mentor/Web/v.aspx/test_4_coveo_topic?chronicleID=0900092980d96d1f&dl=&searchID=km-search-link&row=0","Title": "test_4_coveo_topic"}],"Error":    {"ErrorCode": "","ErrorMessage": "","Severity": ""},"NumberRetrieved": 4,"SearchReferenceID": "0|viv_IX37M1","TotalDocumentsFound": 4}}';

       
    string jsonError =  '{ "Fault":{ "faultcode":"Code-67.8.1", "faultstring":"","detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/PharmacyClaimsService/GetClaimDetails", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" },  "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
          
    public HTTPResponse respond(HTTPRequest req)
    {
        HttpResponse resp = new HttpResponse();
        resp.setStatusCode(200);
        resp.setStatus('Complete');
        
     resp.setBody(json);
             
        return resp;
    }
	
    public HTTPResponse respondError(HTTPRequest req)
    {
        HttpResponse resp = new HttpResponse();
        resp.setStatusCode(400);
        resp.setStatus('Error');
        
        resp.setBody(jsonError);
        
        return resp;
    }
}