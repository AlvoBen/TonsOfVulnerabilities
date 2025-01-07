/*******************************************************************************************************************************
Apex Class Name : InquiryTaskDetailsWrapper_DTO_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Wrapper class for Inquiry Task Details Service.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Riya Agarwal							              09/07/2020                 REQ - 1338123 - PR00090723 - MF 3 - CCP GCP Archived Inquiry Details Page - Task List Section
* Sayali Nemade										  10/01/2020                 DF - 1718 Corrected Task Details Date Mapping
* Ashish Kumar										  04/20/2021                 Added @AuraEnabled to all variables for Lightning strides application 
 ********************************************************************************************************************************/
public with sharing class InquiryTaskDetailsWrapper_DTO_HUM
{
    @AuraEnabled public String taskID {get; set;}
    @AuraEnabled public String sCreatedOn {get; set;}
	@AuraEnabled public String sDate {get;set;}
    @AuraEnabled public String sAge {get; set;}
    @AuraEnabled public String sActionDesc {get; set;}
    @AuraEnabled public string sResultDesc {get; set;}
    @AuraEnabled public string sShortDesc {get; set;}
    @AuraEnabled public string sStatus {get; set;}
    @AuraEnabled public string sDueDate {get; set;}
    @AuraEnabled public string sDateClosed {get; set;}
    @AuraEnabled public String sCreatedBy {get; set;}
    @AuraEnabled public String sActionTime {get; set;}
    @AuraEnabled public string sSrvCtr {get; set;}{}
    @AuraEnabled public string sPriorityDesc {get; set;}
    @AuraEnabled public string sDept {get; set;}
    @AuraEnabled public string sTaskTypeDesc {get; set;}
    @AuraEnabled public String sTeam {get; set;}
    @AuraEnabled public string sUser {get; set;}
	
	//Task Attachment
    @AuraEnabled public String sType {get; set;}		
    @AuraEnabled public String sIDFileName {get; set;}		
    @AuraEnabled public String sAttachType {get; set;}
    @AuraEnabled public string sVersionNumber {get; set;}
    
	//Attachment Description
    @AuraEnabled public List<String> sField{get; set;}		
    @AuraEnabled public List<String> sValue{get; set;}

    @AuraEnabled public List<InquiryTaskDetailsResponse_H_HUM.NotesDetails> NoteDetails = New List<InquiryTaskDetailsResponse_H_HUM.NotesDetails>(); 
    @AuraEnabled public List<InquiryTaskDetailsResponse_H_HUM.AuditDetails> AuditDetails = New List<InquiryTaskDetailsResponse_H_HUM.AuditDetails>();
	@AuraEnabled public List<InquiryTaskDetailsResponse_H_HUM.AttachmentDetails> AttachmentDetails = New List<InquiryTaskDetailsResponse_H_HUM.AttachmentDetails>();
	@AuraEnabled public List<InquiryTaskDetailsResponse_H_HUM.ATTACH_DESC> AttachDescription = New List<InquiryTaskDetailsResponse_H_HUM.ATTACH_DESC>();
    
}