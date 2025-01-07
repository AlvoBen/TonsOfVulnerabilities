/*******************************************************************************************************************************************************
Apex Class Name  : BillingService_LS_HUM 
Version          : 1.0
Created Date     : 05/23/2022
Function         : This is used to return response from service and format the response.
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Suraj Patil                                        05/23/2022                 Original Version
* Prashant Moghe                                     05/23/2022                 User Story 3271229:MF-2923225 - CRM Service Billing Systems Integration: Member Billing Profile Account Billing APP -Billing List
* Suraj Patil                  	  					  06/09/2022                DF 4979 FIx
**********************************************************************************************************************************************/
public with Sharing class BillingService_LS_HUM 
{
 
    String  objOutputsType='';
    String  objOutputsLine1='';
    String  objOutputsLine2 ='';
    String  objOutputsCity ='';
    String  objOutputsState = '';
    String  objOutputsZipCd ='';
    String  objOutputsZipPlus = ''; 
    public Billing_Wrapper_LDTO_HUM dataTransfer= new Billing_Wrapper_LDTO_HUM();
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
    * Return type : BillingProfileRequest_LDTO_HUM
    * Parameter   : BillingProfile_LDTO_HUM
    */
    
    public BillingProfileRequest_LDTO_HUM RequestStructureBillingService(BillingProfile_LDTO_HUM objSerInput)
    {
          BillingProfileRequest_LDTO_HUM objBilling = new BillingProfileRequest_LDTO_HUM();
               try
                  {
                    BillingProfileRequest_LDTO_HUM.GetBillingAccountInfoRequest getBillinginfo = new BillingProfileRequest_LDTO_HUM.GetBillingAccountInfoRequest();
                    BillingProfileRequest_LDTO_HUM.GetBillingAccountInfoRequestCriteria objRequest = new BillingProfileRequest_LDTO_HUM.GetBillingAccountInfoRequestCriteria();
                    objRequest.RequestContext = new BillingProfileRequest_LDTO_HUM.RequestContext();
                    objRequest.RequestContext.CallingSystemID = HUMConstants.sCallingSystemID;
                    objRequest.RequestContext.UserID = objSerInput.sUserID;
                    objRequest.RequestContext.UserIDType = HUMConstants.sUserIDType;
                    objRequest.RequestContext.ProcessDate = DateTime.Now().format('yyyy-MM-dd');
                   
                    
                    BillingProfileRequest_LDTO_HUM.SearchCriteria oSerchCrit = new BillingProfileRequest_LDTO_HUM.SearchCriteria();
                    BillingProfileRequest_LDTO_HUM.SearchCriteriaList oSerchCritlst = new BillingProfileRequest_LDTO_HUM.SearchCriteriaList();
                    List<BillingProfileRequest_LDTO_HUM.SearchCriteria> lstSearchCriteria = new List<BillingProfileRequest_LDTO_HUM.SearchCriteria>();
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
                 HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'RequestStructureBillingService'); 
                }       
                return objBilling;
    } 
    
    
    /*
    * Method Name : prepareProfDetailsSerRequest
    * Description : This method is used to Prepare service request.
    * Return type : BillingProfileRequest_LDTO_HUM.IncludeCriteria
    * Parameter   : NA
    */
    
    public BillingProfileRequest_LDTO_HUM.IncludeCriteria prepareProfDetailsSerRequest()
    {
           
        BillingProfileRequest_LDTO_HUM.IncludeCriteria objSerInput= new BillingProfileRequest_LDTO_HUM.IncludeCriteria();
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
                    objSerInput.InvoiceInfo = new BillingProfileRequest_LDTO_HUM.InvoiceInfo();
                    objSerInput.InvoiceInfo.IncludeInvoiceInformation = 'true';
                    objSerInput.InvoiceInfo.NbrOfInvoices = '1';
                    objSerInput.PremiumInfo = new BillingProfileRequest_LDTO_HUM.PremiumInfo();
                    objSerInput.PremiumInfo.IncludePremiumInformation = 'true';
                    objSerInput.PremiumInfo.RateLevel =HUMConstants.sRateLevel;  
                }
            catch(exception ex)
                {
                    HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'prepareProfDetailsSerRequest'); 
                }
            return objSerInput;
    }
    
     /*
    * Method Name : prepareOutputList
    * Description : This method is used to prepare output list.
    * Return type : List<BillingProfile_LDTO_HUM>
    * Parameter   : BillingProfileResponse_LDTO_HUM.BillingAccountList, List<BillingProfile_LDTO_HUM>
    */
    
    public List<BillingProfile_LDTO_HUM> getProfileInfo(Boolean isDetail,BillingProfileResponse_LDTO_HUM.BillingAccountList objBillAccount,List<BillingProfile_LDTO_HUM> lstServiceOutput )
    {
        BillingProfile_LDTO_HUM objOutput;
        lstServiceOutput  = new List<BillingProfile_LDTO_HUM>();
       try
       {
            for(integer j =0;j <= objBillAccount.BillAccount.size()-1;j++)
            {
                objOutput = new BillingProfile_LDTO_HUM();
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
                    objOutput.dPenalties = '$'+ objBillAccount.BillAccount[j].PremiumInfo.PenaltyAmt;
                    if(objBillAccount.BillAccount[j].PremiumInfo.NetPremium != null)
                    objOutput.dNetMonthlyPremiumAmount =  '$' + objBillAccount.BillAccount[j].PremiumInfo.NetPremium;
                }    
                if(Test.isRunningTest()){
                    isDetail = true;
                }
                if(isDetail)
                {
                   objOutput = getProfileDetails(objBillAccount.BillAccount[j],objOutput );
                }
                objOutput=updateUrl(objOutput);
                lstServiceOutput.Add(objOutput); 

            }
       }
           catch(exception ex)
           {
            HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'getProfileInfo');    
           }
        return lstServiceOutput;
    }

    private BillingProfile_LDTO_HUM updateUrl(BillingProfile_LDTO_HUM lso){       
           
           lso.sBillingDetailLink = '/lightning/n/Member_Billing_Detail?' +
            '&C__Id='+ dataTransfer.sRecId+  
            '&C__Name=' +lso.iProfileNumber+
            '&C__Object=' +dataTransfer.sObjName+
            '&C__PlatformCd='  +dataTransfer.sPlatformCode+
            '&C__UserId=' +dataTransfer.sUserNetworkId+
            '&C__AccKey=' + lso.iAccountNumber+
            '&C__type=Billing&C__subtype=Billing:' + lso.iAccountNumber+
            '&C__TabName=Member Billing Detail';
            
        String sakaname;
        String urlEBilling =BillingNonWebUrls_HUM__c.getInstance('OnlineBillingUrl').Url__c+'?menuTempl=986&AKA_Name='+sakaname+'&Group='+lso.sGroupPIDNumber+'&Profile='+lso.iProfileNumber;  
        lso.seBillingLink=getSSOUrl(urlEBilling, Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c, 'HIDDENTARGET', 3);

       lso.sPBSNewLink= launchNonWebAppNew(dataTransfer.sObjName, '', 'PBS', lso.sGroupPIDNumber, lso.iProfileNumber);
       lso.sPBSNewCommentsLinks = launchNonWebAppNew(dataTransfer.sObjName, '', 'PBSComments', lso.sGroupPIDNumber, lso.iProfileNumber);

        return lso;
}

