/****************************************************************************************************** 
Apex Class Name  : HUMCaseAttachmentInfoToCCPService
Version          : 1.0  
Created Date     : May 9 2014 
Function         : Here all the methods to insert attachment/attachment Info records
Test Class       : HUMCaseAttachmentInfoToCCPServiceTest

****************************************************************************
Modification Log:

* Developer Name                          Code Review                 Date                        Description
*------------------------------------------------------------------------------------------------------------
* Srikant Joshi/Pranali Choudary           17452                  05/24/2014                   Original Version
* Vishal Verma                             17844                  09/11/2014                   Updated code based on review comments
* Vishal Verma                             21559                  03/13/2014                   REQ-160870 - Added Encryption before saving values in Value__c field for Attachment_Log_Item__c.
* Vishal Verma                             22167                  04/07/2014                   Defect: 177542.0001  - Changed the custom setting valuue at line 186.
* Vishal Verma                             22365                  04/15/2014                   Defect: 179417.0001  - Added additonal null check at line 54 before adding AttachmentValue to list.
* Vishal Verma                             23255                  05/20/2014                   Req - 187412  - Added Encryption before saving values in Attachment_Description__c field for Attachment_Log__c
																							   Lines changed are 25, 53 - 66 and 127
* Gargey Sharma                            23848                  06/09/2015                   Removed Encryption for AttachmentLog & AttachmentLogItem object fields as per REQ - 191228		

* Joel George         					   326972	 	          10/01/2019                   Defect 397660 Fix 																					   
*********************************************************************************************************/

