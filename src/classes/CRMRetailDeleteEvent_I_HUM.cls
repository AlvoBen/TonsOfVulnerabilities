/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailDeleteEvent_I_HUM 
* Version          : 1.0
* Created Date     : 24th June, 2021
* Function         : Service class for Delete Event web services.
* Test Class       : CRMRetailDeleteEvent_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                                  		           06/24/2021              Original Version  2361242
**********************************************************************************************************************************/

@RestResource(urlMapping='/CRMRetailServices_V1.0/DeleteEvent')
global with sharing class CRMRetailDeleteEvent_I_HUM {
	/*
	 * Method Name   :   deleteEvent
	 * Description   :   This method is deserialize the request and calls the handler method for further processing of the request   
	 * Return Type   :   N/A
	 * Parameters    :   N/A
	*/
    
	@HttpPost
    global static void deleteEvent()
    {
		CRMRetailVNCRequest_DTO_HUM.CRMRetailDeleteEventRequest_DTO_HUM oRequest = new CRMRetailVNCRequest_DTO_HUM.CRMRetailDeleteEventRequest_DTO_HUM();        
		CRMRetailDeleteEvent_H_HUM objDeleteEvent;
        try
        {
        	RestRequest request = RestContext.request; 
            oRequest = (CRMRetailVNCRequest_DTO_HUM.CRMRetailDeleteEventRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CRMRetailVNCRequest_DTO_HUM.CRMRetailDeleteEventRequest_DTO_HUM.class);
            objDeleteEvent = new CRMRetailDeleteEvent_H_HUM(oRequest);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CRMRetailDeleteEvent_I_HUM','deleteEvent');
           	objDeleteEvent = new CRMRetailDeleteEvent_H_HUM(null);
        }
	}
}