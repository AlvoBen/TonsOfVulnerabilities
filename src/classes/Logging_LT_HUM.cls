/**********************************************************************************************************************************************************************
Apex Class Name  : Logging_LT_HUM
Version          : 1.0 
Created On       : 02/14/2022
Function         : Test class to Logging_LC_HUM apex class

Modification Log :
* Developer Name          Code Review                   Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar                                    02/14/2022                 Original Version
* ceasar sabarre                                          3/2/2023                  US: 4315363 and US: 4268275 Previously logged items
* Jonathan Dickinson                                    06/14/2023                 User Story 4705843: T1PRJ0891339 2023 Arch Remediation-SF-Tech-Filter cases having template attached from existing case history logging for process logging
* Jonathan Dickinson                                   07/16/2023           User Story 4812209: T1PRJ0891415 Platform Management- SF - TECH - Critical- Lightning – Logging
* Isaac Chung                                          12/12/2023           US 5107579 Mail Order Management; Pharmacy- Guided Flow- Inactivate Rx- Implementation (Lightning)
***********************************************************************************************************************************************************************/

@isTest
private class Logging_LT_HUM
{
    @testSetup static void setup() 
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1Log','System Administrator');
        objUser.Current_Queue__C = 'Business Process and Testing';
        insert objUser;

        User objUserNoPermSet = HUMTestDataHelperTest.createTestUser('User2','System Administrator');
        objUserNoPermSet.Current_Queue__C = GLOBAL_CONSTANT_HUM.PDP_Pilot_Pharmacy_Work_Queues;
        insert objUserNoPermSet;

