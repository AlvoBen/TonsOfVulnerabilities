/**
   @Author Gargey Sharma
   @name MBEPlusResponse_DTO_HUM
   @CreateDate 08/16/2017
   @Description Request helper class for MBE Plus service
   @Version 1.0
   @reference REQ - 326261
   @Test Class  
Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Gargey Sharma            142517                     08/16/2017                  Original Version
* Lakshmi Madduri									  10/08/2018				  Added Practice Location in PrimaryCarePhysician
* Suraj Patil                                         05/04/2020                  REQ - 737881 - MF 4 - Add Dental Paid Thru Date for HPS Members
* Sai Kumar Boga                                      11/10/2020                 REQ - 1567028  CALL_MF03_FR02: KY MMC 2021(Added per CR17)_CRM service must create a NATIVE AMERICAN indicator
****************************************************************************************************************************/
public class MBEPlusResponse_DTO_HUM
{
    public GetMemberResponse GetMemberResponse;
    
    public class GetMemberResponse 
    {
        public Header Header;
        public Body Body;
    }
    
    public class Header 
    {
        public ResponseHeader ResponseHeader;
    }
    
    public class ResponseHeader 
    {
        public String Environment='';  
        public String Version='';  
        public Server Server;
        public Metrics Metrics;
        public ResponseMetaData ResponseMetaData;
    }
    
    public class Server 
    {
        public String Name='';
    }
    
    public class Metrics 
    {
        public String Duration=''; 
        public String RequestTimeStamp=''; 
        public String ResponseTimeStamp='';
    }
    
    public class ResponseMetaData 
    {
        public String Status='';
        public String ClientSessionID='';
        public String SessionID='';
        public DataAuthorizationConstraintList DataAuthorizationConstraintList;
    }
    
    public class DataAuthorizationConstraintList 
    {
        public list<DataAuthorizationConstraint> DataAuthorizationConstraint;
    }
    
    public class DataAuthorizationConstraint 
    {
        public String ConstraintType='';
        public String Tenant='';
    }
    
    public class Body 
    {
        public Member Member;
        public MessageList MessageList;
    }
    
    public class Member 
    {
        public String DateOfBirth='';
        public String FirstName='';
        public String Gender='';
        public String LastName='';
        public String MiddleInitial='';
        Public String Ssn='';
        Public Contact Contact;
        Public RaceList RaceList;
        Public MedicareIdentifierList MedicareIdentifierList;
        public String MasterId='';
        public PlatformPointerList PlatformPointerList;
        public SoldProductList SoldProductList;
        public IndicatorList IndicatorList;
    }
    
    public class Contact 
    {
        public ContactProfile ContactProfile;
    }
    
    public class ContactProfile 
    {
        public EmailList EmailList;
        public PhoneList PhoneList;
        public AddressList AddressList;
    }
    
    public class EmailList 
    {
        public list<PersonEmail> PersonEmail;
    }
    
    public class PersonEmail 
    {
        public String Address='';  
        public String Type='';
    }
    
    public class PhoneList 
    {
        public list<PersonPhone> PersonPhone;
    }
    
    public class PersonPhone 
    {
        public String PhoneNumber='';
        public String Type=''; 
    }
    
    public class AddressList 
    {
        public list<PersonAddress> PersonAddress;
    }
    
    public class PersonAddress 
    {
        public String AddressLine1=''; 
        public String City=''; 
        public String StateCode='';
        public String County='';
        public String ZipCode='';
        public String ZipPlus='';
        Public String Type=''; 
    }
    Public Class RaceList
    {
        Public list<Race> Race;
    }
    
    Public Class Race
    {
        Public String Code='';
        Public String Description='';
    } 
    Public class MedicareIdentifierList 
    {
        public list<MedicareIdentifier> MedicareIdentifier;
    }
    
    public class MedicareIdentifier 
    {
        public String MedicareId='';   
    }
    
    public class PlatformPointerList 
    {
        public list<PlatformProxyKey> PlatformProxyKey;
    }
    
    public class PlatformProxyKey 
    {
        public String Application='';
        public String KeyType='';
        public String KeyValue=''; 
        public String Source='';
    }
    
    public class SoldProductList 
    {
        public list<SoldProduct> SoldProduct;
    }
    
    public class SoldProduct 
    {
        public InsuranceProduct InsuranceProduct;
    }
    
    public class InsuranceProduct 
    {
        public String Description='';  
        public String OfferId='';
        public String ProductStartDate='';
        public String ProductEndDate='';
        public String ProductId='';
        public String Source='';
        public String ProductLineCode='';
        public SoldProductDetailList SoldProductDetailList;
        public String ProductOptionKey=''; 
    }
    
    public class SoldProductDetailList 
    {
        public list<SoldProductDetail> SoldProductDetail;
    }
    
    public class SoldProductDetail 
    {
        public NonInsuranceCoverage NonInsuranceCoverage;
        public InsuranceCoverage InsuranceCoverage;
    }
    
