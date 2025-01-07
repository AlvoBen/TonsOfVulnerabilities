/**************************************************************************************************************************************************
    @Apex class Name    : MBEPlusOIResponse_DTO_HUM
    @Function           :  This class serves as DTO for MBEPlus OI Response.
    @Version            : 1.0
    @reference          : ValidOIPolicyMemberID_D_HUM

Modification Log:
* Developer Name         Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Riddhi Mittal                             06/19/2020                  Original Version 
* Raja Sekhar Reddy M V                     08/20/2020	                PR00095673_US_1276005_DualstatusIndicator
* Ajay Chakradhar                           10/05/2020                  US 1467120 
* Mohammed Noor								02/09/2021					USER STORY 1700807 - PR00093155 MF-18 Added new node OICoordinationOfBenefitList
************************************************************************************************************************************************* */

public class MBEPlusOIResponse_DTO_HUM {
    public GetMemberResponse GetMemberResponse {get;set;} 

    public class GetMemberResponse {
        public Header Header {get;set;} 
        public Body Body {get;set;}
    }
    
    public class Header {
        public ResponseHeader ResponseHeader {get;set;}
    }
	
    public class ResponseHeader {
        public String Environment {get;set;} 
        public String Version {get;set;} 
        public Server Server {get;set;} 
        public Metrics Metrics {get;set;} 
        public ResponseMetaData ResponseMetaData {get;set;}
    }
	
	public class Server {
        public String Name {get;set;}
    }
    
	public class Metrics {
        public String Duration {get;set;} 
        public String RequestTimeStamp {get;set;} 
        public String ResponseTimeStamp {get;set;}
    }
	
	public class ResponseMetaData {
        public String Status {get;set;} 
        public String ClientSessionID {get;set;} 
        public String SessionID {get;set;} 
        public DataAuthorizationConstraintList DataAuthorizationConstraintList {get;set;} 
	}
	
	public class DataAuthorizationConstraintList {
        public List<DataAuthorizationConstraint> DataAuthorizationConstraint {get;set;} 
	}
	
	public class DataAuthorizationConstraint {
        public String ConstraintType {get;set;} 
        public String Tenant {get;set;} 
        public String Source {get;set;}
	}
	
	public class Body {
		public MessageList MessageList {get;set;}
        public Member Member {get;set;} 
	}
	
	public class MessageList {
        public List<Message> Message {get;set;} 
    }
	
	public class Message {
        public String Type_Z {get;set;} // in json: Type
        public String Code {get;set;} 
        public String Source {get;set;} 
        public String Description {get;set;} 
    }
	
	public class Member {
        public String DateOfBirth {get;set;} 
        public String FirstName {get;set;} 
        public String Gender {get;set;} 
        public String LastName {get;set;} 
        public String MiddleInitial {get;set;} 
        public String Ssn {get;set;} 
        public Contact Contact {get;set;} 
        public MedicareIdentifierList MedicareIdentifierList {get;set;} 
        public String MasterId {get;set;} 
        public PlatformPointerList PlatformPointerList {get;set;} 
        public SoldProductList SoldProductList {get;set;} 
        public IndicatorList IndicatorList {get;set;} 
		public List<DualStatus> DualStatusList;
    }
	
	public class Contact {
        public ContactProfile ContactProfile {get;set;} 
	}
	
    public class ContactProfile {
        public EmailList EmailList {get;set;} 
        public PhoneList PhoneList {get;set;} 
        public AddressList AddressList {get;set;}
    }
	
    public class EmailList {
        public List<PersonEmail> PersonEmail {get;set;} 
	}
	
	public class PersonEmail {
        public String Address {get;set;} 
        public String Type_Z {get;set;} // in json: Type
    }
	
	public class PhoneList {
        public List<PersonPhone> PersonPhone {get;set;}
    }
	
	public class PersonPhone {
        public String PhoneNumber {get;set;} 
        public String Type_Z {get;set;} // in json: Type
    }
	
	public class AddressList {
        public List<PersonAddress> PersonAddress {get;set;} 
    }
	
	public class PersonAddress {
        public String AddressLine1 {get;set;} 
        public String City {get;set;} 
        public String StateCode {get;set;} 
        public String County {get;set;} 
        public String ZipCode {get;set;} 
        public String ZipPlus {get;set;} 
        public String Type_Z {get;set;} // in json: Type
        public GeoLocation GeoLocation {get;set;} 
        public String CountyCode {get;set;} 
        public PersonIdentifier PersonIdentifier {get;set;} 
        public String DeliveryCode {get;set;} 
        public String ForeignPostalCode {get;set;} 
        public String FIPSStateCode {get;set;} 
        public String FIPSCountryCode {get;set;} 
        public String StartDate {get;set;} 
        public String EndDate {get;set;} 
    }
	
