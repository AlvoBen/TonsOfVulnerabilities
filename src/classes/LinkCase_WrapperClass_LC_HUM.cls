/*******************************************************************************************************************************************************
Apex Class Name  : LinkCase_WrapperClass_LC_HUM
Version          : 1.0
Created Date     : 12/21/2021
Function         : This class is wrapper class to store data for Case Accordion in Lightning.
Test Class       : LinkCase_WrapperClass_LT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Swetha Thuti                                     12/21/2021                 Original Version
***********************************************************************************************************************************************/
public with sharing class LinkCase_WrapperClass_LC_HUM {
        @AuraEnabled public string sCaseId{get;set;}
    	@AuraEnabled public string sCaseNumber{get;set;}
    	@AuraEnabled public string sInfo{get;set;}
    	@AuraEnabled public string sWorkQueue{get;set;}
}