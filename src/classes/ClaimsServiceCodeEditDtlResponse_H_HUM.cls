/**************************************************************************** 
Apex Class Name  : ClaimsServiceCodeEditDtlResponse_H_HUM 
Version          : 1.0  
Created Date     : Februaray 17, 2016
Function         : Request helper for GetCodeEditDetails method in Claims service
Test Class       : ClaimsServiceCodeEditDtlResponse_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma			     30059            02/17/2016                  Original Version
***************************************************************************************************/
public class ClaimsServiceCodeEditDtlResponse_H_HUM 
{
    public GetCodeEditDetailsResponse GetCodeEditDetailsResponse;
    
    public class GetCodeEditDetailsResponse
    {
    	public RationaleDTO RationaleDTO;
    }
    
    public class RationaleDTO
    {
    	public ExCodes ExCodes;
    	public RejectedProcedureCodes RejectedProcedureCodes;
    	public PaidProcedurecodes PaidProcedurecodes;
    	public PaidModifers PaidModifers;
    	public DiagnosisCodes DiagnosisCodes;
    	public RationaleDescriptionCodes RationaleDescriptionCodes;
    	public RationaleExceptionCodes RationaleExceptionCodes;
    }
    
    public class ExCodes
    {
    	public list<string> ExCode;
    }
    
    public class RejectedProcedureCodes
    {
    	public list<RejectedProcedureDTO> RejectedProcedureDTO;
    }
    
    public class RejectedProcedureDTO
    {
    	public string ExCode = '';
    	public string Platform = '';
    	public string ProcCode = '';
    }
    
    public class DiagnosisCodes
    {
    	public list<DiagnosisCode> DiagnosisCode;
    }
    
    public class DiagnosisCode
    {
    	public string DiagnosisCd = '';
    	public string DiagnosisCodeIndicator = '';
    }
    
    public class RationaleDescriptionCodes
    {
    	public list<RationaleDescriptionDTO> RationaleDescriptionDTO;
    }
    
    public class RationaleDescriptionDTO
    {
    	public string CodeEditInd = '';
    	public string ExCode = '';
    	public string RationaleDesc = '';
    	public string RejectedProcedureCode = '';
    }
    
    public class RationaleExceptionCodes
    {
    	public list<RationaleExceptionDTO> RationaleExceptionDTO;
    }
    
    public class RationaleExceptionDTO
    {
    	public string ExceptionDesc = '';
    }
    
    public class PaidModifers
    {
    	public list<string> PaidModifer;
    }
    
    public class PaidProcedureCodes
    {
    	public list<string> PaidProcedureCode;
    }
}