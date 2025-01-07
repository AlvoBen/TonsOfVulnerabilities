/***************************************************************************************************************************************************************************
Apex Class Name  : HUMInteractionWithCaseServiceRest
Version          : 1.0
Created Date     : March 7th 2019
Function         : This Rest Webservice is used to for all the Interactions service.
Test Class       : HUMInteractionWithCaseServiceRestTest
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name              Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Pallavi Ravishankar                                   3/7/2019                  Original version
 * Pallavi Ravishankar            354971                 5/7/2019                  REQ - 377486 - Watson - UpdateInteraction
 * Mohammed Noor                                         07/01/2019                Changes done for REQ - 403693.
 ****************************************************************************************************************************************************************************/
@RestResource(urlMapping='/CreateInteractionInfo/*') 
global with sharing class HUMInteractionWithCaseServiceRest 
{

   /*
    * Method name : createInteractionInfo
    * Description : This method is a restservice method and is exposed to external system to create Interact
    * Return Type : RestContext.response
    * Parameter   : RestContext.request
    */
    @HttpPut
    global static void createInteractionInfo()
    {
        RestRequest request = RestContext.request;
        CreateInteractionRequest_DTO_HUM intRequest = new CreateInteractionRequest_DTO_HUM();
        intRequest = (CreateInteractionRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CreateInteractionRequest_DTO_HUM.class);
        createInteractionRequest(intRequest);       
    }
    
    /*
     * Method name : updateInteractionInfo
     * Description : This method is a restservice method & is exposed to external system to update Interaction with new int members & cases
     * Return Type : RestContext.response
     * Parameter   : RestContext.request
     */
    @HttpPatch
    global static void updateInteractionInfo()
    {
        RestRequest request = RestContext.request;
        UpdateInteractionRequest_DTO_HUM intRequest = new UpdateInteractionRequest_DTO_HUM();
        intRequest = (UpdateInteractionRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), UpdateInteractionRequest_DTO_HUM.class);
        updateInteractionInfoRequest(intRequest);
        
    }
    
    /*
    * Method name : getInteractionInfo
    * Description : This method is a restservice method and is exposed to external system to get Interaction details
    * Return Type : RestContext.response
    * Parameter   : RestContext.request
    */
    @HttpPost
	global static void getInteractionInfo()
	{
		RestRequest request = RestContext.request;
		GetInteractionInfoRequest_DTO_HUM intRequest = new GetInteractionInfoRequest_DTO_HUM();		
		intRequest = (GetInteractionInfoRequest_DTO_HUM)system.JSON.deserialize(request.requestBody.toString(), GetInteractionInfoRequest_DTO_HUM.class);
		getInteractionInfoRequest(intRequest);
	}
	
    /*
     * Method name : createInteractionRequest
     * Description : This method is used to expose the case object to create case records.
     * Return Type : RestContext.response
     * Parameter   : CreateInteractionRequest_DTO_HUM
     */
    private static void createInteractionRequest(CreateInteractionRequest_DTO_HUM request)
    {
        CreateInteractionResponse_DTO_HUM response = new CreateInteractionResponse_DTO_HUM();
        RestResponse res = RestContext.response;
        response = CreateInteractionWithCaseRest_D_HUM.createInteraction(request);
        String createResponse = System.JSON.serialize(response);
        res.statusCode = 200;
        res.addHeader('Content-Type', 'application/json');
        res.responseBody = Blob.valueOf(createResponse);
    }
    
    /*
     * Method name : updateInteractionInfoRequest
     * Description : This method is used to expose the interaction object to update related Interacting members and case details
     * Return Type : RestContext.response
     * Parameter   : UpdateInteractionRequest_DTO_HUM 
     */
   private static void updateInteractionInfoRequest(UpdateInteractionRequest_DTO_HUM request)
   {
    
       RestResponse res = RestContext.response;
       UpdateInteractionResponse_DTO_HUM response = UpdateInteractionWithCaseRest_D_HUM.updateInteraction(request);
       String updateInteractionResponse = System.JSON.serialize(response);
       res.statusCode = 200;
       res.addHeader('Content-Type', 'application/json');
       res.responseBody = Blob.valueOf(updateInteractionResponse);
    }
    
    /*
     * Method name : getInteractionInfoRequest
     * Description : This method is used to retrieve the Interaction details for GetInteractionInfo method
     * Return Type : RestContext.response
     * Parameter   : GetInteractionInfoRequest_DTO_HUM 
     */
    private static void getInteractionInfoRequest(GetInteractionInfoRequest_DTO_HUM request)
    {
    	RestResponse res = RestContext.response;
    	GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(request);
    	string getInteractionInfoResponse = system.JSON.serialize(response);
    	res.statusCode = 200;
    	res.addHeader('Content-Type', 'application/json');
    	res.responseBody = Blob.valueOf(getInteractionInfoResponse);
    }   
}