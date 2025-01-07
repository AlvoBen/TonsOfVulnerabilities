/**************************************************************************** 
Apex class Name  : ProductBusinessEntityResponseV2_H_HUM
Version          : 1.0  
Created Date     : April 12,2018
Function         : response helper for Product Business Entity Service 1.3
Test class       : ProductBusinessEntityResponseV2_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Rakesh Patni             220187             04/12/2018                   Original Version
* Shailesh Mali								  02/14/2020				   REQ - 732113 - added PlanCommentSequenceNumber under plan comment
***************************************************************************************************/
public class ProductBusinessEntityResponseV2_H_HUM
{
    public GetProductResponse GetProductResponse;    
    public class GetProductResponse
    {
        public ProductList ProductList;
    }   
    public class ProductList
    {
        public list<Product> Product;   
    }   
    public class Product
    {
        public Categorization Categorization;
        public MetaData MetaData;
        public Definition Definition;
        public Detail Detail;
    }   
    public class Categorization
    {
        public Category Category;
        public Line Line;
        public Family Family;
    }    
    public class Category
    {
        public String Name = ''; 
        public String Code = '';
    }    
    public class Line
    {
        public String Name = '';
        public String Code = '';
    }    
    public class Family
    {
        public String Name = '';
        public String Code = '';
    }    
    public class Metadata
    {
        public String BrandName = '';
        public String DesignType = '';
        public String PlatformCode = '';
        public BusinessSegmentList BusinessSegmentList;
        public MarketSegmentList MarketSegmentList;
        public LocationList LocationList;
        public EffectiveDates EffectiveDates;
        public QuoteDates QuoteDates;
    }   
    public class BusinessSegmentList
    {
        public list<String> BusinessSegment;
    }   
    public class MarketSegmentList
    {
        public list<String> MarketSegment;
    }       
    public class LocationList
    {
        public list<Location> Location;
    }    
    public class Location
    {
        public String IsPartialCounty = '';
        public County County;
        public ZipList ZipList;
        public Country Country;
        public State State;
    }   
    public class County
    {
        public String Name = '';
        public String Code = '';
    }    
    public class ZipList
    {
        public list<Zip> Zip;
    }    
    public class Zip
    {
        public String Code = '';
        public String Extension = '';
    }    
    public class Country
    {
        public String Name = '';
        public String Code = '';
    }    
    public class State
    {
        public String Name = '';
        public String Code = '';
        public String StateNumber = '';
    }   
    public class EffectiveDates
    {
        public String BeginEffectiveDate = '';
        public String EndEffectiveDate = '';
    }   
    public class QuoteDates
    {
        public String QuoteBeginDate = ''; 
        public String QuoteEndDate = '';
    }   
    public class Definition
    {
        public String ProductMasterId = '';
        public String SourceSystemCode = '';
        public String SourceSystemProductId = '';
        public String Name = '';
        public String Description = '';
        public String GenerationCode = '';
        public String Version = '';
        public IdentifierList IdentifierList;
        public AlternateNameList AlternateNameList;
        public String Status = '';
    }   
    public class IdentifierList
    {
        public list<ProductIdentifier> ProductIdentifier;
    }   
    public class ProductIdentifier
    {
        public String ProductId = '';
        public String ProductIdType = '';
    }    
    public class AlternateNameList
    {
        public list<AlternateName> AlternateName;
    }   
    public class AlternateName
    {
        public String Name = '';
        public String NameType = '';
    }
    public class Detail
    {
        public PackageInfo PackageInfo;
        public GeneralBenefits GeneralBenefits;
        public RiderList RiderList;
        public SupplementalRiderList SupplementalRiderList;
        public BenefitCodesList BenefitCodesList;
        public RxDetail RxDetail;
        public ServiceList ServiceList;
        public ServiceTypeList ServiceTypeList;
        public ServiceCategoryList ServiceCategoryList;
        public PlaceOfServiceList PlaceOfServiceList;
        public ParcodeList ParcodeList;
        public AdaCodeList AdaCodeList;
        public CauseCodeList CauseCodeList;
    }  
    
    public class AdaCodeList
    {
        public list<AdaCode> AdaCode;
    }
    
