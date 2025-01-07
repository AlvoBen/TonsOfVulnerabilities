/*******************************************************************************************************************************
Apex Class Name : PharmacyBenefits_S_HUM.cls
Version         : 1.0
Created On      : 20th June 2017
Function        : Service Class for PharmacyBenefits_C_HUM.
TestClass       : PharmacyBenefits_T_HUM
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Joel George                                          06/20/2017                   Original Version (REQ 305017)
* Joel George				125710 					   09/05/2017					Changes for Req 322640
* Lakshmi Madduri										10/30/2019					Pharmacy Incomm Service
* Sayali Nemade											4/29/2020					REQ - 842472 - Pharmacy InComm Service V2
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
* Pinky Vijur                                           01/23/2023                User Story 3578587: T1PRJ0870026 Solutran Integrations - CRM FR5.01 - TECH - Remove CRM Service coding related to InComm OTC Balance
*****************************************************************************************************************************************/

public with sharing Class PharmacyBenefits_S_HUM 
{
     Public Static final String HUM_PRIORAUTH_RESPONSE = 'parseGetPAuthResponse';
     Public Static final String Content_type_HUM = 'content-type';
     Public Static final String Connection_HUM = 'Connection';
     Public Static final String Keep_alive_HUM = 'keep-alive';
     Public Static final String SOAPAction_HUM = 'SOAPAction';
     Public Static final String VOB_FAULT_CODE = 'faultcode';
     Public Static final Integer HTTP_OK_HUM = 200;
     Public Static final Integer HTTP_ACCEPTED_HUM = 202;
     /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetpAuthResponse services .
     *
     * @param   String          Enterprise Id
     * @return  Continuation
     */
    public static Continuation SendRequestGetpAuthResponse(String sMemID, String accID)
    {
        try
        {
            HUM_Webservice_Callout__c PAHubDetails = HUM_Webservice_Callout__c.getValues('PriorAuthHub');
            
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(PAHubDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = HUM_PRIORAUTH_RESPONSE; 
			con.State = accID;
            HttpRequest httpPAuthReqObj = builtHttpRequest(PAHubDetails);
            httpPAuthReqObj.setBody(JSON.serialize(PharmacyBenefits_D_HUM.RequestStructureGetpAuthResponse(sMemID)));
            con.addHttpRequest(httpPAuthReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyBenefits_S_HUM', 'SendRequestGetpAuthResponse');
            return null;
        }
    }
    

      /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        if (ServiceObj != null && ServiceObj.Active__c)
        {
            request.setMethod(ServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(Content_type_HUM, ServiceObj.Content_Type__c);
            request.setHeader(Connection_HUM, Keep_alive_HUM);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(SOAPAction_HUM,'');
        }
        return request;
    }  
    
     /**
     * Returns responses of continuation object
     * <p>
     * parseGetPAuthResponse Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static Object parseGetPAuthResponse(List < string > labels, Object State)
    {
        try
        {
            List<PriorAuthHubWrapper> oResponse = new List<PriorAuthHubWrapper>();
            HttpResponse pAResponse = Continuation.getResponse(labels[0]);
      String sFaultResponse = '';
            if (pAResponse != null && !pAResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)  && ((pAResponse.getStatusCode() == HTTP_OK_HUM || pAResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                String paHubResponseBody = pAResponse.getBody();
                PriorAuthHubResponse_DTO_HUM PAHubInstance;
                PAHubInstance = (PriorAuthHubResponse_DTO_HUM)System.JSON.deserialize(paHubResponseBody,PriorAuthHubResponse_DTO_HUM.class);
                oResponse = PharmacyBenefits_D_HUM.preparePriorAuthDetails(PAHubInstance);
            }else
            {
              // Fault Code
              sFaultResponse = Pharmacy_H_HUM.logHpFaultError(pAResponse,'PharmacyBenefits_S_HUM', 'parseGetPAuthResponse', State != null ? String.valueof(State) : '').contains('Member not found') ? 'true' : 'false';
            }
            return oResponse;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'PharmacyBenefits_S_HUM', 'parseGetPAuthResponse');
            return null;
        }
    }
    
}