/*******************************************************************************************************************************
Trigger Name    : InactiveLocationonOnCRMRetailEvent_G_HUM 
Version         : 1.0
Created On      : 07/09/2019 
Function        : Trigger execution logic to find Inactive location on Relatedto field on event.

Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*  Santhosh Ganji                               07/09/2019               To avoid duplicate user for Source version.
*  Vinoth L                                     11/13/2020               To prevent duplicate insert and update,delete/update prevention when Interaction exist
*  Abhishek Maurya                              02/17/2021               Production fix - Duplicate check
*  Luke P. Cecil                                02/18/2021               SonarQube security fix
*  Akshay Pai                                   07/15/2021               US-2360057 : Restrict Access to Virtual Locations
*  Vinoth L										05/31/2023				 Edit attendance field permissions on Event
*******************************************************************************************************************************/
trigger InactiveLocationonOnCRMRetailEvent_G_HUM on Event (before insert,before update,before delete) {            
    if (Trigger.isBefore)
    {
        if(Trigger.isInsert)
        {       
            InactiveLocationOnCRMRetailEvent_H_HUM.checkEventAttendanceEntry(Trigger.new,Trigger.oldMap,true);                 	   
            InactiveLocationOnCRMRetailEvent_H_HUM.eventDuplicateCheck(Trigger.new,Trigger.oldMap);
            InactiveLocationOnCRMRetailEvent_H_HUM.checkForLocationOnInsert(Trigger.New);
            InactiveLocationOnCRMRetailEvent_H_HUM.virtualLocationChecks(Trigger.New,Trigger.oldMap);
        }
        else if(Trigger.isUpdate)
        {   
            InactiveLocationOnCRMRetailEvent_H_HUM.checkEventAttendanceEntry(Trigger.new,Trigger.oldMap,false);                 	   
            InactiveLocationOnCRMRetailEvent_H_HUM.eventDuplicateCheck(Trigger.new, Trigger.oldMap);
            InactiveLocationOnCRMRetailEvent_H_HUM.checkForUpdate(Trigger.New,Trigger.oldMap);                  
            InactiveLocationOnCRMRetailEvent_H_HUM.checkForLocationOnUpdate(Trigger.New,Trigger.oldMap);  
            InactiveLocationOnCRMRetailEvent_H_HUM.virtualLocationChecks(Trigger.new,Trigger.oldMap);         
        }
        else if(Trigger.isDelete)
        {
            InactiveLocationOnCRMRetailEvent_H_HUM.checkForDelete(Trigger.old); 
            InactiveLocationOnCRMRetailEvent_H_HUM.virtualLocationChecks(Trigger.old,Trigger.newMap);           
        } 
    }
}