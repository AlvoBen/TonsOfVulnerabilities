/***************************************************************************************************************************************************************************
Apex Class Name  : HUMGetCaseInfoService
Version          : 1.0
Created Date     : May 24 2014
Function         : This Webservice is used to send all the case & case related records to the GCP/CCP system.
Test Class       : HUMGetCaseInfoServiceTest
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name        Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Srikant Joshi         17395                   05/24/2014                   Original Version
 * Sagar Tapadia         17532                   08/06/2014                   Removed Try Catch from getInteractionId method as this method is called from other methods.
                                                                              Its Exception needs to be handled in Parent Method.

 * Sagar Tapadia         17532                   08/06/2014                   Add a check for IsRunningTest() when checking for length of Start and End Date.
 * Chaitanya Kumar       17964                   08/20/2014                   Refactoring of getCaseRelatedInfo. Seggregated functionality into individual methods
 * Shruthi Karanth       18141                   09/25/2014                   Updated logic to return cases which do not have closed date populated
 * Shruthi Karanth       18141                   09/25/2014                   Modified methods as per Humana Standards.
 * Vishal Verma          19306                   11/14/2014                   Added a null check on where clause before querying.
 * Vishal Verma          19306                   11/18/2014                   Modified code as per Humana standards.
 * Vishal Verma                                  12/23/2014                   Modified the code to break the function checkInquiredAboutCases, Added getCases method which will be used as generic method
                                                                              HUMCaseService and HUMGetcaseInfoService.
 * ChiranjeeviRao        20179                   12/31/2014                   Modified Query in getCaseRecord() & assignements in getCaseInfoResponseDTO() to expose all fields from Case 
 * Pradeep Veeravali     20241                   01/07/2015                   Removed 'Recommended Process' field from Query in getCaseRecord() & assignement in getCaseInfoResponseDTO(), also added more fields in detokenizeResults() to detokenize
                                                 01/12/2015                   Chuncked getCaseRecord() into getCaseRecordQuery() as per review comment        
 * Chaitanya Kumar       20294                   01/10/2105                   Fixed Defect 160376. Added if condition to check if record type is sent as Agent and then translate value accordingly
 * Vishal Verma          20294                   01/15/2015                   Changed populateCaseInfoInteractionID method to query on the Case interaction Object for getting the associated cases for an Interaction.
 * ChiranjeeviRao        20783                   02/03/2015                   Added if else conditions in getCases() to CheckInquiredAboutCases, execution of SOQL based on PersonIds list size passed to improve performance.  
 * Vishal Verma          21599                   03/17/2015                   Added Decryption for Value__c field for Attachment_Log_Item__c.
 * Pradeep Veeravali     21834                   03/23/2015                   Added NPI_ID__c decryption changes to service
 * Prachi Bhasin         33416                   18/07/2016                   Changes made as part of REQ- 270349
 * Prachi Bhasin         82363                   31/07/2016                   Changes made as part of REQ- 281090 (URL Link Is Not Viewable in CRM)
 * Pradeepkumar Dani     82363                   04/06/2017                   CTCI Data model update REQ - 306330
 * Prudhvi Pamarthi      142096                  11/13/2017                   REQ-328931 Fix to call DP Dpass service for field decrypt
 * Srivinas Damera                               9/26/2018                    REQ - 374689 - Update CaseService V1 and V3 - GetCaseInfo method
 * Mithra Bharadwaj                              02/09/2021                   US#1522825 SonarQube Changes
  ****************************************************************************************************************************************************************************/

