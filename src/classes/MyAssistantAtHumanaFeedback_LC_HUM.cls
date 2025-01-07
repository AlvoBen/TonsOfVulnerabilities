/************************************************************************************************************************************************************************************       
Apex Class Name  : MyAssistantAtHumanaFeedback_LC_HUM.cls 
Created Date     : September 25, 2023
Function         : This class is to return status code of feedback service.
Test Class Name  : MyAssistantAtHumanaFeedback_LT_HUM.cls

Modification Log: 
Developer Name                    Date                    Description
*--------------------------------------------------------------------------------------------------
* Kiran Kotni               09/25/2023                Initial Version
************************************************************************************************************************************************************************************/
public with sharing class MyAssistantAtHumanaFeedback_LC_HUM {
    
     /* 
    * Method name : sendFeedbackRequest
    * Description : This method will return response code of Feedback service
    * Parameter   : input data which comes from UI(LWC)
    */
    @AuraEnabled
    public static Integer sendFeedbackRequest(string feedbackJson){
            MyAssistantAtHumanaFeedbackReq_DTO_HUM feedbackRequestDto = (MyAssistantAtHumanaFeedbackReq_DTO_HUM)JSON.deserialize(feedbackJson,MyAssistantAtHumanaFeedbackReq_DTO_HUM.class);
            string feedbackRequestJSON = JSON.serialize(feedbackRequestDto);
            Http h = new Http();
            HUM_Webservice_Callout__c feedbackCallout = HUM_Webservice_Callout__c.getValues('MyAssistantAtHumanaFeedbackService');
            HttpRequest request = new HttpRequest();
            request.setMethod(feedbackCallout.Request_Type__c);
            request.setHeader('Content-Type', feedbackCallout.Content_Type__c);
            request.setHeader('client_secret', HUMConstants__c.getValues('MyAssistantAtHumana_clientSecret').StringValue__c);
            request.setHeader('client_id', HUMConstants__c.getValues('MyAssistantAtHumana_clientId').StringValue__c);	
            request.setEndpoint(feedbackCallout.End_Point_URL__c);
            request.setBody(feedbackRequestJSON);
            HttpResponse res ;
         try{
            if(feedbackCallout.Active__c){
               res = h.send(request); 
            }
            Integer statusCode = res.getStatusCode();
             if(statusCode == 200){
                return statusCode;
            }else{
                HUMExceptionHelper.logError('Error In Response:'+statusCode, 'MyAssistantAtHumanaFeedback_LC_HUM', 'sendFeedbackRequest','System.CalloutException','Error In Response');
                return statusCode;
            } 
        }
         catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'MyAssistantAtHumanaFeedback_LC_HUM', 'sendFeedbackRequest');
             return null;
        } 
    }
}