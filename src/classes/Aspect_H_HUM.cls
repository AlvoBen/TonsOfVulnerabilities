/**************************************************************************** 
Apex Class Name  : Aspect_H_HUM
Version          : 1.0  
Created Date     : November 9 2016 
Function         : The class is the Helper vlass for Aspect_WS_HUM. The class is used to parse request and build response.
Test Class       : Aspect_T_HUM

Modification Log: 
 * Developer Name           Code Review                  Date                       Description
 *------------------------------------------------------------------------------------------------------------------
 * Shruthi Karanth             50213                  12/05/2016                  Original Version
 * Ranjeeth Nagishetty         50466                  12/05/2016                  Defect 251769.0001 :passing the percent value added in line PercentServiceLevel 
 * Shruthi Karanth             50573                  12/06/2016                  Defect 251848.0001 : Logic to send ATT,ACWT,ASA,%SL,APS as Decimals with 2 decimal points
 * Ranjeeth Nagishetty         53182                  12/23/2016                  REQ - 296698: SF - USER - Update %SL Calculation
*************************************************************************************************************/ 
public with sharing class Aspect_H_HUM 
{
    /**
    * Returns a List of AspectResponse_DTO_HUM
    * <p>
    * This method is called for the aspect web service
    *
    * @param  AspectRequest_DTO_HUM oRequest
    * @return  List<AspectResponse_DTO_HUM>
    * @see
    */ 
    public static List<AspectResponse_DTO_HUM> getChatData(AspectRequest_DTO_HUM oRequest) 
    {
        List<AspectResponse_DTO_HUM> lstAspectResponse;
        try
        {
            DateTime dtCurrentTime = DateTime.now();
            DateTime dtStartTime = String.isBlank(oRequest.StartDateTime)? dtCurrentTime.addMinutes(GLOBAL_CONSTANT_HUM.ASPECTSERVICE_DEFAULT_DURATION): DateTime.valueOf(oRequest.StartDateTime); 
            DateTime dtEndTime = String.isBlank(oRequest.EndDateTime)? dtCurrentTime : DateTime.valueOf(oRequest.EndDateTime);
            lstAspectResponse = buildResponse(dtStartTime,dtEndTime,oRequest.OpServiceLevelThreshold);
        }
        catch(Exception e)
        {
         
            HUMExceptionHelper.logErrors(e, 'Aspect_H_HUM', 'getChatData');
            return NULL; 
        }
        
        return lstAspectResponse;  
    }
    
    /**
    * Returns a List of AspectResponse_DTO_HUM
    * <p>
    * This method is constructs the response to be sent for aspect service
    *
    * @param  List<LiveChatTranscript> lstChatTranscript
    * @param  DateTime dtStartTime
    * @param  DateTime dtEndTime
    * @param  String sOpServiceLevelThreshold
    * @return  List<AspectResponse_DTO_HUM>
    * @see
    */
    private static list<AspectResponse_DTO_HUM> buildResponse( DateTime dtStartTime,DateTime dtEndTime,String sOpServiceLevelThreshold)
    {
        List<AspectResponse_DTO_HUM> lstAspectResponse = new List<AspectResponse_DTO_HUM>();   
        Map<Id, List<LiveChatTranscript>> mapSkillListLiveChat = Aspect_D_HUM.mapSkillListLiveChat(dtStartTime, dtEndTime);        
        Map<Id,Decimal> mapSkillDuration = Aspect_D_HUM.getAPS(dtStartTime, dtEndTime);
        Integer iCountCompletedRecords = 0;
        Integer iSumCompletedChatDuration = 0;
        Integer iSumCompletedWaitTime = 0;
        Integer iCountMissedRecords = 0;
        Integer iCountAbandonedChats = 0;
        Integer iCountWaitTime = 0;
        Decimal iSkillDuration = 0.00;
        String  sGroupData = '';
        if(!mapSkillListLiveChat.isEmpty())
        {
            for(Id iSkill : mapSkillListLiveChat.keyset())
            {
                iCountCompletedRecords = 0;
                iSumCompletedChatDuration = 0;
                iSumCompletedWaitTime = 0;
                iCountMissedRecords = 0;
                iCountAbandonedChats = 0;
                iSkillDuration = 0.00;
                iCountWaitTime = 0;
                sGroupData = '';
                for(LiveChatTranscript oLiveChat : mapSkillListLiveChat.get(iSkill))
                {
                    if(String.valueOf(iSkill).equalsIgnoreCase(String.valueOf(oLiveChat.SkillId)))
                    {
                        if(GLOBAL_CONSTANT_HUM.HUM_COMPLETED.equalsIgnoreCase(oLiveChat.Status))
                        {
                            iCountCompletedRecords +=  1 ;
                            if(oLiveChat.ChatDuration != null) iSumCompletedChatDuration +=  oLiveChat.ChatDuration ;
                            if(oLiveChat.WaitTime != null) iSumCompletedWaitTime +=  oLiveChat.WaitTime ;
                            if(String.isBlank(sOpServiceLevelThreshold)) sOpServiceLevelThreshold = GLOBAL_CONSTANT_HUM.ASPECTSERVICE_CHAT_SLA;
                            if(oLiveChat.WaitTime != null && oLiveChat.WaitTime< Integer.valueOf(sOpServiceLevelThreshold)) iCountWaitTime += 1;  
                        }
                        else if(GLOBAL_CONSTANT_HUM.ASPECTSERVICE_CHAT_STATUS_MISSED.equalsIgnoreCase(oLiveChat.Status))
                        {
                            if(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM.equalsIgnoreCase(oLiveChat.EndedBy)) iCountMissedRecords +=  1 ; 
                            if(GLOBAL_CONSTANT_HUM.ASPECTSERVICE_VISITOR.equalsIgnoreCase(oLiveChat.EndedBy)) iCountAbandonedChats +=  1 ;                         
                        }
                    }
                }
                sGroupData = mapSkillListLiveChat.containsKey(iSkill) ? mapSkillListLiveChat.get(iSkill)[0].Skill.Masterlabel : String.valueOf(iSkill);
                if(mapSkillDuration.containsKey(iSkill) )iSkillDuration = mapSkillDuration.get(iSkill).divide(1800,2);
                lstAspectResponse.add(buildAspectResponse(dtStartTime,dtEndTime,sGroupData,iSkillDuration ,iCountCompletedRecords,
                iSumCompletedChatDuration,iSumCompletedWaitTime,iCountMissedRecords,iCountWaitTime ,iCountAbandonedChats));
            }
        }
        else if(!mapSkillDuration.isEmpty())
        {
            for(Id iSkill : mapSkillDuration.keyset())
            {
                sGroupData = Aspect_D_HUM.mapSkillName.containsKey(iSkill) ? Aspect_D_HUM.mapSkillName.get(iSkill) : String.valueOf(iSkill);
                if(mapSkillDuration.containsKey(iSkill) )iSkillDuration = mapSkillDuration.get(iSkill);
                lstAspectResponse.add(buildAspectResponse(dtStartTime,dtEndTime,sGroupData,iSkillDuration ,iCountCompletedRecords,
                iSumCompletedChatDuration,iSumCompletedWaitTime,iCountMissedRecords,iCountWaitTime ,iCountAbandonedChats));
            }
        }   
        return lstAspectResponse;
    }
    
    /**
    * Returns the structure for each row of response
    * <p>
    * This method returns AspectResponse_DTO_HUM , the stuctire of the row for response
    *
    * @param  DateTime dtStartTime, 
    * @param  DateTime dtEndTime
    * @param  String DatGroup
    * @param  Integer SkillDuration
    * @param  Integer CountCompletedRecords
    * @param  Integer SumCompletedChatDuration
    * @param  Integer SumCompletedWaitTime
    * @param  Integer CountMissedRecords
    * @param  Integer ChatUnderSLA
    * @param  Integer CountAbandonedChats
    * @return  AspectResponse_DTO_HUM
    */ 
    private static AspectResponse_DTO_HUM buildAspectResponse(DateTime dtStartTime, DateTime dtEndTime, String sDataGroup, Decimal iSkillDuration, Integer iCountCompletedRecords,Integer iSumCompletedChatDuration,
                                                                Integer iSumCompletedWaitTime , Integer iCountMissedRecords ,Integer iCountWaitTime , Integer iCountAbandonedChats )
    {
        AspectResponse_DTO_HUM oAspectResponse = new AspectResponse_DTO_HUM();
        oAspectResponse.StartDate = String.valueOf(dtStartTime.date());
        oAspectResponse.EndTime = String.ValueOf(dtEndTime.format('kk:mm'));
        oAspectResponse.Identifier = GLOBAL_CONSTANT_HUM.ASPECTSERVICE_IDENTIFIER;
        oAspectResponse.DataGroup = sDataGroup;
        oAspectResponse.ContactsOffered_NCO = iCountCompletedRecords + iCountMissedRecords ;
        oAspectResponse.ContactsHandles_NCH = iCountCompletedRecords ;
        oAspectResponse.AverageTalkTime_ATT = iCountCompletedRecords == 0 ? 0.00 : ((Decimal)iSumCompletedChatDuration).divide(iCountCompletedRecords,2);
        oAspectResponse.AverageAfterContactWorkTime_ACWT = 0.00; //The logic for this field is still under discussion
        oAspectResponse.AverageDelay_ASA = iCountCompletedRecords == 0 ? 0.00 : ((Decimal)iSumCompletedWaitTime).divide(iCountCompletedRecords,2);
        oAspectResponse.AveragePositionsStaffed_APS = iSkillDuration; //The logic for this field is still under discussion
        oAspectResponse.ActualAbandons_ABD = iCountAbandonedChats;
        oAspectResponse.PercentServiceLevel = iCountCompletedRecords == 0 ? 0.00 : (((Decimal)iCountWaitTime).divide(iCountCompletedRecords,4)*100).setScale(2);   
        return oAspectResponse;
    }
}