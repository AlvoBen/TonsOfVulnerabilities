/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsSummary_wrapper_HUM
Version            : 1.0
Function           : This Class contains methods for services to show Pharmacy Rx Claims Summary information on UI.
Created On         : July 14 2020
Test Class         : PharmacyRXClaimsSummary_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                      07/14/2020                 Original Version - REQ - 891651 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claims Summary page (RxXP) 
*******************************************************************************************************************/
public with sharing class PharmacyRXClaimsSummary_wrapper_HUM {
    
    @AuraEnabled
    public string FillTime =''; 
    @AuraEnabled
    public String FillDate = '';   
    @AuraEnabled
    public String ClaimID = '';    
    @AuraEnabled
    public String RxNumber = '';
    @AuraEnabled
    public String DrugName = '';   
    @AuraEnabled
    public String Quantity = '' ;    
    @AuraEnabled
    public String  DaysSupply= '';
    @AuraEnabled
    public String ClaimStatus = '';
    @AuraEnabled
    Public String ClientID = '';
    @AuraEnabled
    public String PharmacyName ='';
    @AuraEnabled
    Public String AuthorizationNumber = '';
    @AuraEnabled
    public String DateUpdated = '';
    @AuraEnabled
    Public Double PatientPaid ;
    @AuraEnabled
    Public string LICSCode = '';
    @AuraEnabled
    Public string LICSEffectiveDate = '';
    @AuraEnabled
    Public string DeductibleMessage = '';
    @AuraEnabled
    Public string NDC = '';
    @AuraEnabled
    Public string GCN = '';
    @AuraEnabled
    Public string DEACode = '';
    @AuraEnabled
    Public string Source = '';
    @AuraEnabled
    Public string PreAuthorizationNumber = '';
    @AuraEnabled
    Public string CustomerValue = '';
    @AuraEnabled
    Public string ClientValue = '';
    @AuraEnabled
    Public boolean isExpanded ;
}