/****************************************************************************
Apex Class Name  : PharmacyInvoiceRequest_LT_HUM
Version          : 1.0 
Created On       : 3/04/2022
Function         : Test class to PharmacyInvoiceRequest_LC_HUM Apex class

Modification Log :
* Developer Name                       Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane            			03/04/2022                 Original Version       
************************************************************************************************************************/
@isTest
public  class PharmacyInvoiceRequest_LT_HUM {
    @testSetup static void testData(){
        
        Template__c oTempate = new Template__c(Description__c ='Invoice Request');
        insert oTempate;
        system.assertNotEquals(oTempate,null,'Value not matched');
        system.assertEquals(oTempate.Description__c ,'Invoice Request');   
        
    }
    
    @IsTest
    static void testinit(){
        
    User oUser = HUMTestDataHelperTest.createTestUser('User1','Humana Pharmacy Specialist');
    oUser.Current_Queue__c = 'Humana Pharmacy Calls';
    insert oUser;
    system.assertNotEquals(oUser,null,'Value not matched');
    
     system.assert(oUser.Id!= null);
        System.runAs(oUser){
         Test.startTest();
         string caseId = '';
         
         string invoiceData = PharmacyInvoiceRequest_LC_HUM.init(caseId);
         System.assertNotEquals(null, invoiceData);
         System.assertEquals(oUser.Current_Queue__c,'Humana Pharmacy Calls'); 
         Test.stopTest();
        }
        
        
    }

    /*
     * Method Name : testInvoiceRequestCaseDetail
     * Description : This is the test method which is used to test the InvoiceRequest_C_HUM. 
     * Return type : Void
     * Paramater   : NA
     */     
    private static testMethod void testInvoiceRequestCaseDetail(){
         
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Humana Pharmacy Specialist');
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
            
            Case oCase = new Case(	AccountId=acc1.Id,
            						CTCI_List__c = ctciRec.Id,
            						General_Case__c = true,
            						Interacting_With_Type__c = 'Member',
            						Interacting_With__c=acc1.Id, 
            						Due_Date__c=Date.Today(), 
            						Case_Owner__c=UserInfo.getUserName(), 
            						Owner_Queue__c='Humana Pharmacy Calls', 
            						Work_Queue_View_Name__c='Humana Pharmacy Calls',
            						Service_Center__c ='Humana Pharmacy',
            						Department__c='Calls', 
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
       
            
            Test.startTest();	
            System.assert(oCase.Status == 'In Progress');
            string invoiceData = PharmacyInvoiceRequest_LC_HUM.init(oCase.AccountId);
            System.assertNotEquals(null, invoiceData);
            System.assertEquals(oUser.Current_Queue__c,'Humana Pharmacy Calls'); 
            Test.stopTest();
        }
    }
}