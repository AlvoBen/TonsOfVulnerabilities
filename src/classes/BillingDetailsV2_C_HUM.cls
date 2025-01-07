/*******************************************************************************************************************************************************
Apex Class Name  : BillingDetailsV2_C_HUM
Version          : 1.0
Created Date     : 12/15/2015
Function         : This is controller class for visual force component 'BillingDetails_CMP_HUM'.
                    This is used to display billing details.
Test Class       : BillingDetailsV2_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            29052                    12/15/2015                 Original Version
* Santhi Mandava            29422                    01/19/2016                 Implemented billing phase 2 stories. 
* Pallavi Ravishankar       36948                    08/23/2016                 Modified Code as per REQ 277217 & REQ 279610.
* Praveen Kumar Parimi      36948                    08/29/2016                 Incorporated the review comments for review #36948.
* Prasanthi Kandula         40488                    09/08/2016                 REQ - 277219 Reporting Billing Detail pages using StandardMap.
* Sumit Pharande            243694                   6/13/2018                  Billing Service V2 custom setting
* Sumit Pharande                                     11/05/2019                 REQ - 412009 Billing Service SOAP to Rest conversion
* Harish Gowda                                       02/24/2020                 USER STORY 889485 PR00091574 - MF 1 - AHT - Add jump links on Billing Profile Detail page (RF)
* Amar Gadewar                                       03/19/2020                 USER STORY 939165 PR00091574 - MF 1 - AHT - Add jump links on Billing Profile Detail page (CRM)
* Santhi Mandava                                     04/03/2020                 US - 995046 Removed Rapid force switch
* Sayali Gavane										 02/10/2021					REQ -1625471 PR00094226 - CR AEP readiness -Research - Critical Security Issues (slide 11)		
* Y Sunil Kumar Reddy				     			 04/20/2021					User Story 2112364: T1PRJ0001827 - MF 1 - AHT - Add left hand link panel and links on Billing Profile detail page (RF)
* Atia Uzma                                          06/11/2021                 User Story 2347966: T1PRJ0001827 - MF 1 - AHT - Add left hand link panel and links on Billing Profile detail page (CRM)
* Nirmal Garg										 09/16/2021					Added browser logic for coveo
* Nirmal Garg										 10/22/2021					Defefct  3946 coveo only for member billing
* 	 Nirmal Garg									  11/11/2021			Added CRMS switch for coveo
**********************************************************************************************************************************************/
public with sharing class BillingDetailsV2_C_HUM extends SObjectDescriptorImpl_H_HUM
{
    public BillingProfileResponseV2_DTO_HUM objResponse = Null;
    public BillingService_S_HUM sbilling = new BillingService_S_HUM();
    public String  sObjName{get;set;}
    public String  sName{get;set;}
    public String  sPlatformCd{get;set;}
    public String  sSectionHeader{get;set;}
    public Boolean isProfileDetailsVisible{get;set;}
    public Boolean bShowError{get;set;}
    public BillingProfile_DTO_HUM oProfileDetails{get;set;}
    public String sObjId {get; set;}
    public string sAccNumber {get; set;}
    public String standardMapvalue {get; set;}
    public Map<String, Map<String, Object>> standardMapForLogging = new Map<String, Map<String, Object>>();
    public String sRequest;
    public List<BillingProfile_DTO_HUM> lstServiceOutput {get;set;}
    public list<string> lstFloatLinkelem{get;set;}
    public boolean bCoveoUser {get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';
    /*
    * Method Name : BillingDetailsV2_C_HUM
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : NA
    */
    public BillingDetailsV2_C_HUM()
    {
        sObjId = ApexPages.currentPage().getParameters().get('Id');
        sObjName = ApexPages.currentPage().getParameters().get('Object');
        sName = ApexPages.currentPage().getParameters().get('Name');
        sAccNumber = ApexPages.currentPage().getParameters().get('AccKey');
        if(sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM){sSectionHeader=System.Label.BillingGroupProfileDetail;}
        else{sSectionHeader=System.Label.BillingMemberProfileDetail;}
        
        createFloatingList();
		string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
		if(string.isNotBlank(sBrowserContext) && sObjName != GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}
    }
    
     /*
	 * This method Is to create the list of floating links which are visible at the top of the customized Policy Detail VF pages 
	 * @param  NA
	 * @return void
	 */
    public void createFloatingList()
    {
        lstFloatLinkelem = new list<string>();
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkMemberBillingprofile1);   //Profile Account Information .
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkMemberBillingprofile2);   //Policy DetailsPayer Information .
		lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkMemberBillingprofile3);   //Balance Information .
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkMemberBillingprofile4);   //Last Payment Information .
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkMemberBillingprofile5);   //Collection Information .
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkMemberBillingprofile6);   //Rate Information .
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkMemberBillingprofile7);   //Legacy Contact HistoryLast Invoice Summary
    }
    
   /*
    * Method Name : getProfileDetails
    * Description : This continuation method is used to retrieve detailed information 
                        of the selected billing profile from service 
    * Return type : Object 
    * Parameter   : NA
    */
    public Object getProfileDetails()
    {   
        BillingProfile_DTO_HUM objInput;
        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('BillingServiceV2');
            // Create continuation with a timeout
        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        con.continuationMethod = GLOBAL_CONSTANT_HUM.ParseBillingServiceResponse;
        try
        {
            oProfileDetails = new BillingProfile_DTO_HUM();
            sPlatformCd =  ApexPages.currentPage().getParameters().get('PlatformCd');
            String sUserId=ApexPages.currentPage().getParameters().get('UserId');
            objInput = BillingProfileDetailsV2_H_HUM.getBillingProfilesInput(sUserId.escapeHtml4(),sPlatformCd.escapeHtml4(),sAccNumber.escapeHtml4(),'ACCOUNT',sObjName.escapeHtml4());
            HttpRequest httpBilling = sbilling.builtHttpRequest(RSServiceDetails);
            httpBilling.setBody(JSON.serialize(sbilling.RequestStructureBillingService(objInput)));
            sRequest=con.addHttpRequest(httpBilling);
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'BillingDetailsV2_C_HUM', 'getProfileDetails');
        }
        return con;
    }
       
    /*
    * Method Name : parseBillingServiceResponse
    * Description : This method is used to process service output.
    * Return type : Void
    * Parameter   : NA
    */
    public void parseBillingServiceResponse()
    {
        lstServiceOutput = new List<BillingProfile_DTO_HUM>();
        
        try
        {
          
          HttpResponse responseBillingSummary = Continuation.getResponse(sRequest);
          String BillingResponseBody = responseBillingSummary.getBody();
          BillingProfileResponseV2_DTO_HUM billingRes ;
          String sError = System.Label.BillingProfileServiceError_Account_HUM;
          if (!BillingResponseBody.containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && responseBillingSummary != null && ((responseBillingSummary.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || responseBillingSummary.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
          {
              billingRes = (BillingProfileResponseV2_DTO_HUM) System.JSON.deserialize(BillingResponseBody, BillingProfileResponseV2_DTO_HUM.class);
              BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponse objBillAcc = new BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponse();
                objBillAcc=billingRes.GetBillingAccountInfoResponse;
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
                                lstServiceOutput = sbilling.getProfileInfo(true,billAccList ,lstServiceOutput);
                                if(lstServiceOutput == NULL)
                                {
                                    displayErrorMessage(true , System.Label.BillingProfileDetailServiceError_Account_HUM);
                                    
                                }
                                else
                                {
                                    displayErrorMessage(false,'');
                                 
            oProfileDetails = lstServiceOutput[0];
            standardMapForLogging.put('DisplayID', new Map<String, Object>{'Group/PID Number' => oProfileDetails.sGroupPIDNumber});
            standardMapForLogging.put('ProfileSequenceNbr', new Map<String, Object>{'Profile Number' => oProfileDetails.iProfileNumber});
            standardMapForLogging.put('AccountKey', new Map<String, Object>{'Account Number' => oProfileDetails.iAccountNumber});
            standardMapForLogging.put('ProfileName', new Map<String, Object>{'Profile/Account Name' => oProfileDetails.sProfileAccountName});
            standardMapForLogging.put('EffectiveDate', new Map<String, Object>{'Effective Date' => oProfileDetails.sEffectiveDate});
            standardMapForLogging.put('EndDate', new Map<String, Object>{'End Date' => oProfileDetails.sEndDate});
            standardMapForLogging.put('ProfileStatusCD', new Map<String, Object>{'Profile Status' => oProfileDetails.sStatus});
            standardMapForLogging.put('EndReason', new Map<String, Object>{'Term Reason' => oProfileDetails.sTermReasonCode});
            standardMapForLogging.put('PaymentOption', new Map<String, Object>{'Recurring Payment Option' => oProfileDetails.sRecurringPaymentOption});
            standardMapvalue = JSON.serialize(standardMapForLogging);
            
                                    
                                }
                                    
                            }
                    }
                } 
            }
            }
        catch(Exception ex)
        {
            HUMExceptionHelper.bShowErrorOnPage =false;
            HUMExceptionHelper.logErrors(ex, 'BillingDetailsV2_C_HUM', 'parseBillingServiceResponse');
            HUMExceptionHelper.bShowErrorOnPage =true;
            displayErrorMessage(true, System.Label.BillingProfileDetailServiceError_Account_HUM);
        }
    }
    
   
    
    /*
    * Method Name : displayErrorMessage
    * Description : This method is used to display error messages on UI.
    * Return type : Void
    * Parameter   : Boolean , String
    */
    @testVisible private void displayErrorMessage(Boolean bMemberError, String sError)
    {
        If(bMemberError) 
        {
            bShowError = bMemberError;
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Error,sError,true);
            isProfileDetailsVisible = false; 
        }
        else
        {
            isProfileDetailsVisible = true;
        }
    }
    /**
    * Returns this instance of the class
    * <p>
    * Method to store the Current Page Instance for Side Panel
    * 
    * @return this
    */
    public BillingDetailsV2_C_HUM getThis()
    {
        return this;
    }
    /**
     * Returns List<sObject> of record
     * <p>
     * Virtual required Method
     *
     * @return NULL
     */
    public List < sObject > getRecords()
    {
        return NULL;
    }

    /**
     * Returns sObject of record
     * <p>
     * Virtual required Method
     *
     * @return NULL
     */
    public sObject getRecord()
    {
        return NULL;
    }

}