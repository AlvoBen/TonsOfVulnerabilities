/*******************************************************************************************************************************
Apex Class Name : CaseProcessesLaunch_LT_Hum
Version         : 1.0
Created On      : 4/1/2022
Function        : This class is test class for CaseProcessesLaunch_LC_Hum

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0            Isha Gupta                                       4/1/2022               Original Version   
*    2.0            Santhi Mandava                                   07/28/2022             US3268999- Modified testclass as per class changes 
*    3.0            Muthukumar                                       09/14/2022             DF-6166 Fix 
*    4.0           Prasuna Pattabhi                                    12/21/2022                 US:4020210 QAA Template
* 	 5.0			Prasuna Pattabhi             		02/01/2023                      US-4178418 : TRR Process Template on Case Details Page
*    6.0            Prasuna Pattabhi                  06/14/2023                 US 4467570 : Launching and opening Newborn Notification Template (baby bot) for Medicaid (Lightning)
*/

@isTest
public class CaseProcessesLaunch_LT_Hum {
    
    /**
* Test Setup method to create all the data required for testing.
*
* @param     NULL
* @return    void 
*/
    @testSetup   static void setUpTestData()
    {
        User u = new User(
            ProfileId = [SELECT Id FROM Profile WHERE Name = 'Humana Pharmacy Specialist'].Id,
            LastName = 'last',
            Email = 'puser000@a.com',
            Username = 'puser000@a.com' + System.currentTimeMillis(),
            CompanyName = 'TEST',
            Title = 'title',
            Alias = 'alias',
            TimeZoneSidKey = 'America/Los_Angeles',
            EmailEncodingKey = 'UTF-8',
            LanguageLocaleKey = 'en_US',
            LocaleSidKey = 'en_US'
        );
        insert u;
        
        PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_1210_Commercial_Demographic_Update'];
        
        insert new PermissionSetAssignment(AssigneeId = u.id, PermissionSetId = ps.Id);       
        System.runAs(u){
             Work_Queue_Setup__c wqs = New Work_Queue_Setup__c(Name = 'Louisville Other Insurance Investigation');
        insert wqs;
            List<Account> accList = new List<Account>();            
            
            Account oMemAccount = HUMTestDataHelperTest.getAccount();
            oMemAccount.Group_Number__c = '987654321';
            oMemAccount.Enterprise_Id__c = '1000101';
            oMemAccount.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            oMemAccount.firstName = 'Daphiner';
            oMemAccount.lastName = 'Superman';
            oMemAccount.Veteran_Status__c ='confirmed-veteran';
            oMemAccount.VA_Health_Enrollee__c ='true';
            oMemAccount.Home_Office_Account__c = false;
            accList.add(oMemAccount);
            if(accList != NULL) insert accList;
            system.assert(accList.size() == 1);
            
            Product2 prd = new Product2();
            prd.Product_Type__c = 'MAPD';
            prd.Name = 'test product';
            insert prd;
            
            PurchaserPlan pp = new PurchaserPlan();
            pp.Product__c = prd.id;
            pp.Name = 'test plan';
            insert pp;
            
            MemberPlan mp = new MemberPlan();
            mp.Name = 'test member plan';
            mp.MemberId = oMemAccount.id;
            mp.PlanId = pp.id;
            insert mp;
            
            Case cs = new Case();
            cs.Status = 'In Progress';
            cs.AccountId = accList[0].Id;
            cs.Member_Plan_Id__c = mp.id;
            cs.Origin = 'Email';
            insert cs;
            
            Case cse = new Case();
            cse.Status = 'In Progress';
            cse.AccountId = accList[0].Id;
            cse.Member_Plan_Id__c = mp.id;
            cse.Origin = 'Web Chat';
            insert cse;
            
            Case csee = new Case();
            csee.Status = 'In Progress';
            csee.AccountId = accList[0].Id;
            csee.Member_Plan_Id__c = mp.id;
            csee.Origin = 'Web Chat';
            csee.Area_of_Focus__c = 'Career';
            csee.Case_Owner_Queue__c = wqs.id;
            csee.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert csee;
            
            Template__c tem = new Template__c();
            tem.Unique_Template_Name__c = '786888';
            tem.Description__c  = 'Medicare OI Form';
            insert tem;
            
            Template_Field__c tf = new Template_Field__c();
            tf.Name='BIN';
            tf.Template__c = tem.id;
            insert tf;
            
            Template_Submission_Owner__c tso = new Template_Submission_Owner__c();
            tso.Object_Owner_ID__c = cs.id;
            tso.Object_Owner_Type__c = 'Case';
            insert tso;
            
            Template_Submission__c ts = new Template_Submission__c();
            ts.Submission_Owner__c = tso.id;
            ts.Template__c = tem.id;
            insert ts;
            
            Template_Submission_Data__c tsd = new Template_Submission_Data__c();
            tsd.Template_Submission__c = ts.id;
            tsd.Template_Field__c = tf.id;
            tsd.Value__c = '12345';
            insert tsd;
            
            Template_Submission_Owner__c tso1 = new Template_Submission_Owner__c();
            tso1.Object_Owner_ID__c = cse.id;
            tso1.Object_Owner_Type__c = 'Case';
            insert tso1;
            
            Template_Submission__c ts1 = new Template_Submission__c();
            ts1.Submission_Owner__c = tso1.id;
            ts1.Template__c = tem.id;
            insert ts1;
            
            Template_Submission_Data__c tsd1 = new Template_Submission_Data__c();
            tsd1.Template_Submission__c = ts.id;
            tsd1.Template_Field__c = tf.id;
             insert tsd1;
             
             Case_Action__c objCaseAction = new Case_Action__c(Name='QAA',Action_version__c='1',Active__c=true);
            insert objCaseAction;
    
            Question__c objQues = new Question__c(Name = 'QS-001',Actions__c=objCaseAction.Id,Active__c=true,Question_Label__c='Complaint Details',Sequence_Number__c=1,Question_Type__c='Text');
            insert objQues;
            
            Case_Action_Association__c objCaseActAssociation = new Case_Action_Association__c(Case__c=cse.Id,Case_Action__c = objCaseAction.id);
            insert objCaseActAssociation;
            
            Case_Action_Response__c objResponse = new Case_Action_Response__c(Action__c=objCaseActAssociation .Id,Case_Question__c=objQues.Id,Process_Question_Response__c='Test');
            insert objResponse;
            
            Template__c webIssuestest = new Template__c();
            webIssuestest.Description__c  = 'Centerwell Pharmacy Web Issues';
            insert webIssuestest;
            
            CRMFunctionality_ONOFF_Switch__c switchSubtabClose = new CRMFunctionality_ONOFF_Switch__c();
            switchSubtabClose.Name = 'SubtabClose';
            switchSubtabClose.IsON__c = true;
            insert switchSubtabClose;
        }
    }
    
    
    /**
* This method is used to test template removal if data is entered in the oi flow and saved.
*
* @param     NULL
* @return    void 
*/
    static testMethod void removeTemplateSubmissionTest()
    { 
        User u = [SELECT Id FROM User where Email = 'puser000@a.com'];
        System.runAs(u){
            String caseId = [Select id from Case where origin = 'Email'].id;
            CaseProcessesLaunch_LC_Hum.WrapperResponse processResponse = new CaseProcessesLaunch_LC_Hum.WrapperResponse();
            processResponse = CaseProcessesLaunch_LC_Hum.removeTemplateSubmission(caseId,false);
            System.assertNotEquals(processResponse, null);
            processResponse = CaseProcessesLaunch_LC_Hum.removeTemplateSubmission(caseId,true);
        }
    }
    
