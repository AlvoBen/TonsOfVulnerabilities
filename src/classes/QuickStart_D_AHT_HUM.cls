/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_D_AHT_HUM 
Created Date     : January 20, 2017
Function         : Executes queries in support of the Quick Start Console Component
Test Class       : QuickStart_T_AHT_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
* Cody A. Sanders         -----                 03/10/2017                 Quick Start MVP - Associate to Policy, Display Essential Fields, Open Case Edit and Case Comment Transfer
* Cody A. Sanders         -----                 03/24/2017                 Included additional Work Queue related fields
* Cody A. Sanders         -----                 03/27/2017                 Updated exception handling to account for error types for differentiated page message creation
* Cody A. Sanders         -----                 04/26/2017                 Updated for new CTCI model and added Associate to Account and Mentor Document functionality
* Pradeepkumar Dani       100996                06/13/2017                 Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
* Amar Gadewar                                  07/05/2017                 Existing Case Check fix for no interaction
* Subhamay Mandal                               07/31/2017                 REQ - 325458 Ability of a quick start page to be responsive when selecting top 5 and non top 5 classification and Intent (RF)
* Amar Gadewar                                  08/09/2017                 REQ - 326209 - Add a Chatter Post for with Hash Tag - Quick Start for cases created from Quick Start
* Piyusha Babar                                 08/28/2017                 REQ - 327583 - Ability of a quick start to create a case on an existing interaction created by someone else(RF)
* Subhamay Mandal                               10/02/2017                 REQ - 329803 Ability to prefill the quick start case comment fields based on the Classification and Intent selection
* Subhamay Mandal                               01/12/2018                 REQ - 350392 - Business Configuration model for Quick Start pretext (RF) 
* Subhamay Mandal                               02/20/2018                 REQ - 354335 Ability in Quick Start to allow CCS reps to create multiple cases on the single interaction for certain Classification and Intent (RF)
* Subhamay Mandal                               03/20/2018                 REQ - 357688 Technical Story: RF Quick Start White Screen Issue - (RF) and REQ - 357686 Update QuickStart window UI (RF)
* Darshan Sharma                                04/05/2018                 REQ - 361047 - Ability to update Mentor Links in QuickStart via Business Configuration
* Darshan Sharma                                05/02/2018                 REQ - 361223 Ability to update Mentor Links in QuickStart via Business Configuration (CRM)
* Subhamay Mandal                               05/16/2018                 REQ - 361876 AHTQuality - Preset the Complaint Fields
* Subhamay Mandal                               05/30/2018                 Defect 361102.0001 REQ - 363405 Error message displayed is not appropriate while associating the case to policy
* Subhamay Mandal                               06/27/2018                 REQ - 367101 AHT - Add Frequently Used Classification and Intent to Classification Dropdown (RF)
* Darshan Sharma                                07/12/2018                 REQ - 369793 Quality - Ability to display the AutoLogging Message for additional Classification and Intents (RF+CRM)
* Darshan Sharma                                07/20/2018                 REQ - 370598 Quality - Ability to disable the Case Creation in QS if the user does not have permissions to Create a Case for the Member in CRM (RF)
* Subhamay Mandal                               09/04/2018                 REQ - 372704 Quality - Ability to Identify if a Case with a Classification was created within the Past 14 Days in QS. (RF)
* Subhamay Mandal                               09/14/2018                 REQ - 371313 PCC - Adjust backend code to support PCC, RF and Non Rf users in QS
* Subhamay Mandal                               09/16/2018                 Defect# 382708.0001 Fixed. REQ - 372704_Msg displayed in QS+ on creating a case_Cancelled case with same C created in 14 days 
* Darshan Sharma                                10/01/2018                 REQ - 374820: CTCI Model Update (RF+CRM)
* Asish   Behera                                09/24/2018                 REQ - 375046 Quality - Ability to display an Info Message at the top of QS if the Work Queue does not have Pretext (RF+CRM)
* Darshan Sharma                                11/30/2018                 REQ - 378965 PCC - Mentor Documents Set 1 (RF)
* Amar Gadewar                                  01/22/2019                 REQ - 383509 SF - TECH - Update PCC Quick Start Rules without 655 Permission Set. (RF)
* Subhamay Mandal                               01/23/2019                 REQ - 383554 User Story - Track usage of associate resolution (RF).  
* Subhamay Mandal                               04/10/2019                 REQ - 389663 Defect 411139.0001 Fix. 
* Amar Gadewar                                  04/11/2019                 REQ - 388904 - Check Against Active CTCI record
* Subhamay Mandal                               05/22/2019                 REQ - 397111 Quality - OGO - Display Soft Warning Messages in QuickStart (RF)    
* Subhamay Mandal                               05/23/2019                 REQ - 397111 Code cleanup and improve performance
* Subhamay Mandal                               05/30/2019                 REQ - 387576, REQ - 387577, REQ - 388197 - OGO stories on Compplaint and GnA Rights Given on QS
* Subhamay Mandal                               06/14/2019                 REQ - 402004 SF - TECH - Quick Start Performance Enhancements (RF)
* Subhamay Mandal                               06/25/2019                 REQ - 402804 Quality - Add Informational Message above Callback Number (RF)
* Vinay BL                                      06/26/2019                 REQ - 400083 AHT – RCC – Guidance Center Pretext (Set 4) (CRM) SIT Fix
* Subhamay Mandal                               07/29/2019                 REQ - 404543 SF TECH - Ability for a Business Configuration Owner to add Pretext when a new classification and intent is added (CRM)
* Rakesh Patni                                  08/20/2019                 REQ - 406104. Launch Compass Funcitonality
* Subhamay Mandal                               01/28/2020                 731580|729526|729566 PR00091574 - Code merged for 2/28
* Shruthi Karanth                               02/25/2020                 P9 Medium issue for CRUD Delete fixed in deleteTempMentorRecords method
* Subhamay Mandal                               03/18/2020                 QS 3.0 Changes 938164|938619
* Subhamay Mandal                               03/15/2020                 938292 QS 3.0 - AHT - Ability to create task from Quick Start (RF)
* Subhamay Mandal                               03/17/2020                 938197 QS 3.0 - Quality - Ability to make pretext line item mandatory in quick start (RF)
* Santhi Mandava                                05/02/2020                 User Story 938955: PR00091574 - Update Case Information Section to display "What is Call About" information instead of Classification and Intent fields (RF)
* Amar Gadewar                                  06/18/2020                 User Story 1203874: PR00091574 - MF 1 - RF Clean up (RF)
* Amar Gadewar                                  07/05/2020                 User Story 1247438: PR00091574 - MF 1 - SF - TECH - QS 3.0 Code Improvement and Knowledge cleanup (RF)
* Amar Gadewar                                  08/28/2020                 User Story 1386444: PR00091574 - MF 1 - Rollback "What is the Call About?" feature in Quick Start (RF)
* Samantha Tennyson                             12/28/2020                 User Story 1745934: PR00091574 - MF 1 - Quality - Prevent Complaint/G&A Rights Given fields to be selected on Cases with specific Classification and Intent Combinations (CRM)
* Samantha Tennyson                             01/22/2021                 Adding With Sharing to fix Checkmarx issue
* Samantha Tennyson				                04/01/2021		    	   User Story 1986952: T1PRJ0001827 - MF 1 - AHT/Quality - QS 3.0 - Ability to display Pretext in Grievance and Appeals section based on Grievance/Appeal Reason selection (RF)
* Samantha Tennyson				   				06/14/2021	               User Story 2372808: T1PRJ0001827 - MF 1 - Quality/FCR - Ability to display "Recommended Actions" in Quick Start (RF)
* Amar Gadewar                                  06/28/2021                 User Story 2406575: T1PRJ0001827 - MF 1 - AHT/Quality - Ability to display alert in Quick Start if the Rep is creating a case with potential duplicate grievance (RF)
* Sunil Kumar Reddy Y                           07/26/2021                 User Story 2498928: T1PRJ0001827 - MF 1 - AHT/Quality - Display Informational Message when Case is available in History with Same Classification (RF)
* Jayesh Solanki				                08/30/2021		           User Story 2696518: T1PRJ0001827 - MF 1 - AHT/Quality - Highlight fields with errors in Quick Start (RF)
************************************************************************************************************************************************************************************/
public with sharing class QuickStart_D_AHT_HUM
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
    */ 
    public static List<Case> queryCasesByClassification(String AccountId, String sClassification){
        return [SELECT Id, casenumber ,CTCI_List__c 
                FROM Case
                WHERE AccountId = :AccountId 
                AND CTCI_List__r.Classification__c =: sClassification 
                AND status != 'Cancelled'
                AND createddate >= LAST_N_DAYS:14
                AND recordtype.developerName NOT IN :Constants_C_AHT_HUM.Pharmacy_RecordType_DeveloperNames 
                ORDER BY LastmodifiedDate DESC LIMIT 5];
    }

    /*
    * Queries the policy page to check Product Code and Product type code.
    */ 
    public static List<Policy_Member__c> queryPolicyByClassification(String policyId){
        List<Policy_Member__c> lstPolicyMember =[SELECT id, Product_Type__c,Product_Type_Code__c  FROM Policy_Member__c WHERE id = : policyId];
        return lstPolicyMember;
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