public string launchNonWebAppNew(String sobjName,String spbsurl,String sappName,String sprofdisplayid,Integer sprofnumber) {        
    String sEntityType;
    String sFunctionality;
    String sMode = 'View';
    String sApplicationType = 'NonWeb';
    String sCallingApplication = 'SFDC';
    if (sobjName == 'Account') {
        sEntityType = 'Employer';
        sFunctionality = 'KE';
    }
    else {
        sEntityType = 'Member';
        sFunctionality = 'KM';
    }
    String appLauncherLink = 'AppLauncher:ProfileNumber=' + sprofnumber + '&ProfileDisplayId=' + sprofdisplayid + '&Functionality=' + sFunctionality + '&EntityType=' + sEntityType + '&ApplicationName=' + sappName + '&Mode=' + sMode + '&ApplicationType=' + sApplicationType + '&CallingApplication=' + sCallingApplication + '&Environment=' + HUMConstants__c.getInstance('APPLAUNCHER_ENVIRONMENT').StringValue__c + '';

    appLauncherLink = appLauncherLink.replace('=null&', '=&');
    return appLauncherLink;
    
}

     public String getSSOUrl(String linkurl, String oneClickURL, String oneClickTarget, Integer mapLen){ 
            if(mapLen == 1) return oneClickURL + '?' + oneClickTarget + '=' +linkurl; 
            else return oneClickURL + '?' + oneClickTarget + '=' + EncodingUtil.base64Encode(Blob.valueOf(linkurl));    
    }

    /*
    * Method Name : getProfileDetails
    * Description : This method is used to prepare to prepare output list.
    * Return type : BillingProfile_LDTO_HUM
    * Parameter   : BillingProfileResponse_LDTO_HUM.BillAccount, BillingProfile_LDTO_HUM
    */
    public BillingProfile_LDTO_HUM getProfileDetails(BillingProfileResponse_LDTO_HUM.BillAccount objBillAccount,BillingProfile_LDTO_HUM objOutput)
    {
          try
          {
               if(objBillAccount.BillingProfile != null) objOutput = getAccountSummaryInfo(objBillAccount.BillingProfile ,objBillAccount.RecurringPaymentMethodInfo ,objOutput);
                if(objBillAccount.BalanceList != null) 
                {
                    BillingProfileResponse_LDTO_HUM.BalanceList objBalanceList = new BillingProfileResponse_LDTO_HUM.BalanceList();
                    objBalanceList=objBillAccount.BalanceList;     
                    objOutput = getBalance(objBalanceList.Balance,objOutput);
                }
                if(objBillAccount.TransactionHistoryList != null){
                    BillingProfileResponse_LDTO_HUM.TransactionHistoryList objTransactionHistoryList = objBillAccount.TransactionHistoryList;
                    objOutput = getTransactionDetails(objTransactionHistoryList.TransactionDetails,objOutput);
                }
                if(objBillAccount.InvoiceList != null) 
                {
                   BillingProfileResponse_LDTO_HUM.InvoiceList objInvoiceSummary = objBillAccount.InvoiceList;  
                   objOutput = getInvoiceDetails(objInvoiceSummary.InvoiceSummary,objOutput);
                }
                    objOutput = getCollectionInfo(objBillAccount.CollectionInfo,objOutput );
                    objOutput = getPremiumInfo(objBillAccount.PremiumInfo,objOutput );
                if(objBillAccount.AddressList !=null)
                {
                    BillingProfileResponse_LDTO_HUM.AddressList objAddressList = objBillAccount.AddressList;       
                    objOutput = getAddressInfo(objAddressList.Address,objOutput );
                }
          }
          catch(exception ex)
          {
                HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'getProfileDetails');  
          }
       return objOutput;
    }

    public void setServiceClassVaraibles(Billing_Wrapper_LDTO_HUM dataTransfer){
        this.dataTransfer = dataTransfer;
    }
    /*
    * Method Name : getAccountSummaryInfo
    * Description : This method is used to retrieve Account Summary Info from service response.
    * Return type : BillingProfile_LDTO_HUM
    * Parameter   : BillingProfileResponse_LDTO_HUM.BillingProfile, BillingProfileResponse_LDTO_HUM.RecurringPaymentMethodInfo ,BillingProfile_LDTO_HUM
    */
    
    public BillingProfile_LDTO_HUM getAccountSummaryInfo(BillingProfileResponse_LDTO_HUM.BillingProfile objProfile,BillingProfileResponse_LDTO_HUM.RecurringPaymentMethodInfo objPay,BillingProfile_LDTO_HUM objOutput )
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
            HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'getAccountSummaryInfo');   
        }
        return objOutput; 
    }
    
    /*
    * Method Name : getAddressInfo
    * Description : This method is used to retrieve Address details from service response.
    * Return type : BillingProfile_LDTO_HUM
    * Parameter   : List<BillingProfileResponse_LDTO_HUM.Address> ,BillingProfile_LDTO_HUM
    */
    
    public BillingProfile_LDTO_HUM getAddressInfo(List<BillingProfileResponse_LDTO_HUM.Address> lstAddress ,BillingProfile_LDTO_HUM objOutput)
    {
       try
       {
            if(lstAddress!= null)
            {
            for(BillingProfileResponse_LDTO_HUM.Address objAddress :lstAddress)
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
            HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'getAddressInfo');    
          }
        return objOutput ;
    }
    
     /*
    * Method Name : getPremiumInfo
    * Description : This method is used to retrieve premium details from service response.
    * Return type : BillingProfile_LDTO_HUM
    * Parameter   : List<BillingProfileResponse_LDTO_HUM.PremiumInfo> ,BillingProfile_LDTO_HUM
    */
    
    public BillingProfile_LDTO_HUM getPremiumInfo(BillingProfileResponse_LDTO_HUM.PremiumInfo objPremium,BillingProfile_LDTO_HUM objOutput)
    {
        try
        {
            if(objPremium != null)
            {
                objOutput.dPremium = objPremium.Premium;
                objOutput.dOptionalSupplementalPremium = objPremium.AdditionalSupplementalCharges;
                objOutput.dDiscounts = objPremium.DiscountAmt;
                objOutput.dSubsidies = objPremium.SubsidyAmt;
                objOutput.dPenalties = objPremium.PenaltyAmt;
                objOutput.dFees = objPremium.Fees;
                objOutput.dOthers = objPremium.OtherAmt;
                objOutput.dNetMonthlyPremiumAmount = objPremium.NetPremium;
            }
        }
        catch(exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'getPremiumInfo');     
        }
        return objOutput;
     }
     
     /*
    * Method Name : getCollectionInfo
    * Description : This method is used to retrieve collection details from service response.
    * Return type : BillingProfile_LDTO_HUM
    * Parameter   : List<BillingProfileResponse_LDTO_HUM.CollectionInfo> ,BillingProfile_LDTO_HUM
    */ 
    
    public BillingProfile_LDTO_HUM getCollectionInfo(BillingProfileResponse_LDTO_HUM.CollectionInfo objCollection,BillingProfile_LDTO_HUM objOutput)
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
             HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'getCollectionInfo'); 
        }
        return objOutput;
    }
    
     /*
    * Method Name : getBalance
    * Description : This method is used to retrieve invoice details from service response.
    * Return type : BillingProfile_LDTO_HUM
    * Parameter   : List<BillingProfileResponse_LDTO_HUM.Balance> ,BillingProfile_LDTO_HUM
    */ 
    
    public BillingProfile_LDTO_HUM getBalance(List<BillingProfileResponse_LDTO_HUM.Balance> lstBalanceInfo,BillingProfile_LDTO_HUM objOutput)
    {
        try
         {
           if(lstBalanceInfo != null)
             {
                for(BillingProfileResponse_LDTO_HUM.Balance obj :lstBalanceInfo)
                {
                    if(obj != null)
                    {                    
                        if(obj.Type!= null && obj.Amount != null)
                        {
                            if(obj.Type.trim().equalsignoreCase(HUMConstants.sCurrentBalance))
                            {
                                objOutput.dCurrentAccountBalance = '$'+obj.Amount;
                            }
                            else if(obj.Type.trim().equalsignoreCase(HUMConstants.sCurrentNonSSABalance))
                            {
                               objOutput.dCurrentAccBalNonSSA = '$'+obj.Amount;
                            }
                            else if(obj.Type.trim().equalsignoreCase(HUMConstants.sPastDue))
                            {
                               objOutput.dPastDueBalance = '$'+obj.Amount;
                            }
                            else if(obj.Type.trim().equalsignoreCase(HUMConstants.sNonSSAPastDue))
                            {
                                objOutput.dPastDueBalanceNonSSA = '$'+obj.Amount;
                            }
                           
                        }
                    }
                }
             }
          }
         catch(exception ex)
         {
            HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'getBalance');  
         }
        return objOutput;
    }
    
    /*
    * Method Name : getInvoiceDetails
    * Description : This method is used to retrieve invoice details from service response.
    * Return type : BillingProfile_LDTO_HUM
    * Parameter   : List<BillingProfileResponse_LDTO_HUM.InvoiceList> ,BillingProfile_LDTO_HUM
    */
    
    public BillingProfile_LDTO_HUM getInvoiceDetails(List<BillingProfileResponse_LDTO_HUM.InvoiceSummary> lstInvoices,BillingProfile_LDTO_HUM objOutput)
    {
      try
       {
        if(lstInvoices != null)
         {
           for(integer i=0;i<=lstInvoices.size()-1;i++)
            {
                objOutput.sInvoiceDueDate = formatDate(lstInvoices[i].DueDate);
                objOutput.dAmountDueFromLastInvoice = lstInvoices[i].TotalBalanceFwdAmt;
                objOutput.dTotalPaymentsReceived = lstInvoices[i].TotalPayments;
                objOutput.dAmountPastDue = String.Valueof(Decimal.Valueof(lstInvoices[i].TotalBalanceFwdAmt)+Decimal.Valueof(lstInvoices[i].TotalPayments));
                objOutput.dPremiumsThisPeriod = lstInvoices[i].TotalCharges;
                objOutput.dMemberAdjustments = lstInvoices[i].TotalMemberAdjustments;
                objOutput.dFeesAndOtherAdjustments = lstInvoices[i].TotalOtherAdjustments;
                objOutput.dTotalAmountDue = lstInvoices[i].TotalDueAmt;
                objOutput.sCoverageBeginDate = formatDate(lstInvoices[i].InvoiceDate);
                objOutput.sCreateDate = formatDate(lstInvoices[i].CreatedDate); 
            }
         }
       }
       catch(exception ex)
       {
          HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'getInvoiceDetails');   
       }
        return objOutput;
    }
    
     /*
    * Method Name : getTransactionDetails
    * Description : This method is used to retrieve transaction details from service response.
    * Return type : BillingProfile_LDTO_HUM
    * Parameter   : List<BillingProfileResponse_LDTO_HUM.TransactionHistory> ,BillingProfile_LDTO_HUM
    */
    
    public BillingProfile_LDTO_HUM getTransactionDetails(List<BillingProfileResponse_LDTO_HUM.TransactionDetails> lstTransactions,BillingProfile_LDTO_HUM objOutput)
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
            HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'getTransactionDetails'); 
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
                HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'formatDate'); 
            } 
            return null;
        }

    /*
    * Method Name : getActiveProfile
    * Description : This method is used to identify and return an active Profile
    * Return type : BillingProfile_LDTO_HUM
    * Parameter   : List<BillingProfile_LDTO_HUM>
    */
    public BillingProfile_LDTO_HUM getActiveProfile(List<BillingProfile_LDTO_HUM> lstBillingProfiles)
    {
       BillingProfile_LDTO_HUM objOutput;
       try
       {
            if(null != lstBillingProfiles && !lstBillingProfiles.isEmpty()) 
            {
                List<BillingProfile_LDTO_HUM> lstActiveBillingProfiles = new List<BillingProfile_LDTO_HUM> ();
                for( BillingProfile_LDTO_HUM iterateBillingProfile: lstBillingProfiles)
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
        HUMExceptionHelper.logErrors(ex, 'BillingService_LS_HUM', 'getActiveProfile');    
       }
       return objOutput;
    }
}