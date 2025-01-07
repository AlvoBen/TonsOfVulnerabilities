/**************************************************************************** 
Apex Class Name  : MemberActivePolicies_T_HUM 
Version          : 1.0  
Created Date     : Jan 14, 2018 
Description      : This class is used as test class for MemberActivePolicies_C_HUM
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Amar Gadewar                                       01/14/2017                  Original Version
* Santhi Mandava                                     06/01/2018                  Implemented member id encryption changes.
* Darshan Sharma                                     06/26/2018                  REQ-367008 - AHT - Preselect Policy from the Search Page (RF)
* Santhi Mandava          355559                     05/10/2019                  Removed "ENCRYPTIONTYPE" custom setting creation
* Amar Gadewar                                       01/28/2020                  USER STORY 729689- PR00091574 - MF 1 - User Interface to manage Icons display (CRM)
* Keerti Durbha                                      05/12/2020                  738134- PR00091318 - MF 9 - Medicare OSB Vision And Fitness-on/offswitch
* Jayesh Solanki				     03/17/2021			 User Story 2056116: Ability to display Termed Policies information on the Member Search tab within the Search page (RF)
* Ajay Chakradhar                                    05/13/2021                  REQ-2288131 - Improve Test Code Coverage
* Jayesh Solanki                                     06/08/2021                  User Story 2305573: T1PRJ0001827 - MF 1 - AHT - Ability to display Termed Policies information on the Member Search tab within the Search page (CRM)
* Samantha Tennyson       						 		06/23/2021           	    User Story 2393164: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 6/11/21, 6/25/21, 7/9/21 and 7/23/21 releases (CRM) 
***************************************************************************************************/

@isTest
private class MemberActivePolicies_T_HUM {

    static testMethod void testUnitTest() {
        User user = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert user;
        
        System.runas(user) 
        {
            Profile prof = [SELECT Id FROM Profile WHERE Name = 'System Administrator']; 
            User u = new User(Alias = 'softTest', Email = 'test@softphone.com', 
            EmailEncodingKey='UTF-8', LastName='Testing', LanguageLocaleKey='en_US', 
            LocaleSidKey = 'en_US', ProfileId = prof .Id, 
            TimeZoneSidKey = 'America/Los_Angeles', UserName = 'softphoneuser@testorg.com');       
            
            //Create Member Data
            Account actMember = new Account(Name = 'Test Member Account');    
            insert actMember;   
            
            //Assert the member data is created
            system.assert(actMember.id != null);
            
            //Create Policy, Product and Policy Member Data
            Product2 p = new Product2(Name = 'MER',ProductCode = 'MER');
            insert p;            
            //Assert the product data is created
            system.assert(p.id != null);
            
            //Create Policy, Product and Policy Member Data
            Product2 p2 = new Product2(Name = 'VIS',ProductCode = 'VIS');
            insert p2;            
            //Assert the product data is created
            system.assert(p2.id != null);
            
            Product2 p3 = new Product2(Name = 'MED',ProductCode = 'MED');
            insert p3;            
            //Assert the product data is created
            system.assert(p3.id != null);
            
            //Create Group Data
            string sGroupRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            Account actGroup = new Account(Name = 'Test Group Account',recordtypeId = sGroupRecordtypeId);  
            insert actGroup ;            
            system.assert(actGroup.id != null); 
               
            Policy__c oPolicy = new Policy__c(name = 'Test Policy', Group_Name__c = actGroup.Id,Product__c = p.id,Policy_Group_Number__c='11111');
            insert oPolicy;  
            system.assert(oPolicy.id != null);
            
            Policy__c oPolicy1 = new Policy__c(name = 'Test Policy VIS', Group_Name__c = actGroup.Id, Product__c = p2.id,Policy_Group_Number__c='11111');
            insert oPolicy1;  
            system.assert(oPolicy1.id != null);
            
            Policy__c oPolicy3 = new Policy__c(name = 'Test Policy VIS', Group_Name__c = actGroup.Id, Product__c = p3.id,Policy_Group_Number__c='11111');
            insert oPolicy3;  
            system.assert(oPolicy3.id != null);
            
            List<Policy_Member__c> policyMemberLst = new List<Policy_Member__c>();
            Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = actMember.id,Effective_Date__c=Date.today().addDays(-5).format(),End_Date__c=Date.today().addDays(10).format());  
            policyMemberLst.add(oPolicyMem);  
            
            Policy_Member__c oPolicyMem1 = new Policy_Member__c(name = 'Test Policy Mem1',Policy__c = oPolicy.Id, Member__c = actMember.id,Effective_Date__c=Date.today().addDays(-5).format(),End_Date__c=Date.today().addDays(10).format());    
            policyMemberLst.add(oPolicyMem1);                   
            
            Policy_Member__c oPolicyMem3 = new Policy_Member__c(name = 'Test Policy Mem1',Policy__c = oPolicy3.Id, Member__c = actMember.id,Effective_Date__c=Date.today().addDays(-5).format(),End_Date__c=Date.today().addDays(10).format());    
            policyMemberLst.add(oPolicyMem3); 
            
            insert policyMemberLst;  
            //Assert the policy member data is created
            system.assertEquals(policyMemberLst.size(), 3);
                      
            
            //Create Interaction Data
            Interaction__c interAction = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c = actGroup.id,UUID__c = 'Test UUID',Interacting_With_type__c='Group');
            insert interAction ;
            
