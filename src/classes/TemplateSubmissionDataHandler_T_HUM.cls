/**********************************************************************************************************************************************************
Apex Class Name  : TemplateSubmissionDataHandler_T_HUM
Version          : 1.0
Created Date     : 12/15/2017
Function         : Test class for TemplateSubmissionDataHandler
***********************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Syed Mubarak T                                    04/01/2019                  Original version
* Mohammed Noor S             351483                04/17/2019                  Changes done for Code coverage of REQ - 396223.
* Pooja Kumbhar                                     09/10/2020                  Changes done for increasing Trigger code coverage.
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
************************************************************************************************************************************************************/

@isTest
public class TemplateSubmissionDataHandler_T_HUM
{
    @testSetup static void setUpTestData()
    {
        User usr = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert usr;
        system.assertEquals(usr.lastname=='test User1',true);
        
        HUMTriggerSwitch__c tSwitch = new HUMTriggerSwitch__c();
        tSwitch.Name = 'TemplateSubmissionData';
        tSwitch.Exeute_Trigger__c = true;
        insert tSwitch;
        
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
            
            List<Classification_Type__c> ctList= new list<Classification_Type__c>();
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'calls';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);
            insert ctList;
            system.assertEquals(ctList.size(),1);
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Benefits';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Humana Pharmacy';
            classificationRecDec.Is_Active__c = true;
            lstClassification.add(classificationRecDec);
            insert lstClassification;
            system.assertEquals(lstClassification.size(),2);
            
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            
            Intent__c intentRecDec = new Intent__c();
            intentRecDec.Name = 'Estimate';
            intentRecDec.Is_Active__c = true;
            lstIntent.add(intentRecDec);
            insert lstIntent;
            system.assertEquals(lstIntent.size(),2);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            insert ctciList;
            system.assertEquals(ctciList.size(),1);
            
            Case trrCaseRecord = new Case();
            trrCaseRecord.Owner_Queue__c = 'Test Omni Queue';
            trrCaseRecord.Status = 'In Progress';
            trrCaseRecord.AccountId = accountRec.id;
            trrCaseRecord.ownerId = usr.Id;
            trrCaseRecord.Classification_Type__c = 'calls';
            trrCaseRecord.Classification_id__c=lstClassification[0].id;
            trrCaseRecord.Intent_id__c = lstIntent[0].id;
            trrCaseRecord.Interacting_With__c = accountRec.id;
            trrCaseRecord.Interacting_With_Type__c = 'Member';
            trrCaseRecord.Policy_Member__c = null;
            trrCaseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            trrCaseRecord.Autoroute_Status__c = 'Evaluate';
            trrCaseRecord.Origin = 'Inbound Call';
            trrCaseRecord.Omni_Eligible__c = true;
            trrCaseRecord.IsEscalated = false;
            trrCaseRecord.Due_Date__c = Date.today();
            trrCaseRecord.Subtype__c = 'TRR';
            trrCaseRecord.Follow_up_Due_Date__c = Date.today() + 1;
            //trrCaseRecord.Transferred_Date__c = DateTime.now();
            trrCaseRecord.Work_Queue_View_Name__c = 'Test Queue View';
            
            insert trrCaseRecord;
                        
			list<Case_Milestone__c> lstCaseMile = new list<Case_Milestone__c>();            
            Case_Milestone__c casMile = new Case_Milestone__c();
            casMile.Milestone__c = '1802';
			casMile.Work_Queue__c = 'Test Omni Queue';
			casMile.Work_Queue_View__c = 'Test Queue View';
			casMile.Reply_Code_Category__c = 'UI Series';
			casMile.Case_Action__c = 'CRE';                        
            casMile.Name = 'CM-001';
            lstCaseMile.add(casMile);
            
            casMile = new Case_Milestone__c();
            casMile.Milestone__c = '1803';
			casMile.Work_Queue__c = 'Test Omni Queue';
			casMile.Work_Queue_View__c = 'Test Queue View';
			casMile.Reply_Code_Category__c = 'UI Series';
			casMile.Case_Action__c = 'UPD';                        
            casMile.Name = 'CM-002';
            lstCaseMile.add(casMile);
            
