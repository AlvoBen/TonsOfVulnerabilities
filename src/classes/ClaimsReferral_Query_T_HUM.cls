/**************************************************************************** 
Apex Class Name  : ClaimsReferral_Query_T_HUM 
Version          : 1.0  
Created Date     : March 03, 2016
Function         : Test class for ClaimsReferral_Query_D_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat                               03/03/2016                  Original Version
***************************************************************************************************/
@isTest
private Class ClaimsReferral_Query_T_HUM
{
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
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
        }
    }
    
     /*
     * Method Name : runData
     * Description : Used to run scenerios of test data
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runPositiveData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            
            
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Name, Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
             
            
            Test.setCurrentPage(Page.ClaimSummary_VF_HUM_V2);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
            
            ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
            
            ClaimsReferral_Query_D_HUM oClaimsSummary = New ClaimsReferral_Query_D_HUM();
            Policy_Member__c oPol = oClaimsSummary.getPolicyMemberDetails(oPolMem.Id);
            Account oAcc = oClaimsSummary.getAccountDetails(oPolMem.Id);
            System.assertEquals(oAcc.Enterprise_ID__c,'1005577525');
            System.assertEquals(oPol.Name,'testpolmem11');
            Test.stopTest();  
        }
    }
    /*
     * Method Name : claimSummaryException
     * Description : This method tests the Exception handling 
     * Return type : void
     * Parameter   : None
     */ 
    Static testMethod void claimSummaryException() 
    {
        Test.setCurrentPage(Page.ClaimSummary_VF_HUM_V2);
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        {
            Test.startTest();
            
            Account oAccount = [Select Id, Name, Tenant_Id__c, RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Test.setCurrentPage(Page.ClaimSummary_VF_HUM_V2);
            ApexPages.currentPage().getParameters().put('Id','');
            
            ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
            
            ClaimsReferral_Query_D_HUM oClaimsSummary = new ClaimsReferral_Query_D_HUM();
            Policy_Member__c oPol = oClaimsSummary.getPolicyMemberDetails('');
            Account oAcc = oClaimsSummary.getAccountDetails('');
            System.assertEquals(oAcc, Null);
            System.assertEquals(oPol, Null);
            Test.stopTest(); 
        }
    }
}