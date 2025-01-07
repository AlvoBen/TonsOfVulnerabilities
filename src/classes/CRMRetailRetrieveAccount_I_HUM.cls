/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailRetrieveAccount_I_HUM 
* Version          : 1.0
* Created Date     : 28th May, 2021
* Function         : Service Class for Retrieve Account web services
* Test Class       : CRMRetailRetrieveAccount_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                                  					       5/28/2021              Original Version
**********************************************************************************************************************************/
@RestResource(urlMapping='/CRMRetailServices_V1.0/RetrieveAccount')
global with sharing class CRMRetailRetrieveAccount_I_HUM 
{
    
 /*
 * Method Name   :   retrieveAccount
 * Description   :   This method is deserialize the request and calls the handler method for further processing of the request   
 * Return Type   :   N/A
 * Parameters    :   N/A
 */
    
    @HttpPost
    global static void retrieveAccount()
    {
        CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM oRequest = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();        
		CRMRetailRetrieveAccount_H_HUM objRetrieveAcc;
        try
        {
        	RestRequest request = RestContext.request; 
            oRequest = (CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM.class);
            objRetrieveAcc = new CRMRetailRetrieveAccount_H_HUM(oRequest);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CRMRetailRetrieveAccount_I_HUM','retrieveAccount');
            objRetrieveAcc = new CRMRetailRetrieveAccount_H_HUM(null);
        }
    }
}