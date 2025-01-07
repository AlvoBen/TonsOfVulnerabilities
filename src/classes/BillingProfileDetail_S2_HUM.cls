/*******************************************************************************************************************************************************
Apex Class Name  : BillingProfileDetail_S2_HUM
Version          : 1.0
Created Date     : 06/01/2018
Function         : This is used to return response from service and format the response.
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Vandana Chaudhari          243694                        06/01/2018                Original Version
* Sumit Pharande 										   07/10/2018                REQ-352427
**********************************************************************************************************************************************/
public with sharing class BillingProfileDetail_S2_HUM
{  
    String  objOutputsType='';
    String  objOutputsLine1='';
    String  objOutputsLine2 ='';
    String  objOutputsCity ='';
    String  objOutputsState = '';
    String  objOutputsZipCd ='';
    String  objOutputsZipPlus = ''; 
    /*
    * Method Name : getServiceObject
    * Description : This method is used to return response.
    * Return type : BillingProfileResponseV2_DTO_HUM
    * Parameter   : BillingProfileRequestV2_DTO_HUM
    */
    public BillingProfileResponseV2_DTO_HUM getServiceObject(BillingProfileRequestV2_DTO_HUM billingRequest)
    {     
        BillingProfileResponseV2_DTO_HUM billingRes = new BillingProfileResponseV2_DTO_HUM(); 
          try
          {
              HUM_Webservice_Callout__c getbillingProfile = HUM_Webservice_Callout__c.getValues('BillingServiceV2');
              HttpRequest http_getbillingProfile = new HttpRequest();
              http_getbillingProfile.setEndpoint(getbillingProfile.End_Point_URL__c);
              http_getbillingProfile.setMethod(getbillingProfile.Request_Type__c);
              http_getbillingProfile.setTimeout(Integer.valueOf(getbillingProfile.Timeout__c));
              if (!Test.isRunningTest()) http_getbillingProfile.setClientCertificateName(getbillingProfile.Certificate_Name__c);
              http_getbillingProfile.setHeader(Label.HUMServiceTesterController_content_type, getbillingProfile.Content_Type__c);
              String objJSON = JSON.serialize(billingRequest);
              http_getbillingProfile.setBody(objJSON);
              Http http = new Http();
              HttpResponse responseGetBilling = http.send(http_getbillingProfile);
              billingRes = (BillingProfileResponseV2_DTO_HUM) System.JSON.deserialize(responseGetBilling.getbody(), BillingProfileResponseV2_DTO_HUM.class);
          }
          catch(exception ex)
          {
              HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'getServiceObject');   
          }
          return  billingRes;
    }
    /*
    * Method Name : invokeBillingProfileDetailsService
    * Description : This method is used to return response.
    * Return type : BillingProfileResponseV2_DTO_HUM
    * Parameter   : BillingProfile_DTO_HUM
    */
    public BillingProfileResponseV2_DTO_HUM invokeBillingProfileDetailsService(BillingProfile_DTO_HUM objSerInput)
    {
        BillingProfileResponseV2_DTO_HUM objResponse = new BillingProfileResponseV2_DTO_HUM();
        try
        {
             BillingProfileRequestV2_DTO_HUM objSerReq= prepareServiceRequest(objSerInput);
             objResponse = getServiceObject(objSerReq);
        }
        catch(exception ex)
        {
          HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'invokeBillingProfileDetailsService');   
        }
        return objResponse;
    }
    /*
    * Method Name : prepareServiceRequest
    * Description : This method is used to Prepare service request.
    * Return type : BillingProfileRequestV2_DTO_HUM
    * Parameter   : BillingProfile_DTO_HUM
    */
    public BillingProfileRequestV2_DTO_HUM prepareServiceRequest(BillingProfile_DTO_HUM objSerInput)
    {
          BillingProfileRequestV2_DTO_HUM objBilling = new BillingProfileRequestV2_DTO_HUM();
               try
                  {
                    BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequest getBillinginfo = new BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequest();
                    BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequestCriteria objRequest = new BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequestCriteria();
                    objRequest.RequestContext = new BillingProfileRequestV2_DTO_HUM.RequestContext();
                    objRequest.RequestContext.CallingSystemID = HUMConstants.sCallingSystemID;
                    objRequest.RequestContext.UserID = objSerInput.sUserID;
                    objRequest.RequestContext.UserIDType = HUMConstants.sUserIDType;
                    objRequest.RequestContext.ProcessDate = DateTime.Now().format('yyyy-MM-dd');
                   
                    
                    BillingProfileRequestV2_DTO_HUM.SearchCriteria oSerchCrit = new BillingProfileRequestV2_DTO_HUM.SearchCriteria();
                    BillingProfileRequestV2_DTO_HUM.SearchCriteriaList oSerchCritlst = new BillingProfileRequestV2_DTO_HUM.SearchCriteriaList();
                    List<BillingProfileRequestV2_DTO_HUM.SearchCriteria> lstSearchCriteria = new List<BillingProfileRequestV2_DTO_HUM.SearchCriteria>();
                    oSerchCrit.PlatformCD = objSerInput.sPlatformCode; 
                    oSerchCrit.IDType = objSerInput.sIDType;
                    oSerchCrit.SearchID = objSerInput.sSearchID; 
                    
                    lstSearchCriteria.add(oSerchCrit);
                    oSerchCritlst.SearchCriteria=lstSearchCriteria ;
                    objRequest.SearchCriteria=oSerchCritlst;
                    objRequest.IncludeCriteria = prepareProfDetailsSerRequest();
                    getBillinginfo.GetBillingAccountInfoRequestCriteria = objRequest;
                    objBilling.GetBillingAccountInfoRequest=getBillinginfo ; 
                  }
               
            catch(exception ex)
                {
                 HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'prepareServiceRequest'); 
                }       
                return objBilling;
    }
   /*
    * Method Name : prepareProfDetailsSerRequest
    * Description : This method is used to Prepare service request.
    * Return type : BillingProfileRequestV2_DTO_HUM.IncludeCriteria
    * Parameter   : NA
    */
    public BillingProfileRequestV2_DTO_HUM.IncludeCriteria prepareProfDetailsSerRequest()
    {
           
        BillingProfileRequestV2_DTO_HUM.IncludeCriteria objSerInput= new BillingProfileRequestV2_DTO_HUM.IncludeCriteria();
            try
                {
                    objSerInput.PaymentOption = 'true';
                    objSerInput.CollectionInfo = 'true';
                    objSerInput.BillingMode = 'true';
                    objSerInput.BillingFrequency = 'true';
                    objSerInput.BalanceInfo = 'true';
                    objSerInput.BillingAddressInfo ='true';
                    objSerInput.TransactionInfo = 'true'; 
                    objSerInput.TransactionType = HUMConstants.sTransactionType;
                    objSerInput.NbrOfTransactions = '1';
                    objSerInput.InvoiceInfo = new BillingProfileRequestV2_DTO_HUM.InvoiceInfo();
                    objSerInput.InvoiceInfo.IncludeInvoiceInformation = 'true';
                    objSerInput.InvoiceInfo.NbrOfInvoices = '1';
                    objSerInput.PremiumInfo = new BillingProfileRequestV2_DTO_HUM.PremiumInfo();
                    objSerInput.PremiumInfo.IncludePremiumInformation = 'true';
                    objSerInput.PremiumInfo.RateLevel =HUMConstants.sRateLevel;  
                }
            catch(exception ex)
                {
                    HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'prepareProfDetailsSerRequest'); 
                }
            return objSerInput;
    }
    
    /*
    * Method Name : parseServiceResponse
    * Description : This method is used to parse service response object.
    * Return type : List<BillingProfile_DTO_HUM>
    * Parameter   : Boolean , BillingProfileResponseV2_DTO_HUM
    */
    public List<BillingProfile_DTO_HUM> parseServiceResponse(Boolean isDetail,BillingProfileResponseV2_DTO_HUM objFinalResponse)
    {
        List<BillingProfile_DTO_HUM> lstServiceOutput = null;
        try
            {
                BillingProfileResponseV2_DTO_HUM objServiceRes = objFinalResponse;
                BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponse objBillAcc = new BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponse();
                objBillAcc=objServiceRes.GetBillingAccountInfoResponse;
                BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponseList objBillAccList = new BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponseList();
                objBillAccList=objBillAcc.GetBillingAccountInfoResponseList;
                List<BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponseDetail> objBillAccListDetail = new List<BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponseDetail>();
                objBillAccListDetail =objBillAccList.GetBillingAccountInfoResponseDetail ;
                
                if(objBillAccListDetail != null && !objBillAccListDetail .isEmpty())
                {        
                    for(integer i =0;i<objBillAccListDetail.size();i++)
                    {    
                        BillingProfileResponseV2_DTO_HUM.BillingAccountList billAccList = objBillAccListDetail[0].BillingAccountList;
                            if(billAccList != null && billAccList.BillAccount != null)
                            {
                                lstServiceOutput = getProfileInfo(isDetail,billAccList ,lstServiceOutput);
                            }
                    }
                } 
            }
        catch(exception ex) 
        {
          HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'parseServiceResponse');  
        }       
        return lstServiceOutput;
    }
    
    /*
    * Method Name : prepareOutputList
    * Description : This method is used to prepare output list.
    * Return type : List<BillingProfile_DTO_HUM>
    * Parameter   : BillingProfileResponseV2_DTO_HUM.BillingAccountList, List<BillingProfile_DTO_HUM>
    */
    private List<BillingProfile_DTO_HUM> getProfileInfo(Boolean isDetail,BillingProfileResponseV2_DTO_HUM.BillingAccountList objBillAccount,List<BillingProfile_DTO_HUM> lstServiceOutput )
    {
        BillingProfile_DTO_HUM objOutput;
        lstServiceOutput  = new List<BillingProfile_DTO_HUM>();
       try
       {
            for(integer j =0;j <= objBillAccount.BillAccount.size()-1;j++)
            {
                objOutput = new BillingProfile_DTO_HUM();
                objOutput.sGroupPIDNumber = objBillAccount.BillAccount[j].BillingProfile.DisplayID;
                objOutput.iProfileNumber = Integer.valueof(objBillAccount.BillAccount[j].BillingProfile.ProfileSequenceNbr);
                objOutput.iAccountNumber =  Integer.valueof(objBillAccount.BillAccount[j].BillingProfile.AccountKey);
                objOutput.sProfileAccountName = objBillAccount.BillAccount[j].BillingProfile.ProfileName;
                objOutput.sEffectiveDate = formatDate(objBillAccount.BillAccount[j].BillingProfile.EffectiveDate);
                objOutput.sEndDate = formatDate(objBillAccount.BillAccount[j].BillingProfile.EndDate);
                objOutput.sStatus = (isDetail ? objBillAccount.BillAccount[j].BillingProfile.ProfileStatusCD : (objBillAccount.BillAccount[j].BillingProfile.ProfileStatusCD == 'A'? 'Active':'Termed'));
                objOutput.sTermReasonCode = objBillAccount.BillAccount[j].BillingProfile.EndReason;
                if(isDetail)
                {
                   objOutput = getProfileDetails(objBillAccount.BillAccount[j],objOutput );
                }
                lstServiceOutput.Add(objOutput);            
            }
       }
       catch(exception ex)
           {
            HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'getProfileInfo');    
           }
        return lstServiceOutput;
    }
    
     /*
    * Method Name : getProfileDetails
    * Description : This method is used to prepare to prepare output list.
    * Return type : BillingProfile_DTO_HUM
    * Parameter   : BillingProfileResponseV2_DTO_HUM.BillAccount, BillingProfile_DTO_HUM
    */
    public BillingProfile_DTO_HUM getProfileDetails(BillingProfileResponseV2_DTO_HUM.BillAccount objBillAccount,BillingProfile_DTO_HUM objOutput)
    {
          try
          {
               if(objBillAccount.BillingProfile != null) objOutput = getAccountSummaryInfo(objBillAccount.BillingProfile ,objBillAccount.RecurringPaymentMethodInfo ,objOutput);
                if(objBillAccount.BalanceList != null) 
                {
                    BillingProfileResponseV2_DTO_HUM.BalanceList objBalanceList = new BillingProfileResponseV2_DTO_HUM.BalanceList();
                    objBalanceList=objBillAccount.BalanceList;     
                    objOutput = getBalance(objBalanceList.Balance,objOutput);
                }
                if(objBillAccount.TransactionHistoryList != null){
                    BillingProfileResponseV2_DTO_HUM.TransactionHistoryList objTransactionHistoryList = objBillAccount.TransactionHistoryList;
                    objOutput = getTransactionDetails(objTransactionHistoryList.TransactionDetails,objOutput);
                }
                if(objBillAccount.InvoiceList != null) 
                {
                   BillingProfileResponseV2_DTO_HUM.InvoiceList objInvoiceSummary = objBillAccount.InvoiceList;  
                   objOutput = getInvoiceDetails(objInvoiceSummary.InvoiceSummary,objOutput);
                }
                    objOutput = getCollectionInfo(objBillAccount.CollectionInfo,objOutput );
                    objOutput = getPremiumInfo(objBillAccount.PremiumInfo,objOutput );
                if(objBillAccount.AddressList !=null)
                {
                    BillingProfileResponseV2_DTO_HUM.AddressList objAddressList = objBillAccount.AddressList;       
                    objOutput = getAddressInfo(objAddressList.Address,objOutput );
                }
          }
          catch(exception ex)
          {
                HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'getProfileDetails');  
          }
       return objOutput;
    }
      /*
    * Method Name : getAccountSummaryInfo
    * Description : This method is used to retrieve Account Summary Info from service response.
    * Return type : BillingProfile_DTO_HUM
    * Parameter   : BillingProfileResponseV2_DTO_HUM.BillingProfile, BillingProfileResponseV2_DTO_HUM.RecurringPaymentMethodInfo ,BillingProfile_DTO_HUM
    */
    public BillingProfile_DTO_HUM getAccountSummaryInfo(BillingProfileResponseV2_DTO_HUM.BillingProfile objProfile,BillingProfileResponseV2_DTO_HUM.RecurringPaymentMethodInfo objPay,BillingProfile_DTO_HUM objOutput )
    {
        try
        {
           if(objProfile != null)
            {
                objOutput.sProfileType = objProfile.ProfileTypeCD;
                objOutput.sBillBlock = objProfile.BillBlockCD;
                objOutput.sProductType = objProfile.ProductTypeCD;
                objOutput.sBillingPlatform = objProfile.BillingPlatformCD;
                objOutput.sBillMethod = objProfile.BillMethodCD;
                objOutput.sLastBillPeriodEndDate = formatDate(objProfile.LastBillDate);
                objOutput.sNextBillDate = formatDate(objProfile.NextBillDate);
                objOutput.sBillingFrequency = objProfile.BillFrequencyCD;
                objOutput.sProfBillingMode = objProfile.BillModeCD;           
                objOutput.sPaidThruDate = formatDate(objProfile.PaidThroughDate);
                objOutput.sBillHold = objProfile.HoldBillIndicator;
                objOutput.sBillHoldReason = objProfile.HoldBillReasonCD;
            }
          if(objPay != null) 
            {
                objOutput.sRecurringPaymentOption = objPay.PaymentOption;
                objOutput.sNextRecurringDate = formatDate(objPay.NextRecurringDate);
            }
        }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'getAccountSummaryInfo');   
        }
        return objOutput; 
    }
     
     
     /*
    * Method Name : getAddressInfo
    * Description : This method is used to retrieve Address details from service response.
    * Return type : BillingProfile_DTO_HUM
    * Parameter   : List<BillingProfileResponseV2_DTO_HUM.Address> ,BillingProfile_DTO_HUM
    */
     public BillingProfile_DTO_HUM getAddressInfo(List<BillingProfileResponseV2_DTO_HUM.Address> lstAddress ,BillingProfile_DTO_HUM objOutput)
     {
       try
       {
            if(lstAddress!= null)
            {
            for(BillingProfileResponseV2_DTO_HUM.Address objAddress :lstAddress)
                {
                   objOutputsType = objAddress.Type;
                   objOutputsLine1 = objAddress.Line1;
                   objOutputsLine2 = objAddress.Line2;
                   objOutputsCity = objAddress.City;
                   objOutputsState = objAddress.State;
                   objOutputsZipCd = objAddress.Zip;
                   objOutputsZipPlus = objAddress.ZipPlus;
				   objOutput.sAddress = (objOutputsLine1!=null?objOutputsLine1:'')+' '+(objOutputsLine2!=null?objOutputsLine2:'')+' '+(objOutputsCity!=null?objOutputsCity:'')+' '+(objOutputsState!=null?objOutputsState:'')+' '+(objOutputsZipCd!=null?objOutputsZipCd:'');
                }
            }
       }
       catch(exception ex)
       {
          HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'getAddressInfo');    
       }
        return objOutput ;
      }
      /*
    * Method Name : getPremiumInfo
    * Description : This method is used to retrieve premium details from service response.
    * Return type : BillingProfile_DTO_HUM
    * Parameter   : List<BillingProfileResponseV2_DTO_HUM.PremiumInfo> ,BillingProfile_DTO_HUM
    */
    public BillingProfile_DTO_HUM getPremiumInfo(BillingProfileResponseV2_DTO_HUM.PremiumInfo objPremium,BillingProfile_DTO_HUM objOutput)
    {
        try
        {
            if(objPremium != null)
            {
                objOutput.dPremium = Decimal.valueof(objPremium.Premium);
                objOutput.dOptionalSupplementalPremium = Decimal.valueof(objPremium.AdditionalSupplementalCharges);
                objOutput.dDiscounts = Decimal.valueof(objPremium.DiscountAmt);
                objOutput.dSubsidies = Decimal.valueof(objPremium.SubsidyAmt);
                objOutput.dPenalties = Decimal.valueof(objPremium.PenaltyAmt);
                objOutput.dFees = Decimal.valueof(objPremium.Fees);
                objOutput.dOthers = Decimal.valueof(objPremium.OtherAmt);
                objOutput.dNetMonthlyPremiumAmount = Decimal.valueof(objPremium.NetPremium);
            }
        }
        catch(exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'getPremiumInfo');     
        }
        return objOutput;
     }
    
     /*
    * Method Name : getCollectionInfo
    * Description : This method is used to retrieve collection details from service response.
    * Return type : BillingProfile_DTO_HUM
    * Parameter   : List<BillingProfileResponseV2_DTO_HUM.CollectionInfo> ,BillingProfile_DTO_HUM
    */
   public BillingProfile_DTO_HUM getCollectionInfo(BillingProfileResponseV2_DTO_HUM.CollectionInfo objCollection,BillingProfile_DTO_HUM objOutput)
    {
        try
        {
            if(objCollection != null)
            {
                objOutput.sNextActionCD = objCollection.NextCollectionCD;
                objOutput.sNextActionDate = formatDate(objCollection.NextCollectionActionDate);
                objOutput.sExpectedTermActionDate = formatDate(objCollection.ExpectedTermActionDate);
                objOutput.sExpectedTermDate = objCollection.ExpectedTermDate;
                objOutput.sDelinquentDate = formatDate(objCollection.DelinquentDate);
                objOutput.sCollectionStatusCode = objCollection.CollectionStatusCD;
                objOutput.sProtectUntilDate = formatDate(objCollection.ProtectUntilDate);
            }
        }
        catch(exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'getCollectionInfo'); 
        }
        return objOutput;
    }
    
     /*
    * Method Name : getBalance
    * Description : This method is used to retrieve invoice details from service response.
    * Return type : BillingProfile_DTO_HUM
    * Parameter   : List<BillingProfileResponseV2_DTO_HUM.Balance> ,BillingProfile_DTO_HUM
    */
    public BillingProfile_DTO_HUM getBalance(List<BillingProfileResponseV2_DTO_HUM.Balance> lstBalanceInfo,BillingProfile_DTO_HUM objOutput)
    {
        try
         {
           if(lstBalanceInfo != null)
             {
                for(BillingProfileResponseV2_DTO_HUM.Balance obj :lstBalanceInfo)
                {
                    if(obj != null)
                    {                    
                        if(obj.Type!= null && obj.Amount != null)
                        {
                            if(obj.Type.trim().equalsignoreCase(HUMConstants.sCurrentBalance))
                            {
                                 objOutput.dCurrentAccountBalance = Decimal.valueof(obj.Amount);
                            }
                            else if(obj.Type.trim().equalsignoreCase(HUMConstants.sCurrentNonSSABalance))
                            {
                                objOutput.dCurrentAccBalNonSSA = Decimal.valueof(obj.Amount);
                            }
                            else if(obj.Type.trim().equalsignoreCase(HUMConstants.sPastDue))
                            {
                                objOutput.dPastDueBalance = Decimal.valueof(obj.Amount);
                            }
                            else if(obj.Type.trim().equalsignoreCase(HUMConstants.sNonSSAPastDue))
                            {
                                objOutput.dPastDueBalanceNonSSA = Decimal.valueof(obj.Amount);
                            }
                           
                        }
                    }
                }
             }
          }
         catch(exception ex)
         {
            HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'getBalance');  
         }
        return objOutput;
    }
    
    /*
    * Method Name : getInvoiceDetails
    * Description : This method is used to retrieve invoice details from service response.
    * Return type : BillingProfile_DTO_HUM
    * Parameter   : List<BillingProfileResponseV2_DTO_HUM.InvoiceList> ,BillingProfile_DTO_HUM
    */
    public BillingProfile_DTO_HUM getInvoiceDetails(List<BillingProfileResponseV2_DTO_HUM.InvoiceSummary> lstInvoices,BillingProfile_DTO_HUM objOutput)
    {
      try
       {
        if(lstInvoices != null)
         {
           for(integer i=0;i<=lstInvoices.size()-1;i++)
            {
                objOutput.sInvoiceDueDate = formatDate(lstInvoices[i].DueDate);
                objOutput.dAmountDueFromLastInvoice = Decimal.Valueof(lstInvoices[i].TotalBalanceFwdAmt);
                objOutput.dTotalPaymentsReceived = Decimal.Valueof(lstInvoices[i].TotalPayments);
                objOutput.dAmountPastDue =Decimal.Valueof(lstInvoices[i].TotalBalanceFwdAmt)+Decimal.Valueof(lstInvoices[i].TotalPayments);
                objOutput.dPremiumsThisPeriod =Decimal.Valueof(lstInvoices[i].TotalCharges);
                objOutput.dMemberAdjustments = Decimal.Valueof(lstInvoices[i].TotalMemberAdjustments);
                objOutput.dFeesAndOtherAdjustments = Decimal.Valueof(lstInvoices[i].TotalOtherAdjustments);
                objOutput.dTotalAmountDue = Decimal.Valueof(lstInvoices[i].TotalDueAmt);
                objOutput.sCoverageBeginDate = formatDate(lstInvoices[i].InvoiceDate);
                objOutput.sCreateDate = formatDate(lstInvoices[i].CreatedDate); 
            }
         }
       }
       catch(exception ex)
       {
          HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'getInvoiceDetails');   
       }
        return objOutput;
    }
    
    /*
    * Method Name : getTransactionDetails
    * Description : This method is used to retrieve transaction details from service response.
    * Return type : BillingProfile_DTO_HUM
    * Parameter   : List<BillingProfileResponseV2_DTO_HUM.TransactionHistory> ,BillingProfile_DTO_HUM
    */
    public BillingProfile_DTO_HUM getTransactionDetails(List<BillingProfileResponseV2_DTO_HUM.TransactionDetails> lstTransactions,BillingProfile_DTO_HUM objOutput)
    {
        try
        {
          if(lstTransactions!= null)
            {
                for(integer i=0;i<=lstTransactions.size()-1;i++)
                {
                    objOutput.sAllocationDate = formatDate(lstTransactions[i].TransactionDate);
                    objOutput.dPaymentAmount = Decimal.valueof(lstTransactions[i].TransactionAmt);
                    objOutput.iCheckNumber = Integer.valueof(lstTransactions[i].CheckNbr);
                    objOutput.sPaymentType = lstTransactions[i].TransactionDesc;
                    objOutput.sReceivedDate = formatDate(lstTransactions[i].ReceivedDate);
                 }
            }
        }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'getTransactionDetails'); 
        }
        return objOutput;
    }
    
     /*    
    * Method Name   :    formatDate
    * Description   :    This method is used for retrieving date from String value.
    * Return Type   :    Date 
    * Parameters    :    String myDate
    */ 
    public String formatDate(String myDate) {
        try
        {
           String[] strDateOnly = myDate.split('-'); 
           Integer myIntDate = integer.valueOf(strDateOnly [2].substring(0,2));
           Integer myIntMonth = integer.valueOf(strDateOnly[1]);
           Integer myIntYear = integer.valueOf(strDateOnly[0]); 
           if(integer.valueOf(strDateOnly[0].substring(0,1)) ==  0)  return '';     
           else
               return myIntMonth +'/'+myIntDate +'/'+myIntYear ;
          
         }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'BillingProfileDetail_S2_HUM', 'formatDate'); 
        } 
        return null;
    }
}