/**************************************************************************************************************************************************
    @Apex class Name    :  GreivanceAndAppeal_DTO_HUM 
    @Function           :  This class serves as DTO wrapper for Grievance And Appeal Service.
    @Version            : 1.0
    @reference          : GrievanceAndAppeal_S_HUM

Modification Log:
* Developer Name         Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ajay Chakradhar                              08/28/2018                  Original Version of GreivanceAndAppeal Response  
* Swapnali Sonawane                            04/09/2021                  Req#2119511 added ExRM Fields to MHK Web Service and Additional Data for G&A Status Page in CRM
************************************************************************************************************************************************* */

public with sharing class GreivanceAndAppeal_DTO_HUM 
{
    public string sCase {get; set;}
    public string sType {get; set;}
    public string sCategory {get; set;}
    public string sStatus {get; set;}
    public string sStatusReason {get; set;}
    public string sPriority {get; set;}
    public string sServiceType {get; set;}
    public string sRequestType {get; set;}
    public string sDueDate {get; set;}
    public string sDateofDetermination {get; set;}
    public string sAssignedAnalyst {get; set;}
    public string context {get; set;}
    public string medhokID {get; set;}
    public Notifications notifications{get; set;}    
    public string internalError{get;set;}
    public String sManualReceivedDate{get;set;}
    public String sProviderDisplayName{get;set;}
    public String sDatesOfService{get;set;}
    public string sClaimNumber{get;set;}
    public String sMemberLOB {get;set;}
    public String sMemberCardId {get;set;}
    public String sMemberElgGrp {get;set;}
    public String sMemberElgPlan {get;set;}
    Public String sAllegation {get;set;}
    public String sFinalResolutionCaseNote {get;set;}
    public String sProviderTaxID {get;set;}
    
   
    public class Notifications 
    {
        public list<CaseNotifications> caseNotifications{get; set;}
    }
    
    public class caseNotifications implements Comparable
    {
        public string createdBy{get; set;}
        public string note{get; set;}
        public string noteDate{get; set;}
        
        public CaseNotifications(){}
        
        public CaseNotifications(String createdBy,String note,string noteDate){
            This.createdBy = createdBy;
            This.note = note;
            This.noteDate = noteDate;
        }
        public Integer compareTo(Object compareTo) 
        {
            caseNotifications cWrap = (caseNotifications)compareTo;
            Integer returnValue = 0;
            if(noteDate < cWrap.noteDate){
                returnValue = 1;
            } else if (noteDate > cWrap.noteDate) {
                returnValue = -1;
            }
        return returnValue; 
        }  
    }  
}