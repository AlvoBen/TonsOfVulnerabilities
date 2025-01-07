/******************************************************************************************************************************
Apex Class Name  : HUMEnrollmentWelcomeCall_C_HUM
Version          : 1.0 
Created Date     : March7 2022
Function         : Controller to handle display Enrollment Welcome form.                   
Test Class       : HUMEnrollmentWelcomeCall_T_HUM
Modification Log :
   Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
  Apurva Urkude                             03/13/2018           Original version.
 *********************************************************************************************************************************/
public with sharing class HUMEnrollmentWelcomeCall_C_HUM {
    public string MemberId {get; set; } 
    public String CaseId {get;set;}
    public String CaseNumber {get;set;}
    public String TabId {get;set;}
    public String LaunchProcessTabID {get;set;}
    public String SubmissionID {get;set;}
    public String TN {get;set;}
    public string SubmissionData {get; set; }
    public Boolean TicketRequired {get;set;}
    public Boolean hasProcess { get; set;}
    public string sCaseStatus { get; set;}

    public HUMEnrollmentWelcomeCall_C_HUM() {

        CaseId = ApexPages.currentPage().getParameters().get('CaseId');
        try {
        if(CaseId != null){
            LIst<Case> cas = [SELECT CaseNumber,Status FROM Case WHERE Id =: CaseId LIMIT 1]; 
            CaseNumber = cas[0].CaseNumber;
            sCaseStatus = cas[0].Status;
            List<Case> MemberPrefill = [SELECT Id, Policy_Member__r.Name, Policy_Member__r.member__r.Name, Policy_Member__r.member__r.Birthdate__c FROM case WHERE Id=:CaseId];
        if (MemberPrefill.size() > 0) {
            MemberId = MemberPrefill[0].Policy_Member__r.Name;            
        }
            List<Template_Submission__c> TempList = [SELECT Id FROM Template_Submission__c WHERE Submission_Owner__r.Object_Owner_ID__c =:CaseId LIMIT 1];        
        if(TempList != null && TempList.size() > 0){ 
            SubmissionID = TempList[0].Id;            
            List<Template_Submission_Data__c> Submission = [SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__c =: SubmissionID LIMIT 1];
        if(Submission != null && Submission.size() > 1 && (Submission[0].Value__c != '' && Submission[1].Value__c != ''))
        {            
        if(Submission.size() > 0)
        {
            SubmissionData = Submission[0].Value__c;
        }
        hasProcess = true;                
            }
          }
        }   else{
            SubmissionID = '';           
            hasProcess = false;            
        }
        
        List<Template__c> templateNumber = [SELECT NAME FROM Template__c WHERE Description__c = 'Enroll & Onboard (E&O) Welcome Call' Limit 1];
        if(templateNumber != null && templateNumber.size() > 0){
            TN = templateNumber[0].name;          
         }                       
        } 
        catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
}