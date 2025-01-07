/**************************************************************************** 
Apex Class Name  : UtilityHelper_T_AHT_HUM 
Version          : 1.0  
Created Date     : January 25 2017 
Function         : The class is the Test Class for UtilityHelper_H_AHT_HUM
 
Modification Log: 
 * Developer Name           Code Review                  Date                       Description
 *------------------------------------------------------------------------------------------------------------------
 * Amar Gadewar                                         01/25/2017                  Original Version
 * Amar Gadewar				71394						02/27/2017					Add Test Coverage for getCurrentPageName
 * Amar Gadewar											07/03/2017			  		REQ - 320483 - Add Test Coverage for Quick Start Permision Set Check Generic Function
 * Subhamay Mandal										08/14/2020			  		Increase test coverage for Copado process.
 * Sunil Kumar Reddy									08/14/2020			  		Increase test coverage for PCPPCD Method
 * Amar Gadewar                                         08/28/2020                  User Story 1386444: PR00091574 - MF 1 - Rollback "What is the Call About?" feature in Quick Start (RF)
 * Kavya Bondada                                        04/14/2021                  Increase test coverage for isQAAAutoRoute  Method
 * Sunil Kumar Reddy                                    06/17/2020                  Overall code coverage
 * Sunil Kumar Reddy                                    07/13/2020                  User Story 2454886: T1PRJ0001827 - MF 1 - AHT - Ability to auto launch 'Medicare Good Cause Reinstatement' template when the case is created through Quick Start (CRM)
 * Sunil Kumar Reddy                                    09/23/2021                  User Story 2696526: T1PRJ0001827 - MF 1 - AHT - Ability to auto launch 'Update Plan Demographics' template when a case is created through quick start (RF)
 * Pinky Vijur                                          01/20/2023                  3215226	T1PRJ0891339 - MF 5 - TECH - Pharmville Decommissioning - Permission Set Usage
 * Gowthami Thota                                       07/31/2023                 Test Class Fix for UAT 
*************************************************************************************************************/
@isTest
private class UtilityHelper_T_AHT_HUM {

