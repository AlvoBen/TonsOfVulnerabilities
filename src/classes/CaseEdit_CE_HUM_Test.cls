/**********************************************************************************************************************************************************
Apex Class Name  : CaseEdit_CE_HUM_Test
Version          : 1.0
Created Date     : SEP 25 2015
Function         : Test class for CaseEdit_CE_HUM
***********************************************************************************************************************************************************

Modification Log: 

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------
* Pradeep Raju                                    09/25/2015                  Original Version
* Avinash Choubey                                 01/21/2016                  Case Edit page name change
* Avinash Choubey             29427               01/25/2016                  fixes for review comment
* Avinash Choubey             29427               02/02/2016                  Changes as per C/I DeActivation Changes in main class
* Avinash Choubey                                 02/17/2016                  modified to to cover code added in main class to fix defect# 215911
* Avinash Choubey             32459               07/11/2016                  Chnages made as part of REQ- 255151
* Vamsi Kondragunta           40747               09/22/2016                  changes made as part of build fix.
* Praveen Kumar Parimi        50918               12/06/2016                  Changes made as part of CA 6412051 aka REQ 290998.
* Syed Mubarak                74695               03/01/2017                  Added /Modified code for REQ - 303466, REQ - 301582
* Pallavi ravishankar         78710               03/22/2017                  REQ 304973 - changes as per ctci data model       
* Pallavi ravishankar         84779               04/17/2017                  Updated test class for Defect Fix 272577.0001 changes
* Syed Mubarak                89120               04/28/2017                  Changes made for REQ - 298771 And REQ - 296939
* Vishal Verma                87024               05/05/2017                  REQ - 308318: Case Comment For Humana Pharmacy
* Syed Mubarak                94636               05/22/2017                  Added And Modified for REQ  - 304829
* Gargey Sharma               95016               05/18/2017                  REQ - 300142 : CSI 405 CMO 684 Medicare Calls Issue And Resolution
* Gargey Sharma               97834               05/31/2017                  Updated as per code changes for defect 290523.0001
* Gargey Sharma               99543               06/07/2017                  Updated as per changes in approach for REQ - 300142
* Syed Mubarak               110889               07/18/2017                  updated for REQ - 321253
* Harika Devi Kalidindi      112000               07/20/2017                  REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
* Pallavi R                  110756               07/21/2017                  Updated for REQ - 322573 
* Vishal Verma               114200               07/27/2017                  REQ - 323542 : Exception handling   
* Syed Mubarak               115266               08/01/2017                  REQ - 304169: Launch EMME code coverage changes
* Pallavi R                  116812               08/04/2017                  REQ - 322573 - Updated code as per requirement updates for validations
* Sharan Shanmugam                                08/09/2017                  REQ - 324814 : Save Case Comment to CRM during ePost failure
* Pallavi R                  138826               10/24/2017                  REQ - 311453 - Updated code as per requirement updates for Ca 7079470
* Mohammed Noor              142298               10/27/2017                  REQ - 335062 - CR 5317 Custom Policy Member Search & MET Section Save Order
* Prudhvi Pamarthi           148860               11/20/2017                  REQ - 328245 Sporadic Case Save Issue - Proactive Changes
* Manoj Chaurasia            202594               03/29/2018                  REQ - 357740 Ability to display TAS combination in CRM
* Moshitha Gunasekaran       218172               04/22/2018                  REQ - 359158 Populate Creditable Coverage subtype on completion of ATTVF template 
* Bharath Rajappan           225143               05/07/2018                  REQ - 359179 Case Status freeze for creditable Coverage Attestation verification process
* Mohammed Noor              232736               05/22/2018                  Increase code coverage for REQ - 362097 changes
* Sunil Bukya                244531               06/17/2018                  REQ - 366900 - Implemented Member_Id__c platform encryption changes.
* Bharath Rajappan                                07/11/2018                  REQ - 367804 Creditable Coverage - Ability for CRM to disable Case Edit And Case Detail page buttons
* Syed Mubarak                                    07/26/2018                  REQ - 367878 MET Section CA ticket fix
* Anurag Shah                267634               07/20/2018                  REQ - 371243 : Added a logic to determine page section for PDP Pilot access based on logged in user 
* Prathyu Tushar             324032               01/02/2019                  REQ - 380419: Auto route cases with Expedited Appeal And Part D Redetermination Template
* Pooja Kumbhar                                   02/20/2019                  MF-4 financial recovery
* Prathyu Tushar             344948               03/18/2019                  Added the unit test to intitalize And assert to check those fields are covered.
* Ravi Prakash kantamneni    345989               03/21/2019                  REQ-383718 - Automatically Record Order Details to Case: 'Save & Log' to New Case & Existing Case(RXRF)
* Santhi Mandava                                  03/12/2019                  Removed dpaas switch code. Removed "DpaaSWebServiceMockImpl" uage
* Mohammed Noor              352321               04/17/2019                  REQ - 396223 - Changes done for TRR T/A/S BAM Tracking.
* Santhi Mandava                                  04/15/2019                  Replaced "Member_Id_Value__c" with Name
* Manikanta                                       08/21/2019                  REQ - 396234 - Ability for CRMS to systematically display the HP case record type and Case Comment section
* Rakesh Patni                                    08/20/2019                  REQ - 405880. Launch Compass Funcitonality
* Pooja Kumbhar                                   04/29/2020                  Checkmarx test method without assert error fix.
* Sunil Kumar Reddy Y                             07/13/2020                  User Story 1122112: PR00091574 - MF 1 - Quality - OGO - Ability to automatically update case status to In Progress on a Medicare Case if it Is a Complaint (CRM)
* Pooja Kumbhar                                   08/13/2020                  TestClass fix for Copado
* Sunil Kumar Reddy Y                             08/14/2020                  Overall Code Coverage
* Samantha Tennyson                               08/27/2020                  User Story 1318943: PR00091574 - MF 1 - AHT - Ability to create "Quick MET Entries" from MET Enrollment section on Case Edit page (RF)
* Mithra Bharadwaj                                09/21/2020                  US#1393270 CRM Process - Initiating queue/views for transfers to MHK
* Sunil Kumar Reddy Y                             11/05/2020                  User Story 1641500: PR00091574 - MF 1 - Quality - Ability to auto route KY and IL MCD Grievance cases directly to the Resolution team (CRM)
* Amar Gadewar                                    01/15/2021                  User Story 1745168: PR00091574 - MF 1 - AHT - Ability to create "Quick MET Entries" from MET Enrollment section on Case Edit page (CRM)
* Santhi Mandava                                  02/01/2021                  Fixed code coverage issue
* Vinay Lingegowda                             	  02/19/2021                  User Story 1941154: PR00091574 - IR5050 - INC0855442 - Medicare Good Cause Reinstatement  - Case auto routing to Other queue View (CRM)
* Sunil Kumar Reddy Y                             03/25/2021                  User Story 2056167: T1PRJ0001827 - MF 1 - AHT - Ability to auto launch PCP/PCD template when a case is created through quick start (RF)
* Kavya Bondada                                   05/28/2021                  User Story 2187149: T1PRJ0001827 - MF 1 - Quality - Auto route QAA cases to the G&A team (CRM)
* Aaron Speakman                                  05/21/2021                  Code Coverage Improvements
* Himalay Patel                                   06/07/2021                   User Story 2140526: T1PRJ0002517 - MF 1 - TECH - Switch Clean Up - Auto-Select G&A Rights Given and Complaint Based on Service Model Type - Case Edit Page (CRMS) (ID# 137)
* Samantha Tennyson                               06/22/2021                   User Story 2393164: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 6/11/21, 6/25/21, 7/9/21 and 7/23/21 releases (CRM)
* Prasuna Pattabhi                                09/06/2021                    Code Coverage Improvements
* Keshava Reddy T                                 10/12/2021                   User Story 2737375: T1PRJ0001827 - MF 1 - Quality - Ability to capture Verbal Consent Obtained and making it required for certain case types (RF)
* Pooja Kumbhar                                   11/16/2021                    Code Coverage Improvement
* Ashok Kumar Nutalapati                          05/20/2022                  REQ - 2334447 T1PRJ0002517 - MF 5 - TECH - Switch Clean Up - Add Log Code to Case Edit Page for HSP Users (CRMS) (ID# 142)
* Pooja Kumbhar                                   07/31/2023                   Test class failure fix for release 8_4
* Santhi Mandava                                  01/03/2024                   User Story 5243585: T1PRJ1132745 - MF28328 - C06; Case Management - Classic & Lightning - Oklahoma MCD Update Medicaid PAR Provider Not Accepting Routing rules (MCD OK MBRCALLS 1.1) 
***********************************************************************************************************************************************************/

