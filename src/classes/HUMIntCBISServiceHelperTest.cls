/**************************************************************************************************************************************
Apex Class Name : HUMIntCBISServiceHelperTest
Version         : 1.0
Created On      : 08/05/2014
Function        : Test class for HUMIntCBISServiceHelper

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Chaitanya Kumar            18890                      10/24/2014                 Original Version
****************************************************************************************************************************************/

@istest 
private class HUMIntCBISServiceHelperTest
{
    testMethod static void testHUMIntCBISServiceHelper()
    {
         User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        
        system.runAs(u) 
        {
            Test.startTest();
                HUMIntCBISServiceHelper cbisServiceHelper = new HUMIntCBISServiceHelper();
                HUMIntCBISServiceHelper.Status status = new HUMIntCBISServiceHelper.Status();
                system.assert(status.IsSuccesful == null);
                HUMIntCBISServiceHelper.Address address = new HUMIntCBISServiceHelper.Address();
                system.assert(address.City == null);
                HUMIntCBISServiceHelper.ArrayOfResponseItem arrResponse = new HUMIntCBISServiceHelper.ArrayOfResponseItem();
                HUMIntCBISServiceHelper.Error err = new HUMIntCBISServiceHelper.Error();
                HUMIntCBISServiceHelper.PendHistoryandTermsRequestDTO  reqDTO = new HUMIntCBISServiceHelper.PendHistoryandTermsRequestDTO();
                system.assert(reqDTO.StateCode == null);
                HUMIntCBISServiceHelper.PendHistoryandTermsResponseDTO respDTO = new HUMIntCBISServiceHelper.PendHistoryandTermsResponseDTO();
                HUMIntCBISServiceHelper.Policy pol = new HUMIntCBISServiceHelper.Policy();
                system.assert(pol.BenId == null);
                HUMIntCBISServiceHelper.ResponseItem respItm = new HUMIntCBISServiceHelper.ResponseItem();
                system.assert(respItm.DOB == null);
            Test.stopTest();
        }
    }
}