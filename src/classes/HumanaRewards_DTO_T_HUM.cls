/**************************************************************************** 
Apex Class Name  : HumanaRewards_DTO_T_HUM
Version          : 
Created Date     : April 9th, 2020
Function         : Response Wrapper for Rewardservice Service In Humana Rewards
Test Class       : Test Class for HumanaRewards_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Santhapuri Kishore                          04/09/2020                  Original Version
*************************************************************************************************** */
@isTest
public class HumanaRewards_DTO_T_HUM {
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
            HumanaRewards_DTO_HUM oHumanaRewardsDTO = new HumanaRewards_DTO_HUM();
            HumanaRewards_DTO_HUM.HumanaRewards_DTO_HUM1 oHumanaRewards_DTO_HUM1 = new HumanaRewards_DTO_HUM.HumanaRewards_DTO_HUM1();
            HumanaRewards_DTO_HUM.HumanaEligibleActivities_DTO_HUM oHumanaEligibleActivities_DTO_HUM = new HumanaRewards_DTO_HUM.HumanaEligibleActivities_DTO_HUM();
            HumanaRewards_DTO_HUM.HumanaRewardsEarned_DTO_HUM oHumanaRewardsEarned_DTO_HUM = new HumanaRewards_DTO_HUM.HumanaRewardsEarned_DTO_HUM();
            HumanaRewards_DTO_HUM.HumanaRewardsRedeemed_DTO_HUM oHumanaRewardsRedeemed_DTO_HUM = new HumanaRewards_DTO_HUM.HumanaRewardsRedeemed_DTO_HUM();
            oHumanaRewards_DTO_HUM1.Eligible ='Yes';
            oHumanaRewards_DTO_HUM1.FullfilmentVendorName ='abc';
            oHumanaRewards_DTO_HUM1.MemberEnrolledDate=null;
            oHumanaRewards_DTO_HUM1.MemberCancelledDate =null;
            oHumanaRewards_DTO_HUM1.MemberTerminationDate =null;
            oHumanaRewards_DTO_HUM1.Accountstatus ='Terminated';
            oHumanaRewards_DTO_HUM1.CurrentrewardLevel ='Member does not have 5 Rewards yet.';
            oHumanaRewards_DTO_HUM1.CurrentBalance = 0;
            oHumanaRewards_DTO_HUM1.FullfilmentVendorExternalPhoneNumber='';   
            oHumanaRewards_DTO_HUM1.EligibilityDate1 = null;  
            
           oHumanaRewardsEarned_DTO_HUM.CompletionDate = null;                      
           oHumanaEligibleActivities_DTO_HUM.EligibilityDate2 =null;
           oHumanaRewardsEarned_DTO_HUM.Points_RewardsEarned = null;
           oHumanaRewardsEarned_DTO_HUM.EarningOpportunity2 ='Cardiovascular Disease Screening';
            
           oHumanaEligibleActivities_DTO_HUM.EarningOpportunity1 ='Cardiovascular Disease Screening';
           oHumanaEligibleActivities_DTO_HUM.PotentialPointsToEarn =5.00;
           oHumanaEligibleActivities_DTO_HUM.MaximumEarnings ='';
            
            
            oHumanaRewardsRedeemed_DTO_HUM.ConfirmationNumber = null;            
            oHumanaRewardsRedeemed_DTO_HUM.Points_Rewardsredeemed = 0;            
            oHumanaRewardsRedeemed_DTO_HUM.PurchasePrice =3.00;
            oHumanaRewardsRedeemed_DTO_HUM.RedemptionDate =null;
            oHumanaRewardsRedeemed_DTO_HUM.RedemptionItem ='Diabetic Retinal Exam';
            oHumanaRewardsRedeemed_DTO_HUM.RewardMailingAddress ='xyz';
            oHumanaRewardsEarned_DTO_HUM.TimeFrameforRedeemedpoints_Earnings =null;          
            system.assertEquals(oHumanaRewardsRedeemed_DTO_HUM.RedemptionItem,'Diabetic Retinal Exam'); 
            Test.StopTest();
       }
    } 
}