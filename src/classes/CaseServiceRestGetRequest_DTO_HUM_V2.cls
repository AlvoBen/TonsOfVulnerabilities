/******************************************************************************************************************
Apex class Name    : CaseServiceRestGetRequest_DTO_HUM_V2
Version            : 1.0
Function           : This class is used as request for get case Rest Service  GetCaseInfoRest_D_HUM_V2.
Created On         : January 15 2019
Test Class         : HUMCaseServiceRestTest_V2

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Divya Proddaturi             340085           2/27/2019            Original Version
*   1.1            Moshitha Gunasekaran                          04/02/2019           REQ - 392822 Tech - SF - Enhance GetCaseInfo method of Case Service  
*   1.2            Mohammed Noor                                 07/01/2019           Changes done for REQ - 403693.  
------------------------------------------------------------------------------------------------------------------- */


public class CaseServiceRestGetRequest_DTO_HUM_V2 {
    
  public CaseInfoRequestDTO GetCaseInfo;
  
    public class CaseInfoRequestDTO { 
        public List<String> InclusionCriteria; 
        public List<String> CaseId;  
        public String EndDate =''; 
        public EntityIdentifierDTO[] InteractingAboutCriteria; 
        public EntityIdentifierDTO[] InteractingWithCriteria;            
        public String StartDate = '';   
        public List<String> CaseType;
        public List<String> CaseSubtype; 
        public String StartPaging=''; 
        public List<String> Status;
        public QueueDetailsDTO[] OwnerQueueDetails;
    }
    
    public class QueueDetailsDTO{
        public String WorkQueueName;  
        public List<String> QueueViewName; 
    }
}