    public class InsuranceCoverage 
	{
        public String EndDate {get;set;} 
        public String StartDate {get;set;} 
        public CoveredRoleList CoveredRoleList {get;set;} 
        public String PolicyType {get;set;} 
        public String CoverageType {get;set;} 
        public CoveredRolePlatformPointerList GroupPlatformPointerList {get;set;} 
        public String OperationalMajorLineofBusiness {get;set;}
        public PrimaryCarePhysician PrimaryCarePhysician; 
        public PrimaryCarePhysicianList PrimaryCarePhysicianList;
        public CostShareReduction  CostShareReduction ;
        public CostShareReductionList CostShareReductionList;
        public Network Network;
        public String OriginalEffectiveDate='';
        public EnrollmentInfo EnrollmentInfo;
        public WaitingPeriodList WaitingPeriodList;
        public OutOfAreaList OutOfAreaList;
        public IndicatorList IndicatorList;
		public AncillaryList AncillaryList;
    }
	
    public class AncillaryList
    {
        public list<Ancillary> Ancillary;
    }

    public class Ancillary
    {
        public string Name{get;set;}
        public string Value{get;set;}
    }
	
    public class OutOfAreaList 
	{   
        public List<OutOfArea> OutOfArea;
    }
    
    public class OutOfArea 
	{   
       public String StartDate;
       public String EndDate;
    }
        
    public class EnrollmentInfo 
	{   
		public String EnrollmentType='';
		public String DateofHire='';
		public string TimelyIndicator='';
		public string OpenEnrollmentBeginDate='';
		public string OpenEnrollmentEndDate='';
		public string OpenEnrollmentStatus='';
		public string RedeterminationDate='';
		public PriorCarrier PriorCarrier ;     
    }
    
    public class WaitingPeriodList 
	{    
		public List<WaitingPeriod> WaitingPeriod;   
    }
    
    public class WaitingPeriod 
	{   
		public String BeginDate;
		public String EndDate;
		public String ServiceCategoryId='';
		public String TypeCode='';
		public String ServiceCategoryDescription='';
		public IndicatorList WaitingPeriodIndicatorList;    
    }
    
    public class PriorCarrier  
	{   
		public String CarrierName='';
		public String EffectiveDate;
		public String EndDate;
		public IndicatorList IndicatorList;     
    }
    public class PrimaryCarePhysicianList 
	{
        public List<PrimaryCarePhysician> PrimaryCarePhysician;
    }

    public class CostShareReduction 
	{
        public String Level='';
        public String Amount='';
        public String EndDate='';
        public String ModifiedDate='';
        public String StartDate='';
    }

    public class CostShareReductionList 
	{
        public List<CostShareReduction> CostShareReduction;
    }

    public class PrimaryCarePhysician 
	{
        public String EffectiveDate='';
        public String EndDate='';
        public String Name='';
        public String SourceProviderKey='';
        public string GrouperName='';
        public string SourceGrouperNumber='';
        public string Npi='';
        public String PracticeLocationNumber='';
    }
    
    public class Network 
	{
        Public String NetworkId='';
        Public String CASH1Network ='';
        Public String CASH2Network ='';
        Public String CASD1Network ='';
        Public String CASD2Network =''; 
        Public NetworkDirectoryList NetworkDirectoryList;
    }

    public class NetworkDirectoryList 
	{
        public List<NetworkDirectory> NetworkDirectory;
    }
    
    public class NetworkDirectory 
	{
        public String DirectoryId='';
        public String DisplayName='';
        public String FormNumber='';
        public String Primary='';
    }

    public class NonInsuranceCoverage 
    {
        public String EndDate='';  
        public String StartDate='';    
        public CoveredRoleList CoveredRoleList;
        public GroupPlatformPointerList GroupPlatformPointerList;
        public XReferenceList XReferenceList;
        public String OptOut='';
        public String VitalityContractId='';
    }
    
    public class CoveredRoleList 
    {
        public list<CoveredRole> CoveredRole;
    }
    
    public class CoveredRole 
    {
        public String StartDate='';
        public String EndDate='';
        public String MemberMasterId='';
        public String RelationshipToSubscriber=''; 
        public String ProductRoleType='';
        public CoveredRolePlatformPointerList CoveredRolePlatformPointerList;
    }
    
    public class CoveredRolePlatformPointerList 
    {
        public list<PlatformProxyKey> PlatformProxyKey;
    }
    
    public class GroupPlatformPointerList 
    {
        public list<PlatformProxyKey> PlatformProxyKey;
    }
    
    public class XReferenceList 
    {
        public list<XReference> XReference;
    }
    
    public class XReference 
    {
        public String KeyType;
        public String Name='';
        public list<NameValueList> NameValueList;
    }
    
    public class NameValueList 
    {
    }
    
    public class IndicatorList 
    {
        public list<Indicator> Indicator;
    }
    
    public class Indicator 
    {
        public String Name=''; 
        public String Value='';
    }
    
    public class MessageList 
    {
        public list<Message> Message;
    }
    
    public class Message 
    {
        public String Type=''; 
        public String Code=''; 
        public String Source='';   
        public String Description='';  
    }
}