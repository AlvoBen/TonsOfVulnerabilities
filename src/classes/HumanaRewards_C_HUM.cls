/**************************************************************************** 
Apex Class Name  : HumanaRewards_C_HUM
Version          : 
Created Date     : April 9th, 2020
Function         : Controller for HumanaRewards_VF_HUM
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Santhapuri Kishore                          01/04/2020                  Original Version
*  Anjani Vaddadi                              05/13/2020                  Changed the onload request from null to current year
*  Prasanthi Kandula                           02/01/2021                  US-1669849 SonarQube critical security fixes  
* Suraj Patil								   04/09/2021				   REQ - 1905877 - PR00094226 - CR AEP readiness - Low Security Issues 
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
*************************************************************************************************** */
public with sharing class HumanaRewards_C_HUM {
    
    public integer MemberYear{Get;Set;}
    @testVisible private String sRequest  = '';
    @testVisible private String sResponse = '';
    @testVisible Private string MemberGenkey; 
    public Datetime EligibilityDate{get;set;}
    public List<HumanaRewards_DTO_HUM.HumanaEligibleActivities_DTO_HUM> listEligAct{get;set;}
    public List<HumanaRewards_DTO_HUM.HumanaRewardsEarned_DTO_HUM> listRewEarned{get;set;}
    public List<HumanaRewards_DTO_HUM.HumanaRewardsRedeemed_DTO_HUM> listRewRedeem{get;set;}
    public HumanaRewards_DTO_HUM.HumanaRewards_DTO_HUM1 oRewardDetailsDTO{Get;Set;}    
    public HumanaRewardsResponse_H_HUM odResponse;
    Public String sRecordID{Get;Set;}
    public string sAccountName{Get;Set;}
    public string sAccountId{Get;Set;}
    public string sEnterpriseId{Get;Set;}
    public Map<String, Map<String, Object>> standardMapForLogging = New Map<String, Map<String, Object>>();    
    Public String standardMapvalue{ Get; Set; }{standardMapvalue = ''; }
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
    /*
    * Method Name   :  HumanaRewards_C_HUM
    * Description   :  This is the constructor of the class 
    * Return Type   :  NA
    * Parameters    :  NA
    */
    public HumanaRewards_C_HUM()
    {     
        Policy_Member__c oPolicyRecord = new Policy_Member__C();
        odResponse = new HumanaRewardsResponse_H_HUM();
        MemberYear = System.today().year();
        if(ApexPages.currentPage().getParameters().get('ID') != null)
        sRecordId = ApexPages.currentPage().getParameters().get('ID').escapeHtml4();        
        oPolicyRecord = getAccountDetails(ApexPages.currentPage().getParameters().get('ID').escapeHtml4());
        sAccountName = oPolicyRecord.Member__r.Name;
        sAccountID = oPolicyRecord.Member__c;
        sEnterpriseId = oPolicyRecord.Member__r.Enterprise_ID__c;
        MemberGenkey = oPolicyRecord.Member__r.Mbr_Gen_Key__c;              
    }
    
    /*
    * Method Name   :  rewardOnloadrequest
    * Description   :  This method is used to call the Humana Rewards Service while loading the page.
    * Return Type   :  Object
    * Parameters    :  NA
    */    
    public object rewardOnloadrequest()
    {        
        try
        {
            
            //Values required for Logging
            standardMapForLogging.put('Reward Year', new Map<String, Object>{'Reward Year' => MemberYear});
            standardMapForLogging.put('Enterprise ID', new Map<String, Object>{'Enterprise ID' => sEnterpriseId});
            standardMapvalue = JSON.serialize(standardMapForLogging);
            //Values Required fro loggig end
            HumanaRewards_S_HUM oRewardDetailsService = new HumanaRewards_S_HUM();
            HumanaRewardsServiceDetailsRequest_H_HUM oRewDetail = new HumanaRewardsServiceDetailsRequest_H_HUM();
            oRewDetail = oRewardDetailsService.assignValuesToHelper(MemberGenKey,MemberYear);
            if(oRewDetail != null)
            {                
                HUM_Webservice_Callout__c oServiceCalloutReward = HUM_Webservice_Callout__c.getvalues('Rewardservice');      
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oServiceCalloutReward.Timeout__c) / 1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                con.ContinuationMethod = 'processResponse';
                String sjsonRequest = JSON.Serialize(oRewDetail);       
                HttpRequest oRequest = new HttpRequest(); 
                oRequest = oRewardDetailsService.setHttpRequest(oRequest,oServiceCalloutReward,sjsonRequest); 
                sRequest = con.addHttpRequest(oRequest); 
                
                return con;
            }
            else
            {
                return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HumanaRewards_C_HUM', 'rewardOnloadrequest');
            return null;
        } 
        
    }
    
/*
* Method Name   :  processResponse
* Description   :  This is the Callback method for Continous Object to process the Response data
* Return Type   :  Object
* Parameters    :  NA
*/   
	public object  processResponse() 
	{      
        try{            
            HttpResponse oResponse = new HttpResponse();
            oResponse = Continuation.getResponse(sRequest);  
            if(oResponse!= null)             
            responseDeserialize(oResponse);
            
        }
        catch(Exception ex)
        {
         
            
            HUMExceptionHelper.logErrors(ex, 'HumanaRewards_C_HUM', 'processResponse');
        }
      
        return null;    
    }
    
        /*
        * Method Name   :  responseDeserialize
        * Description   :  This method is used deserialize the response
        * Return Type   :  HumanaRewardsResponse_H_HUM 
        * Parameters    :  HttpResponse 
        */
    
    @testvisible private HumanaRewardsResponse_H_HUM responseDeserialize(HttpResponse sResponse)
    {
        try 
        {   
            Integer statusCode = sResponse.getStatusCode();
            If (statusCode == HTTP_OK_HUM || statusCode == HTTP_ACCEPTED_HUM)
            {
                odResponse = (HumanaRewardsResponse_H_HUM)JSON.deserialize(sResponse.getBody(),HumanaRewardsResponse_H_HUM.class);              
                if(odResponse!=null)
                {
                    HumanaRewardsResponse_H_HUM.GetRewardsDetailsResponse oGetRewardDetailsResponse = odResponse.GetRewardsDetailsResponse;          
                    buildWrapper(oGetRewardDetailsResponse);
                }                
            }            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HumanaRewards_C_HUM', 'responseDeserialize');
            
        } 
        return null;
    }
    
  
   
        
/*
* Method Name   :  buildWrapper
* Description   :  This method is used to parse the response
* Return Type   :  N/A
* Parameters    :  HumanaRewardsResponse_H_HUM.GetRewardsDetailsResponse 
*/    
    @testvisible private void buildWrapper(HumanaRewardsResponse_H_HUM.GetRewardsDetailsResponse oRewardsDetailsResp){
        List<HumanaRewardsResponse_H_HUM.RewardsRedeemed> oRewardsRedeemed = new List<HumanaRewardsResponse_H_HUM.RewardsRedeemed>();
        List<HumanaRewardsResponse_H_HUM.EligibleActivities> oEligibleActivities = new List<HumanaRewardsResponse_H_HUM.EligibleActivities>();
        List<HumanaRewardsResponse_H_HUM.RewardsEarned> oRewardsEarned = new List<HumanaRewardsResponse_H_HUM.RewardsEarned>();    
        oRewardsRedeemed.addAll(oRewardsDetailsResp.GetRewardsDetails.RewardCoverages.RewardsRedeemed);
        oEligibleActivities.addAll(oRewardsDetailsResp.GetRewardsDetails.RewardCoverages.EligibleActivities);
        oRewardsEarned.addAll(oRewardsDetailsResp.GetRewardsDetails.RewardCoverages.RewardsEarned);
        HumanaRewards_S_HUM oResponseparse = new HumanaRewards_S_HUM();
        oRewardDetailsDTO = oResponseparse.RewardDetails(oRewardsDetailsResp);        
        if(oRewardDetailsDTO.EligibilityDate1!=null)MemberYear = Integer.valueOf(oRewardDetailsDTO.EligibilityDate1.substring(6));  
        
        listEligAct = oResponseparse.getEligibleActivities(oEligibleActivities);
        
        listRewEarned = oResponseparse.getRewardsEarned(oRewardsEarned);
        
        listRewRedeem = oResponseparse.getRewardsRedeemed(oRewardsRedeemed);
        }
    
    /*
    * Method Name   :  getdropdown
    * Description   :  This method is used to show the Drop-down Functionality
    * Return Type   :  selectoptions
    * Parameters    :   N/A
    */
    
    public List<selectoption> getdropdown()
    {    
       
        List<selectoption> lstOfObjects = new List<selectoption>();
        
        integer dates = system.today().year();        
        for(integer i=dates;i>=(dates-4);i--)
        {      
            lstOfObjects.add(new selectoption(string.valueof(i),string.valueof(i)));
            
        } 
        return lstOfObjects;     
    }
/* Method Name :getAccountDetails
 * Parameters:Policy Member ID
 * Return type: Policy_Member__c
 * Throws:NA */
        
       @testvisible  private static Policy_Member__c getAccountDetails(string oPolMemId){
            Policy_Member__c oPolicy = [select Id, Member__r.Name, Member__r.Enterprise_ID__c , Member__r.Mbr_gen_Key__c,Member__c from Policy_Member__c where Id=:oPolMemId limit 1];
            return oPolicy;
        }
        
        
    
}