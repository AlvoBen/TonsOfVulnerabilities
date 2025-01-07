/**************************************************************************************************************************************
Apex Class Name : HUMIntAgencySearchServiceTest
Version         : 1.0
Created On      : 08/05/2014
Function        : This class mocks the response of HUMIntAgencySearchService

* Modification Log:
* Developer Name           Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Shruthi Karanth            17172                     08/05/2014                 Original Version
* Shreya Choodamani          17172                     10/14/2014                 WSDL generated class updated as per the updated WSDL for 1411.
* Shruthi Karanth			 18797					   10/20/2014			      Changes as per 14.11 wsdl 
* Shruthi Karanth			 19717					   12/05/2014				  Updated response for Defect 162293.0001
****************************************************************************************************************************************/

@isTest
global class HUMIntAgencySearchServiceTest implements WebServiceMock{
    global void doInvoke(
           Object stub,
           Object request,
           Map<String, Object> response,
           String endpoint,
           String soapAction,
           String requestName,
           String responseNS,
           String responseName,
           String responseType) {
               HUMIntAgencySearchService.SearchProducerResponse_element respElement = new HUMIntAgencySearchService.SearchProducerResponse_element();
               HUMIntAgencySearchService.ProducerSearchResult prodSearchResult = new HUMIntAgencySearchService.ProducerSearchResult();
               HUMIntAgencySearchService.ArrayOfProducer lstAgencyResult = new HUMIntAgencySearchService.ArrayOfProducer();
               HUMIntAgencySearchService.Producer agencyResult = new HUMIntAgencySearchService.Producer();
               HUMIntAgencySearchService.Address addr = new HUMIntAgencySearchService.Address();
               agencyResult.SysAssgnNbr = '1111';
               agencyResult.BaseDate = DateTime.ValueOf('2013-01-01 00:00:00');
               agencyResult.HireDate = DateTime.ValueOf('2013-01-01 00:00:00');
               agencyResult.StatusDate = DateTime.ValueOf('2013-01-01 00:00:00');
               agencyResult.DateOfBirth = DateTime.ValueOf('1990-01-01 00:00:00');
               agencyResult.EmailId = 'testmock@testmock.com';
               agencyResult.FirstName = 'TestMock';
               agencyResult.LastName = 'TestMock';
               agencyResult.HireDate = DateTime.ValueOf('1990-01-01 00:00:00');
               agencyResult.IsTermed = true;
               agencyResult.ProducerType = 'CareerAgent';
               agencyResult.StateCode = 'AL';
               addr.StateCode = 'AL';
               agencyResult.ShippingAddress = addr;
               agencyResult.ResidenceAddress = addr;
               lstAgencyResult.Producer = new List<HUMIntAgencySearchService.Producer>();
               lstAgencyResult.Producer.add(agencyResult);
               prodSearchResult.ProducerList = lstAgencyResult;
               respElement.SearchProducerResult = prodSearchResult;
               response.put('response_x', respElement); 
   }
}