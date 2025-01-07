/****************************************************************************
Apex Class Name  : HUMQAATemplateControllerTest
Version          : 1.0
Created On       : August 12 2014
Function         : Test class to HUMQAATemplateController Apex class

Modification Log :
 * Developer Name          Code Review              Date                   Description
 * ------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma               17353               12/08/2014             Original Version
 *Krishna Tadikonda           17353               08/20/2014             Incorporated code review comments. 
 *Pradeep Raju                18067               09/22/2014             Page displayable error message is modified by modifying the custom label and referring the same in line#331 on HUMQAATemplateController
 *Shreya Choodamani           18878               10/30/2014             Updated the SOQL in the function prepareOnScreenQuestionsDisplay to fix the issue related to defect#: 159662.0001 
 *Asha Deepika Koyyana        20572               01/28/2015             Fixed test class failure for Defect#164139 
 *Praveen Kumar Parimi                            06/17/2015             Fixed test class failure and also included the custom settings data for Encryption         
 *Santhi Mandava              87365               04/26/2017             Changed testclass to support class changes.
 *Praveen Kumar Parimi        262732              07/26/2018             REQ - 367051 aka CA Incident #7521926  Fix for Service Addresses not showing up when conducting a search in the QAA template.
 *Santhi Mandava                                  03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
 * ------------------------------------------------------------------------------------------------------------------------------*/
@isTest
private class HUMQAATemplateControllerTest
{
    Static Case caseObj ;
    Static Case_Action_Association__c caseActionAss ;
    Static Case_Action__c caseAction;
    Static Case_Action_Response__c caseActionResponse ;
    
