/**************************************************************************** 
Apex Class Name  : HumanaRewards_S_HUM
Version          : 
Created Date     : April 9th, 2020
Function         : Service Class for Rewards Service In Humana Rewards
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Santhapuri Kishore                          04/09/2020                  Original Version
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
*************************************************************************************************** */
public with sharing class HumanaRewards_S_HUM 
{
     public String MemberGenKey = '';
     public integer MemberYear;
     Public Static final Integer Service_Timeout_HUM  = 120000;
     /*
     * Returns an object 
     * <p>
     * This method is used to assign values to the request helper class
     *
     * @param  String,String,String
     * @return HumanaRewardsServiceDetailsRequest_H_HUM
     */  
    
    public HumanaRewardsServiceDetailsRequest_H_HUM assignValuesToHelper(String MemberGenKey,integer MemberYear)
    { 
        
         HumanaRewardsServiceDetailsRequest_H_HUM oRewardDetails = new HumanaRewardsServiceDetailsRequest_H_HUM();
         HumanaRewardsServiceDetailsRequest_H_HUM.GetRewardsDetailsRequest oRewardRequest = new HumanaRewardsServiceDetailsRequest_H_HUM.GetRewardsDetailsRequest();       
        oRewardRequest.MemberGenKey = MemberGenKey;
        oRewardRequest.MemberYear = MemberYear;     
        oRewardDetails.GetRewardsDetailsRequest = orewardRequest;             
        return oRewardDetails;     
    }
    
    /*
    * Returns an HttpRequest
    * <p>
    * This method is used to generate the request
    *
    * @param  HttpRequest,HUM_Webservice_Callout__c,String
    * @return HttpRequest
    */ 
      
    public HttpRequest setHttpRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcRewardDetails, String sRequestBody)
    {
        oRequest.setMethod(oSvcRewardDetails.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setHeader('content-type',oSvcRewardDetails.Content_Type__c);    
        if(!Test.isRunningTest())oRequest.setClientCertificateName(oSvcRewardDetails.Certificate_Name__c);
        oRequest.setTimeout(Service_Timeout_HUM);
        oRequest.setEndpoint(oSvcRewardDetails.End_Point_URL__c);        
        return oRequest;   
    }  
    
    /*
     * Returns an object 
     * <p>
     * This method is used to fetch the values for Get RewardDetails Section
     *
     * @param  Object
     * @return HumanaRewards_DTO_HUM
     */
    
    public HumanaRewards_DTO_HUM.HumanaRewards_DTO_HUM1 RewardDetails(HumanaRewardsResponse_H_HUM.GetRewardsDetailsResponse oRewardsDetails)
    {
        try
        {
           HumanaRewards_DTO_HUM.HumanaRewards_DTO_HUM1 oRewardDetailsDTO = new HumanaRewards_DTO_HUM.HumanaRewards_DTO_HUM1();
            if(oRewardsDetails!= null)
            {                
                oRewardDetailsDTO.Eligible = (oRewardsDetails.GetRewardsDetails.Eligible)?'Yes':'No';
                oRewardDetailsDTO.FullfilmentVendorName = oRewardsDetails.GetRewardsDetails.FullfilmentVendorName;
                oRewardDetailsDTO.MemberEnrolledDate = DateToStringFormatAmerican(oRewardsDetails.GetRewardsDetails.MemberEnrolledDate);
                oRewardDetailsDTO.MemberCancelledDate = DateToStringFormatAmerican(oRewardsDetails.GetRewardsDetails.MemberCancelledDate);
                oRewardDetailsDTO.MemberTerminationDate = DateToStringFormatAmerican(oRewardsDetails.GetRewardsDetails.MemberTerminationDate);
                oRewardDetailsDTO.Accountstatus = oRewardsDetails.GetRewardsDetails.Accountstatus;
                oRewardDetailsDTO.CurrentrewardLevel = oRewardsDetails.GetRewardsDetails.CurrentrewardLevel;
                oRewardDetailsDTO.CurrentBalance = oRewardsDetails.GetRewardsDetails.CurrentBalance;
                oRewardDetailsDTO.FullfilmentVendorHoursofoperation = oRewardsDetails.GetRewardsDetails.FullfilmentVendorHoursofoperation;
                oRewardDetailsDTO.FullfilmentVendorExternalPhoneNumber = oRewardsDetails.GetRewardsDetails.FullfilmentVendorExternalPhoneNumber;
                oRewardDetailsDTO.FullfilmentVendorInternalPhoneNumber = oRewardsDetails.GetRewardsDetails.FullfilmentVendorInternalPhoneNumber;               
                oRewardDetailsDTO.EligibilityDate1 = DateToStringFormatAmerican(oRewardsDetails.GetRewardsDetails.RewardCoverages.EligibilityDate);          
                Return oRewardDetailsDTO;
            }   
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HumanaRewards_S_HUM', 'RewardDetails');
        }
        return null;
    } 
    /*
     * Returns a List
     * <p>
     * This method is used to fetch the values for Eligible Activities
     *
     * @param  Object
     * @return List<HumanaRewards_DTO_HUM>
     */
 
    public list<HumanaRewards_DTO_HUM.HumanaEligibleActivities_DTO_HUM> getEligibleActivities(List<HumanaRewardsResponse_H_HUM.EligibleActivities> oEligAct)
    {	
        List<HumanaRewards_DTO_HUM.HumanaEligibleActivities_DTO_HUM> listEligAct = new list<HumanaRewards_DTO_HUM.HumanaEligibleActivities_DTO_HUM>();
        try
        {
            if(oEligAct!=null)
            {      
                for(HumanaRewardsResponse_H_HUM.EligibleActivities obj : oEligAct)
                {
                    HumanaRewards_DTO_HUM.HumanaEligibleActivities_DTO_HUM objwrapper = new HumanaRewards_DTO_HUM.HumanaEligibleActivities_DTO_HUM();
                    objwrapper.EarningOpportunity1 = obj.EarningOpportunity;
                    objwrapper.EligibilityDate2 = DateToStringFormatAmerican(obj.EligibilityDate);
                    objwrapper.PotentialPointsToEarn = obj.PotentialPointsToEarn;
                    objwrapper.MaximumEarnings = obj.MaximumEarnings;
                    listEligAct.add(objwrapper);
                }
                
            }
        }       
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HumanaRewards_S_HUM', 'getEligibleActivities');
        }          
        return listEligAct;        
    }
    
    
    /*
     * Returns a List
     * <p>
     * This method is used to fetch the values for RewardsEarned
     *
     * @param  Object
     * @return List<HumanaRewards_DTO_HUM>
     */
    public list<HumanaRewards_DTO_HUM.HumanaRewardsEarned_DTO_HUM> getRewardsEarned(List<HumanaRewardsResponse_H_HUM.RewardsEarned> oEarned)
    {	
        List<HumanaRewards_DTO_HUM.HumanaRewardsEarned_DTO_HUM> listRewEarned = new list<HumanaRewards_DTO_HUM.HumanaRewardsEarned_DTO_HUM>();
        try
        {
        if(oEarned!=null)
        {        
            for(HumanaRewardsResponse_H_HUM.RewardsEarned obj : oEarned)
            {
                HumanaRewards_DTO_HUM.HumanaRewardsEarned_DTO_HUM objwrapper = new HumanaRewards_DTO_HUM.HumanaRewardsEarned_DTO_HUM();
                objwrapper.EarningOpportunity2 = obj.EarningOpportunity;
                objwrapper.CompletionDate = DateToStringFormatAmerican(obj.CompletionDate);
                objwrapper.Points_RewardsEarned = obj.Points_RewardsEarned;
                objwrapper.TimeFrameforRedeemedpoints_Earnings = DateToStringFormatAmerican(obj.TimeFrameforRedeemedpoints_Earnings); 
                listRewEarned.add(objwrapper);
            }
        }
        }
		catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'HumanaRewards_S_HUM','getRewardsEarned');
        } 
       
        return listRewEarned;        
    }
    
    /*
     * Returns a List
     * <p>
     * This method is used to fetch the values for RewardsRedeemed
     *
     * @param  Object
     * @return List<HumanaRewards_DTO_HUM>
     */
    
    public list<HumanaRewards_DTO_HUM.HumanaRewardsRedeemed_DTO_HUM> getRewardsRedeemed(List<HumanaRewardsResponse_H_HUM.RewardsRedeemed> oRedeemed)
    {	
        List<HumanaRewards_DTO_HUM.HumanaRewardsRedeemed_DTO_HUM> listRewRedeem = new list<HumanaRewards_DTO_HUM.HumanaRewardsRedeemed_DTO_HUM>();
        try
        {
        if(oRedeemed!=null)
        {        
            for(HumanaRewardsResponse_H_HUM.RewardsRedeemed obj : oRedeemed)
            {
                HumanaRewards_DTO_HUM.HumanaRewardsRedeemed_DTO_HUM objwrapper = new HumanaRewards_DTO_HUM.HumanaRewardsRedeemed_DTO_HUM();
                objwrapper.Points_Rewardsredeemed = obj.Points_Rewardsredeemed;
                objwrapper.RedemptionDate = DateToStringFormatAmerican(obj.RedemptionDate);
                objwrapper.RedemptionItem = obj.RedemptionItem;
                objwrapper.PurchasePrice = obj.PurchasePrice;
                objwrapper.ConfirmationNumber = obj.ConfirmationNumber;
                objwrapper.RewardMailingAddress = obj.RewardMailingAddress;       
                listRewRedeem.add(objwrapper);
            }
        }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HumanaRewards_S_HUM', 'getRewardsRedeemed');
        }  
       
        return listRewRedeem;        
    }
    @TestVisible private  static string DateToStringFormatAmerican(DateTime DateHour)
    {
        system.debug(dateHour);
        if(DateHour != null){
        TimeZone tz = UserInfo.getTimeZone();
        Integer Offset =tz.getOffset(DateHour);
        system.debug(' DateHour.addSeconds((Offset*-1)/1000).format(MM-dd-yyyy);'+ DateHour.addSeconds((Offset*-1)/1000).format('MM-dd-yyyy'));
            return      DateHour.addSeconds((Offset*-1)/1000).format('MM/dd/yyyy');}
        else return null;
    }
    
}