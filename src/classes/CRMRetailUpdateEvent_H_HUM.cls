/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailUpdateEvent_H_HUM 
* Version          : 1.0
* Created Date     : 8th June, 2021
* Function         : Handler class for handling and preparing response.
* Test Class       : CRMRetailUpdateEvent_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Sahil Verma                                                       06/08/2021           Original Version 2361199
* Kunal Gajbhiye                                                    03/04/2024           Fixed CheckMarks issue on Line 166
**********************************************************************************************************************************/
public with sharing class CRMRetailUpdateEvent_H_HUM 
{
    private static CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM response = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM(); 
    private static CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM(); 
    private static List<Event> listOfEvent = new list<Event>();
    private static list<Storefront_Location__c> listOfLocation = new list<Storefront_Location__c>();
    
	/**
    * CRMRetailUpdateEvent_H_HUM Method constructor.
    * <p>
    * This method is used to call the handleRequest method.
    *
    * @param        oRequest           Deserialized request                             
    * @return        NA
    */
    public CRMRetailUpdateEvent_H_HUM (CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM oRequest)
    {   
        if(oRequest != null)
        {
            request = oRequest;             
            handleRequest();
        }
        else
        {
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG);
            prepareResponse();
        }
    }
	
    /**
    * handleRequest Method.
    * <p>
    * This method is called from constructor and it handle the request received.
    *
    * @param            NA                                 
    * @return           NA                 
    */
    private void handleRequest()
    {   
        Boolean flag = false;
        flag = validateEvent();
        if(!flag)
        {
            prepareResponse();
        }
        else            
        {
            updateEventDetails();
        }
    }
	
    /**
    * validateAccount Method.
    * <p>
    * This method is used for validating the Event and check if event exist or not.
    *
    * @param         NA                                     
    * @return        Boolean
    */    
    private Boolean validateEvent()
    {
        Id eventId;
        String locationValue;
        try
        {
            eventId = request.sfEventId.trim();
            listOfEvent = [SELECT Facilitator__c, Guest_Speaker__c, Performance__c, Feedback__c, StartDateTime, EndDateTime, WhatId, Reason__c FROM Event WHERE id =: eventId LIMIT 1];
            if(listOfEvent.isEmpty())
            {
                createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_EVENT);
                return false;
            }
                        
            if (string.isBlank(request.relatedToLocation))
            {
                List<CRMRetail_DefLoc__mdt> locRecord = [Select Location_Name__c from CRMRetail_DefLoc__mdt where DeveloperName =: request.sourceSystem.trim()];
                if(locRecord.size() > 0)
                {
                    locationValue =  locRecord[0].Location_Name__c;
                }
                else
                {
                    createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_LOC_FOUND+request.sourceSystem.trim());
                    return false;
                }
            }
            else
            {
                locationValue = request.relatedToLocation.trim();
            }
            
            listOfLocation = [SELECT id FROM Storefront_Location__c WHERE Name =: locationValue LIMIT 1];
            
            if (listOfLocation == null || listOfLocation.isEmpty())
            {
                createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_LOC_NAME_FOUND+locationValue);
                return false;
            }
        }
        catch(Exception e)
        {
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false, error);
            HUMExceptionHelper.logErrors(e,'CRMRetailUpdateEvent_H_HUM','updateEventDetails');
            return false;
        }
        return true;    
    }
    
    /**
    * updateEventDetails Method.
    * <p>
    * This method is used for updating the event details.
    *
    * @param         NA                                     
    * @return        NA
    */
    private void updateEventDetails()
    {
        Integer facilitatorCharacterLength = Event.Facilitator__c.getDescribe().getLength();
        Integer guestSpeakerCharLength = Event.Guest_Speaker__c.getDescribe().getLength();
        Integer performanceCharacterLength = Event.Performance__c.getDescribe().getLength();
        Integer feedbackCharacterLength = Event.Feedback__c.getDescribe().getLength();
		DateTime existingStartDateTime =  Datetime.valueOfGmt(String.valueOf(listOfEvent[0].StartDateTime));
		DateTime existingEndDateTime =  Datetime.valueOfGmt(String.valueOf(listOfEvent[0].EndDateTime));
		
        if(String.isNotBlank(request.intReasonId) && listOfEvent[0].Reason__c != request.intReasonId.trim())
        {
            listOfEvent[0].Reason__c = request.intReasonId.trim();
        }
		
        if(String.isNotBlank(request.relatedToLocation) && listOfEvent[0].WhatId != listOfLocation[0].id)
        {
            listOfEvent[0].WhatId = listOfLocation[0].id;
        }
				
		if(String.isNotBlank(request.startDateTime) && existingStartDateTime != Datetime.valueOfGmt(request.startDateTime.trim()))
        {
            listOfEvent[0].StartDateTime = Datetime.valueOfGmt(request.startDateTime.trim());
        }
		
        if(String.isNotBlank(request.endDateTime) && existingEndDateTime != Datetime.valueOfGmt(request.endDateTime.trim()))
        {
            listOfEvent[0].EndDateTime = Datetime.valueOfGmt(request.endDateTime.trim());
        }
		
		listOfEvent[0].facilitator__c = String.isNotBlank(request.facilitator) ? request.facilitator.left(facilitatorCharacterLength) : '';
        listOfEvent[0].Guest_Speaker__c = String.isNotBlank(request.guestSpeaker) ? request.guestSpeaker.left(guestSpeakerCharLength) : '';
        listOfEvent[0].Performance__c = String.isNotBlank(request.performance) ? request.performance.left(performanceCharacterLength) : ''; 
        listOfEvent[0].Feedback__c = String.isNotBlank(request.feedback) ? request.feedback.left(feedbackCharacterLength) : '';
        
		try
        {   
            GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE, listOfEvent, 'CRMRetailUpdateEvent_H_HUM', 'updateEventDetails');
            createResponse(true,'');
        }
        catch(Exception e)
        {   
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false, error);
            HUMExceptionHelper.logErrors(e,'CRMRetailUpdateEvent_H_HUM','updateEventDetails');
        }
		
        prepareResponse();   
    }
	
    /**
    * prepareResponse Method.
    * <p>
    * This method is used to prepare the response.
    *
    * @param        NA                                 
    * @return       NA                 
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
    * @param        isSuccess               Boolean                               
    * @param        errorMsg                String                                
    * @return       NA                 
    */
    private void createResponse(Boolean isSuccess, String errorMsg)
    {
        response.isSuccess = isSuccess;
        if(String.isNotBlank(errorMsg))
        {
            response.errorMessage = errorMsg;
        }
    }    
}