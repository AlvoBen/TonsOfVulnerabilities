/***************************************************************************************************************************************************************************
Apex Class Name  : HUMEchoRestRequestDTO_HUM
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
public with sharing class HUMEchoRestRequestDTO_HUM 
{
    public HUMEchoRestRequestDTO LastInteractionDetails;
    
    public class HUMEchoRestRequestDTO
    {
        //Variable containing the UUID for the interaction for the survey
        public String sUUID = '';
     
        //Variable containing ID of Account(Interacting With ID)
        public String sInteractingWithID = '';
        
        //Variable containing type of Interacting with account
        public String sInteractingWithType='';
        
        //Variable containing ID of the Interacting About Account
        public String sInteractingAboutID='';
        
        //Variable containing type of Interacting with account
        public String sInteractingAboutType='';
        
        //Variable containing start date of case as input parameter (MM/DD/YYYY)
        public String sStartDate='';
        
        //Variable containing closed date of case as input parameter (MM/DD/YYYY)
        public String sEndDate='';                                                                
    }    
}