/**************************************************************************** 
Apex Class Name  : HUMH1SearchRequestHelperTest 
Version          : 2.0  
Created Date     : August 12 2015 
Description      : This class is used as test class for HUMH1SearchRequestHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shreya CHoodamani                                08/12/2014                     Original Version
***************************************************************************************************/

@isTest
private Class HUMH1SearchRequestHelperTest 
{
    static testMethod void testHUMH1RequestServiceHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUserGrpSrch1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        
        System.runas(user) 
        {
            Test.StartTest();
                HUMH1SearchRequestHelper.EnrollmentDetailRequest EnrollmentDetailRequest = new HUMH1SearchRequestHelper.EnrollmentDetailRequest();
                HUMH1SearchRequestHelper.EnrollmentDetailReq EnrollmentDetailReq = new HUMH1SearchRequestHelper.EnrollmentDetailReq();
            Test.stopTest(); 
        }
    }
}