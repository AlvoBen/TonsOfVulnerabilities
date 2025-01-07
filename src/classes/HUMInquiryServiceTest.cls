/*************************************************************************************************************************
Apex Class Name  : HUMInquiryServiceTest
Version          : 1.0
Created Date     : May 05 2015
Function         : Test class to HUMInquiryService class

* Modification Log :
* Developer                Code Review            Date                 Description
* -------------------------------------------------------------------------------------------------------------------------
* Pradeep Veeravali        22934                  05/05/2015           Original Version
* Sumit Pharande           165664                 30/11/2017           Modified as per REQ-338411
***************************************************************************************************************************/

@isTest
private class HUMInquiryServiceTest{
    
     /*
    * Method Name : testInquiryService
    * Description : Test method to InquiryService
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testInquiryService(){
    
    User user = HUMTestDataHelperTest.createTestUser('TestUserGrpSrch1','Customer Care Specialist'); 
    insert user;
    system.assert(user.Id != null);
    insert new HUM_Webservice_Callout__c( Name = 'HUMCreateInquiryService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
    HUM_Webservice_Callout__c oSvcHeaders = HUM_Webservice_Callout__c.getValues('HUMCreateInquiryService');
    system.assert(oSvcHeaders.Id != null);
     
    System.runas(user) 
    {
        Test.setMock(WebServiceMock.class, new HUMInquiryServiceMockResponseTest());
        HUMTestDataHelperTest.getHUMConstantsData();
        Test.StartTest();
        HUMInquiryService.InquiryDTO oInq = new HUMInquiryService.InquiryDTO();
        oInq.AsoIndicator = 'N';
        HUMInquiryService.AlertLogDTOArray arrAlertLog = new HUMInquiryService.AlertLogDTOArray();
        List<HUMInquiryService.AlertLogDTO> lstAlertLog = new List<HUMInquiryService.AlertLogDTO>();
        HUMInquiryService.AlertLogDTO oAlertLog = new HUMInquiryService.AlertLogDTO();
        HUMInquiryService.AlertLogQuestionResponseDTOArray arrAlertLogQuestionResponse = new HUMInquiryService.AlertLogQuestionResponseDTOArray();
        List<HUMInquiryService.AlertLogQuestionResponseDTO> lstAlertLogQuestionResponse = new List<HUMInquiryService.AlertLogQuestionResponseDTO>();
        HUMInquiryService.AlertLogQuestionResponseDTO oAlertLogQuestionResponse = new HUMInquiryService.AlertLogQuestionResponseDTO();
        lstAlertLogQuestionResponse.add(oAlertLogQuestionResponse);
        oAlertLog.QuestionResponseList = arrAlertLogQuestionResponse;
        lstAlertLog.add(oAlertLog);
        arrAlertLog.AlertLogDTO = lstAlertLog;
        oInq.AlertLogs = arrAlertLog;
        HUMInquiryService.AttachmentDTOArray arrAttachment = new HUMInquiryService.AttachmentDTOArray();
        List<HUMInquiryService.AttachmentDTO> lstAttachment = new List<HUMInquiryService.AttachmentDTO>();
        HUMInquiryService.AttachmentDTO oAttachment = new HUMInquiryService.AttachmentDTO();        
        oAttachment.AttachmentDescription = 'Code-AE034B98';
        oAttachment.AttachmentId = Long.valueOf('0');
        oAttachment.AttachmentKey ='Pending Error Codes';
        oAttachment.AttachmentType = 'IE';

            HUMInquiryService.AttachmentAttributeDTOArray arrAttachmentAttr = new HUMInquiryService.AttachmentAttributeDTOArray();
            List<HUMInquiryService.AttachmentAttributeDTO> lstAttachmentAttr = new List<HUMInquiryService.AttachmentAttributeDTO>();
            HUMInquiryService.AttachmentAttributeDTO oAttachmentAttr = new HUMInquiryService.AttachmentAttributeDTO();
            oAttachmentAttr.AttachmentId = Long.valueOf('0');
            oAttachmentAttr.AttributeName = 'AttributeName';
            oAttachmentAttr.AttributeValue = 'Pending Error Codes';
            oAttachmentAttr.ContactId = Long.valueOf('0');
            oAttachmentAttr.CreatedBy = 'MXG0195';
            oAttachmentAttr.CreatedTeamId = Long.valueOf('11');
            oAttachmentAttr.CreatedTimestamp = Datetime.valueOf('2015-04-06 16:58:13');
            oAttachmentAttr.InquiryId = Long.valueOf('0');
            oAttachmentAttr.LastUpdatedBy = 'MXG0195';
            oAttachmentAttr.PartId = Long.valueOf('0');
            oAttachmentAttr.TaskId = Long.valueOf('0');
            lstAttachmentAttr.add(oAttachmentAttr);
            arrAttachmentAttr.AttachmentAttributeDTO = lstAttachmentAttr;
            oAttachment.Attributes = arrAttachmentAttr;
    
        oAttachment.ContactId = Long.valueOf('0');
        oAttachment.CreatedBy = 'MXG0195';
        oAttachment.CreatedTeamId = Long.valueOf('11');
        oAttachment.CreatedTimestamp = Datetime.valueOf('2015-04-06 16:58:13');
        oAttachment.InquiryId = Long.valueOf('0');
        oAttachment.LastUpdatedTimestamp = Datetime.valueOf('2015-04-06 16:58:13');
        oAttachment.PartId = Long.valueOf('0');
        oAttachment.TaskId = Long.valueOf('0');
        lstAttachment.add(oAttachment);
        arrAttachment.AttachmentDTO = lstAttachment;
        oInq.Attachments = arrAttachment;
        oInq.CategoryReasonDispositionCode = '19';

        HUMInquiryService.ContactDTO oCon = new HUMInquiryService.ContactDTO();
        oCon.ContactId = Long.valueOf('0');
        oCon.CreatedBy = 'MXG0195';
        oCon.CreatedTimestamp = DateTime.valueOf('2015-04-06 16:58:13');
        oCon.LastUpdatedTimestamp = DateTime.valueOf('2015-04-06 16:58:13');
        oCon.MediaTypeCode = 'OT';
        oCon.PartId = Long.valueOf('0');
        oInq.Contact = oCon;

        oInq.ContactId = Long.valueOf('0');
        oInq.CreatedBy = 'MXG0195';
        oInq.CreatedTeamId = Long.valueOf('11');
        oInq.CreatedTimestamp = DateTime.valueOf('2015-04-06 16:58:13');
        HUMInquiryService.GNASummaryDTO oGNASummary = new HUMInquiryService.GNASummaryDTO();
        oInq.GnaSummaryDTO = oGNASummary;
        oInq.InqAGenKey = Long.valueOf('1008437051');
        oInq.InqAEnterpriseId='1008437051';
        oInq.InqAId = 'H0027428400';
        oInq.InqASubType = '';
        oInq.InqATypeCode = 'KM';
        oInq.InqRId = 'H0027428400';
        oInq.InqRGenKey = Long.valueOf('1008437051');
        oInq.InqREnterpriseId='1008437052';
        oInq.InqRTypeCode = 'KM';
        oInq.InquiryId =Long.valueOf('0');
        oInq.InquirySubType = 'CI';
        oInq.InquiryType = 'CI';
        oInq.LastUpdatedTeamId =Long.valueOf('11');
        oInq.LastUpdatedTimestamp = DateTime.valueOf('2015-04-06 16:58:13');

        HUMInquiryService.NoteDTOArray arrNotes = new HUMInquiryService.NoteDTOArray();
        List<HUMInquiryService.NoteDTO> lstNotes = new List<HUMInquiryService.NoteDTO>();
        HUMInquiryService.NoteDTO oNote = new HUMInquiryService.NoteDTO();
        oNote.ContactId = Long.valueOf('0');
        oNote.CreatedBy = 'MXG0195';
        oNote.CreatedTeamId = Long.valueOf('11');
        oNote.CreatedTimestamp = DateTime.valueOf('2015-04-06 16:58:13');
        oNote.FirstName ='Moshitha';
        oNote.InquiryId = Long.valueOf('0');
        oNote.LastName = 'Gunasekaran';
        oNote.NoteDescription ='Testing notes';
        oNote.NoteId = Long.valueOf('0');
        oNote.PartId = Long.valueOf('0');
        oNote.TaskId = Long.valueOf('0');
        oNote.UserSiteName = 'Green Bay';
        lstNotes.add(oNote);
        arrNotes.NoteDTO = lstNotes;
        oInq.Notes = arrNotes;

        oInq.OwnerTeamId =Long.valueOf('11');
        oInq.PartId =Long.valueOf('0');
        oInq.PriorityCode = 'LL';
        //oInq.SellingLedger = Long.valueOf('');
        oInq.StatusCode = 'OP';

        HUMInquiryService.TaskDTOArray arrTask = new HUMInquiryService.TaskDTOArray();
        List<HUMInquiryService.TaskDTO> lstTasks = new List<HUMInquiryService.TaskDTO>();
        HUMInquiryService.TaskDTO oTask = new HUMInquiryService.TaskDTO();
        oTask.ActionCode = '424';
        oTask.ActionCodeDescription ='';
        oTask.ActionCompletedDate = DateTime.valueOf('0001-01-01 00:00:00');
        oTask.ActionCompletedTime = 'PT0S';
        oTask.ActionDate = DateTime.valueOf('2015-04-06 00:00:00');
        oTask.ActionTime ='PT0S';
        oTask.ClosedTimestamp = DateTime.valueOf('0001-01-01 00:00:00');
        oTask.ContactId = Long.valueOf('0');
        oTask.CreatedBy = 'MXG0195';
        oTask.CreatedFirstName = 'Moshitha';
        oTask.CreatedLastName = 'Gunasekaran';
        oTask.CreatedTeamId = Long.valueOf('11');
        oTask.CreatedTeamName = 'CCP IT Support Team';
        oTask.CreatedTimestamp = DateTime.valueOf('2015-04-06 16:58:13');  
        oTask.DueDate =DateTime.valueOf('9999-09-09 00:00:00');  
        oTask.InquiryId = Long.valueOf('0');
        oTask.InquirySubType ='CI';
        oTask.InquiryType ='CI';
        oTask.IntegrationStatus ='NI';
        oTask.LastUpdatedBy ='MXG0195';
        oTask.LastUpdatedFirstName ='Moshitha';
        oTask.LastUpdatedLastName = 'Gunasekaran';
        oTask.LastUpdatedTeamId = Long.valueOf('11'); 
        oTask.LastUpdatedTeamName ='CCP IT Support Team';
        oTask.LastUpdatedTimestamp = DateTime.valueOf('2015-04-06 16:58:13');  
        oTask.OwnerFirstName='';
        oTask.OwnerId='';
        oTask.OwnerLastName='';
        oTask.OwnerTeamId=Long.valueOf('11'); 
        oTask.OwnerTeamName='CCP IT Support Team';
        oTask.PartId=Long.valueOf('0'); 
        oTask.PriorityCode='LL';
        oTask.PriorityCodeDescription='Normal';
        oTask.ResultCode ='96';
        oTask.ResultCodeDescription ='?';
        oTask.StatusCode = 'OP';
        oTask.StatusCodeDescription ='Open';
        oTask.TaskId =Long.valueOf('0'); 
        oTask.TaskNotes ='Testing task notes';
        oTask.TaskType ='VC';
        oTask.TaskTypeDescription ='';
        HUMInquiryService.NoteDTO oTaskNote = new HUMInquiryService.NoteDTO();
        oTaskNote.ContactId = Long.valueOf('0');
        oTaskNote.CreatedBy = 'MXG0195';
        oTaskNote.CreatedTeamId = Long.valueOf('11');
        oTaskNote.CreatedTimestamp = DateTime.valueOf('2015-04-06 16:58:13');
        oTaskNote.FirstName ='Moshitha';
        oTaskNote.InquiryId = Long.valueOf('0');
        oTaskNote.LastName = 'Gunasekaran';
        oTaskNote.NoteDescription ='Task notes';
        oTaskNote.NoteId = Long.valueOf('0');
        oTaskNote.PartId = Long.valueOf('0');
        oTaskNote.TaskId = Long.valueOf('0');
        oTaskNote.UserSiteName = 'Green Bay';
        oTask.NoteDTO = oTaskNote;
        HUMInquiryService.TaskReplyTypeDTOArray arrOTaskReplyType = new HUMInquiryService.TaskReplyTypeDTOArray();
        List<HUMInquiryService.TaskReplyTypeDTO> lstTaskRepType = new List<HUMInquiryService.TaskReplyTypeDTO>();
        HUMInquiryService.TaskReplyTypeDTO oTaskReplyType = new HUMInquiryService.TaskReplyTypeDTO();
        lstTaskRepType.add(oTaskReplyType);
        arrOTaskReplyType.TaskReplyTypeDTO = lstTaskRepType;
        oTask.AllReplyTypewithTrackId = arrOTaskReplyType;
        lstTasks.add(oTask);
        arrTask.TaskDTO =lstTasks;
        oInq.Tasks = arrTask;

        oInq.OpenCloseIndicator ='O';
        HUMInquiryService.ClinicalCaseDTOArray arrClinicalCase = new HUMInquiryService.ClinicalCaseDTOArray();
        List<HUMInquiryService.ClinicalCaseDTO> lstClinicalCase = new List<HUMInquiryService.ClinicalCaseDTO>();
        HUMInquiryService.ClinicalCaseDTO oClinicalCase = new HUMInquiryService.ClinicalCaseDTO();
        HUMInquiryService.PartnerDetailDTOArray arrPartnerDetail = new HUMInquiryService.PartnerDetailDTOArray();
        List<HUMInquiryService.PartnerDetailDTO> lstPartnerDetail = new List<HUMInquiryService.PartnerDetailDTO>();
        HUMInquiryService.PartnerDetailDTO oPartnerDetail = new HUMInquiryService.PartnerDetailDTO();
        lstPartnerDetail.add(oPartnerDetail);
        arrPartnerDetail.PartnerDetailDTO =  lstPartnerDetail;               
        oClinicalCase.PartnerDetailDTO = arrPartnerDetail;
        lstClinicalCase.add(oClinicalCase);
        arrClinicalCase.ClinicalCaseDTO = lstClinicalCase;
        oInq.ClinicalCaseDTO = arrClinicalCase;
        List<HUMInquiryService.InquiryDTO> lstInq = new  List<HUMInquiryService.InquiryDTO>();
        lstInq.add(oInq);
        HUMInquiryService.SaveResponse_element saveRes = new HUMInquiryService.SaveResponse_element();
        List<HUMInquiryService.ResponseDTO> response = new List<HUMInquiryService.ResponseDTO>();
        
        HUMInquiryService.soap oCreateInq = new HUMInquiryService.soap(oSvcHeaders.End_Point_URL__c, oSvcHeaders.Certificate_Name__c, Integer.valueOf(oSvcHeaders.Timeout__c));       
        response = oCreateInq.save(lstInq);        
        system.assert(response != null);  
        Test.stopTest(); 
      }
    }
}