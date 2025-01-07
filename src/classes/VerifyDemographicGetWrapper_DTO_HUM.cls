/*******************************************************************************************************************************
Apex Class Name : VerifyDemographicGetWrapper_DTO_HUM
Version         : 1.0
Created On      : 07/29/2020
Function        : Used to display Verify Demographics section on Account Page
Test Class      : VerifyDemographicGetWrapper_DTO_T_HUM  

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                      07/29/2020                  REQ - 1065536 : Original Version
* Ankima Srivastava							     03/18/2021			   Verify Demographics
* Vishal Shinde                                        07/28/2023                  US-4833019-Mail Order Management - Pharmacy - “Accounts” tab – Verify Demographics - Lightning    
*******************************************************************************************************************************/  
public with sharing class VerifyDemographicGetWrapper_DTO_HUM {
     @AuraEnabled public String sLastVerifiedBy {get;set;}
     @AuraEnabled public String sLastVerifiedOn {get;set;}
     @AuraEnabled public integer iDaysSinceLastVerified {get;set;}
     @AuraEnabled public String networkUserId{get;set;}
}