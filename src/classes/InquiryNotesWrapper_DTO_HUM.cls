/*******************************************************************************************************************************
Apex Class Name : InquiryNotesWrapper_DTO_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains Wrapper Nodes InquiryDetails_S_HUM service class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 							          09/05/2020 				 REQ - 1318083 MF 3 - Archived CCP Inquiry Detail Page - High Level (Overall Look)
* Ashish Kumar										  04/01/2021				 Added @AuraEnabled to all variables for Lightning strides application  
********************************************************************************************************************************/
public with sharing class InquiryNotesWrapper_DTO_HUM
{
    @AuraEnabled public String sNotes {get; set;}
    @AuraEnabled public String sType {get; set;}
}