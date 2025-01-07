/**************************************************************************** 
Apex Class Name  : QAAComplaint_LT_HUM 
Version          : 1.0  
Function         : This class test class for QAAComplaint_LT_HUM
Test Class       : 

 * Developer Name           Code Review                  Date                       Description
 *---------------------------------------------------------------------------------------------------------------------
 * Santhi Mandava                                        08/30/2022                 Original Version
********************************************************************************************/

@isTest
public with sharing class QAAComplaint_LT_HUM {
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
           
        System.runAs(oUser)
        {
            List<Case> lstCases = [Select Id from Case];
            List<QAAComplaint_LC_HUM.CaseActionQuestionHelper_LC_HUM> lstQuestions = new List<QAAComplaint_LC_HUM.CaseActionQuestionHelper_LC_HUM>();
            lstQuestions = QAAComplaint_LC_HUM.getQAATemplateDetails();
            System.assertEquals(13, lstQuestions.size());
            for(QAAComplaint_LC_HUM.CaseActionQuestionHelper_LC_HUM obj : lstQuestions){
                if(obj.questionRec.Question_label__c == 'Is the Member willing to provide their QAA Complaint details verbally?') obj.sValue = 'Yes';
                else if(obj.questionRec.Question_label__c == 'Who is submitting the complaint?') obj.sValue = 'SALAE GROESSER';
                else if(obj.questionRec.Question_label__c == 'Date of the incident/service') obj.sValue = '06/06/2021';
                else obj.sValue = 'test';                                                                                                                                                   
            }
            QAAComplaint_LC_HUM.QAAResponse objResponse = new QAAComplaint_LC_HUM.QAAResponse();
            objResponse.lstResponses = lstQuestions;
            String sResponse = JSON.serialize(objResponse);
            QAAComplaint_LC_HUM.saveQAATemplate(lstCases[0].Id,sResponse);

            List<Case_Action_Response__c> lstRes = [Select Id,Process_Question__c,Process_Question_Response__c FROM Case_Action_Response__c];
            for(Case_Action_Response__c obj : lstRes){
                if(obj.Process_Question__c == 'Who is submitting the complaint?'){
                    System.assertEquals('SALAE GROESSER', obj.Process_Question_Response__c);
                    break;
                }
            }
        }
    }
}