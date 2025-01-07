/**************************************************************************** 
Apex Class Name  : HUMCaseActionQuestionHelper
Version          : 1.0  
Created Date     : August 18 2014 
Function         : This class is used as a base class to handle the Attachments
Test Class       : HUMCaseActionQuestionHelperTest
****************************************************************************
Modification Log:

* Developer Name          Review Number                         Date                        Description
*------------------------------------------------------------------------------------------------------------
* Vivek Batham            17429                              08/18/2014                   Original Version
* Vivek Batham            17875                              09/11/2014                   Made changes according to Humana standards
* Santhi Mandava          71723                              01/03/2017                   Added sClassName variable. 
***************************************************************************************************/

public with Sharing class HUMCaseActionQuestionHelper{
   
    public HUMCaseActionQuestionHelper(){
        questionRec = new Question__c();
        lstDropDownOptions = new List<SelectOption>();
        sSelectedDropDownOption = null;
        boolDisplayQuestion = true;
        sValue=null;
        bIsDisabled = false;
    }
    
    public Question__c questionRec{get;set;}
    public List<SelectOption> lstDropdownOptions{get;set;}
    public String sSelectedDropDownOption{get;set;}
    public Boolean boolDisplayQuestion{get;set;}
    public string sValue{get;set;}
    public String sClassName{get;set;}
    public Boolean bIsDisabled{get;set;}
}