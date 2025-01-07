/*******************************************************************************************************************************
Trigger Name    : CaseTransferAssist_G_HUM
Version         : 1.0
Created On      : 06/16/2020
Function        : Trigger execution sequence logic for Case_Transfer_Assist__c  object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sunil Kumar Reddy Y                          06/16/2020                original version for REQ - 1200920                                                                             
*******************************************************************************************************************************/
trigger CaseTransferAssist_G_HUM  on Case_Transfer_Assist__c (before insert,before update) {
    if(Trigger.IsBefore && Trigger.IsInsert){
        CaseTransferAssist_H_HUM.preventDuplicates(Trigger.New);
    }

    if(Trigger.IsBefore && Trigger.IsUpdate){
        CaseTransferAssist_H_HUM.preventDuplicates(Trigger.New,Trigger.oldMap);
    }
}