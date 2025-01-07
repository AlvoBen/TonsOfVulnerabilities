/******************************************************************************************************************
Apex class Name    : CaseServiceRestGetResponse_DTO_HUM
Version            : 1.0
Function           : This class is used as request for get case Rest Service  GetCaseInfoRest_D_HUM.
Created On         : March 20 2018
Test Class         : HUMCaseServiceRestTest

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*  1.0             Moshitha Gunasekaran        215366                    03/20/2018          Original Version
*  1.1             Srivinas Damera                                 9/26/2018           REQ - 374689 - Update CaseService V1 and V3 - GetCaseInfo method     
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CaseServiceRestGetResponse_DTO_HUM
{
public GetCaseInfoResponseDTO GetCaseInfoResponse;
    public class GetCaseInfoResponseDTO {
        public String CaseCount;    //0
        public CaseInfoResponseDTO[] CaseInfoResponseList;
    }
    
    public class CaseInfoResponseDTO {
        public String AccountName =''; //LECCA GRAMLING
        public boolean ApplyToComplaint;    //false
        public AttachmentDTO[] AttachmentList;
        public AttachmentLogDTO[] AttachmentLogList;
        public String BehavioralHealth = '';    //
        public String CaseAge = ''; //7
        public CaseCommentDTO[] CaseCommentsList;
        public String CaseNumber = '';  //1000000286306
        public String CaseOrigin = '';  //Inbound Call
        public String CaseOwner = '';   //Sumit Pharande
        public String CaseReason = '';  //
        public String CaseTAT = ''; //
        public String Category = '';    //Eligibility
        public String ClassificationType = '';  //Enrollment (RSO)
        public String Closed = '';  //false
        public String ClosedWhenCreated = '';   //false
        public String CoKYCase = '';    //false
        public String Complaint = '';   //
        public String ComplaintReason = ''; //
        public String ContactName = ''; //
        public String CreatedBy = '';   //Sumit Pharande
        public String CreatedByQueue = '';  //Louisville RSO Enrollment
        public String DateTimeClosed = '';  //0001-01-01T05:00:00.000Z
        public String DateTimeOpened = '';  //2018-03-12 06:02:51
        public String DaysSinceLastModified = '';   //7
        public String Department = '';  //Retail Service Operations (RSO) Enrollment
        public String Description = ''; //
        public String DSTCase = ''; //false
        public String DueDate = ''; //0001-01-01
        public String EmailSent = '';   //false
        public String EmployerGroupName = '';   //
        public String ErrorCode = '';   //
        public String ErrorMessage = '';    //
        public String Escalated = '';   //false
        public String Exchange = '';    //
        public String ExchangeType = '';    //
        public String FollowUpDueDate = ''; //
        public String FollowUpRequired = '';    //false
        public String FundingType = ''; //
        public String GAReason = '';    //
        public String GARightsGiven = '';   //
        public String GeneralCase = ''; //true
        public String HODentalCase = '';    //false
        public String HOMedicalCase = '';   //false
        public String HumanaEmployee = '';  //false
        public String InteractingAbout = '';    //7384003072901
        public String InteractingAboutFullName = '';    //LECCA GRAMLING
        public String InteractingAboutType = '';    //Member
        public String InteractingWith = ''; //7384003072901
        public String InteractingWithFullName = ''; //LECCA GRAMLING
        public String InteractingWithName = ''; //
        public String InteractingWithType = ''; //Member
        public InteractionDTO Interaction;
        public String InteractionID = '';   //
        public String InternalComments = '';    //
        public String LanguagePreference = '';  //
        public String LastModifiedBy = '';  //Sumit Pharande
        public String LastModifiedByQueue = ''; //
        public String LineOfBusiness = '';  //
        public String NPIID = '';   //
        public String ParentCase = '';  //Commercial
        public String PlanIssueState = '';  //
        public String PlanSegmentSize = ''; //
        public String PolicyMember = '';    //
        public String PolicyPlatform = '';  //
        public String PreviousCaseOwnerRole = '';   //Developer
        public String Priority = '';    //Normal
        public String Product = ''; //
        public String ProductType = ''; //
        public String Queue = '';   //
        public String QueueOwner = '';  //Louisville RSO Enrollment
        public String Reason = '';  //Term/Cancel Member
        public String ReOpenCaseAge = '';   //0
        public String ReOpenCaseDate = '';  //
        public String Reopened = '';    //false
        public String SellingMarketNumber = ''; //
        public String ServiceCenter = '';   //Louisville
        public String StateMemResidAdd = '';    //TN
        public String Status = '';  //In Progress
        public String Subject = ''; //
        public String SubType = ''; //
        public String Success = ''; //
        public TaskDTO[] TaskList;
        public String TaskTAT = ''; //
        public String TaxID = '';   //
        public String TenantId = '';    //0
        public String TexasComplaintletterRequested = '';   //
        public String Topic = '';   //
        public String TXComplaintRelatedMessage = '';   //
        public String Type = '';    //Customer Inquiry
        public String WorkQueueViewName = '';   //Other
    }
    public class AttachmentDTO {
        public String AttachmentURL = '';
        public String FileName = '';
		public String VersionId = '';
        public String DocId = '';
    }
    public class AttachmentLogDTO {
        public AttachmentAttributeDTO[] AttachmentAttribute;
        public String AttachmentDescription = '';
        public String AttachmentKey = '';
        public String AttachmentType = '';
        public String LoggedBy = '';
        public String LoggedDateTime = '';
        public String UserSelected = '';
        public String UserSelectedExtn = '';
    }
    public class AttachmentAttributeDTO {
        public String AttachmentLog = '';
        public String AttachmentName = '';
        public String AttachmentValue = '';
        public String AttachmentRequest_Id = ''; // Srini added for MRM request
        public String AttachmentDocumentId = '';// Srini added for MRM request
    }
    public class InteractionDTO {
        public String CallerName = '';  //
        public String InteractingWithType = ''; //
        public String InteractionOrigin = '';   //
    }
    public class TaskDTO {
        public String AgeSinceCreated = ''; //
        public String Comments = '';    //
        public String CreatedBy = '';   //
        public String CreatedByQueue = '';  //
        public String Description = ''; //
        public String DueDate = ''; //
        public String LastModifiedBy = '';  //
        public String LastModifiedByAge = '';   //
        public String LastModifiedByQueue = ''; //
        public String OwnerQueue = '';  //
        public String Priority = '';    //
        public String Status = '';  //
        public String Subject = ''; //
        public String TaskNumber = '';  //
        public String TaskOwner = '';   //
    }
    public class CaseCommentDTO
    {
        public String CommentBody = ''; //
        public String CreatedById = ''; //
        public String CreatedByName = '';   //
        public String CreatedDate = ''; //
        public String LastModifiedById = '';    //
        public String LastModifiedDate = '';    //
    }

}