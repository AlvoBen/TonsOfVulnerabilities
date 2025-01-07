/*******************************************************************************************************************************************************
Apex Class Name  : BillingService_S_HUM 
Version          : 1.0
Created Date     : 11/05/2019
Function         : This is used to return response from service and format the response.
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Vandana Chaudhari          243694                        06/01/2018                Original Version
* Sumit Pharande                                           07/10/2018                REQ-352427
* Sumit Pharande                                           11/05/2019                REQ - 412009 Converting Billing SOAP to Rest
* Amit Agarwal                                             05/26/2020                User Story 1114810: PR00091574 - MF 1 - AHT - Ability to display additional information on Billing Profile Summary page (RF)
* Amar Gadewar                                             06/18/2020                User Story 1203617: PR00091574 - MF 1 - AHT - Ability to automatically launch Billing Profile Detail page while accessing Billing Profile Summary page (RF)
* Amit Agarwal                                             06/23/2020                User Story 1204983: PR00091574 - MF 1 - Quality - Billing Profile Summary page updates (RF)
* Atia Uzma                                                10/06/2020                User Story 1564000: PR00091574 - MF 1 - INC0621496 fix - Ability to log on Payment Option field on Billing Profile Summary page (CRM)
* Prasanthi Kandula                                        07/26/2021                US 2484356 T1PRJ0036776_MF_3SF : Member Billing Profile Page - Profile Selection
**********************************************************************************************************************************************/
public with Sharing class BillingService_S_HUM 
{
 
    String  objOutputsType='';
    String  objOutputsLine1='';
    String  objOutputsLine2 ='';
    String  objOutputsCity ='';
    String  objOutputsState = '';
    String  objOutputsZipCd ='';
    String  objOutputsZipPlus = ''; 
    
    /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        if (ServiceObj != null && ServiceObj.Active__c)
        {
            request.setMethod(ServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GLOBAL_CONSTANT_HUM.Content_type_HUM, ServiceObj.Content_Type__c);
            request.setHeader(GLOBAL_CONSTANT_HUM.Connection_HUM, GLOBAL_CONSTANT_HUM.Keep_alive_HUM);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(GLOBAL_CONSTANT_HUM.SOAPAction_HUM,'');
        }
        return request;
    }
    
    /*
    * Method Name : prepareServiceRequest
    * Description : This method is used to Prepare service request.
    * Return type : BillingProfileRequestV2_DTO_HUM
    * Parameter   : BillingProfile_DTO_HUM
    */

    public BillingProfileRequestV2_DTO_HUM RequestStructureBillingService(BillingProfile_DTO_HUM objSerInput)
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
                 HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'RequestStructureBillingService'); 
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
                    HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'prepareProfDetailsSerRequest'); 
                }
            return objSerInput;
    }
    
     /*
    * Method Name : prepareOutputList
    * Description : This method is used to prepare output list.
    * Return type : List<BillingProfile_DTO_HUM>
    * Parameter   : BillingProfileResponseV2_DTO_HUM.BillingAccountList, List<BillingProfile_DTO_HUM>
    */
    
    public List<BillingProfile_DTO_HUM> getProfileInfo(Boolean isDetail,BillingProfileResponseV2_DTO_HUM.BillingAccountList objBillAccount,List<BillingProfile_DTO_HUM> lstServiceOutput )
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
                objOutput.sPartyKey = objBillAccount.BillAccount[j].BillingProfile.PartyKey;
                if(objBillAccount.BillAccount[j].RecurringPaymentMethodInfo != null){
                    if(objBillAccount.BillAccount[j].RecurringPaymentMethodInfo.PaymentOption != null) {
                    objOutput.sRecurringPaymentOption = objBillAccount.BillAccount[j].RecurringPaymentMethodInfo.PaymentOption;
		        if(String.isNotBlank(objOutput.sRecurringPaymentOption)) {
                            Map<String, String> mapRecurringPaymentOption = new Map<String, String>
                            {
                                'ACH'   =>  'Bank Account',
                                'CB'    =>  'Coupon Book',
                                'SSA'   =>  'Social Security Administration',
                                'CC'    =>  'Credit Card',
                                'RRB'   =>  'Railroad Retirement Board',
                                'DC'    =>  'Debit Card',
                                'NB'    =>  'No Payment Option'
                            };

                            if(mapRecurringPaymentOption.containsKey(objOutput.sRecurringPaymentOption.trim())) {
                                objOutput.sRecurringPaymentOptionDescription = mapRecurringPaymentOption.get(objOutput.sRecurringPaymentOption.trim());
                            }
                        }
                    }
                    if(objBillAccount.BillAccount[j].RecurringPaymentMethodInfo.NextRecurringDate != null)
                    objOutput.sNextRecurringDate = formatDate(objBillAccount.BillAccount[j].RecurringPaymentMethodInfo.NextRecurringDate);
                }
                if(objBillAccount.BillAccount[j].BillingProfile != null){
                    if(objBillAccount.BillAccount[j].BillingProfile.PaidThroughDate != null)
                    objOutput.sPaidThruDate = formatDate(objBillAccount.BillAccount[j].BillingProfile.PaidThroughDate);
                }
                if(objBillAccount.BillAccount[j].BalanceList != null) {
                        objOutput = getBalance(objBillAccount.BillAccount[j].BalanceList.Balance,objOutput);
                }               
				if(objBillAccount.BillAccount[j].PremiumInfo != null){
                    if(objBillAccount.BillAccount[j].PremiumInfo.PenaltyAmt != null)
                    objOutput.dPenalties =  Decimal.valueOf(objBillAccount.BillAccount[j].PremiumInfo.PenaltyAmt);
				    if(objBillAccount.BillAccount[j].PremiumInfo.NetPremium != null)
                    objOutput.dNetMonthlyPremiumAmount =  Decimal.valueOf(objBillAccount.BillAccount[j].PremiumInfo.NetPremium);
                }	               
                if(isDetail)
                {
                   objOutput = getProfileDetails(objBillAccount.BillAccount[j],objOutput );
                }
                lstServiceOutput.Add(objOutput);            
            }
       }
           catch(exception ex)
           {
            HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'getProfileInfo');    
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
                HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'getProfileDetails');  
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
            HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'getAccountSummaryInfo');   
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
            HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'getAddressInfo');    
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
             HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'getPremiumInfo');     
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
             HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'getCollectionInfo'); 
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
            HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'getBalance');  
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
          HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'getInvoiceDetails');   
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
            HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'getTransactionDetails'); 
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
                HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'formatDate'); 
            } 
            return null;
        }

    /*
    * Method Name : getActiveProfile
    * Description : This method is used to identify and return an active Profile
    * Return type : BillingProfile_DTO_HUM
    * Parameter   : List<BillingProfile_DTO_HUM>
    */
    public BillingProfile_DTO_HUM getActiveProfile(List<BillingProfile_DTO_HUM> lstBillingProfiles)
    {
       BillingProfile_DTO_HUM objOutput;
       try
       {
            if(null != lstBillingProfiles && !lstBillingProfiles.isEmpty()) 
            {
                List<BillingProfile_DTO_HUM> lstActiveBillingProfiles = new List<BillingProfile_DTO_HUM> ();
                for( BillingProfile_DTO_HUM iterateBillingProfile: lstBillingProfiles)
                {
                    if(null != iterateBillingProfile && String.isNotBlank(iterateBillingProfile.sStatus) && ( 'Active' == iterateBillingProfile.sStatus))
                    {
                        lstActiveBillingProfiles.add(iterateBillingProfile);
                    }
                }

                if(!lstActiveBillingProfiles.isEmpty() && lstActiveBillingProfiles.size() == 1) {

                    objOutput = lstActiveBillingProfiles[0];
                }
            }
       }
       catch(exception ex) {
        HUMExceptionHelper.logErrors(ex, 'BillingService_S_HUM', 'getActiveProfile');    
       }
       return objOutput;
    }
}