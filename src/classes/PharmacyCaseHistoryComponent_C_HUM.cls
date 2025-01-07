/******************************************************************************************************************************
Apex Class Name  : PharmacyCaseHistoryComponent_C_HUM
Version          : 1.0 
Created Date     : 01/18/2017
Function         : Controller to PharmacyCaseHistory_CMP_HUM VF component. It is used to display cases 
                   related to Account in salesforce in DataTable.                   
Test Class       : PharmacyCaseHistoryComponent_T_HUM
Modification Log :
 *  Developer              Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 * Rajesh Chandgothia       59331             01/17/2017        Original Version
 * Pradeepkumar Dani		102268	 		  06/16/2017		Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
 * Lavanya Rao				 							10/11/2021                 User Story 2731234: T1PRJ0002606- 2021 MF6 - Swat-SF-Tech-HumConstant and Global Constant Fix

 ********************************************************************************************************************************/
public with sharing Class PharmacyCaseHistoryComponent_C_HUM 
{
    @TestVisible private String sQuery = '';
    @TestVisible private transient String sCaseDetailsJSONreturn = '';
    @TestVisible private DateTime dtStart = DateTime.Now();
    @TestVisible private DateTime dtEnd = DateTime.Now();
    @TestVisible private transient List<Case> lstCaseDetails = new List<Case>(); 
	
	Public Static final String OBJECT_ACCOUNT_HUM = 'Account';
	 Public Static final String GROUP_RECORDTYPE_HUM = 'GROUP';
	 Public Static final String MEMBER_RECORDTYPE_HUM = 'Member'; 
	 Public Static final Integer NUMBER_CONSTANT_SEVENTHOUSAND_HUM = 7000;
	 Public Static final String BACKSLASH_HUM = '/';
	 
    /*
     * Method Name : PharmacyCaseHistoryComponent_C_HUM
     * Description : Constructor for the class , Intialize variables
     * Return type : NA
     * Paramater   : NA
    */    
    public PharmacyCaseHistoryComponent_C_HUM() 
    {
         try{            
               lstLogErrors = new List<Error_Log__c>();
               bpolCheck = false;
               benableCase = true;
               brecordCheck = false;
                             
               generateQueryString();
               verifyPolicyRecords();
            }
            catch(Exception e)
            {
               HUMExceptionHelper.bRestrictLog = true;
               lstLogErrors = HUMExceptionHelper.logErrors(e,'PharmacyCaseHistoryComponent_C_HUM','PharmacyCaseHistoryComponent_C_HUM');
            }
    }
    public String sCaseLinkID {get; set;}
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
                sCaseDetailsJSONreturn = JSON.serialize(lstCaseDetails); 
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
        String sQueryInit='Select Id,CaseNumber,Origin,Type,'+
        +'CTCI_List__c,CTCI_List__r.Classification__r.Name,CTCI_List__r.Intent__r.Name,CreatedBy.Name,'+
        +'Product__c,Interacting_With__r.Name,Interacting_With_Type__c,'+
        +'Interacting_About__c,Status,Priority,CreatedDate,ClosedDate,Created_By_Queue__c,HO_Dental_Case__c,HO_Medical_Case__c,General_Case__c from Case';
        Policy_Member__c polId ;
        if(ApexPages.CurrentPage().getParameters().containsKey('id'))
        {
            ID objURLID =  ApexPages.currentPage().getParameters().get('id');   
            polId = [Select Id, member__c From Policy_Member__c Where Id =: String.escapeSingleQuotes(objURLID) limit 1];
            if(objURLID != null || objURLID != '')
           {  
              objID = polId.member__c;
           }
        }

        if(objID!=null)
        { 
            if (objID.getSobjectType().getDescribe().getName() == OBJECT_ACCOUNT_HUM)
            {    
                sQueryInit = sQueryInit + ' Where AccountId =\''+ String.escapeSingleQuotes(objID) +'\'';
            } 
        }   
        return sQueryInit;
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
            ID objURLPOLID = ApexPages.currentPage().getParameters().get('id'); 
            polId = [Select Id, member__c From Policy_Member__c Where Id =: String.escapeSingleQuotes(objURLPOLID) limit 1];           
            if(objURLPOLID != null || objURLPOLID != '')
            {    
                objID = polId.member__c;//objURLPOLID;            
                if (objID.getSobjectType().getDescribe().getName() == OBJECT_ACCOUNT_HUM)
                {
                    Account oAcctRT = [SELECT id,RecordType.Name from Account where Id = :objID];
                    if(oAcctRT.RecordType.Name == GROUP_RECORDTYPE_HUM || Test.isRunningTest())
                    {
                       lstPolicy = [SELECT id,Name FROM Policy__c where Group_Name__c = :objID limit 1];
                       if(!lstPolicy.isEmpty()) brecordCheck = true;
                    }
                    else if(oAcctRT.RecordType.Name == MEMBER_RECORDTYPE_HUM || Test.isRunningTest())
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
        bShowWarning = false;
        String sSelectedCaseIds = ApexPages.CurrentPage().getParameters().get('sSelectedCases');        
        Boolean isSuccess = CaseLinkingLogic_S_HUM.linkCases(sSelectedCaseIds);       
        if(isSuccess)
        {
            bShowMessage = true;
            bShowWarning =false;
        }
        else
        {
            bShowMessage = false;
            bShowWarning =true;
        }
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
            lstCaseJunc = [Select  CreatedDate, Case__r.CaseNumber, Case__r.Id, Case_Master__r.Id, Case_Master__r.CaseNumber, Work_Queue__r.Work_Queue_Name__c, CreatedBy.LastName, CreatedBy.FirstName from Case_Junction__c where  IsLinked__c = true AND (Case__r.Id =: sCaseLinkId OR Case_Master__r.Id =: sCaseLinkId) Order By CreatedDate Desc,Case__r.CaseNumber ASC,Case_Master__r.CaseNumber ASC  limit :NUMBER_CONSTANT_SEVENTHOUSAND_HUM];   
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
                string sDate = objCJ.CreatedDate.month() + BACKSLASH_HUM + objCJ.CreatedDate.day() + BACKSLASH_HUM + objCJ.CreatedDate.year();
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