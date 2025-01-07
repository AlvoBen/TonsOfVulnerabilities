/**********************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_RXRF_T_HUM.cls 
Created Date     : May 29, 2020
Function         : Executes SOQL in support of the Quick Start Console Component
Test Class       : QuickStart_RXRF_T_HUM

**Modification Log
* Developer Name                   Date                       Description
*----------------------------------------------------------------------------------------------------------------------
* Kiran Kumar                     05/29/2020                  Original Version
* Kavya Bondada					  01/05/2021				  User Story 1745160: PR00091574 - MF 1 - AHT - Ability to configure Info/Warning Message on Quick Start using Business Configuration (CRM) : Clean up of Is Auto Logging Enabled (Is_Auto_Logging_Enabled__c) from Quick Start Pretext (Quick_Start_Pretext__c)
* Kiran Bhuvanagiri				  01/17/2021			      User Story: 1600241: Humana Pharmacy Quick Start+ - Display Informational Message When Case in History with Same Classification (CRMS)
* Aaron Speakman                  05/11/2020                  Code Coverage Improvements
* Pinky Vijur                     05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
* Kiran Bhuvanagiri		          11/05/2021				  User Story 2649253: Test Class Improvements
***************************************************************************************************************************/
@isTest
private class QuickStart_RXRF_T_HUM
{   
    Public Static final String QUICKSTARTACCESS_PERMISSIONSET_HUM = 'CRMS_650_Quick_Start_Access';
    @testSetup static void setup()
    {
        Group oNewGroup = null;
        oNewGroup = new Group(Name ='Humana Pharmacy Calls PGQ', Type = 'Queue');
        insert oNewGroup;

        QueueSObject oNewQueue = new QueueSObject(QueueId = oNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;

        User oCCSUser = HUMTestDataHelperTest.createTestUser('QuickStartSpecialist','Humana Pharmacy Specialist');
        insert oCCSUser;
       
        system.runAs(oCCSUser)
        {
            Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = 'Humana Pharmacy Calls', OwnerId = oNewGroup.Id, Service_Center__c = 'Humana Pharmacy Calls', Department__c = 'Calls');
            insert oWorkQueue;    
            
             HUMAccountCaseRecordTypes__c recordtypesMapping = new HUMAccountCaseRecordTypes__c();
        recordtypesMapping.Case_RecordType__c = 'HP Member Case';
        recordtypesMapping.Humana_Pharmacy__c = true;
        recordtypesMapping.Name = 'HP Member';
      
        recordtypesMapping.Account_RecordType__c = 'Member';
          Insert recordtypesMapping;
            
            //CLASSIFICATION TYPE
            List<Classification_Type__c> lstClassType= new List<Classification_Type__c>();
            
            Classification_Type__c oClassType = new Classification_Type__c();
            oClassType.Name = 'Humana Pharmacy Calls';
            oClassType.Is_Active__c = true;
            lstClassType.add(oClassType);
            
            insert lstClassType;
            
            oWorkQueue.Classification_Type__c = oClassType.id;
            update oWorkQueue;
            
            
            //CLASSIFICATION
            List<Classification__c> lstClassification = new List<Classification__c>();
            
            Classification__c oClass = new Classification__c();
            oClass.Name = 'Supervisor';
            oClass.Is_Active__c = true;
            lstClassification.add(oClass);
            
            Classification__c oClass2 = new Classification__c();
            oClass2.Name = 'Repeat Caller';
            oClass2.Is_Active__c = true;
            lstClassification.add(oClass2);
            Classification__c oClass3 = new Classification__c();
            oClass3.Name = 'Billing Issue (CT & TM)';
            oClass3.Is_Active__c = true;
            
            lstClassification.add(oClass3);
            
            insert lstClassification;
            
            
            //INTENT
            List<Intent__c> lstIntent = new List<Intent__c>();
            
            Intent__c oIntent = new Intent__c();
            oIntent.Name = 'Issue Resolved';
            oIntent.Is_Active__c = true;
            lstIntent.add(oIntent);
            
            Intent__c oIntent2 = new Intent__c();
            oIntent2.Name = 'Cancelled';
            oIntent2.Is_Active__c = true;
            lstIntent.add(oIntent2);
            
            Intent__c oIntent3 = new Intent__c();
            oIntent3.Name = 'Invoice Requests';
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
            oCTCI.Service_Model_Type__c = 'Humana Pharmacy';
            lstCTCI.add(oCTCI);
            
            CTCI_Junction__c oCTCI2 = new CTCI_Junction__c();
            oCTCI2.Classification_Type__c = oClassType.id;
            oCTCI2.Classification__c = oClass2.Id;
            oCTCI2.Intent__c = oIntent2.id;
            oCTCI2.IsActive__c = true;
            oCTCI2.Service_Model_Type__c = 'Insurance/Plan';
            lstCTCI.add(oCTCI2);  
            
            CTCI_Junction__c oCTCI3 = new CTCI_Junction__c();
            oCTCI3.Classification_Type__c = oClassType.id;
            oCTCI3.Classification__c = oClass3.Id;
            oCTCI3.Intent__c = oIntent3.id;
            oCTCI3.IsActive__c = true;
            lstCTCI.add(oCTCI3);  
            
            insert lstCTCI;
            
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
                                                where Classification__r.Name='Supervisor' and Intent__r.Name='Issue Resolved' and IsActive__c = TRUE limit 1];
            
            Quick_Start_Pretext__c objQSP = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'Humana Pharmacy Calls', Business_Grp_Class_Type__c='Humana Pharmacy Calls Humana Pharmacy Calls', CTCI_List_lkp__c=list_ctci.get(0).Id);
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
        
