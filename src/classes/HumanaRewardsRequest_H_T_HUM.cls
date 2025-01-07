/**************************************************************************** 
Apex Class Name  : HumanaRewardsRequest_H_T_HUM
Version          : 
Created Date     : April 9th, 2020
Function         : Response Wrapper for Rewardservice Service In Humana Rewards
Test Class       : Test Class for HumanaRewardsserviceRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Santhapuri Kishore                          04/09/2020                  Original Version
*************************************************************************************************** */
@isTest
public class HumanaRewardsRequest_H_T_HUM {
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
* Method Name : testHumanaRewardsRequestwrapper
* Description : Test method for ClaimsServiceDetailsResponse_H_HUM
* Return type : NA
* Paramater   : NA
*/
static testMethod void testHumanaRewardsRequestwrapper()
    {            
        User oCCSUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];                    
        system.runAs(oCCSUser)
        {
            Test.StartTest();
            
                HumanaRewardsServiceDetailsRequest_H_HUM oHumanaRewardsServiceDetailsRequest_H_HUM = new HumanaRewardsServiceDetailsRequest_H_HUM();
                //HumanaRewardsServiceDetailsRequest_H_HUM.GetRewardsDetailsRequest oGetRewardsDetailsRequest = new HumanaRewardsSevriceDetailsRequest_H_HUM.GetRewardsDetailsRequest();
                HumanaRewardsServiceDetailsRequest_H_HUM.GetRewardsDetailsRequest oGetRewardsDetailsRequest = new HumanaRewardsServiceDetailsRequest_H_HUM.GetRewardsDetailsRequest();
           		system.assert(oHumanaRewardsServiceDetailsRequest_H_HUM!=null);
            Test.stopTest();
        }
}
}