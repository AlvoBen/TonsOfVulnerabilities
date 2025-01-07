/************************************************************************************************************************************************************************************       
Apex Class Name  : ExternalLinkLauncher_LS_Hum
Created Date     : 05/27/2022
Function         : 
**Modification Log
* Developer Name                   Date                       Description
************************************************************************************************************************************************************************************
* Kalyani Pachpol                  05/27/2022                 User Story 3150124: Benefits External link
************************************************************************************************************************************************************************************/
public with sharing class ExternalLinkLauncher_LS_Hum {
public static Continuation SendRequestForEncryption(String MemberId)
    {
        try
        {
            HUM_Webservice_Callout__c EncryptionService = HUM_Webservice_Callout__c.getValues('EncryptService');
            Integer iTIMEOUT_INT_SECS = Integer.valueof(EncryptionService.Timeout__c) / 100;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'ProcessEncryptService';
            HttpRequest httpEncryptReqObj = RxConnect_LS_HUM.builtHttpRequest(EncryptionService);
            httpEncryptReqObj.setBody(JSON.serialize(RxConnect_LD_HUM.RequestStructureEncryptService(MemberId)));
            con.addHttpRequest(httpEncryptReqObj);
            return con;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'RxConnect_LS_HUM', 'SendRequestForEncryption');
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
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(ExternalLinkLauncherConstant_HUM.HUM_PCP_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
                request.setHeader(ExternalLinkLauncherConstant_HUM.HUM_PCP_SER_CONECTION, ExternalLinkLauncherConstant_HUM.HUM_PCP_SER_KEEPALIVE);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                //Fixed PLT - CA -8649986 - RX is not prefilling the member data, AS soap action was failing from backend.
                //request.setHeader(ExternalLinkLauncherConstant_HUM.HUM_PCP_SER_SOAPACTION,'Encrypt');
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
        if (responseEncrypt != null && ((responseEncrypt.getStatusCode() == ExternalLinkLauncherConstant_HUM.HTTP_OK_HUM || responseEncrypt.getStatusCode() == ExternalLinkLauncherConstant_HUM.HTTP_ACCEPTED_HUM) && (!responseEncrypt.getBody().containsIgnoreCase(ExternalLinkLauncherConstant_HUM.VOB_FAULT_CODE)) || Test.isRunningTest()))
        {
            EncryptionServiceResponse_DTO_HUM oResponseEncrypt = (EncryptionServiceResponse_DTO_HUM)System.JSON.deserialize(responseEncrypt.getBody(), EncryptionServiceResponse_DTO_HUM.class);
            sEncryptValueResponse = RxConnect_LD_HUM.parseEncrypt(oResponseEncrypt);
        }
        return sEncryptValueResponse;
    }
}