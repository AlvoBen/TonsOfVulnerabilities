/*******************************************************************************************************************************
Apex Class Name : CreditableCoverage_D_HUM.cls
Version         : 1.0
Created On      : 2018
Function        : Data layer Class for CreditableCoverage_C_HUM.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sumit Kumar                                      02/19/2018                  Original Version
********************************************************************************************************************************/
public with sharing class CreditableCoverage_D_HUM
{
     /**
     * Returns CreditableCoverageRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for SaveCCTemplate .
     *
     * @param    
     * @return  CreditableCoverageRequest_DTO_HUM  request structure of the wrapper class CreditableCoverageRequest_DTO_HUM 
     */
    public static CreditableCoverageRequest_DTO_HUM RequestStructureGetResponse(CreditableCoverageTemplateRequest_H_HUM objCreditableCoverageTemplateRequestCls)
    {  try 
        {
            CreditableCoverageRequest_DTO_HUM objRequest=new CreditableCoverageRequest_DTO_HUM();
            CreditableCoverageRequest_DTO_HUM.SaveCCTemplate oCCRequest = new CreditableCoverageRequest_DTO_HUM.SaveCCTemplate();
            oCCRequest.ReceiptDate = objCreditableCoverageTemplateRequestCls.ReceiptDate;
            oCCRequest.MemberID= objCreditableCoverageTemplateRequestCls.MemberID;
            oCCRequest.EnrolleeFirstName= objCreditableCoverageTemplateRequestCls.EnrolleeFirstName;
            oCCRequest.EnrolleeLastName= objCreditableCoverageTemplateRequestCls.EnrolleeLastName;
            oCCRequest.CRMCaseNumber= objCreditableCoverageTemplateRequestCls.CRMCaseNumber;
            oCCRequest.CRMCaseCreateDate= objCreditableCoverageTemplateRequestCls.CRMCaseCreateDate;
            oCCRequest.TrackID= objCreditableCoverageTemplateRequestCls.TrackID;
            oCCRequest.EmpUnionCoverage= objCreditableCoverageTemplateRequestCls.EmpUnionCoverage;
            oCCRequest.EmpUnionCoverageStartDate= objCreditableCoverageTemplateRequestCls.EmpUnionCoverageStartDate;
            oCCRequest.EmpUnionCoverageEndDate= objCreditableCoverageTemplateRequestCls.EmpUnionCoverageEndDate;
            oCCRequest.StateCoverage= objCreditableCoverageTemplateRequestCls.StateCoverage;
            oCCRequest.StateCoverageStartDate= objCreditableCoverageTemplateRequestCls.StateCoverageStartDate;
            oCCRequest.StateCoverageEndDate= objCreditableCoverageTemplateRequestCls.StateCoverageEndDate;
            oCCRequest.VABenefits= objCreditableCoverageTemplateRequestCls.VABenefits;
            oCCRequest.VABenefitsStartDate= objCreditableCoverageTemplateRequestCls.VABenefitsStartDate;
            oCCRequest.VABenefitsEndDate= objCreditableCoverageTemplateRequestCls.VABenefitsEndDate;
            oCCRequest.TricareMilitaryCoverage= objCreditableCoverageTemplateRequestCls.TricareMilitaryCoverage;
            oCCRequest.TricareMilitaryCoverageStartDate= objCreditableCoverageTemplateRequestCls.TricareMilitaryCoverageStartDate;
            oCCRequest.TricareMilitaryCoverageEndDate= objCreditableCoverageTemplateRequestCls.TricareMilitaryCoverageEndDate;
            oCCRequest.MedigapCreditableCoverage= objCreditableCoverageTemplateRequestCls.MedigapCreditableCoverage;
            oCCRequest.MedigapCreditableCoverageStartDate= objCreditableCoverageTemplateRequestCls.MedigapCreditableCoverageStartDate;
            oCCRequest.MedigapCreditableCoverageEndDate= objCreditableCoverageTemplateRequestCls.MedigapCreditableCoverageEndDate;
            oCCRequest.IndianHealthService= objCreditableCoverageTemplateRequestCls.IndianHealthService;
            oCCRequest.IndianHealthServiceStartDate= objCreditableCoverageTemplateRequestCls.IndianHealthServiceStartDate;
            oCCRequest.IndianHealthServiceEndDate= objCreditableCoverageTemplateRequestCls.IndianHealthServiceEndDate;
            oCCRequest.PaceCoverage= objCreditableCoverageTemplateRequestCls.PaceCoverage;
            oCCRequest.PaceCoverageStartDate= objCreditableCoverageTemplateRequestCls.PaceCoverageStartDate;
            oCCRequest.PaceCoverageEndDate= objCreditableCoverageTemplateRequestCls.PaceCoverageEndDate;
            oCCRequest.DiffSourceCoverage= objCreditableCoverageTemplateRequestCls.DiffSourceCoverage;
            oCCRequest.DiffSourceCoverageStartDate= objCreditableCoverageTemplateRequestCls.DiffSourceCoverageStartDate;
            oCCRequest.DiffSourceCoverageEndDate= objCreditableCoverageTemplateRequestCls.DiffSourceCoverageEndDate;
            oCCRequest.HumanaCoverage= objCreditableCoverageTemplateRequestCls.HumanaCoverage;
            oCCRequest.HumanaCoverageStartDate= objCreditableCoverageTemplateRequestCls.HumanaCoverageStartDate;
            oCCRequest.HumanaCoverageEndDate= objCreditableCoverageTemplateRequestCls.HumanaCoverageEndDate;
            oCCRequest.ExtraMedicareCoverage= objCreditableCoverageTemplateRequestCls.ExtraMedicareCoverage;
            oCCRequest.ExtraMedicareCoverageStartDate= objCreditableCoverageTemplateRequestCls.ExtraMedicareCoverageStartDate;
            oCCRequest.ExtraMedicareCoverageEndDate= objCreditableCoverageTemplateRequestCls.ExtraMedicareCoverageEndDate;
            oCCRequest.HurricaneKatrinaCoverage = objCreditableCoverageTemplateRequestCls.HurricaneKatrinaCoverage;
            oCCRequest.HurricaneKatrinaCoverageStartDate = objCreditableCoverageTemplateRequestCls.HurricaneKatrinaCoverageStartDate;
            oCCRequest.HurricaneKatrinaCoverageEndDate = objCreditableCoverageTemplateRequestCls.HurricaneKatrinaCoverageEndDate;
            oCCRequest.PuertoRicoReformaCoverage = objCreditableCoverageTemplateRequestCls.PuertoRicoReformaCoverage;
            oCCRequest.PuertoRicoReformaCoverageStartDate = objCreditableCoverageTemplateRequestCls.PuertoRicoReformaCoverageStartDate;
            oCCRequest.PuertoRicoReformaCoverageEndDate = objCreditableCoverageTemplateRequestCls.PuertoRicoReformaCoverageEndDate;
            oCCRequest.NeverHadCreditibleCoverage= objCreditableCoverageTemplateRequestCls.NeverHadCreditibleCoverage;
            system.debug('***RequestDHUM=='+oCCRequest);
            
            objRequest.SaveCCTemplateRequest=oCCRequest;
            return objRequest;
         }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CreditableCoverage_D_HUM', 'RequestStructureGetResponse');
            return null;
        }
    }
    
        /*
    * This is a method to build the response message returned by Template Service
    * @param CreditableCoverageResponse_DTO_HUM
    * @return CreditableCoverageTemplateResponse_H_HUM
    */
    public static CreditableCoverageTemplateResponse_H_HUM buildResponseMessage(CreditableCoverageResponse_DTO_HUM resHelper) 
    {
     try
      {
       CreditableCoverageTemplateResponse_H_HUM oResponse = new CreditableCoverageTemplateResponse_H_HUM();
         if(oResponse!=null){
            oResponse.CRMCaseNumber = resHelper.SaveCCTemplateResponse.CRMCaseNumber;
            oResponse.Type = resHelper.SaveCCTemplateResponse.Type;
            oResponse.ReturnCode = resHelper.SaveCCTemplateResponse.ReturnCode;
            oResponse.Description = resHelper.SaveCCTemplateResponse.Description;
            
            }
       return oResponse;
      }
      catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CreditableCoverage_D_HUM', 'buildResponseMessage');
            return null;
        }  
    }
    /*
    * This is a method to build the response message returned by Template Service
    * @param CreditableCoverageResponse_DTO_HUM
    * @return CreditableCoverageTemplateResponse_H_HUM
    */
    public static CreditableCoverageTemplateResponse_H_HUM buildResponseMessageFaultCode(CCResponseFaultCode_DTO_HUM resFaultDTO) 
    {
     try
      {
       CreditableCoverageTemplateResponse_H_HUM oResponse = new CreditableCoverageTemplateResponse_H_HUM();
         if(resFaultDTO!=null){
            oResponse.CRMCaseNumber = resFaultDTO.Fault.detail.transactionInfo.transaction_id;
            oResponse.Type = HUMConstants.HUMActionType;
            oResponse.ReturnCode = resFaultDTO.Fault.detail.errorInfo.error_code;
            oResponse.Description = resFaultDTO.Fault.detail.errorInfo.error_message;
            
            }
       return oResponse;
      }
      catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CreditableCoverage_D_HUM', 'buildResponseMessageFaultCode');
            return null;
        }  
    }
}