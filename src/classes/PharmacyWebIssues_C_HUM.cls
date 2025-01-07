/******************************************************************************************************************************
Apex Class Name  : PharmacyWebIssues_C_HUM
Version          : 1.0 
Created Date     : October 14 2020
Function         : Controller to handle Guided Process.                   
Modification Log :
    Developer             Code Review             Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Himalay Patel                                  10/14/2020           REQ - 1135788 Original Version
* Kiran Bhuvanagiri								 03/08/2021			  User Story - 1995737: Update Classification/Intent Selected Upon Completion of 'Humana Pharmacy Web Issues' Guided Process (CRMS)
* Venkata Nagasiva                                05/17/2022                US-3367406 T1PRJ0010888 - MF [1] - Rebranding - Update text references from "Humana Pharmacy" to "Centerwell Pharmacy" - Web Issues Guided Flow - Process Name
*********************************************************************************************************************************/


public with sharing class PharmacyWebIssues_C_HUM {
    public string MemberId {get; set; } 
    public String MemberName {get; set; }
    public String MemberDOB { get; set; }
    public Flow.Interview.Humana_Pharmacy_Web_Issues webIssuesFlow { get; set; }
    public String CaseId {get;set;}
    public String TabId {get;set;}
    public String LaunchProcessTabID {get;set;}
    public String SubmissionID {get;set;}
    public String TN {get;set;}
    public string SubmissionData {get; set; }
    public Boolean TicketRequired {get;set;}
    public Boolean hasProcess { get; set;}

    
    
    public PharmacyWebIssues_C_HUM() {
        CaseId = ApexPages.currentPage().getParameters().get('CaseId');
        CaseId = [SELECT Id FROM Case WHERE Id =: CaseId LIMIT 1].Id; //Ensures the Id is always 18 characters 
        TabId = ApexPages.currentPage().getParameters().get('TabId');
        LaunchProcessTabID = ApexPages.currentPage().getParameters().get('caseSubTabId');

        if (LaunchProcessTabID != Null || !String.isBlank(LaunchProcessTabID)){
            TabId = LaunchProcessTabID;
        }
        List<Case> MemberPrefill = [SELECT Id, Policy_Member__r.Name, Policy_Member__r.member__r.Name, Policy_Member__r.member__r.Birthdate__c FROM case WHERE Id=:CaseId];
        if (MemberPrefill.size() > 0) {
            MemberId = MemberPrefill[0].Policy_Member__r.Name;
            MemberName = MemberPrefill[0].Policy_Member__r.member__r.Name;
            MemberDOB = MemberPrefill[0].Policy_Member__r.member__r.Birthdate__c;
        }

        List<Template_Submission__c> TempList = [SELECT Id FROM Template_Submission__c WHERE Submission_Owner__r.Object_Owner_ID__c =:CaseId LIMIT 1];
        if(TempList != null && TempList.size() > 0){ 
            SubmissionID = TempList[0].Id;
            List<Template_Submission_Data__c> Submission = [SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__c =: SubmissionID];
            if(Submission != null && (Submission[0].Value__c != '' && Submission[1].Value__c != ''))
            {
                if(Submission.size() > 0)
                {
                    SubmissionData = Submission[0].Value__c;
                }
                if(Submission.size() > 1)
                {
                    TicketRequired = Boolean.valueOf(Submission[1].Value__c);
                }
                hasProcess = true;
            }
        }else{
            SubmissionID = '';
            hasProcess = false;
        }
        
        String templateNumber = [SELECT NAME FROM Template__c WHERE Description__c = 'Centerwell Pharmacy Web Issues' Limit 1].name;
        if(templateNumber != null && templateNumber != ''){
            TN = templateNumber;
        }
}     
    
    public String getTicketRequired() 
    {     
        if(webIssuesFlow == null)
        { 
            return null;
        }
        else
        {   
            return string.valueOf(webIssuesFlow.Ticket_Required);
        }
    }
    
    public PageReference getSubmitWebIssue()
    {    
        String sTicketRequried = getTicketRequired();
        String sCaseComment = '';
        IF (sTicketRequried == 'true') {
            sCaseComment = 'Centerwell Pharmacy Web Issues process attached to the case and transferred to the Web Ticket team.';
        }
        PageReference p = new PageReference('/apex/CaseProcessRedirect_VF_HUM?CaseId=' + CaseId+ '&TabId=' + TabId + '&TicketRequired=' + sTicketRequried + '&CaseComment=' + sCaseComment);
        p.setRedirect(true); 
        return p;    
    }
    
    /*
    * Method Name   :    updateWebIssuesCTCI
    * Description   :    Update Case classificaiton and Intent for Web Issues Auto Route
    * Return Type   :    NA
    * Parameters    :    Case Record
    */
    @InvocableMethod(label='updateWebIssuesClassCTCI' description='Update Case classificaiton and Intent for Web Issues Auto Route')
    public static void updateWebIssuesCTCI(List<Case> sSelectedCaseId)
    { 
        List<case> lstcasenumber = [SELECT id,CaseNumber,Classification_Type__c, Interacting_With_Type__c, Interacting_With__r.RecordType.DeveloperName FROM case WHERE id =:sSelectedCaseId[0].id LIMIT 1];
        set<String> classificationTypes = new set<String>{'Humana Pharmacy Calls','Humana Pharmacy Web Chat'};
        if(classificationTypes.contains(lstcasenumber[0].Classification_Type__c)){
            for(CTCI_Junction__c ctci : [SELECT id,Classification__c,Intent__c,Classification__r.name,Intent__r.name FROM CTCI_Junction__c WHERE Classification_Type__r.name =:lstcasenumber[0].Classification_Type__c AND IsActive__c = true]){
                if(ctci.Classification__r.name == 'Website (CT & TM)' && ctci.Intent__r.name == 'Web Education/Navigation'){
                    lstcasenumber[0].CTCI_List__c = ctci.id;
                    lstcasenumber[0].Classification_Id__c = ctci.Classification__c;
                    lstcasenumber[0].Intent_Id__c = ctci.Intent__c;
                }
            }

            if(lstcasenumber[0].Interacting_With_Type__c == '' || lstcasenumber[0].Interacting_With_Type__c == Null){
                lstcasenumber[0].Interacting_With_Type__c = lstcasenumber[0].Interacting_With__r.RecordType.DeveloperName;
            }

            update lstcasenumber[0];
        }
    }
}