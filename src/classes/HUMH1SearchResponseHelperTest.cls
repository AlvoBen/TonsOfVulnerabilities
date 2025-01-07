/**************************************************************************** 
Apex Class Name  : HUMH1SearchResponseHelperTest 
Version          : 2.0  
Created Date     : August 12 2015 
Description      : This class is used as test class for HUMH1SearchResponseHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*----------------------------------------------------------------------------------------------------------------------------------
* Shreya CHoodamani                                08/12/2014                     Original Version
**********************************************************************************************************************************/

@isTest
private Class HUMH1SearchResponseHelperTest 
{
    static testMethod void testHUMH1ResponseHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUserGrpSrch1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        
        System.runas(user) 
        {
            Test.StartTest();
                HUMH1SearchResponseHelper.EnrollmentDetail EnrollmentDetail = new HUMH1SearchResponseHelper.EnrollmentDetail();
                HUMH1SearchResponseHelper.EnrollmentDetailResponse EnrollmentDetailResponse = new HUMH1SearchResponseHelper.EnrollmentDetailResponse();
                HUMH1SearchResponseHelper.EnrollmentDetailList EnrollmentDetailList = new HUMH1SearchResponseHelper.EnrollmentDetailList();
            Test.stopTest(); 
        }
    }
}