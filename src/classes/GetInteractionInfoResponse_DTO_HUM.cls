/******************************************************************************************************************
Apex class Name    : GetInteractionInfoResponse_DTO_HUM
Version            : 1.0
Function           : Test class is Response DTO for GetInteractionInfo method of Case Service.
Created On         : July 01 2019
Test Class         : GetInteractionInfoRest_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Mohammed Noor                                  07/01/2019        Original Version
* ------------------------------------------------------------------------------------------------------------------- */
public with sharing class GetInteractionInfoResponse_DTO_HUM
{
	public GetInteractionInfoResponseDTO GetInteractionInfoResponse;
    
    public class GetInteractionInfoResponseDTO 
    {
        public string InteractionCount = '';    
        public InteractionInfoResponseDTO[] InteractionInfoResponseList;
    }
    
    public class InteractionInfoResponseDTO 
    {
        public InteractionSummaryDTO InteractionSummary;
        public InteractionDetailsDTO[] InteractionDetail;        
    }
    
    public class InteractionSummaryDTO 
    {
        public string InteractionNumber = '';
        public string CallerName = '';  
        public string InteractingWithType = '';
        public string InteractingWithName = '';                    
        public string InteractingWithFirstName = '';
        public string InteractingWithLastName = '';
        public string InteractingWithEnterpriseId = '';
        public string InteractingWithDOB = '';
        public string InteractionOrigin = '';
        public InteractionMemberDTO[] InteractionMember;
        public string ErrorMessage = '';
    }
    
    public class InteractionDetailsDTO
    {
        public string CaseNumber = ''; 
        public string CaseOrigin = '';   
        public string CaseCreated = ''; 
        public string CaseType = '';   
        public string CaseSubtype = '';    
        public string CaseStatus = '';  
        public string WorkQueueName = '';  
        public string QueueViewName = '';           
    }
        
    public class InteractionMemberDTO 
    {
        public string InteractingAboutType = '';
        public string InteractingAboutName = '';
        public string InteractingAboutFirstName = '';
        public string InteractingAboutLastName = '';
        public string InteractingAboutEnterpriseId = '';
        public string InteractingAboutDOB = '';
    }    
}