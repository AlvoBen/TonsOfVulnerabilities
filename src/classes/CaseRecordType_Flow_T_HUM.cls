/*******************************************************************************************************************************
Apex Class Name : CaseRecordType_Flow_T_HUM 
Version         : 1.0
Created On      : 02/11/2021
Function        : This class is used for testing the Case_RecordType Flow

Modification Log: 
* Developer Name           Code Review                     Date                            Description
*------------------------------------------------------------------------------------------------------------------------------
* Aaron Speakman                                           2/19/2021                       Original Version
* Gowthami Thota                                            07/31/2023                     Test Class Fix for UAT 
****************************************************************************************************************************/

@isTest
public class CaseRecordType_Flow_T_HUM {

    @testSetup static void TestSetup(){
        
        User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        usr.Current_Queue__c='Test Business Process and Testing';
        usr.Mentor_Sub_Function__c = 'Go365';
        insert usr;
        system.assertEquals(usr.lastname=='test User1',true);

        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
    
        //Creating Account Record
        Account accountRec = HUMTestDataHelperTest.getAccount();
        accountRec.OwnerId = usr.id;
        accountRec.FirstName = 'Test';
        accountRec.LastName = 'Account';
        accountRec.Enterprise_Id__c = '123456';
        accountRec.recordtypeId = AccountRecordType.id;
        insert accountRec;
        system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
        
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
        
        List<Classification_Type__c> ctList= New list<Classification_Type__c>();
        //Launch Compass Functionality
        Classification_Type__c ctRec0 = New Classification_Type__c();
        ctRec0.name = 'Provider Calls (Dental)';
        ctRec0.Is_Active__c=true;
        ctRec0.Mentor_Function__c ='Contact Center Provider';
        ctList.add(ctRec0);
        Classification_Type__c ctRec = New Classification_Type__c();
        ctRec.name = 'calls';
        ctRec.Is_Active__c=true;
        ctList.add(ctRec);
        
        Classification_Type__c ctRec1 = New Classification_Type__c();
        ctRec1.name = 'calls (RSO)';
        ctRec1.Is_Active__c=true;
        ctList.add(ctRec1);
        insert ctList;
        system.assertEquals(ctList.size(),3);
        
        Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = 'Test Business Process and Testing', Service_Center__c = 'Business', Department__c = 'Process and Testing',Classification_Type__c=ctList[0].id);
        insert oWorkQueue;
        system.assertEquals(oWorkQueue.Service_Center__c,'Business');
        
        List<Classification__c> lstClassification = New List<Classification__c>();
        Classification__c classificationRec = New Classification__c();
        classificationRec.Name = 'Benefits';
        classificationRec.Is_Active__c = true;
        lstClassification.add(classificationRec);
        
        Classification__c classificationRecDec = New Classification__c();
        classificationRecDec.Name = 'Humana Pharmacy';
        classificationRecDec.Is_Active__c = true;
        lstClassification.add(classificationRecDec);
        
        Classification__c classificationRecDec1 = New Classification__c();
        classificationRecDec1.Name = 'G&A';
        classificationRecDec1.Is_Active__c = true;
        lstClassification.add(classificationRecDec1);

        Classification__c classificationRecDec2 = New Classification__c();
        classificationRecDec2.Name = 'Rx';
        classificationRecDec2.Is_Active__c = true;
        lstClassification.add(classificationRecDec2);
        insert lstClassification;
        system.assertEquals(lstClassification.size(),4);
       
        List<Intent__c> lstIntent = New List<Intent__c>();
        Intent__c intentRec = New Intent__c();
        intentRec.Name = 'Rewards';
        intentRec.Is_Active__c = true;
        lstIntent.add(intentRec);
        
        Intent__c intentRecDec = New Intent__c();
        intentRecDec.Name = 'Estimate';
        intentRecDec.Is_Active__c = true;
        lstIntent.add(intentRecDec);
        
        
        Intent__c intentRecDec1 = New Intent__c();
        intentRecDec1.Name = 'Expedited';
        intentRecDec1.Is_Active__c = true;
        lstIntent.add(intentRecDec1);
    
        
        Intent__c intentRecDec2 = New Intent__c();
        intentRecDec2.Name = 'Part D Redetermination';
        intentRecDec1.Is_Active__c = true;
        lstIntent.add(intentRecDec2);
        insert lstIntent;
        system.assertEquals(lstIntent.size(),4);
        
        List<CTCI_Junction__c> ctciList = New list<CTCI_Junction__c>();
        CTCI_Junction__c ctciRec = New CTCI_Junction__c();
        ctciRec.Classification_Type__c = ctRec.id;
        ctciRec.Classification__c = classificationRec.Id;
        ctciRec.Intent__c = intentRec.id;
        ctciRec.IsActive__c = true;
        ctciList.add(ctciRec);
     
