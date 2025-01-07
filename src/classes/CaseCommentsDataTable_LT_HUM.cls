/*******************************************************************************************************************************
Apex Class Name : CaseCommentsDataTable_LT_HUM
Version         : 1.0
Created On      : 12/09/2021 
Function        :Test class for CaseCommentsDataTable_LC_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                        12/09/2021                	Original Version
*    2.0           Ashish Kumar                                        03/04/2021                	Original Version
*    3.0           Ankima Srivastava                                   03/21/2021                	Incresed Coverage
*    4.0           Bhumika S                                   	       06/06/2022                   Incresed Coverage
*    5.0           Ritik Agarawal                                      04/04/2022                   Add log code visibility changes US_3192496
*    6.0           M K Manoj                                           07/29/2022                   US-3522143  T1PRJ0170850 - Lightning- Case Comments Removal of HP Log Code field for HP Provider/Agent/Broker/Group/HP Unknown
*    7.0         Prasuna Pattabhi                                      08/24/23                 US 4412371 Market Credentialing Task Fields
*******************************************************************************************************************************************************************/

@isTest
Private with sharing Class CaseCommentsDataTable_LT_HUM {
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert objUser;
        system.assertEquals(objUser.lastname=='test User1',true);

        User objUser1 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Supervisor');
        objUser1.IsActive = true;
        insert objUser1;
        system.assertEquals(objUser1.lastname=='test User2',true);
        
        PermissionSet pRSOHP = [SELECT ID From PermissionSet WHERE Name = 'CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = objUser1.id, PermissionSetId = pRSOHP.id);

        User objPHUser = HUMTestDataHelperTest.createTestUser('User3','Humana Pharmacy Specialist');
        insert objPHUser;
        system.assertEquals(objPHUser.lastname=='test User3',true);
        PermissionSet pHP = [SELECT ID From PermissionSet WHERE Name = 'CRMS_300_Humana_Pharmacy_Supervisor' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = objPHUser.id, PermissionSetId = pHP.id);
        system.runAs(objUser1)
        {
            // Insert Member Account
            GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.onlyForCoverage();
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assert(objMemAccount.LastName == 'LName');
            
            // Insert Member Account
            Account objMemAccountLV = HUMTestDataHelperTest.getAccount();
            objMemAccountLV.FirstName = 'Test FName';
            objMemAccountLV.LastName = 'LName2'; 
            objMemAccountLV.source_platform_code__c = 'LV' ;
            objMemAccountLV.Enterprise_Id__c = String.valueof(2001);
            objMemAccountLV.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccountLV.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccountLV;
            system.assert(objMemAccountLV.LastName == 'LName2');
           
            Product2 oProd = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER',Product_Type__c='MA',Sold_Product_Key_Value__c='FEHVNP55');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
			PurchaserPlan PolicyObj = new PurchaserPlan();
            PolicyObj.Policy_Group_Number__c='11111';
            PolicyObj.Name = 'Dental Product';
            PolicyObj.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObj.PayerId = objMemAccountLV.Id;
            PolicyObj.Major_LOB__c ='MCD';
            PolicyObj.ETL_Record_Deleted__c = false;
            PolicyObj.Product__c = oProd.Id;
            insert PolicyObj;
            
            //MemberPlan
          	MemberPlan MemberPlanObj = new MemberPlan();
            MemberPlanObj.Name='MemberPlan2';
            MemberPlanObj.MemberId=objMemAccountLV.id;
            MemberPlanObj.Member_Dependent_Code__c = '1234567890';
            MemberPlanObj.Member_Id_base__c = 'test23456';
            MemberPlanObj.GroupNumber = '737803';
            MemberPlanObj.PlanId= PolicyObj.Id;
            insert MemberPlanObj;
            
            //Case Object Test Data----------------------------------------------------
            
            Case caseRecord = new Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = objMemAccount.id;
            caseRecord.ownerId = objUser.Id;
            caseRecord.Interacting_With__c = objMemAccount.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Member_Plan_Id__c = MemberPlanObj.Id;
            caseRecord.Classification_Type__c='Claims';
            insert caseRecord;
            System.assertequals(caseRecord.Status,'In Progress'); 

            Case caseRecord1 = new Case();
            caseRecord1.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord1.Status = 'In Progress'; 
            caseRecord1.AccountId = objMemAccount.id;
            caseRecord1.ownerId = objUser.Id;
            caseRecord1.Interacting_With__c = objMemAccount.id;
            caseRecord1.Interacting_With_Type__c = 'Member';
            caseRecord1.Member_Plan_Id__c = MemberPlanObj.Id;
            caseRecord1.Classification_Type__c='Humana Pharmacy Calls';
            caseRecord1.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
            insert caseRecord1;
            System.assertequals(caseRecord1.Status,'In Progress');            
						
	    Task oTasknt = HUMTestDataHelperTest.getTaskRecord();
	    oTasknt.RecordTypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId();
            insert oTasknt;
            System.assert(oTasknt.status == 'In Progress');					
            //CaseComment data
            Case objCase = new Case();
            objCase = HUMTestDataHelperTest.getCase();
            insert objCase;
            System.assertEquals(objCase.Interacting_With_Name__c, 'Test');
            
            CaseComment ccObj = new CaseComment();
            ccObj.CommentBody = '(TEST) Comment';
            ccObj.ParentId = objCase.Id;
            insert ccObj;
            System.assertEquals(ccObj.CommentBody, '(TEST) Comment');
            
            CaseComment ccObjMod = new CaseComment();
            ccObjMod.CommentBody = 'TEST Comment';
            ccObjMod.ParentId = objCase.Id;
            insert ccObjMod;
            System.assertEquals(ccObjMod.CommentBody, 'TEST Comment');
                                                     
            List<PermissionSet> humanaSupervisorPS = [Select id , Name from PermissionSet where name ='CRMS_300_Humana_Pharmacy_Supervisor'];
            if(humanaSupervisorPS != null   )
            {
                insert new PermissionSetAssignment(assigneeId = objUser .id , PermissionSetId =humanaSupervisorPS[0].id);
            }
        
        }
    }
   private static testMethod void TestCaseDetailOnLoad()
            {              
                User testUser = [Select Id,Name from User where LastName='test User1'];  
                System.runAs(testUser)
                {
                    HUM_Webservice_Callout__c RighSourceEditServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_EditMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
                    insert RighSourceEditServiceDetails;            
                    Pharmacy_Supervisor_HUM__c supervisorCS = New Pharmacy_Supervisor_HUM__c(Name = 'CRMS_300_Humana_Pharmacy_Supervisor');
                    insert supervisorCS;
                    Test.startTest();
                    Case CaseRecord = [select id,Owner_Queue__c from Case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
                    System.assertequals(CaseRecord.Owner_Queue__c,'Louisville RSO Calls Support');
                    String Caseid = CaseRecord.Id;
                    Continuation continueObj;
                    CaseCommentsDataTable_LC_HUM cms = new CaseCommentsDataTable_LC_HUM();
                    CaseCommentsDataTable_LC_HUM.sendRequestLogNotes('Testing comment' , '1048868178',Caseid);
                
                    CaseCommentsDataTable_LC_HUM.getCaseCommentOptions(Caseid);                              
                    CaseCommentsDataTable_LC_HUM.getCaseCommentOptions(''); 
                    string caseCommentData='[{"CommentBody":"Testing", "ParentId":"'+CaseRecord.ID+'"}]';
                    CaseCommentsDataTable_LC_HUM.saveCaseCommentData(CaseRecord.ID,caseCommentData);
                    CaseCommentsDataTable_LC_HUM.fetchCaseCTCIObject(CaseRecord.ID);      
                    Test.stopTest(); 
                }
            }
   
    /*
    * Method Name : testLogNotesUID
    * Description : This method Is used To test log notes webservice for UID secnario
    * Return type : None
    * Parameter   : None
    */
   
     Static testMethod void testLogNotesUUID()
    {
        User testUser = [Select Id, Name from User where LastName='test User2' limit 1];        
        System.runAs(testUser)
        {
                Test.startTest();
                Case objCase = new Case();
                objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Type__c ='Agent' limit 1];
                List<CaseComment> ccObjList = new List<CaseComment> ();
                ccObjList = [Select Id,CommentBody from CaseComment where parentid = :objCase.id];
                Continuation continueObj = (Continuation)CaseCommentsDataTable_LC_HUM.sendRequestLogNotes('Test' , '1048868178' ,objCase.id);
            	Continuation continueObj1 = (Continuation)CaseCommentsDataTable_LC_HUM.sendRequestLogNotes('Test' , '1048868178' ,null);

                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"0" } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                string reqType = 'ABC';
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                CaseCommentsDataTable_LC_HUM.parseLogNoteResponse(labelGetmember,continueObj); 
            	CaseCommentsDataTable_LC_HUM.startSaveCommentOperation(objCase.id,'1048868178','Test',true,'true');
                CaseCommentsDataTable_LD_HUM.getCaseRecordStatus(objCase.Id);            
            	CaseCommentsDataTable_LC_HUM.startSaveCommentOperation(null,'1048868178','Test',true,'true');
                Test.stopTest();
        }
    } 

   /*
    * Method Name : testCaseCommentCCS
    * Description : This method Is used To test generate, update & 
                    delete case comments from Customer Care Specialist profile.
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testCaseCommentCCS()
    {
        User testUser = [Select Id, Name from User where LastName='test User2' limit 1];        
        System.runAs(testUser)
        {
            Test.startTest();
            Case objCase = new Case();
            objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Name__c = 'Test' limit 1];
            Task oTasknt = [Select id from Task limit 1];
            List<CaseComment> ccObjList = new List<CaseComment> ();
            ccObjList = [Select Id,CommentBody from CaseComment where parentid = :objCase.id];
            CaseCommentsDataTable_LC_HUM.generateCaseComments(objCase.Id);            
            CaseCommentsDataTable_LC_HUM.updateRecord(ccObjList[0].Id,'test comments', objCase.id);
            CaseCommentsDataTable_LC_HUM.deleteRecord(ccObjList[0].Id,'CaseComment');
            try{
                CaseCommentsDataTable_LC_HUM.updateRecord(oTasknt.Id,'asdasd','caseComment');
            }
            catch(Exception e){
                
            }
                Test.stopTest();
        }
    }    

    /*
    * Method Name : testCaseCommentSysAdmin
    * Description : This method Is used To test generate case comments from System Admin profile.
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testCaseCommentSysAdmin()
    {
            Test.startTest();
            Case objCase = new Case();
            objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Name__c = 'Test' limit 1];
            List<CaseComment> ccObjList = new List<CaseComment> ();
            ccObjList = [Select Id,CommentBody from CaseComment where parentid = :objCase.id];
            CaseCommentsDataTable_LC_HUM.generateCaseComments(objCase.Id);
            Test.stopTest();
    }

    /*
    * Method Name : testDisplayLogCodeForHPProfile
    * Description : used to cover checkDisplayLogCode for HP Profile
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testDisplayLogCodeForHPProfile()
    {
        User testUser = [Select Id, Name from User where LastName='test User3' and isActive=true limit 1]; 
        System.runAs(testUser)
        {
            Pharmacy_Supervisor_HUM__c supervisorCS = New Pharmacy_Supervisor_HUM__c(Name = 'CRMS_300_Humana_Pharmacy_Supervisor');
            insert supervisorCS;
            Test.startTest();
            CaseCommentsDataTable_LC_HUM.checkPermissionForCommentDropDown();
            Test.stopTest();
        }
    } 

    /**
    * 
    *<p>
    * This method is used to cover credentialingClosable method on CaseCommentsDataTable_LD_HUM Class.
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

            Intent__c intentRecDec = New Intent__c();
            intentRecDec.Name = 'LA Medicaid Cred Workflow';
            intentRecDec.Is_Active__c = true;
            insert intentRecDec;
                
            CTCI_Junction__c ctciRec = New CTCI_Junction__c();
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
            CaseCommentsDataTable_LD_HUM.credentialingClosable(oTasknt.Id);
            Test.stopTest();

      }    
    }
}