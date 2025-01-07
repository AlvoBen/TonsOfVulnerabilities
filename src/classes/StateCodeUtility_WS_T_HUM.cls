/*
Class Name       : StateCodeUtility_WS_T_HUM
Created Date     : 08/31/2021
Function         : Webservice Test Class for EnterpriseAddress Service for GetCountyDetails call
Modification Log :
*Version  Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*1.0	  Ankit Avula						     08/31/2021			  US2365934: T1PRJ0002855/PR00094919 - Account Management - Unknown Records - Prefill State Dropdown when user Inputs Zip, Create Unknown Member, Provider, and Group
-------------------------------------------------------------------------------------------------------------------------------*/
@isTest
public class StateCodeUtility_WS_T_HUM {
	static testmethod void getStateCodeRequestTest(){
		Test.startTest();
        HUM_Webservice_Callout__c hwc = new HUM_Webservice_Callout__c();
        hwc.Name = 'EnterpriseAddress_GetCountyDetails';
		hwc.End_Point_URL__c = 'https://int-crmservicews.humana.com/EnterpriseAddressService_V2.0/GetCountyDetails';
		hwc.Request_Type__c = 'POST';
		hwc.Content_Type__c = 'application/json';
		hwc.Timeout__c = '12000';
		hwc.Certificate_Name__c = 'Humana_GuidanceCare_ConsmrMgt';
        insert hwc;
        
		StateCodeUtility_WS_HUM.searchStateCode('21076');		
		Test.stopTest();
	}
}