global with sharing class HUMCaseAttachmentInfoToCCPService
{
    
    /*  
    * Method name : updateCaseAttachmentInfo
    * Description : This method will be used to upsert  records.
    * Return Type : List<HUMAttachmentResponseDTO>
    * Parameter   : HUMattachmentRequestDTO
    */ 
    webService static List<HUMAttachmentResponseDTO> updateCaseAttachmentInfo(HUMattachmentRequestDTO attachmentRequestDTO )
    {
        return updateCaseAttachment(attachmentRequestDTO);
    }
    
    /*  
    * Method name : updateCaseAttachment
    * Description : This method will be used to upsert the  AttachmentLog & List of  AttachmentLogItems records.
    * Return Type : List<HUMAttachmentResponseDTO>
    * Parameter   : HUMattachmentRequestDTO
    */
    private static List<HUMAttachmentResponseDTO> updateCaseAttachment(HUMattachmentRequestDTO attachmentRequestDTO )
    {
        String sErrorMessage = ''; 
        List<HUMAttachmentResponseDTO> lstAttachResp = new List<HUMAttachmentResponseDTO>();
        Map<Id,Case> mapCaseIdToInteraction = new Map<Id,Case>{};
        Attachment_Log__c sobjAttachlog = new Attachment_Log__c();  
        List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>{}; 
        
        if(attachmentRequestDTO == null){return getErrorLogInstance(null,HUMConstants__c.getValues('HUMGetCaseInfoMandatoryError').StringValue__c,false);} 
                
        Savepoint sp = Database.setSavepoint();
        try
        {
            List<Case> lstCase = [Select Id from Case where CaseNumber = :attachmentRequestDTO.CaseId LIMIT :Integer.valueof(HUMConstants__c.getValues('HUMLISTSIZEONE').IntegerValue__c)];
            Case caseRec = null;
            if(!lstCase.isEmpty()) {caseRec =lstCase[Integer.valueOf(HUMConstants__c.getValues('ZERO').IntegerValue__c)];}
            sobjAttachlog = createAttachment(attachmentRequestDTO,caseRec);     
            Database.SaveResult[] lstsobjAttachResult = Database.insert(new List<Attachment_Log__c>{sobjAttachlog},false);
            mapCaseIdToInteraction = new Map<Id,Case>([select id,(select Name,Case__c from Case_Interaction__r) from Case where id= :sobjAttachlog.Case__c]);
            for(Database.SaveResult sobjAttach  : lstsobjAttachResult)
            {
                if (sobjAttach.isSuccess())
                {
                    lstAttachResp.add(attachmentResponse(sobjAttach, mapCaseIdToInteraction.get(sobjAttachlog.Case__c)));
                }
                else 
                {
                    sErrorMessage = errorMessage(sobjAttach);
                }                                   
            }
            HUMExceptionHelper.processSaveResults(lstsobjAttachResult,new List<Attachment_Log__c>{sobjAttachlog} ,'HUMCaseAttachmentInfoToCCPService','updateCaseAttachmentInfo','Attachment_Log__c');                                                        
            if(String.isNotBlank(sErrorMessage))
            {                  
                Database.rollback(sp);
                return getErrorLogInstance(null,sErrorMessage,false);   
            }  
            lstAttachlogItems = createAtachmentLogItem(attachmentRequestDTO.AttachmentAttribute,sobjAttachlog);
            Database.SaveResult[] lstResultAttachLogItems = Database.insert(lstAttachlogItems,false);
            for(Database.SaveResult sobjAttach  : lstResultAttachLogItems)
            {
                if(!sobjAttach.isSuccess()) { sErrorMessage = errorMessage(sobjAttach);}                                           
            }
            HUMExceptionHelper.processSaveResults(lstResultAttachLogItems ,lstAttachlogItems ,'HUMCaseAttachmentInfoToCCPService','updateCaseAttachmentInfo','Attachment_Log_Item__c');
            if(String.isNotBlank(sErrorMessage))
            {
                Database.rollback(sp);
                return getErrorLogInstance(null,sErrorMessage,false);   
            }
        }
        catch(Exception ex)
        {
			Database.rollback(sp);
           	HUMExceptionHelper.logErrors(ex,'HUMCaseAttachmentInfoToCCPService','updateCaseAttachmentInfo');
           	return getErrorLogInstance(null,ex.getMessage(),false); 
        }  
        return  lstAttachResp;        
    }
    
    /*  
    * Method name : createAttachment
    * Description : This method is used to create attachment log record .
    * Return Type : Attachment_Log__c
    * Parameter   : HUMattachmentRequestDTO, Case
    */
    private static Attachment_Log__c createAttachment(HUMattachmentRequestDTO attachmentRequestDTO , Case caseRec)
    {
        Attachment_Log__c sobjAttachlog = new Attachment_Log__c();
        sobjAttachlog.Created_By__c = attachmentRequestDTO.CreatedBy;
        if(caseRec != null) {sobjAttachlog.Case__c = caseRec.Id;}  
        if(!String.isBlank(attachmentRequestDTO.AttachmentDescription))
        {
            sobjAttachlog.Attachment_Description__c = attachmentRequestDTO.AttachmentDescription;
        }
        sobjAttachlog.Attachment_Key__c = attachmentRequestDTO.AttachmentKey; 
        sobjAttachlog.Attachment_Type__c = attachmentRequestDTO.AttachmentType; 
		sobjAttachlog.Source_System__c = GLOBAL_CONSTANT_HUM.HUMCaseSource_CCPGCP;
        return sobjAttachlog;        
    }
    
    /*  
    * Method name : attachmentResponse
    * Description : This method is used to create response when the records are saved .
    * Return Type : HUMAttachmentResponseDTO
    * Parameter   : Database.SaveResult, Case
    */
    private static HUMAttachmentResponseDTO  attachmentResponse (Database.SaveResult sobjAttach , Case caseRecord )
    {
        HUMAttachmentResponseDTO attresp = new HUMAttachmentResponseDTO();
        List<string> lstCaseIntNumber = new List<string>{};
        attresp.AttachmentId = sobjAttach.getId();
        attresp.CaseId = caseRecord.Id;
        attresp.Success = sobjAttach.isSuccess();
        if(caseRecord != null)
        {
            for(Case_Interaction__c sobjCaseObj : caseRecord.Case_Interaction__r)
            {
                lstCaseIntNumber.add(sobjCaseObj.name);// Add all the case interaction number for the case record   
            }
            attresp.InteractionNumber = lstCaseIntNumber;       
        }  
        return attresp;                 
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
        Integer iCounter = Integer.valueOf(HUMConstants__c.getValues('HUMConstantOne').IntegerValue__c);
        for(Database.Error err : sobjAttach.getErrors())
        {
            if(String.isNotBlank(sErrorMessage))
            {
                sErrorMessage = sErrorMessage+sSemicolonConst+string.Valueof(iCounter)+sDotConst+err.getMessage();
            }
            else
            {
                sErrorMessage = string.Valueof(iCounter)+sDotConst+err.getMessage();
            }
            iCounter++;
        }  
        return sErrorMessage;
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
            attLogItem.Value__c = attachmentDesc.AttachmentValue;
            attLogItem.Attachment_Log__c = sobjAttachlog.Id;
            lstAttachlogItems.add(attLogItem);
        }
        return lstAttachlogItems;
    }
    
    /*  
    * Method name : getErrorLogInstance
    * Description : This method will be used to retuen error response instance.
    * Return Type : List<HUMAttachmentResponseDTO>
    * Parameter : Requires 3 arguments, ErrorCode, sErrorMessage and Success
    */
    private static List<HUMAttachmentResponseDTO> getErrorLogInstance(StatusCode sErrorCode,String sErrorMessage,Boolean bSuccess )
    {
         List<HUMAttachmentResponseDTO> lstAttachmentResponseDTO = new List<HUMAttachmentResponseDTO>();
         HUMAttachmentResponseDTO AttachmentResponseDTO = new HUMAttachmentResponseDTO();
         AttachmentResponseDTO.Success = bSuccess;
         AttachmentResponseDTO.ErrorMessage =sErrorMessage;
         if(sErrorCode != null)
            AttachmentResponseDTO.ErrorCode = string.valueof(sErrorCode);
         lstAttachmentResponseDTO.add(AttachmentResponseDTO);
         return lstAttachmentResponseDTO;    
    }   
}