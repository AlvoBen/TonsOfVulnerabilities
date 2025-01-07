/**************************************************************************** 
Apex Class Name  : ProviderDisputesResponse_H_HUM 
Version          : 1.0  
Function         : Response helper for processProviderDisputeCasesResponse method in ProviderDisputes_S_HUM 
Test Class       : ProviderDisputesResponse_T_HUM 

* Developer Name        Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ajay Chakradhar                             10/15/2019                 Original Version 
************************************************************************************************************************************************* */
public class ProviderDisputesResponse_H_HUM {
	public cls_getProviderDisputesByCriteriaResponse getProviderDisputesByCriteriaResponse;
	public class cls_getProviderDisputesByCriteriaResponse {
		public cls_providerDisputes[] providerDisputes;
	}
	public class cls_providerDisputes {
		public String acknowledgementDueDate;	
		public String acknowledgementMailedDate;	
		public cls_agency agency;
		public String allegation;	
		public cls_aor aor;
		public String appealType;	
		public String appealsDecisionDate;	
		public String appealsStatusReason;	
		public String caseDistributionMethod;	
		public String caseNumber;
        public String caseCategory;
		public String category;	
		public String checkMailedDate;	
		public String clinicalReview;	
		public cls_communication[] communication;
		public String completionDate;	
		public cls_coordinatorUser coordinatorUser;
		public String correspondenceDueDate;	
		public String createDate;	
		public cls_createUser createUser;
		public String customDropdown1;	
		public String customDropdown2;	
		public String customDropdown3;	
		public String customDropdown4;	
		public String customDueDate1;	
		public String customDueDate2;	
		public String customText1;	
		public String customText2;	
		public String customText3;	
		public String customText4;	
		public String customText5;	
		public String decisionDate;	
		public String decisionNote;	
		public String disputeType;	
		public String drugName;	
		public String dueDate;
		public String effectuationDate;	
		public String enrolleeExpeditedDenialOralNotification;	
		public String enrolleeExpeditedDenialWrittenNotification;	
		public String enrolleeOralNotification;	
		public String erisaEntity;	
		public String executiveComplaint;	
		public String expeditedCriteriaMet;	
		public String expeditedDecisionDate;	
		public String expeditedDenialReason;	
		public String externalDecision;	
		public String externalDecisionReceivedDate;	
		public String externalEffectuationDate;	
		public String externalEffectuationRequired;	
		public String externalMethodDecision;	
		public String externalReviewMailDate;	
		public String externalReviewNoticeDate;	
		public String finalResolutionCaseNote;	
		public String goodCauseExtReceivedDate;	
		public String goodCauseExtRequestedDate;	
		public String goodCauseExtension;	
		public String goodCauseReason;	
		public String intakeDate;	
		public String intakeMode;	
		public String knownClassification;	
		public String manualReceivedDate;	
		public String medhokID;	
		public cls_medicalClaims[] medicalClaims;
		public cls_member member;
		public String memberParticipation;	
		public String memberResolutionMailedDate;	
		public String ndcCode;	
		public String nextReviewDate;	
		public cls_notification[] notification;
		public String opportunityRootCause;	
		public String opportunitySource;	
		public String preventableOppurtunity;	
		public cls_primarydiagnosis primarydiagnosis;
		public String priority;	
		public String product;	
		public String providerExpeditedDenialOralNotification;	
		public String providerExpeditedDenialWrittenNotification;	
		public String providerOralNotification;	
		public String qioSeverity;	
		public String receivedDate;	
		public String regulatoryAgency;	
		public String regulatoryComplaint;	
		public String requestType;	
		public String rfiNote;	
		public String rootCauseNote;	
		public String secondLevelNoticeMailDate;	
		public String secondLevelReviewDate;	
		public cls_secondarydiagnosis secondarydiagnosis;
		public String sensitiveCase;	
		public String source;	
		public String status;	
		public String statusReason;	
		public String subCategory;	
		public String tatCompletionDate;	
		public String tatDateAsStr;	
		public cls_tertiarydiagnosis tertiarydiagnosis;
		public String updateDate;	
		public cls_updateUser updateUser;
		public String validAppeal;	
		public String verifiedMemberData;	
		public String writtenResponseRequested;	
		public cls_wsMemberEligExtAttribute wsMemberEligExtAttribute;
		public cls_wsMemberEligibility wsMemberEligibility;
		public String appealsCaseOwner;	
		public String customText6;	
		public String dateOFServiceDesc;	
		public String dateOfOccurrence;	
		public String providerDecisionMailDate;	
		public String queueName;	
		public cls_wsProvider wsProvider;
	}
	public class cls_agency {
		public String address1;	
		public String address2;	
		public String city;	
		public String contactName;	
		public String contactPhone;	
		public String fax;	
		public String name;	
		public String phone;	
		public String regulatoryDays;	
		public String state;	
		public String type;	
		public String zip;	
	}
	public class cls_aor {
		public String address1;	
		public String address2;	
		public String city;	
		public String docOnFile;	
		public String docReceivedDate;	
		public String docRequestedDate;	
		public String fax;	
		public String firstName;	
		public String lastName;	
		public String phone;	
		public String state;	
		public String type;	
		public String zip;
	}
	public class cls_communication {
	}
	public class cls_coordinatorUser {
		public String userId;	
		public String firstName;	
		public String lastName;	
		public String title;	
		public String email;	
		public String phone;	
		public String phoneExt;	
		public String tollFree;	
		public String tollFreeExt;	
		public String fax;	
		public String requesterName;	
		public String requesterEmail;	
		public String requestDate;	
		public String terminated;	
		public String accountLocked;	
		public String userType;	
		public String timezone;	
		public String comment;	
		public String siteLocation;	
		public String lastLogin;	
		public String clientName;	
	}
	public class cls_createUser {
		public String userId;	
		public String firstName;	
		public String lastName;	
		public String title;	
		public String email;	
		public String phone;	
		public String phoneExt;	
		public String tollFree;	
		public String tollFreeExt;	
		public String fax;	
		public String requesterName;	
		public String requesterEmail;	
		public String requestDate;	
		public String terminated;	
		public String accountLocked;	
		public String userType;	
		public String timezone;	
		public String comment;	
		public String siteLocation;	
		public String lastLogin;	
		public String clientName;	
	}
	public class cls_medicalClaims {
        public String claimNumber;
	}
	public class cls_member {
		public String medhokId;	
		public String firstName;	
		public String lastName;	
		public String middleName;	
		public String fullName;	
		public String age;	
		public String dateOfBirth;	
		public cls_pcp pcp;
		public String effectiveDate;	
		public String termDate;	
		public String enrollmentStatus;	
		public String benefitPackageDescription;	
		public String daysLastDischarge;	
		public String memberId;	
		public String externalId;	
		public String gender;	
		public String familyId;	
		public String ssn;	
		public String race;	
		public String ethinicity;	
		public String spokenLanguage;	
		public String spokenLanguageSource;	
		public String writtenLanguage;	
		public String writtenLanguageSource;	
		public String otherLanguage;	
		public String otherLanguageSource;	
		public String subscriberId;	
		public cls_memberExtendedAttributes memberExtendedAttributes;
		public cls_memberEligExtAttribute memberEligExtAttribute;
		public cls_memberAddresses memberAddresses;
		public cls_memberEligibilities memberEligibilities;
		public cls_memberVitals memberVitals;
	}
	public class cls_pcp {
		public String organizationName;	
		public String firstName;	
		public String lastName;	
		public String fullName;	
		public String npi;	
		public String groupNpi;	
		public String federalTaxID;	
		public String providerID;	
		public String providerType;	
		public String region;	
		public String address1;	
		public String address2;	
		public String city;	
		public String state;	
		public String zip;	
		public String phone;	
		public String fax;	
		public String contact;	
		public String licenseNumber;	
		public String unknownProvider;	
		public String status;	
		public cls_providerIPAs providerIPAs;
	}
	public class cls_providerIPAs {
		public cls_providerIPA[] providerIPA;
	}
	public class cls_providerIPA {
	}
	public class cls_memberExtendedAttributes {
		public String attribute1;	
		public String attribute2;	
		public String attribute3;	
		public String attribute4;	
		public String attribute5;	
		public String attribute6;	
		public String attribute7;	
		public String attribute8;	
		public String attribute9;	
		public String attribute10;	
		public String attribute11;	
		public String attribute12;	
		public String attribute13;	
		public String attribute14;	
		public String attribute15;	
		public String attribute16;	
		public String attribute17;	
		public String attribute18;	
		public String attribute19;	
		public String attribute20;	
		public String attribute21;	
		public String attribute22;	
		public String attribute23;	
		public String attribute24;	
		public String attribute25;	
	}
	public class cls_memberEligExtAttribute {
		public String attribute1;	
		public String attribute2;	
		public String attribute3;	
		public String attribute4;	
		public String attribute5;	
		public String attribute6;	
		public String attribute7;	
		public String attribute8;	
		public String attribute9;	
		public String attribute10;	
		public String attribute11;	
		public String attribute12;	
		public String attribute13;	
		public String attribute14;	
		public String attribute15;	
		public String attribute16;	
		public String attribute17;	
		public String attribute18;	
		public String attribute19;	
		public String attribute20;	
		public String attribute21;	
		public String attribute22;	
		public String attribute23;	
		public String attribute24;	
		public String attribute25;	
		public String attribute26;	
		public String attribute27;	
		public String attribute28;	
		public String attribute29;	
		public String attribute30;	
		public String attribute31;	
		public String attribute32;	
		public String attribute33;	
		public String attribute34;	
		public String attribute35;	
		public String attribute36;	
		public String attribute37;	
		public String attribute38;	
		public String attribute39;	
		public String attribute40;	
		public String memberEED;	
	}
	public class cls_memberAddresses {
		public cls_address[] address;
	}
	public class cls_address {
	}
	public class cls_memberEligibilities {
		public cls_eligibilityLine eligibilityLine;
	}
	public class cls_eligibilityLine {
		public String medhokId;	
		public String eligGroup;	
		public String company;	
		public String companyLob;	
		public String eligPlan;	
		public String companyAccounts;	
		public String companyAccountGroup;	
		public String ssn;	
		public String medicaid;	
		public String medicareno;	
		public String effectivedate;	
		public String termdate;	
		public String planCode;	
		public String planName;	
		public String ipaGroup;	
		public String enrollmentSource;	
		public String subsidyLevel;	
		public String riderCode;	
		public String contractNumber;	
		public String pbpNumber;	
		public String divCode;	
		public String primaryCarrierFlag;	
		public String memberQmbSlmb;	
		public String extId;	
		public String medCenter;	
		public String regionCenter;	
	}
	public class cls_memberVitals {
		public cls_vital vital;
	}
	public class cls_vital {
		public String medhokId;	
		public String height;	
		public String weight;	
		public String hdl;	
		public String ldl;	
		public String triglyceride;	
		public String collectionDate;	
		public String systolic;	
		public String diastolic;	
		public String pulseRate;	
		public String respirationRate;	
		public String respiratoryMinuteVolume;	
		public String currentSmoker;	
		public String packsPerDay;	
		public String a1c;	
		public String notes;	
		public String bmiPercentile;	
		public String smokerStatus;	
		public String depressionScreen;	
	}
	public class cls_notification {
		public String caseNumber;	
		public String createdBy;	
		public String createdDate;	
		public String moduleContext;	
		public String moduleContextExternalId;	
		public String moduleContextInternalId;	
		public String note;	
		public String noteDate;	
		public String noteType;	
		public String noteTypeId;	
	}
	public class cls_primarydiagnosis {
		public String code;	
		public String description;	
		public String qualifier;	
		public String sequence;	
		public String poa;	
		public String codeType;	
		public cls_createUser createUser;
		public cls_updateUser updateUser;
		public String insertDatetime;	
		public String updateDatetime;	
	}
	public class cls_updateUser {
		public String userId;	
		public String firstName;	
		public String lastName;	
		public String title;	
		public String email;	
		public String phone;	
		public String phoneExt;	
		public String tollFree;	
		public String tollFreeExt;	
		public String fax;	
		public String requesterName;	
		public String requesterEmail;	
		public String requestDate;	
		public String terminated;	
		public String accountLocked;	
		public String userType;	
		public String timezone;	
		public String comment;	
		public String siteLocation;	
		public String lastLogin;	
		public String clientName;	
	}
	public class cls_secondarydiagnosis {
		public String code;	
		public String description;	
		public String qualifier;	
		public String sequence;	
		public String poa;	
		public String codeType;	
		public cls_createUser createUser;
		public cls_updateUser updateUser;
		public String insertDatetime;	
		public String updateDatetime;	
	}
	public class cls_tertiarydiagnosis {
		public String code;	
		public String description;	
		public String qualifier;	
		public String sequence;	
		public String poa;	
		public String codeType;	
		public cls_createUser createUser;
		public cls_updateUser updateUser;
		public String insertDatetime;	
		public String updateDatetime;	
	}
	public class cls_wsMemberEligExtAttribute {
		public String attribute1;	
		public String attribute2;	
		public String attribute3;	
		public String attribute4;	
		public String attribute5;	
		public String attribute6;	
		public String attribute7;	
		public String attribute8;	
		public String attribute9;
		public String attribute10;	
		public String attribute11;	
		public String attribute12;	
		public String attribute13;	
		public String attribute14;	
		public String attribute15;	
		public String attribute16;	
		public String attribute17;	
		public String attribute18;	
		public String attribute19;	
		public String attribute20;	
		public String attribute21;	
		public String attribute22;	
		public String attribute23;	
		public String attribute24;	
		public String attribute25;	
		public String attribute26;	
		public String attribute27;	
		public String attribute28;	
		public String attribute29;	
		public String attribute30;	
		public String attribute31;	
		public String attribute32;	
		public String attribute33;	
		public String attribute34;	
		public String attribute35;	
		public String attribute36;	
		public String attribute37;	
		public String attribute38;	
		public String attribute39;	
		public String attribute40;	
		public String memberEED;	
	}
	public class cls_wsMemberEligibility {
		public String medhokId;	
		public String eligGroup;	
		public String company;	
		public String companyLob;	
		public String eligPlan;	
		public String companyAccounts;	
		public String companyAccountGroup;	
		public String ssn;	
		public String medicaid;	
		public String medicareno;	
		public String effectivedate;
		public String termdate;	
		public String planCode;	
		public String planName;	
		public String ipaGroup;	
		public String enrollmentSource;	
		public String subsidyLevel;	
		public String riderCode;	
		public String contractNumber;	
		public String pbpNumber;	
		public String divCode;	
		public String primaryCarrierFlag;	
		public String memberQmbSlmb;	
		public String extId;
		public String medCenter;	
		public String regionCenter;	
	}
	public class cls_wsProvider {
		public String organizationName;	
		public String firstName;	
		public String lastName;	
		public String fullName;	
		public String npi;	
		public String groupNpi;	
		public String federalTaxID;	
		public String providerID;	
		public String providerType;	
		public String region;	
		public String address1;	
		public String address2;	
		public String city;	
		public String state;	
		public String zip;	
		public String phone;	
		public String fax;	
		public String contact;	
		public String licenseNumber;	
		public String unknownProvider;	
		public String status;	
		public cls_providerIPAs providerIPAs;
		public String providerDisplayName;	
	}
    
    public static ProviderDisputesResponse_H_HUM parse(String json) {
        return (ProviderDisputesResponse_H_HUM) System.JSON.deserialize(json, ProviderDisputesResponse_H_HUM.class);
    }
	
}