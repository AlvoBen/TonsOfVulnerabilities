/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailMarkAttandance_I_HUM 
* Version          : 1.0
* Created Date     : 25th June, 2021
* Function         : Service Class for marking the attandance
* Test Class       : CRMRetailMarkAttandance_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ekta Bist                                  					   06/25/2021              Original Version 2389191
**********************************************************************************************************************************/
@RestResource(urlMapping='/CRMRetailServices_V1.0/MarkAttendance')
global with sharing class CRMRetailMarkAttandance_I_HUM 
{
	/*
    * Method Name   :   markAttandance
    * Description   :   This method is deserialize the request and calls the handler method for further processing of the request   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */ 
    @HttpPost
    global static void markAttandance()
    {  
        CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM oRequest = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM();        
        CRMRetailMarkAttandance_H_HUM objMarkAttandance;
        try
        {
            RestRequest request = RestContext.request; 
            oRequest = (CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM.class);
            objMarkAttandance = new CRMRetailMarkAttandance_H_HUM(oRequest);
        }
        catch(Exception e)
        {  
            HUMExceptionHelper.logErrors(e,'CRMRetailMarkAttandance_I_HUM','markAttandance');
            objMarkAttandance = new CRMRetailMarkAttandance_H_HUM(null);
        }
    }
}