/*****************************************************************************************
Apex class Name    : RepeatCallerInformation_DTO_HUM
Version            : 1.0
Function           : DTO class used to display Repeat Caller Attributes
Created Date       : 06/18/2020
Test Class         : MemberIcons_CT_HUM

Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Santhi Mandava                                                        Original Version
 *   Amit Agarwal                                       06/25/2020         User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (RF)
 *   Ankima Srivastava                                  04/07/2020         Annotation added for LWC usage
 * --------------------------------------------------------------------------------------------------------------------------------
*/

public class RepeatCallerInformation_DTO_HUM {
    @AuraEnabled public Integer iInteractionsCreatedToday{get;set;}
    @AuraEnabled public Integer iInteractionsCreatedInPast14Days{get;set;}
    @AuraEnabled public Integer iOpenCases{get;set;}
    @AuraEnabled public String sTodaysInteractionsColor{get;set;}
    @AuraEnabled public String sPast14DaysInteractionsColor{get;set;}
    @AuraEnabled public String sOpenCasesColor{get;set;}
    @AuraEnabled public Boolean bIsTodaysIntsVisible{get;set;}
    @AuraEnabled public Boolean bIsPast14DaysIntsVisible{get;set;}
    @AuraEnabled public Boolean bIsOpenCasesVisible{get;set;}
    @AuraEnabled public Boolean bIsRepeatCallerVisible{get;set;}
    
    public RepeatCallerInformation_DTO_HUM() {
        iInteractionsCreatedToday = 0;
        iInteractionsCreatedInPast14Days = 0;
        iOpenCases = 0;
        sTodaysInteractionsColor = '';
        sPast14DaysInteractionsColor = '';
        sOpenCasesColor = '';
        bIsTodaysIntsVisible = false;
        bIsPast14DaysIntsVisible = false;
        bIsOpenCasesVisible = false;
        bIsRepeatCallerVisible = false;
    }
}