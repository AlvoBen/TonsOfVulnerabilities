/*
Class Name       : GroupContactsListInfoResponse_DTO_T_HUM
Created Date     : 05/20/2021
Function         : This is a test class for GroupContactsListInfoResponse_DTO_HUM.
Modification Log :
*Version  Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*1.0	  Ankit Avula						     05/20/2021			  US2067694: T1PRJ0022885 - MF 8 - Display Small Group Contacts on Group Contact tab in CRM Service

-------------------------------------------------------------------------------------------------------------------------------*/

@isTest
public class GroupContactsListInfoResponse_DTO_T_HUM {
	static testMethod void assignmentListingInfoWrapperTest(){
        Test.startTest();
        
        GroupContactsListInfoResponse_DTO_HUM obj = new GroupContactsListInfoResponse_DTO_HUM();
        obj.assignmentServiceResponse = new list<GroupContactsListInfoResponse_DTO_HUM.assignmentServiceResponse>();
        
        GroupContactsListInfoResponse_DTO_HUM.assignmentServiceResponse asstSR = new GroupContactsListInfoResponse_DTO_HUM.assignmentServiceResponse();
        
        asstSR.PhoneInfo = new GroupContactsListInfoResponse_DTO_HUM.PhoneInfo();
        asstSR.PhoneInfo.Extension = '';
        asstSR.PhoneInfo.PhoneNumber = '';
        asstSR.PhoneInfo.FaxNumber = '';
        
        asstSR.ContactTypeInfo = new GroupContactsListInfoResponse_DTO_HUM.ContactTypeInfo();
        asstSR.ContactTypeInfo.ContactTypeId = '';
        asstSR.ContactTypeInfo.ContactTypeName = '';
        
        asstSR.DivisionInfo = new GroupContactsListInfoResponse_DTO_HUM.DivisionInfo();
        asstSR.DivisionInfo.DivisionDisplayName = '';
        asstSR.DivisionInfo.DivisionName = '';
        
        asstSR.AddressInfo = new GroupContactsListInfoResponse_DTO_HUM.AddressInfo();
        asstSR.AddressInfo.AddressLine3 = '';
        asstSR.AddressInfo.City = '';
        asstSR.AddressInfo.State = '';
        asstSR.AddressInfo.Zip = '';
        asstSR.AddressInfo.AddressLine1 = '';
        asstSR.AddressInfo.AddressLine2 = '';
        
        asstSR.EmailInfo = new GroupContactsListInfoResponse_DTO_HUM.EmailInfo();
        asstSR.EmailInfo.EmailStatus = '';
        asstSR.EmailInfo.EmailAddress = '';
        
        asstSR.Contactinfo =  new GroupContactsListInfoResponse_DTO_HUM.ContactInfo();
        asstSR.ContactInfo.Suffix = '';
        asstSR.ContactInfo.LastName = '';
        asstSR.ContactInfo.FirstName = '';
        
        asstSR.LevelInfo = new GroupContactsListInfoResponse_DTO_HUM.LevelInfo();
        asstSR.LevelInfo.LevelId = '';
        asstSR.LevelInfo.LevelName = '';
        
        Test.stopTest();
    }
}