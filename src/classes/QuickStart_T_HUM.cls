/**********************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_T_HUM.cls 
Created Date     : June 22, 2017
Function         : Executes SOQL in support of the Quick Start Console Component
Test Class       : QuickStart_T_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
* Amar Gadewar            -----                 06/22/2017                 Original Version
* Siddharth Mehta		  132132				09/25/2017				   Test Coverage for Quick Start
* Amar Gadewar									01/14/2017				   REQ - 350393 - SF - TECH - Removal of Pretext from Mentor Document Links Custom Settings (RF)
* Amar Gadewar			  172620				01/19/2017				   REQ - 350502 Ability of a quick start to create a case on an existing interaction created by someone else(CRM)
* Subhamay Mandal		 					    02/25/2018 				   REQ - 351011 Ability to use Business Configuration model for Quick Start pretext for making changes within Quick Start pre-text (CRM)
* Subhamay Mandal		   					  	04/06/2018				   REQ - 359992 and REQ - 360019. UI change(plus and logo) and Performace improvement
* Darshan Sharma                           		05/02/2018                 REQ - 361223 - Ability to update Mentor Links in QuickStart via Business Configuration
* Darshan Sharma			                    10/01/2018		           REQ - 374820: CTCI Model Update (RF+CRM)
* Darshan Sharma								11/30/2018				   REQ - 378965 PCC - Mentor Documents Set 1 (RF)
* Subhamay Mandal								02/08/2019				   REQ - 383977	Quality - Ability to display the AutoLogging Message for additional Classification and Intents (CRM)
* Subhamay Mandal								03/21/2019  			   REQ - 389663 AHT - RSO - Add Frequently Used Classification and Intent to Classification Dropdown (CRM)	
* Amar Gadewar									04/01/2019		   		   REQ - 388904 - AHT – RSO – Guidance Center Pretext (Set 1) (CRM)
* Prudhvi Pamarthi                              07/15/2019                 REQ - 396335 Legacy Delete Requirement 
* Subhamay Mandal								08/13/2019  			   REQ - 406018 Update test class as per new field	
* Harish Gowda							        	09/02/2019				   REQ - 406901 Quality - OGO - Ability to NOT allow a user to select BOTH the G&A Rights Given and Complaint dropdowns, in QUICKSTART, to Yes (CRM)
* Harish Gowda							        	09/03/2019				   REQ - 406902 Quality - OGO - Ability to auto select G&A Rights Given field in Quick Start for certain classification and intent combinations (CRM)
* Harish Gowda							        	09/04/2019				   REQ - 406903 Quality - OGO - Ability to auto select Complaint in Quick Start for certain classification and intent combinations (CRM)
* Harish Gowda							        	09/01/2019				   REQ - 409549 Quality - Add Informational Message above Callback Number (CRM) .
* Subhamay Mandal							        02/02/2020				   Code Merged 0228. Build Fix 1 
* Amar Gadewar                                      10/09/2020                 User Story 1541640: PR00091574 - MF 1 - Quality - Quick Start Code Improvement and Issue Fixes (CRM)
* Amar Gadewar                                      07/22/2021                 User Story 2498859: T1PRJ0001827 - MF 1 - AHT/Quality - Ability to display alert in Quick Start if the Rep is creating a case with potential duplicate grievance (CRM)
* Prasuna Pattabhi                           07/31/2023                 Test Class error fix - build fix
************************************************************************************************************************************************************************************/
@isTest
private class QuickStart_T_HUM 
{	
	@testSetup static void setup()
    {
        Group oNewGroup = null;
        oNewGroup = new Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oNewGroup;

        QueueSObject oNewQueue = new QueueSObject(QueueId = oNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;

        User oCCSUser = HUMTestDataHelperTest.createTestUser('QuickStartSpecialist','Customer Care Specialist');
        insert oCCSUser;
        
        system.runAs(oCCSUser)
        {
            Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = 'Test Business Process and Testing', OwnerId = oNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing');
            insert oWorkQueue;    
            
            //CLASSIFICATION TYPE
            List<Classification_Type__c> lstClassType= new List<Classification_Type__c>();
            
            Classification_Type__c oClassType = new Classification_Type__c();
            oClassType.Name = 'Calls';
            oClassType.Is_Active__c = true;
            lstClassType.add(oClassType);
            
            Classification_Type__c oClassType2 = new Classification_Type__c();
            oClassType2.Name = 'Provider Calls';
            oClassType2.Is_Active__c = true;
            lstClassType.add(oClassType2);
            
            insert lstClassType;
            
            oWorkQueue.Classification_Type__c = oClassType.id;
            update oWorkQueue;
            
            
            //CLASSIFICATION
            List<Classification__c> lstClassification = new List<Classification__c>();
            
            Classification__c oClass = new Classification__c();
            oClass.Name = 'Benefits';
            oClass.Is_Active__c = true;
            lstClassification.add(oClass);
            
            Classification__c oClass2 = new Classification__c();
            oClass2.Name = 'Communication';
            oClass2.Is_Active__c = true;
            lstClassification.add(oClass2);
            
            Classification__c oClass3 = new Classification__c();
            oClass3.Name = 'Claims';
            oClass3.Is_Active__c = true;
            lstClassification.add(oClass3);
            
            insert lstClassification;
            
            
            //INTENT
            List<Intent__c> lstIntent = new List<Intent__c>();
            
            Intent__c oIntent = new Intent__c();
            oIntent.Name = 'Rewards';
            oIntent.Is_Active__c = true;
            lstIntent.add(oIntent);
            
            Intent__c oIntent2 = new Intent__c();
            oIntent2.Name = 'Welcome Call';
            oIntent2.Is_Active__c = true;
            lstIntent.add(oIntent2);
            
            Intent__c oIntent3 = new Intent__c();
            oIntent3.Name = 'Denied';
            oIntent3.Is_Active__c = true;
            lstIntent.add(oIntent3);
            
            insert lstIntent;
            
            
            //CTCI JUNCTION
            List<CTCI_Junction__c> lstCTCI = new list<CTCI_Junction__c>();
            
            CTCI_Junction__c oCTCI = new CTCI_Junction__c();
            oCTCI.Classification_Type__c = oClassType.id;
            oCTCI.Classification__c = oClass.Id;
            oCTCI.Intent__c = oIntent.id;
            oCTCI.IsActive__c = true;
            lstCTCI.add(oCTCI);
            
            CTCI_Junction__c oCTCI2 = new CTCI_Junction__c();
            oCTCI2.Classification_Type__c = oClassType.id;
            oCTCI2.Classification__c = oClass2.Id;
            oCTCI2.Intent__c = oIntent2.id;
            oCTCI2.IsActive__c = true;
            lstCTCI.add(oCTCI2);            
            
            CTCI_Junction__c oCTCI3 = new CTCI_Junction__c();
            oCTCI3.Classification_Type__c = oClassType.id;
            oCTCI3.Classification__c = oClass3.Id;
            oCTCI3.Intent__c = oIntent3.id;
            oCTCI3.IsActive__c = true;
            lstCTCI.add(oCTCI3);              
            
            insert lstCTCI;
            
            PermissionSet ps = [SELECT id, Name, Label FROM PermissionSet WHERE Name = 'CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1]; 
            
            PermissionSetAssignment psa = new PermissionSetAssignment();
            psa.AssigneeId = oCCSUser.id;
            psa.PermissionSetId = ps.Id;
            insert psa;
            
            RecordType oMemberAcctType = [SELECT Id, DeveloperName, SobjectType
                                          FROM RecordType
                                          WHERE SobjectType = 'Account' AND DeveloperName = 'Member'];
            RecordType oGroupAcctType = [SELECT Id, DeveloperName, SobjectType
                                         FROM RecordType
                                         WHERE SobjectType = 'Account' AND DeveloperName = 'Group'];       
            
            List<Account> lstAccounts = new List<Account>();
            Account oMemberAccount = new Account(
                FirstName = 'Jesse', 
                LastName = 'McCree',
                RecordTypeId = oMemberAcctType.Id,
                CoKY_Account__c = false,
                Tenant_Id__c = '00',
                General_Account__c = true,
                Humana_One_Indicator__c = false,
                Home_Office_Account__c = false
            );
            lstAccounts.add(oMemberAccount);
            Account oGroupAccount = new Account(
                Name = 'Nimble Enterprise',
                RecordTypeId = oGroupAcctType.Id,
                CoKY_Account__c = false,
                DST_Account__c = false,
                General_Account__c = true,
                Group_Number__c = 'R7567001',
                Home_Office_Account__c = false,
                Humana_One_Indicator__c = true,
                Sic = '8062',
                Source_Application_Code__c = 'CI',
                Source_Customer_Key__c = '257567',
                Source_Platform_Code__c = 'LV',
                Tenant_Id__c = '00'
            );
            lstAccounts.add(oGroupAccount);
            insert lstAccounts;
            
            Product2 oProduct = new Product2(
                Name = 'MED',
                ProductCode = 'MED',
                Description = '302 006',
                Alternate_Description__c = 'UNKNOWN', 
                HDHP_Indicator__c = 'N', 
                Major_LOB__c = 'EPO', 
                Policy_Type__c = 'I',
                Segment_Type__c = 'COM', 
                Sold_Product_Key_Value__c = '9881770101', 
                Tenant_ID__c = '00'
            );
            insert oProduct;
            
            Policy__c oPolicy = new Policy__c(
                Name = 'Business Group Legit Policy',  
                Group_Name__c = oGroupAccount.id,
                Product__c = oProduct.id,
                ASO__c = 'N', 
                Benefit_Coverage__c = '9881770101', 
                Client_Number__c = '58', 
                Exchange_Type__c = 'Federal', 
                Exchange__c = 'FEDERAL', 
                Issue_State__c = 'TX', 
                Major_LOB__c = 'EPO', 
                Metallic_Tier__c = 'Gold', 
                Exchange_Indicator__c = true, 
                Selling_Market_Number__c = '13550', 
                Source_Cust_Cov_Key__c = '257567001000', 
                Tenant_ID__c = '00', 
                Fin_Legal_Entity__c = 'hic', 
                Platform__c = 'lv', 
                Business_Segment__c = 'Individual-HumanaOne', 
                Coverage_Plan_Effective_Date__c = '01/01/2016', 
                Coverage_Plan_End_Date__c = '12/31/2016', 
                Policy_Group_Number__c = 'R7567001'
            );
            insert oPolicy;
            
            Policy_Member__c oTestPolMem = new Policy_Member__c(
                Name = 'H0011Test', 
                Member__c = oMemberAccount.id, 
                Policy__c = oPolicy.id,
                Coverage_Type__c = '01', 
                Effective_Date__c = '09/01/2015', 
                End_Date__c = '12/31/2015', 
                Tenant_ID__c = '00', 
                Relationship_Code__c = '00', 
                Source_Coverage_ID__c = '826724351', 
                Relationship__c = 'SUBSCRIBER' 
            );
            insert oTestPolMem;
            
            Interaction__c oInteraction = New Interaction__c(
                Interacting_With__c = oMemberAccount.id,
                Interacting_With_Type__c = 'Member',
                Caller_Name__c = 'testMcCree',
                Interaction_Origin__c = 'Inbound Call'
            );
            insert oInteraction;
            
            
            Interaction_Member__c oIntMem = new Interaction_Member__c(
                Interaction__c = oInteraction.id,
                Interacting_About__c = oMemberAccount.id,
                Interacting_About_Type__c = 'Member'
            );
            insert oIntMem;
            
            
            
            List<HUMConstants__c> lstConstanst = new List<HUMConstants__c>();
            lstConstanst.add(new HUMConstants__c(Name='HUMLimit1000',IntegerValue__c=1000,StringValue__c='0'));
            lstConstanst.add(new HUMConstants__c(Name='CUSTOM_EXCEPTION',IntegerValue__c=0,StringValue__c='HUMCustomException'));
            lstConstanst.add(new HUMConstants__c(Name='ERROR_APPLICATION',IntegerValue__c=0,StringValue__c='Application Error'));
            lstConstanst.add(new HUMConstants__c(Name='ERROR_INTEGRATION',IntegerValue__c=0,StringValue__c='Integration Error'));
            
            insert lstConstanst;
            
            // create QS BCM Data
            List<CTCI_Junction__c> list_ctci = [select Classification__c, Intent__c from CTCI_Junction__c 
                                                where Classification__r.Name='Benefits' and Intent__r.Name='Rewards' and IsActive__c = TRUE limit 1];
            
            Quick_Start_Pretext__c objQSP = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'RSO', Business_Grp_Class_Type__c='RSO Calls (RSO)', Recommended_Actions__c = 'HIDE;FIDE;MedicalSuppliment;DualEligible',CTCI_List_lkp__c=list_ctci.get(0).Id);
            insert objQSP;
            
            List<Quick_Start_Pretext_Section__c> listQSPS = new List<Quick_Start_Pretext_Section__c>();
            listQSPS.add(new Quick_Start_Pretext_Section__c(Quick_Start_Pretext__c = objQSP.ID, Case_Comment_Section__c='DIV_CEO'));
            listQSPS.add(new Quick_Start_Pretext_Section__c(Quick_Start_Pretext__c = objQSP.ID, Case_Comment_Section__c='DIV_AT'));
            listQSPS.add(new Quick_Start_Pretext_Section__c(Quick_Start_Pretext__c = objQSP.ID, Case_Comment_Section__c='DIV_AR', Transfer_Label__c = 'Transferred/Referred', Transfer_Values__c='CIT;DMS', Transfer_Values_with_Input__c='CIT'));  
            insert listQSPS;
            
            List<Quick_Start_Pretext_Line_Item__c> listOSPLI = new List<Quick_Start_Pretext_Line_Item__c>();
            Quick_Start_Pretext_Line_Item__c line_1 = new Quick_Start_Pretext_Line_Item__c(Quick_Start_Pretext_Section__c = listQSPS.get(0).ID, Input_Field_Type__c = 'Textonly', Display_Order__c=1);
            line_1.Input_Field_Labels__c = 'This is sample pretext for CEO.';
            listOSPLI.add(line_1);
            
            Quick_Start_Pretext_Line_Item__c line_2 = new Quick_Start_Pretext_Line_Item__c(Quick_Start_Pretext_Section__c = listQSPS.get(1).ID, Input_Field_Type__c = 'Textbox_wl', Display_Order__c=2);
            line_2.Input_Field_Labels__c = 'Other';
            listOSPLI.add(line_2);
            
            Quick_Start_Pretext_Line_Item__c line_3 = new Quick_Start_Pretext_Line_Item__c(Quick_Start_Pretext_Section__c = listQSPS.get(2).ID, Input_Field_Type__c = 'Selectbox', Display_Order__c=1, Pre_Label__c='Test Picklist');
            line_3.Input_Field_Labels__c = 'Select Value 1; select Value 2';
            listOSPLI.add(line_3);
            
            Quick_Start_Pretext_Line_Item__c line_4 = new Quick_Start_Pretext_Line_Item__c(Quick_Start_Pretext_Section__c = listQSPS.get(2).ID, Input_Field_Type__c = 'Textarea', Display_Order__c=2);
            line_4.Input_Field_Labels__c = 'Additional Info';
            listOSPLI.add(line_4);
            insert listOSPLI;
            
            List<Quick_Start_Pretext_Line_Item__c> listAddtionalLI = new List<Quick_Start_Pretext_Line_Item__c>();              
            Quick_Start_Pretext_Line_Item__c ali_1 = new Quick_Start_Pretext_Line_Item__c(Additional_Line_Item__c=listOSPLI.get(2).Id, Quick_Start_Pretext_Section__c=listQSPS.get(2).Id, Dependent_Input_Value__c='Select Value 1');
            ali_1.Display_Order__c = 0;
            ali_1.Input_Field_Type__c = 'Textbox_wl';
            ali_1.Field_Length__c = 200;
            ali_1.Field_Display_Size__c = 50;
            ali_1.Input_Field_Labels__c ='Comment:';
            listAddtionalLI.add(ali_1); 
            
            Quick_Start_Pretext_Line_Item__c ali_2 = new Quick_Start_Pretext_Line_Item__c(Additional_Line_Item__c=listOSPLI.get(2).Id, Quick_Start_Pretext_Section__c=listQSPS.get(2).Id, Dependent_Input_Value__c='Select Value 1');
            ali_2.Display_Order__c = 1;
            ali_2.Input_Field_Type__c = 'Date_cl';
            ali_2.Ghost_Text__c = 'MM/DD/YYYY';
            ali_2.Field_Length__c = 10;
            ali_2.Field_Display_Size__c = 10;
            ali_2.Input_Field_Labels__c ='Date';
            listAddtionalLI.add(ali_2); 
            insert listAddtionalLI;
            
            Id MentorLinkRecordTypeId = Schema.SObjectType.CustomLink__c.getRecordTypeInfosByName().get('Mentor Link').getRecordTypeId();
        	
            CustomLink__c NMD1 = new CustomLink__c(Name='Medicare Authorization Calls',RecordTypeId = MentorLinkRecordTypeId,Download_MentorLink__c=false,Quick_Start_Pretext__c = objQSP.Id,URL__c='http://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=0900092980db1698&dl=0&searchID=VI-8d446b54f5180ce&row=0&mode=Mentor');
       	    insert NMD1;
        
            Quick_Start_Pretext__c objQSPRSORef = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'RSO', Business_Grp_Class_Type__c='RSO Calls (RSO)');
            insert objQSPRSORef;
            
            Quick_Start_Pretext__c objQSPProviderRef = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'Provider', Business_Grp_Class_Type__c='Provider Calls');
            insert objQSPProviderRef;
            CustomLink__c RefDoc = new CustomLink__c(Name='CCP to CRM Crosswalk',RecordTypeId = MentorLinkRecordTypeId,Download_MentorLink__c=True,Quick_Start_Pretext__c = objQSPRSORef.Id,URL__c='http://dctm.humana.com/Mentor/Web/v.aspx/CCPGCPTeamstoCRMServiceWorkQueueCrosswalk?chronicleID=09000929819617ed&dl=0&searchID=VI-8d55d97a802d5dc&row=0');
            insert RefDoc;
            
            CustomLink__c RefDocPro = new CustomLink__c(Name='PCC Hot Topics',RecordTypeId = MentorLinkRecordTypeId,Download_MentorLink__c=True,Quick_Start_Pretext__c = objQSPProviderRef.Id,URL__c='http://dctm.humana.com/Mentor/Web/v.aspx/CCPGCPTeamstoCRMServiceWorkQueueCrosswalk?chronicleID=09000929819617ed&dl=0&searchID=VI-8d55d97a802d5dc&row=0');
            insert RefDocPro;

            Case caseRecord1 = New Case();
            caseRecord1.Status = 'In Progress';
            caseRecord1.AccountId = oMemberAccount.id;
            caseRecord1.ctci_list__c=oCTCI.id;
            caseRecord1.Complaint__c = 'Yes - Medicare Part C';
            caseRecord1.Complaint_Reason__c = 'Other';
            caseRecord1.Complaint_Type__c = 'Behavioral';
            caseRecord1.Interacting_About_Type__c = 'Member';
            insert caseRecord1;
        }
        
