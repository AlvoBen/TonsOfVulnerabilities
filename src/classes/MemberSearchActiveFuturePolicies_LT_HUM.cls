/*******************************************************************************************************************************
Apex Class Name : MemberSearchActiveFuturePolicies_LT_HUM
Version         : 1.0
Created On      : 09/25/2020
Function        : This is the Test class for MemberSearchActiveFuturePolicies_LC_HUM


Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     09/25/2020                Original Version
*    2.0           Joel George                                      03/25/2021                Improved Code Coverage
*    3.0           Kajal Namdev                                     05/13/2021                Removed references of Policy_Member__c object
*******************************************************************************************************************************************************************/

@isTest
private class MemberSearchActiveFuturePolicies_LT_HUM {
    
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

            //member plan creation
            List<MemberPlan> lstMemberPlan = new List<MemberPlan>();
            MemberPlan objMemberplan = new MemberPlan();
            objMemberplan.Name='MemberPlan1';
            objMemberplan.MemberId=actMember.id;
            
            MemberPlan objMemberplan2 = new MemberPlan();
            objMemberplan2.Name='MemberPlan2';
            objMemberplan2.MemberId=actMember.id;
            
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
            
            //Execute the test for the above data
            CRMFunctionality_ONOFF_Switch__c OSBONOFF= new CRMFunctionality_ONOFF_Switch__c();
            OSBONOFF.IsON__c=true;
            OSBONOFF.Name='738134';
            OSBONOFF.Story_Title__c='PR00091318 - MF 9 - Medicare OSB Vision and Fitness';
            Insert OSBONOFF;
            
            test.starttest(); 
            MemberSearchActiveFuturePolicies_LC_HUM.determinePolicyAccess(actMember.id);
            MemberSearchActiveFuturePolicies_LC_HUM.determinePolicyAccess(null);
			MemberSearchActiveFuturePolicies_LC_HUM.getMemberPlanDetails(actMember.id);
            test.stoptest();
        }
    }
}