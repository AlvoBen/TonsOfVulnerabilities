/*******************************************************************************************************************************
Apex Class Name : CommunityGamificationController_C_HUM 
Version         : 1.0
Created On      : 09/02/2020
Function        : This class is a controller class for Community custom Gamification components
Test Class      : CommunityGamificationController_T_HUM

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Shreya Agrawal                                      09/02/2020                    Original version
**************************************************************************************************************************************/

public with sharing class CommunityGamificationController_C_HUM {
    
    public static Id testNetworkId;
    
    /**
     * getUserProfileGamification
     * Calculate and Fetch User Gamification details  
     * @param 
     * @return Map<String,Object>
     **/
     
    @AuraEnabled(cacheable=true)
    public static Map<String,Object> getUserProfileGamification(){
        
        Map<String,Object> ResponseMap = new Map<String,Object>();
        Map<String,String> NextLevelMappings = new Map<String,String>();
        Map<String,Decimal> PointsRuleMap = new Map<String,Decimal>();
        Map<String,Object> ReputationLevelMap= new Map<String,Object>();
        Boolean bool_Support = false;
        Boolean bool_Go365 = false;
        String CurrentLevel= '';
        Id NetwrkId;
        
        if(Test.isRunningTest()){
           NetwrkId= testNetworkId;   
        }
        else{
            NetwrkId= Network.getNetworkId();
        }
        
        String NetworkName = [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember where MemberID=: UserInfo.getUserId() and NetworkID = :NetwrkId limit 1].Network.Name;
        
        List<ReputationPointsRule> PointsRuleList = [SELECT Points, Type, ParentId, Id FROM ReputationPointsRule where ParentId=: NetwrkId and points > 0 ];
        
        for(ReputationPointsRule eachRule:PointsRuleList){
            PointsRuleMap.put(eachRule.Type,eachRule.Points);
        }
        
        List<NetworkMember> NtwrkMembrInfo = [SELECT Id, NetworkId, MemberId, ReputationPoints FROM NetworkMember where MemberId=: userinfo.getUserId() and NetworkId=: NetwrkId];
        
        if(NtwrkMembrInfo[0].ReputationPoints > 10000 && (NetworkName.equalsIgnorecase(CommunityConstants_C_HUM.COMMUNITY_GO365_MEDICARE) || NetworkName.equalsIgnorecase(CommunityConstants_C_HUM.COMMUNITY_SUPPORT))){
            bool_Support = true;
            ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_NEXT_LEVEL,System.Label.Community_User_Gamification_Congrats_Message_For_Support);
        }    
        else if(NtwrkMembrInfo[0].ReputationPoints > 90000 && NetworkName.equalsIgnorecase(CommunityConstants_C_HUM.COMMUNITY_GO365_COMMERCIAL)) {
            bool_Go365 = true;
            ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_NEXT_LEVEL,System.Label.Community_User_Gamification_Congrats_Message_For_Commercial);
        }
            
        if ( ((NetworkName.equalsIgnorecase(CommunityConstants_C_HUM.COMMUNITY_GO365_MEDICARE) || NetworkName.equalsIgnorecase(CommunityConstants_C_HUM.COMMUNITY_SUPPORT)) && !bool_Support)  || (NetworkName.equalsIgnorecase(CommunityConstants_C_HUM.COMMUNITY_GO365_COMMERCIAL) && !bool_Go365) ) {
        //Finding the current level of the User
        if(NtwrkMembrInfo[0].ReputationPoints >= 0 && NtwrkMembrInfo[0].ReputationPoints <= 500)
            CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_1 ;
        else if(NtwrkMembrInfo[0].ReputationPoints >= 501 && NtwrkMembrInfo[0].ReputationPoints <= 1000)
            CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_2 ;
        else if(NtwrkMembrInfo[0].ReputationPoints >= 1001 && NtwrkMembrInfo[0].ReputationPoints <= 1500)
            CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_3 ;
        else if(NtwrkMembrInfo[0].ReputationPoints >= 1501 && NtwrkMembrInfo[0].ReputationPoints <= 2000)
            CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_4 ;
        else if(NtwrkMembrInfo[0].ReputationPoints >= 2001 && NtwrkMembrInfo[0].ReputationPoints <= 2500)
            CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_5 ;
        else if(NtwrkMembrInfo[0].ReputationPoints >= 2501 && NtwrkMembrInfo[0].ReputationPoints <= 3000)
            CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_6 ;
        else if(NtwrkMembrInfo[0].ReputationPoints >= 3001 && NtwrkMembrInfo[0].ReputationPoints <= 5000)
            CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_7 ;
        else if(NtwrkMembrInfo[0].ReputationPoints >= 5001 && NtwrkMembrInfo[0].ReputationPoints <= 8000)
            CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_8 ;
        else if(NtwrkMembrInfo[0].ReputationPoints >= 8001 && NtwrkMembrInfo[0].ReputationPoints <= 10000)
            CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_9 ;
        if(NetworkName.equalsIgnorecase(CommunityConstants_C_HUM.COMMUNITY_GO365_COMMERCIAL))
        {
            if(NtwrkMembrInfo[0].ReputationPoints >= 10001 && NtwrkMembrInfo[0].ReputationPoints <= 12000)
                CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_10;
            if(NtwrkMembrInfo[0].ReputationPoints >= 12001 && NtwrkMembrInfo[0].ReputationPoints <= 15000)
                CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_11;
                if(NtwrkMembrInfo[0].ReputationPoints >= 15001 && NtwrkMembrInfo[0].ReputationPoints <= 17000)
                CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_12;
            if(NtwrkMembrInfo[0].ReputationPoints >= 17001 && NtwrkMembrInfo[0].ReputationPoints <= 18000)
                CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_13;
                if(NtwrkMembrInfo[0].ReputationPoints >= 18001 && NtwrkMembrInfo[0].ReputationPoints <= 20000)
                CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_14;
            if(NtwrkMembrInfo[0].ReputationPoints >= 20001 && NtwrkMembrInfo[0].ReputationPoints <= 35000)
                CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_15;
                if(NtwrkMembrInfo[0].ReputationPoints >= 35001 && NtwrkMembrInfo[0].ReputationPoints <= 50000)
                CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_16;
            if(NtwrkMembrInfo[0].ReputationPoints >= 50001 && NtwrkMembrInfo[0].ReputationPoints <= 60000)
                CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_17;
                if(NtwrkMembrInfo[0].ReputationPoints >= 60001 && NtwrkMembrInfo[0].ReputationPoints <= 80000)
                CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_18;
            if(NtwrkMembrInfo[0].ReputationPoints >= 80001 && NtwrkMembrInfo[0].ReputationPoints <= 90000)
                CurrentLevel =  CommunityConstants_C_HUM.GAMIFICATION_LEVEL_19;
        }    
        
        List<ReputationLevel> ReputationLevelList = [SELECT Id, ParentId, Label, Threshold, LevelNumber FROM ReputationLevel where ParentId=: NetwrkId ];
        
        for(ReputationLevel eachRepLevel:ReputationLevelList){
            if(!String.isBlank(eachRepLevel.Label)) 
                ReputationLevelMap.put(eachRepLevel.Label,eachRepLevel);
        }
        
        //Map of NextLevel mapping with the current level        
        NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_1,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_2);
        NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_2,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_3);
        NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_3,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_4);
        NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_4,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_5);
        NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_5,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_6);
        NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_6,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_7);
        NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_7,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_8);
        NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_8,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_9);
        NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_9,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_10);
        if(NetworkName.equalsIgnorecase(CommunityConstants_C_HUM.COMMUNITY_GO365_COMMERCIAL)){
            NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_10,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_11);
            NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_11,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_12);
            NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_12,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_13);
            NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_13,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_14);
            NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_14,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_15);
            NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_15,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_16);
            NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_16,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_17);
            NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_17,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_18);
            NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_18,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_19);
            NextLevelMappings.put(CommunityConstants_C_HUM.GAMIFICATION_LEVEL_19,CommunityConstants_C_HUM.GAMIFICATION_LEVEL_20);
            
        }
        
        String nextLevelName =NextLevelMappings.get(CurrentLevel);
        ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_NEXT_LEVEL,nextLevelName);
        ReputationLevel RepLevel= (ReputationLevel) ReputationLevelMap.get(nextLevelName);
        Decimal nextLevelPoints = RepLevel.Threshold;
        List<ChatterActivity> UserChatterActivity = [SELECT Id, NetworkId, PostCount, ParentId, CommentCount, LikeReceivedCount, CommentReceivedCount, InfluenceRawRank FROM ChatterActivity where ParentId=: userinfo.getUserId() and NetworkId=: NetwrkId limit 1];
        if(!UserChatterActivity.isEmpty())
            ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_USER_CHATTER ,UserChatterActivity[0]);
        else 
            ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_USER_CHATTER ,new ChatterActivity());
        
        //Calculation for the required actions to reach the next level
        Decimal RequiredPoints = nextLevelPoints - NtwrkMembrInfo[0].ReputationPoints;
        for(String ruleAction: PointsRuleMap.keySet()){
            if(ruleAction.equalsIgnoreCase(CommunityConstants_C_HUM.GAMIFICATION_RULE_ACTION_FEED_LIKE_SOMETHING )){
                Decimal LikeSomethingPoints = PointsRuleMap.get(ruleAction);
                
                Integer LikeSomethingCountRequired = (Integer) (RequiredPoints / LikeSomethingPoints);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_LIKE_SOMETHING_COUNT,LikeSomethingCountRequired);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_LIKE_SOMETHING_POINTS ,LikeSomethingPoints);
            }
            if(ruleAction.equalsIgnoreCase(CommunityConstants_C_HUM.GAMIFICATION_RULE_ACTION_FEED_ITEM_ANSWER )){
                Decimal AnswerAQuestionPoints = PointsRuleMap.get(ruleAction);

                Integer AnswerAQuestionCountRequired = (Integer) (RequiredPoints / AnswerAQuestionPoints);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_ANSWER_QUESTION_COUNT ,AnswerAQuestionCountRequired);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_ANSWER_QUESTION_POINTS ,AnswerAQuestionPoints);
            }
            if(ruleAction.equalsIgnoreCase(CommunityConstants_C_HUM.GAMIFICATION_RULE_ACTION_FEED_ITEM_QUESTION )){
                Decimal PostQuestionPoints = PointsRuleMap.get(ruleAction);
                
                Integer PostQuestionCountRequired = (Integer) (RequiredPoints / PostQuestionPoints);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_POST_QUESTION_COUNT ,PostQuestionCountRequired);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_POST_QUESTION_POINTS ,PostQuestionPoints);
            }
            if(ruleAction.equalsIgnoreCase(CommunityConstants_C_HUM.GAMIFICATION_RULE_ACTION_FEED_ITEM_WRITE_POST )){
                Decimal WriteAPostpoints = PointsRuleMap.get(ruleAction);
                
                Integer WriteAPostCountRequired = (Integer) (RequiredPoints / WriteAPostpoints);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_WRITE_POST_COUNT ,WriteAPostCountRequired);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_WRITE_POST_POINTS ,WriteAPostpoints);
            }
            if(ruleAction.equalsIgnoreCase(CommunityConstants_C_HUM.GAMIFICATION_RULE_ACTION_FEED_ITEM_RECEIVE_LIKE )){
                Decimal ReceiveALikePoints = PointsRuleMap.get(ruleAction);
                
                Integer ReceiveALikeCountRequired = (Integer) (RequiredPoints / ReceiveALikePoints);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_RECEIVE_LIKE_COUNT ,ReceiveALikeCountRequired);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_RECEIVE_LIKE_POINTS ,ReceiveALikePoints);
            }
            if(ruleAction.equalsIgnoreCase(CommunityConstants_C_HUM.GAMIFICATION_RULE_ACTION_FEED_ITEM_RECEIVE_COMMENT )){
                Decimal ReceiveACommentPoints = PointsRuleMap.get(ruleAction);
                
                Integer ReceiveACommentCountRequired = (Integer) (RequiredPoints / ReceiveACommentPoints);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_RECEIVE_COMMENT_COUNT ,ReceiveACommentCountRequired);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_RECEIVE_COMMENT_POINTS ,ReceiveACommentPoints);
            }
            if(ruleAction.equalsIgnoreCase(CommunityConstants_C_HUM.GAMIFICATION_RULE_ACTION_FEED_ITEM_WRITE_COMMENT )){
                Decimal WriteACommentPoints = PointsRuleMap.get(ruleAction);
                
                Integer WriteACommentCountRequired = (Integer) (RequiredPoints / WriteACommentPoints);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_WRITE_COMMENT_COUNT ,WriteACommentCountRequired);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_WRITE_COMMENT_POINTS ,WriteACommentPoints);
            }
            if(ruleAction.equalsIgnoreCase(CommunityConstants_C_HUM.GAMIFICATION_RULE_ACTION_FEED_ITEM_RECEIVE_ANSWER )){
                Decimal ReceiveAnAnswerPoints = PointsRuleMap.get(ruleAction);
                
                Integer ReceiveAnAnswerCountRequired = (Integer) (RequiredPoints / ReceiveAnAnswerPoints);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_RECEIVE_ANSWER_COUNT ,ReceiveAnAnswerCountRequired);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_RECEIVE_ANSWER_POINTS ,ReceiveAnAnswerPoints);
            }
            if(ruleAction.equalsIgnoreCase(CommunityConstants_C_HUM.GAMIFICATION_RULE_ACTION_FEED_ITEM_BEST_ANSWER )){
                Decimal YourAnswerMarkedBestPoints = PointsRuleMap.get(ruleAction);
                
                Integer YourAnswerMarkedBestCountRequired = (Integer) (RequiredPoints / YourAnswerMarkedBestPoints);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_BEST_ANSWER_COUNT ,YourAnswerMarkedBestCountRequired);
                ResponseMap.put(CommunityConstants_C_HUM.GAMIFICATION_BEST_ANSWER_POINTS ,YourAnswerMarkedBestPoints);
            }
            
        }
       }
       return ResponseMap;
    }
}