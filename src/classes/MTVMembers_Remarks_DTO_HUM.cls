/*******************************************************************************************************************************************************
Apex Class Name  : MTVMembers_Remarks_DTO_HUM
Version          : 1.0
Created Date     : 04/05/2019
Function         : This class is wrapper class to store MTV Remarks data from service
Test Class       : MTVMembers_Remarks_DTO_T_HUM
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Suraj patil		                                06/1/2019                  Original Version
* Sagar MN 										   04/21/2020				  User Story 777415 : MF 09 - MTV Member Remarks � Add Entity Remark Type
* Kajal Namdev				              06/16/2021				  Added @AuraEnabled
************************************************************************************************************************************************/
public with sharing class MTVMembers_Remarks_DTO_HUM
{    
       @AuraEnabled public String sIdType {get; set;}
       @AuraEnabled public String sIdentifierId {get; set;}
       @AuraEnabled public String sSubIdentifier {get; set;}
       @AuraEnabled public String sRemarkCode {get; set;}
       @AuraEnabled public String sRemarkCategory {get; set;}
       @AuraEnabled public String sCreatedDate {get; set;}
       @AuraEnabled public String sRemarkText {get; set;}       
	@AuraEnabled public String sRemarkCount {get; set;}
}