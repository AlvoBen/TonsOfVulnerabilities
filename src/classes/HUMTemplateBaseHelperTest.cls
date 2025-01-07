/****************************************************************************
Apex Class Name  : HUMTemplateBaseHelperTest
Version          : 1.0 
Created On       : 05/21/2015
Function         : Test class to HUMTemplateBaseHelper apex class

Modification Log :
* Developer Name              Code Review                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Santhi Mandava               24116                        05/21/2015                 Original Version
* Tanushree Hazari             24116                        06/19/2015                 Modified to increase code coverage of 
                                                                                       HUMMedicareOIProcessController class 
* Ranjeeth Nagishetty          141064                       10/11/2017                 Added the code related  HUMMedicareReinstatProcessController                                                                                            
* Pallavi Ravishankar          338657                       02/27/2019                 REQ - 383432 - Medicaid Good Cause Request Process
* Mohammed Noor                                             06/08/2020   
* Mithra Bharadwaj                                           09/21/2020                   US#1393270 CRM Process - Initiating queue/views for transfers to MHK
* Y Sunil Kumar Reddy					    01/21/2021			User Story 1763850: PR00091574 - MF 1 - Quality - "Medicaid PAR Provider Not Accepting" template and case validations (CRM)
* Jayeshkumar Solanki					    03/22/2021			User Story 2187337: T1PRJ0001827 - MF 1 - Quality - "Medicare CE and Sp Election DE Process" Template Implementation (RF)
*************************************************************************************************************************/

@isTest
private class HUMTemplateBaseHelperTest
{    
        /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('ProessUser1','Customer Care Specialist');        
        testUser.Current_Queue__c='Test Business Process and Testing';        
        insert testUser;
        system.assertEquals(testUser.lastname =='test ProessUser1',true);
                                                        
        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
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
        
        Classification_Type__c ctRec1 = New Classification_Type__c();
        ctRec1.name = 'calls (RSO)';
        ctRec1.Is_Active__c=true;        
        insert ctRec1;
        
        List<Classification__c> lstClassification = New List<Classification__c>();
        Classification__c classificationRec = New Classification__c();
        classificationRec.Name = 'G&A';
        classificationRec.Is_Active__c = true;
        lstClassification.add(classificationRec);
        
        Classification__c classificationRecDec = New Classification__c();
        classificationRecDec.Name = 'Rx';
        classificationRecDec.Is_Active__c = true;
        lstClassification.add(classificationRecDec);
        
        insert lstClassification;

        List<Intent__c> lstIntent = New List<Intent__c>();
        Intent__c intentRec = New Intent__c();
        intentRec.Name = 'Expedited';
        intentRec.Is_Active__c = true;
        lstIntent.add(intentRec);
        
        Intent__c intentRecDec = New Intent__c();
        intentRecDec.Name = 'Part D Redetermination';
        intentRecDec.Is_Active__c = true;
        lstIntent.add(intentRecDec);

        insert lstIntent;
        
        List<CTCI_Junction__c> ctciList = New list<CTCI_Junction__c>();
        CTCI_Junction__c ctciRec = New CTCI_Junction__c();
        ctciRec.Classification_Type__c = ctRec1.id;
        ctciRec.Classification__c = classificationRec.Id;
        ctciRec.Intent__c = intentRec.id;
        ctciRec.IsActive__c = true;
        ctciList.add(ctciRec);
     
        CTCI_Junction__c ctciRec1 = New CTCI_Junction__c();
        ctciRec1.Classification_Type__c = ctRec1.id;
        ctciRec1.Classification__c = classificationRecDec .Id;
        ctciRec1.Intent__c = intentRecDec.id;
        ctciRec1.IsActive__c = true;
        ctciList.add(ctciRec1);
        
        insert ctciList;
        
