/****************************************************************************
Apex Class Name  : HUMTaskEditButtonOverrideExtensionTest
Version          : 1.0
Created On       : 9/13/2014
Function         : Test class to HUMTaskEditButtonOverrideExtension Apex class

Modification Log :
*    Developer                     CodeReview            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*    Suryakumari Medicherla          17930               09/13/2014             Original Version
SuryaKumari Medicherla                              09/18/2014             Code coverage for Romval of mutiple SQL queries for custom settings
Asha Deepika Koyyana            20572               01/28/2015             Fixed test class failure for Defect#164139    
Prachi Bhasin                   33486               18/07/2016             Changes made as part of REQ- 270349
Pradeepkumar Dani               99570               06/08/2017             Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
Pradeepkumar Dani              100996               06/13/2017             Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
Santhi Mandava                                      03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
Ananya Singh                                        30/09/2021             2561838 : Task Access and Custom Fields
**********************************************************************************************************************************/

@isTest
private class HUMTaskEditButtonOverrideExtensionTest
{
    /**
    * This method is used to insert the data only once and called automatically in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert oUser;
        System.assert(oUser.LanguageLocaleKey == 'en_US');
        system.runAs(oUser)
        {
            Task objTask = new Task();
            objTask.RecordtypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRM_RETAIL_TASK).getRecordTypeId();
            objTask.Subject = 'Test 1';
            objTask.Priority = 'Medium';
            objTask.Status = 'Not Started';
            objTask.CRMRetail_Automated__c = true;
            insert objTask;
            System.assertEquals(objTask.Subject, 'Test 1');
            
            CRMRetail_Edit_Delete_Task_Bypass__c cs = new CRMRetail_Edit_Delete_Task_Bypass__c();
            cs.Name= GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRM_RETAIL_TASK;
            insert cs;
            System.assertEquals(cs.Name, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRM_RETAIL_TASK);
            
            CRMFunctionality_ONOFF_Switch__c switch_2561838 = new CRMFunctionality_ONOFF_Switch__c(Name = '2561838',IsON__c=true);
            insert switch_2561838;
            System.assertEquals(switch_2561838.IsON__c, true);
        }
    }
    
    /**
    * This method is used to test when for recordtype as CRMRetail Task
    *
    * @param  None      
    * @return  Void
    */
    @isTest static void testbyPassValidationToEdit()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        System.assertNotEquals(oUser, null);
        system.runAs(oUser)
        {
			HUMTaskRecordTypes__c oTaskRecTypes = new HUMTaskRecordTypes__c();
            oTaskRecTypes.Name = 'RecordType';
            oTaskRecTypes.CSIRecordTypeName__c = 'CSI';
            oTaskRecTypes.ServiceCRMRecTypeName__c = 'Service CRM';
            insert oTaskRecTypes;
            System.assert(oTaskRecTypes.CSIRecordTypeName__c == 'CSI');
            
            List<Task> lstTsk = [Select RecordType.Name FROM Task WHERE Subject = 'Test 1' LIMIT 1];
            System.assertEquals(lstTsk.size(), 1);
            
            Test.startTest();
            ApexPages.currentPage().getParameters().put('id', lstTsk[0].Id);
            ApexPages.currentPage().getParameters().put(System.label.HUMretURL , lstTsk[0].Id ); 
            ApexPages.StandardController oStdController = new ApexPages.StandardController(lstTsk[0]);
            HUMTaskEditButtonOverrideExtension oExtension = new  HUMTaskEditButtonOverrideExtension(oStdController);
            oExtension.reDirectPage();
            System.assert(oExtension.reDirectPage() == null);
            Test.stopTest();
        }
    }
    
    /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void editTaskCCS() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assert(oUser.LanguageLocaleKey == 'en_US');
        
        system.runAs(oUser)
        {
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.BillingState == 'New York');
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);           
            
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
            
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);

            insert lstIntent;
            system.assertEquals(intentRec.Name == 'Filling', true);

            Case oCase = new Case(AccountId = oAccount.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, 
                             Interacting_With_Type__c = 'Member',Interacting_With__c= oAccount.Id, Due_Date__c=Date.Today(), 
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress');
            oCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId();
            insert oCase;
            System.assert(oCase.Interacting_With_Type__c == 'Member');
            
            HUMTaskRecordTypes__c oTaskRecTypes = new HUMTaskRecordTypes__c();
            oTaskRecTypes.Name = 'RecordType';
            oTaskRecTypes.CSIRecordTypeName__c = 'CSI';
            oTaskRecTypes.ServiceCRMRecTypeName__c = 'Service CRM';
            insert oTaskRecTypes;
            System.assert(oTaskRecTypes.CSIRecordTypeName__c == 'CSI');
            
            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Task_Owner__c = UserInfo.getUserName();
            oTask.Owner_Queue__c = UserInfo.getUserName();            
            oTask.status = 'In Progress';
            oTask.RecordTypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId();
            insert oTask;
            System.assert(oTask.status == 'In Progress');
            
            Task oTask2 = HUMTestDataHelperTest.getTaskRecord();
            oTask2.WhatId = oCase.Id;
            oTask2.Task_Owner__c = UserInfo.getUserName();
            oTask2.Owner_Queue__c = UserInfo.getUserName();            
            oTask2.status = 'In Progress';
            oTask2.RecordTypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('CSI').getRecordTypeId();
            insert oTask2;
            System.assert(oTask2.status == 'In Progress');
    
            Test.startTest();
            String sNextPage; 
             
            ApexPages.currentPage().getParameters().put(System.label.HUMretURL, oTask.id);
            ApexPages.currentPage().getParameters().put('id',oTask.Id ); 
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oTask);
            HUMTaskEditButtonOverrideExtension oExtension = new  HUMTaskEditButtonOverrideExtension(oStdController);
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getURL();
            System.assert(sNextPage != null);
            
            ApexPages.currentPage().getParameters().put('id', oTask2.id);
            ApexPages.currentPage().getParameters().put(System.label.HUMretURL , oTask2.Id ); 
            HUMTaskEditButtonOverrideExtension oExtension2 = new  HUMTaskEditButtonOverrideExtension(oStdController);
            oExtension2.reDirectPage();
            sNextPage = oExtension2.reDirectPage().getURL();
            System.assert(sNextPage != null);
            
            oUser.current_queue__c = 'queue1' ;
            update oUser;
            System.assert(oUser.current_queue__c == 'queue1');
           
            oExtension.reDirectPage(); 
            sNextPage = oExtension.reDirectPage().getURL();
            System.assert(sNextPage != null);  
            Test.stopTest();
        }
    }
   
   /**
    * editTaskBMI
    * <p>
    * Test method to create a task from BMI User profile(standard task functionality)
    * @param            NULL
    * @return           void
    */
    static testMethod void editTaskBMI() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','BME User');
        insert oUser;
        System.assert(oUser.LanguageLocaleKey == 'en_US');
        
        system.runAs(oUser)
        {
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.BillingState == 'New York');
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);           
            
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
            
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);

            insert lstIntent;
            system.assertEquals(intentRec.Name == 'Filling', true);

            Case oCase = new Case(AccountId = oAccount.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, 
                             Interacting_With_Type__c = 'Member',Interacting_With__c= oAccount.Id, Due_Date__c=Date.Today(), 
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress');
            oCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId();
            insert oCase;
            System.assert(oCase.Interacting_With_Type__c == 'Member');
            
            HUMTaskRecordTypes__c oTaskRecTypes = new HUMTaskRecordTypes__c ();
            oTaskRecTypes.Name = 'RecordType';
            oTaskRecTypes.CSIRecordTypeName__c = 'CSI';
            oTaskRecTypes.ServiceCRMRecTypeName__c = 'Service CRM';
            insert oTaskRecTypes;
            System.assert(oTaskRecTypes.CSIRecordTypeName__c == 'CSI');
            
            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Task_Owner__c = UserInfo.getUserName();
            oTask.Owner_Queue__c = UserInfo.getUserName();            
            oTask.status = 'In Progress';
            oTask.RecordTypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId();
            insert oTask;
            System.assert(oTask.status == 'In Progress');
            
            Task oTask2 = HUMTestDataHelperTest.getTaskRecord();
            oTask2.WhatId = oCase.Id;
            oTask2.Task_Owner__c = UserInfo.getUserName();
            oTask2.Owner_Queue__c = UserInfo.getUserName();            
            oTask2.status = 'In Progress';
            oTask2.RecordTypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('CSI').getRecordTypeId();
            insert oTask2;
            System.assert(oTask2.status == 'In Progress');
    
            Test.startTest();
            String sNextPage; 
             
            ApexPages.currentPage().getParameters().put(System.label.HUMretURL, oTask.id);
            ApexPages.currentPage().getParameters().put(System.label.HUMId,oTask.Id ); 
            ApexPages.StandardController stdController = new ApexPages.StandardController(oTask);
            HUMTaskEditButtonOverrideExtension oExtension = new  HUMTaskEditButtonOverrideExtension(stdController);
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getURL();
            System.assert(sNextPage != null);
            
            ApexPages.currentPage().getParameters().put(System.label.HUMId, oTask2.id);
            ApexPages.currentPage().getParameters().put(System.label.HUMretURL , oTask2.Id ); 
            HUMTaskEditButtonOverrideExtension oExtension2 = new  HUMTaskEditButtonOverrideExtension(stdController);
            oExtension2.reDirectPage();
            sNextPage = oExtension2.reDirectPage().getURL();
            System.assert(sNextPage != null);
            
            oUser.current_queue__c = 'queue1' ;
            update oUser;
            System.assert(oUser.current_queue__c == 'queue1');
            
            oExtension.reDirectPage();
            sNextPage = oExtension.reDirectPage().getURL();
            System.assert(sNextPage != null);  
            Test.stopTest(); 
        }
    }
}