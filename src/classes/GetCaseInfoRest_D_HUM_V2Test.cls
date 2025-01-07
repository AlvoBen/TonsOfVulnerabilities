/****************************************************************************
Apex Class Name  : GetCaseInfoRest_D_HUM_V2Test
Version          : 1.0 
Created Date     : Feb 22 2019
Function         : Test class to GetCaseInfoRest_D_HUM_V2 Apex class.
Modification Log :
  Developer Name          Code Review                Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------
 Divya Proddaturi         340085                   2/27/2019                  Original Version
 Moshitha Gunasekaran     343349                   03/08/2019                 REQ - 386797 - Changes done to cover code modifications. 
 Moshitha Gunasekaran                              04/02/2019                 REQ - 392822 Tech - SF - Enhance GetCaseInfo method of Case Service
 Santhi Mandava                                    04/15/2019                 Replaced "Member_Id_Value__c" with Name      
 Mohammed Noor                                     07/01/2019                 Changes done for REQ - 403693. 
 Moshitha Gunasekaran                              08/14/2019                 REQ - 404608 - Support TaxID for Known and Unknown Agent
 *******************************************************************************************************************************/

@isTest
public class GetCaseInfoRest_D_HUM_V2Test {
    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    static void setupProcess()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('humcrmsCaseInfoUser1','System Administrator');
        insert oUser;
        User oUser1 = HUMTestDataHelperTest.createTestUser('humcrmsCaseInfoUser2','ETL API Access');
        insert oUser1;
        system.assertEquals(oUser.lastname=='test humcrmsCaseInfoUser1',true); 
        System.runAs(oUser) 
        {
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'test account';
            oAcc.Enterprise_ID__c = '1234567895425';
            oAcc.Group_Number__c = '123456';
            oAcc.Source_Platform_Code__c = 'LV';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assert(oAcc.Name == 'test account');
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Affidavit';
           
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Affidavit',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'New';
           
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'New',true);           
            
            Case caseObjActionAsso = HUMTestDataHelperTest.getCase();
            caseObjActionAsso.Interacting_With__c = oAcc.Id;
            caseObjActionAsso.Interacting_With_Name__c = 'Test Case Action Name';
            caseObjActionAsso.Classification_ID__c = classificationRecDec.ID;
            caseObjActionAsso.Intent_ID__c  = intentRec.id;
            caseObjActionAsso.Interacting_With_Type__c = 'Group';
            insert caseObjActionAsso; 
            
            List<Template_Submission_Owner__c> lstTemplateSubOwner = new List<Template_Submission_Owner__c>();
            Template_Submission_Owner__c objTemplateubOwner = new Template_Submission_Owner__c();
            objTemplateubOwner.Object_Owner_ID__c = caseObjActionAsso.Id;
            objTemplateubOwner.Object_Owner_Type__c = 'case';
            lstTemplateSubOwner.add(objTemplateubOwner);
            insert lstTemplateSubOwner ;
            System.assertNotEquals(lstTemplateSubOwner, null, 'Template submission owner not created');
            
            List<Template__c> lstTemplate = new List<Template__c>();
            Template__C objTemplate = new Template__c();
            objTemplate.Description__c = 'Medicare Part D Redetermination Request';
            lstTemplate.add(objTemplate);
            insert lstTemplate ;
            System.assertNotEquals(lstTemplate , null, 'Case Template not created');
            
            List<Template_Submission__c> lstSubmission = new List<Template_Submission__c>();
            Template_Submission__c objSubmission = new Template_Submission__c();
            objSubmission.Submission_Owner__c = lstTemplateSubOwner[0].Id;
            objSubmission.Version__c = 1;
            objSubmission.Template__c = lstTemplate[0].Id;
            lstSubmission.add(objSubmission);
            insert lstSubmission;
            System.assertNotEquals(lstSubmission, null, 'Case Template submission not created');                          
                        
            List<Template_Field__c> lstTemplateField = new List<Template_Field__c>();
            Template_Field__c objTemplateField = new Template_Field__c();
            objTemplateField.Data_Type__c = 'String';
            objTemplateField.Template__c= lstTemplate[0].Id;
            lstTemplateField.add(objTemplateField);
            insert lstTemplateField;
            System.assertNotEquals(lstTemplateField, null, 'Case Template field Data not created');
            
