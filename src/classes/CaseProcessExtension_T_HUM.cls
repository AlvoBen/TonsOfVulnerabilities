/******************************************************************************************************************************
Apex Class Name  : CaseProcessExtension_T_HUM
Version          : 1.0 
Created Date     : NOV 17 2017
Function         : Test class for class CaseProcessExtension_C_HUM

Modification Log :
 *    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *    Rakesh Patni       149201              11/17/2017           Original version.
 * Moshitha Gunasekaran  348700              03/25/2019                   REQ - 380742 - Ability to view Transaction Reply Report (TRR) Process data for a specific member on a Medicare case
 * Moshitha Gunasekaran                      02/07/2020                   US752867 - New TRR Templates Process Section
 *********************************************************************************************************************************/
@isTest
private with sharing class CaseProcessExtension_T_HUM
{
   /*
    * Method Name   :    overallTestMethod
    * Description   :    This method is used for overall testing for CaseProcessExtension_T_HUM
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testSetup static void setup()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('CaseProcessUser1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');      
        System.runAs(oCCSUser)
        {
            Template__c objTemplate = new Template__c(Description__c='New Test template', Unique_Template_Name__c='TRR_Template');
            insert objTemplate;
            System.assertNotEquals(objTemplate.Id, NULL, 'Value not matched');         
            Account oAccount = new Account(Name='TestAccount1');
            insert oAccount;
            system.assertNotEquals(oAccount.Id, NULL,'Value not matched');          
            Case oCase = new Case(AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c=UserInfo.getUserName(), Work_Queue_View_Name__c='Cincinati CRU', status='In Progress');
            insert oCase;
            System.assertNotEquals(oCase.Id, NULL, 'Value not matched');
            Case oCase1 = new Case(AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c=UserInfo.getUserName(), Work_Queue_View_Name__c='Cincinati CRU', status='In Progress', Origin='CRMTRR');
            insert oCase1;
            System.assertNotEquals(oCase1.Id, NULL, 'Value not matched');
            Template_Submission_Owner__c oTSO = new Template_Submission_Owner__c(Object_Owner_ID__c=oCase.Id, Object_Owner_Type__c='Case');
            insert oTSO;
            System.assertNotEquals(oTSO.Id, NULL, 'Value not matched'); 
            Template_Submission_Owner__c oTSO1 = new Template_Submission_Owner__c(Object_Owner_ID__c=oCase1.Id, Object_Owner_Type__c='Case');
            insert oTSO1;
            System.assertNotEquals(oTSO1.Id, NULL, 'Value not matched');            
            Template_Submission__c objTS = new Template_Submission__c(Template__c=objTemplate.Id, Submission_Owner__c=oTSO.Id, Version__c=123);
            insert objTS;
            System.assertNotEquals(objTS.Id, NULL, 'Value not matched'); 
            Template_Submission__c objTS1 = new Template_Submission__c(Template__c=objTemplate.Id, Submission_Owner__c=oTSO1.Id, Version__c=123);
            insert objTS1;
            System.assertNotEquals(objTS1.Id, NULL, 'Value not matched');
            List<Template_Field__c> tmpFields = new List<Template_Field__c>();
            Template_Field__c tempField = new Template_Field__c(Name='Processing Date Stamp', Template__c=objTemplate.Id, Data_Type__c = 'Date');
            tmpFields.add(tempField);
            Template_Field__c tempField1 = new Template_Field__c(Name='Reply Code', Template__c=objTemplate.Id, Data_Type__c = 'String');
            tmpFields.add(tempField1);
            Template_Field__c tempField2 = new Template_Field__c(Name='Effective Date', Template__c=objTemplate.Id, Data_Type__c = 'Date');
            tmpFields.add(tempField2);
            insert tmpFields;
            System.assertEquals(tmpFields.size(), 3, 'Value not matched');
            List<Template_Submission_Data__c> lstTemplSubmiss = new List<Template_Submission_Data__c>();
            Template_Submission_Data__c tempData = new Template_Submission_Data__c(Template_Field__c=tmpFields[0].Id, Template_Submission__c = objTS1.Id, Value__c = '2019-12-03');
            lstTemplSubmiss.add(tempData);
            Template_Submission_Data__c tempData1 = new Template_Submission_Data__c(Template_Field__c=tmpFields[1].Id, Template_Submission__c = objTS1.Id, Value__c = '375');          
            lstTemplSubmiss.add(tempData1);
            Template_Submission_Data__c tempData2 = new Template_Submission_Data__c(Template_Field__c=tmpFields[2].Id, Template_Submission__c = objTS1.Id, Value__c = '2019-01-01');                    
            lstTemplSubmiss.add(tempData2);
            insert lstTemplSubmiss;
            System.assertEquals(lstTemplSubmiss.size(), 3, 'Value not matched');
            
            Template__c objTemplate1 = new Template__c(Description__c='New Test template', Unique_Template_Name__c='MedicareGoodCauseReinstatement');
            insert objTemplate1;
            System.assertNotEquals(objTemplate1.Id, NULL, 'Value not matched'); 
            Template_Submission_Owner__c oTSO2 = new Template_Submission_Owner__c(Object_Owner_ID__c=oCase.Id, Object_Owner_Type__c='Case');
            insert oTSO2;
            System.assertNotEquals(oTSO2.Id, NULL, 'Value not matched'); 
                       
            Template_Submission__c objTS2 = new Template_Submission__c(Template__c=objTemplate1.Id, Submission_Owner__c=oTSO2.Id, Version__c=123);
            insert objTS2;
            System.assertNotEquals(objTS2.Id, NULL, 'Value not matched'); 
            
            List<Template_Field__c> tmpFields1 = new List<Template_Field__c>();
            Template_Field__c tempFieldM = new Template_Field__c(Name='Processing Date Stamp', Template__c=objTemplate1.Id, Data_Type__c = 'Date');
            tmpFields1.add(tempFieldM);
            Template_Field__c tempFieldM1 = new Template_Field__c(Name='Reply Code', Template__c=objTemplate1.Id, Data_Type__c = 'String');
            tmpFields1.add(tempFieldM1);
            Template_Field__c tempFieldM2 = new Template_Field__c(Name='Effective Date', Template__c=objTemplate1.Id, Data_Type__c = 'Date');
            tmpFields1.add(tempFieldM2);
            insert tmpFields1;
            System.assertEquals(tmpFields1.size(), 3, 'Value not matched');
            List<Template_Submission_Data__c> lstTemplSubmiss1 = new List<Template_Submission_Data__c>();
            Template_Submission_Data__c tempDataM = new Template_Submission_Data__c(Template_Field__c=tmpFields1[0].Id, Template_Submission__c = objTS2.Id, Value__c = '2019-12-03');
            lstTemplSubmiss1.add(tempDataM);
            Template_Submission_Data__c tempDataM1 = new Template_Submission_Data__c(Template_Field__c=tmpFields1[1].Id, Template_Submission__c = objTS2.Id, Value__c = '375');          
            lstTemplSubmiss1.add(tempDataM1);
            Template_Submission_Data__c tempDataM2 = new Template_Submission_Data__c(Template_Field__c=tmpFields1[2].Id, Template_Submission__c = objTS2.Id, Value__c = '2019-01-01');                    
            lstTemplSubmiss1.add(tempDataM2);
            insert lstTemplSubmiss1;
            System.assertEquals(lstTemplSubmiss1.size(), 3, 'Value not matched');
            
            List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name='ZERO',IntegerValue__c=0));           
            lstConstants.add(new HUMConstants__c(Name='HUMConstantOne',IntegerValue__c=1));
            insert lstConstants;
            System.assertEquals(lstConstants.size(), 2, 'Value not matched');            
        }
    } 
    /*
    * Method Name : method1
    * Description : This method is used to test main functionality with case action association.
    * Return type : void
    * Parameter   : NA
    */
    static testmethod void method1()
    {
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c  from User where LastName = 'test CaseProcessUser1'];
        List<Case> lCase = [Select id from case limit 1];
        System.assertEquals(lCase.size(),1);
        System.runAs(oUser)
        {
            Test.startTest();
            Case_Action__c cAction = new Case_Action__c(Action_version__c = 'v1',Active__c = true);
            insert cAction;
            Case_Action_Association__c cActionAssociation = new Case_Action_Association__c(Case__c = lCase[0].id,Case_Action__c = cAction.id);
            insert cActionAssociation;
            CaseProcessExtension_C_HUM cont= new CaseProcessExtension_C_HUM();
            CaseProcessExtension_C_HUM.getJSONData(lCase[0].id);                             
            Test.stopTest();
        }       
    }
    /*
    * Method Name : method2
    * Description : This method is used to test functionality without case action association.
    * Return type : void
    * Parameter   : NA
    */ 
    static testmethod void method2()
    {
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c  from User where LastName = 'test CaseProcessUser1'];
        List<Case> lCase = [Select id from case limit 1];
        System.assertEquals(lCase.size(),1);
        System.runAs(oUser)
        {
            Test.startTest();
            CaseProcessExtension_C_HUM cont= new CaseProcessExtension_C_HUM();
            CaseProcessExtension_C_HUM.getJSONData(lCase[0].id);                              
            Test.stopTest();
        }      
    } 

    /*
    * Method Name : method3
    * Description : This method is used to test functionality for TRR Template.
    * Return type : void
    * Parameter   : NA
    */ 
    static testmethod void method3()
    {
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c  from User where LastName = 'test CaseProcessUser1'];
        List<Case> lCase = [Select id from case limit 2];
        System.assertEquals(lCase.size(),2);
        System.runAs(oUser)
        {
            Test.startTest();
            ApexPages.currentPage().getParameters().put('id',lCase[1].id);
            system.assertEquals(ApexPages.currentPage().getParameters().get('id'),lCase[1].id);
            CaseProcessExtension_C_HUM caseProcessController= new CaseProcessExtension_C_HUM();
            CaseProcessExtension_C_HUM.getTRRJSONData(lCase[1].id);                              
            Test.stopTest();
        }      
    }
}