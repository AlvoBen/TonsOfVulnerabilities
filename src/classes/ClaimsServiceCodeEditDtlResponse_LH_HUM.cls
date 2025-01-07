public class ClaimsServiceCodeEditDtlResponse_LH_HUM 
{
    public GetCodeEditDetailsResponse GetCodeEditDetailsResponse;
    
    public class GetCodeEditDetailsResponse
    {
      public RationaleDTO RationaleDTO;
    }
    
    public class RationaleDTO
    {
      @AuraEnabled public ExCodes ExCodes;
      @AuraEnabled public RejectedProcedureCodes RejectedProcedureCodes;
      @AuraEnabled public PaidProcedurecodes PaidProcedurecodes;
      @AuraEnabled public PaidModifers PaidModifers;
      @AuraEnabled public DiagnosisCodes DiagnosisCodes;
      @AuraEnabled public RationaleDescriptionCodes RationaleDescriptionCodes;
      @AuraEnabled public RationaleExceptionCodes RationaleExceptionCodes;
    }
    
    public class ExCodes
    {
      @AuraEnabled public list<string> ExCode;
    }
    
    public class RejectedProcedureCodes
    {
      @AuraEnabled public list<RejectedProcedureDTO> RejectedProcedureDTO;
    }
    
    public class RejectedProcedureDTO
    {
      @AuraEnabled public string ExCode = '';
      @AuraEnabled public string Platform = '';
      @AuraEnabled public string ProcCode = '';
    }
    
    public class DiagnosisCodes
    {
      @AuraEnabled public list<DiagnosisCode> DiagnosisCode;
    }
    
    public class DiagnosisCode
    {
      @AuraEnabled public string DiagnosisCd = '';
      @AuraEnabled public string DiagnosisCodeIndicator = '';
    }
    
    public class RationaleDescriptionCodes
    {
      @AuraEnabled public list<RationaleDescriptionDTO> RationaleDescriptionDTO;
    }
    
    public class RationaleDescriptionDTO
    {
      @AuraEnabled public string CodeEditInd = '';
      @AuraEnabled public string ExCode = '';
      @AuraEnabled public string RationaleDesc = '';
      @AuraEnabled public string RejectedProcedureCode = '';
    }
    
    public class RationaleExceptionCodes
    {
      @AuraEnabled public list<RationaleExceptionDTO> RationaleExceptionDTO;
    }
    
    public class RationaleExceptionDTO
    {
      @AuraEnabled public string ExceptionDesc = '';
    }
    
    public class PaidModifers
    {
      @AuraEnabled public list<string> PaidModifer;
    }
    
    public class PaidProcedureCodes
    {
      @AuraEnabled public list<string> PaidProcedureCode;
    }
}