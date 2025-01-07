/******************************************************************************************************************************
Apex Class Name  : CaseHistoryComponent_C_RT_HUM
Version          : 1.0 
Created Date     : July 08 2016
Function         : Controller to CaseHistory_CMP_HUM VF component. It is used to display cases 
                   related to Account, Policy or Policy Member in salesforce in DataTable.                   
Test Class       : CaseHistoryComponent_T_RT_HUM
Modification Log :
 *  Developer              Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *  Pallavi Ravishankar       215883          11/04/2018            REQ - 323439  Converted case history datatable to reusable datatable as part of Resolution Project
 *  Anurag Shah               267634          07/20/2018            REQ - 371243 - Adding the �Queue Selection� fields to the Person Account/ Policy Member pages
 *  Prathyu Tushar            277354          08/23/2018            REQ - 371302   Added the Custom Permission to add the Error message based on Archival Cases
 *  Suresh Sunkari            277354          08/24/2018            REQ - 372716 - Added HasArchival Service on load to check User has any Archival cases or not
 *  Vijaya Koriprolu                          09/07/2018            REQ - 355997, REQ - 356001 and REQ - 357367 - Added code for Access Point Case History Section on Business account, policy and policy member.
 *  Asish Behera             314771           11/29/2018            REQ - 375087 - AHT- Add Last Modified By to Case History Section (CRM)
 * Prudhvi Pamarthi                           02/06/2019            REQ - 361155 - Not allowing case creation on Legacy Deleted policies
 * Prudhvi Pamarthi          356023           06/06/2019            REQ - 399192 - Fix for CA Ticket - 8193321 I want to create a case not associated to an available policy checkbox shouldnt be displayed.
 * Mithra Bharadwaj                           01/27/20202           REQ- 406769 - Display error message when trying to link cases when current queue is inactive
 * Santhi Mandava                             02/18/2020            US - 860272 - Added "DCN Present" column next to case number column
 * Santhi Mandava                             02/18/2020            US - 939206 - Removed RF check for "DCN Present" column next to status column
 * Anjani Vaddadi                             04/22/2020            US#- 1036783 - Archival switch using Custom meta data And DF541
 * Sunil Kumar Reddy                          04/24/2020            User Story 1042378 - PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup - 5/8 and 5/22 Release (CRM)
 * Amar Gadewar                               01/21/2021            User Story 1875233: PR00091574 - MF 1 - Quality - Highlight filters in the Case History section (RF)
 * Mithra Bharadwaj                                                 US#1522825 SonarQube Changes
 * Atia Uzma                                  04/01/2021            User Story 2111342: T1PRJ0001827 - MF 1 - Quality - Highlight filters in the Case History section (CRM)
 * Atia Uzma                                  04/28/2021            User Story 2230288: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 04/16/2021, 05/14/2021 and 05/28/2021 releases (CRM)
 * Pooja Kumbhar                              06/07/2021            User Story 2270747: T1PRJ0002606/PR00094268 - MF1 - Architectural Remediation - Case Management Global Constant Class Changes - Case History
 * Prasanthi Kandula                          06/15/2021            US 2274165: T1PRJ0001319 - (PR00084353) - CR1690 - MF # 4 -SF - TECH Story for Remove the references for Archival (CRM Archival Strategy Rollback)   
 * Pooja Kumbhar							   08/17/2021			2086211-T1PRJ0002606/PR00094268 - Heap Size - Optimize Class performance : CaseHistoryComponent 
 * Subhamay Mandal							  01/13/2022			User Story 2977653: T1PRJ0001827 - MF 1 - AHT - Ability to display count of linked cases in the Case History section (RF)
 * Subhamay Mandal							                   01/13/2022			   User Story 2977653: T1PRJ0001827 - MF 1 - AHT - Ability to display count of linked cases in the Case History section (RF)
 * Pooja Kumbhar						            01/19/2022			User Story - 2701225 : T1PRJ0272555 - INC1495332 - Developer script exception from Humana : JSON : JSON string exceeds heap size limit
 * Akash Surwase                              08/17/2022            USER STORY 2819667 : T1PRJ0100059- MF 3 FR 10 - Case Creation (HP)
*  Ankima/Isha		                          07/18/2022             Changes related to Archived Case Switch
*******************************************************************************************************************************/
public with sharing Class CaseHistoryComponent_C_RT_HUM 
{
    @TestVisible private String sQuery ='';
    @TestVisible private transient String sCaseDetailsJSONreturn = '';
    @TestVisible private DateTime dtStart = DateTime.Now();
    @TestVisible private DateTime dtEnd = DateTime.Now();
    @TestVisible private transient List<Case> lstCaseDetails = new List<Case>(); 
    @TestVisible private transient List<CaseHistoryComponent_DTO_RT_HUM> lstCaseWrap = New List<CaseHistoryComponent_DTO_RT_HUM>();
    @TestVisible private String sRequest ='';
    @TestVisible private boolean bCallService = false;
    public static final string ID_HUM = 'id';
    public static final string OBJECT_ACCOUNT_HUM = 'Account';
    public static final string OBJECT_POLICYMEMBER_HUM = 'Policy_Member__c';
    public static final string OBJECT_POLICY_HUM = 'Policy__C';
	public ID AccountID {get;set;}
    public Boolean bCountCases{get;set;}
    public Boolean Switch_2701225 {get;set;}
    Public Static final String PHARMACYSPECIALIST_PROFILE_HUM = 'Humana Pharmacy Specialist';
    public boolean showArchivedCase {get;set;}
    public String parentObject {get;set;}
    
    
    /*
     * Method Name : CaseHistoryComponent_C_RT_HUM
     * Description : Constructor for the class , Intialize variables
     * Return type : NA
     * Paramater   : NA
    */    
    public CaseHistoryComponent_C_RT_HUM() 
    {
         try{            
               lstLogErrors = new List<Error_Log__c>();
               bpolCheck = false;
               benableCase = true;
               brecordCheck = false;
               bPolicyMemberPageCheck = false;
               bAccountPageCheck = false;
               blegacyDeleteCheck = False; 
               agnosticOnOff = false;
               agnosticRecType = false;            
               sObjectID = (ID) ApexPages.currentPage().getParameters().Get(ID_HUM);
                showArchivedCase = HUMUtilityHelper.isCRMFunctionalityON('3579023');
                 if(showArchivedCase == true){
                     if (Id.valueOf(sObjectID).getSobjectType().getDescribe().getName() == GLOBAL_CONSTANT_HUM.OBJECT_POLICY_HUM ){ 
                      showArchivedCase=false;   
                     }
                 }
                parentObject = Id.valueOf(sObjectID).getSobjectType().getDescribe().getName();
               this.bShowLinkedCaseCount = (HUMUtilityHelper.isCRMFunctionalityON('2977653') && UtilityHelper_H_AHT_HUM.isRapidForceUser());
			   
               Switch_2701225 = false;
			   Switch_2701225 = HUMUtilityHelper.isCRMFunctionalityON('2701225'); 
               if(Switch_2701225)
               {
               CountOfCases();
               }
               generateQueryString();
               verifyPolicyRecords();   
            }
            catch(Exception e)
            {
               HUMExceptionHelper.bRestrictLog = true;
               lstLogErrors = HUMExceptionHelper.logErrors(e,'CaseHistoryComponent_C_RT_HUM','CaseHistoryComponent_C_RT_HUM');
            }
            
         
            columnHeader = json.serialize(new List < String > {'Id', 'Link','Case Number','Date/Time Opened','Date/Time Closed','Classification','Intent','Product','Status','DCN Present', 'Case Origin','Type','Interacting With','Interacting With Type','Interacting About','Priority','Created By','Created By Queue','HO Dental','HO Medical','General','Last Modified By'});
            columnName = json.serialize(new List < String > {'Id', 'sCaseIdVal','sCaseNum','sCreatedDate','sClosedDate','sClassification','sIntent','sProduct','sStatus','sDCN','sOrigin','sType','sInteractingWith','sInteractingWithType','sInteractingAbout','sPriority','sCreatedBy','sCreatedByQueue','bDentalCase','bMedicalcase','bGeneralCase','sLastModifiedBy'});
            
            
            bIsPDPPilotUser = Pharmacy_H_HUM.isPDPPilotUser();
      
     }
    Public string columnName {get; private set;}
    Public string columnHeader{get; private set;}  
    Public string columnHeaderTitle{get; private set;}
    Public String sCaseLinkID {get; set;}
    public String sRowNumber {get; set;}
    public String sCaseRowLinkID {get;set;}
    public String sObjectName{get; set;}
    public Id sCaseID {get; set;}
    public Id sObjectID {get; set;}
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
    public Boolean bPolicyMemberPageCheck {get;set;}
    public Boolean bAccountPageCheck {get;set;}
    public List<Policy__c> lstPolicy{get; set;}
    public List<Policy_Member__c> lstPolicyMember{get; set;}      
    public List<Case_Junction__c> lstCaseJunc {get;set;}
    public List<LinkCase_Wrapper_DTO_HUM> lstCaseLink {get;set;}  
    public String sLinkJson {get;set;}
    public String newCaseId {get; set;}
    public Boolean bIsPDPPilotUser{get; set;}
    public Boolean blegacyDeleteCheck {get; set;}
    public Boolean bInvalidQueueWarning{get;set;}
    public Boolean bShowLinkedCaseCount{get; set;}
    public String sLinkedCaseCountJSON{get;set;} 
    public static boolean agnosticOnOff {get; set;}
    public static boolean agnosticRecType {get; set;}
    public static boolean agnosticOnOffSwitch {get; set;}   
    
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
           if(String.isNotBlank(sQuery) && sQuery!='')
            {   
                for(case oCase: Database.Query(sQuery))
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
                  oCaseWrap.sLastModifiedBy = oCase.LastModifiedBy.Name;
                  if(this.bShowLinkedCaseCount) {
                    oCaseWrap.sLinkedCaseCount = oCase.Case_Junction1__r.size() + oCase.Case_Junction__r.size();
                  }
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
     * Method Name : CountOfCases
     * Description : Aggregate query on case to get the total cases count on account
     * Return type : void
     * Parameter   : NA
     */
    private void CountOfCases()   
    {
         AggregateResult[] groupedcasesCount;
         Decimal DECcaseCount;
         Integer IntcaseCount; 
      
          
        if(ApexPages.CurrentPage().getParameters().containsKey('id'))
        {
           ID objURLID = ApexPages.currentPage().getParameters().get('id');            
            if(objURLID != null || objURLID != '')
            {    
                objID = objURLID;
            }
        }

        if(objID!=null)
        { 
            if (objID.getSobjectType().getDescribe().getName() == OBJECT_ACCOUNT_HUM)
            { 
                groupedcasesCount = [select count(id) from case Where AccountId =: objID];
                  for (AggregateResult ar : groupedcasesCount )  {
                      DECcaseCount = (Decimal) ar.get('expr0');
                      IntcaseCount = DECcaseCount.intValue();
                            if (IntcaseCount > 5000)
                            {
                                    bCountCases = true;
                            }
                            else
                            {
                                    bCountCases = false;
                            }
                        } 
                    
        }
        else
        {
               bCountCases = false;
        }
        
        }
        
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
        +'CTCI_List__r.Classification__r.Name,CreatedBy.Name,CTCI_List__r.Intent__r.Name,'+
        +'Product__c,Interacting_With__r.Name,Interacting_With_Type__c,'+
        +'Interacting_About__c,Status,Priority,CreatedDate,ClosedDate,Created_By_Queue__c,HO_Dental_Case__c,HO_Medical_Case__c,General_Case__c,LastModifiedBy.Name from Case';

        if(this.bShowLinkedCaseCount) {
            sQueryInit='Select Id,CaseNumber,DCN__c,Origin,Type,'+
            +'CTCI_List__r.Classification__r.Name,CreatedBy.Name,CTCI_List__r.Intent__r.Name,'+
            +'Product__c,Interacting_With__r.Name,Interacting_With_Type__c,'+
            +'Interacting_About__c,Status,Priority,CreatedDate,ClosedDate,Created_By_Queue__c,HO_Dental_Case__c,HO_Medical_Case__c,General_Case__c,LastModifiedBy.Name,'+
            +'(select id from Case_Junction1__r where IsLinked__c=true), (select id from Case_Junction__r where IsLinked__c=true) '+
            +'from Case';
        }
        if(ApexPages.CurrentPage().getParameters().containsKey('id'))
        {
            ID objURLID = ApexPages.currentPage().getParameters().get('id');            
            if(objURLID != null || objURLID != '')
            {    
                objID = objURLID;
            }
        }

        if(objID!=null)
        { 
            if (objID.getSobjectType().getDescribe().getName() == OBJECT_ACCOUNT_HUM)
            {    
                sQueryInit = sQueryInit + ' Where AccountId =\''+String.escapeSingleQuotes(objID)+'\'';
                bAccountPageCheck = true;
				AccountID = objID;
            }  
            else if (objID.getSobjectType().getDescribe().getName() == OBJECT_POLICYMEMBER_HUM ) 
            {
                sQueryInit = sQueryInit + ' Where Policy_Member__c =\''+String.escapeSingleQuotes(objID)+'\'';
                bPolicyMemberPageCheck = true;
            }
            else if (objID.getSobjectType().getDescribe().getName() == OBJECT_POLICY_HUM) 
            {
                sQueryInit = sQueryInit + ' Where Policy__c =\''+String.escapeSingleQuotes(objID)+'\'';
            }
           sQueryInit = sQueryInit + ' order by CreatedDate Desc';
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
       agnosticOnOffSwitch=false;
       agnosticOnOffswitch= HUMUtilityHelper.isCRMFunctionalityON('2860677');       
       if(ApexPages.CurrentPage().getParameters().containsKey('id'))
        {             
            ID objURLPOLID = ApexPages.currentPage().getParameters().get('id');            
            if(objURLPOLID != null || objURLPOLID != '')
            {    
                objID = objURLPOLID;            
                if (objID.getSobjectType().getDescribe().getName() == OBJECT_ACCOUNT_HUM)
                {
                    Account oAcctRT = [SELECT id,RecordType.Name,ETL_Record_Deleted__c from Account where Id = :objID];
                    if(oAcctRT.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
                    if(oAcctRT.RecordType.Name == CaseManagement_Constants_HUM.GROUP_RECORDTYPE_HUM )
                    {
                       lstPolicy = [SELECT id,Name FROM Policy__c where Group_Name__c = :objID limit 1];
                       if(!lstPolicy.isEmpty()) brecordCheck = true;
                    }
                    else if(oAcctRT.RecordType.Name == CaseManagement_Constants_HUM.MEMBER_RECORDTYPE_HUM)
                    {
                       for(policy_member__c polMemb : [SELECT id,Name,ETL_Record_Deleted__c,(SELECT Id,Subject FROM cases__r limit 1) FROM Policy_Member__c where Member__c = :objID])
                         {
                             if(polMemb.ETL_Record_Deleted__c)
                             {
                                 List<Case> lstCase = new List<case>();
                                 lstCase = polMemb.Cases__R;
                                 if(!lstCase.isEmpty())
                                 {
                                     lstPolicyMember.add(polMemb);
                                 }
                             }
                             else
                             {
                                 lstPolicyMember.add(polMemb);
                             }
                         }
                       if(!lstPolicyMember.isEmpty()) brecordCheck = true;
                    
                    }
                    else if((oAcctRT.RecordType.Name == ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER)&&(agnosticOnOffswitch))
                    {
                      for(policy_member__c lstPolicyMember1:[SELECT id,Name,ETL_Record_Deleted__c,product__c FROM Policy_Member__c where Member__c= :objID])
                        {
                          if(lstPolicyMember1.Product__c=='TRA' && lstPolicyMember1!=null)
                            lstPolicyMember.add(lstPolicyMember1);
                          else
                            lstPolicyMember.add(lstPolicyMember1);
                        }
                      if(!lstPolicyMember.isEmpty())
                        {
                            brecordCheck=true;
                        }
                        agnosticOnOff = false;
                        agnosticRecType = true;                                                
                        agnosticOnOff = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name == PHARMACYSPECIALIST_PROFILE_HUM ? true : false;
                    }
                }   
                if (objID.getSobjectType().getDescribe().getName() == OBJECT_POLICYMEMBER_HUM)
                {
                    Policy_Member__c oPolMem = [SELECT id,ETL_Record_Deleted__c from Policy_Member__c where Id = :objID];
                    if(oPolMem.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
                }
            }
        }
    }
    
    /*
     * Method Name : enableCaseButton
     * Description : enable/disable case button depending on the policies for group account
     * Return type : Void
     * Parameter   : NA
    */    
    public void enableCaseButton()
    {
       benablecase =!bpolcheck;

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
		else if(bAccountPageCheck && Switch_2701225)
        {
            sQuery = sQuery + ' Limit 5000 ';
        }		
    }  
            
    /*
     * Method Name : createCaseAndRedirect
     * Description : Call Case creation helper class to create new case
     * Return type : void
     * Parameter   : NA
    */    
    public void createCaseAndRedirect()
    {
        try
        {
            HUMNewCaseRedirectExtension caseRedExt = new HUMNewCaseRedirectExtension(NULL);
            caseRedExt.objectID = objID;
            caseRedExt.sObjectName = objID.getSobjectType().getDescribe().getName();
            caseRedExt.sStandardUrlForTab = ApexPages.CurrentPage().getParameters().get('standardURL');
            caseRedExt.funcOnPageLoad();
            caseRedExt.saveCase();
            sCaseID = caseRedExt.caseID;
            newCaseId = caseRedExt.caseID;
            sCaseNumber = caseRedExt.sCaseNumber;
            sRedirectURL = caseRedExt.sRedirectURL;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseHistoryComponent_C_RT_HUM','createCaseAndRedirect');
        }
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
        bInvalidQueueWarning = false;
        String sSelectedCaseIds = ApexPages.CurrentPage().getParameters().get('sSelectedCases').escapehtml4();
        CaseLinkingLogic_S_HUM.bShowLinkedCaseCount = this.bShowLinkedCaseCount;
        Boolean isSuccess = CaseLinkingLogic_S_HUM.linkCases(sSelectedCaseIds);
        if(this.bShowLinkedCaseCount) {
            sLinkedCaseCountJSON = JSON.serialize(CaseLinkingLogic_S_HUM.mapLinkedCaseCount);
        }
        User objUser = [Select Id, Current_Queue__c from User where Id =: UserInfo.getUserId()];
        List<Work_Queue_Setup__C> lstWQS = [Select Id from Work_Queue_Setup__C where Name =: objUser.Current_Queue__c AND IsActive__c = TRUE];  
       if(isSuccess && !lstWQS.isEmpty() )
        {
            bShowMessage = true;
            bShowWarning =false;
        }
        
        else if (!isSuccess && !lstWQS.isEmpty() )
        {
            bShowMessage = false;
            bShowWarning =true;
        }
        
        else 
        {
            bShowMessage = false;
            bShowWarning =false;
            bInvalidQueueWarning=true;
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
            lstCaseJunc = [Select  CreatedDate, Case__r.CaseNumber, Case__r.Id, Case_Master__r.Id, Case_Master__r.CaseNumber, Work_Queue__r.Work_Queue_Name__c, CreatedBy.LastName, CreatedBy.FirstName from Case_Junction__c where  IsLinked__c = true AND (Case__r.Id =: sCaseLinkId OR Case_Master__r.Id =: sCaseLinkId) Order By CreatedDate Desc,Case__r.CaseNumber ASC,Case_Master__r.CaseNumber ASC  limit :CaseManagement_Constants_HUM.NUMBER_CONSTANT_SEVENTHOUSAND_HUM];   
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
                string sDate = objCJ.CreatedDate.month() + GlobalCommonConstants_HUM.BACKSLASH_HUM + objCJ.CreatedDate.day() + GlobalCommonConstants_HUM.BACKSLASH_HUM + objCJ.CreatedDate.year();
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