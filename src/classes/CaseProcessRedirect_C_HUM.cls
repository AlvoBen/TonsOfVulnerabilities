/******************************************************************************************************************************
Apex Class Name  : CaseProcessRedirect_C_HUM
Version          : 1.0 
Created Date     : 
Function         :               
Modification Log :
    Developer             Code Review             Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Aaron Speakman                                  10/14/2020           Original Version.
*********************************************************************************************************************************/

public with sharing class CaseProcessRedirect_C_HUM {
    public String CaseId {get;set;}
    public String TicketRequired {get;set;}
    public String CaseComment {get;set;}
    public String CaseNumber {get;set;}
    public String TabId {get;set;}
    
    public CaseProcessRedirect_C_HUM(){
        CaseId = ApexPages.currentPage().getParameters().get('CaseId');
        TicketRequired = ApexPages.currentPage().getParameters().get('TicketRequired');
        CaseComment = ApexPages.currentPage().getParameters().get('CaseComment');
        TabId = ApexPages.currentPage().getParameters().get('TabId');
        List<Case> lstCases = [Select CaseNumber from case WHERE Id = :CaseId limit 1];
        if(lstCases.size() > 0) CaseNumber = lstCases[0].CaseNumber;       
    }

}