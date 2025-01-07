/**********************************************************************************************************************************************************************
Apex Class Name : SearchResultsBasedOnPermission_LD_HUM
Version         : 1.0
Created On      : 03/16/2021

Test Class      : SearchResultsBasedOnPermission_LT_HUM

Modification Log: 
* Developer Name           Code Review                  Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Ritik Agarwal                                        03/16/2021                   Original Version
**********************************************************************************************************************************************************************/
public with sharing class SearchResultsBasedOnPermission_LD_HUM {
    
    /*
* Method name : getPermissionSets
* Description : This method will return Permissionsetassignment list basd on current login user 
* Return Type :  List<PermissionSetAssignment>
* Parameter   : NA
*/
    public static List<PermissionSetAssignment> getPermissionSets() 
    { 
        return [ SELECT Id, PermissionSet.Name,AssigneeId FROM PermissionSetAssignment WHERE AssigneeId = :Userinfo.getUserId()];
    }
    
    /*
* Method name : getMetadData
* Description : This method will return searchDisplayColumnHUM__mdt list 
* Return Type :  List<searchDisplayColumnHUM__mdt>
* Parameter   : string type
*/
    public static List<searchDisplayColumnHUM__mdt> getMetadData(String sScreenType,String sInput) 
    { 
        return [Select Field_API__c, Permission__c from searchDisplayColumnHUM__mdt where Type__c=:sInput AND Screen_Type__c =: sScreenType];
    }
}