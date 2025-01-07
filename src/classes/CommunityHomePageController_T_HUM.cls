/*******************************************************************************************************************************
Apex Class Name : CommunityHomePageController_T_HUM 
Version         : 1.0
Created On      : 07/29/2020
Function        : This class is used for testing the class CommunityHomePageController_C_HUM 

Modification Log: 
 * Developer Name           Code Review                     Date                            Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shreya Agrawal                                       07/29/2020                      Original version
 * Nitaj Titans                                        16/08/2021                    Added terms And conditions for Go365 Communities
 ****************************************************************************************************************************/

@isTest
public class CommunityHomePageController_T_HUM {

    /*  
     * Method name : TestSetup
     * Description : Test setup method for the testclass
     * Return Type : void
     * Parameter   : none
     */
     
    @testSetup static void TestSetup(){
        User user;
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS0824';
        oUser.UserPermissionsKnowledgeUser  = TRUE;
        oUser.UserRoleId = [SELECT Id, Name FROM UserRole WHERE name = 'System Account Role' LIMIT 1].Id;
        
        profile p= [select id from profile where Name='Humana Community Member'];
        
        Id recType = Schema.SObjectType.Account.getRecordTypeInfosbyDeveloperName().get('Member').getRecordTypeId();
        List<NetworkMember> membership;
        Account objAccount;
        system.runAs(oUser){
            objAccount = new Account();
            objAccount.FirstName='Test Firstname';
            objAccount.LastName='Test LastName';
            objAccount.RecordTypeId =recType;
            insert objAccount;  
            Account oAcc = [Select Id, PersonContactId,FirstName from Account where Id=:objAccount.Id];
            System.assertEquals(oAcc.FirstName, 'Test Firstname');
            
            user = new User(alias = 'test123', email='test123@noemail.com',
                                 emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US',
                                 localesidkey='en_US', profileid = p.id, country='United States',IsActive =true,
                                 ContactId = oAcc.PersonContactId,
                                 timezonesidkey='America/Los_Angeles', username='tester@noemail.com');
            
            insert user;            
            System.assertEquals([SELECT Id,alias FROM User WHERE alias = 'test123' LIMIT 1].alias, 'test123');
            membership = [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember WHERE memberID = :user.id limit 1]; 
        }
        system.runAs(user){
			List<Community_Setup__c> lstSetup = CommunityTestDataFactory_H_HUM.createTestCommunitySetup('Announcement');
            lstSetup[0].Community_Name__c = membership[0].Network.Name;
            insert lstSetup;
            
            List<Community_Setup__c> lstSetup1 = CommunityTestDataFactory_H_HUM.createTestCommunitySetup('Terms and Conditions');
            insert lstSetup1;
            
            List<Community_Setup__c> lstSetup2 = CommunityTestDataFactory_H_HUM.createTestCommunitySetup('Community Banner Image');
            lstSetup2[0].Community_Name__c = membership[0].Network.Name;
            insert lstSetup2;
			
            List<Topic> lstTopic = CommunityTestDataFactory_H_HUM.createTestTopics(1,membership[0].NetworkID);
        }
    }
    /*  
     * Method name : testGetTrendingTopics
     * Description : This method tests the method getTrendingTopics from class CommunityHomePageController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testGetTrendingTopics(){
        
        User u =[select id from User where alias=: 'test123'];
        System.runAs(u){
            Test.startTest();
            List<Topic> TopicList = CommunityHomePageController_C_HUM.getTrendingTopics();
            Test.stopTest();
            System.assertEquals(0, TopicList.size());
        }
    }
    
    /*  
     * Method name : testGetAnnouncements
     * Description : This method tests the method getAnnouncements from class CommunityHomePageController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testGetAnnouncements(){
        User user = [select id from User where alias=: 'test123'];
        System.runAs(user){
        Test.startTest();
        List<Community_Setup__c> AnnouncementList1 = [SELECT Id,Community_Name__c,Announcement_Title__c,Announcement_URL__c,Announcement_Description__c,Is_Announcement_Active__c FROM Community_Setup__c WHERE Recordtype.DeveloperName = :CommunityConstants_C_HUM.ANNOUNCEMENT_RECORDTYPE AND Is_Announcement_Active__c = true Order by CreatedDate desc LIMIT 3];
        List<Community_Setup__c> AnnouncementList2=CommunityHomePageController_C_HUM.getAnnouncements();
        Test.stopTest();
        
        System.assertEquals(AnnouncementList1.size(), AnnouncementList2.size());
        }
    }
    
    /*  
     * Method name : testTermsandConditions
     * Description : This method tests the method fetchTermsandConditions And updateUserRecord from Class CommunityHomePageController_C_HUM For humanasupport community
     * Return Type : void
     * Parameter   : none
     */
    @isTest
    private static void testTermsandConditions(){
        User u =[select id from User where alias=: 'test123'];
        List<NetworkMember> memberships = [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember WHERE memberID =: u.id LIMIT  1];
        system.runAs(u){
            Test.startTest();
            List<Community_Setup__c> List1 = [SELECT Id,Terms_and_Conditions__c FROM Community_Setup__c WHERE Recordtype.DeveloperName = :CommunityConstants_C_HUM.TERMS_AND_CONDITIONS_RECORDTYPE];
            CommunityHomePageController_C_HUM.testNetworkId = memberships[0].NetworkID;
            Community_Setup__c obj = CommunityHomePageController_C_HUM.fetchTermsandConditions();
            Boolean isUpdated = CommunityHomePageController_C_HUM.updateUserRecord();
            Test.stopTest();  
            System.assertEquals(isUpdated, false);
        }
    }
    
