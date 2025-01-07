/*******************************************************************************************************************************************************
Apex Class Name  : ClaimsGetCodeEditDetails_DTO_HUM
Version          : 1.0
Created Date     : 03/07/2016
Function         : This class is wrapper class to store data from service
Test Class       : ClaimsGetCodeEditDetailsDTO_T_HUM
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Prasanthi Kandula          27974                 05/03/2016                 Original Version
* Prasanthi Kandula          28667                 05/30/2016                 Added sContinuationState
************************************************************************************************************************************************/

public with sharing class ClaimsGetCodeEditDetails_LDTO_HUM
{
    @AuraEnabled public String sCodeEditInd {get; set;} 
    @AuraEnabled public String sExCode {get; set;}
    @AuraEnabled public String sRejectedProcedureCode {get;set;}
    @AuraEnabled public String sRationaleDesc {get; set;}  
    @AuraEnabled public String sClaimNbr {get; set;}
    @AuraEnabled public String sCurrentQueue {get; set;}
    @AuraEnabled public String sUrl {get; set;}
    @AuraEnabled public String sContinuationState {get;set;}
}