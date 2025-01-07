/****************************************************************************
Apex Class Name :   HUMChangeCaseOwnerControllerTest  
Version: 1.0 
Created On       : 7/29/2014
Function         : Test class to HUMChangeCaseOwnerController apex class

    Modification Log
* Developer Name              Code Review                        Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Mrudula Jaddu               17093                             07/29/2014                   Original Version
* Sagar Tapadia               17559                             08/14/2014                  Add Test Method for QMS Service
* SuryaKumari Medicherla      18230                             09/29/2014                  Add Test Method for Defect: 156158
* Mrudula Jaddu               19591                             12/01/2014                  code change as per CC#19591
* SuryaKumari Medicherla      19911                             12/10/2014                  Code modified to replace custom settings with custom object (REQ-72809)
* SuryaKumari Medicherla      20226                             01/07/2015                  Code modified to fix defect: 167546
* SuryaKumari Medicherla      20574                             01/28/2015                  Code modified to fix defect: 168836
* Manuel Johnson              20710                             02/03/2015                  Code modified to fix test class failure
* SuryaKumari Medicherla      20634                             02/10/2015                  Code modified to increase code coverage as per comments given in CC#20634
* Santhi Mandava              21128                             02/24/2015                  Fixed test class failure issue related to req-81852.
* SuryaKumari Medicherla      21223                             02/26/2015                  Code modified to fix Issue number 146 And business priority 0(Not able to get users part of queues even after selecting service center And department)
* Rakesh Patni                22986                             05/08/2015                  Added code to fix text class failure by creating group at line# 163- 173
* Praveen Kumar Parimi                                          06/22/2015                  Added mock Dpaas service callout for QMS data.
* Praveen Kumar Parimi/Surya  26451                             09/14/2015                  Added testSetup method And modified code to increase code coverage
* Vishal Verma                30508                             03/15/2016                  Defect 216758.0001 : Passing the Comment entered from UI while transferring the Case to QMS .
* Shikhar Mehta                                                 06/21/2016                  REQ - 237000 : EIP fix, removed dpass calls for decryption.
* Prachi Bhasin               33337                             07/18/2016                  Changes made as part of REQ- 270349
* Vamsi Kondragunta           40364                             09/20/2016                  Added code coverage for changes worked on REQ: 95982 
* Shikhar Mehta                                                 09/22/2016                  Req - 202270
* Harika Devi Kalidindi      42104                              10/04/2016                  REQ - 283105 : showing only Active users names on case/task transfer in a drop-down
* Shikhar Mehta                                                 10/26/2016                  REQ - 290098 MF3 RollBack
* Vamsi Kondragunta           45206                             10/27/2016                  REQ - 290098 MF3 RollBack Build fix 
* Vamsi Kondragunta           46929                             11/08/2016                  Defect fix for #247086.0001
* Vamsi Kondragunta           47326                             11/09/2016                  Defect fix for #247086.0001.
* Praveen Kumar Parimi        50918                             12/06/2016                  Changes made as part of CA 6412051 aka REQ 290998.
* Harshith Mandya                                               02/21/2017                  REQ - 301510 : CA Ticket - Fixing the OWner Queue, Service Center And Department Mismatch ( Increase Code Coverage ).
* Praveen Kumar Parimi        78292                             03/22/2017                  Test custom setting for REQ - 304963 to encrypt QMS Case Comments.
* Ranjeeth Nagishetty                                           03/30/2017                  Changed to data model of CTCI
* Sumit Sharma                89120                             05/03/2017                  Added New method to test REQ - 307653
* Pradeepkumar Dani           99570                             06/08/2017                  Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Pradeepkumar Dani          100996                             06/13/2017                  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
* Harika Devi Kalidindi      112000                             07/20/2017                  REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
* Pallavi R                  110756                             07/21/2017                  Updated for REQ - 322573
* Vishal Verma               114200                             07/27/2017                  REQ - 323542 : Exception handling 
* Sharan Shanmugam                                              08/09/2017                  REQ - 324814 : Save Case Comment to CRM during ePost failure
* Shreya Choodamani                                             10/18/2017                  REQ#:332390 : Updated the test class to increase the coverage. 
* Dinesh Subramaniyan                                           11/29/2017                  REQ - 350875 - SF - TECH - Convert QMSReferralService to Async  
* Asish Behera                                                  03/08/2019                  REQ - 386743 - MIR - Create and update SLA values in Work Queue Setup record (RF)  
* Manikanta                                                     08/21/2019                  REQ - 396234 - Ability for CRMS to systematically display the HP case record type and Case Comment section
* Sunil Kumar Reddy Y                                           06/24/2020                  User Story 1200920: PR00091574 - MF 1 - AHT - Ability to auto-populate Service Center and Department on Case Transfer page (RF)
* Sunil Kumar Reddy Y                                           08/13/2020                  Fixed test class failure
* Suresh Kokkeragadda                                           07/29/2020                  User Story 1317726: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 7/30 , 8/14 and 8/28 releases (CRM)
* Samantha Tennyson                                             11/20/2020                  User Story 1641595: PR00091574 - MF 1 - Quality - Ability for Rep to select "Was the Grievance Resolved during the call?" on Quick Start/Case Page (RF)

* Kavya Bondada                                                 01/20/2021                  User Story 1745816: PR00091574 - MF 1 - Quality - Display guidance on Case Transfer page on selection of Topic (CRM)
* Moshitha Gunasekaran                                          02/08/2021                  User Story 1083065: PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case
* Atia Uzma                                                     02/19/2021                  User Story 1956960: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 11/20/2020 , 02/12/2021, 03/05/2021 and 3/19/2021 releases (CRM)
* Atia Uzma                                                     03/01/2021                  User Story 2040620: T1PRJ0001827 - MF 1 - Quality - Display guidance on Case Transfer page on selection of Service Center and Department (CRM)
* Vamshi Krishna Pemberthi                                      06/17/2021                  Code Coverage Improvements
* Atia Uzma                                                     06/21/2021            User Story 2348383: T1PRJ0001827 - MF 1 - Case Transfer Assist - Business Configuration enhancements (CRM)
* Prudhvi Pamarthi                                              09/21/2021                  US#2422184 Decommission the CRM to QMS Code
* Jasmeen Shangari                                              11/11/2021                  Fix Code Coverage to 90%
* Jasmeen Shangari								  				13/01/2022					USER STORY 3027976 - Fix Queue View issue for MHK Task Type cases
* Pooja Kumbhar                                                 07/31/2023                  Test class failure fix for release 8_4
**********************************************************************************************************************************/

