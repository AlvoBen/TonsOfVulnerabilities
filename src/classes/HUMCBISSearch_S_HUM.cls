/*******************************************************************************************************************************
Apex Class Name : HUMCBISSearch_S_HUM.cls
Version         : 1.0
Created On      : May 10, 2021
Function        : Service layer for HUMCBISSearch Page
Test Class      : HUMCBISSearch_S_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Firoja Begam                                        05/10/2021                 US-2229318 - PR00094232/T1PRJ0002736 - 2021 MF6 - Security - SF - CBIS Specialty Service Migration
* Bhumika S                                           10/14/2022                 User Story 3883795: T1PRJ0533784 -CR 6/MF 25 - Enhance the search capability Functionality on search enrollment tab within CRMs-CBIS
*****************************************************************************************************************************************/
public with sharing class HUMCBISSearch_S_HUM
{

    @TestVisible private static String sBodyCBISSpecialty ='' ; 


    /*
   * Method Name   :  setupRequest
   * Description   :  This method is used to form http request.
   * Return Type   :  HttpRequest 
    * Parameters    :  HttpRequest,HUM_Webservice_Callout__c,string (FirstName, Lastname, DOB, State)
   */
    public HttpRequest setupRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderPolicy, HUMSearchEnrollmentHelper oSearchWrapperRequest)
    {
       HttpRequest orequest = new HttpRequest();    
        try
        {
          sBodyCBISSpecialty = buildRequest(oSearchWrapperRequest);
            if(String.isNotBlank(sBodyCBISSpecialty))
            {
                request.setMethod(oSvcHeaderPolicy.Request_Type__c); 
                request.setHeader(GlobalCommonConstants_HUM.Content_type_HUM, oSvcHeaderPolicy.Content_Type__c); 
                request.setHeader(GlobalCommonConstants_HUM.SOAPAction_HUM, '');    
                request.setHeader(GlobalCommonConstants_HUM.Connection_HUM,GlobalCommonConstants_HUM.Keep_alive_HUM);
                request.setBody(sBodyCBISSpecialty);
                if(!Test.isRunningTest())    
                {
                  request.setClientCertificateName(oSvcHeaderPolicy.Certificate_Name__c);
                }
                request.setTimeout(Integer.valueof(oSvcHeaderPolicy.Timeout__c));
                request.setEndpoint(oSvcHeaderPolicy.End_Point_URL__c);  
                orequest = request;
            }
            else
            {
               orequest = null;  
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMCBISSearch_S_HUM', 'setupRequest');
            orequest = null;
        }
        return orequest; 
    }
    /*
    *  This method is used to Process the Json response from service to Wrapper List
    *  @name   buildRequest
    *  @param  
    *  @return string - Returns the serialized body of the request
    *  @throws NA
    */ 
    @testvisible private string buildRequest(HUMSearchEnrollmentHelper oSearchWrapperRequest)
    {    
      string sJsonbody = '';

        try
        {
          HUMCBISSearchRequest_H_HUM oRequest = new HUMCBISSearchRequest_H_HUM(); 
          HUMCBISSearchRequest_H_HUM.GetPendHistoryTermsDetails specialtyRequestDTO = new HUMCBISSearchRequest_H_HUM.GetPendHistoryTermsDetails();
         
          specialtyRequestDTO.FirstName = (oSearchWrapperRequest.sFirstName <> null ? oSearchWrapperRequest.sFirstName : ''); 
          specialtyRequestDTO.LastName = (oSearchWrapperRequest.sLastName <> null ? oSearchWrapperRequest.sLastName : '');
          specialtyRequestDTO.DOB =  (oSearchWrapperRequest.sBirthdate <> null ? oSearchWrapperRequest.sBirthdate : '');                   
          specialtyRequestDTO.StateCode = (oSearchWrapperRequest.sState <> null ? oSearchWrapperRequest.sState : '');
	        specialtyRequestDTO.SSN = (oSearchWrapperRequest.sSSN <> null ? oSearchWrapperRequest.sSSN : '');	 
          oRequest.GetPendHistoryTermsDetails = specialtyRequestDTO;
          sJsonbody = JSON.serialize(oRequest);         
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMCBISSearch_S_HUM', 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
}