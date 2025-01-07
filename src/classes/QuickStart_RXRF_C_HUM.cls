/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_C_HUM.cls 
Created Date     : September 09,2019
Function         : Executes SOQL in support of the Quick Start Console Component
Test Class       : QuickStart_T_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Kiran Kumar									02/24/2020 				Original Version: REQ: 704823
* Kiran Kumar									04/17/2020				REQ - 735525 Add Humana Pharmacy Log Code to CTCI List Pages - Changing References
* Subhamay Mandal							    05/13/2020				User Story: 1083375RCC - RF build Fix in 6/19 release
* Kiran Kumar									05/13/2020				User Story: 1076034 -  Quick Start File Isolation (CRMS)
* Kavya Bondada									01/05/2021				User Story: 1745160: PR00091574 - MF 1 - AHT - Ability to configure Info/Warning Message on Quick Start using Business Configuration (CRM) : Clean up of Is Auto Logging Enabled (Is_Auto_Logging_Enabled__c) from Quick Start Pretext (Quick_Start_Pretext__c)
* Kiran Bhuvanagiri								01/17/2021				User Story: 1600241: Humana Pharmacy Quick Start+ - Display Informational Message When Case in History with Same Classification (CRMS)
* Himalay Patel			                        04/13/2021 			    User Story 2051142: T1PRJ0002517 -MF 1 - Auto-Select G&A Rights Given And Complaint Based on Service Model Type - Quick Start (CRMS) (ID# 137)
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
* Himalay Patel			                        06/07/2021 			    User Story 2140529: T1PRJ0002517 - MF 1 - TECH - Switch Clean Up - Auto-Select G&A Rights Given and Complaint Based on Service Model Type - Quick Start (CRMS) (ID# 137)
* Himalay Patel			                        06/08/2021 			    User Story 2140529: Build Fix
* AshokKumar Nutalapati                         07/12/2021              REQ - 2206208 T1PRJ0002517 - MF 1 - Update Humana Pharmacy Quick Start for HSP Users (CRMS) (ID# 143)
* Bhavani Tati                                  05/20/2022              User Story 2413145: T1PRJ0002517 - MF 5 - TECH - Switch Clean Up - Update Humana Pharmacy Quick Start for HSP Users (CRMS) (ID# 143)
************************************************************************************************************************************************************************************/
public with sharing Class QuickStart_RXRF_C_HUM 
{  
    Public Static final String NONPHARMACY_USER_HUM = 'No';
    Public Static final String CHANGECASEOWNER_CCSPECIALISTPROFILE_HUM = 'Customer Care Specialist';
    Public Static final String sCustomerCareSuperProfile = 'Customer Care Supervisor';
    Public Static final String sHumanaPharmacySpecProfile = 'Humana Pharmacy Specialist';
    Public Static final List<String> Pharmacy_Record_Type_Name = New List<String>{'Closed HP Agent/Broker Case','Closed HP Group Case','Closed HP Member Case','Closed HP Provider Case','Closed HP Unknown Case','HP Agent/Broker Case','HP Group Case','HP Member Case','HP Provider Case','HP Unknown Case'};
    Public Static final List<String> Owner_Createdby_Queue_Name = New List<String>{'Provider Payment Integrity','Louisville Other Insurance Investigation','Louisville RSO Oral Grievance Oversight','LV Medicare Member Communications'};
    Public Static final List<String> Owner_Createdby_Queue_Webchat = New List<String>{'Humana Pharmacy Web Chat','HP Clinical Services Web Chat'};
    Public Static final List<String> AUTOSELECTGA_SERVICEMODELTYPES = New List<String>{'Humana Pharmacy','Humana Specialty Pharmacy'}; 
    Public Static final String HUMNone = 'None';
    Public Static final String HUMNoneDisplay = '---None--';
    Public Static final String OBJECT_POLICYMEMBER = 'Policy_Member__c';
    Public Static final String sHiphan = '-';
    Public Static final String OBJECT_ACCOUNT = 'Account';
    /*
    * Constructor
    * 
    * @see Quick Start form with Classification dropdown values
    */ 
    public QuickStart_RXRF_C_HUM()
    {
        this.oNewCase = new Case();
        
        this.bErrorNoSave = false;
        this.bErrorDuplicClassIntent = false;
        this.bShowCaseCommentPanel = true;
        this.bRecordAccess = true;      

        this.sUserQueue = Quickstart_RXRF_S_HUM.evaluateUserQueue();
        this.bProviderUser = false;

        String sprofileName = [select name from profile where id = :userinfo.getprofileID() limit 1].name;

        if (String.isNotEmpty(this.sUserQueue))
        {
            this.oUserWorkQueue = Quickstart_RXRF_S_HUM.populateUserQueue(sUserQueue);
            idClassificationType = this.oUserWorkQueue.Classification_Type__c;
            
            string sClassificationName = Quickstart_RXRF_S_HUM.getClassificationTypeName(idClassificationType);
            String sBusinessGroup = Quickstart_RXRF_S_HUM.checkPreTextAndBusinessGroup(idClassificationType);
		    /* For REQ 1600241 */
			sClassificationTypeName = sClassificationName;
			
		    if(null != sBusinessGroup)
        	{
                bProviderUser = (sBusinessGroup.contains('Provider'));
            	bPreTextConfigured = true;
				this.sQSLabel = Constants_C_AHT_HUM.QUICKSTARTLABEL + ' ' +sClassificationName;
        	} else {
        		bPreTextConfigured = false;
                        this.sQSLabel = Constants_C_AHT_HUM.QUICKSTARTLABEL;
            }
            
            // End changes to set Quickstart label as Quickstart+ for pharamcy user.
            this.bErrorDuplicClassification = this.bProviderUser;    
            this.mapQueueCI = Quickstart_RXRF_S_HUM.getQueueClassificationIntent(this.oUserWorkQueue.Classification_Type__c);
            this.mapCIMentor = Quickstart_RXRF_S_HUM.fetchAllMentorLink(idClassificationType,sBusinessGroup);
            this.lstRFDocumentLink = this.mapCIMentor.get(Constants_C_AHT_HUM.REFERENCE_DOCUMENT_CI+'-'+Constants_C_AHT_HUM.REFERENCE_DOCUMENT_CI);        
        }
        // To identify pharmacy user
         bPharmacyuser = Pharmacy_H_HUM.isPharmacyUser() == NONPHARMACY_USER_HUM ? False : true;
        
        //Invoking below function to have list of log codes
         getCaseCommentOptions();
         bShow14DaysClassificationWaring = false; /* For REQ 1600241 */

        if (sClassificationTypeName == GLOBAL_CONSTANT_HUM.LOGCODEHSP_CLASSIFICATIONTYPE) {
            bHideLogPanel = true;
        } else {
            bHideLogPanel = false;
        }        
    }
    
    public Id idClassificationType;
	
	public static String sClassificationTypeName;
	    
    private Map<String,List<CustomLink__c>> mapCIMentor = new Map<String,List<CustomLink__c>>();
    private Map<String, QuickStartCI_DTO_HUM> mapQueueCI = new Map<String, QuickStartCI_DTO_HUM>();
    private Work_Queue_Setup__c oUserWorkQueue;
    private string sUserQueue;

    public Case oNewCase {get;set;}
    
    public Boolean bProviderUser{get; set;}
    
    public boolean bErrorDuplicClassification{get; set;}
    public boolean bErrorNoSave {get;set;}
    public boolean bErrorDuplicClassIntent {get;set;}
    public string  sErrorMessage {get;set;}
    
    public string sSavedCaseId {get;set;}    
    
    public string sClassificationSelect {get;set;}
    public string sIntentSelect {get;set;}

    public id sFocusedObjId {get;set;}
    public String sInteractionId {get;set;}
    
    public boolean bShowDocumentPanel {get;set;}
    public List<CustomLink__c> lstDocumentLink {get;set;}
    public List<CustomLink__c> lstRFDocumentLink {get;set;}
        
    public Boolean bShowCaseCommentPanel{get; set;}
    public String sPreTextdata{get;set;}
        
    public Boolean bRecordAccess {get; set;}
    public Boolean bPreTextConfigured {get; set;}
 
    public String sQSLabel {get;set;}
    public String sLogCode {get;set;}
    //req-404669 
    public Boolean bPharmacyUserEditAccess {get;set;}
    public Boolean blegacyDelete {get;set;}

    public Boolean bPharmacyuser {get;set;}
    /* For REQ 1600241 */
	Public static String sFocusedAccountId{get;set;}
    Public static String sSelectedClassiifcationId{get;set;}
    public static String sClassificationName{get;set;}
    public static List<String> ListOfCaseNumber{get;set;}
    public  static boolean bShow14DaysClassificationWaring{get; set;}
    public static List<DuplicatecaseWrapper> listOfClassObj{get;set;}
	public static boolean bSwitchHSP{get; set;}
	public static boolean bHideLogPanel{get; set;}
	
    //changes for pharmacy QS for log codes
    public list<SelectOption> getCaseCommentOptions()
    {
        List<SelectOption> lstSortBy = new list<SelectOption>();
        lstSortBy.add(new SelectOption('', ''));
		if(oUserWorkQueue !=null){
			for (String code : Pharmacy_H_HUM.fetchLogCode(oUserWorkQueue.Classification_Type__r.Name))
			{
				lstSortBy.add(new SelectOption(code, code));
			}
		}
        return lstSortBy;

    }
  
    /*
    * Get Classification drop-down options by User's Classification Type
    * 
    * @return List<SelectOption> with Classifications
    * @see Classification drop-down options
    */  
    public List<SelectOption> getLstClassificationOptions()
    {
        List<SelectOption> lstClassificationOptions = new List<SelectOption>();
        lstClassificationOptions.add(new SelectOption(HUMNone, HUMNoneDisplay));
        List<SelectOption> lstFrequentlyUserCIOption = Quickstart_RXRF_S_HUM.getFrequentlyUsedCI(idClassificationType);
        if(!this.bProviderUser && !lstFrequentlyUserCIOption.isEmpty()) {

            lstClassificationOptions.add(new SelectOption('', Constants_C_AHT_HUM.SELECT_FREQUENTLY_CI, true));         
            lstClassificationOptions.addAll(lstFrequentlyUserCIOption);
            lstClassificationOptions.add(new SelectOption('', Constants_C_AHT_HUM.SELECT_ALL_CLASSIFICATIONS, true));
        }
        for(String sKeyClassification : this.mapQueueCI.keySet())
        {
            QuickStartCI_DTO_HUM oClassificationIntent = this.mapQueueCI.get(sKeyClassification);
            lstClassificationOptions.add(new SelectOption(sKeyClassification, oClassificationIntent.sClassificationLabel));
        }
        return lstClassificationOptions;
    }
    
    /*
    * Gets Intent dropdown options by selected Classification
    * 
    * @return List<SelectOption> with Intents
    * @see Intent dropdown options
    */
    public List<SelectOption> getLstIntentOptions()
    {
        this.sIntentSelect = null;
        List<SelectOption> lstIntentOptions = new List<SelectOption>();
        lstIntentOptions.add(new SelectOption(HUMNone, HUMNoneDisplay));
        if(String.isNotBlank(this.sClassificationSelect) && this.sClassificationSelect.contains(sHiphan)) {
            String[] arrCI = this.sClassificationSelect.split(sHiphan);
            this.sClassificationSelect = arrCI[0];
            this.sIntentSelect = arrCI[1];
        }
        if(this.mapQueueCI.containsKey(this.sClassificationSelect)) {
            QuickStartCI_DTO_HUM oClassificationIntent = this.mapQueueCI.get(this.sClassificationSelect);
            if(oClassificationIntent != null) {
                for(String sKeyIntent : oClassificationIntent.mapIntentInfo.keySet())   {
                    lstIntentOptions.add(new SelectOption(sKeyIntent, oClassificationIntent.mapIntentInfo.get(sKeyIntent).sIntentLabel));
                }
            }           
        }
        return lstIntentOptions;
    }

    /*
    * Assigns the Case values, inserts the Case and provides the values needed by JavaScript to open the Case Edit page
    * 
    * @see After this method executes successfully, the Case Edit page will open and Quick Start will reset
    */
    public void assignCaseValues()
    {
        try
        {
            this.bRecordAccess = Quickstart_RXRF_S_HUM.fetchRecordAccess(this.sFocusedObjId);
            if(this.bRecordAccess)
            {
                if(String.isNotBlank(sClassificationSelect) && sClassificationSelect != HUMNone  && String.isNotBlank(sIntentSelect) && sIntentSelect != HUMNone) {
                
                    String sClassification = this.mapQueueCI.get(this.sClassificationSelect).sClassificationLabel;
                    String sIntent = this.mapQueueCI.get(this.sClassificationSelect).mapIntentInfo.get(this.sIntentSelect).sIntentLabel;
                    String sCTCIId = this.mapQueueCI.get(this.sClassificationSelect).mapIntentInfo.get(this.sIntentSelect).idCTCI;
                    
                    if(String.isNotBlank(sCTCIId)) {
                        resetFlags();
                        assignGeneralValues(sCTCIId);
                        assignValuesByObject();
                        runSaveRules(sClassification, sIntent);
                        saveCase();
                    } else {
                        this.sErrorMessage =  'Quick Start Invalid CTCI Combination for: '+ sClassification +' & '+ sIntent;
                        this.bErrorNoSave = true;
                    }
                } else {
                    this.sErrorMessage =  'Quick Start Invalid Selection '+sClassificationSelect+' & '+sIntentSelect;
                    this.bErrorNoSave = true;
                }
            } 
            else 
            {
                this.sErrorMessage =  Label.QUICKSTART_PERMISSIONMSG_HUM;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'QuickStart_C_HUM', 'assignCaseValues');
            bErrorNoSave = true;
        }           
    }
    /*
    * Resets error flags
    * 
    */  
    private void resetFlags()
    {
        this.bErrorNoSave = false;
        this.bErrorDuplicClassIntent = false;
    }
    
    /*
    * Performs general Case field assignments
    * 
    */
    private void assignGeneralValues(String sCTCIId)
    {
        Quickstart_RXRF_S_HUM.assignCaseDefaults(this.oNewCase, this.sUserQueue);
        Quickstart_RXRF_S_HUM.assignCaseQueue(this.oNewCase, this.oUserWorkQueue);
        Quickstart_RXRF_S_HUM.assignCaseClassificationIntent(this.oNewCase, this.sClassificationSelect, this.sIntentSelect, sCTCIId);
    }
    
    /*
    * Determine the object being association with and routing Case assignment accordingly
    * 
    */
    private void assignValuesByObject()
    {   
        String sSaveObjectType = sFocusedObjId.getSobjectType().getDescribe().getName();
        
        if(sSaveObjectType == OBJECT_POLICYMEMBER) {
            Quickstart_RXRF_S_HUM.assignPolicyMemberValues(this.oNewCase, this.sFocusedObjId, this.sInteractionId, false);
        } else if(sSaveObjectType == OBJECT_ACCOUNT) {
            Quickstart_RXRF_S_HUM.assignAccountValues(this.oNewCase, this.sFocusedObjId, this.sInteractionId, false);
        }
    }
    
    /*
    * Executes Quick Start specific server-side validation before inserting the Case
    * 
    */
    private void runSaveRules(String sClassification, String sIntent)
    {
        Boolean bisClaimsCall = Constants_C_AHT_HUM.CLAIM_CLASSIFICATION.equalsIgnoreCase(sClassification);
                       
        Boolean bAllowMultipleCase = Quickstart_RXRF_S_HUM.isAllowMultipleCaseCI(sClassification, sIntent, idClassificationType);
        
        if(!bisClaimsCall && !bAllowMultipleCase) {
            /* Condition not to execute for Pharmacy User for Duplicate classification and intent error. */         
                this.bErrorDuplicClassIntent = Quickstart_RXRF_S_HUM.verifyNoDuplicClassIntent(this.oNewCase);
            
        }  
    }       

    /*
    * Inserts the Case and provides the Case ID for JavaScript to open Case Edit
    * 
    * @see Success: the Case Edit page opens for the new Case; Failure: Quick Start displays a page message
    */ 
    
	private void saveCase()
    {   
        this.sErrorMessage = Quickstart_RXRF_S_HUM.insertNewCase(this.oNewCase);
        this.bErrorNoSave = String.isNotEmpty(this.sErrorMessage);
        if(!this.bErrorNoSave) {
                    //system.debug('Printing---this.oNewCase.Id----'+this.oNewCase.Id);
                    this.sSavedCaseId = this.oNewCase.Id;
                }
        
    }

	
    /*
    * Resets the Quick Start page
    * 
    * @return PageReference of new page
    * @see Quick Start refreshes
    */
    public PageReference reset()
    {
        PageReference oNewPage = new PageReference(system.currentPageReference().getUrl());
        oNewPage.setRedirect(true);
        return oNewPage;
    } 

    /*
    * Populates the Panel of Document Links by Classification and Intent
    * 
    * @see The Related Documents panel may appear
    */
    public void populateDocumentLinks()
    {
        try {
            this.lstDocumentLink = null;
            this.bShowDocumentPanel = false;
            this.sPreTextdata = null;
            this.bShowCaseCommentPanel = true;
            if(null != mapQueueCI && String.isNotBlank(sClassificationSelect) && sClassificationSelect != HUMNone  && String.isNotBlank(sIntentSelect) && sIntentSelect != HUMNone) {
                String sClassification = this.mapQueueCI.get(sClassificationSelect).sClassificationLabel;
                String sIntent = this.mapQueueCI.get(sClassificationSelect).mapIntentInfo.get(sIntentSelect).sIntentLabel;
            
                if(null != mapCIMentor &&  mapCIMentor.containskey(sClassification+'-'+sIntent)) {
                    
                    this.lstDocumentLink = this.mapCIMentor.get(sClassification+'-'+sIntent);
                    this.bShowDocumentPanel = !this.lstDocumentLink.isEmpty();
                }
                
                // check for pretext data
                this.sPreTextdata = Quickstart_RXRF_S_HUM.fetchQSPretextData(sClassification, sIntent, idClassificationType);
                this.bShowCaseCommentPanel = String.isBlank(sPreTextdata);

                /*Changes to auto populate Log Code */
                if(bPharmacyuser){
					list<CTCI_Junction__c> listOfCTCI = [SELECT id,Humana_Pharmacy_Log_Code__c FROM CTCI_Junction__c WHERE Classification__c = :sClassificationSelect AND Intent__c =:sIntentSelect AND Humana_Pharmacy_Log_Code__c !=null AND Classification_Type__c =:idClassificationType ];
                    sLogCode = listOfCTCI.size()>0 ? listOfCTCI[0].Humana_Pharmacy_Log_Code__c:'';
                }
                /* End log Code */ 
            }
        } catch(Exception e) {
            HUMExceptionHelper.logErrors(e, 'QuickStart_C_HUM', 'populateDocumentLinks');
            bErrorNoSave = true;
        }
    }
    
    
    /*
    * To check if the Account/Policy is Legacy delete
    * 
    * @return boolean value of Legacy delete by Kiran
    * 
    */
    /* Kiran changes for REQ 400170, AC 13 */
    @RemoteAction   
    public static String getAccountRXRF(id tabId, String sSelectedClasification, String sSelectedIntent) 
    {
        Boolean blegacyDelete = false;
        Id userProfileId = userinfo.getProfileId();
		Id CCSProfileId = [Select id from Profile where Name =: CHANGECASEOWNER_CCSPECIALISTPROFILE_HUM].id;
		Id CCSuprProfileId = [Select id from Profile where Name = :sCustomerCareSuperProfile].id;
		Id HPSProfileId = [Select id from Profile where Name = :sHumanaPharmacySpecProfile].id;
        List<String> lstHpRecordTypes = Pharmacy_Record_Type_Name;
        List<String> Owner_Createdby_Queue_Name = Owner_Createdby_Queue_Name;
        List<String> Owner_Createdby_Queue_Webchat = Owner_Createdby_Queue_Webchat;
        String sAccess = '';
        try{
            if(tabId!=null)
            {
               String sObjectType = tabId.getSobjectType().getDescribe().getName();
              
               if(sObjectType == 'Case') 
               {
                   List<UserRecordAccess> lstUserRecordAccess = [SELECT RecordId, HasReadAccess,HasEditAccess
                                                           FROM UserRecordAccess 
                                                           WHERE UserId =: userinfo.getUserId() 
                                                           AND RecordId =: tabId];
                   Case oCase = [select id, casenumber, status, product__c, RecordTypeId, RecordType.name, Owner_queue__c, Created_by_queue__c, origin, Policy_Member__c,Policy_Member__r.ETL_Record_Deleted__c, accountid,account.ETL_Record_Deleted__c, Classification_Id__r.Name, Intent_Id__r.Name from case where id = :tabId  limit 1];
                   if(!lstUserRecordAccess.isEmpty()){
                       if(!(lstUserRecordAccess[0].HasEditAccess)){
                           sAccess = 'NOACCESS';
                       }
                       else{
                           if(oCase != NULL){
                               /* REQ 400170 Kiran changes to fix error message for closed case and legacy delete account or policy*/
                               if(oCase.status == 'Closed' && (oCase.Policy_Member__r.ETL_Record_Deleted__c  || oCase.account.ETL_Record_Deleted__c)){
                                   sAccess = 'legacyDelete';
                                   return sAccess;
                               }
                              if((oCase.Classification_Id__r.Name != sSelectedClasification || oCase.intent_Id__r.Name != sSelectedIntent) && !(sSelectedClasification == HUMNoneDisplay && sSelectedIntent == HUMNoneDisplay)){
                                   sAccess = 'MismatchError';
                                   return sAccess;
                               }
                               if(sAccess != 'NOACCESS' && sAccess != 'legacyDelete' && (HPSProfileId != NULL && userProfileId == HPSProfileId)) {
                                    if(oCase.product__c == 'Med') {
                                        if(lstHpRecordTypes.contains(oCase.RecordType.Name)) {
                                            sAccess = 'hasAccess';
                                        }else if(!lstHpRecordTypes.contains(oCase.RecordType.Name) && ((Owner_Createdby_Queue_Name.contains(oCase.Owner_queue__c) || Owner_Createdby_Queue_Name.contains(oCase.Created_by_queue__c)) || ((Owner_Createdby_Queue_Webchat.contains(oCase.Owner_queue__c) || Owner_Createdby_Queue_Webchat.contains(oCase.Created_by_queue__c)) && oCase.origin == 'Web Chat'))){
                                            sAccess = 'hasAccess';
                                        }
                                    }else if(oCase.product__c != 'Med' && !string.isblank(oCase.product__c) && ((Owner_Createdby_Queue_Webchat.contains(oCase.Owner_queue__c) || Owner_Createdby_Queue_Webchat.contains(oCase.Created_by_queue__c)) && oCase.origin == 'Web Chat')){
                                        sAccess = 'hasAccess';
                                    }else if(string.isblank(oCase.product__c) && (lstHpRecordTypes.contains(oCase.RecordType.Name) || (!lstHpRecordTypes.contains(oCase.RecordType.Name) && ((Owner_Createdby_Queue_Webchat.contains(oCase.Owner_queue__c) || Owner_Createdby_Queue_Webchat.contains(oCase.Created_by_queue__c)) && oCase.origin == 'Web Chat')))){
                                        sAccess = 'hasAccess';
                                    }
                                }else if(sAccess != 'NOACCESS' && sAccess != 'legacyDelete' && (userProfileId == CCSProfileId || userProfileId == CCSuprProfileId)){
                                    if(UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess('CRMS_205_CCSPDPPharmacyPilot')){
                                        sAccess = 'hasAccess';
                                    }else{
                                        if(!lstHpRecordTypes.contains(oCase.RecordType.Name)) sAccess = 'hasAccess';
                                    }
                                }
                           }
                       }
                   }
                   if(sAccess == '') sAccess = 'ReadOnly';
               }
               /*RXRF- REQ-406750 - START */
               else if(sObjectType == OBJECT_POLICYMEMBER) 
               {
                   policy_member__c polMemb = [SELECT id,Name,ETL_Record_Deleted__c FROM Policy_Member__c where id = :tabId limit 1];
                   if(polMemb.ETL_Record_Deleted__c == true){
                       blegacyDelete = true;
                       sAccess = 'legacyDelete';
                   }                
               }
               /*RXRF- REQ-406750 - END */
             }   
             if(Test.isRunningTest()) {
                Integer i = 5 / 0;
            }
        }catch(Exception ex){
            // System.debug('Printing exception-getAccountRXRF--'+ex.getStackTraceString()+'message --'+ex.getMessage());
        }
        return sAccess;
    }
    
	/*changes For REQ- 1600241 14 Days warning Message*/
    public void  check14daysCase(){
        bShow14DaysClassificationWaring = false;
       String sClassificationTypeName = QuickStart_RXRF_S_HUM.getClassificationTypeName(QuickStart_RXRF_S_HUM.populateUserQueue(QuickStart_RXRF_S_HUM.evaluateUserQueue()).Classification_Type__c);
       ListOfCaseNumber = new List<String>();
	   listOfClassObj = QuickStart_RXRF_S_HUM.get14Dayscases(sClassificationTypeName,sSelectedClassiifcationId,sFocusedAccountId);
	   if(!listOfClassObj.isEmpty()){
	        bShow14DaysClassificationWaring = true;
       } 
        
    }
    @RemoteAction
    public static object checkServiceModelType(String classificationID, String intentID){
		QuickStartHPSpecialtyCalls_DTO_HUM oHSPUserDTO = new QuickStartHPSpecialtyCalls_DTO_HUM();
		oHSPUserDTO.ServiceModelType = '';
		oHSPUserDTO.GARights = false;
		if(classificationID != null && intentId != null){
			String sCurrentQueue = QuickStart_RXRF_S_HUM.evaluateUserQueue();

			if (String.isNotEmpty(sCurrentQueue))
			{
				Id classificationTypeId = QuickStart_RXRF_S_HUM.populateUserQueue(sCurrentQueue).Classification_Type__c;

				list<CTCI_Junction__c> listOfCTCI = [SELECT id, Service_Model_Type__c FROM CTCI_Junction__c WHERE Classification__c =:classificationID AND Intent__c =:intentID AND Classification_Type__c =:classificationTypeID];

				List<String> lstServiceModelTypes = GLOBAL_CONSTANT_HUM.AUTOSELECTGA_SERVICEMODELTYPES;
				String sServiceModelType = listOfCTCI.size()>0 ? listOfCTCI[0].Service_Model_Type__c:'';
				oHSPUserDTO.ServiceModelType = sServiceModelType;
				if(listOfCTCI.size() > 0){
					if (lstServiceModelTypes.Contains(sServiceModelType)) {
						oHSPUserDTO.GARights = true;
						return oHSPUserDTO;
					}else{
						return oHSPUserDTO;
					}
				}
				 else{
					return oHSPUserDTO;
				}
			} else {
				return oHSPUserDTO; 
			}
		} else{
			return oHSPUserDTO;
		}
	} 
	
}