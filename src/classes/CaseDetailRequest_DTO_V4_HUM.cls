/******************************************************************************************************************
Apex class Name    : CaseDetailRequest_DTO_V4_HUM
Version            : 1.0
Function           : This class is used as request for create case Rest Service  CreateCaseRest_D_HUM.
Created On         : March 11 2019
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Sudhendrasimha
*   1.1            Asish Behera                                   08/25/2019           Introduced new param to handle default policy.
*   1.2            Moshitha Gunasekaran                           02/04/2020           US754949 - Ability for expanded the population of cases containing Case Initiated Date for Medicare Enrollment processing
*   1.3            Sumanth Naredla                                06/30/2020           1226831 PR00093202 - TECH - Change Request# CR-012 - Image Gap (Long Term Solution - File To to CRM August Requirement)
*   1.4			   Mohammed Noor                                  09/01/2020           US1428444 - PR00088383 - MF6 - Added new fields in the Attachment DTO.
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CaseDetailRequest_DTO_V4_HUM{  
    
        public String CaseType{get;set;}
        public String CaseInitiatedDate{get;set;}
        public String CaseStatus{get;set;}
        public String Origin{get;set;}
        public String CaseSubType{get;set;}
        public String Priority{get;set;}
        public String TopicName{get;set;}
        public String WorkQueueViewName{get;set;}
        public String CreatedQueue{get;set;}
        public String OwnerQueue{get;set;}
        public String ServiceCenter{get;set;}
        public String Department{get;set;}
        public String ClassificationType{get;set;}
        public String Classification{get;set;}
        public String Intent{get;set;}
        public String Complaint{get;set;}
        public String ComplaintReason{get;set;}
        public String ComplaintType{get;set;}
        public String GARightsGiven{get;set;}
        public String GAReason{get;set;}
        public String FollowupDueDate{get;set;}
        public String ClosedDate{get;set;}
        public String MemberSignDate{get;set;}
        public String PendKey{get;set;}
        public String Contract{get;set;}
        public String PBPCode{get;set;}
        public String SegmentNumber{get;set;}
        public String DCN{get;set;}
        public String DCNType{get;set;}
        public String DCNURL{get;set;}
        public String DocType{get;set;}
        public String ExternalTrackId{get;set;}
        public String MilestoneId{get;set;}
        public String SourceIdTypeCd{get;set;}
        public String ElectionType{get;set;}
        public String LOB{get;set;}        
        public String PolicyMember{get;set;}
        public String EffectiveDate{get;set;}
        public String EndDate{get;set;}
        public String PolicyGroupID{get;set;}
        public list<CaseComment> CaseComments{get;set;}
        public list<AttachmentLog> AttachmentLog{get;set;}
        public list<Attachment> Attachment{get;set;}
        public boolean AsynchronousProcess = false; 
        public Boolean MedicareDefaultPolicy;
        public String CaseOwnerId;
        
       
    public class CaseComment{
        public String CommentBody{get;set;}
        public String ParentId{get;set;}
    }
    public class AttachmentLog{
        public String AttachmentDescription{get;set;}
        public String AttachmentType{get;set;}
        public list<AttachmentAttribute> AttachmentAttribute{get;set;}
        public String AttachmentKey{get;set;}
        public String Source{get;set;}
        public String CreatedBy{get;set;}
        
        
    }
    public class AttachmentAttribute{
        public String AttachmentName{get;set;}
        public String AttachmentValue{get;set;}
    }
    public class Attachment{
        public String DocId{get;set;}
        public String AttachmentURL{get;set;}
        public String FileName{get;set;}
        public String VersionNumber{get;set;}
		public String AlternateDocId{get;set;}
        public String DocumentSource{get;set;}
    }    
    
}