   /**
    * Returns void
    * Method to setup Testdata 
    *
    * @return  Void
    * @see
    */
    @testSetup static void setup() 
    {    
        
        Group oCompassNewGroup = null;
        oCompassNewGroup = new Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oCompassNewGroup;
        system.assertEquals(oCompassNewGroup.Type,'Queue');
        // User without the Custom Permission Set assigned to validate checkAccess utlility of Customer Permission
        User oUserLegacy = HUMTestDataHelperTest.createTestUser('UserLegacy','Customer Care Specialist');
        insert oUserLegacy;
        
        
        // User AHT without the Custom Permission Set assigned to validate checkAccess utlility of Customer Permission
        User oUserAHT = HUMTestDataHelperTest.createTestUser('oUserAHT','Customer Care Specialist');
        insert oUserAHT;
        
        // Add the existing Custom Permission to the AHT User, to verify the access 
        // Create PermissionSet with Custom Permission and assign to test user
	PermissionSet ps = new PermissionSet();
	ps.Name = 'Test';
	ps.Label = 'Test';
	insert ps;
	    
	SetupEntityAccess sea = new SetupEntityAccess();
	sea.ParentId = ps.Id;
	sea.SetupEntityId = [select Id from CustomPermission where DeveloperName = 'Ignore_Cant_edit_GA_Complaint_on_Reopened_Cases_VR'][0].Id;
	insert sea;
	    
	PermissionSetAssignment psa = new PermissionSetAssignment();
	psa.AssigneeId = oUserAHT.id;
	psa.PermissionSetId = ps.Id;
	insert psa;
	    
	PermissionSet pCheckRF =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.RAPIDFORCE_PERMISSIONSET_HUM LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = oUserAHT.id, PermissionSetId = pCheckRF.id);
	PermissionSet pCheckMIR =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.MIR_PERMISSIONSET_HUM LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = oUserAHT.id, PermissionSetId = pCheckMIR.id);
        PermissionSet pCheckMedicare =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = oUserAHT.id, PermissionSetId = pCheckMedicare.id);
        PermissionSet pCheckRFGBO =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.RFGBOTITLETOWNERS_PERMISSION_HUM LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = oUserAHT.id, PermissionSetId = pCheckRFGBO.id);
        PermissionSet pCheckOGO =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.Grievance_Appeals_Permissionset_Name LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = oUserAHT.id, PermissionSetId = pCheckOGO.id);
        PermissionSet pCheckOGOScrubber =[SELECT ID From PermissionSet WHERE Name ='CRMS_401_OGO_Scrubber_Access' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = oUserAHT.id, PermissionSetId = pCheckOGOScrubber.id);
        System.runAs(oUserAHT) {
	  RecordType AccRType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        
        //Creating Account Record
        Account accRecord = HUMTestDataHelperTest.getAccount();
        accRecord.OwnerId = oUserAHT.id;
        accRecord.FirstName = 'Test';
        accRecord.LastName = 'Account';
        accRecord.Enterprise_Id__c = '123456';
        accRecord.recordtypeId = AccRType.id;
        insert accRecord;
        system.assertEquals(accRecord.recordtypeId==AccRType.id,true);
 List<Intent__c> intentList = New List<Intent__c>();
        Intent__c intentRef = New Intent__c();
        intentRef.Name = 'PCP Change Made';
        intentRef.Is_Active__c = true;
        intentList.add(intentRef);
        
         Intent__c intentRef1 = New Intent__c();
        intentRef1.Name = 'QAA';
        intentRef1.Is_Active__c = true;
        intentList.add(intentRef1);
            
insert intentList;
 List<Classification__c> lstClassificationNew = New List<Classification__c>();
        Classification__c classificationRecf = New Classification__c();
        classificationRecf.Name = 'PCP Change';
        classificationRecf.Is_Active__c = true;
        lstClassificationNew.add(classificationRecf);
            
        Classification__c classificationRecf1 = New Classification__c();
        classificationRecf1.Name = 'Complaints or Compliments';
        classificationRecf1.Is_Active__c = true;
        lstClassificationNew.add(classificationRecf1);
insert lstClassificationNew;

 List<Classification_Type__c> ctListNew= New list<Classification_Type__c>();
 Classification_Type__c ctRef = New Classification_Type__c();
        ctRef.name = 'Calls (RS)';
        ctRef.Is_Active__c=true;
        ctListNew.add(ctRef);
insert ctListNew;
 
List<CTCI_Junction__c> ctciListNew = New list<CTCI_Junction__c>();
        CTCI_Junction__c ctciRecf = New CTCI_Junction__c();
        ctciRecf.Classification_Type__c = ctRef.id;
        ctciRecf.Classification__c = lstClassificationNew[0].Id;
        ctciRecf.Intent__c = intentList[0].id;
        ctciRecf.IsActive__c = true;
        ctciListNew.add(ctciRecf);
            
        CTCI_Junction__c ctciRecf1 = New CTCI_Junction__c();
        ctciRecf1.Classification_Type__c = ctRef.id;
        ctciRecf1.Classification__c = lstClassificationNew[1].Id;
        ctciRecf1.Intent__c = intentList[1].id;
        ctciRecf1.IsActive__c = true;
        ctciListNew.add(ctciRecf1);
insert ctciListNew;

Product2 prod4 = New Product2();
        prod4.Product_Type__c = GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_MAPD;
        prod4.ProductCode = GLOBAL_CONSTANT_HUM.MEDICARE_POLICY;
        prod4.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCR;
        prod4.Name = 'MED';
        prod4.Major_LOB__c = 'MER';
        insert prod4;

        system.assertEquals(prod4.Name=='MED',true); 

Policy__c policyRec2 = New Policy__c();
        policyRec2.Group_Name__c = accRecord.Id;
        policyRec2.Product__c = prod4.id;
        policyRec2.Policy_Group_Number__c='11111';
        policyRec2.Major_LOB__c = 'MER';
        insert policyRec2;
        system.assertEquals(policyRec2.Product__c==prod4.id,true);

   Policy_Member__c policyMemRec2 = New Policy_Member__c();
        policyMemRec2.Name = 'efgh';
        policyMemRec2.Policy__c = policyRec2.Id;
        policyMemRec2.Member__c = accRecord.Id;
        insert policyMemRec2;
        system.assertEquals(policyMemRec2.Member__c==accRecord.Id,true);

     Case caseRecord2 = New Case();
        caseRecord2.Status = 'In Progress';
        caseRecord2.Owner_Queue__c = 'Louisville RSO Calls Suppor';
        caseRecord2.AccountId = accRecord.id;
        caseRecord2.ownerId = oUserAHT.Id;
        caseRecord2.Classification_Type__c = 'Calls (RS)';
        caseRecord2.Classification_id__c=lstClassificationNew[0].id;
        caseRecord2.Intent_id__c = intentList[0].id;
        caseRecord2.Interacting_With__c = accRecord.id;
        caseRecord2.Interacting_With_Type__c = 'Member';
        caseRecord2.Interacting_About_Type__c = 'Member';
        caseRecord2.Policy_Member__c = policyMemRec2.Id;
        caseRecord2.CTCI_List__c = ctciListNew[0].Id;
        caseRecord2.Autoroute_Status__c = 'Evaluate';
        caseRecord2.Origin = 'Inbound Call';
        insert caseRecord2;
        system.assertEquals(caseRecord2.Owner_Queue__c=='Louisville RSO Calls Suppor',true);
        system.assertEquals(caseRecord2.CTCI_List__c,ctciListNew[0].id);
            Account objAccountForEntitlement = new Account();
            objAccountForEntitlement .Name =   'MIR Account_DO_NOT_DELETE';
            objAccountForEntitlement .RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Group').getRecordTypeId();
            insert objAccountForEntitlement; 
            system.assertNotEquals(objAccountForEntitlement.id,null);
            List<SlaProcess> objSlaList = [Select Id,Name,createddate,VersionNumber,IsVersionDefault,VersionMaster from SlaProcess Where IsActive = True and Name = 'Member Issue Resolution Process' order by VersionNumber desc];
            Entitlement objEnt = new Entitlement ();
            objEnt.Name = 'Member Issue Resolution SLA';
            objEnt.StartDate = System.today();
            objEnt.EndDate = objEnt.StartDate.addYears(2);
            objEnt.Type = 'Phone Support';
            objEnt.AccountId = objAccountForEntitlement.Id;
            objEnt.SlaProcessId = objSlaList[0].Id;
            insert objEnt;  
	   
            List<Classification_Type__c> ctList= New list<Classification_Type__c>();
            //Launch Compass Functionality
            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'Calls (RSO)';
            ctRec.Is_Active__c=true;
            ctRec.Mentor_Function__c ='Contact Center Provider';
            ctList.add(ctRec);
            insert ctList;
            system.assertEquals(ctList.size(),1);
            Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = 'Test Business Process and Testing', OwnerId = UserInfo.getUserId(), Service_Center__c = 'Business', Department__c = 'Process and Testing',Classification_Type__c=ctList[0].id);
            insert oWorkQueue;
            system.assertEquals(oWorkQueue.Service_Center__c,'Business');
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Billing';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);
            insert lstClassification;
            system.assertEquals(lstClassification.size(),1);
        
            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            insert lstIntent;
            system.assertEquals(lstIntent.size(),1);
            List<CTCI_Junction__c> ctciList = New list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = New CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            insert ctciList;
            system.assertEquals(ctciList.size(),1);
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = UserInfo.getUserId();
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
            Case caseRecord = New Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = accountRec.id;
            caseRecord.ownerId = oUserAHT.id;
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
            insert caseRecord;
            Interaction__c objInteraction = New Interaction__c();
            objInteraction.Interacting_With__c = caseRecord.AccountId;
            objInteraction.Interacting_With_type__c = 'Member';
            objInteraction.Caller_Name__c = 'Test User';
            objInteraction.Interaction_Origin__c = 'Phone';
            insert objInteraction;
            system.assertEquals(caseRecord.Status=='In Progress',true);
            
        Policy__c policyRec3 = New Policy__c();
        policyRec3.Group_Name__c = accRecord.Id;
        policyRec3.Product__c = prod4.id;
        policyRec3.Policy_Group_Number__c='11111';
        policyRec3.Major_LOB__c = 'MCD';
        insert policyRec3;
        system.assertEquals(policyRec3.Product__c==prod4.id,true);

   Policy_Member__c policyMemRec3 = New Policy_Member__c();
        policyMemRec3.Name = 'efgh';
        policyMemRec3.Policy__c = policyRec3.Id;
        policyMemRec3.Member__c = accRecord.Id;
        insert policyMemRec3;
        system.assertEquals(policyMemRec3.Member__c==accRecord.Id,true);

     Case caseRecord3 = New Case();
        caseRecord3.Status = 'In Progress';
        caseRecord3.Owner_Queue__c = 'Louisville RSO Calls Suppor';
        caseRecord3.AccountId = accRecord.id;
        caseRecord3.ownerId = oUserAHT.Id;
        caseRecord3.Classification_Type__c = 'Calls (RS) Test';
        caseRecord3.Classification_id__c=lstClassificationNew[0].id;
        caseRecord3.Intent_id__c = intentList[0].id;
        caseRecord3.Interacting_With__c = accRecord.id;
        caseRecord3.Interacting_With_Type__c = 'Member';
        caseRecord3.Interacting_About_Type__c = 'Member';
        caseRecord3.Policy_Member__c = policyMemRec3.Id;
        caseRecord3.CTCI_List__c = ctciListNew[0].Id;
        caseRecord3.Autoroute_Status__c = 'Evaluate';
        caseRecord3.Origin = 'Inbound Call';
        insert caseRecord3;           
        Policy__c policyRec4 = New Policy__c();
        policyRec4.Group_Name__c = accRecord.Id;
        policyRec4.Product__c = prod4.id;
        policyRec4.Policy_Group_Number__c='11111';
        policyRec4.Major_LOB__c = 'MCD';
        policyRec4.Issue_State__c='FL';
        insert policyRec4;
        system.assertEquals(policyRec4.Product__c==prod4.id,true);

   Policy_Member__c policyMemRec4 = New Policy_Member__c();
        policyMemRec4.Name = 'efghi';
        policyMemRec4.Policy__c = policyRec4.Id;
        policyMemRec4.Member__c = accRecord.Id;
        insert policyMemRec4;
        system.assertEquals(policyMemRec4.Member__c==accRecord.Id,true);

     Case caseRecord4 = New Case();
        caseRecord4.Status = 'In Progress';
        caseRecord4.Owner_Queue__c = 'Louisville RSO Calls Suppor';
        caseRecord4.AccountId = accRecord.id;
        caseRecord4.ownerId = oUserAHT.Id;
        caseRecord4.Classification_Type__c = 'Calls (RS) Test';
        caseRecord4.Classification_id__c=lstClassificationNew[1].id;
        caseRecord4.Intent_id__c = intentList[1].id;
        caseRecord4.Interacting_With__c = accRecord.id;
        caseRecord4.Interacting_With_Type__c = 'Member';
        caseRecord4.Interacting_About_Type__c = 'Member';
        caseRecord4.Policy_Member__c = policyMemRec4.Id;
        caseRecord4.CTCI_List__c = ctciListNew[1].Id;
        caseRecord4.Autoroute_Status__c = 'Evaluate';
        caseRecord4.Origin = 'Inbound Call';
        insert caseRecord4;
        
        Product2 productRef = New Product2();
        productRef.Product_Type__c = 'PDP';
        productRef.ProductCode = GLOBAL_CONSTANT_HUM.MEDICARE_POLICY;
        productRef.Segment_Type__c = GLOBAL_CONSTANT_HUM.SEGMENT_TYPE_MCR;
        productRef.Name = 'MED';
        productRef.Major_LOB__c = 'PDP';
        insert productRef;

        system.assertEquals(productRef.Name=='MED',true); 

 	Policy__c polrec4 = New Policy__c();
        polrec4.Group_Name__c = accRecord.Id;
        polrec4.Product__c = productRef.id;
        polrec4.Policy_Group_Number__c='11111';
        polrec4.Major_LOB__c = 'PDP';
        insert polrec4;
        system.assertEquals(polrec4.Product__c==productRef.id,true);

   Policy_Member__c polmem3 = New Policy_Member__c();
        polmem3.Name = 'efgh';
        polmem3.Policy__c = polrec4.Id;
        polmem3.Member__c = accRecord.Id;
        insert polmem3;
        system.assertEquals(polmem3.Member__c==accRecord.Id,true);

     Case caseRef = New Case();
        caseRef.Status = 'In Progress';
        caseRef.Owner_Queue__c = 'test queue';
        caseRef.AccountId = accRecord.id;
        caseRef.ownerId = oUserAHT.Id;
        caseRef.Classification_Type__c = 'Calls (RSO)';
        caseRef.Classification_id__c=lstClassificationNew[0].id;
        caseRef.Intent_id__c = intentList[0].id;
        caseRef.Interacting_With__c = accRecord.id;
        caseRef.Interacting_With_Type__c = 'Member';
        caseRef.Interacting_About_Type__c = 'Member';
        caseRef.Policy_Member__c = polmem3.Id;
        caseRef.CTCI_List__c = ctciListNew[0].Id;
        caseRef.Autoroute_Status__c = 'Evaluate';
        caseRef.Origin = 'Inbound Call';
        insert caseRef; 
        //Template Data
        Template__c tem1 = new Template__c();
        tem1.Unique_Template_Name__c = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
        tem1.Description__c='goodcause';
        insert tem1;
        
        Template_Field__c temfield1 = new Template_Field__c();
        temfield1.Name = 'GCRTermedPlanYesNo';
        temfield1.Template__c = tem1.id;
        
        insert temfield1;
        
        
        Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
        tOnwer11.Object_Owner_ID__c=string.valueOf(caseRef.id);
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
        }
    }
  static testMethod void testPCPPCD()
    {
          User oUser = [select id, name,lastname from user where lastname='test oUserAHT' limit 1];
          system.assertEquals(oUser.lastname =='test oUserAHT',true); 
        oUser.current_queue__c = 'Louisville RSO Calls Support';
        update oUser;
        system.assertEquals(oUser.current_queue__c =='Louisville RSO Calls Support',true); 
        
        system.runAs(oUser)
        {   
            Test.StartTest();


            List<Case> lstRes = [Select owner_queue__c ,Id,Classification_Type__c,Policy_Member__c,Product__c ,Interaction_ID__c,product_type__c,Interacting_About_Type__c ,Interacting_With_Type__c ,G_A_Rights_Given__c ,Interacting_With_Name__c ,CTCI_List__r.Classification__r.Name, CTCI_List__r.Intent__r.Name,Policy_Member__r.Product_type_code__c from Case where Policy_Member__c!=null limit 1];
            lstRes[0].owner_queue__c = 'Louisville RSO Calls Support';
            update lstRes;
            lstRes[0].Classification_Type__c ='Calls (RSO)';
            update lstRes;
            List<Classification__c> oclassifi = [SELECT id,name from Classification__c WHERE name='PCP Change'];
            List<Intent__c> oIntent = [SELECT id,name from Intent__c WHERE name='PCP Change Made'];
            User currentloggedInUser = [select id,current_queue__c, name,lastname from user where lastname='test oUserAHT' limit 1];
            system.assertEquals(oUser.current_queue__c =='Louisville RSO Calls Support',true);
            UtilityHelper_H_AHT_HUM.checkProcessPCPPCD( lstRes[0], oclassifi[0].name,oIntent[0].name);
            Test.stopTest();
           
        }
    }
    
  static testMethod void testMedicaidPARProcessPositiveNegetive()
    {
          User oUser = [select id, name,lastname from user where lastname='test oUserAHT' limit 1];
          system.assertEquals(oUser.lastname =='test oUserAHT',true); 
        oUser.current_queue__c = 'Louisville RSO Calls Support';
        update oUser;
        system.assertEquals(oUser.current_queue__c =='Louisville RSO Calls Support',true); 
        
        system.runAs(oUser)
        {   
            Test.StartTest();
           
            

            List<Case> lstRes = [Select Id,Classification_Type__c,Policy_Member__r.policy__r.Major_LOB__c,Policy_Member__c,Product__c,Product_Type__c ,Interaction_ID__c,Interacting_About_Type__c ,Interacting_With_Type__c ,G_A_Rights_Given__c ,Interacting_With_Name__c ,CTCI_List__r.Classification__r.Name, CTCI_List__r.Intent__r.Name,Policy_Member__r.Product_type_code__c from Case where Policy_Member__c!=null and classification_type__c='Calls (RS) Test' limit 1];
            lstRes[0].Classification_Type__c =GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_MEDICAID_CALLS;
            update lstRes;
            system.assertEquals(lstRes[0].Classification_Type__c ==GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_MEDICAID_CALLS,true);
            system.assertEquals(lstRes[0].product_type__c=='MCD',true);
            List<Classification__c> oclassifi = [SELECT id,name from Classification__c WHERE name='PCP Change'];
            oclassifi[0].Name = CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderClassification;
            update oclassifi;
            List<Intent__c> oIntent = [SELECT id,name from Intent__c WHERE name='PCP Change Made'];
            oIntent[0].Name = CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderIntent;
            update oIntent;
            UtilityHelper_H_AHT_HUM.checkProcessMedicaidPARProviderNotAccepting( lstRes[0], oclassifi,oIntent);
            
            //Cover Catch Block
            UtilityHelper_H_AHT_HUM.checkProcessMedicaidPARProviderNotAccepting( new Case(), oclassifi,oIntent);
            Test.stopTest();
           
        }
    }   
    
   /**
    * Returns void
    * test method for checkCustomPermissionAccess method of UtilityHelper_H_AHT_HUM class 
    *
    * @return  Void
    * @see
    */
    static testMethod void testCheckCustomPermissionAccessValid()
    {
      	User oUserAHT = [select id, name,lastname from user where lastname='test oUserAHT' limit 1];
        
        RecommendedQueueView__c csRQV = new RecommendedQueueView__c(Name='Coverage Determination', OGO_QueueView_Name__c='Coverage Determination', Set_Topic_for_Accepted__c='Internal Only - HCPR', Set_Topic_for_Rejected__c='Oral Grievance');
        insert csRQV;
        
        system.runAs(oUserAHT)
        {   
            Test.StartTest();
            Boolean bHasAccess = UtilityHelper_H_AHT_HUM.checkCustomPermissionAccess('Ignore_Cant_edit_GA_Complaint_on_Reopened_Cases_VR');          
            Boolean bHasAccessRF = UtilityHelper_H_AHT_HUM.isRapidForceUser();          
            Boolean bHasAccessMIR = UtilityHelper_H_AHT_HUM.isMIRPilotUser();          
            Boolean bHasAccessMedicare = UtilityHelper_H_AHT_HUM.isUserHavingMedicareCustomerServiceAccess();          
            Boolean bHasAccessRFGBO = UtilityHelper_H_AHT_HUM.ischeckCustomPermissionAccess();
            Boolean bHasAccessPS_ANY = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(GLOBAL_CONSTANT_HUM.MIR_PERMISSIONSET_HUM);
            Boolean bHasAccessDisable_OGO = UtilityHelper_H_AHT_HUM.disableButtonOGOCase(GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT);
            Boolean bReccQV = UtilityHelper_H_AHT_HUM.isRecommendedQueueView('Coverage Determination');
            RecommendedQueueView__c csReccQV = UtilityHelper_H_AHT_HUM.getRecommendedQueueViewDetail('Coverage Determination');
            String bCasePrefix = UtilityHelper_H_AHT_HUM.getObjectPrefix('Case');
            Boolean bCaseFeedback = UtilityHelper_H_AHT_HUM.hasCaseFeedback('');
            Boolean bDisableAction = UtilityHelper_H_AHT_HUM.disableActionButton(GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT, GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT);
            Boolean bHasAccessOGOScrubber = UtilityHelper_H_AHT_HUM.isOGOScrubberUser();
            Case oCase = [select id from Case where Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
            List<CaseMilestone> listOfCaseMilestones = [Select id,caseId,milestoneType.name from CaseMilestone where caseId =:oCase.id];
            String sValue = UtilityHelper_H_AHT_HUM.getClassficationId('Billing', oCase.Id);
            // method call only
            UtilityHelper_H_AHT_HUM.formatMinutes(new CaseMilestone());////****missing */
            UtilityHelper_H_AHT_HUM.getTemplateData('',new List<String>(),new case());
            List<User> lstUser = new List<User>{oUserAHT};
            UtilityHelper_H_AHT_HUM.getCaseMilestones(lstUser, true, 10);
            UtilityHelper_H_AHT_HUM.isStringId('');
           UtilityHelper_H_AHT_HUM.isQAAAutoRoute(new Case());
            // eoc method call only
            Test.stopTest();
            System.assert(bHasAccess,'Custom Permission Ignore_Cant_edit_GA_Complaint_on_Reopened_Cases_VR is missing from the Test User' );
            System.assert(bHasAccessRF, true);
            System.assert(bHasAccessMIR, true);
            System.assert(bHasAccessMedicare, true);
            System.assert(bHasAccessRFGBO, true);
            System.assert(bHasAccessPS_ANY, true);
            //System.assert(bHasAccessDisable_OGO, true);
            System.assert(bReccQV);
            System.assert(csReccQV != null);
            System.assert(bCasePrefix=='500');
            System.assert(sValue != null);
        }
    }
    
    /**
    * Returns void
    * test method for checkCustomPermissionAccess method of UtilityHelper_H_AHT_HUM class 
    *
    * @return  Void
    * @see
    */
    static testMethod void testCheckCustomPermissionAccessInValid()
    {
      	User UserLegacy = [select id, name,lastname from user where lastname='test UserLegacy' limit 1];
        
        system.runAs(UserLegacy)
        {   
            Test.StartTest();
            Boolean bHasAccess = UtilityHelper_H_AHT_HUM.checkCustomPermissionAccess('Ignore_Cant_edit_GA_Complaint_on_Reopened_Cases_VR');          
            Test.stopTest();
            System.assert(!bHasAccess,'Customer Permission Ignore_Cant_edit_GA_Complaint_on_Reopened_Cases_VR seems to be added on the Test User');
        }
    }
    
    /**
    * Returns void
    * test method for checkCustomPermissionAccess method of UtilityHelper_H_AHT_HUM class 
    *
    * @return  Void
    * @see
    */
    static testMethod void testGetCurrentPageName()
    {
      	 
      	 PageReference accountDetailsVfPage = new PageReference('/apex/AccountDetailR1_VF_HUM');
		 Test.setCurrentPage(accountDetailsVfPage);
      	
      	User UserLegacy = [select id, name,lastname from user where lastname='test UserLegacy' limit 1];
        
        system.runAs(UserLegacy)
        {   
            Test.StartTest();
           
            String sPageName = UtilityHelper_H_AHT_HUM.getCurrentPageName();          
            Test.stopTest();
           
        }
    }
    
   /**
    * Returns void
    * test method for check Quick Start Permission method of UtilityHelper_H_AHT_HUM class 
    *
    * @return  Void
    * @see
    */
    static testMethod void testIsRSOUserHavingQuickStartAccess()
    {
      	User oUserAHT = [select id, name,lastname from user where lastname='test oUserAHT' limit 1];
      	PermissionSet ps = [SELECT ID From PermissionSet WHERE Name =: GLOBAL_CONSTANT_HUM.QUICKSTARTACCESS_PERMISSIONSET_HUM];
		insert new PermissionSetAssignment(AssigneeId = oUserAHT.id, PermissionSetId = ps.Id );
        
        system.runAs(oUserAHT)
        {   
            Test.StartTest();
            Boolean bHasAccess = UtilityHelper_H_AHT_HUM.isRSOUserHavingQuickStartAccess();          
            Test.stopTest();
            System.assert(bHasAccess,'Quick Start Permission is missing from the Test User' );
        }
    }
    
    /**
    * Returns void
    * test method for check Quick Start Permission method of UtilityHelper_H_AHT_HUM class 
    *
    * @return  Void
    * @see
    */
    static testMethod void testIsRSOUserHavingQuickStartAccessInValid()
    {
      User oUserLegacy = [select id, name,lastname from user where lastname='test UserLegacy' limit 1];
        
        system.runAs(oUserLegacy)
        {   
            Test.StartTest();
            Boolean bHasAccess = UtilityHelper_H_AHT_HUM.isRSOUserHavingQuickStartAccess();          
            Test.stopTest();
            System.assert(!bHasAccess,'Quick Start Permission is present on a Legacy Test User' );
        }
    }
   
    static testMethod void testisQAAAutoRoute()
    {
          User oUser = [select id, name,lastname from user where lastname='test oUserAHT' limit 1];
          system.assertEquals(oUser.lastname =='test oUserAHT',true); 
        oUser.current_queue__c = 'Louisville RSO Calls Support';
        update oUser;
        system.assertEquals(oUser.current_queue__c =='Louisville RSO Calls Support',true); 
        
        system.runAs(oUser)
        {   
            Test.StartTest();


            List<Case> lstRes = [Select owner_queue__c ,Id,Classification_Type__c,Policy_Member__c,Product__c ,CTCI_List__r.Classification__r.Name, CTCI_List__r.Intent__r.Name,Policy_Member__r.Product_type_code__c,Policy_Member__r.Issue_State__c ,Policy_Member__r.Product_Type__c,Policy_Member__r.Segment_Indicator__c from Case where Policy_Member__r.Policy__r.Issue_State__c ='FL' limit 1];
            lstRes[0].owner_queue__c = 'Louisville RSO Calls Support';
            update lstRes;
            lstRes[0].Classification_Type__c ='Calls (RSO)';
            update lstRes;
            
            User currentloggedInUser = [select id,current_queue__c, name,lastname from user where lastname='test oUserAHT' limit 1];
            system.assertEquals(oUser.current_queue__c =='Louisville RSO Calls Support',true);
            UtilityHelper_H_AHT_HUM.isQAAAutoRoute( lstRes[0]);            
            List<Case> caseList = [SELECT id,Classification_Type__c,Policy_Member__r.Product_type_code__c from Case where owner_queue__c='test queue' and Policy_Member__r.Product_type_code__c='PDP' Limit 1];
            UtilityHelper_H_AHT_HUM.checkProcessGoodCause(caseList[0],GLOBAL_CONSTANT_HUM.GOODCAUSECLASSIFICATION,GLOBAL_CONSTANT_HUM.GOODCAUSEINTENT);
            UtilityHelper_H_AHT_HUM.checkUpdateDemographic(caseList[0],CaseAutoRouteConstant_C_HUM.CLASSIFICATION_DEMOGRAPHIC,CaseAutoRouteConstant_C_HUM.INTENT_CHANGE);
            Test.stopTest();
           
        }
    }
    
     static testMethod void testOverAll()
    {
          User oUser = [select id, name,lastname from user where lastname='test oUserAHT' limit 1];
          system.assertEquals(oUser.lastname =='test oUserAHT',true); 
        system.runAs(oUser)
        {   
            Test.StartTest();
               List<Template_Submission_Data__c> tempData = [SELECT Id, Template_Field__r.Name, Value__c FROM Template_Submission_Data__c];
               UtilityHelper_H_AHT_HUM.isTemplateSubmittedHasValue(tempData);
            Test.stopTest();
            
           
        }
    }  
}