/***************************************************************************************************************************************************************************
Apex Class Name  : HUMEchoRestResponseDTO_HUM
Version          : 1.0
Created Date     : Aug 15 2018
Function         : This class for the REST ECHO service input request DTO.
Test Class       : HUMEchoServiceREST_Test
 ****************************************************************************************************************************************************************************

Modification Log:

* Developer Name             Code Review               Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor                296361                  08/15/2018                  Original Version
****************************************************************************************************************************************************************************/
public with sharing class HUMEchoRestResponseDTO_HUM
{
    public HUMEchoRestResponseDTO lastInteractionDetailsResponse;
    
    public class HUMEchoRestResponseDTO
    {
        //ID of latest case for the latest interaction for an account
        public String sCaseId='';
        
        //Name of the agent who created case
        public String sCreatedBy ='';
        
        //Name of the agent who last modified the case
        public String sLastmodifiedby ='';
        
        //Intent of the Case
        public String sReason ='';
        
        //Classification of the Case
        public String sCategory='';
        
        //LastModified Date of the case
        public String sLastModifiedDateCase='';
        
        //ID of latest interaction for the account
        public String sInteractionID='';
        
        //LastModified date of the Interaction
        public String sLastModifiedDateInteraction='';
                
        //Service center of the case
        public String sSite='';
        
        //Department of the Case
        public String sDepartment='';
        
        //Name of the queue/team who last modified the case    
        public String sLastUpdatedTeamName='';
        
        //Constant value CRMS
        public String sReferenceType='';
        
        //Constant value NA 
        public String sDisposition='';
        
        //Variables defining error messages
        public String sErrorCode='';
        
        //Success or Failure Flag
        public Boolean bIsSuccess;
        
        //Error Message for Failure
        public String sErrorMessage='';
    }    
}