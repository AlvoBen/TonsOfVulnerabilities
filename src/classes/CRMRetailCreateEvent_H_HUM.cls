/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailCreateEvent_H_HUM 
* Version          : 1.0
* Created Date     : 7th June, 2021
* Function         : Handler class for handling and preparing response.
* Test Class       : CRMRetailCreateEvent_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Isha Gupta                                  					       06/07/2021              Original Version  2361161
**********************************************************************************************************************************/
public with sharing class CRMRetailCreateEvent_H_HUM 
{
    private static CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM oRequests = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
    private static CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM  response = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
    private static Id intReasonId;
    private static Id locationId;
    private static Id eventRecordTypeId = Schema.SObjectType.Event.getRecordTypeInfosByDeveloperName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENT_REC_TYPE_DEV_NAME).getRecordTypeId();
    private static Boolean NoRecordFlag = false;
    private static set<string> validFrequencyValues = new set<string>{GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DAILY,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_WEEKLY,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MONTHLY};
        private static set<String> createEventMonthDays = new set<String>();
    private static set<integer> validFrequencyIntValues = new set<integer>{1,2};
        private static set<string> createEventWeekDays = new set<string>();
    private static set<string> validWeekdayValues = new set<string>{GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SUN,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MON,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TUE,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_WED,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_THU,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_FRI,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SAT};
        
        
        /**
	* CRMRetailCreateEvent_H_HUM Method constructor.
	* <p>
	* This method is used to call the prepareResponse method.
	*
	* @param  		oRequest           Deserialized request                     		
	* @return		 NA
	*/           
        public CRMRetailCreateEvent_H_HUM (CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM oRequest)
    {
        try{
            if (oRequest != null )
            {
                oRequests=oRequest;
                insertEvents();
            }
            else
            {
                createResponse(False,'',GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG);
            }
        }
        catch(Exception e)
        {
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(False,'',error);
            HUMExceptionHelper.logErrors(e,'CRMRetailCreateEvent_H_HUM','createNewEvent');
        }
        finally{
            prepareResponse();
        }
    }
    
    /**
	* insertEvents Method.
	* <p>
	* This method is used for inserting event records
	*
	* @param  		 NA                               		
	* @return		 NA 
	*/	
    public void insertEvents(){
        string isValidRequest = validateRequest();
        If (isValidRequest == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VALID){
            Set<event> eventsToInsertset = processRequest();
            List<event> eventsToInsert = new List<event>();
            eventsToInsert.addAll(eventsToInsertset);
            if (eventsToInsert.size() > 0 ){
                insert eventsToInsert; 
                
                String eventIdcreated = '';
                for (event e : eventsToInsert)
                {
                    if (string.isBlank(eventIdcreated)){
                        eventIdcreated = String.valueOf(e.id)+',';
                    }
                    else
                    {
                        eventIdcreated +=  String.valueOf(e.id)+',';
                    }
                }
                eventIdcreated = eventIdcreated.substringBeforeLast(',');
                
                createResponse(true,eventIdcreated,'');
            }
            else if(NoRecordFlag == true)
            {					
                createResponse(false,'', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_EVENTCREATED);
            }else
            {
                createResponse(false,'', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTCREATION_FAILED); 
            }            
        }
        else{
            createResponse(false,'', isValidRequest);
        }
    }
    
    /**
	* validateRequest Method.
	* <p>
	* This method is used for validating the request.
	*
	* @param  		 NA                               		
	* @return		 STRING 
	*/    
    private static string validateRequest()
    {
        string message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VALID;
        string locationValue = '';
        string interactionReasonValue ='';
        
        if(!string.isBlank(oRequests.intReasonId))
        {
            interactionReasonValue = oRequests.intReasonId.trim();
        }
        List<Storefront_I_Reason__c>  intReason = new List<Storefront_I_Reason__c>([select id from Storefront_I_Reason__c where Id =: interactionReasonValue and Inactive__c = false limit 1]);
        if (intReason.size() > 0)
        {
            intReasonId = intReason[0].id;
        }
        else
        {
            message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_INT_FOUND+interactionReasonValue;
            return message;
        }
        
        if (string.isBlank(oRequests.relatedToLocation))
        {
            List<CRMRetail_DefLoc__mdt> locRecord = [Select Location_Name__c from CRMRetail_DefLoc__mdt where DeveloperName =: oRequests.sourceSystem];
            if(locRecord.size() > 0){
                locationValue =  locRecord[0].Location_Name__c;
            }else
            {
                message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_LOC_FOUND+oRequests.sourceSystem;
                return message;
            }
        }
        else
        {  
            locationValue = oRequests.relatedToLocation.trim();
        }	
        list<Storefront_Location__c>  location =  new list<Storefront_Location__c> ([select id from Storefront_Location__c where Name =: locationValue and Inactive_indicator__c = false limit 1]);
        if (location.size() > 0)
        {
            locationId = location[0].id;
        }
        else
        {
            message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_LOC_NAME_FOUND+locationValue;
            return message;
        }	
        
        if(Datetime.valueOfGmt(oRequests.endDateTime.trim()) < Datetime.valueOfGmt(oRequests.startDateTime.trim())){
            message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENT_END_DATE_ERROR;
            return message;
        }
        
        if (oRequests.isRepeat)
        {
            if (string.isBlank(oRequests.frequency) || !validFrequencyValues.contains(oRequests.frequency.trim())){
                message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_FREQUENCY_ERROR;
                return message;
            }
            else
            {
                if (oRequests.frequency.trim().toUpperCase() == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DAILY)
                {
                    if (oRequests.frequecyInterval == null || !validFrequencyIntValues.contains(oRequests.frequecyInterval))
                    {
                        message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_FREQUENCY_INTERVAL_ERROR;
                        return message;
                    }	
                }
                else if (oRequests.frequency.trim().toUpperCase() == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_WEEKLY)
                {
                    if (string.isBlank(oRequests.repeatWeekDays))
                    {
                        message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_REPEAT_WEEKDAYS_ERROR;
                        return message;
                    }
                    else
                    {
                        string repeatDays = oRequests.repeatWeekDays.trim().toUpperCase();
                        createEventWeekDays = new Set<String>(repeatDays.split(','));
                        
                        if (createEventWeekDays.size() > 6)
                        {
                            message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ALLWEEKDAYS_ERROR;
                            return message;
                        }
                        else if (createEventWeekDays.size() < 1)
                        {
                            message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ATLEAST_ONEWEEKDAY_ERROR;
                            return message;
                        }
                        else
                        {
                            for( string day : createEventWeekDays)
                            {
                                if(!validWeekdayValues.contains(day))
                                {
                                    message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_WEEKDAY+ day;
                                    return message;
                                }
                            }
                            
                        }
                    }
                } 
                else if(oRequests.frequency.trim().toUpperCase() == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MONTHLY)
                {
                    if (string.isBlank(oRequests.repeatMonthDates))
                    {
                        message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_REPEAT_MONTHDATES_ERROR;
                        return message;
                    }
                    else
                    {
                        string repeatMonths = oRequests.repeatMonthDates;
                        createEventMonthDays = new Set<String>(repeatMonths.split(','));
                        if(createEventMonthDays.size() > 31){
                            message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ALLMONTHDAYS_ERROR;
                            return message;
                        }
                        else if(createEventMonthDays.size() < 1)
                        {
                            message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ATLEAST_ONEMONTHDAY_ERROR;
                            return message;
                        }
                        else
                        {
                            for(String d:createEventMonthDays )
                            {
                                if(( Integer.valueOf(d) < 1) || (Integer.valueOf(d) > 31)){
                                    message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_MONTHDAY+ d;
                                    return message;  
                                }
                            }
                        }
                        
                    }
                    
                }
            }
            
            if (oRequests.numberOfRepeats == null && string.isBlank(oRequests.repeatEndDate))
            {
                message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_REPEATS_ENDDATE_ERROR;
                return message;
            }
            else{
                if (oRequests.numberOfRepeats != null )
                {
                    if((oRequests.frequency.trim().toUpperCase() == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DAILY) && (oRequests.numberOfRepeats < GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MINLIMIT || oRequests.numberOfRepeats >GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DAILY_MAXLIMIT ) )
                    {
                        message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DAILY_LIMIT_ERROR;
                        return message;
                    }
                    else if((oRequests.frequency.trim().toUpperCase() == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_WEEKLY) && (oRequests.numberOfRepeats < GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MINLIMIT || oRequests.numberOfRepeats >GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_WEEKLY_MAXLIMIT ))
                    {
                        message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_WEEKLY_LIMIT_ERROR;
                        return message;
                    }
                    else if((oRequests.frequency.trim().toUpperCase() == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MONTHLY) && (oRequests.numberOfRepeats < GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MINLIMIT || oRequests.numberOfRepeats >GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MONTHLY_MAXLIMIT ) )
                    {
                        message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MONTHLY_LIMIT_ERROR;
                        return message;
                    }
                }
                else if (string.isNotBlank(oRequests.repeatEndDate.trim()))
                {
                    date repeatEndDateValue =  Date.valueOf(oRequests.repeatEndDate.trim());
                    date startDateValue =  Datetime.valueOfGmt(oRequests.startDateTime.trim()).dateGMT();
                    if ( startDateValue.daysBetween(repeatEndDateValue) < 0)
                    {
                        message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_REPEAT_END_DATE_ERROR;
                        return message;
                        
                    }else if(repeatEndDateValue > startDateValue.addYears(2) ){
                        message = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_REPEAT_END_DATE_LIMIT_ERROR;
                        return message;
                    }
                }
            }
        }
        return message;
    }
    
    
    /**
	* processRequest Method.
	* <p>
	* This method is used for processing the request.
	*
	* @param  		 NA                               		
	* @return		 Set<EVENT> 
	*/
    private static Set<event> processRequest(){
        Set<event> eventsToCreate = new Set<event>();
        DateTime startDateTime = DateTime.valueOfGmt(oRequests.startDateTime.trim());
        DateTime endDateTime =  DateTime.valueOfGmt(oRequests.endDateTime.trim());
        
        if (oRequests.isRepeat)
        {
            integer frequecyInterval = oRequests.frequecyInterval;
            string frequency = oRequests.frequency.trim().toUpperCase();
            integer numberOfRepeats = oRequests.numberOfRepeats;
            date startDate =  DateTime.valueOfGmt(oRequests.startDateTime.trim()).dateGMT();
            date repeatEndDate = null;
            if(string.isNotBlank(oRequests.repeatEndDate)){
                repeatEndDate =   Date.valueOf(oRequests.repeatEndDate.trim());
            }
            
            if(frequency == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DAILY){
                if(numberOfRepeats != null){
                    for(Integer i = 0; i < numberOfRepeats; i++){
                        eventsToCreate.add(CreateEventDaily(startDateTime,endDateTime));
                        startDateTime = startDateTime.addDays(frequecyInterval);
                        endDateTime = endDateTime.addDays(frequecyInterval);
                    }
                    
                }else if(repeatEndDate != null){
                    if(startDate <= repeatEndDate){
                        while(startDate <= repeatEndDate){
                            eventsToCreate.add(CreateEventDaily(startDateTime,endDateTime));
                            startDateTime = startDateTime.addDays(frequecyInterval);
                            endDateTime = endDateTime.addDays(frequecyInterval);
                            startDate = startDate.addDays(frequecyInterval);
                        }
                    }
                }
            }
            
            else if (frequency == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_WEEKLY){
                
                if(numberOfRepeats != null){
                    while(numberOfRepeats > 0){
                        if(createEventWeekDays.contains(startDateTime.format('EEE').toUpperCase())){
                            Event ev = createEventRec();
                            ev.StartDateTime = startDateTime;
                            ev.ActivityDate = ev.StartDateTime.dateGMT();
                            ev.EndDateTime = endDateTime;                
                            eventsToCreate.add(ev);
                            
                            startDateTime = startDateTime.addDays(1);
                            endDateTime = endDateTime.addDays(1);
                            numberOfRepeats--;
                        }else{
                            startDateTime = startDateTime.addDays(1);
                            endDateTime = endDateTime.addDays(1); 
                            continue;
                        }
                    }
                } 
                else if(repeatEndDate != null){
                    while(startDate <= repeatEndDate){
                        if(createEventWeekDays.contains(startDateTime.format('EEE').toUpperCase()))  {
                            Event ev = createEventRec();
                            ev.StartDateTime = startDateTime;
                            ev.ActivityDate = ev.StartDateTime.dateGMT();
                            ev.EndDateTime = endDateTime;
                            eventsToCreate.add(ev);
                            
                            startDateTime = startDateTime.addDays(1);
                            endDateTime = endDateTime.addDays(1);
                            startDate = startDate.addDays(1);
                            
                        }else{
                            startDate = startDate.addDays(1);
                            startDateTime = startDateTime.addDays(1);
                            endDateTime = endDateTime.addDays(1);
                            continue;
                        }
                    }
                }
            }
            
            else if (frequency == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MONTHLY){
                Integer monthVal = startDateTime.monthGmt();
                Integer yearVal = startDateTime.yearGmt();
                Integer hourVal = startDateTime.hourGmt();
                Integer minutesVal = startDateTime.minuteGmt();
                Integer secondsVal = startDateTime.secondGmt();
                Integer endDateHourVal = endDateTime.hourGmt();
                Integer endDateMinutesVal = endDateTime.minuteGmt();
                Integer endDateSecondsVal = endDateTime.secondGmt();
                Integer dateDiff = 0;
                DateTime requestStartDateTime = startDateTime;
                
                
                if(startDateTime.dateGMT() != endDateTime.dateGMT()){
                    dateDiff = startDateTime.dateGMT().daysBetween(endDateTime.dateGMT());
                }
                
                if(numberOfRepeats != null){
                    while(numberOfRepeats > 0){
                        
                        monthVal = startDateTime.monthGmt();
                        yearVal = startDateTime.yearGmt(); 
                        
                        for(String str : createEventMonthDays){
                            if(numberOfRepeats > 0){
                                if(Integer.valueOf(str) > Date.daysInMonth(yearVal, monthVal)) {
                                    
                                    DateTime startdateval = DateTime.newInstanceGmt(yearVal, monthVal, Date.daysInMonth(yearVal, monthVal), hourVal, minutesVal, secondsVal);
                                    DateTime enddatevalue = DateTime.newInstanceGmt(yearVal, monthVal,Date.daysInMonth(yearVal, monthVal),endDateHourVal, endDateMinutesVal, endDateSecondsVal); 
                                    
                                    if(dateDiff > 0){
                                        enddatevalue = enddatevalue.addDays(dateDiff);
                                    }
                                    
                                    Event evt = CreateEvents(requestStartDateTime,startdateval,enddatevalue,true,numberOfRepeats,null);
                                    if(evt != null) { 
                                        if(!eventsToCreate.contains(evt)){
                                            eventsToCreate.add(evt);
                                            numberOfRepeats--;
                                        }
                                    }
                                }
                                else
                                {
                                    DateTime startdateval = DateTime.newInstanceGmt(yearVal, monthVal, Integer.valueOf(str), hourVal, minutesVal, secondsVal);
                                    DateTime enddatevalue = DateTime.newInstanceGmt(yearVal, monthVal,Integer.valueOf(str),endDateHourVal, endDateMinutesVal, endDateSecondsVal);
                                    
                                    if(dateDiff > 0){
                                        enddatevalue = enddatevalue.addDays(dateDiff);
                                    }									
                                    
                                    Event evt = CreateEvents(requestStartDateTime,startdateval,enddatevalue,true,numberOfRepeats,null);
                                    if(evt != null)
                                    {
                                        if(!eventsToCreate.contains(evt))
                                        {
                                            eventsToCreate.add(evt);
                                            numberOfRepeats--;
                                        }
                                    }                                    
                                }
                            }
                        }
                        if( numberOfRepeats > 0){
                            startDateTime = startDateTime.addmonths(1);
                        }
                    }
                }                
                else if(repeatEndDate != null)
                {
                    Integer monthsBetween = startDate.monthsBetween(repeatEndDate)+1;

                    while(monthsBetween > 0 )
                    {
                        monthVal = startDateTime.monthGmt();
                        yearVal = startDateTime.yearGmt(); 
                        
                        for(String str : createEventMonthDays){
                            if(Integer.valueOf(str) > Date.daysInMonth(yearVal, monthVal)) 
                            {
                                DateTime startdateval = DateTime.newInstanceGmt(yearVal, monthVal, Date.daysInMonth(yearVal, monthVal), hourVal, minutesVal, secondsVal);
                                DateTime enddatevalue = DateTime.newInstanceGmt(yearVal, monthVal,Date.daysInMonth(yearVal, monthVal),endDateHourVal, endDateMinutesVal, endDateSecondsVal);
                                
                                if(dateDiff > 0){
                                    enddatevalue = enddatevalue.addDays(dateDiff);
                                }
								
                                Event evt = CreateEvents(requestStartDateTime,startdateval,enddatevalue,false,null,repeatEndDate);
                                if(evt != null) {
                                    if(!eventsToCreate.contains(evt)){
                                        eventsToCreate.add(evt);
                                    }
                                }
								
                            }
                            else
                            {
                                DateTime startdateval = DateTime.newInstanceGmt(yearVal, monthVal, Integer.valueOf(str), hourVal, minutesVal, secondsVal);
                                DateTime enddatevalue = DateTime.newInstanceGmt(yearVal, monthVal,Integer.valueOf(str),endDateHourVal, endDateMinutesVal, endDateSecondsVal);
                                
                                if(dateDiff > 0){
                                    enddatevalue = enddatevalue.addDays(dateDiff);
                                }
								
                                Event evt = CreateEvents(requestStartDateTime,startdateval,enddatevalue,false,null,repeatEndDate);
                                if(evt != null) {
                                    if(!eventsToCreate.contains(evt)){
                                        eventsToCreate.add(evt);
                                    }
                                }
                            }
                        }
                        
                        monthsBetween = monthsBetween - 1;
                        
                        if (monthsBetween >0 )
                        {
                            startDateTime = startDateTime.addmonths(1);
                        }
                    }
                }
            } 
        }
        
        else {
            Event ev = createEventRec();
            ev.StartDateTime = startDateTime;
            ev.ActivityDate = ev.StartDateTime.dateGMT();
            ev.EndDateTime = endDateTime;                
            eventsToCreate.add(ev);
        }
        
        if(eventsToCreate.isEmpty())
        {
            NoRecordFlag = true;
        }
        
        return eventsToCreate;
    }
    
    /**
	* createEventRec Method.
	* <p>
	* This method is used for creating instance of Event Records.
	*
	* @param  		 NA                               		
	* @return		 EVENT 
	*/		
    private static event createEventRec(){
        Event eventRef = new Event();
        eventRef.recordTypeId = eventRecordTypeId; 
        eventRef.Subject = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENT_SUBJECT;
        eventRef.Reason__c = intReasonId;
        eventRef.WhatId = locationId;
        eventRef.IsAllDayEvent = oRequests.isAllDayEvent;
        eventRef.Facilitator__c = string.isNotBlank(oRequests.facilitator) ? oRequests.facilitator:'';
        eventRef.Guest_Speaker__c = string.isNotBlank(oRequests.guestSpeaker) ? oRequests.guestSpeaker:'';
        eventRef.Performance__c = string.isNotBlank(oRequests.performance) ? oRequests.performance:'';
        eventRef.Feedback__c = string.isNotBlank(oRequests.feedback) ? oRequests.feedback:'';
        return eventRef;
    }
    
    /**
	* CreateEvents Method.
	* <p>
	* This method is used for creating events monthly.
	*
	* @param  		 DateTime startDateTime,Integer yearVal,Integer monthVal,Integer dayval,Integer hourVal,Integer minutesVal,Integer secondsVal,Integer endDateYearVal,Integer endDateMonthVal,Integer endDateHourVal,Integer endDateMinutesVal,Integer endDateSecondsVal,Integer numberOfRepeats,Boolean repeatCase, Boolean enddateCase,Date repeatEndDate                               		
	* @return		 EVENT 
	*/
    private static Event CreateEvents(DateTime startDateTime,DateTime startdateval,DateTime enddatevalue,Boolean repeatCase,Integer numberOfRepeats,Date repeatEndDate)
    {
        Event ev = null;
		
        if(repeatCase)
        {
            if((startdateval >= startDateTime) && (numberOfRepeats != 0)){
                ev = createEventRec();
                ev.StartDateTime = startdateval;
                ev.ActivityDate = ev.StartDateTime.dateGMT();
                ev.EndDateTime = enddatevalue; 
            } 
        }
        else{
            
            if((startdateval >= startDateTime) && (startdateval.dateGMT() <= repeatEndDate)){
				ev = createEventRec();
                ev.StartDateTime = startdateval;
                ev.ActivityDate = ev.StartDateTime.dateGMT();
                ev.EndDateTime = enddatevalue;
			}
        }
        return ev;
    }
    
    /**
	* CreateEventDaily Method.
	* <p>
	* This method is used for creating events daily.
	*
	* @param  		 DateTime startDateTime,DateTime endDateTime,Integer frequecyInterval                            		
	* @return		 EVENT 
	*/    
    private static Event CreateEventDaily(DateTime startDateTime,DateTime endDateTime)
    {
        Event ev = createEventRec();
        ev.StartDateTime = startDateTime;
        ev.ActivityDate = ev.StartDateTime.dateGMT();
        ev.EndDateTime = endDateTime;
        return ev;
    }
    
    /**
	* prepareResponse Method.
	* <p>
	* This method is used to prepare the response.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/
    private void prepareResponse()
    { 
        String createResponse = System.JSON.serialize(response);
        RestResponse res = RestContext.response;
        res.statusCode = GLOBAL_CONSTANT_STOREFRONT_HUM.INT_CRMRETAIL_STATUSCODE;
        res.addHeader(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CONTENTTYPE, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_APPLICATION_JSON);
        res.responseBody = Blob.valueOf(createResponse);
    }
    
    /**
	* createResponse Method.
	* <p>
	* This method is used to create the response which will be sent on the service call.
	*
	* @param  		isSuccess 				boolean variable which holds the success or failure of the request 
	* @param  		eventId				    comma separated string of eventids for created events                                                               
	* @param  		errorMsg 				error message                                
	* @return 		NA                 
	*/
    private void createResponse(Boolean isSuccess,string eventId,string errorMessage)
    {
        response.isSuccess = isSuccess ;
        response.eventId = eventId;
        response.errorMessage = errorMessage;
        
        if(String.isNotBlank(errorMessage))
        {
            response.errorMessage = errorMessage;
        }
    }   
}