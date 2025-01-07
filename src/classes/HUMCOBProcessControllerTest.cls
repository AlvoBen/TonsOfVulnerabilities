/***************************************************************************************************************************************************************************
Apex Class Name  : HUMCOBProcessControllerTest
Version          : 1.0
Created Date     : Aug 04 2014
Function         : Test Class for HUMCOBProcessController
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name        Code Review                Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* krishna Tadikonda      17353                   08/04/2014                   Original Version
* Vishal Verma           17353                   08/18/2014                   Removed testMethod from testHUMCOBProcessController as this method is used for creating  data
* Pradeep Raju           17718                   09/04/2014                   Added a webservicecall to get cleartext values
* Pradeep Raju           17929                   09/15/2014                   Validation to make sure that future date is not entered for date of birth
* Krishna Tadikonda      18878                   10/30/2014                   Implemented CR-51 to show the subscriber Policy Members, if dependent is selected.
* Asha Deepika Koyyana   19409                   11/20/2014                   To implement Req-70318 added displayOtherCarrier method.      
* Santhi Mandava         20576                   01/28/2015                   Code Modified to fix test class failure Defect # 164139   
* Santhi Mandava                                 03/12/2019                   Removed "DpaaSWebServiceMockImpl" usage.
* Jasmeen Shangari                               04/26/2021                   Checkmarx fixes Azure ID - 2163309
****************************************************************************************************************************************************************************/

@isTest
private class HUMCOBProcessControllerTest
{
    static Case oCase;
    static List<Tabular_Question__c> lstTabularQuestions;
    /*
    * Method name : testSaveandCancel
    * Description : This method is used to test Save and Cancel methods of HUMCOBProcessController .
    * Return Type : Void
    * Parameter   : NA 
    */
    
