/*****************************************************************************************
Apex class Name    : AuthSummary_C_HUM
Version            : 1.0
Function           : Controller to VF Page: AuthSummary_VF_Hum
                     This class is used to display the list of Authorizations on VF page.
                     As Security class is a Helper class to this, the Parent Class should also be Without Sharing as part of security setting to filter Authorizations,
                     which enable CCS Associates to get values for those records(GroupNumber) which they don't have access to.
Created Date       : 15 Dec 2015
Test Class         : Authsummary_T_HUM 

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Sruthi Adivikolanu           29042                 12/15/2015          Original Version REQ - 170232:Ability to read and search for all medical authorizations 
                                                                            and referrals within CRM Service - Summary View
 *   Ranjeeth N                   29042                 01/06/2016          REQ - 236995:Authorization Summary Page: Ability to search
                                                                            for authorization/referral results 
 *   Sruthi A                     29646                 01/23/2016          Applied Securityon search of Authrization.   
 *   Ranjeeth N                   29646                 01/27/2016          Updated on review comments and  as per the coding standards                                                                              
 *   Sruthi  A                    29646                 02/01/2016          Moved  constants to the static class   
 *   Prasanthi Kandula            30647                 03/24/2016          REQ - 196131: Interaction checkbox on Auth Summary page
 *   Prasanthi Kandula                                  04/06/2016          Defect #221363.0001 fix, to get the correct Interaction log
 *   Praveen Kumar Parimi         28468                 05/26/2016          Removed fetchInteractionId() method since Req#196131 is cancelled.
 *   Santhi Mandava               31411                 06/29/2016          Fixed defect 231416.0001.
 *   Praveen Parimi/Prasanthi     33128                 07/18/2016          Defect fix for D-00471.
 *   HarikaDevi Kalidindi         36595                 08/24/2016          Defect fix: 238145.0001 Added getMemberName method to fetch the Name instead of getting from URL 
 *   Santhi Mandava               40488                 09/20/2016          Added logic to Retrieve enterprise id from account object.
 *   Sharan Shanmugam                                   07/07/2017          Added logic to display PromptPA button on AuthSummary page
 *   Joel George                  125710                09/05/2017          Removed Prompt PA Link as per Req 322460
 *   Sharan Shanmugam                                   09/22/2017          Modified for REQ - 329665 
 *   Sharan Shanmugam                                   10/31/2017          Modified for REQ - 335303
 *   Sai Ayyagari                 184451                02/21/2018          REQ - 350853 Fix for HUMExceptionHelper, store the list of exceptions capture
 *   Prudhvi Pamarthi                                   02/18/2019          REQ - 361155 - Not allowing case creation on Legacy Deleted policies
 *   Imran                         416071               12/03/2019          Implemented the swith condition to handle both old And New service. 
 *   Kapil Arora                                        04/17/2020          US#1021296 MF 1 - AHT - Ability to launch multiple line items from Claim Summary and Auth/Referral Summary Pages (RF)
 *   Vinay Lingegowda                                   04/29/2020          USER STORY 1038532 - Add Clear Filter button for Medical Authorizations section of Auth/Referral Summary page (RF)
 *   Sunil Kumar Reddy Y                                04/30/2020          User Story 1062501: PR00091574 - MF 1 - Quality - Display informational message at the top of Claim and Auth/Referral Summary pages to provide guidance about CTRL+Click feature (RF)
 *   Amar Gadewar                                       05/21/2020          User Story 1114771: PR00091574 - MF 1 - AHT - Add Clear Filter button for Medical Authorizations section of Auth/Referral Summary page (CRM)
 *   Amar Gadewar                                       06/02/2020          User Story 1145819 : PR00091574 - MF 1 - Quality - Display informational message at the top of Claim and Auth/Referral Summary pages to provide guidance about CTRL+Click feature (CRM)
 *   Amar Gadewar                                       06/03/2020          User Story 1145807: PR00091574 - MF 1 - AHT - Ability to launch multiple line items from Claim Summary and Auth/Referral Summary Pages (CRM)
 *   Suresh Kokkeragadda                                06/24/2020          User Story 1203671: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 07/07 and 07/17 releases (CRM)
 * Pradeep Kumar Yadav								    07/22/2020	        User Story 1248496: CA ticket- 9228963 - CRM Service: Collection size 1, 050 exceeds to maximum size of 1000
 * Riya Agarwal                                         01/05/2021          REQ - 1835131 - PR00095212- MF1 -SF -Auto-Populate Custom Benefit Grid
 * Trina Ghosh                                          02/09/2021          US:1669845 - Low Security Fixes
 * Rahul Chaurasia									    04/06/2021			User Story 1279935: PR00094226 - SF- Auth/Referral Summary old version Decommission
 * Rahul Chaurasia									    19/06/2021			User Story 1895286: T1PRJ0002736 - 2021 MF6  - CR AEP readiness - Low Security Issues 
 * Anuradha Gajbhe									    06/18/2021			User Story 2315210: MF 6 - SF - Tech - Performance Tuning - Authorization Summary and Detail Pages
 * Nirmal Garg											09/16/2021			Added browser logic for coveo
 * 	 Nirmal Garg									  11/11/2021			Added CRMS switch for coveo
****************************************************************************************************************************/
Public with sharing class AuthSummary_C_HUM extends SObjectDescriptorImpl_H_HUM {
    private AuthSummary_S_HUM oServiceAuth = new AuthSummary_S_HUM(); 
    @TestVisible private String sRequest ='';
    @TestVisible private List<AuthRefferal_Wrapper_DTO_HUM> lstAuthResponse = new List<AuthRefferal_Wrapper_DTO_HUM>();
    private AuthReferral_Query_D_HUM oAuthrefQuery = new AuthReferral_Query_D_HUM();
	public boolean bCoveoUser {get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';
    Public List<Error_Log__c> listErrorLogs
    {
        get
        {
          if(listErrorLogs == Null){
          listErrorLogs = new List<Error_Log__c>();
          if(!oAuthrefQuery.listErrorLogs.isempty())
          listErrorLogs.addAll(oAuthrefQuery.listErrorLogs);
          }return listErrorLogs;
        }
        set;
    }
    /*
     * Method Name : AuthSummary_C_HUM
     * Description : Constructor for the class , initialize variable
     * Return type : NA
     * Parameter   : NA
     */
    Public AuthSummary_C_HUM()
    {
        bIsError = false;
        bShowError = false;
        bShowMessage = false;
        bShowServiceError = false;
        sRecId = apexpages.currentpage().getparameters().get('Id').escapeHtml4();
        sMemName = getMemberName(apexpages.currentpage().getparameters().get('Id').escapeHTML4());
        lstAuth  = new List<AuthRefferal_Wrapper_DTO_HUM>();
        bAuthIdExists = true;
        bShowProvider = false;
        bShowResults = true;
		sAttachKey = AuthReferral_Query_D_HUM.getEnterpriseId(apexpages.currentpage().getparameters().get('Id'));
        sAccId = AuthReferral_Query_D_HUM.getAccountId(apexpages.currentpage().getparameters().get('Id'));
        bdisableButton = false;
        Policy_Member__c oPolMem = [SELECT id,ETL_Record_Deleted__c from Policy_Member__c where Id = :apexpages.currentpage().getparameters().get('Id')];
        if(oPolMem !=null)bdisableButton = oPolMem.ETL_Record_Deleted__c;
        Account oAccount = oAuthrefQuery.getMemberAccount(apexpages.currentpage().getparameters().get('Id'));
        sEnterpriseID = oAccount.Enterprise_ID__c;
        sDateOfBirth = oAccount.Birthdate__c;
		bTotalRecordFlag = HUMUtilityHelper.isCRMFunctionalityON('1248496');
        IsAutoLaunchMentor = false;
        bKMD = false;
        assignValues(apexpages.currentpage().getparameters().get('Id'));
        MentorGridLaunch();
		string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
		if(string.isNotBlank(sBrowserContext)){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}
    }
    public string sEnterpriseID;
    public string sDateOfBirth;
    public Boolean bIsError{get;set;}
    public Boolean bShowError{get;set;}
    public Boolean bShowMessage{get;set;}
    public String sMemName{get;set;}
    public Boolean bShowResults{get;set;}
    public Boolean bShowServiceError{get;set;}
    public Boolean bAuthIdExists{get;set;}
    public Boolean bAuthIdExistsdet{get;set;}
    public String sRecId {get;set;}
    public List<AuthRefferal_Wrapper_DTO_HUM> lstAuth {get;set;}
    public String sUserinputAuthId {get;set;}
    public string errMsg{get; set;}
    public string sProviderName {get; set;}
    public Boolean bShowProvider {get; set;}
    public String sAttachKey{get;set;}
    public String sAccId{get;set;}
    public Boolean bdisableButton {get; set;}
	public Boolean bTotalRecordFlag {get;set;}
	public static boolean IsAutoLaunchMentor{ get;set; }
    public Policy_Member__c objPolMem{ get; set; }
    public String sKMDocRequest = '';
    public static boolean bKMD{ get; set;}
    public static boolean bIsCasPolicy{ get;set; }
    
	
   /*
    * Method Name   :    getThis 
    * Description   :    Store the Current Page Instance
    * Return Type   :    AuthSummary_C_HUM
    * Parameters    :    NA
    */
    public AuthSummary_C_HUM getThis() 
    {
        return this;
    }

    /*
    * Method Name   :    getRecords
    * Description   :    Returns all the records
    * Return Type   :    List<sObject>
    * Parameters    :    NA
    */
    public List<sObject> getRecords()
    {
        return NULL;
    }

    /*
    * Method Name   :    getRecord
    * Description   :    Store the Current Record
    * Return Type   :    sObject 
    * Parameters    :    NA
    */
    public sObject getRecord()
    {
        return NULL;
    }

    /*
    * Method Name   :    setErrorMessage
    * Description   :    Store the Error
    * Return Type   :    void
    * Parameters    :    NA
    */
    public void setErrorMessage()
    {
        errMsg = Label.ClinicalLoggingErrorMsg_HUM;
    }

    /*
    * Method Name   :  initiateRequest
    * Description   :  This method is used to Call the Service while loading the page
    * Return Type   :  continuation (Object of type Continuation)
    * Parameters    :  NA
    */
    public object initiateRequest()
    {
        Try
        {
            sRequest = '';
            if(String.isNotBlank(sRecId)){
				HttpRequest request = new HttpRequest();
				Integer iTIMEOUT_INT_SECS;               
            
				HUM_Webservice_Callout__c  oSvcHeaderClinicalnew= HUM_Webservice_Callout__c.getValues('Clinical_AuthSummary_V2'); 
				iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClinicalnew.Timeout__c)/1000;
				oServiceAuth.setupRequest(request,oSvcHeaderClinicalnew,apexpages.currentpage().getparameters().get('Id'));
            
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                sRequest = con.addHttpRequest(request);
                con.continuationMethod='processResponse';
                return con;
                }
            else{
                return Null;
            }
        }
        catch(Exception ex)
        {
            bIsError = true;
            bShowServiceError = true;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.AuthSummary_Service_Error);
            ApexPages.addMessage(myMsg);
            HUMExceptionHelper.logErrors(ex, 'Authsummary_c_Hum', 'initiaterequest');
            return NULL;
        }
    }    

    /*
    * Method Name   :  processResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void processResponse()
    {
        try
        {
            HttpResponse response = Continuation.getResponse(sRequest);
            responseparse(response);
        }
        catch(Exception ex)
        {
            bIsError = true;
            bShowServiceError = true;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.AuthSummary_Service_Error);
            ApexPages.addMessage(myMsg);
            HUMExceptionHelper.logErrors(ex, 'Authsummary_c_Hum', 'processResponse');
        }
    }

    /*
     * Method Name   :  responseParse
     * Description   :  This method is used to check response
     * Return Type   :  void 
     * Parameters    :  HttpResponse obj
     */
    @testvisible
    private void  responseParse(HttpResponse sResponse)
    {
        Integer statusCode = sResponse.getStatusCode();
        if (statusCode == 200 || statusCode == 202){
            lstAuthResponse = oServiceAuth.processResponse(sResponse.getbody());
			 if(bTotalRecordFlag== true && lstAuthResponse != null && !(lstAuthResponse).isEmpty() && oServiceAuth.totalRecordFound != null)    
            {  
               if(oServiceAuth.totalRecordFound <= Integer.ValueOf('1000'))
                {
                    finalAuthsummarylist(lstAuthResponse);
                    bShowResults = oServiceAuth.bviewresults;
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO,Label.Auth_Referral_guidance_HUM));
               }
                else
                {   
                    bIsError = true;
                    bShowResults = false;
                    bShowError = true;
                    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.INFO,Label.Authsummary_Records_Exceed.replace('##TOTALNOOFRECORDS##',String.valueOf(oServiceAuth.totalRecordFound)));
                    ApexPages.addMessage(myMsg);
                }
            } 
            else if(bTotalRecordFlag == false  && lstAuthResponse != null && !(lstAuthResponse).isEmpty())
            {
                finalAuthsummarylist(lstAuthResponse);
                bShowResults = oServiceAuth.bviewresults;
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO,Label.Auth_Referral_guidance_HUM));
            }
            else
            {
                bIsError = true;
                bShowResults = false;
                bShowError = true;
				ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.INFO,Label.Authsummary_No_Records);
                ApexPages.addMessage(myMsg);
            }
        }
        else
        {
            bIsError = true;
            bShowResults = false; 
            bShowServiceError = true;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.AuthSummary_Service_Error);
            ApexPages.addMessage(myMsg);
        }
    }

    /*
    * Method Name   :  finalAuthsummarylist
    * Description   :  This method is used to form the wrapper list that needs to be display on UI. 
    * Return Type   :  void 
    * Parameters    :  List<AuthReferral_Wrapper_H_HUM>
    */
    public void finalAuthsummarylist(List<AuthRefferal_Wrapper_DTO_HUM> lstAuthResponse)
    {
        bShowMessage = false;
        if(lstAuthResponse != null && !lstAuthResponse.isEmpty())
        {
           for(AuthRefferal_Wrapper_DTO_HUM obj:lstAuthResponse)
           {
                if(obj.bIsAccessible){
                   lstAuth.add(obj);
                }
                else if(! obj.bIsAccessible){
                    bIsError = true;
                    bShowMessage = true;
                    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.AuthSummary_Security_Info);
                    ApexPages.addMessage(myMsg);
                }
           }
        }
        else
        {
            bIsError = true;
            bShowError = false;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.Authsummary_No_Records);
            ApexPages.addMessage(myMsg);    
        }
    }

    /*    
     * Method Name   :  authDetailscheckRequest
     * Description   :  This method is used to Check the users access for auth id
     * Return Type   :  Boolean
     * Parameters    :  None
     */
      public Void authDetailscheckRequest() 
    {
        AuthReferral_Query_D_HUM objdatalayer = new AuthReferral_Query_D_HUM();
        AuthReferral_SecurityHO_H_HUM cHomeOfficeSecurity= new AuthReferral_SecurityHO_H_HUM();
        Account objAcct = new Account();
        Boolean bAuthIdExistsdet;
        
        ID sRecId = apexpages.currentpage().getparameters().get('Id').escapeHtml4();
        String sAuthIdAF = apexpages.currentpage().getparameters().get('authIdAF').escapeHtml4(); 
        if(sAuthIdAF != null && string.isNotEmpty(sAuthIdAF))
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO,Label.Auth_Referral_guidance_HUM));
            if(sRecId != null && string.isNotBlank(sRecId))
            {
               objAcct = objdatalayer.getMemberAccount(sRecId);  
            }
        }
        bAuthIdExistsdet = cHomeOfficeSecurity.filteredAuthsCheck(sAuthIdAF,UserInfo.getUserId(),objAcct.Id);
        
        if (bAuthIdExistsdet)
        {
            ApexPages.Message ErrMsg = new ApexPages.Message(ApexPages.Severity.ERROR,Label.AuthSummary_Search_No_Records);
            ApexPages.Message ErrMsg1 = new ApexPages.Message(ApexPages.Severity.Info,Label.AuthSummary_Security_Info);
            ApexPages.addMessage(ErrMsg);
            ApexPages.addMessage(ErrMsg1);
        }
    }
        
     /*    
     * Method Name   :  authDetailsRequest
     * Description   :  This method is used to Call the Service while loading the page
     * Return Type   :  Object
     * Parameters    :  None
     */
    public Object authDetailsRequest() 
    {
        try
        {
            sUserinputAuthId = apexpages.currentpage().getparameters().get('authIdAF').escapeHtml4();
          if (String.isNotBlank(sUserinputAuthId))
          {
        ClinicalAuthDetails_S_HUM oAuthDetailsService = new ClinicalAuthDetails_S_HUM();
        ClinicalAuthDetailsRequest_H_HUM_V2 oclinicalAuthdetailV2 = new ClinicalAuthDetailsRequest_H_HUM_V2();                        
		oclinicalAuthdetailV2 = oAuthDetailsService.assignValuesToHelperV2(sUserinputAuthId, sEnterpriseID, sDateOfBirth);           
       
        if (oclinicalAuthdetailV2 != null)
        {
            HUM_Webservice_Callout__c oServiceCalloutClinicalV2 = HUM_Webservice_Callout__c.getValues('Clinical_AuthDetail_V2'); 
            Integer iTIMEOUT_INT_SECS;
            String sJsonRequest;
            HttpRequest oRequest = new HttpRequest();
              
			iTIMEOUT_INT_SECS = Integer.valueof(oServiceCalloutClinicalV2.Timeout__c) / 1000;                
			sJsonRequest = JSON.Serialize(oclinicalAuthdetailV2);                 
			oRequest = oauthDetailsService.setHttpRequest(oRequest, oServiceCalloutClinicalV2, sjsonRequest);                 
                     
            Continuation con = new Continuation(iTIMEOUT_INT_SECS );
            con.continuationMethod='processResponseDetail';            
            sRequest = con.addHttpRequest(oRequest);
            return con;
        }
        else {
            return null;
        }
    }
    else{
                bAuthIdExists = true;
                return null;
            }
        }
        catch(Exception ex)
        {
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR,Label.AuthSummary_Search_No_Records);
            ApexPages.addMessage(myMsg);
            bAuthIdExists = true;
            HUMExceptionHelper.logErrors(ex, 'Authsummary_c_Hum', 'authDetailsRequest');
            return null;
        }
    }

    /*
     * Method Name   :  processResponseDetail
     * Description   :  This is the Callback method for Continous Object to process the Response data
     * Return Type   :  void
     * Parameters    :  NA
     */
    public void processResponseDetail() 
    {  
       try
        {   
            bAuthIdExists = false;
            HttpResponse response = Continuation.getResponse(this.srequest);
            checkAuthId(response);
        } 
        catch(Exception ex)
        {
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR,Label.AuthSummary_Search_No_Records);
            ApexPages.addMessage(myMsg);
            bAuthIdExists = true;
            HUMExceptionHelper.logErrors(ex, 'Authsummary_c_Hum', 'processResponseDetail');
        }
    }
    /*
     * Method Name   :  checkAuthId
     * Description   :  This is show error messages on UI if AuthId doesn't exist.
     * Return Type   :  void
     * Parameters    :  HttpResponse response
     */
     @testvisible
     private void checkAuthId(HttpResponse response)
     {
        Integer statusCode = response.getStatusCode();
        if (statusCode == 200 || statusCode == 202 )    
        {
            ClinicalAuthDetailsResponse_H_HUM oresponse = new ClinicalAuthDetailsResponse_H_HUM();
            if(String.isNotBlank(response.getBody()))
             {        
                oresponse = (ClinicalAuthDetailsResponse_H_HUM)JSON.deserialize(response.getBody(),ClinicalAuthDetailsResponse_H_HUM.class);
             }
            if(oresponse.AuthorizationsResponse != null && string.isNotBlank(sRecId))
            {
               ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO,Label.Auth_Referral_guidance_HUM));
              bAuthIdExists = oServiceAuth.checkAuthSecurity(oresponse,apexpages.currentpage().getparameters().get('Id'));
              securityError(bAuthIdExists);
            }
            else
            {
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR,Label.AuthSummary_Search_No_Records);
                ApexPages.addMessage(myMsg);
                bAuthIdExists = true;
            }
         }
         else
         {
            bIsError = true;
            bShowServiceError = true;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.AuthSummary_Service_Error);
            ApexPages.addMessage(myMsg);
         }
     }
     
    /*
     * Method Name   :  securityError
     * Description   :  This is to show security Error.If user doesn't have security to view.
     * Return Type   :  void
     * Parameters    :  HttpResponse response
     */
     @testvisible
     private void securityError(Boolean bAuthIdExists)
     {
        if (bAuthIdExists)
        {
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR,Label.AuthSummary_Search_No_Records);
            ApexPages.Message myMsg1 = new ApexPages.Message(ApexPages.Severity.Info,Label.AuthSummary_Security_Info);
            ApexPages.addMessage(myMsg);
            ApexPages.addMessage(myMsg1);
        }
     }
     
     /*
     * Method Name   :  getMemberName
     * Description   :  This method is return membername
     * Return Type   :  String 
     * Parameters    :  ID sRecId
     */
    @testvisible
    private String getMemberName(ID sRecId )
    {
       List<POLICY_Member__C> lstPolicyMem = oAuthrefQuery.getMemberName(sRecId);
       if(NULL != lstPolicyMem && !lstPolicyMem.isEmpty())    return lstPolicyMem[0].Member__r.Name;
       return NULL;
    }
	
	
	
	/*
     * Method Name   :  assignValues
     * Description   :  This method is return policy member details
     * Return Type   :  String 
     * Parameters    :  ID sRecId
     */
    public void assignValues(Id sRecId)
    {
        if (ApexPages.currentpage().getParameters().containsKey('Id'))
        {
            objPolMem = [Select Id,ETL_Record_Deleted__c, Source_Coverage_ID__c, Policy__r.Product__r.Name, Name, Policy__c, Status__c, Effective_Date__c, End_Date__c, Policy__r.Product__r.ProductCode, Policy__r.Major_LOB_Frm__c,Policy__r.Major_LOB__c, Member__r.Enterprise_Id__c,
            Policy__r.Group_Name__r.Source_Platform_Code__c, Policy__r.Source_Cust_Cov_Key__c, Policy__r.Coverage_Plan_Effective_Date__c, Policy__r.Coverage_Plan_End_Date__c,
            Policy__r.Benefit_Coverage__c, Policy__r.Policy_External_ID__c, Policy__r.Platform__c, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy_Platform__c, Policy__r.Contract_Number__c, Policy__r.PBP_Code__c, Product__c, Product_Type__c, Member__r.personmailingpostalcode, ASO__c,Group_Number__c,Product_Type_Code__c
            From Policy_Member__c Where Id = :sRecId];
        }
     }
     
      /*
     * Method Name   :  MentorGridLaunch
     * Description   :  This method is check condition for Mentor Launch
     * Return Type   :  String 
     * Parameters    : 
     */
     public void MentorGridLaunch()
    {
        //Mentor Benefit Grid Changes
        bIsCasPolicy = false;
        IsAutoLaunchMentor = false;
            if (objPolMem.Policy__r.Platform__c == 'EM')
            {
                if((objPolMem.Policy__r.Benefit_Coverage__c.substring(0, 2).contains('SF') && objPolMem.Policy__r.Benefit_Coverage__c.substring(2, 3).isAlpha()) || (objPolMem.Policy__r.Benefit_Coverage__c.substring(0, 2).contains('FI') && objPolMem.Policy__r.Benefit_Coverage__c.substring(2, 3).isAlpha())|| objPolMem.Policy__r.Benefit_Coverage__c.substring(0, 2).contains('FE'))
                {
                    IsAutoLaunchMentor = true;
                }
            }
            else if (objPolMem.Policy__r.Platform__c == 'LV' && !GLOBAL_CONSTANT_HUM.RestrictedCASProdType.contains(objPolMem.Product_Type__c))
            {
                bIsCasPolicy = true;
            }
            
            else
            {       
                IsAutoLaunchMentor = false; 
            }
    }
    
     /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        
            if (ServiceObj != null && ServiceObj.Active__c)
            {
                request.setMethod(ServiceObj.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader('content-type', ServiceObj.Content_Type__c);
                request.setHeader('Connection', 'keep-alive');
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader('SOAPAction','');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
    }
    
    /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    @testVisible public Continuation MentorGridService()
    {
         continuation oConMentor;
         try
        { 
        MentorServiceRequest_DTO_HUM KMDRequestDTO = new MentorServiceRequest_DTO_HUM();            
        HUM_Webservice_Callout__c KMDServiceDetails = HUM_Webservice_Callout__c.getValues('KMDocument');    
        // Create continuation with a timeout
        integer iTIMEOUT_INT_SECS = Integer.valueof(KMDServiceDetails.Timeout__c) / 1000;
        oConMentor = new Continuation(iTIMEOUT_INT_SECS);
        // Set callback method should be common for KM Document services 
        oConMentor.continuationMethod = 'parseKMDResponse';
        HttpRequest httpKMDReqObj = builtHttpRequest(KMDServiceDetails);
        String sjsonRequest = JSON.serialize(AuthSummary_D_HUM.RequestStructureKDMService(KMDRequestDTO));
        httpKMDReqObj.setBody(sjsonRequest);
        MentorServiceResponse_DTO_HUM responseKMD = new MentorServiceResponse_DTO_HUM();
        oConMentor.state = responseKMD;
        sKMDocRequest=oConMentor.addHttpRequest(httpKMDReqObj);
        }catch(Exception e)
        {
            
             HUMExceptionHelper.logErrors(e, 'AuthSummary_C_HUM', 'MentorGridService');          
        }

        return oConMentor;

    }
    
    /**
     * Returns Response
     * <p>
     * Callback Method
     *
     */
    public void parseKMDResponse()
      {
        try
        {
          MentorServiceResponse_DTO_HUM oResponseKMD;
          HttpResponse responseKMD = Continuation.getResponse(sKMDocRequest);           
            if (responseKMD != null && (responseKMD.getStatusCode() == 200 || responseKMD.getStatusCode() == 202) || Test.isRunningTest())
            {
                oResponseKMD = (MentorServiceResponse_DTO_HUM) System.JSON.deserialize(responseKMD.getBody(), MentorServiceResponse_DTO_HUM.class);
                bKMD = AuthSummary_D_HUM.ParseStructureKMDocument(oResponseKMD);
            }
            else
            {
                bKMD = false;
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'AuthSummary_C_HUM', 'parseKMDResponse');
        }
        
        
    }
}