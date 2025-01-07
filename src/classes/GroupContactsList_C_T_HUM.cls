/*
Class Name       : GroupContactsList_C_T_HUM
Created Date     : 05/20/2021
Function         : Test class for GroupContactsList_C_HUM controller class
Modification Log :
*Version  Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*1.0	  Ankit Avula						     05/20/2021			  US2067694: T1PRJ0022885 - MF 8 - Display Small Group Contacts on Group Contact tab in CRM Service

-------------------------------------------------------------------------------------------------------------------------------*/


@isTest
private class GroupContactsList_C_T_HUM{
	static testmethod void doTesting(){
		// Insert custom settings
		insert new HUM_Webservice_Callout__c(
			
			Name = GroupContactsList_WS_HUM.serviceName,
			End_Point_URL__c = 'https://dev-crmservicews.humana.com/EPICCService/ListContactAssignmentsByGroupNumber',
			Request_Type__c = 'POST',
			Content_Type__c = 'application/json',
			Timeout__c = '120000',
			Certificate_Name__c = 'Humana_GuidanceCare_ConsmrMgt'
		);
	
		Test.startTest();
		
		GroupContactsList_C_HUM cmpCtrl = new GroupContactsList_C_HUM();
		
		cmpCtrl.closeSection();
		
		Continuation cont = (Continuation)cmpCtrl.startAssignmentListRequest();
		
		// Setting fake response
		HttpResponse response = new HttpResponse();
        response.setBody('{ "assignmentServiceResponse": [ { "ContactInfo": { "FirstName": "Iain  G", "LastName": "D Alfonso", "Suffix": "" }, "ContactTypeInfo": { "ContactTypeId": "19", "ContactTypeName": "Primary" }, "LevelInfo": { "LevelId": "0", "LevelName": "Group" }, "DivisionInfo": { "DivisionName": "", "DivisionDisplayName": "" }, "AddressInfo": { "AddressLine1": "12400 W Hwy 71", "AddressLine2": "Suite 350  Pmb 341", "AddressLine3": "", "City": "Austin", "State": "TX", "Zip": "78738" }, "EmailInfo": { "EmailAddress": "", "EmailStatus": "Migrating" }, "PhoneInfo": { "PhoneNumber": "5123011675", "Extension": "", "FaxNumber": "0" } }, { "ContactInfo": { "FirstName": "Iain", "LastName": "Gillott", "Suffix": "" }, "ContactTypeInfo": { "ContactTypeId": "19", "ContactTypeName": "Primary" }, "LevelInfo": { "LevelId": "0", "LevelName": "Division" }, "DivisionInfo": { "DivisionName": "Igillottresearch", "DivisionDisplayName": "01 - Igillottresearch" }, "AddressInfo": { "AddressLine1": "12400 W Hwy 71", "AddressLine2": "Suite #350  Pmb 341", "AddressLine3": "", "City": "Austin", "State": "TX", "Zip": "78738" }, "EmailInfo": { "EmailAddress": "", "EmailStatus": "Migrating" }, "PhoneInfo": { "PhoneNumber": "5123011675", "Extension": "", "FaxNumber": "0" } }, { "ContactInfo": { "FirstName": "Patricia", "LastName": "Granberry", "Suffix": "" }, "ContactTypeInfo": { "ContactTypeId": "20", "ContactTypeName": "Backup" }, "LevelInfo": { "LevelId": "0", "LevelName": "Group" }, "DivisionInfo": { "DivisionName": "", "DivisionDisplayName": "" }, "AddressInfo": { "AddressLine1": "", "AddressLine2": "", "AddressLine3": "", "City": "", "State": "", "Zip": "" }, "EmailInfo": { "EmailAddress": "", "EmailStatus": "NA" }, "PhoneInfo": { "PhoneNumber": "0", "Extension": "", "FaxNumber": "0" } }, { "ContactInfo": { "FirstName": "Mohan", "LastName": "Test", "Suffix": "" }, "ContactTypeInfo": { "ContactTypeId": "20", "ContactTypeName": "Backup" }, "LevelInfo": { "LevelId": "0", "LevelName": "Group" }, "DivisionInfo": { "DivisionName": "", "DivisionDisplayName": "" }, "AddressInfo": { "AddressLine1": "", "AddressLine2": "", "AddressLine3": "", "City": "", "State": "", "Zip": "" }, "EmailInfo": { "EmailAddress": "", "EmailStatus": "NA" }, "PhoneInfo": { "PhoneNumber": "5022980669", "Extension": "", "FaxNumber": "0" } } ] }');

		Test.setContinuationResponse(cmpCtrl.continuationRequestLabel, response);
		
		Object result = Test.invokeContinuationMethod(cmpCtrl, cont);
		
		Test.stopTest();
	}
}