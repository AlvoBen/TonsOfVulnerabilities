/*********************************************************************************************************
Apex Class Name : AttachInvoiceRequestToCase_T_HUM
Version: 1.0
Created On      : 06/22/2020
Function        : Test class for AttachInvoiceRequestToCase_C_HUM

* Modification Log
* Developer Name            Code Review                 Date                       Description
*-----------------------------------------------------------------------------------------------------------
* AshokKumar Nutalapati                                 06/22/2020                 REQ - 1000087 Log 'Invoice Request' Guided Process to Case (RxRF)
* Aaron Speakman                                        05/18/2021                 Code Coverage Improvements
* Kiran Kumar Bhuvanagiri								11/05/2021				   User Story 2649253: Test Class Improvements
************************************************************************************************************/

@isTest
Private Class AttachInvoiceRequestToCase_T_HUM
{
    /*
     * Method Name : setup
     * Description : This is the test method which is used to test the AttachInvoiceRequestToCase_C_HUM. 
     * Return type : Void
     * Paramater   : NA
     */     
    @testSetup static void setup() {
         
        User oUser = HUMTestDataHelperTest.createTestUser('User2','Deployment');
        oUser.Current_Queue__c = 'Humana Pharmacy Calls';
        insert oUser;
        system.assertNotEquals(oUser,null,'Value not matched');
                      
        System.runAs(oUser)
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
            ctRec.name = 'Humana Pharmacy Calls';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);

            ctRec = new Classification_Type__c();
            ctRec.name = 'HP Finance Ops';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);

            insert ctList;
            system.assertEquals(ctList.size(),2);
            
            Work_Queue_Setup__c wqsRecord = new Work_Queue_Setup__c();
            wqsRecord.Name = 'Humana Pharmacy Calls';
            wqsRecord.Classification_Type__c = ctRec.Id;
            insert wqsRecord;
            system.assertEquals(wqsRecord.Name, 'Humana Pharmacy Calls');
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Billing Issue (CT & TM)';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);    
            system.assertEquals(lstClassification[0].Name=='Billing Issue (CT & TM)',true);
            
            classificationRec = new Classification__c();
            classificationRec.Name = 'Billing (Finance Ops)';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);    
            insert lstClassification;
            system.assertEquals(lstClassification[1].Name=='Billing (Finance Ops)',true);
                
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Invoice Requests';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);                
            insert lstIntent;
            system.assertEquals(lstIntent[0].Name=='Invoice Requests',true);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctList[0].id;
            ctciRec.Classification__c = lstClassification[0].Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);

            ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctList[1].id;
            ctciRec.Classification__c = lstClassification[1].Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);

            insert ctciList;
            system.assertEquals(ctciList.size(),2);

            List<Case> lstCases = new List<Case>();

            Case oCase1 = new Case(  AccountId=acc1.Id,
                        CTCI_List__c = ctciList[0].Id,
                        General_Case__c = true,
                        Interacting_With_Type__c = 'Member',
                        Interacting_With__c= acc1.Id, 
                        Due_Date__c=Date.Today(), 
                        Case_Owner__c=UserInfo.getUserName(), 
                        Owner_Queue__c='Humana Pharmacy Calls', 
                        Work_Queue_View_Name__c='Humana Pharmacy Calls',
                        Service_Center__c ='Humana Pharmacy',
                        Department__c='Calls',
                        Classification_Type__c = 'HP Clinical Services', 
                        Status='In Progress');
            lstCases.add(oCase1);

            Case oCase2 = new Case(  AccountId=acc1.Id,
                        CTCI_List__c = ctciList[0].Id,
                        General_Case__c = true,
                        Interacting_With_Type__c = 'Member',
                        Interacting_With__c= acc1.Id, 
                        Due_Date__c=Date.Today(), 
                        Case_Owner__c=UserInfo.getUserName(), 
                        Owner_Queue__c='Humana Pharmacy Calls', 
                        Work_Queue_View_Name__c='Humana Pharmacy Calls',
                        Service_Center__c ='Humana Pharmacy',
                        Department__c='Calls',
                        Classification_Type__c = 'Humana Pharmacy Calls',
                        Status='In Progress');
            lstCases.add(oCase2);

            Case oCase3 = new Case( AccountId=acc1.Id,
                        CTCI_List__c = ctciList[1].Id,
                        General_Case__c = true,
                        Interacting_With_Type__c = 'Member',
                        Interacting_With__c= acc1.Id, 
                        Due_Date__c=Date.Today(), 
                        Case_Owner__c=UserInfo.getUserName(), 
                        Owner_Queue__c='Humana Pharmacy Specialty Calls', 
                        Work_Queue_View_Name__c='Humana Pharmacy Specialty Calls',
                        Service_Center__c ='Humana Pharmacy',
                        Department__c='Specialty Calls',
                        Classification_Type__c = 'HP Finance Ops',
                        Status='In Progress');
            lstCases.add(oCase3);
            insert lstCases;
            
            Template__c oTempate = new Template__c(Description__c ='Invoice Request');
            insert oTempate;
            system.assertNotEquals(oTempate,null,'Value not matched');
            system.assertEquals(oTempate.Description__c ,'Invoice Request');   
            
            Template_Field__c oTemplateField = new Template_Field__c(Name= 'SubmissionData', Data_Type__c= 'String',Template__c = oTempate.Id );
            insert oTemplateField ;
            system.assertNotEquals(oTemplateField ,null,'Value not matched'); 
            system.assertEquals(oTemplateField.Name,'SubmissionData');   
            
            Template_Submission_Owner__c  oTemplateSubmsissionOwner = new Template_Submission_Owner__c(Object_Owner_ID__c = oCase1.Id,  Object_Owner_Type__c = 'Case')  ;
            insert oTemplateSubmsissionOwner;
            system.assertNotEquals(oTemplateSubmsissionOwner,null,'Value not matched'); 
            system.assertEquals(oTemplateSubmsissionOwner.Object_Owner_Type__c  ,'Case'); 
            
            Template_Submission__c oTempalteSubmsision = new Template_Submission__c(Template__c =oTempate.Id, Submission_Owner__c = oTemplateSubmsissionOwner.Id, Version__c = 1.00 );
            insert oTempalteSubmsision;
            system.assertNotEquals(oTempalteSubmsision,null,'Value not matched');  
            
            Template_Submission_Data__c oTemplateSubmissionData = new Template_Submission_Data__c(Template_Field__c = oTemplateField.Id , Template_Submission__c = oTempalteSubmsision.Id ,value__c ='TestValue' );
            insert oTemplateSubmissionData;
            system.assertNotEquals(oTemplateSubmissionData ,null,'Value not matched'); 

            Case_Action__c cAction = new Case_Action__c(Action_version__c = 'v1',Active__c = true);
            insert cAction;
            Case_Action_Association__c cActionAssociation = new Case_Action_Association__c(Case__c = lstCases[1].id,Case_Action__c = cAction.id);
            insert cActionAssociation;
            Attachment_Log__c objLog = new Attachment_Log__c();
            objLog.case__c = lstCases[2].id;
            objLog.Attachment_Key__c =  GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMATTACHMENTLOGFORDAC_HUM;
            insert objLog;
            
            system.assertEquals(oUser.Current_Queue__c,'Humana Pharmacy Calls'); 
           
                      
            LoggingTestDataSetup_T_HUM.createTestData();
            HUMConstants__c constData = New HUMConstants__c(Name ='Size_TwoHundred',IntegerValue__c=200,Stringvalue__c='');
            insert constData;
            System.assertEquals(constData.IntegerValue__c,200); 
        }
    }
    
    /*
    * Method Name : testAttachmentLogFunctionality
    * Description : This method Is used To test Attachment Log functionality.
    * Return type : void
    * Parameter   : NA
    */
    Static testMethod void testAttachmentLogFunctionality() 
    {
        User oUser = [Select Id, Network_User_Id__c from User where LastName = 'test User2'];
        System.runAs(oUser)
        {
            test.startTest();
            Account objAcc = [Select Id, Name from Account limit 1];
            System.assertEquals('Test Account Cases1',objAcc.Name);
            
            AttachInvoiceRequestToCase_C_HUM objClass = New AttachInvoiceRequestToCase_C_HUM();
            
            //Null checks
            objClass.checkGuidedProcessPresent();

            objClass.sObjectId = objAcc.Id;            
            objClass.selectedCaseOption  = 'Existing Case';
            objClass.accId = objAcc.Id;
            objClass.sObjName = ' ';
            objClass.displayInvoiceCaseList();
            objClass.getKeyValue();
            System.assertEquals('1005447107',objClass.enterpriseId );
            
            ApexPages.currentPage().getParameters().put('errorMessage', 'Sample Error');
            ApexPages.currentPage().getParameters().put('dpaascallouttype', 'LoggingSession'); 
            
            objClass.createErrorLog();
            System.assertNotEquals('',objClass.dpaasErrorLogName );
            
            ApexPages.currentPage().getParameters().put('dpaascallouttype', 'LoggingInfo'); 
            objClass.createErrorLog();
            System.assertNotEquals('',objClass.dpaasErrorLogName );
            
            objClass.sCaseNumber = '';
            objClass.sObjName = '';
            objClass.getCaseId();
            
            String screenshotData = 'Sample Screenshot data Sample Screenshot data Sample Screenshot data';
            Blob objblob = Blob.valueOf(screenshotData);
            String paramvalue = EncodingUtil.base64Encode(objblob);
            ApexPages.currentPage().getParameters().put('screenshotData', paramvalue );
            ApexPages.currentPage().getParameters().put('type', 'Auth/Referral Summary'); 
            
            objClass.sObjectId = objAcc.Id;
            objClass.createPharmacyCaseAndRedirect();
            //System.assertNotEquals('',objClass.sSelectedCaseId);
            
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'test account';
            oAcc.Enterprise_ID__c = '1234567895425';
            oAcc.Group_Number__c = '123456';
            oAcc.Source_Platform_Code__c = 'LV';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            
            Interaction__c intRec5 = HUMTestDataHelperTest.getInteraction();
			intRec5.Interacting_With__c = oAcc.Id;
			intRec5.Interacting_With_type__c = 'Group';
			intRec5.Interaction_External_ID__c = '1234567894';
            insert intRec5;
            List<Case> lstCases = [Select Id, Classification_Type__c from Case where Classification_Type__c = 'HP Clinical Services'];
            
            
            objClass.sInteractionId = intRec5.Id;
            objClass.sSelectedCaseId = lstCases[0].Id;
            objClass.associateInteractiontToCase();
            objClass.sSelectedCaseNumber = '1234567';
            objClass.stdsetcon = null; 
            objClass.bSucessSave = True;
            objClass.bGuidedProcesscheck = True;
            objClass.sSubId = null;

            List<Case> lstCases1 = [Select Id, Classification_Type__c from Case where Classification_Type__c = 'HP Clinical Services'];
            ApexPages.currentPage().getParameters().put('caseId', lstCases1[0].Id);
            objClass.checkGuidedProcessPresent();

            List<Template_submission__c> lstTempalteSubmsision = [select id from Template_submission__c where Version__c = 1.00 LIMIT 1];
            objClass.sSubId =lstTempalteSubmsision[0].id;
            objClass.updateWhoID(); 

            lstCases = [Select Id, Classification_Type__c from Case where Classification_Type__c = 'HP Finance Ops'];
            ApexPages.currentPage().getParameters().put('caseId', lstCases[0].Id);
            objClass.checkGuidedProcessPresent();

            lstCases = [Select Id, Classification_Type__c from Case where Classification_Type__c = 'Humana Pharmacy Calls'];
            ApexPages.currentPage().getParameters().put('caseId', lstCases[0].Id);
            objClass.checkGuidedProcessPresent();

            test.stopTest();

        }
    }
    
    /*
    * Method Name : testFetchRecords
    * Description : This method Is used To test Records fetch functionality
    * Return type : void
    * Parameter   : NA
    */
    Static testMethod void testFetchRecords() 
    {
        User oUser = [Select Id, Network_User_Id__c from User where LastName = 'test User2'];
        System.runAs(oUser)
        {
            test.startTest();
            
            AttachInvoiceRequestToCase_C_HUM objClass = New AttachInvoiceRequestToCase_C_HUM();
            
            ApexPages.currentPage().getParameters().put('searchStartDate', '2012-10-12');
            ApexPages.currentPage().getParameters().put('searchEndDate', '2016-10-12');
            ApexPages.currentPage().getParameters().put('searchCaseNumber', '');
            ApexPages.currentPage().getParameters().put('searchCaseNumber', '1236');
            
            objClass.returnListOfInvoiceCase();
            System.assertEquals(objClass.lstFinalCases.size(),0);
            
            String jsonData = objClass.sCaseDetailsJSON;
            System.assertEquals(jsonData,'{"data":[]}');
            test.stopTest();
        }
    }
    
    /*
    * Method Name : testExceptionBlock
    * Description : This method Is cover exception block
    * Return type : void
    * Parameter   : NA
    */
    
    static testMethod void testExceptionBlock() {
        User oUser = [Select Id, Network_User_Id__c from User where LastName = 'test User2'];
		System.runAs(oUser){
        	Test.startTest();
            AttachInvoiceRequestToCase_C_HUM objClass = new AttachInvoiceRequestToCase_C_HUM();
            objClass.sInteractionId = '1254';
            objClass.sSelectedCaseId = '56';
            objClass.associateInteractiontToCase();
            System.assertEquals(objClass.sInteractionId, '1254');
        	Test.stopTest();
        }
    }
}