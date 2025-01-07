/******************************************************************************************************************
Apex class Name    : UpdateChatTranscriptService_D_HUM
Version            : 1.0
Function           : This class is used as data layer class
Created On         : Dec 28, 2021
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Vishnu Pilli                                    12/28/2020          Original Version

------------------------------------------------------------------------------------------------------------------- */
public without sharing class UpdateChatTranscriptService_D_HUM {

    private static UpdateChatTranscriptRequest_DTO_HUM request;
    private static UpdateChatTranscriptResponse_DTO_HUM response;
    private static Interaction__c intObj;
    private static Case caseObj;
    private static LiveChatTranscript objTranscript;

    public static UpdateChatTranscriptResponse_DTO_HUM updateChatTranscript(UpdateChatTranscriptRequest_DTO_HUM oRequest)
    {
        request = oRequest;
        String strValidation = validateRequest();
        
        if(strValidation == GLOBAL_CONSTANT_HUM.STRING_TRUE)
            assignAndUpdateChatTranscriptValues();
        else
            createfaultResponse(strValidation);

        return response;    
    }

    private static String validateRequest() {

        String queryString;
        Set < String > setValidSuccessfulResolution = new Set < String > ();
        setValidSuccessfulResolution.add(GLOBAL_CONSTANT_HUM.sSuccessfulResolutionYes);
        setValidSuccessfulResolution.add(GLOBAL_CONSTANT_HUM.sSuccessfulResolutionNo);
        if (String.isNotBlank(request.UpdateChatTranscriptRequest.SuccessfulResolution)) {
            if (!setValidSuccessfulResolution.contains(request.UpdateChatTranscriptRequest.SuccessfulResolution))
                return System.Label.chatTranscriptServiceInvalidSR;

            if (String.isNotBlank(request.UpdateChatTranscriptRequest.SourceKey)) {
                if (String.isNotBlank(request.UpdateChatTranscriptRequest.SourceApp)) {
                    if (String.isNotBlank(request.UpdateChatTranscriptRequest.InteractionNumber)) {
                        queryString = request.UpdateChatTranscriptRequest.InteractionNumber;

                        for (Interaction__c interaction: [Select id, name from Interaction__c where Name =: queryString Limit 1])
                            intObj = interaction;

                        if (intObj == null)
                            return System.Label.chatTranscriptServiceInteractionNotFound;

                        if (String.isNotBlank(request.UpdateChatTranscriptRequest.CaseNumber)) {
                            queryString = request.UpdateChatTranscriptRequest.CaseNumber;
                            for (Case caseOb: [Select id, CaseNumber, AccountId from Case where CaseNumber =: queryString limit 1])
                                caseObj = caseOb;

                            if (caseObj == null)
                                return System.Label.chatTranscriptServiceCaseNotFound;

                            for (LiveChatTranscript obj : [SELECT Id, Body, Humana_Chat_Transcript__r.Body__c FROM LiveChatTranscript 
                                                            WHERE Name = :request.UpdateChatTranscriptRequest.TranscriptNumber]) {
                                objTranscript = obj;
                                System.debug('AMDev objTranscript - ' + objTranscript);
                            }
                            if (objTranscript == null) {
                                return System.Label.chatTranscriptServiceTranscriptNotFound;
                            }

                            if (String.isNotBlank(request.UpdateChatTranscriptRequest.TranscriptBody)) {
                                if (String.isNotBlank(request.UpdateChatTranscriptRequest.ChatType)) {
                                    String reqChatType = request.UpdateChatTranscriptRequest.ChatType;
                                    String reqSourceApp = request.UpdateChatTranscriptRequest.SourceApp;

                                    List < Chat_Service_Source_App__mdt > SourceAppList = [SELECT MasterLabel, ChatType__c FROM Chat_Service_Source_App__mdt where MasterLabel =: reqSourceApp LIMIT 1];

                                    if (!SourceAppList.IsEmpty()) {
                                        List < String > validChatTypeList = new List < String > ();
                                        validChatTypeList = SourceAppList[0].ChatType__c.toUppercase().split(',');

                                        if (validChatTypeList.size() > 0 && validChatTypeList.contains(reqChatType.toUppercase())) {
                                            return GLOBAL_CONSTANT_HUM.STRING_TRUE;
                                        } else {
                                            return System.Label.chatTranscriptServiceChatTypeMatch;
                                        }

                                    } else {
                                        return System.Label.chatTranscriptServiceInvalidSourceApp;
                                    }
                                } else {
                                    return System.Label.chatTranscriptServiceChatType;
                                }
                            } else {
                                return System.Label.chatTranscriptServiceTranscriptBody;
                            }
                        } else {
                            return System.Label.chatTranscriptServiceCase;
                        }
                    } else {
                        return System.Label.chatTranscriptServiceInteraction;
                    }
                } else {
                    return System.Label.chatTranscriptServiceSourceApp;
                }
            } else {
                return System.Label.chatTranscriptServiceSourceKey;
            }

        } else {
            return System.Label.chatTranscriptServiceSuccessfulResolution;
        }

    }   //  end of validateRequest

    @TestVisible
    private static void assignAndUpdateChatTranscriptValues()
    {   
        if(String.isNotBlank(request.UpdateChatTranscriptRequest.TranscriptBody)) {
            System.debug('AMDev request.UpdateChatTranscriptRequest.TranscriptBody: ' + request.UpdateChatTranscriptRequest.TranscriptBody);
            System.debug('AMDev objTranscript.Body: ' + objTranscript.Body);
            if (String.isBlank(objTranscript.Body)) {
                String customChatBody = String.isBlank(objTranscript.Humana_Chat_Transcript__r.Body__c) ? '' : objTranscript.Humana_Chat_Transcript__r.Body__c;
                objTranscript.Body = customChatBody + request.UpdateChatTranscriptRequest.TranscriptBody.replace('\r\n','<br>').replace('\n','<br>').replace('\r','<br>').replace('\\n','<br>');
            } else {
                objTranscript.Body += '<br>' + request.UpdateChatTranscriptRequest.TranscriptBody.replace('\r\n','<br>').replace('\n','<br>').replace('\r','<br>').replace('\\n','<br>');
            }
            
        }
        if(String.isNotBlank(request.UpdateChatTranscriptRequest.ChatType))
            objTranscript.Chat_Type__c = request.UpdateChatTranscriptRequest.ChatType;
        if(String.isNotBlank(request.UpdateChatTranscriptRequest.SuccessfulResolution))
            objTranscript.Successful_Resolution__c = request.UpdateChatTranscriptRequest.SuccessfulResolution;
        
        
        If(!String.isEmpty(HUMConstants__c.getInstance('RESTRICTED_USER_ID').StringValue__c))
        {
            objTranscript.OwnerId = ID.valueOf(HUMConstants__c.getInstance('RESTRICTED_USER_ID').StringValue__c);
        }
        
        updateTranscript(objTranscript);
    }   //  end of assignAndUpdateChatTranscriptValues

    @TestVisible
    private static void updateTranscript(LiveChatTranscript oCT)
    {
        response = new UpdateChatTranscriptResponse_DTO_HUM();
        UpdateChatTranscriptResponse_DTO_HUM.UpdateChatTranscriptResponse  chatTranscriptResponse = new UpdateChatTranscriptResponse_DTO_HUM.UpdateChatTranscriptResponse();
        String chatTranscriptId = null;
        
        System.debug('AMDev oCT.Body before Update: ' + oCT);

        Database.SaveResult result = Database.update(oCT, false); 
        
        if(result.isSuccess())
        {
            chatTranscriptId = result.getId();                                          
            for(LiveChatTranscript lctObj : [Select Id, Name, Body from LiveChatTranscript where Id = :chatTranscriptId])
            {                  
                System.debug('AMDev lctObj.Body afterUpdate: ' + lctObj);
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
        response.UpdateChatTranscriptResponse = chatTranscriptResponse;        
    }
    
    @TestVisible
    private static void createfaultResponse(String errorMessage)
    {
        response = new UpdateChatTranscriptResponse_DTO_HUM();
        UpdateChatTranscriptResponse_DTO_HUM.UpdateChatTranscriptResponse  chatTranscriptResponse = new UpdateChatTranscriptResponse_DTO_HUM.UpdateChatTranscriptResponse();
        
        chatTranscriptResponse.Success = GLOBAL_CONSTANT_HUM.STRING_FLASE;
        chatTranscriptResponse.ErrorMessage = errorMessage;
        response.UpdateChatTranscriptResponse = chatTranscriptResponse;
    }

}   //  end of UpdateChatTranscriptService_D_HUM