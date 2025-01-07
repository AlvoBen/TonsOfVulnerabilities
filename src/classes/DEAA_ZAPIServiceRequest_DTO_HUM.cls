/***********************************************************************************************************************
Apex Class Name  : DEAA_ZAPIServiceRequest_DTO_HUM
Version          : 1.0  
Created Date     : November 19, 2019
Function         : This is the DTO for the request of the zAPI service for DEAA template.
Test Class       : 
****************************************************************************
Modification Log:

* Developer Name              Code Review #                       Date                        Description
*------------------------------------------------------------------------------------------------------------
* Mohammed Noor                                                  11/19/2019                 Original Version 
***********************************************************************************************************************/
public with sharing class DEAA_ZAPIServiceRequest_DTO_HUM 
{
	public SavePartDOptOut SavePartDOptOutRequest;
	
	public class SavePartDOptOut
	{
		public string CaseNumber = '';	//CaseNumber
		public string RealtimeTransTimestamp = '';	//The timestamp of the member interaction.(MM/DD/YYYY HH:MM:SS.NNNNNN)
		public string E2ETrackId = '';		//Track ID from BAM 
		public string OptOutRequired = '';	//DEAA OptOut Indicator
	}
}