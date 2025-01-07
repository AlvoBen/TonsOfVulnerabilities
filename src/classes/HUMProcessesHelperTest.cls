/****************************************************************************************
Apex Class Name  : HUMProcessesHelperTest
Version          : 1.0 
Created Date     : AUGUST 14 2014
Function         : Test class for HUMProcessHelper Apex class
Modification Log :
 *  Developer            Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 *  Sagar Tapadia        17840                 14/08/2014           Original Version 
 *  Vishal verma         17987                 20/09/2014           Made modifications as per Humana Standard
 *  Vishal Verma         19528                 11/25/2014           Updated since Member Card external id has been made mandatory on Member Id object    
 *  Asha Deepika Koyyana  20579                01/28/2015           Fixed test class failure 
 *  Santhi Mandava                             03/12/2019           Removed "DpaaSWebServiceMockImpl" usage.
 *******************************************************************************************/
@isTest
private class HUMProcessesHelperTest {

    /*
     * Method Name : generateQMSRequestTest
     * Description : Test method for Generating QMS Request and not callout
     * Return type : NA
     * Paramater   : NA
     */ 
    static testMethod void generateHelperTest() {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c='Market HO Quality Operations Compliance';
        insert u;
        System.runAs(u)
        {
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.FirstName = 'Test QMS';
            acc.LastName ='Account Last';
            acc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            acc.OwnerId = u.Id;
            acc.Birthdate__c=String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert acc;

            Policy__c policy = HUMTestDataHelperTest.getPolicy();
            policy.Name = 'Test Policy';
            policy.Group_Name__c = acc.Id;
            insert policy;

            Policy_Member__c polMem = HUMTestDataHelperTest.getPolicyMember();
            polMem.Member__c = acc.Id;
            polMem.Policy__c = policy.Id;
            insert polMem;

            Member_ID__c memId = new Member_ID__c();
            memId.Policy__c = policy.Id;
            memId.Policy_Member__c = polMem.Id;
            memId.Member_Card_External_ID__c = '1234';
            insert memId;

            Case c = HUMTestDataHelperTest.getCase();
            c.AccountId = acc.Id;
            c.Policy_Member__c=polMem.Id;
            c.OwnerId = u.Id;
            insert c;  
            
            Case_Action__c  oCaseAction = HUMTestDataHelperTest.getCaseAction('COB');
            insert oCaseAction;
            
            Case_Action_Association__c caaObj = new Case_Action_Association__c();
            caaObj.Case__c = c.Id;
            caaObj.Case_Action__c = oCaseAction.Id;
            insert caaObj;
            
            Id cobQuestionRecordTypeId = Schema.SObjectType.Question__c.getRecordTypeInfosByName().get('COB').getRecordTypeId();                  
            Question__c  oQues1 = HUMTestDataHelperTest.getQuestion('Q-0001',Label.HUMCOBDropDownType,'Subscriber covered under external policy?',1);
            oQues1.RecordTypeId = cobQuestionRecordTypeId;
            oQues1.Question_Values__c = 'Yes;No';
            insert oQues1;
            Question__c  oQues2 = HUMTestDataHelperTest.getQuestion('Q-0001','Text','Name',2);
            oQues2.RecordTypeId = cobQuestionRecordTypeId;
            oQues2.display_text__c = 'test';
            insert oQues2;
            Question__c  oQues3 = HUMTestDataHelperTest.getQuestion('Q-0001','Text Area','Subscriber',3);
            oQues3.RecordTypeId = cobQuestionRecordTypeId;
            oQues2.display_text_area__c = 'test';
            insert oQues3;
            Question__c  oQues4 = HUMTestDataHelperTest.getQuestion('Q-0001',Label.HUMCOBCheckboxType,'Member',4);
            oQues4.display_checkbox__c = true;
            oQues4.RecordTypeId = cobQuestionRecordTypeId;
            insert oQues4;
            Question__c  oQues5 = HUMTestDataHelperTest.getQuestion('Q-0001','Date','Process',5);
            oQues5.display_Date__c = system.today();
            oQues5.RecordTypeId = cobQuestionRecordTypeId;
            insert oQues5;
            HUMCaseActionQuestionHelper quesWrap1 = new HUMCaseActionQuestionHelper();
            quesWrap1.sSelectedDropDownOption = 'Yes';
            quesWrap1.questionrec = oQues1;
            HUMCaseActionQuestionHelper quesWrap2 = new HUMCaseActionQuestionHelper();
            quesWrap2.questionrec = oQues2;
            HUMCaseActionQuestionHelper quesWrap3 = new HUMCaseActionQuestionHelper();
            quesWrap3.questionrec = oQues3;
            HUMCaseActionQuestionHelper quesWrap4 = new HUMCaseActionQuestionHelper();
            quesWrap4.questionrec = oQues4;
            HUMCaseActionQuestionHelper quesWrap5 = new HUMCaseActionQuestionHelper();
            quesWrap5.questionrec = oQues5;
            list<HUMCaseActionQuestionHelper> lstQuesWrpr = new list<HUMCaseActionQuestionHelper>();
            lstQuesWrpr.add(quesWrap1);
            lstQuesWrpr.add(quesWrap2);
            lstQuesWrpr.add(quesWrap3);
            lstQuesWrpr.add(quesWrap4);
            lstQuesWrpr.add(quesWrap5);
            Case_Action_Response__c caseActRec1 = new Case_Action_Response__c();
            caseActRec1.process_question_response__c = 'Yes';
            insert caseActRec1;
            Case_Action_Response__c caseActRec2 = new Case_Action_Response__c();
            caseActRec2.process_question_response__c = 'true';
            insert caseActRec2;
            Case_Action_Response__c caseActRec3 = new Case_Action_Response__c();
            caseActRec3.process_question_response__c = 'Yes';
            insert caseActRec3;
            Case_Action_Response__c caseActRec4 = new Case_Action_Response__c();
            caseActRec4.process_question_response__c = 'Yes';
            insert caseActRec4;
            Test.startTest();
            list<tabular_Answer_Response__c> lstRes = HUMProcessesHelper.getTabularAnswerResponses(caaObj.Id);
            system.assert(lstRes.size() == 0);
            list<case_Action_Response__c> lstRes2 = HUMProcessesHelper.getCaseActionResponses(caaObj.Id, 'COB');
            system.assert(lstRes2.size() == 0);
            Map<String,Case_Action_Response__c> mapCase = new Map<String,Case_Action_Response__c>();
            mapCase.put(oQues1.question_label__c,caseActRec1);
            list<case_Action_Response__c> lstResp = HUMProcessesHelper.assignQuestionResponse(lstQuesWrpr,mapCase, caaobj);
            system.assert(lstResp.size() > 0);
            Question__c ques = HUMProcessesHelper.assignDisplayText(mapCase,oQues1);
            system.assertEquals(ques.display_checkbox__c,false);           
            list<selectoption> lstSelectOptions =  HUMProcessesHelper.splitPicklistValues('a;b');
            system.assert(lstSelectOptions.size() == 2);  
            Test.stopTest();    
        }
    }

