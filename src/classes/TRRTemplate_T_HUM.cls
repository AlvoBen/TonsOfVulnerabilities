/***************************************************************************************************************************************************************************
Apex Class Name  : TRRTemplate_T_HUM
Version          : 1.0
Created Date     : March 25 2019
Function         : Test Class for TRRTemplate_C_HUM
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name        Code Review                Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Moshitha Gunasekaran  348700                     03/25/2019                  Original Version
****************************************************************************************************************************************************************************/
@isTest
private with sharing class TRRTemplate_T_HUM {

	/*
    * Method Name   :    overallTestMethod
    * Description   :    This method is used for overall testing for TRRTemplate_T_HUM
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testSetup static void setup()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');      
        System.runAs(oCCSUser)
        {
        	
            Template__c objTemplate = new Template__c(Description__c='New Test TRR template', Unique_Template_Name__c='TRR_Template');
            insert objTemplate;
            System.assertNotEquals(objTemplate.Id, NULL, 'Value not matched');         
            Account oAccount = new Account(Name='TestAccount12');
            insert oAccount;
            system.assertNotEquals(oAccount.Id, NULL,'Value not matched');          
            Case oCase1 = new Case(AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c=UserInfo.getUserName(), Work_Queue_View_Name__c='Cincinati CRU', status='In Progress', Origin='CRMTRR');
            insert oCase1;
            System.assertNotEquals(oCase1.Id, NULL, 'Value not matched');
            Template_Submission_Owner__c oTSO1 = new Template_Submission_Owner__c(Object_Owner_ID__c=oCase1.Id, Object_Owner_Type__c='Case');
            insert oTSO1;
            System.assertNotEquals(oTSO1.Id, NULL, 'Value not matched');            
            Template_Submission__c objTS1 = new Template_Submission__c(Template__c=objTemplate.Id, Submission_Owner__c=oTSO1.Id, Version__c=123);
            insert objTS1;
            System.assertNotEquals(objTS1.Id, NULL, 'Value not matched');
            List<Template_Field__c> tmpFields = new List<Template_Field__c>();
            Template_Field__c tempField = new Template_Field__c(Name='Processing Date Stamp1', Template__c=objTemplate.Id, Data_Type__c = 'Date');
            tmpFields.add(tempField);
            Template_Field__c tempField1 = new Template_Field__c(Name='Processing Time Stamp1', Template__c=objTemplate.Id, Data_Type__c = 'DateTime');
            tmpFields.add(tempField1);
            Template_Field__c tempField2 = new Template_Field__c(Name='Reply Code1', Template__c=objTemplate.Id, Data_Type__c = 'String');
            tmpFields.add(tempField2);
            Template_Field__c tempField3 = new Template_Field__c(Name='Effective Date1', Template__c=objTemplate.Id, Data_Type__c = 'Date');
            tmpFields.add(tempField3);
            Template_Field__c tempField4 = new Template_Field__c(Name='End Date1', Template__c=objTemplate.Id, Data_Type__c = 'Date');
            tmpFields.add(tempField4);
            insert tmpFields;
            System.assertEquals(tmpFields.size(), 5, 'Value not matched');
            List<Template_Submission_Data__c> lstTemplSubmiss = new List<Template_Submission_Data__c>();
            Template_Submission_Data__c tempData = new Template_Submission_Data__c(Template_Field__c=tmpFields[0].Id, Template_Submission__c = objTS1.Id, Value__c = '2019-12-03');
            lstTemplSubmiss.add(tempData);
            Template_Submission_Data__c tempData1 = new Template_Submission_Data__c(Template_Field__c=tmpFields[1].Id, Template_Submission__c = objTS1.Id, Value__c = '02:44:00.546789');          
            lstTemplSubmiss.add(tempData1);
            Template_Submission_Data__c tempData2 = new Template_Submission_Data__c(Template_Field__c=tmpFields[2].Id, Template_Submission__c = objTS1.Id, Value__c = '375');          
            lstTemplSubmiss.add(tempData2);
            Template_Submission_Data__c tempData3 = new Template_Submission_Data__c(Template_Field__c=tmpFields[3].Id, Template_Submission__c = objTS1.Id, Value__c = '2019-01-01');                    
            lstTemplSubmiss.add(tempData3);
            insert lstTemplSubmiss;
            System.assertEquals(lstTemplSubmiss.size(), 4, 'Value not matched');
        }
    } 
    private static testMethod void LoadTRRTemplate() {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test User1',true);
         
        System.runAs (oUser) 
        {
            Test.StartTest();
             Case oCase = [select ID, Status, Origin from Case where Origin = 'CRMTRR' limit 1];
            System.assert(oCase.Origin == 'CRMTRR' ); 
            Template_Submission__c oTempSub = [Select Id,Template__c, Name, Version__c from Template_Submission__c where Version__c = 123 limit 1 ];
            System.assert(oTempSub.Version__c == 123 ); 
            ApexPages.currentPage().getParameters().put('caseId',oCase.Id);
            ApexPages.currentPage().getParameters().put('ProcessNumber', oTempSub.Name);
            TRRTemplate_C_HUM oclsObject = new TRRTemplate_C_HUM();
            
            Test.stopTest(); 
        }
        
    }
}