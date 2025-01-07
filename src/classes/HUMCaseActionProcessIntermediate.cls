/******************************************************************************************************************************
Class Name :  HUMCaseActionProcessIntermediate 
Version: 1.0 
Created Date     : JUL 24 2014
Function         : Controller to handle redirection to Process Pages.                   
Test Class       : HUMCaseActionProcessIntermediateTest

        Modification Log : 
 *       Developer                    Code Review      Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *       Vivek Batham                 17353            07/24/2014           Original version.
 *       Vivek Batham                 17840            09/10/2014           Made changes according to Humana standards
 *       Raja Sekhar Reddy M V        19410            11/20/2014           Added Cancel button funtionality. REQ - 40168
 *       Santhi Mandava               23304            05/19/2015           Created HUMTemplateSettings__c custom setting to hold all template names And VF pages names.
                                                                            Added New templates in launch process dropdown.
 *       SuryaKumari Medicherla       23305            05/19/2015           Code Modified for REQ-105810(Redirecting to Disenrollment Template)
 *       SuryaKumari Medicherla       24122            06/12/2015           Code Modified for REQ-105810(opening Disenrollment Template in New tab)   
 *       Harshith Mandya              24122            06/16/2015           Code added for making one process for case ( REQ - 187294 ) Line No 69-90
 *       Shreya Choodamani            24122            06/18/2015           Code added for allowing QAA to be created for Member And Non Member Cases without policy associated Line(206 - 293)
 *       Shreya Choodamani            24161            06/24/2015           Code fix to address review comments by Jeremy.
 *       Harshith Mandya              28341            12/01/2015           Implementation of REQ - 215100
 *       Harshith Mandya              28341            21/12/2015           Removed logic to display Medicare DAC in a diff link - Defet #D26209
 *       Praveen Kumar Parimi         27942            05/12/2016           Added logic for Guided Process business rules And modified the constants to refer from Global Constants.
 *       Manish Kumar                 48114            11/15/2016           Fixed defect#242697.0001. Added logic to read encrypted data from Custom Settings instead of making callout to DPAAS server.
 *       Pallavi R                    141064           10/13/2017           REQ 31153 - Added Medicare good Cause Reinstatement changes & page reference 
 *       Bharath Rajappan             204270           03/22/2018           REQ - 357304 - Creditable Coverage attestation changes
 *       Bharath Rajappan             204793           03/27/2018           REQ - 359255 - Resolve issue reported in CA Ticket # 7435297
 *       Bharath Rajappan                              03/27/2018           REQ - 360169 - Sort Template settings by Process Action Type
 *       Bharath Rajappan                              03/27/2018           REQ - 361052 - Creditable Coverage attestation - Remove restriction on permission set
 *		 Sunil Bukya				  293887		   10/03/2018			REQ - 369758 - Implemented platform changes
 *		 Asish Behera				        		   12/31/2018			REQ - 382703 - User - Ability to prevent attaching multiple processes to cases (CRM)
 *       Pallavi R                    338657           01/29/2019           REQ - 383432 - Product type MCD check for Medicaid Good Cause Request Process
 *       Pallavi R                    345779           03/21/2019           REQ - 383432 - 408100.0001 defect fix for launch process
 *       Mohammed Noor                357636           05/27/2019           Fix for the existing Null Pointer Exception issue.
 *       Moshitha Gunasekaran                          01/29/2020           Story - 725129 - DEAA Medicare Opt Out process display in CRM for DEAA (Dual Eligible Auto Assigned) cases
 *       Jasmeen Shangari                              03/06/2020           Story - 725129 - Added DEAA process criteria for existing Process method 
 *		 Pooja Kumbhar								   05/29/2020			Story - 846171 - Added Demographics process criteria for existing process method
 *       Himalay Patel                                 06/24/2020           REQ - 939336  Display Logged 'Invoice Request' Process on Case Detail Page (RxRF) 
 *		 Ajay Chakradhar							   07/09/2020			US-1276555 - Template Summary Pages
 *       Mohammed Noor                                 08/03/2020           US825176 - Route to new version of COB page based on the ON/OFF switch
 *       Mohammed Noor                                 08/07/2020           US825176 - Fix for Defect DF-1327.
 *       Himalay Patel                                 10/14/2020           REQ - 1145316  Display Completed 'Humana Pharmacy Web Issues' Process on Case Detail Page (RxRF)
 *       Himalay Patel                                 01/26/2021           REQ -  1689789 Access the 'Humana Pharmacy Web Issues' Guided Process (CRMS) (ID# 110)
 *       Sunil Kumar Reddy                             01/27/2021           REQ - 1763850  Display Completed 'Medicaid PAR Provider Not Accepting' Process on Case Detail Page (CRM) 
 *      Mithra Bharadwaj                                                    US#2089572 SonarQube Changes
 *       Prudhvi Pamarthi                              07/01/2021           US#2229116 Eliminate Old AVF Template in Case Page(s)
 *  	 AshokKumar Nutalapati                         01/10/2022           REQ - 2843422 T1PRJ0002517 - MF 1 - Inactivate Rx: Process Content (RxRF) (ID# 163)
 *********************************************************************************************************************************/

