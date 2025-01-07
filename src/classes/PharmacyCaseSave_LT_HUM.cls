/*******************************************************************************************************************************
Apex Class Name : PharmacyCaseSave_LT_HUM
Version         : 1.0
Created On      : 04/11/2018 
Function        : Test Class for PharmacyCaseSave_LC_HUM

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                                        12/14/2021                  Original Version
* Ritik Agarwal                                        02/23/2022                  fix test cases for UAT
* Prasuna Pattabhi                                 09/28/2022               US 3707812 - Create Medicare Case
* Prasuna Pattabhi                                 10/28/2022               Medicare Calls Case Comments build fix
* Jasmeen Shangari                                 12/05/2022               US 4035121-Milestone write for SubType change
* Prasuna Pattabhi                                 12/06/2022               US_4016910,US_4020206,US_4020208,US_4020207
* Pooja Kumbhar									   12/22/2022			    US 3863264 - T1PRJ0170850 - Lightning - RCC/Medicare (MET) Enrollment sections Case Edit Page
* Pooja Kumbhar									   02/16/2023				T1PRJ0170850 - Lightning- Case Management- G & A Complaints Routing Validations
* Sagar.G     		                                02/20/2023				US-4177643 Add claims Buttons EDIT CASE  
* Gowthami Thota                                    07/31/2023              Test Class Fix for UAT 
********************************************************************************************************************************/

