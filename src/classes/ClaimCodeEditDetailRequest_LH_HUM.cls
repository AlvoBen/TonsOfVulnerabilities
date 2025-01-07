/**************************************************************************** 
Apex Class Name  : ClaimsServiceCodeEditDetailRequest_H_HUM 
Version          : 1.0  
Created Date     : Februaray 17, 2016
Function         : Request helper for GetCodeEditDetails method in Claims service
Test Class       : ClaimsServiceCodeEditDetailRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma              30059            02/17/2016                  Original Version
***************************************************************************************************/
public class ClaimCodeEditDetailRequest_LH_HUM 
{
    public GetCodeEditDetails GetCodeEditDetails;
    
    public class GetCodeEditDetails
    {
        public RationaleDTO RationaleDTO;
    }
    
    public class RationaleDTO
    {
        @AuraEnabled public string Consumer;
        @AuraEnabled public ExCodes ExCodes;
        @AuraEnabled public RejectedProcedureCodes RejectedProcedureCodes;
        @AuraEnabled public PaidProcedureCodes PaidProcedureCodes;
        @AuraEnabled public PaidModifers PaidModifers;
        @AuraEnabled public DiagnosisCodes DiagnosisCodes;
    }
    
    public class DiagnosisCodes
    {
        @AuraEnabled public list<DiagnosisCode> DiagnosisCode;
    }
    
    public class ExCodes
    {
        @AuraEnabled public list<string> ExCode;
    }
    
    public class PaidModifers
    {
        @AuraEnabled public list<string> PaidModifer;
    }
    
    public class PaidProcedureCodes
    {
        @AuraEnabled public list<string> PaidProcedureCode;
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
    
    public class DiagnosisCode
    {
        @AuraEnabled public string DiagnosisCd = '';
        @AuraEnabled public string DiagnosisCodeIndicator = '';
    }
}