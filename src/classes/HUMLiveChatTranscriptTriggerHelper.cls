/*******************************************************************************************************************************
Apex Class Name : HUMLiveChatTranscriptTriggerHelper 
Version         : 1.0
Created On      : 09/17/2014
Function        : 1.This class is called from Trigger on LiveChatTranscript.
                  2.This Creates Association between Case and LiveChatTranscript.
Test Class      : HUMLiveChatTranscriptTriggerHelperTest

Modification Log: 
 * Modification ID  Developer Name          Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0              Sagar Tapadia           18240                   09/26/2014                Original Version  
 * 1.1              Ninad Patil             18240                   09/29/2014                 Fixed Internal code reveiw comment.  
 * 1.2              Ninad Patil             18240                   09/30/2014                 Fixed Code reveie comments. 
 * 1.3              Ninad Patil             18771                   10/10/2014                 Fixed for defect :1566999 (Persisting accountId while chat transfer)        
 * 1.4              Ninad Patil             18771                   10/24/2014                 Error handleing code Added.  
 * 1.5              Sagar Tapadia           19037                   11/05/2014                 Updated Class to not send email when running test class      
 * 1.6              Sagar Tapadia           19518                   11/25/2014                 Update class to check if chat is completed then only associate Case    
 * 1.7              Sagar Tapadia           20293                   01/12/2014                 Update class to handle saving of some field values from standard object to custom chat object record.
 * 1.8              Sagar Tapadia           20306                   01/13/2014                 Changes to Custom chat record with fields from Case associated to standard chat transcript record.  
 * 1.9              Sagar Tapadia           22921                   05/07/2015                 Changes for User Story:186099 - Remove Email Sending functionality on transcript error.   
 * 2.0              Sagar Tapadia           23562                   05/28/2015                 CA5027355:Removed after trigger logic of updating custom chat transcript object record.                    
 * 2.1              Pradeepkumar Dani       34627                   08/04/2016                 Updated class with APEX standards - REQ - 270091
 * 2.2              Sharan Shanmugam        82929                   04/06/2017                 Updated primary skills as part of REQ - 301428
 * 2.3              Rajesh CG               152418                  11/29/2017                 Req 333809 Associate Interaction to New Case
 * 2.4              Sumit Sharma            178132                  02/01/2018                 REQ 351821 Defect 341308.0001 Live Agent DML Exceptions
 * 2.5              Vandana Chaudhari       220488                  04/24/2018                 REQ - 355781 CR788: Web Chat Auto Create Case (CMO CR5548)  
 * 2.6              Vandana Chaudhari                               05/17/2018                 REQ - 355781 CR788: Web Chat Auto Create Case (CMO CR5548)
 * 2.7              Shruthi Karanth                                 02/28/2020                 Fix CRUD Delete checkmarx issue in DissociateCasetoMember method
 * 2.8              Luke P. Cecil                                   03/24/2021                 Sonar Qube story fixes
 * 2.9              Alvaro Madrid                                   05/05/2022                 Adding check for duplicate Secondary Chat key and append random number
 * 3.0              Pavan Kumar                                     05/23/2023                 Added new Unsecure LiveChatButtons for new Unsecure Chat implementation in classic
 * 3.1              Sivaprakash Rajendran                           08/08/2023                 US4609720-Update URL value for the Async Chat Transcript.
 *******************************************************************************************************************************/
