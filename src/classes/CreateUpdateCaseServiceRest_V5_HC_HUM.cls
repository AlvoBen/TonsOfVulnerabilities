/***************************************************************************************************************************************************************************
Apex Class Name  : CreateUpdateCaseServiceRest_V5_HC_HUM
Version          : 1.0
Created Date     : Feb 03 2022
Function         : This Rest Webservice is used to for all the Interactions service with respect to Health cloud data model..
Test Class       : 
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name              Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Manoj MK                                          02/03/2022                  User Story 2888387: T1PRJ0170850 - Lighting- VOC Implementation

****************************************************************************************************************************************************************************/
@RestResource(urlMapping='/CaseService_V5/*') 
global with sharing class CreateUpdateCaseServiceRest_V5_HC_HUM {
	
   
   
     /*
	* Method name : getInteractionDetails
	* Description : REST API method exposed to fetch Interaction details
	* Parameter   : void.
	* Return Type : void.    
	*/
    @HttpPost
    global static void getInteractionDetails()
    {
        RestRequest request = RestContext.request;                        
        LastInteractionRequest_DTO_HUM lastInteractionRequest = new LastInteractionRequest_DTO_HUM();
        lastInteractionRequest = (LastInteractionRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), LastInteractionRequest_DTO_HUM.class);                
        getInteractionDetailsResponse(lastInteractionRequest);        
    }
   
    
     /*
	* Method name : getInteractionDetailsResponse
	* Description : This method is used to fetch the response of latest interaction details
	* Parameter   : LastInteractionRequest_DTO_HUM (input request from service call)	
	* Return Type : void.    
	*/ 
    public static void getInteractionDetailsResponse(LastInteractionRequest_DTO_HUM lastInteractionRequest){
        LastInteractionResponse_DTO_HUM lastInteractionResponse = new LastInteractionResponse_DTO_HUM();       
        RESTResponse res = RESTContext.response;        
        lastInteractionResponse = LastInteractionServiceRest_V5_HC_HUM.fetchInteractionDetails(lastInteractionRequest);
        string slastInteractionResponse = System.JSON.serialize(lastInteractionResponse);		   
        res.statusCode = 200;
        res.addHeader('Content-Type', 'application/json');
        res.responseBody = Blob.valueOf(slastInteractionResponse);               
    }
   
}