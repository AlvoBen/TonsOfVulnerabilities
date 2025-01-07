/**************************************************************************************************************************************************
Apex class Name    :  ViewDocuments_LS_HUM
Version            :  1.0
Function           :  This is Service Helper class to CaseRelatedDocument_LC_HUM.It forms request and parse response.
Test Class         :  CaseRelatedDocument_LT_HUM

Modification Log:
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Kalyani Pachpol                           09/02/2022                 Original Version
 * Jonathan Dickinson                        06/28/2023                 User Story 4759866: T1PRJ0891415 Platform Management- SF - TECH - LEGO - Blocker - ViewDocuments_LS_HUM : Avoid Soql queries inside loops.
 ************************************************************************************************************************************************* */

 public with sharing class ViewDocuments_LS_HUM {
    @TestVisible private static String sBody ='' ;
        public class ServiceDownException extends Exception {}
        
        Public Static final String Content_type_HUM = 'content-type';
        Public Static final String SOAPAction_HUM = 'SOAPAction';
        Public Static final String Connection_HUM = 'Connection';
        Public Static final String Keep_alive_HUM = 'keep-alive';
        Public Static final String VOB_FAULT_CODE = 'faultcode';
        Public Static final Integer HTTP_OK_HUM = 200;
        Public Static final Integer HTTP_ACCEPTED_HUM = 202;
    
         public static Continuation sendViewDocumentsRequest(string requestBody)
        {
            HUM_Webservice_Callout__c oGAService = HUM_Webservice_Callout__c.getValues('ViewDocuments');
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oGAService.Timeout__c)/1000;
            Continuation conGo = new Continuation(iTIMEOUT_INT_SECS);
            try
            {  
                if(string.isNotBlank(requestBody))
                {
                    HttpRequest request = new HttpRequest();
                    request.setMethod(oGAService.Request_Type__c); 
                    request.setHeader(CONTENT_TYPE_HUM, oGAService.Content_Type__c);  
                    request.setHeader(SOAPACTION_HUM, '');    
                    request.setHeader(CONNECTION_HUM,KEEP_ALIVE_HUM);
                    request.setBody(requestBody);
                    if(!Test.isRunningTest())    request.setClientCertificateName(oGAService.Certificate_Name__c);    
                    request.setTimeout(Integer.valueof(oGAService.Timeout__c));
                    request.setEndpoint(oGAService.End_Point_URL__c);                
                    conGo.continuationMethod = 'processViewDocumentsResponse';
                    conGo.addHttpRequest(request);
                }
                else 
                { 			
                   if(Test.isRunningTest())  throw new HUMCustomException('Error');
                    return null;
                }				
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'ViewDocuments_LS_HUM', 'sendViewDocumentsRequest');  
                throw new AuraHandledException(ex.getMessage());
            }
            return conGo;  
            
        }
       
        @testvisible
        public static object processViewDocumentsResponse(List<string> labels, object State)
        {
            HttpResponse sResponse = Continuation.getResponse(labels[0]);
            List<DocumentList_DTO_HUM> lstDocumentsDTO = new List<DocumentList_DTO_HUM >();
            String response;
           Try{
               response = sResponse.getbody();
             if (sResponse != null && !sResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) && (sResponse.getStatusCode() == HTTP_OK_HUM || sResponse.getStatusCode() == HTTP_ACCEPTED_HUM))
           {
               if(sResponse.getbody() == '' && Test.isRunningTest())  throw new ServiceDownException('SERVICEERROR::'+sResponse.getBody() );
               return sResponse.getbody();
            }
             else{              
                    throw new ServiceDownException('SERVICEERROR::'+sResponse.getBody() ); 
                 }
            }
            catch(ServiceDownException e)
            {
                HUMExceptionHelper.logErrors(e, 'ViewDocuments_LS_HUM', 'processViewDocumentsResponse');
                throw new AuraHandledException(e.getMessage());
            }
            catch (Exception ex)
            {
                HUMExceptionHelper.logErrors(ex,'ViewDocuments_LS_HUM', 'processViewDocumentsResponse');
                throw new AuraHandledException(ex.getMessage());
            }
        }
        
    }