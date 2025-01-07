/***********************************************************************************************************
Apex Class Name  : HUMValidSubmissionHelperTest 
Version          : 1.0
Created Date     :June 20 015
Function         : Test class for HUMValidSubmissionHelper 
**********************************************************************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Asha Deepika Koyyana                               06/20/2015                 Original Version 
* Tanushree Hazari                                   08/12/2015                 Removed the usage of Account.Group_Number__c field from test class
* Avinash Choubey              	33332                07/20/2016                 Chnages made as part of REQ- 270351
* Pradeepkumar Dani				99570			     06/08/2017					Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Mohammed Noor                      	 		     02/01/2021			   	    US1921246-PR94254-MF4-AEP Performance - SF Limits - Sync CPU Time Limit Optimization
********************************************************************************************************************************************************************/
@isTest
private class HUMValidSubmissionHelperTest 
{
    /**
    * Returns void
    * <p>
    * Method to setup Testdata 
    *
    * @return  Void
    * @see
    */
    @testSetup static void dataSetup()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
    	insert oCCSUser;
        system.assertEquals(oCCSUser.lastname =='test User1',true);    	
        System.runAs(oCCSUser)
        {
            Account oAccount = new Account();
            oAccount.Name = 'New Case Account';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assertEquals(oAccount.Name == 'New Case Account', true);
            
            Classification__c classificationRec = new Classification__c();
	        classificationRec.Name = 'Claims';
	        classificationRec.Is_Active__c = true;
	        insert classificationRec;
	        System.assertEquals(classificationRec.Name == 'Claims', true);
	        
	        Intent__c intentRec = new Intent__c();
	        intentRec.Name = 'Filling';
	        intentRec.Is_Active__c = true;
	        insert intentRec;
	        System.assertEquals(intentRec.Name == 'Filling', true);
            
            Case oCase = new Case(AccountId=oAccount.Id, Intent_Id__c = intentRec.Id, General_Case__c = true, Classification_Id__c =classificationRec.Id, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress');
            insert oCase;
            System.assertEquals(oCase.Interacting_With_Type__c == 'Group', true);
                 
            Template__c oTempate = new Template__c(Description__c =' Address Verification Flow');
            insert oTempate;
            System.assertEquals(oTempate.Description__c ==' Address Verification Flow', true);  
            
            Template_Field__c oTemplateField = new Template_Field__c(Name= 'MemberId', Data_Type__c= 'String',Template__c = oTempate.Id );
            insert oTemplateField ;
            System.assertEquals(oTemplateField.Name == 'MemberId', true); 
            
            Template_Submission_Owner__c  oTemplateSubmsissionOwner = new Template_Submission_Owner__c(Object_Owner_ID__c = oCase.Id,  Object_Owner_Type__c = 'Case')  ;
            insert oTemplateSubmsissionOwner;
            System.assertEquals(oTemplateSubmsissionOwner.Object_Owner_Type__c == 'Case', true);  
        
            Template_Submission__c oTempalteSubmsision = new Template_Submission__c(Template__c =oTempate.Id, Submission_Owner__c = oTemplateSubmsissionOwner.Id, Version__c = 1.00 );
            insert oTempalteSubmsision;
            System.assertEquals(oTempalteSubmsision.Template__c == oTempate.Id, true);  
            
            Template_Submission_Data__c oTemplateSubmissionData = new Template_Submission_Data__c(Template_Field__c = oTemplateField.Id , Template_Submission__c = oTempalteSubmsision.Id ,value__c ='' );
            insert oTemplateSubmissionData ;
            System.assertEquals(oTemplateSubmissionData.Template_Field__c == oTemplateField.Id, true); 
             
            HUMConstants__c oHUMConstant = new HUMConstants__c(Name= 'HUMCase', StringValue__c ='Case'); 
            insert oHUMConstant;
            System.assertEquals(oHUMConstant.Name == 'HUMCase', true);
        }  
    }
    
