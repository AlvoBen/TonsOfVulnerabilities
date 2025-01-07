/****************************************************************************
Apex Class Name  : HUMTaskNewButtonOverrideExtensionTest
Version          : 1.0
Created On       : JULY 15 2014
Function         : Test class to HUMTaskNewButtonOverrideExtension Apex class

Modification Log :
*  Developer Name                 Code Review               Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*  Suryakumari Medicherla          17093                   07/15/2014             Original Version
*  Suryakumari Medicherla          17930                   09/15/2014             Modified For recordtypes
   SuryaKumari Medicherla                                  09/18/2014             Removed mutiple SQL queries for custom settings
   Asha Deepika Koyyana            20572                   01/28/2015             Fixed test class failure for Defect#164139 
*  Harsha N                        33484                   20/07/2016             Changes made based on REQ-270351. 
*  Pradeepkumar Dani               99570                   06/08/2017             Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*  Pradeepkumar Dani              100996                   06/13/2017             Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*  Santhi Mandava                                          03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
**********************************************************************************************************************************/

@isTest
private class HUMTaskNewButtonOverrideExtensionTest
{
    /**
    * testsetup method to create test data
    *<p>
    * @param    NULL
    * @return   void
    */
    @testSetup static void setup() 
    {
        List <user> userList = new List <user>();
        User oUser1 = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        User oUser2 = HUMTestDataHelperTest.createTestUser('User2','BME User');
        User oUser3 = HUMTestDataHelperTest.createTestUser('User3','Customer Care Supervisor');
        userList.add(oUser1);
        userList.add(oUser2);
        userList.add(oUser3);
        insert userList;
        System.assertEquals('test User1',userList[0].LastName);        
        
        Account oAccount = HUMTestDataHelperTest.getAccount();
        oAccount.Name = 'New Case Account';
        oAccount.Group_Number__c  = '123';
        oAccount.BillingState = 'New York';
        oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        insert oAccount;
        System.assertEquals(oAccount.Name,'New Case Account');
        
        Product2 oProduct = HUMTestDataHelperTest.getProduct();
        oProduct.Name='Mediclaim';
        insert oProduct;
        system.assertNotEquals(oProduct,null,'Value not matched');
        
        Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
        oPolicy.Name = 'test123';
        oPolicy.Group_Name__c = oAccount.Id;
        oPolicy.Funding_Type_Code__c = 'Y';
        oPolicy.Product__c = oProduct.Id;
        oPolicy.Issue_State__c = 'IL'; 
        insert oPolicy;
        system.assertNotEquals(oPolicy,null,'Value not matched');
        
        Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
        oPolicyMem.Name = '12345';
        oPolicyMem.Member__c = oAccount.Id;
        oPolicyMem.Policy__c = oPolicy.Id;            
        oPolicyMem.Relationship__c = 'Self';
        insert oPolicyMem;
        system.assertNotEquals(oPolicyMem,null,'Value not matched');
                    
        Classification__c classificationRec = new Classification__c();
        classificationRec.Name = 'Claims';
        classificationRec.Is_Active__c = true;
        insert classificationRec;
        system.assertEquals('Claims',classificationRec.Name);
        
        Intent__c intentRec = new Intent__c();
        intentRec.Name = 'Filling';
        intentRec.Is_Active__c = true;
        insert intentRec;
        system.assertEquals('Filling',intentRec.Name);
        
        Case oCase = new Case(AccountId = oAccount.Id, Intent_Id__c = intentRec.Id, Classification_Id__c =classificationRec.Id, Policy_Member__c= oPolicyMem.Id ,
                         Interacting_With_Type__c = 'Member',Interacting_With__c= oAccount.Id, Due_Date__c=Date.Today(), 
                         Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress');
        oCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId();
        insert oCase;
        System.assert(oCase.Id != null);
        
        HUMTaskRecordTypes__c oTaskRecTypes = new HUMTaskRecordTypes__c ();
        oTaskRecTypes.Name = 'RecordType';
        oTaskRecTypes.CSIRecordTypeName__c = 'CSI';
        oTaskRecTypes.ServiceCRMRecTypeName__c = 'Service CRM';
        insert oTaskRecTypes;
        System.assert(oTaskRecTypes.id != null);
        
        Task oTask = HUMTestDataHelperTest.getTaskRecord();
        oTask.WhatId = oCase.Id;
        oTask.Task_Owner__c = UserInfo.getUserName();
        oTask.Owner_Queue__c = UserInfo.getUserName();            
        oTask.status = 'In Progress';
        oTask.RecordTypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId();
        insert oTask;
        System.assert(oTask.id != null);
        
        Task oTask2 = HUMTestDataHelperTest.getTaskRecord();
        oTask2.WhatId = oCase.Id;
        oTask2.Task_Owner__c = UserInfo.getUserName();
        oTask2.Owner_Queue__c = UserInfo.getUserName();            
        oTask2.status = 'In Progress';
        oTask2.RecordTypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('CSI').getRecordTypeId();
        insert oTask2;
        System.assert(oTask2.id != null);
    }
    
    /**
    * Test method to create task as Customer Care Specialist profile user
    * <p>
    * @return   Void
    * @param    N/A
    */
    static testMethod void createTaskfromCCSProfile() 
    {
        user oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        Account oAccount = [SELECT Id, Name, Group_Number__c, BillingState, recordTypeId FROM Account WHERE Name = 'New Case Account'];
        Product2 oProduct = [SELECT Id, Name FROM Product2 WHERE Name= 'Mediclaim'];
        Policy__c oPolicy = [SELECT Id, Name, Group_Name__c, Funding_Type_Code__c, Product__c, Issue_State__c FROM Policy__c WHERE Name= 'test123'];
        Policy_Member__c oPolicymem = [SELECT Id, Name, Member__c, Policy__c, Relationship__c FROM Policy_Member__c WHERE Name= '12345'];
        Case oCase = [SELECT Id, RecordTypeId, AccountId, Intent_Id__c, Classification_Id__c, Policy_Member__c, Interacting_With_Type__c, Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, Service_Center__c, Department__c, status FROM Case];
        HUMTaskRecordTypes__c oTaskRecTypes = [SELECT Id, Name, CSIRecordTypeName__c, ServiceCRMRecTypeName__c FROM HUMTaskRecordTypes__c];
        Task oTask = [SELECT Id, WhatId, Task_Owner__c, Owner_Queue__c, status, RecordTypeId FROM Task WHERE RecordTypeId =: Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId()];
        Task oTask2 = [SELECT Id, WhatId, Task_Owner__c, Owner_Queue__c, status, RecordTypeId FROM Task WHERE RecordTypeId =: Schema.sObjectType.Task.getRecordTypeInfosByName().get('CSI').getRecordTypeId()];
        
        system.runAs(oUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.HUMTaskNewButtonOverride);
            String sNextPage;
            ApexPages.currentPage().getParameters().put(System.label.HUMwho_id, oTask.id);
            ApexPages.currentPage().getParameters().put(System.label.HUMwhat_id, oCase.Id );
            ApexPages.currentPage().getParameters().put(System.label.HUMretURL , oTask.Id ); 
            ApexPages.StandardController stdController = new ApexPages.StandardController(oCase);
            HUMTaskNewButtonOverrideExtension oExtension = new  HUMTaskNewButtonOverrideExtension(stdController); 
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getUrl();
            System.assert(sNextPage != null);
            
            ApexPages.currentPage().getParameters().put(System.label.HUMwho_id, oTask2.id);
            ApexPages.currentPage().getParameters().put(System.label.HUMwhat_id, oCase.Id );
            ApexPages.currentPage().getParameters().put(System.label.HUMretURL , oTask2.Id ); 
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getUrl();
            System.assert(sNextPage != null);
            
            oUser.current_queue__c = 'queue1' ;
            update oUser;
            System.assert(oUser.current_queue__c != null);
            
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getUrl();
            System.assert(sNextPage != null);
            Test.stopTest();
        }
    }
    
