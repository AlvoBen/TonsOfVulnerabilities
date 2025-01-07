/******************************************************************************************************************
Apex class Name    : GetCaseInfoRest_D_HUM_V2
Version            : 1.0
Function           : This class is for Data Access Layer for for case related activities
Created On         : Jan 20 2019
Test Class         : GetCaseInfoRest_D_HUM_V2Test

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Divya Proddaturi              340085           02/27/2019        Original Version
*   1.1            Mohammed Noor                 319700           03/04/2019        Fix for Defect 404600.
*   1.2            Mohammed Noor                 343349           03/08/2019        REQ - 386797 - Changes done to Support additional Entity types.
*   1.3            Mohammed Noor                 345152           03/18/2019        Fix for Defect 407116.
*   1.4            Moshitha Gunasekaran                           04/02/2019        REQ - 392822 Tech - SF - Enhance GetCaseInfo method of Case Service
*   1.5            Santhi Mandava                354419           04/15/2019        Replaced "Member_Id_Value__c" with Name 
*   1.6            Santhi Mandava                356422           05/16/2019        Replaced member id SOQL with SOSL     
*   1.7            Mohammed Noor                                  07/01/2019        Changes done for REQ - 403693.
*   1.8            Mohammed Noor                                  07/25/2019        REQ - 403693 - Fix for Defect#430513
*   1.9            Moshitha Gunasekaran                           08/02/2019        Fix for Defect#431204
* 	2.0			   Mohammed Noor                                  09/01/2020        US1428444 - PR00088383 - MF6 - Changes done to retrieve DocumentSource and AlternateDocId on Attachment object.
------------------------------------------------------------------------------------------------------------------- */


