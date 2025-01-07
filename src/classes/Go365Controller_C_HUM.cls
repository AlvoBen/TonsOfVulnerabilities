/****************************************************************************       
Apex Class Name  : Go365Controller_C_HUM 
Version          : 1.0  
Created Date     : August 10, 2016
Function         : used to get Go365 details
Test Class       : Go365Controller_T_HUM
****************************************************************************
  Modification Log
* Developer Name          Code Review #         Date                       Description
* Prachi Bhasin             40077               08/10/2016                 Used to get Go365 details
* Avinash Choubey                               01/12/2016                 added a check to return null if EntityID is blank or null
* Pallavi Shewale                               04/30/2021                 User Story 2230075:T1PRJ0002606 - 2021 MF1 -SF - TECH HUmana Constants clean up Member

*---------------------------------------------------------------------------------------------------------------------------
****************************************************************************************************************************/
public with sharing class Go365Controller_C_HUM
{ 
          
    /**
    * Constructor Method to for the class , Initialize variables
    */    
    public Go365Controller_C_HUM()
    {
        if(HUMConstants__c.getInstance('GO365_WEBEMULATE').stringvalue__c != null){
          sWebEmulateLink = string.valueof(HUMConstants__c.getInstance('GO365_WEBEMULATE').stringvalue__c);
        }
    }
    public static string sWebEmulateLink {get; set;}
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 


    
    /**
    * Returns responses of RemoteAction method
    * <p>
    * Method for sending Request to get the information for Vitality using continuation process
    * @param String           Account Id of the policy Member
    * @return  Object         RemoteAction method
    */
    @RemoteAction
    public static Object getVitalityInformation(String accountID)
     {
        try
        {
            VitalityCacheController_C_HUM vitalityCacheController = new VitalityCacheController_C_HUM();
            Vitality_S_HUM oService = new Vitality_S_HUM();
            Continuation continuation;
            VitalityAppState_DTO_HUM oVitalityState= new VitalityAppState_DTO_HUM();
            VitalityEntity_DTO_HUM oVitalityEntity = new VitalityEntity_DTO_HUM(); 
            string sRequestBody = '';
            
            Member_Cache__c oCache = new Member_Cache__c();
            oCache = VitalityCacheController.getMemActivityAndVitalityKeyInfo(accountID);               
            oService.copyWebRegistered(oCache, oVitalityState);         
            oService.copyMemGenKey(oCache, oVitalityState);         
            oService.copyEntityId(oCache, oVitalityEntity);         
            sRequestBody = Vitality_H_HUM.serializeEntityId(oVitalityEntity);  
            // return null if entityID is null or blank
            if(string.isBlank(sRequestBody))
                {
                 continuation = null;
                }
            else
            {      
                HUM_Webservice_Callout__c oSvcHeaderVitality = HUM_Webservice_Callout__c.getValues(HUMVitalityServiceConstants.HUMVitalityService); 
                HttpRequest request = new HttpRequest();
                oService.setupRequest(request, oSvcHeaderVitality, sRequestBody);
             
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderVitality.Timeout__c)/1000;
                continuation = new Continuation(iTIMEOUT_INT_SECS);
                
                oVitalityState.continuationState = continuation.addHttpRequest(request);
                
                continuation.state = oVitalityState;
                continuation.continuationMethod = 'processResponse'; 
            }
            return continuation; 
        }
        catch(Exception e)
        {
        HUMExceptionHelper.logErrors(e, 'Go365Controller_C_HUM', 'getVitalityInformation');
        return null;
        } 
    }
  
    /**
    * Method to get the Response from getVitalityInformation().
    * @param            String (state)
    * @return           Object        
    */ 
    public static Object processResponse(Object state) {
        try
        {
            VitalityAppState_DTO_HUM oVitalityState=  (VitalityAppState_DTO_HUM)state;
            HttpResponse response = Continuation.getResponse(oVitalityState.continuationState);          
            return checkResponse(response,oVitalityState);
        }  
        catch(Exception e)
        {
        HUMExceptionHelper.logErrors(e, 'Go365Controller_C_HUM', 'processResponse');
        return null;
        }
    }    

    /**
    * Method to get the Response from getVitalityInformation().
    * @param            HttpResponse response
    * @param            VitalityAppState_DTO_HUM oVitalityState
    * @return           HUMVitalityResponseHelper
    */   
    public static HUMVitalityResponseHelper checkResponse(HttpResponse response, VitalityAppState_DTO_HUM oVitalityState){

        HUMVitalityResponseHelper oVitalityServiceResponse = new HUMVitalityResponseHelper();    
        if(response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM) 
        {
            oVitalityServiceResponse = (HUMVitalityResponseHelper)System.JSON.deserialize(response.getbody(), HUMVitalityResponseHelper.class);
        }
        if(oVitalityState!=null){
            if(oVitalityState.lastLoginDate != null && !string.isBlank(oVitalityState.lastLoginDate)) {
                 oVitalityServiceResponse.slastLoginDate = oVitalityState.lastLoginDate ;                
        }        
        if(oVitalityState.webRegistered != null && !string.isBlank(oVitalityState.webRegistered))    oVitalityServiceResponse.swebRegistered = oVitalityState.webRegistered ;
        
        if(oVitalityState.MemGenKey != null && !string.isBlank(oVitalityState.MemGenKey))     oVitalityServiceResponse.sMemGenKey = oVitalityState.MemGenKey;        
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