/**************************************************************************** 
Apex Class Name  : HUMH1ExchangeResponseHelperTest
Version          : 1.0  
Created Date     : November 18 2014 
Description      : This class is used as test class for HUMH1ExchangeResponseHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri   19401                      11/18/2014                     Original Version
***************************************************************************************************/

@isTest
private Class HUMH1ExchangeResponseHelperTest 
{
    static testMethod void testHUMH1ExchangeResponseHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUserH1Ex1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        System.runas(user) 
        {
            Test.StartTest();
               HUMH1ExchangeResponseHelper.EnrollmentDetailResponse respElement = new HUMH1ExchangeResponseHelper.EnrollmentDetailResponse();
               HUMH1ExchangeResponseHelper.EnrollmentDetailList RespInfo = new HUMH1ExchangeResponseHelper.EnrollmentDetailList();
               List<HUMH1ExchangeResponseHelper.EnrollmentDetail> lstResp = new List<HUMH1ExchangeResponseHelper.EnrollmentDetail>();
               HUMH1ExchangeResponseHelper.EnrollmentDetail EnrApp = new HUMH1ExchangeResponseHelper.EnrollmentDetail();
               system.assert(EnrApp.strSsn == null);
            Test.stopTest();
        }
    }
}