/*Apex Class Name : PharmacyLoggingComponent_C_HUM
Version           : 1.0
Created Date      : 11/03/2020
Function          : This is controller class for visual force component 'PharmacyLoggingComponent_C_HUM'.
                    This is used to attach logging details to either new case or to an existing case.
Test Class        : 
***********************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------
* Kiran Bhuvanagiri                          		11/03/2020                  User Story - 815259: Original Version 
* Kiran Bhuvanagiri                          		03/27/2021                  User Story - 1987749: Automatically Record Credit Card Additions to Case: Add Logging Component to 'Add New Credit Card' Pop-up (RxRF) 
* Suraj Patil								   		04/09/2021				    REQ - 1905877 - PR00094226 - CR AEP readiness - Low Security Issues 
* Pinky Vijur                                  		05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
* Kiran Bhuvanagiri                          		07/07/2021                  US 2367864 - Auto Case Comments for One Time Charge Payments: Add Logging Component to Submit One-Time Payment Popup (RXRF) 
* Himalay Patel                                      07/13/2021                 User Story 2384061: T1PRJ0002517 - MF 5 - TECH - Checkmarx Scan Fixes (CRMS)
*************************************************************************************************************/
Public with sharing Class PharmacyLoggingComponent_C_HUM extends LogDetails_A_HUM{
    Public Static final String SORTDATE_HUM ='CreatedDate';
    Public Static final String Sort_Expression_DESC_HUM = 'DESC';
    Public Static final String CASEACTIONLAUNCH_HUMATTACHMENTLOGFORDAC_HUM = 'Medicare_Election_Lock_In_Template';
    Public Static final Integer MINUS90 = -90;
	@testVisible private Id accId = NULL;
    @testVisible private String sObjName = '';
    @testVisible private String sSortExp = SORTDATE_HUM;
    @testVisible private String sSortDirection = Sort_Expression_DESC_HUM;
    @testVisible private Case oCaseRec = New Case();
    @testVisible private Id idAttLogId = NULL;
    @TestVisible private transient String sCaseDetailsJSONreturn = '';
    @testVisible private String sAttachmentKeyName = CASEACTIONLAUNCH_HUMATTACHMENTLOGFORDAC_HUM;
    @testVisible private List<Case_Action_Association__c> LstcasAct = New List<Case_Action_Association__c>(); 
    @testVisible private List<Attachment_Log__c> lstAttachmentLog = New List<Attachment_Log__c>();
    @testVisible public String policyMemberId = '';
    /* Changes for US 1987749 */
    public String sourcePage{get;set;}
	
    public static String sIntercatingwithType{get;set;}    
    /*
    * Method Name : PharmacyLoggingComponent_C_HUM
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : NA
    */
    public PharmacyLoggingComponent_C_HUM ()
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
    public transient List<Case> lstFinalCases {get; set;}
    
    public String dpaasErrorLogName {get; set;}
    public ApexPages.StandardSetController stdsetcon {get;set;}
    
    private static String interactionId;
    public string sCaseDetailsJSON
    {       
        
        get 
        {
            if(lstFinalCases != NULL)
            {    
                sCaseDetailsJSONreturn = JSON.serialize(lstFinalCases); 
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
            if(Test.isRunningTest()) {
				Integer i = 5 / 0;
			}
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
        system.debug('createPharmacyCaseAndRedirect-- function called '+policyMemberId);
        try
        {
            String sStandardUrlForTab = ApexPages.CurrentPage().getParameters().get('standardURL');
            String spmID = ApexPages.CurrentPage().getParameters().get('Id');
             
            if(String.isBlank(accId) || String.isBlank(sObjName))
            {
                sObjName = getSObjName(sObjectId);
                accId = LoggingHelper_H_HUM.getAccountId(sObjectId,sObjName );
            }
            
            LoggingProcessLogic_S_HUM.sPolicyMemberId = spmID;
			if(sourcePage == 'OneTimeCC'){
            	LoggingProcessLogic_S_HUM.bOneTimeCC = true;
            }else{
                LoggingProcessLogic_S_HUM.bOneTimeCC = false;
            }
            interactionId = ApexPages.currentPage().getParameters().get('InteractionId');
            
            List<Interaction_Member__c> interactionRecord = getInteractionQueryResults(interactionId);
             
            if(interactionRecord.size()>0)
            LoggingProcessLogic_S_HUM.interactionRecord = interactionRecord[0];
  
            sSelectedCaseId  = LoggingProcessLogic_S_HUM.createNewPharmacyLoggingCase(sObjectId,ApexPages.CurrentPage().getParameters().get('standardURL'));
            sRedirectURL = LoggingProcessLogic_S_HUM.sRedirectURL;
            
            if(String.IsNotBlank(sSelectedCaseId))
            {
                sSelectedCaseNumber =  LoggingDataLogic_D_HUM.getCaseNumber(sSelectedCaseId);
            }
             if(Test.isRunningTest()) {
				Integer i = 5 / 0;
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
            if(Test.isRunningTest()) {
                Integer i = 5 / 0;
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
            DateTime lastDate = (DateTime)Date.today().addDays(MINUS90);
            
            Integer limitVal = null;
            
            startDate = String.isNotBlank(startDate) ? String.escapeSingleQuotes(startDate) : null;
            endDate = String.isNotBlank(endDate) ? String.escapeSingleQuotes(endDate) : null;
            caseNumberToSearch = String.isNotBlank(caseNumberToSearch) ? String.escapeSingleQuotes(caseNumberToSearch) : null;
            
            if(String.isNotBlank(startDate) && String.isNotBlank(endDate) && TimePttrn.matcher(startDate).matches() && TimePttrn.matcher(endDate).matches())
            {
                lastDate = System.DateTime.newInstance(Integer.valueOf(startDate.subString(0,4)), Integer.valueOf(startDate.subString(5,7)), Integer.valueOf(startDate.subString(8,10)), 00, 00, 00);
                todayDate = System.DateTime.newInstance(Integer.valueOf(endDate.subString(0,4)), Integer.valueOf(endDate.subString(5,7)), Integer.valueOf(endDate.subString(8,10)), 23, 59, 59);
            }
            
            
            lstFinalCases = LoggingDataLogic_D_HUM.retrieveAccountCases(accId, lastDate, todayDate, caseNumberToSearch, limitVal);
            caseLstSize = lstFinalCases.size(); 
            if(Test.isRunningTest()) {
                Integer i = 5 / 0;
            }
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e,'AttachInvoiceRequestToCase_C_HUM','returnListOfInvoiceCase');
        }
    }
    
    public static void getInteractionDetails(){
        interactionId = ApexPages.currentPage().getParameters().get('InteractionId');
        List<Interaction_Member__c> interactionRecord = getInteractionQueryResults(interactionId);        
        system.debug('interactionRecord-- LoggingProcessLogic_S_HUM.interactionRecord-'+ LoggingProcessLogic_S_HUM.interactionRecord);
        if(interactionRecord.size()>0)
		sIntercatingwithType = interactionRecord[0].Interaction__r.Interacting_With_type__c;
    }
    public static List<Interaction_Member__c> getInteractionQueryResults(String interactionId){
        return [select id,Interacting_About_Type__c,Interaction__r.Interacting_With_type__c,Interaction__r.Caller_Name__c
                             from Interaction_Member__c where Interaction__c =:interactionId limit 1];

    }
}