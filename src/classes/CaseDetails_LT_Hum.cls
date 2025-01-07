/*******************************************************************************************************************************
Apex Class Name : CaseDetails_LT_Hum
Version         : 1.0
Created On      : 12/16/2021
Function        : This class is test class for CaseDetails_LC_Hum

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0            Isha Gupta                                       12/16/2021               Original Version    
*    1.1            Ritik Agarwal                                    02/07/2022               fixed for memberplan auto population  
*    1.2            Kajal namdev                                     02/23/2022               coverage fix
*    1.3            Kajal namdev                                     03/17/2022               Fix populateCaseDataTestWithException Method
*    1.4           M k Manoj/Tummala Vijaya Lakshmi                  06/28/2022               US-3278156 ,US-3327799 For unknown accounts case creation And case edit functionality
*    1.5            Prasuna Pattabhi                                 09/30/2022               US 3730127 - Edit And Closed Edit Medicare Case
*    1.6            Manohar                                          10/12/2022               US 3759040 - Code changes for record type change for RCC users with 206 permission set
*    1.7            Prasuna Pattabhi                                 10/27/2022               US 3934433 - Medicare Calls CAse Comments
*    1.8            Jasmeen Shangari                                 12/06/2022               US-4016899, Creditable coverage Process changes to launch Case Edit to pre-populate specific values
*    1.9            Prasuna Pattabhi                                12/06/2022                US_4016910,US_4020206,US_4020208,US_4020207
*    2.0            Prasuna Pattabhi                                 12/21/2022              US_4020210 QAA Changes
*    2.1            Prasuna Pattabhi                                 03/31/2023              US 4429882 QS Edit Refresh changes
*    2.2             Pooja Kumbhar                                   04/13/2023              US 4425976 Case Mgt: Disable Buttons for Auto-Routing templates on Case Edit Page 
*    2.3            Prasuna Pattabhi                                06/14/2023                 US 4467570 : Launching and opening Newborn Notification Template (baby bot) for Medicaid (Lightning)
*    2.4            Pooja Kumbhar	                                06/29/2023                US4626269 - T1PRJ0865978 - C06- Case Management - OI : T1PRJ0865978 - C06, Case Management, OI (Medicare and Medicaid Other Insurance )Template, Auto Set CI's and Disable Case Edit Page buttons
*    2.5            Prasuna Pattabhi                                07/27/2023                 US 4876326 Restrict the User to Close and Transfer the Case without attaching Babybot for Medicaid from Case Details page
*    2.5             Gowthami Thota                                 07/31/2023              Test Class Fix for UAT 
*    2.6             Prasuna Pattabhi                               08/24/23                 US 4412371 Market Credentialing Task Fields
*    2.7             Prasuna Pattabhi                               10/05/23                 US 4828071 Disable Case Editing for Legacy Delete Members and Policies
*    2.8             Santhi Mandava                                 01/03/2024               User Story 5243585: T1PRJ1132745 - MF28328 - C06; Case Management - Classic & Lightning - Oklahoma MCD Update Medicaid PAR Provider Not Accepting Routing rules (MCD OK MBRCALLS 1.1) 
*/
@isTest
private class CaseDetails_LT_Hum {
    
    /**
* Test Setup method to create all the data required for testing.
*
* @param     NULL
* @return    void 
*/
    @testSetup static void setUpTestData()
    {
        Group g1 = new Group(Name='test_queue', type='Queue');
        insert g1;
        
        QueuesObject q1 = new QueueSObject(QueueID = g1.id,SobjectType = 'Work_Queue_Setup__c');
        insert q1;
        
        User usr = HUMTestDataHelperTest.createTestUser('CaseProcessUser','Customer Care Specialist');
        usr.Current_Queue__c ='Test Business Process and Testing';
        usr.Mentor_Sub_Function__c = 'Go365';
        insert usr;

        User oCCSUserWithNoQueue = HUMTestDataHelperTest.createTestUser('UserCCS1test','Humana Pharmacy Specialist');
        insert oCCSUserWithNoQueue;   
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('UserCCS1','Humana Pharmacy Specialist');
        oCCSUser.Current_Queue__c='test_queue';
        insert oCCSUser;
        system.assert(oCCSUser.LastName == 'test UserCCS1');

        User usr1 = HUMTestDataHelperTest.createTestUser('CaseEditUser2','Customer Care Specialist');
        insert usr1;
        system.assertEquals(usr1.lastname=='test CaseEditUser2',true);

        List<PermissionSetAssignment> psAssignL = new List<PermissionSetAssignment>();
        for( PermissionSet pRSOHP : [SELECT ID From PermissionSet WHERE Name IN ('CRMS_206_CCSHumanaPharmacyAccess' , 'CRMS_684_Medicare_Customer_Service_Access') ]){
            psAssignL.add(New PermissionSetAssignment(AssigneeId = usr1.id, PermissionSetId = pRSOHP.id));
        }
        insert psAssignL;

        system.runAS(oCCSUser)
        {
            List<CRMFunctionality_ONOFF_Switch__c> lstSwitches = new List<CRMFunctionality_ONOFF_Switch__c>();
            lstSwitches.Add(new CRMFunctionality_ONOFF_Switch__c(Name = '5231359',IsON__c=true));
            lstSwitches.Add(new CRMFunctionality_ONOFF_Switch__c(Name = '5243585',IsON__c=true));
            insert lstSwitches;
            System.assertNotEquals(lstSwitches[0].Id,Null);
            
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
            
            MemberPlan mp = new MemberPlan();
            mp.Name = 'test member plan';
            mp.MemberId = oMemAccount.id;
            insert mp;
            
            Case cs = new Case();
            cs.Status = 'In Progress';
            cs.AccountId = accList[0].Id;
            insert cs;
            
            Classification_Type__c ct = new Classification_Type__c();
            ct.Name = 'Medical CRU/Corr';
            insert ct;
            
            classification__c cl = new classification__c();
            cl.Name = 'Access To Care';
            insert cl;
            
            Intent__c it = new Intent__c();
            it.Name = 'test intent';
            insert it;
            
            CTCI_Junction__c ctci = new CTCI_Junction__c();
            ctci.Classification__c = cl.id;
            ctci.Classification_Type__c = ct.id;
            ctci.Intent__c = it.id;
            ctci.IsActive__c = true;
            insert ctci;
            
            
            Work_Queue_Setup__c wq = new Work_Queue_Setup__c();
            wq.IsActive__c = TRUE;
            wq.OwnerId = g1.id;
            wq.Classification_Type__c = ct.id;
            insert wq;
            
            HUMCaseInteractionAboutType__c cia = new HUMCaseInteractionAboutType__c();
            cia.CaseInteraction_About_Type__c = 'Member';
            cia.Name = 'Member';
            insert cia;
            
            HUMCaseNPI_TAXID__c npi = new HUMCaseNPI_TAXID__c();
            npi.Name = 'Member';
            insert npi;
            
            HUMAccountCaseRecordTypes__c crt = new HUMAccountCaseRecordTypes__c();
            crt.Name = 'Member';
            crt.Case_RecordType__c='HP Member Case';
            crt.Account_RecordType__c = 'Member';
            crt.Humana_Pharmacy__c  = true;
            insert crt;
            
            ConsumerID__c oConsumerID = new ConsumerID__c();
            oConsumerID.Account__c = oMemAccount.id;
            oConsumerID.Consumer_ID__c = '987654321';
            oConsumerID.ID_Type__c = 'TAXID';
            insert oConsumerID;
        }  
    }
    
    /**
* This method is used to create case data based on Current queue of the logged in user
*
* @param     NULL
* @return    void 
*/
    static testMethod void populateCaseDataTest()
    {
        User u = [SELECT id,Current_Queue__c from USER where Current_Queue__c='test_queue'];
        System.assertEquals('test_queue', u.Current_Queue__c);
        System.runAs(u){
            String objid = [SELECT id from Account].id;
            CaseDetails_LC_Hum.CaseModal csobj  =(CaseDetails_LC_Hum.CaseModal)CaseDetails_LC_Hum.populateCaseData(objid, '{"callerPageName":"Humana_Pharmacy_Tab"}');
            System.assertEquals( [Select Id From RecordType Where SobjectType = 'Case' and Name = 'HP Member Case'].Id, csobj.objCase.RecordTypeId);
            CaseDetails_LC_Hum.sQueueHistory = 'test queue history';
            CaseDetails_LC_Hum.funcOnPageLoad('Account', objid);
            CaseDetails_LC_Hum.funcOnPageLoad('MemberPlan', objid);
        }
    }
    
