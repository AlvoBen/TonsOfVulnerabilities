/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsDetail_Response_DTO_HUM
Version            : 1.0
Function           : This Class contains methods for services to parse response to show Pharmacy Rx Claims Detail information on UI.
Created On         : July 24 2020
Test Class         : PharmacyRXClaimsDetail_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Goud                                      07/24/2020                 Original Version - REQ - 1151554 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claim Details page (RxXP)
*******************************************************************************************************************/
public with sharing class PharmacyRXClaimsDetail_Response_DTO_HUM {
	 
	public class RxClaimPharmacy {
		@AuraEnabled
		public String PharmacyIdQualifier;
		@AuraEnabled
		public String MailOrderIndicator;
		@AuraEnabled
		public String PharmacyAddress1;
		@AuraEnabled
		public String PharmacyAddress2;
		@AuraEnabled
		public String PharmacyCity;
		@AuraEnabled
		public String PharmacyFax;
		@AuraEnabled
		public String PharmacyEmailAddress;
		@AuraEnabled
		public String PharmacyId;
		@AuraEnabled
		public String PharmacyMailRetail;
		@AuraEnabled
		public String PharmacyNABP;
		@AuraEnabled
		public String PharmacyName;
		@AuraEnabled
		public String PharmacyNetworkType;
		@AuraEnabled
		public String PharmacyNPI;
		@AuraEnabled
		public String PharmacyPhone;
		@AuraEnabled
		public String PharmacyState;
		@AuraEnabled
		public String PharmacyTaxId;
		@AuraEnabled
		public String PharmacyZipCode;
		@AuraEnabled
		public String IsSpecialtyPharmacy;
	}

	public class RxClaimArgusData {
		@AuraEnabled
		public String ArgusFieldName;
		@AuraEnabled
		public String ArgusFieldValue;
	}

	@AuraEnabled
	public String ActualProcessDate;
	@AuraEnabled
	public String AuthorizationNumber;
	@AuraEnabled
	public String ClaimAdjudicationDate;
	@AuraEnabled
	public String ClaimBrandGeneric;
	@AuraEnabled
	public String ClaimLoadDate;
	@AuraEnabled
	public String ClaimProcessDate;
	@AuraEnabled
	public Integer ClaimProcessingFee;
	@AuraEnabled
	public String ClaimReceiveDate;
	@AuraEnabled
	public String ClaimSource;
	@AuraEnabled
	public String ClaimStatus;
	@AuraEnabled
	public String ClaimTier;
	@AuraEnabled
	public Integer CoinsuranceAmount;
	@AuraEnabled
  public Decimal CopayAmount;
	@AuraEnabled
	public String DataSource;
	@AuraEnabled
	public String DateOfService;
	@AuraEnabled
	public Double DedPaidAmount;
	@AuraEnabled
	public String DispenseAsWritten;
	@AuraEnabled
	public Integer FillNumber;
	@AuraEnabled
	public String IsMaintenanceDrug;
	@AuraEnabled
	public String IsPharmacyPAR;
	@AuraEnabled
	public String IsPreferredNetwork;
	@AuraEnabled
	public String IsReversedClaim;
	@AuraEnabled
	public Double LISAmount;
	@AuraEnabled
	public String MedicareID;
	@AuraEnabled
  public Double MemberResponsibleAmount;
	@AuraEnabled
	public String NDC;
	@AuraEnabled
	public Double NetPaidAmount;
	@AuraEnabled
	public String Part_B_D;
	@AuraEnabled
	public String PersonGenKey;
	@AuraEnabled
	public String PharmacyNPI;
	@AuraEnabled
	public String PharmacyStatusCode;
	@AuraEnabled
	public String PrescriberNPI;
	@AuraEnabled
	public String PriorAuthorizationNumberSubmitted;
	@AuraEnabled
	public String PriorAuthorizationTypeCode;
	@AuraEnabled
	public Integer RxCount;
	@AuraEnabled
	public String RXCUI;
	@AuraEnabled
	public String RxDaySupply;
	@AuraEnabled
	public String RxNumber;
	@AuraEnabled
	public String RxQuantity;
	@AuraEnabled
	public String RxSpecialityCode;
	@AuraEnabled
	public String RxTypeCode;
	@AuraEnabled
	public String RxWrittenDate;
	@AuraEnabled
	public String TransitionClaimCode;
	@AuraEnabled
	public String ClaimId;
	@AuraEnabled
	public String DrugDiscountIndicator;
	@AuraEnabled
	public RxClaimPharmacy RxClaimPharmacy;
	@AuraEnabled
	public RxClaimDrug RxClaimDrug;
	@AuraEnabled
	public RxClaimPatient RxClaimPatient;
	@AuraEnabled
	public List<RxClaimActivities> RxClaimActivities;
	@AuraEnabled
	public List<RxClaimBSQData> RxClaimBSQData;
	@AuraEnabled
	public List<RxClaimAmounts> RxClaimAmounts;
	@AuraEnabled
	public RxClaimPrescriber RxClaimPrescriber;
	@AuraEnabled
	public List<RxClaimErrors> RxClaimErrors;
	@AuraEnabled
	public List<RxClaimArgusData> RxClaimArgusData;

	public class RxClaimActivities {
	@AuraEnabled
	public String ClaimDateType;
	@AuraEnabled
		public String ClaimDateValue;
	}

	public class RxClaimDrug {
	@AuraEnabled
	public String NDC;
	@AuraEnabled
		public String DrugName;
		@AuraEnabled
		public String GCN;
		@AuraEnabled
		public String GenericName;
		@AuraEnabled
		public String IsRefillClaim;
		@AuraEnabled
		public String MedID;
		@AuraEnabled
		public String MostCommonDaysSupply;
		@AuraEnabled
		public String MostCommonNDC;
		@AuraEnabled
		public String MostCommonQuantity;
		@AuraEnabled
		public String OTCIndicator;
		@AuraEnabled
		public String QuantityDispensed;
		@AuraEnabled
		public String MDLIndicator;
		@AuraEnabled
		public String SpecialtyDrugIndicator;
		@AuraEnabled
		public String UnitOfMeasure;
		@AuraEnabled
		public String CompoundDosageIndicator;
		@AuraEnabled
		public String PartDPreferenceIndicator;
		@AuraEnabled
		public String SupBeneIndicator;
	}

	public class RxClaimAmounts {
	@AuraEnabled
	public String ClaimAmountType;
	@AuraEnabled
		public String ClaimAmountValue;
	}

	public class RxClaimPrescriber {
	@AuraEnabled
	public String PrescriberId;
	@AuraEnabled
		public String PrescriberIdQualifier;
		@AuraEnabled
		public String PrescriberName;
	}

	public class RxClaimBSQData {
	@AuraEnabled
	public String BSQKey;
	@AuraEnabled
		public String BSQValue;
	}

	public class RxClaimPatient {
	@AuraEnabled
	public String FirstName;
	@AuraEnabled
		public String LastName;
		@AuraEnabled
		public String DateOfBirth;
		@AuraEnabled
		public String PersonCode;
		@AuraEnabled
		public String Gender;
		@AuraEnabled
		public String GroupId;
		@AuraEnabled
		public String MemberId;
		@AuraEnabled
		public String CustomerId;
		@AuraEnabled
		public String ClientId;
		@AuraEnabled
		public String SubsGenKey;
		@AuraEnabled
		public String MedicareId;
		@AuraEnabled
		public String CoverageCode;
		@AuraEnabled
		public String ExpandedRenewalIndicator;
	}

	public class RxClaimErrors {
	@AuraEnabled
	public String ClaimErrorCode;
	@AuraEnabled
		public String ClaimErrorValue;
	}

	public static PharmacyRXClaimsDetail_Response_DTO_HUM parse(String json) {

		return (PharmacyRXClaimsDetail_Response_DTO_HUM) System.JSON.deserialize(json, PharmacyRXClaimsDetail_Response_DTO_HUM.class);
	}
}