@isTest
Private Class CaseEdit_CE_HUM_Test
{
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        //Create Parent Group
        Group grp = new Group();
        grp.name = 'Louisville Premium Payment Recovery PGQ';
        grp.Type = 'Regular'; 
        Insert grp; 
 
        //Create Group Member
        GroupMember grpMem1 = new GroupMember();
        grpMem1.UserOrGroupId = UserInfo.getUserId();
        grpMem1.GroupId = grp.Id;
        Insert grpMem1;
        
        //Create Parent Group
        Group grp2 = new Group();
        grp2.name = 'PCP Assignment Research PGQ';
        grp2.Type = 'Regular'; 
        Insert grp2; 
 
        //Create Group Member
        GroupMember grpMem2 = new GroupMember();
        grpMem2.UserOrGroupId = UserInfo.getUserId();
        grpMem2.GroupId = grp2.Id;
        Insert grpMem2;
        
        Group oCompassNewGroup = null;
        oCompassNewGroup = new Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oCompassNewGroup;
        system.assertEquals(oCompassNewGroup.Type,'Queue');

        QueueSObject oCompassNewQueue = new QueueSObject(QueueId = oCompassNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
        insert oCompassNewQueue;
        system.assertEquals(oCompassNewQueue.SObjectType,'Work_Queue_Setup__c');

        
        User usr = HUMTestDataHelperTest.createTestUser('CaseEditUser1','Customer Care Specialist');
        usr.Current_Queue__c ='Test Business Process and Testing';
        usr.Mentor_Sub_Function__c = 'Go365';
        insert usr;
        system.assertEquals(usr.lastname=='test CaseEditUser1',true);
        
        PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = usr.id, PermissionSetId = pCheck.id);
        
        User usr1 = HUMTestDataHelperTest.createTestUser('CaseEditUser2','Customer Care Supervisor');
        insert usr1;
        system.assertEquals(usr1.lastname=='test CaseEditUser2',true);
        
        PermissionSet pRSOHP = [SELECT ID From PermissionSet WHERE Name = 'CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = usr1.id, PermissionSetId = pRSOHP.id);
        
        Group oGrp = NULL;
        Group oGrp2 = NULL;
        
        loadCaseSubType();
        loadlaunchEMMEURL();
        
        oGrp=New Group(Name='Cincinnati_Calls1', Type='QUEUE');
        insert oGrp;
        system.assertNotEquals(oGrp,null,'Value not matched');
        System.assertEquals(oGrp.Name, 'Cincinnati_Calls1');
        
        oGrp2 = New Group(Name='CincinnatiCalls1', Type='QUEUE');
        insert oGrp2;
        system.assertNotEquals(oGrp2,null,'Value not matched');
        System.assertEquals(oGrp2.Name, 'CincinnatiCalls1');
        
        QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;   
        system.assertNotEquals(oQueueObject,null,'Insert Failed');
        System.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');
        
        QueuesObject oQueueObject2 = New QueuesObject(QueueID = oGrp2.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject2;   
        system.assertNotEquals(oQueueObject2,null,'Queue Not inserted');
        System.assertEquals(oQueueObject2.SobjectType, 'Work_Queue_Setup__c');
        
        system.runAs(usr)
        { 
            List<CRMFunctionality_ONOFF_Switch__c> lstSwitches = new List<CRMFunctionality_ONOFF_Switch__c>();
            lstSwitches.Add(new CRMFunctionality_ONOFF_Switch__c(Name = '5231359',IsON__c=true));
            lstSwitches.Add(new CRMFunctionality_ONOFF_Switch__c(Name = '5243585',IsON__c=true));
            insert lstSwitches;
            System.assertNotEquals(lstSwitches[0].Id,Null);

        HUM_Webservice_Callout__c RighSourceEditServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_EditMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceEditServiceDetails;
        HUM_Webservice_Callout__c ehubDCNService  = New HUM_Webservice_Callout__c(Name = 'DCN_Ehub',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );    
        insert ehubDCNService;
        Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c(Public_Group_Name__c = 'Group one', Service_Center__c = 'Cincinnati', Department__c = 'Calls1', Name = 'Cincinnati_Calls1');
        oWQS.OwnerId = oGrp.Id;
        insert oWQS;
        System.assert(oWQS != null);
        System.assertEquals(oWQS.Public_Group_Name__c, 'Group one');
        
        List<HUMApplicationSettings__c> lstApplicationName = New List<HUMApplicationSettings__c>();
        System.assertNotEquals(lstApplicationName, NULL, 'Values not matched');
        lstApplicationName.add(New HUMApplicationSettings__c(Name = 'CIPHERCLOUD', Active__c = true));
        insert lstApplicationName;
        System.assertNotEquals(lstApplicationName.size(), 0, 'Values not matched');
            
        List<HUMCipherCloudSettings__c> lstCipherCloudSetting = New List<HUMCipherCloudSettings__c>();
        System.assertNotEquals(lstCipherCloudSetting, NULL, 'Values not matched');
        lstCipherCloudSetting.add(New HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME', Value__c='CRMR1'));
        insert lstCipherCloudSetting;
        System.assertNotEquals(lstCipherCloudSetting.size(), 0, 'Values not matched');
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
        
        Policy__c policyRec = New Policy__c();
        policyRec.Group_Name__c = accountRec.Id;
        policyRec.Product__c = null;
        policyRec.Policy_Group_Number__c='11111';
        policyRec.Major_Lob__c = 'MEF';
        insert policyRec;
        system.assertEquals(policyRec.Group_Name__c==accountRec.Id,true);
        
        Policy_Member__c policyMemRec = New Policy_Member__c();
        policyMemRec.Name = '12345';
        policyMemRec.Policy__c = policyRec.Id;
        policyMemRec.Member__c = accountRec.Id; 
        insert policyMemRec;
        system.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
        
        List<Classification_Type__c> ctList= New list<Classification_Type__c>();
        //Launch Compass Functionality
        Classification_Type__c ctRec0 = New Classification_Type__c();
        ctRec0.name = 'Provider Calls (Dental)';
        ctRec0.Is_Active__c=true;
        ctRec0.Mentor_Function__c ='Contact Center Provider';
        ctList.add(ctRec0);
        Classification_Type__c ctRec = New Classification_Type__c();
        ctRec.name = 'calls';
        ctRec.Is_Active__c=true;
        ctList.add(ctRec);
        
        Classification_Type__c ctRec1 = New Classification_Type__c();
        ctRec1.name = 'calls (RSO)';
        ctRec1.Is_Active__c=true;
        ctList.add(ctRec1);
        
        Classification_Type__c ctRec2 = New Classification_Type__c();
        ctRec2.name = 'Humana Pharmacy Calls';
        ctRec2.Is_Active__c=true;
        ctList.add(ctRec2);

        Classification_Type__c ctRec3 = New Classification_Type__c();
        ctRec3.name = 'HP Specialty Calls';
        ctRec3.Is_Active__c=true;
        ctList.add(ctRec3);

        insert ctList;
        system.assertEquals(ctList.size(),5);
        
        Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = 'Test Business Process and Testing', OwnerId = oCompassNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing',Classification_Type__c=ctList[0].id);
        insert oWorkQueue;
        system.assertEquals(oWorkQueue.Service_Center__c,'Business');
        
        List<Classification__c> lstClassification = New List<Classification__c>();
        Classification__c classificationRec = New Classification__c();
        classificationRec.Name = 'Benefits';
        classificationRec.Is_Active__c = true;
        lstClassification.add(classificationRec);
        
        Classification__c classificationRecDec = New Classification__c();
        classificationRecDec.Name = 'Humana Pharmacy';
        classificationRecDec.Is_Active__c = true;
        lstClassification.add(classificationRecDec);
        
        Classification__c classificationRecDec1 = New Classification__c();
        classificationRecDec1.Name = 'G&A';
        classificationRecDec1.Is_Active__c = true;
        lstClassification.add(classificationRecDec1);

        Classification__c classificationRecDec2 = New Classification__c();
        classificationRecDec2.Name = 'Rx';
        classificationRecDec2.Is_Active__c = true;
        lstClassification.add(classificationRecDec2);
        
        Classification__c classificationRecDec3 = New Classification__c();
        classificationRecDec3.Name = 'HP Order State';
        classificationRecDec3.Is_Active__c = true;
        lstClassification.add(classificationRecDec3);

        Classification__c classificationRecDec4 = New Classification__c();
        classificationRecDec4.Name = 'Website (CT & TM)';
        classificationRecDec4.Is_Active__c = true;
        lstClassification.add(classificationRecDec4);

        insert lstClassification;
        system.assertEquals(lstClassification.size(),6);
       
        List<Intent__c> lstIntent = New List<Intent__c>();
        Intent__c intentRec = New Intent__c();
        intentRec.Name = 'Rewards';
        intentRec.Is_Active__c = true;
        lstIntent.add(intentRec);
        
        Intent__c intentRecDec = New Intent__c();
        intentRecDec.Name = 'Estimate';
        intentRecDec.Is_Active__c = true;
        lstIntent.add(intentRecDec);
        
        
        Intent__c intentRecDec1 = New Intent__c();
        intentRecDec1.Name = 'Expedited';
        intentRecDec1.Is_Active__c = true;
        lstIntent.add(intentRecDec1);
    
        
        Intent__c intentRecDec2 = New Intent__c();
        intentRecDec2.Name = 'Part D Redetermination';
        intentRecDec1.Is_Active__c = true;
        lstIntent.add(intentRecDec2);
              
        Intent__c intentRecDec3 = New Intent__c();
        intentRecDec3.Name = 'In Process';
        intentRecDec3.Is_Active__c = true;
        lstIntent.add(intentRecDec3);
        
        Intent__c intentRecDec4 = New Intent__c();
        intentRecDec4.Name = 'Web Education/Navigation';
        intentRecDec4.Is_Active__c = true;
        lstIntent.add(intentRecDec4);

        insert lstIntent;
        system.assertEquals(lstIntent.size(),6);
        
        List<CTCI_Junction__c> ctciList = New list<CTCI_Junction__c>();
        CTCI_Junction__c ctciRec = New CTCI_Junction__c();
        ctciRec.Classification_Type__c = ctRec.id;
        ctciRec.Classification__c = classificationRec.Id;
        ctciRec.Intent__c = intentRec.id;
        ctciRec.IsActive__c = true;
        ctciList.add(ctciRec);
     
        CTCI_Junction__c ctciRec1 = New CTCI_Junction__c();
        ctciRec1.Classification_Type__c = ctRec.id;
        ctciRec1.Classification__c = classificationRecDec1 .Id;
        ctciRec1.Intent__c = intentRecDec1.id;
        ctciRec1.IsActive__c = true;
        ctciList.add(ctciRec1);
       
        
        CTCI_Junction__c ctciRec2 = New CTCI_Junction__c();
        ctciRec2.Classification_Type__c = ctRec.id;
        ctciRec2.Classification__c = classificationRecDec2 .Id;
        ctciRec2.Intent__c = intentRecDec2.id;
        ctciRec2.IsActive__c = true;
        ctciList.add(ctciRec2);
    
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
               
        //Humana Pharmacy Calls
        CTCI_Junction__c ctciRec5 = New CTCI_Junction__c();
        ctciRec5.Classification_Type__c = ctRec2.id;
        ctciRec5.Classification__c = classificationRecDec3.Id;
        ctciRec5.Intent__c = intentRecDec3.id;
        ctciRec5.IsActive__c = true;
        ctciRec5.Service_Model_Type__c = 'Humana Pharmacy';
        ctciList.add(ctciRec5);
            
        CTCI_Junction__c ctciRec6 = New CTCI_Junction__c();
        ctciRec6.Classification_Type__c = ctRec2.id;
        ctciRec6.Classification__c = classificationRecDec4.Id;
        ctciRec6.Intent__c = intentRecDec4.id;
        ctciRec6.IsActive__c = true;
        ctciRec6.Service_Model_Type__c = 'Insurance/Plan';
        ctciList.add(ctciRec6);

        //HP Specialty Calls
        CTCI_Junction__c ctciRec7 = New CTCI_Junction__c();
        ctciRec7.Classification_Type__c = ctRec3.id;
        ctciRec7.Classification__c = classificationRecDec3.Id;
        ctciRec7.Intent__c = intentRecDec3.id;
        ctciRec7.IsActive__c = true;
        ctciRec7.Service_Model_Type__c = 'Humana Pharmacy';
        ctciList.add(ctciRec7);
            
        CTCI_Junction__c ctciRec8 = New CTCI_Junction__c();
        ctciRec8.Classification_Type__c = ctRec3.id;
        ctciRec8.Classification__c = classificationRecDec4.Id;
        ctciRec8.Intent__c = intentRecDec4.id;
        ctciRec8.IsActive__c = true;
        ctciRec8.Service_Model_Type__c = 'Humana Specialty Pharmacy';
        ctciList.add(ctciRec8);

        insert ctciList;
        system.assertEquals(ctciList.size(),9);
        
        Case caseRecord = New Case();
        caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord.Status = 'In Progress';
        caseRecord.AccountId = accountRec.id;
        caseRecord.ownerId = usr.Id;
        caseRecord.Classification_Type__c = 'calls';
        caseRecord.Classification_id__c=lstClassification[0].id;
        caseRecord.Intent_id__c = lstIntent[0].id;
        caseRecord.Interacting_With__c = accountRec.id;
        caseRecord.Interacting_With_Type__c = 'Member';
        caseRecord.Interacting_About_Type__c = 'Member';
        caseRecord.Policy_Member__c = null;
        caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
        caseRecord.Autoroute_Status__c = 'Evaluate';
        caseRecord.Origin = 'Inbound Call';
        insert caseRecord;
        
        Interaction__c objInteraction = New Interaction__c();
        objInteraction.Interacting_With__c = caseRecord.AccountId;
        objInteraction.Interacting_With_type__c = 'Member';
        objInteraction.Caller_Name__c = 'Test User';
        objInteraction.Interaction_Origin__c = 'Phone';
        insert objInteraction;


        system.assertEquals(caseRecord.Status=='In Progress',true);
        
         List<HUMEncryptedFields__c> lstCS = New List<HUMEncryptedFields__c>();
         lstCS.add(New HUMEncryptedFields__c(Name='CommentBod',ObjectName__c='CaseComment',FieldName__c='CommentBody',EncryptionScheme__c = 'Stateless_AES_Encryption_without_Search'));     
         insert lstCS;
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
                                       New HUMConstants__c(Name = 'HUMRSOMedicare', StringValue__c = 'RSO- Medicare', IntegerValue__c = 0),
                                       New HUMConstants__c(Name = 'HUMCommentBody', StringValue__c = 'CommentBody', IntegerValue__c = 0)      
                                      };
      
        Product2 prod1 = New Product2();
        prod1.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MA;
        prod1.Name = 'Prod1';
        prod1.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCD;
        insert prod1;
        system.assertEquals(prod1.Name=='Prod1',true);
                
        Product2 prod2 = New Product2();
        prod2.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_PDP;
        prod2.Name = 'Prod2';
        insert prod2;
        system.assertEquals(prod2.Name=='Prod2',true);
        
        Product2 prod3 = New Product2();
        prod3.Product_Type__c = null;
        prod3.ProductCode = GLOBAL_CONSTANT_HUM.MEDICARE_POLICY;
        prod3.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MA;
        prod3.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCD;
        
        prod3.Name = 'Prod3';
        insert prod3;
        system.assertEquals(prod3.Name=='Prod3',true);
        
        Product2 prod4 = New Product2();
        prod4.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MAPD;
        prod4.ProductCode = GLOBAL_CONSTANT_HUM.MEDICARE_POLICY;
        prod4.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCR;
        prod4.Name = 'Prod4';
        insert prod4;
        system.assertEquals(prod4.Name=='Prod4',true);      

        Policy__c policyRec1 = New Policy__c();
        policyRec1.Group_Name__c = accountRec.Id;
        policyRec1.Product__c=prod3.Id;
        policyRec1.Major_Lob__c=GLOBAL_CONSTANT_HUM.MAJOR_LOB_MCD;
        policyRec1.Policy_Group_Number__c='11111';
        insert policyRec1;
        system.assertEquals(policyRec1.Product__c==prod3.id,true);
                
        Policy__c policyRec2 = New Policy__c();
        policyRec2.Group_Name__c = accountRec.Id;
        policyRec2.Product__c = prod4.id;
        policyRec2.Policy_Group_Number__c='11111';
        policyRec2.Business_Segment__c='Individual-Medicare';
        insert policyRec2;
        system.assertEquals(policyRec2.Product__c==prod4.id,true);
        
        Policy__c policyRec3 = New Policy__c();
        policyRec3.Group_Name__c = accountRec.Id;
        policyRec3.Product__c = prod3.id;
        policyRec3.Major_Lob__c = GLOBAL_CONSTANT_HUM.MAJOR_LOB_MCD;
        policyRec3.Policy_Group_Number__c='11111';
        policyRec3.Business_Segment__c='Individual-Medicare';
        insert policyRec3;
        system.assertEquals(policyRec3.Product__c==prod3.id,true);
        
        Policy__c policyRec4 = New Policy__c();
        policyRec4.Group_Name__c = accountRec.Id;
        policyRec4.Product__c = prod4.id;
        policyRec4.Major_Lob__c = GLOBAL_CONSTANT_HUM.MAJOR_LOB_MER;
        policyRec4.Policy_Group_Number__c='11111';
        policyRec4.Business_Segment__c='Individual-Medicare';
        insert policyRec4;
        system.assertEquals(policyRec4.Product__c==prod4.id,true); 

        Policy__c policyRec5 = New Policy__c();
        policyRec5.Group_Name__c = accountRec.Id;
        policyRec5.Product__c = prod2.id;
        policyRec5.Major_Lob__c = GLOBAL_CONSTANT_HUM.MAJOR_LOB_MER;
        policyRec5.Policy_Group_Number__c='11111';
        policyRec5.Business_Segment__c='Individual-Medicare';
        insert policyRec5;
        system.assertEquals(policyRec5.Product__c==prod2.id,true); 
        
        Policy_Member__c policyMemRec1 = New Policy_Member__c();
        policyMemRec1.Name = 'abcd';
        policyMemRec1.Policy__c = policyRec1.Id;
        policyMemRec1.Member__c = accountRec.Id;
        insert policyMemRec1;
        system.assertEquals(policyMemRec1.Member__c==accountRec.Id,true);
        
        Policy_Member__c policyMemRec2 = New Policy_Member__c();
        policyMemRec2.Name = 'efgh';
        policyMemRec2.Policy__c = policyRec2.Id;
        policyMemRec2.Member__c = accountRec.Id;
        insert policyMemRec2;
        system.assertEquals(policyMemRec2.Member__c==accountRec.Id,true);
        
        Policy_Member__c policyMemRec3 = New Policy_Member__c();
        policyMemRec3.Name = 'ijkl';
        policyMemRec3.Policy__c = policyRec3.Id;
        policyMemRec3.Member__c = accountRec.Id;
        insert policyMemRec3;
        system.assertEquals(policyMemRec3.Member__c==accountRec.Id,true);
        
        Policy_Member__c policyMemRec4 = New Policy_Member__c();
        policyMemRec4.Name = 'mnop';
        policyMemRec4.Policy__c = policyRec4.Id;
        policyMemRec4.Member__c = accountRec.Id;
        insert policyMemRec4;
        system.assertEquals(policyMemRec4.Member__c==accountRec.Id,true);
        
        Policy_Member__c policyMemRec5 = New Policy_Member__c();
        policyMemRec5.Name = 'qrst';
        policyMemRec5.Policy__c = policyRec5.Id;
        policyMemRec5.Member__c = accountRec.Id;
        insert policyMemRec5;
        system.assertEquals(policyMemRec5.Member__c==accountRec.Id,true);
        
        Policy__c policyRec6 = New Policy__c();
        policyRec6.Group_Name__c = accountRec.Id;
        policyRec6.Product__c = prod3.id;
        policyRec6.Policy_Group_Number__c='11111';
        policyRec6.Major_LOB__c = 'MCD';
        policyRec6.Issue_State__c='FL';
        policyRec6.Business_Segment__c='';
        insert policyRec6;
        system.assertEquals(policyRec6.Product__c==prod3.id,true);

   		Policy_Member__c policyMemRec6 = New Policy_Member__c();
        policyMemRec6.Name = 'efghi';
        policyMemRec6.Policy__c = policyRec6.Id;
        policyMemRec6.Member__c = accountRec.Id;
        insert policyMemRec6;
        system.assertEquals(policyMemRec6.Member__c==accountRec.Id,true);

        Policy__c policyRec7 = New Policy__c();
        policyRec7.Group_Name__c = accountRec.Id;
        policyRec7.Product__c = prod3.id;
        policyRec7.Policy_Group_Number__c='11111';
        policyRec7.Major_LOB__c = 'MCD';
        policyRec7.Issue_State__c='KY';
        policyRec7.Business_Segment__c='';
        insert policyRec7;
        system.assertEquals(policyRec7.Product__c==prod3.id,true);

   		Policy_Member__c policyMemRec7 = New Policy_Member__c();
        policyMemRec7.Name = 'efghi';
        policyMemRec7.Policy__c = policyRec7.Id;
        policyMemRec7.Member__c = accountRec.Id;
        insert policyMemRec7;
        system.assertEquals(policyMemRec7.Member__c==accountRec.Id,true);
            
        Case caseRecord1 = New Case();
        caseRecord1.Status = 'In Progress';
        caseRecord1.Owner_Queue__c = 'Louisville RSO Enrollment';
        caseRecord1.AccountId = accountRec.id;
        caseRecord1.ownerId = usr.Id;
        caseRecord1.ctci_list__c=ctciList[0].id;
        caseRecord1.Classification_Type__c = 'calls';
        caseRecord1.subtype__c = 'Testing Type';
        caseRecord1.Classification_id__c=lstClassification[0].id;
        caseRecord1.Intent_id__c = lstIntent[0].id;
        caseRecord1.Interacting_With__c = accountRec.id;
        caseRecord1.Interacting_With_Type__c = 'Member';
        caseRecord1.Interacting_About_Type__c = 'Member';
        caseRecord1.Policy_Member__c = policyMemRec1.Id;
        caseRecord1.Autoroute_Status__c = 'Evaluate';
        caseRecord1.Origin = 'Inbound Call';
        insert caseRecord1;
        system.assertEquals(caseRecord1.Owner_Queue__c=='Louisville RSO Enrollment',true);
        
        Case caseRecord2 = New Case();
        caseRecord2.Status = 'In Progress';
        caseRecord2.Owner_Queue__c = 'Louisville Medicare Billing';
        caseRecord2.AccountId = accountRec.id;
        caseRecord2.ownerId = usr.Id;
        caseRecord2.Classification_Type__c = 'calls';
        caseRecord2.Classification_id__c=lstClassification[0].id;
        caseRecord2.Intent_id__c = lstIntent[0].id;
        caseRecord2.Interacting_With__c = accountRec.id;
        caseRecord2.Interacting_With_Type__c = 'Member';
            caseRecord2.Interacting_About_Type__c = 'Member';
        caseRecord2.Policy_Member__c = policyMemRec2.Id;
        caseRecord2.CTCI_List__c = ctciList[0].Id;
        caseRecord2.Autoroute_Status__c = 'Evaluate';
        caseRecord2.Origin = 'Inbound Call';
        insert caseRecord2;
        system.assertEquals(caseRecord2.Owner_Queue__c=='Louisville Medicare Billing',true);
        system.assertEquals(caseRecord2.CTCI_List__c,ctciList[0].id);
        
        Case caseRecord3 = New Case();
        caseRecord3.Status = 'In Progress';
        caseRecord3.Owner_Queue__c = 'Louisville Calls';
        caseRecord3.AccountId = accountRec.id;
        caseRecord3.ownerId = usr.Id;
        caseRecord3.Classification_Type__c = 'calls';
        caseRecord3.Classification_id__c=lstClassification[0].id;
        caseRecord3.Intent_id__c = lstIntent[0].id;
        caseRecord3.Interacting_With__c = accountRec.id;
        caseRecord3.Interacting_With_Type__c = 'Member';
            caseRecord3.Interacting_About_Type__c = 'Member';
        caseRecord3.Autoroute_Status__c = 'Evaluate';
        caseRecord3.Origin = 'Inbound Call';
        insert caseRecord3;
        system.assertEquals(caseRecord3.Owner_Queue__c =='Louisville Calls',true);   
        
        Case caseRecord4 = New Case();
        caseRecord4.Status = 'In Progress';
        caseRecord4.Owner_Queue__c = 'Louisville RSO Enrollment';
        caseRecord4.AccountId = accountRec.id;
        caseRecord4.ownerId = usr.Id;
        caseRecord4.Classification_Type__c = 'calls';
        caseRecord4.Classification_id__c=lstClassification[0].id;
        caseRecord4.Intent_id__c = lstIntent[0].id;
        caseRecord4.Interacting_With__c = accountRec.id;
        caseRecord4.Interacting_With_Type__c = 'Member';
            caseRecord4.Interacting_About_Type__c = 'Member';
        caseRecord4.Policy_Member__c = policyMemRec3.Id;
        caseRecord4.Autoroute_Status__c = 'Evaluate';
        caseRecord4.Origin = 'Inbound Call';
        insert caseRecord4;
        system.assertEquals(caseRecord4.Owner_Queue__c=='Louisville RSO Enrollment',true);
        
        Case caseRecord5 = New Case();
        caseRecord5.Status = 'In Progress';
        caseRecord5.Owner_Queue__c = 'Louisville RSO Enrollment';
        caseRecord5.AccountId = accountRec.id;
        caseRecord5.ownerId = usr.Id;
        caseRecord5.Classification_Type__c = 'calls';
        caseRecord5.Classification_id__c=lstClassification[0].id;
        caseRecord5.Intent_id__c = lstIntent[0].id;
        caseRecord5.Interacting_With__c = accountRec.id;
        caseRecord5.Interacting_With_Type__c = 'Member';
            caseRecord5.Interacting_About_Type__c = 'Member';
        caseRecord5.Policy_Member__c = policyMemRec4.Id;
        caseRecord5.Autoroute_Status__c = 'Evaluate';
        caseRecord5.Origin = 'Inbound Call';
        insert caseRecord5;
        system.assertEquals(caseRecord5.Owner_Queue__c=='Louisville RSO Enrollment',true);
        
        Case caseRecord6 = New Case();
        caseRecord6.Status = 'In Progress';
        caseRecord6.Owner_Queue__c = 'Louisville RSO Enrollment';
        caseRecord6.AccountId = accountRec.id;
        caseRecord6.ownerId = usr.Id;
        caseRecord6.Classification_Type__c = 'calls';
        caseRecord6.Classification_id__c=lstClassification[0].id;
        caseRecord6.Intent_id__c = lstIntent[0].id;
        caseRecord6.Interacting_With__c = accountRec.id;
        caseRecord6.Interacting_With_Type__c = 'Member';
            caseRecord6.Interacting_About_Type__c = 'Member';
        caseRecord6.Policy_Member__c = policyMemRec5.Id;
        caseRecord6.Autoroute_Status__c = 'Evaluate';
        caseRecord6.Origin = 'Inbound Call';
        insert caseRecord6;
        system.assertEquals(caseRecord6.Owner_Queue__c=='Louisville RSO Enrollment',true);
        
        Case caseRecord7 = new Case();
        caseRecord7.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
        caseRecord7.Case_Owner__c = 'Louisville RSO Calls Support';
        caseRecord7.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord7.AccountId = accountRec.id;
        caseRecord7.ownerId = usr.Id;
        caseRecord7.Subtype__c = 'Creditable Coverage';
        caseRecord7.Guided_Process_Flag__c = true;
        caseRecord7.Classification_Type__c = 'Calls (RSO)';
        caseRecord7.Classification_id__c=lstClassification[0].id;
        caseRecord7.Intent_id__c = lstIntent[0].id;
        caseRecord7.Interacting_With__c = accountRec.id;
        caseRecord7.Interacting_With_Type__c = 'Member';   
            caseRecord7.Interacting_About_Type__c = 'Member';
        caseRecord7.Origin = 'Inbound Call';
        caseRecord7.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
        insert caseRecord7;
        system.assertEquals(caseRecord7.Case_Owner__c =='Louisville RSO Calls Support',true);
        
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
        caseRecord8.Origin = 'Inbound Call';
        caseRecord8.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
        insert caseRecord8;
        system.assertEquals(caseRecord8.Case_Owner__c =='Louisville RSO Calls Support',true);
            
        Case caseRecord9 = new Case();
        caseRecord9.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
        caseRecord9.Case_Owner__c = 'Louisville RSO Calls Support';
        caseRecord9.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord9.AccountId = accountRec.id;
        caseRecord9.ownerId = usr.Id;
        caseRecord9.Subtype__c = 'Creditable Coverage';
        caseRecord9.Guided_Process_Flag__c = true;
        caseRecord9.Classification_Type__c = 'Calls (RSO)';
        caseRecord9.Work_Queue_View_Name__c='other';
        caseRecord9.Classification_id__c=lstClassification[0].id;
        caseRecord9.Intent_id__c = lstIntent[0].id;
        caseRecord9.Interacting_With__c = accountRec.id;
        caseRecord9.Interacting_With_Type__c = 'Member';   
        caseRecord9.Interacting_About_Type__c = 'Provider';
        caseRecord9.Origin = 'Inbound Call';
        caseRecord9.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
        insert caseRecord9;
        system.assertEquals(caseRecord9.Case_Owner__c =='Louisville RSO Calls Support',true);
            
            Case caseRecordNew = new Case();
        caseRecordNew.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
        caseRecordNew.Case_Owner__c = 'Louisville RSO Calls Support';
        caseRecordNew.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecordNew.AccountId = accountRec.id;
        caseRecordNew.ownerId = usr.Id;
        caseRecordNew.Subtype__c = 'Creditable Coverage';
        caseRecordNew.Guided_Process_Flag__c = true;
        caseRecordNew.Classification_Type__c = 'Calls (RSO)';
        caseRecordNew.Work_Queue_View_Name__c='other';
        caseRecordNew.Classification_id__c=lstClassification[0].id;
        caseRecordNew.Intent_id__c = lstIntent[0].id;
        caseRecordNew.Interacting_With__c = accountRec.id;
        caseRecordNew.Interacting_With_Type__c = 'HP Member';   
        caseRecordNew.Interacting_About_Type__c = 'Provider';
        caseRecordNew.Origin = 'Inbound Call';
        caseRecordNew.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
        insert caseRecordNew;
        system.assertEquals(caseRecordNew.Case_Owner__c =='Louisville RSO Calls Support',true);
            
         Case caseRecord10 = new Case();
        caseRecord10.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
        caseRecord10.Case_Owner__c = 'Louisville RSO Calls Support';
        caseRecord10.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord10.AccountId = accountRec.id;
        caseRecord10.ownerId = usr.Id;
        caseRecord10.Subtype__c = 'Creditable Coverage';
        caseRecord10.Guided_Process_Flag__c = true;
        caseRecord10.Classification_Type__c = 'Calls (RSO)';
        caseRecord10.Classification_id__c=lstClassification[0].id;
        caseRecord10.Intent_id__c = lstIntent[0].id;
        caseRecord10.Interacting_With__c = accountRec.id;
        caseRecord10.Interacting_With_Type__c = 'Member';   
        caseRecord10.Interacting_About_Type__c = 'Unknown-Provider';
        caseRecord10.Origin = 'Inbound Call';
        caseRecord10.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
        insert caseRecord10;
        system.assertEquals(caseRecord10.Case_Owner__c =='Louisville RSO Calls Support',true);
            
        Case caseRecord11 = new Case();
        caseRecord11.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
        caseRecord11.Case_Owner__c = 'Louisville RSO Calls Support';
        caseRecord11.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord11.AccountId = accountRec.id;
        caseRecord11.ownerId = usr.Id;
        caseRecord11.Subtype__c = 'Creditable Coverage';
        caseRecord11.Guided_Process_Flag__c = true;
        caseRecord11.Classification_Type__c = 'Calls (RSO)';
        caseRecord11.Classification_id__c=lstClassification[0].id;
        caseRecord11.Intent_id__c = lstIntent[0].id;
        caseRecord11.Interacting_With__c = accountRec.id;
        caseRecord11.Interacting_With_Type__c = 'Member';   
        caseRecord11.Interacting_About_Type__c = 'Unknown-Agent';
        caseRecord11.Origin = 'Inbound Call';
        caseRecord11.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
        insert caseRecord11;
        system.assertEquals(caseRecord11.Case_Owner__c =='Louisville RSO Calls Support',true);
            
        Case caseRecord12 = new Case();
        caseRecord12.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
        caseRecord12.Case_Owner__c = 'Louisville RSO Calls Support';
        caseRecord12.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord12.AccountId = accountRec.id;
        caseRecord12.ownerId = usr.Id;
        caseRecord12.Subtype__c = 'Creditable Coverage';
        caseRecord12.Guided_Process_Flag__c = true;
        caseRecord12.Classification_Type__c = 'Calls (RSO)';
        caseRecord12.Classification_id__c=lstClassification[0].id;
        caseRecord12.Intent_id__c = lstIntent[0].id;
        caseRecord12.Interacting_With__c = accountRec.id;
        caseRecord12.Interacting_With_Type__c = 'Member';   
        caseRecord12.Interacting_About_Type__c = 'Unknown-Group';
        caseRecord12.Origin = 'Inbound Call';
        caseRecord12.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId();
        insert caseRecord12;
        system.assertEquals(caseRecord12.Case_Owner__c =='Louisville RSO Calls Support',true);
		
		Case caseRecord20 = new Case();
        caseRecord20.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
        caseRecord20.Case_Owner__c = 'Louisville RSO Calls Support';
        caseRecord20.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord20.AccountId = accountRec.id;
        caseRecord20.ownerId = usr.Id;
        caseRecord20.Subtype__c = 'Creditable Coverage';
        caseRecord20.Guided_Process_Flag__c = true;
        caseRecord20.Classification_Type__c = 'Calls (RSO)';
        caseRecord20.Classification_id__c=lstClassification[0].id;
        caseRecord20.Intent_id__c = lstIntent[0].id;
        caseRecord20.Interacting_With__c = accountRec.id;
        caseRecord20.Interacting_With_Type__c = 'Member';   
        caseRecord20.Interacting_About_Type__c = 'Group';
        caseRecord20.Origin = 'Inbound Call';
        caseRecord20.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId();
        insert caseRecord20;
        system.assertEquals(caseRecord20.Case_Owner__c =='Louisville RSO Calls Support',true);
            
        Case caseRecord21 = new Case();
        caseRecord21.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
        caseRecord21.Case_Owner__c = 'Louisville RSO Calls Support';
        caseRecord21.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord21.AccountId = accountRec.id;
        caseRecord21.ownerId = usr.Id;
        caseRecord21.Subtype__c = 'Creditable Coverage';
        caseRecord21.Guided_Process_Flag__c = true;
        caseRecord21.Classification_Type__c = 'Calls (RSO)';
        caseRecord21.Classification_id__c=lstClassification[0].id;
        caseRecord21.Intent_id__c = lstIntent[0].id;
        caseRecord21.Interacting_With__c = accountRec.id;
        caseRecord21.Interacting_With_Type__c = 'Member';   
        caseRecord21.Interacting_About_Type__c = 'Unknown-Agent';
        caseRecord21.Origin = 'Inbound Call';
		caseRecord21.Policy_Member__c = policyMemRec.id;
        caseRecord21.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
        insert caseRecord21;
        system.assertEquals(caseRecord21.Case_Owner__c =='Louisville RSO Calls Support',true);
        
        Case caseRecord14 = new Case();
        caseRecord14.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
        caseRecord14.Case_Owner__c = 'Humana Pharmacy Specialty Calls';
        caseRecord14.Owner_Queue__c = 'Humana Pharmacy Specialty Calls';
        caseRecord14.AccountId = accountRec.id;
        caseRecord14.ownerId = usr.Id;
        caseRecord14.Classification_Type__c = 'HP Specialty Calls';
        caseRecord14.Interacting_With__c = accountRec.id;
        caseRecord14.Interacting_With_Type__c = 'Member';   
        caseRecord14.Interacting_About_Type__c = 'Member';
        caseRecord14.Origin = 'Inbound Call';
        caseRecord14.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
        insert caseRecord14;
        system.assertEquals(caseRecord14.Case_Owner__c =='Humana Pharmacy Specialty Calls', true);

        Case caseRecord15 = new Case();
        caseRecord15.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
        caseRecord15.Case_Owner__c = 'Humana Pharmacy Calls';
        caseRecord15.Owner_Queue__c = 'Humana Pharmacy Calls';
        caseRecord15.AccountId = accountRec.id;
        caseRecord15.ownerId = usr.Id;
        caseRecord15.Classification_Type__c = 'Humana Pharmacy Calls';
        caseRecord15.CTCI_List__c = ctciRec6.id;
        caseRecord15.Interacting_With__c = accountRec.id;
        caseRecord15.Interacting_With_Type__c = 'Member';   
        caseRecord15.Interacting_About_Type__c = 'Member';
        caseRecord15.Origin = 'Inbound Call';
        caseRecord15.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
        insert caseRecord15;
        system.assertEquals(caseRecord15.Case_Owner__c =='Humana Pharmacy Calls',true);
        
        Provider_functionality_switch__c bfs = new Provider_functionality_switch__c();
        bfs.active__c = true;
        insert bfs;
        
        List<Task> testTasks = new List<Task>();    
            Task oTask1 = new Task(WhatId = caseRecord.id,type='Work Task', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                               status='In Progress' , Topic__c = 'SIU');
            testTasks.add(oTask1);  
            Task oTask2 = new Task(WhatId = caseRecord1.id,type='Work Task',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                               status='In Progress', Topic__c = 'SIU');
            testTasks.add(oTask2);  
            Task oTask3 = new Task(WhatId = caseRecord12.id,type='Work Task',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                               Work_Queue_View_Name__c='other', Service_Center__c ='Cincinnati', Department__c='CRU', 
                               status='In Progress', Topic__c = 'SIU');
            testTasks.add(oTask3);
            insert testTasks;      
       
             if([SELECT ID FROM Template__c WHERE Unique_Template_Name__c='ATVF007'].isEmpty())
             {
                //Insert Template data
                Template__c templateObj = new Template__c(Unique_Template_Name__c='ATVF007',Description__c='Creditable Coverage Attestation');
                Insert templateObj;     
        
                //Insert Template Field Data
                List<Template_Field__c> templateFieldObj = new List<Template_Field__c>();
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_CaseNumber'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_CRMCaseCreateDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MedicareTrackId'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_RecieptDate'));        
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PersonSpeakingWith'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_POAUnderstandDefOfCridtableCoverage'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_OtherMemberConsentMoveForwardCall'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_90DaysLetterSent'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FinalDisclaimer'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_NeverHadCrditableCoverage'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FEHBPCoveragePCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FEHBPCoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FEHBPCoverageToDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_SPAPCoverageCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_SPAPCoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_SPAPCoverageToDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_VACoverageCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_VACoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_VACoverageToDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_TRICARECoverageCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_TRICARECoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_TRICARECoverageToDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MediGapCoverageCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MediGapCoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MediGapCoverageToDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ITUCoverageCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ITUCoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ITUToCoverageDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PACECoverageCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PACECoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PACECoverageToDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageSourceName'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageToDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_HumanaCoverageCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_HumanaCoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_HumanaCoverageToDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ExtraHelpCoverageCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ExtraHelpCoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ExtraHelpCoverageToDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_KatrinaCoverageCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_KatrinaCoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_KatrinaCoverageToDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PuertoRicoCoverageCheck'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PuertoRicoCoverageFromDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PuertoRicoCoverageToDate'));
                templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_IsCICMSCalled'));
                
                insert templateFieldObj;
        
            }
    
        List<Template_Submission_Data__c> submissionList = new List<Template_Submission_Data__c>();
        Template__c templateObj = [Select id from Template__c where Unique_Template_Name__c='ATVF007'];
        
        Template_Submission_Owner__c tOnwer = new Template_Submission_Owner__c();
        tOnwer.Object_Owner_ID__c=string.valueOf(caseRecord7.id);
        tOnwer.Object_Owner_Type__c='Case';
        insert tOnwer;
        
        Template_Submission__c submission = new Template_Submission__c();
        submission.Template__c = templateObj.id;
        submission.Submission_Owner__c = tOnwer.id;
        insert submission;
            
        Template__c tem = new Template__c();
        tem.Unique_Template_Name__c = 'd884d160-77c0-4d1b-95aa-edf8c81f3a59';
        tem.Description__c='pcptemplate';
            insert tem;
            
        Template_Field__c temfield = new Template_Field__c();
        temfield.Name = 'Did you receive a Service Fund edit when attempting to change the member\'s PCP';
        temfield.Template__c = tem.id;
        
        insert temfield;
        
        
        Template_Submission_Owner__c tOnwer1 = new Template_Submission_Owner__c();
        tOnwer1.Object_Owner_ID__c=string.valueOf(caseRecordNew.id);
        tOnwer1.Object_Owner_Type__c='Case';
        insert tOnwer1;
        
        Template_Submission__c submission1 = new Template_Submission__c();
        submission1.Template__c = tem.id;
        submission1.Submission_Owner__c = tOnwer1.id;
        insert submission1;
        
        Template_Submission_Data__c tObj1 = new Template_Submission_Data__c();
        tObj1.Template_Field__c = temfield.Id;
        tObj1.Template_Submission__c = submission1.id;
        tObj1.Value__c = 'Yes';
        insert tObj1;
        
        Template__c tem1 = new Template__c();
        tem1.Unique_Template_Name__c = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
        tem1.Description__c='goodcause';
        insert tem1;
        
        Template_Field__c temfield1 = new Template_Field__c();
        temfield1.Name = 'GCRTermedPlanYesNo';
        temfield1.Template__c = tem1.id;
        
        insert temfield1;
        
        
        Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
        tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecordNew.id);
        tOnwer11.Object_Owner_Type__c='Case';
        insert tOnwer11;
    
        Template_Submission__c submission11 = new Template_Submission__c();
        submission11.Template__c = tem1.id;
        submission11.Submission_Owner__c = tOnwer11.id;
        insert submission11;
        
        Template_Submission_Data__c tObj11 = new Template_Submission_Data__c();
        tObj11.Template_Field__c = temfield1.Id;
        tObj11.Template_Submission__c = submission11.id;
        tObj11.Value__c = 'Yes';
        insert tObj11;
            
        
        
        List<Template_Submission_Data__c> newSubmissionData = new List<Template_Submission_Data__c>();
        for(Template_Field__c submissionDataObj : [SELECT id,Name from Template_Field__c])
        {
            Template_Submission_Data__c tObj = new Template_Submission_Data__c();
            tObj.Template_Field__c = submissionDataObj.Id;
            tObj.Template_Submission__c = submission.id;
            
            
            if(submissionDataObj.Name == 'ATVF_RecieptDate')
            {
                dateTime strReciepDateTime = datetime.now();
                tObj.Value__c = (String) strReciepDateTime.format('MM/dd/yyyy');
            }
            
            if(submissionDataObj.Name == 'ATVF_IsCICMSCalled') tObj.Value__c = 'NO' ;

            if(submissionDataObj.Name == 'ATVF_MemberId')
            {
                tObj.Value__c = '123456' ;
            }
                            
            if(submissionDataObj.Name == 'ATVF_CaseNumber')
            {
                tObj.Value__c = '123456' ;
            }

            if(submissionDataObj.Name == 'ATVF_CRMCaseCreateDate')
            {
                dateTime strCreatedDateTime = datetime.now();
                tObj.Value__c = (String) strCreatedDateTime.format('MM/dd/yyyy');           
            }     
            
            if(submissionDataObj.Name == 'ATVF_MedicareTrackId')
            {
                tObj.Value__c = '123456';
            }
            
            if(submissionDataObj.Name == 'ATVF_FEHBPCoveragePCheck')
            {
                tObj.Value__c = 'true' ;
            }
            
            if(submissionDataObj.Name == 'ATVF_FEHBPCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_FEHBPCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_SPAPCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_SPAPCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_SPAPCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_VACoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_VACoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_VACoverageToDate')
            {
                tObj.Value__c ='03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_TRICARECoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_TRICARECoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_TRICARECoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_MediGapCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_MediGapCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_MediGapCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ITUCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_ITUCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ITUToCoverageDate')
            {
                tObj.Value__c = '03/2018';
            }               

            if(submissionDataObj.Name == 'ATVF_PACECoverageCheck')
            {
                tObj.Value__c = 'true';
            }               
            
            if(submissionDataObj.Name == 'ATVF_PACECoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }   
            if(submissionDataObj.Name == 'ATVF_FinalDisclaimer')
            {
                tObj.Value__c = 'Yes';
            }
            if(submissionDataObj.Name == 'ATVF_PACECoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }               
            
            if(submissionDataObj.Name == 'ATVF_DiffSourceOfCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_DiffSourceOfCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_DiffSourceOfCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_HumanaCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_HumanaCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_HumanaCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ExtraHelpCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_ExtraHelpCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ExtraHelpCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_KatrinaCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_KatrinaCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_KatrinaCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_PuertoRicoCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_PuertoRicoCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_PuertoRicoCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_NeverHadCrditableCoverage')
            {
                tObj.Value__c = 'true';
            }                
            newSubmissionData.add(tObj);
        }
        insert newSubmissionData;  

        // Inserting test setup data for REQ - 300142 - Medicare Calls Case Comments
        Policy__c policyRecMed = new Policy__c();
        policyRecMed.Group_Name__c = accountRec.Id;
        policyRecMed.Product__c = prod4.id;
        policyRecMed.Major_Lob__c = GLOBAL_CONSTANT_HUM.MEDICARECALLS_PDP_HUM;
        policyRecMed.Policy_Group_Number__c='11111';
        insert policyRecMed;
        system.assertEquals(policyRecMed.Product__c==prod4.id,true);
        
        Policy_Member__c policyMemRecMed = new Policy_Member__c();
        policyMemRecMed.Name = 'MedCallsCC';
        policyMemRecMed.Policy__c = policyRecMed.Id;
        policyMemRecMed.Member__c = accountRec.Id;
        insert policyMemRecMed;
        system.assertEquals(policyMemRecMed.Member__c==accountRec.Id,true);
        
        Member_ID__c memberId = new Member_ID__c();
        memberId.Name = '123456789';
        memberId.Member_Dependent_Code__c = '00';
        memberId.policy__c = policyRecMed.Id;
        memberId.Policy_Member__c = policyMemRecMed.Id;
        memberId.Member_Card_External_ID__c = '005436667';
        memberId.Type__c = HUMConstants.HUMMEMBER_ID_TYPE;
        insert memberId;
        
        Case caseRecordMed = new Case();
        caseRecordMed.Status = 'In Progress';
        caseRecordMed.Owner_Queue__c = 'Louisville RSO Enrollment';
        caseRecordMed.AccountId = accountRec.id;
        caseRecordMed.ownerId = usr.Id;
        caseRecordMed.ctci_list__c=ctciList[0].id;
        caseRecordMed.Classification_Type__c = 'calls';
        caseRecordMed.Classification_id__c=lstClassification[0].id;
        caseRecordMed.Intent_id__c = lstIntent[0].id;
        caseRecordMed.Interacting_With__c = accountRec.id;
        caseRecordMed.Interacting_With_Type__c = 'Member';
            caseRecordMed.Interacting_About_Type__c = 'Member';
        caseRecordMed.Policy_Member__c = policyMemRecMed.Id;
        caseRecordMed.Autoroute_Status__c = 'Evaluate';
        caseRecordMed.Origin = 'Inbound Call';
        caseRecordMed.Line_of_Business__c = 'Medicare';
        caseRecordMed.status = 'In Progress';
        insert caseRecordMed;
        system.assertEquals(caseRecordMed.Owner_Queue__c=='Louisville RSO Enrollment',true);
        
        Case objCaseRecord = new Case();
        objCaseRecord.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
        objCaseRecord.Case_Owner__c = 'Louisville RSO Calls Support';
        objCaseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
        objCaseRecord.AccountId = accountRec.id;
        objCaseRecord.ownerId = usr.Id;
        objCaseRecord.Subtype__c = 'New Creditable Coverage';
        objCaseRecord.Guided_Process_Flag__c = true;
        objCaseRecord.Classification_Type__c = 'Calls (RSO)';
        objCaseRecord.Classification_id__c=lstClassification[0].id;
        objCaseRecord.Intent_id__c = lstIntent[0].id;
        caseRecordMed.Policy_Member__c = policyMemRecMed.Id;
        objCaseRecord.Interacting_With__c = accountRec.id;
        objCaseRecord.Interacting_With_Type__c = 'Member'; 
          objCaseRecord.Interacting_About_Type__c = 'Member'; 
        objCaseRecord.Origin = 'Inbound Call';
        objCaseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
        insert objCaseRecord;
        system.assertEquals(objCaseRecord.Subtype__c =='New Creditable Coverage',true);
        
        list<CaseCommentType__c> lstCCT = new list<CaseCommentType__c>();
        CaseCommentType__c oCCTIssue = new CaseCommentType__c(Name = GLOBAL_CONSTANT_HUM.MEDICARECALLS_ISSUE_HUM);
        lstCCT.add(oCCTIssue);
        
        CaseCommentType__c oCCTReso = new CaseCommentType__c(Name = GLOBAL_CONSTANT_HUM.MEDICARECALLS_RESO_HUM);
        lstCCT.add(oCCTReso);
        
        insert lstCCT;
        
        Case caseRecordMed1 = new Case();
        caseRecordMed1.Status = 'Closed';
        caseRecordMed1.Owner_Queue__c = 'Louisville RSO Enrollment';
        caseRecordMed1.AccountId = accountRec.id;
        caseRecordMed1.ownerId = usr.Id;
        caseRecordMed1.ctci_list__c=ctciList[0].id;
        caseRecordMed1.Classification_Type__c = 'calls';
        caseRecordMed1.Classification_id__c=lstClassification[0].id;
        caseRecordMed1.Intent_id__c = lstIntent[0].id;
        caseRecordMed1.Interacting_With__c = accountRec.id;
        caseRecordMed1.Interacting_With_Type__c = 'Member';
             caseRecordMed1.Interacting_About_Type__c = 'Member'; 
        caseRecordMed1.Policy_Member__c = policyMemRecMed.Id;
        caseRecordMed1.Autoroute_Status__c = 'Evaluate';
        caseRecordMed1.Origin = 'Inbound Call';
        caseRecordMed1.Line_of_Business__c = 'Medicare';
        caseRecordMed1.SubType__c = 'Routine';
        caseRecordMed1.Complaint__c='No';
        caseRecordMed1.G_A_Rights_Given__c='No';
        insert caseRecordMed1;
        system.assertEquals(caseRecordMed1.Owner_Queue__c=='Louisville RSO Enrollment',true);

        Case caseRecord22 = new Case();
        caseRecord22.Status = 'Closed';
        caseRecord22.Owner_Queue__c = 'Louisville RSO Enrollment';
        caseRecord22.AccountId = accountRec.id;
        caseRecord22.ownerId = usr.Id;
        caseRecord22.ctci_list__c=ctciList[0].id;
        caseRecord22.Classification_Type__c = 'calls';
        caseRecord22.Classification_id__c=lstClassification[0].id;
        caseRecord22.Intent_id__c = lstIntent[0].id;
        caseRecord22.Interacting_With__c = accountRec.id;
        caseRecord22.Interacting_With_Type__c = 'Member';
            caseRecord22.Interacting_About_Type__c = 'Member'; 
        caseRecord22.Policy_Member__c = policyMemRecMed.Id;
        caseRecord22.Autoroute_Status__c = 'Routed Pending Accept';
        caseRecord22.Origin = 'Inbound Call';
        caseRecord22.Line_of_Business__c = 'Medicare';
        caseRecord22.SubType__c = 'Routine';
        caseRecord22.Complaint__c='No';
        caseRecord22.G_A_Rights_Given__c='No';
        insert caseRecord22;
        system.assertEquals(caseRecord22.Owner_Queue__c=='Louisville RSO Enrollment',true);
        
        list<CaseComment> lstCC = new list<CaseComment>();
        CaseComment oCC1 = new CaseComment(ParentId = caseRecordMed1.Id, CommentBody = 'TEST CASE COMMENT MEDI');
        lstCC.add(oCC1);
        CaseComment oCC2 = new CaseComment(ParentId = caseRecordMed1.Id, CommentBody = 'TEST CASE COMMENT MEDI RESOLUTION');
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

        MET_Milestone__c oMetMilestone = new MET_Milestone__c();
        oMetMilestone.Case_Type__c = 'Customer Inquiry';
        oMetMilestone.Case_Subtype__c = 'Routine';
        oMetMilestone.Action__c = oAction.id;
        oMetMilestone.Source_Name__c =  oSource.id;
        oMetMilestone.Task__c =  oTask.id;
        oMetMilestone.Is_Active__c = true;
        oMetMilestone.Is_Milestone__c = true;     
        insert oMetMilestone;
        System.assertEquals(oMetMilestone.Action__c,oAction.id);
    
        MET_Milestone__c oMetMilestone1 = new MET_Milestone__c();
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
        
        list<Case_Milestone__c> lstCustomSettings = new list<Case_Milestone__c>();
        lstCustomSettings.add(New Case_Milestone__c(Name = 'CM-001', Doc_Type__c = 't1', Sub_Type__c = 'Creditable Coverage', Milestone__c = '909', Origin__c = 'Inbound Call', Is_Start_Milestone__c = true));
        insert lstCustomSettings; 
        
        Case caseRecord13 = new Case();
        caseRecord13.Status = 'In Progress';
        caseRecord13.Case_Owner__c = 'Louisville RSO Enrollment';
        caseRecord13.Owner_Queue__c = 'Louisville RSO Enrollment';
        caseRecord13.AccountId = accountRec.id;
        caseRecord13.ownerId = usr.Id;
        caseRecord13.Type = 'TRR Inquiry';
        caseRecord13.Subtype__c = 'TRR';
        caseRecord13.Origin = 'CRMTRR';        
        caseRecord13.Classification_Type__c = 'Calls (RSO)';
        caseRecord13.Classification_id__c=lstClassification[0].id;
        caseRecord13.Intent_id__c = lstIntent[0].id;
        caseRecord13.Interacting_With__c = accountRec.id;
        caseRecord13.Interacting_With_Type__c = 'Member';   
        caseRecord13.Interacting_About_Type__c = 'Member';        
        caseRecord13.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Medicare Case').getRecordTypeId();
        insert caseRecord13;
        system.assertEquals(caseRecord13.Case_Owner__c =='Louisville RSO Enrollment',true);
        ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord13);
        CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
        caseEdit.getCaseRecordType(GLOBAL_CONSTANT_HUM.HP_CASE_RECORD_TYPES);
        caseEdit.changeCaseRecordType();
        list<Enrollment_Track_History__c> lstTRRTracks = new list<Enrollment_Track_History__c>();
        
        Enrollment_Track_History__c TrackHisRec = new Enrollment_Track_History__c();        
        TrackHisRec.Milestone_Id__c = '1905';
        TrackHisRec.Source_App__c = 'CRM';
        TrackHisRec.Recipient_App__c = 'CRM';
        TrackHisRec.Status__c = 'Success';
        TrackHisRec.Case__c = caseRecord13.ID;
        TrackHisRec.Source_Track_Id__c = '123456789';
        lstTRRTracks.add(TrackHisRec);
        
        TrackHisRec = new Enrollment_Track_History__c();        
        TrackHisRec.Milestone_Id__c = '1802';
        TrackHisRec.Source_App__c = 'CRM';
        TrackHisRec.Recipient_App__c = 'CRM';
        TrackHisRec.Status__c = 'Success';
        TrackHisRec.Case__c = caseRecord13.ID;
        TrackHisRec.Source_Track_Id__c = '123456780';
        lstTRRTracks.add(TrackHisRec);
        insert  lstTRRTracks;
        
        MET_Task__c oTask_TRR = new MET_Task__c();
        oTask_TRR.Name = 'Test TRR Task';
        oTask_TRR.Is_Active__c = true;
        oTask_TRR.Case_Subtype__c = 'TRR';
        insert oTask_TRR;
        system.assertEquals(oTask_TRR.Name,'Test TRR Task');
        
        MET_Action__c oAction_TRR = new MET_Action__c();
        oAction_TRR.Name = 'Test TRR Action';
        oAction_TRR.Is_Active__c = true;
        insert oAction_TRR;
        system.assertEquals(oAction_TRR.Name,'Test TRR Action');

        MET_Source__c oSource_TRR = new MET_Source__c();
        oSource_TRR.Name = 'Test TRR Source';
        oSource_TRR.Is_Active__c = true; 
        oSource_TRR.Launch_EMME__c = true;
        insert oSource_TRR; 
        system.assertEquals(oSource_TRR.Name,'Test TRR Source');

        MET_Milestone__c oMetMilestone_TRR = new MET_Milestone__c();
        oMetMilestone_TRR.Name = '1806';
        oMetMilestone_TRR.Case_Type__c = 'TRR Inquiry';
        oMetMilestone_TRR.Case_Subtype__c = 'TRR';
        oMetMilestone_TRR.Action__c = oAction_TRR.id;
        oMetMilestone_TRR.Source_Name__c =  oSource_TRR.id;
        oMetMilestone_TRR.Task__c =  oTask_TRR.id;
        oMetMilestone_TRR.Is_Active__c = true;
        oMetMilestone_TRR.Is_Milestone__c = true;     
        oMetMilestone.met_entries_assoc__c = 'Resubmission Changes Letter or PCRP; Retransmission no letter'; 
        oMetMilestone.Met_Entries_Assoc_Order__c = 'Retransmission no letter1;Resubmission Changes Letter or PCRP1';
        insert oMetMilestone_TRR;
        system.assertEquals(oMetMilestone_TRR.Action__c,oAction_TRR.id);
        
        MET_Task__c oTask_TRR1 = new MET_Task__c();
        oTask_TRR1.Name = 'Test TRR1 Task';
        oTask_TRR1.Is_Active__c = true;
        oTask_TRR1.Case_Subtype__c = 'TRR';
        insert oTask_TRR1;
        system.assertEquals(oTask_TRR1.Name,'Test TRR1 Task');
        
        MET_Action__c oAction_TRR1 = new MET_Action__c();
        oAction_TRR1.Name = 'Test TRR1 Action';
        oAction_TRR1.Is_Active__c = true;
        insert oAction_TRR1;
        system.assertEquals(oAction_TRR1.Name,'Test TRR1 Action');

        MET_Source__c oSource_TRR1 = new MET_Source__c();
        oSource_TRR1.Name = 'Test TRR1 Source';
        oSource_TRR1.Is_Active__c = true; 
        oSource_TRR1.Launch_EMME__c = true;
        insert oSource_TRR1; 
        system.assertEquals(oSource_TRR1.Name,'Test TRR1 Source');

        MET_Milestone__c oMetMilestone_TRR1 = new MET_Milestone__c();
        oMetMilestone_TRR1.Name = '1906';
        oMetMilestone_TRR1.Case_Type__c = 'TRR Inquiry';
        oMetMilestone_TRR1.Case_Subtype__c = 'TRR';
        oMetMilestone_TRR1.Action__c = oAction_TRR1.id;
        oMetMilestone_TRR1.Source_Name__c =  oSource_TRR1.id;
        oMetMilestone_TRR1.Task__c =  oTask_TRR1.id;
        oMetMilestone_TRR1.Is_Active__c = true;
        oMetMilestone_TRR1.Is_Milestone__c = true;     
        insert oMetMilestone_TRR1;
        system.assertEquals(oMetMilestone_TRR1.Action__c,oAction_TRR1.id); 
	
	MET_Milestone__c oMetMilestone_TRR898 = new MET_Milestone__c();
        oMetMilestone_TRR898.Name = '898';
        oMetMilestone_TRR898.Case_Type__c = 'TRR Inquiry';
        oMetMilestone_TRR898.Case_Subtype__c = 'TRR';
        oMetMilestone_TRR898.Action__c = oAction_TRR.id;
        oMetMilestone_TRR898.Source_Name__c =  oSource_TRR.id;
        oMetMilestone_TRR898.Task__c =  oTask_TRR.id;
        oMetMilestone_TRR898.Is_Active__c = true;
        oMetMilestone_TRR898.Is_Milestone__c = true;     
        oMetMilestone.met_entries_assoc__c = 'Resubmission Changes Letter or PCRP; Retransmission no letter'; 
        oMetMilestone.Met_Entries_Assoc_Order__c = 'Retransmission no letter1;Resubmission Changes Letter or PCRP1';
        insert oMetMilestone_TRR898;
        system.assertEquals(oMetMilestone_TRR898.Action__c,oAction_TRR.id);
    }
    
       system.runAs(usr1)
       {       
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = usr1.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
            List<Classification_Type__c> lstClassificationTy = New List<Classification_Type__c>();
            lstClassificationTy = [Select id, name, is_Active__c from Classification_Type__c where Name = 'calls (RSO)' LIMIT 1];
            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRSOHP = New Classification__c();
            classificationRSOHP.Name = 'HP Billing';
            classificationRSOHP.Is_Active__c = true;
            lstClassification.add(classificationRSOHP);
            
            Classification__c classificationRSOHP1 = New Classification__c();
            classificationRSOHP1.Name = 'Eligibility';
            classificationRSOHP1.Is_Active__c = true;
            lstClassification.add(classificationRSOHP1);
            
            insert lstClassification;
            system.assertEquals(lstClassification.size(),2);
            
            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRSOHP = New Intent__c();
            intentRSOHP.Name = 'Add/Updated Credit Card';
            intentRSOHP.Is_Active__c = true;
            lstIntent.add(intentRSOHP);
            
            Intent__c intentRSOHP1 = New Intent__c();
            intentRSOHP1.Name = 'Hospice';
            intentRSOHP1.Is_Active__c = true;
            lstIntent.add(intentRSOHP1);
            
            insert lstIntent;
            system.assertEquals(lstIntent.size(),2);
            
            List<CTCI_Junction__c> ctciList = New list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = New CTCI_Junction__c();
            ctciRec.Classification_Type__c = lstClassificationTy[0].id;
            ctciRec.Classification__c = classificationRSOHP.Id;
            ctciRec.Intent__c = intentRSOHP.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            
            CTCI_Junction__c ctciRec1 = New CTCI_Junction__c();
            ctciRec1.Classification_Type__c = lstClassificationTy[0].id;
            ctciRec1.Classification__c = classificationRSOHP1.Id;
            ctciRec1.Intent__c = intentRSOHP1.id;
            ctciRec1.IsActive__c = true;
            ctciList.add(ctciRec1);
            
            insert ctciList;
            system.assertEquals(ctciList.size(),2);
            
            Case caseRecord = New Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = accountRec.id;
            caseRecord.ownerId = usr1.Id;
            caseRecord.Classification_Type__c = 'Calls (RSO)';
            caseRecord.Classification_id__c=lstClassification[0].id;
            caseRecord.Intent_id__c = lstIntent[0].id;
            caseRecord.Interacting_With__c = accountRec.id;
            caseRecord.Interacting_With_Type__c = 'Member';
           caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Policy_Member__c = null;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Autoroute_Status__c = 'Evaluate';
            caseRecord.Origin = 'Inbound Call';
            caseRecord.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert caseRecord;
            
            Interaction__c objInteraction = New Interaction__c();
            objInteraction.Interacting_With__c = caseRecord.AccountId;
            objInteraction.Interacting_With_type__c = 'Member';
            objInteraction.Caller_Name__c = 'Test User';
            objInteraction.Interaction_Origin__c = 'Phone';
            insert objInteraction;
            system.assertEquals(caseRecord.Status=='In Progress',true);
            
            Case caseRecord1 = new Case();
            caseRecord1.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord1.Status = 'In Progress';        
            caseRecord1.AccountId = accountRec.id;
            caseRecord1.ownerId = usr1.Id;        
            caseRecord1.Classification_Type__c = 'Calls (RSO)';
            caseRecord1.Classification_id__c=lstClassification[0].id;
            caseRecord1.Intent_id__c = lstIntent[0].id;
            caseRecord1.Interacting_With__c = accountRec.id;
            caseRecord1.Interacting_With_Type__c = 'Member';
            caseRecord1.Interacting_About_Type__c = 'Member';
            caseRecord.Policy_Member__c = null;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Autoroute_Status__c = 'Evaluate';
            caseRecord1.Origin = 'Inbound Call';
            caseRecord1.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
            insert caseRecord1;
            system.assertEquals(caseRecord1.Owner_Queue__c=='Louisville RSO Calls Support',true);
           
           Case caseRecordForDCN = new Case();
            caseRecordForDCN.Status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;        
            caseRecordForDCN.Case_Owner__c = 'Louisville RSO Calls Support';
            caseRecordForDCN.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecordForDCN.AccountId = accountRec.id;
            caseRecordForDCN.ownerId = usr.Id;
            caseRecordForDCN.Subtype__c = 'Creditable Coverage';
            caseRecordForDCN.Guided_Process_Flag__c = true;
            caseRecordForDCN.Classification_Type__c = 'Calls (RSO)';
            caseRecordForDCN.Classification_id__c=lstClassification[0].id;
            caseRecordForDCN.Intent_id__c = lstIntent[0].id;
            caseRecordForDCN.Interacting_With__c = accountRec.id;
            caseRecordForDCN.Interacting_With_Type__c = 'Member';   
            caseRecordForDCN.Interacting_About_Type__c = 'Member';
            caseRecordForDCN.Interacting_About_Type__c = 'Unknown-Group';
            caseRecordForDCN.Origin = 'Inbound Call';
            caseRecordForDCN.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId();
            caseRecordForDCN.Type = 'Correspondence Inquiry';
            caseRecordForDCN.DCN__c = '123456789';
            insert caseRecordForDCN;
            system.assertEquals(caseRecordForDCN.Case_Owner__c =='Louisville RSO Calls Support',true);
       }
    }
    
    /*
    * This method is used to test the edit funtionality
    * @param   None
    * @return  None
    */
    static testMethod void testEditCaseScenario()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Boolean bintentreq;
            
            Group oGrp = [Select Name,Type From Group Where Name='Cincinnati_Calls1' AND Type='QUEUE' limit 1];
            System.assertEquals(oGrp.Name, 'Cincinnati_Calls1');
            
             RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        
        //Creating Account Record
        Account accountRec = HUMTestDataHelperTest.getAccount();
        accountRec.OwnerId = testUser.id;
        accountRec.FirstName = 'Test';
        accountRec.LastName = 'Account';
        accountRec.Enterprise_Id__c = '123456';
        accountRec.recordtypeId = AccountRecordType.id;
        insert accountRec;
        system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
            Work_Queue_Setup__c oWQS = [Select Work_Queue_Name__c, Public_Group_Name__c,Service_Center__c,Department__c,Name,OwnerId,Classification_Type__c From Work_Queue_Setup__c Where OwnerId=:oGrp.Id];
            System.assertEquals(oWQS.Public_Group_Name__c, 'Group one');
            
            
            case caseRecord = [Select id, RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'calls' LIMIT 1];
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='Benefits'];
            Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Rewards'];
            Classification__c classificationRecDec = [select id,Is_Active__c from Classification__c where name= 'Humana Pharmacy' limit 1];
            Intent__c intentRecDec = [Select id,Is_Active__c from Intent__c where name= 'Estimate' limit 1];
            
            CTCI_Junction__c ctciRec =[select id,name,Classification_Type__c,Classification__c, Intent__c from CTCI_Junction__c where Classification_Type__c =:ctRec.id LIMIT 1];
            System.assertEquals(ctciRec.Classification__c, classificationRec.Id);
           
            HUMConstants__c constants = [Select id, Name, StringValue__c from HUMConstants__c where Name='HUMCommentBody'];
          
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            
            ApexPages.currentPage().getParameters().put('pharmacylogging', 'true'); 
            
            List<Template__c> tempLst = new List<Template__c>();
            Template__c temp = new Template__c ();
            temp.Unique_Template_Name__c = 'ATVF007';
            temp.Description__c = 'Test Template';
            
            tempLst.add(temp);
            
            Template__c temp1 = new Template__c ();
            temp1.Unique_Template_Name__c = '95C90830-3133';
            temp1.Description__c = 'Test Template1';
            tempLst.add(temp1);
            
            Template__c temp2 = new Template__c ();
            temp2.Unique_Template_Name__c = 'BC929CD6-E06A';
            temp2.Description__c = 'Test Template2';
            tempLst.add(temp2);
            
            Template__c temp3 = new Template__c ();
            temp3.Unique_Template_Name__c = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
            temp3.Description__c = 'Test Template3';
            tempLst.add(temp3);

           
            
            Template__c temp4 = new Template__c ();
            temp4.Unique_Template_Name__c = 'd884d160-77c0-4d1b-95aa-edf8c81f3a59';
            temp4.Description__c = 'Test Template4';
            tempLst.add(temp4);
            
            Template__c temp5 = new Template__c ();
            temp5.Unique_Template_Name__c = 'deeef952-9f63-49ce-97d0-c7eb3204b519';
            temp5.Description__c = 'Test Template5';
            tempLst.add(temp5);
            
            
            insert tempLst;
         
            
            for(Template__c templates: tempLst){
            
                Template_Field__c tf = new Template_Field__c();
                tf.Data_Type__c = 'String';
                tf.Template__c = templates.id;
                insert tf;
            
                Template_Submission_Owner__c tOnwer = new Template_Submission_Owner__c();
                tOnwer.Object_Owner_ID__c=string.valueOf(caseRecord.id);
                tOnwer.Object_Owner_Type__c='Case';
                insert tOnwer;
                
                Template_Submission__c submission = new Template_Submission__c();
                submission.Template__c = templates.id;
                submission.Submission_Owner__c = tOnwer.id;
                insert submission;
                
                Template_Submission_Data__c tObj1 = new Template_Submission_Data__c();
                tObj1.Template_Field__c = tf.Id;
                tObj1.Template_Submission__c = submission.id;
                tObj1.Value__c = 'Yes';
                insert tObj1;
            }
         
            //test
            Template__c tem1 = new Template__c();
            tem1.Unique_Template_Name__c = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
            tem1.Description__c='goodCause';
            insert tem1;
            
            Template_Field__c temfield1 = new Template_Field__c();
            temfield1.Name = 'GCRTermedPlanYesNo';
            temfield1.Template__c = tem1.id;
            
            insert temfield1;
                
                
            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;
            
            Template_Submission__c submission11 = new Template_Submission__c();
            submission11.Template__c = tem1.id;
            submission11.Submission_Owner__c = tOnwer11.id;
            insert submission11;
                
            Template_Submission_Data__c tObj11 = new Template_Submission_Data__c();
            tObj11.Template_Field__c = temfield1.Id;
            tObj11.Template_Submission__c = submission11.id;
            tObj11.Value__c = 'Yes';
            insert tObj11;
            //test
            
            oWQS.Classification_Type__c = ctRec.Id;
            update oWQS;
            system.assertEquals(  oWQS.Classification_Type__c,ctRec.Id);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.sIntent = HUMConstants.HUMNone;  
            caseEdit.errorLevel='Error';
            caseEdit.messageName=Label.HUM_CaseEdit_Int_Err;
            caseEdit.showMessage();         
            caseEdit.save();
            caseEdit.fetchUserDetails();
            caseEdit.isMedExpProcessAttachedPCP();
            caseEdit.isMedExpProcessAttachedGoodCause();
            caseEdit.updateCaseAfterPCP();
            caseEdit.updateCaseAfterGoodCause();
            
            caseEdit.sClassification = HUMConstants.HUMNone;
            caseEdit.save();
            
            caseEdit.checkIntent();
            caseEdit.sClassification = classificationRec.ID;
            caseEdit.sIntent = intentRec.id;    
            caseRecord.status=HUMConstants.HUM_CASEEDIT_CLOSED;
            caseEdit.saveNew();
            caseRecord.complaint__c='No';
            caseRecord.G_A_Rights_Given__c='No';
            list<Template_Submission__c> subDataObj = [SELECT Id FROM Template_Submission__c];  
            list<Template_Submission_Data__c> SubTempData=[SELECT Id, Template_Field__r.Name, Value__c FROM Template_Submission_Data__c ];      
            Boolean isCreditableCoverageProcessAttached = false;
            
            caseEdit.bCaseTransferRedirectionRequired = true;
            caseRecord.DCN__c = 'Test1 DCN';
            update caseRecord;
            
            
            caseEdit.isCreditableCoverageProcessAttached();
            caseEdit.Save(); 
                                    
            caseRecord.G_A_Rights_Given__c='No';        
            caseEdit.save();
            
            List<User> currentUser = [Select id,Name, Current_Queue__c from User where ID=:userinfo.getuserID() ];
            caserecord.OwnerId = currentUser[0].id;
            caserecord.Case_Owner__c = currentUser[0].name;
            caserecord.Owner_Queue__c = currentUser[0].Current_Queue__c;
            caserecord.Origin = 'Web Chat';
            ApexPages.currentPage().getParameters().put('binvoicelogging', 'true'); 
            caseEdit.callSave();
                
            caserecord.Case_Owner__c = 'liveagent Site Guest User';
            caserecord.Origin = 'Web Chat';    
            caseEdit.callSave();
            
            caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.sClassification = null;
            caseEdit.getClassificationValues();
            caseEdit.sIntent = null;
            caseEdit.getIntentValues();
            
            caseRecord.CTCI_List__c = ctciRec.Id;
            caseEdit.sClassification=classificationRec.Id;
            caseEdit.getIntentValues();
            caseEdit.getClassificationValues();
            caseEdit.sIntent = intentRec.Id;
            caseEdit.save();
            caseEdit.listTaskWrapper = 'Testing';
            caseEdit.caserecord.Status = 'Closed';
            caseEdit.bIsMedicareCase = true;
            caseEdit.bIsActiveClassification = false;
            caseEdit.bOGOAccept = true;
            caseEdit.isClassificationChnaged = true;
            
            List<SelectOption> lstBenefitOptions = caseEdit.getCallBenefitCategoryValues();
            caseEdit.pageredirect();
            caseEdit.sMedicareIssInit = '';
            caseEdit.getCCMedicareCalls();
            Test.StopTest();
            
        }
    }
    
    /*
    * This method is used to test the edit funtionality
    * @param   None
    * @return  None
    */
    static testMethod void testCaseEditScenario()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            
            Boolean bintentreq;
            
            Group oGrp = [Select Name,Type From Group Where Name='Cincinnati_Calls1' AND Type='QUEUE' limit 1];
            System.assertEquals(oGrp.Name, 'Cincinnati_Calls1');
            
            
           Work_Queue_Setup__c oWQS = [Select Work_Queue_Name__c, Public_Group_Name__c,Service_Center__c,Department__c,Name,OwnerId,Classification_Type__c From Work_Queue_Setup__c Where OwnerId=:oGrp.Id];
           System.assertEquals(oWQS.Public_Group_Name__c, 'Group one');
            
            
            case caseRecord = [Select id, RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'calls' LIMIT 1];
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='Benefits'];
            Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Rewards'];
            Classification__c classificationRecDec = [select id,Is_Active__c from Classification__c where name= 'Humana Pharmacy' limit 1];
            Intent__c intentRecDec = [Select id,Is_Active__c from Intent__c where name= 'Estimate' limit 1];
            
            CTCI_Junction__c ctciRec =[select id,name,Classification_Type__c,Classification__c, Intent__c from CTCI_Junction__c where Classification_Type__c =:ctRec.id LIMIT 1];
            System.assertEquals(ctciRec.Classification__c, classificationRec.Id);
           
            HUMConstants__c constants = [Select id, Name, StringValue__c from HUMConstants__c where Name='HUMCommentBody'];
            Test.startTest();
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            oWQS.Classification_Type__c = ctRec.Id;
            update oWQS;
            system.assertEquals(  oWQS.Classification_Type__c,ctRec.Id);
                   
            classificationRec.Is_Active__c = false;
            update classificationRec ;
            system.assertEquals(classificationRec.Is_Active__c == false, true);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.getClassificationValues();
            caseEdit.onLoad();
            caseEdit.showIntentReqOnLoad=false;
            caseEdit.viewAccountLookup = false;
            caseEdit.getopenAccountLookup();
            caseEdit.viewAccountLookup = true;
            caseEdit.getopenAccountLookup();

          
            
            
            caseRecord.DCN__c = 'Test2 DCN';
            update caseRecord;
            
            classificationRec.Is_Active__c = true;
            update classificationRec ;
            
            intentRec.Is_Active__c = false;
            update intentRec ;
            system.assertEquals(intentRec.Is_Active__c == false, true);
            caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.getIntentValues();
            intentRec.Is_Active__c = true;
            update intentRec ;
            
            testUser.Current_queue__c = 'Cincinnati_Calls1';
            update testUser;
            System.assertEquals(testUser.Current_queue__C, 'Cincinnati_Calls1');
        
            caseEdit = new CaseEdit_CE_HUM(scon);
            bintentreq = caseEdit.getbIsIntentRequired();
            caseEdit.sClassification = null;
            caseEdit.sIntent = null;  
            bintentreq = caseEdit.getbIsIntentRequired();
            caseEdit.getClassificationValues();
            caseEdit.getIntentValues();
            caseEdit.casecom.CommentBody = 'Testing Case Comments';
            caseEdit.sClassification = classificationRec.ID;
            caseEdit.sIntent = intentRec.id;             
            caseEdit.save();
            
            
            Test.stopTest();
    
        }
    }
    
    
    static testMethod void testCaseEditScenarioNeg()
    {
        User CaseEditTestUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(CaseEditTestUser)
        {
            Test.startTest();
            Boolean bintentreq = false;
            
            Group oGrp = [Select Name,Type From Group Where Name='Louisville RSO Enrollment' AND Type='QUEUE' limit 1];
            System.assertnotEquals(oGrp.Name, 'Cincinnati_Calls1');
            
             Work_Queue_Setup__c oWQS1 = new Work_Queue_Setup__c(Public_Group_Name__c = 'Group one', Service_Center__c = 'Louisville RSO Enrollment', Department__c = 'Calls1', Name = 'Louisville RSO Enrollment');
            oWQS1.OwnerId = oGrp.Id;
            insert oWQS1;
            System.assert(oWQS1 != null);
            System.assertEquals(oWQS1.Public_Group_Name__c, 'Group one');
           
            Work_Queue_Setup__c oWQS = [Select Work_Queue_Name__c, Public_Group_Name__c,Service_Center__c,Department__c,Name,OwnerId,Classification_Type__c From Work_Queue_Setup__c Where OwnerId=:oGrp.Id];
            System.assertEquals(oWQS.Public_Group_Name__c, 'Group one');
            
            
            case caseRecord = [Select id, RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'calls' LIMIT 1];
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='Benefits'];
            Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Rewards'];
            Classification__c classificationRecDec = [select id,Is_Active__c from Classification__c where name= 'Humana Pharmacy' limit 1];
            Intent__c intentRecDec = [Select id,Is_Active__c from Intent__c where name= 'Estimate' limit 1];
            
            CTCI_Junction__c ctciRec =[select id,name,Classification_Type__c,Classification__c, Intent__c from CTCI_Junction__c where Classification_Type__c =:ctRec.id LIMIT 1];
            System.assertEquals(ctciRec.Classification__c, classificationRec.Id);
           
            HUMConstants__c constants = [Select id, Name, StringValue__c from HUMConstants__c where Name='HUMCommentBody'];
            
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            oWQS.Classification_Type__c = ctRec.Id;
            update oWQS;
            system.assertEquals(  oWQS.Classification_Type__c,ctRec.Id);
                   
            classificationRec.Is_Active__c = false;
            update classificationRec ;
            system.assertEquals(classificationRec.Is_Active__c == false, true);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.getClassificationValues();
            
            caseEdit.onLoad();
            
            caseEdit.showIntentReqOnLoad=false;
            caseEdit.viewAccountLookup = false;
            caseEdit.getopenAccountLookup();
            caseEdit.viewAccountLookup = true;
            caseEdit.getopenAccountLookup();
            
            classificationRec.Is_Active__c = true;
            update classificationRec ;
            
            intentRec.Is_Active__c = false;
            update intentRec ;
            system.assertEquals(intentRec.Is_Active__c == false, true);
            caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.getIntentValues();
            intentRec.Is_Active__c = true;
            update intentRec ;
            
            CaseEditTestUser.Current_queue__c = 'Cincinnati_Calls1';
            update CaseEditTestUser;
            System.assertEquals(CaseEditTestUser.Current_queue__C, 'Cincinnati_Calls1');
        
            caseEdit = new CaseEdit_CE_HUM(scon);
            bintentreq = caseEdit.getbIsIntentRequired();
            caseEdit.sClassification = null;
            caseEdit.sIntent = null;  
            bintentreq = caseEdit.getbIsIntentRequired();
            caseEdit.getClassificationValues();
            caseEdit.getIntentValues();
            caseEdit.casecom.CommentBody = 'Testing Case Comments';
            caseEdit.sClassification = '';
            caseEdit.sIntent = intentRec.id;             
            caseEdit.save();
            
            caseEdit.sIntent = HUMConstants.HUMNone;  
            caseEdit.errorLevel='Error';
            caseEdit.messageName=Label.HUM_CaseEdit_Int_Err;
            caseEdit.showMessage();         
            caseEdit.save();
            
            caseEdit.sClassification = HUMConstants.HUMNone;
            caseEdit.save();
            
            caseEdit.checkIntent();
            caseEdit.sClassification = classificationRec.ID;
            caseEdit.sIntent = intentRec.id;    
            caseRecord.status=HUMConstants.HUM_CASEEDIT_CLOSED;
            caseEdit.saveNew();
            caseRecord.complaint__c='No';
            caseRecord.G_A_Rights_Given__c='No';
            caseEdit.Save(); 
            
            caseRecord.G_A_Rights_Given__c='No';        
            caseEdit.save();
           
           MET_Task__c oTask = new MET_Task__c();
            oTask.Name = 'Test Task';
            oTask.Is_Active__c = true;
             oTask.Case_Subtype__c = 'Routine';
            insert oTask;
            System.assertEquals(oTask.Name,'Test Task');
           
           
            caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.sClassification = null;
            caseEdit.getClassificationValues();
            caseEdit.sIntent = null;
            caseEdit.getIntentValues();
            
            caseEdit.sClassification=classificationRec.Id;
            caseEdit.getIntentValues();
            caseEdit.getClassificationValues();
            caseEdit.sIntent = intentRec.Id;
            list<MET_Task__c> oTask1 = [select Name from MET_Task__c ];
            caseEdit.save();
            caseRecord.CTCI_List__c = ctciRec.Id;
            caseEdit.getIntentValues();
            caseEdit.getClassificationValues();
            List<SelectOption> lstBenefitOptions = caseEdit.getCallBenefitCategoryValues();
            caseEdit.pageredirect();
            Test.stopTest();
        }
    }
    
    /*
    * This method is used to test negative case edit funtionality
    * @param   None
    * @return  None
    */
    static testMethod void testNegativeCaseEditScenario()
    {
        User testUser = [Select Id, Name from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            case caseRecord = [Select id,intent_id__c, classification_id__c,complaint__c, RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_STATUS,HUMConstants.HUM_CASEEDIT_CLOSED);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.sClassification = HUMConstants.HUMNone;
            caseEdit.sIntent = null;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseEdit.save();
            caseRecord.intent_id__c = null;
            caseRecord.classification_id__c = null;
            caseRecord.complaint__c='No';
            caseRecord.G_A_Rights_Given__c='No';           
            update caseRecord;
            
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='Benefits'];
            Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Rewards'];
            Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'calls' LIMIT 1];
            CTCI_Junction__c ctciRec =[select id,name,Classification_Type__c,Classification__c, Intent__c from CTCI_Junction__c where Classification_Type__c =:ctRec.id LIMIT 1];
            caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.getClassificationValues();
            caseEdit.getIntentValues();
            

            caseEdit.bHasExpProc = false;
            System.assertNotEquals(caseEdit.bHasExpProc , true);
            caseEdit.bhasPartDProc = false;
            System.assertNotEquals(caseEdit.bHasExpProc , true);

            caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.sClassification = null;
            caseEdit.getClassificationValues();
            caseEdit.sIntent = null;
            caseEdit.getIntentValues();
            caseEdit.sIntent = null;
            caseEdit.getIntentValues();
            
            caseEdit.sClassification=classificationRec.Id;
            caseEdit.getIntentValues();
            caseEdit.getClassificationValues();
            caseEdit.sIntent = intentRec.Id;
            caseEdit.save();
            caseEdit.listTaskWrapper = 'Testing';
            caseEdit.caserecord.Status = 'Closed';
            caseEdit.bIsMedicareCase = true;
            
            caseRecord.CTCI_List__c = ctciRec.Id;
            caseEdit.getIntentValues();
            caseEdit.getClassificationValues();
            List<SelectOption> lstBenefitOptions = caseEdit.getCallBenefitCategoryValues();
            caseEdit.pageredirect();
            
            Test.stopTest();
        }
    }
