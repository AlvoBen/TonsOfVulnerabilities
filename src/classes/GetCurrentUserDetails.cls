/*****************************************************************************************
Apex class Name    : GetCurrentUserDetails
Version            : 1.0
Function           : class for et current loggedin user details
Created Date       : 05/04/2021
Test Class         : GetCurrentUserDetails_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Ritik Agarwal									         05/04/2021				  initial version
* Aishwarya Pawar                                      05/12/2022                REQ - 3285223 Alert Configuration on Plan Member Page

**********************************************************************************************************************************************************/
public with sharing class GetCurrentUserDetails {
    public GetCurrentUserDetails() {}

    @AuraEnabled
     public static User getUserDetails(){
         return [select id,Name,Profile.Name,Current_Queue__c from User where id=:UserInfo.getUserId() LIMIT 1];
     }
}