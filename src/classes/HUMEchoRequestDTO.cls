/****************************************************************************
Apex Class Name  : HUMEchoRequestDTO
Version          : 1.0 
Created Date     : JULY 3 2014
Function         : Wrapper class for generating request for Echo Service
Modification Log :
 *  Modification ID     Developer                Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *          1.0         Pranali Choudhari       07/03/2014            Original Version
 *          2.0         Pranali Choudhari       07/24/2014            Added more input variables as per changes requested
 **********************************************************************************************************************************/

global with sharing class HUMEchoRequestDTO
{
	//Variable containing Enterprise ID of Account as an input parameter
	webservice String sEnterpriseID{get;set;}
	//Variable containing type of Account as input parameter
	webservice String sType{get;set;}
	//Variable containing start date of case as input parameter
	webservice String sStartDate{get;set;}
	//Variable containing closed date of case as input parameter
	webservice String sEndDate{get;set;}
	webservice String sInquiredAbtID{get;set;}
	//Variable containing type of interacting with account
	webservice String sInteractingWithType{get;set;}    
}