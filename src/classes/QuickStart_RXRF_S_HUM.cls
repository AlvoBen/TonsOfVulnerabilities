/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_RXRF_S_HUM.cls 
Created Date     : May 05,2020
Function         : Executes SOQL in support of the Quick Start Console Component
Test Class       : QuickStart_T_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Himalay Patel							        05/13/2020					  REQ - 1076034 Quick Start File Isloation (CRMS)
* Kiran Kumar									08/25/2020				      User Story: 1129777 -  Humana Pharmacy Quick Start+ - Display Informational Message When Case in History with Same Classification (RxRF) 
* Kavya Bondada									01/05/2021				      User Story 1745160: PR00091574 - MF 1 - AHT - Ability to configure Info/Warning Message on Quick Start using Business Configuration (CRM) : Clean up of Is Auto Logging Enabled (Is_Auto_Logging_Enabled__c) from Quick Start Pretext (Quick_Start_Pretext__c)
************************************************************************************************************************************************************************************/
public class QuickStart_RXRF_S_HUM
{
	/*
	* Gets the User's Current Queue
	* 
	* @return string: User's Current Queue
	*/	
	public static String evaluateUserQueue()
	{
		String sUserQueue = QuickStart_RXRF_D_HUM.queryUserQueue();
		return sUserQueue;
	}
    
	/*
	* Gets the User's Current Queue
	* 
	* @param string: User's Current Queue 
	* @return Work_Queue_Setup__c: User's Current Work Queue
	*/
    public static Work_Queue_Setup__c populateUserQueue(string sQueueName)
    {
        Work_Queue_Setup__c oQueue = new Work_Queue_Setup__c();
        List<Work_Queue_Setup__c> lstQueriedQueue = QuickStart_RXRF_D_HUM.queryQueueByName(sQueueName);
        if(!lstQueriedQueue.isEmpty()) {
        	oQueue = lstQueriedQueue.get(0);
        }
        return oQueue;
    }
   
	/*
	* Assigns the Case's Classification and Intent fields from the values selected
	* 
	* @param Case: New Case to assign to
	* @param string: The selected Classification's ID
	* @param string: The selected Intent's ID
	* @param string: The selected CTCI's ID
	* @see If the save is successful, the selected values may be reflected on the Case Edit screen
	*/
    public static void assignCaseClassificationIntent(Case oNewCase, string sClassificationSelect, string sIntentSelect, String sIdCTCI)
    {	
		oNewCase.CTCI_List__c = sIdCTCI;
		oNewCase.Classification_Id__c = sClassificationSelect;
		oNewCase.Intent_Id__c = sIntentSelect;
    }  
    
	/*
	* Assigns the new Case's Queue-related values when associating Quick Start to a record
	* 
	* @param Case: New Case to assign to 
	* @param Work_Queue_Setup__c: User's Current Work Queue
	* @see The User's Work Queue values may be populated on the Case Edit page
	*/
    public static void assignCaseQueue(Case oNewCase, Work_Queue_Setup__c oUserWorkQueue)
    {
        oNewCase.Classification_Type__c = oUserWorkQueue.Classification_Type__r.Name;
        oNewCase.Service_Center__c = oUserWorkQueue.Service_Center__c;
        oNewCase.Department__c = oUserWorkQueue.Department__c;
    }    
    
