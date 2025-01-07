/******************************************************************************************************************************
Apex Class Name  : InvoiceRequest_C_HUM
Version          : 1.0 
Created Date     : June 22 2020
Function         : Controller to handle Guided Process.       
Test Class       : InvoiceRequest_T_HUM            
Modification Log :
    Developer             Code Review             Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Himalay Patel                                  06/22/2020           REQ - 938803 Original Version
* Himalay Patel                                  01/15/2021           REQ - 1649996 Receiving Error Message When Submitting Invoice Request Template (CRMS)
*********************************************************************************************************************************/

public with sharing class InvoiceRequest_C_HUM {
    public string SubmissionData {get; set; } 
    public String SubmissionID {get; set; }
    public String EmailAddress { get; set; }
    public String AltAddress { get; set; }
    public String BillAddress { get; set; }
    public String ShipAddress { get; set; }
    public String PermAddress { get; set; }
    public String CaseId {get;set;}
    public Flow.Interview.Invoice_Request invoiceFlow { get; set; }
    public Boolean hasProcess { get; set;}
    public String TN {get;set;}

    public InvoiceRequest_C_HUM(){
        EmailAddress = ApexPages.currentPage().getParameters().get('emailAddress');
        AltAddress = ApexPages.currentPage().getParameters().get('altAddress');
        BillAddress = ApexPages.currentPage().getParameters().get('billAddress');
        ShipAddress = ApexPages.currentPage().getParameters().get('shipAddress');
        PermAddress = ApexPages.currentPage().getParameters().get('permAddress');
        CaseId = ApexPages.currentPage().getParameters().get('CaseId');
        System.debug(CaseId);
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
        String templateNumber = [SELECT NAME FROM Template__c WHERE Description__c = 'Invoice Request' Limit 1].name;
        if(templateNumber != null && templateNumber != ''){
            TN = templateNumber;
        }
    }
       
    public String getSubmissionData() 
    {
        if(invoiceFlow == null) 
        {
            return null;
        }
        else
        {                       
            return string.valueOf(true);

        }
    } 
       
    public String getSubmissionID() 
    {
        if(invoiceFlow == null)
        { 
            return null;
        }
        else
        {                       
            return string.valueOf(invoiceFlow.SID);
        }
    }
    
    
    public PageReference getSubmissionvariable()
    {        
        ID PId = ApexPages.currentPage().getParameters().get('RecordId');
        PageReference p = new PageReference('/apex/InvoiceRequestLogTo_VF_HUM?SubmissionData='+getSubmissionData()+'&policymemberID='+PId+'&SubmissionID='+getSubmissionID()); 
        p.setRedirect(true); 
        return p;        
    } 
    
}