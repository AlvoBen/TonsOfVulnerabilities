/******************************************************************************************************************************
Apex Class Name :  HumCaseActionLaunchController 
Version: 1.0 
Created Date     : JUL 02 2014
Function         : Used to get the list of selectoptions onto CaseActionLaunchpage.
Test Class       : HUMCaseActionLaunchControllerTest
        Modification Log : 
*     Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Krishna Tadikonda         17353           07/02/2014           Original Version
*  Krishna Tadikonda         17840           09/10/2014           Action guide fixes 
*  Vishal Verma              18313           10/06/2014           Initializing sSelectedGuide in constructor.
*  Krishna Tadikonda         18415           10/08/2014           Change in Constructor argument to accept StandardController parameter.
*  Vishal Verma              21148           02/25/2015           Defect : 172345.0001 Added if condition on line 32 to avoid null object reference exception on line 34
*  Shreya Choodamani         23334           05/19/2015           Added code from line 39-44 for REQ-123923 to show process links for Provider , Agent And Group
*  Pooja Wadhwa              23336           05/19/2015           Added code from line  to show process links for Member Cases conditionally based on the product type.
*  Rakesh Patni              23345           05/25/2015           Added code to show recommended process 
*  Pooja Wadhwa              23831           06/10/2015           Updated code as per New fix to retrieve LOB value from Product
*  Pooja Wadhwa              23888           06/12/2015           Updated code to fix Recommended Panel if case Is closed Or cancelled (Defect- 186481)
*  SuryaKumari Medicherla    23888           06/12/2015           Modified code to open Disenrollment Template in seperate Tab (REQ-105810)
*  Pooja Wadhwa              23981           06/16/2015           Updated code to fix Recommended Panel if case Is commercial (Defect- 187055)
*  Shreya Choodamani         24122           06/19/2015           Added code for the case id
*  Harshith Mandya           24543           07/03/2015           Updated code for Pefomance Issue
*  Harshith M, Manish K      28341           07/06/2015           Incorporated Review Comments
*  Harshith Mandya           28341           21/12/2015           Removed logic to display Medicare DAC in a diff link - Defet #D26209
*  Prachi Bhasin             33416           07/18/2016           Changes made as part of REQ- 270349 
*  Subhamay Mandal                           03/10/2017           Display Mentor Document links. REQ - 303482 
*  Subhamay Mandal                           03/23/2017           Fixed defect#265836.0001
*  Ranjeeth Nagishetty                       04/04/2017           REQ - 306327 SF - TECH - Validate And Update HumCaseActionLaunchController
*  Albin Arul                94816           05/19/2017           Req - 303397 Hide links for Humana Pharmacy profile
*  Subhamay Mandal                           06/28/2017           REQ - 321217, REQ - 321219, REQ - 321220. Mering into base codebase.
*  Ashish Upadhyay                           06/30/2017           REQ-320516 Add Left Panal for the Physician Finder Link
*  Subhamay Mandal                           07/11/2017           Added permission to view Mentor Document
*  Pallavi R                 141064          10/13/2017           REQ 311453 - Added Permission for PDP medicare Good Cause Reinstatement form
*  Pallavi R                                 11/06/2017           Defect fix 326004.0001 - Medicare reinstatment available for product type code PDP
*  Asish Behera                              02/7/2018            REQ - 351006 PCP/PCD flow link added
*  Bharath Rajappan                          03/22/2018           REQ - 357304 Creditable Coverage attestation link added 
*  Syed Mubarak                              04/04/2018           REQ - 350471 - Show Create letter EMME Link
*  Darshan Sharma                            04/09/2018           REQ - 361047 - Ability to update Mentor Links in QuickStart via Business Configuration 
*  Darshan Sharma                            04/27/2018           REQ - 361223 Ability to update Mentor Links in QuickStart via Business Configuration (CRM)
*  Mohammed Noor             222021          04/30/2018           REQ - 350471 - Fix for Defect# 356266.0001. 
*  Syed Mubarak                              05/03/2018           REQ - 350471 - fix for defect #356956.0001
*  Bharath Rajappan                          03/22/2018           REQ - 361052 Creditable Coverage attestation link - Not restricted to any permission 
*  Sunil Bukya               244531          06/17/2018           REQ - 366900 - Implemented Member_Id__c platform encryption changes.
*  Amar Gadewar                              06/22/2018           REQ - 367117 - Remove the Query for the 650 Quick Start Permission Set
*  Asish Behera                              07/24/2018           REQ - 367881 Defect 371524.0001
*  Satyam Malviya            268094          08/07/2018           REQ - 370542 - MF 3.2- Available Processes added for PDP Pilot users
*  Asish Behera                              08/27/2018           Regression defect 378860.0001 fix.
*  Darshan Sharma			                 10/01/2018		      REQ - 374820: CTCI Model Update (RF+CRM)
*  Prathyu Tushar			 324032          09/27/2018		      REQ - 380419: Auto route cases with Expedited Appeal And Part D Redetermination Template
*  Santhosh Ganji                            01/17/2018           REQ - 380499 & 380500: Added code to show Medicare process links under Recommended process Section
*  Amar Gadewar								 01/17/2019		      REQ - 383731 - Quality - Add PCP/PCD Questions Process to the Recommended Process tab on the Case Detail Page (CRM)
*  Subhamay Mandal                           02/18/2019           REQ - 383947 User Story B&E - Enable CI PAPI to the Case Page (RF) 
*  Subhamay Mandal                           02/18/2019           REQ - 383947 Update RF User check
*  Pallavi R                 338657          01/29/2019           REQ - 383432 - Product type MCD check for Medicaid Good Cause Request Process
*  Asish Behera                              03/20/2019           REQ - 388226 - Quality – RSO – Display PCP/PCD Questions Template under Recommended Processes for PCP Change Classification (RF)
*  Moshitha Gunasekaran      348700          03/25/2019           REQ - 380742 - Ability to view Transaction Reply Report (TRR) Process data for a specific member on a Medicare case
*  Santhosh Ganji                            04/19/2019           REQ - 386935 - 'Available Processes/Recommended Process’ side panel sections hide for Non-MED Cases
*  Subhamay Mandal                           04/29/2019           REQ - 397912 Incident 8243185 fix (CRM)
*  Santhi Mandava                            04/15/2019           Replaced "Member_Id_Value__c" with Name  
*  Santhosh Ganji                            05/24/2019           REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
*  Subhamay Mandal							 07/10/2019           REQ - 403669 Quality – RSO – Display PCP/PCD Questions Template under Recommended Processes for PCP Change Classification (CRM)
*  Moshitha Gunasekaran                      01/29/2020           Story - 725129 - DEAA Medicare Opt Out process display in CRM for DEAA (Dual Eligible Auto Assigned) cases
*  Kapil Arora                           	 03/18/2020           US 857398 PR00091574 - MF 1 - AHT/Quality - Medicare Good Cause Reinstatement Template Auto Routing and AHT Updates (CRM)
*  Mohammed Noor						     03/26/2020           US#975731 - Add DEAA OnOff Switch changes 
* Mithra Bharadwaj                           03/30/2020           US#929964 PR00094226 - CA Ticket 8961254 - QAA process template is missing for Dental products 
*  Mohammed Noor                             04/24/2020           US#1020879 - Remove DEAA OnOff Switch
*  Jasmeen Shangari                          04/29/2020           Rename iCreditableConstant to iProcessSeqNo to resolve Checkmarx error
*  Pooja Kumbhar							 05/29/2020			  Added Member Maintainance DFemographic link changes 
*  Himalay Patel							 10/15/2020			  REQ - 1135761 - Access the 'Humana Pharmacy Web Issues' Guided Process (RxRF)
* Himalay Patel                              01/26/2021           REQ -  1689789 Access the 'Humana Pharmacy Web Issues' Guided Process (CRMS) (ID# 110)
*  Y Sunil Kumar Reddy					     01/21/2021			  User Story 1763850: PR00091574 - MF 1 - Quality - "Medicaid PAR Provider Not Accepting" template and case validations (CRM)
* Santhi Mandava                             01/29/2021           User Story 1791226: PR00094254 - MF 4- AEP Performance - SF Limits - Sync CPU Time Limit Analysis - Case Details
* Atia Uzma                                  03/23/2021           User Story 1898796: T1PRJ0001827 - MF 1 - RF Clean Up (RF)
* Jayeshkumar Solanki			     		 03/22/2021  	      User Story 2187337: T1PRJ0001827 - MF 1 - Quality - "Medicare CE and Sp Election DE Process" Template Implementation (RF)
* Kiran Kumar								 05/21/2021 		  User Story 2169124: Update 'Humana Pharmacy Web Issues' Recommended Process Classification/Intent Criteria (CRMS)
* Prudhvi Pamarthi                           07/01/2021           US#2229116 Eliminate Old AVF Template in Case Page(s)   
*********************************************************************************************************************************/

