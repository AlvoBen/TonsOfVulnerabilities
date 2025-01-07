/*******************************************************************************************************************************
Apex Class Name     : Storefront_Interaction_H_HUM 
Version             : 1.0
Created Date        : May 22 2020
Function            : This serves as Handler for Storefront Interaction Trigger.

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj                                      05/22/2020                 Initial version
* Luke P. Cecil                                          08/19/2020                 User Story 1243103: PR00091869 - MF 11 - CRM Storefront Virtual Visitors: New Virtual Non-Member Visitor
* Abhishek Maurya                                        02/17/2021                 Production fix - Duplicate check
* Ananya Singh                                           07/12/2021                 US-2360057 CRM Storefront VNC: Restrict Access to Virtual Locations
* Ekta Bist                                              12/13/2021                 User Story 2792824: T1PRJ0001894 MF7 Storefront Engagement: Go365 Eligible Interaction Reason Hover
* Ekta Bist                                              12/22/2021                 User Story 2789307: T1PRJ0001894 MF7 Storefront Engagement: Account Hover
* Vivek Sharma											 01/13/2022					User Story 2792815: T1PRJ0154546 MF9 Storefront Systematically Remove Notification & SDOH Records When User Deletes an Interaction
* Vivek Sharma                                           02/11/2022                 User Story 2940968: T1PRJ0154546 MF9 Storefront Home Page: Missing 'Event' Interactions
* Vivek Sharma                                           04/25/2022                 User Story 3316820: T1PRJ0154546 / SF / MF9 Storefront - SNP: Change Notifications Section to Custom
* Vivek Sharma                                           06/28/2022                 User Story 3450197: T1PRJ0154546 / SF / MF9 Storefront Modernization (Business Admin/Super Admin) - Home (Right Side)
* Vivek Sharma                                           07/18/2022                 User Story 3450175: T1PRJ0154546 / SF / MF9 Storefront Modernization (Business Admin/Super Admin) - Interactions/Events (Left)
* Vinoth L                                           	 12/13/2022                 User Story 4046005: T1PRJ0154546 / SF / MF11 Storefront Interaction Duplicate Logic Update
* Vinoth L												 05/31/2023				    User Story 4662486: T1PRJ0154546 / SF / MF9 Storefront - Edit attendance field permissions on Event
* Vinoth L												 05/31/2023				    07/07 - Switch Removal & Before to After Context Switch
* Vinoth L												 06/24/2023				    User Story 4769775: - T1PRJ0154546 / SF / MF9 Storefront - Carry Forward First Time Visitor to Event Interactions
* Vinoth L												 07/24/2023			 		User Story 4891744: T1PRJ0154546 / SF / MF9 Storefront - Additional Interaction Types to be excluded from First Time Visitor Logic
* Deepak Khandelwal                                      03/04/2023                 User Story --5668977 --T1PRJ1371022 : MF: 5571284 / SF / Storefront - CxOne - Medium- Storefront_Interaction_H_HUM VIT37935988
*******************************************************************************************************************************/
public with sharing class Storefront_Interaction_H_HUM 
{    
    /**
    * processBeforeDelete
    * <p>
    * Allow sites to delete only when there are no agents related to site.
    * @param  List<Storefront_Interaction__c>    lstSfInteraction
    * @return Void
    **/ 
    public static void processBeforeDelete(List<Storefront_Interaction__c> lstSfInteraction) 
    {
        try
        { 
            List<CRMRetail_OOO_Tracking__c> trackingList = [SELECT Id, CRM_Retail_Interaction__c FROM CRMRetail_OOO_Tracking__c WHERE CRM_Retail_Interaction__c IN: lstSfInteraction];
            if(!trackingList.isEmpty() && CRMRetail_OOO_Tracking__c.sObjectType.getDescribe().isDeletable() && CRMRetail_OOO_Tracking__c.sObjectType.getDescribe().isAccessible()){
               GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DELETE_EVENT_TYPE, trackingList, 'Storefront_Interaction_H_HUM' , 'processBeforeDelete'); 

            }   
            id firstTimevisitor;
            id visitorCheckin;
            id healthEducator;
            List<Storefront_Interaction__c> processAttendanceList = new List<Storefront_Interaction__c>();
            List<Storefront_Interaction__c> processEventAttendanceList = new List<Storefront_Interaction__c>();
            List<Storefront_Interaction__c> deleteRelatedInteractionsList = new List<Storefront_Interaction__c>();
            Set<String> reasonSet = new Set<String>{GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HEALTH_EDUCATOR_REASON};
            for(Storefront_I_Reason__c reason : [SELECT Id, Name FROM Storefront_I_Reason__c WHERE name IN: reasonSet]){
            	if(reason.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN){
                    visitorCheckin = reason.Id;
                }
                else if(reason.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN){
                    firstTimevisitor = reason.Id;   
                }
                else if (reason.name == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HEALTH_EDUCATOR_REASON){
                    healthEducator = reason.Id;  
                }
            }
            for(Storefront_Interaction__c intVar : lstSfInteraction){
                if(intVar.Visitor_ID__c != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VIRTUAL_VISITOR_ID && (intVar.Reason__c == firstTimevisitor || intVar.Reason__c == visitorCheckin)){
                    processAttendanceList.add(intVar);
                }
                if(intVar.Reason__c != firstTimevisitor && intVar.Reason__c != visitorCheckin && intVar.category__c != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NOTIFICATION){
                    processEventAttendanceList.add(intVar);
                }
                if(lstSfInteraction[0].Reason__c == firstTimevisitor || lstSfInteraction[0].Reason__c == visitorCheckin || lstSfInteraction[0].Reason__c == healthEducator)
                    deleteRelatedInteractionsList.add(intVar);
            }
            if(processAttendanceList!= null && !processAttendanceList.isEmpty()){
                processAttendanceBeforeDelete(processAttendanceList);
            }
            if(processEventAttendanceList!= null && !processEventAttendanceList.isEmpty()){
                processEventAttendanceBeforeDelete(processEventAttendanceList);
            }
            if(deleteRelatedInteractionsList!= null && !deleteRelatedInteractionsList.isEmpty()){
                deleteRelatedInteractions(deleteRelatedInteractionsList);
            }        
        }
		catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Storefront_Interaction_H_HUM ', 'processBeforeDelete');
        }
    }
    
    /**
    * processAttendanceBeforeDelete
    * <p>
    * Allow sites to delete only when there are no agents related to site.
    * @param  List<Storefront_Interaction__c>    lstSfInteraction
    * @return Void
    **/ 
    public static void processAttendanceBeforeDelete(List<Storefront_Interaction__c> lstSfInteraction) 
    {
        try
        {
            if(lstSfInteraction[0].Visitor_ID__c != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VIRTUAL_VISITOR_ID)
            {
                List<Attendance__c> attListtoUpdate = new List<Attendance__c>();
                List<Attendance__c> attendanceList = [SELECT Id, Location__c, Date__c, Visitors_Today__c, First_Time_Visitors_Today__c, Current_Attendance__c FROM Attendance__c WHERE Location__c =: lstSfInteraction[0].Location__c AND Date__c =: lstSfInteraction[0].Interaction_Date__c.DateGMT()];
                
                if(!attendanceList.isEmpty())
                {
                    Map<String,Integer> mapCount = calculateCount(lstSfInteraction);
                    
                    for(Attendance__c att : attendanceList)
                    {
                        if(att.Current_Attendance__c > 0)
                            att.Current_Attendance__c = att.Current_Attendance__c - mapCount.get('current'); 
                        if(att.First_Time_Visitors_Today__c > 0)
                            att.First_Time_Visitors_Today__c = att.First_Time_Visitors_Today__c -  mapCount.get('first');
                        if(att.Visitors_Today__c > 0)
                            att.Visitors_Today__c = att.Visitors_Today__c -  mapCount.get('visitor');
                        attListtoUpdate.add(att);
                    }
                    GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE, attListtoUpdate, 'Storefront_Interaction_H_HUM' , 'processAttendanceBeforeDelete');   
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Storefront_Interaction_H_HUM ', 'processAttendanceBeforeDelete');
        }
    }
    /**
    * checkFirstTimeVisit
    * <p>
    * Identifies the first time the user attended an event in Storefront
    * @param  List<Storefront_Interaction__c> lstSfInteraction
    * @return Void
    **/ 
    public static void checkFirstTimeVisit(List<Storefront_Interaction__c> lstSfInteraction) 
    {
        try
        {  
            DateTime currInteractionDate;  
            Set<String> setOfAccIds = new Set<String>();
            Map<String,Date> mpOfAccIdToInteractionDate = new Map<String,Date>();
	        currInteractionDate = DateTime.valueOfGmt(String.valueof(lstSfInteraction[0].Interaction_date__c)).DateGMT();              
            List<Storefront_Interaction__c> lstOfFirstTimeInts = new List<Storefront_Interaction__c>();
            List<String> lstOfInteractionTypes = HUMUtilityHelper.isCRMFunctionalityON('4891744') ? System.Label.CRMRetail_NonFirstTime_Interaction_Types.split(';') : (new List<String>{System.Label.CRMRetail_NonFirstTime_Interaction_Types.split(';')[0]});
            Map<id,Storefront_I_Reason__c> mapOfReasonIdToInteractionType = new Map<Id,Storefront_I_Reason__c>([SELECT Id,Interaction_Type__c from Storefront_I_Reason__c where Interaction_Type__r.Name IN: lstOfInteractionTypes]);                        
            for(Storefront_Interaction__c sfInt: lstSfInteraction)
            {    
                if(!mapOfReasonIdToInteractionType.containsKey(sfInt.Reason__c) && (sfInt.Category__c == null || sfInt.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL) && sfInt.Storefront_Event_Starttime__c != NULL)
                {
                    setOfAccIds.add(string.valueOf(sfInt.Account__c));
                    lstOfFirstTimeInts.add(sfInt);
                }
            }            
            if(!setOfAccIds.isEmpty())
            {                       
                for(Storefront_Interaction__c sfInt: [SELECT Id,Account__c,Interaction_date__c,First_Time_Visitor__c from Storefront_Interaction__c where Account__c IN: setOfAccIds AND Reason__r.Interaction_Type__r.Name NOT IN: lstOfInteractionTypes AND (Category__c ='' OR Category__c =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL) AND Storefront_Event_Starttime__c != NULL order by Interaction_Date__c])
                {
                    if(!mpOfAccIdToInteractionDate.containsKey(sfInt.Account__c)){
                        mpOfAccIdToInteractionDate.put(sfInt.Account__c,DateTime.valueOfGmt(String.valueof(sfInt.Interaction_date__c)).DateGMT());                         
                    }                        
                } 
                for(Storefront_Interaction__c sfInt: lstOfFirstTimeInts)
                {	
                    if(!mpOfAccIdToInteractionDate.containsKey(sfInt.Account__c) || (mpOfAccIdToInteractionDate.containsKey(sfInt.Account__c) && mpOfAccIdToInteractionDate.get(sfInt.Account__c) ==  DateTime.valueOfGmt(String.valueof(sfInt.Interaction_date__c)).DateGMT())){                        
                        sfInt.First_Time_Visitor__c = true;
                    }                                                                                                                                         
                }
            }
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'Storefront_Interaction_H_HUM ', 'checkFirstTimeVisit');                                    
        }
    }
    /**
    * processBeforeInsert
    * <p>
    * Process the Go365 checks, duplicate interactions, Firsdt Time Visitor logics before inserting the interactions
    * @param  List<Storefront_Interaction__c> lstSfInteraction
    * @return Void
    **/ 
    public static void processBeforeInsert(List<Storefront_Interaction__c> lstSfInteraction) 
    {
        try
        {                                          
            go365EligibleChecks(lstSfInteraction);                
            processDuplicateInteractions(lstSfInteraction);
            if(HUMUtilityHelper.isCRMFunctionalityON('4769775'))
            {
                checkFirstTimeVisit(lstSfInteraction);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Storefront_Interaction_H_HUM ', 'processBeforeInsert');                        
        }
    }
    
    /**
    * calculateCount
    * <p>
    * Returns Map<String,Integer> which contain counts of Attendance__c
    * @param  List<Storefront_Interaction__c> lstSfInteraction
    * @return Map<String,Integer>
    **/
    public static Map<String,Integer> calculateCount(List<Storefront_Interaction__c> lstSfInteraction)
    {
        integer visitorCount = 0;
        integer firsttimeCount = 0;
        integer currentCount = 0;
        id firstTimevisitor;
        id visitorCheckin;
        Map<String,Integer> countMap = new Map<String,Integer>();
        List<Storefront_I_Reason__c> reasonList = [SELECT Id, Name FROM Storefront_I_Reason__c WHERE Name IN ('Visitor Check-In','Visitor Check-In (1st Time)')];
        for(Storefront_I_Reason__c reason : reasonList)
        {
            if(reason.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN)
                visitorCheckin = reason.Id;
            else if(reason.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN)
                firstTimevisitor = reason.Id;     
        }
        for(Storefront_Interaction__c sfint : lstSfInteraction)
        {
            if(sfint.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SUPPORT || String.isBlank(sfint.Category__c))
            {
                if(sfint.Reason__c == firstTimevisitor || sfint.Reason__c == visitorCheckin)
                {
                    currentCount = currentCount + 1;
                    visitorCount = visitorCount + 1;
                }
                if(sfint.Reason__c == firstTimevisitor)
                    firsttimeCount = firsttimeCount +1;
            }
        }
        countMap.put('current',currentCount);
        countMap.put('visitor',visitorCount);
        countMap.put('first',firsttimeCount);
        return countMap;
    }
    
    /**
    * processEventAttendanceOnInsert
    * <p>
    * process attendance count for particular events related to the interactions before insert
    * @param  List<Storefront_Interaction__c>    lstSfInteraction
    * @return Void
    **/ 
    public static void processEventAttendanceOnInsert(List<Storefront_Interaction__c> lstSfInteraction) 
    {
        try
        {
             calculateEventAttendance(lstSfInteraction,'insert');
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Storefront_Interaction_H_HUM ', 'processEventAttendanceOnInsert');
        }
    }
    
    /**
    * processEventAttendanceBeforeDelete
    * <p>
    * process attendance count for particular events related to the interactions before delete
    * @param  List<Storefront_Interaction__c>    lstSfInteraction
    * @return Void
    **/ 
    public static void processEventAttendanceBeforeDelete(List<Storefront_Interaction__c> lstSfInteraction) 
    {
        try
        {
             calculateEventAttendance(lstSfInteraction,'delete');
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Storefront_Interaction_H_HUM ', 'processEventAttendanceBeforeDelete');
        }
    }
    
    /**
    * calculateEventAttendance
    * <p>
    * Calculates interaction with blank and virtual category to decide event attendance
    * @param  List<Storefront_Interaction__c>    lstSfInteraction
    * @return Map<String, Integer>
    **/ 
    public static void calculateEventAttendance(List<Storefront_Interaction__c> lstSfInteraction, String actionType) 
    {
        Map<String, Integer> eventAttendanceMap = New Map<String, Integer>();
        Integer virtualCount = 0;
        Integer nonVirtualCount = 0;
        id firstTimevisitor;
        id visitorCheckin;
        id locId;
        set<id> reasonIds = new set<id>();
        set<DateTime> intStartTime = new set<DateTime>();
        set<DateTime> intEndTime = new set<DateTime>();
        List<Event> eventList = new List<Event>();
        set<Event> eventSetToUpdate = new set<Event>();
        List<Storefront_Interaction__c> validSfInteraction = new List<Storefront_Interaction__c>();
        Date intDate;
                
        List<Storefront_I_Reason__c> reasonList = [SELECT Id, Name FROM Storefront_I_Reason__c WHERE Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN OR Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN];
        for(Storefront_I_Reason__c reason : reasonList)
        {
            if(reason.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN)
                visitorCheckin = reason.Id;
            else if(reason.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN)
                firstTimevisitor = reason.Id; 
        }
        try
        {
            for(Storefront_Interaction__c sfint : lstSfInteraction)
            {                
                if(sfint.Reason__c != visitorCheckin && sfint.Reason__c != firstTimevisitor)
                {
                    reasonIds.add(sfint.Reason__c);
                    intStartTime.add(sfint.Storefront_Event_Starttime__c);
                    intEndTime.add(sfint.Storefront_Event_Endtime__c);
                    intDate = sfint.Interaction_Date__c.Date();
                    locId = sfint.Location__c;
                    validSfInteraction.add(sfint);
                }
            }
            if(reasonIds.size() > 0 && intStartTime.size() > 0 && intEndTime.size() > 0 && intDate != null)
            {
                eventList = [SELECT Id, IsAllDayEvent, Total_Attendance_Count__c, Known_Attendee_Count__c, Virtual_Attendee_Count__c, StartDateTime, EndDateTime, Event_StartTime__c, Event_EndTime__c, ActivityDate, Reason__c, WhatId, Scheduled_Onsite_Attendee_Count__c, Scheduled_Virtual_Attendee_Count__c FROM Event WHERE WhatId =:locId AND Reason__c =: reasonIds AND ((StartDateTime =: intStartTime AND EndDateTime =: intEndTime) OR IsAllDayEvent = true) AND ActivityDate =: intDate];
            }
            if(eventList.size() > 0)
            {
                for(Event evt : eventList)
                {
                    virtualCount = 0;
                    nonVirtualCount = 0;
                    for(Storefront_interaction__c sfint : validSfInteraction)
                    {
                        datetime dt;
                        if(((evt.StartDateTime == sfint.Storefront_Event_Starttime__c && evt.EndDateTime == sfint.Storefront_Event_Endtime__c) || evt.IsAllDayEvent == true) && evt.ActivityDate == sfint.Interaction_Date__c.Date() && evt.WhatId == sfint.Location__c && evt.Reason__c == sfint.Reason__c)
                        {
                            if(String.isBlank(sfint.Category__c))
                            {
                                if(actionType == 'insert')
                                {
                                    if(evt.Known_Attendee_Count__c != null)             
                                        evt.Known_Attendee_Count__c = evt.Known_Attendee_Count__c + 1;
                                    else
                                        evt.Known_Attendee_Count__c = 1;
                                }
                                else if(actionType == 'delete')
                                {
                                    if(evt.Known_Attendee_Count__c != null && evt.Known_Attendee_Count__c != 0)             
                                        evt.Known_Attendee_Count__c = evt.Known_Attendee_Count__c - 1;
                                }
                            }
                            else if(sfint.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL)
                            {
                                if(actionType == 'insert')
                                {
                                    if(evt.Virtual_Attendee_Count__c != null)
                                        evt.Virtual_Attendee_Count__c = evt.Virtual_Attendee_Count__c + 1;
                                    else
                                        evt.Virtual_Attendee_Count__c = 1;
                                }
                                else if(actionType == 'delete')
                                {
                                    if(evt.Virtual_Attendee_Count__c != null && evt.Virtual_Attendee_Count__c != 0)
                                        evt.Virtual_Attendee_Count__c = evt.Virtual_Attendee_Count__c - 1;
                                }
                            }
                            else if(sfint.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE)
                            {
                                if(actionType == 'insert')
                                {
                                    if(evt.Scheduled_Onsite_Attendee_Count__c != null)
                                        evt.Scheduled_Onsite_Attendee_Count__c = evt.Scheduled_Onsite_Attendee_Count__c + 1;
                                    else
                                        evt.Scheduled_Onsite_Attendee_Count__c = 1;
                                }
                                else if(actionType == 'delete')
                                {
                                    if(evt.Scheduled_Onsite_Attendee_Count__c != null && evt.Scheduled_Onsite_Attendee_Count__c != 0)
                                        evt.Scheduled_Onsite_Attendee_Count__c = evt.Scheduled_Onsite_Attendee_Count__c - 1;
                                }
                            }
                            else if(sfint.Category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL)
                            {
                                if(actionType == 'insert')
                                {
                                    if(evt.Scheduled_Virtual_Attendee_Count__c != null)
                                        evt.Scheduled_Virtual_Attendee_Count__c = evt.Scheduled_Virtual_Attendee_Count__c + 1;
                                    else
                                        evt.Scheduled_Virtual_Attendee_Count__c = 1;
                                }
                                else if(actionType == 'delete')
                                {
                                    if(evt.Scheduled_Virtual_Attendee_Count__c != null && evt.Scheduled_Virtual_Attendee_Count__c != 0)
                                        evt.Scheduled_Virtual_Attendee_Count__c = evt.Scheduled_Virtual_Attendee_Count__c - 1;
                                }
                            }
                        }
                    }
                    eventSetToUpdate.add(evt);
                }
            }
            if(eventSetToUpdate.size() > 0)
            {       
               	InactiveLocationOnCRMRetailEvent_H_HUM.bypassAttendanceVldn = true;
                update new List<Event>(eventSetToUpdate);               
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Storefront_Interaction_H_HUM ', 'processEventAttendanceBeforeDelete');
        }
    }
    
    /**
    * processBeforeUpdate
    * <p>
    * Process the functionalities that should occur on Interaction update
    * @param  List<Storefront_Interaction__c>  lstOfNewInteraction , Map<Id,Storefront_Interaction__C> mapOfOldInteractionById
    * @return Void
    **/ 
    public static void processBeforeUpdate(List<Storefront_Interaction__c> lstOfNewInteraction,Map<Id,Storefront_Interaction__c> mapOfOldInteractionById){     	                       
        virtualLocationChecks(lstOfNewInteraction,mapOfOldInteractionById);                   
    }
    /**
    * virtualLocationChecks
    * <p>
    * Restrict user without crm super admin access to update,insert,create with location type as virtual
    * @param  List<Storefront_Interaction__c>  lstOfNewInteraction
    * @return Void
    **/ 
    public static void virtualLocationChecks(List<Storefront_Interaction__c> lstOfNewInteraction,Map<Id,Storefront_Interaction__c> mapOfOldInteractionById)
    {
        try
        {  
            Boolean isVirtualLocEligible = FeatureManagement.checkPermission(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VIRTUAL_LOC_ELIGIBILITY_CUSTOM_PERM_NAME);            
            if(!isVirtualLocEligible)
            {  
                Set<id> setLocIds =  new Set<id>();
                Set<id> setVirtualLocIds;
        
                if(lstOfNewInteraction != null)
                {
                    for(Storefront_Interaction__c objInt : lstOfNewInteraction)
                    {
                        if(objInt.Location__c != null)
                        {   
                            setLocIds.add(objInt.Location__c);
                            
                            if(mapOfOldInteractionById != null && !mapOfOldInteractionById.isEmpty() && mapOfOldInteractionById.get(objInt.Id).Location__c != objInt.Location__c)
                            {
                                setLocIds.add(mapOfOldInteractionById.get(objInt.Id).Location__c);
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
                    for(Storefront_Interaction__c objIntVal : lstOfNewInteraction)
                    {
                        if (setVirtualLocIds.contains(objIntVal.Location__c) || setVirtualLocIds.contains(mapOfOldInteractionById.get(objIntVal.Id).Location__c))
                        {
                            objIntVal.addError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VIRTUAL_LOC_INELIGIBILITY_MSG);
                        }
                    }
                }
            }                                              
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Storefront_Interaction_H_HUM ', 'virtualLocationChecks ');
        }
    }           
    
     /**
    * go365EligibleChecks
    * <p>
    * Update the interactions to be Go365Eligible if the Reason assocaited with the Interaction is go365Eligible
    * @param  List<Storefront_Interaction__c>  lstOfInteraction
    * @return Void
    **/ 
    public static void go365EligibleChecks(List<Storefront_Interaction__c> lstOfInteraction)
    {
        try
        {
            Set<Id> setOfReasonId = new Set<Id>();
            Map<Id, Boolean> mapOfReason = new Map<Id, Boolean>();
            String dateStr = System.Label.CRMRetail_Release_Date;
            Date relDate = Date.valueOf(dateStr);
            for(Storefront_Interaction__c obInteraction : lstOfInteraction) {
                if(String.isNotBlank(obInteraction.Reason__c)){
                    setOfReasonId.add(obInteraction.Reason__c);
                }
            }            
            for(Storefront_I_Reason__c obReason : [Select Id, isGo365Eligible__c from Storefront_I_Reason__c where Id IN: setOfReasonId]){                                
                if(obReason != NULL){
                    mapOfReason.put(obReason.Id, obReason.isGo365Eligible__c);                
                }
            }
            for(Storefront_Interaction__c obInteraction : lstOfInteraction) {
                if(obInteraction.Storefront_Event_Starttime__c >= relDate && mapOfReason != NULL && mapOfReason.containsKey(obInteraction.Reason__c)){
                    obInteraction.isGo365Eligible__c = mapOfReason.get(obInteraction.Reason__c);
                }                                
            }                                       
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INTERACTION_HANDLER , GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GO365_ELIGIBLE);
        }
    }
    /**
    * deleteRelatedInteractions
    * <p>
    * process deletion of related interaction when a visitor interaction is deleted and sDOH records in case of Health Educator
    * @param  List<Storefront_Interaction__c>    lstSfInteraction
    * @return Void
    **/ 
    public static void deleteRelatedInteractions(List<Storefront_Interaction__c> lstSfInteraction) 
    {
        try
        {
            Map<Id,Storefront_Interaction__c> visitorInteractionMap = new Map<Id,Storefront_Interaction__c>();
            Map<Id,Storefront_Interaction__c> healthInteractionMap = new Map<Id,Storefront_Interaction__c>();
            List<Storefront_Interaction__c> deleteIntractionList = new List<Storefront_Interaction__c>();
            List<CRMRetail_SDoHDetermt__c> deleteSDOHList = new List<CRMRetail_SDoHDetermt__c>();
            Set<String> setOfInteractionLocation = new Set<String>();
            
            List<Storefront_Interaction__c> interactionList = [SELECT Id, Name, CreatedDate, Reason__c, Reason__r.Name, Account__c, Interaction_Date__c, Location__c FROM Storefront_Interaction__c WHERE Id IN: lstSfInteraction];
            for (Storefront_Interaction__c intObj: InteractionList) {
                if(intObj.Reason__r.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN || intObj.Reason__r.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN){
                    visitorInteractionMap.put(intObj.Account__c, intObj);
                }else if(intObj.Reason__r.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HEALTH_EDUCATOR_REASON){
                    healthInteractionMap.put(intObj.Account__c, intObj);
                }
                if(intObj.Location__c != NULL){
                    setOfInteractionLocation.add(intObj.Location__c);
                }
            }
            if(!visitorInteractionMap.isEmpty()){
                for(Storefront_Interaction__c intObj: [SELECT Id, Name, Category__c, Account__c, Interaction_Date__c, Location__c FROM Storefront_Interaction__c WHERE Account__c IN: visitorInteractionMap.keySet() AND Category__c =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NOTIFICATION AND Location__c IN : setOfInteractionLocation]){
                    if(visitorInteractionMap.containsKey(intObj.Account__c)){
                        if(intObj.Interaction_Date__c.Date() == visitorInteractionMap.get(intObj.Account__c).Interaction_Date__c.Date()){
                            deleteIntractionList.add(intObj);
                        }
                    }
                }
            }
            if(!healthInteractionMap.isEmpty()){
                for(CRMRetail_SDoHDetermt__c sdohObj: [SELECT Id, Name, Account__c, CreatedDate FROM CRMRetail_SDoHDetermt__c WHERE Account__c IN: healthInteractionMap.keySet()]){
                    if(healthInteractionMap.containsKey(sdohObj.Account__c)){
                        if(sdohObj.CreatedDate.Date() == healthInteractionMap.get(sdohObj.Account__c).CreatedDate.Date()){
                            deleteSDOHList.add(sdohObj);
                        }
                    }
                }
            }
            if(!deleteIntractionList.isEmpty()){
               GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DELETE_EVENT_TYPE,deleteIntractionList, 'Storefront_Interaction_H_HUM' , 'deleteRelatedInteractions');
            }
            if(!deleteSDOHList.isEmpty()){
               GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DELETE_EVENT_TYPE,deleteSDOHList, 'Storefront_Interaction_H_HUM' , 'deleteRelatedInteractions'); 
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INTERACTION_HANDLER, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_RELATED_INTERACTIONS);
        }
    }
    /**
    * processAttendanceOnInsert
    * calculates the onsite attendance for the checked-in location
    * Called from Storefront_Iteraction_G_HUM trigger for After Insert Context
    * @param  List<Storefront_Interaction__c>    lstSfInteraction
    * @return Void
    **/
    public static void processAttendanceOnInsert(List<Storefront_Interaction__c> lstSfInteraction) 
    {
        try
        {
            if(lstSfInteraction[0].Visitor_ID__c != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VIRTUAL_VISITOR_ID)
            {
                Map<String,Integer> mapCount = calculateCount(lstSfInteraction);
                List<Attendance__c> attListtoUpdate = new List<Attendance__c>();
                List<Attendance__c> attendanceList = [SELECT Id, Maximum_Concurrent_Visitors__c, Peak_Time__c, Location__c, Date__c, Visitors_Today__c, First_Time_Visitors_Today__c, Current_Attendance__c FROM Attendance__c WHERE Location__c =: lstSfInteraction[0].Location__c AND Date__c =: lstSfInteraction[0].Interaction_Date__c.DateGMT()];
                
                if(!attendanceList.isEmpty())
                {
                    for(Attendance__c att : attendanceList)
                    {
                        att.Current_Attendance__c = att.Current_Attendance__c + mapCount.get('current'); 
                        att.First_Time_Visitors_Today__c = att.First_Time_Visitors_Today__c + mapCount.get('first');
                        att.Visitors_Today__c = att.Visitors_Today__c + mapCount.get('visitor');
                        att = GenericHelper_CRMRetail_H_HUM.evaluateAttendance(att);
                        attListtoUpdate.add(att);
                    }
                    GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE,attListtoUpdate, 'Storefront_Interaction_H_HUM','processAttendanceOnInsert'); 
                }
                else
                {
                    CRM_GenericHelper_H_HUM.accessibilityOutput objAccessOutput = new CRM_GenericHelper_H_HUM.accessibilityOutput();
                    List<Attendance__c> lstAtt = new List<Attendance__c>();
                    Attendance__c attToInsert = GenericHelper_CRMRetail_H_HUM.createAttendance(lstSfInteraction[0].Location__c, lstSfInteraction[0].Interaction_Date_Only__c);
                    attToInsert.Current_Attendance__c = mapCount.get('current');
                    attToInsert.First_Time_Visitors_Today__c = mapCount.get('first'); 
                    attToInsert.Visitors_Today__c = mapCount.get('visitor');
                    attToInsert.Maximum_Concurrent_Visitors__c = mapCount.get('current');
                    lstAtt.add(attToInsert);                                                              
                    objAccessOutput = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.CREATABLE, lstAtt);
                    
                    if(objAccessOutput.exceptionGenerated)
                    {
                        HUMExceptionHelper.logError(String.valueOf(objAccessOutput.exceptionToLog)  ,'Storefront_Interaction_H_HUM','processAttendanceOnInsert','','');                            
                        throw AuraErrorHandler_C_HUM.createAuraHandledException('Storefront_Interaction_H_HUM','processAttendanceOnInsert', String.valueOf(objAccessOutput.exceptionToLog));
                    }
                    else{
                        if(objAccessOutput.isObjectAccessible)
                        {
                            if(objAccessOutput.areFieldsAccessible)
                            {
                                insert lstAtt;                                    
                            }
                            else
                            {
                                Set<String> removedFields =  objAccessOutput.fieldsNotAccessible;
                                HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ATTENDANCE + ' - ' +removedFields,'Storefront_Interaction_H_HUM','processAttendanceOnInsert','','');                                    
                                throw AuraErrorHandler_C_HUM.createAuraHandledException('Storefront_Interaction_H_HUM', 'processAttendanceOnInsert', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ATTENDANCE + ' - ' +removedFields);
                            }
                        }
                        else
                        {
                            HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ATTENDANCE,'Storefront_Interaction_H_HUM','processAttendanceOnInsert','','');                                
                            throw AuraErrorHandler_C_HUM.createAuraHandledException('Storefront_Interaction_H_HUM', 'processAttendanceOnInsert' , GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS +' '+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ATTENDANCE);
                        }
                    }                   
                }                                    
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Storefront_Interaction_H_HUM ', 'processAttendanceOnInsert');                        
        }
    }
    /**
    * processAfterInsert
    * <p>
    * Called from Storefront_Iteraction_G_HUM trigger for After Insert Context
    * @param  List<Storefront_Interaction__c>    lstSfInteraction
    * @return Void
    **/
    public static void processAfterInsert(List<Storefront_Interaction__c> lstSfInteraction) 
    {
        try
        {                
            processMissingEvent(lstSfInteraction, True);
            processAttendanceOnInsert(lstSfInteraction);
            processEventAttendanceOnInsert(lstSfInteraction);                                                 
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INTERACTION_HANDLER, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_AFTER_INSERT);
        }
    }
    /**
    * processAfterDelete
    * <p>
    * Called from Storefront_Interaction_G_HUM trigger for After Insert Context
    * @param  List<Storefront_Interaction__c>    lstSfInteraction
    * @return Void
    **/
    public static void processAfterDelete(List<Storefront_Interaction__c> lstSfInteraction) 
    {
        try
        {            
            processMissingEvent(lstSfInteraction, False);            
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INTERACTION_HANDLER, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_AFTER_DELETE);
        }
    }
    /**
    * processMissingEvent
    * <p>
    * Update missing event field if there are no event interactions for a checkin.
    * @param  List<Storefront_Interaction__c>    lstSfInteraction
    * @return Void
    **/
    public static void processMissingEvent(List<Storefront_Interaction__c> lstSfInteraction, Boolean isInsert) 
    {
        try
        {
            Set<String> locationMdtNameSet = GenericHelper_CRMRetail_H_HUM.getLocationMdtNames();
            Set<Id> locationMdtIdSet = new Set<Id>();
            Set<Id> locationIdSet = new Set<Id>();
            Set<Id> accountIdSet = new Set<Id>();
            Set<String> reasonIdSet = new Set<String>();
            Set<Date> createdDateSet = new Set<Date>();
            Set<String> reasonSet = new Set<String>{GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN};
            Set<String> eventCategorySet = new Set<String>{GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_ONSITE, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE, ''};
            Set<Storefront_Interaction__c> checkinIntSet = new Set<Storefront_Interaction__c>();
            Set<Storefront_Interaction__c> eventIntSet = new Set<Storefront_Interaction__c>();
            Set<Storefront_Interaction__c> checkinIntSetQueried = new Set<Storefront_Interaction__c>();
            Set<Storefront_Interaction__c> eventIntSetQueried = new Set<Storefront_Interaction__c>();
            Map<Id,Boolean> finalMap = new Map<Id,Boolean>();
            List<Storefront_Interaction__c> interactionToUpdate = new List<Storefront_Interaction__c>();
            for(Storefront_I_Reason__c reasonObj: [SELECT Id, Name FROM Storefront_I_Reason__c WHERE Name IN: reasonSet]){
                reasonIdSet.add(reasonObj.Id);
            }
            for(Storefront_Location__c locObj: [SELECT Id, Name FROM Storefront_Location__c WHERE Name IN: locationMdtNameSet]){
                locationMdtIdSet.add(locObj.Id);
            }
            for(Storefront_Interaction__c intObj : lstSfInteraction){ 
                if(!locationMdtIdSet.contains(intObj.Location__c)){   
                    if(String.isBlank(intObj.Category__c) || eventCategorySet.contains(intObj.Category__c)){
                        locationIdSet.add(intObj.Location__c);
                        accountIdSet.add(intObj.Account__c);
                        createdDateSet.add(intObj.CreatedDate.Date());
                        if(reasonIdSet.contains(intObj.reason__c)){
                            checkinIntSet.add(intObj);
                        }else{
                            eventIntSet.add(intObj);
                        }
                    }
                }
            }
            for(Storefront_Interaction__c intObj: [SELECT Id, Name, Account__c, CreatedDate, Category__c, Missing_Event__c, Location__c, Reason__r.Name FROM Storefront_Interaction__c WHERE Location__c IN: locationIdSet AND Account__c IN: accountIdSet AND Category__c IN: eventCategorySet]){
                if(createdDateSet.contains(intObj.CreatedDate.Date())){
                    if(reasonSet.contains(intObj.Reason__r.Name)){
                        checkinIntSetQueried.add(intObj);
                    }
                    else{
                        eventIntSetQueried.add(intObj);
                    }
                }
            }
            If(isInsert){
                if(checkinIntSet.size() > 0){
                    for(Storefront_Interaction__c checkIntObj: checkinIntSet){
                        boolean isMissingEvent = True;
                        for(Storefront_Interaction__c eventIntObj: eventIntSetQueried){
                            if(checkIntObj.Location__c == eventIntObj.Location__c && checkIntObj.Account__c == eventIntObj.Account__c && checkIntObj.CreatedDate.Date() == eventIntObj.CreatedDate.Date()){
                                isMissingEvent =  False;
                                break;
                            }
                        }
                        finalMap.put(checkIntObj.Id, isMissingEvent);
                    }
                }
                if(eventIntSet.size() > 0){
                    for(Storefront_Interaction__c eventIntObj: eventIntSet){
                        for(Storefront_Interaction__c checkIntObj: checkinIntSetQueried){  
                            if(checkIntObj.Missing_Event__c &&  eventIntObj.Location__c == checkIntObj.Location__c && eventIntObj.Account__c == checkIntObj.Account__c && eventIntObj.CreatedDate.Date() == checkIntObj.CreatedDate.Date()){
                                finalMap.put(checkIntObj.Id, False);
                            }                   
                        }
                    }
                }
            }else{
                if(eventIntSet.size() > 0){
                    for(Storefront_Interaction__c eventIntObj: eventIntSet){
                        boolean isotherEventIntAvailable =  False;
                        for(Storefront_Interaction__c queriedIntObj: eventIntSetQueried){                  
                            if(eventIntObj.Id!=queriedIntObj.Id && eventIntObj.Location__c == queriedIntObj.Location__c && eventIntObj.Account__c == queriedIntObj.Account__c && eventIntObj.CreatedDate.Date() == queriedIntObj.CreatedDate.Date()){
                                isotherEventIntAvailable =  True;
                                break;
                            }                   
                        }
                        if(!isotherEventIntAvailable)
                        {
                            for(Storefront_Interaction__c checkIntObj: checkinIntSetQueried){                    
                                if(eventIntObj.Location__c == checkIntObj.Location__c && eventIntObj.Account__c == checkIntObj.Account__c && eventIntObj.CreatedDate.Date() == checkIntObj.CreatedDate.Date()){
                                    finalMap.put(checkIntObj.Id, True);
                                }                   
                            }
                        }
                    }
                }
            }
            for(Storefront_Interaction__c intObj: [SELECT Id, Missing_Event__c FROM Storefront_Interaction__c WHERE Id IN: finalMap.keySet()])
            {
                if(intObj.Missing_Event__c != finalMap.get(intObj.id)){
                    intObj.Missing_Event__c = finalMap.get(intObj.id);
                    interactionToUpdate.add(intObj);
                }
            }
           //CXOne changes
           //update interactionToUpdate;
           GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE,interactionToUpdate, 'Storefront_Interaction_H_HUM','processMissingEvent');  
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INTERACTION_HANDLER, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_PROCESS_MISSING_EVENT);
        }
    }
    /**
    * processDuplicateInteractions
    * <p>
    * Check if duplcate interaction exists.
    * @param  List<Storefront_Interaction__c> lstSfInteraction
    * @return Void
	**/
    public static void processDuplicateInteractions(List<Storefront_Interaction__c> lstSfInteraction) 
    {
        Map<string, Storefront_Interaction__c> intKeyMap = new Map<string, Storefront_Interaction__c>();
        Map<string, Storefront_Interaction__c> firstTimeIntKeyMap = new Map<string, Storefront_Interaction__c>();
        try{
            Set<id> reasonIds = new Set<id>();
            Set<id> accountIds = new Set<id>();
            Set<string> visitiorIds = new Set<string>();
            Set<datetime> intStartTime = new Set<datetime>();
            List<Storefront_Interaction__c> lstInteractionsQueried = new List<Storefront_Interaction__c>();           
            Id checkInFirstTimeReasonId ;
            String key = '';
            String firstTimeKey = '';
            Set<Date> intDate= new Set<Date>();
            Id locationId;
            Set<String> categories = new Set<String>();            
            string category = '';            
            Storefront_I_Reason__c firstTimeCheckinReason = [Select Id FROM Storefront_I_Reason__c WHERE Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN];
            if(firstTimeCheckinReason != null)
            {
                reasonIds.add(firstTimeCheckinReason.Id);
                checkInFirstTimeReasonId = firstTimeCheckinReason.Id;                
            }
            for(Storefront_Interaction__c intObj: lstSfInteraction){
                categories.add(intObj.Category__c);
                intDate.add(intObj.Interaction_Date__c.DateGMT());                
                intDate.add(intObj.Interaction_Date__c.DateGMT()+1);
                intDate.add(intObj.Interaction_Date__c.DateGMT()-1);                
                locationId = intObj.Location__c;
                reasonIds.add(intObj.Reason__c);
                accountIds.add(intObj.Account__c);
                visitiorIds.add(intObj.Visitor_ID__c);
                intStartTime.add(intObj.Storefront_Event_Starttime__c);                 
                if(intObj.Category__c == null)
                    category = '';
                else
                    category = string.valueOf(intObj.Category__c);   
                key = intObj.Interaction_Date__c.format('yyyy-MM-dd')+string.valueOf(intObj.Reason__c) + string.valueOf(intObj.Location__c)+ string.valueOf(intObj.Account__c)+ string.valueOf(intObj.Visitor_ID__c)+
                    string.valueOf(intObj.Storefront_Event_Starttime__c) + category;
                firstTimeKey = intObj.Interaction_Date__c.format('yyyy-MM-dd')+checkInFirstTimeReasonId + intObj.Location__c+ intObj.Account__c+ intObj.Visitor_ID__c+
                    string.valueOf(intObj.Storefront_Event_Starttime__c) + category; 
                intKeyMap.put(key, intObj);                
                firstTimeIntKeyMap.put(firstTimeKey, intObj);
            }  
             
            lstInteractionsQueried = [SELECT Id,Reason__c, Reason__r.Name,Location__c,Interaction_Date__c ,Account__c, Account__r.Name, Visitor_ID__c,Storefront_Event_Starttime__c,  Category__c FROM Storefront_Interaction__c WHERE Reason__c =: reasonIds AND (Location__c =: locationId OR  Reason__c =: checkInFirstTimeReasonId) AND  DAY_ONLY(Interaction_Date__c) IN: intDate AND Account__c =: accountIds  ANd Visitor_ID__c =: visitiorIds AND (Storefront_Event_Starttime__c =: intStartTime OR Storefront_Event_Starttime__c = null) AND Category__c =: categories];                                              
            if(!lstInteractionsQueried.isEmpty()){ 
                for(Storefront_Interaction__c intQObj:lstInteractionsQueried){
                    if(intQObj.Category__c == null){
                        category = '';
                    }
                    else{
                        category = string.valueOf(intQObj.Category__c);
                        key = intQObj.Interaction_Date__c.format('yyyy-MM-dd')+intQObj.Reason__c + intQObj.Location__c+ intQObj.Account__c+ intQObj.Visitor_ID__c+
                        string.valueOf(intQObj.Storefront_Event_Starttime__c) + category; 
                    }
                    if(intKeyMap.containsKey(key)) 
                    {
                        intKeyMap.get(key).addError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DUPLICATE_KEY  + '-' + intQObj.Account__c +':' + intQObj.Account__r.Name +GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ALREADY_ASSIGNED_TEXT+ intQObj.Reason__r.Name);
                    }                    
                    else if(firstTimeIntKeyMap.containsKey(key)){
                        firstTimeIntKeyMap.get(key).addError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DUPLICATE_KEY + '-' + intQObj.Account__c + ':' + intQObj.Account__r.Name +GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ALREADY_ASSIGNED_TEXT+ intQObj.Reason__r.Name);
                    } 
                }               
            }
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INTERACTION_HANDLER, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_PROCESS_DUPLICATE);
        }
    }
}