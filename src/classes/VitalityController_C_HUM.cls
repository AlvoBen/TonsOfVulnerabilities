/****************************************************************************       
Apex Class Name  : VitalityController_C_HUM 
Version          : 1.0  
Created Date     : October 21, 2015
Function         : used to get vitality details
Test Class       : VitalityController_C_HUMTest
****************************************************************************
  Modification Log
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------------------------------
* Pooja Wadhwa            27652                 10/21/2015                 Original Version
* Cody Sanders            27730                 11/11/2015                 Modified Entity ID Parsing for defect fixes and added Unescape Strings method. 
****************************************************************************************************************************/
global with sharing class VitalityController_C_HUM
{       
    global VitalityController_C_HUM()
    {
        if(HUMConstants__c.getInstance('HUMWebEmulate').stringvalue__c != null){
          sWebEmulateLink = string.valueof(HUMConstants__c.getInstance('HUMWebEmulate').stringvalue__c);
        }
    }

    public static string sWebEmulateLink {get; set;}

/*
     * Method Name   :    getVitalityInformation() 
     * Description   :    Method for sending Request to get the information for Vitality using continuation process .
     * return Type   :    Object
     * Parameters    :    String (accountID)
*/

   @RemoteAction
    global static Object getVitalityInformation(String accountID)
     {  

        VitalityCacheController_C_HUM vitalityCacheController = new VitalityCacheController_C_HUM();
        Vitality_S_HUM oService = new Vitality_S_HUM();
        
        VitalityAppState_DTO_HUM oVitalityState= new VitalityAppState_DTO_HUM();
        VitalityEntity_DTO_HUM oVitalityEntity = new VitalityEntity_DTO_HUM(); 
        string sRequestBody = '';
        
        Member_Cache__c oCache = new Member_Cache__c();
        oCache = VitalityCacheController.getMemActivityAndVitalityKeyInfo(accountID);       
        oService.copyWebRegistered(oCache, oVitalityState);
        oService.copyMemGenKey(oCache, oVitalityState);
        oService.copyEntityId(oCache, oVitalityEntity);
        sRequestBody = Vitality_H_HUM.serializeEntityId(oVitalityEntity);
        
        HUM_Webservice_Callout__c oSvcHeaderVitality = HUM_Webservice_Callout__c.getValues(HUMVitalityServiceConstants.HUMVitalityService); 
        HttpRequest request = new HttpRequest();
        
        oService.setupRequest(request, oSvcHeaderVitality, sRequestBody);
        
        Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderVitality.Timeout__c)/1000;
        Continuation continuation = new Continuation(iTIMEOUT_INT_SECS);
        
        oVitalityState.continuationState = continuation.addHttpRequest(request);
        
        continuation.state = oVitalityState;
        continuation.continuationMethod = HUMVitalityServiceConstants.CONTINUATION_PROCESSRESPONSE_HUM; 
        
        return continuation;  
    }

 /*
     * Method Name   :    processResponse() 
     * Description   :    Method to get the Response from getVitalityInformation().
     * return Type   :    Object
     * Parameters    :    String (state)
*/   
    public static Object processResponse(Object state) {
        VitalityAppState_DTO_HUM oVitalityState=  (VitalityAppState_DTO_HUM)state;
        HttpResponse response = Continuation.getResponse(oVitalityState.continuationState);          
        return checkResponse(response,oVitalityState);  

    }  
    

/*
     * Method Name   :    checkResponse() 
     * Description   :    Method to get the Response from getVitalityInformation().
     * return Type   :    Object
     * Parameters    :    String (state)
*/   
    public static HUMVitalityResponseHelper checkResponse(HttpResponse response, VitalityAppState_DTO_HUM oVitalityState){

        HUMVitalityResponseHelper oVitalityServiceResponse = new HUMVitalityResponseHelper();    
        if(response.getStatusCode() == HUMConstants.HTTP_OK || response.getStatusCode() == HUMConstants.HTTP_ACCEPTED) 
        {
            oVitalityServiceResponse = (HUMVitalityResponseHelper)System.JSON.deserialize(response.getbody(), HUMVitalityResponseHelper.class);
        }
        if(oVitalityState!=null){
            if(oVitalityState.lastLoginDate != null && !string.isBlank(oVitalityState.lastLoginDate)) {
                 oVitalityServiceResponse.slastLoginDate = oVitalityState.lastLoginDate ;                
        }        
        if(oVitalityState.webRegistered != null && !string.isBlank(oVitalityState.webRegistered))
            oVitalityServiceResponse.swebRegistered = oVitalityState.webRegistered ;
        if(oVitalityState.MemGenKey != null && !string.isBlank(oVitalityState.MemGenKey))
            oVitalityServiceResponse.sMemGenKey = oVitalityState.MemGenKey;        
        } 
        if(oVitalityServiceResponse!=null && oVitalityServiceResponse.Response!=null && oVitalityServiceResponse.Response.MemberVitalityData!=null)
        {    
           if(oVitalityServiceResponse.Response.MemberVitalityData.MemberGoals!=null && oVitalityServiceResponse.Response.MemberVitalityData.MemberGoals.MemberGoal.size()> 0)
           {                 
               List<MemberGoalHelper> goalList = new List<MemberGoalHelper>();                    
               for(MemberGoalHelper goal: oVitalityServiceResponse.Response.MemberVitalityData.MemberGoals.MemberGoal)
               {                    
                   MemberGoalHelper finalGoal= new MemberGoalHelper();
                   finalGoal= goal;
                   if(goal.ExternalEndDate!=null && goal.ExternalEndDate!='')
                   {
                   string dateGoal = goal.ExternalEndDate +' 00:00:00';                                        
                   DateTime goalDateTime = DateTime.valueOf(dateGoal);
                   dateGoal = goalDateTime.formatGMT('MM-dd-yyyy HH:mm:ss');
                   dateGoal= dateGoal.substring(0, dateGoal.indexOf(' '));
                   dateGoal= dateGoal.replace('-', '/');
                   finalGoal.ExternalEndDate= dateGoal ;
                   }
                    goalList.add(finalGoal);
               }
               oVitalityServiceResponse.Response.MemberVitalityData.MemberGoals.MemberGoal= goalList;
           }
      }        
        return oVitalityServiceResponse;          
    }  
}