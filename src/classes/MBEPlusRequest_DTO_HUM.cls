/**
   @Author Gargey Sharma
   @name MBEPlusRequest_DTO_HUM
   @CreateDate 08/16/2017
   @Description Request helper class for MBE Plus service
   @Version 1.0
   @reference REQ - 326261
   @Test Class  
Modification Log :  
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Gargey Sharma                                     08/16/2017                      Original Version
* Sumit Pharande           162950                   01/09/2018                      Added EnhancedHealthBenefitDTO
 Sai Kumar Boga                                     11/10/2020                      REQ - 1567028  CALL_MF03_FR02: KY MMC 2021(Added per CR17)_CRM service must create a NATIVE AMERICAN indicator
****************************************************************************************************************************/
Public Class MBEPlusRequest_DTO_HUM
{
    Public GetMember GetMember;
    
    Public Class GetMember
    {
        Public Member Member;
    }

    Public Class Member
    {                                   
        Public Contact Contact;
        Public RaceList RaceList;
        Public MedicaidIdentifierList MedicaidIdentifierList;
        Public MedicareIdentifierList MedicareIdentifierList;
        Public LongTermCareEligibilityList LongTermCareEligibilityList;
        Public LowIncomeSubsidyLevelList LowIncomeSubsidyLevelList;
        Public PlatformPointerList PlatformPointerList;
        Public SoldProductList SoldProductList;
        Public IndicatorList IndicatorList;
        Public GetCriteria GetCriteria;
    }
    Public Class Contact
    {
        Public ContactProfile ContactProfile;
    }
    
    Public Class ContactProfile
    {
        Public PhoneList PhoneList;
        Public EmailList EmailList;
        Public AddressList AddressList;
    }
    
    Public Class PhoneList
    {
        Public String PersonPhone =''; 
    }
    
    Public Class EmailList
    {
        Public String PersonEmail = ''; 
    }
    
    Public Class AddressList
    {
        Public String PersonAddress = '';
    }

    Public Class RaceList
    {
      Public String Race = ''; 
    }

     Public Class MedicaidIdentifierList
    {
        Public String MedicaidIdentifier = '';
    }
    
    Public Class MedicareIdentifierList
    {
        Public String MedicareIdentifier = '';
    }
    
    Public Class LongTermCareEligibilityList
    {
        Public String LongTermCareEligibility = '';
    }
    
    Public Class LowIncomeSubsidyLevelList
    {
        Public String LowIncomeSubsidyLevel = '';
    }
    
    Public Class PlatformPointerList
    {
        Public String PlatformProxyKey = '';
    }
    
    Public Class SoldProductList
    {
        Public list<SoldProduct> SoldProduct;
    }
    
    Public Class SoldProduct
    {
        Public InsuranceProduct InsuranceProduct;
        Public NonInsuranceProduct NonInsuranceProduct;
    }
    
    Public Class NonInsuranceProduct
    {
        Public SoldProductDetailList SoldProductDetailList;
    }
    
    Public Class InsuranceProduct
    {
        Public SoldProductDetailList SoldProductDetailList;
    }
    
    Public Class SoldProductDetailList
    {
        Public SoldProductDetail SoldProductDetail;
    }
    
    Public Class SoldProductDetail
    {
        Public InsuranceCoverage InsuranceCoverage;
        Public NonInsuranceCoverage NonInsuranceCoverage;
        
    }
    
    Public Class InsuranceCoverage
    {
        Public CoveredRoleList CoveredRoleList;
        Public ReasonList ReasonList;
        Public MemberPlatformPointerList MemberPlatformPointerList;
        Public GroupPlatformPointerList GroupPlatformPointerList;
        Public IndicatorList IndicatorList;
        Public XReferenceList XReferenceList;
        Public Network Network;
        Public String OptionalSupplementalBenefitList = '';
        Public EnhancedHealthBenefit EnhancedHealthBenefit;
        Public String LongTermCare = '';    
        Public CoordinationOfBenefitList CoordinationOfBenefitList;
        Public AdvancedPremiumTaxCreditList AdvancedPremiumTaxCreditList;
        Public WaitingPeriodList WaitingPeriodList;
        Public EnrollmentInfo EnrollmentInfo;
        Public PrimaryCarePhysician PrimaryCarePhysician;        
        Public PrimaryCarePhysicianList PrimaryCarePhysicianList;
        Public CostShareReduction CostShareReduction ; 
        Public CostShareReductionList CostShareReductionList;
        Public OutOfAreaList OutOfAreaList;
        public AncillaryList AncillaryList;
     }
       
        Public Class EnhancedHealthBenefit
        {  
          Public String PediatricDentalTermDate='';
        }
         Public Class CostShareReduction
        {  
         
        }
        
         Public Class PrimaryCarePhysician
        {  
          
        }
        Public Class NonInsuranceCoverage
        { 
          Public CoveredRoleList CoveredRoleList;
          Public ReasonList ReasonList;
          Public MemberPlatformPointerList MemberPlatformPointerList;
          Public GroupPlatformPointerList GroupPlatformPointerList;
          Public IndicatorList IndicatorList;
          Public XReferenceList XReferenceList;
          Public String Utilization = ''; 
          Public String ParentProductInfo = ''; 

        }
    
    Public Class CoveredRoleList
    {
        Public CoveredRole CoveredRole;
    }

    Public Class CoveredRole
    {
        Public CoveredRolePlatformPointerList CoveredRolePlatformPointerList;
    }
    
    Public Class CoveredRolePlatformPointerList
    {
        Public String PlatformProxyKey = '';    
    }
    
    Public Class ReasonList
    {
        Public String Reason = '';  
    }
    
    Public Class MemberPlatformPointerList
    {
        Public String PlatformProxyKey = '';    
    }
    
    Public Class GroupPlatformPointerList
    {
        Public String PlatformProxyKey = '';     
    }
    
    Public Class IndicatorList
    {
        Public String Indicator = '';
    }
    
    Public Class XReferenceList
    {
        Public XReference XReference;
    }
    
    Public Class XReference
    {
        Public String NameValueList = ''; 
    }
    
    Public Class Network
    {
        Public NetworkDirectoryList NetworkDirectoryList;
    }
    
    Public Class NetworkDirectoryList
    {
        Public String NetworkDirectory = '';     
    }
    
    Public Class CoordinationOfBenefitList
    {
        Public String CoordinationOfBenefit = '';    
    }
    
    Public Class AdvancedPremiumTaxCreditList
    {
        Public String AdvancedPremiumTaxCredit = '';    
    }
    
    Public Class WaitingPeriodList
    {
        Public WaitingPeriod WaitingPeriod;
    }
    
    Public Class WaitingPeriod
    {
        Public WaitingPeriodIndicatorList WaitingPeriodIndicatorList;
    }
    
    Public Class WaitingPeriodIndicatorList
    {
        Public String Indicator = '';   
    }
    
    Public Class EnrollmentInfo
    {
        Public PriorCarrier PriorCarrier;
    }
    
    Public Class PriorCarrier
    {
        Public IndicatorList IndicatorList;
    }
    
    Public Class PrimaryCarePhysicianList
    {
        Public String PrimaryCarePhysician = '';    
    }
    
    Public Class CostShareReductionList
    {
        Public String CostShareReduction = '';
    }
    
    Public Class OutOfAreaList
    {
        Public String OutofArea = '';
    }
    
	Public Class AncillaryList
    {
        Public String Ancillary = '';
    }
	
    Public Class GetCriteria
    {
        Public PersonIdCriteria PersonIdCriteria;
        //Public MemberGenKeyCriteria MemberGenKeyCriteria;
    }
    
    Public Class PersonIdCriteria
    {
        Public String AsOfDate = '';    
        Public Period PeriodList;
        Public String PersonId = '';    
    }
    
    Public Class Period{
        Public List<String> Period;
    }
    
   /* Public Class MemberGenKeyCriteria
    {
        Public String AsOfDate = '';    
        Public Period PeriodList;
        Public String MemberGenKey = '';    
    }*/
    
}