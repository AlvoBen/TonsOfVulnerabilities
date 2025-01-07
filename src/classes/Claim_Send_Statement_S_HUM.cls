/**************************************************************************************************************************************************
Apex class Name    :  Claim_Send_Statement_S_HUM 
Version            :  1.0
Function           :  This is the service class for claim send statement
Test Class         :  Claim_Send_Statement_S_T_HUM

Modification Log:

*   Developer                   Code Review             Date               Description
* -------------------------------------------------------------------------------------------------------------------------------------------------  
*  Vijaya Koriprolu                                 09/24/2018          origina Version
************************************************************************************************************************************************* */

public  with sharing class Claim_Send_Statement_S_HUM
{
    @TestVisible private String sBody ='' ;
    public  Claim_Send_Statement_DTO_HUM clmSendWrapObj{get;set;}
    public  List<Claim_Send_Statement_DTO_HUM> InteractionDetails{get;set;}
    private Claim_Send_Statement_DTO_HUM oSClaimsWrap = new Claim_Send_Statement_DTO_HUM();
    
 /*
*  This method is used to form http request for Encrypt Rest Servive
*  @name   setupRequest
*  @param  HttpRequest - instance of HttpRequest, HUM_Webservice_Callout__c  - contains webservice callout details, 
*  @return HttpRequest - request for Get Cases and Interaction 
*  @throws setupRequest exception
*/   
    public HttpRequest setupRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcAddress,String sAddressLine1, String sAddressLine2,String sCity, String sStateCode, String sZipCode)
    {
        HttpRequest orequest = new HttpRequest();    
        try
        { 
            sBody =  buildRequest(sAddressLine1, sAddressLine2, sCity, sStateCode, sZipCode);
           
            if(String.isNotBlank(sBody))
            { 
                request.setMethod(oSvcAddress.Request_Type__c); 
                request.setHeader(GLOBAL_CONSTANT_HUM.CONTENT_TYPE_HUM, oSvcAddress.Content_Type__c);  
                request.setHeader(GLOBAL_CONSTANT_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_HUM.KEEP_ALIVE_HUM);
                request.setClientCertificateName(oSvcAddress.Certificate_Name__c);
                
                request.setTimeout(Integer.valueof(oSvcAddress.Timeout__c));
                request.setEndpoint(oSvcAddress.End_Point_URL__c);
                request.setBody(sBody);
                orequest = request; 
            } 
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, 'Claim_Send_Statement_S_HUM' , 'setupRequest');
            orequest = null;
        }
        return orequest; 
    }  
    
    @testvisible private string buildRequest(String sAddressLine1, String sAddressLine2,String sCity, String sStateCode, String sZipCode)
    {    
        string sJsonbody = '';
        try
        { 
            Claim_Send_Statement_Request_H_HUM oRequest = new Claim_Send_Statement_Request_H_HUM(); 
            Claim_Send_Statement_Request_H_HUM.StandardizeAddressRequest getSendStmtRequest = new Claim_Send_Statement_Request_H_HUM.StandardizeAddressRequest();
            getSendStmtRequest.AddressLine1 = sAddressLine1;
            getSendStmtRequest.AddressLine2 = sAddressLine2;
            getSendStmtRequest.City = sCity;
            getSendStmtRequest.StateCode = sStateCode;
            getSendStmtRequest.ZipCode = sZipCode;
            oRequest.StandardizeAddressRequest = getSendStmtRequest;
            sJsonbody = JSON.serialize(oRequest); 
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, 'Claim_Send_Statement_S_HUM' , 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    } 
    
    
    
    public void processResponse(string response) 
    {  
        Claim_Send_Statement_Response_H_HUM objRestResp = new Claim_Send_Statement_Response_H_HUM();
        objRestResp = Claim_Send_Statement_Response_H_HUM.parse(response);
        if(objRestResp.StandardizeAddressResponse != Null)
        { 
            
            oSClaimsWrap  = parseServiceResponse(objRestResp);
        }
    }
    
    private Claim_Send_Statement_DTO_HUM parseServiceResponse(Claim_Send_Statement_Response_H_HUM objRestResp)
    { 
        clmSendWrapObj  = new Claim_Send_Statement_DTO_HUM();
        Claim_Send_Statement_Response_H_HUM.StandardizeAddressResponse oStandaredAddResults = objRestResp.StandardizeAddressResponse;
        clmSendWrapObj.sSuccessFlag= oStandaredAddResults.SuccessFlag; 
        clmSendWrapObj.sAddressLine1 = oStandaredAddResults.AddressLine1; 
        
        clmSendWrapObj.sCity= oStandaredAddResults.City; 
        clmSendWrapObj.sZipCode= oStandaredAddResults.ZipCode; 
        clmSendWrapObj.sStateCode= oStandaredAddResults.StateCode; 
        clmSendWrapObj.sStateID= oStandaredAddResults.StateID;
        clmSendWrapObj.sZipCodePlus= oStandaredAddResults.ZipCodePlus; 
        clmSendWrapObj.sCountyName = oStandaredAddResults.CountyName; 
        clmSendWrapObj.sCountyID = oStandaredAddResults.CountyID; 
        clmSendWrapObj.sValid= oStandaredAddResults.Valid; 
        
        clmSendWrapObj.sChangeIndicator = oStandaredAddResults.ChangeIndicator; 
        return clmSendWrapObj;
    }
    
    
    
    
}