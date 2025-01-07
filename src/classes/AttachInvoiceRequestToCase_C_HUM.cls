/**********************************************************************************************************
Apex Class Name  : AttachInvoiceRequestToCase_C_HUM
Version          : 1.0
Created Date     : 06/22/2020
Function         : This is controller class for visual force component 'AttachInvoiceRequestCase_CMP_HUM'.
                    This is used to attach Invoice logging details to either new case or to an existing case.
Test Class       : AttachInvoiceRequestToCase_T_HUM
***********************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------
* AshokKumar Nutalapati                              06/22/2019                 REQ - 1000087 Log 'Invoice Request' Guided Process to Case (RxRF) 
* Himalay Patel                                      06/24/2020                 REQ - 939336  Display Logged 'Invoice Request' Process on Case Detail Page (RxRF) 
* Kiran Kumar										 06/26/2020					REQ - 941987: Automatically Transfer Case When 'Invoice Request' Guided Process Attached (RxRF)
* Himalay Patel                                      10/14/2020                 REQ - 1145316  Display Completed 'Humana Pharmacy Web Issues' Process on Case Detail Page (RxRF) 
* Sunil Kumar Reddy                                  01/27/2021                 REQ - 1763850  Display Completed 'Medicaid PAR Provider Not Accepting' Process on Case Detail Page (CRM)
* Ashok Kumar Nutalapati                             04/20/2021                 User Story 2144269: T1PRJ0002517 - MF 1 - TECH - Checkmarx Scan Fixes 
* Jayeshkumar Solanki				     03/22/2021			User Story 2187337: T1PRJ0001827 - MF 1 - Quality - "Medicare CE and Sp Election DE Process" Template Implementation (RF)
* Himalay Patel                                      07/13/2021                 User Story 2384061: T1PRJ0002517 - MF 5 - TECH - Checkmarx Scan Fixes (CRMS)
* AshokKumar Nutalapati                         	 01/10/2022            		REQ - 2843422 T1PRJ0002517 - MF 1 - Inactivate Rx: Process Content (RxRF) (ID# 163)
*************************************************************************************************************/

