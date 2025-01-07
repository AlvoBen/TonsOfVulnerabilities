/*****************************************************************************************
Apex class Name    : EligibilityInformation_LC_HUM
Version            : 1.0
Function           : controller for retrieving eligibility info
Created Date       : 03/12/2021
Test Class         : EligibilityInformation_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Joel George                                            05/03/2021                  initial version
****************************************************************************************************************/

public with sharing class EligibilityInformation_LC_HUM 
{
   /*
    * Method Name   :  callCIMedMultipleMemberService
    * Description   :  This method is continuation call for CI Medicare Multiple service.
    * Return Type   :  Continuation
    * Parameters    :  NA
    */  
    @AuraEnabled(continuation=true)
    public static Object callCIMedMultipleMemberService(String recId)
    {    
        return EligibilityInformation_LS_HUM.callCIMedMultipleMemberService(recId);       
    }
   
   /*
    * Method Name   :  processAllResponses
    * Description   :  This method is to handle multiple calls at a time.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    @AuraEnabled
    public static Object processAllResponses(List<String> labels,object state)
    {
       try 
        {
            return EligibilityInformation_LS_HUM.processAllResponses(labels, state);
        }
        catch(Exception ex)
        {   
            HUMExceptionHelper.logErrors(ex, 'HUMEligibilityInfoController', 'processAllResponses');
            return null;
        }
    }

}