/*******************************************************************************************************************************************************
Apex Class Name  : BillingDetails_LC_HUM
Version          : 1.0
Created Date     : 05/24/2022
Function         : This is controller class for LWC billingProfileDetailsLWC_HUM
                    This is used to display billing details.
*****************************************************************************************************************************************************
Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Suraj patil            		                    05/24/2022                 Original Version
* Raj Paliwal										12/22/2022				   User Story 4003680:MF 3624855-CRM Service Billing Systems Integration: Lightning- Billing Info/Error/Toast Messages
**********************************************************************************************************************************************/
public with sharing class BillingDetails_LC_HUM 
{
    public BillingProfileResponse_LDTO_HUM objResponse = Null;
    public Static BillingService_LS_HUM sbilling = new BillingService_LS_HUM();
    public Static String sRequest;
    public Static List<BillingProfile_LDTO_HUM > lstServiceOutput {get;set;}
       
   /*
    * Method Name : getProfileDetails
    * Description : This continuation method is used to retrieve detailed information 
                        of the selected billing profile from service 
    * Return type : Object 
    * Parameter   : NA
    */
    @AuraEnabled(Continuation=true)
    public Static Object getProfileDetails(String PlatformCd, String sUserId, String sAccNumber)
    {   
        BillingProfile_LDTO_HUM  objInput;
        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('BillingServiceV2');
        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        con.continuationMethod = 'parseBillingServiceResponse';
        try
        {
            objInput = getBillingProfilesInput(sUserId,PlatformCd,sAccNumber,'ACCOUNT','');
            HttpRequest httpBilling = sbilling.builtHttpRequest(RSServiceDetails);
            httpBilling.setBody(JSON.serialize(sbilling.RequestStructureBillingService(objInput)));
            sRequest=con.addHttpRequest(httpBilling);
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'BillingDetails_LC_HUM', 'getProfileDetails');
        }
        return con;
    }
    
    /*
    * Method Name : getBillingProfilesInput
    * Description : This method is used to prepare service input.
    * Return type : BillingProfile_LDTO_HUM 
    */
    public static BillingProfile_LDTO_HUM  getBillingProfilesInput(String sUserId, String sPlatformCode,String sSearchID,String sIdType,String sObjName)
    {
        HUM_Webservice_Callout__c obj = HUM_Webservice_Callout__c.getInstance('BillingServiceV2');
        BillingProfile_LDTO_HUM  objSerInput = new BillingProfile_LDTO_HUM ();
        objSerInput.iTimeout = ((obj != null && obj.Timeout__c != null) ? Integer.ValueOf(obj.Timeout__c)/1000:10);
        objSerInput.sUserID=sUserId;
        objSerInput.sPlatformCode = sPlatformCode;
        objSerInput.sIDType = sIdType ;
        objSerInput.sSearchID = sSearchID ;
        return objSerInput;
    }
       
    /*
    * Method Name : parseBillingServiceResponse
    * Description : This method is used to process service output.
    * Return type : Void
    * Parameter   : NA
    */
    @AuraEnabled
    public Static List<BillingProfile_LDTO_HUM> parseBillingServiceResponse(List<string> labels, Object State)
    {
        lstServiceOutput = new List<BillingProfile_LDTO_HUM >();
        try
        {
          HttpResponse responseBillingSummary = Continuation.getResponse(labels[0]);
          String BillingResponseBody = responseBillingSummary.getBody();
          BillingProfileResponse_LDTO_HUM billingRes ;
          String sError = System.Label.BillingProfileServiceError_Account_HUM;
          if (!BillingResponseBody.containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && responseBillingSummary != null && ((responseBillingSummary.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || responseBillingSummary.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
          {
                billingRes = (BillingProfileResponse_LDTO_HUM) System.JSON.deserialize(BillingResponseBody, BillingProfileResponse_LDTO_HUM.class);
                BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponse objBillAcc = new BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponse();
                objBillAcc=billingRes.GetBillingAccountInfoResponse;
                BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponseList objBillAccList = new BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponseList();
                objBillAccList=objBillAcc.GetBillingAccountInfoResponseList;
                List<BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponseDetail> objBillAccListDetail = new List<BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponseDetail>();
                objBillAccListDetail =objBillAccList.GetBillingAccountInfoResponseDetail ;
                if(objBillAccListDetail != null && !objBillAccListDetail .isEmpty())
                {        
                    for(integer i =0;i<objBillAccListDetail.size();i++)
                    {    
                        BillingProfileResponse_LDTO_HUM.BillingAccountList billAccList = objBillAccListDetail[0].BillingAccountList;
                        if(billAccList != null && billAccList.BillAccount != null)
                        {
                            lstServiceOutput = sbilling.getProfileInfo(true,billAccList ,lstServiceOutput);
							if(lstServiceOutput == NULL)
                                {
                                    return null;
                                }
                        }
                    }
                } 
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.bShowErrorOnPage =false;
            HUMExceptionHelper.logErrors(ex, 'BillingDetails_LC_HUM', 'parseBillingServiceResponse');
			return null;
        }
        return lstServiceOutput;
    }
}