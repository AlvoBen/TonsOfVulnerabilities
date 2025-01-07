/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailRegisterAccount_I_HUM 
* Version          : 1.0
* Created Date     : 25th May, 2021
* Function         : Service Class for Register Account web services
* Test Class       :  
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ekta                                  					       5/25/2021              Original Version 2322470
**********************************************************************************************************************************/
@RestResource(urlMapping='/CRMRetailServices_V1.0/RegisterAccount')
global with sharing class CRMRetailRegisterAccount_I_HUM 
{    
     /*
     * Method Name   :   RegisterNewParticipants
     * Description   :   This method is deserialize the request and calls the handler method for further processing of the request   
     * Return Type   :   N/A
     * Parameters    :   N/A
     */    
    @HttpPost
    global static void registerNewParticipants()
    {        
        CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM oRequest = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM();        
        CRMRetailRegisterAccount_H_HUM objRegisterAcc;       
        try
        {        	
            RestRequest request = RestContext.request; 
            oRequest = (CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM)System.JSON.deserialize(request.requestBody.toString(), CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM.class);
            objRegisterAcc = new CRMRetailRegisterAccount_H_HUM(oRequest);            
        }
        catch(Exception e)
        {           
            HUMExceptionHelper.logErrors(e,'CRMRetailRegisterAccount_I_HUM','registerNewParticipants');
            objRegisterAcc = new CRMRetailRegisterAccount_H_HUM(null);                       
        }
    }
}