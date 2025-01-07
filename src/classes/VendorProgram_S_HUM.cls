/*
Class Name       : VendorProgram_S_HUM 
Version          : 1.0 
Created Date     : Sep 08 2020
Function         : Service Class to Controller
Modification Log

  Developer       Code Review        Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Firoja Begam                       9/8/2020             Original Version -- 1279297 -- new fields in policy details section (group policy page) 
* Ajay Chakradhar                    11/22/2021           DEF 4182 FIX - Global Constant replace for performance improvement                
 *******************************************************************************************************************************/
 public with sharing class VendorProgram_S_HUM 
{
 @TestVisible private String sBodyVendor ='' ;
 private String sNoneValue = 'NONE';  
 private String sNullValue = 'Uninitialized';  
 private String sLVPlatForm = 'LV';
 private String sEMPlatForm = 'EM'; 
 private String sZeroOne = '01';
 private String sZeroZeroOne = '001';
 private String sDummyBenValue = 'XXXXXXXX';
 private String GROUPSTRING = 'GROUP';
 private String HUM_TRUE_VAL = 'true';
  
  /*
   * Method Name   :  setupRequest
   * Description   :  This method is used to form http request.
   * Return Type   :  HttpRequest 
   * Parameters    :  HttpRequest,HUM_Webservice_Callout__c,string (Policy Plan Id)
   */
    public HttpRequest setupRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderPolicy,Policy_Plan_Details__c policyPlan)
    {
       HttpRequest orequest = new HttpRequest();    
        try
        {
          sBodyVendor = buildRequest(policyPlan);
            if(String.isNotBlank(sBodyVendor))
            {
                request.setMethod(oSvcHeaderPolicy.Request_Type__c); 
                request.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, oSvcHeaderPolicy.Content_Type__c); 
                request.setHeader(GlobalCommonConstants_HUM.SOAPACTION_HUM, '');    
                request.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM,GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
                request.setBody(sBodyVendor);
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
            HUMExceptionHelper.logErrors(ex, 'VendorProgram_S_HUM', 'setupRequest');
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
    @testvisible private string buildRequest(Policy_Plan_Details__c policyPlan)
    {    
        string sJsonbody = '';
        try
        {
          GBEServiceRequest_H_HUM oRequest = new GBEServiceRequest_H_HUM();
          GBEServiceRequest_H_HUM.GroupDetailDTO grpDetailDTO = new GBEServiceRequest_H_HUM.GroupDetailDTO();
          grpDetailDTO.GroupId = (policyPlan.Platform__c == sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,6):policyPlan.Group_Number__c);
          grpDetailDTO.SubGroupId = (policyPlan.Platform__c == sEMPlatForm ? (policyPlan.Group_Number__c+ sZeroOne) : policyPlan.Platform__c == sLVPlatForm ? sZeroZeroOne:'');
          grpDetailDTO.BenefitPackageId = policyPlan.Platform__c == sEMPlatForm ? sDummyBenValue : ''; 
          grpDetailDTO.Platform = policyPlan.Platform__c;
          grpDetailDTO.StartDate =  (policyPlan.Coverage_Plan_Effective_Date__c <> null ? policyPlan.Coverage_Plan_Effective_Date__c : '');
          grpDetailDTO.EndDate = (policyPlan.Coverage_Plan_End_Date__c <> null ? policyPlan.Coverage_Plan_End_Date__c : '');
          grpDetailDTO.BenefitSequenceNumber='';
          grpDetailDTO.DivisionStartPosition ='';
          grpDetailDTO.DivisionRecordsToReturn ='';
          grpDetailDTO.InclusionCriteria = GROUPSTRING;
          grpDetailDTO.VendorOfferProgramGroup = HUM_TRUE_VAL;
          grpDetailDTO.VendorOfferProgramSubGroup = HUM_TRUE_VAL;
          oRequest.GroupDetailDTO = grpDetailDTO;
          sJsonbody = JSON.serialize(oRequest);         
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'VendorProgram_S_HUM', 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
}