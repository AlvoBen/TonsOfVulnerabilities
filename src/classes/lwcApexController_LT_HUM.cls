/**
 
 */
@isTest
private class lwcApexController_LT_HUM {

    @TestSetup
    static void createTestData(){
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
            RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
            
            List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name='TENANTID_GEN',StringValue__c='00'));
            lstConstants.add(new HUMConstants__c(Name='UNKNOWN_MEMBER',StringValue__c='Unknown Member'));
            lstConstants.add(new HUMConstants__c(Name='UNKNOWN_MEMBER_NOTATION',StringValue__c='M'));

            lstConstants.add(new HUMConstants__c(Name='UNKNOWN_AGENT_EXTID',StringValue__c='UKA|00|SFDC|'));
            lstConstants.add(new HUMConstants__c(Name='UNKNOWN_GROUP_EXTID',StringValue__c='UKG|00|SFDC|'));
            lstConstants.add(new HUMConstants__c(Name='UNKNOWN_PROVIDER_EXTID',StringValue__c='UKP|00|SFDC|'));
            lstConstants.add(new HUMConstants__c(Name='UNKNOWN_MEMBER_EXTID',StringValue__c='UKM|00|SFDC|'));
            lstConstants.add(new HUMConstants__c(Name='PIPE_CHARACTER',StringValue__c='|'));
            insert lstConstants;
            System.assertEquals(lstConstants.size(), 8);
            
            // HUMSearchLimit__c oSearchLimit = new HUMSearchLimit__c(Name='Member_SearchLimit', Search_Limit__c=101);
            // insert oSearchLimit;
            // System.assertNotEquals(oSearchLimit, NULL, 'Value not matched');
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Group_Number__c = '675717';
            oAcc.recordtypeid = oMemRecType.Id;
            oAcc.Birthdate__c = '01/01/1984';
            oAcc.firstName = 'Daphiner';
            oAcc.lastName = 'Superman';
            oAcc.MiddleName = 'J';
            oAcc.Enterprise_ID__c='1030693160';
            oAcc.Home_Office_Account__c = false;    
            insert oAcc;
            
            Account oAcc1 = HUMTestDataHelperTest.getAccount();
            oAcc1.Group_Number__c = '1234';
            oAcc1.recordtypeid = oMemRecType.Id;
            oAcc1.Birthdate__c = '01/01/1984';
            oAcc1.firstName = 'Daphiner';
            oAcc1.lastName = 'Superman';
            oAcc1.MiddleName = 'J';
            oAcc1.Enterprise_ID__c='1030693162';
            oAcc1.Home_Office_Account__c = false;    
            insert oAcc1;
            System.AssertNotEquals(oAcc, NULL);           
        }
    }

    @isTest
    static void testinsertAccountMethod() {
       Map<String,Object> mapResult = new Map<String,Object>();
       Account objAcc = new Account(Name='Sam Unknown Member',PersonMailingPostalCode='40202');
        System.AssertNotEquals(objAcc, NULL);
       lwcApexController.insertAccountMethod(objAcc,'Medicaid-Id', '123321456654','Sam' , 'Unknown Member','sam@test.com','', '01/01/2002','','','', '', 'KY' , '40202');
       lwcApexController.insertAccountMethod(objAcc,'', '456654789987','Vicky' , 'Unknown Member','Vicky@test.com','', '01/01/1999','','','', '', 'KY' , '40222');
       lwcApexController.insertAccountMethod(objAcc,'', '','Vicky' , 'Unknown Member','Vicky@test.com','', '01/01/1999','','','', '', 'KY' , '40222');
       lwcApexController.insertAccountMethod(objAcc,'Medicaid-Id', '123321456654','Sam' , 'Unknown Member','sam@test.com','', '01/01/2002','','','', '', 'KY' , '40202');
    }

    @isTest
    static void testgetAccountMethod(){
        Account objAcc =[Select Id FROM Account Where Enterprise_ID__c='1030693160' Limit 1];
        System.AssertNotEquals(objAcc, NULL);
        lwcApexController.getAccountMethod(objAcc.Id);
        lwcApexController.getAccountList();
    }
}