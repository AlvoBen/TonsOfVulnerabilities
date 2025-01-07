/**************************************************************************************************************************************************
    @Apex class Name    : MBEPlusOIRequest_DTO_HUM
    @Function           :  This class serves as DTO class for MBEPlusrequest.
    @Version            : 1.0
    @test class          : MBEPlusOIRequest_DTO_T_HUM

Modification Log:
* Developer Name         Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Riddhi Mittal                             06/19/2020                  Original Version 
* Raja Sekhar Reddy M V                     08/20/2020                  PR00095673_US_1276005_DualstatusIndicator    
* Mohammed Noor								02/09/2021					USER STORY 1700807 - PR00093155 MF-18 Added new node OICoordinationOfBenefitList
************************************************************************************************************************************************* */

public class MBEPlusOIRequest_DTO_HUM {
    public GetMember GetMember;
    
    public class GetMember {
        public Member Member;
    }
    
    public class Member {
        public Contact Contact;
        public MedicaidIdentifierList MedicaidIdentifierList;
        public MedicareIdentifierList MedicareIdentifierList;
        public LongTermCareEligibilityList LongTermCareEligibilityList;
        public LowIncomeSubsidyLevelList LowIncomeSubsidyLevelList;
        public PlatformPointerList PlatformPointerList;
        public SoldProductList SoldProductList;
        public IndicatorList IndicatorList;
		public DualStatusList DualStatusList;
        public GetCriteria GetCriteria;
    }
    
    public class Contact {
        public ContactProfile ContactProfile;
    }
    
    public class ContactProfile {
        public PhoneList PhoneList;
        public EmailList EmailList;
        public AddressList AddressList;
    }
    
    public class PhoneList {
        public String PersonPhone;
    }
    
    public class EmailList {
        public String PersonEmail;
    }
    
    public class AddressList {
        public String PersonAddress;
        public String PlatformAddress;
        public String ResidentialAddress;
    }
    
    public class MedicaidIdentifierList {
        public String MedicaidIdentifier;
    }
    
    public class MedicareIdentifierList {
        public String MedicareIdentifier;
    }
    
    public class LongTermCareEligibilityList {
        public String LongTermCareEligibility;
    }
    
    public class LowIncomeSubsidyLevelList {
        public String LowIncomeSubsidyLevel;
    }
    
    public class PlatformPointerList {
        public String PlatformProxyKey;
    }
    
    public class SoldProductList {
        public List<SoldProduct> SoldProduct;
    }
    
    public class SoldProduct {
        public InsuranceProduct InsuranceProduct;
        public NonInsuranceProduct NonInsuranceProduct;
    }
    
    public class InsuranceProduct {
        public SoldProductDetailList SoldProductDetailList;
    }
    public class NonInsuranceProduct {
		public SoldProductDetailList_Z SoldProductDetailList;
	}
    public class SoldProductDetailList_Z {
		public SoldProductDetail_Z SoldProductDetail;
	}
    public class SoldProductDetailList {
        public SoldProductDetail SoldProductDetail;
    }
    public class SoldProductDetail_Z {
		public NonInsuranceCoverage NonInsuranceCoverage;
	}
    public class SoldProductDetail {
        public InsuranceCoverage InsuranceCoverage;
        //public NonInsuranceCoverage NonInsuranceCoverage;
    }
    
    public class InsuranceCoverage {
        public CoveredRoleList CoveredRoleList;
        public ReasonList ReasonList;
        public MemberPlatformPointerList MemberPlatformPointerList;
        public GroupPlatformPointerList GroupPlatformPointerList;
        public IndicatorList IndicatorList;
        public XReferenceList XReferenceList;
        public Network Network;
        public String OptionalSupplementalBenefitList;
        public String EnhancedHealthBenefit;
        public String LongTermCare;
        public CoordinationOfBenefitList CoordinationOfBenefitList;
		public OICoordinationOfBenefitList OICoordinationOfBenefitList;
        public AdvancedPremiumTaxCreditList AdvancedPremiumTaxCreditList;
        public WaitingPeriodList WaitingPeriodList;
        public EnrollmentInfo EnrollmentInfo;
        public String PrimaryCarePhysician;
        public PrimaryCarePhysicianList PrimaryCarePhysicianList;
        public String CostShareReduction;
        public CostShareReductionList CostShareReductionList;
        public OutOfAreaList OutOfAreaList;
    }
    Public Class MemberPlatformPointerList
    {
        Public String PlatformProxyKey = '';    
    }
    
    Public Class GroupPlatformPointerList
    {
        Public String PlatformProxyKey = '';     
    }
    public class CoveredRoleList {
        public CoveredRole CoveredRole;
    }
    
    public class CoveredRole {
        public CoveredRolePlatformPointerList CoveredRolePlatformPointerList;
    }
    public class CoveredRolePlatformPointerList{
    	Public String PlatformProxyKey = '';    
    }
    
    public class ReasonList {
        public String Reason;
    }
    
    public class IndicatorList {
        public String Indicator;
    }
	
	public class DualStatusList {
        public String DualStatus;
    }
    
    public class XReferenceList {
        public XReference XReference;
    }
    
    public class XReference {
        public String NameValueList;
    }
    
    public class Network {
        public NetworkDirectoryList NetworkDirectoryList;
    }

    public class NetworkDirectoryList {
        public String NetworkDirectory;
    }
    
    public class CoordinationOfBenefitList {
        public String CoordinationOfBenefit;
    }
	
	public class OICoordinationOfBenefitList {
    	public String OICoordinationOfBenefit;
    }
    
    public class AdvancedPremiumTaxCreditList {
        public String AdvancedPremiumTaxCredit;
    }
    
    public class WaitingPeriodList {
        public WaitingPeriod WaitingPeriod;
    }
    
    public class WaitingPeriod {
        public WaitingPeriodIndicatorList WaitingPeriodIndicatorList;
    }
    public class WaitingPeriodIndicatorList {
        public String Indicator;
    }
    
    public class EnrollmentInfo {
        public PriorCarrier PriorCarrier;
    }
    
    public class PriorCarrier {
        public IndicatorList IndicatorList;
    }
    
    public class PrimaryCarePhysicianList {
        public String PrimaryCarePhysician;
    }
    
    public class CostShareReductionList {
        public String CostShareReduction;
    }
    
    public class OutOfAreaList {
        public String OutofArea;
    }
    
    
    
    public class NonInsuranceCoverage {
        public CoveredRoleList CoveredRoleList;
        public ReasonList ReasonList;
        public MemberPlatformPointerList MemberPlatformPointerList;
        public GroupPlatformPointerList GroupPlatformPointerList;
        public IndicatorList IndicatorList;
        public XReferenceList XReferenceList;
        public String Utilization;
        public String ParentProductInfo;
    }
    
    public class GetCriteria {
        public PersonIdCriteria PersonIdCriteria;
    }

    public class PersonIdCriteria {
        public String AsOfDate;
        public PeriodList PeriodList;
        public String PersonId;
    }

    public class PeriodList {
        public List<String> Period;
    }
	
	public static MBEPlusOIRequest_DTO_HUM parse(String json) {
        return (MBEPlusOIRequest_DTO_HUM) System.JSON.deserialize(json, MBEPlusOIRequest_DTO_HUM.class);
    }
}