/******************************************************************************************************************
Apex class Name    : CaseServiceRestGetResponse_DTO_HUM_V2
Version            : 2.0
Function           : This class is used as rspnse for get case Rest Service  GetCaseInfoRest_D_HUM.
Created On         : Jan 20 2019
Test Class         : HUMCaseServiceRestTest_V2

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
    1.0           Divya Proddaturi              340085           2/27/2019            Original Version
    1.1           Mohammed Noor                 345152           03/14/2019            Changes done based on ICD Updates.   
    1.1           Moshitha Gunasekaran                          04/02/2019           REQ - 392822 Tech - SF - Enhance GetCaseInfo method of Case Service
    1.2           Mohammed Noor                                 07/01/2019           Changes done for REQ - 403693.
    1.3			  Mohammed Noor                                 09/01/2020           US1428444 - PR00088383 - MF6 - Added new fields in the Attachment DTO.	
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CaseServiceRestGetResponse_DTO_HUM_V2
{
public GetCaseInfoResponseDTO GetCaseInfoResponse;
    public class GetCaseInfoResponseDTO {
        public String CaseCount = '';    
        public CaseInfoResponseDTO[] CaseInfoResponseList;
    }
    public class CaseInfoResponseDTO {
        public CaseSummaryDTO CaseSummary;
        public CaseDetailsDTO CaseDetail;
        public AttachmentDTO[] Attachment;
        public AttachmentLogDTO[] AttachmentLog;
        public InteractionDTO[] Interaction;
        public TaskDTO[] Tasks;        
        public CaseCommentDTO[] CaseComments;
        public ProcessSummaryDTO[] ProcessSummary;
        
    }
    public class CaseSummaryDTO
    {
        public String CaseId ='';  
        public String Status = '';  
        public String SubType = ''; 
        public String Type = '';    
        public String CaseOrigin = '';  
        public String WorkQueueName = '';
        public String QueueViewName = '';
        public String CaseCreated=''; 
        public String ErrorMessage = ''; 
    }
    public class CaseDetailsDTO{
        public String AccountName =''; 
        public String BehavioralHealth = '';   
        public String CaseAge = ''; 
        public String CaseOwner = '';   
        public String Classification = '';    
        public String ClassificationType = '';  
        public boolean Closed;  
        public boolean ClosedWhenCreated;   
        public String Complaint = '';   
        public String ComplaintReason = ''; 
        public String ContactId = ''; 
        public String CreatedBy = '';
        public String CreatedByQueue = '';  
        public String DateTimeClosed = '';  
        public String DaysSinceLastModified = '';   
        public String Department = '';  
        public String DueDate = ''; 
        public String EmployerGroupID = '';  
        public boolean Escalated;   
        public String Exchange = '';    
        public String ExchangeType = '';    
        public String FollowUpDueDate = ''; 
        public boolean FollowUpRequired;    
        public String FundingType = ''; 
        public String GAReason = '';    
        public String GARightsGiven = '';  
        public boolean GeneralCase; 
        public boolean HODentalCase;    
        public boolean HOMedicalCase;   
        public boolean HumanaEmployee;  
        public String InteractingAboutID = '';    
        public String InteractingAboutType = '';
        public UnknownAccountDTO InteractingAboutUnknownAccount;  
        public String InteractingWithID = ''; 
        public String InteractingWithName = ''; 
        public String InteractingWithType = ''; 
        public UnknownAccountDTO InteractingWithUnknownAccount;
        public String LanguagePreference = '';  
        public String LastModifiedBy = '';  
        public String LastModifiedByQueue = ''; 
        public String LineOfBusiness = '';  
        public String NPIID = '';   
        public String OwnerQueue = '';
        public String PlanIssueState = '';  
        public String PlanSegmentSize = ''; 
        public String PolicyMemberId = '';    
        public String PolicyPlatform = '';  
        public String PreviousCaseOwnerRole = '';   
        public String Priority = '';    
        public String Product = ''; 
        public String ProductType = ''; 
        public String Intent = '';  
        public String ReOpenCaseAge = '';   
        public String ReOpenCaseDate = '';  
        public boolean Reopened;    
        public String SellingMarketNumber = ''; 
        public String ServiceCenter = '';   
        public String StateMemResidAdd = '';    
        public String Subject = ''; 
        public String Success = ''; 
        public String TaxID = '';   
        public boolean TexasComplaintletterRequested;   
        public String Topic = '';   
    }
    public class AttachmentDTO {
        public String AttachmentURL = '';
        public String FileName = '';
        public String VersionNumber = ''; 
        public String DocId = '';
		public String AlternateDocId = '';
        public String DocumentSource = '';
    }
    public class AttachmentLogDTO {
        public AttachmentAttributeDTO[] AttachmentAttribute;
        public String AttachmentDescription = '';
        public String AttachmentKey = '';
        public String AttachmentType = '';
        public String LoggedBy = '';
        public String LoggedDateTime = ''; 
        public String Source = '';      
    }
    public class AttachmentAttributeDTO {
        public String AttachmentName = '';
        public String AttachmentValue = '';
    }
    public class InteractionDTO {
        public String CallerName = '';  
        public String InteractingWithType = ''; 
        public String InteractionOrigin = '';   
        public String InteractionId='';
        public String InteractingWithName = '';
        public String InteractingWithFirstName='';
        public String InteractingWithLastName='';
        public String InteractingWithEnterpriseId='';
        public String InteractingWithDOB='';
        public InteractionMemberDTO[] InteractionMember;
    }
    public class InteractionMemberDTO 
    {
        public String InteractingAboutType='';
        public String InteractingAboutName = '';
        public String InteractingAboutFirstName='';
        public String InteractingAboutLastName='';
        public String InteractingAboutEnterpriseId='';
        public String InteractingAboutDOB='';
    }
    public class TaskDTO {
        public String AgeSinceCreated = ''; 
        public String CreatedBy = '';   
        public String CreatedByQueue = '';  
        public String Description = ''; 
        public String DueDate = ''; 
        public String LastModifiedBy = '';  
        public String LastModifiedByAge = '';   
        public String LastModifiedByQueue = ''; 
        public String OwnerQueue = '';  
        public String Priority = '';    
        public String Status = '';  
        public String Subject = ''; 
        public String TaskID = '';  
        public String TaskOwner = '';   
    }
    public class CaseCommentDTO
    {
        public String CommentBody = ''; 
        public String CreatedBy = ''; 
        public String CreatedDate = ''; 
        public String LastModified = '';    
        public String LastModifiedDate = '';    
    }
    public class ProcessSummaryDTO{
    public ProcessDetailDTO[] ProcessDetail;
    
        public String ProcessName='';
        public String ProcessVersion='';
        public String ProcessCreatedDate='';
        public String ProcessCreatedBy='';
        public String ProcessLastModifiedDate='';
        public String ProcessLastModifiedBy='';
        
    }
    public Class ProcessDetailDTO{
       public String FieldName='';  
       public String FieldValue='';  
       public String RowNumber='';

    }
    public class UnknownAccountDTO
    {
        public String DOB='';
        public AccountIdentifierDTO[] AccountIdentifier;
    }
    public class AccountIdentifierDTO
    {
       public String IDType='';  
       public String ID='';  
    }

}