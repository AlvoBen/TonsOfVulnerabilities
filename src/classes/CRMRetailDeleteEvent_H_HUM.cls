/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailDeleteEvent_H_HUM 
* Version          : 1.0
* Created Date     : 24th June, 2021
* Function         : Handler class for handling and preparing response.
* Test Class       : CRMRetailDeleteEvent_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                                  					       06/24/2021              Original Version  2361242
**********************************************************************************************************************************/

public with sharing class CRMRetailDeleteEvent_H_HUM {
	
	private static CRMRetailVNCRequest_DTO_HUM.CRMRetailDeleteEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailDeleteEventRequest_DTO_HUM();
	private static CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM response = new CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM();

	/**
	* CRMRetailDeleteEvent_H_HUM Method constructor.
	* <p>
	* This method is used to call the extract method.
	*
	* @param  		oRequest           Deserialized request                     		
	* @return		 NA
	*/    
	
	public CRMRetailDeleteEvent_H_HUM(CRMRetailVNCRequest_DTO_HUM.CRMRetailDeleteEventRequest_DTO_HUM oRequest)
    {
        if(oRequest != null)
        {
            request = oRequest;             
            fetchEventRecordDelete();
        }
        else
        {
			createResponse(false, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG);
        }
        prepareResponse();
    }
	
	/**
	* fetchEventRecordDelete Method constructor.
	* <p>
	* This method is used to fetch the record of event and delete it.
	*
	* @param  		oRequest           Deserialized request                     		
	* @return		 NA
	*/ 
	
	private void fetchEventRecordDelete()
	{
        try{
			List<Event> lstEvent = [SELECT Id FROM Event WHERE id =: request.sfEventId.trim() LIMIT 1];
			if(!lstEvent.isEmpty())
            {
				delete lstEvent;
                createResponse(true,'');
			}
			else
			{
                createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_EVENT);
			}
		}
		catch(Exception e)
		{
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false, error);
            HUMExceptionHelper.logErrors(e,'CRMRetailDeleteEvent_H_HUM','deleteEvent'); 
		}
	}
	
	/**
	* createResponse Method.
	* <p>
	* This method is used for creating the response.
	*
	* @param  		 NA                               		
	* @return		 NA
	*/

	private void createResponse(Boolean isSuccess, String errorMessage)
	{
		response.isSuccess = isSuccess;
		response.errorMessage = errorMessage;
	}
	
	/**
	* prepareResponse Method.
	* <p>
	* This method is used for preparing the response.
	*
	* @param  		 NA                               		
	* @return		 NA
	*/

	private void prepareResponse()
	{
		String createResponse = System.JSON.serialize(response);
		RestResponse res = RestContext.response;
		res.statusCode = GLOBAL_CONSTANT_STOREFRONT_HUM.INT_CRMRETAIL_STATUSCODE;
		res.addHeader(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CONTENTTYPE, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_APPLICATION_JSON);
		res.responseBody = Blob.valueOf(createResponse);
	}

}