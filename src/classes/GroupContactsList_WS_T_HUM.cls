/*
Class Name       : GroupContactsList_WS_T_HUM
Created Date     : 05/20/2021
Function         : Webservice Test Class for EPICC Service for GroupContactsList call
Modification Log :
*Version  Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*1.0	  Ankit Avula						     05/20/2021			  US2067694: T1PRJ0022885 - MF 8 - Display Small Group Contacts on Group Contact tab in CRM Service

-------------------------------------------------------------------------------------------------------------------------------*/

@isTest
private class GroupContactsList_WS_T_HUM{
	static testmethod void getGroupContactsRequestTest(){
		Test.startTest();
		
		GroupContactsList_WS_HUM.getGroupContactsRequest(
			new HUM_Webservice_Callout__c(
				End_Point_URL__c = 'https://dev-crmservicews.humana.com/EPICCService/ListContactAssignmentsByGroupNumber',
				Request_Type__c = 'POST',
				Content_Type__c = 'application/json',
				Timeout__c = '12000',
				Certificate_Name__c = 'Humana_GuidanceCare_ConsmrMgt'
			)
		);
		
		Test.stopTest();
	}
	
	static testmethod void getGroupContactsRequestBodyTest(){
		Test.startTest();
		
		GroupContactsList_WS_HUM.getGroupContactsRequestBody('500150','true','asc','LastName','true','1','true','1','9999');
		
		Test.stopTest();
	}
    
}