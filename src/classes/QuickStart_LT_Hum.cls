/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_LT_HUM.cls 
Created Date     : March 2, 2022
Function         : Test class for QUick start lightening
Apex Class       : QuickStart_LC_HUM

**Modification Log
* Developer Name          Code Review #               Date                       Description
* Jasmeen Shangari            -----                 03/16/2022                 Original Version
* Pooja Kumbhar									    08/02/2022				   US:3230754 - Lightning - Quick Start - MVP Core - G & A Complaints case comment Pretext Data 
* Pooja Kumbhar									    09/02/2022				   US:3272646 - Lightning - Quick Start - RCC Case Comment section
* Pooja Kumbhar										09/12/2022				   US:3705153 - Lightning - Quick Start - RCC Specific - Task Information Section 
* Jasmeen Shangari									09/27/2022				   US:3230697 - Check to see if current user Is RSOHP user And selected CI belongs to HP
* Pooja Kumbhar										09/27/2022				   US:3755319 and US:3755922 - Reference and Mentor document opening in LWC 
* Ajay Chakradhar                                   09/17/2023                 US:4874911 - Lightning - Mentor Documents
************************************************************************************************************************************************************************************/
@isTest
private class QuickStart_LT_Hum {

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
            
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            RecordType AccountRecordType1 = [Select Id from RecordType where Name='Provider' and sObjectType='Account'];
        
        
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = oCCSUser.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
        	system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = 'Test Business Process and Testing', OwnerId = oNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing');
            insert oWorkQueue;   
            
            Interaction__C oInt = new Interaction__C();
			oInt.Caller_name__C = 'Test Caller';
			oInt.Interacting_with_type__C = 'Member';
			oInt.Interacting_with__c = accountRec.Id;
			oInt.Interaction_Origin__c = 'Inbound Call';
			oInt.OwnerId = oCCSUser.Id;
			insert oInt ;
			system.assertEquals(accountRec.Id,[Select Interacting_With__c from Interaction__c where id=:oInt.Id].Interacting_With__c);
			
			Interaction_Member__c oIntMem = new Interaction_Member__c(
											Interaction__c = oInt.id,
											Interacting_About__c = accountRec.Id,
											Interacting_About_Type__c = 'Member'
											);
			insert oIntMem;
			system.assertEquals(oInt.id,[Select Interaction__c from Interaction_Member__c where id=:oIntMem.id].Interaction__c);
				
				//Creating Account Record
            Account accountRec1 = HUMTestDataHelperTest.getAccount();
            accountRec1.OwnerId = oCCSUser.id;
            accountRec1.Name = 'Test1';
            accountRec1.NPI_ID__c = '1861597825';
            accountRec1.Enterprise_Id__c = '12345678';
            accountRec1.recordtypeId = AccountRecordType1.id;
            insert accountRec1;
			
			system.assertEquals(accountRec1.recordtypeId==AccountRecordType1.id,true);
                  
            Interaction__C oInt1 = new Interaction__C();
			oInt1.Caller_name__C = 'Test Caller';
			oInt1.Interacting_with_type__C = 'Provider';
			oInt1.Interacting_with__c = accountRec1.Id;
			oInt1.Interaction_Origin__c = 'Inbound Call';
			oInt1.OwnerId = oCCSUser.Id;
			insert oInt1 ;
			system.assertEquals(accountRec1.Id,[Select Interacting_With__c from Interaction__c where id=:oInt1.Id].Interacting_With__c);
			
			Interaction_Member__c oIntMem1 = new Interaction_Member__c(
											Interaction__c = oInt1.id,
											Interacting_About__c = accountRec1.Id,
											Interacting_About_Type__c = 'Provider'
											);
			insert oIntMem1;
			system.assertEquals(oInt1.id,[Select Interaction__c from Interaction_Member__c where id=:oIntMem1.id].Interaction__c);
				
				
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
            
