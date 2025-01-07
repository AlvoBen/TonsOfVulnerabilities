/**************************************************************************** 
Apex Class Name  : Aspect_D_HUM
Version          : 1.0  
Created Date     : December 5 2016 
Function         : The class is the Data vlass for Aspect_WS_HUM. The class is used to query database and perfirm DML.
Test Class       : Aspect_T_HUM
 
Modification Log: 
 * Developer Name           Code Review                  Date                       Description
 *------------------------------------------------------------------------------------------------------------------
 * Shruthi Karanth            50213                  12/05/2016                  Original Version 
 * Shruthi Karanth                                   12/06/2016                  Defect 251848.0001 : Logic to send ATT,ACWT,ASA,%SL,APS as Decimals with 2 decimal points
*************************************************************************************************************/ 
public with sharing class Aspect_D_HUM 
{
    public static map<Id, string> mapSkillName = new map<Id, string>(); 
    /**
    * Returns the Map of Skill id to list ofTanscript records
    * <p>
    * This method queries the LiveChatTranscrpipt object and stores the value in a map which is grouped by Skill
    *
    * @param  DateTime dtStartTime, 
    * @param  DateTime dtEndTime
    * @return  Map<Id, List<LiveChatTranscript>
    */
    public Static Map<Id, List<LiveChatTranscript>> mapSkillListLiveChat(DateTime dtStartTime, DateTime dtEndTime)
    {
        Map<Id, List<LiveChatTranscript>> mapSkillIdListLiveChat = new Map<Id, List<LiveChatTranscript>>();
        for(LiveChatTranscript oChat : [Select id,ChatDuration,waitTime,Skillid,Skill.Masterlabel ,Status,EndedBy from LiveChatTranscript where EndTime >= :dtStartTime AND EndTime < :dtEndTime AND Successful_Resolution__c=''])
        {
            if(oChat.Skillid != NULL)
            {
                List<LiveChatTranscript> lstLiveChatTranscript;
                if(mapSkillIdListLiveChat.containskey(oChat.Skillid))
                {
                    lstLiveChatTranscript = mapSkillIdListLiveChat.get(oChat.Skillid);                
                }
                else
                {
                    lstLiveChatTranscript = new List<LiveChatTranscript>();
                }
                lstLiveChatTranscript.add(oChat);
                mapSkillIdListLiveChat.put(oChat.Skillid,lstLiveChatTranscript);
            }
        }
        return mapSkillIdListLiveChat;
    }
    
    /**
    * Returns the value of APS
    * <p>
    * This method returns a map of Skill Id and Average positions staffed for each skill. Average position is calculated as 
    *    Total of the online time/Number of agents * difference between the start date and end date passed
    *
    * @param  DateTime dtStartTime, DateTime dtEndTime
    * @return  Map<Id,Integer>
    */ 
    public static Map<Id,Decimal> getAPS(DateTime dtStartTime, DateTime dtEndTime)
    {
        Map<Id, Integer> mapDuration = new Map<Id, Integer>(); //Key is Agent Id and value is the corresponding online duration
        Map<Id, Set<Id>> mapProfile = new Map<Id, Set<Id>>(); //Key is Profile Id and value the set of all users associated to that profile

        for(LiveAgentSession oSession : [SELECT Id, Name, TimeInOnlineStatus, AgentId, Agent.ProfileId FROM LiveAgentSession WHERE LogoutTime >= :dtStartTime AND LogoutTime < :dtEndTime])
        {
            mapDuration.put(oSession.AgentId, (mapDuration.containsKey(oSession.AgentId) ? mapDuration.get(oSession.AgentId) : 0 ) + oSession.TimeInOnlineStatus);
            Set<Id> setAgentId = new Set<Id>();
            if(mapProfile.containsKey(oSession.Agent.profileId)) setAgentId = mapProfile.get(oSession.Agent.profileId);
            setAgentId.add(oSession.AgentId);
            mapProfile.put(oSession.Agent.profileId, setAgentId);
        }
        
        List<SkillUser> lstSkillUser = [SELECT Id, SkillId, Skill.Masterlabel, UserId FROM SkillUser WHERE UserId = :mapDuration.keyset()];
        
        for(SkillUser sUser : lstSkillUser)mapSkillName.put(sUser.SkillId, sUser.Skill.Masterlabel);
        
        for(SkillProfile oSkillProfile : [SELECT Id, SkillId, Skill.Masterlabel, ProfileId FROM SkillProfile WHERE ProfileId = :mapProfile.keyset()])
        {
            mapSkillName.put(oSkillProfile.SkillId, oSkillProfile.Skill.Masterlabel);
            for(Id idAgent : mapProfile.get(oSkillProfile.ProfileId))
            {
                lstSkillUser.add(new SkillUser(SkillId = oSkillProfile.SkillId, UserId = idAgent));
            }
        }
        
        map<Id, Decimal> mapSkillDuration = new map<Id, Decimal>(); //mapSkillDuration : Key is Skill Id and value is the APS value corresponding to all agents with that skill
        
        for(SkillUser oSkillUser : lstSkillUser)
        {
            Decimal iOnlineDuration = 0.00;
            
            if(mapDuration.containsKey(oSkillUser.UserId)) iOnlineDuration = mapDuration.get(oSkillUser.UserId);
            
            if(mapSkillDuration.containsKey(oSkillUser.SkillId)) iOnlineDuration += mapSkillDuration.get(oSkillUser.SkillId);
            
            mapSkillDuration.put(oSkillUser.SkillId, ((Decimal)iOnlineDuration));
        }
        
        return mapSkillDuration;                
    }
    
}