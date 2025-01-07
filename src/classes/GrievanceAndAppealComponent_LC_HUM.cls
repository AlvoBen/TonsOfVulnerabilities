/**
 *   name        : GrievanceAndAppealComponent_LC_HUM
 *   CreateDate  : 09/05/2022
 *   Description : This class serves as controller for GrievanceAndAppeal LWC component
 *   Version     : 1.0
 *   Test        : GrievanceAndAppealComponent_LT_HUM
 *   Modification Log:
    
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Abhishek Mangutkar                          09/05/2022                  Original Version
 ************************************************************************************************************************************************* */
public with sharing class GrievanceAndAppealComponent_LC_HUM
{
   /**
    * This continuation invoked method generates list of Greivance and Appeal cases associated to the account.
    * @Name 	getArchiveAttachments 
    * @param    sRecordID and requestBody
    * @return   response from service in JSON 
    */
    @AuraEnabled(continuation=true)
    public static continuation getArchiveAttachments(string sRecordID, string requestBody) 
    {           
        return GrievanceAndAppeal_LS_HUM.sendGreivanceAndAppealRequest(sRecordID, requestBody);
    }
    
       
    /**
    * This continuation invoked method generates list of Greivance and Appeal cases associated to the account.
    * @Name 	processGreivanceAndAppealResponse 
    * @param    labels and State of continuation
    * @return   response from service in JSON 
    */
    @AuraEnabled
    public static object processGreivanceAndAppealResponse(List < string > labels, object State)
    {
        return GrievanceAndAppeal_LS_HUM.processGreivanceAndAppealResponse(labels, State);
    } 
}