global with sharing class HUMGetCaseInfoService
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
   Global static HUMGetCaseInfoRequestHelper caseInfoRequestDTO = null;
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
   
    /**
    * getCaseRelatedInfo
    * <p>
    * This method will be used to upsert the  AttachmentLog & list of  AttachmentLogItems records.
    *
    * @param           HUMGetCaseInfoRequestHelper
    * @return          List<HUMGetCaseInfoResponseHelper.CaseInfoDTO>                  
    */ 
    webService static List<HUMGetCaseInfoResponseHelper> getCaseRelatedInfo(HUMGetCaseInfoRequestHelper caseInfoRequestDTOResult)
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
        caseInfoRequestDTO=caseInfoRequestDTOResult;
        //If the request is empty
        if(caseInfoRequestDTO == null) return getErrorLogInstance(null,sCaseInfoMandatoryInfoError,false); // If there is no request, return a error message.
        //First check : check if the caseID is provIDed.
        if(String.isNotBlank(caseInfoRequestDTO.CaseID) && String.isNotEmpty(caseInfoRequestDTO.CaseID))
        {
            List<HUMGetCaseInfoResponseHelper> getCaseInfoData= populateCaseInfoCase();
            if(getCaseInfoData!=null) { return getCaseInfoData; }
        }
        //Second check : check if the InteractionID is provIDed.
        else if(String.isNotBlank(caseInfoRequestDTO.InteractionID) && String.isNotEmpty(caseInfoRequestDTO.InteractionID))
        {
            List<HUMGetCaseInfoResponseHelper> getCaseInfoInteraction= populateCaseInfoInteractionID();
            if(getCaseInfoInteraction!=null) { return getCaseInfoInteraction; }
        }
        //Third check : check if the caseID & InteractionID is are not provIDed.
        else
        {
            List<HUMGetCaseInfoResponseHelper> getCriteriaInfo= populateCriteriaWithNoData();
            if(getCriteriaInfo!=null) { return getCriteriaInfo; }
        }
        // If caseID is obtained from the above operations get all the relavant resutls.
        if(lstCaseId != null && !lstCaseId.IsEmpty()) 
        { 
            return getInfoResults(lstCaseId,lstInteractionId,caseInfoRequestDTO);
            
        }
        else 
        { 
            return null;
        }
    }

    /**
    * getInfoResults
    * <p>
    * This method is used to call the each indivisual methods to get the records.
    *
    * @param     List<String>                      lstCaseIDs
    * @param     List<String>                      lstInteractionIDs
    * @param     HUMGetCaseInfoRequestHelper       caseInfoRequestDTOResult
    * @return    List<HUMGetCaseInfoResponseHelper.CaseInfoDTO>                      
    */ 
    private static List<HUMGetCaseInfoResponseHelper> getInfoResults(List<String> lstCaseIDs,List<String> lstInteractionIDs,HUMGetCaseInfoRequestHelper caseInfoRequestDTOResult)
    {
        // If caseID is obtained from the above operations get all the relavant resutls.
        List<HUMGetCaseInfoResponseHelper> lstCaseInfoResponseDTO = new List<HUMGetCaseInfoResponseHelper>();
        if(lstCaseIDs != null && !lstCaseIDs.IsEmpty())
        {
            try
            {
                lstCaseInfoResponseDTO = getCaseRecord(lstCaseIDs,lstInteractionIDs,caseInfoRequestDTOResult);
            }
            catch(Exception ex)
            {
                //Process the errors onto the error log object
                HUMExceptionHelper.logErrors(ex,'HUMGetCaseInfoService','getInfoResults');
                lstCaseInfoResponseDTO = getErrorLogInstance(null, ex.getmessage(),false);
            }
        }
        
        return lstCaseInfoResponseDTO;
       
     }
   
    
    /**
    * checkInquiredAboutCases
    * <p>
    * This method is a method exposed in the wsdl to check if the records are present for InquireAboutCriteria.
    *
    * @param     List<HUMEntityIdentifierHelper>    lstInputInquireAboutCriteria
    * @return    Boolean                    
    */ 
    webService static Boolean checkInquiredAboutCases(List<HUMEntityIdentifierHelper> lstInputInquireAboutCriteria)
    {
        List<Case> lstCase = getCases(lstInputInquireAboutCriteria);
        return (!lstCase.isEmpty() && lstCase.size() > 0);
    }
       
    /**
    * getCases
    * <p>
    * This method is aused to get list of cases for InquireAboutCriteria.
    *
    * @param     List<HUMEntityIdentifierHelper>    lstInputInquireAboutCriteria
    * @return    List<Case>                    
    */ 
    public static List<Case> getCases(List<HUMEntityIdentifierHelper> lstInputInquireAboutCriteria)
    {
        List<Case> lstCaseRecs = new List<Case>();
        String sQry = '';
        List<String> lstReqInquireAboutCriteria = new List<String>{} ;
        Set<String> setValidInquireAbtCriteria = new Set<String>{};
        List<String> lstValidInquireAbtCriteria = new List<String>{};
        Map<String,String> mapInquireAbtCriteria = new Map<String,String>();
        if(!lstInputInquireAboutCriteria.IsEmpty() )
        {   
            for(HUMEntityIdentifierHelper EntityIDentifierDTO : lstInputInquireAboutCriteria)
            {
                if(EntityIDentifierDTO != null)
                {
                    if(String.isNotEmpty(EntityIDentifierDTO.IDValue) && String.isNotEmpty(EntityIDentifierDTO.EntityType) && mapRecordType!=null)
                    {
                        lstReqInquireAboutCriteria.add(String.escapeSingleQuotes(EntityIDentifierDTO.IDValue));
                        mapInquireAbtCriteria.put(EntityIDentifierDTO.IDValue, mapRecordType.get(EntityIDentifierDTO.EntityType));
                    }
                }
            }
        }
        if(lstReqInquireAboutCriteria != null && !lstReqInquireAboutCriteria.isEmpty())
        {
            List<Account> lstAccntInqAbt = [Select id,Enterprise_ID__c,Recordtype.Developername from Account where Account.Enterprise_ID__c in :lstReqInquireAboutCriteria LIMIT 1000];          
            for(Account accInqAbt : lstAccntInqAbt)
            {                
                if(accInqAbt.Recordtype.Developername == mapInquireAbtCriteria.get(accInqAbt.Enterprise_ID__c)) setValidInquireAbtCriteria.add(String.escapeSingleQuotes(accInqAbt.Enterprise_ID__c));
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
                lstCaseRecs = [SELECT CaseNumber FROM Case WHERE Account.Enterprise_ID__c IN :lstValidInquireAbtCriteria];
            }
            //if list size greater than 25 throw exception 
            else 
            {
                throw new HUMCustomException(System.Label.CASEINFO_LISTSIZEGREATERTHAN25_HUM); 
            }
        }
        return lstCaseRecs ;
    }
    
    /**
    * populateCaseInfoCase
    * <p>
    * PopulateCase Info based on the DTO result.
    *
    * @param     NA
    * @return    List<HUMGetCaseInfoResponseHelper>                    
    */ 
    private static List<HUMGetCaseInfoResponseHelper> populateCaseInfoCase()
    {
        try
        {
            lstCaseId.add(caseInfoRequestDTO.CaseID);
            lstCaseRec = getInteractionID('caseNumber = \''+String.escapeSingleQuotes(caseInfoRequestDTO.CaseID) +'\'');// Get the relavant case record for the caseID provIDed
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
            HUMExceptionHelper.logErrors(ex,'HUMGetCaseInfoService','populateCaseInfoCase');
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
    * @return    List<HUMGetCaseInfoResponseHelper>                    
    */ 
    private static List<HUMGetCaseInfoResponseHelper>  populateCaseInfoInteractionID()
    {
        try
        {
            List<Id> lstCsId = new List<Id>();
            for(Case_Interaction__c csInt :  [Select Id , Case__c from Case_Interaction__c where Interaction__r.Name  = :caseInfoRequestDTO.InteractionID])
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
            HUMExceptionHelper.logErrors(ex,'HUMGetCaseInfoService','populateCaseInfoInteractionID');
            return getErrorLogInstance(null,sCaseInfoInteractionIDError,false);
        }
        return null;
    }
    
    /**
    * createListInquireCriteria
    * <p>
    * Method is used to create List for Inquire about and Inquire for criteria
    *
    * @param     List<HUMEntityIdentifierHelper> lstHUMEntityIdentifierHelper
    * @return    List<String>                   
    */ 
    private static List<String> createListInquireCriteria(List<HUMEntityIdentifierHelper> lstHUMEntityIdentifierHelper)
    {
        List<String> lstInquireCriteria = new List<String>();
        for(HUMEntityIdentifierHelper EntityIDentifierDTO : lstHUMEntityIdentifierHelper)
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
    * @param     List<HUMEntityIdentifierHelper> lstHUMEntityIdentifierHelper
    * @return    Map<String,String>                   
    */ 
    private static Map<String,String> createMapInquireCriteria(List<HUMEntityIdentifierHelper> lstHUMEntityIdentifierHelper)
    {
        Map<String,String> mapInquireCriteria = new Map<String,String>();
        for(HUMEntityIdentifierHelper EntityIDentifierDTO : lstHUMEntityIdentifierHelper)
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
    private Static List<String> constructInquireCriteriaList(List<String> lstInquireCriteria , Map<String,String> mapInquireCriteria)
    {
        List<String> listValidInquireCriteria = new List<String>();
        Set<String> setValidInquireCriteria = new Set<String>();
        for(Account acc :  [Select id,Enterprise_ID__c,Recordtype.Developername from Account where Account.Enterprise_ID__c in :lstInquireCriteria])
        {
            if(acc.Recordtype.Developername == mapInquireCriteria.get(acc.Enterprise_ID__c)) setValidInquireCriteria.add('\''+String.escapeSingleQuotes(acc.Enterprise_ID__c)+'\'');
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
    * @return    List<HUMGetCaseInfoResponseHelper>                 
    */  
    private static List<HUMGetCaseInfoResponseHelper>  populateCriteriaWithNoData()
    {
        try
        {
            List<String> lstInquireAboutCriteria = new List<String>();
            List<String> lstInquireForCriteria = new List<String>();
            map<String,String> mapInquireAboutCriteria = new map<String,String>();
            map<String,String> mapInquireForCriteria = new map<String,String>();
            if(caseInfoRequestDTO.InquireAboutCriteria != null  && !caseInfoRequestDTO.InquireAboutCriteria.isEmpty())
            {
                lstInquireAboutCriteria.addAll(createListInquireCriteria(caseInfoRequestDTO.InquireAboutCriteria));
                mapInquireAboutCriteria.putAll(createMapInquireCriteria(caseInfoRequestDTO.InquireAboutCriteria));
            }
            if(caseInfoRequestDTO.InquireForCriteria != null  && !caseInfoRequestDTO.InquireForCriteria.isEmpty())
            {
                lstInquireForCriteria.addAll(createListInquireCriteria(caseInfoRequestDTO.InquireForCriteria));
                mapInquireForCriteria.putAll(createMapInquireCriteria(caseInfoRequestDTO.InquireForCriteria));
            }
            if(lstInquireAboutCriteria  == null || lstInquireAboutCriteria.isEmpty() || String.isBlank(caseInfoRequestDTO.StartDate) 
                    || String.isEmpty(caseInfoRequestDTO.StartDate) || String.isBlank(caseInfoRequestDTO.EndDate) || String.isEmpty(caseInfoRequestDTO.EndDate))
            {
                return getErrorLogInstance(null,sCaseInfoMandatoryInfoError,false); // Return error if mandatory fields are not provided
            }
            else
            {
                dtFromDate = parseStringToDate(caseInfoRequestDTO.StartDate);
                dtToDate = parseStringToDate(caseInfoRequestDTO.EndDate);
                dTimeToDate = datetime.newInstanceGmt(dtToDate,Time.newInstance(23,59,00,00));
                if(dtFromDate != null && dTimeToDate != null )
                {
                    lstCaseRec = fetchCase(lstInquireAboutCriteria,mapInquireAboutCriteria,lstInquireForCriteria,mapInquireForCriteria,caseInfoRequestDTO.InqForAboutMatchRule);
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
            HUMExceptionHelper.logErrors(ex,'HUMGetCaseInfoService','populateCriteriaWithNoData');
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
        if(!lstValidInquireAboutCriteria.isEmpty()) sWhereClause = 'Account.Enterprise_ID__c in '+lstValidInquireAboutCriteria+ ' AND CreatedDate >= :dtFromDate AND (CreatedDate <= :dTimeToDate)';
        if(!lstInquireForCriteria.isEmpty()) lstValidInquireForCriteria.addAll(constructInquireCriteriaList(lstInquireForCriteria,mapInquireForCriteria));
        if(lstValidInquireForCriteria != null && !lstValidInquireForCriteria.isEmpty() && sMatchRule == GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_BOTH_HUM) sWhereClause = sWhereClause+ ' AND Interacting_With__r.Enterprise_ID__c in '+lstValidInquireForCriteria ;
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
    * @param     HUMGetCaseInfoRequestHelper       caseInfoRequestDTOResult
    * @return    List<HUMGetCaseInfoResponseHelper>                  
    */  
    private static List<HUMGetCaseInfoResponseHelper> getCaseRecord(List<String> lstCaseIDs,List<String> lstInteractionIDs, HUMGetCaseInfoRequestHelper caseInfoRequestDTOResult){
       
        map<ID,List<HUMAttachmentHelper>> mapAttachmentDTO  = new map<ID,List<HUMAttachmentHelper>>();
        map<ID,List<HUMAttachmentItemHelper>> mapAttachmentItemDTO = new map<ID,List<HUMAttachmentItemHelper>>();
        map<ID,List<HUMTaskHelper>> mapTaskDTO = new map<ID,List<HUMTaskHelper>>();
        map<ID,HUMInteractionLogHelper> mapInteractionLogDTO = new map<ID,HUMInteractionLogHelper>();
        if(caseInfoRequestDTOResult.AttachmentFlag != null && caseInfoRequestDTOResult.AttachmentFlag)  mapAttachmentDTO  = getAttachmentDTO(lstCaseIDs) ;
        if(caseInfoRequestDTOResult.AttachmentLogFlag  != null && caseInfoRequestDTOResult.AttachmentLogFlag )   mapAttachmentItemDTO = getAttachmentLogItemRecord(lstCaseIDs);
        if(caseInfoRequestDTOResult.InteractionFlag != null && caseInfoRequestDTOResult.InteractionFlag)   mapInteractionLogDTO = getInteractionLogDTO(lstInteractionIDs) ;
        if(caseInfoRequestDTOResult.TaskFlag != null && caseInfoRequestDTOResult.TaskFlag)    mapTaskDTO = getTaskDTO(lstCaseIDs);
        return getCaseRecordQuery(lstCaseIDs, mapAttachmentDTO, mapAttachmentItemDTO, mapInteractionLogDTO, mapTaskDTO, caseInfoRequestDTOResult);
    }
    
    /**
    * getCaseRecordQuery
    * <p>
    * This method will be used to get all fields of case & related casecomment records.
    *
    * @param     List<String>                                lstCaseIDs
    * @param     map<ID,List<HUMAttachmentHelper>>           mapAttachmentDTO
    * @param     map<ID,List<HUMAttachmentItemHelper>>       mapAttachmentItemDTO
    * @param     map<ID,HUMInteractionLogHelper>             mapInteractionLogDTO
    * @param     map<ID,List<HUMTaskHelper>>                 mapTaskDTO
    * @param     HUMGetCaseInfoRequestHelper                 caseInfoRequestDTOResult
    * @return    List<HUMGetCaseInfoResponseHelper>                  
    */  
    private static List<HUMGetCaseInfoResponseHelper> getCaseRecordQuery(List<String> lstCaseIDs, map<ID,List<HUMAttachmentHelper>> mapAttachmentDTO, map<ID,List<HUMAttachmentItemHelper>> mapAttachmentItemDTO, map<ID,HUMInteractionLogHelper> mapInteractionLogDTO, map<ID,List<HUMTaskHelper>> mapTaskDTO, HUMGetCaseInfoRequestHelper caseInfoRequestDTOResult)
    {
         HUMGetCaseInfoResponseHelper caseInfoResponseDTO = new HUMGetCaseInfoResponseHelper();
         List<HUMGetCaseInfoResponseHelper> lstcaseInfoResponseDTO = new List<HUMGetCaseInfoResponseHelper>();
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
            caseInfoResponseDTO = HUMGetCaseInfoService.getCaseInfoResponseDTO(caseRec);
            if(caseInfoRequestDTOResult.CaseCommentFlag != null && caseInfoRequestDTOResult.CaseCommentFlag)  caseInfoResponseDTO.caseCommentDTO = generateCaseComments(caseRec.CaseComments);
            if(!mapAttachmentDTO.IsEmpty()) caseInfoResponseDTO.AttachmentDTO  = mapAttachmentDTO.get(CaseRec.ID) ;
            if(!mapAttachmentItemDTO.IsEmpty())  caseInfoResponseDTO.AttachmentlogDTO = mapAttachmentItemDTO.get(CaseRec.ID);
            if(!mapInteractionLogDTO.IsEmpty())  caseInfoResponseDTO.InteractionDTO = mapInteractionLogDTO.get(CaseRec.Interaction_ID__c);
            if(!mapTaskDTO.IsEmpty()) caseInfoResponseDTO.TaskDTO = mapTaskDTO.get(CaseRec.ID);
            lstCaseInfoResponseDTO.add(caseInfoResponseDTO);
         }
         return lstCaseInfoResponseDTO;
     }
     
    /**
    * generateCaseComments
    * <p>
    * Case comments response is generated .
    *
    * @param    List<CaseComment> lstCaseComment
    * @return   List<HUMCaseCommentHelper>                  
    */  
    private static List<HUMCaseCommentHelper> generateCaseComments(List<CaseComment> lstCaseComment)
    {
        List<HUMCaseCommentHelper> lstCaseCommentDTO  = new List<HUMCaseCommentHelper>();
        for(CaseComment caseCommentRec :  lstCaseComment)
        {
            HUMCaseCommentHelper caseCommentDTO = new HUMCaseCommentHelper();
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
    * @param    HUMGetCaseInfoResponseHelper caseDTO
    * @return   HUMGetCaseInfoResponseHelper                  
    */  
    private Static HUMGetCaseInfoResponseHelper initialize(HUMGetCaseInfoResponseHelper caseDTO)
    {
        caseDTO.CaseAge = 0;
        caseDTO.DaysSinceLastModified = 0;
        return caseDTO;
    }

    /**
    * getCaseInfoResponseDTO
    * <p>
    * This method will be used to build the caseInfoResponseDTO for cases 
    *
    * @param    Case caseRec
    * @return   HUMGetCaseInfoResponseHelper                  
    */
    private Static HUMGetCaseInfoResponseHelper getCaseInfoResponseDTO(Case caseRec)
    {
        HUMGetCaseInfoResponseHelper caseInfoResponseDTO = new HUMGetCaseInfoResponseHelper();
        caseInfoResponseDTO.AccountName= CaseRec.Account.Name;
        caseInfoResponseDTO.CaseAge = CaseRec.Case_Age__c == null ? 0 : CaseRec.Case_Age__c;
        caseInfoResponseDTO.CaseNumber = CaseRec.CaseNumber;
        caseInfoResponseDTO.CaseOrigin = CaseRec.Origin;        
        caseInfoResponseDTO.CaseOwner  = CaseRec.Case_Owner__c;
        caseInfoResponseDTO.CaseReason= CaseRec.Reason;
        if(NULL != CaseRec.CTCI_List__c)   caseInfoResponseDTO.Category= CaseRec.CTCI_List__r.Classification__r.Name ;
        caseInfoResponseDTO.Closed= CaseRec.IsClosed;
        caseInfoResponseDTO.Complaint= CaseRec.Complaint__c;
        caseInfoResponseDTO.ComplaintReason = CaseRec.Complaint_Reason__c;
        caseInfoResponseDTO.ContactName= CaseRec.ContactID;
        caseInfoResponseDTO.CreatedBy= CaseRec.CreatedBy.Name;        
        caseInfoResponseDTO.DateTimeClosed = ( CaseRec.ClosedDate != null ? CaseRec.ClosedDate : datetime.newInstance(0001, 01, 01));
        caseInfoResponseDTO.DaysSinceLastModified= CaseRec.Days_Since_Last_Modified__c == null ? 0 : CaseRec.Days_Since_Last_Modified__c;
        caseInfoResponseDTO.Description= CaseRec.Description;                
        caseInfoResponseDTO.DueDate= (CaseRec.Due_Date__c != null ? CaseRec.Due_Date__c : date.valueOf(system.label.HUM_MinDate));
        caseInfoResponseDTO.Escalated= CaseRec.IsEscalated;
        caseInfoResponseDTO.FundingType = CaseRec.Funding_Type__c;
        caseInfoResponseDTO.GAReason= CaseRec.G_A_Reason__c;
        caseInfoResponseDTO.GARightsGiven= CaseRec.G_A_Rights_Given__c;
        caseInfoResponseDTO.HumanaEmployee = CaseRec.Interacting_With__r.Home_Office_Account__c;
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
        caseInfoResponseDTO.Reopened= CaseRec.Reopened__c;
        caseInfoResponseDTO.Status= CaseRec.Status;
        caseInfoResponseDTO.Subtype= CaseRec.Subtype__c;
        caseInfoResponseDTO.Type= CaseRec.Type;
        caseInfoResponseDTO.InteractingAbout = CaseRec.Account.Enterprise_ID__c;
        caseInfoResponseDTO.DateTimeOpened= String.valueof(CaseRec.CreatedDate);
        caseInfoResponseDTO.CreatedByQueue = CaseRec.Created_By_Queue__c;
        caseInfoResponseDTO.lastModifiedByQueue = CaseRec.LastModifiedby_Queue__c;
        caseInfoResponseDTO.QueueOwner= CaseRec.Owner_Queue__c;
        caseInfoResponseDTO.ReOpenCaseAge = String.valueof(CaseRec.Re_Open_Case_Age__c);
        caseInfoResponseDTO.ReOpenCaseDate = String.valueof(CaseRec.Re_Open_Case_Date__c);
        
        //added new Assignments as per REQ-96619.
        caseInfoResponseDTO.applyToComplaint= CaseRec.Apply_to_Complaint__c;
        caseInfoResponseDTO.behavioralHealth= CaseRec.Behavioral_Health__c;
        caseInfoResponseDTO.caseTAT= CaseRec.Case_TAT__c;
        if (NULL != CaseRec.CTCI_List__c) caseInfoResponseDTO.classificationType= CaseRec.CTCI_List__r.Classification_Type__r.Name;
        caseInfoResponseDTO.CoKY_Case= CaseRec.CoKY_Case__c;
        caseInfoResponseDTO.Department= CaseRec.Department__c;
        caseInfoResponseDTO.DSTCase= CaseRec.DST_Case__c;
        caseInfoResponseDTO.emailSent= CaseRec.Email_Sent__c;
        caseInfoResponseDTO.employerGroupName= CaseRec.Employer_Group_Name__c;
        caseInfoResponseDTO.Exchange= CaseRec.Exchange__c;
        caseInfoResponseDTO.ExchangeType= CaseRec.Exchange_Type__c;
        caseInfoResponseDTO.FollowUpDueDate= CaseRec.Follow_up_Due_Date__c;
        caseInfoResponseDTO.FollowUpRequired= CaseRec.Follow_Up_Needed__c;
        caseInfoResponseDTO.GeneralCase= CaseRec.General_Case__c;
        caseInfoResponseDTO.HODentalCase= CaseRec.HO_Dental_Case__c;
        caseInfoResponseDTO.HOMedicalCase= CaseRec.HO_Medical_Case__c;
        caseInfoResponseDTO.InteractingWithName= CaseRec.Interacting_With_Name__c;
        caseInfoResponseDTO.InteractionID= CaseRec.Interaction_ID__c;
        caseInfoResponseDTO.ClosedWhenCreated= CaseRec.IsClosedOnCreate;
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
        caseInfoResponseDTO.TexasComplaintLetterRequested= CaseRec.Texas_Complaint_Letter_Requested__c;
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
    * @return   map<ID,List<HUMAttachmentHelper>>                  
    */
    private static map<ID,List<HUMAttachmentHelper>> getAttachmentDTO(List<String> lstCaseIDs)
    {
        map<ID,List<HUMAttachmentHelper>> mapAttachmentDTO  = new map<ID,List<HUMAttachmentHelper>>();
        List<HUMAttachmentHelper> lstAttachmentDTO = null;
        map<ID,Case> mapCaseToAttachment = new map<ID,Case>([select ID,(select ID,CreatedDate,name,Attachment_URL_Text__c,DocId__c,Version_Id__c,Created_By_Queue__c,Link_To_Task__c,
                Related_To__c,Related_To_Case__c,Related_To_Task__c from Attachements__r limit 5000) from Case where caseNumber in :lstCaseIDs limit 5000]);
        for(ID caseID :mapCaseToAttachment.keyset())
        {
            lstAttachmentDTO  = new List<HUMAttachmentHelper>();
            for(Attachment__c AttachmentRec : mapCaseToAttachment.get(caseID).Attachements__r)
            {
                HUMAttachmentHelper  AttachmentDTO  = new HUMAttachmentHelper ();
                attachmentDTO.FileName = AttachmentRec.Name;
                attachmentDTO.AttachmentURL = AttachmentRec.Attachment_URL_Text__c;
				attachmentDTO.DocId = AttachmentRec.DocId__c;
                attachmentDTO.VersionNumber = AttachmentRec.Version_Id__c;
                
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
    * @return   map<ID,List<HUMAttachmentItemHelper>>                  
    */
    private static map<ID,List<HUMAttachmentItemHelper>> getAttachmentLogItemRecord(list<String> lstCaseIDs)
    {   
        map<ID,List<HUMAttachmentItemHelper>> mapAttachmentItemDTO = new map<ID,List<HUMAttachmentItemHelper>>();
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
    * @return   List<HUMAttachmentItemHelper>                  
    */
    private static List<HUMAttachmentItemHelper>  buildAttachmentLog (List<Attachment_Log__c> attachmentLog , map<ID,Attachment_Log__c> mapAttachmentToAttachmentLog)
    {
        List<HUMAttachmentItemHelper>   lstAttachmentItemDTO  = new List<HUMAttachmentItemHelper>();
        for(Attachment_Log__c attachLog : attachmentLog)
        {
            HUMAttachmentItemHelper attachmentItemDTO = new HUMAttachmentItemHelper();
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
    * @return   list<HUMAttachmentItemAttributeHelper>                  
    */
    private static list<HUMAttachmentItemAttributeHelper> buildAttachmentLogItem (List<Attachment_Log_Item__c> attachmentLogItem)
    {
        list<HUMAttachmentItemAttributeHelper> lstAttachLogItem = new list<HUMAttachmentItemAttributeHelper>();
        for(Attachment_Log_Item__c attachLogItem : attachmentLogItem)
        {
            HUMAttachmentItemAttributeHelper attachmentAttribute = new HUMAttachmentItemAttributeHelper();
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
    * @return   map<ID,HUMInteractionLogHelper>                  
    */
    private static map<ID,HUMInteractionLogHelper> getInteractionLogDTO(List<String> lstInteractionIDs)
    {
        map<ID,HUMInteractionLogHelper> mapInteractionLogDTO = new map<ID,HUMInteractionLogHelper>();
        for(Interaction__c intRec : [select ID,Caller_Name__c,Interacting_With_type__c,Interaction_Origin__c from Interaction__c where ID in :lstInteractionIDs LIMIT 5000])
        {
            HUMInteractionLogHelper interactionLogDTO = new HUMInteractionLogHelper();
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
    * @return   map<ID,List<HUMTaskHelper>>                  
    */
    private static map<ID,List<HUMTaskHelper>> getTaskDTO(List<String> lstCaseIDs)
    {
        map<ID,List<HUMTaskHelper>> mapTaskDTO = new map<ID,List<HUMTaskHelper>>();
        List<HUMTaskHelper> lstTaskDTO;
        map<ID,Case> mapCaseToTask = new map<ID,Case>([select ID,(select ID,Task_Number__c,Age_Since_Created__c,OwnerID,Owner.Name,Owner_Queue__c,CreatedByID,
                CreatedBy.Name,Created_By_Queue__c,WhatId,LastModifiedByID,LastModifiedBy.Name,LastModifiedby_Queue__c, Last_Modified_Age__c,
                subject,ActivityDate, Status,Priority,Description from tasks LIMIT 5000)from Case where caseNumber in :lstCaseIDs LIMIT 5000]);
        for(ID caseID :mapCaseToTask.keyset())
        {
            lstTaskDTO  = new List<HUMTaskHelper>();
            for(task taskRec : mapCaseToTask.get(caseID).tasks)
            {
                HUMTaskHelper  taskDTO  = new HUMTaskHelper();
                taskDTO.Subject = taskRec.Subject ;
                taskDTO.DueDate = taskRec.ActivityDate;
                taskDTO.Status = taskRec.Status;
                taskDTO.Priority = taskRec.Priority;
                taskDTO.Description = taskRec.Description;
                taskDTO.LastModifiedByAge = taskRec.Last_Modified_Age__c != null ? Integer.valueOf(taskRec.Last_Modified_Age__c) : null;
                taskDTO.TaskNumber = taskRec.Task_Number__c ;
                taskDTO.AgeSinceCreated = taskRec.Age_Since_Created__c != null ? Integer.valueOf(taskRec.Age_Since_Created__c) : null;
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
        List<Case> lstCaseRecs = database.Query('select ID,interaction_ID__c,caseNumber from case where '+ whereClause + ' ORDER BY CreatedDate DESC NULLS LAST LIMIT 5000');        
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
    * @return   List<HUMGetCaseInfoResponseHelper>                 
    */
    public static List<HUMGetCaseInfoResponseHelper> getErrorLogInstance(String sErrorCode,String sErrorMessage,Boolean sSuccess )
    {
        HUMGetCaseInfoResponseHelper CaseInfoResponseDTO = new HUMGetCaseInfoResponseHelper();
        caseInfoResponseDTO = HUMGetCaseInfoService.initialize(caseInfoResponseDTO);        
        caseInfoResponseDTO.Success = sSuccess;
        caseInfoResponseDTO.ErrorMessage =sErrorMessage;
        caseInfoResponseDTO.ErrorCode = sErrorCode;
        caseInfoResponseDTO.Closed  = false;
        caseInfoResponseDTO.DateTimeClosed =  datetime.newInstance(0001, 01, 01);
        caseInfoResponseDTO.DueDate = date.valueOf(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_DATEVALUE_HUM);
        caseInfoResponseDTO.Escalated = false;
        caseInfoResponseDTO.FollowUpRequired = false;
        caseInfoResponseDTO.HumanaEmployee = false; 
        caseInfoResponseDTO.Reopened = false;
        caseInfoResponseDTO.CaseAge = GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM;
        return new List<HUMGetCaseInfoResponseHelper>{CaseInfoResponseDTO};
    }  
}