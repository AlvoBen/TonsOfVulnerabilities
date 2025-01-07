/******************************************************************************************************************************
Apex Class Name  : PharmacyInactivateRx_C_HUM
Version          : 1.0 
Created Date     : 01/10/2022 
Function         : Controller to handle Inactivte Rx Guided Process.       
Test Class       : PharmacyInactivateRx_T_HUM
Modification Log :
*   Developer                   Date                 Description
------------------------------------------------------------------------------------------------------------------------------                
*   Ashok Kumar Nutalapati		01/10/2022           REQ-2843422 Original  Version.
*********************************************************************************************************************************/

public with sharing class PharmacyInactivateRx_C_HUM {

    public Flow.Interview.Inactivate_Rx InactivateRxFlow { get; set; }
    public string InactivateRx {get; set;}
    public String TN {get;set;}
    public String SubmissionID {get; set;}
    public string SubmissionData {get; set; }
    public String sInteractionId {get; set;}
    public String sAccountId{get;set;}
    public String sPolicyId{get;set;}
    public String CaseId {get;set;}
    public Boolean hasProcess { get; set;}

    public PharmacyInactivateRx_C_HUM(){
        InactivateRx = ApexPages.currentPage().getParameters().get('InactivateRx');
        CaseId = ApexPages.currentPage().getParameters().get('CaseId');
        if(CaseId != null){
            Template_Submission__c Temp = [SELECT Id FROM Template_Submission__c WHERE Submission_Owner__r.Object_Owner_ID__c =:CaseId LIMIT 1];
            if(Temp != null){
                SubmissionID = Temp.Id;
                SubmissionData = [SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__c =: SubmissionID LIMIT 1].value__c;
            }else{
                SubmissionID = '';
            }
            hasProcess = true;
        } else{
            SubmissionID = '';
            hasProcess = false;
        }
        String templateNumber = [SELECT NAME FROM Template__c WHERE Description__c = 'Inactivate Rx' Limit 1].NAME;
        if(templateNumber != null && templateNumber != ''){
            TN = templateNumber;
        }
    }
       
    public PageReference getSubmissionvariable()
    {        
        sAccountId = ApexPages.currentPage().getParameters().get('AccountId');
        sPolicyId = ApexPages.currentPage().getParameters().get('PolicyId');
        sInteractionId = ApexPages.currentPage().getParameters().get('InteractionId');
        PageReference p = new PageReference('/apex/InactivateRxLogToCase_VF_HUM?PolicyId=' + sPolicyId + '&AccountId=' + sAccountId + '&ClinicalTransfer=' + getClinicalTransfer() + '&InteractionId=' + sInteractionId + '&SubmissionID=' + getSubmissionID()); 
        p.setRedirect(true);
        return p;        
    }
    
    public String getClinicalTransfer() 
    {
        if(InactivateRxFlow == null)
        { 
            return null;
        }
        else
        {                       
            return string.valueOf(InactivateRxFlow.Clinical_Transfer);
        }
    }
    
    public String getSubmissionID() 
    {
        if(InactivateRxFlow == null)
        { 
            return null;
        }
        else
        {                       
            return string.valueOf(InactivateRxFlow.SID);
        }
    }
    
}