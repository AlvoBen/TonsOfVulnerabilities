/**************************************************************************** 
Apex Class Name  : MobiusDocView_C_HUM
Version          : 1.0  
Created Date     : Apr 20, 2018
Function         : This is Controller for MobiusDocView_VF_HUM 
Test Class       : MobiusDocView_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Muralidhar Kollu                              04/20/2018                  Original Version
* Suraj patil									06/14/2018					REQ - 2233176 T1PRJ0002735 (PR00094226) - SF - Mobius, MTV - CR AEP readiness - High Security Issues
***************************************************************************************************/

public with sharing class MobiusDocView_C_HUM{

	public MobiusDocView_C_HUM(){

		sDocKey = getHTMLEncodedPageParameter('DocumentKey');
		HUM_Webservice_Callout__c oServiceCalloutMobius = HUM_Webservice_Callout__c.getValues('MobiusService_GetMobiusDocument');  
		sMobiusUrl = oServiceCalloutMobius.End_Point_URL__c;
		sAppKey = GLOBAL_CONSTANT_HUM.MOBIUS_GETDOCUMENT_APPLICATIONKEY;
	}

	public string sDocKey{get; set;}
	public string sMobiusUrl{get; set;}
	public string sAppKey{get; set;}

	/*
    * Method Name   :  getHTMLEncodedPageParameter
    * Description   :  This method is used to get Encoded Page Parameter.
    * Return Type   :  String 
    * Parameters    :  String
    */
    private String getHTMLEncodedPageParameter(String param) {
        String paramValue = ApexPages.currentPage().getParameters().get(param);
        return ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(paramValue);
    }
}