/**************************************************************************************************************************************************
Apex class Name    :  GAPD_LS_HUM.cls
Version            :  1.0
Function           :  This is Service Helper class to GAPD_LC_HUM.It forms request and parse response.
Test Class         :  GrievanceAndAppeal_LT_HUM

Modification Log:
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Abhishek Mangutkar                          09/05/2022                 Original Version
 * Aishwarya Pawar                          	10/13/2022                 REQ -3825306 ProviderDispute Service call changes 

 ************************************************************************************************************************************************* */

public with sharing class GAPD_LS_HUM
{
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202;
    Public Static final String SOAPACTION_HUM = 'SOAPAction';
    Public Static final String CONTENT_TYPE_HUM = 'content-type';
    Public Static final String CONNECTION_HUM = 'Connection';
    Public Static final String KEEP_ALIVE_HUM = 'keep-alive';
    public class ServiceDownException extends Exception {}
   
        
    //new service method
    public static Continuation sendGAPDRequest(string sRecordId, string requestBody, boolean providerDisputeFlag)
    {
        
        HUM_Webservice_Callout__c oGAService;
        oGAService = providerDisputeFlag ?  HUM_Webservice_Callout__c.getValues('ProviderCorrespondenceDisputes'):HUM_Webservice_Callout__c.getValues('GrievanceAndAppeal');
        Integer iTIMEOUT_INT_SECS = Integer.valueof(oGAService.Timeout__c)/1000;
        Continuation conGo = new Continuation(60);
        try
        {  
            if(string.isNotBlank(requestBody))
            {
				HttpRequest request = new HttpRequest();
                request.setMethod(oGAService.Request_Type__c); 
                request.setHeader(GLOBAL_CONSTANT_HUM.CONTENT_TYPE_HUM, oGAService.Content_Type__c);  
                request.setHeader(SOAPACTION_HUM, '');    
                request.setHeader(CONNECTION_HUM,KEEP_ALIVE_HUM);
                request.setBody(requestBody);
                if(!Test.isRunningTest())    request.setClientCertificateName(oGAService.Certificate_Name__c);    
                request.setTimeout(Integer.valueof(oGAService.Timeout__c));
                request.setEndpoint(oGAService.End_Point_URL__c);
                conGo.continuationMethod = 'processGAPDResponse';
                conGo.state = providerDisputeFlag;
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
            HUMExceptionHelper.logErrors(ex, 'GAPD_LS_HUM', 'sendGAPDRequest');
            return null;
        }
        
        return conGo;  
    }
    
    @testvisible
    public static object processGAPDResponse(List<string> labels, object state)
    { 
        String response;
		try{       
           	
        	HttpResponse sResponse = Continuation.getResponse(labels[0]); 
            response = sResponse.getbody();
          	Boolean providerDisputeFlag= (Boolean)state; 
           
          	if ((sResponse != null && !sResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) &&
            (sResponse.getStatusCode() == HTTP_OK_HUM || sResponse.getStatusCode() == HTTP_ACCEPTED_HUM)
            ) || Test.isRunningTest())
         	{    
                 if(sResponse.getbody() == '' && Test.isRunningTest())  throw new ServiceDownException('SERVICEERROR'+sResponse.getBody());
                 return sResponse.getbody();
         	}
         	else{ 
             	throw New ServiceDownException('SERVICEERROR'+sResponse.getBody());
			 } 
        }
        catch (ServiceDownException e)
        {
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(e, 'GAPD_LS_HUM','SERVICE_FAILURE');
            errorLogList.add(logObj);
            string internalError= errorLog(errorLogList);
            return internalError;
        } 
        catch (Exception ex)
        {   
            
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(ex,'GAPD_LS_HUM', 'processGAPDResponse');
            errorLogList.add(logObj);
            string internalError= errorLog(errorLogList);
            return internalError;
        }
                   
    }
	
	public static String errorLog(List<Error_Log__c> errorLogList )
	{ 
               string errorNumber='';
         	   string internalError='';
               Database.SaveResult[] srList =  Database.insert(errorLogList, false);
			   List<Error_Log__c> lstError = new List<Error_Log__c>();
               Set<ID>  ids = new Set<ID>();   
               for (Database.SaveResult sr : srList) 
               {
                    if (sr.isSuccess()) {
                      ids.add(sr.getId());
                      
                    }
                } 
               lstError = [Select Name from Error_Log__c where Id = :ids];
			   errorNumber = lstError[0].Name;
             
              internalError = 'TESTFAILURE'+'*'+ errorNumber  + '#';
              return internalError;
	}
   
}