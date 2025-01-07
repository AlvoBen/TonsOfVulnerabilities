/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailCheckSFStatus_I_HUM 
* Version          : 1.0
* Created Date     : 25th May, 2021
* Function         : Service Class for Dummy API
* Test Class       : CRMRetailCheckSFStatus_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Akshay Pai                                  					     6/3/2022            Original Version 3175716
**********************************************************************************************************************************/
@RestResource(urlMapping='/CRMRetailServices_V1.0/CheckSFStatus')
global class CRMRetailCheckSFStatus_I_HUM {
    
    /*
     * Method Name   :   CheckSFStatus
     * Description   :   Method to validate health of Salesforce rest services
     * Return Type   :   N/A
     * Parameters    :   N/A
     */    
    @HttpPost
    global static void CheckSFStatus()
    {        
        try
        {        	
            String createResponse = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATEDUMMYRES;
            RestResponse res = RestContext.response;
			res.statusCode = GLOBAL_CONSTANT_STOREFRONT_HUM.INT_CRMRETAIL_STATUSCODE;
			res.addHeader(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CONTENTTYPE, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_APPLICATION_JSON);
            res.responseBody = Blob.valueOf(createResponse);            
        }
        catch(Exception e)
        {           
            HUMExceptionHelper.logErrors(e,'CRMRetailCheckSFStatus_I_HUM','CheckSFStatus');                   
        }
    }

}