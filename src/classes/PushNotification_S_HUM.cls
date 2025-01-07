/******************************************************************************************************************************
Apex Class Name  : PushNotification_S_HUM
Version          : 1.0 
Created Date     : June 18 2021
Function         : Service class to handle the Push Notification Service callout for Coaching cases.                   
Test Class       : HUMFeedItemHelperTest
Modification Log :
   Developer                           Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Moshitha Gunasekaran                 06/18/2021           Original version. 
* Moshitha Gunasekaran                 07/27/2021           Made changes as per latest payload updates
*********************************************************************************************************************************/
public with sharing class PushNotification_S_HUM {

    public static string PushNotificationServiceCallout = 'PushNotificationService';

    /**
	* Method to make callout to Push Notification service for Coaching Cases. 
	*
	* @param	sPersonId - PersonId for whom notification of new message to be sent to Go365. 
	* @param	sFeedItemId - FeedItem or FeedComment that is created. 
    * @param	sFeedElementType - What kind of Feed created for that member
	*/
    @future (callout=true)
    public static void callPushNotificationService(String sPersonAccountId, String sFeedItemId, String sFeedElementType) 
	{		
		 
        PushNotification_DTO_HUM oCoaching_Request;
        try 
        {
            
            HUM_Webservice_Callout__c oCoaching_SvcHeaders = HUM_Webservice_Callout__c.getValues(PushNotificationServiceCallout);
            
            List<Account> lstAccount = [SELECT Id, Enterprise_Id__c FROM Account where Id = :sPersonAccountId and ETL_Record_Deleted__c = false limit 1];
            
            if(null != lstAccount && !lstAccount.isEmpty())
            {
                if(String.isNotBlank(lstAccount[0].Enterprise_Id__c) && String.isNotBlank(sFeedItemId) && String.isNotBlank(sFeedElementType))
                {
                    oCoaching_Request = createPushNotiServiceRequest(lstAccount[0].Enterprise_Id__c, sFeedItemId, sFeedElementType);			
                    if(oCoaching_Request!=null && oCoaching_SvcHeaders!=null)
                    {
                        String sBody = JSON.serialize(oCoaching_Request);				
                        if(String.isNotBlank(sBody))
                        {				
                            HttpRequest req = new HttpRequest();
                            req.setEndpoint(oCoaching_SvcHeaders.End_Point_URL__c);
                            req.setMethod(oCoaching_SvcHeaders.Request_Type__c);
                            req.setTimeout(Integer.valueOf(oCoaching_SvcHeaders.Timeout__c));
                            req.setBody(sBody);
                            if (!Test.isRunningTest()) req.setClientCertificateName(oCoaching_SvcHeaders.Certificate_Name__c);
                            req.setHeader(Label.HUMServiceTesterController_content_type, oCoaching_SvcHeaders.Content_Type__c);
                            Http http = new Http();
                            HttpResponse oCoaching_Response;
                            if(!Test.isRunningTest())
                            {
                                oCoaching_Response = http.send(req);
                            }
                            else {
                                oCoaching_Response = new HttpResponse();
                                oCoaching_Response.setHeader('Content-Type', 'application/json');
                                oCoaching_Response.setBody('{"Fault": { "errorInfo": { "error-headers": "HTTP/1.1 500 Internal Server Error\r\nX-Backside-Transport: FAIL FAIL\r\nContent-Type: text/xml\r\nConnection: close\r\n\r\n", "error-message": "No tokens found for user/app combination." } } }');
                                oCoaching_Response.setStatusCode(200);
                           }
                            if (oCoaching_Response.getStatusCode() == 200 && oCoaching_Response.getbody() != null && oCoaching_Response.getbody().contains('Fault'))
                            {
                                string sErrorMsg = 'Status Code: ' + oCoaching_Response.getStatus() + '; Body: ' + oCoaching_Response.getbody();
                                throw new PushServiceException(sErrorMsg);
                            }
                        }					
                    }
                }	
            }	
        }
        catch(Exception ex)
        {
        	Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex, 'PushNotification_S_HUM','callPushNotificationService');
            
            if(error!=null) 
            { 
                HUMExceptionHelper.saveLog(new List<Error_Log__c>{error}, false);
            }
        }
	}
	
	/**
	* Method to create the Request DTO for PushNotification Service 
	*
	* @param	sPersonId - PersonId for whom notification of new message to be sent to Go365. 
	* @param	sFeedItemId - FeedItem or FeedComment that is created. 
    * @param	sFeedElementType - What kind of Feed created for that member
	* @return   PushNotification_DTO_HUM - request DTO for Push Notification service callout.
	*/
	private static PushNotification_DTO_HUM createPushNotiServiceRequest(String sPersonId, String sFeedItemId, String sFeedElementType)
	{
		PushNotification_DTO_HUM oRequest = new PushNotification_DTO_HUM();		
        PushNotification_DTO_HUM.FeedDataDTO oFeedDataRequest = new PushNotification_DTO_HUM.FeedDataDTO();
        oFeedDataRequest.FeedCommentId = (string.isNotBlank(sFeedElementType) && sFeedElementType.equalsIgnoreCase('FeedComment')) ? sFeedItemId : '';			
        oFeedDataRequest.FeedItemId = (string.isNotBlank(sFeedElementType) && sFeedElementType.equalsIgnoreCase('FeedItem')) ? sFeedItemId : '';		
        oRequest.FeedData = oFeedDataRequest;
        oRequest.Person_Id = sPersonId;
		return oRequest;
    }
    public class PushServiceException extends Exception {}
}