     @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        system.runAs(oUser)
        {
            apexpages.currentpage().getparameters().put('ProcessType','QAA');
            
            caseAction = new Case_Action__c();
            caseAction.Name = 'QAA';
            caseAction.Active__c = true;
            insert caseAction;
            System.assertEquals('QAA', caseAction.Name);
            
            
            caseObj = HUMTestDataHelperTest.getCase();
            insert caseObj;
            System.assertNotEquals(Null, caseObj.Id);
            
            Question__c objParent;
            List<Question__c> lstQuestions = new List<Question__c>();
            
            objParent = new Question__c(Active__c = true, Question_label__c = 'Is the Member willing to provide their QAA Complaint details verbally?', Question_Type__c = 'Drop down' ,
                                        Sequence_Number__c= 1, Actions__c = caseAction.Id,Question_Values__c = 'Yes;No;',Display_text__c='Yes');
            insert objParent;
            lstQuestions.Add(new Question__c(Parent_Question__c= objParent.Id,Active__c = true, Question_label__c = 'Provider/Facility/Group NPI', Question_Type__c = 'Text' ,
                                        Sequence_Number__c= 2,Dependent_Value__c='Yes', Actions__c = caseAction.Id));
            lstQuestions.Add(new Question__c(Parent_Question__c= objParent.Id,Active__c = true, Question_label__c = 'Provider/Facility/Group Tax ID', Question_Type__c = 'Text' ,
                                        Sequence_Number__c= 3,Dependent_Value__c='Yes', Actions__c = caseAction.Id));
            lstQuestions.Add(new Question__c(Parent_Question__c= objParent.Id,Active__c = true, Question_label__c = 'Is this for a Provider or a Facility/Group?', Question_Type__c = 'Drop down' ,
            Sequence_Number__c= 4,Dependent_Value__c='Yes', Actions__c = caseAction.Id,Question_Values__c = 'Provider;Facility/Group'));
            lstQuestions.Add(new Question__c(Parent_Question__c= objParent.Id,Active__c = true, Question_label__c = 'Facility/ Group Name', Question_Type__c = 'Text' ,
            Sequence_Number__c= 5,Dependent_Value__c='Yes', Actions__c = caseAction.Id));
            lstQuestions.Add(new Question__c(Parent_Question__c= objParent.Id,Active__c = true, Question_label__c = 'Provider First Name', Question_Type__c = 'Text' ,
            Sequence_Number__c= 6,Dependent_Value__c='Yes', Actions__c = caseAction.Id));
            lstQuestions.Add(new Question__c(Parent_Question__c= objParent.Id,Active__c = true, Question_label__c = 'Provider Last Name', Question_Type__c = 'Text' ,
            Sequence_Number__c= 7,Dependent_Value__c='Yes', Actions__c = caseAction.Id));
            lstQuestions.Add(new Question__c(Parent_Question__c= objParent.Id,Active__c = true, Question_label__c = 'Provider State', Question_Type__c = 'Drop down' ,
            Sequence_Number__c= 9,Dependent_Value__c='Yes', Actions__c = caseAction.Id,Question_Values__c = 'AL - Alabama;AK - Alaska;AZ - Arizona;AR - Arkansas;CA - California;CO - Colorado;CT - Connecticut;TX - Texas;WA - Washington;'));
            lstQuestions.Add(new Question__c(Parent_Question__c= objParent.Id,Active__c = true, Question_label__c = 'Provider Zip Code', Question_Type__c = 'Text' ,
            Sequence_Number__c= 10,Dependent_Value__c='Yes', Actions__c = caseAction.Id)); 
            lstQuestions.Add(new Question__c(Parent_Question__c= objParent.Id,Active__c = true, Question_label__c = 'Provider Street Address', Question_Type__c = 'Text' ,
            Sequence_Number__c= 11,Dependent_Value__c='Yes', Actions__c = caseAction.Id)); 
            lstQuestions.Add(new Question__c(Parent_Question__c= objParent.Id,Active__c = true, Question_label__c = 'Provider City', Question_Type__c = 'Text' ,
            Sequence_Number__c= 12,Dependent_Value__c='Yes', Actions__c = caseAction.Id));
            lstQuestions.Add(new Question__c(Parent_Question__c= objParent.Id,Active__c = true, Question_label__c = 'Who is submitting the complaint?', Question_Type__c = 'Text' ,
            Sequence_Number__c= 13,Dependent_Value__c='Yes', Actions__c = caseAction.Id)); 
            lstQuestions.Add(new Question__c(Parent_Question__c = objParent.Id,Active__c = true, Question_label__c = 'Date of the incident/service', Question_Type__c = 'Date' ,
            Sequence_Number__c= 14,Dependent_Value__c='Yes', Actions__c = caseAction.Id));   
            
            insert lstQuestions;
            System.assertNotEquals(Null, lstQuestions[4].Id);
            
            List<HUMEncryptedFields__c>lstCS4 = new List<HUMEncryptedFields__c>();
            lstCS4.add(new HUMEncryptedFields__c(Name='Test Response',ObjectName__c='Case_Action_Response__c', FieldName__c='Process_Question_Response__c', QuestionExclude__c = 'Employee/Subscriber Name', EncryptionScheme__c = 'Stateless_AES_Encryption_without_Search'));
            insert lstCS4;
            system.assertequals(false,lstCS4.isEmpty());      
            insert new HUMConstants__c [] {new HUMConstants__c( Name = 'HUMCaseActionResponse', StringValue__c = 'Case_Action_Response__c', IntegerValue__c = 0),
            new HUMConstants__c( Name = 'HUMProcessQuestionResponse', StringValue__c = 'Process_Question_Response__c', IntegerValue__c = 0),                            
            new HUMConstants__c(Name = 'HUMStatelessAESEncryptionwithoutSearch', StringValue__c = 'Stateless_AES_Encryption_without_Search', IntegerValue__c = 0),
            new HUMConstants__c(Name = 'HUMStatelessPhoneNumberEncryption', StringValue__c = 'Stateless_Phone_Number_Encryption', IntegerValue__c = 0),
            new HUMConstants__c(Name = 'ZERO', StringValue__c = '0', IntegerValue__c = 0),
            new HUMConstants__c(Name = 'HUMExclamation', StringValue__c = '!', IntegerValue__c = 0),
            new HUMConstants__c(Name = 'HUMDoubleStar', StringValue__c = '**', IntegerValue__c = 0),
            new HUMConstants__c(Name = 'AMP', StringValue__c = '&', IntegerValue__c = 0),
            new HUMConstants__c(Name = 'HUMValueOne', StringValue__c = '1', IntegerValue__c = 1),
            new HUMConstants__c(Name = 'HUMOpenBrace', StringValue__c = '(', IntegerValue__c = 0),
            new HUMConstants__c(Name = 'HUMAT', StringValue__c = '@', IntegerValue__c = 0),
            new HUMConstants__c(Name = 'HUMDash', StringValue__c = '-', IntegerValue__c = 0)
            };
        }
    }
    
     /*
    * Method Name : testFunctionality
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testFunctionality() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        Map<String,HUMCaseActionQUestionHelper> mapQAAQuestions = new Map<String,HUMCaseActionQUestionHelper>();
        
        System.runAs(oUser)
        {
            List<Case> lstCases = [Select Id from Case];
            apexpages.currentpage().getparameters().put('CaseId',lstCases[0].id);
            apexpages.currentpage().getparameters().put('ProcessType','QAA');
            HUMQAATemplateController objQAAProcess = new HUMQAATemplateController();  
            
            //Selecting Yes option for dependant question.
            List<HUMCaseActionQuestionHelper> lstValues = objQAAProcess.lstWrapperQuestions;
            For(HUMCaseActionQuestionHelper obj : lstValues )
            {
                if(obj.QuestionRec.Question_Label__c == 'Is the Member willing to provide their QAA Complaint details verbally?')
                {
                    obj.sSelectedDropDownOption = 'Yes';
                    obj.boolDisplayQuestion = true;
                }
                mapQAAQuestions.put(obj.QuestionRec.Question_Label__c,obj );
            }
            objQAAProcess.lstWrapperQuestions = lstValues ;
            
            objQAAProcess.reCalculatePageQuestions();
            System.assertEquals(true, objQAAProcess.mapProviderQuestions.get('Provider/Facility/Group NPI').boolDisplayQuestion );
            
            //Saving questions without entering data.
            objQAAProcess.saveAction();
            System.assertEquals(true, objQAAProcess.isErrorExist);
            
            mapQAAQuestions.get('Is this for a Provider or a Facility/Group?').sSelectedDropDownOption = 'Facility/Group' ;
            objQAAProcess.setProviderFieldsVisibility();
            System.assertEquals(true, objQAAProcess.mapProviderQuestions.get('Facility/ Group Name').boolDisplayQuestion );
            System.assertEquals(false, objQAAProcess.mapProviderQuestions.get('Provider First Name').boolDisplayQuestion );
            System.assertEquals(false, objQAAProcess.mapProviderQuestions.get('Provider Last Name').boolDisplayQuestion );
            
            objQAAProcess.mapProviderQuestions.get('Facility/ Group Name').questionRec.Display_text__c = 'Norton Hospital';
            objQAAProcess.mapProviderQuestions.get('Provider/Facility/Group Tax ID').questionRec.Display_text__c = '270671858';
            objQAAProcess.mapProviderQuestions.get('Provider/Facility/Group NPI').questionRec.Display_text__c = '1234567890';
            objQAAProcess.mapProviderQuestions.get('Provider State').sSelectedDropDownOption = 'TX - Texas';
            mapQAAQuestions.get('Date of the incident/service').questionRec.Display_Text__c = '01/01/2018';
            
            objQAAProcess.searchProvider();
            HttpResponse res = new HttpResponse();
            res.setHeader('Content-type', 'application/json');
            res.setBody('{"SearchProviderResponse":{ "EnterpriseProviders":[ { "EnterpriseID":"12101444", "UniqueID":{ "Source":"CAS" }, "ProviderName":{ "Last":"CARROLL", "First":"JOHN", "Degree":"DDS" }, "DbaName":"CARROLL, JOHN DDS", "Classification":"Individual", "Demographics":{ "Addresses":[ { "AddressType":"Billing", "Line1":"1901 E MAIN ST", "Line2":"", "City":"LEAGUE CITY", "State":"TX", "Zip":"77573", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"2813326323", "Extension":"" } ] }, {"AddressType":"Service", "Line1":"111 Michigan Ave NW", "Line2":"", "City":"Washington", "State":"DC", "Zip":"200102978", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"2024765000", "Extension":"" } ]} ], "DateOfBirth":"", "Gender":"" }, "Identifiers":[ { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"270671858" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"270671858" } ] } ], "Specialties":[ { "Description":"Dentistry  [CAS]" } ], "SourceProviders":[ { "Source":"CAS", "ProviderID":"270671858 D" } ], "Taxonomy":[ { "Code":"122300000X" } ] } ] } }');
            res.setStatusCode(200);
            objQAAProcess.processResponse();
            objQAAProcess.parseServiceResponse(res);
            System.assertEquals(1,objQAAProcess.lstProviderAccounts.size());

            objQAAProcess.saveAction();
            System.assertEquals(true, objQAAProcess.isErrorExist);
            
            mapQAAQuestions.get('Date of the incident/service').questionRec.Display_Text__c = '01/01/2017';
            mapQAAQuestions.get('Who is submitting the complaint?').questionRec.Display_Text__c = 'KAMIYA ADAMSON';
            objQAAProcess.bIsProviderSelected = true;
            objQAAProcess.saveAction();
            System.assertEquals(true, objQAAProcess.isErrorExist);
            
            objQAAProcess.initLoadEdit();
            System.assertEquals('View', objQAAProcess.loadView);
            
            objQAAProcess.editAction();
            objQAAProcess.cancel();
            String sProvDetails = '####Provider First Name--####Provider Last Name--J THOMAS CARROLL DDS####Facility/ Group Name--J THOMAS CARROLL DDS####DBA Name--J THOMAS CARROLL DDS####Provider/Facility/Group Tax ID--####Provider/Facility/Group NPI--####Provider Street Address--1901 E MAIN ST####Provider City--LEAGUE CITY####Provider State--TX####Provider Zip Code--775731234';
            Apexpages.currentPage().getParameters().put('providerData',sProvDetails );
            objQAAProcess.populateProviderDetails();
            System.assertEquals('LEAGUE CITY', objQAAProcess.mapProviderQuestions.get('Provider City').questionRec.Display_text__c);
            
            String sValues = '####Is any part of the complaint related to accessibility care issues?----Yes####Is this for a Provider or a Facility/Group?----Facility/Group####Provider State----WA - Washington';
            ApexPages.currentPage().getParameters().put('sproviderDetails', sValues );
            objQAAProcess.getProviderDetailsFromPage();
            System.assertEquals('WA - Washington', objQAAProcess.mapProviderQuestions.get('Provider State').sSelectedDropDownOption );
            
            objQAAProcess.clearProviderDetails();
            System.assertEquals('', objQAAProcess.mapProviderQuestions.get('Provider City').questionRec.Display_text__c);
        }
    }
}