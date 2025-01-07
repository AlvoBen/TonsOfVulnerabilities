/******************************************************************************************************************
Apex class Name    : UpdateCaseDetails_DTO_V4_HUM
Version            : 1.0
Function           : This class is used as request for update case rest service.
Created On         : July 30 2019
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Sagar MN										  7/30/2019				Original Version
*   1.1			   Mohammed Noor                                  09/01/2020            US1428444 - PR00088383 - MF6 - Added new fields in the Attachment DTO.
*   1.2			   Moshitha Gunasekaran                           07/23/2021            T1PRJ0001848 - (CSI1955) - Added CaseOwnerId field in CaseDetail DTO.
------------------------------------------------------------------------------------------------------------------- */
public with sharing class UpdateCaseDetails_DTO_V4_HUM {
       
        public String CaseType='';
        public String CaseStatus='';
        public String Origin='';
        public String CaseSubType='';
        public String Priority='';
        public String TopicName='';
        public String WorkQueueViewName='';
        public String CreatedQueue='';
        public String OwnerQueue='';
        public String ServiceCenter='';
        public String Department='';
        public String ClassificationType='';
        public String Classification='';
        public String Intent='';
        public String Complaint='';
        public String ComplaintReason='';
        public String ComplaintType='';
        public String GARightsGiven='';
        public String GAReason='';
        public String FollowupDueDate='';
        public String ElectionType='';
        public CaseComment[] CaseComments;
        public AttachmentLog[] AttachmentLog;
        public Attachment[] Attachment;
        public String OGOResolutionDate='';
        public String OGOResolutionType='';
        public String OralGrievanceCategory='';
        public String OralGrievanceSubCategory='';
        public String CaseOwnerId='';         
    
    public class CaseComment{
        public String CommentBody='';
       // public String ParentId='';
    }
    public class AttachmentLog{
        public String AttachmentDescription='';
        public String AttachmentType='';
        public AttachmentAttribute[] AttachmentAttribute;
        public String AttachmentKey='';
        public String Source='';
       // public String CreatedBy='';
        
        
    }
    public class AttachmentAttribute{
        public String AttachmentName='';
        public String AttachmentValue='';
    }
    public class Attachment{
        public String DocId='';
        public String AttachmentURL='';
        public String FileName='';
        public String VersionNumber='';
		public String AlternateDocId='';
        public String DocumentSource='';
    } 
}