@isTest
Private Class HUMChangeCaseOwnerControllerTest
{   
    /**
    * Test Setup method to create all the data required for testing.
    *
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
         insert pharmaUser;
        
         PermissionSet pRSOHP = [SELECT ID From PermissionSet WHERE Name = 'CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1];
         insert New PermissionSetAssignment(AssigneeId = oRSOHPUser.id, PermissionSetId = pRSOHP.id);

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
             oAcc.Source_Platform_Code__c = 'CA';
             oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
             insert oAcc;
             System.assertEquals(oAcc.Name,'TestAccount1');

             Classification_Type__c objcT = New Classification_Type__c();
             objcT.Name ='CallRSOtest';
             objcT.Is_Active__c = true;
             insert objcT;
             System.assertEquals(objcT.Name,'CallRSOtest');
            
             Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c(Name = oGrp.Name);
             oWQS.Service_Center__c ='Cincinnati';
             oWQS.Department__c ='CRU1';
             oWQS.CokY__c =true;
             oWQS.HO_Medical__c =true;
             oWQS.TenantID__c ='00'; 
             oWQs.Public_Group_Name__c='Cincinnati_CRU1_PGQ';   
             oWQs.isActive__c = TRUE;               
             oWQS.ownerId = oGrp.Id;   
             oWQS.Classification_Type__c = objcT.id;         
             oWQS.Work_Queue_Guidance__c = 'test';
             
             insert oWQS;
             System.assertEquals(oWQS.TenantID__c ,'00');
             
             
             

             List<Classification__c> lstClassification = New List<Classification__c>();
             Classification__c classificationRec = New Classification__c();
             classificationRec.Name = 'Claims';
             classificationRec.Is_Active__c = true;
             lstClassification.add(classificationRec);          
             insert lstClassification;
             System.assertEquals(lstClassification[0].Name=='Claims',true);

            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            insert lstIntent;
            System.assertEquals(intentRec.Name == 'Filling', true);

            Classification__c classificationRec1 = New Classification__c();
            classificationRec1 .Name = 'Claimstest';
            classificationRec1 .Is_Active__c = true;
            insert classificationRec1 ;
            System.assertEquals(classificationRec1.Name,'Claimstest');
            
            Intent__c intentRec1 = New Intent__c();
            intentRec1.Name = 'Fillingtest';
            intentRec1.Is_Active__c = true;
            insert intentRec1 ;
            System.assertEquals(intentRec1.Name,'Fillingtest');
            
            
            
            CTCI_Junction__c objCTCI = New CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec1.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec1.id;
            objCTCI.IsActive__c=true;
            insert objCTCI ;
            System.assertNotEquals(Null,objCTCI.id);
            
            
            

            Case oCase = New Case(AccountId = oAcc.Id, CoKY_Case__c = True,
                                   Interacting_With_Type__c = 'Group', Interacting_About_Type__c = 'Group',Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                                   Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = oGrp.Name,
                                   Service_Center__c ='Cincinnati', Department__c='CRU1', status='In Progress');
                                   oCase.Complaint__c = 'Yes'                   ;
                                   oCase.Complaint_Reason__c = 'Claims';
                                   oCase.Classification_Type__c = 'Calls';
                                   oCase.CTCI_List__c = objCTCI.id;
                                   oCase.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
                                   oCase.G_A_Rights_Given__c ='No';

            insert oCase; 
            System.assertEquals(oCase.Classification_Type__c ,'Calls');

            List<CaseComment> lstCaseComment = New List<CaseComment>();
            CaseComment caseCommentRec = New CaseComment();        
            caseCommentRec.ParentId = oCase.Id;        
            caseCommentRec.CommentBody = 'TESTING';  
            lstCaseComment.add(caseCommentRec);      
            insert lstCaseComment;        
            System.assertEquals(lstCaseComment.size(),1);




            Group oGrp1 = [Select id, Name, DeveloperName from Group where type = 'Queue' AND DeveloperName = :System.label.HUMQMSMarketHOQueue];
            Work_Queue_Setup__c oWQS1 = New Work_Queue_Setup__c(Name = oGrp1.Name);
            oWQS1.Service_Center__c ='Market';
            oWQS1.Department__c ='Home Office Quality Operations Compliance';
            oWQS1.General__c =false;
            oWQS1.HO_Medical__c =true;
            oWQS1.TenantID__c ='00';       
            insert oWQS1;
            System.assertEquals(oWQS1.TenantID__c ,'00');

            Case oCaseRecord = New Case(AccountId = oAcc.Id, General_Case__c = True, CTCI_List__c = objCTCI.id,
                                   Interacting_With_Type__c = 'Group',Interacting_About_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),OwnerId = oCurrentUser.Id,
                                   Case_Owner__c = oCurrentUser.Name, Owner_Queue__c ='TestAccount1', 
                                    Service_Center__c ='Market', Department__c='Home Office Quality Operations Compliance', status='In Progress');
                                    oCaseRecord.Classification_Type__c = 'Calls';
                                    oCaseRecord.G_A_Rights_Given__c = '';
                                    oCaseRecord.Complaint__c = '';

            insert oCaseRecord;  
            System.assert(oCaseRecord.General_Case__c == true);

            Case_Action__c oCaseAction = HUMTestDataHelperTest.getCaseAction('QAA');
            insert oCaseAction;
            System.assertNotEquals(oCaseAction.id,null,'Value not matched');   

            Case_Action_Association__c oCaseAssociation = HUMTestDataHelperTest.getCaseActionAssociation();
            oCaseAssociation.Case__c = oCaseRecord.Id;
            oCaseAssociation.Case_Action__c = oCaseAction.Id;
            insert oCaseAssociation;
            System.assertEquals(oCaseAssociation.Case__c,oCaseRecord.id);  

            Case_Action_Response__c oCaseResp = New Case_Action_Response__c();
            oCaseResp.Action__c = oCaseAssociation.Id;
            oCaseResp.Process_Question__c = System.label.HUMQMSDateIncidence;
            oCaseResp.Process_Question_Response__c=String.valueOf(System.today().month()+'/'+System.today().day()+'/'+System.today().year());
            insert oCaseResp;
            System.assertEquals(oCaseResp.Action__c ,oCaseAssociation.Id); 

            Work_Queue_Topic__c owrkqtopic = New Work_Queue_Topic__c();
            owrkqtopic.Name = 'Work Queue Topic';
            insert owrkqtopic;
            System.assertEquals(owrkqtopic.Name,'Work Queue Topic');


            Work_Queue_Junction__c owrkqjunction = New Work_Queue_Junction__c();
            owrkqjunction.Name = 'Work Queue Junction';
            owrkqjunction.Work_Queue_Setup_Name__c = oWQS.id;
            owrkqjunction.Work_Queue_Topic_Name__c = owrkqtopic.id;
            owrkqjunction.Work_Queue_Topic_Guidance__c ='test';
            insert owrkqjunction;
            System.assertEquals(owrkqjunction.Name,'Work Queue Junction');

            List<HUMApplicationSettings__c> lstApplicationName = New List<HUMApplicationSettings__c>();            
            lstApplicationName.add(New HUMApplicationSettings__c(Name = 'CIPHERCLOUD', Active__c = true));
            insert lstApplicationName;
            System.assertEquals(lstApplicationName.size(), 1);

            List<HUMCipherCloudSettings__c> lstCipherCloudSetting = New List<HUMCipherCloudSettings__c>();
            lstCipherCloudSetting.add(New HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME', Value__c='CRMR1'));
            insert lstCipherCloudSetting;
            System.assertEquals(lstCipherCloudSetting.size(), 1);

            //Create Custom setting
            HUM_Webservice_Callout__c oWSCallout = New HUM_Webservice_Callout__c(Name = 'QMSReferralService' , Certificate_Name__c = 'TestCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
            insert oWSCallout;
            System.assertEquals(oWSCallout.Name,'QMSReferralService','Value not matched');
            
            //create Custom Setting entried for Account And insert custom settings data
             List<HUMEncryptedFields__c>lstCS7 = New List<HUMEncryptedFields__c>();
             lstCS7.add(New HUMEncryptedFields__c(Name='CommentBod',ObjectName__c='CaseComment',FieldName__c='CommentBody',EncryptionScheme__c = 'Stateless_AES_Encryption_without_Search'));     
             insert lstCS7;
             System.assertequals(false,lstCS7.isEmpty());     
             insert New HUMConstants__c [] {New HUMConstants__c( Name = 'HUMCaseComment', StringValue__c = 'CaseComment', IntegerValue__c = 0),                                  
             New HUMConstants__c(Name = 'HUMStatelessAESEncryptionwithoutSearch', StringValue__c = 'Stateless_AES_Encryption_without_Search', IntegerValue__c = 0),     
             New HUMConstants__c(Name = 'ZERO', StringValue__c = '0', IntegerValue__c = 0),
             New HUMConstants__c(Name = 'HUMExclamation', StringValue__c = '!', IntegerValue__c = 0),
             New HUMConstants__c(Name = 'HUMDoubleStar', StringValue__c = '**', IntegerValue__c = 0),
             New HUMConstants__c(Name = 'AMP', StringValue__c = '&', IntegerValue__c = 0),
             New HUMConstants__c(Name = 'HUMValueOne', StringValue__c = '1', IntegerValue__c = 1),
             New HUMConstants__c(Name = 'HUMOpenBrace', StringValue__c = '(', IntegerValue__c = 0),
             New HUMConstants__c(Name = 'HUMAT', StringValue__c = '@', IntegerValue__c = 0),
             New HUMConstants__c(Name = 'HUMDash', StringValue__c = '-', IntegerValue__c = 0),
             New HUMConstants__c(Name = 'HUMCommentBody', StringValue__c = 'CommentBody', IntegerValue__c = 0),
             New HUMConstants__c(Name = 'HUMRSOMedicare', StringValue__c = 'RSO- Medicare', IntegerValue__c = 0)                  
             };
             
             Pharmacy_Supervisor_HUM__c objPharmacySup = New Pharmacy_Supervisor_HUM__c(Name ='CRMS_300_Humana_Pharmacy_Supervisor');
               insert objPharmacySup;
               System.assertEquals(objPharmacySup.Name,'CRMS_300_Humana_Pharmacy_Supervisor');
             HUM_Webservice_Callout__c RighSourceEditServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_EditMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
             insert RighSourceEditServiceDetails;
            
        Policy__c policyRec = New Policy__c();
        policyRec.Group_Name__c = oAcc.Id;
        policyRec.Policy_Group_Number__c='11111';
        insert policyRec;
        System.assertEquals(policyRec.Group_Name__c==oAcc.Id,true);
        
        Policy_Member__c policyMemRec = New Policy_Member__c();
        policyMemRec.Name = '12345';
        policyMemRec.Policy__c = policyRec.Id;
        policyMemRec.Member__c = oAcc.Id; 
        insert policyMemRec;
        System.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
        
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
        caseRecord.Classification_id__c=lstClassification[0].id;
        caseRecord.Intent_id__c = lstIntent[0].id;
        caseRecord.Interacting_With__c = oAcc.id;
        caseRecord.Interacting_With_Type__c = 'Member';
        caseRecord.Policy_Member__c = policyMemRec.Id;
        caseRecord.Interacting_About_Type__c = 'Group';
        caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
        //caseRecord.Autoroute_Status__c = 'Evaluate';
        insert caseRecord;
        System.assertEquals(caseRecord.Status=='In Progress',true);     
        
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
        
        Product2 prod4 = New Product2();
        prod4.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MAPD;
        prod4.ProductCode = GLOBAL_CONSTANT_HUM.MEDICARE_POLICY;
        prod4.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCR;
        prod4.Name = 'Prod4';
        insert prod4;
        System.assertEquals(prod4.Name=='Prod4',true);  
        
        Policy__c policyRecMed = New Policy__c();
        policyRecMed.Group_Name__c = accountRec.id;
        policyRecMed.Product__c = prod4.id;
        policyRecMed.Major_Lob__c = GLOBAL_CONSTANT_HUM.MEDICARECALLS_PDP_HUM;
        insert policyRecMed;
        System.assertEquals(policyRecMed.Product__c==prod4.id,true);
        
        Policy_Member__c policyMemRecMed = New Policy_Member__c();
        policyMemRecMed.Name = 'MedCallsCC';
        policyMemRecMed.Policy__c = policyRecMed.Id;
        policyMemRecMed.Member__c = accountRec.id;
        insert policyMemRecMed;
        System.assertEquals(policyMemRecMed.Member__c==accountRec.Id,true);
        
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
        caseRecordMed.Policy_Member__c = policyMemRecMed.Id;
        caseRecordMed.Autoroute_Status__c = 'Evaluate';
        caseRecordMed.Origin = 'Inbound Call';
        caseRecordMed.Line_of_Business__c = 'Medicare';
        caseRecordMed.status = 'In Progress';
        caseRecordMed.Interacting_About_Type__c = 'Member';
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
        caseRecordMHK.Policy_Member__c = policyMemRecMed.Id;
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
        caseRecordMHK1.Policy_Member__c = policyMemRecMed.Id;
        caseRecordMHK1.Origin = 'Correspondence';
        caseRecordMHK1.Type = 'MHK Dispute Task';
        caseRecordMHK1.General_Case__c = true;
        caseRecordMHK1.Tenant_Id__c = '00';
        caseRecordMHK1.G_A_Rights_Given__c = '';
        caseRecordMHK1.Complaint__c = '';
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
        caseRecordMed1.Policy_Member__c = policyMemRecMed.Id;
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
        
        Case_Transfer_Assist__c cta = new Case_Transfer_Assist__c();
                        cta.CTCI_List__c = objCTCI.id;
                        cta.Product__c = prod4.id; 
                        cta.Work_Queue_Setup__c = oWQS.id;
                        cta.Is_Active__c = true;
                        insert cta;
             //creating switch
 
         }

        System.runAs(oRSOHPUser)
        {
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount2';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assertEquals(oAcc.Name,'TestAccount2');
            
            Policy__c policyRec = New Policy__c();
            policyRec.Group_Name__c = oAcc.Id;
            policyRec.Policy_Group_Number__c='111111';
            insert policyRec;
            System.assertEquals(policyRec.Group_Name__c==oAcc.Id,true);
            
            Policy_Member__c policyMemRec = New Policy_Member__c();
            policyMemRec.Name = '123456';
            policyMemRec.Policy__c = policyRec.Id;
            policyMemRec.Member__c = oAcc.Id; 
            insert policyMemRec;
            System.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'HP Billing';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);          
            insert lstClassification;
            System.assertEquals(lstClassification[0].Name=='HP Billing',true);

            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Eligibility';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            insert lstIntent;
            System.assertEquals(intentRec.Name == 'Eligibility', true);
             
            Case caseRecord = new Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'Pending - Response';
            caseRecord.AccountId = oAcc.id;
            caseRecord.ownerId = oRSOHPUser.Id;
            caseRecord.Classification_Type__c = 'calls';
            caseRecord.Classification_id__c=lstClassification[0].id;
            caseRecord.Intent_id__c = lstIntent[0].id;
            caseRecord.Interacting_With__c = oAcc.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Policy_Member__c = policyMemRec.Id;
            caseRecord.Interacting_About_Type__c = 'test';
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
            insert caseRecord;
            System.assertequals(caseRecord.Status,'Pending - Response');
            
            Case caseRecord1 = new Case();
            caseRecord1.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord1.Status = 'Pending - Response';
            caseRecord1.AccountId = oAcc.id;
            caseRecord1.ownerId = oRSOHPUser.Id;
            caseRecord1.Classification_Type__c = 'calls';
            caseRecord1.Classification_id__c=lstClassification[0].id;
            caseRecord1.Intent_id__c = lstIntent[0].id;
            caseRecord1.Interacting_With__c = oAcc.id;
            caseRecord1.Interacting_With_Type__c = 'Member';
            caseRecord1.Policy_Member__c = policyMemRec.Id;
            caseRecord1.Interacting_About_Type__c = 'test';
            caseRecord1.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord1.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert caseRecord1;

        

            System.assertequals(caseRecord1.Status,'Pending - Response');
        }

        System.runAs(pharmaUser)
        {
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount2';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assertEquals(oAcc.Name,'TestAccount2');
            
            Policy__c policyRec = New Policy__c();
            policyRec.Group_Name__c = oAcc.Id;
            policyRec.Policy_Group_Number__c='111111';
            insert policyRec;
            System.assertEquals(policyRec.Group_Name__c==oAcc.Id,true);
            
            Policy_Member__c policyMemRec = New Policy_Member__c();
            policyMemRec.Name = '123456';
            policyMemRec.Policy__c = policyRec.Id;
            policyMemRec.Member__c = oAcc.Id; 
            insert policyMemRec;
            System.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'HP';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);          
            insert lstClassification;
            System.assertEquals(lstClassification[0].Name=='HP',true);

            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Eligibility1';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            insert lstIntent;
            System.assertEquals(intentRec.Name == 'Eligibility1', true);
            Group oGrp3 = [Select id, Name, DeveloperName from Group where type = 'Queue' AND DeveloperName = :'Cincinnati_CRU_PGQ'];
            Work_Queue_Setup__c oWQS9 = New Work_Queue_Setup__c(Name = oGrp3.Name);
            oWQS9.Service_Center__c ='Market';
            oWQS9.Department__c ='Home Office Quality Operations Compliance';
            oWQS9.General__c =false;
            oWQS9.HO_Medical__c =true;
            oWQS9.TenantID__c ='00';  
            oWQS9.Public_Group_Name__c = 'Cincinnati CRU PGQ';     
            insert oWQS9;
             
            Case caseRecord = new Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'Pending - Response';
            caseRecord.AccountId = oAcc.id;
            caseRecord.ownerId = pharmaUser.Id;
            caseRecord.Classification_Type__c = 'calls';
            caseRecord.Classification_id__c=lstClassification[0].id;
            caseRecord.Intent_id__c = lstIntent[0].id;
            caseRecord.Interacting_With__c = oAcc.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Policy_Member__c = policyMemRec.Id;
            caseRecord.Interacting_About_Type__c = 'test';
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
            insert caseRecord;
            System.assertequals(caseRecord.Status,'Pending - Response');        
            
 
            
                Case oCase12 = New Case(AccountId = oAcc.Id, 
                        CoKY_Case__c = True,
                       Interacting_With_Type__c = 'Group', 
                       Interacting_With__c=oAcc.Id,
                       Due_Date__c=Date.Today(),
                       Case_Owner__c = UserInfo.getUserName(), 
                       ownerId = pharmaUser.Id,
                       Owner_Queue__c = oGrp.Name,
                       Service_Center__c ='Cincinnati', 
                       Department__c='CRU1', 
                       status='In Progress State',                                 
                       Complaint__c = '',
                       recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId(),
                       G_A_Rights_Given__c ='');
                       insert oCase12;

            System.assertequals(oCase12.Status,'In Progress State');              

        }

   }
   
    /**
    * Test Non-Setup method to create all the data required for testing.
    *
    * @param     NULL
    * @return    void 
    */
    @testSetup static void setup1() 
    {
        HUMTriggerSwitch__c temp = new HUMTriggerSwitch__c(); 
        temp.Name = 'Work Queue Setup'; 
        temp.Exeute_Trigger__C = true; 
        insert temp;
        System.assertNotEquals(temp.Id, null);
        System.assertEquals(temp.Exeute_Trigger__C, true);
    }
   
   Private Static testMethod void nonHomeTestMethod()
    {
        User oCurrentUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test ChangeCaseOwnerUser1' LIMIT 1];
        User oNonHomeUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test ChangeCaseOwnerUser2' LIMIT 1];
        System.runAs(oCurrentUser)
        {
            insert new CRMFunctionality_ONOFF_Switch__c(Name = '2578074',IsON__c=true,Release_Name__c='03/19/2021',Scrum_Team__c='Transformers',Story_Title__c='PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case');     
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE Name='TestAccount1' LIMIT 1 ];
          
            Case oCase = [SELECT id, AccountId,Owner_Queue__c,CTCI_List__c, Tenant_Id__c ,User__c, Service_Center__c ,Department__c,General_Case__c,DST_Case__c,HO_Dental_Case__c,CoKY_Case__c
                         ,HO_Medical_Case__c From Case WHERE AccountId=:oAcc.Id And Service_Center__c ='Cincinnati'and Department__c='CRU1' limit 1];
            oCase.Tenant_Id__c = '00';
            oCase.User__c = NULL;
            update oCase;

            ApexPages.currentPage().getParameters().put('id', oCase.id);
            HUMChangeCaseOwnerController objHandler = New HUMChangeCaseOwnerController();   
            test.startTest();  

            objHandler.checkUserProfile();            

            List <SelectOption> lstOption = objHandler.getServiceCenters();
           
            objHandler.sServiceCenter = 'Cincinnati';
            lstOption = objHandler.getDepartments();                             
            objHandler.sDepartment = 'CRU1';
            objHandler.hPharmacyUser = true;
            lstOption = objHandler.getTopics(); 
            objHandler.hPharmacyUser = false;
            lstOption = objHandler.getTopics();                      
            objHandler.sTopic='xxx' ;            
            objHandler.assignFixedValuesToCase();  
            

            objHandler.sTopic='' ;            
            objHandler.assignFixedValuesToCase();
            System.assertEquals(objHandler.sGrpQueueName,'Cincinnati CRU1');                      
         
        }   
    }
    
     
     Private Static testMethod void testTransferAssist()
     {

        User oUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test ChangeCaseOwnerUser1' LIMIT 1];
        System.runAs(oUser)
        {
        Test.startTest();
            Product2 pro = [SELECT Product_Type__c FROM Product2 where name='Prod4'];
            pro.Major_LOB__c = 'PDP';
            update pro;
                                                                
            Classification_Type__c ct = [SELECT id,name from Classification_Type__c where name = 'CallRSOtest' LIMIT 1];
            ct.Name = 'Calls (RSO)';
            update ct;
            System.assertEquals(ct.Name,'Calls (RSO)'); 
            Case oCaseRecord =  [Select id, AccountId,CTCI_List__c ,Classification_Type__c,Policy_Member__c,Policy_Member__r.Product__c,Policy_Member__r.Product_Type__c,CTCI_List__r.Classification__r.name,CTCI_List__r.Intent__r.name,Policy_Member__r.Product_Type_Code__c from Case Where Status='In Progress' AND Owner_Queue__c = 'Louisville RSO Enrollment' LIMIT 1];
            List<Case_Transfer_Assist__c> ctaList = [SELECT Id, Name, CTCI_List__r.Classification_Type__r.Name, CTCI_List__r.Intent__r.Name, CTCI_List__r.Classification__r.Name, Product__c, Product__r.Name, Product__r.Major_LOB__c, Product__r.Product_Type__c, Work_Queue_Setup__r.Name 
                                                                    FROM Case_Transfer_Assist__c];
            update ctaList ;
            oCaseRecord.Classification_Type__c='Calls (RSO)';
            update oCaseRecord;
             System.assertEquals(oCaseRecord.Classification_Type__c,'Calls (RSO)'); 

             ApexPages.currentPage().getParameters().put('id', oCaseRecord.id);
             HUMChangeCaseOwnerController objHandler = new HUMChangeCaseOwnerController();
             Test.stopTest();
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
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE Name='TestAccount1' LIMIT 1 ];
            Work_Queue_Junction__c owrkqjunction = [Select id from Work_Queue_Junction__c LIMIT 1];
            Group oGrp = [SELECT id, Name from Group WHERE Name='Cincinnati_CRU1'];
            QueuesObject oQueueObject = [SELECT id, QueueID, SobjectType  from QueuesObject WHERE QueueID = :oGrp.id limit 1];
            Work_Queue_Setup__c oWQS = [SELECT id, Name, Service_Center__c, Department__c, General__c, TenantID__c, HO_Medical__c from Work_Queue_Setup__c WHERE Name=:oGrp.Name limit 1];
            oCurrentUser.Current_Queue__c = oWQS.Name;
            update oCurrentUser;
            System.assertEquals(oCurrentUser.Current_Queue__c,oWQS.Name);

            Case oCase = [SELECT id, AccountId, Tenant_Id__c , CTCI_List__c ,Service_Center__c ,Department__c From Case WHERE AccountId=:oAcc.Id And Service_Center__c ='Cincinnati'and Department__c='CRU1' limit 1];
            oCase.Tenant_Id__c = '00';
            update oCase;
            System.assertEquals(oCase.Tenant_Id__c,'00');

            ApexPages.currentPage().getParameters().put('id', oCase.id);
            HUMChangeCaseOwnerController objHandler = New HUMChangeCaseOwnerController();   
            test.startTest();  

            objHandler.checkUserProfile();            

            List <SelectOption> lstOption = objHandler.getServiceCenters();
            System.assert(lstOption.size()>0);

            objHandler.sServiceCenter = 'None';
            Pagereference oPageRef = objHandler.saveTaskOrCase();
            System.assertEquals(oPageRef ,NULL); 

            objHandler.sServiceCenter = 'Cincinnati';
            lstOption = objHandler.getDepartments();
            System.assert(lstOption.size()>0);

            objHandler.sDepartment = 'None';
            oPageRef = objHandler.saveTaskOrCase(); 
            System.assertEquals(oPageRef ,NULL);            

            objHandler.sDepartment = 'CRU1';
            lstOption = objHandler.getTopics();            
            System.assert(lstOption.size()>0);

            objHandler.sTopic='xxx' ;            
            oPageRef = objHandler.saveTaskOrCase();
            System.assertEquals(oPageRef ,NULL);  

            objHandler.sTopic='Select a Topic' ;
            oPageRef = objHandler.saveTaskOrCase(); 
            System.assertEquals(oPageRef ,NULL);
           
            oPageRef = objHandler.saveTaskOrCase(); 
            System.assertEquals(oPageRef ,NULL);
            
            objHandler.sCaseComments = 'Test case comments';
            objHandler.sCode = '';
            objHandler.bIsPharmacyUser = true;
            oPageRef = objHandler.saveTaskOrCase(); 
            System.assertEquals(oPageRef ,NULL);

            objHandler.sTopic='xxx' ;            
            objHandler.sOwnerId = oCurrentUser.id;
            objHandler.sOwnerName ='mrudula';
            oPageRef = objHandler.saveTaskOrCase();

            objHandler.sTopic='Select a Topic' ;
            objHandler.sOwnerId = oCurrentUser.id;
            objHandler.sOwnerName ='mrudula';     
            PageReference oActualReference = new Pagereference('/' + oCase.id);       
            oPageRef = objHandler.saveTaskOrCase();

            oPageRef = objHandler.CaseDetailCancel();  
            System.assertEquals(oPageRef.getURL() ,oActualReference.getURL());

            oPageRef = objHandler.Clear();
            PageReference oPageRefForClear = new PageReference(HUMConstants.HUMApexinURL + HUMConstants.HUMChangeCaseOwner);
            oPageRefForClear.getParameters().put(HUMConstants.HUMId, oCase.id);   
            oPageRefForClear.getParameters().put(GLOBAL_CONSTANT_HUM.STRING_CLEAR,GLOBAL_CONSTANT_HUM.STRING_TRUE);  
            System.assertEquals(oPageRef.getURL() ,oPageRefForClear.getURL());

            objHandler.sOwnerId = null;
            objHandler.saveTaskOrCase();
            List<Case> lstObjCase = [select Id from Case where OwnerId =: oCurrentUser.id];            
            System.assertEquals(lstObjCase.size()>0,true);

            test.stopTest(); 
        }
     } 
     Static testMethod void testOverAll1(){
     
        User testUser = [Select Id,Name,IsActive, Current_Queue__C from User where LastName='test ChangeCaseOwnerUser3'];
        User oCurrentUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test ChangeCaseOwnerUser1' LIMIT 1];
        Group oGrp1 = New Group(Name ='Cincinnati CRU1',DeveloperName='Cincinnati_CRU2', Type='QUEUE');
        insert oGrp1;
        QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp1.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;   
        System.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');  
        
        System.runAs(testUser)
        {
            Test.startTest();
            Case CaseRecord = [select id,Owner_Queue__c from Case where RecordType.Name = 'HP Member Case' and Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
            System.assertequals(CaseRecord.Owner_Queue__c,'Louisville RSO Calls Support');
            ApexPages.currentPage().getParameters().put('id', CaseRecord.id);
            HUMChangeCaseOwnerController hcc = new HUMChangeCaseOwnerController();
            system.assert(hcc.bRSOHPFlag!=null);

            Case CaseRecord1 = [select id,Owner_Queue__c from Case where RecordType.Name = 'Member Case' and Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
            System.assertequals(CaseRecord1.Owner_Queue__c,'Louisville RSO Calls Support');
            ApexPages.currentPage().getParameters().put('id', CaseRecord1.id);
            HUMChangeCaseOwnerController hcc1 = new HUMChangeCaseOwnerController();
            hcc1.returnToCaseCreatorQueue();
           // hcc1.validateUserForSelectedServDep(); //Currently not there in Testfull
            hcc1.returnToPriorSenderQueue();
            
      Classification_Type__c ctype= [SELECT id from Classification_Type__c LIMIT 1];
            Work_Queue_Junction__c owrkqjunction = [select Work_Queue_Topic_Name__r.IsActive__c,id, Name, Work_Queue_Setup_Name__r.Name, Work_Queue_Topic_Name__r.Name, Work_Queue_Topic_Guidance__c from Work_Queue_Junction__c where Name='Work Queue Junction' limit 1];
            Work_Queue_Topic__c owrkqtopic = [select id, Name,IsActive__c from Work_Queue_Topic__c where Name= 'Work Queue Topic' limit 1 ];

      Work_Queue_Setup__c oWQS1 = New Work_Queue_Setup__c(Name = oGrp1.Name);
      oWQS1.Service_Center__c ='Cincinnati';
      oWQS1.Department__c ='CRU1';
      oWQS1.CokY__c =true;
      oWQS1.HO_Medical__c =true;
      oWQS1.TenantID__c ='00'; 
      oWQs1.Public_Group_Name__c='Cincinnati_CRU1_PGQ';   
      oWQs1.isActive__c = TRUE;               
      oWQS1.ownerId = oGrp1.Id;   
      oWQS1.Classification_Type__c = ctype.id;         
      insert oWQS1;

            Work_Queue_Junction__c owrkqjunction1 = New Work_Queue_Junction__c();
            owrkqjunction1.Name = 'Work Queue Junction';
            owrkqjunction1.Work_Queue_Setup_Name__c = oWQS1.id;
            owrkqjunction1.Work_Queue_Topic_Name__c = owrkqtopic.id;
            insert owrkqjunction1;
          
            hcc1.sTopic = 'Work Queue Topic';
            hcc1.sSelectedWorkQueue = 'Cincinnati CRU1';
            hcc1.displayTopicGuidance();
            Test.stopTest();
        }
    }
     /**
    * Method to test Warm Case Transfer
    *
    * @param     NULL
    * @return    void 
    */   
    Private Static testMethod void WarmCaseTest()
     {      
        User oCurrentUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test ChangeCaseOwnerUser1' LIMIT 1];
    System.runAs(oCurrentUser)
        {       
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE Name='TestAccount1' LIMIT 1 ];
            Work_Queue_Junction__c owrkqjunction = [Select id from Work_Queue_Junction__c LIMIT 1];
            Group oGrp = [SELECT id, Name from Group WHERE Name='Cincinnati_CRU1'];
            QueuesObject oQueueObject = [SELECT id, QueueID, SobjectType  from QueuesObject WHERE QueueID = :oGrp.id limit 1];
            Work_Queue_Setup__c oWQS = [SELECT id, Name, Service_Center__c, Department__c, General__c, TenantID__c, HO_Medical__c from Work_Queue_Setup__c WHERE Name=:oGrp.Name limit 1];
            oCurrentUser.Current_Queue__c = oWQS.Name;
            update oCurrentUser;
            System.assertEquals(oCurrentUser.Current_Queue__c,oWQS.Name);

            Case oCase = [SELECT id, AccountId, Tenant_Id__c ,User__c, CTCI_List__c ,Service_Center__c ,Department__c From Case WHERE AccountId=:oAcc.Id And Service_Center__c ='Cincinnati'and Department__c='CRU1' limit 1];
            oCase.Tenant_Id__c = '00';
            update oCase;
            System.assertEquals(oCase.Tenant_Id__c,'00');

            ApexPages.currentPage().getParameters().put('id', oCase.id);
            HUMChangeCaseOwnerController objHandler = new HUMChangeCaseOwnerController();   
            objHandler.cid = oCase.Id;
            test.startTest();  

            objHandler.checkUserProfile();            

            List <SelectOption> lstOption = objHandler.getServiceCenters();
            System.assert(lstOption.size()>0);
           
            objHandler.sServiceCenter = 'Cincinnati';
            Pagereference oPageRef = objHandler.saveTaskOrCase();
            objHandler.hPharmacyUser = true;
            lstOption = objHandler.getDepartments();      
            objHandler.hPharmacyUser = false;
            lstOption = objHandler.getDepartments();                         
            objHandler.sDepartment = 'CRU1';
            lstOption = objHandler.getTopics();                       
            objHandler.sTopic='xxx' ;            
            objHandler.oCaseRec.User__c = oCurrentUser.id;        
            oPageRef = objHandler.saveTaskOrCase(); 
            
            objHandler.sCaseComments = 'Test case comments';
            oPageRef = objHandler.saveTaskOrCase(); 
            
            oPageRef = objHandler.Clear();
            PageReference oPageRefForClear = new PageReference(HUMConstants.HUMApexinURL + HUMConstants.HUMChangeCaseOwner);
            oPageRefForClear.getParameters().put(HUMConstants.HUMId, oCase.id);   
            oPageRefForClear.getParameters().put(GLOBAL_CONSTANT_HUM.STRING_CLEAR,GLOBAL_CONSTANT_HUM.STRING_TRUE);  
            System.assertEquals(oPageRef.getURL() ,oPageRefForClear.getURL());
            
            test.stopTest(); 
        }
     } 
    /**
    * Method to test QMS Service
    *
    * @param     NULL
    * @return    void 
    */   
     Private Static testMethod void callQMSService()
     {

        User oUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test ChangeCaseOwnerUser1' LIMIT 1];
        System.runAs(oUser)
        {

            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE Name='TestAccount1' LIMIT 1 ];
            Group oGrp = [Select id, Name, DeveloperName from Group where type = 'Queue' AND DeveloperName = :System.label.HUMQMSMarketHOQueue];
            Work_Queue_Setup__c oWQS1 = [SELECT id, Name, Service_Center__c, OwnerId, Department__c, General__c, TenantID__c, HO_Medical__c, Work_Queue_Name__c from Work_Queue_Setup__c WHERE Name=:oGrp.Name limit 1];
            oWQS1.OwnerId = oGrp.Id;
            update oWQS1;
            System.assertEquals(oWQS1.OwnerId,oGrp.Id);            

            oUser.Current_Queue__c = oWQS1.Id;
            update oUser;
            System.assertEquals(oUser.Current_Queue__c,oWQS1.Id);

            Work_Queue_Topic__c owrkqtopic = [select id, Name from Work_Queue_Topic__c where Name= 'Work Queue Topic' limit 1 ];
            Work_Queue_Junction__c owrkqjunction = [select id, Name, Work_Queue_Setup_Name__r.Name, Work_Queue_Topic_Name__r.Name, Work_Queue_Setup_Name__c, Work_Queue_Topic_Name__c from Work_Queue_Junction__c where Name='Work Queue Junction' limit 1];
            Case oCaseRecord = [Select id, AccountId, Tenant_Id__c, User__c from Case Where AccountId=:oAcc.Id And Service_Center__c ='Market' and Department__c='Home Office Quality Operations Compliance' limit 1];            
            oCaseRecord.Tenant_Id__c = '00';
            oCaseRecord.User__c = NULL;
            update oCaseRecord;
            System.assertEquals(oCaseRecord.Tenant_Id__c,'00');
            System.assertEquals(oCaseRecord.User__c, NULL);
            
            Case_Action__c oCaseAction = [Select id, Name from Case_Action__c where name='QAA' limit 1];
            Case_Action_Association__c oCaseAssociation = [select id, Case__c, Case_Action__c from Case_Action_Association__c where Case__c =:oCaseRecord.Id limit 1];
            Case_Action_Response__c oCaseResp = [select id, Action__c, Process_Question__c, Process_Question_Response__c from Case_Action_Response__c where Action__c=:oCaseAssociation.Id limit 1];

            ApexPages.currentPage().getParameters().put('id', oCaseRecord.id);
            Test.setMock(WebServiceMock.class, new HUMQMSServiceMockTest());
            
            ApexPages.currentPage().getParameters().put('id', oCaseRecord.id);

            HUMChangeCaseOwnerController objHandler = new HUMChangeCaseOwnerController();
            objHandler.checkUserProfile();  
             objHandler.populateQueueName();                         
            objHandler.sServiceCenter = 'Market';
            objHandler.sDepartment = 'Home Office Quality Operations Compliance';
            objHandler.sTopic = 'South Florida';
            objHandler.sCaseComments = 'Screen Message';
            
            objHandler.hPharmacyUser =true;
            List <SelectOption> lstOption1 = objHandler.getServiceCenters();
            
            objHandler.hPharmacyUser =false;
            List <SelectOption> lstOption = objHandler.getServiceCenters();
            System.assert(lstOption.size()>0);

            lstOption = objHandler.getDepartments();
            System.assert(lstOption.size()>0);
            
            objHandler.checkUserProfile(); 
            
            Map<String,Work_Queue_Setup__c> mapServCenDeptWorkQueue_Setup = new Map<String,Work_Queue_Setup__c>();
            mapServCenDeptWorkQueue_Setup.put(oWQS1.Service_Center__c +'#' + oWQS1.Department__c,oWQS1); 
            objHandler.mapServCenDeptWorkQueueSetup = mapServCenDeptWorkQueue_Setup;
            objHandler.populateQueueName();
             
            PageReference oExpPageRef = objHandler.saveTaskOrCase(); 
        
       
            
            System.assertEquals(ApexPages.currentPage().getParameters().get('id'),oCaseRecord.Id);
        }
     }
     /**
    * Method to test Case comments exception
    *
    * @param     NULL
    * @return    void 
    */   
     Private Static testMethod void testcasecommentsexception()
     {

        User oUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test ChangeCaseOwnerUser1' LIMIT 1];
        System.runAs(oUser)
        {
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE Name='TestAccount1' LIMIT 1 ];
            Case oCaseRecord1 = [Select id, AccountId,CTCI_List__c , Tenant_Id__c from Case Where AccountId=:oAcc.Id And Service_Center__c ='Market' and Department__c='Home Office Quality Operations Compliance' limit 1];  
        
            ApexPages.currentPage().getParameters().put('id', oCaseRecord1.id);

            HUMChangeCaseOwnerController objHandler = new HUMChangeCaseOwnerController();
            objHandler.checkUserProfile();  
            objHandler.populateQueueName();                    
            objHandler.sServiceCenter = 'Market';
            objHandler.sDepartment = 'Home Office Quality Operations Compliance';
            objHandler.sTopic = 'South Florida';
            objHandler.sCaseComments = 'asdasasdasasdasasdasasdasasdasasdasasdasasdasasdasasdasas';

            List <SelectOption> lstOption = objHandler.getServiceCenters();
            System.assert(lstOption.size()>0);

            objHandler.hPharmacyUser = true;
            lstOption = objHandler.getDepartments();

            objHandler.hPharmacyUser = false;
            lstOption = objHandler.getDepartments();
            System.assert(lstOption.size()>0);

            PageReference oExpPageRef = objHandler.saveTaskOrCase();          
            System.assertEquals(oExpPageRef,null);
            
        }
    }
    
     /**
    * Method to testCaseRecordType
    *
    * @param     NULL
    * @return    void 
    */   
     Private Static testMethod void testCaseRecordType()
     {

        User oUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test ChangeCaseOwnerUser1' LIMIT 1];
        System.runAs(oUser)
        {
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE Name='TestAccount1' LIMIT 1 ];
            Case oCaseRecord = [Select id, AccountId,CTCI_List__c ,Interacting_About_Type__c, Tenant_Id__c from Case Where AccountId=:oAcc.Id And Service_Center__c ='Market' and Department__c='Home Office Quality Operations Compliance' limit 1];  
            oCaseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.MEDICARE_CASE).getRecordTypeId(); 
            oCaseRecord.Interacting_About_Type__c ='Group';
            update oCaseRecord;

            ApexPages.currentPage().getParameters().put('id', oCaseRecord.id);

            HUMChangeCaseOwnerController objHandler = new HUMChangeCaseOwnerController();
            objHandler.checkUserProfile();                      
            objHandler.populateQueueName();                    
            objHandler.sServiceCenter = 'Market';
            objHandler.sDepartment = 'Home Office Quality Operations Compliance';
            objHandler.sTopic = 'South Florida';
            objHandler.sCaseComments = 'dddd';

            PageReference oPageRef = objHandler.saveTaskOrCase();          
            System.assertEquals(oPageRef,null);
        }
    }

      /*
    * Method Name : testLogNotes
    * Description : This method Is used To test log notes webservice
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testLogNotes()
    {
         User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test ChangeCaseOwnerUser1'];
         case caseRecord = [Select id,Policy_Member__r.Member__r.RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
        
        System.runAs(testUser)
        {
            Test.startTest();
               Case objCase = new Case();
                objCase = [Select id,Policy_Member__r.Member__r.RecordType.Name, Interacting_About_Type__c from case LIMIT 1];
                
                Continuation continueObj = (Continuation)HUMChangeCaseOwnerController.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' ,'Test', 'sAccNum',objCase.id);

                continueObj.state = 'BOTH';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"" } }';
                httpRes.setBody(resBodyString);
                System.assert(httpRes.getBody() == resBodyString);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
                string reqType = '';
                for(String s : mapDetails.keyset())
                {
                    reqType = s;
                } 
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                HUMChangeCaseOwnerController.parseLogNoteResponse(labelGetmember,continueObj);
                
                Continuation continueObjNeg = (Continuation)HUMChangeCaseOwnerController.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' ,'Test', 'sAccNum',objCase.id);

                continueObjNeg.state = 'BOTH';
                HttpResponse httpResN = new Httpresponse ();
                String resBodyStringN = '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"test" } }';
                httpResN.setBody(resBodyStringN);
                System.assert(httpResN.getBody() == resBodyStringN);
                Map<String, HTTPRequest> mapDetail = continueObjNeg.getRequests();
                string reqTypes = '';
                for(String s : mapDetail.keyset())
                {
                    reqTypes = s;
                } 
                List<string>labelGetmembers = new List<string>();
                labelGetmembers.add(reqTypes);
                Test.setContinuationResponse(reqTypes,httpResN);
                HUMChangeCaseOwnerController.parseLogNoteResponse(labelGetmembers,continueObjNeg);
                
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testLogNotesFault
    * Description : This method Is used To test log notes webservice
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testLogNotesFault()
    {
         User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test ChangeCaseOwnerUser1'];
         case caseRecord = [Select id,Policy_Member__r.Member__r.RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
        
        System.runAs(testUser)
        {
            Test.startTest();
               Case objCase = new Case();
                objCase = [Select id,Policy_Member__r.Member__r.RecordType.Name, Interacting_About_Type__c from case LIMIT 1];
                
                Continuation continueObj = (Continuation)HUMChangeCaseOwnerController.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' , 'Test', 'sAccNum',objCase.id);

                continueObj.state = 'BOTH';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{ "Fault":{ "faultcode":"Code-67.8.1", "faultstring":"error_service_name :RightSourceService| error_service_method : GetAdjustmentsAndPayments |        fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
                httpRes.setBody(resBodyString);
                System.assert(httpRes.getBody() == resBodyString);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
                string reqType = '';
                for(String s : mapDetails.keyset())
                {
                    reqType = s;
                } 
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                HUMChangeCaseOwnerController.parseLogNoteResponse(labelGetmember,continueObj);
               
            Test.stopTest();
        }
    }
     /*
    * Method Name : testMedicareCallsCC
    * Description : This method Is used To test Medicare Calls Case Comments section
    * Return type : None
    * Parameter   : None
    */
   static testMethod void testMedicareCallsCC()
    {
        User testUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test ChangeCaseOwnerUser1' LIMIT 1];
        
        System.runAs(testUser)
        {    
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE LastName = 'Account2' LIMIT 1 ];
            Case caseRecord = [SELECT id, AccountId, Tenant_Id__c ,Recordtype.Name,policy_member__c,Policy_Member__r.Name, CTCI_List__c ,Service_Center__c ,Department__c, User__c From Case WHERE AccountId=:oAcc.Id AND Policy_Member__r.Name = 'MedCallsCC' LIMIT 1];
            System.assertEquals(caseRecord.Policy_Member__r.Name, 'MedCallsCC');
            
            CaseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM).getRecordTypeId(); 
            CaseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Tenant_Id__c = '00';
            update caseRecord;
            System.assertEquals(CaseRecord.Tenant_Id__c,'00');

            ApexPages.currentPage().getParameters().put('id', caseRecord.id);
            PermissionSet ps = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM];
            insert new PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = ps.Id );
            HUMChangeCaseOwnerController ceHum = new HUMChangeCaseOwnerController();  
            pageReference pref;      
            Test.startTest();
                                
            //Test required inputs
            pRef =ceHum.saveTaskOrCase();
            //Medicare Calls Case Comment insert
            ceHum.iCountMediIssueCC = 10;
            ceHum.iCountMediResCC = 25;
            ceHUM.sMedCallsIssueCC = 'Test Med Call Case Comment';
            ceHUM.sMedCallsResCC = 'TestMed Call Case CommentRes';
            pRef = ceHum.saveTaskOrCase();
            System.assert(ceHUM.sMedCallsIssueCC == 'Test Med Call Case Comment');
            //Medicare Calls Case Comment update
            ceHum.iCountMediIssueCC = 25;
            ceHum.iCountMediResCC = 30;
            ceHUM.sMedCallsIssueCC = 'Test Med Call Case Comment Update';
            ceHUM.sMedCallsResCC = 'TestMed Call Case CommentRes Update';
            ceHUM.bMediCCUpdate = true;
            pRef = ceHum.saveTaskOrCase();
            //Test length validation
            ceHum.iCountMediIssueCC = 1;
            ceHum.iCountMediResCC = 2;
            ceHUM.sMedCallsIssueCC = 'TestL';
            ceHUM.sMedCallsResCC = 'TestL';
            pRef =ceHum.saveTaskOrCase();
            Test.stopTest();
        } 
    }
    
      /*
    * Method Name : testPharmFunc
    * Description : This method Is used To test log notes webservice
    * Return type : None
    * Parameter   : None
    */       
    
    static testMethod void testPharmFunc()
    {
        User testUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test ChangeCaseOwnerUser1' LIMIT 1];
        
        System.runAs(testUser)
        {   
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE LastName = 'Account2' LIMIT 1 ];
            Case caseRecord = [SELECT id, AccountId, Tenant_Id__c ,Recordtype.Name,policy_member__c,Policy_Member__r.Name, CTCI_List__c ,Service_Center__c ,Department__c From Case WHERE AccountId=:oAcc.Id And Policy_Member__r.Name = 'MedCallsCC' LIMIT 1];
            System.assertEquals(caseRecord.Policy_Member__r.Name, 'MedCallsCC');
            CaseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM).getRecordTypeId(); 
            CaseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Tenant_Id__c = '00';
            update caseRecord;
            System.assertEquals(CaseRecord.Tenant_Id__c,'00');

            ApexPages.currentPage().getParameters().put('id', caseRecord.id);
            ApexPages.currentPage().getParameters().put('redirectFlag','true');
            ApexPages.currentPage().getParameters().put('errorCaseComment','true');
            PermissionSet ps = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.PHARMACYSUPERVISOR_PERMISSION_HUM];
            insert new PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = ps.Id );
            HUMChangeCaseOwnerController ceHum = new HUMChangeCaseOwnerController();  
            pageReference pref;    
            Test.startTest();
            pref = ceHUM.populateHiddentVal();
            pref = ceHUM.saveHPCaseComment();
            pref = ceHUM.redirectToCasePage();
            List <SelectOption> lstOption = ceHUM.getCaseCommentOptions();
            ceHUM.sCodeHidden = '';
            ceHUM.iCountMediIssueCCInit = 0;
            ceHUM.iCountMediResCCInit = 0;
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
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test ChangeCaseOwnerUser3'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Case CaseRecord = [select id,Owner_Queue__c from Case where RecordType.Name = 'HP Member Case' and Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
            System.assertequals(CaseRecord.Owner_Queue__c,'Louisville RSO Calls Support');
            ApexPages.currentPage().getParameters().put('id', CaseRecord.id);
            HUMChangeCaseOwnerController hcc = new HUMChangeCaseOwnerController();
            system.assert(hcc.bRSOHPFlag!=null);

            Case CaseRecord1 = [select id,Owner_Queue__c from Case where RecordType.Name = 'Member Case' and Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
            System.assertequals(CaseRecord1.Owner_Queue__c,'Louisville RSO Calls Support');
            ApexPages.currentPage().getParameters().put('id', CaseRecord1.id);
            HUMChangeCaseOwnerController hcc1 = new HUMChangeCaseOwnerController();
            system.assert(hcc1.bRSOHPFlag!=null);
            Test.stopTest();
        }
    }
    
    Static testMethod void testOverAll(){
     
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test ChangeCaseOwnerUser3'];
        Group oGrp1 = New Group(Name ='Cincinnati CRU1',DeveloperName='Cincinnati_CRU2', Type='QUEUE');
        insert oGrp1;
        QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp1.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;   
        System.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');  
        
        System.runAs(testUser)
        {
            Test.startTest();
            Case CaseRecord = [select id,Owner_Queue__c from Case where RecordType.Name = 'HP Member Case' and Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
            System.assertequals(CaseRecord.Owner_Queue__c,'Louisville RSO Calls Support');
            ApexPages.currentPage().getParameters().put('id', CaseRecord.id);
            HUMChangeCaseOwnerController hcc = new HUMChangeCaseOwnerController();
            system.assert(hcc.bRSOHPFlag!=null);

            Case CaseRecord1 = [select id,Owner_Queue__c from Case where RecordType.Name = 'Member Case' and Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
            System.assertequals(CaseRecord1.Owner_Queue__c,'Louisville RSO Calls Support');
            ApexPages.currentPage().getParameters().put('id', CaseRecord1.id);
            HUMChangeCaseOwnerController hcc1 = new HUMChangeCaseOwnerController();
            hcc1.returnToCaseCreatorQueue();
            hcc1.returnToPriorSenderQueue();
            system.assert(hcc1.bRSOHPFlag!=null);
            Test.stopTest();
        }
    }
    
          /*
    * Method Name : testMHKDisputeTask
    * Description : This method Is used To test MHK Dispute task
    * Return type : None
    * Parameter   : None
    */ 
    private static testMethod void testMHKDisputeTask(){
     
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test ChangeCaseOwnerUser1'];
        
        System.runAs(testUser)
        {
            insert new CRMFunctionality_ONOFF_Switch__c(Name = '1414643',IsON__c=true,Release_Name__c='03/19/2021',Scrum_Team__c='Transformers',Story_Title__c='PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case');
            
                     
            Case CaseRecord = [select id,Owner_Queue__c, Type, Tenant_Id__c,G_A_Rights_Given__c,Complaint__c, General_Case__c from Case where Type = 'MHK Dispute Task' and Owner_Queue__c = 'Cincinnati CRU1' limit 1];
            System.assertequals(CaseRecord.Owner_Queue__c,'Cincinnati CRU1');
            ApexPages.currentPage().getParameters().put('ID', CaseRecord.id);
            HUMChangeCaseOwnerController hcc = new HUMChangeCaseOwnerController();
            pageReference pref;
            Test.startTest();
            List <SelectOption> lstOption = hcc.getServiceCenters();
            List <SelectOption> lstOption1 = hcc.getDepartments();
            hcc.sCaseType = 'MHK Dispute Task';
            hcc.sCaseComments = '';
            pref = hcc.saveTaskOrCase();
            hcc.sSelectedWorkQueue = 'Market_Quality_Operations_Compliance';
            pref = hcc.saveTaskOrCase();
            hcc.sServiceCenter = 'Louisville';
            hcc.sDepartment = 'Griveance and Appeals';
            pref = hcc.saveTaskOrCase();
            hcc.bGAndASectionRender = true;
            CaseRecord.G_A_Rights_Given__c = '';
            CaseRecord.Complaint__c='';
            update CaseRecord;
            List <SelectOption> lstOption2 = hcc.getDepartments();
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testnonMHKDisputeTask
    * Description : This method Is used To test non MHK Dispute task
    * Return type : None
    * Parameter   : None
    */ 
    private static testMethod void testnonMHKDisputeTask(){
     
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test ChangeCaseOwnerUser1'];
        
        System.runAs(testUser)
        {
            insert new CRMFunctionality_ONOFF_Switch__c(Name = '1414643',IsON__c=true,Release_Name__c='03/19/2021',Scrum_Team__c='Transformers',Story_Title__c='PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case');
            
                     
            Case CaseRecord = [select id,Owner_Queue__c, Type,G_A_Rights_Given__c,Complaint__c, Tenant_Id__c, General_Case__c from Case where Type <> 'MHK Dispute Task' and Owner_Queue__c = 'Cincinnati CRU1' limit 1];
            System.assertequals(CaseRecord.Owner_Queue__c,'Cincinnati CRU1');
            ApexPages.currentPage().getParameters().put('ID', CaseRecord.id);
            HUMChangeCaseOwnerController hcc = new HUMChangeCaseOwnerController();
            pageReference pref;
            Test.startTest();
            hcc.sServiceCenter = 'Provider';
            hcc.sDepartment = 'Correspondence Task to MHK';
            pref = hcc.saveTaskOrCase();
            CaseRecord.G_A_Rights_Given__c = null;
            update CaseRecord;
            hcc.bGAndASectionRender=true;
            pref = hcc.saveTaskOrCase();
            
            /* hcc.QualityReferralCRMResponseDataContract.ReferralID = 123456;
            hcc.createCaseComment(); */
            Test.stopTest();
        }
    }

    /*
    * to imporve code coverage 
    */
    static testMethod void testMedicareCallsCCUser()
    {
        User testUser = [SELECT id, Current_Queue__c,Name, LastName from User WHERE  Lastname = 'test ChangeCaseOwnerUser1' LIMIT 1];
        
        System.runAs(testUser)
        {    
            
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE LastName = 'Account2' LIMIT 1 ];
            Case caseRecord = [SELECT id, AccountId, Tenant_Id__c ,Recordtype.Name,policy_member__c,Policy_Member__r.Name, CTCI_List__c ,Service_Center__c ,Department__c, User__c From Case WHERE AccountId=:oAcc.Id AND Policy_Member__r.Name = 'MedCallsCC' AND Status = 'Pending - Unassigned' LIMIT 1];
            System.assertEquals(caseRecord.Policy_Member__r.Name, 'MedCallsCC');
            
            CaseRecord.User__c = testUser.id;

            CaseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM).getRecordTypeId(); 
            CaseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Tenant_Id__c = '00';
            update caseRecord;
            System.assertEquals(CaseRecord.Tenant_Id__c,'00');

            ApexPages.currentPage().getParameters().put('id', caseRecord.id);
            PermissionSet ps = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM];
            insert new PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = ps.Id );
            HUMChangeCaseOwnerController ceHum = new HUMChangeCaseOwnerController();  
            pageReference pref;      
            Test.startTest();
            ceHum.bMedicareCalls = false;
            ceHum.sHashTag = 'Hashtag Comment';
            ceHum.sCaseComments = 'Test case comments';                    
            //Test required inputs
            pRef =ceHum.saveTaskOrCase();
            //Medicare Calls Case Comment insert
            ceHum.bMedicareCalls = false;
            ceHum.sHashTag = 'Hashtag Comment';
            String str='';
            for(Integer i= 0; i<=3000; i++){
                str += 'Test case comments' + 'aaa'+i;
            }        
            ceHum.sCaseComments = str;            
            //Test required inputs
            pRef =ceHum.saveTaskOrCase();

            ceHum.sCaseComments = 'Test case comments'; 

            ceHum.bMedicareCalls = true;
            ceHum.iCountMediIssueCC = 10;
            ceHum.iCountMediResCC = 25;
            ceHUM.sMedCallsIssueCC = 'Test Med Call Case Comment';
            ceHUM.sMedCallsResCC = 'TestMed Call Case CommentRes';
            pRef = ceHum.saveTaskOrCase();
            System.assert(ceHUM.sMedCallsIssueCC == 'Test Med Call Case Comment');
            //Medicare Calls Case Comment update
            ceHum.iCountMediIssueCC = 25;
            ceHum.iCountMediResCC = 30;
            ceHUM.sMedCallsIssueCC = 'Test Med Call Case Comment Update';
            ceHUM.sMedCallsResCC = 'TestMed Call Case CommentRes Update';
            ceHUM.bMediCCUpdate = true;
            pRef = ceHum.saveTaskOrCase();
            //Test length validation
            ceHum.iCountMediIssueCC = 1;
            ceHum.iCountMediResCC = 2;
            ceHUM.sMedCallsIssueCC = 'TestL';
            ceHUM.sMedCallsResCC = 'TestL';
            ceHUM.sMedicareIssInit = 'TestL';
            ceHUM.sMedicareResInit = 'TestL';
            pRef =ceHum.saveTaskOrCase();
            

            User oLoggedUser= [Select Id, lastName from User where lastName = 'test ChangeCaseOwnerUser2'];
            System.runAs(oLoggedUser)
            {
                
                
                ApexPages.currentPage().getParameters().put('id', caseRecord.id);                   
                pRef =ceHum.saveTaskOrCase();
            }
            
            Test.stopTest();
        } 
    }

    static testMethod void testPharmFuncfalse()
    {
        User testUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test ChangeCaseOwnerUser1' LIMIT 1];
        
        System.runAs(testUser)
        {   
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE LastName = 'Account2' LIMIT 1 ];
            Case caseRecord = [SELECT id, AccountId,OwnerId, Tenant_Id__c ,Recordtype.Name,policy_member__c,Policy_Member__r.Name, CTCI_List__c ,Service_Center__c ,Department__c From Case WHERE AccountId=:oAcc.Id And Policy_Member__r.Name = 'MedCallsCC' LIMIT 1];
            System.assertEquals(caseRecord.Policy_Member__r.Name, 'MedCallsCC');
            caseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM).getRecordTypeId(); 
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Tenant_Id__c = '00';
            update caseRecord;
            System.assertEquals(caseRecord.Tenant_Id__c,'00');

            ApexPages.currentPage().getParameters().put('id', caseRecord.id);
            ApexPages.currentPage().getParameters().put('redirectFlag','true');
            ApexPages.currentPage().getParameters().put('errorCaseComment','false');
            PermissionSet ps = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.PHARMACYSUPERVISOR_PERMISSION_HUM];
            insert new PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = ps.Id );
            HUMChangeCaseOwnerController ceHum = new HUMChangeCaseOwnerController();  
            pageReference pref;    
            Test.startTest();
            pref = ceHUM.populateHiddentVal();
            ceHUM.sCodeHidden = '1234';
            ceHUM.sCaseComments ='Case Comments Test';
            ceHum.sHashTag = '#';
            pref = ceHUM.saveHPCaseComment();
            pref = ceHUM.redirectToCasePage();
            List <SelectOption> lstOption = ceHUM.getCaseCommentOptions();
            ceHUM.sCodeHidden = '';
            ceHUM.iCountMediIssueCCInit = 0;
            ceHUM.iCountMediResCCInit = 0;


            Test.stopTest();
            
        }
    } 
    Private Static testMethod void pharmacySpecialist()
    {
        User oCurrentUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test PharmaUser' LIMIT 1];
        User oNonHomeUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test ChangeCaseOwnerUser2' LIMIT 1];
        System.runAs(oCurrentUser)
        {
          
            Case oCase = [SELECT id, AccountId,Owner_Queue__c From Case  limit 1];

            ApexPages.currentPage().getParameters().put('id', oCase.id);
            HUMChangeCaseOwnerController objHandler = New HUMChangeCaseOwnerController();   
            Test.startTest();
            HUMChangeCaseOwnerController.hasAccessToWorkQueue(oCurrentUser.id, 'Cincinnati CRU PGQ');  
            HUMChangeCaseOwnerController.hasAccessToWorkQueue(oNonHomeUser.id, 'abcdqweqwe');
            HUMChangeCaseOwnerController.sendRequestLogNotes('null', 'null','null' , 'null', 'null', 'null');
            objHandler.checkUserProfile();
            objHandler.populateQueueName();
            objHandler.sTopic = null;
            objHandler.sGrpQueueName = null;  
            
            Test.stopTest();
         
        }   
    } 

    Private Static testMethod void exceptions()
    {
        User oCurrentUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test PharmaUser' LIMIT 1];
        User oNonHomeUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test ChangeCaseOwnerUser2' LIMIT 1];
        System.runAs(oNonHomeUser)
        {
          
            
            Test.startTest();
            ID ab = null;
            HUMChangeCaseOwnerController.hasAccessToWorkQueue(oCurrentUser.id, 'Cincinnati CRU PGQ');  
            HUMChangeCaseOwnerController.hasAccessToWorkQueue(ab, 'abcdqweqwe');
            HUMChangeCaseOwnerController.sendRequestLogNotes('null', 'null','null' , 'null', 'null', 'null');
                        
            Test.stopTest();
         
        }   
    } 
    

    /*
    * Method Name : pharmacyTestMethod
    * Description : This method Is used To test Pharmacy scenarios
    * Return type : None
    * Parameter   : None
    */  
   Private Static testMethod void pharmacyTestMethod()
    {
        User oCurrentUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test PharmaUser' LIMIT 1];
    
                                   
        System.runAs(oCurrentUser)
        {
            insert new CRMFunctionality_ONOFF_Switch__c(Name = '2578074',IsON__c=true,Release_Name__c='03/19/2021',Scrum_Team__c='Transformers',Story_Title__c='PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case');     
            insert new CRMFunctionality_ONOFF_Switch__c(Name = '1414643',IsON__c=true,Release_Name__c='03/19/2021',Scrum_Team__c='Transformers',Story_Title__c='PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case');
  
            Case oCase = [SELECT id, AccountId,Owner_Queue__c,CTCI_List__c, Tenant_Id__c ,User__c, Service_Center__c ,Department__c,General_Case__c,DST_Case__c,HO_Dental_Case__c,CoKY_Case__c
                         ,HO_Medical_Case__c From Case WHERE Service_Center__c ='Cincinnati'and Department__c='CRU1' limit 1];
            oCase.Tenant_Id__c = '00';
            oCase.User__c = NULL;
            ocase.Interacting_About_Type__c = 'Group';
            update oCase;

            ApexPages.currentPage().getParameters().put('id', oCase.id);
            HUMChangeCaseOwnerController objHandler = New HUMChangeCaseOwnerController();   
            test.startTest();  
            objHandler.sCenters.add('Cincinnati');
             objHandler.checkUserProfile();            

            List <SelectOption> lstOption = objHandler.getServiceCenters();
            
            objHandler.sCaseType = 'MHK Dispute Task';
            objHandler.bGAndASectionRender = true;
                        
            objHandler.sServiceCenter = 'Cincinnati';                        
            List <SelectOption> lstOption1 = objHandler.getDepartments();

            objHandler.sDepartment = 'CRU1';
            objHandler.hPharmacyUser = true;
            List <SelectOption> lstOption2 = objHandler.getTopics();
            
            
        }   
    }      
        
}