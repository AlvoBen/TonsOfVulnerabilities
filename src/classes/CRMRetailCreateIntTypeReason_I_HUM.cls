/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailCreateIntTypeReason_I_HUM 
* Version          : 1.0
* Created Date     : 9th June, 2021
* Function         : Service class for Create Interaction Type and Reason web services.
* Test Class       : CRMRetailCreateIntTypeReason_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                                  					       06/09/2021              Original Version  2322478
**********************************************************************************************************************************/

@RestResource(urlMapping='/CRMRetailServices_V1.0/CreateInteractionTypeReason')
global with sharing class CRMRetailCreateIntTypeReason_I_HUM {

	/*
	 * Method Name   :   createIntTypeReason
	 * Description   :   This method is deserialize the request and calls the handler method for further processing of the request   
	 * Return Type   :   N/A
	 * Parameters    :   N/A
	*/

	@HttpPost
    global static void createIntTypeReason()
    {
        CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM oRequest = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM();        
		CRMRetailCreateIntTypeReason_H_HUM objcreateIntTypeReason;
        try
        {
        	RestRequest request = RestContext.request; 
            oRequest = (CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM.class);
            objcreateIntTypeReason = new CRMRetailCreateIntTypeReason_H_HUM(oRequest);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CRMRetailCreateIntTypeReason_I_HUM','createIntTypeReason');
           	objcreateIntTypeReason = new CRMRetailCreateIntTypeReason_H_HUM(null);
        }
    }

}