        system.runAs(objUser) {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.PersonMailingCountry ='United States';
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;

            Account objMemAccountNoPermSet = HUMTestDataHelperTest.getAccount();
            objMemAccountNoPermSet.FirstName = 'Test FName No Perm Set';
            objMemAccountNoPermSet.LastName = 'LName'; 
            objMemAccountNoPermSet.source_platform_code__c = 'EM' ;
            objMemAccountNoPermSet.PersonMailingCountry ='United States';
            objMemAccountNoPermSet.Enterprise_Id__c = String.valueof(2000);
            objMemAccountNoPermSet.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccountNoPermSet.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccountNoPermSet;
            
            MemberPlan oMemPlan =  new MemberPlan();
            oMemPlan.Name = '12345';
            oMemPlan.MemberId = objMemAccount.Id;
            insert oMemPlan;

            Case oCase = new Case(AccountId = objMemAccount.Id, Interacting_With_Type__c = 'Member',Interacting_With__c = objMemAccount.Id, Due_Date__c = Date.Today(), 
            Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c='Business Process and Testing', Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c='CRU', status='In Progress');
            insert oCase;

            Case oCaseNoPermSet = new Case(AccountId = objMemAccountNoPermSet.Id, Interacting_With_Type__c = 'Member',Interacting_With__c = objMemAccountNoPermSet.Id, Due_Date__c = Date.Today(), 
            Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c='Business Process and Testing', Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c='CRU', status='In Progress');
            insert oCaseNoPermSet;

            Template__c oTemplate = new Template__c(Description__c	= 'Test Template', OwnerId=objUser.Id);
            insert oTemplate;

            Template_Field__c oTempField = new Template_Field__c(Template__c=oTemplate.Id);
            insert oTempField;

            Template_Submission_Owner__c oTempSubOwner = new Template_Submission_Owner__c(Object_Owner_ID__c=oCase.Id, Object_Owner_Type__c='Case', OwnerId=objUser.Id);
            insert oTempSubOwner;

            Template_Submission__c oTempSub = new Template_Submission__c(Template__c=oTemplate.Id, Submission_Owner__c = oTempSubOwner.Id, Version__c=1);
            insert oTempSub;

            Template_Submission_Data__c oTempSubData = new Template_Submission_Data__c(Template_Field__c=oTempField.Id, Template_Submission__c=oTempSub.Id);
            insert oTempSubData;

            Interaction__c inte = HUMTestDataHelperTest.getInteraction();
            insert inte;

            Interaction__c inteNoPermSet = HUMTestDataHelperTest.getInteraction();
            inteNoPermSet.Caller_Name__c = 'Test No Perm Set';
            insert inteNoPermSet;

            ConsumerID__c conId = new ConsumerID__c();
            conId.Account__c = objMemAccount.Id;
            conId.Consumer_ID__c = '123456789';
            conId.ID_Type__c = 'TAXID';
            insert conId;

            List<Group> lstgrp1 = [Select Id from Group where type = :GLOBAL_CONSTANT_HUM.GROUP_REGULAR_HUM and Name = :GLOBAL_CONSTANT_HUM.PDP_Pilot_PGQ];

            GroupMember grpmem = new GroupMember();
            grpmem.UserOrGroupId = objUser.id;
            grpmem.GroupId = lstgrp1[0].Id;
            insert grpmem;  

            PermissionSet pCCSPDP = [SELECT ID From PermissionSet WHERE Name = 'CRMS_205_CCSPDPPharmacyPilot' LIMIT 1];

            PermissionSetAssignment psa = New PermissionSetAssignment();
            psa.AssigneeId = objUser.id;
            psa.PermissionSetId = pCCSPDP.id;
            insert psa;

            HUMAccountCaseRecordTypes__c crt = new HUMAccountCaseRecordTypes__c();
            crt.Name = 'Member';
            crt.Case_RecordType__c='HP Member Case';
            crt.Account_RecordType__c = 'Member';
            crt.Humana_Pharmacy__c  = true;
            insert crt;

            Classification_Type__c oCtHpFinanceOps = new Classification_Type__c();
            oCtHpFinanceOps.Name = 'HP Finance Ops';
            oCtHpFinanceOps.Is_Active__c = true;
            insert oCtHpFinanceOps;

            Classification__c oCFinOps = new Classification__c();
            oCFinOps.Name = 'Billing (Finance Ops)';
            oCFinOps.Is_Active__c = true;
            insert oCFinOps;

            Classification__c oCBillingIssue = new Classification__c();
            oCBillingIssue.Name = 'Billing Issue (CT & TM)';
            oCBillingIssue.Is_Active__c = true;
            insert oCBillingIssue;

            Intent__c oIntInvcReqs = new Intent__c();
            oIntInvcReqs.Name = 'Invoice Requests';
            oIntInvcReqs.Is_Active__c = true;
            insert oIntInvcReqs;

            CTCI_Junction__c oCTCIFinOps = new CTCI_Junction__c();
            oCTCIFinOps.Classification__c = oCFinOps.id;
            oCTCIFinOps.Intent__c = oIntInvcReqs.id;
            oCTCIFinOps.Classification_Type__c = oCtHpFinanceOps.id;
            oCTCIFinOps.IsActive__c = true;
            insert oCTCIFinOps;

            CTCI_Junction__c oCTCIBillingIssue = new CTCI_Junction__c();
            oCTCIBillingIssue.Classification__c = oCBillingIssue.id;
            oCTCIBillingIssue.Intent__c = oIntInvcReqs.id;
            oCTCIBillingIssue.Classification_Type__c = oCtHpFinanceOps.id;
            oCTCIBillingIssue.IsActive__c = true;
            insert oCTCIBillingIssue;

            List<Group> lstgrp2 = [Select Id from Group where type = :GLOBAL_CONSTANT_HUM.GROUP_QUEUE_HUM and Name = :GLOBAL_CONSTANT_HUM.PDP_Pilot_Pharmacy_Work_Queues];

            Work_Queue_Setup__c wqs = new Work_Queue_Setup__c();
            wqs.Service_Center__c = 'Prescription Drug Plan';
            wqs.Department__c = 'Pilot - Humana Pharmacy Calls';
            wqs.Classification_Type__c = oCtHpFinanceOps.Id;
            wqs.OwnerId = lstgrp2[0].Id;
            wqs.IsActive__c = true;
            insert wqs;


            HUMCaseNPI_TAXID__c npi = new HUMCaseNPI_TAXID__c();
            npi.Name = 'Member';
            insert npi;

            HUMCaseInteractionAboutType__c cia = new HUMCaseInteractionAboutType__c();
            cia.CaseInteraction_About_Type__c = 'Member';
            cia.Name = 'Member';
            insert cia;
        }
    }
    
    @isTest
    static void testLoggingFunctionality() {

        User testUser = [Select Id,Name from User where LastName='test User1Log'];
        Account objMemAccount = [select id, Enterprise_ID__c from Account where FirstName = 'Test FName' and LastName = 'LName' limit 1];
        Interaction__c inte = [Select Id From Interaction__c Where Caller_Name__c = 'Test' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();                        
            
            try{
				Case objCase = new Case(AccountId = objMemAccount.Id, Interacting_With_Type__c = 'Member',Interacting_With__c = objMemAccount.Id, Due_Date__c = Date.Today(), 
								 Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c='CRU', status='In Progress');
				objCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
				insert objCase;
				
				String caseId = objCase.id;
				
				System.assertNotEquals(caseId, null, 'Case Id should not be empty'); 

				Datetime today = Datetime.now();
				String todayFormatted = today.format('MM/dd/yyyy');
				String logValues = '{\n' +
							' "LableName" :"Gender",\n' +
							' "LabelValue" : "Female",\n' +
							' "RelatedField" : "test User1Log",\n' +
							' "Section" : "accoutDetailHighlightsHum",\n' +                        
							'}';       
				
				CaseComment cc = new CaseComment();
					cc.ParentId = objCase.id;
					cc.CommentBody = 'Test';
					insert cc; 
				
				String result = Logging_LC_HUM.createLog(JSON.serialize(logValues), caseId, objMemAccount.Enterprise_ID__c, 'Enterprise Id', 'Account', 'Account Details');
				System.assertNotEquals(result, null, 'Attachment Id should not be empty');            
				
				String caseHistoryResultNoFilter = Logging_LC_HUM.getCaseHistory(objMemAccount.Id, '02/03/2022', '02/17/2022', false);//enddate - 02/17/2022, startdate - 02/03/2022
				System.assertNotEquals(caseHistoryResultNoFilter, null, 'Case History should not be empty'); 

				String caseHistoryResultWithFilter = Logging_LC_HUM.getCaseHistory(objMemAccount.Id, todayFormatted, todayFormatted, true);
				List<CaseHistory_DTO_HUM> caseHistoryParsed = (List<CaseHistory_DTO_HUM>)JSON.deserialize(caseHistoryResultWithFilter, List<CaseHistory_DTO_HUM>.class);
				System.assertEquals(1, caseHistoryParsed.size(), 'Case History should include 1 record since 1 record should be filtered out');         
				
				List<Attachment_Log__c> lstAttachmentLogs = Logging_LC_HUM.getAttachmentRecord(result);
				System.assertNotEquals(lstAttachmentLogs, null, 'Log should not be empty');
				
				List<Attachment_Log__c> lstAttachmentLogs1 = Logging_LC_HUM.getAttachmentLogs(objMemAccount.Id);
				System.assertNotEquals(lstAttachmentLogs1, null, 'Log should not be empty');

				List<CaseComment> lstCaseComments = Logging_LC_HUM.getCaseComments(objMemAccount.Id);
				System.assertNotEquals(lstCaseComments, null, 'CaseComments should not be empty');

				String logData = Logging_LC_HUM.getCaseLoggedData(caseId);
				System.assertNotEquals(null, logData);
            }catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
    }

    @isTest
    static void testNewCaseAndProcessAttachAccount()
    {

        User testUser = [Select Id,Name from User where LastName='test User1Log'];
        Account objMemAccount = [select id, Enterprise_ID__c from Account where FirstName = 'Test FName' and LastName = 'LName' limit 1];
        Interaction__c inte = [Select Id From Interaction__c Where Caller_Name__c = 'Test' limit 1];
        String caseid = [Select Id From Case Where AccountId =: objMemAccount.Id And Owner_Queue__c='Business Process and Testing' limit 1].Id;
        String temp = [Select Id From Template__c Where Description__c	= 'Test Template' limit 1].Id;
        String tempSubId = [Select Id From Template_Submission__c Where Template__c =: temp limit 1].Id;

        System.runAs(testUser)
        {
            Test.startTest();                        

            String newCase = Logging_LC_HUM.createNewCase(objMemAccount.Id, 'invoice', inte.Id);
            System.assertNotEquals(null, newCase, 'Case number should be returned');

            List<String> tempSubIds = new List<String>();
            tempSubIds.add(tempSubId); 
            Boolean isAttachProcessSuccess = Logging_LC_HUM.updateWhoId(tempSubIds, caseid);
            System.assertEquals(true, isAttachProcessSuccess);

            Test.stopTest();
        }
    }

    @isTest
    static void testNewCaseAndProcessAttachMemberPlan()
    {

        User testUser = [Select Id,Name from User where LastName='test User1Log'];
        Account objMemAccount = [select id, Enterprise_ID__c from Account where FirstName = 'Test FName' and LastName = 'LName' limit 1];
        MemberPlan oMemPlan = [select id from MemberPlan where Name = '12345' and MemberId =: objMemAccount.Id limit 1];
        Interaction__c inte = [Select Id From Interaction__c Where Caller_Name__c = 'Test' limit 1];
        String caseid = [Select Id From Case Where AccountId =: objMemAccount.Id And Owner_Queue__c='Business Process and Testing' limit 1].Id;
        String temp = [Select Id From Template__c Where Description__c	= 'Test Template' limit 1].Id;
        String tempSubId = [Select Id From Template_Submission__c Where Template__c =: temp limit 1].Id;

        System.runAs(testUser)
        {
            Test.startTest();                        

            String newCase = Logging_LC_HUM.createNewCase(oMemPlan.Id, 'invoice', inte.Id);
            System.assertNotEquals(null, newCase, 'Case number should be returned');

            List<String> tempSubIds = new List<String>();
            tempSubIds.add(tempSubId); 
            Boolean isAttachProcessSuccess = Logging_LC_HUM.updateWhoId(tempSubIds, caseid);
            System.assertEquals(true, isAttachProcessSuccess);

            Test.stopTest();
        }
    }

    @isTest
    static void testNewCaseAndProcessAttachAccountNoQueueChange()
    {

        User testUser = [Select Id,Name from User where LastName='test User2'];
        Account objMemAccount = [select id, Enterprise_ID__c from Account where FirstName = 'Test FName No Perm Set' and LastName = 'LName' limit 1];
        Interaction__c inte = [Select Id From Interaction__c Where Caller_Name__c = 'Test No Perm Set' limit 1];
        String caseid = [Select Id From Case Where AccountId =: objMemAccount.Id And Owner_Queue__c='Business Process and Testing' limit 1].Id;
        String temp = [Select Id From Template__c Where Description__c	= 'Test Template' limit 1].Id;
        String tempSubId = [Select Id From Template_Submission__c Where Template__c =: temp limit 1].Id;

        System.runAs(testUser)
        {
            Test.startTest();                        

            String newCase = Logging_LC_HUM.createNewCase(objMemAccount.Id, 'invoice', inte.Id);
            System.assertNotEquals(null, newCase, 'Case number should be returned');

            List<String> tempSubIds = new List<String>();
            tempSubIds.add(tempSubId); 
            Boolean isAttachProcessSuccess = Logging_LC_HUM.updateWhoId(tempSubIds, caseid);
            System.assertEquals(true, isAttachProcessSuccess);

            Test.stopTest();
        }
    }
    

    @isTest
    static void testFetchCases()
    {
        User testUser = [Select Id,Name from User where LastName='test User1Log'];
        Account objMemAccount = [select id, Enterprise_ID__c from Account where FirstName = 'Test FName' and LastName = 'LName' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();                        

            List<Case> cases = Logging_LC_HUM.fetchCases(objMemAccount.Id);
            System.assert(cases.size() > 0, 'Case list should not be empty');

            Test.stopTest();
        }
    }

    @isTest
    static void testAttachmentLogs()
    {
        User testUser = [Select Id,Name from User where LastName='test User1Log'];
        Account objMemAccount = [select id, Enterprise_ID__c from Account where FirstName = 'Test FName' and LastName = 'LName' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();                        

            List<Attachment_Log__c> logs = Logging_LC_HUM.getAttachmentLogs(objMemAccount.Id);
            System.assertNotEquals(null, logs);

            Test.stopTest();
        }
    }

    @isTest
    static void testNewCaseInactivateRx()
    {
        User testUser = [Select Id,Name from User where LastName='test User1Log'];
        Account objMemAccount = [select id, Enterprise_ID__c from Account where FirstName = 'Test FName' and LastName = 'LName' limit 1];
        Interaction__c inte = [Select Id From Interaction__c Where Caller_Name__c = 'Test' limit 1];
        String caseid = [Select Id From Case Where AccountId =: objMemAccount.Id And Owner_Queue__c='Business Process and Testing' limit 1].Id;
        String temp = [Select Id From Template__c Where Description__c	= 'Test Template' limit 1].Id;
        String tempSubId = [Select Id From Template_Submission__c Where Template__c =: temp limit 1].Id;

        System.runAs(testUser)
        {
            Test.startTest();                        
            Case objCase = new Case(AccountId = objMemAccount.Id, Interacting_With_Type__c = 'Member',Interacting_With__c = objMemAccount.Id, Due_Date__c = Date.Today(), Interacting_About_Type__c = 'Member',
            Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c='Louisville After Hour Calls', Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c='CRU', status='In Progress');
            objCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert objCase;

            string lstCase = [SELECT Id FROM Case WHERE Owner_Queue__c = 'Louisville After Hour Calls' limit 1].id;
            Boolean updateInactivateRxValues = Logging_LC_HUM.assignValuesForInactivateRx(lstcase);
            System.assertEquals(true, updateInactivateRxValues, 'should update and return true');

            Test.stopTest();
        }
    }
}