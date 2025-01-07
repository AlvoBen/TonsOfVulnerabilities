/***********************************************************************************************************
Apex Class Name  : CloseFlow_T_HUM
Version          : 1.0
Created Date     : April 22 2018
Function         : Test class for CloseFlow_C_HUM 
************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Moshitha Gunasekaran          218172                04/22/2018                 Original Version 
* Moshitha Gunasekaran                                01/29/2020                 Story - 725129 - DEAA Medicare Opt Out process display in CRM for DEAA (Dual Eligible Auto Assigned) cases
* Jasmeen Shangari                                    04/21/2021                 Checkmarx fixes Azure ID - 2163309
************************************************************************************************************/
@isTest
private class CloseFlow_T_HUM {
    @testSetup static void testData()
     {
        User usr = HUMTestDataHelperTest.createTestUser('CloseFlowUser1','Customer Care Specialist');
        insert usr;
        system.assertEquals(usr.lastname=='test CloseFlowUser1',true);
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
            Case oCase1 = new Case(AccountId=oAccount.Id,General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='Cincinnati',Department__c='CRU2', status='In Progress');
            insert oCase1;
            system.assert(oCase.Interacting_With_Type__c == 'Group');
            
       if([SELECT ID FROM Template__c WHERE Unique_Template_Name__c='ATVF007'].isEmpty())
       {
        //Insert Template data
        Template__c templateObj = new Template__c(Unique_Template_Name__c='ATVF007',Description__c='Creditable Coverage Attestation');
        Insert templateObj;     
        
        //Insert Template Field Data
        List<Template_Field__c> templateFieldObj = new List<Template_Field__c>();
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_CaseNumber'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_CRMCaseCreateDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MedicareTrackId'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_RecieptDate'));        
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PersonSpeakingWith'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_POAUnderstandDefOfCridtableCoverage'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_OtherMemberConsentMoveForwardCall'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_90DaysLetterSent'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FinalDisclaimer'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_NeverHadCrditableCoverage'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FEHBPCoveragePCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FEHBPCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_FEHBPCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_SPAPCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_SPAPCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_SPAPCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_VACoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_VACoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_VACoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_TRICARECoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_TRICARECoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_TRICARECoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MediGapCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MediGapCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_MediGapCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ITUCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ITUCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ITUToCoverageDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PACECoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PACECoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PACECoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageSourceName'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_DiffSourceOfCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_HumanaCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_HumanaCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_HumanaCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ExtraHelpCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ExtraHelpCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_ExtraHelpCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_KatrinaCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_KatrinaCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_KatrinaCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PuertoRicoCoverageCheck'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PuertoRicoCoverageFromDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_PuertoRicoCoverageToDate'));
        templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ATVF_IsCICMSCalled'));
        
        insert templateFieldObj;
        
    }
    if([select id from HUMTemplateSettings__c where name = 'ATTVF'].isEmpty())
    {
        insert (new HUMTemplateSettings__c(Name='ATTVF',ProcessActionType__c='Creditable Coverage Attestation Verification Form',VFPageName__c='AttestationVerification_VF_HUM'));
    }                  
     
    List<Template_Submission_Data__c> submissionList = new List<Template_Submission_Data__c>();
        Template__c templateObj = [Select id from Template__c where Unique_Template_Name__c='ATVF007'];

         Template_Submission_Owner__c tOnwer = new Template_Submission_Owner__c();
         tOnwer.Object_Owner_ID__c=string.valueOf(oCase1.id);
         tOnwer.Object_Owner_Type__c='Case';
         insert tOnwer;

         Template_Submission__c submission = new Template_Submission__c();
         submission.Template__c = templateObj.id;
         submission.Submission_Owner__c = tOnwer.id;
         insert submission;
         
         List<Template_Submission_Data__c> newSubmissionData = new List<Template_Submission_Data__c>();
        for(Template_Field__c submissionDataObj : [SELECT id,Name from Template_Field__c])
        {
            Template_Submission_Data__c tObj = new Template_Submission_Data__c();
            tObj.Template_Field__c = submissionDataObj.Id;
            tObj.Template_Submission__c = submission.id;
            
            
            if(submissionDataObj.Name == 'ATVF_RecieptDate')
            {
                dateTime strReciepDateTime = datetime.now();
                tObj.Value__c = (String) strReciepDateTime.format('MM/dd/yyyy');
            }
            
            if(submissionDataObj.Name == 'ATVF_IsCICMSCalled')
            {
                tObj.Value__c = 'NO' ;
            }
            
            if(submissionDataObj.Name == 'ATVF_MemberId')
            {
                tObj.Value__c = '123456' ;
            }
                            
            if(submissionDataObj.Name == 'ATVF_CaseNumber')
            {
                tObj.Value__c = '123456' ;
            }

            if(submissionDataObj.Name == 'ATVF_CRMCaseCreateDate')
            {
                dateTime strCreatedDateTime = datetime.now();
                tObj.Value__c = (String) strCreatedDateTime.format('MM/dd/yyyy');           
            }     
            
            if(submissionDataObj.Name == 'ATVF_MedicareTrackId')
            {
                tObj.Value__c = '123456';
            }
            
            if(submissionDataObj.Name == 'ATVF_FEHBPCoveragePCheck')
            {
                tObj.Value__c = 'true' ;
            }
            
            if(submissionDataObj.Name == 'ATVF_FEHBPCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_FEHBPCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_SPAPCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_SPAPCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_SPAPCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_VACoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_VACoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_VACoverageToDate')
            {
                tObj.Value__c ='03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_TRICARECoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_TRICARECoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_TRICARECoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_MediGapCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_MediGapCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_MediGapCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ITUCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_ITUCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ITUToCoverageDate')
            {
                tObj.Value__c = '03/2018';
            }               

            if(submissionDataObj.Name == 'ATVF_PACECoverageCheck')
            {
                tObj.Value__c = 'true';
            }               
            
            if(submissionDataObj.Name == 'ATVF_PACECoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }   
            
            if(submissionDataObj.Name == 'ATVF_PACECoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }               
            
            if(submissionDataObj.Name == 'ATVF_DiffSourceOfCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_DiffSourceOfCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_DiffSourceOfCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_HumanaCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_HumanaCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_HumanaCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ExtraHelpCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_ExtraHelpCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_ExtraHelpCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_KatrinaCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_KatrinaCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_KatrinaCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_PuertoRicoCoverageCheck')
            {
                tObj.Value__c = 'true';
            }
            
            if(submissionDataObj.Name == 'ATVF_PuertoRicoCoverageFromDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_PuertoRicoCoverageToDate')
            {
                tObj.Value__c = '03/2018';
            }
            
            if(submissionDataObj.Name == 'ATVF_NeverHadCrditableCoverage')
            {
                tObj.Value__c = 'true';
            }                
            newSubmissionData.add(tObj);
        }
        insert newSubmissionData;    
                
   
               
                 
        } 
    }
    
    /*
    * This method is used to call CloseFlow_C_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testOverAll()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CloseFlowUser1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test CloseFlowUser1',true);
         
        System.runAs (oUser) 
        {
            Test.StartTest();
            PageReference humClosePage = Page.HUMCloseFlow;
            Test.setCurrentPage(humClosePage);
            Case oCase = [select AccountId, Interacting_About_Type__c , Origin , 
                           Type , Intent_ID__c , Classification_ID__c , Intent_ID__r.name , Classification_ID__r.name ,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c ,
                           Priority , Line_of_Business__c , G_A_Rights_Given__c , 
                           G_A_Reason__c, Complaint__c , Complaint_Reason__c ,
                           Behavioral_Health__c ,status,RecordTypeId,recordType.developerName from Case where Interacting_With_Type__c = 'Group' limit 1];
            System.assert(oCase.Interacting_With_Type__c == 'Group' );
            ApexPages.currentPage().getParameters().put('ProcessType','ATTVF');            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            CloseFlow_C_HUM oCloseFlowController = new CloseFlow_C_HUM(oStdController);
            Test.stopTest(); 
            
        }
    }
    
    /*
    * This method is used to call CloseFlow_C_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testNoProcessTypeWithCaseId()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CloseFlowUser1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test CloseFlowUser1',true);
         
        System.runAs (oUser) 
        {
            Test.StartTest();
            PageReference humClosePage = Page.HUMCloseFlow;
            Test.setCurrentPage(humClosePage);
            Case oCase = [select AccountId, Interacting_About_Type__c , Origin , 
                           Type , Intent_ID__c , Classification_ID__c , Intent_ID__r.name , Classification_ID__r.name ,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c ,
                           Priority , Line_of_Business__c , G_A_Rights_Given__c , 
                           G_A_Reason__c, Complaint__c , Complaint_Reason__c ,
                           Behavioral_Health__c ,status,RecordTypeId,recordType.developerName from Case where Interacting_With_Type__c = 'Group' limit 1];
            System.assert(oCase.Interacting_With_Type__c == 'Group' );
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put('ATTVFCaseId',oCase.Id);
            CloseFlow_C_HUM oCloseFlowController = new CloseFlow_C_HUM(oStdController);
            Test.stopTest(); 
            
        }
    }
    
     /*
    * This method is used to call CloseFlow_C_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testNoProcessTypeWithCaseIdNegative()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CloseFlowUser1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test CloseFlowUser1',true);
         
        System.runAs (oUser) 
        {
            Test.StartTest();
            PageReference humClosePage = Page.HUMCloseFlow;
            Test.setCurrentPage(humClosePage);
            Case oCase = [select Id from Case where Interacting_With_Type__c = 'Group' and Department__c='CRU2' limit 1];
            ApexPages.currentPage().getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,'Case');
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put('ATTVFCaseId',oCase.Id);
            CloseFlow_C_HUM oCloseFlowController = new CloseFlow_C_HUM(oStdController);
            Test.stopTest(); 
            
            
        }
    }

    /*
    * This method is used to call CloseFlow_C_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testDEAACase()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CloseFlowUser1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test CloseFlowUser1',true);
         
        System.runAs (oUser) 
        {
            Test.StartTest();
            PageReference humClosePage = Page.HUMCloseFlow;
            Test.setCurrentPage(humClosePage);
            Case oCase = [select Id from Case where Interacting_With_Type__c = 'Group' and Department__c='CRU2' limit 1];
            ApexPages.currentPage().getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,'Case');
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put('DEAACaseId',oCase.Id);
            CloseFlow_C_HUM oCloseFlowController = new CloseFlow_C_HUM(oStdController);
            Test.stopTest(); 
            
        }
    }

    /*
    * This method is used to call CloseFlow_C_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testEXPCase()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CloseFlowUser1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test CloseFlowUser1',true);
         
        System.runAs (oUser) 
        {
            Test.StartTest();
            PageReference humClosePage = Page.HUMCloseFlow;
            Test.setCurrentPage(humClosePage);
            Case oCase = [select Id from Case where Interacting_With_Type__c = 'Group' and Department__c='CRU2' limit 1];
            ApexPages.currentPage().getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,'Case');
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put('ExpCaseId',oCase.Id);
            CloseFlow_C_HUM oCloseFlowController = new CloseFlow_C_HUM(oStdController);
            Test.stopTest(); 
            
        }
    }
    
    /*
    * This method is used to call CloseFlow_C_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testPartDCase()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CloseFlowUser1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test CloseFlowUser1',true);
         
        System.runAs (oUser) 
        {
            Test.StartTest();
            PageReference humClosePage = Page.HUMCloseFlow;
            Test.setCurrentPage(humClosePage);
            Case oCase = [select Id from Case where Interacting_With_Type__c = 'Group' and Department__c='CRU2' limit 1];
            ApexPages.currentPage().getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,'Case');
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put('PartD',oCase.Id);
            CloseFlow_C_HUM oCloseFlowController = new CloseFlow_C_HUM(oStdController);
            Test.stopTest(); 
            
        }
    }
    
    /*
    * This method is used to call CloseFlow_C_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testPCPCase()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CloseFlowUser1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test CloseFlowUser1',true);
       
        System.runAs (oUser) 
        {      
            Account oAccount = new Account();
            oAccount.Name = 'PCP Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.Name == 'PCP Case Account');

            Case oCase = new Case(AccountId=oAccount.Id,General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='Cincinnati',Department__c='CRU3', status='In Progress');
            insert oCase;      
            
            //Insert Template data
            Template__c templateObj = new Template__c(Unique_Template_Name__c='d884d160-77c0-4d1b-95aa-edf8c81f3a59',Description__c='PCP');
            Insert templateObj;     
            
            //Insert Template Field Data
            List<Template_Field__c> templateFieldObj = new List<Template_Field__c>();
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Did you receive a Service Fund edit when attempting to change the member\'s PCP'));       
            insert templateFieldObj;     

             Template_Submission_Owner__c tOnwer = new Template_Submission_Owner__c();
             tOnwer.Object_Owner_ID__c=string.valueOf(oCase.id);
             tOnwer.Object_Owner_Type__c='Case';
             insert tOnwer;

             Template__c templateObj1 = [Select id from Template__c where Unique_Template_Name__c='d884d160-77c0-4d1b-95aa-edf8c81f3a59'];         
             Template_Submission__c submission = new Template_Submission__c();
             submission.Template__c = templateObj1.id;
             submission.Submission_Owner__c = tOnwer.id;
             insert submission;     
             
            List<Template_Submission_Data__c> newSubmissionData = new List<Template_Submission_Data__c>();
            for(Template_Field__c submissionDataObj : [SELECT id,Name from Template_Field__c])
            {
                Template_Submission_Data__c tObj = new Template_Submission_Data__c();
                tObj.Template_Field__c = submissionDataObj.Id;
                tObj.Template_Submission__c = submission.id;
                
                
                if(submissionDataObj.Name == 'Did you receive a Service Fund edit when attempting to change the member\'s PCP')
                {
                    tObj.Value__c = 'Yes';
                }
                 newSubmissionData.add(tObj);
            }    
            insert newSubmissionData;       
         

            Test.StartTest();
            PageReference humClosePage = Page.HUMCloseFlow;
            Test.setCurrentPage(humClosePage);
            Case oCase1 = [select Id from Case where Interacting_With_Type__c = 'Group' and Department__c='CRU3' limit 1];
            ApexPages.currentPage().getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,'Case');
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase1);
            ApexPages.currentPage().getParameters().put('PCP',oCase1.Id);
            CloseFlow_C_HUM oCloseFlowController = new CloseFlow_C_HUM(oStdController);
            Test.stopTest(); 
            
        }
    }   
	
   /*
    * This method is used to call CloseFlow_C_HUM class
    * @param        NA
    * @return       void
    */
    static testMethod void testGoodCauseCase()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CloseFlowUser1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test CloseFlowUser1',true);
         
        System.runAs (oUser) 
        {
            Test.StartTest();
            PageReference humClosePage = Page.HUMCloseFlow;
            Test.setCurrentPage(humClosePage);
            Case oCase = [select Id from Case where Interacting_With_Type__c = 'Group' and Department__c='CRU2' limit 1];
            ApexPages.currentPage().getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,'Case');
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put('GoodCause',oCase.Id);
            CloseFlow_C_HUM oCloseFlowController = new CloseFlow_C_HUM(oStdController);
            Test.stopTest(); 
            
        }
    }    
}