             //Assert the interaction data is created
            system.assert(interAction.id <> null);
             
            Interaction_Member__c intMem = new Interaction_Member__c(Interacting_About__c = actGroup.id,Interaction__c = interAction.id,Interacting_About_Type__c='Group');             
            insert intMem; 
             
            //Assert the interaction member is created
            system.assert(intMem.id <> null);
             
            //Execute the test for the above data
            CRMFunctionality_ONOFF_Switch__c OSBONOFF= new CRMFunctionality_ONOFF_Switch__c();
            OSBONOFF.IsON__c=true;
            OSBONOFF.Name='738134';
            OSBONOFF.Story_Title__c='PR00091318 - MF 9 - Medicare OSB Vision and Fitness';
            Insert OSBONOFF;
            
            CRMFunctionality_ONOFF_Switch__c objSwitch1 = new CRMFunctionality_ONOFF_Switch__c();
            objSwitch1.IsON__c = true;
            objSwitch1.Name = '2037674';
            insert objSwitch1;
            test.starttest(); 

			PageReference pageRef = Page.HUMAccountDetailPage;
            Test.setCurrentPage(pageRef);
            Apexpages.currentPage().getParameters().put('id',actMember.id);
            MemberActivePolicies_C_HUM  mapController = new MemberActivePolicies_C_HUM();
            mapController.sAccId = actMember.id;
            List<HUMPolicyAccessDetailsWrapperHelper> policyListDetails = new List<HUMPolicyAccessDetailsWrapperHelper>();
            policyListDetails = mapController.lstpolicyAccessWrapper;
            
