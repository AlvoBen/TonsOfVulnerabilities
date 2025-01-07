/*******************************************************************************************************************************
Apex Class Name : NBA_RecommendationHistory_C_HUM.cls
Version         : 1.0
Created On      : 11/05/2020
Function        : Class is used as base class to display recommendation on screen flow.
Test Class      : NBA_RecommendationHistory_T_HUM
Modification Log:
* Developer Name            Code Review           Date                       Description
*------------------------------------------------- -----------------------------------------------------------------------------
* Sayali Nemade									11/05/2020					Original Version - REQ - 1041220 -- PR00090631 - 
MF 9 - MVP Ability to modify and store alerts in CRM Service	
* Kinal Mangukiya                               08/18/2023                  US 4812228 - T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – NBA
*****************************************************************************************************************************************/

public with Sharing class NBA_RecommendationHistory_C_HUM {

    @AuraEnabled(cacheable=true)
    public static List<Recommendation_History__c> lstOfRecommHistory(String sRecommID) {
        List<Recommendation_History__c> recomHistory = [
            SELECT Id, Name, Recommendation__c, Old_Value__c, New_Value__c, CreatedBy.Name, CreatedDate, Recommendation__r.Name, CreatedById
            FROM Recommendation_History__c
            WHERE Recommendation__c = :sRecommID WITH USER_MODE
            ORDER BY CreatedDate DESC 
        ];
        
        return recomHistory;
    }

    @AuraEnabled(cacheable=true)
    public static List<Recommendation> lstOfRecommendation(String sRecommID) {
        List<Recommendation> lstOfRecom = [
            SELECT Id, Name
            FROM Recommendation
            WHERE Id = :sRecommID WITH USER_MODE
        ];
        
        return lstOfRecom;
    }
}