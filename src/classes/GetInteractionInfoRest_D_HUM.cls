/******************************************************************************************************************
Apex class Name    : GetInteractionInfoRest_D_HUM
Version            : 1.0
Function           : This class is for GetInteractionInfo service method of Case Service
Created On         : July 01 2019
Test Class         : GetInteractionInfoRest_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Mohammed Noor                                  07/01/2019        Original Version
*   1.1            Mohammed Noor                                  07/23/2019        Fix for Defects# 430437 & 430438
*   1.2            Mohammed Noor                                  07/25/2019        REQ - 403693 - Fix for Defect#430513
*   1.3            Mohammed Noor                                  07/26/2019        REQ - 403693 - Fix for Defect#430969
* ------------------------------------------------------------------------------------------------------------------- */
public with sharing class GetInteractionInfoRest_D_HUM
{
    private static GetInteractionInfoRequest_DTO_HUM getIntInfoRequest = new GetInteractionInfoRequest_DTO_HUM();    
    private static map<string,set<string>> mapInteractionAccts = new map<string,set<string>>();
    private static Integer iTotalInteractions = 0;
    private static list<Interaction__c> lstInteractions = new list<Interaction__c>();    
    private static set<string> setInteractionNumber = new set<string>();        
    private static Integer iOffset;
    private static Date dtFromDate = null;
   	private static Date dtToDate = null;
	private static DateTime dtFromDateTimeGMT = null;
   	private static DateTime dtToDateTimeGMT = null;
	private static boolean bSearchUnknown = false;   
    private static final string sOrderBy = ' ORDER BY CreatedDate DESC';
    private static final string sInteractionQuery = 'SELECT Id, Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, Caller_Name__c, Created_By_Queue__c, Interacting_With__c, Interacting_With_type__c,'+
												'Interacting_With__r.Name, Interacting_With__r.FirstName, Interacting_With__r.LastName, Interacting_With__r.Birthdate__c, Interacting_With__r.Enterprise_ID__c, Interaction_Origin__c, UUID__c, '+
												'(SELECT Id, Interacting_About__c, Interacting_About__r.Name, Interacting_About__r.FirstName, Interacting_About__r.LastName, Interacting_About__r.Birthdate__c, Interacting_About__r.Enterprise_ID__c, '+
												'Interaction__c, Interacting_About_Type__c, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById FROM Interaction_Members__r) FROM Interaction__c WHERE';      
    
    /**
    * getInteractionInfo
    * <p>
    * This is the main entry method for GetInteractionInfo service.
    *
    * @param    request - Input request DTO.       
    * @return   GetInteractionInfoResponse_DTO_HUM - final response DTO                 
    */
    public static GetInteractionInfoResponse_DTO_HUM getInteractionInfo(GetInteractionInfoRequest_DTO_HUM request)
    {        
        GetInteractionInfoResponse_DTO_HUM response = new GetInteractionInfoResponse_DTO_HUM();
        response.GetInteractionInfoResponse=new GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO();
        getIntInfoRequest = request;
        if(getIntInfoRequest == null)
        {           
            response.GetInteractionInfoResponse.InteractionInfoResponseList = getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOMANDATROYINFOERROR_HUM,false);                        
        }
        else
        {                   
            //If Interaction Numbers provided in the input, ignore other input parameters and fetch the data by Interaction numbers
            if(getIntInfoRequest.GetInteractionInfo.InteractionNumber!=null && !getIntInfoRequest.GetInteractionInfo.InteractionNumber.isEmpty())
            {               
                response.GetInteractionInfoResponse = fetchByInteractionNumber(getIntInfoRequest.GetInteractionInfo.InteractionNumber);
            }
            else if(getIntInfoRequest.GetInteractionInfo.InteractingWithCriteria!= null && !getIntInfoRequest.GetInteractionInfo.InteractingWithCriteria.isEmpty() && 
					String.isNotBlank(getIntInfoRequest.GetInteractionInfo.StartPaging) && String.isNotBlank(getIntInfoRequest.GetInteractionInfo.StartDate) &&
					String.isNotBlank(getIntInfoRequest.GetInteractionInfo.EndDate))
            {
            	iOffset = (Integer.valueOf(getIntInfoRequest.GetInteractionInfo.StartPaging) > 0) ? Integer.valueOf(getIntInfoRequest.GetInteractionInfo.StartPaging)-1 : 0;          		
		        dtFromDate = EntitySearch_H_HUM.parsestringToDate(getIntInfoRequest.GetInteractionInfo.StartDate);
        		dtToDate = EntitySearch_H_HUM.parsestringToDate(getIntInfoRequest.GetInteractionInfo.EndDate);
        		if(dtFromDate!= null && dtToDate!= null && dtToDate>=dtFromDate.addYears(1)) //return error if the date range is beyond 1 year
	            {
	                response.GetInteractionInfoResponse.InteractionInfoResponseList=getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.CASESERVICE_GETCASEINFODATERANGERROR_HUM,false);	                
	            }
	            else
	            {
	                if(getIntInfoRequest.GetInteractionInfo.InclusionCriteria!= null  && (!getIntInfoRequest.GetInteractionInfo.InclusionCriteria.isEmpty()) && getIntInfoRequest.GetInteractionInfo.InclusionCriteria.contains(GLOBAL_CONSTANT_HUM.CASESERVICE_SEARCHUNKNOWN_INCLUSION))
	            	{
	                	bSearchUnknown=true;
	            	}
	                dtToDate = dtToDate.addDays(1);
					DateTime tempFromDateLocal = DateTime.newInstance(dtFromDate.year(), dtFromDate.month(), dtFromDate.day(), 0, 0, 0); //constructs the start datetime in User's local Time zone. 
	                DateTime tempToDateLocal = DateTime.newInstance(dtToDate.year(), dtToDate.month(), dtToDate.day(), 0, 0, 0);  //constructs the End datetime in User's local time zone.
					Date tempDateGMT = tempFromDateLocal.dateGMT();  //get the Date part in GMT
					Time tempTimeGMT = tempFromDateLocal.timeGMT();  //get the time part in GMT
					dtFromDateTimeGMT = DateTime.newInstanceGMT(tempDateGMT, tempTimeGMT);		//construct the start datetime in GMT(UTC) to use in SOQL WHERE Clause.
					tempDateGMT = tempToDateLocal.dateGMT();	//get the Date part in GMT
					tempTimeGMT = tempToDateLocal.timeGMT();	//get the time part in GMT
					dtToDateTimeGMT = DateTime.newInstanceGMT(tempDateGMT, tempTimeGMT);	//construct the end datetime in GMT(UTC) to use in SOQL WHERE Clause.
	                //Call the Utility Method to find the interacting with accounts (interacting about passed as null to Generic search method)
	            	mapInteractionAccts.putAll(EntitySearch_H_HUM.performEntitySearch(null,getIntInfoRequest.GetInteractionInfo.InteractingWithCriteria,bSearchUnknown));
	            		            	
					if(mapInteractionAccts!=null && !mapInteractionAccts.isEmpty())  //fetch the interaction details based on the accounts located and build the response
					{
						response.GetInteractionInfoResponse = fetchInteractionsByAccount();
					}
					else if(mapInteractionAccts==null || (mapInteractionAccts!=null && mapInteractionAccts.isEmpty()))  //send error response of no results found
					{
						response.GetInteractionInfoResponse.InteractionCount = '0';
                    	response.GetInteractionInfoResponse.InteractionInfoResponseList=getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOMANDATORYERROR_HUM,false);
					}    
	            }         		        		        						
            }
            else
            {
            	response.GetInteractionInfoResponse.InteractionInfoResponseList = getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOMANDATROYINFOERROR_HUM,false);	
            }
        }       
        return response;    
    }
    
    /**
    * fetchByInteractionNumber
    * <p>
    * This method is used to fetch the Interactions and Interaction Members based on the input Interaction Numbers.
    *
    * @param    none       
    * @return   GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO - final response DTO                 
    */
    @TestVisible
    private static GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO fetchByInteractionNumber(list<string> lstInteractionNumber)
    {
        GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO GetInteractionInfoResponse = new GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO(); 
        string sQuery = '';
        try
        {               
            if(lstInteractionNumber!=null && !lstInteractionNumber.isEmpty())
            {
                if(lstInteractionNumber.size()>GLOBAL_CONSTANT_HUM.MAXLIMIT)
                {
                    for(integer index=0; index<GLOBAL_CONSTANT_HUM.MAXLIMIT; index++)
                    {
                        setInteractionNumber.add(lstInteractionNumber[index]);
                    }
                }
                else
                {
                    setInteractionNumber.addAll(lstInteractionNumber);
                }
                sQuery =  sInteractionQuery + ' Name IN :setInteractionNumber' + sOrderBy;               
                lstInteractions = Database.Query(sQuery);
                if(lstInteractions!=null && !lstInteractions.isEmpty())
                {                    
                    iTotalInteractions = lstInteractions.size();                   
                    GetInteractionInfoResponse = createResponse(lstInteractions);                   
                }
                else
                {       
                    GetInteractionInfoResponse.InteractionCount = '0';
                    GetInteractionInfoResponse.InteractionInfoResponseList=getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOMANDATORYERROR_HUM,false);                
                }           
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'GetInteractionInfoRest_D_HUM','fetchByInteractionNumber');
            GetInteractionInfoResponse.InteractionInfoResponseList=getErrorLogInstance(null,ex.getMessage(),false);
        }
        return GetInteractionInfoResponse;      
    }
    
    /**
    * fetchInteractionsByAccount
    * <p>
    * This method is used to fetch the Interactions and Interaction Members based on the input Interaction About and Interacting with Criteria.
    *
    * @param    none       
    * @return   GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO - final response DTO                 
    */
    @TestVisible
    private static GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO fetchInteractionsByAccount()
    {
    	GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO getInteractionInfoResponse = new GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO();
    	list<Interaction_Member__c> lstInteractionMembers = new list<Interaction_Member__c>();
    	list<Interaction__c> lstAllInteractions = new list<Interaction__c>();    	
    	string sQuery = '';
    	try
    	{
	    	if(mapInteractionAccts!=null && !mapInteractionAccts.isEmpty() && mapInteractionAccts.containsKey(GLOBAL_CONSTANT_HUM.INTWITH))
	    	{    			    		
    			set<string> setIntWith = mapInteractionAccts.get(GLOBAL_CONSTANT_HUM.INTWITH);
    			
    			//fetch Interaction and Interaction Members in a single query
    			sQuery += sInteractionQuery + ' Interacting_With__c IN :setIntWith';
    			if(!Test.isRunningTest() && dtFromDateTimeGMT!=null && dtToDateTimeGMT!=null)
        		{
            		sQuery+=' AND CreatedDate >= :dtFromDateTimeGMT AND CreatedDate < :dtToDateTimeGMT';
        		}
        		sQuery+= sOrderBy;
        		lstAllInteractions = Database.Query(sQuery);
        		if(lstAllInteractions!=null && !lstAllInteractions.isEmpty())
				{
					iTotalInteractions = lstAllInteractions.size();
					Integer iMaxOffset = iOffset + GLOBAL_CONSTANT_HUM.CASESERVICE_PAGINGLIMIT_INCLUSION;
					if(iMaxOffset > lstAllInteractions.size())  iMaxOffset = lstAllInteractions.size();
					for(Integer index=iOffset; index<iMaxOffset; index++ )
                    {
                        lstInteractions.add(lstAllInteractions[index]);
                    } 
					
					getInteractionInfoResponse = createResponse(lstInteractions);		
				}
				else  //send error response
				{
					GetInteractionInfoResponse.InteractionCount = '0';
                	GetInteractionInfoResponse.InteractionInfoResponseList=getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOMANDATORYERROR_HUM,false);
				}        		    		    	
	    	}
    	}
    	catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'GetInteractionInfoRest_D_HUM','fetchInteractionsByAccount');
            GetInteractionInfoResponse.InteractionInfoResponseList=getErrorLogInstance(null,ex.getMessage(),false);
        }
    	return getInteractionInfoResponse;
    }
        
    /**
    * createResponse
    * <p>
    * This method is used to organize the data for the final response
    *
    * @param    lstInteractions - list of Interaction records       
    * @return   GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO - final response DTO                 
    */
    @TestVisible
    private static GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO createResponse(list<Interaction__c> lstInteractions)
    {
        GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO GetInteractionInfoResponse = new GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO();
        set<string> setInteractionId =new set<string>();
        list<Interaction_Member__c> lstInteractionMembers = new list<Interaction_Member__c>();
        map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionMemberDTO>> mapInteractionMembers = new map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionMemberDTO>>();        
        map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionDetailsDTO>> mapInteractionDetails = new map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionDetailsDTO>>();
        
        if(lstInteractions!=null && !lstInteractions.isEmpty())
        {
            for(Interaction__c interaction : lstInteractions)
            {
                setInteractionId.add(interaction.Id);
                if(interaction.Interaction_Members__r!=null && !interaction.Interaction_Members__r.isEmpty())	lstInteractionMembers.addAll(interaction.Interaction_Members__r);
            }
            if(lstInteractionMembers!=null && !lstInteractionMembers.isEmpty())		mapInteractionMembers = getInteractionMemberDTO(lstInteractionMembers);            
        
			if(getIntInfoRequest.GetInteractionInfo.InclusionCriteria.contains(GLOBAL_CONSTANT_HUM.CASE_CONSTANT_HUM)) mapInteractionDetails = getInteractionDetailsDTO(setInteractionId);     
			//build final response 
			GetInteractionInfoResponse = buildInteractionInfoResponse(lstInteractions, mapInteractionMembers, mapInteractionDetails);
		}
		else  //send error response
		{
			GetInteractionInfoResponse.InteractionCount = '0';
        	GetInteractionInfoResponse.InteractionInfoResponseList=getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOMANDATORYERROR_HUM,false);
		}
        return GetInteractionInfoResponse;
    }         
       
    /**
    * buildInteractionInfoResponse
    * <p>
    * This method is used to build the GetInteractionInfo final response
    *
    * @param    lstInteractions - list of Interaction records
    * @param    mapInteractionMembers - map of Interaction Member DTO by Interactions
    * @param    mapInteractionDetails - map of Case details DTO by Interactions.
    * @return   GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO - final response DTO                 
    */
    @TestVisible
    private static GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO buildInteractionInfoResponse(list<Interaction__c> lstInteractions, map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionMemberDTO>> mapInteractionMembers, map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionDetailsDTO>> mapInteractionDetails) 
    {
        GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO getInteractionInfoResponse = new GetInteractionInfoResponse_DTO_HUM.GetInteractionInfoResponseDTO();
        list<GetInteractionInfoResponse_DTO_HUM.InteractionInfoResponseDTO> lstInteractionInfoResponseDTO = new list<GetInteractionInfoResponse_DTO_HUM.InteractionInfoResponseDTO>();
        if(lstInteractions!=null && !lstInteractions.isEmpty())
        {
            GetInteractionInfoResponse_DTO_HUM.InteractionInfoResponseDTO interactionInfoResponse;
            GetInteractionInfoResponse_DTO_HUM.InteractionSummaryDTO interactionSummary;
            for(Interaction__c interaction : lstInteractions)
            {
                interactionInfoResponse = new GetInteractionInfoResponse_DTO_HUM.InteractionInfoResponseDTO();
                interactionSummary = new GetInteractionInfoResponse_DTO_HUM.InteractionSummaryDTO();
                interactionSummary.InteractionNumber = interaction.Name;
                interactionSummary.CallerName = interaction.Caller_Name__c;  
                interactionSummary.InteractingWithType = interaction.Interacting_With_type__c; 
				interactionSummary.InteractingWithName = interaction.Interacting_With__r.Name;
                interactionSummary.InteractingWithFirstName = interaction.Interacting_With__r.FirstName;
                interactionSummary.InteractingWithLastName = interaction.Interacting_With__r.LastName;
                interactionSummary.InteractingWithEnterpriseId = interaction.Interacting_With__r.Enterprise_ID__c;
                interactionSummary.InteractingWithDOB = interaction.Interacting_With__r.Birthdate__c;
                interactionSummary.InteractionOrigin = interaction.Interaction_Origin__c;
                if(mapInteractionMembers!=null && mapInteractionMembers.containsKey(interaction.Id))    interactionSummary.InteractionMember = mapInteractionMembers.get(interaction.Id);               

                interactionInfoResponse.InteractionSummary = interactionSummary;
                lstInteractionInfoResponseDTO.add(interactionInfoResponse);
                if(getIntInfoRequest.GetInteractionInfo.InclusionCriteria.contains(GLOBAL_CONSTANT_HUM.CASE_CONSTANT_HUM) && mapInteractionDetails!=null && mapInteractionDetails.containsKey(interaction.Id))
                {
                	interactionInfoResponse.InteractionDetail = mapInteractionDetails.get(interaction.Id);
                }    
            }
            getInteractionInfoResponse.InteractionCount = String.valueOf(iTotalInteractions);
            getInteractionInfoResponse.InteractionInfoResponseList = lstInteractionInfoResponseDTO;
        }
        return getInteractionInfoResponse;
    }
    
    /**
    * getInteractionMemberDTO
    * <p>
    * This method is used to create the InteractionMemberDTO from the list of Interaction Members records.
    *
    * @param    lstInteractionMembers - list of Interaction Members records    
    * @return   map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionMemberDTO>> - map of Interaction ID to Interaction Member DTO for response                 
    */
    @TestVisible    
    private static map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionMemberDTO>> getInteractionMemberDTO(list<Interaction_Member__c> lstInteractionMembers)
    {
        map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionMemberDTO>> mapIntMemberDTO = new map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionMemberDTO>>();
                
        if(lstInteractionMembers!=null && !lstInteractionMembers.isEmpty())
        {
            GetInteractionInfoResponse_DTO_HUM.InteractionMemberDTO intMemberDTO;
            for(Interaction_Member__c intMember : lstInteractionMembers)
            {                
                intMemberDTO = new GetInteractionInfoResponse_DTO_HUM.InteractionMemberDTO();
                intMemberDTO.InteractingAboutType = intMember.Interacting_About_Type__c;
                intMemberDTO.InteractingAboutName = intMember.Interacting_About__r.Name;
                intMemberDTO.InteractingAboutFirstName = intMember.Interacting_About__r.FirstName;
                intMemberDTO.InteractingAboutLastName = intMember.Interacting_About__r.LastName;
                intMemberDTO.InteractingAboutEnterpriseId = intMember.Interacting_About__r.Enterprise_ID__c;
                intMemberDTO.InteractingAboutDOB = intMember.Interacting_About__r.Birthdate__c;
                if(mapIntMemberDTO!=null && mapIntMemberDTO.containsKey(intMember.Interaction__c))
                {
                    mapIntMemberDTO.get(intMember.Interaction__c).add(intMemberDTO);    
                }
                else
                {
                    mapIntMemberDTO.put(intMember.Interaction__c, new list<GetInteractionInfoResponse_DTO_HUM.InteractionMemberDTO>{intMemberDTO});
                }
            }
        }
        return mapIntMemberDTO;
    }

    
    /**
    * getInteractionDetailsDTO
    * <p>
    * This method is used retrieve the Case details and return the case details DTO.
    *
    * @param    setInteractionId - set of Interaction recordIds    
    * @return   map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionDetailsDTO>> - map of Interaction ID to case details DTO for response                 
    */
    @TestVisible
    private static map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionDetailsDTO>> getInteractionDetailsDTO(set<string> setInteractionId)
    {
        map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionDetailsDTO>> mapIntDetailsDTO = new map<string, list<GetInteractionInfoResponse_DTO_HUM.InteractionDetailsDTO>>();
        
        list<Case_Interaction__c> lstCaseInteraction = [SELECT Id, Case__c, Case__r.Casenumber, Case__r.Origin, Case__r.Createddate, Case__r.Type, Case__r.Subtype__c, Case__r.Status, Case__r.Owner_Queue__c, Case__r.Work_queue_view_name__c, Interaction__c 
                                                        FROM Case_Interaction__c WHERE Interaction__c IN :setInteractionId];
        if(lstCaseInteraction!=null && !lstCaseInteraction.isEmpty())
        {
            GetInteractionInfoResponse_DTO_HUM.InteractionDetailsDTO interactionDetails;
            for(Case_Interaction__c caseInteraction : lstCaseInteraction)
            {
                interactionDetails = new GetInteractionInfoResponse_DTO_HUM.InteractionDetailsDTO();
                interactionDetails.CaseNumber = caseInteraction.Case__r.Casenumber; 
                interactionDetails.CaseOrigin = caseInteraction.Case__r.Origin;
                interactionDetails.CaseCreated = String.valueOf(caseInteraction.Case__r.Createddate);
                interactionDetails.CaseType = caseInteraction.Case__r.Type;
                interactionDetails.CaseSubtype = caseInteraction.Case__r.Subtype__c;
                interactionDetails.CaseStatus = caseInteraction.Case__r.Status;
                interactionDetails.WorkQueueName = caseInteraction.Case__r.Owner_Queue__c; 
                interactionDetails.QueueViewName = caseInteraction.Case__r.Work_queue_view_name__c;
                
                if(mapIntDetailsDTO!=null && mapIntDetailsDTO.containsKey(caseInteraction.Interaction__c))
                {
                    mapIntDetailsDTO.get(caseInteraction.Interaction__c).add(interactionDetails);
                }
                else
                {
                    mapIntDetailsDTO.put(caseInteraction.Interaction__c, new list<GetInteractionInfoResponse_DTO_HUM.InteractionDetailsDTO>{interactionDetails});
                }
            }   
        }
        return mapIntDetailsDTO;                                                              
    }
    
    /**
    * getErrorLogInstance
    * <p>
    * This method will be used return the response instance with error related fields.This method is public as it will be called from other classes
    *
    * @param    string sErrorCode
    * @param    string sErrorMessage
    * @param    Boolean sSuccess
    * @return   list<GetInteractionInfoResponse_DTO_HUM.InteractionInfoResponseDTO>                 
    */
    public static list<GetInteractionInfoResponse_DTO_HUM.InteractionInfoResponseDTO> getErrorLogInstance(string sErrorCode,string sErrorMessage,Boolean sSuccess )
    {
        GetInteractionInfoResponse_DTO_HUM.InteractionInfoResponseDTO InteractionInfoResponseDTO = new GetInteractionInfoResponse_DTO_HUM.InteractionInfoResponseDTO();
        GetInteractionInfoResponse_DTO_HUM.InteractionSummaryDTO InteractionSummary =new GetInteractionInfoResponse_DTO_HUM.InteractionSummaryDTO();
        InteractionSummary.ErrorMessage =sErrorMessage;
        InteractionInfoResponseDTO.InteractionSummary=InteractionSummary;
        return new list<GetInteractionInfoResponse_DTO_HUM.InteractionInfoResponseDTO>{InteractionInfoResponseDTO};
    }  
    
}