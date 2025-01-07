/*********************************************************************************************************************************************     
Apex Class Name  : PhysicianFinder_S_HUM 
Created Date     : 11 Nov 2021
Function         : 
**Modification Log
* Developer Name          Code Review          Date                       Description
******************************************************************************************************************************************************
* Suraj Patil								   11/10/2021				  REQ - 2512130 T1PRJ0193862 - MF 01 - Ability to attach PFP email automatically to CRM case (Mbr Policy Page)
*****************************************************************************************************************************************************/
public  with sharing class PhysicianFinder_S_HUM
{
    @TestVisible private String sBody ='' ;
    private EncryptStringResponse_H_HUM lstEncValue = new EncryptStringResponse_H_HUM();
 
   /*
    *  This method is used to form http request for Encrypt Rest Service
    *  @name   setupRequest
    *  @param  HttpRequest - instance of HttpRequest, HUM_Webservice_Callout__c  - contains web service callout details, 
    *  @return HttpRequest - request for Encrypt Array Rest Service
    *  @throws setupRequest exception
    */
    public HttpRequest setupRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcEncrypt, Map<String,String> mapValues)
    {
      HttpRequest orequest = new HttpRequest();    
        try
        { 
            sBody = buildRequest(mapValues);
			
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
            HUMExceptionHelper.logErrors(ex, 'PhysicianFinder_S_HUM' , 'setupRequest');
            orequest = null;
        }
       return orequest; 
    }  
    
       
   /*
    *  This method is used to Process the Json response from service to Wrapper List
    *  @name   buildRequest
    *  @param  String sMemGenKey - member gen key of account
    *  @return string - Returns the encrypted array of the string
    *  @throws NA
    */ 
    @testvisible private string buildRequest(Map<String,String> mapValues)
    {    
        string sJsonbody = '';
        try
        { 
            EncryptStringRequest_H_HUM oRequest = new EncryptStringRequest_H_HUM(); 
            EncryptStringRequest_H_HUM.EncryptStringArray oEncrypt = new EncryptStringRequest_H_HUM.EncryptStringArray();
            List<string> lstStr = new List<string>();
            
            for(string str : mapValues.keyset())
            {
                lstStr.add(mapValues.get(str));
            }
            oEncrypt.Value = lstStr;
            oEncrypt.Application = 'CRM';
            oRequest.EncryptStringArray = oEncrypt; 
            sJsonbody = JSON.serialize(oRequest); 
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, 'PhysicianFinder_S_HUM' , 'buildRequest');
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
    public EncryptStringResponse_H_HUM processResponse(string response) 
    { 
        EncryptStringResponse_H_HUM objRestResp = new EncryptStringResponse_H_HUM ();
        lstEncValue  = (EncryptStringResponse_H_HUM ) System.JSON.deserialize(response, EncryptStringResponse_H_HUM.class);
        return lstEncValue ;
    }
}