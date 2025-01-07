/******************************************************************************************************************************
Apex Class Name  : PharmacyCaseHistoryComponent_C_RT_HUM
Version          : 1.0 
Created Date     : 04/12/2018
Function         : Controller to PharmacyCaseHistory_CMP_RT_HUM VF component. It is used to display cases 
                   related to Account in salesforce in DataTable.                   
Test Class       : PharmacyCaseHistoryComponent_T_RT_HUM
Modification Log :
 *  Developer              Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 * Pallavi R                  215883              04/12/2018        REQ - 323439 -Cloned from PharmacyCaseHistoryComponent_C_HUM as part of resolution changes
 * Alvaro Collazo Suarez      283774              9/06/2018         REQ - 357351 - Humana Pharmacy Page (Access Point Case History Section)
 * Deepankar Binjola          30410               07/25/2019        REQ - 403707 - Add exception in Error log object in Pharmacy_VF_HUM
 * Santhi Mandava                                 02/18/2020        US - 860272 - Added "DCN Present" column next to case number column
 * Santhi Mandava                                 02/18/2020        US - 939206 - Removed RF check for "DCN Present" column next to status column
 * Anjani Vaddadi                                 04/22/2020        US#- 1036783 - Archival switch using Custom meta data And DF541
 * Sunil Kumar Reddy                              04/24/2020        User Story 1042378 - PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup - 5/8 and 5/22 Release (CRM)
 * Pallavi Shewale				  02/10/2021	    User Story 1669828 - PR00094226 - CR AEP readiness -Research - Critical Security Issues
 * Prasanthi Kandula                              06/15/2021        US 2274165: T1PRJ0001319 - (PR00084353) - CR1690 - MF # 4 -SF - TECH Story for Remove the references for Archival (CRM Archival Strategy Rollback)
 * Apurva urkude              2549610                08/13/2021                   REQ - 2549610 MF6- SF -TECH Check Marx vulnerability Fix - Pharmacy Case History Section 
  * Ankima/Kajal                                  11/18/2022        Archived Case related changes for Pharmacy page 
 ********************************************************************************************************************************/
