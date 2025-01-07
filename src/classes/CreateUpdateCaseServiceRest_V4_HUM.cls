/***************************************************************************************************************************************************************************
Apex Class Name  : CreateUpdateCaseServiceRest_V4_HUM
Version          : 1.0
Created Date     : March 7th 2019
Function         : This Rest Webservice is used to for all the Interactions service.
Test Class       : 
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name              Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Asish Kumar Behera                                     07/17/2019                  Original version
 * Sagar MN												  07/30/2019				 Req - 405925 - update case service V4
 * Sagar MN	 											  08/27/2019				 Req - 405927 - Last Interaction Info Service V4
****************************************************************************************************************************************************************************/
@RestResource(urlMapping='/CaseService_V4/*') 
global with sharing class CreateUpdateCaseServiceRest_V4_HUM {
	
    /*
    * Method name : createCase
    * Description : This method is a restservice method and is exposed to external system to create Case
    * Return Type : RestContext.response
    * Parameter   : RestContext.request
    */
    @HttpPut
    global static void createCase()
    {
        RestRequest request = RestContext.request;       
        CreateCaseRequest_V4_HUM intRequest = new CreateCaseRequest_V4_HUM();
        intRequest = (CreateCaseRequest_V4_HUM)System.JSON.deserialize(request.requestBody.toString(), CreateCaseRequest_V4_HUM.class);
         createCase(intRequest);
        //refer this class for test data -- test_CaseCreationV4       
    }
    
    /*
     * Method name : updateCase
     * Description : This method is a restservice method & is exposed to external system to update cases, create comments,attachment and attachment log
     * Return Type : RestContext.response
     * Parameter   : RestContext.request
     */
    @HttpPatch
    global static void updateCase()
    {
        RestRequest request = RestContext.request;        
        UpdateCaseRequest_V4_HUM intRequest = new UpdateCaseRequest_V4_HUM();
        intRequest = (UpdateCaseRequest_V4_HUM)System.JSON.deserialize(request.requestBody.toString(), UpdateCaseRequest_V4_HUM.class);
        updateCase(intRequest);
        
    }
    
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
     * Method name : createInteractionRequest
     * Description : This method is used to expose the case object to create case records.
     * Return Type : RestContext.response
     * Parameter   : CreateCaseRequest_V4_HUM
     */
    private static void createCase(CreateCaseRequest_V4_HUM request)
    {
        RestResponse res = RestContext.response;        
        CaseDetailResponse_DTO_V4_HUM response = CreateUpdateCaseServiceRest_D_HUM.createCase(request);        
        String createResponse = System.JSON.serialize(response);                
        res.statusCode = 200;
        res.addHeader('Content-Type', 'application/json');
        res.responseBody = Blob.valueOf(createResponse);
    }
    
    /*
     * Method name : createInteractionRequest
     * Description : This method is used to expose the case object to create case records.
     * Return Type : RestContext.response
     * Parameter   : UpdateCaseRequest_V4_HUM
    */ 
    private static void updateCase(UpdateCaseRequest_V4_HUM request)
    {      
       RestResponse res = RestContext.response;      
       CaseDetailResponse_DTO_V4_HUM response = UpdateCase_D_V4_HUM.updateCaseDetails(request);
        String updateCaseInfoResponse = System.JSON.serialize(response);       
       	res.statusCode = 200;
       	res.addHeader('Content-Type', 'application/json');
       	res.responseBody = Blob.valueOf(updateCaseInfoResponse);
       
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
        lastInteractionResponse = LastInteractionServiceRest_V4_HUM.fetchInteractionDetails(lastInteractionRequest);
        string slastInteractionResponse = System.JSON.serialize(lastInteractionResponse);		   
        res.statusCode = 200;
        res.addHeader('Content-Type', 'application/json');
        res.responseBody = Blob.valueOf(slastInteractionResponse);               
    }
   
}