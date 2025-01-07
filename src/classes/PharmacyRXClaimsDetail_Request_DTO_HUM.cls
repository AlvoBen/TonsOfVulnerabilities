/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsDetail_Request_DTO_HUM
Version            : 1.0
Function           : This Class contains methods for services to create request to get Pharmacy Rx Claims Detail information.
Created On         : July 24 2020
Test Class         : PharmacyRXClaimsDetail_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Goud                                      07/24/2020                 Original Version - REQ - 1151554 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claim Details page (RxXP)
*******************************************************************************************************************/

public with sharing class PharmacyRXClaimsDetail_Request_DTO_HUM {
    
     public PharmacyRXClaimsDetail_Request_DTO_HUM(){
        
    }
	public String Requestor;
    public String MemberGenKey;
	public String AuthorizationNumber;
	public Boolean ActivityDetails;
	public Boolean ArgusDetails;
	public Boolean DrugDetails;
	public Boolean PharmacyDetails;
	public Boolean ErrorDetails;
	public Boolean PrescriberDetails;
	public Boolean AmountDetails;
	public Boolean OtherDetails;
	public Boolean PatientDetails;
	public Boolean BSQDetails;
    
    
}