public with sharing class HUMLiveChatTranscriptTriggerHelper 
{   
    /**
    * onBeforeInsert Method for calling the handler method.
    * <p>
    * Execute all on Before Insert Trigger Events
    *
    * @param  lstLiveChatTranscript List of LiveChatTranscript object being modified
    * @return void
    */
    public static void onBeforeInsert(List <LiveChatTranscript> lstLiveChatTranscript) 
    {
        validateDuplicates(lstLiveChatTranscript);
        associateCaseToTranscript(lstLiveChatTranscript);
    }

    public static void validateDuplicates(List <LiveChatTranscript> lstLiveChatTranscript){
        set<String> secondaryChatIds = new set<String>();
        List<String> lstDupChatIds = new List<String>();
        String duplicateErrorMsj = 'Live Chat Transcripts with following Secondary_Chat_Id were not created as duplicated values were found your org: ';

        try{
            for(LiveChatTranscript lct : lstLiveChatTranscript){
                secondaryChatIds.add(lct.Secondary_Chat_ID__c);
            }
            for(LiveChatTranscript lct: [SELECT Secondary_Chat_ID__c FROM LiveChatTranscript WHERE Secondary_Chat_ID__c IN: secondaryChatIds AND CreatedDate = LAST_N_DAYS:2]){
                lstDupChatIds.add(lct.Secondary_Chat_ID__c);
            }
            if (! lstDupChatIds.isEmpty()) {
                //  Found duplicate Transcripts with Secondary Chat Ids from the current list of transcripts
                for (LiveChatTranscript objTranscript : lstLiveChatTranscript) {
                    if (lstDupChatIds.contains(objTranscript.Secondary_Chat_ID__c)) {
                        objTranscript.Secondary_Chat_ID__c += '_' +  Integer.valueof((Math.random() * 1000));
                    }
                }

                duplicateErrorMsj = duplicateErrorMsj  + String.join(lstDupChatIds, ', ');
                HUMExceptionHelper.logError(duplicateErrorMsj, 'HUMLiveChatTranscriptTriggerHelper', 'validateDuplicates', 'duplicatedSecondaryChatId', 'duplicated Secondary Chat Id');
            }
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'HUMLiveChatTranscriptTriggerHelper', 'validateDuplicates');
        }
    }
    /**
    * associateCaseToTranscript Method to process list of records.
    * <p>
    * This method associates Case being saved to the LiveChatTranscript if any.
    *
    * @param  lstLiveChatTranscript List of LiveChatTranscript object being modified
    * @return void
    */
    public static void associateCaseToTranscript(List <LiveChatTranscript> lstLiveChatTranscript) 
    {
        try 
        {
            map<Id, LiveChatButton> mapBtnIdToButton = new map<Id, LiveChatButton>([SELECT Id, DeveloperName FROM LiveChatButton WHERE DeveloperName IN ('MyHumana_Unsecure_Chat','Go365_Unsecure_Chat_Button','Go365_Unsecure_Commercial_Group','Humana_com_Unsecure_Commercial_Group','Humana_com_Unsecure_Medicare_Medicaid','CWP_Unsecure')]);  
            map<id,string> mapSkill = new map<id,string>();
             
            for(Skill S:[Select id, masterlabel from skill limit 1000])
            {
                mapSkill.put(S.id,S.masterlabel);
            }
            for (LiveChatTranscript lChat: lstLiveChatTranscript) 
            {
                lChat.Primary_Skill__c = mapSkill.get(lChat.SkillId);

                if( mapBtnIdToButton.containsKey(lChat.LiveChatButtonId))
                {
                    String buttonDevName = mapBtnIdToButton.get(lChat.LiveChatButtonId).DeveloperName;
                
                    if((buttonDevName == 'MyHumana_Unsecure_Chat') || (buttonDevName == 'Humana_com_Unsecure_Commercial_Group') || (buttonDevName == 'Humana_com_Unsecure_Medicare_Medicaid')){
                        lChat.Chat_Type__c ='Humana Unsecure';   
                    }else if((buttonDevName == 'Go365_Unsecure_Chat_Button') || (buttonDevName == 'Go365_Unsecure_Commercial_Group')){
                        lChat.Chat_Type__c ='Go365 Unsecure';
                    }else if(buttonDevName == 'CWP_Unsecure'){
                        lChat.Chat_Type__c ='CWP Unsecure';
                    } 
                }
                else
                {
                    lChat.AccountId = lChat.AccountId__c;           
                    //RESTRICTED_USER_ID value is org specific hence it is not consdered as GLOBAL_CONTSANTS
                    If(!String.isEmpty(HUMConstants__c.getInstance('RESTRICTED_USER_ID').StringValue__c))
                    {
                        Id userId = ID.valueOf(HUMConstants__c.getInstance('RESTRICTED_USER_ID').StringValue__c);
                        lChat.OwnerId = userId;
                    }
                }
            }
        }
        catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMLiveChatTranscriptTriggerHelper', 'associateCaseToTranscript');
        }
    }   
    /**
    * onAfterInsert Method for calling the handler method.
    * <p>
    * Execute all on After Insert Trigger Events
    *
    * @param  lstLiveChatTranscript         List of LiveChatTranscript object being modified
    * @return void
    */
    public static void onAfterInsert(List <LiveChatTranscript> lstLiveChatTranscript) 
    {
        List<LiveChatTranscript> lstSecureChatTranscript = new List<LiveChatTranscript>();
        map<Id, LiveChatButton> mapBtnIdToButton = new map<Id, LiveChatButton>([SELECT Id, DeveloperName FROM LiveChatButton WHERE DeveloperName IN ('MyHumana_Unsecure_Chat','Go365_Unsecure_Chat_Button','Go365_Unsecure_Commercial_Group','Humana_com_Unsecure_Commercial_Group','Humana_com_Unsecure_Medicare_Medicaid','CWP_Unsecure')]);
        
        for(LiveChatTranscript liveChatObj : lstLiveChatTranscript)
        {
            if(!mapBtnIdToButton.containsKey(liveChatObj.LiveChatButtonId))
                lstSecureChatTranscript.add(liveChatObj);
        }

        if(!lstSecureChatTranscript.IsEmpty())
        associateCaseToInteraction(lstSecureChatTranscript);
    }
    /**
    * associateCaseToInteraction Method to process list of records.
    * <p>
    * This method associates Interaction to New Case.
    *
    * @param  lstLiveChatTranscript         List of LiveChatTranscript object being modified
    * @return void
    */
    
    public static void associateCaseToInteraction(List <LiveChatTranscript> lstLiveChatTranscript) 
    {
        try 
        {
           Set<Id> setInteractionIds = new Set<Id>();
                      
            for (LiveChatTranscript liveChatTranscript: lstLiveChatTranscript) {
               setInteractionIds.add(liveChatTranscript.Interaction__c);             
            }          
            List<Case_Interaction__c> listCaseInteraction;
            if(setInteractionIds.size() > 0){
                listCaseInteraction = [SELECT Case__c,interaction__c FROM Case_Interaction__c where interaction__c in :setInteractionIds];
            }
            Set<String> setCaseInteraction = new Set<String> ();            
            if(listCaseInteraction.size() > 0){
                for(Case_Interaction__c caseInteraction : listCaseInteraction){  
                    setCaseInteraction.add(caseInteraction.Case__c+''+caseInteraction.interaction__c);
                }
            }           
            List<Case_Interaction__c> lstCaseInteractions = new List<Case_Interaction__c>();
            for (LiveChatTranscript lstCaseInteraction: lstLiveChatTranscript) 
            {
                Case_Interaction__c objCaseInteraction = new Case_Interaction__c();
                if(lstCaseInteraction.CaseId != Null && lstCaseInteraction.Interaction__c != Null && setCaseInteraction.contains(lstCaseInteraction.CaseId+''+lstCaseInteraction.Interaction__c) == false){
                    objCaseInteraction.Case__c = lstCaseInteraction.CaseId;                   
                    objCaseInteraction.Interaction__c = lstCaseInteraction.Interaction__c;
                    lstCaseInteractions.add(objCaseInteraction);                    
                 }                 
            }
            
            if(lstCaseInteractions.size()>0)
                insert lstCaseInteractions;          
        } 
        catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMLiveChatTranscriptTriggerHelper', 'associateCaseToInteraction');
        }
    }
    
    // This function will divide the records into records that have case and don't have case
    public static void onBeforeUpdate (List <LiveChatTranscript> lstLiveChatTranscript, Map <Id,LiveChatTranscript> mapOldChatTranscript) 
    {
        try 
        {
            Map<Id,Id> mapCaseToTranscript = new Map<Id,Id>();
            List<LiveChatTranscript> lstTranscriptWithCase = new List<LiveChatTranscript>();
            List<LiveChatTranscript> lstTranscriptWithoutCase = new List<LiveChatTranscript>();            
            Set<Id> setInteractionIds = new Set<Id>();
            Map<Id,Id> mapTranscriptToCase = new Map<Id,Id>();
            for(LiveChatTranscript lctObj : lstLiveChatTranscript)
            {
                if( null != lctObj.chat_type__c )
                {
                    if((lctObj.caseid == null) && (mapOldChatTranscript.get(lctObj.Id).caseid != null)) 
                    {
                      if((lctObj.Status !='InProgress'))
                        lctObj.addError('A Case cannot be detached from a completed chat');
                      else 
                        lstTranscriptWithoutCase.add(lctObj);
                    }
                    else if((lctObj.caseid != null) && (mapOldChatTranscript.get(lctObj.Id).caseid == null)) 
                    {
                      setInteractionIds.add(lctObj.Interaction__c);
                      mapTranscriptToCase.put(lctObj.Id,lctObj.caseid); 
                      lstTranscriptWithCase.add(lctObj);              
                    }
                            
                    if ( lctObj.caseid != null && lctObj.caseid == mapOldChatTranscript.get(lctObj.Id).caseid  && lctObj.Status == 'InProgress') 
                    {
                      mapCaseToTranscript.put(lctObj.caseid , lctObj.OwnerId);
                    }//US4678688-Addtional Logic to check the Owner for Unsecure Chat (Omni-Channel) for Transfer scenarios
                    else if(lctObj.caseid != null && lctObj.caseid == mapOldChatTranscript.get(lctObj.Id).caseid  && lctObj.Status == 'Completed'){
                        if(UserInfo.getUiThemeDisplayed() == 'Theme3' && lctObj.Chat_Type__c.contains('Unsecure')){
                            mapCaseToTranscript.put(lctObj.caseid , lctObj.OwnerId);
                        }
                    }
                  
                    if(lctObj.Status =='Completed' && mapOldChatTranscript.get(lctObj.Id).Status != 'Completed')
                    {
                      If(!String.isEmpty(HUMConstants__c.getInstance('RESTRICTED_USER_ID').StringValue__c))
                      {
                        Id userId = ID.valueOf(HUMConstants__c.getInstance('RESTRICTED_USER_ID').StringValue__c);
                        lctObj.OwnerId = userId;
                      }
                    }
                } 
            }
                    
            if(!lstTranscriptWithoutCase.IsEmpty()) 
            {
              dissociateCaseToMember(lstTranscriptWithoutCase);           
            }
            if(!lstTranscriptWithCase.IsEmpty()) 
            {
              associateTranscriptToAccount(lstTranscriptWithCase,mapTranscriptToCase, setInteractionIds);                
            }
            if(mapCaseToTranscript.size() > 0)
            {
              HUMLiveChatTranscriptChangeOwner.changeCaseOwnerShip(mapCaseToTranscript);
            }
        }
        catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMLiveChatTranscriptTriggerHelper', 'onBeforeUpdate');
        }       
    }    
    
    public static void associateTranscriptToAccount(List <LiveChatTranscript> lstLiveChatTranscript,  Map<Id,Id> mapTranscriptIdToCaseId, Set<Id> setInteractionIds) 
    {
        try 
        {
            List<Case> lstCase = new List<Case>();
            Map<Id,Id> mapCaseIdToAccountId = new Map<Id,Id>();
            Map<Id,Case> mapCaseIdToCase = new  Map<Id,Case>([Select id, AccountId,Account.RecordType.Name from Case where id=:mapTranscriptIdToCaseId.values()]); 
            Map<Id,Interaction__c> mapInteractionId = new Map<Id, Interaction__c>([Select Id,Interacting_With__c,Interacting_With_type__c from Interaction__c where Id=: setInteractionIds]);
            Interaction__c intObj;
            Case CaseObj;
            Case_Interaction__c objCaseInteraction;
            Interaction_Member__c objInteractionMember;
            List<Case_Interaction__c> lstCaseInteractions = new List<Case_Interaction__c>();
            List<Interaction_Member__c> lstInteractionMember = new List<Interaction_Member__c>();

            for(LiveChatTranscript chatTranscript: lstLiveChatTranscript)  
            {
                intObj = new  Interaction__c();
                intObj = mapInteractionId.get(chatTranscript.Interaction__c);
                CaseObj = new Case();
                CaseObj = mapCaseIdToCase.get(chatTranscript.caseid);
                
                 intObj.Interacting_With__c =  CaseObj.AccountId;
                 if(CaseObj.Account.RecordType.Name == 'Member')
                     intObj.Interacting_With_type__c =  'Member';
                 else
                      intObj.Interacting_With_type__c =  'Unknown-Member';
                
                mapInteractionId.put(intObj.Id,intObj);
                chatTranscript.AccountId = CaseObj.AccountId;
                
                objCaseInteraction = new Case_Interaction__c();
                objCaseInteraction.Case__c = CaseObj.Id;             
                objCaseInteraction.Interaction__c = intObj.Id;
                lstCaseInteractions.add(objCaseInteraction);  

                objInteractionMember = new Interaction_Member__c();
                objInteractionMember.Interacting_About__c = chatTranscript.AccountId;
                objInteractionMember.Interaction__c = chatTranscript.Interaction__c;
                objInteractionMember.Interacting_About_Type__c = intObj.Interacting_With_type__c;                   
                lstInteractionMember.add(objInteractionMember);
            }
            
            List<Interaction__c> lstInteractionToUpdate = mapInteractionId.values();
            if(!lstInteractionToUpdate.IsEmpty())
            update lstInteractionToUpdate;
            
            if(!lstCaseInteractions.IsEmpty())
            insert lstCaseInteractions;
            
            if(!lstInteractionMember.IsEmpty())
            insert lstInteractionMember;
        }
        catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMLiveChatTranscriptTriggerHelper', 'associateCaseToTranscript');
        }
    } 
    
    public static void dissociateCaseToMember(List <LiveChatTranscript> lstLiveChatTranscript) 
    {
        try 
        {
            Set<Id> setInteraction = new Set<Id>();
            for(LiveChatTranscript chatTranscript : lstLiveChatTranscript)
            {
                setInteraction.add(chatTranscript.Interaction__c);
                chatTranscript.AccountId = null;
            }
            if(setInteraction.size() > 0)
            {
                List<Interaction_Member__c> lstInteractionMember = [Select id,Interaction__c from Interaction_Member__c where Interaction__c IN: setInteraction];
                List<Case_Interaction__c> lstCaseInteraction = [Select id,Interaction__c from Case_Interaction__c where Interaction__c IN: setInteraction];
                List<Interaction__c> lstInteraction = [Select id,Interacting_With__c,Caller_Name__c,Interacting_With_type__c,Interaction_Origin__c,Created_By_Queue__c 
                                                    from Interaction__c where id IN:setInteraction];
                if(!lstInteractionMember.IsEmpty())
                {
                    SObjectType objInteractionMember = Schema.getGlobalDescribe().get('Interaction_Member__c');
                    if(objInteractionMember.getDescribe().isDeletable())
                        delete lstInteractionMember;
                }
                if(!lstCaseInteraction.IsEmpty())
                {
                    SObjectType objCaseInteraction = Schema.getGlobalDescribe().get('Case_Interaction__c');
                    if(objCaseInteraction.getDescribe().isDeletable())
                        delete lstCaseInteraction;
                }
                if(!lstInteraction.IsEmpty())
                {
                    for(Interaction__c intObj : lstInteraction)
                    {
                        intObj.Interacting_With__c = null;
                        intObj.Interacting_With_type__c = null;
                    }
                    update lstInteraction;
                }
            }
        } 
        catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMLiveChatTranscriptTriggerHelper', 'dissociateCaseToMember');
        }
    }
    
    public static List<LiveChatTranscript> validateLiveChats(List<LiveChatTranscript> triggerNews)
    {
        Set<String> validChatTypeSet = new Set<String>();

        for(Chat_Service_Source_App__mdt sourceAppObj : [SELECT MasterLabel,ChatType__c FROM Chat_Service_Source_App__mdt where MasterLabel IN ('IVAWatson','IVANINA') ])
        {
            validChatTypeSet.addall(sourceAppObj.ChatType__c.toUppercase().split(','));
        }

        List<LiveChatTranscript> lstValidLiveChat = new List<LiveChatTranscript>();
    
        for(LiveChatTranscript lctObj : triggerNews)
        {      
            if(String.isNotBlank(lctObj.chat_type__c) && validChatTypeSet.contains(lctObj.chat_type__c.toUppercase()))
            {
               continue;
            }
            lstValidLiveChat.add(lctObj);
        }   
        return lstValidLiveChat;
    }

    /**
    * US4609720-updateURLForAsycnChatBeforeInsert Method to update the URL for the Async chat Transcript.
    * @param  chatTranscript List of Humana_Chat_Transcript__c object being modified
    * @return void
    */
    public static void updateURLForAsycnChatBeforeInsert(List <LiveChatTranscript> lstLiveChatTranscript){
        try{
            //RegEx patterns are defined in the custom setting for identifying the matching contents.
            Chat_Transcript_Formatting__c chatTranscriptcustomSettings = Chat_Transcript_Formatting__c.getInstance('Formatting_RegEx_Pattern');
            String regExPatternURLUpdate = chatTranscriptcustomSettings.URL_Update__c;
            Pattern URLUpdatePattern = pattern.compile(regExPatternURLUpdate);
            Matcher matcher;
            String urlValue = '';
            
            for (LiveChatTranscript chatTranscript: lstLiveChatTranscript) 
            {
                //Update the URL only for Async Web Chat 
                if(chatTranscript.chat_type__c != null && chatTranscript.chat_type__c == GLOBAL_CONSTANT_HUM.ASYNC_WEB_CHAT){
                    //There could be many URL values present in the DataStream, So pick only the first URL. 
                    if( String.isNotBlank(chatTranscript.Body)){
                        matcher = URLUpdatePattern.matcher(chatTranscript.Body);  
                        while(matcher.find()){
                            urlValue = matcher.group(1);
                            break;
                        }
                    } 

                    if(String.isNotBlank(urlValue)){
                        chatTranscript.URL__c = urlValue.replaceAll('\\s+|<br>', '');
                    }
                }
                
            }  
        }catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMLiveChatTranscriptTriggerHelper', 'updateURLForAsycnChatBeforeInsert');
        }
            
    }
}