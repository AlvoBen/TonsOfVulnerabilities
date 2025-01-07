/*****************************************************************************************
Apex class Name    : ClaimImageService_LS_HUM 
Version            : 1.0
Function           :This is Helper class to ClaimImage_LC_Hum.It forms request.
Test Class         : ClaimImageService_T_HUM 

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 * Sagar.G                                                                Original Version REQ - 3856190:Ability as a CRM Service end user to view the Claim image for a specific claim within CRM Service - Claim Detail Page
 ******************************************************************************************************************************/
  public with sharing class ClaimImageService_LS_HUM 
  {
      /*
       * Returns an object 
       * <p>
       * This method is used to assign values to the request helper class
       * @param  String,String
       * @return ImagingServiceGetClaimURLRequest_H_HUM 
       */   
       public static ImagingServiceGetClaimURLRequest_H_HUM assignValuesToHelper(String sClaimNumber, String sClient, String sStartDate)
       {
           ImagingServiceGetClaimURLRequest_H_HUM oClaimImageReq = new ImagingServiceGetClaimURLRequest_H_HUM();
           if(sClaimNumber!= null && sClient!= null && sStartDate != null)
           {
              oClaimImageReq .ClaimNumber = sClaimNumber;
              oClaimImageReq .Client = sClient;
              oClaimImageReq .ServiceStartDate = sStartDate ;
              oClaimImageReq .ImageLoadDaysBuffer = GLOBAL_CONSTANT_HUM.ImageLoadDaysBuffer_HUM;
              return oClaimImageReq ;
           }
           else
           {
               return null;
           }
      }

      /*
       * Method Name   :  setupRequest
       * Description   :  This method is used to form http request.
       * Return Type   :  HttpRequest 
       * Parameters    :  HttpRequest,HUM_Webservice_Callout__c,string 
       */
       public static HttpRequest setupRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcHeaderClaimsImg,String sRequestBody)
       { 
           try{
           oRequest.setMethod(oSvcHeaderClaimsImg.Request_Type__c);  
           oRequest.setBody(sRequestBody);
           oRequest.setHeader('content-type', oSvcHeaderClaimsImg.Content_Type__c); 
           oRequest.setHeader('SOAPAction', '');    
           oRequest.setHeader('Connection','keep-alive');
           if(!Test.isRunningTest())oRequest.setClientCertificateName(oSvcHeaderClaimsImg.Certificate_Name__c);
           oRequest.setTimeout(GLOBAL_CONSTANT_HUM.Service_Timeout_HUM);
           oRequest.setEndpoint(oSvcHeaderClaimsImg.End_Point_URL__c); 
               return oRequest;
           }catch(exception e){
               system.debug('In catch'+e);
               return oRequest;
           }
               
       }
  }