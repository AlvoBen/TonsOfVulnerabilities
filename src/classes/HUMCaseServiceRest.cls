/***************************************************************************************************************************************************************************
Apex Class Name  : HUMCaseServiceRest
Version          : 1.0
Created Date     : March 14 2018
Function         : This Rest Webservice is used to for all the case related services.
Test Class       : HUMCaseServiceRestTest
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name              Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Moshitha Gunasekaran         215366                    03/15/2018                  Original Version
 ****************************************************************************************************************************************************************************/

@RestResource(urlMapping='/CaseServiceRest/*')
global with sharing class HUMCaseServiceRest
{
    
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
     * Method name : getCaseInfo
     * Description : This method is a restservice method and is exposed to external system to get case related information
     * Return Type : RestContext.response
     * Parameter   : RestContext.request
     */
    @HttpPost
    global static void getCaseInfo()
    {
        RestRequest request = RestContext.request;
        CaseServiceRestGetRequest_DTO_HUM getCaseRequest = new CaseServiceRestGetRequest_DTO_HUM();
        getCaseRequest = (CaseServiceRestGetRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CaseServiceRestGetRequest_DTO_HUM.class);
        getCaseInfoRequest(getCaseRequest);
        
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
     * Method name : getCaseInfoRequest
     * Description : This method is used to expose the case object to get case records.
     * Return Type : RestContext.response
     * Parameter   : CaseServiceRestGetRequest_DTO_HUM 
     */
   private static void getCaseInfoRequest(CaseServiceRestGetRequest_DTO_HUM request)
   {
       CaseServiceRestGetResponse_DTO_HUM response = new CaseServiceRestGetResponse_DTO_HUM();
       response.GetCaseInfoResponse = new CaseServiceRestGetResponse_DTO_HUM.GetCaseInfoResponseDTO();
       RestResponse res = RestContext.response;
       Integer iCaseCount = 0;
       if(request.GetCaseInfo.getCaseInfo == true && request.GetCaseInfo.CaseInfoRequest != null )
       {
            response = GetCaseInfoRest_D_HUM.getCaseRelatedInfo(request);
        }
       else if (request.GetCaseInfo.getCaseInfo == false && request.GetCaseInfo.IntAboutCaseAvailabilityCheck != null)
       {
            iCaseCount = GetCaseInfoRest_D_HUM.getCases(request).size();
            response.GetCaseInfoResponse.CaseCount = String.ValueOf(iCaseCount);
       }
              
       String getCaseInfoResponse = System.JSON.serialize(response);
       res.statusCode = 200;
       res.addHeader('Content-Type', 'application/json'); 
       res.responseBody = Blob.valueOf(getCaseInfoResponse);
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