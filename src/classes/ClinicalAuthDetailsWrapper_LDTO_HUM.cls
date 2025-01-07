/*****************************************************************************************
Apex class Name    : ClinicalAuthDetailsWrapper_LDTO_HUM
Version            : 1.0
Function           : This class is used as a Wrapper class for Authorization Details.
Created Date       : 07/12/2022
Test Class         : ClinicalAuthDetailsRequest_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date                  Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Anuradha Gajbhe                                  07/12/2022               Original Version
 ******************************************************************************************************************************/
 
public  with sharing class ClinicalAuthDetailsWrapper_LDTO_HUM
{
    @AuraEnabled public String sAuthID {get;set;}   
    @AuraEnabled public String sAuthType {get;set;} 
    @AuraEnabled public String sAuthStatus {get;set;} 
    @AuraEnabled public String sUpdatedDate {get;set;} 
    @AuraEnabled public String sCreatedDate {get;set;} 
    @AuraEnabled public String sDateOfService {get;set;} 
    @AuraEnabled public String sContactMethod {get;set;} 
    @AuraEnabled public String sIsReferral {get;set {
            sIsReferral = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
                                       }
                              }  
    @AuraEnabled public String sStatusReason {get;set;} 
    @AuraEnabled public String sCustomerGenkey {get;set;} 
    @AuraEnabled public String sGroupId {get;set;} 
    @AuraEnabled public String sPlatformCode {get;set;} 
    @AuraEnabled public String sCoverageSequenceNumber {get;set;} 
    @AuraEnabled public String sEffectiveDate {get;set;}
    
    /*Authorization/Referral Information*/
    @AuraEnabled public String sRequestType {get;set;}
    @AuraEnabled public String sDenialReason {get;set;}
    @AuraEnabled public String sClaimsPaymentNotes {get;set;}
    @AuraEnabled public String sNextReviewDate {get;set;}

    //InPatient
    @AuraEnabled public String sAdmissionDate {get;set;}   
    @AuraEnabled public String sDischargeDate {get;set;}
    @AuraEnabled public String sBedType {get;set;}
    @AuraEnabled public String sAdmissionType {get;set;}         
    @AuraEnabled public String sTotalDaysApproved {get;set;}  
    @AuraEnabled public String sLastCoveredDate {get;set;}
    //OutPatient
    @AuraEnabled public String sServiceType {get;set;}
    @AuraEnabled public String sOPRequestType {get;set;} 
    @AuraEnabled public String sFirstDay {get;set;}
    @AuraEnabled public String sLastDay {get;set;}
    @AuraEnabled public Integer iTotalDays {get;set;} 
    
    /*Diagnosis Codes*/
    @AuraEnabled public String sDiagnosisType{get;set;} 
    @AuraEnabled public String sDiagnosisCode{get;set;}  
    @AuraEnabled public String sDiagnosisDescription{get;set;}   
    @AuraEnabled public String sDiagnosisStatus{get;set;} 
    
    /*Procedure Codes*/
    @AuraEnabled public String sProcedureCode{get;set;}  
    @AuraEnabled public String sProcedureDescription{get;set;}   
    @AuraEnabled public String sProcedureStatus{get;set;}
    //InPatient 
    @AuraEnabled public String sProcedureServiceCode{get;set;}  
    //OutPatient
    @AuraEnabled public String sProcedureRequestingUnits{get;set;}
    @AuraEnabled public String sProcedureAuthorizedUnits{get;set;}
    @AuraEnabled public String sProcedureTypeofUnits{get;set;}
    
    /*Provider Information*/
    @AuraEnabled public String sReqProviderCity {get; set;}
    @AuraEnabled public String sReqProviderState {get; set;}
    @AuraEnabled public String sReqProviderPhnNum {get; set;}
    @AuraEnabled public String sReqProviderStreet {get; set;}
    @AuraEnabled public String sReqProviderZipCode {get; set;}
    @AuraEnabled public String sReqProviderID {get; set;}
    @AuraEnabled public String sReqProviderName {get; set;}
    @AuraEnabled public String sReqProviderTaxID {get; set;}
    @AuraEnabled public String sReqProviderPartStatus {get; set;}
    @AuraEnabled public String sReqProviderType {get; set;}

        @AuraEnabled public String sRecordType {get; set;}
    @AuraEnabled public String sCommCreatedDate {get; set;}  
    @AuraEnabled public String sCommCreatedBy {get; set;}
    @AuraEnabled public String sCommLastModifiedDate {get; set;}
    @AuraEnabled public String sCommLastModifiedBy {get; set;} 
    @AuraEnabled public String sDepartment {get; set;} 
    @AuraEnabled public String sCommunicationType {get; set;}    
    @AuraEnabled public String sCommContactMethod {get; set;}  
    @AuraEnabled public String sContactType {get; set;}  
    @AuraEnabled public String sContactAttemptNo {get; set;}  
    @AuraEnabled public String sContactResult {get; set;}  
    @AuraEnabled public List<String> lstContactResult {get; set;}  
    @AuraEnabled public String sContactResultOther {get; set;} 
    @AuraEnabled public String sNotes {get; set;}  
    @AuraEnabled public string sDetails {get; set;}
    
    /*Letters*/
    @AuraEnabled public String sLettersType {get; set;}
    @AuraEnabled public String sLettersAssociatedAuthID {get; set;} 
    @AuraEnabled public String sLettersAssociatedCaseID {get; set;}
    @AuraEnabled public String sLettersCreatedBy {get; set;}
    @AuraEnabled public String sLettersCreatedDate {get; set;}
    @AuraEnabled public String sLettersTemplateName {get; set;}
    @AuraEnabled public String sLettersStatus {get; set;}
    @AuraEnabled public String sLettersFullfillmentStatus {get; set;}
    @AuraEnabled public String sLettersP2POfferedinLetter {get; set;}
    @AuraEnabled public String sLettersLetterName {get; set;}
    @AuraEnabled public String sLettersVoidRemarks {get; set;}
    @AuraEnabled public String sLettersLastModifiedBy {get; set;}
    @AuraEnabled public String sLettersLastModifiedDate {get; set;}
    @AuraEnabled public String sFilePath {get; set;}
    @AuraEnabled public Boolean bCheck{get; set;}

}