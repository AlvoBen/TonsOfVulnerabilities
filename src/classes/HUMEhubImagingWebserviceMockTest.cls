/***************************************************************************************************************************************************************************
Apex Class Name  : HUMEhubImagingWebserviceMockTest
Version          : 1.0
Created Date     : March 19 2015
Function         : Mock Response for HUMEhubImagingWebservice
 Modification Log: 
 * Developer Name              Code Review                    Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma                21654                        03/19/2015                Original Version    

*****************************************************************************************************************************************************************************/
@isTest
public with sharing class HUMEhubImagingWebserviceMockTest implements WebServiceMock
{
	 public void doInvoke(
           Object stub,
           Object request,
           Map<String, Object> response,
           String endpoint,
           String soapAction,
           String requestName,
           String responseNS,
           String responseName,
           String responseType)
		{
			HUMEhubImagingWebservice.AddDocumentWithUserIdResponse_element responseElement = new HUMEhubImagingWebservice.AddDocumentWithUserIdResponse_element();
			HUMEhubImagingWebservice.DssResultDTO attachmentResponse = new HUMEhubImagingWebservice.DssResultDTO();
        	attachmentResponse.SalesforceRecordID = '1234';
	        attachmentResponse.DocID = '1234';
	        attachmentResponse.Version = '1234';
	        attachmentResponse.ViewUrl = '1234';
	        attachmentResponse.FileData = '1234';
	        attachmentResponse.Status = '1234';
	        attachmentResponse.Status_description = '1234';
			responseElement.AddDocumentWithUserIdResult = attachmentResponse;
			response.put('response_x', responseElement);
		}
}