/**************************************************************************** 
Apex Class Name  : HUMSoftPhoneCallTransferHelperTest 
Version          : 1.0  
Created Date     : September 24 2014 
Description      : This class is used as test class for HUMSoftPhoneCallTransferHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shruthi Karanth           18133                  09/24/2014                     Original Version
* Ranjeeth Nagishetty                              01/10/2015                     REQ-95950 regarding validation rule
* Shreya Choodamani                                01/12/2015                     Added code to fix the test class failure.
* Asha Deepika Koyyana      20572                  01/28/2015                     Fixed test class failure for Defect#164139
* Harika Devi Kalidindi     112000                 07/20/2017                     REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy    
* Santhi Mandava                                   03/12/2019                     Removed "DpaaSWebServiceMockImpl" usage.
***************************************************************************************************/

@isTest
private Class HUMSoftPhoneCallTransferHelperTest 
{
 
    /*
    * Method Name : callTransferDataTest
    * Description : This method is used for testing soft phone call transfer service
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void callTransferDataTest()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert user;
        
        System.runas(user) 
        {
            Profile prof = [SELECT Id FROM Profile WHERE Name = 'System Administrator']; 
            User u = new User(Alias = 'softTest', Email = 'test@softphone.com', 
            EmailEncodingKey = 'UTF-8', LastName = 'Testing', LanguageLocaleKey = 'en_US', 
            LocaleSidKey = 'en_US', ProfileId = prof .Id, 
            TimeZoneSidKey = 'America/Los_Angeles', UserName = 'softphoneuser@testorg.com');       
            
            //Create Member Data
            Account actMember = new Account(Name = 'Test Member Account');    
            insert actMember;
            string sGroupRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();  
            Account actGroup = new Account(Name = 'Test Group Account',recordtypeId =sGroupRecordtypeId);  
            insert actGroup ; 
            
            //Assert the member data is created
            system.assert(actMember.id != null);
            
            //Create Policy, Product and Policy Member Data
            Product2 p = new Product2(Name = 'MER',ProductCode = 'MER');
            insert p;
            
            //Assert the product data is created
            system.assert(p.id != null);
            
            Policy__c oPolicy = new Policy__c(name = 'Test Policy', Group_Name__c = actGroup.Id,Product__c = p.id,Policy_Group_Number__c='11111');  
            insert oPolicy;  
            
            List<Policy_Member__c> policyMemberLst = new List<Policy_Member__c>();
            Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = actMember.id );  
            policyMemberLst.add(oPolicyMem);  
            
            insert policyMemberLst;  
            
            //Assert the policy member data is created
            system.assertEquals(policyMemberLst.size(),1);
            
            //Create Interaction Data
            Interaction__c interAction = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c = actGroup.id,UUID__c = 'Test UUID',Interacting_With_type__c ='Group');
            insert interAction ;
            
             //Assert the interaction data is created
            system.assert(interAction.id <> null);
             
            Interaction_Member__c intMem = new Interaction_Member__c(Interacting_About__c = actGroup.id,Interaction__c = interAction.id,Interacting_About_Type__c='Group');             
            insert intMem; 
             
            //Assert the interaction member is created
            system.assert(intMem.id<>null);
            
            //Create a case for call transfer
            Case memberCase = new Case(AccountId=actMember.id,Policy_Member__c = policyMemberLst[0].id,Interacting_With_Name__c = 'TEST');
            insert memberCase ;
            
            //Assert the member case data is created
            system.assert(memberCase.id <> null);
            
            //Execute the test for the above data
            
            test.starttest();      
                //Test the call transfer helper class
                HUMSoftPhoneCallTransferHelper humSoftPhoneResponse = new HUMSoftPhoneCallTransferHelper(oPolicyMem,actMember,actGroup,memberCase,interAction,'MARKETID',intMem);
            test.stoptest();
            
            system.assert(humSoftPhoneResponse != null); 
        }
    }
}