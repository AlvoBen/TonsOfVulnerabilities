/*******************************************************************************************************************************
Apex Class Name : InquiryTaskDetailsResponse_H_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains request Nodes InquiryDetails_S_HUM service class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Riya Agarwal							              09/07/2020                 REQ - 1338123 - PR00090723 - MF 3 - CCP GCP Archived Inquiry Details Page - Task List Section
* Sayali Nemade										  10/01/2020                 DF - 1718 Corrected Task Details Date Mapping
* Ashish Kumar										  04/20/2021                 Added @AuraEnabled to all variables for Lightning strides application 
 ********************************************************************************************************************************/
 public  with sharing class InquiryTaskDetailsResponse_H_HUM
{
    @AuraEnabled public List<TaskDetails> TaskDetails;
    
    public class TaskDetails
    {
        @AuraEnabled public String TASK_ID = ''; 
        @AuraEnabled public String CREATED_TS = '';
		@AuraEnabled public String ACTION_DATE = '';
        @AuraEnabled public String AGE = '';
        @AuraEnabled public String ACTION_CD_DESC = ''; 
        @AuraEnabled public String RESULT_CD_DESC = '';  
        @AuraEnabled public String SHORT_DESC = '';     
        @AuraEnabled public String STATUS_CD = '';
        @AuraEnabled public String DUE_DATE = '';   
        @AuraEnabled public String CLOSED_TS = '';
        @AuraEnabled public String CREATED_FIRST_NAME = '';
        @AuraEnabled public String CREATED_LAST_NAME = '';
        @AuraEnabled public String CREATED_BY = '';
        @AuraEnabled public String CREATED_TEAM_NAME = '';  
        @AuraEnabled public String CREATED_DEPT_LGNM = ''; 
        @AuraEnabled public String CREATED_SITE_LGNM = '';
        @AuraEnabled public String ACTION_TIME = '';        
        @AuraEnabled public String OWNER_SITE_LGNM = '';    
        @AuraEnabled public String PRIORITY_CD_DESC = '';   
        @AuraEnabled public String OWNER_DEPT_LGNM = '';    
        @AuraEnabled public String TASK_TYPE_DESC = '';     
        @AuraEnabled public String OWNER_TEAM_NAME = '';
        @AuraEnabled public String OWNER_FIRST_NAME = '';
        @AuraEnabled public String OWNER_LAST_NAME = '';
        @AuraEnabled public List<NotesDetails> NotesDetails;
        @AuraEnabled public List<AuditDetails> AuditDetails;
		@AuraEnabled public List<AttachmentDetails> AttachmentDetails;
      }
        
    public class NotesDetails
    {
        @AuraEnabled public String Type = '';
        @AuraEnabled public String Task_ID = '';
        @AuraEnabled public String NOTE_DESC = '';
    }
    
    public class AuditDetails
    {
        @AuraEnabled public String TASK_ID = '';
        @AuraEnabled public String ACTION_TYPE_DESC = '';
        @AuraEnabled public String Modified_On = '';
        @AuraEnabled public String CREATED_LAST_NAME = '';
        @AuraEnabled public String CREATED_FIRST_NAME = '';
        @AuraEnabled public String PREVIOUS_DESC = '';
        @AuraEnabled public String CURRENT_DESC  = '';

    }
	
     public class AttachmentDetails{
        @AuraEnabled public String ATTACH_TYPE_DESC = '';
        @AuraEnabled public String ATTACH_TYPE = '';
        @AuraEnabled public String Doc_Id = '';
        @AuraEnabled public String Version_String = '';
        @AuraEnabled public List<ATTACH_DESC> ATTACH_DESC;
        
    }
     public class ATTACH_DESC{
      @AuraEnabled public String Field= '';
      @AuraEnabled public String Value = '';  
    }
}