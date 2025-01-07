/*******************************************************************************************************************************
Apex Class Name : InquirySummaryWrapper_LC_DTO_HUM.cls
Version         : 1.0
Created On      :  03/08/2021
Function        : Wrapper class for Inquiry Summary Service.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal 				                       03/08/2021                     initial version 
 ********************************************************************************************************************************/
public with sharing class InquirySummaryWrapper_LC_DTO_HUM
{
    @AuraEnabled public String sReferenceID ;
    @AuraEnabled public String sInquiryID ;
    @AuraEnabled public String sType ;
    @AuraEnabled public String sPolicy ;
    @AuraEnabled public String sCategory ;
    @AuraEnabled public String sReason ;
    @AuraEnabled public String sDisposition ;
    @AuraEnabled public String sCreatedOn ;
    @AuraEnabled public String sInquiryType ;
    @AuraEnabled public String sInquiredForID ;{}
    @AuraEnabled public String sInquiredForName ;
    @AuraEnabled public String sInquiredAboutType ;
    @AuraEnabled public String sInquiredAboutID ;
    @AuraEnabled public String sInquiredAboutName ;
    @AuraEnabled public String sOwnedBy ;
    @AuraEnabled public String sOwnerTeam ;
    @AuraEnabled public String sOwnerDept ;
    @AuraEnabled public String sTasks;
    @AuraEnabled public String sPriority ;
    @AuraEnabled public String sStatus ;
    @AuraEnabled public String sReceivedDate ;
    @AuraEnabled public String sCount; 
    @AuraEnabled public String sFlag_24_Months;
	@AuraEnabled public Boolean bContactDet;
}