    /**
* This method is used to create case data based on diffrent queuehistory 
*
* @param     NULL
* @return    void 
*/
    static testMethod void populateCaseDataTestWithDifferentSQueueHistory()
    {
        User u = [SELECT id,Current_Queue__c from USER where Current_Queue__c='test_queue'];
        System.runAs(u){
            
            String objid = [SELECT id from Case]?.id;
            CaseDetails_LC_Hum.sQueueHistory = 'test queue history';
            CaseDetails_LC_Hum.fetchCaseData(Id.valueOf(objid));
            CaseDetails_LC_Hum.assignValuesToCase();
            System.AssertNotEquals(objid,Null);
        }
    }
    
    /**
* This method is used to test the exception scenario having no current queue of the loggedin user
*
* @param     NULL
* @return    void 
*/
    static testMethod void populateCaseDataTestWithException()
    {
        User u = [SELECT id,Current_Queue__c from USER where lastname = 'test UserCCS1test' ];
        System.runAs(u){
            try{
                Account oMemAccount = HUMTestDataHelperTest.getAccount();
                oMemAccount.Group_Number__c = '987654321';
                oMemAccount.Enterprise_Id__c = '1000101';
                oMemAccount.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
                oMemAccount.firstName = 'Daphiner';
                oMemAccount.lastName = 'Superman';
                oMemAccount.Veteran_Status__c ='confirmed-veteran';
                oMemAccount.VA_Health_Enrollee__c ='true';
                oMemAccount.Home_Office_Account__c = false;
                insert oMemAccount;
                CaseDetails_LC_Hum.CaseModal csobj  =(CaseDetails_LC_Hum.CaseModal)CaseDetails_LC_Hum.populateCaseData(oMemAccount.Id,'{"callerPageName":"Humana_Pharmacy_Tab"}');
                System.AssertNotEquals(oMemAccount.Id,Null);
            }
            catch(Exception e){}
        }
    }
    
 /**
* This method is used to test if condition of CoverassignFixedValuesToCase method
*
* @param     NULL
* @return    void 
*/   
   static testMethod void testCoverassignFixedValuesToCase(){
        User u = [SELECT id,Current_Queue__c from USER where Current_Queue__c='test_queue'];
        System.runAs(u){
            
            String objid = [SELECT id from Account].id;
            CaseDetails_LC_Hum.fetchCaseData(Id.valueOf(objid));
            CaseDetails_LC_Hum.sQueueHistory = 'test queue history';
            CaseDetails_LC_Hum.assignValuesToCase();//objid
            System.AssertNotEquals(objid,Null);
        } 
    } 

