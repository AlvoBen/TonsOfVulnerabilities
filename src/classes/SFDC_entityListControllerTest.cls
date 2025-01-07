/************************************************************************************************************************************************
Apex Class Name  : SFDC_entityListControllerTest
Version          : 1.1
Created Date     : Apr 18 2016
Function         : Test Class to test functionality of the referenced class.
Class(es)        : SFDC_entityListController
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Anthony Weaver                                   08/03/2016                   Initial Test Class
*************************************************************************************************************************************************/

@isTest
private class SFDC_entityListControllerTest {

    static testMethod void testController() {

        User runAsUser = createSFUser('System Administrator', true);
        System.assert(runAsUser.id != null);
     
        System.runAs(runAsUser) {
			createTestRecords(5,1);
            
        	SFDC_entityListController controller = new SFDC_entityListController();
        	List<SelectOption> entityList = controller.getEntityList();
        	System.assert( !entityList.isEmpty(), 'getEntityList() is empty.' );

            List<SelectOption> recTypeList = controller.getRecordTypeList();
            System.assert( !recTypeList.isEmpty(), 'getRecordTypeList() is empty. Are there record types for accounts in this instance?' );
        	
            // Next - test selecting Person Accounts
            controller.entityName = 'Person Accounts';
            List<SelectOption> recTypeList2 = controller.getRecordTypeList();
            System.assert( !recTypeList2.isEmpty(), '(2)getRecordTypeList() is empty. Are there record types for person accounts in this instance?' );
            
            // Select a record type and call retrieveEntityIds
            controller.recTypeSelected = recTypeList2[1].getLabel();
            controller.retrieveEntityIds();
            
            List<String> subtabEntityList = SFDC_entityListController.getSubtabEntityList('Accounts');
            System.assert( !subtabEntityList.isEmpty(), 'getSubtabEntityList() is empty.' );
            
            List<Id> subtabEntityIdList = SFDC_entityListController.retrieveSubtabEntityIds('Accounts');
            System.assert( !subtabEntityIdList.isEmpty(), 'retrieveSubtabEntityIds() is empty.' );
            
        }
    }
    
    private static User createSFUser(String profileName, Boolean addPermSet){
    
        Profile profileUsed;
        String currentUser = UserInfo.getUserName();
        String ret = 'word' + math.rint(math.random() * 100000);
        // AXW3742 2015/03/03: Added additional variable to account for validation rules in the nophi environment
        String tmpUserName = ret + '@' + currentUser.substringAfter('@');
     
        Profile p = [select id from profile where name= :profileName];
        //WHERE Name = 'AM_User_Provisioning'
        PermissionSet pset = [SELECT Id FROM PermissionSet WHERE IsOwnedByProfile = false LIMIT 1];       
        
        String namePrefix = ret;
    
         User testUser = new User();
         testUser.Email = 'test=test.com@example.com';
         testUser.Username = tmpUserName;
         testUser.LastName = 'test';
         testUser.Alias = 'test';
         testUser.ProfileId = p.Id;
         testUser.LanguageLocaleKey = 'en_US';
         testUser.LocaleSidKey = 'en_US';
         testUser.TimeZoneSidKey = 'America/Chicago';
         testUser.EmailEncodingKey = 'UTF-8';     
         
         insert testUser; 
        
         if (addPermSet = true) {
             PermissionSetAssignment psa = new PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = pset.id);
             insert psa;
         }
         
         return testUser;
  	} 
  
    private static void createTestRecords(Integer numAccts, Integer numContactsPerAcct){
        List<Account> accts = new List<Account>();
        
        for(Integer i=0;i<numAccts;i++) {
            Account a = new Account(Name='TestAccount' + i);
            accts.add(a);
        }
        insert accts;
        
        List<Contact> cons = new List<Contact>();
        for (Integer j=0;j<numAccts;j++) {
            Account acct = accts[j];            
            // For each account just inserted, add contacts
            for (Integer k=numContactsPerAcct*j;k<numContactsPerAcct*(j+1);k++) {
                cons.add(new Contact(firstname='Test'+k,
                                     lastname='Test'+k,
                                     AccountId=acct.Id,
                                     phone='1234567890'));
            }
        }
        // Insert all contacts for all accounts
        insert cons;
    }
}