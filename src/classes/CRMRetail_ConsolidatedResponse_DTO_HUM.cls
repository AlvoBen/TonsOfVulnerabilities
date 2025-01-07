/***********************************************************************************************************************************
* Apex Class Name  : CRMRetail_ConsolidatedResponse_DTO_HUM 
* Version          : 1.0
* Created Date     : 6/14/2022
* Function         : Wrapper Class for consolidated web service response
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Lakshmi Madduri                       					         6/14/2022           US-3258682 T1PRJ0154546 / SF / MF9 Storefront - SNP: Indicators Field - Data Population
**********************************************************************************************************************************/
public class CRMRetail_ConsolidatedResponse_DTO_HUM {

	public class SupplementalFields {
		public String year_mth;
		public String engagement_quintile;
		public String go365_contrib;
		public String rtsrce_contrib;
		public String programs_contrib;
		public String logins_contrib;
		public String amlk_model_contrib;
		public String hedis_contrib;
		public String engagement_cohort;
		public String mco_contract_nbr;
		public String plan_benefit_package_id;
		public String ind_new_mbr;
	}

	public class MedicareMemberLISSNPInfoResponse {
		public String MedicareID;
		public String LISIndicator;
		public String SubsidyEffectDate;
		public String SubsidyEndDate;
		public String LISSubsidyLevel;
		public String LISCopayCategory;
		public String LTSSIndicator;
		public String ESRDIndicator;
		public String SPAPIndicator;
		public String SNPIndicator;
		public String SNPTypeCode;
		public String POAIndicator;
		public String POALastName;
		public String POAFirstName;
		public String POAPhoneNumber;
		public String POAType;
		public String POAApplDate;
		public Errors Errors;
	}

	public class Errors {
		public Error Error;
	}

	public List<SDoHSuccess> SDoHSuccess;
	public SDoHError SDoHError;
	public EngagementIndexSuccess EngagementIndexSuccess;
	public SDoHError EngagementIndexError;
	public MedicareMemberInfoSuccess MedicareMemberInfoSuccess;
    public MedicareMemberInfoError MedicareMemberInfoError;

    public class MedicareMemberInfoError{
        
    }
	public class EngagementIndexSuccess {
		public Integer sdrPersonId;
		public String srcPlatformCd;
		public String modelName;
		public Integer modelVersion;
		public Integer modelLookback;
		public String scoreDate;
		public String actionTimestamp;
		public String score;
		public String scaledScore;
		public String scoringType;
		public String label;
		public String triggerEventIdName;
		public String triggerEventId;
		public String finalPrediction;
		public String finalPredictionThreshold;
		public SupplementalFields supplementalFields;
	}
	public class SDoHError {
		public String timestamp;
		public Integer status;
		public String error;
		public String message;
		public String path;
	}
	 public class Error
    {
    	public string Code;
    	public string Description;
    }

	public class GetMedicareMemberInfo {
		public MedicareMemberLISSNPInfoResponse MedicareMemberLISSNPInfoResponse;
	}

	public class CIMedicareResponse {
		public List<GetMedicareMemberInfo> GetMedicareMemberInfo;
	}

	public class MedicareMemberInfoSuccess {
		public CIMedicareResponse CIMedicareResponse;
	}

	public class SDoHSuccess {
		public String sdrPersonId;
        public String scoreDate;
        public String screeningDate;
        public String metricName; 
        public String metricRating; 
        public String metricDescription;  
        public String metricSource; 
        public DateTime timestamp;
	}
}