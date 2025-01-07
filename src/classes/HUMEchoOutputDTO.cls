/****************************************************************************
Apex Class Name  : HUMEchoOutputDTO
Version          : 1.0 
Created Date     : JULY 2 2014
Function         : Wrapper class for generating response for Echo Interface
Modification Log :
 *  Modification ID     Developer				Code review                Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *          1.0         Pranali Choudhari       17465					   07/03/2014            Original Version

 **********************************************************************************************************************************/

global with sharing class HUMEchoOutputDTO
{
    
    //ID of latest case for the latest interaction for an account
    webservice String sCaseId{get;set;}
    //Name of the agent who created case
    webservice String sCreatedBy {get;set;}
    //Name of the agent who last modified the case
    webservice String sLastmodifiedby {get;set;}
    webservice String sReason {get;set;}
    webservice String sCategory{get;set;}
    webservice String sLastModifiedDateCase{get;set;}
    //ID of latest interaction for the account
    webservice String sInteractionID{get;set;}
    webservice String sLastModifiedDateInteraction{get;set;}
    //ID of the queue/team who created case
    webservice String sCreatedTeamID{get;set;}
    webservice String sSite{get;set;}
    webservice String sDepartment{get;set;}
    //Name of the queue/team who last modified the case
    webservice String sLastUpdatedTeamName{get;set;}
    //Whether coming from CCP or CRMS
    webservice String sReferenceType{get;set;}
    webservice String sDisposition{get;set;}
    //Variables defining error messages
    webservice String sErrorCode{get;set;}
    webservice Boolean bIsSuccess{get;set;}
    webservice String sErrorMessage{get;set;}
}