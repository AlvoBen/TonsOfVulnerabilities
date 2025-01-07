/**************************************************************************** 
Apex Class Name  : MBEServiceRequest_H_HUM 
Version          : 1.0  
Created Date     : February 02, 2016
Function         : Request helper for GetMember method in MBE Service
Test Class       : MBEServiceRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri    29768                02/02/2015                    Original Version
***************************************************************************************************/
public class MBEServiceRequest_H_HUM
{   
   public GetMember GetMember;
   public class GetMember {
        public MemberMasterList MemberMasterList;
    }
   public class MemberMasterList {
        public MemberMaster MemberMaster;
    }
   public class MemberMaster {
        public EmailList EmailList;
        public PhoneList PhoneList;
        public AddressList AddressList;
        public MemberMasterPlatformPointerList MemberMasterPlatformPointerList;
        public MemberPlatformList MemberPlatformList;
        public BlockCodeList BlockCodeList;
        public BlockOverrideList BlockOverrideList;
        public LowIncomeSubsidyLevelList LowIncomeSubsidyLevelList;
        public IndicatorList IndicatorList;
        public MemberPolicyList MemberPolicyList;
        public MemberProgramList MemberProgramList;
        public LongTermCareEligibilityList LongTermCareEligibilityList;
        public MemberIdList MemberIdList;
        public HcfaDualEnrolledIndicator HcfaDualEnrolledIndicator;
        public ContactProfileAdditional ContactProfileAdditional;
        public MemberMasterCriteriaList MemberMasterCriteriaList;
    }
    public class EmailList {
    }
    public class PhoneList {
    }
    public class AddressList {
    }
    public class MemberMasterPlatformPointerList {
    }
    public class MemberPlatformList {
        public MemberPlatform MemberPlatform;
    }
    public class MemberPlatform {
        public EmailList EmailList;
        public PhoneList PhoneList;
        public AddressList AddressList;
    }
    public class BlockCodeList {
    }
    public class BlockOverrideList {
    }
    public class LowIncomeSubsidyLevelList {
    }
    public class IndicatorList {
    }
    public class MemberPolicyList {
        public MemberPolicy MemberPolicy;
    }
    public class MemberPolicy {
        public CoordinationOfBenefitList CoordinationOfBenefitList;
        public MemberPolicyRoleList MemberPolicyRoleList;
        public PrimaryCarePhysicianList PrimaryCarePhysicianList;
        public OptionalSupplementalBenefitList OptionalSupplementalBenefitList;
        public WaitingPeriodList WaitingPeriodList;
        public NetworkList NetworkList;
        public MemberPolicyProductPlatformPointerList MemberPolicyProductPlatformPointerList;
        public Product Product;
        public GroupPolicy GroupPolicy;
        public PlanCommentList PlanCommentList;
        public MemberPolicyGroupPlatformPointerList MemberPolicyGroupPlatformPointerList;
        public AdvancedPremiumTaxCreditList AdvancedPremiumTaxCreditList;
        public XReferenceList XReferenceList;
        public MemberPolicyPlatformPointerList MemberPolicyPlatformPointerList;
        public PolicyIndicatorList PolicyIndicatorList;
        public EnrollmentInformation EnrollmentInformation;
        public PrimaryCarePhysicianHistory PrimaryCarePhysicianHistory;
        public CSRLevel CSRLevel;
        public CSRLevelHistory CSRLevelHistory;
        public OutOfAreaList OutOfAreaList;
    }
    public class CoordinationOfBenefitList {
    }
    public class MemberPolicyRoleList {
        public MemberPolicyRole MemberPolicyRole;
    }
    public class MemberPolicyRole {
        public MemberPolicyRolePlatformPointerList MemberPolicyRolePlatformPointerList;
        public EnhancedHealthBenefit EnhancedHealthBenefit;
    }
    public class MemberPolicyRolePlatformPointerList {
    }
    public class EnhancedHealthBenefit {
    }
    public class PrimaryCarePhysicianList {
    }
    public class OptionalSupplementalBenefitList {
    }
    public class WaitingPeriodList {
    }
    public class NetworkList {
        public Network Network;
    }
    public class Network {
        public NetworkDirectoryList NetworkDirectoryList;
    }
    public class NetworkDirectoryList {
    }
    public class MemberPolicyProductPlatformPointerList {
    }
    public class Product {
        public RiderList RiderList;
        public BenefitList BenefitList;
    }
    public class RiderList {
    }
    public class BenefitList {
        public Benefit Benefit;
    }
    public class Benefit {
        public EligibilityBenefitCodeList EligibilityBenefitCodeList;
        public HealthCareServicesDirectoryCodeList HealthCareServicesDirectoryCodeList;
    }
    public class EligibilityBenefitCodeList {
    }
    public class HealthCareServicesDirectoryCodeList {
    }
    public class XReferenceList {
        public XReference XReference;
    }
    public class GroupPolicy {
    }
    public class PlanCommentList {
    }
    public class MemberPolicyGroupPlatformPointerList {
    }
    public class AdvancedPremiumTaxCreditList {
    }
    public class XReference {
        public NameValueList NameValueList;
    }
    public class NameValueList {
    }
    public class MemberPolicyPlatformPointerList {
    }
    public class PolicyIndicatorList {
    }
    public class EnrollmentInformation {
        public PriorCarrier PriorCarrier;
    }
    public class PriorCarrier {
        public PriorCarrierIndicatorList PriorCarrierIndicatorList;
    }
    public class PriorCarrierIndicatorList {
    }
    public class PrimaryCarePhysicianHistory {
    }
    public class CSRLevel {
    }
    public class CSRLevelHistory {
    }
    public class OutOfAreaList {
    }
    public class MemberProgramList {
        public MemberProgram MemberProgram;
    }
    public class MemberProgram {
        public MemberProgramRoleList MemberProgramRoleList;
        public GroupProgram GroupProgram;
        public MemberProgramGroupPlatformPointerList MemberProgramGroupPlatformPointerList;
        public Order Order;
        public XReferenceList XReferenceList;
        public MemberProgramProductPlatformPointerList MemberProgramProductPlatformPointerList;
        public MemberProgramPlatformPointerList MemberProgramPlatformPointerList;
        public MemberProgramEndReasonList MemberProgramEndReasonList;
    }
    public class MemberProgramRoleList {
        public MemberProgramRole MemberProgramRole;
    }
    public class MemberProgramRole {
        public PlatformPointerList PlatformPointerList;
    }
    public class PlatformPointerList {
    }
    public class GroupProgram {
    }
    public class MemberProgramGroupPlatformPointerList {
    }
    public class Order {
        public RiderList RiderList;
        public BenefitList BenefitList;
        public XReferenceList XReferenceList;
        public Utilization Utilization;
    }
    public class Utilization {
    }
    public class MemberProgramProductPlatformPointerList {
    }
    public class MemberProgramPlatformPointerList {
    }
    public class MemberProgramEndReasonList {
    }
    public class LongTermCareEligibilityList {
    }
    public class MemberIdList {
        public MemberIdentity MemberIdentity;
    }
    public class MemberIdentity {
        public MemberMasterPlatformPointerList MemberMasterPlatformPointerList;
        public GroupIdList GroupIdList;
    }
    public class GroupIdList {
    }
    public class HcfaDualEnrolledIndicator {
    }
    public class ContactProfileAdditional {
        public EmailList EmailList;
        public PhoneList PhoneList;
        public AddressList AddressList;
    }
    public class MemberMasterCriteriaList {
        public MemberMasterCriteria MemberMasterCriteria;
    }
    public class MemberMasterCriteria {
        public String type = ''; 
        public String PersonId = '';
        public String AsOfDate = ''; 
        public Period PeriodList;
    }
    
    public class Period {
        public list<String> Period;
    }

}