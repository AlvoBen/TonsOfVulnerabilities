/*****************************************************************************************************
Apex Class Name :  LiveChatTranscript_C_HUM
Version: 1.0 
Created Date 		: 10/12/2017
Function            : Controller for live chat transcripts related to case
Test Class  		: LiveChatTranscript_T_HUM

        Modification Log:   
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Sumit Pharande              148798           10/12/2017                   Original Version
* Pradeepkumar Dani		  	  148798		   11/17/2017				    re-factoring
* Ravikanth Samudrala 						   05-18-2023                   US-4531288 - T1PRJ0901110 MF1.11 - Additional Fields (blue marked) to the CRM PDF - CRM and MHK 
*******************************************************************************************************/
Public with sharing Class LiveChatTranscript_C_HUM
{
    Public string columnName {Get;Set;}
    Public string columnHeader {Get;Set;}  
    
    /**
	*  Constructor for the class
	*  @name LiveChatTranscript_C_HUM
	*  @param NA
	*  @return void
	*  @throws NA
	*/ 
    Public LiveChatTranscript_C_HUM()
    {
        columnName = json.serialize(New List < String > {'Name', 'StartTime', 'EndTime', 'Owner', 'Status'});     
        columnHeader = json.serialize(New List < String > {'Live Chat Transcript Name', 'Start Time', 'End Time', 'Owner', 'Status'});     
    }
    
    /**
	*  Gets list of Live Chat Transcripts for given case using remote method
	*  
	*  @param 	sCaseRecordId
	*  @return 	lstscript
	*/
    @RemoteAction
    Public Static list <LiveChatTranscript_DTO_HUM > getTranscript(String sCaseRecordId) 
    {
        Return LiveChatTranscript_C_HUM.getLiveChatDTOList(sCaseRecordId);
    }
    
    /**
	*  Gets list of Live Chat Transcripts for given case using 
	*  
	*  @param caseRecId
	*  @return lstscript
	*/
    Public Static list<LiveChatTranscript_DTO_HUM > getLiveChatDTOList(String caseRecId)
    {
        list<LiveChatTranscript_DTO_HUM> lstscript = new list<LiveChatTranscript_DTO_HUM>();
        String sName = system.UserInfo.getName();
        Case ocase = [SELECT Id, Case_Owner__c, Owner_Queue__c,CreatedDate FROM Case WHERE id =:caseRecId];
        User ouser = [SELECT Id, Current_Queue__c FROM User WHERE Id=:UserInfo.getUserId()];
        list<LiveChatTranscript> chatlst;
        chatlst = [SELECT Name,Id, StartTime, EndTime,Owner.Alias, OwnerId, Status, Humana_Chat_Transcript__c ,URL__c FROM LiveChatTranscript WHERE Case.id =:caseRecId];
        for(LiveChatTranscript oLct :chatlst)
        {
            LiveChatTranscript_DTO_HUM oLiveChat = new LiveChatTranscript_DTO_HUM ();
            oLiveChat.Name =  oLct.Name + CaseDetailConstants_HUM.sSrcUp + oLct.Id  ;
            String sStartDate = oLct.StartTime != null ? oLct.StartTime.format(CaseDetailConstants_HUM.HUMDateTimeFormat) : CaseDetailConstants_HUM.sEMPTY;
            oLiveChat.StartTime = sStartDate ; 
            String sEndDate = oLct.EndTime != null ? oLct.EndTime.format(CaseDetailConstants_HUM.HUMDateTimeFormat) : CaseDetailConstants_HUM.sEMPTY;
            oLiveChat.EndTime =sEndDate;
            oLiveChat.Status = oLct.Status; 
            oLiveChat.Owner = oLct.Owner.alias + CaseDetailConstants_HUM.sSrcUp + oLct.OwnerId; 
            oLiveChat.HumanaChatTranscript = oLct.Humana_Chat_Transcript__c;
            oLiveChat.URL = oLct.URL__c;
            lstscript.add(oLiveChat);
        }
        return lstscript ;
    }
    
      /**
	*  Gets list of Live Chat Transcripts for given case using 
	*  
	*  @param caseRecId
	*  @return lstscript
	*/
    Public Static list<LiveChatTranscriptEvent_DTO_HUM > getLiveChatEventDTOList(String caseRecId)
    {
        list<LiveChatTranscriptEvent_DTO_HUM> lstscriptEvent = new list<LiveChatTranscriptEvent_DTO_HUM>();
        String sName = system.UserInfo.getName();
        Case ocase = [SELECT Id, Case_Owner__c, Owner_Queue__c,CreatedDate FROM Case WHERE id =:caseRecId];
        list<LiveChatTranscriptEvent> chatEventlst;
        chatEventlst = [Select id,Agent.Name,Type,Time,Detail,LiveChatTranscript.Name from LiveChatTranscriptEvent where Livechattranscript.Case.Id =:caseRecId];
        for(LiveChatTranscriptEvent oEventLct :chatEventlst)
        {
            LiveChatTranscriptEvent_DTO_HUM oLiveChatEvent = new LiveChatTranscriptEvent_DTO_HUM ();
            String sStartDate = oEventLct.Time != null ? oEventLct.Time.format(CaseDetailConstants_HUM.HUMDateTimeFormat) : CaseDetailConstants_HUM.sEMPTY;
            oLiveChatEvent.StartTime = sStartDate ; 
            oLiveChatEvent.Agent =oEventLct.Agent.Name;
            oLiveChatEvent.Type = oEventLct.Type; 
            oLiveChatEvent.Detail = oEventLct.Detail; 
            oLiveChatEvent.HumanaChatTranscriptName = oEventLct.LiveChatTranscript.Name;
            lstscriptEvent.add(oLiveChatEvent);
        }
        return lstscriptEvent ;
    }
}