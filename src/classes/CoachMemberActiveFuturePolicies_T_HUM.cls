/*******************************************************************************************************************************
Apex Class Name : CoachMemberActiveFuturePolicies_T_HUM
Version         : 1.0
Created On      : 03/08/2021
Function        : This is the Test class for CoachMemberActiveFuturePolicies_C_HUM


Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Jasmeen Shangari                                   03/08/2021                Original Version
*******************************************************************************************************************************************************************/

@isTest
private class CoachMemberActiveFuturePolicies_T_HUM {
    
    static testMethod void testUnitTest() {
        User user = HUMTestDataHelperTest.createTestUser('CoachUser1','System Administrator'); 
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
            //member plan creation
            List<MemberPlan> lstMemberPlan = new List<MemberPlan>();
            MemberPlan objMemberplan = new MemberPlan();
            objMemberplan.Name='MemberPlan1';
            objMemberplan.MemberId=actMember.id;
            objMemberplan.Policy__c=oPolicy3.Id;
            
            MemberPlan objMemberplan2 = new MemberPlan();
            objMemberplan2.Name='MemberPlan2';
            objMemberplan2.MemberId=actMember.id;
            objMemberplan2.Policy__c=oPolicy3.Id;
            
            lstMemberPlan.add(objMemberplan2);
            lstMemberPlan.add(objMemberPlan);
            insert lstMemberPlan;
            
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
            CoachMemberActiveFuturePolicies_C_HUM.determinePolicyAccess(actMember.id);
            test.stoptest();
        }
    }
}