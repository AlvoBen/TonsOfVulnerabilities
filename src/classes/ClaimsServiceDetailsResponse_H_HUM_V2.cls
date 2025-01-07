/**************************************************************************** 
Apex Class Name  : ClaimsServiceDetailsResponse_H_HUM_V2
Version          : 1.0  
Created Date     : March 06, 2018
Function         : response helper for GetClaimsDetails method in claims service
Test Class       : ClaimsServiceDetailsResponse_T_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prasanthi Kandula         212620              03/06/2018                 Original Version
* Raja Sekhar Reddy M V                         11/12/2018                 REQ - 353088 - Added code for Bank code
* Prasanthi Kandula                             09/19/2019                 REQ - 404535 - Added ProviderDetails to view EOR description
* Santhapuri Kishore                            03/05/2020                 US - 746884  Ability to view the Provider Specialty Codes and Description associated to the Provider listed on a claim   
* Pallavi Shewale                               03/03/2021                 US #1422598 - Add 'Claims Payment Responsibility' Field on 'Claim/Pre-determination Details' page  
* Pinky Vijur                                   05/20/2021                 US# 2251807 - T1PRJ0003145 - CR-004 - Remove 'Claims Payment Responsibility' Field from "Provider Details" Section (CRM Service)
* Abhishek Mangutkar                            05/24/2021         		   US - 2201523 -  Add 'Claims Payment Responsibility' Field in Claim Line Item Accordion (CRM Service)
***************************************************************************************************/
public class ClaimsServiceDetailsResponse_H_HUM_V2
{
    public ClaimDetailReponse ClaimDetailReponse;
    
    public class ClaimDetailReponse 
    {
        public ClaimDetailResult ClaimDetailResult;
    }
    
    public class ClaimDetailResult 
    {
        public string ClaimNumber ='';    
        public string PatientRecordNumber = '';
        public DiagnosisCode DiagnosisCode;
        public string DiagnosisIndicator; 
        public string ClaimMediaCode;
        public string SourceAuthKeyId = '';
        public string SourceLvCASPrefixCode = '';  
        public string DeficiencyIndicator = '';   
        public string DeficiencyField = '';
        public string DeficiencyDate = '';
        public string DeficiencyDesc = '';
        public string ChargeAmount = '';   
        public string BenefitAllowedAmount = ''; 
        public string ProviderWriteOff = '';
        public string BenefitDeniedAmount = '';
        public string MemberResponsibilityAmount  = '';
        public string PaidAmount = ''; 
        public string SourceNationalProviderId = '';
        public List<ProviderSpecialties> ProviderSpecialties;
        public string TotalInterestAmount = '';
        public string ExcludeAmount = '';
        public string COBPaidAmount = '';
        public string PaymentMethod = '';
        public string BankAccountCode = '';        
        public ClaimDetailLines ClaimDetailLines;
    }
    
    public class ProviderSpecialties
    {
        public string Description ='';  
    }
    public class DiagnosisCode
    {
        public list<string> Code;
    }
    
    public class ClaimDetailLines 
    {
        public list<ClaimDetailLine> ClaimDetailLine;
    }
    
    public class ClaimDetailLine 
    {
        public string LineNumber  = '';
        public string ServiceStartDate = '';
        public string ServiceEndDate = '';
        public string CheckNumber = '';
        public string ParIndicator = '';
        public string FeeScheduleId = '';
        public string ProviderCISContractId = '';
        public string ChargeAmount = '';
        public string BenefitAllowedAmount = '';
        public string ProviderWriteOff = '';
        public string BenefitDeniedAmount = '';
        public string DeductibleAmount = '';
        public string CopaymentAmount = '';
        public string CoinsuranceAmount  = '';
        public string MemberResponsibilityAmount = '';
        public string PaidAmount = '';
        public string ExcludeAmount = '';
        public string BenefitPayPCTCode = '';
        public string COBPaidAmount = '';
        public string LastProcessDate = '';
        public PaymentCodes PaymentCodes;
        public string RemittanceNumber = '';
        public string PayeeCode = '';
        public string SourceClaimLineSequence = '';
        public string VoucherCode = '';
        public string PlaceofTreatmentCode = '';
        public string PlaceofTreatmentDescription = '';
        public DiagnosisCode DiagnosisCode;
        public string PaidStatus;
        public string ServiceCode = '';
        public string ServiceDescription = '';
        public string Revenuecode = '';
        public string CPTModifierCode = '';
        public string AnesthesiaMinuteNumber = '';
        public string ServiceUnitCount = '';
        public string CauseOfIllnessCode = '';
        public string ToothCode = '';
        public string ToothSurfaceCode = '';
        public string EncounterIndicator = '';   
        public List<ProviderDetails> ProviderDetails;  
		public string ClaimProcessOptIndicator= '';
        public string ClaimProcessOptDescription= ''; 
    }
    
    public class ProviderDetails
    {
        public string ProviderReasonCode = '';
        public string ProviderReasonMessage = '';
    }
    
    public class PaymentCodes 
    {
        public list<PaymentDetail> PaymentDetail;
    }
    
    public class PaymentDetail
    {
        public string PaymentCategory = '';
        public string PaymentCode  = '';
        public string PaymentEntity = '';
        public string HIPAAMessage = '';
        public string LegacyCode = '';
        public string LegacyMessage = '';
    }
}