Public with sharing Class HumCaseActionLaunchController extends SObjectDescriptorImpl_H_HUM
{ 
    Private string sClassification ='';
    private string sIntent = '';         
    @testVisible private Case caseRec = New Case();
    
    Public List<CustomLink__c> listMentorLinks{Get; Set;}
    
    public Boolean bShowMentorLink{get; set;}
    public Boolean isMedProcessAttachedValueExp{get;set;}
    public Boolean isMedProcessAttachedValuePart{get;set;}
    public Boolean bShowMemAvailableProcess {get;set;}
     /* Added variables for the Member Maintainance Update Demographics link*/
    public Boolean bShowDemographicRecommended {get;set;}
    public Boolean bShowDemographicAvailable {get;set;}
    public Boolean bShowCommercialDemographicRecommended {get;set;}
    public Boolean bShowCommercialDemographicAvailable {get;set;}
    public Boolean bShowBothDemographicRecommended {get;set;}
    public Boolean bShowBothDemographicAvailable {get;set;}
    public Boolean bShowEnrollmentWelcomeCallRecommended {get;set;}
    public Boolean bShowWebIssuesRecommended {get;set;}
    public Boolean bShowWebIssuesAvailalble {get;set;}
    public Boolean bIsCaseOwner{get;set;}
    public Boolean bMemberCase{get;set;}
    public List<Integer> processlinkiter {get;set;}
    public Boolean bShowEnrollmentWelcomeCallLink {get;set;}  
   
    /*
     * Constructor which initializes variables And gathers data into select 
     * @param    ApexPages.StandardController
     * @return   NA
     */
     
    Public HumCaseActionLaunchController(ApexPages.Standardcontroller controller)
    {
        if(!Test.isRunningTest())
            controller.addFields(New List < String > {'RecordType.Name','Classification_Type__c','Policy_Member__r.Policy_Platform__c','Policy_Member__r.Group_Number__c','Interacting_About_Type__c','Interacting_With_Type__c','recordType.developerName', 'CTCI_List__c','CTCI_List__r.Classification__c' , 'CTCI_List__r.Intent__c' ,'CTCI_List__r.Classification__r.name' , 'CTCI_List__r.Intent__r.Name' ,  'status' ,'Policy_Member__c', 'Origin'});
        caseRec = (Case)controller.getRecord();
        bDisplayLink = (caseRec.recordtype.developerName == GLOBAL_CONSTANT_HUM.DEV_MEDICARE_CASE || caseRec.recordtype.developerName == GLOBAL_CONSTANT_HUM.DEV_CLOSED_MEDICARE_CASE) ? true : false;
        bShowMemAvailableProcess = false;
        //If case record Origin is CRMTRR then AvailableProcesses section should not be displayed
        if(string.isNotBlank(caseRec.Origin) && caseRec.Origin.equals(GLOBAL_CONSTANT_HUM.TRR_ORIGIN))
		{
			return;
		}
		else
		{
			bShowMemAvailableProcess = true;
		}
		
        ApexPages.currentPage().getParameters().put('caseId',caseRec.id);
        bShowLinks = false;
        bShowPDPLinks = false;
        bShowPCPPCDLinks = false;
        bOpenCase = False;
        this.bShowMentorLink = false;
        sRecommendedProcess = '';
        iProcessSeqNo = 0;
        iPCPPCDConstant = 0;
	iEnrollmentWelcomeCallConstant = 0;
	iMedicareGoodCause = 0;
        iMCDLinksConstant = 0;
        iMedicareDCTemplateConstant = 0;
        bShowCreditableCoverageLinks = false;
        bShowCreditableCoverageLinksUnKnown = false;
		bShowAddressverificationLink = false;
        bShowOtherinsuranceLink = false;
		bMedicarePolicy = false;
        bShowDEAALink = false;
		CaseReadonly = true;
		bShowProviderNetwork = false;
		/* Initialize the variables for 846171 */
		bShowDemographicRecommended = false;
        bShowDemographicAvailable = false;
        bShowCommercialDemographicRecommended = false;
        bShowCommercialDemographicAvailable = false;
        bShowBothDemographicRecommended = false;
	bShowEnrollmentWelcomeCallRecommended = false;
        bShowBothDemographicAvailable = false;
        bShowWebIssuesRecommended = false;
        bShowWebIssuesAvailalble = false;
        bIsCaseOwner = false;
        bMemberCase = false;
	bShowEnrollmentWelcomeCallLink = false;
			
        populateLinks();
        
        if((caseRec.RecordType.Name == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM || caseRec.RecordType.Name == GLOBAL_CONSTANT_HUM.HUM_CLOSEDMEMBERCASE || ((caseRec.RecordType.Name == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_UNKNOWNCASE_HUM || caseRec.RecordType.Name == GLOBAL_CONSTANT_HUM.HUM_CLOSEDUNKNOWNCASE) && caseRec.Interacting_About_Type__c == GLOBAL_CONSTANT_HUM.CASEEDIT_INT_ABOUT_TYPE_UNKNOWN_MEMBER )) && caseRec.Classification_Type__c == GLOBAL_CONSTANT_HUM.HUMCCPEnrollmentRSO)
        {
            showEMMELink = true;
            populateEMMELink();
        }
        else
        {
            showEMMELink = false;
        }
        isMedProcessAttachedValueExp  = (([SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  = '95C90830-3133'  AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :caseRec.Id]).size()>0) ? true : false;   
 	    isMedProcessAttachedValuePart  = (([SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  =  'BC929CD6-E06A'  AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :caseRec.Id]).size()>0) ? true : false;
    }
      
    Public boolean bDisplayLink {Get;Set;}
    Public boolean bShowLinks {Get;Set;}
    Public boolean bShowCommercialLinks {Get;Set;}
    Public boolean bShowMedicareLinks {Get;Set;}
    Public boolean bShowSpecialityLinks {Get;Set;}
    //declare variable to disply and hide PCP flow lunch
    public boolean bShowPCPPCDLinks {get;set;}
    Public boolean bShowPDPLinks {Get;Set;}
	Public boolean bShowGoodCauseReinstatementLink {Get;Set;}
    Public String sRecommendedLink{Get; Set;}
    Public boolean bOpenCase{Get; Set;}
    Public String sRecommendedProcess { Get; Set; }
	/* Added list variables for recommended process to show two links at a time for Member Maintainance Demographic Links */
	public List<String>  sRecommendedProcesslist{get;set;}
    Public List<String>  sRecommendedLinklist{get; set;}
    //declare variable to display and hide Creditable Coverage flow lunch
    public boolean bShowCreditableCoverageLinks {get;set;}
    public boolean bShowCreditableCoverageLinksUnKnown{get;set;}
    public Integer iProcessSeqNo{get;set;}
    public Integer iPCPPCDConstant{get;set;}
    public Integer iEnrollmentWelcomeCallConstant{get;set;}
	public Integer iMedicareGoodCause{get;set;}
	public boolean bShowAddressverificationLink{get;set;}
    public boolean bShowOtherinsuranceLink{get;set;}
    //To Show EMME Link
    public String launchEmmeURL{ get; set; }
    public Boolean showEMMELink{get;set;}
    //To show Medicaid Good Cause Request
    Public boolean bShowMCDLinks {Get;Set;}
	public Boolean bMedicarePolicy{get;set;}
	public Boolean CaseReadOnly{get;set;}
    public boolean bShowDEAALink {get;set;}
    //Show Medicare Disenrollment/Cancellation Template
    public Boolean bShowMedicareDCTemplate{get;set;}
    public Integer iMedicareDCTemplateConstant{get;set;}
    public Integer iMCDLinksConstant {get;set;}
    public Set<String> wqueues = new Set<String>{GLOBAL_CONSTANT_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,GLOBAL_CONSTANT_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,GLOBAL_CONSTANT_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public Set<String> chatQueues = new Set<String>(GLOBAL_CONSTANT_HUM.WEB_CHAT_WQUEUES);
    public Boolean bShowProviderNetwork {get;set;}
    /**
    * Returns this instance of the class
    * <p>
    * Method to store the Current Page Instance
    * 
    * @return this
    */
    Public HumCaseActionLaunchController getThis()
    {
        Return this;
    }
    
    /**
    * Returns sObject of record
    * <p>
    * Virtual required Method
    *
    * @return NULL
    */
    Public sObject getRecord()
    {
        Return NULL;
    }

    /**
    * Returns List<sObject> of record
    * <p>
    * Virtual required Method
    *
    * @return NULL
    */
    Public List<sObject> getRecords()
    {
        Return NULL;
    }
                    
    /*
    *This method Is used to retrieve the various links to be displayed in the VF page
    * @param    NA
    * @return   void    
    */
    Public Void populateLinks()
    {
        String sProcess ='';
        String sCategoryDesc = '';
        String sMajorLOB = '';
        String sProcessName ='';
        if(caseRec.Id != null)
         {
           Case objCase = [Select Product__c,Policy_Member__r.Policy__r.Product__r.ProductCode, Policy_Member__r.Product__c, Policy_Member__r.Policy__r.Product__r.Major_LOB__c,
                            Policy_Member__r.Policy__r.Product__r.Product_Type__c, product_type__c, Policy_Member__c, OwnerID, Account.RecordType.Name,Classification_Type__c,Owner_Queue__c,Created_By_Queue__c , Policy_Member__r.Policy__r.Segment_Indicator__c From Case Where Id =: caseRec.Id];
        	If ((caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_MEMBERCASETYPE_HUM || caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.DEV_MEDICARE_CASE ) &&
               ((objCase.Policy_Member__r.Policy__r.Product__r.Major_LOB__c == 'PDP' && objCase.Policy_Member__r.Policy__r.Product__r.Product_Type__c == 'PDP') ||
                (objCase.Policy_Member__r.Policy__r.Product__r.Major_LOB__c == 'MES' && objCase.Policy_Member__r.Policy__r.Product__r.Product_Type__c == NULL) ||
                (objCase.Policy_Member__r.Policy__r.Product__r.Product_Type__c == 'MA' || objCase.Policy_Member__r.Policy__r.Product__r.Product_Type__c == 'MAPD')))
                {
                    if(HUMUtilityHelper.isCRMFunctionalityON('2187337') && UtilityHelper_H_AHT_HUM.isRapidForceUser()){
                    bShowMedicareDCTemplate= True;
                    iMedicareDCTemplateConstant+= 1;
                    }
                    
                }
         
            If (caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_GROUPCASETYPE_HUM ||
               caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_PROVIDERCASETYPE_HUM ||
               caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_AGENTBROKERCASETYPE_HUM ||
               caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_HP_GROUPCASETYPE_HUM ||
               caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_HP_PROVIDERCASETYPE_HUM ||
               caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_HP_AGENTBROKERCASETYPE_HUM)
            {
                bShowLinks = True;
            }
			
	    if((caseRec.recordType.developerName ==GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_MEMBERCASETYPE_HUM || caseRec.recordType.Name ==GLOBAL_CONSTANT_HUM.HUM_CLOSEDMEMBERCASE) && objCase.Product__c ==GLOBAL_CONSTANT_HUM.MED && objCase.Product_Type__c==GLOBAL_CONSTANT_HUM.MAJOR_LOB_MCD){
                bShowProviderNetwork = true;
            }
			/*Member Maintainance changes for Update Demographics links boolean variables set up for link visibility*/
            if(HUMUtilityHelper.isCRMFunctionalityON(GLOBAL_CONSTANT_HUM.DEMOGRAPHIC_ONOFF_SWITCH))
            {
                //Available process section check for Member Maintainance Update Demographics
                if(UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(GLOBAL_CONSTANT_HUM.MM_CRMS_COMMPERM_NAME))
                {
                    bShowCommercialDemographicAvailable = true;
                }
                if(UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(GLOBAL_CONSTANT_HUM.MM_CRMS_MEDPERM_NAME))
                {
                    bShowDemographicAvailable = true;
                }
                if(bShowCommercialDemographicAvailable && bShowDemographicAvailable)
                {
                    bShowBothDemographicAvailable = true;
                }

            //Recommended process section check for Member Maintainance Update Demographics
                if(String.isNotBlank(caseRec.CTCI_List__r.classification__r.Name) && ((caseRec.CTCI_List__r.classification__r.Name).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CLASSIFICATION_DEMOGRAPHIC)))
                {
                    if(bShowCommercialDemographicAvailable)
                    {
                        bShowCommercialDemographicRecommended = true;
                    }
                    if(bShowDemographicAvailable)
                    {
                        bShowDemographicRecommended = true;
                    }
                    if(bShowBothDemographicAvailable)
                    {
                        bShowBothDemographicRecommended = true;
                    }
                }
            }
           
            if((caseRec.Interacting_About_Type__c != NULL && caseRec.Interacting_With_Type__c != NULL) && ((caseRec.Interacting_About_Type__c).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEMBER)  && (caseRec.Interacting_With_Type__c).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEMBER)))
            {
                bMemberCase = true;
            }
            
			bOpenCase = caseRec.status == GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM || caseRec.status == GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM ? False : true;   
			if(!(String.isBlank(objCase.OwnerID)) && (objCase.OwnerID == UserInfo.getUserId())){
                bIsCaseOwner= true;
            }
			
            if(caseRec.CTCI_List__c!=NULL && NULL != caseRec.CTCI_List__r.Classification__c)  sClassification=caseRec.CTCI_List__r.Classification__r.Name;
                            
            if(caseRec.CTCI_List__c!=NULL && NULL != caseRec.CTCI_List__r.Intent__c) sIntent=caseRec.CTCI_List__r.Intent__r.Name ;          
            if((caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_HP_MEMBERCASETYPE_HUM) && (String.isNotBlank(CaseRec.Policy_Member__c) && CaseRec.Policy_Member__c != null))
            {      
            bShowWebIssuesAvailalble = true;

            if(!(String.isBlank(objCase.Classification_Type__c)) && (objCase.Classification_Type__c.equals(GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_HUMANA_PHARMACY_CALLS) || (objCase.Classification_Type__c.equals(GLOBAL_CONSTANT_HUM.HUMANAPHARMACYWEBCHATWQ))))
            {    
                if(!(String.isBlank(sclassification) && String.isBlank(sintent)) && (sclassification == GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_PLAN_WEBMOBILE_CTTM) && (sintent == GLOBAL_CONSTANT_HUM.CASE_INTENT_WEB_EDUCATIONNAVIGATION))
                {
                    if(bIsCaseOwner || HUMUtilityHelper.getCurrentUserProfileName() == GLOBAL_CONSTANT_HUM.Customer_Care_Supervisor || UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(GLOBAL_CONSTANT_HUM.PHARMACYSUPERVISOR_PERMISSION_HUM)){    
						bShowWebIssuesRecommended = true; 
					}
                }
                
            }                
            }
            
            //Creditable Coverage - Show recommended process when case is Medicare and Classification is Eligibility and intent is Late Enrollment Penalty (LEP)/Creditable Coverage
             sProcess = (bOpenCase && caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.MEDICARE_CASE && caseRec.CTCI_List__r.classification__r.Name == GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_ELIGIBILITY && caseRec.CTCI_List__r.Intent__r.Name == GLOBAL_CONSTANT_HUM.CASE_INTENT_LATEENROLLMENTPENALTY) ? populateRecommendedProcess(GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPGCPRSOMEDICARE_HUM) : '';
           
            if(bOpenCase && caseRec.Policy_Member__c != NULL)
                                            { 
                sCategoryDesc = HUMGetBusinessDescription.getCaseLaunchBusinessDescription(caseRec.Id,Null,false);
                if(sCategoryDesc == GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPGCPRSOMEDICARE_HUM)
                {  
                          sProcessName = GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPGCPRSOMEDICARE_HUM;
                }
				else if(sCategoryDesc == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_GSOCOMMERCIAL_HUM && caseRec.CTCI_List__r.classification__r.Name == GLOBAL_CONSTANT_HUM.CLASSIFICATION_DEMOGRAPHIC)
                {
                              sProcessName = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_GSOCOMMERCIAL_HUM;   
                }
                else if(sCategoryDesc == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_GSOCOMMERCIAL_HUM || (sCategoryDesc == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_DENTAL_HUM && caseRec.CTCI_List__r.classification__r.Name == GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_PROVIDER && caseRec.CTCI_List__r.Intent__r.Name == GLOBAL_CONSTANT_HUM.CASE_INTENT_PCPPCD))
                {    
                     
                         sProcessName = GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPGCPRSOMEDICARE_HUM;
                     
                }
                //populatating PCPPCD process in recomonded process for DEN policy 
                else if(sCategoryDesc == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_DENTAL_HUM && caseRec.CTCI_List__r.classification__r.Name == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_Classification_HUM && caseRec.CTCI_List__r.Intent__r.Name == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_Intent_HUM)
                {
                        
                          sProcessName = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_DENTAL_HUM;
                }
				else if(sCategoryDesc == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_DENTAL_HUM && caseRec.CTCI_List__r.classification__r.Name == GLOBAL_CONSTANT_HUM.CLASSIFICATION_DEMOGRAPHIC)
                {
                              sProcessName = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_DENTAL_HUM;   
                }
                else if(caseRec.CTCI_List__r.classification__r.Name == GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_PLAN_WEBMOBILE_CTTM && caseRec.CTCI_List__r.Intent__r.Name == GLOBAL_CONSTANT_HUM.CASE_INTENT_WEB_EDUCATIONNAVIGATION)
                {
                    sProcessName = GLOBAL_CONSTANT_HUM.PHARMACY_WEBISSUES_RECOMMENDED;
                }
		 //Show Enrollment Welcome Call link when the  Case Record Type is Member and Case is associated to a policy with Product as MED and Individual Medicare         
                 else if ( caseRec.CTCI_List__r.classification__r.Name == GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_COMMUNICATION && caseRec.CTCI_List__r.Intent__r.Name == GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_WelcomeCall && caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_MEMBERCASETYPE_HUM && caseRec.Policy_Member__c != NULL && objCase.Policy_Member__r.Product__c == GLOBAL_CONSTANT_HUM.MEDICARE_POLICY && objCase.Policy_Member__r.Policy__r.Segment_Indicator__c == GLOBAL_CONSTANT_HUM.EINSTEIN_BOT_ACCOUNT_SEGMENT_INDICATOR && UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(GLOBAL_CONSTANT_HUM.CRM_Enroll_and_Onboard_Access)){
                    bShowEnrollmentWelcomeCallRecommended = true;                    
                   sProcessName = GLOBAL_CONSTANT_HUM.EnrollmentWelcome_Process;
                } 
                
                if(!(String.isBlank(sProcessName )))
                sProcess = populateRecommendedProcess(sProcessName);
            }   
                                          
            If (caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_MEMBERCASETYPE_HUM || caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_HP_MEMBERCASETYPE_HUM||Test.isRunningTest())
            {                                                   
                if(!(String.isBlank(sCategoryDesc)) && bOpenCase)
                                                            {
                        if(sCategoryDesc == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_GSOCOMMERCIAL_HUM)
                                                bShowCommercialLinks = True;
                        else if(sCategoryDesc == GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPGCPRSOMEDICARE_HUM)
                             {
                               if(objCase.Policy_Member__r.Policy__r.Product__r.Product_Type__c !=null && objCase.Policy_Member__r.Policy__r.Product__r.Product_Type__c.equalsIgnoreCase('PDP')) 
							   {    
                                    bShowPDPLinks = true;
									
                                    Boolean bIsPharmacyUser = Pharmacy_H_HUM.isPharmacyUser() == GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM ? false : true;
                                    if (!bIsPharmacyUser)
                                    {
                                       if (caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_MEMBERCASETYPE_HUM && 
                                        (String.isNotBlank(objCase.Classification_Type__c) &&
                                         (objCase.Classification_Type__c.equals(GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_LINETRSO))))
                                        {
                                            bShowGoodCauseReinstatementLink = false;
                                        }
                                        else
                                        {
                                            bShowGoodCauseReinstatementLink = true;
                                            iMedicareGoodCause = iMedicareGoodCause + 1;
                                        } 
                                    }
                               }
							   else
                               {
                                  bShowMedicareLinks = True;
                                  if(objCase.Product_Type__c !=null && objCase.Product_Type__c.equalsIgnoreCase('MCD'))
                                  { 
				  	bShowMCDLinks = true;
                                    	iMCDLinksConstant+=1;
                                  }
                               }  
                             }      
                        else if(sCategoryDesc == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_SPECIALITY_HUM || sCategoryDesc == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_DENTAL_HUM)
                            bShowSpecialityLinks = True;
                 }              
            }
            
            // blockl to set bShowPCPPCDLinks variable as true when PCP flow should be lunched.
             if (caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_MEMBERCASETYPE_HUM){
                 if(!(String.isBlank(sCategoryDesc)) && bOpenCase){
                     
                      if(sCategoryDesc == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_GSOCOMMERCIAL_HUM || sCategoryDesc == GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPGCPRSOMEDICARE_HUM || sCategoryDesc == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_DENTAL_HUM) {
                      	bShowPCPPCDLinks = true;
                      	iPCPPCDConstant = iPCPPCDConstant + 1;	
                      	
                      }
                        
                 }   
             }
			 
			//Added code to show Medicare process links under Recommended process Section
             if(caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_HP_MEMBERCASETYPE_HUM||Test.isRunningTest())
             {  
                 if(!(String.isBlank(objCase.Classification_Type__c)) && (objCase.Classification_Type__c.equals(GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_HUMANA_PHARMACY_CALLS)))
                 {  
                   if(!(String.isBlank(sclassification) || String.isBlank(sintent)) && (sclassification == GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_MEMBER_PROFILEACCOUNT_CTTM) && (sintent == GLOBAL_CONSTANT_HUM.CASE_INTENT_ADDUPDATE_PLAN_DEMOGRAPHICS))
                   {
                        bShowAddressverificationLink = true;
                   }
                   else if(!(String.isBlank(sclassification) || String.isBlank(sintent)) && (sclassification == GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_BENEFITS_CTTM) && (sintent == GLOBAL_CONSTANT_HUM.CASE_INTENT_TRANSFER_COB))
                   {
                        bShowOtherinsuranceLink = true;
                   }  
                 }
             }
             
            //Show Creditable link as true when the Classification Type is RSO(CALL) or PDP Pilot Plan Calls
              if(!(String.isBlank(objCase.Classification_Type__c)) && (objCase.Classification_Type__c.equals(GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO) || objCase.Classification_Type__c.equals(GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_CALLPDPPilot)))
            {
                bShowCreditableCoverageLinks = true;
                iProcessSeqNo = iProcessSeqNo + 1;
                if(objCase.Account.RecordType.Name == GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNMEMBER_HUM)
                {
                    bShowCreditableCoverageLinksUnKnown = true ;                                
                }
                
            }
            
        	// Do not Show PCP / PCD Process as recommended for Non-Member Record Type and PDP Policy Type
            if(( bShowLinks || bShowPDPLinks) && caseRec.CTCI_List__r.classification__r.Name == GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_PROVIDER && caseRec.CTCI_List__r.Intent__r.Name == GLOBAL_CONSTANT_HUM.CASE_INTENT_PCPPCD) {
                
                sProcess ='';
            }
           
            sRecommendedProcess = sProcess;
			
			//Hide Side Panel Links for Non Medicare Cases
			if(caseRec.recordtype.name.containsIgnoreCase(GLOBAL_CONSTANT_HUM.HumHPRecType) || (caseRec.Policy_Member__c != NULL && objCase.Policy_Member__r.Product__c == GLOBAL_CONSTANT_HUM.MEDICARE_POLICY) || (chatQueues.contains(objCase.Owner_Queue__c) && caseRec.Origin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN))
            {
               bMedicarePolicy = True;
               if(caseRec.recordtype.name.containsIgnoreCase(GLOBAL_CONSTANT_HUM.HumHPRecType) || wqueues.contains(objCase.Owner_Queue__c) || wqueues.contains(objCase.Created_By_Queue__c) || (chatQueues.contains(objCase.Owner_Queue__c) && caseRec.Origin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN))
               {
                 CaseReadonly = false;
               }
            }

            //Show DEAA link as true when the Classification is DEAA Plan Change and Case Origin is DEAA
            if(bOpenCase && string.isNotBlank(sclassification) && string.isNotBlank(caseRec.Origin) && caseRec.Origin.equals(GLOBAL_CONSTANT_HUM.DEAA_Origin) && sclassification.equals(GLOBAL_CONSTANT_HUM.DEAA_Classification))
            {
                list<Template_Submission_Data__c> lstTempData = [SELECT Id, Name, Template_Field__c, Template_Submission__c, 
	            Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c FROM Template_Submission_Data__c WHERE 
	            Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :caseRec.id AND Template_Submission__r.Template__r.Unique_Template_Name__c = :GLOBAL_CONSTANT_HUM.DEAA_Template];
	            if(lstTempData != null && !lstTempData.isEmpty())
		        {
		          bShowDEAALink = false;  
		        }
		        else
		        {
		          bShowDEAALink = true;  
		        }            
          	}
          	else 
            {
                bShowDEAALink = false;
            }			
	    //Show Enrollment Welcome Call link when the  Case Record Type is Member and Case is associated to a policy with Product as MED and Individual Medicare         
            if (caseRec.recordType.developerName == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_MEMBERCASETYPE_HUM && caseRec.Policy_Member__c != NULL && objCase.Policy_Member__r.Product__c == GLOBAL_CONSTANT_HUM.MEDICARE_POLICY && objCase.Policy_Member__r.Policy__r.Segment_Indicator__c == GLOBAL_CONSTANT_HUM.EINSTEIN_BOT_ACCOUNT_SEGMENT_INDICATOR && UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(GLOBAL_CONSTANT_HUM.CRM_Enroll_and_Onboard_Access)){
                bShowEnrollmentWelcomeCallLink = true;
                iEnrollmentWelcomeCallConstant = 1;
            }
        }
    }
    
   
    /*
    *This method Is used to retrieve recommended process url based on classification And intent values
    *@param      String   sProcessType
    *@rertunr    String    
    */
    Public String populateRecommendedProcess(String sProcessType)
    {   
		Integer i = 0;
        String sRecommendedProcess = '';
        String sProcessAVF = GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWADDRESSVERIFDESC_HUM;
		sRecommendedProcesslist = new List<String>();
        sRecommendedLinklist = new List<String>();
        processlinkiter = new List<Integer>();
        If(!(String.isBlank(sclassification) || String.isBlank(sintent)))
        {
            List <HUMRecommended_Process__c> lstRecommendedProcess = [Select RecommendedProcess__c, RecommendedLink__c from HUMRecommended_Process__c 
                                                                where classification__c=:sClassification and intent__c=: sIntent and PolicyType__c=:sProcessType and RecommendedProcess__c!=: sProcessAVF];                                
            if(!lstRecommendedProcess.isEmpty())
            {
                sRecommendedProcess = lstRecommendedProcess[0].RecommendedProcess__c; 
                sRecommendedLink = lstRecommendedProcess[0].RecommendedLink__c;
             } 
			 /* Hard coded the values for process as having check on classification and policy type only not on Intent 
			and also need to disply the two links at a time so added list variables to display the links*/
			 if(bShowBothDemographicRecommended)
                {
                            sRecommendedProcesslist.add(GLOBAL_CONSTANT_HUM.MedicareDemographic_Recommended);
                            sRecommendedLinklist.add(GLOBAL_CONSTANT_HUM.MedicareDemographic_process);
                            processlinkiter.add(i);
                            i++;
                            sRecommendedProcesslist.add(GLOBAL_CONSTANT_HUM.CommercialDemographic_Recommended);
                            sRecommendedLinklist.add(GLOBAL_CONSTANT_HUM.CommercialDemographic_process);
                            processlinkiter.add(i);
                            i++;
                }
                else if(bShowDemographicRecommended && !bShowBothDemographicRecommended)
                {
                            sRecommendedProcesslist.add( GLOBAL_CONSTANT_HUM.MedicareDemographic_Recommended);
                            sRecommendedLinklist.add(GLOBAL_CONSTANT_HUM.MedicareDemographic_process);   
                            processlinkiter.add(i);
                            i++;        
                }
                else if(bShowCommercialDemographicRecommended && !bShowBothDemographicRecommended)
                {
                            sRecommendedProcesslist.add(GLOBAL_CONSTANT_HUM.MedicareDemographic_Recommended);
                            sRecommendedLinklist.add(GLOBAL_CONSTANT_HUM.CommercialDemographic_process);    
                            processlinkiter.add(i);
                            i++;       
                }
                else if(bShowWebIssuesRecommended){
                    sRecommendedProcesslist.add(GLOBAL_CONSTANT_HUM.PHARMACY_WEBISSUES_RECOMMENDED);
                    sRecommendedLinklist.add(GLOBAL_CONSTANT_HUM.PHARMACY_WEBISSUES_RECOMMENDED);    
                    processlinkiter.add(i);
                    i++;
		 }
                else if (bShowEnrollmentWelcomeCallRecommended) {
                    sRecommendedProcesslist.add(GLOBAL_CONSTANT_HUM.EnrollmentWelcome_Process);
                    sRecommendedLinklist.add(GLOBAL_CONSTANT_HUM.EnrollmentWelcome_Recommended);    
                    processlinkiter.add(i);
                    i++;
                }
                                  
        }   
        return  sRecommendedProcess;  
    }   
    
    // aht 03102017 get mentor document links
    /**
    * Method to get all mentor document links based on clasification and Intent
    * @param      NA
    * @rerturn    PageReference
    */
    public void getMentorLinks() {
        this.listMentorLinks = null;

        try {
           
            	if(String.isNotBlank(caseRec.Classification_Type__c))
                {
                String strClassification = System.CurrentPageReference().getParameters().get('Classification');
                String strIntent = System.CurrentPageReference().getParameters().get('Intent');
                
                strClassification = strClassification.unescapeHtml4();
                strIntent = strIntent.unescapeHtml4();
                 
                Integer iValue = Integer.Valueof(HUMConstants__c.getInstance('HUMLimit1000').IntegerValue__c); 
                
                listMentorLinks = [select Id,Name 
                                  from CustomLink__c 
                                  where recordtype.developerName = 'Mentor_Link'
                                  and Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification__r.Name = :strClassification 
                                  and Quick_Start_Pretext__r.CTCI_List_lkp__r.Intent__r.Name = : strIntent 
                                  and Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification_Type__r.Name =: caseRec.Classification_Type__c
                                  order by Name LIMIT : iValue];
                
                this.bShowMentorLink = !this.listMentorLinks.isEmpty();
                }   
             
        } catch(Exception e) {
            HUMExceptionHelper.logErrors(e, 'HumCaseActionLaunchController', 'getMentorLinks');
        }
    }
    
    public void populateEMMELink()
    {
        String URLValue;
        URLValue = MET_Enrollment_Launch_EMME_URL__c.getInstance('Non_ME_Create_EMME_URL').Launch_EMME_URL__c;
        Member_ID__c memberNumber;
        List<Member_ID__c> memberList = new List<Member_ID__c>();
        if(String.isNotBlank(CaseRec.Policy_Member__c))
        {
            memberList = [SELECT Name, Member_Dependent_Code__c  FROM Member_ID__c WHERE Policy_Member__c = :caseRec.policy_member__c AND Type__c = : HUMConstants.HUMMEMBER_ID_TYPE LIMIT 1];
        }
        if(caseRec.Id != null)
        { 
             Boolean isMemberCase = (caseRec.RecordType.Name == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM || caseRec.RecordType.Name == GLOBAL_CONSTANT_HUM.HUM_CLOSEDMEMBERCASE ? true:false);
            
                if (isMemberCase && String.isNotBlank(CaseRec.Policy_Member__c) && CaseRec.Policy_Member__c != null && !memberList.isEmpty())
                {
                    memberNumber = memberList.get(0);
                    
                }
                    
                List<String> urlValues = URLValue.split('\\?');
                URLValue = urlValues[0] + '?';
                Integer i = 0;
                for (String urlVal : urlValues[1].split('\\&'))
                {
                    
                    if (isMemberCase)
                    {
                        urlVal = urlVal.replace('V1','Member');
                        urlVal = urlVal.replace('V2','');
                        if(memberNumber != null)
                        {
                            urlVal = urlVal.replace('V3', (String.isnotblank(memberNumber.Name)) ? membernumber.Name: '');
                            urlVal = urlVal.replace('V4', (String.isNotBlank(memberNumber.Member_Dependent_Code__c)) ? memberNumber.Member_Dependent_Code__c : '');
                        }
                        else
                        {
                            urlVal = urlVal.replace('V3','');
                            urlVal = urlVal.replace('V4','');
                        }
                        urlVal = urlVal.replace('V5', (String.IsnotBlank(CaseRec.Policy_Member__r.Policy_Platform__c)) ? CaseRec.Policy_Member__r.Policy_Platform__c : '');
                        urlVal = urlVal.replace('V6', (string.IsNotBlank(CaseRec.Policy_Member__r.Group_Number__c)) ? CaseRec.Policy_Member__r.Group_Number__c : '');

                    }
                    else if(isMemberCase == false)
                    {
                        urlVal = urlVal.replace('V1', 'Other');
                        urlVal = urlVal.replace('V2', 'Unknown CRM Member');
                        urlVal = urlVal.replace('V3','');
                        urlVal = urlVal.replace('V4','');
                        urlVal = urlVal.replace('V5','');
                        urlVal = urlVal.replace('V6','');
                    }
                    urlVal = urlVal.replace('V7', (String.isNotBlank(CaseRec.CaseNumber)) ? CaseRec.CaseNumber : '');
                     if (i != 0)
                     {
                         URLValue = URLValue + '&' + urlVal; 
                     }
                     else
                     {
                        URLValue = URLValue + urlVal; 
                     }
                      i++;             
                }
                launchEmmeURL = URLValue;
        }
    }
}