/*****************************************************************************************
Apex class Name    : MemberPreferencesLink_LC_HUM 
Version            : 1.0
Function           : This is the controller class for Account and Policy Member Member Preference Link
Created Date       : 18/01/2023 
Test Class         : MemberPreferencesLink_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 *  Vishal Shinde                                     18/01/2023           4167387- Implementation of Links & landing pages- Person account page and Medical Plan member page - Part2
 ************************************************************************************************************************************************************************************/

public class MemberPreferencesLink_LC_HUM {
public static String sRedirectMember {get;set;}
 @TestVisible private static MemberPreferencesLink_LS_HUM oServiceEncrypt = new MemberPreferencesLink_LS_HUM();
 public static MemberPreferencesLink_LDTO_HUM lstMemGenKeyDetails {get; private set;}
 public static String sMemGenKey{get;set;}
 public static Boolean bShowServiceError{get;set;}
 @TestVisible private static String sRequest ='';
 private boolean bIsDataAvailable;
 public static String sMemEncryptedValue{get;set;}


/*
    * Method Name   :  initiateRequest
    * Description   :  This method is used to call the Service when clicked on Member PreferencesLink on the UI
    * Return Type   :  continuation
    * Parameters    :  NA
    */    
    @AuraEnabled(Continuation=true)
    public static Object initiateRequest(string sMemGenKey)
    { 
      try
        { 
            if(String.isNotBlank(sMemGenKey))
            {   
                HUM_Webservice_Callout__c oSvcEncrypt = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_HUM.EncryptRestService_HUM); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcEncrypt.Timeout__c)/1000;
                HttpRequest request = new HttpRequest();
                oServiceEncrypt.setupRequest(request, oSvcEncrypt,sMemGenKey);
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                sRequest = con.addHttpRequest(request);
                con.continuationMethod='processResponse';
             	if(Test.isRunningTest()) throw new HUMCustomException('Error');
                return con;
            }
            else
            {
                bShowServiceError = true;
                String sError = System.Label.EncryptRestService_No_records;
                return sError;            
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberPreferencesLink_LC_HUM', 'initiateRequest');
            return NULL;          
        }
    
    }
    
      /*
    * Method Name   :  processResponse
    * Description   :  This method is used to get response from EncryptRest service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
 @auraEnabled(cacheable = true)
 public static string processResponse(List<string> labels, Object State)
    {     
        try  
        {   string ResponseLink='';
            HttpResponse response = Continuation.getResponse(labels[0]);
            sMemEncryptedValue = responseparse(response);
            Endpoint_Url_HUM__c oEndPoint = Endpoint_Url_HUM__c.getValues(GLOBAL_CONSTANT_HUM.MemPreference_HUM); 
            sRedirectMember  = oEndPoint.Remote_Url__c;
         	ResponseLink = '{"sRm":'+ sRedirectMember +',' + '"MemEncVal":' + sMemEncryptedValue + '}';
            return ResponseLink; 
         
         }
        catch(Exception ex)
        {   
            bShowServiceError = true;
            String sError = System.Label.EncrytRest_Service_Error;
            HUMExceptionHelper.bRestrictLog = true;
            return sError;
        }
  
    }
    
       /*
    * Method Name   :  responseParse
    * Description   :  This method is used to parse response from Encryt method Encrypt Rest Service.
    * Return Type   :  String   Encryted Mem Gen Key 
    * Parameters    :  NA
    */
    
    @testvisible public static String responseParse(HttpResponse sResponse)
    {  String sEncyptedMemGenKey ='';
       lstMemGenKeyDetails =  new MemberPreferencesLink_LDTO_HUM();
        Integer iStatusCode = sResponse.getStatusCode();
        if (iStatusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || iStatusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
        { 
            lstMemGenKeyDetails = oServiceEncrypt.processResponse(sResponse.getbody());
          if(lstMemGenKeyDetails ==null)
            {
                String sError = System.Label.EncryptRestService_No_records;
                return sError;
               
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
            return sError;
            
        } 
    return sEncyptedMemGenKey;              
    }  
}