/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailMarkAttandance_H_HUM 
* Version          : 1.0
* Created Date     : 25th June, 2021
* Function         : Handler Class for marking attandance for Event web services
* Test Class       : CRMRetailMarkAttandance_T_HUM
**************************************************************************** 
* Modification Log:
* Developer Name                   Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ekta                			  		 				 06/25/2021             Original Version  2389191
**********************************************************************************************************************************/
public  with sharing class CRMRetailMarkAttandance_H_HUM 
{
    private static CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM response = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
    private static CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
    String visitorId = '';
	
    /**
    * CRMRetailMarkAttandance_H_HUM Method constructor.
    * <p>
    * This method is used to call the handlerequest method.
    *
    * @param  		oRequest                             Deserialized request                     		
    * @return		 NA
    */
	public CRMRetailMarkAttandance_H_HUM(CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM oRequest)
    {  
        if(oRequest != null)
        {
            request = oRequest;
            checkEventType();
        }
        else
        {
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG,'');
        }
        prepareResponse();
    }
	
    /**
    * checkEventType Method .
    * <p>
    * This method is used to get check where the event is a video event or not.
    *
	* @param  		 NA								
	* @return		 NA								
    */
    private void checkEventType() 
    {
        Boolean isVideoEvent = request.isVideoEvent;
        
        if(!isVideoEvent)
		{
            if(String.isBlank(request.interactionId))
            {
                createResponse(false, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INTERACTION_MISSING ,'');
                return;
            }
            isNotVideoEvent();
        }
		else
		{	
            if(String.isBlank(request.intReasonId))
            {
                createResponse(false, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_REASON_MISSING ,'');
                return;
            }
            if(String.isBlank(request.accountId))
            {
                createResponse(false, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT_MISSING,'');
                return;
            }
            isVideoEvent();
        }
		
        return;
    }
	
    /**
    * isVideoEvent Method .
    * <p>
    * This method is used to get check the duplicates in case of isVideoEvent is True if any and create a attended interaction along with a check in interaction.
    *
	* @param  		 NA								
	* @return		 NA								
    */
    private void isVideoEvent()
    {
        try{
            Id reasonId = request.intReasonId.trim();
			Id accountId = request.accountId.trim();
			string sourceSystem = request.sourceSystem.trim();
            List<Storefront_I_Reason__c> listOfReason = new List<Storefront_I_Reason__c>();
            List<Storefront_Location__c> listOfLocation = new List<Storefront_Location__c>();
            Map<String,Boolean> mapInteractionWithStatus = new Map<String,Boolean>(); 
            Storefront_Interaction__c objInteraction = new Storefront_Interaction__c();
            
			String locationValue = '';
			List<Storefront_Interaction__c> lstInteraction = new List<Storefront_Interaction__c>();
            List<Storefront_Interaction__c> lstInteractionToInsert = new List<Storefront_Interaction__c>();
            
            Map<String, Boolean> mapToCheckDuplicates = new Map<String,Boolean>();
            
            Storefront_Interaction__c objRecordedInteraction = new Storefront_Interaction__c();
            Storefront_Interaction__c objCheck1stInt  = new Storefront_Interaction__c();
			
            listOfReason = [Select id, Name, Inactive__c from Storefront_I_Reason__c where Id =: reasonId AND Inactive__c = false LIMIT 1];
            
            if(listOfReason.isEmpty())
            {
                createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_INT_FOUND  + reasonId,'');
                return;
            }
           
            List<CRMRetail_DefLoc__mdt> locRecord = [Select Location_Name__c from CRMRetail_DefLoc__mdt where DeveloperName =: sourceSystem];
            
            if(locRecord.size() > 0){
                locationValue =  locRecord[0].Location_Name__c;
            }
            
            listOfLocation = [Select id, Name, Inactive_indicator__c from Storefront_Location__c where name =: locationValue AND Inactive_indicator__c = false LIMIT 1];
            
            if(listOfLocation.isEmpty())
            {
                createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_LOC_FOUND +  sourceSystem,'');
                return;
            }
			
            Account objAccount = CRMRetailVNC_Helper_HUM.returnAccount(accountId);
            
            if(objAccount == null)
            {
				createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_MATCHING_ACCOUNT,'');
                return;
            }
			
			visitorId = CRMRetailVNC_Helper_HUM.getVisitorID(objAccount);
            String visitorType = (objAccount.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER) ? objAccount.RecordType.Name : (objAccount.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR) ? GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NON_MEMBER : '';
            
			Id firstReasonID = CRMRetailVNC_Helper_HUM.returnReasonId(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN);
            
            if(firstReasonID == null)
            {                
                createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_INT_FOUND + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN,'');
                return;
            }

			objRecordedInteraction = CRMRetailVNC_Helper_HUM.createInteraction(objAccount.Id, visitorType, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_RECORDED, null, null, System.Now(), false, reasonId, listOfLocation[0].Id, visitorId, false);
			
            lstInteraction = CRMRetailVNC_Helper_HUM.returnInteractions(objAccount.Id);

            if(lstInteraction.isEmpty())
            {
                objCheck1stInt = CRMRetailVNC_Helper_HUM.createInteraction(objAccount.Id, visitorType , GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL, null,null, System.Now() , false ,firstReasonID,listOfLocation[0].Id,visitorId,false);
				
                lstInteractionToInsert.add(objCheck1stInt);
                lstInteractionToInsert.add(objRecordedInteraction);
            }
            else
            {
                List<Storefront_Interaction__c> listInts = new List<Storefront_Interaction__c>();
                listInts.add(objRecordedInteraction);
				Id visitorReasonId = CRMRetailVNC_Helper_HUM.returnReasonId(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN);
                
                if(visitorReasonId == null)
                {                
                    createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_INT_FOUND + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN,'');
                    return;
                }

				String key = ''; 
				String firstTimekey = '';				
				Storefront_Interaction__c objCheckInInt  = new Storefront_Interaction__c();
                Boolean createCheckIn = true;                 
				objCheckInInt = CRMRetailVNC_Helper_HUM.createInteraction(objAccount.Id, visitorType , GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL, null,null, System.Now() , false ,visitorReasonId,listOfLocation[0].Id,visitorId,false);
				listInts.add(objCheckInInt);
                mapToCheckDuplicates = GenericHelper_CRMRetail_H_HUM.checkDuplicateInteractions(listInts);

                if (mapToCheckDuplicates != null) 
                {
                    
					for (Storefront_Interaction__c intObj : listInts)
					{
						key = string.valueOf(intObj.Interaction_Date__c.DateGMT())+
						string.valueOf(intObj.Reason__c) + string.valueOf(intObj.Location__c)+ string.valueOf(intObj.Account__c)+ string.valueOf(intObj.Visitor_ID__c)+
						string.valueOf(intObj.Storefront_Event_Starttime__c) + intObj.Category__c;
						
						firstTimekey = string.valueOf(intObj.Interaction_Date__c.DateGMT())+
						firstReasonID + string.valueOf(intObj.Location__c)+ string.valueOf(intObj.Account__c)+ string.valueOf(intObj.Visitor_ID__c)+
						string.valueOf(intObj.Storefront_Event_Starttime__c)+ intObj.Category__c; 
                            
						
						if(mapToCheckDuplicates.containsKey(key) && mapToCheckDuplicates.get(key))
						{
							if(intObj.Reason__c == reasonId)
							{
								createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ALREADY_ATTENDED + listOfReason[0].Name,'');
								return;
							}
							else
							{
								createCheckIn = false;
							}														
						}						
						else if(mapToCheckDuplicates.containsKey(firstTimekey) && mapToCheckDuplicates.get(firstTimekey))
						{
							createCheckIn = false;
						}
					}
					
					if (createCheckIn)
					{
						lstInteractionToInsert.add(objCheckInInt);
						lstInteractionToInsert.add(objRecordedInteraction);
					}
                    else{
						lstInteractionToInsert.add(objRecordedInteraction);
					}
						
                }
                else
                {
                    createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERAL_ERROR,'');
                    return;
                }
                
            }            
            
            if (!lstInteractionToInsert.isEmpty())
                insert lstInteractionToInsert;
            
            String interactionIdToSend = CRMRetailVNC_Helper_HUM.returnInteractionId(lstInteractionToInsert);
            
            if(interactionIdToSend != null)
            {
                createResponse(true,'',interactionIdToSend);
                return;
            }
            else
            {
                createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERAL_ERROR,'');
                return;	
            }         
        }
        catch(Exception e)
        {
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERAL_ERROR + error ,'');
            HUMExceptionHelper.logErrors(e,'CRMRetailMarkAttandance_H_HUM','isVideoEvent');
            return;
        }
    }	
    /**
    * isNotVideoEvent Method .
    * <p>
    * This method is used to get check the duplicates isVideoEvent is false if any and create a attended interaction along with a check in interaction.
    *
	* @param  		 objAccount								Account
 	* @param  		 lstInteraction							interactions related to AccountId sent in the reruest
	* @return		 NA								
    */
	private void isNotVideoEvent()
    {  
        try{           
        	List<Storefront_Interaction__c> listOfInteraction = new List<Storefront_Interaction__c>();
			List<Storefront_Interaction__c> lstInteraction = new List<Storefront_Interaction__c>();
            List<Storefront_Interaction__c> lstInteractionToInsert = new List<Storefront_Interaction__c>();
			Map<String,Boolean> mapInteractionWithStatus = new Map<String,Boolean>(); 
            Storefront_Interaction__c objAttendedInteraction = new Storefront_Interaction__c();
			String categoryToPopulate = '';
            Id attendedInteractionID;
            
            Map<String, Boolean> mapToCheckDuplicates = new Map<String,Boolean>();
			Id interactionId = request.interactionId.trim();
			
            listOfInteraction = [Select id, Visitor_ID__c, isAllDayEvent__c,Reason__c, Storefront_Event_Endtime__c, Category__c, Storefront_Event_Starttime__c, Location__c, Location__r.Name, Reason__r.Name, Account__r.Name, CRM_Retail_Interaction_Visitor_Type__c, Interaction_Date_Only__c, Interaction_Date__c, Account__c ,Account__r.Id, First_Time_Visitor__c from Storefront_Interaction__c where Id =: interactionId];  
			
            if(listOfInteraction.isEmpty())
            {
                createResponse(false, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_INTERACTION_FOUND + interactionId ,'');
                return;
            }
            
            if(listOfInteraction[0].Category__c != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE && listOfInteraction[0].Category__c != GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL)
            {
                createResponse(false, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_INTERACTION_ID,'');
                return;
            }
           
            String categoryVal = (listOfInteraction[0].category__c == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL) ? 'Virtual' : '';
			
            objAttendedInteraction = CRMRetailVNC_Helper_HUM.createInteraction(listOfInteraction[0].Account__r.Id, listOfInteraction[0].CRM_Retail_Interaction_Visitor_Type__c, categoryVal, listOfInteraction[0].Storefront_Event_Starttime__c, listOfInteraction[0].Storefront_Event_Endtime__c, System.Now() , listOfInteraction[0].First_Time_Visitor__c, listOfInteraction[0].Reason__c, listOfInteraction[0].Location__c, listOfInteraction[0].Visitor_ID__c, listOfInteraction[0].isAllDayEvent__c);
			
            List<Storefront_Interaction__c> listInts = new List<Storefront_Interaction__c>();
            listInts.add(objAttendedInteraction);
            mapToCheckDuplicates = GenericHelper_CRMRetail_H_HUM.checkDuplicateInteractions(listInts);
            
            if (mapToCheckDuplicates != null)
			{
                String key = string.valueOf(objAttendedInteraction.Interaction_Date__c.DateGMT())+
                string.valueOf(objAttendedInteraction.Reason__c) + string.valueOf(objAttendedInteraction.Location__c)+ string.valueOf(objAttendedInteraction.Account__c)+ string.valueOf(objAttendedInteraction.Visitor_ID__c)+
                string.valueOf(objAttendedInteraction.Storefront_Event_Starttime__c) + categoryVal;
				
                System.debug('mapToCheckDuplicates===::'+mapToCheckDuplicates);
                
				if(mapToCheckDuplicates.containsKey(key) && mapToCheckDuplicates.get(key))
				{
					createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ALREADY_ATTENDED + listOfInteraction[0].Reason__r.Name,'');
                    return;
				}
                else
                {
                    listInts.clear();
					map<string,Storefront_Interaction__c> checkInToInsertMap = new map<string,Storefront_Interaction__c>();
                    String firstTimekey = '';
                    Storefront_Interaction__c objCheckInInt  = new Storefront_Interaction__c();
                    Storefront_Interaction__c objCheck1stInInt  = new Storefront_Interaction__c();
                    
                    Id visitorReasonId = CRMRetailVNC_Helper_HUM.returnReasonId(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN);
                    
                    if(visitorReasonId == null)
                    {                
                        createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_INT_FOUND + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN,'');
                        return;
                    }
                    
                    Id firstReasonID = CRMRetailVNC_Helper_HUM.returnReasonId(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN);
                    
                    if(firstReasonID == null)
                    {                
                        createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_INT_FOUND + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN,'');
                        return;
                    }

					if(categoryVal == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL)
					{
						objCheckInInt = CRMRetailVNC_Helper_HUM.createInteraction(listOfInteraction[0].Account__r.Id, listOfInteraction[0].CRM_Retail_Interaction_Visitor_Type__c,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL, null,null, System.Now() , listOfInteraction[0].First_Time_Visitor__c,visitorReasonId,listOfInteraction[0].Location__c,listOfInteraction[0].Visitor_ID__c,false);
						listInts.add(objCheckInInt);
						
						objCheck1stInInt = CRMRetailVNC_Helper_HUM.createInteraction(listOfInteraction[0].Account__r.Id, listOfInteraction[0].CRM_Retail_Interaction_Visitor_Type__c,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL, null,null, System.Now() , listOfInteraction[0].First_Time_Visitor__c,firstReasonID,listOfInteraction[0].Location__c,listOfInteraction[0].Visitor_ID__c,false);
						listInts.add(objCheck1stInInt);					
					}
					else{
						objCheckInInt = CRMRetailVNC_Helper_HUM.createInteraction(listOfInteraction[0].Account__r.Id, listOfInteraction[0].CRM_Retail_Interaction_Visitor_Type__c,'', null,null, System.Now() , listOfInteraction[0].First_Time_Visitor__c,visitorReasonId,listOfInteraction[0].Location__c,listOfInteraction[0].Visitor_ID__c,false);
						listInts.add(objCheckInInt);
						
						objCheck1stInInt = CRMRetailVNC_Helper_HUM.createInteraction(listOfInteraction[0].Account__r.Id, listOfInteraction[0].CRM_Retail_Interaction_Visitor_Type__c,'', null,null, System.Now() , listOfInteraction[0].First_Time_Visitor__c,firstReasonID,listOfInteraction[0].Location__c,listOfInteraction[0].Visitor_ID__c,false);
						listInts.add(objCheck1stInInt);	
					}					
					
					mapToCheckDuplicates = GenericHelper_CRMRetail_H_HUM.checkDuplicateInteractions(listInts);

                    for (Storefront_Interaction__c intObj : listInts)
					{
						string checkInCategory = '';
						
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
							lstInteractionToInsert.add(objAttendedInteraction);
							break;
						}
						else if(mapToCheckDuplicates.containsKey(firstTimekey) && mapToCheckDuplicates.get(firstTimekey))
						{
							lstInteractionToInsert.add(objAttendedInteraction);
							break;
						}						
					}
                    if (lstInteractionToInsert.isEmpty())
					{
						lstInteractionToInsert.add(objCheckInInt);
						lstInteractionToInsert.add(objAttendedInteraction);
					}
                }
            }
            else
            {
                createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERAL_ERROR,'');
                return;
            }
            
            if (!lstInteractionToInsert.isEmpty())
                insert lstInteractionToInsert;
            
            String interactionIdToSend = CRMRetailVNC_Helper_HUM.returnInteractionId(lstInteractionToInsert);
            
            if(interactionIdToSend != null)
            {
                createResponse(true,'',interactionIdToSend);
                return;
            }
            else
            {
                createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERAL_ERROR,'');
                return;	
            }           
        }
        catch(Exception e)
        {
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERAL_ERROR + error ,'');
			HUMExceptionHelper.logErrors(e,'CRMRetailMarkAttandance_H_HUM','isNotVideoEvent');
            return;
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
    private void createResponse(Boolean isSuccess, String errorMsg, String interactionId)
    {
        response.isSuccess = isSuccess;
        response.interactionId = interactionId;
        
        if(String.isNotBlank(errorMsg))
        {
            response.errorMessage = errorMsg;
        }
    } 
}