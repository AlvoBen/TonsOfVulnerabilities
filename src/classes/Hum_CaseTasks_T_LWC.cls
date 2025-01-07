/************************************************************************************************************************
Apex class Name    : Hum_CaseTasks_T_LWC
Version            : 1.0
Function           : Test class Hum_CaseTasks_LWC and CaseTaskView_DTO_HUM_LWC
Created On         : October 10 2016

 **************************************************************************************************************************/
@isTest
private class Hum_CaseTasks_T_LWC
{
    /**
    *  Test Setup method to create all the data required for testing.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    @testSetup static void setup()
    {
        User oCCSpecialistUser = HUMTestDataHelperTest.createTestUser('CCSpl','Customer Care Specialist');
        oCCSpecialistUser.Current_Queue__C = 'Cincinati CRU1';
        insert oCCSpecialistUser; 
        System.assertEquals(oCCSpecialistUser.LastName,'test CCSpl');
        
        User oCCSupervisorUser = HUMTestDataHelperTest.createTestUser('CCSup','Customer Care Supervisor');
        oCCSupervisorUser.Current_Queue__C = 'Cincinati CRU1';
        insert oCCSupervisorUser; 
        System.assertEquals(oCCSupervisorUser.LastName,'test CCSup');
        
        User oCCSupervisorUserP = HUMTestDataHelperTest.createTestUser('CCSupPS','Customer Care Supervisor');
        oCCSupervisorUserP.Current_Queue__C = 'Cincinati CRU1';
        insert oCCSupervisorUserP; 
        System.assertEquals(oCCSupervisorUserP.LastName,'test CCSupPS');
        
        PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name =:HumConstants_LWC.MEDICARECALLS_PERMISSIONSET_HUM LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = oCCSupervisorUserP.id, PermissionSetId = pCheck.id);
        
        PermissionSet pCheck1 =[SELECT ID From PermissionSet WHERE Name =:HumConstants_LWC.RSO_HP_PermissionSet_Name LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = oCCSupervisorUserP.id, PermissionSetId = pCheck1.id);
        
        User oAdminUser = HUMTestDataHelperTest.createTestUser('Admin','system Administrator');
        oAdminUser.Current_Queue__C = 'Cincinati CRU1';
        insert oAdminUser;
        System.assertEquals(oAdminUser.LastName,'test Admin');
        
        system.runAs(oAdminUser)
        {
            User oCCS = [ SELECT Id, UserName , Name FROM User WHERE LastName = 'test CCSpl' ];
            User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName = 'test CCSup'];
            User oCCSup1 = [ SELECT Id, UserName ,Name FROM User WHERE LastName = 'test CCSupPS'];
            List<RecordType> lstRecordTypeCaseIds = [SELECT Id  FROM RecordType WHERE Name = 'Member Case' LIMIT 1 ];
            id idMemberCaseRecordType = lstRecordTypeCaseIds[0].Id;
            
            List<RecordType> lstRecordTypeMemberIds = [SELECT Id  FROM RecordType WHERE Name = 'Member' LIMIT 1 ];
            id idMemberAccountRecordType = lstRecordTypeMemberIds[0].Id;
            
            Group oGroup = new Group(Type='Queue', Name='Cincinati CRU1');
            insert oGroup ;
            system.assertEquals(oGroup.Type, 'Queue', 'Value not matched');
            
            QueueSObject oQueue = new QueueSObject(SobjectType='Case', QueueId=oGroup.Id);
            insert oQueue;
            system.assertEquals(oQueue.SobjectType, 'Case', 'Value not matched');
            
            Group oGroup1 = new Group(Type='Regular', Name='PG Cincinati CRU1');
            insert oGroup1;
            system.assertEquals(oGroup1.Type, 'Regular', 'Value not matched');
            
            GroupMember oGroupMember1 = new GroupMember(GroupId = oGroup1.Id, UserOrGroupId = oCCS.Id);
            insert oGroupMember1;
            system.assertEquals(oGroupMember1.GroupId, oGroup1.Id, 'Value not matched');
            
            GroupMember oGroupMember2 = new GroupMember(GroupId = oGroup1.Id, UserOrGroupId = oCCSup.Id);
            insert oGroupMember2;
            system.assertEquals(oGroupMember2.GroupId, oGroup1.Id, 'Value not matched');
            
            GroupMember oGroupMember3 = new GroupMember(GroupId = oGroup.Id, UserOrGroupId = oCCS.Id);
            insert oGroupMember3;
            system.assertEquals(oGroupMember3.GroupId, oGroup.Id, 'Value not matched');
            
            GroupMember oGroupMember4 = new GroupMember(GroupId = oGroup.Id, UserOrGroupId = oCCSup.Id);
            insert oGroupMember4;
            system.assertEquals(oGroupMember4.GroupId, oGroup.Id, 'Value not matched');
            
            GroupMember oGroupMember5 = new GroupMember(GroupId = oGroup.Id, UserOrGroupId = oCCSup1.Id);
            insert oGroupMember5;
            system.assertEquals(oGroupMember5.GroupId, oGroup.Id, 'Value not matched');
            
            Account oAccount = new Account(FirstName='Test' , LastName='Account1', recordtypeid=idMemberAccountRecordType);
            insert oAccount;
            system.assertEquals(oAccount.FirstName,'Test','Value not matched');
            
            Account oAccount1 = new Account(FirstName='Test' , LastName='Account2', recordtypeid=idMemberAccountRecordType);
            insert oAccount1;
            system.assertEquals(oAccount1.FirstName,'Test','Value not matched');
            
            Group oGroup6 = new Group(Type='Queue', Name='Louisville Premium Payment Recovery');
            insert oGroup6 ;
            system.assertEquals(oGroup6.Type, 'Queue', 'Value not matched');
            
            QueueSObject oQueue6 = new QueueSObject(SobjectType='Case', QueueId=oGroup6.Id);
            insert oQueue6;
            system.assertEquals(oQueue6.SobjectType, 'Case', 'Value not matched');
            
            GroupMember oGroupMember6 = new GroupMember(GroupId = oGroup6.Id, UserOrGroupId = oCCSup1.Id);
            insert oGroupMember6;
            system.assertEquals(oGroupMember6.GroupId, oGroup6.Id, 'Value not matched');
            
            Group oGroup7 = new Group(Type='Regular', Name='LV GA and Correspondence Screening PGQ');
            insert oGroup7;
            system.assertEquals(oGroup7.Type, 'Regular', 'Value not matched');
            
            GroupMember oGroupMember7 = new GroupMember(GroupId = oGroup7.Id, UserOrGroupId = oCCSup1.Id);
            insert oGroupMember7;
            system.assertEquals(oGroupMember7.GroupId, oGroup7.Id, 'Value not matched');
            
            list<CaseCommentType__c> lstCCT = new list<CaseCommentType__c>();
            CaseCommentType__c oCCTIssue = new CaseCommentType__c(Name = HumConstants_LWC.MEDICARECALLS_ISSUE_HUM);
            lstCCT.add(oCCTIssue);
            CaseCommentType__c oCCTReso = new CaseCommentType__c(Name = HumConstants_LWC.MEDICARECALLS_RESO_HUM);
            lstCCT.add(oCCTReso);
            insert lstCCT;
            
            Product2 prod3 = New Product2();
            prod3.Product_Type__c = null;
            prod3.ProductCode = HumConstants_LWC.MEDICARE_POLICY;
            prod3.Product_Type__c = HumConstants_LWC.PRODUCT_TYPE_MA;
            prod3.Segment_Type__c = HumConstants_LWC.SEGMENT_TYPE_MCD;
            prod3.Name = 'Prod3';
            insert prod3;
            system.assertEquals(prod3.Name=='Prod3',true);
            
            Product2 prod2 = New Product2();
            prod2.Product_Type__c = 'PDP';
            prod2.ProductCode = HumConstants_LWC.MEDICARE_POLICY;
            prod2.Segment_Type__c = HumConstants_LWC.SEGMENT_TYPE_MCR;
            prod2.Name = 'Prod2';
            insert prod2;
            system.assertEquals(prod2.Name=='Prod2',true);
            
            
            Policy__c policyRec = New Policy__c();
            policyRec.Group_Name__c = oAccount.Id;
            policyRec.Product__c = prod3.Id;
            policyRec.Policy_Group_Number__c='11111';
            policyRec.Major_Lob__c = HumConstants_LWC.MAJOR_LOB_MER;
            insert policyRec;
            system.assertEquals(policyRec.Group_Name__c==oAccount.Id,true);
            
            Policy__c policyRec2 = New Policy__c();
            policyRec2.Group_Name__c = oAccount.Id;
            policyRec2.Product__c = prod2.Id;
            policyRec2.Policy_Group_Number__c='11111';
            policyRec2.Major_Lob__c = HumConstants_LWC.MAJOR_LOB_MER;
            insert policyRec2;
            system.assertEquals(policyRec2.Group_Name__c==oAccount.Id,true);
            
            Policy_Member__c policyMemRec = New Policy_Member__c();
            policyMemRec.Name = '12345';
            policyMemRec.Policy__c = policyRec.Id;
            policyMemRec.Member__c = oAccount.Id; 
            insert policyMemRec;
            
            Policy_Member__c policyMemRec2 = New Policy_Member__c();
            policyMemRec2.Name = '12345';
            policyMemRec2.Policy__c = policyRec2.Id;
            policyMemRec2.Member__c = oAccount.Id;
            insert policyMemRec2;
        
             CRMFunctionality_ONOFF_Switch__c crm2 = new CRMFunctionality_ONOFF_Switch__c();
            crm2.IsON__c =true;
            crm2.Name = '2578074';
            crm2.Scrum_Team__c = 'CRM Incredibles';
            crm2.Story_Title__c = 'T1PRJ0002980/PR00095219 - MF 5 - Supervisor Case Management';
            crm2.Release_Name__c = '10/08/2021';
            insert crm2;
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claimskk';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claimskk');
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Fillingkk';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Fillingkk');
            
            Classification_Type__c objcT = new Classification_Type__c();
            objcT.Name ='CallRSOpp';
            objcT.Is_Active__c = true;
            insert objcT;
            System.assertEquals(objcT.Name,'CallRSOpp');
            
            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec.id;
            objCTCI.IsActive__c=true;
            insert objCTCI ;
            system.assertNotEquals(Null,objCTCI.id);
            
            Classification__c classificationRec1 = New Classification__c();
            classificationRec1.Name = 'Billing';
            classificationRec1.Is_Active__c = true;
            insert classificationRec1;
            System.assertEquals(classificationRec1.Name,'Billing');
            
            Intent__c intentRec1 = New Intent__c();
            intentRec1.Name = 'Reinstatement Review';
            intentRec1.Is_Active__c = true;
            insert intentRec1;
            System.assertEquals(intentRec1.Name,'Reinstatement Review');
            
            Classification_Type__c objcT1 = new Classification_Type__c();
            objcT1.Name ='Guidance Center';
            objcT1.Is_Active__c = true;
            insert objcT1;
            System.assertEquals(objcT1.Name,'Guidance Center');
            
            CTCI_Junction__c objCTCI1 = new CTCI_Junction__c();
            objCTCI1.Classification__c=classificationRec1.id;
            objCTCI1.Classification_Type__c= objcT1.id;
            objCTCI1.Intent__c =intentRec1.id;
            objCTCI1.IsActive__c=true;
            insert objCTCI1 ;
            system.assertNotEquals(Null,objCTCI1.id);
            
            List<Case> lstCase = new List<Case>();
            Case oCase1 = new Case(ownerid=oGroup.Id,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today()+2,Follow_Up_Due_Date__c = Date.Today(),
                                   Case_Owner__c = 'Cincinati CRU1', Work_Queue_View_Name__c='other', Owner_Queue__c='Cincinati CRU1', status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType,Interacting_With_TYpe__c = 'Member',
                                   Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = objCTCI.Classification_Type__r.name , CTCI_List__c=objCTCI.id,
                                   G_A_Rights_Given__c = 'No', Complaint__c = 'Yes',
                                   Complaint_Reason__c = 'Customer Service Quality', Behavioral_Health__c = 'None');
            system.assertEquals(oCase1.Origin, 'Inbound Call', 'Value not matched');
            
            Case oCase2 = new Case(ownerid=oCCS.Id,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Follow_Up_Due_Date__c = Date.Today(), Due_Date__c=Date.Today(), Case_Owner__c=oCCS.Name, Work_Queue_View_Name__c='other' , Owner_Queue__c=oCCS.Name, status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType, Interacting_With_Type__c = 'Member', Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = objCTCI.Classification_Type__r.name, CTCI_List__c=objCTCI.id, G_A_Rights_Given__c = 'No', Complaint__c = 'Yes', Complaint_Reason__c = 'Customer Service Quality', Behavioral_Health__c = 'None');
            system.assertEquals(oCase2.Origin, 'Inbound Call', 'Value not matched');
            
            Case oCase3 = new Case(ownerid=oCCSup.Id,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id,Follow_Up_Due_Date__c = Date.Today(), Priority='High' ,Due_Date__c=Date.Today()+2, Case_Owner__c=oCCSup.Name, Work_Queue_View_Name__c='other' , Owner_Queue__c=oCCSup.Name, status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType, Interacting_With_Type__c = 'Member', Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c =objCTCI.Classification_Type__r.name, CTCI_List__c=objCTCI.id, G_A_Rights_Given__c = 'No', Complaint__c = 'Yes', Complaint_Reason__c = 'Customer Service Quality', Behavioral_Health__c = 'None');
            system.assertEquals(oCase3.Origin, 'Inbound Call', 'Value not matched');
            
             Case oCase4 = new Case(ownerid=oGroup.Id,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(),Follow_Up_Due_Date__c = Date.Today(),
                                   Case_Owner__c = 'Cincinati CRU1', Work_Queue_View_Name__c='other', Owner_Queue__c='Cincinati CRU1', status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType,  Interacting_With_TYpe__c = 'Member', 
                                   Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = objCTCI.Classification_Type__r.name, CTCI_List__c=objCTCI.id, 
                                   G_A_Rights_Given__c = 'No', Complaint__c = 'Yes', 
                                   Complaint_Reason__c = 'Customer Service Quality', Behavioral_Health__c = 'None');
            system.assertEquals(oCase4.Origin, 'Inbound Call', 'Value not matched');
            Case oCase5 = new Case(ownerid=oGroup.Id,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today()+2,Follow_Up_Due_Date__c = Date.Today(),
                                   Case_Owner__c = 'Cincinati CRU1', Work_Queue_View_Name__c='other', Owner_Queue__c='Cincinati CRU1', status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType,Interacting_With_TYpe__c = 'Member',
                                   Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = objCTCI.Classification_Type__r.name , CTCI_List__c=objCTCI.id,
                                   G_A_Rights_Given__c = 'No', Complaint__c = 'No',
                                   Behavioral_Health__c = 'None',Call_Benefit_Category__c = 'Medicare Part D');
            system.assertEquals(oCase5.Origin, 'Inbound Call', 'Value not matched');
            
            Case oCase6 = new Case(ownerid=oGroup.Id,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(),Follow_Up_Due_Date__c = Date.Today(),
                                   Case_Owner__c = 'Cincinati CRU1', Work_Queue_View_Name__c='other', Owner_Queue__c='Cincinati CRU1', status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType,Interacting_With_TYpe__c = 'Member',
                                   Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = objCTCI.Classification_Type__r.name , CTCI_List__c=objCTCI.id,
                                   G_A_Rights_Given__c = 'No', Complaint__c = 'No',
                                   Behavioral_Health__c = 'None',Policy_Member__c = policyMemRec.Id,Call_Benefit_Category__c = 'Medicare Part D');
            system.assertEquals(oCase6.Origin, 'Inbound Call', 'Value not matched');
            
            Case oCase7 = new Case(ownerid=oGroup.Id,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today()+2,Follow_Up_Due_Date__c = Date.Today(),
                                   Case_Owner__c ='Cincinati CRU1', Work_Queue_View_Name__c='other', Owner_Queue__c='Cincinati CRU1', status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType,Interacting_With_TYpe__c = 'Member',
                                   Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = objCTCI.Classification_Type__r.name , CTCI_List__c=objCTCI.id,
                                   G_A_Rights_Given__c = 'No', Complaint__c = 'No',
                                   Behavioral_Health__c = 'None',Policy_Member__c = policyMemRec.Id,Call_Benefit_Category__c = 'Medicare Part D');
            system.assertEquals(oCase7.Origin, 'Inbound Call', 'Value not matched');
            
            Case oCase8 = new Case(ownerid=oGroup.Id,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(),Follow_Up_Due_Date__c = Date.Today(),
                                   Case_Owner__c = HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER, Work_Queue_View_Name__c='other', Owner_Queue__c=HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER, status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType,Interacting_With_TYpe__c = 'Member',
                                   Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = objCTCI1.Classification_Type__r.name , CTCI_List__c=objCTCI1.id,
                                   G_A_Rights_Given__c = 'No', Complaint__c = 'No',
                                   Behavioral_Health__c = 'None',Policy_Member__c = policyMemRec2.Id,Call_Benefit_Category__c = 'Medicare Part D');
            system.assertEquals(oCase8.Origin, 'Inbound Call', 'Value not matched');
            
            Case oCase9 = new Case(ownerid=oGroup6.Id,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today()+2,Follow_Up_Due_Date__c = Date.Today(),
                                   Case_Owner__c = 'Louisville Premium Payment Recovery', Work_Queue_View_Name__c='other', Owner_Queue__c='Louisville Premium Payment Recovery', status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType,Interacting_With_TYpe__c = 'Member',
                                   Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = 'Guidance Center' , CTCI_List__c=objCTCI1.id,
                                   G_A_Rights_Given__c = 'No', Complaint__c = 'No',AutoRoute_Status__c = 'Completed',
                                   Behavioral_Health__c = 'None',Policy_Member__c = policyMemRec2.Id,Call_Benefit_Category__c = 'Medicare Part D');
            system.assertEquals(oCase9.Origin, 'Inbound Call', 'Value not matched');
            
            lstCase.add(oCase1);
            lstCase.add(oCase2);
            lstCase.add(oCase3);
            lstCase.add(oCase4);
            lstCase.add(oCase5);
            lstCase.add(oCase6);
            lstCase.add(oCase7);
            lstCase.add(oCase8);
            lstCase.add(oCase9);
            insert lstCase;
            system.assertEquals(lstCase.size(), 9 , 'Value not matched');
            
            
            list<CaseComment> lstCC1 = new list<CaseComment>();
            CaseComment oCC11 = new CaseComment(ParentId = oCase7.Id, CommentBody = 'CASE');
            lstCC1.add(oCC11);
            CaseComment oCC21 = new CaseComment(ParentId = oCase7.Id, CommentBody = 'COMMENT');
            lstCC1.add(oCC21);
            insert lstCC1;
            system.assert(lstCC1[0].CommentBody == 'CASE');
            
            list<CaseCommentType_Junction__c> lstCCTJ1 = new list<CaseCommentType_Junction__c>();
            CaseCommentType_Junction__c oCCTJ11 = new CaseCommentType_Junction__c(CaseCommentType__c = oCCTIssue.Id, CaseComment__c = oCC11.Id);
            lstCCTJ1.add(oCCTJ11);
            CaseCommentType_Junction__c oCCTJ21 = new CaseCommentType_Junction__c(CaseCommentType__c = oCCTReso.Id, CaseComment__c = oCC21.Id);
            lstCCTJ1.add(oCCTJ21);
            insert lstCCTJ1;
            system.assert(lstCCTJ1[0].CaseComment__c == lstCC1[0].Id);
            
            list<CaseComment> lstCC = new list<CaseComment>();
            CaseComment oCC1 = new CaseComment(ParentId = oCase7.Id, CommentBody = 'TEST CASE COMMENT MEDI');
            lstCC.add(oCC1);
            CaseComment oCC2 = new CaseComment(ParentId = oCase7.Id, CommentBody = 'TEST CASE COMMENT MEDI RESOLUTION');
            lstCC.add(oCC2);
            insert lstCC;
            system.assert(lstCC[0].CommentBody == 'TEST CASE COMMENT MEDI');
            
            list<CaseCommentType_Junction__c> lstCCTJ = new list<CaseCommentType_Junction__c>();
            CaseCommentType_Junction__c oCCTJ1 = new CaseCommentType_Junction__c(CaseCommentType__c = oCCTIssue.Id, CaseComment__c = oCC1.Id);
            lstCCTJ.add(oCCTJ1);
            CaseCommentType_Junction__c oCCTJ2 = new CaseCommentType_Junction__c(CaseCommentType__c = oCCTReso.Id, CaseComment__c = oCC2.Id);
            lstCCTJ.add(oCCTJ2);
            insert lstCCTJ;
            system.assert(lstCCTJ[0].CaseComment__c == lstCC[0].Id);
                    
            Case_Action__c cAction = new Case_Action__c(Action_version__c = 'v1',Active__c = true,Name='QAA');
            insert cAction;
            Case_Action_Association__c cActionAssociation = new Case_Action_Association__c(Case__c = oCase8.id,Case_Action__c = cAction.id);
            insert cActionAssociation;
            
            Template__c templateObj = New Template__c(Unique_Template_Name__c ='ATVF007',Description__c='Creditable Coverage Attestation');
            Insert templateObj;   
            
            //Insert Template submission owner
            Template_Submission_Owner__c ownerObj = New Template_Submission_Owner__c();
            ownerObj.Object_Owner_ID__c = oCase9.Id;
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
            
            Attachment_Log__c objLog = new Attachment_Log__c();
            objLog.case__c = oCase1.Id;
            insert objLog;
            Attachment_Log_Item__c objLI1 = new Attachment_Log_Item__c();
            objLI1.Attachment_Log__c = objLog.Id;
            objLI1.Name = 'Received Date';
            insert objLI1;
            
            Attachment_Log_Item__c objLI2 = new Attachment_Log_Item__c();
            objLI2.Attachment_Log__c = objLog.Id;
            objLI2.Name = 'Received Date1';
            objLI2.Value__c = 'Conduent';
            insert objLI2;
            
            Task oTask1 = new Task(WhatId=oCase1.Id,Type='Work Task', QueueOrUserId__c=oGroup.Id, OwnerId=oCCSup.Id, ActivityDate=Date.Today(), Work_Queue_View_Name__c='other' , Task_Owner__c='Cincinati CRU1', Owner_Queue__c='Cincinati CRU1', Status='In Progress', Priority='High');
            insert oTask1;
            system.assertEquals(oTask1.Type, 'Work Task' , 'Value not matched');
            
            Task oTask2 = new Task(WhatId=oCase1.Id,Type='Work Task', QueueOrUserId__c=oGroup.Id, OwnerId=oCCSup.Id, ActivityDate=Date.Today(), Work_Queue_View_Name__c='other' , Task_Owner__c='Cincinati CRU1', Owner_Queue__c='Cincinati CRU1', Status='In Progress', Priority='Normal');
            insert oTask2;
            Task oTask3 = new Task(WhatId=oCase1.Id,Type='Work Task', QueueOrUserId__c=oGroup.Id, OwnerId=oCCSup.Id, ActivityDate=Date.Today(), Work_Queue_View_Name__c='other' , Task_Owner__c='Cincinati CRU1', Owner_Queue__c='Cincinati CRU1', Status='In Progress', Priority='Normal');
            insert oTask3;
            system.assertEquals(oTask2.Type, 'Work Task' , 'Value not matched');
            
            Task_Field_History__c taskField = new Task_Field_History__c();
            taskField.SObjFieldValue__c = 'test';
            taskField.SObjID__c =oTask1.id;
            taskField.Created_At__c=date.today();
            taskField.SObjFieldName__c ='Description';
            insert taskField;
            system.assertEquals(taskField.SObjFieldValue__c, 'test' , 'Value not matched');
            
            HUMSearchLimit__c objLimit = new HUMSearchLimit__c();
            objlimit.name = 'CASEQUERYLIMIT';
            objlimit.Search_Limit__c=2500;
            insert objLimit;
            System.assertEquals(objLimit.Name,'CASEQUERYLIMIT');
            
            HUMSearchLimit__c objLimit1 = new HUMSearchLimit__c();
            objlimit1.name = 'TASKQUERYLIMIT';
            objlimit1.Search_Limit__c=2500;
            insert objLimit1;
            System.assertEquals(objLimit1.Name,'TASKQUERYLIMIT');
            
            HUMSearchLimit__c objLimit2 = new HUMSearchLimit__c();
            objLimit2.name = 'TEAMTASKQUERYLIMIT';
            objLimit2.Search_Limit__c=2500;
            insert objLimit2;
            System.assertEquals(objLimit2.Name,'TEAMTASKQUERYLIMIT');
            
            HUMSearchLimit__c objLimit3 = new HUMSearchLimit__c();
            objLimit3.name = 'TEAMCASEQUERYLIMIT';
            objLimit3.Search_Limit__c=2500;
            insert objLimit3;
            System.assertEquals(objLimit3.Name,'TEAMCASEQUERYLIMIT');
            
            List<Work_Queue_Setup__c> lstWQS = new List<Work_Queue_Setup__c>();
            Group oGrp=new Group(Name='Cincinnati_CRU1', Type='QUEUE');
            insert oGrp;
            System.assertEquals(oGrp.Name,'Cincinnati_CRU1','Value not matched');
            
            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject ;   
            System.assertEquals(oQueueObject.SobjectType,'Work_Queue_Setup__c','Value not matched');
            
            Work_Queue_Setup__c oWQSelector = new Work_Queue_Setup__c(Name = 'Cincinnati_CRU1');
            oWQSelector.Public_Group_Name__c = 'PG Cincinati CRU1';
            oWQSelector.IsActive__c  = true;
            oWQSelector.Service_Center__c = 'Cincinnati';
            oWQSelector.Department__c = 'CRU1';
            oWQSelector.General__c = true;
            oWQSelector.HO_Medical__c = true;
            oWQSelector.OwnerId = oGrp.id;
            oWQSelector.Classification_Type__c=objcT.id;
            lstWQS.add(oWQSelector);
            insert lstWQS;
            System.assertEquals(oWQSelector.Service_Center__c,'Cincinnati','Value not matched');
            
            List<Queue_View__c> lstQV = new List<Queue_View__c>();
            Queue_View__c oQueueView1= new Queue_View__c(Queue_View_Name__c='Other', IsActive__c  = true ,Work_Queue_Name__c=lstWQS[0].Id, Department__c='CRU1', Group_Name__c = 'PG Cincinati CRU1' ,Queue_Name__c='Cincinati CRU1', Service_Center__c='Cincinnati');
            System.assertEquals(oQueueView1.Queue_View_Name__c, 'Other', 'Value not matched');
            lstQV.add(oQueueView1);
            Queue_View__c oQueueView2= new Queue_View__c(Queue_View_Name__c='test',Work_Queue_Name__c=lstWQS[0].Id, Department__c='CRU1', IsActive__c  = true ,Group_Name__c = 'PG Cincinati CRU1' ,Queue_Name__c='Cincinati CRU1', Service_Center__c='Cincinnati');
            System.assertEquals(oQueueView2.Queue_View_Name__c, 'test', 'Value not matched');
            lstQV.add(oQueueView2);
            insert lstQV;
            system.assertEquals(lstQV.size(), 2 , 'Value not matched');
        }
    }
    
   
    /**
    * Test method to test Constructor of the class.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testALL()
    {
        User oCCSup = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName ='test CCSup'];
        System.assertEquals(oCCSup.LastName, 'test CCSup');
        system.runAs(oCCSup)
        {   
            Test.StartTest();
                Hum_CaseTasks_LWC.sViewCategorydd1 = 'ALL';
               Hum_CaseTasks_LWC.sSelectedAssign = HumConstants_LWC.TASKLISTVIEW_UNASSIGNED_HUM;
                Hum_CaseTasks_LWC.getWorkQueueSetupRecords();
                Hum_CaseTasks_LWC.getQueueViewRecords();
                Hum_CaseTasks_LWC.getInitialLoad();
                Hum_CaseTasks_LWC.getWorkQueueSelectedQueueView(new list<String>{'All other Queue Views','All PCI Reviews (Louisville Code Editing)','Other (Louisville Code Editing)'});
                
                List<Case> lstCases = [Select Id, AccountId, Policy_Member__c from case where Policy_Member__c != null];
                Hum_CaseTasks_LWC.accountTabId = lstCases[0].Id;
                Hum_CaseTasks_LWC.bPolicyAccount = True;
                Hum_CaseTasks_LWC.getPolicyAccountDetails();

                Hum_CaseTasks_LWC.sSelectedAssign = 'Assigned';
				Hum_CaseTasks_LWC.initializeWorkQueue();

                Hum_CaseTasks_LWC.sSelectedAssign = 'UnAssigned';
				Hum_CaseTasks_LWC.initializeWorkQueue();

                Hum_CaseTasks_LWC.sSelectedAssign = 'All';
				Hum_CaseTasks_LWC.initializeWorkQueue();

                Case_Action__c caseAction = new Case_Action__c();
                caseAction.Name = 'QAA';
                caseAction.Active__c = true;
                insert caseAction;

                Case_Action_Association__c caseActionAss = new Case_Action_Association__c();                
                caseActionAss.Case_Action__c = caseAction.id;
                caseActionAss.Case__c = lstCases[0].id;
                insert caseActionAss;
                List<string> lstCaseDTO = new List<string>();
                lstCaseDTO.add(lstCases[0].id);
                Hum_CaseTasks_LWC.checkQAA(lstCaseDTO);                
            
                MedicareCallsCaseComments_H_HUM_LWC oMedCalls = new MedicareCallsCaseComments_H_HUM_LWC();
                oMedCalls.checkMedicareCare('Inbound Call', 'MED', 'PDP','', 'MCR');
            Test.StopTest();
        }
    }
    
    /**
    * Test method to test Supervisor use cases.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testSupervisor()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSup'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Cincinati Calls'},new list<String>{'Cincinati CRU1'}, new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View (Cincinati Calls)','CC View (Cincinati Calls)'},new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View(Cincinati Calls)','CC View(Cincinati Calls)'},new list<String>{'Tasks'},new list<String>{'UnAssigned'},new list<String>{'All'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},new list<String>{oCCSup.id});
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =false;
            crm1.Name = '2578074';
            crm1.Scrum_Team__c = 'CRM Incredibles';
            crm1.Story_Title__c = 'T1PRJ0002980/PR00095219 - MF 5 - Supervisor Case Management';
            crm1.Release_Name__c = '10/08/2021';
            insert crm1;
            
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask1 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Cincinati Calls'},new list<String>{'Cincinati CRU1'}, new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View (Cincinati Calls)','CC View (Cincinati Calls)'},new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View(Cincinati Calls)','CC View(Cincinati Calls)'},new list<String>{'Tasks'},new list<String>{'UnAssigned'},new list<String>{'Home Office Dental'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},new list<String>{oCCSup.id});
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask2 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Cincinati Calls'},new list<String>{'Cincinati CRU1'}, new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View (Cincinati Calls)','CC View (Cincinati Calls)'},new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View(Cincinati Calls)','CC View(Cincinati Calls)'},new list<String>{'Tasks'},new list<String>{'UnAssigned'},new list<String>{'General'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},new list<String>{oCCSup.id});
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask3 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Cincinati Calls'},new list<String>{'Cincinati CRU1'}, new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View (Cincinati Calls)','CC View (Cincinati Calls)'},new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View(Cincinati Calls)','CC View(Cincinati Calls)'},new list<String>{'Tasks'},new list<String>{'UnAssigned'},new list<String>{'Home Office All Other'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},new list<String>{oCCSup.id});
           
            system.assert(lstCaseTask!= Null);
            Test.StopTest();
            
             List<CaseTaskView_DTO_HUM_LWC> lstCaseTask4 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Cincinati Calls'},new list<String>{'Cincinati CRU1'}, new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View (Cincinati Calls)','CC View (Cincinati Calls)'},new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View(Cincinati Calls)','CC View(Cincinati Calls)'},new list<String>{'Tasks'},new list<String>{'UnAssigned'},new list<String>{'General','Home Office Dental'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},new list<String>{oCCSup.id});
           List<CaseTaskView_DTO_HUM_LWC> lstCaseTask5 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Cincinati Calls'},new list<String>{'Cincinati CRU1'}, new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View (Cincinati Calls)','CC View (Cincinati Calls)'},new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View(Cincinati Calls)','CC View(Cincinati Calls)'},new list<String>{'Tasks'},new list<String>{'UnAssigned'},new list<String>{'General','Home Office All Other'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},new list<String>{oCCSup.id});
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask6 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Cincinati Calls'},new list<String>{'Cincinati CRU1'}, new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View (Cincinati Calls)','CC View (Cincinati Calls)'},new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View(Cincinati Calls)','CC View(Cincinati Calls)'},new list<String>{'Tasks'},new list<String>{'UnAssigned'},new list<String>{'Home Office Dental','Home Office All Other'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},new list<String>{oCCSup.id});
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask7 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Cincinati Calls'},new list<String>{'Cincinati CRU1'}, new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View (Cincinati Calls)','CC View (Cincinati Calls)'},new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)','View(Cincinati Calls)','CC View(Cincinati Calls)'},new list<String>{'Tasks'},new list<String>{'UnAssigned'},new list<String>{'General','Home Office Dental','Home Office All Other'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},new list<String>{oCCSup.id});
            
        }
    }
        
    /**
    * Test method to test AcceptSelected use cases.
    * <p>
    * @return   :    void
    * @param    :    NA
    */ 
    
