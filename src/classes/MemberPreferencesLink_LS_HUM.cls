/**************************************************************************************************************************************************
Apex class Name    :  MemberPreferencesLink_LS_HUM 
Version            :  1.0
Function           :  This is the service class for Encrypt Rest Servive and return encrypted Member Gen Key
Test Class         :  MemberPreferencesLink_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 *  Vishal Shinde                                     18/01/2023           4167387- Implementation of Links & landing pages- Person account page and Medical Plan member page - Part2
 ************************************************************************************************************************************************************************************/

public with sharing class MemberPreferencesLink_LS_HUM {
	   @TestVisible private String sBody ='' ;
    private MemberPreferencesLink_LDTO_HUM lstEncValue = new MemberPreferencesLink_LDTO_HUM();
 
   /*
    *  This method is used to form http request for Encrypt Rest Servive
    *  @name   setupRequest
    *  @param  HttpRequest - instance of HttpRequest, HUM_Webservice_Callout__c  - contains webservice callout details, 
                           sMemGenKey - Member Gen Key of the account                          
    *  @return HttpRequest - request for Encrypt Rest Servive
    *  @throws setupRequest exception
    */
    public HttpRequest setupRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcEncrypt, String sMemGenKey)
    {
      HttpRequest orequest = new HttpRequest();    
        try
        { 
            sBody = buildRequest(sMemGenKey);
            
            if(String.isNotBlank(sBody))
            { 
               request.setMethod(oSvcEncrypt.Request_Type__c); 
                request.setHeader(GLOBAL_CONSTANT_HUM.CONTENT_TYPE_HUM, oSvcEncrypt.Content_Type__c);  
                request.setHeader(GLOBAL_CONSTANT_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_HUM.KEEP_ALIVE_HUM);
                if(!Test.isRunningTest())    
                {
                    request.setClientCertificateName(oSvcEncrypt.Certificate_Name__c);
                }
                request.setTimeout(Integer.valueof(oSvcEncrypt.Timeout__c));
                request.setEndpoint(oSvcEncrypt.End_Point_URL__c);
                request.setBody(sBody);
                orequest = request; 
            } 
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, 'MemberPreferencesLink_LS_HUM' , 'setupRequest');
            orequest = null;
        }
       return orequest; 
    }  
    
       
   /*
    *  This method is used to Process the Json response from service to Wrapper List
    *  @name   buildRequest
    *  @param  String sMemGenKey - member gen key of account
    *  @return string - Returns the encrypted mem gen key
    *  @throws NA
    */ 
    @testvisible private string buildRequest(String sMemGenKey)
    {    
        string sJsonbody = '';
        try
        { 
            MemberPreferencesLink_LH_HUM oRequest = new MemberPreferencesLink_LH_HUM(); 
            MemberPreferencesLink_LH_HUM.Encrypt oEncrypt = new MemberPreferencesLink_LH_HUM.Encrypt();
            oEncrypt.Value = sMemGenKey;
			oEncrypt.Application = GLOBAL_CONSTANT_HUM.MemberPreference;
            oRequest.Encrypt = oEncrypt; 
            sJsonbody = JSON.serialize(oRequest); 
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, 'MemberPreferencesLink_LS_HUM' , 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    } 
    
       /*
    *  This method is used to Process the Json response from service to Object
    *  @name   processResponse
    *  @param  string(Json response) - Body of the response in JSON format
    *  @return List<MemberPreferencesLink_DTO_HUM> - Object
    *  @throws NA
    */ 
    public MemberPreferencesLink_LDTO_HUM processResponse(string response) 
    {  
        MemberPreferencesLinkResponse_LH_HUM  objRestResp = new MemberPreferencesLinkResponse_LH_HUM ();
        objRestResp = MemberPreferencesLinkResponse_LH_HUM.parse(response);
        if(objRestResp.EncryptResponse != Null)
        { 
            lstEncValue  = parseServiceResponse(objRestResp);
        }
        return lstEncValue ;
    }
    
       /*
    *  This method is used to parse the reponse and assign it to the wrapper class objects
    *  @name   parseServiceResponse
    *  @param  MemberPreferencesLinkResponse_LH_HUM - The Response object
    *  @throws NA
    */  
    private MemberPreferencesLink_LDTO_HUM parseServiceResponse(MemberPreferencesLinkResponse_LH_HUM objRestResp)
    { 
       MemberPreferencesLink_LDTO_HUM encryptedDetails = new MemberPreferencesLink_LDTO_HUM();
       MemberPreferencesLinkResponse_LH_HUM.EncryptResponse oEncryptResponse = objRestResp.EncryptResponse;
       MemberPreferencesLinkResponse_LH_HUM.EncryptResult oEncryptResult = oEncryptResponse.EncryptResult;
       encryptedDetails.sEncValue = oEncryptResult.EncryptValue; 
       return encryptedDetails;
    }
}