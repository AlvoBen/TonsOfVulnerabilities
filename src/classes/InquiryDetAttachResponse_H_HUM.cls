/*******************************************************************************************************************************
Apex Class Name : InquiryDetAttachResponse_H_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains response Nodes InquiryDetails_S_HUM service class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 							          09/05/2020 				 REQ - 1318083 MF 3 - Archived CCP Inquiry Detail Page - High Level (Overall Look)
* Ashish Kumar										  04/01/2021				 Added @AuraEnabled to all variables for Lightning strides application    
********************************************************************************************************************************/
public with sharing class InquiryDetAttachResponse_H_HUM 
{
    public List<DRAttachmentDetails> DRAttachmentDetails; 
    
    public class DRAttachmentDetails {
    public String ATTACH_TYPE_DESC = '';
    public String ATTACH_TYPE = '';
    public String Doc_Id = '';
    public String Version_String = ''; 
    public List<ATTACH_DESC> ATTACH_DESC;
	public String TASK_ID = ''; 
    }
    
    public class ATTACH_DESC{
      @AuraEnabled public String Field= '';
      @AuraEnabled public String Value = '';  
    }
}