/*******************************************************************************************************************************
Apex Trigger Name : HumanaChatTranscriptTrigger 
Version           : 1.0
Created On        : 06/01/2023
Function          : 1.This class is called from Trigger on HumanaChatTranscriptTrigger.
                    2.This helper class is used format the Content and append the appropriate Agent and Customer name for the Async Chat Transcript.
Test Classes      : HumanaChatTranscriptTriggerHelperTest                 
Modification Log: 
* Modification ID           Developer Name           Code Review                 Date                       Description
*---------------------------------------------------------------------------------------------------------------------------
* 1.0                       Sivaprakash Rajendran                                06/01/2023                 US4595853 - Original Version
*******************************************************************************************************************************/
public with sharing class HumanaChatTranscriptTriggerHelper {

    //To bypass the trigger from the test class when its not required
    public static boolean byPassTrigger = false;

    public static void onBeforeInsert(List <Humana_Chat_Transcript__c> humanaChatTranscript) 
    {
        if(HUMUtilityHelper.isCRMFunctionalityON(HUMConstants.SWITCH_US4595853)){
            chatTranscriptContentFormatting(humanaChatTranscript);
        }
        
    }

    public static void onBeforeUpdate (List <Humana_Chat_Transcript__c> humanaChatTranscript, Map <Id,Humana_Chat_Transcript__c> mapOldChatTranscript) 
    {
        if(HUMUtilityHelper.isCRMFunctionalityON(HUMConstants.SWITCH_US4595853)){
            chatTranscriptContentFormatting(humanaChatTranscript);   
        }                      
    }

    /**
    * chatTranscriptContentFormatting Method to process and format the Async Transcript content.
    * @param  chatTranscript List of Humana_Chat_Transcript__c object being modified
    * @return void
    */
    public static void chatTranscriptContentFormatting(List<Humana_Chat_Transcript__c> chatTranscript){
        //RegEx patterns are defined in the custom setting for identifying the matching contents.
        Chat_Transcript_Formatting__c chatTranscriptcustomSettings = Chat_Transcript_Formatting__c.getInstance('Formatting_RegEx_Pattern');
        String regExPatternContent = chatTranscriptcustomSettings.Chat_Content__c;
        String regExPatternNameUpdate = chatTranscriptcustomSettings.Name_Update__c;
        Pattern contentPattern = pattern.compile(regExPatternContent);
        Pattern nameUpdatePattern = pattern.compile(regExPatternNameUpdate);
        Matcher matcher;
        String firstName;
        String lastname;
        String clientFullName;
        String agentName;
        Set<Id> setchatOwnerIds = new Set<Id>();
        Set<Id> setCustomChatIds = new Set<Id>();

        try 
        {   
            //Collect all userId's from transcriptObj
            for(Humana_Chat_Transcript__c transcriptObj : chatTranscript)
            {
                setchatOwnerIds.add(transcriptObj.OwnerId);
                setCustomChatIds.add(transcriptObj.live_chat_id__C);
            }
            //Query the user records based on the setchatOwnerIds
            Map<Id,User> chatOwnerIdsMap = new Map<Id,User>([SELECT Id, Name from User WHERE Id IN :setchatOwnerIds]);
            Map<Id,LiveChatTranscript> liveChatIdsMap = new Map<Id,LiveChatTranscript>([SELECT chat_type__c from LiveChatTranscript WHERE Id IN :setCustomChatIds]);

            for(Humana_Chat_Transcript__c transcriptObj : chatTranscript)
            {
                //Check if the Async chat has content to format
                LiveChatTranscript livechatObj = liveChatIdsMap.get(transcriptObj.live_chat_id__C);
                if(livechatObj != null && livechatObj.chat_type__c != null && livechatObj.chat_type__c == GLOBAL_CONSTANT_HUM.ASYNC_WEB_CHAT){
                    if(transcriptObj.Body__c != null && transcriptObj.Body__c != ''){
                        String chatTranscriptBody = transcriptObj.Body__c;
                        //Run the matcher for content
                        matcher = contentPattern.matcher(chatTranscriptBody);   
        
                        while(matcher.find()){
                            String messageContent = matcher.group();
                            chatTranscriptBody = chatTranscriptBody.replace(messageContent, '<b>'+messageContent+'</b>');                                                                                                      
                        }   
        
                        //Run the matcher for Names
                        matcher = nameUpdatePattern.matcher(chatTranscriptBody);   
        
                        while(matcher.find()){
                            String customerNameField = matcher.group(1);
                            String customerNameValue = matcher.group(2);
                           
                            if(customerNameField.equalsIgnoreCase(HUMConstants.OBJECT_ACCOUNT_API_FIRSTNAME)){
                                if(customerNameValue != null && customerNameValue != ''){
                                    firstName = formatName(customerNameValue);
                                }    
                            }
        
                            if(customerNameField.equalsIgnoreCase(HUMConstants.OBJECT_ACCOUNT_API_LASTNAME)){
                                if(customerNameValue != null && customerNameValue != ''){
                                    lastname = formatName(customerNameValue);
                                }
                            }
                        }
                           
                        //Replace CLIENT_MESSAGE to specific Customer Name
                        if(firstName != null && firstName != '' && lastname != null && lastname != ''){
                            chatTranscriptBody = chatTranscriptBody.replaceAll(HUMConstants.LIVEAGENT_API_CLIENT_MESSAGE, +firstName+' '+lastname);
                        }
                        
                        //Replace AGENT_MESSAGE to actual Agent Name
                        if(transcriptObj.OwnerId != null){
                            User transcriptOwner = chatOwnerIdsMap.get(transcriptObj.OwnerId);
                            if(transcriptOwner != null){
                                agentName = transcriptOwner.name;
                            }
                        }
                        
                        if(agentName != ''){
                            chatTranscriptBody = chatTranscriptBody.replaceAll(HUMConstants.LIVEAGENT_API_AGENT_MESSAGE, +agentName);
                        }
                                
                        //Update the final formated content to the original chat transcript object.
                        transcriptObj.Body__c = chatTranscriptBody;    
                        System.debug('Final Formated content::'+ transcriptObj.Body__c);                   
                    }
                
                }
             }                  
        }
        catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HumanaChatTranscriptTriggerHelper', 'onBeforeUpdate');
        }       
    }

    /**
    * formatName Method to remove the extra spaces and <br> from the chat transcript.
    * @param  namevalue  of FirstName, LastName from the chat transcript used.
    * @return void
    */
    public Static String formatName(String namevalue){
        return namevalue.replaceAll('\\s+|<br>', '');
    }
}