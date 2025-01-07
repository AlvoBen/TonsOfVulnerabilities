/**
   @Author      : Pradeepkumar Dani
   @name        : CaseDetail_T_HUM
   @CreateDate  : 10/30/2017
   @Description : This test Class ensures CaseDetail_C_HUM coverage
   @Version     : 1.0
   @reference   : CaseDetail_C_HUM
   
Modification Log :  
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pradeepkumar Dani         146796                  11/10/2017                     Original version 
 * Srivinas Damera           188503                  2/22/2018                      REQ - 335172 - Removal of Permission Set to Access Custom Case Page  
 * Bharath Rajappan                                  05/12/2018                     REQ - 359179 - Disable change case owner button for creditable coverage  
 * Sunil Bukya               251880                  07/03/2018                     REQ - 366900 - Implemented Member_Id__c platform encryption changes.
 * Bharath Rajappan                                  07/16/2018                     REQ - 367804 - Disable case edit,close and chhange owner button for creditable coverage cases
 * Prathyu Tushar            324032                  01/02/2019                     REQ - 380419: Auto route cases with Expedited Appeal and Part D Redetermination 
 * Lakshmi Madduri                                   02/15/2019                     REQ-385259
 * Pradeep Dani              350910                  04/12/2019                     REQ - 396202 - Removing changes for Live agent case chat key association.
 * Santhi Mandava            355559                  05/10/2019                     Removed "ENCRYPTIONTYPE" custom setting creation
 * Rakesh Patni                                      08/26/2019                     REQ-40588 - Launch Compass 
 * Pooja Kumbhar                                     04/29/2020                     Checkmarx test method without assert error fix.
 * Mohammed Noor                                     06/08/2020                     Changes done for CR674 - US1034997, US1034977 and US1035031
 * Santhi Mandava                                    02/01/2021                     Fixed coverage issues
 * Kavya Bondada                                     04/26/2021                     Fixed coverage issues
 * Sunil Kumar Reddy Y                               06/09/2021                     Fixed coverage issues
 * Vamshi Krishna Pemberthi                          07/08/2021                     Fixed coverage issues
 * Atia Uzma                                         07/29/2021                     User Story 2498842: T1PRJ0001827 - MF 1 - AHT - Ability to Reopen and Edit closed cases (RF)
 * Mohammed Noor                                     08/19/2021                     Changes done for USER STORY 1801197
 * Pooja Kumbhar                                     09/01/2021                     Modified to increase code coverage
 * Anil parvathaneni                                 26/06/2022                     User Story 3252318 - T1PRJ0272555  - Code Coverage issue Resolution - Test Classes
********************************************************************************************************************************/
@isTest
Private With sharing Class CaseDetail_T_HUM
{
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('CaseDetailUser1','Customer Care Specialist');
        testUser.UserPermissionsLiveAgentUser = true;
        testUser.Current_Queue__c='Test Business Process and Testing';
        testUser.Mentor_Sub_Function__c = 'Go365';
        insert testUser;
        system.assertEquals(testUser.lastname =='test CaseDetailUser1',true);
        
        User testUser1 = HUMTestDataHelperTest.createTestUser('CaseDetailUser2','Customer Care Supervisor');
        testUser1.Current_Queue__c='Test Business Process and Testing';
        insert testUser1;
               
        Group oCompassNewGroup = null;
        oCompassNewGroup = new Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oCompassNewGroup;
        system.assertEquals(oCompassNewGroup.Type,'Queue');

        QueueSObject oCompassNewQueue = new QueueSObject(QueueId = oCompassNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
        insert oCompassNewQueue;
        system.assertEquals(oCompassNewQueue.SObjectType,'Work_Queue_Setup__c');
        
        RecordType caseRecordType = [Select Id from RecordType where Name='Closed Member Case' and sObjectType='Case' limit 1];
        
        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account' limit 1];

        system.runAs(testUser)
        { 
                
            //Creating Account Record
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
            
            Case objCase = HUMTestDataHelperTest.getCase();            
            objCase.Guided_Process_Flag__c = True;
            objCase.Origin = GLOBAL_CONSTANT_HUM.TRR_ORIGIN;
            objCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            objCase.Case_Owner__c = 'Louisville RSO Enrollment';
            objCase.status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;
            objCase.ChatKey__c = 'TestKey';
            objCase.Policy_Member__c = policyMemRec.Id;
            objCase.AccountID = accountRec.Id;
            insert objCase;
            system.assertNotEquals(objCase, null, 'Value not matched');
            
            Case objCase11 = HUMTestDataHelperTest.getCase();            
            objCase11.Guided_Process_Flag__c = True;
            objCase11.Origin = GLOBAL_CONSTANT_HUM.TRR_ORIGIN;
            objCase11.Owner_Queue__c = 'PCP Assignment Research';
            objCase11.Case_Owner__c = 'Louisville RSO Enrollment';
            objCase11.status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;
            objCase11.ChatKey__c = 'TestKey';
            objCase11.Policy_Member__c = policyMemRec.Id;
            objCase11.AccountID = accountRec.Id;
            objCase11.AutoRoute_Status__c = 'Completed';
            insert objCase11;
            system.assertNotEquals(objCase11, null, 'Value not matched');
            
            Case objCase12 = HUMTestDataHelperTest.getCase();            
            objCase12.Guided_Process_Flag__c = True;
            objCase12.Origin = GLOBAL_CONSTANT_HUM.TRR_ORIGIN;
            objCase12.Owner_Queue__c = 'Louisville Premium Payment Recovery';
            objCase12.Case_Owner__c = 'Louisville RSO Enrollment';
            objCase12.status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;
            objCase12.ChatKey__c = 'TestKey';
            objCase12.Policy_Member__c = policyMemRec.Id;
            objCase12.AccountID = accountRec.Id;
            objCase12.AutoRoute_Status__c = 'Completed';
            insert objCase12;
            system.assertNotEquals(objCase12, null, 'Value not matched');
            
            Case objCase13 = HUMTestDataHelperTest.getCase();            
            objCase13.Guided_Process_Flag__c = True;
            objCase13.Origin = GLOBAL_CONSTANT_HUM.TRR_ORIGIN;
            objCase13.Owner_Queue__c = 'LV G and A and Correspondence Screening';
            objCase13.Case_Owner__c = 'Louisville RSO Enrollment';
            objCase13.status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;
            objCase13.ChatKey__c = 'TestKey';
            objCase13.Policy_Member__c = policyMemRec.Id;
            objCase13.AccountID = accountRec.Id;
            objCase13.AutoRoute_Status__c = 'Completed';
            insert objCase13;
            system.assertNotEquals(objCase13, null, 'Value not matched');
            
            Case objCase14 = HUMTestDataHelperTest.getCase();            
            objCase14.Guided_Process_Flag__c = True;
            objCase14.Origin = GLOBAL_CONSTANT_HUM.TRR_ORIGIN;
            objCase14.Owner_Queue__c = 'LV G and A and Correspondence Screening';
            objCase14.Case_Owner__c = 'Louisville RSO Enrollment';
            objCase14.status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;
            objCase14.ChatKey__c = 'TestKey1';
            objCase14.Policy_Member__c = policyMemRec.Id;
            objCase14.AccountID = accountRec.Id;
            objCase14.AutoRoute_Status__c = 'Completed';
            insert objCase14;
            system.assertNotEquals(objCase14, null, 'Value not matched');
            
            //Closed Member Case
            Case objCase1 = HUMTestDataHelperTest.getCase();                                    
            objCase1.Owner_Queue__c = 'Louisville RSO Enrollment';
            objCase1.Case_Owner__c = 'Louisville RSO Enrollment';
            objCase1.status = 'Closed';                     
            objCase1.AccountID = accountRec.Id;
            objCase1.recordtypeID = caseRecordType.Id;
            insert objCase1;
            system.assertNotEquals(objCase1, null, 'Value not matched');
            
            //Insert Template data
            Template__c templateObj = New Template__c(Unique_Template_Name__c ='ATVF007',Description__c='Creditable Coverage Attestation');
            Insert templateObj;   
            //Insert Template data
            Template__c templateObj1 = New Template__c(Unique_Template_Name__c ='95C90830-3133',Description__c='Medicare/Medicaid Expedited Appeal Request');
            Insert templateObj1;
            Template__c templateObj2 = New Template__c(Unique_Template_Name__c ='BC929CD6-E06A',Description__c='Medicare Part D Redetermination Request');
            Insert templateObj2;
            Template__c templateObj3 = New Template__c(Unique_Template_Name__c ='E0A13562-89EE-48B8-8EBA-E485C6583FF1',Description__c='Medicare Good Cause Reinstatement Form');
            Insert templateObj3;
            Template__c templateObj4 = New Template__c(Unique_Template_Name__c ='d884d160-77c0-4d1b-95aa-edf8c81f3a59',Description__c='PCP/PCD Questions');
            Insert templateObj4;
             Template__c templateObj5 = New Template__c(Unique_Template_Name__c ='deeef952-9f63-49ce-97d0-c7eb3204b519',Description__c='Medicaid PAR Provider Not Accepting');
            Insert templateObj5;
            
            //Insert Template submission owner
            Template_Submission_Owner__c ownerObj = New Template_Submission_Owner__c();
            ownerObj.Object_Owner_ID__c = objCase.Id;
            ownerObj.Object_Owner_Type__c = 'Case';
            insert ownerObj;  
            
            //Insert Template submission
            Template_Submission__c  submissionObj = New Template_Submission__c();
            submissionObj.Submission_Owner__c = ownerObj.Id;
            submissionObj.Template__c = templateObj.id;
            insert submissionObj;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj = New Template_Field__c();
            templateFieldObj.Template__c = templateObj.Id;
            templateFieldObj.Data_Type__c ='String';
            templateFieldObj.Name = 'ATVF_MemberId';
            insert templateFieldObj;
            
            //Insert template submission data
            Template_Submission_Data__c submissionDataObj = New Template_Submission_Data__c();
            submissionDataObj.Value__c = '12345';
            submissionDataObj.Template_Field__c = templateFieldObj.Id;
            submissionDataObj.Template_Submission__c = submissionObj.Id;
            insert submissionDataObj;
            //Insert Template submission owner
            Template_Submission_Owner__c ownerObj1 = New Template_Submission_Owner__c();
            ownerObj1.Object_Owner_ID__c = objCase.Id;
            ownerObj1.Object_Owner_Type__c = 'Case';
            insert ownerObj1;  
            
            //Insert Template submission
            Template_Submission__c  submissionObj1 = New Template_Submission__c();
            submissionObj1.Submission_Owner__c = ownerObj.Id;
            submissionObj1.Template__c = templateObj1.id;
            insert submissionObj1;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj1 = New Template_Field__c();
            templateFieldObj1.Template__c = templateObj1.Id;
            templateFieldObj1.Data_Type__c ='String';
            templateFieldObj1.Name = '95C90830-3133';
            insert templateFieldObj1;
            
            //Insert template submission data
            Template_Submission_Data__c submissionDataObj1 = New Template_Submission_Data__c();
            submissionDataObj1.Value__c = '12345';
            submissionDataObj1.Template_Field__c = templateFieldObj1.Id;
            submissionDataObj1.Template_Submission__c = submissionObj1.Id;
            insert submissionDataObj1;
            
            //Insert Template submission owner
            Template_Submission_Owner__c ownerObj1MED = New Template_Submission_Owner__c();
            ownerObj1MED.Object_Owner_ID__c = objCase14.Id;
            ownerObj1MED.Object_Owner_Type__c = 'Case';
            insert ownerObj1MED;  
            
            //Insert Template submission
            Template_Submission__c  submissionObj1MED = New Template_Submission__c();
            submissionObj1MED.Submission_Owner__c = ownerObj1MED.Id;
            submissionObj1MED.Template__c = templateObj1.id;
            insert submissionObj1MED;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj1MED = New Template_Field__c();
            templateFieldObj1MED.Template__c = templateObj1.Id;
            templateFieldObj1MED.Data_Type__c ='String';
            templateFieldObj1MED.Name = '95C90830-3133';
            insert templateFieldObj1MED;
            
            //Insert template submission data
            Template_Submission_Data__c submissionDataObj1MED = New Template_Submission_Data__c();
            submissionDataObj1MED.Value__c = '12345';
            submissionDataObj1MED.Template_Field__c = templateFieldObj1MED.Id;
            submissionDataObj1MED.Template_Submission__c = submissionObj1MED.Id;
            insert submissionDataObj1MED;
            
            //Insert Template submission owner
            Template_Submission_Owner__c ownerObj2 = New Template_Submission_Owner__c();
            ownerObj2.Object_Owner_ID__c = objCase.Id;
            ownerObj2.Object_Owner_Type__c = 'Case';
            insert ownerObj2;  
            
            //Insert Template submission
            Template_Submission__c  submissionObj2 = New Template_Submission__c();
            submissionObj2.Submission_Owner__c = ownerObj.Id;
            submissionObj2.Template__c = templateObj2.id;
            insert submissionObj2;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj2 = New Template_Field__c();
            templateFieldObj2.Template__c = templateObj2.Id;
            templateFieldObj2.Data_Type__c ='String';
            templateFieldObj2.Name = 'BC929CD6-E06A';
            insert templateFieldObj2;
            
            //Insert template submission data
            Template_Submission_Data__c submissionDataObj2 = New Template_Submission_Data__c();
            submissionDataObj2.Value__c = '12345';
            submissionDataObj2.Template_Field__c = templateFieldObj2.Id;
            submissionDataObj2.Template_Submission__c = submissionObj2.Id;
            insert submissionDataObj2;
            
            //Insert Template submission owner
            Template_Submission_Owner__c ownerObj2MED = New Template_Submission_Owner__c();
            ownerObj2MED.Object_Owner_ID__c = objCase13.Id;
            ownerObj2MED.Object_Owner_Type__c = 'Case';
            insert ownerObj2MED;  
            
            //Insert Template submission
            Template_Submission__c  submissionObj2MED = New Template_Submission__c();
            submissionObj2MED.Submission_Owner__c = ownerObj2MED.Id;
            submissionObj2MED.Template__c = templateObj2.id;
            insert submissionObj2MED;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj2MED = New Template_Field__c();
            templateFieldObj2MED.Template__c = templateObj2.Id;
            templateFieldObj2MED.Data_Type__c ='String';
            templateFieldObj2MED.Name = 'BC929CD6-E06A';
            insert templateFieldObj2MED;
            
            //Insert template submission data
            Template_Submission_Data__c submissionDataObj2MED = New Template_Submission_Data__c();
            submissionDataObj2MED.Value__c = '12345';
            submissionDataObj2MED.Template_Field__c = templateFieldObj2MED.Id;
            submissionDataObj2MED.Template_Submission__c = submissionObj2MED.Id;
            insert submissionDataObj2MED;
            
            //Insert Template submission owner
            Template_Submission_Owner__c ownerObj3 = New Template_Submission_Owner__c();
            ownerObj3.Object_Owner_ID__c = objCase.Id;
            ownerObj3.Object_Owner_Type__c = 'Case';
            insert ownerObj3;  
            
            //Insert Template submission
            Template_Submission__c  submissionObj3 = New Template_Submission__c();
            submissionObj3.Submission_Owner__c = ownerObj.Id;
            submissionObj3.Template__c = templateObj3.id;
            insert submissionObj3;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj3 = New Template_Field__c();
            templateFieldObj3.Template__c = templateObj3.Id;
            templateFieldObj3.Data_Type__c ='String';
            templateFieldObj3.Name = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
            insert templateFieldObj3;
            
            //Insert template submission data
            Template_Submission_Data__c submissionDataObj3 = New Template_Submission_Data__c();
            submissionDataObj3.Value__c = '12345';
            submissionDataObj3.Template_Field__c = templateFieldObj3.Id;
            submissionDataObj3.Template_Submission__c = submissionObj3.Id;
            insert submissionDataObj3;
            
             //Insert Template submission owner
            Template_Submission_Owner__c ownerObj3MED = New Template_Submission_Owner__c();
            ownerObj3MED.Object_Owner_ID__c = objCase12.Id;
            ownerObj3MED.Object_Owner_Type__c = 'Case';
            insert ownerObj3MED;  
            
            //Insert Template submission
            Template_Submission__c  submissionObj3MED = New Template_Submission__c();
            submissionObj3MED.Submission_Owner__c = ownerObj3MED.Id;
            submissionObj3MED.Template__c = templateObj3.id;
            insert submissionObj3MED;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj3MED = New Template_Field__c();
            templateFieldObj3MED.Template__c = templateObj3.Id;
            templateFieldObj3MED.Data_Type__c ='String';
            templateFieldObj3MED.Name = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
            insert templateFieldObj3MED;
            
            //Insert template submission data
            Template_Submission_Data__c submissionDataObj3MED = New Template_Submission_Data__c();
            submissionDataObj3MED.Value__c = '12345';
            submissionDataObj3MED.Template_Field__c = templateFieldObj3MED.Id;
            submissionDataObj3MED.Template_Submission__c = submissionObj3MED.Id;
            insert submissionDataObj3MED;
            
            //Insert Template submission owner
            Template_Submission_Owner__c ownerObj4 = New Template_Submission_Owner__c();
            ownerObj4.Object_Owner_ID__c = objCase.Id;
            ownerObj4.Object_Owner_Type__c = 'Case';
            insert ownerObj4;  
            
            //Insert Template submission
            Template_Submission__c  submissionObj4 = New Template_Submission__c();
            submissionObj4.Submission_Owner__c = ownerObj.Id;
            submissionObj4.Template__c = templateObj4.id;
            insert submissionObj4;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj4 = New Template_Field__c();
            templateFieldObj4.Template__c = templateObj4.Id;
            templateFieldObj4.Data_Type__c ='String';
            templateFieldObj4.Name = 'd884d160-77c0-4d1b-95aa-edf8c81f3a59';
            insert templateFieldObj4;
            
            //Insert template submission data
            Template_Submission_Data__c submissionDataObj4 = New Template_Submission_Data__c();
            submissionDataObj4.Value__c = '12345';
            submissionDataObj4.Template_Field__c = templateFieldObj4.Id;
            submissionDataObj4.Template_Submission__c = submissionObj4.Id;
            insert submissionDataObj4;
            
            //Insert Template submission owner
            Template_Submission_Owner__c ownerObj4PCP = New Template_Submission_Owner__c();
            ownerObj4PCP.Object_Owner_ID__c = objCase11.Id;
            ownerObj4PCP.Object_Owner_Type__c = 'Case';
            insert ownerObj4PCP;
            
            //Insert Template submission
            Template_Submission__c  submissionObj4PCP = New Template_Submission__c();
            submissionObj4PCP.Submission_Owner__c = ownerObj4PCP.Id;
            submissionObj4PCP.Template__c = templateObj4.id;
            insert submissionObj4PCP;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj4PCP = New Template_Field__c();
            templateFieldObj4PCP.Template__c = templateObj4.Id;
            templateFieldObj4PCP.Data_Type__c ='String';
            templateFieldObj4PCP.Name = 'd884d160-77c0-4d1b-95aa-edf8c81f3a59';
            insert templateFieldObj4PCP;
				
			//Insert template submission data
            Template_Submission_Data__c submissionDataObj4PCP = New Template_Submission_Data__c();
            submissionDataObj4PCP.Value__c = '12345';
            submissionDataObj4PCP.Template_Field__c = templateFieldObj4PCP.Id;
            submissionDataObj4PCP.Template_Submission__c = submissionObj4PCP.Id;
            insert submissionDataObj4PCP;
            
            //Insert Template submission owner
            Template_Submission_Owner__c ownerObj5 = New Template_Submission_Owner__c();
            ownerObj5.Object_Owner_ID__c = objCase.Id;
            ownerObj5.Object_Owner_Type__c = 'Case';
            insert ownerObj5;  
            
            //Insert Template submission
            Template_Submission__c  submissionObj5 = New Template_Submission__c();
            submissionObj5.Submission_Owner__c = ownerObj.Id;
            submissionObj5.Template__c = templateObj5.id;
            insert submissionObj5;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj5 = New Template_Field__c();
            templateFieldObj5.Template__c = templateObj5.Id;
            templateFieldObj5.Data_Type__c ='String';
            templateFieldObj5.Name = 'deeef952-9f63-49ce-97d0-c7eb3204b519';
            insert templateFieldObj5;
            
            //Insert template submission data
            Template_Submission_Data__c submissionDataObj5 = New Template_Submission_Data__c();
            submissionDataObj5.Value__c = '12345';
            submissionDataObj5.Template_Field__c = templateFieldObj5.Id;
            submissionDataObj5.Template_Submission__c = submissionObj5.Id;
            insert submissionDataObj5;
            
            //Launch Compass Functionality
            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'Provider Calls (Dental)';
            ctRec.Is_Active__c=true;
            ctRec.Mentor_Function__c ='Contact Center Provider';            
            insert ctRec;
            system.assertEquals(ctRec.Is_Active__c,true);
            
            Product2 prod = new Product2();
            prod.Name = 'TestProd';
            prod.ProductCode = 'MED';
            prod.Product_Type__c = 'MAPD';
            prod.Segment_Type__c = 'MCR';
            insert prod;

            Policy__c policyRec1 = New Policy__c();
            policyRec1.Group_Name__c = accountRec.Id;
            policyRec1.Product__c = prod.id;
            policyRec1.Policy_Group_Number__c='11111';
            policyRec1.Major_Lob__c = 'MEF';
            insert policyRec1;
            system.assertEquals(policyRec1.Group_Name__c==accountRec.Id,true);
            
            Policy_Member__c policyMemRec1 = New Policy_Member__c();
            policyMemRec1.Name = '12345';
            policyMemRec1.Policy__c = policyRec1.Id;
            policyMemRec1.Member__c = accountRec.Id; 
            insert policyMemRec1;
            system.assertEquals(policyMemRec1.Policy__c==policyRec1.Id,true);

            Case prodCase = HUMTestDataHelperTest.getCase();            
            prodCase.Guided_Process_Flag__c = True;
            prodCase.Origin = GLOBAL_CONSTANT_HUM.MEDICARECALLS_CASEORIGIN_HUM;
            prodCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            prodCase.Case_Owner__c = 'Louisville RSO Enrollment';
            prodCase.status = GLOBAL_CONSTANT_HUM.CASESTATUS_HUM;
            prodCase.ChatKey__c = 'TestKeyProd';
            prodCase.Policy_Member__c = policyMemRec1.Id;
            prodCase.AccountID = accountRec.Id;
            insert prodCase;
            
            Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = 'Test Business Process and Testing', OwnerId = oCompassNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing',Classification_Type__c=ctRec.id);
            insert oWorkQueue;
            system.assertEquals(oWorkQueue.Service_Center__c,'Business');
        }
    }
    
    /*
    * This method Is used to test the detail redirection funtionality
    * @param   None
    * @return  None
    */
    Static testMethod void testCaseDetail()
    {
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test CaseDetailUser1'];
        System.assertEquals(testUser.LastName,'test CaseDetailUser1');                
        
        Group grp = new Group();
        grp.name = 'LV GA and Correspondence Screening PGQ';
        grp.Type = 'Regular'; 
        Insert grp; 
 
        //Create Group Member
        GroupMember grpMem1 = new GroupMember();
        grpMem1.UserOrGroupId = testUser.Id;
        grpMem1.GroupId = grp.Id;
        Insert grpMem1;
        
        grp = new Group();
        grp.name = 'LV GA and Correspondence Screening E PGV';
        grp.Type = 'Regular'; 
        Insert grp; 
 
        //Create Group Member
        grpMem1 = new GroupMember();
        grpMem1.UserOrGroupId = testUser.Id;
        grpMem1.GroupId = grp.Id;
        Insert grpMem1;
        
        grp = new Group();
        grp.name = 'LV GA and Correspondence Screening P PGV';
        grp.Type = 'Regular'; 
        Insert grp; 
 
        //Create Group Member
        grpMem1 = new GroupMember();
        grpMem1.UserOrGroupId = testUser.Id;
        grpMem1.GroupId = grp.Id;
        Insert grpMem1;
        
        grp = new Group();
        grp.name = 'Louisville Premium Payment Recovery PGQ';
        grp.Type = 'Regular'; 
        Insert grp; 
 
        //Create Group Member
        grpMem1 = new GroupMember();
        grpMem1.UserOrGroupId = testUser.Id;
        grpMem1.GroupId = grp.Id;
        Insert grpMem1;
        
        grp = new Group();
        grp.name = 'PCP Assignment Research PGQ';
        grp.Type = 'Regular'; 
        Insert grp; 
 
        //Create Group Member
        grpMem1 = new GroupMember();
        grpMem1.UserOrGroupId = testUser.Id;
        grpMem1.GroupId = grp.Id;
        Insert grpMem1;
        
        grp = new Group();
        grp.name = 'Market National Network Ops PGQ';
        grp.Type = 'Regular'; 
        Insert grp;
        
        //Create Group Member
        grpMem1 = new GroupMember();
        grpMem1.UserOrGroupId = testUser.Id;
        grpMem1.GroupId = grp.Id;
        Insert grpMem1;
   
        
        System.runAs(testUser)
        {  
            Test.startTest() ;
            Case caseRecord = [Select id from case where isClosed = false limit 1];
            case caseRecord1 = [Select id from case where isClosed = false AND Owner_Queue__c = 'PCP Assignment Research' limit 1];
            case caseRecord2 = [Select id from case where isClosed = false AND Owner_Queue__c = 'Louisville Premium Payment Recovery' limit 1];
            case caseRecord3 = [Select id from case where isClosed = false AND Owner_Queue__c = 'LV G and A and Correspondence Screening' limit 1];
            case caseRecord4 = [Select id from case where isClosed = false AND Owner_Queue__c = 'LV G and A and Correspondence Screening' AND ChatKey__c = 'TestKey1' limit 1];
            //Testing for redirecting page
            Test.setCurrentPage(page.CaseDetailRedirect_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(caseRecord);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail = New CaseDetail_C_HUM(scon);  
            caseDetail.bMedicareCalls = true;
            caseDetail.constructRecordTypePageMap();
            caseDetail.createFloatingList();
            caseDetail.removeFloaters(CaseDetailConstants_HUM.sAgent);
            caseDetail.redirectToRecType();
            caseDetail.referEdit();     
            caseDetail.changeCaseOwner();
            caseDetail.getVerifyAccountType();
            caseDetail.closeCase();
            caseDetail.isMedicaidPARProviderProcessAttached = true;
            caseDetail.isMedicaidPARProviderProcessAttached();
            caseDetail.isMedExpProcessAttachedExp();
            caseDetail.MedExpButtonDisableValue = false;
            caseDetail.blegacyDeleteCheck = false;
            
            ApexPages.StandardController scon1 = New ApexPages.StandardController(caseRecord1);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail1 = New CaseDetail_C_HUM(scon1);  
            caseDetail1.bMedicareCalls = true;
            caseDetail1.isPCPProcessAttached();
             
            ApexPages.StandardController scon2 = New ApexPages.StandardController(caseRecord2);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail2 = New CaseDetail_C_HUM(scon2);  
            caseDetail2.bMedicareCalls = true;
            caseDetail2.isMedExpProcessAttachedGoodCause();
            
            ApexPages.StandardController scon3 = New ApexPages.StandardController(caseRecord3);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail3 = New CaseDetail_C_HUM(scon3);  
            caseDetail3.bMedicareCalls = true;
            caseDetail3.isMedExpProcessAttachedPartD();
            caseDetail3.isMedExpProcessAttachedExp();
            
            ApexPages.StandardController scon4 = New ApexPages.StandardController(caseRecord4);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail4 = New CaseDetail_C_HUM(scon4);  
            caseDetail4.bMedicareCalls = true;
            caseDetail4.isMedExpProcessAttachedExp();
            
             CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(
                Name = '2498842',
                IsON__c=true);
            insert objCS;
            system.assertNotEquals(caseDetail.reopenAndEditSwith, null);
            
            
            //Testing for CaseDetail_Member_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Member_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon);  
            
            //Testing for CaseDetail_Agent_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Agent_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon); 
            
            //Testing for CaseDetail_Medicare_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Medicare_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon);                         
        }
    }
    
     /*
    * This method Is used to test the detail redirection funtionality
    * @param   None
    * @return  None
    */
    Static testMethod void testCaseDetailAll()
    {
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test CaseDetailUser1'];
        System.assertEquals(testUser.LastName,'test CaseDetailUser1');                
        
        Group grp = new Group();
        grp.name = 'LV GA and Correspondence Screening PGQ';
        grp.Type = 'Regular'; 
        Insert grp; 
 
        //Create Group Member
        GroupMember grpMem1 = new GroupMember();
        grpMem1.UserOrGroupId = testUser.Id;
        grpMem1.GroupId = grp.Id;
        Insert grpMem1;
        
        grp = new Group();
        grp.name = 'Market National Network Ops PGQ';
        grp.Type = 'Regular'; 
        Insert grp;
        
         //Create Group Member
        grpMem1 = new GroupMember();
        grpMem1.UserOrGroupId = testUser.Id;
        grpMem1.GroupId = grp.Id;
        Insert grpMem1;
        
       
        System.runAs(testUser)
        {  
            Test.startTest() ;
            Case caseRecord = [Select id from case where isClosed = false limit 1];
            case caseRecord1 = [Select id from case where isClosed = false AND Owner_Queue__c = 'PCP Assignment Research' limit 1];
            case caseRecord2 = [Select id from case where isClosed = false AND Owner_Queue__c = 'Louisville Premium Payment Recovery' limit 1];
            case caseRecord3 = [Select id from case where isClosed = false AND Owner_Queue__c = 'LV G and A and Correspondence Screening' limit 1];
            case caseRecord4 = [Select id from case where isClosed = false AND Owner_Queue__c = 'LV G and A and Correspondence Screening' AND ChatKey__c = 'TestKey1' limit 1];
            //Testing for redirecting page
            Test.setCurrentPage(page.CaseDetailRedirect_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(caseRecord);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail = New CaseDetail_C_HUM(scon);  
            caseDetail.bMedicareCalls = true;
            caseDetail.constructRecordTypePageMap();
            caseDetail.createFloatingList();
            caseDetail.removeFloaters(CaseDetailConstants_HUM.sAgent);
            caseDetail.redirectToRecType();
            caseDetail.referEdit();     
            caseDetail.changeCaseOwner();
            caseDetail.getVerifyAccountType();
            caseDetail.closeCase();
            caseDetail.isMedicaidPARProviderProcessAttached = true;
            caseDetail.isMedicaidPARProviderProcessAttached();
            caseDetail.isMedExpProcessAttachedExp();
            caseDetail.MedExpButtonDisableValue = false;
            caseDetail.blegacyDeleteCheck = false;
            
            ApexPages.StandardController scon1 = New ApexPages.StandardController(caseRecord1);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail1 = New CaseDetail_C_HUM(scon1);  
            caseDetail1.bMedicareCalls = true;
            caseDetail1.isPCPProcessAttached();
            
            ApexPages.StandardController scon2 = New ApexPages.StandardController(caseRecord2);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail2 = New CaseDetail_C_HUM(scon2);  
            caseDetail2.bMedicareCalls = true;
            caseDetail2.isMedExpProcessAttachedGoodCause();
            
            ApexPages.StandardController scon3 = New ApexPages.StandardController(caseRecord3);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail3 = New CaseDetail_C_HUM(scon3);  
            caseDetail3.bMedicareCalls = true;
            caseDetail3.isMedExpProcessAttachedPartD();
            
            ApexPages.StandardController scon4 = New ApexPages.StandardController(caseRecord4);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail4 = New CaseDetail_C_HUM(scon4);  
            caseDetail4.bMedicareCalls = true;
            caseDetail4.isMedExpProcessAttachedExp();
            
            CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(
                Name = '2498842',
                IsON__c=true);
            insert objCS;
            system.assertNotEquals(caseDetail.reopenAndEditSwith, null);
            
            
            //Testing for CaseDetail_Member_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Member_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon);  
            
            //Testing for CaseDetail_Agent_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Agent_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon); 
            
            //Testing for CaseDetail_Medicare_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Medicare_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon);                         
        }
    }
    

    /*
    * This method Is used to test the detail redirection functionality. The existing method is split to 
    * avoid the SOQL limits.
    * @param   None
    * @return  None
    */
    
    Static testMethod void testCaseDetail1()
    {
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test CaseDetailUser1'];
        System.assertEquals(testUser.LastName,'test CaseDetailUser1');
  
        System.runAs(testUser)
        {  
            Test.startTest() ;
            Case caseRecord = [Select id, status from case where isClosed = false limit 1];                        
                                    
            //Testing for CaseDetail_Provider_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Provider_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            ApexPages.currentPage().getParameters().put('caseComment', 'testComment');
            CaseDetail_C_HUM caseDetail = New CaseDetail_C_HUM(scon);             
            
            //Testing for CaseDetail_Pharmacy_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Pharmacy_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon); 
            caseDetail.bIsPharmacyUser = true;
            
            //Testing for CaseDetail_Group_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Group_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon); 
            
            //Testing for CaseDetail_Unknown_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Unknown_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon); 
        }
    }
     
     
   /*
    * This method is used to test expedited disable buttons method funtionality
    * @param   None
    * @return  None
    */
    static testMethod void disableChangeOwnerMedicare()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseDetailUser1'];
        
        System.runAs(testUser)
        {
            case caseRecord = [Select id from case  where isClosed = false limit 1];
            Test.startTest();         
            //Testing for CaseDetail_Member_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Medicare_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail = New CaseDetail_C_HUM(scon);  
            
            caseDetail.isMedExpProcessAttachedExp();
            System.assertEquals(caseDetail.bQueueUser,true);  
            caseDetail.isMedExpProcessAttachedPartD();
           
            Test.Stoptest();
        }  
    }

      /*
    * This method is used to test referEdit method for closed case
    * @param   None
    * @return  None
    */
    static testMethod void testReferEdit()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseDetailUser1'];
        
        System.runAs(testUser)
        {
            case caseRecord = [Select id from case  where isClosed = true limit 1];
            Test.startTest();         
            //Testing for CaseDetail_Member_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Member_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);                       
            CaseDetail_C_HUM caseDetail = New CaseDetail_C_HUM(scon);
            caseDetail.referEdit();                           
            System.assertNotEquals(caseDetail,null);    
            caseDetail.caseRecmdAction();   
            caseDetail.isInvoiceRequestAttached=true;
            caseDetail.isInvoiceRequestAttached();
            caseDetail.isOGOScrubberUser(); 
            
            Test.Stoptest();
        }  
    }   
    Static testMethod void testCaseDetailPAR()
    {
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test CaseDetailUser1'];
        System.assertEquals(testUser.LastName,'test CaseDetailUser1');
  
        System.runAs(testUser)
        {  
            Test.startTest() ;
            Case caseRecord = [Select id, status from case where isClosed = false limit 1];
            
            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'Medicaid Calls';
            ctRec.Is_Active__c=true;
            insert ctRec;
        Classification__c classificationRec2 = New Classification__c();
            classificationRec2.Name = 'Advanced Resolution (ART)';
            classificationRec2.Is_Active__c = true;
            insert classificationRec2;
            
            Intent__c intentRec2 = New Intent__c();
            intentRec2.Name = 'PAR Provider Not Accepting';
            intentRec2.Is_Active__c = true;
            insert intentRec2;
            
            CTCI_Junction__c ctciRec2 = New CTCI_Junction__c();
            ctciRec2.Classification_Type__c = ctRec.id;
            ctciRec2.Classification__c = classificationRec2.Id;
            ctciRec2.Intent__c = intentRec2.id;
            ctciRec2.IsActive__c = true;
            insert ctciRec2;
            caseRecord.CTCI_List__c = ctciRec2.id;
            caseRecord.AutoRoute_Status__c = 'Completed';
            caseRecord.Owner_Queue__c = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE;
            update caseRecord;
            Test.setCurrentPage(page.CaseDetail_Member_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail = New CaseDetail_C_HUM(scon); 
        
            
        Template__c tem1 = new Template__c();
            tem1.Unique_Template_Name__c = 'deeef952-9f63-49ce-97d0-c7eb3204b519';
            tem1.Description__c='PAR';
            insert tem1;
            
            Template_Field__c temfield1 = new Template_Field__c();
            temfield1.Name = 'Provider State';
            temfield1.Template__c = tem1.id;
            
            insert temfield1;
                
                
            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;
            
            Template_Submission__c submission11 = new Template_Submission__c();
            submission11.Template__c = tem1.id;
            submission11.Submission_Owner__c = tOnwer11.id;
            insert submission11;
                
            Template_Submission_Data__c tObj11 = new Template_Submission_Data__c();
            tObj11.Template_Field__c = temfield1.Id;
            tObj11.Template_Submission__c = submission11.id;
            tObj11.Value__c = 'KY';
            insert tObj11;
            
        caseDetail.isMedicaidPARProviderProcessAttached();
            Boolean flag = caseDetail.isGenesysUser;
        Test.stopTest() ;
            
    }
  }
  
  // Added testCaseDetail2 method to improve code coverage
  Static testMethod void testCaseDetail2()
    {
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test CaseDetailUser1'];
        System.assertEquals(testUser.LastName,'test CaseDetailUser1');                
        
        User testUser1 = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test CaseDetailUser2'];

        PermissionSet ps = [SELECT Id FROM PermissionSet Where Name ='CRMS_630_MedicareElectionTracking_EnrollmentEdit'];
        insert new PermissionSetAssignment(AssigneeId = testUser.Id, PermissionSetId = ps.Id);
        
        System.runAs(testUser)
        {  
            Test.startTest() ;
            Case caseRecord = [Select id from case where isClosed = false and status = 'In Progress' limit 1];
            
            //Testing for redirecting page
            Test.setCurrentPage(page.CaseDetailRedirect_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(caseRecord);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail = New CaseDetail_C_HUM(scon);  
            caseDetail.disableButtonOGOCase('abcd');

            caseDetail.isGenesysUser = false;
            caseDetail.phoneBookSwitch1726016 = false;
            caseDetail.blegacyDeleteCheck = false;
           
            //Testing for CaseDetail_Member_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Member_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon);  
            
            //Testing for CaseDetail_Agent_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Agent_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon); 
            
            //Testing for CaseDetail_Medicare_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Medicare_VF_HUM);
            scon = New ApexPages.StandardController(caseRecord);            
            ApexPages.currentPage().getParameters().put('test', 'test');
            caseDetail = New CaseDetail_C_HUM(scon);    
            
            Test.stopTest();
        }
    }
    
    // Added testmedicareProd method to improve code coverage
    static testMethod void testmedicareProd()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseDetailUser1'];
        
        System.runAs(testUser)
        {
            case caseRecord = [Select id from case  where ChatKey__c = 'TestKeyProd' limit 1];
            Test.startTest();         
            //Testing for CaseDetail_Member_VF_HUM page
            Test.setCurrentPage(page.CaseDetail_Member_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);           
            ApexPages.currentPage().getParameters().put('test', 'test');
            CaseDetail_C_HUM caseDetail = New CaseDetail_C_HUM(scon);  
            
            caseDetail.isMedExpProcessAttachedExp();
            System.assertEquals(caseDetail.bQueueUser,true);  
            caseDetail.isMedExpProcessAttachedPartD();
           
            Test.Stoptest();
        }  
    }

	/* This method is used to test createCoveoMessage method
    * @param   None
    * @return  None
    */
    static testMethod void testCreateCoveoMessage()
    {
        User testUser = [SELECT Id, Name, Current_Queue__c FROM User WHERE LastName = 'test CaseDetailUser1'];
        Case caseRec = [SELECT Id FROM Case WHERE isClosed = false limit 1];
        System.runAs(testUser)
        {            
            Test.startTest();
            if(caseRec!=null)
            {
                Test.setCurrentPage(page.CaseDetail_Member_VF_HUM);
                ApexPages.StandardController scon = new ApexPages.StandardController(caseRec);                       
                CaseDetail_C_HUM caseDetail = New CaseDetail_C_HUM(scon);
                String sJSONMsg = caseDetail.createCoveoMessage();
                System.assertEquals(true, String.isNotBlank(sJSONMsg));
            }
            Test.StopTest();
        }
    }
    /* This method is used to test Esclation indicator Functionality
    * @param   None
    * @return  None
    */
    static testMethod void testEscalationindicator()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseDetailUser1'];
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '2310106', IsON__c=true, Release_Name__c='05/06/2022', Scrum_Team__c='MasterMinds', Story_Title__c='T1PRJ0003805/T1PRJ0184373 - CR 008LS- ESS ID 305- Add an Escalation Indicator in CRM Service');
        insert objCS;
        System.runAs(testUser)
        {
            case caseRecord = [Select id from case  where isClosed = false limit 1];
            Test.startTest(); 
            caseRecord.Escalation_Indicator__c = 'Immediate Leader';
            update caseRecord;
            Test.setCurrentPage(page.CaseDetail_Medicare_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);           
            ApexPages.currentPage().getParameters().put('test', 'test');           
            Test.Stoptest();
        }  
    }
    
    static testMethod void testCreateCoverage()
    {
        User testUser = [SELECT Id, Name, Current_Queue__c FROM User WHERE LastName = 'test CaseDetailUser1'];
        Case caseRec = [SELECT Id FROM Case WHERE isClosed = false limit 1];
        System.runAs(testUser)
        {            
            Test.startTest();
            if(caseRec!=null)
            {
                Test.setCurrentPage(page.CaseDetail_Member_VF_HUM);
                ApexPages.StandardController scon = new ApexPages.StandardController(caseRec);                       
                CaseDetail_C_HUM caseDetail = New CaseDetail_C_HUM(scon);
                String sJSONMsg = caseDetail.createCoveoMessage();
                System.assertEquals(true, String.isNotBlank(sJSONMsg));
                caseDetail.closePopup();
                PageReference pref = caseDetail.saveAndContPopup();
                caseDetail.showEsclIndicatorAndDate();
            }
            Test.StopTest();
        }
    }
    
}