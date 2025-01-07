/*******************************************************************************************************************************
Apex Class Name : guidedFlowAuthSummary_LS_Hum
Version         : 1.0
Created On      : 05/11/2022
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Pinky Vijur                                               05/11/2022          User Story 2892985: T1PRJ0022885/96118 - SF - Tech - CR7/MF 10 -(HMO/IPA) Ability to house all ASO and FI Custom, along w/ FI General medical auths criteria within CRM
*Tharun Madishetti                                         07/19/2022          US-3491274:T1PRJ0533784 - MF 10 - INC1809196 Restrict code selection to one service code on the GBO Authorization and Referral Requirements template
*Anusha C                                                  07/17/2023          US-4812126 Critical- Lightning - Generic             
*********************************************************************************************************************************/
public  with sharing class guidedFlowAuthSummary_LS_Hum{


    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String AUTH_RESPONSE_ERROR = 'Authorization requirements were unable to be determined. See your leader for assistance.';
    Public Static final String AUTH_DEFAULT_Y_MESSAGE = 'Authorization is Required';
    Public Static final String AUTH_DEFAULT_N_MESSAGE = 'Authorization is NOT Required';
    
    
    @AuraEnabled(continuation=true)
            public static Continuation invokeAuthRefRequest(String sRecordId, AuthRefRequest_DTO_HUM Obj){    
                 try
            {
                HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('CheckAuthRequired');
                // Create continuation with a timeout
                integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                con.continuationMethod = 'parseAuthRefResponse';
                con.state = sRecordId;
                HttpRequest httpRSGetMemberReqObj = builtHttpRequest(RSServiceDetails);
                httpRSGetMemberReqObj.setBody(JSON.serialize(Obj));
                con.addHttpRequest(httpRSGetMemberReqObj);
                Return con;
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex,'guidedFlowAuthSummary_LS_Hum', 'invokeAuthRefRequest');
                return null;
            }
            }
            
            
                    
         /* Returns HttpRequest request with request body
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
                    if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUMConstants.HUM_RSS_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
                    request.setHeader(HUMConstants.HUM_RSS_SER_CONECTION, HUMConstants.HUM_RSS_SER_KEEPALIVE);
                    request.setEndpoint(ServiceObj.End_Point_URL__c);
                    request.setHeader(HUMConstants.HUM_RSS_SER_SOAPACTION, '');
                }
                return request;
            } 
            
        public static object parseAuthRefResponse(List < string > labels, Object State)
        {
            String clinicalMessage = '';
    
            try
            {
                HttpResponse responsSummary = Continuation.getResponse(labels[0]);
   
                if(responsSummary != null){
                    boolean validResponseSummary = ( (responsSummary.getStatusCode() == HTTP_OK_HUM  ||
                                                      responsSummary .getStatusCode() == HTTP_ACCEPTED_HUM)
                                                    &&
                                                     (!responsSummary.getBody().containsIgnoreCase(VOB_FAULT_CODE)) 
                                                   );
 
                    if(validResponseSummary || Test.isRunningTest()){
                        String createResponseBody = responsSummary.getBody();
                        AuthRefResponse_DTO_HUM responseIsAuthRequired ;
                        responseIsAuthRequired = (AuthRefResponse_DTO_HUM) System.JSON.deserialize(createResponseBody, AuthRefResponse_DTO_HUM.Class);
                                  
                        boolean validResponseIsAuthRequired = (responseIsAuthRequired != null && responseIsAuthRequired.Isauthrequiredresponse != null && responseIsAuthRequired.Isauthrequiredresponse.ServiceDetails.RedirectionMessage != null && responseIsAuthRequired.Isauthrequiredresponse.ServiceDetails.RedirectionMessage != '' && responseIsAuthRequired.Isauthrequiredresponse.ServiceDetails.RedirectionMessage != ' ');
                        boolean validIsAuthRequired = (responseIsAuthRequired.Isauthrequiredresponse.ServiceDetails.ProcedureCodes[0].IsAuthRequired != null && responseIsAuthRequired.Isauthrequiredresponse.ServiceDetails.ProcedureCodes[0].IsAuthRequired != '');
                        boolean IsAuthRequired_Y = (responseIsAuthRequired.Isauthrequiredresponse.ServiceDetails.ProcedureCodes[0].IsAuthRequired == 'Y');
                        boolean IsAuthRequired_N = (responseIsAuthRequired.Isauthrequiredresponse.ServiceDetails.ProcedureCodes[0].IsAuthRequired == 'N');
                        
                        clinicalMessage = validResponseIsAuthRequired ? responseIsAuthRequired.Isauthrequiredresponse.ServiceDetails.RedirectionMessage:                        
                                          ((validIsAuthRequired && IsAuthRequired_Y) ? AUTH_DEFAULT_Y_MESSAGE: 
                                          ((validIsAuthRequired && IsAuthRequired_N) ? AUTH_DEFAULT_N_MESSAGE: AUTH_RESPONSE_ERROR));                                      
                    }
                }
                else
                {
                    clinicalMessage = AUTH_RESPONSE_ERROR;
                }
                return clinicalMessage;
    
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex,'guidedFlowAuthSummary_LSH_Hum', 'parseAuthRefResponse');
                clinicalMessage = AUTH_RESPONSE_ERROR;
    
                return clinicalMessage;
            }
        }
              
}