	public class GeoLocation {
        public Position Position {get;set;} 
    }
	
	public class Position {
        public Coordinates Coordinates {get;set;} 
        public String AcquiredTime {get;set;}
    }
    
    public class Coordinates {
        public String Latitude {get;set;} 
        public String Longitude {get;set;} 
    }
	
	public class PersonIdentifier {
        public PlatformProxyKey PlatformProxyKey {get;set;}
    }
	
	public class PlatformProxyKey {
        public String Application {get;set;} 
        public String KeyType {get;set;} 
        public String KeyValue {get;set;} 
        public String Source {get;set;} 
    }
	
	public class MedicareIdentifierList {
        public List<MedicareIdentifier> MedicareIdentifier {get;set;} 
    }
	
	public class MedicareIdentifier {
        public String MedicareId {get;set;} 
	}
	
	public class PlatformPointerList {
        public List<PlatformProxyKey> PlatformProxyKey {get;set;} 
	}
	
	public class IndicatorList {
        public List<Indicator> Indicator {get;set;} 
	}
	
	public class Indicator {
        public String Name {get;set;} 
        public String Value {get;set;} 
    }
	
	public class DualStatus {
		public String EffectiveDate {get;set;} 
		public String EndDate {get;set;} 
		public String Value {get;set;} 
		public String Code {get;set;} 
		public Boolean Indicator {get;set;} 
	}
	
	public class SoldProductList {
        public List<SoldProduct> SoldProduct {get;set;} 
	}
	
	public class SoldProduct {
        public InsuranceProduct InsuranceProduct {get;set;} 
        public NonInsuranceProduct NonInsuranceProduct {get;set;} 
	}
	
	public class InsuranceProduct {
        public String AlternateDescription {get;set;} 
        public String Description {get;set;} 
        public String DeductibleTypeCode {get;set;} 
		public String MajorLineOfBusinessCode {get;set;} 
        public String MajorLineOfBusinessDescription {get;set;} 
        public String ProductStartDate {get;set;} 
        public String ProductEndDate {get;set;} 
        public String ProductId {get;set;} 
        public String Source {get;set;} 
        public String ProductLineCode {get;set;} 
        public String ProductLineDescription {get;set;} 
        public String ProductTypeCode {get;set;} 
        public String SegmentType {get;set;} 
        public SoldProductDetailList SoldProductDetailList {get;set;} 
        public String IsCarryOverCredit {get;set;} 
        public String IsCustomerCarveOut {get;set;} 
        public String IsCustomPlan {get;set;} 
        public String IsFedQualified {get;set;} 
        public String IsHdhp {get;set;} 
        public String IsPca {get;set;} 
        public String IsPcpRequired {get;set;} 
        public String IsRxMedicalIntegratedDeductible {get;set;} 
        public String IsRxSpeciality {get;set;}
	 
	}
	
	public class NonInsuranceProduct {
        public String Description {get;set;} 
        public String OfferId {get;set;} 
        public String ProductStartDate {get;set;} 
        public String ProductEndDate {get;set;} 
        public String ProductId {get;set;} 
        public String Source {get;set;} 
        public String ProductLineCode {get;set;} 
        public SoldProductDetailList SoldProductDetailList {get;set;} 
        public String ProductOptionKey {get;set;} 

    }
	
	public class SoldProductDetailList {
        public List<SoldProductDetail> SoldProductDetail {get;set;} 
	}
	
    public class SoldProductDetail {
        public InsuranceCoverage InsuranceCoverage {get;set;} 
		public NonInsuranceCoverage NonInsuranceCoverage {get;set;}
	}
	
    public class InsuranceCoverage {
        public String EndDate {get;set;} 
        public String StartDate {get;set;} 
        public CoveredRoleList CoveredRoleList {get;set;} 
        public String PolicyType {get;set;} 
        public String CoverageType {get;set;} 
        public ReasonList ReasonList {get;set;} 
        public PlatformPointerList GroupPlatformPointerList {get;set;} 
        public IndicatorList IndicatorList {get;set;} 
        public String ContractId {get;set;} 
        public Network Network {get;set;} 
        public String LongTermCare {get;set;} 
        public PrimaryCarePhysician PrimaryCarePhysician {get;set;} 
        public PrimaryCarePhysicianList PrimaryCarePhysicianList {get;set;} 
		public String OriginalEffectiveDate {get;set;} 
        public EnrollmentInfo EnrollmentInfo {get;set;} 
        public String OperationalMajorLineofBusiness {get;set;} 
        public String PolicySequenceNumber {get;set;} 
        public String HcfaEnrollmentSourceCode {get;set;} 
        public CoordinationOfBenefitList CoordinationOfBenefitList {get;set;}
		public OICoordinationOfBenefitList OICoordinationOfBenefitList {get;set;}
	}
    
