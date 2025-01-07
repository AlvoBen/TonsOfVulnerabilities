/*******************************************************************************************************************************
Apex Class Name : MTVRemarksMember_LC_HUM
Version         : 1.0
Created On      : 06/04/2021   
Function        : This class is used to get data from  MTV member service.
Test Class      : MTVRemarksMember_LT_HUM
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Kajal Namdev		                                  06/04/2021                  Original version
*******************************************************************************************************************************************************************/
public with sharing class MTVRemarksMember_LC_HUM {

   /*
    Method Name : initiateMTVRequest
    Functionality : initiateMTVRequest is to call 'GetMTVRemarks' service.
    Return type : MTVResponseWrapper_DTO_HUM
    */ 
    @AuraEnabled (continuation=true )
    public static Object initiateMTVRequest(string memberPlanId){
        MTVResponseWrapper_DTO_HUM mtvResponse = new MTVResponseWrapper_DTO_HUM();
        if(HUMUtilityHelper.isCRMFunctionalityON('2172712')){
            List<MemberPlan> memberPlanList = MemberPlanDetail_LD_HUM.getMemberName(memberPlanId);
            if(memberPlanList.Size() > 0){
                return MTVRemarksMember_LS_HUM.getMTVRemarkData(memberPlanList[0].Name); 
            }else{
            	mtvResponse.isOnSwitch= true;
            	return mtvResponse;    
            }
        }else{
            mtvResponse.isOnSwitch= false;
            return mtvResponse;
        }
    }
    
    /*
    Method Name : processMTVResponse
    Functionality : processMTVResponse is to parse reponse from 'GetMTVRemarks' service.
    Return type : MTVResponseWrapper_DTO_HUM
    */
    @AuraEnabled (continuation=true )
    public static object processMTVResponse(List<String> labels, Object State){   
        return MTVRemarksMember_LS_HUM.processResponse(labels, State);
    } 
}