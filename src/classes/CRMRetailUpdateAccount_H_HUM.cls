/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailUpdateAccount_H_HUM 
* Version          : 1.0
* Created Date     : 1st June, 2021
* Function         : Handler class for handling and preparing response.
* Test Class       : 
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ekta Bist                                  					    06/01/2021           Original Version 2322492
* Deepak khandelwal                                                 03/04/2024           US-0037832: T1PRJ1371022 : MF: 5571284  / SF / Storefront - CxOne - Medium- CRMRetailUpdateAccount_H_HUM VIT37935988
**********************************************************************************************************************************/
public with sharing class CRMRetailUpdateAccount_H_HUM
{       
    private static CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM response = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM(); 
    private static CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM(); 
    List<Account> listOfAccount = new List<Account>();
    /**
    * CRMRetailUpdateAccount_H_HUM Method constructor.
    * <p>
    * This method is used to call the handleRequest method.
    *
    * @param  		oRequest           Deserialized request                     		
    * @return		 NA
    */
    public CRMRetailUpdateAccount_H_HUM (CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM oRequest)
    {   
        if(oRequest != null)
        {
            request = oRequest;             
            handleRequest();
        }
        else
        {
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG);
            prepareResponse();
        }
    }
    /**
    * handleRequest Method.
    * <p>
    * This method is called from constructor and it handle the request received.
    *
    * @param  			NA                                 
    * @return 			NA                 
    */
    public void handleRequest()
    {   
        Boolean flag = false;
        flag = validateAccount();
        if(!flag)
        {
            prepareResponse();
        }
        else            
        {
            updateAccountDetails();
        }
    } 
    /**
    * validateAccount Method.
    * <p>
    * This method is used for validating the account details to check whether account is a member account or a visitor account.
    *
    * @param  		 NA                               		
    * @return		 Boolean
    */
    public Boolean validateAccount()
    {
        Id accountId;
        try
        {
            accountId = request.accountId.trim();
            listOfAccount = [Select id, Firstname, Lastname, Birthdate__c, PersonEmail, PersonMailingPostalCode, PersonMailingCity, PersonMailingState, RecordType.Name from Account where id =: accountId];
            if(listOfAccount != null && !listOfAccount.isEmpty())
            {   
                if(listOfAccount[0].RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                {
                    createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MEMERROR_MSG);
               		return false;
           	    }
                
                if(listOfAccount[0].RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR)
                    return true; 
            }
            else
            {
                createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_MATCHING_ACCOUNT);
                return false;
            }
        }
        catch(Exception e)
        {
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG);
            HUMExceptionHelper.logErrors(e,'CRMRetailUpdateAccount_H_HUM','validateAccount');
        }
        return false;
    } 
    /**
    * updateAccountDetails Method.
    * <p>
    * This method is used for updating the account details.
    *
    * @param  		 NA                               		
    * @return		 NA
    */
    public void updateAccountDetails()
    {   
        if(String.isNotBlank(request.firstName) && listOfAccount[0].FirstName != request.firstName.trim())
            listOfAccount[0].FirstName = request.firstName.trim();
        
        if(String.isNotBlank(request.lastName) && listOfAccount[0].Lastname != request.lastName.trim())
            listOfAccount[0].Lastname = request.lastName.trim();
        
        if(String.isNotBlank(request.dateOfBirth) && listOfAccount[0].Birthdate__c != request.dateOfBirth.trim())
            listOfAccount[0].Birthdate__c = request.dateOfBirth.trim();
        
        if(String.isNotBlank(request.emailId) && listOfAccount[0].PersonEmail != request.emailId.trim())
            listOfAccount[0].PersonEmail = request.emailId.trim();
        
        if(String.isNotBlank(request.zipCode) && listOfAccount[0].PersonMailingPostalCode != request.zipCode.trim())
            listOfAccount[0].PersonMailingPostalCode = request.zipCode.trim();
        
        listOfAccount[0].PersonMailingCity = request.city != null ? request.city.trim() : '';
        listOfAccount[0].PersonMailingState = request.state != null ? request.state.trim() : '';
        try
        {   
           GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE, listOfAccount, 'CRMRetailUpdateAccount_H_HUM', 'updateAccountDetails');
            createResponse(true,'');
        }
        catch(Exception e)
        {   
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG + error);
            HUMExceptionHelper.logErrors(e,'CRMRetailUpdateAccount_H_HUM','updateAccountDetails');
        }
        prepareResponse();
    } 
    /**
    * prepareResponse Method.
    * <p>
    * This method is used to prepare the response.
    *
    * @param  		NA                                 
    * @return 		NA                 
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
    * This method is used to create the response which will be sent on the service call.
    *
    * @param  		isSuccess 				Boolean                               
    * @param  		errorMsg 				String                                
    * @return 		NA                 
    */
    public void createResponse(Boolean isSuccess, String errorMsg)
    {
        response.isSuccess = isSuccess;
        if(String.isNotBlank(errorMsg))
        {
            response.errorMessage = errorMsg;
        }
    }
}