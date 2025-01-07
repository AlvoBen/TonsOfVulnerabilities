/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_LD_HUM.cls 
Created Date     : March 2, 2022
Function         : Executes SOQL in support of the Quick Start Console Component
Test Class       : QuickStart_LT_HUM

**Modification Log
* Developer Name          Code Review #               Date                       Description
* Jasmeen Shangari            -----                 03/02/2022                 Original Version
* Pooja Kumbhar                                     03/16/2022                 User Story - 3158054 : T1PRJ0170850 - Lightning - Quick Start - MVP Core - Case Comment Section - Populate from pretext - caller expected outcome, Action Taken & Associate resolution
* Pooja Kumbhar									    08/02/2022				   US:3230754 - Lightning - Quick Start - MVP Core - G & A Complaints case comment Pretext Data 
* Pooja Kumbhar						  			    08/16/2022  		       US:3272640 and US:3272641 - Reference and Mentor document section
* Pooja Kumbhar									    08/22/2022				   US:3272634 - Lightning - Quick Start - RCC Specific - G&A Complaint Section
* Pooja Kumbhar										09/12/2022				   US:3705153 - Lightning - Quick Start - RCC Specific - Task Information Section
* Pooja Kumbhar						  			    09/27/2022  		       US:3755319 and US:3755922 - Reference and Mentor document opening in LWC 
* Dinesh Subramaniyan                               07/13/2023                 US: 4810481 -RAID#093 (Error Editing Notification Task)
************************************************************************************************************************************************************************************/

