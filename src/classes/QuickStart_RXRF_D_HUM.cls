/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_RXRF_D_HUM.cls 
Created Date     : May 05,2020
Function         : Executes SOQL in support of the Quick Start Console Component
Test Class       : QuickStart_T_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Himalay Patel							        05/13/2020					  REQ - 1076034 Quick Start File Isloation (CRMS)
* Kiran Kumar									08/25/2020				      User Story: 1129777 -  Humana Pharmacy Quick Start+ - Display Informational Message When Case in History with Same Classification (RxRF) 
* Kavya Bondada									01/05/2021				      User Story 1745160: PR00091574 - MF 1 - AHT - Ability to configure Info/Warning Message on Quick Start using Business Configuration (CRM) : Clean up of Is Auto Logging Enabled (Is_Auto_Logging_Enabled__c) from Quick Start Pretext (Quick_Start_Pretext__c)
* Aaron Speakman                                01/22/2021                    Checkmarx Sharing Rule Fix - 2/12 Release
* Kiran Kumar Bhuvanagiri						02/21/2022					  User Story 3123903: T1PRJ0200895 - MF 1 - Humana Pharmacy Quick Start+ - Ability to Make Pretext Line Items Mandatory (CRMS) (ID# 24)
************************************************************************************************************************************************************************************/
public with sharing class QuickStart_RXRF_D_HUM
{
    /*
    * Queries the user's current queue
    * 
    * @return string: The user's current queue
    */  
    public static string queryUserQueue()
    { 
       String sUserQueue = [SELECT Current_Queue__c 
                      		FROM User 
                      		WHERE Id = :UserInfo.getUserId()].Current_Queue__c;
        return sUserQueue;
    }
    
    /*
    * Queries the Work Queue from the User's Current Queue
    * 
    * @param string: The user's current queue
    * @return Work_Queue_Setup__c: User's current queue 
    */  
    public static List<Work_Queue_Setup__c> queryQueueByName(string sQueueName)
    {
        List<Work_Queue_Setup__c> lstQueriedQueue = [SELECT Id, Name, Classification_Type__c, Classification_Type__r.Name, Service_Center__c, Department__c
                                              FROM Work_Queue_Setup__c
                                              WHERE Name = :sQueueName LIMIT 1];
        return lstQueriedQueue;
    }

    /*
    * Queries CTCI Lists by Classification Type for Classification and Intent
    * 
    * @param string: The User's Queue's Classification Type ID
    * @return List<CTCI_Junction__c>: CTCI Lists with Classifications for the provided Classification Type
    */ 
	public static List<CTCI_Junction__c> queryCTCIByClassificationTypeCI(string sQueueClassificationType)
	{
		List<CTCI_Junction__c> lstCTCI = [SELECT id, Classification__c, Classification__r.Name, Intent__c, Intent__r.Name 
									      FROM CTCI_Junction__c 
										  WHERE Classification_Type__c = :sQueueClassificationType and IsActive__c = true order by Classification__r.Name, Intent__r.Name 
										  LIMIT 500];
		return lstCTCI;
	}	
    
    /*
    * Queries the Policy Member record when Quick Start Associates to a Policy Member
    * 
    * @param string: Policy Member ID for the record associated
    * @return Policy_Member__c: Policy Member for the associated record
    * @see: The queried record will appear on the Case Edit page
    */
    public static List<Policy_Member__c> queryActivePolicyMember(string sActivePolMemId)
    {
        List<Policy_Member__c> lstPolMem = [SELECT Id, Name, Member__c, ETL_Record_Deleted__c
					 FROM Policy_Member__c
					 WHERE Id = :sActivePolMemId LIMIT 1];

        if(!lstPolMem.isEmpty() && lstPolMem[0].ETL_Record_Deleted__c == true)
		{
            throw new HUMCustomException(Label.QUICKSTART_LEGACYDELMESSAGE_HUM);
        }
				

			

        return lstPolMem;
    }
    
    /*
    * Queries the Account for Quick Start's Associated record
    * 
    * @param string: Account ID for the related Account
    * @return Account: The Account related to Quick Start's association
    * @see: The queried record may appear on the Case Edit page
    */
    public static List<Account> queryActiveAccount(string sReferenceId)
    {
        List<Account> lstAcct = [SELECT Id, RecordTypeId, RecordType.Name, NPI_ID__c,ETL_Record_Deleted__c, (SELECT Consumer_ID__c FROM ConsumerIDs__r LIMIT 1)  
				   FROM Account
				   WHERE Id = :sReferenceId LIMIT 1];

        if(!lstAcct.isEmpty() && lstAcct[0].ETL_Record_Deleted__c == true)
		{
            throw new HUMCustomException(Label.QUICKSTART_LEGACYDELMESSAGE_HUM);
        }
        return lstAcct;
    }

    /*
    * Queries the Interaction Member for the User and Account
    * 
    * @param id: ID for the current Account 
    * @param string: ID of the User
    * @return Interaction_Member__c: Newest created Interaction Member for this Account and the User
    * @see The queried record may appear on the Case Edit page
    */
    
	public static List<Interaction_Member__c> queryActiveIntMem(Id idActiveAcct, string sInteractionId)
	{
		List<Interaction_Member__c> lstIntMem = [SELECT Interaction__r.Interacting_With_Type__c, Interaction__r.Interacting_With__c, Interaction__r.Caller_Name__c, Interaction__r.Interaction_Origin__c, Interacting_About_Type__c 
                     FROM Interaction_Member__c
                     WHERE Interacting_About__c = :idActiveAcct AND Interaction__c = :sInteractionId
                     ORDER BY CreatedDate DESC LIMIT 1];
        return lstIntMem;		
	}

    /*
    * Queries the Cases created during the Interaction for the specified Policy Member
    * 
    * @param Case: The new Case being processed
    * @return List<Case> lstExistingCases: Cases created for this Policy Member during the current Interaction
    */  
    public static List<Case> queryCasesByActiveInteraction(Case oNewCase)
    {
        List<Case> lstExistingCases = new List<Case>();

    	// If Interaction is not present we will not query for existing Cases, as within an interaction we want to avoid duplicate cases.
    	if(String.isNotBlank(oNewCase.Interaction_ID__c)) {
    		lstExistingCases = [SELECT Id, CTCI_List__c
                            	FROM Case
                            	WHERE Policy_Member__c = :oNewCase.Policy_Member__c AND 
                            		  Interaction_ID__c = :oNewCase.Interaction_ID__c AND 
                            		  AccountId = :oNewCase.AccountId AND
                            		  CTCI_List__c =: oNewCase.CTCI_List__c];
    	}
        return lstExistingCases;
    }   
    
    
    /*
    * Queries the Cases created during last 14days with same Classification
    * 
    * @param Case: The new Case being processed
    * @return List<Case> lstExistingCases: Cases created for this Policy Member during the current Interaction
    */  
    public static List<Case> queryCasesByClassification(Case oNewCase)
    {
    	String sNewCaseClassification = [select Classification__r.Name from CTCI_Junction__c where id =: oNewCase.CTCI_List__c].Classification__r.Name;
    	List<Case> lstExistingCases = [SELECT Id, CTCI_List__c 
                            	FROM Case
                            	WHERE Policy_Member__c = :oNewCase.Policy_Member__c AND AccountId = :oNewCase.AccountId AND
                            		  CTCI_List__r.Classification__r.Name =: sNewCaseClassification AND status != 'Cancelled' AND createddate = LAST_N_DAYS:14];
        return lstExistingCases;
    }    

    /*
    * Inserts the the Associated Case
    * 
    * @param Case: Case to insert
    * @return Boolean: Returns false if no errors occurred during the save
    * @see: The Case Edit page will open for the newly inserted Case
    */  
    public static String insertAssociatedCase(Case oNewCase)
    {
        String sHasSaveError = '';
        database.SaveResult oSaveSuccess = database.insert(oNewCase, false);
    	
    	if(!oSaveSuccess.isSuccess()) {
    		for(Database.Error err : oSaveSuccess.getErrors()) {
	            String sErroFieldLabels = '';
    			for(String sErrorField : err.getFields()) {
    				Schema.SObjectField sTemplabel = Constants_C_AHT_HUM.MAP_CASE_FIELDS.get(sErrorField);
    				if(sTemplabel != null) {
    					sErroFieldLabels += sTemplabel.getDescribe().getLabel() +', ';	
    				}
    			}
    			if(sErroFieldLabels.length() > 1) {
    				sErroFieldLabels = sErroFieldLabels.substring(0, sErroFieldLabels.trim().length() -1);
    			}
    			if(String.isNotBlank(sErroFieldLabels)) {
    				sHasSaveError += sErroFieldLabels +': '+ err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';
    			} else {
    				sHasSaveError += err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';	
    			}
	        }
    	}
        return sHasSaveError;
    }
    
    
	/*
    * Queries to get Pretext data from BCM based on classification and intent
    * @param string: User's selected Classification
    * @param string: User's selected Intent
    * @param Id: Classification Type
    * @return List<Quick_Start_Pretext_Section__c>: List of Pretext data
    */
   	public static List<Quick_Start_Pretext_Section__c> queryPretextSectionData(String sClassification, String sIntent, Id idClassificationType) {
        List<Quick_Start_Pretext_Section__c> lstPretextSection = [SELECT Transfer_Values_with_Input__c, Transfer_Values__c, Transfer_Label__c, Case_Comment_Section__c,
        															(SELECT id, Quick_Start_Pretext_Section__c, Pre_Label__c, Input_Field_Type__c, Input_Field_Labels__c, Ghost_Text__c, 
        																Field_Length__c, Field_Display_Size__c, Dependent_Input_Value__c, Additional_Line_Item__c,Display_Order__c, Display_Position__c, Is_Required_Line_Item__c
        															 FROM Quick_Start_Pretext_Line_Items__r 
        															 WHERE Additional_Line_Item__c = null 
        															 ORDER BY Display_Order__c, Display_Position__c asc) 
        														  FROM Quick_Start_Pretext_Section__c 
        														  WHERE Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification__r.Name =: sClassification AND Quick_Start_Pretext__r.CTCI_List_lkp__r.Intent__r.Name =: sIntent 
        														  		AND Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification_Type__c =: idClassificationType AND Quick_Start_Pretext__r.Is_Active__c = true
        														  		AND Quick_Start_Pretext__r.CTCI_List_lkp__r.IsActive__c = true 
        														  ORDER BY Case_Comment_Section__c desc];
        return lstPretextSection;
	}
	
    /*
    * Queries to multiple case creation flag for classification and intent
    * @param string: User's selected Classification
    * @param string: User's selected Intent
    * @param Id: Classification Type
    * @return List<Quick_Start_Pretext_Section__c>: List of Pretext data
    */
	public static List<Quick_Start_Pretext__c> queryAllowMultipleCaseCI(String sClassification, String sIntent, Id idClassificationType) {
		List<Quick_Start_Pretext__c> lstPTAllowMultipleCI = [SELECT Is_Allow_Multiple_Case__c 
																From Quick_Start_Pretext__c 
																WHERE CTCI_List_lkp__r.Classification__r.Name =: sClassification AND CTCI_List_lkp__r.Intent__r.Name =: sIntent AND CTCI_List_lkp__r.Classification_Type__c =: idClassificationType AND Is_Active__c = true AND Is_Allow_Multiple_Case__c = true LIMIT 1];
		
		return lstPTAllowMultipleCI;
	}
    
    /*
    * Query all Mentor Document on QuickStart page load
    * @param Id: Classification Type
    * @param String: Business Group
    * @return List<CustomLink__c,>: List of Pretext data
    */

    public static List<CustomLink__c> queryAllCustomLink(Id idClassificationType, String sBusinessGroup){
        
        Integer iValue = Integer.Valueof(HUMConstants__c.getInstance('HUMLimit1000').IntegerValue__c); 
         
        List<CustomLink__c> lstAllMentorLink = [SELECT Id,Name,Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification__r.Name,
                                                	Quick_Start_Pretext__r.CTCI_List_lkp__r.Intent__r.Name 
                                                FROM CustomLink__c 
                                                WHERE recordtype.developerName = 'Mentor_Link'
	                                                AND ((Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification_Type__c =: idClassificationType AND Quick_Start_Pretext__r.CTCI_List_lkp__r.IsActive__c = true)
	                                                OR (Quick_Start_Pretext__r.CTCI_List_lkp__c = NULL AND Quick_Start_Pretext__r.Business_Grp_Class_Type__c =: sBusinessGroup))
                                                    And Quick_Start_Pretext__r.Is_Active__c = true 
                                                ORDER BY Name 
                                                LIMIT : iValue];
     	return lstAllMentorLink;
    }
    
    /*
    * Query all Mentor Document on QuickStart page load
    * @param Id: Classification Type
    * @return List<CustomLink__c,>: List of Pretext data
    */
    public static List<Quick_Start_Pretext__c> queryFrequentlyUsedCI(Id idClassificationType){
        List<Quick_Start_Pretext__c> lstFrequentlyUsedCI = [SELECT id, CTCI_List_lkp__r.Classification__c, CTCI_List_lkp__r.Classification__r.Name, CTCI_List_lkp__r.Intent__c, CTCI_List_lkp__r.Intent__r.Name 
        													FROM Quick_Start_Pretext__c 
        													WHERE is_Frequently_Used__c = true AND Is_Active__c = true AND CTCI_List_lkp__r.Classification_Type__c =: idClassificationType AND CTCI_List_lkp__r.IsActive__c = true
        													ORDER BY CTCI_List_lkp__r.Classification__r.Name, CTCI_List_lkp__r.Intent__r.Name LIMIT 5];
     	return lstFrequentlyUsedCI;
    }
    
    /*
     * Query UserRecordAccess to check record Access
     * @return boolean
     */
     public static List<UserRecordAccess> queryUserRecordAccess(String recordId)
     {
         List<UserRecordAccess> lstUserRecordAccess = [SELECT RecordId, HasReadAccess 
                                                           FROM UserRecordAccess 
                                                           WHERE UserId =: userinfo.getUserId() 
                                                           AND RecordId =: recordId];
         return lstUserRecordAccess;
     }
     
     /*
     * Query Quick_Start_Pretext__c to get Business Group for the particular classfication type
     * @param String
     * @return String
     */
     public static String getBusinessGroup(String sClassficationTypeId){  
     	
     	String sbusinessGroup = null;
         
       	List<Quick_Start_Pretext__c> lstQuickStartPretext = [SELECT Business_Grp_Class_Type__c 
           	     											 FROM Quick_Start_Pretext__c 
                 											 WHERE CTCI_List_lkp__r.Classification_Type__c =: sClassficationTypeId AND Is_Active__c = true AND CTCI_List_lkp__r.IsActive__c = true and Business_Grp_Class_Type__c != ''  
                 											 LIMIT 1]; 
                 										
        if(!lstQuickStartPretext.isEmpty())    {
        	
        	sbusinessGroup = lstQuickStartPretext[0].Business_Grp_Class_Type__c;
        }
        
        return  sbusinessGroup;     										                                         
                                                                
     
     }
    /*
    * Query to get classfication type Name
    * @param String
    * @return String
    */
	public static String getClassificationName(String sClassficationTypeId){  
		
		String sClassificationType = null;
		List<Classification_Type__c> oClassificationType = [SELECT Id, Name, Is_Active__c FROM Classification_Type__c
															 where id = :sClassficationTypeId and Is_Active__c = true limit 1]; 
		if(!oClassificationType.Isempty()){
			sClassificationType = oClassificationType[0].Name;
		}
		return  sClassificationType;  
	}
	public static List<Case> check14daysDuplicateCase(String sClassificationName,String sClssificationId,String sAccid){
        Boolean isDuplicate = false;
        List<Case> lstExistingCases; 
        if(String.isNotBlank(sAccid)){
            lstExistingCases = [SELECT Id, CaseNumber, (SELECT CommentBody, CreatedDate FROM CaseComments ORDER BY LastmodifiedDate DESC LIMIT 1)
									   FROM Case WHERE CreatedDate >= LAST_N_DAYS:14 AND status != 'Cancelled' AND AccountId =:sAccid AND CTCI_List__r.Classification__c =:sClssificationId  AND Classification_Type__c =:sClassificationName ORDER BY LastmodifiedDate desc];
        }  
        return lstExistingCases;
    } 
}