global with sharing class GetCaseInfoRest_D_HUM_V2 {
     
   private static Date dtFromDate = null;
   private static Date dtToDate = null;
   private static DateTime dtFromDateTimeGMT = null;
   private static DateTime dtToDateTimeGMT = null;
   private static string sWhereClause='';
   private static list<Case> lstCaseRec = new list<Case>();
   private static set<string> setCaseNumber = new set<string>();
   private static set<string> setCaseId =new set<string>(); 
   private static string sCaseInfoMandatoryError = '';
   private static string sCaseInfoCaseIDError = '';
   private static string sCaseInfoMandatoryInfoError = '';
   private static string sCaseInfoMDateRangeError = '';
   private static CaseServiceRestGetRequest_DTO_HUM_V2 caseInfoRequestDTO=new CaseServiceRestGetRequest_DTO_HUM_V2();                                 
   private static string sPaging='';
   private static list<string> lstReqCaseNumbers=new list<string>();
   private static set<string> WorkQueueName=new set<string>();
   private static set<string> QueueViewName=new set<string>();
   private static map<string,set<string>> mapQueue=new map<string,set<string>>();
   private static string sCount='';
   private static set<string> setType = new set<string>();
   private static set<string> setCaseSubtype = new set<string>();
   private static Integer iOffset;
   private static list<string> lstStatus=new list<string>();      
   private static map<string,set<string>> mapInquirecriteria=new map<string,set<string>>();                       
   private static map<string,set<ConsumerID__c>> mapAcctWithConsumer = new map<string,set<ConsumerID__c>>();       
    
    private static string sOrderBy = ' ORDER BY CreatedDate DESC';  
    private static string sCaseInfoQuery = 'SELECT ID,AccountID,Account.Name,Interacting_About__c,CreatedDate,CreatedBy.Name,LastModifiedBy.Name,Created_By_Queue__c,'+
        'LastModifiedby_Queue__c,Re_Open_Case_Age__c,Re_Open_Case_Date__c,Case_Age__c,CaseNumber,Origin,OwnerID,'+
        'Owner.Name,CTCI_List__r.Classification__c,CTCI_List__r.Classification__r.Name,IsClosed,Complaint__c,Complaint_Reason__c,'+
        'ContactID,CreatedByID,ClosedDate,Days_Since_Last_Modified__c,Due_Date__c,IsEscalated,Funding_Type__c,G_A_Reason__c,'+
        'G_A_Rights_Given__c,Interacting_With__r.Home_Office_Account__c,Interacting_About_Type__c,Interacting_With__c,'+
        'Interacting_With_Type__c,LastModifiedDate,LastModifiedByID,Line_of_Business__c,Plan_Issue_State__c,Policy_Member__r.Name,'+
        'Policy_Platform__c,Priority,Product__c,Product_Type__c,Owner_Queue__c,CTCI_List__r.Intent__c,CTCI_List__r.Intent__r.Name,Reopened__c,'+
        'Status,Subject,Subtype__c,Type,Interacting_With__r.Enterprise_ID__c,Account.Enterprise_ID__c,Account.BirthDate__c,Behavioral_Health__c,'+
        'Case_Owner__c,CTCI_List__r.Classification_Type__c,CTCI_List__r.Classification_Type__r.Name,Department__c,'+
        'Employer_Group_Name__c,Exchange__c,Exchange_Type__c,Follow_up_Due_Date__c,Follow_Up_Needed__c,General_Case__c,'+
        'HO_Dental_Case__c,HO_Medical_Case__c,Interacting_With_Name__c,IsClosedOnCreate,Language_Preference__c,NPI_ID__c,Plan_Segment_Size__c,'+
        'Previous_Case_Owner_Role__c,Selling_Market_Number__c,Service_Center__c,State_Mem_Resid_Add__c,Tax_ID__c,'+
        'Texas_Complaint_Letter_Requested__c,Topic__c,Work_Queue_View_Name__c,Interacting_With__r.Name, Interacting_With__r.BirthDate__c,'+
        'Account.RecordTypeId, Account.RecordType.Name,Account.NPI_ID__c,Account.Agent_ID__c,Account.Group_Number__c,Interacting_With__r.NPI_ID__c,Interacting_With__r.Agent_ID__c,'+
        'Interacting_With__r.Group_Number__c,Interacting_With__r.RecordTypeId,Interacting_With__r.RecordType.Name FROM Case WHERE';
                
    private static string sCaseSummaryQuery = 'SELECT ID,CaseNumber,Work_Queue_View_Name__c,Owner_Queue__c,CreatedBy.Name,Origin,'+
        'Subtype__c,Type,Status,CreatedDate FROM Case WHERE';
         
    /*
    * getCaseRelatedInfo
    * <p>
    * This method will be used to get Caserelated info
    *
    * @param           CaseServiceRestGetRequest_DTO_HUM_V2
    * @return          CaseServiceRestGetResponse_DTO_HUM_V2                  
    */     
    public static CaseServiceRestGetResponse_DTO_HUM_V2 getCaseRelatedInfo(CaseServiceRestGetRequest_DTO_HUM_V2 request)
    {
        //Initialize the variables
        dtFromDate = null;
        dtToDate = null;       
        sCaseInfoMandatoryError = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOMANDATORYERROR_HUM;
        sCaseInfoCaseIDError = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOCASEIDERROR_HUM;
        sCaseInfoMandatoryInfoError = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOMANDATROYINFOERROR_HUM;
        sCaseInfoMDateRangeError=GLOBAL_CONSTANT_HUM.CASESERVICE_GETCASEINFODATERANGERROR_HUM;
        caseInfoRequestDTO=request;
        if(string.isNotBlank(caseInfoRequestDTO.GetCaseInfo.StartPaging))    sPaging=caseInfoRequestDTO.GetCaseInfo.StartPaging;
        
        if(caseInfoRequestDTO.GetCaseInfo.Status!= null && !caseInfoRequestDTO.GetCaseInfo.Status.isEmpty())    lstStatus=caseInfoRequestDTO.GetCaseInfo.Status;
        if(caseinforequestDTO.GetCaseInfo.CaseId!=null && !caseinforequestDTO.GetCaseInfo.CaseId.isEmpty()) lstReqCaseNumbers=caseinforequestDTO.GetCaseInfo.CaseId;
        CaseServiceRestGetResponse_DTO_HUM_V2 response = new CaseServiceRestGetResponse_DTO_HUM_V2();
        response.GetCaseInfoResponse=new CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO();
        if(string.isNotBlank(sPaging) && Integer.valueOf(sPaging)!=0)
        {
            iOffset=Integer.valueOf(sPaging)-1;
        }
        else
        {
            iOffset=0;
        }
        if(string.isNotBlank(caseInfoRequestDTO.GetCaseInfo.StartDate)) dtFromDate = EntitySearch_H_HUM.parsestringToDate(caseInfoRequestDTO.GetCaseInfo.StartDate);
        if(string.isNotBlank(caseInfoRequestDTO.GetCaseInfo.EndDate))   dtToDate = EntitySearch_H_HUM.parsestringToDate(caseInfoRequestDTO.GetCaseInfo.EndDate);
        
       //If the request is empty return an error message
        if(caseInfoRequestDTO == null)
        {       
            response.GetCaseInfoResponse.CaseInfoResponseList = getErrorLogInstance(null,sCaseInfoMandatoryInfoError,false);            
            return response; 
        }
        
        //First check : check if the caseID is provIded.       
        else if(lstReqCaseNumbers!=null && (!lstReqCaseNumbers.isEmpty()))
        {
            response.GetCaseInfoResponse = populateCaseInfoCase();            
            return response;
        }
        //Second check if Inquire about criteria or Inquire for criteria is given
        else if(((caseInfoRequestDTO.GetCaseInfo.InteractingAboutCriteria!= null && !caseInfoRequestDTO.GetCaseInfo.InteractingAboutCriteria.isEmpty())
                 || (caseInfoRequestDTO.GetCaseInfo.InteractingWithCriteria!= null && !caseInfoRequestDTO.GetCaseInfo.InteractingWithCriteria.isEmpty()))
                 && (string.isNotBlank(caseInfoRequestDTO.GetCaseInfo.StartPaging)&& string.isNotBlank(caseInfoRequestDTO.GetCaseInfo.StartDate) && string.isNotBlank(caseInfoRequestDTO.GetCaseInfo.EndDate)))     
        {   
            if(dtFromDate!= null && dtToDate!= null && dtToDate>=dtFromDate.addYears(1))
            {
                response.GetCaseInfoResponse.CaseInfoResponseList=getErrorLogInstance(null,sCaseInfoMDateRangeError,false);
                return response;
            }
            else
            {
                if(dtToDate!=null)  dtToDate = dtToDate.addDays(1);
				DateTime tempFromDateLocal = DateTime.newInstance(dtFromDate.year(), dtFromDate.month(), dtFromDate.day(), 0, 0, 0); //constructs the start datetime in User's local Time zone. 
                DateTime tempToDateLocal = DateTime.newInstance(dtToDate.year(), dtToDate.month(), dtToDate.day(), 0, 0, 0);  //constructs the End datetime in User's local time zone.
				Date tempDateGMT = tempFromDateLocal.dateGMT();  //get the Date part in GMT
				Time tempTimeGMT = tempFromDateLocal.timeGMT();  //get the time part in GMT
				dtFromDateTimeGMT = DateTime.newInstanceGMT(tempDateGMT, tempTimeGMT);		//construct the start datetime in GMT(UTC) to use in SOQL WHERE Clause.
				tempDateGMT = tempToDateLocal.dateGMT();	//get the Date part in GMT
				tempTimeGMT = tempToDateLocal.timeGMT();	//get the time part in GMT
				dtToDateTimeGMT = DateTime.newInstanceGMT(tempDateGMT, tempTimeGMT);	//construct the end datetime in GMT(UTC) to use in SOQL WHERE Clause.
                response.GetCaseInfoResponse = populateByInteractionInfo(); 
                return response;
            }                      
        }  
        else
        {
            response.GetCaseInfoResponse.CaseInfoResponseList=getErrorLogInstance(null,sCaseInfoMandatoryInfoError,false);
            return response;
        }
    }
    /*
    * populateCaseInfoCase
    * <p>
    * PopulatesCase Info based on CaseNumbers passed in input request.
    *
    * @param     NA
    * @return    CaseServiceRestGetResponse_DTO_HUM_V2                    
    */ 
    @TestVisible
    private static CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO populateCaseInfoCase()
    {         
       try
        { 
           if(lstReqCaseNumbers!=null && !lstReqCaseNumbers.isEmpty())
           {
                // To limit the input request only to 5
                if(lstReqCaseNumbers.size()>GLOBAL_CONSTANT_HUM.MAXLIMIT)
                {
                    for(Integer i=0;i<GLOBAL_CONSTANT_HUM.MAXLIMIT;i++)
                    {
                        setCaseNumber.add(lstReqCaseNumbers[i]);
                    }
                }
                else
                {
                    setCaseNumber.addAll(lstReqCaseNumbers);
                }                              
                sWhereClause=' CaseNumber in :setCaseNumber';
                sWhereClause+=sOrderBy;
                if(caseInfoRequestDTO.GetCaseInfo.InclusionCriteria!= null  && (!caseInfoRequestDTO.GetCaseInfo.InclusionCriteria.isEmpty()) && caseInfoRequestDTO.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_CASEDETAILS_INCLUSION))
                {
                    sCaseInfoQuery+=sWhereClause;
                    lstCaseRec=Database.Query(sCaseInfoQuery);       
                }
                else
                {
                    sCaseSummaryQuery+=sWhereClause;
                    lstCaseRec=Database.Query(sCaseSummaryQuery);
                }
                if(lstCaseRec!=null && !lstCaseRec.isEmpty())
                {
                    sCount=string.valueOf(lstCaseRec.size());
                    for(Case c:lstCaseRec)
                    {   
                        setCaseId.add(c.ID);
                    }
                    return getCaseResponse(lstCaseRec,setCaseId,caseInfoRequestDTO);
                }
                else
                {
                    CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO caseInforesponseDTO=new CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO();
                    caseInforesponseDTO.CaseCount = '0';
                    caseInforesponseDTO.CaseInfoResponseList=getErrorLogInstance(null,sCaseInfoCaseIDError,false);
                    return caseInforesponseDTO;
                }
            }
            else
            {                 
                 CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO caseInforesponseDTO=new CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO();
                 caseInforesponseDTO.CaseInfoResponseList=getErrorLogInstance(null,sCaseInfoMandatoryInfoError,false);
                 return caseInforesponseDTO;
            }
        }
        catch(Exception ex)
        {
            //Process the errors onto the error log object            
            HUMExceptionHelper.logErrors(ex,'GetCaseInfoRest_D_HUM_V2','populateCaseInfoCase');
            CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO caseInforesponseDTO=new CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO();
            caseInforesponseDTO.CaseInfoResponseList=getErrorLogInstance(null,ex.getMessage(),false);
            return caseInforesponseDTO;        
         }
    }
    
    /**
    * getErrorLogInstance
    * <p>
    * This method will be used return the response instance with error related fields.This method is public as it will be called from other classes
    *
    * @param    string sErrorCode
    * @param    string sErrorMessage
    * @param    Boolean sSuccess
    * @return   list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO>                 
    */
    public static list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO> getErrorLogInstance(string sErrorCode,string sErrorMessage,Boolean sSuccess )
    {
        CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO CaseInfoResponseDTO = new CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO();
        CaseServiceRestGetResponse_DTO_HUM_V2.CaseSummaryDTO CaseSummary =new CaseServiceRestGetResponse_DTO_HUM_V2.CaseSummaryDTO();
        CaseSummary.ErrorMessage =sErrorMessage;
        CaseInfoResponseDTO.CaseSummary=CaseSummary;
        return new list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO>{CaseInfoResponseDTO};
    }   
    
    /**
    * getCaseRecord
    * <p>
    * This method will be used to get all the case & related records based on response inclusion criteria of request.
    *
    * @param     set<string> setCaseId 
    * @param     list<case>  lstCaserecs
    * @param     CaseServiceRestGetRequest_DTO_HUM_V2 caseInfoRequestDTOResult
    * @return    CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO                 
    */  
    @TestVisible
    private static CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO getCaseResponse(list<case> lstCaserecs,set<string> setCaseId, CaseServiceRestGetRequest_DTO_HUM_V2 caseInfoRequestDTOResult){
       
        map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO>> mapAttachmentDTO  = new map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO>>();
        map<ID,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>> mapAttachmentItemDTO = new map<ID,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>>();
        map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseCommentDTO>> mapCaseCommentDTO = new map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseCommentDTO>>();
        map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO>> mapTaskDTO = new map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO>>();
        map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO>> mapInteractionLogDTO = new map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO>>();
       
        map<string,List<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO>> mapCaseProcessSummaryDTO = new map<string,List<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO>>();        
        if(caseInfoRequestDTO.GetCaseInfo.InclusionCriteria!= null  && (!caseInfoRequestDTO.GetCaseInfo.InclusionCriteria.isEmpty()))
        {
            if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_ATTACHMENT_INCLUSION))     mapAttachmentDTO  = getAttachmentDTO(setCaseId);
            if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_CASECOMMENT_INCLUSION))    mapCaseCommentDTO=getCaseComments(setCaseId);
            if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_ATTACHMENTLOG_INCLUSION))  mapAttachmentItemDTO = getAttachmentLogItemRecord(setCaseId);
            if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_INTERACTION_INCLUSION))    mapInteractionLogDTO = getInteractionLogDTO(setCaseId) ;
            if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_TASKS_INCLUSION))          mapTaskDTO = getTaskDTO(setCaseId);            
            if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_PROCESSSUMMARY_INCLUSION)) mapCaseProcessSummaryDTO = getProcessSummary(setCaseId,caseInfoRequestDTOResult);                       
        }        
        return getCaseRecordQuery(lstCaserecs,mapCaseCommentDTO, mapAttachmentDTO, mapAttachmentItemDTO,mapTaskDTO,mapInteractionLogDTO,caseInfoRequestDTOResult,mapCaseProcessSummaryDTO);
    }
    
    
     /**
    * getAttachmentDTO
    * <p>
    * This method will be used to get all the case & related case attachments
    *
    * @param   set<string> setCaseId
    * @return  map<string,ListAttachmentDTO<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO>>                  
    */
    @TestVisible
    private static map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO>> getAttachmentDTO(set<string> setCaseId)
    {
        map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO>> mapAttachmentDTO  = new map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO>>();
        
        try
        {
            list<Attachment__c> lstAttachment = [select ID,Related_To_Case__c,CreatedDate,name,Attachment_URL_Text__c,DocId__c,Version_Id__c,Created_By_Queue__c, 
                     							Document_Source__c,AlternateDocId__c from Attachment__c where Related_To_Case__c IN :setCaseId];
            for(Attachment__c a:lstAttachment)
            {
                CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO  attachmentDTO  = new CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO ();
                attachmentDTO.FileName = a.Name;
                attachmentDTO.AttachmentURL = a.Attachment_URL_Text__c;
                attachmentDTO.VersionNumber = a.Version_Id__c;
                attachmentDTO.DocId = a.DocId__c;
				attachmentDTO.DocumentSource = a.Document_Source__c;
                attachmentDTO.AlternateDocId = a.AlternateDocId__c;
                if(mapAttachmentDTO.containsKey(a.Related_To_Case__c))
                {
                    mapAttachmentDTO.get(a.Related_To_Case__c).add(attachmentDTO);                    
                }
                else
                {
                    mapAttachmentDTO.put(a.Related_To_Case__c,new List<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO>{attachmentDTO});
                }
            }
        }
        catch(Exception ex)
        {
            throw ex;
        }
        return mapAttachmentDTO;
    }
    
    /**
    * getAttachmentLogItemRecord
    * <p>
    * This method will be used to get all the attachmentItem records.
    *
    * @param    set<string> setCaseId
    * @return   map<ID,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>>                  
    */
    @TestVisible
    private static map<ID,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>> getAttachmentLogItemRecord(set<string> setCaseId)
    {   
        map<ID,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>> mapAttachmentItemDTO = new map<ID,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>>();
        try
        {
            map<string, list<Attachment_Log__c>> mapAttachmentLogsByCase = new map<string, list<Attachment_Log__c>>();
            list<Attachment_Log__c> lstAttachmentLogs = new list<Attachment_Log__c>();
            if(setCaseId!=null && !setCaseId.isEmpty())
            {
                lstAttachmentLogs = [SELECT ID,CreatedDate,Created_By__c,Case__c,Attachment_Description__c,Attachment_Key__c, Source_System__c,
                                Attachment_Type__c, (select name,value__c from Attachment_Log_Items__r) FROM Attachment_Log__c WHERE Case__c IN :setCaseId];
            }
            
            if(lstAttachmentLogs!=null && !lstAttachmentLogs.isEmpty())
            {
                for(Attachment_Log__c attachmentLog : lstAttachmentLogs)
                {
                    if(mapAttachmentLogsByCase!=null && mapAttachmentLogsByCase.containsKey(attachmentLog.case__c))
                        mapAttachmentLogsByCase.get(attachmentLog.case__c).add(attachmentLog);
                    else
                        mapAttachmentLogsByCase.put(attachmentLog.case__c, new list<Attachment_Log__c>{attachmentLog});
                }
            }           
                        
            if(mapAttachmentLogsByCase!=null && !mapAttachmentLogsByCase.isEmpty())
            {
                for(String sCaseID :mapAttachmentLogsByCase.keyset())
                {
                    mapAttachmentItemDTO.put(sCaseID,buildAttachmentLog(mapAttachmentLogsByCase.get(sCaseID)));
                }
            }
        }
        catch(Exception ex)
        {
            throw ex;
        }
        return mapAttachmentItemDTO;
    }    
    /**
    * buildAttachmentLog
    * <p>
    * This method will be used to get all the attachment log records.
    *
    * @param    list<Attachment_Log__c> lstAttachmentLogs
    * @return   list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>                  
    */
    @TestVisible
    private static list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>  buildAttachmentLog (list<Attachment_Log__c> lstAttachmentLogs)
    {
        list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>   lstAttachmentItemDTO  = new list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>();
        for(Attachment_Log__c attachLog : lstAttachmentLogs)
        {
            CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO attachmentItemDTO = new CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO();
            attachmentItemDTO.LoggedBy = attachLog.Created_By__c;
            attachmentItemDTO.LoggedDateTime = string.valueOf(attachLog.CreatedDate); 
            attachmentItemDTO.AttachmentDescription = attachLog.Attachment_Description__c;
            attachmentItemDTO.AttachmentKey = attachLog.Attachment_Key__c;
            attachmentItemDTO.AttachmentType = attachLog.Attachment_Type__c;
            attachmentItemDTO.Source = attachLog.Source_System__c;
            attachmentItemDTO.AttachmentAttribute = buildAttachmentLogItem(attachLog.Attachment_Log_Items__r);            
            lstAttachmentItemDTO.add(attachmentItemDTO);
        }
        return lstAttachmentItemDTO;
    }
    
    /**
    * buildAttachmentLogItem
    * <p>
    * This method will be used to get all the attachment log Item records.
    *
    * @param    list<Attachment_Log_Item__c> lstAttachmentLogItems
    * @return   list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentAttributeDTO>                  
    */
    @TestVisible
    private static list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentAttributeDTO> buildAttachmentLogItem (list<Attachment_Log_Item__c> lstAttachmentLogItems)
    {
        list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentAttributeDTO> lstAttachLogItem = new list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentAttributeDTO>();
        if(lstAttachmentLogItems!=null && !lstAttachmentLogItems.isEmpty())
        {
            for(Attachment_Log_Item__c attachLogItem : lstAttachmentLogItems)
            {
                CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentAttributeDTO attachmentAttribute = new CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentAttributeDTO();
                attachmentAttribute.AttachmentName =attachLogItem.Name;
                attachmentAttribute.AttachmentValue = attachLogItem.Value__c;
                lstAttachLogItem .add(attachmentAttribute);
            }
        }       
        return lstAttachLogItem;
    }
    
    /**
    * getTaskDTOAccountId
    * <p>
    * This method will be used to get all the task records.
    *
    * @param    set<string> setCaseId
    * @return   map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO>>                  
    */
    @TestVisible
    private static map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO>> getTaskDTO(set<string> setCaseId)
    {
        map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO>> mapTaskDTO = new map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO>>();
        
        try
        {
            List<Task> lstTask = [select ID,Task_Number__c,Age_Since_Created__c,OwnerID,Owner.Name,Owner_Queue__c,CreatedByID,
                    CreatedBy.Name,Created_By_Queue__c,WhatId,LastModifiedByID,LastModifiedBy.Name,LastModifiedby_Queue__c, Last_Modified_Age__c,
                    subject,ActivityDate, Status,Priority,Description from task  where WhatId  in :setCaseId];
            for(Task t:lstTask)
            {            
                CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO  taskDTO  = new CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO();
                taskDTO.Subject = t.Subject ;
                taskDTO.DueDate = string.valueOf(t.ActivityDate);
                taskDTO.Status = t.Status;
                taskDTO.Priority = t.Priority;
                taskDTO.Description = t.Description;
                taskDTO.LastModifiedByAge = t.Last_Modified_Age__c != null ? string.valueOf(Integer.valueOf(t.Last_Modified_Age__c)) : null;
                taskDTO.TaskID = t.Task_Number__c ;
                taskDTO.AgeSinceCreated = t.Age_Since_Created__c != null ? string.valueOf(Integer.valueOf(t.Age_Since_Created__c)) : null;
                taskDTO.TaskOwner = t.Owner.Name;
                taskDTO.OwnerQueue = t.Owner_Queue__c;
                taskDTO.CreatedBy = t.CreatedBy.Name;
                taskDTO.CreatedByQueue = t.Created_By_Queue__c;
                taskDTO.lastModifiedBy = t.LastModifiedBy.Name;
                taskDTO.lastModifiedByQueue = t.LastModifiedby_Queue__c;        
                if(!mapTaskDTO.containsKey(t.WhatId))
                {
                    mapTaskDTO.put(t.WhatId,new list<CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO>{taskDTO});
                }
                else
                {
                    mapTaskDTO.get(t.WhatId).add(taskDTO);
                }
            }
        }
        catch(Exception ex)
        {
            throw ex;
        }
        return mapTaskDTO;
    }
    /**
    * getInteractionLogDTO
    * <p>
    * This method will be used to get all the interaction records.
    * param     set<string> setCaseId
    * @return   map<string,CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO>                  
    */
    @TestVisible
    private static map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO>> getInteractionLogDTO(set<string> setCaseId)
    {
        map<string,list<string>> mapCaseInteractions=new map<string,list<string>>();
        list<Case_Interaction__c> lstCaseInteractions = [SELECT id,Case__c,Interaction__c from Case_Interaction__c where Case__c IN:setCaseId];
        
        for(Case_Interaction__c ci:lstCaseInteractions)
        {
            if(!mapCaseInteractions.containsKey(ci.Interaction__c))
            {
                mapCaseInteractions.put(ci.Interaction__c,new list<string>{ci.Case__c});
            }
            else
            {
                mapCaseInteractions.get(ci.Interaction__c).add(ci.Case__c);
            }
        }
        map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO>> mapInteractionLogDTO = new map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO>>();
        try
        { 
            list<Interaction__c> lstInteraction = [SELECT Id,Name, Interacting_With__r.Name, Interacting_With__r.LastName,Interacting_With__r.FirstName,Interacting_With__r.Birthdate__c,Interacting_With__r.Enterprise_ID__c, 
            Interacting_With_type__c, Interaction_Origin__c, Caller_Name__c, (Select Interacting_About__c,Interacting_About_Type__c,Interacting_About__r.Name, Interacting_About__r.FirstName,
            Interacting_About__r.LastName, Interacting_About__r.Birthdate__c,Interacting_About__r.Enterprise_ID__c from Interaction_Members__r) FROM Interaction__c where ID in :mapCaseInteractions.keySet()]; 
            for(Interaction__c intRec:lstInteraction)
            {
                 CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO interactionLogDTO = new CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO();
                 interactionLogDTO.CallerName = intRec.Caller_Name__c;
                 interactionLogDTO.InteractingWithType = intRec.Interacting_With_type__c;
                 interactionLogDTO.InteractionOrigin= intRec.Interaction_Origin__c;
                 interactionLogDTO.InteractionId=intRec.Name;
                 interactionLogDTO.InteractingWithName=intRec.Interacting_With__r.Name;
                 interactionLogDTO.InteractingWithFirstName=intRec.Interacting_With__r.FirstName;
                 interactionLogDTO.InteractingWithLastName=intRec.Interacting_With__r.LastName;
                 interactionLogDTO.InteractingWithEnterpriseId=intRec.Interacting_With__r.Enterprise_ID__c;
                 interactionLogDTO.InteractingWithDOB=intRec.Interacting_With__r.Birthdate__c;
                 list<CaseServiceRestGetResponse_DTO_HUM_V2.InteractionMemberDTO> lstIntMember=new list<CaseServiceRestGetResponse_DTO_HUM_V2.InteractionMemberDTO>();
                 for(Interaction_Member__c member:intRec.Interaction_members__r)
                 {
                    CaseServiceRestGetResponse_DTO_HUM_V2.InteractionMemberDTO intmember=new CaseServiceRestGetResponse_DTO_HUM_V2.InteractionMemberDTO();
                    intmember.InteractingAboutType=member.Interacting_About_Type__c;
                    intmember.InteractingAboutName=member.Interacting_About__r.Name;
                    intmember.InteractingAboutFirstName=member.Interacting_About__r.FirstName;
                    intmember.InteractingAboutLastName=member.Interacting_About__r.LastName;
                    intmember.InteractingAboutEnterpriseId=member.Interacting_About__r.Enterprise_ID__c;
                    intmember.InteractingAboutDOB=member.Interacting_About__r.Birthdate__c;
                    lstIntMember.add(intmember);
                 }
                 interactionLogDTO.InteractionMember=lstIntMember;
                 if(mapCaseInteractions.containsKey(intRec.Id)){
                    for(string caseId: mapCaseInteractions.get(intRec.Id))
                    {
                        if(mapInteractionLogDTO.containsKey(caseId))
                        {
                            mapInteractionLogDTO.get(caseId).add(interactionLogDTO);
                        }
                        else
                        {
                            mapInteractionLogDTO.put(caseId,new list< CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO>{interactionLogDTO});
                        }
                    }
                     
                 }
            } 
        }
        catch(Exception ex)
        {
            throw ex;
        }      
        return mapInteractionLogDTO;
    }
    
     /**
    * getCaseRecordQuery
    * <p>
    * This method will be used to get all fields of case & related records.
    *
    * @param     list<Case> lstCaserecs
    * @param     map<ID,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO>>     mapAttachmentDTO
    * @param     map<ID,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>>  mapAttachmentItemDTO
    * @param     map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO>>mapInteractionLogDTO
    * @param     map<ID,list<CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO>>           mapTaskDTO
    * @param     CaseServiceRestGetRequest_DTO_HUM_V2                                  caseInfoRequestDTOResult
    * @return    CaseServiceRestGetResponse_DTO_HUM_V2                 
    */  
    @TestVisible
    private static CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO getCaseRecordQuery(list<Case> lstCaserecs, map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseCommentDTO>> mapCaseCommentDTO,map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO>> mapAttachmentDTO, map<ID,list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>> mapAttachmentItemDTO,map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO>> mapTaskDTO,map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO>> mapInteractionLogDTO,CaseServiceRestGetRequest_DTO_HUM_V2 caseInfoRequestDTOResult,map<string,List<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO>> mapCaseProcessSummaryDTO)
    {        
        CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO caseResponseDTO = new CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO();
        list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO> CaseInfoResponseList=new list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO>();
        //Retrieve TaxId values for Unknowns from ConsumerId object
        if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria!= null  && (!caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.isEmpty()) 
        && caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_CASEDETAILS_INCLUSION) && caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNDETAILS_INCLUSION))
        {
            set<string> setUnknownAccount = new set<string>();
            for(Case caseRec :lstCaserecs)
            {
                if(string.isNotBlank(caseRec.Account.RecordType.Name) && caseRec.Account.RecordType.Name.contains(GLOBAL_CONSTANT_HUM.CASESERVICE_Unknown))
                {
                    setUnknownAccount.add(caseRec.AccountID);
                }
                if(string.isNotBlank(caseRec.Interacting_With__r.RecordType.Name) && caseRec.Interacting_With__r.RecordType.Name.contains(GLOBAL_CONSTANT_HUM.CASESERVICE_Unknown))
                {
                    setUnknownAccount.add(caseRec.Interacting_With__c);
                }
            }
            if(setUnknownAccount != null && !setUnknownAccount.isEmpty())
            {
                list<ConsumerID__c> lstConsumerRecs = [SELECT Id, Name, Account__c,ID_Type__c,Consumer_ID__c FROM ConsumerID__c WHERE Account__c IN :setUnknownAccount];
                if(lstConsumerRecs != null && !lstConsumerRecs.isEmpty())
                {
                    for(ConsumerID__c consRec:lstConsumerRecs)
                    {
                        if(mapAcctWithConsumer!= null && !mapAcctWithConsumer.isEmpty() && mapAcctWithConsumer.containsKey(consRec.Account__c))
                        {
                            mapAcctWithConsumer.get(consRec.Account__c).add(consRec);
                        }
                        else
                        {
                            mapAcctWithConsumer.put(consRec.Account__c, new set<ConsumerID__c>{consRec});
                        }
                    }
                }
            } 
        }
        // Checking resposne inclusion criteria of request for CaseDetails to fetch all the details of case
        for(Case caseRec :lstCaserecs)
        {
            CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO CaseInfoResponse=new CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO();
            CaseInfoResponse.CaseSummary= GetCaseInfoRest_D_HUM_V2.getBasicCaseInfoResponseDTO(caseRec);
            if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria!= null  && (!caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.isEmpty()))
            {
                if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_CASEDETAILS_INCLUSION))
                {               
                    CaseInfoResponse.CaseDetail = GetCaseInfoRest_D_HUM_V2.getCaseInfoResponseDTO(caseRec);
                }
                                
                if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_CASECOMMENT_INCLUSION))
                {
                    CaseInfoResponse.CaseComments = new list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseCommentDTO>();
                    if(!mapCaseCommentDTO.IsEmpty() && mapCaseCommentDTO.containsKey(caseRec.ID)) CaseInfoResponse.CaseComments= mapCaseCommentDTO.get(caseRec.ID);
                }
                if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_PROCESSSUMMARY_INCLUSION))
                {                                                            
                    CaseInfoResponse.ProcessSummary= new List<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO>();
                    if(!mapCaseProcessSummaryDTO.IsEmpty() && mapCaseProcessSummaryDTO.containsKey(caseRec.ID)) CaseInfoResponse.ProcessSummary= mapCaseProcessSummaryDTO.get(caseRec.ID);                                        
                }               
                
                if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_ATTACHMENT_INCLUSION))
                {
                    CaseInfoResponse.Attachment  = new list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentDTO>() ;
                    if(!mapAttachmentDTO.IsEmpty() && mapAttachmentDTO.containsKey(caseRec.ID)) CaseInfoResponse.Attachment  = mapAttachmentDTO.get(caseRec.ID) ;
                }
                if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_ATTACHMENTLOG_INCLUSION))
                {
                    CaseInfoResponse.AttachmentLog  = new list<CaseServiceRestGetResponse_DTO_HUM_V2.AttachmentLogDTO>() ;
                    if(!mapAttachmentItemDTO.IsEmpty() && mapAttachmentItemDTO.containsKey(caseRec.ID))  CaseInfoResponse.AttachmentLog = mapAttachmentItemDTO.get(caseRec.ID);
                }
                if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_TASKS_INCLUSION))
                {
                    CaseInfoResponse.Tasks  = new list<CaseServiceRestGetResponse_DTO_HUM_V2.TaskDTO>() ;
                    if(!mapTaskDTO.IsEmpty() && mapTaskDTO.containsKey(caseRec.ID))   CaseInfoResponse.Tasks = mapTaskDTO.get(caseRec.ID);
                }
                if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_INTERACTION_INCLUSION))
                {
                    CaseInfoResponse.Interaction  = new list<CaseServiceRestGetResponse_DTO_HUM_V2.InteractionDTO>() ;
                    if(!mapInteractionLogDTO.IsEmpty() && mapInteractionLogDTO.containsKey(caseRec.ID))  CaseInfoResponse.Interaction = mapInteractionLogDTO.get(caseRec.ID);
                }
            }
            CaseInfoResponseList.add(CaseInfoResponse);
        }
        
        caseResponseDTO.CaseCount=sCount;
        // checking for the cases retrieved for the given request
        if(!CaseInfoResponseList.isEmpty())
        {
            caseResponseDTO.CaseInfoResponseList=CaseInfoResponseList;
        }
        return caseResponseDTO;
      }

    
     /**
    * getCaseInfoResponseDTO
    * <p>
    * This method will be used to build the CaseDetailDTO of response for cases 
    *
    * @param    Case CaseRec
    * @return   CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO                  
    */
    @TestVisible
    private Static CaseServiceRestGetResponse_DTO_HUM_V2.CaseDetailsDTO getCaseInfoResponseDTO(Case CaseRec)
    {
        CaseServiceRestGetResponse_DTO_HUM_V2.CaseDetailsDTO CaseDetails = new CaseServiceRestGetResponse_DTO_HUM_V2.CaseDetailsDTO();
        CaseDetails.AccountName= CaseRec.Account.Name;
        CaseDetails.CaseAge = CaseRec.Case_Age__c == null ?'0' : string.valueOf(CaseRec.Case_Age__c);
        CaseDetails.CaseOwner  = CaseRec.Case_Owner__c;
        if(NULL != CaseRec.CTCI_List__c)   CaseDetails.Classification= CaseRec.CTCI_List__r.Classification__r.Name ;
        CaseDetails.Closed= CaseRec.IsClosed;
        CaseDetails.Complaint= CaseRec.Complaint__c;
        CaseDetails.ComplaintReason = CaseRec.Complaint_Reason__c;
        CaseDetails.ContactId= CaseRec.ContactID;
        CaseDetails.CreatedBy= CaseRec.CreatedBy.Name;
        CaseDetails.DateTimeClosed = (CaseRec.ClosedDate != null ? string.valueOf(CaseRec.ClosedDate) : string.valueOf(datetime.newInstance(0001, 01, 01)));
        CaseDetails.DaysSinceLastModified= CaseRec.Days_Since_Last_Modified__c == null ? '0' : string.valueOf(CaseRec.Days_Since_Last_Modified__c);
        CaseDetails.DueDate= (CaseRec.Due_Date__c != null ? string.valueOf(CaseRec.Due_Date__c) : string.valueOf(date.valueOf(system.label.HUM_MinDate)));
        CaseDetails.Escalated= CaseRec.IsEscalated;
        CaseDetails.FundingType = CaseRec.Funding_Type__c;
        CaseDetails.GAReason= CaseRec.G_A_Reason__c;
        CaseDetails.GARightsGiven= CaseRec.G_A_Rights_Given__c;
        CaseDetails.HumanaEmployee = CaseRec.Interacting_With__r.Home_Office_Account__c;
        CaseDetails.InteractingAboutType= CaseRec.Interacting_About_Type__c;
        CaseDetails.InteractingWithID = CaseRec.Interacting_With__r.Enterprise_ID__c;
        CaseDetails.InteractingWithType = CaseRec.Interacting_With_Type__c;
        CaseDetails.LastModifiedBy= CaseRec.LastModifiedBy.Name;
        CaseDetails.LineofBusiness= CaseRec.Line_of_Business__c;
        CaseDetails.PlanIssueState = CaseRec.Plan_Issue_State__c;
        CaseDetails.PolicyMemberId = CaseRec.Policy_Member__r.Name;
        CaseDetails.PolicyPlatform= CaseRec.Policy_Platform__c;
        CaseDetails.Priority= CaseRec.Priority;
        CaseDetails.Product= CaseRec.Product__c;
        CaseDetails.ProductType= CaseRec.Product_Type__c;
        if(NULL != CaseRec.CTCI_List__c)  CaseDetails.Intent= CaseRec.CTCI_List__r.Intent__r.Name;
        CaseDetails.Reopened= CaseRec.Reopened__c;
        CaseDetails.InteractingAboutID = CaseRec.Account.Enterprise_ID__c;
        CaseDetails.CreatedByQueue = CaseRec.Created_By_Queue__c;
        CaseDetails.LastModifiedByQueue = CaseRec.LastModifiedby_Queue__c;
        CaseDetails.OwnerQueue= CaseRec.Owner_Queue__c;
        CaseDetails.ReOpenCaseAge = string.valueof(CaseRec.Re_Open_Case_Age__c);
        CaseDetails.ReOpenCaseDate = string.valueof(CaseRec.Re_Open_Case_Date__c);
        CaseDetails.BehavioralHealth= CaseRec.Behavioral_Health__c;
        if (NULL != CaseRec.CTCI_List__c) CaseDetails.ClassificationType= CaseRec.CTCI_List__r.Classification_Type__r.Name;
        CaseDetails.Department= CaseRec.Department__c;
        CaseDetails.EmployerGroupID= CaseRec.Employer_Group_Name__c;
        CaseDetails.Exchange= CaseRec.Exchange__c;
        CaseDetails.ExchangeType= CaseRec.Exchange_Type__c;
        CaseDetails.FollowUpDueDate= string.valueOf(CaseRec.Follow_up_Due_Date__c);
        CaseDetails.FollowUpRequired= CaseRec.Follow_Up_Needed__c;
        CaseDetails.GeneralCase= CaseRec.General_Case__c;
        CaseDetails.HODentalCase= CaseRec.HO_Dental_Case__c;
        CaseDetails.HOMedicalCase= CaseRec.HO_Medical_Case__c;
        CaseDetails.InteractingWithName= CaseRec.Interacting_With__r.Name;
        CaseDetails.ClosedWhenCreated= CaseRec.IsClosedOnCreate;
        CaseDetails.LanguagePreference= CaseRec.Language_Preference__c;
        CaseDetails.NPIID= CaseRec.NPI_ID__c;
        CaseDetails.PlanSegmentSize= CaseRec.Plan_Segment_Size__c;
        CaseDetails.PreviousCaseOwnerRole= CaseRec.Previous_Case_Owner_Role__c;
        CaseDetails.SellingMarketNumber= CaseRec.Selling_Market_Number__c;
        CaseDetails.ServiceCenter= CaseRec.Service_Center__c;
        CaseDetails.StateMemResidAdd= CaseRec.State_Mem_Resid_Add__c;
        CaseDetails.TaxID= CaseRec.Tax_ID__c;
        CaseDetails.TexasComplaintLetterRequested= CaseRec.Texas_Complaint_Letter_Requested__c;
        CaseDetails.Topic= CaseRec.Topic__c;
        if(caseInfoRequestDTO.GetCaseInfo.InclusionCriteria!= null  && (!caseInfoRequestDTO.GetCaseInfo.InclusionCriteria.isEmpty()) 
        && caseInfoRequestDTO.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNDETAILS_INCLUSION))
        {
            //Retrieve NPIID, GroupNumber or AgentId from Account object based on values for Unknowns 
            if(string.isNotBlank(CaseRec.Account.RecordType.Name) && CaseRec.Account.RecordType.Name.contains(GLOBAL_CONSTANT_HUM.CASESERVICE_Unknown))
            {
                CaseDetails.InteractingAboutUnknownAccount = new CaseServiceRestGetResponse_DTO_HUM_V2.UnknownAccountDTO();
                CaseDetails.InteractingAboutUnknownAccount.DOB = CaseRec.Account.BirthDate__c;
                CaseDetails.InteractingAboutUnknownAccount.AccountIdentifier = new list<CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO>();
                if(mapAcctWithConsumer != null && !mapAcctWithConsumer.isEmpty() && mapAcctWithConsumer.containsKey(CaseRec.Account.Id))
                {
                    for(ConsumerID__c consumer : mapAcctWithConsumer.get(CaseRec.AccountId))
                    {
                        CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO accountIdentifier = new CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO();
                        accountIdentifier.ID = consumer.Consumer_ID__c;
                        if(String.isNotBlank(consumer.ID_Type__c))
                        {
                        	if(consumer.ID_Type__c.equals(GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_CONSUMERIDTAXID_HUM))	//TAXID --> TaxID
                        		accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.TAXIDTAG_PROVIDERSEARCH_HUM;	
                        	else if(consumer.ID_Type__c.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM))	//Medicaid-Id  --> MedicaidID
                        		accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.UNKNOWN_MEMBER_MEDICAIDID;
                        	else if(consumer.ID_Type__c.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_HUMANAID_HUM))	//HumanaId  --> Member-Id-Base
                        		accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM;
                        	else
                        		accountIdentifier.IDType = consumer.ID_Type__c;	
                        }
                        CaseDetails.InteractingAboutUnknownAccount.AccountIdentifier.add(accountIdentifier);
                    }
                }
                if(CaseRec.Account.RecordType.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNPROVIDERRECORDTYPE_HUM)
                && string.isNotBlank(CaseRec.Account.NPI_ID__c))
                {
                    CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO accountIdentifier = new CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO();
                    accountIdentifier.ID = CaseRec.Account.NPI_ID__c;
                    accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID;
                    CaseDetails.InteractingAboutUnknownAccount.AccountIdentifier.add(accountIdentifier);
                }
                else if(CaseRec.Account.RecordType.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNAGENTRECORDTYPE_HUM)
                && string.isNotBlank(CaseRec.Account.Agent_ID__c))
                {
                    CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO accountIdentifier = new CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO();
                    accountIdentifier.ID = CaseRec.Account.Agent_ID__c;
                    accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID;
                    CaseDetails.InteractingAboutUnknownAccount.AccountIdentifier.add(accountIdentifier);
                }
                else if(CaseRec.Account.RecordType.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE_HUM)
                && string.isNotBlank(CaseRec.Account.Group_Number__c))
                {
                    CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO accountIdentifier = new CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO();
                    accountIdentifier.ID = CaseRec.Account.Group_Number__c;
                    accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM;
                    CaseDetails.InteractingAboutUnknownAccount.AccountIdentifier.add(accountIdentifier);
                }
            }
            //Retrieve NPIID, GroupNumber or AgentId from Account object based on values for Unknowns 
            if(string.isNotBlank(CaseRec.Interacting_With__r.RecordType.Name) && CaseRec.Interacting_With__r.RecordType.Name.contains(GLOBAL_CONSTANT_HUM.CASESERVICE_Unknown))
            {
                CaseDetails.InteractingWithUnknownAccount = new CaseServiceRestGetResponse_DTO_HUM_V2.UnknownAccountDTO();
                CaseDetails.InteractingWithUnknownAccount.DOB = CaseRec.Account.BirthDate__c;
                CaseDetails.InteractingWithUnknownAccount.AccountIdentifier = new list<CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO>();
                if(mapAcctWithConsumer != null && !mapAcctWithConsumer.isEmpty() && mapAcctWithConsumer.containsKey(CaseRec.Interacting_With__r.Id))
                {
                    for(ConsumerID__c consumer : mapAcctWithConsumer.get(CaseRec.Interacting_With__r.Id))
                    {
                        CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO accountIdentifier = new CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO();
                        accountIdentifier.ID = consumer.Consumer_ID__c;                        
                        if(String.isNotBlank(consumer.ID_Type__c))
                        {
                        	if(consumer.ID_Type__c.equals(GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_CONSUMERIDTAXID_HUM))	//TAXID --> TaxID
                        		accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.TAXIDTAG_PROVIDERSEARCH_HUM;	
                        	else if(consumer.ID_Type__c.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM))	//Medicaid-Id  --> MedicaidID
                        		accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.UNKNOWN_MEMBER_MEDICAIDID;
                        	else if(consumer.ID_Type__c.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_HUMANAID_HUM))	//HumanaId  --> Member-Id-Base
                        		accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM;
                        	else
                        		accountIdentifier.IDType = consumer.ID_Type__c;	
                        }                         	                        
                        CaseDetails.InteractingWithUnknownAccount.AccountIdentifier.add(accountIdentifier);
                    }
                }
                if(CaseRec.Interacting_With__r.RecordType.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNPROVIDERRECORDTYPE_HUM)
                && string.isNotBlank(CaseRec.Interacting_With__r.NPI_ID__c))
                {
                    CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO accountIdentifier = new CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO();
                    accountIdentifier.ID = CaseRec.Interacting_With__r.NPI_ID__c;
                    accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID;
                    CaseDetails.InteractingWithUnknownAccount.AccountIdentifier.add(accountIdentifier);
                }
                else if(CaseRec.Interacting_With__r.RecordType.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNAGENTRECORDTYPE_HUM)
                && string.isNotBlank(CaseRec.Interacting_With__r.Agent_ID__c))
                {
                    CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO accountIdentifier = new CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO();
                    accountIdentifier.ID = CaseRec.Interacting_With__r.Agent_ID__c;
                    accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID;
                    CaseDetails.InteractingWithUnknownAccount.AccountIdentifier.add(accountIdentifier);
                }
                else if(CaseRec.Interacting_With__r.RecordType.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE_HUM)
                && string.isNotBlank(CaseRec.Interacting_With__r.Group_Number__c))
                {
                    CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO accountIdentifier = new CaseServiceRestGetResponse_DTO_HUM_V2.AccountIdentifierDTO();
                    accountIdentifier.ID = CaseRec.Interacting_With__r.Group_Number__c;
                    accountIdentifier.IDType = GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM;
                    CaseDetails.InteractingWithUnknownAccount.AccountIdentifier.add(accountIdentifier);
                }
            }
        }
        return CaseDetails;
    }
    /**
    * getBasicCaseInfoResponseDTO
    * <p>
    * This method will be used to build the CaseSummaryDTO of response  for cases 
    * @param    Case CaseRec
    * @return   CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO                  
                */
    @TestVisible
    private Static CaseServiceRestGetResponse_DTO_HUM_V2.CaseSummaryDTO getBasicCaseInfoResponseDTO(Case CaseRec)
    {
        CaseServiceRestGetResponse_DTO_HUM_V2.CaseSummaryDTO CaseSummary = new CaseServiceRestGetResponse_DTO_HUM_V2.CaseSummaryDTO();       
        CaseSummary.CaseId = CaseRec.CaseNumber;
        CaseSummary.CaseOrigin = CaseRec.Origin;               
        CaseSummary.Status= CaseRec.Status;
        CaseSummary.Subtype= CaseRec.Subtype__c;
        CaseSummary.Type= CaseRec.Type;
        CaseSummary.QueueViewName=CaseRec.Work_Queue_View_Name__c;
        CaseSummary.WorkQueueName=CaseRec.Owner_Queue__c;
        CaseSummary.CaseCreated= string.valueOf(CaseRec.CreatedDate);
        return CaseSummary;
    }
     /**
    * getCaseComments
    * <p>
    * To generate Case comments response
    *
    * @param    set<string> setCaseId
    * @return   map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseCommentDTO>>                  
    */  
    @TestVisible
     private static map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseCommentDTO>> getCaseComments(set<string> setCaseId)
    {
        map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseCommentDTO>> mapCaseCommentDTO  = new map<string,list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseCommentDTO>>();
        try
        {
            list<CaseComment> lstCaseComment = [select ID,Createddate,CommentBody,ParentId,CreatedBy.Name,LastModifiedDate,LastModifiedBy.Name from CaseComment where ParentId IN :setCaseId];
            for(CaseComment c :lstCaseComment)
            {
                CaseServiceRestGetResponse_DTO_HUM_V2.CaseCommentDTO caseCommentDTO = new CaseServiceRestGetResponse_DTO_HUM_V2.CaseCommentDTO();
                caseCommentDTO.CommentBody = c.CommentBody;
                caseCommentDTO.CreatedBy = c.CreatedBy.Name;
                caseCommentDTO.CreatedDate = string.valueOf(c.CreatedDate);
                caseCommentDTO.LastModifiedDate = string.valueOf(c.LastModifiedDate);  
                caseCommentDTO.LastModified = c.LastModifiedBy.Name;
                if(mapCaseCommentDTO.containsKey(c.ParentId))
                {
                    mapCaseCommentDTO.get(c.ParentId).add(caseCommentDTO);                    
                }
                else
                {
                    mapCaseCommentDTO.put(c.ParentId,new list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseCommentDTO>{caseCommentDTO});
                }
            }
        }
        catch(Exception ex)
        {
            throw ex;
        }
            
        return mapCaseCommentDTO; 
    }
                      
    /**
    * populateByInteractionInfo
    * <p>
    * Populate Case Info based on the DTO result
    *
    * @param     NA
    * @return    list<CaseServiceRestGetResponse_DTO_HUM_V2.CaseInfoResponseDTO>                 
    */  
    @TestVisible
    private static CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO populateByInteractionInfo()
    {        
        CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO caseInforesponseDTO=new CaseServiceRestGetResponse_DTO_HUM_V2.GetCaseInfoResponseDTO();            
        try
        { 
            list<Case> lstFinalCases = new list<Case>();
            Boolean bSearchUnknown=false;
            list<EntityIdentifierDTO> Interactionaboutvalues=new list<EntityIdentifierDTO>();
            list<EntityIdentifierDTO> Interactionwithvalues=new list<EntityIdentifierDTO>();
            if(caseInfoRequestDTO.GetCaseInfo.InclusionCriteria!= null  && (!caseInfoRequestDTO.GetCaseInfo.InclusionCriteria.isEmpty()) && caseInfoRequestDTO.GetCaseInfo.InclusionCriteria.contains(GLOBAL_CONSTANT_HUM.CASESERVICE_SEARCHUNKNOWN_INCLUSION))
            {
                bSearchUnknown=true;
            }

            // Constructing map based on  Entitytype,IDvalueType and IDvalues from requested Interactingabout/Interactingwith values
            if(caseInfoRequestDTO.GetCaseInfo.InteractingAboutCriteria!= null 
               && !caseInfoRequestDTO.GetCaseInfo.InteractingAboutCriteria.isEmpty())
            {
                if(caseInfoRequestDTO.GetCaseInfo.InteractingAboutCriteria.size()>GLOBAL_CONSTANT_HUM.MAXLIMIT)
                {                    
                    for(Integer i=0;i<GLOBAL_CONSTANT_HUM.MAXLIMIT;i++)
                    {
                        Interactionaboutvalues.add(caseInfoRequestDTO.GetCaseInfo.InteractingAboutCriteria[i]);
                    }                                                           
                }
                else    
                {
                    Interactionaboutvalues.addAll(caseInfoRequestDTO.GetCaseInfo.InteractingAboutCriteria);
                }                                                              
            }
            if(caseInfoRequestDTO.GetCaseInfo.InteractingWithCriteria!= null 
               && !caseInfoRequestDTO.GetCaseInfo.InteractingWithCriteria.isEmpty())
            {
                if(caseInfoRequestDTO.GetCaseInfo.InteractingWithCriteria.size()>GLOBAL_CONSTANT_HUM.MAXLIMIT)
                {                                        
                    for(Integer i=0;i<GLOBAL_CONSTANT_HUM.MAXLIMIT;i++)
                    {
                        Interactionwithvalues.add(caseInfoRequestDTO.GetCaseInfo.InteractingWithCriteria[i]);
                    }                                                                           
                }
                else
                {
					Interactionwithvalues.addAll(caseInfoRequestDTO.GetCaseInfo.InteractingWithCriteria);
                }
            }
                                    
            if((Interactionaboutvalues != null && !Interactionaboutvalues.isEmpty()) || (Interactionwithvalues!= null && !Interactionwithvalues.isEmpty()))
            {                                
                mapInquirecriteria.putAll(EntitySearch_H_HUM.performEntitySearch(Interactionaboutvalues,Interactionwithvalues,bSearchUnknown));
                if(mapInquirecriteria!=null && !mapInquirecriteria.isEmpty())
                {                    
                    set<string> setIntAbtIDs = new set<string>();
                    set<string> setIntWithIDs = new set<string>();
                    string sCaseQuery ='';
                    string sIntAbtClause = ' AccountID IN : setIntAbtIDs';
                    string sIntWithClause = ' Interacting_With__c IN : setIntWithIDs';
                    string sWhereClause = '';
                    if(caseInfoRequestDTO.GetCaseInfo.InclusionCriteria!= null  && (!caseInfoRequestDTO.GetCaseInfo.InclusionCriteria.isEmpty()) && caseInfoRequestDTO.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_CASEDETAILS_INCLUSION))  sCaseQuery = sCaseInfoQuery;                    
                    else    sCaseQuery = sCaseSummaryQuery;                                                            
                    if(mapInquireCriteria.containsKey(GLOBAL_CONSTANT_HUM.INTABOUT))   setIntAbtIDs = mapInquireCriteria.get(GLOBAL_CONSTANT_HUM.INTABOUT);
                    if(mapInquireCriteria.containsKey(GLOBAL_CONSTANT_HUM.INTWITH))    setIntWithIDs = mapInquireCriteria.get(GLOBAL_CONSTANT_HUM.INTWITH);                     
                    if (Interactionaboutvalues != null && !Interactionaboutvalues.isEmpty() && setIntAbtIDs!=null && !setIntAbtIDs.isEmpty() && Interactionwithvalues!= null && !Interactionwithvalues.isEmpty() && setIntWithIDs!=null && !setIntWithIDs.isEmpty())
                    {
                        sWhereClause = sIntAbtClause + ' AND' + sIntWithClause;
                    }
                    else if((Interactionaboutvalues == null || (Interactionaboutvalues != null && Interactionaboutvalues.isEmpty())) && (Interactionwithvalues!= null && !Interactionwithvalues.isEmpty() && setIntWithIDs!=null && !setIntWithIDs.isEmpty()))
                    {
                        sWhereClause = sIntWithClause;
                    }
                    else if((Interactionwithvalues==null || (Interactionwithvalues!= null && Interactionwithvalues.isEmpty())) && (Interactionaboutvalues != null && !Interactionaboutvalues.isEmpty() && setIntAbtIDs!=null && !setIntAbtIDs.isEmpty()))
                    {
                        sWhereClause = sIntAbtClause;
                    }
                    
                    if(String.isNotBlank(sWhereClause))
                    {                           
                        sCaseQuery += sWhereClause;
                        sWhereClause='';
                        sWhereClause = getWhereQuery();
                        if(string.isNotBlank(sWhereClause)) sCaseQuery +=sWhereClause;  
                        sCaseQuery += sOrderBy;
                        list<Case> lstCases = new list<Case>();                        
                        lstCases = Database.query(sCaseQuery);                    
                        if(lstCases!=null && !lstCases.isEmpty())
                        {
                            if(WorkQueueName!=null && !WorkQueueName.isEmpty()) //If Work Queue provided in the input, filter the final list of cases
                            {
                                lstCaseRec.addAll(getMatchingCases(lstCases));
                            }
                            else
                            {
                                lstCaseRec.addAll(lstCases);
                            }
                        }                    
                        if(lstCaseRec!=null && !lstCaseRec.isEmpty())
                        {
                            sCount=string.valueOf(lstCaseRec.size());                        
                            Integer iMaxOffset = iOffset + GLOBAL_CONSTANT_HUM.CASESERVICE_PAGINGLIMIT_INCLUSION;                     
                            if(iMaxOffset > lstCaseRec.size())  iMaxOffset = lstCaseRec.size();
                            for(Integer index=iOffset; index<iMaxOffset; index++ )
                            {
                                lstFinalCases.add(lstCaseRec[index]);
                            }                           
                        }
                    }
                } 
            }
            if(lstFinalCases!=null && !lstFinalCases.isEmpty())
            {
                for(Case caseRec:lstFinalCases)
                {   
                    setCaseId.add(caseRec.ID);
                }
                return getCaseResponse(lstFinalCases,setCaseId,caseInfoRequestDTO);
            }                
            else
            {
                caseInforesponseDTO.CaseCount = '0';
                caseInforesponseDTO.CaseInfoResponseList=getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOINTERACTIONIDERROR_HUM,false);
                return caseInforesponseDTO;
            }                                                
        }
        catch(Exception ex)
        {            
            //Process the errors onto the error log object
            HUMExceptionHelper.logErrors(ex,'GetCaseInfoRest_D_HUM_V2','populateByInteractionInfo');
            caseInforesponseDTO.CaseInfoResponseList=getErrorLogInstance(null,ex.getMessage(),false);
            return caseInforesponseDTO;
        }
    }
        
     /**
    * getWhereQuery
    * 
    * Method is used to form the whereclause for a query to get the case records
    * @param   NA
    * @return  string                  
    */  
    
    public Static string getWhereQuery()
    { 
        list<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO> ownerQueDetails=new list<CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO>();        
        if(caseInfoRequestDTO.GetCaseInfo.OwnerQueueDetails!=NULL && !caseInfoRequestDTO.GetCaseInfo.OwnerQueueDetails.isEmpty())
        {
            if(caseInfoRequestDTO.GetCaseInfo.OwnerQueueDetails.size()>GLOBAL_CONSTANT_HUM.MAXLIMIT)
            {
                for(integer i=0;i<GLOBAL_CONSTANT_HUM.MAXLIMIT;i++)
                {                    
                    ownerQueDetails.add(caseInfoRequestDTO.GetCaseInfo.OwnerQueueDetails[i]);                   
                }
            }
            else
            {
                ownerQueDetails.addAll(caseInfoRequestDTO.GetCaseInfo.OwnerQueueDetails);
            }
            for(CaseServiceRestGetRequest_DTO_HUM_V2.QueueDetailsDTO queuedetail:ownerQueDetails)
            {
               
               if(string.isNotBlank(queuedetail.WorkQueueName))
               {
                   WorkQueueName.add(queuedetail.WorkQueueName);
                   set<String> setQViewNames = new set<String>();               
                   if(queuedetail.QueueViewName!=NULL && !queuedetail.QueueViewName.isEmpty())
                   { 
                        
                        if(queuedetail.QueueViewName.size()>GLOBAL_CONSTANT_HUM.MAXLIMIT)
                        {
                            for(integer i=0;i<GLOBAL_CONSTANT_HUM.MAXLIMIT;i++)
                            {  
                                QueueViewName.add(queuedetail.QueueViewName[i]);
                                setQViewNames.add(queuedetail.QueueViewName[i]);
                            }
                        }
                        else
                        { 
                            QueueViewName.addAll(queuedetail.QueueViewName);
                            setQViewNames.addAll(queuedetail.QueueViewName);
                        }
                   }
                    /* mapQueue will have the WQ, set<QV> entries if both WQ and QV provided in the input
                    *  if only WQ is provided, then mapQueue will have WQ, empty set<String> entry.
                    */
                    if(mapQueue.containsKey(string.valueOf(queuedetail.WorkQueueName)))
                    {    
                        mapQueue.get(string.valueOf(queuedetail.WorkQueueName)).addall(setQViewNames);                            
                    }
                    else
                    {
                        mapQueue.put(queuedetail.WorkQueueName,setQViewNames);
                    }                                                                                                       
                  }                  
               }
               string sWQClause = ' Owner_Queue__c IN: WorkQueueName';
               string sQVClause = ' Work_Queue_View_Name__c IN: QueueViewName';
               string sQueueClause = '';
                                   
                if(WorkQueueName!=null && !WorkQueueName.isEmpty())
                {
                    sQueueClause = ' (' + sWQClause;
                    if(QueueViewName!=null && !QueueViewName.isEmpty())
                    {
                        if(string.isBlank(sQueueClause))    sQueueClause = ' (' + sQVClause;    
                        else    sQueueClause += ' OR' + sQVClause;
                    }
                    if(string.isNotBlank(sQueueClause))
                    {
                        sQueueClause += ' )';
                        sWhereClause += ' AND' + sQueueClause;
                    }
                }
            }
        if(caseInfoRequestDTO.GetCaseInfo.CaseType != null && !caseInfoRequestDTO.GetCaseInfo.CaseType.isEmpty())  
        {
            if(caseInfoRequestDTO.GetCaseInfo.CaseType.size()>GLOBAL_CONSTANT_HUM.MAXLIMIT)
            {
                for(integer i=0;i<GLOBAL_CONSTANT_HUM.MAXLIMIT;i++)
                {  
                    setType.add(caseInfoRequestDTO.GetCaseInfo.CaseType[i]);
                }
            }
            else
            { 
                setType.addAll(caseInfoRequestDTO.GetCaseInfo.CaseType);
            }
        }
        if(caseInfoRequestDTO.GetCaseInfo.CaseSubtype != null && !caseInfoRequestDTO.GetCaseInfo.CaseSubtype.isEmpty())  
        {
            if(caseInfoRequestDTO.GetCaseInfo.CaseSubtype.size()>GLOBAL_CONSTANT_HUM.MAXLIMIT)
            {
                for(integer i=0;i<GLOBAL_CONSTANT_HUM.MAXLIMIT;i++)
                {  
                    setCaseSubType.add(caseInfoRequestDTO.GetCaseInfo.CaseSubtype[i]);
                }
            }
            else
            { 
                setCaseSubType.addAll(caseInfoRequestDTO.GetCaseInfo.CaseSubtype);
            }
        }
        if(setType!=null && !setType.isEmpty())sWhereClause+=' AND Type IN :setType';
        if(setCaseSubType!=null && !setCaseSubType.isEmpty())sWhereClause+=' AND Subtype__c IN : setCaseSubType ';
        if(!lstStatus.isEmpty())
        {
            sWhereClause+=' AND Status IN: lstStatus';
        }
        if (!Test.isRunningTest() && dtFromDateTimeGMT!=null && dtToDateTimeGMT!=null)
        {
            sWhereClause+=' AND CreatedDate >= :dtFromDateTimeGMT AND CreatedDate < :dtToDateTimeGMT';
        }                      
        return sWhereClause;
    }
    
    /*
    * getMatchingCases
    * <p>
    * This method is used to match the cases against Work Queue and Queue view inputs and get the final list of cases for response 
    *  
    * @param    lstCases (list of cases from the query)  
    * @return   list<Case> (list of final cases with matching Work Queue and Queue Views)                  
    */
    @TestVisible
    private static list<Case> getMatchingCases(list<Case> lstCases)
    {
        list<Case> lstMatchingCases = new list<Case>();
        if(mapQueue!=null && !mapQueue.isEmpty())
        {
            for(Case caseRec : lstCases)
            {
                if(mapQueue.containsKey(caseRec.Owner_Queue__c)) //Match if WQ and QV provided in input
                {
                    set<String> setQViews = new set<String>();
                    setQViews = mapQueue.get(caseRec.Owner_Queue__c);    
                    if(setQViews!=null && setQViews.isEmpty())
                    {
                        lstMatchingCases.add(caseRec);
                    }
                    else if(setQViews!=null && setQViews.contains(caseRec.Work_Queue_View_Name__c))
                    {
                        lstMatchingCases.add(caseRec);
                    }                                       
                }               
            }
        }       
        return lstMatchingCases; 
    }
   
    /**
    * getprocessSummary
    * <p>
    * To generate Process Summary response based on inclusion criteria
    *
    * @param   set<string> setCaseId,CaseServiceRestGetRequest_DTO_HUM_V2 caseInfoRequestDTOResult 
    * @return  map<string,List<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO>>  
    */
    @TestVisible
     private static map<string,List<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO>> getProcessSummary(set<string> setCaseId,CaseServiceRestGetRequest_DTO_HUM_V2 caseInfoRequestDTOResult)
     {         
         map<string,List<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO>> mapProcessSummaryDTO  = new map<string,List<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO>>();                           
         
         try
         {    
              List<Template_Submission__c> lstTemplatesubmission = new  List<Template_Submission__c> ();
              map<String, list<Template_Submission__c>> mapTempDataBySubmission = new map<String, list<Template_Submission__c>>();
              
              if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_PROCESSDETAIL_INCLUSION))
              {          
                  lstTemplatesubmission = [Select (Select Template_Field__r.Name,Template_Field__c, Template_Submission__c, Name__c, Value__c From Template_Submission_Data__r), Submission_Owner__r.Object_Owner_ID__c,Template__r.Description__c, Name, LastModifiedDate, LastModifiedBy.Name, Id, CreatedDate, CreatedBy.Name,Version__c From Template_Submission__c where Submission_Owner__r.Object_Owner_ID__c IN :setCaseId];
                  
              }
              else
              {
                  lstTemplatesubmission = [Select Submission_Owner__r.Object_Owner_ID__c,Template__r.Description__c, Name, LastModifiedDate, LastModifiedBy.Name, Id, CreatedDate, CreatedBy.Name,Version__c From Template_Submission__c where Submission_Owner__r.Object_Owner_ID__c IN :setCaseId];                  
              }
             //Retrieve all Templates and its data field and put it in map based on Template Submission   
              if(lstTemplatesubmission != null && !lstTemplatesubmission .isEmpty())
              {
                    
                    for(Template_Submission__c tempData : lstTemplatesubmission )
                    {
                      if(mapTempDataBySubmission!=null && !mapTempDataBySubmission.isEmpty() && mapTempDataBySubmission.containsKey(tempData.Submission_Owner__r.Object_Owner_ID__c))
                      {
                        mapTempDataBySubmission.get(tempData.Submission_Owner__r.Object_Owner_ID__c).add(tempData); 
                      }
                      else
                      {
                        mapTempDataBySubmission.put(tempData.Submission_Owner__r.Object_Owner_ID__c, new List<Template_Submission__c>{tempData});
                      }
                   }
                                         
                  if(mapTempDataBySubmission !=null && !mapTempDataBySubmission.isEmpty())
                  {
                    for(String sCaseID :mapTempDataBySubmission.keyset())
                    {
                        mapProcessSummaryDTO.put(sCaseID,buildTemplateDataProcessSummary(mapTempDataBySubmission.get(sCaseID),caseInfoRequestDTOResult));
                    }                    
                  }
             
            }
          
            else
            {
               map<String, list<Case_Action_Association__c>> mapCaseActionAssociation = new map<String, list<Case_Action_Association__c>>();
               list<Case_Action_Association__c> lstCaseActionAssociation = new  list<Case_Action_Association__c> ();
               if(caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_PROCESSDETAIL_INCLUSION))
               {                                              
                    lstCaseActionAssociation = [SELECT LastModifiedDate, LastModifiedBy.Name, LastModifiedById, CreatedDate, CreatedBy.Name,
                                                         CreatedById, Case__c, Case_Action__r.Name, Case_Action__r.CreatedBy.Name,Case_Action__r.LastModifiedBy.Name,Case_Action__r.LastModifiedDate,Case_Action__r.CreatedDate,Case_Action__c, Action__c,(Select Id, Action_Response__c, Date_Response__c, Grid_Question__c, Question_Values__c, Response__c,Row_Number__c From Tabular_Answer_Responses__r),(Select Process_Question_Response__c, Process_Question__c From Case_Action_Responses__r)
                                                         FROM Case_Action_Association__c WHERE Case__c IN :setCaseId ];                                                          
                                                         
               }
               else
               {
                   lstCaseActionAssociation = [SELECT LastModifiedDate, LastModifiedBy.Name, LastModifiedById, CreatedDate, CreatedBy.Name,
                                                         CreatedById, Case__c, Case_Action__r.Name, Case_Action__r.CreatedBy.Name,Case_Action__r.LastModifiedBy.Name,Case_Action__r.LastModifiedDate,Case_Action__r.CreatedDate,Case_Action__c, Action__c
                                                         FROM Case_Action_Association__c WHERE Case__c IN :setCaseId ];                                                          
               } 
                if(lstCaseActionAssociation != null && !lstCaseActionAssociation.isEmpty())
                {
                    for(Case_Action_Association__c c :lstCaseActionAssociation )
                    {                    
                        if(mapCaseActionAssociation !=null && !mapCaseActionAssociation.isEmpty() && mapCaseActionAssociation.containsKey(c.Case__c))
                          {
                            mapCaseActionAssociation.get(c.Case__c).add(c); 
                          }
                          else
                          {
                            mapCaseActionAssociation.put(c.Case__c, new List<Case_Action_Association__c >{c});
                          }                          
                    }
                  
                  if(mapCaseActionAssociation!=null && !mapCaseActionAssociation.isEmpty())
                  {
                    for(String sCaseID :mapCaseActionAssociation.keyset())
                    {                            
                        mapProcessSummaryDTO.put(sCaseID,buildCaseActionAssociationProcessSummary(mapCaseActionAssociation.get(sCaseID),caseInfoRequestDTOResult));                            
                    }                    
                  }                  
              }
           }
        }
        catch(Exception ex)
        {
            throw ex;
        }       
        return mapProcessSummaryDTO;
     }
     
     /**
    * buildCaseActionAssociationProcessSummary
    * <p>
    * This method will be used to get all the Case Action association records.
    *
    * @param    list<Case_Action_Association__c> lstProcessSummary
    * @return   list<CaseServiceRestGetResponse_DTO_HUM_V2.processSummaryDTO>                  
    */
    @TestVisible
    private static list<CaseServiceRestGetResponse_DTO_HUM_V2.processSummaryDTO>  buildCaseActionAssociationProcessSummary(list<Case_Action_Association__c> lstProcessSummary,CaseServiceRestGetRequest_DTO_HUM_V2 caseInfoRequestDTOResult)
    {        
        list<CaseServiceRestGetResponse_DTO_HUM_V2.processSummaryDTO> lstProcessSummaryDTO  = new list<CaseServiceRestGetResponse_DTO_HUM_V2.processSummaryDTO>();
        map<Id,List<Tabular_Answer_Response__c>> mapTabularResponse = new map<Id,List<Tabular_Answer_Response__c>>();
        map<Id,List<Case_Action_Response__c>> mapCaseActionResponse = new map<Id,List<Case_Action_Response__c>>();
        list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO> lstTabularProcessDetailDTO = new list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO>();
                list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO> lstLinearProcessDetailDTO = new list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO>();
        for(Case_Action_Association__c c: lstProcessSummary)
        {            
            CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO processSummaryDTO = new CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO();
            processSummaryDTO.ProcessDetail = new list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO>();
            processSummaryDTO.ProcessName = c.Case_Action__r.Name;                       
            processSummaryDTO.ProcessCreatedDate = string.valueOf(c.CreatedDate);             
            processSummaryDTO.ProcessCreatedBy = c.CreatedBy.Name;
            processSummaryDTO.ProcessLastModifiedDate = string.valueOf(c.LastModifiedDate);
            processSummaryDTO.ProcessLastModifiedBy = c.LastModifiedBy.Name; 
            if(c.Tabular_Answer_Responses__r != null && caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_PROCESSDETAIL_INCLUSION))
            {                                           
                processSummaryDTO.ProcessDetail.addAll(buildCaseActionTabularResponseProcessDetail(c.Tabular_Answer_Responses__r));                 
            }           
           if(c.Case_Action_Responses__r != null && caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_PROCESSDETAIL_INCLUSION))
           {                                                                           
                processSummaryDTO.ProcessDetail.addAll(buildCaseActionResponseProcessDetail(c.Case_Action_Responses__r));                                                                
           } 
           
           lstProcessSummaryDTO.add(processSummaryDTO);            
        }
        return lstProcessSummaryDTO ;
    }
    
     /**
    * buildCaseActionTabularResponseProcessDetail
    * <p>
    * This method will be used to get all the Tabular answer responses records.
    *
    * @param    list<Tabular_Answer_Response__c> lstTabularAnswerResponse
    * @return   list<CaseServiceRestGetResponse_DTO_HUM_V2.processDetailDTO>                  
    */
    @TestVisible
    private static list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO> buildCaseActionTabularResponseProcessDetail(list<Tabular_Answer_Response__c> lstTabularAnswerResponse)
    {        
        list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO> lstProcessDetail = new list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO>();        
        if(lstTabularAnswerResponse!=null && !lstTabularAnswerResponse.isEmpty())
        {
            for(Tabular_Answer_Response__c TabAnsRes : lstTabularAnswerResponse)
            {
                CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO processDetailDTO = new CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO ();
                processDetailDTO.FieldName= TabAnsRes.Question_Values__c;                
                processDetailDTO.FieldValue = TabAnsRes.Response__c;                
                processDetailDTO.RowNumber= string.valueOf(TabAnsRes.Row_Number__c ); 
                lstProcessDetail.add(processDetailDTO);
            }            
        }      
        return lstProcessDetail;
    }
    
     /**
    * buildCaseActionResponseProcessDetail
    * <p>
    * This method will be used to get all the Case action response records.
    *
    * @param    list<Case_Action_Response__c> lstCaseActionResponse
    * @return   list<CaseServiceRestGetResponse_DTO_HUM_V2.processDetailDTO>                  
    */
    @TestVisible
    private static list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO> buildCaseActionResponseProcessDetail(list<Case_Action_Response__c> lstCaseActionResponse)
    {        
        list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO> lstProcessDetail = new list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO>();        
        if(lstCaseActionResponse !=null && !lstCaseActionResponse.isEmpty())
        {
            for(Case_Action_Response__c caseAcRes : lstCaseActionResponse)
            {
                CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO processDetailDTO = new CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO ();
                processDetailDTO.FieldName= caseAcRes.Process_Question__c;                
                processDetailDTO.FieldValue= caseAcRes.Process_Question_Response__c;
                lstProcessDetail.add(processDetailDTO);
            }
        }      
        return lstProcessDetail;
    }
     
     /**
    * buildTemplateDataProcessSummary
    * <p>
    * This method will be used to get all Template submission records.
    *
    * @param    list<Template_Submission__c> lstProcessSummary
    * @return   list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO>                  
    */
    @TestVisible
    private static list<CaseServiceRestGetResponse_DTO_HUM_V2.processSummaryDTO>  buildTemplateDataProcessSummary(list<Template_Submission__c> lstProcessSummary,CaseServiceRestGetRequest_DTO_HUM_V2 caseInfoRequestDTOResult)
    {
        list<CaseServiceRestGetResponse_DTO_HUM_V2.processSummaryDTO>   lstProcessSummaryDTO  = new list<CaseServiceRestGetResponse_DTO_HUM_V2.processSummaryDTO>();
        for(Template_Submission__c c: lstProcessSummary)
        {            
            CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO processSummaryDTO = new CaseServiceRestGetResponse_DTO_HUM_V2.ProcessSummaryDTO();
            processSummaryDTO.ProcessName = c.Template__r.Description__c;                             
            if(c.Version__c != NULL)
            processSummaryDTO.ProcessVersion = string.valueOf(c.Version__c);             
            processSummaryDTO.ProcessCreatedDate = string.valueOf(c.CreatedDate);                        
            processSummaryDTO.ProcessCreatedBy = c.CreatedBy.Name;
            processSummaryDTO.ProcessLastModifiedDate = string.valueOf(c.LastModifiedDate);
            processSummaryDTO.ProcessLastModifiedBy = c.LastModifiedBy.Name;
            if(c.Template_Submission_Data__r != null && caseInfoRequestDTOResult.GetCaseInfo.InclusionCriteria.Contains(GLOBAL_CONSTANT_HUM.CASESERVICE_PROCESSDETAIL_INCLUSION))
            processSummaryDTO.ProcessDetail =buildTemplateProcessDetail(c.Template_Submission_Data__r);  
            lstProcessSummaryDTO.add(processSummaryDTO); 
        }
        return lstProcessSummaryDTO ;
    }
    
    /**
    * buildTemplateProcessDetail
    * <p>
    * This method will be used to get all the Template submission data records.
    *
    * @param    list<Template_Submission_Data__c> lstTemplateSumbmissionData
    * @return   list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO>                  
    */
    @TestVisible
    private static list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO> buildTemplateProcessDetail(list<Template_Submission_Data__c> lstTemplateSumbmissionData)
    {
        list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO> lstProcessDetail = new list<CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO>();
        if(lstTemplateSumbmissionData !=null && !lstTemplateSumbmissionData.isEmpty())
        {
            for(Template_Submission_Data__c c : lstTemplateSumbmissionData)
            {
                CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO processDetailDTO = new CaseServiceRestGetResponse_DTO_HUM_V2.ProcessDetailDTO ();
                processDetailDTO.FieldName= c.Template_Field__r.Name;                
                processDetailDTO.FieldValue= c.Value__c;
                lstProcessDetail.add(processDetailDTO);
            }
        }      
        return lstProcessDetail;
    }   
}