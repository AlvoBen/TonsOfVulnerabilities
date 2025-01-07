/***************************************************************************************************************************************************************************
Apex Class Name  : HUMCaseService
Version          : 1.0
Created Date     : December 12 2014
Function         : This Webservice is used to for all the case related services.
Test Class       : HUMCaseServiceTest
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name              Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma        			 20294                    12/19/2014                   Original Version
 * Prafull Verma                 103536            		  06/10/2016                   update for Create Case Service 
 ****************************************************************************************************************************************************************************/

global with sharing class HUMCaseService
{
    
    /*
     * Method name : getCaseInfo
     * Description : This method is a webservice method and is exposed to external system to get case related information
     * Return Type : HUMGetCaseServiceResponseHelper
     * Parameter   : HUMGetCaseServiceRequestHelper 
     */
    webservice static HUMGetCaseServiceResponseHelper getCaseInfo(HUMGetCaseServiceRequestHelper request)
    {
        return getCaseRequest(request);
    }
    
    /*
     * Method name : updateCaseInfo
     * Description : This method is a webservice method and is exposed to external system to update case and atachemnts for cases
     * Return Type : HUMUpdateCaseServiceResponseHelper
     * Parameter   : HUMUpdateCaseServiceRequestHelper   
     */
    webservice static HUMUpdateCaseServiceResponseHelper updateCaseInfo(HUMUpdateCaseServiceRequestHelper request)
    {
        return updateCaseRequest(request);
    }
     /*
     * Method name : createCaseInfo
     * Description : This method is a webservice method and is exposed to external system to create cases
     * Return Type : HUMCreateCaseServiceRequestHelper 
     * Parameter   : HUMCreateCaseServiceResponseHelper 
     */
    webservice static HUMCreateCaseServiceResponseHelper createCaseInfo(HUMCreateCaseServiceRequestHelper request)
    {
        return createCaseRequest(request);
    } 
    /*
     * Method name : getCaseRequest
     * Description : This method is used to parse the getcaseinfo request and return the number of cases and other information about cases.The method will route the request to HUMGetCaseInfoService
                     where the request is parsed and exceptions are handled in that class. if getCaseInfo is set to true we get the case info and pass it back in response , if it is false only the
                     case count is retrned 
     * Return Type : HUMGetCaseServiceResponseHelper
     * Parameter   : HUMGetCaseServiceRequestHelper 
     */
    private static HUMGetCaseServiceResponseHelper getCaseRequest(HUMGetCaseServiceRequestHelper request)
    {
        HUMGetCaseServiceResponseHelper response = new HUMGetCaseServiceResponseHelper();
        List<HUMGetCaseInfoResponseHelper> lstCaseInfoResponse = new List<HUMGetCaseInfoResponseHelper>();
        Integer iCaseCount = 0;
        // Validate Request , getCaseInfo and intAboutCaseAvailabilityCheck ia always required 
        if(request.getCaseInfo != null )
        {
            // If getCaseInfo is true then get case related info needs to be pased in the response
            if(request.getCaseInfo == true && request.caseInfoRequest != null)
            {
                lstCaseInfoResponse = HUMGetCaseInfoService.getCaseRelatedInfo(request.caseInfoRequest);
                response.caseInfoResponse = lstCaseInfoResponse;
                return response;
            }
            else            
            if(request.getCaseInfo == false && request.intAboutCaseAvailabilityCheck != null)
            {
                iCaseCount = HUMGetCaseInfoService.getCases(request.intAboutCaseAvailabilityCheck).size();
                response.caseCount = iCaseCount;
                return response;
            }
            
                
        }
            
            lstCaseInfoResponse = HUMGetCaseInfoService.getErrorLogInstance(null,system.label.HUMRequiredInputsNotFound,false);  
            response.caseInfoResponse = lstCaseInfoResponse;  
        return response;
    }
    
    /*
     * Method name : updateCaseRequest 
     * Description : This method is used to parse the updatecaseinfo request and return the update status. The method will route the request to HUMCaseUpdateLogAttachmentService
                     where the request is parsed and exceptions are handled in that class
     * Return Type : HUMUpdateCaseServiceResponseHelper
     * Parameter   : HUMUpdateCaseServiceRequestHelper 
     */
    private static HUMUpdateCaseServiceResponseHelper updateCaseRequest(HUMUpdateCaseServiceRequestHelper request)
    {
        HUMUpdateCaseServiceResponseHelper response = new HUMUpdateCaseServiceResponseHelper();
        response.updateServiceResponse = HUMCaseUpdateLogAttachmentService.updateCaseAndAttachment(request.CaseUpdateRequest);
        return response; 
    }
    
     /*
     * Method name : createCaseRequest
     * Description : This method is used to expose the case object to create case records.
     * Return Type : HUMUpdateCaseServiceResponseHelper
     * Parameter   : HUMUpdateCaseServiceRequestHelper 
     */
   private static HUMCreateCaseServiceResponseHelper createCaseRequest(HUMCreateCaseServiceRequestHelper request)
   {
       HUMCreateCaseServiceResponseHelper response = new HUMCreateCaseServiceResponseHelper();
       response.createCaseResponse = CreateCase_D_HUM.createCase(request.createCaseRequest);
       return response; 
   }
}