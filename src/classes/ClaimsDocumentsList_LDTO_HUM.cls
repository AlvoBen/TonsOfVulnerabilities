/*******************************************************************************************************************************************************
Apex Class Name  : ClaimsDocumentsList_LDTO_HUM
Version          : 1.0
Created Date     : 04/23/2016
Function         : This class is wrapper class to store Claim Statements (documents list) data received from service
Test Class       : ClaimsDocumentsListDTO_LT_HUM
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
***************************************************************************************************/
public with sharing class ClaimsDocumentsList_LDTO_HUM
{
        public string sDocumentKey {get; set;}
        public string sPrintedDate {get; set;}
        public string sTopicName {get; set;}      
        public string sStatementBeginDate {get; set;} 
        public string sStatementEndDate {get; set;}
        public string sStatementType {get; set;}
        public string sPersonId {get; set;}
        public string sRemitId {get; set;}
        public string sStatementDescription {get; set;}
        public string MemberGenKey{get; set;}
}