/**************************************************************************** 
Apex Class Name  : HUMIntTrackEnrollmentRequestHelperTest 
Version          : 1.0  
Created Date     : September 30 2014 
Description      : This class is used as test class for HUMIntTrackEnrollmentRequestHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shruthi Karanth                                  09/30/2014                     Original Version
* Shruthi Karanth								   10/24/2014					  Changes as per updated wsdl
***************************************************************************************************/

@isTest
private Class HUMIntTrackEnrollmentRequestHelperTest 
{
    static testMethod void testHUMIntTrackEnrollmentRequestHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUser1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        System.runas(user) 
        {
            Test.StartTest();
                HUMIntTrackEnrollmentRequestHelper humIntTrckEnrlmntReqHelper = new HUMIntTrackEnrollmentRequestHelper();
                HUMIntTrackEnrollmentRequestHelper.RequestComplexType reqElement = new HUMIntTrackEnrollmentRequestHelper.RequestComplexType();
                system.assert(reqElement.Subscriber_SSN == null);
            Test.stopTest();
        }
    }
}