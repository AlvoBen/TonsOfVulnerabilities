/*******************************************************************************************************************************
Apex Class Name : CommunityGamificationController_T_HUM
Version         : 1.0
Created On      : 09/09/2020
Function        : This class is used for testing the class CommunityGamificationController_C_HUM

Modification Log: 
* Developer Name           Code Review                     Date                            Description
*------------------------------------------------------------------------------------------------------------------------------
* Veena Aphale                                           09/09/2020                      Original version
****************************************************************************************************************************/

@isTest
public class CommunityGamificationController_T_HUM {
    
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
            membership = [SELECT NetworkID,Network.Name, MemberID,ReputationPoints  FROM NetworkMember WHERE memberID = :user.id limit 1]; 
            
        }
        
    }
    
    
    /*  
* Method name : testGamification
* Description : This method tests the method getUserProfileGamificationfrom class CommunityGamificationController_C_HUM
* Return Type : void
* Parameter   : none
*/
    private static testmethod void testGamification(){
        
        List<NetworkMember> memberships;
        User user =[select id from User where alias=: 'test123'];
        Map<String,Object> mapObject = new Map<String,Object> ();
        List<Network> lstNetwork = [SELECT id,Name FROM Network where Name =: CommunityConstants_C_HUM.COMMUNITY_GO365_MEDICARE Or Name=: CommunityConstants_C_HUM.COMMUNITY_SUPPORT limit 1];
        
        system.runAs(user){
            
            Test.startTest();
            CommunityGamificationController_C_HUM.testNetworkId=lstNetwork[0].id;
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            system.assertEquals(mapObject.get(CommunityConstants_C_HUM.GAMIFICATION_ANSWER_QUESTION_COUNT),25);
            
            memberships = [SELECT NetworkID,Network.Name, MemberID,ReputationPoints  FROM NetworkMember WHERE memberID = :user.id limit 1]; 
            memberships[0].ReputationPoints =600;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =1200;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =1800;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =2400;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =2800;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =3505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =6505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =9505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =10505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            Test.stopTest();
        }
        
    }
    
    /*  
* Method name : testGamification_Go365
* Description : This method tests the method getUserProfileGamificationfrom class CommunityGamificationController_C_HUM for Go365 Commercial community
* Return Type : void
* Parameter   : none
*/
    private static testmethod void testGamification_Go365(){
        
        List<NetworkMember> memberships;
        Map<String,Object> mapObject = new Map<String,Object> ();
        
        List<Network> lstNetwork = [SELECT id,Name FROM Network where Name =: CommunityConstants_C_HUM.COMMUNITY_GO365_COMMERCIAL limit 1];
        User usertest =[select id from User where alias=: 'test123'];
        
        system.runAs(usertest){ 
            
            Test.startTest();
            CommunityGamificationController_C_HUM.testNetworkId=lstNetwork[0].id;
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            system.assertEquals(mapObject.get(CommunityConstants_C_HUM.GAMIFICATION_ANSWER_QUESTION_COUNT),25);
            
            memberships = [SELECT NetworkID,Network.Name, MemberID,ReputationPoints  FROM NetworkMember WHERE memberID = :usertest.id and Network.Name=: CommunityConstants_C_HUM.COMMUNITY_GO365_COMMERCIAL limit 1]; 
            
            memberships[0].ReputationPoints =11505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =14505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =16505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =17505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =18505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =21505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =35505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =55505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =65505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =80505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            memberships[0].ReputationPoints =95505;
            update memberships[0];
            mapObject = CommunityGamificationController_C_HUM.getUserProfileGamification();
            
            Test.stopTest();
        }
        
    }
    
    
}