/**************************************************************************** 
Apex Class Name  : CaseCommentsDataTable_T_HUM
Version          : 1.0  
Created Date     : September 4 ,2017
Function         : test class for CaseCommentsDataTable_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Sumit Pharande           149239              08/31/2017                   Original Version
* Suraj Patil               200646             03/22/2018                   REQ - 354517 Pharmacy Case comment and non - Pharmacy case comment
* Manikanta				                       08/21/2019              		REQ - 396234 - Ability for CRMS to systematically display the HP case record type and Case Comment section
* Aaron Speakman                               05/21/2021                   Code Coverage Improvements
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
***************************************************************************************************/

@isTest
Private with sharing Class CaseCommentsDataTable_T_HUM {
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
        insert objUser1;
        system.assertEquals(objUser1.lastname=='test User2',true);
        
        PermissionSet pRSOHP = [SELECT ID From PermissionSet WHERE Name = 'CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = objUser1.id, PermissionSetId = pRSOHP.id);

        system.runAs(objUser)
        {
            // Insert Member Account
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
           
            // Non Medical - Dental Policy   
            Policy__c objPolicyLV = HUMTestDataHelperTest.getPolicy();
            objPolicyLV.Name = 'Test Policy';
            objPolicyLV.source_cust_cov_key__c = '12345678911234567891' ;
            objPolicyLV.Group_Name__c = objMemAccountLV.Id;       
            insert objPolicyLV;
            system.assert(objPolicyLV.Name == 'Test Policy');
            
            //Non Medical - Dental Policy Member
            Policy_Member__c objPolMemLV = HUMTestDataHelperTest.getPolicyMember();
            objPolMemLV.Member__c = objMemAccountLV.Id;
            objPolMemLV.Policy__c = objPolicyLV.Id;
            objPolMemLV.Relationship__c = 'self'; 
            objPolMemLV.name = '12591';              
            objPolMemLV.Policy_Member_External_ID__c = '00|ODS|123456785|89|89|02-01-2001';
            insert objPolMemLV;
            system.assert(objPolMemLV.Relationship__c == 'self'); 

            //Case Object Test Data----------------------------------------------------
            
            Case caseRecord = new Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = objMemAccount.id;
            caseRecord.ownerId = objUser.Id;
            caseRecord.Interacting_With__c = objMemAccount.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Policy_Member__c = objPolMemLV.Id;
            insert caseRecord;
            System.assertequals(caseRecord.Status,'In Progress');            
						
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

        system.runAs(objUser1)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName1';
            objMemAccount.LastName = 'LName1';
            objMemAccount.source_platform_code__c = 'EM';
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assert(objMemAccount.LastName == 'LName1');
            
            // Insert Member Account
            Account objMemAccountLV = HUMTestDataHelperTest.getAccount();
            objMemAccountLV.FirstName = 'Test FName2';
            objMemAccountLV.LastName = 'LName3';
            objMemAccountLV.source_platform_code__c = 'LV' ;
            objMemAccountLV.Enterprise_Id__c = String.valueof(2001);
            objMemAccountLV.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccountLV.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccountLV;
            system.assert(objMemAccountLV.LastName == 'LName3');
           
            // Non Medical - Dental Policy
            Policy__c objPolicyLV = HUMTestDataHelperTest.getPolicy();
            objPolicyLV.Name = 'Test Policy1';
            objPolicyLV.source_cust_cov_key__c = '12345678911234567891';
            objPolicyLV.Group_Name__c = objMemAccountLV.Id;
            insert objPolicyLV;
            system.assert(objPolicyLV.Name == 'Test Policy1');
            
            // Non Medical - Dental Policy Member
            Policy_Member__c objPolMemLV = HUMTestDataHelperTest.getPolicyMember();
            objPolMemLV.Member__c = objMemAccountLV.Id;
            objPolMemLV.Policy__c = objPolicyLV.Id;
            objPolMemLV.Relationship__c = 'self';
            objPolMemLV.name = '12591';
            objPolMemLV.Policy_Member_External_ID__c = '00|ODS|123456785|89|89|02-01-2002';
            insert objPolMemLV;
            system.assert(objPolMemLV.Relationship__c == 'self');
            
            Case caseRecord = new Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'Pending - Response';
            caseRecord.AccountId = objMemAccount.id;
            caseRecord.ownerId = objUser1.Id;
            caseRecord.Interacting_With__c = objMemAccount.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Policy_Member__c = objPolMemLV.Id;
            caseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
            insert caseRecord;
            System.assertequals(caseRecord.Status,'Pending - Response');
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
                    //Test.setCurrentPage(page.CaseDetail_VF_RT_HUM);
                    ApexPages.currentPage().getParameters().put('Id',CaseRecord.Id);
                    CaseCommentsDataTable_C_HUM cms = new CaseCommentsDataTable_C_HUM();
                    CaseCommentsDataTable_C_HUM.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' , 'sRecId' , 'sAccNum','objCase');
                    cms.sCaseID = CaseRecord.id;
                    cms.sNewCaseComment = 'test';
                    cms.sCodeHidden = 'close';
                    cms.sNewCaseCommentPharmacy = 'casecomment';
                    cms.sCode = 'close';     
                    cms.sHiddenCaseCommentMemberNotFound = 'ABC';
                    cms.sHiddenCaseCommentFaultError = 'EFG';
                    list<SelectOption> li = cms.getCaseCommentOptions();                              
                    cms.populateHiddentVal();                        
                    cms.createNewCaseCommentPharmacy();
                    CaseCommentsDataTable_C_HUM.getCaseCommentsData(Caseid);
                    CaseCommentsDataTable_C_HUM.getCaseCommentsDTO(Caseid);
                    cms.createNewCaseComment();                     
                    cms.onload(); 
                    Test.stopTest(); 
                }
            }
         
    Static testMethod void testLogNotes()
    {
       User testUser = [Select Id,Name from User where LastName='test User1' limit 1];        
        System.runAs(testUser)
        {            
                Test.startTest();
                Case objCase = new Case();
                objCase = [select id,Owner_Queue__c from Case limit 1];
               Continuation continueObj = (Continuation)CaseCommentsDataTable_C_HUM.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' , 'sRecId' , 'sAccNum',objCase.id);
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"" } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                List<string>labelGetmember = new List<string>();
                labelGetmember.add('XYZ');
                Test.setContinuationResponse('XYZ',httpRes);
                CaseCommentsDataTable_C_HUM.parseLogNoteResponse(labelGetmember,continueObj);            
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
        User testUser = [Select Id, Name from User where LastName='test User1' limit 1];        
        System.runAs(testUser)
        {
                Test.startTest();
                Case objCase = new Case();
                objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Type__c ='Agent' limit 1];
                List<CaseComment> ccObjList = new List<CaseComment> ();
                ccObjList = [Select Id,CommentBody from CaseComment where parentid = :objCase.id];
                Continuation continueObj = (Continuation)CaseCommentsDataTable_C_HUM.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' , 'sRecId' , 'sAccNum',objCase.id);
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"0" } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                string reqType = 'ABC';
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                CaseCommentsDataTable_C_HUM.parseLogNoteResponse(labelGetmember,continueObj);            
                Test.stopTest();
        }
    }    
	
	/*
    * Method Name : testRSOHP
    * Description : This method is used to test RSO humana pharmacy related logic
    * Return type : None
    * Parameter   : None
    */
   
    Static testMethod void testRSOHP()
    {
        User testUser = [Select Id, Name from User where LastName='test User2' limit 1]; 
        System.runAs(testUser)
        {
            HUM_Webservice_Callout__c RighSourceEditServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_EditMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            insert RighSourceEditServiceDetails;            
            Test.startTest();
            Case CaseRecord = [select id,Owner_Queue__c from Case where RecordType.Name = 'HP Member Case' and Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
            System.assertequals(CaseRecord.Owner_Queue__c,'Louisville RSO Calls Support');
            String Caseid = CaseRecord.Id;
            Continuation continueObj;
            ApexPages.currentPage().getParameters().put('Id',CaseRecord.Id);
            CaseCommentsDataTable_C_HUM cms = new CaseCommentsDataTable_C_HUM();
            CaseCommentsDataTable_C_HUM.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' , 'sRecId' , 'sAccNum','objCase');
            cms.sCaseID = CaseRecord.id;
            cms.sNewCaseComment = 'test';
            cms.sCodeHidden = 'close';
            cms.sNewCaseCommentPharmacy = 'casecomment';
            cms.sCode = 'close';     
            cms.sHiddenCaseCommentMemberNotFound = 'ABC';
            cms.sHiddenCaseCommentFaultError = 'EFG';
            list<SelectOption> li = cms.getCaseCommentOptions();                              
            cms.populateHiddentVal();                        
            cms.createNewCaseCommentPharmacy();
            CaseCommentsDataTable_C_HUM.getCaseCommentsData(Caseid);
            CaseCommentsDataTable_C_HUM.getCaseCommentsDTO(Caseid);
            cms.createNewCaseComment();                     
            cms.onload();
            system.assert(cms.bIsPharmacyUser!=null);
            Test.stopTest(); 
        }
    }
        
    Static testMethod void testPharmacyCaseComment()
    {
        User testUser = [Select Id, Name from User where LastName='test User2' limit 1]; 
        System.runAs(testUser)
        {
            Test.startTest();
            Case CaseRecord = [select id,Owner_Queue__c, AccountId from Case where Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
            System.assertequals(CaseRecord.Owner_Queue__c,'Louisville RSO Calls Support');
            String Caseid = CaseRecord.Id;
            
            Interaction__c objInteraction = New Interaction__c();
            objInteraction.Interacting_With__c = caseRecord.AccountId;
            objInteraction.Interacting_With_type__c = 'Member';
            objInteraction.Caller_Name__c = 'Test User';
            objInteraction.Interaction_Origin__c = 'Phone';
            insert objInteraction;
            System.assertequals(objInteraction.Interacting_With_type__c,'Member');
            
            ApexPages.currentpage().getParameters().put('caseComment','Test NEW_LINE Test INT_WITH');
            ApexPages.currentpage().getParameters().put('interactionId', objInteraction.Id);
            ApexPages.currentPage().getParameters().put('Id',CaseRecord.Id);
            System.debug('Interaction ID: ' + objInteraction.Id);
            
            CaseCommentsDataTable_C_HUM cms = new CaseCommentsDataTable_C_HUM();
            cms.sCaseID = CaseRecord.id;
            cms.onload(); 

            ApexPages.currentpage().getParameters().put('caseComment','INT_WITH Test');
            ApexPages.currentpage().getParameters().put('interactionId', CaseRecord.AccountId);
            cms.onload(); 

            String commentOver1900Char = 'abcdefghijklmnopqrstuvwxyz';
            while(commentOver1900Char.length() < 1900){
                commentOver1900Char += commentOver1900Char;
            }

            ApexPages.currentpage().getParameters().put('caseComment', commentOver1900Char);
            cms.onload(); 
            Test.stopTest(); 
        }
    }
	
	Static testMethod void testRSOHPCaseComment()
    {
        User testUser = [Select Id, Name from User where LastName='test User2' limit 1]; 
        System.runAs(testUser)
        {
            HUM_Webservice_Callout__c RighSourceEditServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_EditMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            insert RighSourceEditServiceDetails;            
            Test.startTest();
            CaseComment_DTO_HUM sCDTO = New CaseComment_DTO_HUM();
            sCDTO.CreatedByQueue = '';
            sCDTO.LastModifiedByQueue = '';
            sCDTO.LogCode = '';
            sCDTO.Comment = '';
            Test.stopTest(); 
        }
    }
      
}