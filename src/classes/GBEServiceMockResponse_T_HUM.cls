/**************************************************************************** 
Apex Class Name  : GBEServiceMockResponse_T_HUM
Version          : 1.0  
Created Date     : June 6 2016 
Description      : This class is used as MockHttpResponse for GBEService
****************************************************************************
Modification Log:

* Developer Name             Code Review      Date              Description
*----------------------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri      29201           06/06/2016        Original Version
*****************************************************************************************************************/
@isTest                        
global class GBEServiceMockResponse_T_HUM implements HttpCalloutMock 
{
    /*
    * Method Name : respond 
    * Description : method to create mock response     
    * Return type : HTTPResponse     
    * Paramater   : HTTPRequest  
    */
    global HTTPResponse respond(HTTPRequest req) 
    {        
        // Create a Mock response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        String sBodyContent = req.getBody();
        String strXml = '<GroupInfo>'+
                          '<AsOfDate>01/01/2016</AsOfDate>'+
                          '<BenefitPackageId>XXXXXXXX</BenefitPackageId>'+
                          '<BenfitSequenceNumber/>'+
                          '<DentalOriginalEffectiveDate/>'+
                          '<EffectiveDate/>'+
                          '<EndDate>01/01/2016</EndDate>'+
                          '<EnrolledSubscriberCountDental/>'+
                          '<EnrolledSubscriberCountMedical/>'+
                          '<Exchange>'+
                             '<BillPlatformInd/>'+
                             '<BillingPlatform>PBS</BillingPlatform>'+
                             '<ExchangeIndicator>Off</ExchangeIndicator>'+
                             '<ExchangeName/>'+
                             '<ExchangeSegment>LargeGroup</ExchangeSegment>'+
                             '<ExchangeType/>'+
                             '<IsCommercial>true</IsCommercial>'+
                             '<IsHumanaOne>false</IsHumanaOne>'+
                             '<IsMedicare>false</IsMedicare>'+
                             '<LineOfBusiness>Commercial</LineOfBusiness>'+
                             '<MetallicTier/>'+
                             '<SalesChannelIdentifier/>'+
                          '</Exchange>'+
                          '<GroupId>708174</GroupId>'+
                          '<GroupName/>'+
                          '<NextRenewalDate/>'+
                          '<Platform>EM</Platform>'+
                          '<StartDate>01/01/2016</StartDate>'+
                       '</GroupInfo>';
        res.setBody(strXml );
        res.setStatusCode(200);
        return res;        
    }
}