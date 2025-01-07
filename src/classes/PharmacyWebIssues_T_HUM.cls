/**************************************************************************** 
Apex Class Name  : PharmacyWebIssues_T_HUM
Version          : 1.0  
Created Date     : 09/01/2020
Function         : Test class for PharmacyWebIssues_C_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* AshokKumar Nutalapati                         09/16/2020                 Original Version
* Venkata Nagasiva                                05/17/2022                US-3367406 T1PRJ0010888 - MF [1] - Rebranding - Update text references from "Humana Pharmacy" to "Centerwell Pharmacy" - Web Issues Guided Flow - Process Name
***************************************************************************************************/

@isTest
private class PharmacyWebIssues_T_HUM
{
    
    /*
    * Method Name : dataSetup
    * Description : Create common test Data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('ProessUser1','Humana Pharmacy Specialist');        
        testUser.Current_Queue__c='Test Business Process and Testing';        
        insert testUser;
        system.assertEquals(testUser.lastname =='test ProessUser1',true);
                                                        
        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
        Account accountRec = HUMTestDataHelperTest.getAccount();
        accountRec.OwnerId = testUser.id;
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
        
        Classification_Type__c ctRec1 = New Classification_Type__c();
        ctRec1.name = 'Humana Pharmacy Calls';
        ctRec1.Is_Active__c=true;        
        insert ctRec1;
        
        List<Classification__c> lstClassification = New List<Classification__c>();
        Classification__c classificationRecDec = New Classification__c();
        classificationRecDec.Name = 'Plan Web/Mobile (CT & TM)';
        classificationRecDec.Is_Active__c = true;
        lstClassification.add(classificationRecDec);
        insert lstClassification;

        List<Intent__c> lstIntent = New List<Intent__c>();
        Intent__c intentRecDec = New Intent__c();
        intentRecDec.Name = 'Web Education/Navigation';
        intentRecDec.Is_Active__c = true;
        lstIntent.add(intentRecDec);
        insert lstIntent;
        
        List<CTCI_Junction__c> ctciList = New list<CTCI_Junction__c>();
        CTCI_Junction__c ctciRec1 = New CTCI_Junction__c();
        ctciRec1.Classification_Type__c = ctRec1.id;
        ctciRec1.Classification__c = classificationRecDec.Id;
        ctciRec1.Intent__c = intentRecDec.id;
        ctciRec1.IsActive__c = true;
        ctciList.add(ctciRec1);
        
        insert ctciList;

        Template__c webIssuesTemplate = new Template__c();
        webIssuesTemplate.Description__c = 'Centerwell Pharmacy Web Issues';
        insert webIssuesTemplate;
        
        Case objCase = HUMTestDataHelperTest.getCase(); 
        system.runAs(testUser)
        { 
            //Case objCase = HUMTestDataHelperTest.getCase();            
            objCase.Guided_Process_Flag__c = True;
            objCase.Owner_Queue__c = 'Humana Pharmacy Calls';
            objCase.Case_Owner__c = 'Humana Pharmacy Calls';
            objCase.Classification_Type__c = 'Humana Pharmacy Calls';
            objCase.Work_Queue_View_Name__c = 'Other';
            objCase.status = 'In Progress';        
            objCase.Policy_Member__c = policyMemRec.Id;
            objCase.AccountID = accountRec.Id;
            insert objCase;
            system.assertNotEquals(objCase, null, 'Value not matched');            
                        
            HUMConstants__c objType = new HUMConstants__c(Name = 'HUMCase',StringValue__c = 'Case');
            insert objType;
            system.assertEquals(objType.id!=null,true); 
        }
        Template_Submission_Owner__c webIssuesTemplateSubmissionOwner = new Template_Submission_Owner__c();
        webIssuesTemplateSubmissionOwner.Object_Owner_ID__c = objCase.Id;
        webIssuesTemplateSubmissionOwner.Object_Owner_Type__c = 'TEST';
        insert webIssuesTemplateSubmissionOwner;
        Template_Submission__c webIssuesTemplateSubmission = new Template_Submission__c();
        webIssuesTemplateSubmission.template__c = webIssuesTemplate.id;
        webIssuesTemplateSubmission.Submission_Owner__c = webIssuesTemplateSubmissionOwner.id;
        insert webIssuesTemplateSubmission;
        Template_Field__c webIssuesTemplateField = new Template_Field__c();
        webIssuesTemplateField.Template__c = webIssuesTemplate.id;
        insert webIssuesTemplateField;
        List<Template_Submission_data__c> webIssuesTemplateSubmissionDataList = New list<Template_Submission_data__c>();
        Template_Submission_Data__c webIssuesTemplateSubmissionData = new Template_Submission_Data__c();
        webIssuesTemplateSubmissionData.Template_Submission__c = webIssuesTemplateSubmission.id;
        webIssuesTemplateSubmissionData.Value__c = 'TESTING';
        webIssuesTemplateSubmissionData.Template_Field__c = webIssuesTemplateField.id;
        webIssuesTemplateSubmissionDataList.add(webIssuesTemplateSubmissionData);
        Template_Submission_Data__c webIssuesTemplateSubmissionData1 = new Template_Submission_Data__c();
        webIssuesTemplateSubmissionData1.Template_Submission__c = webIssuesTemplateSubmission.id;
        webIssuesTemplateSubmissionData1.Value__c = 'TESTING';
        webIssuesTemplateSubmissionData1.Template_Field__c = webIssuesTemplateField.id;
        webIssuesTemplateSubmissionDataList.add(webIssuesTemplateSubmissionData1);
        insert webIssuesTemplateSubmissionDataList;
        }

    
    /*
    * Method Name : testWebIssueMethods
    * Description : Method to assign values to PharmacyWebIssues_T_HUM
    * Return type : void
    * Paramater   : NA
    */   
    testMethod static void testWebIssueMethods()
    {
        User oUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test ProessUser1'];
        System.assertEquals(oUser.LastName,'test ProessUser1');
        List<Case> lstCases = [Select id, CaseNumber from case limit 1];
        Case caseRecord = [Select id from case limit 1];
        system.runAs(oUser)
        {
            Test.startTest();
            ApexPages.currentPage().getParameters().put('CaseId', lstCases[0].id);
            ApexPages.currentPage().getParameters().put('TabId', 'S1-01');
            PharmacyWebIssues_C_HUM oProcess =new PharmacyWebIssues_C_HUM();
            oProcess.MemberId = '';
            oProcess.MemberName = '';
            oProcess.MemberDOB = '';
            oProcess.getSubmitWebIssue();
            PharmacyWebIssues_C_HUM.updateWebIssuesCTCI(new list<Case>{caseRecord});           
            Test.stopTest();
        }
    }
}