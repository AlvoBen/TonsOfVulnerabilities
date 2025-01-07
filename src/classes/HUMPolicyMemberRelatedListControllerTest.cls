/**************************************************************************** 
Apex Class Name  : HUMPolicyMemberRelatedListControllerTest 
Version          : 1.0  
Created Date     : September 24 2014 
Description      : This class is used as test class for HUMPolicyMemberRelatedListController
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shruthi Karanth         18234                    09/24/2014                     Original Version
* Shruthi Karanth         18234                    09/29/2014                     Modified logic as inner class is removed
                                                                                  from HUMPolicyMemberRelatedListController
* Ranjeeth Nagishetty     20291                    01/10/2014                     REQ-95950 validation rule
* Shreya Choodamani       20291                    01/12/2015                     Added code to fix test class failure 
* Ranjeeth Nagishetty     20287                    01/13/2015                     Code Review Comments fixs
* Harika Devi Kalidindi   112000                   07/20/2017                     REQ - 323435: Added Policy_Group_Number__c field value while inserting policy
* Keerti Durbha                                    05/12/2020                     738134- PR00091318 - MF 9 - Medicare OSB Vision And Fitness-on/offswitch
* Asish Behera                                     09/04/2020                     PR00090588 - CR1727 - Display Medicare PDP Plan Name - Policy Section (Person Account Page)
***************************************************************************************************/

