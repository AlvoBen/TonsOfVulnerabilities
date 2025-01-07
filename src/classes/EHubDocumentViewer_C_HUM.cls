/**************************************************************************** 
Apex Class Name  : EHubDocumentViewer_C_HUM
Version          : 1.0  
Created Date     : Sep 27, 2018
Function         : This is Controller for eHubDocumentBytes_C_HUM_VF_HUM 
Test Class       : eHubDocumentBytes_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Srinivas Damera         293796                 09/27/2018                    Original Version
***************************************************************************************************/

public with sharing class EHubDocumentViewer_C_HUM{

public EHubDocumentViewer_C_HUM(){

    sDocId = ApexPages.currentPage().getParameters().get('DocId');
    sVersionId= ApexPages.currentPage().getParameters().get('VId');
    HUM_Webservice_Callout__c oServiceCalloutEhub = HUM_Webservice_Callout__c.getValues('EhubService_GetDocument');  
    sEhubUrl = oServiceCalloutEhub.End_Point_URL__c;
    sAppKey = GLOBAL_CONSTANT_HUM.EHUB_GETDOCUMENT_APPLICATIONKEY;
   }

public string sDocId{get; set;}
public string sVersionId{get; set;}
public string sEhubUrl{get; set;}
public string sAppKey{get; set;}

}