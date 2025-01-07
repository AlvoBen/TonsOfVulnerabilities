/**********************************************************************************************************************************************************************
Apex Class Name :  caseLoggingDetails_LT
Version: 1.0 
Created On       : 07/27/2022
Function         : Test class to caseLoggingDetails_LC apex class

Modification Log : 
* Developer Name          Code Review                   Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Prasuna Pattabhi                                   07/27/2022                 Original Version
***********************************************************************************************************************************************************************/

@isTest
Public with sharing Class caseLoggingDetails_LT {

    @testSetup static void setup(){

        Group oNewGroup = null;
        oNewGroup = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oNewGroup;

        QueueSObject oNewQueue = New QueueSObject(QueueId = oNewGroup.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;

        User objUser = HUMTestDataHelperTest.createTestUser('caseLoggedInfo','Customer Care Specialist');
        insert objUser;        
        objUser.Current_Queue__C = 'Test Business Process and Testing';
        update objUser;

        User objUserHumanaSpecialist = HUMTestDataHelperTest.createTestUser('CaseLogHPS','Humana Pharmacy Specialist');   
        insert objUserHumanaSpecialist;
        List<PermissionSet> humanaSupervisorPS = [Select id , Name from PermissionSet where name ='CRMS_300_Humana_Pharmacy_Supervisor'];
        if(humanaSupervisorPS != null){
        	insert new PermissionSetAssignment(assigneeId = objUserHumanaSpecialist.id , PermissionSetId =humanaSupervisorPS[0].id);
        }

        system.runAs(objUser){
            
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'CaseLogged FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.PersonMailingCountry ='United States';
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            
        }
    }
    
     @isTest static void testLoggingFunctionality(){
        User caseLoggedInfoUser = [Select Id,Name,Network_User_Id__c from User where LastName='test caseLoggedInfo'];
        Account objMemAccount = [select id, Enterprise_ID__c from Account where FirstName = 'CaseLogged FName' and LastName = 'LName' limit 1];
        
        System.runAs(caseLoggedInfoUser){
        
            Test.startTest(); 
           
            Case objCase = new Case(AccountId = objMemAccount.Id, Interacting_With_Type__c = 'Member',Interacting_With__c = objMemAccount.Id, Due_Date__c = Date.Today(), 
                             Case_Owner__c = caseLoggedInfoUser.Name, Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c='CRU', status='In Progress');
            objCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
            insert objCase;

            String caseId = objCase.id;

            String logValues = '{ "LableName" :"Gender", "LabelValue" : "Female", "RelatedField" : "test User1", "Section" : "accoutDetailHighlightsHum"}';

            Attachment_log__c attachLog = new Attachment_Log__c (
                    Case__c = caseId,
                    Attachment_Key__c = objMemAccount.Enterprise_ID__c,
                    Attachment_Description__c = 'Enterprise Id',
                    Attachment_Type__c = 'Account',
                    Attachment_Sub_type__c = 'Account Details',
                    Source_System__c = 'CRM Lightning',
                    Logging_URL__c = '',
                    Standard_Value__c = '',
                    User_Selected_Value__c = 'logValues',
                    User_Selected_Value_EXT__c = 'logValues',
                    Created_By__c =  caseLoggedInfoUser.Network_User_Id__c,
                    CreatedByQueue__c = 'Cincinnati CRU'
                );
            insert attachLog;
            String attachmentId = attachLog.Id;                   
            system.assertEquals(attachLog.Attachment_Description__c=='Enterprise Id',true);
            
            caseLoggingDetails_LC.hasDeleteAccess(caseId);
            caseLoggingDetails_LC.getCaseLoggedDetails(caseId,0);
            caseLoggingDetails_LC.getCaseNumber(caseId);
            caseLoggingDetails_LC.getCaseLoggedDetails(caseId,5);
            caseLoggingDetails_LC.deleteAttachment(attachmentId);
            
            Test.stopTest();
        }
    }
   @isTest static void testLoggingFunctionalityHP(){
        User caseLoggedInfoUser = [Select Id, Name from User where LastName='test CaseLogHPS'];
       
        System.runAs(caseLoggedInfoUser){
        
            Test.startTest(); 
                       
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'CaseLogged FName1';
            objMemAccount.LastName = 'LName1'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.PersonMailingCountry ='United States';
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
			system.assertEquals(objMemAccount.LastName=='LName1',true);  
            
            Case objCase = new Case(
                AccountId = objMemAccount.Id, 
                Interacting_With_Type__c = 'Member',
                Interacting_With__c = objMemAccount.Id, 
                Due_Date__c = Date.Today(), 
                Case_Owner__c = caseLoggedInfoUser.Name, 
                Owner_Queue__c='Humana Pharmacy Web Chat', 
                Work_Queue_View_Name__c='Affidavit Request', 
                Service_Center__c = 'Humana Pharmacy',
                Department__c='Web Chat', 
                status='In Progress',
                Origin='Web Chat');
            objCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
            insert objCase;
            String caseId = objCase.id;
            caseLoggingDetails_LC.hasDeleteAccess(caseId);
            Test.stopTest();
        }
    }    
}