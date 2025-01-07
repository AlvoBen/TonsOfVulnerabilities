/*****************************************************************************************************************************************************************************
Apex Class Name : CommunityTestDataFactory_H_HUM
Version         : 1.0
Created On      : 7/01/2020
Function        : Keeps all test data creation in a single spot and call from test classes.
Modification Log: 
* Developer Name               Code Review                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Shreya Agrawal                                             7/01/2020               Community Migration Implementation 
**************************************************************************************************************************************************/

@isTest
public class CommunityTestDataFactory_H_HUM {
    /*
    * Method Name   :   createFeedItemPosts
    * Description   :   A number is passed in via parameter and a collection of FeedItem is returned.
    * Return Type   :   List<FeedItem>
    * Parameters    :   Integer,id
    */
    public static List<FeedItem> createFeedItemPosts(Integer numOfFeedItems,id CurrentCommunityId) {
        List<FeedItem> allFeedItems = new List<FeedItem>();
        try
        {
            //Create FeadItems           
            for(Integer x=0; x<numOfFeedItems;x++) {
                FeedItem post = new FeedItem(); 
                post.ParentId = userinfo.getuserid(); 
                post.Body = 'test@test.com'; 
                post.Title = 'Test data';
                post.Type = 'QuestionPost';
                post.NetworkScope = CurrentCommunityId;
                allFeedItems.add(post);
            }
            insert(allFeedItems);
            
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityTestDataFactory_H_HUM ', 'createFeedItemPosts');
        }
       return allFeedItems; 
    }
    
