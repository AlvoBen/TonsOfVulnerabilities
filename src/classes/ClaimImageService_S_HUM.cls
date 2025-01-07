/*****************************************************************************************
Apex class Name    : ClaimImageService_S_HUM 
Version            : 1.0
Function           :This is Helper class to ClaimImage_C_Hum.It forms request.
Test Class         : ClaimImageService_T_HUM 

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 * Harika Devi Kalidindi         25398                 02/29/2016          Original Version
 * Muralidhar Kollu              24438                 04/23/2016          Original Version
 
  ******************************************************************************************************************************/
  public with sharing class ClaimImageService_S_HUM 
  {
      /*
       * Returns an object 
       * <p>
       * This method is used to assign values to the request helper class
       * @param  String,String
       * @return ImagingServiceGetClaimURLRequest_H_HUM 
       */   
       public ImagingServiceGetClaimURLRequest_H_HUM assignValuesToHelper(String sClaimNumber, String sClient, String sStartDate)
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
       public HttpRequest setupRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcHeaderClaimsImg,String sRequestBody)
       { 
           oRequest.setMethod(oSvcHeaderClaimsImg.Request_Type__c);  
           oRequest.setBody(sRequestBody);
           oRequest.setHeader('content-type', oSvcHeaderClaimsImg.Content_Type__c); 
           oRequest.setHeader('SOAPAction', '');    
           oRequest.setHeader('Connection','keep-alive');
           if(!Test.isRunningTest())oRequest.setClientCertificateName(oSvcHeaderClaimsImg.Certificate_Name__c);
           oRequest.setTimeout(GLOBAL_CONSTANT_HUM.Service_Timeout_HUM);
           oRequest.setEndpoint(oSvcHeaderClaimsImg.End_Point_URL__c); 
           return oRequest;    
       }
  }