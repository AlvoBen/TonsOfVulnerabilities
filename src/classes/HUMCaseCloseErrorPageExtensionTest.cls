/*****************************************************************************************
Apex class Name    : HUMCaseCloseErrorPageExtensionTest
Version            : 1.0
Function           : This is test class for HUMCaseCloseErrorPageExtension
Created Date       : 03/31/2015 

Modification Log:
*   Developer                           Code Review             Date                   Description
* ------------------------------------------------------------------------------------------------------------------------------ 
*  SuryaKumari Medicherla               22085                  03/31/2015              Original Version 
*  SuryaKumari Medicherla               25100                  07/29/2015              Code Modified to fix CA Ticket: 5249265(Customer is not able to close an open case)
*  Asha Deepika Koyyana                 25100                  08/27/2015              Fixed code review comments.       
*  Avinash Choubey                      29247                  01/25/2016              Modified at line 84 and 89 
*  Avinash Choubey                      33344                  07/11/2016              Changes made as part of REQ- 255151
*  Subhamay Mandal                                             05/17/2017              Update Test class. REQ - 313447
*  Pradeepkumar Dani                    99570                  06/08/2017              Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*  Pradeepkumar Dani                    100996                 06/13/2017              Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*  Pradeepkumar Dani                    102268                 06/16/2017              Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
*  Pallavi Ravishankar                  122650                 08/16/2017              Defect Fix 311425.0001 - case closes without entering medicare comments
*  Atia Uzma                                                   03/10/2021              Build Fix
*  Mohammed Noor                                               08/25/2021              Updtate Test Class for US2060135.
* Prasuna Pattabhi                           07/31/2023                 Test Class error fix - build fix
********************************************************************************************************************************/ 
@isTest
private class HUMCaseCloseErrorPageExtensionTest
{
     
    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname=='test User1',true);
        PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM LIMIT 1];
        insert new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = pCheck.id);
        
        System.runAs(oUser) 
        { 
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assertEquals(oAccount.Name, 'New Case Account', 'record inserted sucessfuly');
            System.assertEquals(oAccount.Group_Number__c, '123', 'record inserted sucessfuly');
            System.assertEquals(oAccount.BillingState, 'New York', 'record inserted sucessfuly');
            
            List<Classification_Type__c> ctList= new list<Classification_Type__c>();
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'calls';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);
            insert ctList;
            system.assertEquals(ctList.size(),1);
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Claims';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Claims',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'Filling',true);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRecDec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            insert ctciList;
            system.assertEquals(ctciList.size(),1);
            
            Case oCase = new Case(
                AccountId=oAccount.Id, 
                Intent_ID__c = intentRec.id, 
                General_Case__c = true, 
                Classification_ID__c = classificationRecDec.id, 
                Interacting_With_Type__c = 'Group',
                Interacting_With__c = oAccount.Id, 
                Due_Date__c = Date.Today(), 
                Case_Owner__c = UserInfo.getUserName(), 
                Owner_Queue__c = 'Cincinnati CRU1', 
                Work_Queue_View_Name__c = 'Cincinati CRU1', 
                Service_Center__c = 'Cincinnati', 
                Department__c = 'CRU1', 
                status = 'In Progress',
                Classification_Type__c = 'Calls',
                Call_Benefit_Category__c = 'Medicare Part C',
                Autoroute_Status__c = 'Evaluate',
                Interacting_About_Type__c = 'Group'               
                );
            insert oCase;
            system.assertNotEquals(oCase.Id, null, 'Value matched');             
            system.assertEquals(oCase.General_Case__c, True, 'record inserted sucessfuly');
            system.assertEquals(oCase.Interacting_With_Type__c, 'Group', 'record inserted sucessfuly'); 
            system.assertEquals(oCase.Due_Date__c, Date.Today(), 'record inserted sucessfuly');
            system.assertEquals(oCase.Case_Owner__c, UserInfo.getUserName(), 'record inserted sucessfuly');
            system.assertEquals(oCase.Owner_Queue__c, 'Cincinnati CRU1', 'record inserted sucessfuly');
            system.assertEquals(oCase.Work_Queue_View_Name__c, 'Cincinati CRU1', 'record inserted sucessfuly');
            system.assertEquals(oCase.Service_Center__c, 'Cincinnati', 'record inserted sucessfuly');
            system.assertEquals(oCase.Department__c, 'CRU1', 'record inserted sucessfuly');            
            system.assertEquals(oCase.status, 'In Progress', 'record inserted sucessfuly');
        
            Case oCase1 = new Case(
                AccountId=oAccount.Id, 
                Intent_ID__c = intentRec.id, 
                General_Case__c = true, 
                Classification_ID__c = classificationRecDec.id, 
                Interacting_With_Type__c = 'Member',
                Interacting_With__c = oAccount.Id, 
                Due_Date__c = Date.Today(), 
                Case_Owner__c = UserInfo.getUserName(), 
                Owner_Queue__c = 'Cincinnati CRU1', 
                Work_Queue_View_Name__c = 'Cincinati CRU1', 
                Service_Center__c = 'Cincinnati', 
                Department__c = 'CRU1', 
                status = 'In Progress',
                Classification_Type__c = 'Calls',
                Call_Benefit_Category__c = 'Medicare Part C',                
                Complaint__c = 'Yes - Medicare Part C',
                Complaint_Reason__c = 'Provider Quality of Care',
                Complaint_Type__c = 'Medical',
                G_A_Rights_Given__c = 'No',
                Interacting_About_Type__c = 'Member'
                );
            insert oCase1;
            
       // Inserting test setup data  Medicare Calls Case Comments
        Account accountRec = HUMTestDataHelperTest.getAccount();
        accountRec.OwnerId = oUser.Id;
        accountRec.FirstName = 'Test';
        accountRec.LastName = 'Account2';
        accountRec.Enterprise_Id__c = '123456';
        accountRec.recordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert accountRec;
        system.assertEquals(accountRec.Enterprise_Id__c=='123456',true);
        
        Product2 prod4 = new Product2();
        prod4.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MAPD;
        prod4.ProductCode = GLOBAL_CONSTANT_HUM.MEDICARE_POLICY;
        prod4.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCR;
        prod4.Name = 'Prod4';
        insert prod4;
        system.assertEquals(prod4.Name=='Prod4',true);    
        
        Policy__c policyRecMed = new Policy__c();
        policyRecMed.Group_Name__c = accountRec.Id;
        policyRecMed.Product__c = prod4.id;
        policyRecMed.Major_Lob__c = GLOBAL_CONSTANT_HUM.MEDICARECALLS_PDP_HUM;
        policyRecMed.Policy_Group_Number__c='11111';
        insert policyRecMed;
        system.assertEquals(policyRecMed.Product__c==prod4.id,true);
        
        Policy_Member__c policyMemRecMed = new Policy_Member__c();
        policyMemRecMed.Name = 'MedCallsCC';
        policyMemRecMed.Policy__c = policyRecMed.Id;
        policyMemRecMed.Member__c = accountRec.Id;
        insert policyMemRecMed;
        system.assertEquals(policyMemRecMed.Member__c==accountRec.Id,true);
        

                
        Case caseRecordMed = new Case();
        caseRecordMed.Status = 'In Progress';
        caseRecordMed.Case_Owner__c = UserInfo.getUserName();
        caseRecordMed.Owner_Queue__c = 'Cincinnati CRU1';
        caseRecordMed.AccountId = accountRec.id;
        caseRecordMed.Service_Center__c = 'Cincinnati';
        caseRecordMed.Department__c = 'CRU1';
        caseRecordMed.Work_Queue_View_Name__c = 'Cincinati CRU1';       
        caseRecordMed.Call_Benefit_Category__c = 'Medicare Part C';
        caseRecordMed.ctci_list__c=ctciList[0].id;
        caseRecordMed.Classification_Type__c = 'calls';
        caseRecordMed.Classification_id__c=classificationRecDec.id;
        caseRecordMed.Intent_id__c = intentRec.id;
        caseRecordMed.Interacting_With__c = accountRec.id;
        caseRecordMed.Interacting_With_Type__c = 'Member';
        caseRecordMed.Policy_Member__c = policyMemRecMed.Id;
        caseRecordMed.Autoroute_Status__c = 'Evaluate';
        caseRecordMed.Origin = 'Inbound Call';
        caseRecordMed.Line_of_Business__c = 'Medicare';
        caseRecordMed.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
        caseRecordMed.Due_Date__c = Date.Today();
        caseRecordMed.General_Case__c = true;   
        caseRecordMed.Interacting_About_Type__c = 'Member';     
        insert caseRecordMed;
        system.assertEquals(caseRecordMed.Owner_Queue__c=='Cincinnati CRU1',true);
        
        list<CaseCommentType__c> lstCCT = new list<CaseCommentType__c>();
        CaseCommentType__c oCCTIssue = new CaseCommentType__c(Name = GLOBAL_CONSTANT_HUM.MEDICARECALLS_ISSUE_HUM);
        lstCCT.add(oCCTIssue);
        
        CaseCommentType__c oCCTReso = new CaseCommentType__c(Name = GLOBAL_CONSTANT_HUM.MEDICARECALLS_RESO_HUM);
        lstCCT.add(oCCTReso);
        
        insert lstCCT; 

        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c();
        objCS.Name = '2060135';
        objCS.IsON__c=true;
        objCS.Release_Name__c='09/24/2021';
        objCS.Scrum_Team__c='CRM Transformers';
        objCS.Story_Title__c='T1PRJ0002606 / PR00094268 - OGO CDAG CRM Cases Not Routed to OGO or Routed away from OGO Queue';
        insert objCS;
        
        }
    }
    
    /**
    * This method is used for test positive cases of HUMTaskEditPageExtension Class. 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void overAllTestMethod()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.assertNotEquals(oUser, null, '​Value not matched'); 
        
        System.runAs(oUser) 
        { 
            Test.startTest();
              
            Case oCase = [Select id,CaseNumber, Status, CTCI_List__c,RecordTypeId, Policy_Member__c,Policy_Member__r.Policy__c, Classification_Id__c, Intent_Id__c, Complaint__c, G_A_Rights_Given__c, Origin from Case WHERE Policy_Member__c = null AND Complaint__c != 'Yes - Medicare Part C' limit 1];
            system.assertNotEquals(oCase.id, null, 'Value not matched'); 
            
            PageReference pageRef = Page.HUMCaseCloseErrorPage;
            Test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('id', oCase.id);
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            HUMCaseCloseErrorPageExtension oCaseCloseError = new HUMCaseCloseErrorPageExtension(oStdController);
            
            pagereference oPageRef1 = oCaseCloseError.CaseClose();
            pagereference oPageRefTest =  new PageReference('/apex/' + HUMConstants.HUM_CASEEDIT_EDITPAGENAME + '?Id=' + oCase.id + HUMConstants.HUM_CASEEDIT_CLOSEDSTATUS + HUMConstants.HUMCaseCloseReturnURL + oCase.id + HUMConstants.HUMEnt + HUMConstants.HUMNoOverrideEqualsOne);
            System.debug('oPageRef1: '+oPageRef1);
            System.debug('oPageRef1.geturl(): '+oPageRef1.geturl());
            System.debug('oPageRefTest.getUrl(): '+oPageRefTest.getUrl());
            System.assertEquals(oPageRef1.geturl(), oPageRefTest.getUrl(), ' Value matched');           
           
            oPageRef1 = oCaseCloseError.CheckForOpenTaskCountCount();
            pagereference oPageRefTest1 =  new PageReference('/apex/' + HUMConstants.HUM_CASEEDIT_EDITPAGENAME + '?Id=' + oCase.id + HUMConstants.HUM_CASEEDIT_CLOSEDSTATUS + HUMConstants.HUMCaseCloseReturnURL + oCase.id + HUMConstants.HUMEnt + HUMConstants.HUMNoOverrideEqualsOne);           

            System.assertEquals(oPageRef1.geturl(), oPageRefTest1.getUrl(), ' Value matched'); 
            
            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Type = 'Work Task';
            oTask.Task_Owner__c = 'User1';
            oTask.Owner_Queue__c ='Cincinnati CRU1';            
            oTask.status = 'In Progress';
            oTask.QueueOrUserId__c = oUser.Id;
            insert oTask;
            System.assertNotEquals(oTask.Id, null, 'Value matched');             
            System.assertEquals(oTask.status, 'In Progress', 'record inserted succesfuly');
            System.assertEquals(oTask.Type, 'Work Task', 'record inserted succesfuly');
            System.assertEquals(oTask.Task_Owner__c, 'User1', 'record inserted succesfuly');
            System.assertEquals(oTask.Owner_Queue__c, 'Cincinnati CRU1', 'record inserted succesfuly');
                        
            Task oTask2 = HUMTestDataHelperTest.getTaskRecord();
            oTask2.WhatId = oCase.Id;
            oTask2.Type = 'Work Task';
            oTask2.Task_Owner__c = 'User1';
            oTask2.Owner_Queue__c ='Cincinnati CRU1';            
            oTask2.status = 'Closed';
            oTask2.QueueOrUserId__c = oUser.Id;
            insert oTask2;
            System.assertNotEquals(oTask2.Id, null, 'Value matched');             
            System.assertEquals(oTask2.status, 'Closed', 'record inserted succesfuly');
            System.assertEquals(oTask2.Type, 'Work Task', 'record inserted succesfuly');
            System.assertEquals(oTask2.Task_Owner__c, 'User1', 'record inserted succesfuly');
            System.assertEquals(oTask2.Owner_Queue__c, 'Cincinnati CRU1', 'record inserted succesfuly');
            
            list<CaseCommentType__c> lstCCT = new list<CaseCommentType__c>();
            CaseCommentType__c oCCTIssue = new CaseCommentType__c(Name = GLOBAL_CONSTANT_HUM.MEDICARECALLS_ISSUE_HUM);
            lstCCT.add(oCCTIssue);
            
            CaseCommentType__c oCCTReso = new CaseCommentType__c(Name = GLOBAL_CONSTANT_HUM.MEDICARECALLS_RESO_HUM);
            lstCCT.add(oCCTReso);
        
            insert lstCCT;
            CaseComment ocasecomm = new CaseComment();
            ocasecomm.ParentId = oCase.id;
            ocasecomm.CommentBody = 'test';
            insert ocasecomm;
            
            CaseCommentType_Junction__c ocasecommtype = new CaseCommentType_Junction__c();
            ocasecommtype.CaseComment__c= ocasecomm.Id;
            insert ocasecommtype;
            list<CaseCommentType_Junction__c> lstCCTJunc = [SELECT Id, Name,CaseCommentType__r.Description__c,CaseCommentType__r.Name, CaseComment__c FROM CaseCommentType_Junction__c];
            system.assertNotEquals(lstCCTJunc, null);
            oCaseCloseError.sMedCallsIssueCC ='test';
            oCaseCloseError.sMedCallsResCC = 'test';
            
            
            List<Task> lstTasks = oCaseCloseError.getLstTasks();
            System.assert(lstTasks.size() > 0);
            pagereference  oPageRef2 = oCaseCloseError.CaseClose();
            System.assertEquals(oPageRef2, null, 'Should return null');
            oCaseCloseError.returnToCase();
            oPageRef1 = oCaseCloseError.CheckForOpenTaskCountCount();
            System.assertEquals(oPageRef1, null, 'Should return null');
            oCaseCloseError.getCCMedicareCalls();
            Test.stopTest();
        }
    }
    
    
    /**
    * This method is used for test positive cases of HUMTaskEditPageExtension Class. 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void overAllTestMethodWithoutTask()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.assertNotEquals(oUser, null, '​Value not matched'); 
        
        System.runAs(oUser) 
        { 
            Test.startTest();
              
            Case oCase = [Select id,CaseNumber,Policy_Member__c, Status, CTCI_List__c, Classification_Id__c, Policy_Member__r.Policy__c, Intent_Id__c, RecordTypeId, Complaint__c, G_A_Rights_Given__c, Origin from Case where Policy_Member__c = null AND Complaint__c != 'Yes - Medicare Part C' limit 1];
            oCase.Complaint__c = 'No';        
            oCase.G_A_Rights_Given__c = 'No';
            update oCase;
            system.assertNotEquals(oCase.id, null, 'Value not matched'); 
            
            PageReference pageRef = Page.HUMCaseCloseErrorPage;
            Test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('id', oCase.id);
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            HUMCaseCloseErrorPageExtension oCaseCloseError = new HUMCaseCloseErrorPageExtension(oStdController);
            
            pagereference oPageRef1 = oCaseCloseError.CaseClose();
            pagereference oPageRefTest =  new PageReference('/apex/' + HUMConstants.HUM_CASEEDIT_EDITPAGENAME + '?Id=' + oCase.id + HUMConstants.HUM_CASEEDIT_CLOSEDSTATUS + HUMConstants.HUMCaseCloseReturnURL + oCase.id + HUMConstants.HUMEnt + HUMConstants.HUMNoOverrideEqualsOne);
            System.assertNotEquals(oPageRef1, null, ' Page Reference is Null');         
           
            oPageRef1 = oCaseCloseError.CheckForOpenTaskCountCount();
            pagereference oPageRefTest1 =  new PageReference('/apex/' + HUMConstants.HUM_CASEEDIT_EDITPAGENAME + '?Id=' + oCase.id + HUMConstants.HUM_CASEEDIT_CLOSEDSTATUS + HUMConstants.HUMCaseCloseReturnURL + oCase.id + HUMConstants.HUMEnt + HUMConstants.HUMNoOverrideEqualsOne);           

            System.assertEquals(oPageRef1.geturl(), oPageRefTest1.geturl(), ' Values matched'); 
            
            List<Task> lstTasks = oCaseCloseError.getLstTasks();
            System.assert(lstTasks.isEmpty());
            pagereference  oPageRef2 = oCaseCloseError.CaseClose();
            System.assertNotEquals(oPageRef2, null, 'Should  return null'); 
            oCaseCloseError.returnToCase();
            oPageRef1 = oCaseCloseError.CheckForOpenTaskCountCount();
            System.assertNotEquals(oPageRef1, null, 'Should  not return null');
            
            Test.stopTest();
        }
    }
    
    /**
    * This method is used for testcase close scenarios for medicare cases. 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testMedicareScenarios()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.assertNotEquals(oUser, null, '​Value not matched'); 
        
        System.runAs(oUser) 
        { 
            Test.startTest();
              
            Case oCase = [Select id,CaseNumber, Status, CTCI_List__c,RecordTypeId, Policy_Member__c,Policy_Member__r.Policy__c, Classification_Id__c, Intent_Id__c, Complaint__c, G_A_Rights_Given__c, Origin from Case  where Policy_Member__r.Name = 'MedCallsCC' LIMIT 1];
            system.assertNotEquals(oCase.id, null, 'Value not matched'); 
            
            PageReference pageRef = Page.HUMCaseCloseErrorPage;
            Test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('id', oCase.id);
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            HUMCaseCloseErrorPageExtension oCaseCloseError = new HUMCaseCloseErrorPageExtension(oStdController);
            
            pagereference oPageRef1 = oCaseCloseError.CaseClose();      
            oCaseCloseError.sMedCallsIssueCC = 'testing medicare issue';
            oCaseCloseError.sMedCallsResCC = 'testing medicare resolution';
            List<Error_Log__c> listErrorLogs  = oCaseCloseError.listErrorLogs;
            
            pagereference  oPageRef2 = oCaseCloseError.CaseClose();
            oCaseCloseError.returnToCase();
            
            
            Test.stopTest();
        }
    }

    /**
    * This method is used for testcase close scenarios for OGO cases. 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testOGOScenarios()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.assertNotEquals(oUser, null, '​Value not matched'); 
        
        System.runAs(oUser) 
        { 
            Test.startTest();
              
            Case oCase = [Select id,CaseNumber, Status, CTCI_List__c,RecordTypeId, Policy_Member__c,Policy_Member__r.Policy__c, Classification_Id__c, Intent_Id__c, Complaint__c, G_A_Rights_Given__c, Origin, Interacting_with_type__c,autoroute_status__c from Case  where Policy_Member__c = null AND Complaint__c = 'Yes - Medicare Part C' LIMIT 1];
            system.assertNotEquals(oCase.id, null, 'Value not matched'); 

            PageReference pageRef = Page.HUMCaseCloseErrorPage;
            Test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('id', oCase.id);
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            HUMCaseCloseErrorPageExtension oCaseCloseError = new HUMCaseCloseErrorPageExtension(oStdController);
            
            pagereference oPageRef1 = oCaseCloseError.CaseClose();
            Test.stopTest();                  
            Case updtCase = [SELECT ID, Status FROM Case WHERE ID = :oCase.Id];
            system.assertEquals(updtCase.Status, 'In Progress'); 
        }
    }
}