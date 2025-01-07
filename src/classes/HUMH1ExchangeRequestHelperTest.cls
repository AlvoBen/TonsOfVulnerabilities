/**************************************************************************** 
Apex Class Name  : HUMH1ExchangeRequestHelperTest
Version          : 1.0  
Created Date     : November 18 2014 
Description      : This class is used as test class for HUMH1ExchangeRequestHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri   19401                      11/18/2014                     Original Version
***************************************************************************************************/

@isTest
private Class HUMH1ExchangeRequestHelperTest 
{
    static testMethod void testHUMH1ExchangeRequestHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUserH1Ex1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        System.runas(user) 
        {
            Test.StartTest();
                HUMH1ExchangeRequestHelper humH1ExReqHelper = new HUMH1ExchangeRequestHelper();
                HUMH1ExchangeRequestHelper.EnrollmentDetailReq Endetreq= new HUMH1ExchangeRequestHelper.EnrollmentDetailReq();
                HUMH1ExchangeRequestHelper.EnrollmentDetailRequest  respElement = new HUMH1ExchangeRequestHelper.EnrollmentDetailRequest();
                system.assert(respElement.mbrSsn == null);
            Test.stopTest();
        }
    }
}