     /*  
     * Method name : testTermsandConditionsGo365Commercial
     * Description : This method tests the method fetchTermsandConditions And updateUserRecord from Class CommunityHomePageController_C_HUM For Go365Commercial community
     * Return Type : void
     * Parameter   : none
     */
    @isTest
    private static void testTermsandConditionsGo365Commercial(){
        User u =[select id from User where alias=: 'test123'];
        List<NetworkMember> memberships = [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember WHERE memberID =: u.id AND Network.Name =: CommunityConstants_C_HUM.COMMUNITY_GO365_COMMERCIAL LIMIT 1];
        system.runAs(u){
            Test.startTest();
            List<Community_Setup__c> List1 = [SELECT Id,Terms_and_Conditions__c FROM Community_Setup__c WHERE Recordtype.DeveloperName = :CommunityConstants_C_HUM.TERMS_AND_CONDITIONS_RECORDTYPE];
            CommunityHomePageController_C_HUM.testNetworkId = memberships[0].NetworkID;
            Community_Setup__c obj = CommunityHomePageController_C_HUM.fetchTermsandConditions();
            Boolean isUpdated = CommunityHomePageController_C_HUM.updateUserRecord();
            Test.stopTest();  
            System.assertEquals(isUpdated, false);
        }
    }

     /*  
     * Method name : testTermsandConditionsGo365Medicare
     * Description : This method tests the method fetchTermsandConditions And updateUserRecord from Class CommunityHomePageController_C_HUM For Go365Medicare community
     * Return Type : void
     * Parameter   : none
     */
    @isTest
    private static void testTermsandConditionsGo365Medicare(){
        User u =[select id from User where alias=: 'test123'];
        List<NetworkMember> memberships = [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember WHERE memberID =: u.id AND Network.Name =: CommunityConstants_C_HUM.COMMUNITY_GO365_MEDICARE LIMIT 1];
        system.runAs(u){
            Test.startTest();
            List<Community_Setup__c> List1 = [SELECT Id,Terms_and_Conditions__c FROM Community_Setup__c WHERE Recordtype.DeveloperName = :CommunityConstants_C_HUM.TERMS_AND_CONDITIONS_RECORDTYPE];
            CommunityHomePageController_C_HUM.testNetworkId = memberships[0].NetworkID;
            Community_Setup__c obj = CommunityHomePageController_C_HUM.fetchTermsandConditions();
            Boolean isUpdated = CommunityHomePageController_C_HUM.updateUserRecord();
            Test.stopTest();  
            System.assertEquals(isUpdated, false);
        }
    }

    /*  
     * Method name : testTopicDetails
     * Description : This method tests the method testTopicDetails from class CommunityHomePageController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testTopicDetails(){
    User u =[select id from User where alias=: 'test123'];
    system.runAs(u){
        Test.startTest();
        Topic topic = [Select Name, id from Topic limit 1];
        Topic objTopic = CommunityHomePageController_C_HUM.getTopicDetails(topic.Name);
        System.assertEquals(objTopic.Name, 'Test0');
        Test.stopTest();
        }
        
    }
    
    /*  
     * Method name : testBannerImages
     * Description : This method tests the method getBannerImages from class CommunityHomePageController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    private static testmethod void testBannerImages(){
    User u =[select id from User where alias=: 'test123'];
    List<NetworkMember> memberships = [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember WHERE memberID = :u.id limit 1];
    
    system.runAs(u){
        Test.startTest();
        Topic topic = [Select Name, id from Topic limit 1];
        Community_Setup__c objSetup = CommunityHomePageController_C_HUM.getBannerImages(topic.Id);
        
        System.assertEquals(objSetup.Topic_Name__c , 'Test0');
        Test.stopTest();
        }
        
    }
    
}