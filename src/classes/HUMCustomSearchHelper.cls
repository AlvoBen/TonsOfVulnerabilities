/*******************************************************************************************************************************
Apex Class Name : HUMCustomSearchHelper
Version         : 1.0
Created On      : 08/09/2014
Function        : This class is used to store results fetched from different searches.
Test Class      : HUMSearchFieldsHelperTest

Modification Log: 
* Developer Name           Code Review                 Date                        Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar             17122                       08/09/2014                  Original version
* Ranjeeth Nagishetty      19484                       11/24/2014                  Defect Fix 162135 
* Santhi Mandava                                       12/10/2014                  Incorporated REQ-81851 changes.
*******************************************************************************************************************************/
public with sharing class HUMCustomSearchHelper
{
    public String sMemberId{get; set;}
    public String sFirstName{get; set;}
    public String sLastName{get; set;}
    public String sBirthdate{get; set;}
    public String sState{get; set;}
    public String sPostalCode{get; set;}
    public String sPhone{get; set;}
    public String sGroupNumber{get; set;}
    public String sPersonEmail{get; set;}
    public String sGroupName{get; set;}
    public String sAgentId{get; set;}
    public String sAgentType{get; set;}
    public String sAgencyName {get; set;}
    public String sTaxID {get; set;}
    public String sNPI {get; set;}
    public String sSpeciality{get; set;}
    public String sProviderName{get; set;}
    public String sFacilityName{get; set;}
    public String sAddress{get; set;}
    public String sOrgName{get; set;}
    public string sExtID {get; set;}
    public string sCity {get; set;}
    public string sAddresstype {get; set;}
    public string sGender {get; set;}
    public string sClassification {get; set;}
    public string sTaxmonycode {get; set;}
    public string sDBA {get; set;}
    public boolean bIsUserHaveReadAccess{get; set;}
    public String sLocMemFound {get; set;}
    public String sMemName {get; set;}
    public String sPlatform {get; set;}
    public String sGroupId {get; set;}
    public String sProduct {get; set;}
    public String sLOB {get;  set;}
    public String sEffectiveDate {get;set;}
    public String sEndDate {get; set;}
    public String sPend {get; set;}
    public String sPendKey {get; set;}
    public String sDateTimePend {get; set;}
    public String sSSN{get; set;}    
    public String sStartDate {get; set;}
    public Date dtMinDate{get; set;}
    public Date dtMaxDate{get; set;}
    public String sDegree {get; set;}
    public String sEnterpriseID {get; set;}
    public String sPhoneExtn {get;set;}
    public String sServiceaddress {get;set;}
    public String sServiceState {get;set;}
    public String sServicezip {get;set;}
    public String sServiceCity {get;set;}
    public String sSuffix {get;set;}
}