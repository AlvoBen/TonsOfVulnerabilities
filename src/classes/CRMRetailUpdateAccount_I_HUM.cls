/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailUpdateAccount_I_HUM 
* Version          : 1.0
* Created Date     : 1st June, 2021
* Function         : Service Class for Updation of Account services
* Test Class       : 
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ekta Bist                                  					   06/01/2021              Original Version 2322492
**********************************************************************************************************************************/
@RestResource(urlMapping='/CRMRetailServices_V1.0/UpdateAccount')
global with sharing class CRMRetailUpdateAccount_I_HUM 
{     
 /*
 * Method Name   :   updateAccount
 * Description   :   This method is deserialize the request and calls the handler method for further processing of the request   
 * Return Type   :   N/A
 * Parameters    :   N/A
 */ 
    @HttpPost
    global static void updateAccount()
    {  
        CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM oRequest = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM();        
        CRMRetailUpdateAccount_H_HUM objUpdateeAcc;
        try
        {
            RestRequest request = RestContext.request; 
            oRequest = (CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM.class);
            objUpdateeAcc = new CRMRetailUpdateAccount_H_HUM(oRequest);
        }
        catch(Exception e)
        {  
            HUMExceptionHelper.logErrors(e,'CRMRetailRetrieveAccount_I_HUM','retrieveAccount');
            objUpdateeAcc = new CRMRetailUpdateAccount_H_HUM(null);
        }
    }
}