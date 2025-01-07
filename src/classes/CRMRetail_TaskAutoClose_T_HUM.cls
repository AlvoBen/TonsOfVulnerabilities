/******************************************************************************************************************************
Apex Class Name  : CRMRetail_TaskAutoClose_T_HUM
Version          : 1.0 
Created Date     : 03/21/2023
Function         : To cover the CRMRetail_TaskAutoClose_B_HUM class 
Test Class For   : CRMRetail_TaskAutoClose_B_HUM
Modification Log :
* Developer              Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Mohamed Thameem      	                	    03/21/2023           US - 4407867 Storefront: Auto-close tasks for deceased member
* Mohamed Thameem      	                	    05/12/2023           US - 4594514 Storefront: Auto-close tasks for members with missing contact info
*********************************************************************************************************************************/

@isTest
private class CRMRetail_TaskAutoClose_T_HUM {
    
    @testSetup
    static void setup () 
    {
        Id memebr =  SObjectType.Account.getRecordTypeInfosByDeveloperName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER).getRecordTypeId();
        Id crmRetailTask = SObjectType.Task.getRecordTypeInfosByDeveloperName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKRECORDTYPENAME).getRecordTypeId();
        List<user> userList = new List<user>();
        
        User CRMUser = HUMTestDataHelperTest.createTestUser('User1','CRMRetail User');
        userList.add(CRMUser);

        User readonly = HUMTestDataHelperTest.createTestUser('Read','Read Only');
        userList.add(readonly);
        
        User DepUser = HUMTestDataHelperTest.createTestUser('Deployment','Deployment');
        userList.add(DepUser);
        
        insert userList;
        
        system.assertEquals(userList[2].LastName,'test Deployment');
        system.assertEquals(userList[1].LastName,'test Read');
        system.assertEquals(userList[0].LastName,'test User1');

        PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name = :GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN];
        PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = userList[0].id, PermissionSetId = SuperAdmin.Id);
        insert supAdmin;
        
        system.assertEquals(supAdmin.AssigneeId, CRMUser.id);
        
        System.runAs(CRMUser)
        {
            List<CRMFunctionality_ONOFF_Switch__c> switchList =  new List<CRMFunctionality_ONOFF_Switch__c>(); 
            switchList.add(new CRMFunctionality_ONOFF_Switch__c(Name = '4407867',IsON__c=true,Release_Name__c='5/12/2023',Scrum_Team__c='OAKS',Story_Title__c='US - 4407867 Storefront: Auto-close tasks for deceased member'));
            switchList.add(new CRMFunctionality_ONOFF_Switch__c(Name = '4594514',IsON__c=true,Release_Name__c='6/23/2023',Scrum_Team__c='OAKS',Story_Title__c='US - 4594514 Storefront: Auto-close tasks for members with missing contact info'));
            insert switchList;
            system.assertEquals(switchList[0].IsON__c, true);
            system.assertEquals(switchList[1].IsON__c, true);
            
            List<Account> accList = new List<Account>();
            date dob = System.today()-1000;
            Account acc = CRMRetail_TestDataFactory_H_HUM.createAccount(null,memebr,dob,'02/17/1998', GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DECEASED, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER,'','',false,'','70005','787892577','',System.today(),'',''); 
            acc.Deceased_Date__c = '10/16/2022';
            accList.add(acc);
            
            Account accWithNoContact = CRMRetail_TestDataFactory_H_HUM.createAccount(null,memebr,dob,'02/17/1998', GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DECEASED, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER,'','',false,'','70005',null,'',System.today(),'',''); 
            accWithNoContact.PersonOtherPhone = null;
            accList.add(accWithNoContact);
            
            update accList;
            system.assertEquals(acc.Deceased_Date__c, '10/16/2022');
            system.assertEquals(accWithNoContact.PersonHomePhone, null);
            
            List<task> taskList = new List<task>();
            
            Task tsk = new Task();
            tsk.WhatId = acc.id;
            tsk.subject = 'Deceased Task 1';
            tsk.ActivityDate = System.today()+2;
            tsk.Status = 'Not Started';
            tsk.Priority = 'High';
            tsk.RecordTypeId = crmRetailTask;
            taskList.add(tsk);
            
            Task tsk1 = new Task();
            tsk1.WhatId = acc.id;
            tsk1.subject = 'Deceased Task 2';
            tsk1.ActivityDate = System.today()+2;
            tsk1.Status = 'Not Started';
            tsk1.Priority = 'High';
            tsk1.Completed_Reason__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_UNABLE_TO_CONTACT;
            tsk1.RecordTypeId = crmRetailTask;
            tsk1.CRMRetail_Automated__c  = true;
            taskList.add(tsk1);
            
            Task tskNoPhone = new Task();
            tskNoPhone.WhatId = accWithNoContact.id;
            tskNoPhone.subject = 'No Phone Number Listed Task';
            tskNoPhone.ActivityDate = System.today()+2;
            tskNoPhone.Status = 'Not Started';
            tskNoPhone.Priority = 'High';
            tskNoPhone.RecordTypeId = crmRetailTask;
            taskList.add(tskNoPhone);
            
            insert tasklist;
            system.assertEquals(tasklist[0].Status, 'Not Started');
            system.assertEquals(tasklist[1].Completed_Reason__c, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_UNABLE_TO_CONTACT);
            system.assertEquals(tasklist[2].Status, 'Not Started');
        }
    }
    
    @isTest
    static void testAutoClose()
    {    
        User depUser = [select id,profile.name from user where name = 'test Deployment' limit 1];
        system.assertEquals('Deployment', depUser.profile.name);
        
        System.runAs(depUser)
        {
            Test.startTest();
            CRMRetail_TaskAutoClose_B_HUM batch= new CRMRetail_TaskAutoClose_B_HUM();
            Id jobid = Database.executeBatch(batch);
            Test.stopTest();
            
            Task tsk = [SELECT Id, WhoId, WhatId, RecordTypeId, Subject,Completed_reason__c, ActivityDate, Status, Priority, Description FROM Task where subject='Deceased Task 1'  limit 1];
            Task noPhonetsk = [SELECT Id, WhoId, WhatId, RecordTypeId, Subject,Completed_reason__c, ActivityDate, Status, Priority, Description FROM Task where subject='No Phone Number Listed Task'  limit 1];

            System.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_COMPLETED_STATUS, tsk.Status);
            System.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DECEASED, tsk.Description);
            System.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_UNABLE_TO_CONTACT, tsk.Completed_reason__c); 
            
            System.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_COMPLETED_STATUS, noPhonetsk.Status);
            System.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_PHONENOT_LISTED, noPhonetsk.Description);
            System.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_UNABLE_TO_CONTACT, noPhonetsk.Completed_reason__c);
        }                                                                             
    }
    
    @isTest
    static void testSchedule()
    {    
        User depUser = [select id,profile.name from user where name = 'test Deployment' limit 1];
        system.assertEquals('Deployment', depUser.profile.name);
        
        System.runAs(depUser)
        {
            Test.startTest();
            System.schedule('Storefront Batch', '0 0 23 * * ?', new CRMRetail_TaskAutoClose_B_HUM());
            list<AsyncApexJob> batchScheduleid = [SELECT Id,ApexClass.Name FROM AsyncApexJob where ApexClass.Name = 'CRMRetail_TaskAutoClose_B_HUM'];
            
            System.assertEquals('CRMRetail_TaskAutoClose_B_HUM', batchScheduleid[0].ApexClass.Name);
            Test.stopTest();
        }                                                                             
    }
    
    @isTest
    static void testExceptionBlock()
    {    
        User CRMUser = [select id,profile.name from user where name = 'test User1' limit 1];
        system.assertEquals('CRMRetail User', CRMUser.profile.name);
        
        System.runAs(CRMUser)
        {
            Test.startTest();        
            Database.BatchableContext BC;
            CRMRetail_TaskAutoClose_B_HUM objBatch = new CRMRetail_TaskAutoClose_B_HUM();
            objBatch.execute(BC, null); 
            Test.stopTest();
            
            List<Error_Log__c> lstErrorLog = [Select Id,Error_Message__c from Error_Log__c];
            System.assert(lstErrorLog[0].Error_Message__c.contains('Attempt to de-reference a null object')); 
        }
    }
    
    @isTest
    static void testUserWithNoAccess()
    {    
        User readonly = [select id,profile.name from user where name = 'test Read' limit 1];
        system.assertEquals('Read Only', readonly.profile.name);

        System.runAs(readonly)
        {
            Test.startTest();        
            CRMRetail_TaskAutoClose_B_HUM batch= new CRMRetail_TaskAutoClose_B_HUM();
            Id jobid = Database.executeBatch(batch);
            Test.stopTest();
            
            List<Error_Log__c> lstErrorLog = [Select Id,Error_Message__c,Object_Type__c,Method_Name__c from Error_Log__c];
            System.assert(lstErrorLog[0].Error_Message__c.contains('User doesn\'t have required access to UPDATE')); 
        }
    }
}