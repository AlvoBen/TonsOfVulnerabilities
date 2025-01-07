/*******************************************************************************************************************************
Trigger Name    : PurchaserPlan_LG_HUM
Version         : 1.0
Created On      : 11/09/2020
Function        : Trigger execution sequence logic for PurchaserPlan  object.
Handler Class   : PurchaserPlanTriggerHandler_LH_HUM
Test Class      : PurchaserPlanTrigger_LT_HUM                 
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Anjani Vaddadi                                 11/09/2020                  Original Version
*/ 
trigger PurchaserPlan_LG_HUM on PurchaserPlan (before insert, before update) {
     HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('PurchaserPlan');
    if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c == true ){
    if(Trigger.isBefore && Trigger.isInsert && CRMFunctionalityEnabler_H_HUM.Checkflagvalue('PurchaserPlanBeforeInsertTriggerSwitch')){
        PurchaserPlanTriggerHandler_LH_HUM.insertSecurityFieldsBeforeInsert(Trigger.new);
    }
    if(Trigger.isBefore && Trigger.isUpdate && CRMFunctionalityEnabler_H_HUM.Checkflagvalue('PurchaserPlanBeforeUpdateTriggerSwitch')){
        PurchaserPlanTriggerHandler_LH_HUM.updateSecurityFieldsBeforeUpdate(Trigger.new,Trigger.oldMap);
    }
    }
}