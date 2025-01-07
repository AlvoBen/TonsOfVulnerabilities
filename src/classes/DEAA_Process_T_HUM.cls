/**********************************************************************************************************************************************************
Apex Class Name  : DEAA_Process_T_HUM
Version          : 1.0
Created Date     : Nov 28 2019
Function         : Test class for DEAA_Process_C_HUM,DEAA_Process_Q_HUM, DEAA_Process_S_HUM and DEAA_Process_H_HUM 
***********************************************************************************************************************************************************
Modification Log:

* Developer Name          Code Review     Date            Description
*------------------------------------------------------------------------------------------------------------
* Mohammed Noor                            11/28/2019     Original Version.
* Jasmeen Shangari                         04/21/2021     Checkmarx fixes Azure ID - 2163309
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
***********************************************************************************************************************************************************/
@isTest
Private Class DEAA_Process_T_HUM
{
   /**
    * Test Setup method to create all the data required for testing.
    *
    * @param     NULL
    * @return    void 
    */
   @testSetup   static void setUpTestData()
   {
     User DEAAUser = HUMTestDataHelperTest.createTestUser('DEAAUser','Customer Care Specialist');
        insert DEAAUser;
        system.assert(DEAAUser.LastName == 'test DEAAUser');
        
        system.runAS(DEAAUser)
        {
             
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        
            //Creating Account Record
            Account accRec = HUMTestDataHelperTest.getAccount();
            accRec.OwnerId = DEAAUser.id;
            accRec.FirstName = 'Test DEAA';
            accRec.LastName = 'Account';
            accRec.Enterprise_Id__c = '123456';
            accRec.recordtypeId = AccountRecordType.id;
            insert accRec;
            system.assertEquals(accRec.recordtypeId==AccountRecordType.id,true);
        
            Case CaseRecord = new Case(AccountId=accRec.Id,General_Case__c = true, Interacting_With_Type__c = 'Member',Interacting_With__c=accRec.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Louisville RSO Enrollment', status='In Progress',Medicare_Track_ID__c='2864231945');
            insert CaseRecord;
            system.assert(CaseRecord.Interacting_With_Type__c == 'Member');
            
            Case CaseRecord1 = new Case(AccountId=accRec.Id,General_Case__c = true, Interacting_With_Type__c = 'Member',Interacting_With__c=accRec.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Louisville RSO Enrollment', status='In Progress', Medicare_Track_ID__c = '2864231945');
            insert CaseRecord1;
            system.assert(CaseRecord1.Interacting_With_Type__c == 'Member');
            
            Case CaseRecord2 = new Case(AccountId=accRec.Id,General_Case__c = true, Interacting_With_Type__c = 'Member',Interacting_With__c=accRec.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Louisville RSO Enrollment', status='In Progress');
            insert CaseRecord2;
            system.assert(CaseRecord2.Interacting_With_Type__c == 'Member');
            
            Template__c TempateRec = new Template__c(Description__c ='DEAA Medicare Opt Out', Unique_Template_Name__c = 'DEAA_Template');
            insert TempateRec;
            system.assert(TempateRec.Description__c == 'DEAA Medicare Opt Out');  

            Template_Submission_Owner__c  TemplateSubmsissionOwnerRec = new Template_Submission_Owner__c(Object_Owner_ID__c = CaseRecord.Id,  Object_Owner_Type__c = 'Case')  ;
            insert TemplateSubmsissionOwnerRec;
            system.assert(TemplateSubmsissionOwnerRec.Object_Owner_Type__c == 'Case');  
            
            Template_Submission_Owner__c  TemplateSubmsissionOwnerRec1 = new Template_Submission_Owner__c(Object_Owner_ID__c = CaseRecord1.Id,  Object_Owner_Type__c = 'Case')  ;
            insert TemplateSubmsissionOwnerRec1;
            system.assert(TemplateSubmsissionOwnerRec1.Object_Owner_Type__c == 'Case');

            Template_Submission__c TempalteSubmsisionRec = new Template_Submission__c(Template__c =TempateRec.Id, Submission_Owner__c = TemplateSubmsissionOwnerRec.Id, Version__c = 0.00 );
            insert TempalteSubmsisionRec;
            system.assert(TempalteSubmsisionRec.Version__c == 0.00);  
            
            
            Template_Submission__c TempalteSubmsisionRec1 = new Template_Submission__c(Template__c =TempateRec.Id, Submission_Owner__c = TemplateSubmsissionOwnerRec1.Id, Version__c = 0.00 );
            insert TempalteSubmsisionRec1;
            system.assert(TempalteSubmsisionRec.Version__c == 0.00);
                         
            Template_Field__c TemplateFieldRec = New Template_Field__c(Name = 'DEAA_IsZAPIServiceCalled', Template__c =TempateRec.Id);
            insert TemplateFieldRec;
            system.assert(TemplateFieldRec.Name == 'DEAA_IsZAPIServiceCalled');
            
            Template_Field__c TemplateFieldRec1 = New Template_Field__c(Name = 'DEAA_MemberOptOutOption', Template__c =TempateRec.Id);
            insert TemplateFieldRec1;
            system.assert(TemplateFieldRec1.Name == 'DEAA_MemberOptOutOption');
            
            Template_Submission_Data__c TemplateSubmissionDataRec = New Template_Submission_Data__c(Template_Submission__c = TempalteSubmsisionRec.Id, Template_Field__c = TemplateFieldRec.Id, Value__c = 'No');
            insert TemplateSubmissionDataRec;
            
            Template_Submission_Data__c TemplateSubmissionDataRec1 = New Template_Submission_Data__c(Template_Submission__c = TempalteSubmsisionRec.Id, Template_Field__c = TemplateFieldRec1.Id, Value__c = 'No');
            insert TemplateSubmissionDataRec1;
                         
            Template_Submission_Data__c TemplateSubmissionDataRec2 = New Template_Submission_Data__c(Template_Submission__c = TempalteSubmsisionRec1.Id, Template_Field__c = TemplateFieldRec.Id, Value__c = 'No');
            insert TemplateSubmissionDataRec2;
            
            Template_Submission_Data__c TemplateSubmissionDataRec3 = New Template_Submission_Data__c(Template_Submission__c = TempalteSubmsisionRec1.Id, Template_Field__c = TemplateFieldRec1.Id, Value__c  = 'Yes');
            insert TemplateSubmissionDataRec3;
                        
            HUMConstants__c HUMConstantRec = new HUMConstants__c(Name= 'HUMCase', StringValue__c ='Case'); 
            insert HUMConstantRec;
            System.assert(HUMConstantRec.Name == 'HUMCase'); 
            
            HUM_Webservice_Callout__c svcHeaders = new HUM_Webservice_Callout__c();
            svcHeaders.Name = 'Zapi Service';
            svcHeaders.Active__c = true;
            svcHeaders.End_Point_URL__c = 'https://dev-crmservicews.humana.com/TemplateService_V2.0/SavePartDOptOut';
            svcHeaders.Certificate_Name__c = 'Humana_GuidanceCare_Mars';
            svcHeaders.Timeout__c = '120000';
            svcHeaders.Content_Type__c = 'application/json';
            svcHeaders.Request_Type__c = 'POST';
            insert svcHeaders;            
            system.assert(svcHeaders.Name=='Zapi Service');
        }
        
   }
   
   /*
    * This method is used to call DEAA_Process_C_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testOverAllDEAA()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test DEAAUser' LIMIT 1];
       system.assertEquals(oUser.lastname=='test DEAAUser',true);
       Case oCase = [select AccountId, Interacting_About_Type__c , Origin , CaseNumber,
                           Type , Intent_ID__c , Classification_ID__c , Intent_ID__r.name , Classification_ID__r.name ,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c ,
                           Priority , Line_of_Business__c , G_A_Rights_Given__c , 
                           G_A_Reason__c, Complaint__c , Complaint_Reason__c ,
                           Behavioral_Health__c ,status,RecordTypeId,recordType.developerName from Case where Interacting_With_Type__c = 'Member' limit 1];
       system.assert(oCase.Interacting_With_Type__c == 'Member' );
                
        System.runAs (oUser) 
        {
            Test.startTest();
            PageReference DEAA_Process_Page = Page.DEAA_Process_VF_HUM;
            Test.setCurrentPage(DEAA_Process_Page);                             
            ApexPages.currentPage().getParameters().put('CaseId',oCase.Id);
            DEAA_Process_C_HUM oDEAAProcessController = New DEAA_Process_C_HUM (); 
            oDEAAProcessController.getFinishPage();                       
            Test.stopTest();             
            System.assertEquals(oDEAAProcessController.SubmissionId,[SELECT ID FROM Template_Submission__c limit 1].Id);                    
        }
    }
    
   /*
    * This method Is used to call DEAA_Process_C_HUM class And test exception case
    * @param        NA
    * @return       void
    */
    static testMethod void testOverAllExceptionDEAA()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test DEAAUser' LIMIT 1];
        system.assertEquals(oUser.lastname=='test DEAAUser',true);
         
        System.runAs (oUser) 
        {
            Test.StartTest();
            PageReference DEAA_Process_Page = Page.DEAA_Process_VF_HUM;
            Test.setCurrentPage(DEAA_Process_Page);
           
            ApexPages.currentPage().getParameters().put('CaseId','');
            DEAA_Process_C_HUM oDEAAProcessController = New DEAA_Process_C_HUM ();                     
            Test.stopTest();            
            System.assertEquals(oDEAAProcessController.SubmissionId,null);                    
        }        
    }
    
    
   /*
    * This method is used to call DEAA_Process_Q_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testOverAllDEAA_Q()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test DEAAUser' LIMIT 1];
        system.assertEquals(oUser.lastname=='test DEAAUser',true);
         
        System.runAs (oUser) 
        {
            Test.startTest();
            Case oCase = [select Id from Case where Interacting_With_Type__c = 'Member' limit 1];
            List<String> lstStrings = new List<String>();
            String CaseValue=String.ValueOf(oCase.Id);
            lstStrings.add(CaseValue); 
            DEAA_Process_Q_HUM oDEAAProcessController_Q = New DEAA_Process_Q_HUM (lstStrings);
            Test.stopTest(); 
        }
    }
    
    /*
    * This method is used to call DEAA_Process_Q_HUM class And test exception case
    * @param        NA
    * @return       void
    */
    static testMethod void testOverAllExceptionDEAA_Q()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test DEAAUser' LIMIT 1];
        system.assertEquals(oUser.lastname=='test DEAAUser',true);
         
        System.runAs (oUser) 
        {
            Test.startTest();
            Try{
            List<String> lstStrings = New List<String>();
            lstStrings.clear();
            system.assertEquals(lstStrings.isEmpty(), true);
            DEAA_Process_Q_HUM oDEAAProcessController_Q = New DEAA_Process_Q_HUM(lstStrings);
            System.enqueueJob(oDEAAProcessController_Q);
            }
            Catch(Exception e)
            {
             system.assertEquals(e.getMessage(), e.getMessage(), e.getMessage());
            }
            Test.stopTest(); 
        }
    }
    
    /*
    * This method is used to call QueueableMethod from DEAA_Process_Q_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testQueueableMethod()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test DEAAUser' LIMIT 1];
        system.assertEquals(oUser.lastname=='test DEAAUser',true);
        Case oCase = [select Id from Case where Interacting_With_Type__c = 'Member' limit 1];
         
        System.runAs (oUser) 
        {
            Test.startTest();            
            List<String> lstStrings = new List<String>();
            String CaseValue=String.ValueOf(oCase.Id);
            lstStrings.add(CaseValue); 
            DEAA_Process_Q_HUM oDEAAProcessController_Q = New DEAA_Process_Q_HUM (lstStrings);
            System.enqueueJob(oDEAAProcessController_Q);
            List<String> lstString = New List<String>();
            String CaseValue1 ='1111111111111111';
            lstString.add(CaseValue1);
            DEAA_Process_Q_HUM qException = New DEAA_Process_Q_HUM(null);
            System.enqueueJob(New DEAA_Process_Q_HUM(lstString));
            Test.stopTest();    
        }
    }
    
   /*
    * This method is used to call DEAA_Process_H_HUM class and Test Negative Case
    * @param        NA
    * @return       void
    */
    static testMethod void testOverAllDEAA_H()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test DEAAUser' LIMIT 1];
       system.assertEquals(oUser.lastname=='test DEAAUser',true);
       list<Case> lstCase = [select AccountId, Interacting_About_Type__c , Origin , CaseNumber,
                           Type , Intent_ID__c , Classification_ID__c , Intent_ID__r.name , Classification_ID__r.name ,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c ,
                           Priority , Line_of_Business__c , G_A_Rights_Given__c , 
                           G_A_Reason__c, Complaint__c , Complaint_Reason__c ,
                           Behavioral_Health__c ,status,RecordTypeId,recordType.developerName from Case where Interacting_With_Type__c = 'Member' and Medicare_Track_ID__c != null];
        system.assertEquals(lstCase.isEmpty(), false);
        List<String> lstStrings = new List<String>();            
        for(Case oCase : lstCase)
        {
        	lstStrings.add(oCase.ID);
        }
        system.assertEquals(lstStrings.isEmpty(), false);
        System.runAs (oUser) 
        {
            Test.StartTest();
            Test.setMock(HttpCalloutMock.class, new DEAA_ZAPIServiceMockRespons_T_HUM());
            
            DEAA_Process_H_HUM oDEAAProcessController_H = New DEAA_Process_H_HUM (lstStrings);            
            oDEAAProcessController_H.invokeZAPIService();
            DEAA_Process_H_HUM oDEAAProcessController_H1 = New DEAA_Process_H_HUM (null);            
            Test.stopTest(); 
        }
    }
    
    /*
    * This method is used to call DEAA_Process_H_HUM class and Test Negative Case
    * @param        NA
    * @return       void
    */
    static testMethod void testExceptionDEAA_H()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test DEAAUser' LIMIT 1];
       system.assertEquals(oUser.lastname=='test DEAAUser',true);
       list<Case> lstCase = [select AccountId, Interacting_About_Type__c , Origin , CaseNumber,
                           Type , Intent_ID__c , Classification_ID__c , Intent_ID__r.name , Classification_ID__r.name ,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c ,
                           Priority , Line_of_Business__c , G_A_Rights_Given__c , 
                           G_A_Reason__c, Complaint__c , Complaint_Reason__c ,
                           Behavioral_Health__c ,status,RecordTypeId,recordType.developerName from Case where Interacting_With_Type__c = 'Member'];
        system.assertEquals(lstCase.isEmpty(), false);
        List<String> lstStrings = new List<String>();            
        for(Case oCase : lstCase)
        {
        	lstStrings.add(oCase.ID);
        }
        system.assertEquals(lstStrings.isEmpty(), false);
        System.runAs (oUser) 
        {
            Test.StartTest();
            Test.setMock(HttpCalloutMock.class, new DEAA_ZAPIServiceMockRespons_T_HUM());            
            DEAA_Process_H_HUM oDEAAProcessController_H = New DEAA_Process_H_HUM (lstStrings);            
            oDEAAProcessController_H.invokeZAPIService();
			DEAA_Process_H_HUM.UpdateCaseDEAABatch(lstCase);  
            DEAA_Process_H_HUM oDEAAProcessController_H1 = New DEAA_Process_H_HUM (null);            
            Test.stopTest(); 
        }
    }
            
    /*
    * This method is used to call processDEAATemplateAfterFinish method from DEAA_Process_S_HUM class 
    * @param        NA
    * @return       void
    */
    static testMethod void processDEAATemplate()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test DEAAUser' LIMIT 1];
       	system.assertEquals(oUser.lastname=='test DEAAUser',true);
        Case oCase = [select Id from Case where Interacting_With_Type__c = 'Member' limit 1];
        Template_Submission__c temp = [select Id from Template_Submission__c limit 1 ];
        list<List<String>> lstStrings = new list<List<String>>
                                    {
                                        new List<String>{String.ValueOf(oCase.Id),String.valueOf(temp.Id)}
                                        
                                    };                                                                                                             
       	system.runAs(oUser)
       	{
			Test.startTest();
			DEAA_Process_S_HUM.processDEAATemplateAfterFinish(lstStrings);
			Test.stopTest();
       	}
    }
    
   /**
    * Test method for ZAPI SERVICE response
    * 
    * @param    NA
    * @return   void
    */    
	public static testmethod void testZAPIResp()
    {
		User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test DEAAUser' LIMIT 1];
       	system.assertEquals(oUser.lastname=='test DEAAUser',true);
        Case oCase = [select Id from Case where Interacting_With_Type__c = 'Member' limit 1];
        list<String> lstStrings = new list<String>();            
        lstStrings.add(oCase.Id);
       	
       	Test.startTest();                         
        Test.setMock(HttpCalloutMock.class, new DEAA_ZAPIServiceMockRespons_T_HUM());
		DEAA_Process_H_HUM oDEAAProcess_H = new DEAA_Process_H_HUM(lstStrings);
		oDEAAProcess_H.invokeZAPIService();
		DEAA_ZAPIServiceResponse_DTO_HUM oDEAA_Response = new DEAA_ZAPIServiceResponse_DTO_HUM();
		DEAA_ZAPIServiceResponse_DTO_HUM.SavePartDOptOutResponse res = new DEAA_ZAPIServiceResponse_DTO_HUM.SavePartDOptOutResponse();
		res.CaseNumber = '1000000262663';
		res.Type = 'INFORMATION';
		res.Code = 'CICMSIN003';
		res.Description = 'CASE STATUS & OPT-OUT-IND UPDATE SUCCESSFUL.';
		oDEAA_Response.SavePartDOptOutResponse = res;
		system.assertNotEquals(oDEAA_Response, null);
        Test.stopTest();                               
    }
	
   /**
    * Test method for DEAA Batch Job response
    * 
    * @param    NA
    * @return   void
    */      
    public static testmethod void testUpdateCaseDEAABatch()
    {
        User usr = HUMTestDataHelperTest.createTestUser('DEAAProcessBatch','Customer Care Specialist');
        insert usr;
        system.assertEquals(usr.lastname=='test DEAAProcessBatch',true);
        system.runAs(usr)
        { 
            Account oAccount = new Account();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.Name == 'New Case Account');
            
            Classification_Type__c classificationType = new Classification_Type__c(name = 'Enrollment (RSO)', Is_Active__c = true);
             insert classificationType ;
             system.assertNotEquals(classificationType, null , 'classificationType  not created');
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'DEAA Plan Change';
           
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'DEAA Plan Change',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Grace Period Expired';
           
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'Grace Period Expired',true);
            
            List<CTCI_Junction__c> listctciJunction = new List<CTCI_Junction__c>();
             CTCI_Junction__c ctciJunction  =  new CTCI_Junction__c(Classification__c =  classificationRecDec.ID, Classification_Type__c = classificationType.ID, Intent__c = intentRec.ID , IsActive__c = true);
             listctciJunction.Add(ctciJunction);
             insert listctciJunction ;
             system.assertNotEquals(listctciJunction, null ,'CTCIJunctionlist  not created'); 

            Case oCase = new Case(AccountId=oAccount.Id,General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today().addDays(-1), Case_Owner__c='Louisville RSO Enrollment', Owner_Queue__c='Louisville RSO Enrollment', Work_Queue_View_Name__c='Other',Service_Center__c ='Louisville',Department__c='Retail Service Operations (RSO) Enrollment', status='In Progress', Origin='DEAA');
            insert oCase;
            Case oCase1 = new Case(AccountId=oAccount.Id,General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today().addDays(-1), Case_Owner__c='Louisville RSO Enrollment', Owner_Queue__c='Louisville RSO Enrollment', Work_Queue_View_Name__c='Other',Service_Center__c ='Louisville',Department__c='Retail Service Operations (RSO) Enrollment', status='In Progress', Origin='DEAA');
            insert oCase1;
            Case oCase2 = new Case(AccountId=oAccount.Id,General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today().addDays(-1), Case_Owner__c='Louisville RSO Enrollment', Owner_Queue__c='Louisville RSO Enrollment', Work_Queue_View_Name__c='Other',Service_Center__c ='Louisville',Department__c='Retail Service Operations (RSO) Enrollment', status='In Progress', Origin='DEAA');
            insert oCase2;
            Task oTask = new Task(WhatId = oCase2.id, Task_Owner__c = 'Louisville RSO Enrollment', Owner_Queue__c = 'Louisville RSO Enrollment',Work_Queue_View_Name__c = 'Other', Service_Center__c = 'Louisville', Department__c = 'Retail Service Operations (RSO) Enrollment', status = 'In Progress' ,Description = 'Hi Comment' );
            insert oTask;               
            system.assert(oTask.Owner_Queue__c == 'Louisville RSO Enrollment');
            system.assert(oCase.Interacting_With_Type__c == 'Group');  

            HUM_Webservice_Callout__c svcHeaders1 = new HUM_Webservice_Callout__c();
            svcHeaders1.Name = 'Zapi Service';
            svcHeaders1.Active__c = true;
            svcHeaders1.Certificate_Name__c = 'Humana_GuidanceCare_Mars';
            insert svcHeaders1;    
                        
            list<Case> lstCase = [Select Id, CaseNumber, AccountId, Type, Status,Subtype__c, Origin, Due_Date__c, Complaint_Type__c, Case_Owner__c, Owner_Queue__c, G_A_Rights_Given__c, Complaint__c FROM Case where IsClosed = false and Origin = 'DEAA' and Due_Date__C < today];
                           
            List<String> lstStrings = new List<String>();            
            for(Case lsCase : lstCase)
            {
                lstStrings.add(lsCase.ID);
            } 
                                               
            Test.StartTest();
            DEAA_Process_H_HUM.UpdateCaseDEAABatch(lstCase);   
            
            Test.setMock(HttpCalloutMock.class, new DEAA_ZAPIServiceMockRespons_T_HUM());            
            DEAA_Process_H_HUM oDEAAProcessController_H = New DEAA_Process_H_HUM (lstStrings);            
            oDEAAProcessController_H.invokeZAPIService();
                    
            Test.stopTest();   
    }
}	
}