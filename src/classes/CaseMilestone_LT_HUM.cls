/*******************************************************************************************************************************
Apex Class Name : CaseMilestone_LT_HUM
Version         : 1.0
Created On      : 12/05/2022
Function        : This class is Test class for CaseMilestone_LC_HUM which is used to write Milestone to BAM while creating or updating case


Modification Log:
* Version          Developer Name                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Jasmeen Shangari                12/05/2022               Original Version
*    1.1           Jasmeen Shangari 			   12/22/2022				US-4035121 Milestone writing for BAM tracking for MET & SubType change
*    1.2           Jasmeen Shangari                02/03/2023               US 4178419-Added Changes for TRR Milestone
*/
@isTest
private class CaseMilestone_LT_HUM {

    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        
        User usr = HUMTestDataHelperTest.createTestUser('CaseEditUser1','Customer Care Supervisor');
        insert usr;
        system.assertEquals(usr.lastname=='test CaseEditUser1',true);
        
        PermissionSet pRSOHP = [SELECT ID From PermissionSet WHERE Name = 'CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = usr.id, PermissionSetId = pRSOHP.id);
                
        system.runAs(usr)
        { 
            
            list<Case_Milestone__c> lstCustomSettings = new list<Case_Milestone__c>();
            lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-001', Doc_Type__c = 't1', Sub_Type__c = 'Creditable Coverage', Milestone__c = '909', Origin__c = 'Inbound Call', Is_Start_Milestone__c = true));
            lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-002', Doc_Type__c = 't2', Sub_Type__c = 'Incomplete ISNP', Milestone__c = '854', Origin__c = 'Inbound Call', Is_Start_Milestone__c = false));
            lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-003', Doc_Type__c = 't2', Sub_Type__c = 'Cancellation of Enrollment', Milestone__c = '870', Origin__c = 'Inbound Call', Is_Start_Milestone__c = true));

            insert lstCustomSettings; 
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

            MET_Task__c oTask = new MET_Task__c();
            oTask.Name = 'Test Task';
            oTask.Is_Active__c = true;
            oTask.Case_Subtype__c = 'Routine';
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
            oSource.Launch_EMME__c = true;
            insert oSource; 
            System.assertEquals(oSource.Name,'Test Source');
			
            MET_Task__c oTask1 = new MET_Task__c();
            oTask1.Name = 'Test Task1';
            oTask1.Is_Active__c = true;
            oTask1.Case_Subtype__c = 'TRR';
            insert oTask1;
            System.assertEquals(oTask1.Name,'Test Task1');
            
            MET_Action__c oAction1 = new MET_Action__c();
            oAction1.Name = 'Test Action1';
            oAction1.Is_Active__c = true;
            insert oAction1;
            System.assertEquals(oAction1.Name,'Test Action1');
    
            MET_Source__c oSource1 = new MET_Source__c();
            oSource1.Name = 'Test Source1';
            oSource1.Is_Active__c = true; 
            oSource1.Launch_EMME__c = true;
            insert oSource1; 
            System.assertEquals(oSource1.Name,'Test Source1');	

            MET_Task__c oTask2 = new MET_Task__c();
            oTask2.Name = 'Test Task2';
            oTask2.Is_Active__c = true;
            oTask2.Case_Subtype__c = 'TRR';
            insert oTask2;
            System.assertEquals(oTask2.Name,'Test Task2');
            
            MET_Action__c oAction2 = new MET_Action__c();
            oAction2.Name = 'Test Action2';
            oAction2.Is_Active__c = true;
            insert oAction2;
            System.assertEquals(oAction2.Name,'Test Action2');
    
            MET_Source__c oSource2 = new MET_Source__c();
            oSource2.Name = 'Test Source2';
            oSource2.Is_Active__c = true; 
            oSource2.Launch_EMME__c = true;
            insert oSource2; 
            System.assertEquals(oSource2.Name,'Test Source2');				
    
            MET_Milestone__c oMetMilestone = new MET_Milestone__c();
            oMetMilestone.Case_Type__c = 'Customer Inquiry';
            oMetMilestone.Case_Subtype__c = 'Creditable Coverage';
            oMetMilestone.Action__c = oAction.id;
            oMetMilestone.Source_Name__c =  oSource.id;
            oMetMilestone.Task__c =  oTask.id;
            oMetMilestone.Is_Active__c = true;
            oMetMilestone.Is_Milestone__c = true;     
            insert oMetMilestone;
            System.assertEquals(oMetMilestone.Action__c,oAction.id);
        
            MET_Milestone__c oMetMilestone1 = new MET_Milestone__c();
			oMetMilestone1.name ='1932';
            oMetMilestone1.Case_Type__c = 'TRR Inquiry';
            oMetMilestone1.Case_Subtype__c = 'TRR';
            oMetMilestone1.Action__c = oAction.id;
            oMetMilestone1.Source_Name__c =  oSource.id;
            oMetMilestone1.Task__c =  oTask.id;
            oMetMilestone1.Is_Active__c = true;
            oMetMilestone1.Is_Milestone__c = true; 
            oMetMilestone1.met_entries_assoc__c = 'Denial with Letter';
            insert oMetMilestone1;      
            System.assertEquals(oMetMilestone1.Action__c,oAction.id);     
       
            MET_Milestone__c oMetMilestone2 = new MET_Milestone__c();
			oMetMilestone2.name ='1832';
            oMetMilestone2.Case_Type__c = 'TRR Inquiry';
            oMetMilestone2.Case_Subtype__c = 'TRR';
            oMetMilestone2.Action__c = oAction1.id;
            oMetMilestone2.Source_Name__c =  oSource1.id;
            oMetMilestone2.Task__c =  oTask1.id;
            oMetMilestone2.Is_Active__c = true;
            oMetMilestone2.Is_Milestone__c = true; 
            oMetMilestone2.met_entries_assoc__c = 'Denial with Letter';
            insert oMetMilestone2;      
            System.assertEquals(oMetMilestone2.Action__c,oAction1.id); 
			
            MET_Milestone__c oMetMilestone3 = new MET_Milestone__c();
			oMetMilestone3.name ='898';
            oMetMilestone3.Case_Type__c = 'TRR Inquiry';
            oMetMilestone3.Case_Subtype__c = 'TRR';
            oMetMilestone3.Action__c = oAction2.id;
            oMetMilestone3.Source_Name__c =  oSource2.id;
            oMetMilestone3.Task__c =  oTask2.id;
            oMetMilestone3.Is_Active__c = true;
            oMetMilestone3.Is_Milestone__c = true; 
            oMetMilestone3.met_entries_assoc__c = 'Denial with Letter';
            insert oMetMilestone3;      
            System.assertEquals(oMetMilestone3.Action__c,oAction2.id); 			
			
            Case caseRecord8 = new Case();
            caseRecord8.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
            caseRecord8.Case_Owner__c = 'Louisville RSO Calls Support';
            caseRecord8.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord8.AccountId = accountRec.id;
            caseRecord8.ownerId = usr.Id;
            caseRecord8.Subtype__c = 'Creditable Coverage';
            caseRecord8.Guided_Process_Flag__c = true;
            caseRecord8.Classification_Type__c = 'Calls (RSO)';
            caseRecord8.Classification_id__c=lstClassification[0].id;
            caseRecord8.Intent_id__c = lstIntent[0].id;
            caseRecord8.Interacting_With__c = accountRec.id;
            caseRecord8.Interacting_With_Type__c = 'Member';   
            caseRecord8.Interacting_About_Type__c = 'Member';
            caseRecord8.Type = 'Customer Inquiry';
            caseRecord8.Origin = 'Inbound Call';
            caseRecord8.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert caseRecord8;
            system.assertEquals(caseRecord8.Case_Owner__c =='Louisville RSO Calls Support',true);


            Case caseRecord1 = new Case();
            caseRecord1.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
            caseRecord1.Case_Owner__c = 'Louisville RSO Calls Support';
            caseRecord1.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord1.AccountId = accountRec.id;
            caseRecord1.ownerId = usr.Id;
            caseRecord1.Subtype__c = 'Cancellation of Enrollment';
            caseRecord1.Guided_Process_Flag__c = true;
            caseRecord1.Classification_Type__c = 'Calls (RSO)';
            caseRecord1.Classification_id__c=lstClassification[0].id;
            caseRecord1.Intent_id__c = lstIntent[0].id;
            caseRecord1.Interacting_With__c = accountRec.id;
            caseRecord1.Interacting_With_Type__c = 'Member';   
            caseRecord1.Interacting_About_Type__c = 'Member';
            caseRecord1.Origin = 'Inbound Call';
            caseRecord1.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            caseRecord1.Type = 'Customer Inquiry';
            caseRecord1.DCN__c = '123456789';
            insert caseRecord1;
            system.assertEquals(caseRecord1.Subtype__c =='Cancellation of Enrollment',true);         
            
            Case caseRecord2 = new Case();
            caseRecord2.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
            caseRecord2.Case_Owner__c = 'Louisville RSO Calls Support';
            caseRecord2.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord2.AccountId = accountRec.id;
            caseRecord2.ownerId = usr.Id;
            caseRecord2.Subtype__c = 'TRR';
            caseRecord2.Guided_Process_Flag__c = true;
            caseRecord2.Classification_Type__c = 'Calls (RSO)';
            caseRecord2.Classification_id__c=lstClassification[0].id;
            caseRecord2.Intent_id__c = lstIntent[0].id;
            caseRecord2.Interacting_With__c = accountRec.id;
            caseRecord2.Interacting_With_Type__c = 'Member';   
            caseRecord2.Interacting_About_Type__c = 'Member';
            caseRecord2.Origin = 'CRMTRR';
            caseRecord2.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            caseRecord2.Type = 'TRR Inquiry';
            caseRecord2.DCN__c = '123456789';
            insert caseRecord2;
            system.assertEquals(caseRecord2.Subtype__c == 'TRR',true);   


			list<Enrollment_Track_History__c> lstTRRTracks = new list<Enrollment_Track_History__c>();
			
			Enrollment_Track_History__c TrackHisRec = new Enrollment_Track_History__c();        
			TrackHisRec.Milestone_Id__c = '1905';
			TrackHisRec.Source_App__c = 'CRM';
			TrackHisRec.Recipient_App__c = 'CRM';
			TrackHisRec.Status__c = 'Success';
			TrackHisRec.Case__c = caseRecord2.ID;
			TrackHisRec.Source_Track_Id__c = '123456789';
			lstTRRTracks.add(TrackHisRec);
			
			Enrollment_Track_History__c TrackHisRec1 = new Enrollment_Track_History__c();        
			TrackHisRec1.Milestone_Id__c = '1802';
			TrackHisRec1.Source_App__c = 'CRM';
			TrackHisRec1.Recipient_App__c = 'CRM';
			TrackHisRec1.Status__c = 'Success';
			TrackHisRec1.Case__c = caseRecord2.ID;
			TrackHisRec1.Source_Track_Id__c = '123456780';
			lstTRRTracks.add(TrackHisRec1);
			insert  lstTRRTracks;			
        }
    }


