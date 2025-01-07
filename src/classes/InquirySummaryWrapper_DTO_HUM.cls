/*******************************************************************************************************************************
Apex Class Name : InquirySummaryWrapper_DTO_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Wrapper class for Inquiry Summary Service.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 				                       08/27/2020                REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look) 
 ********************************************************************************************************************************/
public with sharing class InquirySummaryWrapper_DTO_HUM
{
    public String sReferenceID {get; set;}
    public String sInquiryID {get; set;}
    public String sType {get; set;}
    public String sPolicy {get; set;}
    public String sCategory {get; set;}
    public String sReason {get; set;}
    public String sDisposition {get; set;}
    public String sCreatedOn {get; set;}
    public String sInquiryType {get; set;}
    public String sInquiredForID {get; set;}{}
    public String sInquiredForName {get; set;}
    public String sInquiredAboutType {get; set;}
    public String sInquiredAboutID {get; set;}
    public String sInquiredAboutName {get; set;}
    public String sOwnedBy {get; set;}
    public String sOwnerTeam {get; set;}
    public String sOwnerDept {get; set;}
    public String sTasks{get; set;}
    public String sPriority {get; set;}
    public String sStatus {get; set;}
    public String sReceivedDate {get; set;}
    public String sCount {get; set;}  
    public String sFlag_24_Months{get; set;}
	public Boolean bContactDet {get; set;}
}