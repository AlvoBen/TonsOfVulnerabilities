/*******************************************************************************************************************************
Apex Class Name : InquiryNotesRequest_H_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains request Nodes InquiryDetails_S_HUM service class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 							          09/05/2020 				 REQ - 1318083 MF 3 - Archived CCP Inquiry Detail Page - High Level (Overall Look)
 ********************************************************************************************************************************/
public with sharing class InquiryNotesRequest_H_HUM {

    public CCPNotesDetails CCPNotesDetails;
  
    public class CCPNotesDetails {
        public String ContactId = '';  
        public String InquiryId = '';  
    }
}