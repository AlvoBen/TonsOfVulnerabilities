/************************************************************************************************
Apex Class Name  : HUMVerifyFamilyDatacontract
Version          : 1.0 
Created Date     : July 16  2014
Function         : System Generated Proxy class for PersonServiceFlattenedWSDL_External_DP_Dev WSDL
Test Class       : HUMVerifyFamilyDatacontractTest 
Log:
*     Developer Name       Code Review          Date                 Description
* -------------------------------------------------------------------------------------------------             
*     Kritika Popat         17076               23/05/2014            Original Version
*     Saurabh Kaple         162071              01/10/2018		    REQ - 333069 - Genkey conversion- updates to person service request
***************************************************************************************************/

public class HUMVerifyFamilyDatacontract {
    public class LongTermCareDTO {
        public String EffectiveDate;
        public String EndDate;
        public String ProdPlanID;
        public String ProgramCode;
        public String StateCode;
        private String[] EffectiveDate_type_info = new String[]{'EffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] EndDate_type_info = new String[]{'EndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ProdPlanID_type_info = new String[]{'ProdPlanID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ProgramCode_type_info = new String[]{'ProgramCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] StateCode_type_info = new String[]{'StateCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'EffectiveDate','EndDate','ProdPlanID','ProgramCode','StateCode'};
    }
    public class ArrayOfMemberCoverageDTO {
        public HUMVerifyFamilyDatacontract.MemberCoverageDTO[] MemberCoverageDTO;
        private String[] MemberCoverageDTO_type_info = new String[]{'MemberCoverageDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'MemberCoverageDTO'};
    }
    public class ArrayOfClinicalCaseDTO {
        public HUMVerifyFamilyDatacontract.ClinicalCaseDTO[] ClinicalCaseDTO;
        private String[] ClinicalCaseDTO_type_info = new String[]{'ClinicalCaseDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'ClinicalCaseDTO'};
    }
    public class PlanDTO {
        public String Description;
        public String EffectiveDate;
        public String NonParDeductible;
        public String NonParMOOP;
        public String NonParSuperMOOP;
        public String ParDeductible;
        public String ParMOOP;
        public String ParSuperMOOP;
        public String PolicyType;
        public String ProductLineCode;
        public String ProductTypeCode;
        public HUMVerifyFamilyDatacontract.ArrayOfMemberCoveragePropertyDTO Properties;
        public String SrcCustDivisionID;
        public String SrcProdKey;
        public String StateOfIssue;
        private String[] Description_type_info = new String[]{'Description','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] EffectiveDate_type_info = new String[]{'EffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] NonParDeductible_type_info = new String[]{'NonParDeductible','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] NonParMOOP_type_info = new String[]{'NonParMOOP','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] NonParSuperMOOP_type_info = new String[]{'NonParSuperMOOP','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ParDeductible_type_info = new String[]{'ParDeductible','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ParMOOP_type_info = new String[]{'ParMOOP','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ParSuperMOOP_type_info = new String[]{'ParSuperMOOP','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PolicyType_type_info = new String[]{'PolicyType','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ProductLineCode_type_info = new String[]{'ProductLineCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ProductTypeCode_type_info = new String[]{'ProductTypeCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Properties_type_info = new String[]{'Properties','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SrcCustDivisionID_type_info = new String[]{'SrcCustDivisionID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SrcProdKey_type_info = new String[]{'SrcProdKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] StateOfIssue_type_info = new String[]{'StateOfIssue','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'Description','EffectiveDate','NonParDeductible','NonParMOOP','NonParSuperMOOP','ParDeductible','ParMOOP','ParSuperMOOP','PolicyType','ProductLineCode','ProductTypeCode','Properties','SrcCustDivisionID','SrcProdKey','StateOfIssue'};
    }
    public class ProviderDTO {
        public String PCPAddress;
        public String PCPCity;
        public String PCPName;
        public String PCPState;
        public String PCPTelephone;
        public String PCPZip;
        public String SrcGrouperKey;
        private String[] PCPAddress_type_info = new String[]{'PCPAddress','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PCPCity_type_info = new String[]{'PCPCity','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PCPName_type_info = new String[]{'PCPName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PCPState_type_info = new String[]{'PCPState','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PCPTelephone_type_info = new String[]{'PCPTelephone','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PCPZip_type_info = new String[]{'PCPZip','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SrcGrouperKey_type_info = new String[]{'SrcGrouperKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'PCPAddress','PCPCity','PCPName','PCPState','PCPTelephone','PCPZip','SrcGrouperKey'};
    }
    public class SupplementalCoverageDTO {
        public String OSBCategoryDescription;
        public String OSBEffectiveDate;
        public String OSBEndDate;
        public String OSBItemCode;
        public String OSBItemTypeCode;
        public String OSBPackageKey;
        public String OSBPackageName;
        public HUMVerifyFamilyDatacontract.SupplementCoverageKeysDTO SupplementCoverageKeys;
        private String[] OSBCategoryDescription_type_info = new String[]{'OSBCategoryDescription','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] OSBEffectiveDate_type_info = new String[]{'OSBEffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] OSBEndDate_type_info = new String[]{'OSBEndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] OSBItemCode_type_info = new String[]{'OSBItemCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] OSBItemTypeCode_type_info = new String[]{'OSBItemTypeCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] OSBPackageKey_type_info = new String[]{'OSBPackageKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] OSBPackageName_type_info = new String[]{'OSBPackageName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SupplementCoverageKeys_type_info = new String[]{'SupplementCoverageKeys','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'OSBCategoryDescription','OSBEffectiveDate','OSBEndDate','OSBItemCode','OSBItemTypeCode','OSBPackageKey','OSBPackageName','SupplementCoverageKeys'};
    }
    public class MemberCoveragePropertyDTO {
        public String Key;
        public String Value;
        private String[] Key_type_info = new String[]{'Key','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Value_type_info = new String[]{'Value','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'Key','Value'};
    }
    public class ArrayOfLongTermCareDTO {
        public HUMVerifyFamilyDatacontract.LongTermCareDTO[] LongTermCareDTO;
        private String[] LongTermCareDTO_type_info = new String[]{'LongTermCareDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'LongTermCareDTO'};
    }
    public class MemberCoverageDTO {
        public HUMVerifyFamilyDatacontract.ArrayOfAPTCDTO APTC;
        public String ASOIndicator;
        public HUMVerifyFamilyDatacontract.AlternateCoverageIDList AlternateCoverageIDList;
        public String BillPlatformInd;
        public HUMVerifyFamilyDatacontract.ArrayOfCOBDTO COB;
        public String CSRIndicator;
        public String CategoryDescription;
        public String Category;
        public String ClientNumber;
        public String ContinuousCoverageEffectiveDate;
        public String ContinuousCoverageEndDate;
        public String CoverageIndicator;
        public String CoveragePeriod;
        public String CustCovSeqNbr;
        public String CustomerGenKey;
        public String DisplayGroupID;
        public String DisplayedPlatform;
        public String DualDemoIndicator;
        public String EHBTermDate;
        public String EnrollmentSrcCode;
        public String ExchangeMemberID;
        public String GroupName;
        public Boolean HasParent;
        public String HumanaRelationDescription;
        public String InternalCaseID;
        public String IsHumanaOneDental;
        public String IsHumanaOneMedical;
        public Boolean IsMedicarePolicy;
        public String KICClientID;
        public String LTCProvCode;
        public String LineOfBusiness;
        public String MTVBusinessLevel7;
        public String MarketDescription;
        public String MarketID;
        public String MemberCoverageEffectiveDate;
        public String MemberCoverageEndDate;
        public String MemberGenKey;
        public String MemberIDBase;
        public String MemberIDDependentCode;
        public HUMVerifyFamilyDatacontract.NetworkDTO Network;
        public String OSBPresent;
        public String PaidThruDate;
        public String ParentCoverageIdentifier;
        public String PlanId;
        public HUMVerifyFamilyDatacontract.PlanDTO Plan;
        public String PlatformCoverageIdentifier;
        public String ProdPlanID;
        public HUMVerifyFamilyDatacontract.ArrayOfMemberCoveragePropertyDTO Properties;
        public HUMVerifyFamilyDatacontract.ProviderDTO Provider;
        public String RoutingLOB;
        public String RxEligibleIndicator;
        public String SegmentIndicator;
        public String SellingMarketNumber;
        public String SourceCustCovKey;
        public String SourceCustKey;
        public HUMVerifyFamilyDatacontract.ArrayOfMemberCoverageSourceDTO SourceHeaderKeys;
        public String SrcCustCovKey;
        public String SrcPlatformCode;
        public String Status;
        public String SubscriberGenKey;
        public String SubscriberIDBase;
        public String SubscriberIDDependentCode;
        public String SubscriberInd;
        public HUMVerifyFamilyDatacontract.ArrayOfSupplementalCoverageDTO SupplementalCovgInformation;
        private String[] APTC_type_info = new String[]{'APTC','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ASOIndicator_type_info = new String[]{'ASOIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] AlternateCoverageIDList_type_info = new String[]{'AlternateCoverageIDList','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] BillPlatformInd_type_info = new String[]{'BillPlatformInd','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] COB_type_info = new String[]{'COB','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] CSRIndicator_type_info = new String[]{'CSRIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] CategoryDescription_type_info = new String[]{'CategoryDescription','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Category_type_info = new String[]{'Category','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ClientNumber_type_info = new String[]{'ClientNumber','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ContinuousCoverageEffectiveDate_type_info = new String[]{'ContinuousCoverageEffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ContinuousCoverageEndDate_type_info = new String[]{'ContinuousCoverageEndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] CoverageIndicator_type_info = new String[]{'CoverageIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] CoveragePeriod_type_info = new String[]{'CoveragePeriod','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] CustCovSeqNbr_type_info = new String[]{'CustCovSeqNbr','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] CustomerGenKey_type_info = new String[]{'CustomerGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] DisplayGroupID_type_info = new String[]{'DisplayGroupID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] DisplayedPlatform_type_info = new String[]{'DisplayedPlatform','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] DualDemoIndicator_type_info = new String[]{'DualDemoIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] EHBTermDate_type_info = new String[]{'EHBTermDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] EnrollmentSrcCode_type_info = new String[]{'EnrollmentSrcCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ExchangeMemberID_type_info = new String[]{'ExchangeMemberID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] GroupName_type_info = new String[]{'GroupName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] HasParent_type_info = new String[]{'HasParent','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','false'};
        private String[] HumanaRelationDescription_type_info = new String[]{'HumanaRelationDescription','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] InternalCaseID_type_info = new String[]{'InternalCaseID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] IsHumanaOneDental_type_info = new String[]{'IsHumanaOneDental','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] IsHumanaOneMedical_type_info = new String[]{'IsHumanaOneMedical','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] IsMedicarePolicy_type_info = new String[]{'IsMedicarePolicy','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','false'};
        private String[] KICClientID_type_info = new String[]{'KICClientID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] LTCProvCode_type_info = new String[]{'LTCProvCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] LineOfBusiness_type_info = new String[]{'LineOfBusiness','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MTVBusinessLevel7_type_info = new String[]{'MTVBusinessLevel7','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MarketDescription_type_info = new String[]{'MarketDescription','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MarketID_type_info = new String[]{'MarketID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberCoverageEffectiveDate_type_info = new String[]{'MemberCoverageEffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberCoverageEndDate_type_info = new String[]{'MemberCoverageEndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberGenKey_type_info = new String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberIDBase_type_info = new String[]{'MemberIDBase','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberIDDependentCode_type_info = new String[]{'MemberIDDependentCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Network_type_info = new String[]{'Network','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] OSBPresent_type_info = new String[]{'OSBPresent','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PaidThruDate_type_info = new String[]{'PaidThruDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ParentCoverageIdentifier_type_info = new String[]{'ParentCoverageIdentifier','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PlanId_type_info = new String[]{'PlanId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Plan_type_info = new String[]{'Plan','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PlatformCoverageIdentifier_type_info = new String[]{'PlatformCoverageIdentifier','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ProdPlanID_type_info = new String[]{'ProdPlanID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Properties_type_info = new String[]{'Properties','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Provider_type_info = new String[]{'Provider','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] RoutingLOB_type_info = new String[]{'RoutingLOB','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] RxEligibleIndicator_type_info = new String[]{'RxEligibleIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SegmentIndicator_type_info = new String[]{'SegmentIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SellingMarketNumber_type_info = new String[]{'SellingMarketNumber','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SourceCustCovKey_type_info = new String[]{'SourceCustCovKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SourceCustKey_type_info = new String[]{'SourceCustKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SourceHeaderKeys_type_info = new String[]{'SourceHeaderKeys','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SrcCustCovKey_type_info = new String[]{'SrcCustCovKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SrcPlatformCode_type_info = new String[]{'SrcPlatformCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Status_type_info = new String[]{'Status','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SubscriberGenKey_type_info = new String[]{'SubscriberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SubscriberIDBase_type_info = new String[]{'SubscriberIDBase','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SubscriberIDDependentCode_type_info = new String[]{'SubscriberIDDependentCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SubscriberInd_type_info = new String[]{'SubscriberInd','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SupplementalCovgInformation_type_info = new String[]{'SupplementalCovgInformation','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'APTC','ASOIndicator','AlternateCoverageIDList','BillPlatformInd','COB','CSRIndicator','CategoryDescription','Category','ClientNumber','ContinuousCoverageEffectiveDate','ContinuousCoverageEndDate','CoverageIndicator','CoveragePeriod','CustCovSeqNbr','CustomerGenKey','DisplayGroupID','DisplayedPlatform','DualDemoIndicator','EHBTermDate','EnrollmentSrcCode','ExchangeMemberID','GroupName','HasParent','HumanaRelationDescription','InternalCaseID','IsHumanaOneDental','IsHumanaOneMedical','IsMedicarePolicy','KICClientID','LTCProvCode','LineOfBusiness','MTVBusinessLevel7','MarketDescription','MarketID','MemberCoverageEffectiveDate','MemberCoverageEndDate','MemberGenKey','MemberIDBase','MemberIDDependentCode','Network','OSBPresent','PaidThruDate','ParentCoverageIdentifier','PlanId','Plan','PlatformCoverageIdentifier','ProdPlanID','Properties','Provider','RoutingLOB','RxEligibleIndicator','SegmentIndicator','SellingMarketNumber','SourceCustCovKey','SourceCustKey','SourceHeaderKeys','SrcCustCovKey','SrcPlatformCode','Status','SubscriberGenKey','SubscriberIDBase','SubscriberIDDependentCode','SubscriberInd','SupplementalCovgInformation'};
    }
    public class PartnerDetailDTO {
        public String EndDate;
        public String PartnerName;
        public String PartnerType;
        public String StartDate;
        private String[] EndDate_type_info = new String[]{'EndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PartnerName_type_info = new String[]{'PartnerName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PartnerType_type_info = new String[]{'PartnerType','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] StartDate_type_info = new String[]{'StartDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'EndDate','PartnerName','PartnerType','StartDate'};
    }
    public class ArrayOfMemberCoverageSourceDTO {
        public HUMVerifyFamilyDatacontract.MemberCoverageSourceDTO[] MemberCoverageSourceDTO;
        private String[] MemberCoverageSourceDTO_type_info = new String[]{'MemberCoverageSourceDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'MemberCoverageSourceDTO'};
    }
    public class MemberCoverageSourceDTO {
        public String Key;
        public String SourceCode;
        public String Source;
        private String[] Key_type_info = new String[]{'Key','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SourceCode_type_info = new String[]{'SourceCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Source_type_info = new String[]{'Source','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'Key','SourceCode','Source'};
    }
    public class ArrayOfMemberCoveragePropertyDTO {
        public HUMVerifyFamilyDatacontract.MemberCoveragePropertyDTO[] MemberCoveragePropertyDTO;
        private String[] MemberCoveragePropertyDTO_type_info = new String[]{'MemberCoveragePropertyDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'MemberCoveragePropertyDTO'};
    }
    public class COBDTO {
        public String CarrierName;
        public String EffectiveDate;
        public String EndDate;
        public String Primary;
        private String[] CarrierName_type_info = new String[]{'CarrierName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] EffectiveDate_type_info = new String[]{'EffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] EndDate_type_info = new String[]{'EndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Primary_type_info = new String[]{'Primary','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'CarrierName','EffectiveDate','EndDate','Primary'};
    }
    public class ClinicalCaseDTO {
        public String CaseType;
        public HUMVerifyFamilyDatacontract.ArrayOfPartnerDetailDTO PartnerDetail;
        private String[] CaseType_type_info = new String[]{'CaseType','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PartnerDetail_type_info = new String[]{'PartnerDetail','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'CaseType','PartnerDetail'};
    }
    public class MemberPropertiesDTO {
        public String DemographicsChangedBy;
        public DateTime DemographicsChangedTimeStamp;
        public String DemographicsVerifiedBy;
        public DateTime DemographicsVerifiedTimestamp;
        public DateTime LastContactInquiredAbout;
        public DateTime LastContactInquiringFor;
        public String MedicareID;
        public String MemberGenKey;
        public String MemberName;
        public DateTime OIDenVerifiedTS;
        public DateTime OIMedVerifiedTS;
        public Boolean VIP;
        private String[] DemographicsChangedBy_type_info = new String[]{'DemographicsChangedBy','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] DemographicsChangedTimeStamp_type_info = new String[]{'DemographicsChangedTimeStamp','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] DemographicsVerifiedBy_type_info = new String[]{'DemographicsVerifiedBy','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] DemographicsVerifiedTimestamp_type_info = new String[]{'DemographicsVerifiedTimestamp','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] LastContactInquiredAbout_type_info = new String[]{'LastContactInquiredAbout','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] LastContactInquiringFor_type_info = new String[]{'LastContactInquiringFor','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MedicareID_type_info = new String[]{'MedicareID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberGenKey_type_info = new String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberName_type_info = new String[]{'MemberName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] OIDenVerifiedTS_type_info = new String[]{'OIDenVerifiedTS','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] OIMedVerifiedTS_type_info = new String[]{'OIMedVerifiedTS','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] VIP_type_info = new String[]{'VIP','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','false'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'DemographicsChangedBy','DemographicsChangedTimeStamp','DemographicsVerifiedBy','DemographicsVerifiedTimestamp','LastContactInquiredAbout','LastContactInquiringFor','MedicareID','MemberGenKey','MemberName','OIDenVerifiedTS','OIMedVerifiedTS','VIP'};
    }
    public class ArrayOfMemberDTO {
        public HUMVerifyFamilyDatacontract.MemberDTO[] MemberDTO;
        private String[] MemberDTO_type_info = new String[]{'MemberDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'MemberDTO'};
    }
    public class AddressDTO {
        public String City;
        public String County;
        public String Line1;
        public String Line2;
        public String State;
        public String ZipPlus;
        public String Zip;
        private String[] City_type_info = new String[]{'City','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] County_type_info = new String[]{'County','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Line1_type_info = new String[]{'Line1','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Line2_type_info = new String[]{'Line2','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] State_type_info = new String[]{'State','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ZipPlus_type_info = new String[]{'ZipPlus','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Zip_type_info = new String[]{'Zip','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'City','County','Line1','Line2','State','ZipPlus','Zip'};
    }
    public class NetworkDTO {
        public String NetworkID;
        private String[] NetworkID_type_info = new String[]{'NetworkID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'NetworkID'};
    }
    public class ArrayOfCOBDTO {
        public HUMVerifyFamilyDatacontract.COBDTO[] COBDTO;
        private String[] COBDTO_type_info = new String[]{'COBDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'COBDTO'};
    }
    public class MemberDTO {
        public String DateOfBirth;
        public String FirstName;
        public String LastName;
        public String MemberGenKey;
        public String MemberID;
        public String MiddleInitial;
        public HUMVerifyFamilyDatacontract.AddressDTO Address;
        public Integer Age;
        public String AlternateID;
        public String ClientId;
        public HUMVerifyFamilyDatacontract.ArrayOfClinicalCaseDTO ClinicalCase;
        public String EmailAddress;
        public HUMVerifyFamilyDatacontract.ArrayOfMemberDTO FamilyMembers;
        public String Gender;
        public String HomePhone;
        public String HumanaRelationDescription;
        public String LastContactDate;
        public HUMVerifyFamilyDatacontract.ArrayOfLongTermCareDTO LongTermCareHistory;
        public String MaskedSSN;
        public String MedicareID;
        public HUMVerifyFamilyDatacontract.ArrayOfMemberCoverageDTO MemberCoverage;
        public HUMVerifyFamilyDatacontract.MemberPropertiesDTO MemberProperties;
        public String NextBirthDay;
        public String PersonID;
        public String PrimaryPartnerName;
        public HUMVerifyFamilyDatacontract.ArrayOfMemberCoveragePropertyDTO Properties;
        public String Race;
        public HUMVerifyFamilyDatacontract.ResidentialAddressDTO ResidentialAddress;
        public String SSN;
        public String SelMemberCoverageEffectiveDate;
        public String SelMemberCoverageEndDate;
        public String TenantID;
        public String WKEmailAddress;
        public String WorkPhoneExtension;
        public String WorkPhone;
        private String[] DateOfBirth_type_info = new String[]{'DateOfBirth','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] FirstName_type_info = new String[]{'FirstName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] LastName_type_info = new String[]{'LastName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberGenKey_type_info = new String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberID_type_info = new String[]{'MemberID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MiddleInitial_type_info = new String[]{'MiddleInitial','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Address_type_info = new String[]{'Address','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Age_type_info = new String[]{'Age','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','false'};
        private String[] AlternateID_type_info = new String[]{'AlternateID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ClientId_type_info = new String[]{'ClientId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ClinicalCase_type_info = new String[]{'ClinicalCase','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] EmailAddress_type_info = new String[]{'EmailAddress','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] FamilyMembers_type_info = new String[]{'FamilyMembers','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Gender_type_info = new String[]{'Gender','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] HomePhone_type_info = new String[]{'HomePhone','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] HumanaRelationDescription_type_info = new String[]{'HumanaRelationDescription','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] LastContactDate_type_info = new String[]{'LastContactDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] LongTermCareHistory_type_info = new String[]{'LongTermCareHistory','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MaskedSSN_type_info = new String[]{'MaskedSSN','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MedicareID_type_info = new String[]{'MedicareID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberCoverage_type_info = new String[]{'MemberCoverage','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberProperties_type_info = new String[]{'MemberProperties','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] NextBirthDay_type_info = new String[]{'NextBirthDay','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PersonID_type_info = new String[]{'PersonID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] PrimaryPartnerName_type_info = new String[]{'PrimaryPartnerName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Properties_type_info = new String[]{'Properties','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] Race_type_info = new String[]{'Race','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] ResidentialAddress_type_info = new String[]{'ResidentialAddress','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SSN_type_info = new String[]{'SSN','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SelMemberCoverageEffectiveDate_type_info = new String[]{'SelMemberCoverageEffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SelMemberCoverageEndDate_type_info = new String[]{'SelMemberCoverageEndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] TenantID_type_info = new String[]{'TenantID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] WKEmailAddress_type_info = new String[]{'WKEmailAddress','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] WorkPhoneExtension_type_info = new String[]{'WorkPhoneExtension','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] WorkPhone_type_info = new String[]{'WorkPhone','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'DateOfBirth','FirstName','LastName','MemberGenKey','MemberID','MiddleInitial','Address','Age','AlternateID','ClientId','ClinicalCase','EmailAddress','FamilyMembers','Gender','HomePhone','HumanaRelationDescription','LastContactDate','LongTermCareHistory','MaskedSSN','MedicareID','MemberCoverage','MemberProperties','NextBirthDay','PersonID','PrimaryPartnerName','Properties','Race','ResidentialAddress','SSN','SelMemberCoverageEffectiveDate','SelMemberCoverageEndDate','TenantID','WKEmailAddress','WorkPhoneExtension','WorkPhone'};
    }
    public class SupplementCoverageKeysDTO {
        public String CustCovSeqNbr;
        public String CustomerGenKey;
        public String MemberCoverageEffectiveDate;
        public String MemberGenKey;
        public String SubscriberGenKey;
        private String[] CustCovSeqNbr_type_info = new String[]{'CustCovSeqNbr','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] CustomerGenKey_type_info = new String[]{'CustomerGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberCoverageEffectiveDate_type_info = new String[]{'MemberCoverageEffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberGenKey_type_info = new String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] SubscriberGenKey_type_info = new String[]{'SubscriberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'CustCovSeqNbr','CustomerGenKey','MemberCoverageEffectiveDate','MemberGenKey','SubscriberGenKey'};
    }
    public class ArrayOfSupplementalCoverageDTO {
        public HUMVerifyFamilyDatacontract.SupplementalCoverageDTO[] SupplementalCoverageDTO;
        private String[] SupplementalCoverageDTO_type_info = new String[]{'SupplementalCoverageDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'SupplementalCoverageDTO'};
    }
    public class BaseMemberDTO {
        public String DateOfBirth;
        public String FirstName;
        public String LastName;
        public String MemberGenKey;
        public String MemberID;
        public String MiddleInitial;
        private String[] DateOfBirth_type_info = new String[]{'DateOfBirth','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] FirstName_type_info = new String[]{'FirstName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] LastName_type_info = new String[]{'LastName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberGenKey_type_info = new String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MemberID_type_info = new String[]{'MemberID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] MiddleInitial_type_info = new String[]{'MiddleInitial','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'DateOfBirth','FirstName','LastName','MemberGenKey','MemberID','MiddleInitial'};
    }
    public class ArrayOfPartnerDetailDTO {
        public HUMVerifyFamilyDatacontract.PartnerDetailDTO[] PartnerDetailDTO;
        private String[] PartnerDetailDTO_type_info = new String[]{'PartnerDetailDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        private String[] field_order_type_info = new String[]{'PartnerDetailDTO'};
    }
    public class APTCDTO {
        public String Amount;
        public String EffectiveDate;
        public String EndDate;
        public String IndiviualAmount;
        public String ModDate;
        public String OtherAmount1;
        public String OtherAmount2;
        public String OtherAmount3;
        public String OtherAmount4;
        public String PremiumAmount;
        Public String SeqNumber;
        private String[] Amount_type_info = new String[]{'Amount','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] EffectiveDate_type_info = new String[]{'EffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] EndDate_type_info = new String[]{'EndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] IndiviualAmount_type_info = new String[]{'IndiviualAmount','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] ModDate_type_info = new String[]{'ModDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] OtherAmount1_type_info = new String[]{'OtherAmount1','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] OtherAmount2_type_info = new String[]{'OtherAmount2','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] OtherAmount3_type_info = new String[]{'OtherAmount3','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] OtherAmount4_type_info = new String[]{'OtherAmount4','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] PremiumAmount_type_info = new String[]{'PremiumAmount','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] SeqNumber_type_info = new String[]{'SeqNumber','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'Amount','EffectiveDate','EndDate','IndiviualAmount','ModDate','OtherAmount1','OtherAmount2','OtherAmount3','OtherAmount4','PremiumAmount','SeqNumber'};
    }
    public class UpdatedMemberDemographicsDetailsDTO {
        public String CntyCd;
        public String DOB;
        public String FirstName;
        public String Gender;
        public String GroupGenKeys;
        public String IsHumanaOneHCR;
        public String LastName;
        public String MiddleInit;
        public String SSN;
        public String TransType;
        public String UpdateCnt;
        public String UpdateType;
        public HUMVerifyFamilyDatacontract.ArrayOfUpdatedMemberAddressDetailsDTO UpdatedMemberAddressDetails;
        public HUMVerifyFamilyDatacontract.ArrayOfUpdatedMemberCoverageDetailsDTO UpdatedMemberCoverageDetails;
        public HUMVerifyFamilyDatacontract.UpdatedMemberResidentialAddressDTO UpdatedMemberResidentialAddressDetails;
        public String UserId;
        private String[] CntyCd_type_info = new String[]{'CntyCd','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] DOB_type_info = new String[]{'DOB','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] FirstName_type_info = new String[]{'FirstName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] Gender_type_info = new String[]{'Gender','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] GroupGenKeys_type_info = new String[]{'GroupGenKeys','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] IsHumanaOneHCR_type_info = new String[]{'IsHumanaOneHCR','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] LastName_type_info = new String[]{'LastName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] MiddleInit_type_info = new String[]{'MiddleInit','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] SSN_type_info = new String[]{'SSN','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] TransType_type_info = new String[]{'TransType','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] UpdateCnt_type_info = new String[]{'UpdateCnt','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] UpdateType_type_info = new String[]{'UpdateType','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] UpdatedMemberAddressDetails_type_info = new String[]{'UpdatedMemberAddressDetails','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] UpdatedMemberCoverageDetails_type_info = new String[]{'UpdatedMemberCoverageDetails','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] UpdatedMemberResidentialAddressDetails_type_info = new String[]{'UpdatedMemberResidentialAddressDetails','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] UserId_type_info = new String[]{'UserId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'CntyCd','DOB','FirstName','Gender','GroupGenKeys','IsHumanaOneHCR','LastName','MiddleInit','SSN','TransType','UpdateCnt','UpdateType','UpdatedMemberAddressDetails','UpdatedMemberCoverageDetails','UpdatedMemberResidentialAddressDetails','UserId'};
    }
    public class GCSValidationErrors {
        public String FieldName;
        public String Message;
        private String[] FieldName_type_info = new String[]{'FieldName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] Message_type_info = new String[]{'Message','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'FieldName','Message'};
    }
    public class ArrayOfAPTCDTO {
        public HUMVerifyFamilyDatacontract.APTCDTO[] APTCDTO;
        private String[] APTCDTO_type_info = new String[]{'APTCDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'APTCDTO'};
    }
    public class ArrayOfGCSValidationErrors {
        public HUMVerifyFamilyDatacontract.GCSValidationErrors[] GCSValidationErrors;
        private String[] GCSValidationErrors_type_info = new String[]{'GCSValidationErrors','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'GCSValidationErrors'};
    }
    public class PersonCoverageInfoDTO {
        public String Category;
        public String MemberIDBase;
        public String MemberIDDependentCode;
        public String SrcPlatformCode;
        private String[] Category_type_info = new String[]{'Category','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] MemberIDBase_type_info = new String[]{'MemberIDBase','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] MemberIDDependentCode_type_info = new String[]{'MemberIDDependentCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] SrcPlatformCode_type_info = new String[]{'SrcPlatformCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'Category','MemberIDBase','MemberIDDependentCode','SrcPlatformCode'};
    }
    public class UpdatedMemberResidentialAddressDTO {
        public String Addr1;
        public String Addr2;
        public String City;
        public String CountyCd;
        public String State;
        public String Zip;
        public String ZipPlusCd;
        private String[] Addr1_type_info = new String[]{'Addr1','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] Addr2_type_info = new String[]{'Addr2','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] City_type_info = new String[]{'City','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] CountyCd_type_info = new String[]{'CountyCd','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] State_type_info = new String[]{'State','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] Zip_type_info = new String[]{'Zip','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] ZipPlusCd_type_info = new String[]{'ZipPlusCd','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'Addr1','Addr2','City','CountyCd','State','Zip','ZipPlusCd'};
    }
    public class ArrayOfUpdatedMemberCoverageDetailsDTO {
        public HUMVerifyFamilyDatacontract.UpdatedMemberCoverageDetailsDTO[] UpdatedMemberCoverageDetailsDTO;
        private String[] UpdatedMemberCoverageDetailsDTO_type_info = new String[]{'UpdatedMemberCoverageDetailsDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'UpdatedMemberCoverageDetailsDTO'};
    }
    public class ResidentialAddressDTO {
        public String AddressSeqNumber;
        public String AdminDivCode;
        public String CityName;
        public String CityNumber;
        public String CountryCode;
        public String CountyCode;
        public String EffectiveDate;
        public String EndDate;
        public String ForeignPostCode;
        public String Line1;
        public String Line2;
        public String Line3;
        public String Line4;
        public String PersonSeqNumber;
        public String StateCode;
        public String TypeCode;
        public String UseCode;
        public String Zip;
        public String ZipDeliveryCode;
        public String ZipPlus;
        private String[] AddressSeqNumber_type_info = new String[]{'AddressSeqNumber','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] AdminDivCode_type_info = new String[]{'AdminDivCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] CityName_type_info = new String[]{'CityName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] CityNumber_type_info = new String[]{'CityNumber','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] CountryCode_type_info = new String[]{'CountryCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] CountyCode_type_info = new String[]{'CountyCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] EffectiveDate_type_info = new String[]{'EffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] EndDate_type_info = new String[]{'EndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] ForeignPostCode_type_info = new String[]{'ForeignPostCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] Line1_type_info = new String[]{'Line1','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] Line2_type_info = new String[]{'Line2','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] Line3_type_info = new String[]{'Line3','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] Line4_type_info = new String[]{'Line4','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] PersonSeqNumber_type_info = new String[]{'PersonSeqNumber','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] StateCode_type_info = new String[]{'StateCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] TypeCode_type_info = new String[]{'TypeCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] UseCode_type_info = new String[]{'UseCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] Zip_type_info = new String[]{'Zip','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] ZipDeliveryCode_type_info = new String[]{'ZipDeliveryCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] ZipPlus_type_info = new String[]{'ZipPlus','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'AddressSeqNumber','AdminDivCode','CityName','CityNumber','CountryCode','CountyCode','EffectiveDate','EndDate','ForeignPostCode','Line1','Line2','Line3','Line4','PersonSeqNumber','StateCode','TypeCode','UseCode','Zip','ZipDeliveryCode','ZipPlus'};
    }
    public class UpdatedMemberAddressDetailsDTO {
        public String Addr1;
        public String Addr2;
        public String City;
        public String FirstName;
        public String HomeEmail;
        public String HomePhone;
        public String LastName;
        public String MemGenKey;
        public String MiddleInitial;
        public String State;
        public String WorkEmail;
        public String WorkPhone;
        public String WorkPhoneExt;
        public String Zip;
        public String ZipPlusCd;
        private String[] Addr1_type_info = new String[]{'Addr1','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] Addr2_type_info = new String[]{'Addr2','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] City_type_info = new String[]{'City','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] FirstName_type_info = new String[]{'FirstName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] HomeEmail_type_info = new String[]{'HomeEmail','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] HomePhone_type_info = new String[]{'HomePhone','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] LastName_type_info = new String[]{'LastName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] MemGenKey_type_info = new String[]{'MemGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] MiddleInitial_type_info = new String[]{'MiddleInitial','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] State_type_info = new String[]{'State','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] WorkEmail_type_info = new String[]{'WorkEmail','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] WorkPhone_type_info = new String[]{'WorkPhone','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] WorkPhoneExt_type_info = new String[]{'WorkPhoneExt','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] Zip_type_info = new String[]{'Zip','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] ZipPlusCd_type_info = new String[]{'ZipPlusCd','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'Addr1','Addr2','City','FirstName','HomeEmail','HomePhone','LastName','MemGenKey','MiddleInitial','State','WorkEmail','WorkPhone','WorkPhoneExt','Zip','ZipPlusCd'};
    }
    public class UpdatedMemberCoverageDetailsDTO {
        public String AE_ContractId;
        public String AE_DepSequence;
        public String AE_SrcGrpId;
        public String AE_SrcPlatformCd;
        public String AE_SubscrSequence;
        public String Benefit;
        public String CusCovSeq;
        public String CusGenKey;
        public String DepDOB;
        public String DepFirstName;
        public String DepLastName;
        public String DepSSN;
        public String EffDate;
        public String EndDate;
        public String MbrGenKey;
        public String MbrTermDate;
        public String MbrTermReason;
        public String Product;
        public String RecordSeparator;
        public String Status;
        public String SubFirstName;
        public String SubGenKey;
        public String SubLastName;
        public String SubSSN;
        public String SubscriberInd;
        private String[] AE_ContractId_type_info = new String[]{'AE_ContractId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] AE_DepSequence_type_info = new String[]{'AE_DepSequence','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] AE_SrcGrpId_type_info = new String[]{'AE_SrcGrpId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] AE_SrcPlatformCd_type_info = new String[]{'AE_SrcPlatformCd','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] AE_SubscrSequence_type_info = new String[]{'AE_SubscrSequence','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] Benefit_type_info = new String[]{'Benefit','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] CusCovSeq_type_info = new String[]{'CusCovSeq','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] CusGenKey_type_info = new String[]{'CusGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] DepDOB_type_info = new String[]{'DepDOB','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] DepFirstName_type_info = new String[]{'DepFirstName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] DepLastName_type_info = new String[]{'DepLastName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] DepSSN_type_info = new String[]{'DepSSN','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] EffDate_type_info = new String[]{'EffDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] EndDate_type_info = new String[]{'EndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] MbrGenKey_type_info = new String[]{'MbrGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] MbrTermDate_type_info = new String[]{'MbrTermDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] MbrTermReason_type_info = new String[]{'MbrTermReason','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] Product_type_info = new String[]{'Product','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] RecordSeparator_type_info = new String[]{'RecordSeparator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] Status_type_info = new String[]{'Status','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] SubFirstName_type_info = new String[]{'SubFirstName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] SubGenKey_type_info = new String[]{'SubGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] SubLastName_type_info = new String[]{'SubLastName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] SubSSN_type_info = new String[]{'SubSSN','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] SubscriberInd_type_info = new String[]{'SubscriberInd','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'AE_ContractId','AE_DepSequence','AE_SrcGrpId','AE_SrcPlatformCd','AE_SubscrSequence','Benefit','CusCovSeq','CusGenKey','DepDOB','DepFirstName','DepLastName','DepSSN','EffDate','EndDate','MbrGenKey','MbrTermDate','MbrTermReason','Product','RecordSeparator','Status','SubFirstName','SubGenKey','SubLastName','SubSSN','SubscriberInd'};
    }
    public class PersonDemographicsLastVerifiedInfoDTO {
        public HUMVerifyFamilyDatacontract.ArrayOfPersonMemberInfoDTO DemographicMemberInfo;
        public String SelectedMemberGenKey;
        private String[] DemographicMemberInfo_type_info = new String[]{'DemographicMemberInfo','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] SelectedMemberGenKey_type_info = new String[]{'SelectedMemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'DemographicMemberInfo','SelectedMemberGenKey'};
    }
    public class ArrayOfUpdatedMemberAddressDetailsDTO {
        public HUMVerifyFamilyDatacontract.UpdatedMemberAddressDetailsDTO[] UpdatedMemberAddressDetailsDTO;
        private String[] UpdatedMemberAddressDetailsDTO_type_info = new String[]{'UpdatedMemberAddressDetailsDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'UpdatedMemberAddressDetailsDTO'};
    }
    public class ArrayOfPersonMemberInfoDTO {
        public HUMVerifyFamilyDatacontract.PersonMemberInfoDTO[] PersonMemberInfoDTO;
        private String[] PersonMemberInfoDTO_type_info = new String[]{'PersonMemberInfoDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'PersonMemberInfoDTO'};
    }
    public class ArrayOfPersonCoverageInfoDTO {
        public HUMVerifyFamilyDatacontract.PersonCoverageInfoDTO[] PersonCoverageInfoDTO;
        private String[] PersonCoverageInfoDTO_type_info = new String[]{'PersonCoverageInfoDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'PersonCoverageInfoDTO'};
    }
    public class PersonMemberInfoDTO {
        public HUMVerifyFamilyDatacontract.ArrayOfPersonCoverageInfoDTO DemographicMemberCoverageInfo;
        public String MemberGenKey;
        private String[] DemographicMemberCoverageInfo_type_info = new String[]{'DemographicMemberCoverageInfo','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] MemberGenKey_type_info = new String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'DemographicMemberCoverageInfo','MemberGenKey'};
    }
    public class GCServiceFaultBase {
        public String ApplicationIdentifier;
        public String ErrorTransactionIdentifier;
        public String TransactionIdentifier;
        private String[] ApplicationIdentifier_type_info = new String[]{'ApplicationIdentifier','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults',null,'0','1','true'};
        private String[] ErrorTransactionIdentifier_type_info = new String[]{'ErrorTransactionIdentifier','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults',null,'0','1','true'};
        private String[] TransactionIdentifier_type_info = new String[]{'TransactionIdentifier','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults','true','false'};
        private String[] field_order_type_info = new String[]{'ApplicationIdentifier','ErrorTransactionIdentifier','TransactionIdentifier'};
    }
    public class GCSPersonServiceFault {
        public String ApplicationIdentifier;
        public String ErrorTransactionIdentifier;
        public String TransactionIdentifier;
        public String FaultCode;
        public String Message;
        public String ReferenceId;
        public String Severity;
        public HUMVerifyFamilyDatacontract.ArrayOfGCSValidationErrors ValidationErrors;
        private String[] ApplicationIdentifier_type_info = new String[]{'ApplicationIdentifier','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults',null,'0','1','true'};
        private String[] ErrorTransactionIdentifier_type_info = new String[]{'ErrorTransactionIdentifier','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults',null,'0','1','true'};
        private String[] TransactionIdentifier_type_info = new String[]{'TransactionIdentifier','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults',null,'0','1','true'};
        private String[] FaultCode_type_info = new String[]{'FaultCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults',null,'0','1','true'};
        private String[] Message_type_info = new String[]{'Message','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults',null,'0','1','true'};
        private String[] ReferenceId_type_info = new String[]{'ReferenceId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults',null,'0','1','true'};
        private String[] Severity_type_info = new String[]{'Severity','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults',null,'0','1','true'};
        private String[] ValidationErrors_type_info = new String[]{'ValidationErrors','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults','true','false'};
        private String[] field_order_type_info = new String[]{'ApplicationIdentifier','ErrorTransactionIdentifier','TransactionIdentifier','FaultCode','Message','ReferenceId','Severity','ValidationErrors'};
    }
    public class ArrayOfAlternateValue {
        public HUMVerifyFamilyDatacontract.AlternateValue[] AlternateValue;
        private String[] AlternateValue_type_info = new String[]{'AlternateValue','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common','true','false'};
        private String[] field_order_type_info = new String[]{'AlternateValue'};
    }
    public class AlternateCoverageIDList {
        public HUMVerifyFamilyDatacontract.ArrayOfAlternateValue AlternateValues;
        private String[] AlternateValues_type_info = new String[]{'AlternateValues','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common','true','false'};
        private String[] field_order_type_info = new String[]{'AlternateValues'};
    }
    public class AlternateValue {
        public String Source;
        public String Type_x;
        public String Value;
        private String[] Source_type_info = new String[]{'Source','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common',null,'0','1','true'};
        private String[] Type_x_type_info = new String[]{'Type','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common',null,'0','1','true'};
        private String[] Value_type_info = new String[]{'Value','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common','true','false'};
        private String[] field_order_type_info = new String[]{'Source','Type_x','Value'};
    }
    public class OIDemographicsOutputDTO {
        public DateTime DemographicsLastVerifiedDate;
        public String DemographicsVerifiedBy;
        public DateTime OIDenLastVerifiedDate;
        public DateTime OIMedLastVerifiedDate;
        private String[] DemographicsLastVerifiedDate_type_info = new String[]{'DemographicsLastVerifiedDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'0','1','true'};
        private String[] DemographicsVerifiedBy_type_info = new String[]{'DemographicsVerifiedBy','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'0','1','true'};
        private String[] OIDenLastVerifiedDate_type_info = new String[]{'OIDenLastVerifiedDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'0','1','true'};
        private String[] OIMedLastVerifiedDate_type_info = new String[]{'OIMedLastVerifiedDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics','true','false'};
        private String[] field_order_type_info = new String[]{'DemographicsLastVerifiedDate','DemographicsVerifiedBy','OIDenLastVerifiedDate','OIMedLastVerifiedDate'};
    }
    public class OIDemographicsInputDTO {
        public String Category;
        public String MemberDependantCode;
        public Long MemberGenKey;
		public String EnterpriseId;
        public String MemberId;
        public String PlatformCode;
        private String[] Category_type_info = new String[]{'Category','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'1','1','true'};
        private String[] MemberDependantCode_type_info = new String[]{'MemberDependantCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'1','1','true'};
        private String[] EnterpriseId_info = new String[]{'EnterpriseId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'1','1','true'};
		private String[] MemberGenKey_type_info = new String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'1','1','false'};
        private String[] MemberId_type_info = new String[]{'MemberId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'1','1','true'};
        private String[] PlatformCode_type_info = new String[]{'PlatformCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics','true','false'};
        private String[] field_order_type_info = new String[]{'Category','MemberDependantCode','MemberGenKey','EnterpriseId','MemberId','PlatformCode'};
    }
}