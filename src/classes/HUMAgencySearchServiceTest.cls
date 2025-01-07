/**************************************************************************************************************************************
Apex Class Name : HUMAgencySearchServiceTest
Version         : 1.0
Created On      : 08/05/2014
Function        : This class mocks the response of HUMAgencySearchService

* Modification Log:
* Developer Name           Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Shreya Choodamani          17172                     08/05/2015                 Original Version
* Prasanthi Kandula          357233                    05/24/2019                 Assigned address to Business Address variable
****************************************************************************************************************************************/

@isTest
global class HUMAgencySearchServiceTest implements WebServiceMock{
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
               HUMAgencySearchService.SearchProducerResponse_element respElement = new HUMAgencySearchService.SearchProducerResponse_element();
               HUMAgencySearchService.ProducerSearchResult prodSearchResult = new HUMAgencySearchService.ProducerSearchResult();
               HUMAgencySearchService.ArrayOfProducer lstAgencyResult = new HUMAgencySearchService.ArrayOfProducer();
               HUMAgencySearchService.Producer agencyResult = new HUMAgencySearchService.Producer();
               HUMAgencySearchService.Address addr = new HUMAgencySearchService.Address();
               agencyResult.SysAssgnNbr = '1283803';
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
               agencyResult.BusinessAddress = addr;
               lstAgencyResult.Producer = new List<HUMAgencySearchService.Producer>();
               lstAgencyResult.Producer.add(agencyResult);
               prodSearchResult.ProducerList = lstAgencyResult;
               respElement.SearchProducerResult = prodSearchResult;
               response.put('response_x', respElement); 
   }
}