    /**
* This method is used to test the template submission existence for a case.
*
* @param     NULL
* @return    void 
*/
    static testMethod void getTemplateSubmissionIdTest()
    {
        User u = [SELECT Id FROM User where Email = 'puser000@a.com' ];
        System.runAs(u){
            String caseId = [Select id from Case where origin = 'Email'].id;
            String templateSubId = CaseProcessesLaunch_LC_Hum.getTemplateSubmissionId(caseId);
            CaseProcessesLaunch_LC_Hum.getCaseStatus(caseId);
            System.assert(templateSubId != null);
        }
    }
    
    
    /**
* This method is used to test the appearance of oi flow on a case
*
* @param     NULL
* @return    void 
*/
    static testMethod void showOIMEdicareFormTest()
    {
        User u = [SELECT Id FROM User where Email = 'puser000@a.com'];
        System.runAs(u){
            String caseId = [Select id from Case where origin = 'Email'].id;
            Boolean showOI = CaseProcessesLaunch_LC_Hum.showOIMEdicareForm(caseId);
           System.assertEquals(showOI,true); 
        }
    }
    
     /**
* This method is used to show oi flow based on work queue setup
*
* @param     NULL
* @return    void 
*/
    static testMethod void showOIMEdicareFormTestWithWorkQueueSetup()
    {
        User u = [SELECT Id FROM User where Email = 'puser000@a.com'];
        System.runAs(u){
            String caseId = [Select id from Case where origin = 'Web Chat' and Area_of_Focus__c != 'Career'].id;
            Boolean showOI = CaseProcessesLaunch_LC_Hum.showOIMEdicareForm(caseId);
            System.assertEquals(showOI, true);
        }
    }
    