            CTCI_Junction__c oCTCI0 = new CTCI_Junction__c();
            oCTCI0.Classification_Type__c = oWorkQueue.Classification_Type__c;
            oCTCI0.Classification__c = oClass.Id;
            oCTCI0.Intent__c = oIntent.id;
            oCTCI0.IsActive__c = true;
            insert oCTCI0;
            
            CTCI_Junction__c oCTCI = new CTCI_Junction__c();
            oCTCI.Classification_Type__c = oClassType.id;
            oCTCI.Classification__c = oClass.Id;
            oCTCI.Intent__c = oIntent.id;
            oCTCI.IsActive__c = true;
            lstCTCI.add(oCTCI);
            
            CTCI_Junction__c oCTCI2 = new CTCI_Junction__c();
            oCTCI2.Classification_Type__c = oClassType.id;
            oCTCI2.Classification__c = oClass.Id;
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
            
            Case caseRecord = New Case();
            caseRecord.Owner_Queue__c = 'Test Business Process and Testing';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = accountRec.id;
            caseRecord.ownerId = oCCSUser.Id;
            caseRecord.Classification_Type__c = 'calls';
            caseRecord.Classification_id__c=oClass3.id;
            caseRecord.Intent_id__c = oIntent3.id;
            caseRecord.Interacting_With__c = accountRec.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Origin = 'Inbound Call';
            caseRecord.Complaint__c = 'Yes - Medicare Part C';
            caseRecord.Complaint_Reason__c = 'Claims';
            caseRecord.Classification_Type__c = 'Calls';
            caseRecord.G_A_Rights_Given__c ='No';
            caseRecord.CTCI_List__c = oCTCI3.id;
            insert caseRecord;
            
            HUMConstants__c oHUMObj = new HUMConstants__c(Name='HUMLastCaseComment', StringValue__c=Label.HUMNoCaseComments);
            insert oHUMObj;
            System.assertEquals(oHUMObj.Name=='HUMLastCaseComment',true);
            
            HUMConstants__c oHUMObj1 = new HUMConstants__c(Name='CaseCommentSize', StringValue__c= '200');
            insert oHUMObj1;
            System.assertEquals(oHUMObj1.Name=='CaseCommentSize',true);
            
            HUMConstants__c oHUMObj2 = new HUMConstants__c(Name='HUMConstantOne', IntegerValue__c= 1);
            insert oHUMObj2;
            System.assertEquals(oHUMObj2.Name=='HUMConstantOne',true);
            
            HUMConstants__c oHUMObj3 = new HUMConstants__c(Name='HUMLimit1000', IntegerValue__c= 1000);
            insert oHUMObj3;
            System.assertEquals(oHUMObj3.Name=='HUMLimit1000',true);
                        
