/******************************************************************************************************************
Apex class Name    : GetCaseInfoRest_D_HUM
Version            : 1.0
Function           : This class is for Data Access Layer for for case related activities
Created On         : March 19 2018
Test Class         : HUMCaseServiceRestTest

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran        215336              03/19/2018          Original Version
*   1.1            Srivinas Damera                                 9/26/2018           REQ - 374689 - Update CaseService V1 and V3 - GetCaseInfo method
*   1.2            Mithra Bharadwaj                                                    SonarQube Changes
------------------------------------------------------------------------------------------------------------------- */
global with sharing class GetCaseInfoRest_D_HUM
{
   Global static Date dtFromDate = null;
   Global static Date dtToDate = null;
   Global static DateTime dTimeToDate = null;
   Global static String sWhereClause = '' ;
   Global static List<Case> lstCaseRec = null;
   Global static List<Id> lstCaseRecId = null;
   Global static List<String> lstCaseId = null;
   Global static List<String> lstInteractionId = null;
   Global static String sCaseInfoMandatoryError = '';
   Global static String sCaseInfoCaseIDError = '';
   Global static String sCaseInfoInteractionIDError = '';
   Global static String sCaseInfoMandatoryInfoError = '';
   Global static CaseServiceRestGetRequest_DTO_HUM caseInfoRequestDTO = null;
   Global static final String RECORDTYPE_MEMBER = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM;
   Global static final String RECORDTYPE_PROVIDER = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM;
   Global static final String RECORDTYPE_GROUP = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM;
   Global static final String ACCOUNT_AGENT_TYPE = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM; 
   Global static final String AGENT_BROKER = GLOBAL_CONSTANT_HUM.CLOSEDCASE_AGENTBROKER2_HUM; 
   Global static Map<String,String> mapRecordType = new Map<String,String>
                                                {
                                                    RECORDTYPE_MEMBER =>RECORDTYPE_MEMBER,
                                                    RECORDTYPE_PROVIDER => RECORDTYPE_PROVIDER,
                                                    RECORDTYPE_GROUP => RECORDTYPE_GROUP,
                                                    ACCOUNT_AGENT_TYPE =>AGENT_BROKER,
                                                    AGENT_BROKER => AGENT_BROKER
                                                };
    Global static List<String> lstValidInquireAboutCriteria = new List<String>();
    Global static List<String> lstValidInquireForCriteria = new List<String>();
    Global static String sCaseNumber = '';
   
    /*
    * getCaseRelatedInfo
    * <p>
    * This method will be used to upsert the  AttachmentLog & list of  AttachmentLogItems records.
    *
    * @param           CaseServiceRestGetRequest_DTO_HUM
    * @return          CaseServiceRestGetResponse_DTO_HUM                  
    */ 
    
    public static CaseServiceRestGetResponse_DTO_HUM getCaseRelatedInfo(CaseServiceRestGetRequest_DTO_HUM request)
    {
        //Initialize the variables
        dtFromDate = null;
        dtToDate = null;
        dTimeToDate = null;
        sWhereClause = null;
        lstCaseRec = new List<Case>();
        lstCaseRecId = new List<Id>();
        lstCaseId = new List<String>{};
        lstInteractionId = new List<String>{};
        sCaseInfoMandatoryError = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOMANDATORYERROR_HUM;
        sCaseInfoCaseIDError = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOCASEIDERROR_HUM;
        sCaseInfoInteractionIDError = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOINTERACTIONIDERROR_HUM;
        sCaseInfoMandatoryInfoError = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_GETCASEINFOMANDATROYINFOERROR_HUM;
        caseInfoRequestDTO=request;
        CaseServiceRestGetResponse_DTO_HUM response = new CaseServiceRestGetResponse_DTO_HUM();
        response.GetCaseInfoResponse = new CaseServiceRestGetResponse_DTO_HUM.GetCaseInfoResponseDTO();
        response.GetCaseInfoResponse.CaseInfoResponseList = new List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO>();
        
        //If the request is empty
        if(caseInfoRequestDTO == null)
        {       
            response.GetCaseInfoResponse.CaseInfoResponseList = getErrorLogInstance(null,sCaseInfoMandatoryInfoError,false);
            return response; 
        }// If there is no request, return a error message.
        //First check : check if the caseID is provIDed.
        if(String.isNotBlank(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.CaseId) && String.isNotEmpty(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.CaseId))
        {
            response.GetCaseInfoResponse.CaseInfoResponseList = populateCaseInfoCase();
            if(response.GetCaseInfoResponse.CaseInfoResponseList!=null) { return response; }
        }
        //Second check : check if the InteractionID is provIDed.
        else if(String.isNotBlank(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InteractionID) && String.isNotEmpty(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InteractionID))
        {
            response.GetCaseInfoResponse.CaseInfoResponseList = populateCaseInfoInteractionID();
            if(response.GetCaseInfoResponse.CaseInfoResponseList!=null) { return response; }
        }
        //Third check : check if the caseID & InteractionID is are not provIDed.
        else
        {
            response.GetCaseInfoResponse.CaseInfoResponseList = populateCriteriaWithNoData();
            if(response.GetCaseInfoResponse.CaseInfoResponseList!=null) { return response; }
        }
        // If caseID is obtained from the above operations get all the relavant resutls.
        if(lstCaseId != null && !lstCaseId.IsEmpty()) 
        { 
            response.GetCaseInfoResponse.CaseInfoResponseList = getInfoResults(lstCaseId,lstInteractionId,caseInfoRequestDTO);
            return response;
            
        }
        else 
        { 
            return null;
        }    
    }
    
    
    /*
    * getInfoResults
    * <p>
    * This method is used to call the each indivisual methods to get the records.
    *
    * @param     List<String>                      lstCaseIDs
    * @param     List<String>                      lstInteractionIDs
    * @param     CaseServiceRestGetRequest_DTO_HUM       caseInfoRequestDTOResult
    * @return    List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO>                      
    */ 
    @TestVisible
    private static List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> getInfoResults(List<String> lstCaseIDs,List<String> lstInteractionIDs,CaseServiceRestGetRequest_DTO_HUM caseInfoRequestDTOResult)
    {
        // If caseID is obtained from the above operations get all the relavant resutls.
        List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> caseInfoResponseDTO = new List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO>();
        if(lstCaseIDs != null && !lstCaseIDs.IsEmpty())
        {
            try
            {
                caseInfoResponseDTO = getCaseRecord(lstCaseIDs,lstInteractionIDs,caseInfoRequestDTOResult);
            }
            catch(Exception ex)
            {
                //Process the errors onto the error log object
                HUMExceptionHelper.logErrors(ex,'GetCaseInfoRest_D_HUM','getInfoResults');
                caseInfoResponseDTO = getErrorLogInstance(null, ex.getmessage(),false);
            }
        }
        
        return caseInfoResponseDTO;
       
    }
    
    /**
    * checkInquiredAboutCases
    * <p>
    * This method is a method exposed in the wsdl to check if the records are present for InquireAboutCriteria.
    *
    * @param     CaseServiceRestGetRequest_DTO_HUM    lstInputInquireAboutCriteria
    * @return    Boolean                    
    */ 
    public static Boolean checkInquiredAboutCases(CaseServiceRestGetRequest_DTO_HUM request)
    {
        List<Case> lstCase = getCases(request);
        return (!lstCase.isEmpty() && lstCase.size() > 0);
    }
    
    /*
    * getCases
    * <p>
    * This method is aused to get list of cases for InquireAboutCriteria.
    *
    * @param     CaseServiceRestGetRequest_DTO_HUM    request
    * @return    List<Case>                    
    */ 
    public static List<Case> getCases(CaseServiceRestGetRequest_DTO_HUM request)
    {
        List<Case> lstCaseRecs = new List<Case>();
        String sQry = '';
        List<String> lstReqInquireAboutCriteria = new List<String>{} ;
        Set<String> setValidInquireAbtCriteria = new Set<String>{};
        List<String> lstValidInquireAbtCriteria = new List<String>{};
        Map<String,String> mapInquireAbtCriteria = new Map<String,String>();
        if(!request.GetCaseInfo.IntAboutCaseAvailabilityCheck.IsEmpty() )
        {   
            for(CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO IDentifierDTO : request.GetCaseInfo.IntAboutCaseAvailabilityCheck)
            {
                if(IDentifierDTO != null)
                {
                    if(String.isNotEmpty(IDentifierDTO.IDValue) && String.isNotEmpty(IDentifierDTO.EntityType) && mapRecordType!=null)
                    {
                        lstReqInquireAboutCriteria.add('\''+String.escapeSingleQuotes(IDentifierDTO.IDValue)+'\'');
                        mapInquireAbtCriteria.put(IDentifierDTO.IDValue, mapRecordType.get(IDentifierDTO.EntityType));
                    }
                }
            }
        }
        if(lstReqInquireAboutCriteria != null && !lstReqInquireAboutCriteria.isEmpty())
        {
            List<Account> lstAccntInqAbt = Database.Query('Select id,Enterprise_ID__c,Recordtype.Developername from Account where Account.Enterprise_ID__c in '+lstReqInquireAboutCriteria + ' LIMIT 1000');
            for(Account accInqAbt : lstAccntInqAbt)
            {                
                if(accInqAbt.Recordtype.Developername == mapInquireAbtCriteria.get(accInqAbt.Enterprise_ID__c)) setValidInquireAbtCriteria.add('\''+String.escapeSingleQuotes(accInqAbt.Enterprise_ID__c)+'\'');
            }
        }
        if(setValidInquireAbtCriteria!= null && !setValidInquireAbtCriteria.isEmpty())
        {
            lstValidInquireAbtCriteria.addAll(setValidInquireAbtCriteria);
            // added conditions to execute different SOQLs with operators based on List size of the personIds to improve performance
            Integer lstPersonIdSizeOne = 0;
            lstPersonIdSizeOne++;
            //if list size equal to 1 then use = operator in query            
            if(lstValidInquireAbtCriteria.size() == lstPersonIdSizeOne)
            { 
                           
                lstCaseRecs = [SELECT CaseNumber FROM Case WHERE Account.Enterprise_ID__c = :lstValidInquireAbtCriteria[0]];   
            }
            //if list size greater than 1 & less than or equal to 25 use IN operator in query
            else if(lstValidInquireAbtCriteria.size()>lstPersonIdSizeOne && lstValidInquireAbtCriteria.size()<=Integer.valueof('25'))
            {                    
                lstCaseRecs = Database.Query('SELECT CaseNumber FROM Case WHERE Account.Enterprise_ID__c IN ' + lstValidInquireAbtCriteria);
            }
            //if list size greater than 25 throw exception 
            else 
            {
                throw new HUMCustomException(System.Label.CASEINFO_LISTSIZEGREATERTHAN25_HUM); 
            }
        }
        return lstCaseRecs ;
    }
    
    /*
    * populateCaseInfoCase
    * <p>
    * PopulateCase Info based on the DTO result.
    *
    * @param     NA
    * @return    CaseServiceRestGetResponse_DTO_HUM                    
    */ 
    @TestVisible
    private static List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> populateCaseInfoCase()
    {
        try
        {
            lstCaseId.add(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.CaseId);
            sCaseNumber = caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.CaseId;
            lstCaseRec = getInteractionID('caseNumber = :sCaseNumber');// Get the relavant case record for the caseID provIDed
            if(lstCaseRec != null && !lstCaseRec.IsEmpty())
            {
                for(Case caseRec : lstCaseRec)
                {
                    lstCaseRecId.add(caseRec.Id);
                    lstInteractionId.add(caseRec.interaction_ID__c);
                }
            }
            else
            {
                return getErrorLogInstance(null,sCaseInfoCaseIDError,false);
            }
        }
        catch(Exception ex)
        {
            //Process the errors onto the error log object
            HUMExceptionHelper.logErrors(ex,'GetCaseInfoRest_D_HUM','populateCaseInfoCase');
            return getErrorLogInstance(null,sCaseInfoCaseIDError,false);
        }
        return null;
    }
    
    /**
    * populateCaseInfoInteractionID
    * <p>
    * PopulateCase Info based on the DTO result
    *
    * @param     NA
    * @return    CaseServiceRestGetResponse_DTO_HUM                    
    */ 
    @TestVisible
    private static List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> populateCaseInfoInteractionID()
    {
        try
        {
            List<Id> lstCsId = new List<Id>();
            for(Case_Interaction__c csInt :  [Select Id , Case__c from Case_Interaction__c where Interaction__r.Name  = :caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InteractionId])
            {
                lstCsId.add(csInt.Case__c);
            }
            String sQueryStr = 'Id IN '+ lstCsId ;
            if(!lstCsId.isEmpty())lstCaseRec = [select ID,interaction_ID__c,caseNumber from case where Id In: lstCsId ORDER BY CreatedDate DESC NULLS LAST LIMIT 5000];
            if(lstCaseRec != null && !lstCaseRec.IsEmpty())
            {
                for(Case caseRec : lstCaseRec)
                {
                    lstCaseId.add(caseRec.caseNumber);
                    lstCaseRecId.add(caseRec.Id);
                    lstInteractionId.add(caseRec.interaction_ID__c);
                }
            }
            else
            {
                return getErrorLogInstance(null,sCaseInfoInteractionIDError,false);
            }
        }
        catch(Exception ex)
        {
            //Process the errors onto the error log object
            HUMExceptionHelper.logErrors(ex,'GetCaseInfoRest_D_HUM','populateCaseInfoInteractionID');
            return getErrorLogInstance(null,sCaseInfoInteractionIDError,false);
        }
        return null;
    }
    
    /**
    * createListInquireCriteria
    * <p>
    * Method is used to create List for Inquire about and Inquire for criteria
    *
    * @param     List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO> request
    * @return    List<String>                   
    */ 
    @TestVisible
    private static List<String> createListInquireCriteria(List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO> request)
    {
        List<String> lstInquireCriteria = new List<String>();
        for(CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO EntityIDentifierDTO : request)
        {
            if(EntityIDentifierDTO != null && (String.isNotEmpty(EntityIDentifierDTO.EntityType) && String.isNotEmpty(EntityIDentifierDTO.IDValue))) lstInquireCriteria.add(String.escapeSingleQuotes(EntityIDentifierDTO.IDValue));
        }
        return lstInquireCriteria;
    }
    
    /**
    * createMapInquireCriteria
    * <p>
    * Method is used to create Map for Inquire about and Inquire for criteria
    *
    * @param     List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO> request
    * @return    Map<String,String>                   
    */ 
    @TestVisible
    private static Map<String,String> createMapInquireCriteria(List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO> request)
    {
        Map<String,String> mapInquireCriteria = new Map<String,String>();
        for(CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO EntityIDentifierDTO : request)
        {
            if(EntityIDentifierDTO != null && (String.isNotEmpty(EntityIDentifierDTO.EntityType) && String.isNotEmpty(EntityIDentifierDTO.IDValue)) && mapRecordType!=null) mapInquireCriteria.put(EntityIDentifierDTO.IDValue,mapRecordType.get(EntityIDentifierDTO.EntityType));
        }
        return mapInquireCriteria;        
    }
    
    /**
    * parseStringToDate
    * <p>
    * Method is used to parse date from String
    *
    * @param     String sDate
    * @return    Date                  
    */ 
    private Static Date parseStringToDate(String sDate)
    {
        Date dDate = null;
        if(sDate.length() == 8 && !Test.isRunningTest())
        {
            //Added Test.isRunningTest() here because Salesforce shows date as MM/DD/YYYY or M/D/YYYY
            //While running test class Date is either in / or without / format. Also initial days of month or starting month of year are stored in single digit format
            //For example 1st January is 1/1 and 29th January is 1/29
            dDate = date.newInstance(Integer.valueof(sDate.substring(4,8)),
            Integer.valueof(sDate.substring(2,4)), Integer.valueof(sDate.substring(0,2)));
        }
        else
        {
            dDate = date.parse(sDate);
        }
        return dDate;        
    }
    
    /**
    * constructInquireCriteriaList
    * <p>
    * Method is used to Construct  Valid Inquire About set and Inquire For Set
    *
    * @param     List<String> lstInquireCriteria
    * @param     Map<String,String> mapInquireCriteria
    * @return    List<String>                 
    */ 
    @TestVisible
    private Static List<String> constructInquireCriteriaList(List<String> lstInquireCriteria , Map<String,String> mapInquireCriteria)
    {
        List<String> listValidInquireCriteria = new List<String>();
        Set<String> setValidInquireCriteria = new Set<String>();
        for(Account acc :  [Select id,Enterprise_ID__c,Recordtype.Developername from Account where Account.Enterprise_ID__c in :lstInquireCriteria])
        {
            if(acc.Recordtype.Developername == mapInquireCriteria.get(acc.Enterprise_ID__c)) setValidInquireCriteria.add(acc.Enterprise_ID__c);
        }
        if(!setValidInquireCriteria.isEmpty()) listValidInquireCriteria.addall(setValidInquireCriteria);
        return listValidInquireCriteria;
    }
    
    /**
    * populateCriteriaWithNoData
    * <p>
    * PopulateCase Info based on the DTO result
    *
    * @param     NA
    * @return    List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO>                 
    */  
    @TestVisible
    private static List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO>  populateCriteriaWithNoData()
    {
        try
        {
            List<String> lstInquireAboutCriteria = new List<String>();
            List<String> lstInquireForCriteria = new List<String>();
            map<String,String> mapInquireAboutCriteria = new map<String,String>();
            map<String,String> mapInquireForCriteria = new map<String,String>();
            if(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InquireAboutCriteria != null  && !caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InquireAboutCriteria.isEmpty())
            {
                lstInquireAboutCriteria.addAll(createListInquireCriteria(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InquireAboutCriteria));
                mapInquireAboutCriteria.putAll(createMapInquireCriteria(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InquireAboutCriteria));
            }
            if(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InquireForCriteria != null  && !caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InquireForCriteria.isEmpty())
            {
                lstInquireForCriteria.addAll(createListInquireCriteria(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InquireForCriteria));
                mapInquireForCriteria.putAll(createMapInquireCriteria(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InquireForCriteria));
            }
            if(lstInquireAboutCriteria  == null || lstInquireAboutCriteria.isEmpty() || String.isBlank(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.StartDate) 
                    || String.isEmpty(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.StartDate) || String.isBlank(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.EndDate) || String.isEmpty(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.EndDate))
            {
                return getErrorLogInstance(null,sCaseInfoMandatoryInfoError,false); // Return error if mandatory fields are not provided
            }
            else
            {
                dtFromDate = parseStringToDate(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.StartDate);
                dtToDate = parseStringToDate(caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.EndDate);
                dTimeToDate = datetime.newInstanceGmt(dtToDate,Time.newInstance(23,59,00,00));
                if(dtFromDate != null && dTimeToDate != null )
                {
                    lstCaseRec = fetchCase(lstInquireAboutCriteria,mapInquireAboutCriteria,lstInquireForCriteria,mapInquireForCriteria,caseInfoRequestDTO.GetCaseInfo.CaseInfoRequest.InqForAboutMatchRule);
                    if(lstCaseRec != null && !lstCaseRec.IsEmpty())
                    {
                        for(Case caseRec : lstCaseRec)
                        {
                            lstCaseRecId.add(caseRec.Id);
                            lstCaseId.add(caseRec.caseNumber);
                            lstInteractionId.add(caseRec.interaction_ID__c);
                        }
                    }
                    else
                    {
                        return getErrorLogInstance(null,sCaseInfoMandatoryError,false) ;
                    }
                }
            }
        }
        catch(Exception ex)
        {
            //Process the errors onto the error log object
            HUMExceptionHelper.logErrors(ex,'GetCaseInfoRest_D_HUM','populateCriteriaWithNoData');
            return getErrorLogInstance(null,ex.getmessage(),false);
        }
        return null;
    }
    
    /**
    * fetchCase
    * <p>
    * This method will be used to create the where clause query and then return the lsit of cases.
    *
    * @param     List<String>          lstInquireAboutCriteria
    * @param     Map<String,String>    mapInquireAboutCriteria
    * @param     List<String>          lstInquireForCriteria
    * @param     Map<String,String>    mapInquireForCriteria
    * @param     String                sMatchRule
    * @return    List<Case>                 
    */  
    public static List<Case> fetchCase(List<String> lstInquireAboutCriteria ,  Map<String,String> mapInquireAboutCriteria, List<String> lstInquireForCriteria,  Map<String,String> mapInquireForCriteria , String sMatchRule)
    {
        List<String> lstValidInquireAboutCriteria = new List<String>();
        List<String> lstValidInquireForCriteria = new List<String>();
        if(!lstInquireAboutCriteria.isEmpty()) lstValidInquireAboutCriteria.addAll(constructInquireCriteriaList(lstInquireAboutCriteria,mapInquireAboutCriteria));
        if(!lstValidInquireAboutCriteria.isEmpty()) sWhereClause = 'Account.Enterprise_ID__c in :lstValidInquireAboutCriteria AND CreatedDate >= :dtFromDate AND (CreatedDate <= :dTimeToDate)';
        if(!lstInquireForCriteria.isEmpty()) lstValidInquireForCriteria.addAll(constructInquireCriteriaList(lstInquireForCriteria,mapInquireForCriteria));
        if(lstValidInquireForCriteria != null && !lstValidInquireForCriteria.isEmpty() && sMatchRule == GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_BOTH_HUM) sWhereClause = sWhereClause+ ' AND Interacting_With__r.Enterprise_ID__c in :lstValidInquireForCriteria' ;
        if(sWhereClause != null && sWhereClause != '') return(getInteractionID(sWhereClause));
        return null;
    }
    
    /**
    * getCaseRecord
    * <p>
    * This method will be used to get all the case & related casecomment records.
    *
    * @param     List<String>                      lstCaseIDs
    * @param     List<String>                      lstInteractionIDs
    * @param     CaseServiceRestGetRequest_DTO_HUM caseInfoRequestDTOResult
    * @return    List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO>                 
    */  
    @TestVisible
    private static List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> getCaseRecord(List<String> lstCaseIDs,List<String> lstInteractionIDs, CaseServiceRestGetRequest_DTO_HUM caseInfoRequestDTOResult){
       
        map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO>> mapAttachmentDTO  = new map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO>>();
        map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>> mapAttachmentItemDTO = new map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>>();
        map<ID,List<CaseServiceRestGetResponse_DTO_HUM.TaskDTO>> mapTaskDTO = new map<ID,List<CaseServiceRestGetResponse_DTO_HUM.TaskDTO>>();
        map<ID,CaseServiceRestGetResponse_DTO_HUM.InteractionDTO> mapInteractionLogDTO = new map<ID,CaseServiceRestGetResponse_DTO_HUM.InteractionDTO>();
        if(caseInfoRequestDTOResult.GetCaseInfo.CaseInfoRequest.AttachmentFlag != null && caseInfoRequestDTOResult.GetCaseInfo.CaseInfoRequest.AttachmentFlag)  mapAttachmentDTO  = getAttachmentDTO(lstCaseIDs) ;
        if(caseInfoRequestDTOResult.GetCaseInfo.CaseInfoRequest.AttachmentLogFlag  != null && caseInfoRequestDTOResult.GetCaseInfo.CaseInfoRequest.AttachmentLogFlag )   mapAttachmentItemDTO = getAttachmentLogItemRecord(lstCaseIDs);
        if(caseInfoRequestDTOResult.GetCaseInfo.CaseInfoRequest.InteractionFlag != null && caseInfoRequestDTOResult.GetCaseInfo.CaseInfoRequest.InteractionFlag)   mapInteractionLogDTO = getInteractionLogDTO(lstInteractionIDs) ;
        if(caseInfoRequestDTOResult.GetCaseInfo.CaseInfoRequest.TaskFlag != null && caseInfoRequestDTOResult.GetCaseInfo.CaseInfoRequest.TaskFlag)    mapTaskDTO = getTaskDTO(lstCaseIDs);
        return getCaseRecordQuery(lstCaseIDs, mapAttachmentDTO, mapAttachmentItemDTO, mapInteractionLogDTO, mapTaskDTO, caseInfoRequestDTOResult);
    }
    
     /**
    * getCaseRecordQuery
    * <p>
    * This method will be used to get all fields of case & related casecomment records.
    *
    * @param     List<String>                                						lstCaseIDs
    * @param     map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO>>     mapAttachmentDTO
    * @param     map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>>  mapAttachmentItemDTO
    * @param     map<ID,CaseServiceRestGetResponse_DTO_HUM.InteractionDTO>          mapInteractionLogDTO
    * @param     map<ID,List<CaseServiceRestGetResponse_DTO_HUM.TaskDTO>>           mapTaskDTO
    * @param     CaseServiceRestGetRequest_DTO_HUM           						caseInfoRequestDTOResult
    * @return    CaseServiceRestGetResponse_DTO_HUM                 
    */  
    @TestVisible
    private static List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> getCaseRecordQuery(List<String> lstCaseIDs, map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO>> mapAttachmentDTO, map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>> mapAttachmentItemDTO, map<ID,CaseServiceRestGetResponse_DTO_HUM.InteractionDTO> mapInteractionLogDTO, map<ID,List<CaseServiceRestGetResponse_DTO_HUM.TaskDTO>> mapTaskDTO, CaseServiceRestGetRequest_DTO_HUM caseInfoRequestDTOResult)
    {
        List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> caseInfoResponseDTO = new List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO>();
                  
         for(Case caseRec :[select ID,(select ID,Createddate,CommentBody,CreatedByID,CreatedBy.Name,LastModifiedDate,LastModifiedByID,LastModifiedBy.Name from CaseComments),AccountID,
                           Account.Name,Interacting_About__c,CreatedDate,CreatedBy.Name,LastModifiedBy.Name,Created_By_Queue__c,LastModifiedby_Queue__c,Re_Open_Case_Age__c,Re_Open_Case_Date__c,Case_Age__c,
                           CaseNumber,Case_Number__c,Origin,OwnerID,Owner.Name,Reason,CTCI_List__r.Classification__c,CTCI_List__r.Classification__r.Name,IsClosed,Complaint__c,Complaint_Reason__c,ContactID,CreatedByID,ClosedDate,Days_Since_Last_Modified__c,
                           Description,Due_Date__c,IsEscalated,Funding_Type__c,G_A_Reason__c,G_A_Rights_Given__c,Interaction_ID__c,Interacting_With__r.Home_Office_Account__c,Interacting_About_Type__c,
                           Interacting_With__c,Interacting_With_Type__c,LastModifiedDate,LastModifiedByID,Line_of_Business__c,ParentID,Plan_Issue_State__c,Policy_Member__r.Name,Policy_Platform__c,Priority,Product__c,
                           Product_Type__c,Owner_Queue__c,CTCI_List__r.Intent__c,CTCI_List__r.Intent__r.Name,Reopened__c,Status,Subject,Subtype__c,Type,Interacting_With__r.Enterprise_ID__c,Account.Enterprise_ID__c,Apply_to_Complaint__c,Behavioral_Health__c,
                           Case_Owner__c,Case_TAT__c,CTCI_List__r.Classification_Type__c, CTCI_List__r.Classification_Type__r.Name,CoKY_Case__c,Department__c,DST_Case__c,Email_Sent__c,Employer_Group_Name__c,Exchange__c,Exchange_Type__c,Follow_up_Due_Date__c,
                           Follow_Up_Needed__c,General_Case__c,HO_Dental_Case__c,HO_Medical_Case__c,Interacting_With_Name__c,IsClosedOnCreate,Language_Preference__c,NPI_ID__c,Plan_Segment_Size__c,
                           Previous_Case_Owner_Role__c,Selling_Market_Number__c,Service_Center__c,State_Mem_Resid_Add__c,Task_TAT__c,Tax_ID__c,Tenant_Id__c,Texas_Complaint_Letter_Requested__c,
                           Topic__c,TX_Complaint_Related_Message__c,Work_Queue_View_Name__c,Interacting_With__r.Name
                           from Case where caseNumber in :lstCaseIDs LIMIT 5000])
         {
            CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO getCaseInResponseDTO = new CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO();
            getCaseInResponseDTO = GetCaseInfoRest_D_HUM.getCaseInfoResponseDTO(caseRec);
            if(caseInfoRequestDTOResult.GetCaseInfo.CaseInfoRequest.CaseCommentFlag != null && caseInfoRequestDTOResult.GetCaseInfo.CaseInfoRequest.CaseCommentFlag)  getCaseInResponseDTO.CaseCommentsList = generateCaseComments(caseRec.CaseComments);
            if(!mapAttachmentDTO.IsEmpty()) getCaseInResponseDTO.AttachmentList  = mapAttachmentDTO.get(CaseRec.ID) ;
            if(!mapAttachmentItemDTO.IsEmpty())  getCaseInResponseDTO.AttachmentLogList = mapAttachmentItemDTO.get(CaseRec.ID);
            if(!mapInteractionLogDTO.IsEmpty())  getCaseInResponseDTO.Interaction = mapInteractionLogDTO.get(CaseRec.Interaction_ID__c);
            if(!mapTaskDTO.IsEmpty()) getCaseInResponseDTO.TaskList = mapTaskDTO.get(CaseRec.ID);
            caseInfoResponseDTO.add(getCaseInResponseDTO);
         }
         return caseInfoResponseDTO;
     }
     
     /**
    * generateCaseComments
    * <p>
    * Case comments response is generated .
    *
    * @param    List<CaseComment> lstCaseComment
    * @return   List<CaseServiceRestGetResponse_DTO_HUM.CaseCommentDTO>                  
    */  
    @TestVisible
    private static List<CaseServiceRestGetResponse_DTO_HUM.CaseCommentDTO> generateCaseComments(List<CaseComment> lstCaseComment)
    {
        List<CaseServiceRestGetResponse_DTO_HUM.CaseCommentDTO> lstCaseCommentDTO  = new List<CaseServiceRestGetResponse_DTO_HUM.CaseCommentDTO>();
        for(CaseComment caseCommentRec :  lstCaseComment)
        {
            CaseServiceRestGetResponse_DTO_HUM.CaseCommentDTO caseCommentDTO = new CaseServiceRestGetResponse_DTO_HUM.CaseCommentDTO();
            caseCommentDTO.CommentBody = CaseCommentRec.CommentBody;
            caseCommentDTO.CreatedByID = CaseCommentRec.CreatedBy.Id;
            caseCommentDTO.CreatedByName = CaseCommentRec.CreatedBy.Name;
            caseCommentDTO.CreatedDate = String.Valueof(CaseCommentRec.CreatedDate);
            caseCommentDTO.LastModifiedDate = String.Valueof(CaseCommentRec.LastModifiedDate);
            caseCommentDTO.LastModifiedByID = String.Valueof(CaseCommentRec.LastModifiedBy.Name);
            lstCaseCommentDTO.add(caseCommentDTO);
        }
        return lstCaseCommentDTO;    
    }
    
    /**
    * initialize
    * <p>
    * This method will be used to build the caseInfoResponseDTO for cases .
    *
    * @param    CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO caseDTO
    * @return   CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO                  
    */  
    private Static CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO initialize(CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO caseDTO)
    {
        caseDTO.CaseAge = '0';
        caseDTO.DaysSinceLastModified = '0';
        return caseDTO;
    }
    
     /**
    * getCaseInfoResponseDTO
    * <p>
    * This method will be used to build the caseInfoResponseDTO for cases 
    *
    * @param    Case caseRec
    * @return   CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO                  
    */
    @TestVisible
    private Static CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO getCaseInfoResponseDTO(Case caseRec)
    {
        CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO caseInfoResponseDTO = new CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO();
        caseInfoResponseDTO.AccountName= CaseRec.Account.Name;
        caseInfoResponseDTO.CaseAge = CaseRec.Case_Age__c == null ? '0' : String.valueOf(CaseRec.Case_Age__c);
        caseInfoResponseDTO.CaseNumber = CaseRec.CaseNumber;
        caseInfoResponseDTO.CaseOrigin = CaseRec.Origin;        
        caseInfoResponseDTO.CaseOwner  = CaseRec.Case_Owner__c;
        caseInfoResponseDTO.CaseReason= CaseRec.Reason;
        if(NULL != CaseRec.CTCI_List__c)   caseInfoResponseDTO.Category= CaseRec.CTCI_List__r.Classification__r.Name ;
        caseInfoResponseDTO.Closed= String.valueOf(CaseRec.IsClosed);
        caseInfoResponseDTO.Complaint= CaseRec.Complaint__c;
        caseInfoResponseDTO.ComplaintReason = CaseRec.Complaint_Reason__c;
        caseInfoResponseDTO.ContactName= CaseRec.ContactID;
        caseInfoResponseDTO.CreatedBy= CaseRec.CreatedBy.Name;        
        caseInfoResponseDTO.DateTimeClosed = ( CaseRec.ClosedDate != null ? String.valueOf(CaseRec.ClosedDate) : String.valueOf(datetime.newInstance(0001, 01, 01)));
        caseInfoResponseDTO.DaysSinceLastModified= CaseRec.Days_Since_Last_Modified__c == null ? '0' : String.valueOf(CaseRec.Days_Since_Last_Modified__c);
        caseInfoResponseDTO.Description= CaseRec.Description;                
        caseInfoResponseDTO.DueDate= (CaseRec.Due_Date__c != null ? String.valueOf(CaseRec.Due_Date__c) : String.valueOf(date.valueOf(system.label.HUM_MinDate)));
        caseInfoResponseDTO.Escalated= String.valueOf(CaseRec.IsEscalated);
        caseInfoResponseDTO.FundingType = CaseRec.Funding_Type__c;
        caseInfoResponseDTO.GAReason= CaseRec.G_A_Reason__c;
        caseInfoResponseDTO.GARightsGiven= CaseRec.G_A_Rights_Given__c;
        caseInfoResponseDTO.HumanaEmployee = String.valueOf(CaseRec.Interacting_With__r.Home_Office_Account__c);
        caseInfoResponseDTO.InteractingAboutType= CaseRec.Interacting_About_Type__c;
        caseInfoResponseDTO.InteractingWith = CaseRec.Interacting_With__r.Enterprise_ID__c;
        caseInfoResponseDTO.InteractingWithType = CaseRec.Interacting_With_Type__c;
        caseInfoResponseDTO.LastModifiedBy= CaseRec.LastModifiedBy.Name;
        caseInfoResponseDTO.LineofBusiness= CaseRec.Line_of_Business__c;
        caseInfoResponseDTO.ParentCase= CaseRec.Line_of_Business__c;
        caseInfoResponseDTO.PlanIssueState = CaseRec.Plan_Issue_State__c;
        caseInfoResponseDTO.PolicyMember = CaseRec.Policy_Member__r.Name;
        caseInfoResponseDTO.PolicyPlatform= CaseRec.Policy_Platform__c;
        caseInfoResponseDTO.Priority= CaseRec.Priority;
        caseInfoResponseDTO.Product= CaseRec.Product__c;
        caseInfoResponseDTO.ProductType= CaseRec.Product_Type__c;
        if(NULL != CaseRec.CTCI_List__c)  caseInfoResponseDTO.Reason= CaseRec.CTCI_List__r.Intent__r.Name;
        caseInfoResponseDTO.Reopened= String.valueOf(CaseRec.Reopened__c);
        caseInfoResponseDTO.Status= CaseRec.Status;
        caseInfoResponseDTO.Subtype= CaseRec.Subtype__c;
        caseInfoResponseDTO.Type= CaseRec.Type;
        caseInfoResponseDTO.InteractingAbout = CaseRec.Account.Enterprise_ID__c;
        caseInfoResponseDTO.DateTimeOpened= String.valueof(CaseRec.CreatedDate);
        caseInfoResponseDTO.CreatedByQueue = CaseRec.Created_By_Queue__c;
        caseInfoResponseDTO.LastModifiedByQueue = CaseRec.LastModifiedby_Queue__c;
        caseInfoResponseDTO.QueueOwner= CaseRec.Owner_Queue__c;
        caseInfoResponseDTO.ReOpenCaseAge = String.valueof(CaseRec.Re_Open_Case_Age__c);
        caseInfoResponseDTO.ReOpenCaseDate = String.valueof(CaseRec.Re_Open_Case_Date__c);
        
        //added new Assignments as per REQ-96619.
        caseInfoResponseDTO.ApplyToComplaint= CaseRec.Apply_to_Complaint__c;
        caseInfoResponseDTO.BehavioralHealth= CaseRec.Behavioral_Health__c;
        caseInfoResponseDTO.CaseTAT= CaseRec.Case_TAT__c;
        if (NULL != CaseRec.CTCI_List__c) caseInfoResponseDTO.ClassificationType= CaseRec.CTCI_List__r.Classification_Type__r.Name;
        caseInfoResponseDTO.CoKYCase= String.valueOf(CaseRec.CoKY_Case__c);
        caseInfoResponseDTO.Department= CaseRec.Department__c;
        caseInfoResponseDTO.DSTCase= String.valueOf(CaseRec.DST_Case__c);
        caseInfoResponseDTO.EmailSent= String.valueOf(CaseRec.Email_Sent__c);
        caseInfoResponseDTO.EmployerGroupName= CaseRec.Employer_Group_Name__c;
        caseInfoResponseDTO.Exchange= CaseRec.Exchange__c;
        caseInfoResponseDTO.ExchangeType= CaseRec.Exchange_Type__c;
        caseInfoResponseDTO.FollowUpDueDate= String.valueOf(CaseRec.Follow_up_Due_Date__c);
        caseInfoResponseDTO.FollowUpRequired= String.valueOf(CaseRec.Follow_Up_Needed__c);
        caseInfoResponseDTO.GeneralCase= String.valueOf(CaseRec.General_Case__c);
        caseInfoResponseDTO.HODentalCase= String.valueOf(CaseRec.HO_Dental_Case__c);
        caseInfoResponseDTO.HOMedicalCase= String.valueOf(CaseRec.HO_Medical_Case__c);
        caseInfoResponseDTO.InteractingWithName= CaseRec.Interacting_With_Name__c;
        caseInfoResponseDTO.InteractionID= CaseRec.Interaction_ID__c;
        caseInfoResponseDTO.ClosedWhenCreated= String.valueOf(CaseRec.IsClosedOnCreate);
        caseInfoResponseDTO.LanguagePreference= CaseRec.Language_Preference__c;
        caseInfoResponseDTO.NPIID= CaseRec.NPI_ID__c;
        caseInfoResponseDTO.PlanSegmentSize= CaseRec.Plan_Segment_Size__c;
        caseInfoResponseDTO.PreviousCaseOwnerRole= CaseRec.Previous_Case_Owner_Role__c;
        caseInfoResponseDTO.SellingMarketNumber= CaseRec.Selling_Market_Number__c;
        caseInfoResponseDTO.ServiceCenter= CaseRec.Service_Center__c;
        caseInfoResponseDTO.StateMemResidAdd= CaseRec.State_Mem_Resid_Add__c;
        caseInfoResponseDTO.TaskTAT= CaseRec.Task_TAT__c;
        caseInfoResponseDTO.TaxID= CaseRec.Tax_ID__c;
        caseInfoResponseDTO.TenantId= CaseRec.Tenant_Id__c;
        caseInfoResponseDTO.TexasComplaintLetterRequested= String.valueOf(CaseRec.Texas_Complaint_Letter_Requested__c);
        caseInfoResponseDTO.Topic= CaseRec.Topic__c;
        caseInfoResponseDTO.TXComplaintRelatedMessage= CaseRec.TX_Complaint_Related_Message__c;
        caseInfoResponseDTO.WorkQueueViewName= CaseRec.Work_Queue_View_Name__c;
        caseInfoResponseDTO.InteractingAboutFullName= CaseRec.Account.Name;
        caseInfoResponseDTO.InteractingWithFullName= CaseRec.Interacting_With__r.Name;                        
        return caseInfoResponseDTO;
    }
    
    /**
    * getAttachmentDTO
    * <p>
    * This method will be used to get all the case & related casecomment records.
    *
    * @param    List<String> lstCaseIDs
    * @return   map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO>>                  
    */
    @TestVisible
    private static map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO>> getAttachmentDTO(List<String> lstCaseIDs)
    {
        map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO>> mapAttachmentDTO  = new map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO>>();
        List<CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO> lstAttachmentDTO = null;
        map<ID,Case> mapCaseToAttachment = new map<ID,Case>([select ID,(select ID,CreatedDate,name,Attachment_URL_Text__c,DocId__c,Version_Id__c,Created_By_Queue__c,Link_To_Task__c,
                Related_To__c,Related_To_Case__c,Related_To_Task__c from Attachements__r limit 5000) from Case where caseNumber in :lstCaseIDs limit 5000]);
        for(ID caseID :mapCaseToAttachment.keyset())
        {
            lstAttachmentDTO  = new List<CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO>();
            for(Attachment__c AttachmentRec : mapCaseToAttachment.get(caseID).Attachements__r)
            {
                CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO  AttachmentDTO  = new CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO ();
                attachmentDTO.FileName = AttachmentRec.Name;
                attachmentDTO.AttachmentURL = AttachmentRec.Attachment_URL_Text__c;
				attachmentDTO.VersionId = AttachmentRec.Version_Id__c;
                attachmentDTO.DocId = AttachmentRec.DocId__c;
				
                lstAttachmentDTO.add(attachmentDTO);
            }
            mapAttachmentDTO.put(caseID,lstAttachmentDTO);
        }
        return mapAttachmentDTO;
    }
    
    /**
    * getAttachmentLogItemRecord
    * <p>
    * This method will be used to get all the attachmentItem records.
    *
    * @param    List<String> lstCaseIDs
    * @return   map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>>                  
    */
    @TestVisible
    private static map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>> getAttachmentLogItemRecord(list<String> lstCaseIDs)
    {   
        map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>> mapAttachmentItemDTO = new map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>>();
        map<ID,Case> mapAttachmentToLogItem = new map<ID,Case>([select ID,(select ID,CreatedDate,Created_By__c,Case__c,Attachment_Description__c,Attachment_Key__c,Standard_Value__c,User_Selected_Value__c,User_Selected_Value_EXT__c,
                Attachment_Type__c from Attachment_Logs__r limit 10000) from Case where caseNumber in :lstCaseIDs limit 5000]);
        map<ID,Attachment_Log__c> mapAttachmentToAttachmentLog = new map<ID,Attachment_Log__c>([select ID,Created_By__c,Case__c,Attachment_Description__c,
                Standard_Value__c,User_Selected_Value__c,User_Selected_Value_EXT__c, Attachment_Key__c,Attachment_Type__c,(select name,value__c from Attachment_Log_Items__r) from Attachment_Log__c where Case__r.caseNumber IN :lstCaseIDs limit 10000]);
        if(!mapAttachmentToLogItem.isEmpty())
        {
            for(ID caseID :mapAttachmentToLogItem.keyset())
            {
                mapAttachmentItemDTO.put(caseID,buildAttachmentLog(mapAttachmentToLogItem.get(caseID).Attachment_Logs__r , mapAttachmentToAttachmentLog));
            }
        }
        return mapAttachmentItemDTO;
    }
    
    /**
    * buildAttachmentLog
    * <p>
    * This method will be used to get all the attachment log  records.
    *
    * @param    List<Attachment_Log__c>      attachmentLog
    * @param    map<ID,Attachment_Log__c>    mapAttachmentToAttachmentLog
    * @return   List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>                  
    */
    @TestVisible
    private static List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>  buildAttachmentLog (List<Attachment_Log__c> attachmentLog , map<ID,Attachment_Log__c> mapAttachmentToAttachmentLog)
    {
        List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>   lstAttachmentItemDTO  = new List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>();
        for(Attachment_Log__c attachLog : attachmentLog)
        {
            CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO attachmentItemDTO = new CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO();
            attachmentItemDTO.LoggedBy = attachLog.Created_By__c;
            attachmentItemDTO.LoggedDateTime = String.Valueof(attachLog.CreatedDate);
            attachmentItemDTO.AttachmentDescription = attachLog.Attachment_Description__c;
            attachmentItemDTO.AttachmentKey = attachLog.Attachment_Key__c;
            attachmentItemDTO.AttachmentType = attachLog.Attachment_Type__c;
            attachmentItemDTO.AttachmentAttribute = buildAttachmentLogItem(mapAttachmentToAttachmentLog.get(attachLog.Id).Attachment_Log_Items__r);
            lstAttachmentItemDTO.add(attachmentItemDTO);
        }
        return lstAttachmentItemDTO;
    }
    
    /**
    * buildAttachmentLogItem
    * <p>
    * This method will be used to get all the attachment log Item records.
    *
    * @param    List<Attachment_Log_Item__c> attachmentLogItem
    * @return   list<CaseServiceRestGetResponse_DTO_HUM.AttachmentAttributeDTO>                  
    */
    @TestVisible
    private static list<CaseServiceRestGetResponse_DTO_HUM.AttachmentAttributeDTO> buildAttachmentLogItem (List<Attachment_Log_Item__c> attachmentLogItem)
    {
        list<CaseServiceRestGetResponse_DTO_HUM.AttachmentAttributeDTO> lstAttachLogItem = new list<CaseServiceRestGetResponse_DTO_HUM.AttachmentAttributeDTO>();
        for(Attachment_Log_Item__c attachLogItem : attachmentLogItem)
        {
            CaseServiceRestGetResponse_DTO_HUM.AttachmentAttributeDTO attachmentAttribute = new CaseServiceRestGetResponse_DTO_HUM.AttachmentAttributeDTO();
            attachmentAttribute.AttachmentName = attachLogItem.Name;
            attachmentAttribute.AttachmentValue = attachLogItem.Value__c;
            lstAttachLogItem.add(attachmentAttribute);
        }
        return lstAttachLogItem;
    }
    
    /**
    * getInteractionLogDTO
    * <p>
    * This method will be used to get all the interaction records.
    *
    * @param    List<String> lstInteractionIDs
    * @return   map<ID,CaseServiceRestGetResponse_DTO_HUM.InteractionDTO>                  
    */
    @TestVisible
    private static map<ID,CaseServiceRestGetResponse_DTO_HUM.InteractionDTO> getInteractionLogDTO(List<String> lstInteractionIDs)
    {
        map<ID,CaseServiceRestGetResponse_DTO_HUM.InteractionDTO> mapInteractionLogDTO = new map<ID,CaseServiceRestGetResponse_DTO_HUM.InteractionDTO>();
        for(Interaction__c intRec : [select ID,Caller_Name__c,Interacting_With_type__c,Interaction_Origin__c from Interaction__c where ID in :lstInteractionIDs LIMIT 5000])
        {
            CaseServiceRestGetResponse_DTO_HUM.InteractionDTO interactionLogDTO = new CaseServiceRestGetResponse_DTO_HUM.InteractionDTO();
            interactionLogDTO.CallerName = intRec.Caller_Name__c;
            interactionLogDTO.InteractingWithType = intRec.Interacting_With_type__c;
            interactionLogDTO.InteractionOrigin= intRec.Interaction_Origin__c;

            mapInteractionLogDTO.put(intRec.ID,interactionLogDTO);
        }
        return mapInteractionLogDTO;
    }
    
    /**
    * getTaskDTOAccountId
    * <p>
    * This method will be used to get all the task records.
    *
    * @param    List<String> lstCaseIDs
    * @return   map<ID,List<CaseServiceRestGetResponse_DTO_HUM.TaskDTO>>                  
    */
    @TestVisible
    private static map<ID,List<CaseServiceRestGetResponse_DTO_HUM.TaskDTO>> getTaskDTO(List<String> lstCaseIDs)
    {
        map<ID,List<CaseServiceRestGetResponse_DTO_HUM.TaskDTO>> mapTaskDTO = new map<ID,List<CaseServiceRestGetResponse_DTO_HUM.TaskDTO>>();
        List<CaseServiceRestGetResponse_DTO_HUM.TaskDTO> lstTaskDTO;
        map<ID,Case> mapCaseToTask = new map<ID,Case>([select ID,(select ID,Task_Number__c,Age_Since_Created__c,OwnerID,Owner.Name,Owner_Queue__c,CreatedByID,
                CreatedBy.Name,Created_By_Queue__c,WhatId,LastModifiedByID,LastModifiedBy.Name,LastModifiedby_Queue__c, Last_Modified_Age__c,
                subject,ActivityDate, Status,Priority,Description from tasks LIMIT 5000)from Case where caseNumber in :lstCaseIDs LIMIT 5000]);
        for(ID caseID :mapCaseToTask.keyset())
        {
            lstTaskDTO  = new List<CaseServiceRestGetResponse_DTO_HUM.TaskDTO>();
            for(task taskRec : mapCaseToTask.get(caseID).tasks)
            {
                CaseServiceRestGetResponse_DTO_HUM.TaskDTO  taskDTO  = new CaseServiceRestGetResponse_DTO_HUM.TaskDTO();
                taskDTO.Subject = taskRec.Subject ;
                taskDTO.DueDate = String.valueOf(taskRec.ActivityDate);
                taskDTO.Status = taskRec.Status;
                taskDTO.Priority = taskRec.Priority;
                taskDTO.Description = taskRec.Description;
                taskDTO.LastModifiedByAge = taskRec.Last_Modified_Age__c != null ? String.valueOf(Integer.valueOf(taskRec.Last_Modified_Age__c)) : null;
                taskDTO.TaskNumber = taskRec.Task_Number__c ;
                taskDTO.AgeSinceCreated = taskRec.Age_Since_Created__c != null ? String.valueOf(Integer.valueOf(taskRec.Age_Since_Created__c)) : null;
                taskDTO.Comments = taskRec.Description;
                taskDTO.TaskOwner = taskRec.Owner.Name;
                taskDTO.OwnerQueue = taskRec.Owner_Queue__c;
                taskDTO.CreatedBy = taskRec.CreatedBy.Name;
                taskDTO.CreatedByQueue = taskRec.Created_By_Queue__c;
                taskDTO.lastModifiedBy = taskRec.LastModifiedBy.Name;
                taskDTO.lastModifiedByQueue = taskRec.LastModifiedby_Queue__c;
                lstTaskDTO.add(taskDTO);
            }
            mapTaskDTO.put(caseID,lstTaskDTO);
        }
        return mapTaskDTO;
    }
    
    /**
    * getInteractionID
    * <p>
    * This method will be used to return the case record for the whereClause specified
    *
    * @param    String whereClause
    * @return   List<Case>                 
    */
    private static List<Case> getInteractionID(String whereClause)
    {
        List<Case> lstCaseRecs = database.Query('select ID,interaction_ID__c,caseNumber from case where '+ string.escapeSingleQuotes(whereClause) + ' ORDER BY CreatedDate DESC NULLS LAST LIMIT 5000');        
        return lstCaseRecs;
    }
    
    /**
    * getErrorLogInstance
    * <p>
    * This method will be used retuen the response instance with error related fields.This method is public as it will becalled from other classes
    *
    * @param    String sErrorCode
    * @param    String sErrorMessage
    * @param    Boolean sSuccess
    * @return   List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO>                 
    */
    public static List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> getErrorLogInstance(String sErrorCode,String sErrorMessage,Boolean sSuccess )
    {
        CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO CaseInfoResponseDTO = new CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO();
        caseInfoResponseDTO = GetCaseInfoRest_D_HUM.initialize(caseInfoResponseDTO);        
        caseInfoResponseDTO.Success = String.valueOf(sSuccess);
        caseInfoResponseDTO.ErrorMessage =sErrorMessage;
        caseInfoResponseDTO.ErrorCode = sErrorCode;
        caseInfoResponseDTO.Closed  = 'false';
        caseInfoResponseDTO.DateTimeClosed =  String.valueOf(datetime.newInstance(0001, 01, 01));
        caseInfoResponseDTO.DueDate = String.valueOf(date.valueOf(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_DATEVALUE_HUM));
        caseInfoResponseDTO.Escalated = 'false';
        caseInfoResponseDTO.FollowUpRequired = 'false';
        caseInfoResponseDTO.HumanaEmployee = 'false'; 
        caseInfoResponseDTO.Reopened = 'false';
        caseInfoResponseDTO.CaseAge = String.valueOf(GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM);
        return new List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO>{CaseInfoResponseDTO};
    }  
}