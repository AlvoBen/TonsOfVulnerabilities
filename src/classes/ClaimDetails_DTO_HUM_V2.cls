/*******************************************************************************************************************************************************
Apex Class Name  : ClaimDetails_DTO_HUM_V2
Version          : 1.0
Created Date     : 03/06/2018 
Function         : This class is wrapper class to store data from service
Test Class       : ClaimDetailsService_T_HUM_V2
Modification Log :

* Developer Name            Code Review           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Prasanthi Kandula           212620             03/06/2018                 Original Version
* Raja Sekhar Reddy M V                         11/12/2018                 REQ - 353088 - Added code for Bank code
* Santhapuri Kishore                            03/05/2020                 US - 746884  Ability to view the Provider Specialty Codes And Description associated to the Provider listed on a claim  
*   Trina Ghosh									06/17/2020		           US# - 1039163 Primary Speciality Fields Logging
* Pallavi Shewale                               03/03/2021                 US #1422598 - Add 'Claims Payment Responsibility' Field on 'Claim/Pre-determination Details' page  
*   Pinky Vijur                                       05/20/2021          US# 2251807 - T1PRJ0003145 - CR-004 - Remove 'Claims Payment Responsibility' Field from "Provider Details" Section (CRM Service)
************************************************************************************************************************************************/

public with sharing class ClaimDetails_DTO_HUM_V2
{
    public String sClaimNbr {get; set;} 
    public String sClmMediaCd {get; set;}
    public String sPatRecordNbr {get; set;} 
    public String sSrcAuthKey {get; set;}
    public boolean bAuthKeyFlag {get; set;}
    public String sDeficiencyInd {get; set;}  
    public String sDeficiencyField {get; set;}
    public String sDeficiencyDate {get; set;}
    public String sDeficiencyDesc {get; set;}
    public List<String> lstDiagnosisCodes {get; set;}
    public String sSrcLvCASPrefix {get; set;}
    public String sTotalInterestFooter {get; set;}
    public String sChargeAmt {get; set;}   
    public String sBenAllowAmt {get; set;} 
    public String sProvWriteOff {get; set;}
    public String sBenDenyAmt {get; set;}
    public String sMbrRespAmt {get; set;}
    public String sPaidAmt {get; set;} 
    public String sTotalInterest {get; set;}
    public String sExcludeAmt {get; set;}
    public String sCobPaidAmt {get; set;}
    public String sDiagIndicator {get; set;} 
    public String sFirstDiagnosisCode{get; set;} 
    public String sPaymentMethod {Get; Set;}     
    Public String sBankCode {Get; Set;}
    Public boolean bShowMore {Get;Set;}
    Public string sProviderSpecFirstRecord {get;set;}
    public string sProviderSpecAllRecords {Get;Set;}
    Public String listPrimarySpeciality {Get;Set;}
}