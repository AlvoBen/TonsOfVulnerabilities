/*******************************************************************************************************************************
Apex Class Name : HUMProviderSearchWrapperHelper
Version         : 1.0
Created On      : 07/31/2015
Function        : This class is used to store results fetched from Provider Search.
Test Class      : HUMProviderSearchWrapperHelperTest

Modification Log: 
* Developer Name           Code Review                 Date                        Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kuamr                                         07/31/2015                  Original version
* Shruthi Karanth         84636                        04/14/2017                  REQ-306322 Softphone Optimization : Added new variable mapEncryptedValues
* Charan k											   10/03/2018				   REQ-367752.
* Mohammed Noor          357461                        05/29/2019                  REQ - 380470 Application Search : Added new variables
* Seema Kaulgi           357488                        05/29/2019                  REQ - 379048 TRR Search : Added new variables
*******************************************************************************************************************************/
public with sharing class HUMProviderSearchWrapperHelper
{
    public String sMemberId{get; set;}
    public String sSuffix {get;set;}
    public String sFirstName{get; set;}
    public String sLastName{get; set;}
    public String sGroupNumber{get; set;}
    public String sBirthdate{get; set;}
    public String sPhone{get; set;}
    public String sState{get; set;}
    public String sPostalCode{get; set;}
    
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
    public String sPID{get; set;}
    public Map<String,String> mapEncryptedValues {get;set;}
	public String sMedicareId {get;set;}
    public String sApplicationId {get;set;}
    public String sOECConfirmationId {get;set;}
    public String sBarCode {get;set;}
    public String sMedicaidId {get;set;}
    public String sEffectiveDateFrom {get;set;}
    public String sEffectiveDateTo {get;set;}
    public String sReceivedDateFrom {get;set;}
    public String sReceivedDateTo {get;set;}
    public String sSearchType {get;set;}
	public String sHumanaReceivedDateFrom {get;set;}
    public String sHumanaReceivedDateTo {get;set;}
}