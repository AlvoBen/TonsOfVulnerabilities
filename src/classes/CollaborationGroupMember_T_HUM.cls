/**********************************************************************************************************************
Apex Class: CollaborationGroupMember_T_HUM
Description: This is to test to restrict the user to unfollow the chatter group. 
Created By: Rajesh Keswani
Created On:  09/16/2016

Modification Log :
------------------------------------------------------------------------------------------------------------------------
* Developer        	 		Code Review       	 	Date                    Description
* --------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani              39866        	 	 07/09/2014               Original version
*----------------------------------------------------------------------------------------------------------------------*/

@isTest
private class  CollaborationGroupMember_T_HUM 
{
	/**
	* Returns nothing
	* This method set up the data for test class.
	*
	* @param   nothing
	* @return  nothing
	*/
    @testSetup static void setUpTestData()
    {	
        User objUserAdmin = HUMTestDataHelperTest.createTestUser('TestUserCollaborationAdmin','System Administrator');
        insert objUserAdmin;
        system.runAs(objUserAdmin)
        {
	        CollaborationGroup cg = new CollaborationGroup(name='Test CRM Service Communication',CollaborationType='Public',isArchived = true, isAutoArchiveDisabled = false);
	       	insert cg;
	       	User objUser = HUMTestDataHelperTest.createTestUser('TestUserCollaboration','Customer Care Specialist');
	        insert objUser;
	        system.assertEquals(objUser.lastname=='test TestUserCollaboration',true);
	        CollaborationGroupMember cmg = new CollaborationGroupMember(MemberId = objUser.id,CollaborationGroupId = cg.id,CollaborationRole = 'Standard',NotificationFrequency = 'N');
	        insert cmg;
        }
    }
    
    /**
	* Returns nothing
	* This method is used to test to restrict the user to unfollow the chatter group.
	*
	* @param   nothing
	* @return  nothing
	*/
    static testMethod void testUnfollowCollaborationGroup()
    {
        User testUser = [Select Id,Name from User where LastName='test TestUserCollaboration'];
        System.runAs(testUser)
        {
            Test.startTest();
            try
            {
	            CollaborationGroupMember cmg = [select id from CollaborationGroupMember where MemberId =:testUser.id limit 1  ];
	            delete cmg;
            }
            catch(Exception ex)
            {
            	system.assert(ex.getMessage().contains(GLOBAL_CONSTANT_HUM.UNFOLLOWERRORMSG_HUM),true);
            }
            Test.stopTest();
        }
    }
}