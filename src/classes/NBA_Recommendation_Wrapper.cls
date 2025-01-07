/*******************************************************************************************************************************
Apex Class Name :  NBA_Recommendation_Wrapper.cls
Version: 1.0
Created On      : 10/13/2020
Function        : Class is wrapper classs used To display fields On screen flow

Modification Log : 
* Developer Name            Code Review           Date                       Description
*------------------------------------------------- -----------------------------------------------------------------------------
* Suraj Patil									10/13/2020					Original version	
* Jagadeesh Kureti                              06/06/2023                  US-3416423 T1PRJ0049998- MF [3174634 ] - Associate NVDE Dispositions to Interaction:  Classic
* Jonathan Dickinson			                10/02/2023				    User Story 5110926: T1PRJ0891339  2023 Arch Remediation - SF - TECH - Incident - INC2536276 - CRM - Unable to open Alerts for one user
*****************************************************************************************************************************************/

Public Class NBA_Recommendation_Wrapper implements Comparable {
    @AuraEnabled @InvocableVariable Public String Name = '';
    @AuraEnabled @InvocableVariable Public String Message = '';
    @AuraEnabled @InvocableVariable Public String Question_1 = '';
    @AuraEnabled @InvocableVariable Public String Question_2 = '';
    @AuraEnabled @InvocableVariable Public String Question_3 = '';
    @AuraEnabled @InvocableVariable Public String Question_4 = '';
    @AuraEnabled @InvocableVariable Public String Question_5 = '';
    @AuraEnabled @InvocableVariable Public String Question_6 = '';
    @AuraEnabled @InvocableVariable Public String Free_Text_Question_1 = '';
    @AuraEnabled @InvocableVariable Public String Free_Text_Question_2 = '';
    @AuraEnabled @InvocableVariable Public String Free_Text_Question_3 = '';
    @AuraEnabled @InvocableVariable Public String Free_Text_Question_4 = '';
    @AuraEnabled @InvocableVariable Public String Free_Text_Question_5 = '';
    @AuraEnabled @InvocableVariable Public String Free_Text_Question_6 = '';
    @AuraEnabled @InvocableVariable Public Decimal Priority;
    @AuraEnabled @InvocableVariable Public String AlertType = '';
    @AuraEnabled @InvocableVariable Public String Termable = '';
    @AuraEnabled @InvocableVariable Public String Link_Name_1 = '';
    @AuraEnabled @InvocableVariable Public String Link_Path_1 = '';
    @AuraEnabled @InvocableVariable Public String Link_Name_2 = '';
    @AuraEnabled @InvocableVariable Public String Link_Path_2 = '';
    @AuraEnabled @InvocableVariable Public String Link_Name_3 = '';
    @AuraEnabled @InvocableVariable Public String Link_Path_3 = '';
    @AuraEnabled @InvocableVariable Public String Link_Name_4 = '';
    @AuraEnabled @InvocableVariable Public String Link_Path_4 = '';
    @AuraEnabled @InvocableVariable Public String Link_Name_5 = '';
    @AuraEnabled @InvocableVariable Public String Link_Path_5 = '';
    @AuraEnabled @InvocableVariable Public String Link_Name_6 = '';
    @AuraEnabled @InvocableVariable Public String Link_Path_6 = '';
    @AuraEnabled @InvocableVariable Public String RecommendationDetailId = '';
    @AuraEnabled @InvocableVariable Public String RecommendationId = '';
    @AuraEnabled @InvocableVariable Public String RecommendationActionId = '';
    @AuraEnabled @InvocableVariable Public String TotalRecommendation = '';
    @AuraEnabled @InvocableVariable Public String RecommendationCount = '';
	@AuraEnabled @InvocableVariable Public String ExternalIdToSet = '';
    @AuraEnabled @InvocableVariable Public String interactionId = '';
    @AuraEnabled @InvocableVariable Public String discussionTopicsCounter = '';
    
    public Integer compareTo(Object compareTo) {
        NBA_Recommendation_Wrapper compareToRec = (NBA_Recommendation_Wrapper) compareTo;
        if (Priority == compareToRec.Priority) {return 0;}
        if (Priority > compareToRec.Priority) {return 1;}
        return -1;        
    }
}