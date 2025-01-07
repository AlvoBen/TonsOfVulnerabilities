/*****************************************************************************************
Apex class Name    : MemberPreferencesLink_C_HUM 
Version            : 1.0
Function           : This is the controller class for Account and Policy Member Member Preference Link
Created Date       : 03/05/2018 
Test Class         : MemberPreferencesLink_T_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *  Shivam Anand                                      03/05/2018           REQ349581- Add Member Preferences Link to CRM Service Person Account Page
 *  Vijaya Koriprolu                                  03/06/2018           REQ - 349580- Add Member Preferences Link on CRM Policy Member Page
 *  Santhi Mandava                                    03/12/2019           Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
 *  Riya Agarwal									  10/20/2020		   REQ-1578355 - PR00094254 - CR AEP readiness -Research - Critical Security Issues
 *********************************************************************************************************************************/
 public with sharing class MemberPreferencesLink_C_HUM {
 
 public String sRedirectMember {get;set;}
 @TestVisible private MemberPreferencesLink_S_HUM oServiceEncrypt = new MemberPreferencesLink_S_HUM();
 public MemberPreferencesLink_DTO_HUM lstMemGenKeyDetails {get; private set;}
 public String sMemGenKey{get;set;}
 public Boolean bShowServiceError{get;set;}
 @TestVisible private String sRequest ='';
 private boolean bIsDataAvailable;
 public String sMemEncryptedValue{get;set;}
 public Boolean bIsError{get; private set;}
 Public List<Error_Log__c> lstLogErrors
    {
        get
        {
            if(lstLogErrors == Null)
            {
                lstLogErrors = new List<Error_Log__c>();
            } 
            return lstLogErrors;
        }
        set;
    }
    

  /*
    * Method Name   :  MemberPreferencesLink_C_HUM
    * Description   :  This is a constructor for class MemberPreferencesLink_C_HUM
    * Return Type   :  NA
    * Parameters    :  NA
    */

 public MemberPreferencesLink_C_HUM(){
   sMemGenKey= ApexPages.currentPage().getParameters().get('MemberGenKey').escapeHtml4();
   bShowServiceError = false;
   bIsDataAvailable = true;
   bIsError = false;
 }

/*
    * Method Name   :  initiateRequest
    * Description   :  This method is used to call the Service when clicked on Member PreferencesLink on MemberPreferencesLink_VF_HUM Page
    * Return Type   :  continuation
    * Parameters    :  NA
    */       
    public Object initiateRequest()
    { 
      try
        { 
            if(String.isNotBlank(sMemGenKey))
            {   HUM_Webservice_Callout__c oSvcEncrypt = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_HUM.EncryptRestService_HUM); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcEncrypt.Timeout__c)/1000;
                HttpRequest request = new HttpRequest();
                oServiceEncrypt.setupRequest(request, oSvcEncrypt,sMemGenKey);
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                sRequest = con.addHttpRequest(request);
                con.continuationMethod='processResponse';
                return con;
            }
            else
            {
                bShowServiceError = true;
                String sError = System.Label.EncryptRestService_No_records;
                displayErrorMessage(true , sError , GLOBAL_CONSTANT_HUM.sNoRecords);
                return Null;            
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberPreferencesLink_C_HUM', 'initiateRequest');
            return NULL;          
        }
    
    }
    
      /*
    * Method Name   :  processResponse
    * Description   :  This method is used to get response from EncryptRest service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
 
 public void processResponse()
    {     
        try 
        { 
            HttpResponse response = Continuation.getResponse(sRequest);
            sMemEncryptedValue = responseparse(response);
            Endpoint_Url_HUM__c oEndPoint = Endpoint_Url_HUM__c.getValues(GLOBAL_CONSTANT_HUM.MemPreference_HUM); 
            sRedirectMember  = oEndPoint.Remote_Url__c;
            
         }
        catch(Exception ex)
        {   
            bShowServiceError = true;
            String sError = System.Label.EncrytRest_Service_Error;
            displayErrorMessage(true, System.Label.EncrytRest_Service_Error, GLOBAL_CONSTANT_HUM.sServiceError);
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors = HUMExceptionHelper.logErrors(ex,'MemberPreferencesLink_C_HUM','processResponse');   
                    
        }
  
    }
    
       /*
    * Method Name   :  responseParse
    * Description   :  This method is used to parse response from Encryt method Encrypt Rest Service.
    * Return Type   :  String   Encryted Mem Gen Key 
    * Parameters    :  NA
    */
    @testvisible public String responseParse(HttpResponse sResponse)
    {  String sEncyptedMemGenKey ='';
       lstMemGenKeyDetails =  new MemberPreferencesLink_DTO_HUM();
        Integer iStatusCode = sResponse.getStatusCode();
        if (iStatusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || iStatusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
        { 
            lstMemGenKeyDetails = oServiceEncrypt.processResponse(sResponse.getbody());
          if(lstMemGenKeyDetails ==null)
            {
                String sError = System.Label.EncryptRestService_No_records;
                displayErrorMessage(true , sError ,GLOBAL_CONSTANT_HUM.sNoRecords);
            }
             else
            {
               sEncyptedMemGenKey = lstMemGenKeyDetails.sEncValue;
            }
           
        }
        else
        {
            bShowServiceError = true;
            String sError = System.Label.EncrytRest_Service_Error;
            displayErrorMessage(true, sError, GLOBAL_CONSTANT_HUM.sServiceError);
        } 
    return sEncyptedMemGenKey;              
    }  
        
     /*
    * Method Name : displayErrorMessage
    * Description : This method is used to display error message on UI.
    * Return type : Void
    * Parameter   : Boolean ,String 
    */
    @testVisible private void displayErrorMessage(Boolean bError, String sError, String errorType)
    {
        if(bError) 
        { 
            bIsDataAvailable = true;
            bIsError = bError;
            if(errorType.equalsIgnorecase(GLOBAL_CONSTANT_HUM.sNoRecords))  
            { 
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,sError,true);
            }
            if(errorType.equalsIgnorecase(GLOBAL_CONSTANT_HUM.sServiceError))    
            {
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Error,sError,true);
            }
            bIsDataAvailable = false; 
        }
    }
}