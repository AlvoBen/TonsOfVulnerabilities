/*
Class Name       : GroupContactsListInfoRequest_DTO_HUM
Created Date     : 05/20/2021
Function         : This class is used to build the request with the default values for the GroupContactsList component.
Modification Log :
*Version  Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*1.0	  Ankit Avula						     05/20/2021			  US2067694: T1PRJ0022885 - MF 8 - Display Small Group Contacts on Group Contact tab in CRM Service

-------------------------------------------------------------------------------------------------------------------------------*/

public class GroupContactsListInfoRequest_DTO_HUM{
	public AssignmentListingInfo assignmentListingInfo;
	public class AssignmentListingInfo{
		public String IsShowAlternate;
		public String SortDirection;
		public String SortBy;
		public String IsRequestorList;
		public String CurrentPage;
		public String IsSameFeature;
		public String PageSize;
		public String DivisionId;
		public String SearchText;
	}
}