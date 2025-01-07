/*******************************************************************************************************************************
Apex Trigger Name : SSOAuthHandler_T_HUM
Version           : 1.0
Created On        : 09/22/2020
Function          : This serves as test class for JIT for Jive community SSO.
                
Modification Log: 
* Developer Name           Code Reveiw                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------

* Gaurav Dharra										 09/22/2020				   Updated permission set assignment method to assign PS based on community ID

*******************************************************************************************************************************/

@isTest
private class SSOAuthHandler_T_HUM {
    
    private static final String accountEmail = 'testsso@test.com';
    private static final String federationIdentifier = '01223456789';
    private static final String accLastName = 'SSO_Val';
    private static final String accLastNameUpdate = 'SSO_Val_u';
    private static final Id samlSsoProviderId = '01000000000E000';
    private static Id communityId = null;
    private static final Id portalId = null; 
    private static final String assertion = 'assertion';
    private static final String federationIdentifierNoMatch = '01223456787';
    private static Map<String, String> attributes = new Map<String, String> {
        'FederatedId' => federationIdentifier,
            'LastName' => accLastName,
            'Email' => accountEmail
            };
                
                @testSetup
                private static void testSetup() {
                    
                    Id MemberRecordTypeId = [select Id from RecordType where (Name='Member') and (SobjectType='Account')].Id;
                    
                    User testUser = [ select Id from User where Id = :UserInfo.getUserId() ];
                    
                    List<Account> accList = new List<Account>();
                    
                    accList.add(new Account(
                        FirstName = 'Test',
                        LastName = accLastName,
                        PersonEmail = accountEmail,
                        Enterprise_Id__c = federationIdentifier,
                        RecordTypeId = MemberRecordTypeId));
                    insert accList;

                    User newUser = HUMTestDataHelperTest.createTestUser('userforInactive','Standard User');
                    insert newUser;
                    system.assert(newUser != null);

                    User systemUser = HUMTestDataHelperTest.createTestUser('systemUser','System Administrator');
                    insert systemUser;
                    system.assert(systemUser != null);
                }
    
    @isTest 
    private static void createUser_ExistingContactAndAccount() {
        User testUser = [ select Id from User where Id = :UserInfo.getUserId() ];
        UserRole userrole = [Select Id, DeveloperName From UserRole Where DeveloperName = 'CEO' Limit 1];
        testUser.UserRoleId = userrole.id;
        update testUser;
        
        Network networkList = [SELECT Id, Name FROM Network WHERE Name=:HUMConstants.HUMGo365Commercial LIMIT 1];
        communityId = networkList.Id;
        
        System.runAs(testUser) {
            
            Test.startTest();
            SSOAuthHandler_I_HUM ssoHandler = new SSOAuthHandler_I_HUM();
            ssoHandler.createUser(samlSsoProviderId, communityId, portalId, federationIdentifier, attributes, assertion);
            Test.stopTest();
            
            User newUser = [SELECT Email, IsActive, FederationIdentifier FROM User Where lastname =: accLastName  LIMIT 1];
            PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name =: HUMConstants.HUMGo365CommercialPS];
            List<PermissionSetAssignment> userPS = [SELECT Id, PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId =: newUser.Id AND PermissionSetId =: ps.Id];
            System.assertEquals(accountEmail, newUser.Email);
            System.assertEquals(true, newUser.IsActive);
            System.assertEquals(federationIdentifier, newUser.FederationIdentifier);
            System.assertEquals(userPS!=null, true);
        }
        
    }
    
    @isTest 
    private static void updateUser_ExistingContactAndAccount() {
        User testUser = [ select Id from User where Id = :UserInfo.getUserId() ];
        UserRole userrole = [Select Id, DeveloperName From UserRole Where DeveloperName = 'CEO' Limit 1];
        testUser.UserRoleId = userrole.id;
        update testUser;
        User systemUser =  [ select Id from User where Email = 'systemUser@test.com' ];
         
        Network networkList = [SELECT Id, Name FROM Network WHERE Name=:HUMConstants.HUMGo365Medicare LIMIT 1];
        communityId = networkList.Id;

        user inactiveUser = [SELECT Id, isActive FROM User WHERE Email = 'userforInactive@test.com'];
        System.runAs(systemUser){
            
            inactiveUser.LastName = accLastNameUpdate;
            inactiveUser.IsActive = False;
            update inactiveUser;
        } 
        
        System.runAs(testUser) {
            
            SSOAuthHandler_I_HUM ssoHandler = new SSOAuthHandler_I_HUM();
            ssoHandler.createUser(samlSsoProviderId, communityId, portalId, federationIdentifier, attributes, assertion);

            Test.startTest();
            ssoHandler.updateUser(inactiveUser.Id, samlSsoProviderId, communityId, portalId, federationIdentifier, attributes, assertion);
            Test.stopTest();
            
            PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name =: HUMConstants.HUMGo365MedicarePS];
            PermissionSetAssignment userPS = [SELECT Id, PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId =: inactiveUser.Id AND PermissionSetId =: ps.Id];
            
            System.assertEquals(accLastNameUpdate, inactiveUser.LastName);
            System.assertEquals(userPS!=null, true);
        }
        
    }
    
    @isTest 
    private static void handleJITException() {
        User testUser = [ select Id from User where Id = :UserInfo.getUserId() ];
        
        Network networkList = [SELECT Id, Name FROM Network WHERE Name=:HUMConstants.HUMGo365Medicare LIMIT 1];
        communityId = networkList.Id;
        
        System.runAs(testUser) {
            attributes.put('FederatedId',federationIdentifierNoMatch);
            try {
                Test.startTest();
                SSOAuthHandler_I_HUM ssoHandler = new SSOAuthHandler_I_HUM();
                ssoHandler.createUser(samlSsoProviderId, communityId, portalId, federationIdentifierNoMatch, attributes, assertion);
                Test.stopTest();
            }
            catch(Exception ex) {
                System.assertEquals(HumConstants.HUMJITExceptionMessage,ex.getMessage());
            } 
        }
    }
}