/*******************************************************************************************************************************
Apex Class Name : HUMNewCaseredirectExtension 
Version         : 1.0
Created On      : 05/21/2014
Function        : This class saves case when new Case button is clicked and redirects to edit page of that case.
                  This class pre populates Interaction related details on Case edit layout.
Test Class      : HUMNewCaseRedirectExtensionTest
                  
Modification Log: 
* Developer Name                        Code Review                           Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
    Sagar Tapadia                          17093                             05/21/2014                 Original Version
    Manuel Johnson                         17093                             06/06/2014                 Commented out the setting of GA/Complaints
    Sagar Tapadia                          17093                             06/07/2014                 Removed Setting of GA/Complaints
    Sagar Tapadia                          17093                             06/09/2014                 Added Page Messages to show Validation errors/Also removed SOQL for logged in user   
    Ninad Patil                            17093                             06/19/2014                 Changes done for populating interacting ABout type on case layout.   
    Sagar Tapadia                          17093                             06/21/2014                 Making private variables test visible.   
    Sagar Tapadia                          17093                             06/21/2014                 Added Exception Handling
    SuryaKumari Medicherla                 17093                             06/25/2014                 Added a method to set default RecordType for Cases based on account recordtype
    SuryaKumari Medicherla                 18032                             09/18/2014                 Added a Logic to avoid case creation with out being in queue Defect:154410 
    SuryaKumari Medicherla                 18257                             09/30/2014                 Added lines of code to assign values to Interaction About Type when user trying to create a case from account,Defect:156164
    SuryaKumari Medicherla                 18446                             10/09/2014                 Added variable to display new cases in different tabs,Defect:157175,157474
    Mrudula Jaddu                          18935                             11/03/2014                 Case origin Value from interaction record added
    Mrudula Jaddu                          19591                             11/27/2014                 REQ - 72764. Case Redirection to view page.
    Vishal Verma                           19911                             12/11/2014                 The redirection is being done using javascript so in the Save method the return tyoe should be null;
    SuryaKumari Medicherla                 19911                             12/12/2014                 Code modified to replace custom settings with custom object (REQ-72809)
    Sagar Tapadia                          20195                             01/05/2015                 Defect Fix:165404 - As code got overridden, so added fix related to this defect by taking latest code from QAS.
    Sagar Tapadia                          20649                             01/30/2015                 Default Interacting About Type to Member during Chat.
    Sagar Tapadia                          20649                             02/03/2015                 Updated Code as per review comments. Moved Hard Coded values to custom settings and custom labels.
    Sagar Tapadia                          20890                             02/12/2015                 Defect fix : 170495.0001 - Added a check to see if interaction id is present or not in URL.
    Manish Kumar                           27139                             10/14/2015                 Fixed creation of duplicate Case from Recent tab issue. Code modified throughtout the class.
    Kritika P Popat                        28219                             11/20/2015                 Worked on REQ 161520 for Cases related to Group Policy 
    Manish Kumar                                                             01/07/2016                 Fixed defect#211500.0001, 212554.0001, 212524.0001
    Manish Kumar                           35879                             08/16/2016                 Made the changes for New Case creation.
    Sharan Shanmugam                       47072                             11/07/2016                 Added logic in assignFixedValuesToCase method to pick only active queues.
    Sharan Shanmugam                       50226                             11/30/2106                 Implemented REQ - 290990 -  removed all lookup related fields from 
                                                                                                        Case/Task/Interaction/Attachment to Work Queue Setup and Queue View
    Pallavi R                                                                03/21/2017                 REQ 30973 - Case data model changes
    Vandana Chaudhari  			 	       220488         				     04/09/2018         	    REQ - 355781 CR788: Web Chat Auto Create Case (CMO CR5548)  
    Sumit Sharma  			 	                         				     05/02/2018         	    REQ - 355781 - Auto Case creation from web chat defect fixed
    Anurag Shah                            267634							 07/20/2018             	REQ - 371243 - Adding the �Queue Selection� fields to the Person Account/ Policy Member pages
    Suresh Sunkari                                                           07/08/2019                 REQ - 402831 -  Assigning the owner Queue depending on the assigned permissionsets and queues.
	Kiran Kumar																 06/26/2020					REQ - 941987: Automatically Transfer Case When 'Invoice Request' Guided Process Attached (RxRF)
    Kiran Kumar Bhuvanagiri	 						  						 11/03/2020					User Story - 815259: Automatically Record Credit Card Updates to Case: Add Logging Component to 'Edit Credit Card' Pop-up (RxRF)
	Kiran Bhuvanagiri										 				 09/22/2020					User Story - 1445907: Update 'Created by Queue', 'Last Modified By Queue' and Case Record Type for PDP Pilot Users (CRMS)
    Kiran Kumar																 06/03/2021				    User Story - 2316079: Switch Clean Up - Update 'Created by Queue', 'Last Modified By Queue' and Case Record Type for PDP Pilot Users (CRMS)
        Mohammed Noor                                                            07/01/2021                 User Story - 2344857: Fix CheckMarx errors
    Prasuna Pattabhi                                                         08/19/2021                 User Story - 2590404 Checkmarx Issue
	Ashok Kumar Nutalapati            										 09/30/2021            		REQ - 2593758 T1PRJ0002517 - MF 2 - New case button, create case, & open new case in a subtab (RXRF) (ID# 35b)
	AshokKumar Nutalapati                         	 						 01/10/2022                 REQ - 2843422 T1PRJ0002517 - MF 1 - Inactivate Rx: Process Content (RxRF) (ID# 163)
    Aaron Speakman                                                           02/17/2022                 DF-4488 Fix - Specialist w/206 permissions - Case Record Type not displaying as HP case
    AshokKumar Nutalapati		                			   				 02/15/2022				    User Story 2934951: T1PRJ0200895 - MF 1 - Inactivate Rx: Create New Case (CRMS) (ID# 163)
***********************************************************************************************************************************************/
public with sharing class HUMNewCaseRedirectExtension 
{
    private String sCurrentWorkQueue ='';    
    private String sCaseRecordTypeName = '';
    private String sAccRecordTypeName = '';
	@TestVisible private String sCurrentQueue = '';
	private String sQueueHistory = '';
    @TestVisible private Interaction__c ofetchinteraction = NULL;
    @TestVisible private final Case objCase = NULL;
    @testVisible private Id accRecordTypeId = NULL;
    @testVisible private Id caseRecordTypeId = NULL;
    
    /*
     * Method Name : HUMNewCaseredirectExtension
     * Description : Constructor for the class , Initialize variables
     * Return type : N/A
     * Paramater   : StandardController
     */
    public HUMNewCaseRedirectExtension(ApexPages.StandardController controller) 
    {
        sCurrentQueue = [select Current_Queue__C from User where id =: UserInfo.getUserId()].Current_Queue__C;
        sQueueHistory = sCurrentQueue;
		if (ApexPages.currentPage().getParameters().keySet().contains(HUMConstants.HUMObjID)) 
        {
            objectID = ApexPages.currentPage().getParameters().get(HUMConstants.HUMObjID);
            sObjectName = objectID.getSobjectType().getDescribe().getName();
            objCase = new Case();
        }			
        else if(NULL == controller)
        {
            objCase = new Case();
        }
        else    
        {
            objCase = (Case) controller.getRecord();
        }
    }
	
    /*
     * Method Name : HUMNewCaseredirectExtension
     * Description : Constructor for the class , Initialize variables
     * Return type : N/A
     * Paramater   : StandardController, Pharmacy Logging variable
     */
	
	 public HUMNewCaseRedirectExtension(ApexPages.StandardController controller, Boolean isPharmacyLogging) 
    {
        sCurrentQueue = [select Current_Queue__C from User where id =: UserInfo.getUserId()].Current_Queue__C;
        sQueueHistory = sCurrentQueue;
		checkPermissionandQueue();
        if (ApexPages.currentPage().getParameters().keySet().contains(HUMConstants.HUMObjID)) 
        {
            objectID = ApexPages.currentPage().getParameters().get(HUMConstants.HUMObjID);
            sObjectName = objectID.getSobjectType().getDescribe().getName();
            objCase = new Case();
        }           
        else if(NULL == controller)
        {
            objCase = new Case();
        }
        else    
        {
            objCase = (Case) controller.getRecord();
        }
    }
	
	/* 
	 * Method Name : HUMNewCaseredirectExtension
	 * Description : Constructor for the class , Initialize variables
	 * Return type : N/A
	 * Paramater   : StandardController, Pharmacy Logging variable, Address Varialbe
	 */

	 public HUMNewCaseRedirectExtension(ApexPages.StandardController controller, Boolean isPharmacyLogging, Boolean isFromAddress) 
	 {
		 sCurrentQueue = [select Current_Queue__C from User where id =: UserInfo.getUserId()].Current_Queue__C;
		 sQueueHistory = sCurrentQueue;
		 checkPermissionandQueue();
		 if(NULL == controller)
		 {
			 objCase = new Case();
		 }
		 else    
		 {
			 objCase = (Case) controller.getRecord();
		 }
	 }
    
    public Id objectID = NULL;
    public String sStandardUrlForTab = '';
    public String sObjectName = '';
    public String caseID { get; set; }
    public String sCaseNumber { get; set; }
    public String sRedirectURL { get; set; }

    /*
     * Method Name : funcOnPageLoad
     * Description : This method is used to assign recordtype id for case if it is created from account
     * Return type : void
     * Paramater   : N/A
     */ 
    public void funcOnPageLoad()
    { 
        if(objectID != NULL)
        {		
            Boolean bExists = false;
            String sParentObjId = '';
            Map<ID, Schema.RecordTypeInfo> mapRecordType = Schema.SObjectType.Account.getRecordTypeInfosById();
            Map<String, HUMCaseInteractionAboutType__c> mapCaseInterAboutType = HUMCaseInteractionAboutType__c.getAll();
            Map<String, HUMCaseNPI_TAXID__c> mapCaseNPIAndTaxId = HUMCaseNPI_TAXID__c.getAll();
            List <Policy__c> lstPolicy = new List<Policy__c>();
            List <Policy_Member__c> lstPolicyMem = new List<Policy_Member__c>();
            Account oAccount = NULL;
            
            if ((sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_ACCOUNT)) || (sObjectName.equalsIgnoreCase(HUMConstants.HUM_Object_Policy)) || (sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_POLICYMEMBER)))  
            {
                if(sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_ACCOUNT))
                    oAccount = [select RecordtypeId, NPI_ID__c from Account where Id = :ESAPI.encoder().SFDC_HTMLENCODE(objectID)];
                
                if(sObjectName.equalsIgnoreCase(HUMConstants.HUM_Object_Policy))
                {
                    objCase.Policy__c = objectID;
                    lstPolicy = [Select Group_Name__r.Id from Policy__c where ID =: ESAPI.encoder().SFDC_HTMLENCODE(objectID)];
                    if(!lstPolicy.isEmpty())   sParentObjId = lstPolicy[0].Group_Name__r.Id;
                    oAccount = [select RecordtypeId, NPI_ID__c from Account where Id = :sParentObjId]; 
                }
                
                if(sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_POLICYMEMBER))
                {
                    objCase.Policy_Member__c = objectID;
                    lstPolicyMem = [Select Member__r.Id from Policy_Member__c where ID =: ESAPI.encoder().SFDC_HTMLENCODE(objectID)];
                    if(!lstPolicyMem.isEmpty())   sParentObjId = lstPolicyMem[0].Member__r.Id;
                    oAccount = [select RecordtypeId, NPI_ID__c from Account where Id = :sParentObjId]; 
                }
                
                accRecordTypeId = oAccount.RecordtypeId;
                sAccRecordTypeName = (NULL != mapRecordType.get(accRecordTypeId)) ? mapRecordType.get(accRecordTypeId).getName() : NULL;
                caseRecordTypeId = HUMUtilityHelper.getCaseRecordType(sAccRecordTypeName);    
                objCase.Interacting_About_Type__c = mapCaseInterAboutType.get(sAccRecordTypeName).CaseInteraction_About_Type__c; 
                for(String sCaseNPITax : mapCaseNPIAndTaxId.keySet())
                {
                    if(!String.isBlank(sCaseNPITax) && sCaseNPITax.equalsIgnoreCase(sAccRecordTypeName))
                    {
                        bExists = true; 
                        break;               
                    }
                }
                        
                if(bExists)
                {
                    objCase.NPI_ID__c = oAccount.NPI_ID__c;
                    List<ConsumerID__c> lstConsumerId = NULL;
                    try
                    {
                        if (sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_ACCOUNT))
                        {
                            lstConsumerId = [select Consumer_ID__c from ConsumerID__c where Account__c =: ESAPI.encoder().SFDC_HTMLENCODE(objectID)];
                        }     
                        else if(sObjectName.equalsIgnoreCase(HUMConstants.HUM_Object_Policy))
                        {
                            lstConsumerId = [select Consumer_ID__c from ConsumerID__c where Account__c =: sParentObjId];
                        }
                        if(!lstConsumerId.isEmpty())    objCase.Tax_ID__c = lstConsumerId[0].Consumer_ID__c;
                    }
                    catch (Exception ex) 
                    {
                        HUMExceptionHelper.logErrors(ex, 'HUMNewCaseRedirectExtension', 'funcOnPageLoad'); 
                    }
                }
            }                                      
        } 
    }

    /*
       * Method Name : saveCase
       * Description : This method is used to create a case on click of New Button 
                        and then redirect to edit page of created case.
       * Return type : Pagereference
       * Paramater   : N/A
     */
    public void saveCase() 
    {
        try
        {           
            if(!String.isBlank(sCurrentQueue))
            {
                assignValuesToCase();
                List<Case> lstCase = new List<Case>{objCase};
                List<Database.SaveREsult> lstSaveResults = Database.insert(lstCase, false);
                
                boolean bIsError = HUMExceptionHelper.processSaveResults(lstSaveResults, lstCase, 'HUMNewCaseRedirect', 'saveCase', 'Case');
                caseID = objCase.Id;
                sCaseNumber = objCase.CaseNumber;
                ApexPages.currentPage().getParameters().put('sCaseNumber', ESAPI.encoder().SFDC_HTMLENCODE(objCase.CaseNumber));
                ApexPages.currentPage().getParameters().put('sCaseId', ESAPI.encoder().SFDC_HTMLENCODE(objCase.Id));
                sRedirectURL = NULL;                
            } 
            else
            {
                buildRedirectURL();
            }
            ApexPages.currentPage().getParameters().put('sRedirectURL', ESAPI.encoder().SFDC_HTMLENCODE(sRedirectURL));                
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMNewCaseRedirect', 'saveCase');
        }
    }
    
    /*
    * Method Name : buildRedirectURL
    * Description : This method is used to redirect to queue selection page.
    * Return type : void 
    * Paramater   : NA
    */
    private void buildRedirectURL()
    {
        sRedirectURL = HUMConstants.HUMApexinURL + HUMConstants.HUMQueueSelectionPage;
        sRedirectURL += '?' + HUMConstants.HUMObjectType + '=' + HUMConstants.OBJECT_CASE;
        sRedirectURL += '&' + HUMConstants.HUMParentObjectId + '=' + objectID;
        
        if(NULL != objectID && sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_POLICYMEMBER))
        {           
            sRedirectURL += '&' + HUMConstants.HUMActionType + '=' + HUMConstants.HUMQueueYesFromCaseHistory;
        }
        else if(NULL != objectID && (sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_ACCOUNT)|| sObjectName.equalsIgnoreCase(HUMConstants.HUM_Object_Policy)))
        {
            sRedirectURL += '&' + HUMConstants.HUMActionType + '=' + HUMConstants.HUMQueueYesFromAccountRelatedList;
            sRedirectURL += '&' + HUMConstants.HUMcaseRecordType + '=' + caseRecordTypeId;  
            sRedirectURL += '&' + HUMConstants.HUMAccRecordTypeName + '=' + sAccRecordTypeName;  
        }
    }
        
    /*
       * Method Name : assignValuesToCase
       * Description : This method is used to assign values to case fields before creating a case.
       * Return type : void
       * Paramater   : NA
     */
    private void assignValuesToCase()
    { 
        String sAccountId = '';
        Interaction_Member__c oHUMIntMember = new Interaction_Member__c();
        List <Policy__c> lstPolicy = new List<Policy__c>();
        
        if (!String.isBlank(sStandardUrlForTab)) 
        {
            if(sStandardUrlForTab.indexOf(HUMConstants.HUMChatIdParameter) > -1)
            {
                objCase.Origin = HUMConstants.HUMWebChatText;
                objCase.Interacting_With_Type__c = HUMConstants.HUMSearchTypeMember;
                objCase.Interacting_About_Type__c = HUMConstants.HUMSearchTypeMember;               
            }
			else
            {
                if (sObjectName.equalsIgnoreCase(HUMConstants.HUM_Object_Policy))
                {
                    lstPolicy = [Select Group_Name__r.Id from Policy__c where ID =: sAccountId];
                    if(!lstPolicy.isEmpty())   sAccountId = lstPolicy[0].Group_Name__r.Id;
                }
                
                Integer iStartPos = sStandardUrlForTab.indexOf('&interactionId=');
                if (-1 != iStartPos) 
                {
                    if(!String.isBlank(sStandardUrlForTab.substring(iStartPos + 15)))
                    {
                        String sIntId = sStandardUrlForTab.substring(iStartPos + 15, iStartPos + 33);
                            
                        List <Interaction__c> lstInt = [Select id, Interacting_With__c, Interacting_With_type__c, Caller_Name__c,Interaction_Origin__c FROM Interaction__C Where id = : sIntId];
                        if (!lstInt.isEmpty()) 
                        {
                            ofetchinteraction = lstInt[0];
                            List<Interaction_Member__c> lstSelectedIntMembers = [select Interacting_About__c, Interacting_About_Type__c FROM Interaction_Member__c Where Interaction__c = : sIntId AND Interacting_About__c = :sAccountId];
                            
                           if(!lstSelectedIntMembers.isEmpty())
                          {
                                oHUMIntMember.Interacting_About_Type__c = lstSelectedIntMembers[0].Interacting_About_Type__c;
                                oHUMIntMember.Interacting_About__c = lstSelectedIntMembers[0].Interacting_About__c;
                            }
                        }
                    }
                }
            }
        }
        assignInteractionValuesToCase(oHUMIntMember);
        assignFixedValuesToCase();                               
    }
    
    /*
    * Method Name : assignInteractionValuesToCase
    * Description : This method is used to assign interaction values to case before creation of case.
    * Return type : Void
    * Paramater   : Interaction_Member__c 
    */
    private void assignInteractionValuesToCase(Interaction_Member__c oHUMIntMember)
    {
        if (sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_ACCOUNT)) 
        {
            objCase.AccountId = objectID;
            objCase.RecordTypeId = caseRecordTypeId;
            objCase.Interacting_With__c=objCase.AccountId;
        }
        else if (sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_POLICYMEMBER) )
        {
            objCase.Policy_Member__c = objectID;
            List<Policy_Member__c > lstPolicyMember;
            if(HUMUtilityHelper.getCurrentUserQueue() == GLOBAL_CONSTANT_HUM.PDP_Pilot_Pharmacy_Work_Queues){
                objCase.RecordTypeId = caseRecordTypeId;
            }

            lstPolicyMember = [Select Member__c From Policy_Member__c where Id =: ESAPI.encoder().SFDC_HTMLENCODE(objectID)];
            if(!lstPolicyMember.isEmpty() && NULL != lstPolicyMember[0].Member__c)
            {
                objCase.AccountId = lstPolicyMember[0].Member__c;
                objCase.Interacting_With__c = objCase.AccountId;
            }
        }
        else if (sObjectName.equalsIgnoreCase(HUMConstants.HUM_Object_Policy)) 
        {
            List <Policy__c> lstPolicy = [Select Group_Name__r.Id from Policy__c where ID =: ESAPI.encoder().SFDC_HTMLENCODE(objectID)];
            if(!lstPolicy.isEmpty())   
            {
                String sAccountId = lstPolicy[0].Group_Name__r.Id;
                objCase.AccountId = sAccountId;
                objCase.RecordTypeId = caseRecordTypeId;
                objCase.Interacting_With__c=objCase.AccountId;
            }
        }
        if (ofetchinteraction != null) 
        {
            objCase.Interacting_With_Type__c = ofetchinteraction.interacting_with_type__C;
            objCase.Interacting_With__c = ofetchinteraction.Interacting_With__c;
            objCase.Interaction_id__C = ofetchinteraction.id;
            objCase.Origin = ofetchinteraction.Interaction_Origin__c;
            objCase.Interacting_with_name__C = ofetchinteraction.Caller_name__c;
        }
       if(!String.isBlank(oHUMIntMember.Interacting_About_Type__c))
       {
            //populating interacting Member Type on Case edit fields.
            objCase.AccountID = oHUMIntMember.Interacting_About__c;
            objCase.Interacting_About_Type__c = oHUMIntMember.Interacting_About_Type__c;
        }
    }
    
    /*
    * Method Name : assignFixedValuesToCase
    * Description : This method is used to assign the fixed values to case fields before creating a case.
    * Return type : Void
    * Paramater   : N/A
    */
    private Void assignFixedValuesToCase()
    {
        objCase.Status = HUMConstants.CASE_STATUS;
        objCase.Type = HUMConstants.CASE_TYPE;
        objCase.Case_Owner__c = UserInfo.getName();
        objCase.Owner_Queue__c = sCurrentQueue;
		
		/* REQ 1445907 */
        if(sCurrentQueue != sQueueHistory){
            if(sCurrentQueue.equals(GLOBAL_CONSTANT_HUM.PDP_Pilot_Pharmacy_Work_Queues)){
                Id HpCaseRecord = [SELECT Id FROM RecordType WHERE Name = :GLOBAL_CONSTANT_HUM.HP_MEMBERCASE_HUM Limit 1].Id;
                if(HpCaseRecord != null){
                    objCase.RecordTypeId = HpCaseRecord;
                }
            }
            objCase.Created_By_Queue__c = sCurrentQueue;
            objCase.LastModifiedby_Queue__c = sCurrentQueue;
        } 
		
        sCurrentQueue = sCurrentQueue.replace(' ','_');
        
        if(objCase.Origin == null)  objCase.Origin = HUMConstants.CASE_ORIGIN;
        
        List <Work_Queue_Setup__c> lstWQS = [select Service_Center__c, Department__c, Classification_Type__c, Classification_Type__r.name from Work_Queue_Setup__c where Work_Queue_Name__c =: sCurrentQueue AND IsActive__c = TRUE];
        if(!lstWQS.isEmpty()) 
        {
            Work_Queue_Setup__c oWQS = lstWQS[0];
            objCase.Service_Center__c = oWQS.Service_Center__c; 
            objCase.Department__c =  oWQS.Department__c;  
            if(oWQS.Classification_Type__c!=null)         
            if(LoggingProcessLogic_S_HUM.bInactivateRx) {
				objCase.Classification_Type__c = GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_HUMANA_PHARMACY_CALLS;
			} else {
				objCase.Classification_Type__c = oWQS.Classification_Type__r.name;
			} 
            List<CTCI_Junction__c> listofCTCI = [select id,Classification__c,Intent__c,Classification__r.name,Intent__r.name from CTCI_Junction__c where Classification_Type__r.name =:objCase.Classification_Type__c];
            /* Changes for Classification & Intent for Invoice Request Logging */
            set<String> classificationTypes = new set<String>{'Humana Pharmacy Calls','Humana Pharmacy Web Chat','HP Finance Ops'};
            If(LoggingProcessLogic_S_HUM.bInvoiseRequest && classificationTypes.contains(objCase.Classification_Type__c)){
                for(CTCI_Junction__c ctci : listofCTCI){
                    if(objCase.Classification_Type__c == 'HP Finance Ops' && ctci.Classification__r.name == 'Billing (Finance Ops)' && ctci.Intent__r.name == 'Invoice Requests'){
                        objCase.CTCI_List__c = ctci.id;
                        objCase.Classification_Id__c = ctci.Classification__c;
                        objCase.Intent_Id__c = ctci.Intent__c;
                    }else if(ctci.Classification__r.name == 'Billing Issue (CT & TM)' && ctci.Intent__r.name == 'Invoice Requests'){
                        objCase.CTCI_List__c = ctci.id;
                        objCase.Classification_Id__c = ctci.Classification__c;
                        objCase.Intent_Id__c = ctci.Intent__c;
                    }
                }
            }
        /* US - 815259 Changes for applicable when Pharmacy Logging Component is used */
        set<String> pharmacyLoggingCTCITypes  = new set<String>{'Humana Pharmacy Calls','Humana Pharmacy Web Chat',
            'HP RxE Calls', 'HP Clinical Services', 'HP Finance Ops', 'Calls (RSO)', 'HP Specialty Calls'};
			if(String.isNotBlank( LoggingProcessLogic_S_HUM.sPolicyMemberId)){
				objCase.Policy_Member__c = LoggingProcessLogic_S_HUM.sPolicyMemberId; 
				
				
				if(LoggingProcessLogic_S_HUM.interactionRecord !=null){
					 objCase.Interacting_About_Type__c = LoggingProcessLogic_S_HUM.interactionRecord.Interacting_About_Type__c;
					 objCase.Interacting_With_Type__c = LoggingProcessLogic_S_HUM.interactionRecord.Interaction__r.Interacting_With_type__c;
					 objCase.Interacting_With_Name__c = LoggingProcessLogic_S_HUM.interactionRecord.Interaction__r.Caller_Name__c;
				}
				
				/* US - 815259 Changes for Setting Classification and Intent for Credit Card Logging */
				if(pharmacyLoggingCTCITypes.contains(objCase.Classification_Type__c)){
					for(CTCI_Junction__c ctci : listofCTCI){
						if(LoggingProcessLogic_S_HUM.bAddressLWC) {
							if(objCase.Classification_Type__c == 'Humana Pharmacy Calls' || objCase.Classification_Type__c == 'Humana Pharmacy Web Chat') {
								system.debug('ctci.Classification__r.name--'+ctci.Classification__r.name);
								system.debug('ctci.Intent__r.name-'+ctci.Intent__r.name);
								if(ctci.Classification__r.name == 'Member Profile/Account (CT & TM)' && ctci.Intent__r.name == 'Add/Update RX Demographics'){
									objCase.CTCI_List__c = ctci.id;
									objCase.Classification_Id__c = ctci.Classification__c;
									objCase.Intent_Id__c = ctci.Intent__c;
								}
							} else if(objCase.Classification_Type__c == 'HP RxE Calls') {
								system.debug('ctci.Classification__r.name--'+ctci.Classification__r.name);
								system.debug('ctci.Intent__r.name-'+ctci.Intent__r.name);
								if(ctci.Classification__r.name == 'Member Profile/Account TM' && ctci.Intent__r.name == 'Add/Update RX Demographics'){
									objCase.CTCI_List__c = ctci.id;
									objCase.Classification_Id__c = ctci.Classification__c;
									objCase.Intent_Id__c = ctci.Intent__c;
								}
							} else if(objCase.Classification_Type__c == 'HP Specialty Calls' || objCase.Classification_Type__c == 'Calls (RSO)') {
								system.debug('ctci.Classification__r.name--'+ctci.Classification__r.name);
								system.debug('ctci.Intent__r.name-'+ctci.Intent__r.name);
								if(ctci.Classification__r.name == 'HP Member Profile Account' && ctci.Intent__r.name == 'Add/Update RX Demographics'){
									objCase.CTCI_List__c = ctci.id;
									objCase.Classification_Id__c = ctci.Classification__c;
									objCase.Intent_Id__c = ctci.Intent__c;
								}
							}
						}  else if(LoggingProcessLogic_S_HUM.bInactivateRx) {
							if(objCase.Classification_Type__c == 'Humana Pharmacy Calls') {
								system.debug('ctci.Classification__r.name--'+ctci.Classification__r.name);
								system.debug('ctci.Intent__r.name-'+ctci.Intent__r.name);
								if(ctci.Classification__r.name == 'Clinical Services Outreach/Transfer (CT & TM)' && ctci.Intent__r.name == 'Inactivate Rx'){
									objCase.CTCI_List__c = ctci.id;
									objCase.Classification_Id__c = ctci.Classification__c;
									objCase.Intent_Id__c = ctci.Intent__c;
								}
							}
							if (LoggingProcessLogic_S_HUM.sClinicalTransfer == 'true') {
								if(LoggingProcessLogic_S_HUM.interactionRecord == null) {
									objCase.Interacting_With_Type__c = objCase.Interacting_About_Type__c;
								}
								objCase.Autoroute_Status__c = 'Inactivate Rx';
							}
						} else {
							if(objCase.Classification_Type__c == 'Humana Pharmacy Calls' || objCase.Classification_Type__c == 'Humana Pharmacy Web Chat') {
								if(LoggingProcessLogic_S_HUM.bOneTimeCC && ctci.Classification__r.name == 'Billing Issue (CT & TM)' &&  ctci.Intent__r.name == 'One Time Charge Request'){
									objCase.CTCI_List__c = ctci.id;
									objCase.Classification_Id__c = ctci.Classification__c;
									objCase.Intent_Id__c = ctci.Intent__c;
								}
								else if(!LoggingProcessLogic_S_HUM.bOneTimeCC && ctci.Classification__r.name == 'Member Profile/Account (CT & TM)' && ctci.Intent__r.name == 'Add/Updated Credit Card'){
									objCase.CTCI_List__c = ctci.id;
									objCase.Classification_Id__c = ctci.Classification__c;
									objCase.Intent_Id__c = ctci.Intent__c;
								}
							}
							else if(objCase.Classification_Type__c == 'HP Finance Ops' && ctci.Classification__r.name == 'Billing (Finance Ops)' && ctci.Intent__r.name == 'Make a Payment/Payment Changes'){
								objCase.CTCI_List__c = ctci.id;
								objCase.Classification_Id__c = ctci.Classification__c;
								objCase.Intent_Id__c = ctci.Intent__c;
							}else if( objCase.Classification_Type__c ==  'HP Clinical Services'){
								if(LoggingProcessLogic_S_HUM.bOneTimeCC && ctci.Classification__r.name == 'Billing' &&  ctci.Intent__r.name == 'One Time Charge Request'){
									objCase.CTCI_List__c = ctci.id;
									objCase.Classification_Id__c = ctci.Classification__c;
									objCase.Intent_Id__c = ctci.Intent__c;
								}else if(!LoggingProcessLogic_S_HUM.bOneTimeCC && ctci.Classification__r.name == 'Member Profile' && ctci.Intent__r.name == 'Credit Card'){
									objCase.CTCI_List__c = ctci.id;
									objCase.Classification_Id__c = ctci.Classification__c;
									objCase.Intent_Id__c = ctci.Intent__c;
								}
								
							}
							else if(objCase.Classification_Type__c == 'Calls (RSO)' && ctci.Classification__r.name == 'HP Billing'){
								if(LoggingProcessLogic_S_HUM.bOneTimeCC && ctci.Intent__r.name == 'One Time Charge Request'){
									objCase.CTCI_List__c = ctci.id;
									objCase.Classification_Id__c = ctci.Classification__c;
									objCase.Intent_Id__c = ctci.Intent__c;
                                    if(sAccRecordTypeName != NULL && sAccRecordTypeName != ''){
                                        Id HpCaseRecordType = HUMUtilityHelper.getHPCaseRecordType(sAccRecordTypeName);
                                        if(HpCaseRecordType != NULL){
                                            objCase.RecordTypeId = HpCaseRecordType;
                                        }
                                    }
								}else if(!LoggingProcessLogic_S_HUM.bOneTimeCC && ctci.Intent__r.name == 'Add/Updated Credit Card'){
									objCase.CTCI_List__c = ctci.id;
									objCase.Classification_Id__c = ctci.Classification__c;
									objCase.Intent_Id__c = ctci.Intent__c;
								}
								
							}
							else if(!LoggingProcessLogic_S_HUM.bOneTimeCC && objCase.Classification_Type__c == 'HP RxE Calls' && ctci.Classification__r.name == 'Order Status TM' && ctci.Intent__r.name == 'Billing Payment'){
								objCase.CTCI_List__c = ctci.id;
								objCase.Classification_Id__c = ctci.Classification__c;
								objCase.Intent_Id__c = ctci.Intent__c;
							}
						}
						
					}
				} 
				
			}
            
        }
    
         
    }
    
    /*
    * Method Name : checkPermissionandQueue
    * Description : This method is used to assign queue value after checking the permissionset and queue assigned.
    * Return type : Void
    * Paramater   : N/A
    */  
    public void checkPermissionandQueue()
    {
        List<Group> lstgrp = [Select Id from Group where type = :GLOBAL_CONSTANT_HUM.GROUP_REGULAR_HUM and Name = :GLOBAL_CONSTANT_HUM.PDP_Pilot_PGQ];
        List<GroupMember> lstgrpmem = [Select UserOrGroupId From GroupMember where GroupId =:lstgrp[0].Id and UserOrGroupId IN (select Id from User where Id =: UserInfo.getUserId())];
        List<PermissionSetAssignment> psas = [ select Id from PermissionSetAssignment where PermissionSetId IN ( select Id from PermissionSet where Name = 'CRMS_205_CCSPDPPharmacyPilot') and AssigneeId IN (select Id from User where Id =: UserInfo.getUserId()) ];
        if(lstgrpmem.size() > 0 && psas.size() > 0)
        {
           sCurrentQueue = GLOBAL_CONSTANT_HUM.PDP_Pilot_Pharmacy_Work_Queues;
        }    
    }
}