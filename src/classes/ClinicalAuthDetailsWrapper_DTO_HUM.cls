/*****************************************************************************************
Apex class Name    : ClinicalAuthDetailsWrapper_DTO_HUM
Version            : 1.0
Function           : Controller to VF Page:
                     This class is used as a Wrapper class for Authorization Details.
Created Date       : 18 Dec 2015
Test Class         : ClinicalAuthDetailsWrapper_T_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Prasanthi Kandula          29039                 12/18/2015          Original Version
 *   Ranjeeth N                 29648                 02/01/2016          changed the class name
 *   Prasanthi Kandula                                07/10/2020          Defect Fix  1037 - Details column on communication section is not mapped correctly
 *   Satyanaraynaa Chenna 							  05/31/2021 		  2233159- T1PRJ0002735 - CR AEP Readiness - High Security Issues 		
 ******************************************************************************************************************************/

public  with sharing class ClinicalAuthDetailsWrapper_DTO_HUM
{
    public String sAuthID {get;set;}   
    public String sAuthType {get;set;} 
    public String sAuthStatus {get;set;} 
    public String sUpdatedDate {get;set;} 
    public String sCreatedDate {get;set;} 
    public String sDateOfService {get;set;} 
    public String sContactMethod {get;set;} 
    public String sIsReferral {get;set {
            sIsReferral = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
									   }
							  }  
    public String sStatusReason {get;set;} 
    public String sCustomerGenkey {get;set;} 
    public String sGroupId {get;set;} 
    public String sPlatformCode {get;set;} 
    public String sCoverageSequenceNumber {get;set;} 
    public String sEffectiveDate {get;set;}
    
    /*Authorization/Referral Information*/
    public String sRequestType {get;set;}
    public String sDenialReason {get;set;}
    public String sClaimsPaymentNotes {get;set;}
    public String sNextReviewDate {get;set;}

    //InPatient
    public String sAdmissionDate {get;set;}   
    public String sDischargeDate {get;set;}
    public String sBedType {get;set;}
    public String sAdmissionType {get;set;}         
    public String sTotalDaysApproved {get;set;}  
    public String sLastCoveredDate {get;set;}
    //OutPatient
    public String sServiceType {get;set;}
    public String sOPRequestType {get;set;} 
    public String sFirstDay {get;set;}
    public String sLastDay {get;set;}
    public Integer iTotalDays {get;set;} 
    
    /*Diagnosis Codes*/
    public String sDiagnosisType{get;set;} 
    public String sDiagnosisCode{get;set;}  
    public String sDiagnosisDescription{get;set;}   
    public String sDiagnosisStatus{get;set;} 
    
    /*Procedure Codes*/
    public String sProcedureCode{get;set;}  
    public String sProcedureDescription{get;set;}   
    public String sProcedureStatus{get;set;}
    //InPatient 
    public String sProcedureServiceCode{get;set;}  
    //OutPatient
    public String sProcedureRequestingUnits{get;set;}
    public String sProcedureAuthorizedUnits{get;set;}
    public String sProcedureTypeofUnits{get;set;}
    
    /*Provider Information*/
    public String sReqProviderCity {get; set;}
    public String sReqProviderState {get; set;}
    public String sReqProviderPhnNum {get; set;}
    public String sReqProviderStreet {get; set;}
    public String sReqProviderZipCode {get; set;}
    public String sReqProviderID {get; set;}
    public String sReqProviderName {get; set;}
    public String sReqProviderTaxID {get; set;}
    public String sReqProviderPartStatus {get; set;}
    public String sReqProviderType {get; set;}

        public String sRecordType {get; set;}
    public String sCommCreatedDate {get; set;}  
    public String sCommCreatedBy {get; set;}
    public String sCommLastModifiedDate {get; set;}
    public String sCommLastModifiedBy {get; set;} 
    public String sDepartment {get; set;} 
    public String sCommunicationType {get; set;}    
    public String sCommContactMethod {get; set;}  
    public String sContactType {get; set;}  
    public String sContactAttemptNo {get; set;}  
    public String sContactResult {get; set;}  
    public List<String> lstContactResult {get; set;}  
    public String sContactResultOther {get; set;} 
    public String sNotes {get; set;}  
    public string sDetails {get; set;}
    
    /*Letters*/
    Public String sLettersType {get; set;}
    Public String sLettersAssociatedAuthID {get; set;} 
    Public String sLettersAssociatedCaseID {get; set;}
    Public String sLettersCreatedBy {get; set;}
    Public String sLettersCreatedDate {get; set;}
    Public String sLettersTemplateName {get; set;}
    Public String sLettersStatus {get; set;}
    Public String sLettersFullfillmentStatus {get; set;}
    Public String sLettersP2POfferedinLetter {get; set;}
    Public String sLettersLetterName {get; set;}
    Public String sLettersVoidRemarks {get; set;}
    Public String sLettersLastModifiedBy {get; set;}
    Public String sLettersLastModifiedDate {get; set;}
    Public String sFilePath {get; set;}
    public Boolean bCheck{get; set;}

}