/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsDetail_C_HUM
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

public with sharing class PharmacyRXClaimsDetail_C_HUM {
   
    /**
     * Returns request object of continuation object     
     * Method to invoke the GetClaimDetails to show claim detail data
     * @parameter memberGenKey, authorizationNumber     
     * @return continuation object
     */
    @AuraEnabled(continuation=true)
    Public static object GetClaimDetail(string memberGenKey, string authorizationNumber){             
		return PharmacyRXClaimsDetail_S_HUM.invokePharmacyClaimDetail(memberGenKey, authorizationNumber );    
    }
    
    /**
     * Returns responses of continuation object    
     * Callback Method to parse the getClaims service response
     * @parameter labels, object   
     * @return object
     */
    @AuraEnabled
    Public Static Object processPharmacyRXClaimDetailResponse(List < String > labels, Object State)
    {
        return PharmacyRXClaimsDetail_S_HUM.processPharmacyRXClaimDetailResponse(labels, State);
    }   
}