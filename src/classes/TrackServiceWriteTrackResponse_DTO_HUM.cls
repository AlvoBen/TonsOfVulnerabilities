/******************************************************************************************************************
Apex class Name    : TrackServiceWriteTrackResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for Medicare Enrollment Track Service Recipients Id
Created On         : June 15,2017
Test Class         : TrackService_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0             Syed Mubarak                114278           06/15/2017        Original version: REQ - 320541
------------------------------------------------------------------------------------------------------------------- */
public with sharing class TrackServiceWriteTrackResponse_DTO_HUM
{
	public WriteTrackResponse WriteTrackresponse;

	public class WriteTrackResponse
	{
		public String Status ='';
		public String RecipientTrackingId = '';
		public String SourceTrackingId = '';
		public String CaseId = '';
		public String MilestoneId = ''; 
	}
}