    /*
     * Method Name : testcaseSubtypeOnlyChangeMStone
     * Description : This method is used to test caseSubtypeOnlyChangeMStone
     * Return type : NA
     * Paramater : NA
     */
    testMethod static void testcaseSubtypeOnlyChangeMStone()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();                       
            case caseRecord = [Select id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c  from case where Subtype__c = 'Creditable Coverage' and Case_Owner__c = 'Louisville RSO Calls Support' LIMIT 1];
            System.assertEquals(caseRecord.Case_Owner__c , 'Louisville RSO Calls Support');
            CaseMilestone_LC_HUM caseMilestoneObj = new CaseMilestone_LC_HUM();
            caseMilestoneObj.caseSubtypeOnlyChangeMStone(caseRecord, '');
            caseMilestoneObj.writeMstoneToTrackService(null,null ,caseRecord,'CustomInQ');    			
            Test.stopTest();            
        }
    } 


    /*
     * Method Name : isCreditableCoverageSubtypeElligibleTest
     * Description : This method is used to test isCreditableCoverageSubtypeElligible
     * Return type : NA
     * Paramater : NA
     */ 
   
    testMethod static void testcaseSubtypeAndTASMStone()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];

        system.runAs(testUser) 
        {

            case caseRecord = [Select id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c, type, origin  from case where Subtype__c = 'Creditable Coverage' and Case_Owner__c = 'Louisville RSO Calls Support' LIMIT 1];

            list<MET_Milestone__c> lstMETMilestones = [SELECT ID, Name, Case_Type__c, Case_Subtype__c, Action__c, Source_Name__c, Task__c, Is_Active__c, Is_Milestone__c FROM MET_Milestone__c WHERE Case_Subtype__c = 'Creditable Coverage'];
            System.assertEquals(lstMETMilestones[0].Case_Subtype__c,'Creditable Coverage');
            
            list<Task> lstMETTasks = new list<Task>();
            for(MET_Milestone__c METMilestone : lstMETMilestones)
            {               
                Task taskInst = new Task();            
                taskInst.RecordTypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.METTASKTYPE).getRecordTypeId();
                taskInst.MET_Task__c = METMilestone.Task__c;                
                taskInst.MET_Action__c = METMilestone.Action__c;                
                taskInst.MET_Source__c = METMilestone.Source_Name__c;   
                taskInst.Status = 'Completed';
                taskInst.MET_CancelledByUser__c = testUser.Id;
                taskInst.MET_CancelledBydate__c = System.Now();
                taskInst.WhatId = caseRecord.Id;
                taskInst.Type = GLOBAL_CONSTANT_HUM.METTASKTYPE;
                lstMETTasks.add(taskInst);  
            }            
            list<Database.UpsertResult> saveRes = Database.upsert(lstMETTasks);            

                                  
            Test.startTest(); 
            CaseMilestone_LC_HUM caseMilestoneObj = new CaseMilestone_LC_HUM();
            caseMilestoneObj.writeMstoneToTrackService(saveRes, lstMETTasks,caseRecord,'Creditable Coverage'); 
            caseMilestoneObj.writeMstoneToTrackService(saveRes, lstMETTasks,caseRecord,'sPrevSubType'); 
            Test.stopTest();      
            
        }   
    }

      /*
     * Method Name : testTRRMETTracking
     * Description : This method is used to test testTRRMETTracking
     * Return type : NA
     * Paramater : NA
     */ 
   
    testMethod static void testTRRMETTracking()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        system.runAs(testUser) 
        {
                                   
            case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE Type = 'TRR Inquiry' AND Subtype__c = 'TRR' LIMIT 1];               
            
            list<MET_Milestone__c> lstMETMilestones = [SELECT ID, Name, Case_Type__c, Case_Subtype__c, Action__c, Source_Name__c, Task__c, Is_Active__c, Is_Milestone__c FROM MET_Milestone__c WHERE Case_Subtype__c = 'TRR'];
            System.assertEquals(lstMETMilestones[0].Case_Subtype__c,'TRR');
            
            list<Task> lstMETTasks = new list<Task>();
            for(MET_Milestone__c METMilestone : lstMETMilestones)
            {               
                Task taskInst = new Task();            
                taskInst.RecordTypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.METTASKTYPE).getRecordTypeId();
                taskInst.MET_Task__c = METMilestone.Task__c;                
                taskInst.MET_Action__c = METMilestone.Action__c;                
                taskInst.MET_Source__c = METMilestone.Source_Name__c;   
                taskInst.Status = 'Completed';
                taskInst.MET_CancelledByUser__c = testUser.Id;
                taskInst.MET_CancelledBydate__c = System.Now();
                taskInst.WhatId = caseRecord.Id;
                taskInst.Type = GLOBAL_CONSTANT_HUM.METTASKTYPE;
                lstMETTasks.add(taskInst);  
            }            
            list<Database.UpsertResult> saveRes = Database.upsert(lstMETTasks);            
            Test.startTest();
            CaseMilestone_LC_HUM caseMilestoneObj = new CaseMilestone_LC_HUM();
            caseMilestoneObj.writeMstoneToTrackService(saveRes, lstMETTasks,caseRecord,'');            
            Test.stopTest();      
        }
    }

}