            mapController = new MemberActivePolicies_C_HUM();
            mapController.sAccId = actMember.id;
            System.assertEquals(3,mapController.lstPolicyAccessWrapper.size());
            test.stoptest();
        }
    }
    
    static testMethod void testUnitTest1() {
        User user = HUMTestDataHelperTest.createTestUser('TestUser1','Customer Care Specialist'); 
        insert user;
        
        PermissionSet pCheckRF =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.RAPIDFORCE_PERMISSIONSET_HUM LIMIT 1];
        
        PermissionSetAssignment psa = new PermissionSetAssignment();
        psa.AssigneeId = user.id;
        psa.PermissionSetId = pCheckRF.Id;
        insert psa;
        
        System.runas(user) 
        {
            Profile prof = [SELECT Id FROM Profile WHERE Name = 'Customer Care Specialist']; 
            User u = new User(Alias = 'softTest', Email = 'test@softphone.com', 
            EmailEncodingKey='UTF-8', LastName='Testing', LanguageLocaleKey='en_US', 
            LocaleSidKey = 'en_US', ProfileId = prof .Id, 
            TimeZoneSidKey = 'America/Los_Angeles', UserName = 'softphoneuser@testorg.com');       
            
            //Create Member Data
            Account actMember = new Account(Name = 'Test Member Account');    
            insert actMember;   
            
            //Assert the member data is created
            system.assert(actMember.id != null);
            
            //Create Policy, Product and Policy Member Data
            Product2 p = new Product2(Name = 'MER',ProductCode = 'MER');
            insert p;            
            //Assert the product data is created
            system.assert(p.id != null);
            
            //Create Policy, Product and Policy Member Data
            Product2 p2 = new Product2(Name = 'VIS',ProductCode = 'VIS');
            insert p2;            
            //Assert the product data is created
            system.assert(p2.id != null);
            
            Product2 p3 = new Product2(Name = 'MED',ProductCode = 'MED');
            insert p3;            
            //Assert the product data is created
            system.assert(p3.id != null);
            
            //Create Group Data
            string sGroupRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            Account actGroup = new Account(Name = 'Test Group Account',recordtypeId = sGroupRecordtypeId);  
            insert actGroup ;            
            system.assert(actGroup.id != null); 
               
            Policy__c oPolicy = new Policy__c(name = 'Test Policy', Group_Name__c = actGroup.Id,Product__c = p.id,Policy_Group_Number__c='11111');
            insert oPolicy;  
            system.assert(oPolicy.id != null);
            
            Policy__c oPolicy1 = new Policy__c(name = 'Test Policy VIS', Group_Name__c = actGroup.Id, Product__c = p2.id,Policy_Group_Number__c='11111');
            insert oPolicy1;  
            system.assert(oPolicy1.id != null);
            
            Policy__c oPolicy3 = new Policy__c(name = 'Test Policy VIS', Group_Name__c = actGroup.Id, Product__c = p3.id,Policy_Group_Number__c='11111');
            insert oPolicy3;  
            system.assert(oPolicy3.id != null);
            
            List<Policy_Member__c> policyMemberLst = new List<Policy_Member__c>();
            Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = actMember.id,Effective_Date__c=Date.today().addDays(-5).format(),End_Date__c=Date.today().addDays(10).format());  
            policyMemberLst.add(oPolicyMem);  
            
            Policy_Member__c oPolicyMem1 = new Policy_Member__c(name = 'Test Policy Mem1',Policy__c = oPolicy.Id, Member__c = actMember.id,Effective_Date__c=Date.today().addDays(-5).format(),End_Date__c=Date.today().addDays(10).format());    
            policyMemberLst.add(oPolicyMem1);                   
            
            Policy_Member__c oPolicyMem3 = new Policy_Member__c(name = 'Test Policy Mem1',Policy__c = oPolicy3.Id, Member__c = actMember.id,Effective_Date__c=Date.today().addDays(-5).format(),End_Date__c=Date.today().addDays(10).format());    
            policyMemberLst.add(oPolicyMem3); 
            
            insert policyMemberLst;  
            //Assert the policy member data is created
            system.assertEquals(policyMemberLst.size(), 3);
                      
            
            //Create Interaction Data
            Interaction__c interAction = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c = actGroup.id,UUID__c = 'Test UUID',Interacting_With_type__c='Group');
            insert interAction ;
            
             //Assert the interaction data is created
            system.assert(interAction.id <> null);
             
            Interaction_Member__c intMem = new Interaction_Member__c(Interacting_About__c = actGroup.id,Interaction__c = interAction.id,Interacting_About_Type__c='Group');             
            insert intMem; 
             
            //Assert the interaction member is created
            system.assert(intMem.id <> null);
             
            //Execute the test for the above data
            CRMFunctionality_ONOFF_Switch__c OSBONOFF= new CRMFunctionality_ONOFF_Switch__c();
            OSBONOFF.IsON__c=true;
            OSBONOFF.Name='2056116';
            OSBONOFF.Story_Title__c='Ability to display Termed Policies information on the Member Search tab within the Search page';
            Insert OSBONOFF;
            
            test.starttest(); 
                PageReference pageRef = Page.HUMAccountDetailPage;
                Test.setCurrentPage(pageRef);
                Apexpages.currentPage().getParameters().put('id',actMember.id);
                MemberActivePolicies_C_HUM  mapController = new MemberActivePolicies_C_HUM();
                mapController.sAccId = actMember.id;
                List<HUMPolicyAccessDetailsWrapperHelper> policyListDetails = new List<HUMPolicyAccessDetailsWrapperHelper>();
                policyListDetails = mapController.lstpolicyAccessWrapper;
                
                mapController = new MemberActivePolicies_C_HUM();
                mapController.sAccId = actMember.id;
                System.assertEquals(3,mapController.lstPolicyAccessWrapper.size());
            test.stoptest();
        }
    }
    
    static testMethod void testUnitTes3() {
        User user = HUMTestDataHelperTest.createTestUser('TestUser1','Customer Care Specialist'); 
        insert user;
        
        PermissionSet pCheckRF =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.RAPIDFORCE_PERMISSIONSET_HUM LIMIT 1];
        
        PermissionSetAssignment psa = new PermissionSetAssignment();
        psa.AssigneeId = user.id;
        psa.PermissionSetId = pCheckRF.Id;
        insert psa;
        
        System.runas(user) 
        {
            Profile prof = [SELECT Id FROM Profile WHERE Name = 'Customer Care Specialist']; 
            User u = new User(Alias = 'softTest', Email = 'test@softphone.com', 
            EmailEncodingKey='UTF-8', LastName='Testing', LanguageLocaleKey='en_US', 
            LocaleSidKey = 'en_US', ProfileId = prof .Id, 
            TimeZoneSidKey = 'America/Los_Angeles', UserName = 'softphoneuser@testorg.com');       
            
            //Create Member Data
            Account actMember = new Account(Name = 'Test Member Account');    
            insert actMember;   
            
            //Assert the member data is created
            system.assert(actMember.id != null);
            
            //Create Policy, Product and Policy Member Data
            Product2 p = new Product2(Name = 'MER',ProductCode = 'MER');
            insert p;            
            //Assert the product data is created
            system.assert(p.id != null);
            
            //Create Policy, Product and Policy Member Data
            Product2 p2 = new Product2(Name = 'VIS',ProductCode = 'VIS');
            insert p2;            
            //Assert the product data is created
            system.assert(p2.id != null);
            
            Product2 p3 = new Product2(Name = 'MED',ProductCode = 'MED');
            insert p3;            
            //Assert the product data is created
            system.assert(p3.id != null);
            
            //Create Group Data
            string sGroupRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            Account actGroup = new Account(Name = 'Test Group Account',recordtypeId = sGroupRecordtypeId);  
            insert actGroup ;            
            system.assert(actGroup.id != null); 
               
            Policy__c oPolicy = new Policy__c(name = 'Test Policy', Group_Name__c = actGroup.Id,Product__c = p.id,Policy_Group_Number__c='11111');
            insert oPolicy;  
            system.assert(oPolicy.id != null);
            
            Policy__c oPolicy1 = new Policy__c(name = 'Test Policy VIS', Group_Name__c = actGroup.Id, Product__c = p2.id,Policy_Group_Number__c='11111');
            insert oPolicy1;  
            system.assert(oPolicy1.id != null);
            
            Policy__c oPolicy3 = new Policy__c(name = 'Test Policy VIS', Group_Name__c = actGroup.Id, Product__c = p3.id,Policy_Group_Number__c='11111');
            insert oPolicy3;  
            system.assert(oPolicy3.id != null);
            
            List<Policy_Member__c> policyMemberLst = new List<Policy_Member__c>();
            Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = actMember.id,Effective_Date__c=Date.today().addDays(-5).format(),End_Date__c=Date.today().addDays(-4).format());  
            policyMemberLst.add(oPolicyMem);  
            
            
            
            insert policyMemberLst;  
            //Assert the policy member data is created
            system.assertEquals(policyMemberLst.size(), 1);
                      
            
            //Create Interaction Data
            Interaction__c interAction = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c = actGroup.id,UUID__c = 'Test UUID',Interacting_With_type__c='Group');
            insert interAction ;
            
             //Assert the interaction data is created
            system.assert(interAction.id <> null);
             
            Interaction_Member__c intMem = new Interaction_Member__c(Interacting_About__c = actGroup.id,Interaction__c = interAction.id,Interacting_About_Type__c='Group');             
            insert intMem; 
             
            //Assert the interaction member is created
            system.assert(intMem.id <> null);
             
            test.starttest(); 
                PageReference pageRef = Page.HUMAccountDetailPage;
                Test.setCurrentPage(pageRef);
                Apexpages.currentPage().getParameters().put('id',actMember.id);
                MemberActivePolicies_C_HUM  mapController = new MemberActivePolicies_C_HUM();
                mapController.sAccId = actMember.id;
                List<HUMPolicyAccessDetailsWrapperHelper> policyListDetails = new List<HUMPolicyAccessDetailsWrapperHelper>();
                policyListDetails = mapController.lstpolicyAccessWrapper;
                
                mapController = new MemberActivePolicies_C_HUM();
                mapController.sAccId = actMember.id;
                System.assertEquals(1,mapController.lstPolicyAccessWrapper.size());
            test.stoptest();
        }
    }
}