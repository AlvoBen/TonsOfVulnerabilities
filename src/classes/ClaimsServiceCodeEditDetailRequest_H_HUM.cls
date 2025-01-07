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
* Gargey Sharma			     30059            02/17/2016                  Original Version
***************************************************************************************************/
public class ClaimsServiceCodeEditDetailRequest_H_HUM 
{
    public GetCodeEditDetails GetCodeEditDetails;
    
    public class GetCodeEditDetails
    {
    	public RationaleDTO RationaleDTO;
    }
    
    public class RationaleDTO
    {
    	public string Consumer;
    	public ExCodes ExCodes;
    	public RejectedProcedureCodes RejectedProcedureCodes;
    	public PaidProcedureCodes PaidProcedureCodes;
    	public PaidModifers PaidModifers;
    	public DiagnosisCodes DiagnosisCodes;
    }
    
    public class DiagnosisCodes
    {
    	public list<DiagnosisCode> DiagnosisCode;
    }
    
    public class ExCodes
    {
    	public list<string> ExCode;
    }
    
    public class PaidModifers
    {
    	public list<string> PaidModifer;
    }
    
    public class PaidProcedureCodes
    {
    	public list<string> PaidProcedureCode;
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
    
    public class DiagnosisCode
    {
    	public string DiagnosisCd = '';
    	public string DiagnosisCodeIndicator = '';
    }
}