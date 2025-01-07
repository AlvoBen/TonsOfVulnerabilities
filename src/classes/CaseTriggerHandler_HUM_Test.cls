/**********************************************************************************************************************************************************
Apex Class Name  : CaseTriggerHandler_HUM_Test
Version          : 1.0
Created Date     : March 08 2016
Function         : Test class for CaseTriggerHandler_HUM
***********************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey            30464                   03/08/2016                  Original version
* Prachi Bhasin              33390                   07/20/2016                  Changes made for REQ- 255151
* PradeepKumar Dani          83713                   02/12/2016                  Implmented REQ - 308332(CaseTriggerHandler_HUM)
* Pradeepkumar Dani          99570                   06/08/2017                  Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Pradeepkumar Dani         100996                   06/13/2017                  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
* Pradeepkumar Dani         102268                   06/16/2017                  Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
* Harika Devi Kalidindi     112000                   07/20/2017                  REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
* Moshitha Gunasekaran      224503                   05/02/2018                 REQ - 350862 - Fix to process Omni Routing rules for asynchrnous call from ETL API Acess profile
* Vandana Chaudhari                                  07/24/2018                 Code changes as per REQ - 370601 CA Ticket 7646745 Fix: Web Chat Auto Create Case
* Mohammed Noor                                      09/05/2019                 Code Changes as per REQ - 408201
* Shailesh Mali                                      11/05/2019                 REQ - 409603 Process builder logic migration to apex code
* Pooja Kumbhar                                      04/29/2020                 Checkmarx Test methods without assert Error fix
* Santhi Mandava                                                                Fixed testclass issue
* Pooja Kumbhar											 07/29/2022					US3521594 - Case Visibility - CRM Lightning in CRM Classic and CRM Classic in CRM Lightning
* Pooja Kumbhar											 12/28/2022				    User story 4083329: T1PRJ0170850 - Lightning- UI Trigger Change to Support ETL
* Pooja Kumbhar											 1/11/2023				    User story 4083329: Change in Flag settings
* Moshitha G                                         01/23/2023                 US3788451 - T1PRJ0337737 - 2022 - SF- additional Access for Mulesoft user for Case and Interaction details
* Gowthami Thota                                    07/31/2023              Test Class Fix for UAT 
************************************************************************************************************************************************************/

@isTest
private class CaseTriggerHandler_HUM_Test{
    /*
    * This method creates test data for test class
    * @param   None
    * @return  None
    */
    @testSetup static void setUpTestData()
    {
        User usr = HUMTestDataHelperTest.createTestUser('humcrmsCaseTriggerUser1','Customer Care Specialist');
        insert usr;
        system.assertEquals(usr.lastname=='test humcrmsCaseTriggerUser1',true);
        usr.Current_Queue__c = 'Louisville RSO Enrollment';
        update usr;
        
        User usr1 = HUMTestDataHelperTest.createTestUser('humcrmsCaseTriggerUser2','ETL API Access');
        insert usr1;
        system.assertEquals(usr1.lastname=='test humcrmsCaseTriggerUser2',true);
		
		User usr2 = HUMTestDataHelperTest.createTestUser('TUser1','ETL API Access');
        insert usr2;
        system.assertEquals(usr2.lastname=='test TUser1',true);
        usr2.Current_Queue__c = 'Louisville RSO Enrollment';
        update usr2;
        User usr3 = HUMTestDataHelperTest.createTestUser('humcrmsCaseTriggerUser3','System Integration');
        insert usr3;
        system.assertEquals(usr3.lastname=='test humcrmsCaseTriggerUser3',true);
        PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name = 'CRMS_000_AdminTools_MulesoftAccess' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = usr3.id, PermissionSetId = pCheck.id);
       
        system.runAs(usr)
        {
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = usr.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeid = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeid==AccountRecordType.id,true);
            
            Policy__c policyRec = new Policy__c();
            policyRec.Group_Name__c = accountRec.Id;
            policyRec.Policy_Group_Number__c='11111'; 
            insert policyRec;
            system.assertEquals(policyRec.Group_Name__c==accountRec.Id,true);
            
            Policy_Member__c policyMemRec = new Policy_Member__c();
            policyMemRec.Name = '12345';
            policyMemRec.Policy__c = policyRec.Id;
            insert policyMemRec;
            system.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
            
            Policy_Member__c policyMemRec1 = new Policy_Member__c();
            policyMemRec1.Name = '12345678';
            policyMemRec1.Policy__c = policyRec.Id;
            insert policyMemRec1;
            system.assertEquals(policyMemRec1.Policy__c==policyRec.Id,true);
            
            MemberPlan Memplan = new MemberPlan();
            Memplan.Name = '12345';
            Memplan.Policy_Member__c = policyMemRec.id;
            Memplan.MemberId = accountRec.id;
            insert Memplan;
            
            MemberPlan Memplan1 = new MemberPlan();
            Memplan1.Name = '12345678';
            Memplan1.Policy_Member__c = policyMemRec1.id;
            Memplan1.MemberId = accountRec.id;
            insert Memplan1;
            
            policyMemRec.Member_Plan__c = Memplan.id;
            update policyMemRec;
            
            policyMemRec1.Member_Plan__c = Memplan1.id;
            update policyMemRec1;
            
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
            system.assertEquals(lstClassification[0].Name=='Benefits',true);
            
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
            system.assertEquals(lstIntent[1].Name == 'Estimate', true);
            
