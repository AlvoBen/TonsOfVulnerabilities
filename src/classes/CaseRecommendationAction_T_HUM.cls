/************************************************************************************************************************************************
Apex Class Name  : CaseRecommendationAction_T_HUM 
Version          : 1.0
Created Date     : July 1 2020
Function         : Test Class - coverage of classes,used for Case Recommendation for OGO Cases.
****************************************************************************************************************************************************************************/
@isTest
private class CaseRecommendationAction_T_HUM {
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        Group oCompassNewGroup = new Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oCompassNewGroup;
        system.assertEquals(oCompassNewGroup.Type,'Queue');
        
        QueueSObject oCompassNewQueue = new QueueSObject(QueueId = oCompassNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
        insert oCompassNewQueue;
        system.assertEquals(oCompassNewQueue.SObjectType,'Work_Queue_Setup__c');
        
        User usr = HUMTestDataHelperTest.createTestUser('ScrubberUser','Customer Care Specialist');
        usr.Current_Queue__c = [Select Id, Name, Current_Queue__c from User where Id =: UserInfo.getUserId()].Current_Queue__c;
        insert usr;
        system.debug('Vmnusr'+usr);
        
        PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.OGO_SCRUBBER_PERMISSION_SET LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = usr.id, PermissionSetId = pCheck.id);
        //RecordType
        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        
        User thisUser=[select Id from User where id=:UserInfo.getUserId()];
        System.RunAs(usr) {
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = usr.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            system.debug('VmnaccountRec'+accountRec);
            
            Product2 prod = HUMTestDataHelperTest.getProduct();
            prod.Name = 'MED';
            prod.Product_Type__c = 'PDP';            
            insert prod;  
            
            Policy__c policyRec = New Policy__c();
            policyRec.Group_Name__c = accountRec.Id;
            policyRec.Product__c = prod.Id;
            policyRec.Policy_Group_Number__c='11111';
            policyRec.Major_Lob__c = 'MPD';
            insert policyRec;
            system.assertEquals(policyRec.Group_Name__c==accountRec.Id,true);
            
            Policy_Member__c policyMemRec = New Policy_Member__c();
            policyMemRec.Name = '12345';
            policyMemRec.Policy__c = policyRec.Id;
            policyMemRec.Member__c = accountRec.Id; 
            insert policyMemRec;
            system.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
            
            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'calls (RSO)';
            ctRec.Is_Active__c=true;
            insert ctRec;

            Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = usr.Current_Queue__c, OwnerId = oCompassNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing',Classification_Type__c=ctRec.id);
            insert oWorkQueue;
            system.assertEquals(oWorkQueue.Service_Center__c,'Business');

            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Demographics';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Change';
            intentRec.Is_Active__c = true;
            insert intentRec;
            
            CTCI_Junction__c ctciRec = New CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            insert ctciRec;
            
            Case caseRecord = New Case();
            caseRecord.AccountId = accountRec.id;
            caseRecord.Owner_Queue__c = 'Louisville RSO Oral Grievance Oversight';
            caseRecord.Origin = 'Inbound Call';
            caseRecord.Status = 'In Progress';            
            caseRecord.ownerId = usr.Id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Interacting_With__c = accountRec.id;
            caseRecord.Classification_Type__c = 'calls (RSO)';
            caseRecord.Classification_id__c=classificationRec.id;
            caseRecord.Intent_Text__c='change';                      
            caseRecord.Priority='Normal';
            caseRecord.Complaint__c='Yes - Medicare Part D';
            caseRecord.Complaint_Reason__c='IPA';
            caseRecord.Complaint_Type__c='Medical';
            caseRecord.Topic__c='Internal Only - Organization Determination and Grievance';
            caseRecord.Work_Queue_View_Name__c= 'Organization Determination and Grievance';
            caseRecord.Case_Owner__c = usr.Name;
            caseRecord.Previous_Owner_Queue__c = 'Louisville RSO Enrollment'; 
            insert caseRecord;
            
            Work_Queue_Setup__c oWorkQueue2 = new Work_Queue_Setup__c(Name = caseRecord.Previous_Owner_Queue__c, OwnerId = oCompassNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing',Classification_Type__c=ctRec.id);
            insert oWorkQueue2;

            RecommendedQueueView__c csRQuVDetail = new RecommendedQueueView__c();
            csRQuVDetail.name = 'Org Determination and Griev';
            csRQuVDetail.OGO_QueueView_Name__c = 'Organization Determination and Grievance';
            csRQuVDetail.Set_Topic_for_Accepted__c = 'Internal Only - CIT';
            csRQuVDetail.Set_Topic_for_Rejected__c = 'Oral Grievance';
            csRQuVDetail.Clone_Case__c=true;
            insert csRQuVDetail;

            
            Interaction__c oInter = new Interaction__c();
            oInter.Interacting_With__c=accountRec.id;  
            oInter.Interacting_With_type__c = 'Member';
            oInter.Interaction_Origin__c= 'Inbound Call';
            insert oInter; 
          
            
            List<Case_Interaction__c> lstCaseInter =new List<Case_Interaction__c>();
            Case_Interaction__c oCaseInter = new Case_Interaction__c();
            oCaseInter.Case__c=caseRecord.id;            
            oCaseInter.Interaction__c=oInter.id;
            lstCaseInter.add(oCaseInter);
            insert lstCaseInter;
            system.assertNotEquals(null, lstCaseInter);
  

            Classification_Type__c cT =[ Select Id from Classification_Type__c where name = 'calls (RSO)'];
            List<Work_Queue_SetUp__c> wQSetUp = new List<Work_Queue_SetUp__c>();
            Work_Queue_SetUp__c wQSetUpRecord = new Work_Queue_SetUp__c();
            wQSetUpRecord.Name = 'Louisville RSO Oral Grievance Oversight';
            wQSetUpRecord.Service_Center__c = 'Louisville';
            wQSetUpRecord.Department__c = 'Retail Service Operations (RSO) Oral Grievance Oversight Team';
            wQSetUpRecord.Classification_Type__c = ctRec.Id;
            wQSetUpRecord.IsActive__c = true;
            wQSetUp.add(wQSetUpRecord);
            insert wQSetUp;

            Work_Queue_Priority_CriteriaLimit_Value__c cv = new Work_Queue_Priority_CriteriaLimit_Value__c();
            cv.Name='Criteria';
            cv.Date__c= System.today();
            cv.Value__c = 25;
            Insert cv;

            insert new HUM_Webservice_Callout__c(Name='CaseTransferService',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://test.humana.com/test', Timeout__c='120000', Request_Type__c='POST', Content_Type__c='application/json', Certificate_Name__c='Test_Certificate');     
        }
    }
    
    /**
    * Returns void
    * test method for caseRecommendationAccept method of CaseRecommendationAction_C_HUM, CaseRecommendationAction_S_HUM, CaseRecommendationAction_D_HUM class 
    * Accept Scenario
    * @return  Void
    * @see
    */

    Static testMethod void caseRecommendationAccept()
    {
        
        Test.StartTest();
   
        Case oTestCase = [Select Id, Interaction_ID__c,Interacting_With__c, CaseNumber, Complaint__c, Complaint_Reason__c, Complaint_Type__c, Topic__c, Recordtype.Name, Work_Queue_View_Name__c, Owner_Queue__c, 
                                OwnerId, Status, Policy_Member__c, Created_By_Queue__c, Previous_Owner_Queue__c, CreatedBy.Current_Queue__c From Case where Owner_Queue__c='Louisville RSO Oral Grievance Oversight' LIMIT 1];
        System.assertNotEquals(null, oTestCase);
        ApexPages.currentPage().getParameters().put('id', oTestCase.id);
        CaseRecommendationAction_C_HUM oTest=new CaseRecommendationAction_C_HUM();
        oTest.sPopupMsg ='Case has not been routed. Please take appropriate action on the case.';
        oTest.bShowPopUp =false;
        oTest.oCaseRec = new Case();
        oTest.oNewTask = new Task();
        oTest.oCaseRec = CaseRecommendationAction_S_HUM.getCaseDetails(oTestCase.Id);
        oTest.oNewTask = CaseRecommendationAction_S_HUM.populateTaskDetails(oTest.oCaseRec);
        RecommendedQueueView__c csRQVDetails = [select OGO_QueueView_Name__c,Set_Topic_for_Accepted__c, Set_Topic_for_Rejected__c from RecommendedQueueView__c where OGO_QueueView_Name__c='Organization Determination and Grievance' LIMIT 1  ];
        system.assertEquals(csRQVDetails.OGO_QueueView_Name__c ,'Organization Determination and Grievance');
        oTest.oNewTask.Type = 'Feedback Accepted';                     
        oTest.createRecommendation(); 

        Test.StopTest();
    } 
    
    /**
    * Returns void
    * test method for caseRecommendationReject method of CaseRecommendationAction_C_HUM, CaseRecommendationAction_S_HUM, CaseRecommendationAction_D_HUM class 
    * Reject Scenario
    * @return  Void
    * @see
    */
    
    Static testMethod void caseRecommendationReject()
    {
        Test.StartTest();
    
        Case oTestCase = [Select Id, Interaction_ID__c,Interacting_With__c, CaseNumber, Complaint__c, Complaint_Reason__c, Complaint_Type__c, Topic__c, Recordtype.Name, Work_Queue_View_Name__c, Owner_Queue__c, 
                                OwnerId, Status, Policy_Member__c, Created_By_Queue__c, Previous_Owner_Queue__c, CreatedBy.Current_Queue__c From Case where Owner_Queue__c='Louisville RSO Oral Grievance Oversight' LIMIT 1];
        System.assertNotEquals(null, oTestCase);
        ApexPages.currentPage().getParameters().put('id', oTestCase.id);
        CaseRecommendationAction_C_HUM oTest=new CaseRecommendationAction_C_HUM();
        oTest.sPopupMsg ='Case has not been routed. Please take appropriate action on the case.';
        oTest.bShowPopUp =false;
        oTest.oCaseRec = new Case();
        oTest.oNewTask = new Task();
        oTest.oCaseRec = CaseRecommendationAction_S_HUM.getCaseDetails(oTestCase.Id);
        oTest.oNewTask = CaseRecommendationAction_S_HUM.populateTaskDetails(oTest.oCaseRec);
        CaseRecommendationAction_S_HUM.assginCaseBackToUser(oTest.oCaseRec);
        RecommendedQueueView__c csRQVDetails = [select OGO_QueueView_Name__c,Set_Topic_for_Accepted__c, Set_Topic_for_Rejected__c from RecommendedQueueView__c where OGO_QueueView_Name__c='Organization Determination and Grievance' LIMIT 1  ];
        system.assertEquals(csRQVDetails.OGO_QueueView_Name__c ,'Organization Determination and Grievance');

        oTest.oNewTask.Type = 'Feedback Rejected';         
        oTest.createRecommendation(); 
        Test.StopTest();
    } 
    
    /**
    * Returns void
    * test method for caseRecommendationDeferred method of CaseRecommendationAction_C_HUM, CaseRecommendationAction_S_HUM, CaseRecommendationAction_D_HUM class 
    * Deferred Scenario
    * @return  Void
    * @see
    */
    
    Static testMethod void caseRecommendationDeferred()
    {
        Test.StartTest();
     
        Case oTestCase = [Select Id, Interaction_ID__c,Interacting_With__c, CaseNumber, Complaint__c, Complaint_Reason__c, Complaint_Type__c, Topic__c, Recordtype.Name, Work_Queue_View_Name__c, Owner_Queue__c, 
                                OwnerId, Status, Policy_Member__c, Created_By_Queue__c, Previous_Owner_Queue__c, CreatedBy.Current_Queue__c From Case where Owner_Queue__c='Louisville RSO Oral Grievance Oversight' LIMIT 1];
        System.assertNotEquals(null, oTestCase);
        ApexPages.currentPage().getParameters().put('id', oTestCase.id);
        CaseRecommendationAction_C_HUM oTest=new CaseRecommendationAction_C_HUM();
        oTest.sPopupMsg ='Case has not been routed. Please take appropriate action on the case.';
        oTest.bShowPopUp =false;
        oTest.oCaseRec = new Case();
        oTest.oNewTask = new Task();
        oTest.oCaseRec = CaseRecommendationAction_S_HUM.getCaseDetails(oTestCase.Id);
        oTest.oNewTask = CaseRecommendationAction_S_HUM.populateTaskDetails(oTest.oCaseRec);
        RecommendedQueueView__c csRQVDetails = [select OGO_QueueView_Name__c,Set_Topic_for_Accepted__c, Set_Topic_for_Rejected__c from RecommendedQueueView__c where OGO_QueueView_Name__c='Organization Determination and Grievance' LIMIT 1  ];
        system.assertEquals(csRQVDetails.OGO_QueueView_Name__c ,'Organization Determination and Grievance');

        oTest.oNewTask.Type = 'Feedback Deferred';         
        oTest.createRecommendation(); 
        Test.StopTest();
    } 
    
    /**
    * Returns void
    * test method for errorScenario method of CaseRecommendationAction_C_HUM, CaseRecommendationAction_S_HUM, CaseRecommendationAction_D_HUM class 
    * Insert/Update Scenario.
    * @return  Void
    * @see
    */

    Static testMethod void errorScenario()
    {
       Test.StartTest();
            Case oTestCase = [Select Id, Interaction_ID__c,Interacting_With__c, CaseNumber, Complaint__c, Complaint_Reason__c, Complaint_Type__c, Topic__c, Recordtype.Name, Work_Queue_View_Name__c, Owner_Queue__c, 
                                OwnerId, Status, Policy_Member__c, Created_By_Queue__c, Previous_Owner_Queue__c, CreatedBy.Current_Queue__c From Case where Owner_Queue__c='Louisville RSO Oral Grievance Oversight' LIMIT 1];
            System.assertNotEquals(null, oTestCase);
            ApexPages.currentPage().getParameters().put('id', oTestCase.id);
            
            List<Case_Interaction__c> lstCloneCaseInteraction = new List<Case_Interaction__c>();
            Case_Interaction__c cloneCaseInteraction = [SELECT ID, case__c, Interaction__c FROM Case_Interaction__c Limit 1];
            cloneCaseInteraction.ID= null;
            lstCloneCaseInteraction.add(cloneCaseInteraction );
            Case_Junction__c oCaseJunRec = new Case_Junction__c();
            oCaseJunRec.Case_Master__c = null;
            oCaseJunRec.Case__c = null;

            CaseRecommendationAction_C_HUM oTest=new CaseRecommendationAction_C_HUM();
            oTest.oCaseRec = new Case();
            oTest.oNewTask = new Task();    
            oTest.oCaseRec = CaseRecommendationAction_S_HUM.getCaseDetails(oTestCase.Id);
            oTest.oNewTask = CaseRecommendationAction_S_HUM.populateTaskDetails(oTest.oCaseRec);
            oTest.oNewTask.Description = 'My feedback comments';
            String saveTaskResultSuccess=CaseRecommendationAction_S_HUM.insertNewTask(oTest.oNewTask);
            oTest.oNewTask.OwnerID = oTestCase.Id;
            String saveTaskResultFailure=CaseRecommendationAction_S_HUM.insertNewTask(oTest.oNewTask);
            oTest.oCaseRec.id=null;
            String saveCaseResult=CaseRecommendationAction_S_HUM.updateCaseFields(oTest.oCaseRec);
            Case cloneCase = CaseRecommendationAction_D_HUM.saveClonedCase(oTest.oCaseRec);
            String saveIntractionResult = CaseRecommendationAction_D_HUM.saveCloneInteraction(lstCloneCaseInteraction);
            String saveLinkResult = CaseRecommendationAction_D_HUM.saveCaseLinkRecords(oCaseJunRec);
            Test.StopTest();
        
    } 

}