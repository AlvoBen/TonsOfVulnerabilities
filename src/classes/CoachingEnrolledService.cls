/***************************************************************************************************************************************************************************
Apex Class Name  : CoachingEnrolledService
Version          : 1.0
Created Date     : June 03 2021
Function         : This Rest Webservice is used to check  if member is enrolled for coaching.
Test Class       : CarePlanService_T_HUM
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name                    Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Moshitha Gunasekaran              06/03/2021                  Original Version
 ****************************************************************************************************************************************************************************/
@RestResource(urlMapping='/CoachingEnrolledService/*')
global with sharing class CoachingEnrolledService {
    
    /*
     * Method name : getCarePlans
     * Description : This method is a restservice method and is exposed to external system to check for coaching enrolled.
     * Return Type : RestContext.response
     * Parameter   : RestContext.request
     */	
    @HttpPost
    global static void IsCoachingEnrolled()
    {
        RestRequest request = RestContext.request;
        GetCarePlanRequestDTO coachEnrollRequest = new GetCarePlanRequestDTO();
        coachEnrollRequest = (GetCarePlanRequestDTO)System.JSON.deserialize(request.requestBody.toString(), GetCarePlanRequestDTO.class);
        GetCarePlanResponseDTO oCoachingEnrolled = CarePlan_D_HUM.IsCoachingEnrolled(coachEnrollRequest);
        String coachingEnrolledResponse = System.JSON.serialize(oCoachingEnrolled);
        RestResponse res = RestContext.response;
       	res.statusCode = 200;
      	res.addHeader('Content-Type', 'application/json'); 
       	res.responseBody = Blob.valueOf(coachingEnrolledResponse);
    }
}