/******************************************************************************************************************
Apex class Name    : UpdateCaseInfoRest_D_HUM
Version            : 1.0
Function           : This class is for Data Access Layer for for case related activities
Created On         : March 20 2018
Test Class         : HUMCaseServiceRestTest

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran        215366                    03/20/2018          Original Version
*   2.0            Srivinas Damera             229608                    5/15/2018           UpdateCaseInfo Attachment log as a list
*   3.0            Melkisan Selvaraj		   317009					 12/06/2018          REQ - 378820 source system set to GCP/CCP in Update Case
------------------------------------------------------------------------------------------------------------------- */
global with sharing class UpdateCaseInfoRest_D_HUM
{
   
   
    /*  
     * Method name : updateCaseAndAttachment
     * Description : This method will be used by external system to update case and attachment.
     * Return Type : CaseServiceRestUpdateResponse_DTO_HUM
     * Parameter   : CaseServiceRestUpdateRequest_DTO_HUM 
     */
    
    public static CaseServiceRestUpdateResponse_DTO_HUM updateCaseAndAttachment(CaseServiceRestUpdateRequest_DTO_HUM request)
    {
        CaseServiceRestUpdateResponse_DTO_HUM response = new CaseServiceRestUpdateResponse_DTO_HUM();
        response.UpdateCaseInfoResponse = constructStatusMessage(request);
        
        return response;
    }
    
    /*  
     * Method name : constructStatusMessage
     * Description : This method will be used to construct status message.
     * Return Type : CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO 
     * Parameter   : CaseServiceRestUpdateRequest_DTO_HUM 
     */    
    private static CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO constructStatusMessage(CaseServiceRestUpdateRequest_DTO_HUM request)
    {
        CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO statusMessageHelper =  NULL;
        CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO caseStatusMessage = new CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO(true, null, '');
        CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO attachmentStatusMessage = new CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO(true, null, '');
        
        if(validateRequest(request))
        {              
            Savepoint sp = Database.setSavepoint(); 
            String sErrorMessage = '';  
            try
            {   
                List<Case> lstCase = [SELECT Id, caseNumber, Status, OwnerId FROM Case WHERE CaseNumber = :request.UpdateCaseInfo.CaseUpdateRequest.CaseNumber];
                if(lstCase != null && !lstCase.isEmpty())
                {
                    if(String.isNotBlank(request.UpdateCaseInfo.CaseUpdateRequest.CaseStatus)) caseStatusMessage = updateCaseStatus(request.UpdateCaseInfo.CaseUpdateRequest ,lstCase[0]);
                    
                    if(request.UpdateCaseInfo.CaseUpdateRequest.AttachmentLog != null) attachmentStatusMessage = logAttachment(request.UpdateCaseInfo.CaseUpdateRequest,lstCase[0]);
                    
                    sErrorMessage = caseStatusMessage.ErrorMessage;
                    sErrorMessage = String.isblank(sErrorMessage) ? attachmentStatusMessage.ErrorMessage : sErrorMessage + attachmentStatusMessage.ErrorMessage; 
                    statusMessageHelper = new CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO(caseStatusMessage.Status && attachmentStatusMessage.Status, null, sErrorMessage);
                                                        
                    if(!statusMessageHelper.Status) Database.rollback(sp);
                }
                else
                {
                    statusMessageHelper = new CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO(false, null, system.label.HUMNoInteractionRecFound);
                }
            }
            catch(Exception ex)
            {
                Database.rollback(sp);
                statusMessageHelper = new CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO(false, null, ex.getMessage());
            }
        }
        else
        {
            statusMessageHelper = new CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO(false, null, system.label.HUMRequiredInputsNotFound); 
        }
        return statusMessageHelper;
    }
    
        /*  
     * Method name : validateRequest
     * Description : This method is used to validate the request and check if all the required parameters are present in the request.
     * Return Type : boolean
     * Parameter   : CaseServiceRestUpdateRequest_DTO_HUM 
     */
    private static boolean validateRequest(CaseServiceRestUpdateRequest_DTO_HUM CaseUpdateLogAttachmentRequestDTO)
    {
        return((String.isNotBlank(CaseUpdateLogAttachmentRequestDTO.UpdateCaseInfo.CaseUpdateRequest.CaseStatus) || CaseUpdateLogAttachmentRequestDTO.UpdateCaseInfo.CaseUpdateRequest.AttachmentLog != null)
            && String.isNotBlank(CaseUpdateLogAttachmentRequestDTO.UpdateCaseInfo.CaseUpdateRequest.CaseNumber));
    } 
    
    /*  
     * Method name : updateCaseStatus
     * Description : This method will be used to update the case fields.
     * Return Type : CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO
     * Parameter   : CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO, Case
     */
     @TestVisible
    private static CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO updateCaseStatus(CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO CaseUpdateLogAttachmentRequestDTO , Case caseRec) {
        CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO statMessage = new CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO(true, null, '');
        try
        {   
            caseRec =  assignValuesToCase(CaseUpdateLogAttachmentRequestDTO,caseRec);
            Database.SaveResult[] lstResultCase = Database.update(new Case[] {caseRec}, false);
            for (Database.SaveResult resultCase: lstResultCase)
            {
                 if (!resultCase.isSuccess())
                 {
                    statMessage = new CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO(false, resultCase.getErrors()[0].getStatusCode(), resultCase.getErrors()[0].getMessage());
                 }
            }
            if (statMessage.Status)
            {
                // By using the comment provided, create the casecomment record for the case.
                if(String.isNotBlank(CaseUpdateLogAttachmentRequestDTO.Comment)) statMessage = doCreateCaseComment(CaseUpdateLogAttachmentRequestDTO,caseRec);
                
                // By using the attachment details provided, create the Attachment record for the case.
                if(statMessage.Status && CaseUpdateLogAttachmentRequestDTO.Attachment != null && CaseUpdateLogAttachmentRequestDTO.Attachment.size() > 0)
                statMessage = doCreateAttachment(CaseUpdateLogAttachmentRequestDTO,caseRec);
            }
        }
        catch(Exception ex)
        {
            statMessage = new CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO(false, null, ex.getMessage());
        }
        return statMessage;     
    }
    
    /*  
     * Method name : logAttachment
     * Description : This method will be used to update the attachment fields.
     * Return Type : CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO
     * Parameter   : CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO, Case
     */
     @TestVisible
    private static CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO logAttachment(CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO CaseUpdateLogAttachmentRequestDTO , Case caseRec) 
    {
        String sErrorMessage = ''; 
        CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO attachResp = NULL;
        Attachment_Log__c sobjAttachlog = new Attachment_Log__c();  
        List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>{};
        List<Attachment_Log__c> lstAttachmentLogs = new List<Attachment_Log__c>();		
        
        try
        {
            attachResp = createAttachment(CaseUpdateLogAttachmentRequestDTO,caseRec);
			
        }
        catch(Exception ex)
        {
           HUMExceptionHelper.logErrors(ex,'UpdateCaseInfoRest_D_HUM','logAttachment');
           return getErrorLogInstance(null,ex.getMessage(),false); 
        }
        return attachResp;
    }
    
    /*  
    * Method name : createAtachmentLogItem
    * Description : This method is used to create List of Attchment log item records.
    * Return Type : List<Attachment_Log_Item__c> 
    * Parameter   : List<CaseServiceRestUpdateRequest_DTO_HUM.AttachmentAttributeDTO> , Attachment_Log__c
    */
    private static List<Attachment_Log_Item__c> createAtachmentLogItem(List<CaseServiceRestUpdateRequest_DTO_HUM.AttachmentAttributeDTO> AttachmentAttribute, Attachment_Log__c sobjAttachlog)
    {
        List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>();
        for(CaseServiceRestUpdateRequest_DTO_HUM.AttachmentAttributeDTO attachmentDesc : AttachmentAttribute)
        {
            Attachment_Log_Item__c attLogItem = new Attachment_Log_Item__c();
            attLogItem.Name =  attachmentDesc.AttachmentName;
            if(!String.isBlank(attachmentDesc.AttachmentValue))
            {
                attLogItem.Value__c = attachmentDesc.AttachmentValue;
            }          
            attLogItem.Attachment_Log__c = sobjAttachlog.Id;
            lstAttachlogItems.add(attLogItem);
        }
        return lstAttachlogItems;
    }
    
    /*  
    * Method name : errorMessage
    * Description : This method is used to get the error message if the record is not saved.
    * Return Type : String
    * Parameter   : Database.SaveResult
    */
    @TestVisible
    private Static String errorMessage(Database.SaveResult sobjAttach)
    {
        String sSemicolonConst = HUMConstants__c.getValues('SEMICOLON').StringValue__c;
        String sDotConst = HUMConstants__c.getValues('DOT').StringValue__c;
        String sErrorMessage = '';
        Integer iCounter = 1;
        for(Database.Error err : sobjAttach.getErrors())
        {
            if(String.isNotBlank(sErrorMessage))
            {
                sErrorMessage = sErrorMessage+';'+string.Valueof(iCounter)+'.'+err.getMessage();
            }
            else
            {
                sErrorMessage = string.Valueof(iCounter)+'.'+err.getMessage();
            }
            iCounter++;
        }  
        return sErrorMessage;
    }
    
    /*  
    * Method name : createAttachment
    * Description : This method is used to create attachment log record .
    * Return Type : Attachment_Log__c
    * Parameter   : CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO , Case
    */
     private static CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO createAttachment(CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO CaseUpdateLogAttachmentRequestDTO , Case caseRec)
    {
    
        List<Attachment_Log__c> lstAttachmentLogs = new List<Attachment_Log__c>();
        List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>{}; 
        CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO attachResp = NULL;
        String sErrorMessage = ''; 
        for(CaseServiceRestUpdateRequest_DTO_HUM.AttachmentDTO  attachmentLog : CaseUpdateLogAttachmentRequestDTO.AttachmentLog)
        {
            Attachment_Log__c sobjLog  = new Attachment_Log__c();
            sobjLog.Created_By__c = attachmentLog.CreatedBy;
            if(caseRec != null) {sobjLog.Case__c = caseRec.Id;} 
            if(attachmentLog != null)
            {
                if(!String.isBlank(attachmentLog.AttachmentDescription))
                {
                     sobjLog.Attachment_Description__c = attachmentLog.AttachmentDescription;
                }
            }
			sobjLog.Source_System__c = GLOBAL_CONSTANT_HUM.HUMCaseSource_CCPGCP;
            sobjLog.Attachment_Key__c = attachmentLog.AttachmentKey; 
            sobjLog.Attachment_Type__c = attachmentLog.AttachmentType; 
                    
            Database.SaveResult[] lstsobjAttachResult = Database.insert(new list<Attachment_Log__c>{sobjLog},false);
            for(Database.SaveResult sobjAttach  : lstsobjAttachResult)
            {
                if (sobjAttach.isSuccess())
                {
                 attachResp = new CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO(true,null,'');
                }
                else 
                {
                    sErrorMessage = errorMessage(sobjAttach);
                }                                   
            }
            HUMExceptionHelper.processSaveResults(lstsobjAttachResult,lstAttachmentLogs,'UpdateCaseInfoRest_D_HUM','updateCaseAttachmentInfo','Attachment_Log__c');                                                        
            if(String.isNotBlank(sErrorMessage))
            {                  
                return getErrorLogInstance(null,sErrorMessage,false);   
            }
            lstAttachlogItems = createAtachmentLogItem(attachmentLog.AttachmentAttribute,sobjLog);
            Database.SaveResult[] lstResultAttachLogItems = Database.insert(lstAttachlogItems,false);
            for(Database.SaveResult sobjAttach  : lstResultAttachLogItems)
            {
                if(!sobjAttach.isSuccess()) { sErrorMessage = errorMessage(sobjAttach);}                                           
            }
            HUMExceptionHelper.processSaveResults(lstResultAttachLogItems ,lstAttachlogItems ,'UpdateCaseInfoRest_D_HUM','updateCaseAttachmentInfo','Attachment_Log_Item__c');
            if(String.isNotBlank(sErrorMessage))
            {
                return getErrorLogInstance(null,sErrorMessage,false);   
            }
        }
    
        return attachResp;   
    }
    
    /*  
    * Method name : getErrorLogInstance
    * Description : This method will be used to retuen error response instance.
    * Return Type : CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO 
    * Parameter :   StatusCode , String , Boolean 
     */
    private static CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO getErrorLogInstance(StatusCode sErrorCode,String sErrorMessage,Boolean bSuccess )
    {
         CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO statMessage = new CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO(bSuccess,sErrorCode,sErrorMessage);
         
         return statMessage;    
    }
    
    /*  
     * Method name : assignValuesToCase
     * Description : This method is used to assign values to case on the basis of the input.
     * Return Type : Case
     * Parameter   : CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO , Case
     */
    private Static Case assignValuesToCase(CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO CaseUpdateRequestDTO , Case caseRec)
    {
        if(String.isNotBlank(CaseUpdateRequestDTO.OwnerId))
        {
            List<User> userList = [Select Id , Name from User where id = :CaseUpdateRequestDTO.OwnerId LIMIT :Integer.valueof(HUMConstants__c.getValues('HUMLISTSIZEONE').IntegerValue__c)];
            if(userList != null && !userList.isEMpty())
            {
                caseRec.OwnerId = userList[Integer.valueOf(HUMConstants__c.getValues('ZERO').IntegerValue__c)].id;
                caseRec.Case_Owner__c = userList[Integer.valueOf(HUMConstants__c.getValues('ZERO').IntegerValue__c)].Name;
            }
        }
        if(String.isNotBlank(CaseUpdateRequestDTO.QueueName)) caseRec.Owner_Queue__c = CaseUpdateRequestDTO.QueueName;
        
        if (CaseUpdateRequestDTO.CaseStatus.equalsIgnoreCase(String.valueOf(HUMConstants__c.getValues('ConstantO').StringValue__c)))
        {
            caseRec.Status = String.valueOf(HUMConstants__c.getValues('HUMInProgress').StringValue__c);
        }
        else if (CaseUpdateRequestDTO.CaseStatus.equalsIgnoreCase(String.valueOf(HUMConstants__c.getValues('ConstantC').StringValue__c)))
        {
            caseRec.Status = String.valueOf(HUMConstants__c.getValues('HUMClosed').StringValue__c);
        }
        return caseRec;
    }
    
    /*  
     * Method name : doCreateCaseComment
     * Description : This method is used to create case comments.
     * Return Type : CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO 
     * Parameter   : CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO , Case
     */
    private static CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO doCreateCaseComment(CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO CaseUpdateRequestDTO,Case caseRec)
    {
        CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO resp = NULL ;
        // By using the comment provided, create the casecomment record for the case.
        Boolean bCaseCommResult = true;  
        String sErrorMessage = '';       
        //CaseComment CaseCommentRec = new CaseComment(ParentId = caseRec.Id, CommentBody = CaseUpdateRequestDTO.sComment);
        Database.SaveResult[] lstResultCaseCmnt = Database.Insert(new CaseComment[] {new CaseComment(ParentId = caseRec.Id, CommentBody = CaseUpdateRequestDTO.Comment)}, false);
        for (Database.SaveResult resultCaseCmnt: lstResultCaseCmnt)
        {
            if (!resultCaseCmnt.isSuccess()) 
            {
                bCaseCommResult =  false;
                sErrorMessage = String.valueOf(HUMConstants__c.getValues('Error_Update_CaseComments').StringValue__c);
                
            }
        }
        resp = getErrorLogInstance(null,sErrorMessage, bCaseCommResult);
        return resp;
    }
    
    /*  
     * Method name : doCreateAttachment
     * Description : This method is used to create case Attachment.
     * Return Type : CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO 
     * Parameter   : CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO , Case
     */
    private static CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO doCreateAttachment(CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO CaseUpdateRequestDTO,Case caseRec)
    {
        CaseServiceRestUpdateResponse_DTO_HUM.UpdateCaseInfoResponseDTO resp ;
        // By using the attachment details provided, create the casecomment record for the case.
        Boolean bAttachmentResult = true;
        String sErrorMessage = '';
        List<Attachment__c> lstAttachmentRec = new List<Attachment__c>{};
        for(CaseServiceRestUpdateRequest_DTO_HUM.lstAttachmentDTO AttachmentDTORec : CaseUpdateRequestDTO.Attachment)
        {
            Attachment__c attachmentRec = new Attachment__c(Related_To_Case__c = caseRec.Id,Attachment_URL_Text__c = AttachmentDTORec.AttachmentUrl,Name = AttachmentDTORec.FileName);
            lstAttachmentRec.add(attachmentRec);        
        }
        if(lstAttachmentRec.size() > 0)
        {
            Database.SaveResult[] lstResultCaseAttach = Database.Insert(lstAttachmentRec, false);
            for (Database.SaveResult resultCaseAttach: lstResultCaseAttach) 
            {
                if (!resultCaseAttach.isSuccess())
                {
                    bAttachmentResult = false;
                    sErrorMessage = String.valueOf(HUMConstants__c.getValues('Error_Update_Attachment').StringValue__c);
                }
            }
        }
        resp = getErrorLogInstance(null,sErrorMessage, bAttachmentResult);
        return resp;
    }
}