/************************************************************************************************************************************************************************************       
Apex Class Name  : MyAssistantAtHumanaEndsession_LC_HUM.cls 
Created Date     : September 25, 2023
Function         : This class is to return status code of Endsession service.
Test Class Name  : MyAssistantAtHumanaEndsession_LT_HUM.cls

Modification Log: 
Developer Name                    Date                    Description
*--------------------------------------------------------------------------------------------------
* Kiran Kotni               09/25/2023                Initial Version
************************************************************************************************************************************************************************************/
public with sharing class MyAssistantAtHumanaEndsession_LC_HUM {
    
    /*Apex Class Name  : EndSessionRequestDTO.cls 
      Function         : This class contain request nodes of Endsession service.*/
    public class EndSessionRequestDTO{
        public string chatSessionId;
    } 
    
    /* 
    * Method name : sendEndsessionRequest
    * Description : This method will return response code of Endsession service
    * Parameter   : input data which comes from UI(LWC)
    */
    @AuraEnabled
    public static Integer sendEndsessionRequest(string endsessionJson){
            EndSessionRequestDTO endsessionRequestDto = (EndSessionRequestDTO)JSON.deserialize(endsessionJson,EndSessionRequestDTO.class);
            string endsessionRequestJSON = JSON.serialize(endsessionRequestDto);
            Http http = new Http();
            HUM_Webservice_Callout__c endsessionCallout = HUM_Webservice_Callout__c.getValues('MyAssistantAtHumanaEndSessionService');
            HttpRequest request = new HttpRequest();
            request.setMethod(endsessionCallout.Request_Type__c);
            request.setHeader('Content-Type', endsessionCallout.Content_Type__c);
            request.setHeader('client_secret', HUMConstants__c.getValues('MyAssistantAtHumana_clientSecret').StringValue__c);
            request.setHeader('client_id', HUMConstants__c.getValues('MyAssistantAtHumana_clientId').StringValue__c);	
            request.setEndpoint(endsessionCallout.End_Point_URL__c);
            request.setBody(endsessionRequestJSON);
            HttpResponse res ;
          try{
            if(endsessionCallout.Active__c){
               res = http.send(request); 
            }
            Integer statusCode = res.getStatusCode();
              
            if(statusCode == 200){
                return statusCode;
            }else{
                HUMExceptionHelper.logError('Error In Response:'+statusCode, 'MyAssistantAtHumanaEndsession_LC_HUM', 'sendEndsessionRequest','System.CalloutException','Error In Response');
                return statusCode;
            }
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'MyAssistantAtHumanaEndsession_LC_HUM', 'sendEndsessionRequest');
            return null;
        }  
    }
}