            List<Classification_Type__c> ctList = new list<Classification_Type__c>();
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'Claims (RSO)';
            ctRec.Is_Active__c = true;
            ctList.add(ctRec);
            
            Classification_Type__c ctRecord = new Classification_Type__c();
            ctRecord.name = 'Web Chat';
            ctRecord.Is_Active__c = true;
            ctList.add(ctRecord);
            
            insert ctList;
            system.assertEquals(ctList.size(), 2);

            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            
            CTCI_Junction__c ctciRecDec = new CTCI_Junction__c();
            ctciRecDec.Classification_Type__c = ctRec.id;
            ctciRecDec.Classification__c = classificationRecDec.Id;
            ctciRecDec.Intent__c = intentRecDec.id;
            ctciRecDec.IsActive__c = true;
            ctciList.add(ctciRecDec);
            insert ctciList;
            system.assertEquals(ctciList.size(), 2);            
            
            List<Case> lstCase = new List<Case>();
            Case caseRecord1 = new Case();
            caseRecord1.Owner_Queue__c = 'Cincinati CRU';
            caseRecord1.Status = 'In Progress';
            caseRecord1.AccountId = accountRec.id;
            caseRecord1.ownerId = usr.Id;
            caseRecord1.Classification_Type__c = 'Calls';
            caseRecord1.CTCI_List__c = ctciRec.Id;
            caseRecord1.Classification_id__c=lstClassification[0].id;
            caseRecord1.Intent_id__c = lstIntent[0].id;
            caseRecord1.Interacting_With__c = accountRec.id;
            caseRecord1.Interacting_With_Type__c = 'Member';
            caseRecord1.Interacting_About_Type__c = 'Member';
            caseRecord1.Policy_Member__c = policyMemRec.Id;
            caseRecord1.SubType__c = 'Creditable Coverage';
            caseRecord1.Guided_Process_Flag__c=true;
            lstCase.add(caseRecord1);
            
            Case caseRecord2 = new Case();
            caseRecord2.Owner_Queue__c = 'cincinati Calls';
            caseRecord2.Status = 'In Progress';
            caseRecord2.AccountId = accountRec.id;
            caseRecord2.ownerId = usr.Id;
            caseRecord2.Classification_Type__c = 'Calls (RSO)';
            caseRecord2.CTCI_List__c = ctciRec.Id;
            caseRecord2.Classification_id__c=lstClassification[0].id;
            caseRecord2.Intent_id__c = lstIntent[0].id;
            caseRecord2.Interacting_With__c = accountRec.id;
            caseRecord2.Interacting_With_Type__c = 'Member';
            caseRecord2.Interacting_About_Type__c = 'Member';
            caseRecord2.Policy_Member__c = policyMemRec.Id;
            caseRecord2.SubType__c = 'Creditable Coverage';
            caseRecord2.Guided_Process_Flag__c=true;
            lstCase.add(caseRecord2);
            
            Case caseRecord3 = new Case();
            caseRecord3.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord3.Status = 'In Progress';
            caseRecord3.AccountId = accountRec.id;
            caseRecord3.ownerId = usr.Id;
            caseRecord3.Classification_Type__c = 'Claims (RSO)';
            caseRecord3.CTCI_List__c = ctciRec.Id;
            caseRecord3.Classification_id__c=lstClassification[0].id;
            caseRecord3.Intent_id__c = lstIntent[0].id;
            caseRecord3.Interacting_With__c = accountRec.id;
            caseRecord3.Interacting_With_Type__c = 'Member';
            caseRecord3.Interacting_About_Type__c = 'Member';
            caseRecord3.Policy_Member__c = policyMemRec.Id;
            caseRecord3.Guided_Process_Flag__c=true;
            lstCase.add(caseRecord3);

            Case caseRecord4 = new Case();
            caseRecord4.Owner_Queue__c = 'Disenrollment0to3_Stg';
            caseRecord4.Status = 'In Progress';
            caseRecord4.AccountId = accountRec.id;
            caseRecord4.ownerId = usr1.Id;
            caseRecord4.Classification_Type__c = 'Claims (RSO)';
            caseRecord4.CTCI_List__c = ctciRec.Id;
            caseRecord4.Classification_id__c=lstClassification[0].id;
            caseRecord4.Intent_id__c = lstIntent[0].id;
            caseRecord4.Interacting_With__c = accountRec.id;
            caseRecord4.Interacting_With_Type__c = 'Member';
            caseRecord4.Interacting_About_Type__c = 'Member';
            caseRecord4.Policy_Member__c = policyMemRec.Id;
            caseRecord4.SubType__c = 'Creditable Coverage';
            caseRecord4.Guided_Process_Flag__c=true;
            lstCase.add(caseRecord4);
            
            Case caseRecord5 = new Case();
            caseRecord5.Owner_Queue__c = 'Incomplete0to3';
            caseRecord5.Status = 'In Progress';
            caseRecord5.AccountId = accountRec.id;
            caseRecord5.ownerId = usr1.Id;
            caseRecord5.Classification_Type__c = 'Claims (RSO)';
            caseRecord5.CTCI_List__c = ctciRecDec.Id;
            caseRecord5.Classification_id__c=lstClassification[1].id;
            caseRecord5.Intent_id__c = lstIntent[1].id;
            caseRecord5.Interacting_With__c = accountRec.id;
            caseRecord5.Interacting_With_Type__c = 'Member';
            caseRecord5.Interacting_About_Type__c = 'Member';
            caseRecord5.Policy_Member__c = policyMemRec.Id;
            caseRecord5.Complaint__c = 'No';
            caseRecord5.G_A_Rights_Given__c = 'No';
            caseRecord5.Omni_Eligible__c = true;
            caseRecord5.Case_Owner__c = 'Incomplete0to3';
             caseRecord5.SubType__c = 'Creditable Coverage';
            caseRecord5.Guided_Process_Flag__c=true;
            lstCase.add(caseRecord5);
            
