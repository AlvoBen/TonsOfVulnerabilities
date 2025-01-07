/*******************************************************************************************************************************
Apex Class Name : CaseMilestoneTrackService_T_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : Test Class contains method for services to post Milestone information.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Pradeepkumar Dani          114278                   07/10/2017                 Original Version: REQ - 315927
* Pradeepkumar Dani                                   08/11/2017                 Updated for Correspondence related changes.
* Shruthi Karanth            122673                   08/25/2017                 Updated test user last name to fix test class failure
* Syed Mubarak               221369                   04/27/2018                 Modified for REQ - 360058
* Mohammed Noor              232736                   05/22/2018                 Increase Test coverage for REQ - 362097
* Mohammed Noor              238897                   06/04/2018                 Changes done for Defect# 360701.0001
* Vamshi Krishna Pemberthi                            07/08/2021                 Test Class error fix and code coverage improvements.
* Prasuna Pattabhi                           07/31/2023                 Test Class error fix - build fix.
********************************************************************************************************************************/
@isTest
private class CaseMilestoneTrackService_T_HUM 
{
    /**
    * test set-up method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void prepareTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('UserMETTrack','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        oUser.Network_User_Id__c = 'XYZ0000';
        insert oUser;        
        system.assert(oUser.LanguageLocaleKey == 'en_US');
        
        list<Case_Milestone__c> lstCustomSettings = new list<Case_Milestone__c>();
        lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-001', Doc_Type__c = 't1', Sub_Type__c = 'Test Sub-Type', Milestone__c = '909', Origin__c = 'Origin', Is_Start_Milestone__c = false));
        lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-002', Doc_Type__c = 't2', Sub_Type__c = 'Test Sub-Type', Milestone__c = '900', Origin__c = 'Inbound Call', Is_Start_Milestone__c = true));
        lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-003', Doc_Type__c = 't3', Sub_Type__c = 'Stop Sub-Type', Milestone__c = '800', Origin__c = 'Origin1', Is_Start_Milestone__c = true, Activity_Category__c = '1'));
        lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-004', Doc_Type__c = 't4', Sub_Type__c = 'Stop Sub-Type2', Milestone__c = '801', Origin__c = 'Inbound Call', Is_Start_Milestone__c = true, Activity_Category__c = '2'));
        lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-005', Doc_Type__c = 't5', Sub_Type__c = 'Stop Sub-Type3', Milestone__c = '802', Origin__c = 'Origin3', Is_Start_Milestone__c = true));
        lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-006', Doc_Type__c = 't6', Sub_Type__c = 'Test Cont-Type', Milestone__c = '909', Origin__c = 'Inbound Call', Is_Start_Milestone__c = false));
        lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-007', Doc_Type__c = 't7', Sub_Type__c = 'Test Start-Type', Milestone__c = '909', Origin__c = 'Inbound Call', Is_Start_Milestone__c = true));
        insert lstCustomSettings;
        
        insert new HUM_Webservice_Callout__c(Name='TrackService_WriteTrack', Certificate_Name__c='Humana_GuidanceCare_MOCK', Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/TrackService_V1.0/WriteTrack', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='application/json');
        
        system.runAs(oUser)
        {
            Work_Queue_Setup__c oQSetup = new Work_Queue_Setup__c(Name='Cincinnati CRU');
            insert oQSetup ;
            system.assertNotEquals(null, oQSetup.id);
           
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
            system.assertEquals(oMemID.Id != Null, true);
            system.assertEquals(oMemID.Type__c, 'SSN');
        }       
    }
    
    /**
    * testMethodForPositive
    *<p>
    *
    * @param            NULL
    * @return           void
    */
    static testMethod void testInvocableMethod() 
    {
        User oUser = [Select id,Current_Queue__c from user where LastName ='test UserMETTrack' limit 1 ];
        Account acc = [SELECT Id FROM Account LIMIT 1];
        Policy_Member__c oPolicyMem = [SELECT Id FROM Policy_Member__c LIMIT 1];
        Id medicareCaseRTId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Medicare Case').getRecordTypeId();
        Test.setMock(HttpCalloutMock.class, new TrackServiceMockResponse_T_HUM());       
        system.runAs (oUser) 
        {
            //Create List of Cases
            integer loopCount = 20;
            list<Case> lstCases = new List<Case>();
            for(Integer i=0; i<loopCount; i++)
            {
                Case cs = HUMTestDataHelperTest.getCase();
                cs.Subject = 'Case Subject'+ i;
                cs.AccountId = acc.Id;
                cs.OwnerId = oUser.Id;
                cs.RecordTypeId = medicareCaseRTId;
                cs.SubType__c = 'Test Sub-Type';
                cs.Doc_Type__c = 't4';
                cs.Origin = 'Origin';
                cs.Policy_Member__c = oPolicyMem.id;
                cs.Interacting_About_Type__c = 'Group';
                lstCases.add(cs);
            } 
            
            for(Integer i=0; i<loopCount; i++)
            {
                Case cs = HUMTestDataHelperTest.getCase();
                cs.Subject = 'Case Subject'+ i;
                cs.AccountId = acc.Id;
                cs.OwnerId = oUser.Id;
                cs.RecordTypeId = medicareCaseRTId;
                cs.SubType__c = 'Test Sub-Type - Update';
                cs.Doc_Type__c = 't3';
                cs.Origin = 'Origin';
                cs.Policy_Member__c = oPolicyMem.id;
                cs.Interacting_About_Type__c = 'Group';
                lstCases.add(cs);
            }     
            
                Case csStop = HUMTestDataHelperTest.getCase();
                csStop.Subject = 'Case Subject Stop';
                csStop.AccountId = acc.Id;
                csStop.OwnerId = oUser.Id;
                csStop.RecordTypeId = medicareCaseRTId;
                csStop.SubType__c = 'Stop Sub-Type';
                csStop.Doc_Type__c = 't3';
                csStop.Origin = 'Origin';
                csStop.Policy_Member__c = oPolicyMem.id;
                csStop.Interacting_About_Type__c = 'Group';
                lstCases.add(csStop);

                Case csStart = HUMTestDataHelperTest.getCase();
                csStart.Subject = 'Case Subject Start';
                csStart.AccountId = acc.Id;
                csStart.OwnerId = oUser.Id;
                csStart.RecordTypeId = medicareCaseRTId;
                csStart.SubType__c = 'Test Start-Type';
                csStart.Doc_Type__c = 't7';
                csStart.Origin = 'Inbound Call';
                csStart.Policy_Member__c = oPolicyMem.id;
                csStart.Interacting_About_Type__c = 'Group';
                lstCases.add(csStart);
                 
            
            Test.StartTest();
            
            //insert list of Cases
            insert lstCases;
            system.assertEquals(lstCases.size() == (loopCount * 2)+2 , true);
            
            list<Case> lstCaseUpdate = [SELECT Id, SubType__c, Doc_Type__c FROM Case WHERE SubType__c = 'Test Sub-Type - Update'];
            list<Id> lstCaseIds = new list<Id>();
            for(Case cs: lstCaseUpdate)
            {
                cs.SubType__c = 'Test Sub-Type';
                cs.Doc_Type__c = 't5';
                lstCaseIds.add(cs.Id);
            }
            
            update lstCaseUpdate;
            
            
            //Start/Stop Miestone
            Case milestoneCase = [SELECT Id,SubType__c,Doc_Type__c, Origin from Case where Subtype__c = 'Stop Sub-Type'];
            milestoneCase.Subtype__c = 'Stop Sub-Type2';
            update milestoneCase;
            milestoneCase.Subtype__c = 'Stop Sub-Type';
            update milestoneCase;
            
            List<Enrollment_Track_History__c> lstHistory = new List<Enrollment_Track_History__c>();
            Enrollment_Track_History__c track1 = new Enrollment_Track_History__c();
            track1.case__c = milestoneCase.Id;
            track1.Milestone_Id__c = '800';
            track1.status__c = 'Success';
            lstHistory.add(track1);
            insert lstHistory;
            
            List<Enrollment_Track_History__c> lstHistory1 = [SELECT Id, Name, Case__c,case__r.casenumber, Source_App__c, Recipient_App__c, Source_Track_Id__c, Recipient_Track_ID__c, Member_ID__c, Status__c, Milestone_Id__c, Milestone_MET_Id__c, createddate 
                                                           FROM Enrollment_Track_History__c where case__c = :milestoneCase.Id order by createddate desc];
                                   
            CaseMilestoneTrackService_C_HUM.CaseMilestoneParams caseParams = new CaseMilestoneTrackService_C_HUM.CaseMilestoneParams();
            caseparams.CaseId = milestoneCase.Id;
            caseparams.previousCaseSubType = 'Stop Sub-Type2';
            caseparams.isNewCase = false;
            
            List<CaseMilestoneTrackService_C_HUM.CaseMilestoneParams> caseParamsList = new List<CaseMilestoneTrackService_C_HUM.CaseMilestoneParams>();
            caseParamsList.add(caseparams);

            //Queable apex invoking scenario when process builder is inactive
            CaseMilestoneTrackService_C_HUM.invokeTrackService(caseParamsList);
            
            //Test Continuity Milestone update Process builder flow            
            milestoneCase.Subtype__c = 'Test Cont-Type';
            update milestoneCase;
            
            
            caseParams = new CaseMilestoneTrackService_C_HUM.CaseMilestoneParams();
            caseparams.CaseId = milestoneCase.Id;
            caseparams.previousCaseSubType = 'Stop Sub-Type2';
            caseparams.isNewCase = false;
            
            caseParamsList = new List<CaseMilestoneTrackService_C_HUM.CaseMilestoneParams>();
            caseParamsList.add(caseparams);
            
            CaseMilestoneTrackService_C_HUM.invokeTrackService(caseParamsList);

            Case milestoneCase1 = [SELECT Id,SubType__c,Doc_Type__c, Origin from Case where Subtype__c = 'Test Start-Type'];

            caseParams = new CaseMilestoneTrackService_C_HUM.CaseMilestoneParams();
            caseparams.CaseId = milestoneCase1.Id;
            caseparams.previousCaseSubType = 'Test Sub-Type';
            caseparams.isNewCase = true;
            
            caseParamsList = new List<CaseMilestoneTrackService_C_HUM.CaseMilestoneParams>();
            caseParamsList.add(caseparams);

            CaseMilestoneTrackService_C_HUM.invokeTrackService(caseParamsList);
            
            
            //Test getUICaseMilestone method for new Medicare case
            List<Case> lstCaseRec = [Select Id, origin, subtype__c from Case where subtype__c = 'Test Sub-Type' and Origin = 'Origin' limit 1];
            set<MET_Milestone__c> milestoneResult = CaseMilestoneTrackService_C_HUM.getUICaseMilestone(lstCaseRec[0], '', new set<MET_Milestone__c>());            
            System.assertNotEquals(milestoneResult.isEmpty(),true);
            
            //Test getUICaseMilestone method for subtype change
            lstCaseRec = [Select Id, origin, subtype__c from Case where subtype__c = 'Test Sub-Type' and Origin = 'Origin' limit 1];
            case caseRec = lstCaseRec[0];
            caseRec.Subtype__c = 'Test Cont-Type';
                        
            Enrollment_Track_History__c hist1 = new Enrollment_Track_History__c();
            hist1.status__c = 'Success';
            hist1.Case__c = caseRec.Id;
            hist1.Milestone_Id__c = '809';
            insert hist1;
            
            MET_Milestone__c prevMS = new MET_Milestone__c(Name='800');
            set<MET_Milestone__c> milestoneResult1 = CaseMilestoneTrackService_C_HUM.getUICaseMilestone(caseRec,'Test Sub-Type', new set<MET_Milestone__c>{prevMS});            
            System.assertNotEquals(milestoneResult1.isEmpty(),true);
            
            caseRec.Subtype__c = 'Stop Sub-Type2';
            set<MET_Milestone__c> milestoneResult2 = CaseMilestoneTrackService_C_HUM.getUICaseMilestone(caseRec,'Test Sub-Type', new set<MET_Milestone__c>{prevMS});            
            System.assertNotEquals(milestoneResult2.isEmpty(),true);
            
            Test.StopTest();
            

            //Error Scenario
            CaseMilestoneTrackService_C_HUM.invokeTrackService(null);
            system.assertEquals([SELECT Id FROM Error_Log__c].size() != 0, true);
        }        
    }
}