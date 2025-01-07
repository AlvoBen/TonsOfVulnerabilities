/************************************************************************************************************************************************************************************       
Apex Class Name  : MyAssistantAtHumanaChat_LC_HUM.cls 
Created Date     : September 22, 2023
Function         : This class is for showing Chat response data table.
Test Class Name  : MyAssistantAtHumanaChat_LT_HUM.cls

Modification Log: 
Developer Name                    Date                    Description
*--------------------------------------------------------------------------------------------------
* Kiran Kotni               09/22/2023                Initial Version
************************************************************************************************************************************************************************************/
public class MyAssistantAtHumanaChat_LC_HUM {
   
     /* 
    * Method name : sendChatRequest
    * Description : This method will return response DTO object
    * Parameter   : input data which comes from UI(LWC)
    */
    @AuraEnabled
    public static MyAssistantAtHumanaChatResponse_DTO_HUM sendChatRequest(string chatJson){
            MyAssistantAtHumanaChatRequest_DTO_HUM chatRequestDto = (MyAssistantAtHumanaChatRequest_DTO_HUM)JSON.deserialize(chatJson,MyAssistantAtHumanaChatRequest_DTO_HUM.class);
            string chatRequestJSON = JSON.serialize(chatRequestDto);
            Http h = new Http();
            HUM_Webservice_Callout__c chatCallout = HUM_Webservice_Callout__c.getValues('MyAssistantAtHumanaChatService');
            HttpRequest request = new HttpRequest();
            request.setMethod(chatCallout.Request_Type__c);   
            request.setTimeOut(60000);
            request.setHeader('Content-Type', chatCallout.Content_Type__c);
            request.setHeader('client_secret', HUMConstants__c.getValues('MyAssistantAtHumana_clientSecret').StringValue__c);
            request.setHeader('client_id', HUMConstants__c.getValues('MyAssistantAtHumana_clientId').StringValue__c);
            request.setEndpoint(chatCallout.End_Point_URL__c);
            request.setBody(chatRequestJSON);
           HttpResponse res ;
           try{
            if(chatCallout.Active__c){
               res = h.send(request); 
            }
            Integer statusCode = res.getStatusCode();
            if(statusCode==200){
                MyAssistantAtHumanaChatResponse_DTO_HUM chatResponseDto = (MyAssistantAtHumanaChatResponse_DTO_HUM)JSON.deserialize(res.getBody(),MyAssistantAtHumanaChatResponse_DTO_HUM.class);
                return chatResponseDto;  
            }else{
                HUMExceptionHelper.logError('Error In Response:'+res.getStatusCode(), 'MyAssistantAtHumanaChat_LC_HUM', 'sendChatRequest','System.CalloutException','Error In Response');
                return null;
            } 
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'MyAssistantAtHumanaChat_LC_HUM', 'sendChatRequest');
            return null;
        }
    }
}