     /**
* This method is used to test template removal if data is not entered in the oi flow and saved.
*
* @param     NULL
* @return    void 
*/
    static testMethod void removeTemplateSubmissionTestWithNoValue()
    { 
        User u = [SELECT Id FROM User where Email = 'puser000@a.com'];
        System.runAs(u){
            String caseId = [Select id from Case where origin = 'Web Chat' and Area_of_Focus__c != 'Career'].id;
            CaseProcessesLaunch_LC_Hum.WrapperResponse processResponse = new CaseProcessesLaunch_LC_Hum.WrapperResponse();
            processResponse = CaseProcessesLaunch_LC_Hum.removeTemplateSubmission(caseId,false);
            System.assertNotEquals(processResponse, null);
        }
    }
    
      /**
* This method is used to show oi flow on member case
*
* @param     NULL
* @return    void 
*/
    static testMethod void showOIMEdicareFormTestWithMemberRecType()
    {
        User u = [SELECT Id FROM User where Email = 'puser000@a.com'];
        System.runAs(u){
            String caseId = [Select id from Case where Area_of_Focus__c = 'Career'].id;
            Boolean showOI = CaseProcessesLaunch_LC_Hum.showOIMEdicareForm(caseId);
            System.assertEquals(showOI, true);
            List<Template_Submission__c> lstTemSub =[Select Id from Template_Submission__c Limit 1];
            CaseProcessesLaunch_LC_Hum.WrapperResponse obj = CaseProcessesLaunch_LC_Hum.getdatafromTemplateSubmission('Medicare OI Form',lstTemSub [0].Id);
            System.assertNotEquals(obj , null );

            List<TemplateSetting__mdt> lstmeta = CaseProcessesLaunch_LC_Hum.getTemplateDetails();
            System.assertNotEquals(lstmeta, null);
        }
    }

/**
* This method Is used to check process attached Or Not
*
* @param     CaseId, Template Name
* @return    void 
*/
    static testMethod void testIsProcessAttached(){

        User usr = HUMTestDataHelperTest.createTestUser('CaseProcessUser','Customer Care Specialist');
        usr.Current_Queue__c ='Test Business Process and Testing';
        usr.Mentor_Sub_Function__c = 'Go365';
        insert usr;
        Group oGrp = New Group(Name ='Test Business Process and Testing Group', Type = 'Queue');
        insert oGrp;
         QueueSObject oNewQueue = New QueueSObject(QueueId = oGrp.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;
        system.runAs(usr){
            
            Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c(
            Name = 'Test Business Process and Testing', OwnerId = oGrp.Id, 
            Service_Center__c = 'Business',Department__c = 'Process and Testing');
            insert oWQS;

            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'Calls (RSO)';
            ctRec.Is_Active__c=true;
            insert ctRec;

            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Benefits';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
			System.assertEquals(classificationRec.Name == 'Benefits',true);
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            insert intentRec;

            CTCI_Junction__c ctciRec = New CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            insert ctciRec;

            RecordType AccountRecordType = [SELECT Id, Name, DeveloperName, SobjectType FROM RecordType
            WHERE SobjectType = 'Account' AND DeveloperName = 'Member']; 

            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = usr.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;

            Case caseRecord = New Case();
            caseRecord.Work_Queue_View_Name__c = 'Other';
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = accountRec.id;
            caseRecord.ownerId = usr.Id;
            caseRecord.Classification_Type__c = 'Calls (RSO)';
            caseRecord.Classification_id__c=classificationRec.id;
            caseRecord.Intent_id__c = intentRec.id;
            caseRecord.Interacting_With__c = accountRec.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Policy_Member__c = null;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Origin = 'Inbound Call';
            insert caseRecord;

            Template__c partD = New Template__c();
            partD.Unique_Template_Name__c = 'BC929CD6-E06A';
            partD.Description__c='partD';
            insert partD;

            Template_Submission_Owner__c tOnwer11 = New Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;
            
            classificationRec.Name = 'Rx';
            update classificationRec;
            intentRec.Name = 'Part D Redetermination';
            update intentRec;

            ctciRec.Classification__c = classificationRec.id;
            ctciRec.Intent__c =intentRec.id;
            update ctciRec;  

            Template_Field__c partField = New Template_Field__c();
            partField.Name = 'RxObtained2';
            partField.Template__c = partD.id;            
            insert partField;

            Template_Submission__c partSubmission = New Template_Submission__c();
            partSubmission.Template__c = partD.id;
            partSubmission.Submission_Owner__c = tOnwer11.id;
            insert partSubmission;
            
            Template_Submission_Data__c partDataObj = New Template_Submission_Data__c();
            partDataObj.Template_Field__c = partField.Id;
            partDataObj.Template_Submission__c = partSubmission.id;
            partDataObj.Value__c = 'Yes';
            insert partDataObj;

            CaseProcessesLaunch_LC_Hum.isProcessAttached(caseRecord.Id, 'Medicare Part D Redetermination');
            CaseProcessesLaunch_LC_Hum.isProcessAttached(caseRecord.Id, 'Medicaid PAR Provider Not Accepting');

            Case_Action__c a = New Case_Action__c();
            a.Name ='QAA';
            insert a;

            Case_Action_Association__c c = New Case_Action_Association__c();
            c.Case_Action__c = a.Id;
            c.Case__c = caseRecord.Id;
            insert c;
            CaseProcessesLaunch_LC_Hum.isProcessAttached(caseRecord.Id, 'QAA Complaint');
            
        }
    }
	/**
* This method Is used to check TRR process changes
* @param     NA
* @return    void 
*/
static testMethod void testTRRChanges(){

    User usr = HUMTestDataHelperTest.createTestUser('CaseProcessTRRUser','Customer Care Specialist');
    usr.Current_Queue__c ='Test Business Process and Testing';
    insert usr;
    Group oGrp = null;
    oGrp = New Group(Name ='Test Business Process and Testing Group', Type = 'Queue');
    insert oGrp;

    QueueSObject oNewQueue = New QueueSObject(QueueId = oGrp.Id, SobjectType = 'Work_Queue_Setup__c');
    insert oNewQueue;
    
    system.runAs(usr){
        
        Template__c objTemplate = new Template__c(Description__c='New Test TRR template', Unique_Template_Name__c='TRR_Template');
        insert objTemplate;
        System.assertNotEquals(objTemplate.Id, NULL, 'Value not matched');         
        
		Account oAccount = new Account(Name='TestAccount12');
        insert oAccount;
        system.assertNotEquals(oAccount.Id, NULL,'Value not matched');   


        Case oCase1 = new Case(AccountId=oAccount.Id, 
            Interacting_With__c=oAccount.Id, 
            Due_Date__c=Date.Today(), 
            Case_Owner__c=UserInfo.getUserName(), 
            Owner_Queue__c=UserInfo.getUserName(), 
            Work_Queue_View_Name__c='Cincinati CRU', 
            status='In Progress', 
            Origin='CRMTRR');
        insert oCase1;
        System.assertNotEquals(oCase1.Id, NULL, 'Value not matched');
		
		
        Template_Submission_Owner__c oTSO1 = new Template_Submission_Owner__c(Object_Owner_ID__c=oCase1.Id, Object_Owner_Type__c='Case');
        insert oTSO1;
        System.assertNotEquals(oTSO1.Id, NULL, 'Value not matched');   
		
        Template_Submission__c objTS1 = new Template_Submission__c(Template__c=objTemplate.Id, Submission_Owner__c=oTSO1.Id, Version__c=123);
        insert objTS1;
        System.assertNotEquals(objTS1.Id, NULL, 'Value not matched');

        Template_Submission__c objTS2 = new Template_Submission__c(Template__c=objTemplate.Id, Submission_Owner__c=oTSO1.Id, Version__c=124);
        insert objTS2;
        System.assertNotEquals(objTS2.Id, NULL, 'Value not matched');
			
        List<Template_Field__c> tmpFields = new List<Template_Field__c>();
        Template_Field__c tempField = new Template_Field__c(Name='Processing Date Stamp', Template__c=objTemplate.Id, Data_Type__c = 'Date');
        tmpFields.add(tempField);
        Template_Field__c tempField2 = new Template_Field__c(Name='Reply Code', Template__c=objTemplate.Id, Data_Type__c = 'String');
        tmpFields.add(tempField2);
        Template_Field__c tempField3 = new Template_Field__c(Name='Effective Date', Template__c=objTemplate.Id, Data_Type__c = 'Date');
        tmpFields.add(tempField3);        
        Template_Field__c tempField4 = new Template_Field__c(Name='Processing Time Stamp1', Template__c=objTemplate.Id, Data_Type__c = 'DateTime');
        tmpFields.add(tempField4);
        insert tmpFields;
        System.assertEquals(tmpFields.size(), 4, 'Value not matched');
        
		List<Template_Submission_Data__c> lstTemplSubmiss = new List<Template_Submission_Data__c>();
        Template_Submission_Data__c tempData1 = new Template_Submission_Data__c(Template_Field__c=tmpFields[0].Id, Template_Submission__c = objTS1.Id, Value__c = '2019-12-03');
        lstTemplSubmiss.add(tempData1);
        Template_Submission_Data__c tempData2 = new Template_Submission_Data__c(Template_Field__c=tmpFields[1].Id, Template_Submission__c = objTS1.Id, Value__c = '375');          
        lstTemplSubmiss.add(tempData2);
        Template_Submission_Data__c tempData3 = new Template_Submission_Data__c(Template_Field__c=tmpFields[2].Id, Template_Submission__c = objTS1.Id, Value__c = '2019-01-01');                    
        lstTemplSubmiss.add(tempData3);
        Template_Submission_Data__c tempData4 = new Template_Submission_Data__c(Template_Field__c=tmpFields[3].Id, Template_Submission__c = objTS1.Id, Value__c = '02:44:00.546789');
        lstTemplSubmiss.add(tempData4);
        insert lstTemplSubmiss;
        System.assertEquals(lstTemplSubmiss.size(), 4, 'Value not matched');
		
        List<Template_Submission_Data__c> lstTemplSubmiss1 = new List<Template_Submission_Data__c>();
        Template_Submission_Data__c tempData11 = new Template_Submission_Data__c(Template_Field__c=tmpFields[0].Id, Template_Submission__c = objTS2.Id, Value__c = '2019-12-04');
        lstTemplSubmiss1.add(tempData11);
        Template_Submission_Data__c tempData12 = new Template_Submission_Data__c(Template_Field__c=tmpFields[1].Id, Template_Submission__c = objTS2.Id, Value__c = '376');          
        lstTemplSubmiss1.add(tempData12);
        Template_Submission_Data__c tempData13 = new Template_Submission_Data__c(Template_Field__c=tmpFields[2].Id, Template_Submission__c = objTS2.Id, Value__c = '2019-01-02');                    
        lstTemplSubmiss1.add(tempData13);
        insert lstTemplSubmiss1;
        System.assertEquals(lstTemplSubmiss1.size(), 3, 'Value not matched');
		
		Test.startTest();
		
        Case oCase = [select ID, Status, Origin from Case where Origin = 'CRMTRR' limit 1];
        System.assert(oCase.Origin == 'CRMTRR' ); 
        Template_Submission__c oTempSub = [Select Id,Template__c, Name, Version__c from Template_Submission__c where Version__c = 123 limit 1 ];
        System.assert(oTempSub.Version__c == 123 ); 
        CaseProcessesLaunch_LC_Hum.hasTRRProcess(oCase.Id,false,3); 
		CaseProcessesLaunch_LC_Hum.hasTRRProcess(oCase.Id,true,3); 
        CaseProcessesLaunch_LC_Hum.hasTRRProcess(oCase.Id,false,1);
        CaseProcessesLaunch_LC_Hum.getTRRDataForProcessNumber(oTempSub.Name);
		
        Test.stopTest(); 
    }
}
/**
	* This method Is used to test the Medicaid Newborn Notification 
	*
	* @param     NULL
	* @return    void 
	*/
	static testMethod void testMedicaidNewbornNotification(){
		
        User custCareSpec = HUMTestDataHelperTest.createTestUser('Newborn','Customer Care Specialist');
        insert custCareSpec;
        system.assertEquals(custCareSpec.lastname=='test Newborn',true);      
       
    
        system.runAS(custCareSpec){
           
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = custCareSpec.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);		
            
            List<Classification_Type__c> ctList= new list<Classification_Type__c>();
           
            Classification_Type__c ctRec1 = new Classification_Type__c();
            ctRec1.name = 'Medicaid Calls';
            ctRec1.Is_Active__c=true;
            ctList.add(ctRec1);        
    
            insert ctList;
            system.assertEquals(ctList.size(),1);
            
            List<Classification__c> lstClassification = new List<Classification__c>();        
            Classification__c classificationRecDec1 = new Classification__c();
            classificationRecDec1.Name = 'G&A';
            classificationRecDec1.Is_Active__c = true;
            lstClassification.add(classificationRecDec1);
    
            Classification__c classificationRecDec2 = New Classification__c();
            classificationRecDec2.Name = 'Enrolment';
            classificationRecDec2.Is_Active__c = true;
            lstClassification.add(classificationRecDec2);
            
            insert lstClassification;
            system.assertEquals(lstClassification.size(),2);
           
            List<Intent__c> lstIntent = New List<Intent__c>();     
            
            Intent__c intentRecDec1 = New Intent__c();
            intentRecDec1.Name = 'Expedited';
            intentRecDec1.Is_Active__c = true;
            lstIntent.add(intentRecDec1);
            
            Intent__c intentRecDec2 = New Intent__c();
            intentRecDec2.Name = 'Newborn';
            intentRecDec2.Is_Active__c = true;
            lstIntent.add(intentRecDec2);
                  
            insert lstIntent;
            system.assertEquals(lstIntent.size(),2);
            
            List<CTCI_Junction__c> ctciList = New list<CTCI_Junction__c>();
    
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
            system.assertEquals(ctciList.size(),2);
       
            Case caseRecord1 = new Case();
            caseRecord1.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
            caseRecord1.Case_Owner__c = 'Louisville RSO Calls Support';
            caseRecord1.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord1.AccountId = accountRec.id;
            caseRecord1.ownerId = custCareSpec.Id;
            caseRecord1.Classification_Type__c = 'Medicaid Calls';
            caseRecord1.Classification_id__c=lstClassification[0].id;
            caseRecord1.Intent_id__c = lstIntent[0].id;
            caseRecord1.Interacting_With__c = accountRec.id;
            caseRecord1.Interacting_With_Type__c = 'Member';   
            caseRecord1.Interacting_About_Type__c = 'Member';
            caseRecord1.Origin = 'Inbound Call';
            caseRecord1.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert caseRecord1;
            system.assertEquals(caseRecord1.Case_Owner__c =='Louisville RSO Calls Support',true);
            


            if([SELECT ID FROM Template__c WHERE Unique_Template_Name__c='Medicaid_Newborn_Notification'].isEmpty()){
                Template__c templateObj = new Template__c(Unique_Template_Name__c='Medicaid_Newborn_Notification',Description__c='Medicaid Newborn Notification');
                Insert templateObj; 
                List<Template_Field__c> templateFieldObj = new List<Template_Field__c>();
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'MothersHumanaID'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'MothersMedicaidID'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'MothersFirstName'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'MothersLastName'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'MultipleBirth'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn1Name'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn1LastName'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn1DateofBirth'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn1DateofDeath'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn1Gender'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn1MedicaidID'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn2Name'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn2LastName'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn2DateofBirth'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn2DateofDeath'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn2Gender'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Newborn2MedicaidID'));
                
                insert templateFieldObj;
        
            }
    
