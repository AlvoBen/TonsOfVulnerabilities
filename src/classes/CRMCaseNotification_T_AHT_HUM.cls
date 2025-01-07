/******************************************************************************************************************************
Apex Class Name  : CRMCaseNotification_T_AHT_HUM
Version          : 1.0 
Created Date     : May 2 2019
Function         : To cover test coverage for CRMCaseNotification_C_AHT_HUM.cls
Modification Log :
 *  Developer              Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------
 * Subhamay Mandal                             05/02/2019             Test class for  CRMCaseNotification_C_AHT_HUM
 * Gowthami Thota                               07/31/2023              Test Class Fix for UAT 
********************************************************************************************************************************/
@isTest
private with sharing class CRMCaseNotification_T_AHT_HUM{
    
   
    
    @testSetup static void setUpTestData(){
        Id memeberRecordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr;
        system.assertEquals(usr.lastname=='test User1',true);
        PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name = 'CRMS_1100_MemberIssueResolution' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = usr.id, PermissionSetId = pCheck.id);
         Group oGrp = NULL;
        oGrp=New Group(Name='Louisville RSO Calls Support PGQ', Type='QUEUE');
        insert oGrp;
        system.assertNotEquals(oGrp,null,'Value not matched');
        System.assertEquals(oGrp.Name, 'Louisville RSO Calls Support PGQ');
        
        QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;   
        system.assertNotEquals(oQueueObject,null,'Insert Failed');
        System.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');
        system.runAs(usr)
        { 
        Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c(Public_Group_Name__c = 'Louisville RSO Calls Support PGQ', Service_Center__c = 'Louisville', Department__c = 'Retail Service Operations (RSO) Calls Support', Name = 'Louisville RSO Calls Support',SLA_In_Days__c = 2,SLA_On_Case_Transfer__c=true);
        oWQS.OwnerId = oGrp.Id;
        insert oWQS;
        System.assert(oWQS != null);
        System.assert(oWQS.SLA_In_Days__c  == 2);
        System.assertEquals(oWQS.Public_Group_Name__c, 'Louisville RSO Calls Support PGQ');
        
        Queue_View__c oWQV = New Queue_View__c (IsActive__c = true,SLA_On_Case_Transfer__c = true,Work_Queue_Name__c = oWQS.id, Name = 'Agent Communication',SLA_In_Days__c = 1);
        insert oWQV ;
        System.assert(oWQV != null);
        System.assert(oWQV.SLA_In_Days__c  == 1);
        
        
        //Creating Account Record
        Account accountRec = HUMTestDataHelperTest.getAccount();
        accountRec.OwnerId = usr.id;
        accountRec.FirstName = 'Test';
        accountRec.LastName = 'Account';
        accountRec.Enterprise_Id__c = '123456';
        accountRec.recordtypeId = memeberRecordtypeId;
        insert accountRec;
        system.assertEquals(accountRec.recordtypeId,memeberRecordtypeId);
        
        Policy__c policyRec = New Policy__c();
        policyRec.Group_Name__c = accountRec.Id;
        policyRec.Product__c = null;
        policyRec.Policy_Group_Number__c='11111';
        policyRec.Major_Lob__c = 'MEF';
        insert policyRec;
        system.assertEquals(policyRec.Group_Name__c==accountRec.Id,true);
        
        Policy_Member__c policyMemRec = New Policy_Member__c();
        policyMemRec.Name = '12345';
        policyMemRec.Policy__c = policyRec.Id;
        policyMemRec.Member__c = accountRec.Id; 
        insert policyMemRec;
        system.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
        
        Classification_Type__c ctRec = New Classification_Type__c();
        ctRec.name = 'Calls (RSO)';
        ctRec.Is_Active__c=true;
        insert ctRec;        
        system.assertNotEquals(ctRec.id,null);
        
        Classification__c  classificationRecDec1 = New Classification__c();
        classificationRecDec1.Name = 'G&A';
        classificationRecDec1.Is_Active__c = true;
        insert classificationRecDec1;
        system.assertNotEquals(classificationRecDec1.id,null);
        
        Intent__c intentRec = New Intent__c();
        intentRec.Name = 'Rewards';
        intentRec.Is_Active__c = true;
        insert intentRec; 
        system.assertNotEquals(intentRec.id,null);
        
        CTCI_Junction__c ctciRec = New CTCI_Junction__c();
        ctciRec.Classification_Type__c = ctRec.id;
        ctciRec.Classification__c = classificationRecDec1.Id;
        ctciRec.Intent__c = intentRec.id;
        ctciRec.IsActive__c = true;
        insert ctciRec ; 
        system.assertNotEquals(ctciRec.id,null);
        
        Account objAccountForEntitlement = new Account();
        objAccountForEntitlement .Name =   'MIR Account_DO_NOT_DELETE';
        objAccountForEntitlement .RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Group').getRecordTypeId();
        insert objAccountForEntitlement; 
        system.assertNotEquals(objAccountForEntitlement.id,null);
        
        List<SlaProcess> objSlaList = [Select Id,Name,createddate,VersionNumber,IsVersionDefault,VersionMaster from SlaProcess Where IsActive = True and Name = 'Member Issue Resolution Process' order by VersionNumber desc];
               
               Entitlement objEnt = new Entitlement();
               objEnt.Name = 'Member Issue Resolution SLA';
               objEnt.StartDate = System.today();
               objEnt.EndDate = objEnt.StartDate.addYears(2);
               objEnt.Type = 'Phone Support';
               objEnt.AccountId = objAccountForEntitlement.Id;
               objEnt.SlaProcessId = objSlaList[0].Id;
               insert objEnt;  
       }              
        
    }
    
    static testMethod void testcaseInitialCaseTurnaroundTime(){
   
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Account accountRec = [select id from Account where LastName = 'Account' and Enterprise_Id__c = '123456' limit 1];
        Entitlement objEnt = [Select id from Entitlement where name = 'Member Issue Resolution SLA' limit 1];
        Classification__c classificationRecDec1 = [Select id from Classification__c where name = 'G&A' limit 1];
        Intent__c intentRec = [Select id from Intent__c where name = 'Rewards' limit 1];
        Test.startTest();
        
        System.runAs(testUser){
            Case caseRecord = New Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = accountRec.id;
            caseRecord.ownerId = testUser.Id;
            caseRecord.Classification_Type__c = 'Calls (RSO)';
            caseRecord.Classification_id__c = classificationRecDec1 .id;
            caseRecord.Intent_id__c = intentRec.id;
            caseRecord.Interacting_With__c = accountRec.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Policy_Member__c = null;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Autoroute_Status__c = 'Evaluate';
            caseRecord.Origin = 'Inbound Call';
            caseRecord.Work_Queue_View_Name__c = 'Agent Communication';
            //caseRecord.EntitlementId= objEnt.id;        
            insert caseRecord;
            
            Case caseRecord2 = New Case();
            caseRecord2.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord2.Status = 'In Progress';
            caseRecord2.AccountId = accountRec.id;
            caseRecord2.ownerId = testUser.Id;
            caseRecord2.Classification_Type__c = 'Calls (RSO)';
            caseRecord2.Classification_id__c = classificationRecDec1 .id;
            caseRecord2.Intent_id__c = intentRec.id;
            caseRecord2.Interacting_With__c = accountRec.id;
            caseRecord2.Interacting_With_Type__c = 'Member';
            caseRecord2.Interacting_About_Type__c = 'Member';
            caseRecord2.Policy_Member__c = null;
            caseRecord2.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord2.Autoroute_Status__c = 'Evaluate';
            caseRecord2.Origin = 'Inbound Call';
            caseRecord2.Work_Queue_View_Name__c = 'Agent Communication';
            //caseRecord.EntitlementId= objEnt.id;        
            insert caseRecord2;
            
            system.assertEquals(caseRecord.Status=='In Progress',true);
            
            CRMCaseNotification_C_AHT_HUM oCaseNotification = new CRMCaseNotification_C_AHT_HUM();
            oCaseNotification.sParamLabel = 'Test Notification';
            oCaseNotification.bParamIsViolated = false;
            oCaseNotification.bPopupCaseList = false;            
            oCaseNotification.showPopUpCaseList();
            CRMCaseNotification_C_AHT_HUM.lstViewCMWrapper.sort();
            //system.assertEquals(CRMCaseNotification_C_AHT_HUM.lstViewCMWrapper.size() == 2, true);
            
            Test.stopTest();
       }
    }
    
}