@RestResource(urlMapping='/LiveChatTranscriptService_V1.0/UpdateChatTranscript')
global with sharing class UpdateChatTranscriptService_I_HUM {

    private static UpdateChatTranscriptRequest_DTO_HUM oRequest;
    private static UpdateChatTranscriptResponse_DTO_HUM oResponse;

    @HttpPost
    Global Static void updateChatTranscriptInfo(){
        try
        {
			RestRequest request = RestContext.request;     
            oRequest = (UpdateChatTranscriptRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), UpdateChatTranscriptRequest_DTO_HUM.class);
            processChatTranscriptRequest();
        }
        catch(Exception e)
        {
            //prepare for error response
            prepareResponse(e.getMessage() + e.getStackTraceString() + e.getLineNumber());
            HUMExceptionHelper.logErrors(e,'UpdateChatTranscriptService_I_HUM','updateChatTranscriptInfo');
        }
    }   //  end of updateChatTranscriptInfo

    /*
    * Method name : processChatTranscriptRequest
    * Description : This method is used to expose the case object to create case records.
    * Return Type : RestContext.response
    * Parameter   : ChatTranscriptRestCreateRequest_DTO_HUM 
    */
    private static void processChatTranscriptRequest()
    {
        UpdateChatTranscriptResponse_DTO_HUM response = new UpdateChatTranscriptResponse_DTO_HUM();
        RestResponse res = RestContext.response;
        response = UpdateChatTranscriptService_D_HUM.updateChatTranscript(oRequest);
        String createResponse = System.JSON.serialize(response);
        res.statusCode = 200;
        res.addHeader('Content-Type', 'application/json');
        res.responseBody = Blob.valueOf(createResponse);
    }

    private static void prepareResponse(string message)
    {
        RestResponse res = RestContext.response;
        oResponse = new UpdateChatTranscriptResponse_DTO_HUM();       
		UpdateChatTranscriptResponse_DTO_HUM.UpdateChatTranscriptResponse  chatTranscriptResponse = new UpdateChatTranscriptResponse_DTO_HUM.UpdateChatTranscriptResponse();        
        chatTranscriptResponse.Success = 'false'; 
        chatTranscriptResponse.ErrorMessage = message;
        oResponse.UpdateChatTranscriptResponse = chatTranscriptResponse;
        
        String createResponse = System.JSON.serialize(oResponse);
        res.statusCode = 200;
        res.addHeader('Content-Type', 'application/json');
        res.responseBody = Blob.valueOf(createResponse);
       
    }
    
}   //  end of UpdateChatTranscriptService_I_HUM