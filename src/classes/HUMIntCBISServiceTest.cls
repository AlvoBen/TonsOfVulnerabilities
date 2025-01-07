/**************************************************************************************************************************************
Apex Class Name : HUMIntCBISServiceTest
Version         : 1.0
Created On      : 08/05/2014
Function        : Test class for HUMIntCBISService

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Chaitanya Kumar             18890                    10/24/2014                 Original Version
****************************************************************************************************************************************/

@istest 
private class HUMIntCBISServiceTest
{
    testMethod static void testHUMIntCBISService()
    {
         User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        
        system.runAs(u) 
        {
            Test.startTest();
                Test.setMock(WebServiceMock.class, new HUMIntCBISServiceResponseTest());
                HUMIntCBISService cbisService = new HUMIntCBISService();
                HUMIntCBISServiceHelper.PendHistoryandTermsRequestDTO request = new HUMIntCBISServiceHelper.PendHistoryandTermsRequestDTO();
                HUMIntCBISService.EndPoint_GetPendHistoryAndTermsDetails aeHistoryServies = new HUMIntCBISService.EndPoint_GetPendHistoryAndTermsDetails();
                aeHistoryServies.GetPendHistoryTermsDetails(request);
                HUMIntCBISService.EndPoint_GetPendHistoryAndTermsDetails aeHistory = new HUMIntCBISService.EndPoint_GetPendHistoryAndTermsDetails(null,null,1);
                HUMIntCBISService.GetPendHistoryTermsDetails_element pendHistory = new HUMIntCBISService.GetPendHistoryTermsDetails_element();
                HUMIntCBISService.GetPendHistoryTermsDetailsResponse_element pendHistoryResponse = new HUMIntCBISService.GetPendHistoryTermsDetailsResponse_element();
                system.assert(aeHistoryServies.endpoint_x == null);
            Test.stopTest();
        }
    }
}