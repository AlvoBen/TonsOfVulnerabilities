/****************************************************************************
Apex Class Name  : HUMProcessesHelper
Version          : 1.0 
Created Date     : Aug 18 2014
Function         : Acts as Utility class for templates functionality.
Test Class       : HUMProcessesHelperTest
Modification Log :
 *  Modification ID     Developer     Code Review           Date                Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *          1.0         Vivek Batham     17840            07/26/2014             Original Version 
 *          2.0         Vivek Batham     17987            09/04/2014            Changes for QMS Attachments
 *          3.0         Praveen Kumar Parimi              06/17/2015            Added a null check to associationRec in assignQuestionResponse 
 *																				method as part of Encryption at Line #124
 *			4.0			Mohammed Noor					  07/24/2020			User Story 825176 PR00093155 - MF 18 - COB Template Changes
 **********************************************************************************************************************************/

public with sharing class HUMProcessesHelper{

      /*
     * Method Name : convertStringtoDate
     * Description : This method accepts String formatted date as parameter and converts into data format based on the input given.
     * Return type : Date
     * Paramater   : String
     */
    public static Date convertStringtoDate(string sInputDate)
    {
        try
        {
            if(sInputDate.length() == 8)
            {
                date formattedDate = date.newInstance(Integer.valueOf(sInputDate.subString(4)),
                        Integer.valueOf(sInputDate.subString(0,2)),
                        Integer.valueOf(sInputDate.subString(2,4)));
                return formattedDate;
            }
            else if(sInputDate.length() == 10)
            {
                Integer invalidFormatCount = 0;
                Integer validFormatCount = 0;
                for(Integer i=0;i<sInputDate.length();i++)
                {
                    if(sInputDate.subString(i,i+1)=='-') invalidFormatCount++;
                    if(sInputDate.subString(i,i+1)=='/') validFormatCount++;
                    
                }
                if(invalidFormatCount == 0 && validFormatCount == 2)
                {
                   date newDate = date.newInstance(Integer.valueOf(sInputDate.subString(6)),
                                                       Integer.valueOf(sInputDate.subString(0,2)),
                                                       Integer.valueOf(sInputDate.subString(3,5)));                                                      
                    return newDate;
                }
                else
                {
                    return null;
                }
             }
            else
            {
                return null;
            }             
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMProcessesHelper', 'convertStringtoDate');
            return null;
        }
    }
    
    /*
     * Method Name : assignDisplayText
     * Description :assigns the field values based on the question data type.
     * Return type : Question__c
     * Paramater   : Map<String, Case_Action_Response__c>, Question__c
     */
    public static Question__c assignDisplayText(Map<String, Case_Action_Response__c> quesRespMap, Question__c ques )
    {
        try
        {
            if(quesRespMap.get(ques.Question_Label__c) != null && ques.Question_Type__c == Label.HUMCOBCheckboxType)
            {
                ques.Display_Checkbox__c = quesRespMap.get(ques.Question_Label__c).Process_Question_Response__c =='true'? true : false;
            }
            if(quesRespMap.get(ques.Question_Label__c) != null && ques.Question_Type__c == Label.HUMCOBDateQuestionType)
            {
                ques.Display_Text__c = quesRespMap.get(ques.Question_Label__c).Process_Question_Response__c != null ? quesRespMap.get(ques.Question_Label__c).Process_Question_Response__c : null;
            }
            if(quesRespMap.get(ques.Question_Label__c) != null && ques.Question_Type__c == Label.HUMCOBTextAreaType)
            {
                ques.Display_text_area__c = quesRespMap.get(ques.Question_Label__c).Process_Question_Response__c;
            }
            if(quesRespMap.get(ques.Question_Label__c) != null && ques.Question_Type__c == Label.HUMCOBTextType)
            {
                ques.Display_text__c = quesRespMap.get(ques.Question_Label__c).Process_Question_Response__c;
            }
            if(quesRespMap.get(ques.Question_Label__c) != null && ques.Question_Type__c == Label.HUMCOBOutputTextType)
            {
                ques.Display_text__c = quesRespMap.get(ques.Question_Label__c).Process_Question_Response__c;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMProcessesHelper', 'assignDisplayText');
        }
        return ques;
    }
    
    /*
     * Method Name : assignQuestionResponse
     * Description : Assigns response values from question records before saving the responses to database.
     * Return type : list<Case_Action_Response__c>
     * Paramater   : list<HUMCaseActionQuestionHelper>
     */
    public static list<Case_Action_Response__c> assignQuestionResponse(list<HUMCaseActionQuestionHelper> lstWrapperQuestions, Map<String, Case_Action_Response__c> quesResponseMap, Case_Action_Association__c associationRec){
        List<Case_Action_Response__c> lstResponses = new List<Case_Action_Response__c>();
        for(HUMCaseActionQuestionHelper question: lstWrapperQuestions)
        {
            if(question.boolDisplayQuestion)
            {
                Case_Action_Response__c response = new Case_Action_Response__c();
                if(quesResponseMap != null)
                {
                    response.Id = quesResponseMap.get(question.questionRec.Question_Label__c) != null ? quesResponseMap.get(question.questionRec.Question_Label__c).Id : null;
                }
                if(associationRec != null)                
                {
                	response.Action__c = associationRec.Id;
                }
                response.Process_Question__c = question.questionRec.Question_Label__c;
                if(question.questionRec.Question_Type__c == Label.HUMCOBDropDownType)
                {
                     response.Process_Question_Response__c = question.sSelectedDropDownOption;
                }
                else if(question.questionRec.Question_Type__c == Label.HUMCOBDateQuestionType)
                {
                    response.Process_Question_Response__c = HUMProcessesHelper.changeDateFormat(question.questionRec.Display_Date__c);
                }
                else if(question.questionRec.Question_Type__c == Label.HUMCOBCheckboxType)
                {
                    response.Process_Question_Response__c = String.valueOf(question.questionRec.Display_Checkbox__c);
                }
                else if(question.questionRec.Question_Type__c == Label.HUMCOBTextType)
                {
                    response.Process_Question_Response__c = question.questionRec.Display_text__c;     
                }                       
                else if(question.questionRec.Question_Type__c == Label.HUMCOBTextAreaType)
                {
                    response.Process_Question_Response__c = question.questionRec.Display_text_area__c;    
                }                        
                else if(question.questionRec.Question_Type__c == Label.HUMCOBOutputTextType)
                {
                    response.Process_Question_Response__c = question.questionRec.Display_text__c;
                }
                response.case_Question__c = question.questionRec.Id;
                lstResponses.add(response);
            }
        }
        return lstResponses;
    }
    
    /*
     * Method Name : changeDateFormat
     * Description : Accepts date variable as parameter and returns string in 'MM/dd/yyyy' format.
     * Return type : string
     * Paramater   : Date
     */
     public static string changeDateFormat(Date dInputDate)
     {
        try
        {
            string sOutputDate = '';
            if(dInputDate != null)
            {
                string sMonth = string.valueOf(dInputDate.month()).length() == 2 ? string.valueOf(dInputDate.month()) : '0'+string.valueOf(dInputDate.month());
                string sDay = string.valueOf(dInputDate.day()).length() == 2 ? string.valueOf(dInputDate.day()) : '0'+string.valueOf(dInputDate.day());
                string sYear = string.valueOf(dInputDate.year());
                sOutputDate = sMonth+'/'+sDay+'/'+sYear;
            }
            return sOutputDate;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMProcessesHelper', 'changeDateFormat');
            return null;
        }
        
     }
    
    /*
     * Method Name : splitPicklistValues
     * Description : Accepts the semi-colon separated picklist values and splits the string based on semi-colon and stores into selectoption list.
     * Return type : list<selectoption>
     * Paramater   : string
     */
     public static list<selectoption> splitPicklistValues(string picklistValues)
     {
     	list<selectoption> lstSelectOptions = new list<selectoption>();
        try
        {
        	for (String value: picklistValues.split(';')) 
                {
                    lstSelectOptions.add(new SelectOption(value, value));
                }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMProcessesHelper', 'splitPicklistValues');
        }
        return lstSelectOptions;
     }
    
    /*
     * Method Name : processQuestionResponse
     * Description :Checks with the input question type and assigns the matching response from relevant field.
     * Return type : string
     * Paramater   : HUMCaseActionQuestionHelper
     */
    public static string processQuestionResponse(HUMCaseActionQuestionHelper question)
    {
        string sValue = '';
        try
        {
            if (question.questionRec.Question_Type__c == Label.HUMCOBDropDownType)
            {
                sValue = question.sSelectedDropDownOption;
            }
            else if (question.questionRec.Question_Type__c == Label.HUMCOBDateQuestionType)
            { 
                sValue = HUMProcessesHelper.changeDateFormat(question.questionRec.Display_Date__c);
            }
            else if (question.questionRec.Question_Type__c == Label.HUMCOBCheckboxType) 
            {
                sValue = String.valueOf(question.questionRec.Display_Checkbox__c);
            }
            else if (question.questionRec.Question_Type__c == Label.HUMCOBTextType) 
            {
                sValue = string.isEmpty(question.sValue) ? question.questionRec.Display_text__c : question.sValue;
            }
            else if (question.questionRec.Question_Type__c == Label.HUMCOBTextAreaType) 
            {
                sValue = question.questionRec.Display_text_area__c;
            }
            else if(question.questionRec.Question_Type__c == Label.HUMCOBOutputTextType)
            {
                sValue = question.questionRec.Display_text__c;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMProcessesHelper', 'processQuestionResponse');
        }
        return sValue;
    }
    
    
    /*
     * Method Name : getCaseActionResponses
     * Description : This method returns the CaseActionResponse records by accepting the respective Case Association record Id and ProcessType.
     * Return type : list<Case_Action_Response__c>
     * Parameter   : Id, string
     */
    public static list<Case_Action_Response__c> getCaseActionResponses(Id AssociationRecId, string processType){
        List<Case_Action_Response__c> lstActionResponses = [Select Process_Question__c, Process_Question_Response__c, Name, Id, Case_Question__c, Action__c
                                                               From Case_Action_Response__c where Action__c = : AssociationRecId AND Action__r.Case_Action__r.Name = : processType
                                                               ];
        return lstActionResponses;
    } 
    
    
      /*
     * Method Name : getTabularAnswerResponses
     * Description : This method returns the TabularAnswerResponses records by accepting the respective Case Association record Id.
     * Return type : list<Tabular_Answer_Response__c>
     * Parameter   : Id
     */
    public static list<Tabular_Answer_Response__c> getTabularAnswerResponses(Id AssociationRecId){
    	//US825176 - Added an extra field Grid_Question__r.Question__r.Question_Label__c and order by Row Number to display the records in order on COB template detail page
        List<Tabular_Answer_Response__c> lstTabularResponses = [Select Row_Number__c, Response__c, Id, Date_Response__c, Column_Title__c, Boolean_Response__c, Action_Response__c, Grid_Question__r.Question_Type__c,
                                                      Grid_Question__r.Question__r.Question_Label__c From Tabular_Answer_Response__c where Action_Response__c = : AssociationRecId order by Row_Number__c, Grid_Question__r.Sequence_Number__c
                                                      ];
        return lstTabularResponses;
    } 
    
    
    
     /*
     * Method Name : validateTaxId
     * Description : Validates if the entered Tax Id matches the pattern.
     * Return type : Boolean
     * Paramater   : String
     */
    public static Boolean validateTaxId(string taxNo)
    {
        try
        {
            Pattern isnumbers = Pattern.Compile('^[0-9]+$');
            Pattern isninedigits = Pattern.Compile('^\\d{9}$');
            Matcher postalMatch = isnumbers.matcher(taxNo);
            Matcher postalMatch2 = isninedigits.matcher(taxNo);
            Boolean result = false;
            if(postalMatch.Matches() && postalMatch2.Matches())
            {
                Set<String> splitTaskNo = new Set<String>();
                for(Integer i=0; i < taxNo.length() - 1; i++)
                {
                    splitTaskNo.add(taxNo.substring(i,i+1));
                }
                if(splitTaskNo.size() > 1)
                {
                    result = true;
                }
                else
                {
                    result = false;
                }
                return result;
            }
            else
            {
                return result;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMProcessesHelper', 'validateTaxId');
            return false;
        }
    }     
 
 }