            Case caseRecord6 = new Case();
            caseRecord6.Origin = 'Web Chat';
            caseRecord6.Status = 'In Progress';
            caseRecord6.AccountId = accountRec.id;
            caseRecord6.ownerId = usr.Id;            
            caseRecord6.Interacting_With__c = accountRec.id;
            caseRecord6.Interacting_With_Type__c = 'Member';
            caseRecord6.Interacting_About_Type__c = 'Member';
            caseRecord6.SubType__c = 'Creditable Coverage';
            caseRecord6.Guided_Process_Flag__c=true;
            lstCase.add(caseRecord6);
            
            Case caseRecord7 = new Case();
            caseRecord7.Origin = 'Mail';
            caseRecord7.Type = 'Customer Inquiry';
            caseRecord7.Owner_Queue__c = 'Cincinati CRU';
            caseRecord7.Status = 'In Progress';
            caseRecord7.AccountId = accountRec.id;
            caseRecord7.ownerId = usr.Id;
            caseRecord7.Classification_Type__c = 'Calls';
            caseRecord7.CTCI_List__c = ctciRec.Id;
            caseRecord7.Classification_id__c=lstClassification[0].id;
            caseRecord7.Intent_id__c = lstIntent[0].id;
            caseRecord7.Interacting_With__c = accountRec.id;
            caseRecord7.Interacting_With_Type__c = 'Member';
            caseRecord7.Interacting_About_Type__c = 'Member';
            caseRecord7.Policy_Member__c = policyMemRec.Id;
            caseRecord7.SubType__c = 'Creditable Coverage';
            caseRecord7.Guided_Process_Flag__c=true;
            caseRecord7.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Medicare Case').getRecordTypeId();
            lstCase.add(caseRecord7);
            
            Case caseRecord8 = new Case();
            caseRecord8.Origin = 'Mail';
            caseRecord8.Type = 'Customer Inquiry';
            caseRecord8.Owner_Queue__c = 'Cincinati CRU';
            caseRecord8.Status = 'In Progress';
            caseRecord8.AccountId = accountRec.id;
            caseRecord8.ownerId = usr.Id;
            caseRecord8.Classification_Type__c = 'Calls';
            caseRecord8.CTCI_List__c = ctciRec.Id;
            caseRecord8.Classification_id__c=lstClassification[0].id;
            caseRecord8.Intent_id__c = lstIntent[0].id;
            caseRecord8.Interacting_With__c = accountRec.id;
            caseRecord8.Interacting_With_Type__c = 'Member';
            caseRecord8.Interacting_About_Type__c = 'Member';
            caseRecord8.Member_Plan_Id__c = Memplan.Id;
            caseRecord8.SubType__c = 'Creditable Coverage';
            caseRecord8.Guided_Process_Flag__c=true;
            caseRecord8.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Medicare Case').getRecordTypeId();
            lstCase.add(caseRecord8);
            
            Classification_Type__c cT =[ Select Id from Classification_Type__c where name = 'Web Chat'];
            
            List<Work_Queue_Setup__c> wQSetUp = new List<Work_Queue_Setup__c>();
            Work_Queue_Setup__c wQSetUpRecord = new Work_Queue_Setup__c();
            wQSetUpRecord.Name = 'Web Chat';
            wQSetUpRecord.Service_Center__c = 'Web';
            wQSetUpRecord.Department__c = 'Chat';
            wQSetUpRecord.Classification_Type__c = ctRecord.Id;
            //wQSetUpRecord.Work_Queue_Name__c = 'Web_Chat';
            wQSetUpRecord.IsActive__c = true;
            wQSetUp.add(wQSetUpRecord);
            
            Work_Queue_Setup__c wQSetUpRecord1 = new Work_Queue_Setup__c();
            wQSetUpRecord1.Name = 'Louisville RSO Enrollment';
            wQSetUpRecord1.Service_Center__c = 'Web';
            wQSetUpRecord1.Department__c = 'Chat';
            wQSetUpRecord1.Classification_Type__c = ctRecord.Id;
            wQSetUpRecord1.IsActive__c = true;
            wQSetUp.add(wQSetUpRecord1);
            
            insert wQSetUp;
            
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = '2513665';
            crm1.Scrum_Team__c = 'test';
            crm1.Story_Title__c = 'test';
            crm1.Release_Name__c = '1/2/2022';
            insert crm1;  
            
            Work_Queue_Priority_CriteriaLimit_Value__c cv = new Work_Queue_Priority_CriteriaLimit_Value__c();
            cv.Name='Criteria';
            cv.Date__c= System.today();
            cv.Value__c = 25;
            Insert cv;            
            
            insert lstCase;            
            system.assertEquals(lstCase[0].Status=='In Progress',true);
            
            Task oTask1 = new Task(WhatId = lstCase[0].id,Type='Work task');
            insert oTask1 ;             
            system.assertEquals(oTask1.type=='Work task',true);
                
