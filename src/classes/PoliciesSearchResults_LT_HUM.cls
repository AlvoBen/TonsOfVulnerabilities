/*******************************************************************************************************************************
Apex Class Name : PoliciesSearchResults_LT_HUM
Version         : 1.0
Created On      : 11/13/2020
Function        : This is the Test class for PoliciesSearchResults_LC_HUM


Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0          Ritik Agarwal                                     11/13/2020                Original Version
*	 1.0          Ankima Srivastava							        03/19/2021				  fix to increase code coverage
*    1.3          Kajal Namdev                                      05/17/2021               Replaced references of policy with purchaserpla
*    1.4          Kajal Namdev                                      06/21/2021               Removed status field of MemberPlan
*    1.5          Vardhman Jain                                     12/22/2022               US3879280: Lightning - Consumer/Implement for CPD/General Search & Person Account Page
*    1.6	      Gowthami Thota				                    06/29/2023		         US4737500: Case Management (Provider) - Case Edit Page- Implement Plan member look up functionality in the Related Accounts section
*******************************************************************************************************************************************************************/

@isTest
public with sharing class PoliciesSearchResults_LT_HUM 
{
    
    @isTest
    public static void testPolicies(){
        User objUser = [Select Id,Name from User LIMIT 1];
        
        System.runAs(objUser) 
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
            
            Product2 p = new Product2(Name = 'DEN',ProductCode = 'DEN');
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
            
            Id accRecTypId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            Account oAccount = new Account();
            oAccount.Name__c = 'TestingAccount';
            oAccount.LastName = 'Demo';
            oAccount.RecordTypeId =accRecTypId; 
            insert oAccount; 
            
            Case cs = new Case();
            cs.Status = 'In Progress';
            cs.AccountId = oAccount.Id;
            insert cs;
            PurchaserPlan oPolicy = new PurchaserPlan(name = 'Test Policy', PayerId = oAccount.Id,Product__c = p.id,Policy_Group_Number__c='11111');
            insert oPolicy;  
            system.assert(oPolicy.id != null);
            
            PurchaserPlan oPolicy1 = new PurchaserPlan(name = 'Test Policy VIS', PayerId = oAccount.Id, Product__c = p2.id,Policy_Group_Number__c='11111');
            insert oPolicy1;  
            system.assert(oPolicy1.id != null);
            
            PurchaserPlan oPolicy3 = new PurchaserPlan(name = 'Test Policy VIS', PayerId = oAccount.Id, Product__c = p3.id,Policy_Group_Number__c='11111');
            insert oPolicy3;  
            system.assert(oPolicy3.id != null);
            
            List<MemberPlan> lstMemberPlan = new List<MemberPlan>();
            MemberPlan objMemberplan = new MemberPlan();
            objMemberplan.Name='MemberPlan1';
            objMemberplan.MemberId=oAccount.id;
            
            MemberPlan objMemberplan2 = new MemberPlan();
            objMemberplan2.Name='MemberPlan2';
            objMemberplan2.MemberId=oAccount.id;
            
            MemberPlan objMemberplan3 = new MemberPlan();
            objMemberplan3.Name='MemberPlan3';
            objMemberplan3.MemberId=cs.AccountId;
            
            
            lstMemberPlan.add(objMemberplan2);
            lstMemberPlan.add(objMemberPlan);
            lstMemberPlan.add(objMemberPlan3);
            insert lstMemberPlan;
            
            test.starttest(); 
            PoliciesSearchResults_LC_HUM.getMemberPlanDtls(oAccount.id);
            PoliciesSearchResults_LC_HUM.getMemberPlanDtls(cs.Id);
            
            test.stoptest();
            
        }
    }
    
    @isTest
    public static void testGroupPolicies() {
        User objUser1 = [Select Id,Name from User LIMIT 1];
        
        System.runAs(objUser1) 
        {
            Id accRecTypId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            Account oAccount = new Account();
            oAccount.Name = 'TestingAccount';
            oAccount.RecordTypeId =accRecTypId; 
            insert oAccount; 
            
            Product2 p = new Product2(Name = 'DEN',ProductCode = 'DEN');
            insert p;            
            //Assert the product data is created
            system.assert(p.id != null);
            
            //Create Policy, Product and Policy Member Data
            Product2 p2 = new Product2(Name = 'VIS',ProductCode = 'VIS');
            insert p2;            
            //Assert the product data is created
            system.assert(p2.id != null);
            
            PurchaserPlan oPolicy = new PurchaserPlan(name = 'Test Policy', PayerId = oAccount.Id,Product__c = p.id,Policy_Group_Number__c='11111');
            insert oPolicy;  
            system.assert(oPolicy.id != null);
            
            PurchaserPlan oPolicy1 = new PurchaserPlan(name = 'Test Policy VIS', PayerId = oAccount.Id, Product__c = p2.id,Policy_Group_Number__c='11111');
            insert oPolicy1;  
            system.assert(oPolicy1.id != null);
            
            List<PurchaserPlan> lstPolicies = PoliciesSearchResults_LC_HUM.getGroupAccPolicies(oAccount.id);
            system.assert(lstPolicies.size()==2);
        }
    }
    
    @isTest
    public static void testMemberPlan() {
        User objUser2 = [Select Id,Name from User LIMIT 1];
        
        System.runAs(objUser2) 
        {
            Id accRecTypId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            Account oAccount = new Account();
            oAccount.LastName = 'TestingAccount';
            oAccount.RecordTypeId =accRecTypId; 
            insert oAccount; 
            
            Product2 p = new Product2(Name = 'DEN',ProductCode = 'DEN');
            insert p;            
            //Assert the product data is created
            system.assert(p.id != null);
            
            //Create Policy, Product and Policy Member Data
            Product2 p2 = new Product2(Name = 'VIS',ProductCode = 'VIS');
            insert p2;            
            //Assert the product data is created
            system.assert(p2.id != null);
            
            PurchaserPlan oPolicy = new PurchaserPlan(name = 'Test Policy', PayerId = oAccount.Id,Product__c = p.id,Policy_Group_Number__c='11111');
            insert oPolicy;  
            system.assert(oPolicy.id != null);
            
            MemberPlan oMP = new MemberPlan( Name='test', MemberId=oAccount.Id,PlanId = oPolicy.Id);
            insert oMP;  
            system.assert(oMP.id != null);
            
            MemberPlan objMP = PoliciesSearchResults_LC_HUM.getMemberPlanDetails(oMP.id);
            system.assert(objMP != null);
        }
    }
    
    @isTest
    public static void testPurchaserPlan() {
       User objUser3 = [Select Id,Name from User LIMIT 1];
        
        System.runAs(objUser3) 
        {
            Id accRecTypId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            Account oAccount = new Account();
            oAccount.LastName = 'TestingAccount';
            oAccount.RecordTypeId =accRecTypId; 
            insert oAccount; 
            
            Product2 p = new Product2(Name = 'DEN',ProductCode = 'DEN');
            insert p;            
            //Assert the product data is created
            system.assert(p.id != null);
            
            //Create Policy, Product and Policy Member Data
            Product2 p2 = new Product2(Name = 'VIS',ProductCode = 'VIS');
            insert p2;            
            //Assert the product data is created
            system.assert(p2.id != null);
            
            PurchaserPlan oPolicy = new PurchaserPlan(name = 'Test Policy', PayerId = oAccount.Id,Product__c = p.id,Policy_Group_Number__c='11111');
            insert oPolicy;  
            system.assert(oPolicy.id != null);
            
            /*PurchaserPlan oPP = new PurchaserPlan( Name='test',Policy__c = oPolicy.Id);
            insert oPP;  
            system.assert(oPP.id != null);*/
            
            List<PurchaserPlan> objPP = PoliciesSearchResults_LC_HUM.getpurchaserplandetails(oPolicy.id);
            system.assert(objPP != null);
        }
    }

    @isTest
    public static void testPoliciesPanel(){
        //PolicesDetailsHighlightPanel
         Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER',Product_Type__c='MA');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
        
         Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1023880950';
            objAcc.General_Account__c = true;
            ObjAcc.BirthDate__c = Date.today().month() +'/'+Date.today().day()+'/'+Date.today().year();
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
        
        	PurchaserPlan objpol = new PurchaserPlan();
         	objpol.Policy_Group_Number__c='11111';
            objpol.Name='testPolicy';
            objpol.PayerId = objAcc.Id;
            objpol.Major_LOB__c = 'MGP';
            objpol.Source_Cust_Cov_Key__c = '1023880950';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            objpol.Client_Number__c = '57';
            objpol.Contract_Number__c='H1036';
            objpol.Issue_State__c='FL';
            objpol.Medicare_Segment_ID__c='2';
            objpol.PBP_Code__c='806';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'1023880950');
        
         MemberPlan objPolMem = new MemberPlan();
            objPolMem.Name = 'testpolmem11';
            objPolMem.EffectiveFrom=Date.today().addDays(-1);
            objPolMem.EffectiveTo =Date.today().addDays(1);
           	objPolMem.MemberId = objAcc.Id;
            objPolMem.PlanId = objpol.Id;
            objPolMem.Plan = objpol;
            objPolMem.ETL_Record_Deleted__c=False;
            objPolMem.EHB_Term_Date__c=Date.Today();
            objPolMem.EffectiveFrom = Date.Today();
            objPolMem.EffectiveTo = Date.Today().addYears(+10);
            insert objPolMem;
        
           MemberPlan MemObject = PoliciesSearchResults_LC_HUM.getPoliciesHighlightPanel(objPolMem.Id);
           system.assert(MemObject !=null);
    }
        
}