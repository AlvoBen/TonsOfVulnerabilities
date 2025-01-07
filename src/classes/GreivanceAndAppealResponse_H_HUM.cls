/**************************************************************************** 
Apex Class Name  : GreivanceAndAppealResponse_H_HUM 
Version          : 1.0  
Function         : Response helper for processGreivanceAndAppealResponse method in GrievanceAndAppeal_S_HUM 
Test Class       : GreivanceAndAppealResponse_T_HUM 

Modification Log:
* Developer Name        Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ajay Chakradhar                             10/15/2018                 Original Version 
* Swapnali Sonawane                           04/09/2021                 Req 2119511 added ExRM Fields to MHK Web Service and Additional Data for G&A Status Page in CRM 
* Swapnali Sonawane                           04/27/2021                 Fix Defect#2910 EXRM Case On CRM UI, shows incorrect/ blank values for the fields for which data is present in MHK
************************************************************************************************************************************************* */
public with sharing class GreivanceAndAppealResponse_H_HUM {

    public class Address {
        public String address1;
        public String address2;
        public String address3;
        public String addressType;
        public String alternatePhone;
        public String city;
        public String country;
        public String county;
        public String dstAdjust;
        public String effectivedate;
        public String email;
        public String emergencyPhone;
        public String eveningPhone;
        public String extId;
        public String fax;
        public String firstName;
        public String island;
        public String lastName;
        public String latitude;
        public String longitude;
        public String medhokId;
        public String phone;
        public String state;
        public String termdate;
        public String timeZone;
        public String zip;
    }

    public class MemberAddresses {
        public List<Address> address;
    }

    public GetGrievanceAndAppealByCriteriaResponse getGrievanceAndAppealByCriteriaResponse;

    public class CreateUser {
        public String userId;
        public String firstName;
        public String lastName;
        public String title;
        public String email;
        public String terminated;
        public String accountLocked;
        public String comment;
        public String siteLocation;
        public String lastLogin;
        public String clientName;
        public String phone;
        public String fax;
    }

    public class Notification {
        public String createdBy;
        public String createdDate;
        public String note;
        public String noteDate;
        public String noteType;
    }

    public class Member_Z {
        public String age;
        public String benefitPackageDescription;
        public String dateOfBirth;
        public String daysLastDischarge;
        public String effectiveDate;
        public String enrollmentStatus;
        public String ethinicity;
        public String externalId;
        public String familyId;
        public String firstName;
        public String fullName;
        public String gender;
        public String lastName;
        public String medhokId;
        public MemberAddresses_Z memberAddresses;
        public MemberEligibilities_Z memberEligibilities;
        public String middleName;
        public String otherLanguage;
        public String otherLanguageSource;
        public Pcp pcp;
        public String memberId;
        public String ssn;
        public String subscriberId;
        public String termDate;
    }

    public class MemberEligibilities {
        public List<EligibilityLine> eligibilityLine;
    }

    public class EligibilityLine {
        public String company;
        public String companyAccountGroup;
        public String companyAccounts;
        public String companyLob;
        public String contractNumber;
        public String divCode;
        public String effectivedate;
        public String eligGroup;
        public String eligPlan;
        public String enrollmentSource;
        public String extId;
        public String ipaGroup;
        public String medCenter;
        public String medhokId;
        public String medicaid;
        public String medicareno;
        public String memberQmbSlmb;
        public String pbpNumber;
        public String planCode;
        public String planName;
        public String primaryCarrierFlag;
        public String regionCenter;
        public String riderCode;
        public String ssn;
        public String subsidyLevel;
        public String termdate;
    }

    public class GetGrievanceAndAppealByCriteriaResponse {
        public List<Cases> cases;
    }

    public class Communication_Z {
    }

    public class UpdateUser {
        public String userId;
        public String firstName;
        public String lastName;
        public String title;
        public String email;
        public String phone;
        public String fax;
        public String terminated;
        public String accountLocked;
        public String comment;
        public String siteLocation;
        public String lastLogin;
        public String clientName;
    }

    public class MemberEligibilities_Z {
        public List<Communication_Z> eligibilityLine;
    }

    public class Pcp {
        public String address1;
        public String address2;
        public String city;
        public String fax;
        public String federalTaxID;
        public String firstName;
        public String fullName;
        public String groupNpi;
        public String lastName;
        public String licenseNumber;
        public String npi;
        public String organizationName;
        public String phone;
        public String providerID;
        public String providerType;
        public String region;
        public String state;
        public String zip;
    }

    public class Member {
        public String age;
        public String benefitPackageDescription;
        public String dateOfBirth;
        public String daysLastDischarge;
        public String effectiveDate;
        public String enrollmentStatus;
        public String ethinicity;
        public String externalId;
        public String familyId;
        public String firstName;
        public String fullName;
        public String gender;
        public String lastName;
        public String medhokId;
        public MemberAddresses memberAddresses;
        public MemberEligibilities memberEligibilities;
        public String middleName;
        public String otherLanguage;
        public String otherLanguageSource;
        public Pcp pcp;
        public String memberId;
        public String ssn;
        public String subscriberId;
        public String termDate;
    }

    public class MemberAddresses_Z {
        public List<Communication_Z> address;
    }

    public class Cases {
        public String caseNumber;
        public String caseType;
        public String category;
        public String caseCategory;
        public List<Communication> communication;
        public String createDate;
        public CreateUser createUser;
        public String dueDate;
        public Member member;
        public String medhokID;
        public String decisionDate;
        public List<Notification> notification;
        public String priority;
        public String receivedDate;
        public String requestType;
        public String status;
        public String statusReason;
        public String subCategory;
        public UpdateUser updateUser;
        public String validCase;
        public String datesOfService;
        public String reportedDate;
        Public String reportedAt;
	    Public String manualReceivedDate;
	    Public String providerDisplayName;
	    Public MemberEligibility memberEligibility;
        public MemberEligExtAttribute memberEligExtAttribute;
        public List<MedicalClaims> medicalClaims;
        public string notes;
        Public String finalResolutionCaseNote;
	    Public String allegation;
	    Public List<Documents> documents;
        public string requesterFirstname;
        public string requesterLastname;
        public string originalCaseNo;
        public string ireDecision;
		public string internalNotes;
        public string ireCaseType;
        public string ireDecisionDate;
        public string ireAppealType;
		public string ireAppealsDisputeType;
        public string irePriority;
        public string runningDueDate;
        public string creationType;
        public string originalCaseAppealsSubCategory;
    }
    public class MemberEligibility
    {
        public String eligGroup;
        public string eligPlan;

    }
    public class MemberEligExtAttribute
    {
        public String attribute8;
        public String attribute22;
    }
    public class MedicalClaims
    {
    	public string claimNumber;
    }
    public class Documents{
        public string attachementDate;
    }
    public class Communication {
        public String attempt;
        public String commDate;
        public String commMethod;
        public String commOutcome;
        public String commType;
        public String contactFirstName;
        public String contactLastName;
        public String details;
    }

    
    public static GreivanceAndAppealResponse_H_HUM parse(String json) {
        return (GreivanceAndAppealResponse_H_HUM) System.JSON.deserialize(json, GreivanceAndAppealResponse_H_HUM.class);
    }
}