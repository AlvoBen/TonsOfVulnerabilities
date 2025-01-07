/*****************************************************************************************
Apex class Name    : ClaimDetails_S_HUM_V2 
Version            : 1.0
Function           : Service class for Claim Details
                     This is a Helper class for ClaimDetails_C_HUM. It forms request and parse response.
Created Date       : 07 March 2018
Test Class         : ClaimDetailsService_T_HUM_V2

Modification Log:
 *   Developer                   Code Review             Date               Description
 * --------------------------------------------------------------------------------------------------------------------------------------------  
 *   Prasanthi Kandula             25441               04/07/2016         Original Version
 *   Prasanthi Kandula             26076               04/26/2016         Added LineItemsFooter method to read Total Claim Processing Details
 *   Muralidhar Kollu              26844               05/04/2016         assigning CheckNbr received from service to DTO required for Claim Payments in ClaimLineItems()
 *   Kritika P Popat               26844               05/04/2016         Assign Remit Number from service in ClaimLineItems()
 *   Prasanthi Kandula             27974               05/19/2016         REQ-24426, REQ-268861 To fetch CodeEdit Details from getCodeEditDetails service
 *   Prasanthi Kandula             28233               05/23/2016         REQ - 269803 - Remove Line Number field from Line Items datatable
 *   Prasanthi Kandula             28233               05/24/2016         Defect 226352.0001 Remove LineNbr from Line Items section 
 *   Kritika Popat                 28325               05/25/2016         Defect 227495.0001 Added code for Explanation description that was overidden. 
 *   Prasanthi Kandula             28390               06/07/2016         Defect# 228315.0001 fix Added getSequenceNumberMTV() method to return Sequence Number for every Last Processed Date
 *   Santhi Mandava                29879               06/11/2016         Implemented standard map for claim logging
 *   Prasanthi Kandula             30481               06/17/2016         Defect #229650.0001 fix MTV remarks not displaying for dental Claims
 *   Muralidhar Kollu              30622               06/20/2016         REQ - 270092 - update payment details service input  
 *   Prasanthi Kandula             31877               07/04/2016         REQ - 270129 Updated getSequenceNumberMTV method to add last porcessed date and sequence# in the map key 
 *   Muralidhar Kollu              32214               07/05/2016         Defect # 232217.0001 - 249360: Payee field not returning correct values     
 *   Prasanthi Kandula             32709               07/14/2016         Defect #233055.0001 fix -  Updated standardMapForLogging map labels with the correct labels mentioned on defect
 *   Prasanthi Kandula             33598               07/22/2016         Prod defect# D-00477 fix - Updated codeEditDetailsResponse by removing the query for current queue of loggedin user
 *   Prasanthi Kandula             212620              03/07/2018         REQ - 355817 - Incorporated changes as part of Claims V3 service change.
 *   Mithra Bharadwaj              231314              05/21/2018         REQ - 357370 CA Incident #6961670 Code Editing Option Not availabe in CRM Service but available in CCP
 *   Muralidhar Kollu              231314              08/03/2018         REQ - 351451 break-fix - CA Ticket# XXX - Fix to include 9 0s validation logic for Auth/Referral Number
 *   Raja Sekhar Reddy M V                             11/12/2018         REQ - 353088 - Added code for Bank code
 *   Mithra Bharadwaj              327590              01/23/2019         REQ - 383497-  CA ticket 7991726 - CRM - no EX codes for some claims
 *   Prasanthi Kandula             48226               09/19/2019         REQ - 404535   Updated paymentcodes method to display EOR description along with legacy code and legacy(EOB) message
 *   Santhapuri Kishore                                03/05/2020         US - 746884  Ability to view the Provider Specialty Codes And Description associated to the Provider listed on a claim   
 *   Prasanthi Kandula                                 03/12/2020         US - 746884 Implemented switch for PrimarySpecialty field      
 *   Anjani Vaddadi                                    05/21/2020         US-1031822 CodeEditing for Multiple Code Edits
 *   Trina Ghosh									   06/17/2020		  US# - 1039163 Primary Speciality Fields Logging
 *   Pallavi Shewale                                   03/03/2021          US #1422598 - Add 'Claims Payment Responsibility' Field on 'Claim/Pre-determination Details' page  
 *   Pinky Vijur                                       05/20/2021          US# 2251807 - T1PRJ0003145 - CR-004 - Remove 'Claims Payment Responsibility' Field from "Provider Details" Section (CRM Service)
 *   Abhishek Mangutkar                                05/24/2021         US - 2201523 -  Add 'Claims Payment Responsibility' Field in Claim Line Item Accordion (CRM Service)
 *   Pinky Vijur                                       09/06/2022         US - 3427005 - T1PRJ0509966 - MF8 - Modify the CRM System to handle any dental claims with dual procedure codes
 *   Pinky Vijur                                       02/22/2023         User Story 4200769: T1PRJ0883127- Author Shutdown- MF 2- Display and Resend Author generated tEOB�s in CRM Service (Classic)
 ************************************************************************************************************************************************/
 
