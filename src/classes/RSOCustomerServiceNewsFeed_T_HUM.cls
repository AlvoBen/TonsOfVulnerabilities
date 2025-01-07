/**********************************************************************************************************************************************************************************************       
Apex Class Name  : RSOCustomerServiceNewsFeed_T_HUM 
Created Date     : 2018
Function         : Test Class for RSO Customer Service News Feed
Assoicated Class :- RSOCustomerServiceNewsFeed_C_HUM and RSOCustomerServiceNewsFeed_D_HUM
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Amar Gadewar                               		   07/05/2018                 Original Version
* Gourab Tewary                                        07/16/2021                 User Story 1964687: T1PRJ0001827-IR 5050 - Add Footer notification to NEWS FEED footer and Mini Page (RF,CRM)
*****************************************************************************************************************************************/
@isTest
private class RSOCustomerServiceNewsFeed_T_HUM {
	
 	/**
    * 
    * Test Setup Method to create records for user and CollaborationGroup
    * @ Param     Na
    * @ Return     NA
    */
	@testSetup static void setup()
    {
        User oRSOCustomerServiceUser = HUMTestDataHelperTest.createTestUser('RSOCustomerServiceSpecialist','Customer Care Specialist');
        insert oRSOCustomerServiceUser;
        
        /*CollaborationGroup cg = new CollaborationGroup(
        Name = HUMConstants.RSO_CUSTOMER_SERVICE_NEWSFEED_GROUP,
        CollaborationType = 'Public');
      	insert cg;
		*/
     }
 
    /**
    * 
    * Test Method to test whether controller is pulling the latest post from the feed object.
    * @ Param      NA
    * @ Return     NA
    */   
	static testMethod void testnewChatterPost()
    {           
        User oUser = [SELECT Id, Name, UserName, Current_Queue__c 
                      FROM User WHERE LastName = 'test RSOCustomerServiceSpecialist'];
        system.runAs(oUser)
        {   
            Test.startTest();
            RSOCustomerServiceNewsFeed_C_HUM rsoCustomerServiceNewsFeed = new RSOCustomerServiceNewsFeed_C_HUM();
            rsoCustomerServiceNewsFeed.getLatestChatterPost(); 
            rsoCustomerServiceNewsFeed.getOpenInventoryCount();
            Test.stopTest();
        }
    }
}