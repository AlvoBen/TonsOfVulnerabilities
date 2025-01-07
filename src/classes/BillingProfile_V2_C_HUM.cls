/*******************************************************************************************************************************************************
Apex Class Name  : BillingProfile_V2_C_HUM
Version          : 1.0
Created Date     : 08/17/2016
Function         : This is controller class for visual force component 'BillingProfile_V1_CMP_HUM'.
                   This is used to display billing profiles with latest Data table standards.
Test Class       : BillingProfile_V2_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi        36948                 08/17/2016                 Original Version
* Pallavi Ravishankar         36948                 08/22/2016                 Modified as per REQ 277217 and REQ 279160.
* Praveen Kumar Parimi        36948                 08/29/2016                 Incorporated the review comments for review #36948. 
* Santhi Mandava              40488                 09/19/2016                 Implemented billing logging. 
* Santhi Mandava                                    03/12/2019                 Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* Satyam Malviya                                    07/11/2019                 REQ - 402887 - Create a CRM Application Launcher - PBS Links
* Sumit Pharande                                    11/05/2019                 REQ - 412009 Billing Service SOAP to Rest conversion
* Amit Agarwal                                      05/26/2020                 User Story 1114810: PR00091574 - MF 1 - AHT - Ability to display additional information on Billing Profile Summary page (RF)
* Amar Gadewar                                      06/18/2020                 User Story 1203617: PR00091574 - MF 1 - AHT - Ability to automatically launch Billing Profile Detail page while accessing Billing Profile Summary page (RF)
* Sayali Nemade                                     06/22/2020                 Renaming new links, removing old links (Story 1 of 4) (app launcher impacted links) PBS and PBS comment - 833985 (Applauncher Full Launch)
* Amar Gadewar                                      07/14/2020                 User Story 1276147: PR00091574 - MF 1 - AHT - Ability to automatically launch Billing Profile Detail page while accessing Billing Profile Summary page (CRM)
* Amit Agarwal                                      07/17/2020                 User Story 1276157: PR00091574 - MF 1 - AHT - Ability to display additional information on Billing Profile Summary page (CRM)
* Suresh Kokkeragadda                               07/29/2020                 User Story 1317726: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 7/30 , 8/14 and 8/28 releases (CRM)
* Riya Agarwal                                      01/29/2021                 User Story 1759871: PR00094167 - MF 4 - TECH - SF - CRMS – Legacy App Launcher code clean up - PBS, PBS Comments
* Prasanthi Kandula                                 07/26/2021                 US 2484356 T1PRJ0036776_MF_3SF : Member Billing Profile Page - Profile Selection  
************************************************************************************************************************************************/

