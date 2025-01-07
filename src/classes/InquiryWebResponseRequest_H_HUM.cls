/*******************************************************************************************************************************
Apex Class Name : InquiryWebResponseRequest_H_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains request Nodes InquiryDetails_S_HUM service class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 							          09/05/2020 				 REQ - 1318083 MF 3 - Archived CCP Inquiry Detail Page - High Level (Overall Look)
 ********************************************************************************************************************************/
public with sharing class InquiryWebResponseRequest_H_HUM {

    public CCPWebResponse CCPWebResponse;
  
    public class CCPWebResponse{
        public String ContactId = '';  
        public String InquiryId= '';        
    }
}