@isTest
public with Sharing class PharmacyCaseSave_LT_HUM {
    /*
* Method Name : testsaveCase
* Description : used to test saveCaseData method
* Return type : void
* Parameter   : NA
*/
    static @isTest void testsaveCase()
    {
        try{    
        Account oMemAccount = new Account();
            oMemAccount.Group_Number__c = '987654321';
            oMemAccount.Enterprise_Id__c = '1000101';
            oMemAccount.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            oMemAccount.firstName = 'Daphiner';
            oMemAccount.lastName = 'Superman';
            
        User oCCSUser = HUMTestDataHelperTest.createTestUser('UserCCS1','Humana Pharmacy Specialist');
        insert oCCSUser;
        PermissionSet ps  = [Select id from PermissionSet where name='CRMS_111_StridesAccess'];
        PermissionSetLicense psL = [SELECT id FROM PermissionSetLicense WHERE MasterLabel = 'Health Cloud Platform'];
        insert new PermissionSetLicenseAssign(AssigneeId=oCCSUser.Id, PermissionSetLicenseId=psL.Id);
        insert new PermissionSetAssignment(AssigneeId=oCCSUser.Id, PermissionSetId=ps.Id);
        System.runAs(oCCSUser){
            insert oMemAccount;
            String recTypeId = [select id from recordtype where name='HP Member Case' LIMIT 1]?.id;
            String caseJson = '{"Origin":"Inbound Call","Owner_Queue__c":"Humana Pharmacy Clinical Services","Type":"Customer Inquiry","Case_Owner__c":"Juan Acevedo Jr","Subtype__c":null,"Status":"In Progress","Interacting_With_Type__c":null,"Interacting_About_Type__c":"Member","Interacting_With__c":"","Interacting_With_Name__c":"","Member_Plan_Id__c":"","Priority":"Normal","OpenEnrollment__c":false,"Topic__c":null,"OpenEnrollmentType__c":null,"G_A_Rights_Given__c":null,"Complaint__c":null,"G_A_Reason__c":null,"Complaint_Reason__c":null,"Complaint_Type__c":null,"Language_Preference__c":null,"Classification_Type__c":"HP Clinical Services","RecordTypeId":"'+recTypeId+'","AccountId":"'+oMemAccount.id+'"}';
            String caseCommentJson = '{"CommentBody":"Test caseComment and case save functions"}';
            String caseId = PharmacyCaseSave_LC_HUM.saveCase(caseJson, caseCommentJson);
            List<case> lstCase = [Select id,origin,Owner_Queue__c,Type,Case_Owner__c,Interacting_About_Type__c,Status,G_A_Reason__c,Complaint_Reason__c,
                                  Complaint_Type__c,Language_Preference__c,Classification_Type__c,OpenEnrollment__c from case where id=:caseId];
            
            System.assertEquals(true, caseId!=null);
            System.assertEquals('Inbound Call', lstCase[0].origin);
            System.assertEquals('Humana Pharmacy Clinical Services', lstCase[0].Owner_Queue__c);
            System.assertEquals('In Progress', lstCase[0].Status);
            System.assertEquals(false, lstCase[0].OpenEnrollment__c);
            System.assertEquals('Member', lstCase[0].Interacting_About_Type__c);
            System.assertEquals('Customer Inquiry', lstCase[0].Type);
            System.assertEquals(Null, lstCase[0].G_A_Reason__c);
            System.assertEquals(Null, lstCase[0].Complaint_Reason__c);
            System.assertEquals(Null, lstCase[0].Complaint_Type__c);
            System.assertEquals(Null, lstCase[0].Language_Preference__c);
            System.assertEquals('HP Clinical Services', lstCase[0].Classification_Type__c);
        }
        }catch(Exception e){
            
        }
    }
    
    /*
* Method Name : testsaveCaseComment
* Description : used to test caseComment saving 
* Return type : void
* Parameter   : NA
*/
    static @isTest void testsaveCaseComment()
    {
        try{
         Account oMemAccount = new Account();
            oMemAccount.Group_Number__c = '987654321';
            oMemAccount.Enterprise_Id__c = '1000101';
            oMemAccount.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            oMemAccount.firstName = 'Daphiner';
            oMemAccount.lastName = 'Superman';
        User oCCSUser = HUMTestDataHelperTest.createTestUser('UserCCS1','Humana Pharmacy Specialist');
        insert oCCSUser;
        PermissionSet ps  = [Select id from PermissionSet where name='CRMS_111_StridesAccess'];
        PermissionSetLicense psL = [SELECT id FROM PermissionSetLicense WHERE MasterLabel = 'Health Cloud Platform'];
        insert new PermissionSetLicenseAssign(AssigneeId=oCCSUser.Id, PermissionSetLicenseId=psL.Id);
        insert new PermissionSetAssignment(AssigneeId=oCCSUser.Id, PermissionSetId=ps.Id);
        System.runAs(oCCSUser){
            insert oMemAccount;
            String recTypeId = [select id from recordtype where name='HP Member Case' LIMIT 1]?.id;
            String caseJson = '{"Origin":"Inbound Call","Owner_Queue__c":"Humana Pharmacy Clinical Services","Type":"Customer Inquiry","Case_Owner__c":"Juan Acevedo Jr","Subtype__c":null,"Status":"In Progress","Interacting_With_Type__c":null,"Interacting_About_Type__c":"Member","Interacting_With__c":"","Interacting_With_Name__c":"","Member_Plan_Id__c":"","Priority":"Normal","OpenEnrollment__c":false,"Topic__c":null,"OpenEnrollmentType__c":null,"G_A_Rights_Given__c":null,"Complaint__c":null,"G_A_Reason__c":null,"Complaint_Reason__c":null,"Complaint_Type__c":null,"Language_Preference__c":null,"Classification_Type__c":"HP Clinical Services","RecordTypeId":"'+recTypeId+'","AccountId":"'+oMemAccount.id+'"}';
            String caseCommentJson = '{"CommentBody":"Test caseComment and case save functions"}';
            String caseId = PharmacyCaseSave_LC_HUM.saveCase(caseJson, caseCommentJson);
            List<CaseComment> lstCaseComment = [Select Id,CommentBody,ParentId,Parent.Origin from CaseComment where parentId=:caseId];
            
            System.assertEquals(true, caseId!=null);
            System.assertEquals(caseId, lstCaseComment[0].ParentId);
            System.assertEquals('Test caseComment and case save functions', lstCaseComment[0].CommentBody);
            System.assertEquals('Inbound Call', lstCaseComment[0].Parent.Origin);  
        }
        }catch(Exception e){
            
        }
    }
    
    /*
* Method Name : testfailsave
* Description : used to get DML while save case
* Return type : void
* Parameter   : NA
*/
    static @isTest void testfailsave()
    {
        try{
            String recTypeId = [select id from recordtype where name='HP Member Case' LIMIT 1]?.id;
            String caseJson = '{"Id":"ritik","Origin":"Inbound Call","Owner_Queue__c":"Humana Pharmacy Clinical Services","Type":"Customer Inquiry","Case_Owner__c":"Juan Acevedo Jr","Subtype__c":null,"Interacting_With_Type__c":null,"Interacting_About_Type__c":"Member","Interacting_With__c":"","Interacting_With_Name__c":"","Member_Plan_Id__c":"","Priority":"Normal","OpenEnrollment__c":false,"Topic__c":null,"OpenEnrollmentType__c":null,"G_A_Rights_Given__c":null,"Complaint__c":null,"G_A_Reason__c":null,"Complaint_Reason__c":null,"Complaint_Type__c":null,"Language_Preference__c":null,"Classification_Type__c":"HP Clinical Services","RecordTypeId":"'+recTypeId+'","AccountId":"0012h00000f9VJEAA2"}';
            String caseCommentJson = '{"CommentBody":"Test caseComment and case save functions"}';
            String caseId = PharmacyCaseSave_LC_HUM.saveCase(caseJson, caseCommentJson);
        }catch(Exception ex){
        }
    }
    
    /*
* Method Name : testFalsesaveCase
* Description : used to test negative scenario to test save case
* Return type : void
* Parameter   : NA
*/
    static @isTest void testFalsesaveCase()
    {
        try{
            String caseId = PharmacyCaseSave_LC_HUM.saveCase('', '');
        }catch(Exception ex){
            System.assertEquals(true, ex.getMessage().contains('No content to map to Object due to end of input'));
        }
    }
/**
* This method Is used to test the Medicare Case Record scenario 
*
* @param     NULL
* @return    void 
*/
    Static testMethod void testMedicareCaseRelatedChanges(){      
        try{
        Group oNewGroup = null;
        oNewGroup = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oNewGroup;

        QueueSObject oNewQueue = New QueueSObject(QueueId = oNewGroup.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue; 

        User custCareSpecialist = HUMTestDataHelperTest.createTestUser('userMediareCase','Customer Care Specialist');
        insert custCareSpecialist;

        custCareSpecialist.Current_Queue__c = 'Test Business Process and Testing';
        update custCareSpecialist; 

        PermissionSet ps  = [Select id from PermissionSet where name='CRMS_111_StridesAccess'];
        PermissionSetLicense psL = [SELECT id FROM PermissionSetLicense WHERE MasterLabel = 'Health Cloud Platform'];

        insert new PermissionSetLicenseAssign(AssigneeId=custCareSpecialist.Id, PermissionSetLicenseId=psL.Id);
        insert new PermissionSetAssignment(AssigneeId=custCareSpecialist.Id, PermissionSetId=ps.Id);

        system.runAS(custCareSpecialist){
            
            Work_Queue_Setup__c qsWorkQueue = New Work_Queue_Setup__c(
            Name = 'Test Business Process and Testing', OwnerId = oNewGroup.Id, 
            Service_Center__c = 'Business',Department__c = 'Process and Testing');
            insert qsWorkQueue;           

            Classification_Type__c callsClassificationType = New Classification_Type__c(Name = 'Calls',Is_Active__c = true);  
            insert callsClassificationType;

            qsWorkQueue.Classification_Type__c = callsClassificationType.id;
            update qsWorkQueue;

            Classification__c billing = New Classification__c(Name = 'Billing',Is_Active__c = true);
            insert billing;

            Intent__c makePayment = New Intent__c(Name = 'Make Payment',Is_Active__c = true);                       
            insert makePayment;

            CTCI_Junction__c ctciValid = New CTCI_Junction__c(Classification_Type__c = callsClassificationType.id,
            Classification__c = billing.Id, Intent__c = makePayment.id, IsActive__c = True);
            insert ctciValid;

            RecordType memberAccountType = [SELECT Id, Name, DeveloperName, SobjectType FROM RecordType
                WHERE SobjectType = 'Account' AND DeveloperName = 'Member']; 

            Account memberAccountValid = New Account(
                FirstName = 'medicareCase', LastName = 'member',RecordTypeId = memberAccountType.Id,
                CoKY_Account__c = False, Tenant_Id__c = '00',General_Account__c = true, NPI_ID__c = '182245',
                Humana_One_Indicator__c = False, Home_Office_Account__c = False, ETL_Record_Deleted__c = False
            );
            insert memberAccountValid;

            Product2 productRec = New Product2(
                Name = 'MED',ProductCode = 'MED',Description = '302 006',
                Alternate_Description__c = 'UNKNOWN', HDHP_Indicator__c = 'N', 
                Major_LOB__c = 'MER', Product_Type__c = 'MAPD',Segment_Type__c = 'MCR', 
                Sold_Product_Key_Value__c = '9881770101', Tenant_ID__c = '00'
            );
            insert productRec;

            PurchaserPlan policyObj = New PurchaserPlan();
            policyObj.Policy_Group_Number__c='11111'; 
            policyObj.Name = 'Policy New'; 
            policyObj.source_cust_cov_key__c = '12345678901234567890' ; 
            policyObj.PayerId = memberAccountValid.Id;
            policyObj.Major_LOB__c ='MER';
            policyObj.Platform__c = 'EM';            
            policyObj.ETL_Record_Deleted__c = false;
            policyObj.product__c = productRec.Id;
            insert policyObj;

            MemberPlan memPlan = New MemberPlan(Name ='memplanQs',MemberId=memberAccountValid.id,
            Member_Dependent_Code__c = 'H1234567890',Member_Id_base__c = 'test23456',GroupNumber = '672534',
            EffectiveFrom = Date.newInstance(2020, 1, 1), EffectiveTo = Date.newInstance(1999, 12, 31),
            PlanId = policyObj.Id, ETL_Record_Deleted__c = False);
            insert memPlan; 
	    
            MemberPlan memPlan1 = New MemberPlan(Name ='memplanQs',MemberId=memberAccountValid.id,
            Member_Dependent_Code__c = 'H2345678910',Member_Id_base__c = 'test123456',GroupNumber = '672534',
            EffectiveFrom = Date.newInstance(2020, 1, 1), EffectiveTo = Date.newInstance(1999, 12, 31),
            PlanId = policyObj.Id, ETL_Record_Deleted__c = False);
            insert memPlan1;
        
            Case caseRecordInp = New Case(Status = 'In Progress',AccountId = memberAccountValid.id,
            ctci_list__c = ctciValid.id, Complaint__c = 'Yes - Medicare Part C',Complaint_Reason__c = 'Other',
            Complaint_Type__c = 'Behavioral',DCN__c='12345678');
            insert caseRecordInp;

            HUMCaseInteractionAboutType__c InteractingAboutType = new HUMCaseInteractionAboutType__c();
            InteractingAboutType.Name = 'Member';
            InteractingAboutType.CaseRecordType__c = 'Member';
            InteractingAboutType.CaseInteraction_About_Type__c = 'Member';
            insert InteractingAboutType;

            MEDENROLLMENT_CASE_SUBTYPE_HUM__c meden = new MEDENROLLMENT_CASE_SUBTYPE_HUM__c();
            meden.Name = 'Creditable Coverage';
            meden.SubType__c = 'Creditable Coverage';
            insert meden;

            Id CseRecrdTypeId = Schema.SobjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            
            PharmacyCaseSave_LC_HUM.updateCaseRecordType(memberAccountValid.id ,'Correspondence Inquiry','Creditable Coverage',caseRecordInp.id,'Customer Care Specialist',false,CseRecrdTypeId,'Member');
            PharmacyCaseSave_LC_HUM.updateCaseRecordType(memberAccountValid.id ,'Correspondence Inquiry','Creditable Coverage',caseRecordInp.id,'Customer Care Specialist',true,CseRecrdTypeId,'Member');            
            
            RecordType unknownMemberAccountType = [SELECT Id, Name, DeveloperName, SobjectType FROM RecordType
                WHERE SobjectType = 'Account' AND DeveloperName = 'Unknown_Member'];

            Account unknownMemberAccountValid = New Account(
                FirstName = 'medicareCase', LastName = 'unknownmember',RecordTypeId = unknownMemberAccountType.Id,
                CoKY_Account__c = False, Tenant_Id__c = '00',General_Account__c = true, NPI_ID__c = '182245',
                Humana_One_Indicator__c = False, Home_Office_Account__c = False, ETL_Record_Deleted__c = False
            );
            insert unknownMemberAccountValid;

            HUMCaseInteractionAboutType__c InteractingAboutType1 = new HUMCaseInteractionAboutType__c();
            InteractingAboutType1.Name = 'Unknown Member';
            InteractingAboutType1.CaseRecordType__c = 'Unknown Member';
            InteractingAboutType1.CaseInteraction_About_Type__c = 'Unknown-Member';
            insert InteractingAboutType1;

            PharmacyCaseSave_LC_HUM.updateCaseRecordType(unknownMemberAccountValid.id ,'Correspondence Inquiry','Creditable Coverage',caseRecordInp.id,'Customer Care Specialist',true,CseRecrdTypeId,'Member');
			String recTypeId = [select id from recordtype where name='HP Member Case' LIMIT 1]?.id;
            String caseJson = '{"Origin":"Inbound Call","Owner_Queue__c":"Humana Pharmacy Clinical Services","Type":"Customer Inquiry","Case_Owner__c":"Juan Acevedo Jr","Subtype__c":null,"Status":"In Progress","Interacting_With_Type__c":"Member","Interacting_About_Type__c":"Member","Interacting_With__c":"'+memberAccountValid.id+'","Interacting_With_Name__c":"","Member_Plan_Id__c":"'+memplan.Id+'","Priority":"Normal","OpenEnrollment__c":false,"Topic__c":null,"OpenEnrollmentType__c":null,"G_A_Rights_Given__c":null,"Complaint__c":null,"G_A_Reason__c":null,"Complaint_Reason__c":null,"Complaint_Type__c":null,"Language_Preference__c":null,"Classification_Type__c":"HP Clinical Services","RecordTypeId":"'+recTypeId+'","AccountId":"'+memberAccountValid.id+'"}';
            String caseCommentJson = '{"CommentBody":"Test caseComment and case save functions"}';
            String caseId = PharmacyCaseSave_LC_HUM.saveCaseDetails(caseJson, caseCommentJson,null,'Humana Pharmacy Specialist',false,false,false,null,null,true,'PrevSubType','','987979');
            String caseJson1 = '{"Origin":"Inbound Call","Owner_Queue__c":"Humana Pharmacy Clinical Services","Type":"Customer Inquiry","Case_Owner__c":"Juan Acevedo Jr","Subtype__c":null,"Status":"In Progress","Interacting_With_Type__c":"Member","Interacting_About_Type__c":"Member","Interacting_With__c":"'+memberAccountValid.id+'","Interacting_With_Name__c":"","Member_Plan_Id__c":"'+memplan1.Id+'","Priority":"Normal","OpenEnrollment__c":false,"Topic__c":null,"OpenEnrollmentType__c":null,"G_A_Rights_Given__c":null,"Complaint__c":null,"G_A_Reason__c":null,"Complaint_Reason__c":null,"Complaint_Type__c":null,"Language_Preference__c":null,"Classification_Type__c":"HP Clinical Services","RecordTypeId":"'+recTypeId+'","AccountId":"'+memberAccountValid.id+'"}';
            String caseId1 = PharmacyCaseSave_LC_HUM.saveCaseDetails(caseJson1, caseCommentJson,caseRecordInp.id,'Humana Pharmacy Specialist',false,false,false,null,null,true,'PrevSubType','','876876876');        
			
        }
        }catch(Exception e)
        {
            
        }
    }
    /*
* Method Name : testsaveCaseDetails
* Description : used to test saveCaseData method
* Return type : void
* Parameter   : NA
*/
static @isTest void testsaveCaseDetails(){
    try{
        Account oMemAccount = new Account();
        oMemAccount.Group_Number__c = '987654321';
        oMemAccount.Enterprise_Id__c = '1000101';
        oMemAccount.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        oMemAccount.firstName = 'Daphiner';
        oMemAccount.lastName = 'Superman';        
    User oCCSUser = HUMTestDataHelperTest.createTestUser('UserCCS1','Humana Pharmacy Specialist');
    insert oCCSUser;
    PermissionSet ps  = [Select id from PermissionSet where name='CRMS_111_StridesAccess'];
    PermissionSetLicense psL = [SELECT id FROM PermissionSetLicense WHERE MasterLabel = 'Health Cloud Platform'];
    insert new PermissionSetLicenseAssign(AssigneeId=oCCSUser.Id, PermissionSetLicenseId=psL.Id);
    insert new PermissionSetAssignment(AssigneeId=oCCSUser.Id, PermissionSetId=ps.Id);
        System.runAs(oCCSUser){
            insert oMemAccount;
            HUMCaseInteractionAboutType__c InteractingAboutType = new HUMCaseInteractionAboutType__c();
            InteractingAboutType.Name = 'Member';
            InteractingAboutType.CaseRecordType__c = 'Member';
            InteractingAboutType.CaseInteraction_About_Type__c = 'Member';
            insert InteractingAboutType;
            
            String recTypeId = [select id from recordtype where name='HP Member Case' LIMIT 1]?.id;
            String caseJson = '{"Origin":"Inbound Call","Owner_Queue__c":"Humana Pharmacy Clinical Services","Type":"Customer Inquiry","Case_Owner__c":"Juan Acevedo Jr","Subtype__c":null,"Status":"In Progress","Interacting_With_Type__c":"Member","Interacting_About_Type__c":"Member","Interacting_With__c":"'+oMemAccount.id+'","Interacting_With_Name__c":"","Priority":"Normal","OpenEnrollment__c":false,"Topic__c":null,"OpenEnrollmentType__c":null,"G_A_Rights_Given__c":null,"Complaint__c":null,"G_A_Reason__c":null,"Complaint_Reason__c":null,"Complaint_Type__c":null,"Language_Preference__c":null,"Classification_Type__c":"HP Clinical Services","RecordTypeId":"'+recTypeId+'","AccountId":"'+oMemAccount.id+'"}';
            String caseCommentJson = '{"CommentBody":"Test caseComment and case save functions"}';
            String caseId = PharmacyCaseSave_LC_HUM.saveCaseDetails(caseJson, caseCommentJson,null,'Humana Pharmacy Specialist',false,false,false,null,null,true,'PrevSubType','','98798798');
            String caseId1 = PharmacyCaseSave_LC_HUM.saveCaseDetails(caseJson, caseCommentJson,caseId,'Humana Pharmacy Specialist',true,false,false,null,null,false,'','','');        
			
            String caseJson1 = '{"Origin":"Inbound Call","Owner_Queue__c":"Humana Pharmacy Clinical Services","Type":"Customer Inquiry","Case_Owner__c":"Juan Acevedo Jr","Subtype__c":"Creditable Coverage","Status":"In Progress","Interacting_With_Type__c":"Member","Interacting_About_Type__c":"Member","Interacting_With__c":"'+oMemAccount.id+'","Interacting_With_Name__c":"","Priority":"Normal","OpenEnrollment__c":false,"Topic__c":null,"OpenEnrollmentType__c":null,"G_A_Rights_Given__c":null,"Complaint__c":null,"G_A_Reason__c":null,"Complaint_Reason__c":null,"Complaint_Type__c":null,"Language_Preference__c":null,"Classification_Type__c":"HP Clinical Services","RecordTypeId":"'+recTypeId+'","AccountId":"'+oMemAccount.id+'"}';
            String caseId2 = PharmacyCaseSave_LC_HUM.saveCaseDetails(caseJson, caseCommentJson,caseId,'Humana Pharmacy Specialist',true,false,false,null,null,true,'PrevSubType','','76876876876');        
			
        }
    }catch(Exception e){
        
    }
    }
            /**
    * This method Is used to test Is Creditable Coverage Changes
    *
    * @param     NULL
    * @return    void 
    */
testMethod static void isCreditableCoverageTest(){
    try{
        User testUser = HUMTestDataHelperTest.createTestUser('CaseEditUser1','Customer Care Specialist');
        insert testUser;
        system.runAs(testUser) {
            
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];

            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = testUser.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;

            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Benefits';
            classificationRec.Is_Active__c = true;
            insert classificationRec;

            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            insert intentRec;

            Case caseRecord = new Case();
            caseRecord.Status = 'Pending - Response';        
            caseRecord.Case_Owner__c = 'Louisville RSO Calls Support';
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.AccountId = accountRec.id;
            caseRecord.ownerId = testUser.Id;
            caseRecord.Subtype__c = 'Creditable Coverage';
            caseRecord.Guided_Process_Flag__c = true;
            caseRecord.Classification_Type__c = 'Calls (RSO)';
            caseRecord.Classification_id__c=classificationRec.id;
            caseRecord.Intent_id__c = intentRec.id;
            caseRecord.Interacting_With__c = accountRec.id;
            caseRecord.Interacting_With_Type__c = 'Member';              
            caseRecord.Interacting_About_Type__c = 'Member';              
            caseRecord.Origin = 'Inbound Call';
            caseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert caseRecord;
            PharmacyCaseSave_LC_HUM.calEscalationIndDate(caseRecord);
            Template__c templateObj = new Template__c(Unique_Template_Name__c='ATVF007',Description__c='Creditable Coverage Attestation');
            Insert templateObj;  
            Template_Field__c templateFieldObj = new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FinalDisclaimer');
            insert templateFieldObj;
            Template_Submission_Owner__c tOnwer = new Template_Submission_Owner__c();
            tOnwer.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer.Object_Owner_Type__c='Case';
            insert tOnwer;            
            Template_Submission__c submission = new Template_Submission__c();
            submission.Template__c = templateObj.id;
            submission.Submission_Owner__c = tOnwer.id;
            insert submission;        
            Template_Submission_Data__c tObj = new Template_Submission_Data__c();
            tObj.Template_Field__c = templateFieldObj.Id;
            tObj.Template_Submission__c = submission.id;
            tObj.Value__c = 'Yes';
            insert tObj;
            PharmacyCaseSave_LC_HUM.isCreditableCoverageSubtypeElligible(caseRecord.Id);
            PharmacyCaseSave_LC_HUM.calEscalationIndDate(caseRecord);	
            caseRecord.Escalation_Indicator__c=CONSTANTS_CASE_HUM.ImmediateLeader;	
            update caseRecord;	
            PharmacyCaseSave_LC_HUM.calEscalationIndDate(caseRecord);	
            caseRecord.Escalation_Indicator__c=CONSTANTS_CASE_HUM.SecondaryLeader;	
            PharmacyCaseSave_LC_HUM.calEscalationIndDate(caseRecord);	
            caseRecord.id=null;	
            PharmacyCaseSave_LC_HUM.calEscalationIndDate(caseRecord);	
            PharmacyCaseSave_LC_HUM.getRSOHPFlag(CONSTANTS_CASE_HUM.ClosedHPAgent);
        }
    }catch(exception e)
    {}
    }
	
	/**
    * This method Is used to test MET task creation and Milestone writting changes
    *
    * @param     NULL
    * @return    void 
    */
   testMethod static void testMETMilestoneSaveCaseDetails(){
       try{
        User testUser = HUMTestDataHelperTest.createTestUser('CaseEditUser1','Customer Care Specialist');
        insert testUser;
        system.runAs(testUser) {
            
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];

            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = testUser.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;

            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Benefits';
            classificationRec.Is_Active__c = true;
            insert classificationRec;

            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            insert intentRec;

            Case caseRecord = new Case();
            caseRecord.Status = 'Pending - Response';        
            caseRecord.Case_Owner__c = 'Louisville RSO Calls Support';
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.AccountId = accountRec.id;
            caseRecord.ownerId = testUser.Id;
            caseRecord.Subtype__c = 'Creditable Coverage';
            caseRecord.Guided_Process_Flag__c = true;
            caseRecord.Classification_Type__c = 'Calls (RSO)';
            caseRecord.Classification_id__c=classificationRec.id;
            caseRecord.Intent_id__c = intentRec.id;
            caseRecord.Interacting_With__c = accountRec.id;
            caseRecord.Interacting_With_Type__c = 'Member';   
            caseRecord.Interacting_About_Type__c = 'Member';                         
            caseRecord.Origin = 'Inbound Call';
            caseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert caseRecord;

            PharmacyCaseSave_LC_HUM.calEscalationIndDate(caseRecord);	
            caseRecord.Escalation_Indicator__c= CONSTANTS_CASE_HUM.ImmediateLeader;	
            update caseRecord;	
            PharmacyCaseSave_LC_HUM.calEscalationIndDate(caseRecord);	
            caseRecord.Escalation_Indicator__c=CONSTANTS_CASE_HUM.SecondaryLeader;
            PharmacyCaseSave_LC_HUM.calEscalationIndDate(caseRecord);	
            caseRecord.id=null;	
            PharmacyCaseSave_LC_HUM.calEscalationIndDate(caseRecord);	
            PharmacyCaseSave_LC_HUM.getRSOHPFlag(CONSTANTS_CASE_HUM.ClosedHPAgent);
            
            MET_Task__c oTask = new MET_Task__c();
            oTask.Name = 'Test Task';
            oTask.Is_Active__c = true;
            oTask.Case_Subtype__c = 'Creditable Coverage';
            insert oTask;
            System.assertEquals(oTask.Name,'Test Task');
            
            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = 'Test Action';
            oAction.Is_Active__c = true;
            insert oAction;
            System.assertEquals(oAction.Name,'Test Action');

            MET_Source__c oSource = new MET_Source__c();
            oSource.Name = 'Test Source';
            oSource.Is_Active__c = true; 
            insert oSource; 
            
            System.assertEquals(oSource.Name,'Test Source');
            
            String recTypeId = [select id from recordtype where name='HP Member Case' LIMIT 1]?.id;
            String caseJson = '{"Origin":"Inbound Call","Owner_Queue__c":"Humana Pharmacy Clinical Services","Type":"Customer Inquiry","Case_Owner__c":"Juan Acevedo Jr","Subtype__c":"Creditable Coverage","Status":"In Progress","Interacting_With_Type__c":"Member","Interacting_About_Type__c":"Member","Interacting_With__c":"","Interacting_With_Name__c":"'+accountRec.id+'","Priority":"Normal","OpenEnrollment__c":false,"Topic__c":null,"OpenEnrollmentType__c":null,"G_A_Rights_Given__c":null,"Complaint__c":null,"G_A_Reason__c":null,"Complaint_Reason__c":null,"Complaint_Type__c":null,"Language_Preference__c":null,"Classification_Type__c":"HP Clinical Services","RecordTypeId":"'+recTypeId+'","AccountId":"'+accountRec.id+'"}';
            String caseCommentJson = '{"CommentBody":"Test caseComment and case save functions"}';
            
            Task objTask = new Task(WhatID = caseRecord.id);
            objTask.MET_Task__c  = oTask.id;
            objTask.MET_Action__c  = oAction.id;
            objTask.MET_Source__c  = oSource.id;
            objTask.Status  = 'Completed';
      		objTask.CreatedDate_Sort__c  = '2021-01-01 01:11:10:764';
            insert objTask;
            string lstTask = '{"listMETTaskDTO" :[{"sTask":"'+objTask.MET_Task__c+'","sAction":"'+objTask.MET_Action__c+'","sSource":"'+objTask.MET_Source__c +'","isSourceEMMEMap":true,"sStatus":"Completed","sCreatedDate_Sort":"2022-12-13 15:30:58:712"}] }';
            
            String caseId =caseRecord.id;
            
            string caseId2 = PharmacyCaseSave_LC_HUM.saveCaseDetails(caseJson, caseCommentJson,caseId,'Humana Pharmacy Specialist',false,false,false,null,null,true,'',lstTask,'987979');
            string caseId3 = PharmacyCaseSave_LC_HUM.saveCaseDetails(caseJson, caseCommentJson,caseId,'Humana Pharmacy Specialist',false,false,false,null,null,true,'','','987979');
            string caseId4 = PharmacyCaseSave_LC_HUM.saveCaseDetails(caseJson, caseCommentJson,caseId,'Humana Pharmacy Specialist',false,false,true,null,null,true,'',lstTask,'987979');
        }
       }catch(exception e){
           
       }
}
}