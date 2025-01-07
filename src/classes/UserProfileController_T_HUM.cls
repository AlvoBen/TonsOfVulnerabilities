/*******************************************************************************************************************************
Apex Class Name : UserProfileController_T_HUM 
Version         : 1.0
Created On      : 07/24/2020
Function        : This class is used for testing the class UserProfileController_C_HUM 

Modification Log: 
 * Developer Name           Code Review                     Date                            Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shreya Agrawal                                       07/24/2020                      Original version
 ****************************************************************************************************************************/

@isTest
public class UserProfileController_T_HUM {
    
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
        Knowledge__kav kav;
        List<Knowledge__kav> lstKnowledge = new List<Knowledge__kav> ();
        Knowledge__kav objKnowledge;
        List<ArticleBookmark__c> lstBookmarkArticles = new List<ArticleBookmark__c> ();
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
            
             List<Community_Setup__c> lstSetup = CommunityTestDataFactory_H_HUM.createTestCommunitySetup('Direct Message');
             lstSetup[0].Moderator__c = adminUser.Id;
             insert lstSetup; 
            
            lstKnowledge = CommunityTestDataFactory_H_HUM.createTestKnowledgeArticles(1);
            objKnowledge = [Select Id,UrlName,Title from Knowledge__kav limit 1];
            lstBookmarkArticles = CommunityTestDataFactory_H_HUM.createArticleBookmark(1,objKnowledge.Id);
            
        }
        
        System.runAs(user){
            List<FeedItem> allFeedItems = CommunityTestDataFactory_H_HUM.createFeedItemPosts(1,membership[0].NetworkID);
        }           
    }
    /*  
     * Method name : testGetUserActivity
     * Description : This method tests the method getUserActivity from class UserProfileController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testGetUserActivity(){
        
        User u =[select id from User where alias=: 'test123'];
        System.runAs(u){
            Map<String,String> resMap = UserProfileController_C_HUM.getUserActivity();
            System.assertEquals('00', resMap.get('Answered'));
        }
    }
    
    /*  
     * Method name : testSendMessage
     * Description : This method tests the method sendMessage from class UserProfileController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testSendMessage(){
        
        User u =[select id from User where alias=: 'test123'];
        System.runAs(u){
            String respString= UserProfileController_C_HUM.sendMessage('Test Param');
            System.assertEquals('', respString);
        }
    } 
    
    /*  
     * Method name : testBookmarkArticles
     * Description : This method tests the method getBookmarkedArticles from class UserProfileController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testBookmarkArticles(){
        
        User u =[select id from User where alias=: 'test1234'];
        System.runAs(u){
            List<ArticleBookmark__c> lstArticles = UserProfileController_C_HUM.getBookmarkedArticles();
            System.assertEquals(1, lstArticles.size());
        }
    }
}