/*********************************************************************************************************************************
Apex Class Name  : InactiveLocationOnCRMRetailEvent_H_HUM 
Version          : 1.0
Created Date     : 07/09/2019
Function         : Helper class for InactiveLocationonOnCRMRetailEvent_G_HUM Trigger
**********************************************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------------------------------------
*  Santhosh Ganji                               07/09/2019                    Original Version.
*  Vinoth L										11/13/2020					  Trigger to prevent insert/update duplicate,and to prevent update/delete when interaction exist.
*  Abhishek Maurya								02/17/2021					  Production fix - Duplicate check
*  Luke P. Cecil								02/18/2021				 	  SonarQube security fix
*  Ananya Singh                 				07/12/2021         			  US-2360057 : Restrict Access to Virtual Locations
*  Vinoth L										05/31/2023					  US-4662486 : T1PRJ0154546 / SF / MF9 Storefront - Edit attendance field permissions on Event
**********************************************************************************************************************************/
public with sharing class InactiveLocationOnCRMRetailEvent_H_HUM {
    
    public class controllerException extends Exception {} 
    
    private static Map<String,Event> keyByEvent=new  Map<String,Event>();
    private static Set<String> setOfReason = new Set<String>();
    private static Set<String> setOfLocation = new Set<String>();                 
    private static Set<Date> setOfActivityDate = new Set<Date>();
    public static Boolean bypassAttendanceVldn = false;
    /**
*  Checks for Inactive Location on insert
*  @name checkForLocationOnInsert
*  @param lstRecords Holds the list of all Event records
*  @return void
*  @throws NA
*/
    public static void checkForLocationOnInsert(List<Event> lstRecords)
    {
        try{              
            Map<String,Event> mapEvent = new Map<String,Event>();
            for(Event oRecord : lstRecords)
            {
                mapEvent.put(oRecord.whatid,oRecord);
            }
            checkAndMarkErrorForInactiveLocation(mapEvent);            
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'InactiveLocationonOnCRMRetailEvent_G_HUM ', 'InactiveLocationonOnCRMRetailEvent_G_HUM ');
        }
    } 
    
    /**
*  Checks for Inactive Location on update
*  @name checkForLocationOnUpdate
*  @param List<Event> lstNewRecords- Holds the list of all updated event records
*  @param Map<Id,Event> mapOldRecords- Holds a map with old event record values
*  @return void
*  @throws NA
*/
    public static void checkForLocationOnUpdate(List<Event> lstNewRecords, Map<Id,Event> mapOldRecords)
    {
        try{
            Map<String,Event> mapEvent = new Map<String,Event>();
            
            for(Event oRecord : lstNewRecords)
            {
                if(String.isNotBlank(oRecord.whatid) && !oRecord.whatid.equals(mapOldRecords.get(oRecord.Id).whatid))
                {
                    mapEvent.put(oRecord.whatid,oRecord);
                }
            }
            checkAndMarkErrorForInactiveLocation(mapEvent);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'InactiveLocationonOnCRMRetailEvent_G_HUM ', 'InactiveLocationonOnCRMRetailEvent_G_HUM ');
        }
    }
    /**
    *  Checks for access to update
    *  @name checkForUpdate
    *  @param List<Event> lstNewRecords- Holds the list of all updated event records
    *  @param List<Event> lstOldRecords- Holds a list of all old event records
    *  @return void
    *  @throws NA
    */
    public static void checkForUpdate(List<Event> lstNewRecords,Map<Id,Event> evtOldMap)
    {       
        try{ 
            for(Event oNewRecord : lstNewRecords)
            {
                Event oldEvt = evtOldMap.get(oNewRecord.Id);                    
                if((oldEvt.EndDateTime!=oNewRecord.EndDateTime ||  oldEvt.StartDateTime!=oNewRecord.StartDateTime
                    || oldEvt.IsAllDayEvent!= oNewRecord.IsAllDayEvent || oldEvt.Reason__c!=oNewRecord.Reason__c 
                    || oldEvt.WhatId!=oNewRecord.WhatId || oldEvt.IsRecurrence!=oNewRecord.IsRecurrence
                    || oldEvt.OwnerId!=oNewRecord.OwnerId || oldEvt.Subject!=oNewRecord.Subject) 
                   && (oldEvt.Total_Attendance_Count__c>0 || oldEvt.Scheduled_Total_Attendance__c>0))  
                {                 
                    oNewRecord.adderror(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR_UPDATE_INTERACTION_EXIST);    
                }                 
            } 
        }        
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'InactiveLocationonOnCRMRetailEvent_G_HUM ', 'InactiveLocationonOnCRMRetailEvent_G_HUM ');
        }
    }
    /**
    *  Restricts the users from entering the attendance fields
    *  @name checkEventAttendanceEntry
    *  @param List<Event> listOfEvents- Holds the list of all inserted or updated event records and Map<Id,Event> oldEventMapById for Events queried from Database
    *  @return void
    *  @throws NA
    */
    public static void checkEventAttendanceEntry(List<Event> listOfEvents, Map<Id,Event> evtOldMap, Boolean isInsert)
    {             
        Event oldEvt = new Event();
        try{  
            if(HUMUtilityHelper.isCRMFunctionalityON('4662486'))
            {
                for(Event eveRef:listOfEvents)
                {
                    oldEvt = (!isInsert) ? evtOldMap.get(eveRef.Id) : null;  
                    if(!FeatureManagement.checkPermission(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ADMIN_CUSTOM_PERMISSION) && ((isInsert && (eveRef.Known_Attendee_Count__c > 0 || eveRef.Virtual_Attendee_Count__c > 0 || eveRef.Scheduled_Onsite_Attendee_Count__c > 0 || eveRef.Scheduled_Virtual_Attendee_Count__c > 0)) || (!isInsert && !bypassAttendanceVldn && oldEvt != NULL && (oldEvt.Scheduled_Onsite_Attendee_Count__c != eveRef.Scheduled_Onsite_Attendee_Count__c || oldEvt.Scheduled_Virtual_Attendee_Count__c != eveRef.Scheduled_Virtual_Attendee_Count__c || oldEvt.Virtual_Attendee_Count__c != eveRef.Virtual_Attendee_Count__c || oldEvt.Known_Attendee_Count__c != eveRef.Known_Attendee_Count__c))))
                    {
                        eveRef.adderror(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_EVT_ATTND_UPDATE_ERROR_MSG);    
                    }
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'InactiveLocationonOnCRMRetailEvent_H_HUM ', 'checkEventAttendanceEntry ');
        }
    }
    /**
    *  Checks for duplicate records while a new Event is created or existing event is updated
    *  @name eventDuplicateCheck
    *  @param List<Event> listOfEvents- Holds the list of all inserted or updated event records and Map<Id,Event> oldEventMapById for Events queried from Database
    *  @return void
    *  @throws NA
    */
    public static void eventDuplicateCheck(List<Event> listOfEvents,Map<Id,Event> mapOfOldEventById)
    {    
        List<Event> lstOfQueriedEvent=new List<Event>();
        
        try{            
            for(Event eveRef:listOfEvents)
            {                      
                if(mapOfOldEventById!=NULL){
                    Event oldEvtRecrod = mapOfOldEventById.get(eveRef.Id);                    
                    if(eveRef.EndDateTime!=oldEvtRecrod.EndDateTime ||  eveRef.StartDateTime!=oldEvtRecrod.StartDateTime
                       || eveRef.IsAllDayEvent!= oldEvtRecrod.IsAllDayEvent || eveRef.Reason__c!=oldEvtRecrod.Reason__c 
                       || eveRef.WhatId!=oldEvtRecrod.WhatId || eveRef.IsRecurrence!=oldEvtRecrod.IsRecurrence
                      ){
                          createDataforDuplicateCheck(eveRef);
                      }
                }
                else{
                    createDataforDuplicateCheck(eveRef);
                }
            }
            if(keyByEvent.size()>0){
                lstOfQueriedEvent = [select ActivityDate,IsAllDayEvent,Reason__c,WhatId,StartDateTime,EndDateTime
                                     from Event where Reason__c in :setOfReason
                                     AND WhatId in : setOfLocation
                                     AND ActivityDate in : setOfActivityDate ORDER BY createddate DESC];   
            }        
            if(lstOfQueriedEvent.size()>0){
                for(Event eveRef : lstOfQueriedEvent){
                    String key=eveRef.Reason__c+'@'+eveRef.WhatId+'@'+eveRef.ActivityDate;
                    if(keyByEvent.containsKey(key) && (keyByEvent.get(key).IsAllDayEvent || eveRef.IsAllDayEvent)){
                        keyByEvent.get(key).addError(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR_INSERT_DUPLICATE);
                    }
                    
                    else if(keyByEvent.containsKey(key) && keyByEvent.get(key).StartDateTime == eveRef.StartDateTime && keyByEvent.get(key).EndDateTime == eveRef.EndDateTime){
                        keyByEvent.get(key).addError(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR_INSERT_DUPLICATE);
                    }
                }
            }              
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'InactiveLocationonOnCRMRetailEvent_G_HUM ', 'InactiveLocationonOnCRMRetailEvent_G_HUM ');
        }
        
	}
    /**
    * Method used to populate class level variables for eventDuplicateCheck method
    * @Name createDataforDuplicateCheck
    * @Param  single event record 
    * */
    
    private static void createDataforDuplicateCheck(Event eveRef){
        setOfReason.add(eveRef.Reason__c);            
        setOfLocation.add(eveRef.WhatId);            
        setOfActivityDate.add(eveRef.ActivityDate);  
        String key=eveRef.Reason__c+'@'+eveRef.WhatId+'@'+eveRef.ActivityDate;
        keyByEvent.put(key,eveRef);
    }
    /**
    *  Checks for Interactions before performing Delete Operation
    *  @name checkForDelete
    *  @param List<Event> lstNewRecords- Holds the list of all event records to delete
    *  @return void
    *  @throws NA
    */
    public static void checkForDelete(List<Event> events)
    {
        try{
            for(Event del: events)
            {
                if(del.Total_Attendance_Count__c>0 || del.Scheduled_Total_Attendance__c>0)
                {                
                    del.adderror(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR_DELETE_INTERACTION_EXIST);  
                }            
            }             
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'InactiveLocationonOnCRMRetailEvent_G_HUM ', 'InactiveLocationonOnCRMRetailEvent_G_HUM ');
        }
    }
    /**
*  Queries the list of active Locations and events, if any event record created with the Inactiev location an error will be thrown
*  @name checkAndMarkErrorForInactiveLocation
*  @param Map<String,Event> mapEvent
*  @return void
*  @throws NA
*/
    private static void checkAndMarkErrorForInactiveLocation(Map<String,Event> mapEvent)
    {
        List<Storefront_Location__c> location = [SELECT Id, Name FROM Storefront_Location__c WHERE Id IN: mapEvent.keySet() AND Inactive_indicator__c = True];
        for(Storefront_Location__c loc :location){
            for(Event oRecords : [SELECT Id, whatid FROM Event WHERE whatid =: loc.Id])
            {     
                mapEvent.get(oRecords.whatid).addError(Label.INACTIVE_LOCATION_CRMRETAIL_HUM);
            }
        }
        
        if(Test.isRunningTest())
        {
            throw new controllerException();
        }
    } 
     
    /**
    *  Checks whether the user has assigned to the CRM_Retail_Virtual_location_Access permission and the event location must not be virtual otherwise throw error
    *  @name virtualLocationChecks
    *  @param List<Event> lstNewEvent
    *  @return void
    *  @throws NA
    */
    public static void virtualLocationChecks(List<Event> lstNewEvent,Map<Id,Event> mapOfOldEventById)
    {
        try
        {   
            Boolean isVirtualLocEligible = FeatureManagement.checkPermission(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VIRTUAL_LOC_ELIGIBILITY_CUSTOM_PERM_NAME);                    
            if(!isVirtualLocEligible)
            {  
                Set<id> setLocIds =  new Set<id>();
                Set<id> setVirtualLocIds;
        
                if(lstNewEvent != null)
                {
                    for(Event objEvent : lstNewEvent)
                    {
                        if(objEvent.WhatId != null)
                        {   
                            setLocIds.add(objEvent.WhatId);
                            
                            if(mapOfOldEventById != null && !mapOfOldEventById.isEmpty() && mapOfOldEventById.get(objEvent.Id).WhatId != objEvent.WhatId)
                            {
                                setLocIds.add(mapOfOldEventById.get(objEvent.Id).WhatId);
                            }   
                        }
                    }
                }
                
                if(setLocIds != null && !setLocIds.isEmpty())
                {
                    setVirtualLocIds = GenericHelper_CRMRetail_H_HUM.getVirtualLocations(setLocIds);
                }
                
                if(setVirtualLocIds != null && !setVirtualLocIds.isEmpty())
                {
                    for (Event evt : lstNewEvent)
                    {
                        if (setVirtualLocIds.contains(evt.whatId) || setVirtualLocIds.contains(mapOfOldEventById.get(evt.Id).WhatId))
                        {
                            evt.addError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VIRTUAL_LOC_INELIGIBILITY_MSG);
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'InactiveLocationOnCRMRetailEvent_H_HUM ', 'virtualLocationChecks ');
        }
    }
}