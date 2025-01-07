/*******************************************************************************************************************************
Test Class Name : PurchaserPlanTrigger_LT_HUM
Version         : 1.0
Created On      : 11/09/2020
Function        : Test class for PurchaserPlanTriggerHandler_H_HUM

Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Anjani Vaddadi                                 11/09/2020                  Original Version
*/
@isTest
public class PurchaserPlanTrigger_LT_HUM {
    /*
* Method Name : setup
* Description : Create common test Data
* Return type : void
* Parameter   : None
*/ 
    @testSetup static void setup() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
         boolean bSwitch = CRMFunctionalityEnabler_H_HUM.Checkflagvalue('PurchaserPlanBeforeInsertTriggerSwitch');
        insert New HUMTriggerSwitch__c(name = 'PurchaserPlan', Exeute_Trigger__c = true);
        System.runAs (oCCSUser) 
        {

            List<Account> accountList = new List<Account>();   
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.Mbr_Gen_Key__c = '12345676';
            objAcc.General_Account__c = true;
            objAcc.Home_Office_Account__c = false;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            accountList.add(objAcc);
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            System.assertEquals(objAcc.Enterprise_ID__c,'1005577525');
            System.assertEquals(objAcc.LastName,'Account');
            
            Account objAcc2 = HUMTestDataHelperTest.getAccount(); 
            objAcc2.FirstName = 'Test';
            objAcc2.LastName = 'Account2';
            objAcc2.Tenant_Id__c = '01';
            objAcc2.Enterprise_ID__c = '';
            objAcc2.General_Account__c = true;
            objAcc.Home_Office_Account__c = false;
            objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            accountList.add(objAcc2);
            System.assertEquals(objAcc2.Tenant_Id__c,'01');
            System.assertEquals(objAcc2.Enterprise_ID__c,'');
            System.assertEquals(objAcc2.LastName,'Account2');
            insert accountList;
            system.assertequals(accountList.size(),2); 
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            List<Policy__c> policyList = new List<Policy__c>();
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            
            
            policyList.add(objpol);
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            system.assertEquals(objpol.Group_Name__c,objAcc.Id);
            system.assertEquals(objpol.Name,'testPolicy');
            
            Policy__c objpol2 = HUMTestDataHelperTest.getPolicy();
            objpol2.Name='testPolicy2';
            objpol2.Group_Name__c = objAcc2.Id;
            objpol2.Major_LOB__c = 'MER';
            objpol2.Source_Cust_Cov_Key__c = '123456789';
            objpol2.Product__c = oProd.Id;
            objpol2.Product__r = oProd;
            objpol2.Platform__c = 'LV';
            policyList.add(objpol2);
            system.assertEquals(objpol2.Source_Cust_Cov_Key__c,'123456789');
            system.assertEquals(objpol2.Group_Name__c,objAcc2.Id);
            system.assertEquals(objpol2.Name,'testPolicy2');
            insert policyList;
            system.assertequals(policyList.size(),2);
        }
    }
    /*
* Method Name : runPositiveData
* Description : Used to run scenerios of test data
* Return type : void
* Paramater   : NA
*/ 
    static testMethod void runPositiveData(){
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        boolean bSwitch = CRMFunctionalityEnabler_H_HUM.Checkflagvalue('PurchaserPlanBeforeInsertTriggerSwitch');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest(); 
            if(bSwitch){
            List<Policy__c> LiPolicy =[Select Id, group_Name__c, group_name__r.General_Account__c, group_name__r.Home_Office_Account__c from Policy__c where group_name__r.LastName = 'Account' OR group_name__r.LastName ='Account2' order by group_name__r.Tenant_Id__c];
            System.assertNotEquals(LiPolicy[0], NULL);
            
            List<PurchaserPlan> testpp = new List<PurchaserPlan>();
            PurchaserPlan pp = new  PurchaserPLan();
            pp.Name = 'testpp2';
            pp.policy__c = LiPolicy[0].Id;
            testpp.add(pp);
            
            PurchaserPlan pp1 = new  PurchaserPLan();
            pp1.Name = 'testpp';            
            pp1.policy__c = LiPolicy[1].Id;
            testpp.add(pp1);        
            insert testpp; 
            
            PurchaserPlan testRecord = [SELECT Id, Name, PayerId, Policy__c, General_Account__c, Home_Office_Account__c FROM PurchaserPlan where Policy__c =:LiPolicy[0].Id];
            testRecord.Policy__c =LiPolicy[1].Id;
            update testRecord;
            system.assertEquals(testRecord.General_Account__c,true);
            system.assertEquals(testRecord.Home_Office_Account__c,false);
            }
            Test.stopTest();
        }
    }
    
    /*
* Method Name : runNegativeData
* Description : Used to run negative scenerios of test data
* Return type : void
* Paramater   : NA
*/ 
    static testMethod void runNegativeData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        boolean bSwitch = CRMFunctionalityEnabler_H_HUM.Checkflagvalue('PurchaserPlanBeforeInsertTriggerSwitch');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            if(bSwitch){
            List<Policy__c> LiPolicy =[Select Id, group_Name__c, group_name__r.General_Account__c, group_name__r.Home_Office_Account__c from Policy__c where group_name__r.LastName = 'Account' OR group_name__r.LastName ='Account2' order by group_name__r.Tenant_Id__c];            
            System.assertNotEquals(LiPolicy[0], NULL);
            
            List<PurchaserPlan> testpp = new List<PurchaserPlan>();
            PurchaserPlan pp = new  PurchaserPLan();
            pp.Name = 'test2pp2';
            testpp.add(pp);
            
            PurchaserPlan pp1 = new  PurchaserPLan();
            pp1.Name = 'test2pp1';            
            testpp.add(pp1);           
            insert testpp; 
            
            PurchaserPlan testRecord =[SELECT Id, Name, Policy__c, GEneral_Account__c, Home_Office_Account__c FROM PurchaserPlan where Name='test2pp1'];            
            system.assertEquals(testRecord.policy__c, NULL);
            }
            Test.stopTest();
        }
    }
}