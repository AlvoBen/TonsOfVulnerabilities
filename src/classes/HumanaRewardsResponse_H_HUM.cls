/**************************************************************************** 
Apex Class Name  : HumanaRewardsResponse_H_HUM
Version          : 
Created Date     : April 9th, 2020
Function         : Response Wrapper for Rewardservice Service In Humana Rewards
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Santhapuri Kishore                          04/09/2020                  Original Version
*************************************************************************************************** */
public class HumanaRewardsResponse_H_HUM
{		
public GetRewardsDetailsResponse GetRewardsDetailsResponse = null;
public class GetRewardsDetailsResponse
{
public GetRewardsDetails GetRewardsDetails = null;
}
   
public class GetRewardsDetails 
{
	public Boolean Eligible;    
	public String FullfilmentVendorName = '';
	public DateTime MemberEnrolledDate;
	public DateTime MemberCancelledDate; 
	public DateTime MemberTerminationDate;
	public String Accountstatus = '';
	public String CurrentrewardLevel = '';  
	public double CurrentBalance;   
	public String FullfilmentVendorHoursofoperation = '';
	public String FullfilmentVendorExternalPhoneNumber = '';
	public String FullfilmentVendorInternalPhoneNumber = '';
    public RewardCoverages RewardCoverages = null;
	 
}

public class RewardCoverages 
{  
   public DateTime EligibilityDate;
   Public List<EligibleActivities> EligibleActivities =null;
   Public List<RewardsEarned> RewardsEarned =null;
   Public List<RewardsRedeemed> RewardsRedeemed =null;
}

public class EligibleActivities 
{
	public String EarningOpportunity ='';    
	public DateTime EligibilityDate;
	public double PotentialPointsToEarn;
	public String MaximumEarnings = '';  
	
}

public class RewardsEarned  
{
	public String EarningOpportunity ='';    
	public DateTime CompletionDate;
	public Double Points_RewardsEarned;
	public DateTime TimeFrameforRedeemedpoints_Earnings;  
	
}

public class RewardsRedeemed   
{
	public Double Points_Rewardsredeemed;    
	public DateTime RedemptionDate;
	public String RedemptionItem = '';
	public Double PurchasePrice; 
	public String ConfirmationNumber = '';
	public String RewardMailingAddress = '';   
	
}
}