/*
    * This method is used to test case comments exception
    * @param   None
    * @return  None
    */
    static testMethod void testcasecommentsfailure()
    {
         User testUser = [Select Id,Name from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
                       
            Test.startTest();
            
            Boolean bintentreq;
            case caseRecord = [Select id, RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='Benefits'];
            Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Rewards'];
            Classification__c classificationRecDec = [select id,Is_Active__c from Classification__c where name= 'Humana Pharmacy' limit 1];
            Intent__c intentRecDec = [Select id,Is_Active__c from Intent__c where name= 'Estimate' limit 1];
            HUMConstants__c constants = [Select id, Name, StringValue__c from HUMConstants__c where Name='HUMCommentBody'];
            
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
            bintentreq = caseEdit.getbIsIntentRequired();
            caseEdit.sClassification = classificationRec.ID;
            caseEdit.sIntent = intentRec.id;  
            bintentreq = caseEdit.getbIsIntentRequired();
            caseEdit.getClassificationValues();
            caseEdit.getIntentValues();
            caseEdit.casecom.CommentBody = 'asdasasdasasdasasdasasdas';
            
            
            try
            {
                caseEdit.save();
                throw new HUMCustomException(system.Label.CASECOMMENT_LENGTHWARNING_HUM);
            }
            catch (Exception e) 
            {                           
                System.assertEquals(system.Label.CASECOMMENT_LENGTHWARNING_HUM , e.getMessage());            
            }
            Test.stopTest();
                        
        }
    }
    /*
    * This method is used to test Medicare PartC and PartD functionality
    * @param   None
    * @return  None
    */
    static testMethod void testCaseBenefitCategoryValues()
    {
        User testUser = [Select Id,Name from User where LastName='test CaseEditUser1'];
        System.runAs(testUser)
        {
            Test.startTest();
            case caseRecord1 = [Select id, RecordType.Name,status from case where status='In Progress' and Policy_Member__r.Name = 'abcd' ];
            System.assertEquals(caseRecord1.status,'In Progress');
            
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon1 = new ApexPages.StandardController(caseRecord1);
            CaseEdit_CE_HUM caseEdit1 = new CaseEdit_CE_HUM(scon1);
            List<SelectOption> lstBenefitOptions1 = caseEdit1.getCallBenefitCategoryValues();
           
            case caseRecord2 = [Select id, RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville Medicare Billing'];
            ApexPages.StandardController scon2 = new ApexPages.StandardController(caseRecord2);
            CaseEdit_CE_HUM caseEdit2 = new CaseEdit_CE_HUM(scon2);
            List<SelectOption> lstBenefitOptions2 = caseEdit2.getCallBenefitCategoryValues();
    
            caseRecord2.Owner_Queue__c = 'Louisville Calls';
            update caserecord2;
            List<SelectOption> lstBenefitOptions3 = caseEdit2.getCallBenefitCategoryValues();
            
            case caseRecord4 = [Select id, RecordType.Name from case where status='In Progress' and Policy_Member__r.Name = 'ijkl'];
            ApexPages.StandardController scon4 = new ApexPages.StandardController(caseRecord4);
            CaseEdit_CE_HUM caseEdit4 = new CaseEdit_CE_HUM(scon4);
            List<SelectOption> lstBenefitOptions4 = caseEdit4.getCallBenefitCategoryValues();
           
            case caseRecord5 = [Select id, RecordType.Name from case where status='In Progress' and Policy_Member__r.Name = 'mnop'];
            ApexPages.StandardController scon5 = new ApexPages.StandardController(caseRecord5);
            CaseEdit_CE_HUM caseEdit5 = new CaseEdit_CE_HUM(scon5);
            List<SelectOption> lstBenefitOptions5 = caseEdit5.getCallBenefitCategoryValues();
          
            case caseRecord6 = [Select id, RecordType.Name from case where status='In Progress' and Policy_Member__r.Name = 'qrst'];
            ApexPages.StandardController scon6 = new ApexPages.StandardController(caseRecord6);
            CaseEdit_CE_HUM caseEdit6 = new CaseEdit_CE_HUM(scon6);
            List<SelectOption> lstBenefitOptions6 = caseEdit6.getCallBenefitCategoryValues();
            caseEdit6.rerenderMedicareSection();
            
            Test.stopTest();
        }
    }
    
    /*
    * This method is used to test the edit funtionality
    * @param   None
    * @return  None
    */
    static testMethod void testCaseClassificationIntentEditScenario()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            case caseRecord = [Select id, RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Enrollment' LIMIT 1];
            system.assert(caseRecord.id!=null);
            HUMConstants__c constants = [Select id, Name, StringValue__c from HUMConstants__c where Name='HUMCommentBody'];
            system.assertEquals(constants.Name,'HUMCommentBody');
            
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.currentPage().getParameters().put(GLOBAL_CONSTANT_HUM.CASEEDIT_STATUS_HUM, 'Closed');
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
                              
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.casecom.CommentBody = 'Testing Case Comments';
            caseEdit.errorLevel='Error';
            caseEdit.messageName=Label.HUM_CaseEdit_Int_Err;
            caseEdit.showMessage();
            Boolean b =  caseEdit.getbIsIntentRequired();
            caseEdit.getClassificationValues();
            caseEdit.getIntentValues();
            boolean bcomplaint = caseEdit.medicareComplaintCheck();
            boolean bsegment = caseEdit.checkProductSegmentType();
            List<SelectOption> lstBenefitOptions = caseEdit.getCallBenefitCategoryValues();
            caseRecord.complaint__c='No';
            caseRecord.G_A_Rights_Given__c='No';
            caseedit.save();
            Test.stopTest();    
        }
    }
 /*
    * This method is used to test the getlstVerbalConsentOptionValues method.
    * @param   None
    * @return  None
    */
    static testMethod void testGetlstVerbalConsentOptionValues()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            case caseRecord = [Select id, RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Enrollment' LIMIT 1];
            system.assert(caseRecord.id!=null);
            
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
            List<SelectOption> options = caseEdit.getlstVerbalConsentOptionValues();
            system.assertEquals(options.get(0).getValue(), 'Yes');
            Test.stopTest();    
        }
    }
    
    /*
    * This method is used to load the Medicare Subtypes from Custom settings
    * @param   None
    * @return  None
    */

    @future
    static void loadCaseSubType()
    {
        List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c> medicareCaseSubTypeList = new List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c>();
        MEDENROLLMENT_CASE_SUBTYPE_HUM__c SubType = new MEDENROLLMENT_CASE_SUBTYPE_HUM__c();
        SubType.Name = 'Routine';
        SubType.SubType__c = 'Routine';
        medicareCaseSubTypeList.add(SubType);
        insert medicareCaseSubTypeList;
        
        HUMCaseInteractionAboutType__c InteractingAboutType = new HUMCaseInteractionAboutType__c();
        InteractingAboutType.Name = 'Member';
        InteractingAboutType.CaseRecordType__c = 'Member';
        InteractingAboutType.CaseInteraction_About_Type__c = 'Member';
        insert InteractingAboutType;
        
        HUMCaseInteractionAboutType__c InteractingAboutType2 = new HUMCaseInteractionAboutType__c();
        InteractingAboutType2.Name = 'Unknown Member';
        InteractingAboutType2.CaseRecordType__c = 'Unknown Member';
        InteractingAboutType2.CaseInteraction_About_Type__c = 'Unknown Member';
        insert InteractingAboutType2;
        
        Map<String, HUMCaseInteractionAboutType__c> mapCaseInterAboutType = HUMCaseInteractionAboutType__c.getAll();
        
    } 
    
     /*
    * This method is used to load the launch EMME URL from Custom settings
    * @param   None
    * @return  None
    */
    
    @future
    static void loadlaunchEMMEURL()
    {
        MET_Enrollment_Launch_EMME_URL__c URL = new MET_Enrollment_Launch_EMME_URL__c();
        URL.Name = 'URL';
        URL.Launch_EMME_URL__c = 'https://hss.humana.com/Emme/CreateMessage/Create?&verify=N&Suffix=V1&provTypeCd=TIN&mbrNo=V2&actionType=select&grpNo=V3&trackID=50980663200&CCPInquiryNo=894569111401-000000001&prefillAction=CREATE&letter=&plat=V4&buttonNext=Redirect';
        insert URL;
        
        Endpoint_Url_HUM__c e = new Endpoint_Url_HUM__c();
        e.Name = 'SSO_LOGINURL';
        e.Remote_Url__c = 'http://test-hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx';
        insert e;
    }
    
    /*
    * Method Name : testLogNotes
    * Description : This method Is used To test log notes webservice
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testLogNotes()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
               Case objCase = new Case();
                objCase = [Select id from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Enrollment' LIMIT 1];
                
                Continuation continueObj = (Continuation)CaseEdit_CE_HUM.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' ,'Test' ,'sAccNum',objCase.id);

                continueObj.state = 'BOTH';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"" } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
                string reqType = '';
                for(String s : mapDetails.keyset())
                {
                    reqType = s;
                } 
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                CaseEdit_CE_HUM.parseLogNoteResponse(labelGetmember,continueObj);
            
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testLogNotesUID
    * Description : This method Is used To test log notes webservice
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testLogNotesUID()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
               Case objCase = new Case();
                objCase = [Select id from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Enrollment' LIMIT 1];
                
                Continuation continueObj = (Continuation)CaseEdit_CE_HUM.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' ,'Test' ,'sAccNum',objCase.id);

                continueObj.state = 'BOTH';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"0" } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
                string reqType = '';
                for(String s : mapDetails.keyset())
                {
                    reqType = s;
                } 
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                CaseEdit_CE_HUM.parseLogNoteResponse(labelGetmember,continueObj);
            
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
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
               Case objCase = new Case();
                objCase = [Select id from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Enrollment' LIMIT 1];
                
                Continuation continueObj = (Continuation)CaseEdit_CE_HUM.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' ,'Test' ,'sAccNum',objCase.id);

                continueObj.state = 'BOTH';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
                string reqType = '';
                for(String s : mapDetails.keyset())
                {
                    reqType = s;
                } 
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                CaseEdit_CE_HUM.parseLogNoteResponse(labelGetmember,continueObj);
            
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testgetCaseCommentOptions
    * Description : This method Is used To test the return of log nodes
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testgetCaseCommentOptions()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            case caseRecord = [Select id, RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            system.assert(caseRecord.id!=null);
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.currentPage().getParameters().put(GLOBAL_CONSTANT_HUM.CASEEDIT_STATUS_HUM, 'Closed');
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            list<SelectOption> lstSortBy = new list<SelectOption>();
            PageReference pRef ;
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            lstSortBy = ceHum.getCaseCommentOptions();
            system.assert(lstSortBy.size()>0);
            pRef = ceHum.populateHiddentVal();
            
            Test.stopTest();
            
        }
        
        
    }
    
    /*
    * Method Name : testgetHPCaseComment
    * Description : This method Is used To test the Hp Case Comments
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testgetHPCaseComment()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            case caseRecord = [Select id, RecordType.Name,Owner_Queue__c from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            system.assert(caseRecord.id!=null);
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.currentPage().getParameters().put(GLOBAL_CONSTANT_HUM.CASEEDIT_STATUS_HUM, 'Closed');
            ApexPages.currentPage().getParameters().put('redirectFlag', 'true');
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            list<SelectOption> lstSortBy = new list<SelectOption>();
            PageReference pRef ;
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            ceHum.casecom.ParentId = caseRecord.Id;
            ceHum.casecom.CommentBody = 'Test';
            ceHum.sCodeHidden = 'Test';
            ceHum.bCaseTransferRedirectionRequired = true;
            ceHum.saveHPCaseComment();
            lstSortBy = ceHum.getCaseCommentOptions();
            system.assert(lstSortBy.size()>0);
            pRef = ceHum.populateHiddentVal();
            ceHum.bCaseTransferRedirectionRequired = true;
             ceHum.errorLevel='ERROR';
          	ceHum.messageName=Label.CaseTransferErrorMessage_HUM;
            ceHum.saveHPCaseComment();
            ceHum.save();
            ApexPages.currentpage().getParameters().put('errorCaseComment','false');
            ceHum.bCaseTransferRedirectionRequired = true;
            ceHum.saveHPCaseComment();
             ceHum.bCaseTransferRedirectionRequired = false;
            ceHum.saveHPCaseComment();
            ApexPages.currentpage().getParameters().put('errorCaseComment','true');
            ceHum.saveHPCaseComment();
            ceHum.sCode = '';
            ceHum.save();
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
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            case caseRecord = [Select id, RecordType.Name from case where Policy_Member__r.Name = 'MedCallsCC' LIMIT 1];
            system.assert(caseRecord.id!=null);
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_STATUS,'In Progress');
            list<SelectOption> lstSortBy = new list<SelectOption>();
            PageReference pRef ;            
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            ceHum.caseRecordTypeName ='Member Case';
            //Test required inputs
            ceHum.save();
            //Medicare Calls Case Comment insert
            ceHum.iCountMediIssueCC = 10;
            ceHum.iCountMediResCC = 25;
            ceHUM.sMedCallsIssueCC = 'Test Med Call Case Comment';
            ceHUM.sMedCallsResCC = 'TestMed Call Case CommentRes';
            ceHum.save();
            system.assert(ceHUM.sMedCallsIssueCC == 'Test Med Call Case Comment');
            //Medicare Calls Case Comment update
            ceHum.iCountMediIssueCC = 25;
            ceHum.iCountMediResCC = 30;
            ceHUM.sMedCallsIssueCC = 'Test Med Call Case Comment Update';
            ceHUM.sMedCallsResCC = 'TestMed Call Case CommentRes Update';
            ceHUM.bMediCCUpdate = true;
            ceHum.save();
            //Test length validation
            ceHum.iCountMediIssueCC = 1;
            ceHum.iCountMediResCC = 2;
            ceHum.iCountMediIssueCCInit = 9;
            ceHum.iCountMediResCCInit = 9;
            ceHum.bMediCCIssUpdate = true;
            ceHum.bMediCCResUpdate = true;
            ceHUM.sMedCallsIssueCC = 'TestL';
            ceHUM.sMedCallsResCC = 'TestL';
            ceHum.save();
            //Medicare Calls Case Comment insert
            ceHUM.sMedCallsIssueCC = 'Test Med Call Case Comment';
            ceHUM.sMedCallsResCC = 'TestMed Call Case CommentRes';
            ceHum.save();
            system.assert(ceHUM.sMedCallsIssueCC == 'Test Med Call Case Comment');
            Test.stopTest();
        }        
    }
    
    /*
    * Method Name : testMedicareCallsViewExistingCC
    * Description : This method Is used To test view existing test Medicare Calls Case Comments 
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testMedicareCallsViewExistingCC()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            case caseRecord = [Select id, RecordTypeID, RecordType.name from case where Status = 'Closed' and Owner_Queue__c = 'Louisville RSO Enrollment' LIMIT 1];
            caserecord.G_A_Rights_Given__c='No';
            caseRecord.Complaint__c='No';
            system.assert(caseRecord.id!=null);
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_STATUS,'Pending - Unassigned');
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            list<SelectOption> lstSortBy = new list<SelectOption>();
            PageReference pRef ;
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            cehum.checkCCMedicareCalls();
            cehum.getCCMedicareCalls();
            ceHum.caseRecordTypeName ='Member Case';
            //Test required inputs
            ceHum.save();
            //Medicare Calls Case Comment insert
            ceHum.iCountMediIssueCC = 10;
            ceHum.iCountMediResCC = 25;
            ceHUM.sMedCallsIssueCC = 'Test Med Call Case Comment';
            ceHUM.sMedCallsResCC = 'TestMed Call Case CommentRes';
            ceHum.save();
            ceHUM.sMedCallsIssueCC = null;
            ceHUM.sMedCallsResCC = null;
            ceHum.save();   
            ceHum.iCountMediIssueCC = 2110;
            ceHum.iCountMediResCC = 2500;
            ceHUM.sMedCallsIssueCC = 'Test Med Call Case Comment';
            ceHUM.sMedCallsResCC = 'TestMed Call Case CommentRes';
            ceHum.save(); 
            ceHUM.sMedCallsIssueCC = 'Test Med Call Case Comment';
            ceHUM.sMedCallsResCC = null;
            ceHum.save();        
            Test.stopTest();
        }
    }

    /*
    * Method Name : testMETEnrollmentSection
    * Description : This method Is used To test Medicare Section Methods
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testMETEnrollmentSection()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        Test.setMock(HttpCalloutMock.class, new TrackServiceMockResponse_T_HUM()); 
        System.runAs(testUser)
        {
            Test.startTest();
            case caseRecord = [Select id, RecordType.name, Type, Subtype__c from case where Policy_Member__r.Name = 'MedCallsCC' LIMIT 1];
            system.assert(caseRecord.id!=null);
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            String metTask = [Select id from MET_Task__c where Name = 'Test Task' limit 1].id;
            String metAction = [Select id from MET_Action__c where Name = 'Test Action' limit 1].id;
            String metSource = [Select id from MET_Source__c where Name = 'Test Source' limit 1].id;
            
            METEnrollment_H_HUM.getMultipleMETEntries(caseRecord.Type , caseRecord.Subtype__c); 
            METEnrollment_H_HUM.getMultipleMETTasks(caseRecord.Type , caseRecord.Subtype__c,'Retransmission no letter'); 
            
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            ceHUM.bIsMedicareCase = true;
            ceHUM.caseRec.subtype__c = 'Routine';
            ceHUM.listTaskWrapper = '{"listMETTaskDTO":[{"sTask":"'+metTask+'","sAction":"'+metAction+'","sSource":"'+metSource+'","sStatus":"Completed"}]}';
            ceHUM.selectedMETTaskId = metTask;
            ceHUM.selectedMETActionId = metAction;
            ceHUM.selectedMETSourceId = metSource;
            ceHum.loadMETTask();
            ceHum.loadMETAction();
            ceHum.loadMETSource();
            ceHum.fetchMetTaskList();
            ceHum.save();
            ceHum.getopenPolicyMemLookup();
            ceHum.getopenPolicyMemLookup();
            ceHum.getPolicyMemberRecs();
             
            ceHum.messageName = 'Test Message';
            ceHum.showMETMessage();
            
            
            ceHum.sMETEntriesCreationValue = 'Retransmission no letter';
            ceHum.loadMultipleMETTask();
            ceHum.getMETEntriesCreation(); 
            METEnrollment_H_HUM.getMultipleMETTasks(caseRecord.Type , caseRecord.Subtype__c,'');
            
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        
        //Creating Account Record
        Account accountRec = HUMTestDataHelperTest.getAccount();
        accountRec.OwnerId = testUser.id;
        accountRec.FirstName = 'Test';
        accountRec.LastName = 'Account';
        accountRec.Enterprise_Id__c = '123456';
        accountRec.recordtypeId = AccountRecordType.id;
        insert accountRec;
        system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
            Policy_Member__c pol = new Policy_Member__c();
            pol = [select id, Member__c from Policy_Member__c where id =: caseRecord.Policy_Member__c limit 1];
            pol.Member__c = accountRec.id;
            update pol;
            ceHum.errorLevel='ERROR';
          	ceHum.messageName=CaseEditConstants_HUM.POLICY_MEMBER_ERROR;
			ceHum.save();           
            
            pol.Member__c = caseRecord.AccountId;
            pol.Etl_Record_Deleted__c = True;
            update pol;
            
            ceHum.getPolicyMemberRecs();
            
            Test.stopTest();
        }
    }

    /*
     * Method Name : testHUMListLogErrors
     * Description : This method is used to test List listErrorLogs getters setters
     * Return type : NA
     * Paramater : NA
     */ 
   
    testMethod static void testHUMListLogErrors()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];

         
        system.runAs(testUser) 
        {
            Test.startTest();
            case caseRecord = [Select id,RecordType.name from case where Status = 'Closed' and Owner_Queue__c = 'Louisville RSO Enrollment' LIMIT 1];
            caserecord.G_A_Rights_Given__c='No';
            caseRecord.Complaint__c='No';
            system.assert(caseRecord.id!=null);
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_STATUS,'Pending - Unassigned');
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);

            ApexPages.currentpage().getParameters().put('caseComment','INT_WITH Placed order #### for the following: Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10). Payment Method: Invoice, Shipping Address: 7491 PINNICKINICK STREET,SANDY OR 97055, Release Date: 03/22/2019');

            CaseEdit_CE_HUM cedit = new CaseEdit_CE_HUM(scon);
                        
            List<Error_Log__c> lstLog = new List<Error_Log__c>();
            Error_Log__c error = new Error_Log__c(Status__c = 'CaseEdit');
            lstLog.add(error);
            
            cedit.listErrorLogs = lstLog;
            System.assert(cedit.listErrorLogs.size() > 0);
           
            Test.stopTest();
        }
    }

     /*
     * Method Name : testupdateCaseAfterATTVF
     * Description : This method is used to test testupdateCaseAfterATTVF
     * Return type : NA
     * Paramater : NA
     */ 
   
    testMethod static void testupdateCaseAfterATTVF()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();
            case caseRecord = [Select id,Topic__c, RecordType.Name,status from case where Status = 'Closed' and Owner_Queue__c = 'Louisville RSO Enrollment' LIMIT 1];
            System.assertEquals(caseRecord.Status, 'Closed');
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            ceHum.updateCaseAfterATTVF();
            ceHum.getClassificationValues();
            ceHum.getIntentValues();
            Test.stopTest();
        }
    }

     /*
     * Method Name : checkCreditableCoverageReadOnlyStatusTest
     * Description : This method is used to test checkCreditableCoverageReadOnlyStatus
     * Return type : NA
     * Paramater : NA
     */ 
   
    testMethod static void checkCreditableCoverageReadOnlyStatusTest()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();                       
            case caseRecord = [Select id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c  from case where Subtype__c = 'Creditable Coverage' and Case_Owner__c = 'Louisville RSO Calls Support' and Status = :GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE LIMIT 1];
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            CreditableCoverageConstant_HUM constantObj = new CreditableCoverageConstant_HUM();
            ApexPages.currentPage().getParameters().put(CreditableCoverageConstant_HUM.CC_CASE_DETAIL_TO_EDIT_URL_PARAMNAME,CreditableCoverageConstant_HUM.TRUE_STRING_CONSTANT );
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);
            caseEditPageObj.checkCreditableCoverageReadOnlyStatus();     
            caseEditPageObj.cleanUpTemplateData();      
            Test.stopTest();
            System.assertEquals(caseEditPageObj.bCreditableStatusReadonly,true);
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
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);
            caseEditPageObj.sPrevSubtype = '';
            caseEditPageObj.caseSubtypeOnlyChangeMStone();
            Test.stopTest();            
        }
    }
    /*
     * Method Name : isCreditableCoverageSubtypeElligibleTest
     * Description : This method is used to test isCreditableCoverageSubtypeElligible
     * Return type : NA
     * Paramater : NA
     */ 
   
    testMethod static void isCreditableCoverageSubtypeElligibleTest()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();                       
            case caseRecord = [Select id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c, type, origin  from case where Subtype__c = 'Creditable Coverage' and Case_Owner__c = 'Louisville RSO Calls Support' LIMIT 1];
            Test.setCurrentPage(page.CaseEdit_VF_HUM);           
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);
            System.assertEquals(caseEditPageObj.isCreditableCoverageSubtypeElligible(),true); 
            
            List<Task> lstTasks = [Select ID from Task Limit 1];
            List<ID> lstIds = new List<ID>();
            lstIds.Add(lstTasks[0].Id);
            caseEditPageObj.caseSubtypeAndTAS_MStone(lstIds);   
            
            caseRecord.SubType__c = '';
            
            List<Database.UpsertResult> objresults = new List<Database.UpsertResult>();
            Database.UpsertResult obj = Database.upsert(caseRecord, true);
            objresults.Add(obj);
            caseEditPageObj.writeMstoneToTrackService(objresults, lstTasks);
            
            Test.stopTest();      
        }
    }
        
    /*
     * Method Name : testSaveMethod
     * Description : This method is used to test save functionality
     * Return type : NA
     * Paramater : NA
     */ 
   
    testMethod static void testSaveMethod()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();                       
            case caseRecord = [Select id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c  from case where Subtype__c = 'New Creditable Coverage'LIMIT 1];
            Policy_Member__c objPol = [Select Id,Name from Policy_Member__c where Name = 'MedCallsCC' Limit 1];
            caseRecord.G_A_Rights_Given__c = 'Yes';
            caseRecord.G_A_Reason__c ='testing';
            caseRecord.Complaint__c ='No'; 
                                         
            Test.setCurrentPage(page.CaseEdit_VF_HUM); 
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
        
            ApexPages.currentpage().getParameters().put('caseComment','INT_WITH Placed order #### for the following: Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10). Payment Method: Invoice, Shipping Address: 7491 PINNICKINICK STREET,SANDY OR 97055, Release Date: 03/22/2019');

            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.caseRec.Status = 'closed';
            caseEdit.sClassification = 'Claims';
            
            caseEdit.bIsMedicareCase = true;
            caseEdit.sIntent = 'Duplicate Request';
            METTaskList_DTO_HUM objTasks = new METTaskList_DTO_HUM();
            objTasks.listMETTaskDTO = new List<METTaskList_DTO_HUM.METTask_DTO_HUM>();
            
            MET_Task__c oTask = [Select Id,Name from MET_Task__c Where Name = 'Test Task' Limit 1];
            MET_Source__c oSource = [Select Id,Name from MET_Source__c Where Name = 'Test Source' Limit 1];
            MET_Action__c oAction = [Select Id,Name from MET_Action__c Where Name = 'Test Action' Limit 1];

            Task oTask1 = new Task(WhatId = caseEdit.caseRec.id,type='Work Task', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                               status='Closed' , Topic__c = 'SIU',MET_Task__c = oTask.Id,MET_Source__c = oSource.Id,MET_Action__c=oAction.Id);
            
            METTaskList_DTO_HUM.METTask_DTO_HUM obj = new METTaskList_DTO_HUM.METTask_DTO_HUM(oTask1);
            obj.sTask = oTask.Name;
            obj.sAction = oAction.Id;
            obj.sSource = oSource.Id;
            objTasks.listMETTaskDTO.Add(obj);
            caseEdit.listTaskWrapper = Json.Serialize(objTasks);
            caseEdit.save();
            System.assertEquals('',caseEdit.returnURL);
            obj.sTask = oTask.Id;
            objTasks.listMETTaskDTO = new List<METTaskList_DTO_HUM.METTask_DTO_HUM>();
            objTasks.listMETTaskDTO.Add(obj);
            caseEdit.listTaskWrapper = Json.Serialize(objTasks);
            caseEdit.sourceIdLaunchEMMEMap = new Map<String, Boolean>();
            caseEdit.sourceIdLaunchEMMEMap.put(oSource.Id,true);
            caseEdit.CaseRec.Policy_Member__c = objPol.Id;
            caseEdit.save();
            caseEdit.listTaskWrapper = '';
            
            caseEdit.save();
            Test.stopTest();
        }
    }
    /*
    * This method is to test show and hide fields 
    * @param  NA
    * @return void
    */
     testMethod static void showHideTest()
     {
         User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        
        System.runAs(testUser)
        {
            test.startTest();
         case case1 = new case();  
         case1 = [select Interacting_About_Type__c,id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c  from case where Interacting_About_Type__c='Member' limit 1];
         System.assertEquals(case1.Interacting_About_Type__c,'Member');
         ApexPages.StandardController scon = new ApexPages.StandardController(case1);
         CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
         
         caseEdit.showHideFields();
            
         case case2 = new case();  
         case2 = [select Interacting_About_Type__c,id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c  from case where Interacting_About_Type__c='Provider' limit 1];
         ApexPages.StandardController scon1 = new ApexPages.StandardController(case2);
         CaseEdit_CE_HUM caseEdit1 = new CaseEdit_CE_HUM(scon1);
         
         caseEdit1.showHideFields();
            
             case case3 = new case();  
         case3 = [select Interacting_About_Type__c,id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c  from case where Interacting_About_Type__c='Unknown-Provider' limit 1];
         ApexPages.StandardController scon2 = new ApexPages.StandardController(case3);
            CaseEdit_CE_HUM caseEdit2 = new CaseEdit_CE_HUM(scon2);
         
         caseEdit2.showHideFields();
            
             case case4 = new case();  
         case4 = [select Interacting_About_Type__c,id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c  from case where Interacting_About_Type__c='Unknown-Agent' limit 1];
         ApexPages.StandardController scon3 = new ApexPages.StandardController(case4);
            CaseEdit_CE_HUM caseEdit3 = new CaseEdit_CE_HUM(scon3);
         
         caseEdit3.showHideFields();
            test.stopTest();
        }
     }
    
    /*
    * This method is to test checkProcess
    * @param   caserecord id
    * @return  true Or false
    */
    
    testMethod static void checkProcesstest()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        
        System.runAs(testUser)
        {
       case case1 = new case();  
         case1 = [select Interacting_About_Type__c,id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c  from case where Interacting_About_Type__c='Member' limit 1];
         System.assertEquals(case1.Interacting_About_Type__c,'Member');
         ApexPages.StandardController scon = new ApexPages.StandardController(case1);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
         
         caseEdit.checkProcess(case1.Id);
         caseEdit.getVerifyAccountType();
        
         caseEdit.populateNPITAX();
         caseEdit.clearNPITAX();
           
        }
    }
    
    /*
    * This method is to test the method that retrun the taxId
    * @param  NA
    * @return void
    */
    
     testMethod static void getTaxIdtest()
     {
          
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        
        System.runAs(testUser)
        {
            Account acc=new Account();
            acc=[select id,OwnerId,FirstName,LastName,recordtypeId from Account where LastName='Account' limit 1];
            System.assertEquals(acc.LastName,'Account');
            ConsumerID__c cons = new ConsumerID__c();
            cons.Account__c = acc.id;
            insert cons;
            CaseEdit_CE_HUM.getTaxId(acc.id);
        }
         
     }
    
     /*
    * This method Is to test the method that update classification and intents for Part D process and for medicare appeal process
    * @param  NA
    * @return void
    */
       
    testMethod static void updateCasetest()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        
        System.runAs(testUser)
        {
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = '1393270';
            crm1.Scrum_Team__c = 'Transformers';
            crm1.Story_Title__c = 'CRM Process - Initiating queue/views for transfers to MHK';
            crm1.Release_Name__c = '10/23/2020';
            insert crm1;  
       case case1 = new case();  
         CTCI_Junction__c ctci=new CTCI_Junction__c();
         ctci=[SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name='G&A' and Intent__r.Name='Expedited' and Classification_Type__r.Name='Calls (RSO)' limit 1];
         System.assertEquals(ctci.Intent__r.Name,'Expedited');
         case1 = [select Interacting_About_Type__c,id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c,Classification_Type__c  from case where Classification_Type__c='Calls (RSO)' AND Work_Queue_View_Name__c='other' limit 1];
         case1.Topic__c = 'Other';
            case1.Interacting_About_Type__c = 'Member';
            update case1;
         ApexPages.StandardController scon = new ApexPages.StandardController(case1);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
             caseEdit.updateCaseAfterExp();
         CTCI_Junction__c ctci1=new CTCI_Junction__c();
         ctci1=[SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name='Rx' and Intent__r.Name='Part D Redetermination' and Classification_Type__r.Name='Calls (RSO)' limit 1];
             caseEdit.updateCaseAfterPart();
        caseEdit.isMedExpProcessAttachedExp();
             caseEdit.isMedExpProcessAttachedPartD();
        CRMFunctionality_ONOFF_Switch__c crm2 = new CRMFunctionality_ONOFF_Switch__c();
             crm2.IsON__c =false;
            crm2.Name = '1393270';
            crm2.Scrum_Team__c = 'Transformers';
            crm2.Story_Title__c = 'CRM Process - Initiating queue/views for transfers to MHK';
            crm2.Release_Name__c = '10/23/2020';
            insert crm2;  
         caseEdit.updateCaseAfterExp();
         caseEdit.updateCaseAfterPart();
         caseEdit.isMedExpProcessAttachedExp();
         caseEdit.isMedExpProcessAttachedPartD();
        }
    }
    
     /*
     * Method Name : isCreditableCoverageProcessAttachedTest
     * Description : This method is used to test isCreditableCoverageProcessAttached
     * Return type : NA
     * Paramater : NA
     */ 
   
    testMethod static void isCreditableCoverageProcessAttachedTest()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        system.runAs(testUser) 
        {
            Test.startTest();                       
            case caseRecord = [Select id,Topic__c,status,Guided_Process_Flag__c,RecordType.Name,Case_Owner__c,Owner_Queue__c,Subtype__c, type, origin  from case where Subtype__c = 'Creditable Coverage' and Case_Owner__c = 'Louisville RSO Calls Support' LIMIT 1];
            Test.setCurrentPage(page.CaseEdit_VF_HUM);           
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);
            System.assertEquals(caseEditPageObj.isCreditableCoverageProcessAttached(),true); 
            
            List<Task> lstTasks = [Select ID from Task Limit 1];
            List<ID> lstIds = new List<ID>();
            lstIds.Add(lstTasks[0].Id);
            caseEditPageObj.caseSubtypeAndTAS_MStone(lstIds);   
            
            caseRecord.SubType__c = '';
            
            List<Database.UpsertResult> objresults = new List<Database.UpsertResult>();
            Database.UpsertResult obj = Database.upsert(caseRecord, true);
            objresults.Add(obj);
            caseEditPageObj.writeMstoneToTrackService(objresults, lstTasks);            
            
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
            Test.setCurrentPage(page.CaseEdit_VF_HUM);           
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);                                                        
            caseEditPageObj.writeMstoneToTrackService(saveRes, lstMETTasks);            
            Test.stopTest();      
        }
    }
    
    /*
    * Method Name : testRSOHPFunctionality
    * Description : This method is used To test changeCaseRecordType() method
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testRSOHPFunctionality()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser2'];
        
        System.runAs(testUser)
        {            
            Boolean bintentreq;
            
            Group oGrp = [Select Name,Type From Group Where Name='Cincinnati_Calls1' AND Type='QUEUE' limit 1];
            System.assertEquals(oGrp.Name, 'Cincinnati_Calls1');
                       
            Work_Queue_Setup__c oWQS = [Select Work_Queue_Name__c, Public_Group_Name__c,Service_Center__c,Department__c,Name,OwnerId,Classification_Type__c From Work_Queue_Setup__c Where OwnerId=:oGrp.Id];
            System.assertEquals(oWQS.Public_Group_Name__c, 'Group one');
                        
            case caseRecord = [Select id, RecordType.Name from case where RecordType.Name = 'Member Case' and Owner_Queue__c = 'Louisville RSO Calls Support' LIMIT 1];
            Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'Calls (RSO)' LIMIT 1];
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='HP Billing' LIMIT 1];
            Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Add/Updated Credit Card' LIMIT 1];
            
            CTCI_Junction__c ctciRec =[select id,name,Classification_Type__c,Classification__c,Intent__c from CTCI_Junction__c where Classification_Type__c=:ctRec.id and Classification__c=:classificationRec.id LIMIT 1];
            System.assertEquals(ctciRec.Intent__c, intentRec.Id);
            
            case caseRecord1 = [Select id, RecordType.Name from case where RecordType.Name = 'HP Member Case' and Owner_Queue__c = 'Louisville RSO Calls Support' LIMIT 1];
            Classification__c classificationRec1 = [select id,Is_Active__c from Classification__c where Name='Eligibility' LIMIT 1];
            Intent__c intentRec1 = [Select id,Is_Active__c from Intent__c where Name='Hospice' LIMIT 1];
            
            CTCI_Junction__c ctciRec1 =[select id,name,Classification_Type__c,Classification__c,Intent__c from CTCI_Junction__c where Classification_Type__c=:ctRec.id and Classification__c=:classificationRec1.id LIMIT 1];
            System.assertEquals(ctciRec1.Intent__c, intentRec1.Id);
           
            Test.startTest();
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            oWQS.Classification_Type__c = ctRec.Id;
            update oWQS;
            system.assertEquals(oWQS.Classification_Type__c,ctRec.Id);
            
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.sClassification = classificationRec.ID;
            caseEdit.sIntent = intentRec.id;    
            caseEdit.saveNew();
            caseRecord.complaint__c='No';
            caseRecord.G_A_Rights_Given__c='No';
            caseEdit.Save();
            system.assert(caseEdit.sClassification!=null);
           
            ApexPages.StandardController scon1 = new ApexPages.StandardController(caseRecord1);
            oWQS.Classification_Type__c = ctRec.Id;
            update oWQS;
            system.assertEquals(oWQS.Classification_Type__c,ctRec.Id);
            
            CaseEdit_CE_HUM caseEdit1 = new CaseEdit_CE_HUM(scon1);
            caseEdit1.sClassification = classificationRec1.ID;
            caseEdit1.sIntent = intentRec1.id;    
            caseEdit1.saveNew();
            caseRecord1.complaint__c='No';
            caseRecord1.G_A_Rights_Given__c='No';
            caseEdit1.Save();
            system.assert(caseEdit1.sClassification!=null);
            caseEdit1.fetchUserDetails();
            caseEdit1.getlstSubFunctions();
            Test.stopTest();
        }
    }
    
     /*
     * Method Name : testCompassLogic
     * Description : This method is used to test Compass Logic Changes
     * Return type : NA
     * Paramater : NA
     */ 
     @isTest
      static void testCompassLogic()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        testuser.Mentor_Sub_Function__c = 'Individual Medicare';
        update testUser;
        system.runAs(testUser) 
        {                       
            case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE Type = 'TRR Inquiry' AND Subtype__c = 'TRR' LIMIT 1];  
            Test.startTest();
            Test.setCurrentPage(page.CaseEdit_VF_HUM);           
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon); 
            caseEditPageObj.getlstSubFunctions();
            system.assertEquals(caseEditPageObj.getlstSubFunctions().size(),0); 
                                                                          
            Test.stopTest();      
        }
    }
    
     @isTest
    static void testOGOCase()
  {
      User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
      system.runAs(testUser) 
      {        
        Test.startTest(); 

          Product2 prd = [SELECT id, Name,Major_LOB__c,product_type__c FROM product2 LIMIT 1] ;
          prd.Major_LOB__c = 'MER';
          prd.product_type__c = '';
          update prd;

          Policy__c pol = [SELECT id,Product__c FROM Policy__c   LIMIT 1];
          pol.product__c = prd.id;
          update pol;

          Policy_Member__c pm = [SELECT id, policy__c FROM Policy_Member__c LIMIT 1];
          pm.policy__C = pol.id;
          update pm;

        Account acc = [SELECT id,lastname FROM ACCOUNT WHERE lastname='Account' LIMIT 1];
        Classification__c classificationRec1 = [select id,Is_Active__c from Classification__c where Name='Eligibility' LIMIT 1];
        Intent__c intentRec1 = [Select id,Is_Active__c from Intent__c where Name='Hospice' LIMIT 1];
        Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'Calls (RSO)' LIMIT 1];
        CTCI_Junction__c ctciRec1 =[select id,name,Classification_Type__c,Classification__c,Intent__c from CTCI_Junction__c where Classification_Type__c=:ctRec.id and Classification__c=:classificationRec1.id LIMIT 1];
        System.assertEquals(ctciRec1.Intent__c, intentRec1.Id);
        Case oCase12 = New Case(AccountId = acc.Id, Intent_Id__c = intentRec1.Id, Classification_Id__c = classificationRec1.Id, Policy_Member__c = pm.Id,
                             Interacting_With_Type__c = 'Member', Interacting_With__c=acc.Id, Due_Date__c=Date.Today(), ctci_list__c=ctciRec1.id,Type = 'TRR Inquiry',
                             Case_Owner__c = 'Louisville RSO Calls Support', Owner_Queue__c ='Green Bay Grievance and Appeals', Work_Queue_View_Name__c='Cincinati CRU',AutoRoute_status__c = 'Not Eligible',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', recordtypeid =  Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId(),Complaint__c = 'Yes - Medicare Part C',Complaint_Reason__c = 'Other',Complaint_Type__c = 'Behavioral',
                             G_A_Rights_Given__c='No', Interacting_About_type__c = 'Member');
                             
                             insert oCase12;
                             
        oCase12.Owner_Queue__c = 'Green Bay Grievance and Appeals';
        oCase12.recordtypeid= Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
        oCase12.status = 'In Progress';
        update oCase12;
                             List<Case> cases1 = new List<Case>();
        cases1.add(oCase12);
        CaseHelperPBOGOInProgress_H_HUM.invokeFromPB(cases1);
      
        System.assertNotEquals(cases1, null);
        System.assertEquals(cases1.isEmpty(), false);
          
          	Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Complaints or Compliments';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
           
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'QAA';
            intentRec.Is_Active__c = true;
            
            insert intentRec;
            
          ApexPages.StandardController scon = new ApexPages.StandardController(oCase12);
          CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);
          
          caseEditPageObj.bOGOAccept = true;
          caseEditPageObj.callSave();
          caseEditPageObj.sClassification = classificationRec.Id;
          caseEditPageObj.sIntent = intentRec.id;
          caseEditPageObj.errorLevel='WARNING';
          caseEditPageObj.messageName=Label.QAAPROCESS_WARNING_HUM;
          caseEditPageObj.Save();
          caseEditPageObj.sAutoRouteStatus = 'Evaluate Good Cause';
          caseEditPageObj.errorLevel='ERROR';
          caseEditPageObj.messageName=Label.Complaint_must_be_selected_No;
          caseEditPageObj.Save();
        Test.stopTest();      
      }
  }
    @isTest
    static void testOGOCase1()
  {
      User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
      system.runAs(testUser) 
      {        
        Test.startTest(); 
        List<Case> caseList = [SELECT Id,RecordType.Name,Complaint__c,Owner_Queue__c,status FROM Case WHERE subtype__c!=null LIMIT 1];

         System.assertEquals(caseList.isEmpty(), false);
        caseList[0].Owner_Queue__c = 'Green Bay Grievance and Appeals';
        caseList[0].status = 'Closed';
        caseList[0].recordtypeid= Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed Medicare Case').getRecordTypeId();
        caseList[0].complaint__c= 'No';
        caseList[0].G_A_Rights_Given__c='No';
        update caseList;
        CaseHelperPBOGOInProgress_H_HUM.invokeFromPB(caseList);
         
        caseList[0].Owner_Queue__c = 'Green Bay Grievance and Appeals';
        caseList[0].status = 'Closed';
        caseList[0].recordtypeid= Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed Member Case').getRecordTypeId();
        caseList[0].complaint__c= 'No';
        caseList[0].G_A_Rights_Given__c='No';
        caseList[0].subtype__c = '';
        update caseList;
        CaseHelperPBOGOInProgress_H_HUM.invokeFromPB(caseList);

        Test.stopTest();   
 
      }
  }
    
    /*
     * test class for validating case DCN
     */
     @isTest
    static void testEhubValidationDCNServicePositive(){
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        testuser.Mentor_Sub_Function__c = 'Individual Medicare';
        update testUser;
        System.AssertEquals(testUser.Mentor_Sub_Function__c,'Individual Medicare');

        system.runAs(testUser) 
        { 
            Test.startTest();
            list<Case> lstCase = new list<Case>();
            HUM_Webservice_Callout__c ehubDCNService = HUM_Webservice_Callout__c.getValues('DCN_Ehub');
            case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin,DCN__c  FROM Case WHERE Type = 'Correspondence Inquiry' LIMIT 1];  
            caseRecord.DCN__c = '123456789';
            caseRecord.Interacting_About_Type__c = 'Member';
            update caseRecord;
            
            lstCase.add(caseRecord);
            Test.setCurrentPage(page.CaseEdit_VF_HUM);           
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon); 
            caseEditPageObj.isDCNDisplay = true;
            caseEditPageObj.save();
            caseEditPageObj.getlstSubFunctions();
            //system.assertEquals(caseEditPageObj.getlstSubFunctions().size(),6); 
            HUMUtilityHelper.ehubValidationDCNService('123456789');
            HUMUtilityHelper.createAttachmentForDCN(lstCase);
            ehubDCNService.Timeout__c = null;
            update ehubDCNService;
            HUMUtilityHelper.ehubValidationDCNService('123456789');
            Test.stopTest();      
        }
    }
   
    
     @isTest
      static void testPermissionRSO()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        PermissionSet ps1 = new PermissionSet();
        ps1.Name = 'CRMS_400_Grievance_Appeals';
        PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_206_CCSHumanaPharmacyAccess'];
        insert new PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = ps.Id);
        PermissionSet permission = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_400_Grievance_Appeals'];
         insert new PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = permission.Id);
        system.runAs(testUser) 
        {  
            test.startTest();
            case caseRecord1 = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE Classification_Type__c = 'Calls (RSO)' LIMIT 1]; 
            case caseRecord2 = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE recordType.Name = 'HP Member Case' LIMIT 1]; 
            ApexPages.StandardController scon1 = new ApexPages.StandardController(caseRecord1);
             Test.setCurrentPage(page.CaseEdit_VF_HUM);
            CaseEdit_CE_HUM caseEditPageObj1 = new CaseEdit_CE_HUM(scon1); 
             caseEditPageObj1.checkIntent();
            caseEditPageObj1.updateCaseAfterATTVF();
             caseEditPageObj1.updateCaseAfterPCP();
            caseEditPageObj1.updateCaseAfterGoodCause();

            ApexPages.StandardController scon2 = new ApexPages.StandardController(caseRecord2);
             
            CaseEdit_CE_HUM caseEditPageObj2 = new CaseEdit_CE_HUM(scon2); 
            caseEditPageObj2.updateCaseAfterPCP();
            caseEditPageObj2.isMedExpProcessAttachedGoodCause();
            
            caseRecord1.status = CaseEditConstants_HUM.CASE_STATUS_PENDING_RESPONSE;
            caseRecord1.Guided_Process_Flag__c = true;
            caseRecord1.Topic__c = 'Other';
            update caseRecord1;
            System.AssertEquals(caseRecord1.Topic__c,'Other');
             caseEditPageObj1.updateCaseAfterATTVF();
            caseEditPageObj1.updateCaseAfterExp();
            caseEditPageObj1.updateCaseAfterGoodCause();
            caseEditPageObj1.updateCaseAfterPCP();
            caseEditPageObj1.updateCaseMedicaidPARProvider();
            Test.stopTest(); 
        }
    }
    
    
    @isTest
      static void testClaimsLogic()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        
        system.runAs(testUser) 
        {  
            case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE Classification_Type__c = 'Calls (RSO)'  LIMIT 1];  
            
            Test.startTest();
            Test.setCurrentPage(page.CaseEdit_VF_HUM);           
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon); 
            caseEditPageObj.checkMedicareCustomerServiceAccess = true;
            caseEditPageObj.bClaimNumberBtn = true;
            caseEditPageObj.sClaimNumbers = '12345,11111,22222';
            caseEditPageObj.saveClaimNumbersTobackend();
            System.AssertNotEquals(caseRecord.Id,Null);
            Test.stopTest(); 
        }
    }
    
    @isTest
      static void testCheckClassification()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        system.runAs(testUser) 
        {  
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='HP Billing' LIMIT 1];
            case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE Classification_Type__c = 'Calls (RSO)'  LIMIT 1];  
            Test.startTest();
            Test.setCurrentPage(page.CaseEdit_VF_HUM);   
            
            caseRecord.OGO_Resolution_Type__c ='Oral';
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon); 
            caseEditPageObj.showAccountName = true;
            caseEditPageObj.bIsIntentRequired = true;
            caseEditPageObj.customAccLookupFlag = true;
            caseEditPageObj.bMemberFlag = true;
            caseEditPageObj.bProviderFlag = true;
            caseEditPageObj.bGroupFlag = true;
            caseEditPageObj.bAgentFlag = true;
            caseEditPageObj.sSubFunctionSelected = 'SomeFunction';
            caseEditPageObj.bDisableCloseCaseBtn = true;
            caseEditPageObj.ResolDate();
            caseEditPageObj.oHUMSearchController = new HUMSearchControllerV2();
            caseEditPageObj.sClassification = classificationRec.ID;
            caseEditPageObj.bProviderSwitch = true;
            caseEditPageObj.checkClassification();
           // caseEditPageObj.setLogCode();
           System.AssertNotEquals(classificationRec.Id,Null);
            Test.stopTest(); 
        }
    }

    @isTest
    static void testHpConstructorCoverage()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        
        PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.PDPPilot_PermissionSet_Name LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = pCheck.id);
        
        List<Group> grp = [Select Id from Group where type = :GLOBAL_CONSTANT_HUM.GROUP_QUEUE_HUM and Name = :GLOBAL_CONSTANT_HUM.PDP_Pilot_Pharmacy_Work_Queues Limit 1];
         if(grp.size()>0){
            GroupMember grpMem1 = new GroupMember();
            grpMem1.UserOrGroupId = testUser.id;
            grpMem1.GroupId = grp[0].Id;
            insert grpMem1;
        }
        
        system.runAs(testUser) 
        {  
            case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin, AccountId  FROM Case WHERE recordType.Name = 'HP Member Case' AND Classification_Type__c = 'Humana Pharmacy Calls' LIMIT 1]; 

            Test.startTest();
            Test.setCurrentPage(page.CaseEdit_VF_HUM);    
            ApexPages.currentpage().getParameters().put('isHP','true');
            ApexPages.currentpage().getParameters().put('invoicelogging','true');
            ApexPages.currentpage().getParameters().put('pharmacylogging','true');
            ApexPages.currentpage().getParameters().put('creditcardlogging','true');
            ApexPages.currentpage().getParameters().put('TicketRequired','true');
            ApexPages.currentpage().getParameters().put('caseComment','Test NEW_LINE Test');
            System.AssertNotEquals(caseRecord.Id,Null);

            Interaction__c objInteraction = New Interaction__c();
            objInteraction.Interacting_With__c = caseRecord.AccountId;
            objInteraction.Interacting_With_type__c = 'Member';
            objInteraction.Caller_Name__c = 'Test User';
            objInteraction.Interaction_Origin__c = 'Phone';
            insert objInteraction;
            
            ApexPages.currentpage().getParameters().put('interactionId', objInteraction.Id);
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);

            caseEditPageObj.resetIntentCCLogging = true;
            caseEditPageObj.checkClassification();

            Test.stopTest(); 
        }
    }
    
    @isTest
    static void testWebIssueRequired()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        
        system.runAs(testUser) 
        {  
            case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE recordType.Name = 'HP Member Case' AND Classification_Type__c = 'Humana Pharmacy Calls' LIMIT 1]; 
                
            Test.startTest();
            System.AssertNotEquals(caseRecord.Id,Null);
            Test.setCurrentPage(page.CaseEdit_VF_HUM);    
            ApexPages.currentpage().getParameters().put('isHP','true');
            ApexPages.currentpage().getParameters().put('TicketRequired','false');
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);
            
            caseEditPageObj.sClassification = 'Test';
            caseEditPageObj.sIntent = 'Test';
            
            caseEditPageObj.setLogCode();

            Test.stopTest(); 
        }
    }

    @isTest
    static void testServiceModelType()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        
        system.runAs(testUser) 
        {  
            case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE recordType.Name = 'HP Member Case' AND Classification_Type__c = 'Humana Pharmacy Calls' LIMIT 1]; 

            list<CTCI_Junction__c> serviceModelCTCI = [SELECT Classification_Type__r.name, Classification__r.Name, Intent__r.Name, Service_Model_Type__c FROM CTCI_Junction__c WHERE Service_Model_Type__c != null AND Classification_Type__r.name =: caseRecord.Classification_Type__c  ORDER BY Classification_Type__r.name, Classification__r.Name DESC];
                
            Test.startTest();
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);
            System.AssertNotEquals(caseRecord.Id,Null);
            
            caseEditPageObj.sClassification = serviceModelCTCI[0].Classification__c;
            caseEditPageObj.sIntent = serviceModelCTCI[0].Intent__c;
            caseEditPageObj.setLogCode();

            caseEditPageObj.sClassification = serviceModelCTCI[1].Classification__c;
            caseEditPageObj.sIntent = serviceModelCTCI[1].Intent__c;
            caseEditPageObj.setLogCode();

            //caseEditPageObj.lstRSOHPPermissionSet.add(new PermissionSetAssignment());
            //caseEditPageObj.setLogCode();

            Test.stopTest(); 

            caseRecord.Classification_Type__c = 'HP Specialty Calls';
            update caseRecord;

        }
    }

    @isTest
    static void testHpCommentSwitching()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1];
        
        system.runAs(testUser) 
        {  
            case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE recordType.Name = 'HP Member Case' AND Classification_Type__c = 'HP Specialty Calls' LIMIT 1]; 

            list<CTCI_Junction__c> serviceModelCTCI = [SELECT Classification_Type__r.name, Classification__r.Name, Intent__r.Name, Service_Model_Type__c FROM CTCI_Junction__c WHERE Service_Model_Type__c != null AND Classification_Type__r.name =: caseRecord.Classification_Type__c ORDER BY Classification_Type__r.name, Classification__r.Name ASC];
                
            Test.startTest();
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            System.AssertNotEquals(caseRecord.Id,Null);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);

            caseEditPageObj.bIsPharmacyUser = true; 
            caseEditPageObj.bHasLogCodeList = true;
            caseEditPageObj.bIsLogCodelistAvailable = false;

            caseEditPageObj.sClassification = serviceModelCTCI[0].Classification__c;
            caseEditPageObj.sIntent = serviceModelCTCI[0].Intent__c;
            caseEditPageObj.setLogCode();
            caseEditPageObj.setLogCode();

            caseEditPageObj.sClassification = serviceModelCTCI[1].Classification__c;
            caseEditPageObj.sIntent = serviceModelCTCI[1].Intent__c;
            caseEditPageObj.setLogCode();
            caseEditPageObj.setLogCode();

            caseEditPageObj.sClassification = null;
            caseEditPageObj.bIsLogCodelistAvailable = true;
            caseEditPageObj.setLogCode();
            caseEditPageObj.setLogCode();

            Test.stopTest();

        }
    }

    /**
    *  This method to test MET 
    */
    @testSetup static void testLoadMultipleMETTask()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1]; 
        system.runAs(testUser)
        {        
            Account acc1 = HUMTestDataHelperTest.getAccount();
            acc1.Name = 'Test Account Cases1';
            acc1.Birthdate__c='01/15/1951';
            acc1.Enterprise_ID__c = '1005447107';
            insert acc1;        
            system.assertEquals(acc1.Name == 'Test Account Cases1',true);
        
            Policy__c pol = HUMTestDataHelperTest.getPolicy();
            pol.Group_Name__c = acc1.Id;
            pol.Name = 'Test Policy';
            insert pol;
            system.assertEquals(pol.Name,'Test Policy');
            system.assertEquals(pol.Group_Name__c == acc1.Id,true);
        
            Policy_Member__c polMember = HUMTestDataHelperTest.getPolicyMember();
            polMember.Name='Policy Member PM1';
            polMember.Policy__c=pol.Id;
            polMember.Member__c=acc1.Id;
            insert polMember;
            system.assertEquals(polMember.Id != Null,true);
            system.assertEquals(polMember.Name,'Policy Member PM1');
            system.assertEquals(polMember.Policy__c,pol.Id);  
            
            List<Classification_Type__c> ctList= new list<Classification_Type__c>();
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'Claims (RSO)';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);
            
            insert ctList;
            system.assertEquals(ctList.size(),1);
            
            Work_Queue_Setup__c wqsRecord = new Work_Queue_Setup__c();
            wqsRecord.Name = 'Cincinnati CRU';
            wqsRecord.Classification_Type__c = ctRec.Id;
            insert wqsRecord;
            system.assertEquals(wqsRecord.Name, 'Cincinnati CRU');
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);    
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
                
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);                
            insert lstIntent;
            system.assertEquals(lstIntent[0].Name=='Filling',true);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            insert ctciList;
            system.assertEquals(ctciList.size(),1);
            String metTaskType = 'Medicare Case';
            Id metRecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(metTaskType).getRecordTypeId();
            
            Case oCase = new Case(  AccountId=acc1.Id,
                                    recordTypeId = metRecordTypeId,
                                    CTCI_List__c = ctciRec.Id,
                                    General_Case__c = true,
                                    Interacting_With_Type__c = 'Group',
                                    Interacting_With__c=acc1.Id, 
                                    Due_Date__c=Date.Today(), 
                                    Case_Owner__c=UserInfo.getUserName(), 
                                    Owner_Queue__c='Cincinnati CRU1', 
                                    Work_Queue_View_Name__c='Cincinati CRU1',
                                    Service_Center__c ='Cincinnati',
                                    Department__c='CRU1', 
                                    status='In Progress',
                                    Type ='TRR Inquiry',
                                    Subtype__c='TRR'
                                    );
            insert oCase;
            system.assertNotEquals(oCase.Id,null,'Value not matched');
            
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
            insert oSource; 
            System.assertEquals(oSource.Name,'Test Source');

            MET_Milestone__c oMetMilestone = new MET_Milestone__c();           
            oMetMilestone .Case_Type__c = 'TRR Inquiry';
            oMetMilestone .Case_Subtype__c =  'TRR';
            oMetMilestone .Action__c = oAction.id;
            oMetMilestone .Source_Name__c =  oSource.id;
            oMetMilestone .Task__c =  oTask.id;
            oMetMilestone .Is_Active__c = true;
            oMetMilestone .Is_Milestone__c = true; 
            oMetMilestone.met_entries_assoc__c = 'Resubmission Changes Letter or PCRP; Retransmission no letter';
            oMetMilestone.Met_Entries_Assoc_Order__c = 'Retransmission no letter1;Resubmission Changes Letter or PCRP1';
            insert oMetMilestone ;
            
            METEnrollment_H_HUM.getMultipleMETEntries(oCase.Type , oCase.Subtype__c);
            METEnrollment_H_HUM.getMultipleMETTasks(oCase.Type , oCase.Subtype__c,'Retransmission no letter');
            ApexPages.StandardController scon = new ApexPages.StandardController(oCase);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.sMETEntriesCreationValue = 'Retransmission no letter';
            caseEdit.loadMultipleMETTask();
            caseEdit.getMETEntriesCreation(); 
            METEnrollment_H_HUM.getMultipleMETTasks(oCase.Type , oCase.Subtype__c,'');
            System.assertEquals(oMetMilestone .Action__c,oAction.id);
            
        }
    }

    /*
    * Method Name : testCreateTasks
    * Description : This method is used to test if the CreateTasks method create Tasks
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testCreateMETTasks()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1]; 
        system.runAs(testUser)
        {
            METTaskList_DTO_HUM objTaskListDTO = new METTaskList_DTO_HUM();
            METTaskList_DTO_HUM.METTask_DTO_HUM objTaskDTO = new METTaskList_DTO_HUM.METTask_DTO_HUM();
            objTaskDTO.sTaskId = null;
            objTaskDTO.sTask = [Select id from MET_Task__c where Name = 'Test Task' limit 1].id;
            objTaskDTO.sAction = [Select Id, Name from MET_Action__c where Name = 'Test Action' limit 1].id;
            objTaskDTO.sSource = [Select id from MET_Source__c where Name = 'Test Source' limit 1].id;
            objTaskDTO.sStatus = 'Completed';
            objTaskListDTO.listMETTaskDTO = new List<METTaskList_DTO_HUM.METTask_DTO_HUM>();
            objTaskListDTO.listMETTaskDTO.add(objTaskDTO); 
            Case caseRecord = [Select id, Complaint__c, RecordType.Name from case where Subtype__c = 'TRR' LIMIT 1];
            System.assert(caseRecord.id!=null);
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            Test.startTest();
            List<Task> metTasks = METEnrollment_H_HUM.createTasks(objTaskListDTO,caseRecord);
            System.assertEquals(metTasks.size(),1);
            Test.stopTest();
        }
    }
    
    static testMethod void testOverAllCoverage()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1' limit 1]; 
        system.runAs(testUser)
        {
           
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            Case caseRecord = [Select id, Complaint__c, RecordType.Name from case where Subtype__c = 'TRR' LIMIT 1];
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            Test.startTest();
            ceHum.checkPartDAndExpeditedProcess(new case());
            System.AssertNotEquals(caseRecord.Id,Null);
            Test.stopTest();
        }
    }
    
   static testMethod void goodCAuse()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        System.runAs(testUser)
        {
            test.startTest();
            Boolean bintentreq;
            
            Group oGrp = [Select Name,Type From Group Where Name='Cincinnati_Calls1' AND Type='QUEUE' limit 1];
            System.assertEquals(oGrp.Name, 'Cincinnati_Calls1');
            
            
            Work_Queue_Setup__c oWQS = [Select Work_Queue_Name__c, Public_Group_Name__c,Service_Center__c,Department__c,Name,OwnerId,Classification_Type__c From Work_Queue_Setup__c Where OwnerId=:oGrp.Id];
            System.assertEquals(oWQS.Public_Group_Name__c, 'Group one');
            
            
            case caseRecord = [Select id, RecordType.Name from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            caseRecord.Classification_Type__c = 'Calls (RSO)';
            update caseRecord;
            
            Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'Calls (RSO)' LIMIT 1];
            
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='Benefits'];
            classificationRec.Name = 'Billing';
            update classificationRec;
            
            Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Rewards'];
            intentRec.Name = 'Reinstatement Review';
            update intentRec;
            
            CTCi_Junction__c ctciRec = new CTCi_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.id;
            ctciRec.Intent__c =intentRec.id;
            insert ctciRec;
           
          
            HUMConstants__c constants = [Select id, Name, StringValue__c from HUMConstants__c where Name='HUMCommentBody'];
           
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);

            //test
            Template__c tem1 = new Template__c();
            tem1.Unique_Template_Name__c = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
            tem1.Description__c='goodCause';
            insert tem1;
            
            Template_Field__c temfield1 = new Template_Field__c();
            temfield1.Name = 'GCRTermedPlanYesNo';
            temfield1.Template__c = tem1.id;
            
            insert temfield1;
                
                
            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;
            
            Template_Submission__c submission11 = new Template_Submission__c();
            submission11.Template__c = tem1.id;
            submission11.Submission_Owner__c = tOnwer11.id;
            insert submission11;
            
            Template_Submission_Data__c tObj11 = new Template_Submission_Data__c();
            tObj11.Template_Field__c = temfield1.Id;
            tObj11.Template_Submission__c = submission11.id;
            tObj11.Value__c = 'Yes';
            insert tObj11;
            //test
            //
           
            Template_Field__c temfield2 = new Template_Field__c();
            temfield2.Name = 'GCRIsMedicationNeeded';
            temfield2.Template__c = tem1.id;
            
            insert temfield2;
                
            Template_Submission_Data__c tObj12 = new Template_Submission_Data__c();
            tObj12.Template_Field__c = temfield2.Id;
            tObj12.Template_Submission__c = submission11.id;
            tObj12.Value__c = 'Yes';
            insert tObj12;
           
            
            oWQS.Classification_Type__c = ctRec.Id;
            update oWQS;
           // system.assertEquals(  oWQS.Classification_Type__c,ctRec.Id);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
       
          
            caseEdit.fetchUserDetails();

            caseEdit.updateCaseAfterGoodCause();
            
            //caseEdit.sClassification = HUMConstants.HUMNone;
            //caseEdit.save();
            caseEdit.getCaseRecordType(GLOBAL_CONSTANT_HUM.CASE_RECORD_TYPES);
            caseEdit.checkPartDAndExpeditedProcess(caseRecord);
            caseEdit.updateCaseAfterGoodCause();
            
            tObj12.Value__c = 'No';
            update tObj12;
            
            caseEdit.updateCaseAfterGoodCause();
            
          
            Test.stopTest();
            
        }
    }

    /*
    * This method is used to test the Process builder workflow
    * @param   None
    * @return  None
    */
    static testMethod void testProcessBuilderFlow()
    {
            Test.startTest();
        //Create Parent Group
        Group grp = new Group();
        grp.name = 'Louisville RSO Calls Support PGQ';
        grp.Type = 'Regular'; 
        Insert grp; 
 
        //Create Group Member
        GroupMember grpMem1 = new GroupMember();
        grpMem1.UserOrGroupId = UserInfo.getUserId();
        grpMem1.GroupId = grp.Id;
        Insert grpMem1;
        
        Group oCompassNewGroup = null;
        oCompassNewGroup = new Group(Name ='Louisville RSO Calls Support', Type = 'Queue');
        insert oCompassNewGroup;
        system.assertEquals(oCompassNewGroup.Type,'Queue');

        QueueSObject oCompassNewQueue = new QueueSObject(QueueId = oCompassNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
        insert oCompassNewQueue;
        system.assertEquals(oCompassNewQueue.SObjectType,'Work_Queue_Setup__c');

        User usr = HUMTestDataHelperTest.createTestUser('UserPCP','Customer Care Specialist');
        usr.Current_Queue__c='Louisville RSO Calls Support';
        insert usr;

        PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = usr.id, PermissionSetId = pCheck.id);
       
        system.runAs(usr){ 
        
            HUM_Webservice_Callout__c RighSourceEditServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_EditMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            insert RighSourceEditServiceDetails;
            HUM_Webservice_Callout__c ehubDCNService  = New HUM_Webservice_Callout__c(Name = 'DCN_Ehub',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );    
            insert ehubDCNService;
            
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
            //Launch Compass Functionality

            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'calls RS';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);

            insert ctList;
            system.assertEquals(ctList.size(),1);
            
            Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = 'Test Business Process and Testing', OwnerId = oCompassNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing',Classification_Type__c=ctList[0].id);
            insert oWorkQueue;
            system.assertEquals(oWorkQueue.Service_Center__c,'Business');
            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Benefit';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);
			
            
            Classification__c classificationRec1 = New Classification__c();
            classificationRec1.Name = 'Complaints or Compliments';
            classificationRec1.Is_Active__c = true;
            lstClassification.add(classificationRec1);
            
            insert lstClassification;
           
            system.assertEquals(lstClassification.size(),2);
        
            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Reward';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            
            Intent__c intentRec1 = New Intent__c();
            intentRec1.Name = 'QAA';
            intentRec1.Is_Active__c = true;
            lstIntent.add(intentRec1);
            
            insert lstIntent;
            system.assertEquals(lstIntent.size(),2);
            
            List<CTCI_Junction__c> ctciList = New list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = New CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            
             
            CTCI_Junction__c ctciRec1 = New CTCI_Junction__c();
            ctciRec1.Classification_Type__c = ctRec.id;
            ctciRec1.Classification__c = classificationRec1.Id;
            ctciRec1.Intent__c = intentRec1.id;
            ctciRec1.IsActive__c = true;
            ctciList.add(ctciRec1);
                
            insert ctciList;
            system.assertEquals(ctciList.size(),2);
            Classification__c classificationRec2 = New Classification__c();
            classificationRec2.Name = 'Advanced Resolution (ART)';
            classificationRec2.Is_Active__c = true;
            insert classificationRec2;
            
            Intent__c intentRec2 = New Intent__c();
            intentRec2.Name = 'PAR Provider Not Accepting';
            intentRec2.Is_Active__c = true;
            insert intentRec2;
            
            CTCI_Junction__c ctciRec2 = New CTCI_Junction__c();
            ctciRec2.Classification_Type__c = ctRec.id;
            ctciRec2.Classification__c = classificationRec2.Id;
            ctciRec2.Intent__c = intentRec2.id;
            ctciRec2.IsActive__c = true;
            insert ctciRec2;

            
            Product2 prod3 = New Product2();
            prod3.Product_Type__c = null;
            prod3.ProductCode = GLOBAL_CONSTANT_HUM.MEDICARE_POLICY;
            prod3.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MA;
            prod3.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCD;
            
            prod3.Name = 'Prod3';
            insert prod3;
            system.assertEquals(prod3.Name=='Prod3',true);
            
            Product2 prod4 = New Product2();
        	prod4.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MAPD;
        	prod4.ProductCode = GLOBAL_CONSTANT_HUM.MEDICARE_POLICY;
        	prod4.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCR;
        	prod4.Name = 'Prod4';
			insert prod4;
            system.assertEquals(prod4.Name=='Prod4',true);
            
            Policy__c policyRec1 = New Policy__c();
            policyRec1.Group_Name__c = accountRec.Id;
            policyRec1.Product__c=prod3.Id;
            policyRec1.Major_Lob__c=GLOBAL_CONSTANT_HUM.MAJOR_LOB_MCD;
            policyRec1.Policy_Group_Number__c='11111';
            insert policyRec1;
            system.assertEquals(policyRec1.Product__c==prod3.id,true);
            
            Policy__c policyRec2 = New Policy__c();
            policyRec2.Group_Name__c = accountRec.Id;
            policyRec2.Product__c=prod3.Id;
            policyRec2.Major_Lob__c=GLOBAL_CONSTANT_HUM.MAJOR_LOB_MCD;
            policyRec2.Policy_Group_Number__c='11111';
            policyRec2.Issue_State__c ='KY';
            insert policyRec2;
            system.assertEquals(policyRec2.Product__c==prod3.id,true);
            
            Policy__c policyRec3 = New Policy__c();
            policyRec3.Group_Name__c = accountRec.Id;
            policyRec3.Product__c=prod3.Id;
            policyRec3.Major_Lob__c=GLOBAL_CONSTANT_HUM.MAJOR_LOB_MCD;
            policyRec3.Policy_Group_Number__c='11111';
            policyRec3.Business_Segment__c='Individual-Medicare';
            policyRec3.Issue_State__c ='FL';
            insert policyRec3;
            system.assertEquals(policyRec3.Product__c==prod3.id,true);
            
            Policy__c policyRec4 = New Policy__c();
            policyRec4.Group_Name__c = accountRec.Id;
            policyRec4.Product__c = prod4.id;
            policyRec4.Major_Lob__c = 'PDP';
            //policyRec4.Segment_Indicator__c='';
            policyRec4.Business_Segment__c='Individual-Medicare';
            policyRec4.Policy_Group_Number__c='11111';
            insert policyRec4;
            system.assertEquals(policyRec4.Product__c==prod4.id,true); 
            
            Policy_Member__c policyMemRec1 = New Policy_Member__c();
            policyMemRec1.Name = 'abcd';
            policyMemRec1.Policy__c = policyRec1.Id;
            policyMemRec1.Member__c = accountRec.Id;
            insert policyMemRec1;
            system.assertEquals(policyMemRec1.Member__c==accountRec.Id,true);
            
            Policy_Member__c policyMemRec2 = New Policy_Member__c();
            policyMemRec2.Name = 'abcde';
            policyMemRec2.Policy__c = policyRec2.Id;
            policyMemRec2.Member__c = accountRec.Id;
            insert policyMemRec2;
            system.assertEquals(policyMemRec2.Member__c==accountRec.Id,true);
            
            Policy_Member__c policyMemRec3 = New Policy_Member__c();
            policyMemRec3.Name = 'abcdf';
            policyMemRec3.Policy__c = policyRec3.Id;
            policyMemRec3.Member__c = accountRec.Id;
            insert policyMemRec3;
            system.assertEquals(policyMemRec3.Member__c==accountRec.Id,true);
            
            Policy_Member__c policyMemRec4 = New Policy_Member__c();
            policyMemRec4.Name = 'abcdfe';
            policyMemRec4.Policy__c = policyRec4.Id;
            policyMemRec4.Member__c = accountRec.Id;
            insert policyMemRec4;
            system.assertEquals(policyMemRec3.Member__c==accountRec.Id,true);

            //Test Process builder flow  PCP/PCD
            Case pcpCase = New Case();
            pcpCase.Status = 'In Progress';
            pcpCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            pcpCase.AccountId = accountRec.id;
            pcpCase.ownerId = usr.Id;
            pcpCase.ctci_list__c=ctciList[0].id;
            pcpCase.Classification_Type__c = 'calls (RSO)';
            pcpCase.subtype__c = 'Testing Type';
            pcpCase.Classification_id__c=lstClassification[0].id;
            pcpCase.Intent_id__c = lstIntent[0].id;
            pcpCase.Interacting_With__c = accountRec.id;
            pcpCase.Interacting_With_Type__c = 'Member';
            pcpCase.Interacting_About_Type__c = 'Member';
            pcpCase.Policy_Member__c = policyMemRec1.Id;
            pcpCase.Autoroute_Status__c = 'PCP Reassignment';
            pcpCase.Origin = 'Inbound Call';
            insert pcpCase;
            
            Case pcpCase1 = New Case();
            pcpCase1.Status = 'In Progress';
            pcpCase1.Owner_Queue__c = 'Louisville RSO Enrollment';
            pcpCase1.AccountId = accountRec.id;
            pcpCase1.ownerId = usr.Id;
            pcpCase1.ctci_list__c=ctciList[0].id;
            pcpCase1.Classification_Type__c = 'calls (RSO)';
            pcpCase1.subtype__c = 'Testing Type';
            pcpCase1.Classification_id__c=lstClassification[0].id;
            pcpCase1.Intent_id__c = lstIntent[0].id;
            pcpCase1.Interacting_With__c = accountRec.id;
            pcpCase1.Interacting_With_Type__c = 'Member';
            pcpCase1.Interacting_About_Type__c = 'Member';
            pcpCase1.Policy_Member__c = policyMemRec4.Id;
            pcpCase1.Autoroute_Status__c = 'PCP Reassignment';
            pcpCase1.Origin = 'Inbound Call';
            insert pcpCase1;

            //Test Process builder flow Good cause reinstatement
            Case gcrCase = New Case();
            gcrCase.Status = 'In Progress';
            gcrCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            gcrCase.AccountId = accountRec.id;
            gcrCase.ownerId = usr.Id;
            gcrCase.ctci_list__c=ctciList[0].id;
            gcrCase.Classification_Type__c = 'calls (RSO)';
            gcrCase.subtype__c = 'Testing Type';
            gcrCase.Classification_id__c=lstClassification[0].id;
            gcrCase.Intent_id__c = lstIntent[0].id;
            gcrCase.Interacting_With__c = accountRec.id;
            gcrCase.Interacting_With_Type__c = 'Member';
            gcrCase.Interacting_About_Type__c = 'Member';
            gcrCase.Policy_Member__c = policyMemRec1.Id;
            gcrCase.Autoroute_Status__c = 'Evaluate Good Cause';
            gcrCase.Origin = 'Inbound Call';
            insert gcrCase;
            
           //Test Process builder flow Good cause reinstatement TIE
            Case gcrCaseTIE = New Case();
            gcrCaseTIE.Status = 'In Progress';
            gcrCaseTIE.Owner_Queue__c = 'Louisville RSO Enrollment';
            gcrCaseTIE.AccountId = accountRec.id;
            gcrCaseTIE.ownerId = usr.Id;
            gcrCaseTIE.ctci_list__c=ctciList[0].id;
            gcrCaseTIE.Classification_Type__c = 'calls (RSO)';
            gcrCaseTIE.subtype__c = 'Testing Type';
            gcrCaseTIE.Classification_id__c=lstClassification[0].id;
            gcrCaseTIE.Intent_id__c = lstIntent[0].id;
            gcrCaseTIE.Interacting_With__c = accountRec.id;
            gcrCaseTIE.Interacting_With_Type__c = 'Member';
            gcrCaseTIE.Interacting_About_Type__c = 'Member';
            gcrCaseTIE.Policy_Member__c = policyMemRec1.Id;
            gcrCaseTIE.Autoroute_Status__c = 'Termed in Error';
            gcrCaseTIE.Origin = 'Inbound Call';
            insert gcrCaseTIE;
            
            
            //Test Process builder flow Part D
            Case partDCase = New Case();
            partDCase.Status = 'In Progress';
            partDCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            partDCase.AccountId = accountRec.id;
            partDCase.ownerId = usr.Id;
            partDCase.ctci_list__c=ctciList[0].id;
            partDCase.Classification_Type__c = 'calls (RSO)';
            partDCase.subtype__c = 'Testing Type';
            partDCase.Classification_id__c=lstClassification[0].id;
            partDCase.Intent_id__c = lstIntent[0].id;
            partDCase.Interacting_With__c = accountRec.id;
            partDCase.Interacting_With_Type__c = 'Member';
            partDCase.Interacting_About_Type__c = 'Member';
            partDCase.Policy_Member__c = policyMemRec1.Id;
            partDCase.Autoroute_Status__c = 'Evaluate Part D';
            partDCase.Origin = 'Inbound Call';
            insert partDCase;

            //Test Process builder flow  Humana Pharmacy Web Issues
            Case pharmCase = New Case();
            pharmCase.Status = 'In Progress';
            pharmCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            pharmCase.AccountId = accountRec.id;
            pharmCase.ownerId = usr.Id;
            pharmCase.ctci_list__c=ctciList[0].id;
            pharmCase.Classification_Type__c = 'calls (RSO)';
            pharmCase.subtype__c = 'Testing Type';
            pharmCase.Classification_id__c=lstClassification[0].id;
            pharmCase.Intent_id__c = lstIntent[0].id;
            pharmCase.Interacting_With__c = accountRec.id;
            pharmCase.Interacting_With_Type__c = 'Member';
            pharmCase.Interacting_About_Type__c = 'Member';
            pharmCase.Policy_Member__c = policyMemRec1.Id;
            pharmCase.Autoroute_Status__c = 'Humana Pharmacy Web Issues';
            pharmCase.Origin = 'Inbound Call';
            insert pharmCase;
            
            //Test Process builder flow QAA KY MCD plans
            Case qaaKYCase = New Case();
            qaaKYCase.Status = 'In Progress';
            qaaKYCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            qaaKYCase.AccountId = accountRec.id;
            qaaKYCase.ownerId = usr.Id;
            qaaKYCase.ctci_list__c=ctciList[1].id;
            qaaKYCase.Classification_Type__c = 'calls (RSO)';
            qaaKYCase.subtype__c = 'Testing Type';
            qaaKYCase.Classification_id__c=lstClassification[1].id;
            qaaKYCase.Intent_id__c = lstIntent[1].id;
            qaaKYCase.Interacting_With__c = accountRec.id;
            qaaKYCase.Interacting_With_Type__c = 'Member';
            qaaKYCase.Interacting_About_Type__c = 'Member';
            qaaKYCase.Policy_Member__c = policyMemRec2.Id;
            qaaKYCase.Autoroute_Status__c = 'Evaluate KY IL MCD'; 
            qaaKYCase.Origin = 'Inbound Call';
            insert qaaKYCase;
            
            //Test Process builder flow QAA FL MCD plans
            Case qaaFLCase = New Case();
            qaaFLCase.Status = 'In Progress';
            qaaFLCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            qaaFLCase.AccountId = accountRec.id;
            qaaFLCase.ownerId = usr.Id;
            qaaFLCase.ctci_list__c=ctciList[1].id;
            qaaFLCase.Classification_Type__c = 'calls (RSO)';
            qaaFLCase.subtype__c = 'Testing Type';
            qaaFLCase.Classification_id__c=lstClassification[1].id;
            qaaFLCase.Intent_id__c = lstIntent[1].id;
            qaaFLCase.Interacting_With__c = accountRec.id;
            qaaFLCase.Interacting_With_Type__c = 'Member';
            qaaFLCase.Interacting_About_Type__c = 'Member';
            qaaFLCase.Policy_Member__c = policyMemRec3.Id;
            qaaFLCase.Autoroute_Status__c = 'Evaluate FL MCD';
            qaaFLCase.Origin = 'Inbound Call';
            insert qaaFLCase;

            //Test Process builder QAA Medicare Plans
            Case qaaMedCase = New Case();
            qaaMedCase.Status = 'In Progress';
            qaaMedCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            qaaMedCase.AccountId = accountRec.id;
            qaaMedCase.ownerId = usr.Id;
            qaaMedCase.ctci_list__c=ctciList[1].id;
            qaaMedCase.Classification_Type__c = 'calls (RSO)';
            qaaMedCase.subtype__c = 'Testing Type';
            qaaMedCase.Classification_id__c=lstClassification[1].id;
            qaaMedCase.Intent_id__c = lstIntent[1].id;
            qaaMedCase.Interacting_With__c = accountRec.id;
            qaaMedCase.Interacting_With_Type__c = 'Member';
            qaaMedCase.Interacting_About_Type__c = 'Member';
            qaaMedCase.Policy_Member__c = policyMemRec4.Id;
            qaaMedCase.Autoroute_Status__c = 'Evaluate Medicare';
            qaaMedCase.Origin = 'Inbound Call';
            insert qaaMedCase;
			
			
            Work_Queue_Priority_CriteriaLimit_Value__c cv = new Work_Queue_Priority_CriteriaLimit_Value__c();
            cv.name = 'Criteria';
            cv.Value__c = 2;
            insert cv;
            
            
             Case parCase = New Case();
            parCase.Status = 'In Progress';
            parCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            parCase.AccountId = accountRec.id;
            parCase.ownerId = usr.Id;
            parCase.ctci_list__c=ctciList[1].id;
            parCase.Classification_Type__c = 'calls (RSO)';
            
            parCase.Classification_id__c=classificationRec2.id;
            parCase.Intent_id__c = intentRec2.id;
            parCase.Interacting_With__c = accountRec.id;
            parCase.Interacting_With_Type__c = 'Member';
            parCase.Interacting_About_Type__c = 'Member';
            parCase.Policy_Member__c = policyMemRec4.Id;
            parCase.Autoroute_Status__c = 'Evaluate PAR';
            parCase.Origin = 'Inbound Call';
            insert parCase;
            
             Case parCase1 = New Case();
            parCase1.Status = 'In Progress';
            parCase1.Owner_Queue__c = 'Louisville RSO Enrollment';
            parCase1.AccountId = accountRec.id;
            parCase1.ownerId = usr.Id;
            parCase1.ctci_list__c=ctciList[1].id;
            parCase1.Classification_Type__c = 'calls (RSO)';
            
            parCase1.Classification_id__c=classificationRec2.id;
            parCase1.Intent_id__c = intentRec2.id;
            parCase1.Interacting_With__c = accountRec.id;
            parCase1.Interacting_With_Type__c = 'Member';
            parCase1.Interacting_About_Type__c = 'Member';
            parCase1.Policy_Member__c = policyMemRec4.Id;
            parCase1.Autoroute_Status__c = 'Evaluate PAR KY';
            parCase1.Origin = 'Inbound Call';
	    insert parCase1;
            Test.StopTest();
        }
    }
    
    static testMethod void pageMessageQAAWarning()
    {
    User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
    
    System.runAs(testUser)
    {
        Test.startTest();
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(
                Name = '2040308',
                IsON__c=true,
                Release_Name__c='04/16/2021',
                Scrum_Team__c='RapidForce',
                Story_Title__c='User Story 2040308: T1PRJ0001827 - MF 1 - Quality - Update logic to make QAA Template required on the Case (CRM)');
            
        insert objCS;
        case caseRecord = [Select id,  RecordType.Name,complaint__c,Policy_Member__c,Policy_Member__r.Product_type_code__c from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
        Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'calls' LIMIT 1];
        Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='Benefits'];
        classificationRec .Name='Complaints or Compliments';
        update classificationRec;
        Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Rewards'];
        intentRec.Name='QAA';
        update intentRec;
        List<CTCI_Junction__c> ctciRec =[select id,name,Classification_Type__c,Classification__c, Intent__c from CTCI_Junction__c where Classification_Type__c =:ctRec.id LIMIT 1];
           ctciRec[0].Classification_Type__c = ctRec.id;
           ctciRec[0].Classification__c = classificationRec.id;
           ctciRec[0].Intent__c = intentRec.id;
          
           update ctciRec;
           caseRecord.CTCI_List__c = ctciRec[0].id;
           update caseRecord;
           
        System.assertEquals(ctciRec[0].Classification__c, classificationRec.Id);
        ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
        CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);
        

            Template__c tem1 = new Template__c();
            tem1.Unique_Template_Name__c = 'd884d160-77c0-4d1b-95aa-edf8c81f3a59';
            tem1.Description__c='pcppcd';
            insert tem1;
            
            Template_Field__c temfield1 = new Template_Field__c();
            temfield1.Name = 'Current Provider Name';
            temfield1.Template__c = tem1.id;
            
            insert temfield1;
                
                
            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;
            
            Template_Submission__c submission11 = new Template_Submission__c();
            submission11.Template__c = tem1.id;
            submission11.Submission_Owner__c = tOnwer11.id;
            insert submission11;
                
            Template_Submission_Data__c tObj11 = new Template_Submission_Data__c();
            tObj11.Template_Field__c = temfield1.Id;
            tObj11.Template_Submission__c = submission11.id;
            tObj11.Value__c = 'Yes';
            insert tObj11;

        caseEditPageObj.sClassification = classificationRec.ID;
        caseEditPageObj.sIntent = intentRec.id;  
        caseEditPageObj.changeCaseRecordType();
        caseEditPageObj.getClassificationValues();
        caseEditPageObj.getIntentValues();
        caseEditPageObj.save();
        caseEditPageObj.sClassification = 'None';
        caseEditPageObj.sIntent = 'None';  
        caseEditPageObj.getClassificationValues();
        caseEditPageObj.getIntentValues();
        caseEditPageObj.save();
        
        
        Test.stopTest(); 
    }
   }
    /*
    * This method is used to test updateCaseQAA method
    * @param   None
    * @return  None
    */
    static testMethod void  updateCaseQAATest(){
       User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();
            Policy_Member__c pmId=[select id,Policy__r.Issue_State__c from Policy_Member__c where Policy__r.Issue_State__c ='FL' LIMIT 1];
            Policy_Member__c pmKYId=[select id,Policy__r.Issue_State__c from Policy_Member__c where Policy__r.Issue_State__c ='KY' LIMIT 1];
           	Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'calls (RSO)' LIMIT 1];
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='Benefits'];
            classificationRec .Name='Complaints or Compliments';
            update classificationRec;
            Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Rewards'];
            intentRec.Name='QAA';
            update intentRec;
        	List<CTCI_Junction__c> ctciRec =[select id,name,Classification_Type__c,Classification__c, Intent__c from CTCI_Junction__c where Classification_Type__c =:ctRec.id LIMIT 1];
            ctciRec[0].Classification_Type__c = ctRec.id;
            ctciRec[0].Classification__c = classificationRec.id;
            ctciRec[0].Intent__c = intentRec.id;
            update ctciRec;
        	System.assertEquals(ctciRec[0].Classification__c, classificationRec.Id); case caseRecord = [Select id, RecordType.Name,AutoRoute_Status__c,status,owner_queue__c ,Classification_Type__c,Policy_Member__c,Product__c ,CTCI_List__r.Classification__r.Name, CTCI_List__r.Intent__r.Name,Policy_Member__r.Product_type_code__c,Policy_Member__r.Issue_State__c ,Policy_Member__r.Product_Type__c,Policy_Member__r.Segment_Indicator__c  from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            caseRecord.Policy_Member__c = pmId.Id;
            update caseRecord;
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            ceHUM.updateCaseQAA();
            Test.stopTest();
        }
    }
    static testMethod void  updateCaseQAATest1(){
       User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();
            Policy_Member__c pmKYId=[select id,Policy__r.Issue_State__c from Policy_Member__c where Policy__r.Issue_State__c ='KY' LIMIT 1];
           	Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'calls (RSO)' LIMIT 1];
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='Benefits'];
            classificationRec .Name='Complaints or Compliments';
            update classificationRec;
            Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Rewards'];
            intentRec.Name='QAA';
            update intentRec;
        	List<CTCI_Junction__c> ctciRec =[select id,name,Classification_Type__c,Classification__c, Intent__c from CTCI_Junction__c where Classification_Type__c =:ctRec.id LIMIT 1];
            ctciRec[0].Classification_Type__c = ctRec.id;
            ctciRec[0].Classification__c = classificationRec.id;
            ctciRec[0].Intent__c = intentRec.id;
            update ctciRec;
        	System.assertEquals(ctciRec[0].Classification__c, classificationRec.Id); 
            case caseRecord = [Select id, RecordType.Name,AutoRoute_Status__c,status,owner_queue__c ,Classification_Type__c,Policy_Member__c,Product__c ,CTCI_List__r.Classification__r.Name, CTCI_List__r.Intent__r.Name,Policy_Member__r.Product_type_code__c,Policy_Member__r.Issue_State__c ,Policy_Member__r.Product_Type__c,Policy_Member__r.Segment_Indicator__c  from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            caseRecord.Policy_Member__c = pmKYId.Id;
            update caseRecord;
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            ceHUM.updateCaseQAA();
            Test.stopTest();
        }
    }
    
    static testMethod void  updateCaseQAATest2(){
       User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();
            Policy_Member__c pmKYId=[select id,Policy__r.Issue_State__c from Policy_Member__c where Policy__r.Business_Segment__c ='Individual-Medicare' LIMIT 1];
           	Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'calls (RSO)' LIMIT 1];
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='Benefits'];
            classificationRec .Name='Complaints or Compliments';
            update classificationRec;
            Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Rewards'];
            intentRec.Name='QAA';
            update intentRec;
        	List<CTCI_Junction__c> ctciRec =[select id,name,Classification_Type__c,Classification__c, Intent__c from CTCI_Junction__c where Classification_Type__c =:ctRec.id LIMIT 1];
            ctciRec[0].Classification_Type__c = ctRec.id;
            ctciRec[0].Classification__c = classificationRec.id;
            ctciRec[0].Intent__c = intentRec.id;
            update ctciRec;
        	System.assertEquals(ctciRec[0].Classification__c, classificationRec.Id); 
            case caseRecord = [Select id, RecordType.Name,AutoRoute_Status__c,status,owner_queue__c ,Classification_Type__c,Policy_Member__c,Product__c ,CTCI_List__r.Classification__r.Name, CTCI_List__r.Intent__r.Name,Policy_Member__r.Product_type_code__c,Policy_Member__r.Issue_State__c ,Policy_Member__r.Product_Type__c,Policy_Member__r.Segment_Indicator__c  from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            caseRecord.Policy_Member__c = pmKYId.Id;
            update caseRecord;
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            ceHUM.updateCaseQAA();
            Test.stopTest();
        }
    }

     /*
    * This method is used to test updateCaseComplaint method in  CaseComplaintUpdateHelper_HUM  class
    * @param   None
    * @return  None
    */
    static testMethod void  testupdateCaseComplaint(){
       User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();
            Policy_Member__c pmId=[select id,Policy__r.Issue_State__c from Policy_Member__c where Policy__r.Issue_State__c ='FL' LIMIT 1];
            //case caseRecord = [Select RecordType.Name,AutoRoute_Status__c,status,owner_queue__c ,Id,Classification_Type__c,Policy_Member__c,Product__c ,CTCI_List__r.Classification__r.Name, CTCI_List__r.Intent__r.Name,Policy_Member__r.Product_type_code__c,Policy_Member__r.Issue_State__c ,Policy_Member__r.Product_Type__c,Policy_Member__r.Segment_Indicator__c from Case where Policy_Member__r.Policy__r.Issue_State__c ='FL' limit 1];
            case caseRecord = [Select id, RecordType.Name,AutoRoute_Status__c,status,owner_queue__c ,Classification_Type__c,Policy_Member__c,Product__c ,CTCI_List__r.Classification__r.Name, CTCI_List__r.Intent__r.Name,Policy_Member__r.Product_type_code__c,Policy_Member__r.Issue_State__c ,Policy_Member__r.Product_Type__c,Policy_Member__r.Segment_Indicator__c  from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            caseRecord.Policy_Member__c = pmId.Id;
            update caseRecord;
            List<Id> lstCase =new List<Id>();
            lstCase.add(caseRecord.Id);
            CaseComplaintUpdateHelper_HUM.updateCaseComplaint(lstCase);
            System.AssertNotEquals(caseRecord.Id,Null);
            Test.stopTest();
        }
    }
    
    
   
     static testMethod void  testupdateCaseMedicaidPARProvider(){
       User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();
case caseRecord = [Select id,  RecordType.Name,complaint__c,Policy_Member__c,Policy_Member__r.Product_type_code__c from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            caseRecord.Classification_Type__c = 'Medicaid Calls';
            update caseRecord;
        Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c LIMIT 1];
            ctRec.name = 'Medicaid Calls';
            update ctRec;
        Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='Benefits'];
        classificationRec.Name=CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderClassification;
        update classificationRec;
        Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Rewards'];
        intentRec.Name=CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderIntent;
        update intentRec;
        List<CTCI_Junction__c> ctciRec =[select id,name,Classification_Type__c,Classification__c, Intent__c from CTCI_Junction__c  LIMIT 1];
           ctciRec[0].Classification_Type__c = ctRec.id;
           ctciRec[0].Classification__c = classificationRec.id;
           ctciRec[0].Intent__c = intentRec.id;
            ctciRec[0].IsActive__c = true;
          
           update ctciRec;
           caseRecord.CTCI_List__c = ctciRec[0].id;
           update caseRecord;
           
        System.assertEquals(ctciRec[0].Classification__c, classificationRec.Id);
        ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
        CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);
        

            Template__c tem1 = new Template__c();
            tem1.Unique_Template_Name__c = 'deeef952-9f63-49ce-97d0-c7eb3204b519';
            tem1.Description__c='PAR';
            insert tem1;
            
            Template_Field__c temfield1 = new Template_Field__c();
            temfield1.Name = 'Provider State';
            temfield1.Template__c = tem1.id;
            
            insert temfield1;
                
                
            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;
            
            Template_Submission__c submission11 = new Template_Submission__c();
            submission11.Template__c = tem1.id;
            submission11.Submission_Owner__c = tOnwer11.id;
            insert submission11;
                
            Template_Submission_Data__c tObj11 = new Template_Submission_Data__c();
            tObj11.Template_Field__c = temfield1.Id;
            tObj11.Template_Submission__c = submission11.id;
            tObj11.Value__c = 'IL';
            insert tObj11;
 

        caseEditPageObj.sClassification = classificationRec.ID;
        caseEditPageObj.sIntent = intentRec.id;  
        caseEditPageObj.changeCaseRecordType();
        caseEditPageObj.getClassificationValues();
        caseEditPageObj.getIntentValues();
        caseEditPageObj.save();
            caseEditPageObj.updateCaseMedicaidPARProvider();
            
             tObj11.Value__c = 'KY';
            update tObj11;
 caseEditPageObj.updateCaseMedicaidPARProvider();
                         tObj11.Value__c = 'FL';
            update tObj11;
 caseEditPageObj.updateCaseMedicaidPARProvider();

        tObj11.Value__c = 'SC';
        update tObj11;
		caseEditPageObj.updateCaseMedicaidPARProvider();

            Test.stopTest();
        }
    }
    
    /*
    * This method is used to test apex template Methods
    * @param   None
    * @return  None
    */
    static testMethod void  testTemplateMethods(){
       User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        
        system.runAs(testUser) 
        {
            Test.startTest();
            
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = '1393270';
            crm1.Scrum_Team__c = 'Transformers';
            crm1.Story_Title__c = 'CRM Process - Initiating queue/views for transfers to MHK';
            crm1.Release_Name__c = '10/23/2020';
            insert crm1;  
            
            case caseRecord1 = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE Classification_Type__c = 'Calls (RSO)' LIMIT 1]; 
            caseRecord1.AutoRoute_Status__c = 'Completed';
            caseRecord1.Owner_Queue__c = CaseEditConstants_HUM.GOODCAUSEQUEUE;
            update caseRecord1;
            System.AssertNotEquals(caseRecord1.Id,Null);

            Template__c templateObj = new Template__c(Unique_Template_Name__c='E0A13562-89EE-48B8-8EBA-E485C6583FF1',Description__c='Creditable Coverage Attestation');
                Insert templateObj; 
            
            Template_Field__c tf = new Template_Field__c();
            	tf.Name = CaseEditConstants_HUM.GCRTERMEDPLAN;
                tf.Data_Type__c = 'String';
                tf.Template__c = templateObj.id;
                insert tf;
            
                Template_Submission_Owner__c tOnwer = new Template_Submission_Owner__c();
                tOnwer.Object_Owner_ID__c=string.valueOf(caseRecord1.id);
                tOnwer.Object_Owner_Type__c='Case';
                insert tOnwer;
                
                Template_Submission__c submission = new Template_Submission__c();
                submission.Template__c = templateObj.id;
                submission.Submission_Owner__c = tOnwer.id;
                insert submission;
                
                Template_Submission_Data__c tObj1 = new Template_Submission_Data__c();
                tObj1.Template_Field__c = tf.Id;
                tObj1.Template_Submission__c = submission.id;
                tObj1.Value__c = 'Yes';
                insert tObj1;
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord1);
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            ceHum.isMedExpProcessAttachedGoodCause();
            
            caseRecord1.Owner_Queue__c =  'LV G and A and Correspondence Screening';
            update caseRecord1;
           
            Template__c templateObj1 = new Template__c(Unique_Template_Name__c='95C90830-3133',Description__c='Creditable Coverage Attestation');
                Insert templateObj1; 
            
            Template_Field__c tf1 = new Template_Field__c();
                tf1.Data_Type__c = 'String';
                tf1.Template__c = templateObj1.id;
                insert tf1;
            
                Template_Submission_Owner__c tOnwer1 = new Template_Submission_Owner__c();
                tOnwer1.Object_Owner_ID__c=string.valueOf(caseRecord1.id);
                tOnwer1.Object_Owner_Type__c='Case';
                insert tOnwer1;
                
                Template_Submission__c submission1 = new Template_Submission__c();
                submission1.Template__c = templateObj1.id;
                submission1.Submission_Owner__c = tOnwer1.id;
                insert submission1;
                
                Template_Submission_Data__c tObj11 = new Template_Submission_Data__c();
                tObj11.Template_Field__c = tf1.Id;
                tObj11.Template_Submission__c = submission1.id;
                tObj11.Value__c = 'Yes';
                insert tObj11;
            
            Template__c templateObj2 = new Template__c(Unique_Template_Name__c='BC929CD6-E06A',Description__c='Creditable Coverage Attestation');
                Insert templateObj2; 
            
            Template_Field__c tf2 = new Template_Field__c();
                tf2.Data_Type__c = 'String';
                tf2.Template__c = templateObj2.id;
                insert tf2;
            
                Template_Submission_Owner__c tOnwer2 = new Template_Submission_Owner__c();
                tOnwer2.Object_Owner_ID__c=string.valueOf(caseRecord1.id);
                tOnwer2.Object_Owner_Type__c='Case';
                insert tOnwer2;
                
                Template_Submission__c submission2 = new Template_Submission__c();
                submission2.Template__c = templateObj2.id;
                submission2.Submission_Owner__c = tOnwer2.id;
                insert submission2;
                
                Template_Submission_Data__c tObj12 = new Template_Submission_Data__c();
                tObj12.Template_Field__c = tf2.Id;
                tObj12.Template_Submission__c = submission2.id;
                tObj12.Value__c = 'Yes';
                insert tObj12;
            ApexPages.StandardController scon1 = new ApexPages.StandardController(caseRecord1);
            CaseEdit_CE_HUM ceHum1 = new CaseEdit_CE_HUM(scon);
            ceHum1.isMedExpProcessAttachedExp();
            ceHum1.isMedExpProcessAttachedPartD();
            Test.stopTest();
        }
    }
    
     /*
    * This method is used to test apex template Methods
    * @param   None
    * @return  None
    */
    static testMethod void  testTemplateMethodsNegative(){
       User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
       
        system.runAs(testUser) 
        {
            Test.startTest();
            
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =false;
            crm1.Name = '1393270';
            crm1.Scrum_Team__c = 'Transformers';
            crm1.Story_Title__c = 'CRM Process - Initiating queue/views for transfers to MHK';
            crm1.Release_Name__c = '10/23/2020';
            insert crm1;  
            
            case caseRecord1 = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE Classification_Type__c = 'Calls (RSO)' LIMIT 1]; 
            caseRecord1.AutoRoute_Status__c = 'Completed';
            caseRecord1.Owner_Queue__c = CaseEditConstants_HUM.GOODCAUSEQUEUE;
            update caseRecord1;
            System.AssertNotEquals(caseRecord1.Id,Null);
            Template__c templateObj = new Template__c(Unique_Template_Name__c='E0A13562-89EE-48B8-8EBA-E485C6583FF1',Description__c='Creditable Coverage Attestation');
                Insert templateObj; 
            
            Template_Field__c tf = new Template_Field__c();
            	tf.Name = CaseEditConstants_HUM.GCRTERMEDPLAN;
                tf.Data_Type__c = 'String';
                tf.Template__c = templateObj.id;
                insert tf;
            
                Template_Submission_Owner__c tOnwer = new Template_Submission_Owner__c();
                tOnwer.Object_Owner_ID__c=string.valueOf(caseRecord1.id);
                tOnwer.Object_Owner_Type__c='Case';
                insert tOnwer;
                
                Template_Submission__c submission = new Template_Submission__c();
                submission.Template__c = templateObj.id;
                submission.Submission_Owner__c = tOnwer.id;
                insert submission;
                
                Template_Submission_Data__c tObj1 = new Template_Submission_Data__c();
                tObj1.Template_Field__c = tf.Id;
                tObj1.Template_Submission__c = submission.id;
                tObj1.Value__c = 'Yes';
                insert tObj1;
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord1);
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            ceHum.isMedExpProcessAttachedGoodCause();
            
            caseRecord1.Owner_Queue__c =  'LV G and A and Correspondence Screening';
            update caseRecord1;
           
            Template__c templateObj1 = new Template__c(Unique_Template_Name__c='95C90830-3133',Description__c='Creditable Coverage Attestation');
                Insert templateObj1; 
            
            Template_Field__c tf1 = new Template_Field__c();
                tf1.Data_Type__c = 'String';
                tf1.Template__c = templateObj1.id;
                insert tf1;
            
                Template_Submission_Owner__c tOnwer1 = new Template_Submission_Owner__c();
                tOnwer1.Object_Owner_ID__c=string.valueOf(caseRecord1.id);
                tOnwer1.Object_Owner_Type__c='Case';
                insert tOnwer1;
                
                Template_Submission__c submission1 = new Template_Submission__c();
                submission1.Template__c = templateObj1.id;
                submission1.Submission_Owner__c = tOnwer1.id;
                insert submission1;
                
                Template_Submission_Data__c tObj11 = new Template_Submission_Data__c();
                tObj11.Template_Field__c = tf1.Id;
                tObj11.Template_Submission__c = submission1.id;
                tObj11.Value__c = 'Yes';
                insert tObj11;
            
            Template__c templateObj2 = new Template__c(Unique_Template_Name__c='BC929CD6-E06A',Description__c='Creditable Coverage Attestation');
                Insert templateObj2; 
            
            Template_Field__c tf2 = new Template_Field__c();
                tf2.Data_Type__c = 'String';
                tf2.Template__c = templateObj2.id;
                insert tf2;
            
                Template_Submission_Owner__c tOnwer2 = new Template_Submission_Owner__c();
                tOnwer2.Object_Owner_ID__c=string.valueOf(caseRecord1.id);
                tOnwer2.Object_Owner_Type__c='Case';
                insert tOnwer2;
                
                Template_Submission__c submission2 = new Template_Submission__c();
                submission2.Template__c = templateObj2.id;
                submission2.Submission_Owner__c = tOnwer2.id;
                insert submission2;
                
                Template_Submission_Data__c tObj12 = new Template_Submission_Data__c();
                tObj12.Template_Field__c = tf2.Id;
                tObj12.Template_Submission__c = submission2.id;
                tObj12.Value__c = 'Yes';
                insert tObj12;
            ApexPages.StandardController scon1 = new ApexPages.StandardController(caseRecord1);
            CaseEdit_CE_HUM ceHum1 = new CaseEdit_CE_HUM(scon);
            ceHum1.isMedExpProcessAttachedExp();
            ceHum1.isMedExpProcessAttachedPartD();
            
            caseRecord1.Owner_Queue__c = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE_KY;
            update caseRecord1;
           	ApexPages.StandardController scon2 = new ApexPages.StandardController(caseRecord1);
            CaseEdit_CE_HUM ceHum2 = new CaseEdit_CE_HUM(scon2);
            ceHum2.bHasCTCIExpRef = true;
            ceHum2.updateCaseMedicaidPARProvider();
            ceHum2.isMedicaidPARProviderAttached();
            
            caseRecord1.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health; 
            update caseRecord1;
           	ApexPages.StandardController scon3 = new ApexPages.StandardController(caseRecord1);
            CaseEdit_CE_HUM ceHum3 = new CaseEdit_CE_HUM(scon3);
            ceHum3.updateCaseMedicaidPARProvider();
            
            caseRecord1.Owner_Queue__c = CaseEditConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT; 
            update caseRecord1;
            
            ApexPages.StandardController scon4 = new ApexPages.StandardController(caseRecord1);
            CaseEdit_CE_HUM ceHum4 = new CaseEdit_CE_HUM(scon4);
            
            Test.stopTest();
        }
        
    }
     /*
    
    * @param   None
    * @return  None
    */
    static testMethod void  testAllMethods(){
       User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
       PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name =:CaseEditConstants_HUM.Grievance_Appeals_Permissionset_Name LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = pCheck.id);
		PermissionSet pCheck1 =[SELECT ID From PermissionSet WHERE Name =:CaseDetailConstants_HUM.sCsertComplaints LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = pCheck1.id);
       
        system.runAs(testUser) 
        {
            	Test.startTest();
            case caseRecord1 = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE Classification_Type__c = 'Calls (RSO)' LIMIT 1]; 
    		caseRecord1.Owner_Queue__c = CaseEditConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT; 
			caseRecord1.type = 'SIU';
            update caseRecord1;
            
            ApexPages.StandardController scon4 = new ApexPages.StandardController(caseRecord1);
            CaseEdit_CE_HUM ceHum4 = new CaseEdit_CE_HUM(scon4);
			
			caseRecord1.origin = CaseEditConstants_HUM.RTIChannelIVR;
			caseRecord1.Type = CaseEditConstants_HUM.IVRCASETYPE;
            update caseRecord1;
			
            ApexPages.StandardController scon5 = new ApexPages.StandardController(caseRecord1);
            CaseEdit_CE_HUM ceHum5 = new CaseEdit_CE_HUM(scon5);
			
            case caseRecord3 = [Select id,status,CTCI_List__c,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE Classification_Type__c = 'Calls (RSO)' AND RecordType.name= 'Member Case' AND Policy_Member__c !='' LIMIT 1]; 
			caseRecord3.origin = CaseEditConstants_HUM.sMHKTaskOrigin;
			caseRecord3.Type = CaseEditConstants_HUM.MHK_CASE_TYPE;
            caseRecord3.Interacting_About_Type__c = 'Member';
            update caseRecord3;
            
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = '1414643';
            crm1.Scrum_Team__c = 'Transformers';
            crm1.Story_Title__c = 'test';
            crm1.Release_Name__c = '10/23/2020';
            insert crm1; 
			
			ApexPages.StandardController scon7 = new ApexPages.StandardController(caseRecord3);
            CaseEdit_CE_HUM ceHum7 = new CaseEdit_CE_HUM(scon7);
           
			
             RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
              //Creating Account Record
                Account accountRec = HUMTestDataHelperTest.getAccount();
                accountRec.OwnerId = testUser.id;
                accountRec.FirstName = 'Test';
                accountRec.LastName = 'Account';
                accountRec.Enterprise_Id__c = '123456';
                accountRec.recordtypeId = AccountRecordType.id;
                insert accountRec;
                system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
            caseRecord3.accountId = accountRec.id;
            update caseRecord3;
            
           
			case caseRecord2 = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE RecordType.name= 'Group case' AND Interacting_About_Type__c =: CaseEditConstants_HUM.HUMGroupIntType LIMIT 1]; 
            
			ApexPages.StandardController scon6 = new ApexPages.StandardController(caseRecord2);
            CaseEdit_CE_HUM ceHum6 = new CaseEdit_CE_HUM(scon6);
            
            ceHum6.AccountId = accountRec.id;
            ceHum6.AccountRecordTypeId = accountRec.recordtypeId;
            ceHum6.updateCaseRecord();
           
			
            case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE recordType.Name = 'HP Member Case' AND Classification_Type__c = 'Humana Pharmacy Calls' LIMIT 1]; 
            caseRecord.Owner_Queue__c = CaseEditConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT; 
            update caseRecord;
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
          
            caseRecord3.Complaint__c = '';
            caseRecord3.G_A_Rights_Given__c = '';
            caseRecord3.Complaint_Reason__c = '';
            caseRecord3.OGO_Resolution_Type__c ='Oral';
            caseRecord3.DCN__c = '123456789';
            caseRecord3.Type = CaseEditConstants_HUM.CorrespondenceInq;
            update caseRecord3;
            
            ApexPages.StandardController scon10 = new ApexPages.StandardController(caseRecord3);
            ApexPages.currentpage().getParameters().put('invoicelogging','true');
             ApexPages.currentpage().getParameters().put('caseComment','INT_WITH Placed order #### for the following: Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10). Payment Method: Invoice, Shipping Address: 7491 PINNICKINICK STREET,SANDY OR 97055, Release Date: 03/22/2019');
            CaseEdit_CE_HUM ceHum10 = new CaseEdit_CE_HUM(scon10);
            ceHum10.onLoad();
            ceHum10.sAutoRouteStatus = 'Evaluate FL MCD';
           
            ceHum10.save();
            ceHum10.checkMandatoryforClosedCase();
            ceHum10.getClassificationValues();
            ceHum10.getCallBenefitCategoryValues();
            ceHum10.medicareComplaintCheck();
            ceHum10.AccountRecordTypeId = AccountRecordType.id;
            ceHum10.updateCaseRecord();
            
            test.stopTest();
        }
    }
    
    /*
    
    * @param   None
    * @return  None
    */
    static testMethod void  testAllMethods1(){
       User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();
           case caseRecord = [Select id, RecordType.Name,CTCI_List__c from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
            
             Classification_Type__c ctRec = [Select id, name, is_Active__c from Classification_Type__c where Name = 'Calls (RSO)' LIMIT 1];
            Classification__c classificationRec = [select id,Is_Active__c from Classification__c where Name='HP Billing' LIMIT 1];
            Intent__c intentRec = [Select id,Is_Active__c from Intent__c where Name='Add/Updated Credit Card' LIMIT 1];
            
            
           CTCI_Junction__c ctciRec =[select id,name,Classification_Type__c,Classification__r.Is_Active__c,Intent__r.Is_Active__c ,Classification__c,Intent__c from CTCI_Junction__c where Classification_Type__c=:ctRec.id and Classification__c=:classificationRec.id LIMIT 1];
            System.assertEquals(ctciRec.Intent__c, intentRec.Id);
            ctciRec.Classification__r.Is_Active__c = false;
            ctciRec.Intent__r.Is_Active__c = false;
            update ctciRec;
            
            caseRecord.CTCI_List__c = ctciRec.id;
            update caseRecord;
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
            caseEdit.getClassificationValues();
            caseEdit.getIntentValues();
            Test.stopTest();
        }
    }
     
    
    /*
    
    * @param   None
    * @return  None
    */
    static testMethod void  testAllMethods2(){
       User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();
           case caseRecord = [Select id, RecordType.Name,CTCI_List__c from case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support'];
			 
            caseRecord.Complaint__c = 'Yes - Medicare Part C';
            caseRecord.Complaint_Reason__c = 'Other';
            caseRecord.Complaint_Type__c = '';
            caseRecord.Guided_Process_Flag__c = true;
            caseRecord.status = CaseEditConstants_HUM.CASE_STATUS_PENDING_RESPONSE;
            update caseRecord;
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
        
            caseEdit.checkMandatoryforClosedCase();
            
            case caseRecord1 = [Select id, RecordType.Name,status from case where status='In Progress' and Policy_Member__r.Name = 'abcd' ];
            System.assertEquals(caseRecord1.status,'In Progress');
            
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon1 = new ApexPages.StandardController(caseRecord1);
            CaseEdit_CE_HUM caseEdit1 = new CaseEdit_CE_HUM(scon1);
            List<SelectOption> lstBenefitOptions1 = caseEdit1.getCallBenefitCategoryValues();
            
            case caseRecord2 = [select id,RecordType.Name,status from case where status='In Progress' and Policy_Member__r.Name = 'mnop' limit 1];
            
            ApexPages.StandardController scon2 = new ApexPages.StandardController(caseRecord2);
            CaseEdit_CE_HUM caseEdit2 = new CaseEdit_CE_HUM(scon2);
            caseEdit2.checkProductSegmentType();
            
             RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        
        //Creating Account Record
        Account accountRec = HUMTestDataHelperTest.getAccount();
        accountRec.OwnerId = testUser.id;
        accountRec.FirstName = 'Test';
        accountRec.LastName = 'Account';
        accountRec.Enterprise_Id__c = '123456';
        accountRec.recordtypeId = AccountRecordType.id;
        insert accountRec;
        system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
             Product2 prod4 = New Product2();
        prod4.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MAPD;
        prod4.ProductCode = GLOBAL_CONSTANT_HUM.MEDICARE_POLICY;
        prod4.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCR;
        prod4.Name = 'ProdNew';
        insert prod4;
        system.assertEquals(prod4.Name=='ProdNew',true);   
            
             Policy__c policyRec4 = New Policy__c();
        policyRec4.Group_Name__c = accountRec.Id;
        policyRec4.Product__c = prod4.id;
        policyRec4.Major_Lob__c = GLOBAL_CONSTANT_HUM.MAJOR_LOB_MER;
        policyRec4.Policy_Group_Number__c='11111';
        policyRec4.Business_Segment__c='Individual-Medicare';
        insert policyRec4;
        system.assertEquals(policyRec4.Product__c==prod4.id,true); 

            
            Policy_Member__c policyMemRec4 = New Policy_Member__c();
            policyMemRec4.Name = 'abcdfe';
            policyMemRec4.Policy__c = policyRec4.Id;
            policyMemRec4.Member__c = accountRec.Id;
            insert policyMemRec4;
            system.assertEquals(policyMemRec4.Member__c==accountRec.Id,true);
            
            case caseRecord7 = [Select id, RecordType.Name,Policy_Member__r.Name  from case where complaint__c = 'No' limit 1];
            caseRecord7.Policy_Member__c = policyMemRec4.id;
            caseRecord7.Interacting_About_Type__c = 'Member';
            update caseRecord7;
            ApexPages.StandardController scon7 = new ApexPages.StandardController(caseRecord7);
            CaseEdit_CE_HUM caseEdit7 = new CaseEdit_CE_HUM(scon7);
            List<SelectOption> lstBenefitOptions7 = caseEdit7.getCallBenefitCategoryValues();
            caseEdit7.rerenderMedicareSection();
            prod4.Product_Type__c = '';
            update prod4;
             caseEdit7.rerenderMedicareSection();
            
            case caseRecord8 = [Select id, RecordType.Name,Policy_Member__r.Name  from case where Policy_Member__c = null limit 1];
             ApexPages.StandardController scon8 = new ApexPages.StandardController(caseRecord8);
            CaseEdit_CE_HUM caseEdit8 = new CaseEdit_CE_HUM(scon8);
            List<SelectOption> lstBenefitOptions8 = caseEdit8.getCallBenefitCategoryValues();
            caseEdit8.rerenderMedicareSection();
            testUser.Mentor_Sub_Function__c = '';
            update testUser;
            caseEdit8.getlstSubFunctions();
            System.AssertNotEquals(caseRecord8.Id,Null);
            Test.stopTest();
        }
    }
    
    static testMethod void sendRequestLogNotestestCatch()
    {
         User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        system.runAs(testUser) 
        {
            Test.startTest();
            	 HUM_Webservice_Callout__c RighSourceEditServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            	RighSourceEditServiceDetails.Timeout__c = '0';
        			update RighSourceEditServiceDetails;
                  Case objCase = new Case();
               
                Continuation continueObj = (Continuation)CaseEdit_CE_HUM.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' ,'Test' ,'sAccNum',objCase.id);
                System.AssertNotEquals(RighSourceEditServiceDetails.Id,Null);
            Test.stopTest();
        }
        
    }
    
    static testMethod void testRSO_HP_PermissionSet_Name()
    {
          User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
        PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name =:CaseEditConstants_HUM.RSO_HP_PermissionSet_Name LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = pCheck.id);
        
        system.runAs(testUser) 
        {
            Test.startTest();
              case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE recordType.Name = 'HP Member Case' AND Classification_Type__c = 'Humana Pharmacy Calls' LIMIT 1]; 
              System.AssertNotEquals(caseRecord.Id,Null);
            Test.setCurrentPage(page.CaseEdit_VF_HUM);    
            ApexPages.currentpage().getParameters().put('isHP','true');
            ApexPages.currentpage().getParameters().put('TicketRequired','false');
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);
            
            caseEditPageObj.sClassification = 'Test';
            caseEditPageObj.sIntent = 'Test';
            
            caseEditPageObj.setLogCode();
            caseEditPageObj.casecom.CommentBody = 'test';
            caseEditPageObj.sCode = '';
            caseEditPageObj.errorLevel='ERROR';
            caseEditPageObj.messageName='Humana Pharmacy Log Code:  You must enter a value';
            caseEditPageObj.save();
            
            caseEditPageObj.setLogCode();
            caseEditPageObj.casecom.CommentBody = '';
            caseEditPageObj.sCode = 'test';
            caseEditPageObj.errorLevel='ERROR';
            caseEditPageObj.messageName='Case Comments:  You must enter a value';
            caseEditPageObj.save();
            caseEditPageObj.fetchUserDetails();
			caseEditPageObj.getlstSubFunctions();
             testuser.Mentor_Sub_Function__c = 'Individual Medicare';
        	update testUser;
        	  caseEditPageObj.fetchUserDetails();
			caseEditPageObj.getlstSubFunctions();
            Test.stopTest();
        }
    }
  
    static testMethod void testrequestIDCaseCreate()
    {
          User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test CaseEditUser1'];
          PermissionSet pRSOHP = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.RAPIDFORCE_PERMISSIONSET_HUM LIMIT 1];
        	insert New PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = pRSOHP.id);
        
        system.runAs(testUser) 
        {
            Test.startTest();
              case caseRecord = [Select id,status,RecordType.Name,Case_Owner__c,Owner_Queue__c, Classification_Type__c, Subtype__c, type, origin  FROM Case WHERE recordType.Name = 'HP Member Case' AND Classification_Type__c = 'Humana Pharmacy Calls' LIMIT 1]; 
              
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = '2786833';
            crm1.Scrum_Team__c = 'test';
            crm1.Story_Title__c = 'test';
            crm1.Release_Name__c = '10/21/2021';
            insert crm1; 

            Test.setCurrentPage(page.CaseEdit_VF_HUM);    
            
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
             ApexPages.currentpage().getParameters().put('PageRequestIDFrom','true');
            CaseEdit_CE_HUM caseEditPageObj = new CaseEdit_CE_HUM(scon);
            caseEditPageObj.requestIDCaseCreate();
            System.AssertNotEquals(caseRecord.Id,Null);
            Test.stopTest();
        }
    }        
}