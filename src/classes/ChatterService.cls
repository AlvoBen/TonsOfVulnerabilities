/***************************************************************************************************************************************************************************
Apex Class Name  : ChatterService
Version          : 1.0
Created Date     : May 13 2021
Function         : This Rest Webservice is used to for all the chatter related services.
Test Class       : ChatterService_T_HUM
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name                    Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Moshitha Gunasekaran              05/13/2021                  Original Version
 ****************************************************************************************************************************************************************************/
@RestResource(urlMapping='/ChatterService/*')
global with sharing class ChatterService {
        
    /*
     * Method name : postFeedGroup
     * Description : This method is a restservice method and is exposed to external system to post feed
     * Return Type : RestContext.response
     * Parameter   : RestContext.request
     */
    @HttpPut
    global static void postFeedGroup()
    {
        RestRequest request = RestContext.request;
        ChatterRequestDTO chatterRequest = new ChatterRequestDTO();
        chatterRequest = (ChatterRequestDTO)System.JSON.deserialize(request.requestBody.toString(), ChatterRequestDTO.class);
        ChatterResponseDTO chatterResponse = ChatterFeed_D_HUM.createChatterRequest(chatterRequest);
        String feedResponse = System.JSON.serialize(chatterResponse);
        RestResponse res = RestContext.response;
       	res.statusCode = 200;
      	res.addHeader('Content-Type', 'application/json'); 
       	res.responseBody = Blob.valueOf(feedResponse);
    }
    
     /*
     * Method name : getFeedItem
     * Description : This method is a restservice method and is exposed to external system to get feed
     * Return Type : RestContext.response
     * Parameter   : RestContext.request
     */
    @HttpPost
    global static void getFeedItem()
    {
       RestRequest request = RestContext.request;
        ChatterGetFeedRequestDTO chatterGetFeedRequest = new ChatterGetFeedRequestDTO();
        chatterGetFeedRequest = (ChatterGetFeedRequestDTO)System.JSON.deserialize(request.requestBody.toString(), ChatterGetFeedRequestDTO.class);
        ChatterGetFeedResponseDTO feedvalue = ChatterGetFeed_D_HUM.getFeeds(chatterGetFeedRequest);
        String feedResponse = System.JSON.serialize(feedvalue);
        RestResponse res = RestContext.response;
       	res.statusCode = 200;
      	res.addHeader('Content-Type', 'application/json'); 
       	res.responseBody = Blob.valueOf(feedResponse);
    }
}