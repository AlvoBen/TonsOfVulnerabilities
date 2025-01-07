/**********************************************************************************************************************************
Apex class Name    : HUMCaseCloseErrorPageExtension
Version            : 1.0
Function           : Extension to VF Page:: HUMCaseCloseErrorPage.
                     This class is used to show Error message and list of open work tasks
                     to end user when user tries to close a Case with open tasks
Created Date       : 25 March 2015
Test Class         : HUMCaseCloseErrorPageExtensionTest

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------ 
 *   SuryaKumari Medicherla        22085              03/27/2015          Original Version
 *   SuryaKumari Medicherla        22311              04/13/2015          Code Modified to fix Defect: 179525
 *   SuryaKumari Medicherla        25100              07/29/2015          Code Modified to fix CA Ticket: 5249265(Customer is not able to close an open case)
 *   Asha Deepika Koyyana          25100              08/17/2015          Code review fixes for CC #25100  
 *   Avinash Kumar Choubey         29427              01/21/2016          Modified to set page name in page reference (line no 94, 133)
 *   Avinash Kumar Choubey         30267              03/03/2016          Modified to fix defect# 217905 (line no 46 to 54)   
 *   Subhamay Mandal                                  05/11/2017          REQ - 313447 Close Case button in Case detail section should close the open case (CRM)
 *   Pradeepkumar Dani            102268              06/16/2017          Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
 *   Pallavi Ravishankar          122650              08/16/2017          Defect Fix 311425.0001 - REQ 322573
 *   Santhi Mandava                                   03/27/2018          Removed ciphercloud decryption code from getCCMedicareCalls method
 *   Pallavi Ravishankar          268909              07/30/2018          REQ 361296 -check for openwork tasks of type ='Extension Requests' addition
 *   Santhi Mandava                                   08/17/2018          Fixed CA ticket 7651703
 *   Subhamay Mandal								  07/05/2019		  REQ - 403789 AHT - All Users - Do not close case tab on closing Case through Case Detail page (RF)
 *   Dan Laratta                                      02/18/2021          User Story 1874745: PR00091574 - MF 1 - AHT - Do not close case tab on closing Case through Case Detail page (CRM)
 *   Atia Uzma                                        02/19/2021          User Story 1956960: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 11/20/2020 , 02/12/2021, 03/05/2021 and 3/19/2021 releases (CRM)
 *   Mithra Bharadwaj                                                     US#1522825 SonarQube Changes
 *   Mohammed Noor                                    08/25/2021          US2060135 - INC0932986 - Fix OGO Case Autorouting issue.
************************************************************************************************************************************   
*/

