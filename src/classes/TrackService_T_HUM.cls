/*********************************************************************************************
Apex Class Name  : TrackService_T_HUM
Version          : 1.0  
Created Date     : July 25 2017  
Description      : This class is used as test class for TrackService_Q_HUM, TrackService_H_HUM & TrackService_S_HUM
*********************************************************************************************
Modification Log:

* Developer Name          Code Review     Date            Description
*------------------------------------------------------------------------------------------------------------
* Lalit Jain	          114278        07/27/2017         Original Version Req # 308348
* Pradeepkumar Dani		  131258			  09/21/2017			Multiple task milestone enhancement
* Sumit Kumar             206605             03/28/2018       Creditable Coverage
* Manoj Chaurasia         202594             03/29/2018       REQ - 354470 Ability to display TAS combination in CRM
* Suresh Gottipati        240518          5/31/2018        REQ - 366200 - Added Member_Id.Member_Id_Value field in code which is replacement of Name Field
* Bharath Rajappan                           05/31/2018       REQ - 361863 - send follow up due date to track grace period 
* Bharath Rajappan							 09/28/2018		  Increased the Coverage for Milestone multiple en-queue error
* Mohammed Noor		      352321  			 04/19/2019		  Changes to support TRR Process BAM tracking.  
* Santhi Mandava                             04/15/2019       Replaced "Member_Id_Value__c" with Name
* Mohammed Noor                              12/04/2019       Changes to support ZAPI service callout changes.
* Mohammed Noor                              12/16/2019       Changes done for DEAA Auto Closure code coverage
* Pooja Kumbhar								 04/29/2020		  Checkmarx Test method without assert error fix.
* Prasuna Pattabhi                           07/31/2023                 Test Class error fix - build fix
***************************************************************************************************/
@isTest
Private Class TrackService_T_HUM
{
    /*
    * Method Name : testSetup 
    * Description : Test method To setup common test data For all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    Static void testSetup()
    {
    	insert New HUM_Webservice_Callout__c(Name='TrackService_WriteTrack', Certificate_Name__c='Humana_GuidanceCare_MOCK', SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/TrackService_V1.0/WriteTrack', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='application/json',Methods_Available__c='A;B;C',Enable_Service_Tester__c=true);                    
		
        User oUser = HUMTestDataHelperTest.createTestUser('mUser1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname,'test mUser1');
		
        system.runAs(oUser) 
		{
            String sAccRecordTypeName = 'Group';      
            Id accRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(sAccRecordTypeName).getRecordTypeId();            
            Account acc1 = HUMTestDataHelperTest.getAccount();
            acc1.Name = 'Test Account Cases1';
            acc1.recordTypeId = accRecordTypeId;            
            acc1.Birthdate__c='01/15/1951';
            acc1.Enterprise_ID__c = '1005447107';
            insert acc1;        
            system.assertEquals(acc1.Name == 'Test Account Cases1',true);
        
            Policy__c oPolicyIns = HUMTestDataHelperTest.getPolicy();
            oPolicyIns.Group_Name__c = acc1.Id;
            oPolicyIns.Name = 'Test Policy';
            insert oPolicyIns;
            system.assertEquals(oPolicyIns.Name,'Test Policy');
            system.assertEquals(oPolicyIns.Group_Name__c == acc1.Id,true);
        
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name='Policy Member PM1';
            oPolicyMem.Policy__c=oPolicyIns.Id;
            oPolicyMem.Member__c=acc1.Id;
            insert oPolicyMem;
            system.assertEquals(oPolicyMem.Id != Null,true);
            system.assertEquals(oPolicyMem.Name,'Policy Member PM1');
            system.assertEquals(oPolicyMem.Policy__c,oPolicyIns.Id);  

            Member_ID__c oMemID = HUMTestDataHelperTest.getmemberID();
            oMemID.Policy_Member__c = oPolicyMem.id;
            oMemID.Policy__c = oPolicyIns.ID;
            oMemID.Type__c = 'SSN';
            oMemID.Name = '123456789';
            oMemID.Member_Card_External_ID__c = '123456';
            insert oMemID;
            
            List<Classification_Type__c> ctList= New list<Classification_Type__c>();
            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'Claims (RSO)';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);
            
            insert ctList;
            system.assertEquals(ctList.size(),1);
            
            Work_Queue_Setup__c wqsRecord = New Work_Queue_Setup__c();
            wqsRecord.Name = 'Cincinnati CRU';
            wqsRecord.Classification_Type__c = ctRec.Id;
            insert wqsRecord;
            system.assertEquals(wqsRecord.Name, 'Cincinnati CRU');
            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);    
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
                
            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);                
            insert lstIntent;
            system.assertEquals(lstIntent[0].Name=='Filling',true);
            
            List<CTCI_Junction__c> ctciList = New list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = New CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            insert ctciList;
            system.assertEquals(ctciList.size(),1);

            String sCaseRecordTypeName = 'Medicare Case';      
            Id caseRecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(sCaseRecordTypeName).getRecordTypeId();            
            Case oCase = New Case(AccountId = acc1.Id,
                                    recordTypeId = caseRecordTypeId,
                                    Policy_Member__c = oPolicyMem.id,
                                    CTCI_List__c = ctciRec.Id,
                                    General_Case__c = True,
                                    Interacting_With_Type__c = 'Group',
                                    Interacting_With__c = acc1.Id,
                                    Due_Date__c = Date.Today(),
                                    Case_Owner__c = UserInfo.getUserName(),
                                    Owner_Queue__c ='Cincinnati CRU1', 
                                    Work_Queue_View_Name__c ='Cincinati CRU1',
                                    Service_Center__c ='Cincinnati',
                                    Department__c ='CRU1', 
                                    Type = 'Customer Inquiry',
                                    status ='In Progress',
                                    Subtype__c ='Routine',
                                    Interacting_About_Type__c = 'Group');
            insert oCase;
            system.assertNotEquals(oCase.Id,null,'Value not matched');
            
			Template_Submission_Owner__c submissionObj = new Template_Submission_Owner__c();
            submissionObj.Object_Owner_ID__c = oCase.Id;       
            submissionObj.Object_Owner_Type__c = 'Case';
            insert submissionObj;
            
            Template__c templateObj = new Template__c(Unique_Template_Name__c='ATVF007',Description__c='Creditable Coverage Attestation');
            Insert templateObj;

            Template_Field__c templateFieldObj = new Template_Field__c();
            templateFieldObj.Template__c=templateObj.Id;
            templateFieldObj.Data_Type__c='String';
            templateFieldObj.Name = 'ATVF_IsCICMSCalled';            
            insert templateFieldObj;
            
            Template_Submission__c templateSubmissionObj = new Template_Submission__c();
            templateSubmissionObj.Submission_Owner__c = submissionObj.id;
            templateSubmissionObj.Template__c = templateObj.id;
            insert templateSubmissionObj;

            Template_Submission_Data__c submissionDataObj = new Template_Submission_Data__c();            
            submissionDataObj.Template_Field__c = templateFieldObj.id;
            submissionDataObj.Template_Submission__c = templateSubmissionObj .id;
            submissionDataObj.Value__c = 'NO';
            insert submissionDataObj;
         
            MET_Task__c oMETTask = New MET_Task__c();
            oMETTask.Name = 'Test Task';
            oMETTask.Is_Active__c = true;
            oMETTask.Case_Subtype__c = 'Routine';
            insert oMETTask;
            System.assertEquals(oMETTask.Name,'Test Task');
            
            MET_Action__c oAction = New MET_Action__c();
            oAction.Name = 'Test Action';
            oAction.Is_Active__c = true;
            insert oAction;
            System.assertEquals(oAction.Name,'Test Action');

            MET_Source__c oSource = New MET_Source__c();
            oSource.Name = 'Test Source';
            oSource.Is_Active__c = true; 
            insert oSource; 
            System.assertEquals(oSource.Name,'Test Source');

            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Type = 'Work Task';
            oTask.Status ='Closed';
            oTask.MET_Task__c = oMETTask.ID;
            oTask.MET_Action__c= oAction.ID;
            oTask.MET_Source__c = oSource.ID;
            insert oTask;	
			System.assertEquals(oTask.Status, 'Closed');
            
            MET_Milestone__c oMETMStone = New MET_Milestone__c(); //REPLACE THIS With THE Get METHOD Of THE HUMTestDataHelperTest Class.
			oMETMStone.Name = '700';
            oMETMStone.Task__c = oMETTask.id;
            oMETMStone.Action__c = oAction.id;
            oMETMStone.Source_Name__c = oSource.id;
            oMETMStone.Case_Type__c = oCase.Type;
            oMETMStone.Case_Subtype__c = oCase.Subtype__c;
			oMETMStone.Is_Milestone__c = true;
            insert oMETMStone;
			System.assertEquals(oMETMStone.Case_Type__c, oCase.Type);
			System.assertEquals(oMETMStone.Name, '700');
			
			Enrollment_Track_History__c TrackHisRec = new Enrollment_Track_History__c();
			TrackHisRec.Milestone_Id__c = '1802';
			TrackHisRec.Source_App__c = 'CRM';
			TrackHisRec.Recipient_App__c = 'CRM';
			TrackHisRec.Status__c = 'Pending';
			TrackHisRec.Case__c = oCase.ID;
			TrackHisRec.Source_Track_Id__c = '1234567890';
			insert TrackHisRec;
			
			//Create Member Account Record
            Account accRec1 = HUMTestDataHelperTest.getAccount();            
            accRec1.FirstName = 'Test DEAA';
            accRec1.LastName = 'Account';
            accRec1.Enterprise_Id__c = '123456';
            accRec1.recordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            insert accRec1;            
			
			//Create DEAA Case
			Case oDEAACase = New Case(AccountId = accRec1.Id,
                                    recordTypeId = caseRecordTypeId,
                                    Policy_Member__c = oPolicyMem.id,
                                    CTCI_List__c = ctciRec.Id,
                                    General_Case__c = True,
                                    Interacting_With_Type__c = 'Member',
                                    Interacting_About_Type__c = 'Member',
                                    Interacting_With__c = accRec1.Id,
                                    Due_Date__c = Date.Today(),
                                    Case_Owner__c = UserInfo.getUserName(),
                                    Owner_Queue__c ='Cincinnati CRU1', 
                                    Work_Queue_View_Name__c ='Cincinati CRU1',
                                    Service_Center__c ='Cincinnati',
                                    Department__c ='CRU1', 
                                    Type = 'Customer Inquiry',
                                    status ='In Progress',
                                    Subtype__c ='Dual Eligible Auto Assign',
                                    Origin = 'DEAA',
                                    Medicare_Track_ID__c = '2864231945');
            insert oDEAACase;
            system.assertNotEquals(oDEAACase.Id,null,'Value not matched');
		}
    }
    
    /*
    * Method Name : testSingleTrackServiceCalloutSuccess
    * Description : Test method For calling the enque method TrackService_Q_HUM Class. This method will pass Task ID To execute method instead Of Set.
					It will also cover the other supporting methods In the TrackService_H_HUM Class And TrackService_S_HUM Class
    * Return type : void     
    * Paramater   : NA  
    */       
    Static testMethod void testSingleTrackServiceCalloutSuccess()
    {                 
        Test.setMock(HttpCalloutMock.class, New TrackServiceMockResponse_T_HUM());       
        User oUser = [select Id, Name from User where name = 'test mUser1' limit 1];  
		system.assertEquals(oUser.Name,'test mUser1');
        system.runAs(oUser) 
        {
			Task oTaskRec = [Select ID from Task Limit 1];
			Case oCaseRec = [Select ID,subType__c,Guided_Process_Flag__c,Medicare_Track_ID__c from Case Limit 1]; 
            oCaseRec.subType__c = GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE;
            oCaseRec.Guided_Process_Flag__c = TRUE;
            update oCaseRec;         
            test.startTest();
			System.enqueueJob(New TrackService_Q_HUM(oTaskRec.ID,oCaseRec.ID));            
			test.stopTest();
			
        }
    }
	
    /*
    * Method Name : testTrackServiceCalloutFailure
    * Description : Test method For testing Exception handling For the Track Service 
    * Return type : void     
    * Paramater   : NA   
    */       
    Static testMethod void testTrackServiceCalloutFailure()
    {                 
        Test.setMock(HttpCalloutMock.class, New TrackServiceMockErrorResponse_T_HUM());       
        User oUser = [select Id, Name from User where name = 'test mUser1' limit 1];                
        system.runAs(oUser) 
        {
			Task oTaskRec = [Select ID from Task Limit 1];
			Case oCaseRec = [Select ID, Medicare_Track_ID__c from Case Limit 1];                        
            test.startTest();
			System.enqueueJob(New TrackService_Q_HUM(oTaskRec.ID,oCaseRec.ID));   
			test.stopTest();
			
      TrackService_Q_HUM oTrack1 = New TrackService_Q_HUM(New list < Id > (), '');
      TrackService_Q_HUM oTrack2 = New TrackService_Q_HUM(New map<Id, set<MET_Milestone__c>>());

			Case oCaseRec1 = [Select ID, Medicare_Track_ID__c from Case Limit 1];
			Enrollment_Track_History__c oTrackHistory = New Enrollment_Track_History__c();
			oTrackHistory = [select ID, Case__c, Recipient_App__c, Recipient_Track_ID__c, Source_App__c, Source_Track_ID__c, Member_id__r.Name, Status__c, Milestone_ID__c, Milestone_MET_ID__c from  Enrollment_Track_History__c limit 1];
			system.assertEquals(null, oCaseRec1.Medicare_Track_ID__c);			
			
			
        }
    }
	
    /*
    * Method Name : testGetMileStoneTSinEST
    * Description : Test method For testing getMileStoneTSinEST method. 
    * Return type : void     
    * Paramater   : NA  
    */       
    Static testMethod void testGetMileStoneTSinEST()
    {                 
        User oUser = [select Id, Name from User where name = 'test mUser1' limit 1];  
        system.runAs(oUser) 
        {
			Case oCaseRec = [Select ID, SubType__c,CreatedDate from Case Limit 1];                        			
            insert New MEDENROLLMENT_CASE_SUBTYPE_HUM__c(Name = 'None', SubType__c = 'Routine', TrackSevice_MilestoneTS__c = true);
			test.startTest();
			TrackService_H_HUM oHelpClass = New TrackService_H_HUM('',null);
			Set<String> subTypeSet = oHelpClass.getMileStoneTSSubTypes();
			String sTimeStamp = oHelpClass.getMileStoneTSinEST(oCaseRec, subTypeSet);
			test.stopTest();
            system.assert(sTimeStamp!= null); 
        }
    }	
	private static testmethod void writeMstoneToTrackServiceForCrediatbleCoverageTest()
    {    
        Test.setMock(HttpCalloutMock.class, new TrackServiceMockResponse_T_HUM());       
        User oUser = [select Id,Name from User where name = 'test mUser1' limit 1];     
		System.assertEquals(oUser.Name, 'test mUser1');
        system.runAs(oUser) 
        {
            test.startTest();
            Task objTaskIds=[Select ID from Task Limit 1];
            List<Id> lstTaskIds=new List<Id>();
            lstTaskIds.add(objTaskIds.Id);
            
                
            MET_Milestone__c oMETMStone = [select Id,Task__r.Name,Task__c,Action__c,Action__r.Name,Source_Name__r.Name,Source_Name__c, Name from MET_Milestone__c Limit 1];
            Set<MET_Milestone__c> oMETMStoneSet = new Set<MET_Milestone__c>();
            oMETMStoneSet.add(oMETMStone);
            Map<Id, set<MET_Milestone__c>> obj=new Map<Id, set<MET_Milestone__c>>();
            obj.put(oMETMStone.Id,oMETMStoneSet);
            TrackService_H_HUM oHelpClass = new TrackService_H_HUM(lstTaskIds,obj);
            Case oCaseRec = [Select ID,Classification_Type__c,Account.FirstName,Account.LastName,CaseNumber,Type,Status,Subtype__c,Medicare_Track_ID__c,Policy_Member__r.Name,Policy_Member__c,CreatedDate from Case limit 1];
            
             // Inserting data into Employment Track History
            Enrollment_Track_History__c oTrackSerHis=new Enrollment_Track_History__c();
            oTrackSerHis.Case__c =oCaseRec.id;
            oTrackSerHis.Milestone_ID__c = '700';            
            oTrackSerHis.Milestone_MET_ID__c = oMETMStone.Id;
            oTrackSerHis.Source_track_ID__c = '2323232';
            oTrackSerHis.Source_App__c = 'CRM';
            oTrackSerHis.Recipient_App__c = '435345';
            oTrackSerHis.Member_ID__c = [Select Id from Member_ID__c where Name = '123456789' Limit 1].Id;
            oTrackSerHis.Status__c = 'Success';
            oTrackSerHis.Recipient_Track_ID__c = '10392266761';
            insert oTrackSerHis;  
                   
            Member_ID__c oMemID=[Select Id,Policy_Member__c,Type__c,Name,Member_Card_External_ID__c from Member_ID__c Limit 1];

            Set<String> subTypeSet=new Set<String>();
            subTypeSet.add('Test1');
            Enrollment_Track_History__c oTrackHistory = [select ID, Case__c, Recipient_App__c, Recipient_Track_ID__c, Source_App__c, Source_Track_ID__c, Member_id__r.Name, Status__c, Milestone_ID__c, Milestone_MET_ID__c from  Enrollment_Track_History__c limit 1];          
            oHelpClass.writeMstoneToTrackService(oCaseRec,oMETMStone,oMemID,subTypeSet, oTrackHistory, new Set<String>(), new set<String>());
            test.stopTest();



        }
    }

   /*
    * Method Name : createWriteTrackRequestTestGracePeriod
    * Description : Test method For testing createWriteTrackRequest when GracePeriod is passed. 
    * Return type : void     
    * Paramater   : NA  
    */       
    Static testMethod void createWriteTrackRequestTestGracePeriod()
    {
        Test.startTest();
        TrackServiceWriteTrackRequest_DTO_HUM reqObj = TrackService_S_HUM.createWriteTrackRequest('', '', '','', '','','',GLOBAL_CONSTANT_HUM.CASE_SUBTYPE_INCOMPLETE,'','','','','' ,'' ,'','',String.valueOF(Date.Today()));
        Test.stopTest();
        TrackServiceWriteTrackRequest_DTO_HUM oRequest;
        oRequest = new TrackServiceWriteTrackRequest_DTO_HUM();
        
        System.assertEquals(reqObj.WriteTrackRequest.TrackInfo.GracePeriodDate,String.valueOF(Date.Today())); 
    }
	
	/*
    * Method Name : testTRRProcessTracking
    * Description : Test method For testing BAM tracking changes for TRR Process. 
    * Return type : void     
    * Paramater   : NA  
    */       
    Static testMethod void testTRRProcessTracking()
    {
 		test.setMock(HttpCalloutMock.class, New TrackServiceMockResponse_T_HUM());       
        User oUser = [select Id, Name from User where name = 'test mUser1' limit 1];
		system.assertEquals(oUser.Name, 'test mUser1');
        map<string, map<string, set<Enrollment_Track_History__c>>> mapCaseEnrollHistory = new map<string, map<string, set<Enrollment_Track_History__c>>>();                
        system.runAs(oUser) 
        {
            Case oCaseRec = [Select ID,subType__c,Guided_Process_Flag__c,Medicare_Track_ID__c from Case Limit 1]; 
 			list<Enrollment_Track_History__c> lstTrackHist = [SELECT ID, Milestone_Id__c, Source_App__c, Recipient_App__c, Status__c, Case__c, Source_Track_Id__c FROM Enrollment_Track_History__c 
 																WHERE Case__c = :oCaseRec.Id AND Status__c='Pending'];			           
			map<string, set<Enrollment_Track_History__c>> mapHistRec = new map<string, set<Enrollment_Track_History__c>>(); 
			for(Enrollment_Track_History__c trackHist : lstTrackHist)
			{
				mapHistRec.put(trackHist.Source_Track_Id__c, new set<Enrollment_Track_History__c>{trackHist});				
			} 
			mapCaseEnrollHistory.put(oCaseRec.ID, mapHistRec);                    
            test.startTest();
            System.enqueueJob(new TrackService_Q_HUM(mapCaseEnrollHistory));            
            test.stopTest();            
        }   	
    }
	
	/*
    * Method Name : testprocessDEAACasesBulk
    * Description : Test method For testing BAM tracking changes for DEAA cases. 
    * Return type : void     
    * Paramater   : NA  
    */       
    Static testMethod void testprocessDEAACasesBulk()
    {
 		test.setMock(HttpCalloutMock.class, New TrackServiceMockResponse_T_HUM());       
        User oUser = [select Id, Name from User where name = 'test mUser1' limit 1];
        set<Id> setCaseIds = new set<Id>();                
        system.runAs(oUser) 
        {
            Case oCaseRec = [Select ID from Case WHERE Origin = 'DEAA' limit 1]; 
            system.assertNotEquals(oCaseRec, null); 			 
			setCaseIds.add(oCaseRec.Id);                    
            test.startTest();
            System.enqueueJob(new TrackService_Q_HUM(setCaseIds));            
            test.stopTest();            
        }   	
    }

}