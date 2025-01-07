/******************************************************************************************************************
Apex class Name    : CaseServiceRestCreateRequest_DTO_HUM
Version            : 1.0
Function           : This class is used as request for create case Rest Service  CreateCaseRest_D_HUM.
Created On         : March 14 2018
Test Class         : HUMCaseServiceRestTest

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran        215366              03/14/2018          Original Version
*   1.1            Moshitha Gunasekaran        224503              05/02/2018          REQ - 350862 - Fix to process Omni Routing rules for asynchrnous call from ETL API Acess profile
*   1.2            Moshitha Gunasekaran        292111              08/09/2018          Added Attachment Log and Attachment Attribute details in request
*   1.3 	       Rakesh Patni									   10/19/2018		   REQ - 375710 - Added New Nodes.
*   1.4            Melkisan Selvaraj                               11/05/2018          REQ - 378853 - Removed data elements which are not used anymore
*   1.5			   Charan Konkandla								   11/28/2018          Added data elements based on new version of ICD and removed unused nodes.
*   1.6             Shailesh Mali 								   02/15/2019          Added class for Attachment.
*   1.7             Moshitha Gunasekaran                           02/04/2020          US754949 - Ability for expanded the population of cases containing Case Initiated Date for Medicare Enrollment processing
*   1.8            Sumanth Naredla                                 07/09/2020          1226831 - PR00093202 - TECH - Change Request# CR-012 - Image Gap (Long Term Solution - File To to CRM August Requirement)
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CaseServiceRestCreateRequest_DTO_HUM
{
	public CreateCaseRequestDTO CreateCaseRequest;
    public class CreateCaseRequestDTO 
    {
        public String AccountErrMsg = '';
        public boolean AsynchronousProcess = false;
        public List<AttachmentLogDTO> AttachmentLog = null;
		public List<AttachmentDTO> Attachment = null;
        public String CaseCommentErrMsg = '';
        public CaseCommentsList CaseComments;
        public String CaseInitiatedDate = '';
        public String Classification = '';   
        public String ClassificationType = '';   
        public String ClosedDate = '';   
        public String Complaint = '';    
        public String ComplaintReason = '';  
        public String ComplaintType = '';    
        public String Contract = ''; 
        public String CreatedQueue = ''; 
        public String DCN = '';
        public String DCNType = '';
        public String DCNURL = '';  
        public String Department = '';   
        public String DocType = '';  
        public String ElectionType = ''; 
        public String ExternalTrackId = '';  
        public String FollowupDueDate = '';  
        public String GAReason = ''; 
        public String GARightsGiven = '';   
        public String CaseSubType = '';  
        public String Intent = '';   
        public String InteractingAboutAddressLine1 = ''; 
        public String InteractingAboutCity = ''; 
        public String InteractingAboutDOB = '';  
        public String InteractingAboutEmailID = '';  
        public String InteractingAboutFirstName = '';    
        public String InteractingAboutHomePhone = '';    
        public String InteractingAboutId = '';   
        public String InteractingAboutIdType = '';  
        public String InteractingAboutLastName = ''; 
        public String InteractingAboutState = '';    
        public String InteractingAboutType = ''; 
        public String InteractingAboutZipCode = '';
        public String InteractingAboutProviderClassification = '';
        public String InteractingAboutName = ''; 
        public String InteractingWithAddressLine1 = '';  
        public String InteractingWithCity = '';  
        public String InteractingWithDOB = '';  
        public String InteractingWithEmailID = '';   
        public String InteractingWithFirstName = ''; 
        public String InteractingWithHomePhone = ''; 
        public String InteractingWithId = '';    
        public String InteractingWithIdType = '';    
        public String InteractingWithLastName = '';  
        public String InteractingWithState = ''; 
        public String InteractingWithType = '';  
        public String InteractingWithZipCode = ''; 
        public String InteractingWithProviderClassification = '';
        public String InteractingWithName = '';  
        public String MbrSignDate = '';  
        public String MemberSSN = '';    
        public String MilestoneId = '';  
        public String Origin = '';   
        public String OwnerQueue = '';   
        public String PBP = '';  
        public String PendKey = '';  
        public String PolicyErrMsg = '';
        public String Priority = ''; 
        public String RecordTypeID = '';
        public String SegmentNumber = '';  
        public String ServiceCenter = '';   
        public String SourceApp = '';   
        public String SourceIdTypeCd = '';  
        public String SourceKey = '';   
        public String CaseStatus = '';   
        public String TopicName = '';   
        public String CaseType = ''; 
        public String WorkQueueViewName = '';
        public String PolicyMember = '';
        public String EffectiveDate = '';
        public String EndDate = '';
        public String PolicyGroupID = ''; 
        
    }
    public class CaseCommentsList 
    {
        public List<String> CaseComment = null;
    }
    public class AttachmentLogDTO 
    {
        public AttachmentAttributeDTO[] AttachmentAttribute;
        public String AttachmentDescription = '';  
        public String AttachmentKey = '';   
        public String AttachmentType = ''; 
        public String CreatedBy = '';
        public String Source = '';
    }
    public class AttachmentAttributeDTO 
    {
        public String AttachmentName = ''; 
        public String AttachmentValue = ''; 
    }
	
	public class AttachmentDTO{
    	public String AttachmentUrl= '';
        public String FileName= '';    	
    	public String VersionId = '';
        public String DocId = '';
    }
}