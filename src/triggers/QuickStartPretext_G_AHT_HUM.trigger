/*******************************************************************************************************************************
Trigger Name    : QuickStartPretext_G_AHT_HUM
Version         : 1.0
Created On      : 01/24/2017
Function        : Trigger execution sequence logic for CTCI_Junction__c  object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Subhamay Mandal                                 01/24/2018                 original version for REQ - 350392                                                                             
*******************************************************************************************************************************/
trigger QuickStartPretext_G_AHT_HUM on Quick_Start_Pretext__c (before insert, before update) {
    if(Trigger.isBefore) {
    	if(Trigger.isInsert) {
            QuickStartPretext_H_AHT_HUM.duplicatePretextCI(Trigger.New);
        } else if(Trigger.isUpdate) {
    		QuickStartPretext_H_AHT_HUM.duplicatePretextCI(Trigger.New, Trigger.Old);
        }
	}
}