@isTest
private Class HUMPolicyMemberRelatedListControllerTest 
{
 
    /*
    * Method Name : callTransferDataTest
    * Description : This method is used for testing soft phone call transfer service
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void policyRelatedListTest()
    {
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
            list<Product2> listOfProduct = new list<Product2>();
            //Create Policy, Product and Policy Member Data
            Product2 p = new Product2(Name = 'MER',ProductCode = 'MER');
            listOfProduct.add(p);
            
            Product2 pdp = new Product2(Name = 'MED',ProductCode = 'PDP');
            listOfProduct.add(pdp);
            Product2 vis = new Product2(Name = 'VIS',ProductCode = 'PDP');
            listOfProduct.add(vis);
            
            if(!listOfProduct.isEmpty()){
                Insert listOfProduct;
            }
            //Assert the product data is created
            system.assert(listOfProduct[0].id != null);
            system.assert(listOfProduct[1].id != null);
            
            //Create Group Data
            string sGroupRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            Account actGroup = new Account(Name = 'Test Group Account',recordtypeId = sGroupRecordtypeId);  
            insert actGroup ;
            system.assert(actGroup.id != null); 
            list<Policy__c> listOfPolicy = new list<Policy__c>();
            Policy__c oPolicy = new Policy__c(name = 'Test Policy', Group_Name__c = actGroup.Id,Product__c = listOfProduct[0].id,Policy_Group_Number__c='11111');  
            listOfPolicy.add(oPolicy);
            Policy__c oPDPPolicy = new Policy__c(name = 'Test PDP Policy', Group_Name__c = actGroup.Id,Product__c = listOfProduct[1].id,Policy_Group_Number__c='2222',Major_LOB__c = 'PDP');  
           	listOfPolicy.add(oPDPPolicy);
            Policy__c oVISPolicy = new Policy__c(name = 'Test VIS Policy', Group_Name__c = actGroup.Id,Product__c = listOfProduct[2].id,Policy_Group_Number__c='2222',Major_LOB__c = 'PDP');  
           	listOfPolicy.add(oVISPolicy);
            if(!listOfPolicy.isEmpty()){
               Insert listOfPolicy;
            }
            List<Policy_Plan_Details__c> listofPolicyPlans =  new List<Policy_Plan_Details__c>();
            Policy_Plan_Details__c plan1 = new Policy_Plan_Details__c(name = 'Current',Policy__c=listOfPolicy[1].id,iab_description__c = 'Current Plan',Coverage_Plan_Effective_Date__c = '01/01/2013',
                                                                      Coverage_Plan_End_Date__c = '01/01/9999',Policy_Plan_External_ID__c = '23');
            Policy_Plan_Details__c plan2 = new Policy_Plan_Details__c(name ='past',Policy__c=listOfPolicy[1].id,iab_description__c = 'past Plan',Coverage_Plan_Effective_Date__c = '01/01/2012',
                                                                      Coverage_Plan_End_Date__c = '01/01/2013',Policy_Plan_External_ID__c = '12');
            listofPolicyPlans.add(plan1);
            listofPolicyPlans.add(plan2);
            Insert listofPolicyPlans;
            //system.assert(oPolicy.id != null);            
            //system.assert(oPolicy.id != null);    
            List<Policy_Member__c> policyMemberLst = new List<Policy_Member__c>();
            Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = listOfPolicy[0].Id, Member__c = actMember.id );  
            policyMemberLst.add(oPolicyMem);  
            
            Policy_Member__c oPolicyMem1 = new Policy_Member__c(name = 'Test Policy Mem1',Policy__c = listOfPolicy[0].Id, Member__c = actMember.id );    
            policyMemberLst.add(oPolicyMem1);  
            Policy_Member__c oPolicyMem2 = new Policy_Member__c(name = 'Test PDP Policy Mem',Policy__c = listOfPolicy[1].Id, Member__c = actMember.id );    
            policyMemberLst.add(oPolicyMem2);  
             Policy_Member__c oPolicyMem3 = new Policy_Member__c(name = 'Test Vis Policy Mem',Policy__c = listOfPolicy[2].Id, Member__c = actMember.id );    
            policyMemberLst.add(oPolicyMem3);
            
            insert policyMemberLst;  
            //Assert the policy member data is created
            system.assertEquals(policyMemberLst.size(),4);
            
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
	    CRMFunctionality_ONOFF_Switch__c PDPSwtich = new CRMFunctionality_ONOFF_Switch__c();
            PDPSwtich.IsON__c=true;
            PDPSwtich.Name='1279870';
            PDPSwtich.Story_Title__c='PR00090588 - CR1727 - Display Medicare PDP Plan Name - Policy Section (Person Account Page)';
            Insert PDPSwtich;
            test.starttest();      
                PageReference pageRef = Page.HUMAccountDetailPage;
                Test.setCurrentPage(pageRef);
                Apexpages.currentPage().getParameters().put('id',actMember.id);
                HUMPolicyMemberRelatedListController  plycon = new HUMPolicyMemberRelatedListController();
            	HUMPolicyMemberRelatedListController  plycon1 = new HUMPolicyMemberRelatedListController();
                plycon.sAccId = actMember.id;
            	plycon1.sAccId = actMember.id;
                plycon.sInteractionId = '';
                ApexPages.StandardSetController c;
                plycon.setConPolicies = c;
            	plycon1.setConPolicies = c;
                List<HUMPolicyAccessDetailsWrapperHelper> policyListDetails = new List<HUMPolicyAccessDetailsWrapperHelper>();
                plycon.next();
                plycon.previous();
                policyListDetails = plycon.lstpolicyAccessWrapper;
                plycon.sPolicyId = listOfPolicy[1].id;
                plycon1.sPolicyId = listOfPolicy[1].id;
                plycon.rowNumber = 0;
            	plycon.showrelatedTable();
            	plycon1.showrelatedTable();
            	plycon1.removerelatedTable();
                plycon.removerelatedTable();
                plycon.saveDataForCallTransfer();
                system.assert(ApexPages.hasMessages() == true);
                plycon.sInteractionId = '&interactionId='+interAction .id;            
                policyListDetails = plycon.lstpolicyAccessWrapper;
                plycon.saveDataForCallTransfer();
                system.assert(ApexPages.hasMessages() == true);
            test.stoptest();
        }
    } 
}