public with sharing class ClaimDetails_S_HUM_V2 {
    
    private string sTotalInterestFooter ='';
    public Map<String, Map<String, Object>> standardMapForLogging = New Map<String, Map<String, Object>>();
    public string sClaimNumber = '';
    private static final String sLegacyCode_String      = GLOBAL_CONSTANT_HUM.sLegacyCode_SubString;
    public Boolean bSwitch_4200713 {get;set;}
   
    /*
     * Returns an object 
     * <p>
     * This method Is used to assign values to the request helper class
     *
     * @param  String,String,String
     * @return ClaimsServiceDetailsRequest_H_HUM_V2
     */    
     public ClaimsServiceDetailsRequest_H_HUM_V2 assignValuesToHelper(String sClaimType, String sClaimNbr, String sClaimGenKey)
      {
          ClaimsServiceDetailsRequest_H_HUM_V2 oClaimDetailsReq = new ClaimsServiceDetailsRequest_H_HUM_V2();
          ClaimsServiceDetailsRequest_H_HUM_V2.GetClaimDetail oDetailsRequest = new ClaimsServiceDetailsRequest_H_HUM_V2.GetClaimDetail();
          if(String.isNotBlank(sClaimType)&& String.isNotBlank(sClaimNbr) && String.isNotBlank(sClaimGenKey))
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
  
   /*
    * Returns an HttpRequest
    * <p>
    * This method is used to generate the request
    *
    * @param  HttpRequest,HUM_Webservice_Callout__c,String
    * @return HttpRequest
    */ 
    
   public HttpRequest setHttpRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcClaimDetail, String sRequestBody)
    {
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
     * Returns an object 
     * <p>
     * This method is used to fetch the values for Claim Detail Section
     *
     * @param  Object
     * @return ClaimDetails_DTO_HUM
     */
    public ClaimDetails_DTO_HUM_V2 claimDetails(ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult oClaimDetailResult, Boolean bprimarySpecSwitch)
    {
        try
        {
            if(oClaimDetailResult != null)
            {
                List<String> listPSpeciality = new List<String>();
                ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode oDiagCode = oClaimDetailResult.DiagnosisCode; 
                ClaimDetails_DTO_HUM_V2 oClaimDetailsDTO = new ClaimDetails_DTO_HUM_V2();
                oClaimDetailsDTO.bShowMore = false;
                oClaimDetailsDTO.sClaimNbr = oClaimDetailResult.ClaimNumber;
                if(bprimarySpecSwitch)
                {
                    List<ClaimsServiceDetailsResponse_H_HUM_V2.ProviderSpecialties> lstProviderSpec = oClaimDetailResult.ProviderSpecialties;              
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
                }
                oClaimDetailsDTO.sClmMediaCd = oClaimDetailResult.ClaimMediaCode;
                oClaimDetailsDTO.sPatRecordNbr =  oClaimDetailResult.PatientRecordNumber;
                oClaimDetailsDTO.sSrcAuthKey = oClaimDetailResult.SourceAuthKeyId ;
                oClaimDetailsDTO.sBankCode  =oClaimDetailResult.BankAccountCode;
                If (oClaimDetailsDTO.sSrcAuthKey == '0' || oClaimDetailsDTO.sSrcAuthKey == '' || oClaimDetailsDTO.sSrcAuthKey == '00000000000' || oClaimDetailsDTO.sSrcAuthKey == '000000000')
                    oClaimDetailsDTO.bAuthKeyFlag = False;
                else
                    oClaimDetailsDTO.bAuthKeyFlag = true;
                oClaimDetailsDTO.sDeficiencyInd = oClaimDetailResult.DeficiencyIndicator;
                oClaimDetailsDTO.sDeficiencyField = oClaimDetailResult.DeficiencyField;
                oClaimDetailsDTO.sDeficiencyDate = oClaimDetailResult.DeficiencyDate;
                oClaimDetailsDTO.sDeficiencyDesc = oClaimDetailResult.DeficiencyDesc;
                oClaimDetailsDTO.sDiagIndicator = oClaimDetailResult.DiagnosisIndicator;
                oClaimDetailsDTO.sSrcLvCASPrefix = oClaimDetailResult.SourceLvCASPrefixCode;
                oClaimDetailsDTO.sPaymentMethod = oClaimDetailResult.PaymentMethod; 
                sTotalInterestFooter = oClaimDetailResult.TotalInterestAmount;
                oClaimDetailsDTO.lstDiagnosisCodes = new List<string>();
                oClaimDetailsDTO.lstDiagnosisCodes = oDiagCode.Code;
                if(!oClaimDetailsDTO.lstDiagnosisCodes.isEmpty() )
                    oClaimDetailsDTO.sFirstDiagnosisCode = oClaimDetailsDTO.lstDiagnosisCodes[0];
                standardMapForLogging.put('ClaimNbr', new Map<String, Object>{'Claim/Pre-D #' => oClaimDetailResult.ClaimNumber});
                standardMapForLogging.put('SrcLvCASPrefix', new Map<String, Object>{'SrcLvCASPrefix' => oClaimDetailResult.SourceLvCASPrefixCode});
                standardMapForLogging.put('Bankcode', new Map<String, Object>{'BankCode ' => oClaimDetailResult.BankAccountCode});
                return oClaimDetailsDTO;
             }
         }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_S_HUM_V2', 'ClaimDetails');
        }
        return null;
    }
    
    /*
     * Returns a string
     * <p>
     * This method is used to concatenate the footer values for Line Item
     *
     * @param  Object, String, String, String
     * @return string
     */
    
    public string lineItemsFooter(ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult oClaimDetailResult, String DeductAmt, String CopayAmt, String CoInsAmt)
    {
        try
        {
            if(oClaimDetailResult != null)
            {
                ClaimDetails_DTO_HUM_V2 oClaimDetailsDTO = new ClaimDetails_DTO_HUM_V2();
                oClaimDetailsDTO.sChargeAmt  = oClaimDetailResult.ChargeAmount;
                oClaimDetailsDTO.sBenAllowAmt = oClaimDetailResult.BenefitAllowedAmount;
                oClaimDetailsDTO.sProvWriteOff = oClaimDetailResult.ProviderWriteOff;
                oClaimDetailsDTO.sBenDenyAmt = oClaimDetailResult.BenefitDeniedAmount;
                oClaimDetailsDTO.sMbrRespAmt = oClaimDetailResult.MemberResponsibilityAmount;
                oClaimDetailsDTO.sPaidAmt = oClaimDetailResult.PaidAmount;
                oClaimDetailsDTO.sTotalInterest = oClaimDetailResult.TotalInterestAmount;
                oClaimDetailsDTO.sExcludeAmt = oClaimDetailResult.ExcludeAmount;
                oClaimDetailsDTO.sCobPaidAmt = oClaimDetailResult.COBPaidAmount;
                String LineItemFooter = oClaimDetailsDTO.sChargeAmt+'&'+oClaimDetailsDTO.sBenAllowAmt+'&'+oClaimDetailsDTO.sProvWriteOff+'&'+oClaimDetailsDTO.sExcludeAmt;
                LineItemFooter += '&'+oClaimDetailsDTO.sBenDenyAmt+'&'+CopayAmt+'&'+DeductAmt+'&'+CoInsAmt+'&'+oClaimDetailsDTO.sCobPaidAmt+'&'+oClaimDetailsDTO.sMbrRespAmt+'&'+oClaimDetailsDTO.sTotalInterest+'&'+oClaimDetailsDTO.sPaidAmt+'& ';
                
                standardMapForLogging.put('ChargeAmt', new Map<String, Object>{'Billed Amt' => oClaimDetailResult.ChargeAmount});
                return LineItemFooter;
             }
         }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_S_HUM_V2', 'LineItemsFooter');
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
    public List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine> claimLineItems(ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult oClaimDetailResult,string sProviderName,string sProviderId,string snpiId)
    {
        try
        {
            if(oClaimDetailResult != null)
            {
                List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine> lstClaimDetailLineItem = new List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine>();
                ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode oDiagCodeService = oClaimDetailResult.DiagnosisCode;
                ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLines oClaimDetailLines = oClaimDetailResult.ClaimDetailLines; 
                List<ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLine> lstClaimDetail = oClaimDetailLines.ClaimDetailLine;
                List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstCodes = new List<ClaimLineItems_DTO_HUM_V2.PaymentCode>();
                for(ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLine oClaimDetail : lstClaimDetail)
                {
                   ClaimLineItems_DTO_HUM_V2.ClaimDetailLine oClaimDetails = new ClaimLineItems_DTO_HUM_V2.ClaimDetailLine();
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
                   ClaimLineItems_DTO_HUM_V2.DiagCode oDiagCode = new ClaimLineItems_DTO_HUM_V2.DiagCode();
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
    
    
    /*
     * Returns a Object
     * <p>
     * This method is used to fetch the values of Payment Codes for Claim Line Items
     *
     * @param  Object
     * @return ClaimLineItems_DTO_HUM_V2.PaymentCodes
     */
    public ClaimLineItems_DTO_HUM_V2.PaymentCodes getClaimPaymentCodes(ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLine  oClaimDetail)
    {
       ClaimLineItems_DTO_HUM_V2.ClaimDetailLine oClaimDetails = new ClaimLineItems_DTO_HUM_V2.ClaimDetailLine();                
       ClaimsServiceDetailsResponse_H_HUM_V2.PaymentCodes oPaymntCodes = oClaimDetail.PaymentCodes;
       List<ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail> lstPaymentCodes = oPaymntCodes.PaymentDetail;
       List<ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails> lstProviderDetails = oClaimDetail.ProviderDetails;
       List<ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail> lstPaymentCodesClone = lstPaymentCodes.clone();
       List<ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails> lstProviderDetailsClone = lstProviderDetails.clone();
       List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstPayCodes = new List<ClaimLineItems_DTO_HUM_V2.PaymentCode>();
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
                       ClaimLineItems_DTO_HUM_V2.PaymentCode oLineItemPaymentCode =  new ClaimLineItems_DTO_HUM_V2.PaymentCode();
                       if(lstPaymentCodes[iPayCode].LegacyCode.contains('<'))
                       {
                           lstPaymentCodes[iPayCode].LegacyCode=lstPaymentCodes[iPayCode].LegacyCode.replaceAll('<',' &lt;');
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
       ClaimLineItems_DTO_HUM_V2.PaymentCodes oClaimPayment = new ClaimLineItems_DTO_HUM_V2.PaymentCodes();
       oClaimPayment.PaymentCode = lstPayCodes;
       return oClaimPayment ;
    }
   
   /*
     * Returns a Object
     * <p>
     * This method is used to fetch the values of Payment Codes for claim line items when providerDetails are received as blank from service
     *
     * @param  List<ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail>
     * @return List<ClaimLineItems_DTO_HUM_V2.PaymentCode>
     */ 
   public static List<ClaimLineItems_DTO_HUM_V2.PaymentCode> getPayCodes( List<ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail> lstPaymentCodes)
    {
        List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstPaymtCodes = new List<ClaimLineItems_DTO_HUM_V2.PaymentCode>();
        for(ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail oPayCode : lstPaymentCodes)
           {
                ClaimLineItems_DTO_HUM_V2.PaymentCode oLineItemPaymentCode =  new ClaimLineItems_DTO_HUM_V2.PaymentCode();
                if(oPayCode.LegacyCode.contains('<'))
                {
                    oPayCode.LegacyCode=oPayCode.LegacyCode.replaceAll('<','&lt;');
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
     * @param  List<ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails>
     * @return List<ClaimLineItems_DTO_HUM_V2.PaymentCode>
     */ 
   public static List<ClaimLineItems_DTO_HUM_V2.PaymentCode> getProviderCodes( List<ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails> lstProviderDetails)
    {
         List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstProviderCodes = new List<ClaimLineItems_DTO_HUM_V2.PaymentCode>();
         for(ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails oProviderCode : lstProviderDetails)
               {
                   ClaimLineItems_DTO_HUM_V2.PaymentCode oLineItemPaymentCode =  new ClaimLineItems_DTO_HUM_V2.PaymentCode();
                   if(oProviderCode.ProviderReasonCode.contains('<'))
                   {
                       oProviderCode.ProviderReasonCode = oProviderCode.ProviderReasonCode.replaceAll('<','&lt;');
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
    
    /*
     * Returns a List
     * <p>
     * This method is used to fetch the values for Claim Line Items
     *
     * @param  Object
     * @return List<ClaimDetails_DTO_HUM>
     */
    public ClaimLineItems_DTO_HUM_V2.ClaimDetailLine getClaimLineItemDetails(ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLine  oClaimDetail)
    {
        Boolean isCheckServiceCodeSwitch =  HUMUtilityHelper.isCRMFunctionalityON('3427005');
        ClaimLineItems_DTO_HUM_V2.ClaimDetailLine oClaimDetails = new ClaimLineItems_DTO_HUM_V2.ClaimDetailLine();                
        oClaimDetails.sLastProcessDate = oClaimDetail.LastProcessDate;
        oClaimDetails.sSrcClaimLineSequence = oClaimDetail.SourceClaimLineSequence;
        oClaimDetails.sServiceStartDate = oClaimDetail.ServiceStartDate;
        oClaimDetails.sServiceEndDate = oClaimDetail.ServiceEndDate;
        oClaimDetails.sServiceCode = oClaimDetail.ServiceCode;
         if(isCheckServiceCodeSwitch)
        {
        	if(string.isNotEmpty(oClaimDetails.sServiceCode) && oClaimDetails.sServiceCode.contains('*')) 
        	{ 
            	oClaimDetails.sServiceCode = oClaimDetails.sServiceCode.removeEnd('*');
        	}
        }
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
        oClaimDetails.sExLineItem = FetchExcodeLineItem(oClaimDetail.PaymentCodes);
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
     * This method is used to return the Sequence Number for every Last Processed Date
     *
     * @param  List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine>, String
     * @return String
     */
    public String getSequenceNumberMTV(List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine> lstClaimDetailLineItem,string sPlatformCode,string sProduct)
    {
         bSwitch_4200713 = HUMUtilityHelper.isCRMFunctionalityON('4200713');
         Map<String,String> mapSeqNumber = new Map<String,String>();
         String sSeqNumberForDate = '';
         String sMTVFilter = '';
         if(lstClaimDetailLineItem!= null && !lstClaimDetailLineItem.isEmpty())
         for(ClaimLineItems_DTO_HUM_V2.ClaimDetailLine oClaimDetail : lstClaimDetailLineItem)
         {
              if(sPlatformCode == 'EM'|| (bSwitch_4200713 && sPlatformCode == 'HP' && sProduct == 'DEN'))
                   sMTVFilter = oClaimDetail.sLastProcessDate +' ('+ oClaimDetail.sSrcClaimLineSequence+')';
              if(sPlatformCode  == 'LV' || (bSwitch_4200713 && sPlatformCode == 'HP' && sProduct == 'MED'))
                   sMTVFilter = oClaimDetail.sLastProcessDate;
              if(!mapSeqNumber.containsKey(sMTVFilter))  
              {
                   mapSeqNumber.put(sMTVFilter,oClaimDetail.sLastProcessDate);   
              }
         }
         sSeqNumberForDate = JSON.serialize(mapSeqNumber);
         return sSeqNumberForDate;
    }
    
    /*
     * Returns a String
     * <p>
     * This method is used to return the first Legacy code for every line item
     *
     * @param  Object
     * @return String
     */
    public String fetchExcodeLineItem(ClaimsServiceDetailsResponse_H_HUM_V2.PaymentCodes Excode)
    {
       if(Excode!=null)
       {
         List<ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail> lstPaymentCodes = Excode.PaymentDetail;
         String sFirstExCode;
         if(lstPaymentCodes!=null && !lstPaymentCodes.isEmpty())
         {
             if(lstPaymentCodes[0].LegacyCode.contains('<')) 
             {
                lstPaymentCodes[0].LegacyCode=lstPaymentCodes[0].LegacyCode.replaceAll('<','&lt;');
                 sFirstExCode =  lstPaymentCodes[0].LegacyCode;
             }
             else
             {
                 sFirstExCode =  lstPaymentCodes[0].LegacyCode;
             }
         }
         return sFirstExCode;
       }
       else
        return null;
    }
    
    
    /*
    * Returns an Object
    * <p>
    * This method is used to form the input for getCodeEditDetails() service method
    * and display Rationale on Claims Detail Page
    * @param  List<String>,List<ClaimLineItems_DTO_HUM_V2.PaymentCode>, String, String, String, String, String
    * @return ClaimsServiceCodeEditDetailRequest_H_HUM
    */
    public ClaimsServiceCodeEditDetailRequest_H_HUM  codeEditRationale(List<String> lstDiagnosisCd,List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstPaymentCode, String sPlatformCode, String sDiagIndicator, String sPaidStatDesc, String sCPTModCode, String sServiceCode) 
    {
       ClaimsServiceCodeEditDetailRequest_H_HUM oClaimCodeEditReq = new ClaimsServiceCodeEditDetailRequest_H_HUM();
       ClaimsServiceCodeEditDetailRequest_H_HUM.GetCodeEditDetails oDetailsRequest = new ClaimsServiceCodeEditDetailRequest_H_HUM.GetCodeEditDetails();
       ClaimsServiceCodeEditDetailRequest_H_HUM.RationaleDTO oRationaleDTO = new ClaimsServiceCodeEditDetailRequest_H_HUM.RationaleDTO();
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
    * @param  List<ClaimLineItems_DTO_HUM_V2.PaymentCode>
    * @return ClaimsServiceCodeEditDetailRequest_H_HUM.ExCodes
    */
    public ClaimsServiceCodeEditDetailRequest_H_HUM.ExCodes codeEditExCodes(List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstPaymentCode)
    {
       ClaimsServiceCodeEditDetailRequest_H_HUM.ExCodes oExCodes = new ClaimsServiceCodeEditDetailRequest_H_HUM.ExCodes();
       List<String> lstStringExCodes = new List<String>();
       string sLegacyCode;
       for(ClaimLineItems_DTO_HUM_V2.PaymentCode oPaymentCode : lstPaymentCode)
       {
           if(oPaymentCode.sLegacyCd.length()<=3)
           sLegacyCode = oPaymentCode.sLegacyCd;
           else
           {
                 sLegacyCode = oPaymentCode.sLegacyCd;
                 if(sLegacyCode.contains(sLegacyCode_String)== true)
                    {
                       sLegacyCode=sLegacyCode.replaceAll(sLegacyCode_String,'');
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
    * @param  String, List<ClaimLineItems_DTO_HUM_V2.PaymentCode>, String, String
    * @return ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureCodes
    */
    public ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureCodes codeEditRejectedProcedureCode(String sPaidStatDesc,List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstPaymentCode,String sPlatformCode, String sServiceCode)
    {
       ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureCodes oRejectProcedure = new ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureCodes();
       List<ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureDTO> lstRejectDTO = new List<ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureDTO>();
       ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureDTO oRejectDTO = new ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureDTO(); 
       if(sPaidStatDesc == 'DENIED')
       {
          for(ClaimLineItems_DTO_HUM_V2.PaymentCode oPaymentCode : lstPaymentCode)
           {
               oRejectDTO = new ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureDTO(); 
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
    * @return ClaimsServiceCodeEditDetailRequest_H_HUM.PaidProcedureCodes
    */
    public ClaimsServiceCodeEditDetailRequest_H_HUM.PaidProcedureCodes codeEditPaidProcedureCd(String sServiceCode, String sPaidStatDesc)
    {
       ClaimsServiceCodeEditDetailRequest_H_HUM.PaidProcedureCodes oPaidCodes = new ClaimsServiceCodeEditDetailRequest_H_HUM.PaidProcedureCodes();
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
    * @return ClaimsServiceCodeEditDetailRequest_H_HUM.PaidModifers
    */
    public ClaimsServiceCodeEditDetailRequest_H_HUM.PaidModifers codeEditPaidModifier(String sCPTModCode) 
    {
       ClaimsServiceCodeEditDetailRequest_H_HUM.PaidModifers oPaidModifier = new ClaimsServiceCodeEditDetailRequest_H_HUM.PaidModifers();
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
    * @return ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCodes
    */
    public ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCodes codeEditDiagnosisCodes(List<String> lstDiagnosisCd, String sDiagIndicator)
    {
       ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCodes oDiagnosisCd = new ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCodes();
       List<ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCode> lstDiagnosisCod = new List<ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCode>();
       for(String sCode : lstDiagnosisCd)
       {
               ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCode oDiagCode = new ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCode();
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
    * Returns an ClaimsGetCodeEditDetails_DTO_HUM
    * <p>
    * This method is used to assign response to dto class
    *
    * @param  ClaimsServiceCodeEditDtlResponse_H_HUM,HUM_Webservice_Callout__c,String
    * @return ClaimsGetCodeEditDetails_DTO_HUM
    */ 
    public List<ClaimsGetCodeEditDetails_DTO_HUM> codeEditDetailsResponse(ClaimsServiceCodeEditDtlResponse_H_HUM.GetCodeEditDetailsResponse oClaimsCodeEditResponse)
    {
         ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDTO oRationaleDTO =  oClaimsCodeEditResponse.RationaleDTO;
         ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionCodes oRationaleDescCode = oRationaleDTO.RationaleDescriptionCodes;
         List<ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionDTO> lstClaimDetail = oRationaleDescCode.RationaleDescriptionDTO;
         List<ClaimsGetCodeEditDetails_DTO_HUM> lstCodeEditDTO = new List<ClaimsGetCodeEditDetails_DTO_HUM>();
         for(ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionDTO oClaimDetail : lstClaimDetail)
         {
             ClaimsGetCodeEditDetails_DTO_HUM oClaimCodeDetails = new ClaimsGetCodeEditDetails_DTO_HUM();   
             oClaimCodeDetails.sCodeEditInd =  oClaimDetail.CodeEditInd;
             oClaimCodeDetails.sRationaleDesc =  oClaimDetail.RationaleDesc;
             oClaimCodeDetails.sClaimNbr = sClaimNumber;
             lstCodeEditDTO.add(oClaimCodeDetails);
         }
          return lstCodeEditDTO;
    }
  
 }