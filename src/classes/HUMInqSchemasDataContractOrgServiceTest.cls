/***************************************************************************************************************************************************************************
Apex Class Name  : HUMInqSchemasDataContractOrgServiceTest
Version          : 1.0
Created Date     : Aug 18 2014
Function         : Test Class for HUMInqSchemasDataContractOrgService
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name        Code Review                Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Vishal Verma           18313                     08/18/2014                    Original Version
****************************************************************************************************************************************************************************/

@isTest
Public Class HUMInqSchemasDataContractOrgServiceTest
{
    
    /*
    * Method name : testHInqSchemasDataContractOrgService
    * Description : This method is used to test HUMInqSchemasDataContractOrgService .
    * Return Type : Void
    * Parameter   : NA 
    */
    
    public static testMethod void testHInqSchemasDataContractOrgService()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
            Test.StartTest();        
            HUMInqSchemasDataContractOrgService inqService = new HUMInqSchemasDataContractOrgService();
            HUMInqSchemasDataContractOrgService.PartnerDetailDTO partnerDetailDTO = new HUMInqSchemasDataContractOrgService.PartnerDetailDTO();
            System.assert(partnerDetailDTO.EndDate == null);
            
            HUMInqSchemasDataContractOrgService.ClinicalCaseDTO clinicalCaseDTO = new HUMInqSchemasDataContractOrgService.ClinicalCaseDTO();
            System.assert(clinicalCaseDTO.CaseType == null);

            HUMInqSchemasDataContractOrgService.ArrayOfClinicalCaseDTO arrayOfClinicalCaseDTO = new HUMInqSchemasDataContractOrgService.ArrayOfClinicalCaseDTO();

            HUMInqSchemasDataContractOrgService.ArrayOfPartnerDetailDTO arrayOfPartnerDetailDTO = new HUMInqSchemasDataContractOrgService.ArrayOfPartnerDetailDTO();

            HUMInqSchemasDataContractOrgService.ClientMetaDataDTO clientMetaDataDTO = new HUMInqSchemasDataContractOrgService.ClientMetaDataDTO();
            System.assert(clientMetaDataDTO.ClientName == null);

            HUMInqSchemasDataContractOrgService.TaskDTO taskDTO = new HUMInqSchemasDataContractOrgService.TaskDTO();
            System.assert(taskDTO.ActionCode == null);

            HUMInqSchemasDataContractOrgService.AlertLogDTO alertLogDTO = new HUMInqSchemasDataContractOrgService.AlertLogDTO();
            System.assert(alertLogDTO.AlertId == null);

            HUMInqSchemasDataContractOrgService.ArrayOfAlertLogDTO arrayOfAlertLogDTO = new HUMInqSchemasDataContractOrgService.ArrayOfAlertLogDTO();
            
            HUMInqSchemasDataContractOrgService.ContactDTO contactDTO = new HUMInqSchemasDataContractOrgService.ContactDTO();
            System.assert(contactDTO.ContactTypeCode == null);

            HUMInqSchemasDataContractOrgService.ArrayOfAlertLogQuestionResponseDTO arrayOfAlertLogQuestionResponseDTO = new HUMInqSchemasDataContractOrgService.ArrayOfAlertLogQuestionResponseDTO();

            HUMInqSchemasDataContractOrgService.ArrayOfNoteDTO arrayOfNoteDTO = new HUMInqSchemasDataContractOrgService.ArrayOfNoteDTO();

            HUMInqSchemasDataContractOrgService.AttachmentAttributeDTO attachmentAttributeDTO = new HUMInqSchemasDataContractOrgService.AttachmentAttributeDTO();
            System.assert(attachmentAttributeDTO.AttachmentId == null);

            HUMInqSchemasDataContractOrgService.ArrayOfAuditLogDTO arrayOfAuditLogDTO = new HUMInqSchemasDataContractOrgService.ArrayOfAuditLogDTO();

            HUMInqSchemasDataContractOrgService.TaskReplyTypeDTO taskReplyTypeDTO = new HUMInqSchemasDataContractOrgService.TaskReplyTypeDTO();
            System.assert(taskReplyTypeDTO.PBPCode == null);

            HUMInqSchemasDataContractOrgService.AttachmentDTO attachmentDTO = new HUMInqSchemasDataContractOrgService.AttachmentDTO();
            System.assert(attachmentDTO.ActualValue == null);
            
            HUMInqSchemasDataContractOrgService.InquirySearchRequestDTO inquirySearchRequestDTO = new HUMInqSchemasDataContractOrgService.InquirySearchRequestDTO();
            HUMInqSchemasDataContractOrgService.ErrorDTO errorDTO = new HUMInqSchemasDataContractOrgService.ErrorDTO();
            System.assert(errorDTO.ErrorCode == null);

            HUMInqSchemasDataContractOrgService.EntityIdentifier entityIdentifier = new HUMInqSchemasDataContractOrgService.EntityIdentifier();
            System.assert(entityIdentifier.IdType == null);

            HUMInqSchemasDataContractOrgService.ArrayOfTaskReplyTypeDTO arrayOfTaskReplyTypeDTO = new HUMInqSchemasDataContractOrgService.ArrayOfTaskReplyTypeDTO();
            HUMInqSchemasDataContractOrgService.ArrayOfTaskDTO arrayOfTaskDTO = new HUMInqSchemasDataContractOrgService.ArrayOfTaskDTO();
            HUMInqSchemasDataContractOrgService.NoteDTO noteDTO = new HUMInqSchemasDataContractOrgService.NoteDTO();
            System.assert(noteDTO.InquiryId == null);

            HUMInqSchemasDataContractOrgService.InquiryDTO inquiryDTO = new HUMInqSchemasDataContractOrgService.InquiryDTO();
            System.assert(inquiryDTO.Complaint == null);

            HUMInqSchemasDataContractOrgService.ArrayOfAttachmentDTO arrayOfAttachmentDTO = new HUMInqSchemasDataContractOrgService.ArrayOfAttachmentDTO();
            HUMInqSchemasDataContractOrgService.ArrayOfInquiryDTO arrayOfInquiryDTO = new HUMInqSchemasDataContractOrgService.ArrayOfInquiryDTO();
            HUMInqSchemasDataContractOrgService.ArrayOfAttachmentAttributeDTO arrayOfAttachmentAttributeDTO = new HUMInqSchemasDataContractOrgService.ArrayOfAttachmentAttributeDTO();
            HUMInqSchemasDataContractOrgService.ArrayOfNameValuePair arrayOfNameValuePair = new HUMInqSchemasDataContractOrgService.ArrayOfNameValuePair();
            HUMInqSchemasDataContractOrgService.AuditLogDTO auditLogDTO = new HUMInqSchemasDataContractOrgService.AuditLogDTO();
            System.assert(auditLogDTO.AuditId == null);

            HUMInqSchemasDataContractOrgService.InquirySearchReturnCriteria inquirySearchReturnCriteria = new HUMInqSchemasDataContractOrgService.InquirySearchReturnCriteria();
            System.assert(inquirySearchReturnCriteria.InquiryDetails ==null);

            HUMInqSchemasDataContractOrgService.NameValuePair nameValuePair = new HUMInqSchemasDataContractOrgService.NameValuePair();
            System.assert(nameValuePair.FilterValue == null);

            HUMInqSchemasDataContractOrgService.ArrayOfEntityIdentifier arrayOfEntityIdentifier = new HUMInqSchemasDataContractOrgService.ArrayOfEntityIdentifier();
            HUMInqSchemasDataContractOrgService.InquirySearchCriteriaDTO inquirySearchCriteriaDTO = new HUMInqSchemasDataContractOrgService.InquirySearchCriteriaDTO();
            System.assert(inquirySearchCriteriaDTO.ContactId == null);

            HUMInqSchemasDataContractOrgService.GNASummaryDTO gNASummaryDTO = new HUMInqSchemasDataContractOrgService.GNASummaryDTO();
            System.assert(gNASummaryDTO.CaseLevel == null);

            Test.StopTest();
        }
     }
 }