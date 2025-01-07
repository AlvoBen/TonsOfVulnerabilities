/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailRegisterParticipant_H_HUM   
* Version          : 1.0
* Created Date     : 14th June, 2021
* Function         : Handler Class for Register Participant for Event web services
* Test Class       : CRMRetailRegisterParticipant_T_HUM
**************************************************************************** 
* Modification Log:
* Developer Name                   Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ekta                			  		 				 06/14/2021             Original Version  -2389054
* Akshay                			  		 			 08/02/2021             Performace improvement
* Vinoth L										  		 06/24/2023		  		User Story 4769775: - T1PRJ0154546 / SF / MF9 Storefront - Carry Forward First Time Visitor to Event Interactions
**********************************************************************************************************************************/
public with sharing class CRMRetailRegisterParticipant_H_HUM  
{
    private static CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM response = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM(); 
    private static CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM(); 
    public static String visitorId = '';
    public static String category = '';
    public static String visitorType = '';	
	/**
    * CRMRetailRegisterParticipant_H_HUM Method constructor.
    * <p>
    * This method is used to call the handlerequest method.
    *
    * @param  		oRequest                             Deserialized request                     		
    * @return		 NA
    */
	public CRMRetailRegisterParticipant_H_HUM(CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM oRequest)
    {  
        if(oRequest != null)
        {
            request = oRequest;
            checkAccountEventDetails();
        }
        else
        {
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG,'');
        }
		
        prepareResponse();
    }
	
    /**
    * checkAccountEventDetails Method .
    * <p>
    * This method is used to check whether Account and event exists for the data in the request.
    *
	* @param  		 NA                                                 		
    * @return		 NA
    */
    private void checkAccountEventDetails()
    {
        String accountId = request.accountId.trim();
        String eventId = request.sfEventId.trim();
        category = request.category.trim();
        Account objAccount;
        
        Set<String> categoryValue = new Set<String>();
        categoryValue.add(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_ONSITE.toUpperCase());
        categoryValue.add(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL.toUpperCase());
			 
        objAccount = CRMRetailVNC_Helper_HUM.returnAccount(accountId);
        
        if(objAccount == null)
        {
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_MATCHING_ACCOUNT,'');
            return;
        }
        
        List<Event> listOfEvent = new List<Event>([Select id, WhatId, Reason__c, Reason__r.name, startDateTime,endDateTime,activityDate,IsAllDayEvent from Event where Id =: eventId Limit 1]);
        
        if(listOfEvent.isEmpty())
        {
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_EVENT,'');
            return;
        }
            
        if(!categoryValue.contains(category.toUpperCase()))
        {
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_ERROR,'');
            return;
        }
        visitorId = CRMRetailVNC_Helper_HUM.getVisitorID(objAccount);
        visitorType = (objAccount.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER) ? objAccount.RecordType.Name : (objAccount.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR) ? GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NON_MEMBER : '';
        processRequest(objAccount,listOfEvent[0]);
    }
	
    /**
    * getAllInteractions Method .
    * <p>
    * This method is used to get the Interactions for the accountId sent in the request.
    *
	* @param  		 objAccount				Account
	* @param  		 objEvent				Event
    * @return		 NA
    */
    private void processRequest(Account objAccount, Event objEvent)
    {   
		List<Storefront_Interaction__c> listOfInteraction = new List<Storefront_Interaction__c>(); 
		Storefront_Interaction__c objCheckInIntVirtual  = new Storefront_Interaction__c();
		Storefront_Interaction__c objCheckInIntOnsite  = new Storefront_Interaction__c();
        Storefront_Interaction__c objEventInt  = new Storefront_Interaction__c();
        Storefront_Interaction__c obj1stCheckInInt  = new Storefront_Interaction__c();
		List<Storefront_Interaction__c> lstInteractionToInsert = new List<Storefront_Interaction__c>();
        Id firstReasonID = CRMRetailVNC_Helper_HUM.returnReasonId(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN);
        
        if(firstReasonID == null)
        {                
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_INT_FOUND + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN,'');
            return;
        }
        
		Map<String, Boolean> mapToCheckDuplicates = new Map<String,Boolean>();
		
		objEventInt = getInteractionData(objEvent,objAccount,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATE_EVENT_INT,objEvent.Reason__c,category);
		
		listOfInteraction = CRMRetailVNC_Helper_HUM.returnInteractions(objAccount.Id);
		
		if(listOfInteraction.isEmpty())
		{
			string categoryValue =  (category == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL) ? GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL : '';  
			
			obj1stCheckInInt = getInteractionData(objEvent,objAccount,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATE_FIRST_CHECK_IN,firstReasonID,categoryValue);
			
            lstInteractionToInsert.add(obj1stCheckInInt);
            lstInteractionToInsert.add(objEventInt);
		}
		else
		{	
			String key = '';
			List<Storefront_Interaction__c> listInts = new List<Storefront_Interaction__c>();
            listInts.add(objEventInt);
            mapToCheckDuplicates = GenericHelper_CRMRetail_H_HUM.checkDuplicateInteractions(listInts);
						
			if (mapToCheckDuplicates != null)
			{				
				key = string.valueOf(objEventInt.Interaction_Date__c.DateGMT())+
                string.valueOf(objEventInt.Reason__c) + string.valueOf(objEventInt.Location__c)+ string.valueOf(objEventInt.Account__c)+ string.valueOf(objEventInt.Visitor_ID__c)+
                string.valueOf(objEventInt.Storefront_Event_Starttime__c) + objEventInt.category__c;
				
				if(mapToCheckDuplicates.containsKey(key) && mapToCheckDuplicates.get(key))
				{
					createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DUPLICATE_ERROR,'');
                    return;
				}
				else
				{					
					listInts.clear();
					map<string,Storefront_Interaction__c> checkInToInsertMap = new map<string,Storefront_Interaction__c>();
					String firstTimekey = '';
					Id checkInReasonId = CRMRetailVNC_Helper_HUM.returnReasonId(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN);
                    
                    if(checkInReasonId == null)
                    {                
                        createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_INT_FOUND + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN,'');
                        return;
                    }
					
					objCheckInIntVirtual = getInteractionData(objEvent,objAccount,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATECHECK_IN,checkInReasonId,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL);
					listInts.add(objCheckInIntVirtual);
					checkInToInsertMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL,objCheckInIntVirtual);
					
					objCheckInIntOnsite = getInteractionData(objEvent,objAccount,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATECHECK_IN,checkInReasonId,'');
					listInts.add(objCheckInIntOnsite);
					checkInToInsertMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_ONSITE,objCheckInIntOnsite);
					
					mapToCheckDuplicates = GenericHelper_CRMRetail_H_HUM.checkDuplicateInteractions(listInts);
					
					for (Storefront_Interaction__c intObj : listInts)
					{
						string checkInCategory ='';
						
						if(intObj.category__c != null)
                    	checkInCategory = string.valueOf(intObj.category__c);
					
						key = string.valueOf(intObj.Interaction_Date__c.DateGMT())+
						string.valueOf(intObj.Reason__c) + string.valueOf(intObj.Location__c)+ string.valueOf(intObj.Account__c)+ string.valueOf(intObj.Visitor_ID__c)+
						string.valueOf(intObj.Storefront_Event_Starttime__c) + checkInCategory;
						
						firstTimekey = string.valueOf(intObj.Interaction_Date__c.DateGMT())+
						firstReasonID + string.valueOf(intObj.Location__c)+ string.valueOf(intObj.Account__c)+ string.valueOf(intObj.Visitor_ID__c)+
						string.valueOf(intObj.Storefront_Event_Starttime__c)+ checkInCategory; 
						
						if(mapToCheckDuplicates.containsKey(key) && mapToCheckDuplicates.get(key))
						{
							lstInteractionToInsert.add(objEventInt);
							break;
						}
						else if(mapToCheckDuplicates.containsKey(firstTimekey) && mapToCheckDuplicates.get(firstTimekey))
						{
							lstInteractionToInsert.add(objEventInt);
							break;
						}						
					}
					
					if (lstInteractionToInsert.isEmpty())
					{
                        lstInteractionToInsert.add(checkInToInsertMap.get(category));
						lstInteractionToInsert.add(objEventInt);
					}
				}
				
			}
			else
            {
                createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERAL_ERROR,'');
                return;
            }			
		}
		
		if (!lstInteractionToInsert.isEmpty())
		{
			try
			{
                insert lstInteractionToInsert;
			}
			catch(Exception e)
			{
				String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
				createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERAL_ERROR + error ,'');
				HUMExceptionHelper.logErrors(e,'CRMRetailRegisterParticipant_H_HUM','processRequest'); 
				return;
			}
		}
                    
		String interactionId = CRMRetailVNC_Helper_HUM.returnInteractionId(lstInteractionToInsert);
		
		if(interactionId != null)
		{
			createResponse(true,'',interactionId);
			return;
		}
		else
		{
			createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERAL_ERROR,'');
			return;	
		}       
    }
	
    /**
    * getInteractionData Method .
    * <p>
    * This method is used to create the interactions.
    *
	* @param  		 objEvent								Event 
	* @param  		 objAccount								Account
	* @param  		 mapInteractionWithStatus				map
    * @return		 NA		
    */    
    public static Storefront_Interaction__c getInteractionData(Event objEvent, Account objAccount,String typeOfInteraction,Id reasonId,string categoryValue) 
    {        
        try{
			if (String.isNotBlank(typeOfInteraction) && reasonId != null && objAccount != null && objEvent != null)
			{	
				String categoryToStore = '';
				DateTime eventStartTime;
				DateTime eventEndTime;
				DateTime interactionDate;
				Boolean isFirstTime = false;
				Boolean isAllDayEvent  = false;
				Storefront_Interaction__c objInteraction = new Storefront_Interaction__c();
				
				Id location = objEvent.WhatId;				
                if(typeOfInteraction.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CHECK_IN))
                {
                    interactionDate = System.Now(); 
					categoryToStore = categoryValue;					
                    if (!HUMUtilityHelper.isCRMFunctionalityON('4769775') && typeOfInteraction == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATE_FIRST_CHECK_IN)
                    {
                        isFirstTime = true;                        
                    }
                }
				else if (typeOfInteraction == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CREATE_EVENT_INT)
				{
					if(objEvent.IsAllDayEvent)
					{
						eventStartTime = objEvent.StartDateTime.addDays(1).addHours(4).addMinutes(59);
						eventEndTime = objEvent.endDateTime.addDays(1).addHours(4).addMinutes(59);
                        Integer year = objEvent.StartDateTime.yearGMT();
                        Integer month = objEvent.StartDateTime.monthGMT();
                        Integer intDate = objEvent.StartDateTime.dayGMT();
                        interactionDate = Datetime.newInstance(year,month,intDate,12,0,0);
						isAllDayEvent = true;
					}
					else
					{
						eventStartTime = objEvent.StartDateTime;
						eventEndTime = objEvent.endDateTime;
                        interactionDate = eventStartTime;
					}					
					categoryToStore = (category == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL) ? GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL : GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE;
				}
				
				objInteraction = CRMRetailVNC_Helper_HUM.createInteraction(objAccount.Id,visitorType,categoryToStore,eventStartTime,eventEndTime,interactionDate,isFirstTime,reasonId,location,visitorId,isAllDayEvent);
				return objInteraction;
			}
            else
            {
            	createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERAL_ERROR,'');
            	return null;
            }
        }
        catch(Exception e)
        {
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERAL_ERROR + error ,'');
            HUMExceptionHelper.logErrors(e,'CRMRetailRegisterParticipant_H_HUM','getInteractionData'); 
            return null;
        }
    } 
	
    /**
    * prepareResponse Method.
    * <p>
    * This method is used to prepare the response.
    *
    * @param  		response	response			                                 
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
    * @param  		errorMsg 				error message  
    * @param		interactionId           interaction id 
    * @param		response          		response                  
    * @return 		NA                 
    */
    public static void createResponse(Boolean isSuccess, String errorMsg, String interactionId)
    {
        response.isSuccess = isSuccess;
        response.interactionId = interactionId;
        
        if(String.isNotBlank(errorMsg))
        {
            response.errorMessage = errorMsg;
        }
    } 
}