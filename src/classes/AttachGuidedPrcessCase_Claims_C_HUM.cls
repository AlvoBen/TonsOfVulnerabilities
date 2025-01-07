/**********************************************************************************************************
Apex Class Name  : AttachGuidedPrcessCase_Claims_C_HUM
Version          : 1.0
Created Date     : 25/10/2018
Function         : This is controller class for visual force component'AttachGuidedPrcessCase_Claims_C_HUM '.
                   This is used to attach Guided Process to either new case or to an existing case.
Test Class       : AttachGuidedProcessCase_Claims_T_HUM
***********************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------
* Shivam Anand                                            11/05/2018            REQ - 371426- Logging for Member Claims
* Vijaya K                                                11/05/2018            REQ - 371427- Logging for Provide claims
* Asish Behera				        		              12/31/2018			REQ - 382703 - User - Ability to prevent attaching multiple processes to cases (CRM)
* Prasanthi Kandula                                       04/16/2020            US 1003374 - To display send print page when redirected from RTI 
* Himalay Patel                                      	  10/14/2020            REQ - 1145316  Display Completed 'Humana Pharmacy Web Issues' Process on Case Detail Page (RxRF) 
* Sunil Kumar Reddy                                       01/27/2021            REQ - 1763850  Display Completed 'Medicaid PAR Provider Not Accepting' Process on Case Detail Page (CRM) 
* Prasanthi Kandula                                       02/01/2021            US-1669846 SonarQube critical security fixes  
* Suraj Patil								   			  04/09/2021			REQ - 1905877 - PR00094226 - CR AEP readiness - Low Security Issues 
* Jayeshkumar Solanki					  03/22/2021		User Story 2187337: T1PRJ0001827 - MF 1 - Quality - "Medicare CE and Sp Election DE Process" Template Implementation (RF)
* Suraj Patil								   			  04/20/2021			REQ - 1871982 - PR00094226 - CR AEP readiness - Low Security Issues 
* AshokKumar Nutalapati                         	 	  01/10/2022            REQ - 2843422 T1PRJ0002517 - MF 1 - Inactivate Rx: Process Content (RxRF) (ID# 163)
*************************************************************************************************************/
public with sharing class AttachGuidedPrcessCase_Claims_C_HUM extends LogDetails_A_HUM
{
    @testVisible private Id accId = NULL;
    @testVisible private String sObjName ='';
    @testVisible private List<Case> lstFinalCases = null;
    @testVisible private String sSortExp = GLOBAL_CONSTANT_HUM.SORTDATE_HUM;
    @testVisible private String sSortDirection = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
    @testVisible private String sAttachmentKeyName = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMATTACHMENTLOGFORDAC_HUM;
    @testVisible private Case oCaseRec = new Case();
    @testVisible private Id idAttLogId = NULL;
    @testVisible private List<Case_Action_Association__c> LstcasAct = new List<Case_Action_Association__c>();    
    @testVisible private List<Attachment_Log__c> lstAttachmentLog = new List<Attachment_Log__c>();    
    public Boolean bCasesProcessAttached{get;set;}
    public List<Template_Submission__c> lstTemplate = new List<Template_Submission__c>();
    Claim_Send_Statement_C_HUM oClaims = new Claim_Send_Statement_C_HUM();
    
    /*
    * Method Name : AttachGuidedPrcessCase_Claims_C_HUM 
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : NA
    */
    public AttachGuidedPrcessCase_Claims_C_HUM ()
    {
        bShowMessageData = false;
        displayInfoMeaasge = false;
        bIsExistingCase = false;
        reRenderQueueSelectionPage = true;
        bSucessSave = false;
        lstFinalCases = new List<Case>();                        
    }
       
    public String sSubId {get;set;}
    public String sGetSelctedCaseId{get;set;}
    public String sObjectId{get;set;}
    public String selectedCaseOption{get;set;}
    public String userCurrentQueue {get; set;}    
    public String sCaseNumber{get;set;}
    public String sSelectedCaseNumber{get;set;}
    public String sSelectedCaseId{get;set;}
    public String sRedirectURL {get; set;}
    public String sCaseId {get; set;}          
    public Boolean bIsExistingCase{get;set;}
    public Boolean displayInfoMeaasge {get; set;}           
    public Boolean bGuidedProcesscheck {get;set;}
    public Boolean bSucessSave {get; set;}
    public Boolean reRenderQueueSelectionPage {get; set;}  
    public Boolean bShowMessageData{get;set;}
    public boolean bRTIDisWarningMsg {get; set;}
    public ApexPages.StandardSetController stdsetcon {get;set;}      
        
    public Boolean hasNext 
    {
        get 
        {
            if(stdsetcon!=null){return stdsetcon.getHasNext();}
            else {return null;}
        }
        set;
    }    
    public Boolean hasPrevious 
    {
        get 
        {
            if(stdsetcon!=null){return stdsetcon.getHasPrevious();}
            else {return null;}
        }
        set;
    }       
    public Integer pageNumber 
    {
        get 
        {
            if(stdsetcon!=null){return stdsetcon.getPageNumber();}
            else{ return null;}
        }
        set;
    }    
    public List<Case> lstCases
    {
        get
        {
            if (stdsetcon != null)
            {
                return (List < Case > ) stdsetcon.getRecords();
            }
            else
            {
                displayGuidedProcessCaseList();
                return (List < Case > ) stdsetcon.getRecords();
            }
        }
        set;
    }    
    public integer iConstantValue
    {
        get
        {
          iConstantValue = GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_HUNDRED_HUM;
          return iConstantValue;
        }
        set;
    }    
    public String sSortExpression 
    {
        get 
        {
            return sSortExp;
        }
        set 
        {
            if (value == sSortExp)
            {
                sSortDirection = (sSortDirection == GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM) ? GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM : GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
            }
            else{sSortDirection = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;}
            sSortExp = value;
        }
    }
    
    /*
    * Method Name : displayGuidedProcessCaseList
    * Description : This method is used to retrieve cases associated to an account.
    * Return type : Void
    * Parameter   : NA
    */
    public void displayGuidedProcessCaseList()
    {    
        bIsExistingCase= false;
        sSelectedCaseId ='';
        sSelectedCaseNumber = '';
        bIsExistingCase = true;      
        sObjName = getSObjName(sObjectId);
        accId = LoggingHelper_H_HUM.getAccountId(sObjectId,sObjName );        
        lstFinalCases = LoggingDataLogic_D_HUM.retrieveAccountCases(accId, DateTime.Now().adddays(-90), DateTime.Now(), null, null);
        stdsetcon = new ApexPages.StandardSetController(lstFinalCases);
        if(stdsetcon != NULL) stdsetcon.setPageSize(GLOBAL_CONSTANT_HUM.PageSize_HUM);        
    } 
    
    /*
    * Method Name : search
    * Description : This method is used filter cases by CaseNumber. This is an exact match search
    * Return type : Void
    * Parameter   : NA
    */
    public void search()
    {
        if(String.isBlank(sCaseNumber))
        {
            stdsetcon = new ApexPages.StandardSetController(lstFinalCases);
        }
        else
        {
            stdsetcon = new ApexPages.StandardSetController(LoggingProcessLogic_S_HUM.SearchCases(sCaseNumber, lstFinalCases));
        }
        if(stdsetcon != NULL)
        {
            stdsetcon.setPageSize(GLOBAL_CONSTANT_HUM.PageSize_HUM);
        }
    }
    
    public void getSelectedCase()
    {
    }
    
    /*
     * Method Name : createCaseAndRedirect
     * Description : This method is used to create new case
     * Return type : void
     * Parameter   : NA
    */    
    public void createCaseAndRedirect()
    {
        try
        {
            String sStandardUrlForTab = ApexPages.CurrentPage().getParameters().get('standardURL').escapeHtml4();
            if(String.isBlank(accId) || String.isBlank(sObjName))
            {
                sObjName = getSObjName(sObjectId);
                accId = LoggingHelper_H_HUM.getAccountId(sObjectId,sObjName );
            }
            sSelectedCaseId  = LoggingProcessLogic_S_HUM.createNewCase(sObjectId, ApexPages.CurrentPage().getParameters().get('standardURL').escapeHtml4());
            sRedirectURL = LoggingProcessLogic_S_HUM.sRedirectURL;
            
            if(String.IsNotBlank(sSelectedCaseId))
            {
                sSelectedCaseNumber =  LoggingDataLogic_D_HUM.getCaseNumber(sSelectedCaseId);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'AttachGuidedPrcessCase_Claims_C_HUM','createCaseAndRedirect');
        }
    }
    
    /*
     * Method Name : getSortDirection
     * Description : This method is used to get the sortdirection.
     * Return type : String
     * Parameter   : NA
    */    
    public String getSortDirection()
    {
        if (String.isBlank(sSortDirection))
        {
            return GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
        }
        else
        {
            return sSortDirection;
        }
    }
    
    /*
     * Method Name : setSortDirection
     * Description : This method is used to set the Sort direction.
     * Return type : Void
     * Parameter   : String
    */    
    public void setSortDirection(String value)
    {
        sSortDirection = value;
    }
    
    /*
     * Method Name : sortResultPage
     * Description : This method is used to sort the case records.
     * Return type : Void
     * Parameter   : NA
    */    
    public void sortResultPage()
    {
        try
        {            
            lstFinalCases = LoggingDataLogic_D_HUM.retrieveAccountCases(accId, DateTime.Now().adddays(-90), DateTime.Now(), null, null);
            stdsetcon = new ApexPages.StandardSetController(lstFinalCases);
            if(stdsetcon != Null)    stdsetcon.setPageSize(GLOBAL_CONSTANT_HUM.PageSize_HUM);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'AttachGuidedPrcessCase_Claims_C_HUM','sortResultPage');
        }
    }
    
    /*
     * Method Name : previous
     * Description : This method returns the previous page of records.
     * Return type : Void
     * Parameter   : NA
    */ 
    public void previous()
    {
        stdsetcon.previous();
    }

    /*
     * Method Name : first
     * Description : This method returns the first page of records.
     * Return type : Void
     * Parameter   : NA
    */     
    public void first()
    {
        stdsetcon.first();
    }

    /*
     * Method Name : last
     * Description : This method returns the last page of records.
     * Return type : Void
     * Parameter   : NA
    */ 
    public void last() 
    {
        stdsetcon.last();
    }

    /*
     * Method Name : next
     * Description : This method returns the next page of records.
     * Return type : Void
     * Parameter   : NA
    */      
    public void next() 
    {
        stdsetcon.next();
    } 
    
    /*
     * Method Name : checkUserQueue
     * Description : This method checks if User is part of any queue or not
     * Return type : Void
     * Parameter   : NA
    */  
 public void checkUserQueue() 
  {
     Logging_C_HUM.updateQueueLogic();
     userCurrentQueue = LoggingDataLogic_D_HUM.getUserQueue(UserInfo.getUserId());
     String caseIdSelectedVal = ApexPages.CurrentPage().getParameters().get('CaseIdVal');
     String sStmtType = ApexPages.CurrentPage().getParameters().get('statementType');
     if(caseIdSelectedVal != null && !String.isBlank(caseIdSelectedVal) && sStmtType!='rtiMember')
     {
        bCasesProcessAttached = ([SELECT Id FROM Template_Submission__c where Template__r.description__c IN ('Send Claim Statement: Member','Send Claim Statement: Provider') and Submission_Owner__r.Object_Owner_ID__c =:caseIdSelectedVal]).size()>=20 ? true : false;
     }
     if(caseIdSelectedVal != null && !String.isBlank(caseIdSelectedVal) && sStmtType=='rtiMember')
     {
        bCasesProcessAttached = ([SELECT Id FROM Template_Submission__c where Template__r.description__c IN ('Send Print Item: Member') and Submission_Owner__r.Object_Owner_ID__c =:caseIdSelectedVal]).size()>=20 ? true : false;
     }
    }
    
    public void showMessageData(){
        String sStmtType = ApexPages.CurrentPage().getParameters().get('statementType');
        if(sStmtType!='rtiMember')
            bShowMessageData = true;
        else
            bRTIDisWarningMsg = true;
    }
    
    /*
    * Method Name : updateQueueLogic
    * Description : This method is used to update user's queue
    * Return type : void
    * Parameter   : NA
    */
    public static void updateQueueLogic()
    {    
        HUMQueueSelectionController queueObj = new HUMQueueSelectionController();
        queueObj.init();        
    }   
    
    /*
     * Method Name : checkGuidedProcessPresent
     * Description : This method checks if there is already guided process present to the selected existing case.
     * Return type : void
     * Parameter   : NA
    */     
    public void checkGuidedProcessPresent()
    {
        String sTemplateSubmission = ''; 
        String CaseID = ApexPages.currentPage().getParameters().get('caseId');
        if(CaseID !=null && string.isNotBlank(CaseId.escapeHtml4()))
        {
            lstcasAct = [Select Id, Case_Action__r.Name, Case_Action__c, Case__c From Case_Action_Association__c where Case__c =: ApexPages.currentPage().getParameters().get('caseId') limit 1];
            lstAttachmentLog = [Select Attachment_Key__c,Attachment_Type__c from Attachment_Log__c where Case__c = :ApexPages.currentPage().getParameters().get('caseId') and Attachment_Key__c =: sAttachmentKeyName limit 1];          
            List<Template_Submission_Owner__c> lstTSO = [Select (Select Template__r.Description__c, LastModifiedDate, LastModifiedBy.Name, LastModifiedById, 
            CreatedDate, CreatedBy.Name, CreatedById From Template_Submissions__r) 
            From Template_Submission_Owner__c WHERE Object_Owner_ID__c =: ApexPages.currentPage().getParameters().get('caseId')]; 
               
            if(lstTSO != NULL && !lstTSO.isEmpty() && lstTSO[0].Template_Submissions__r != NULL && !lstTSO[0].Template_Submissions__r.isEmpty())
            {
                sTemplateSubmission = lstTSO[0].Template_Submissions__r[0].Template__r.Description__c;
                if(sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWAUTHREFERRALFLOWDESC_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREOIDESCRIPTION_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREEXPEDITEDDESC_HUM || 
                sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWADDRESSVERIFDESC_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREPARTDESCR_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWPCPPCDPROCESS_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEPROCESS_INVOICEREQUEST_DESC_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.PharmacyWebIssues_process || sTemplateSubmission == GLOBAL_CONSTANT_HUM.PharmacyInactivateRx_process || sTemplateSubmission == System.Label.MedicaidPARProviderNotAccepting_HUM || sTemplateSubmission == System.Label.MedicareDisenrollment_HUM) 
                {
                    bGuidedProcesscheck = true;
                    ApexPages.addmessage(new ApexPages.message(ApexPages.severity.WARNING,System.Label.GuidedProcessWarning_L_HUM));
                } 
                else
                {
                    bGuidedProcesscheck = false;           
                }               
            }
            else if(lstcasAct != NULL && !lstcasAct.isEmpty())
            {
                bGuidedProcesscheck = true;
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.WARNING,System.Label.GuidedProcessWarning_L_HUM));
            }
            else if(lstAttachmentLog != NULL && !lstAttachmentLog.isEmpty())
            {
                bGuidedProcesscheck = true;
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.WARNING,System.Label.GuidedProcessWarning_L_HUM));
            }
            else
            {
                bGuidedProcesscheck = false;          
            }
        }     
    }
      
    /*
     * Method Name : updateWhoID
     * Description : This method will launch the auto save flow.
     * Return type : void
     * Parameter   : NA
     */
     public void updateWhoID() 
     {     
      if(sSubId != null)
       {    
         ID isubmission = id.Valueof(sSubId);     
         String CaseID = ApexPages.currentPage().getParameters().get('caseId'); 
         if(String.isBlank(CaseID)) return;
         list<Template_Submission__C> OwnertoUpdate =  [Select ID,  Submission_Owner__r.Object_Owner_ID__c  from Template_Submission__C where ID =: String.escapeSingleQuotes(isubmission) LIMIT 1];          
         list<Template_submission_Owner__c> lstUpdate =[select  ID ,Object_Owner_ID__c from Template_submission_Owner__c where id =:OwnertoUpdate[0].Submission_Owner__c];         
         if(OwnertoUpdate != NULL && !OwnertoUpdate.isEmpty() && lstUpdate != NULL && !lstUpdate.isEmpty())
         {     
             lstUpdate[0].Object_Owner_ID__c  = CaseID ;        
             update lstUpdate;
             bSucessSave = true;     
         } 
         List<case> lstcasenumber = [select id,CaseNumber from case where id =:ApexPages.CurrentPage().getParameters().get('caseId') LIMIT 1]; 
         sCaseNumber = lstcasenumber[0].CaseNumber ;
     } 
    }
       
    /*
     * Method Name : getCaseId
     * Description : Gets the caseId for the case on which mouse is hover
     * Return type : void
     * Parameter   : NA
    */    
    public void getCaseId()
    {
        sCaseId = ApexPages.CurrentPage().getParameters().get('caseId');
    }    
}