public with sharing class BillingProfile_V2_C_HUM
{
    public BillingService_S_HUM sbilling = new BillingService_S_HUM();
    @testVisible private Id recID = null;
    
    /*
    * Method Name : BillingProfile_V2_C_HUM
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : NA
    */
    public BillingProfile_V2_C_HUM()
    {
        initializeVariables();
    }
    public List<BillingProfile_DTO_HUM> lstServiceOutput {get;set;}
    public BillingProfile_A_HUM oPageController{get;set;}
    public List<BillingProfile_DTO_HUM> lstBillingProfiles{get;set;}   
    public String sGroupName{get;set;}
    public List<SelectOption> lstProfileStatus{get;set;}
    public String sProfileStatus{get;set;}
    public String sTermDate{get;set;}
    public String sNoResultsMessage{get;set;}
    public Boolean bIsMemberError{get;set;}
    public Boolean bIsDataAvailable{get;set;}
    public String sObjName{get;set;}
    public Boolean isProfileDetailsVisible{get;set;}
    public String sPBSUrl{get;set;}    
    public String sOnlineBillingUrl{get;set;}
    public String sSectionHeader{get;set;}
    public String sAkaName{get;set;}
    public String sRowNumber {get; set;}
    public String sBillingProfileJson{get;set;}
    public String sPlatformCode{get;set;}
    public String sIDType{get;set;}
    public String sUserId{get;set;}
    public String sSearchID{get;set;}
    public String sRecId{get;set;}
    public String sPBSNew{get;set;}
    public String sPBSNewComments{get;set;}
    public String sEnvironment{get;set;}
    public String sPBSUrlNew{get;set;}
    public String sRequest;
    public Boolean bActiveProfileExists{get;set;}
    public BillingProfile_DTO_HUM oActiveBillingProfile {get;set;}
    public String sSubscriberIDBase {get;set;} 
    public String sMemberIdBase {get;set;}
    public boolean bSwitchMemberBillingRadio {get; set;}
    public boolean bIsGenesysUser {get;set;}
    
    /*
    * Method Name : getHSS_ONECLICK_URL
    * Description : This method is used to get hss onclick url from custom setting.
    * Return type : String
    * Parameter   : NA
    */
    public string getHSS_ONECLICK_URL()
    {
        return Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;
    }
    
    /*
    * Method Name : initializeVariables
    * Description : This method is used to initialize variables
    * Return type : Void
    * Parameter   : NA
    */
    private void initializeVariables()
    {
        bIsGenesysUser = false;
        bIsMemberError = false;
        bIsDataAvailable = false;
        lstBillingProfiles = new List<BillingProfile_DTO_HUM>();
        isProfileDetailsVisible = false;
        lstProfileStatus = new List<SelectOption>();
        sNoResultsMessage = '';
        sPBSUrl = BillingNonWebUrls_HUM__c.getInstance('PBSUrl').Url__c;
        sPBSUrlNew =GLOBAL_CONSTANT_HUM.PBS_Url_New ;
        sPBSNew = GLOBAL_CONSTANT_HUM.PBS_New;
        sPBSNewComments = GLOBAL_CONSTANT_HUM.PBS_New_Comments;
		sEnvironment = HUMConstants__c.getInstance('APPLAUNCHER_ENVIRONMENT').StringValue__c;         
        sOnlineBillingUrl = BillingNonWebUrls_HUM__c.getInstance('OnlineBillingUrl').Url__c;
        sRecId = ApexPages.CurrentPage().getParameters().get('Id');
        bSwitchMemberBillingRadio = CRMFunctionalityenabler_H_HUM.Checkflagvalue('MemberBillingRadio_2484356');
        bIsGenesysUser = PhoneBook_H_HUM.isGenesysUser();
    }
       
    
    
    /*
    * Method Name : initiateBillingDetails
    * Description : This method is used to get selected object record details like id, name.
    * Return type : Void
    * Parameter   : NA
    */
    public void initiateBillingDetails()
    { 
        sObjName = oPageController.getSObjName(((Id)sRecId));  
        If(oPageController.objRecordData != NULL)
        {
            sGroupName = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ?((Account)oPageController.objRecordData).Name :((Policy_Member__c)oPageController.objRecordData).Name);
            sSectionHeader = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ? System.Label.BillingGroupBillingTitle_Account_HUM : System.Label.BillingMemberBillingTitle_Account_HUM);
        }
    }
    
    /*
    * Method Name : getBillingProfiles
    * Description : This method is used to retrieve billing profiles from service.
    * Return type : Object 
    * Parameter   : NA
    */
    public Object getBillingProfiles()
    {   
        BillingProfile_DTO_HUM objInput = getServiceInput();
        
        bIsDataAvailable = false;
        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('BillingServiceV2');
            // Create continuation with a timeout
        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
         con.continuationMethod = GLOBAL_CONSTANT_HUM.ParseBillingServiceResponse;
        if(String.isBlank(objInput.sUserID))
        {
            displayErrorMessage(true,System.Label.BillingNetworkIdError);
        }
        else if(String.isBlank(objInput.sSearchID))
        {
            displayErrorMessage(true,(sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ? System.Label.BillingSCKError :System.Label.BillingSCIDError));
        }
        else
        {    
            HttpRequest httpBilling = sbilling.builtHttpRequest(RSServiceDetails);
            httpBilling.setBody(JSON.serialize(sbilling.RequestStructureBillingService(objInput)));
            sRequest=con.addHttpRequest(httpBilling);
         }
        
        return con;
    }
    
   /*
    * Method Name : getServiceInput
    * Description : This method is used to get the service input.
    * Return type : BillingProfile_DTO_HUM 
    * Parameter   : NA
    */
    @testVisible private BillingProfile_DTO_HUM getServiceInput()
    {
        sObjName = oPageController.getSObjName(((Id)sRecId));
        sIDType = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ? 'GROUPID' :'CONTRID');
        sUserId = oPageController.getUserNetworkId();
        If(String.isBlank(sGroupName)) sGroupName = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ?((Account)oPageController.objRecordData).Name :((Policy_Member__c)oPageController.objRecordData).Name);
        If(oPageController.objRecordData != NULL)
        {
             sPlatformCode = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM?((Account)oPageController.objRecordData).Source_Platform_Code__c :((Policy_Member__c)oPageController.objRecordData).Policy_Platform__c);   
             sSearchID = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM?((Account)oPageController.objRecordData).Source_Customer_Key__c :((Policy_Member__c)oPageController.objRecordData).Source_Coverage_ID__c);   
             if(sSectionHeader.equalsIgnoreCase(System.Label.BillingMemberBillingTitle_Account_HUM)){
                sMemberIdBase = ((Policy_Member__c)oPageController.objRecordData).Name;
                sMemberIdBase = sMemberIdBase.substring(0,sMemberIdBase.length()-2);
                sSubscriberIDBase =  ((Policy_Member__c)oPageController.objRecordData).Subscriber__r.Name;
                sSubscriberIDBase = (String.isNotBlank(sSubscriberIDBase)?sSubscriberIDBase.substring(0,sSubscriberIDBase.length()-2):'');  
             }
        }
        BillingProfile_DTO_HUM objInput = BillingProfileDetailsV2_H_HUM.getBillingProfilesInput(sUserId,sPlatformCode,sSearchID,sIDType,sObjName );
        return objInput;
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
                                lstServiceOutput = sbilling.getProfileInfo(false,billAccList ,lstServiceOutput);
                                if(lstServiceOutput == NULL)
                                {
                                     if(sObjName != GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM) sError =  System.Label.BillingMemberProfileServiceError_Account_HUM;
                                     displayErrorMessage(true , sError );
                                    
                                }
                                else
                                {
                                    displayErrorMessage(false,'');
                                    lstBillingProfiles = lstServiceOutput;
                                    lstBillingProfiles.sort();

                                    bActiveProfileExists = false;
                                    oActiveBillingProfile = sbilling.getActiveProfile(lstBillingProfiles);

                                    if(null != oActiveBillingProfile) {

                                        bActiveProfileExists = true;
                                    }
                                }
                                    
                            }
							else
							{							
								sError =  System.Label.BillingMemberProfileServiceError_Account_HUM;
								displayErrorMessage(true , sError );                
							}
                    }
                } 
            }
            }
        catch(exception ex) 
        {
            HUMExceptionHelper.bShowErrorOnPage =false;
            HUMExceptionHelper.logErrors(ex, 'BillingProfile_V2_C_HUM', 'parseBillingServiceResponse');
            HUMExceptionHelper.bShowErrorOnPage =true;
            displayErrorMessage(true, System.Label.BillingProfileServiceError_Account_HUM);
        }       
        
      }
    
    
    /*
    * Method Name : displayErrorMessage
    * Description : This method is used to display error message on UI.
    * Return type : Void
    * Parameter   : Boolean ,String 
    */
    @testVisible private void displayErrorMessage(Boolean bMemberError, String sError)
    {
        If(bMemberError) 
        {
            bIsMemberError = bMemberError;
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Error,sError,true);
            bIsDataAvailable = false; 
        }
        else
        {
            bIsDataAvailable = true;
        }
    }
    
   
    /*
    * Method Name : getThis
    * Description : This method is used to return this class object
    * Return type : BillingProfile_V2_C_HUM 
    * Parameter   : NA 
    */
    public BillingProfile_V2_C_HUM getThis()
    {
        return this;
    }
    
}