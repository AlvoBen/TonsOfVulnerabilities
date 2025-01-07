/*******************************************************************************************************************************
Apex Class Name : Go365Controller_LC_HUM.cls
Version         : 1.0
Created On      : 2022
Function        : Class to get Go365 Details.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 *Pallavi Shewale                                        07/01/2022                 Original Version
 *Swapnali Sonawane                                      08/23/2022                 US#3631288 Use purchaser plan object
 ********************************************************************************************************************************/
public with sharing class Go365Controller_LC_HUM{

   /**
    * Constructor Method to for the class , Initialize variables
    */  
    
   @AuraEnabled(cacheable = true)
    public static string getWebEmulatedURL()
    {
        if(HUMConstants__c.getInstance('GO365_WEBEMULATE').stringvalue__c != null){
          sWebEmulateLink = string.valueof(HUMConstants__c.getInstance('GO365_WEBEMULATE').stringvalue__c);
        }
        return sWebEmulateLink;

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
    @AuraEnabled(continuation=true)
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
            throw new AuraHandledException(e.getMessage());
        } 
    }
  
    /**
    * Method to get the Response from getVitalityInformation().
    * @param            String (state)
    * @return           Object        
    */ 
    @AuraEnabled(continuation=true)
    public static Object processResponse(Object state) {
        try
        {
            Map<string,object> vitalityResponse = new Map<string,object>();
            VitalityAppState_DTO_HUM oVitalityState=  (VitalityAppState_DTO_HUM)state;
            HttpResponse response = Continuation.getResponse(oVitalityState.continuationState);  
        
            return JSON.serialize(checkResponse(response,oVitalityState));
        }  
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Go365Controller_C_HUM', 'processResponse');
            throw new AuraHandledException(e.getMessage());
        }
    }    

    /**
* Method to get the Response from getVitalityInformation().
* @param            HttpResponse response
* @param            VitalityAppState_DTO_HUM oVitalityState
* @return           HUMVitalityResponseHelper
*/   
    public static Map<string,object> checkResponse(HttpResponse response, VitalityAppState_DTO_HUM oVitalityState){
        
        Map<string,object> vitalityData = new Map<string,object>();
        vitalityData.put('vitalityStateData',oVitalityState);

        HUMVitalityResponseHelper oVitalityServiceResponse = new HUMVitalityResponseHelper();  

        if(response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM) 
        {
            oVitalityServiceResponse = (HUMVitalityResponseHelper)System.JSON.deserialize(response.getbody(), HUMVitalityResponseHelper.class);
            vitalityData.put('vitalityServiceResponse',oVitalityServiceResponse);
        }else{
            vitalityData.put('vitalityServiceResponse',null);
        }

       
        return vitalityData;          
    }  
}