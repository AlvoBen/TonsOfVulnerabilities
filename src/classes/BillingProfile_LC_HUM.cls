/*******************************************************************************************************************************************************
Apex Class Name  : BillingProfile_LC_HUM
Version          : 1.0
Created Date     : 05/23/2022
Function         : This is controller class for LWC component BillingProfile.
                   This is used to display billing profiles with latest Data table standards.
Test Class       : BillingProfile_LT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Suraj Patil                                        05/23/2022                 Original Version
* Prashant Moghe                                     05/23/2022                 User Story 3271229:MF-2923225 - CRM Service Billing Systems Integration: Member Billing Profile Account Billing APP -Billing List
* Raj Paliwal					                     12/22/2022			        User Story 4003680:MF 3624855-CRM Service Billing Systems Integration: Lightning- Billing Info/Error/Toast Messages
* Anuradha Gajbhe                                    03/06/2023                 US#4302387 - Lightning-Phonebook- Secure Payment UI Controls-EBilling (Surge) Genesys impact
************************************************************************************************************************************************/

public with sharing class BillingProfile_LC_HUM
{
    public Static BillingService_LS_HUM sbilling = new BillingService_LS_HUM();
    @testVisible private Id recID = null;
    
    /*
    * Method Name : BillingProfile_LC_HUM
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : NA
    */  
    
    public List<BillingProfile_LDTO_HUM> lstServiceOutput {get;set;}    
    public static List<BillingProfile_LDTO_HUM> lstBillingProfiles{get;set;}   
    public Static String sGroupName{get;set;}
    public List<SelectOption> lstProfileStatus{get;set;}
    public Static String sProfileStatus{get;set;}
    public Static String sTermDate{get;set;}
    public Static String sNoResultsMessage{get;set;}
    public Static Boolean bIsMemberError{get;set;}
    public Static String sObjName{get;set;}
    public Static Boolean isProfileDetailsVisible{get;set;}
    public Static String sPBSUrl{get;set;}    
    public Static String sOnlineBillingUrl{get;set;}
    public Static String sSectionHeader{get;set;}
    public Static String sAkaName{get;set;}
    public Static String sRowNumber {get; set;}
    public Static String sBillingProfileJson{get;set;}
    public Static String sPlatformCode{get;set;}
    public Static String sIDType{get;set;}
    public Static String sUserId{get;set;}
    public Static String sSearchID{get;set;}
    public Static String sRecId{get;set;}
    public Static String sPBSNew{get;set;}
    public Static String sPBSNewComments{get;set;}
    public Static String sEnvironment{get;set;}
    public Static String sPBSUrlNew{get;set;}
    public Static String sRequest;
    public Static Boolean bActiveProfileExists{get;set;}
    public Static BillingProfile_LDTO_HUM oActiveBillingProfile {get;set;}
    public Static String sSubscriberIDBase {get;set;} 
    public Static String sMemberIdBase {get;set;}
    public Static boolean bSwitchMemberBillingRadio {get; set;}
    public Static boolean bIsGenesysUser {get;set;}
    public Static list<MemberPlan> objMemPRecordData{get;set;}
    public Static list<Account> objAccRecordData{get;set;}
    
    /*
    * Method Name : getHSS_ONECLICK_URL
    * Description : This method is used to get hss onclick url from custom setting.
    * Return type : String
    * Parameter   : NA
    */
    @AuraEnabled(cacheable=true)
    public static string getHSS_ONECLICK_URL()
    {
        return Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;
    }
    /*
    * Method Name : getPBSUrl
    * Description : This method is used to get PBSUrl url from custom setting.
    * Return type : String
    * Parameter   : NA
    */
    @AuraEnabled(cacheable=true)
    public static string getsPBSUrl()
    {
        return BillingNonWebUrls_HUM__c.getInstance('PBSUrl').Url__c;
    }
    
    /*
    * Method Name : getsOnlineBillingUrl
    * Description : This method is used to get sOnlineBillingUrl url from custom setting.
    * Return type : String
    * Parameter   : NA
    */
    @AuraEnabled(cacheable=true)
    public static string getsOnlineBillingUrl()
    {
        return BillingNonWebUrls_HUM__c.getInstance('OnlineBillingUrl').Url__c;
    }
    
    /*
    * Method Name : getsEnvironment
    * Description : This method is used to get sEnvironment from custom setting.
    * Return type : String
    * Parameter   : NA
    */
    @AuraEnabled(cacheable=true)
    public static string getsEnvironment()
    {
        return HUMConstants__c.getInstance('APPLAUNCHER_ENVIRONMENT').StringValue__c;
    }
    
    /*	
    * Method Name : getsPBSNew
    * Description : This method is used to get sPBSNew from global constant.
    * Return type : String
    * Parameter   : NA
    */
    @AuraEnabled(cacheable=true)
    public static string getsPBSNew()
    {
        return GLOBAL_CONSTANT_HUM.PBS_New;
    }
   	/*
    * Method Name : getsPBSNewComments
    * Description : This method is used to get sPBSNewComments from global constant.
    * Return type : String
    * Parameter   : NA
    */
    @AuraEnabled(cacheable=true)
    public static string getsPBSNewComments()
    {
        return GLOBAL_CONSTANT_HUM.PBS_New_Comments;
    }


    /*
    * Method Name : initializeVariables
    * Description : This method is used to initialize variables
    * Return type : Void
    * Parameter   : NA
    */
    
    public static void initializeVariables(String sRecordId)
    {
        bIsGenesysUser = false;
        bIsMemberError = false;
        List<BillingProfile_LDTO_HUM> lstBillingProfiles = new List<BillingProfile_LDTO_HUM>();
        isProfileDetailsVisible = false;
        List<SelectOption> lstProfileStatus = new List<SelectOption>();
        sNoResultsMessage = '';
        sPBSUrl = BillingNonWebUrls_HUM__c.getInstance('PBSUrl').Url__c;
        sPBSUrlNew =GLOBAL_CONSTANT_HUM.PBS_Url_New ;
        sPBSNew = GLOBAL_CONSTANT_HUM.PBS_New;
        sPBSNewComments = GLOBAL_CONSTANT_HUM.PBS_New_Comments;
        sEnvironment = HUMConstants__c.getInstance('APPLAUNCHER_ENVIRONMENT').StringValue__c;         
        sOnlineBillingUrl = BillingNonWebUrls_HUM__c.getInstance('OnlineBillingUrl').Url__c;
        sRecId = sRecordId;
        bSwitchMemberBillingRadio = CRMFunctionalityenabler_H_HUM.Checkflagvalue('MemberBillingRadio_2484356');
        bIsGenesysUser = PhoneBook_H_HUM.isGenesysUser();
    }
       
    
    
    /*
    * Method Name : initiateBillingDetails
    * Description : This method is used to get selected object record details like id, name.
    * Return type : Void
    * Parameter   : NA
    */
    @AuraEnabled 
    public static void initiateBillingDetails(String sRecordId)
    {   
        initializeVariables(sRecordId);
        sObjName = getSObjName(((Id)sRecId));  
        If(sObjName != NULL)
        {
            if (sObjName == 'MemberPlan'){
                objMemPRecordData = [SELECT Id, Name, MemberId, GroupNumber, PlanId, Policy_Platform__c, Source_Coverage_ID__c, SubscriberId, Display_Group_Name__c, Policy__c FROM MemberPlan where id = :sRecId]; 
            }
            else if (sObjName == 'Account'){
                objAccRecordData = [SELECT Id, Name, Source_Platform_Code__c, Source_Customer_Key__c, Group_Number__c FROM Account where id = :sRecId];  
            }
            sGroupName = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ? objAccRecordData[0].Name : objMemPRecordData[0].Name);
            sSectionHeader = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ? System.Label.BillingGroupBillingTitle_Account_HUM : System.Label.BillingMemberBillingTitle_Account_HUM);
        }
    }

    /*
    * Method Name : getSecurePayFlagDetails
    * Description : This method is used to get secure pay flag details.
    * Return type : boolean
    * Parameter   : Record id
    */
    @AuraEnabled 
    public static boolean getSecurePayFlagDetails(String sRecordId)
    {   
        boolean bSecurePaybuttonFlag = true;
        bSwitchMemberBillingRadio = CRMFunctionalityenabler_H_HUM.Checkflagvalue('MemberBillingRadio_2484356');
        bIsGenesysUser = PhoneBook_H_HUM.isGenesysUser();

        sObjName = getSObjName(((Id)sRecordId));  
        If(sObjName != NULL)
        {
            if (sObjName == 'MemberPlan'){
                objMemPRecordData = [SELECT Id, Name, MemberId, GroupNumber, PlanId, Policy_Platform__c, Source_Coverage_ID__c, SubscriberId, Display_Group_Name__c, Policy__c FROM MemberPlan where id = :sRecordId]; 
            }
            else if (sObjName == 'Account'){
                objAccRecordData = [SELECT Id, Name, Source_Platform_Code__c, Source_Customer_Key__c, Group_Number__c FROM Account where id = :sRecordId];  
            }
            sGroupName = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ? objAccRecordData[0].Name : objMemPRecordData[0].Name);
            sSectionHeader = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ? System.Label.BillingGroupBillingTitle_Account_HUM : System.Label.BillingMemberBillingTitle_Account_HUM);
        }

        If (bSwitchMemberBillingRadio != null && bIsGenesysUser != null && sSectionHeader != null){
            If(bSwitchMemberBillingRadio && bIsGenesysUser && sSectionHeader.contains('Member'))
            {
                bSecurePaybuttonFlag = false;
            }else{
                bSecurePaybuttonFlag = true;
            }
        }else{
            bSecurePaybuttonFlag = true;
        }
        return bSecurePaybuttonFlag;	
    }

    /*
    * Method Name : getSObjName
    * Description : This method is used get current record's object name.
    * Return type : String 
    * Parameter   : Id
    */
    @AuraEnabled(Cacheable=true)
    public static String getSObjName(Id recID)
    {
        If(recID != null)
        {
           return recID.getSobjectType().getDescribe().getName();
        }
        return Null;
    }
    
    /*
    * Method Name : getBillingProfiles
    * Description : This method is used to retrieve billing profiles from service.
    * Return type : Object 
    * Parameter   : NA
    */
    @AuraEnabled(Continuation=true)
    public Static Object getBillingProfiles(String sRecordId)
    {   
        initiateBillingDetails(sRecordId);
        BillingProfile_LDTO_HUM objInput = getServiceInput();
        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('BillingServiceV2');
        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
		con.continuationMethod = 'parseBillingServiceResponse';
        if(String.isBlank(objInput.sUserID))
        {
            return System.Label.BillingNetworkIdError;
        }
        else if(String.isBlank(objInput.sSearchID))
        {
            return sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ? System.Label.BillingSCKError :System.Label.BillingSCIDError ;
        }
        else
        {
        HttpRequest httpBilling = sbilling.builtHttpRequest(RSServiceDetails);
        httpBilling.setBody(JSON.serialize(sbilling.RequestStructureBillingService(objInput)));
        Billing_Wrapper_LDTO_HUM dataTransfer=new Billing_Wrapper_LDTO_HUM(sRecId, sGroupName, sObjName, sPlatformCode, getUserNetworkId());
        con.state=dataTransfer;
        sRequest=con.addHttpRequest(httpBilling); 
		}		
        return con;
    }
    
   /*
    * Method Name : getServiceInput
    * Description : This method is used to get the service input.
    * Return type : BillingProfile_LDTO_HUM 
    * Parameter   : NA
    */
    @AuraEnabled
    Public Static BillingProfile_LDTO_HUM getServiceInput()
    {
        sObjName = getSObjName(((Id)sRecId));
        sIDType = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ? 'GROUPID' :'CONTRID');
        sUserId = getUserNetworkId();
	If(Test.isRunningTest()){
            sGroupName='';
        }
        If(String.isBlank(sGroupName)) {
            if(sObjName == 'MemberPlan'){
                objMemPRecordData = [SELECT Id, Name, MemberId, GroupNumber, PlanId, Policy_Platform__c, Source_Coverage_ID__c, SubscriberId, Display_Group_Name__c, Policy__c FROM MemberPlan where id = :sRecId]; 
            }
            else if(sObjName == 'Account'){
                objAccRecordData = [SELECT Id, Name, Source_Platform_Code__c, Source_Customer_Key__c, Group_Number__c FROM Account where id = :sRecId]; 
            } 
            sGroupName = (sObjName == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM ? objAccRecordData[0].Name : objMemPRecordData[0].Name);
        }
        
        if(objMemPRecordData != NULL)
        {
             sPlatformCode = objMemPRecordData[0].Policy_Platform__c;   
             sSearchID = objMemPRecordData[0].Source_Coverage_ID__c;   
             if(sSectionHeader.equalsIgnoreCase(System.Label.BillingMemberBillingTitle_Account_HUM)){
                sMemberIdBase = objMemPRecordData[0].Name;
                sMemberIdBase = sMemberIdBase.substring(0,sMemberIdBase.length()-2);
                sSubscriberIDBase = sMemberIdBase;
             }
        else if(objAccRecordData != NULL){
            sPlatformCode = objAccRecordData[0].Source_Platform_Code__c ;   
            sSearchID = objAccRecordData[0].Source_Customer_Key__c ;   
        }    
        }
        BillingProfile_LDTO_HUM objInput = BillingProfileDetails_LH_HUM.getBillingProfilesInput(sUserId,sPlatformCode,sSearchID,sIDType,sObjName );
        return objInput;
    }
    
    /*
    * Method Name : getUserNetworkId
    * Description : This method is used get the logged in users network id.
    * Return type : String 
    * Parameter   : NA
    */
    @AuraEnabled
    public Static String getUserNetworkId()
    {
        String sNUserId = '';
        User objUser = [Select Network_User_Id__c from User where Id =: UserInfo.getUserId()];
        If (objUser != null) sNUserId = objUser.Network_User_Id__c;
        return sNUserId;
    }
    
    /*
    * Method Name : parseBillingServiceResponse
    * Description : This method is used to process service output.
    * Return type : Void
    * Parameter   : NA
    */
   

    @AuraEnabled 
    public static Object parseBillingServiceResponse(List<string> labels, Object state)
    {
        Billing_Wrapper_LDTO_HUM dataTransfer = (Billing_Wrapper_LDTO_HUM) state;
        sbilling.setServiceClassVaraibles(dataTransfer);
        List<BillingProfile_LDTO_HUM> lstServiceOutput = new List<BillingProfile_LDTO_HUM>();
        try
        {
          BillingProfile_LDTO_HUM dtobillingSummaryObj = new BillingProfile_LDTO_HUM();           
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
                                lstServiceOutput = sbilling.getProfileInfo(false,billAccList ,lstServiceOutput);
                                if(lstServiceOutput == NULL)
                                {
                                     if(sObjName != GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM) sError =  System.Label.BillingMemberProfileServiceError_Account_HUM;
										return sError;
                                }
                                else
                                {
                                    lstBillingProfiles = lstServiceOutput;
                                    lstBillingProfiles.sort();
                                    bActiveProfileExists = false;
                                    oActiveBillingProfile = sbilling.getActiveProfile(lstBillingProfiles);
                                    dtobillingSummaryObj = lstBillingProfiles[0];
                                    if(null != oActiveBillingProfile) {

                                        bActiveProfileExists = true;
                                    }
                                }
                                    
                            }
							else
							{							
								sError =  System.Label.BillingMemberProfileServiceError_Account_HUM;
								return sError;                
							}
                    }
                } 
                return dtobillingSummaryObj;
            }
            return null;
            }
        catch(exception ex) 
        {
            HUMExceptionHelper.bShowErrorOnPage =false;
            HUMExceptionHelper.logErrors(ex, 'BillingProfile_LC_HUM', 'parseBillingServiceResponse');
            HUMExceptionHelper.bShowErrorOnPage =true;
            return  System.Label.BillingProfileServiceError_Account_HUM;
        }       
        
      }
   
    /*
    * Method Name : getThis
    * Description : This method is used to return this class object
    * Return type : BillingProfile_LC_HUM 
    * Parameter   : NA 
    */
    public BillingProfile_LC_HUM getThis()
    {
        return this;
    }
}