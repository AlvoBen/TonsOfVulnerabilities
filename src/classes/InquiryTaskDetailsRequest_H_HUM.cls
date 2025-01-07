/*******************************************************************************************************************************
Apex Class Name : InquiryTaskDetailsRequest_H_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains request Nodes InquiryDetails_S_HUM service class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Riya Agarwal							              09/07/2020                 REQ - 1338123 - PR00090723 - MF 3 - CCP GCP Archived Inquiry Details Page - Task List Section
 ********************************************************************************************************************************/
 public with sharing class InquiryTaskDetailsRequest_H_HUM {

    public CCPTaskDetails CCPTaskDetails;
  
    public class CCPTaskDetails{
        public String ContactId = '';  
        public String InquiryId = '';  
    }
}