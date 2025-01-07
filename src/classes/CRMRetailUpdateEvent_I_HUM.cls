/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailUpdateEvent_I_HUM 
* Version          : 1.0
* Created Date     : 8th June, 2021
* Function         : Service Class for Updation of Event services
* Test Class       : CRMRetailUpdateEvent_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Sahil Verma                                  					   06/08/2021              Original Version 2361199
**********************************************************************************************************************************/
@RestResource(urlMapping='/CRMRetailServices_V1.0/UpdateEvent')
global with sharing class CRMRetailUpdateEvent_I_HUM 
{
	 /*
	 * Method Name   :   updateEvent
	 * Description   :   This method is deserialize the request and calls the handler method for further processing of the request   
 	 * Return Type   :   N/A
 	 * Parameters    :   N/A
 	 */
    @HttpPost
    global static void updateEvent()
    {  
        CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM oRequest = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM();        
        CRMRetailUpdateEvent_H_HUM objUpdateEvent;
        try
        {
            RestRequest request = RestContext.request; 
            oRequest = (CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM.class);
            objUpdateEvent = new CRMRetailUpdateEvent_H_HUM(oRequest);
        }
        catch(Exception e)
        {  
            HUMExceptionHelper.logErrors(e,'CRMRetailUpdateEvent_I_HUM','UpdateEvent');
            objUpdateEvent = new CRMRetailUpdateEvent_H_HUM(null);
        }
    }    
}