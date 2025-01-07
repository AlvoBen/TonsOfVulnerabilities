/************************************************************************************************************************************************************************************       
Apex class Name      : RxConnect_S_HUM
Created Date         : 11/11/2019
Function             : Service Class for RxConnect_C_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Satyam Malviya								11/11/2019				   REQ-404584 - Original Version - Created new class for RX connect functionality
************************************************************************************************************************************************************************************/
public with sharing class RxConnect_S_HUM 
{
   public static Object SendRequestForEncryption(String MemberId)
    {
        try
        {
            HUM_Webservice_Callout__c EncryptionService = HUM_Webservice_Callout__c.getValues('EncryptService');
            Integer iTIMEOUT_INT_SECS = Integer.valueof(EncryptionService.Timeout__c) / 100;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = GLOBAL_CONSTANT_HUM.RXLOOKUP_PROCESS_ENCRYPTSERVICE;
            HttpRequest httpEncryptReqObj = RxConnect_S_HUM.builtHttpRequest(EncryptionService);
            httpEncryptReqObj.setBody(JSON.serialize(RxConnect_D_HUM.RequestStructureEncryptService(MemberId)));
            con.addHttpRequest(httpEncryptReqObj);
            return con;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'RxConnect_S_HUM', 'SendRequestForEncryption');
            return Null;
        }
    }
    
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        try
        {
            if (ServiceObj != null && ServiceObj.Active__c)
            {
                request.setMethod(ServiceObj.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUMConstants.HUM_PCP_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
                request.setHeader(HUMConstants.HUM_PCP_SER_CONECTION, HUMConstants.HUM_PCP_SER_KEEPALIVE);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
				//Fixed PLT - CA -8649986 - RX is not prefilling the member data, AS soap action was failing from backend.
                //request.setHeader(HUMConstants.HUM_PCP_SER_SOAPACTION,'Encrypt');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'RxConnect_S_HUM', 'builtHttpRequest');
            return Null;
        }
    }
    
    public static String ProcessEncryptService(List<String> labels, object State)
    {
        String sEncryptValueResponse;
        HttpResponse responseEncrypt = Continuation.getResponse(labels[0]);
        if (responseEncrypt != null && ((responseEncrypt.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || responseEncrypt.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) && (!responseEncrypt.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)) || Test.isRunningTest()))
        {
            EncryptionServiceResponse_DTO_HUM oResponseEncrypt = (EncryptionServiceResponse_DTO_HUM)System.JSON.deserialize(responseEncrypt.getBody(), EncryptionServiceResponse_DTO_HUM.class);
            sEncryptValueResponse = RxConnect_D_HUM.parseEncrypt(oResponseEncrypt);
        }
        return sEncryptValueResponse;
    }
}