public with sharing class AttachInvoiceRequestToCase_C_HUM extends LogDetails_A_HUM
{
    @testVisible private Id accId = NULL;
    @testVisible private String sObjName = '';
    @testVisible private String sSortExp = GLOBAL_CONSTANT_HUM.SORTDATE_HUM;
    @testVisible private String sSortDirection = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
    @testVisible private Case oCaseRec = new Case();
    @testVisible private Id idAttLogId = NULL;
    @TestVisible private transient String sCaseDetailsJSONreturn = '';
    @testVisible private String sAttachmentKeyName = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMATTACHMENTLOGFORDAC_HUM;
    @testVisible private List<Case_Action_Association__c> LstcasAct = new List<Case_Action_Association__c>(); 
    @testVisible private List<Attachment_Log__c> lstAttachmentLog = new List<Attachment_Log__c>();
    
    /*
    * Method Name : AttachInvoiceRequestToCase_C_HUM
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : NA
    */
    public AttachInvoiceRequestToCase_C_HUM ()
    {
        displayInfoMeaasge = false;
        bIsExistingCase = false;
        lstFinalCases = new List<Case>();
    }
    
    public String enterpriseId {get; private set;}
    public Boolean bSucessSave {get; set;}
    public Boolean bGuidedProcesscheck {get;set;}
    public Integer caseLstSize {get; private set;}
    public String sCaseNumber{get;set;}
    public String selectedCaseOption{get;set;}
    public String sInteractionId {get;set;} 
    public String sCaseId {get; set;}
    public String sSelectedCaseNumber{get;set;}
    public String sRedirectURL {get; set;}
    public String sSelectedCaseId{get;set;}
    public Id sObjectId{get;set;}
    public Boolean bIsExistingCase{get;set;}
    public Boolean displayInfoMeaasge {get; set;}
    public transient List<Case> lstFinalCases {get; private set;}
    public String sSubId {get;set;}
    public String dpaasErrorLogName {get; set;}
    public ApexPages.StandardSetController stdsetcon {get;set;}
    
    public string sCaseDetailsJSON
    {
        get 
        {
            if(lstFinalCases != NULL)
            {    
                sCaseDetailsJSONreturn = JSON.serialize(lstFinalCases); 
                sCaseDetailsJSONreturn = '{"data":'+sCaseDetailsJSONreturn+'}';
                system.debug('Ashok' + sCaseDetailsJSONreturn);
                return sCaseDetailsJSONreturn;
            }
            else
            {
                return null;
            }
        }
        private set;
    }
    
    /*
    * Method Name : getKeyValue
    * Description : This method is used to retrieve Key to create Attachment_log record
    * Return type : void
    * Parameter   : NA
    */
    public void getKeyValue()
    {
        try
        {
            sObjName = getSObjName(sObjectId);
            accId = LoggingHelper_H_HUM.getAccountId(sObjectId,sObjName );
            enterpriseId = LoggingProcessLogic_S_HUM.getKeyDesc(accId);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'AttachLogDetailsToCase_C_HUM','getKeyValue');
        }
    }
    
    /*
    * Method Name : createErrorLog
    * Description : This method is used to create ErrorLog
    * Return type : void
    * Parameter   : NA
    */
    public void createErrorLog()
    {
        String sErrorMessage = Apexpages.currentpage().getparameters().get('errorMessage');
        dpaasErrorLogName = LoggingProcessLogic_S_HUM.createErrorLog(sErrorMessage,'AttachInvoiceRequestToCase_C_HUM' );
    }

    /*
    * Method Name : createPharmacyCaseAndRedirect
    * Description : This method is used to create new case from Logging component in Invoice Request page
    * Return type : void
    * Parameter   : NA
    */
    public void createPharmacyCaseAndRedirect()
    {
        try
        {
            String sStandardUrlForTab = ApexPages.CurrentPage().getParameters().get('standardURL');
            if(String.isBlank(accId) || String.isBlank(sObjName))
            {
                sObjName = getSObjName(sObjectId);
                accId = LoggingHelper_H_HUM.getAccountId(sObjectId,sObjName );
            }
            LoggingProcessLogic_S_HUM.bInvoiseRequest = true;
			sSelectedCaseId  = LoggingProcessLogic_S_HUM.createNewPharmacyLoggingCase(sObjectId,sStandardUrlForTab);
            sRedirectURL = LoggingProcessLogic_S_HUM.sRedirectURL;
            
            if(String.IsNotBlank(sSelectedCaseId))
            {
                sSelectedCaseNumber =  LoggingDataLogic_D_HUM.getCaseNumber(sSelectedCaseId);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'AttachLogDetailsToCase_C_HUM','createPharmacyCaseAndRedirect');
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
    
     /*
     * Method Name : associateInteractiontToCase
     * Description : 
     * Return type : void
     * Parameter   : NA
     */
    public void associateInteractiontToCase(){
        
        try{
            
            if(String.isNotBlank(sInteractionId) && String.isNotBlank(sSelectedCaseId)) {

                List<Case_interaction__c> lstCaseInt = [SELECT Id FROM Case_interaction__c WHERE interaction__c = :sInteractionId AND Case__c = :sSelectedCaseId Limit 1];
                if(lstCaseInt.isEmpty() && Schema.sObjectType.Case_Interaction__c.fields.Case__c.isCreateable() && Schema.sObjectType.Case_Interaction__c.fields.Interaction__c.isCreateable()){
                    Case_Interaction__c oCaseInt = new Case_Interaction__c();    
                    oCaseInt.Case__c = sSelectedCaseId;   
                    oCaseInt.Interaction__c = sInteractionId;
                    insert oCaseInt;
                }
            }
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'AttachLogDetailsToCase_C_HUM', 'AssociateInteractiontoCase');
        }
        
    }
    
        /*
    * Method Name : displayInvoiceCaseList
    * Description : This method is used to retrieve cases associated to an Policy member.
    * Return type : Void
    * Parameter   : startDate, endDate, caseNumberToSearch, limitVal
    */
    public void displayInvoiceCaseList()
    {
        try
        {
            bIsExistingCase= false;
            if(selectedCaseOption == 'Existing Case')
            {
                sSelectedCaseId ='';
                sSelectedCaseNumber = '';
                bIsExistingCase = true;
                sCaseNumber = '' ;
                if(String.isBlank(accId) || String.isBlank(sObjName))
                {
                    sObjName = getSObjName(sObjectId);
                    accId = LoggingHelper_H_HUM.getAccountId(sObjectId, sObjName);
                }
                returnListOfInvoiceCase();
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'AttachInvoiceRequestToCase_C_HUM','displayInvoiceCaseList');
        }
    }
    
    /*
    * Method Name : returnListOfInvoiceCase
    * Description : This method is used to retrieve cases associated to an account.
    * Return type : Void
    * Parameter   : NA
    */
    public void returnListOfInvoiceCase()
    {
        try
        {
            String startDate = ApexPages.currentPage().getParameters().get('searchStartDate');
            String endDate = ApexPages.currentPage().getParameters().get('searchEndDate');
            String caseNumberToSearch = ApexPages.currentPage().getParameters().get('searchCaseNumber');
            Pattern TimePttrn = Pattern.compile('(19|20)\\d\\d([- /.])(0[1-9]|1[012])\\2(0[1-9]|[12][0-9]|3[01])');
            DateTime todayDate = DateTime.now();
            DateTime lastDate = (DateTime)Date.today().addDays(GLOBAL_CONSTANT_HUM.Minus90);
            Integer limitVal = (Integer)HUMConstants__c.getInstance('Size_TwoHundred').IntegerValue__c;
            
            startDate = String.isNotBlank(startDate) ? String.escapeSingleQuotes(startDate) : null;
            endDate = String.isNotBlank(endDate) ? String.escapeSingleQuotes(endDate) : null;
            caseNumberToSearch = String.isNotBlank(caseNumberToSearch) ? String.escapeSingleQuotes(caseNumberToSearch) : null;
            
            if(String.isNotBlank(startDate) && String.isNotBlank(endDate) && TimePttrn.matcher(startDate).matches() && TimePttrn.matcher(endDate).matches())
            {
                lastDate = System.DateTime.newInstance(Integer.valueOf(startDate.subString(0,4)), Integer.valueOf(startDate.subString(5,7)), Integer.valueOf(startDate.subString(8,10)), 00, 00, 00);
                todayDate = System.DateTime.newInstance(Integer.valueOf(endDate.subString(0,4)), Integer.valueOf(endDate.subString(5,7)), Integer.valueOf(endDate.subString(8,10)), 23, 59, 59);
            }
            
            lstFinalCases = LoggingDataLogic_D_HUM.retrievePharmacyInvoiceCases(accId, sObjectId, lastDate, todayDate, caseNumberToSearch, limitVal);
            caseLstSize = lstFinalCases.size(); 
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e,'AttachInvoiceRequestToCase_C_HUM','returnListOfInvoiceCase');
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
      ID isubmission = id.Valueof(sSubId); 
      String CaseID = ApexPages.currentPage().getParameters().get('caseId'); 
      //system.debug('Test    '+ isubmission+' '+ CaseID);    
      if(String.isBlank(CaseID)) return;
      list<Template_Submission__C> OwnertoUpdate =  [Select ID,  Submission_Owner__r.Object_Owner_ID__c  from Template_Submission__C where ID =: isubmission LIMIT 1];     
      list<Template_submission_Owner__c> lstUpdate =[select  ID ,Object_Owner_ID__c from Template_submission_Owner__c where id =:OwnertoUpdate[0].Submission_Owner__c];         
      if(OwnertoUpdate != NULL && !OwnertoUpdate.isEmpty() && lstUpdate != NULL && !lstUpdate.isEmpty())
      {     
          if(Schema.SObjectType.Template_submission_Owner__c.fields.Object_Owner_ID__c.isUpdateable()){
			  lstUpdate[0].Object_Owner_ID__c  = CaseID ;        
			  update lstUpdate;
			  bSucessSave = true;                  
		  } else {
			  bSucessSave = false;
		  }     
      }
      sCaseId = ApexPages.CurrentPage().getParameters().get('caseId');
      List<case> lstcasenumber = [select id,CaseNumber from case where id =:sCaseId LIMIT 1];
      sCaseNumber = lstcasenumber[0].CaseNumber ;
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
        if(string.isNotBlank(CaseId))
        {
            lstcasAct = [Select Id, Case_Action__r.Name, Case_Action__c, Case__c From Case_Action_Association__c where Case__c =: CaseId limit 1];
            lstAttachmentLog = [Select Attachment_Key__c,Attachment_Type__c from Attachment_Log__c where Case__c = :CaseId and Attachment_Key__c =: sAttachmentKeyName limit 1];          
            List<Template_Submission_Owner__c> lstTSO = [Select (Select Template__r.Description__c, LastModifiedDate, LastModifiedBy.Name, LastModifiedById, 
            CreatedDate, CreatedBy.Name, CreatedById From Template_Submissions__r) 
            From Template_Submission_Owner__c WHERE Object_Owner_ID__c =: CaseID]; 
               
            if(lstTSO != NULL && !lstTSO.isEmpty() && lstTSO[0].Template_Submissions__r != NULL && !lstTSO[0].Template_Submissions__r.isEmpty())
            {
                sTemplateSubmission = lstTSO[0].Template_Submissions__r[0].Template__r.Description__c;
                if(sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWAUTHREFERRALFLOWDESC_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREOIDESCRIPTION_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREEXPEDITEDDESC_HUM || 
                sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWADDRESSVERIFDESC_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREPARTDESCR_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWPCPPCDPROCESS_HUM  || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEPROCESS_INVOICEREQUEST_DESC_HUM ||
                sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREREINSTATDESC_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAIDREQUESTDESC_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREAVF_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREDAC_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICARECCA_HUM || sTemplateSubmission == GLOBAL_CONSTANT_HUM.PharmacyWebIssues_process || sTemplateSubmission == GLOBAL_CONSTANT_HUM.PharmacyInactivateRx_process || sTemplateSubmission == System.Label.MedicaidPARProviderNotAccepting_HUM || sTemplateSubmission == System.Label.MedicareDisenrollment_HUM) 
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
          
			List<case> lstcasenumber = [select id,CaseNumber,Classification_Type__c from case where id =:CaseId LIMIT 1];
            set<String> classificationTypes = new set<String>{'Humana Pharmacy Calls','Humana Pharmacy Web Chat','HP Finance Ops'};
               
                if(classificationTypes.contains(lstcasenumber[0].Classification_Type__c)){
                    for(CTCI_Junction__c ctci : [select id,Classification__c,Intent__c,Classification__r.name,Intent__r.name from CTCI_Junction__c where Classification_Type__r.name =:lstcasenumber[0].Classification_Type__c]){
                        if(lstcasenumber[0].Classification_Type__c == 'HP Finance Ops' && ctci.Classification__r.name == 'Billing (Finance Ops)' && ctci.Intent__r.name == 'Invoice Requests'){
                            
                            lstcasenumber[0].CTCI_List__c = ctci.id;
                            lstcasenumber[0].Classification_Id__c = ctci.Classification__c;
                            lstcasenumber[0].Intent_Id__c = ctci.Intent__c;
                        }else if(ctci.Classification__r.name == 'Billing Issue (CT & TM)' && ctci.Intent__r.name == 'Invoice Requests'){
                            lstcasenumber[0].CTCI_List__c = ctci.id;
                            lstcasenumber[0].Classification_Id__c = ctci.Classification__c;
                            lstcasenumber[0].Intent_Id__c = ctci.Intent__c;
                            
                        }
                    }
                    update lstcasenumber[0];
                }
        }
    }
}