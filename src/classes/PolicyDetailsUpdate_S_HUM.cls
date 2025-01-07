/*******************************************************************************************************************************
Apex Class Name : PolicyDetailsUpdate_S_HUM
Version         : 1.0
Created On      : 03/16/2017
Function        : Service Class for the controller 
Test Class      : HUMPolicyDetailsUpdateControllerTest                  
Modification Log: 
 * Developer Name           Review Number                      Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pallavi R                                                 03/16/2017               Original Version - REQ 304134
 * Ajay Chakradhar                                           05/31/2021               REQ - 1755482 - Replacing constants to improve performance               
 *******************************************************************************************************************************/
 public with sharing class PolicyDetailsUpdate_S_HUM 
{
 @TestVisible private String sBody ='' ;
 private String sNoneValue = 'NONE';   
 private String sNullValue = 'Uninitialized';  
 private String sLVPlatForm =  'LV';
 private String sEMPlatForm =  'EM';   
  
  /*
   * Method Name   :  setupRequest
   * Description   :  This method is used to form http request.
   * Return Type   :  HttpRequest 
   * Parameters    :  HttpRequest,HUM_Webservice_Callout__c,string (Policy Member Id)
   */
    public HttpRequest setupRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderPolicy,Policy_Plan_Details__c policyPlan,Policy_Member__c policyMember )
    {
       HttpRequest orequest = new HttpRequest();    
        try
        {
          sBody = buildRequest(policyPlan,policyMember);
            if(String.isNotBlank(sBody))
            {
                request.setMethod(oSvcHeaderPolicy.Request_Type__c); 
                request.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, oSvcHeaderPolicy.Content_Type__c); 
                request.setHeader(GlobalCommonConstants_HUM.SOAPACTION_HUM, '');    
                request.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM,GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
                request.setBody(sBody);
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
            HUMExceptionHelper.logErrors(ex, 'PolicyDetailsUpdate_S_HUM', 'setupRequest');
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
    @testvisible private string buildRequest(Policy_Plan_Details__c policyPlan,Policy_Member__c policyMember)
    {    
        string sJsonbody = '';
        try
        {
          GBEServiceRequest_H_HUM oRequest = new GBEServiceRequest_H_HUM();
          GBEServiceRequest_H_HUM.GroupDetailDTO grpDetailDTO = new GBEServiceRequest_H_HUM.GroupDetailDTO();
          grpDetailDTO.GroupId = (policyPlan.Platform__c == sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,6):policyMember.Group_Number__c);
          grpDetailDTO.SubGroupId = (policyPlan.Platform__c == sEMPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,8) : policyPlan.Platform__c == sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(9,12):'');  
          grpDetailDTO.BenefitPackageId =policyPlan.Platform__c == sEMPlatForm ? policyPlan.Product__r.Sold_Product_Key_Value__c : ''; 
          grpDetailDTO.Platform = policyPlan.Platform__c;
          grpDetailDTO.StartDate =  (policyMember.Effective_Date__c <> null ? policyMember.Effective_Date__c : sNoneValue);                   
          grpDetailDTO.EndDate = policyMember.End_Date__c <> null ? policyMember.End_Date__c : sNoneValue;
          grpDetailDTO.StartDate =  (policyPlan.Coverage_Plan_Effective_Date__c <> null ? policyPlan.Coverage_Plan_Effective_Date__c : sNoneValue);
          grpDetailDTO.EndDate = policyPlan.Coverage_Plan_End_Date__c <> null ? policyPlan.Coverage_Plan_End_Date__c : sNoneValue;
          grpDetailDTO.BenefitSequenceNumber= (policyPlan.Platform__c  == sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(6,9) : ''); 
          grpDetailDTO.InclusionCriteria = 'EXCHANGE';
          oRequest.GroupDetailDTO = grpDetailDTO;
          sJsonbody = JSON.serialize(oRequest);         
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PolicyDetailsUpdate_S_HUM', 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
}