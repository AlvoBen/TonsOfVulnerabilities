/*******************************************************************************************************************************
Apex Class Name : FeedDetailController_T_HUM 
Version         : 1.0
Created On      : 07/23/2020
Function        : This class is used for testing the class FeedDetailController_C_HUM 

Modification Log: 
* Developer Name           Code Review                     Date                            Description
*------------------------------------------------------------------------------------------------------------------------------
* Shreya Agrawal                                       07/23/2020                      Original version
****************************************************************************************************************************/

@isTest
public class FeedDetailController_T_HUM {
    
     /*  
     * Method name : TestSetup
     * Description : Test setup method for the testclass
     * Return Type : void
     * Parameter   : none
     */
     
    @testSetup static void TestSetup(){
        
        profile p= [select id from profile where Name='Humana Community Member'];
        profile pAdmin = [select id from profile where Name='System Administrator'];
        Id recType = Schema.SObjectType.Account.getRecordTypeInfosbyDeveloperName().get('Member').getRecordTypeId();
        List<NetworkMember> membership;
        List<Knowledge__kav> allArticles;
        User user;
        
        User adminUser = new User(alias = 'test1234', email='test1234@noemail.com',
                                  emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US',
                                  localesidkey='en_US', profileid = pAdmin.id, country='United States',IsActive =true,
                                  timezonesidkey='America/Los_Angeles', username='tester1234@noemail.com');
        adminUser.Current_Queue__c = 'Cincinnati CRU1';
        adminUser.Network_User_Id__c = 'SXS0824';
        adminUser.UserPermissionsKnowledgeUser  = TRUE;
        adminUser.UserRoleId = [SELECT Id, Name FROM UserRole WHERE name = 'System Account Role' LIMIT 1].Id;
        insert adminUser;
        System.runAs(adminUser){
            
            Account objAccount = new Account();
            objAccount.FirstName='Test Firstname';
            objAccount.LastName='Test LastName';
            objAccount.RecordTypeId =recType;
            insert objAccount;   
            
            
            Account AccountRecord = [Select Id, PersonContactId from Account where Id=:objAccount.Id];
            
            user = new User(alias = 'test123', email='test123@noemail.com',
                            emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US',
                            localesidkey='en_US', profileid = p.id, country='United States',IsActive =true,
                            ContactId = AccountRecord.PersonContactId,
                            timezonesidkey='America/Los_Angeles', username='tester@noemail.com');
            
            insert user;
            membership = [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember WHERE memberID = :user.id limit 1]; 
            
            //Create a Knowledge Article
            allArticles = CommunityTestDataFactory_H_HUM.createTestKnowledgeArticles(2);
        }
        System.runAs(user){
            //Create topics
            List<Topic> allTopics = CommunityTestDataFactory_H_HUM.createTestTopics(1,membership[0].NetworkID);
            //create TopicAssignment
            List<TopicAssignment> allTopicAssigns = CommunityTestDataFactory_H_HUM.createTopicAssignment(1,allArticles[0].id,allTopics[0].id);
        }
    }
    /*  
    * Method name : testGetRecommendations
    * Description : This method tests the method getRecommendations from class FeedDetailController_C_HUM
    * Return Type : void
    * Parameter   : none
    */
    private static testmethod void testGetRecommendations(){
        
        User u =[select id from User where alias=: 'test123'];
        Knowledge__kav kv= [Select id,UrlName from Knowledge__kav limit 1];
        
        System.runAs(u){
            List<Knowledge__kav> kvList=FeedDetailController_C_HUM.getRecommendations(kv.UrlName,'ArticleDetail','video');
            FeedDetailController_C_HUM.getRecommendations(kv.id,'QuestionDetail','video');
            FeedDetailController_C_HUM.getRecommendations(kv.id,'HomePage','video');
            FeedDetailController_C_HUM.getRecommendations(kv.UrlName,'ArticleDetail','Article');
            FeedDetailController_C_HUM.getRecommendations(kv.id,'QuestionDetail','Article');
            FeedDetailController_C_HUM.getRecommendations(kv.id,'HomePage','Article');
            
            System.assertEquals(0,kvList.size());
        }
    }
    
    /*  
    * Method name : testGetGroups
    * Description : This method tests the method getGroups from class FeedDetailController_C_HUM
    * Return Type : void
    * Parameter   : none
    */
    private static testmethod void testGetGroups(){
        
        User u =[select id from User where alias=: 'test123'];
        Knowledge__kav kv= [Select id,UrlName from Knowledge__kav limit 1];
        
        System.runAs(u){
            FeedDetailController_C_HUM.getGroups('');
            List<CollaborationGroup> groupList=FeedDetailController_C_HUM.getGroups(kv.id);
            System.assertEquals(0,groupList.size());
        }
    }
}