            casMile = new Case_Milestone__c();
            casMile.Milestone__c = '1905';
			casMile.Work_Queue__c = 'Test Omni Queue';
			casMile.Work_Queue_View__c = 'Test Queue View';
			casMile.Reply_Code_Category__c = 'Rejection Replies';
			casMile.Case_Action__c = 'CRE';                        
            casMile.Name = 'CM-003';
            lstCaseMile.add(casMile);
            
            casMile = new Case_Milestone__c();
            casMile.Milestone__c = '1910';
			casMile.Work_Queue__c = 'Test Omni Queue';
			casMile.Work_Queue_View__c = 'Test Queue View';
			casMile.Reply_Code_Category__c = 'Rejection Replies';
			casMile.Case_Action__c = 'UPD';                        
            casMile.Name = 'CM-004';
            lstCaseMile.add(casMile);
            insert lstCaseMile;
            
			Template__c template = new Template__c();
            template.Description__c = 'TRR Template';
            template.Unique_Template_Name__c = 'TRR_Template' ;
            insert template;
            
            list<Template_Field__c> lstTemplateFlds = new list<Template_Field__c>();
			
			Template_Field__c templateFld = new Template_Field__c();
            templateFld.Name = 'Processing Time Stamp';
            templateFld.Data_Type__c = 'DATETIME';
            templateFld.Template__c = template.Id;
            lstTemplateFlds.add(templateFld);
            
            templateFld = new Template_Field__c();
            templateFld.Name = 'Trans Code';
            templateFld.Data_Type__c = 'STRING';
            templateFld.Template__c = template.Id;
			lstTemplateFlds.add(templateFld);
			
			templateFld = new Template_Field__c();
            templateFld.Name = 'Case Action';
            templateFld.Data_Type__c = 'STRING';
            templateFld.Template__c = template.Id;            
			lstTemplateFlds.add(templateFld);
			
			templateFld = new Template_Field__c();
            templateFld.Name = 'Reply Code Category';
            templateFld.Data_Type__c = 'STRING';
            templateFld.Template__c = template.Id;            
			lstTemplateFlds.add(templateFld);
						
			templateFld = new Template_Field__c();
            templateFld.Name = 'Reply Type Track ID';
            templateFld.Data_Type__c = 'STRING';
            templateFld.Template__c = template.Id;            
			lstTemplateFlds.add(templateFld);
			
