/*****************************************************************************************
Apex class Name    : CaseHistoryComponent_LC_HUM
Version            : 1.0
Function           : Gets Case History Records as Wrapper to LWC caller   
Created Date       : 11/20/2020 
Test Class         : CaseHistoryComponent_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
     Ákshay K                                           11/24/2020          Original Version
     Ashish Kumar                                       11/26/2020          Modified query
    Ritik Agarwal                                       03/03/2021          shifted the SOQL to data laer class(CaseHistoryComponent_LD_HUM)
   Ashish Kumar/Ritik									09/13/2021				  Added login for Date time formatting
   Nirmal Garg											09/24/2021			Added logic for pharmacy case history
   Gowthami Thota										09/24/2021			Changed logic to pull Product value from MemberPlan and 
                                                                                modified to re-use on Member Plan case history
   Gowthami Thota                                       2/3/2022             US- 2081786 Case Management - Case Linking 
   Swetha Thuti                                         12/21/2021          Added logic for the Accordion - Case History Table
   Ankima                                               02/17/2022          Added method for case comment timeline                       
    Ankima                                              09/21/2022          Added changes for switch for archived case
 *********************************************************************************************************************************/

public with sharing class CaseHistoryComponent_LC_HUM 
{ 
    public CaseHistoryComponent_LC_HUM(){}
    
     /*
     * Method Name : generatecasehistory
     * Description : generate the string for Query and fetch the case records.
     * Return type : NA
     * Parameter   : NA
     */
    @AuraEnabled
    public static String generatecasehistory(ID objID)
    {
        String sQuery = initializeQuery(objID);
        if(String.isNotBlank(sQuery))
        {
            return getcasewrapper(Database.Query(sQuery));
        }
        return sQuery;
    }
    
     /*
     * Method Name : archivedLinkSwitchValue
     * Description : to check whether the archival related switch is on or off in order to show or hide Archiced Case History link.
     * Return type : Boolean
     * Parameter   : NA
     */
    @AuraEnabled
    public static Boolean archivedLinkSwitchValue()
    {
    	return HUMUtilityHelper.isCRMFunctionalityON('3579023');
    }
	
	/*
     * Method Name : generatepharmacycasehistory
     * Description : generate the string for Query and fetch the case records.
     * Return type : NA
     * Parameter   : NA
     */
    @AuraEnabled(cacheable=true)
    public static String generatepharmacycasehistory(String objID, integer days)
    {
        try {
            Map<String,Object> bindParams = new Map<string,object>();
            bindParams.put('accountId', objID);
            string sQuery = CaseHistoryComponent_LD_HUM.initializePharmacyQuery(days);
            if(string.isNotBlank(sQuery)){
                return getpharmacycasewrapper(Database.queryWithBinds(sQuery, bindParams, AccessLevel.USER_MODE));
            }
            return null;
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
       
    /*
     * Method Name : initializeQuery
     * Description : create the query for Case
     * Return type : String
     * Parameter   : NA
     */
    private static String initializeQuery(ID objID)
    {      
        return CaseHistoryComponent_LD_HUM.initializeQuery(objID);
    }
    
    /*
     * Method Name : getCaseCommentDetails
     * Description : generate the string for Query and fetch the case comment records.
     * Return type : NA
     * Parameter   : Case Record Id
     */
    @AuraEnabled
    public static Object getCaseCommentDetails(String caseId){
        return CaseHistoryComponent_LD_HUM.getCaseCommentDetails(caseId);
        
    }

     /*
     * Method Name : linkSelectedCases
     * Description : Method to link the selected cases.
     * Return type : void
     * Parameter   : NA
    */ 
    @AuraEnabled
    public static messageData_DTO_HUM linkSelectedCases(List<String> sSelectedCaseIds)
    {
        messageData_DTO_HUM msgWrap = new messageData_DTO_HUM();

        Boolean isSuccess = CaseLinkingLogic_LS_HUM.linkCases(sSelectedCaseIds);
        
        User objUser = [Select Id, Current_Queue__c from User where Id =: UserInfo.getUserId()];
        List<Work_Queue_Setup__C> lstWQS = [Select Id from Work_Queue_Setup__C where Name =: objUser.Current_Queue__c AND IsActive__c = TRUE];  
       if(isSuccess && !lstWQS.isEmpty() )
        {
            msgWrap.bShowMessage = true;
            msgWrap.bShowWarning =false;
        }
        
        else if (!isSuccess && !lstWQS.isEmpty() )
        {
            msgWrap.bShowMessage = false;
            msgWrap.bShowWarning =true;
        }
        
        else 
        {
            msgWrap.bShowMessage = false;
            msgWrap.bShowWarning =false;
            msgWrap.bInvalidQueueWarning=true;
        }
        return msgWrap;
    }
    
    public class messageData_DTO_HUM {
        @AuraEnabled public Boolean bShowWarning{get;set;}
    	@AuraEnabled public Boolean bShowMessage{get;set;}
    	@AuraEnabled public Boolean bInvalidQueueWarning{get;set;}
        
        public messageData_DTO_HUM(){
            bShowWarning = false;
            bShowMessage = false;
            bInvalidQueueWarning = false;
        }
    }
	
	/*
     * Method Name : getpharmacycasewrapper
     * Description : wrapper conversion for Case Obj
     * Return type : String
     * Parameter   : NA
     */
    private static String getpharmacycasewrapper(List<Case> lstCaseDetails)
    {      
        List<PharmacyCaseHistory_DTO_HUM> lstCaseWrap = New List<PharmacyCaseHistory_DTO_HUM>();
        if(lstCaseDetails !=NULL )
        {   
            for(case oCase: lstCaseDetails)
            {
                PharmacyCaseHistory_DTO_HUM oCaseWrap = new PharmacyCaseHistory_DTO_HUM();
                oCaseWrap.Id = oCase.Id;
                oCaseWrap.sCaseIdVal = oCase.Id;
                oCaseWrap.sCaseNum = oCase.CaseNumber;
                oCaseWrap.sDCN = (String.IsNotBlank(oCase.DCN__c)) ? 'Yes' : 'No';
                oCaseWrap.sCreatedDate = (oCase.CreatedDate!=null) ?  oCase.CreatedDate.format('h:mm a') + ' | ' + oCase.CreatedDate.format('MM/dd/yyyy') :'';
				oCaseWrap.sOpenedDate = (oCase.CreatedDate!=null) ? oCase.CreatedDate.format('MM/dd/yyyy') :'';
                oCaseWrap.sClosedDate = (oCase.ClosedDate!=null) ? oCase.ClosedDate.format('MM/dd/yyyy') :'';
                oCaseWrap.sClassification = oCase.CTCI_List__r.Classification__r.Name;
                oCaseWrap.sIntent = oCase.CTCI_List__r.Intent__r.Name;
                oCaseWrap.sProduct = oCase.Product__C;
                oCaseWrap.sStatus = oCase.Status;
                oCaseWrap.sOrigin = oCase.Origin;
                oCaseWrap.sType = oCase.Type;
                oCaseWrap.sInteractingWith = oCase.Interacting_With__r.Name;
                oCaseWrap.sInteractingWithType = oCase.Interacting_With_Type__c;
                oCaseWrap.sInteractingAbout = oCase.Interacting_About__c;
                oCaseWrap.sPriority = oCase.Priority;
                oCaseWrap.sCreatedBy = oCase.CreatedBy.Name;
                oCaseWrap.sCreatedByQueue = oCase.Created_By_Queue__c;
                oCaseWrap.sOwnerQueue = oCase.Owner_Queue__c;
                oCaseWrap.sLastModifiedByQueue = oCase.LastModifiedby_Queue__c;
                oCaseWrap.bDentalCase = oCase.HO_Dental_Case__c;
                oCaseWrap.bMedicalCase = oCase.HO_Medical_Case__c;
                oCaseWrap.bGeneralCase = oCase.General_Case__c;
                oCaseWrap.sLastModifiedBy = oCase.LastModifiedBy.Name;
                oCaseWrap.sComplaint = oCase.Complaint__c;
                oCaseWrap.sFollowUpDate = (oCase.Follow_up_Due_Date__c !=null) ? oCase.Follow_up_Due_Date__c.format() : '';
                oCaseWrap.lCaseComments = (oCase.CaseComments != null) ? oCase.CaseComments : null;
                lstCaseWrap.add(oCaseWrap);
            }               
            return JSON.serialize(lstCaseWrap);
        }
        else
        {
            return null;
        }
    }
	
	

    /*
     * Method Name : getcasewrapper
     * Description : wrapper conversion for Case Obj
     * Return type : String
     * Parameter   : NA
     */
    private static String getcasewrapper(List<Case> lstCaseDetails)
    {      
        List<CaseHistory_DTO_HUM> lstCaseWrap = New List<CaseHistory_DTO_HUM>();
        Map<Id,String> dcnUrlMap = DCNRedirection_LC_HUM.generateRedirectToDCNURL(lstCaseDetails);
        if(lstCaseDetails !=NULL )
        {   
            for(case oCase: lstCaseDetails)
            {
                CaseHistory_DTO_HUM oCaseWrap = new CaseHistory_DTO_HUM();
                oCaseWrap.Id = oCase.Id;
                oCaseWrap.sCaseIdVal = oCase.Id;
                oCaseWrap.sCaseNum = oCase.CaseNumber;
                oCaseWrap.sDCN = (String.IsNotBlank(oCase.DCN__c)) ? 'Yes' : 'No';
                oCaseWrap.sdcnURL = dcnUrlMap.get(oCase.Id);
                oCaseWrap.sCreatedDate = (oCase.CreatedDate!=null) ? oCase.CreatedDate.format('MM/dd/yyyy') :'';
                oCaseWrap.sClosedDate = (oCase.ClosedDate!=null) ? oCase.ClosedDate.format('MM/dd/yyyy') :'';
                oCaseWrap.sClassification = oCase.CTCI_List__r.Classification__r.Name;
                oCaseWrap.sIntent = oCase.CTCI_List__r.Intent__r.Name;
                oCaseWrap.sProduct = oCase.Member_Plan_Id__r.Product__c;
                oCaseWrap.sStatus = oCase.Status;
                oCaseWrap.sOrigin = oCase.Origin;
                oCaseWrap.sType = oCase.Type;
                oCaseWrap.sInteractingWith = oCase.Interacting_With__r.Name;
                oCaseWrap.sInteractingWithType = oCase.Interacting_With_Type__c;
                oCaseWrap.sInteractingAbout = oCase.Interacting_About__c;
                oCaseWrap.sInteractingAboutType = oCase.Interacting_About_Type__c;
                oCaseWrap.sPriority = oCase.Priority;
                oCaseWrap.sCreatedBy = oCase.CreatedBy.Name;
                oCaseWrap.sCreatedByQueue = oCase.Created_By_Queue__c;
                oCaseWrap.sOwnerQueue = oCase.Owner_Queue__c;
                oCaseWrap.sLastModifiedByQueue = oCase.LastModifiedby_Queue__c;
                oCaseWrap.bDentalCase = oCase.HO_Dental_Case__c;
                oCaseWrap.bMedicalCase = oCase.HO_Medical_Case__c;
                oCaseWrap.bGeneralCase = oCase.General_Case__c;
                oCaseWrap.sLastModifiedBy = oCase.LastModifiedBy.Name;
                oCaseWrap.sComplaint = oCase.Complaint__c;
                oCaseWrap.sFollowUpDate = (oCase.Follow_up_Due_Date__c !=null) ? oCase.Follow_up_Due_Date__c.format() : '';
                  
                lstCaseWrap.add(oCaseWrap);
            }               
            return JSON.serialize(lstCaseWrap);
        }
        else
        {
            return null;
        }
    }

    class CaseHistory_DTO_HUM
    {
        public String Id  = '';
        public String sCaseIdVal  = '';
        public String sCaseNum  = '';
        public String sDCN = '';
        public String sdcnURL = '';
        public String sCreatedDate  = '';
        public String sClosedDate  = '';
        public String sClassification  = '';
        public string sIntent  = '';
        public String sProduct  = '';
        public String sStatus  = '';
        public String sOrigin  = '';
        public string sType  = '';
        public String sInteractingWith  = '';
        public String sInteractingWithType  = '';
        public String sInteractingAbout  = '';
        public String sInteractingAboutType  = '';
        public String sPriority  = '';
        public String sCreatedBy  = '';
        public String sCreatedByQueue  = '';
        public String sOwnerQueue  = '';
        public String sLastModifiedByQueue  = '';
        public String sLastModifiedBy  = '';
        public String sFollowUpDate  = '';
        public String sComplaint = '';
        public boolean bDentalCase = false;
        public boolean bMedicalcase = false;
        public boolean bGeneralCase = false;
    }
	
	class PharmacyCaseHistory_DTO_HUM
    {
        public String Id  = '';
        public String sCaseIdVal  = '';
        public String sCaseNum  = '';
        public String sDCN = '';
        public String sCreatedDate  = '';
		public String sOpenedDate  = '';
        public String sClosedDate  = '';
        public String sClassification  = '';
        public string sIntent  = '';
        public String sProduct  = '';
        public String sStatus  = '';
        public String sOrigin  = '';
        public string sType  = '';
        public String sInteractingWith  = '';
        public String sInteractingWithType  = '';
        public String sInteractingAbout  = '';
        public String sPriority  = '';
        public String sCreatedBy  = '';
        public String sCreatedByQueue  = '';
        public String sOwnerQueue  = '';
        public String sLastModifiedByQueue  = '';
        public String sLastModifiedBy  = '';
        public String sFollowUpDate  = '';
        public String sComplaint = '';
        public boolean bDentalCase = false;
        public boolean bMedicalcase = false;
        public boolean bGeneralCase = false;
        public List<CaseComment> lCaseComments;
    }
    @AuraEnabled
    public static String getLinkedCaseList(String caseId) {
        String result = '{"data":[]}';
        List<Case_Junction__c> lstCaseJunc = new List<Case_Junction__c>();
        List<LinkCase_WrapperClass_LC_HUM> lstCaseLink = new List<LinkCase_WrapperClass_LC_HUM>();
        if(String.isNotBlank(caseId)) 
        {
            lstCaseJunc = [Select  CreatedDate, Case__r.CaseNumber, Case__r.Id, Case_Master__r.Id, Case_Master__r.CaseNumber, Work_Queue__r.Work_Queue_Name__c, CreatedBy.LastName, CreatedBy.FirstName from Case_Junction__c where  IsLinked__c = true AND (Case__r.Id =: caseId OR Case_Master__r.Id =: caseId) Order By CreatedDate Desc,Case__r.CaseNumber ASC,Case_Master__r.CaseNumber ASC  limit :CaseManagement_Constants_HUM.NUMBER_CONSTANT_SEVENTHOUSAND_HUM];   
            for(Case_Junction__c objCJ :lstCaseJunc)
            {
                LinkCase_WrapperClass_LC_HUM objLink = new LinkCase_WrapperClass_LC_HUM();
                if((String.valueof(objCJ.Case_Master__r.Id)).equals(caseId))  
                {
                    objLink.sCaseNumber = objCJ.Case__r.CaseNumber;
                    objLink.sCaseId = objCJ.Case__r.Id;
                }
                else
                {
                    objLink.sCaseNumber = objCJ.Case_Master__r.CaseNumber;
                    objLink.sCaseId = objCJ.Case_Master__r.Id;
                }
                string sDate = objCJ.CreatedDate.month() + GlobalCommonConstants_HUM.BACKSLASH_HUM + objCJ.CreatedDate.day() + GlobalCommonConstants_HUM.BACKSLASH_HUM + objCJ.CreatedDate.year();
                objLink.sInfo = objCJ.CreatedBy.FirstName + ' ' + objCJ.CreatedBy.LastName + '  ' +  sDate;
                if(!string.isBlank(objCJ.Work_Queue__r.Work_Queue_Name__c))
                    objLink.sWorkQueue =  objCJ.Work_Queue__r.Work_Queue_Name__c;   
            
                lstCaseLink.add(objLink);
            }
            result = JSON.serialize(lstCaseLink); 
            result = '{"data":'+result+'}';
        }
        return result;
    } 

    
    @AuraEnabled
    public static String generateCaseComments(String objID)
    {
      List<Case> caseComments = CaseHistoryComponent_LD_HUM.getCaseDetails(objID);
      return getcasecommentwrapper(caseComments);
    }
      private static String getcasecommentwrapper(List<Case> lstCaseComments)
    {    
        String sClassification;
        String sIntent;
        String logNoteCode;
        List<PharmacyOMSLogNotes_DTO_HUM> lstCaseWrap = New List<PharmacyOMSLogNotes_DTO_HUM>();
        if(lstCaseComments !=NULL )
        {   
            for(Case caseObj: lstCaseComments)
            {
                
                sClassification = caseObj.CTCI_List__r.Classification__r.Name;
                sIntent = caseObj.CTCI_List__r.Intent__r.Name;
                if(sClassification==null) sClassification='';
                if(sIntent==null) sIntent='';
                logNoteCode = sClassification+' / '+sIntent;
                for(CaseComment oCase :caseObj.CaseComments ){
                    PharmacyOMSLogNotes_DTO_HUM oCaseWrap = new PharmacyOMSLogNotes_DTO_HUM();
                    oCaseWrap.LogNoteCode = logNoteCode;
                    oCaseWrap.LogNoteDate = (oCase.LastModifiedDate!=null) ?  oCase.LastModifiedDate.format('h:mm a') + ' | ' + oCase.LastModifiedDate.format('MM/dd/yyyy') :'';
                    oCaseWrap.LogNoteMessage =oCase.CommentBody;
                    oCaseWrap.LogNoteUser = oCase.CreatedBy.Name;
                    lstCaseWrap.add(oCaseWrap);
                }
            }  
            return JSON.serialize(lstCaseWrap);
        }
        else
        {
            return null;
        }
    }    
    
}