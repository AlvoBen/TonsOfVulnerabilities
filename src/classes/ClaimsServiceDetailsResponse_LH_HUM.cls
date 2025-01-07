public class ClaimsServiceDetailsResponse_LH_HUM
{
    public ClaimDetailReponse ClaimDetailReponse;
    
    public class ClaimDetailReponse 
    {
        public ClaimDetailResult ClaimDetailResult;
    }
    
    public class ClaimDetailResult 
    {
        @AuraEnabled public string ClaimNumber ='';    
        @AuraEnabled public string PatientRecordNumber = '';
        @AuraEnabled public DiagnosisCode DiagnosisCode;
        @AuraEnabled public string DiagnosisIndicator; 
        @AuraEnabled public string ClaimMediaCode;
        @AuraEnabled public string SourceAuthKeyId = '';
        @AuraEnabled public string SourceLvCASPrefixCode = '';  
        @AuraEnabled public string DeficiencyIndicator = '';   
        @AuraEnabled public string DeficiencyField = '';
        @AuraEnabled public string DeficiencyDate = '';
        @AuraEnabled public string DeficiencyDesc = '';
        @AuraEnabled public string ChargeAmount = '';   
        @AuraEnabled public string BenefitAllowedAmount = ''; 
        @AuraEnabled public string ProviderWriteOff = '';
        @AuraEnabled public string BenefitDeniedAmount = '';
        @AuraEnabled public string MemberResponsibilityAmount  = '';
        @AuraEnabled public string PaidAmount = ''; 
        @AuraEnabled public string SourceNationalProviderId = '';
        @AuraEnabled public List<ProviderSpecialties> ProviderSpecialties;
        @AuraEnabled public string TotalInterestAmount = '';
        @AuraEnabled public string ExcludeAmount = '';
        @AuraEnabled public string COBPaidAmount = '';
        @AuraEnabled public string PaymentMethod = '';
        @AuraEnabled public string BankAccountCode = '';        
        @AuraEnabled public ClaimDetailLines ClaimDetailLines;
        @AuraEnabled public string ProviderTaxId = '';
    }
    
    public class ProviderSpecialties
    {
        @AuraEnabled public string Description ='';  
    }
    public class DiagnosisCode
    {
        @AuraEnabled public list<string> Code;
    }
    
    public class ClaimDetailLines 
    {
        @AuraEnabled public list<ClaimDetailLine> ClaimDetailLine;
    }
    
    public class ClaimDetailLine 
    {
        @AuraEnabled public string LineNumber  = '';
        @AuraEnabled public string ServiceStartDate = '';
        @AuraEnabled public string ServiceEndDate = '';
        @AuraEnabled public string CheckNumber = '';
        @AuraEnabled public string ParIndicator = '';
        @AuraEnabled public string FeeScheduleId = '';
        @AuraEnabled public string ProviderCISContractId = '';
        @AuraEnabled public string ChargeAmount = '';
        @AuraEnabled public string BenefitAllowedAmount = '';
        @AuraEnabled public string ProviderWriteOff = '';
        @AuraEnabled public string BenefitDeniedAmount = '';
        @AuraEnabled public string DeductibleAmount = '';
        @AuraEnabled public string CopaymentAmount = '';
        @AuraEnabled public string CoinsuranceAmount  = '';
        @AuraEnabled public string MemberResponsibilityAmount = '';
        @AuraEnabled public string PaidAmount = '';
        @AuraEnabled public string ExcludeAmount = '';
        @AuraEnabled public string BenefitPayPCTCode = '';
        @AuraEnabled public string COBPaidAmount = '';
        @AuraEnabled public string LastProcessDate = '';
        @AuraEnabled public PaymentCodes PaymentCodes;
        @AuraEnabled public string RemittanceNumber = '';
        @AuraEnabled public string PayeeCode = '';
        @AuraEnabled public string SourceClaimLineSequence = '';
        @AuraEnabled public string VoucherCode = '';
        @AuraEnabled public string PlaceofTreatmentCode = '';
        @AuraEnabled public string PlaceofTreatmentDescription = '';
        @AuraEnabled public DiagnosisCode DiagnosisCode;
        @AuraEnabled public string PaidStatus;
        @AuraEnabled public string ServiceCode = '';
        @AuraEnabled public string ServiceDescription = '';
        @AuraEnabled public string Revenuecode = '';
        @AuraEnabled public string CPTModifierCode = '';
        @AuraEnabled public string AnesthesiaMinuteNumber = '';
        @AuraEnabled public string ServiceUnitCount = '';
        @AuraEnabled public string CauseOfIllnessCode = '';
        @AuraEnabled public string ToothCode = '';
        @AuraEnabled public string ToothSurfaceCode = '';
        @AuraEnabled public string EncounterIndicator = '';   
        @AuraEnabled public List<ProviderDetails> ProviderDetails;  
        @AuraEnabled public string ClaimProcessOptIndicator= '';
        @AuraEnabled public string ClaimProcessOptDescription= ''; 
    }
    
    public class ProviderDetails
    {
        @AuraEnabled public string ProviderReasonCode = '';
        @AuraEnabled public string ProviderReasonMessage = '';
    }
    
    public class PaymentCodes 
    {
        @AuraEnabled public list<PaymentDetail> PaymentDetail;
    }
    
    public class PaymentDetail
    {
        @AuraEnabled public string PaymentCategory = '';
        @AuraEnabled public string PaymentCode  = '';
        @AuraEnabled public string PaymentEntity = '';
        @AuraEnabled public string HIPAAMessage = '';
        @AuraEnabled public string LegacyCode = '';
        @AuraEnabled public string LegacyMessage = '';
    }
}