    public class CoveredRoleList {
        public List<CoveredRole> CoveredRole {get;set;} 
	}
	
    public class CoveredRole {
        public String StartDate {get;set;} 
        public String EndDate {get;set;} 
        public String MemberMasterId {get;set;} 
        public String RelationshipToSubscriber {get;set;} 
		public String ProductRoleType {get;set;}
        public PlatformPointerList CoveredRolePlatformPointerList {get;set;} 
        public String Hipaa {get;set;} 
        public String CoverageKey {get;set;} 
	}
	
	public class ReasonList {
        public List<Reason> Reason {get;set;} 
	}
    
    public class Reason {
        public String Code {get;set;} 
        public String Description {get;set;} 
        public String Type_Z {get;set;} // in json: Type
	}
	
	public class Network {
        public String NetworkId {get;set;} 
        public String CASH1Network {get;set;} 
        public String CASD1Network {get;set;} 
        public String CASH2Network {get;set;} 
        public String CASD2Network {get;set;} 
        public NetworkDirectoryList NetworkDirectoryList {get;set;} 
    }
    
    public class NetworkDirectoryList {
        public List<NetworkDirectory> NetworkDirectory {get;set;} 
	}
    
    public class NetworkDirectory {
        public String Primary {get;set;} 
		public String DirectoryId {get;set;} 
        public String DisplayName {get;set;} 
        public String FormNumber {get;set;} 
    }
	
	public class PrimaryCarePhysician {
        public String EffectiveDate {get;set;} 
        public String EndDate {get;set;} 
        public String Name {get;set;} 
		public String Npi {get;set;}
        public String SourceProviderKey {get;set;} 
        public String SourceGrouperNumber {get;set;} 
        public String GrouperName {get;set;} 
    }
	
	public class PrimaryCarePhysicianList {
        public List<PrimaryCarePhysician> PrimaryCarePhysician {get;set;} 
	}
	
    public class EnrollmentInfo {
        public String DateofHire {get;set;} 
        public String TimelyIndicator {get;set;} 
        public PriorCarrier PriorCarrier {get;set;} 
	}
	
    public class PriorCarrier {
        public IndicatorList IndicatorList {get;set;} 
	}
	
	public class CoordinationOfBenefitList {
        public List<CoordinationOfBenefit> CoordinationOfBenefit {get;set;} 
	}
	
	public class CoordinationOfBenefit {
        public String CarrierName {get;set;} 
        public String EffectiveDate {get;set;} 
        public String EndDate {get;set;} 
        public String Primary {get;set;} 
        public String OILastVerifiedDate {get;set;} 
        public String LastUpdatedDate {get; set;}
	}
	
	public class OICoordinationOfBenefitList {
        public List<OICoordinationOfBenefit> OICoordinationOfBenefit {get;set;} 
	}
	
	public class OICoordinationOfBenefit {
        public String CarrierName {get;set;} 
        public String EffectiveDate {get;set;} 
        public String EndDate {get;set;} 
        public String Primary {get;set;} 
        public String OILastVerifiedDate {get;set;} 
        public String LastUpdatedDate {get; set;}
	}
    
    public class NonInsuranceCoverage {
        public String EndDate {get;set;} 
        public String StartDate {get;set;} 
        public CoveredRoleList CoveredRoleList {get;set;} 
        public PlatformPointerList GroupPlatformPointerList {get;set;} 
        public XReferenceList XReferenceList {get;set;} 
        public String OptOut {get;set;} 
        public String VitalityContractId {get;set;} 
        public ParentProductInfo ParentProductInfo {get;set;} 
	}
	
	public class Hipaa {
        public String CoverageType {get;set;} 
		public String RelationshipCode {get;set;} 
        public String RelationshipDesc {get;set;} 
        public String MajorLineOfBusinessCode {get;set;} 
        public String MajorLineOfBusinessDesc {get;set;} 
	}
    
	public class XReferenceList {
        public List<XReference> XReference {get;set;} 
	}
    
    public class XReference {
        public String KeyType {get;set;} 
        public String Name {get;set;} 
        public List<NameValueList> NameValueList {get;set;}
    }
    public class NameValueList {}
    
    public class ParentProductInfo {
        public String IsNonInsurance {get;set;} 
        public String PlatformCode {get;set;} 
        public String ProductID {get;set;} 
        public String ProductStartDate {get;set;} 
        public String CoverageId {get;set;} 
	}
    
}