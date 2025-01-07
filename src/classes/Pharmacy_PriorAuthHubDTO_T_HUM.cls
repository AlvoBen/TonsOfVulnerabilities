/*******************************************************************************************************************************
Apex Class Name : Pharmacy_PriorAuthHubDTO_T_HUM.cls
Version         : 1.0
Created On      : May 10,2018
Function        : Test CLass to Cover PriorAuthHub DTO class

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Sharan Shanmugam                             	  05/10/2018                   Original Version (REQ - 335128)
 ********************************************************************************************************************************/
@isTest 
public class Pharmacy_PriorAuthHubDTO_T_HUM 
{
	/**
    * @testSetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */																											
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
        insert objUser;
    }   
    
	/**
    * This method used To Test DTO For Right Source
    *<p>
    * @param            NULL
    * @return           void
    */
	static testMethod void testPriorAuthDTO()
	{
		User testUser = [Select Id, Name from User where LastName='test User1'];
		System.runAs(testUser)
		{
			Test.StartTest();
				PriorAuthHubResponse_DTO_HUM objRespDTO = New PriorAuthHubResponse_DTO_HUM();
				PriorAuthHubResponse_DTO_HUM.GetPriorAuthStatusHistoryResponse objGetPriorResp = New PriorAuthHubResponse_DTO_HUM.GetPriorAuthStatusHistoryResponse();
				PriorAuthHubResponse_DTO_HUM.PriorAuthDetail objPriorAuthResp = New PriorAuthHubResponse_DTO_HUM.PriorAuthDetail();
				PriorAuthHubResponse_DTO_HUM.ArgusAuth_Detail objArgusAuthResp = New PriorAuthHubResponse_DTO_HUM.ArgusAuth_Detail();
				PriorAuthHubResponse_DTO_HUM.ArgusAuthDetail objArgusAuthDetailResp = New PriorAuthHubResponse_DTO_HUM.ArgusAuthDetail();
				PriorAuthHubResponse_DTO_HUM.AgadiaAuth_Detail objAgadiaAuthResp = New PriorAuthHubResponse_DTO_HUM.AgadiaAuth_Detail();
				PriorAuthHubResponse_DTO_HUM.AgadiaAuthDetail objAgadiaAuthDetailResp = New PriorAuthHubResponse_DTO_HUM.AgadiaAuthDetail();
			Test.StopTest();
		}	
	} 
}