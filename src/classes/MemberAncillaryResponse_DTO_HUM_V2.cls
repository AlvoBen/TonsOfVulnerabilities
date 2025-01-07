/**************************************************************************** 
Apex Class Name  : MemberAncillaryResponse_DTO_HUM_V2
Version          : 1.0 
Function         : This class Is wrapper class to store data from service
Created Date     : 11/04/2019
Test Class       : MemberAncillaryResponse_DTO_T_HUM_V2
Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *  Manikanta                 	356372           		 05/31/2019         Original Version - REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
 *  Manikanta                                   		 06/26/2019         REQ - 379006 Defect Fix 424421.0001 
 *   Yogesh Gupta                                        11/04/2019         V2 Version - REQ-403709 Ancillary Response for Medicaid Id
 *  Keerti Durbha                                        08/20/2020         US-1114331- PR00094035 - MF 02 � Adding three new fields for Medicaid Policy Details section for �SC� 
 *  Subrahmanyam N                                       27/01/2021         REQ - 1896703  CALL_MF05_FR01: KY MMC 2021_The CRM system must display PriorMedicaidID & PriorMedicaidBeginDate
*   Ashish Kumar								 		 05/03/2021			Made parameters as @AuraEnabled for lightning functionality
*  Akash Surwase                                    05/23/2022                 User Story 3085055: T1PRJ0054938 - FR09.07 CRM Service must display the following fields on the Medicaid policy detail page for OH Medicaid
*   Santhi Mandava                                       06/15/2023         US-4525682 - Medicaid info enhancements - Mothers medicaid id, admit date and discharge date
*  Tharun Madishetti									02/28/2024			US-5479030 & US-5480355 : T1PRJ1132745 - MF 28387 - C13; Consumer - Oklahoma MCD Lightning - Display fields on the Medicaid Policy Details Section
***************************************************************************************************/
public with sharing class MemberAncillaryResponse_DTO_HUM_V2
{
  
    public String CommunityEngagementExemptCode{get;set;}
    public String TypeOfAssistance{get;set;}
    public Double DeductibleAmount{get;set;} 
    public String MemberIdBase{get;set;}
    public String DependentCode{get;set;}
    public String PlatformCode{get;set;}
    public DateTime CoverageStartDate{get;set;}
    public String GroupId{get;set;}
    public String DivisionId{get;set;}
    public String ProductId{get;set;}
    public String Type{get;set;}
    public String premiumValue{get;set;}
    public String premiumStartDate{get;set;}
    public String premiumEndDate{get;set;}
    public String penaltyValue{get;set;}
    public String penaltyStartDate{get;set;}
    public String penaltyEndDate{get;set;}
    public String suspensionValue{get;set;}
    public String rewardsValue{get;set;}
    public String FirstName{get;set;}
    public String LastName{get;set;}
    public String fullName{get;set;}
    public String AddressLine1{get;set;}
    public String AddressLine2{get;set;}
    public String City{get;set;}
    public String State{get;set;}
    public String Zip{get;set;}
    public String fullAddress{get;set;}
    public String OtherHealthIndicators{get;set;}
    public String SpecialCond{get;set;}
    public String LtcProgramRecommendation{get;set;}
    public String ProdPlanId{get;set;}
    public String ProviderCode{get;set;}
    public String LevelOfCareDate{get;set;}
    public String LevelOfCareCode{get;set;}
    public String SpecialNeedsNote{get;set;}
    public String MedicaidAidCategoryCode{get;set;}
    public String MedicaidBeginDate{get;set;}
    public String MedicaidEndDate{get;set;}
    public String ACWM{get;set;}
    
    //REQ-403709 New Fields added in response
    public String Preminum{get; set;}
    public String MedicallyFrail{get; set;}
    public String Rewards{get; set;}
    public String JuvenilleJustice{get; set;}
    public String PenaltyReasonIndicator{get; set;}
    public String HospiceIndicator{get; set;}
    public String ForesterCareIndicator{get; set;}
    public String GuardianshipIndicator{get; set;}
    public String CoverageEndDate{get; set;}
    public String CapitationCode{get; set;}
    public String ChoiceCode{get; set;}
    public String HighRiskPregancyCode{get; set;}
    public String BehavioralHealthCode{get; set;}
    public String ClosureCode{get; set;}
    public String RenewalDate{get; set;}
    public String RenewalCode{get; set;}
    public String MultipleBirthIndicator{get; set;}
    public String MedicalAssistanceCategoryCode{get; set;}
    public String CaseTypeCode{get; set;}
    public String StateCode{get; set;}
    public String StartDate{get; set;}
    public String EndDate{get; set;}
    @AuraEnabled public String NewBornMothersReferenceId{get; set;}
    @AuraEnabled public String AdmitDate{get; set;}
    @AuraEnabled public String dischargeDate{get; set;}
    
    public String Doctype{get; set;}
    public String DocstartDate{get; set;}
    public String DocendDate{get; set;}
    public String DocpreReleaseDate{get; set;}
    public String DoclocationIndicator{get; set;}
    public String DocstateCode{get; set;}
    public String Manager{get; set;}
    Public String MaintenanceReason{get; set;}
    Public String HandicapIndicator{get; set;}
     
    @AuraEnabled public String ReviewDate{get; set;}
    @AuraEnabled public String ChoiseValue{get; set;}
    @AuraEnabled public String MotherMedicaidID{get; set;}
    @AuraEnabled Public String priorMedicaidid{Get; Set;}
    @AuraEnabled Public String priorMedicaidBeginDate{Get; Set;}
    @AuraEnabled public String CostShareInformation{get;set;}
    @AuraEnabled public String SuspensionReasonIndicator{get; set;}
    @AuraEnabled public String OhioRISEBeginDate{get; set;}
    @AuraEnabled public String OhioRISEEndDate{get; set;}
    @AuraEnabled public String Indvalue{get; set;}     //This are changes related with US-3085055(OhioRise)
    //Adding new fields for Ok state as part of US-5479030
    @AuraEnabled public String AIANIndicatorValue{get; set;}
    @AuraEnabled public String PregnancyDueDate{get; set;}
    @AuraEnabled public String ReviewDate_OK{get; set;}
    @AuraEnabled public String CaseHeadFirstName{get; set;}
    @AuraEnabled public String CaseHeadLastName{get; set;}
    @AuraEnabled public String CustodyCode{get; set;}
    @AuraEnabled public String CustodyDescription{get; set;}    
}