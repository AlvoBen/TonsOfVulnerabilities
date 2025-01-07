/***************************************************************************************************************************************************************************
Apex Class Name  : CallInteractionService
Version          : 1.0
Created Date     : June 24th 2021
Function         : This Rest Webservice is used to for all the Interactions service.
Test Class       : CallInteractionService_T_HUM
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name              Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Moshitha Gunasekaran                                   6/24/2021                  Original version
 ****************************************************************************************************************************************************************************/
@RestResource(urlMapping='/CallInteractionInfo/*') 
global with sharing class CallInteractionService {
    
    /*
    * Method name : CallCreateInteractionInfo
    * Description : This method is a restservice method and is exposed to Calls to create Interaction
    * Return Type : RestContext.response
    * Parameter   : RestContext.request
    */
    @HttpPut
    global static void CallCreateInteractionInfo()
    {
        RestRequest request = RestContext.request;
        CallInteractionRequest_DTO_HUM intRequest = new CallInteractionRequest_DTO_HUM();
        intRequest = (CallInteractionRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CallInteractionRequest_DTO_HUM.class);
        if(null != request && null != request.requestBody && null != intRequest) intRequest.CallData = request.requestBody.toString();
        CallInteractionResponse_DTO_HUM interactionResponse = createInteractionRequest(intRequest);  
        String feedResponse = System.JSON.serialize(interactionResponse);
        RestResponse res = RestContext.response;
       	res.statusCode = 200;
      	res.addHeader('Content-Type', 'application/json'); 
       	res.responseBody = Blob.valueOf(feedResponse);     
    }

    /*
     * Method name : createInteractionRequest
     * Description : This method is used to expose the interaction object to create interaction records for call.
     * Return Type : RestContext.response
     * Parameter   : CreateInteractionRequest_DTO_HUM
     */
    private static CallInteractionResponse_DTO_HUM createInteractionRequest(CallInteractionRequest_DTO_HUM request)
    {
        CallInteractionResponse_DTO_HUM response = new CallInteractionResponse_DTO_HUM();
        RestResponse res = RestContext.response;
        response = CreateInteractionWithCaseRest_D_HUM.createCallInteraction(request);
        
        return response;
    }
}