        List<Template_Submission_Data__c> submissionList = new List<Template_Submission_Data__c>();
        Template__c templateObj = [Select id from Template__c where Unique_Template_Name__c='Medicaid_Newborn_Notification'];
        
        Template_Submission_Owner__c tOnwer = new Template_Submission_Owner__c();
        tOnwer.Object_Owner_ID__c=string.valueOf(caseRecord1.id);
        tOnwer.Object_Owner_Type__c='Case';
        insert tOnwer;
        
        Template_Submission__c submission = new Template_Submission__c();
        submission.Template__c = templateObj.id;
        submission.Submission_Owner__c = tOnwer.id;
        insert submission;        
        
        List<Template_Submission_Data__c> newSubmissionData = new List<Template_Submission_Data__c>();

        for(Template_Field__c submissionDataObj : [SELECT id,Name from Template_Field__c]){
            Template_Submission_Data__c tObj = new Template_Submission_Data__c();
            tObj.Template_Field__c = submissionDataObj.Id;
            tObj.Template_Submission__c = submission.id;    
                             
            if(submissionDataObj.Name == 'MothersFirstName'){
                tObj.Value__c = 'Mother FN' ;
            }
            if(submissionDataObj.Name == 'MothersLastName'){
                tObj.Value__c = 'Mother LN';      
            } 
            if(submissionDataObj.Name == 'MothersHumanaID'){
                tObj.Value__c = '123456';
            }
            if(submissionDataObj.Name == 'MothersMedicaidID'){
                tObj.Value__c = '345678' ;
            }
            if(submissionDataObj.Name == 'MultipleBirth'){
                tObj.Value__c = 'Twins' ;
            }
            if(submissionDataObj.Name == 'Newborn1Name'){
                tObj.Value__c = 'Newborn1 FN' ;
            }
            if(submissionDataObj.Name == 'Newborn1LastName'){
                tObj.Value__c = 'Newborn1 LN' ;
            }
            if(submissionDataObj.Name == 'Newborn1DateofBirth'){
                dateTime strCreatedDateTime = datetime.now();
                tObj.Value__c = (String) strCreatedDateTime.format('MM/dd/yyyy'); 
            }

            if(submissionDataObj.Name == 'Newborn1DateofDeath'){
                dateTime strCreatedDateTime = datetime.now();
                tObj.Value__c = (String) strCreatedDateTime.format('MM/dd/yyyy'); 
            }

            if(submissionDataObj.Name == 'Newborn1Gender'){
                tObj.Value__c = 'Female' ;
            }

            if(submissionDataObj.Name == 'Newborn1MedicaidID'){
                tObj.Value__c = 'NB12345' ;
            }
            if(submissionDataObj.Name == 'Newborn2Name'){
                tObj.Value__c = 'Newborn2 FN' ;
            }
            if(submissionDataObj.Name == 'Newborn2LastName'){
                tObj.Value__c = 'Newborn2 LN' ;
            }
            if(submissionDataObj.Name == 'Newborn2DateofBirth'){
                dateTime strCreatedDateTime = datetime.now();
                tObj.Value__c = (String) strCreatedDateTime.format('MM/dd/yyyy'); 
            }

            if(submissionDataObj.Name == 'Newborn2DateofDeath'){
                dateTime strCreatedDateTime = datetime.now();
                tObj.Value__c = (String) strCreatedDateTime.format('MM/dd/yyyy'); 
            }

            if(submissionDataObj.Name == 'Newborn2Gender'){
                tObj.Value__c = 'Male' ;
            }

            if(submissionDataObj.Name == 'Newborn2MedicaidID'){
                tObj.Value__c = 'NB22345' ;
            }                         
            
            newSubmissionData.add(tObj);
        }
         insert newSubmissionData;  
            
         Test.startTest();
         CaseProcessesLaunch_LC_Hum.isNewbornProcessAttached(caseRecord1.Id);
         CaseProcessesLaunch_LC_Hum.getNewbornTemplateDetails();
         Test.stopTest();     
    
        }
    }
}