/**************************************************************************** 
Apex Class Name  : HumanaRewardsResponse_H_T_HUM
Version          : 
Created Date     : April 9th, 2020
Function         : Response Wrapper for Rewardservice Service In Humana Rewards
Test Class       : Test Class for HumanaRewardsResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Santhapuri Kishore                          04/09/2020                  Original Version
*************************************************************************************************** */
@isTest 
public class HumanaRewardsResponse_H_T_HUM
{
/*
* Method Name : testSetup 
* Description : Test method to setup common test data for all test methods     
* Return type : void     
* Paramater   : NA  
*/
@testSetup
static void testSetup()
    {        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
    } 
/*
* Method Name : testHumanaRewardsresponseWrapper
* Description : Test method for ClaimsServiceDetailsResponse_H_HUM
* Return type : NA
* Paramater   : NA
*/
static testMethod void testHumanaRewardsresponseWrapper()
    {            
        User oCCSUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];                    
        system.runAs(oCCSUser)
        {
            Test.StartTest();
            
            HumanaRewardsResponse_H_HUM oHumanaRewardsResponse_H_HUM = new HumanaRewardsResponse_H_HUM();
            HumanaRewardsResponse_H_HUM.GetRewardsDetailsResponse  oGetRewardsDetailsResponse=  new HumanaRewardsResponse_H_HUM.GetRewardsDetailsResponse();            
            HumanaRewardsResponse_H_HUM.GetRewardsDetails oGetRewardsDetails= new HumanaRewardsResponse_H_HUM.GetRewardsDetails();
            HumanaRewardsResponse_H_HUM.RewardCoverages oRewardCoverages= new HumanaRewardsResponse_H_HUM.RewardCoverages();
            HumanaRewardsResponse_H_HUM.RewardsEarned oRewardsEarned =new HumanaRewardsResponse_H_HUM.RewardsEarned();
            HumanaRewardsResponse_H_HUM.RewardsRedeemed oRewardsRedeemed = new HumanaRewardsResponse_H_HUM.RewardsRedeemed();
			HumanaRewardsResponse_H_HUM.EligibleActivities oEligibilityActivities = new HumanaRewardsResponse_H_HUM.EligibleActivities();
            system.assert(oHumanaRewardsResponse_H_HUM!=null);
           
            Test.StopTest();
       }
    } 
}