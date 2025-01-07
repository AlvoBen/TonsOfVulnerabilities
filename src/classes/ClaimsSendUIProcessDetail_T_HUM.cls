/**************************************************************************************************************************************************
Apex class Name    :  ClaimsSendUIProcessDetail_T_HUM
Version            :  1.0
Function           :  This is the test class for ClaimsSendUIProcessDetail_C_HUM controller class

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *  Ranadeer                        					05/14/2021          Original Version    
************************************************************************************************************************************************* */
@isTest
Public  with sharing class ClaimsSendUIProcessDetail_T_HUM
{
   /*
    * Method Name : prepareTestData
    * Description : Used to create wrapper class data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData() 
    {
		User testUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        testUser.UserPermissionsLiveAgentUser = true;
        testUser.Current_Queue__c='Test Business Process and Testing';
        testUser.Mentor_Sub_Function__c = 'Go365';
        insert testUser;
        system.assertEquals(testUser.lastname =='test User1',true);
               
        Group oCompassNewGroup = null;
        oCompassNewGroup = new Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oCompassNewGroup;
        system.assertEquals(oCompassNewGroup.Type,'Queue');

        QueueSObject oCompassNewQueue = new QueueSObject(QueueId = oCompassNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
        insert oCompassNewQueue;
        system.assertEquals(oCompassNewQueue.SObjectType,'Work_Queue_Setup__c');
        
        RecordType caseRecordType = [Select Id from RecordType where Name='Closed Member Case' and sObjectType='Case' limit 1];
        
        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account' limit 1];

        system.runAs(testUser)
        { 
                
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = testUser.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
            Policy__c policyRec = New Policy__c();
            policyRec.Group_Name__c = accountRec.Id;
            policyRec.Product__c = null;
            policyRec.Policy_Group_Number__c='11111';
            policyRec.Major_Lob__c = 'MEF';
            insert policyRec;
            system.assertEquals(policyRec.Group_Name__c==accountRec.Id,true);
            
            Policy_Member__c policyMemRec = New Policy_Member__c();
            policyMemRec.Name = '12345';
            policyMemRec.Policy__c = policyRec.Id;
            policyMemRec.Member__c = accountRec.Id; 
            insert policyMemRec;
            system.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
      
			Case objCase = HUMTestDataHelperTest.getCase();            
            objCase.Guided_Process_Flag__c = True;
            objCase.Origin = GLOBAL_CONSTANT_HUM.TRR_ORIGIN;
            objCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            objCase.Case_Owner__c = 'Louisville RSO Enrollment';
            objCase.status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;
            objCase.ChatKey__c = 'TestKey';
			objCase.Policy_Member__c = policyMemRec.Id;
            objCase.AccountID = accountRec.Id;
            insert objCase;
            system.assertNotEquals(objCase, null, 'Value not matched');
            
            
            //Insert Template data
            Template__c templateObj = New Template__c(Unique_Template_Name__c ='ATVF007',Description__c='Creditable Coverage Attestation');
            Insert templateObj;   
            
            //Insert Template submission owner
            Template_Submission_Owner__c ownerObj = New Template_Submission_Owner__c();
            ownerObj.Object_Owner_ID__c = objCase.Id;
            ownerObj.Object_Owner_Type__c = 'Case';
            insert ownerObj;  
            
            //Insert Template submission
            Template_Submission__c  submissionObj = New Template_Submission__c();
            submissionObj.Submission_Owner__c = ownerObj.Id;
            submissionObj.Template__c = templateObj.id;
            insert submissionObj;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj = New Template_Field__c();
            templateFieldObj.Template__c = templateObj.Id;
            templateFieldObj.Data_Type__c ='String';
            templateFieldObj.Name = 'data';
            insert templateFieldObj;
            
            //Insert template submission data
            Template_Submission_Data__c submissionDataObj = New Template_Submission_Data__c();
            submissionDataObj.Value__c = '12345';
            submissionDataObj.Template_Field__c = templateFieldObj.Id;
            submissionDataObj.Template_Submission__c = submissionObj.Id;
            insert submissionDataObj;
            
			//Launch Compass Functionality
			Classification_Type__c ctRec = New Classification_Type__c();
			ctRec.name = 'Provider Calls (Dental)';
			ctRec.Is_Active__c=true;
			ctRec.Mentor_Function__c ='Contact Center Provider';          
			insert ctRec;
			system.assertEquals(ctRec.Is_Active__c,true);

			Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = 'Test Business Process and Testing', OwnerId = oCompassNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing',Classification_Type__c=ctRec.id);
			insert oWorkQueue;
			system.assertEquals(oWorkQueue.Service_Center__c,'Business');
        }
    }   
    

    /*
     * Method Name : getDetailValueTest
     * Description : Used to run getDetailValue
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void getDetailValueTest()
    {   
		Test.setCurrentPage(Page.ClaimsSendUIProcessDetail_VF_HUM);
		Case c = [SELECT Id FROM Case LIMIT 1];
		Template_Submission__c  t = [SELECT Id FROM Template_Submission__c  LIMIT 1];
		ApexPages.currentPage().getParameters().put('CaseId',c.Id);
		ApexPages.currentPage().getParameters().put('relatedProID',t.Id);
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName,'test User1');                
		
        System.runAs(testUser)
        {
           Test.StartTest();
		   ClaimsSendUIProcessDetail_C_HUM claimSend = new ClaimsSendUIProcessDetail_C_HUM();
		   String retValue = claimSend.getDetailValue();
		   System.assertEquals(retValue, '12345');
           Test.stopTest();  
        }
        
    } 
	
	/*
     * Method Name : getPolicyMemberNameTest
     * Description : Used to run getPolicyMemberName
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void getPolicyMemberNameTest()
    {   
		Test.setCurrentPage(Page.ClaimsSendUIProcessDetail_VF_HUM);
		Case c = [SELECT Id FROM Case LIMIT 1];
		Template_Submission__c  t = [SELECT Id FROM Template_Submission__c  LIMIT 1];
		ApexPages.currentPage().getParameters().put('CaseId',c.Id);
		ApexPages.currentPage().getParameters().put('relatedProID',t.Id);
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName,'test User1');                
		
        System.runAs(testUser)
        {
           Test.StartTest();
		   ClaimsSendUIProcessDetail_C_HUM claimSend = new ClaimsSendUIProcessDetail_C_HUM();
		   String retValue = claimSend.getPolicyMemberName();
		   System.assertEquals('Test Account', retValue);
           Test.stopTest();  
        }
        
    }

	/*
     * Method Name : logErrorsException
     * Description : Used to test lstLogErrors
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void logErrorsException()
    {   
		Test.setCurrentPage(Page.ClaimsSendUIProcessDetail_VF_HUM);
		Case c = [SELECT Id FROM Case LIMIT 1];
		Template_Submission__c  t = [SELECT Id FROM Template_Submission__c  LIMIT 1];
		ApexPages.currentPage().getParameters().put('CaseId',c.Id);
		ApexPages.currentPage().getParameters().put('relatedProID',t.Id);
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName,'test User1');                
		
        System.runAs(testUser)
        {
           Test.StartTest();
		   ClaimsSendUIProcessDetail_C_HUM claimSend = new ClaimsSendUIProcessDetail_C_HUM();
		   try {
			   Account acc = new Account();
			   insert acc;
		   } catch(Exception e) {
				claimSend.listErrorLogs = HUMExceptionHelper.logErrors(e, 'GuidedProcessDetail_C_HUM', 'GuidedProcessDetail_C_HUM');
				System.assertEquals(claimSend.listErrorLogs != null, true);
		   }
		   claimSend.listErrorLogs = null;
		   System.assertEquals(claimSend.listErrorLogs.isEmpty(), true);
           Test.stopTest();  
        }
        
    }	
}