public with sharing class QuickStart_LD_Hum {
    /*
    * Queries the user's current queue
    * 
    * @return string: The current User's Record
    */  
    public static User queryUserQueue()
    { 
       User oUser = [SELECT Id,name, profileid, profile.name, Current_Queue__c 
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
    * Query to get additional information for a selected classfication & intent
    * @param Id: selected classification Id
    * @param Id: selected intent Id
    * @param Id: Classification Type
    * @return List<Quick_Start_Pretext__c>: List of Pretext data for additional info
    */
    public static List<Quick_Start_Pretext__c> queryQSPAdditionalInfo(Id idClassification, Id idIntent, Id idClassificationType) {
        List<Quick_Start_Pretext__c> lstQSPAdditionalInfo = [SELECT id,Is_Allow_Multiple_Case__c, Header_Message_Type__c, Header_Message__c, Complaint_GA_Setup__c, Informational_Message__c, Soft_Warning_Message__c, CTCI_List_lkp__r.Never_a_Complaint__c, CTCI_List_lkp__r.Never_a_G_A_Rights_Given__c, Recommended_Actions__c, Complaint_GA__c, Complaint_GA_Reason__c   
                                                        FROM Quick_Start_Pretext__c 
                                                        WHERE CTCI_List_lkp__r.Classification__c =: idClassification AND CTCI_List_lkp__r.Intent__c =: idIntent 
                                                        AND CTCI_List_lkp__r.Classification_Type__c =: idClassificationType AND Is_Active__c = true AND CTCI_List_lkp__r.IsActive__c = true ];
        return lstQSPAdditionalInfo;
    }

    /*
    * Return picklist values for MedicarePartC or PartD field
    * @param : void
    * @return  Map<String,String>: map of picklist value for MedicarePartC or PartD field
    */
    public static Map<String,String> getPicklistValue()
    {
        Map<String,String> mapMedicarePartCPartD = new  Map<String,String>();
       Schema.DescribeFieldResult fieldResult = Case.Call_Benefit_Category__c.getDescribe();
       List<Schema.PicklistEntry> lstmedicarePartCPartD = fieldResult.getPicklistValues();
       for(Schema.PicklistEntry medPartCPartD : lstmedicarePartCPartD)
       {
        mapMedicarePartCPartD.put(medPartCPartD.getLabel(), medPartCPartD.getValue());
       }     
       return mapMedicarePartCPartD;
    }
    
     /*
    * Queries to get Pretext data based on classification and intent
    * @param String: QSPretext ID
    * @return List<Quick_Start_Pretext_Section__c>: List of Pretext data
    */
    public static List<Quick_Start_Pretext_Section__c> queryPretextSectionData(string sPretextId) {
      List<Quick_Start_Pretext_Section__c> lstPretextSection = [SELECT id, Transfer_Values_with_Input__c, Transfer_Values__c, Transfer_Label__c, Case_Comment_Section__c,
                                                                  (SELECT id, Quick_Start_Pretext_Section__c, Pre_Label__c, Input_Field_Type__c, Input_Field_Labels__c, Ghost_Text__c, 
                                                                      Field_Length__c, Field_Display_Size__c, Display_Order__c, Is_Required_Line_Item__c, Dependent_Input_Value__c, Display_Position__c, Additional_Line_Item__c 
                                                                   FROM Quick_Start_Pretext_Line_Items__r 
                                                                   WHERE Additional_Line_Item__c = null 
                                                                   ORDER BY Display_Order__c, Display_Position__c asc) 
                                                                FROM Quick_Start_Pretext_Section__c 
                                                                WHERE Quick_Start_Pretext__c =: sPretextId AND Quick_Start_Pretext__r.Is_Active__c = true
                                                                      AND Quick_Start_Pretext__r.CTCI_List_lkp__r.IsActive__c = true 
                                                                ORDER BY Case_Comment_Section__c desc];
      return lstPretextSection;
  }
  /* Queries to get GnAPretext data
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
    * This method checks if any Cases existing in last N days having same C/I and Complaint Value set
    * @param  String - Accountid
    * @param  String - Classification Id
    * @param  String - Intent Id
    * @param  String - Complaint value
    * @return List<Case> List of Case Number matching this criteria details
    */

    public static List<Case> getCasesByLastNDays(String accountID,String sClassificationId, String sIntentId, String sComplaint){
    integer iConstantOne =  Integer.valueOf(HUMConstants__c.getInstance('HUMConstantOne').IntegerValue__c) ;
        return [
                    SELECT id,CaseNumber,CTCI_List__r.Classification__c,CTCI_List__r.Intent__c,(Select Id, CommentBody,LastModifiedDate From CaseComments order by LastModifiedDate DESC Limit: iConstantOne)
                    FROM Case 
                    WHERE CreatedDate >= LAST_N_DAYS:30 
                            AND status != 'Cancelled' 
                            AND AccountId =:accountID
                            AND CTCI_List__r.Classification__c =:sClassificationId 
                            AND CTCI_List__r.Intent__c =:sIntentId 
                            AND complaint__c =:sComplaint 
                        ORDER BY LastmodifiedDate DESC LIMIT 5];
       
    }
    
    /*
    * Query all Mentor and Reference Document on QuickStart page load
    * @param Id: Classification Type
    * @param String: Business Group
    * @return List<CustomLink__c,>: List of Pretext data
    */
    public static List<CustomLink__c> queryAllCustomLink(Id idClassificationType, String sBusinessGroup){
         
        Integer iValue = Integer.Valueof(HUMConstants__c.getInstance('HUMLimit1000').IntegerValue__c); 
        HUMCertificateURL__c objHssURL = HUMCertificateURL__c.getValues('HSSRedirectURL');
         
        List<CustomLink__c> lstAllMentorLink = [SELECT Id,Name, Download_MentorLink__c,URL__c,Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification__r.Name,
                                                    Quick_Start_Pretext__r.CTCI_List_lkp__r.Intent__r.Name 
                                                FROM CustomLink__c 
                                                WHERE recordtype.developerName = 'Mentor_Link'
                                                    AND ((Quick_Start_Pretext__r.CTCI_List_lkp__r.Classification_Type__c =: idClassificationType AND Quick_Start_Pretext__r.CTCI_List_lkp__r.IsActive__c = true)
                                                    OR (Quick_Start_Pretext__r.CTCI_List_lkp__c = NULL AND Quick_Start_Pretext__r.Business_Grp_Class_Type__c =: sBusinessGroup))
                                                    And Quick_Start_Pretext__r.Is_Active__c = true 
                                                ORDER BY Name 
                                                LIMIT : iValue];
        if(lstAllMentorLink.size()>0)
        {
         for(integer i=0;i<lstAllMentorLink.size();i++)
         {
             if(!lstAllMentorLink[i].Download_MentorLink__c)
             {
                 if(objHssURL != null && null != objHssURL.CertificateListURL__c && String.isNotBlank(lstAllMentorLink[i].URL__c)) {
                        
                       lstAllMentorLink[i].URL__c = objHssURL.CertificateListURL__c + EncodingUtil.base64Encode(blob.valueof(lstAllMentorLink[i].URL__c));
                 }
             }
         }
         }
        return lstAllMentorLink;
    }
	
	 /*
    * This method is used to get document details
    * @param  String - Folder id and Document Names
    * @return List<Document> folder details
    */
    public static List<Document> getRecommendedActionsDocuments(){
        return [SELECT Id, Name, Description FROM Document WHERE FolderId IN (SELECT Id FROM Folder WHERE Name =: Constants_C_AHT_HUM.DOC_NAME_RECOMMENDED_ACTIONS)];
    }

     /**
    * insert Task record
    */    
    public static String insertAssociatedTask(Task oNewTask, string sTaskComment) {
        String sHasSaveError = '';
        Task_Field_History__c oTaskFH = new Task_Field_History__c ();
        database.SaveResult oSaveSuccess = database.insert(oNewTask, false);
       if(oSaveSuccess.isSuccess() && sTaskComment!='' && sTaskComment!= null && sTaskComment!='undefined')
        {
           if(Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isUpdateable()) oTaskFH.SObjID__c = oNewTask.Id;  
                    if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isUpdateable()) oTaskFH.SObjFieldName__c = GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM; 
                    if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isUpdateable()) oTaskFH.SObjFieldValue__c= sTaskComment;    
                    if(Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isUpdateable()) oTaskFH.Created_At__c = System.now(); 
        }
        database.UpsertResult oSaveCommentSuccess = database.upsert(oTaskFH , false);
        if(!oSaveCommentSuccess.isSuccess())
        {
             String error = '';
             for(Database.Error err : oSaveSuccess.getErrors()) {
                 error = err.getMessage();
             }
        }
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

        if(oSaveSuccess.isSuccess()) {
            if(String.isNotBlank(oNewTask.Id)){
                List<Task> taskList = [Select Task_Number__c, Subject from Task WHERE Id = :oNewTask.Id limit 1];
                if(taskList != null && taskList.size() > 0) {
                    taskList[0].Subject = taskList[0].Task_Number__c;
                    update(taskList); //To Update Task Subject
                }
            }
    	}

        return sHasSaveError;
    }
}