/**************************************************************************** 
Apex Class Name  : GBEServiceResponse_H_HUM 
Version          : 1.0  
Created Date     : February 09, 2016
Function         : Response helper for GetGroupInfo method in GBE Service
Test Class       : GBEServiceResponse_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri   29854                 02/09/2016                    Original Version
* ChiranjeeviRao Ravuri   30345                 03/10/2016                    REQ-255066:Made changes as per new response structure
* Adil Mohammed           30619                 03/23/2016                    REQ - 263776 : Made Changes as per now response strructure
* Firoja Begam                                  10/19/2020                    REQ - 1279297 : PR00095366 - MF 1 - new fields in policy details section (group policy page)
* Firoja Begam                                  02/10/2021                    US-1760349 - PR00095180 - CRM 6.01 Add PEO Indicator for MTV and CAS products  to Member Policy Page (copy)
***************************************************************************************************/
public class GBEServiceResponse_H_HUM
{   
     public GroupInfo GroupInfo;
     public class GroupInfo {
        public String GroupId = '';
        public String Platform = '';
        public String BenefitPackageId = '';
        public String StartDate = '';
        public String EndDate = '';
        public String AsOfDate = '';
        public String BenfitSequenceNumber = '';
        public String GroupName = '';
        public String EffectiveDate = '';
        public String EnrolledSubscriberCountDental = '';
        public String EnrolledSubscriberCountMedical = '';
        public String NextRenewalDate = '';
        public String DentalOriginalEffectiveDate = '';
        public String DivisionRecordsTotalCount= '';
        public Benefit Benefit;
        public DivisionList DivisionList;
        public Exchange Exchange;
        public VendorProgramOfferListGroup VendorProgramOfferListGroup;
        public VendorProgramOfferListSubGroup VendorProgramOfferListSubGroup;
        /****Added for PEO Indicator Field ****/
        public AssociationList AssociationList;
        public OrganizationTypeList OrganizationTypeList;
    }
       
    public class Benefit {
        public String Product = '';
        public String ProductCode = '';
        public String ASOIndicator = '';
        public String Status = '';
        public String Network = '';
        public String BenefitEffectiveDate = '';
        public String BenefitEndDate = '';
        public String UnitCount = '';
        public String UnitType = '';
        public EffectiveProvisionList EffectiveProvisionList;
        public String MentalHealthVendorCode = '';
        public String Company = '';
        public String LegalEntity = '';
        public String SellingLedger = '';
        public String SellingLedgerDescription = '';
        public String MarketId = '';
        public String MarketName = '';
        public String BenefitPeriod = '';
        public String Market = '';
        public String LastRenewalDate = '';
        public String Certificate = '';
        public String RightSourceSpeciality = '';
        public String MaximumDependentAge = '';
        public String MaximumStudentAge = '';
        public String GroupEnrollmentCount = '';
        public String EnrollmentSource = '';
        public String HourlyRequirement = '';
        public String ProductOption = '';
        public OpenEnrollmentList OpenEnrollmentList;
    }
    public class EffectiveProvisionList {
        public List<EffectiveProvision> EffectiveProvision;
    }
    public class EffectiveProvision {
        public String EffectiveProvisionType = '';
        public String EffectiveProvisionValue = '';
    }
    public class OpenEnrollmentList {
        public List<OpenEnrollment> OpenEnrollment;
    }
    public class OpenEnrollment {
        public String BeginDate = '';
        public String EndDate = '';
    }
    public class DivisionList {
        public List<Division> Division;
    }
    public class Division {
        public String VitalityIndicator = '';
        public String SubGroupID = '';
        public String SubGroupName = '';
        public String UnitCount = '';
        public String Unit = '';
        public String EffectiveProvision = '';
    }
    public class Exchange {
        public String ExchangeIndicator = '';
        public String ExchangeName = '';
        public String ExchangeType = '';
        public String SalesChannelIdentifier = '';
        public String ExchangeSegment = '';
        public String BillingPlatform = '';
        public String MetallicTier = '';
        public String IsHumanaOne = '';
        public String BillPlatformInd = '';
        public String IsCommercial = '';
        public String IsMedicare = '';
        public String LineOfBusiness = '';
    }
    public class VendorProgramOfferListGroup {
        public List<VendorProgramOffer> VendorProgramOffer;
    }
    public class VendorProgramOfferListSubGroup {
        public List<VendorProgramOffer> VendorProgramOffer;
    }
    public class VendorProgramOffer {  
        public String VendorIdentifier = '';
        public String VendorIdentifierDescription = '';
        public String VendorType = '';
        public String VendorTypeDescription ='';
        public String VendorBenefitCode = '';
        public String VendorBenefitCodeDescription = '';
        public String ProgramOfferEffectiveDate = '';
        public String ProgramOfferEndDate = '';
    }
    public class AssociationList {
        public List<Association> Association;
    }
    public class Association {  
        public String AssociationType = '';
        public String AssociationName = '';
        public String AssociationEffectiveDate = '';
        public String AssociationEndDate ='';
    }
    public class OrganizationTypeList {
        public List<OrganizationType> OrganizationType;
    }
    public class OrganizationType {  
        public String Code = '';
    }
}