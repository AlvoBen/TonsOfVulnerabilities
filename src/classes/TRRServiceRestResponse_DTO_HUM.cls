/******************************************************************************************************************
Apex class Name    : TRRServiceRestResponse_DTO_HUM
Version            : 1.0
Function           : This class is used as request for get TRR details using Rest Service
Created On         : Feb 28 2019
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------

*   1.0            Divya Proddaturi                                                   Original Version
*                  Seema Kaulgi                                   03/15/2019          REQ - 379048  SF - Ability for all CRM users to SEARCH for member specific Transaction Reply Reports (TRR) in CRM
*                  Vardhman Jain                                  08/18/2021          REQ - US 2098896  - Add Fields to TRR Reply Detail
*                  Nilesh Gadkar                                  05/19/2023          User Story 4407442: T1PRJ0191994- MF 128, 129- Display Member Race and Ethnicity in CRM Service- ‘TRR Reply Detail’ page (Classic)
*				   Vardhman Jain								  05/18/2023          US 4407458- Race and ethnicity story.
------------------------------------------------------------------------------------------------------------------- *
**/


public class TRRServiceRestResponse_DTO_HUM {

public TRRResponse TRRResponse; 

public class TRRResponse { 
public TRRDetails TRRDetails; 
} 
public class TRRDetails { 
public TRRDetail[] TRRDetail; 
} 
public class TRRDetail { 
public string MedicareId='';
public string LastName='';
public string FirstName='';
public string MiddleInitial='';
public string DateOfBirth='';
public string GenderCode='';
public string StateCode='';
public string CountyCode='';
public string Contract='';
public string PBP='';
public string EffectiveDate='';
public string EndDate='';
public string ReplyCode='';
public string TransactionCode='';
public string ElectionTypeCode='';
public string DedupIndicator='';
public string UIInitialChangeFlag='';
public string UIOrganization='';
public string ErrorMessage='';
public string EnrollmentSourceCode='';
public string Variable='';
public string ApplicationDate='';
public string SourceId='';
public string PriorPBP='';
public string HumanaReceivedDate='';
public string TransactionDate='';
public string CMSProcessingTimestamp='';
public string ApplicationDateIndicator='';
public string TRCShortName='';
public string CreditableCoverageFlag='';
public string CumulativeNumberofUncoveredMonths='';
public string SubmittedNumberofUncoveredMonths='';
public string PartDPenaltyAmount='';
public string PartDPenaltyWaivedAmount='';
public string PartDPenaltySubAmount='';
public string PartDRxBIN='';
public string PartDRxPCN='';
public string PartDRxGroup='';
public string PartDRxId='';
public string LISPremiumLevel='';
public string LISCopayCategory='';
public string LISPeriodEffectiveDate='';
public string LISPremAmount='';
public string LISPeriodEndDate='';
public string LISSourceCode='';
public string LISEnrolleeTypeCode='';
public string SecondaryInsDrugFlag='';
public string SecondaryRxId='';
public string SecondaryRxGroup='';
public string SecondaryRxBIN='';
public string SecondaryRxPCN='';
public string EmployerSubsidyOverrideFlag='';
public string EGHPIndicator='';
public string ESRDIndicator='';
public string PartDOptOutFlag='';
public string OutOfAreaFlag='';
public string PremiumWithholdOptionCode='';
public string PartCPremiumAmount='';
public string PartDPremiumAmount='';
public string MSPStatusFlag='';
public string WAIndicator='';
public string EntitlementTypeCode='';
public string InstitutionalIndicator='';
public string DisabilityIndicator='';
public string HospiceIndicator='';
public string DistrictOfficeCode='';
public string DeMinimisDifferentialAmount='';
public string PrevTROOPPBP='';
public string SegmentNumber='';
public string SystemAssignedTransTrackingId='';
public string PlanAssignedTransTrackingId='';
public string DisenrollmentReasonCode='';
public string FADemoOptOut='';
//US 2098896-- TRR Detail change
public string PrefLangcode='';
public string AccessibleFmtCode='';
public string SEPReasCode=''; 
//US 4407442 and 4407458 -- Race and Ethinicity
public string RaceCd='';
public string EthinicityCd='';

} 
}