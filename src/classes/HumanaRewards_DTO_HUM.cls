/**************************************************************************** 
Apex Class Name :  HumanaRewards_DTO_HUM
Version: 
Created Date      April 9th, 2020
Function         : DTO for Rewardservice Service In Humana Rewards
Test Class       : HumanaRewards_DTO_T_HUM
****************************************************************************
Modification Log: 
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Santhapuri Kishore                          04/09/2020                  Original Version
*************************************************************************************************** */
Public with sharing class HumanaRewards_DTO_HUM {   
    Public HumanaRewards_DTO_HUM1 oHumanaRewards_DTO_HUM1;
    Public HumanaEligibleActivities_DTO_HUM oHumanaEligibleActivities_DTO_HUM;
    Public HumanaRewardsEarned_DTO_HUM oHumanaRewardsEarned_DTO_HUM;
    Public HumanaRewardsRedeemed_DTO_HUM oHumanaRewardsRedeemed_DTO_HUM;
    
    Public with sharing class HumanaRewards_DTO_HUM1
{
    //GetReward Details
    Public string Eligible { get; set; }
    Public String FullfilmentVendorName { get; set; }
    Public string MemberEnrolledDate { get; set; }
    Public string MemberCancelledDate { get; set; }
    Public string MemberTerminationDate { Get; Set; }
    Public String Accountstatus { get; set; }
    Public String CurrentrewardLevel { get; set; }
    Public double CurrentBalance { get; set; }
    Public String FullfilmentVendorHoursofoperation { get; set; }
    Public String FullfilmentVendorExternalPhoneNumber { get; set; }
    Public String FullfilmentVendorInternalPhoneNumber { get; set; }
    Public string EligibilityDate1 { get; set; }

}
public with sharing class HumanaEligibleActivities_DTO_HUM
{
//EligibleActivities
    public String EarningOpportunity1 { get; set; }
    Public string EligibilityDate2 { get; set; }
    public double PotentialPointsToEarn { get; set; }
    public String MaximumEarnings { get; set; }
}

public with sharing class HumanaRewardsEarned_DTO_HUM
{
 //RewardsEarned
    public String EarningOpportunity2 { get; set; }
    public string CompletionDate { get; set; }
    public Double Points_RewardsEarned { get; set; }
    public string TimeFrameforRedeemedpoints_Earnings { get; set; }
}
public with sharing class HumanaRewardsRedeemed_DTO_HUM
{  //RewardsRedeemed
    public Double Points_Rewardsredeemed { get; set; }
    public string RedemptionDate { get; set; }
    public String RedemptionItem { get; set; }
    public Double PurchasePrice { get; set; }
    public String ConfirmationNumber { get; set; }
    Public String RewardMailingAddress { get; set; }
}	 
}