			templateFld = new Template_Field__c();
            templateFld.Name = 'Reply Code';
            templateFld.Data_Type__c = 'STRING';
            templateFld.Template__c = template.Id;            
			lstTemplateFlds.add(templateFld);			
			insert lstTemplateFlds;			    
    }
    
	/*
    * This method is used get the Template fields
    * @param   None
    * @return  None
    */
	private static map<string,string> getTRRTemplateFields()
	{
		map<string,string> mapTemplateFlds = new map<string, string>();
		list<Template_Field__c> lstTemplateFlds = [SELECT ID, Name, Data_Type__c FROM Template_Field__c WHERE Template__r.Unique_Template_Name__c = 'TRR_Template'];
		system.assertNotEquals(lstTemplateFlds, null);
		system.assertEquals(lstTemplateFlds.isEmpty(), false);
		for(Template_Field__c templateFld : lstTemplateFlds)
		{
			mapTemplateFlds.put(templateFld.Name, templateFld.ID);
		}
		return mapTemplateFlds;
	}
	
	/*
    * This method is used to test the UI Series Template Create and Update Scenario
    * @param   None
    * @return  None
    */	
	static testMethod void testUISeriesTemplate()
	{
		list<Template_Submission_Data__c> lstSubmissionData = new list<Template_Submission_Data__c>();
		
		Template__c trrTemplate = [SELECT ID, Unique_Template_Name__c FROM Template__c WHERE Unique_Template_Name__c = 'TRR_Template' limit 1];
		system.assertNotEquals(trrTemplate, null);
		
		Case trrCaseRecord = [select Id from Case Where Subtype__c = 'TRR' LIMIT 1];
		system.assertNotEquals(trrCaseRecord, null);
		
		map<string, string> mapTemplateFlds = getTRRTemplateFields();
		system.assertNotEquals(mapTemplateFlds, null);
				
		Template_Submission_Owner__c TSubOwner = new Template_Submission_Owner__c();
		TSubOwner.Object_Owner_ID__c = trrCaseRecord.Id;
		TSubOwner.Object_Owner_Type__c = 'Case';
		insert TSubOwner;
		system.assertNotEquals(TSubOwner.ID, null);
		
		Template_Submission__c TSubmission = new Template_Submission__c();
		TSubmission.Template__c = trrTemplate.Id;
		TSubmission.Tenant_Id__c = '00';
		TSubmission.Version__c = 0;
		TSubmission.Submission_Owner__c = TSubOwner.Id;
		insert TSubmission;
		
		Template_Submission_Data__c submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Trans Code'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '004';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '61';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code Category'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = 'UI Series';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Case Action'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = 'CRE';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Type Track ID'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '123456789';
		lstSubmissionData.add(submissionData);
		
		test.startTest();
			insert lstSubmissionData;
		test.stopTest();
		
		list<Enrollment_Track_History__c> lstTrackHistory = [SELECT Id, Name, Case__c, case__r.casenumber, Recipient_App__c, Source_App__c, Recipient_Track_ID__c, Source_Track_Id__c, Member_ID__c, Status__c, Milestone_Id__c, Milestone_MET_Id__c, CreatedDate, CreatedById 
															FROM Enrollment_Track_History__c WHERE Case__c = :trrCaseRecord.ID];		
		system.assertNotEquals(lstTrackHistory, null);
		system.assertEquals(lstTrackHistory.isEmpty(), false);
		
		list<Template_Submission_Data__c> lstSubmissionData1 = new list<Template_Submission_Data__c>();
		Template_Submission__c TSubmission1 = new Template_Submission__c();
		TSubmission1.Template__c = trrTemplate.Id;
		TSubmission1.Tenant_Id__c = '00';
		TSubmission1.Version__c = 0;
		TSubmission1.Submission_Owner__c = TSubOwner.Id;
		insert TSubmission1;
		
		Template_Submission_Data__c submissionData1 = new Template_Submission_Data__c();
		submissionData1.Template_Field__c = mapTemplateFlds.get('Trans Code'); 
		submissionData1.Template_Submission__c = TSubmission1.Id;
		submissionData1.Value__c = '004';
		lstSubmissionData1.add(submissionData1);
		
		submissionData1 = new Template_Submission_Data__c();
		submissionData1.Template_Field__c = mapTemplateFlds.get('Reply Code'); 
		submissionData1.Template_Submission__c = TSubmission1.Id;
		submissionData1.Value__c = '61';
		lstSubmissionData1.add(submissionData1);
		
		submissionData1 = new Template_Submission_Data__c();
		submissionData1.Template_Field__c = mapTemplateFlds.get('Reply Code Category'); 
		submissionData1.Template_Submission__c = TSubmission1.Id;
		submissionData1.Value__c = 'UI Series';
		lstSubmissionData1.add(submissionData1);
		
		submissionData1 = new Template_Submission_Data__c();
		submissionData1.Template_Field__c = mapTemplateFlds.get('Case Action'); 
		submissionData1.Template_Submission__c = TSubmission1.Id;
		submissionData1.Value__c = 'UPD';
		lstSubmissionData1.add(submissionData1);
		
		submissionData1 = new Template_Submission_Data__c();
		submissionData1.Template_Field__c = mapTemplateFlds.get('Reply Type Track ID'); 
		submissionData1.Template_Submission__c = TSubmission1.Id;
		submissionData1.Value__c = '123456780';
		lstSubmissionData1.add(submissionData1);
				
		insert lstSubmissionData1;
				
		list<Enrollment_Track_History__c> lstTrackHistory1 = [SELECT Id, Name, Case__c, case__r.casenumber, Recipient_App__c, Source_App__c, Recipient_Track_ID__c, Source_Track_Id__c, Member_ID__c, Status__c, Milestone_Id__c, Milestone_MET_Id__c, CreatedDate, CreatedById 
															FROM Enrollment_Track_History__c WHERE Case__c = :trrCaseRecord.ID];
		system.assertNotEquals(lstTrackHistory1, null);
		system.assertEquals(lstTrackHistory1.isEmpty(), false);					
	}
	
	/*
    * This method is used to test the Rejection Replies Template Create and Update Scenario
    * @param   None
    * @return  None
    */		
	static testMethod void testRejectionTemplate()
	{
		list<Template_Submission_Data__c> lstSubmissionData = new list<Template_Submission_Data__c>();
		
		Template__c trrTemplate = [SELECT ID, Unique_Template_Name__c FROM Template__c WHERE Unique_Template_Name__c = 'TRR_Template' limit 1];
		system.assertNotEquals(trrTemplate, null);
		
		Case trrCaseRecord = [select Id from Case Where Subtype__c = 'TRR' LIMIT 1];
		system.assertNotEquals(trrCaseRecord, null);
		
		map<string, string> mapTemplateFlds = getTRRTemplateFields();
		system.assertNotEquals(mapTemplateFlds, null);
				
		Template_Submission_Owner__c TSubOwner = new Template_Submission_Owner__c();
		TSubOwner.Object_Owner_ID__c = trrCaseRecord.Id;
		TSubOwner.Object_Owner_Type__c = 'Case';
		insert TSubOwner;
		system.assertNotEquals(TSubOwner.ID, null);
		
		Template_Submission__c TSubmission = new Template_Submission__c();
		TSubmission.Template__c = trrTemplate.Id;
		TSubmission.Tenant_Id__c = '00';
		TSubmission.Version__c = 0;
		TSubmission.Submission_Owner__c = TSubOwner.Id;
		insert TSubmission;
		
		Template_Submission_Data__c submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Trans Code'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '004';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '61';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code Category'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = 'Rejection Replies';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Case Action'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = 'CRE';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Type Track ID'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '123456781';
		lstSubmissionData.add(submissionData);
		
		test.startTest();
		insert lstSubmissionData;
		test.stopTest();
		
		list<Enrollment_Track_History__c> lstTrackHistory = [SELECT Id, Name, Case__c, case__r.casenumber, Recipient_App__c, Source_App__c, Recipient_Track_ID__c, Source_Track_Id__c, Member_ID__c, Status__c, Milestone_Id__c, Milestone_MET_Id__c, CreatedDate, CreatedById 
															FROM Enrollment_Track_History__c WHERE Case__c = :trrCaseRecord.ID];		
		system.assertNotEquals(lstTrackHistory, null);
		system.assertEquals(lstTrackHistory.isEmpty(), false);
		
		list<Template_Submission_Data__c> lstSubmissionData1 = new list<Template_Submission_Data__c>();
		Template_Submission__c TSubmission1 = new Template_Submission__c();
		TSubmission1.Template__c = trrTemplate.Id;
		TSubmission1.Tenant_Id__c = '00';
		TSubmission1.Version__c = 0;
		TSubmission1.Submission_Owner__c = TSubOwner.Id;
		insert TSubmission1;
		
		Template_Submission_Data__c submissionData1 = new Template_Submission_Data__c();
		submissionData1.Template_Field__c = mapTemplateFlds.get('Trans Code'); 
		submissionData1.Template_Submission__c = TSubmission1.Id;
		submissionData1.Value__c = '004';
		lstSubmissionData1.add(submissionData1);
		
		submissionData1 = new Template_Submission_Data__c();
		submissionData1.Template_Field__c = mapTemplateFlds.get('Reply Code'); 
		submissionData1.Template_Submission__c = TSubmission1.Id;
		submissionData1.Value__c = '61';
		lstSubmissionData1.add(submissionData1);
		
		submissionData1 = new Template_Submission_Data__c();
		submissionData1.Template_Field__c = mapTemplateFlds.get('Reply Code Category'); 
		submissionData1.Template_Submission__c = TSubmission1.Id;
		submissionData1.Value__c = 'Rejection Replies';
		lstSubmissionData1.add(submissionData1);
		
		submissionData1 = new Template_Submission_Data__c();
		submissionData1.Template_Field__c = mapTemplateFlds.get('Case Action'); 
		submissionData1.Template_Submission__c = TSubmission1.Id;
		submissionData1.Value__c = 'UPD';
		lstSubmissionData1.add(submissionData1);
		
		submissionData1 = new Template_Submission_Data__c();
		submissionData1.Template_Field__c = mapTemplateFlds.get('Reply Type Track ID'); 
		submissionData1.Template_Submission__c = TSubmission1.Id;
		submissionData1.Value__c = '123456782';
		lstSubmissionData1.add(submissionData1);
		insert lstSubmissionData1;
	
		list<Enrollment_Track_History__c> lstTrackHistory1 = [SELECT Id, Name, Case__c, case__r.casenumber, Recipient_App__c, Source_App__c, Recipient_Track_ID__c, Source_Track_Id__c, Member_ID__c, Status__c, Milestone_Id__c, Milestone_MET_Id__c, CreatedDate, CreatedById 
															FROM Enrollment_Track_History__c WHERE Case__c = :trrCaseRecord.ID];		
		system.assertNotEquals(lstTrackHistory1, null);
		system.assertEquals(lstTrackHistory1.isEmpty(), false);				
	}
	
	/*
    * This method is used to test the combination of UI Series and Rejection Replies Templates Create Scenario
    * @param   None
    * @return  None
    */
	static testMethod void testUISeriesAndRejTemplate()
	{
		list<Template_Submission_Data__c> lstSubmissionData = new list<Template_Submission_Data__c>();
		
		Template__c trrTemplate = [SELECT ID, Unique_Template_Name__c FROM Template__c WHERE Unique_Template_Name__c = 'TRR_Template' limit 1];
		system.assertNotEquals(trrTemplate, null);
		
		Case trrCaseRecord = [select Id from Case Where Subtype__c = 'TRR' LIMIT 1];
		system.assertNotEquals(trrCaseRecord, null);
		
		map<string, string> mapTemplateFlds = getTRRTemplateFields();
		system.assertNotEquals(mapTemplateFlds, null);
				
		Template_Submission_Owner__c TSubOwner = new Template_Submission_Owner__c();
		TSubOwner.Object_Owner_ID__c = trrCaseRecord.Id;
		TSubOwner.Object_Owner_Type__c = 'Case';
		insert TSubOwner;
		system.assertNotEquals(TSubOwner.ID, null);
		
		Template_Submission__c TSubmission = new Template_Submission__c();
		TSubmission.Template__c = trrTemplate.Id;
		TSubmission.Tenant_Id__c = '00';
		TSubmission.Version__c = 0;
		TSubmission.Submission_Owner__c = TSubOwner.Id;
		insert TSubmission;
		
		Template_Submission_Data__c submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Trans Code'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '004';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '61';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code Category'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = 'Rejection Replies';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Case Action'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = 'CRE';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Type Track ID'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '123456789';
		lstSubmissionData.add(submissionData);
		
		Template_Submission__c TSubmission1 = new Template_Submission__c();
		TSubmission1.Template__c = trrTemplate.Id;
		TSubmission1.Tenant_Id__c = '00';
		TSubmission1.Version__c = 0;
		TSubmission1.Submission_Owner__c = TSubOwner.Id;
		insert TSubmission1;
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Trans Code'); 
		submissionData.Template_Submission__c = TSubmission1.Id;
		submissionData.Value__c = '004';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code'); 
		submissionData.Template_Submission__c = TSubmission1.Id;
		submissionData.Value__c = '61';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code Category'); 
		submissionData.Template_Submission__c = TSubmission1.Id;
		submissionData.Value__c = 'UI Series';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Case Action'); 
		submissionData.Template_Submission__c = TSubmission1.Id;
		submissionData.Value__c = 'CRE';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Type Track ID'); 
		submissionData.Template_Submission__c = TSubmission1.Id;
		submissionData.Value__c = '123456780';
		lstSubmissionData.add(submissionData);
		
		Template_Submission__c TSubmission2 = new Template_Submission__c();
		TSubmission2.Template__c = trrTemplate.Id;
		TSubmission2.Tenant_Id__c = '00';
		TSubmission2.Version__c = 0;
		TSubmission2.Submission_Owner__c = TSubOwner.Id;
		insert TSubmission2;
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Trans Code'); 
		submissionData.Template_Submission__c = TSubmission2.Id;
		submissionData.Value__c = '002';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code'); 
		submissionData.Template_Submission__c = TSubmission2.Id;
		submissionData.Value__c = '51';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code Category'); 
		submissionData.Template_Submission__c = TSubmission2.Id;
		submissionData.Value__c = 'UI Series';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Case Action'); 
		submissionData.Template_Submission__c = TSubmission2.Id;
		submissionData.Value__c = 'CRE';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Type Track ID'); 
		submissionData.Template_Submission__c = TSubmission2.Id;
		submissionData.Value__c = '123456780';
		lstSubmissionData.add(submissionData);
		
		test.startTest();
		insert lstSubmissionData;
		test.stopTest();
		
		list<Enrollment_Track_History__c> lstTrackHistory = [SELECT Id, Name, Case__c, case__r.casenumber, Recipient_App__c, Source_App__c, Recipient_Track_ID__c, Source_Track_Id__c, Member_ID__c, Status__c, Milestone_Id__c, Milestone_MET_Id__c, CreatedDate, CreatedById 
															FROM Enrollment_Track_History__c WHERE Case__c = :trrCaseRecord.ID];		
		system.assertNotEquals(lstTrackHistory, null);
		system.assertEquals(lstTrackHistory.isEmpty(), false);
														
	}
	
	/*
    * This method is used to test multiple Rejection Replies Templates Create Scenario
    * @param   None
    * @return  None
    */
	static testMethod void testMultipleRejTemplates()
	{
		list<Template_Submission_Data__c> lstSubmissionData = new list<Template_Submission_Data__c>();
		
		Template__c trrTemplate = [SELECT ID, Unique_Template_Name__c FROM Template__c WHERE Unique_Template_Name__c = 'TRR_Template' limit 1];
		system.assertNotEquals(trrTemplate, null);
		
		Case trrCaseRecord = [select Id from Case Where Subtype__c = 'TRR' LIMIT 1];
		system.assertNotEquals(trrCaseRecord, null);
		
		map<string, string> mapTemplateFlds = getTRRTemplateFields();
		system.assertNotEquals(mapTemplateFlds, null);
				
		Template_Submission_Owner__c TSubOwner = new Template_Submission_Owner__c();
		TSubOwner.Object_Owner_ID__c = trrCaseRecord.Id;
		TSubOwner.Object_Owner_Type__c = 'Case';
		insert TSubOwner;
		system.assertNotEquals(TSubOwner.ID, null);
		
		Template_Submission__c TSubmission = new Template_Submission__c();
		TSubmission.Template__c = trrTemplate.Id;
		TSubmission.Tenant_Id__c = '00';
		TSubmission.Version__c = 0;
		TSubmission.Submission_Owner__c = TSubOwner.Id;
		insert TSubmission;
		
		Template_Submission_Data__c submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Trans Code'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '004';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '61';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code Category'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = 'Rejection Replies';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Case Action'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = 'CRE';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Type Track ID'); 
		submissionData.Template_Submission__c = TSubmission.Id;
		submissionData.Value__c = '123456789';
		lstSubmissionData.add(submissionData);
		
		Template_Submission__c TSubmission1 = new Template_Submission__c();
		TSubmission1.Template__c = trrTemplate.Id;
		TSubmission1.Tenant_Id__c = '00';
		TSubmission1.Version__c = 0;
		TSubmission1.Submission_Owner__c = TSubOwner.Id;
		insert TSubmission1;
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Trans Code'); 
		submissionData.Template_Submission__c = TSubmission1.Id;
		submissionData.Value__c = '004';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code'); 
		submissionData.Template_Submission__c = TSubmission1.Id;
		submissionData.Value__c = '51';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code Category'); 
		submissionData.Template_Submission__c = TSubmission1.Id;
		submissionData.Value__c = 'Rejection Replies';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Case Action'); 
		submissionData.Template_Submission__c = TSubmission1.Id;
		submissionData.Value__c = 'CRE';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Type Track ID'); 
		submissionData.Template_Submission__c = TSubmission1.Id;
		submissionData.Value__c = '123456780';
		lstSubmissionData.add(submissionData);
		
		Template_Submission__c TSubmission2 = new Template_Submission__c();
		TSubmission2.Template__c = trrTemplate.Id;
		TSubmission2.Tenant_Id__c = '00';
		TSubmission2.Version__c = 0;
		TSubmission2.Submission_Owner__c = TSubOwner.Id;
		insert TSubmission2;
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Trans Code'); 
		submissionData.Template_Submission__c = TSubmission2.Id;
		submissionData.Value__c = '002';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code'); 
		submissionData.Template_Submission__c = TSubmission2.Id;
		submissionData.Value__c = '41';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Code Category'); 
		submissionData.Template_Submission__c = TSubmission2.Id;
		submissionData.Value__c = 'Rejection Replies';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Case Action'); 
		submissionData.Template_Submission__c = TSubmission2.Id;
		submissionData.Value__c = 'CRE';
		lstSubmissionData.add(submissionData);
		
		submissionData = new Template_Submission_Data__c();
		submissionData.Template_Field__c = mapTemplateFlds.get('Reply Type Track ID'); 
		submissionData.Template_Submission__c = TSubmission2.Id;
		submissionData.Value__c = '123456781';
		lstSubmissionData.add(submissionData);
		
		test.startTest();
		insert lstSubmissionData;
		test.stopTest();
		
		list<Enrollment_Track_History__c> lstTrackHistory = [SELECT Id, Name, Case__c, case__r.casenumber, Recipient_App__c, Source_App__c, Recipient_Track_ID__c, Source_Track_Id__c, Member_ID__c, Status__c, Milestone_Id__c, Milestone_MET_Id__c, CreatedDate, CreatedById 
															FROM Enrollment_Track_History__c WHERE Case__c = :trrCaseRecord.ID];		
		system.assertNotEquals(lstTrackHistory, null);
		system.assertEquals(lstTrackHistory.isEmpty(), false);
														
	}	
	/*
    * This method is used to test the exception in Trigger logic.
    * @param   None
    * @return  None
    */
    static testMethod void ExceptionTest()
    {
        Test.startTest();
        HUMTriggerSwitch__c tSwitch = new HUMTriggerSwitch__c();
        tSwitch.Name = 'TemplateSubmissionData';
        tSwitch.Exeute_Trigger__c = false;
        insert tSwitch;
        
        list<Template_Submission_Data__c> lstSubmissionData = new list<Template_Submission_Data__c>();
        
        Template__c trrTemplate = [SELECT ID, Unique_Template_Name__c FROM Template__c WHERE Unique_Template_Name__c = 'TRR_Template' limit 1];
        system.assertNotEquals(trrTemplate, null);
        
        Case trrCaseRecord = [select Id from Case Where Subtype__c = 'TRR' LIMIT 1];
        system.assertNotEquals(trrCaseRecord, null);
        
        map<string, string> mapTemplateFlds = getTRRTemplateFields();
        system.assertNotEquals(mapTemplateFlds, null);
                
        Template_Submission_Owner__c TSubOwner = new Template_Submission_Owner__c();
        TSubOwner.Object_Owner_ID__c = trrCaseRecord.Id;
        TSubOwner.Object_Owner_Type__c = 'Case';
        insert TSubOwner;
        system.assertNotEquals(TSubOwner.ID, null);
        
        Template_Submission__c TSubmission = new Template_Submission__c();
        TSubmission.Template__c = trrTemplate.Id;
        TSubmission.Tenant_Id__c = '00';
        TSubmission.Version__c = 0;
        TSubmission.Submission_Owner__c = TSubOwner.Id;
        insert TSubmission;
        
        Template_Submission_Data__c submissionData = new Template_Submission_Data__c();
        submissionData.Template_Field__c = mapTemplateFlds.get('Trans Code'); 
        submissionData.Template_Submission__c = TSubmission.Id;
        submissionData.Value__c = '004';
        lstSubmissionData.add(submissionData);
        
        insert lstSubmissionData;
        
        test.stopTest();
        
        
    }
}