        /**
* This method Is used to test PopulateAccountRecordTypeName
*
* @param     NULL
* @return    void 
*/   
   Static testMethod void testPopulateAccountRecordTypeName(){
        User oUser = [SELECT id, Current_Queue__c from USER where Current_Queue__c='test_queue'];
        System.runAs(oUser){
            
            String sObjId = [SELECT id from Account].id;
            String sCaseObjId =[SELECT id from Case].id;
            CaseDetails_LC_Hum.populateAccountRecordTypeName(sObjId);
             CaseDetails_LC_Hum.populateAccountRecordTypeName(sCaseObjId);
             system.assertNotEquals(sObjId,Null);
        } 
    } 
    /**
* This method Is used to test the Medicare Case Record scenario 
*
* @param     NULL
* @return    void 
*/
Static testMethod void testMedicareCaseRelatedChanges(){
        Group oNewGroup = null;
        oNewGroup = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oNewGroup;
        System.AssertNotEquals(oNewGroup.Id,Null);

        QueueSObject oNewQueue = New QueueSObject(QueueId = oNewGroup.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue; 
        System.AssertNotEquals(oNewQueue.Id,Null);

        User custCareSpecialist = HUMTestDataHelperTest.createTestUser('userMediareCase','Customer Care Specialist');
        insert custCareSpecialist;
        System.AssertNotEquals(custCareSpecialist.Id,Null);

        User custCareSpecialist1 = HUMTestDataHelperTest.createTestUser('userMediareCase1','Customer Care Specialist');
        insert custCareSpecialist1;
        System.AssertNotEquals(custCareSpecialist1.Id,Null);

        custCareSpecialist.Current_Queue__c = 'Test Business Process and Testing';
        update custCareSpecialist; 

        custCareSpecialist1.Current_Queue__c = 'Test Business Process and Testing';
        update custCareSpecialist1; 

        system.runAS(custCareSpecialist){
            
            Work_Queue_Setup__c qsWorkQueue = New Work_Queue_Setup__c(
            Name = 'Test Business Process and Testing', OwnerId = oNewGroup.Id, 
            Service_Center__c = 'Business',Department__c = 'Process and Testing');
            insert qsWorkQueue;           
            System.AssertNotEquals(qsWorkQueue.Id,Null);

            Classification_Type__c callsClassificationType = New Classification_Type__c(Name = 'Calls',Is_Active__c = true);  
            insert callsClassificationType;

            qsWorkQueue.Classification_Type__c = callsClassificationType.id;
            update qsWorkQueue;

            Classification__c billing = New Classification__c(Name = 'Billing',Is_Active__c = true);
            insert billing;
            System.AssertNotEquals(billing.Id,Null);

            Intent__c makePayment = New Intent__c(Name = 'Make Payment',Is_Active__c = true);                       
            insert makePayment;

            CTCI_Junction__c ctciValid = New CTCI_Junction__c(Classification_Type__c = callsClassificationType.id,
            Classification__c = billing.Id, Intent__c = makePayment.id, IsActive__c = True);
            insert ctciValid;
            System.AssertNotEquals(ctciValid.Id,Null);

            RecordType memberAccountType = [SELECT Id, Name, DeveloperName, SobjectType FROM RecordType
                WHERE SobjectType = 'Account' AND DeveloperName = 'Member']; 

            Account memberAccountValid = New Account(
                FirstName = 'medicareCase', LastName = 'member',RecordTypeId = memberAccountType.Id,
                CoKY_Account__c = False, Tenant_Id__c = '00',General_Account__c = true, NPI_ID__c = '182245',
                Humana_One_Indicator__c = False, Home_Office_Account__c = False, ETL_Record_Deleted__c = False
            );
            insert memberAccountValid;

            RecordType unknownMemberAccountType = [SELECT Id, Name, DeveloperName, SobjectType FROM RecordType
                WHERE SobjectType = 'Account' AND DeveloperName = 'Unknown_Member'];

            Account unknownMemberAccountValid = New Account(
                FirstName = 'medicareCase', LastName = 'unknownmember',RecordTypeId = unknownMemberAccountType.Id,
                CoKY_Account__c = False, Tenant_Id__c = '00',General_Account__c = true, NPI_ID__c = '182245',
                Humana_One_Indicator__c = False, Home_Office_Account__c = False, ETL_Record_Deleted__c = False
            );
            insert unknownMemberAccountValid;

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
        
            Case caseRecordInp = New Case(Status = 'In Progress',AccountId = memberAccountValid.id,
            ctci_list__c = ctciValid.id, Complaint__c = 'Yes - Medicare Part C',Complaint_Reason__c = 'Other',
            Complaint_Type__c = 'Behavioral',DCN__c='12345678');
            insert caseRecordInp;

            Task oTask = New Task(WhatId = caseRecordInp.id, type ='Work Task', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                Work_Queue_View_Name__c ='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                status ='In Progress' , Topic__c = 'SIU');

            /** Validate Case */
            CaseDetails_LC_Hum.validateCase('In Progress','Inbound Call',memPlan.Id,false,'1234577',caseRecordInp.id,'MHK Dispute Task');
            CaseDetails_LC_Hum.validateCase('In Progress','Inbound Call',memPlan.Id,true,'1234577',caseRecordInp.id,'MHK Dispute Task'); 
            CaseDetails_LC_Hum.validateCase('Closed','Inbound Call',memPlan.Id,true,'1234577',caseRecordInp.id,'MHK Dispute Task');

            /**  Call Benificiary without access to PS*/
            CaseDetails_LC_Hum.callBenifitCategoryView(memPlan.Id,'Member','Inbound Call'); 
            CaseDetails_LC_Hum.checkMedicareCalls(memPlan.Id,'Inbound Call');
            
            /**  Call Benificiary with access to PS*/

            /** With Memebr Plan ID */
            PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM LIMIT 1];
            insert New PermissionSetAssignment(AssigneeId = custCareSpecialist.id, PermissionSetId = pCheck.id);
            pCheck =[SELECT ID From PermissionSet WHERE Name ='CRMS_400_Grievance_Appeals' LIMIT 1];
            insert New PermissionSetAssignment(AssigneeId = custCareSpecialist.id, PermissionSetId = pCheck.id);
            pCheck =[SELECT ID From PermissionSet WHERE Name ='CRMS_630_MedicareElectionTracking_EnrollmentEdit' LIMIT 1];
            insert New PermissionSetAssignment(AssigneeId = custCareSpecialist.id, PermissionSetId = pCheck.id);
            CaseDetails_LC_Hum.checkPermisionSetAccess();
            CaseDetails_LC_Hum.callBenifitCategoryView(memPlan.Id,'Member','Inbound Call'); 
            CaseDetails_LC_Hum.checkMedicareCalls(memPlan.Id,'Inbound Call'); 
            CaseDetails_LC_Hum.getCallBenefitCategoryValues(memPlan.Id);  

            /** No Member Plan ID */
            CaseDetails_LC_Hum.callBenifitCategoryView(null,'Member','Inbound Call'); 
            CaseDetails_LC_Hum.checkMedicareCalls(null,'Inbound Call'); 
            CaseDetails_LC_Hum.getCallBenefitCategoryValues(null);

            productRec.ProductCode = 'MED';
            productRec.Major_LOB__c = 'MER';
            productRec.Product_Type__c = 'MAPD';
            productRec.Segment_Type__c = 'MCR';
            update productRec;
            CaseDetails_LC_Hum.callBenifitCategoryView(memPlan.Id,'Member','Inbound Call'); 

            productRec.Product_Type__c = '';
            productRec.Segment_Type__c = 'MCD';
            update productRec;
            policyObj.Major_LOB__c ='MCD';            
            update policyObj;
            CaseDetails_LC_Hum.callBenifitCategoryView(memPlan.Id,'Member','Inbound Call');

            productRec.Segment_Type__c = 'MCR';
            update productRec;
            policyObj.Major_LOB__c ='MER';            
            update policyObj;
            CaseDetails_LC_Hum.callBenifitCategoryView(memPlan.Id,'Member','Inbound Call');

            /** Without Plan ID */
            memPlan.PlanId = null;
            update memPlan;
            CaseDetails_LC_Hum.callBenifitCategoryView(memPlan.Id,'Member','Inbound Call'); 
            CaseDetails_LC_Hum.checkMedicareCalls(memPlan.Id,'Inbound Call'); 
            CaseDetails_LC_Hum.getCallBenefitCategoryValues(memPlan.Id);
            
            /** With Plan ID */
            memPlan.PlanId = policyObj.Id;
            update memPlan;
            CaseDetails_LC_Hum.callBenifitCategoryView(memPlan.Id,'Member','Inbound Call'); 
            CaseDetails_LC_Hum.checkMedicareCalls(memPlan.Id,'Inbound Call'); 
            CaseDetails_LC_Hum.getCallBenefitCategoryValues(memPlan.Id);                      
            
            productRec.Product_Type__c = 'MA';
            update productRec; 
            CaseDetails_LC_Hum.getCallBenefitCategoryValues(memPlan.Id); 

            productRec.Product_Type__c = 'PDP';
            update productRec; 
            CaseDetails_LC_Hum.getCallBenefitCategoryValues(memPlan.Id); 
            
            productRec.Product_Type__c = 'PDP';
            productRec.Segment_Type__c = 'MCR';
            update productRec; 
            CaseDetails_LC_Hum.checkMedicareCalls(memPlan.Id,'Inbound Call');

            productRec.Product_Type__c = '';
            productRec.Segment_Type__c = 'MER';            
            update productRec; 
            policyObj.Major_Lob__c='MER';
            update policyObj;
            CaseDetails_LC_Hum.callBenifitCategoryView(memPlan.Id,'Member','Inbound Call');

            productRec.Product_Type__c = '';
            productRec.Segment_Type__c = 'MCD';
            update productRec; 
            policyObj.Major_Lob__c='MCD';
            update policyObj;
            CaseDetails_LC_Hum.callBenifitCategoryView(memPlan.Id,'Member','Inbound Call');

            MedicareCallsCaseComments_H_HUM_LWC oMedCalls = New MedicareCallsCaseComments_H_HUM_LWC();
            oMedCalls.checkMedicareCare('Inbound Call', 'MED', 'PDP','', 'MCR');            

            list<CaseCommentType__c> lstCCT = New list<CaseCommentType__c>();
            CaseCommentType__c oCCTIssue = New CaseCommentType__c(Name = HumConstants_LWC.MEDICARECALLS_ISSUE_HUM);
            lstCCT.add(oCCTIssue);
            CaseCommentType__c oCCTReso = New CaseCommentType__c(Name = HumConstants_LWC.MEDICARECALLS_RESO_HUM);
            lstCCT.add(oCCTReso);
            insert lstCCT;            

            list<CaseComment> lstCC1 = New list<CaseComment>();
            CaseComment oCC11 = New CaseComment(ParentId = caseRecordInp.Id, CommentBody = 'TEST CASE COMMENT MEDI ISSUE');
            lstCC1.add(oCC11);
            CaseComment oCC21 = New CaseComment(ParentId = caseRecordInp.Id, CommentBody = 'TEST CASE COMMENT MEDI RESOLUTION');
            lstCC1.add(oCC21);
            insert lstCC1;
            
            list<CaseCommentType_Junction__c> lstCCTJ1 = New list<CaseCommentType_Junction__c>();
            CaseCommentType_Junction__c oCCTJ11 = New CaseCommentType_Junction__c(CaseCommentType__c = oCCTIssue.Id, CaseComment__c = oCC11.Id);
            lstCCTJ1.add(oCCTJ11);
            CaseCommentType_Junction__c oCCTJ21 = New CaseCommentType_Junction__c(CaseCommentType__c = oCCTReso.Id, CaseComment__c = oCC21.Id);
            lstCCTJ1.add(oCCTJ21);
            insert lstCCTJ1;
            MedicareCallsCaseComments_H_HUM_LWC.createUpdateCaseCommentRecord('TEST CASE ISSUE','TEST CASE RESOLUTION',caseRecordInp.Id);
            CaseDetails_LC_Hum.getCCMedicareCalls(caseRecordInp.Id); 
        }
    }

    /**
    * This method Is used to test UpdateAccountRecordTypeName
    *
    * @param     NULL
    * @return    void 
    */   
    
    Static testMethod void testgetRecordType(){

        User user1 = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser2'];
        Id ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'Medical CRU/Corr' LIMIT 1].id;
        Id classificationRec = [select id,Is_Active__c from Classification__c where Name='Access To Care' LIMIT 1].id;
        Id intentRec = [Select id,Is_Active__c from Intent__c where Name='test intent' LIMIT 1].id;
        
        CTCI_Junction__c ctciRec =[select id,name,Classification_Type__c,Classification__c,Intent__c,Service_Model_Type__c from CTCI_Junction__c where Classification_Type__c=:ctRec and Classification__c=:classificationRec and Intent__c =:intentRec LIMIT 1];
        ctciRec.Service_Model_Type__c = 'Humana Pharmacy';
        update ctciRec;

        system.runAs(user1) 
        {
            Test.startTest();
            CaseDetails_LC_Hum.getRecordTypeChange(classificationRec,intentRec,'Member Case','Medical CRU/Corr' );
            System.AssertNotEquals(ctciRec.Id,Null);
            Test.stopTest();
        }
    }
	
	 /**
	* This method Is used to test the Creditable Coverage Case Update scenario 
	*
	* @param     NULL
	* @return    void 
	*/
	static testMethod void testUpdateCaseCreditableCoverage(){
		
    User custCareSpec = HUMTestDataHelperTest.createTestUser('CaseEditUser1','Customer Care Specialist');
    insert custCareSpec;
    system.assertEquals(custCareSpec.lastname=='test CaseEditUser1',true);
    
    PermissionSet pRSOHP = [SELECT ID From PermissionSet WHERE Name = 'CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1];
    insert New PermissionSetAssignment(AssigneeId = custCareSpec.id, PermissionSetId = pRSOHP.id);

    system.runAS(custCareSpec){
        System.AssertNotEquals(custCareSpec.Id,Null);
        list<Case_Milestone__c> lstCustomSettings = new list<Case_Milestone__c>();
        lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-001', Doc_Type__c = 't1', Sub_Type__c = 'Creditable Coverage', Milestone__c = '909', Origin__c = 'Inbound Call', Is_Start_Milestone__c = true));
        insert lstCustomSettings; 
        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
    
        //Creating Account Record
        Account accountRec = HUMTestDataHelperTest.getAccount();
        accountRec.OwnerId = custCareSpec.id;
        accountRec.FirstName = 'Test';
        accountRec.LastName = 'Account';
        accountRec.Enterprise_Id__c = '123456';
        accountRec.recordtypeId = AccountRecordType.id;
        insert accountRec;
        system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);		
        
        List<Classification_Type__c> ctList= New list<Classification_Type__c>();
       
        Classification_Type__c ctRec1 = New Classification_Type__c();
        ctRec1.name = 'calls (RSO)';
        ctRec1.Is_Active__c=true;
        ctList.add(ctRec1);        

        insert ctList;
        system.assertEquals(ctList.size(),1);
        
        List<Classification__c> lstClassification = New List<Classification__c>();        
        Classification__c classificationRecDec1 = New Classification__c();
        classificationRecDec1.Name = 'G&A';
        classificationRecDec1.Is_Active__c = true;
        lstClassification.add(classificationRecDec1);

        Classification__c classificationRecDec2 = New Classification__c();
        classificationRecDec2.Name = 'Rx';
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
        intentRecDec2.Name = 'Part D Redetermination';
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
        caseRecord1.Subtype__c = 'Creditable Coverage';
        caseRecord1.Guided_Process_Flag__c = true;
        caseRecord1.Classification_Type__c = 'Calls (RSO)';
        caseRecord1.Classification_id__c=lstClassification[0].id;
        caseRecord1.Intent_id__c = lstIntent[0].id;
        caseRecord1.Interacting_With__c = accountRec.id;
        caseRecord1.Interacting_With_Type__c = 'Member';   
        caseRecord1.Interacting_About_Type__c = 'Member';
        caseRecord1.Origin = 'Inbound Call';
        caseRecord1.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
        insert caseRecord1;
        system.assertEquals(caseRecord1.Case_Owner__c =='Louisville RSO Calls Support',true);

         if([SELECT ID FROM Template__c WHERE Unique_Template_Name__c='ATVF007'].isEmpty())
         {
            //Insert Template data
            Template__c templateObj = new Template__c(Unique_Template_Name__c='ATVF007',Description__c='Creditable Coverage Attestation');
            Insert templateObj;     
    
            //Insert Template Field Data
            List<Template_Field__c> templateFieldObj = new List<Template_Field__c>();
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_CaseNumber'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_CRMCaseCreateDate'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MedicareTrackId'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_IsCICMSCalled'));
            
            insert templateFieldObj;
    
        }

