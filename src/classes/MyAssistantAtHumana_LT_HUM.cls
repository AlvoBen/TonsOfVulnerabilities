/************************************************************************************************************************************************************************************       
Apex Class Name  : MyAssistantAtHumana_LT_HUM.cls
Version          : 1.0
Created Date     : September 15, 2023
Function         : Test class for MyAssistantAtHumana_LC_HUM.cls 

Modification Log: 
Developer Name                    Date                    Description
*--------------------------------------------------------------------------------------------------
* Robert Crispen                09/15/2023                Initial Version
************************************************************************************************************************************************************************************/
@isTest
private class MyAssistantAtHumana_LT_HUM {
    
    @testSetup static void setup(){
        
        User oSAUser = HUMTestDataHelperTest.createTestUser('MyAHSpecialist','System Administrator');
        insert oSAUser;
        
        system.runAs(oSAUser)
        {
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = oSAUser.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.Birthdate__c = '1/13/1974';
            accountRec.Gender__c = 'Male';
            accountRec.PersonMailingCity='WEST ALLIS';
            accountRec.PersonMailingState='WISCONSIN';
            accountRec.PersonMailingPostalCode='53227';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
        	system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);  
            
            Interaction__C oInt = new Interaction__C();
			oInt.Caller_name__C = 'Test Caller';
			oInt.Interacting_with_type__C = 'Member';
			oInt.Interacting_with__c = accountRec.Id;
			oInt.Interaction_Origin__c = 'Inbound Call';
			oInt.OwnerId = oSAUser.Id;
			insert oInt ;
			system.assertEquals(accountRec.Id,[Select Interacting_With__c from Interaction__c where id=:oInt.Id].Interacting_With__c);
			
			Interaction_Member__c oIntMem = new Interaction_Member__c(
											Interaction__c = oInt.id,
											Interacting_About__c = accountRec.Id,
											Interacting_About_Type__c = 'Member'
											);
			insert oIntMem;
			system.assertEquals(oInt.id,[Select Interaction__c from Interaction_Member__c where id=:oIntMem.id].Interaction__c);
            
            MemberPlan memberPlanRec = new MemberPlan();
            memberPlanRec.Name = '89571723404';
            memberPlanRec.MemberId = accountRec.Id;
            memberPlanRec.Status = 'Active'; 
            memberPlanRec.EffectiveFrom = Date.newInstance(2023,01,01);
            memberPlanRec.EffectiveTo = Date.newInstance(4000,12,31); 
            memberPlanRec.ETL_Record_Deleted__c = false;
            memberPlanRec.Member_Id_Base__c = '895717234';
            memberPlanRec.Member_Dependent_Code__c = '01';
            memberPlanRec.GroupNumber = '923382';
            insert memberPlanRec;
        }
    }
    @isTest
    static void testGetAiChatAccess(){
        User oSAUser = [Select Id,Name from User where LastName='test MyAHSpecialist' Limit 1];
        PermissionSet ps1 = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_900_My_Assistant_At_Humana'];
        PermissionSetAssignment psa1 = new PermissionSetAssignment();
        psa1.AssigneeId = oSAUser.Id;
        psa1.PermissionSetId = ps1.Id;
        insert psa1;
        
        system.runAs(oSAUser){
            Test.startTest();
            Boolean hasAccess = MyAssistantAtHumana_LC_HUM.getAiChatAccess();
            system.AssertEquals(true,hasAccess);
            Test.stopTest();
        }
    }
    
    @isTest
    static void testGetAccount(){
        User oSAUser = [SELECT Id, Name, UserName, Current_Queue__c FROM User WHERE LastName = 'test MyAHSpecialist'];
        system.runAs(oSAUser){
            Test.startTest();
             Account acc = [select Id,Name from Account where LastName='Account' limit 1];
            MyAssistantAtHumana_LC_HUM.getAccount(acc.Id);
            Test.stopTest();   
        }
    }
    
     @isTest
     static void testGetMyAHOnOffSwitchBooleanValue(){
        User oSAUser = [SELECT Id, Name, UserName, Current_Queue__c FROM User WHERE LastName = 'test MyAHSpecialist'];
         
        CRMFunctionality_ONOFF_Switch__c myAHFunctionalitySwitch = new CRMFunctionality_ONOFF_Switch__c();
        myAHFunctionalitySwitch.IsON__c = true;
        myAHFunctionalitySwitch.Name = '5029083';
        insert myAHFunctionalitySwitch;	
         
        system.runAs(oSAUser){
            Test.startTest();
            Boolean myAhSwitch = MyAssistantAtHumana_LC_HUM.getMyAHOnOffSwitchBooleanValue();
            system.assertEquals(true,myAhSwitch);
            Test.stopTest();   
        }
    }
    
    @isTest
    static void testExceptionGetMyAHOnOffSwitchBooleanValue(){
        User oSAUser = [SELECT Id, Name, UserName, Current_Queue__c FROM User WHERE LastName = 'test MyAHSpecialist'];
        system.runAs(oSAUser){
            Test.startTest();
            Boolean myAhSwitch = MyAssistantAtHumana_LC_HUM.getMyAHOnOffSwitchBooleanValue();
            system.assertEquals(false,myAhSwitch);
            Test.stopTest();   
        }
    }
    
    @isTest
    static void testGetMemberPlan(){
        User oSAUser = [SELECT Id, Name, UserName, Current_Queue__c FROM User WHERE LastName = 'test MyAHSpecialist'];
        system.runAs(oSAUser){
            MemberPlan mPlan = [select Id,MemberId from MemberPlan where Member_Id_Base__c = '895717234' limit 1];
            Test.startTest();
            MyAssistantAtHumana_LC_HUM.getMemberPlan(mPlan.Id);
            Test.stopTest();
        }   
    }
    
    @isTest
    static void testGetInteractionByCreator(){
        User oSAUser = [select Id,Name,UserName from User where LastName = 'test MyAHSpecialist'];
        system.runAs(oSAUser){
            Test.startTest();
            MyAssistantAtHumana_LC_HUM.getInteractionByCreator(oSAUser.Id);
            Test.stopTest();
        }
    }
        
    @isTest
    static void testGetPlanAccessDetails(){
        User objUser = [Select Id,Name from User LIMIT 1];

        System.runAs(objUser) 
        {
            Profile prof = [SELECT Id FROM Profile WHERE Name = 'System Administrator']; 
            User u = new User(Alias = 'MyAHTest', Email = 'test@MyAH.com', 
                              EmailEncodingKey='UTF-8', LastName='Testing', LanguageLocaleKey='en_US', 
                              LocaleSidKey = 'en_US', ProfileId = prof.Id, 
                              TimeZoneSidKey = 'America/Los_Angeles', UserName = 'MyAHuser@testorg.com');       
            
            Product2 p = new Product2(Name = 'DEN',ProductCode = 'DEN');
            insert p;            
            //Assert the product data is created
            system.assert(p.id != null, 'p is null');
            
            //Create Policy, Product and Policy Member Data
            Product2 p1 = new Product2(Name = 'VIS',ProductCode = 'VIS');
            insert p1;            
            //Assert the product data is created
            system.assert(p1.id != null, 'p1 is null');
            
            Product2 p2 = new Product2(Name = 'MED',ProductCode = 'MED');
            insert p2;            
            //Assert the product data is created
            system.assert(p2.id != null, 'p2 is null');
            
            Id accRecTypId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            Account oAccount = new Account();
            oAccount.Name__c = 'TestingAccount';
            oAccount.LastName = 'Demo';
            oAccount.RecordTypeId =accRecTypId; 
            insert oAccount; 
            
            PurchaserPlan oPolicy = new PurchaserPlan(name = 'Test Policy', PayerId = oAccount.Id,Product__c = p.id, Policy_Group_Number__c='11111');
            insert oPolicy;  
            system.assert(oPolicy.id != null, 'oPolicy is null');
            
            PurchaserPlan oPolicy1 = new PurchaserPlan(name = 'Test Policy VIS', PayerId = oAccount.Id, Product__c = p1.id, Policy_Group_Number__c='11111');
            insert oPolicy1;  
            system.assert(oPolicy1.id != null, 'oPolicy1 is null');
            
            PurchaserPlan oPolicy2 = new PurchaserPlan(name = 'Test Policy VIS', PayerId = oAccount.Id, Product__c = p2.id, Policy_Group_Number__c='11111');
            insert oPolicy2;  
            system.assert(oPolicy2.id != null, 'oPolicy2 is null');

            List<MemberPlan> listMemberPlan = new List<MemberPlan>();
            MemberPlan objMemberplan = new MemberPlan();
            objMemberplan.Name='MemberPlan1';
            objMemberplan.MemberId=oAccount.id;
            
            MemberPlan objMemberPlan2 = new MemberPlan();
            objMemberPlan2.Name='MemberPlan2';
            objMemberPlan2.MemberId=oAccount.id;
            
            listMemberPlan.add(objMemberPlan2);
            listMemberPlan.add(objMemberPlan);
            insert listMemberPlan;

            test.starttest(); 
            MyAssistantAtHumana_LC_HUM.getPlanAccessDetails(oAccount.id);
            test.stoptest();
            
        }
    }
}