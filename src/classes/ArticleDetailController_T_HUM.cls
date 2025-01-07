/*******************************************************************************************************************************
Apex Class Name : ArticleDetailController_T_HUM 
Version         : 1.0
Created On      : 07/23/2020
Function        : This class is used for testing the class ArticleDetailController_C_HUM 

Modification Log: 
 * Developer Name           Code Review                     Date                            Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shreya Agrawal                                       07/23/2020                      Original version
 ****************************************************************************************************************************/

@isTest
public class ArticleDetailController_T_HUM {

     /*  
     * Method name : TestSetup
     * Description : Test setup method for the testclass
     * Return Type : void
     * Parameter   : none
     */
     
    @testSetup static void TestSetup(){
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS0824';
        oUser.UserPermissionsKnowledgeUser  = TRUE;
        oUser.UserRoleId = [SELECT Id, Name FROM UserRole WHERE name = 'System Account Role' LIMIT 1].Id;
        
        profile p= [select id from profile where Name='Humana Community Member'];
        
        Id recType = Schema.SObjectType.Account.getRecordTypeInfosbyDeveloperName().get('Member').getRecordTypeId();
        Account objAccount;
        system.runAs(oUser){
            objAccount = new Account();
            objAccount.FirstName='Test Firstname';
            objAccount.LastName='Test LastName';
            objAccount.RecordTypeId =recType;
            insert objAccount;  
            Account oAcc = [Select Id, PersonContactId,FirstName from Account where Id=:objAccount.Id];
            System.assertEquals(oAcc.FirstName, 'Test Firstname');
            
            User user = new User(alias = 'test123', email='test123@noemail.com',
                                 emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US',
                                 localesidkey='en_US', profileid = p.id, country='United States',IsActive =true,
                                 ContactId = oAcc.PersonContactId,
                                 timezonesidkey='America/Los_Angeles', username='tester@noemail.com');
            
            insert user;            
            System.assertEquals([SELECT Id,alias FROM User WHERE alias = 'test123' LIMIT 1].alias, 'test123');
        }
    }
    
