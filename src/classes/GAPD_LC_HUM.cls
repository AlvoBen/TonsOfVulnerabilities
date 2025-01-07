/**
 *   name        : GAPD_LC_HUM
 *   Description : This class serves as controller for GrievanceAndAppeal and ProviderDispute LWC component
 *   Version     : 1.0
 *   Test        : GAPD_LT_HUM
 *   Modification Log:
    
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Abhishek Mangutkar                          09/05/2022                  Original Version
 * Aishwarya Pawar                          	10/13/2022                 REQ -3825306 ProviderDispute Service call changes 
 ************************************************************************************************************************************************* */


public with sharing class GAPD_LC_HUM
{
   /**
    * This continuation invoked method generates list of Greivance and Appeal cases/ Provider Dispute Cases associated to the account.
    * @Name 	getArchiveAttachments 
    * @param    sRecordID and requestBody and providerDisputeFlag
    * @return   response from service in JSON 
    */
    @AuraEnabled(continuation=true)
    public static continuation getArchiveAttachments(string sRecordID, string requestBody  , Boolean providerDisputeFlag ) 
    {           
        return GAPD_LS_HUM.sendGAPDRequest(sRecordID, requestBody, providerDisputeFlag);
    }
    
       
    /**
    * This continuation invoked method generates list of Greivance and Appeal cases/ ProviderDispute Cases associated to the account.
    * @Name 	processGAPDResponse 
    * @param    labels and State of continuation
    * @return   response from service in JSON 
    */
    @AuraEnabled
    public static object processGAPDResponse(List < string > labels, object state)
    {
        return GAPD_LS_HUM.processGAPDResponse(labels, state);
    } 
}