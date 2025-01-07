/**************************************************************************** 
Apex Class Name  : HUMCaseUpdateLogAttachmentService
Version          : 1.0  
Created Date     : Dec 03 2014 
Function         : This class will be used by external system to update case and attachment
Test class       : HUMCaseUpdateLogAttachmentServiceTest              
***************************************************************************/
/*
Modification Log:
* Developer Name       Code Review #                                Date                        Description
*------------------------------------------------------------------------------------------------------------* 
* Krishna Tadikonda         20294                                   12/03/2014                   Original Version
* Vishal Verma              21559                                   03/13/2014                   REQ-160870 - Added Encryption before saving values in Value__c field for Attachment_Log_Item__c.
* Vishal Verma              22167                                   04/07/2014                   Defect: 177542.0001  - Changed the custom setting valuue at line 205.
* Vishal Verma              22365                                   04/15/2014                   Defect: 179417.0001  - Added additonal null check at line 51 before adding AttachmentValue to list.
* ChiranjeeviRao Ravuri     23848                                   06/10/2015                   Removed Encryption for AttachmentLog & AttachmentLogItem object fields as per REQ - 191226
* ChiranjeeviRao Ravuri     30921                                   04/07/2016                   REQ - 262439:Replace Attachment_URL__c field with Attachment_URL_Text__c field. 
* Santhi Mandava                                                    12/26/2018                   Removed dpaas call
***************************************************************************************************/
global with sharing class HUMCaseUpdateLogAttachmentService
{    
    /*  
     * Method name : updateCaseAndAttachment
     * Description : This method will be used by external system to update case and attachment.
     * Return Type : HUMStatusMessageHelper
     * Parameter   : HUMCaseUpdateLogAttachmentHelper 
     */
    webService static HUMStatusMessageHelper updateCaseAndAttachment(HUMCaseUpdateLogAttachmentHelper CaseUpdateLogAttachmentRequestDTO) {
        HUMStatusMessageHelper statusMessageHelper = constructStatusMessage(CaseUpdateLogAttachmentRequestDTO);
        return statusMessageHelper;     
    }
    
    /*  
     * Method name : constructStatusMessage
     * Description : This method will be used to construct status message.
     * Return Type : HUMStatusMessageHelper 
     * Parameter   : HUMCaseUpdateLogAttachmentHelper 
     */    
    private static HUMStatusMessageHelper constructStatusMessage(HUMCaseUpdateLogAttachmentHelper CaseUpdateLogAttachmentRequestDTO)
    {
        HUMStatusMessageHelper statusMessageHelper =  NULL;
        HUMStatusMessageHelper caseStatusMessage = new HUMStatusMessageHelper(true, null, '');
        HUMStatusMessageHelper attachmentStatusMessage = new HUMStatusMessageHelper(true, null, '');
        
        if(validateRequest(CaseUpdateLogAttachmentRequestDTO))
        {
            Savepoint sp = Database.setSavepoint(); 
            String sErrorMessage = '';  
            try
            {   
                List<Case> lstCase = [SELECT Id, caseNumber, Status, OwnerId FROM Case WHERE CaseNumber = :CaseUpdateLogAttachmentRequestDTO.sCaseNumber];
                if(lstCase != null && !lstCase.isEmpty())
                {
                    if(String.isNotBlank(CaseUpdateLogAttachmentRequestDTO.sCaseStatus)) caseStatusMessage = updateCaseStatus(CaseUpdateLogAttachmentRequestDTO ,lstCase[0]);
                    
                    if(CaseUpdateLogAttachmentRequestDTO.attachmentDTO != null) attachmentStatusMessage = logAttachment(CaseUpdateLogAttachmentRequestDTO,lstCase[0]);
                    
                    sErrorMessage = caseStatusMessage.sErrorMessage;
                    sErrorMessage = String.isblank(sErrorMessage) ? attachmentStatusMessage.sErrorMessage : sErrorMessage + attachmentStatusMessage.sErrorMessage; 
                    statusMessageHelper = new HUMStatusMessageHelper(caseStatusMessage.bStatus && attachmentStatusMessage.bStatus, null, sErrorMessage);
                                                        
                    if(!statusMessageHelper.bStatus) Database.rollback(sp);
                }
                else
                {
                    statusMessageHelper = new HUMStatusMessageHelper(false, null, system.label.HUMNoInteractionRecFound);
                }
            }
            catch(Exception ex)
            {
                Database.rollback(sp);
                statusMessageHelper = new HUMStatusMessageHelper(false, null, ex.getMessage());
            }
        }
        else
        {
            statusMessageHelper = new HUMStatusMessageHelper(false, null, system.label.HUMRequiredInputsNotFound); 
        }
        return statusMessageHelper;
    }
    
    /*  
     * Method name : validateRequest
     * Description : This method is used to validate the request and check if all the required parameters are present in the request.
     * Return Type : boolean
     * Parameter   : HUMCaseUpdateLogAttachmentHelper 
     */
    private static boolean validateRequest(HUMCaseUpdateLogAttachmentHelper CaseUpdateLogAttachmentRequestDTO)
    {
        return((String.isNotBlank(CaseUpdateLogAttachmentRequestDTO.sCaseStatus) || CaseUpdateLogAttachmentRequestDTO.attachmentDTO != null)
            && String.isNotBlank(CaseUpdateLogAttachmentRequestDTO.sCaseNumber));
    } 
    
    /*  
     * Method name : updateCaseStatus
     * Description : This method will be used to update the case fields.
     * Return Type : HUMStatusMessageHelper
     * Parameter   : HUMCaseUpdateLogAttachmentHelper, Case
     */
    private static HUMStatusMessageHelper updateCaseStatus(HUMCaseUpdateLogAttachmentHelper CaseUpdateLogAttachmentRequestDTO , Case caseRec) {
        HUMStatusMessageHelper statMessage = new HUMStatusMessageHelper(true, null, '');
        try
        {   
            caseRec =  assignValuesToCase(CaseUpdateLogAttachmentRequestDTO,caseRec);
            Database.SaveResult[] lstResultCase = Database.update(new Case[] {caseRec}, false);
            for (Database.SaveResult resultCase: lstResultCase)
            {
                 if (!resultCase.isSuccess())
                 {
                    statMessage = new HUMStatusMessageHelper(false, resultCase.getErrors()[0].getStatusCode(), resultCase.getErrors()[0].getMessage());
                 }
            }
            if (statMessage.bStatus)
            {
                // By using the comment provided, create the casecomment record for the case.
                if(String.isNotBlank(CaseUpdateLogAttachmentRequestDTO.sComment)) statMessage = doCreateCaseComment(CaseUpdateLogAttachmentRequestDTO,caseRec);
                
                // By using the attachment details provided, create the Attachment record for the case.
                if(statMessage.bStatus && CaseUpdateLogAttachmentRequestDTO.lstAttachmentDTO != null && CaseUpdateLogAttachmentRequestDTO.lstAttachmentDTO.size() > 0)
                statMessage = doCreateAttachment(CaseUpdateLogAttachmentRequestDTO,caseRec);
            }
        }
        catch(Exception ex)
        {
            statMessage = new HUMStatusMessageHelper(false, null, ex.getMessage());
        }
        return statMessage;     
    }
        
    /*  
     * Method name : logAttachment
     * Description : This method will be used to update the attachment fields.
     * Return Type : HUMStatusMessageHelper
     * Parameter   : HUMCaseUpdateLogAttachmentHelper, Case
     */
    private static HUMStatusMessageHelper logAttachment(HUMCaseUpdateLogAttachmentHelper CaseUpdateLogAttachmentRequestDTO , Case caseRec) 
    {
        String sErrorMessage = ''; 
        HUMStatusMessageHelper attachResp = NULL;
        Attachment_Log__c sobjAttachlog = new Attachment_Log__c();  
        List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>{}; 
        
        try
        {
            sobjAttachlog = createAttachment(CaseUpdateLogAttachmentRequestDTO,caseRec);     
            Database.SaveResult[] lstsobjAttachResult = Database.insert(new List<Attachment_Log__c>{sobjAttachlog},false);
            for(Database.SaveResult sobjAttach  : lstsobjAttachResult)
            {
                if (sobjAttach.isSuccess())
                {
                 attachResp = new HUMStatusMessageHelper(true,null,'');
                }
                else 
                {
                    sErrorMessage = errorMessage(sobjAttach);
                }                                   
            }
            HUMExceptionHelper.processSaveResults(lstsobjAttachResult,new List<Attachment_Log__c>{sobjAttachlog} ,'HUMCaseAttachmentInfoToCCPService','updateCaseAttachmentInfo','Attachment_Log__c');                                                        
            if(String.isNotBlank(sErrorMessage))
            {                  
                return getErrorLogInstance(null,sErrorMessage,false);   
            }
            lstAttachlogItems = createAtachmentLogItem(CaseUpdateLogAttachmentRequestDTO.attachmentDTO.AttachmentAttribute,sobjAttachlog);  
            Database.SaveResult[] lstResultAttachLogItems = Database.insert(lstAttachlogItems,false);
            for(Database.SaveResult sobjAttach  : lstResultAttachLogItems)
            {
                if(!sobjAttach.isSuccess()) { sErrorMessage = errorMessage(sobjAttach);}                                           
            }
            HUMExceptionHelper.processSaveResults(lstResultAttachLogItems ,lstAttachlogItems ,'HUMCaseAttachmentInfoToCCPService','updateCaseAttachmentInfo','Attachment_Log_Item__c');
            if(String.isNotBlank(sErrorMessage))
            {
                return getErrorLogInstance(null,sErrorMessage,false);   
            }
        }
        catch(Exception ex)
        {
           HUMExceptionHelper.logErrors(ex,'HUMCaseUpdateLogAttachmentService','logAttachment');
           return getErrorLogInstance(null,ex.getMessage(),false); 
        }
        return attachResp;
    }
    
    
    /*  
    * Method name : createAtachmentLogItem
    * Description : This method is used to create List of Attchment log item records.
    * Return Type : List<Attachment_Log_Item__c> 
    * Parameter   : List<HUMAttachmentAttributeRequestDTO> , Attachment_Log__c
    */
    private static List<Attachment_Log_Item__c> createAtachmentLogItem(List<HUMAttachmentAttributeRequestDTO> AttachmentAttribute, Attachment_Log__c sobjAttachlog)
    {
        List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>();
        for(HUMAttachmentAttributeRequestDTO attachmentDesc : AttachmentAttribute)
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
    * Parameter   : HUMCaseUpdateLogAttachmentHelper , Case
    */
    private static Attachment_Log__c createAttachment(HUMCaseUpdateLogAttachmentHelper CaseUpdateLogAttachmentRequestDTO , Case caseRec)
    {
        Attachment_Log__c sobjAttachlog = new Attachment_Log__c();
        sobjAttachlog.Created_By__c = CaseUpdateLogAttachmentRequestDTO.attachmentDTO.CreatedBy;
        if(caseRec != null) {sobjAttachlog.Case__c = caseRec.Id;}  
        if(CaseUpdateLogAttachmentRequestDTO.attachmentDTO != null)
        {
            if(!String.isBlank(CaseUpdateLogAttachmentRequestDTO.attachmentDTO.AttachmentDescription))
            {
                sobjAttachlog.Attachment_Description__c = CaseUpdateLogAttachmentRequestDTO.attachmentDTO.AttachmentDescription;
            } 
        } 
        sobjAttachlog.Attachment_Key__c = CaseUpdateLogAttachmentRequestDTO.attachmentDTO.AttachmentKey; 
        sobjAttachlog.Attachment_Type__c = CaseUpdateLogAttachmentRequestDTO.attachmentDTO.AttachmentType; 
        return sobjAttachlog;        
    
    }
    
    /*  
    * Method name : getErrorLogInstance
    * Description : This method will be used to retuen error response instance.
    * Return Type : HUMStatusMessageHelper 
    * Parameter :   StatusCode , String , Boolean 
     */
    private static HUMStatusMessageHelper getErrorLogInstance(StatusCode sErrorCode,String sErrorMessage,Boolean bSuccess )
    {
         HUMStatusMessageHelper statMessage = new HUMStatusMessageHelper(bSuccess,sErrorCode,sErrorMessage);
         
         return statMessage;    
    }
    /*  
     * Method name : assignValuesToCase
     * Description : This method is used to assign values to case on the basis of the input.
     * Return Type : Case
     * Parameter   : HUMCaseUpdateLogAttachmentHelper , Case
     */
    private Static Case assignValuesToCase(HUMCaseUpdateLogAttachmentHelper CaseUpdateRequestDTO , Case caseRec)
    {
        if(String.isNotBlank(CaseUpdateRequestDTO.sOwnerId))
        {
            List<User> userList = [Select Id , Name from User where id = :CaseUpdateRequestDTO.sOwnerId LIMIT :Integer.valueof(HUMConstants__c.getValues('HUMLISTSIZEONE').IntegerValue__c)];
            if(userList != null && !userList.isEMpty())
            {
                caseRec.OwnerId = userList[Integer.valueOf(HUMConstants__c.getValues('ZERO').IntegerValue__c)].id;
                caseRec.Case_Owner__c = userList[Integer.valueOf(HUMConstants__c.getValues('ZERO').IntegerValue__c)].Name;
            }
        }
        if(String.isNotBlank(CaseUpdateRequestDTO.sQueueName)) caseRec.Owner_Queue__c = CaseUpdateRequestDTO.sQueueName;
        
        if (CaseUpdateRequestDTO.sCaseStatus.equalsIgnoreCase(String.valueOf(HUMConstants__c.getValues('ConstantO').StringValue__c)))
        {
            caseRec.Status = String.valueOf(HUMConstants__c.getValues('HUMInProgress').StringValue__c);
        }
        else if (CaseUpdateRequestDTO.sCaseStatus.equalsIgnoreCase(String.valueOf(HUMConstants__c.getValues('ConstantC').StringValue__c)))
        {
            caseRec.Status = String.valueOf(HUMConstants__c.getValues('HUMClosed').StringValue__c);
        }
        return caseRec;
    }
    
    /*  
     * Method name : doCreateCaseComment
     * Description : This method is used to create case comments.
     * Return Type : HUMStatusMessageHelper 
     * Parameter   : HUMCaseUpdateLogAttachmentHelper , Case
     */
    private static HUMStatusMessageHelper doCreateCaseComment(HUMCaseUpdateLogAttachmentHelper CaseUpdateRequestDTO,Case caseRec)
    {
        HUMStatusMessageHelper resp = NULL ;
        // By using the comment provided, create the casecomment record for the case.
        Boolean bCaseCommResult = true;  
        String sErrorMessage = '';       
        //CaseComment CaseCommentRec = new CaseComment(ParentId = caseRec.Id, CommentBody = CaseUpdateRequestDTO.sComment);
        Database.SaveResult[] lstResultCaseCmnt = Database.Insert(new CaseComment[] {new CaseComment(ParentId = caseRec.Id, CommentBody = CaseUpdateRequestDTO.sComment)}, false);
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
     * Return Type : HUMStatusMessageHelper 
     * Parameter   : HUMCaseUpdateLogAttachmentHelper , Case
     */
    private static HUMStatusMessageHelper doCreateAttachment(HUMCaseUpdateLogAttachmentHelper CaseUpdateRequestDTO,Case caseRec)
    {
        HUMStatusMessageHelper resp ;
        // By using the attachment details provided, create the casecomment record for the case.
        Boolean bAttachmentResult = true;
        String sErrorMessage = '';
        List<Attachment__c> lstAttachmentRec = new List<Attachment__c>{};
        for(HUMCaseUpdateAttachmentDTO AttachmentDTORec : CaseUpdateRequestDTO.lstAttachmentDTO)
        {
            Attachment__c attachmentRec = new Attachment__c(Related_To_Case__c = caseRec.Id,Attachment_URL_Text__c = AttachmentDTORec.sAttachmentUrl,Name = AttachmentDTORec.sFileName);
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