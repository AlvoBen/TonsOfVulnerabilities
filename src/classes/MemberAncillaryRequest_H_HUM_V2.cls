/*******************************************************************************************************************************************************
Apex Class Name  : MemberAncillaryRequest_H_HUM_V2
Version          : 1.0
Created Date     : 11/04/2019
Function         : This class is wrapper class to store data from service Request for Ancillary service
Test Class       : MemberAncillaryRequest_H_T_HUM_V2
Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *  Manikanta                  356372                05/31/2019         Original Version - REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
 *  Manikanta                                        06/07/2019         REQ - 379006 Made code changes to align with the DP changes
 *   Yogesh Gupta                                    11/04/2019         V2 Version - REQ-403709 Ancillary Request for Medicaid Id
 *  Keerti Durbha                                    08/20/2020         US-1114331- PR00094035 - MF 02 – Adding three New fields for Medicaid Policy Details section for “SC” 
 *  Subrahmanyam N                                   27/01/2021         REQ - 1896703  CALL_MF05_FR01: KY MMC 2021_The CRM system must display PriorMedicaidID & PriorMedicaidBeginDate
 *  Akash Surwase                                    05/23/2022         User Story 3085055: T1PRJ0054938 - FR09.07 CRM Service must display the following fields on the Medicaid policy detail page for OH Medicaid
 *  Tharun Madishetti								 02/28/2024			US-5479030 & US-5480355 : T1PRJ1132745 - MF 28387 - C13; Consumer - Oklahoma MCD Lightning - Display fields on the Medicaid Policy Details Section
************************************************************************************************************************************************/
public with sharing class MemberAncillaryRequest_H_HUM_V2
{  
   public getMemberAncillary getMemberAncillary; 
   
   public class getMemberAncillary
   { 
       public ancillaryRequest ancillaryRequest;  
   }
  
   public class includes
   {
        Public Boolean ICostShareInformation;
        Public Boolean IMedicaidManagedCareDetails;
        Public Boolean ICommunityEngagementExemptCode;
        public Boolean IDeductibleAmount;
        public Boolean ITypeOfAssistance;
        public Boolean IPremium;
        public Boolean IMedicallyFrail;
        public Boolean IRewards;
        public Boolean IJuvenilleJustice;   
        public Boolean IPenaltyReasonIndicator;
        public Boolean ISuspensionReasonIndicator;
        public Boolean IHospiceIndicator; 
        public Boolean IFosterCareIndicator;
        public Boolean IGuardianshipIndicator;
        public Boolean IHeadOfHousehold;
        public Boolean ILTSS;
        public Boolean IBehavioralHealthCode;
        public Boolean ICapitationCode;
        public Boolean ICaseTypeCode;
        public Boolean IChishlmCase;
        public Boolean IChoiceCode;
        public Boolean IClosureCode;
        public Boolean ICoverageEndDate;
        public Boolean IDepartmentOfCorrection;
        public Boolean IHighRiskPregancyCode;
        public Boolean IMedicalAssistanceCategoryCode;
        public Boolean IMultipleBirthIndicator;
        public Boolean INewBornMothersReferenceId;
        public Boolean IRenewalCode;
        public Boolean IRenewalDate;
        public Boolean ICoordinatedSystemOfCare;
        public Boolean IMaintenanceReason;
        public Boolean IHandicapIndicator;   
        public Boolean IReviewDetails;
        public Boolean IChoiseReasonCd;
        public Boolean IParentInfoIdentifier;
        public Boolean IRiseDetails;  //change related to US-3085055(OhioRise)
       	public Boolean IAIANIndicator; //OK State Changes US-5479030
       	public Boolean IPregnancyInfo;
       	public Boolean ICaseHead;
        public Boolean ICustodyDetails;
   }
     
   public class ancillaryRequest
   {
        public List<includes> includes;
        public Criteria Criteria;
   }
    
   public class memberIdentifier 
   {
      public String MemberIdBase = '';
      public String DependentCode = '';
      public String PlatformCode = '';
      public String CoverageStartDate = '';
      public String GroupId = '';
      public String DivisionId = '';
      public String ProductId = '';
   }
   
    public class Criteria
    {
        public Integer sequenceId; 
        public memberIdentifier memberIdentifier; 
        public String stateCode;
    }
 }