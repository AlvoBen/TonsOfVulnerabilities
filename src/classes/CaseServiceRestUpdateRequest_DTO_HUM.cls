/******************************************************************************************************************
Apex class Name    : CaseServiceRestUpdateRequest_DTO_HUM
Version            : 1.0
Function           : This class is used as request for update case Rest Service UpdateCaseInfoRest_D_HUM.
Created On         : March 21 2018
Test Class         : HUMCaseServiceRestTest

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran        215366              03/21/2018          Original Version
*   2.0            Srivinas Damera             229608              05/16/2018          CaseUpdateRequest Attachmentlog changed to List
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CaseServiceRestUpdateRequest_DTO_HUM
{
public UpdateCaseInfoDTO UpdateCaseInfo;
    public class UpdateCaseInfoDTO {
        public CaseUpdateRequestDTO CaseUpdateRequest;
    }
    public class CaseUpdateRequestDTO {
        public AttachmentDTO[] AttachmentLog;
        public lstAttachmentDTO[] Attachment;
        public String CaseNumber ='';  //1000000019476
        public String CaseStatus = '';  //C
        public String Comment = ''; // 
        public String OwnerId = ''; // 
        public String QueueName = '';   //1
    }
    public class AttachmentDTO {
        public AttachmentAttributeDTO[] AttachmentAttribute;
        public String AttachmentDescription = '';   //Exchange-Off;Exchange Type-N/A;
        public String AttachmentKey = '';   //80000044023170000193
        public String AttachmentType = '';  //EX
        public String CreatedBy = '';   //TXS0700
    }
    public class AttachmentAttributeDTO {
        public String AttachmentName = '';  //Exchange
        public String AttachmentValue = ''; //Off
    }
    public class lstAttachmentDTO {
        public String AttachmentUrl = '';   //test.com
        public String FileName = '';    //test
    }
}