	/*
	* Assigns the new Case's values when associating Quick Start to a Policy Member record
	* 
	* @param Case: New Case to assign to 
	* @param string: Record ID selected for association from Quick Start
	* @param string: User's current Queue 	 
	* @see Account, Policy Member and Interaction values may be populated on the Case Edit page
	*/
	public static void assignPolicyMemberValues(Case oNewCase, string sFocusedObjId, string sInteractionId, boolean bIsHPCase)
	{
		if(String.isNotBlank(sFocusedObjId))
		{
			List<Policy_Member__c> lstCurrentPolMem = QuickStart_RXRF_D_HUM.queryActivePolicyMember(sFocusedObjId);
			
			if(!lstCurrentPolMem.isEmpty())
			{
				oNewCase.Policy_Member__c =  lstCurrentPolMem[0].Id;
				String sActiveAccount = lstCurrentPolMem[0].Member__c;
				
				List<Account> lstCurrentAcct = QuickStart_RXRF_D_HUM.queryActiveAccount(sActiveAccount);
                
                if(bIsHPCase)
				{
                    oNewCase.RecordTypeId = HUMUtilityHelper.getHPCaseRecordType(lstCurrentAcct[0].RecordType.Name);
				}
				if(!lstCurrentAcct.isEmpty())
				{
					oNewCase.AccountId = lstCurrentAcct[0].Id;
				}
			}
			assignCaseInteraction(oNewCase,sInteractionId);
		}
		
		
		
	}    
	/*
	* Assigns the new Case's values when associating Quick Start to an Account record
	* 
	* @param oNewCase: New Case to assign to 
	* @param string: Record ID selected for association from Quick Start
	* @param string: User's current Queue 	 
	* @see Account and Interaction values may be populated on the Case Edit page
	*/
	public static void assignAccountValues(Case oNewCase, string sFocusedObjId, string sInteractionId, boolean bIsHPCase)
	{
		if(String.isNotBlank(sFocusedObjId))
		{
			List<Account> lstCurrentAcct = QuickStart_RXRF_D_HUM.queryActiveAccount(sFocusedObjId);
			if(!lstCurrentAcct.isEmpty())
			{
				Account oCurrentAccount = lstCurrentAcct[0];
				oNewCase.AccountId = oCurrentAccount.Id;
				
				String sAcctRecordTypeName = oCurrentAccount.RecordType.Name;
				if(bIsHPCase)
				{
                    oNewCase.RecordTypeId = HUMUtilityHelper.getHPCaseRecordType(sAcctRecordTypeName);
                }
				else
				{
					oNewCase.RecordTypeId = HUMUtilityHelper.getCaseRecordType(sAcctRecordTypeName);
				}
				oNewCase.NPI_ID__c = oCurrentAccount.NPI_ID__c;
				
				if(!oCurrentAccount.ConsumerIDs__r.isEmpty()) {
					oNewCase.Tax_ID__c = oCurrentAccount.ConsumerIDs__r[0].Consumer_ID__c;
				}
				assignDefaultValueforUnKnownMemberCase(oNewCase);	
			}
			assignCaseInteraction(oNewCase,sInteractionId);
		}
	}
	
	/*
	* Assign's the Case's Interaction-related values
	* 
	* @param Case: New Case to assign to
	* @param String: Current User's ID
	* @see Interaction values may be populated on the Case Edit page
	*/
	public static void assignCaseInteraction(Case oNewCase,string sInteractionId)
	{
		if(String.isNotBlank(sInteractionId))
		{
			List<Interaction_Member__c> lstActiveIntMember = QuickStart_RXRF_D_HUM.queryActiveIntMem(oNewCase.AccountId, sInteractionId);
			if(!lstActiveIntMember.isEmpty())
			{
				oNewCase.Interaction_ID__c = sInteractionId;
				oNewCase.Origin = lstActiveIntMember[0].Interaction__r.Interaction_Origin__c;
				oNewCase.Interacting_With_Type__c = lstActiveIntMember[0].Interaction__r.Interacting_With_Type__c;
				oNewCase.Interacting_With__c = lstActiveIntMember[0].Interaction__r.Interacting_With__c;
				oNewCase.Interacting_With_Name__c = lstActiveIntMember[0].Interaction__r.Caller_Name__c;
				oNewCase.Interacting_About_Type__c = lstActiveIntMember[0].Interacting_About_Type__c;			
			}			
		}
	}
	
	/*
	* Assigns general Case fields
	
	* 
	* @param Case: New Case to assign to
	* @param string: User's current Queue
	* @see Values assigned here may be populated on the Case Edit page
	*/
	public static void assignCaseDefaults(Case oNewCase, string sUserQueue)
	{
		oNewCase.Status = HUMConstants.CASE_STATUS;
		oNewCase.Type = HUMConstants.CASE_TYPE;
		oNewCase.Owner_Queue__c = sUserQueue;
		oNewCase.Case_Owner__c = UserInfo.getName();
		oNewCase.Origin = HUMConstants.CASE_ORIGIN;
		oNewCase.Call_Benefit_Category__c = null;
	}    

	
	