            List<Template_Submission_Data__c> lstSubmissionData = new List<Template_Submission_Data__c>();
            Template_Submission_Data__c objSubmissionData = new Template_Submission_Data__c();
            objSubmissionData.Template_Field__c= lstTemplateField[0].Id;
            objSubmissionData.Template_Submission__c= lstSubmission[0].Id;
            objSubmissionData.Value__c= 'TestTmp';
            lstSubmissionData.add(objSubmissionData);
            insert lstSubmissionData;
            System.assertNotEquals(lstSubmissionData , null, 'Case Template submission Data not created');
        }
    }
    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
     {
             
        User oUser = HUMTestDataHelperTest.createTestUser('humcrmsCaseInfoUser1','System Administrator');
        insert oUser;
        User oUser1 = HUMTestDataHelperTest.createTestUser('humcrmsCaseInfoUser2','ETL API Access');
        insert oUser1;
        system.assertEquals(oUser.lastname=='test humcrmsCaseInfoUser1',true); 
        System.runAs(oUser) 
        {
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'test account';
            oAcc.Enterprise_ID__c = '1234567895425';
            oAcc.Group_Number__c = '123456';
            oAcc.Source_Platform_Code__c = 'LV';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assert(oAcc.Name == 'test account');
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Affidavit';
           
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Affidavit',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'New';
           
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'New',true);
            
            Case caseObj = HUMTestDataHelperTest.getCase();
            caseObj.Interacting_With__c = oAcc.Id;
            caseObj.Interacting_With_Name__c = 'Test Name';
            caseObj.Classification_ID__c = classificationRecDec.ID;
            caseObj.Intent_ID__c  = intentRec.id;
            caseobj.Interacting_With_Type__c = 'Group';
            insert caseObj ;
                        
            List<Case_Action__c> lstCaseAction = new List<Case_Action__c>();
            Case_Action__c objCaseAction = new Case_Action__c();
            objCaseAction.Action_version__c= '1';
            lstCaseAction.add(objCaseAction);
            insert lstCaseAction ; 
            System.assertNotEquals(lstCaseAction , null, 'Case Action not created');  
            
            List<Case_Action_Association__c> lstCaseActionAsso = new List<Case_Action_Association__c>();
            Case_Action_Association__c objCaseAsso = new Case_Action_Association__c();
            objCaseAsso.Case__c = caseObj.Id;
            objCaseAsso.Case_Action__c = lstCaseAction[0].Id;
            lstCaseActionAsso.add(objCaseAsso);
            insert lstCaseActionAsso; 
            System.assertNotEquals(lstCaseActionAsso, null, 'Case Assocoation not created');
            
            List<Tabular_Answer_Response__c> lstTabularResponse = new List<Tabular_Answer_Response__c>();
            Tabular_Answer_Response__c objTabularRes = new Tabular_Answer_Response__c();
            objTabularRes.Question_Values__c= 'which is the submission area?';
            objTabularRes.Response__c= 'Aetna';
            objTabularRes.Row_Number__c=6;
            objTabularRes.Action_Response__c=lstCaseActionAsso[0].Id;
            lstTabularResponse.add(objTabularRes);
            insert lstTabularResponse ; 
            System.assertNotEquals(lstTabularResponse, null, 'Case Assocoation Tabular response not created');            
            
            List<Case_Action_Response__c> lstCaseActionRes = new List<Case_Action_Response__c>();
            Case_Action_Response__c objCaseActionRes = new Case_Action_Response__c();
            objCaseActionRes.Process_Question__c= 'Is the subscriber of the other insurance covered by this Humana policy?';
            objCaseActionRes.Process_Question_Response__c = 'No';
            objCaseActionRes.Action__c = lstCaseActionAsso[0].Id;
            lstCaseActionRes.add(objCaseActionRes);
            insert lstCaseActionRes; 
            System.assertNotEquals(lstCaseActionRes, null, 'Case Assocoation Linear response not created');            
            
            System.assertEquals(caseobj.Interacting_With_Type__c == 'Group', true);
            List<CaseComment> lstCaseComment = new List<CaseComment>();
            CaseComment objCaseComment = HUMTestDataHelperTest.getCaseComm();
            objCaseComment.ParentId = caseObj.Id;
            lstCaseComment.Add(objCaseComment);
            insert lstCaseComment ;
            System.assertNotEquals(lstCaseComment, null, 'CaseComment not created');  
            
            Attachment__c objAttachment = HUMTestDataHelperTest.getCustomAttachment();
            objAttachment.Attachment_URL_Text__c = 'ehub.com';
            objAttachment.Name = 'ehub.com';
            objAttachment.Related_To_Case__c = caseObj.Id;
            insert objAttachment;
            System.assertNotEquals(objAttachment, null, 'Attachment not created'); 
             
             Attachment_Log__c objAttachmentLog = HUMTestDataHelperTest.getAttachLog();
             objAttachmentLog.Attachment_Key__c = '1254567';
             objAttachmentLog.Attachment_Type__c = 'Test';
             objAttachmentLog.Case__C = caseObj.Id;
             insert objAttachmentLog;
             System.assertNotEquals(objAttachmentLog, null, 'AttachmentLog not created'); 
             
             Attachment_Log_Item__c objAttachmentLogItem = HUMTestDataHelperTest.getAttLogItem();
             objAttachmentLogItem.Name = 'Test';
             objAttachmentLogItem.Attachment_Log__c = objAttachmentLog.Id;
             insert objAttachmentLogItem;
             System.assertNotEquals(objAttachmentLogItem, null, 'AttachmentLogItem not created');
             
             Interaction__c objInteraction = HUMTestDataHelperTest.getInteraction();
             insert objInteraction;
             System.assertNotEquals(objInteraction, null, 'Interaction not created');
            
             Interaction_Member__c InteractionMember = new Interaction_Member__c();
             InteractionMember.Interacting_About__c = oAcc.id;
             InteractionMember.Interaction__c = objInteraction.id;
             InteractionMember.Interacting_About_Type__c ='Group';
             insert InteractionMember;
             System.assertNotEquals(InteractionMember, null, 'InteractionMember not created');
            
             Case_Interaction__c caseIntWthCallName = new Case_Interaction__c();
             caseIntWthCallName.Case__C = caseObj.Id;
             caseIntWthCallName.Interaction__c = objInteraction.Id;
             Insert caseIntWthCallName;
             System.assertNotEquals(caseIntWthCallName, null, 'Case-Interaction not created');
            
             Task objTask = HUMTestDataHelperTest.getTaskRecord();
             objTask.WhatId = caseObj.Id;
             objTask.OwnerID = oUser.Id;
             insert objTask;
             System.assertNotEquals(objTask, null, 'Task not created');
             
             Group testGroup = new group(name = 'Louisville RSO Enrollment' , type = 'Queue');
            insert testGroup;                     
            system.assertNotEquals(testGroup, null , 'Group  not created'); 
                      
            QueueSobject mappingObject = new QueueSobject(QueueId = testGroup.Id, SobjectType = 'Case');
            insert mappingObject;
            system.assertNotEquals(mappingObject, null, 'Value not matched');
             
            Work_Queue_Setup__c oQSetup = new Work_Queue_Setup__c(Name='Louisville RSO Enrollment' , IsActive__c= true);
            insert oQSetup ;
            system.assertNotEquals(oQSetup, null, 'Value not matched');
            Queue_View__c oQv1 = new Queue_View__c(Queue_View_Name__c='Louisville RSO Enrollment', Department__c='Enrollment', Work_Queue_Name__c=oQSetup.Id); 
            insert oQv1;
        
            system.assertNotEquals(oQv1, null, 'Value not matched');
                List<Account> lstAccount = new  List<Account>();
             Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc1 = new Account( Enterprise_Id__c = '223300104', firstname = 'test2' ,lastname = 'method2' , Birthdate__c= '01/02/1990', ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc2 = new Account( Enterprise_Id__c = '002935049', firstname = 'test3', lastname = 'method3' ,Birthdate__c= '01/01/1990' ,ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc3 = new Account( Enterprise_Id__c = '5478451', Agent_ID__c = '5478451', Agent_Type__c = 'Agency', Name = 'test4Broker', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId());
             Account acc4 = new Account( Enterprise_Id__c = '5454451', Agent_ID__c = '5454451', Agent_Type__c = 'Agency', Name = 'test5Broker', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId());
             Account acc5 = new Account( Enterprise_Id__c = '1254785', NPI_ID__c = '1235452151', DBA__c = 'TestProvider', Name = 'Provider Test', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId());
             Account acc6 = new Account( Enterprise_Id__c = '5451254', NPI_ID__c = '5457894561', DBA__c = 'TestProvider1', Name = 'Provider Test1', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId());
             Account acc7 = new Account( Enterprise_Id__c = '5451354', NPI_ID__c = '5457893561', DBA__c = 'TestProvider2', Name = 'Provider Test2', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Provider').getRecordTypeId());
             Account acc8 = new Account( Enterprise_Id__c = '5478351', Agent_ID__c = '5478351', Agent_Type__c = 'Agency', Name = 'test5Broker', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Agent/Broker').getRecordTypeId());
             Account acc9 = HUMTestDataHelperTest.getAccount();
             acc9.Name = 'test account';
             acc9.Enterprise_ID__c = '5478351567890';
             acc9.Group_Number__c = '567456';
             acc9.Source_Platform_Code__c = 'EM';
             acc9.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Group').getRecordTypeId();
             lstAccount.Add(acc);
             lstAccount.Add(acc1);
             lstAccount.Add(acc2);
             lstAccount.Add(acc3);
             lstAccount.Add(acc4);
             lstAccount.Add(acc5);
             lstAccount.Add(acc6);
             lstAccount.Add(acc7);
             lstAccount.Add(acc8);
             lstAccount.Add(acc9);
             Insert lstAccount;
             system.assertNotEquals(lstAccount, null , 'Accounts not created');
            
            list<ConsumerID__c> lstConsumerRecs = new list<ConsumerID__c>(); 
             //Known Agent TAXID
			ConsumerID__c consumerrec2=new ConsumerID__c();  
			consumerrec2.ID_Type__c='TAXID';
			consumerrec2.Consumer_ID__c='112345679';
			consumerrec2.Account__c=lstAccount[3].Id;
			lstConsumerRecs.add(consumerrec2);
			
			//UnKnown Agent TAXID
			ConsumerID__c consumerrec3=new ConsumerID__c();  
			consumerrec3.ID_Type__c='TAXID';
			consumerrec3.Consumer_ID__c='112345667';
			consumerrec3.Account__c=lstAccount[8].Id;
			lstConsumerRecs.add(consumerrec3);
			
			insert lstConsumerRecs;
             
            Case caseObj1 = HUMTestDataHelperTest.getCase();
            caseObj1.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseObj1.Work_Queue_View_Name__c='Other';
            caseObj1.accountId = lstAccount[0].Id;
            caseObj1.Interacting_With__c = lstAccount[0].Id;
            caseObj1.Interacting_With_Name__c = 'Test Name';
            caseObj1.Classification_ID__c = classificationRecDec.ID;
            caseObj1.Intent_ID__c  = intentRec.id;
            caseobj1.Interacting_With_Type__c = 'Group';
            insert caseObj1 ;
            
             Case caseObj2 = HUMTestDataHelperTest.getCase();
            caseObj2.Work_Queue_View_Name__c='Other';
            caseObj2.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseObj2.accountId  = lstAccount[1].Id;
            caseObj2.Interacting_With__c = lstAccount[1].Id;
            caseObj2.Interacting_With_Name__c = 'Test Name';
            caseObj2.Classification_ID__c = classificationRecDec.ID;
            caseObj2.Intent_ID__c  = intentRec.id;
            caseobj2.Interacting_With_Type__c = 'Group';
            insert caseObj2 ;
            
            Case caseObj3 = HUMTestDataHelperTest.getCase();
            caseObj3.Work_Queue_View_Name__c='Puerto Rico Incomplete';
            caseObj3.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseObj3.accountId  = lstAccount[1].Id;
            caseObj3.Interacting_With__c = lstAccount[2].Id;
            caseObj3.Interacting_With_Name__c = 'Test Name';
            caseObj3.Classification_ID__c = classificationRecDec.ID;
            caseObj3.Intent_ID__c  = intentRec.id;
            caseobj3.Interacting_With_Type__c = 'Group';
            caseobj3.Subtype__c='Incomplete';
            caseobj3.Type='Customer Inquiry';
            insert caseObj3 ;
            
            Case caseObj4 = HUMTestDataHelperTest.getCase();
            caseObj4.Work_Queue_View_Name__c='Chronic Care Special Needs Plan (SNP) Incomplete';
            caseObj4.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj4.accountId  = lstAccount[1].Id;
            caseObj4.Interacting_With__c = lstAccount[2].Id;
            caseObj4.Interacting_With_Name__c = 'Test Name';
            caseObj4.Classification_ID__c = classificationRecDec.ID;
            caseObj4.Intent_ID__c  = intentRec.id;
            caseObj4.Interacting_With_Type__c = 'Group';
            caseObj4.Subtype__c='Incomplete';
            caseObj4.Type='Customer Inquiry';
            insert caseObj4 ;
            
            Case caseObj5 = HUMTestDataHelperTest.getCase();
            caseObj5.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj5.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseObj5.accountId  = lstAccount[1].Id;
            caseObj5.Interacting_With__c = lstAccount[2].Id;
            caseObj5.Interacting_With_Name__c = 'Test Name';
            caseObj5.Classification_ID__c = classificationRecDec.ID;
            caseObj5.Intent_ID__c  = intentRec.id;
            caseObj5.Interacting_With_Type__c = 'Group';
            caseObj5.Subtype__c='Incomplete';
            caseObj5.Type='Customer Inquiry';
            insert caseObj5 ;
            
            Case caseObj6 = HUMTestDataHelperTest.getCase();
            caseObj6.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj6.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj6.accountId  = lstAccount[1].Id;
            caseObj6.Interacting_With__c = lstAccount[2].Id;
            caseObj6.Interacting_With_Name__c = 'Test Name';
            caseObj6.Classification_ID__c = classificationRecDec.ID;
            caseObj6.Intent_ID__c  = intentRec.id;
            caseObj6.Interacting_With_Type__c = 'Group';
            caseObj6.Subtype__c='Incomplete';
            caseObj6.Type='Customer Inquiry';
            insert caseObj6 ;
            
            Case caseObj7 = HUMTestDataHelperTest.getCase();
            caseObj7.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj7.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj7.accountId  = lstAccount[3].Id;
            caseObj7.Interacting_With__c = lstAccount[3].Id;
            caseObj7.Interacting_With_Name__c = 'Test Name';
            caseObj7.Classification_ID__c = classificationRecDec.ID;
            caseObj7.Intent_ID__c  = intentRec.id;
            caseObj7.Interacting_With_Type__c = 'Agent';
            caseObj7.Subtype__c='Incomplete';
            caseObj7.Type='Customer Inquiry';
            insert caseObj7 ;
            
            Case caseObj8 = HUMTestDataHelperTest.getCase();
            caseObj8.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj8.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj8.accountId  = lstAccount[4].Id;
            caseObj8.Interacting_With__c = lstAccount[4].Id;
            caseObj8.Interacting_With_Name__c = 'Test Name';
            caseObj8.Classification_ID__c = classificationRecDec.ID;
            caseObj8.Intent_ID__c  = intentRec.id;
            caseObj8.Interacting_With_Type__c = 'Agent';
            caseObj8.Subtype__c='Incomplete';
            caseObj8.Type='Customer Inquiry';
            insert caseObj8 ;
            
            Case caseObj9 = HUMTestDataHelperTest.getCase();
            caseObj9.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj9.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj9.accountId  = lstAccount[5].Id;
            caseObj9.Interacting_With__c = lstAccount[5].Id;
            caseObj9.Interacting_With_Name__c = 'Test Name';
            caseObj9.Classification_ID__c = classificationRecDec.ID;
            caseObj9.Intent_ID__c  = intentRec.id;
            caseObj9.Interacting_With_Type__c = 'Provider';
            caseObj9.Subtype__c='Incomplete';
            caseObj9.Type='Customer Inquiry';
            insert caseObj9 ;
            
            Case caseObj10 = HUMTestDataHelperTest.getCase();
            caseObj10.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj10.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj10.accountId  = lstAccount[6].Id;
            caseObj10.Interacting_With__c = lstAccount[6].Id;
            caseObj10.Interacting_With_Name__c = 'Test Name';
            caseObj10.Classification_ID__c = classificationRecDec.ID;
            caseObj10.Intent_ID__c  = intentRec.id;
            caseObj10.Interacting_With_Type__c = 'Provider';
            caseObj10.Subtype__c='Incomplete';
            caseObj10.Type='Customer Inquiry';
            insert caseObj10 ;
            
            Case caseObj11 = HUMTestDataHelperTest.getCase();
            caseObj11.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj11.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj11.accountId  = oAcc.Id;
            caseObj11.Interacting_With__c = oAcc.Id;
            caseObj11.Interacting_With_Name__c = 'Test Name';
            caseObj11.Classification_ID__c = classificationRecDec.ID;
            caseObj11.Intent_ID__c  = intentRec.id;
            caseObj11.Interacting_With_Type__c = 'Provider';
            caseObj11.Subtype__c='Incomplete';
            caseObj11.Type='Customer Inquiry';
            insert caseObj11 ;
            
            Case caseObj12 = HUMTestDataHelperTest.getCase();
            caseObj12.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj12.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj12.accountId  = lstAccount[7].Id;
            caseObj12.Interacting_About_Type__c = 'Unknown-Provider';
            caseObj12.Interacting_With__c = lstAccount[6].Id;
            caseObj12.Interacting_With_Name__c = 'Test Name';
            caseObj12.Classification_ID__c = classificationRecDec.ID;
            caseObj12.Intent_ID__c  = intentRec.id;
            caseObj12.Interacting_With_Type__c = 'Provider';
            caseObj12.Subtype__c='Incomplete';
            caseObj12.Type='Customer Inquiry';
            caseObj12.Created_By_Queue__c = 'Louisville RSO Enrollment';
            caseObj12.LastModifiedby_Queue__c = 'Louisville RSO Enrollment';
            caseObj12.Origin = 'Inbound Call';
            caseObj12.Complaint__c = 'No';
            caseObj12.G_A_Rights_Given__c = 'No';
            insert caseObj12 ;
            
            Case caseObj13 = HUMTestDataHelperTest.getCase();
            caseObj13.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj13.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj13.accountId  = lstAccount[6].Id;
            caseObj13.Interacting_About_Type__c = 'Provider';
            caseObj13.Interacting_With__c = lstAccount[7].Id;
            caseObj13.Interacting_With_Name__c = 'Test Name';
            caseObj13.Classification_ID__c = classificationRecDec.ID;
            caseObj13.Intent_ID__c  = intentRec.id;
            caseObj13.Interacting_With_Type__c = 'Unknown-Provider';
            caseObj13.Subtype__c='Incomplete';
            caseObj13.Type='Customer Inquiry';
            caseObj13.Created_By_Queue__c = 'Louisville RSO Enrollment';
            caseObj13.LastModifiedby_Queue__c = 'Louisville RSO Enrollment';
            caseObj13.Origin = 'Inbound Call';
            caseObj13.Complaint__c = 'No';
            caseObj13.G_A_Rights_Given__c = 'No';
            insert caseObj13 ;
            
            Case caseObj14 = HUMTestDataHelperTest.getCase();
            caseObj14.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj14.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj14.accountId  = lstAccount[4].Id;
            caseObj14.Interacting_About_Type__c = 'Agent';
            caseObj14.Interacting_With__c = lstAccount[8].Id;
            caseObj14.Interacting_With_Name__c = 'Test Name';
            caseObj14.Classification_ID__c = classificationRecDec.ID;
            caseObj14.Intent_ID__c  = intentRec.id;
            caseObj14.Interacting_With_Type__c = 'Unknown-Agent';
            caseObj14.Subtype__c='Incomplete';
            caseObj14.Type='Customer Inquiry';
            caseObj14.Created_By_Queue__c = 'Louisville RSO Enrollment';
            caseObj14.LastModifiedby_Queue__c = 'Louisville RSO Enrollment';
            caseObj14.Origin = 'Inbound Call';
            caseObj14.Complaint__c = 'No';
            caseObj14.G_A_Rights_Given__c = 'No';
            insert caseObj14 ;
            
            Case caseObj15 = HUMTestDataHelperTest.getCase();
            caseObj15.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj15.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj15.accountId  = lstAccount[8].Id;
            caseObj15.Interacting_About_Type__c = 'Unknown-Agent';
            caseObj15.Interacting_With__c = lstAccount[4].Id;
            caseObj15.Interacting_With_Name__c = 'Test Name';
            caseObj15.Classification_ID__c = classificationRecDec.ID;
            caseObj15.Intent_ID__c  = intentRec.id;
            caseObj15.Interacting_With_Type__c = 'Agent';
            caseObj15.Subtype__c='Incomplete';
            caseObj15.Type='Customer Inquiry';
            caseObj15.Created_By_Queue__c = 'Louisville RSO Enrollment';
            caseObj15.LastModifiedby_Queue__c = 'Louisville RSO Enrollment';
            caseObj15.Origin = 'Inbound Call';
            caseObj15.Complaint__c = 'No';
            caseObj15.G_A_Rights_Given__c = 'No';
            insert caseObj15 ;
            
            Case caseObj16 = HUMTestDataHelperTest.getCase();
            caseObj16.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj16.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj16.accountId  = lstAccount[9].Id;
            caseObj16.Interacting_About_Type__c = 'Unknown-Group';
            caseObj16.Interacting_With__c = oAcc.Id;
            caseObj16.Interacting_With_Name__c = 'Test Name';
            caseObj16.Classification_ID__c = classificationRecDec.ID;
            caseObj16.Intent_ID__c  = intentRec.id;
            caseObj16.Interacting_With_Type__c = 'Group';
            caseObj16.Subtype__c='Incomplete';
            caseObj16.Type='Customer Inquiry';
            caseObj16.Created_By_Queue__c = 'Louisville RSO Enrollment';
            caseObj16.LastModifiedby_Queue__c = 'Louisville RSO Enrollment';
            caseObj16.Origin = 'Inbound Call';
            caseObj16.Complaint__c = 'No';
            caseObj16.G_A_Rights_Given__c = 'No';
            insert caseObj16 ;
            
            Case caseObj17 = HUMTestDataHelperTest.getCase();
            caseObj17.Work_Queue_View_Name__c='Creditable Coverage';
            caseObj17.Owner_Queue__c = 'Louisville RSO Enrollment';
            caseObj17.accountId  = oAcc.Id;
            caseObj17.Interacting_About_Type__c = 'Group';
            caseObj17.Interacting_With__c = lstAccount[9].Id;
            caseObj17.Interacting_With_Name__c = 'Test Name';
            caseObj17.Classification_ID__c = classificationRecDec.ID;
            caseObj17.Intent_ID__c  = intentRec.id;
            caseObj17.Interacting_With_Type__c = 'Unknown-Group';
            caseObj17.Subtype__c='Incomplete';
            caseObj17.Type='Customer Inquiry';
            caseObj17.Created_By_Queue__c = 'Louisville RSO Enrollment';
            caseObj17.LastModifiedby_Queue__c = 'Louisville RSO Enrollment';
            caseObj17.Origin = 'Inbound Call';
            caseObj17.Complaint__c = 'No';
            caseObj17.G_A_Rights_Given__c = 'No';
            insert caseObj17 ;
            
             list<Product2> listProduct = new List<Product2>();
             Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
             Product2 testProduct1 = new Product2(Name ='testprod2', ProductCode ='MED');
             Product2 testProduct2 = new Product2(Name ='testprod3', ProductCode ='MED');
             listProduct.Add(testProduct);
             listProduct.Add(testProduct1);
             listProduct.Add(testProduct2);
              Insert listProduct;
             system.assertNotEquals(listProduct, null , 'Products not created');
             
                          
             list<Policy__c> listPolicy = new List<Policy__c>();             
             Policy__c testPolicy = new Policy__c(Group_Name__c = lstAccount[0].ID, Major_LOB__c = 'MER', Product__c = listProduct[0].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy1 = new Policy__c(Group_Name__c = lstAccount[1].ID, Major_LOB__c = 'MCD', Product__c = listProduct[1].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy2 = new Policy__c(Group_Name__c = lstAccount[2].ID, Major_LOB__c = 'MEF',Platform__c = 'LV', Product__c = listProduct[2].ID,Policy_Group_Number__c='123456');
             listPolicy.Add(testPolicy);
             listPolicy.Add(testPolicy1);
             listPolicy.Add(testPolicy2);
              insert listPolicy; 
             system.assertNotEquals(listPolicy, null , 'Policies not created');
             
             List<Policy_Member__c>  listPolicyMember = new List<Policy_Member__c>();
             Policy_Member__c memeber = new Policy_Member__c(Policy__c = listPolicy[0].ID, Member__c = lstAccount[0].ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber1 = new Policy_Member__c(Policy__c = listPolicy[1].ID, Member__c = lstAccount[1].ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017', End_Date__c = '12/31/2017');
             Policy_Member__c memeber2 = new Policy_Member__c(Policy__c = listPolicy[2].ID, Member__c = lstAccount[2].ID, ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             listPolicyMember.Add(memeber);
             listPolicyMember.Add(memeber1);
             listPolicyMember.Add(memeber2);
             Insert listPolicyMember ;
             system.assertNotEquals(listPolicyMember, null ,  'Policies  not created for members');
             
             List<Member_ID__c> listmember = new List<Member_ID__c>();
             Member_ID__c member = new Member_ID__c(Policy__c = listPolicy[0].ID,Policy_Member__c = listPolicyMember[0].ID, Member_Card_External_ID__c= '123345' , name = '360306642' ,Type__c ='SSN');
             Member_ID__c member1 = new Member_ID__c(Policy__c = listPolicy[1].ID,Policy_Member__c = listPolicyMember[1].ID, Member_Card_External_ID__c= '123645' , name = '360306641' ,Type__c ='MemberID',Member_Dependent_Code__c='00');
             Member_ID__c member2 = new Member_ID__c(Policy__c = listPolicy[2].ID,Policy_Member__c = listPolicyMember[2].ID, Member_Card_External_ID__c= '123245' , name = '002935049' , Type__c ='MedicareID');
             listmember.Add(member);
             listmember.Add(member1);
             listmember.Add(member2);
              insert listmember;
             system.assertNotEquals(listmember, null , 'Members  not created'); 
             
             ConsumerID__c consumerrec=new ConsumerID__c();
             consumerrec.ID_Type__c='MedicareID';
             consumerrec.Consumer_ID__c='1123456';
             consumerrec.Account__c=lstAccount[0].Id;
             insert consumerrec;
            system.assertNotEquals(consumerrec, null , 'Consumer record  not created');
        }
     }
    
   /**
    * This is a test method for Get Case Info Service 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_GetCaseInfoRest_V2()
    {
        List<Case> objCase = [Select CaseNumber,Interacting_With_Type__c from Case where Interacting_With_Name__c='Test Name' Limit 2];
        List<String> lstcaseids=new List<String>();
        for(Case c:objCase)
        {
            lstcaseids.add(c.casenumber);
        }
        
        List<Case> objCase1 = [Select CaseNumber,Interacting_With_Type__c from Case where Interacting_With_Name__c='Test Case Action Name'];
        List<String> lstcaseids1 =new List<String>();
        for(Case c:objCase1)
        {
            lstcaseids1.add(c.casenumber);
        }
        
       
        List<String> inclusioncriteria= new List<String>();
        inclusioncriteria.add('Tasks');
        inclusioncriteria.add('Attachment');
        inclusioncriteria.add('Attachment Log');
        inclusioncriteria.add('Interaction');
        inclusioncriteria.add('Case Comment');
        inclusioncriteria.add('Search Unknown');
         
        
        List<String> inclusioncriteriaDetails= new List<String>();
        inclusioncriteriaDetails.add('Case Details');
        inclusioncriteriaDetails.add('Search Unknown');
        inclusioncriteriaDetails.add('Unknown Details');
        inclusioncriteriaDetails.add('Process Summary');
        inclusioncriteriaDetails.add('Process Detail');
        
            // For MedicareID
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo1 = new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse1= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingWithCriteria1 = new List<EntityIdentifierDTO>();
            List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO> OwnerQueueDetails1=new List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO>();
            EntityIdentifierDTO entityIdentifierRequest1 = new EntityIdentifierDTO();
            entityIdentifierRequest1.EntityType = 'Member';
            entityIdentifierRequest1.IdValue ='002935049';
            entityIdentifierRequest1.IdValueType='MedicareID';
            entityIdentifierRequest1.Platform='';
            entityIdentifierRequest1.MemberIdDependentCode='';
            lstInteractingWithCriteria1.add(entityIdentifierRequest1);
            CaseInfoResponse1.InteractingWithCriteria=lstInteractingWithCriteria1;
            CaseInfoResponse1.EndDate ='03/03/2019';
            CaseInfoResponse1.StartDate = '02/02/2019';
            CaseInfoResponse1.StartPaging='1';
            CaseInfoResponse1.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo1.GetCaseInfo=CaseInfoResponse1;
            
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo2 = new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse2= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingaboutCriteria2 = new List<EntityIdentifierDTO>();
            List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO> OwnerQueueDetails2=new List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO>();
            CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO Ownerqueue2=new CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO();
            EntityIdentifierDTO entityIdentifierRequest2 = new EntityIdentifierDTO();
            entityIdentifierRequest2.EntityType = 'Member';
            entityIdentifierRequest2.IdValue ='002935049';
            entityIdentifierRequest2.IdValueType='MedicareID';
            entityIdentifierRequest2.Platform='';
            entityIdentifierRequest2.MemberIdDependentCode='';
            EntityIdentifierDTO entityIdentifierRequest21= new EntityIdentifierDTO();
            entityIdentifierRequest21.EntityType = 'Member';
            entityIdentifierRequest21.IdValue ='223300104';
            entityIdentifierRequest21.IdValueType='EnterpriseID';
            entityIdentifierRequest21.Platform='';
            entityIdentifierRequest21.MemberIdDependentCode='';
            EntityIdentifierDTO entityIdentifierReq132= new EntityIdentifierDTO();
            entityIdentifierReq132.EntityType = 'Member';
            entityIdentifierReq132.IdValue ='223300104';
            entityIdentifierReq132.IdValueType='EnterpriseId';
            entityIdentifierReq132.Platform='';
            entityIdentifierReq132.MemberIdDependentCode='';
            lstInteractingAboutCriteria2.add(entityIdentifierReq132);
            EntityIdentifierDTO entityIdentifierReq133= new EntityIdentifierDTO();
            entityIdentifierReq133.EntityType = 'Member';
            entityIdentifierReq133.IdValue ='H46738930';
            entityIdentifierReq133.IdValueType='MemberID';
            entityIdentifierReq133.Platform='';
            entityIdentifierReq133.MemberIdDependentCode='00';
            lstInteractingAboutCriteria2.add(entityIdentifierReq133);
            EntityIdentifierDTO entityIdentifierReq134= new EntityIdentifierDTO();
            entityIdentifierReq134.EntityType = 'Member';
            entityIdentifierReq134.IdValue ='360306641';
            entityIdentifierReq134.IdValueType='SSN';
            entityIdentifierReq134.Platform='';
            entityIdentifierReq134.MemberIdDependentCode='';
            lstInteractingAboutCriteria2.add(entityIdentifierReq134);
            EntityIdentifierDTO entityIdentifierReq135= new EntityIdentifierDTO();
            entityIdentifierReq135.EntityType = 'Member';
            entityIdentifierReq135.IdValue ='002935049';
            entityIdentifierReq135.IdValueType='EnterpriseId';
            entityIdentifierReq135.Platform='';
            entityIdentifierReq135.MemberIdDependentCode='';
            lstInteractingAboutCriteria2.add(entityIdentifierReq135);
            lstInteractingAboutCriteria2.add(entityIdentifierRequest21);
            lstInteractingAboutCriteria2.add(entityIdentifierRequest2);
            CaseInfoResponse2.InteractingAboutCriteria=lstInteractingaboutCriteria2;
            CaseInfoResponse2.InteractingWithCriteria=lstInteractingaboutCriteria2;
            List<String> QueueViewNames2=new List<String>{'Chronic Care Special Needs Plan (SNP) Incomplete','Creditable Coverage'};
            Ownerqueue2.WorkQueueName='Louisville RSO Enrollment';
            Ownerqueue2.QueueViewName=QueueViewNames2;
            OwnerQueueDetails2.add(Ownerqueue2);
            CaseInfoResponse2.OwnerQueueDetails=OwnerQueueDetails2;
            CaseInfoResponse2.EndDate ='03/03/2019';
            CaseInfoResponse2.StartDate = '02/02/2019';
            CaseInfoResponse2.StartPaging='1';
            CaseInfoResponse2.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo2.GetCaseInfo=CaseInfoResponse2;
         // Date range beyond 6 months
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo3= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse3= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingaboutCriteria3 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest3 = new EntityIdentifierDTO();
            entityIdentifierRequest3.EntityType = 'Member';
            entityIdentifierRequest3.IdValue ='002935049';
            entityIdentifierRequest3.IdValueType='MedicareID';
            entityIdentifierRequest3.Platform='';
            entityIdentifierRequest3.MemberIdDependentCode='';
            lstInteractingAboutCriteria3.add(entityIdentifierRequest3);
            CaseInfoResponse3.InteractingAboutCriteria=lstInteractingaboutCriteria3;
            CaseInfoResponse3.EndDate ='03/03/2019';
            CaseInfoResponse3.StartDate = '02/02/2018';
            CaseInfoResponse3.StartPaging='1';
            CaseInfoResponse3.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo3.GetCaseInfo=CaseInfoResponse3;
            // For EnterpriseID
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo4= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse4=new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingAboutCriteria4= new List<EntityIdentifierDTO>();
            List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO> OwnerQueueDetails4=new List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO>();
            CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO Ownerqueue4=new CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO();
            EntityIdentifierDTO entityIdentifierRequest4= new EntityIdentifierDTO();
            entityIdentifierRequest4.EntityType = 'Member';
            entityIdentifierRequest4.IdValue ='223300104';
            entityIdentifierRequest4.IdValueType='EnterpriseID';
            entityIdentifierRequest4.Platform='';
            entityIdentifierRequest4.MemberIdDependentCode='';
            lstInteractingAboutCriteria4.add(entityIdentifierRequest4);
            CaseInfoResponse4.InteractingAboutCriteria=lstInteractingAboutCriteria4;
            List<String> QueueViewNames4=new List<String>{'',''};
            Ownerqueue4.WorkQueueName='';
            Ownerqueue4.QueueViewName=QueueViewNames4;
            OwnerQueueDetails4.add(Ownerqueue4);
            CaseInfoResponse4.OwnerQueueDetails=OwnerQueueDetails4;
            CaseInfoResponse4.EndDate ='03/03/2019';
            CaseInfoResponse4.StartDate = '02/02/2019';
            CaseInfoResponse4.StartPaging='1';
            CaseInfoResponse4.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo4.GetCaseInfo=CaseInfoResponse4;
        // MemberID
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo5 = new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse5= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingWithCriteria5 = new List<EntityIdentifierDTO>();
            List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO> OwnerQueueDetails5=new List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO>();
            CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO Ownerqueue5=new CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO();
            EntityIdentifierDTO entityIdentifierRequest5 = new EntityIdentifierDTO();
            entityIdentifierRequest5.EntityType = 'Member';
            entityIdentifierRequest5.IdValue ='H46738930';
            entityIdentifierRequest5.IdValueType='MemberID';
            entityIdentifierRequest5.Platform='';
            entityIdentifierRequest5.MemberIdDependentCode='00';
            lstInteractingWithCriteria5.add(entityIdentifierRequest5);
            CaseInfoResponse5.InteractingAboutCriteria=lstInteractingWithCriteria5;
            List<String> QueueViewNames5=new List<String>{'',''};
            Ownerqueue5.WorkQueueName='';
            Ownerqueue5.QueueViewName=QueueViewNames5;
            OwnerQueueDetails5.add(Ownerqueue5);
            CaseInfoResponse5.OwnerQueueDetails=OwnerQueueDetails5;
            CaseInfoResponse5.EndDate ='03/03/2019';
            CaseInfoResponse5.StartDate = '02/02/2019';
            CaseInfoResponse5.StartPaging='1';
            CaseInfoResponse5.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo5.GetCaseInfo=CaseInfoResponse5;
            
        
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo8= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse8=new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingAboutCriteria8= new List<EntityIdentifierDTO>();
            CaseInfoResponse8.EndDate ='';
            CaseInfoResponse8.StartDate = '';
            CaseInfoResponse8.StartPaging='';
            requestgetcaseinfo8.GetCaseInfo=CaseInfoResponse8;
        
        // Multiple Queueviews scenario
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo9= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse9= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingaboutCriteria9 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest9 = new EntityIdentifierDTO();
            List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO> OwnerQueueDetails9=new List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO>();
            CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO Ownerqueue9=new CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO();
            entityIdentifierRequest9.EntityType = 'Member';
            entityIdentifierRequest9.IdValue ='002935049';
            entityIdentifierRequest9.IdValueType='MedicareID';
            entityIdentifierRequest9.Platform='';
            entityIdentifierRequest9.MemberIdDependentCode='';
            lstInteractingAboutCriteria9.add(entityIdentifierRequest9);
            CaseInfoResponse9.InteractingAboutCriteria=lstInteractingAboutCriteria9;
            List<String> QueueViewNames9=new List<String>{'Other','Puerto Rico Incomplete'};
            Ownerqueue9.WorkQueueName='Louisville RSO Enrollment';
            Ownerqueue9.QueueViewName=QueueViewNames9;
            OwnerQueueDetails9.add(Ownerqueue9);
            CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO Ownerqueue10=new CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO();
            Ownerqueue10.WorkQueueName='Louisville RSO Calls Support';
            Ownerqueue10.QueueViewName=QueueViewNames9;
            OwnerQueueDetails9.add(Ownerqueue10);
            CaseInfoResponse9.OwnerQueueDetails = OwnerQueueDetails9;
            CaseInfoResponse9.EndDate ='03/03/2019';
            CaseInfoResponse9.StartDate = '02/02/2019';
            CaseInfoResponse9.StartPaging='1';
            CaseInfoResponse9.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo9.GetCaseInfo=CaseInfoResponse9;
        // Option fields with Interactingabout or Interactingwith Criteria
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo10= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse10= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingaboutCriteria10 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest10 = new EntityIdentifierDTO();
            entityIdentifierRequest10.EntityType = 'Member';
            entityIdentifierRequest10.IdValue ='002935049';
            entityIdentifierRequest10.IdValueType='MedicareID';
            entityIdentifierRequest10.Platform='';
            entityIdentifierRequest10.MemberIdDependentCode='';
            lstInteractingAboutCriteria10.add(entityIdentifierRequest10);
            List<String> lststatus=new List<String>();
            lststatus.add('Open');
            CaseInfoResponse10.InteractingAboutCriteria=lstInteractingAboutCriteria10;
            CaseInfoResponse10.EndDate ='03/03/2019';
            CaseInfoResponse10.StartDate = '02/02/2019';
            CaseInfoResponse10.CaseType= new list<string>{'Incomplete'};
            CaseInfoResponse10.CaseSubtype= new list<string>{'Customer Inquiry'};
            CaseInfoResponse10.StartPaging='1';
            CaseInfoResponse10.Status=lststatus;
            CaseInfoResponse10.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo10.GetCaseInfo=CaseInfoResponse10;
        
            
                    
            Test.startTest();
            requestgetcaseinfo1.GetCaseInfo=CaseInfoResponse1;
            CaseServiceRestGetResponse_DTO_HUM_V2 response1 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo1 );
            system.assertNotEquals(response1,NULL);
            requestgetcaseinfo2.GetCaseInfo=CaseInfoResponse2;
            CaseServiceRestGetResponse_DTO_HUM_V2 response2 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo2 );
            system.assertNotEquals(response2,NULL);
            requestgetcaseinfo3.GetCaseInfo=CaseInfoResponse3;
            CaseServiceRestGetResponse_DTO_HUM_V2 response3 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo3 );
            system.assertNotEquals(response3,NULL);
            requestgetcaseinfo4.GetCaseInfo=CaseInfoResponse4;
            CaseServiceRestGetResponse_DTO_HUM_V2 response4 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo4 );
            system.assertNotEquals(response4,NULL);
            requestgetcaseinfo5.GetCaseInfo=CaseInfoResponse5;
            CaseServiceRestGetResponse_DTO_HUM_V2 response5 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo5 );
            system.assertNotEquals(response5,NULL);
            requestgetcaseinfo8.GetCaseInfo=CaseInfoResponse8;
            CaseServiceRestGetResponse_DTO_HUM_V2 response8 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo8 );
            system.assertNotEquals(response8,NULL);
            requestgetcaseinfo9.GetCaseInfo=CaseInfoResponse9;
            CaseServiceRestGetResponse_DTO_HUM_V2 response9 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo9 );
            system.assertNotEquals(response9,NULL);
            requestgetcaseinfo10.GetCaseInfo=CaseInfoResponse10;
            CaseServiceRestGetResponse_DTO_HUM_V2 response10 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo10 );
            system.assertNotEquals(response10,NULL);
            Test.stopTest();
            
            
    }
    
    /**
    * This is a test method for Get Case Info Service for Unknown
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_GetCaseInfoRest_V2_Unknown()
    {
        List<Case> objCase = [Select CaseNumber,Interacting_With_Type__c from Case where Interacting_With_Name__c='Test Name' Limit 2];
        List<String> lstcaseids=new List<String>();
        for(Case c:objCase)
        {
            lstcaseids.add(c.casenumber);
        }
        
        List<Case> objCase1 = [Select CaseNumber,Interacting_With_Type__c from Case where Interacting_With_Name__c='Test Name'];
        List<String> lstcaseids1 =new List<String>();
        for(Case c:objCase1)
        {
            lstcaseids1.add(c.casenumber);
        }
        
       
        List<String> inclusioncriteria= new List<String>();
        inclusioncriteria.add('Tasks');
        inclusioncriteria.add('Attachment');
        inclusioncriteria.add('Attachment Log');
        inclusioncriteria.add('Interaction');
        inclusioncriteria.add('Case Comment');
        inclusioncriteria.add('Search Unknown');
         
        
        List<String> inclusioncriteriaDetails= new List<String>();
        inclusioncriteriaDetails.add('Case Details');
        inclusioncriteriaDetails.add('Search Unknown');
        inclusioncriteriaDetails.add('Unknown Details');
        
         //  CaseNumbers in request
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo6= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse6=new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingAboutCriteria6= new List<EntityIdentifierDTO>();
            CaseInfoResponse6.CaseId=lstcaseids1;
            CaseInfoResponse6.EndDate ='03/03/2019';
            CaseInfoResponse6.StartDate = '02/02/2019';
            CaseInfoResponse6.StartPaging='1';
            CaseInfoResponse6.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo6.GetCaseInfo=CaseInfoResponse6;
            
            List<String> inclusioncriteria7= new List<String>();
            inclusioncriteria7.add('Case Details');
            inclusioncriteria7.add('Tasks');
            inclusioncriteria7.add('Attachment');
            inclusioncriteria7.add('Attachment Log');
            inclusioncriteria7.add('Interaction');
            inclusioncriteria7.add('Case Comment');
            inclusioncriteria7.add('Process Summary');
            inclusioncriteria7.add('Process Detail');
            inclusioncriteria7.add('Search Unknown');        
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo7= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse7=new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingAboutCriteria7= new List<EntityIdentifierDTO>();
            CaseInfoResponse7.CaseId=lstcaseids;
            CaseInfoResponse7.EndDate ='03/03/2019';
            CaseInfoResponse7.StartDate = '02/02/2019';
            CaseInfoResponse7.StartPaging='1';
            CaseInfoResponse7.InclusionCriteria=inclusioncriteria7;
            requestgetcaseinfo7.GetCaseInfo=CaseInfoResponse7;
        // Unknwon Member Search
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo11= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse11= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingaboutCriteria11 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest11 = new EntityIdentifierDTO();
            entityIdentifierRequest11.EntityType = 'Member';
            entityIdentifierRequest11.IdValue ='1123456';
            entityIdentifierRequest11.IdValueType='MedicareID';
            entityIdentifierRequest11.Platform='';
            entityIdentifierRequest11.MemberIdDependentCode='';
            lstInteractingAboutCriteria11.add(entityIdentifierRequest11);
            CaseInfoResponse11.InteractingAboutCriteria=lstInteractingAboutCriteria11;
            CaseInfoResponse11.EndDate ='03/03/2019';
            CaseInfoResponse11.StartDate = '02/02/2019';
            CaseInfoResponse11.StartPaging='1';
            List<String> inclusioncriteria1=new List<string>();
            inclusioncriteria1.add('Search Unknown');
            CaseInfoResponse11.InclusionCriteria=inclusioncriteria1;
            requestgetcaseinfo11.GetCaseInfo=CaseInfoResponse11;
        // Date range beyond 1 year
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo12= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse12= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingaboutCriteria12 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest12 = new EntityIdentifierDTO();
            entityIdentifierRequest12.EntityType = 'Member';
            entityIdentifierRequest12.IdValue ='1223456';
            entityIdentifierRequest12.IdValueType='MedicareID';
            entityIdentifierRequest12.Platform='';
            entityIdentifierRequest12.MemberIdDependentCode='';
            lstInteractingAboutCriteria12.add(entityIdentifierRequest12);
            CaseInfoResponse12.InteractingAboutCriteria=lstInteractingAboutCriteria12;
            CaseInfoResponse12.EndDate ='03/03/2019';
            CaseInfoResponse12.StartDate = '01/02/2018';
            CaseInfoResponse12.StartPaging='1';
            requestgetcaseinfo12.GetCaseInfo=CaseInfoResponse12;
        // Multiple InteractingwithCriteria
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo13= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse13=new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingwithCriteria13= new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierReq131= new EntityIdentifierDTO();
            List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO> OwnerQueueDetails13=new List<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO>();
            CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO Ownerqueue13=new CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO();
            entityIdentifierReq131.EntityType = 'Member';
            entityIdentifierReq131.IdValue ='002935049';
            entityIdentifierReq131.IdValueType='MedicareID';
            entityIdentifierReq131.Platform='';
            entityIdentifierReq131.MemberIdDependentCode='';
            lstInteractingwithCriteria13.add(entityIdentifierReq131);
            EntityIdentifierDTO entityIdentifierReq132= new EntityIdentifierDTO();
            entityIdentifierReq132.EntityType = 'Member';
            entityIdentifierReq132.IdValue ='223300104';
            entityIdentifierReq132.IdValueType='EnterpriseId';
            entityIdentifierReq132.Platform='';
            entityIdentifierReq132.MemberIdDependentCode='';
            lstInteractingwithCriteria13.add(entityIdentifierReq132);
            EntityIdentifierDTO entityIdentifierReq133= new EntityIdentifierDTO();
            entityIdentifierReq133.EntityType = 'Member';
            entityIdentifierReq133.IdValue ='H46738930';
            entityIdentifierReq133.IdValueType='MemberID';
            entityIdentifierReq133.Platform='';
            entityIdentifierReq133.MemberIdDependentCode='00';
            lstInteractingwithCriteria13.add(entityIdentifierReq133);
            EntityIdentifierDTO entityIdentifierReq134= new EntityIdentifierDTO();
            entityIdentifierReq134.EntityType = 'Member';
            entityIdentifierReq134.IdValue ='360306641';
            entityIdentifierReq134.IdValueType='SSN';
            entityIdentifierReq134.Platform='';
            entityIdentifierReq134.MemberIdDependentCode='';
            lstInteractingwithCriteria13.add(entityIdentifierReq134);
            EntityIdentifierDTO entityIdentifierReq135= new EntityIdentifierDTO();
            entityIdentifierReq135.EntityType = 'Member';
            entityIdentifierReq135.IdValue ='002935049';
            entityIdentifierReq135.IdValueType='EnterpriseId';
            entityIdentifierReq135.Platform='';
            entityIdentifierReq135.MemberIdDependentCode='';
            lstInteractingwithCriteria13.add(entityIdentifierReq135);
            EntityIdentifierDTO entityIdentifierReq136= new EntityIdentifierDTO();
            entityIdentifierReq136.EntityType = 'Member';
            entityIdentifierReq136.IdValue ='223366104';
            entityIdentifierReq136.IdValueType='MedicareID';
            entityIdentifierReq136.Platform='';
            entityIdentifierReq136.MemberIdDependentCode='';
            lstInteractingwithCriteria13.add(entityIdentifierReq136);
            CaseInfoResponse13.InteractingWithCriteria=lstInteractingwithCriteria13;
            CaseInfoResponse13.InteractingAboutCriteria=lstInteractingwithCriteria13;
            List<String> QueueViewNames13=new List<String>{'Other','Puerto Rico Incomplete'};
            Ownerqueue13.WorkQueueName='Louisville RSO Enrollment';
            Ownerqueue13.QueueViewName=QueueViewNames13;
            OwnerQueueDetails13.add(Ownerqueue13);
            CaseInfoResponse13.OwnerQueueDetails=OwnerQueueDetails13;
            CaseInfoResponse13.EndDate ='03/03/2019';
            CaseInfoResponse13.StartDate = '02/02/2019';
            CaseInfoResponse13.CaseType= new list<string>{'Customer Inquiry', 'Correspondence Inquiry', 'TRR Inquiry', 'SIU', 'Critical Inquiry', 'Urgent Inquiry'};
            CaseInfoResponse13.CaseSubtype= new list<string>{'TRR', 'Routine','Disenrollment', 'Incomplete', 'TRR', 'Enrollment'};
            CaseInfoResponse13.StartPaging='1';
            CaseInfoResponse13.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo13.GetCaseInfo=CaseInfoResponse13;
        
        
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo14= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse14=new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<String> CaseId=new List<String>();
            CaseInfoResponse14.CaseId=CaseId;
            CaseInfoResponse14.EndDate ='';
            CaseInfoResponse14.StartDate = '';
            CaseInfoResponse14.CaseType=new list<string>();
            CaseInfoResponse14.CaseSubtype=new list<string>();
            CaseInfoResponse14.StartPaging='';
            requestgetcaseinfo14.GetCaseInfo=CaseInfoResponse14;
            
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo15= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse15= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingaboutCriteria15 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest15 = new EntityIdentifierDTO();
            entityIdentifierRequest15.EntityType = 'Agent';
            entityIdentifierRequest15.IdValue ='5478451';
            entityIdentifierRequest15.IdValueType='AgentID';
            entityIdentifierRequest15.Platform='';
            entityIdentifierRequest15.MemberIdDependentCode='';
            lstInteractingAboutCriteria15.add(entityIdentifierRequest15);
            CaseInfoResponse15.InteractingAboutCriteria=lstInteractingAboutCriteria15;
            CaseInfoResponse15.InteractingWithCriteria=lstInteractingAboutCriteria15;
            CaseInfoResponse15.EndDate ='03/03/2019';
            CaseInfoResponse15.StartDate = '02/02/2019';
            CaseInfoResponse15.StartPaging='1';
            CaseInfoResponse15.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo15.GetCaseInfo=CaseInfoResponse15;
            
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo16= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse16= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingaboutCriteria16 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest16 = new EntityIdentifierDTO();
            entityIdentifierRequest16.EntityType = 'Agent';
            entityIdentifierRequest16.IdValue ='5478451';
            entityIdentifierRequest16.IdValueType='AgentID';
            entityIdentifierRequest16.Platform='';
            entityIdentifierRequest16.MemberIdDependentCode='';
            lstInteractingAboutCriteria16.add(entityIdentifierRequest16);
            EntityIdentifierDTO entityIdentifierRequest161 = new EntityIdentifierDTO();
            entityIdentifierRequest161.EntityType = 'Agent';
            entityIdentifierRequest161.IdValue ='5454451';
            entityIdentifierRequest161.IdValueType='AgentID';
            entityIdentifierRequest161.Platform='';
            entityIdentifierRequest161.MemberIdDependentCode='';
            lstInteractingAboutCriteria16.add(entityIdentifierRequest161);
            CaseInfoResponse16.InteractingAboutCriteria=lstInteractingAboutCriteria16;
            CaseInfoResponse16.InteractingWithCriteria=lstInteractingAboutCriteria16;
            CaseInfoResponse16.EndDate ='03/03/2019';
            CaseInfoResponse16.StartDate = '02/02/2019';
            CaseInfoResponse16.StartPaging='1';
            CaseInfoResponse16.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo16.GetCaseInfo=CaseInfoResponse16;
            
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo17= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse17= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingaboutCriteria17 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest17 = new EntityIdentifierDTO();
            entityIdentifierRequest17.EntityType = 'Provider';
            entityIdentifierRequest17.IdValue ='1254785';
            entityIdentifierRequest17.IdValueType='ProviderID';
            entityIdentifierRequest17.Platform='';
            entityIdentifierRequest17.MemberIdDependentCode='';
            lstInteractingAboutCriteria17.add(entityIdentifierRequest17);
            EntityIdentifierDTO entityIdentifierRequest171 = new EntityIdentifierDTO();
            entityIdentifierRequest171.EntityType = 'Provider';
            entityIdentifierRequest171.IdValue ='5451254';
            entityIdentifierRequest171.IdValueType='ProviderID';
            entityIdentifierRequest171.Platform='';
            entityIdentifierRequest171.MemberIdDependentCode='';
            lstInteractingAboutCriteria17.add(entityIdentifierRequest171);
            EntityIdentifierDTO entityIdentifierRequest172 = new EntityIdentifierDTO();
            entityIdentifierRequest172.EntityType = 'Provider';
            entityIdentifierRequest172.IdValue ='1235452151';
            entityIdentifierRequest172.IdValueType='NPIID';
            entityIdentifierRequest172.Platform='';
            entityIdentifierRequest172.MemberIdDependentCode='';
            lstInteractingAboutCriteria17.add(entityIdentifierRequest172);
            EntityIdentifierDTO entityIdentifierRequest173 = new EntityIdentifierDTO();
            entityIdentifierRequest173.EntityType = 'Provider';
            entityIdentifierRequest173.IdValue ='5457894561';
            entityIdentifierRequest173.IdValueType='NPIID';
            entityIdentifierRequest173.Platform='';
            entityIdentifierRequest173.MemberIdDependentCode='';
            lstInteractingAboutCriteria17.add(entityIdentifierRequest173);
            CaseInfoResponse17.InteractingAboutCriteria=lstInteractingAboutCriteria17;
            CaseInfoResponse17.InteractingWithCriteria=lstInteractingAboutCriteria17;
            CaseInfoResponse17.EndDate ='03/03/2019';
            CaseInfoResponse17.StartDate = '02/02/2019';
            CaseInfoResponse17.StartPaging='1';
            CaseInfoResponse17.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo17.GetCaseInfo=CaseInfoResponse17;
            
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo18= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse18= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingAboutCriteria18 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest18 = new EntityIdentifierDTO();
            entityIdentifierRequest18.EntityType = 'Provider';
            entityIdentifierRequest18.IdValue ='1254785';
            entityIdentifierRequest18.IdValueType='ProviderID';
            entityIdentifierRequest18.Platform='';
            entityIdentifierRequest18.MemberIdDependentCode='';
            lstInteractingAboutCriteria18.add(entityIdentifierRequest18);
            EntityIdentifierDTO entityIdentifierRequest183 = new EntityIdentifierDTO();
            entityIdentifierRequest183.EntityType = 'Provider';
            entityIdentifierRequest183.IdValue ='5457894561';
            entityIdentifierRequest183.IdValueType='NPIID';
            entityIdentifierRequest183.Platform='';
            entityIdentifierRequest183.MemberIdDependentCode='';
            lstInteractingAboutCriteria18.add(entityIdentifierRequest183);
            CaseInfoResponse18.InteractingAboutCriteria=lstInteractingAboutCriteria18;
            CaseInfoResponse18.InteractingWithCriteria=lstInteractingAboutCriteria18;
            CaseInfoResponse18.EndDate ='03/03/2019';
            CaseInfoResponse18.StartDate = '02/02/2019';
            CaseInfoResponse18.StartPaging='1';
            CaseInfoResponse18.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo18.GetCaseInfo=CaseInfoResponse18;
            
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo19= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse19= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingAboutCriteria19 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest19 = new EntityIdentifierDTO();
            entityIdentifierRequest19.EntityType = 'Group';
            entityIdentifierRequest19.IdValue ='1234567895425';
            entityIdentifierRequest19.IdValueType='CustomerGenKey';
            entityIdentifierRequest19.Platform='';
            entityIdentifierRequest19.MemberIdDependentCode='';
            lstInteractingAboutCriteria19.add(entityIdentifierRequest19);
            EntityIdentifierDTO entityIdentifierRequest191 = new EntityIdentifierDTO();
            entityIdentifierRequest191.EntityType = 'Group';
            entityIdentifierRequest191.IdValue ='123465';
            entityIdentifierRequest191.IdValueType='GroupNumber';
            entityIdentifierRequest191.Platform='LV';
            entityIdentifierRequest191.MemberIdDependentCode='';
            lstInteractingAboutCriteria19.add(entityIdentifierRequest191);
            CaseInfoResponse19.InteractingAboutCriteria=lstInteractingAboutCriteria19;
            CaseInfoResponse19.InteractingWithCriteria=lstInteractingAboutCriteria19;
            CaseInfoResponse19.EndDate ='03/03/2019';
            CaseInfoResponse19.StartDate = '02/02/2019';
            CaseInfoResponse19.StartPaging='1';
            CaseInfoResponse19.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo19.GetCaseInfo=CaseInfoResponse19;
            
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo20= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse20= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingAboutCriteria20 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest20 = new EntityIdentifierDTO();
            entityIdentifierRequest20.EntityType = 'Group';
            entityIdentifierRequest20.IdValue ='1234567895425';
            entityIdentifierRequest20.IdValueType='CustomerGenKey';
            entityIdentifierRequest20.Platform='';
            entityIdentifierRequest20.MemberIdDependentCode='';
            lstInteractingAboutCriteria20.add(entityIdentifierRequest20);
            EntityIdentifierDTO entityIdentifierRequest201 = new EntityIdentifierDTO();
            entityIdentifierRequest201.EntityType = 'Provider';
            entityIdentifierRequest201.IdValue ='5457894561';
            entityIdentifierRequest201.IdValueType='NPIID';
            entityIdentifierRequest201.Platform='';
            entityIdentifierRequest201.MemberIdDependentCode='';
            lstInteractingAboutCriteria20.add(entityIdentifierRequest201);
            EntityIdentifierDTO entityIdentifierRequest202 = new EntityIdentifierDTO();
            entityIdentifierRequest202.EntityType = 'Agent';
            entityIdentifierRequest202.IdValue ='5454451';
            entityIdentifierRequest202.IdValueType='AgentID';
            entityIdentifierRequest202.Platform='';
            entityIdentifierRequest202.MemberIdDependentCode='';
            lstInteractingAboutCriteria20.add(entityIdentifierRequest202);
            CaseInfoResponse20.InteractingAboutCriteria=lstInteractingAboutCriteria20;
            CaseInfoResponse20.InteractingWithCriteria=new List<EntityIdentifierDTO>();
            CaseInfoResponse20.EndDate ='03/03/2019';
            CaseInfoResponse20.StartDate = '02/02/2019';
            CaseInfoResponse20.StartPaging='1';
            CaseInfoResponse20.InclusionCriteria=inclusioncriteriaDetails;
            requestgetcaseinfo20.GetCaseInfo=CaseInfoResponse20;
            
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo21= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse21= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingAboutCriteria21 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest213 = new EntityIdentifierDTO();
            entityIdentifierRequest213.EntityType = 'Group';
            entityIdentifierRequest213.IdValue ='1234567895425';
            entityIdentifierRequest213.IdValueType='CustomerGenKey';
            entityIdentifierRequest213.Platform='';
            entityIdentifierRequest213.MemberIdDependentCode='';
            lstInteractingAboutCriteria21.add(entityIdentifierRequest213);
            EntityIdentifierDTO entityIdentifierRequest211 = new EntityIdentifierDTO();
            entityIdentifierRequest211.EntityType = 'Provider';
            entityIdentifierRequest211.IdValue ='5457894561';
            entityIdentifierRequest211.IdValueType='NPIID';
            entityIdentifierRequest211.Platform='';
            entityIdentifierRequest211.MemberIdDependentCode='';
            lstInteractingAboutCriteria21.add(entityIdentifierRequest211);
            EntityIdentifierDTO entityIdentifierRequest212 = new EntityIdentifierDTO();
            entityIdentifierRequest212.EntityType = 'Agent';
            entityIdentifierRequest212.IdValue ='5454451';
            entityIdentifierRequest212.IdValueType='AgentID';
            entityIdentifierRequest212.Platform='';
            entityIdentifierRequest212.MemberIdDependentCode='';
            lstInteractingAboutCriteria21.add(entityIdentifierRequest212);
            CaseInfoResponse21.InteractingAboutCriteria= new List<EntityIdentifierDTO>();
            CaseInfoResponse21.InteractingWithCriteria= lstInteractingAboutCriteria21;
            CaseInfoResponse21.EndDate ='05/03/2019';
            CaseInfoResponse21.StartDate = '02/02/2019';
            CaseInfoResponse21.StartPaging='1';
            CaseInfoResponse21.InclusionCriteria=inclusioncriteriaDetails;
            requestgetcaseinfo21.GetCaseInfo=CaseInfoResponse21;
            
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo22= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse22= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingaboutCriteria22 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest22 = new EntityIdentifierDTO();
            entityIdentifierRequest22.EntityType = 'Agent';
            entityIdentifierRequest22.IdValue ='112345679';
            entityIdentifierRequest22.IdValueType='TaxID';
            entityIdentifierRequest22.Platform='';
            entityIdentifierRequest22.MemberIdDependentCode='';
            lstInteractingAboutCriteria22.add(entityIdentifierRequest22);
            EntityIdentifierDTO entityIdentifierRequest122 = new EntityIdentifierDTO();
            entityIdentifierRequest122.EntityType = 'Agent';
            entityIdentifierRequest122.IdValue ='112345679';
            entityIdentifierRequest122.IdValueType='TaxID';
            entityIdentifierRequest122.Platform='';
            entityIdentifierRequest122.MemberIdDependentCode='';
            lstInteractingAboutCriteria22.add(entityIdentifierRequest122);
            CaseInfoResponse22.InteractingAboutCriteria=lstInteractingAboutCriteria22;
            CaseInfoResponse22.InteractingWithCriteria=lstInteractingAboutCriteria22;
            CaseInfoResponse22.EndDate ='03/03/2019';
            CaseInfoResponse22.StartDate = '02/02/2019';
            CaseInfoResponse22.StartPaging='1';
            CaseInfoResponse22.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo22.GetCaseInfo=CaseInfoResponse22;
            
            CaseServiceRestGetRequest_DTO_HUM_V2 requestgetcaseinfo23= new CaseServiceRestGetRequest_DTO_HUM_V2();
            CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO CaseInfoResponse23= new CaseServiceRestGetRequest_DTO_HUM_V2.CaseInfoRequestDTO();
            List<EntityIdentifierDTO> lstInteractingaboutCriteria23 = new List<EntityIdentifierDTO>();
            EntityIdentifierDTO entityIdentifierRequest23 = new EntityIdentifierDTO();
            entityIdentifierRequest23.EntityType = 'Agent';
            entityIdentifierRequest23.IdValue ='112345667';
            entityIdentifierRequest23.IdValueType='TaxID';
            entityIdentifierRequest23.Platform='';
            entityIdentifierRequest23.MemberIdDependentCode='';
            lstInteractingAboutCriteria23.add(entityIdentifierRequest23);
            CaseInfoResponse23.InteractingAboutCriteria=lstInteractingAboutCriteria23;
            CaseInfoResponse23.EndDate ='03/03/2019';
            CaseInfoResponse23.StartDate = '02/02/2019';
            CaseInfoResponse23.StartPaging='1';
            CaseInfoResponse23.InclusionCriteria=inclusioncriteria;
            requestgetcaseinfo23.GetCaseInfo=CaseInfoResponse23;
                        
            Test.startTest();
            CaseServiceRestGetResponse_DTO_HUM_V2 response20 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo20 );
            system.assertNotEquals(response20,NULL);
            CaseServiceRestGetResponse_DTO_HUM_V2 response21 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo21 );
            system.assertNotEquals(response21,NULL);
            CaseServiceRestGetResponse_DTO_HUM_V2 response11 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo11 );
            system.assertNotEquals(response11,NULL);
            CaseServiceRestGetResponse_DTO_HUM_V2 response12 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo12 );
            system.assertNotEquals(response12,NULL);
            requestgetcaseinfo13.GetCaseInfo=CaseInfoResponse13;
            CaseServiceRestGetResponse_DTO_HUM_V2 response13 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo13 );
            system.assertNotEquals(response13,NULL);
            CaseServiceRestGetResponse_DTO_HUM_V2 response14 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo14 );
            system.assertNotEquals(response14,NULL);
            CaseServiceRestGetResponse_DTO_HUM_V2 response15 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo15 );
            system.assertNotEquals(response15,NULL);
            CaseServiceRestGetResponse_DTO_HUM_V2 response16 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo16 );
            system.assertNotEquals(response16,NULL);
            CaseServiceRestGetResponse_DTO_HUM_V2 response17 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo17 );
            system.assertNotEquals(response17,NULL);
            CaseServiceRestGetResponse_DTO_HUM_V2 response18 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo18 );
            system.assertNotEquals(response18,NULL);
            CaseServiceRestGetResponse_DTO_HUM_V2 response19 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo19 );
            system.assertNotEquals(response19,NULL);
            CaseServiceRestGetResponse_DTO_HUM_V2 response22 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo22 );
            system.assertNotEquals(response22,NULL);
            CaseServiceRestGetResponse_DTO_HUM_V2 response23 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo23 );
            system.assertNotEquals(response23,NULL);
            requestgetcaseinfo6.GetCaseInfo=CaseInfoResponse6;
            CaseServiceRestGetResponse_DTO_HUM_V2 response6 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo6 );
            system.assertNotEquals(response6,NULL);
            requestgetcaseinfo7.GetCaseInfo=CaseInfoResponse7;
            CaseServiceRestGetResponse_DTO_HUM_V2 response7 = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(requestgetcaseinfo7 );
            system.assertNotEquals(response7,NULL);
            Test.StopTest();
            
    }
}