            Quick_Start_Pretext__c objQSPRSORef = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'Humana Pharmacy Calls', Business_Grp_Class_Type__c='Humana Pharmacy Calls Humana Pharmacy Calls');
            insert objQSPRSORef;
            
            Quick_Start_Pretext__c objQSPProviderRef = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'Humana Pharmacy Calls', Business_Grp_Class_Type__c='Humana Pharmacy Calls Humana Pharmacy Calls');
            insert objQSPProviderRef;
            CustomLink__c RefDoc = new CustomLink__c(Name='CCP to CRM Crosswalk',RecordTypeId = MentorLinkRecordTypeId,Download_MentorLink__c=True,Quick_Start_Pretext__c = objQSPRSORef.Id,URL__c='http://dctm.humana.com/Mentor/Web/v.aspx/CCPGCPTeamstoCRMServiceWorkQueueCrosswalk?chronicleID=09000929819617ed&dl=0&searchID=VI-8d55d97a802d5dc&row=0');
            insert RefDoc;
            
            CustomLink__c RefDocPro = new CustomLink__c(Name='PCC Hot Topics',RecordTypeId = MentorLinkRecordTypeId,Download_MentorLink__c=True,Quick_Start_Pretext__c = objQSPProviderRef.Id,URL__c='http://dctm.humana.com/Mentor/Web/v.aspx/CCPGCPTeamstoCRMServiceWorkQueueCrosswalk?chronicleID=09000929819617ed&dl=0&searchID=VI-8d55d97a802d5dc&row=0');
            insert RefDocPro;
        }
        
        Work_Queue_Setup__c oInsertedQueue = [SELECT Id, Name, OwnerId
                                              FROM Work_Queue_Setup__c
                                              WHERE Name = 'Humana Pharmacy Calls'];
        oCCSUser.Current_Queue__c = oInsertedQueue.Name;
        update oCCSUser;
        List<Permissionset> lstpermission = [Select Name,Id from PermissionSet where Name = : QUICKSTARTACCESS_PERMISSIONSET_HUM];
        List<PermissionSetAssignment> lstPerAssign = new List<PermissionSetAssignment>();
        if(!lstpermission.IsEmpty())
        {
        PermissionsetAssignment oPSA = new PermissionSetAssignment(PermissionSetId = lstpermission[0].Id,AssigneeId = oCCSUser.Id);
        Insert oPSA;
        }
    }

    static testMethod void testAssociatePolicy()
    {           
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];

        //Test 1
        //Pass Associate to Policy                           
        system.runAs(oCCSUser)
        {   
            QuickStart_RXRF_C_HUM oQuickStart = new QuickStart_RXRF_C_HUM();
            
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Humana Pharmacy Calls' AND Is_Active__c = true];         

            //Test Classification Dropdown Values
            List<SelectOption> lstClassDropdownVals = oQuickStart.getLstClassificationOptions();
            system.assertEquals(4, lstClassDropdownVals.size());
            
            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Supervisor'];
            oQuickStart.sClassificationSelect = oClsMatch.id;
            
            //Test Intent Dropdown Values           
            List<SelectOption> lstIntDropDownVals = oQuickStart.getLstIntentOptions();
            system.assertEquals(2, lstIntDropDownVals.size());
            
            Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Issue Resolved' AND Is_Active__c = true LIMIT 1];
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
            //oQuickStart.bIsHPCase = false;
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
            //oQuickStart.saveCase();
            system.assert(oQuickStart.bErrorNoSave == false);
            system.assertEquals(oQuickStart.oNewCase.Id, oQuickStart.sSavedCaseId);
            
            //Fail on duplicate Classification and Intent
            //oQuickStart.assignCaseValues();
            //system.assert(oQuickStart.bErrorDuplicClassIntent == true);
            
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
            
            oQuickStart.sClassificationSelect = 'FakeId';
            oQuickStart.populateDocumentLinks();
            
            PageReference pageRef = Page.QuickStart_RXRF_VF_HUM;
            Test.setCurrentPage(pageRef);
            oQuickStart.reset();
        }   

        //Test 2
        //Break on invalid fields and receive page message
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Test.setCurrentPage(Page.QuickStart_RXRF_VF_HUM);
            QuickStart_RXRF_C_HUM oQuickStart = new QuickStart_RXRF_C_HUM();
            
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Humana Pharmacy Calls' AND Is_Active__c = true];         

            //Test Classification Dropdown Values
            List<SelectOption> lstClassDropdownVals = oQuickStart.getLstClassificationOptions();
            system.assertEquals(4, lstClassDropdownVals.size());
            

            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Repeat Caller'];
            oQuickStart.sClassificationSelect = oClsMatch.id;
            
            //Test Intent Dropdown Values           
            List<SelectOption> lstIntDropDownVals = oQuickStart.getLstIntentOptions();
            system.assertEquals(2, lstIntDropDownVals.size());
            
            Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Cancelled' AND Is_Active__c = true LIMIT 1];
            oQuickStart.sIntentSelect = oIntMatch.id;
            
            //Assign QuickStart Case Values
            oQuickStart.oNewCase.G_A_Rights_Given__c = 'No';
            oQuickStart.oNewCase.Complaint__c = 'Yes';
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
            //oQuickStart.bIsHPCase = false;
            oQuickStart.assignCaseValues();
            system.assertEquals(oCTCI.Id, oQuickStart.oNewCase.CTCI_List__c);
            
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
            QuickStart_RXRF_C_HUM oQuickStart = new QuickStart_RXRF_C_HUM();
  
            List<SelectOption> lstClassDropdownVals = oQuickStart.getLstClassificationOptions();
            system.assertEquals(4, lstClassDropdownVals.size());

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
            QuickStart_RXRF_C_HUM oQuickStart = new QuickStart_RXRF_C_HUM();
            
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Humana Pharmacy Calls' AND Is_Active__c = true];         
            //Test Classification Dropdown Values
            List<SelectOption> lstClassDropdownVals = oQuickStart.getLstClassificationOptions();
            system.assertEquals(4, lstClassDropdownVals.size());
            
            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Supervisor'];
            oQuickStart.sClassificationSelect = oClsMatch.id;
            
            //Test Intent Dropdown Values           
            List<SelectOption> lstIntDropDownVals = oQuickStart.getLstIntentOptions();
            system.assertEquals(2, lstIntDropDownVals.size());
            
            Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Issue Resolved' AND Is_Active__c = true LIMIT 1];
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
                                             
            oQuickStart.sFocusedObjId = oAssocAccount.id;
            QuickStart_RXRF_C_HUM.getAccountRXRF(oAssocAccount.id, oQuickStart.sClassificationSelect, oQuickStart.sIntentSelect); 
            oQuickStart.assignCaseValues();
            system.assertEquals(oCTCI.Id, oQuickStart.oNewCase.CTCI_List__c);            
            system.assertEquals(oAssocAccount.Id, oQuickStart.oNewCase.AccountId);   

            Classification__c oClsMatch2 = [SELECT Id, Name FROM Classification__c WHERE Name = 'Supervisor'];
            oQuickStart.sClassificationSelect = oClsMatch2.Id;
            oQuickStart.assignCaseValues();

            oQuickStart.sClassificationSelect = 'FakeId';
            oQuickStart.assignCaseValues();

            oQuickStart.sClassificationSelect = null;
            oQuickStart.assignCaseValues();

            oQuickStart.sFocusedObjId = null;
            oQuickStart.assignCaseValues();

            QuickStart_RXRF_C_HUM.sClassificationName = 'Test';
            oQuickStart.blegacyDelete = false;
            oQuickStart.bPharmacyUserEditAccess = false;

            oQuickStart.sClassificationSelect = 'Test-Frequent';
            oQuickStart.getLstIntentOptions();      
        }
    }

    static testMethod void KirantestAssociatePolicy()
    {           
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
                         
         //Test 5
        //Get Account testing by Kiran
        system.runAs(oCCSUser)
        {
            //Create new Interaction
            Account oAssocAccount = [SELECT Id
                                     FROM Account
                                     WHERE FirstName = 'Jesse' AND LastName = 'McCree']; 
            
            //Construct
            QuickStart_RXRF_C_HUM oQuickStart = new QuickStart_RXRF_C_HUM();
            
            //Setup Classification Type
            Classification_Type__c oClassType = [SELECT Id, Name, Is_Active__c 
                                                 FROM Classification_Type__c 
                                                 WHERE Name = 'Humana Pharmacy Calls' AND Is_Active__c = true];         
            //Test Classification Dropdown Values
            List<SelectOption> lstClassDropdownVals = oQuickStart.getLstClassificationOptions();
            system.assertEquals(4, lstClassDropdownVals.size());
            
            Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Supervisor'];
            oQuickStart.sClassificationSelect = oClsMatch.id;
            
            //Test Intent Dropdown Values           
            List<SelectOption> lstIntDropDownVals = oQuickStart.getLstIntentOptions();
            system.assertEquals(2, lstIntDropDownVals.size());
            
            Intent__c oIntMatch = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Issue Resolved' AND Is_Active__c = true LIMIT 1];
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
                                          WHERE SobjectType = 'Case' AND DeveloperName = 'HP_Member_Case'];                         
                                             
            oQuickStart.sFocusedObjId = oAssocAccount.id;
           
            oQuickStart.assignCaseValues();
            system.assertEquals(oCTCI.Id, oQuickStart.oNewCase.CTCI_List__c);            
            system.assertEquals(oAssocAccount.Id, oQuickStart.oNewCase.AccountId);
            
            QuickStart_RXRF_C_HUM.getAccountRXRF(oQuickStart.oNewCase.id, oQuickStart.sClassificationSelect, oQuickStart.sIntentSelect); 
            //Kiran changes
            QuickStart_RXRF_C_HUM.sFocusedAccountId = oAssocAccount.id;
            QuickStart_RXRF_C_HUM.sSelectedClassiifcationId = oClsMatch.id;
            oQuickStart.check14daysCase();
            oQuickStart.bPharmacyUserEditAccess = false;
            oQuickStart.blegacyDelete = false;
            QuickStart_RXRF_C_HUM.sClassificationName = 'HP';
            //Case updatecase1 = new Case(Id = oQuickStart.oNewCase.id,status='Closed');
            //update updatecase1;
            //QuickStart_RXRF_C_HUM.getAccountRXRF(oQuickStart.oNewCase.id, oQuickStart.sClassificationSelect, oQuickStart.sIntentSelect); 
            //
            QuickStart_RXRF_C_HUM oQuickStart1 = new QuickStart_RXRF_C_HUM();
            
            Classification__c oClsMatch1 = [SELECT Id, Name FROM Classification__c WHERE Name = 'Billing Issue (CT & TM)'];
            oQuickStart1.sClassificationSelect = oClsMatch1.id;
            oQuickStart1.sFocusedObjId = oAssocAccount.id;
            
            Intent__c oIntMatch1 = [SELECT Id, Name
                                   FROM Intent__c
                                   WHERE Name = 'Invoice Requests' AND Is_Active__c = true LIMIT 1];
            oQuickStart1.sIntentSelect = oIntMatch1.id;
            //Assign QuickStart Case Values
            oQuickStart1.oNewCase.G_A_Rights_Given__c = 'No';
            oQuickStart1.oNewCase.Complaint__c = 'No';
            
            //Test General Assignment
            CTCI_Junction__c oCTCI1 = [SELECT Id, Classification_Type__c, Classification__c, Intent__c
                                      FROM CTCI_Junction__c
                                      WHERE Classification_Type__c = :oClassType.Id AND Classification__c = :oQuickStart1.sClassificationSelect AND Intent__c = :oQuickStart1.sIntentSelect AND IsActive__c = true]; 
            oQuickStart1.assignCaseValues();
            QuickStart_RXRF_C_HUM.getAccountRXRF(oQuickStart1.oNewCase.id, oClsMatch1.name, oIntMatch1.name); 
        }
                         
         //Test 6
        //Get Account testing by Kiran for policy memmber
        system.runAs(oCCSUser)
        {
             //Test Policy Member Assignment
            Policy_Member__c oAssocPolMem = [SELECT Id, Name, Member__c
                                             FROM Policy_Member__c
                                             WHERE Name = 'H0011Test'];
            QuickStart_RXRF_C_HUM.getAccountRXRF(oAssocPolMem.id, null, null);
        }

         //Test checkServiceModelType
         system.runAs(oCCSUser)
         {                              
             Classification__c oClsMatch = [SELECT Id, Name FROM Classification__c WHERE Name = 'Supervisor'];
 
             Intent__c oIntMatch = [SELECT Id, Name
                                 FROM Intent__c
                                 WHERE Name = 'Issue Resolved' AND Is_Active__c = true LIMIT 1];
 
             QuickStart_RXRF_C_HUM.checkServiceModelType(null,null);
             QuickStart_RXRF_C_HUM.checkServiceModelType('FakeId','FakeId');
			 QuickStart_RXRF_C_HUM.checkServiceModelType(oClsMatch.Id,oIntMatch.Id);
             //Boolean checkBool = QuickStart_RXRF_C_HUM.checkServiceModelType(oClsMatch.Id,oIntMatch.Id);
             //system.assertEquals(true, checkBool);
             QuickStart_RXRF_C_HUM.checkServiceModelType(oClsMatch.Id,oIntMatch.Id);
 
             Classification__c oClsMatch2 = [SELECT Id, Name FROM Classification__c WHERE Name = 'Repeat Caller'];
 
             Intent__c oIntMatch2 = [SELECT Id, Name
                                 FROM Intent__c
                                 WHERE Name = 'Cancelled' AND Is_Active__c = true LIMIT 1];
 
			QuickStart_RXRF_C_HUM.checkServiceModelType(oClsMatch2.Id,oIntMatch2.Id);
			 //checkBool = QuickStart_RXRF_C_HUM.checkServiceModelType(oClsMatch2.Id,oIntMatch2.Id);
             //system.assertEquals(false, checkBool);
             QuickStart_RXRF_C_HUM.checkServiceModelType(oClsMatch2.Id,oIntMatch2.Id);
 
             oCCSUser.Current_Queue__c = '';
             update oCCSUser;
             QuickStart_RXRF_C_HUM.checkServiceModelType('FakeId','FakeId');
         }    
                                    
     }
}