   /*
	* Assigns the Default Vales for Unknown Member Case for GA Rights Given and Compaint drop down values
	* 
	* @param Case: New Case to assign to
	*/	
	private static void assignDefaultValueforUnKnownMemberCase(Case oNewCase)
	{
		Map<ID, Schema.RecordTypeInfo> mapRecordType = Schema.SObjectType.Case.getRecordTypeInfosById();
		
		if(null != mapRecordType && null != mapRecordType.get(oNewCase.recordTypeId) && String.isNotBlank(mapRecordType.get(oNewCase.recordTypeId).getName())
		&& mapRecordType.get(oNewCase.recordTypeId).getName().containsIgnoreCase(HUMConstants.HUMUnknownCase))
		{
			if(String.isBlank(oNewCase.G_A_Rights_Given__c)) oNewCase.G_A_Rights_Given__c = HUMConstants.GA_RIGHTS_GIVEN;
			
			if(String.isBlank(oNewCase.Complaint__c)) oNewCase.Complaint__c = HUMConstants.COMPLAINT;
		}
	}				 		
	
	/*
	* Verifies that no other Cases for this Interaction have the same CTCI List value
	* 
	* @param Case: New Case to assign to
	* @return boolean: Returns true if a duplicate was found (negative scenario)
	* @see If true, Quick Start will display the associated page message
	*/
	public static boolean verifyNoDuplicClassIntent(Case oNewCase)
	{
        boolean bHasDuplicate = false;

		List<Case> lstExistingCases = QuickStart_RXRF_D_HUM.queryCasesByActiveInteraction(oNewCase);
		bHasDuplicate = !lstExistingCases.isEmpty();
		
        return bHasDuplicate;
	}
	
	/*
	* Verifies that no other Cases for this Classifiaction in las 14 days
	* 
	* @param Case: New Case to assign to
	* @return boolean: Returns true if a duplicate was found (negative scenario)
	* @see If true, Quick Start will display the associated page message
	*/
	public static boolean verifyNoDuplicClassification(Case oNewCase)
	{
        boolean bHasDuplicate = false;
		if(oNewCase.Interacting_With_Type__c == HUMConstants.MEMBER || oNewCase.Interacting_With_Type__c == HUMConstants.MEMBERCAREGIVER)
        {
		List<Case> lstExistingCases = QuickStart_RXRF_D_HUM.queryCasesByClassification(oNewCase);
		bHasDuplicate = !lstExistingCases.isEmpty();
        }
        return bHasDuplicate;
	} 
	   
	/*
	* Facilitates insertion of the Associated Case
	* 
	* @param Case: Case to insert
	* @return Boolean: Returns false if no errors occurred during the save
	* @see: The Case Edit page will open for the newly inserted Case
	*/
    public static String insertNewCase(Case oNewCase)
    {   
    	String sErrorDidNotSave = QuickStart_RXRF_D_HUM.insertAssociatedCase(oNewCase);
		return sErrorDidNotSave;
    }    

    
	/*
    * Method to fetch QS pretext data for Clasiffication and Intent combination
    * 
    * @param String: selected classification value
    * @param String: selected intent value
    * @param Id: Classification Type
    * @return List: Pretext data
    *
    * @see: Pretext layout on QS window
    */
    public static String fetchQSPretextData(String sClassification, String sIntent, Id idClassificationType) {
        List<Quick_Start_Pretext_Section__c> lstofPreTextSections = QuickStart_RXRF_D_HUM.queryPretextSectionData(sClassification,sIntent,idClassificationType);        
        String sPreTextdata = QuickStart_RXRF_H_HUM.parsePreTextData(lstofPreTextSections);
        return sPreTextdata;
    }
	
