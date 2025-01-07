/*******************************************************************************************************************************
Apex Class Name     : Storefront_Interaction_G_HUM 
Version             : 1.0
Created Date        : May 22 2020
Function            : This serves as Trigger for Storefront Interaction Object.

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj                                      05/22/2020                 Initial Version
* Melkisan Selvaraj                                      06/23/2020                 1180532 - Before Insert changes to track Attendance
* Akshay Pai                                      		 07/15/2021                 US-2360057 : Restrict Access to Virtual Locations
* Vivek Sharma                                           02/11/2022                 User Story 2940968: T1PRJ0154546 MF9 Storefront Home Page: Missing 'Event' Interactions
*******************************************************************************************************************************/
trigger Storefront_Interaction_G_HUM on Storefront_Interaction__c (before delete, after delete, before insert, after insert, before update) 
{    
    HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('Storefront_Interaction__c');    
    if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c == true )
    {               
        if(Trigger.isBefore && Trigger.isDelete)
        {            
            Storefront_Interaction_H_HUM.processBeforeDelete(Trigger.Old);
            Storefront_Interaction_H_HUM.virtualLocationChecks(Trigger.old,Trigger.newMap);
        }
        if(Trigger.isAfter && Trigger.isDelete)
        {            
            Storefront_Interaction_H_HUM.processAfterDelete(Trigger.Old);
        }
        if(Trigger.isBefore && Trigger.isInsert)
        {
            Storefront_Interaction_H_HUM.processBeforeInsert(Trigger.New);             
            Storefront_Interaction_H_HUM.virtualLocationChecks(Trigger.New,Trigger.oldMap);            
        }
        if(Trigger.isAfter && Trigger.isInsert)
        {
            Storefront_Interaction_H_HUM.processAfterInsert(Trigger.New);
        }
        if(Trigger.isBefore && Trigger.isUpdate)
        {
            Storefront_Interaction_H_HUM.processBeforeUpdate(Trigger.New,Trigger.oldMap);                                           
        } 
    }
}