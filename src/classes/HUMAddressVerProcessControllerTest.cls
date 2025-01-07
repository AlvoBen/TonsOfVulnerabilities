/***********************************************************************************************************
Apex Class Name  : HUMAddressVerificationProcessControllerTest 
Version          : 1.0
Created Date     :June 10 2015
Function         : Test class for HUMAddressVerificationProcessController 
************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Asha Deepika Koyyana                                                Original Version 
* Avinash Choubey              	33390	          07/11/2016          Changes made as part of REQ- 255151
* Asish Behera                                    06/25/2018          REQ - 367102 have the Person Speaking With and the Relationship to Member fields prefilled if the Interacting With is equal to Member or Member Representative or CareGiver
************************************************************************************************************/
@isTest
private class HUMAddressVerProcessControllerTest 
{
    
    @testSetup static void testData()
     {
        User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr;
        system.assertEquals(usr.lastname=='test User1',true);
        system.runAs(usr)
        { 
	        Account oAccount = new Account();
	        oAccount.Name = 'New Case Account';
	        oAccount.Group_Number__c  = '123';
	        oAccount.BillingState = 'New York';
	        oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
	        insert oAccount;
	        System.assert(oAccount.Name == 'New Case Account');
	        
	        Case oCase = new Case(AccountId=oAccount.Id,General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress');
	        insert oCase;
	        system.assert(oCase.Interacting_With_Type__c == 'Group');
	             
	        Template__c oTempate = new Template__c(Description__c =' Address Verification Flow');
	        insert oTempate;
	        system.assert(oTempate.Description__c == ' Address Verification Flow');  
	        
	        Template_Submission_Owner__c  oTemplateSubmsissionOwner = new Template_Submission_Owner__c(Object_Owner_ID__c = oCase.Id,  Object_Owner_Type__c = 'Case')  ;
	        insert oTemplateSubmsissionOwner;
	        system.assert(oTemplateSubmsissionOwner.Object_Owner_Type__c == 'Case');  
	        
	        Template_Submission__c oTempalteSubmsision = new Template_Submission__c(Template__c =oTempate.Id, Submission_Owner__c = oTemplateSubmsissionOwner.Id, Version__c = 1.00 );
	        insert oTempalteSubmsision;
	        system.assert(oTempalteSubmsision.Version__c == 1.00);  
	        
	        HUMConstants__c oHUMConstant = new HUMConstants__c(Name= 'HUMConstantOne', IntegerValue__c = 1); 
	        insert oHUMConstant;
	        System.assert(oHUMConstant.Name == 'HUMConstantOne');
	        
	        HUMConstants__c oHUMConstant1 = new HUMConstants__c(Name= 'HUMCase', StringValue__c ='Case'); 
	        insert oHUMConstant1;
	        System.assert(oHUMConstant1.Name == 'HUMCase');
	        
	        HUMConstants__c oHUMConstant2 = new HUMConstants__c(Name= 'HUMAVFMedicareID', StringValue__c = 'MedicareID'); 
	        insert oHUMConstant2;
	        System.assert(oHUMConstant2.Name == 'HUMAVFMedicareID');
	        
	        HUMConstants__c oHUMConstant3 = new HUMConstants__c(Name= 'HUMAVFMemberID', StringValue__c ='Member-Id-Base'); 
	        insert oHUMConstant3;
	        System.assert(oHUMConstant3.Name == 'HUMAVFMemberID');
            Interaction__c inte = HUMTestDataHelperTest.getInteraction();
        	Insert inte;
        	Case_Interaction__c caseInt = new Case_Interaction__c();
        	caseInt.Case__C = oCase.id;
        	caseInt.Interaction__c = inte.Id;
        	Insert caseInt; 
            
            //Below test record for Caller name and Interacting_With__c populated.
            Case oCaseWthCallName = new Case(AccountId=oAccount.Id,General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='StLouis',Department__c='CRU1', status='In Progress');
	        Insert oCaseWthCallName;
            Interaction__c inteWthCallName = HUMTestDataHelperTest.getInteraction();
            inteWthCallName.Interacting_With_type__c = 'Group';
            inteWthCallName.Interacting_With__c = oAccount.id;
        	Insert inteWthCallName;
        	Case_Interaction__c caseIntWthCallName = new Case_Interaction__c();
        	caseIntWthCallName.Case__C = oCaseWthCallName.id;
        	caseIntWthCallName.Interaction__c = inteWthCallName.Id;
        	Insert caseIntWthCallName;
            
            //Below test record for Caller name and Interacting_With__c populated.
            Case oCaseNoCallName = new Case(AccountId=oAccount.Id,General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='LouisVille',Department__c='CRU1', status='In Progress');
	        Insert oCaseNoCallName;
            Interaction__c inteNoCallName = HUMTestDataHelperTest.getInteraction();
            inteNoCallName.Caller_Name__c = '';
            inteNoCallName.Interacting_With_type__c = 'Group';
            inteNoCallName.Interacting_With__c = oAccount.id;
        	Insert inteNoCallName;
        	Case_Interaction__c caseIntNoCallName = new Case_Interaction__c();
        	caseIntNoCallName.Case__C = oCaseNoCallName.id;
        	caseIntNoCallName.Interaction__c = inteNoCallName.Id;
        	Insert caseIntNoCallName; 
        } 
    }
    
    /*
    * This method is used to call HUMAddressVerificationProcessController class
    * @param        NA
    * @return       void
    */
    static testMethod void testOverAll()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test User1',true);
         
        System.runAs (oUser) 
        {
            Test.StartTest();
             Case oCase = [select AccountId, Interacting_About_Type__c , Origin , 
                           Type , Intent_ID__c , Classification_ID__c , Intent_ID__r.name , Classification_ID__r.name ,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c ,
                           Priority , Line_of_Business__c , G_A_Rights_Given__c , 
                           G_A_Reason__c, Complaint__c , Complaint_Reason__c ,
                           Behavioral_Health__c ,status,RecordTypeId,recordType.developerName from Case where Interacting_With_Type__c = 'Group' limit 1];
            Case oCaseWthCallName = [select AccountId, Interacting_About_Type__c , Origin , 
                           Type , Intent_ID__c , Classification_ID__c , Intent_ID__r.name , Classification_ID__r.name ,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c ,
                           Priority , Line_of_Business__c , G_A_Rights_Given__c , 
                           G_A_Reason__c, Complaint__c , Complaint_Reason__c ,
                           Behavioral_Health__c ,status,RecordTypeId,recordType.developerName from Case where Interacting_With_Type__c = 'Group' AND Service_Center__c ='StLouis' limit 1];
            
            Case oCaseNoCallName = [select AccountId, Interacting_About_Type__c , Origin , 
                           Type , Intent_ID__c , Classification_ID__c , Intent_ID__r.name , Classification_ID__r.name ,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c ,
                           Priority , Line_of_Business__c , G_A_Rights_Given__c , 
                           G_A_Reason__c, Complaint__c , Complaint_Reason__c ,
                           Behavioral_Health__c ,status,RecordTypeId,recordType.developerName from Case where Interacting_With_Type__c = 'Group' AND Service_Center__c ='LouisVille' limit 1];
            System.assert(oCase.Interacting_With_Type__c == 'Group' ); 
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            Apexpages.currentPage().getParameters().put(Label.HUMCOBCaseIdParam,oCase.id);
            HUMAddressVerificationProcessController oHUMAddressVerificationProcessController = new HUMAddressVerificationProcessController(oStdController);
            
            ApexPages.StandardController oStdControllerWthCallName = new ApexPages.StandardController(oCaseWthCallName);
            Apexpages.currentPage().getParameters().put(Label.HUMCOBCaseIdParam,oCaseWthCallName.id);
            HUMAddressVerificationProcessController oHUMAddressVerificationProcessController1 = new HUMAddressVerificationProcessController(oStdControllerWthCallName);
            
            ApexPages.StandardController oStdControlleroCaseNoCallName = new ApexPages.StandardController(oCaseNoCallName);
            Apexpages.currentPage().getParameters().put(Label.HUMCOBCaseIdParam,oCaseNoCallName.id);
            HUMAddressVerificationProcessController oHUMAddressVerificationProcessController2 = new HUMAddressVerificationProcessController(oStdControlleroCaseNoCallName);
            
            Test.stopTest(); 
        }
    }
   }