	/*
    * Method to get isAllowMultipleCase indicator
	* @param String: selected classification value
	* @param String: selected intent value
    * @param Id: Classification Type
    * @return Boolean
    *
    * @see: Pretext layout on QS window
    */
    public static Boolean isAllowMultipleCaseCI(String sClassification, String sIntent, Id idClassificationType) {
    	Boolean bAllowMultipleCaseCI = false;
    	List<Quick_Start_Pretext__c> lstAllowMultipleCI = QuickStart_RXRF_D_HUM.queryAllowMultipleCaseCI(sClassification, sIntent, idClassificationType);
	bAllowMultipleCaseCI = !lstAllowMultipleCI.isEmpty();
    	return bAllowMultipleCaseCI;
    }
    
    /*
    * Method to get Classification and Intent values for Call (RSO)
 	* @param String: selected ClassificationType for user queue 
    * @return Map<Id, ClassificationIntent_DTO>
    *
    * @see: Pretext layout on QS window
    */
    public static Map<String, QuickStartCI_DTO_HUM> getQueueClassificationIntent(String sQueueClassificationType) {
    	Map<String, QuickStartCI_DTO_HUM> mapQueueCI = new Map<String, QuickStartCI_DTO_HUM>();
    	Id sCurrentId;
    	Id sPreviousId;
    	Map<Id, QuickStartCI_DTO_HUM.Intent_DTO_HUM> mapIntentInfo = new Map<Id, QuickStartCI_DTO_HUM.Intent_DTO_HUM>();
    	CTCI_Junction__c ctciPrevious = null;
    	for(CTCI_Junction__c ctci : QuickStart_RXRF_D_HUM.queryCTCIByClassificationTypeCI(sQueueClassificationType)) {
    		sCurrentId = ctci.Classification__c;
    		if(String.isBlank(sPreviousId)) {
				mapIntentInfo.put(ctci.Intent__c, new QuickStartCI_DTO_HUM.Intent_DTO_HUM(ctci));
				sPreviousId = sCurrentId;
				ctciPrevious = ctci;
    		} else if(sCurrentId != sPreviousId) {
    			mapQueueCI.put(sPreviousId, new QuickStartCI_DTO_HUM(ctciPrevious, mapIntentInfo));
    			
    			mapIntentInfo = new Map<Id, QuickStartCI_DTO_HUM.Intent_DTO_HUM>();
    			ctciPrevious = ctci;
    			sPreviousId = sCurrentId;
    			mapIntentInfo.put(ctci.Intent__c, new QuickStartCI_DTO_HUM.Intent_DTO_HUM(ctci));
    		} else {
    			mapIntentInfo.put(ctci.Intent__c, new QuickStartCI_DTO_HUM.Intent_DTO_HUM(ctci));
    		}
    	}
    	if(ctciPrevious != null) {
    		mapQueueCI.put(sCurrentId, new QuickStartCI_DTO_HUM(ctciPrevious, mapIntentInfo));
    	}
    	
    	return mapQueueCI;
    }
    
    /*
    * Fetch all Mentor Link doc on Quickstart page load
    * @param Id: Classification Type
    * @param String: Business Group
    * @return Map<String, List<customLink__c>>
    */
    public static Map<String,List<customLink__c>> fetchAllMentorLink(Id idClassificationType, String sBusinessGroup){
        
        Map<String,List<CustomLink__c>> mapCIMentor= new Map<String,List<CustomLink__c>>();
        
        List<CustomLink__c> lstAllMentorLink = QuickStart_RXRF_D_HUM.queryAllCustomLink(idClassificationType,sBusinessGroup);
        String ClassificationName;
        String IntentName;
        for(CustomLink__c mentordoc : lstAllMentorLink)
        {
            
	        ClassificationName = mentordoc.Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification__r.Name;
	        IntentName = mentordoc.Quick_Start_Pretext__r.CTCI_List_lkp__r.Intent__r.Name;
                
	        if(string.isBlank(ClassificationName) && string.isBlank(IntentName))
	        {
	            ClassificationName = Constants_C_AHT_HUM.REFERENCE_DOCUMENT_CI;
	            IntentName = Constants_C_AHT_HUM.REFERENCE_DOCUMENT_CI;
	        }
                
	        if(mapCIMentor.containskey(ClassificationName+'-'+IntentName))
	        {
	            List<CustomLink__c> lstmentorlink = mapCIMentor.get(ClassificationName+'-'+IntentName);
	            lstmentorlink.add(mentordoc);
	            mapCIMentor.put(ClassificationName+'-'+IntentName,lstmentorlink);
	        }
	        else
	        {
	            mapCIMentor.put(ClassificationName+'-'+IntentName,new List<customLink__c> {mentordoc});
	        }
        }
        
        return mapCIMentor;
    }
    
