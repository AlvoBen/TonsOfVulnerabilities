/*******************************************************************************************************************************
Helper Class Name: PurchaserPlanTriggerHandler_LH_HUM
Version          : 1.0
Created On       : 11/09/2020
Function         : Helper Class for Trigger execution on PurchaserPlan  object.
Test Class       : PurchaserPlanTrigger_LH_HUM

Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Anjani Vaddadi                                 11/09/2020                  Original Version
*/
public with sharing class PurchaserPlanTriggerHandler_LH_HUM {
    /*
* Method Name : updateSecurityFieldsBeforeInsert
* Description : Used to Update the security fields on PurchaserPlan object "before Insert"
* Return type : void
* Paramater   : Trigger.New
*/
    
    public static void insertSecurityFieldsBeforeInsert(List<PurchaserPlan> lstTriggerNewData){
        try{
            List<PurchaserPlan> lstPurchaserPlanToInsert = new List<PurchaserPlan>();
            Map<Id,PurchaserPlan> mapPurchaserPlan = new Map<Id,PurchaserPlan>();            
            for(PurchaserPlan oPplan : lstTriggerNewData)
                mapPurchaserPlan.put(oPplan.Policy__c,oPplan);  
            Map<Id, Policy__c> mapPolicyAccount = new Map<Id, Policy__c>([SELECT Id, Group_Name__r.General_Account__c, Group_Name__r.Home_Office_Account__c FROM Policy__c WHERE Id In :mapPurchaserPlan.keySet()]);       
            for(PurchaserPlan oPurchaserPlan : lstTriggerNewData){
                if(mapPolicyAccount.get(oPurchaserPlan.Policy__c)!=NULL){
                    oPurchaserPlan.PayerID = mapPolicyAccount.get(oPurchaserPlan.Policy__c).Group_Name__c;
                    oPurchaserPlan.Home_Office_Account__c = mapPolicyAccount.get(oPurchaserPlan.Policy__c).Group_Name__r.Home_Office_Account__c;
                    oPurchaserPlan.General_Account__c = mapPolicyAccount.get(oPurchaserPlan.Policy__c).Group_Name__r.General_Account__c;
                    lstPurchaserPlanToInsert.add(oPurchaserPlan);
                }
            }        
        }
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'PurchaserPlanTrigger_H_HUM', 'PurchaserPlanTrigger_H_HUM ');
        }
    }
    /*
* Method Name : updateSecurityFieldsBeforeUpdate
* Description : Used to Update the security fields on PurchaserPlan object "before Update"
* Return type : void
* Paramater   : Trigger.New, Trigger.oldMap
*/
    
    public static void updateSecurityFieldsBeforeUpdate(List<PurchaserPlan> lstTriggerNew, Map<Id,PurchaserPlan> mapTriggerOldMap){
        try{
            Map<Id,PurchaserPlan> mapPurchaser = new Map<Id,PurchaserPlan>();
            for(PurchaserPlan oPurchaserPlan:lstTriggerNew){
                if(oPurchaserPlan.Policy__c != mapTriggerOldMap.get(oPurchaserPlan.Id).Policy__c){
                    mapPurchaser.put(oPurchaserPlan.Policy__c,oPurchaserPlan);                
                }
            } 
            if(mapPurchaser!=null || !mapPurchaser.isEmpty()){
                Map<Id,Policy__c> mapPolicy = new Map<Id,Policy__c>([select id, Group_Name__c, Group_Name__r.General_Account__c, Group_Name__r.Home_Office_Account__c from Policy__c where Id in :MapPurchaser.keySet()]);
                for(PurchaserPlan oPurchaserPlan : lstTriggerNew){
                    if(mapPolicy.get(oPurchaserPlan.Policy__c)!=NULL){
                        oPurchaserPlan.PayerID = mapPolicy.get(oPurchaserPlan.Policy__c).Group_Name__c;
                        oPurchaserPlan.Home_Office_Account__c = mapPolicy.get(oPurchaserPlan.Policy__c).Group_Name__r.Home_Office_Account__c;
                        oPurchaserPlan.General_Account__c = mapPolicy.get(oPurchaserPlan.Policy__c).Group_Name__r.General_Account__c;                                        
                    }
                }  
            }
        }
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'PurchaserPlanTrigger_H_HUM', 'PurchaserPlanTrigger_H_HUM '); 
        }
    }
    
    
}