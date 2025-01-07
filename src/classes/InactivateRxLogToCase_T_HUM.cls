/****************************************************************************
Apex Class Name  : InactivateRxLogToCase_T_HUM
Version          : 1.0 
Created On       : 01/10/2022 
Function         : Test class to InactivateRxLogToCase_C_HUM Apex class
Modification Log :
*   Developer                   Date                 Description
------------------------------------------------------------------------------------------------------------------------------                
*   Ashok Kumar Nutalapati		01/10/2022           REQ-2843422 Original  Version.
*   Ashok Kumar Nutalapati		05/09/2022           User Story 2934951: T1PRJ0200895 - MF 1 - Inactivate Rx: Create New Case (CRMS) (ID# 163)
*********************************************************************************************************************************/

@isTest
Private Class InactivateRxLogToCase_T_HUM 
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data For test Class
    * Return type : None
    * Parameter   : None
    */                                                                                                          
    @testSetup static void setUpTestData()
     {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
        insert objUser;
        
      system.runAs(objUser)
        {
            Pharmacy_Supervisor_HUM__c supervisorCS = New Pharmacy_Supervisor_HUM__c(Name = 'CRMS_300_Humana_Pharmacy_Supervisor');
            insert supervisorCS;
            
            system.assertEquals(objUser.lastname == 'test User1',true);
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.PersonMailingCountry ='United States';
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);
            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';          
            insert oMedicalPolicy;
            system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
            // Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            system.assertEquals(objMedPolMem.Relationship__c == 'self',true);
            Interaction__c objInteraction= HUMTestDataHelperTest.getInteraction();
            objInteraction.Interacting_With__c = objMemAccount.id;
            objInteraction.Interacting_With_type__c = 'Member';
            objInteraction.Interaction_External_ID__c = '1234567895';
            insert objInteraction;
            
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
            classificationRec.Name = 'Clinical Services Outreach/Transfer (CT & TM)';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);    
            system.assertEquals(lstClassification[0].Name=='Clinical Services Outreach/Transfer (CT & TM)',true);
            insert lstClassification;
                
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Inactivate Rx';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);                
            insert lstIntent;
            system.assertEquals(lstIntent[0].Name=='Inactivate Rx',true);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctList[0].id;
            ctciRec.Classification__c = lstClassification[0].Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);

            insert ctciList;
            system.assertEquals(ctciList.size(),1);

            List<Case> lstCases = new List<Case>();

            Case oCase1 = new Case(  AccountId=objMemAccount.Id,
                        CTCI_List__c = ctciList[0].Id,
                        General_Case__c = true,
                        Interacting_With_Type__c = 'Member',
                        Interacting_With__c= objMemAccount.Id, 
                        Due_Date__c=Date.Today(), 
                        Case_Owner__c=UserInfo.getUserName(), 
                        Owner_Queue__c='Humana Pharmacy Calls', 
                        Work_Queue_View_Name__c='Humana Pharmacy Calls',
                        Service_Center__c ='Humana Pharmacy',
                        Department__c='Calls',
                        Classification_Type__c = 'HP Clinical Services', 
                        Status='In Progress');
            lstCases.add(oCase1);
            insert lstCases;
            
            Template__c oTempate = new Template__c(Description__c ='Inactivate Rx');
            insert oTempate;
            system.assertNotEquals(oTempate,null,'Value not matched');
            system.assertEquals(oTempate.Description__c ,'Inactivate Rx');   
            
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
        }
    }
  
    /*
    * Method Name : testcreatePharmacyCaseAndRedirect
    * Description : This method Is used To test createPharmacy Case Redirect.
    * Return type : void
    * Parameter   : NA
    */
    Static testMethod void testcreatePharmacyCaseAndRedirect() 
    {    
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        account accobj = [select id from account where FirstName = 'Test FName'Limit 1];
        List<case> caseRec =[SELECT Id,CaseNumber FROM case limit 1];
        String caseNumber = caseRec[0].CaseNumber;
        String caseId= caseRec[0].Id;
        List<Interaction__c> intId =[SELECT Id, Name FROM Interaction__c limit 1];
        String intractId = intID[0].id;
        
        System.runAs(testUser)
        {
            test.startTest();
            InactivateRxLogToCase_C_HUM inactivateRx = new InactivateRxLogToCase_C_HUM();  
            inactivateRx.sPolicyId = policyObj.Id; 
            inactivateRx.ClinicalTransfer = 'false';
            inactivateRx.sInteractionId = intractId;  
            inactivateRx.sAccountId = accobj.Id;
            inactivateRx.sCaseId = caseId;
            inactivateRx.sCaseNumber = caseNumber;
            List<Template_submission__c> lstTempalteSubmsision = [select id from Template_submission__c where Version__c = 1.00 LIMIT 1];
            inactivateRx.SubmissionID = lstTempalteSubmsision[0].id;
            inactivateRx.createPharmacyCaseAndRedirect();
            
            test.stopTest();
        }
    }
}