    /*
    * Fetch Frequently Used Classification and Intent
    * @param Id: Classification Type
    * @return List<SelectOption>
    */
    public static List<SelectOption> getFrequentlyUsedCI(Id idClassificationType) {
    	List<SelectOption> lstFrequentlyUsedCIOption = new List<SelectOption>();
    	for(Quick_Start_Pretext__c qspFrequentlyUsedCI : QuickStart_RXRF_D_HUM.queryFrequentlyUsedCI(idClassificationType)) {
    		String strValue = qspFrequentlyUsedCI.CTCI_List_lkp__r.Classification__c + HUMConstants.sHiphan + qspFrequentlyUsedCI.CTCI_List_lkp__r.Intent__c;
    		String strLabel = qspFrequentlyUsedCI.CTCI_List_lkp__r.Classification__r.Name +' :: '+ qspFrequentlyUsedCI.CTCI_List_lkp__r.Intent__r.Name;
    		lstFrequentlyUsedCIOption.add(new SelectOption(strValue, strLabel));
    	}
    	
    	return lstFrequentlyUsedCIOption;
    }
    
    
	
	/*
     * Query UserRecordAccess to check record Access
     * @return boolean
     */
    public static boolean fetchRecordAccess(String recordId)
    {
        boolean bRecordAccess = false;
        List<UserRecordAccess> lstUserRecordAccess = QuickStart_RXRF_D_HUM.queryUserRecordAccess(recordId);
            
            if(!lstUserRecordAccess.isEmpty())
            {
                bRecordAccess = lstUserRecordAccess[0].HasReadAccess;
            }
        return bRecordAccess;
    }
    
     /*
     * check Pretext configured for current workqueue
     * @param Boolean
     * @return boolean
     */
    public static String  checkPreTextAndBusinessGroup(String sClassficationTypeId)
    {
        return  QuickStart_RXRF_D_HUM.getBusinessGroup(sClassficationTypeId);
    }
	
	/*
	* get Classification Type Name
	* @param Boolean
	* @return boolean
	*/
    public static String  getClassificationTypeName(String sClassficationTypeId)
    {
        return  QuickStart_RXRF_D_HUM.getClassificationName(sClassficationTypeId);
    }
	
	/*
	 * get cases created within 14 days
	 */
	public static List<DuplicatecaseWrapper> get14Dayscases(String sClassificationTypeName,String sClssificationId,String sAccid){
        Integer totalCaseNumber = 0;
        Integer count = 0;
        List<DuplicatecaseWrapper> listOfClassObj = new List<DuplicatecaseWrapper>();
        system.debug('sClassificationTypeName---'+sClassificationTypeName);
        List<Case> ListOfCase  = QuickStart_RXRF_D_HUM.check14daysDuplicateCase(sClassificationTypeName,sClssificationId,sAccid);
        if(!ListOfCase.isEmpty()){
            totalCaseNumber = ListOfCase.size();
            for(Case objCase :ListOfCase){
                count +=1; 
                DuplicatecaseWrapper clsObj = new DuplicatecaseWrapper();                
                clsObj.Title = totalCaseNumber == count ? objCase.CaseNumber:objCase.CaseNumber+',';                    
                clsObj.Id = objCase.Id;
                if(objCase.CaseComments.size() >0 ){
                    clsObj.lastcomment = objCase.CaseComments[0].CommentBody;
                    clsObj.commentdate = String.valueof(objCase.CaseComments[0].CreatedDate);
                }
                
                listOfClassObj.add(clsObj);
            }
        }
        return listOfClassObj;
    }
}