Public with sharing Class HUMCaseActionProcessIntermediate 
{
    Private String sExistingProcess =''; 
    private Case caseRec = New Case();  
    Private String sDisEnrollment = '';
    private String sMedicareDACType = '';
    Private List<Case_Action_Association__c> casActLst = New List<Case_Action_Association__c>();
    Private List<Attachment_Log__c> lstAttachmentLog = New List<Attachment_Log__c>();
    Private Map<String, HUMTemplateSettings__c> mapOftemplateSettings = New Map<String, HUMTemplateSettings__c>();
    Private List<Attachment_Log_Item__c> lstAttachmentLogItem = New List<Attachment_Log_Item__c>();
    Private Static final String sAddressVerDescr = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWADDRESSVERIFDESC_HUM;
    Private Static final String sAddressVerProc = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREAVF_HUM;
    Private Static final String sMedicareExpeditedAppealDescr = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREEXPEDITEDDESC_HUM;
    Private Static final String sMedicareExpeditedAppealProc = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREEXPEDITED_HUM;    
    Private Static final String sMedicareOIDescr = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREOIDESCRIPTION_HUM;    
    Private Static final String sMedicareOIProc = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREOI_HUM;
    Private Static final String sMedicarePartDDesc = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREPARTDESCR_HUM;
    Private Static final String sMedicarePartDProc = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREPARTD_HUM;
    Private Static final String sAuthReferralDesc = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWAUTHREFERRALFLOWDESC_HUM;
    Private Static final String sAuthReferralProc = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWAUTHREFERRALFLOWPROCESS_HUM;
    Private Static final String sMediReinstateDesc = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREREINSTATDESC_HUM;
    Private Static final String sMediReinstateProc = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREREINSTATPROCESS_HUM;
    
    /*
     * This method Is constructor for this class And used to initialize variables.
     * @name   HUMCaseActionProcessIntermediate
     * @param  ApexPages.StandardController - Object which holds instance of current loaded record
     * @return NA
     * @throws NA
     */
    Public HUMCaseActionProcessIntermediate(ApexPages.StandardController controller) 
    {
        casActLst = new List<Case_Action_Association__c>();
        lstProcessTypes = new List<SelectOption>();
        mapOftemplateSettings = HUMTemplateSettings__c.getAll();           
        List<Case> ccCaseCheckList = new List<Case>();
        
        caseId = String.escapeSingleQuotes(Apexpages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEID_CONSTANT_HUM));
        caseId = String.isNotBlank(caseId) ? [Select ID from Case where Id =: caseId][0].Id : null;
        
         if(String.isNotBlank(caseId))
        {
            ccCaseCheckList = [Select Classification_Type__c, product__c, product_type__C, Policy_Member__c, Policy_Member__r.Policy__r.Segment_Indicator__c , OwnerID, recordType.developerName FROM  Case WHERE Id =: caseId];
        }     
        for(HUMTemplateSettings__c objTemplateSettings : [SELECT Name,ProcessActionType__c FROM HUMTemplateSettings__c where ProcessActionType__c  !=: sAddressVerDescr ORDER BY ProcessActionType__c ASC])
        {
            if(objTemplateSettings.Name == GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGE_ATTESTATION_PROCESS_NAME)
            {
                //Restrict Medicare attestation link to be visble only for classification type Calls RSO
                if(ccCaseCheckList.size() > 0 && ccCaseCheckList[0].Classification_Type__c == GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO)
                {
                    lstProcessTypes.add(new SelectOption(objTemplateSettings.Name,objTemplateSettings.ProcessActionType__c));
                }
                
            }
            else if(objTemplateSettings.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAIDREQUESTPROCESS_HUM))
            {
              //Restrict Medicare good cause request for only MCD product
              if(ccCaseCheckList.size() > 0 && String.isNotBlank(ccCaseCheckList[0].product_type__c) && ccCaseCheckList[0].product_type__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MAJOR_LOB_MCD))                
                {
                    lstProcessTypes.add(new SelectOption(objTemplateSettings.Name,objTemplateSettings.ProcessActionType__c));
                }
            }
            else if(objTemplateSettings.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.PharmacyWebIssues_process)){
                if(ccCaseCheckList.size() > 0){
                    if(ccCaseCheckList[0].recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_HP_MEMBERCASETYPE_HUM && (String.isNotBlank(ccCaseCheckList[0].Policy_Member__c) && ccCaseCheckList[0].Policy_Member__c != null)){
                        String sProfileName = [Select Name from Profile Where Id =: UserInfo.getProfileId()].Name;
                        
                        if((!(String.isBlank(ccCaseCheckList[0].OwnerID)) && ccCaseCheckList[0].OwnerID == UserInfo.getUserId()) || UtilityHelper_H_AHT_HUM.checkCustomPermissionAccess(GLOBAL_CONSTANT_HUM.CRMS_300_HP_Supervisor_Custom) || sProfileName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Customer_Care_Supervisor)){
                            lstProcessTypes.add(new SelectOption(objTemplateSettings.Name, objTemplateSettings.ProcessActionType__c));
                        }
                    }
                }
            }
			else if(objTemplateSettings.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.EnrollmentWelcome_Recommended)){      
                if(ccCaseCheckList.size() > 0){
                    if(ccCaseCheckList[0].recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_MEMBERCASETYPE_HUM  && (String.isNotBlank(ccCaseCheckList[0].Policy_Member__c)) && ccCaseCheckList[0].Policy_Member__c != null && ccCaseCheckList[0].product__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICARE_POLICY) && ccCaseCheckList[0].Policy_Member__r.Policy__r.Segment_Indicator__c == GLOBAL_CONSTANT_HUM.EINSTEIN_BOT_ACCOUNT_SEGMENT_INDICATOR){
                        String sProfileName = [Select Name from Profile Where Id =: UserInfo.getProfileId()].Name;  
                        if((!(String.isBlank(ccCaseCheckList[0].OwnerID)) && ccCaseCheckList[0].OwnerID == UserInfo.getUserId()) && UtilityHelper_H_AHT_HUM.checkCustomPermissionAccess(GLOBAL_CONSTANT_HUM.CRM_Enroll_and_Onboard_Access)){
                            lstProcessTypes.add(new SelectOption(objTemplateSettings.Name, objTemplateSettings.ProcessActionType__c));
                        }
                    }
                }
            }
            else if(!objTemplateSettings.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.DEAA_Origin) && !objTemplateSettings.Name.equalsIgnoreCase(Label.HUMCOBTemplateLabelV1))
            {
                //DEAA Template should not be displayed in launch process dropdown so added condition to remove the template
				//US825176 - added check to remove the V1 version of the COB template so that it shows only once in the Drop down 
                lstProcessTypes.add(new SelectOption(objTemplateSettings.Name, objTemplateSettings.ProcessActionType__c));
            }
            
        }
        
        bShowError = false;
        sTabId = String.isNotBlank(Apexpages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWTABID_HUM)) ? String.escapeSingleQuotes(Apexpages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWTABID_HUM).escapeHtml4()) : null;
        caseSubTabId = String.isNotBlank(Apexpages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_CASESUBTABID_HUM)) ? String.escapeSingleQuotes(Apexpages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_CASESUBTABID_HUM).escapeHtml4()) : null;
        sProcessToCreate = String.isNotBlank(Apexpages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM)) ? String.escapeSingleQuotes(Apexpages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM).escapeHtml4()) : null;
        sMedicareDACType = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMMEDICAREDAC_HUM;
        checkExistingProcess();
    }
    
    public String caseSubTabId { get; set; }
    public boolean bShowError{get; set;}
    public String sTabId {get; set;}
    public String caseId {get; set;}
    public List<SelectOption> lstProcessTypes {get; set;}
    public String sProcessType {get; set;}
    public String sProcessPgaeName {get;set;}
    public String sProcessToCreate {get;set;}
    public String sMedicareDACTypePage { get; set; }
    
    /*
     * This method is used to populate existing processes and check if a process already exists for a Case
     * @name   checkExistingProcess
     * @param  NA
     * @return void
     * @throws NA
     */
    public void checkExistingProcess()
    {
        HUMConstants__c constObj = HUMConstants__c.getInstance(GLOBAL_CONSTANT_HUM.HUMATTACHMENTLOGFORDAC_HUM);
        HUMConstants__c constPlainTextObj = HUMConstants__c.getInstance('PLAIN_ATTACHMENTLOGFORDAC_HUM');
        String sTemplateSubmission = '';
        
        if(String.isNotBlank(caseId))
        {
            casActLst = [Select Id, Case_Action__r.Name, Case_Action__c, Case__c From Case_Action_Association__c where Case__c =: caseId limit 1];
            
            if(casActLst != NULL && !casActLst.isEmpty())
            {
                sExistingProcess = casActLst[0].Case_Action__r.Name;
            }
            else
            {
                for(Template_Submission_Owner__c oTSO : (List<Template_Submission_Owner__c>)[Select 
                            (Select Template__r.Description__c, LastModifiedDate, LastModifiedBy.Name, LastModifiedById, CreatedDate, CreatedBy.Name, CreatedById 
                            From Template_Submissions__r) 
                           From Template_Submission_Owner__c WHERE Object_Owner_ID__c =: caseId])
                {
                    if(oTSO.Template_Submissions__r != NULL && !oTSO.Template_Submissions__r.isEmpty())
                    {
                        sTemplateSubmission = oTSO.Template_Submissions__r[0].Template__r.Description__c;
                        if(sTemplateSubmission.equalsIgnoreCase(sMedicareOIDescr))
                            sExistingProcess = sMedicareOIProc;
                        else if(sTemplateSubmission.equalsIgnoreCase(sMedicareExpeditedAppealDescr))
                            sExistingProcess = sMedicareExpeditedAppealProc;
                        else if(sTemplateSubmission.equalsIgnoreCase(sAddressVerDescr))
                            sExistingProcess = sAddressVerProc;
                         else if(sTemplateSubmission.equalsIgnoreCase(sMedicarePartDDesc))
                            sExistingProcess = sMedicarePartDProc;
                         else if(sTemplateSubmission.equalsIgnoreCase(sAuthReferralDesc))
                            sExistingProcess = sAuthReferralProc;    
                         else if(sTemplateSubmission.equalsIgnoreCase(sMediReinstateDesc)) //REQ 311453
                            sExistingProcess = sMediReinstateProc;  
                         else if(sTemplateSubmission.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_CREDITABLECOVERAGE_ATTESTATION_DESC_HUM)) 
                            sExistingProcess = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_CREDITABLECOVERAGE_ATTESTATION_PROCESS_HUM; 
                        else if(sTemplateSubmission.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWPCPPCDPROCESS_HUM)) 
                            sExistingProcess = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWPCPPCDPROCESS_HUM;                                   
                         else if(sTemplateSubmission.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAIDREQUESTDESC_HUM)) //REQ - 383432
                            sExistingProcess = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAIDREQUESTPROCESS_HUM;  
						else if(sTemplateSubmission.equalsIgnoreCase(System.Label.HUMDEAAMedicareOptOutProcess))
                            sExistingProcess = GLOBAL_CONSTANT_HUM.DEAA_Origin;
                        else if(sTemplateSubmission.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEPROCESS_INVOICEREQUEST_PROCESS_HUM))
                            sExistingProcess = GLOBAL_CONSTANT_HUM.CASEPROCESS_INVOICEREQUEST_DESC_HUM;
                        else if(sTemplateSubmission.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.PHARMACY_WEBISSUES_RECOMMENDED))
                            sExistingProcess = GLOBAL_CONSTANT_HUM.PharmacyWebIssues_process;
						else if(sTemplateSubmission.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.PHARMACY_INACTIVATERX_RECOMMENDED))
							sExistingProcess = GLOBAL_CONSTANT_HUM.PharmacyInactivateRx_process;
                        else if(sTemplateSubmission.equalsIgnoreCase(System.Label.MedicaidPARProviderNotAccepting_HUM))
                            sExistingProcess = System.Label.MedicaidPARProviderNotAccepting_HUM;   
                        else if(sTemplateSubmission.equalsIgnoreCase(System.Label.MedicareDisenrollment_HUM))
                            sExistingProcess = 	System.Label.MedicareDisenrollment_HUM;
						else if(sTemplateSubmission.equalsIgnoreCase('Contact Demographic Update - Commercial' ))
                            sExistingProcess = 'Commercial Template';
						else if(sTemplateSubmission.equalsIgnoreCase('Critical Demographic Update - Commercial' ))
							sExistingProcess = 'Commercial Template';
						else if(sTemplateSubmission.equalsIgnoreCase('Contact Demographic Update - Medicare' ))
							sExistingProcess = 'Demographic Update';
						else if(sTemplateSubmission.equalsIgnoreCase('Critical Demographic Update - Medicare' ))
							sExistingProcess = 'Demographic Update';
                        else if(sTemplateSubmission.equalsIgnoreCase('Address Update - Medicare' ))
                            sExistingProcess = 'Demographic Update';
                        else if(sTemplateSubmission.equalsIgnoreCase('Address Update - Medicaid' ))
                            sExistingProcess = 'Demographic Update';
						 else if(sTemplateSubmission.equalsIgnoreCase('Enroll & Onboard (E&O) Welcome Call' ))
                            sExistingProcess = 'Enrollment Welcome Call';   
                    }
                }
            }
            if(constObj != NULL && constPlainTextObj != Null) lstAttachmentLog = [Select Attachment_Key__c, Attachment_Type__c from Attachment_Log__c where (Case__c =: caseId) AND (Attachment_Key__c =: constObj.StringValue__c OR Attachment_Key__c =: constPlainTextObj.StringValue__c) limit 1];
            if(lstAttachmentLog != NULL && !lstAttachmentLog.isEmpty())    sExistingProcess = sMedicareDACType;
        }
    }
    
    /*
     * This method is used to redirect user to actual process from the dropdown selection
     * @name   loadProcess
     * @param  NA
     * @return PageReference - Target page reference where User will be redirected.
     * @throws Exception
     */
    public PageReference loadProcess() 
    {
        PageReference pgRef = NULL;
        
        try
        {
            pgRef = redirectingPreferences(sProcessType);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCaseActionProcessIntermediate', 'loadProcess');
        }
        return pgRef;
     }
    
    /*
     * This method is used to redirect the user to a Process if there is an existing process.
     * @name   redirectProcess
     * @param  NA
     * @return PageReference - Target page reference where User will be redirected.
     * @throws Exception
     */
    public PageReference redirectProcess()
    {
        PageReference pgRef = NULL;
        
        //This will remove the unsaved template data and helps rep to launch template form again.
        HUMValidSubmissionHelper.deleteInValidSubmission(caseId); 
       
        try
        {
            bShowError = false;
            if(String.isNotBlank(sProcessToCreate) && String.isNotBlank(sExistingProcess))
            {
                if(sProcessToCreate.equalsIgnoreCase(sExistingProcess))
                {
                    pgRef = redirectingPreferences(sProcessToCreate);
                }
                else
                {
                    bShowError = true;
                    throw new HUMCustomException(system.Label.HUMSingleCaseActionError);
                }
            }
            else if(String.isNotBlank(sProcessToCreate) && String.isBlank(sExistingProcess))
            {
                pgRef = redirectingPreferences(sProcessToCreate);
            }
            else if(String.isBlank(sProcessToCreate) && String.isNotBlank(sExistingProcess))
            {
                pgRef = redirectingPreferences(sExistingProcess);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCaseActionProcessIntermediate', 'redirectProcess');
        }
        
        return pgRef;
    }
    
    /*
     * This method is used to get parameters based up on input parameters.
     * @name   redirectingPreferences
     * @param  String - text value which holds process name
     * @return PageReference - Target page reference where User will be redirected.
     * @throws Exception
     */
    public PageReference redirectingPreferences(String sProcess)
    {
        PageReference pageRef = NULL;
        
        try
        {
            Case casObj = [Select Id, Policy_Member__c, Policy_Member__r.Name, CaseNumber, RecordType.Name,Classification_Type__c from Case where Id =: caseId limit 1];
            if(mapOftemplateSettings.containsKey(sProcess))
            {
                //If no-policy attached give exception to show for creditable coverage attestation form and for Case origin DEAA exception should not be displayed
                if(casObj != null  && casObj.Policy_Member__c == null && sProcess != GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGE_ATTESTATION_PROCESS_NAME
                  && sProcess != GLOBAL_CONSTANT_HUM.DEAA_Origin && sProcess != GLOBAL_CONSTANT_HUM.MedicareDemographic_process  && sProcess != GLOBAL_CONSTANT_HUM.CommercialDemographic_process)
                {
                    bShowError = true;
                    throw new HUMCustomException(system.Label.HUMCOBNoPolicyExists);
                }
                else
                {
                    pageref = createPageRef(sProcess);
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCaseActionProcessIntermediate', 'redirectingPreferences');
        }
        
        return pageRef;
    }
    
    /*
     * This method is used to return the Page Reference for re-direction called from RedirectingPreferences.
     * @name   createPageRef
     * @param  String - text value which holds process name
     * @return PageReference - Target page reference where User will be redirected.
     * @throws NA
     */
     public PageReference createPageRef(String sProcName)
     {
        PageReference pageRef = NULL;
        
        sProcessPgaeName = mapOftemplateSettings.get(sProcName).VFPageName__c;
        if(Label.HUMCOBTemplateLabel.equalsIgnoreCase(sProcName) && HUMUtilityHelper.isCRMFunctionalityON('825176') && mapOftemplateSettings.containsKey(Label.HUMCOBTemplateLabelV1))
        {
        	//if the switch is ON then open the HUMCOBProcess_V1 page
        	pageRef = new PageReference('/apex/' + mapOftemplateSettings.get(Label.HUMCOBTemplateLabelV1).VFPageName__c);
        }
        else
        {
        	pageRef = new PageReference('/apex/' + mapOftemplateSettings.get(sProcName).VFPageName__c);
        }       
        pageRef.getParameters().put(Label.HUMCOBCaseIdParam,caseId);
        pageRef.getParameters().put(Label.HUMCOBProcessType,sProcName);
        pageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_CASESUBTABID_HUM, caseSubTabId);
        pageRef.getParameters().put(Label.HUMTabId, sTabId);
        pageRef.setRedirect(true);
        return pageRef;
     }
    
    /*
     * This method is used to return to Case detail page if Cancel button is clicked.
     * @name   cancel
     * @param  NA
     * @return PageReference - Target page reference where User will be redirected.
     * @throws Exception
     */
    public PageReference cancel() 
    {
        PageReference pgRef = null;
         
        if(sTabId == null)
        {
            try
            {
                pgRef = new PageReference('/' + caseId);
                pgRef.setRedirect(true);
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'HUMCaseActionProcessIntermediate', 'cancel');
            }
        }
        return pgRef;
    }
}