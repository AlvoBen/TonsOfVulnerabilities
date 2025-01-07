/**************************************************************************** 
Apex Class Name  : HUMIntAEHistoryResponseHelperTest 
Version          : 1.0  
Created Date     : Aug 09 2014 
Description      : This class is used as test class for HUMIntAEHistoryResponseHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shruthi Karanth           17165                   08/09/2014                     Original Version
***************************************************************************************************/

@isTest
private Class HUMIntAEHistoryResponseHelperTest 
{
    static testMethod void testHUMIntAEHistoryResponseHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUser1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        System.runas(user) 
        {
            Test.StartTest();
            	HUMIntAEHistoryResponseHelper intAEHistory = new HUMIntAEHistoryResponseHelper();
            	HUMIntAEHistoryResponseHelper.PendHistory penHist = new HUMIntAEHistoryResponseHelper.PendHistory();
            	HUMIntAEHistoryResponseHelper.PendHistoryList pendingHist = new HUMIntAEHistoryResponseHelper.PendHistoryList();
            	HUMIntAEHistoryResponseHelper.ResponseComplexType resp = new HUMIntAEHistoryResponseHelper.ResponseComplexType();
            	system.assert(resp.Result ==  null);
            Test.stopTest();
        }
    }
}