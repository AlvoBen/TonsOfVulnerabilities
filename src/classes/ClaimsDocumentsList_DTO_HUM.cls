/*******************************************************************************************************************************************************
Apex Class Name  : ClaimsDocumentsList_DTO_HUM
Version          : 1.0
Created Date     : 04/23/2016
Function         : This class is wrapper class to store Claim Statements (documents list) data received from service
Test Class       : ClaimsDocumentsListDTO_T_HUM
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Muralidhar Kollu          27974                 05/15/2016                 Original Version
* Muralidhar Kollu          288893                10/18/2018           REQ - 357260 - Provider Claim Statements section code
* Muralidhar Kollu          355971                05/17/2019                    REQ - 397216
* Anjani Vaddadi                                  02/25/2020                   REQ - 764250
************************************************************************************************************************************************/
public with sharing class ClaimsDocumentsList_DTO_HUM
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