public with sharing Class PharmacyCaseHistoryComponent_C_RT_HUM 
{
    @TestVisible private String sQuery ='';
    @TestVisible private transient String sCaseDetailsJSONreturn = '';
    @TestVisible private DateTime dtStart = DateTime.Now();
    @TestVisible private DateTime dtEnd = DateTime.Now();
    @TestVisible private transient List<Case> lstCaseDetails = New List<Case>(); 
    @TestVisible private transient List<CaseHistoryComponent_DTO_RT_HUM> lstCaseWrap = New List<CaseHistoryComponent_DTO_RT_HUM>();
    @TestVisible private String sRequest ='';
    @TestVisible public Boolean isAccountIDPolMemberIDNull{set;get;}
    /*
     * Method Name : PharmacyCaseHistoryComponent_C_RT_HUM
     * Description : Constructor for the class , Intialize variables
     * Return type : NA
     * Paramater   : NA
    */    
    public PharmacyCaseHistoryComponent_C_RT_HUM() 
    {
         try{            
               isAccountIDPolMemberIDNull = false;
               lstLogErrors = new List<Error_Log__c>();
               bpolCheck = false;
               benableCase = true;
               brecordCheck = false;
               
               sObjectID = (ID) ApexPages.currentPage().getParameters().Get(GLOBAL_CONSTANT_HUM.ID_HUM);
               generateQueryString();
               verifyPolicyRecords();
            }
            catch(Exception e)
            {
               HUMExceptionHelper.bRestrictLog = true;
               lstLogErrors = HUMExceptionHelper.logErrors(e,'PharmacyCaseHistoryComponent_C_RT_HUM','PharmacyCaseHistoryComponent_C_RT_HUM');
            }
            columnHeader = json.serialize(new List < String > {'Id', 'Link','Case Number','DCN Present', 'Date/Time Opened','Date/Time Closed','Classification','Intent','Product','Status','Case Origin','Type','Interacting With','Interacting With Type','Interacting About','Priority','Created By','Created By Queue','HO Dental','HO Medical','General','Last Modified By'});
            columnName = json.serialize(new List < String > {'Id', 'sCaseIdVal','sCaseNum','sDCN','sCreatedDate','sClosedDate','sClassification','sIntent','sProduct','sStatus','sOrigin','sType','sInteractingWith','sInteractingWithType','sInteractingAbout','sPriority','sCreatedBy','sCreatedByQueue','bDentalCase','bMedicalcase','bGeneralCase','sLastModifiedBy'});
            
    }
    public String sObjectName{get; set;}
    public Id sObjectID {get; set;}
    public Id sPMObjectID {get; set;}
    Public string columnName {get; private set;}
    Public string columnHeader{get; private set;}  
    Public string columnHeaderTitle{get; private set;}
    Public String sCaseLinkID {get; set;}
    public String sRowNumber {get; set;}
    public String sCaseRowLinkID {get;set;}
    public Id sCaseID {get; set;}
    public String scaseIdValues {get; set;}
    public String sCaseNumber {get; set;}
    public String sRedirectURL {get; set;}
    public String sCaseNum {get;set;}
    public String sStartDate{get; set;}
    public String sEndDate{get; set;}    
    public Id objID {get;set;}
    public Boolean bpolCheck {get; set;}
    public Boolean benableCase {get; set;}
    public Boolean brecordCheck {get; set;}
    public Boolean bShowMessage{get;set;}
    public Boolean bShowWarning{get;set;}
    public List<Policy__c> lstPolicy{get; set;}
    public List<Policy_Member__c> lstPolicyMember{get; set;}      
    public List<Case_Junction__c> lstCaseJunc {get;set;}
    public List<LinkCase_Wrapper_DTO_HUM> lstCaseLink {get;set;}  
    public String sLinkJson {get;set;}
    public String newCaseId {get; set;}
    public Policy_Member__c polId;
    public boolean showArchivedCase 
    {
        get{
        	return HUMUtilityHelper.isCRMFunctionalityON('3579023');    
        }set;
    }
    public List<Error_Log__c> lstLogErrors
    {
        get
        {
            if(lstLogErrors==null){return new List<Error_Log__c>();}
            else{return lstLogErrors;}
        }
        set;
    }          
    public string sCaseDetailsJSON
    {
        get 
        {       
           if(lstCaseDetails !=NULL )
            {               
                for(case oCase: lstCaseDetails)
                {
                  CaseHistoryComponent_DTO_RT_HUM oCaseWrap = new CaseHistoryComponent_DTO_RT_HUM();
                  oCaseWrap.Id = oCase.Id;
                  oCaseWrap.sCaseIdVal = oCase.Id;
                  oCaseWrap.sCaseNum = oCase.CaseNumber + ',srcUp,' + '/' + oCase.Id ;
                  oCaseWrap.sDCN = (String.IsNotBlank(oCase.DCN__c)) ? oCase.DCN__c : 'No';
                  if(oCase.CreatedDate!=null)oCaseWrap.sCreatedDate = oCase.CreatedDate.format('MM/dd/yyyy');
                  if(oCase.ClosedDate!=null)oCaseWrap.sClosedDate = oCase.ClosedDate.format('MM/dd/yyyy');
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
                  oCaseWrap.bDentalCase = oCase.HO_Dental_Case__c;
                  oCaseWrap.bMedicalCase = oCase.HO_Medical_Case__c;
                  oCaseWrap.bGeneralCase = oCase.General_Case__c;
                  lstCaseWrap.add(oCaseWrap);
                }    
                sCaseDetailsJSONreturn = JSON.serialize(lstCaseWrap);
                sCaseDetailsJSONreturn = '{"data":'+sCaseDetailsJSONreturn+'}';
                return sCaseDetailsJSONreturn;
            }
            else
            {
                return null;
            }
        }
        set;
    }
       
    /*
     * Method Name : initializeQuery
     * Description : create the query for Case
     * Return type : String
     * Parameter   : NA
     */
    private String initializeQuery()
    {      
        String sQueryInit='Select Id,CaseNumber,DCN__c,Origin,Type,'+
        +'CTCI_List__r.Classification__r.Name,CTCI_List__r.Intent__r.Name,CreatedBy.Name,'+
        +'Product__c,Interacting_With__r.Name,Interacting_With_Type__c,'+
        +'Interacting_About__c,Status,Priority,CreatedDate,ClosedDate,Created_By_Queue__c,HO_Dental_Case__c,HO_Medical_Case__c,General_Case__c from Case';
        Policy_Member__c polId ;
        if(ApexPages.CurrentPage().getParameters().containsKey('id'))
        {
            ID objURLID =  ApexPages.currentPage().getParameters().get('id').escapeHtml4();   
            polId = [Select Id, member__c From Policy_Member__c Where Id =: String.escapeSingleQuotes(objURLID) limit 1];
            if(objURLID != null || objURLID != '')
           {  
              objID = polId.member__c;
           }
        }

        if(objID!=null)
        { 
            if (objID.getSobjectType().getDescribe().getName() == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM)
            {    
                sQueryInit = sQueryInit + ' Where AccountId =\''+ String.escapeSingleQuotes(objID) +'\'';
            } 
        }
        else
        {
            if((HUMConstants__c.getInstance('HUMPharmacy_Case_History_Switch').StringValue__c).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.STRING_TRUE))
             {
                isAccountIDPolMemberIDNull = true;             
                return null;
             }
        }
        return sQueryInit;
    }

    /*
     * Method Name : onLoadErrorLog
     * Description : Error Log creation on Page load
     * Return type : Void
     * Parameter   : NA
     */
    public void onLoadErrorLog()
    {
        
        try
        {
            if(isAccountIDPolMemberIDNull){
        
                ID objURLID =  ApexPages.currentPage().getParameters().get('id');
                Error_Log__c errorLog = new Error_Log__c(Error_Message__c = 'Policy Member ID:'+polId.Id+', '+'\n'+'Member__c :'+polId.Member__c+', '+'\n'+'Policy Member Name from Url ::'+objURLID+', '+'\n'+'EnterpriseId is ::'+polId.member__r.Enterprise_Id__c,                                
                class_name__c = 'PharmacyCaseHistoryComponent_C_RT_HUM',
                Method_Name__c = 'initializeQuery',
                Error_Code__c = 'HUMCustomException',
                Type__c = 'Application Error',
                Request_Sent__c = true);
				If (Error_Log__c.sObjectType.getDescribe().isCreateable()) {
                Database.insert(errorLog);
				}
            
            }    
        }    
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'PharmacyCaseHistoryComponent_C_RT_HUM','onLoadErrorLog');
        }
                                                    
    }
    /*
     * Method Name : verifyPolicyRecords
     * Description : Verify if the group account has policies mapped
     * Return type : Void
     * Parameter   : NA
     */
    public void verifyPolicyRecords()
    {
       lstPolicy = new List<Policy__c>();
       lstPolicyMember = new List<Policy_Member__c>();
       Policy_Member__c polId ;
       if(ApexPages.CurrentPage().getParameters().containsKey('id'))
        {             
            ID objURLPOLID = ApexPages.currentPage().getParameters().get('id').escapeHtml4(); 
            polId = [Select Id, member__c From Policy_Member__c Where Id =: String.escapeSingleQuotes(objURLPOLID) limit 1];           
            if(objURLPOLID != null || objURLPOLID != '')
            {    
                objID = polId.member__c;//objURLPOLID;            
                if (objID.getSobjectType().getDescribe().getName() == GLOBAL_CONSTANT_HUM.OBJECT_ACCOUNT_HUM)
                {
                    Account oAcctRT = [SELECT id,RecordType.Name from Account where Id = :objID];
                    if(oAcctRT.RecordType.Name == GLOBAL_CONSTANT_HUM.GROUP_RECORDTYPE_HUM || Test.isRunningTest())
                    {
                       lstPolicy = [SELECT id,Name FROM Policy__c where Group_Name__c = :objID limit 1];
                       if(!lstPolicy.isEmpty()) brecordCheck = true;
                    }
                    else if(oAcctRT.RecordType.Name == GLOBAL_CONSTANT_HUM.MEMBER_RECORDTYPE_HUM || Test.isRunningTest())
                    {
                       lstPolicyMember = [SELECT id,Name FROM Policy_Member__c where Member__c = :objID limit 1];
                       if(!lstPolicyMember.isEmpty()) brecordCheck = true;
                    }
                }
            }
        }
    }
    
    /*
     * Method Name : generateQueryString
     * Description : generate the string for Query and fetch the case records.
     * Return type : NA
     * Parameter   : NA
     */
    @testvisible private void generateQueryString()
    {        
        sQuery = initializeQuery();     
        if(String.isNotBlank(sStartDate) && String.isNotBlank(sEndDate))
        {            
            sQuery = sQuery + ' AND ( CreatedDate >=:dtStart AND CreatedDate <=:dtEnd )';
        }
        if(String.isNotBlank(sQuery))
        lstCaseDetails = Database.Query(sQuery);        
    }    
    /*
     * Method Name : getCaseComments
     * Description : Get the caseId for the case on which mouse is hover
     * Return type : void
     * Parameter   : NA
    */      
    public void getCaseComments()    
    {        
        scaseIdValues = ApexPages.CurrentPage().getParameters().get('caseIdValues');        
    }
   
    /*
     * Method Name : linkSelectedCases
     * Description : Method to link the selected cases.
     * Return type : void
     * Parameter   : NA
    */       
    public void linkSelectedCases()
    {
        bShowMessage = false;
        bShowWarning = False;
        String sSelectedCaseIds = ApexPages.CurrentPage().getParameters().get('sSelectedCases');        
        //Boolean isSuccess = CaseLinkingLogic_S_HUM.linkCases(sSelectedCaseIds);       
        bShowMessage = CaseLinkingLogic_S_HUM.linkCases(sSelectedCaseIds.escapeHtml4());
		if(!bShowMessage)
        bShowWarning = !CaseLinkingLogic_S_HUM.linkCases(sSelectedCaseIds.escapeHtml4());
        ApexPages.CurrentPage().getParameters().put('bShowMessage', bShowMessage + '');        
    }
    
    /*
     * Method Name : linkCasesList
     * Description : Method to list out the linked Cases
     * Return type : void
     * Parameter   : NA
    */       
    public void linkCasesList()
    {
        lstCaseJunc = new List<Case_Junction__c>();
        lstCaseLink = new List<LinkCase_Wrapper_DTO_HUM>();
        if(String.isNotBlank(sCaseLinkId)) 
        {
            lstCaseJunc = [Select  CreatedDate, Case__r.CaseNumber, Case__r.Id, Case_Master__r.Id, Case_Master__r.CaseNumber, Work_Queue__r.Work_Queue_Name__c, CreatedBy.LastName, CreatedBy.FirstName from Case_Junction__c where  IsLinked__c = true AND (Case__r.Id =: sCaseLinkId OR Case_Master__r.Id =: sCaseLinkId) Order By CreatedDate Desc,Case__r.CaseNumber ASC,Case_Master__r.CaseNumber ASC  limit :GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_SEVENTHOUSAND_HUM];   
            for(Case_Junction__c objCJ :lstCaseJunc)
            {
                LinkCase_Wrapper_DTO_HUM objLink = new LinkCase_Wrapper_DTO_HUM();
                if((String.valueof(objCJ.Case_Master__r.Id)).equals(sCaseLinkId))  
                {
                    objLink.sCaseNumber = objCJ.Case__r.CaseNumber;
                    objLink.sCaseId = objCJ.Case__r.Id;
                }
                else
                {
                    objLink.sCaseNumber = objCJ.Case_Master__r.CaseNumber;
                    objLink.sCaseId = objCJ.Case_Master__r.Id;
                }
                string sDate = objCJ.CreatedDate.month() + GLOBAL_CONSTANT_HUM.BACKSLASH_HUM + objCJ.CreatedDate.day() + GLOBAL_CONSTANT_HUM.BACKSLASH_HUM + objCJ.CreatedDate.year();
                objLink.sInfo = objCJ.CreatedBy.FirstName + ' ' + objCJ.CreatedBy.LastName + '  ' +  sDate;
                if(!string.isBlank(objCJ.Work_Queue__r.Work_Queue_Name__c))
                    objLink.sWorkQueue =  objCJ.Work_Queue__r.Work_Queue_Name__c;   
            
                lstCaseLink.add(objLink);
            }
            sLinkJson = JSON.serialize(lstCaseLink); 
            sLinkJson = '{"data":'+sLinkJson+'}';
        }
        
    } 
   
}