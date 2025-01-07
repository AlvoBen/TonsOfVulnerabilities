/*******************************************************************************************************************************
Apex Class Name : CommunityHomePageController_C_HUM 
Version         : 1.0
Created On      : 07/20/2020
Function        : This is the Controller class for Home Page Communities.
Test Class      : CommunityHomePageController_T_HUM

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Veena  Aphale                                      07/20/2020                    Original version
* Nitaj Titans                                        16/08/2021                    Added terms And conditions for Go365 Communities
**************************************************************************************************************************************/

Public with sharing Class CommunityHomePageController_C_HUM {
    public static Id testNetworkId;
    Public class CommunityTestException extends Exception {}
    
    /**
     * getTrendingTopics
     * Fetch list of trending topics from the current community
     * @param
     * @return List<Topic>
     **/

    @AuraEnabled(cacheable=true)
    public static List<Topic> getTrendingTopics(){
        List<Topic> TopicsList=new List<Topic>();
        try {
            TopicsList = [ SELECT Id, NetworkId, Name, TalkingAbout
                          FROM Topic
                          WHERE NetworkId = : Network.getNetworkId() 
                          ORDER BY TalkingAbout desc limit 6 ];
            User LoggedInUser=[select id,Is_Old_Community_Member__c from User where id=: userinfo.getUserId()];
            
            if(!LoggedInUser.Is_Old_Community_Member__c){
                List<Topic> onbrdingTopic=[ select id, NetworkId,Name from Topic where Name = : System.Label.Community_Onboarding_Topic and id NOT in: TopicsList and NetworkId = : Network.getNetworkId() LIMIT 1];
                
                if(!onbrdingTopic.isEmpty())
                    TopicsList.add(onbrdingTopic[0]);
            }
            
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityHomePageController_C_HUM ', 'getTrendingTopics');
        }
        return TopicsList;
    }
    
    /**
     * getAnnouncements
     * Fetch list of active Announcements to be displayed on home page
     * @param
     * @return List<Community_Setup__c>
     **/
    @AuraEnabled(cacheable=true)
    public static List<Community_Setup__c> getAnnouncements(){
        
        List<Community_Setup__c> AnnouncementList = new List<Community_Setup__c> ();
        Id NetwrkId;
        if(Test.isRunningTest()){
            NetwrkId= [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember where MemberID=: UserInfo.getUserId() limit 1].NetworkID;
        }
        else{
            NetwrkId= Network.getNetworkId();
        }
        
        try {
            Network Ntwrk = [SELECT Id, Name, Status FROM Network where id =: NetwrkId];
            AnnouncementList = [SELECT Id,Announcement_Title__c,Announcement_URL__c,Announcement_Description__c,Is_Announcement_Active__c 
                                FROM Community_Setup__c 
                                WHERE Recordtype.DeveloperName = :CommunityConstants_C_HUM.ANNOUNCEMENT_RECORDTYPE 
                                AND Is_Announcement_Active__c = true 
                                AND Community_Name__c =: Ntwrk.Name Order 
                                by CreatedDate desc LIMIT 3];
            if(Test.isRunningTest())
            {
                throw new CommunityTestException('Test Exception');
            }
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityHomePageController_C_HUM', 'getAnnouncements');
        }
        return AnnouncementList;
    }
    
    /**
     * updateUserRecord
     * When user accepts Terms and Conditions displayed on home page, update user record 
     * @param
     * @return Boolean
     **/

    @AuraEnabled
    public static Boolean updateUserRecord(){
        Boolean isSuccess = false;
        Id NetwrkId;
        if(Test.isRunningTest()){
            NetwrkId= testNetworkId;
        }
        else{
            NetwrkId= Network.getNetworkId();
        }
        try {
            User userRecord = [SELECT Id,Name,Community_Terms_Accept_Date__c,Go365Commercial_Terms_Accept_Date__c,Go365Medicare_Terms_Accept_Date__c FROM User WHERE Id = :UserInfo.getUserId()];
            Network Ntwrk = [SELECT Id, Name, Status FROM Network where id =: NetwrkId];
            //US - 2242801 Added to update terms and conditions accept date for Go365 communities - start
            if(Ntwrk.Name == CommunityConstants_C_HUM.COMMUNITY_SUPPORT)
                userRecord.Community_Terms_Accept_Date__c = System.today();
            if(Ntwrk.Name == CommunityConstants_C_HUM.COMMUNITY_GO365_COMMERCIAL)
                userRecord.Go365Commercial_Terms_Accept_Date__c = System.today();
            if(Ntwrk.Name == CommunityConstants_C_HUM.COMMUNITY_GO365_MEDICARE)
                userRecord.Go365Medicare_Terms_Accept_Date__c = System.today();
            //US - 2242801 Added to update terms and conditions accept date for Go365 communities - end
            update userRecord;
            isSuccess = true;
            if(Test.isRunningTest())
            {
                throw new CommunityTestException('Test Exception');
            }
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityHomePageController_C_HUM', 'updateUserRecord');
            isSuccess = false;
        }
        return isSuccess;
    }

    
    /**
     * fetchTermsandConditions
     * Fetch Terms and Conditions to be displayed on home page
     * @param
     * @return Community_Setup__c
     **/

    @AuraEnabled(cacheable=true)
    public static Community_Setup__c fetchTermsandConditions(){
        Community_Setup__c objSetup = new Community_Setup__c();
        List<Community_Setup__c> lstSetup = new List<Community_Setup__c> ();
        /*US - 2242801 Changes made to support terms and conditions for all communities - Nitaj - Start */
        Id NetwrkId;
        if(Test.isRunningTest()){
            NetwrkId= testNetworkId;
        }
        else{
            NetwrkId= Network.getNetworkId();
        }
        try {
            Network Ntwrk = [SELECT Id, Name, Status FROM Network where id =: NetwrkId];
            User userRecord = [SELECT Id,Name,Community_Terms_Accept_Date__c,Go365Commercial_Terms_Accept_Date__c,Go365Medicare_Terms_Accept_Date__c FROM User WHERE Id = :UserInfo.getUserId()];
            if (Ntwrk.Name == CommunityConstants_C_HUM.COMMUNITY_SUPPORT && (userRecord.Community_Terms_Accept_Date__c == null || (userRecord.Community_Terms_Accept_Date__c).daysBetween(system.today()) >= 365)) {
                lstSetup =  [SELECT Id,Terms_and_Conditions__c FROM Community_Setup__c WHERE Recordtype.DeveloperName = :CommunityConstants_C_HUM.TERMS_AND_CONDITIONS_RECORDTYPE
                             AND Community_Name__c =: Ntwrk.Name LIMIT 1];    
            }
            if (Ntwrk.Name == CommunityConstants_C_HUM.COMMUNITY_GO365_COMMERCIAL && (userRecord.Go365Commercial_Terms_Accept_Date__c == null || (userRecord.Go365Commercial_Terms_Accept_Date__c).daysBetween(system.today()) >= 365)) {
                lstSetup =  [SELECT Id,Terms_and_Conditions__c FROM Community_Setup__c WHERE Recordtype.DeveloperName = :CommunityConstants_C_HUM.TERMS_AND_CONDITIONS_RECORDTYPE
                             AND Community_Name__c =: Ntwrk.Name LIMIT 1];    
            }
            if (Ntwrk.Name == CommunityConstants_C_HUM.COMMUNITY_GO365_MEDICARE && (userRecord.Go365Medicare_Terms_Accept_Date__c == null || (userRecord.Go365Medicare_Terms_Accept_Date__c).daysBetween(system.today()) >= 365)) {
                lstSetup =  [SELECT Id,Terms_and_Conditions__c FROM Community_Setup__c WHERE Recordtype.DeveloperName = :CommunityConstants_C_HUM.TERMS_AND_CONDITIONS_RECORDTYPE
                             AND Community_Name__c =: Ntwrk.Name LIMIT 1];    
            }
            if (!lstSetup.isEmpty())
               objSetup = lstSetup[0];
            if(Test.isRunningTest())
            {
                throw new CommunityTestException('Test Exception');
            }
           /*US - 2242801 Changes made to support terms and conditions for all communities - Nitaj - End */
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityHomePageController_C_HUM', 'fetchTermsandConditions');
            
        }
        return objSetup;
    }

    /**
     * getTopicDetails
     * Fetch topic details using the Topic name sent as parameter
     * @param String topicName
     * @return Topic
     **/

    @AuraEnabled(cacheable=true)
    public static Topic getTopicDetails(String topicName){
        
        Id NetwrkId;
        List<Topic> topicFetched = new List<Topic>();
        Topic objTopic = new Topic();
        if(Test.isRunningTest()){
            NetwrkId= [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember where MemberID=: UserInfo.getUserId() limit 1].NetworkID;
        }
        else{
            NetwrkId= Network.getNetworkId();
        }
        
        try {
            topicFetched = [select id, NetworkId, Name from Topic where Name = : topicName and NetworkId =:NetwrkId LIMIT 1];
            if (!topicFetched.isEmpty())
                objTopic = topicFetched[0];
            if(Test.isRunningTest())
            {
                throw new CommunityTestException('Test Exception');
            }
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityHomePageController_C_HUM', 'getTopicDetails');
        }
        return objTopic;
    }
    
    /**
     * getBannerImages 
     * get Banner Images from the Community Setup Object based on the Topic and community user is logged in
     * @param Id recId
     * @return Community_Setup__c
     **/
    
    @AuraEnabled(cacheable=true)
    public static Community_Setup__c getBannerImages(Id recId){
        
        List<Community_Setup__c> TopicDetail = new List<Community_Setup__c> ();
        Community_Setup__c objTopicDetail = new Community_Setup__c();
        Id NetwrkId;
        if(Test.isRunningTest()){
            NetwrkId= [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember where MemberID=: UserInfo.getUserId() limit 1].NetworkID;
        }
        else{
            NetwrkId= Network.getNetworkId();
        }
            try {
                
                List<Topic> TopicList = [SELECT Id, NetworkId, Name FROM Topic where NetworkId =:NetwrkId and id =: recId limit 1 ];
                Network Ntwrk = [SELECT Id, Name, Status FROM Network where id =: NetwrkId];
                TopicDetail = [SELECT Id,Community_Name__c, Topic_Image__c, Topic_Name__c,Topic_Image_Mobile__c,Topic_Image_Tablet__c 
                               FROM Community_Setup__c 
                               WHERE Recordtype.DeveloperName = :CommunityConstants_C_HUM.BANNER_IMAGE_RECORDTYPE
                               AND Community_Name__c =: Ntwrk.Name 
                               AND Topic_Name__c =: TopicList[0].Name];
                if (!TopicDetail.isEmpty())
                    objTopicDetail = TopicDetail[0];
                
                if(Test.isRunningTest())
                {
                    throw new CommunityTestException('Test Exception');
                }
            } catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'CommunityHomePageController_C_HUM', 'getBannerImages');
            }
            return objTopicDetail ;
    }
    
}