     /*  
     * Method name : testGetArticle
     * Description : This method tests the method getArticle from class ArticleDetailController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testGetArticle(){        
        
        User oAdmin = [SELECT Id FROM User WHERE Network_User_Id__c = 'SXS0824' LIMIT 1];
        List<Knowledge__kav> allArticles;
        system.runAs(oAdmin){
            //Create a Knowledge Article
            allArticles = CommunityTestDataFactory_H_HUM.createTestKnowledgeArticles(1);
        }
        
        
        User oMem = [SELECT Id FROM User WHERE alias = 'test123' LIMIT 1];
        system.runAs(oMem){
            
            //Create topics
            List<Topic> allTopics = CommunityTestDataFactory_H_HUM.createTestTopics(1,Network.getNetworkId());
            //create TopicAssignment
            List<TopicAssignment> allTopicAssigns = CommunityTestDataFactory_H_HUM.createTopicAssignment(1,allArticles[0].id,allTopics[0].id);
            //Create an article Bookmark
            List<ArticleBookmark__c> allBookMarks = CommunityTestDataFactory_H_HUM.createArticleBookmark(1,allArticles[0].id);
            
            Knowledge__kav kv= [Select id,Title,UrlName from Knowledge__kav limit 1];
            Knowledge__kav kav=ArticleDetailController_C_HUM.getArticle(kv.UrlName);
            System.assertEquals(kv.Title, kav.Title);
        }
    } 
    
    /*  
     * Method name : testGetTopics
     * Description : This method tests the method getTopics from class ArticleDetailController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testGetTopics(){
        User oAdmin = [SELECT Id FROM User WHERE Network_User_Id__c = 'SXS0824' LIMIT 1];
        List<Knowledge__kav> allArticles;
        system.runAs(oAdmin){
            //Create a Knowledge Article
            allArticles = CommunityTestDataFactory_H_HUM.createTestKnowledgeArticles(1);
        }
        
        
        User oMem = [SELECT Id FROM User WHERE alias = 'test123' LIMIT 1];
        system.runAs(oMem){
            
            //Create topics
            List<Topic> allTopics = CommunityTestDataFactory_H_HUM.createTestTopics(1,Network.getNetworkId());
            //create TopicAssignment
            List<TopicAssignment> allTopicAssigns = CommunityTestDataFactory_H_HUM.createTopicAssignment(1,allArticles[0].id, allTopics[0].id);
            //Create an article Bookmark
            List<ArticleBookmark__c> allBookMarks = CommunityTestDataFactory_H_HUM.createArticleBookmark(1,allArticles[0].id);
            
            Knowledge__kav kv= [Select id,UrlName from Knowledge__kav limit 1];
            List<TopicAssignment> topicAssign = [ SELECT Id, Topic.Name,EntityId,TopicId FROM TopicAssignment where  EntityId =: kv.id limit 1];
            List<TopicAssignment> topicsList = ArticleDetailController_C_HUM.getTopics(kv.UrlName);
        }
    }
    
    /*  
     * Method name : testbookmarkController
     * Description : This method tests the method bookmarkController from class ArticleDetailController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testbookmarkController(){
        
        User oAdmin = [SELECT Id FROM User WHERE Network_User_Id__c = 'SXS0824' LIMIT 1];
        List<Knowledge__kav> allArticles;
        system.runAs(oAdmin){
            //Create a Knowledge Article
            allArticles = CommunityTestDataFactory_H_HUM.createTestKnowledgeArticles(1);
        }
        
        
        User oMem = [SELECT Id FROM User WHERE alias = 'test123' LIMIT 1];
        system.runAs(oMem){
            
            //Create topics
            List<Topic> allTopics = CommunityTestDataFactory_H_HUM.createTestTopics(1,Network.getNetworkId());
            //create TopicAssignment
            List<TopicAssignment> allTopicAssigns = CommunityTestDataFactory_H_HUM.createTopicAssignment(1,allArticles[0].id,allTopics[0].id);
            //Create an article Bookmark
            List<ArticleBookmark__c> allBookMarks = CommunityTestDataFactory_H_HUM.createArticleBookmark(1,allArticles[0].id);
            
            Knowledge__kav kv= [Select id,Title,UrlName from Knowledge__kav limit 1];
            ArticleDetailController_C_HUM.bookmarkController(kv.Id);
            ArticleDetailController_C_HUM.bookmarkController(kv.Id);
            boolean bookmark = ArticleDetailController_C_HUM.bookmarkController(kv.Id);
            System.assertEquals(true, bookmark);
        }
    }
    
    /*  
     * Method name : testGetKnowledgeFeedDetails
     * Description : This method tests the method getKnowledgeFeedDetails from class ArticleDetailController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testGetKnowledgeFeedDetails(){
        
        User oAdmin = [SELECT Id FROM User WHERE Network_User_Id__c = 'SXS0824' LIMIT 1];
        List<Knowledge__kav> allArticles;
        system.runAs(oAdmin){
            //Create a Knowledge Article
            allArticles = CommunityTestDataFactory_H_HUM.createTestKnowledgeArticles(1);
        }
        
        User oMem = [SELECT Id FROM User WHERE alias = 'test123' LIMIT 1];
        system.runAs(oMem){
            
            //Create topics
            List<Topic> allTopics = CommunityTestDataFactory_H_HUM.createTestTopics(1,Network.getNetworkId());
            //create TopicAssignment
            List<TopicAssignment> allTopicAssigns = CommunityTestDataFactory_H_HUM.createTopicAssignment(1,allArticles[0].id,allTopics[0].id);
            //Create an article Bookmark
            List<ArticleBookmark__c> allBookMarks = CommunityTestDataFactory_H_HUM.createArticleBookmark(1,allArticles[0].id);
            
            Knowledge__kav kv= [Select id,UrlName,ArticleNumber from Knowledge__kav limit 1];
            Map<String,Object> resMap = ArticleDetailController_C_HUM.getKnowledgeFeedDetails(kv.ArticleNumber);
            System.assertEquals(0, resMap.get('likesCount'));
        }
    }
    
    /*  
     * Method name : testGetRecommendations
     * Description : This method tests the method getRecommendations from class ArticleDetailController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testGetRecommendations(){
        
        User oAdmin = [SELECT Id FROM User WHERE Network_User_Id__c = 'SXS0824' LIMIT 1];
        List<Knowledge__kav> allArticles;
        system.runAs(oAdmin){
            //Create a Knowledge Article
            allArticles = CommunityTestDataFactory_H_HUM.createTestKnowledgeArticles(1);
        }
        
        User oMem = [SELECT Id FROM User WHERE alias = 'test123' LIMIT 1];
        system.runAs(oMem){
            
            //Create topics
            List<Topic> allTopics = CommunityTestDataFactory_H_HUM.createTestTopics(1,Network.getNetworkId());
            //create TopicAssignment
            List<TopicAssignment> allTopicAssigns = CommunityTestDataFactory_H_HUM.createTopicAssignment(1,allArticles[0].id,allTopics[0].id);
            //Create an article Bookmark
            List<ArticleBookmark__c> allBookMarks = CommunityTestDataFactory_H_HUM.createArticleBookmark(1,allArticles[0].id);
            
            Knowledge__kav kv= [Select id,UrlName from Knowledge__kav limit 1];
            List<Knowledge__kav> ArticleList=ArticleDetailController_C_HUM.getRecommendations(kv.UrlName,'ArticleDetail','video');
            System.assertEquals(0, ArticleList.size());
        }
    }
}