    List<Template_Submission_Data__c> submissionList = new List<Template_Submission_Data__c>();
    Template__c templateObj = [Select id from Template__c where Unique_Template_Name__c='ATVF007'];
    
    Template_Submission_Owner__c tOnwer = new Template_Submission_Owner__c();
    tOnwer.Object_Owner_ID__c=string.valueOf(caseRecord1.id);
    tOnwer.Object_Owner_Type__c='Case';
    insert tOnwer;
    
    Template_Submission__c submission = new Template_Submission__c();
    submission.Template__c = templateObj.id;
    submission.Submission_Owner__c = tOnwer.id;
    insert submission;        
    System.AssertNotEquals(submission.Id,Null);

    List<Template_Submission_Data__c> newSubmissionData = new List<Template_Submission_Data__c>();
    for(Template_Field__c submissionDataObj : [SELECT id,Name from Template_Field__c])
    {
        Template_Submission_Data__c tObj = new Template_Submission_Data__c();
        tObj.Template_Field__c = submissionDataObj.Id;
        tObj.Template_Submission__c = submission.id;   
                         
        if(submissionDataObj.Name == 'ATVF_CaseNumber')
        {
            tObj.Value__c = '123456' ;
        }

        if(submissionDataObj.Name == 'ATVF_CRMCaseCreateDate')
        {
            dateTime strCreatedDateTime = datetime.now();
            tObj.Value__c = (String) strCreatedDateTime.format('MM/dd/yyyy');           
        }     
        
        if(submissionDataObj.Name == 'ATVF_MedicareTrackId')
        {
            tObj.Value__c = '123456';
        }
        
        if(submissionDataObj.Name == 'ATVF_IsCICMSCalled')
        {
            tObj.Value__c = 'true' ;
        }
        newSubmissionData.add(tObj);
    }
     insert newSubmissionData;  
        
     Test.startTest();                       
     case caseRecord = [Select id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c  from case where Subtype__c = 'Creditable Coverage' and Case_Owner__c = 'Louisville RSO Calls Support' LIMIT 1];
     System.assertEquals(caseRecord.Case_Owner__c , 'Louisville RSO Calls Support');
     CaseDetails_LC_Hum.editCaseData = caseRecord;
	 CaseDetails_LC_Hum.objCase = caseRecord;
     Map<String,String> flowDataResult = CaseDetails_LC_Hum.updateCaseCreditableCoverage();
     CaseDetails_LC_Hum.CheckCaseTemplates(caseRecord.id);
     System.AssertNotEquals(caseRecord.Id,Null);
     Test.stopTest();     

    }
}  	
        /**
    * This method Is used to test Pard D Process Changes
    *
    * @param     NULL
    * @return    void 
    */  
Static testMethod void testProcessPartD(){

        User usr = [Select Id,Name, Current_Queue__C from User where LastName='test CaseProcessUser']; 
        Group oGrp = null;
        oGrp = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oGrp;

        QueueSObject oNewQueue = New QueueSObject(QueueId = oGrp.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;
        
        system.runAs(usr){
            
            Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c(
            Name = 'Test Business Process and Testing', OwnerId = oGrp.Id, 
            Service_Center__c = 'Business',Department__c = 'Process and Testing');
            insert oWQS;
            System.AssertNotEquals(oWQS.Id,Null);
            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'Calls (RSO)';
            ctRec.Is_Active__c=true;
            insert ctRec;

            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Benefits';
            classificationRec.Is_Active__c = true;
            insert classificationRec;

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

            MemberPlan mp = new MemberPlan();
            mp.Name = 'test member plan';
            mp.MemberId = accountRec.id;
            insert mp;

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
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Policy_Member__c = null;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Origin = 'Inbound Call';
            insert caseRecord;
            System.AssertNotEquals(caseRecord.Id,Null);

            Template__c partD = new Template__c();
            partD.Unique_Template_Name__c = 'BC929CD6-E06A';
            partD.Description__c='partD';
            insert partD;

            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
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

            Template_Field__c partField = new Template_Field__c();
            partField.Name = 'RxObtained2';
            partField.Template__c = partD.id;            
            insert partField;

            Template_Submission__c partSubmission = new Template_Submission__c();
            partSubmission.Template__c = partD.id;
            partSubmission.Submission_Owner__c = tOnwer11.id;
            insert partSubmission;
            
            Template_Submission_Data__c partDataObj = new Template_Submission_Data__c();
            partDataObj.Template_Field__c = partField.Id;
            partDataObj.Template_Submission__c = partSubmission.id;
            partDataObj.Value__c = 'Yes';
            insert partDataObj;
            System.AssertNotEquals(partDataObj.Id,Null);

            CaseDetails_LC_Hum.CaseModal csobj  =(CaseDetails_LC_Hum.CaseModal)CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"Medicare Part D Redetermination"}');
            CaseDetails_LC_Hum.CheckCaseTemplates(caseRecord.id);
        }
    }