    private static testMethod void testSaveandCancel()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        insert new HUMConstants__c(Name='HUMLISTSIZEZERO', IntegerValue__c = 0);
        System.assert(u.id != null);
        system.runas(u)
        {
            testHUMCOBProcessController();
            ApexPages.currentPage().getParameters().put('caseId',oCase.Id);
            ApexPages.currentPage().getParameters().put('ProcessType','COB');    
            ApexPages.currentPage().getParameters().put('tabId','345');    			
            Test.StartTest(); 
            HUMCOBProcessController objCOB = new HUMCOBProcessController();
            HUMPersonServiceResponseHelper oCOBData = new HUMPersonServiceResponseHelper();
            oCOBData.Member = 'test account';
            oCOBData.Relationship = 'Subscriber';
            oCOBData.OICarriername = 'Test Carrier';
            oCOBData.OIEffectiveDate = '01/02/2020';
            oCOBData.OIEndDate = '12/31/2020';
            oCOBData.OIPrimary = 'Y';       
            oCOBData.OILastVerifieddate = '07/01/2020';
            oCOBData.OIMedicalLastVerifieddate = '12/31/2021';
            oCOBData.OIDentalLastVerifieddate = '12/31/2021';
            oCOBData.memberRelationship = '00';
            oCOBData.memberFirstName = 'TestFN';
            oCOBData.memberLastName = 'TestLN';         
            
            objCOB.mapResponseWrapper.put('1', oCOBData);			
            objCOB.boolIsServiceRequired = false;
            objCOB.personResponseMethod();
            objCOB.boolIsServiceRequired = true; 
            objCOB.personResponseMethod();
            objCOB.iRowNumAddOI = 1;
            objCOB.sColumnName = 'Add OI'; 
            objCOB.prepareAddOITable();
            objCOB.iRowNumAddOI = 1;
            objCOB.sColumnName = 'Remove OI';
            objCOB.prepareAddOITable();
            system.assertequals(objCOB.boolDisplayQuestions , true);
            objCOB.iRowNumRemoveOI = 1;
            objCOB.reCalculateRemoveOIDisplay();
            objCOB.displayOtherCarrier();
            System.assert(true,objCOB.displayOtherCarrier()!= NULL);
			
            List<Error_Log__c> error = objCOB.lstLogErrors;            
            List<Error_Log__c> lstLogErrors = new List<Error_Log__c>();
            Error_Log__c errorLog = new Error_Log__c(Error_Message__c = 'Time Out', class_name__c = 'COB Process', Method_Name__c = 'Save', Severity__c = 'Log',Error_Code__c = 'Customize Error');
            lstLogErrors.add(errorLog);
            objCOB.lstLogErrors = lstLogErrors;                        
            List<Error_Log__c> error1 = objCOB.lstLogErrors;			
            
            for(HUMCaseActionQuestionHelper s: objCOB.lstWrapperQuestions)
            {
              s.sSelectedDropDownOption = 'Yes';
              s.questionrec.display_text__c = 'test text';
              s.questionrec.display_text_area__c = 'text area test';
              s.questionrec.display_date__c = system.today()-1;
              s.sValue = 'test';              
            }
            objCOB.recalculatePageQuestions();
            for (Integer row_num: objCoB.mapSequenceResponses.keySet()) 
            {
                for (HUMCaseTemplateHelper responseWrapper: objCOB.mapSequenceResponses.get(row_num)) 
                {
                        if (responseWrapper.sDataType == Label.HUMCOBDropDownType && responseWrapper.sSelectedPicklistValue == Label.HUMCOBNoneSelectOption)  responseWrapper.sSelectedPicklistValue = 'Cigna';
                        if (responseWrapper.sDataType == Label.HUMCOBDateQuestionType && responseWrapper.responseRec.Date_Response__c == null)  responseWrapper.responseRec.Date_Response__c = system.today()-1;                        
                }
            }
            HUMCaseActionQuestionHelper wrapQues = new HUMCaseActionQuestionHelper();
            objCOB.getSubscriberNames(wrapQues);
            objCOB.saveAction(); 
            objCOB.getMapSequenceResponsesSize();
            objCOB.getRowDispTableMapSize();
            Tabular_Answer_Response__c resp1 = new Tabular_Answer_Response__c();
            resp1.Row_Number__c = 1;
            Tabular_Answer_Response__c resp2 = new Tabular_Answer_Response__c();
            resp1.Row_Number__c = 2;
            Tabular_Answer_Response__c resp3 = new Tabular_Answer_Response__c();
            resp1.Row_Number__c = 2;
            list<Tabular_Answer_Response__c> lstAnswerResp = new list<Tabular_Answer_Response__c>();
            lstAnswerResp.add(resp1);
            lstAnswerResp.add(resp2);
            insert lstAnswerResp;
            objCOB.rowdispTableMap.put(1,lstAnswerResp);
            list<integer> lstRows = objCOB.addRowonDisplayTable(lstAnswerResp);
            system.assert(lstRows.size() > 0);
			list<integer> lstRows1 = objCOB.addRowonDisplayTable(lstAnswerResp);			
            map<Integer, list< HUMCaseTemplateHelper >> mapReturnWrapper = objCOB.doPrepareEmptyTable(lstTabularQuestions);
            system.assert(mapReturnWrapper.size() > 0);
             ApexPages.currentPage().getParameters().put('caseId',null);
            ApexPages.currentPage().getParameters().put('ProcessType','COB');
            HUMCOBProcessController objCOB3 = new HUMCOBProcessController();
            objCOB3.rowDispTableMap = null;
            system.assert(objCOB3.getRowDispTableMapSize() == 0);
            Test.StopTest();
          }
    }
    /*
    * Method name : testRecalculateQuestions
    * Description : This method is used to test RecalculateQuestions methods of HUMCOBProcessController .
    * Return Type : Void
    * Parameter   : NA 
    */    
    private static testMethod void testRecalculateQuestions()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        insert new HUMConstants__c(Name='HUMLISTSIZEZERO', IntegerValue__c = 0);
        System.assert(u.id != null);
        system.runas(u)
        { 
            testHUMCOBProcessController();
            ApexPages.currentPage().getParameters().put('caseId',oCase.Id);
            ApexPages.currentPage().getParameters().put('ProcessType','COB');
			ApexPages.currentPage().getParameters().put('tabId','345'); 
            Test.StartTest();
            HUMCOBProcessController objCOB1 = new HUMCOBProcessController();
            for(HUMCaseActionQuestionHelper rec: objCOB1.lstWrapperQuestions)
            {
                if(rec.questionrec.question_Label__c == 'Subscriber covered under external policy?') rec.sSelectedDropDownOption = 'Yes';
            }
            objCOB1.reCalculatePageQuestions();
            for (Integer row_num: objCoB1.mapSequenceResponses.keySet()) 
            {
                for (HUMCaseTemplateHelper responseWrapper: objCOB1.mapSequenceResponses.get(row_num)) 
                {
                        if (responseWrapper.sDataType == Label.HUMCOBDropDownType && responseWrapper.sSelectedPicklistValue == Label.HUMCOBNoneSelectOption)  responseWrapper.sSelectedPicklistValue = 'Cigna';
                        if (responseWrapper.sDataType == Label.HUMCOBDateQuestionType && responseWrapper.responseRec.Date_Response__c == null)  responseWrapper.responseRec.Date_Response__c = system.today()-1;                        
                }
            }
			objCOB1.sTabId = null;   			
            objCOB1.saveAction();
            system.assertequals(objCOB1.sLoadView , Label.HUMCOBView);
            PageReference cancelRedirect = objCOB1.cancel();
            System.assert(ApexPages.currentPage().getParameters().get('caseId') == oCase.Id);
            Test.StopTest();
        }
    }
    

   /*
    * Method name : testDateConvert
    * Description : This method is used to test DateConvert methods of HUMCOBProcessController .
    * Return Type : Void
    * Parameter   : NA 
    */
    private static testMethod void testDateConvert()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        insert new HUMConstants__c(Name='HUMLISTSIZEZERO', IntegerValue__c = 0);
        System.assert(u.id != null);
        system.runas(u)
        {
            testHUMCOBProcessController();
            ApexPages.currentPage().getParameters().put('caseId',oCase.Id);
            ApexPages.currentPage().getParameters().put('ProcessType','COB');
			ApexPages.currentPage().getParameters().put('tabId','345'); 
            Test.StartTest();
            HUMCOBProcessController objCOB2 = new HUMCOBProcessController();
            for (HUMCaseActionQuestionHelper question: objCOB2.lstWrapperQuestions) 
            {
                if (objCOB2.boolDisplayQuestions && question.boolDisplayQuestion) 
                {
                    question.sSelectedDropDownOption = 'Yes';
                    question.questionRec.Display_Date__c = system.today();
                    question.questionRec.Display_text__c = 'test account';
                    question.questionRec.Display_text_area__c = 'test long text';
                    question.boolDisplayQuestion = true;
                    if(question.questionRec.Question_Label__c == 'Other Insurance Policy subscriber name') {question.sSelectedDropDownOption = 'test account';}
                    if(question.questionRec.Question_Type__c == 'Date') {question.questionRec.Display_text__c = '01/01/2001';}
                }
            }
            for (HUMCaseActionQuestionHelper question: objCOB2.lstWrapperQuestions) 
            {
                if (objCOB2.boolDisplayQuestions && question.boolDisplayQuestion) 
                {
                    if(question.questionRec.Question_Type__c == 'Date')
                    {
                        question.questionRec.Display_text__c = '01012001';
                        break;
                    }
                }
            }       
            Date convDate = HUMProcessesHelper.convertStringtoDate('01012001');
            System.assertnotequals(convDate , null);
            Date convDate2 = HUMProcessesHelper.convertStringtoDate('01/01/2001');
            System.assertnotequals(convDate2 , null);
            Date convDate3 = HUMProcessesHelper.convertStringtoDate('01-01-2001');
            System.assertequals(convDate3 , null);
            Test.StopTest();
        }
    }
    
     /*
    * Method name : testDisplayData
    * Description : This method is used to test loading saved COB.
    * Return Type : Void
    * Parameter   : NA 
    */
    private static testMethod void testDisplayData()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        insert new HUMConstants__c(Name='HUMLISTSIZEZERO', IntegerValue__c = 0);
        System.assert(u.id != null);
        system.runas(u)
        {
            testHUMCOBProcessController();
            ApexPages.currentPage().getParameters().put('caseId',oCase.Id);
            ApexPages.currentPage().getParameters().put('ProcessType','COB');
			ApexPages.currentPage().getParameters().put('tabId','345'); 
            Test.StartTest();
            HUMCOBProcessController objCOB2 = new HUMCOBProcessController();
            objCOB2.initLoadEdit();
            for (HUMCaseActionQuestionHelper question: objCOB2.lstWrapperQuestions) 
            {
                if (objCOB2.boolDisplayQuestions && question.boolDisplayQuestion) 
                {
                    question.sSelectedDropDownOption = 'Yes';
                    question.questionRec.Display_Date__c = system.today();
                    question.questionRec.Display_text__c = 'test account';
                    question.questionRec.Display_text_area__c = 'test long text';
                    question.boolDisplayQuestion = true;
                    if(question.questionRec.Question_Label__c == 'Other Insurance Policy subscriber name') {question.sSelectedDropDownOption = 'test account';}
                    if(question.questionRec.Question_Type__c == 'Date') {question.questionRec.Display_text__c = '01/01/2001';}
                }
            }
            List<HUMCaseActionQuestionHelper> lstCsActionHelper = new List<HUMCaseActionQuestionHelper>();
            HUMCaseActionQuestionHelper csAction = new HUMCaseActionQuestionHelper();
            csAction.boolDisplayQuestion = true;
            csAction.sSelectedDropDownOption = null;
            lstCsActionHelper.add(csAction);
            objCOB2.lstWrapperQuestions = lstCsActionHelper;
            for (HUMCaseActionQuestionHelper question: objCOB2.lstWrapperQuestions) 
            {
                if (objCOB2.boolDisplayQuestions && question.boolDisplayQuestion) 
                {
                    if(question.questionRec.Question_Type__c == 'Date')
                    {
                        question.questionRec.Display_text__c = '01012001';
                        break;
                    }
                }
            }       
            Date convDate = HUMProcessesHelper.convertStringtoDate('01012001');
            System.assertnotequals(convDate , null);
            objCOB2.boolDisplayQuestions = true;
            for (Integer row_num: objCoB2.mapSequenceResponses.keySet()) 
            {
                for (HUMCaseTemplateHelper responseWrapper: objCOB2.mapSequenceResponses.get(row_num)) 
                {
                        if (responseWrapper.sDataType == Label.HUMCOBDropDownType && responseWrapper.sSelectedPicklistValue == Label.HUMCOBNoneSelectOption)  responseWrapper.sSelectedPicklistValue = 'Cigna';
                        if (responseWrapper.sDataType == Label.HUMCOBDateQuestionType && responseWrapper.responseRec.Date_Response__c == null)  responseWrapper.responseRec.Date_Response__c = system.today()-1;                        
                }
            }
            objCOB2.saveAction();
            System.assertNotEquals(objCOB2.bIsSuccess,true); 
            objCOB2.initLoadEdit();           
            Test.StopTest();
        }
    }
    
    /*
    * Method name : testHUMCOBProcessController
    * Description : This method is used to create test data.
    * Return Type : Void
    * Parameter   : NA 
    */
    Private Static void testHUMCOBProcessController()
    {
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.Name = 'test account';
        oAcc.Enterprise_ID__c = '123456';
        insert oAcc;
        System.assert(oAcc.Id != null);

        Policy__c oPol = HUMTestDataHelperTest.getPolicy();
        oPol.Name = 'test policy';
        oPol.Group_Name__c = oAcc.Id;
        insert oPol;
        System.assert(oPol.Id != null);

        Policy_Member__c oPolMem = HUMTestDataHelperTest.getPolicyMember();
        oPolMem.Member__c = oAcc.Id;
        oPolMem.Policy__c = oPol.Id;
        insert oPolMem;
        System.assert(oPolMem.Id != null);

        Policy_Member__c oSubscriber = HUMTestDataHelperTest.getPolicyMember();
        oSubscriber.Member__c = oAcc.Id;
        oSubscriber.policy__c = oPol.Id;
        oSubscriber.Subscriber__c = oPolMem.Id;
        insert oSubscriber;
        System.assert(oSubscriber.Id != null);

        oCase = HUMTestDataHelperTest.getCase();
        oCase.AccountId = oAcc.Id;
        oCase.Policy_Member__c = oPolMem.Id;
        oCase.Interacting_With__c = oAcc.Id;
        oCase.Interacting_With_Name__c = 'Test Name';
        insert oCase;
        System.assert(oCase.Id != null);

        Case_Action__c  oCaseAction = HUMTestDataHelperTest.getCaseAction('COB');
        insert oCaseAction;
        System.assert(oCaseAction.Id != null);
        
        List<HUMTemplateCOBColumns__c> lstCobColumns = new List<HUMTemplateCOBColumns__c>();
        HUMTemplateCOBColumns__c cobColumn = HUMTestDataHelperTest.getCOBColumn();
        cobColumn.Name = 'COB Table';
        cobColumn.Column_Titles__c = 'Member,Relationship,Remove OI,Add OI,Verified';
        lstCobColumns.add(cobColumn);

        HUMTemplateCOBColumns__c addOIColumn = HUMTestDataHelperTest.getCOBColumn();
        addOIColumn.Name = 'Add OI';
        addOIColumn.Column_Titles__c = 'Member,Relationship';
        lstCobColumns.add(addOIColumn);

        HUMTemplateCOBColumns__c removeOIColumn = HUMTestDataHelperTest.getCOBColumn();
        removeOIColumn.Name = 'Remove OI';
        removeOIColumn.Column_Titles__c = 'Member,Relationship,OI Should Never Have Been Listed';
        lstCobColumns.add(removeOIColumn);

        if(lstCOBColumns != null && lstCOBColumns.size() >0 ){insert lstCOBColumns; }

        for(HUMTemplateCOBColumns__c col : lstCOBColumns)
        {
            System.assert(col.Id != null);
        }

        lstTabularQuestions = new List<Tabular_Question__c>();
        Tabular_Question__c tabQues1 = HUMTestDataHelperTest.getTabularQuestion('TQ-0001', 'Text', 'Member', 1);
        lstTabularQuestions.add(tabQues1);
        Tabular_Question__c tabQues2 = HUMTestDataHelperTest.getTabularQuestion('TQ-0002', 'Text', 'Relationship', 2);
        lstTabularQuestions.add(tabQues2);
        Tabular_Question__c tabQues3 = HUMTestDataHelperTest.getTabularQuestion('TQ-0003', 'Checkbox', 'Remove OI', 3);
        lstTabularQuestions.add(tabQues3);
        Tabular_Question__c tabQues4 = HUMTestDataHelperTest.getTabularQuestion('TQ-0004', 'Checkbox', 'Add OI', 4);
        lstTabularQuestions.add(tabQues4);
        Tabular_Question__c tabQues5 = HUMTestDataHelperTest.getTabularQuestion('TQ-0004', 'Checkbox', 'Verified', 5);
        lstTabularQuestions.add(tabQues5);
        Tabular_Question__c tabQues6 = HUMTestDataHelperTest.getTabularQuestion('TQ-0004', 'Text', 'OI Should Never Have Been Listed', 6);
        lstTabularQuestions.add(tabQues6);
        Tabular_Question__c tabQues7 = HUMTestDataHelperTest.getTabularQuestion('TQ-0004', 'Drop down', 'OI Carrier Name', 7);
        tabQues7.Question_Values__c = 'Aetna; Blue Cross/ Blue Shield; Cigna; Guardian;Humana-CAS;Humana-MTV;Medical Mutual;'+
                'Medicare-Part A; Medicare-Part A & B; Other Carrier; Pacificare;Priority Health;Sisco;Unicare;'+
                'United Heathcare;United Medical Resources; Unknown;Wausou;WPS;YVAT;';
        lstTabularQuestions.add(tabQues7);
        Tabular_Question__c tabQues9 = HUMTestDataHelperTest.getTabularQuestion('TQ-0004', 'Date', 'OI Effective Date', 9);
        lstTabularQuestions.add(tabQues9);
        Tabular_Question__c tabQues10 = HUMTestDataHelperTest.getTabularQuestion('TQ-0004', 'Date', 'OI End Date', 10);
        lstTabularQuestions.add(tabQues10);
        Tabular_Question__c tabQues11 = HUMTestDataHelperTest.getTabularQuestion('TQ-0004', 'Text', 'OI Primary', 11);
        lstTabularQuestions.add(tabQues11);
        Tabular_Question__c tabQues12 = HUMTestDataHelperTest.getTabularQuestion('TQ-0004', 'Date', 'OI Medical Last Verified Date', 12);
        lstTabularQuestions.add(tabQues12);
        Tabular_Question__c tabQues13 = HUMTestDataHelperTest.getTabularQuestion('TQ-0004', 'Date', 'OI Dental Last Verified Date', 13);
        lstTabularQuestions.add(tabQues13);

        if(lstTabularQuestions != null && lstTabularQuestions.size() >0) {insert lstTabularQuestions;}

        Id cobQuestionRecordTypeId = Schema.SObjectType.Question__c.getRecordTypeInfosByName().get('COB').getRecordTypeId();
        Question__c  oQues1 = HUMTestDataHelperTest.getQuestion('Q-0001','Drop down','Subscriber covered under external policy?',1);
        oQues1.RecordTypeId = cobQuestionRecordTypeId;
        oQues1.Question_Values__c = 'Yes;No';
        insert oQues1;  
		
        List<Question__c >lstQuestions = new List<Question__c>();
        Question__c  oQues2 = HUMTestDataHelperTest.getQuestion('Q-0002','Drop down','Other Insurance Policy subscriber name',2);
        oQues2.Parent_Question__c = oQues1.Id;
        oQues2.Dependent_Value__c = 'Yes';
        oQues2.RecordTypeId = cobQuestionRecordTypeId;
        oQues2.Question_Values__c = 'needs to be loaded from parent';
        lstQuestions.add(oQues2);
		
        Question__c  oQues6 = HUMTestDataHelperTest.getQuestion('Q-0006','Text','Other Insurance Policy subscriber First name',6);
        oQues6.Parent_Question__c = oQues1.Id;
        oQues6.Dependent_Value__c = 'Yes';
        oQues6.RecordTypeId = cobQuestionRecordTypeId;
        oQues6.Question_Values__c = 'needs to be loaded from parent';
        lstQuestions.add(oQues6);  		

        Question__c  oQues3 = HUMTestDataHelperTest.getQuestion('Q-0003','Date','Indicate the effective date for Medicare Part A Coverage',3);
        oQues3.Parent_Question__c = oQues1.Id;
        oQues3.Dependent_Value__c = 'Yes';
        oQues3.RecordTypeId = cobQuestionRecordTypeId;
        lstQuestions.add(oQues3);

        Question__c  oQues4 = HUMTestDataHelperTest.getQuestion('Q-0004','Text','Relationship',4);
        oQues4.Parent_Question__c = oQues1.Id;
        oQues4.Dependent_Value__c = 'Yes';
        oQues4.RecordTypeId = cobQuestionRecordTypeId;
        lstQuestions.add(oQues4);

        Question__c  oQues5 = HUMTestDataHelperTest.getQuestion('Q-0005','Text Area','Other reasons',5);
        oQues5.Parent_Question__c = oQues1.Id;
        oQues5.Dependent_Value__c = 'Yes';
        oQues5.RecordTypeId = cobQuestionRecordTypeId;
        lstQuestions.add(oQues5);
        if(lstQuestions != null && lstQuestions.size() > 0) {insert lstQuestions;}
        system.assert(lstQuestions.size() > 0);
    }

}