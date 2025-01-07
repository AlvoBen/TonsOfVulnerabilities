/*******************************************************************************************************************************

Class Name : ClaimDetailsService_LC_HUM.cls

Modification Log: 
  Developer Name           Code Review                      Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------- 
* Apurva Urkude												04/18/2023				  US-4399890: Claims- Filter Functionality on Claims Detail Page 
* Suraj Patil												04/18/2023				  US-4399869: Claims- Add "Total of Last Processed Date" field/data 
* Suraj Patil												05/22/2023				  US-4600146: Remove references of Member_ID__c in Lightning C
* Apurva Urkude                                             06/15/2023	              US-4724857: Switch Cleanup Claims- Filter Functionality on Claims Detail Pag
* Sagar G      					                            25/07/2023	              DF_7974: QA_Lightning_US4858079_T1PRJ0865978_While clicking on this hyperlink "Access the Claim Inquiry Tool Link for Code Editing Rationale" it is navigating to the test region page instead of QA region page
* Sagar G      									            25/08/2023               US-5008473: T1PRJ0865978 - MF 4796385 - C03, Contact Servicing- Code Editing update
* Apurva Urkude                                             09/01/2023               US-4997879: F 4937947 - C03-Contact Servicing- Add hyperlink to Mentor for Claims "Modifier" field
* Kiran Bhuvanagiri                                         10/24/2023               US-5232110: Claim Detail Line item - EX Code issue
* Anuradha Gajbhe                                           02/29/2024               US-5289600: Claims Not Visible - INC2619433 and INC2621261
*********************************************************************************************************************************/
public with sharing class ClaimDetailsService_LC_HUM {

    Public Static List<ClaimsGetCodeEditDetails_LDTO_HUM> oCodeEditDTO{Get;Set;} 
    public static List<ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine> oclaimlineItems = new List<ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine>();
    public Static Boolean bPaymentZeroCheckNum {get; set;}
    public Static Boolean bPaymentNonZeroCheckNum {get; set;}
    public Static string sClaimNumber {get; set;}
    public Static string sPlatformCode {get; set;}
    Public static ClaimDetailsService_LS_HUM oClaimDetailsService = new ClaimDetailsService_LS_HUM();
    
    
    
    @TestVisible private Static ClaimPaymentDetails_S_HUM oclaimPayments = new ClaimPaymentDetails_S_HUM(); 
    public Static string sPaymentLastProcessDate{get; set;}
    public Static string sRemittenceID{get; set;}
    public Static string sPayeeCd{get; set;}
    public Static string AdjustInd {get; set;}
    public class CASLaunchpadRequestData 
    {
        @AuraEnabled public string ClientNumber = '';
        @AuraEnabled public string MemberId = '';
        @AuraEnabled public string FirstName = '';
        @AuraEnabled public string Relation = '';
        @AuraEnabled public string Environment = '';
    }

    @AuraEnabled(cacheable=true) 
    public static String getApplauncherEnvironment()
    {
        return HUMConstants__c.getInstance('APPLAUNCHER_ENVIRONMENT').StringValue__c;
    }
	
	 @AuraEnabled(cacheable=true) 
    public static String getCauseCodeCustomSettingDetails()
    {
        HUMCertificateURL__c objHssURL = HUMCertificateURL__c.getValues('HSSRedirectURL');
        HUMCertificateURL__c oCauseCodeMentorLinkURL = HUMCertificateURL__c.getValues('CauseCodeMentorLinkURL');
        if(objHssURL != null && oCauseCodeMentorLinkURL != null)
        return objHssURL.CertificateListURL__c + EncodingUtil.base64Encode(blob.valueof(oCauseCodeMentorLinkURL.CertificateListURL__c));

        return null;
    }
	
	@AuraEnabled(cacheable=true) 
    public static MemberPlan getAccountDetails(String MemberPlanID)
    {
        return [SELECT Id, MemberId FROM MemberPlan where id  =:MemberPlanID];
    }

    
	@AuraEnabled(cacheable=true) 
    public static String getClaimInquiryToolLink()
    {
        return Endpoint_Url_HUM__c.getInstance('ClaimInquiryToolLink').Remote_Url__c;
    }
    
	
    @AuraEnabled(cacheable=true) 
    public static CASLaunchpadRequestData getCASApplauncherData(string sMemberPlanId)
    {
        List<MemberPlan> lstMemberPlan = [SELECT Id, Member_Id_Base__c, Policy_Member__c, Relationship_Code__c, Policy__r.Client_Number__c, Member.FirstName FROM MemberPlan where id =: sMemberPlanId LIMIT 1 ];         
        List<LVRelationShipCodeToCAS__c> lstlvcas = new List<LVRelationShipCodeToCAS__c>();
        CASLaunchpadRequestData lstCASLaunchpadRequestData = new CASLaunchpadRequestData();
        if(lstMemberPlan.size() > 0)
        {
            lstlvcas = [SELECT Name, CASTranslation__c FROM LVRelationShipCodeToCAS__c  WHERE Name =: lstMemberPlan[0].Relationship_Code__c LIMIT 1];
            lstCASLaunchpadRequestData.ClientNumber = String.IsNotblank(lstMemberPlan[0].Policy__r.Client_Number__c) ? lstMemberPlan[0].Policy__r.Client_Number__c : '';
            lstCASLaunchpadRequestData.FirstName = String.IsNotblank(lstMemberPlan[0].Member.FirstName) ? lstMemberPlan[0].Member.FirstName : '';        
            list<MemberPlan> lstmemIdObj= ClaimDetails_LD_HUM_V2.getMemberPlanObjDetails(lstMemberPlan[0].id); 
            if(((lstmemIdObj!=null) && (!lstmemIdObj.isEmpty())) && lstmemIdObj[0].Name !=null) lstCASLaunchpadRequestData.MemberId  = lstmemIdObj[0].Member_Id_Base__c; 
        }        
        if(lstlvcas.size() > 0) lstCASLaunchpadRequestData.Relation = String.IsNotblank(lstlvcas[0].CASTranslation__c) ? lstlvcas[0].CASTranslation__c : '';
        lstCASLaunchpadRequestData.Environment = HUMConstants__c.getInstance('APPLAUNCHER_ENVIRONMENT').StringValue__c;
        return lstCASLaunchpadRequestData; 
    }
    
    @auraEnabled(continuation=true)
    public static Object claimDetailsRequest(String claimId) 
    {
       try
        {
            String sClaimGenKey = claimId;
            ClaimsServiceDetailsRequest_LH_HUM oClaimDetailsReq = new ClaimsServiceDetailsRequest_LH_HUM();
            oClaimDetailsReq = assignValuesToHelper(sClaimGenKey);
            if(oClaimDetailsReq != null)
            {
                HUM_Webservice_Callout__c oServiceCalloutClaimsDetail = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsDetail_V3');  
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oServiceCalloutClaimsDetail.Timeout__c)/1000;  
                string sJsonRequest = JSON.Serialize(oClaimDetailsReq);
                HttpRequest oRequest = setHttpRequest(oServiceCalloutClaimsDetail,sJsonRequest);
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod = 'processResponse';
                con.addHttpRequest(oRequest);
                return con;
            }
            else 
            {
               return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'claimDetailsRequest');
            return null;
        } 
    }
    
    @AuraEnabled
    public static ClaimsServiceDetailsRequest_LH_HUM assignValuesToHelper(String sClaimGenKey)
      {
          ClaimsServiceDetailsRequest_LH_HUM oClaimDetailsReq = new ClaimsServiceDetailsRequest_LH_HUM();
          ClaimsServiceDetailsRequest_LH_HUM.GetClaimDetail oDetailsRequest = new ClaimsServiceDetailsRequest_LH_HUM.GetClaimDetail();
          if(String.isNotBlank(sClaimGenKey))
          {
              oDetailsRequest.Id = sClaimGenKey;
              oClaimDetailsReq.GetClaimDetail = oDetailsRequest;
              return oClaimDetailsReq;
          }
          else
          {
              return null;
          }
      }
      
    public static HttpRequest setHttpRequest(HUM_Webservice_Callout__c oSvcClaimDetail, String sRequestBody)
     {
          HttpRequest oRequest = new HttpRequest();
           oRequest.setMethod(oSvcClaimDetail.Request_Type__c);
            oRequest.setBody(sRequestBody);
            oRequest.setEndpoint(oSvcClaimDetail.End_Point_URL__c);
            if(!Test.isRunningTest())    
            {
                oRequest.setClientCertificateName(oSvcClaimDetail.Certificate_Name__c);
            }
            oRequest.setTimeout(Integer.valueof(oSvcClaimDetail.Timeout__c));
            oRequest.setHeader('content-type', oSvcClaimDetail.Content_Type__c);
            oRequest.setHeader('SOAPAction', '');    
            oRequest.setHeader('Connection','keep-alive');
            return oRequest;
    }
    
      /*
     * Method Name   :  processResponse
     * Description   :  This is the Callback method for Continous Object to process the Response data
     * Return Type   :  Object
     * Parameters    :  NA
     */ 
     @AuraEnabled
    public static Object processResponse(List<string> labels, Object State) 
     {  
        ClaimDetails_LWC_DTO_HUM_V2 lstClaimlineItems = new ClaimDetails_LWC_DTO_HUM_V2(); 
        try
        {
          HttpResponse response = Continuation.getResponse(labels[0]);
          if(response != null)
          lstClaimlineItems = responseDeserialize(Response);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'processResponse');
        }
        return lstClaimlineItems;  
    }
    
    /*
    * Method Name   :  responseDeserialize
    * Description   :  This method is used dederialize the response
    * Return Type   :  ClaimsServiceDetailsResponse_LH_HUM 
    * Parameters    :  HttpResponse 
    */
    public static ClaimDetails_LWC_DTO_HUM_V2 responseDeserialize(HttpResponse sResponse)
    {
        ClaimDetails_LWC_DTO_HUM_V2 lstClaimlineItems = new ClaimDetails_LWC_DTO_HUM_V2();
          
       try 
       {   
          Integer statusCode = sResponse.getStatusCode();
          if (statusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || statusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
          {
              ClaimsServiceDetailsResponse_LH_HUM oResponse = new ClaimsServiceDetailsResponse_LH_HUM();
              oResponse = (ClaimsServiceDetailsResponse_LH_HUM)JSON.deserialize(sResponse.getBody(),ClaimsServiceDetailsResponse_LH_HUM.class); 
              if(oResponse.ClaimDetailReponse != null)
              {
                  ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailReponse oClaimDetailsResponse = oResponse.ClaimDetailReponse;
                  ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailResult oClaimDetailsResult = oClaimDetailsResponse.ClaimDetailResult;
                  lstClaimlineItems = responseParse(oClaimDetailsResult);
                  
             }
         }
         
       }
       catch(Exception ex)
        {
           HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'responseDeserialize');
           
        }  
         return lstClaimlineItems;
    } 
    
    /*
    * Method Name   :  responseParse
    * Description   :  This method is used to parse the response
    * Return Type   :  N/A
    * Parameters    :  ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailResult 
    */
  public static ClaimDetails_LWC_DTO_HUM_V2 responseParse(ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailResult oClaimDetailsResult)
    {
        string sClaimLineItemsjson ;
        ClaimDetails_LWC_DTO_HUM_V2 lstClaimDetail = new ClaimDetails_LWC_DTO_HUM_V2();
        List<ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine> lstClaimlineItems;
        if(oClaimDetailsResult != null)
        {  
             lstClaimDetail = claimDetails(oClaimDetailsResult); 
             lstClaimlineItems = ClaimLineItems(oClaimDetailsResult);
             lstClaimDetail.ClaimDetailLines = lstClaimlineItems;
             lstClaimDetail.ClaimDetailLineTotal = LineItemTotal(oClaimDetailsResult);
        }
        return lstClaimDetail ;
    }

    public static ClaimDetails_LWC_DTO_HUM_V2 claimDetails(ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailResult oClaimDetailResult)
    {
        try
        {
            if(oClaimDetailResult != null)
            {
                List<String> listPSpeciality = new List<String>();
                ClaimsServiceDetailsResponse_LH_HUM.DiagnosisCode oDiagCode = oClaimDetailResult.DiagnosisCode; 
                ClaimDetails_LWC_DTO_HUM_V2 oClaimDetailsDTO = new ClaimDetails_LWC_DTO_HUM_V2();
                oClaimDetailsDTO.bShowMore = false;
                oClaimDetailsDTO.sClaimNbr = oClaimDetailResult.ClaimNumber;
                
                    List<ClaimsServiceDetailsResponse_LH_HUM.ProviderSpecialties> lstProviderSpec = oClaimDetailResult.ProviderSpecialties; 
                    ClaimsServiceDetailsResponse_LH_HUM.ProviderSpecialties obj = new ClaimsServiceDetailsResponse_LH_HUM.ProviderSpecialties();
                    lstProviderSpec.add(obj);
                    if(lstProviderSpec != null && !lstProviderSpec.isEmpty() )
                    {
                        string sProvSpecRecs;
                        for(integer i=0; i<lstProviderSpec.size(); i++)    
                        {
                            if(i == 0)
                                sProvSpecRecs = lstProviderSpec[i].Description;
                            else
                                sProvSpecRecs = sProvSpecRecs+', '+lstProviderSpec[i].Description;
                            listPSpeciality.add(lstProviderSpec[i].Description);
                        }
                        oClaimDetailsDTO.listPrimarySpeciality = JSON.serialize(listPSpeciality);
                        oClaimDetailsDTO.sProviderSpecAllRecords = sProvSpecRecs;
                        oClaimDetailsDTO.sProviderSpecFirstRecord = lstProviderSpec[0].Description;
 
                        if( lstProviderSpec.size()>1 )
                        {
                           oClaimDetailsDTO.bShowMore = true;
                        }
                     }
                
                oClaimDetailsDTO.sClmMediaCd = oClaimDetailResult.ClaimMediaCode;
                oClaimDetailsDTO.sPatRecordNbr =  oClaimDetailResult.PatientRecordNumber;
                oClaimDetailsDTO.sSrcAuthKey = oClaimDetailResult.SourceAuthKeyId ;
                oClaimDetailsDTO.sBankCode  =oClaimDetailResult.BankAccountCode;
                If (oClaimDetailsDTO.sSrcAuthKey == '0' || oClaimDetailsDTO.sSrcAuthKey == '' || oClaimDetailsDTO.sSrcAuthKey == '00000000000' || oClaimDetailsDTO.sSrcAuthKey == '000000000')
                    oClaimDetailsDTO.bAuthKeyFlag = false;
                else
                    oClaimDetailsDTO.bAuthKeyFlag = true;
                
                oClaimDetailsDTO.sDeficiencyInd = oClaimDetailResult.DeficiencyIndicator;
                oClaimDetailsDTO.sDeficiencyField = oClaimDetailResult.DeficiencyField;
                oClaimDetailsDTO.sDeficiencyDate = oClaimDetailResult.DeficiencyDate;
                oClaimDetailsDTO.sDeficiencyDesc = oClaimDetailResult.DeficiencyDesc;
                oClaimDetailsDTO.sDiagIndicator = oClaimDetailResult.DiagnosisIndicator;
                oClaimDetailsDTO.sSrcLvCASPrefix = oClaimDetailResult.SourceLvCASPrefixCode;
                oClaimDetailsDTO.sPaymentMethod = oClaimDetailResult.PaymentMethod;
                oClaimDetailsDTO.sTin = oClaimDetailResult.ProviderTaxId;
                oClaimDetailsDTO.sNpi = oClaimDetailResult.SourceNationalProviderId; 
                oClaimDetailsDTO.lstDiagnosisCodes = new List<string>();
                oClaimDetailsDTO.lstDiagnosisCodes = oDiagCode.Code;
                
                if(!oClaimDetailsDTO.lstDiagnosisCodes.isEmpty() )
                    oClaimDetailsDTO.sFirstDiagnosisCode = oClaimDetailsDTO.lstDiagnosisCodes[0];
                return oClaimDetailsDTO;
             }
         }
           catch(Exception ex)
            {
               HUMExceptionHelper.logErrors(ex, 'ClaimDetailsService_LS_HUM', 'ClaimLineItems');
            }
            return null;
    }

    public static List<ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine> claimLineItems(ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailResult oClaimDetailResult) 
    {
        try
        {
            if(oClaimDetailResult != null)
            {
                List<ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine> lstClaimDetailLineItem = new List<ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine>();
                ClaimsServiceDetailsResponse_LH_HUM.DiagnosisCode oDiagCodeService = oClaimDetailResult.DiagnosisCode;
                ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailLines oClaimDetailLines = oClaimDetailResult.ClaimDetailLines; 
                List<ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailLine> lstClaimDetail = oClaimDetailLines.ClaimDetailLine;
                List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> lstCodes = new List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>();
                for(ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailLine oClaimDetail : lstClaimDetail)
                {
                   ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine oClaimDetails = new ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine();
                   oClaimDetails  = getClaimLineItemDetails(oClaimDetail);
                   
                   oClaimDetails.sFeeSchdId = oClaimDetail.FeeScheduleId;
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
                   ClaimDetails_LWC_DTO_HUM_V2.DiagCode oDiagCode = new ClaimDetails_LWC_DTO_HUM_V2.DiagCode();
                   oDiagCode.Code = new List<string>();
                   oDiagCode.Code = oDiagCodeService.Code;
                   oClaimDetails.DiagCode = oDiagCode;
                   oClaimDetails.PaymentCodes =  getClaimPaymentCodes(oClaimDetail);      
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
    
   public static ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine getClaimLineItemDetails(ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailLine  oClaimDetail)
    {
        
        ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine oClaimDetails = new ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine();  
        oClaimDetails.ID = oClaimDetail.LineNumber;  
        oClaimDetails.hideBool = true;            
        oClaimDetails.sLastProcessDate = oClaimDetail.LastProcessDate;
        oClaimDetails.sSrcClaimLineSequence = oClaimDetail.SourceClaimLineSequence;
        oClaimDetails.sServiceStartDate = oClaimDetail.ServiceStartDate;
        oClaimDetails.sServiceEndDate = oClaimDetail.ServiceEndDate;
        oClaimDetails.sServiceCode = oClaimDetail.ServiceCode;
		if(string.isNotEmpty(oClaimDetails.sServiceCode) && oClaimDetails.sServiceCode.contains('*')) 
        	{ 
            	oClaimDetails.sServiceCode = oClaimDetails.sServiceCode.removeEnd('*');
        	}
		oClaimDetails.sServiceCodeLink = redirectMentorDocument('',oClaimDetails.sServiceCode,'');
        oClaimDetails.sServiceDesc = oClaimDetail.ServiceDescription;
        oClaimDetails.sPaidStatDesc = oClaimDetail.PaidStatus;
        oClaimDetails.sChargeAmt = '$' + oClaimDetail.ChargeAmount;
        oClaimDetails.sBenAllowAmt = '$' + oClaimDetail.BenefitAllowedAmount;
        oClaimDetails.sProvWriteOff = '$' + oClaimDetail.ProviderWriteOff;
        oClaimDetails.sExcludeAmt = '$' + oClaimDetail.ExcludeAmount;
        oClaimDetails.sBenDenyAmt = '$' + oClaimDetail.BenefitDeniedAmount;
        oClaimDetails.sCopayAmt = '$' + oClaimDetail.CopaymentAmount;
        oClaimDetails.sDeductAmt = '$' + oClaimDetail.DeductibleAmount;
        oClaimDetails.sCoInsAmt = '$' + oClaimDetail.CoinsuranceAmount;
        oClaimDetails.sCobPaidAmt = '$' + oClaimDetail.COBPaidAmount;
        oClaimDetails.sMbrRespAmt = '$' + oClaimDetail.MemberResponsibilityAmount;
        oClaimDetails.sTotalInterest = '$' + '0.00';
        oClaimDetails.sPaidAmt = '$' + oClaimDetail.PaidAmount;
        oClaimDetails.sExLineItem = fetchExcodeLineItem(oClaimDetail.PaymentCodes);
        oClaimDetails.sRevenuecode = oClaimDetail.Revenuecode;
        oClaimDetails.sCPTModCode = oClaimDetail.CPTModifierCode;
        oClaimDetails.sAnesMinutes = oClaimDetail.AnesthesiaMinuteNumber;
        oClaimDetails.sServUnitCnt = oClaimDetail.ServiceUnitCount;
        oClaimDetails.sCauseCd = oClaimDetail.CauseOfIllnessCode;
        oClaimDetails.sSrcPOTCode = oClaimDetail.PlaceofTreatmentCode;
        oClaimDetails.sPOTLongDesc = oClaimDetail.PlaceofTreatmentDescription;
        return oClaimDetails;
    }

    /*
     * Returns a String
     * <p>
     * This method is used to return the total of line item
     *
     * @param  Object
     * @return String
     */
    public static ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLineTotal LineItemTotal(ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailResult oClaimDetailResult)
    {
        ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLineTotal objTotalLineItem = new ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLineTotal(); 
            objTotalLineItem.sChargeAmt = '$' + oClaimDetailResult.ChargeAmount;
            objTotalLineItem.sBenAllowAmt = '$' + oClaimDetailResult.BenefitAllowedAmount;
            objTotalLineItem.sProvWriteOff = '$' + oClaimDetailResult.ProviderWriteOff;
            objTotalLineItem.sExcludeAmt = '$' + oClaimDetailResult.ExcludeAmount;
            objTotalLineItem.sBenDenyAmt = '$' + oClaimDetailResult.BenefitDeniedAmount;
            objTotalLineItem.sCobPaidAmt = '$' + oClaimDetailResult.COBPaidAmount;
            objTotalLineItem.sMbrRespAmt = '$' + oClaimDetailResult.MemberResponsibilityAmount;
            objTotalLineItem.sPaidAmt = '$' + oClaimDetailResult.PaidAmount;
            objTotalLineItem.sCopayAmt = '';
            objTotalLineItem.sDeductAmt = '';
            objTotalLineItem.sCoInsAmt = '';
        return objTotalLineItem;
    }

    /*
     * Returns a String
     * <p>
     * This method is used to return the first Legacy code for every line item
     *
     * @param  Object
     * @return String
     */
    public static String fetchExcodeLineItem(ClaimsServiceDetailsResponse_LH_HUM.PaymentCodes Excode)
    {
       if(Excode!=null)
       {
         List<ClaimsServiceDetailsResponse_LH_HUM.PaymentDetail> lstPaymentCodes = Excode.PaymentDetail;
         String sFirstExCode;
         if(lstPaymentCodes!=null && !lstPaymentCodes.isEmpty())
         {
             if(lstPaymentCodes[0].LegacyCode.contains('<')) 
             {
                //lstPaymentCodes[0].LegacyCode=lstPaymentCodes[0].LegacyCode.replaceAll('<','&lt;'); - INC2595218 
                 sFirstExCode = lstPaymentCodes[0].LegacyCode;
             }
             else
             {
                 sFirstExCode = lstPaymentCodes[0].LegacyCode;
             }
         }
         return sFirstExCode;
       }
       else
        return null;
    }
	
    @AuraEnabled public static ClaimDetails_LWC_DTO_HUM_V2.PaymentCodes getClaimPaymentCodes(ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailLine oClaimDetail)
    {
       ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine oClaimDetails = new ClaimDetails_LWC_DTO_HUM_V2.ClaimDetailLine();                
       ClaimsServiceDetailsResponse_LH_HUM.PaymentCodes oPaymntCodes = oClaimDetail.PaymentCodes;
       List<ClaimsServiceDetailsResponse_LH_HUM.PaymentDetail> lstPaymentCodes = oPaymntCodes.PaymentDetail;
       List<ClaimsServiceDetailsResponse_LH_HUM.ProviderDetails> lstProviderDetails = oClaimDetail.ProviderDetails;
       List<ClaimsServiceDetailsResponse_LH_HUM.PaymentDetail> lstPaymentCodesClone = lstPaymentCodes.clone();
       List<ClaimsServiceDetailsResponse_LH_HUM.ProviderDetails> lstProviderDetailsClone = lstProviderDetails.clone();
       List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> lstPayCodes = new List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>();
       if(lstProviderDetails!=null && !lstProviderDetails.isEmpty() && lstPaymentCodes!=null && !lstPaymentCodes.isEmpty())
       {
           Integer iPaymentLstIndex = lstPaymentCodes.size()-1;
           for(integer iPayCode=lstPaymentCodes.size()-1; iPayCode>=0; iPayCode--)
           {
               Integer iProviderLstIndex = lstProviderDetails.size()-1;
               for(integer iProvCode=lstProviderDetails.size()-1; iProvCode>=0; iProvCode--)
               {
                   if(lstPaymentCodes[iPayCode].LegacyCode == lstProviderDetails[iProvCode].ProviderReasonCode) 
                   {
                       ClaimDetails_LWC_DTO_HUM_V2.PaymentCode oLineItemPaymentCode =  new ClaimDetails_LWC_DTO_HUM_V2.PaymentCode();
                       if(lstPaymentCodes[iPayCode].LegacyCode.contains('<'))
                       {
                           //lstPaymentCodes[iPayCode].LegacyCode=lstPaymentCodes[iPayCode].LegacyCode.replaceAll('<',' &lt;'); - INC2595218 
                           oLineItemPaymentCode.sLegacyCd=lstPaymentCodes[iPayCode].LegacyCode;
                       }
                       else
                       {
                           oLineItemPaymentCode.sLegacyCd =lstPaymentCodes[iPayCode].LegacyCode;
                       }
                       oLineItemPaymentCode.sLegacyMessage = lstPaymentCodes[iPayCode].LegacyMessage;
                       oLineItemPaymentCode.sEORDescription = lstProviderDetails[iProvCode].ProviderReasonMessage;
                       lstPayCodes.add(oLineItemPaymentCode);
                       lstProviderDetailsClone.remove(iProviderLstIndex);
                       lstPaymentCodesClone.remove(iPaymentLstIndex);
                   }
                   iProviderLstIndex--;
               }
               iPaymentLstIndex--;  
           } 
           
           if(lstPaymentCodesClone != null && !lstPaymentCodesClone.isEmpty())
           {
               lstPayCodes.addAll( getPayCodes(lstPaymentCodesClone));
           }
           
           if(lstProviderDetailsClone != null && !lstProviderDetailsClone.isEmpty())
           {
              lstPayCodes.addAll( getProviderCodes(lstProviderDetailsClone));
           }
       }
       if(lstProviderDetails.isempty())
       {
           lstPayCodes.addAll( getPayCodes(lstPaymentCodes));
       }
       if(lstPaymentCodes.isempty())
       {
           lstPayCodes.addAll( getProviderCodes(lstProviderDetails));
       }
       ClaimDetails_LWC_DTO_HUM_V2.PaymentCodes oClaimPayment = new ClaimDetails_LWC_DTO_HUM_V2.PaymentCodes();
       oClaimPayment.PaymentCode = lstPayCodes;
       return oClaimPayment ;
    }
   
   /*
     * Returns a Object
     * <p>
     * This method is used to fetch the values of Payment Codes for claim line items when providerDetails are received as blank from service
     *
     * @param  List<ClaimsServiceDetailsResponse_LH_HUM.PaymentDetail>
     * @return List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>
     */ 
   @AuraEnabled public static List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> getPayCodes( List<ClaimsServiceDetailsResponse_LH_HUM.PaymentDetail> lstPaymentCodes)
    {
        List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> lstPaymtCodes = new List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>();
        for(ClaimsServiceDetailsResponse_LH_HUM.PaymentDetail oPayCode : lstPaymentCodes)
           {
                ClaimDetails_LWC_DTO_HUM_V2.PaymentCode oLineItemPaymentCode =  new ClaimDetails_LWC_DTO_HUM_V2.PaymentCode();
                if(oPayCode.LegacyCode.contains('<'))
                {
                    //oPayCode.LegacyCode=oPayCode.LegacyCode.replaceAll('<','&lt;'); - INC2595218 
                    oLineItemPaymentCode.sLegacyCd=oPayCode.LegacyCode;
                }
                else
                {
                    oLineItemPaymentCode.sLegacyCd =oPayCode.LegacyCode;
                }
                oLineItemPaymentCode.sLegacyMessage = oPayCode.LegacyMessage;
                lstPaymtCodes.add(oLineItemPaymentCode);
            } 
           return lstPaymtCodes;      
    }
    
     /*
     * Returns a Object
     * <p>
     * This method is used to fetch the values of Provider Codes for claim line items when payment codes are received as blank from service
     *
     * @param  List<ClaimsServiceDetailsResponse_LH_HUM.ProviderDetails>
     * @return List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>
     */ 
   @AuraEnabled public static List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> getProviderCodes( List<ClaimsServiceDetailsResponse_LH_HUM.ProviderDetails> lstProviderDetails)
    {
         List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> lstProviderCodes = new List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>();
         for(ClaimsServiceDetailsResponse_LH_HUM.ProviderDetails oProviderCode : lstProviderDetails)
               {
                   ClaimDetails_LWC_DTO_HUM_V2.PaymentCode oLineItemPaymentCode =  new ClaimDetails_LWC_DTO_HUM_V2.PaymentCode();
                   if(oProviderCode.ProviderReasonCode.contains('<'))
                   {
                       //oProviderCode.ProviderReasonCode = oProviderCode.ProviderReasonCode.replaceAll('<','&lt;'); - INC2595218 
                       oLineItemPaymentCode.sLegacyCd=oProviderCode.ProviderReasonCode;
                   }
                   else
                   {
                       oLineItemPaymentCode.sLegacyCd =oProviderCode.ProviderReasonCode;
                   }
                   oLineItemPaymentCode.sEORDescription = oProviderCode.ProviderReasonMessage;
                   lstProviderCodes.add(oLineItemPaymentCode);
               }
           return lstProviderCodes;      
    }
    
    @AuraEnabled(continuation=true)
    public static Object getRationale(List<String> lstDiagnosisCd,string strPaymentCode, String sPlatformCode, String sDiagIndicator, String sPaidStatDesc, String sCPTModCode, String sServiceCode, String sClmNbr) {
        List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> lstPaymentCodes= (List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>) JSON.deserialize(strPaymentCode, List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>.class);
        ClaimCodeEditDetailRequest_LH_HUM oCodeEditReq = new ClaimCodeEditDetailRequest_LH_HUM(); 
        oCodeEditReq  = CodeEditRationale(lstDiagnosisCd, lstPaymentCodes, sPlatformCode, sDiagIndicator, sPaidStatDesc, sCPTModCode, sServiceCode);      
        if(oCodeEditReq != null)
          {
                HUM_Webservice_Callout__c oServiceCalloutClaimsCodeEdit = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsCodeEdit_V3');  
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oServiceCalloutClaimsCodeEdit.Timeout__c)/1000; 
                string sJsonRequestCodeEdit = JSON.Serialize(oCodeEditReq );
                HttpRequest oRequest = new HttpRequest();
                oRequest = setHttpRequestCodeEdit(oRequest,oServiceCalloutClaimsCodeEdit,sJsonRequestCodeEdit);
                ClaimsGetCodeEditDetails_LDTO_HUM oCodeEditState = new ClaimsGetCodeEditDetails_LDTO_HUM();
                oCodeEditState.sClaimNbr = sClmNbr;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                oCodeEditState.sContinuationState = con.addHttpRequest(oRequest);
                con.state = oCodeEditState;
                con.continuationMethod = 'processResponseCodeEdit';
                return con;
          }
          else 
          {
             return null;
          } 
       
    }
    
    @AuraEnabled
    public static ClaimCodeEditDetailRequest_LH_HUM codeEditRationale(List<string> lstDiagnosisCd,List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> lstPaymentCode, String sPlatformCode, String sDiagIndicator, String sPaidStatDesc, String sCPTModCode, String sServiceCode) 
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
    * @param  List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>
    * @return ClaimCodeEditDetailRequest_LH_HUM.ExCodes
    */
    public static ClaimCodeEditDetailRequest_LH_HUM.ExCodes codeEditExCodes(List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> lstPaymentCode)
    {
       ClaimCodeEditDetailRequest_LH_HUM.ExCodes oExCodes = new ClaimCodeEditDetailRequest_LH_HUM.ExCodes();
       List<String> lstStringExCodes = new List<String>();
       string sLegacyCode;
       for(ClaimDetails_LWC_DTO_HUM_V2.PaymentCode oPaymentCode : lstPaymentCode)
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
    * @param  String, List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>, String, String
    * @return ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureCodes
    */
    public static ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureCodes codeEditRejectedProcedureCode(String sPaidStatDesc,List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> lstPaymentCode,String sPlatformCode, String sServiceCode)
    {
       ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureCodes oRejectProcedure = new ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureCodes();
       List<ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureDTO> lstRejectDTO = new List<ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureDTO>();
       ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureDTO oRejectDTO = new ClaimCodeEditDetailRequest_LH_HUM.RejectedProcedureDTO(); 
       if(sPaidStatDesc == 'DENIED')
       {
          for(ClaimDetails_LWC_DTO_HUM_V2.PaymentCode oPaymentCode : lstPaymentCode)
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
    public static ClaimCodeEditDetailRequest_LH_HUM.PaidProcedureCodes codeEditPaidProcedureCd(String sServiceCode, String sPaidStatDesc)
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
    public static ClaimCodeEditDetailRequest_LH_HUM.PaidModifers codeEditPaidModifier(String sCPTModCode) 
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
    public static ClaimCodeEditDetailRequest_LH_HUM.DiagnosisCodes codeEditDiagnosisCodes(List<string> lstDiagnosisCd, String sDiagIndicator)
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
    
    public static HttpRequest setHttpRequestCodeEdit(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcClaimCodeEdit, String sRequestBody)
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
    
     @AuraEnabled
    public static Object processResponseCodeEdit(List<String> labels, Object State) 
     { try { 
             
            ClaimsGetCodeEditDetails_LDTO_HUM  oCodeEditState =  (ClaimsGetCodeEditDetails_LDTO_HUM)state;
            HttpResponse oResponseCodeEdit = Continuation.getResponse(labels[0]);
            return responseDeserializeCodeEdit(oResponseCodeEdit,oCodeEditState);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'processResponseCodeEdit');
        }
        return null;
    }
     
    /*
    * Method Name   :  responseDeserializeCodeEdit
    * Description   :  This method is used dederialize the response
    * Return Type   :  ClaimsGetCodeEditDetails_LDTO_HUM 
    * Parameters    :  HttpResponse 
    */
    public static List<ClaimsGetCodeEditDetails_LDTO_HUM> responseDeserializeCodeEdit(HttpResponse sResponse,ClaimsGetCodeEditDetails_LDTO_HUM  oCodeEditState)
    {
       try 
       {   
          List<ClaimsGetCodeEditDetails_LDTO_HUM> oCodeEditDTO = new List<ClaimsGetCodeEditDetails_LDTO_HUM>() ;
          Integer statusCode = sResponse.getStatusCode();
          if (statusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || statusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
          {
              ClaimsServiceCodeEditDtlResponse_LH_HUM oResponseCodeEdit = new ClaimsServiceCodeEditDtlResponse_LH_HUM ();
             oResponseCodeEdit = (ClaimsServiceCodeEditDtlResponse_LH_HUM )JSON.deserialize(sResponse.getBody(),ClaimsServiceCodeEditDtlResponse_LH_HUM .class); 
              if(oResponseCodeEdit.GetCodeEditDetailsResponse != null)
              {
                 ClaimsServiceCodeEditDtlResponse_LH_HUM.GetCodeEditDetailsResponse oClaimsCodeEditResponse = oResponseCodeEdit.GetCodeEditDetailsResponse;
                 oCodeEditDTO = CodeEditDetailsResponse(oClaimsCodeEditResponse );
              }
             return oCodeEditDTO;
         }
      } 
      catch(Exception ex)
        {
           HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'responseDeserializeCodeEdit');
           
        }  
        return null;
    } 
    
    public static List<ClaimsGetCodeEditDetails_LDTO_HUM> codeEditDetailsResponse(ClaimsServiceCodeEditDtlResponse_LH_HUM.GetCodeEditDetailsResponse oClaimsCodeEditResponse)
    {
         ClaimsServiceCodeEditDtlResponse_LH_HUM.RationaleDTO oRationaleDTO =  oClaimsCodeEditResponse.RationaleDTO;
         ClaimsServiceCodeEditDtlResponse_LH_HUM.RationaleDescriptionCodes oRationaleDescCode = oRationaleDTO.RationaleDescriptionCodes;
         List<ClaimsServiceCodeEditDtlResponse_LH_HUM.RationaleDescriptionDTO> lstClaimDetail = oRationaleDescCode.RationaleDescriptionDTO;
         List<ClaimsGetCodeEditDetails_LDTO_HUM> lstCodeEditDTO = new List<ClaimsGetCodeEditDetails_LDTO_HUM>();
         for(ClaimsServiceCodeEditDtlResponse_LH_HUM.RationaleDescriptionDTO oClaimDetail : lstClaimDetail)
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
    * Method Name   :  initiatePaymentRequest
    * Description   :  This method is used to hit the Claims Payment service(835).
    * Return Type   :  Object
    * Parameters    :  NA
    */
    @auraEnabled(continuation=true)
    public Static Object initiatePaymentRequest(string AdjInd, string sPaymentCheckNumber, String ClaimNumber, string PlatformCode) 
    {
        try
        {
            ClaimsPaymentDetailsRequest_LH_HUM oPaymentsReq = new ClaimsPaymentDetailsRequest_LH_HUM();
            oPaymentsReq.GetPaymentDetails = new ClaimsPaymentDetailsRequest_LH_HUM.GetPaymentDetails();
            oPaymentsReq.GetPaymentDetails.SearchType = GLOBAL_CONSTANT_HUM.POLICYMEMBER_CLAIMPAYMENT_SEARCHTYPE_HUM;
            oPaymentsReq.GetPaymentDetails.ClaimNbr = ClaimNumber;
            oPaymentsReq.GetPaymentDetails.PlatformCd = PlatformCode;
            if(AdjInd == 'Y')
            {
                oPaymentsReq.GetPaymentDetails.CheckEFTNumber = sPaymentCheckNumber; 
                oPaymentsReq.GetPaymentDetails.PaymentMethod = GLOBAL_CONSTANT_HUM.POLICYMEMBER_CLAIMPAYMENT_METHOD_HUM;
            }

            if(oPaymentsReq != null)
            {
                HUM_Webservice_Callout__c oSvcHeaderClaimPayments = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsPayment_V3'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimPayments.Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                con.continuationMethod = 'processPay';
                string sJsonRequest = JSON.Serialize(oPaymentsReq);
                HttpRequest oRequest = new HttpRequest();
                oRequest = oclaimPayments.setupRequest(oRequest,oSvcHeaderClaimPayments,sjsonRequest);
                string sPaymentRequest = con.addHttpRequest(oRequest);
                return con;            
            }
            else
            {
                return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'hitPaymentServiceMethod');
            return Null;
        } 
    }

    /*
    * Method Name   :  processPay
    * Description   :  This method is used to process the response from Claims Payments service.
    * Return Type   :  void 
    * Parameters    :  HttpResponse 
    */
    @auraEnabled(continuation=true)
    public Static string processPay(List<string> labels, Object State)
    {
        try
        {
			String PaymentResponse;
            HttpResponse response = Continuation.getResponse(labels[0]);
            List<ClaimsPaymentDetails_LDTO_HUM> lstPaymentsResp = new List<ClaimsPaymentDetails_LDTO_HUM>();
            if (response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
            {
                lstPaymentsResp = oClaimDetailsService.processResponse(response.getBody());
				PaymentResponse = JSON.Serialize(lstPaymentsResp);
            }
			else
            {
                PaymentResponse = Label.ClaimsSummary_Service_Error;
            }
            return PaymentResponse;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'processPay');
            return null;
        }
        
    }
	
	        /**
    * redirect to mentor document links
    * @return PageReference
    */
   
    @AuraEnabled(cacheable=true)
    public static string redirectMentorDocument(String strLinkName,String sMentorCode,String sFilterCodeSet) {
        String strURL = '';
     	try {
		    	HUMCertificateURL__c objHssURL = HUMCertificateURL__c.getValues('HSSRedirectURL');
            	Boolean bDownloadDocument;
            	String strDocURL;
            
		    	
                if(String.isNotBlank(strLinkName))
                {
                    
                    CustomLink__c mentorLink = [Select Download_MentorLink__c,URL__c from CustomLink__c where Id = : strLinkName];
                    bDownloadDocument = mentorLink.Download_MentorLink__c;
                    strDocURL = mentorLink.URL__c;
                    
                    if(objHssURL != null && null != objHssURL.CertificateListURL__c && String.isNotBlank(strDocURL)) {
                        
                        strURL = objHssURL.CertificateListURL__c + EncodingUtil.base64Encode(blob.valueof(strDocURL));
                        if(bDownloadDocument) {
                            strURL = strDocURL;
                        }          
                    }
                }
		    		
		    		if(String.isNotBlank(sMentorCode))
		    		{
		    			HUMCertificateURL__c objMenURL = new HUMCertificateURL__c();
				    	objMenURL = HUMCertificateURL__c.getValues('MentorRedirectURL'); 				    		
				    	
				    	String sPlainurl =  objMenURL.CertificateListURL__c+HUMConstants.sProcFilterType+sMentorCode;
				    	
				    	if (String.isNotBlank(sFilterCodeSet)) {
				    		sPlainurl += '&filters={"CodeSet":"'+ sFilterCodeSet +'"}';
				    	}
				    	
				    	 strURL = objHssURL.CertificateListURL__c + EncodingUtil.base64Encode(blob.valueof(sPlainurl.escapeHtml4()));
 
	    			}
            
        } catch(Exception e) {
              HUMExceptionHelper.logErrors(e, 'ClaimDetails_C_HUM_V3', 'redirectMentorDocument');
        }
        return strURL;
        
    }

    
}