        CTCI_Junction__c ctciRec1 = New CTCI_Junction__c();
        ctciRec1.Classification_Type__c = ctRec.id;
        ctciRec1.Classification__c = classificationRecDec1 .Id;
        ctciRec1.Intent__c = intentRecDec1.id;
        ctciRec1.IsActive__c = true;
        ctciList.add(ctciRec1);
       
        
        CTCI_Junction__c ctciRec2 = New CTCI_Junction__c();
        ctciRec2.Classification_Type__c = ctRec.id;
        ctciRec2.Classification__c = classificationRecDec2 .Id;
        ctciRec2.Intent__c = intentRecDec2.id;
        ctciRec2.IsActive__c = true;
        ctciList.add(ctciRec2);
    
        CTCI_Junction__c ctciRec3 = New CTCI_Junction__c();
        ctciRec3.Classification_Type__c = ctRec1.id;
        ctciRec3.Classification__c = classificationRecDec1.Id;
        ctciRec3.Intent__c = intentRecDec1.id;
        ctciRec3.IsActive__c = true;
        ctciList.add(ctciRec3);
            
        CTCI_Junction__c ctciRec4 = New CTCI_Junction__c();
        ctciRec4.Classification_Type__c = ctRec1.id;
        ctciRec4.Classification__c = classificationRecDec2.Id;
        ctciRec4.Intent__c = intentRecDec2.id;
        ctciRec4.IsActive__c = true;
        ctciList.add(ctciRec4);
               
        insert ctciList;
        system.assertEquals(ctciList.size(),5);
        
        Case caseRecord = New Case();
        caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord.Status = 'In Progress';
        caseRecord.AccountId = accountRec.id;
        caseRecord.ownerId = usr.Id;
        caseRecord.Classification_Type__c = 'calls';
        caseRecord.Classification_id__c=lstClassification[0].id;
        caseRecord.Intent_id__c = lstIntent[0].id;
        caseRecord.Interacting_With__c = accountRec.id;
        caseRecord.Interacting_With_Type__c = 'Member';
        caseRecord.Interacting_About_Type__c = 'Member';
        caseRecord.Policy_Member__c = null;
        caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
        caseRecord.Autoroute_Status__c = 'Evaluate';
        caseRecord.Origin = 'Inbound Call';
        insert caseRecord;
    
    }

    private static testMethod void validCaseRecordType(){
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User1'];

        Case caseRecord = [SELECT Id, RecordTypeID FROM CASE WHERE Owner_Queue__c = 'Louisville RSO Calls Support' LIMIT 1];

        RecordType memberCaseRecordType = [Select Id from RecordType where Name='Member Case' and sObjectType='Case'];

        System.debug('Case Record Type Id: ' + caseRecord.RecordTypeID);
        System.debug('Member Case: ' + memberCaseRecordType);

        Map<String, Object> params = new Map<String, Object>();
        params.put('Case_ID', caseRecord.id);
        params.put('Case_RecordType_Name', 'Member Case');
        
        System.runAs(testUser)
        {
            Test.startTest();
                Flow.Interview.Case_RecordType myflow = new Flow.Interview.Case_RecordType(params);
                myflow.Start();
            Test.stopTest();
        }

        Case caseTestRecord = [SELECT Id,RecordTypeID FROM CASE WHERE Id = :caseRecord.id LIMIT 1];
        system.assertEquals(caseRecord.RecordTypeID, caseTestRecord.RecordTypeID);
    }
    
    private static testMethod void invalidCaseRecordType(){
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User1'];

        Case caseRecord = [SELECT Id, RecordTypeID FROM CASE WHERE Owner_Queue__c = 'Louisville RSO Calls Support' LIMIT 1];

        RecordType memberCaseRecordType = [Select Id from RecordType where Name='Member Case' and sObjectType='Case'];

        System.debug('Case Record Type Id: ' + caseRecord.RecordTypeID);
        System.debug('Member Case: ' + memberCaseRecordType);

        Map<String, Object> params = new Map<String, Object>();
        params.put('Case_ID', caseRecord.id);
        params.put('Case_RecordType_Name', 'Member Case');
        
        System.runAs(testUser)
        {
            Test.startTest();
                Flow.Interview.Case_RecordType myflow = new Flow.Interview.Case_RecordType(params);
                myflow.Start();
            Test.stopTest();
        }

        Case caseTestRecord = [SELECT Id,RecordTypeID FROM CASE WHERE Id = :caseRecord.id LIMIT 1];
        system.assertEquals(caseRecord.RecordTypeID, caseTestRecord.RecordTypeID);
    }
    
}