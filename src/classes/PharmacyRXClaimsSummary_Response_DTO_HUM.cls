/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsSummary_Response_DTO_HUM
Version            : 1.0
Function           : Response DTO for PharmacyRXClaimsSummary ClaimResponse
Created On         : July 14 2020
Test Class         : PharmacyRXClaimsSummary_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                      07/14/2020                 Original Version - REQ - 891651 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claims Summary page (RxXP) 
*******************************************************************************************************************/
public with sharing class PharmacyRXClaimsSummary_Response_DTO_HUM {
    
	public string ActualProcessDate  { get; set; }
    public String ClaimId  { get; set; }
    public String RxNumber  { get; set; }
    public Double RxQuantity  { get; set; }
    public String RxDaySupply  { get; set; }
    public String ClaimStatus  { get; set; }
	public String AuthorizationNumber   { get; set; }
	public String ClaimAdjudicationDate   { get; set; }
    public String ClaimSource { get; set; }	
    public Double MemberResponsibleAmount  { get; set; }
	public String NDC  { get; set; }
    public String PriorAuthorizationNumberSubmitted  { get; set; }	
	public RxClaimPharmacy RxClaimPharmacy ;
	public RxClaimDrug RxClaimDrug;
	public RxClaimPatient RxClaimPatient;
    
    public class RxClaimPharmacy {
		public String PharmacyName;
	}
	
    public class RxClaimDrug {
		public String NDC;
		public String DrugName;
		public String GCN;
	}

	public class RxClaimPatient {
		public String CustomerId;
		public String ClientId;		
	}	
}