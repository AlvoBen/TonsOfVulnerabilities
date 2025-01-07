/******************************************************************************************************************
Apex class Name    : UpdateCase_D_V4_HUM
Version            : 1.0
Function           : This class is for update case rest service.
Created On         : July 30 2019
Test Class         : HUMCaseServiceRestTest_V4

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Sagar MN										  7/30/2019				Original Version
*   1.1   		   Sagar MN	 									  8/19/2019				Exception Handling
*	1.2			   Sagar MN										  9/04/2019				AttachmentLog Mandatory RemovalFIX
*	1.3			   Asish Behera									  9/04/2019				Medicare case recordtype fix and case comments fix.
*	1.4			   Suraj Patil									  11/25/2019			Defect 449878 - Regression_While inserting attachment log it Is picking source field as “CCPGCP” as hard coded.
*   1.5			   Pooja Kumbhar								  06/23/2020			REQ-For RPA update the complaint field from existing Yes value to No
*   1.6			   Mohammed Noor                                  09/01/2020            US1428444 - PR00088383 - MF6 - Changes done to save the DocumentSource and AlternateDocID on Attachment object.
*   1.7			   Moshitha Gunasekaran                           07/23/2021            US2027311 - T1PRJ0001848 - (CSI1955) - Asynch Chat - Async Case and Interaction in CRM
------------------------------------------------------------------------------------------------------------------- */
Public with sharing class UpdateCase_D_V4_HUM {

    private static boolean bSwitch_2027311 = false;
    
    /*  
* Method name : updateCaseDetails
* Description : This method will be used to update the case details.
* Return Type : CaseDetailResponse_DTO_V4_HUM
* Parameter   : UpdateCaseRequest_DTO_V4_HUM request
*/    
    public static CaseDetailResponse_DTO_V4_HUM updateCaseDetails(UpdateCaseRequest_V4_HUM request){
        CaseDetailResponse_DTO_V4_HUM response= new CaseDetailResponse_DTO_V4_HUM();
        
        if(request == null)
        {
            response = getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.HUMCaseCreateMandatoryError,false);            
        }
        else
        {  
            response = updateCaseRecord(request);            
        }        
        return response;
    }
    /*  
* Method name : updateCaseRecord
* Description : This method will be used to update the case fields,Attachments and comments.
* Return Type : CaseDetailResponse_DTO_V4_HUM
* Parameter   : UpdateCaseRequest_DTO_V4_HUM request
*/ 
    public static CaseDetailResponse_DTO_V4_HUM updateCaseRecord(UpdateCaseRequest_V4_HUM request)
    {
        CaseDetailResponse_DTO_V4_HUM response= new CaseDetailResponse_DTO_V4_HUM();
        CaseDetailResponse_DTO_V4_HUM caseDetailsUpdate = new CaseDetailResponse_DTO_V4_HUM();     
        CaseDetailResponse_DTO_V4_HUM attachmentDetailsUpdate = new CaseDetailResponse_DTO_V4_HUM();
        
        if(String.isNotBlank(request.UpdateCaseRequest.CaseNumber))
        { 
            Savepoint sp = Database.setSavepoint(); 
            Boolean bStatus = false;  
            try{
                List<Case> lstCase =[SELECT Id, caseNumber, Status, OwnerId FROM Case WHERE CaseNumber = :request.UpdateCaseRequest.CaseNumber]; 
                
                if(lstCase!= null && !lstCase.isEmpty()) {
                    //update case fields
                    if(request.UpdateCaseRequest.CaseNumber!='')
                    {
                        caseDetailsUpdate = updateCaseFields(request.UpdateCaseRequest.UpdateCaseDetails,lstCase[0]);
                        if(caseDetailsUpdate.Success){
                            bStatus = true;
                        }
                        response = caseDetailsUpdate;
                    }
                    
                    //update attachments 
                    if(request.UpdateCaseRequest.UpdateCaseDetails.AttachmentLog.size()>0 && caseDetailsUpdate.Success)
                    {
                        attachmentDetailsUpdate= updateAttachmentsLogs(request.UpdateCaseRequest.UpdateCaseDetails,lstCase[0]);                    
                        if(attachmentDetailsUpdate.Success){
                            bStatus = true;
                        }
                        response = attachmentDetailsUpdate;
                    }
                    // sErrorMessage = caseDetailsUpdate.ErrorMessage;
                    // sErrorMessage = String.isblank(sErrorMessage) ? attachmentDetailsUpdate.ErrorMessage : sErrorMessage + attachmentDetailsUpdate.ErrorMessage; 
                    if(bStatus){                        
                        CaseDetailResponse_DTO_V4_HUM resp = new CaseDetailResponse_DTO_V4_HUM();
                        resp.CaseId=lstCase[0].Id;
                        resp.CaseNumber=lstCase[0].caseNumber;
                        resp.Success=True;                       
                        response =resp; 
                    }                                                       
                }else{
                    CaseDetailResponse_DTO_V4_HUM resp = new CaseDetailResponse_DTO_V4_HUM();                
                    resp.CaseNumber = request.UpdateCaseRequest.CaseNumber;
                    resp.Success = false; 
                    resp.ErrorCode = GLOBAL_CONSTANT_HUM.Error_Code_InvalidCaseId;
                    resp.ErrorMessage = GLOBAL_CONSTANT_HUM.Error_Message_IvalidCaseId;
                    response = resp;                     
                }
            }catch(Exception ex){
                Database.rollback(sp);                
                HUMExceptionHelper.logErrors(ex,'UpdateCase_D_V4_HUM','updateCaseRecord');
                //Generic Error Message
                CaseDetailResponse_DTO_V4_HUM resp = new CaseDetailResponse_DTO_V4_HUM();                
                resp.CaseNumber = request.UpdateCaseRequest.CaseNumber;
                resp.Success = false; 
                resp.ErrorCode = GLOBAL_CONSTANT_HUM.Error_Code_InvalidCaseId;
                resp.ErrorMessage = GLOBAL_CONSTANT_HUM.Error_Message_IvalidCaseId;
                response = resp;                 
            }
        }
        else{          
            CaseDetailResponse_DTO_V4_HUM resp = new CaseDetailResponse_DTO_V4_HUM();                
            resp.CaseNumber = request.UpdateCaseRequest.CaseNumber;
            resp.Success = false; 
            resp.ErrorCode = GLOBAL_CONSTANT_HUM.Error_Code_InvalidCaseId;
            resp.ErrorMessage = GLOBAL_CONSTANT_HUM.Error_Message_IvalidCaseId;
            response = resp;            
        }       
        return response;
    }
    
    /*  
* Method name : updateCaseFields
* Description : This method will be used to update the case fields,Attachments and comments.
* Return Type : CaseDetailResponse_DTO_V4_HUM
* Parameter   : UpdateCaseRequest_DTO_V4_HUM request
*/   
    public Static CaseDetailResponse_DTO_V4_HUM updateCaseFields(UpdateCaseDetails_DTO_V4_HUM updateReq,Case caseRec)
    {     
        CaseDetailResponse_DTO_V4_HUM respValue = new CaseDetailResponse_DTO_V4_HUM();
        String sErrorMessage='';        
        try{            
            caseRec =  assignValuesToCase(updateReq,caseRec);            
            if(caseRec!=Null)
            {
                Database.SaveResult[] updateResult = Database.update(new Case[] {caseRec} ,false);
                
                for(Database.SaveResult resultCase:updateResult){
                    if (!resultCase.isSuccess()){
                        //Generic Error Message
                        CaseDetailResponse_DTO_V4_HUM resp = new CaseDetailResponse_DTO_V4_HUM();
                        resp.CaseId = caseRec.Id;
                        resp.CaseNumber = caseRec.caseNumber;
                        resp.Success = false; 
                        resp.ErrorCode = GLOBAL_CONSTANT_HUM.Error_Code_UpdateCasev4;
                        resp.ErrorMessage = GLOBAL_CONSTANT_HUM.Error_Message_UpdateCasev4;
                        respValue = resp;                                                
                    } else{
                        CaseDetailResponse_DTO_V4_HUM newRespValue = new CaseDetailResponse_DTO_V4_HUM();
                        newRespValue.CaseId=caseRec.Id;                                          	
                        newRespValue.Success=true; 
                        newRespValue.CaseNumber=caseRec.Case_Number__c;
                        respValue=newRespValue;                       
                    } 
                }            
                if(respValue.Success)
                {          
                    //Create the Comments provided in the request
                    if(!updateReq.CaseComments.isEmpty())
                    {                        
                        respValue = createCaseComments(updateReq,caseRec);                       
                    }
                    //create Attachment record provided in the request
                    if(respValue.Success && updateReq.Attachment!=null && updateReq.Attachment.size()>0)
                    {                        
                        respValue = createCaseAttachments(updateReq,caseRec);                       
                    }
                }               
            }   
        }catch(DMlException ex)
        {            
            //Error Log
            HUMExceptionHelper.logErrors(ex,'UpdateCase_D_V4_HUM','updateCaseFields');           
            //Generic Error Message
            CaseDetailResponse_DTO_V4_HUM resp = new CaseDetailResponse_DTO_V4_HUM();
            resp.CaseId = caseRec.Id;
            resp.CaseNumber = caseRec.caseNumber;
            resp.Success = false; 
            resp.ErrorCode = GLOBAL_CONSTANT_HUM.Error_Code_UpdateCasev4;
            resp.ErrorMessage = GLOBAL_CONSTANT_HUM.Error_Message_UpdateCasev4;
            respValue = resp;                       
        }catch(Exception ex)
        {         
            //Error Log
            HUMExceptionHelper.logErrors(ex,'UpdateCase_D_V4_HUM','updateCaseFields');           
            //Generic Error Message
            CaseDetailResponse_DTO_V4_HUM resp = new CaseDetailResponse_DTO_V4_HUM();
            resp.CaseId = caseRec.Id;
            resp.CaseNumber = caseRec.caseNumber;
            resp.Success = false; 
            resp.ErrorCode = GLOBAL_CONSTANT_HUM.Error_Code_UpdateCasev4;
            resp.ErrorMessage = GLOBAL_CONSTANT_HUM.Error_Message_UpdateCasev4;
            respValue = resp;                        
        }               
        return respValue;
    } 
    /*  
* Method name : assignValuesToCase
* Description : This method will be used to assign case fields with values from the case details request.
* Return Type : Case
* Parameter   : UpdateCaseRequest_DTO_V4_HUM request
*/
    public static Case assignValuesToCase(UpdateCaseDetails_DTO_V4_HUM updateReq,Case caseRec)
    {
        List<CTCI_Junction__c> obj = new List<CTCI_Junction__c>(); 
        Case caseObj = new Case();
        caseObj.Id = caseRec.Id;               
        
        if(String.isNotBlank(updateReq.CaseType)) caseObj.Type = updateReq.CaseType;
        if(String.isNotBlank(updateReq.CaseStatus)) caseObj.Status = updateReq.CaseStatus;
        if(String.isNotBlank(updateReq.Origin)) caseObj.Origin = updateReq.Origin;
        if(String.isNotBlank(updateReq.CaseSubType)) caseObj.Subtype__c = updateReq.CaseSubType;
        if(String.isNotBlank(updateReq.Priority)) caseObj.Priority = updateReq.Priority;
        if(String.isNotBlank(updateReq.TopicName)) caseObj.Topic__c = updateReq.TopicName;
        if(String.isNotBlank(updateReq.WorkQueueViewName)) caseObj.Work_Queue_View_Name__c = updateReq.WorkQueueViewName;
        if(String.isNotBlank(updateReq.CreatedQueue)) caseObj.Created_By_Queue__c = updateReq.CreatedQueue;
        if(String.isNotBlank(updateReq.OwnerQueue)) caseObj.Owner_Queue__c = updateReq.OwnerQueue;
        if(String.isNotBlank(updateReq.ServiceCenter)) caseObj.Service_Center__c = updateReq.ServiceCenter;
        if(String.isNotBlank(updateReq.Department)) caseObj.Department__c = updateReq.Department;
        bSwitch_2027311 =  HUMUtilityHelper.isCRMFunctionalityON('2027311');
        /* updating the CaseOwner and OwnerId */ 
        if(bSwitch_2027311 && string.isNotBlank(updateReq.CaseOwnerId))
        {
            list<User> lstUsr = [Select Id, Name, FirstName, LastName, Network_User_Id__c, IsActive from User where Network_User_Id__c = :updateReq.CaseOwnerId AND IsActive=true limit 1];
            if(null != lstUsr && !lstUsr.isEmpty())
            {
                if (Schema.sObjectType.Case.fields.Case_Owner__c.isUpdateable()) caseObj.Case_Owner__c = lstUsr[0].FirstName + ' ' + lstUsr[0].LastName;
                if (Schema.sObjectType.Case.fields.OwnerId.isUpdateable()) caseObj.OwnerId  = lstUsr[0].Id;
            }  
        }                  
        /* updating the complaintReason and ComplaintType to null when Complaint is No*/              
        if(String.isNotBlank(updateReq.Complaint))
        { 
        caseObj.Complaint__c = updateReq.Complaint; 
        if((GLOBAL_CONSTANT_HUM.NO_Complaint_GARights).equalsIgnoreCase(updateReq.Complaint))  
        {
            caseObj.Complaint_Reason__c = '';
            caseObj.Complaint_Type__c = ''; 
        }
        else
        {
            if(String.isNotBlank(updateReq.ComplaintReason)) caseObj.Complaint_Reason__c = updateReq.ComplaintReason;
            if(String.isNotBlank(updateReq.ComplaintType)) caseObj.Complaint_Type__c = updateReq.ComplaintType;
        }  
        }   
        /* updating the GAReason field to null when GARightsGiven is No*/
        if(String.isNotBlank(updateReq.GARightsGiven))
        {
        caseObj.G_A_Rights_Given__c = updateReq.GARightsGiven;
        if((GLOBAL_CONSTANT_HUM.NO_Complaint_GARights).equalsIgnoreCase(updateReq.GARightsGiven))  
        {
            caseObj.G_A_Reason__c = '';
        }
        else
        {
            if(String.isNotBlank(updateReq.GAReason)) caseObj.G_A_Reason__c = updateReq.GAReason;
        }
        }
        if(String.isNotBlank(updateReq.FollowupDueDate)){
            Date dateRes = parseDateFormat(updateReq.FollowupDueDate);            
            caseObj.Follow_up_Due_Date__c = dateRes;
        }
        if(String.isNotBlank(updateReq.ElectionType)) caseObj.Election_Type_Code__c = updateReq.ElectionType;
        if(String.isNotBlank(updateReq.OGOResolutionDate)) caseObj.OGO_Resolution_Date__c = date.parse(updateReq.OGOResolutionDate);
        if(String.isNotBlank(updateReq.OGOResolutionType)) caseObj.OGO_Resolution_Type__c = updateReq.OGOResolutionType;
        if(String.isNotBlank(updateReq.OralGrievanceCategory)) caseObj.Oral_Grievance_Category__c = updateReq.OralGrievanceCategory;
        if(String.isNotBlank(updateReq.OralGrievanceSubCategory)) caseObj.Oral_Grievance_Sub_Category__c = updateReq.OralGrievanceSubCategory;
        if(String.isNotBlank(updateReq.ClassificationType)) caseObj.Classification_Type__c = updateReq.ClassificationType;      	
        if(string.isNotBlank(updateReq.ClassificationType) && string.isNotBlank(updateReq.Classification) && string.isNotBlank(updateReq.Intent))
        {            
            obj = [SELECT Id, Classification__c, Classification_Type__c, Intent__c FROM CTCI_Junction__c 
                   WHERE Classification_Type__r.Name =:updateReq.ClassificationType AND 
                   Classification__r.Name =:updateReq.Classification AND 
                   Intent__r.Name =:updateReq.Intent
                   AND Intent__r.IS_ACTIVE__C= true
                   AND Classification__r.IS_ACTIVE__C=true AND CTCI_Junction__c.ISACTIVE__C=true limit 1];
        }
        
        if(obj.size()>0){
            caseObj.CTCI_List__c = obj[0].Id;
            if( String.IsNotBlank(obj[0].Classification__c)){
                caseObj.Classification_Id__c = obj[0].Classification__c;
            }
            if(String.IsNotBlank(obj[0].Intent__c)){
                caseObj.Intent_Id__c = obj[0].Intent__c; 
            }
        }
        
        // medicare record type population
        if(String.isNotBlank(updateReq.CaseType) && String.isNotBlank(updateReq.CaseSubType)){
             caseObj.recordtypeid =  HUMUtilityHelper.getCaseRecordType(null,updateReq.CaseType , updateReq.CaseSubType, null,true ); 
        }
       
        return caseObj;
    }
    /*  
* Method name : updateAttachmentsLogs
* Description : This method will be used to update the attachment fields.
* Return Type : CaseDetailResponse_DTO_V4_HUM
* Parameter   : UpdateCaseRequest_DTO_V4_HUM updateReq, Case
*/  
    public static CaseDetailResponse_DTO_V4_HUM updateAttachmentsLogs(UpdateCaseDetails_DTO_V4_HUM updateReq,Case caseRec)
    {        
        CaseDetailResponse_DTO_V4_HUM caseAttachLogResp= new CaseDetailResponse_DTO_V4_HUM();
        List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog> attachmentList = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>();
        try{
            attachmentList=updateReq.AttachmentLog;                        
            if(!attachmentList.isEmpty()){
                caseAttachLogResp = createAttachmentLog(attachmentList,caseRec);				               
            } 
            if(!caseAttachLogResp.Success){
                CaseDetailResponse_DTO_V4_HUM resp = new CaseDetailResponse_DTO_V4_HUM();
                resp.CaseId = caseRec.Id;
                resp.CaseNumber = caseRec.caseNumber;
                resp.Success = false; 
                resp.ErrorCode = GLOBAL_CONSTANT_HUM.Error_Code_UpdateCasev4;
                resp.ErrorMessage = GLOBAL_CONSTANT_HUM.Error_Message_UpdateCasev4;
                caseAttachLogResp = resp;  
            }
        }
        catch(Exception ex){           
            return getErrorLogInstance(null,ex.getMessage(),false);
        }       
        Return caseAttachLogResp;
    }
    /*  
* Method name : parseDateFormat
* Description : This method will be used to parse date from the request followupduedate 
* Return Type : Date
* Parameter   : String inputDate
*/
    public static Date parseDateFormat(String inputDate)
    {
        Date	dateRes		= null;
        //	1 - Try locale specific mm/dd/yyyy or dd/mm/yyyy	
        try {
            String candDate		= inputDate.substring(0,Math.min(10,inputDate.length()));// grab date portion only m[m]/d[d]/yyyy , ignore time
            dateRes 	= Date.parse(candDate);
        }
        catch (Exception e) {}
        
        if (dateRes == null) {
            //	2 - Try yyyy-mm-dd			
            try {
                String candDate		= inputDate.substring(0,10); // grabs date portion only, ignore time, if any
                dateRes				= Date.valueOf(candDate);
            }
            catch (Exception e) {} 
        }        
        return dateRes;
    }
    /*  
* Method name : createAttachmentLog
* Description : This method is used to create attachment log record .
* Return Type : CaseDetailResponse_DTO_V4_HUM
* Parameter   : UpdateCaseRequest_DTO_V4_HUM , Case
*/
    public static CaseDetailResponse_DTO_V4_HUM createAttachmentLog(List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog> updateReq,Case caseRec)
    {        
        CaseDetailResponse_DTO_V4_HUM  attachResp = new CaseDetailResponse_DTO_V4_HUM();  
        String sErrorMessage = ''; 
        List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>{};        
            
            for(UpdateCaseDetails_DTO_V4_HUM.AttachmentLog attachlog : updateReq)
        {  
            Attachment_Log__c sobjLog  = new Attachment_Log__c();         
            if(caseRec != null)
            {
                sobjLog.Case__c = caseRec.Id;
            }
            if(attachlog!=Null)
            {
                if(String.isNotBlank(attachlog.AttachmentDescription))
                {
                    sobjLog.Attachment_Description__c = attachlog.AttachmentDescription; 
                }
            }
			// Defect 449878 - removed hard coded source value and setting created by field
            sobjLog.Source_System__c = attachlog.Source;
            sobjLog.Attachment_Key__c = attachlog.AttachmentKey;
            sobjLog.Attachment_Type__c	= attachlog.AttachmentType; 
			sobjLog.Created_By__c = GLOBAL_CONSTANT_HUM.DatapowerUserName;			
            if(sobjLog!=Null)
            {
                Database.SaveResult[] lstObjAtttachResult = Database.insert(new list<Attachment_Log__c>{sobjLog},false);
                for(Database.SaveResult sobjAttach : lstObjAtttachResult)
                {                     
                    if(sobjAttach.isSuccess())
                    {                       
                        attachResp.CaseId=caseRec.Id;                        
                        attachResp.Success=True; 
                    }
                    else
                    {
                        sErrorMessage = errorMessage(sobjAttach);                        
                    }
                }         
                if(String.isNotBlank(sErrorMessage))
                {                  
                    attachResp = getErrorLogInstance(lstObjAtttachResult[0].getId(),sErrorMessage,false);   
                }
            } 
            if(attachlog.AttachmentAttribute.size()>0 && sobjLog!=Null)
            {
                //attachment log item                
                lstAttachlogItems = createAtachmentLogItem(attachlog.AttachmentAttribute,sobjLog);              
                if(!lstAttachlogItems.isEmpty()){
                    Database.SaveResult[] lstResultAttachLogItems = Database.insert(lstAttachlogItems,false);
                    for(Database.SaveResult sobjAttach  : lstResultAttachLogItems)
                    {
                        if(!sobjAttach.isSuccess())
                        { 
                            sErrorMessage = errorMessage(sobjAttach);                        
                        }
                        else
                        {
                            attachResp.CaseId=caseRec.Id;                        
                            attachResp.Success=True;   
                        }
                    }               
                    if(String.isNotBlank(sErrorMessage))
                    {
                        attachResp= getErrorLogInstance(lstResultAttachLogItems[0].getId(),sErrorMessage,false);   
                    }
                }
            }                
        }              
        return attachResp;
    }    
    /*  
* Method name : createAtachmentLogItem
* Description : This method is used to create List of Attchment log item records.
* Return Type : List<Attachment_Log_Item__c> 
* Parameter   : List<CaseServiceRestUpdateRequest_DTO_HUM.AttachmentAttributeDTO> , Attachment_Log__c
*/
    private static List<Attachment_Log_Item__c> createAtachmentLogItem(List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute> AttachmentAttribute, Attachment_Log__c sobjAttachlog)
    {
        List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>();
        for(UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachmentDesc : AttachmentAttribute)
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
* Method name : updateComments
* Description : This method will be used to update the Comments fields from the request.
* Return Type : CaseDetailResponse_DTO_V4_HUM
* Parameter   : UpdateCaseRequest_DTO_V4_HUM.CaseComment updateReq, Case
*/ 
    public static CaseDetailResponse_DTO_V4_HUM createCaseComments(UpdateCaseDetails_DTO_V4_HUM updateReq,Case caseRec)
    {        
        
		List<CaseComment> caseCommentsList = new List<CaseComment>();
		CaseDetailResponse_DTO_V4_HUM caseCommentsResp = new CaseDetailResponse_DTO_V4_HUM();		
		for(UpdateCaseDetails_DTO_V4_HUM.CaseComment cmt:updateReq.CaseComments)
		{
			CaseComment objCaseComment = new CaseComment();
			objCaseComment.commentBody = cmt.CommentBody;
			objCaseComment.ParentId = caseRec.Id;
			caseCommentsList.add(objCaseComment);				
		}
		
        Boolean bCaseCommResult = true;  
        String sErrorMessage = '';
        
        if(caseCommentsList.size()>0) { 
            try{
                Database.SaveResult[] lstResultCaseCmnt= Database.insert(caseCommentsList, false);            
                for (Database.SaveResult resultCaseCmnt: lstResultCaseCmnt)
                {            
                    if (!resultCaseCmnt.isSuccess()) 
                    {
                        bCaseCommResult =  false;
                        sErrorMessage = String.valueOf(HUMConstants__c.getValues('Error_Update_CaseComments').StringValue__c);                          	                    
                    }else{
                        caseCommentsResp.CaseId=caseRec.Id;                
                        caseCommentsResp.Success=true;
                    }
                }           
                if(String.isNotBlank(sErrorMessage)){
                    caseCommentsResp = getErrorLogInstance(lstResultCaseCmnt[0].getId(),sErrorMessage, bCaseCommResult);
                }
            }Catch(Exception ex){
                HUMExceptionHelper.logErrors(ex,'UpdateCase_D_V4_HUM','createCaseComments');
                //Generic Error Message
                CaseDetailResponse_DTO_V4_HUM resp = new CaseDetailResponse_DTO_V4_HUM();
                resp.CaseId = caseRec.Id;
                resp.CaseNumber = caseRec.caseNumber;
                resp.Success = false; 
                resp.ErrorCode = GLOBAL_CONSTANT_HUM.Error_Code_UpdateCasev4;
                resp.ErrorMessage = GLOBAL_CONSTANT_HUM.Error_Message_UpdateCasev4;
                caseCommentsResp = resp;                
            }
        }       
        return caseCommentsResp;        
    }
    /*  
* Method name : createCaseAttachments
* Description : This method is used to create case Attachment.
* Return Type : CaseDetailResponse_DTO_V4_HUM 
* Parameter   : UpdateCaseRequest_DTO_V4_HUM.UpdateCaseDetailsRequestDTO ,Case 
*/
    public static CaseDetailResponse_DTO_V4_HUM createCaseAttachments(UpdateCaseDetails_DTO_V4_HUM createAttachReq,Case caseRec)
    {
        CaseDetailResponse_DTO_V4_HUM resp =new CaseDetailResponse_DTO_V4_HUM();        
        Boolean bAttachmentResult = true;
        String sErrorMessage = '';
        Attachment__c attachmentRec;
        List<Attachment__c> lstAttachmentRec = new List<Attachment__c>{};
            for(UpdateCaseDetails_DTO_V4_HUM.Attachment AttachmentDTORec : createAttachReq.Attachment)
        {
            if(string.ISNOTBLANK(AttachmentDTORec.AttachmentUrl) || string.ISNOTBLANK(AttachmentDTORec.VersionNumber) && string.ISNOTBLANK(AttachmentDTORec.DocId))
            {
                if(AttachmentDTORec.FileName !=null)
                {
                    attachmentRec = new Attachment__c(Name = AttachmentDTORec.FileName,Related_To_Case__c = caseRec.Id,Version_Id__c = AttachmentDTORec.VersionNumber,
                                                      DocId__c = AttachmentDTORec.DocId,Attachment_URL_Text__c=AttachmentDTORec.AttachmentUrl,Document_Source__c=AttachmentDTORec.DocumentSource,AlternateDocId__c=AttachmentDTORec.AlternateDocId);
                    lstAttachmentRec.add(attachmentRec); 
                }
                else{
                    attachmentRec = new Attachment__c(Related_To_Case__c = caseRec.Id,Version_Id__c = AttachmentDTORec.VersionNumber, DocId__c = AttachmentDTORec.DocId,
                                                      Attachment_URL_Text__c=AttachmentDTORec.AttachmentUrl,Document_Source__c=AttachmentDTORec.DocumentSource,AlternateDocId__c=AttachmentDTORec.AlternateDocId);
                    lstAttachmentRec.add(attachmentRec); 
                }                      
            }                  
        }
        if(lstAttachmentRec.size() > 0)
        {
            try{
                Database.SaveResult[] lstResultCaseAttach = Database.Insert(lstAttachmentRec, false);            
                for (Database.SaveResult resultCaseAttach: lstResultCaseAttach) 
                {               
                    if (!resultCaseAttach.isSuccess())
                    {
                        bAttachmentResult = false;
                        sErrorMessage = String.valueOf(HUMConstants__c.getValues('Error_Update_Attachment').StringValue__c);              	                    
                    }
                    else
                    {
                        resp.CaseId=caseRec.Id;
                        resp.CaseNumber=caseRec.Case_Number__c;                  
                        resp.Success= resultCaseAttach.isSuccess(); 
                    }
                }
                if(String.isNotBlank(sErrorMessage))
                {
                    resp = getErrorLogInstance(lstResultCaseAttach[0].getId(),sErrorMessage, bAttachmentResult);  
                }
            }catch(Exception ex){
                HUMExceptionHelper.logErrors(ex,'UpdateCase_D_V4_HUM','createCaseAttachments');
                //Generic Error Message
                CaseDetailResponse_DTO_V4_HUM response = new CaseDetailResponse_DTO_V4_HUM();
                response.CaseId = caseRec.Id;
                response.CaseNumber = caseRec.caseNumber;
                response.Success = false; 
                response.ErrorCode = GLOBAL_CONSTANT_HUM.Error_Code_UpdateCasev4;
                response.ErrorMessage = GLOBAL_CONSTANT_HUM.Error_Message_UpdateCasev4;                           
                resp = response;                
            }
        }       
        return resp;    
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
* Method name : getErrorLogInstance
* Description : This method will be used to retuen error response instance.
* Return Type : CaseDetailResponse_DTO_V4_HUM
* Parameter   : StatusCode , String , Boolean 
*/
    private static CaseDetailResponse_DTO_V4_HUM getErrorLogInstance(String sErrorCode,String sErrorMessage,Boolean bSuccess)
    {
        CaseDetailResponse_DTO_V4_HUM statMessage = new CaseDetailResponse_DTO_V4_HUM();
        statMessage.ErrorCode = sErrorCode;
        statMessage.ErrorMessage = sErrorMessage;
        statMessage.Success = bSuccess;        
        return statMessage;    
    }
}