/*******************************************************************************************************************************
Apex Class Name :    CaseClose_LT_HUM
Version: 1.0
Created On      : 06/15/2022
Function        : Test class for CaseClose_LD_HUM And CaseClose_LC_HUM

Modification Log:    
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Gowthami 											    06/15/2022            		Original Version
*     1.1                    Gowthami Thota                                         07/31/2023              Test Class Fix for UAT 
*     1.2                    Gowthami Thota                                         09/04/2023              QAS Test classFix 09/08
******************************************************************************************************************************************************************************/


@isTest
private class CaseClose_LT_HUM {
    
/**
* Test Setup method to create all the data required for testing.
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
            oAcc.Enterprise_Id__c = '1000103';
            //oAcc.Name = 'TestGroup00';
            oAcc.Group_Number__c = '00';
            //oAcc.BillingState = 'Alabama';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assertEquals(oAcc.Name,'TestAccount1');
            system.debug('Account inserted'+oAcc.Name);
            
            Classification_Type__c objcT = New Classification_Type__c();
            objcT.Name ='Calls';
            objcT.Is_Active__c = true;
            insert objcT;
            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
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
           
            //This is the case
            Case oCase = New Case(AccountId = oAcc.Id, CoKY_Case__c = True,
                                  Interacting_With_Type__c = 'Group',Interacting_About_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                                  Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = oGrp.Name,
                                  Service_Center__c ='Cincinnati', Department__c='CRU1', status='In Progress');
            oCase.Complaint__c = 'Yes';
            oCase.Complaint_Reason__c = 'Claims';
            oCase.Complaint_Type__c = 'Behavioural';
            oCase.Classification_Type__c = 'Calls';
            oCase.CTCI_List__c = objCTCI.id;
            oCase.Classification_Id__c = lstClassification[0].id;
            oCase.Intent_Id__c = lstIntent[0].id;
            oCase.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
            oCase.G_A_Rights_Given__c ='No';
            oCase.Created_By_Queue__c='Louisville RSO Calls Support';
            insert oCase;
            
            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.Status = 'Closed';
            oTask.Subject = 'Test Task Record';
            oTask.WhatId = oCase.Id;
            oTask.ActivityDate= System.today();
            oTask.Type = 'Work Task';
            insert oTask;
            
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
            caseRecord.CTCI_List__c = objCTCI.id;
            caseRecord.Classification_id__c=lstClassification[0].id;
            caseRecord.Intent_id__c = lstIntent[0].id;
            caseRecord.Interacting_With__c = oAcc.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            insert caseRecord;
            System.assertEquals(caseRecord.Status=='In Progress',true); 
            
            Task oTask1 = HUMTestDataHelperTest.getTaskRecord();
            oTask1.Status = 'In Progress';
            oTask1.Subject = 'Test Task Record';
            oTask1.WhatId = caseRecord.Id;
            oTask1.ActivityDate= System.today();
            oTask1.Type = 'Work Task';
            insert oTask1;
            
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
            caseRecordMed.Interacting_About_Type__c = 'Member';
            caseRecordMed.Autoroute_Status__c = 'Evaluate';
            caseRecordMed.Origin = 'Inbound Call';
            caseRecordMed.Line_of_Business__c = 'Medicare';
            caseRecordMed.status = 'In Progress';
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
            //caseRecordMed1.Policy_Member__c = policyMemRecMed.Id;
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
         
            Case oCase = [SELECT id, AccountId,Status,Classification_Id__c,Intent_Id__c,G_A_Rights_Given__c,Complaint__c,CTCI_List__c ,Service_Center__c ,Department__c, Member_Plan_ID__r.Product__c, Member_Plan_ID__r.Product_Type_Code__c From Case WHERE Service_Center__c ='Cincinnati'and Department__c='CRU1' limit 1];
            Case caseRecord = [SELECT id, AccountId,Status,Classification_Id__c,Intent_Id__c,G_A_Rights_Given__c,Complaint__c,CTCI_List__c ,Service_Center__c ,Department__c, Member_Plan_ID__r.Product__c, Member_Plan_ID__r.Product_Type_Code__c From Case WHERE Interacting_About_Type__c = 'Member' limit 1];
            
            Humana_Pharmacy_Log_Code_HUM__mdt mdata = [Select id,Log_Code__c,Classification_Type__c from Humana_Pharmacy_Log_Code_HUM__mdt LIMIT 1];
            String pageInput= '{"sCaseId":"'+ oCase.Id +'","bRSOFlag":true}';
            String pageInput1 = '{"sCaseId":"","bRSOFlag":true}';
            String caseCloseInput = '{"Id":"'+oCase.Id +'","Status":"'+oCase.Status+'"}';
            system.debug('caseCloseInput' +caseCloseInput);
            String caseCloseInput1 = '{"sCaseId":"'+caseRecord.Id +'","Status":"'+caseRecord.Status+'","Complaint__c":"","G_A_Rights_Given__c":"","Classification_Id__c":"","Intent_Id__c":""}';
            
                test.startTest();  
                CaseClose_LC_HUM.CaseCloseWrapper wrap =  new CaseClose_LC_HUM.CaseCloseWrapper();
                CaseClose_LC_HUM.InputParams input =  new CaseClose_LC_HUM.InputParams();
                
                CaseClose_LC_HUM.fetchCaseData(pageInput);
                CaseClose_LC_HUM.fetchCaseData(pageInput1);
                CaseClose_LC_HUM.CaseCloseSave(oCase.Id);
                CaseClose_LC_HUM.CaseCloseSave(null);
                CaseClose_LC_HUM.CheckForOpenTaskCount(oCase.Id);
                CaseClose_LC_HUM.CheckForOpenTaskCount(caseRecord.Id);
                CaseClose_LD_HUM.fetchLogCodeForBothHPCCS('Test', true, true);
                CaseClose_LD_HUM.fetchLogCodeForBothHPCCS('Test', false, true);
                test.stopTest();
            try{
                CaseClose_LC_HUM.saveCaseClose(caseCloseInput1);
            }
            catch(Exception ex){
                HUMExceptionHelper.logErrors(ex, 'CaseClose_LT_HUM', 'overallTestMethod');
                
            }
        }
    } 

}