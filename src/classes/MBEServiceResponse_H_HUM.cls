/**************************************************************************** 
Apex Class Name :  MBEServiceResponse_H_HUM 
Version: 1.0  
Created Date     : February 03, 2016
Function         : Response helper for GetMember method in MBE Service
Test class       : MBEServiceResponse_T_HUM
****************************************************************************
Modification Log: 
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri    29768                   02/03/2016              Original Version
* ChiranjeeviRao Ravuri    30442                   03/02/2016              Added extra fields as per New response structure
* Shruthi Karanth          30795                   04/04/2016              Added the tag'ServiceCategoryDescription' to fix issue 221236.0001
* Raja Sekhar Reddy M V    132082                  09/18/2017              Added 4 fields as per New response structure to NetworkID
***************************************************************************************************/
Public Class MBEServiceResponse_H_HUM
{   
   Public GetMemberResponse GetMemberResponse;
   Public Class GetMemberResponse {
        Public Header Header;
        Public Body Body;
    }
    Public Class Header {
        Public ResponseHeader ResponseHeader;
    }
    Public Class ResponseHeader {
        Public String Environment ='';
        Public String Version = '';
        Public Server Server;
        Public Metrics Metrics;
        Public ResponseMetaData ResponseMetaData;
        Public PageMetaData PageMetaData;
    }
    Public Class Server {
        Public String Name = '';
    }
    Public Class Metrics {
        Public String Duration = '';
        Public String RequestTimeStamp = '';
        Public String ResponseTimeStamp = '';
    }
    Public Class ResponseMetaData {
        Public String Status = '';
        Public String ClientSessionID = '';
        Public String SessionID = '';
        Public DataAuthorizationConstraintList DataAuthorizationConstraintList;
    }
    Public Class DataAuthorizationConstraintList {
        Public List<DataAuthorizationConstraint> DataAuthorizationConstraint;
    }
    Public Class DataAuthorizationConstraint {
        Public String ConstraintType = '';
        Public String Tenant = '';
    }
    Public Class PageMetaData {
        Public String StartRecord = '';
        Public String EndRecord = '';
        Public String TotalRecords = '';
        Public String RecordsLimit = '';
       
        Public SortCriteria SortCriteria;
        Public String EndOfRecord = '';
        Public String FilteredRecords = '';
    }
    
    Public Class SortCriteria {
        Public FirstName FirstName;
        Public LastName LastName;
    }
    Public Class FirstName {
        Public String Sequence = '';  
        Public String Order = '';
    }
    Public Class LastName {
        Public String Sequence = '';  
        Public String Order = '';
    }
    Public Class Body {
        Public MemberMasterList MemberMasterList;
        Public MessageList MessageList;
    }
    Public Class MemberMasterList {
        Public List<MemberMaster> MemberMaster;
    }
    Public Class MemberMaster {
        Public String DateOfBirth = '';
        Public String DrivingLicenseNo = '';
        Public String FirstName = '';
        Public String Gender = '';
        Public String LastName = '';
        Public String MiddleInitial = '';
        Public String MiddleName = '';
        Public String NickName = '';
        Public String Prefix = '';
        Public String Ssn = '';
        Public EmailList EmailList;
        Public PhoneList PhoneList;
        Public AddressList AddressList;
        Public String MedicaidId = '';
        Public String MedicareId = '';
        Public String PersonId = '';
        Public MemberMasterPlatformPointerList MemberMasterPlatformPointerList;
        Public MemberPlatformList MemberPlatformList;
        Public BlockCodeList BlockCodeList;
        Public BlockOverrideList BlockOverrideList;
        Public LowIncomeSubsidyLevelList LowIncomeSubsidyLevelList;
        Public IndicatorList IndicatorList;
        Public MemberPolicyList MemberPolicyList;
        Public MemberProgramList MemberProgramList;
        Public LongTermCareEligibilityList LongTermCareEligibilityList;
        Public MemberIdList MemberIdList;
        Public HcfaDualEnrolledIndicator HcfaDualEnrolledIndicator;
        Public ContactProfileAdditional ContactProfileAdditional;
        Public String DeceasedDate = ''; 
    }
    Public Class EmailList {
        Public List<EmailAdditional> EmailAdditional;
        Public List<Email> Email;
    }
    Public Class Email {
        Public String EmailAddress = '';
        Public String Type = '';
    }
    Public Class PhoneList {
        Public List<PhoneAdditional> PhoneAdditional;
        Public List<Phone> Phone;
    }
    Public Class Phone {
        Public String Extension = '';
        Public String PhoneNumber = '';
        Public String Type = '';
    }
    Public Class AddressList {
        Public List<AddressAdditional> AddressAdditional;
        Public List<Address> Address;
    }
   
    Public Class Address {
        Public String Line1 = ''; 
        Public String Line2 = ''; 
        Public String Line3 = ''; 
        Public String Type = ''; 
        Public String City = '';
        Public String County = ''; 
        Public String StateCode = ''; 
        Public String ZipCode = ''; 
        Public String ZipPlus = '';
        Public String StartDate = '';
        Public String EndDate = '';
        Public GeoLocation GeoLocation;
    }
    Public Class GeoLocation {
        Public Position Position;
    }
    Public Class Position {
        Public Coordinates Coordinates;
        Public String AcquiredTime = '';
    }
    Public Class Coordinates {
        Public String Latitude = '';
        Public String Longitude = '';
        Public String Altitude = '';
    }
    Public Class MemberMasterPlatformPointerList {
        Public List<MemberMasterPlatformPointer> MemberMasterPlatformPointer;
    }
    Public Class MemberMasterPlatformPointer {
        Public String KeyType = '';
        Public String KeyValue = '';
        Public String Source = '';
        Public String ApplicationCode = '';
    }
    Public Class MemberPlatformList {
        Public List<MemberPlatform> MemberPlatform;
    }
    Public Class MemberPlatform {
        Public String DateOfBirth = '';
        Public String DrivingLicenseNo = '';
        Public String FirstName = '';
        Public String Gender = '';
        Public String LastName = '';
        Public String MiddleInitial = ''; 
        Public String MiddleName = ''; 
        Public String NickName = ''; 
        Public String Prefix = ''; 
        Public String Ssn = ''; 
        Public String EmailList = ''; 
        Public String PhoneList = ''; 
        Public String AddressList = '';
        Public String Source = ''; 
    }
    Public Class BlockCodeList {
        Public List<BlockCode> BlockCode;
    }
    Public Class BlockCode {
        Public String Name = '';
        Public String Value = '';
    }
    Public Class BlockOverrideList {
        Public List<BlockOverride> BlockOverride;
    }
    Public Class BlockOverride {
        Public String Name = '';
        Public String Value = '';
    }
    Public Class LowIncomeSubsidyLevelList {
        Public List<LowIncomeSubsidyLevel> LowIncomeSubsidyLevel;
    }
    Public Class LowIncomeSubsidyLevel {
        Public String CopayLevelCode = '';
        Public String EffectiveDate = '';
        Public String EndDate = '';
    }
    Public Class IndicatorList {
        Public List<Indicator> Indicator;
    }
    Public Class Indicator {
        Public String Name = '';
        Public String Value = '';
        
    }
    Public Class MemberPolicyList {
        Public List<MemberPolicy> MemberPolicy;
    }
    Public Class MemberPolicy {
        Public CoordinationOfBenefitList CoordinationOfBenefitList;
        Public MemberPolicyRoleList MemberPolicyRoleList;
        Public PrimaryCarePhysicianList PrimaryCarePhysicianList;
        Public OptionalSupplementalBenefitList OptionalSupplementalBenefitList;
        Public WaitingPeriodList WaitingPeriodList;
        Public NetworkList NetworkList;
        Public MemberPolicyProductPlatformPointerList MemberPolicyProductPlatformPointerList;
        Public Product Product;
        Public String Type = '';
        Public String CoverageType = '';
        Public GroupPolicy GroupPolicy;
        Public String PolicyProgramOfferKey = ''; 
        Public String StartDate = '';
        Public String EndDate = '';
        Public PlanCommentList PlanCommentList;
        Public MemberPolicyGroupPlatformPointerList MemberPolicyGroupPlatformPointerList;
        Public String LongTermCareProviderCode = ''; 
        Public String ProdPlanId = '';
        Public AdvancedPremiumTaxCreditList AdvancedPremiumTaxCreditList;
        Public XReferenceList XReferenceList;
        Public MemberPolicyPlatformPointerList MemberPolicyPlatformPointerList;
        Public String OriginalEffectiveDate = '';
        Public PolicyIndicatorList PolicyIndicatorList;
        Public EnrollmentInformation EnrollmentInformation;
        Public PrimaryCarePhysicianHistory PrimaryCarePhysicianHistory;
        Public CSRLevel CSRLevel;
        Public CSRLevelHistory CSRLevelHistory;
        Public OutOfAreaList OutOfAreaList;
        Public String ExchangeMemberId = '';
    }
    Public Class CoordinationOfBenefitList {
        Public List<CoordinationOfBenefit> CoordinationOfBenefit;
    }
    Public Class CoordinationOfBenefit {
        Public String CarrierName = ''; 
        Public String EffectiveDate = ''; 
        Public String EndDate = ''; 
        Public String Primary = '';
    }
    Public Class MemberPolicyRoleList {
        Public List<MemberPolicyRole> MemberPolicyRole;
    }
    Public Class MemberPolicyRole {
        Public String EndDate = ''; 
        Public String PersonId = '';
        Public String RoleType = ''; 
        Public String RelationshipToSubscriber = '';
        Public String StartDate = '';
        Public MemberPolicyRolePlatformPointerList MemberPolicyRolePlatformPointerList;
        Public EnhancedHealthBenefit EnhancedHealthBenefit;
    }
    Public Class MemberPolicyRolePlatformPointerList {
        Public List<MemberPolicyRolePlatformPointer> MemberPolicyRolePlatformPointer;
    }
    Public Class MemberPolicyRolePlatformPointer {
        Public String KeyType = '';
        Public String KeyValue = '';
        Public String Source = '';
        Public String ApplicationCode = '';
    }
    Public Class EnhancedHealthBenefit {
        Public String PediatricDentalTermDate = '';
    }
    Public Class PrimaryCarePhysicianList {
        Public List<PrimaryCarePhysician> PrimaryCarePhysician;
    }
    Public Class PrimaryCarePhysician {
        Public String EffectiveDate = '';
        Public String Name = '';
        Public String NpiSourceProviderKey = '';
        Public String SourceProviderKey = ''; 
        Public String SourceGrouperNumber = ''; 
        Public String GrouperName = ''; 
    }
    Public Class OptionalSupplementalBenefitList {
        Public List<OptionalSupplementalBenefit> OptionalSupplementalBenefit;
    }
    Public Class OptionalSupplementalBenefit {
        Public String CategoryDescription = ''; 
        Public String EndDate = ''; 
        Public String ItemCode = '';
        Public String ItemTypeCode = '';
        Public String PackageKey = ''; 
        Public String PackageName = ''; 
        Public String StartDate = '';
    }
    Public Class WaitingPeriodList {
        Public List<WaitingPeriod> WaitingPeriod;
    }
    Public Class WaitingPeriod {
        Public String ServiceCategoryID = '';
        Public String WaitBeginDate = '';
        Public String WaitEndDate = '';
        Public String WaitTypeCode = '';
        Public WaitingPeriodIndicatorList WaitingPeriodIndicatorList;
        Public String WaiveAccidentIndicator = '';
        Public String PersonPriorCoverageReduction = '';
        Public String MemberEligibilityReduction = '';
        Public String ServiceCategoryDescription = '';
    }
    Public Class WaitingPeriodIndicatorList {
        Public List<Indicator> Indicator;
    }
    
    Public Class NetworkList {
        Public List<Network> Network;
    }
    Public Class Network {
        Public String NetworkId = '';
        Public String CASH1Network = '';
        Public String CASH2Network = '';
        Public String CASD1Network = '';
        Public String CASD2Network = ''; 
        
        Public NetworkDirectoryList NetworkDirectoryList;
    }
    Public Class NetworkDirectoryList {
        Public List<NetworkDirectory> NetworkDirectory;
    }
    Public Class NetworkDirectory {
        Public String ActionTimestamp = '';
        Public String DirectoryId = '';
        Public String DisplayName = '';
        Public String FormNumber = '';
        Public String Primary = '';
    }
    Public Class MemberPolicyProductPlatformPointerList {
        Public List<MemberPolicyProductPlatformPointer> MemberPolicyProductPlatformPointer;
    }
    Public Class MemberPolicyProductPlatformPointer {
        Public String KeyType = '';
        Public String KeyValue = '';
        Public String Source = '';
        Public String ApplicationCode = '';
    }
    Public Class Product {
        Public RiderList RiderList;
        Public BenefitList BenefitList;
        Public XReferenceList XReferenceList;
        Public String AlternateDescription = ''; 
        Public String CarryOverCredit = '';
        Public String ClassificationCode = '';
        Public String CustomerCarveOutIndicator = '';
        Public String CustomPlan = '';
        Public String DeductibleTypeCode = '';
        Public String Description = '';
        Public String FedQualified = '';
        Public String GroupSize = '';
        Public String HighDeductibleHealthPlan = '';
        Public String MajorLineOfBusiness = '';
        Public String MinorLineOfBusiness = '';
        Public String PcaDeductibleTypeCode = '';
        Public String PcpRequired = ''; 
        Public String PersonalCareAccountIndicator = '';
        Public String ProductEffectiveDate = '';
        Public String ProductEndDate = '';
        Public String ProductLine = '';
        Public String ProductLineCode = '';
        Public String ProductTypeCode = '';
        Public String RxMedicalIntegratedDeductibleIndicator = '';
        Public String RxsSpecialityIndicator = '';
        Public String SegmentType = '';
        Public String SourcePlanId = '';
    }
    Public Class RiderList {
        Public List<Rider> Rider;
    }
    Public Class Rider {
        Public String CarveOut = '';
        Public String DisplaySequenceNumber = ''; 
        Public String IntegratedBenefit = '';  
        Public String RiderId = ''; 
        Public String RiderName = '';
        Public String TypeCode = ''; 
        Public String VendorId = ''; 
    }
    Public Class BenefitList {
        Public List<Benefit> Benefit;
    }
    Public Class Benefit {
        Public String AdaDescription = '';   
        Public String AppliesToDeductible = '';  
        Public String BenefitDisplayKey = '';    
        Public String BenefitLevel = ''; 
        Public String BenefitShortDescription = '';  
        Public String Description = '';  
        Public String IvrPlanDescCode = '';  
        Public String LimitId = ''; 
        Public String LimitName = '';    
        Public String LimitSequence = '';    
        Public String LimitText = '';    
        Public String LisCopayLevel = '';    
        Public String PlanBenSeqNbr = '';    
        Public String ReimbursementRuleName = '';    
        Public String ReimbursementRuleSequence = '';    
        Public String SourceApplicationCode = '';    
        Public EligibilityBenefitCodeList EligibilityBenefitCodeList;
        Public HealthCareServicesDirectoryCodeList HealthCareServicesDirectoryCodeList;
    }
    Public Class EligibilityBenefitCodeList {
        Public List<BenefitCode> BenefitCode;
    }
    Public Class BenefitCode {
        Public String Name = '';
        Public String Value = '';    
    }
    Public Class HealthCareServicesDirectoryCodeList {
        Public List<DirectoryCode> DirectoryCode;
    }
    Public Class DirectoryCode {
        Public String Name = '';
        Public String Value = '';    
    }
    Public Class XReferenceList {
        Public List<XReference> XReference;
    }
    Public Class XReference {
        Public String KeyType = '';
        Public String KeyValue = '';
        Public String Source = '';
        Public String ApplicationCode = '';
        Public String Name = '';
        Public NameValueList NameValueList;
    }
    Public Class NameValueList {
        Public List<NameValue> NameValue;
    }
    Public Class NameValue {
        Public String Name = ''; 
        Public String Value = '';    
    }
    Public Class GroupPolicy {
        Public String AsoIndicator = '';
        Public String BillingLineOfBusiness = '';
        Public String EndDate = '';
        Public String GroupName = '';
        Public String MaximumDependantAge = '';
        Public String MaximumStudentAge = '';
        Public String OperationalMajorLineOfBusiness = '';
        Public String StartDate = '';
        Public String StateOfIssue = '';
        Public String TermReason = '';
        Public String Type = '';
        Public String UnionIndicator = '';
    }
    Public Class PlanCommentList {
        Public List<PlanComment> PlanComment;
    }
    Public Class PlanComment {
        Public String CommentText = '';  
    }
    Public Class MemberPolicyGroupPlatformPointerList {
        Public List<MemberPolicyGroupPlatformPointer> MemberPolicyGroupPlatformPointer;
    }
    Public Class MemberPolicyGroupPlatformPointer {
        Public String KeyType = '';  
        Public String KeyValue = '';
        Public String Source = '';
        Public String ApplicationCode = ''; 
    }
    Public Class AdvancedPremiumTaxCreditList {
        Public List<AdvancedPremiumTaxCredit> AdvancedPremiumTaxCredit;
    }
    Public Class AdvancedPremiumTaxCredit {
        Public String Amount = '';
        Public String EffectiveDate = '';
        Public String EndDate = '';
        Public String IndividualAmount = '';
        Public String OtherAmount1 = '';
        Public String OtherAmount2 = '';
        Public String OtherAmount3 = '';
        Public String OtherAmount4 = '';
        Public String PremiumAmount = '';
        Public String SeqNumber = '';
    }
    Public Class MemberPolicyPlatformPointerList {
        Public List<MemberPolicyPlatformPointer> MemberPolicyPlatformPointer;
    }
    Public Class MemberPolicyPlatformPointer {
        Public String KeyType = '';  
        Public String KeyValue = '';
        Public String Source = '';
        Public String ApplicationCode = ''; 
    }
    Public Class PolicyIndicatorList {
        Public List<Indicator> Indicator;
    }
    Public Class EnrollmentInformation {
        Public String EnrollmentType = ''; 
        Public String DateofHire = ''; 
        Public String TimelyIndicator = ''; 
        Public PriorCarrier PriorCarrier;
    }
    Public Class PriorCarrier {
        Public String CarrierName = '';
        Public String EffectiveDate = ''; 
        Public String EndDate = '';
        Public PriorCarrierIndicatorList PriorCarrierIndicatorList;
    }
    Public Class PriorCarrierIndicatorList {
        Public List<Indicator> Indicator;
    }
    Public Class PrimaryCarePhysicianHistory {
        Public List<PrimaryCarePhysician> PrimaryCarePhysician;
    }
    Public Class CSRLevel {
        Public String CSRLevel = ''; 
        Public String EndDate = ''; 
        Public String ModifiedDate = ''; 
        Public String StartDate = '';  
    }
    Public Class CSRLevelHistory {
        Public List<CostShareReduction> CostShareReduction;
    }
    Public Class CostShareReduction {
        Public String CSRLevel = ''; 
        Public String EndDate = ''; 
        Public String ModifiedDate = '';
        Public String StartDate = '';
    }
    Public Class OutOfAreaList {
        Public List<OutOfArea> OutOfArea;
    }
    Public Class OutOfArea {
        Public String StartDate = '';
        Public String EndDate = ''; 
    }
    Public Class MemberProgramList {
        Public List<MemberProgram> MemberProgram;
    }
    Public Class MemberProgram {
        Public String EndDate = ''; 
        Public String Type = ''; 
        Public String ProductOfferKey = ''; 
        Public String StartDate = ''; 
        Public MemberProgramRoleList MemberProgramRoleList;
        Public GroupProgram GroupProgram;
        Public String ProgramOptOut = ''; 
        Public String PolicyProgramOfferKey = '';
        Public MemberProgramGroupPlatformPointerList MemberProgramGroupPlatformPointerList;
        Public Order Order;
        Public XReferenceList XReferenceList;
        Public MemberProgramProductPlatformPointerList MemberProgramProductPlatformPointerList;
        Public MemberProgramPlatformPointerList MemberProgramPlatformPointerList;
        Public MemberProgramEndReasonList MemberProgramEndReasonList;
    }
    Public Class MemberProgramRoleList {
        Public List<MemberProgramRole> MemberProgramRole;
    }
    Public Class MemberProgramRole {
        Public String PersonId = ''; 
        Public String RoleType = ''; 
        Public PlatformPointerList PlatformPointerList;
        Public String RelationshipToSubscriber = '';
    }
    Public Class PlatformPointerList {
        Public List<PlatformPointer> PlatformPointer;
    }
    Public Class PlatformPointer {
        Public String KeyType = ''; 
        Public String KeyValue = ''; 
        Public String Source = ''; 
        Public String ApplicationCode = ''; 
    }
    Public Class GroupProgram {
        Public String EndDate = '';
        Public String GroupSinceDate = '';
        Public String IsEmbedded = '';
        Public String ProductGroup = ''; 
        Public String ProductFamily = '';
        Public String ProgramOption = '';
        Public String ProgramPlan = '';
        Public String ProgramOptOut = '';
        Public String StartDate = '';
    }
    Public Class MemberProgramGroupPlatformPointerList {
        Public List<MemberProgramGroupPlatformPointer> MemberProgramGroupPlatformPointer;
    }
    Public Class MemberProgramGroupPlatformPointer {
        Public String KeyType = '';  
        Public String KeyValue = ''; 
        Public String Source = '';   
        Public String ApplicationCode = '';
    }
    Public Class Order {
        Public RiderList RiderList;
        Public BenefitList BenefitList;
        Public String AlternateDescription = ''; 
        Public String CarryOverCredit = ''; 
        Public String ClassificationCode = '';  
        Public String CustomerCarveOutIndicator = '';
        Public String CustomPlan = ''; 
        Public String DeductibleTypeCode = ''; 
        Public String Description = '';  
        Public String FedQualified = ''; 
        Public String GroupSize = ''; 
        Public String HighDeductibleHealthPlan = ''; 
        Public String MajorLineOfBusiness = '';
        Public String MinorLineOfBusiness = ''; 
        Public String PcaDeductibleTypeCode = ''; 
        Public String PcpRequired = '';
        Public String PersonalCareAccountIndicator = '';
        Public String ProductEffectiveDate = '';
        Public String ProductEndDate = ''; 
        Public String ProductLine = ''; 
        Public String ProductLineCode = '';
        Public String ProductTypeCode = ''; 
        Public String RxMedicalIntegratedDeductibleIndicator = '';   
        Public String RxsSpecialityIndicator = '';   
        Public String SegmentType = '';  
        Public String SourcePlanId = ''; 
        Public XReferenceList XReferenceList;
        Public String Payer = '';  
        Public String OrderId = ''; 
        Public Utilization Utilization;
    }
    Public Class Utilization {
        Public String BeginDate = '';   
        Public String EndDate = '';  
        Public String MaximumLimit = ''; 
        Public String Period = '';  
        Public String PerPeriod = '';  
    }
    Public Class MemberProgramProductPlatformPointerList {
        Public List<MemberProgramProductPlatformPointer> MemberProgramProductPlatformPointer;
    }
    Public Class MemberProgramProductPlatformPointer {
        Public String KeyType = ''; 
        Public String KeyValue = ''; 
        Public String Source = '';  
        Public String ApplicationCode = ''; 
    }
    Public Class MemberProgramPlatformPointerList {
        Public List<MemberProgramPlatformPointer> MemberProgramPlatformPointer;
    }
    Public Class MemberProgramPlatformPointer {
        Public String KeyType = ''; 
        Public String KeyValue = ''; 
        Public String Source = '';   
        Public String ApplicationCode = ''; 
    }
    Public Class MemberProgramEndReasonList {
        Public List<MemberProgramEndReason> MemberProgramEndReason;
    }
    Public Class MemberProgramEndReason {
        Public String Code = ''; 
        Public String Description = '';
        Public String Type = '';
    }
    Public Class LongTermCareEligibilityList {
        Public List<LongTermCareEligibility> LongTermCareEligibility;
    }
    Public Class LongTermCareEligibility {
        Public String EffectiveDate = ''; 
        Public String EndDate = '';
        Public String PlanTypeCode = ''; 
        Public String ProgramCategoryCode = ''; 
        Public String StateCode = '';
    }
    Public Class MemberIdList {
        Public List<MemberIdentity> MemberIdentity;
    }
    Public Class MemberIdentity {
        Public String MemberId = ''; 
        Public String DependentCode = '';
        Public String MajorLineOfBusiness = '';
        Public String RelationshipToSubscriber = '';
        Public String SubscriberId = '';
        Public MemberMasterPlatformPointerList MemberMasterPlatformPointerList;
        Public GroupIdList GroupIdList;
    }
    Public Class GroupIdList {
        Public List<GroupId> GroupId;
    }
    Public Class GroupId {
        Public String GroupId = '';  
        Public String DivisionId = '';
        Public String ClassId = ''; 
        Public String CoverageId = '';
    }
    Public Class HcfaDualEnrolledIndicator {
        Public String Name = ''; 
        Public String Value = '';  
        Public SourcePlatform SourcePlatform;
    }
    Public Class SourcePlatform {
        Public String KeyType = '';
        Public String KeyValue = '';
        Public String Source = ''; 
        Public String ApplicationCode = '';
    }
    Public Class ContactProfileAdditional {
        Public EmailList EmailList;
        Public PhoneList PhoneList;
        Public AddressList AddressList;
    }
    Public Class EmailAdditional {
        Public String EmailAddress = '';
        Public String Type = '';
        Public EmailAdditionalIdentifier EmailAdditionalIdentifier;
        Public String StartDate = ''; 
        Public String EndDate = ''; 
        Public String Priority = '';
        Public String IsActive = '';
        Public String ValidationLevel = '';
        Public EmailAdditionalAudit EmailAdditionalAudit;
    }
    Public Class EmailAdditionalIdentifier {
        Public String IdentifierId = '';
        Public String IdentifierRowVersion = '';
    }
    Public Class EmailAdditionalAudit {
        Public String SourceName = ''; 
        Public String SourceUserID = '';
        Public String SourceCreateDate = '';
        Public String SourceModifiedDate = '';
    }
    Public Class PhoneAdditional {
        Public PhoneAdditionalIdentifier PhoneAdditionalIdentifier;
        Public String Type = ''; 
        Public String CountryCode = '';
        
      
        Public String PhoneNumber = ''; 
        Public String Extension = '';
        Public String SmsEnabled = '';
        Public String SmartPhoneEnabled = '';
        Public String TelephoneNetwork = ''; 
        Public String StartDate = '';
        Public String EndDate = ''; 
        Public String IsActive = ''; 
        Public String Priority = '';
        Public String ValidationLevel = '';
        Public PhoneAdditionalAudit PhoneAdditionalAudit;
    }
    Public Class PhoneAdditionalIdentifier {
        Public String IdentifierId = ''; 
        Public String IdentifierRowVersion = ''; 
    }
    Public Class PhoneAdditionalAudit {
        Public String SourceName = ''; 
        Public String SourceUserID = ''; 
        Public String SourceCreateDate = '';
        Public String SourceModifiedDate = '';
    }
    Public Class AddressAdditional {
        Public String Line1 = ''; 
        Public String Line2 = '';  
        Public String Line3 = '';  
        Public String Type = ''; 
        Public String City = ''; 
        Public String County = ''; 
        Public String StateCode = '';
        Public String ZipCode = ''; 
        Public String ZipPlus = '';
        Public String StartDate = ''; 
        Public String EndDate = ''; 
        Public GeoLocation GeoLocation;
        Public AddressAdditionalIdentifier AddressAdditionalIdentifier;
        Public String DeliveryFrequency = ''; 
        Public String Priority = '';
        Public String IsActive = ''; 
        Public String IsDeliverable = ''; 
        Public String IsDeliverableEndDate = ''; 
        Public String ValidationLevel = ''; 
        Public PostalAddressAdditionalAudit PostalAddressAdditionalAudit;
    }
    Public Class AddressAdditionalIdentifier {
        Public String IdentifierId = '';
        Public String IdentifierRowVersion = '';
    }
    Public Class PostalAddressAdditionalAudit {
        Public String SourceName = ''; 
        Public String SourceUserID = ''; 
        Public String SourceCreateDate = ''; 
        Public String SourceModifiedDate = '';  
    }
    Public Class MessageList {
        Public List<Message> Message;
    }
    Public Class Message {
        Public String Type = ''; 
        Public String Code = ''; 
        Public String Source = '';   
        Public String Description = '';  
    }     

}