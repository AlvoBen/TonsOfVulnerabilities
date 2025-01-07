/**************************************************************************************************************************************************
Apex class Name    :  MemberIconDisplayHelper_T_HUM
Version            :  1.0
Function           :  This is the test class for MemberIconDisplayHelper_H_HUM  class

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *  Amar Gadewar                                         10/31/2018          Original Version
 *  Amar Gadewar                                         11/05/2018          Added date value in custom setting
 *  Santhi Mandava                                       08/06/2019          Implemented REQ - 403609 to display group medicare icon
 *  Amar Gadewar                                         02/17/2020          USER STORY 774245 - Clean Up of Unsed Code.
************************************************************************************************************************************************* */
@isTest
public with sharing class MemberIconsWrapper_T_HUM{
    
     /*
    * Method Name : prepareTestData
    * Description : Used to create wrapper class data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Product2 oProdMA = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MA', Major_LOB__c='MA', Product_Type__c='MA');
            insert oProdMA;
            System.assertNotEquals(oProdMA.Id, NULL, 'Value not matched');
            
            Product2 oProdMAPD = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MAPD', Major_LOB__c='MAPD', Product_Type__c='MAPD');
            insert oProdMAPD;
            System.assertNotEquals(oProdMAPD.Id, NULL, 'Value not matched');
          
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1023880950';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '1023880950';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            objpol.Client_Number__c = '57';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'1023880950');
            
            Policy__c objpolMA = HUMTestDataHelperTest.getPolicy();
            objpolMA.Name='testPolicyMA';
            objpolMA.Group_Name__c = objAcc.Id;
            objpolMA.Major_LOB__c = 'MA';
            objpolMA.Source_Cust_Cov_Key__c = '1023880951';
            objpolMA.Product__c = oProdMA.Id;
            objpolMA.Product__r = oProdMA;
            objpolMA.Platform__c = 'LV';
            objpolMA.Client_Number__c = '57';
            insert objpolMA;
            system.assertEquals(objpolMA.Source_Cust_Cov_Key__c,'1023880951');
            
            Policy__c objpolMAPD = HUMTestDataHelperTest.getPolicy();
            objpolMAPD.Name='testPolicyMAPD';
            objpolMAPD.Group_Name__c = objAcc.Id;
            objpolMAPD.Major_LOB__c = 'MAPD';
            objpolMAPD.Source_Cust_Cov_Key__c = '1023880952';
            objpolMAPD.Product__c = oProdMAPD.Id;
            objpolMAPD.Product__r = oProdMAPD;
            objpolMAPD.Platform__c = 'LV';
            objpolMAPD.Client_Number__c = '57';
            insert objpolMAPD;
            system.assertEquals(objpolMAPD.Source_Cust_Cov_Key__c,'1023880952');
            
            Policy__c objpolMPD = HUMTestDataHelperTest.getPolicy();
            objpolMPD.Name='testPolicyMPD';
            objpolMPD.Group_Name__c = objAcc.Id;
            objpolMPD.Major_LOB__c = 'MPD';
            objpolMPD.Source_Cust_Cov_Key__c = '1023880952';
            objpolMPD.Product__c = oProdMAPD.Id;
            objpolMPD.Product__r = oProdMAPD;
            objpolMPD.Platform__c = 'LV';
            objpolMPD.Client_Number__c = '57';
            insert objpolMPD;
            system.assertEquals(objpolMPD.Source_Cust_Cov_Key__c,'1023880952');
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Policy_Member__c objPolMemMPD = new Policy_Member__c();
            objPolMemMPD.Name = 'testpolmemMPD';
            objPolMemMPD.Relationship__c = 'Self';
            objPolMemMPD.Relationship_Code__c = '00';
            objPolMemMPD.Subscriber__c = objPolMem.Id;
            objPolMemMPD.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMemMPD.End_Date__c=Date.today().addDays(1).format();
            objPolMemMPD.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2011-07-01';
            objPolMemMPD.Member__c = objAcc.Id;
            objPolMemMPD.Policy__c = objpolMPD.Id;
            //objPolMemMPD.Policy__r = objpolMPD;
            insert objPolMemMPD;
            system.assertEquals(objPolMemMPD.Relationship__c, 'Self');
        }
    }   
    
    static testmethod void testNewMemberIndicator_1() {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        Account objAcc = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
        Policy__c objpolMA = [Select Id, Name from Policy__c where Name = 'testPolicyMA'];
        Policy__c objpolMAPD = [Select Id, Name from Policy__c where Name = 'testPolicyMAPD'];
        
        Policy_Member__c objPolMemMA = new Policy_Member__c();
        objPolMemMA.Name = 'testpolmem11';
        objPolMemMA.Relationship__c = 'Self';
        objPolMemMA.Relationship_Code__c = '00';
        objPolMemMA.Subscriber__c = objPolMemMA.Id;
        objPolMemMA.Effective_Date__c = '1/1/2020';
        objPolMemMA.End_Date__c = '12/31/9999';
        objPolMemMA.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2020-07-01';
        objPolMemMA.Member__c = objAcc.Id;
        objPolMemMA.Policy__c = objpolMA.Id;
        objPolMemMA.Policy__r = objpolMA;
        insert objPolMemMA;
        system.assertEquals(objPolMemMA.Relationship__c, 'Self');
        
        Policy_Member__c objPolMemMA1 = new Policy_Member__c();
        objPolMemMA1.Name = 'testpolmem2018';
        objPolMemMA1.Relationship__c = 'Self';
        objPolMemMA1.Relationship_Code__c = '00';
        objPolMemMA1.Subscriber__c = objPolMemMA.Id;
        objPolMemMA1.Effective_Date__c = '1/1/2018';
        objPolMemMA1.End_Date__c = '12/31/2018';
        objPolMemMA1.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2018-07-01';
        objPolMemMA1.Member__c = objAcc.Id;
        objPolMemMA1.Policy__c = objpolMA.Id;
        objPolMemMA1.Policy__r = objpolMA;
        insert objPolMemMA1;
        system.assertEquals(objPolMemMA1.Relationship__c, 'Self');
        
        Policy_Member__c objPolMemMA2 = new Policy_Member__c();
        objPolMemMA2.Name = 'testpolmem2017';
        objPolMemMA2.Relationship__c = 'Self';
        objPolMemMA2.Relationship_Code__c = '00';
        objPolMemMA2.Subscriber__c = objPolMemMA.Id;
        objPolMemMA2.Effective_Date__c = '1/1/2017';
        objPolMemMA2.End_Date__c = '12/31/2017';
        objPolMemMA2.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2017-07-01';
        objPolMemMA2.Member__c = objAcc.Id;
        objPolMemMA2.Policy__c = objpolMA.Id;
        objPolMemMA2.Policy__r = objpolMA;
        insert objPolMemMA2;
        system.assertEquals(objPolMemMA2.Relationship__c, 'Self');

        Policy_Member__c objPolMemMA3 = new Policy_Member__c();
        objPolMemMA3.Name = 'testpolmem2016';
        objPolMemMA3.Relationship__c = 'Self';
        objPolMemMA3.Relationship_Code__c = '00';
        objPolMemMA3.Subscriber__c = objPolMemMA.Id;
        objPolMemMA3.Effective_Date__c = '1/1/2016';
        objPolMemMA3.End_Date__c = '12/31/2016';
        objPolMemMA3.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2016-07-01';
        objPolMemMA3.Member__c = objAcc.Id;
        objPolMemMA3.Policy__c = objpolMAPD.Id;
        objPolMemMA3.Policy__r = objpolMAPD;
        insert objPolMemMA3;
        system.assertEquals(objPolMemMA3.Relationship__c, 'Self');
           
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
           System.assertEquals(oAccount.Tenant_Id__c, '00');
           String sAccRecType = oAccount.RecordType.Name;
           System.assertEquals(sAccRecType,'Member');
           
           Boolean bIsNewMember = MemberIcons_S_HUM.getNewMemberIconStatus(oAccount.Id);
           
           
           
           
           Test.stopTest();           
        }
    }
    
    static testmethod void testNewMemberIndicator_2() {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        Account objAcc = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
        Policy__c objpolMA = [Select Id, Name from Policy__c where Name = 'testPolicyMA'];
        Policy__c objpolMAPD = [Select Id, Name from Policy__c where Name = 'testPolicyMAPD'];
        
        Policy_Member__c objPolMemMA = new Policy_Member__c();
        objPolMemMA.Name = 'testpolmem11';
        objPolMemMA.Relationship__c = 'Self';
        objPolMemMA.Relationship_Code__c = '00';
        objPolMemMA.Subscriber__c = objPolMemMA.Id;
        objPolMemMA.Effective_Date__c = '1/1/2020';
        objPolMemMA.End_Date__c = '12/31/2020';
        objPolMemMA.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2020-07-01';
        objPolMemMA.Member__c = objAcc.Id;
        objPolMemMA.Policy__c = objpolMA.Id;
        objPolMemMA.Policy__r = objpolMA;
        insert objPolMemMA;
        system.assertEquals(objPolMemMA.Relationship__c, 'Self');       
        
        Policy_Member__c objPolMemMA2 = new Policy_Member__c();
        objPolMemMA2.Name = 'testpolmem2017';
        objPolMemMA2.Relationship__c = 'Self';
        objPolMemMA2.Relationship_Code__c = '00';
        objPolMemMA2.Subscriber__c = objPolMemMA.Id;
        objPolMemMA2.Effective_Date__c = '1/1/2017';
        objPolMemMA2.End_Date__c = '12/31/2017';
        objPolMemMA2.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2017-07-01';
        objPolMemMA2.Member__c = objAcc.Id;
        objPolMemMA2.Policy__c = objpolMA.Id;
        objPolMemMA2.Policy__r = objpolMA;
        insert objPolMemMA2;
        system.assertEquals(objPolMemMA2.Relationship__c, 'Self');

        Policy_Member__c objPolMemMA3 = new Policy_Member__c();
        objPolMemMA3.Name = 'testpolmem2016';
        objPolMemMA3.Relationship__c = 'Self';
        objPolMemMA3.Relationship_Code__c = '00';
        objPolMemMA3.Subscriber__c = objPolMemMA.Id;
        objPolMemMA3.Effective_Date__c = '1/1/2016';
        objPolMemMA3.End_Date__c = '12/31/2016';
        objPolMemMA3.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2016-07-01';
        objPolMemMA3.Member__c = objAcc.Id;
        objPolMemMA3.Policy__c = objpolMAPD.Id;
        objPolMemMA3.Policy__r = objpolMAPD;
        insert objPolMemMA3;
        system.assertEquals(objPolMemMA3.Relationship__c, 'Self');
        
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
           System.assertEquals(oAccount.Tenant_Id__c, '00');
           String sAccRecType = oAccount.RecordType.Name;
           System.assertEquals(sAccRecType,'Member');
           
           Boolean bIsNewMember = MemberIcons_S_HUM.getNewMemberIconStatus(oAccount.Id);
           
           
           
           Test.stopTest();           
        }
    }

    static testmethod void testNewMemberIndicator_3() {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        Account objAcc = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
        Policy__c objpolMA = [Select Id, Name from Policy__c where Name = 'testPolicyMA'];
        Policy__c objpolMAPD = [Select Id, Name from Policy__c where Name = 'testPolicyMAPD'];
        
        Policy_Member__c objPolMemMA = new Policy_Member__c();
        objPolMemMA.Name = 'testpolmem11';
        objPolMemMA.Relationship__c = 'Self';
        objPolMemMA.Relationship_Code__c = '00';
        objPolMemMA.Subscriber__c = objPolMemMA.Id;
        objPolMemMA.Effective_Date__c = '1/1/2020';
        objPolMemMA.End_Date__c = '12/31/2020';
        objPolMemMA.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2020-07-01';
        objPolMemMA.Member__c = objAcc.Id;
        objPolMemMA.Policy__c = objpolMAPD.Id;
        objPolMemMA.Policy__r = objpolMAPD;
        insert objPolMemMA;
        system.assertEquals(objPolMemMA.Relationship__c, 'Self');
           
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
           System.assertEquals(oAccount.Tenant_Id__c, '00');
           String sAccRecType = oAccount.RecordType.Name;
           System.assertEquals(sAccRecType,'Member');
           
           Boolean bIsNewMember = MemberIcons_S_HUM.getNewMemberIconStatus(oAccount.Id);
           
           
           Test.stopTest();           
        }
    }
    
    static testmethod void testNewMemberIndicator_4() {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        Account objAcc = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
        Policy__c objpolMA = [Select Id, Name from Policy__c where Name = 'testPolicyMA'];
        Policy__c objpolMAPD = [Select Id, Name from Policy__c where Name = 'testPolicyMAPD'];
        
        Policy_Member__c objPolMemMA = new Policy_Member__c();
        objPolMemMA.Name = 'testpolmem11';
        objPolMemMA.Relationship__c = 'Self';
        objPolMemMA.Relationship_Code__c = '00';
        objPolMemMA.Subscriber__c = objPolMemMA.Id;
        objPolMemMA.Effective_Date__c = '1/1/2020';
        objPolMemMA.End_Date__c = '12/31/2020';
        objPolMemMA.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2020-07-01';
        objPolMemMA.Member__c = objAcc.Id;
        objPolMemMA.Policy__c = objpolMA.Id;
        objPolMemMA.Policy__r = objpolMA;
        insert objPolMemMA;
        system.assertEquals(objPolMemMA.Relationship__c, 'Self');       
        
        Policy_Member__c objPolMemMA2 = new Policy_Member__c();
        objPolMemMA2.Name = 'testpolmem2017';
        objPolMemMA2.Relationship__c = 'Self';
        objPolMemMA2.Relationship_Code__c = '00';
        objPolMemMA2.Subscriber__c = objPolMemMA.Id;
        objPolMemMA2.Effective_Date__c = '1/1/2018';
        objPolMemMA2.End_Date__c = '08/31/2018';
        objPolMemMA2.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2017-07-01';
        objPolMemMA2.Member__c = objAcc.Id;
        objPolMemMA2.Policy__c = objpolMA.Id;
        objPolMemMA2.Policy__r = objpolMA;
        insert objPolMemMA2;
        system.assertEquals(objPolMemMA2.Relationship__c, 'Self');
           
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
           System.assertEquals(oAccount.Tenant_Id__c, '00');
           String sAccRecType = oAccount.RecordType.Name;
           System.assertEquals(sAccRecType,'Member');
           
           Boolean bIsNewMember = MemberIcons_S_HUM.getNewMemberIconStatus(oAccount.Id);
           
           
           Test.stopTest();           
        }
    }  
    
    static testmethod void testNewMemberIndicator_5() {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        Account objAcc = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
        Policy__c objpolMA = [Select Id, Name from Policy__c where Name = 'testPolicyMA'];
        Policy__c objpolMAPD = [Select Id, Name from Policy__c where Name = 'testPolicyMAPD'];
        
        Policy_Member__c objPolMemMA = new Policy_Member__c();
        objPolMemMA.Name = 'testpolmem11';
        objPolMemMA.Relationship__c = 'Self';
        objPolMemMA.Relationship_Code__c = '00';
        objPolMemMA.Subscriber__c = objPolMemMA.Id;
        objPolMemMA.Effective_Date__c = '1/1/2020';
        objPolMemMA.End_Date__c = '12/31/2020';
        objPolMemMA.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2020-07-01';
        objPolMemMA.Member__c = objAcc.Id;
        objPolMemMA.Policy__c = objpolMA.Id;
        objPolMemMA.Policy__r = objpolMA;
        insert objPolMemMA;
        system.assertEquals(objPolMemMA.Relationship__c, 'Self');       
        
        Policy_Member__c objPolMemMA2 = new Policy_Member__c();
        objPolMemMA2.Name = 'testpolmem2017';
        objPolMemMA2.Relationship__c = 'Self';
        objPolMemMA2.Relationship_Code__c = '00';
        objPolMemMA2.Subscriber__c = objPolMemMA.Id;
        objPolMemMA2.Effective_Date__c = '1/1/2018';
        objPolMemMA2.End_Date__c = '04/31/2018';
        objPolMemMA2.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2017-07-01';
        objPolMemMA2.Member__c = objAcc.Id;
        objPolMemMA2.Policy__c = objpolMAPD.Id;
        objPolMemMA2.Policy__r = objpolMAPD;
        insert objPolMemMA2;
        system.assertEquals(objPolMemMA2.Relationship__c, 'Self');
           
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
           System.assertEquals(oAccount.Tenant_Id__c, '00');
           String sAccRecType = oAccount.RecordType.Name;
           System.assertEquals(sAccRecType,'Member');
           
           Boolean bIsNewMember = MemberIcons_S_HUM.getNewMemberIconStatus(oAccount.Id);
           
           
           Test.stopTest();           
        }
    } 
    
    static testmethod void testNewMemberIndicator_6() {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        Account objAcc = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
        Policy__c objpolMA = [Select Id, Name from Policy__c where Name = 'testPolicyMA'];
        Policy__c objpolMAPD = [Select Id, Name from Policy__c where Name = 'testPolicyMAPD'];
        
        Policy_Member__c objPolMemMA = new Policy_Member__c();
        objPolMemMA.Name = 'testpolmem11';
        objPolMemMA.Relationship__c = 'Self';
        objPolMemMA.Relationship_Code__c = '00';
        objPolMemMA.Subscriber__c = objPolMemMA.Id;
        objPolMemMA.Effective_Date__c = '12/1/2018';
        objPolMemMA.End_Date__c = '06/31/2019';
        objPolMemMA.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2019-07-01';
        objPolMemMA.Member__c = objAcc.Id;
        objPolMemMA.Policy__c = objpolMA.Id;
        objPolMemMA.Policy__r = objpolMA;
        insert objPolMemMA;
             
           
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
           System.assertEquals(oAccount.Tenant_Id__c, '00');
           String sAccRecType = oAccount.RecordType.Name;
           System.assertEquals(sAccRecType,'Member');
           
           
           Boolean bIsNewMember = MemberIcons_S_HUM.getNewMemberIconStatus(oAccount.Id);
           
           
           Test.stopTest();           
        }
    }
}