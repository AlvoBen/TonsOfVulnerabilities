/*******************************************************************************************************************************
Apex Class Name : CaseTransfer_LT_HUM
Version         : 1.0
Created On      : 4/7/2022
Function        : This class is test class for CaseTransfer_LC_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0            Aakash Aggarwal                                    4/7/2022               	Original Version 
*    1.1            Prasuna Pattabhi                                    7/6/2022               Fixed account issue
*    1.2               Gowthami Thota                                    07/31/2023              Test Class Fix for UAT 
*    1.3            Gowthami Thota                                      09/04/2023              QAS Test Class Fix 09/08
*/
@isTest
Private Class CaseTransfer_LT_HUM
{   
    /**
* Test Setup method to create all the data required for testing.
*
* @param     NULL
* @return    void 
*/
    @testSetup static void setup() 
    {
        User oCurrentUser = HUMTestDataHelperTest.createTestUser('ChangeCaseOwnerUser1','Customer Care Specialist');
        oCurrentUser.Current_Queue__c = 'Cincinnati CRU1';
        insert oCurrentUser;
        System.assertEquals( oCurrentUser.Current_Queue__c,'Cincinnati CRU1');
        
        User oNonHomeOfficeUser = HUMTestDataHelperTest.createTestUser('ChangeCaseOwnerUser2','Customer Care Specialist');
        insert oNonHomeOfficeUser;
        System.assertEquals(oNonHomeOfficeUser.LastName,'test ChangeCaseOwnerUser2');
        
        User oRSOHPUser = HUMTestDataHelperTest.createTestUser('ChangeCaseOwnerUser3','Customer Care Supervisor');
        oRSOHPUser.Current_Queue__c = 'Louisville RSO Calls Support';
        insert oRSOHPUser;
        system.assertEquals(oRSOHPUser.lastname=='test ChangeCaseOwnerUser3',true);
        
        User pharmaUser = HUMTestDataHelperTest.createTestUser('PharmaUser','Humana Pharmacy Specialist');
        pharmaUser.Current_Queue__c = 'Cincinnati CRU1';
        insert pharmaUser;
        
        PermissionSet pRSOHP = [SELECT ID From PermissionSet WHERE Name = 'CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = oRSOHPUser.id, PermissionSetId = pRSOHP.id);
        //insert New PermissionSetAssignment(AssigneeId = pharmaUser.id, PermissionSetId = pRSOHP.id);
        
        Group oGrp = New Group(Name ='Cincinnati_CRU1',DeveloperName='Cincinnati_CRU1', Type='QUEUE');
        insert oGrp;
        System.assertEquals(oGrp.Name,'Cincinnati_CRU1');
        
        GroupMember gm = New GroupMember(); 
        gm.GroupId=oGrp.id;
        gm.UserOrGroupId = oCurrentUser.id;
        insert gm;
        User oUser = [Select Id, Name, email from User where Id=:gm.UserOrGroupId LIMIT 1];
        System.assertEquals(oUser.Name,'test ChangeCaseOwnerUser1');
        
        Group oGrp2 = New Group(Name ='Cincinnati_CRU_PGQ',DeveloperName='Cincinnati_CRU_PGQ', Type='QUEUE');
        insert oGrp2;
        System.assertEquals(oGrp2.Name,'Cincinnati_CRU_PGQ');
        
        GroupMember gm1 = New GroupMember(); 
        gm1.GroupId=oGrp2.id;
        gm1.UserOrGroupId = pharmaUser.id;
        insert gm1;
        PermissionSet rFPS = [SELECT ID From PermissionSet WHERE Name = 'CRM_RF_LouisvilleUser_Access' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = rFPS.id);
        
        QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;   
        System.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');                  
        
        System.runAs(oCurrentUser)
        {
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assertEquals(oAcc.Name,'TestAccount1');
            
            Classification_Type__c objcT = New Classification_Type__c();
            objcT.Name ='Calls';
            objcT.Is_Active__c = true;
            insert objcT;
            
            Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c(Name = oGrp.Name);
            oWQS.Service_Center__c ='Humana Pharmacy';
            oWQS.Department__c ='CRU1';
            oWQS.CokY__c =true;
            oWQS.HO_Medical__c =true;
            oWQS.TenantID__c ='00'; 
            oWQs.Public_Group_Name__c='Cincinnati_CRU1_PGQ';   
            oWQs.isActive__c = TRUE;               
            oWQS.ownerId = oGrp.Id;   
            oWQS.Classification_Type__c = objcT.id;         
            oWQS.Work_Queue_Guidance__c = 'test';
            
            insert oWQS;
            System.assertEquals(oWQS.TenantID__c ,'00');
            
            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Calls';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);          
            insert lstClassification;
            
            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            insert lstIntent;
            System.assertEquals(intentRec.Name == 'Filling', true);
            
            
            CTCI_Junction__c objCTCI = New CTCI_Junction__c();
            objCTCI.Classification__c=lstClassification[0].id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =lstIntent[0].id;
            objCTCI.IsActive__c=true;
            insert objCTCI ;
            System.assertNotEquals(Null,objCTCI.id);
            
            PurchaserPlan PolicyObjOI1 = new PurchaserPlan();
            PolicyObjOI1.Policy_Group_Number__c='11111';
            PolicyObjOI1.Name = 'Policy New';
            PolicyObjOI1.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObjOI1.PayerId = oAcc.Id;
            PolicyObjOI1.Major_LOB__c ='EM';
            PolicyObjOI1.Platform__c = 'EM';
            PolicyObjOI1.ETL_Record_Deleted__c = false;
            insert PolicyObjOI1;
            //Create Policy, Product and Policy Member Data
            Product2 p2 = new Product2(Name = 'VIS',ProductCode = 'VIS');
            insert p2;            
            //Assert the product data is created
            system.assert(p2.id != null);
            
            PurchaserPlan oPolicy = new PurchaserPlan(name = 'Test Policy', Major_LOB__c ='EM',PayerId = oAcc.Id,Product__c = p2.id,Policy_Group_Number__c='11111');
            insert oPolicy;  
            system.assert(oPolicy.id != null);
            
            MemberPlan MemberPlanObj = new MemberPlan();
            MemberPlanObj.Name='MemberPlan2';
            MemberPlanObj.MemberId=oAcc.id;
            MemberPlanObj.Member_Dependent_Code__c = '1234567890';
            MemberPlanObj.Member_Id_base__c = 'test23456';
            MemberPlanObj.GroupNumber = '737803';
            MemberPlanObj.PlanId= oPolicy.Id;
            //MemberPlanObj.Product__c = p2.Id;
            insert MemberPlanObj;
            //This is the case
            Case oCase = New Case(AccountId = oAcc.Id, CoKY_Case__c = True,
                                  Interacting_With_Type__c = 'Group', Interacting_About_Type__c = 'Group',Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                                  Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = oGrp.Name,
                                  Service_Center__c ='Cincinnati', Department__c='CRU1', status='In Progress');
            oCase.Complaint__c = 'Yes'                   ;
            oCase.Complaint_Reason__c = 'Claims';
            oCase.Classification_Type__c = 'Calls';
            oCase.CTCI_List__c = objCTCI.id;
            oCase.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
            oCase.G_A_Rights_Given__c ='No';
            oCase.Member_Plan_Id__c=MemberPlanObj.Id ;
            oCase.Created_By_Queue__c='Louisville RSO Calls Support';

            insert oCase; 
            
            // case history data creation
            
            
            Work_Queue_Topic__c owrkqtopic = New Work_Queue_Topic__c();
            owrkqtopic.Name = 'Work Queue Topic';
            insert owrkqtopic;
            System.assertEquals(owrkqtopic.Name,'Work Queue Topic');
            
            
            Work_Queue_Junction__c owrkqjunction = New Work_Queue_Junction__c();
            owrkqjunction.Name = 'Work Queue Junction';
            owrkqjunction.Work_Queue_Setup_Name__c = oWQS.id;
            owrkqjunction.Work_Queue_Topic_Name__c = owrkqtopic.id;
            owrkqjunction.Work_Queue_Topic_Guidance__c ='test';
            insert owrkqjunction;
            System.assertEquals(owrkqjunction.Name,'Work Queue Junction');
            
            
            List<Classification_Type__c> ctList= New list<Classification_Type__c>();
            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'calls';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);
            insert ctList;
            System.assertEquals(ctList.size(),1);
            
            Case caseRecord = New Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = oAcc.id;
            caseRecord.ownerId = oCurrentUser.Id;
            caseRecord.Classification_Type__c = 'calls';
            caseRecord.Classification_id__c=lstClassification[0].id;
            caseRecord.Intent_id__c = lstIntent[0].id;
            caseRecord.Interacting_With__c = oAcc.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Member_Plan_Id__c=MemberPlanObj.Id ;
            
            insert caseRecord;
            System.assertEquals(caseRecord.Status=='In Progress',true);     
            
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = oCurrentUser.Id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account2';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
            System.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
            Product2 prod4 = New Product2();
            prod4.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MAPD;
            prod4.ProductCode = GLOBAL_CONSTANT_HUM.MEDICARE_POLICY;
            prod4.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCR;
            prod4.Name = 'Prod4';
            insert prod4;
            System.assertEquals(prod4.Name=='Prod4',true);  
            
            
            Case caseRecordMed = New Case();
            caseRecordMed.Status = 'In Progress';
            caseRecordMed.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseRecordMed.AccountId = accountRec.id;
            caseRecordMed.ownerId = oCurrentUser.Id;
            caseRecordMed.ctci_list__c=objCTCI.id;
            caseRecordMed.Classification_Type__c = 'calls';
            caseRecordMed.Classification_id__c=lstClassification[0].id;
            caseRecordMed.Intent_id__c = lstIntent[0].id;
            caseRecordMed.Interacting_With__c = accountRec.id;
            caseRecordMed.Interacting_With_Type__c = 'Member';
            caseRecordMed.Autoroute_Status__c = 'Evaluate';
            caseRecordMed.Origin = 'Inbound Call';
            caseRecordMed.Line_of_Business__c = 'Medicare';
            caseRecordMed.status = 'In Progress';
            caseRecordMed.Interacting_About_Type__c = 'Member';
            caseRecordMed.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert caseRecordMed;
            System.assertEquals(caseRecordMed.Owner_Queue__c=='Louisville RSO Enrollment',true);
            
            Case caseRecordMHK = New Case();
            caseRecordMHK.Status = 'In Progress';
            caseRecordMHK.Owner_Queue__c = 'Cincinnati CRU1';
            caseRecordMHK.AccountId = accountRec.id;
            caseRecordMHK.ownerId = oCurrentUser.Id;
            caseRecordMHK.ctci_list__c=objCTCI.id;
            caseRecordMHK.Classification_Type__c = 'calls';
            caseRecordMHK.Classification_id__c=lstClassification[0].id;
            caseRecordMHK.Intent_id__c = lstIntent[0].id;
            caseRecordMHK.Interacting_With__c = accountRec.id;
            caseRecordMHK.Interacting_With_Type__c = 'Member';
            caseRecordMHK.Origin = 'Correspondence';
            caseRecordMHK.Type = 'Correspondence Inquiry';
            caseRecordMHK.General_Case__c = true;
            caseRecordMHK.Tenant_Id__c = '00';
            caseRecordMHK.G_A_Rights_Given__c = 'No';
            caseRecordMHK.Complaint__c = 'No';
            caseRecordMHK.Interacting_About_Type__c = 'Member';
            caseRecordMHK.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert caseRecordMHK;
            System.assertEquals(caseRecordMHK.Owner_Queue__c=='Cincinnati CRU1',true);
            
            Case caseRecordMHK1 = New Case();
            caseRecordMHK1.Status = 'In Progress';
            caseRecordMHK1.Owner_Queue__c = 'Cincinnati CRU1';
            caseRecordMHK1.AccountId = accountRec.id;
            caseRecordMHK1.ownerId = oCurrentUser.Id;
            caseRecordMHK1.ctci_list__c=objCTCI.id;
            caseRecordMHK1.Classification_Type__c = 'calls';
            caseRecordMHK1.Classification_id__c=lstClassification[0].id;
            caseRecordMHK1.Intent_id__c = lstIntent[0].id;
            caseRecordMHK1.Interacting_With__c = accountRec.id;
            caseRecordMHK1.Interacting_With_Type__c = 'Member';
            caseRecordMHK1.Origin = 'Correspondence';
            caseRecordMHK1.Type = 'MHK Dispute Task';
            caseRecordMHK1.General_Case__c = true;
            caseRecordMHK1.Tenant_Id__c = '00';
            caseRecordMHK1.G_A_Rights_Given__c = 'No';
            caseRecordMHK1.Complaint__c = 'No';
            caseRecordMHK1.Interacting_About_Type__c = 'Member';
            caseRecordMHK1.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert caseRecordMHK1;
            System.assertEquals(caseRecordMHK1.Owner_Queue__c=='Cincinnati CRU1',true);
            
            list<CaseCommentType__c> lstCCT = New list<CaseCommentType__c>();
            CaseCommentType__c oCCTIssue = New CaseCommentType__c(Name = GLOBAL_CONSTANT_HUM.MEDICARECALLS_ISSUE_HUM);
            lstCCT.add(oCCTIssue);
            
            CaseCommentType__c oCCTReso = New CaseCommentType__c(Name = GLOBAL_CONSTANT_HUM.MEDICARECALLS_RESO_HUM);
            lstCCT.add(oCCTReso);
            
            insert lstCCT;
            
            Case caseRecordMed1 = New Case();
            caseRecordMed1.Status = 'Pending - Unassigned';
            caseRecordMed1.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseRecordMed1.AccountId = accountRec.id;
            caseRecordMed1.ownerId = oCurrentUser.Id;
            caseRecordMed1.ctci_list__c=objCTCI.id;
            caseRecordMed1.Classification_Type__c = 'calls';
            caseRecordMed1.Classification_id__c=lstClassification[0].id;
            caseRecordMed1.Intent_id__c = lstIntent[0].id;
            caseRecordMed1.Interacting_With__c = accountRec.id;
            caseRecordMed1.Interacting_With_Type__c = 'Member';
            caseRecordMed1.Autoroute_Status__c = 'Evaluate';
            caseRecordMed1.Origin = 'Inbound Call';
            caseRecordMed1.Line_of_Business__c = 'Medicare';
            caseRecordMed1.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            caseRecordMed1.Interacting_About_Type__c = 'Member';
            insert caseRecordMed1;
            System.assertEquals(caseRecordMed1.Owner_Queue__c=='Louisville RSO Enrollment',true);
            
            list<CaseComment> lstCC = New list<CaseComment>();
            CaseComment oCC1 = New CaseComment(ParentId = caseRecordMed1.Id, CommentBody = 'TEST CASE COMMENT MEDI');
            lstCC.add(oCC1);
            CaseComment oCC2 = New CaseComment(ParentId = caseRecordMed1.Id, CommentBody = 'TEST CASE COMMENT MEDI RESOLUTION');
            lstCC.add(oCC2);
            CaseComment oCC3 = New CaseComment(ParentId = caseRecordMHK.Id, CommentBody = 'TEST CASE COMMENT for MHK');
            lstCC.add(oCC3);
            insert lstCC;
            System.assert(lstCC[0].CommentBody == 'TEST CASE COMMENT MEDI');
            
            list<CaseCommentType_Junction__c> lstCCTJ = New list<CaseCommentType_Junction__c>();
            CaseCommentType_Junction__c oCCTJ1 = New CaseCommentType_Junction__c(CaseCommentType__c = oCCTIssue.Id, CaseComment__c = oCC1.Id);
            lstCCTJ.add(oCCTJ1);
            CaseCommentType_Junction__c oCCTJ2 = New CaseCommentType_Junction__c(CaseCommentType__c = oCCTReso.Id, CaseComment__c = oCC2.Id);
            lstCCTJ.add(oCCTJ2);
            insert lstCCTJ;
            System.assert(lstCCTJ[0].CaseComment__c == lstCC[0].Id);
            
            Case_Transfer_Assist__c cta = new Case_Transfer_Assist__c();
            cta.CTCI_List__c = objCTCI.id;
            cta.Product__c = p2.Id;//prod4.id; 
            cta.Work_Queue_Setup__c = oWQS.id;
            cta.Is_Active__c = true;
            insert cta;
            //creating switch
            
        }
        
        System.runAs(oRSOHPUser)
        {
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount2';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assertEquals(oAcc.Name,'TestAccount2');
            
            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'HP Billing';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);          
            insert lstClassification;
            System.assertEquals(lstClassification[0].Name=='HP Billing',true);
            
            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Eligibility';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            insert lstIntent;
            System.assertEquals(intentRec.Name == 'Eligibility', true);
            
            Case caseRecord = new Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'Pending - Response';
            caseRecord.AccountId = oAcc.id;
            caseRecord.ownerId = oRSOHPUser.Id;
            caseRecord.Created_By_Queue__c='Louisville RSO Calls Support';
            caseRecord.Classification_Type__c = 'calls';
            caseRecord.Classification_id__c=lstClassification[0].id;
            caseRecord.Intent_id__c = lstIntent[0].id;
            caseRecord.Interacting_With__c = oAcc.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
            insert caseRecord;
            System.assertequals(caseRecord.Status,'Pending - Response');
            
            Case caseRecord1 = new Case();
            caseRecord1.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord1.Status = 'Pending - Response';
            caseRecord1.AccountId = oAcc.id;
            caseRecord1.ownerId = oRSOHPUser.Id;
            caseRecord1.Classification_Type__c = 'calls';
            caseRecord1.Classification_id__c=lstClassification[0].id;
            caseRecord1.Intent_id__c = lstIntent[0].id;
            caseRecord1.Interacting_With__c = oAcc.id;
            caseRecord1.Interacting_With_Type__c = 'Member';
            caseRecord1.Interacting_About_Type__c = 'Member';
            caseRecord1.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord1.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert caseRecord1;

           
                        
            System.assertequals(caseRecord1.Status,'Pending - Response');
        }
        
        System.runAs(pharmaUser)
        {
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount2';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assertEquals(oAcc.Name,'TestAccount2');
            
            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'HP';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);          
            insert lstClassification;
            System.assertEquals(lstClassification[0].Name=='HP',true);
            
            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Eligibility1';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            insert lstIntent;
            System.assertEquals(intentRec.Name == 'Eligibility1', true);
            Group oGrp3 = [Select id, Name, DeveloperName from Group where type = 'Queue' AND DeveloperName = :'Cincinnati_CRU_PGQ'];
            Work_Queue_Setup__c oWQS9 = New Work_Queue_Setup__c(Name = oGrp3.Name);
            oWQS9.Service_Center__c ='Humana Pharmacy';
            oWQS9.Department__c ='Home Office Quality Operations Compliance';
            oWQS9.General__c =false;
            oWQS9.HO_Medical__c =true;
            oWQS9.TenantID__c ='00';  
            oWQS9.Public_Group_Name__c = 'Cincinnati CRU PGQ';     
            insert oWQS9;
            
            Case caseRecord = new Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'Pending - Response';
            caseRecord.AccountId = oAcc.id;
            caseRecord.ownerId = pharmaUser.Id;
            caseRecord.Classification_Type__c = 'calls';
            caseRecord.Classification_id__c=lstClassification[0].id;
            caseRecord.Intent_id__c = lstIntent[0].id;
            caseRecord.Interacting_With__c = oAcc.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Service_Center__c ='Cincinnati'; 
            caseRecord.Department__c='CRU1';
            caseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
            insert caseRecord;
            System.assertequals(caseRecord.Status,'Pending - Response');        
            
            
        }
        
    }
    
    /**
* This method Is used to provide different service center And department conditions on changeowner of case record.
*
* @param     NULL
* @return    void 
*/
    Private Static testMethod void overallTestMethod()
    {      
        User oCurrentUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test ChangeCaseOwnerUser1' LIMIT 1];
        System.runAs(oCurrentUser)
        {       
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE Name='TestAccount1' LIMIT 1 ];
            Work_Queue_Junction__c owrkqjunction = [Select id from Work_Queue_Junction__c LIMIT 1];
            Group oGrp = [SELECT id, Name from Group WHERE Name='Cincinnati_CRU1'];
            QueuesObject oQueueObject = [SELECT id, QueueID, SobjectType  from QueuesObject WHERE QueueID = :oGrp.id limit 1];
            Work_Queue_Setup__c oWQS = [SELECT id, Name, Service_Center__c, Department__c, General__c, TenantID__c, HO_Medical__c from Work_Queue_Setup__c WHERE Name=:oGrp.Name limit 1];
            oCurrentUser.Current_Queue__c = oWQS.Name;
            update oCurrentUser;
            System.assertEquals(oCurrentUser.Current_Queue__c,oWQS.Name);
            
            Case_Transfer_Assist__c ctaRec = [SELECT Id, Work_Queue_Setup__r.Name,Is_Active__c,CTCI_List__c,Product__r.Name,Product__r.Major_LOB__c,
                                              Product__r.Product_Type__c,CTCI_List__r.IsActive__c, Work_Queue_Setup__r.IsActive__c FROM Case_Transfer_Assist__c];
            
            Case oCase = [SELECT id, AccountId, Tenant_Id__c , CTCI_List__c ,Service_Center__c ,Department__c, Member_Plan_ID__r.Product__c, Member_Plan_ID__r.Product_Type_Code__c   From Case WHERE AccountId=:oAcc.Id And Service_Center__c ='Cincinnati'and Department__c='CRU1' limit 1];
            oCase.Tenant_Id__c = '00';
            update oCase;
            String pageInput= '{"sCaseId":"'+ oCase.Id +'","bRSOFlag":true}';
            
            test.startTest();  
            
            CaseTransfer_LC_HUM.fetchCaseData(pageInput);
            
            test.stopTest(); 
        }
    } 
    
    
    Static testMethod void testOverAll(){
        
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test PharmaUser'];
        //User testUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test ChangeCaseOwnerUser1' LIMIT 1];
        Group oGrp1 = New Group(Name ='Cincinnati CRU1',DeveloperName='Cincinnati_CRU2', Type='QUEUE');
        insert oGrp1;
        QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp1.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;   
        System.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');  
        
        System.runAs(testUser)
        {
            Test.startTest();
            
            Group oGrp3 = [Select id, Name, DeveloperName from Group where type = 'Queue' AND DeveloperName = :'Cincinnati_CRU_PGQ'];
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE Name='TestAccount2' LIMIT 1 ];
            Work_Queue_Setup__c wQS = [Select id,Service_Center__c,Department__c,Work_Queue_Name__c,Public_Group_Name__c from Work_Queue_Setup__c where IsActive__c = true And Name =: oGrp3.Name Limit 1];
            Case oCase = [SELECT id, AccountId, Tenant_Id__c ,OwnerId, CTCI_List__c ,Service_Center__c ,Department__c, Member_Plan_ID__r.Product__c, Member_Plan_ID__r.Product_Type_Code__c ,Created_By_Queue__c  From Case WHERE AccountId=:oAcc.Id And Service_Center__c ='Cincinnati'and Department__c='CRU1' limit 1];
            CaseTransfer_LC_HUM.mapServCenDeptWorkQueueSetup.put(wQS.Service_Center__c + '#' + wQS.Department__c,wQS);
            CaseTransfer_LC_HUM.mapWorkQueueSetup = CaseTransfer_LD_HUM.getMapWorkQueueSetup();
            CaseTransfer_LC_HUM.setServiceCenterAndDepartment(oGrp3.Name);
            
            CaseTransfer_LC_HUM.InputParams obj = new CaseTransfer_LC_HUM.InputParams();
            obj.sCaseID = oCase.Id;
            obj.bRSOFlag = true;
            String str = JSON.serialize(obj);
            CaseTransfer_LC_HUM.sServiceCenter = 'Humana Pharmacy';
            CaseTransfer_LC_HUM.sDepartment = 'CRU1';
            String pageInput= '{"sCaseId":"'+ oCase.Id +'","bRSOFlag":true}';
            CaseTransfer_LC_HUM.fetchCaseData(str);
            CaseTransfer_LC_HUM.fetchTopics('Humana Pharmacy','CRU1',true,JSON.serialize(oCase));
            CaseTransfer_LC_HUM.reAssignToCreator(JSON.serialize(oCase));
            CaseTransfer_LC_HUM.reAssignToSender(JSON.serialize(oCase));

            CaseTransfer_LD_HUM.fetchLogCodeForBothHPCCS('Test', true, true);
            CaseTransfer_LD_HUM.fetchUser(testUser.id);
            Test.stopTest();
        }
    }
    
    
}