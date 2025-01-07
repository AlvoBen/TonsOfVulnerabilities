/***************************************************************************************************************************************************************************
Apex Class Name  : CarePlanService
Version          : 1.0
Created Date     : May 28 2021
Function         : This Rest Webservice is used to for getting care plans for coaching.
Test Class       : CarePlanService_T_HUM
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name                    Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Moshitha Gunasekaran              05/28/2021                  Original Version
 ****************************************************************************************************************************************************************************/
@RestResource(urlMapping='/CarePlanService/*')
global with sharing class CarePlanService {
	
	/*
     * Method name : getCarePlans
     * Description : This method is a restservice method and is exposed to external system to get care plans.
     * Return Type : RestContext.response
     * Parameter   : RestContext.request
     */	
    @HttpPost
    global static void getCarePlans()
    {
        RestRequest request = RestContext.request;
        GetCarePlanRequestDTO getCarePlanRequest = new GetCarePlanRequestDTO();
        getCarePlanRequest = (GetCarePlanRequestDTO)System.JSON.deserialize(request.requestBody.toString(), GetCarePlanRequestDTO.class);
        GetCarePlanResponseDTO carePlans = CarePlan_D_HUM.getCarePlans(getCarePlanRequest);
        String carePlanResponse = System.JSON.serialize(carePlans);
        RestResponse res = RestContext.response;
       	res.statusCode = 200;
      	res.addHeader('Content-Type', 'application/json'); 
       	res.responseBody = Blob.valueOf(carePlanResponse);
    }
    
}