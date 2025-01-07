/**************************************************************************** 
Apex Class Name  : HUMPolicyAccessDetailsWrapperHelperTest 
Version          : 1.0  
Created Date     : September 29 2014 
Description      : This class is used as test class for HUMPolicyAccessDetailsWrapperHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shruthi Karanth         18234                     09/29/2014                     Original Version
* Harika Devi Kalidindi   112000                    07/20/2017                     REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
* Keerti Durbha                                     04/23/2020                     738134-PR00091318 - MF 9 - Medicare OSB Vision And Fitness
* Sagar MN                                          08/06/2020                     USER STORY 1329433 EDW Reporting changes - Increased the test coverage
* Atia Uzma                                         06/17/2021                     Test Coverage
* Amar Gadewar                                      07/07/2021                     User Story 2451265: T1PRJ0001827 - MF 1 - AHT - Ability to default the value of Medicare Call Part C/D field in QS to 'Not Medicare' for Medicaid Policies (CRM)
* Atia Uzma                                         08/05/2021                     User Story 2551785: T1PRJ0001827 - MF 1 - SF - TECH - On/Off switch cleanup for 8/20/21 Release (CRM)
***************************************************************************************************/

@isTest
private Class HUMPolicyAccessDetailsWrapperHelperTest 
{
    
        /**
    * Test Setup method to create all the data required for testing.
    *
    * @param     NULL
    * @return    void 
    */
   @testSetup  static void setUpTestData()
   {
     User oCCSUser = HUMTestDataHelperTest.createTestUser('Userprll','Customer Care Specialist');
        insert oCCSUser;
        System.assert(oCCSUser.LastName == 'test Userprll');
        
        Account acc = HUMTestDataHelperTest.getAccount();
        acc.OwnerId=oCCSUser.id;
        acc.Name = 'Test Account Cases';
        acc.Enterprise_ID__c = '45678';
        acc.Group_Number__c = '12345678';
        acc.Source_Customer_Key__c ='234234324234232';
        insert acc;
        System.assertEquals(acc.id!=null,true);
        
        Product2 prod = new Product2();
        prod.Name='MED';
        prod.Sold_Product_Key_Value__c ='prodKey1234';
        prod.Product_Type__c ='MA';
        prod.Major_LOB__c = 'MEP';
        insert prod;
        System.assertNotEquals(prod.Id,null,'?Value not matched');
        
        Policy__c policy=HUMTestDataHelperTest.getPolicy();
        list<Policy__c> lstpolicy=new list<Policy__c> ();
        policy.Name = 'TestPolicy';
        policy.Group_Name__c=acc.id;
        policy.Exchange_Indicator__c = true;
        policy.Product__c =prod.id ;
        policy.Source_Cust_Cov_Key__c='2234233432423423422323';
        policy.Benefit_Coverage__c='HYG1';
        policy.Platform__c='LV';       
        policy.Coverage_Plan_Effective_Date__c='08/01/2015';
        lstpolicy.add(policy);
        insert lstpolicy;
        System.assertEquals(policy.id!=null,true);
        
        Policy_Member__c polMember = new Policy_Member__c();
        polMember.Name = 'testpolmem2';
        polMember.Relationship__c = 'Self';
        polMember.Effective_Date__c=Date.today().addDays(-1).format();
        polMember.End_Date__c=Date.today().addDays(1).format();
        polMember.Policy_Member_External_ID__c='00|ODS|5004434817|12345678|34567|2013-07-01';
        polMember.Member__c = acc.Id;
        polMember.Policy__c = policy.Id;
        //polMember.OSB_Indicator__c = 'O'; 
        insert polMember;
        system.assertEquals(polMember.id!=null, true);
       
        Member_ID__c member = new Member_ID__c();
        member.Name= '12345';
        member.Type__c='Member-Id-Base';
        member.Policy_Member__c= polMember.id;
        member.policy__c = policy.Id;
        insert member;
       
       //platform EM
        Product2 prod1 = new Product2();
        prod1.Name='DEN';
        prod1.Sold_Product_Key_Value__c ='IL3E0875';
        prod1.Product_Type__c ='PPO';
        insert prod1;
        System.assertNotEquals(prod1.Id,null,'?Value not matched');
        
        Policy__c policy1=HUMTestDataHelperTest.getPolicy();
        list<Policy__c> lstpolicy1=new list<Policy__c> ();
        policy1.Name = 'TestPolicy1';
        policy1.Group_Name__c=acc.id;
        policy1.Exchange_Indicator__c = true;
        policy1.Product__c =prod1.id ;
        policy1.Source_Cust_Cov_Key__c='2234233432423423422323';
        policy1.Benefit_Coverage__c='HYG1';
        policy1.Platform__c='EM';       
        policy1.Coverage_Plan_Effective_Date__c='08/01/2015';
        lstpolicy1.add(policy1);
        insert lstpolicy1;
        System.assertEquals(policy1.id!=null,true);
        
        Policy_Member__c polMember1 = new Policy_Member__c();
        polMember1.Name = 'testpolmemtest';
        polMember1.Relationship__c = 'Self';
        polMember1.Effective_Date__c=Date.today().addDays(-1).format();
        polMember1.End_Date__c=Date.today().addDays(1).format();
        polMember1.Policy_Member_External_ID__c='00|ODS|5004434817|12345678|34567|2014-07-01';
        polMember1.Member__c = acc.Id;
        polMember1.Policy__c = policy1.Id;
        //polMember1.OSB_Indicator__c = 'O'; 
        insert polMember1;
        system.assertEquals(polMember1.id!=null, true);
        
        
        //platform CB
        Product2 prod2 = new Product2();
        prod2.Name='VIS';
        prod2.Sold_Product_Key_Value__c ='IL3E0875';
        prod2.Product_Type__c ='PPO';
        insert prod2;
        System.assertNotEquals(prod2.Id,null,'?Value not matched');
        
        Policy__c policy2=HUMTestDataHelperTest.getPolicy();
        list<Policy__c> lstpolicy2=new list<Policy__c> ();
        policy2.Name = 'TestPolicy2';
        policy2.Group_Name__c=acc.id;
        policy2.Exchange_Indicator__c = true;
        policy2.Product__c =prod2.id ;
        policy2.Source_Cust_Cov_Key__c='2234233432423423422323';
        policy2.Benefit_Coverage__c='HYG1';
        policy2.Platform__c='CB';       
        policy2.Coverage_Plan_Effective_Date__c='08/01/2015';
        lstpolicy2.add(policy2);
        insert lstpolicy2;
        System.assertEquals(policy2.id!=null,true);
        
        Policy_Member__c polMember2 = new Policy_Member__c();
        polMember2.Name = 'testpolmemtest4';
        polMember2.Relationship__c = 'Self';
        polMember2.Effective_Date__c=Date.today().addDays(-1).format();
        polMember2.End_Date__c=Date.today().addDays(1).format();
        polMember2.Policy_Member_External_ID__c='00|ODS|5004434817|12345678|34567|2011-07-01';
        polMember2.Member__c = acc.Id;
        polMember2.Policy__c = policy2.Id;
        //polMember2.OSB_Indicator__c = 'O'; 
        insert polMember2;
        system.assertEquals(polMember2.id!=null, true);
       

       

 
       Product2 prod3 = new Product2();
       prod3.Name='MED';
       prod3.Major_LOB__c = 'PDP';
       prod3.Sold_Product_Key_Value__c ='prodKey1234';
       prod3.Product_Type__c ='PDP';
       insert prod3;
       System.assertNotEquals(prod3.Id,null,'?Value not matched');



       Policy__c policy3=HUMTestDataHelperTest.getPolicy();
       list<Policy__c> lstpolicy3=new list<Policy__c> ();
       policy3.Name = 'TestPolicy3';
       policy3.Group_Name__c=acc.id;
       policy3.Exchange_Indicator__c = true;
       policy3.Product__c =prod3.id ;
       policy3.Source_Cust_Cov_Key__c='2234233432423423422323';
       policy3.Benefit_Coverage__c='HYG1';
       policy3.Platform__c='CB';       
       policy3.Coverage_Plan_Effective_Date__c='08/01/2015';
       lstpolicy3.add(policy3);
       insert lstpolicy3;
       System.assertEquals(policy3.id!=null,true);
       
       Policy_Member__c polMember3 = new Policy_Member__c();
       polMember3.Name = 'testpolmemtest5';
       polMember3.Relationship__c = 'Self';
       polMember3.Effective_Date__c=Date.today().addDays(-1).format();
       polMember3.End_Date__c=Date.today().addDays(1).format();
       polMember3.Policy_Member_External_ID__c='00|ODS|5004434817|12345678|34567|2021-07-08';
       polMember3.Member__c = acc.Id;
       polMember3.Policy__c = policy3.Id;
       //polMember2.OSB_Indicator__c = 'O'; 
       insert polMember3;
       system.assertEquals(polMember3.id!=null, true);




       Product2 prod4 = new Product2();
       prod4.Name='MED';
       prod4.Sold_Product_Key_Value__c ='prodKey1234';
       prod4.Major_LOB__c = 'MCD';
       insert prod4;
       System.assertNotEquals(prod4.Id,null,'?Value not matched');



       
       Policy__c policy4=HUMTestDataHelperTest.getPolicy();
       list<Policy__c> lstpolicy4=new list<Policy__c> ();
       policy4.Name = 'TestPolicy4';
       policy4.Group_Name__c=acc.id;
       policy4.Exchange_Indicator__c = true;
       policy4.Product__c =prod4.id ;
       policy4.Source_Cust_Cov_Key__c='2234233432423423422323';
       policy4.Benefit_Coverage__c='HYG1';
       policy4.Platform__c='CB';       
       policy4.Coverage_Plan_Effective_Date__c='08/01/2015';
       lstpolicy4.add(policy4);
       insert lstpolicy4;
       System.assertEquals(policy4.id!=null,true);
       
       Policy_Member__c polMember4 = new Policy_Member__c();
       polMember4.Name = 'testpolmemtest6';
       polMember4.Relationship__c = 'Self';
       polMember4.Effective_Date__c=Date.today().addDays(-1).format();
       polMember4.End_Date__c=Date.today().addDays(1).format();
       polMember4.Policy_Member_External_ID__c='00|ODS|5004434817|12345678|34567|2021-07-09';
       polMember4.Member__c = acc.Id;
       polMember4.Policy__c = policy4.Id;
       //polMember2.OSB_Indicator__c = 'O'; 
       insert polMember4;
       system.assertEquals(polMember4.id!=null, true);

       CRMFunctionality_ONOFF_Switch__c objSwitch1 = new CRMFunctionality_ONOFF_Switch__c(Name = '738134',IsON__c = true);
       insert objSwitch1;
       CRMFunctionality_ONOFF_Switch__c objSwitch2 = new CRMFunctionality_ONOFF_Switch__c(Name = '1279870',IsON__c = true);
       insert objSwitch2;
       CRMFunctionality_ONOFF_Switch__c objSwitch3 = new CRMFunctionality_ONOFF_Switch__c(Name = '2369318',IsON__c = true);
       insert objSwitch3;


       } 
     /**
    * This method is used to test Policy related list Methods
    *
    * @param     NULL
    * @return    void 
    */
  private static testMethod void testMethods()
  {
     User objuser=[select id,lastName from user where lastName='test Userprll'];  
     System.assertEquals('test Userprll', objuser.lastName); 
      System.runAs(objuser)
        {
           Test.StartTest();
            Policy_Member__c objpolicyMember = [SELECT id,Name,Product__c,policy__r.Product_Type__c,Product_Type__c,ASO__c,Relationship__c,OSB_Indicator__c, policy__r.Contract_Number__c,policy__r.PBP_Code__c,policy__r.Medicare_Segment_ID__c,policy__r.Source_Cust_Cov_Key__c,policy__r.Benefit_Coverage__c,policy__r.Coverage_Plan_Effective_Date__c,policy__r.Platform__c,Group_Name__c,Group_Number__c,Effective_Date__c,End_Date__c,Status__c,EHB_Term_Date__c, ETL_Record_Deleted__c,LastModifiedDate
                                         FROM Policy_Member__c where name = 'testpolmem2'];            
            HUMPolicyAccessDetailsWrapperHelper dtoObj = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember , true ,'MED');
            system.assert(dtoObj.hasAccess = true);
            HUMPolicyAccessDetailsWrapperHelper dtoObj1 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember , false ,'MED');
            HUMPolicyAccessDetailsWrapperHelper dtoObj2 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember , true ,'MEP');
            HUMPolicyAccessDetailsWrapperHelper dtoObj3 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember , false ,'MEP');
            HUMPolicyAccessDetailsWrapperHelper dtoObj4 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember,true,'');
            Product2 objprod1=[select id,name,Sold_Product_Key_Value__c  from Product2 where name ='DEN'];
             Policy__c objpolicy1 =[select id,name,Group_Name__c,Exchange_Indicator__c ,Product__c,Source_Cust_Cov_Key__c,Platform__c,Benefit_Coverage__c,Coverage_Plan_Effective_Date__c from  Policy__c  where name='TestPolicy1'];
             Policy_Member__c objpolicyMember1 = [SELECT id,Name,Product__c,policy__r.Product_Type__c,Product_Type__c,ASO__c,Relationship__c,OSB_Indicator__c, policy__r.Contract_Number__c,policy__r.PBP_Code__c,policy__r.Medicare_Segment_ID__c,policy__r.Source_Cust_Cov_Key__c,policy__r.Benefit_Coverage__c,policy__r.Coverage_Plan_Effective_Date__c,policy__r.Platform__c,Group_Name__c,Group_Number__c,Effective_Date__c,End_Date__c,Status__c,EHB_Term_Date__c, ETL_Record_Deleted__c,LastModifiedDate
                                         FROM Policy_Member__c where Product__c=:objprod1.Name];   
            HUMPolicyAccessDetailsWrapperHelper dtoObj5 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember1 , false ,'DEN');
            HUMPolicyAccessDetailsWrapperHelper dtoObj6 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember1 , true ,'DEN');
            
            Product2 objprod2=[select id,name,Sold_Product_Key_Value__c  from Product2 where name ='VIS'];
             Policy__c objpolicy2 =[select id,name,Group_Name__c,Exchange_Indicator__c ,Product__c,Source_Cust_Cov_Key__c,Platform__c,Benefit_Coverage__c,Coverage_Plan_Effective_Date__c from  Policy__c  where name='TestPolicy2'];
             Policy_Member__c objpolicyMember2 = [SELECT id,Name,Product__c,policy__r.Product_Type__c,Product_Type__c,ASO__c,Relationship__c,OSB_Indicator__c, policy__r.Contract_Number__c,policy__r.PBP_Code__c,policy__r.Medicare_Segment_ID__c,policy__r.Source_Cust_Cov_Key__c,policy__r.Benefit_Coverage__c,policy__r.Coverage_Plan_Effective_Date__c,policy__r.Platform__c,Group_Name__c,Group_Number__c,Effective_Date__c,End_Date__c,Status__c,EHB_Term_Date__c, ETL_Record_Deleted__c,LastModifiedDate
                                         FROM Policy_Member__c where Product__c=:objprod2.Name];   
            HUMPolicyAccessDetailsWrapperHelper dtoObj8 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember2 , false ,'VIS');
            HUMPolicyAccessDetailsWrapperHelper dtoObj9 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember2 , true ,'VIS');
            HUMPolicyAccessDetailsWrapperHelper dtoObj10 = new HUMPolicyAccessDetailsWrapperHelper();
            HUMPolicyAccessDetailsWrapperHelper dtoObj11 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember1 , false ,'MPD');
            HUMPolicyAccessDetailsWrapperHelper dtoObj12 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember1 , true ,'MCD');

            HUMPolicyAccessDetailsWrapperHelper dtoObj13 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember , false ,'MED');



            Policy_Member__c objpolicyMember3 = [SELECT id,Name,Product__c,policy__r.Product_Type__c,Product_Type__c,ASO__c,Relationship__c,OSB_Indicator__c, policy__r.Contract_Number__c,policy__r.PBP_Code__c,policy__r.Medicare_Segment_ID__c,policy__r.Source_Cust_Cov_Key__c,policy__r.Benefit_Coverage__c,policy__r.Coverage_Plan_Effective_Date__c,policy__r.Platform__c,Group_Name__c,Group_Number__c,Effective_Date__c,End_Date__c,Status__c,EHB_Term_Date__c, ETL_Record_Deleted__c,LastModifiedDate
            FROM Policy_Member__c where name = 'testpolmemtest5'];  
            
            HUMPolicyAccessDetailsWrapperHelper dtoObj14 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember3 , true ,'PDP');

            Policy_Member__c objpolicyMember4 = [SELECT id,Name,Product__c,policy__r.Product_Type__c,Product_Type__c,ASO__c,Relationship__c,OSB_Indicator__c, policy__r.Contract_Number__c,policy__r.PBP_Code__c,policy__r.Medicare_Segment_ID__c,policy__r.Source_Cust_Cov_Key__c,policy__r.Benefit_Coverage__c,policy__r.Coverage_Plan_Effective_Date__c,policy__r.Platform__c,Group_Name__c,Group_Number__c,Effective_Date__c,End_Date__c,Status__c,EHB_Term_Date__c, ETL_Record_Deleted__c,LastModifiedDate
            FROM Policy_Member__c where name = 'testpolmemtest6'];  
            
            HUMPolicyAccessDetailsWrapperHelper dtoObj15 = new HUMPolicyAccessDetailsWrapperHelper(objpolicyMember3 , true ,'MCD');



            Test.stopTest();
            
        }
  }
    
     static testMethod void testMEDpolicy()
  	{
        User objuser=[select id,lastName from user where lastName='test Userprll'];  
        System.assertEquals('test Userprll', objuser.lastName);   
        
        User objUserHumanaSpecialist = HUMTestDataHelperTest.createTestUser('User2','Humana Pharmacy Specialist');   
        insert objUserHumanaSpecialist;
      System.runAs(objuser)
      {  
          
          Account actMember = new Account(Name = 'Test Member Account');    
          insert actMember;
          system.assert(actMember.id != null);
          
          Product2 p = new Product2(Name = 'MED',ProductCode = 'MED',Product_Type__c = 'MCD',Sold_Product_Key_Value__c='IL3E0875');
          insert p;            
          system.assert(p.id != null);
          
          string sGroupRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
          Account actGroup = new Account(Name = 'Test Group Account',recordtypeId = sGroupRecordtypeId);  
          insert actGroup ;            
          system.assert(actGroup.id != null);
                
          Policy__c oPolicy = new Policy__c(name = 'Test Policy',Group_Name__c = actGroup.Id,Product__c = p.id,Policy_Group_Number__c='11111');
          insert oPolicy;  
          system.assert(oPolicy.id != null);
          
          Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = actMember.id,Effective_Date__c=Date.today().addDays(-5).format(),End_Date__c=Date.today().addDays(10).format());            
          insert oPolicyMem;
          
          CRMFunctionality_ONOFF_Switch__c objSwitch1 = new CRMFunctionality_ONOFF_Switch__c(Name = '738134',IsON__c = true);
          insert objSwitch1;
          CRMFunctionality_ONOFF_Switch__c objSwitch2 = new CRMFunctionality_ONOFF_Switch__c(Name = '1279870',IsON__c = true);
          insert objSwitch2;
          CRMFunctionality_ONOFF_Switch__c objSwitch3 = new CRMFunctionality_ONOFF_Switch__c(Name = '2369318',IsON__c = true);
          insert objSwitch3;
          
          HUMPolicyAccessDetailsWrapperHelper opolicyaccess = new HUMPolicyAccessDetailsWrapperHelper(oPolicyMem , true ,'MED');
          HUMPolicyAccessDetailsWrapperHelper opolicyaccess2 = new HUMPolicyAccessDetailsWrapperHelper(oPolicyMem , false ,'PDP');
          HUMPolicyAccessDetailsWrapperHelper opolicyaccess1 = new HUMPolicyAccessDetailsWrapperHelper(oPolicyMem , true ,'MCD');
      }
  }
    
     static testMethod void testpolicy()
  	{
        User objuser=[select id,lastName from user where lastName='test Userprll'];  
        System.assertEquals('test Userprll', objuser.lastName);   
        
        User objUserHumanaSpecialist = HUMTestDataHelperTest.createTestUser('User2','Humana Pharmacy Specialist');   
        insert objUserHumanaSpecialist;
      System.runAs(objuser)
      { 
         Account actMember = new Account(Name = 'Test Member Account');    
          insert actMember;
          system.assert(actMember.id != null);
          
          Product2 p = new Product2(Name = 'DEN',Product_Type__c = 'PPO',Sold_Product_Key_Value__c='IL3E0875');
          insert p;            
          system.assert(p.id != null);
          
          string sGroupRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
          Account actGroup = new Account(Name = 'Test Group Account',recordtypeId = sGroupRecordtypeId);  
          insert actGroup ;            
          system.assert(actGroup.id != null);
                
          Policy__c oPolicy = new Policy__c(name = 'Test Policy',Group_Name__c = actGroup.Id,Product__c = p.id,Policy_Group_Number__c='11111');
          insert oPolicy;  
          system.assert(oPolicy.id != null);
          
          Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = actMember.id,Effective_Date__c=Date.today().addDays(-5).format(),End_Date__c=Date.today().addDays(10).format());            
          insert oPolicyMem; 
          
          HUMPolicyAccessDetailsWrapperHelper opolicyaccess = new HUMPolicyAccessDetailsWrapperHelper(oPolicyMem , true ,'DEN');
  	  }
    }
    
     static testMethod void testPDPpolicy()
  	{
        User objuser=[select id,lastName from user where lastName='test Userprll'];  
        System.assertEquals('test Userprll', objuser.lastName);   

      System.runAs(objuser)
      { 
         Account actMember = new Account(Name = 'Test Member Account');    
          insert actMember;
          system.assert(actMember.id != null);
          
          Product2 p = new Product2(Name = 'MED',Product_Type__c = 'PDP',Sold_Product_Key_Value__c='IL3E0875');
          insert p;            
          system.assert(p.id != null);
          
          string sGroupRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
          Account actGroup = new Account(Name = 'Test Group Account',recordtypeId = sGroupRecordtypeId);  
          insert actGroup ;            
          system.assert(actGroup.id != null);
                
          Policy__c oPolicy = new Policy__c(name = 'Test Policy',Group_Name__c = actGroup.Id,Product__c = p.id,Policy_Group_Number__c='11111');
          insert oPolicy;  
          system.assert(oPolicy.id != null);
          
          Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = actMember.id,Effective_Date__c=Date.today().addDays(-5).format(),End_Date__c=Date.today().addDays(10).format());            
          insert oPolicyMem; 
          
          HUMPolicyAccessDetailsWrapperHelper opolicyaccess = new HUMPolicyAccessDetailsWrapperHelper(oPolicyMem , true ,'PDP');
  	  }
    }
    
     static testMethod void testmethodpolicy()
  	{
        User objuser=[select id,lastName from user where lastName='test Userprll'];  
        System.assertEquals('test Userprll', objuser.lastName);   

      System.runAs(objuser)
      { 
         Account actMember = new Account(Name = 'Test Member Account');    
          insert actMember;
          system.assert(actMember.id != null);
          
          Product2 p = new Product2(Name = 'MED',Product_Type__c = 'PDP',Sold_Product_Key_Value__c='IL3E0875');
          insert p;            
          system.assert(p.id != null);
          
          string sGroupRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
          Account actGroup = new Account(Name = 'Test Group Account',recordtypeId = sGroupRecordtypeId);  
          insert actGroup ;            
          system.assert(actGroup.id != null);
                
          Policy__c oPolicy = new Policy__c(name = 'Test Policy',Group_Name__c = actGroup.Id,Product__c = p.id,Policy_Group_Number__c='11111');
          insert oPolicy;  
          system.assert(oPolicy.id != null);
          
          Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = actMember.id,Effective_Date__c=Date.today().addDays(-5).format(),End_Date__c=Date.today().addDays(10).format(), OSB_Indicator__c = 'O');            
          insert oPolicyMem; 
          
          HUMPolicyAccessDetailsWrapperHelper opolicyaccess = new HUMPolicyAccessDetailsWrapperHelper(oPolicyMem , true ,'PDP');
  	  }
    }

}