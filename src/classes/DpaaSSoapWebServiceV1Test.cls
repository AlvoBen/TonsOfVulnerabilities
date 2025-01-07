@isTest
public with sharing class DpaaSSoapWebServiceV1Test {
   @isTest static void test(){
        // This causes a fake response to be generated
        Test.setMock(WebServiceMock.class, new DpaaSWebServiceMockImpl());
        
        DpaaSSoapWebServiceV1 dpaaSSoapWebServiceV1 =new DpaaSSoapWebServiceV1();
   
       DpaaSSoapWebServiceV1.NamedUrls gNamedUrls = new DpaaSSoapWebServiceV1.NamedUrls();
       DpaaSSoapWebServiceV1.ErrorMessages gErrorMessages  = new DpaaSSoapWebServiceV1.ErrorMessages();
       DpaaSSoapWebServiceV1.SalesforceAuthentication gSalesforceAuthentication  = new DpaaSSoapWebServiceV1.SalesforceAuthentication();
       DpaaSSoapWebServiceV1.BaseResponse  gBaseResponse  = new DpaaSSoapWebServiceV1.BaseResponse();
       DpaaSSoapWebServiceV1.Organization  gOrganization  = new DpaaSSoapWebServiceV1.Organization();
       DpaaSSoapWebServiceV1.Factory  gFactory  = new DpaaSSoapWebServiceV1.Factory();
       DpaaSSoapWebServiceV1.BaseRequest gBaseRequest  = new DpaaSSoapWebServiceV1.BaseRequest();
       DpaaSSoapWebServiceV1.NamedUrl gNamedUrl = new DpaaSSoapWebServiceV1.NamedUrl();
       DpaaSSoapWebServiceV1.Credentials gCredentials  = new DpaaSSoapWebServiceV1.Credentials();
       DpaaSSoapWebServiceV1.IpAddressAuthentication  gIpAddressAuthentication  = new DpaaSSoapWebServiceV1.IpAddressAuthentication();
       DpaaSSoapWebServiceV1.ErrorMessage  gErrorMessage = new DpaaSSoapWebServiceV1.ErrorMessage();
       
       DpaaSSoapWebServiceV1.Address gAddress   = new DpaaSSoapWebServiceV1.Address ();
       DpaaSSoapWebServiceV1.BasicAuthentication gBasicAuthentication   = new DpaaSSoapWebServiceV1.BasicAuthentication ();
       DpaaSSoapWebServiceV1.ContactInfo gContactInfo  = new DpaaSSoapWebServiceV1.ContactInfo ();
       DpaaSSoapWebServiceV1.Parameter gParameter  = new DpaaSSoapWebServiceV1.Parameter ();
       DpaaSSoapWebServiceV1.Person gPerson   = new DpaaSSoapWebServiceV1.Person ();
       DpaaSSoapWebServiceV1.OpenIdAuthentication gOpenIdAuthentication  = new DpaaSSoapWebServiceV1.OpenIdAuthentication ();
       
       
    }
}