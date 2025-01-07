/*******************************************************************************************************************************
Apex Class Name : TimelineCaseComments_DTO_HUM.cls
Version         : 1.0
Function        : DTO Class for Log Codes and visibility

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar                                         05/25/2022                 Original Version          	
*********************************************************************************************************************************/

public with sharing class TimelineCaseComments_DTO_HUM{
    @AuraEnabled public String LogNoteCode = '';
    @AuraEnabled public String LogNoteDate = ''; 
    @AuraEnabled public String LogNoteKey = '';  
    @AuraEnabled public String LogNoteMessage = '';  
    @AuraEnabled public String LogNoteUser = '';
    @AuraEnabled public String LogNoteLastModified = ''; 
    @AuraEnabled public String caseCommentId='';
    @AuraEnabled public Boolean isDropDownVisible= false;
    @AuraEnabled public Boolean canEdit= false;
    @AuraEnabled public Boolean canDelete= false;
}