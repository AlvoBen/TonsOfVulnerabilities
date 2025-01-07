/**************************************************************************** 
Apex Class Name  : HumanaRewardsServiceDetailsRequest_H_HUM
Version          : 
Created Date     : April 9th, 2020
Function         : Request Wrapper for Rewardservice Service In Humana Rewards
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Santhapuri Kishore                          04/09/2020                  Original Version
*************************************************************************************************** */
public class HumanaRewardsServiceDetailsRequest_H_HUM
{   
   public GetRewardsDetailsRequest GetRewardsDetailsRequest;
    
    public class GetRewardsDetailsRequest 
    {
        public string MemberGenKey = ''; 
        public integer MemberYear;
    }
}