        Work_Queue_Setup__c oInsertedQueue = [SELECT Id, Name, OwnerId
                                              FROM Work_Queue_Setup__c
                                              WHERE Name = 'Test Business Process and Testing'];
        oCCSUser.Current_Queue__c = oInsertedQueue.Name;
        update oCCSUser;
        List<Permissionset> lstpermission = [Select Name,Id from PermissionSet where Name = : GLOBAL_CONSTANT_HUM.QUICKSTARTACCESS_PERMISSIONSET_HUM];
        List<PermissionSetAssignment> lstPerAssign = new List<PermissionSetAssignment>();
        if(!lstpermission.IsEmpty())
        {
        PermissionsetAssignment oPSA = new PermissionSetAssignment(PermissionSetId = lstpermission[0].Id,AssigneeId = oCCSUser.Id);
        Insert oPSA;
        }
    }
    @isTest
    static void testAssociatePolicy()
    {           
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        Work_Queue_Setup__c wQS=[select Id,Name,Department__c,Service_Center__c,Classification_Type__c,Public_Group_Queue_Id__c from Work_Queue_Setup__c where Service_Center__c='Business' ];

        //Test 1
        //Pass Associate to Policy                           
        system.runAs(oCCSUser)
        {   
            
            List<Quick_Start_Pretext__c> lstFrequentlyUsedCI = QuickStart_D_HUM.queryFrequentlyUsedCI(wQS.Classification_Type__c);
            List<SelectOption> lstFrequentlyUsedCIOption =QuickStart_S_HUM.getFrequentlyUsedCI(wQS.Classification_Type__c);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Calls' AND Is_Active__c = true];         

            //Test Classification Dropdown Values
            List<SelectOption> lstClassDropdownVals = oQuickStart.getLstClassificationOptions();
            system.assertEquals(4, lstClassDropdownVals.size());
            
            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Benefits'];
            oQuickStart.sClassificationSelect = oClsMatch.id;
            
            //Test Intent Dropdown Values           
            List<SelectOption> lstIntDropDownVals = oQuickStart.getLstIntentOptions();
            system.assertEquals(2, lstIntDropDownVals.size());
            
            Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Rewards' AND Is_Active__c = true LIMIT 1];
            oQuickStart.sIntentSelect = oIntMatch.id;
            
            //Assign QuickStart Case Values
            oQuickStart.oNewCase.G_A_Rights_Given__c = 'No';
            oQuickStart.oNewCase.Complaint__c = 'No';
            
            //Test General Assignment
            CTCI_Junction__c oCTCI = [SELECT Id, Classification_Type__c, Classification__c, Intent__c
                                      FROM CTCI_Junction__c
                                      WHERE Classification_Type__c = :oClassType.Id AND Classification__c = :oQuickStart.sClassificationSelect AND Intent__c = :oQuickStart.sIntentSelect AND IsActive__c = true];
            
            
            //Test Policy Member Assignment
            Policy_Member__c oAssocPolMem = [SELECT Id, Name, Member__c
                                             FROM Policy_Member__c
                                             WHERE Name = 'H0011Test'];
            
            Account oAssocAccount = [SELECT Id
                                     FROM Account
                                     WHERE FirstName = 'Jesse' AND LastName = 'McCree'];
            Interaction__c oAssocInt = [SELECT Id, Interacting_With__c
                                        FROM Interaction__c
                                        WHERE Caller_Name__c = 'testMcCree'];
                                        
                                        
                                                      
            oQuickStart.sFocusedObjId = oAssocPolMem.id;
            oQuickStart.bIsHPCase = false;
            oQuickStart.sInteractionId = oAssocInt.Id;           

            oQuickStart.assignCaseValues();
            system.assertEquals(oCTCI.Id, oQuickStart.oNewCase.CTCI_List__c);
            system.assertEquals(oAssocPolMem.Id, oQuickStart.oNewCase.Policy_Member__c);
            system.assertEquals(oAssocAccount.Id, oQuickStart.oNewCase.AccountId);
            system.assertEquals(oAssocInt.Id, oQuickStart.oNewCase.Interaction_ID__c);
            system.assertEquals(oAssocAccount.Id, oQuickStart.oNewCase.Interacting_With__c);
            system.assertEquals('In Progress', oQuickStart.oNewCase.Status);
            system.assertEquals(oCCSUser.Current_Queue__c, oQuickStart.oNewCase.Owner_Queue__c);
            
            
            //Save successfully
            system.assertEquals(oQuickStart.oNewCase.Id, oQuickStart.sSavedCaseId);
            
            //Fail on duplicate Classification and Intent
            oQuickStart.assignCaseValues();
            
            oQuickStart.populateDocumentLinks();
            // Test Mentor Links
          	CustomLink__c MentorLink = [Select Id from CustomLink__c where Name = 'Medicare Authorization Calls'];  
            CustomLink__c RefDoc;
            if(oQuickStart.bProviderUser)
            {
                RefDoc = [Select Id from CustomLink__c where Name = 'PCC Hot Topics'];
            }
            else
            {
               RefDoc = [Select Id from CustomLink__c where Name = 'CCP to CRM Crosswalk']; 
            }  
            Id QSPMentorId = oQuickStart.lstDocumentLink[0].Id;
            ID QSPRefId = oQuickStart.lstRFDocumentLink[0].Id;
            system.assertEquals(QSPMentorId, MentorLink.Id);
            system.assertEquals(QSPRefId, RefDoc.Id);
            
            PageReference pageRef = Page.QuickStart_VF_HUM;
            Test.setCurrentPage(pageRef);
            
            QuickStart_S_HUM.assginTaskDefaults(new Task(), oQuickStart.sSavedCaseId, '',wQS, oCCSUser);
            QuickStart_S_HUM.insertNewTask(new task());
            oQuickStart.reset();
        }   

        //Test 2
        //Break on invalid fields and receive page message
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Test.setCurrentPage(Page.QuickStart_VF_HUM);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
            
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Calls' AND Is_Active__c = true];         

            //Test Classification Dropdown Values
            List<SelectOption> lstClassDropdownVals = oQuickStart.getLstClassificationOptions();
            system.assertEquals(4, lstClassDropdownVals.size());
            

            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Communication'];
            oQuickStart.sClassificationSelect = oClsMatch.id;
            
            //Test Intent Dropdown Values           
            List<SelectOption> lstIntDropDownVals = oQuickStart.getLstIntentOptions();
            system.assertEquals(2, lstIntDropDownVals.size());
            
            Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Welcome Call' AND Is_Active__c = true LIMIT 1];
            oQuickStart.sIntentSelect = oIntMatch.id;
            
            //Assign QuickStart Case Values
            oQuickStart.oNewCase.G_A_Rights_Given__c = 'No';
            oQuickStart.oNewCase.Complaint__c = 'Yes';
            oQuickStart.oNewCase.Complaint_Reason__c = 'IPA';
            oQuickStart.oNewCase.Complaint_Type__c = 'Medical';
            
            
            oQuickStart.oNewTask.id = null;
	    oQuickStart.oNewTask.OwnerID = null;
            oQuickStart.oNewTask.Task_Owner__c = null;
            oQuickStart.sSavedTaskId=null;
            
            //Test General Assignment
            CTCI_Junction__c oCTCI = [SELECT Id, Classification_Type__c, Classification__c, Intent__c
                                      FROM CTCI_Junction__c
                                      WHERE Classification_Type__c = :oClassType.Id AND Classification__c = :oQuickStart.sClassificationSelect AND Intent__c = :oQuickStart.sIntentSelect AND IsActive__c = true];          
                        
            //Test Policy Member Assignment
            Policy_Member__c oAssocPolMem = [SELECT Id, Name, Member__c
                                             FROM Policy_Member__c
                                             WHERE Name = 'H0011Test'];
            
            Account oAssocAccount = [SELECT Id
                                     FROM Account
                                     WHERE FirstName = 'Jesse' AND LastName = 'McCree'];

            Interaction__c oAssocInt = [SELECT Id, Interacting_With__c
                                        FROM Interaction__c
                                        WHERE Caller_Name__c = 'testMcCree'];
                                             
            oQuickStart.sFocusedObjId = oAssocPolMem.id;
            oQuickStart.sInteractionId = oAssocInt.Id;
            oQuickStart.bIsHPCase = false;
            oQuickStart.assignCaseValues();
            system.assertEquals(oCTCI.Id, oQuickStart.oNewCase.CTCI_List__c);
            
    	    QuickStart_S_HUM.assginTaskDefaults(oQuickStart.oNewTask, oQuickStart.sSavedCaseId, oQuickStart.sTaskDueDate, oQuickStart.oUserWorkQueue, oQuickStart.sUserQueue);
            oQuickStart.sErrorMessage = QuickStart_S_HUM.insertNewTask(oQuickStart.oNewTask);
            
            oQuickStart.bProviderUser = true;
           
	    oQuickStart.saveTask();
            
			
            //oQuickStart.assignValuesByObject();
            system.assertEquals(oAssocPolMem.Id, oQuickStart.oNewCase.Policy_Member__c);
            system.assertEquals(oAssocAccount.Id, oQuickStart.oNewCase.AccountId);
            system.assertEquals(oAssocInt.Id, oQuickStart.oNewCase.Interaction_ID__c);
            system.assertEquals(oAssocAccount.Id, oQuickStart.oNewCase.Interacting_With__c);
            system.assertEquals('In Progress', oQuickStart.oNewCase.Status);
            system.assertEquals(oCCSUser.Current_Queue__c, oQuickStart.oNewCase.Owner_Queue__c);
                         
            List<ApexPages.Message> lstMsg = ApexPages.getMessages();  
            Test.stopTest();
        }
        
        //Test 3
        //Break on invalid dropdown values. 
        system.runAs(oCCSUser)
        {
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
  
            List<SelectOption> lstClassDropdownVals = oQuickStart.getLstClassificationOptions();
            system.assertEquals(4, lstClassDropdownVals.size());
            
            oQuickStart.sClassificationSelect = '';
            List<SelectOption> lstIntDropDownVals = oQuickStart.getLstIntentOptions();
            system.assertEquals(1, lstIntDropDownVals.size());          
        }   

        //Test 4
        //Associate to Account with New Interaction
        system.runAs(oCCSUser)
        {
            //Create new Interaction
            Account oAssocAccount = [SELECT Id
                                     FROM Account
                                     WHERE FirstName = 'Jesse' AND LastName = 'McCree']; 

            //Construct
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
            
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Calls' AND Is_Active__c = true];         
            //Test Classification Dropdown Values
            List<SelectOption> lstClassDropdownVals = oQuickStart.getLstClassificationOptions();
            system.assertEquals(4, lstClassDropdownVals.size());
        
            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Benefits'];
            oQuickStart.sClassificationSelect = oClsMatch.id;
            
            //Test Intent Dropdown Values           
            List<SelectOption> lstIntDropDownVals = oQuickStart.getLstIntentOptions();
            system.assertEquals(2, lstIntDropDownVals.size());
            
            Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Rewards' AND Is_Active__c = true LIMIT 1];
            oQuickStart.sIntentSelect = oIntMatch.id;
            
            //Assign QuickStart Case Values
            oQuickStart.oNewCase.G_A_Rights_Given__c = 'No';
            oQuickStart.oNewCase.Complaint__c = 'No';
            
            //Test General Assignment
            CTCI_Junction__c oCTCI = [SELECT Id, Classification_Type__c, Classification__c, Intent__c
                                      FROM CTCI_Junction__c
                                      WHERE Classification_Type__c = :oClassType.Id AND Classification__c = :oQuickStart.sClassificationSelect AND Intent__c = :oQuickStart.sIntentSelect AND IsActive__c = true];       
                        
            RecordType oMemCaseRecType = [SELECT Id, DeveloperName, IsActive, SobjectType 
                                          FROM RecordType
                                          WHERE SobjectType = 'Case' AND DeveloperName = 'Member_Case'];                            
            
            oQuickStart.bCreateTask = true;
            oQuickStart.bProviderUser = true;
            oQuickStart.sSavedCaseId ='test';
            //oQuickStart.sSavedCaseId = oQuickStart.oNewCase.id;
            oQuickStart.saveTask();

            List<CTCI_Junction__c> list_ctci = [select Classification__c, Intent__c from CTCI_Junction__c 
                                                where Classification__r.Name='Claims' and Intent__r.Name='Denied' and IsActive__c = TRUE limit 1];
             
            Quick_Start_Pretext__c objQSP = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'RSO', Business_Grp_Class_Type__c='RSO Calls', CTCI_List_lkp__c=list_ctci.get(0).Id);
            insert objQSP;
            
            List<SelectOption> lstFrequentlyUserCIOption = new List<SelectOption>();
            String strValue = objQSP.CTCI_List_lkp__r.Classification__c + HUMConstants.sHiphan + objQSP.CTCI_List_lkp__r.Intent__c; 
            String strLabel = objQSP.CTCI_List_lkp__r.Classification__r.Name +' :: '+ objQSP.CTCI_List_lkp__r.Intent__r.Name;
            lstFrequentlyUserCIOption.add(new SelectOption(strValue, strLabel));
                                             
            oQuickStart.sFocusedObjId = oAssocAccount.id;
	    oQuickStart.assignCaseValues();
            system.assertEquals(oCTCI.Id, oQuickStart.oNewCase.CTCI_List__c);            
        }
		//Test 5
        //when sIntentSelect is null on assignCaseValues method
        system.runAs(oCCSUser)
        {   
            
            Test.setCurrentPage(Page.QuickStart_VF_HUM);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
            
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Calls' AND Is_Active__c = true];         

            
			oQuickStart.sIntentSelect = '';
            
           
            //Test Policy Member Assignment
            Policy_Member__c oAssocPolMem = [SELECT Id, Name, Member__c
                                             FROM Policy_Member__c
                                             WHERE Name = 'H0011Test'];
            
            Account oAssocAccount = [SELECT Id
                                     FROM Account
                                     WHERE FirstName = 'Jesse' AND LastName = 'McCree'];

            Interaction__c oAssocInt = [SELECT Id, Interacting_With__c
                                        FROM Interaction__c
                                        WHERE Caller_Name__c = 'testMcCree'];
										  										
                                             
            oQuickStart.sFocusedObjId = oAssocPolMem.id;
            oQuickStart.sInteractionId = oAssocInt.Id;
            oQuickStart.bIsHPCase = false;
			oQuickStart.bRecordAccess = false;
			oQuickStart.sErrorMessage = Label.QUICKSTART_PERMISSIONMSG_HUM;
			
            oQuickStart.assignCaseValues();
			oQuickStart.populateDocumentLinks();
                       
            List<ApexPages.Message> lstMsg = ApexPages.getMessages();  
            
        }
        
        
    }
    static void testNegetaivepopulateDocumentLinksFunNew()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);

        //Test 1
        //Pass Associate to Policy                          
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Test.setCurrentPage(Page.QuickStart_VF_HUM);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
             
            oQuickStart.lstFrequentlyUserCIOption.add(new SelectOption('', Constants_C_AHT_HUM.SELECT_FREQUENTLY_CI, true)); 
            oQuickStart.sClassificationSelect ='t-t';        
            oQuickStart.bProviderUser=false;
            List<SelectOption> lstClOpt= oQuickStart.getLstClassificationOptions();
            
            oQuickStart.getLstIntentOptions();
            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Benefits'];
            oQuickStart.sClassificationSelect = oClsMatch.id;
           
            
           
            oQuickStart.populateDocumentLinks();
            
            
        }
    }
    
    @isTest
    static void testLstIntentOptions()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);
                          
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Test.setCurrentPage(Page.QuickStart_VF_HUM);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
            
            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Communication'];
            
            Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Welcome Call' AND Is_Active__c = true LIMIT 1];
                                   
            oQuickStart.sClassificationSelect = oClsMatch.Id + '-' + oIntMatch.Id;                                   
            oQuickStart.getLstIntentOptions();  
        }
     }
    
    @isTest
    static void testOverall()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);

        //Test 1
        //Pass Associate to Policy                          
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Test.setCurrentPage(Page.QuickStart_VF_HUM);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
            
            List<PermissionSetAssignment> lstCCSHPPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name='CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1];
           
            oQuickStart.bHasHPPermSet = true;
            
            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Communication'];
            
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Calls' AND Is_Active__c = true];         

           oQuickStart.getLstClassificationOptions(); 
            
           Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Welcome Call' AND Is_Active__c = true LIMIT 1];
            oQuickStart.sIntentSelect = oIntMatch.id;   
            oQuickStart.getLstIntentOptions();  
            
            //Get Case ID
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = oCCSUser.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
            Case caseRecord1 = new Case();
            caseRecord1.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord1.Status = 'In Progress';        
            caseRecord1.AccountId = accountRec.id;
            caseRecord1.ownerId = oCCSUser.Id;        
            caseRecord1.Classification_Type__c = 'Calls (RSO)';
            caseRecord1.Classification_id__c=oClsMatch.id;
            caseRecord1.Intent_id__c = oIntMatch.id;
            caseRecord1.Interacting_With__c = accountRec.id;
            caseRecord1.Interacting_With_Type__c = 'Member';
            caseRecord1.Policy_Member__c = null;
            caseRecord1.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord1.Autoroute_Status__c = 'Evaluate';
            caseRecord1.Origin = 'Inbound Call';
            caseRecord1.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
            caseRecord1.Interacting_About_Type__c = 'Member';
            insert caseRecord1;
             
             oQuickStart.populateDocumentLinks();
             oQuickStart.bCreateTask = true;
             oQuickStart.bProviderUser = true;
             oQuickStart.assignCaseValues();
             oQuickStart.saveTask();
             
             
             List<CTCI_Junction__c> list_ctci = [select Classification__c, Intent__c from CTCI_Junction__c 
                                                where Classification__r.Name='Claims' and Intent__r.Name='Denied' and IsActive__c = TRUE limit 1];
             
             Quick_Start_Pretext__c objQSP = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'RSO', Business_Grp_Class_Type__c='RSO Calls', CTCI_List_lkp__c=list_ctci.get(0).Id);
             insert objQSP;
             
             List<SelectOption> lstFrequentlyUserCIOption = new List<SelectOption>();
             String strValue = objQSP.CTCI_List_lkp__r.Classification__c + HUMConstants.sHiphan + objQSP.CTCI_List_lkp__r.Intent__c; 
             String strLabel = objQSP.CTCI_List_lkp__r.Classification__r.Name +' :: '+ objQSP.CTCI_List_lkp__r.Intent__r.Name;
             lstFrequentlyUserCIOption.add(new SelectOption(strValue, strLabel));
             oQuickStart.sSavedCaseId = caseRecord1.Id; //passing case Id
             oQuickStart.assignCaseValues();
             oQuickStart.saveTask();
        }
    }

    @isTest
    static void testAssignCaseValuesNegative()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);
                         
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Test.setCurrentPage(Page.QuickStart_VF_HUM);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
            
            List<PermissionSetAssignment> lstCCSHPPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name='CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1];
            oQuickStart.bHasHPPermSet = true;
            
            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Communication'];
            
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Calls' AND Is_Active__c = true];         

           oQuickStart.getLstClassificationOptions();
            
            
             Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Welcome Call' AND Is_Active__c = true LIMIT 1];
             oQuickStart.sIntentSelect = oIntMatch.id;   
             oQuickStart.getLstIntentOptions(); 
             
             CTCI_Junction__c ctci = [select Classification__c, Intent__c from CTCI_Junction__c 
                                                where Classification__r.Name='Claims' and Intent__r.Name='Denied' and IsActive__c = TRUE limit 1];
             oQuickStart.assignCaseValues();
             oQuickStart.saveTask();
        }
    }
    
    @isTest
    static void testLstClassificationOptions()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
                          
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Test.setCurrentPage(Page.QuickStart_VF_HUM);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
            
            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Claims'];
            Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Denied' AND Is_Active__c = true LIMIT 1];
            
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Provider calls' AND Is_Active__c = true]; 
            
            CTCI_Junction__c oCTCI = new CTCI_Junction__c();
            oCTCI.Classification_Type__c = oClassType.id;
            oCTCI.Classification__c = oClsMatch.Id;
            oCTCI.Intent__c = oIntMatch.id;
            oCTCI.IsActive__c = true;
            insert oCTCI;
            
            Quick_Start_Pretext__c objQSP = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'Provider', Business_Grp_Class_Type__c='Provider Calls', CTCI_List_lkp__c=oCTCI.Id);
            insert objQSP;
            
            oQuickStart.getLstClassificationOptions();
              
        }
     }
    
    @isTest
    public static void testSaveTask()
    {
       
			User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];	
            Work_Queue_Setup__c wQS=[select Id,Name,Department__c,Service_Center__c,Classification_Type__c,Public_Group_Queue_Id__c from Work_Queue_Setup__c where Service_Center__c='Business' ];
		
      system.runAs(oCCSUser)
        {

		 Task obj = new Task();
         insert obj;
         QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
         oQuickStart.bCreateTask = true;
         oQuickStart.bProviderUser = false;
         oQuickStart.sSavedCaseId ='0017c00000l7QtQAAU';
         oQuickStart.oNewTask=obj;
         oQuickStart.sTaskDueDate='12/12/2018';
         oQuickStart.bErrorNoSave=true;
         Test.startTest();
         oQuickStart.saveTask();
         Test.stopTest();
		}					  
    }
    
    @isTest
    static void testPretextGA()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);
    
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Test.setCurrentPage(Page.QuickStart_VF_HUM);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM(); 
            oQuickStart.sOGOFieldValue = 'Complaint';
            oQuickStart.sOGOReasonValue = 'Claims';
            oQuickStart.getAFPretextGA();
        }
    }
    
    @isTest
    static void testNegativePretextGA()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);
    
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Test.setCurrentPage(Page.QuickStart_VF_HUM);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM(); 
            oQuickStart.sOGOFieldValue = '';
            oQuickStart.sOGOReasonValue = '';
            oQuickStart.getAFPretextGA();
            system.assert(String.isBlank(oQuickStart.sPretextGAJSON));
        }
    }
    
    @isTest
    static void testNegetaivenew()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);

        //Test 1
        //Pass Associate to Policy                          
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Test.setCurrentPage(Page.QuickStart_VF_HUM);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
              oQuickStart.lstFrequentlyUserCIOption.add(new SelectOption('', Constants_C_AHT_HUM.SELECT_FREQUENTLY_CI, true)); 
              oQuickStart.sClassificationSelect ='test-test';        
             oQuickStart.bProviderUser=false;
             oQuickStart.getLstClassificationOptions();
             oQuickStart.getLstIntentOptions();
              
            
            
            
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Calls' AND Is_Active__c = true];         

            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Communication'];
            oQuickStart.sClassificationSelect = oClsMatch.id;
                                
             oQuickStart.assignCaseValues();
             oQuickStart.populateDocumentLinks();
             oQuickStart.bCreateTask = true;
             oQuickStart.bProviderUser = true;
             oQuickStart.sSavedCaseId ='test';
             oQuickStart.saveTask();
           
        }
    }


    @isTest
    static void testCheckDuplicateGrievance()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);

                                 
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Test.setCurrentPage(Page.QuickStart_VF_HUM);
            QuickStart_C_HUM oQuickStart = new QuickStart_C_HUM();
            
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Calls' AND Is_Active__c = true];         

            //Test Classification Dropdown Values
            List<SelectOption> lstClassDropdownVals = oQuickStart.getLstClassificationOptions();
       
            

            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Benefits'];
            oQuickStart.sClassificationSelect = oClsMatch.id;
            
            //Test Intent Dropdown Values           
            List<SelectOption> lstIntDropDownVals = oQuickStart.getLstIntentOptions();
          
            
            Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Rewards' AND Is_Active__c = true LIMIT 1];
            oQuickStart.sIntentSelect = oIntMatch.id;
            
            //Assign QuickStart Case Values
            oQuickStart.oNewCase.G_A_Rights_Given__c = 'No';
            oQuickStart.oNewCase.Complaint__c = 'Yes - Medicare Part C';
            oQuickStart.oNewCase.Complaint_Reason__c = 'IPA';
            oQuickStart.oNewCase.Complaint_Type__c = 'Medical';
            
            
            //Test General Assignment
            CTCI_Junction__c oCTCI = [SELECT Id, Classification_Type__c, Classification__c, Intent__c
                                      FROM CTCI_Junction__c
                                      WHERE Classification_Type__c = :oClassType.Id AND Classification__c = :oQuickStart.sClassificationSelect AND Intent__c = :oQuickStart.sIntentSelect AND IsActive__c = true];          
                        
            //Test Policy Member Assignment
            Policy_Member__c oAssocPolMem = [SELECT Id, Name, Member__c
                                             FROM Policy_Member__c
                                             WHERE Name = 'H0011Test'];
            
            Account oAssocAccount = [SELECT Id
                                     FROM Account
                                     WHERE FirstName = 'Jesse' AND LastName = 'McCree'];

            Interaction__c oAssocInt = [SELECT Id, Interacting_With__c
                                        FROM Interaction__c
                                        WHERE Caller_Name__c = 'testMcCree'];
                                        
            oQuickStart.sFocusedObjId = oAssocPolMem.id;
            oQuickStart.sInteractionId = oAssocInt.Id;
            oQuickStart.sAccountId = oAssocAccount.id;

            oQuickStart.checkDuplicateGrievance();
            Test.stopTest();
        }
    }
}