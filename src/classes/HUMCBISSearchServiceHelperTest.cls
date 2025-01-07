/**************************************************************************** 
Apex Class Name  : HUMCBISSearchServiceHelperTest
Version          : 2.0  
Created Date     : August 12 2015 
Description      : This class is used as test class for HUMCBISSearchServiceHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shreya Choodamani                                 08/12/2015                     Original Version
***************************************************************************************************/

@isTest
private class HUMCBISSearchServiceHelperTest 
{
    static testMethod void testHUMCBISServiceHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUserH1Ex1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        System.runas(user) 
        {
            Test.StartTest();
                HUMCBISSearchServiceHelper humH1ExReqHelper = new HUMCBISSearchServiceHelper();
                HUMCBISSearchServiceHelper.Policy opolicy = new HUMCBISSearchServiceHelper.Policy();
                HUMCBISSearchServiceHelper.Status oStatus = new HUMCBISSearchServiceHelper.Status();
                HUMCBISSearchServiceHelper.PendHistoryandTermsRequestDTO oPendHistoryandTermsRequestDTO = new HUMCBISSearchServiceHelper.PendHistoryandTermsRequestDTO();
                HUMCBISSearchServiceHelper.Address oAddress = new HUMCBISSearchServiceHelper.Address();
                HUMCBISSearchServiceHelper.PendHistoryandTermsResponseDTO oPendHistoryandTermsResponseDTO = new HUMCBISSearchServiceHelper.PendHistoryandTermsResponseDTO();
                HUMCBISSearchServiceHelper.ArrayOfResponseItem oArrayOfResponseItem = new HUMCBISSearchServiceHelper.ArrayOfResponseItem();
                HUMCBISSearchServiceHelper.Error oError = new HUMCBISSearchServiceHelper.Error();
                HUMCBISSearchServiceHelper.ResponseItem oResponseItem = new HUMCBISSearchServiceHelper.ResponseItem();
            Test.stopTest();
        }
    }
}