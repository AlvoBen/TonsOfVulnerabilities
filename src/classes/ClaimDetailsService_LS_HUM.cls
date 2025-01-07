public class ClaimDetailsService_LS_HUM {

     /*
    * Returns an Object
    * <p>
    * This method is used to form the input for getCodeEditDetails() service method
    * and display Rationale on Claims Detail Page
    * @param  List<String>,List<ClaimLineItems_LDTO_HUM.PaymentCode>, String, String, String, String, String
    * @return ClaimCodeEditDetailRequest_LH_HUM 
    */
    
    public ClaimCodeEditDetailRequest_LH_HUM codeEditRationale(List<String> lstDiagnosisCd,List<ClaimLineItems_LDTO_HUM.PaymentCode> lstPaymentCode, String sPlatformCode, String sDiagIndicator, String sPaidStatDesc, String sCPTModCode, String sServiceCode) 
    {
       ClaimCodeEditDetailRequest_LH_HUM oClaimCodeEditReq = new ClaimCodeEditDetailRequest_LH_HUM();
       ClaimCodeEditDetailRequest_LH_HUM.GetCodeEditDetails oDetailsRequest = new ClaimCodeEditDetailRequest_LH_HUM.GetCodeEditDetails();
       ClaimCodeEditDetailRequest_LH_HUM.RationaleDTO oRationaleDTO = new ClaimCodeEditDetailRequest_LH_HUM.RationaleDTO();
       oRationaleDTO.Consumer = 'CCP';
       oRationaleDTO.ExCodes = codeEditExCodes(lstPaymentCode);
       oRationaleDTO.RejectedProcedureCodes = codeEditRejectedProcedureCode(sPaidStatDesc,lstPaymentCode,sPlatformCode,sServiceCode );
       oRationaleDTO.PaidProcedureCodes = codeEditPaidProcedureCd(sServiceCode,sPaidStatDesc);   
       oRationaleDTO.PaidModifers = codeEditPaidModifier(sCPTModCode);   
       oRationaleDTO.DiagnosisCodes = codeEditDiagnosisCodes(lstDiagnosisCd,sDiagIndicator);
       oDetailsRequest.RationaleDTO = oRationaleDTO;
       oClaimCodeEditReq.GetCodeEditDetails = oDetailsRequest;
       return oClaimCodeEditReq;
    }
    
    /*
    * Returns an Object
    * <p>
    * This method is used to form the input of ExCode object for getCodeEditDetails() service method
    * 
    * @param  List<ClaimLineItems_LDTO_HUM.PaymentCode>
    * @return ClaimCodeEditDetailRequest_LH_HUM.ExCodes
    */
    
    public ClaimCodeEditDetailRequest_LH_HUM.ExCodes codeEditExCodes(List<ClaimLineItems_LDTO_HUM.PaymentCode> lstPaymentCode)
    {
       ClaimCodeEditDetailRequest_LH_HUM.ExCodes oExCodes = new ClaimCodeEditDetailRequest_LH_HUM.ExCodes();
       List<String> lstStringExCodes = new List<String>();
       string sLegacyCode;
       for(ClaimLineItems_LDTO_HUM.PaymentCode oPaymentCode : lstPaymentCode)
       {
           if(oPaymentCode.sLegacyCd.length()<=3)
           sLegacyCode = oPaymentCode.sLegacyCd;
           else
           {
                 sLegacyCode = oPaymentCode.sLegacyCd;
                 if(sLegacyCode.contains('amp&')== true)
                    {
                       sLegacyCode=sLegacyCode.replaceAll('amp&','');
                    }                 
           }
           lstStringExCodes.add(sLegacyCode);
       }
       oExCodes.ExCode = lstStringExCodes;
       return oExCodes;
    }
    
    /*
    * Returns an Object
    * <p>
    * This method is used to form the input of RejectedProcedureCode object 
    * for getCodeEditDetails() service method
    * 
    * @param  String, List<ClaimLineItems_LDTO_HUM.PaymentCode>, String, String
    * @return ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureCodes
    */
    public ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureCodes codeEditRejectedProcedureCode(String sPaidStatDesc,List<ClaimLineItems_LDTO_HUM.PaymentCode> lstPaymentCode,String sPlatformCode, String sServiceCode)
    {
       ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureCodes oRejectProcedure = new ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureCodes();
       List<ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureDTO> lstRejectDTO = new List<ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureDTO>();
       ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureDTO oRejectDTO = new ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureDTO(); 
       if(sPaidStatDesc == 'DENIED')
       {
          for(ClaimLineItems_LDTO_HUM.PaymentCode oPaymentCode : lstPaymentCode)
           {
               oRejectDTO = new ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureDTO(); 
               if(oPaymentCode.sLegacyCd.length()<=3)
                   oRejectDTO.ExCode  = oPaymentCode.sLegacyCd;
               else
                   oRejectDTO.ExCode = oPaymentCode.sLegacyCd.subString(0,3);
               oRejectDTO.Platform = sPlatformCode;
               oRejectDTO.ProcCode = sServiceCode;
               lstRejectDTO.add(oRejectDTO);
           } 
       }
       else
       {
           oRejectDTO.ExCode  = '';
           oRejectDTO.Platform = '';
           oRejectDTO.ProcCode = '';
           lstRejectDTO.add(oRejectDTO);
       }
       oRejectProcedure.RejectedProcedureDTO = lstRejectDTO; 
       return oRejectProcedure;
    } 
    
    
    /*
    * Returns an Object
    * <p>
    * This method is used to form the input of PaidProcedureCode object 
    * for getCodeEditDetails() service method
    * 
    * @param  String, String
    * @return ClaimCodeEditDetailRequest_LH_HUM.PaidProcedureCodes
    */
    public ClaimCodeEditDetailRequest_LH_HUM.PaidProcedureCodes codeEditPaidProcedureCd(String sServiceCode, String sPaidStatDesc)
    {
       ClaimCodeEditDetailRequest_LH_HUM.PaidProcedureCodes oPaidCodes = new ClaimCodeEditDetailRequest_LH_HUM.PaidProcedureCodes();
       List<String> lstPaidCode = new List<String>();
       if(sPaidStatDesc == 'DENIED')
       {
           sServiceCode = '';
           lstPaidCode.add(sServiceCode);
       }
       if(sPaidStatDesc == 'PAID' || sPaidStatDesc == 'PREPAID' )
       {
           lstPaidCode.add(sServiceCode);
       }
       oPaidCodes.PaidProcedureCode = lstPaidCode;
       return oPaidCodes;
    }
    
    /*
    * Returns an Object
    * <p>
    * This method is used to form the input of PaidModifier object 
    * for getCodeEditDetails() service method
    * 
    * @param  String
    * @return ClaimCodeEditDetailRequest_LH_HUM.PaidModifers
    */
    public ClaimCodeEditDetailRequest_LH_HUM.PaidModifers codeEditPaidModifier(String sCPTModCode) 
    {
       ClaimCodeEditDetailRequest_LH_HUM.PaidModifers oPaidModifier = new ClaimCodeEditDetailRequest_LH_HUM.PaidModifers();
       List<String> lstPaidModifier = new List<String>();
       lstPaidModifier.add(sCPTModCode); 
       oPaidModifier.PaidModifer = lstPaidModifier;
       return oPaidModifier;
    }
    
   /*
    * Returns an Object
    * <p>
    * This method is used to form the input of DiagnosisCodes object 
    * for getCodeEditDetails() service method
    * 
    * @param  List<String>,String
    * @return ClaimCodeEditDetailRequest_LH_HUM.DiagnosisCodes
    */
    public ClaimCodeEditDetailRequest_LH_HUM.DiagnosisCodes codeEditDiagnosisCodes(List<String> lstDiagnosisCd, String sDiagIndicator)
    {
       ClaimCodeEditDetailRequest_LH_HUM.DiagnosisCodes oDiagnosisCd = new ClaimCodeEditDetailRequest_LH_HUM.DiagnosisCodes();
       List<ClaimCodeEditDetailRequest_LH_HUM.DiagnosisCode> lstDiagnosisCod = new List<ClaimCodeEditDetailRequest_LH_HUM.DiagnosisCode>();
       for(String sCode : lstDiagnosisCd)
       {
               ClaimCodeEditDetailRequest_LH_HUM.DiagnosisCode oDiagCode = new ClaimCodeEditDetailRequest_LH_HUM.DiagnosisCode();
               oDiagCode.DiagnosisCd = sCode;
               oDiagCode.DiagnosisCodeIndicator = 'Diag'+sDiagIndicator;
               lstDiagnosisCod.add(oDiagCode);
       }
        oDiagnosisCd.DiagnosisCode = lstDiagnosisCod;
       return oDiagnosisCd;
    }
    
    /*
    * Returns an HttpRequest
    * <p>
    * This method is used to set the request for getCodeEditDetails() service method
    *
    * @param  HttpRequest,HUM_Webservice_Callout__c,String
    * @return HttpRequest
    */ 
    public HttpRequest setHttpRequestCodeEdit(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcClaimCodeEdit, String sRequestBody)
    {
           oRequest.setMethod(oSvcClaimCodeEdit.Request_Type__c);
            oRequest.setBody(sRequestBody);
            oRequest.setEndpoint(oSvcClaimCodeEdit.End_Point_URL__c);
            if(!Test.isRunningTest())    
            {
                oRequest.setClientCertificateName(oSvcClaimCodeEdit.Certificate_Name__c);
            }
            oRequest.setTimeout(Integer.valueof(oSvcClaimCodeEdit.Timeout__c));
            oRequest.setHeader('content-type', oSvcClaimCodeEdit.Content_Type__c);
            oRequest.setHeader('SOAPAction', '');    
            oRequest.setHeader('Connection','keep-alive');
            return oRequest;
    } 
    
    /*
    * Returns an ClaimsGetCodeEditDetails_LDTO_HUM
    * <p>
    * This method is used to assign response to dto class
    *
    * @param  ClaimsServiceCodeEditDtlResponse_H_HUM,HUM_Webservice_Callout__c,String
    * @return ClaimsGetCodeEditDetails_LDTO_HUM
    */ 
    public List<ClaimsGetCodeEditDetails_LDTO_HUM> codeEditDetailsResponse(ClaimsServiceCodeEditDtlResponse_H_HUM.GetCodeEditDetailsResponse oClaimsCodeEditResponse, string sClaimNumber)
    {
         ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDTO oRationaleDTO =  oClaimsCodeEditResponse.RationaleDTO;
         ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionCodes oRationaleDescCode = oRationaleDTO.RationaleDescriptionCodes;
         List<ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionDTO> lstClaimDetail = oRationaleDescCode.RationaleDescriptionDTO;
         List<ClaimsGetCodeEditDetails_LDTO_HUM> lstCodeEditDTO = new List<ClaimsGetCodeEditDetails_LDTO_HUM>();
         for(ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionDTO oClaimDetail : lstClaimDetail)
         {
             ClaimsGetCodeEditDetails_LDTO_HUM oClaimCodeDetails = new ClaimsGetCodeEditDetails_LDTO_HUM();   
             oClaimCodeDetails.sCodeEditInd =  oClaimDetail.CodeEditInd;
             oClaimCodeDetails.sRationaleDesc =  oClaimDetail.RationaleDesc;
             oClaimCodeDetails.sClaimNbr = sClaimNumber;
             lstCodeEditDTO.add(oClaimCodeDetails);
         }
          return lstCodeEditDTO;
    }
    
    /*
     * Returns a List
     * <p>
     * This method is used to fetch the values for Claim Line Items
     *
     * @param  Object
     * @return List<ClaimDetails_DTO_HUM>
     */
    public List<ClaimLineItems_LDTO_HUM.ClaimDetailLine> claimLineItems(ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailResult oClaimDetailResult,string sProviderName,string sProviderId,string snpiId)
    {
        try
        {
            if(oClaimDetailResult != null)
            {
                List<ClaimLineItems_LDTO_HUM.ClaimDetailLine> lstClaimDetailLineItem = new List<ClaimLineItems_LDTO_HUM.ClaimDetailLine>();
                ClaimsServiceDetailsResponse_LH_HUM.DiagnosisCode oDiagCodeService = oClaimDetailResult.DiagnosisCode;
                ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailLines oClaimDetailLines = oClaimDetailResult.ClaimDetailLines; 
                List<ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailLine> lstClaimDetail = oClaimDetailLines.ClaimDetailLine;
                List<ClaimLineItems_LDTO_HUM.PaymentCode> lstCodes = new List<ClaimLineItems_LDTO_HUM.PaymentCode>();
                for(ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailLine oClaimDetail : lstClaimDetail)
                {
                   ClaimLineItems_LDTO_HUM.ClaimDetailLine oClaimDetails = new ClaimLineItems_LDTO_HUM.ClaimDetailLine();
                   oClaimDetails  = getClaimLineItemDetails(oClaimDetail);
                   oClaimDetails.sFeeSchdId = oClaimDetail.FeeScheduleId;
                   oClaimDetails.sServicingFacility = sProviderName;
                   oClaimDetails.sTIN = sProviderId ;
                   oClaimDetails.sNPI = snpiId ;
                   oClaimDetails.sProviderCISContractId = oClaimDetail.ProviderCISContractId;
                   oClaimDetails.sParInd = oClaimDetail.ParIndicator;
                   oClaimDetails.sToothCode = oClaimDetail.ToothCode ;
                   oClaimDetails.sRemitNumber = oClaimDetail.RemittanceNumber;
                   oClaimDetails.sPayeeCd = oClaimDetail.PayeeCode;                   
                   oClaimDetails.sToothSurface = oClaimDetail.ToothSurfaceCode;
                   oClaimDetails.sCheckNbr = oClaimDetail.CheckNumber;       
                   oClaimDetails.sEncounterIndicator = oClaimDetail.EncounterIndicator;  
                   oClaimDetails.sClaimProcessOptIndicator = oClaimDetail.ClaimProcessOptIndicator;
                   oClaimDetails.sClaimProcessOptDescription = oClaimDetail.ClaimProcessOptDescription;                  
                   ClaimLineItems_LDTO_HUM.DiagCode oDiagCode = new ClaimLineItems_LDTO_HUM.DiagCode();
                   oDiagCode.Code = new List<string>();
                   oDiagCode.Code = oDiagCodeService.Code;
                   oClaimDetails.DiagCode = oDiagCode;
                   //oClaimDetails.PaymentCodes =  getClaimPaymentCodes(oClaimDetail);      
                   lstClaimDetailLineItem.add(oClaimDetails);
                 }
                 return lstClaimDetailLineItem;
               }
           }
           catch(Exception ex)
            {
               HUMExceptionHelper.logErrors(ex, 'ClaimDetails_S_HUM_V2', 'ClaimLineItems');
            }
            return null;
    }
    
    /*
     * Returns a List
     * <p>
     * This method is used to fetch the values for Claim Line Items
     *
     * @param  Object
     * @return List<ClaimDetails_DTO_HUM>
     */
    public ClaimLineItems_LDTO_HUM.ClaimDetailLine getClaimLineItemDetails(ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailLine  oClaimDetail)
    {
        
        ClaimLineItems_LDTO_HUM.ClaimDetailLine oClaimDetails = new ClaimLineItems_LDTO_HUM.ClaimDetailLine();                
        oClaimDetails.sLastProcessDate = oClaimDetail.LastProcessDate;
        oClaimDetails.sSrcClaimLineSequence = oClaimDetail.SourceClaimLineSequence;
        oClaimDetails.sServiceStartDate = oClaimDetail.ServiceStartDate;
        oClaimDetails.sServiceEndDate = oClaimDetail.ServiceEndDate;
        oClaimDetails.sServiceCode = oClaimDetail.ServiceCode;
        oClaimDetails.sServiceDesc = oClaimDetail.ServiceDescription;
        oClaimDetails.sPaidStatDesc = oClaimDetail.PaidStatus;
        oClaimDetails.sChargeAmt = oClaimDetail.ChargeAmount;
        oClaimDetails.sBenAllowAmt = oClaimDetail.BenefitAllowedAmount;
        oClaimDetails.sProvWriteOff = oClaimDetail.ProviderWriteOff;
        oClaimDetails.sExcludeAmt = oClaimDetail.ExcludeAmount;
        oClaimDetails.sBenDenyAmt = oClaimDetail.BenefitDeniedAmount;
        oClaimDetails.sCopayAmt = oClaimDetail.CopaymentAmount;
        oClaimDetails.sDeductAmt = oClaimDetail.DeductibleAmount;
        oClaimDetails.sCoInsAmt = oClaimDetail.CoinsuranceAmount;
        oClaimDetails.sCobPaidAmt = oClaimDetail.COBPaidAmount;
        oClaimDetails.sMbrRespAmt = oClaimDetail.MemberResponsibilityAmount;
        oClaimDetails.sTotalInterest = '0.00';
        oClaimDetails.sPaidAmt = oClaimDetail.PaidAmount;
        //oClaimDetails.sExLineItem = FetchExcodeLineItem(oClaimDetail.PaymentCodes);
        oClaimDetails.sRevenuecode = oClaimDetail.Revenuecode;
        oClaimDetails.sCPTModCode = oClaimDetail.CPTModifierCode;
        oClaimDetails.sAnesMinutes = oClaimDetail.AnesthesiaMinuteNumber;
        oClaimDetails.sServUnitCnt = oClaimDetail.ServiceUnitCount;
        oClaimDetails.sCauseCd = oClaimDetail.CauseOfIllnessCode;
        oClaimDetails.sSrcPOTCode = oClaimDetail.PlaceofTreatmentCode;
        oClaimDetails.sPOTLongDesc = oClaimDetail.PlaceofTreatmentDescription;
        return oClaimDetails;
    }
    
    
    // payment details code
    /*
     * Method Name   :  setupRequest
     * Description   :  This method is used to form http request.
     * Return Type   :  HttpRequest 
     * Parameters    :  HttpRequest, HUM_Webservice_Callout__c, String 
     */    
     
    public HttpRequest setupRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcHeaderPayments, String sRequestBody)
    {
        oRequest.setMethod(oSvcHeaderPayments.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oSvcHeaderPayments.End_Point_URL__c);
         if(!Test.isRunningTest())    
        {
           oRequest.setClientCertificateName(oSvcHeaderPayments.Certificate_Name__c);
        }
        oRequest.setTimeout(Integer.valueof(oSvcHeaderPayments.Timeout__c));
        oRequest.setHeader(GLOBAL_CONSTANT_HUM.Content_type_HUM, oSvcHeaderPayments.Content_Type__c);
        oRequest.setHeader('SOAPAction', '');    
        oRequest.setHeader('Connection','keep-alive');
        return oRequest;          
    }
    
    public List<ClaimsPaymentDetails_LDTO_HUM> processResponse(string response)
    {
        try
        {
            
            Endpoint_Url_HUM__c oSsoLogin = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL');
            Endpoint_Url_HUM__c oChk = Endpoint_Url_HUM__c.getInstance('CheckStatusToolLink');            
           
            ClaimsPaymentDetailResponse_LH_HUM objResp = new ClaimsPaymentDetailResponse_LH_HUM();
            List<ClaimsPaymentDetails_LDTO_HUM> listPaymentResp = new List<ClaimsPaymentDetails_LDTO_HUM>();
            objResp = (ClaimsPaymentDetailResponse_LH_HUM)System.Json.deserialize(response,ClaimsPaymentDetailResponse_LH_HUM.class);
            if(objResp.GetEhub835XmlResponse != Null && objResp.GetEhub835XmlResponse.GetEhub835XmlResult != Null && objResp.GetEhub835XmlResponse.GetEhub835XmlResult.Transaction_835  != Null)
            {
                List<ClaimsPaymentDetailResponse_LH_HUM.Transaction_835> lstPayments = objresp.GetEhub835XmlResponse.GetEhub835XmlResult.Transaction_835;

                for(ClaimsPaymentDetailResponse_LH_HUM.Transaction_835 obj :lstPayments)
                {
                    ClaimsPaymentDetails_LDTO_HUM objWrapper = new ClaimsPaymentDetails_LDTO_HUM();
                    objWrapper.sCheckNumber    =    obj.CheckNumber;
                    objWrapper.sPayeeName      =    obj.PayeeName;
                    objWrapper.sPayeeAddress   =    obj.PayeeAddress;
                    objWrapper.sCheckDate      =    obj.CheckDate;
                    objWrapper.sPaymentType    =    obj.PaymentType;
                    objWrapper.sPaymentAmount  =    obj.PaymentAmount;
                    objWrapper.sPayeeID        =    obj.PayeeID;
                    objWrapper.sPaymentIssueDate=   obj.PaymentIssueDate;
                    objWrapper.sPaymentStatus  =    obj.PaymentStatus;
                    objWrapper.sPaymentStatusDate = obj.PaymentStatusDate;
                    objWrapper.bPaymentInfoMsg = false;
                    objWrapper.sPaymentTypeID  =    obj.CheckNumber;                                         
                    
                    if(obj.PaymentType=='CHK')
                    {
                        objWrapper.sPaymentUrl = oSsoLogin.Remote_Url__c +'?TARGET='+ oChk.Remote_Url__c;  
                    }
                    if(obj.ClaimDetails != null)
                    {
                        List<ClaimsPaymentDetailResponse_LH_HUM.ClaimDetail> lstclaims = obj.ClaimDetails.ClaimDetail; 
                        List<ClaimsPaymentDetails_LDTO_HUM.ClaimDetail> listPaymentClaimsResp = new List<ClaimsPaymentDetails_LDTO_HUM.ClaimDetail>();                               
                        for(ClaimsPaymentDetailResponse_LH_HUM.ClaimDetail objClaim : lstclaims )
                        {
                            ClaimsPaymentDetails_LDTO_HUM.ClaimDetail cDetail = new ClaimsPaymentDetails_LDTO_HUM.ClaimDetail();
                            cDetail.ClaimNumber = objClaim.ClaimNumber;
                            cDetail.ClaimPaymentAmount = objClaim.ClaimPaymentAmount;
                            listPaymentClaimsResp.add(cDetail);
                        }
                           
                        if(listPaymentClaimsResp != null)
                        {
                            objWrapper.ClaimDetails = new ClaimsPaymentDetails_LDTO_HUM.ClaimDetails();
                            objWrapper.ClaimDetails.ClaimDetail = listPaymentClaimsResp;                        
                        }
                    }
                    listPaymentResp.add(objWrapper);
                                                       
                }
                return listPaymentResp;
            } 
            else
            {
                 return listPaymentResp;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetailsService_LS_HUM', 'processResponse');
            return Null;
        }
    }
}