public with sharing class HUMCaseCloseErrorPageExtension 
{
    public boolean bMedicareCalls{get;set;}
    public Boolean checkMedicareCustomerServiceAccess{get;set;}   
    public boolean bCheckMedicareCCPermission{get;set;}   
    public Policy__c policy{get;set;}
    private Case oCaseRec = NULL;
    private ApexPages.standardController oCaseStdController = null;
    private Integer iZero = HUMConstants.ZERO;
    private String strSlash = HUMConstants.HUMSingleSlash;
    private String strCloseCareURL = HUMConstants.HUMCaseCloseReturnURL;    
    private String strEntURL = HUMConstants.HUMEnt;
    private String strNoOverride = HUMConstants.HUMNoOverrideEqualsOne;
    private String strCaseStatus ;
    public string sMedCallsIssueCC{get;set;}
    public string sMedCallsResCC{get;set;}
    public boolean bMediClose{get;set;}    
    public MedicareCallsCaseComments_H_HUM oMedCalls = new MedicareCallsCaseComments_H_HUM();   
    
    /*
     * Method Name : HUMCaseCloseErrorPageExtension
     * Description : Constructor for the class , initialize variable
     * Return type : N/A
     * Paramater   : ApexPages.StandardController
     */
    public HUMCaseCloseErrorPageExtension(ApexPages.StandardController oController) 
    {
        bRenderTaskFlag = false;
        bMedicareCalls = false;
        bCheckMedicareCCPermission = false;
        oCaseStdController = oController;
        if (!Test.isRunningTest()) oController.addFields(new list<string>{'CaseNumber', 'Status', 'CTCI_List__c', 'Classification_Id__c', 'Intent_Id__c', 'Complaint__c', 'G_A_Rights_Given__c', 'Policy_Member__c', 'Policy_Member__r.Policy__c', 'RecordTypeId','Origin','Autoroute_Status__c','Interacting_With_Type__c'});
        oCaseRec = (Case)oController.getRecord();
        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, HUMConstants.HUM_CASEEDIT_CASECLOSEERRORPAGEMSG));
        strCaseStatus= ApexPages.currentPage().getParameters().get(HUMConstants.HUM_CASEEDIT_STATUS);
        if(strCaseStatus != null && strCaseStatus == HUMConstants.HUM_CASEEDIT_CANCELLED)
        {
            strCaseStatus= HUMConstants.HUM_CASEEDIT_CANCELLEDSTATUS;
        }
        else
        {
            strCaseStatus= HUMConstants.HUM_CASEEDIT_CLOSEDSTATUS;
        }
        getUpdatedTasks();
    }
    
    public transient List<Task> lstOpenTasks{get; set;}
    public String sParentID {get;set;}
    public String sParentName {get;set;}
    public Boolean bRenderTaskFlag {get;set;}
    
    /*
     * Method Name : listErrorLogs
     * Description : Capture Any DML Exceptions in errorLog object
     * Return Type : List<Error_Log__c> 
     * Parameters  : NA
     */
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null)    return new List<Error_Log__c>();
            return listErrorLogs;
        }
        set;
    }
    
    /*
     * Method Name : returnToCase
     * Description : Move to Case record detail page when cancel is clicked
     * Return type : Pagereference
     * Paramater   : NA
     */
    public pagereference returnToCase()
    {
        return oCaseStdController.cancel();
    }
    
    /*
     * Method Name : CaseClose
     * Description : Redirects to standard or custom close page based on open work task count of Case
     * Return type : Pagereference
     * Paramater   : NA
     */
    public pagereference CaseClose()
    {
        checkCCMedicareCalls();               
        PageReference pageRef = null;       
        List<Task> lastOpenTasks= [Select Id From Task Where (Type = :HUMConstants.HUMWorkTask or Type = :HUMConstants.HUMExtensionTask) and Status != :HUMConstants.HUMClosed and whatId = :oCaseRec.id];      
        sParentName = oCaseRec.CaseNumber;
        if(lastOpenTasks.isEmpty()) {
            try {   
                if(null != oCaseRec && strCaseStatus.equals(HUMConstants.HUM_CASEEDIT_CLOSEDSTATUS) && this.oCaseRec.Classification_Id__c != null && this.oCaseRec.Intent_Id__c != null && this.oCaseRec.Complaint__c != null && this.oCaseRec.G_A_Rights_Given__c != null  && bMediClose==true) {
                    this.oCaseRec.status = HUMConstants.HUM_CASEEDIT_CLOSED;
                    if(HUMUtilityHelper.isCRMFunctionalityON('2060135'))    { checkForOGOCase(); }
                    update this.oCaseRec;
                        pageRef = new PageReference(strSlash + oCaseRec.id);                                      
                } else {
                    pageRef = new PageReference(strSlash + 'apex/' + HUMConstants.HUM_CASEEDIT_EDITPAGENAME + '?Id=' + oCaseRec.id + strCaseStatus + strCloseCareURL + oCaseRec.id + strEntURL + strNoOverride);
                }
            }
            catch(Exception ex) {
                pageRef = new PageReference(strSlash + 'apex/' + HUMConstants.HUM_CASEEDIT_EDITPAGENAME + '?Id=' + oCaseRec.id + strCaseStatus + strCloseCareURL + oCaseRec.id + strEntURL + strNoOverride);
                HUMExceptionHelper.logErrors(ex, 'HUMCaseCloseErrorPageExtension', 'CaseClose');
            }                       
        } 
        return pageRef;
    }
    
    /*
     * Method Name : getLstTasks
     * Description : Returns the list of open tasks available to that case 
     * Return Type : List<Task> 
     * Parameters  : NA
     */
    public List<Task> getLstTasks()
    {
        getUpdatedTasks();
        if(lstOpenTasks != NULL && lstOpenTasks.size() > iZero)    return lstOpenTasks;
        return new List<Task>();
    }
    
    /*
     * Method Name : CheckForOpenTaskCountCount
     * Description : Queries the list of open work tasks available to that case and returns to stndard or 
                     custom case close page after clicking save button based on open work task count.
     * Return Type : PageReference  
     * Parameters  : NA
     */
    public PageReference CheckForOpenTaskCountCount()
    {
        getUpdatedTasks();
        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, HUMConstants.HUM_CASEEDIT_CASECLOSEERRORPAGEMSG));
        if(lstOpenTasks != NULL && lstOpenTasks.size() <= iZero)
        {
          return new PageReference(strSlash + 'apex/' + HUMConstants.HUM_CASEEDIT_EDITPAGENAME + '?Id=' + oCaseRec.id + strCaseStatus + strCloseCareURL + oCaseRec.id + strEntURL + strNoOverride);  
        }
        return NULL;
    }
    
    /*
     * Method Name   :    getUpdatedTasks
     * Description   :    Query for the Tasks records depending on the Parent ID
     * Return Type   :    void
     * Parameters    :    NA
     */
    private void getUpdatedTasks() 
    {
        if(String.isBLANK(sParentID))
        {
            if( ApexPages.currentPage().getParameters().containsKey(HUMConstants.HUMId))    sParentID=ApexPages.currentPage().getParameters().get(HUMConstants.HUMId);
        }
        if(!String.ISBLANK(sParentID))
        {
            lstOpenTasks = [Select id,Subject,Task_Number__c,Type,CreatedDate, WhoId, ActivityDate,Status,
                                                                    Priority,Date_Time_Closed__c,Last_Modified_Age__c,Task_Owner__c,
                                                                    RefCase_Queue__c,Age_Since_Created__c,CreatedBy.Name,Created_By_Queue__c, Owner_Queue__c from Task 
                                                                    where Whatid = :(ID)sParentID AND (Type = :HUMConstants.HUMWorkTask or Type = :HUMConstants.HUMExtensionTask) and Status != :HUMConstants.HUMClosed
                                                                    order by LastmodifiedDate];
            
            if(lstOpenTasks.size() <= iZero)
            {
                bRenderTaskFlag = false; 
            }
            else
            {
                bRenderTaskFlag = true;
            }
        }
    }
    
   /*
    * This method is to check the permission set and Case origin for Medicare Calls Part C or Part D
    * @param  NA
    * @return null
    */
    public void validateMedicareCustomerServiceAccess()
    {
        try
        {
            String UserId = Userinfo.getUserId();
            PermissionSetAssignment pCheck =[SELECT Id FROM PermissionSetAssignment WHERE PermissionSet.Name =:GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM AND AssigneeId =:userId];
            if(pCheck != null && oCaseRec.Origin == GLOBAL_CONSTANT_HUM.MEDICARECALLS_CASEORIGIN_HUM)
            {
                checkMedicareCustomerServiceAccess = true;
            }
            else{checkMedicareCustomerServiceAccess = false;}
        }
        catch(Exception e)
        {
            checkMedicareCustomerServiceAccess = false;
        }
    
    }
    
    /*
     * This method to check if this is a Medcare call case
     * @param    NA
     * @return   void
     */
    public void checkCCMedicareCalls()
     {
      bMediClose = true;
      String caseRecordTypeName = Schema.SObjectType.Case.getRecordTypeInfosById().get(oCaseRec.RecordTypeId).getName();
       if (ocaseRec.Policy_Member__r.Policy__c!= null)
        {
          this.policy = [SELECT Major_Lob__c,Policy__c.Product__c ,Policy__c.Product__r.Product_Type__c, Policy__c.Product__r.Segment_Type__c,Policy__c.Product__r.ProductCode from Policy__c WHERE id =:ocaseRec.Policy_Member__r.Policy__c LIMIT 1];   
        }
       checkMedicareCustomerServiceAccess = false;
       validateMedicareCustomerServiceAccess();
       if(caseRecordTypeName != GLOBAL_CONSTANT_HUM.MEDICARE_CASE && caseRecordTypeName != GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE)
        {    
         if (ocaseRec.Policy_Member__c != null && String.isNotBlank(ocaseRec.Policy_Member__c) && checkMedicareCustomerServiceAccess)
          { 
            bMedicareCalls = oMedCalls.checkMedicareCare(ocaseRec.Origin, policy.Product__r.ProductCode, policy.Major_Lob__c, policy.Product__r.Product_Type__c, policy.Product__r.Segment_Type__c);
            list<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :UserInfo.getUserId() AND PermissionSet.Name = : GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM];
            if(lstPMAssign != null && !lstPMAssign.isEmpty()) bCheckMedicareCCPermission = true;
            if(bMedicareCalls == true && bCheckMedicareCCPermission == true) getCCMedicareCalls();
          }
        }     
     }
     
   /*
    * This method to get case comments in case of Medicare Calls    
    * @param    NA
    * @return   void
    */
    public void getCCMedicareCalls()
    {   
      Integer iIssueLength=0;
      Integer iResLength=0;
      map<Id, CaseComment> mapMediCC = new map<Id, CaseComment>([SELECT Id, CommentBody FROM CaseComment WHERE ParentId =: oCaseRec.Id]);
      list<CaseCommentType_Junction__c> lstCCTJunc = [SELECT Id, Name,CaseCommentType__r.Description__c,CaseCommentType__r.Name, CaseComment__c FROM CaseCommentType_Junction__c WHERE CaseComment__c IN : mapMediCC.keyset() order by createdDate ASC];
       if(lstCCTJunc != null && !lstCCTJunc.isEmpty())
        {        
            for(CaseCommentType_Junction__c oCCTJ : lstCCTJunc)
            {
                if(oCCTJ.CaseCommentType__r.Name == GLOBAL_CONSTANT_HUM.MEDICARECALLS_ISSUE_HUM && mapMediCC.containsKey(Id.valueOf(oCCTJ.CaseComment__c)))
                {
                	sMedCallsIssueCC = mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).CommentBody;                  
                }
                if(oCCTJ.CaseCommentType__r.Name == GLOBAL_CONSTANT_HUM.MEDICARECALLS_RESO_HUM && mapMediCC.containsKey(Id.valueOf(oCCTJ.CaseComment__c)))
                {
                	sMedCallsResCC = mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).CommentBody;
                }
            }             
          If(String.IsNotBlank(sMedCallsIssueCC)) iIssueLength =  sMedCallsIssueCC.length();
          If(String.IsNotBlank(sMedCallsResCC)) iResLength =  sMedCallsResCC.length();  
          
          if(iIssueLength>= 10 && iIssueLength <= 2000 && iResLength >= 10 && iResLength <= 2000) bMediClose = true;
          else bMediClose = false;
        }
        else bMediClose = false;
    }

    /*
    * This method is to check if the Case's autoroute status is Blank and Case is OGO Autoroute eligible 
    * and then set autoroute status to Evalute.
    * @param    NA
    * @return   void
    */
    public void checkForOGOCase()
    {
        set<String> setOGOComplaint = new set<String>{GLOBAL_CONSTANT_HUM.MEDICAID, GLOBAL_CONSTANT_HUM.MEDICAREPART_C, GLOBAL_CONSTANT_HUM.MEDICAREPART_D};
        set<String> setOGOIntWithType = new set<String>{GLOBAL_CONSTANT_HUM.MEMBER,GLOBAL_CONSTANT_HUM.CASEEDIT_INT_ABOUT_TYPE_UNKNOWN_MEMBER,GLOBAL_CONSTANT_HUM.MEMBERCAREGIVER,GLOBAL_CONSTANT_HUM.sInternalAccountType,'Government','Care Manager'};
        set<String> setAutorouteTemplates = new set<String>{'95C90830-3133','BC929CD6-E06A',GLOBAL_CONSTANT_HUM.GOODCAUSEUNIQUEID,GLOBAL_CONSTANT_HUM.PCPTEMPLATEUNIQUEID,'deeef952-9f63-49ce-97d0-c7eb3204b519',GLOBAL_CONSTANT_HUM.ATTESTATION_TEMPLATE_NAME};
        set<String> setNotOGORecType = new set<String>();
        setNotOGORecType.addAll(GLOBAL_CONSTANT_HUM.Pharmacy_Record_Type_Name);
        setNotOGORecType.add(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_UNKNOWNCASE_HUM);
        setNotOGORecType.add(GLOBAL_CONSTANT_HUM.HUM_CLOSEDUNKNOWNCASE);

        String sCaseRecType = Schema.SObjectType.Case.getRecordTypeInfosById().get(oCaseRec.RecordTypeId).getName();
        if(String.isBlank(oCaseRec.Autoroute_Status__c) && String.isNotBlank(oCaseRec.Complaint__c) && setOGOComplaint.contains(oCaseRec.Complaint__c)
           && String.isNotBlank(sCaseRecType) && !setNotOGORecType.contains(sCaseRecType) && String.isNotBlank(oCaseRec.Interacting_With_Type__c) && setOGOIntWithType.contains(oCaseRec.Interacting_With_Type__c))
        {
            boolean bAutoroute = true;
            //check if no Process is attached to the Case
            list<Template_Submission__c> lstSubmission = [SELECT Id, Template__r.Unique_Template_Name__c FROM Template_Submission__c WHERE Submission_Owner__r.Object_Owner_ID__c = :oCaseRec.Id AND Template__r.Unique_Template_Name__c IN :setAutorouteTemplates];
            if(lstSubmission!=null && !lstSubmission.isEmpty())
            {
                bAutoroute = false;
            }
            else 
            {
                list<Case_Action_Association__c> lstCaseActionAssociation = [SELECT Id, Name, Case__r.CaseNUmber, Case_Action__r.name, Action__c FROM Case_Action_Association__c where Case__c = :oCaseRec.Id and Case_Action__r.Name = 'QAA'];
                if(lstCaseActionAssociation!=null && !lstCaseActionAssociation.isEmpty())   bAutoroute = false;
            }
            if(bAutoroute && Schema.SObjectType.Case.fields.Autoroute_Status__c.isUpdateable())  oCaseRec.Autoroute_Status__c = GLOBAL_CONSTANT_HUM.AUTOROUTING_EVALUATE;
        }
    }
}