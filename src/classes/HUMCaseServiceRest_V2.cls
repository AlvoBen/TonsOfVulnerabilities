/***************************************************************************************************************************************************************************
Apex Class Name  : HUMCaseServiceRest_V2
Version          : 1.0
Created Date     : January 17 2019
Function         : This Rest Webservice is used to for all the case service.
Test Class       : HUMCaseServiceRestTest_V2
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name              Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Divya Proddaturi             340085                   2/27/2019                  Original version
 ****************************************************************************************************************************************************************************/
@RestResource(urlMapping='/CaseServiceRestV2/*')
global with sharing class HUMCaseServiceRest_V2 {
    
    
      /*
     * Method name : createCaseInfo
     * Description : This method is a restservice method and is exposed to external system to create cases
     * Return Type : RestContext.response
     * Parameter   : RestContext.request
     */
    @HttpPut
    global static void createCaseInfo()
    {
        RestRequest request = RestContext.request;
        CaseServiceRestCreateRequest_DTO_HUM caseRequest = new CaseServiceRestCreateRequest_DTO_HUM();
        caseRequest = (CaseServiceRestCreateRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CaseServiceRestCreateRequest_DTO_HUM.class);
        createCaseRequest(caseRequest);
        
    }
    /*
     * Method name : updateCaseInfo
     * Description : This method is a restservice method and is exposed to external system to update case and attachments for cases
     * Return Type : RestContext.response
     * Parameter   : RestContext.request
     */
    @HttpPatch
    global static void updateCaseInfo()
    {
        RestRequest request = RestContext.request;
        CaseServiceRestUpdateRequest_DTO_HUM updateCaseRequest = new CaseServiceRestUpdateRequest_DTO_HUM();
        updateCaseRequest = (CaseServiceRestUpdateRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CaseServiceRestUpdateRequest_DTO_HUM.class);
        updateCaseInfoRequest(updateCaseRequest);
        
    }
    
    /*
     * Method name : getCaseInfo
     * Description : This method is a restservice method and is exposed to external system to get case related information
     * Return Type : RestContext.response
     * Parameter   : RestContext.request
     */
    @HttpPost
    global static void getCaseInfo()
    {
        RestRequest request = RestContext.request;
        CaseServiceRestGetRequest_DTO_HUM_V2 getCaseRequest = new CaseServiceRestGetRequest_DTO_HUM_V2();
        getCaseRequest = (CaseServiceRestGetRequest_DTO_HUM_V2)System.JSON.deserialize(request.requestBody.toString(), CaseServiceRestGetRequest_DTO_HUM_V2.class);              
        getCaseInfoRequest(getCaseRequest);
       
    }
    /*
     * Method name : getCaseInfoRequest
     * Description : This method is used to expose the case object to get case records.
     * Return Type : RestContext.response
     * Parameter   : CaseServiceRestGetRequest_DTO_HUM 
     */
   private static void getCaseInfoRequest(CaseServiceRestGetRequest_DTO_HUM_V2 request)
   {
       CaseServiceRestGetResponse_DTO_HUM_V2 response = new CaseServiceRestGetResponse_DTO_HUM_V2();
       response.GetCaseInfoResponse = new CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO();
       RestResponse res = RestContext.response;
       response = GetCaseInfoRest_D_HUM_V2.getCaseRelatedInfo(request);
       String getCaseInfoResponse = System.JSON.serialize(response);
       res.statusCode = 200;
       res.addHeader('Content-Type', 'application/json'); 
       res.responseBody = Blob.valueOf(getCaseInfoResponse);
   }
     /*
     * Method name : createCaseRequest
     * Description : This method is used to expose the case object to create case records.
     * Return Type : RestContext.response
     * Parameter   : CaseServiceRestCreateRequest_DTO_HUM 
     */
   private static void createCaseRequest(CaseServiceRestCreateRequest_DTO_HUM request)
   {
       CaseServiceRestCreateResponse_DTO_HUM response = new CaseServiceRestCreateResponse_DTO_HUM();
       RestResponse res = RestContext.response;
       response = CreateCaseRest_D_HUM.createCase(request);
       String createResponse = System.JSON.serialize(response);
       res.statusCode = 200;
       res.addHeader('Content-Type', 'application/json');
       res.responseBody = Blob.valueOf(createResponse);
   }
    
    /*
     * Method name : updateCaseInfoRequest
     * Description : This method is used to expose the case object to update case records.
     * Return Type : RestContext.response
     * Parameter   : CaseServiceRestGetRequest_DTO_HUM 
     */
   private static void updateCaseInfoRequest(CaseServiceRestUpdateRequest_DTO_HUM request)
   {
    
       RestResponse res = RestContext.response;
       CaseServiceRestUpdateResponse_DTO_HUM response = UpdateCaseInfoRest_D_HUM.updateCaseAndAttachment(request);
       String updateCaseInfoResponse = System.JSON.serialize(response);
       res.statusCode = 200;
       res.addHeader('Content-Type', 'application/json');
       res.responseBody = Blob.valueOf(updateCaseInfoResponse);
    }   
    
}