/*******************************************************************************************************************************
Apex Class Name : CommunityProcessBuilder_UpdateUser_T_HUM 
Version         : 1.0
Created On      : 10/30/2020
Function        : This class is used for testing the the process builder-Update User Level for Community

Modification Log: 
* Developer Name           Code Review                     Date                            Description
*------------------------------------------------------------------------------------------------------------------------------
* Veena Aphale                                           10/30/2020                      Original version
****************************************************************************************************************************/

@isTest
public class CommunityProcessBuilder_UpdateUser_T_HUM {
    
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
* Method name : testProcessBuilder
* Description : This method tests the method Process builder
* Return Type : void
* Parameter   : none
*/
    private static testmethod void testProcessBuilder(){
        
        List<NetworkMember> memberships;
        User user =[select id from User where alias=: 'test123'];
        Map<String,Object> mapObject = new Map<String,Object> ();
        List<Network> lstNetwork = [SELECT id,Name FROM Network where Name =: CommunityConstants_C_HUM.COMMUNITY_GO365_MEDICARE Or Name=: CommunityConstants_C_HUM.COMMUNITY_SUPPORT limit 1];
        
        system.runAs(user){
            
            Test.startTest();
            
            memberships = [SELECT NetworkID,Network.Name, MemberID,ReputationPoints  FROM NetworkMember WHERE memberID = :user.id limit 1]; 
            memberships[0].ReputationPoints =1100;
            update memberships[0];
            
            user = [select id,Is_Old_Community_Member__c from User where alias=: 'test123'];
            system.assertEquals(user.Is_Old_Community_Member__c , true);
            
            Test.stopTest();
        }
        
    }
}