    /**
    * Returns void
    * <p>
    * Used to all scenerios of test data 
    *
    * @return  Void
    * @see
    */ 
    static testMethod void testOverAll()
    {
        User oCCSUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oCCSUser.lastname =='test User1',true);
        System.runAs(oCCSUser)
        {
            Test.StartTest();
            Case oCase = [Select Classification_Id__r.name, Intent_Id__r.name,status from Case where status='In Progress' LIMIT 1];
            System.assert(oCase.status == 'In Progress');
            HUMValidSubmissionHelper.deleteInValidSubmission(oCase.Id);
            Test.stopTest();
        }
    }
    
    /**
    * Returns void
    * <p>
    * Used to all aysnch scenerios of test data 
    *
    * @return  Void
    * @see
    */ 
    static testMethod void testAsynchMethods()
    {
        User oCCSUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oCCSUser.lastname =='test User1',true);
        Case oCase = [Select Classification_Id__r.name, Intent_Id__r.name,status from Case where status='In Progress' LIMIT 1];
        
        Template__c oTempate = new Template__c(Description__c =' Address Verification Flow');
        insert oTempate;
        System.assertEquals(oTempate.Description__c ==' Address Verification Flow', true);  
        
        Template_Field__c oTemplateField = new Template_Field__c(Name= 'MemberId', Data_Type__c= 'String',Template__c = oTempate.Id );
        insert oTemplateField ;
        System.assertEquals(oTemplateField.Name == 'MemberId', true); 
        
        Template_Submission_Owner__c  oTemplateSubmsissionOwner = new Template_Submission_Owner__c(Object_Owner_ID__c = oCase.Id,  Object_Owner_Type__c = 'Case')  ;
        insert oTemplateSubmsissionOwner;
        System.assertEquals(oTemplateSubmsissionOwner.Object_Owner_Type__c == 'Case', true);  
    
        Template_Submission__c oTempalteSubmsision = new Template_Submission__c(Template__c =oTempate.Id, Submission_Owner__c = oTemplateSubmsissionOwner.Id, Version__c = 1.00 );
        insert oTempalteSubmsision;
        System.assertEquals(oTempalteSubmsision.Template__c == oTempate.Id, true);  
        
        Template_Submission_Data__c oTemplateSubmissionData = new Template_Submission_Data__c(Template_Field__c = oTemplateField.Id , Template_Submission__c = oTempalteSubmsision.Id ,value__c ='' );
        insert oTemplateSubmissionData ;
        System.assertEquals(oTemplateSubmissionData.Template_Field__c == oTemplateField.Id, true);
            
        System.runAs(oCCSUser)
        {
            Test.StartTest();            
            System.assert(oCase.status == 'In Progress');
            Boolean bResult = HUMValidSubmissionHelper.deleteInValidSubmissionAsynch(oCase.Id);
            System.assertEquals(bResult,true);
            Test.stopTest();
        }
    }
    
    /**
    * Returns void
    * <p>
    * Used to test the scenerio of NonEmpty Templates 
    *
    * @return  Void
    * @see
    */ 
    static testMethod void testNonEmptyTemplates()
    {
        User oCCSUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oCCSUser.lastname =='test User1',true);
        Case oCase = [Select Classification_Id__r.name, Intent_Id__r.name,status from Case where status='In Progress' LIMIT 1];
        
        Template__c oTempate = new Template__c(Description__c =' Address Verification Flow');
        insert oTempate;
        System.assertEquals(oTempate.Description__c ==' Address Verification Flow', true);  
        
        Template_Field__c oTemplateField = new Template_Field__c(Name= 'MemberId', Data_Type__c= 'String',Template__c = oTempate.Id );
        insert oTemplateField ;
        System.assertEquals(oTemplateField.Name == 'MemberId', true); 
        
        Template_Submission_Owner__c  oTemplateSubmsissionOwner = new Template_Submission_Owner__c(Object_Owner_ID__c = oCase.Id,  Object_Owner_Type__c = 'Case')  ;
        insert oTemplateSubmsissionOwner;
        System.assertEquals(oTemplateSubmsissionOwner.Object_Owner_Type__c == 'Case', true);  
    
        Template_Submission__c oTempalteSubmsision = new Template_Submission__c(Template__c =oTempate.Id, Submission_Owner__c = oTemplateSubmsissionOwner.Id, Version__c = 1.00 );
        insert oTempalteSubmsision;
        System.assertEquals(oTempalteSubmsision.Template__c == oTempate.Id, true);  
        
        Template_Submission_Data__c oTemplateSubmissionData = new Template_Submission_Data__c(Template_Field__c = oTemplateField.Id , Template_Submission__c = oTempalteSubmsision.Id ,value__c ='Test' );
        insert oTemplateSubmissionData ;
        System.assertEquals(oTemplateSubmissionData.Template_Field__c == oTemplateField.Id, true);
            
        System.runAs(oCCSUser)
        {
            Test.StartTest();            
            System.assert(oCase.status == 'In Progress');
            HUMValidSubmissionHelper.deleteInValidSubmission(oCase.Id);
            Boolean bResult = HUMValidSubmissionHelper.deleteInValidSubmissionAsynch(oCase.Id);
            System.assertEquals(bResult,false);
            Test.stopTest();
        }
    }
    
    
    /**
    * Returns void
    * <p>
    * Used to test the exception scenerio 
    *
    * @return  Void
    * @see
    */ 
    static testMethod void testException()
    {
        User oCCSUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oCCSUser.lastname =='test User1',true);
        Case oCase = [Select Classification_Id__r.name, Intent_Id__r.name,status from Case where status='In Progress' LIMIT 1];
                        
        System.runAs(oCCSUser)
        {
            Test.StartTest();            
            System.assert(oCase.status == 'In Progress');            
            HUMValidSubmissionHelper.deleteSubmissionRecords(oCase.Id,oCase.Id);
            Test.stopTest();
        }
    }
}