        system.runAs(testUser)
        { 
            Case objCase = HUMTestDataHelperTest.getCase();            
            objCase.Guided_Process_Flag__c = True;
            objCase.Owner_Queue__c = 'Louisville RSO Calls Support';
            objCase.Case_Owner__c = 'Louisville RSO Calls Support';
            objCase.Work_Queue_View_Name__c = 'Other';
            objCase.status = 'In Progress';        
            objCase.Policy_Member__c = policyMemRec.Id;
            objCase.AccountID = accountRec.Id;
            insert objCase;
            system.assertNotEquals(objCase, null, 'Value not matched');            
            
            HUMConstants__c objType = new HUMConstants__c(Name = 'HUMCase',StringValue__c = 'Case');
            insert objType;
            system.assertEquals(objType.id!=null,true); 
        }
        
    }
	
	/*
     * Method Name : OverAllMainTestMethod
     * Description : This is the main method which is used to test template base helper. 
     * Return type : Void
     * Parameter   : NA
     */     
    private static testMethod void OverAllMainTestMethod()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        insert oUser;
        system.assertNotEquals(oUser,null,'Value not matched'); 
        
        HUMConstants__c objType = new HUMConstants__c(Name = 'HUMCase',StringValue__c = 'Case');
        insert objType;
        system.assertEquals(objType.id!=null,true); 
        
        system.runAs(oUser)
        {
            Case objCase = HUMTestDataHelperTest.getCase();
            insert objCase; 
            system.assertNotEquals(objCase ,null,'Value not matched');  
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(objCase);
             ApexPages.currentPage().getParameters().put('CaseId', objCase.id);
            system.assertNotEquals(oStdController ,null,'Value not matched'); 
             
            HUMTemplateBaseHelper objHelper = new HUMTemplateBaseHelper();
            system.assertNotEquals(objHelper ,null,'Value not matched');  
            
            HUMMedicareOIProcessController objOI = new HUMMedicareOIProcessController(oStdController );
            system.assertNotEquals(objOI ,null,'Value not matched');  
            
            HUMMedicarePartDRedeterProcessController objPartD = New HUMMedicarePartDRedeterProcessController(oStdController );
            objPartD.getFinishPage();
            system.assertNotEquals(objPartD ,null,'Value not matched');
              
            HUMMedicareExpAppealProcessController objExpApp = New HUMMedicareExpAppealProcessController(oStdController );
            objExpApp.getFinishPage();
            system.assertNotEquals(objExpApp ,null,'Value not matched'); 
            
            HUMMedicareReinstatProcessController objGoodcause = new HUMMedicareReinstatProcessController(oStdController);
            system.assertNotEquals(objGoodcause ,null,'Value not matched');
            
            MedicaidGoodCauseRequestProcess_C_HUM objMedicaidRequest = new MedicaidGoodCauseRequestProcess_C_HUM(oStdController);
            system.assertNotEquals(objMedicaidRequest ,null,'Value not matched');

            MedicaidPARProviderNotAccepting_C_HUM objMedicaidPAR = new MedicaidPARProviderNotAccepting_C_HUM(oStdController);
            objMedicaidPAR.getFinishPage();
            system.assertNotEquals(objMedicaidPAR ,null,'Value not matched');
             
        }
    }
	
	/*
     * Method Name : testMedicareExpeditedProcess
     * Description : This is the main method which is used to test HUMMedicareExpAppealProcessController. 
     * Return type : Void
     * Parameter   : NA
     */     
    private static testMethod void testMedicareExpeditedProcess()
    {
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test ProessUser1'];
        System.assertEquals(testUser.LastName,'test ProessUser1');
  
        System.runAs(testUser)
        {  
            Test.startTest() ;
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = '1393270';
            crm1.Scrum_Team__c = 'Transformers';
            crm1.Story_Title__c = 'CRM Process - Initiating queue/views for transfers to MHK';
            crm1.Release_Name__c = '10/23/2020';
            insert crm1;  
            Case caseRecord = [Select id, status from case limit 1];
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(caseRecord);
            ApexPages.currentPage().getParameters().put('CaseId', caseRecord.id); 
            system.assertNotEquals(oStdController ,null,'Value not matched'); 
             
            HUMTemplateBaseHelper objHelper = new HUMTemplateBaseHelper();
            system.assertNotEquals(objHelper ,null,'Value not matched'); 

            HUMMedicareExpAppealProcessController objExpApp = New HUMMedicareExpAppealProcessController(oStdController );            
            HUMMedicareExpAppealProcessController.updateCaseAutoClassificationAndIntentEXP(new list<Case>{caseRecord});
            system.assertNotEquals(objExpApp ,null,'Value not matched'); 
            CRMFunctionality_ONOFF_Switch__c crm2 = new CRMFunctionality_ONOFF_Switch__c();
             crm2.IsON__c =false;
            crm2.Name = '1393270';
            crm2.Scrum_Team__c = 'Transformers';
            crm2.Story_Title__c = 'CRM Process - Initiating queue/views for transfers to MHK';
            crm2.Release_Name__c = '10/23/2020';
            insert crm2;
            HUMMedicareExpAppealProcessController.updateCaseAutoClassificationAndIntentEXP(new list<Case>{caseRecord});
            Test.stopTest();
        }
    }    

    /*
     * Method Name : testMedicarePartDProcess
     * Description : This is the main method which is used to test HUMMedicarePartDRedeterProcessController. 
     * Return type : Void
     * Parameter   : NA
     */     
    private static testMethod void testMedicarePartDProcess()
    {
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test ProessUser1'];
        System.assertEquals(testUser.LastName,'test ProessUser1');
  
        System.runAs(testUser)
        {  
            Test.startTest() ;
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = '1393270';
            crm1.Scrum_Team__c = 'Transformers';
            crm1.Story_Title__c = 'CRM Process - Initiating queue/views for transfers to MHK';
            crm1.Release_Name__c = '10/23/2020';
            insert crm1;
            Case caseRecord = [Select id, status from case limit 1];
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(caseRecord);
            ApexPages.currentPage().getParameters().put('CaseId', caseRecord.id); 
            system.assertNotEquals(oStdController ,null,'Value not matched'); 
             
            HUMTemplateBaseHelper objHelper = new HUMTemplateBaseHelper();
            system.assertNotEquals(objHelper ,null,'Value not matched'); 

            HUMMedicarePartDRedeterProcessController objExpApp = New HUMMedicarePartDRedeterProcessController(oStdController );            
            HUMMedicarePartDRedeterProcessController.checkAutoRouteCriteria(new list<Case>{caseRecord});
            system.assertNotEquals(objExpApp ,null,'Value not matched'); 
             CRMFunctionality_ONOFF_Switch__c crm2 = new CRMFunctionality_ONOFF_Switch__c();
             crm2.IsON__c =false;
            crm2.Name = '1393270';
            crm2.Scrum_Team__c = 'Transformers';
            crm2.Story_Title__c = 'CRM Process - Initiating queue/views for transfers to MHK';
            crm2.Release_Name__c = '10/23/2020';
            insert crm2;
            HUMMedicarePartDRedeterProcessController.checkAutoRouteCriteria(new list<Case>{caseRecord});
            Test.stopTest();
        }
    }    
    private static testMethod void testMedicaidPARNotAcceptingProcess()
    {
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test ProessUser1'];
        System.assertEquals(testUser.LastName,'test ProessUser1');
  
        System.runAs(testUser)
        {  
            Test.startTest() ;
            Case caseRecord = [Select id, status from case limit 1];
            Template__c tem1 = new Template__c();
            tem1.Unique_Template_Name__c = 'deeef952-9f63-49ce-97d0-c7eb3204b519';
            tem1.Description__c='Medicaid PAR Provider Not Accepting';
            insert tem1;
            
            Template_Field__c temfield1 = new Template_Field__c();
            temfield1.Name = 'Provider Name';
            temfield1.Template__c = tem1.id;
            
            insert temfield1;
                
                
            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;
            
            Template_Submission__c submission11 = new Template_Submission__c();
            submission11.Template__c = tem1.id;
            submission11.Submission_Owner__c = tOnwer11.id;
            insert submission11;
            
            Template_Submission_Data__c tObj11 = new Template_Submission_Data__c();
            tObj11.Template_Field__c = temfield1.Id;
            tObj11.Template_Submission__c = submission11.id;
            tObj11.Value__c = 'Yes';
            insert tObj11;
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(caseRecord);
            ApexPages.currentPage().getParameters().put('CaseId', caseRecord.id); 
            system.assertNotEquals(oStdController ,null,'Value not matched'); 
             
            HUMTemplateBaseHelper objHelper = new HUMTemplateBaseHelper();
            system.assertNotEquals(objHelper ,null,'Value not matched'); 

            MedicaidPARProviderNotAccepting_C_HUM objPar = New MedicaidPARProviderNotAccepting_C_HUM(oStdController);            
            
            system.assertNotEquals(objPar ,null,'Value not matched'); 
           
            Test.stopTest();
        }
    }
    
    private static testMethod void testDisenrollmenttempletver01_C_HUM()
    {
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test ProessUser1'];
        System.assertEquals(testUser.LastName,'test ProessUser1');
  
        System.runAs(testUser)
        {  
            Test.startTest() ;
            HUMConstants__c crm1 = new HUMConstants__c();
            crm1.IntegerValue__c =1;
            crm1.Name = 'HUMConstantOne';
            crm1.StringValue__c ='Test';
            insert crm1;
            
            Case caseRecord = [Select id, status from case limit 1];
            Template__c tem1 = new Template__c();
            tem1.Unique_Template_Name__c = '43f2875b-2b26-457e-aa62-24c2862796e7';
            tem1.Description__c='Medicare CE and Sp Election DE Process';
            insert tem1;
            
            Template_Field__c temfield1 = new Template_Field__c();
            temfield1.Name = 'Provider Name';
            temfield1.Template__c = tem1.id;
            
            insert temfield1;
                
                
            Template_Submission_Owner__c tOnwer11 = new Template_Submission_Owner__c();
            tOnwer11.Object_Owner_ID__c=string.valueOf(caseRecord.id);
            tOnwer11.Object_Owner_Type__c='Case';
            insert tOnwer11;
            
            Template_Submission__c submission11 = new Template_Submission__c();
            submission11.Template__c = tem1.id;
            submission11.Submission_Owner__c = tOnwer11.id;
            insert submission11;
            
            Template_Submission_Data__c tObj11 = new Template_Submission_Data__c();
            tObj11.Template_Field__c = temfield1.Id;
            tObj11.Template_Submission__c = submission11.id;
            tObj11.Value__c = 'Yes';
            insert tObj11;
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(caseRecord);
 		    ApexPages.currentPage().getParameters().put('CaseId', caseRecord.id); 
            system.assertNotEquals(oStdController ,null,'Value not matched'); 
           
            HUMTemplateBaseHelper objHelper = new HUMTemplateBaseHelper();
            MedicareDisenrollment_C_HUM objPar = New MedicareDisenrollment_C_HUM(oStdController);            
            system.assertNotEquals(objPar ,null,'Value not matched'); 
            
            Test.stopTest();
        }
    }
}