            List<HUMEncryptedFields__c> lstCS = new List<HUMEncryptedFields__c>();
            lstCS.add(new HUMEncryptedFields__c(Name='CommentBod',ObjectName__c='CaseComment',FieldName__c='CommentBody',EncryptionScheme__c = 'Stateless_AES_Encryption_without_Search'));
            insert lstCS;
            system.assertEquals(lstCS[0].Name=='CommentBod',true);
            insert new HUMConstants__c [] {new HUMConstants__c( Name = 'HUMCaseComment', StringValue__c = 'CaseComment', IntegerValue__c = 0),
                new HUMConstants__c(Name = 'HUMStatelessAESEncryptionwithoutSearch', StringValue__c = 'Stateless_AES_Encryption_without_Search', IntegerValue__c = 0),
                new HUMConstants__c(Name = 'ZERO', StringValue__c = '0', IntegerValue__c = 0),
                new HUMConstants__c(Name = 'HUMExclamation', StringValue__c = '!', IntegerValue__c = 0),
                new HUMConstants__c(Name = 'HUMDoubleStar', StringValue__c = '**', IntegerValue__c = 0),
                new HUMConstants__c(Name = 'AMP', StringValue__c = '&', IntegerValue__c = 0),
                new HUMConstants__c(Name = 'HUMValueOne', StringValue__c = '1', IntegerValue__c = 1),
                new HUMConstants__c(Name = 'HUMOpenBrace', StringValue__c = '(', IntegerValue__c = 0),
                new HUMConstants__c(Name = 'HUMAT', StringValue__c = '@', IntegerValue__c = 0),
                new HUMConstants__c(Name = 'HUMDash', StringValue__c = '-', IntegerValue__c = 0),
                new HUMConstants__c(Name = 'HUMCommentBody', StringValue__c = 'CommentBody', IntegerValue__c = 0) ,
                new HUMConstants__c(Name = 'HUMLISTSIZEZERO', StringValue__c = '0', IntegerValue__c = 0),
                new HUMConstants__c(Name = 'HUMConstantOne', StringValue__c = '1', IntegerValue__c = 1),
                new HUMConstants__c(Name = 'HUMViewCriteriaLimit', StringValue__c = '23', IntegerValue__c = 23),
                new HUMConstants__c(Name = 'HUMDefaultBatchLimit', StringValue__c = '5', IntegerValue__c = 5),
                new HUMConstants__c(Name = 'WEBCHAT_DEFAULT_LIVEAGENT_NAME',StringValue__c='liveagent Site Guest User',IntegerValue__c=0),
                new HUMConstants__c(Name = 'HUMWebChatText',StringValue__c='Web Chat',IntegerValue__c=0)
            };
            insert new HUMTriggerSwitch__c (name='Case', Exeute_Trigger__c=true);
            insert new HUMQueueViewBatchSettings__c ( name= 'QueueViewBatchSetup', TriggerExecutionRequired__c= true);
            insert new HUMCaseUpdateAllowedProfies__c [] {new HUMCaseUpdateAllowedProfies__c (name='Customer Care Specialist'),
                new HUMCaseUpdateAllowedProfies__c (name='ETL API Access')
            };

        }
    }
    
    /*
    * This method is used to test All the methods of CaseTriggerHandler_HUM class
    * @param   None
    * @return  None
    */
    static testMethod void testAllHandlerMethods()
    {
        User testUser = [Select Id,Name,LastName from User where LastName='test humcrmsCaseTriggerUser1'];
        System.assertEquals(testUser.LastName, 'test humcrmsCaseTriggerUser1');
        Case cas = [Select type, subtype__c, Guided_Process_Flag__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__c,Classification_Id__c,Intent_Id__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c from case WHERE Subtype__c = null limit 1];
        list<CTCI_Junction__c> lstCTCI = [SELECT Id, Classification_Type__c, Classification__c, Intent__c FROM CTCI_Junction__c WHERE Classification__r.Name = 'Humana Pharmacy' limit 1];             
        
        System.runAs(testUser)
        {
            Test.startTest();
            GLOBAL_CONSTANT_HUM gblConstant = new GLOBAL_CONSTANT_HUM();
            List<Case> lstNewCase= [Select Classification_Id__c,Intent_Id__c, Member_Plan_Id__c,Policy_Member__c,Interacting_with__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c,SubType__c,Guided_Process_Flag__c from case where status='In Progress'];
            List<Case> lstNewCase1= [Select Classification_Id__c,Intent_Id__c, Member_Plan_Id__c,Policy_Member__c,Interacting_with__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c,SubType__c,Guided_Process_Flag__c from case where Member_Plan_Id__c !=''];
            Map<Id, Case> mapCases= new Map<ID,Case>();
            for(Case caseRecord :  lstNewCase)
            {
             mapCases.put(caseRecord.id, caseRecord);
            }
            String profileName = HumUtilityHelper.getCurrentUserProfileName();
            CaseTriggerHandler_HUM.ProcessCaseBeforeUpdate (lstNewCase,mapCases);
            CaseTriggerHandler_HUM.processCaseBeforeInsert(lstNewCase,profileName );
            CaseTriggerHandler_HUM.reassignWebChatOwner(lstNewCase);
            CaseTriggerHandler_HUM.processCaseAfterInsert(lstNewCase,profileName );
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeInsert(lstNewCase);
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeInsert(lstNewCase1);
            CaseTriggerHandler_HUM.callMilestoneTrackService(lstNewCase,null);
            cas.subtype__c = GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE;
            cas.status= GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;
            cas.Guided_Process_Flag__c = true;
            cas.Classification_Id__c = lstCTCI[0].Classification__c;
            cas.Intent_Id__c = lstCTCI[0].Intent__c;
            cas.CTCI_List__c = lstCTCI[0].Id;
            update cas;
            
            Test.stopTest();
        }
        
    }
    
    /*
    * This method is used to test methods  for ETL API access profile of CaseTriggerHandler_HUM class
    * @param   None
    * @return  None
    */
     static testMethod void testAllHandlerMethods_ETL()
    {
        User testUser2 = [Select Id,Name,LastName,profile.name from User where LastName= 'test humcrmsCaseTriggerUser2'];
        System.assertEquals(testUser2.LastName,  'test humcrmsCaseTriggerUser2');
        Case cas = [Select id, CTCI_List__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__c from case limit 1];
        
        System.runAs(testUser2)
        {
            Test.startTest();
            List<Case> lstNewCase= [Select id,Open_Work_Task_Count__c,Member_Plan_Id__c,Policy_Member__c,casenumber,CTCI_List__c,Omni_Eligible__c,Status,Owner_Queue__c,Case_Owner__c,SubType__c,Guided_Process_Flag__c,Work_Queue_View_Name__c from case where status='In Progress' AND Policy_Member__r.name = '12345' limit 1];
            List<Case> lstNewCase1= [Select Classification_Id__c,Intent_Id__c, Member_Plan_Id__c,Policy_Member__c,Interacting_with__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c,SubType__c,Guided_Process_Flag__c from case where Member_Plan_Id__r.name ='12345' limit 1];
            List<policy_Member__c> polMem = [select id, name from policy_Member__c where name = '12345678' limit 1];
            List<MemberPlan> memplan = [select id, name from MemberPlan where name = '12345678' limit 1];
            Map<Id, Case> mapCases= new Map<ID,Case>();
            Map<Id, Case> mapCases1= new Map<ID,Case>();
            Map<Id, Case> mapNewCases=new Map<ID,Case>();
            for(Case caseRecord :  lstNewCase)
            {
                mapCases.put(caseRecord.id, caseRecord);
            }
            lstNewCase[0].Policy_Member__c = polMem[0].id;
            update lstNewCase;
            
            for(Case caseRecord1 :  lstNewCase1)
            {
                mapCases1.put(caseRecord1.id, caseRecord1);
            }
            lstNewCase1[0].Member_Plan_Id__c = memplan[0].id;
            update lstNewCase1;
            String profileName = testUser2.profile.name; 
            CaseTriggerHandler_HUM.ProcessCaseBeforeUpdate (lstNewCase,mapCases);
            lstNewCase[0].Policy_Member__c = null;
            update lstNewCase;
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeUpdate (lstNewCase,mapCases);
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeUpdate (lstNewCase1,mapCases1);
            CaseTriggerHandler_HUM.processCaseBeforeInsert(lstNewCase,profileName );
            CaseTriggerHandler_HUM.ProcessCaseBeforeUpdate(lstNewCase,mapCases1);
            CaseTriggerHandler_HUM.processOmniCaseAssignmentRuleEvaluation (lstNewCase);
            CaseTriggerHandler_HUM.processCaseAfterInsert(lstNewCase,profileName);
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeInsert(lstNewCase);
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeInsert(lstNewCase1);
            update cas;
            
            CaseTriggerHandler_HUM.ProcessCaseAfterUpdate(lstNewCase,mapCases,mapNewCases);
            Test.stopTest();
        }
        
    }

    /*          
    * This method is used to test methods  for Mulesoft Accesss of CaseTriggerHandler_HUM class
    * @param   None
    * @return  None
    */
    static testMethod void testAllHandlerMethods_Mule()
    {
        User testUser2 = [Select Id,Name,LastName,profile.name from User where LastName= 'test humcrmsCaseTriggerUser3'];
        System.assertEquals(testUser2.LastName,  'test humcrmsCaseTriggerUser3');
        Case cas = [Select id, CTCI_List__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__c from case limit 1];
        System.runAs(testUser2)
        {
            Test.startTest();
            List<Case> lstNewCase= [Select id,Open_Work_Task_Count__c,Member_Plan_Id__c,Policy_Member__c,casenumber,CTCI_List__c,Omni_Eligible__c,Status,Owner_Queue__c,Case_Owner__c,SubType__c,Guided_Process_Flag__c,Work_Queue_View_Name__c from case where status='In Progress' AND Policy_Member__r.name = '12345' limit 1];
            List<Case> lstNewCase1= [Select Classification_Id__c,Intent_Id__c, Member_Plan_Id__c,Policy_Member__c,Interacting_with__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c,SubType__c,Guided_Process_Flag__c from case where Member_Plan_Id__r.name ='12345' limit 1];
            List<policy_Member__c> polMem = [select id, name from policy_Member__c where name = '12345678' limit 1];
            List<MemberPlan> memplan = [select id, name from MemberPlan where name = '12345678' limit 1];
            Map<Id, Case> mapCases= new Map<ID,Case>();
            Map<Id, Case> mapCases1= new Map<ID,Case>();
            Map<Id, Case> mapNewCases=new Map<ID,Case>();
            for(Case caseRecord :  lstNewCase)
            {
                mapCases.put(caseRecord.id, caseRecord);
            }
            lstNewCase[0].Policy_Member__c = polMem[0].id;
            update lstNewCase;
            for(Case caseRecord1 :  lstNewCase1)
            {
                mapCases1.put(caseRecord1.id, caseRecord1);
            }
            lstNewCase1[0].Member_Plan_Id__c = memplan[0].id;
            update lstNewCase1;
            String profileName = testUser2.profile.name; 
            CaseTriggerHandler_HUM.ProcessCaseBeforeUpdate (lstNewCase,mapCases);
            lstNewCase[0].Policy_Member__c = null;
            update lstNewCase;
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeUpdate (lstNewCase,mapCases);
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeUpdate (lstNewCase1,mapCases1);
            CaseTriggerHandler_HUM.processCaseBeforeInsert(lstNewCase,profileName );
            CaseTriggerHandler_HUM.ProcessCaseBeforeUpdate(lstNewCase,mapCases1);
            CaseTriggerHandler_HUM.processOmniCaseAssignmentRuleEvaluation (lstNewCase);
            CaseTriggerHandler_HUM.processCaseAfterInsert(lstNewCase,profileName);
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeInsert(lstNewCase);
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeInsert(lstNewCase1);
            update cas;
            CaseTriggerHandler_HUM.ProcessCaseAfterUpdate(lstNewCase,mapCases,mapNewCases);
            Test.stopTest();
        }
    }
    /*          
    * This method is used to test All the methods of CaseTriggerHandler_HUM class for Exception     
    * @param   None     
    * @return  None     
    */      
    static testMethod void testAllHandlerMethodsForException()      
    {       
        User testUser = [Select Id,Name,LastName from User where LastName='test humcrmsCaseTriggerUser1'];
        System.assertEquals(testUser.LastName, 'test humcrmsCaseTriggerUser1');
        System.runAs(testUser)      
        {       
            Test.startTest();       
            List<Case> lstNewCase = null;       
            Map<Id, Case> mapCases= null;       
            Map<Id, Case> mapNewCases= null;
            String profileName = HumUtilityHelper.getCurrentUserProfileName();
            CaseTriggerHandler_HUM.ProcessCaseBeforeUpdate (lstNewCase,mapCases);       
            CaseTriggerHandler_HUM.ProcessCaseAfterUpdate(lstNewCase,mapCases,mapNewCases);     
            CaseTriggerHandler_HUM.processCaseBeforeInsert(lstNewCase,profileName );     
            CaseTriggerHandler_HUM.processCaseAfterInsert(lstNewCase,profileName);
            CaseTriggerHandler_HUM.reassignWebChatOwner(lstNewCase); 
            Test.stopTest();        
        }       
    }
    
    @isTest
    static void testMilestoneTrackService(){   
        User etlUser = [Select Id,Name,LastName from User where LastName='test humcrmsCaseTriggerUser2'];
        System.assertEquals(etlUser.LastName=='test humcrmsCaseTriggerUser2', true);
        System.runAs(etlUser)
        {
        Test.startTest();
            List<Case> lstNewCase= [Select id,type, Origin,Open_Work_Task_Count__c,casenumber,CTCI_List__c,Omni_Eligible__c,Status,Owner_Queue__c,Case_Owner__c,SubType__c,Guided_Process_Flag__c from case where Type = 'Customer Inquiry' and Origin = 'Mail'];
            Map<Id, Case> mapCases= new Map<ID,Case>();
            Map<Id, Case> mapNewCases=new Map<ID,Case>();
            for(Case caseRecord :  lstNewCase)
            {
                mapCases.put(caseRecord.id, caseRecord);
            }
           String profileName = HumUtilityHelper.getCurrentUserProfileName();
           CaseTriggerHandler_HUM.processCaseAfterInsert(lstNewCase,profileName );
           Case c = [Select Id, Origin, Type, Subtype__c,RecordTypeId from Case where Type = 'Customer Inquiry' and Origin = 'Mail' limit 1];
            c.Subtype__c = 'Best Available Evidence';
            c.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Medicare Case').getRecordTypeId();
            update c;
            
            CaseTriggerHandler_HUM.ProcessCaseAfterUpdate(lstNewCase,mapCases,mapNewCases);
            Test.stopTest();
        }        
        
    }
    
    /*
    * This method is used to test methods  for Lookup policy Member and member plan object
    * 
    * @param   None
    * @return  None
    */
     static testMethod void testLookupMemberPlanandpolicyMember()
    {
         User testUser = [Select Id,Name,LastName from User where LastName='test humcrmsCaseTriggerUser1'];
        System.assertEquals(testUser.LastName, 'test humcrmsCaseTriggerUser1');
       System.runAs(testUser)
        {
            Test.startTest();
            List<Case> lstNewCase= [Select id,Open_Work_Task_Count__c,Member_Plan_Id__c,Policy_Member__c,casenumber,CTCI_List__c,Omni_Eligible__c,Status,Owner_Queue__c,Case_Owner__c,SubType__c,Guided_Process_Flag__c,Work_Queue_View_Name__c from case where status='In Progress' AND Policy_Member__r.name = '12345' limit 1];
            List<Case> lstNewCase1= [Select Classification_Id__c,Intent_Id__c, Member_Plan_Id__c,Policy_Member__c,Interacting_with__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c,SubType__c,Guided_Process_Flag__c from case where Member_Plan_Id__r.name ='12345' limit 1];
            List<policy_Member__c> polMem = [select id, name from policy_Member__c where name = '12345678' limit 1];
            List<MemberPlan> memplan = [select id, name from MemberPlan where name = '12345678' limit 1];
            Map<Id, Case> mapCases= new Map<ID,Case>();
            Map<Id, Case> mapCases1= new Map<ID,Case>();
            Map<Id, Case> mapNewCases=new Map<ID,Case>();
            for(Case caseRecord :  lstNewCase)
            {
                mapCases.put(caseRecord.id, caseRecord);
            }
            lstNewCase[0].Policy_Member__c = polMem[0].id;
            update lstNewCase;
            
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeUpdate(lstNewCase,mapCases);
           
            Test.stopTest();
        }
        
    }
    /*
    * This method is used to test methods  for Lookup policy Member and member plan object
    * 
    * @param   None
    * @return  None
    */
     static testMethod void testLookupMemberPlanandpolicyMember1()
    {
         User testUser = [Select Id,Name,LastName from User where LastName='test humcrmsCaseTriggerUser1'];
        System.assertEquals(testUser.LastName, 'test humcrmsCaseTriggerUser1');
       System.runAs(testUser)
        {
            Test.startTest();
            List<Case> lstNewCase= [Select id,Open_Work_Task_Count__c,Member_Plan_Id__c,Policy_Member__c,casenumber,CTCI_List__c,Omni_Eligible__c,Status,Owner_Queue__c,Case_Owner__c,SubType__c,Guided_Process_Flag__c,Work_Queue_View_Name__c from case where status='In Progress' AND Policy_Member__r.name = '12345' limit 1];
            List<Case> lstNewCase1= [Select Classification_Id__c,Intent_Id__c, Member_Plan_Id__c,Policy_Member__c,Interacting_with__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c,SubType__c,Guided_Process_Flag__c from case where Member_Plan_Id__r.name ='12345' limit 1];
            List<policy_Member__c> polMem = [select id, name from policy_Member__c where name = '12345678' limit 1];
            List<MemberPlan> memplan = [select id, name from MemberPlan where name = '12345678' limit 1];
            Map<Id, Case> mapCases= new Map<ID,Case>();
            Map<Id, Case> mapCases1= new Map<ID,Case>();
            Map<Id, Case> mapNewCases=new Map<ID,Case>();
            for(Case caseRecord :  lstNewCase)
            {
                mapCases.put(caseRecord.id, caseRecord);
            }
            
            lstNewCase[0].Policy_Member__c = null;
            update lstNewCase;
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeUpdate(lstNewCase,mapCases);
             lstNewCase[0].Policy_Member__c = null;
            update lstNewCase;
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeUpdate(lstNewCase,mapCases);
            
            for(Case caseRecord1 :  lstNewCase1)
            {
                mapCases1.put(caseRecord1.id, caseRecord1);
            }
            lstNewCase1[0].Member_Plan_Id__c = memplan[0].id;
            update lstNewCase1;
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeUpdate(lstNewCase1,mapCases1);
            
            Test.stopTest();
        }
        
    }
    
    /*
    * This method is used to test methods  for Lookup policy Member and member plan object
    * 
    * @param   None
    * @return  None
    */
     static testMethod void testLookupMemberPlanandpolicyMember2()
    {
         User testUser = [Select Id,Name,LastName from User where LastName='test humcrmsCaseTriggerUser1'];
        System.assertEquals(testUser.LastName, 'test humcrmsCaseTriggerUser1');
       System.runAs(testUser)
        {
            Test.startTest();
            List<Case> lstNewCase= [Select id,Open_Work_Task_Count__c,Member_Plan_Id__c,Policy_Member__c,casenumber,CTCI_List__c,Omni_Eligible__c,Status,Owner_Queue__c,Case_Owner__c,SubType__c,Guided_Process_Flag__c,Work_Queue_View_Name__c from case where status='In Progress' AND Policy_Member__r.name = '12345' limit 1];
            List<Case> lstNewCase1= [Select Classification_Id__c,Intent_Id__c, Member_Plan_Id__c,Policy_Member__c,Interacting_with__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c,SubType__c,Guided_Process_Flag__c from case where Member_Plan_Id__r.name ='12345' limit 1];
            List<policy_Member__c> polMem = [select id, name from policy_Member__c where name = '12345678' limit 1];
            List<MemberPlan> memplan = [select id, name from MemberPlan where name = '12345678' limit 1];
            Map<Id, Case> mapCases= new Map<ID,Case>();
            Map<Id, Case> mapCases1= new Map<ID,Case>();
            Map<Id, Case> mapNewCases=new Map<ID,Case>();
            for(Case caseRecord1 :  lstNewCase1)
            {
                mapCases1.put(caseRecord1.id, caseRecord1);
            }
            lstNewCase1[0].Member_Plan_Id__c = memplan[0].id;
            update lstNewCase1;
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeUpdate(lstNewCase1,mapCases1);
            lstNewCase1[0].Member_Plan_Id__c = null;
            update lstNewCase1;
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeUpdate(lstNewCase1,mapCases1);
            Test.stopTest();
        }
        
    }
    
    /*
    * This method is used to test methods  for Lookup policy Member and member plan object
    * 
    * @param   None
    * @return  None
    */
     static testMethod void testLookupMemberPlanandpolicyMember3()
    {
         User testUser = [Select Id,Name,LastName from User where LastName='test humcrmsCaseTriggerUser1'];
        System.assertEquals(testUser.LastName, 'test humcrmsCaseTriggerUser1');
       System.runAs(testUser)
        {
            Test.startTest();
            List<Case> lstNewCase= [Select id,Open_Work_Task_Count__c,Member_Plan_Id__c,Policy_Member__c,casenumber,CTCI_List__c,Omni_Eligible__c,Status,Owner_Queue__c,Case_Owner__c,SubType__c,Guided_Process_Flag__c,Work_Queue_View_Name__c from case where status='In Progress' AND Policy_Member__r.name = '12345' limit 1];
            List<Case> lstNewCase1= [Select Classification_Id__c,Intent_Id__c, Member_Plan_Id__c,Policy_Member__c,Interacting_with__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c,SubType__c,Guided_Process_Flag__c from case where Member_Plan_Id__r.name ='12345' limit 1];
            List<policy_Member__c> polMem = [select id, name from policy_Member__c where name = '12345678' limit 1];
            List<MemberPlan> memplan = [select id, name from MemberPlan where name = '12345678' limit 1];
            Map<Id, Case> mapCases= new Map<ID,Case>();
            Map<Id, Case> mapCases1= new Map<ID,Case>();
            Map<Id, Case> mapNewCases=new Map<ID,Case>();
            for(Case caseRecord1 :  lstNewCase1)
            {
                mapCases1.put(caseRecord1.id, caseRecord1);
            }
            lstNewCase1[0].Member_Plan_Id__c = null;
            update lstNewCase1;
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeUpdate(lstNewCase1,mapCases1);
            Test.stopTest();
        }
        
    }
    
    /*
    * This method is used to test methods  for metadat object
    * 
    * @param   None
    * @return  None
    */
     static testMethod void testmetadata()
    {
         User testUser = [Select Id,Name,LastName from User where LastName='test humcrmsCaseTriggerUser1'];
        System.assertEquals(testUser.LastName, 'test humcrmsCaseTriggerUser1');
       System.runAs(testUser)
        {
            Test.startTest();
            List<Case> lstNewCase= [Select id,Open_Work_Task_Count__c,RecordTypeId,Member_Plan_Id__c,Policy_Member__c,casenumber,CTCI_List__c,Omni_Eligible__c,Status,Owner_Queue__c,Case_Owner__c,SubType__c,Guided_Process_Flag__c,Work_Queue_View_Name__c from case where status='In Progress' AND Policy_Member__r.name = '12345' limit 1];
            
            Map<Id, Case> mapCases1= new Map<ID,Case>();
            for(Case caseRecord1 :  lstNewCase)
            {
                mapCases1.put(caseRecord1.id, caseRecord1);
            }
            lstNewCase[0].Owner_Queue__c = 'HGS Member Correspondence';
            lstNewCase[0].Work_Queue_View_Name__c = 'Provider Misroutes';
            update lstNewCase;
            CaseTriggerHandler_HUM.ProcessCaseBeforeUpdate(lstNewCase,mapCases1);
            Test.stopTest();
        }
        
    }
	
	/*
    * This method is used to test ETL user changes
    * @param   None
    * @return  None
    */
    static testMethod void testETLUserchanges()
    {
        User testUser = [Select Id,Name,LastName from User where LastName='test TUser1'];
        System.assertEquals(testUser.LastName, 'test TUser1');
        Case cas = [Select type, subtype__c, Guided_Process_Flag__c, CTCI_List__r.Classification__c, CTCI_List__r.Intent__c,Classification_Id__c,Intent_Id__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c from case WHERE Subtype__c = null limit 1];
        list<CTCI_Junction__c> lstCTCI = [SELECT Id, Classification_Type__c, Classification__c, Intent__c FROM CTCI_Junction__c WHERE Classification__r.Name = 'Humana Pharmacy' limit 1];             
        
        System.runAs(testUser)
        {
            Test.startTest();
            
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = 'ETL Visibility Code skip';
            crm1.Scrum_Team__c = 'test';
            crm1.Story_Title__c = 'test';
            crm1.Release_Name__c = '1/2/2022';
            insert crm1;
            
            string susername = userinfo.getUserName();
            ETL_User_Check__c etluser = new ETL_User_Check__c();
            etluser.Name = susername;
            insert etluser;
            
            GLOBAL_CONSTANT_HUM gblConstant = new GLOBAL_CONSTANT_HUM();
            List<Case> lstNewCase= [Select Classification_Id__c,Intent_Id__c, Member_Plan_Id__c,Policy_Member__c,Interacting_with__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c,SubType__c,Guided_Process_Flag__c from case where status='In Progress'];
            List<Case> lstNewCase1= [Select Classification_Id__c,Intent_Id__c, Member_Plan_Id__c,Policy_Member__c,Interacting_with__c, id,Open_Work_Task_Count__c,casenumber,origin,Case_Owner__c,OwnerId,Owner_Queue__c,Omni_Eligible__c,Status,CTCI_List__c,SubType__c,Guided_Process_Flag__c from case where Member_Plan_Id__c !=''];
            Map<Id, Case> mapCases= new Map<ID,Case>();
            for(Case caseRecord :  lstNewCase)
            {
             mapCases.put(caseRecord.id, caseRecord);
            }
            String profileName = HumUtilityHelper.getCurrentUserProfileName();
            CaseTriggerHandler_HUM.ProcessCaseBeforeUpdate (lstNewCase,mapCases);
            CaseTriggerHandler_HUM.processCaseBeforeInsert(lstNewCase,profileName );
            CaseTriggerHandler_HUM.processCaseAfterInsert(lstNewCase,profileName );
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeInsert(lstNewCase);
            CaseTriggerHandler_HUM.MemberPlanAndPolicyMemberLookupBeforeInsert(lstNewCase1);
            
            Test.stopTest();
        }
        
    }
}