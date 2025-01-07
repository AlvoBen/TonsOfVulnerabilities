/*
Class Name       : GroupContactsListInfoResponse_DTO_HUM
Created Date     : 05/20/2021
Function         : This class is used to get the response and display the values on the GroupContactsList component.
Modification Log :
*Version  Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*1.0	  Ankit Avula						     05/20/2021			  US2067694: T1PRJ0022885 - MF 8 - Display Small Group Contacts on Group Contact tab in CRM Service

-------------------------------------------------------------------------------------------------------------------------------*/

public class GroupContactsListInfoResponse_DTO_HUM{
    public list<assignmentServiceResponse> assignmentServiceResponse{get;set;}
	public class PhoneInfo{
		public String Extension {get;set;}
		public String PhoneNumber {get;set;}
		public String FaxNumber {get;set;}
	}
	public class EmailInfo{
		public String EmailStatus {get;set;}
		public String EmailAddress {get;set;}
	}
	public class DivisionInfo{
        public String DivisionDisplayName {get;set;}
		public String DivisionName {get;set;}
	}
	public class ContactTypeInfo{
		public String ContactTypeId {get;set;}
		public String ContactTypeName {get;set;}
	}
    public class LevelInfo{
        public String LevelId {get;set;}
        public String LevelName {get;set;}
    }
	public class ContactInfo{
		public String Suffix {get;set;}
        public String LastName {get;set;}
		public String FirstName {get;set;}
	}
	public class AssignmentServiceResponse{
		public DivisionInfo DivisionInfo {get;set;}
		public AddressInfo AddressInfo {get;set;}
		public EmailInfo EmailInfo {get;set;}
		public ContactTypeInfo ContactTypeInfo {get;set;}
		public PhoneInfo PhoneInfo {get;set;}
		public ContactInfo ContactInfo {get;set;}
        public LevelInfo LevelInfo {get;set;}
	}
	public class AddressInfo{
		public String AddressLine3 {get;set;}
		public String City {get;set;}
		public String State {get;set;}
		public String AddressLine2 {get;set;}
		public String Zip{get;set;}
		public String AddressLine1 {get;set;}
	}
}