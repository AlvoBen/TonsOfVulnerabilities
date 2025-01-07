/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailCreateEvent_I_HUM 
* Version          : 1.0
* Created Date     : 7th June, 2021
* Function         : Service class for Create Event web services.
* Test Class       : CRMRetailCreateEvent_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Isha Gupta                                  					       06/07/2021              Original Version  2361161
**********************************************************************************************************************************/

@RestResource(urlMapping='/CRMRetailServices_V1.0/CreateEvent')
global with sharing class CRMRetailCreateEvent_I_HUM 
{
    /*
* Method Name   :   createNewEvent
* Description   :   This method is used to deserialize the request and calls the handler method for further processing of the request   
* Return Type   :   N/A
* Parameters    :   N/A
*/
    
    @HttpPost
    global static void createNewEvent()
    {
        CRMRetailCreateEvent_H_HUM objCreateEvent;
        CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM oRequest = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();        
        
        try
        {
            RestRequest request = RestContext.request;  
            oRequest = (CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(),CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM.class);
            objCreateEvent = new CRMRetailCreateEvent_H_HUM(oRequest); 
        }
        catch(Exception e)
        {
            objCreateEvent = new CRMRetailCreateEvent_H_HUM(null); 
            HUMExceptionHelper.logErrors(e,'CRMRetailCreateEvent_I_HUM','createNewEvent');
        }
    }
    
}