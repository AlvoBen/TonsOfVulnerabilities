/*****************************************************************************************
Apex class Name    : MemberAncillaryResponse_H_HUM_V2
Version            : 1.0
Function           : DataLayer Class for the Member Ancillary response
Created Date       : 11/04/2019
Test Class         : MemberAncillaryResponse_H_T_HUM_V2
Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *  Manikanta                      356372                05/31/2019         Original Version - REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
 *  Manikanta                                            06/07/2019         REQ - 379006 Made code changes to align with the DP changes
 *   Yogesh Gupta                                        11/04/2019         V2 Version - REQ-403709 Ancillary Response for Medicaid Id
 *	 Yogesh Gupta										 11/27/2019			Defect - 450407 Response structure changes for State KY
 *  Keerti Durbha                                        08/20/2020             US-1114331- PR00094035 - MF 02 – Adding three New fields for Medicaid Policy Details section for “SC”
 *  Subrahmanyam N                                       27/01/2021          REQ - 1896703  CALL_MF05_FR01: KY MMC 2021_The CRM system must display PriorMedicaidID & PriorMedicaidBeginDate
 *  Akash Surwase                                        05/23/2022          User Story 3085055: T1PRJ0054938 - FR09.07 CRM Service must display the following fields on the Medicaid policy detail page for OH Medicaid
 *  Akash Surwase                                        02/16/2023          User Story 4042809: T1PRJ0881713- FR09.07 v2 - TECH - CRM Service must display the OH Rise Indicator from the MAS Team on the Medicaid policy detail page for OH Medicaid (Classic)
 *  Tharun Madishetti									 02/28/2024			 US-5479030 & US-5480355 : T1PRJ1132745 - MF 28387 - C13; Consumer - Oklahoma MCD Lightning - Display fields on the Medicaid Policy Details Section
*********************************************************************************************************************************/

Public with sharing class MemberAncillaryResponse_H_HUM_V2
{
    
    
    Public MemberAncillaryResponse_H_HUM_V2(){
    }
    
    Public getMemberAncillaryResponse getMemberAncillaryResponse;
    
    Public class getMemberAncillaryResponse
    {
        Public List<AncillaryEntityList>  AncillaryEntityList;        
    }
  
    Public class AncillaryEntityList
    {
        public HeadofHousehold HeadofHousehold;
        Public LTSS LTSS;
        Public MedicaidManagedCareDetails MedicaidManagedCareDetails;
        Public String CostShareInformation = '';
        public String CommunityEngagementExemptCode = '';
        public String TypeOfAssistance = '';
        public Double DeductibleAmount; 
        
        //Defect - 450407 Data Type changes according to response for KY state
        public Premium Premium;
        public String MedicallyFrail = '';
        public String Rewards = '';
        public String JuvenilleJustice = '';  
        public PenaltyReasonIndicator PenaltyReasonIndicator;
        public String SuspensionReasonIndicator = ''; 
        public String HospiceIndicator = '';
        public String FosterCareIndicator = '';
        public String GuardianshipIndicator = '';
        // For State Code LA
        public ChishlmCase ChishlmCase;
        public CoordinatedSystemOfCare CoordinatedSystemOfCare;
        public DepartmentOfCorrection DepartmentOfCorrection;
        public String BehavioralHealthCode = '';
        public String ChoiceCode = '';
        public String NewBornMothersReferenceId = '';
        public String RenewalCode = '';
        public String MultipleBirthIndicator = '';
        public String ClosureCode = '';
        public String HighRiskPregancyCode = '';
        public String CoverageEndDate = '';
        public String MaintenanceReason = '';
        public String HandicapIndicator = '';
        public String CaseTypeCode = ''; 
        public String RenewalDate = '';
        public String MedicalAssistanceCategoryCode = '';
        public String CapitationCode = '';
        // For State Code SC
        public ReviewDetails ReviewDetails;
        public ParentInfoIdentifier ParentInfoIdentifier;
        public ChoiseReasonCd ChoiseReasonCd;
        public RiseDetails RiseDetails;  //Changes related with US 3085055
        //For OK State : US-5479030
        public String AIANIndicator = ''; 
        public CaseHead CaseHead;
        public PregnancyInfo PregnancyInfo;
        public CustodyDetails CustodyDetails;
    }
    
    public class HeadOfHousehold
    {
        public String FirstName = '';
        public String LastName = '';
        public postalAddress postalAddress;
    }
    
    public class postalAddress
    {
        public String addressLine1 = '';
        public String addressLine2 = '';
        public String city = '';
        public String state = '';
        public String zip = '';
    }
    public class PenaltyReasonIndicator {
    	public String penaltyValue='';
    	public String penaltyStartDate ='';
    	public String penaltyEndDate = '';
    }
    public class Premium {
    	public String premiumValue='';
    	public String premiumStartDate='';
    	public String premiumEndDate='';
    }
	
    //FOR LA
    public class ChishlmCase
    {
        public String startDate = '';
        public String endDate = '';
        public String manager = '';
    }
    
     public class CoordinatedSystemOfCare
    {
        public String admitDate = '';
        public String dischargeDate = '';    
    }
    
      public class DepartmentOfCorrection
    {
        public String type = '';
        public String startDate = '';
        public String endDate = '';
        public String preReleaseDate = '';
        public String locationIndicator = '';    
    }
    
    public class LTSS
    {
        public String specialCond = '';
        public String prodPlanId = '';
        public String levelOfCareDate = '';
        public String levelOfCareCode = '';
        public String specialNeedsNote = '';
        public String medicaidAidCategoryCode = '';
        public String medicaidBeginDate = '';
        public String medicaidEndDate = '';
        public String ACWM = '';
        public String otherHealthIndicators = ''; 
        public String ltcProgramRecommendation = '';  
        public String providerCode = ''; 
    }
    public class ReviewDetails
    {
        public String ReviewDate = '';       
    } 
    public class ParentInfoIdentifier
    {
        public String MotherMedicaidID = ''; 
    } 
    public class ChoiseReasonCd
    {
        Public String ChoiseValue = ''; 
    }
    Public Class MedicaidManagedCareDetails
    {
      Public String priorMedicaidid = '';
      Public String priorMedicaidBeginDate = ''; 
    }
    //As part of US-3085055 (OhioRise changes)
    public class RiseDetails
    {
		public String BeginDate = '';
        public String EndDate = '';
        Public String indicator='';
	}    
    public class PregnancyInfo
    {
		public String pregnancyDueDate = '';
	}
    public class CaseHead
    {
		public String firstName = '';
        public String lastName = '';
	}
    public class CustodyDetails
    {
		public String code = '';
        public String description = '';
	}
}