/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_D_HUM.cls 
Created Date     : June 22, 2017
Function         : Executes SOQL in support of the Quick Start Console Component
Test Class       : QuickStart_T_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
* Amar Gadewar            -----                 06/22/2017                 Original Version
* Amar Gadewar									07/05/2017				   Existing Case Check fix for no interaction
* Amar Gadewar									08/09/2017				   REQ - 326209 - Add a Chatter Post for with Hash Tag - Quick Start for cases created from Quick Start
* Subhamay Mandal        165717                 11/09/2017                 REQ - 335028 Ability to prefill the quick start case comment fields based on the Classification and Intent selection (CRM)
* Amar Gadewar			 172615					01/19/2017				   REQ - 350502 Ability of a quick start to create a case on an existing interaction created by someone else(CRM)
* Amar Gadewar			 188566					02/23/2018				   REQ - 350501 - Error Message on Quick Start
* Subhamay Mandal		 					    02/25/2018 				   REQ - 351011 Ability to use Business Configuration model for Quick Start pretext for making changes within Quick Start pre-text (CRM)
* Piyusha Babar                                 02/26/2018  			   REQ - 354465 SF - TECH - Remove RSO:Quick Start Case report and Clean up Labels (CRM)
* Subhamay Mandal		   					  	03/01/2018				   REQ - 355820 Ability in Quick Start to allow CCS reps to create multiple cases on the single interaction for certain Classification and Intent (CRM)
* Subhamay Mandal		   					  	04/06/2018				   REQ - 359992 and REQ - 360019. UI change(plus and logo) and Performace improvement
* Darshan Sharma		   					  	04/27/2018				   REQ - 361223 Ability to update Mentor Links in QuickStart via Business Configuration (CRM)
* Subhamay Mandal							    05/17/2018				   REQ - 363405 SPLUNK Logs custom field validation error in Quick Start
* Subhamay Mandal								05/30/2018			       Defect 361102.0001 REQ - 363405 Error message displayed is not appropriate while associating the case to policy
* Subhamay Mandal								09/14/2018				   REQ - 371313 PCC - Adjust backend code to support PCC, RF and Non Rf users in QS
* Darshan Sharma			                    10/01/2018		           REQ - 374820: CTCI Model Update (RF+CRM)
* Asish   Behera								09/24/2018 		   		   REQ - 375046 Quality - Ability to display an Info Message at the top of QS if the Work Queue does not have Pretext (RF+CRM)
* Darshan Sharma								10/04/2018 		   		   REQ - 377121 Quality - Ability to disable the Case Creation in QS if the user does not have permissions to Create a Case for the Member in CRM (CRM)
* Darshan Sharma								11/30/2018				   REQ - 378965 PCC - Mentor Documents Set 1 (RF)
* Subhamay Mandal								01/17/2019				   REQ - 383735 AHT - Update - Ability to Identify if a Case with a Classification was created within the Past 14 Days in QS. (CRM)
* Subhamay Mandal								02/08/2019				   REQ - 383977	Quality - Ability to display the AutoLogging Message for additional Classification and Intents (CRM)
* Subhamay Mandal								03/15/2019  			   REQ - 389663 AHT - RSO - Add Frequently Used Classification and Intent to Classification Dropdown (CRM)
* Amar Gadewar									04/01/2019				   REQ - 388904 - AHT RSO Guidance Center Pretext (Set 1) (CRM)
* Subhamay Mandal								04/10/2019				   REQ - 389663 Defect 411139.0001 Fix.
* Amar Gadewar									04/11/2019				   REQ - 388904 - Check Against Active CTCI record
* Subhamay Mandal								07/29/2019				   REQ - 404543 SF TECH - Ability for a Business Configuration Owner to add Pretext when a new classification and intent is added (CRM)
* Harish Gowda							        	09/02/2019				   REQ - 406901 Quality - OGO - Ability to NOT allow a user to select BOTH the G&A Rights Given and Complaint dropdowns, in QUICKSTART, to Yes (CRM)
* Harish Gowda							        	09/03/2019				   REQ - 406902 Quality - OGO - Ability to auto select G&A Rights Given field in Quick Start for certain classification and intent combinations (CRM)
* Harish Gowda							        	09/04/2019				   REQ - 406903 Quality - OGO - Ability to auto select Complaint in Quick Start for certain classification and intent combinations (CRM)
* Harish Gowda							        	09/01/2019				   REQ - 409549 Quality - Add Informational Message above Callback Number (CRM)
* Kiran Kumar									09/09/2019 				   REQ: 397251: added methods for RxRf quickstart functioinality
* Prathyu Tushar					        	09/27/2019				   Quickstart label for Pharmville User to disply Classification Type Name.
* Kiran Kumar                                   11/19/2019                 REQ - 410397 Humana Pharmacy Quick Start+ Page - Pretext (RxRF)
* Amar Gadewar                                  12/2/2019                  REQ - 416714 SF Tech - Quick Start fine tuning for performance enhancements (CRM) 
* AshokKumar Nutalapati 				  	    03/11/2020                 REQ - 757004 Humana Pharmacy Quick Start+ Page - Pretext - Part 4 (CRMS)
* Subhamay Mandal        				  	    04/13/2020                 REQ - 1003198 QS 3.0 - Quality - Ability to make pretext line item mandatory in quick start (CRM)
* Subhamay Mandal                               05/12/2020                 1083375 PR00091574 - MF 1 - QS 3.0 - AHT - Ability to create task from Quick Start (CRM)
* Amar Gadewar                                  07/05/2020                 User Story 1247438: PR00091574 - MF 1 - SF - TECH - QS 3.0 Code Improvement and Knowledge cleanup (RF)
* Kavya Bondada                                 01/05/2021                 User Story 1745160: PR00091574 - MF 1 - AHT - Ability to configure Info/Warning Message on Quick Start using Business Configuration (CRM)
* Samantha Tennyson                             12/28/2020                 User Story 1745934: PR00091574 - MF 1 - Quality - Prevent Complaint/G&A Rights Given fields to be selected on Cases with specific Classification and Intent Combinations (CRM)
* Samantha Tennyson                             01/22/2021                 Adding With Sharing to fix Checkmarx issue
* Samantha Tennyson				                07/06/2021		           User Story 2454006: T1PRJ0001827 - MF 1 - AHT/Quality - QS 3.0 - Ability to display Pretext in Grievance and Appeals section based on Grievance/Appeal Reason selection (CRM)
* Amar Gadewar                                  07/12/2021                 User Story 2451271: T1PRJ0001827 - MF 1 - Quality/FCR - Ability to display "Recommended Actions" in Quick Start (CRM)
* Amar Gadewar                                  07/22/2021                 User Story 2498859: T1PRJ0001827 - MF 1 - AHT/Quality - Ability to display alert in Quick Start if the Rep is creating a case with potential duplicate grievance (CRM)
************************************************************************************************************************************************************************************/
public with sharing class QuickStart_D_HUM
{
    /*
    * Queries the user's current queue
    * 
    * @return string: The current User's Record
    */  
    public static User queryUserQueue()
    { 
       User oUser = [SELECT Current_Queue__c 
                            FROM User 
                            WHERE Id = :UserInfo.getUserId()];
       return oUser;
    }
    
    /*
    * Queries the Work Queue from the User's Current Queue
    * 
    * @param string: The user's current queue
    * @return Work_Queue_Setup__c: User's current queue 
    */  
    public static List<Work_Queue_Setup__c> queryQueueByName(string sQueueName)
    {
        List<Work_Queue_Setup__c> lstQueriedQueue = [SELECT Id, Name, Classification_Type__c, Classification_Type__r.Name, Service_Center__c, Public_Group_Name__c, Public_Group_Queue_Id__c, Department__c
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
                                                                        Field_Length__c, Field_Display_Size__c, Display_Order__c, Is_Required_Line_Item__c, Dependent_Input_Value__c, Display_Position__c, Additional_Line_Item__c 
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
                                                             WHERE CTCI_List_lkp__r.Classification_Type__c =: sClassficationTypeId AND Is_Active__c = true AND CTCI_List_lkp__r.IsActive__c = true and  Business_Grp_Class_Type__c != '' 
                                                             LIMIT 1]; 
                                                        
        if(!lstQuickStartPretext.isEmpty())    {
            
            sbusinessGroup = lstQuickStartPretext[0].Business_Grp_Class_Type__c;
        }
        
        return  sbusinessGroup;                                                                                      
     }
     
    /*
    * Query to get additional information for a selected classfication & intent
    * @param string: User's selected Classification
    * @param string: User's selected Intent
    * @param Id: Classification Type
    * @return List<Quick_Start_Pretext__c>: List of Pretext data for additional info
    */
    public static List<Quick_Start_Pretext__c> queryQSPAdditionalInfo(String sClassification, String sIntent, Id idClassificationType) {
        List<Quick_Start_Pretext__c> lstQSPAdditionalInfo = [SELECT Is_Allow_Multiple_Case__c, Header_Message_Type__c, Header_Message__c, Complaint_GA_Setup__c, Informational_Message__c, Soft_Warning_Message__c, CTCI_List_lkp__r.Never_a_Complaint__c, CTCI_List_lkp__r.Never_a_G_A_Rights_Given__c, Recommended_Actions__c   
                                                        FROM Quick_Start_Pretext__c 
                                                        WHERE CTCI_List_lkp__r.Classification__r.Name =: sClassification AND CTCI_List_lkp__r.Intent__r.Name =: sIntent 
                                                        AND CTCI_List_lkp__r.Classification_Type__c =: idClassificationType AND Is_Active__c = true AND CTCI_List_lkp__r.IsActive__c = true ];
        return lstQSPAdditionalInfo;
    }

    /**
    * insert Task record
    */    
    public static String insertAssociatedTask(Task oNewTask) {
        String sHasSaveError = '';
        database.SaveResult oSaveSuccess = database.insert(oNewTask, false);
        if(!oSaveSuccess.isSuccess()) {
            for(Database.Error err : oSaveSuccess.getErrors()) {
                String sErroFieldLabels = '';
                for(String sErrorField : err.getFields()) {
                    Schema.SObjectField sTemplabel = Constants_C_AHT_HUM.MAP_TASK_FIELDS.get(sErrorField);
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
    
    /* Queries to get Pretext data from BCM based
    * @param string: User's selected sOGOFieldValue
    * @param string: User's selected sOGOReasonValue
    * @return List<Quick_Start_Pretext_Section__c>: List of Pretext data
    */
    public static List<Quick_Start_Pretext_Section__c> queryPretextGASectionData(String sOGOFieldValue, String sOGOReasonValue) {
        List<Quick_Start_Pretext_Section__c> lstPretextSection = [SELECT Transfer_Values_with_Input__c, Transfer_Values__c, Transfer_Label__c, Case_Comment_Section__c,Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification__r.Name,
                                                                    Quick_Start_Pretext__r.CTCI_List_lkp__r.Intent__r.Name,Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification_Type__c, (SELECT id, Quick_Start_Pretext_Section__c, Pre_Label__c, Input_Field_Type__c, Input_Field_Labels__c, Ghost_Text__c,
                                                                        Field_Length__c, Field_Display_Size__c,Display_Position__c, Display_Order__c, Is_Required_Line_Item__c, Dependent_Input_Value__c, Additional_Line_Item__c
                                                                     FROM Quick_Start_Pretext_Line_Items__r
                                                                     WHERE Additional_Line_Item__c = null 
                                                                     ORDER BY Display_Order__c, Display_Position__c asc)
                                                                  FROM Quick_Start_Pretext_Section__c
                                                                  WHERE Quick_Start_Pretext__r.RecordType.Name = 'Quick Start GA Rights And Complaint' AND Quick_Start_Pretext__r.Complaint_GA__c =:sOGOFieldValue AND Quick_Start_Pretext__r.Complaint_GA_Reason__c =:sOGOReasonValue AND Quick_Start_Pretext__r.Business_Grp_Class_Type__c=:Constants_C_AHT_HUM.OGO_PRETEXT_SETUP AND Quick_Start_Pretext__r.Is_Active__c = true AND Quick_Start_Pretext__r.CTCI_List_lkp__c = null AND Case_Comment_Section__c =:Constants_C_AHT_HUM.DIV_GA];
                                                                  
        return lstPretextSection;
    }
    
    /*
    * This method is used to get document details
    * @param  String - Folder id and Document Names
    * @return List<Document> folder details
    */
    public static List<Document> getRecommendedActionsDocuments(){
        return [SELECT Id, Name, Description FROM Document WHERE FolderId IN (SELECT Id FROM Folder WHERE Name =: Constants_C_AHT_HUM.DOC_NAME_RECOMMENDED_ACTIONS)];
    }

    /*
    * This method checks if any Cases existing in last N days having same C/I and Complaint Value set
    * @param  String - Accountid
    * @param  String - Classification Id
    * @param  String - Intent Id
    * @param  String - Complaint value
    * @return List<Case> List of Case Number matching this criteria details
    */

    public static List<Case> getCasesByLastNDays(String sAccid, String sClassificationId, String sIntentId, String sComplaint){
        return [
                    SELECT id,CaseNumber,CTCI_List__r.Classification__c,CTCI_List__r.Intent__c 
                    FROM Case 
                    WHERE CreatedDate >= LAST_N_DAYS:30 
                            AND status != 'Cancelled' 
                            AND AccountId =:sAccid 
                            AND CTCI_List__r.Classification__c =:sClassificationId 
                            AND CTCI_List__r.Intent__c =:sIntentId 
                            AND complaint__c =:sComplaint 
                        ORDER BY LastmodifiedDate DESC LIMIT 5];
    }
}