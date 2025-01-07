/**************************************************************************** 
Apex Class Name  : HUMIntTrackEnrollmentResponseHelperTest 
Version          : 1.0  
Created Date     : September 30 2014 
Description      : This class is used as test class for HUMIntTrackEnrollmentResponseHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shruthi Karanth           18892                  09/30/2014                     Original Version
* Shruthi Karanth			18892				   10/24/2014					  Changes as per updated wsdl
***************************************************************************************************/

@isTest
private Class HUMIntTrackEnrollmentResponseHelperTest 
{
    static testMethod void testHUMIntTrackEnrollmentResponseHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUser1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        System.runas(user) 
        {
            Test.StartTest();
                HUMIntTrackEnrollmentResponseHelper humIntTrckEnrlmntResHelper = new HUMIntTrackEnrollmentResponseHelper();
                HUMIntTrackEnrollmentResponseHelper.ResponseComplexType respComplex = new HUMIntTrackEnrollmentResponseHelper.ResponseComplexType();
            	HUMIntTrackEnrollmentResponseHelper.TrackList TrackListResponse = new HUMIntTrackEnrollmentResponseHelper.TrackList();
            	List<HUMIntTrackEnrollmentResponseHelper.Track> lstTrack = new List<HUMIntTrackEnrollmentResponseHelper.Track>();
            	HUMIntTrackEnrollmentResponseHelper.Track respElement = new HUMIntTrackEnrollmentResponseHelper.Track();
                system.assert(respElement.Subscriber_SSN == null);
            Test.stopTest();
        }
    }
}