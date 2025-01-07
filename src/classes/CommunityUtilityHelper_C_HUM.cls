/*******************************************************************************************************************************
Apex Class Name : CommunityUtilityHelper_C_HUM 
Version         : 1.0
Created On      : 07/20/2020
Function        : This is a utility class for generic methods used across components.
Test Class      : 

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Veena  Aphale                                      07/20/2020                    Original version
* Shreya Agrawal                                      08/05/2020                    Added method getCurrentUserProfileName to fetch the profile name of the logged in user
**************************************************************************************************************************************/


public with sharing class CommunityUtilityHelper_C_HUM {

    /**
     * getRecommendedWatchArticles
     * Fetch the recommended Articles/Videos based on topics , vote stats
     * @param String recId,String source,String articleType
     * @return List<Knowledge__kav>
     **/
    
    public List<Knowledge__kav> getRecommendedWatchArticles(String recId,String source,String articleType){
        List<Knowledge__VoteStat> topArticles = new List<Knowledge__VoteStat> ();
        Set<Id> setTopicIds = new Set<Id>();
        Set<Id> setArticleIds = new Set<Id>();
        List<Knowledge__kav> kv = new List<Knowledge__kav>();
        List<Knowledge__kav> topKnowledgeArticles = new List<Knowledge__kav> ();
        List<Knowledge__kav> lstKnowledgeArticles = new List<Knowledge__kav> ();
        List<Knowledge__kav> lstTempKnowledgeArticles = new List<Knowledge__kav>();
        List<TopicAssignment> topicsAssign = new List<TopicAssignment> ();
        List<Topic> lstTopic = new List<Topic>();
        Integer counter=0;
        Set<Id> helpfulArticleIds = new Set<Id> ();
        Boolean boolTopics = false;
        List<TopicAssignment> homeTopicsAssign = new List<TopicAssignment> ();
        Set<Id> setOnboardingArticles = new Set<Id> ();
        List<Knowledge__kav> lstOnboardingArticles = new List<Knowledge__kav> ();
        List<Knowledge__kav> lstOnboardingVideos = new List<Knowledge__kav> ();
        List<Knowledge__kav> lstOnboardingKnowledge = new List<Knowledge__kav> ();
        
        Id NetwrkId;
        if(Test.isRunningTest()){
            NetwrkId= [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember where MemberID=: UserInfo.getUserId() limit 1].NetworkID;
        }
        else{
            NetwrkId= Network.getNetworkId();
        }
        
        try{
            // Get original Feed using recId (or urlname)
            if (source.equalsIgnoreCase(CommunityConstants_C_HUM.FEED_DETAIL) || source.equalsIgnoreCase(CommunityConstants_C_HUM.QUESTION_DETAIL)) {
                topicsAssign = [SELECT Id, TopicId, Topic.name,EntityId FROM TopicAssignment where EntityId  =: recId ];
                
            } else if (source.equalsIgnoreCase(CommunityConstants_C_HUM.ARTICLE_DETAIL)) {
                kv = [ SELECT Id, Title, Article_Body__c,UrlName FROM Knowledge__kav where UrlName =: recId LIMIT 1];
                topicsAssign = [SELECT Id, TopicId, Topic.name,EntityId FROM TopicAssignment where EntityId  =: kv[0].Id ];
            } else if (source.equalsIgnoreCase(CommunityConstants_C_HUM.HOME_PAGE)) {
                
                lstTopic = [SELECT Id FROM Topic WHERE NetworkId = : NetwrkId 
                            order by TalkingAbout desc limit :CommunityConstants_C_HUM.QUERY_LIMIT2 ];
                User userRecord = [Select Id,Is_Old_Community_Member__c from User WHERE Id=:UserInfo.getUserId()];
                // If User is new member, show Onboarding Knowledge Articles
                if (!userRecord.Is_Old_Community_Member__c) { 
                    //get topic assignments for onboarding topic
                    homeTopicsAssign = [ SELECT Topic.name, EntityId,EntityType,NetworkId FROM TopicAssignment WHERE  Topic.Name = : System.Label.Community_Onboarding_Topic 
                                        AND EntityType= 'Knowledge' AND NetworkId = : NetwrkId AND EntityId in (SELECT Id FROM Knowledge__kav WHERE PublishStatus = 'Online') ];
                    if (!homeTopicsAssign.isEmpty()) {
                        for (TopicAssignment  ta1 : homeTopicsAssign)
                            setOnboardingArticles.add(ta1.EntityId); 
                        //get Onboarding articles or videos        
                        if (articleType.equalsIgnoreCase(CommunityConstants_C_HUM.ARTICLE))
                            lstOnboardingKnowledge = [SELECT Id, Title, UrlName, Summary,Top_Rating_Article__c, ArticleCreatedDate, ArticleTotalViewCount 
                                                      FROM Knowledge__kav
                                                      WHERE PublishStatus = 'Online' 
                                                      AND Id in: setOnboardingArticles 
                                                      AND Top_Rating_Article__c = true
                                                      AND Article_Has_Video__c = false  LIMIT :CommunityConstants_C_HUM.QUERY_LIMIT2];
                        else if (articleType.equalsIgnoreCase(CommunityConstants_C_HUM.VIDEO))
                            lstOnboardingKnowledge = [SELECT Id, Title, UrlName, Summary,Top_Rating_Article__c, ArticleCreatedDate, ArticleTotalViewCount  
                                                      FROM Knowledge__kav
                                                      WHERE PublishStatus = 'Online' 
                                                      AND Id in: setOnboardingArticles 
                                                      AND Article_Has_Video__c = true 
                                                      AND Top_Rating_Article__c = true  LIMIT :CommunityConstants_C_HUM.QUERY_LIMIT2];
                        
                    }
                    lstKnowledgeArticles.addAll(lstOnboardingKnowledge); 
                    lstTempKnowledgeArticles = new List<Knowledge__kav>();
                    lstTempKnowledgeArticles = lstKnowledgeArticles;
                }
                if (!lstTopic.isEmpty()) {
                    for(Topic obj:lstTopic)
                        setTopicIds.add(obj.Id);
                    
                    boolTopics = true;
                }
            }
            
            if (!topicsAssign.isEmpty()) {
                for (TopicAssignment ta : topicsAssign) {
                        setTopicIds.add(ta.TopicId);
                    }
            }
            List<Topic> genericTopic=[ select id, NetworkId,Name from Topic where Name =: System.Label.Community_Generic_Topic and NetworkId=: NetwrkId limit 1];
                if(!genericTopic.isEmpty()){
                        setTopicIds.add(genericTopic[0].Id); 
            }
            
            //Get Articles assigned for related Topics   
            if (!setTopicIds.isEmpty()) {
                
                // Get all articles related to all Topics
                List<TopicAssignment> articleTopicsAssign = [ SELECT Id, TopicId, Topic.name, EntityId,EntityType,NetworkId 
                                                             FROM TopicAssignment 
                                                             WHERE  TopicId in :setTopicIds 
                                                             AND EntityType=: 'Knowledge' 
                                                             AND NetworkId = :  NetwrkId
                                                             AND EntityId in (SELECT Id FROM Knowledge__kav
                                                                              WHERE PublishStatus = 'Online' AND Id NOT in :lstTempKnowledgeArticles) ];
                
                if (!articleTopicsAssign.isEmpty()) {
                    for (TopicAssignment  ta1 : articleTopicsAssign)
                        setArticleIds.add(ta1.EntityId); 
                    
                    if (lstKnowledgeArticles.isEmpty() || lstKnowledgeArticles.size() < 6){
                        Integer limit1 = (lstKnowledgeArticles.isEmpty())? 6 : 6 - lstKnowledgeArticles.size();
                        // if Feed detail or Question detail get all associated top-rated  articles (Video and Non-video) 
                        if (source.equalsIgnoreCase(CommunityConstants_C_HUM.FEED_DETAIL) || source.equalsIgnoreCase(CommunityConstants_C_HUM.QUESTION_DETAIL) || source.equalsIgnoreCase(CommunityConstants_C_HUM.HOME_PAGE)){ 
                            if (articleType.equalsIgnoreCase(CommunityConstants_C_HUM.ARTICLE))
                                topKnowledgeArticles = [SELECT Id, Title, UrlName, Summary,Top_Rating_Article__c, ArticleCreatedDate, ArticleTotalViewCount 
                                                        FROM Knowledge__kav
                                                        WHERE PublishStatus = 'Online' 
                                                        AND Id in: setArticleIds 
                                                        AND Top_Rating_Article__c = true
                                                        AND Article_Has_Video__c = false  LIMIT :limit1];
                            else if (articleType.equalsIgnoreCase(CommunityConstants_C_HUM.VIDEO))
                                topKnowledgeArticles = [SELECT Id, Title, UrlName, Summary,Top_Rating_Article__c, ArticleCreatedDate, ArticleTotalViewCount  
                                                        FROM Knowledge__kav
                                                        WHERE PublishStatus = 'Online' 
                                                        AND Id in: setArticleIds 
                                                        AND Article_Has_Video__c = true 
                                                        AND Top_Rating_Article__c = true  LIMIT :limit1];
                        } // else if Article detail get all associated top-rated articles (Video and Non-video) except the current one
                        else if (source.equalsIgnoreCase(CommunityConstants_C_HUM.ARTICLE_DETAIL)) {     
                            if (articleType.equalsIgnoreCase(CommunityConstants_C_HUM.ARTICLE))
                                topKnowledgeArticles = [SELECT Id, Title, UrlName, Summary,Top_Rating_Article__c, ArticleCreatedDate, ArticleTotalViewCount 
                                                        FROM Knowledge__kav
                                                        WHERE PublishStatus = 'Online' 
                                                        AND Id in: setArticleIds
                                                        AND Id <> :kv[0].Id 
                                                        AND Top_Rating_Article__c = true
                                                        AND Article_Has_Video__c = false  LIMIT :limit1];
                            else if (articleType.equalsIgnoreCase(CommunityConstants_C_HUM.VIDEO))
                                topKnowledgeArticles = [SELECT Id, Title, UrlName, Summary,Top_Rating_Article__c, ArticleCreatedDate, ArticleTotalViewCount  
                                                        FROM Knowledge__kav
                                                        WHERE PublishStatus = 'Online' 
                                                        AND Id in: setArticleIds 
                                                        AND Id <> :kv[0].Id
                                                        AND Article_Has_Video__c = true 
                                                        AND Top_Rating_Article__c = true  LIMIT :limit1];
                            
                        }
                    }
                    if (!topKnowledgeArticles.isEmpty()) {
                        lstKnowledgeArticles.addAll(topKnowledgeArticles); 
                        lstTempKnowledgeArticles = new List<Knowledge__kav>();
                        lstTempKnowledgeArticles = lstKnowledgeArticles;
                    }    
                    
                    if (lstKnowledgeArticles.isEmpty() || lstKnowledgeArticles.size() < 6) { // if less than 6 articles are found
                        Integer limit1 = (lstKnowledgeArticles.isEmpty())? 6 : 6 - lstKnowledgeArticles.size();
                        
                        topArticles = [ Select parentId, NormalizedScore from Knowledge__VoteStat 
                                       WHERE parentid in  (SELECT KnowledgeArticleId FROM Knowledge__kav
                                                           WHERE publishStatus = 'Online' and Id in: setArticleIds and Id NOT in :lstTempKnowledgeArticles) order by NormalizedScore DESC LIMIT :limit1];
                        if (!topArticles.isEmpty()) 
                            for (Knowledge__VoteStat kv1: topArticles)
                            helpfulArticleIds.add(kv1.parentId);
                        
                        List<Knowledge__kav> lstKnowledgeKav = new List<Knowledge__kav> ();
                        // if Feed detail or Question detail get all associated articles from VoteStat table (Video and Non-video) 
                        if (source.equalsIgnoreCase(CommunityConstants_C_HUM.FEED_DETAIL) || source.equalsIgnoreCase(CommunityConstants_C_HUM.QUESTION_DETAIL) || source.equalsIgnoreCase(CommunityConstants_C_HUM.HOME_PAGE)){ 
                            if (articleType.equalsIgnoreCase(CommunityConstants_C_HUM.ARTICLE))
                                lstKnowledgeKav = [Select Id, Title, UrlName, Summary, ArticleCreatedDate,ArticleTotalViewCount from Knowledge__kav where Id in : helpfulArticleIds AND Article_Has_Video__c = false LIMIT :limit1];
                            else if (articleType.equalsIgnoreCase(CommunityConstants_C_HUM.VIDEO))
                                lstKnowledgeKav = [Select Id, Title, UrlName, Summary, ArticleCreatedDate,ArticleTotalViewCount from Knowledge__kav where Id in : helpfulArticleIds AND Article_Has_Video__c = true LIMIT :limit1];
                        } // if Feed detail or Question detail get all associated articles from VoteStat table (Video and Non-video) except the current one
                        else if (source.equalsIgnoreCase(CommunityConstants_C_HUM.ARTICLE_DETAIL)) { 
                            if (articleType.equalsIgnoreCase(CommunityConstants_C_HUM.ARTICLE)){
                                lstKnowledgeKav = [Select Id, Title, UrlName, Summary, ArticleCreatedDate,ArticleTotalViewCount from Knowledge__kav where Id in : helpfulArticleIds AND Article_Has_Video__c = false AND Id <> :kv[0].Id  LIMIT :limit1];
                                
                            }
                            else if (articleType.equalsIgnoreCase(CommunityConstants_C_HUM.VIDEO))
                                lstKnowledgeKav = [Select Id, Title, UrlName, Summary, ArticleCreatedDate,ArticleTotalViewCount from Knowledge__kav where Id in : helpfulArticleIds AND Article_Has_Video__c = true AND Id <> :kv[0].Id LIMIT :limit1];
                        }
                        lstKnowledgeArticles.addAll(lstKnowledgeKav);
                    }  // if less than 6 articles
                } 
            } // get Articles assigned for related topics
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityUtilityHelper_C_HUM ', 'getRecommendedWatchArticles');
        }
        
        return lstKnowledgeArticles;
    } // method
    

    /**
     * getGroups
     * Fetch the Groups which match the Topic name
     * @param String recId
     * @return List<CollaborationGroup>
     **/

    public List<CollaborationGroup> getGroups(String recId) {
        List<CollaborationGroup> lstGroup = new List<CollaborationGroup>();
        List<TopicAssignment> topicsAssign = new List<TopicAssignment> ();
        List<String> setTopicNames = new List<String> ();
        
        List<Topic> TopicList = new List<Topic> ();
        String [] topicsList = new String [] {};
            
            Id NetwrkId;
        if(Test.isRunningTest()){
            NetwrkId= [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember where MemberID=: UserInfo.getUserId() limit 1].NetworkID;
        }
        else{
            NetwrkId= Network.getNetworkId();
        }
        
        try { 
            // Recommended groups on Home Page
            if (String.isBlank(recId)) {
                TopicList = [ SELECT Id, NetworkId, Name, TalkingAbout
                             FROM Topic
                             WHERE NetworkId = : NetwrkId 
                             ORDER BY TalkingAbout desc limit :CommunityConstants_C_HUM.QUERY_LIMIT2 ];
                
                if (!TopicList.isEmpty())
                    for (Topic obj:TopicList)
                    setTopicNames.add('%'+obj.Name+'%');
                // If User is new member
                User userRecord = [Select Id,Is_Old_Community_Member__c from User WHERE Id=:UserInfo.getUserId()];
                if (!userRecord.Is_Old_Community_Member__c) {
                    Topic onboardingTopic=[ select id, NetworkId,Name from Topic where Name =: System.Label.Community_Onboarding_Topic limit 1];
                    setTopicNames.add('%'+onboardingTopic.Name+'%');
                }
                
            } else { // Popular groups on Feed / Question Detail Page
                String recordId=recId.remove('"');
                topicsAssign = [SELECT Id, TopicId, Topic.name,EntityId FROM TopicAssignment WHERE EntityId = :recordId ];
                
                if (!topicsAssign.isEmpty()){
                    for (TopicAssignment rec : topicsAssign)
                        setTopicNames.add('%'+rec.Topic.Name+'%');
                }
                List<Topic> genericTopic=[ select id, NetworkId,Name from Topic where Name =: System.Label.Community_Generic_Topic limit 1];
                if(!genericTopic.isEmpty()){
                    setTopicNames.add('%'+genericTopic[0].Name+'%');
                }
            }    
            if (!setTopicNames.isEmpty()){
                for (String str:setTopicNames)
                    topicsList.add(str);
                
                lstGroup = [SELECT Id,Name,MemberCount,IsArchived,NetworkId,CollaborationType FROM CollaborationGroup WHERE NetworkId = :NetwrkId AND  Name like :topicsList order by MemberCount DESC LIMIT :CommunityConstants_C_HUM.QUERY_LIMIT3];
            }
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityUtilityHelper_C_HUM ', 'getGroups');
        }  
        return lstGroup;
    } // method
    
    /**
     * getCurrentUserProfileName
     * Fetch the Groups which match the Topic name
     * @param Fetch the current User's profile details
     * @return String
     **/

    public static String getCurrentUserProfileName()
    {       
        Id profileId=userinfo.getProfileId();
        String profileName=[Select Id,Name from Profile where Id=:profileId].Name;
        return profileName;
    }
}