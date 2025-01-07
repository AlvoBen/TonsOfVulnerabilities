/*********************************************************************************************************
Apex Class Name : PharmacyLoggingComponent_T_HUM
Version: 1.0
Created On      : 11/03/2020
Function        : Test class for PharmacyLoggingComponent_C_HUM

* Modification Log
* Developer Name            Code Review                 Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Kiran Bhuvanagiri                                 11/03/2020                  User Story - 815259: Original Version
* Aaron Speakman                                    05/11/2020                  Code Coverage Improvements
************************************************************************************************************/

@isTest
Private Class PharmacyLoggingComponent_T_HUM
{
    /*
     * Method Name : setup
     * Description : This is the test method which is used to test the PharmacyLoggingComponent_C_HUM. 
     * Return type : Void
     * Paramater   : NA
     */     
    @testSetup static void setup() {
         
        User oUser = HUMTestDataHelperTest.createTestUser('User2','Humana Pharmacy Specialist');
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
            insert ctList;
            system.assertEquals(ctList.size(),1);
            
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
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Billing Issue (CT & TM)',true);
                
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Invoice Requests';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);                
            insert lstIntent;
            system.assertEquals(lstIntent[0].Name=='Invoice Requests',true);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            insert ctciList;
            system.assertEquals(ctciList.size(),1);
            
            Case oCase = new Case(  AccountId=acc1.Id,
                                    CTCI_List__c = ctciRec.Id,
                                    General_Case__c = true,
                                    Interacting_With_Type__c = 'Member',
                                    Interacting_With__c= acc1.Id, 
                                    Due_Date__c=Date.Today(), 
                                    Case_Owner__c=UserInfo.getUserName(), 
                                    Owner_Queue__c='Humana Pharmacy Calls', 
                                    Work_Queue_View_Name__c='Humana Pharmacy Calls',
                                    Service_Center__c ='Humana Pharmacy',
                                    Department__c='Calls',
                                    //Classification_Type__c = 'Humana Pharmacy Calls',
                                    Classification_Type__c = 'HP Clinical Services', 
                                    Status='In Progress');
            insert oCase;
            system.assertNotEquals(oCase,null,'Value not matched');
            
            Template__c oTempate = new Template__c(Description__c ='Invoice Request');
            insert oTempate;
            system.assertNotEquals(oTempate,null,'Value not matched');
            system.assertEquals(oTempate.Description__c ,'Invoice Request');   
            
            Template_Field__c oTemplateField = new Template_Field__c(Name= 'SubmissionData', Data_Type__c= 'String',Template__c = oTempate.Id );
            insert oTemplateField ;
            system.assertNotEquals(oTemplateField ,null,'Value not matched'); 
            system.assertEquals(oTemplateField.Name,'SubmissionData');   
            
            Template_Submission_Owner__c  oTemplateSubmsissionOwner = new Template_Submission_Owner__c(Object_Owner_ID__c = oCase.Id,  Object_Owner_Type__c = 'Case')  ;
            insert oTemplateSubmsissionOwner;
            system.assertNotEquals(oTemplateSubmsissionOwner,null,'Value not matched'); 
            system.assertEquals(oTemplateSubmsissionOwner.Object_Owner_Type__c  ,'Case'); 
            
            Template_Submission__c oTempalteSubmsision = new Template_Submission__c(Template__c =oTempate.Id, Submission_Owner__c = oTemplateSubmsissionOwner.Id, Version__c = 1.00 );
            insert oTempalteSubmsision;
            system.assertNotEquals(oTempalteSubmsision,null,'Value not matched');  
            
            Template_Submission_Data__c oTemplateSubmissionData = new Template_Submission_Data__c(Template_Field__c = oTemplateField.Id , Template_Submission__c = oTempalteSubmsision.Id ,value__c ='TestValue' );
            insert oTemplateSubmissionData;
            system.assertNotEquals(oTemplateSubmissionData ,null,'Value not matched'); 
       
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
            
            PharmacyLoggingComponent_C_HUM objClass = New PharmacyLoggingComponent_C_HUM();
            
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
            
            objClass.sInteractionId = '1234567';
            objClass.sSelectedCaseId = '1234567';
            objClass.associateInteractiontToCase();
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
            
            PharmacyLoggingComponent_C_HUM objClass = New PharmacyLoggingComponent_C_HUM();
            
            ApexPages.currentPage().getParameters().put('searchStartDate', '2012-10-12');
            ApexPages.currentPage().getParameters().put('searchEndDate', '2016-10-12');
            ApexPages.currentPage().getParameters().put('searchCaseNumber', '');
            ApexPages.currentPage().getParameters().put('searchCaseNumber', '1236');
            
            objClass.returnListOfInvoiceCase();
            System.assertEquals(objClass.lstFinalCases.size(),0);
            String jsonData = objClass.sCaseDetailsJSON;
            System.assertEquals(jsonData,'{"data":[]}');

            PharmacyLoggingComponent_C_HUM.getInteractionDetails();

            objClass.sourcePage = 'Test';
            PharmacyLoggingComponent_C_HUM.sIntercatingwithType = 'Test';
            objClass.bSucessSave = false;
            objClass.bGuidedProcesscheck = false;
            
            test.stopTest();
        }
    }
}