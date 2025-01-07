/******************************************************************************************************************
Apex class Name    : CreateChatTranscriptService_D_HUM
Version            : 1.0
Function           : This class is used as data layer class
Created On         : June 08 ,2020
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Joel George                                    6/08/2020          Original Version

------------------------------------------------------------------------------------------------------------------- */
public with sharing class CreateChatTranscriptService_D_HUM
{
    private static CreateChatTranscriptRequest_DTO_HUM request;
    private static CreateChatTranscriptResponse_DTO_HUM response;
    private static Interaction__c intObj;
    private static Case caseObj;
    
    public static CreateChatTranscriptResponse_DTO_HUM createChatTranscript(CreateChatTranscriptRequest_DTO_HUM oRequest)
    {
        request = oRequest;
        String validateRequest = validationSuccess();
        
        if(validateRequest == GLOBAL_CONSTANT_HUM.STRING_TRUE)
            assignChatTranscriptValues();
        else
            createfaultResponse(validateRequest);

        return response;    
    }
    
    private static String validationSuccess()
    {
        
        String queryString;
        Set<String>  setValidSuccessfulResolution = new Set<String>();
        setValidSuccessfulResolution.add(GLOBAL_CONSTANT_HUM.sSuccessfulResolutionYes);
        setValidSuccessfulResolution.add(GLOBAL_CONSTANT_HUM.sSuccessfulResolutionNo);
        if(String.isNotBlank(request.CreateChatTranscriptRequest.SuccessfulResolution))
        {
           if(!setValidSuccessfulResolution.contains(request.CreateChatTranscriptRequest.SuccessfulResolution))
            return System.Label.chatTranscriptServiceInvalidSR;
        
            if(String.isNotBlank(request.CreateChatTranscriptRequest.SourceKey))
            {
                 if(String.isNotBlank(request.CreateChatTranscriptRequest.SourceApp))
                {
                    if(String.isNotBlank(request.CreateChatTranscriptRequest.InteractionNumber))
                    {
                        queryString = request.CreateChatTranscriptRequest.InteractionNumber;
                       
                        for(Interaction__c interaction : [Select id , name from Interaction__c where Name=: queryString Limit 1])
							intObj = interaction;
                        
                        if(intObj== null)
                return System.Label.chatTranscriptServiceInteractionNotFound;
            
			if(String.isNotBlank(request.CreateChatTranscriptRequest.CaseNumber))	
            {
                queryString = request.CreateChatTranscriptRequest.CaseNumber;
							for(Case caseOb : [Select id,CaseNumber,AccountId  from Case where CaseNumber=:queryString limit 1])
							caseObj = caseOb;
                
                            if(caseObj == null)
                    return System.Label.chatTranscriptServiceCaseNotFound;
                
				if(String.isNotBlank(request.CreateChatTranscriptRequest.TranscriptBody))
                {
					if(String.isNotBlank(request.CreateChatTranscriptRequest.ChatType))
                    {
						String reqChatType = request.CreateChatTranscriptRequest.ChatType;
						String reqSourceApp = request.CreateChatTranscriptRequest.SourceApp;
						
						List<Chat_Service_Source_App__mdt> SourceAppList = [SELECT MasterLabel,ChatType__c FROM Chat_Service_Source_App__mdt where MasterLabel =: reqSourceApp LIMIT 1];
						
						if(!SourceAppList.IsEmpty())
						{
							List<String> validChatTypeList = new List<String>();
							validChatTypeList = SourceAppList[0].ChatType__c.toUppercase().split(',');
							
							if (validChatTypeList.size() > 0 && validChatTypeList.contains(reqChatType.toUppercase()))
							{
                                 return GLOBAL_CONSTANT_HUM.STRING_TRUE ;
							}
							else
							{
                                 return System.Label.chatTranscriptServiceChatTypeMatch;
							}
							
						}
						else
						{
                             return System.Label.chatTranscriptServiceInvalidSourceApp;
						}
                    }
                    else
                    {
                        return System.Label.chatTranscriptServiceChatType;
                    }
                }
                else
                {
                    return System.Label.chatTranscriptServiceTranscriptBody;
                }
            }
            else
            {
                return System.Label.chatTranscriptServiceCase;
            }
        }
        else
        {
            return System.Label.chatTranscriptServiceInteraction;
        }
                }
                else
                {
                    return System.Label.chatTranscriptServiceSourceApp;
                }
           }
           else
           {
                return System.Label.chatTranscriptServiceSourceKey;
           }
       
        }
        else
        {
            return System.Label.chatTranscriptServiceSuccessfulResolution;
        }
    
    }
    
    @TestVisible
    private static void assignChatTranscriptValues()
    {   
        LiveChatTranscript oLCT = new LiveChatTranscript();
        LiveChatVisitor oLCV = new LiveChatVisitor();
        insert oLCV;
        
        if(String.isNotBlank(request.CreateChatTranscriptRequest.TranscriptBody))
            System.debug('##-- Chat Body before: ' + request.CreateChatTranscriptRequest.TranscriptBody);
            oLCT.Body = request.CreateChatTranscriptRequest.TranscriptBody.replace('\r\n','<br>').replace('\n','<br>').replace('\r','<br>').replace('\\n','<br>');
            System.debug('##-- Chat Body after: ' + oLCT.Body);
        if(String.isNotBlank(request.CreateChatTranscriptRequest.ChatType))
            oLCT.Chat_Type__c = request.CreateChatTranscriptRequest.ChatType;
        if(String.isNotBlank(request.CreateChatTranscriptRequest.Issue))
            oLCT.Issue__c = request.CreateChatTranscriptRequest.Issue;
        if(String.isNotBlank(request.CreateChatTranscriptRequest.PlanName))
            oLCT.Category__c = request.CreateChatTranscriptRequest.PlanName;  //PLAN name population
        if(String.isNotBlank(request.CreateChatTranscriptRequest.URL))
            oLCT.URL__c = request.CreateChatTranscriptRequest.URL;
        if(String.isNotBlank(request.CreateChatTranscriptRequest.SuccessfulResolution))
            oLCT.Successful_Resolution__c = request.CreateChatTranscriptRequest.SuccessfulResolution;
        if(request.CreateChatTranscriptRequest.AverageResponseTimeVisitor !=  null)
            oLCT.AverageResponseTimeVisitor = request.CreateChatTranscriptRequest.AverageResponseTimeVisitor;
        if(request.CreateChatTranscriptRequest.MaxResponseTimeVisitor !=  null)
            oLCT.MaxResponseTimeVisitor = request.CreateChatTranscriptRequest.MaxResponseTimeVisitor;
         if(String.isNotBlank(request.CreateChatTranscriptRequest.EndedBy))
            oLCT.EndedBy = request.CreateChatTranscriptRequest.EndedBy; // Check to see if blank
        if(request.CreateChatTranscriptRequest.AverageResponseTimeOperator !=  null)
            oLCT.AverageResponseTimeOperator = request.CreateChatTranscriptRequest.AverageResponseTimeOperator;
        if(request.CreateChatTranscriptRequest.MaxResponseTimeOperator !=  null)
            oLCT.MaxResponseTimeOperator = request.CreateChatTranscriptRequest.MaxResponseTimeOperator;
        if(request.CreateChatTranscriptRequest.VisitorMessageCount !=  null)
            oLCT.VisitorMessageCount = request.CreateChatTranscriptRequest.VisitorMessageCount;
        if(request.CreateChatTranscriptRequest.OperatorMessageCount !=  null)
            oLCT.OperatorMessageCount = request.CreateChatTranscriptRequest.OperatorMessageCount;
        
        // date time population
		if(String.isNotBlank(request.CreateChatTranscriptRequest.RequestTime))
			oLCT.RequestTime =  DateTime.ValueOfGMT(request.CreateChatTranscriptRequest.RequestTime);
		if(String.isNotBlank(request.CreateChatTranscriptRequest.EndTime))
			oLCT.EndTime =  DateTime.ValueOfGMT(request.CreateChatTranscriptRequest.EndTime);
        else
             oLCT.EndTime = System.now();
		if(String.isNotBlank(request.CreateChatTranscriptRequest.StartTime))
			oLCT.StartTime =  DateTime.ValueOfGMT(request.CreateChatTranscriptRequest.StartTime);
        
        
        
         If(!String.isEmpty(HUMConstants__c.getInstance('RESTRICTED_USER_ID').StringValue__c))
        {
            oLCT.OwnerId = ID.valueOf(HUMConstants__c.getInstance('RESTRICTED_USER_ID').StringValue__c);
        }
        //populate case  
        if(caseObj != null)
        {
            oLCT.CaseId = caseObj.Id;
            oLCT.AccountId = caseObj.AccountId;
        }

        
        //populate Interaction
        if(intObj!= null)
        {
            oLCT.Interaction__c = intObj.Id;
        }           
        if(oLCV != null) oLCT.LiveChatVisitorId = oLCV.Id;
        oLCT.Status = GLOBAL_CONSTANT_HUM.HUM_COMPLETED;
        oLCT.chatkey = String.valueOf(system.now())+' '+ oLCT.CaseId;
        
        insertCTObj(oLCT);
    }
    
    @TestVisible
    private static void insertCTObj(LiveChatTranscript oCT)
    {
        response = new CreateChatTranscriptResponse_DTO_HUM();
        CreateChatTranscriptResponse_DTO_HUM.CreateChatTranscriptResponse  chatTranscriptResponse = new CreateChatTranscriptResponse_DTO_HUM.CreateChatTranscriptResponse();
        String chatTranscriptId = null;
        
        Database.SaveResult result = Database.insert(oCT, false); 
        
        if(result.isSuccess())
        {
            chatTranscriptId = result.getId();                                              
            for(LiveChatTranscript lctObj : [Select Id, Name from LiveChatTranscript where Id = :chatTranscriptId])
            {                  
                chatTranscriptResponse.TranscriptNumber  = lctObj.Name;
                chatTranscriptResponse.TranscriptId  = lctObj.Id;
                chatTranscriptResponse.Success = 'true';                          
            }
        }
        else
        {               
            String buildErrorMsg;
            for(Database.Error err : result.getErrors())  
            buildErrorMsg = 'The following error has occurred.' + err.getStatusCode() + ': ' + err.getMessage() +'--LiveChatTranscript fields that affected this error: ' + err.getFields();
            chatTranscriptResponse.Success =  GLOBAL_CONSTANT_HUM.STRING_FLASE;        
            CalloutException e = new CalloutException();
            e.setMessage(buildErrorMsg);
            throw e;                       
        }   
        response.CreateChatTranscriptResponse = chatTranscriptResponse;        
    }
    
    @TestVisible
    private static void createfaultResponse(String errorMessage)
    {
        response = new CreateChatTranscriptResponse_DTO_HUM();
        CreateChatTranscriptResponse_DTO_HUM.CreateChatTranscriptResponse  chatTranscriptResponse = new CreateChatTranscriptResponse_DTO_HUM.CreateChatTranscriptResponse();
        
        chatTranscriptResponse.Success = GLOBAL_CONSTANT_HUM.STRING_FLASE;
        chatTranscriptResponse.ErrorMessage = errorMessage;
        response.CreateChatTranscriptResponse = chatTranscriptResponse;
    }
}