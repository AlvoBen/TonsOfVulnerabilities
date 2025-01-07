/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailRegisterParticipant_I_HUM 
* Version          : 1.0
* Created Date     : 14th June, 2021
* Function         : Service Class for Register Participant for Event web services
* Test Class       : CRMRetailRegisterParticipant_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ekta                                  					       06/14/2021              Original Version 
**********************************************************************************************************************************/
@RestResource(urlMapping='/CRMRetailServices_V1.0/RegisterParticipants')
global with sharing class CRMRetailRegisterParticipant_I_HUM 
{    
     /*
     * Method Name   :   registerParticipants
     * Description   :   This method is deserialize the request and calls the handler method for further processing of the request   
     * Return Type   :   N/A
     * Parameters    :   N/A
     */    
    @HttpPost
    global static void registerParticipants()
    {        
        CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM oRequest = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM();        
        CRMRetailRegisterParticipant_H_HUM objRegParticipant;       
        try
        {        	
            RestRequest request = RestContext.request; 
            oRequest = (CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(),CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM.class);
            objRegParticipant = new CRMRetailRegisterParticipant_H_HUM(oRequest);            
        }
        catch(Exception e)
        {           
            HUMExceptionHelper.logErrors(e,'CRMRetailRegisterParticipant_I_HUM','registerParticipants');
            objRegParticipant = new CRMRetailRegisterParticipant_H_HUM(null);                       
        }
    }
}