    /*
    * Method Name   :   createTestKnowledgeArticles
    * Description   :   A number is passed in via parameter and a collection of Knowledge Articles is returned.
    * Return Type   :   List<Knowledge__kav>
    * Parameters    :   Integer
    */
        public static List<Knowledge__kav> createTestKnowledgeArticles(Integer numOfArticles) {
            List<Knowledge__kav> allArticles = new List<Knowledge__kav>();
            try
            {
                //Create Knowledge Articles           
                for(Integer x=0; x < numOfArticles ;x++) {
                    Knowledge__kav kv = new Knowledge__kav(URLName = 'Apex-Test-URLName'+x,Title = 'Apex Test URLName'+x,auto_publish__c=true,IsVisibleInPkb=true,IsVisibleInCsp=true);
                    allArticles.add(kv);
                   } 
                insert allArticles;
                
            }catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'CommunityTestDataFactory_H_HUM ', 'createTestKnowledgeArticles');
            }
            return allArticles; 
        }
    
    /*
    * Method Name   :   createTestTopics
    * Description   :   A number is passed in via parameter and a collection of Topics is returned.
    * Return Type   :   List<Topic>
    * Parameters    :   Integer,id
    */
        public static List<Topic> createTestTopics(Integer numOfTopics,Id CommunityId) {
            List<Topic> allTopics = new List<Topic>();
            try
            {
                //Create Topic          
                for(Integer x=0; x < numOfTopics ;x++) {
                    Topic newTopic = new Topic(Name = 'Test'+x,NetworkId = CommunityId);
                    allTopics.add(newTopic);
                   } 
                insert allTopics;
             }catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'CommunityTestDataFactory_H_HUM ', 'createTestTopics');
            }
            return allTopics; 
        }
     /*
    * Method Name   :   createTopicAssignment
    * Description   :   A number is passed in via parameter and a collection of TopicsAssignment Records is returned.
    * Return Type   :   List<TopicAssignment>
    * Parameters    :   Integer,Id,Id
    */
        public static List<TopicAssignment> createTopicAssignment(Integer numOfTopicAssignments,Id ArticleId,Id topicId) {
            List<TopicAssignment> allTopicAssignments = new List<TopicAssignment>();
            try
            {
                //Create Topic          
                for(Integer x=0; x < numOfTopicAssignments ;x++) {
                    TopicAssignment TopAssign = new TopicAssignment(EntityId = ArticleId,TopicId=topicId);
                    allTopicAssignments.add(TopAssign);
                   } 
                insert allTopicAssignments;
             }catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'CommunityTestDataFactory_H_HUM ', 'createTopicAssignment');
            }
            return allTopicAssignments; 
        }
    
     /*
    * Method Name   :   createArticleBookmark
    * Description   :   A number is passed in via parameter and a collection of Article Bookmarked Records is returned.
    * Return Type   :   List<ArticleBookmark__c>
    * Parameters    :   Integer,Id
    */
        public static List<ArticleBookmark__c> createArticleBookmark(Integer numOfBookmrkRec,Id ArticleId) {
            List<ArticleBookmark__c> allArticleBookmarks = new List<ArticleBookmark__c>();
            try
            {
                //Create Topic          
                for(Integer x=0; x < numOfBookmrkRec ;x++) {
                    ArticleBookmark__c BkMrkRec = new ArticleBookmark__c(ArticleId__c = ArticleId,Bookmarked__c=true);
                    allArticleBookmarks.add(BkMrkRec);
                   } 
                insert allArticleBookmarks;
             }catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'CommunityTestDataFactory_H_HUM ', 'createArticleBookmark');
            }
            return allArticleBookmarks; 
        }

    
    /*
    * Method Name   :   createArticleAttachment
    * Description   :   A number is passed in via parameter and a collection of ContentDocumentLink Records is returned.
    * Return Type   :   List<ContentDocumentLink>
    * Parameters    :   Integer,Id
    */
    public static List<ContentDocumentLink> createArticleAttachment(Integer numOfDocLinks,Id ArticleId) {
        List<ContentDocumentLink> allConDocLinks = new List<ContentDocumentLink>();
        try
        {
            //Create Content Version          
            ContentVersion contentVersion = new ContentVersion(
                Title = 'Penguins',
                PathOnClient = 'Penguins.jpg',
                VersionData = Blob.valueOf('Test Content'),
                IsMajorVersion = true
            );
            insert contentVersion;   
            
            List<ContentDocument> documents = [SELECT Id, Title, LatestPublishedVersionId FROM ContentDocument];
            
            //create ContentDocumentLink  record 
            for(Integer x=0; x < numOfDocLinks ;x++) { 
                ContentDocumentLink cdl = New ContentDocumentLink();
                cdl.LinkedEntityId = ArticleId;
                cdl.ContentDocumentId = documents[0].Id;
                cdl.shareType = 'V';
                allConDocLinks.add(cdl);
            }
            insert allConDocLinks;
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityTestDataFactory_H_HUM ', 'createArticleAttachment');
        }
        return allConDocLinks; 
    }
    
    
    /*
    * Method Name   :   createTestTopicWithName
    * Description   :   Name of the Topic is passed in via parameter and the created Topic is returned.
    * Return Type   :   Topic
    * Parameters    :   STring,id
    */
        public static Topic createTestTopicWithName(String TopicName,Id CommunityId) {
            Topic newTopic=new Topic();
            try
            {
                //Create Topic          
                newTopic = new Topic(Name = TopicName,NetworkId = CommunityId);
                insert newTopic;
             }catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'CommunityTestDataFactory_H_HUM ', 'createTestTopicWithName');
            }
            return newTopic; 
        }
    
    /*
    * Method Name   :   createTestKnowledgeArticlesUnpublished
    * Description   :   A number is passed in via parameter and a collection of Knowledge Articles is returned.
    * Return Type   :   List<Knowledge__kav>
    * Parameters    :   Integer
    */
        public static List<Knowledge__kav> createTestKnowledgeArticlesUnpublished(Integer numOfArticles) {
            List<Knowledge__kav> allArticles = new List<Knowledge__kav>();
            try
            {
                //Create Knowledge Articles           
                for(Integer x=0; x < numOfArticles ;x++) {
                    Knowledge__kav kv = new Knowledge__kav(URLName = 'Apex-Test-URLName'+x,Title = 'Apex Test URLName'+x,IsVisibleInPkb=true,IsVisibleInCsp=true);
                    allArticles.add(kv);
                   } 
                insert allArticles;
                
            }catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'CommunityTestDataFactory_H_HUM ', 'createTestKnowledgeArticlesUnpublished');
            }
            return allArticles; 
        }
    
    
   /*
    * Method Name   :   createTestCommunitySetup
    * Description   :   A record type is passed in via string parameter and a collection of Community Setup is returned.
    * Return Type   :   List<Community_Setup__c>
    * Parameters    :   String
    */
        public static List<Community_Setup__c> createTestCommunitySetup(String recordtypename) {
            List<Community_Setup__c> lstSetup= new List<Community_Setup__c>();
            Community_Setup__c objSetup1 = new Community_Setup__c();
            Community_Setup__c objSetup2 = new Community_Setup__c();
            
            Id recType1 = Schema.SObjectType.Community_Setup__c.getRecordTypeInfosbyDeveloperName().get(CommunityConstants_C_HUM.TERMS_AND_CONDITIONS_RECORDTYPE).getRecordTypeId();
            Id recType2 = Schema.SObjectType.Community_Setup__c.getRecordTypeInfosbyDeveloperName().get(CommunityConstants_C_HUM.ANNOUNCEMENT_RECORDTYPE).getRecordTypeId();
            Id recType3 = Schema.SObjectType.Community_Setup__c.getRecordTypeInfosbyDeveloperName().get(CommunityConstants_C_HUM.DIRECT_MESSAGE_RECORDTYPE).getRecordTypeId();
            Id recType4 = Schema.SObjectType.Community_Setup__c.getRecordTypeInfosbyDeveloperName().get(CommunityConstants_C_HUM.BANNER_IMAGE_RECORDTYPE).getRecordTypeId();
            
            try
            {
                //Create set up records 
                if (recordtypename=='Announcement'){
                    objSetup1.Name='setup 1';
                    objSetup1.Announcement_Title__c = 'test1';
                    objSetup1.Announcement_Description__c = 'test1 description';
                    objSetup1.Is_Announcement_Active__c = true;
                    objSetup1.recordtypeId = recType2;
                    lstSetup.add(objSetup1);
                }
                else if  (recordtypename=='Direct Message') {
                    objSetup1.Name = 'setup 2';
                    objSetup1.Is_Active_Moderator__c= true;
                    objSetup1.recordtypeid=recType3 ;
                    lstSetup.add(objSetup1);
                }
                else if  (recordtypename=='Terms and Conditions') {
                    objSetup1.Name = 'setup 3';
                    objSetup1.Terms_and_Conditions__c = 'Test terms and conditions';
                    objSetup1.Community_Name__c = 'humanasupport';
                    objSetup1.recordtypeid = recType1 ;
                    lstSetup.add(objSetup1);
                }
                else if  (recordtypename=='Community Banner Image') {
                    objSetup1.Name = 'setup 4';
                    objSetup1.Topic_Name__c = 'Test0';
                    objSetup1.Topic_Image__c = '<p>Test Image</p>';
                    objSetup1.Community_Name__c = 'humanasupport';
                    objSetup1.recordtypeid=recType4 ;
                    lstSetup.add(objSetup1);
                }
                
            }catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'CommunityTestDataFactory_H_HUM ', 'createTestCommunitySetup');
            }
            return lstSetup; 
        }
    
}