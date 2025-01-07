/**************************************************************************** 
Apex Class Name  : QAAComplaint_LC_HUM 
Version          : 1.0  
Function         : This class is used to display/save QAA template details.
Test Class       : QAAComplaint_LT_HUM

 * Developer Name           Code Review                  Date                       Description
 *---------------------------------------------------------------------------------------------------------------------
 * Santhi Mandava                                        08/30/2022                 Original Version
********************************************************************************************/

public with sharing class QAAComplaint_LC_HUM {
    private static map<String,Map<String,List<String>>> mapMasterConditions = new Map<String,Map<String,List<String>>>();
    private static list<CaseActionQuestionHelper_LC_HUM> lstWrapperQuestions;

     /*  
     * Method name : getQAATemplateDetails
     * Description : this method is used to retrieve QAA template questions from Question__c object
     * Return Type : List<CaseActionQuestionHelper_LC_HUM>
     * Parameter :   N/A
     */ 
    @AuraEnabled
    public static List<CaseActionQuestionHelper_LC_HUM> getQAATemplateDetails()
    {
        try{
            List<Question__c> lstQuestions = [Select Id,Name,Question_Label__c,Parent_Question__c,Parent_Question__r.Question_Label__c,Question_Type__c,Question_Values__c,Dependent_Value__c, 
            Display_Text__c,Display_Text_Area__c,Display_Checkbox__c,Display_Date__c, Sequence_Number__c, Help_Text__c 
            from Question__c where Actions__r.Name = 'QAA' AND Active__c = true ORDER BY Sequence_Number__c ASC NULLS LAST];
            return getQAAWrapperList(lstQuestions);
        }catch(Exception ex){
            return null;
        }
    }

    /*  
     * Method name : getQAATemplateDetails
     * Description : this method is used to create wrapper list of QAA template questions
     * Return Type : List<CaseActionQuestionHelper_LC_HUM>
     * Parameter :   List<Question__c>
     */ 
    private static List<CaseActionQuestionHelper_LC_HUM> getQAAWrapperList(List<Question__c> lstQuestions){
        List<CaseActionQuestionHelper_LC_HUM> lstWrapperQuestions = new List<CaseActionQuestionHelper_LC_HUM>();
        CaseActionQuestionHelper_LC_HUM objQuestion;
        for(Question__c objQues : lstQuestions)
        {
            objQuestion = new CaseActionQuestionHelper_LC_HUM();
            objQuestion.questionRec = objQues;
            if(objQues.Parent_Question__r != Null)  objQuestion.boolDisplayQuestion = false;
            if(objQues.Question_Values__c !=NULL){
                for(String DropDownValue: objQues.Question_Values__c.split(';'))
                {
                    If(objQues.Question_Label__c == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERSTATE_HUM)
                    {
                        String[] selectArr = DropDownValue.split('-');
                        String selectValue= selectArr[0].trim();
                        String selectLabel = selectArr[1].trim();
                        objQuestion.mapState.put(selectValue,selectLabel);
                    }
                    else
                    {
                        objQuestion.lstDropDownOptions.add(DropDownValue);
                    }
                }
                if(objQues.Question_Label__c == 'Is the Member willing to provide their QAA Complaint details verbally?') objQuestion.sSelectedDropDownOption = 'Yes';
                else if(objQues.Question_Label__c == 'Is this for a Provider or a Facility/Group?') objQuestion.sSelectedDropDownOption = 'Provider';
            }
            if(objQues.Question_Label__c == 'Process Type:') objQuestion.sValue = 'QAA Complaint';
            lstWrapperQuestions.add(objQuestion);
        }
        return lstWrapperQuestions;
    }

    /*  
     * Method name : saveQAATemplate
     * Description : this method is used to save QAA template details
     * Return Type : Void
     * Parameter :   String,String
     */ 
    @AuraEnabled
    public static void saveQAATemplate(String scaseid,String jsonqaadata){
        try{
            QAAComplaint_LC_HUM.QAAResponse qaaResponse ;
            qaaResponse = (QAAComplaint_LC_HUM.QAAResponse)System.JSON.deserialize(jsonqaadata, QAAComplaint_LC_HUM.QAAResponse.class);
            Case_Action__c activeAction = [Select Id from Case_Action__c where Name= 'QAA' and Active__c = true LIMIT 1];
            Case_Action_Association__c associationRec = new Case_Action_Association__c();
                        
            if(Schema.sObjectType.Case_Action_Association__c.fields.Case__c.isCreateable()) associationRec.Case__c = scaseid;
            if(Schema.sObjectType.Case_Action_Association__c.fields.Case_Action__c.isCreateable()) associationRec.Case_Action__c = activeAction.Id; 
            List<Case_Action_Association__c> lstCaseActionAssociations = new List<Case_Action_Association__c>();
            lstCaseActionAssociations.add(associationRec);
            List<Database.SaveResult> lstSvr = Database.insert(lstCaseActionAssociations,false);
            List<Case_Action_Response__c> lstResponses = new List<Case_Action_Response__c>();
            Case_Action_Response__c objUserRes;
            for(CaseActionQuestionHelper_LC_HUM objRes :qaaResponse.lstResponses){
                objUserRes = new Case_Action_Response__c();
                objUserRes.Action__c = associationRec.Id;
                objUserRes.Case_Question__c = objRes.questionRec.Id;
                objUserRes.Process_Question__c = objRes.questionRec.Question_Label__c;
                objUserRes.Process_Question_Response__c = objRes.sValue;
                lstResponses.Add(objUserRes);
            }
            List<Database.SaveResult> lstSaveResults = Database.insert(lstResponses,false);
        }catch(Exception ex){

        }
    }
   
    
    public with sharing class QAAResponse {
        @AuraEnabled public List<QAAComplaint_LC_HUM.CaseActionQuestionHelper_LC_HUM> lstResponses{get;set;}
    }

     public with sharing class CaseActionQuestionHelper_LC_HUM {
        public CaseActionQuestionHelper_LC_HUM() {
            this.questionRec = new Question__c();
            this.lstDropDownOptions = new List<String>();
            this.sSelectedDropDownOption = null;
            this.boolDisplayQuestion = true;
            this.sValue=null;
            this.bIsDisabled = false;
            this.mapState = new Map<String,String>();
        }
    
        @AuraEnabled public Question__c questionRec{get;set;}
        @AuraEnabled public List<String> lstDropdownOptions{get;set;}
        @AuraEnabled public String sSelectedDropDownOption{get;set;}
        @AuraEnabled public Boolean boolDisplayQuestion{get;set;}
        @AuraEnabled public string sValue{get;set;}
        @AuraEnabled public Boolean bIsDisabled{get;set;}
        @AuraEnabled public Map<String,String> mapState{get;set;}
   }
}