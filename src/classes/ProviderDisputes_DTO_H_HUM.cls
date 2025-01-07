/**************************************************************************************************************************************************
    @Apex class Name    :  ProviderDisputes_DTO_H_HUM 
    @Function           :  This class serves as DTO wrapper for Provider Dispute Service.
    @Version            :  1.0
    @reference          :  ProviderDisputes_S_HUM

* Developer Name         Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ajay Chakradhar                              10/15/2019                  Original Version of Provider Dispute Cases Response  
************************************************************************************************************************************************* */
public class ProviderDisputes_DTO_H_HUM {
    public string sCase {get; set;}
    public string sType {get; set;}
    public string sCategory {get; set;}
    public string sSubCategory {get; set;}
    public string sStatus {get; set;}
    public string sStatusReason {get; set;}
    public string sPriority {get; set;}
    public string sDueDate {get; set;}
    public string sProviderName {get; set;}
    public string sServiceType {get; set;}
    public string sRequestType {get; set;}
    public string sDateofDetermination {get; set;}
    public string sProduct {get; set;}
    public string sReceivedDate {get; set;}
    public string sProviderTaxID {get; set;}
    public string sDateofService {get; set;}
    public string sClaimNumber {get; set;}
    public string sAssignedAnalyst {get; set;}
    public Notifications notifications{get; set;}    
    public string internalError{get;set;}
    public string medhokID{get;set;}
    public string context{get;set;}
	
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