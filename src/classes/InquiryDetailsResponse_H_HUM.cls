/*******************************************************************************************************************************
Apex Class Name : InquiryDetailsResponse_H_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains response Nodes InquiryDetails_S_HUM service class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 							          09/05/2020 				 REQ - 1318083 MF 3 - Archived CCP Inquiry Detail Page - High Level (Overall Look)
* Ashish Kumar										  03/25/2021				 Added @AuraEnabled to all variables in main class and inner class for Lightning strides application 
********************************************************************************************************************************/
public with sharing class InquiryDetailsResponse_H_HUM
{	
    @AuraEnabled public DRInquiryDetails DRInquiryDetails;
   
    public class DRInquiryDetails
    {
        @AuraEnabled public String CONTACT_ID = '';
        @AuraEnabled public String CREATED_BY = '';
        @AuraEnabled public String CREATED_TEAM_NAME = '';
        @AuraEnabled public String CREATED_DEPT_NAME  = '';
        @AuraEnabled public String CATEGORY_CD_DESC = '';
        @AuraEnabled public String DISPOSITION_DESC = '';
        @AuraEnabled public String DateOfService = '';
        @AuraEnabled public String ResponseShortText = '';
        @AuraEnabled public String PRIORITY_CD_DESC = '';
        @AuraEnabled public String OWNER_SITE_NAME = '';
        @AuraEnabled public String OWNER_DEPT_NAME = '';
        @AuraEnabled public String OWNER_TEAM_NAME = '';
        @AuraEnabled public String DCN = '';
        @AuraEnabled public String CREATED_TS = '';
        @AuraEnabled public String OWNER_FIRST_NAME = '';
        @AuraEnabled public String OWNER_LAST_NAME = '';
        @AuraEnabled public String CREATED_SITE_NAME = '';
		@AuraEnabled public String CREATED_FIRST_NAME = '';
		@AuraEnabled public String CREATED_LAST_NAME = '';
		@AuraEnabled public String STATUS_CD_DESC = '';
        @AuraEnabled public String REASON_CD_DESC = '';
    }
  }