            Quick_Start_Pretext__c objQSP = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'RSO', Business_Grp_Class_Type__c='RSO Calls (RSO)', Recommended_Actions__c = 'HIDE;FIDE;MedicalSuppliment;DualEligible', is_Frequently_Used__c = true,CTCI_List_lkp__c=oCTCI.Id);
            insert objQSP;
                
             Quick_Start_Pretext__c objQSPcustomlink = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'RSO', Business_Grp_Class_Type__c='RSO Calls (RSO)', Recommended_Actions__c = 'HIDE;FIDE;MedicalSuppliment;DualEligible', is_Frequently_Used__c = true,CTCI_List_lkp__c=oCTCI0.Id);
            insert objQSPcustomlink;
            
            List<Quick_Start_Pretext_Section__c> listQSPS = new List<Quick_Start_Pretext_Section__c>();
            listQSPS.add(new Quick_Start_Pretext_Section__c(Quick_Start_Pretext__c = objQSP.ID, Case_Comment_Section__c='DIV_CEO'));
            listQSPS.add(new Quick_Start_Pretext_Section__c(Quick_Start_Pretext__c = objQSP.ID, Case_Comment_Section__c='DIV_AT'));
            listQSPS.add(new Quick_Start_Pretext_Section__c(Quick_Start_Pretext__c = objQSP.ID, Case_Comment_Section__c='DIV_AR', Transfer_Label__c = 'Transferred/Referred', Transfer_Values__c='CIT;DMS', Transfer_Values_with_Input__c='CIT'));  
            listQSPS.add(new Quick_Start_Pretext_Section__c(Quick_Start_Pretext__c = objQSP.ID, Case_Comment_Section__c='DIV_GA'));
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
                
             CustomLink__c NMD2 = new CustomLink__c(Name='Medicare Authorization Calls',RecordTypeId = MentorLinkRecordTypeId,Download_MentorLink__c=false,Quick_Start_Pretext__c = objQSPcustomlink.Id,URL__c='http://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=0900092980db1698&dl=0&searchID=VI-8d446b54f5180ce&row=0&mode=Mentor');
       	    insert NMD2;
        
            Quick_Start_Pretext__c objQSPRSORef = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'RSO', Business_Grp_Class_Type__c='RSO Calls (RSO)');
            insert objQSPRSORef;
            
            Quick_Start_Pretext__c objQSPProviderRef = new Quick_Start_Pretext__c(Is_Active__c=TRUE, Business_Groups__c = 'Provider', Business_Grp_Class_Type__c='Provider Calls');
            insert objQSPProviderRef;
            CustomLink__c RefDoc = new CustomLink__c(Name='CCP to CRM Crosswalk',RecordTypeId = MentorLinkRecordTypeId,Download_MentorLink__c=True,Quick_Start_Pretext__c = objQSPRSORef.Id,URL__c='http://dctm.humana.com/Mentor/Web/v.aspx/CCPGCPTeamstoCRMServiceWorkQueueCrosswalk?chronicleID=09000929819617ed&dl=0&searchID=VI-8d55d97a802d5dc&row=0');
            insert RefDoc;
            
            CustomLink__c RefDocPro = new CustomLink__c(Name='PCC Hot Topics',RecordTypeId = MentorLinkRecordTypeId,Download_MentorLink__c=True,Quick_Start_Pretext__c = objQSPProviderRef.Id,URL__c='http://dctm.humana.com/Mentor/Web/v.aspx/CCPGCPTeamstoCRMServiceWorkQueueCrosswalk?chronicleID=09000929819617ed&dl=0&searchID=VI-8d55d97a802d5dc&row=0');
            insert RefDocPro;
            
        }
        
        Work_Queue_Setup__c oInsertedQueue = [SELECT Id, Name, OwnerId
                                              FROM Work_Queue_Setup__c
                                              WHERE Name = 'Test Business Process and Testing'];
        oCCSUser.Current_Queue__c = oInsertedQueue.Name;
        update oCCSUser;
    }

    @isTest
    static void testClassificationIntent()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);

        system.runAs(oCCSUser)
        {   
            Test.startTest();
             
             List<CTCI_Junction__c> list_ctci = [select Classification__c, Intent__c, Classification_Type__c from CTCI_Junction__c 
                                                where Classification_type__r.Name='Calls' and IsActive__c = TRUE ];
             
             QuickStart_LC_HUM.getClassificationIntent();  
            Test.stopTest();
        }
    }

    @isTest
    static void testPretextData()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);

        system.runAs(oCCSUser)
        {   
            Test.startTest();
             List<CTCI_Junction__c> list_ctci = [select Classification__c, Intent__c, Classification_Type__c, classification__r.name, intent__r.name from CTCI_Junction__c l
                                                where Classification__r.Name='Benefits' and Intent__r.Name='Rewards' and IsActive__c = TRUE limit 1];
             
             QuickStart_LC_HUM.getQSPAdditionalInfo(list_ctci.get(0).classification__c, list_ctci.get(0).intent__c,list_ctci.get(0).Classification_Type__c,list_ctci.get(0).classification__r.name,list_ctci.get(0).intent__r.name);
             Test.stopTest();

        }
    }
    @isTest
    static void testfetchPretextrecord()
    {
        
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);

        system.runAs(oCCSUser)
        {   
            Test.startTest();
             Quick_Start_Pretext__c objQSP = [select Id from Quick_Start_Pretext__c 
                                                where Is_Active__c=TRUE AND Business_Groups__c = 'RSO' limit 1];
             
             QuickStart_LC_HUM.fetchQSPretextData(objQSP.Id);
             QuickStart_LC_HUM.fetchQsPretextGnAData('Complaint','test');
             Test.stopTest();

        }
    }    
    @isTest
    static void testgetQSDuplicateGrievance()
    {
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);

        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Account acc = [select id,name,Enterprise_Id__c from Account where Enterprise_Id__c='123456' limit 1];
            Classification__c classificationid = [select id,Name from Classification__c where Name = 'Claims' limit 1];
            Intent__c Intentid = [select id, Name from Intent__c where Name = 'Denied' limit 1];
            QuickStart_LC_HUM.getQSDuplicateGrievance(acc.id,classificationid.id,Intentid.id,'Yes - Medicare Part C');
            QuickStart_LC_HUM.checkCasecommentFieldPermission('parentid');
            QuickStart_LC_HUM.FixNewLine('testing case comment \n');
            QuickStart_LC_HUM.FixNewLine('testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case comment testing case commenttesting case comment testing case comment testing case comment testing case comment testing case comment testing case comment');
            
            Test.stopTest();
        }
    }
    
     @isTest
    static void testgetcallbackNumber()
    {
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);

        system.runAs(oCCSUser)
        {   
            Test.startTest();
            Interaction__c inter = [select id, caller_Name__c from interaction__c limit 1];
            QuickStart_LC_HUM.getCallbackNumber(inter.Id);
            Interaction__c inter1 = [select id, caller_Name__c from interaction__c where  interacting_with_type__c ='Provider' limit 1];
            QuickStart_LC_HUM.getCallbackNumber(inter1.Id);
            Test.stopTest();
        }
    }
    
    @isTest
    static void testMemtorlink()
    {
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);

        system.runAs(oCCSUser)
        {   
            Test.startTest();
           	List<CTCI_Junction__c> list_ctci = [select Classification__c, Intent__c, Classification_Type__c, classification__r.name, intent__r.name from CTCI_Junction__c l
                                                where Classification__r.Name='Benefits' and Intent__r.Name='Rewards' and IsActive__c = TRUE limit 1];
            string ctciId = list_ctci.get(0).Classification_Type__c; 
            QuickStart_LC_HUM.getMentorLinks('Benefits', 'Rewards', ctciId, 'RSO');
            Test.stopTest();
        }
    }   
    
     @isTest
    static void testSaveTask()
    {
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);
		Case ocase = [select id, caseNumber from Case limit 1];
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            
            string sdate = DateTime.newInstance(
       						 Date.today().year(), Date.today().month(), Date.today().day()).format('yyyy-MM-dd');
           	QuickStart_LC_HUM.saveTask('Notification Task', sdate, 'testing', 'Business', 'Process and Testing', oCCSUser.Current_Queue__c, oCCSUser.Name,oCCSUser.Id,ocase.id);
            Test.stopTest();
        }
    }
    
    @isTest
    static void testgetBusinessGroupName()
    {
        User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assertEquals('Test Business Process and Testing',oCCSUser.Current_Queue__c);
		Case ocase = [select id, caseNumber from Case limit 1];
        system.runAs(oCCSUser)
        {   
            Test.startTest();
            QuickStart_LC_HUM.getQSAccess();
            QuickStart_LC_HUM.getBusinessGroupName(ocase.id,'Benefits', 'Verification');
            Test.stopTest();
        }
    }
}