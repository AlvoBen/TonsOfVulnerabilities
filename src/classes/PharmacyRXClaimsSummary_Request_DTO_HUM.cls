/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsSummary_Request_DTO_HUM
Version            : 1.0
Function           : Request DTO for PharmacyRXClaimsSummary ClaimRequest
Created On         : July 14 2020
Test Class         : PharmacyRXClaimsSummary_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                      07/14/2020                 Original Version - REQ - 891651 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claims Summary page (RxXP) 
*******************************************************************************************************************/

public with sharing class PharmacyRXClaimsSummary_Request_DTO_HUM {
    public PharmacyRXClaimsSummary_Request_DTO_HUM(){
        
    }
    
    public class MemberIdentifier {
        public String Key;
        public String Value;
    }  
    public String Requestor;
    public List<MemberIdentifier> MemberIdentifier;
    public String FromDate;
    public String ToDate;
    public Integer CoverageType;
    public List<Integer> ClaimTypes;
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
    public Integer PageSize;
    public Integer PageNumber;
}