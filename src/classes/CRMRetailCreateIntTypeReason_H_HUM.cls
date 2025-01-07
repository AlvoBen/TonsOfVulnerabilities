/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailCreateIntTypeReason_H_HUM 
* Version          : 1.0
* Created Date     : 9th June, 2021
* Function         : Handler class for handling and preparing response.
* Test Class       : CRMRetailCreateIntTypeReason_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                                  					       06/09/2021              Original Version  2361100
**********************************************************************************************************************************/

public with sharing class CRMRetailCreateIntTypeReason_H_HUM {
	private static CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM();
	private static CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM response = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM();

	/**
	* CRMRetailCreateIntTypeReason_H_HUM Method constructor.
	* <p>
	* This method is used to call the extract method.
	*
	* @param  		oRequest           Deserialized request                     		
	* @return		 NA
	*/    
	
	public CRMRetailCreateIntTypeReason_H_HUM(CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM oRequest)
    {
        if(oRequest != null)
        {
            request = oRequest;             
            fetchIntTypeWithReason();
        }
        else
        {
			createResponse(false, '',' ', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG);
        }
        prepareResponse();
    }
	
	/**
	* fetchIntTypeWithReason Method.
	* <p>
	* This method is used for retrieving the data for preparing response.
	*
	* @param  		 NA                               		
	* @return		 NA
	*/
	
	public void fetchIntTypeWithReason()
	{
		List<Storefront_I_Type__c> lstInteractionType = new List<Storefront_I_Type__c>();
		Id getIntTypeId;
		Id getReasonTypeId;
		lstInteractionType = [Select Id,Name FROM Storefront_I_Type__c WHERE Name =: request.intTypeName.trim() AND Inactive__c = false];
		
		if(lstInteractionType != null && !lstInteractionType.isEmpty())
		{
			
			for(Storefront_I_Type__c objIntType : lstInteractionType)
			{
				if(request.intTypeName.trim().equals(objIntType.Name))
				{
					getIntTypeId = objIntType.Id;
				}
			}
			
			if(getIntTypeId != null)
			{
				List<Storefront_I_Reason__c> lstRetailIntReason =[Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name =: request.intReasonName.trim() AND Interaction_Type__c =: getIntTypeId];
				
				if(lstRetailIntReason != null && !lstRetailIntReason.isEmpty())
				{
					for(Storefront_I_Reason__c objIntReason : lstRetailIntReason)
					{
						if(request.intReasonName.trim().equals(objIntReason.Name))
						{
							getReasonTypeId = objIntReason.Id;
                            break;
						}
					}
					
					if(getReasonTypeId != null)
					{
						createResponse(true, getIntTypeId, getReasonTypeId, '');
						return;
					}
					
				}
			}
		}
        createIntTypeReason(getIntTypeId);
	}

	/**
	* createIntTypeReason Method.
	* <p>
	* This method is used for creating the data for preparing response.
	*
	* @param  		 NA                               		
	* @return		 NA
	*/

	public void createIntTypeReason(Id intId)
	{
		Savepoint sp = Database.setSavepoint();
        try
		{
			Id getIntTypeId;
            Id getIntReasonId;
			
			if(intId != null)
			{
                getIntTypeId = intId;
			}
			else
			{
                Storefront_I_Type__c objIntType = new Storefront_I_Type__c();
				objIntType.Name = request.intTypeName.trim();
				objIntType.Inactive__c = false;
				insert objIntType;
				getIntTypeId = objIntType.Id;
			}
			Storefront_I_Reason__c objIntReason = new Storefront_I_Reason__c();
			objIntReason.Name = request.intReasonName.trim();
			objIntReason.Interaction_Type__c = getIntTypeId;
			insert objIntReason;
			
			getIntReasonId = objIntReason.Id;
			createResponse(true, getIntTypeId, getIntReasonId, '');			
		}
		catch(Exception e)
		{
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false, '', '', error);
            Database.rollback(sp);
            HUMExceptionHelper.logErrors(e,'CRMRetailCreateIntTypeReason_H_HUM','createIntTypeReason');          
		}
	}

	/**
	* prepareResponse Method.
	* <p>
	* This method is used for preparing the response.
	*
	* @param  		 NA                               		
	* @return		 NA
	*/

	public void prepareResponse()
	{
		String createResponse = System.JSON.serialize(response);
		RestResponse res = RestContext.response;
		res.statusCode = GLOBAL_CONSTANT_STOREFRONT_HUM.INT_CRMRETAIL_STATUSCODE;
		res.addHeader(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CONTENTTYPE, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_APPLICATION_JSON);
		res.responseBody = Blob.valueOf(createResponse);
	}

	/**
	* createResponse Method.
	* <p>
	* This method is used for creating the response.
	*
	* @param  		 NA                               		
	* @return		 NA
	*/

	public void createResponse(Boolean isSuccess, String intTypeId, String intReasonId, String errorMessage)
	{
		response.isSuccess = isSuccess;
		response.intTypeId = intTypeId;
		response.intReasonId = intReasonId;
		response.errorMessage = errorMessage;
	}
}