    /*
     * Method Name : generatePersonServiceRespTest
     * Description : Test method for Generating Person Service Request Response
     * Return type : NA
     * Paramater   : NA
     */ 
    static testMethod void callConvertStringTest()
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c='Market HO Quality Operations Compliance';
        insert u;
        System.runAs(u)
        {
            Test.startTest();
                Date dRes1 = HUMProcessesHelper.convertStringtoDate('01012001');
                system.assertNotEquals(dRes1,null);
                Date dRes2 = HUMProcessesHelper.convertStringtoDate('01/01/2001');
                system.assertNotEquals(dRes2,null);
                Date dRes3 = HUMProcessesHelper.convertStringtoDate('01-01-2001');
                system.assertEquals(dRes3,null);
            Test.stopTest();
        }
    }
    
    /*
     * Method Name : generatePersonServiceRespTest
     * Description : Test method for Generating Person Service Request Response
     * Return type : NA
     * Paramater   : NA
     */ 
    static testMethod void callprocessQuestionResponseTest()
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c='Market HO Quality Operations Compliance';
        insert u;
        System.runAs(u)
        {
            Id cobQuestionRecordTypeId = Schema.SObjectType.Question__c.getRecordTypeInfosByName().get('COB').getRecordTypeId();                  
            Question__c  oQues1 = HUMTestDataHelperTest.getQuestion('Q-0001',Label.HUMCOBDropDownType,'Subscriber covered under external policy?',1);
            oQues1.RecordTypeId = cobQuestionRecordTypeId;
            oQues1.Question_Values__c = 'Yes;No';
            insert oQues1;
            Question__c  oQues2 = HUMTestDataHelperTest.getQuestion('Q-0001','Text','Name',2);
            oQues2.RecordTypeId = cobQuestionRecordTypeId;
            oQues2.display_text__c = 'test';
            insert oQues2;
            Question__c  oQues3 = HUMTestDataHelperTest.getQuestion('Q-0001','Text Area','Subscriber',3);
            oQues3.RecordTypeId = cobQuestionRecordTypeId;
            oQues2.display_text_area__c = 'test';
            insert oQues3;
            Question__c  oQues4 = HUMTestDataHelperTest.getQuestion('Q-0001',Label.HUMCOBCheckboxType,'Member',4);
            oQues4.display_checkbox__c = true;
            oQues4.RecordTypeId = cobQuestionRecordTypeId;
            insert oQues4;
            Question__c  oQues5 = HUMTestDataHelperTest.getQuestion('Q-0001','Date','Process',5);
            oQues5.display_Date__c = system.today();
            oQues5.RecordTypeId = cobQuestionRecordTypeId;
            insert oQues5;
            HUMCaseActionQuestionHelper quesWrap1 = new HUMCaseActionQuestionHelper();
            quesWrap1.sSelectedDropDownOption = 'Yes';
            quesWrap1.questionrec = oQues1;
            HUMCaseActionQuestionHelper quesWrap2 = new HUMCaseActionQuestionHelper();
            quesWrap2.questionrec = oQues2;
            HUMCaseActionQuestionHelper quesWrap3 = new HUMCaseActionQuestionHelper();
            quesWrap3.questionrec = oQues3;
            HUMCaseActionQuestionHelper quesWrap4 = new HUMCaseActionQuestionHelper();
            quesWrap4.questionrec = oQues4;
            HUMCaseActionQuestionHelper quesWrap5 = new HUMCaseActionQuestionHelper();
            quesWrap5.questionrec = oQues5;
            list<HUMCaseActionQuestionHelper> lstQuesWrpr = new list<HUMCaseActionQuestionHelper>();
            lstQuesWrpr.add(quesWrap1);
            lstQuesWrpr.add(quesWrap2);
            lstQuesWrpr.add(quesWrap3);
            lstQuesWrpr.add(quesWrap4);
            lstQuesWrpr.add(quesWrap5);
        Test.startTest();
            string sRes = HUMProcessesHelper.processQuestionResponse(quesWrap1);
            System.assertNotEquals(sRes,null);
            string sRes2 = HUMProcessesHelper.processQuestionResponse(quesWrap2);
            System.assertNotEquals(sRes2,null);
            string sRes3 = HUMProcessesHelper.processQuestionResponse(quesWrap3);
            System.assertEquals(sRes3,null); 
            string sRes4 = HUMProcessesHelper.processQuestionResponse(quesWrap4);
            System.assertNotEquals(sRes4,null);
            string sRes5 = HUMProcessesHelper.processQuestionResponse(quesWrap5); 
            System.assertNotEquals(sRes5,null);  
        Test.stopTest();
        }
    }
    
    /*
     * Method Name : generatePersonServiceRespTest
     * Description : Test method for Generating Person Service Request Response
     * Return type : NA
     * Paramater   : NA
     */ 
    static testMethod void callValidateTaxTest()
    {
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        u.Current_Queue__c='Market HO Quality Operations Compliance';
        insert u;
        System.runAs(u)
        {
            Test.startTest();
               boolean boolResd1 = HUMProcessesHelper.validateTaxId('123456789');  
               system.assertEquals(boolResd1,true);
               boolean boolResd2 =  HUMProcessesHelper.validateTaxId('12345-789');  
               system.assertEquals(boolResd2,false);
               boolean boolResd3 =  HUMProcessesHelper.validateTaxId('123456-789'); 
               system.assertEquals(boolResd3,false); 
            Test.stopTest();
        }
    }
}