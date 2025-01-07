/*****************************************************************************************
Apex class Name    : GroupDetailSection_S_HUM_V2
Version            : 1.0
Function           : This service class is used in process to invoke the GroupBusinessEntityService
Created On         : March 10 2016
Test Class         : GroupDetailSection_S_T_HUM_V2

Modification Log:
*   Developer                   Code Review           Date               Description
* ------------------------------------------------------------------------------------------------------------------------------ 
*  Praveen Kumar Parimi          23955              03/10/2016          Original Version  
********************************************************************************************************************************/
public with sharing class GroupDetailSection_S_HUM_V2
{       
    /*
     * Method Name   :  setuprequestToGBE
     * Description   :  This method is used to create request DTO and to call the GBE Service
     * Return Type   :  HttpRequest
     * Parameters    :  HUM_Webservice_Callout__c,String
    */    
        public HttpRequest setuprequestToGBE(HUM_Webservice_Callout__c oSvcHeaderGBEService, string sRequestBody)
        {
        	HttpRequest request = new HttpRequest();
        	
        	if(!String.isBlank(sRequestBody)&&(oSvcHeaderGBEService!=null)&&(oSvcHeaderGBEService.Active__c))
        		{
            		request.setMethod(oSvcHeaderGBEService.Request_Type__c);
            		request.setBody(sRequestBody);

            		if(!Test.isRunningTest() && String.isNotBlank(oSvcHeaderGBEService.Certificate_Name__c))
            		{
                		request.setClientCertificateName(oSvcHeaderGBEService.Certificate_Name__c); 
            		}                    
            		if(String.isNotBlank(oSvcHeaderGBEService.Content_Type__c))
            		{
                		request.setHeader(Label.HUMServiceTesterController_content_type, oSvcHeaderGBEService.Content_Type__c);
            		}
			        request.setHeader(Label.HUMServiceTesterControllerConnection,Label.HUMServiceTesterController_keep_alive);
			        request.setHeader(Label.HUMServiceTesterControllerSOAPAction,'');
			        request.setEndpoint(oSvcHeaderGBEService.End_Point_URL__c);            
            		return request;
        		}
        	else return null;
    	}  

}