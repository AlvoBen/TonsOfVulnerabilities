/************************************************************************************************
Apex Class Name  : HUMVerifyFamilyCOBService 
Version          : 1.0 
Created Date     : 02/13/2015
Function         : Apex Generated Proxy class for PersonService WSDL
Test Class       : HUMVerifyFamilyCOBServiceTest
Modification Log:
*  Developer Name             Code Review          Date                 Description
* -------------------------------------------------------------------------------------------------             
*  Pradeep Veeravali          20976                02/13/2015           Original Version
* Saurabh Kaple               162071               01/10/2018			REQ - 333069 - Genkey conversion- updates to person service request																																					
* Rajesh CG                   176180               01/25/2018			REQ - 350788 - Convert Address Verification on HumVerifyFamilyServiceController_R1 to Asynch.
* Praveen Kumar Parimi        309949               11/15/2018			REQ - 378729 - Sending the NameSpace for the GetMemberInformation method.
***************************************************************************************************/

Public Class HUMVerifyFamilyCOBService {
    Public Class LongTermCareDTO {
        Public String EffectiveDate;
        Public String EndDate;
        Public String ProdPlanID;
        Public String ProgramCode;
        Public String StateCode;
        Private String[] EffectiveDate_type_info = New String[]{'EffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] EndDate_type_info = New String[]{'EndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ProdPlanID_type_info = New String[]{'ProdPlanID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ProgramCode_type_info = New String[]{'ProgramCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] StateCode_type_info = New String[]{'StateCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'EffectiveDate','EndDate','ProdPlanID','ProgramCode','StateCode'};
    }
    Public Class ArrayOfMemberCoverageDTO {
        Public HUMVerifyFamilyCOBService.MemberCoverageDTO[] MemberCoverageDTO;
        Private String[] MemberCoverageDTO_type_info = New String[]{'MemberCoverageDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'MemberCoverageDTO'};
    }
    Public Class ArrayOfClinicalCaseDTO {
        Public HUMVerifyFamilyCOBService.ClinicalCaseDTO[] ClinicalCaseDTO;
        Private String[] ClinicalCaseDTO_type_info = New String[]{'ClinicalCaseDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'ClinicalCaseDTO'};
    }
    Public Class PlanDTO {
        Public String Description;
        Public String EffectiveDate;
        Public String NonParDeductible;
        Public String NonParMOOP;
        Public String NonParSuperMOOP;
        Public String ParDeductible;
        Public String ParMOOP;
        Public String ParSuperMOOP;
        Public String PolicyType;
        Public String ProductLineCode;
        Public String ProductTypeCode;
        Public HUMVerifyFamilyCOBService.ArrayOfMemberCoveragePropertyDTO Properties;
        Public String SrcCustDivisionID;
        Public String SrcProdKey;
        Public String StateOfIssue;
        Private String[] Description_type_info = New String[]{'Description','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] EffectiveDate_type_info = New String[]{'EffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] NonParDeductible_type_info = New String[]{'NonParDeductible','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] NonParMOOP_type_info = New String[]{'NonParMOOP','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] NonParSuperMOOP_type_info = New String[]{'NonParSuperMOOP','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ParDeductible_type_info = New String[]{'ParDeductible','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ParMOOP_type_info = New String[]{'ParMOOP','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ParSuperMOOP_type_info = New String[]{'ParSuperMOOP','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PolicyType_type_info = New String[]{'PolicyType','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ProductLineCode_type_info = New String[]{'ProductLineCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ProductTypeCode_type_info = New String[]{'ProductTypeCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Properties_type_info = New String[]{'Properties','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SrcCustDivisionID_type_info = New String[]{'SrcCustDivisionID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SrcProdKey_type_info = New String[]{'SrcProdKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] StateOfIssue_type_info = New String[]{'StateOfIssue','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'Description','EffectiveDate','NonParDeductible','NonParMOOP','NonParSuperMOOP','ParDeductible','ParMOOP','ParSuperMOOP','PolicyType','ProductLineCode','ProductTypeCode','Properties','SrcCustDivisionID','SrcProdKey','StateOfIssue'};
    }
    Public Class ProviderDTO {
        Public String PCPAddress;
        Public String PCPCity;
        Public String PCPName;
        Public String PCPState;
        Public String PCPTelephone;
        Public String PCPZip;
        Public String SrcGrouperKey;
        Private String[] PCPAddress_type_info = New String[]{'PCPAddress','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PCPCity_type_info = New String[]{'PCPCity','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PCPName_type_info = New String[]{'PCPName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PCPState_type_info = New String[]{'PCPState','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PCPTelephone_type_info = New String[]{'PCPTelephone','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PCPZip_type_info = New String[]{'PCPZip','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SrcGrouperKey_type_info = New String[]{'SrcGrouperKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'PCPAddress','PCPCity','PCPName','PCPState','PCPTelephone','PCPZip','SrcGrouperKey'};
    }
    Public Class SupplementalCoverageDTO {
        Public String OSBCategoryDescription;
        Public String OSBEffectiveDate;
        Public String OSBEndDate;
        Public String OSBItemCode;
        Public String OSBItemTypeCode;
        Public String OSBPackageKey;
        Public String OSBPackageName;
        Public HUMVerifyFamilyCOBService.SupplementCoverageKeysDTO SupplementCoverageKeys;
        Private String[] OSBCategoryDescription_type_info = New String[]{'OSBCategoryDescription','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] OSBEffectiveDate_type_info = New String[]{'OSBEffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] OSBEndDate_type_info = New String[]{'OSBEndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] OSBItemCode_type_info = New String[]{'OSBItemCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] OSBItemTypeCode_type_info = New String[]{'OSBItemTypeCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] OSBPackageKey_type_info = New String[]{'OSBPackageKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] OSBPackageName_type_info = New String[]{'OSBPackageName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SupplementCoverageKeys_type_info = New String[]{'SupplementCoverageKeys','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'OSBCategoryDescription','OSBEffectiveDate','OSBEndDate','OSBItemCode','OSBItemTypeCode','OSBPackageKey','OSBPackageName','SupplementCoverageKeys'};
    }
    Public Class MemberCoveragePropertyDTO {
        Public String Key;
        Public String Value;
        Private String[] Key_type_info = New String[]{'Key','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Value_type_info = New String[]{'Value','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'Key','Value'};
    }
    Public Class ArrayOfLongTermCareDTO {
        Public HUMVerifyFamilyCOBService.LongTermCareDTO[] LongTermCareDTO;
        Private String[] LongTermCareDTO_type_info = New String[]{'LongTermCareDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'LongTermCareDTO'};
    }
    Public Class MemberCoverageDTO {
        Public HUMVerifyFamilyCOBService.ArrayOfAPTCDTO APTC;
        Public String ASOIndicator;
        Public HUMVerifyFamilyCOBService.AlternateCoverageIDList AlternateCoverageIDList;
        Public String BillPlatformInd;
        Public HUMVerifyFamilyCOBService.ArrayOfCOBDTO COB;
        Public String CSRIndicator;
        Public String CategoryDescription;
        Public String Category;
        Public String ClientNumber;
        Public String ContinuousCoverageEffectiveDate;
        Public String ContinuousCoverageEndDate;
        Public String CoverageIndicator;
        Public String CoveragePeriod;
        Public String CustCovSeqNbr;
        Public String CustomerGenKey;
        Public String DisplayGroupID;
        Public String DisplayedPlatform;
        Public String DualDemoIndicator;
        Public String EHBTermDate;
        Public String EnrollmentSrcCode;
        Public String ExchangeMemberID;
        Public String GroupName;
        Public Boolean HasParent;
        Public String HumanaRelationDescription;
        Public String InternalCaseID;
        Public String IsHumanaOneDental;
        Public String IsHumanaOneMedical;
        Public Boolean IsMedicarePolicy;
        Public String KICClientID;
        Public String LTCProvCode;
        Public String LineOfBusiness;
        Public String MTVBusinessLevel7;
        Public String MarketDescription;
        Public String MarketID;
        Public String MemberCoverageEffectiveDate;
        Public String MemberCoverageEndDate;
        Public String MemberGenKey;
		Public String EnterpriseId;
        Public String MemberIDBase;
        Public String MemberIDDependentCode;
        Public HUMVerifyFamilyCOBService.NetworkDTO Network;
        Public String OSBPresent;
        Public String PaidThruDate;
        Public String ParentCoverageIdentifier;
        Public String PlanId;
        Public HUMVerifyFamilyCOBService.PlanDTO Plan;
        Public String PlatformCoverageIdentifier;
        Public String ProdPlanID;
        Public HUMVerifyFamilyCOBService.ArrayOfMemberCoveragePropertyDTO Properties;
        Public HUMVerifyFamilyCOBService.ProviderDTO Provider;
        Public String RoutingLOB;
        Public String RxEligibleIndicator;
        Public String SegmentIndicator;
        Public String SellingMarketNumber;
        Public String SourceCustCovKey;
        Public String SourceCustKey;
        Public HUMVerifyFamilyCOBService.ArrayOfMemberCoverageSourceDTO SourceHeaderKeys;
        Public String SrcCustCovKey;
        Public String SrcPlatformCode;
        Public String Status;
        Public String SubscriberGenKey;
        Public String SubscriberIDBase;
        Public String SubscriberIDDependentCode;
        Public String SubscriberInd;
        Public HUMVerifyFamilyCOBService.ArrayOfSupplementalCoverageDTO SupplementalCovgInformation;
        Private String[] APTC_type_info = New String[]{'APTC','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ASOIndicator_type_info = New String[]{'ASOIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] AlternateCoverageIDList_type_info = New String[]{'AlternateCoverageIDList','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] BillPlatformInd_type_info = New String[]{'BillPlatformInd','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] COB_type_info = New String[]{'COB','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] CSRIndicator_type_info = New String[]{'CSRIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] CategoryDescription_type_info = New String[]{'CategoryDescription','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Category_type_info = New String[]{'Category','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ClientNumber_type_info = New String[]{'ClientNumber','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ContinuousCoverageEffectiveDate_type_info = New String[]{'ContinuousCoverageEffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ContinuousCoverageEndDate_type_info = New String[]{'ContinuousCoverageEndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] CoverageIndicator_type_info = New String[]{'CoverageIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] CoveragePeriod_type_info = New String[]{'CoveragePeriod','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] CustCovSeqNbr_type_info = New String[]{'CustCovSeqNbr','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] CustomerGenKey_type_info = New String[]{'CustomerGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] DisplayGroupID_type_info = New String[]{'DisplayGroupID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] DisplayedPlatform_type_info = New String[]{'DisplayedPlatform','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] DualDemoIndicator_type_info = New String[]{'DualDemoIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] EHBTermDate_type_info = New String[]{'EHBTermDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] EnrollmentSrcCode_type_info = New String[]{'EnrollmentSrcCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ExchangeMemberID_type_info = New String[]{'ExchangeMemberID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] GroupName_type_info = New String[]{'GroupName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] HasParent_type_info = New String[]{'HasParent','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','false'};
        Private String[] HumanaRelationDescription_type_info = New String[]{'HumanaRelationDescription','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] InternalCaseID_type_info = New String[]{'InternalCaseID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] IsHumanaOneDental_type_info = New String[]{'IsHumanaOneDental','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] IsHumanaOneMedical_type_info = New String[]{'IsHumanaOneMedical','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] IsMedicarePolicy_type_info = New String[]{'IsMedicarePolicy','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','false'};
        Private String[] KICClientID_type_info = New String[]{'KICClientID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] LTCProvCode_type_info = New String[]{'LTCProvCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] LineOfBusiness_type_info = New String[]{'LineOfBusiness','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MTVBusinessLevel7_type_info = New String[]{'MTVBusinessLevel7','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MarketDescription_type_info = New String[]{'MarketDescription','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MarketID_type_info = New String[]{'MarketID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberCoverageEffectiveDate_type_info = New String[]{'MemberCoverageEffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberCoverageEndDate_type_info = New String[]{'MemberCoverageEndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] EnterpriseId_type_info = New String[]{'EnterpriseId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
		Private String[] MemberGenKey_type_info = New String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberIDBase_type_info = New String[]{'MemberIDBase','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberIDDependentCode_type_info = New String[]{'MemberIDDependentCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Network_type_info = New String[]{'Network','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] OSBPresent_type_info = New String[]{'OSBPresent','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PaidThruDate_type_info = New String[]{'PaidThruDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ParentCoverageIdentifier_type_info = New String[]{'ParentCoverageIdentifier','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PlanId_type_info = New String[]{'PlanId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Plan_type_info = New String[]{'Plan','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PlatformCoverageIdentifier_type_info = New String[]{'PlatformCoverageIdentifier','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ProdPlanID_type_info = New String[]{'ProdPlanID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Properties_type_info = New String[]{'Properties','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Provider_type_info = New String[]{'Provider','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] RoutingLOB_type_info = New String[]{'RoutingLOB','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] RxEligibleIndicator_type_info = New String[]{'RxEligibleIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SegmentIndicator_type_info = New String[]{'SegmentIndicator','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SellingMarketNumber_type_info = New String[]{'SellingMarketNumber','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SourceCustCovKey_type_info = New String[]{'SourceCustCovKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SourceCustKey_type_info = New String[]{'SourceCustKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SourceHeaderKeys_type_info = New String[]{'SourceHeaderKeys','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SrcCustCovKey_type_info = New String[]{'SrcCustCovKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SrcPlatformCode_type_info = New String[]{'SrcPlatformCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Status_type_info = New String[]{'Status','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SubscriberGenKey_type_info = New String[]{'SubscriberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SubscriberIDBase_type_info = New String[]{'SubscriberIDBase','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SubscriberIDDependentCode_type_info = New String[]{'SubscriberIDDependentCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SubscriberInd_type_info = New String[]{'SubscriberInd','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SupplementalCovgInformation_type_info = New String[]{'SupplementalCovgInformation','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'APTC','ASOIndicator','AlternateCoverageIDList','BillPlatformInd','COB','CSRIndicator','CategoryDescription','Category','ClientNumber','ContinuousCoverageEffectiveDate','ContinuousCoverageEndDate','CoverageIndicator','CoveragePeriod','CustCovSeqNbr','CustomerGenKey','DisplayGroupID','DisplayedPlatform','DualDemoIndicator','EHBTermDate','EnrollmentSrcCode','ExchangeMemberID','GroupName','HasParent','HumanaRelationDescription','InternalCaseID','IsHumanaOneDental','IsHumanaOneMedical','IsMedicarePolicy','KICClientID','LTCProvCode','LineOfBusiness','MTVBusinessLevel7','MarketDescription','MarketID','MemberCoverageEffectiveDate','MemberCoverageEndDate' ,'EnterpriseId','MemberIDBase','MemberIDDependentCode','Network','OSBPresent','PaidThruDate','ParentCoverageIdentifier','PlanId','Plan','PlatformCoverageIdentifier','ProdPlanID','Properties','Provider','RoutingLOB','RxEligibleIndicator','SegmentIndicator','SellingMarketNumber','SourceCustCovKey','SourceCustKey','SourceHeaderKeys','SrcCustCovKey','SrcPlatformCode','Status','SubscriberGenKey','SubscriberIDBase','SubscriberIDDependentCode','SubscriberInd','SupplementalCovgInformation'};
    }
    Public Class PartnerDetailDTO {
        Public String EndDate;
        Public String PartnerName;
        Public String PartnerType;
        Public String StartDate;
        Private String[] EndDate_type_info = New String[]{'EndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PartnerName_type_info = New String[]{'PartnerName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PartnerType_type_info = New String[]{'PartnerType','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] StartDate_type_info = New String[]{'StartDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'EndDate','PartnerName','PartnerType','StartDate'};
    }
    Public Class ArrayOfMemberCoverageSourceDTO {
        Public HUMVerifyFamilyCOBService.MemberCoverageSourceDTO[] MemberCoverageSourceDTO;
        Private String[] MemberCoverageSourceDTO_type_info = New String[]{'MemberCoverageSourceDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'MemberCoverageSourceDTO'};
    }
    Public Class MemberCoverageSourceDTO {
        Public String Key;
        Public String SourceCode;
        Public String Source;
        Private String[] Key_type_info = New String[]{'Key','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SourceCode_type_info = New String[]{'SourceCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Source_type_info = New String[]{'Source','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'Key','SourceCode','Source'};
    }
    Public Class ArrayOfMemberCoveragePropertyDTO {
        Public HUMVerifyFamilyCOBService.MemberCoveragePropertyDTO[] MemberCoveragePropertyDTO;
        Private String[] MemberCoveragePropertyDTO_type_info = New String[]{'MemberCoveragePropertyDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'MemberCoveragePropertyDTO'};
    }
    Public Class COBDTO {
        Public String CarrierName;
        Public String EffectiveDate;
        Public String EndDate;
        Public String Primary;
        Private String[] CarrierName_type_info = New String[]{'CarrierName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] EffectiveDate_type_info = New String[]{'EffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] EndDate_type_info = New String[]{'EndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Primary_type_info = New String[]{'Primary','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'CarrierName','EffectiveDate','EndDate','Primary'};
    }
    Public Class ClinicalCaseDTO {
        Public String CaseType;
        Public HUMVerifyFamilyCOBService.ArrayOfPartnerDetailDTO PartnerDetail;
        Private String[] CaseType_type_info = New String[]{'CaseType','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PartnerDetail_type_info = New String[]{'PartnerDetail','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'CaseType','PartnerDetail'};
    }
    Public Class MemberPropertiesDTO {
        Public String DemographicsChangedBy;
        Public DateTime DemographicsChangedTimeStamp;
        Public String DemographicsVerifiedBy;
        Public DateTime DemographicsVerifiedTimestamp;
        Public DateTime LastContactInquiredAbout;
        Public DateTime LastContactInquiringFor;
        Public String MedicareID;
        Public String MemberGenKey;
        Public String MemberName;
        Public DateTime OIDenVerifiedTS;
        Public DateTime OIMedVerifiedTS;
        Public Boolean VIP;
        Private String[] DemographicsChangedBy_type_info = New String[]{'DemographicsChangedBy','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] DemographicsChangedTimeStamp_type_info = New String[]{'DemographicsChangedTimeStamp','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] DemographicsVerifiedBy_type_info = New String[]{'DemographicsVerifiedBy','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] DemographicsVerifiedTimestamp_type_info = New String[]{'DemographicsVerifiedTimestamp','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] LastContactInquiredAbout_type_info = New String[]{'LastContactInquiredAbout','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] LastContactInquiringFor_type_info = New String[]{'LastContactInquiringFor','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MedicareID_type_info = New String[]{'MedicareID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberGenKey_type_info = New String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberName_type_info = New String[]{'MemberName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] OIDenVerifiedTS_type_info = New String[]{'OIDenVerifiedTS','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] OIMedVerifiedTS_type_info = New String[]{'OIMedVerifiedTS','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] VIP_type_info = New String[]{'VIP','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','false'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'DemographicsChangedBy','DemographicsChangedTimeStamp','DemographicsVerifiedBy','DemographicsVerifiedTimestamp','LastContactInquiredAbout','LastContactInquiringFor','MedicareID','MemberGenKey','MemberName','OIDenVerifiedTS','OIMedVerifiedTS','VIP'};
    }
    Public Class ArrayOfMemberDTO {
        Public HUMVerifyFamilyCOBService.MemberDTO[] MemberDTO;
        Private String[] MemberDTO_type_info = New String[]{'MemberDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'MemberDTO'};
    }
    Public Class AddressDTO {
        Public String City;
        Public String County;
        Public String Line1;
        Public String Line2;
        Public String State;
        Public String ZipPlus;
        Public String Zip;
        Private String[] City_type_info = New String[]{'City','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] County_type_info = New String[]{'County','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Line1_type_info = New String[]{'Line1','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Line2_type_info = New String[]{'Line2','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] State_type_info = New String[]{'State','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ZipPlus_type_info = New String[]{'ZipPlus','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Zip_type_info = New String[]{'Zip','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'City','County','Line1','Line2','State','ZipPlus','Zip'};
    }
    Public Class NetworkDTO {
        Public String NetworkID;
        Private String[] NetworkID_type_info = New String[]{'NetworkID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'NetworkID'};
    }
    Public Class ArrayOfCOBDTO {
        Public HUMVerifyFamilyCOBService.COBDTO[] COBDTO;
        Private String[] COBDTO_type_info = New String[]{'COBDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'COBDTO'};
    }
    Public Class MemberDTO {
        Public String DateOfBirth;
        Public String FirstName;
        Public String LastName;
        //Public String MemberGenKey;
		Public String EnterpriseId;
        Public String MemberID;
        Public String MiddleInitial;
        Public HUMVerifyFamilyCOBService.AddressDTO Address;
        Public Integer Age;
        Public String AlternateID;
        Public String ClientId;
        Public HUMVerifyFamilyCOBService.ArrayOfClinicalCaseDTO ClinicalCase;
        Public String EmailAddress;
        Public HUMVerifyFamilyCOBService.ArrayOfMemberDTO FamilyMembers;
        Public String Gender;
        Public String HomePhone;
        Public String HumanaRelationDescription;
        Public String LastContactDate;
        Public HUMVerifyFamilyCOBService.ArrayOfLongTermCareDTO LongTermCareHistory;
        Public String MaskedSSN;
        Public String MedicareID;
        Public HUMVerifyFamilyCOBService.ArrayOfMemberCoverageDTO MemberCoverage;
        Public HUMVerifyFamilyCOBService.MemberPropertiesDTO MemberProperties;
        Public String NextBirthDay;
        Public String PersonID;
        Public String PrimaryPartnerName;
        Public HUMVerifyFamilyCOBService.ArrayOfMemberCoveragePropertyDTO Properties;
        Public String Race;
        Public HUMVerifyFamilyCOBService.ResidentialAddressDTO ResidentialAddress;
        Public String SSN;
        Public String SelMemberCoverageEffectiveDate;
        Public String SelMemberCoverageEndDate;
        Public String TenantID;
        Public String WKEmailAddress;
        Public String WorkPhoneExtension;
        Public String WorkPhone;
        Private String[] DateOfBirth_type_info = New String[]{'DateOfBirth','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] FirstName_type_info = New String[]{'FirstName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] LastName_type_info = New String[]{'LastName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] EnterpriseId_type_info = New String[]{'EnterpriseId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
		//Private String[] MemberGenKey_type_info = New String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberID_type_info = New String[]{'MemberID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MiddleInitial_type_info = New String[]{'MiddleInitial','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Address_type_info = New String[]{'Address','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Age_type_info = New String[]{'Age','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','false'};
        Private String[] AlternateID_type_info = New String[]{'AlternateID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ClientId_type_info = New String[]{'ClientId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ClinicalCase_type_info = New String[]{'ClinicalCase','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] EmailAddress_type_info = New String[]{'EmailAddress','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] FamilyMembers_type_info = New String[]{'FamilyMembers','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Gender_type_info = New String[]{'Gender','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] HomePhone_type_info = New String[]{'HomePhone','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] HumanaRelationDescription_type_info = New String[]{'HumanaRelationDescription','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] LastContactDate_type_info = New String[]{'LastContactDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] LongTermCareHistory_type_info = New String[]{'LongTermCareHistory','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MaskedSSN_type_info = New String[]{'MaskedSSN','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MedicareID_type_info = New String[]{'MedicareID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberCoverage_type_info = New String[]{'MemberCoverage','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberProperties_type_info = New String[]{'MemberProperties','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] NextBirthDay_type_info = New String[]{'NextBirthDay','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PersonID_type_info = New String[]{'PersonID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] PrimaryPartnerName_type_info = New String[]{'PrimaryPartnerName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Properties_type_info = New String[]{'Properties','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] Race_type_info = New String[]{'Race','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] ResidentialAddress_type_info = New String[]{'ResidentialAddress','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SSN_type_info = New String[]{'SSN','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SelMemberCoverageEffectiveDate_type_info = New String[]{'SelMemberCoverageEffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SelMemberCoverageEndDate_type_info = New String[]{'SelMemberCoverageEndDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] TenantID_type_info = New String[]{'TenantID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] WKEmailAddress_type_info = New String[]{'WKEmailAddress','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] WorkPhoneExtension_type_info = New String[]{'WorkPhoneExtension','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] WorkPhone_type_info = New String[]{'WorkPhone','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'DateOfBirth','FirstName','LastName' ,'EnterpriseId','MemberID','MiddleInitial','Address','Age','AlternateID','ClientId','ClinicalCase','EmailAddress','FamilyMembers','Gender','HomePhone','HumanaRelationDescription','LastContactDate','LongTermCareHistory','MaskedSSN','MedicareID','MemberCoverage','MemberProperties','NextBirthDay','PersonID','PrimaryPartnerName','Properties','Race','ResidentialAddress','SSN','SelMemberCoverageEffectiveDate','SelMemberCoverageEndDate','TenantID','WKEmailAddress','WorkPhoneExtension','WorkPhone'};
    }
    Public Class SupplementCoverageKeysDTO {
        Public String CustCovSeqNbr;
        Public String CustomerGenKey;
        Public String MemberCoverageEffectiveDate;
        Public String MemberGenKey;
        Public String SubscriberGenKey;
        Private String[] CustCovSeqNbr_type_info = New String[]{'CustCovSeqNbr','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] CustomerGenKey_type_info = New String[]{'CustomerGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberCoverageEffectiveDate_type_info = New String[]{'MemberCoverageEffectiveDate','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberGenKey_type_info = New String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] SubscriberGenKey_type_info = New String[]{'SubscriberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'CustCovSeqNbr','CustomerGenKey','MemberCoverageEffectiveDate','MemberGenKey','SubscriberGenKey'};
    }
    Public Class ArrayOfSupplementalCoverageDTO {
        Public HUMVerifyFamilyCOBService.SupplementalCoverageDTO[] SupplementalCoverageDTO;
        Private String[] SupplementalCoverageDTO_type_info = New String[]{'SupplementalCoverageDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'SupplementalCoverageDTO'};
    }
    /* Not used in any methods Or DTOs
    Public Class BaseMemberDTO {
        Public String DateOfBirth;
        Public String FirstName;
        Public String LastName;
        Public String MemberGenKey;
		Public String EnterpriseId;
        Public String MemberID;
        Public String MiddleInitial;
        Private String[] DateOfBirth_type_info = New String[]{'DateOfBirth','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] FirstName_type_info = New String[]{'FirstName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] LastName_type_info = New String[]{'LastName','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] EnterpriseId_type_info = New String[]{'EnterpriseId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
		Private String[] MemberGenKey_type_info = New String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MemberID_type_info = New String[]{'MemberID','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] MiddleInitial_type_info = New String[]{'MiddleInitial','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'1','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'DateOfBirth','FirstName','LastName','EnterpriseId','MemberGenKey','MemberID','MiddleInitial'};
    }*/
    Public Class ArrayOfPartnerDetailDTO {
        Public HUMVerifyFamilyCOBService.PartnerDetailDTO[] PartnerDetailDTO;
        Private String[] PartnerDetailDTO_type_info = New String[]{'PartnerDetailDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member',null,'0','-1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member','true','false'};
        Private String[] field_order_type_info = New String[]{'PartnerDetailDTO'};
    }
    Public Class UpdateDemographicsResponse_element {
        Public String UpdateDemographicsResult;
        Private String[] UpdateDemographicsResult_type_info = New String[]{'UpdateDemographicsResult','http://tempuri.org/',null,'0','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://tempuri.org/','true','false'};
        Private String[] field_order_type_info = New String[]{'UpdateDemographicsResult'};
    }
    Public Class UpdateDemographicsResponse_elementFuture extends System.WebServiceCalloutFuture {
        Public String getValue() {
            HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element response = (HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element)System.WebServiceCallout.endInvoke(this);
            Return response.UpdateDemographicsResult;
        }
    }
    Public Class UpdateDemographics_element {
        Public String actionType;
        Public HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO lastVerifieDOI;
        Public String memberId;
        Public String trimSize;
        Public HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO demographicsInputDTO;
        Private String[] actionType_type_info = New String[]{'actionType','http://tempuri.org/',null,'0','1','true'};
        Private String[] lastVerifieDOI_type_info = New String[]{'lastVerifieDOI','http://tempuri.org/',null,'0','1','true'};
        Private String[] memberId_type_info = New String[]{'memberId','http://tempuri.org/',null,'0','1','true'};
        Private String[] trimSize_type_info = New String[]{'trimSize','http://tempuri.org/',null,'0','1','true'};
        Private String[] demographicsInputDTO_type_info = New String[]{'demographicsInputDTO','http://tempuri.org/',null,'0','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://tempuri.org/','true','false'};
        Private String[] field_order_type_info = New String[]{'actionType','lastVerifieDOI','memberId','trimSize','demographicsInputDTO'};
    }
    Public Class GetMemberInformationResponse_element {
        Public HUMVerifyFamilyCOBService.ArrayOfMemberDTO GetMemberInformationResult;
        Private String[] GetMemberInformationResult_type_info = New String[]{'GetMemberInformationResult','http://tempuri.org/',null,'0','1','true'};
        Private String[] apex_schema_type_info = New String[]{'http://tempuri.org/','true','false'};
        Private String[] field_order_type_info = New String[]{'GetMemberInformationResult'};
    }
    Public Class GetLastVerifiedDatesResponse_element {
        Public HUMVerifyFamilyCOBService.OIDemographicsOutputDTO GetLastVerifiedDatesResult;
        private String[] GetLastVerifiedDatesResult_type_info = new String[]{'GetLastVerifiedDatesResult','http://tempuri.org/',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://tempuri.org/','true','false'};
        private String[] field_order_type_info = new String[]{'GetLastVerifiedDatesResult'};
    }
                                                                                                                             public class GetLastVerifiedDatesResponse_elementFuture extends System.WebServiceCalloutFuture {
   Public HUMVerifyFamilyCOBService.OIDemographicsOutputDTO getValue() {
            HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element response = (HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element)System.WebServiceCallout.endInvoke(this);
            return response.GetLastVerifiedDatesResult;
        }
    }
    public class GetLastVerifiedDates_element {
        public HUMVerifyFamilyCOBService.OIDemographicsInputDTO oiDemographicDatesDTO;
        private String[] oiDemographicDatesDTO_type_info = new String[]{'oiDemographicDatesDTO','http://tempuri.org/',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://tempuri.org/','true','false'};
        private String[] field_order_type_info = new String[]{'oiDemographicDatesDTO'};
    }
    public class GetMemberInformation_element {
        public String EnterpriseId;
        public Long memberGenKey;
        private String[] EnterpriseId_type_info = new String[] {'EnterpriseId', 'http://tempuri.org/', null, '0', '1', 'false' };
        private String[] memberGenKey_type_info = new String[]{'memberGenKey','http://tempuri.org/',null,'0','1','false'};
        private String[] apex_schema_type_info = new String[]{'http://tempuri.org/','true','false'};
        private String[] field_order_type_info = new String[]{'EnterpriseId','memberGenKey'};
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
        public String SeqNumber;
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
        public HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberAddressDetailsDTO UpdatedMemberAddressDetails;
        public HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberCoverageDetailsDTO UpdatedMemberCoverageDetails;
        public HUMVerifyFamilyCOBService.UpdatedMemberResidentialAddressDTO UpdatedMemberResidentialAddressDetails;
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
        public HUMVerifyFamilyCOBService.APTCDTO[] APTCDTO;
        private String[] APTCDTO_type_info = new String[]{'APTCDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'APTCDTO'};
    }
    public class ArrayOfGCSValidationErrors {
        public HUMVerifyFamilyCOBService.GCSValidationErrors[] GCSValidationErrors;
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
        public HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO[] UpdatedMemberCoverageDetailsDTO;
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
		public String EnterpriseId;
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
        private String[] EnterpriseId_type_info = new String[]{'EnterpriseId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
	    private String[] MiddleInitial_type_info = new String[]{'MiddleInitial','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] State_type_info = new String[]{'State','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] WorkEmail_type_info = new String[]{'WorkEmail','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] WorkPhone_type_info = new String[]{'WorkPhone','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] WorkPhoneExt_type_info = new String[]{'WorkPhoneExt','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] Zip_type_info = new String[]{'Zip','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] ZipPlusCd_type_info = new String[]{'ZipPlusCd','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'Addr1','Addr2','City','FirstName','HomeEmail','HomePhone','LastName','MemGenKey' ,'EnterpriseId','MiddleInitial','State','WorkEmail','WorkPhone','WorkPhoneExt','Zip','ZipPlusCd'};
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
		public String EnterpriseId;
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
        private String[] EnterpriseId_type_info = new String[]{'EnterpriseId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
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
        private String[] field_order_type_info = new String[]{'AE_ContractId','AE_DepSequence','AE_SrcGrpId','AE_SrcPlatformCd','AE_SubscrSequence','Benefit','CusCovSeq','CusGenKey','DepDOB','DepFirstName','DepLastName','DepSSN','EffDate','EndDate','MbrGenKey', 'EnterpriseId','MbrTermDate','MbrTermReason','Product','RecordSeparator','Status','SubFirstName','SubGenKey','SubLastName','SubSSN','SubscriberInd'};
    }
    public class PersonDemographicsLastVerifiedInfoDTO {
        public HUMVerifyFamilyCOBService.ArrayOfPersonMemberInfoDTO DemographicMemberInfo;
        public String SelectedMemberGenKey;
		public String SelectedEnterpriseId;
        private String[] DemographicMemberInfo_type_info = new String[]{'DemographicMemberInfo','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] SelectedMemberGenKey_type_info = new String[]{'SelectedMemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] SelectedEnterpriseId_type_info = new String[] { 'SelectedEnterpriseId', 'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects', null, '1', '1', 'true' };
		private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'DemographicMemberInfo','SelectedMemberGenKey' ,'SelectedEnterpriseId'};
    }
    public class ArrayOfUpdatedMemberAddressDetailsDTO {
        public HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO[] UpdatedMemberAddressDetailsDTO;
        private String[] UpdatedMemberAddressDetailsDTO_type_info = new String[]{'UpdatedMemberAddressDetailsDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'UpdatedMemberAddressDetailsDTO'};
    }
    public class ArrayOfPersonMemberInfoDTO {
        public HUMVerifyFamilyCOBService.PersonMemberInfoDTO[] PersonMemberInfoDTO;
        private String[] PersonMemberInfoDTO_type_info = new String[]{'PersonMemberInfoDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'PersonMemberInfoDTO'};
    }
    public class ArrayOfPersonCoverageInfoDTO {
        public HUMVerifyFamilyCOBService.PersonCoverageInfoDTO[] PersonCoverageInfoDTO;
        private String[] PersonCoverageInfoDTO_type_info = new String[]{'PersonCoverageInfoDTO','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'PersonCoverageInfoDTO'};
    }
    public class PersonMemberInfoDTO {
        public HUMVerifyFamilyCOBService.ArrayOfPersonCoverageInfoDTO DemographicMemberCoverageInfo;
        public String MemberGenKey;
		public String EnterpriseId;
        private String[] DemographicMemberCoverageInfo_type_info = new String[]{'DemographicMemberCoverageInfo','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] MemberGenKey_type_info = new String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
		private String[] EnterpriseId_type_info = new String[]{'EnterpriseId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects','true','false'};
        private String[] field_order_type_info = new String[]{'DemographicMemberCoverageInfo','MemberGenKey', 'EnterpriseId'};
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
        public HUMVerifyFamilyCOBService.ArrayOfGCSValidationErrors ValidationErrors;
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
        public HUMVerifyFamilyCOBService.AlternateValue[] AlternateValue;
        private String[] AlternateValue_type_info = new String[]{'AlternateValue','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common',null,'0','-1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common','true','false'};
        private String[] field_order_type_info = new String[]{'AlternateValue'};
    }
    public class AlternateCoverageIDList {
        public HUMVerifyFamilyCOBService.ArrayOfAlternateValue AlternateValues;
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
        private String[] MemberGenKey_type_info = new String[]{'MemberGenKey','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'1','1','false'};
        private String[] EnterpriseId_type_info = new String[]{'EnterpriseId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'1','1','true'};
		private String[] MemberId_type_info = new String[]{'MemberId','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'1','1','true'};
        private String[] PlatformCode_type_info = new String[]{'PlatformCode','http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics',null,'1','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics','true','false'};
        private String[] field_order_type_info = new String[]{'Category','MemberDependantCode','EnterpriseId', 'MemberGenKey','MemberId','PlatformCode'};
    }
    public class soap {
        public String endpoint_x;
        public Map<String,String> inputHttpHeaders_x;
        public Map<String,String> outputHttpHeaders_x;
        public String clientCertName_x;
        public String clientCert_x;
        public String clientCertPasswd_x;
        public Integer timeout_x;
        private String[] ns_map_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member', 'HUMVerifyFamilyCOBService', 'http://tempuri.org/', 'HUMVerifyFamilyCOBService', 'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.Enums', 'HUMVerifyFamilyCOBService', 'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects', 'HUMVerifyFamilyCOBService', 'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults', 'HUMVerifyFamilyCOBService', 'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common', 'HUMVerifyFamilyCOBService', 'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics', 'HUMVerifyFamilyCOBService'};
        public String UpdateDemographics(String actionType,HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO lastVerifieDOI,String memberId,String trimSize,HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO demographicsInputDTO) {
            HUMVerifyFamilyCOBService.UpdateDemographics_element request_x = new HUMVerifyFamilyCOBService.UpdateDemographics_element();
            request_x.actionType = actionType;
            request_x.lastVerifieDOI = lastVerifieDOI;
            request_x.memberId = memberId;
            request_x.trimSize = trimSize;
            request_x.demographicsInputDTO = demographicsInputDTO;
            HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element response_x;
            Map<String, HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element> response_map_x = new Map<String, HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element>();
            response_map_x.put('response_x', response_x);
            WebServiceCallout.invoke(
              this,
              request_x,
              response_map_x,
              new String[]{endpoint_x,
              'http://tempuri.org/IPersonService/UpdateDemographics',
              'http://tempuri.org/',
              'UpdateDemographics',
              'http://tempuri.org/_V2',
              'UpdateDemographicsResponse',
              'HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element'}
            );
            response_x = response_map_x.get('response_x');
            return response_x.UpdateDemographicsResult;
        }
        public HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture beginUpdateDemographics(System.Continuation continuation,String actionType,HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO lastVerifieDOI,String memberId,String trimSize,HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO demographicsInputDTO) {
            HUMVerifyFamilyCOBService.UpdateDemographics_element request_x = new HUMVerifyFamilyCOBService.UpdateDemographics_element();
            request_x.actionType = actionType;
            request_x.lastVerifieDOI = lastVerifieDOI;
            request_x.memberId = memberId;
            request_x.trimSize = trimSize;
            request_x.demographicsInputDTO = demographicsInputDTO;
            return (HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture) System.WebServiceCallout.beginInvoke(
              this,
              request_x,
              HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture.class,
              continuation,
              new String[]{endpoint_x,
              'http://tempuri.org/IPersonService/UpdateDemographics',
              'http://tempuri.org/',
              'UpdateDemographics',
              'http://tempuri.org/_V2',
              'UpdateDemographicsResponse',
              'HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element'}
            );
        }
        public HUMVerifyFamilyCOBService.OIDemographicsOutputDTO GetLastVerifiedDates(HUMVerifyFamilyCOBService.OIDemographicsInputDTO oiDemographicDatesDTO) {
            HUMVerifyFamilyCOBService.GetLastVerifiedDates_element request_x = new HUMVerifyFamilyCOBService.GetLastVerifiedDates_element();
            request_x.oiDemographicDatesDTO = oiDemographicDatesDTO;
            HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element response_x;
            Map<String, HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element> response_map_x = new Map<String, HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element>();
            response_map_x.put('response_x', response_x);
            WebServiceCallout.invoke(
              this,
              request_x,
              response_map_x,
              new String[]{endpoint_x,
              'http://tempuri.org/IPersonService/GetLastVerifiedDates',
              'http://tempuri.org/',
              'GetLastVerifiedDates',
              'http://tempuri.org/_V2',
              'GetLastVerifiedDatesResponse',
              'HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element'}
            );
            response_x = response_map_x.get('response_x');
            return response_x.GetLastVerifiedDatesResult;
        }
        Public HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_elementFuture beginGetLastVerifiedDates(System.Continuation continuation,HUMVerifyFamilyCOBService.OIDemographicsInputDTO demographicDatesDTO) {
            HUMVerifyFamilyCOBService.GetLastVerifiedDates_element request_x = new HUMVerifyFamilyCOBService.GetLastVerifiedDates_element();
            request_x.oiDemographicDatesDTO = demographicDatesDTO;
            system.debug(demographicDatesDTO);
            HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_elementFuture cc;
            return  (HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_elementFuture) System.WebServiceCallout.beginInvoke(
              this,
              request_x,
              HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_elementFuture.class,
              continuation,
              new String[]{endpoint_x,
              'http://tempuri.org/IPersonService/GetLastVerifiedDates',
              'http://tempuri.org/',
              'GetLastVerifiedDates',
              'http://tempuri.org/_V2',
              'GetLastVerifiedDatesResponse',
              'HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element'}
            );
            
            
        }
        public HUMVerifyFamilyCOBService.ArrayOfMemberDTO GetMemberInformation(String EnterpriseId, Long memberGenKey) {
            HUMVerifyFamilyCOBService.GetMemberInformation_element request_x = new HUMVerifyFamilyCOBService.GetMemberInformation_element();
			request_x.EnterpriseId = EnterpriseId;
            request_x.memberGenKey = memberGenKey;
            HUMVerifyFamilyCOBService.GetMemberInformationResponse_element response_x;
            Map<String, HUMVerifyFamilyCOBService.GetMemberInformationResponse_element> response_map_x = new Map<String, HUMVerifyFamilyCOBService.GetMemberInformationResponse_element>();
            response_map_x.put('response_x', response_x);
            WebServiceCallout.invoke(
              this,
              request_x,
              response_map_x,
              new String[]{endpoint_x,
              'http://tempuri.org/IPersonService/GetMemberInformation',
              'http://tempuri.org/',
              'GetMemberInformation',
              'http://tempuri.org/_V2',
              'GetMemberInformationResponse',
              'HUMVerifyFamilyCOBService.GetMemberInformationResponse_element'}
            );
            response_x = response_map_x.get('response_x');
            return response_x.GetMemberInformationResult;
        }
    }
}