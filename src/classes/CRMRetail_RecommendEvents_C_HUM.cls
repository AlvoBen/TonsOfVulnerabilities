/*
Component Name   : CRMRetail_RecommendEvents_C_HUM
Version          : 1.0
Created On       : 10/06/2021
Function         : this class is used as controller for Lightning Web Component CRMRetail_RecommendEvents_LWC_HUM

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*****************************************************************************************************************************
* Vinoth L											   08/01/2022					Initial Version
**************************************************************************************************************************** */
public with sharing class  CRMRetail_RecommendEvents_C_HUM {              
    /*
	* Method Name   :    getRecommendedEvents
	* Description   :    Computes the logic for showing the recommendations
	* Return Type   :    Map<string,List<RecommendationEventsWrapper>>
	* Parameters    :    Account id
	*/   
    @AuraEnabled
    public static Map<String,Map<String,List<RecommendationEventsWrapper>>> getRecommendedEvents(String accId){
        Map<String,Map<String,List<RecommendationEventsWrapper>>> mpOfRecommendEvents = new Map<String,Map<String,List<RecommendationEventsWrapper>>>();
        try{            
            List<String> ListofInteractionTypes = new List<String>();
            Map<String,integer> mpOfIntTypeToCount = new Map<String,integer>();
            Map<String,long> mpOfIntTypeToCountOfEvents = new Map<String,long>();                         
            Integer interactionsTotalCount = 0;        
            String accountId = String.escapeSingleQuotes(accId);
            String sCurrentLocation = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION);             
            String sLocName = sCurrentLocation.split('-',2)[1];             
			string currentLocationId ;
            Map<Id,String> mpOfLocationToType = new Map<Id,String>();
            for(Storefront_Location__c objLoc : [SELECT Id,Name,Location_type__c FROM Storefront_Location__c WHERE Name =: sLocName OR Location_type__c =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL]){              
                mpOfLocationToType.put(objLoc.Id,objLoc.Location_Type__c);				
				if (objLoc.name == sLocName)
				currentLocationId =objLoc.id;
            }           
			
            List<AggregateResult> lstOfRcmdInts =  [select count(id),reason__r.Interaction_Type__c from storefront_interaction__c where account__c=:accountId and Location__r.Name =: sLocName AND Interaction_Date__c=last_n_days:30 and reason__r.Interaction_Type__c != null and Reason__r.Name !=: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN AND Reason__r.Name !=: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN AND Category__c NOT IN: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SET_INTERACTION_CATEGORY_TYPES AND reason__r.Interaction_Type__r.Storefront_Excld_Recmd__c = False  group by reason__r.Interaction_Type__c order by count(id) desc LIMIT 3];        
                  	
            if(lstOfRcmdInts != NULL && lstOfRcmdInts.size() >0){               
                for(AggregateResult arr: lstOfRcmdInts)
                {                      
                    ListofInteractionTypes.add((Id)arr.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INTERACTIONTYPE)); 
                    interactionsTotalCount += (Integer)arr.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EXPR0);
                    mpOfIntTypeToCount.put((Id)arr.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INTERACTIONTYPE),(Integer)arr.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EXPR0));
                }
            }            
			
            if (interactionsTotalCount >0)
            {
                for (string intTypes:ListofInteractionTypes){                
                    Decimal frequencyPercentage = (Decimal)mpOfIntTypeToCount.get(intTypes)/interactionsTotalCount;
                    Decimal frequencyEvents = (frequencyPercentage * GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDATION_COUNT);                
                    long noOfEventsRounded = frequencyEvents.round();                
                    mpOfIntTypeToCountOfEvents.put(intTypes,noOfEventsRounded);
                }
            }  
			
            List<Event> lstEvents = new List<Event>();             
            lstEvents = [Select startdatetime,WhatId,enddatetime,reason__r.name,reason__c,reason__r.Interaction_type__c,isAllDayEvent from event where whatid =: mpOfLocationToType.keySet() and (startdatetime = NEXT_n_DAYS:90 OR startdatetime = today) AND reason__r.Interaction_Type__r.Storefront_Excld_Recmd__c = False ORDER BY startdatetime ];                                            
			
            Map<string,Map<String,List<event>>> mapOfIntTypeToEvents = new Map<string,Map<String,List<event>>>(); 			
			Map<string,Map<String,List<event>>> mapOfIntTypeToNext7DaysEvents = new Map<string,Map<String,List<event>>>(); 
			Date nextWeekDate =  system.today().adddays(7);
			
            List<event> finalListOfEvents  = new List<event>();
            Boolean isRecommendedEligible = true;
            if(lstEvents != NULL && lstEvents.size() >0){
                for (event evt:lstEvents){    
					date eventDate = evt.startdatetime.date();
                    if ( mapOfIntTypeToEvents.containskey(evt.reason__r.Interaction_type__c) ){                        
                        if (mapOfIntTypeToEvents.get(evt.reason__r.Interaction_type__c).containskey(evt.reason__c)){
                            mapOfIntTypeToEvents.get(evt.reason__r.Interaction_type__c).get(evt.reason__c).add(evt);							
                        }
                        else{                            
                            mapOfIntTypeToEvents.get(evt.reason__r.Interaction_type__c).put(evt.reason__c,new List<event>{evt});																					
                        }												
						if ( eventDate <= nextWeekDate && evt.whatID == currentLocationId && mapOfIntTypeToNext7DaysEvents.containskey(evt.reason__r.Interaction_type__c) ){
							if (mapOfIntTypeToNext7DaysEvents.get(evt.reason__r.Interaction_type__c).containskey(evt.reason__c)){								
									mapOfIntTypeToNext7DaysEvents.get(evt.reason__r.Interaction_type__c).get(evt.reason__c).add(evt);								
							}
							else{								
									mapOfIntTypeToNext7DaysEvents.get(evt.reason__r.Interaction_type__c).put(evt.reason__c,new List<event>{evt});														
							}	
						}						
                    }
                    else{
                        map<string,List<event>>  mapOfIntReasonToEvents = new map<string,List<event>>();
                        mapOfIntReasonToEvents.put(evt.reason__c,new List<event>{evt});
                        mapOfIntTypeToEvents.put(evt.reason__r.Interaction_type__c, mapOfIntReasonToEvents);						
                        if ( eventDate <= nextWeekDate && evt.whatID == currentLocationId){
							map<string,List<event>>  mapOfIntReasonToNext7DaysEvents = new map<string,List<event>>();
							mapOfIntReasonToNext7DaysEvents.put(evt.reason__c,new List<event>{evt});
							
                            mapOfIntTypeToNext7DaysEvents.put(evt.reason__r.Interaction_type__c, mapOfIntReasonToNext7DaysEvents);
                        }                        
                    }
                }
                integer totalCount = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDATION_COUNT;            
                List<string> keyList;                
			    for(string intTypes: ListofInteractionTypes){                   
                    if (mapOfIntTypeToEvents.containskey(intTypes) && mpOfIntTypeToCountOfEvents.containskey(intTypes)){
                        integer count =  (integer)mpOfIntTypeToCountOfEvents.get(intTypes);                
                        integer availableEventsCount = mapOfIntTypeToEvents.get(intTypes).size();                  
                        Map<String,List<event>>  mapOfIntReasonToEventsLocal =  mapOfIntTypeToEvents.get(intTypes); 
                        if ( availableEventsCount <= count){ 							
                            for (List<event> events : mapOfIntReasonToEventsLocal.values() )
                            {
                                finalListOfEvents.addAll(events);
                            }
                            totalCount = totalCount - availableEventsCount;
                        }
                        else {  							
                            while (count != 0){
                                keyList = new List<string>(mapOfIntReasonToEventsLocal.keyset());
                                integer randomIndex =  Integer.valueOf(Math.Random() * availableEventsCount);                                                
                                string key =  keyList[randomIndex];                        
                                finalListOfEvents.addAll(mapOfIntReasonToEventsLocal.get(key));
                                mapOfIntReasonToEventsLocal.remove(key);
                                count = count-1;
                                totalCount = totalCount - 1;
                                availableEventsCount =  availableEventsCount-1;
                            }
                        }
                    }
                }
                if(totalCount == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDATION_COUNT){					
					if (mapOfIntTypeToNext7DaysEvents.size()> 0)
					{
						isRecommendedEligible = false;						
						finalListOfEvents.addAll(getFinalListOfRecommendedEvents(mapOfIntTypeToNext7DaysEvents,totalCount,ListofInteractionTypes));
					}
					else{						
						mpOfRecommendEvents.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NO_EVENTS,NULL);
					}
                }                        
                else if(totalCount > 0 && totalCount != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDATION_COUNT ){ 
                    finalListOfEvents.addAll(getFinalListOfRecommendedEvents(mapOfIntTypeToEvents,totalCount,ListofInteractionTypes));
                }                                                 
            }                
            else{
                mpOfRecommendEvents.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NO_EVENTS,NULL);
            } 
            Map<String,List<RecommendationEventsWrapper>> mapOfRcmdEvents = new Map<String,List<RecommendationEventsWrapper>>();			
            if(finalListOfEvents != NULL && finalListOfEvents.size() > 0){                             
                mapOfRcmdEvents = populateFinalMap(finalListOfEvents,mpOfLocationToType); 
                if(isRecommendedEligible){ 
                    mpOfRecommendEvents.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDED_EVENTS,mapOfRcmdEvents);                                        
                }           
                else{
                    mpOfRecommendEvents.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_UPCOMING_EVENTS,mapOfRcmdEvents);
                }
            }               
        }
        catch(exception ex){
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EVENTRECOMMENDATION_CLASS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GET_RECOMMENDATION_EVENTS);
            String error = CRMRetailVNC_Helper_HUM.getError(ex.getMessage());
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EVENTRECOMMENDATION_CLASS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GET_RECOMMENDATION_EVENTS,error);    
        }
        return mpOfRecommendEvents;
    }  
    private static List<Event> getFinalListOfRecommendedEvents(Map<string,Map<String,List<event>>> mapOfIntTypeToEvents,Integer totalCount,List<string> ListofInteractionTypes){
        List<Event> finalListOfEvents = new List<Event>();
        for(String intType: mapOfIntTypeToEvents.keyset()){                
            if (!ListofInteractionTypes.contains(intType)){
                for (List<event> events : mapOfIntTypeToEvents.get(intType).values())
                {
                    finalListOfEvents.addAll(events);
                    totalCount = totalCount - 1;
                    
                    if (totalCount == 0){
                        Break;
                    }
                }			
            }
            if (totalCount == 0){
                Break;
            }
        }		
        return finalListOfEvents;
    }
    /*
	* Method Name   :    populateFinalMap
	* Description   :    prepares all the event recommendations to be shown on UI
	* Return Type   :    Map<string,List<RecommendationEventsWrapper>>
	* Parameters    :    List of Events, Map of location it's type
	*/
    private static Map<String,List<RecommendationEventsWrapper>> populateFinalMap(List<Event> finalListOfEvents,Map<Id,String> mpOfLocationToType){
        Map<String,List<RecommendationEventsWrapper>> mpOfRcmdEvents = new Map<String,List<RecommendationEventsWrapper>>();
        RecommendationEventsWrapper eventsWrap;
        for(Event eve: finalListOfEvents){
            eventsWrap= new RecommendationEventsWrapper();
            eventsWrap.ReasonName = eve.Reason__r.Name;
            eventsWrap.ReasonId = eve.Reason__c;
            eventsWrap.IsAllDayEvent = eve.IsAllDayEvent;
            eventsWrap.StartDateTime = eve.StartDateTime;
            eventsWrap.EndDateTime = eve.EndDateTime; 
            eventsWrap.IsvirtualEvent = (mpOfLocationToType != NULL && mpOfLocationToType.containsKey(eve.WhatId) && mpOfLocationToType.get(eve.WhatId) != NULL && mpOfLocationToType.get(eve.WhatId) == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL) ? true : false;            
            if(mpOfRcmdEvents != NULL && mpOfRcmdEvents.containsKey(eve.Reason__r.Name) && mpOfRcmdEvents.get(eve.Reason__r.Name) !=NULL)
            {                
                mpOfRcmdEvents.get(eve.Reason__r.Name).add(eventsWrap);
            }   
            else{
                mpOfRcmdEvents.put(eve.Reason__r.Name,new List<RecommendationEventsWrapper>{eventsWrap});
            }   
        }        
        return mpOfRcmdEvents;
    }
    /*
	* Method Name   :    createNewInteractionsForRecommendation
	* Description   :    create interactions for events through recommendation logic
	* Return Type   :    Map<string,String>
	* Parameters    :    Interaction List Stringified, Reason List Stringified, Category, Need to create tasks
	*/
    @AuraEnabled
    Public Static Map<String,String> createNewInteractionsForRecommendation(String sinteractionList, String sintReasonList, String categoryType, boolean isTaskCreationCheck) 
    {  
        Map<String,String> returnMap = new Map<String,String>();
        try{
            returnMap =CRMRetail_InteractionsEvents_LC_HUM.createNewInteractions(sinteractionList,sintReasonList,categoryType);
            if(returnMap.size()>0 && returnMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSUCCESS) == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TRUE && isTaskCreationCheck)
            {
                List<String> successCheckinIds = (List<String>)JSON.deserialize(returnMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SUCCESSLIST), List<String>.class);
                List<Storefront_Interaction__C> successInteractionList = new List<Storefront_Interaction__C>();
                successInteractionList = getListofSuccessInteractions(successCheckinIds); 
                BusinessHours businessHourObj = getBusinessHour();
                createTasks(successInteractionList,businessHourObj);                    
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EVENTRECOMMENDATION_CLASS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CREATEINTERACTIONFORRECOMMEND_METHOD);
            String error = CRMRetailVNC_Helper_HUM.getError(ex.getMessage());
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EVENTRECOMMENDATION_CLASS, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CREATEINTERACTIONFORRECOMMEND_METHOD,error);           
        }
        return returnMap;              
    }    
	/*
	* Method Name   :    getRequiredDueDate
	* Description   :    Computes the Business hours 
	* Return Type   :    Date
	* Parameters    :    Business hours, start date, offset to compute
	*/
    public static Date getRequiredDueDate(BusinessHours businessHourObj,DateTime startDate,Integer daysOffset)
    {        
        DateTime finalDay;
        Timezone tz = UserInfo.getTimeZone();        
        DateTime userStartDateTime = startDate.addSeconds((tz.getOffset(startDate)/1000));         
        final Integer INTEGER_3DAY_MILISECONDS = daysOffset*24*60*60*1000;        
        finalDay = (businessHourObj != null) ?BusinessHours.add(businessHourObj.id, userStartDateTime, -INTEGER_3DAY_MILISECONDS) : null;         
        String finalDayString = finalDay.format(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_TASKDATEFORMAT,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_TIMEZONEGMT);
        List<String> dateString = finalDayString.split(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_HIGHPHEN);
        Date finalDate = date.newinstance(Integer.valueOf(dateString[0]),Integer.valueOf(dateString[1]) , Integer.valueOf(dateString[2])); 
        return finalDate;        
    } 
	/*
	* Method Name   :    getListofSuccessInteractions
	* Description   :    get the list of success interactions
	* Return Type   :    List<Storefront_Interaction__C>
	* Parameters    :    List of Storefront_Interactions
	*/
    public static List<Storefront_Interaction__C> getListofSuccessInteractions(List<String> ListIds)
    {
        return [select Id,Account__r.PersonContactId,Account__r.Id,Reason__c,Reason__r.Name,Location__r.Id,Storefront_Event_Starttime__c from Storefront_Interaction__C where Id in :ListIds];
    }
	/*
	* Method Name   :    getBusinessHour
	* Description   :    get the required Business Hours
	* Return Type   :    BusinessHours
	* Parameters    :    N/A
	*/
    public static BusinessHours getBusinessHour()
    {
       BusinessHours bObj;
       List<BusinessHours> listBusinesshours = [SELECT id from BusinessHours Where name =: GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_BUSINESSHOURS];
       if(listBusinesshours.size()>0)
         bObj = listBusinesshours[0];
       return bObj;
    }
	/*
	* Method Name   :    createTasks
	* Description   :    method to create the follow-up tasks for recommendation events
	* Return Type   :    void
	* Parameters    :    List of successful interactions, Business hours
	*/
    public static void createTasks(List<Storefront_Interaction__C> successInteractionList, BusinessHours businessHourObj)
    {
        List<Task> tasksToInsert = new  List<Task>();
        try
        {    
            Id crmretailTaskRecordType = Schema.SObjectType.Task.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRM_RETAIL_TASK).getRecordTypeId();          
            for(Storefront_Interaction__C intObj : successInteractionList)
            {  
                if(intObj.Storefront_Event_Starttime__c.date() > Date.Today().addDays(7))
                {
                    DateTime tempStartDate=intObj.Storefront_Event_Starttime__c;                                        
                    String formattedStartDate = tempStartDate.format(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_TASKSUBJECTDATE_FORMAT,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_TIMEZONEGMT);                    
                    Task taskObj = new Task();
                    taskObj.WhoId = intObj.Account__r.PersonContactId;
                    taskObj.Subject = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_FOLLOWUP+intObj.Reason__r.Name + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SPACED_HYPHEN + formattedStartDate;
                    taskObj.OwnerId = UserInfo.getUserId();
                    taskObj.Status = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKNSSTATUS;
                    taskObj.CRM_Retail_Location__c=intObj.Location__r.Id;
                    taskObj.Description = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NOTAPPLICABLE; 
                    taskObj.ActivityDate = getRequiredDueDate(businessHourObj,intObj.Storefront_Event_Starttime__c,3);
                    taskObj.Priority = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NORMAL;
                    taskObj.whatId = intObj.Account__r.Id; 
                    taskObj.CRMRetail_Automated__c=false; 
                    taskObj.recordTypeId = crmretailTaskRecordType;                                
                    tasksToInsert.add(taskObj);           
                } 
            }
            CRM_GenericHelper_H_HUM.accessibilityOutput accessbilityObj  = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.CREATABLE, tasksToInsert);             
            if(accessbilityObj.exceptionGenerated)
            {   
                HUMExceptionHelper.logErrors(accessbilityObj.exceptionToLog, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EVENTRECOMMENDATION_CLASS,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATETASK);
                Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EVENTRECOMMENDATION_CLASS, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATETASK, accessbilityObj.exceptionToLog.getMessage());
            }
            else if(accessbilityObj.isObjectAccessible && accessbilityObj.areFieldsAccessible)
            {                
                Database.SaveResult[] lstSaveResult = Database.insert(tasksToInsert,false);
                String sError='';
                for (Database.SaveResult validrec : lstSaveResult)
                {                     
                     for(Database.Error err : validrec.getErrors())
                        {                    
                            sError =  err.getMessage();  
                            HUMExceptionHelper.logError(sError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EVENTRECOMMENDATION_CLASS, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATETASK,'','');
                        }                    
                } 
            }            
            else
            {
                String objectError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK;
                String fieldError = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;                           
                String logError = (!accessbilityObj.isObjectAccessible) ? objectError : fieldError;
                HUMExceptionHelper.logError(logError,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EVENTRECOMMENDATION_CLASS, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATETASK,'','');
                Throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EVENTRECOMMENDATION_CLASS,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATETASK, logError);
            }            
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EVENTRECOMMENDATION_CLASS, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATETASK);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_EVENTRECOMMENDATION_CLASS, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATETASK, ex.getMessage());
        }       
    }
    /*
    Component Name   : RecommendationEventsWrapper
    Version          : 1.0
    Created On       : 10/06/2021
    Function         : this class is used to hold the required attributes for Recommendation flow
    
    Modification Log: 
    * Developer Name           Code Review                 Date                         Description
    *****************************************************************************************************************************
    * Vinoth L											   08/01/2022					Initial Version
    **************************************************************************************************************************** */
    @TestVisible
    public class RecommendationEventsWrapper{
        @TestVisible @AuraEnabled public String ReasonId;
        @TestVisible @AuraEnabled public boolean IsAllDayEvent;
        @TestVisible @AuraEnabled public DateTime StartDateTime;
        @TestVisible @AuraEnabled public DateTime EndDateTime;
        @TestVisible @AuraEnabled public String ReasonName;
        @TestVisible @AuraEnabled public boolean IsvirtualEvent;
    }        
}