    /**
    * Test method to create task as BMI profile user
    * <p>
    * @return   Void
    * @param    N/A
    */
    static testMethod void createTaskfromBMIProfile() 
    {
        user oUser = [SELECT Id FROM User  WHERE Lastname='test User2' LIMIT 1];
        Account oAccount = [SELECT Id, Name, Group_Number__c, BillingState, recordTypeId FROM Account WHERE Name = 'New Case Account'];
        Product2 oProduct = [SELECT Id, Name FROM Product2 WHERE Name= 'Mediclaim'];
        Policy__c oPolicy = [SELECT Id, Name, Group_Name__c, Funding_Type_Code__c, Product__c, Issue_State__c FROM Policy__c WHERE Name= 'test123'];
        Policy_Member__c oPolicymem = [SELECT Id, Name, Member__c, Policy__c, Relationship__c FROM Policy_Member__c WHERE Name= '12345'];
        Case oCase = [SELECT Id, RecordTypeId, AccountId, Intent_Id__c, Classification_Id__c, Policy_Member__c, Interacting_With_Type__c, Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, Service_Center__c, Department__c, status FROM Case];
        HUMTaskRecordTypes__c oTaskRecTypes = [SELECT Id, Name, CSIRecordTypeName__c, ServiceCRMRecTypeName__c FROM HUMTaskRecordTypes__c];
        Task oTask = [SELECT Id, WhatId, Task_Owner__c, Owner_Queue__c, status, RecordTypeId FROM Task WHERE RecordTypeId =: Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId()];
        Task oTask2 = [SELECT Id, WhatId, Task_Owner__c, Owner_Queue__c, status, RecordTypeId FROM Task WHERE RecordTypeId =: Schema.sObjectType.Task.getRecordTypeInfosByName().get('CSI').getRecordTypeId()];
                
        system.runAs(oUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.HUMTaskNewButtonOverride);
            String sNextPage;
            ApexPages.currentPage().getParameters().put(System.label.HUMwho_id, oTask.id);
            ApexPages.currentPage().getParameters().put(System.label.HUMwhat_id, oCase.Id );
            ApexPages.currentPage().getParameters().put(System.label.HUMretURL , oTask.Id ); 
            ApexPages.StandardController stdController = new ApexPages.StandardController(oCase);
            HUMTaskNewButtonOverrideExtension oExtension = new  HUMTaskNewButtonOverrideExtension(stdController); 
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getUrl();
            System.assert(sNextPage != null);
            
            ApexPages.currentPage().getParameters().put(System.label.HUMwho_id, oTask2.id);
            ApexPages.currentPage().getParameters().put(System.label.HUMwhat_id, oCase.Id );
            ApexPages.currentPage().getParameters().put(System.label.HUMretURL , oTask2.Id ); 
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getUrl();
            System.assert(sNextPage != null);
            
            oUser.current_queue__c = 'queue1' ;
            update oUser;
            System.assert(oUser.current_queue__c != null);
            
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getUrl();
            System.assert(sNextPage != null);
            Test.stopTest();
        }
    }
    
    /**
    * Test method to verify the request with invalid What Id
    * <p>
    * @return   Void
    * @param    N/A
    */
    static testMethod void invalidWhatId() 
    {
        user oUser = [SELECT Id FROM User  WHERE Lastname='test User3' LIMIT 1];
        Account oAccount = [SELECT Id, Name, Group_Number__c, BillingState, recordTypeId FROM Account WHERE Name = 'New Case Account'];
        Product2 oProduct = [SELECT Id, Name FROM Product2 WHERE Name= 'Mediclaim'];
        Policy__c oPolicy = [SELECT Id, Name, Group_Name__c, Funding_Type_Code__c, Product__c, Issue_State__c FROM Policy__c WHERE Name= 'test123'];
        Policy_Member__c oPolicymem = [SELECT Id, Name, Member__c, Policy__c, Relationship__c FROM Policy_Member__c WHERE Name= '12345'];
        Case oCase = [SELECT Id, RecordTypeId, AccountId, Intent_Id__c, Classification_Id__c, Policy_Member__c, Interacting_With_Type__c, Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, Service_Center__c, Department__c, status FROM Case];
        HUMTaskRecordTypes__c oTaskRecTypes = [SELECT Id, Name, CSIRecordTypeName__c, ServiceCRMRecTypeName__c FROM HUMTaskRecordTypes__c];
        Task oTask = [SELECT Id, WhatId, Task_Owner__c, Owner_Queue__c, status, RecordTypeId FROM Task WHERE RecordTypeId =: Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId()];
        Task oTask2 = [SELECT Id, WhatId, Task_Owner__c, Owner_Queue__c, status, RecordTypeId FROM Task WHERE RecordTypeId =: Schema.sObjectType.Task.getRecordTypeInfosByName().get('CSI').getRecordTypeId()];
                
        system.runAs(oUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.HUMTaskNewButtonOverride);
            String sNextPage;
            ApexPages.currentPage().getParameters().put(System.label.HUMwho_id, oTask.id);
            ApexPages.currentPage().getParameters().put(System.label.HUMwhat_id, oTask.id );
            ApexPages.currentPage().getParameters().put(System.label.HUMretURL , oTask.Id ); 
            ApexPages.StandardController stdController = new ApexPages.StandardController(oCase);
            HUMTaskNewButtonOverrideExtension oExtension = new  HUMTaskNewButtonOverrideExtension(stdController); 
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getUrl();
            System.assert(sNextPage != null);
            
            ApexPages.currentPage().getParameters().put(System.label.HUMwho_id, oTask.id);
            ApexPages.currentPage().getParameters().put(System.label.HUMwhat_id, oTask.id );
            ApexPages.currentPage().getParameters().put(System.label.HUMretURL , oTask2.Id ); 
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getUrl();
            System.assert(sNextPage != null);
            
            oUser.current_queue__c = 'queue1' ;
            update oUser;
            System.assert(oUser.current_queue__c != null);
            
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getUrl();
            System.assert(sNextPage != null);
            Test.stopTest();
        }
    }
}