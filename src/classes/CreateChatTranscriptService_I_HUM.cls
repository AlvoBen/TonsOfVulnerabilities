@RestResource(urlMapping='/LiveChatTranscriptService_V1.0/CreateChatTranscript')
global with sharing class CreateChatTranscriptService_I_HUM 
{
    public static CreateChatTranscriptRequest_DTO_HUM oRequest;
    public static CreateChatTranscriptResponse_DTO_HUM oResponse;
    
    @HttpPost
    global static void createChatTranscriptInfo()
    {
        try
        {
			RestRequest request = RestContext.request;     
            oRequest = (CreateChatTranscriptRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CreateChatTranscriptRequest_DTO_HUM.class);
            processChatTranscriptRequest();
        }
        catch(Exception e)
        {
            //prepare for error response
            prepareResponse(e.getMessage() + e.getStackTraceString() + e.getLineNumber());
            HUMExceptionHelper.logErrors(e,'CreateChatTranscriptService_I_HUM','createChatTranscriptInfo');
        }
    }
    
    /*
    * Method name : processChatTranscriptRequest
    * Description : This method is used to expose the case object to create case records.
    * Return Type : RestContext.response
    * Parameter   : ChatTranscriptRestCreateRequest_DTO_HUM 
    */
    private static void processChatTranscriptRequest()
    {
        CreateChatTranscriptResponse_DTO_HUM response = new CreateChatTranscriptResponse_DTO_HUM();
        RestResponse res = RestContext.response;
        response = CreateChatTranscriptService_D_HUM.createChatTranscript(oRequest);
        String createResponse = System.JSON.serialize(response);
        res.statusCode = 200;
        res.addHeader('Content-Type', 'application/json');
        res.responseBody = Blob.valueOf(createResponse);
    }   
    
    
    private static void prepareResponse(string message)
    {
        RestResponse res = RestContext.response;
        oResponse = new CreateChatTranscriptResponse_DTO_HUM();       
		CreateChatTranscriptResponse_DTO_HUM.CreateChatTranscriptResponse  chatTranscriptResponse = new CreateChatTranscriptResponse_DTO_HUM.CreateChatTranscriptResponse();        
        chatTranscriptResponse.Success = 'false'; 
        chatTranscriptResponse.ErrorMessage = message;
        oResponse.CreateChatTranscriptResponse = chatTranscriptResponse;
        
        String createResponse = System.JSON.serialize(oResponse);
        res.statusCode = 200;
        res.addHeader('Content-Type', 'application/json');
        res.responseBody = Blob.valueOf(createResponse);
       
    }
    
}