    public class AdaCode
    {
    	public String Code = '';
        public String Name = '';
    }
    public class PackageInfo
    {
        public String PackageId = '';
        public String Name = '';
        public String AlternateName = ''; 
        public String Description = '';
        public String AlternateReimburseId = '';
        public String IsCarryOverCredit = '';
        public String IsRxSpeciality = '';
        public String IsPCA = '';
        public String IsWomensPreventive = '';
        public EffectiveDates EffectiveDates;
        public String AccumulationPeriod = '';
        public String CASLOBCode = '';
    }  
    public class GeneralBenefits
    {
        public String FinanceMarketNumber = '';
        public String IsBenchmarkPlan = '';
        public String IsCarveOut = '';
        public String IsHDHP = '';
        public String IsMinimumEssentialCoverage = '';
        public String IsMinimumValueStandard = '';
        public String IsPcpRequired = '';
        public String OnOrOffExchange = '';
        public String OptoutCode = '';
        public String PolicySeries = '';
        public String ProductType = '';
        public String FirstDollarAmount = ''; 
        public Deductible Deductible;
        public OutOfPocket OutOfPocket;
        public MaxOutOfPocket MaxOutOfPocket;
        public CoInsurance CoInsurance;
        public SuperMoop SuperMoop;
        public Limitation Limitation;
        public CopayOfficeVisit CopayOfficeVisit;
        public PlanCommentList PlanCommentList;
        public BenefitIndicatorList BenefitIndicatorList;
        public Copay Copay;
    }   
    public class Deductible
    {
        public String DeductibleBehaviorCode = '';
        public String IsRxIncluded = '';
        public Accumulates Accumulates;
        public DeductibleList DeductibleList;
        public String TypeCode = '';
    }    
    public class Accumulates
    {
        public InNetwork InNetwork;
        public OutOfnetwork OutOfnetwork;
    }    
    public class InNetwork
    {
        public list<String> PlanType;
    }    
    public class OutOfnetwork
    {
        public list<String> PlanType;
    }    
    public class DeductibleList
    {
        public list<Coverage> Coverage;
    }    
    public class Coverage
    {
        public String NetworkType = '';
        public BenefitAmountList BenefitAmountList;
        public String TierNumber = '';
        public HipaaServiceType HipaaServiceType;
    }   
    public class HipaaServiceType
    {
    	public String Code = '';
    	public String Description = '';
    }
    public class BenefitAmountList
    {
        public list<BenefitAmount> BenefitAmount;
    }   
    public class BenefitAmount
    {
        public String IsLimit = '';
        public String LimitName = '';
        public String CostShareReductionLevel = '';
        public UnitDetail UnitDetail;
        public String CoverageType = '';
        public String BenefitDescription = '';
        public String ProviderSubNetworkNumber = '';
    }   
    public class UnitDetail
    {
        public String Quantity = '';
        public String Notes = '';
        public String UnitType = '';
        public String TimePeriodQualifierType = '';
        public String QuantityType = '';
        public QuantityPeriod QuantityPeriod;
        public String ParCodeDescription ='';
    }    
    public class QuantityPeriod
    {
        public String QuantityPeriodBegin = '';
        public String QuantityPeriodEnd = '';
        public String Notes = '';
    }    
    public class OutOfPocket
    {
        public String TypeCode = '';
        public Accumulates Accumulates;
        public OutOfPocketList OutOfPocketList;
    }   
    public class OutOfPocketList
    {
        public list<Coverage> Coverage;
    }    
    public class MaxOutOfPocket
    {
        public MaxOutOfPocketList MaxOutOfPocketList;
    }   
    public class MaxOutOfPocketList
    {
        public list<Coverage> Coverage;
    }   
    public class CoInsurance
    {
        public Accumulates Accumulates;
        public CoInsuranceList CoInsuranceList;
    }    
    public class CoInsuranceList
    {
        public list<Coverage> Coverage;
    }    
    public class SuperMoop
    {
        public Accumulates Accumulates;
        public SuperMoopList SuperMoopList;
    }    
    public class SuperMoopList
    {
        public list<Coverage> Coverage;
    }  
    public class Limitation
    {
        public LimitationList LimitationList;
    }    
    public class LimitationList
    {
        public list<Coverage> Coverage; 
    }    
    public class CopayOfficeVisit
    {
        public CopayOfficeVisitList CopayOfficeVisitList;
    }    
    public class CopayOfficeVisitList
    {
        public list<Coverage> Coverage;
    }   
    public class PlanCommentList
    {
        public list<PlanComment> PlanComment;
    } 
    public class PlanComment
    {
        public String TypeCode = '';
        public String Comment = '';
		public String PlanCommentSequenceNumber = '';
    }
    public class BenefitIndicatorList
    {
        public list<BenefitIndicator> BenefitIndicator;
    }
    public class BenefitIndicator
    {
        public String BenefitIndicatorType = '';
        public String Value = '';
    }
    public class Copay
    {
    	public CopayList CopayList;
    } 
    public class CopayList
    {
    	public list<Coverage> Coverage;
    }  
    public class RiderList
    {
        public list<Rider> Rider;
    }   
    public class Rider
    {
        public String Id = '';
        public String Name = '';
        public String AlternateName = '';
        public String CertDefinition = '';
        public String DisplayName = '';
    }   
    public class SupplementalRiderList
    {
        public list<SupplementalRider> SupplementalRider;
    }   
    public class SupplementalRider
    {
        public String Id = '';
        public String TypeCode = '';
        public String VendorId = '';
        public String IsCarveOut = '';
        public String IsIntegratedBenefit = '';
    }   
    public class BenefitCodesList
    {
        public list<BenefitCodes> BenefitCodes;
    }   
    public class BenefitCodes
    {
        public String ADADescription = '';
        public String AppliesToDeductible = '';
        public String BenefitDispayKey = '';
        public String BenefitLevel = '';
        public String CostShareLevel = '';
        public String LISCopayLevel = '';
        public String ReimburseRuleName = '';
        public String ReimburseRuleSequence = '';
        public String BenefitDescription = '';
        public String ServiceTypeName = '';
        public String ServiceCategoryName = '';
        public String TierNumber = '';
        public String ProviderSubNetworkNumber = '';
        public BenefitCodeLimit BenefitCodeLimit;
        public BenefitCodeList  BenefitCodeList;
    }   
    public class BenefitCodeLimit
    {
        public String LimitId = '';
        public String LimitName = '';
        public String LimitSequence = '';
        public String LimitText = '';
    }    
    public class BenefitCodeList
    {
        public list<BenefitCode> BenefitCode;
    }    
    public class BenefitCode
    {
        public String CodeValue = '';
        public String CodeType = '';  
    }    
    public class RxDetail
    {
        public String PharmacyNetwork = '';
        public String RxPlanId = '';
        public RxServiceList RxServiceList;
    }    
    public class RxServiceList
    {
        public list<RxCoverage> RxCoverage;
    }    
    public class RxCoverage
    {
        public String Service = '';
        public RxBenefitDetailList BenefitDetailList;
        public String NetworkType = '';
        public String BenefitType = '';
    }
    
