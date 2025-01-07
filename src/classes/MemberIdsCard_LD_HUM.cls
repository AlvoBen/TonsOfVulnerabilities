/*******************************************************************************************************************************
Apex Class Name : MemberIdsCard_LC_HUM
Version         : 1.0
Created On      : 06/23/2021
Function        : This class is used for SOQL purpose on custom setting 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                        06/23/2021               Original Version
*    2.0           Ajay Chakradhar                                      10/07/2021               US : 2260341 - Account Management - Additional Member ID Fields 
*******************************************************************************************************************************************************************/

public with sharing class MemberIdsCard_LD_HUM {
   
    /*
    * Method name : queryMemberIdCardData
    * Description : This method will return the list of MemberPlan object
    * Parameter   : MemberPlan Id, PageName - that decide which field to show on which screen
    */
    public Static ObjectVSFieldNameMapping__mdt[] queryMemberIdCardData(String pageName){
        ObjectVSFieldNameMapping__mdt[] fldMappings = [SELECT MasterLabel,FieldAPIName__c,ObjectTypeName__c
                                                       FROM ObjectVSFieldNameMapping__mdt where Active__c=true and ScreenTypeName__c=:String.escapeSingleQuotes(pageName)];
        return fldMappings;
    }

    /*
    * Method name : queryAccountMemberIdCardData
    * Description : This method will return the list of ObjectVSFieldNameMapping__mdt object
    * Parameter   : NA
    */
    public Static ObjectVSFieldNameMapping__mdt[] queryAccountMemberIdCardData(){
        ObjectVSFieldNameMapping__mdt[] fldMappings = [SELECT MasterLabel,FieldAPIName__c,ObjectTypeName__c
                                                       FROM ObjectVSFieldNameMapping__mdt where Active__c=true and ObjectTypeName__c=:String.escapeSingleQuotes('Account')];
        return fldMappings;
    }
}