/**
    * This method Is used to test Expediate Process Changes
    *
    * @param     NULL
    * @return    void 
    */
    Static testMethod void testProcessExp(){
        
        User usr = [Select Id,Name, Current_Queue__C from User where LastName='test CaseProcessUser'];
        Group oGrp = null;
        oGrp = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oGrp;

        QueueSObject oNewQueue = New QueueSObject(QueueId = oGrp.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;
        
        system.runAs(usr){
            System.AssertNotEquals(usr.Id,Null);
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
            System.AssertNotEquals(accountRec.Id,Null);

            MemberPlan mp = new MemberPlan();
            mp.Name = 'test member plan';
            mp.MemberId = accountRec.id;
            insert mp;

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
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Policy_Member__c = null;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Origin = 'Inbound Call';
            insert caseRecord;
            System.AssertNotEquals(caseRecord.Id,Null);

            Template__c expedited = new Template__c();
            expedited.Unique_Template_Name__c = '95C90830-3133';
            expedited.Description__c='expedited';
            insert expedited;

            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;

            classificationRec.Name = 'G&A';
            update classificationRec;
            intentRec.Name = 'Expedited';
            update intentRec;

            ctciRec.Classification__c = classificationRec.id;
            ctciRec.Intent__c =intentRec.id;
            update ctciRec;

            Template_Field__c expField = new Template_Field__c();
            expField.Name = 'PhysicianName';
            expField.Template__c = expedited.id;            
            insert expField;

            Template_Submission__c expSubmission = new Template_Submission__c();
            expSubmission.Template__c = expedited.id;
            expSubmission.Submission_Owner__c = tOnwer11.id;
            insert expSubmission;
            
            Template_Submission_Data__c expDataObj = new Template_Submission_Data__c();
            expDataObj.Template_Field__c = expField.Id;
            expDataObj.Template_Submission__c = expSubmission.id;
            expDataObj.Value__c = 'Yes';
            insert expDataObj;
            System.AssertNotEquals(expDataObj.Id,Null);

            CaseDetails_LC_Hum.CaseModal csobj  =(CaseDetails_LC_Hum.CaseModal)CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"Medicare/Medicaid Expedited Appeal"}');
            CaseDetails_LC_Hum.CheckCaseTemplates(caseRecord.id);
        }
    }
	/**
    * This method Is used to test Good Cause Process Changes
    *
    * @param     NULL
    * @return    void 
    */
    Static testMethod void testProcessGoodCause(){
        
        User usr = [Select Id,Name, Current_Queue__C from User where LastName='test CaseProcessUser'];
        Group oGrp = null;
        oGrp = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oGrp;

        QueueSObject oNewQueue = New QueueSObject(QueueId = oGrp.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;
        
        system.runAs(usr){
            
            Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c(
            Name = 'Test Business Process and Testing', OwnerId = oGrp.Id, 
            Service_Center__c = 'Business',Department__c = 'Process and Testing');
            insert oWQS;
            System.AssertNotEquals(oWQS.Id,Null);
            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'Calls (RSO)';
            ctRec.Is_Active__c=true;
            insert ctRec;

            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Benefits';
            classificationRec.Is_Active__c = true;
            insert classificationRec;

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

            MemberPlan mp = new MemberPlan();
            mp.Name = 'test member plan';
            mp.MemberId = accountRec.id;
            insert mp;

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
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Policy_Member__c = null;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Origin = 'Inbound Call';
            insert caseRecord;

            Template__c goodCause = new Template__c();
            goodCause.Unique_Template_Name__c = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
            goodCause.Description__c='goodCause';
            insert goodCause;   
            
            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;

            Template_Field__c temfield1 = new Template_Field__c();
            temfield1.Name = 'GCRTermedPlanYesNo';
            temfield1.Template__c = goodCause.id;            
            insert temfield1;

            Template_Submission__c submission11 = new Template_Submission__c();
            submission11.Template__c = goodCause.id;
            submission11.Submission_Owner__c = tOnwer11.id;
            insert submission11;
            
            Template_Submission_Data__c tObj11 = new Template_Submission_Data__c();
            tObj11.Template_Field__c = temfield1.Id;
            tObj11.Template_Submission__c = submission11.id;
            tObj11.Value__c = 'Yes';
            insert tObj11;
           
            Template_Field__c temfield2 = new Template_Field__c();
            temfield2.Name = 'GCRIsMedicationNeeded';
            temfield2.Template__c = goodCause.id;
            
            insert temfield2;   
                
            Template_Submission_Data__c tObj12 = new Template_Submission_Data__c();
            tObj12.Template_Field__c = temfield2.Id;
            tObj12.Template_Submission__c = submission11.id;
            tObj12.Value__c = 'Yes';
            insert tObj12;

            classificationRec.Name = 'Billing';
            update classificationRec;
            intentRec.Name = 'Reinstatement Review';
            update intentRec;

            ctciRec.Classification__c = classificationRec.id;
            ctciRec.Intent__c =intentRec.id;
            update ctciRec;
            system.assertNotEquals(ctciRec.Id,Null);
            oWQS.Classification_Type__c = ctRec.Id;
            update oWQS;
            CaseDetails_LC_Hum.CaseModal csobj  =(CaseDetails_LC_Hum.CaseModal)CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"Medicare Good Cause Reinstatement Form"}');
            
            tObj12.Value__c = 'No';
            update tObj12;
            
            csobj  =(CaseDetails_LC_Hum.CaseModal)CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"Medicare Good Cause Reinstatement Form"}');
        	CaseDetails_LC_Hum.CheckCaseTemplates(caseRecord.id);
            system.assertNotEquals(csobj,Null);
        }
    }
	/**
    * This method Is used to test Par Provider Process Changes
    *
    * @param     NULL
    * @return    void 
    */
    Static testMethod void testProcessParProviderChanges(){
        
        User usr = [Select Id,Name, Current_Queue__C from User where LastName='test CaseProcessUser'];
        Group oGrp = null;
        oGrp = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
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

            MemberPlan mp = new MemberPlan();
            mp.Name = 'test member plan';
            mp.MemberId = accountRec.id;
            insert mp;

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
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Policy_Member__c = null;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Origin = 'Inbound Call';
            insert caseRecord;

            Template__c parProvider = new Template__c();
            parProvider.Unique_Template_Name__c = 'deeef952-9f63-49ce-97d0-c7eb3204b519';
            parProvider.Description__c='parProvider';
            insert parProvider;

            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;

            classificationRec.Name = 'Advanced Resolution (ART)';
            update classificationRec;
            intentRec.Name = 'Par Provider Not Accepting';
            update intentRec;

            ctciRec.Classification__c = classificationRec.id;
            ctciRec.Intent__c =intentRec.id;
            update ctciRec;

            Template_Field__c parField = new Template_Field__c();
            parField.Name = 'Product Type';
            parField.Template__c = parProvider.id;            
            insert parField;

            Template_Submission__c parSubmission = new Template_Submission__c();
            parSubmission.Template__c = parProvider.id;
            parSubmission.Submission_Owner__c = tOnwer11.id;
            insert parSubmission;
            
            Template_Submission_Data__c parDataObj = new Template_Submission_Data__c();
            parDataObj.Template_Field__c = parField.Id;
            parDataObj.Template_Submission__c = parSubmission.id;
            parDataObj.Value__c = 'Yes';
            insert parDataObj;

            CaseDetails_LC_Hum.CaseModal csobj  =(CaseDetails_LC_Hum.CaseModal)CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"Medicaid PAR Provider Not Accepting"}');           
            ctRec.name = 'Medicaid Calls';
            update ctRec;
            caseRecord.Classification_Type__c = 'Medicaid Calls';
            update caseRecord;
            classificationRec.Name = CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderClassification;
            update classificationRec;
            intentRec.Name = CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderIntent;
            update intentRec;
            ctciRec.Classification__c = classificationRec.id;
            ctciRec.Intent__c =intentRec.id;
            update ctciRec;
            caseRecord.CTCI_List__c = ctciRec.id;
            update caseRecord;

            Template_Field__c parField1 = new Template_Field__c();
            parField1.Name = 'Provider State';
            parField1.Template__c = parProvider.id;            
            insert parField1;
            
            Template_Submission__c parSubmission1 = new Template_Submission__c();
            parSubmission1.Template__c = parProvider.id;
            parSubmission1.Submission_Owner__c = tOnwer11.id;
            insert parSubmission1;
                
            Template_Submission_Data__c parObj = new Template_Submission_Data__c();
            parObj.Template_Field__c = parField1.Id;
            parObj.Template_Submission__c = parSubmission1.id;
            parObj.Value__c = 'IL';
            insert parObj;
            csobj  =(CaseDetails_LC_Hum.CaseModal)CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"Medicaid PAR Provider Not Accepting"}');           
            
            parObj.Value__c = 'KY';
            update parObj;
            csobj  =(CaseDetails_LC_Hum.CaseModal)CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"Medicaid PAR Provider Not Accepting"}');           
            
            parObj.Value__c = 'FL';
            update parObj;
            csobj  =(CaseDetails_LC_Hum.CaseModal)CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"Medicaid PAR Provider Not Accepting"}');           
            System.assertNotEquals(parObj.Id,Null);
            System.assertEquals(parObj.Value__c,'FL');

            parObj.Value__c = 'SC';
            update parObj;
            csobj  =(CaseDetails_LC_Hum.CaseModal)CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"Medicaid PAR Provider Not Accepting"}');           
        
            CaseDetails_LC_Hum.CheckCaseTemplates(caseRecord.id);
            
        }
    }

    /*
    * This method is used to test updateCaseQAA method
    * @param   None
    * @return  None
    */
    static testMethod void  updateCaseQAATest(){
        User usr = [Select Id,Name, Current_Queue__C from User where LastName='test CaseProcessUser'];
        Group oGrp = null;
        oGrp = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oGrp;

        QueueSObject oNewQueue = New QueueSObject(QueueId = oGrp.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;
        
        system.runAs(usr){
            
            Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c( Name = 'Test Business Process and Testing', OwnerId = oGrp.Id,Service_Center__c = 'Business',Department__c = 'Process and Testing');
            insert oWQS;

            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'Calls (RSO)';
            ctRec.Is_Active__c=true;
            insert ctRec;

            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Benefits';
            classificationRec.Is_Active__c = true;
            insert classificationRec;

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

            Product2 productRec = New Product2(
                Name = 'MED',ProductCode = 'MED',Description = '302 006',
                Alternate_Description__c = 'UNKNOWN', HDHP_Indicator__c = 'N', 
                Major_LOB__c = 'MER', Product_Type__c = 'MA',Segment_Type__c = 'MCD', 
                Sold_Product_Key_Value__c = '9881770101', Tenant_ID__c = '00'
            );
            insert productRec;

            PurchaserPlan policyObj = New PurchaserPlan();
            policyObj.Name = 'Policy New'; 
            policyObj.Major_LOB__c ='MCD';
            policyObj.Issue_State__c ='FL';            
            policyObj.ETL_Record_Deleted__c = false;
            insert policyObj;

            Policy__c policyRec = New Policy__c();
            policyRec.Group_Name__c = accountRec.Id;
            policyRec.Product__c = productRec.id;
            policyRec.Policy_Group_Number__c='11111';
            policyRec.Business_Segment__c='Individual-Medicare';
            insert policyRec;            
            
            MemberPlan mplan1 = new MemberPlan();
            mplan1.Name = 'test memberplan';
            mplan1.GroupNumber='';
            mplan1.MemberId = accountRec.Id;
            mplan1.planId=policyObj.Id;
            insert mplan1;

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
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Member_Plan_Id__c = mplan1.Id;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Origin = 'Inbound Call';
            insert caseRecord;
            
            classificationRec .Name='Complaints or Compliments';
            update classificationRec;
            intentRec.Name='QAA';            
            update intentRec;

            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.id;
            ctciRec.Intent__c = intentRec.id;
            update ctciRec;

            CaseDetails_LC_Hum.CaseModal csobj  =(CaseDetails_LC_Hum.CaseModal) CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"QAA Complaint"}');           
            CaseDetails_LC_Hum.isQAAProcessAttached(caseRecord,'Completed');
            
            mplan1.policy__c=policyRec.Id;
            update mplan1;
            csobj  =(CaseDetails_LC_Hum.CaseModal) CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"QAA Complaint"}');           
	        CaseDetails_LC_Hum.isQAAProcessAttached(caseRecord,'Completed');
            
            policyRec.Business_Segment__c='IndividualMedicare';
            update policyRec;
            mplan1.GroupNumber='123';
            update mplan1;
            System.assertNotEquals(mplan1.Id,Null);
            csobj  =(CaseDetails_LC_Hum.CaseModal) CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"QAA Complaint"}');           
            CaseDetails_LC_Hum.isQAAProcessAttached(caseRecord,'Completed');
            
            mplan1.GroupNumber='1232324';
            update mplan1;
            csobj  =(CaseDetails_LC_Hum.CaseModal) CaseDetails_LC_Hum.populateCaseData(caseRecord.Id, '{"callerPageName":"","flowName":"QAA Complaint"}');           
        	CaseDetails_LC_Hum.isQAAProcessAttached(caseRecord,'Completed');
        }
    }

    /**
* This method Is used to test the Process Changes
*
* @param     NULL
* @return    void 
*/
Static testMethod void testQSProcessChanges(){
        Group oNewGroup = null;
        oNewGroup = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oNewGroup;

        QueueSObject oNewQueue = New QueueSObject(QueueId = oNewGroup.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue; 

        User custCareSpecialist = HUMTestDataHelperTest.createTestUser('userMediareCase','Customer Care Specialist');
        insert custCareSpecialist;

        custCareSpecialist.Current_Queue__c = 'Test Business Process and Testing';
        update custCareSpecialist; 

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
			System.AssertNotEquals(memberAccountValid.Id,Null);

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
        
            Case caseRecordInp = New Case(Status = 'In Progress',AccountId = memberAccountValid.id,
            ctci_list__c = ctciValid.id, Complaint__c = 'Yes - Medicare Part C',Complaint_Reason__c = 'Other',
            Complaint_Type__c = 'Behavioral',DCN__c='12345678');
            insert caseRecordInp;

            CaseDetails_LC_Hum.getProcessDetails(caseRecordInp.Id,'Medicare Part D Redetermination');
			CaseDetails_LC_Hum.getProcessDetails(caseRecordInp.Id,'Medicare/Medicaid Expedited Appeal');
			CaseDetails_LC_Hum.getProcessDetails(caseRecordInp.Id,'Medicare Good Cause Reinstatement Form');
			CaseDetails_LC_Hum.getProcessDetails(caseRecordInp.Id,'Medicaid PAR Provider Not Accepting');
			CaseDetails_LC_Hum.getProcessDetails(caseRecordInp.Id,'Medicare Creditable Coverage Attestation');
			CaseDetails_LC_Hum.getProcessDetails(caseRecordInp.Id,'QAA Complaint');
	            CaseDetails_LC_Hum.getProcessDetails(caseRecordInp.Id,'PCP Update/Change');
	            CaseDetails_LC_Hum.getProcessDetails(caseRecordInp.Id,'PCP/PCD Questions');
	            CaseDetails_LC_Hum.getProcessDetails(caseRecordInp.Id,'Medicaid Newborn Notification');
	            CaseDetails_LC_Hum.getProcessDetails(caseRecordInp.Id,'Medicare and Medicaid Other Insurance Form');
            
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
            
            Classification_Type__c ctRec1 = new Classification_Type__c();
            ctRec1.name = 'Medicaid Calls';
            ctRec1.Is_Active__c=true;
            insert ctRec1; 
             
            Classification__c classificationRecDec2 = new Classification__c();
            classificationRecDec2.Name = 'Enrollment';
            classificationRecDec2.Is_Active__c = true;
            insert classificationRecDec2;

            Intent__c intentRecDec2 = New Intent__c();
            intentRecDec2.Name = 'Newborn';
            intentRecDec2.Is_Active__c = true;
            insert intentRecDec2;
                
            CTCI_Junction__c ctciRec4 = New CTCI_Junction__c();
            ctciRec4.Classification_Type__c = ctRec1.id;
            ctciRec4.Classification__c = classificationRecDec2.Id;
            ctciRec4.Intent__c = intentRecDec2.id;
            ctciRec4.IsActive__c = true;
            insert ctciRec4;

            Product2 productRec = New Product2(
                Name = 'MED',ProductCode = 'MED',Description = '302 006',
                Alternate_Description__c = 'UNKNOWN', HDHP_Indicator__c = 'N', 
                Major_LOB__c = 'MCD', Product_Type__c = '',Segment_Type__c = 'MCR', 
                Sold_Product_Key_Value__c = '9881770101', Tenant_ID__c = '00'
            );
            insert productRec;

            PurchaserPlan policyObj = New PurchaserPlan();
            policyObj.Policy_Group_Number__c='11111'; 
            policyObj.Name = 'Policy New'; 
            policyObj.source_cust_cov_key__c = '12345678901234567890' ; 
            policyObj.PayerId = accountRec.Id;
            policyObj.Major_LOB__c ='MCD';
            policyObj.Platform__c = 'EM';            
            policyObj.ETL_Record_Deleted__c = false;
            policyObj.product__c = productRec.Id;
            insert policyObj;

            MemberPlan memPlan = New MemberPlan(Name ='memplanQs',MemberId=accountRec.id,
            Member_Dependent_Code__c = 'H1234567890',Member_Id_base__c = 'test23456',GroupNumber = '672534',
            EffectiveFrom = Date.newInstance(2020, 1, 1), EffectiveTo = Date.newInstance(1999, 12, 31),
            PlanId = policyObj.Id, ETL_Record_Deleted__c = False);
            insert memPlan; 

            Case caseRecord1 = new Case();
            caseRecord1.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
            caseRecord1.Case_Owner__c = 'Louisville RSO Calls Support';
            caseRecord1.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord1.AccountId = accountRec.id;
            caseRecord1.ownerId = custCareSpec.Id;
            caseRecord1.Classification_Type__c = 'Medicaid Calls';
            caseRecord1.Classification_id__c=classificationRecDec2.id;
            caseRecord1.Intent_id__c = intentRecDec2.id;
            caseRecord1.Interacting_With__c = accountRec.id;
            caseRecord1.Interacting_With_Type__c = 'Member';   
            caseRecord1.Interacting_About_Type__c = 'Member';
            caseRecord1.Origin = 'Inbound Call';
            caseRecord1.member_Plan_id__c = memPlan.Id;
            caseRecord1.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert caseRecord1;

            Template__c babybot = new Template__c();
            babybot.Unique_Template_Name__c = 'Medicaid_Newborn_Notification';
            babybot.Description__c='Medicaid Newborn Notification';
            insert babybot;

            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord1.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;
                  
            Template_Field__c babybotField1 = new Template_Field__c();
            babybotField1.Name = 'MothersHumanaID';
            babybotField1.Template__c = babybot.id;            
            insert babybotField1;
            
            Template_Submission__c babybotSubmission1 = new Template_Submission__c();
            babybotSubmission1.Template__c = babybot.id;
            babybotSubmission1.Submission_Owner__c = tOnwer11.id;
            insert babybotSubmission1;
            System.assertNotEquals(babybotSubmission1.Id,Null);

            Template_Submission_Data__c babybitObj = new Template_Submission_Data__c();
            babybitObj.Template_Field__c = babybotField1.Id;
            babybitObj.Template_Submission__c = babybotSubmission1.id;
            babybitObj.Value__c = '12314';
            insert babybitObj;

            Test.startTest();
            CaseDetails_LC_Hum.updateNewbornNotification(caseRecord1.Id);
            CaseDetails_LC_Hum.isNewbornReq(caseRecord1.Id);
            Test.stopTest();         
        }
    }
     /**
* This method Is used to test the Medicare and medicaid template
*
* @param     NULL
* @return    void 
*/
Static testMethod void testMedicareOITemplate(){

    	Group oNewGroup = null;
        oNewGroup = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oNewGroup;

        QueueSObject oNewQueue = New QueueSObject(QueueId = oNewGroup.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue; 
    
        User custCareSpecialist = HUMTestDataHelperTest.createTestUser('userMediareCase','Customer Care Specialist');
        insert custCareSpecialist;

        User custCareSpecialist1 = HUMTestDataHelperTest.createTestUser('userMediareCase1','Customer Care Specialist');
        insert custCareSpecialist1;

        custCareSpecialist.Current_Queue__c = 'Test Business Process and Testing';
        update custCareSpecialist; 

        system.runAS(custCareSpecialist){
            
            test.startTest();
            Work_Queue_Setup__c qsWorkQueue = New Work_Queue_Setup__c(
            Name = 'Test Business Process and Testing', OwnerId = oNewGroup.Id, 
            Service_Center__c = 'Business',Department__c = 'Process and Testing');
            insert qsWorkQueue;           

            Classification_Type__c callsClassificationType = New Classification_Type__c(Name = 'Calls',Is_Active__c = true);  
            insert callsClassificationType;

            qsWorkQueue.Classification_Type__c = callsClassificationType.id;
            update qsWorkQueue;

            Classification__c classificationRec = New Classification__c(Name = 'COB',Is_Active__c = true);
            insert classificationRec;

            Intent__c intentRec = New Intent__c(Name = 'Medical Update/Dispute',Is_Active__c = true);                       
            insert intentRec;

            CTCI_Junction__c ctciValid = New CTCI_Junction__c(Classification_Type__c = callsClassificationType.id,
            Classification__c = classificationRec.Id, Intent__c = intentRec.id, IsActive__c = True);
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
        	
            Case caseRecord = New Case();
            caseRecord.Work_Queue_View_Name__c = 'Other';
            caseRecord.Owner_Queue__c = 'Test Business Process and Testing';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = memberAccountValid.id;
            caseRecord.ownerId = custCareSpecialist.Id;
            caseRecord.Classification_Type__c = 'Calls';
            caseRecord.Classification_id__c=classificationRec.id;
            caseRecord.Intent_id__c = intentRec.id;
            caseRecord.Interacting_With__c = memberAccountValid.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.member_Plan_id__c = memPlan.Id;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Origin = 'Inbound Call';
            insert caseRecord;
            
            productRec.Product_Type__c = '';
            productRec.Segment_Type__c = 'MCD';
            update productRec;
            policyObj.Major_LOB__c ='MCD';            
            update policyObj;

            Template__c parProvider = new Template__c();
            parProvider.Unique_Template_Name__c = '9FD171F0-D1F0';
            parProvider.Description__c='Medicare and Medicaid OI template';
            insert parProvider;

            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;
                  
            Template_Field__c parField1 = new Template_Field__c();
            parField1.Name = 'OIUpdateType';
            parField1.Template__c = parProvider.id;            
            insert parField1;
            
            Template_Submission__c parSubmission1 = new Template_Submission__c();
            parSubmission1.Template__c = parProvider.id;
            parSubmission1.Submission_Owner__c = tOnwer11.id;
            insert parSubmission1;
                
            Template_Submission_Data__c parObj = new Template_Submission_Data__c();
            parObj.Template_Field__c = parField1.Id;
            parObj.Template_Submission__c = parSubmission1.id;
            parObj.Value__c = 'RX Update/Dispute';
            insert parObj;
            
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form'); 
            
            parObj.Value__c = 'Newborn';
            update parObj;
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form'); 
           
            productRec.Product_Type__c = '';
            productRec.Segment_Type__c = 'MES';            
            update productRec; 
            policyObj.Major_Lob__c='MES';
            update policyObj;
            
            parObj.Value__c = 'RX Update/Dispute';
            update parObj;
            
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form'); 
            
            parObj.Value__c = 'Medical Update/Dispute';
            update parObj;
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form');
            
            parObj.Value__c = 'RX 090 Denial';
            update parObj;
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form'); 
            
            parObj.Value__c = 'Newborn';
            update parObj;
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form'); 
            CaseDetails_LC_Hum.CheckCaseTemplates(caseRecord.Id);
            
            parObj.Value__c = 'OI Prescription Drug Letter';
            update parObj;
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form'); 
            
            productRec.Product_Type__c = 'MA';
            productRec.Segment_Type__c = '';            
            update productRec; 
            policyObj.Major_Lob__c='MEP';
            update policyObj;
            
            parObj.Value__c = 'RX Update/Dispute';
            update parObj;
            
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form'); 
            
            parObj.Value__c = 'Medical Update/Dispute';
            update parObj;
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form');
            
            parObj.Value__c = 'RX 090 Denial';
            update parObj;
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form'); 
            
            parObj.Value__c = 'Newborn';
            update parObj;
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form'); 
            CaseDetails_LC_Hum.CheckCaseTemplates(caseRecord.Id);
            System.assertNotEquals(parObj.Id,Null);

            parObj.Value__c = 'OI Prescription Drug Letter';
            update parObj;
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'Medicare and Medicaid Other Insurance Form');
            
            CaseDetails_LC_Hum.CheckCaseTemplates(caseRecord.Id);
            
            test.stopTest();
            
        }
    }
    
    /**
* This method Is used to test the PCP Template
*
* @param     NULL
* @return    void 
*/
Static testMethod void testPCPemplate(){

    	Group oNewGroup = null;
        oNewGroup = New Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oNewGroup;

        QueueSObject oNewQueue = New QueueSObject(QueueId = oNewGroup.Id, SobjectType = 'Work_Queue_Setup__c');
        insert oNewQueue; 
    
        User custCareSpecialist = HUMTestDataHelperTest.createTestUser('userMediareCase','Customer Care Specialist');
        insert custCareSpecialist;

        User custCareSpecialist1 = HUMTestDataHelperTest.createTestUser('userMediareCase1','Customer Care Specialist');
        insert custCareSpecialist1;

        custCareSpecialist.Current_Queue__c = 'Test Business Process and Testing';
        update custCareSpecialist; 

        system.runAS(custCareSpecialist){
            
            test.startTest();
            Work_Queue_Setup__c qsWorkQueue = New Work_Queue_Setup__c(
            Name = 'Test Business Process and Testing', OwnerId = oNewGroup.Id, 
            Service_Center__c = 'Business',Department__c = 'Process and Testing');
            insert qsWorkQueue;           

            Classification_Type__c callsClassificationType = New Classification_Type__c(Name = 'Calls',Is_Active__c = true);  
            insert callsClassificationType;

            qsWorkQueue.Classification_Type__c = callsClassificationType.id;
            update qsWorkQueue;

            Classification__c classificationRec = New Classification__c(Name = 'COB',Is_Active__c = true);
            insert classificationRec;

            Intent__c intentRec = New Intent__c(Name = 'Medical Update/Dispute',Is_Active__c = true);                       
            insert intentRec;

            CTCI_Junction__c ctciValid = New CTCI_Junction__c(Classification_Type__c = callsClassificationType.id,
            Classification__c = classificationRec.Id, Intent__c = intentRec.id, IsActive__c = True);
            insert ctciValid;

            RecordType memberAccountType = [SELECT Id, Name, DeveloperName, SobjectType FROM RecordType
                WHERE SobjectType = 'Account' AND DeveloperName = 'Member']; 

            Account memberAccountValid = New Account(
                FirstName = 'medicareCase', LastName = 'member',RecordTypeId = memberAccountType.Id,
                CoKY_Account__c = False, Tenant_Id__c = '00',General_Account__c = true, NPI_ID__c = '182245',
                Humana_One_Indicator__c = False, Home_Office_Account__c = False, ETL_Record_Deleted__c = False
            );
            insert memberAccountValid;
			System.AssertNotEquals(memberAccountValid.Id,Null);
        	
            Case caseRecord = New Case();
            caseRecord.Work_Queue_View_Name__c = 'Other';
            caseRecord.Owner_Queue__c = 'Test Business Process and Testing';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = memberAccountValid.id;
            caseRecord.ownerId = custCareSpecialist.Id;
            caseRecord.Classification_Type__c = 'Calls';
            caseRecord.Classification_id__c=classificationRec.id;
            caseRecord.Intent_id__c = intentRec.id;
            caseRecord.Interacting_With__c = memberAccountValid.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Origin = 'Inbound Call';
            insert caseRecord;
            System.AssertNotEquals(caseRecord.Id,Null);
            
            Template__c parProvider = new Template__c();
            parProvider.Unique_Template_Name__c = 'd884d160-77c0-4d1b-95aa-edf8c81f3a59';
            parProvider.Description__c='PCP/PCD template';
            insert parProvider;

            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;
                  
            Template_Field__c parField1 = new Template_Field__c();
            parField1.Name = 'Did you receive a Service Fund edit when attempting to change the member\'s PCP';
            parField1.Template__c = parProvider.id;            
            insert parField1;
            
            Template_Submission__c parSubmission1 = new Template_Submission__c();
            parSubmission1.Template__c = parProvider.id;
            parSubmission1.Submission_Owner__c = tOnwer11.id;
            insert parSubmission1;
                
            Template_Submission_Data__c parObj = new Template_Submission_Data__c();
            parObj.Template_Field__c = parField1.Id;
            parObj.Template_Submission__c = parSubmission1.id;
            parObj.Value__c = 'Yes';
            insert parObj;
            
            CaseDetails_LC_Hum.getProcessDetails(caseRecord.Id,'PCP Update/Change'); 
            
            
            CaseDetails_LC_Hum.CheckCaseTemplates(caseRecord.Id);
            
            test.stopTest();
            
        }
    }
    /**
    * 
    *<p>
    * This method is used to cover checkIsCredentialing method on CaseDetails_LC_Hum Class.
    * @param            NULL
    * @return           void
    */
    static testMethod void testCredentialingEligible(){
        
        User credentialingSpec = HUMTestDataHelperTest.createTestUser('Credentialing','Customer Care Specialist');
        insert credentialingSpec;
        system.assertEquals(credentialingSpec.lastname=='test Credentialing',true);     
       
    
        system.runAS(credentialingSpec){
           
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = credentialingSpec.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);		
            
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'Provider';
            ctRec.Is_Active__c=true;
            insert ctRec; 
             
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Provider Requests';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;

            Intent__c intentRecDec = new Intent__c();
            intentRecDec.Name = 'LA Medicaid Cred Workflow';
            intentRecDec.Is_Active__c = true;
            insert intentRecDec;
                
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRecDec.Id;
            ctciRec.Intent__c = intentRecDec.id;
            ctciRec.IsActive__c = true;
            insert ctciRec;

            Case oCase = new Case(AccountId=accountRec.Id, 
                CTCI_List__c = ctciRec.Id, CoKY_Case__c = True,
                Interacting_With_Type__c = 'Member',Interacting_With__c=accountRec.Id, 
                Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), 
                Owner_Queue__c = 'Market National Network Ops', Work_Queue_View_Name__c='Cincinati CRU1',
                Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress',
                Tenant_Id__c = '00',Interacting_About_Type__c = 'Member');
            insert oCase;

            Task oTasknt = HUMTestDataHelperTest.getTaskRecord();
            oTasknt.WhatId = oCase.Id;
            oTasknt.Task_Owner__c = 'User1';
            oTasknt.Owner_Queue__c ='Market National Network Ops';            
            oTasknt.status = 'In Progress';
            oTasknt.Service_Center__c = 'Cincinnati';
            oTasknt.Department__c = 'CRU1';
            oTasknt.Callback_Requested__c = 'Yes';
            oTasknt.Time_Zone__c = 'EST - Eastern';
            oTasknt.Call_Back_Start__c = datetime.now().time();
            oTasknt.Call_Back_End__c= datetime.now().time();
            oTasknt.Description = 'Test Comments1';
            oTasknt.QueueOrUserId__c = credentialingSpec.Id;
            oTasknt.Type ='Notification Task';
            oTasknt.RecordTypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId();
            insert oTasknt;

            Test.startTest();   
            CaseDetails_LC_Hum.checkIsCredentialing(oCase.Id);
            Test.stopTest();

      }    
    }
    /**
    * 
    *<p>
    * This method is used to cover check isLegacyDeleteMemberOrPolicy method on CaseDetails_LC_Hum Class.
    * @param            NULL
    * @return           void
    */
    static testMethod void testLegacytDeletedMember(){
        
        User legacyDeleteSpec = HUMTestDataHelperTest.createTestUser('LegacyDelete','Customer Care Specialist');
        insert legacyDeleteSpec;
        system.assertEquals(legacyDeleteSpec.lastname=='test LegacyDelete',true);     
       
    
        system.runAS(legacyDeleteSpec){
           
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'Calls (RSO)';
            ctRec.Is_Active__c=true;
            insert ctRec; 
             
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Billing';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;

            Intent__c intentRecDec = new Intent__c();
            intentRecDec.Name = 'Info Given';
            intentRecDec.Is_Active__c = true;
            insert intentRecDec;
                
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRecDec.Id;
            ctciRec.Intent__c = intentRecDec.id;
            ctciRec.IsActive__c = true;
            insert ctciRec;

            List<Account> lstAccounts = New List<Account>();
            Account memberAccountValid = New Account(
                FirstName = 'legcyDelete', LastName = 'validaccount',RecordTypeId = AccountRecordType.Id,
                CoKY_Account__c = False, Tenant_Id__c = '00',General_Account__c = true, NPI_ID__c = '182245',
                Humana_One_Indicator__c = False, Home_Office_Account__c = False, ETL_Record_Deleted__c = False
            );
            lstAccounts.add(memberAccountValid);
            Account memberAccountInvalid = New Account(
                FirstName = 'legcyDelete', LastName = 'etldeletedaccount',RecordTypeId = AccountRecordType.Id,
                CoKY_Account__c = False, Tenant_Id__c = '00',General_Account__c = true,
                Humana_One_Indicator__c = False, Home_Office_Account__c = False, ETL_Record_Deleted__c = True
            );
            lstAccounts.add(memberAccountInvalid);            
            insert lstAccounts; 

            PurchaserPlan policyObj = New PurchaserPlan();
            policyObj.Policy_Group_Number__c='11111';
            policyObj.Name = 'Policy New';
            policyObj.source_cust_cov_key__c = '12345678901234567890' ; 
            policyObj.PayerId = memberAccountValid.Id;
            policyObj.Major_LOB__c ='EM';
            policyObj.Platform__c = 'EM';
            policyObj.ETL_Record_Deleted__c = false;
            insert policyObj;            

            List<MemberPlan> lstMemPlan = New List<MemberPlan>();

            MemberPlan memPlan = New MemberPlan(Name ='memplanQs',MemberId=memberAccountValid.id,
            Member_Dependent_Code__c = 'H1234567890',Member_Id_base__c = 'test23456',GroupNumber = '672534',
            EffectiveFrom = Date.newInstance(2020, 1, 1), EffectiveTo = Date.newInstance(1999, 12, 31),
            PlanId = policyObj.Id, ETL_Record_Deleted__c = False);
            lstMemPlan.add(memPlan);

            MemberPlan memPlanEtlDel = New MemberPlan(Name ='memplanQsEtlDel',MemberId=memberAccountValid.id,
            Member_Dependent_Code__c = 'H1234567890',Member_Id_base__c = 'test23456',GroupNumber = '672534',
            EffectiveFrom = Date.newInstance(2020, 1, 1), EffectiveTo = Date.newInstance(1999, 12, 31),
            PlanId = policyObj.Id, ETL_Record_Deleted__c = True);
            lstMemPlan.add(memPlanEtlDel);

            insert lstMemPlan;

            Case oCase = new Case(AccountId=memberAccountValid.Id, 
                CTCI_List__c = ctciRec.Id, CoKY_Case__c = True,
                Interacting_With_Type__c = 'Member',Interacting_With__c=memberAccountValid.Id, 
                Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), 
                Owner_Queue__c = 'Market National Network Ops', Work_Queue_View_Name__c='Cincinati CRU1',
                Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress',
                Tenant_Id__c = '00',Interacting_About_Type__c = 'Member');
            insert oCase;
            
            Case oCase1 = new Case(AccountId=memberAccountInvalid.Id, 
                CTCI_List__c = ctciRec.Id, CoKY_Case__c = True,
                Interacting_With_Type__c = 'Member',Interacting_With__c=memberAccountInvalid.Id, 
                Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), 
                Owner_Queue__c = 'Market National Network Ops', Work_Queue_View_Name__c='Cincinati CRU1',
                Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress',
                Tenant_Id__c = '00',Interacting_About_Type__c = 'Member');
            insert oCase1;

            Case oCase2 = new Case(AccountId=memberAccountValid.Id, 
                CTCI_List__c = ctciRec.Id, CoKY_Case__c = True,
                Interacting_With_Type__c = 'Member',Interacting_With__c=memberAccountValid.Id, 
                Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), 
                Owner_Queue__c = 'Market National Network Ops', Work_Queue_View_Name__c='Cincinati CRU1',
                Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress',
                Tenant_Id__c = '00',Interacting_About_Type__c = 'Member',Member_Plan_Id__c=lstMemPlan[1].Id);
            insert oCase2;

            Test.startTest();   
            CaseDetails_LC_Hum.isLegacyDeleteMemberOrPolicy(oCase.Id);
            CaseDetails_LC_Hum.isLegacyDeleteMemberOrPolicy(oCase1.Id);
            CaseDetails_LC_Hum.isLegacyDeleteMemberOrPolicy(oCase2.Id);
            Test.stopTest();
        }    
    }
}