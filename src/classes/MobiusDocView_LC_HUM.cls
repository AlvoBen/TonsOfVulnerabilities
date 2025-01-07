/**************************************************************************** 
Apex Class Name  : MobiusDocView_LC_HUM
Version          : 1.0  
Created Date     : Sep 08, 2022
Function         : This is Controller for mobiusDocViewHum 
Test Class       : MobiusDocView_LT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Anuradha Gajbhe                              09/08/2022                  Original Version
***************************************************************************************************/

public with sharing class MobiusDocView_LC_HUM{
	
    public static string sDocKey{get; set;}
	public static string sMobiusUrl{get; set;}
	public static string sAppKey{get; set;}
    
    @AuraEnabled
	public static object initiateViewRequest(string DocumentKey){

		sDocKey = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(DocumentKey);
		HUM_Webservice_Callout__c oServiceCalloutMobius = HUM_Webservice_Callout__c.getValues('MobiusService_GetMobiusDocument');  
		sMobiusUrl = oServiceCalloutMobius.End_Point_URL__c;
		sAppKey = '238F6F83-B8B4-11CF-8771-00A024541EE3';
        object ViewRequest = '{: sDocKey: ' + sDocKey + ': sMobiusUrl: ' + sMobiusUrl + ': sAppKey: ' + sAppKey + ': }';
        return ViewRequest;
	}


}