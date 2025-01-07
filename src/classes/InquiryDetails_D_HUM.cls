/*******************************************************************************************************************************
Apex Class Name : InquiryDetails_D_HUM.cls
Version         : 1.0
Created On      : 08/27/2020
Function        : Data Class for InquiryDetails_C_HUM.
TestClass       : InquiryDetails_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 							          09/05/2020 				 REQ - 1318083 MF 3 - Archived CCP Inquiry Detail Page - High Level (Overall Look)
* Riya Agarwal							              09/07/2020                 REQ - 1338123 - PR00090723 - MF 3 - CCP GCP Archived Inquiry Details Page - Task List Section                                                       
*****************************************************************************************************************************************/

public with sharing class InquiryDetails_D_HUM
{
    /**
     * Returns InquiryDetailsRequest_H_HUM object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for Inquiry Details Service.
     *
     * @param String sContactId, String sInquiryId
     * @return  InquiryDetailsRequest_H_HUM request structure of the wrapper class InquiryDetailsRequest_H_HUM
     */
    public static InquiryDetailsRequest_H_HUM requestInquiryDetails(String sContactId, String sInquiryId)
    { 
		InquiryDetailsRequest_H_HUM oInquiryDetailsRequest = new InquiryDetailsRequest_H_HUM();
        InquiryDetailsRequest_H_HUM.CCPInquiryDetails oIDRequest = new InquiryDetailsRequest_H_HUM.CCPInquiryDetails();
        oIDRequest.ContactId = sContactId;
        oIDRequest.InquiryId = sInquiryId;
        oInquiryDetailsRequest.CCPInquiryDetails = oIDRequest;
        return oInquiryDetailsRequest;
    }
    
    /**
     * Returns InquiryNotesRequest_H_HUM object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for Inquiry Notes Service.
     *
     * @param String sContactId, String sInquiryId 
     * @return  InquirySingleReferenceRequest_H_HUM request structure of the wrapper class InquirySingleReferenceRequest_H_HUM
     */
    public static InquiryNotesRequest_H_HUM requestInquiryNotes(String sContactId, String sInquiryId)
    {
        InquiryNotesRequest_H_HUM oInquiryNotesRequest = new InquiryNotesRequest_H_HUM();
        InquiryNotesRequest_H_HUM.CCPNotesDetails oNoteDetails = new InquiryNotesRequest_H_HUM.CCPNotesDetails();   
        oNoteDetails.ContactId= sContactId;
        oNoteDetails.InquiryId = sInquiryId; 
        oInquiryNotesRequest.CCPNotesDetails = oNoteDetails;
        return oInquiryNotesRequest;
    }
	
     /**
     * Returns InquiryDetAuditRequest_H_HUM object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for Inquiry detail audit Service.
     *
     * @param String sContactId, sInquiryId  
     * @return  InquiryDetAuditRequest_H_HUM request structure of the wrapper class InquiryDetAuditRequest_H_HUM 
     */
    public static InquiryDetAuditRequest_H_HUM requestInquiryAudit(String sContactId, String sInquiryId)
    { 
        InquiryDetAuditRequest_H_HUM oInquiryAuditRequest = new InquiryDetAuditRequest_H_HUM();
        InquiryDetAuditRequest_H_HUM.CCPAuditDetails oDRRequest = new InquiryDetAuditRequest_H_HUM.CCPAuditDetails();
        oDRRequest.ContactId = sContactId;        
        oDRRequest.InquiryId = sInquiryId;
        oInquiryAuditRequest.CCPAuditDetails = oDRRequest;
        return oInquiryAuditRequest;
    }
	
    /**
     * Returns InquiryWebResponseRequest_H_HUM object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for Inquiry detail audit Service.
     *
     * @param String sContactId, sInquiryId  
     * @return  InquiryWebResponseRequest_H_HUM request structure of the wrapper class InqWebResponseWrapper_DTO_HUM
     */
    public static InquiryWebResponseRequest_H_HUM requestInquiryWebResponse(String sContactId, String sInquiryId)
    { 
        InquiryWebResponseRequest_H_HUM oWebResponseRequest = new InquiryWebResponseRequest_H_HUM();
        InquiryWebResponseRequest_H_HUM.CCPWebResponse oDRRequest = new InquiryWebResponseRequest_H_HUM.CCPWebResponse ();
        oDRRequest.ContactId= sContactId;        
        oDRRequest.InquiryId= sInquiryId;
        oWebResponseRequest.CCPWebResponse = oDRRequest;
        return oWebResponseRequest;
    }
	
     /**
     * Returns InquiryDetAttachRequest_H_HUM object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for Inquiry Attachment Service.
     *
     * @param String Contact Id,Inquiry Id 
     * @return  InquiryDetAttachRequest_H_HUM request structure of the wrapper class InquiryDetAttachRequest_H_HUM 
     */
    public static InquiryDetAttachRequest_H_HUM requestInquiryAttachment(String sContactId, String sInquiryId)
    { 
        InquiryDetAttachRequest_H_HUM oInquiryAttachRequest = new InquiryDetAttachRequest_H_HUM();
        InquiryDetAttachRequest_H_HUM.CCPAttachmentDetails oDRRequest = new InquiryDetAttachRequest_H_HUM.CCPAttachmentDetails();
        oDRRequest.ContactId= sContactId;
        oDRRequest.InquiryId= sInquiryId;
        oInquiryAttachRequest.CCPAttachmentDetails = oDRRequest;
        return oInquiryAttachRequest;
    }
	
	/**
     * Returns InquiryTaskDetailsRequest_H_HUM object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for Inquiry Single Reference Service.
     *
     * @param String Card Number, Member ID   
     * @return  InquiryTaskDetailsRequest_H_HUM request structure of the wrapper class InquiryTaskDetailsRequest_H_HUM 
     */
    public static InquiryTaskDetailsRequest_H_HUM requestInquiryTaskDetails(String sContactId, String sInquiryId)
    {
        InquiryTaskDetailsRequest_H_HUM oInqTaskDetRequest = new InquiryTaskDetailsRequest_H_HUM();
        InquiryTaskDetailsRequest_H_HUM.CCPTaskDetails oCCPTaskDet = new InquiryTaskDetailsRequest_H_HUM.CCPTaskDetails();   
        oCCPTaskDet.ContactId = sContactId; 
        oCCPTaskDet.InquiryId = sInquiryId;
        oInqTaskDetRequest.CCPTaskDetails = oCCPTaskDet;
        return oInqTaskDetRequest;
    }
}