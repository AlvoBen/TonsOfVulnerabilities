/**************************************************************************** 
Apex Class Name  : HUMCIMedicareMemberMockHttpResponseTest
Version          : 1.0  
Created Date     : December 05 2014 
Description      : This class is used as MockHttpResponse for HUMCIMedicareMemberService
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date             Description
*----------------------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri      19768                  12/05/2014        Original Version
* Pradeep Veeravali          20628                  01/27/2015        Appended POA & EOE data, Error response for Mock            
* Pradeep Veeravali          20878                  02/11/2015        Modified to Remove EOE data from Mock Response   
* Pradeep Veeravali                                 04/14/2015        Modified to make Request method from GET to POST   
* Shreya Choodamani          22538                  04/22/2015        Modified the test class to handle different responses for the application changes
* Shruthi Karanth            34224                  08/02/2106		  Changed response from xml to json
*****************************************************************************************************************/
@isTest

global class HUMCIMedicareMemberMockHttpResponseTest implements HttpCalloutMock 
{
	// Implement this interface method
    global HTTPResponse respond(HTTPRequest req) {        
    // Create a fake response
    HttpResponse res = new HttpResponse();
    res.setHeader('Content-Type', 'application/xml');
    String strXml='';        
    String sBodyContent = req.getBody(); 
    if(sBodyContent.contains('500426508A'))
    {
    	strXml = '{"MedicareMemberInfoResponse": {"LISIndicator": "N","SubsidyEffectDate": "          ","SubsidyEndDate": "          ","LISSubsidyLevel": "   ","LISCopayCategory": " ","LTSSIndicator": "N","ESRDIndicator": "N","SPAPIndicator": "","SNPIndicator": "Y","SNPTypeCode": "","POAIndicator": "N","POALastName": "                    ","POAFirstName": "               ","POAPhoneNumber": "            ","POAType": " ","POAApplDate": "          ","Errors": {"Error": []}}}';
    }
    else if(sBodyContent.contains('500426508E'))
    {
    	strXML = '{"MedicareMemberInfoResponse": {"LISIndicator": " ","SubsidyEffectDate": "          ","SubsidyEndDate": "          ","LISSubsidyLevel": "   ","LISCopayCategory": " ","LTSSIndicator": " ","ESRDIndicator": " ","SPAPIndicator": "","SNPIndicator": "","SNPTypeCode": "","POAIndicator": " ","POALastName": "                    ","POAFirstName": "               ","POAPhoneNumber": "            ","POAType": " ","POAApplDate": "          ","Errors": {"Error":    [{"Code": "E0001","Description": "MRPS006 - MEDICARE-ID COULD NOT BE RETRIEVED IN CI          "},{"Code": "E0001","Description": "MRPS010 - MEDICARE-ID COULD NOT BE RETRIEVED IN CI          "}]}}}';
    }
    else if(sBodyContent.contains('123456789123')) 
	 {
		strXml = '{"MedicareMemberInfoResponse": {"LISIndicator": "N","SubsidyEffectDate": "4/15/2015","SubsidyEndDate": "4/15/2015","LISSubsidyLevel": "100","LISCopayCategory": "1","LTSSIndicator": "N","ESRDIndicator": "N","SPAPIndicator": "","SNPIndicator": "Y","SNPTypeCode": "IN","POAIndicator": "N","POALastName": "jones","POAFirstName": "christopher","POAPhoneNumber": "            ","POAType": "Y","POAApplDate": "          ","Errors": {"Error": []}}}';
		
	 }
    else
    {
    	strXml = '{"MedicareMemberInfoResponse": {"LISIndicator": "N","SubsidyEffectDate": "          ","SubsidyEndDate": "          ","LISSubsidyLevel": "   ","LISCopayCategory": " ","LTSSIndicator": "N","ESRDIndicator": "N","SPAPIndicator": "","SNPIndicator": "Y","SNPTypeCode": "","POAIndicator": "N","POALastName": "                    ","POAFirstName": "               ","POAPhoneNumber": "            ","POAType": " ","POAApplDate": "          ","Errors": {"Error": []}}}';
    }                     
      res.setBody(strXml );
      res.setStatusCode(200);
      return res;        
    }
}