    static testMethod void testAcceptSelected()
    {
        User oCCSup = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName = 'test CCSup'];
        User oCCSup1 = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName = 'test CCSupPS'];
     
        System.assertEquals(oCCSup.LastName, 'test CCSup');
        system.runAs(oCCSup)
        {   
            Test.StartTest();
           
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask = Hum_CaseTasks_LWC.applyFilter(true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'UnAssigned'},new list<String>{'All'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},new list<String>{oCCSup1.id});
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask1 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'All','Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'All','Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'All','Cases','Tasks'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'All'},new list<String>{'Greater Than 10 days'},null);
            lstCaseTask1.addall(Hum_CaseTasks_LWC.applyFilter( false, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'All'},new list<String>{'6 to 10 days'},new list<String>{oCCSup1.id}));
			  
            List<String> lstRecords = new List<String>();
            set<String> lstRecordsStatus = new set<String>();
            boolean queuePresent;
            map<string,CaseTaskView_DTO_HUM_LWC> mapCaseDTO = new  map<string,CaseTaskView_DTO_HUM_LWC>();
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask)
            {
                if(caseTAsk.sAssigned == HumConstants_LWC.TASKLISTVIEW_UNASSIGNED_HUM)
                {
                    lstRecords.add(caseTask.sCaseTaskId);
                    
                }
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                }
            }
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask1)
            {
                
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                   
                }
            }
            List<String> lstOfUnassignedRecords = new List<String>(lstRecordsStatus);
            String mapDTO;
            mapDTO=JSON.serialize(mapCaseDTO);			
            
            if(!lstRecords.isEmpty())
            {	
                Hum_CaseTasks_LWC.closeCase(lstOfUnassignedRecords,new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Cincinati CRU1'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'Assigned to me'},new list<String>{'All'}, 'Test Case Comment','Closed',mapDTO,new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
                Hum_CaseTasks_LWC.updateOwnerOfAssignedRecords(lstRecords,new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'All','Cases','Tasks'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'All'}, new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'});
             Hum_CaseTasks_LWC.updateOwnerOfAssignedRecords(lstRecords,new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Tasks'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'All'}, new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'});
                   
            }
            queuePresent = Hum_CaseTasks_LWC.checkUserQueue();
            System.assertEquals(queuePresent, true);
            Test.StopTest();
        }
       
    }
    
    /**
    * Test method to test CloseCase method
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testCloseCase()
    {
        User oCCSup1 = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName = 'test CCSupPS'];
        System.assertEquals(oCCSup1.LastName, 'test CCSupPS');
        system.runAs(oCCSup1)
        {   
            Test.StartTest();
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask = Hum_CaseTasks_LWC.applyFilter(true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'All'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},new list<String>{oCCSup1.id});
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask1 = Hum_CaseTasks_LWC.applyFilter( false, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Tasks'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'All'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
            lstCaseTask1.addall(Hum_CaseTasks_LWC.applyFilter( false, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'All'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'General'},new list<String>{'Upto 5 days'},new list<String>{oCCSup1.id}));
			    
            set<String> lstRecordsStatus = new set<String>();
            boolean queuePresent;
            map<string,CaseTaskView_DTO_HUM_LWC> mapCaseDTO = new  map<string,CaseTaskView_DTO_HUM_LWC>();
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask)
            {
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                }
            }
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask1)
            {
                
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                }
            }
            List<String> lstOfUnassignedRecords = new List<String>(lstRecordsStatus);
            String mapDTO;
            mapDTO=JSON.serialize(mapCaseDTO);
            
            if(!lstRecordsStatus.isEmpty())
            {
                Hum_CaseTasks_LWC.closeCase(lstOfUnassignedRecords,new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Cincinati CRU1'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'Assigned to me'},new list<String>{'All'}, 'Test Case Comment','Closed',mapDTO,new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
                Hum_CaseTasks_LWC.closeCase(lstOfUnassignedRecords,new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Cincinati CRU1'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'Assigned to me'},new list<String>{'All'}, '','Closed',mapDTO,new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);      
                Hum_CaseTasks_LWC.checkQAA(lstOfUnassignedRecords);
               
            }
            case caserec = [select RecordTypeId,id from case limit 1];
             case caserec1 = [select RecordTypeId,id from case where Case_Owner__c = 'Louisville Premium Payment Recovery' limit 1];
            case caserec2 = [select RecordTypeId,id from case where Case_Owner__c = :HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER limit 1];
            List<Case_Action_Association__c> lstCaseAction = [SELECT Id, Case__c, Name, Case__r.CaseNUmber, Case_Action__r.name, Action__c FROM Case_Action_Association__c where Case_Action__r.Name = :HumConstants_LWC.CASEACTIONLAUNCHCONTROLLER_Intent_HUM limit 1];
            
            CaseTaskView_H_HUM_LWC.checkProcess(caserec2.id,lstCaseAction);
            date d = Hum_CaseTasks_LWC.convertStringtoDate(Datetime.now().format('yyyy-MM-dd'));
            Policy__c policy = [SELECT Major_Lob__c, Policy__c.Product__c, Policy__c.Product__r.Product_Type__c, Policy__c.Product__r.Segment_Type__c, Policy__c.Product__r.ProductCode from Policy__c LIMIT 1];
            Boolean b = CaseTaskView_H_HUM_LWC.checkProductSegmentType(policy);
            List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c,Template_Submission__r.Template__r.Unique_Template_Name__c,Template_Field__r.Name FROM Template_Submission_Data__c limit 1];
            System.assertEquals(CaseTaskView_H_HUM_LWC.disableChangeOwnerCreditableCoverage(flowValue,HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER, true, 'In Progress', caserec.id,true),false);
            System.assertEquals(CaseTaskView_H_HUM_LWC.disableChangeOwnerCreditableCoverage(flowValue,HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER, true, 'In Progress', caserec1.id,false),false);
            Set<String> grpLst = new Set<String>();
            grpLst.add(HumConstants_LWC.Group_LV_Correspondence_Screening_PGQ);
             Set<String> grpLst1 = new Set<String>();
            grpLst1.add(HumConstants_LWC.Group_LV_Correspondence_Screening_PGQ);
            grpLst1.add(HumConstants_LWC.Group_LV_Correspondence_Screening_PGVE);
             grpLst1.add(HumConstants_LWC.Group_LV_Correspondence_Screening_PGVP);
            grpLst1.add(HumConstants_LWC.PCPGROUPNAME);
            grpLst1.add(HumConstants_LWC.GOODCAUSEGROUPNAME);
            boolean bMedExp = CaseTaskView_H_HUM_LWC.isMedExpProcessAttachedExp(grpLst, flowValue,HumConstants_LWC.WORK_QUEUE_LV_Screening, 'Completed', 'In Progress', caserec.id,true);
            System.assertEquals(bMedExp, false);
            boolean bMedExpDetail = CaseTaskView_H_HUM_LWC.isMedExpProcessAttachedExp(grpLst1, flowValue,HumConstants_LWC.WORK_QUEUE_LV_Screening, 'Completed', 'In Progress', caserec.id,false);
            System.assertEquals(bMedExpDetail , false);
            boolean bMedExpPartD = CaseTaskView_H_HUM_LWC.isMedExpProcessAttachedPartD(grpLst, flowValue,HumConstants_LWC.WORK_QUEUE_LV_Screening, 'Completed', 'In Progress', caserec.id,true);
            System.assertEquals(bMedExpPartD , false);
            boolean bMedExpPartDDetail = CaseTaskView_H_HUM_LWC.isMedExpProcessAttachedPartD(grpLst1, flowValue,HumConstants_LWC.WORK_QUEUE_LV_Screening, 'Completed', 'In Progress', caserec.id,false);
            System.assertEquals(bMedExpPartDDetail , false);
            boolean bPCP = CaseTaskView_H_HUM_LWC.isPCPProcessAttached(grpLst, flowValue,HumConstants_LWC.WORK_QUEUE_PCP_Assignment_Research, HumConstants_LWC.AUTOROUTING_PCP, 'In Progress', caserec.id,true);
            System.assertEquals(bPCP , false);
            boolean bPCPDetail = CaseTaskView_H_HUM_LWC.isPCPProcessAttached(grpLst1, flowValue,HumConstants_LWC.WORK_QUEUE_PCP_Assignment_Research, HumConstants_LWC.HUM_COMPLETED, 'In Progress', caserec.id,false);
            System.assertEquals(bPCPDetail , false);
            boolean bGoodCause = CaseTaskView_H_HUM_LWC.isMedExpProcessAttachedGoodCause(grpLst, flowValue,HumConstants_LWC.GOODCAUSEQUEUE, HumConstants_LWC.HUM_COMPLETED, 'In Progress', caserec.id,true);
            System.assertEquals(bGoodCause , false);
            boolean bGoodCauseDetail = CaseTaskView_H_HUM_LWC.isMedExpProcessAttachedGoodCause(grpLst1, flowValue,HumConstants_LWC.GOODCAUSEQUEUE, HumConstants_LWC.HUM_COMPLETED, 'In Progress', caserec.id,false);
            System.assertEquals(bGoodCauseDetail , false);
            boolean bcheckDetailPageClose = CaseTaskView_H_HUM_LWC.checkDetailPageClose(flowValue,HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER, false,grpLst,HumConstants_LWC.WORK_QUEUE_PCP_Assignment_Research, 'Completed', 'In Progress', caserec.id, caserec.RecordTypeId);
            System.assertEquals(bcheckDetailPageClose , false);
            boolean bcheckEditPageClose = CaseTaskView_H_HUM_LWC.checkEditPageClose(flowValue,HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER, true,grpLst,HumConstants_LWC.WORK_QUEUE_LV_Screening, 'Evaluate Expedited Appeal', 'In Progress', caserec.id);
            System.assertEquals(bcheckEditPageClose, false);
            boolean bcheckRecordType =CaseTaskView_H_HUM_LWC.checkRecordType('Provider Case', 'Provider');
            System.assertEquals(bcheckRecordType, true);
            boolean bcheckRecordType1 =CaseTaskView_H_HUM_LWC.checkRecordType('Agent/Broker Case', 'Agent/Broker');
            System.assertEquals(bcheckRecordType1, false);
            boolean bcheckEditPageClose1 = CaseTaskView_H_HUM_LWC.checkEditPageClose(flowValue,HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER, true,grpLst,HumConstants_LWC.GOODCAUSEQUEUE, 'Evaluate Expedited Appeal', 'In Progress', caserec.id);
            System.assertEquals(bcheckEditPageClose1, false);
           
            Test.StopTest();
        }
     }
     
    /**
    * Test method to test changeCaseOwner method
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testTransferCase()
    {
        User oCCSup1 = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName = 'test CCSupPS'];
        System.assertEquals(oCCSup1.LastName, 'test CCSupPS');
        system.runAs(oCCSup1)
        {   
            Test.StartTest();
             List<CaseTaskView_DTO_HUM_LWC> lstCaseTask = Hum_CaseTasks_LWC.applyFilter(true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'All'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
             List<CaseTaskView_DTO_HUM_LWC> lstCaseTask1 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'All','Cases','Tasks'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'General', 'Home Office Dental', 'Home Office All Other'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
			 lstCaseTask1.addall(Hum_CaseTasks_LWC.applyFilter( false, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Tasks'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'Home Office Dental', 'Home Office All Other', 'Contract Protected Data'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null));
               
            set<String> lstRecordsStatus = new set<String>();
            boolean queuePresent;
            map<string,CaseTaskView_DTO_HUM_LWC> mapCaseDTO = new  map<string,CaseTaskView_DTO_HUM_LWC>();
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask)
            {
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                }
            }
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask1)
            {
                
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                }
            }
             List<String> lstOfUnassignedRecords = new List<String>(lstRecordsStatus);
             
            if(!lstRecordsStatus.isEmpty())
            {
                Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
                oWQS = [Select Name,id,Classification_Type__r.Name,Classification_Type__c, CokY__c, Department__c, DST__c, General__c, HO_Dental__c, HO_Medical__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId from Work_Queue_Setup__c limit 1];
                String JsonMap;
                JsonMap = JSON.serialize(oWQS);
                String mapDTO;
                mapDTO=JSON.serialize(mapCaseDTO);
                Hum_CaseTasks_LWC.changeCaseOwner(new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'Assigned to me'},new list<String>{'Home Office Dental'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},lstOfUnassignedRecords,mapDTO,'Test Case Comment','Dental','Calls','',oCCSup1.Name,oCCSup1.Id,'Transferred Per Process',JsonMap,null);
                Hum_CaseTasks_LWC.changeCaseOwner(new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'Assigned to me'},new list<String>{'Home Office All Other'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},lstOfUnassignedRecords,mapDTO,'','Dental','Calls','','','','Transferred Per Process',JsonMap,null);
               
				}
             
            Test.StopTest();
        }
    }

        /**
    * Test method to test changeCaseOwner method
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testTransferCaseGeneralMedicalDental()
    {
        User oCCSup1 = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName = 'test CCSupPS'];
        System.assertEquals(oCCSup1.LastName, 'test CCSupPS');
        system.runAs(oCCSup1)
        {   
            Test.StartTest();
            
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask = Hum_CaseTasks_LWC.applyFilter(true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'General','Home Office Dental','Home Office All Other'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask1 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'All','Cases','Tasks'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'General','Home Office Dental'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
            lstCaseTask1.addall(Hum_CaseTasks_LWC.applyFilter( false, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'All'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null));
            
            set<String> lstRecordsStatus = new set<String>();
            boolean queuePresent;
            map<string,CaseTaskView_DTO_HUM_LWC> mapCaseDTO = new  map<string,CaseTaskView_DTO_HUM_LWC>();
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask)
            {
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                }
            }
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask1)
            {
                
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                }
            }
            List<String> lstOfUnassignedRecords = new List<String>(lstRecordsStatus);
                
            if(!lstRecordsStatus.isEmpty())
            {
                Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
                oWQS = [Select Name,id,Classification_Type__r.Name,Classification_Type__c, CokY__c, Department__c, DST__c, General__c, HO_Dental__c, HO_Medical__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId from Work_Queue_Setup__c limit 1];
                String JsonMap;
                JsonMap = JSON.serialize(oWQS);
                
                String mapDTO;
                mapDTO=JSON.serialize(mapCaseDTO);
                Hum_CaseTasks_LWC.changeCaseOwner(new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'Assigned to me'},new list<String>{'General','Home Office All Other'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},lstOfUnassignedRecords,mapDTO,'Test Case Comment','Dental','Calls','',oCCSup1.Name,oCCSup1.Id,'Transferred Per Process',JsonMap,null);
                Hum_CaseTasks_LWC.changeCaseOwner(new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'Assigned to me'},new list<String>{'Home Office Dental','Home Office All Other'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},lstOfUnassignedRecords,mapDTO,'','Dental','Calls','','','','Transferred Per Process',JsonMap,null);
                
            }
            Test.StopTest();
        }
    }
    
    /**
    * Test method to test changeCaseOwner method
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testTransferCaseContractGeneralMedicalDental()
    {
        User oCCSup1 = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName = 'test CCSupPS'];
        System.assertEquals(oCCSup1.LastName, 'test CCSupPS');
        system.runAs(oCCSup1)
        {   
            Test.StartTest();
          
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask = Hum_CaseTasks_LWC.applyFilter(true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'General', 'Contract Protected Data'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask1 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'All','Cases','Tasks'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'General','Home Office All Other', 'Contract Protected Data'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
            lstCaseTask1.addall(Hum_CaseTasks_LWC.applyFilter( false, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'General','Home Office Dental','Home Office All Other', 'Contract Protected Data'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null));
            
            set<String> lstRecordsStatus = new set<String>();
            boolean queuePresent;
            map<string,CaseTaskView_DTO_HUM_LWC> mapCaseDTO = new  map<string,CaseTaskView_DTO_HUM_LWC>();
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask)
            {
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                }
            }
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask1)
            {
                
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                }
            }
            List<String> lstOfUnassignedRecords = new List<String>(lstRecordsStatus);
               
            if(!lstRecordsStatus.isEmpty())
            {
                 Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
                oWQS = [Select Name,id,Classification_Type__r.Name,Classification_Type__c, CokY__c, Department__c, DST__c, General__c, HO_Dental__c, HO_Medical__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId from Work_Queue_Setup__c limit 1];
                String JsonMap;
                JsonMap = JSON.serialize(oWQS);
                
                String mapDTO;
                mapDTO=JSON.serialize(mapCaseDTO);
                Hum_CaseTasks_LWC.changeCaseOwner(new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'Assigned to me'},new list<String>{'Contract Protected Data'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},lstOfUnassignedRecords,mapDTO,'Test Case Comment','Dental','Calls','',oCCSup1.Name,oCCSup1.Id,'Transferred Per Process',JsonMap,null);
                Hum_CaseTasks_LWC.changeCaseOwner(new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'Assigned to me'},new list<String>{'General','Contract Protected Data'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},lstOfUnassignedRecords,mapDTO,'','Dental','Calls','','','','Transferred Per Process',JsonMap,null);
               
            }
            Test.StopTest();
        }
    }
    
    /**
    * Test method to test changeCaseOwner method
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testTransferCaseContractcombination()
    {
        User oCCSup1 = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName = 'test CCSupPS'];
        System.assertEquals(oCCSup1.LastName, 'test CCSupPS');
        system.runAs(oCCSup1)
        {   
            Test.StartTest();
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask = Hum_CaseTasks_LWC.applyFilter(true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'Home Office Dental','Contract Protected Data'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask1 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'All','Cases','Tasks'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'Home Office All Other', 'Contract Protected Data'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
            lstCaseTask1.addall(Hum_CaseTasks_LWC.applyFilter( false, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'General','Home Office Dental','Contract Protected Data'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null));			   
            
            set<String> lstRecordsStatus = new set<String>();
            boolean queuePresent;
            map<string,CaseTaskView_DTO_HUM_LWC> mapCaseDTO = new  map<string,CaseTaskView_DTO_HUM_LWC>();
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask)
            {
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                }
            }
            for(CaseTaskView_DTO_HUM_LWC caseTask : lstCaseTask1)
            {
                
                if(caseTask.sCaseTaskId.substring(0,3) == '500')
                {
                lstRecordsStatus.add(caseTask.sCaseTaskId);
                mapCaseDTO.put(caseTask.sCaseTaskId, caseTask);
                }
            }
            List<String> lstOfUnassignedRecords = new List<String>(lstRecordsStatus);
            if(!lstRecordsStatus.isEmpty())
            {
                Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
                oWQS = [Select Name,id,Classification_Type__r.Name,Classification_Type__c, CokY__c, Department__c, DST__c, General__c, HO_Dental__c, HO_Medical__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId from Work_Queue_Setup__c limit 1];
                String JsonMap;
                JsonMap = JSON.serialize(oWQS);
                
                String mapDTO;
                mapDTO=JSON.serialize(mapCaseDTO);
                Hum_CaseTasks_LWC.changeCaseOwner(new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'Assigned to me'},new list<String>{'Home Office Dental', 'Home Office All Other', 'Contract Protected Data'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},lstOfUnassignedRecords,mapDTO,'Test Case Comment','Dental','Calls','',oCCSup1.Name,oCCSup1.Id,'Transferred Per Process',JsonMap,null);
                Hum_CaseTasks_LWC.changeCaseOwner(new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'Assigned to me'},new list<String>{'General', 'Home Office All Other', 'Contract Protected Data'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},lstOfUnassignedRecords,mapDTO,'','Dental','Calls','','','','Transferred Per Process',JsonMap,null);
            }
            Test.StopTest();
        }
    }
    
    
    /**
    * Test method to test specialist use cases.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testSpecialist()
    {
        User oCCSup = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName ='test CCSpl'];
        System.assertEquals(oCCSup.LastName, 'test CCSpl');
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            Boolean bperformFilter = false;
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask = Hum_CaseTasks_LWC.applyFilter( false, new list<String>{'Cincinati CRU1'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)'},new list<String>{'Other (Cincinati CRU1)'},new list<String>{'Tasks'},new list<String>{'Assigned to me'},new list<String>{'All'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
			
            Test.StopTest();
        }
    }
    
    /**
    * Test method to test specialist use cases.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testSpecialistMultipleQueuesCaseFilter()
    {
        User oCCSup = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName ='test CCSpl'];
        System.assertEquals(oCCSup.LastName, 'test CCSpl');
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask = Hum_CaseTasks_LWC.applyFilter(true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'All'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask1 = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'All','Cases','Tasks'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'All'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
		   lstCaseTask1.addall(Hum_CaseTasks_LWC.applyFilter( false, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Other (Cincinati CRU1)','Other (Louisville Premium Payment Recovery)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'All'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null));
           
            Test.StopTest();
        }
    }
    
    /**
    * Test method to test specialist use cases.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testSpecialistMultipleQueuesAllFilter()
    {
        User oCCSup = [ SELECT Id, UserName ,Name,LastName FROM User WHERE LastName ='test CCSpl'];
        System.assertEquals(oCCSup.LastName, 'test CCSpl');
        system.runAs(oCCSup)
        {   
            Test.StartTest();
                  List<CaseTaskView_DTO_HUM_LWC> lstCaseTask = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)'},new list<String>{'Other(Cincinati CRU1)','Other(Cincinati Calls)'},new list<String>{'All','Cases','Tasks'},new list<String>{'Assigned to me'},new list<String>{'General'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
          Test.StopTest();
        }
    }
    
    /**
    * Test method to test specialist use cases.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testSpecialistMultipleQueuesTaskFilter()
    {
        User oCCSup = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName ='test CCSpl'];
        System.assertEquals(oCCSup.LastName, 'test CCSpl');
        system.runAs(oCCSup)
        {   
            Test.StartTest();
           
            List<CaseTaskView_DTO_HUM_LWC> lstCaseTask = Hum_CaseTasks_LWC.applyFilter( true, new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'},new list<String>{'Cincinati CRU1','Louisville Premium Payment Recovery'}, new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)'},new list<String>{'Other (Cincinati CRU1)','Other (Cincinati Calls)'},new list<String>{'Cases'},new list<String>{'All','Assigned to me','UnAssigned'},new list<String>{'General'},new list<String>{'All','Greater Than 10 days','6 to 10 days','Upto 5 days'},null);
            
            Test.StopTest();
        }
    }
    
     /**
    * Test method to test Pull WOrk with Priority -Normal Only .
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testPullWorkPriorityNormal()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSpl'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            List<String> lstQueue = new List<String>();
            lstQueue.add('Cincinati CRU1');
            List<String> lstView = new List<String>();
            lstView.add('Other (Cincinati CRU1)');
            List<CaseTaskView_DTO_HUM_LWC> pulledWork = Hum_CaseTasks_LWC.pullWorkItems(lstQueue , lstView , HumConstants_LWC.TASKLISTVIEW_PRIORITYNORMALONLY_HUM,new List<String>{'All','Cases','Tasks'});
            system.assert(pulledWork.size() > 0);
            Test.StopTest();
        }
    }
   
    /**
    * Test method to test Pull WOrk with Priority - Critical and High Only .
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testPullWorkPriorityCriticalHigh()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSpl'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            List<String> lstQueue = new List<String>();
            lstQueue.add('Cincinati CRU1');
            List<String> lstView = new List<String>();
            lstView.add('Other (Cincinati CRU1)');
            List<CaseTaskView_DTO_HUM_LWC> pulledWork = Hum_CaseTasks_LWC.pullWorkItems(lstQueue , lstView , HumConstants_LWC.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM,new List<String>{'All','Cases','Tasks'});
            system.assert(pulledWork.size() > 0);
            Test.StopTest();
        }
     }
   
   /**
    * Test method to test Pull WOrk with Priority - Tasks Only .
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testPullWorkPriorityTaskOnly()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSpl'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            List<String> lstQueue = new List<String>();
            lstQueue.add('Cincinati CRU1');
            List<String> lstView = new List<String>();
            lstView.add('Other (Cincinati CRU1)');
            List<CaseTaskView_DTO_HUM_LWC> pulledWork = Hum_CaseTasks_LWC.pullWorkItems(lstQueue , lstView , HumConstants_LWC.TASKLISTVIEW_PRIORITYTSKONLY_HUM,new List<String>{'Tasks'});
            system.assert(pulledWork.size() > 0);
            Test.StopTest();
        }
     }
   
    /**
    * Test method to test Pull WOrk with Priority - Cases Only .
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testPullWorkPriorityCaseOnly()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSpl'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            List<String> lstQueue = new List<String>();
            lstQueue.add('Cincinati CRU1');
            List<String> lstView = new List<String>();
            lstView.add('Other (Cincinati CRU1)');
            List<CaseTaskView_DTO_HUM_LWC> pulledWork = Hum_CaseTasks_LWC.pullWorkItems(lstQueue , lstView , HumConstants_LWC.TASKLISTVIEW_PRIORITYCASEONLY_HUM,new List<String>{'Cases'});
            system.assert(pulledWork.size() > 0);
            Test.StopTest();
        }
     }
   
    /**
    * Test method to test Pull WOrk with Priority .
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testPullWorkPriority()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSpl'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            List<String> lstQueue = new List<String>();
            lstQueue.add('Cincinati CRU1');
            List<String> lstView = new List<String>();
            lstView.add('Other (Cincinati CRU1)');
            List<CaseTaskView_DTO_HUM_LWC> pulledWork = Hum_CaseTasks_LWC.pullWorkItems(lstQueue , lstView , HumConstants_LWC.TASKLISTVIEW_PRIORITYONLY_HUM,new List<String>{'All','Cases','Tasks'});
            system.assert(pulledWork.size() > 0);
            Test.StopTest();
        }
     }
    
    /**
    * Test method to test Pull WOrk with Age -oldeast Tasks on Case Only .
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testPullWorkAgeTaskOldestCase()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSpl'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            List<String> lstQueue = new List<String>();
            lstQueue.add('Cincinati CRU1');
            List<String> lstView = new List<String>();
            lstView.add('Other (Cincinati CRU1)');
            List<CaseTaskView_DTO_HUM_LWC> pulledWork = Hum_CaseTasks_LWC.pullWorkItems(lstQueue , lstView , HumConstants_LWC.TASKLISTVIEW_AGETASKSONCASE_HUM,new List<String>{'All','Cases','Tasks'});
            system.assert(pulledWork.size() > 0);
            Test.StopTest();
        }
     }
    
    
     /**
    * Test method to test Pull WOrk with Age - Tasks Only .
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testPullWorkAgeTaskOnly()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSpl'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            List<String> lstQueue = new List<String>();
            lstQueue.add('Cincinati CRU1');
            List<String> lstView = new List<String>();
            lstView.add('Other (Cincinati CRU1)');
            List<CaseTaskView_DTO_HUM_LWC> pulledWork = Hum_CaseTasks_LWC.pullWorkItems(lstQueue , lstView  , HumConstants_LWC.TASKLISTVIEW_AGETASKSONLY_HUM,new List<String>{'All','Cases','Tasks'});
            system.assert(pulledWork.size() > 0);
            Test.StopTest();
        }
    }  
     
    /**
    * Test method to test Pull WOrk with Age - Cases Only .
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testPullWorkAgeCaseOnly()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSpl'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            List<String> lstQueue = new List<String>();
            lstQueue.add('Cincinati CRU1');
            List<String> lstView = new List<String>();
            lstView.add('Other (Cincinati CRU1)');
            List<CaseTaskView_DTO_HUM_LWC> pulledWork = Hum_CaseTasks_LWC.pullWorkItems(lstQueue , lstView , HumConstants_LWC.TASKLISTVIEW_AGECASESONLY_HUM,new List<String>{'Cases'});
            system.assert(pulledWork.size() > 0);
            Test.StopTest();
        }
    } 
    
     
    /**
    * Test method to test Pull WOrk with Age .
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testPullWorkAge()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSpl'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
            List<String> lstQueue = new List<String>();
            lstQueue.add('Cincinati CRU1');
            List<String> lstView = new List<String>();
            lstView.add('Other (Cincinati CRU1)');
            List<CaseTaskView_DTO_HUM_LWC> pulledWork = Hum_CaseTasks_LWC.pullWorkItems(lstQueue , lstView , HumConstants_LWC.TASKLISTVIEW_AGE_HUM,new List<String>{'All','Cases','Tasks'});
            system.assert(pulledWork.size() > 0);
            
            Test.StopTest();
        }
    }
    
    /**
    * Test method to test Pull WOrk with Due Date .
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testPullWorkDueDate()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSpl'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
                List<String> lstQueue = new List<String>();
                lstQueue.add('Cincinati CRU1');
                List<String> lstView = new List<String>();
                lstView.add('Other (Cincinati CRU1)');
                List<CaseTaskView_DTO_HUM_LWC> pulledWork = Hum_CaseTasks_LWC.pullWorkItems(lstQueue , lstView , HumConstants_LWC.TASKLISTVIEW_DUESPACEDATE_HUM,new List<String>{'All','Cases','Tasks'});
                system.assert(pulledWork.size() > 0);
            Test.StopTest();
        }
    }
    
    /**
    * Test method to test Pull WOrk with Due Date .
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testPullWorkDueDateMultipleData()
    {
        User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName ='test CCSpl'];
        system.runAs(oCCSup)
        {   
            Test.StartTest();
                List<String> lstQueue = new List<String>();
                lstQueue.add('Cincinati CRU1');
                lstQueue.add('Cincinati Calls1');
                List<String> lstView = new List<String>();
                lstView.add('Other (Cincinati CRU1)');
                lstView.add('Other (Cincinati Calls1)');
                lstView.add('View (Cincinati Calls1)');
                List<CaseTaskView_DTO_HUM_LWC> pulledWork = Hum_CaseTasks_LWC.pullWorkItems(lstQueue , lstView , HumConstants_LWC.TASKLISTVIEW_DUESPACEDATE_HUM,new List<String>{'All','Cases','Tasks'});
                system.assert(pulledWork.size() > 0);
            Test.StopTest();
        }
    }
    
}