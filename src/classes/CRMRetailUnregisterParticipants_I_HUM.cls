/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailUnregisterParticipants_I_HUM 
* Version          : 1.0
* Created Date     : 24th June, 2021
* Function         : Service class for Unregister Participants web services.
* Test Class       : CRMRetailUnregisterParticipants_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                                  					       06/24/2021              Original Version  2389080
**********************************************************************************************************************************/

@RestResource(urlMapping='/CRMRetailServices_V1.0/UnregisterParticipant')
global with sharing class CRMRetailUnregisterParticipants_I_HUM {
	/*
	 * Method Name   :   deleteEvent
	 * Description   :   This method is deserialize the request and calls the handler method for further processing of the request   
	 * Return Type   :   N/A
	 * Parameters    :   N/A
	*/
    
	@HttpPost
    global static void unregisterParticpants()
    {
		CRMRetailVNCRequest_DTO_HUM.CRMRetailUnregisterParticipantsReq_DTO_HUM oRequest = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUnregisterParticipantsReq_DTO_HUM();        
		CRMRetailUnregisterParticipants_H_HUM objUnregisterParticipants;
        try
        {
        	RestRequest request = RestContext.request; 
            oRequest = (CRMRetailVNCRequest_DTO_HUM.CRMRetailUnregisterParticipantsReq_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CRMRetailVNCRequest_DTO_HUM.CRMRetailUnregisterParticipantsReq_DTO_HUM.class);
            objUnregisterParticipants = new CRMRetailUnregisterParticipants_H_HUM(oRequest);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CRMRetailUnregisterParticipants_I_HUM','unregisterParticpants');
           	objUnregisterParticipants = new CRMRetailUnregisterParticipants_H_HUM(null);
        }
	}
}