    public class RxBenefitDetailList
    {
        public list<RxBenefitDetail> RxBenefitDetail;
    }   
    public class RxBenefitDetail
    {
        public String TierNumber = '';
        public String BenefitSequence = '';
        public String Location = '';
        public BenefitAmountList BenefitAmountList;
    }   
    public class ServiceList
    {
        public list<Service> Service;
    }  
    public class Service
    {
        public String ServiceCategoryCode = '';
        public String ServiceTypeId = '';
		public String CauseCode = '';
        public BenefitList BenefitList;
        public PlanCommentList CommentList; 
    }
    public class BenefitList
    {
        public list<Benefit> Benefit;
    }    
    public class Benefit
    {
        public String IsCoveredService = '';
        public String NetworkType = '';
        public BenefitDetailList BenefitDetailList;
    }   
    public class BenefitDetailList
    {
        public list<BenefitDetail> BenefitDetail;
    }  
    public class BenefitDetail
    {
        public String TierNumber = '';
        public String BenefitSequence = '';
        public String IsCoveredBenefit = '';
        public PlaceOfServiceCodeList PlaceOfServiceCodeList;
        public ParCodeIDList ParCodeIDList;
        public String BenefitType = '';
        public BenefitAmountList BenefitAmountList;
        public ServiceAdaCodeList ServiceAdaCodeList;
    }  
    public class ServiceAdaCodeList
    {
    	public list<string> Code;
    	
    }  
    public class PlaceOfServiceCodeList
    {
        public list<string> Code;
    }  
   	public class PlaceOfServiceCode
   	{
   		public string Code = '';
   	}  	
   	 Public class ParCodeIDList
    {
        Public list<string> Code ;        
    }   
    public class ParCodeID
    {
        public string Code = '';
    }  
    public class ServiceTypeList
    {
        public list<ServiceType> ServiceType;
    }    
    public class ServiceType
    {
        public ParCodeIDList ParCodeIDList;
        public PlaceOfServiceCodeList PlaceOfServiceCodeList;
        public String ServiceCategoryCode;
        public String ServiceTypeId;
        public String ServiceName;
        public String CauseCode; //newly added for PBE 18.8
        public ServiceAdaCodeList ServiceAdaCodeList;
    }   
    public class ServiceCategoryList
    {
        public list<ServiceCategory> ServiceCategory;
    }   
    public class ServiceCategory
    {
        public String Code = '';
        public String Name = '';
        public CodeList CodeList; //newly added for PBE 18.8
    }   
    public class PlaceOfServiceList
    {
        public list<PlaceOfService> PlaceOfService;
    }  
    public class PlaceOfService
    {
        public String Code = '';
        public String Name = '';
    }      
    public class ParcodeList
    {
        public list<ParCode> ParCode;
    }   
    public class ParCode
    {
        public String Code = '';
        public String Name = '';
    }   
    //newly added for PBE 18.8
    public class CauseCodeList
    {
                public list<CauseCode> CauseCode;
    }  
    public class CauseCode
    {
                public String Code = '';
                public String Name = '';
    }  
    public class CodeList
    {
                Public List<string> Code;                                                       
    }
}