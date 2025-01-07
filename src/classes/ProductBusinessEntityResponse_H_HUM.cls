/**************************************************************************** 
Apex Class Name  : ProductBusinessEntityResponse_H_HUM 
Version          : 1.0  
Created Date     : February 01,2016
Function         : response helper for Product Business Entity Service
Test Class       : ProductBusinessEntityResponse_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             29829             02/01/2016                   Original Version
* Adil Mohammed             30736             03/30/2016                   Updated as per changes in REQ - 263787
* Gargey Sharma             30736             04/05/2016                   Updated as per changes in ICD.
* Adil Mohammed             31466             05/18/2016                   Updated placeOfServiceCodeList.
* Praveen Kumar Parimi      56654             01/11/2017                   Updated the Tier Number and Provider Network Number as part of PCP Tiering Project.
* Kritika Popat             112848            07/25/2017                   REQ - 320577 Blended coverage
* Prasanthi Kandula         112848            07/25/2017                   REQ - 320577 Blended coverage
* Rajesh Keswani            109089	          07/24/2017                   REQ - 299431 Making Place of Service drop down dynamic
* Avinash Choubey           109914 	          07/24/2017                   REQ - 300751 Making Par Code” drop down dynamic(for CAS Members) and Sorted
* Rajesh Keswani            118189            02/08/2017                   REQ - 305662  PCU- RX Carved Out Information
* Kritika Popat             121425            08/21/2017                   REQ - 326924 MF2 changes to accommodate MF8 service work (Defect 305971.0001)
* Prasanthi Kandula         121425            08/21/2017                   REQ - 326924 MF2 changes to accommodate MF8 service work (Defect 305971.0001)

***************************************************************************************************/
Public Class ProductBusinessEntityResponse_H_HUM
{
    Public GetProductResponse GetProductResponse;
    
    Public Class GetProductResponse
    {
        Public ProductList ProductList;
    }
    
    Public Class ProductList
    {
        Public list<Product> Product;   
    }
    
    Public Class Product
    {
        Public Categorization Categorization;
        Public MetaData MetaData;
        Public Definition Definition;
        Public Detail Detail;
    }
    
    Public Class Categorization
    {
        Public Category Category;
        Public Line Line;
        Public Family Family;
    }
    
    Public Class Category
    {
        Public String Name = ''; 
        Public String Code = '';
    }
    
    Public Class Line
    {
        Public String Name = '';
        Public String Code = '';
    }
    
    Public Class Family
    {
        Public String Name = '';
        Public String Code = '';
    }
    
    Public Class Metadata
    {
        Public String BrandName = '';
        Public String DesignType = '';
        Public String PlatformCode = '';
        Public BusinessSegmentList BusinessSegmentList;
        Public MarketSegmentList MarketSegmentList;
        Public LocationList LocationList;
        Public EffectiveDates EffectiveDates;
        Public QuoteDates QuoteDates;
    }
    
    Public Class BusinessSegmentList
    {
        Public list<String> BusinessSegment;
    }
    
    Public Class MarketSegmentList
    {
        Public list<String> MarketSegment;
    } 
       
    Public Class LocationList
    {
        Public list<Location> Location;
    }
    
    Public Class Location
    {
        Public String IsPartialCounty = '';
        Public County County;
        Public ZipList ZipList;
        Public Country Country;
        Public State State;
    }
    
    Public Class ZipList
    {
        Public list<Zip> Zip;
    }
    
    Public Class Zip
    {
        Public String Code = '';
        Public String Extension = '';
    }
    
    Public Class Country
    {
        Public String Name = '';
        Public String Code = '';
    }
    
    Public Class State
    {
        Public String Name = '';
        Public String Code = '';
        Public String StateNumber = '';
    }
    
    Public Class County
    {
        Public String Name = '';
        Public String Code = '';
    }
    
    Public Class QuoteDates
    {
        Public String QuoteBeginDate = ''; 
        Public String QuoteEndDate = '';
    }
    
    Public Class Definition
    {
        Public String ProductMasterId = '';
        Public String SourceSystemCode = '';
        Public String SourceSystemProductId = '';
        Public String Name = '';
        Public String Description = '';
        Public String GenerationCode = '';
        Public String Version = '';
        Public IdentifierList IdentifierList;
        Public AlternateNameList AlternateNameList;
        Public String Status = '';
    }
    
    Public Class IdentifierList
    {
        Public list<ProductIdentifier> ProductIdentifier;
    }
    
    Public Class ProductIdentifier
    {
        Public String ProductId = '';
        Public String ProductIdType = '';
    }
    
    Public Class AlternateNameList
    {
        Public list<AlternateName> AlternateName;
    }
    
    Public Class AlternateName
    {
        Public String Name = '';
        Public String NameType = '';
    }
    
    /*For now, renaming InsuranceProductDetail to Detail, as service Is still in development phase And Is returning details only for InsuranceProductDetail.
    Public Class Detail
    {
        Public NIProductDetail NIProductDetail;
        Public InsuranceProductDetail InsuranceProductDetail;
    }
    
    Public Class NIProductDetail
    {
        Public String OracleFinanceProductId = '';
        Public String VendorPlanId = '';
        Public list<LegalEntity> LegalEntityList;
    }
    
    Public Class LegalEntity
    {
        Public String Id = '';
        Public String Name = '';
    }
    
    Public Class InsuranceProductDetail*/
    Public Class Detail
    {
        Public PackageInfo PackageInfo;
        Public GeneralBenefits GeneralBenefits;
        Public RiderList RiderList;
        Public SupplementalRiderList SupplementalRiderList;
        Public ServiceAreaSubNetworkList ServiceAreaSubNetworkList;
        Public BenefitCodesList BenefitCodesList;
        Public RxDetail RxDetail;
        Public ServiceList ServiceList;
        Public ServiceTypeList ServiceTypeList;
        Public ServiceCategoryList ServiceCategoryList;
        Public PlaceOfServiceList PlaceOfServiceList;
        public ParcodeList ParcodeList;
    }
    
    Public Class ServiceList
    {
        Public list<Service> Service;
    }
    Public Class ParcodeList
    {
        Public list<ParCode> ParCode;
    }
     
    Public Class ServiceCategoryList
    {
        Public list<ServiceCategory> ServiceCategory;
    }
    
    Public Class ServiceTypeList
    {
        Public list<ServiceType> ServiceType;
    }
    Public Class PlaceOfServiceList
    {
        Public list<PlaceOfService> PlaceOfService;
    }
    
    Public Class BenefitCodesList
    {
        Public list<BenefitCodes> BenefitCodes;
    }
    
    Public Class ServiceAreaSubNetworkList
    {
        Public list<ServiceAreaSubNetwork> ServiceAreaSubNetwork;
    }
    
    Public Class RiderList
    {
        Public list<Rider> Rider;
    }
    
     Public Class SupplementalRiderList
    {
        Public list<SupplementalRider> SupplementalRider;
    }
    
    Public Class ServiceCategory
    {
        Public String Code = '';
        Public String Name = '';
    }
    
    Public Class ParCode
    {
        Public String Code = '';
        Public String Name = '';
    }
    
    Public Class ServiceType
    {
        public ParCodeIDList ParCodeIDList;
        public PlaceOfServiceCodeList PlaceOfServiceCodeList;
        Public String ServiceCategoryCode;
        Public String ServiceTypeId;
        Public String ServiceName;
    }
    Public Class ParCodeID
    {
        Public String Code = '';
        
    }
        
    Public class ParCodeIDList
    {
        Public list<string> Code ;
        
    }
    
    Public Class PlaceofServiceCode
    {
        Public String Code = '';
    }
    
    
    Public Class PlaceOfService
    {
        Public String Code = '';
        Public String Name = '';
    }
    
    Public Class PackageInfo
    {
        Public String PackageId = '';
        Public String Name = '';
        Public String AlternateName = ''; 
        Public String Description = '';
        Public String AlternateReimburseId = '';
        Public String IsCarryOverCredit = '';
        Public String IsRxSpeciality = '';
        Public String IsPCA = '';
        Public String IsWomensPreventive = '';
        Public EffectiveDates EffectiveDates;
        Public String AccumulationPeriod = '';
    }
    
    Public Class EffectiveDates
    {
        Public String BeginEffectiveDate = '';
        Public String EndEffectiveDate = '';
    }
    
    Public Class GeneralBenefits
    {
        Public String FinanceMarketNumber = '';
        Public String IsBenchmarkPlan = '';
        Public String IsCarveOut = '';
        Public String IsHDHP = '';
        Public String IsMinimumEssentialCoverage = '';
        Public String IsMinimumValueStandard = '';
        Public String IsPcpRequired = '';
        Public String OnOrOffExchange = '';
        Public String OptoutCode = '';
        Public String PolicySeries = '';
        Public String ProductType = '';
        Public String FirstDollarAmount = ''; 
        Public Deductible Deductible;
        Public OutOfPocket OutOfPocket;
        Public MaxOutOfPocket MaxOutOfPocket;
        Public CoInsurance CoInsurance;
        Public SuperMoop SuperMoop;
        Public Limitation Limitation;
        Public CopayOfficeVisit CopayOfficeVisit;
        Public PlanCommentList PlanCommentList;
        Public BenefitIndicatorList BenefitIndicatorList;
    }
    
    Public Class PlanCommentList
    {
        Public list<PlanComment> PlanComment;
    }
    
    Public Class BenefitIndicatorList
    {
        Public list<BenefitIndicator> BenefitIndicator;
    }
    
    Public Class Deductible
    {
        Public String DeductibleBehaviorCode = '';
        Public String IsRxIncluded = '';
        Public Accumulates Accumulates;
        Public DeductibleList DeductibleList;
        Public String TypeCode = '';
    }
    
    Public Class Accumulates
    {
        Public InNetwork InNetwork;
        Public OutOfnetwork OutOfnetwork;
    }
    
    Public Class InNetwork
    {
        Public list<String> PlanType;
    }
    
    Public Class OutOfnetwork
    {
        Public list<String> PlanType;
    }
    
    Public Class DeductibleList
    {
        Public list<Coverage> Coverage;
    }
    
    Public Class Coverage
    {
        Public String NetworkType = '';
        Public BenefitAmountList BenefitAmountList;
        Public String TierNumber = '';
    }
    
    Public Class BenefitAmountList
    {
        Public list<BenefitAmount> BenefitAmount;
    }
    
    Public Class BenefitAmount
    {
        Public String IsLimit = '';
        Public String LimitName = '';
        Public String CostShareReductionLevel = '';
        Public UnitDetail UnitDetail;
        Public String CoverageType = '';
        Public String BenefitDescription = '';
        Public String ProviderSubNetworkNumber = '';
    }
    
    Public Class UnitDetail
    {
        Public String Quantity = '';
        Public String Notes = '';
        Public String UnitType = '';
        Public String TimePeriodQualifierType = '';
        Public String QuantityType = '';
        Public QuantityPeriod QuantityPeriod;
    }
    
    Public Class QuantityPeriod
    {
        Public String QuantityPeriodBegin = '';
        Public String QuantityPeriodEnd = '';
        Public String Notes = '';
    }
    
    Public Class OutOfPocket
    {
        Public String TypeCode = '';
        Public Accumulates Accumulates;
        Public OutOfPocketList OutOfPocketList;
    }
    
    Public Class OutOfPocketList
    {
        Public list<Coverage> Coverage;
    }
    
    Public Class MaxOutOfPocket
    {
        Public MaxOutOfPocketList MaxOutOfPocketList;
    }
    
    Public Class MaxOutOfPocketList
    {
        Public list<Coverage> Coverage;
    }
    
    Public Class CoInsurance
    {
        Public Accumulates Accumulates;
        Public CoInsuranceList CoInsuranceList;
    }
    
    Public Class CoInsuranceList
    {
        Public list<Coverage> Coverage;
    }
    
    Public Class SuperMoop
    {
        Public Accumulates Accumulates;
        Public SuperMoopList SuperMoopList;
    }
    
    Public Class SuperMoopList
    {
        Public list<Coverage> Coverage;
    }
    
    Public Class Limitation
    {
        Public LimitationList LimitationList;
    }
    
    Public Class LimitationList
    {
        Public list<Coverage> Coverage; 
    }
    
    Public Class CopayOfficeVisit
    {
        Public CopayOfficeVisitList CopayOfficeVisitList;
    }
    
    Public Class CopayOfficeVisitList
    {
        Public list<Coverage> Coverage;
    }
    
    Public Class PlanComment
    {
        Public String TypeCode = '';
        Public String Comment = '';
    }
    
    Public Class BenefitIndicator
    {
        Public String BenefitIndicatorType = '';
        Public String Value = '';
    }
    
    Public Class Rider
    {
        Public String Id = '';
        Public String Name = '';
        Public String AlternateName = '';
        Public String CertDefinition = '';
        Public String DisplayName = '';
    }
    
     Public Class SupplementalRider
    {
        Public String Id = '';
        Public String TypeCode = '';
        Public String VendorId = '';
        Public String IsCarveOut = '';
        Public String IsIntegratedBenefit = '';
    }
    
    Public Class ServiceAreaSubNetwork
    {
        Public String SubNetwork = '';
        Public ServiceAreaList ServiceAreaList;
    }
    
    Public Class ServiceAreaList
    {
        Public list<ServiceArea> ServiceArea;
    }
    
    Public Class ServiceArea
    {
        Public String MarketLedger = '';
        Public Location ServiceArea;
    }
    
    Public Class BenefitCodeList
    {
        Public list<BenefitCode> BenefitCode;
    }
    
    Public Class BenefitCodes
    {
        Public String ADADescription = '';
        Public String AppliesToDeductible = '';
        Public String BenefitDispayKey = '';
        Public String BenefitLevel = '';
        Public String CostShareLevel = '';
        Public String LISCopayLevel = '';
        Public String ReimburseRuleName = '';
        Public String ReimburseRuleSequence = '';
        Public String BenefitDescription = '';
        Public String ServiceTypeName = '';
        Public String ServiceCategoryName = '';
        Public String ProviderSubNetworkNumber = '';
        Public BenefitCodeLimit BenefitCodeLimit;
        Public BenefitCodeList  BenefitCodeList;
    }
    
    Public Class BenefitCodeLimit
    {
        Public String LimitId = '';
        Public String LimitName = '';
        Public String LimitSequence = '';
        Public String LimitText = '';
    }
    
    Public Class BenefitCode
    {
        Public String CodeValue = '';
        Public String CodeType = '';  
    }
    
    Public Class RxDetail
    {
        Public String PharmacyNetwork = '';
        Public String RxPlanId = '';
        Public RxServiceList RxServiceList;
    }
    
    Public Class RxServiceList
    {
        Public list<RxCoverage> RxCoverage;
    }
    
    Public Class RxCoverage
    {
        Public String Service = '';
        Public RxBenefitDetailList BenefitDetailList;
        Public String NetworkType = '';
        Public String BenefitType = '';
    }
    
    Public Class RxBenefitDetailList
    {
        Public list<RxBenefitDetail> RxBenefitDetail;
    }
    
    Public Class RxBenefitDetail
    {
        Public String TierNumber = '';
        Public String BenefitSequence = '';
        Public String Location = '';
        Public BenefitAmountList BenefitAmountList;
    }
    
    Public Class Service
    {
        Public String ServiceCategoryId = '';
        Public String ServiceName = '';
        Public BenefitList BenefitList;
        Public PlanCommentList CommentList; 
    }
    
    Public Class BenefitList
    {
        Public list<Benefit> Benefit;
    }
    
    Public Class Benefit
    {
        Public String IsCoveredService = '';
        Public String NetworkType = '';
        Public BenefitDetailList BenefitDetailList;
    }
    
    Public Class BenefitDetailList
    {
        Public list<BenefitDetail> BenefitDetail;
    }
    
    Public Class BenefitDetail
    {
        Public String TierNumber = '';
        Public String BenefitSequence = '';
        Public String IsCoveredBenefit = '';
        Public PlaceOfServiceCodeList PlaceOfServiceCodeList;
        public ParCodeIDList ParCodeIDList;
        Public String BenefitType = '';
        Public BenefitAmountList BenefitAmountList;
    }
    
    Public Class PlaceOfServiceCodeList
    {
        Public list<String> Code;
    }
    
 }