/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsDetail_D_HUM
Version            : 1.0
Function           : This Class contains methods for services to fetch Pharmacy Rx Claims Detail information.
Created On         : July 24 2020
Test Class         : PharmacyRXClaimsDetail_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Goud                                      07/24/2020                 Original Version - REQ - 1151554 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claim Details page (RxXP)
*******************************************************************************************************************/

public with sharing class PharmacyRXClaimsDetail_D_HUM {
    
    @AuraEnabled
    public static PharmacyRXClaimsDetail_Request_DTO_HUM requestStructurePharmacyRXClaimDetail(string memberGenKey, string authorizationNumber){
        PharmacyRXClaimsDetail_Request_DTO_HUM rxClaimRequest = new PharmacyRXClaimsDetail_Request_DTO_HUM();
        rxClaimRequest.MemberGenKey = memberGenKey;
        rxClaimRequest.Requestor = 'Salesforce-CRM';      
        rxClaimRequest.AuthorizationNumber = authorizationNumber;
        rxClaimRequest.ActivityDetails = true;
        rxClaimRequest.ArgusDetails = true;
        rxClaimRequest.DrugDetails = true;
        rxClaimRequest.PharmacyDetails = true;
        rxClaimRequest.ErrorDetails = true;
        rxClaimRequest.PrescriberDetails = true;
        rxClaimRequest.AmountDetails = true;
        rxClaimRequest.OtherDetails = true;
        rxClaimRequest.PatientDetails =true;
        rxClaimRequest.BSQDetails = true;     
        return rxClaimRequest;  
    }
}