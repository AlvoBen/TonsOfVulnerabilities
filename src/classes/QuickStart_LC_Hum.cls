/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_LC_HUM.cls 
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
* Pooja Kumbhar									    09/02/2022				   US:3272646 - Lightning - Quick Start - RCC Specific - case Comment callback Number
* Manohar Billa                                     08/26/2022                 Quick start - Check for User Permission set
* Pooja Kumbhar										09/12/2022				   US:3705153 - Lightning - Quick Start - RCC Specific - Task Information Section 
* Jasmeen Shangari									09/27/2022				   US:3230697 - Check to see if current user is RSOHP user and selected CI belongs to HP
* Pooja Kumbhar									    10/06/2022				   DF-6290: QA Lightning US3705153 The name of the Task tab is not displayed correctly
* Ajay Chakradhar                                   09/17/2023                 US:4874911 - Lightning - Mentor Documents
************************************************************************************************************************************************************************************/
public with sharing class QuickStart_LC_Hum {

	public static final List<String> lstComplaintsYes =  new List<String> {'Yes - Medicare Part C','Yes - Medicare Part D','Yes - Medicaid'};
	public static Map<String,List<CustomLink__c>> mapCIMentor;
    /*
    * Method to get details for Classification & Intent, picklist value for Medicare PartC PartD
    * 
    * @param void: No input parameter
    * @return Object: Consist for CI information as an Object 
    */

    @AuraEnabled(cacheable=true)
    public static Object getClassificationIntent() 
    {
        QuickStart_LDTO_HUM quickStartData;
        try{

            User sUserQueue = QuickStart_LS_HUM.evaluateUserQueue();
            Work_Queue_Setup__c oUserWorkQueue;
            ID idClassificationType, userId;
            String sBusinessGroup, sUserName, sProfileName, sServiceCenter, sDepartment;
            Map<String,String> lstMedicarePartCPartD;        
            Map<String,String> mapFrequentlyUserCIOption;
            Map<String,QuickStartCTCI_LDTO_HUM> mapClassificationIntent;
            List<CustomLink__c> lstRFDocumentLink;
            
            if(sUserQueue!= null)
            {
                userId = sUserQueue.Id;
                sUserName = sUserQueue.Name;
                sProfileName = sUserQueue.profile.name;
            }

            if(String.isNotEmpty(sUserQueue.Current_Queue__c))
            {
                oUserWorkQueue = QuickStart_LS_HUM.populateUserQueue(sUserQueue.Current_Queue__c);
                idClassificationType = oUserWorkQueue.Classification_Type__c;
                sServiceCenter = oUserWorkQueue.Service_Center__c;
                sDepartment = oUserWorkQueue.Department__c;

                sBusinessGroup = QuickStart_LS_HUM.checkPreTextAndBusinessGroup(idClassificationType);
                lstMedicarePartCPartD =  QuickStart_LS_HUM.getMedicarePartCDPicklist();            
                mapClassificationIntent = QuickStart_LS_HUM.getQueueClassificationIntent(idClassificationType);
                mapFrequentlyUserCIOption = QuickStart_LS_HUM.getFrequentlyUsedCI(idClassificationType);  
                mapCIMentor = QuickStart_LS_HUM.fetchAllMentorLink(idClassificationType,sBusinessGroup);
                lstRFDocumentLink = mapCIMentor.get(Constants_C_AHT_HUM.REFERENCE_DOCUMENT_CI+'-'+Constants_C_AHT_HUM.REFERENCE_DOCUMENT_CI);        
               
            }

            quickStartData = new QuickStart_LDTO_HUM(userId,sUserName,sProfileName,sServiceCenter,sDepartment, oUserWorkQueue.Classification_Type__r.Name, idClassificationType,oUserWorkQueue.Name, oUserWorkQueue.ID, sBusinessGroup,lstMedicarePartCPartD, mapClassificationIntent,mapFrequentlyUserCIOption,lstRFDocumentLink);
        } 
        catch(Exception exceptionToLog) {
            HUMExceptionHelper.logErrors(exceptionToLog, 'QuickStart_LC_HUM', 'getClassificationIntent');
          }
          return JSON.serialize(quickStartData);
    }
  
    /*
    * Method to fetch additional information for Clasiffication and Intent combination
    * 
    * @param Id: selected classification Id
    * @param Id: selected intent Id
    * @param Id: Classification Type
    * @param String: selected Classification Name
    * @param String: selected intent Name  
    * @return String: Quick Start Pretext information
    *
    * @see: Pretext layout on QS window
    */
    @AuraEnabled(cacheable=true)
    public static Object getQSPAdditionalInfo(Id idClassification, Id idIntent, Id idClassificationType, String sClassification, String sIntent)
    {

        return JSON.serialize( QuickStart_LS_HUM.fetchQSPAdditionalInfo(idClassification,idIntent,idClassificationType, sClassification,sIntent));
    }	
  
     /*
    * Method to fetch QS pretext data From QS Pretext ID
    * 
    * @param String: QSPretext ID
    * @return String: Return the String containig the JSON stringify data from QS Pretext
    *
    * @see: Pretext layout on QS window
    */

   @AuraEnabled(cacheable=true)
   public static string fetchQSPretextData(string sPretextId)
   {
      List<Quick_Start_Pretext_Section__c> lstofPreTextSections = QuickStart_LD_HUM.queryPretextSectionData(sPretextId);        
      String sPreTextdata = QuickStart_LH_HUM.parsePreTextData(lstofPreTextSections);
      return sPreTextdata;
   }
   /*
    * Method to fetch QS GnApretext data 
    * 
    * @param String: sOGOReason , sOGOfield
    * @return String: Return the String containig the JSON stringify data from QS Pretext
    *
    * @see: GnA Pretext layout on QS window
    */
   
  @AuraEnabled(cacheable=true)
   public static String fetchQsPretextGnAData(String sOGOFieldValue,String sOGOReasonValue) {
        List<Quick_Start_Pretext_Section__c> lstofPreTextSections = QuickStart_LD_HUM.queryPretextGASectionData(sOGOFieldValue, sOGOReasonValue);
        String sPretextGAJSON = QuickStart_LH_HUM.parsePreTextData(lstofPreTextSections);
        return sPretextGAJSON;
    }



 
   /* @param String: selected classificationSelected
    * @param String: selected intentSelected
    * @param String: complaintSelected
    * @return List of duplicate cases
    *
    */
    
    @AuraEnabled(cacheable=true)
    public static Object getQSDuplicateGrievance(String accountID, String classificationSelected, String intentSelected, String complaintSelected)
    {
        string sCaseComment;
        String scompleteCaseComment;
        String lastModDateRetValue;
        String sCaseCommentString = HUMConstants__c.getInstance('HUMLastCaseComment').StringValue__c;
        integer iConstantOne =  Integer.valueOf(HUMConstants__c.getInstance('HUMConstantOne').IntegerValue__c) ;
        Map<String,Map<String,String>> DupCases = new Map<String,Map<String,String>>();
        Map<string,string> casecomment = new Map<string,string>();
        QuickStart_LDTO_HUM duplicateCaseData;
        List<Case> lstCases = new List<Case>();


        try {
	    if(String.isNotBlank(accountID) && String.isNotBlank(classificationSelected) 
            && String.isNotBlank(intentSelected) && String.isNotBlank(complaintSelected)
            && lstComplaintsYes.contains(complaintSelected)) 
            {
                lstCases = QuickStart_LS_HUM.verifyNoDuplicateGrievance(accountID,classificationSelected, intentSelected, complaintSelected);
            }  
                  if(lstCases.size()>0)
                  {
                              
                    for(Case cs:lstCases)
                    {
                     
                         casecomment = new Map<string,string>();  
                        sCaseComment = (cs.CaseComments.isEmpty() || cs.CaseComments.get(cs.CaseComments.size()-iConstantOne).CommentBody == null) ? System.Label.HUMNoCaseComments : cs.CaseComments.get(cs.CaseComments.size()-iConstantOne).CommentBody;
                       
                        sCaseComment = FixNewLine(sCaseComment); 
                        if(!sCaseComment.equals(System.Label.HUMNoCaseComments) && checkCasecommentFieldPermission('parentid')){
                 If(Schema.sObjectType.Case.fields.LastModifiedDate.isAccessible())
                 {
                            lastModDateRetValue = cs.CaseComments.get(cs.CaseComments.size()-iConstantOne ).LastModifiedDate.format('MM/dd/YYYY\' \'hh:mm\' \'a');                     
                 }
            }else{
                lastModDateRetValue='';
            }           
                        if(sCaseComment == System.Label.HUMNoCaseComments)
                        {
                            scompleteCaseComment = sCaseComment;
                        }else
                        {
                            scompleteCaseComment = sCaseCommentString + ' ' + lastModDateRetValue + '\n '+ sCaseComment;
                                              
                        }
                    casecomment.put(cs.CaseNumber,scompleteCaseComment);
                    DupCases.put(cs.Id,casecomment); 
                    }
                duplicateCaseData = new QuickStart_LDTO_HUM(DupCases);
                                           
                }        
            }catch(Exception e) {
                                        
                                }
                                    return JSON.serialize(duplicateCaseData);
     }
                    
                    
     /* @param String: case comment field
    *
    * @return boolean value - checking if the case comment field access is available for user
    *
    */
    public static boolean checkCasecommentFieldPermission( string strCCFld){
        list<string> newCasecommentCreateFields = new list<string>();
        newCasecommentCreateFields.add(strCCFld);
        map<string,Schema.sObjectField> mapSobject = Schema.sObjectType.CaseComment.fields.getMap();
        boolean isAccess = false;
        for(string str : newCasecommentCreateFields){
            if(mapSobject.get(str).getDescribe().isAccessible())
            {
                isAccess = true;
            }
        }
        return isAccess;
    }   
    
	
	/* @param String: case comment 
    *
    * @return string caseComment - fixing case comment replacing new line items from case comment with ''
    *
    */
    public static String FixNewLine(String sComment) {
        
        if (sComment!= null) 
        {
            sComment = sComment.replaceAll('\r\n', '<br/>').replaceAll('"', '\\"');
                if(sComment.length()>500)
                 {
                     sComment = sComment.substring(0,500) +'...';
                 }
        }
        return sComment;
    }    
    /* @param String: selected classificationSelected
    * @param String: selected intentSelected
    * @return List of Mentor Documents
    *
    */
    
    @AuraEnabled(cacheable=true)
    public static List<CustomLink__c> getMentorLinks(String classificationSelected, String intentSelected, string idClassificationType, string sBusinessGroup)
    {
    List<CustomLink__c> lstMentorDocumentLink;
        try {
	if(String.isNotBlank(classificationSelected) 
            && String.isNotBlank(intentSelected) && String.isNotBlank(idClassificationType) && String.isNotBlank(sBusinessGroup)) {
            mapCIMentor = QuickStart_LS_HUM.fetchAllMentorLink(idClassificationType,sBusinessGroup);
            if(mapCIMentor!=null && mapCIMentor.containskey(classificationSelected+'-'+intentSelected)){
                lstMentorDocumentLink = mapCIMentor.get(classificationSelected+'-'+intentSelected);    
                } 
                }            
        }catch(Exception e) {
            
       }
        return lstMentorDocumentLink ;
    }
	
	/* @param String: selected task type
    * @param String: selected duedate, task comment and some user and queue data
    * @return taskID
    *
    */
    @AuraEnabled(Cacheable=false)
    public static object saveTask(string sTasktype, string sTaskDueDate, string sTaskComment, string serviceCenter, string department, string workqueuename, string username,string userId, string caseId) {
            QuickStart_LDTO_HUM.Task_DTO_HUM otaskInfo;
            Task oNewTask = new Task();
            oNewTask.RecordTypeId = Constants_C_AHT_HUM.RECORDTYPE_ID_NOTIFICATION_TASK;
            oNewTask.type= sTasktype;
        	if(oNewTask.type.contains('Notification')) 
            oNewTask.Callback_Requested__c = 'No';
            QuickStart_LS_HUM.assginTaskDefaults(oNewTask, caseId, sTaskDueDate, serviceCenter,department,workqueuename,username,userId);
            string sErrorMessage = QuickStart_LS_HUM.insertNewTask(oNewTask, sTaskComment);
            boolean bErrorNoSave = String.isNotEmpty(sErrorMessage);
            if(!bErrorNoSave) {
	          Task oNewTask1 = [select id,Task_Number__c from task where id=:oNewTask.id limit 1];
              otaskInfo= new QuickStart_LDTO_HUM.Task_DTO_HUM(oNewTask.id,oNewTask1.Task_Number__c);
             }
            return JSON.serialize(otaskInfo);
    }
	
	@AuraEnabled(Cacheable=true)
    public static string getCallbackNumber(string sInteractionId)
    {
    string sHomePhone;
    list<Interaction__C> lstInt = [SELECT ID, name, Interacting_With__c,Authenticated__c, Interacting_With__r.Name,Interacting_With__r.PersonHomePhone,Interacting_With__r.Phone,Interacting_With__r.IsPersonAccount, Caller_name__c, Interacting_With_type__c,Call_Category_Description__c,Genesys_Interaction_ID__c,Survey_ID__c,
                                                   Interaction_origin__c, uuid__c, Call_Data__c FROM Interaction__C WHERE ID = :sInteractionId];

                    if (lstInt != null && !lstInt.isEmpty())
                    {
                        
                        if(lstInt[0].Interacting_With__r.IsPersonAccount)
                        {
                            sHomePhone = lstInt[0].Interacting_With__r.PersonHomePhone;
                        }
                        else
                        {
                            sHomePhone = lstInt[0].Interacting_With__r.Phone;
                        }
                     }
     return sHomePhone;
    }

        /*
    * Method to fetch QS Access for the user based on permission set
    * 
    * @param String: None
    * @return String: Return the boolean saying whether user has the PS
    *
    */
    
    @AuraEnabled(cacheable=true)
    public static Boolean getQSAccess(){
    Boolean isQSEnabled = false; 
        for(PermissionSetAssignment psAsign: [SELECT Id FROM PermissionSetAssignment WHERE PermissionSet.name = 'CRMS_650_Quick_Start_Access' and AssigneeId = :userinfo.getuserid()]){
               isQSEnabled = true;
            }
        return isQSEnabled;
    }
    
    /*
    * Method to fetch BusinessGroup for the user 
    * @param Id: caseId 
    * @param String: sClassificationSelected
    * @param String: sIntentSelected
    * @return Object: Return the CaseMentorDocuments_LDTO_Hum Object 
    */
    @AuraEnabled(cacheable=true)
    public static Object getBusinessGroupName(Id caseId,String sClassificationSelected,String sIntentSelected) 
    {
        CaseMentorDocuments_LDTO_Hum supportData;
		List<Case> lstCaseDetails;
        try{
            User sUserQueue = QuickStart_LS_HUM.evaluateUserQueue();
            Work_Queue_Setup__c oUserWorkQueue;
            ID idClassificationType, userId;
            String sBusinessGroup, sUserName, sUserProfileName, sClassificationName, sIntentName;
            if(sUserQueue!= null)
            {
                userId = sUserQueue.Id;
                sUserName = sUserQueue.Name;
                sUserProfileName = sUserQueue.profile.name;
            }
            if(String.isNotEmpty(sUserQueue.Current_Queue__c))
            {
                oUserWorkQueue = QuickStart_LS_HUM.populateUserQueue(sUserQueue.Current_Queue__c);
                idClassificationType = oUserWorkQueue.Classification_Type__c;
                sBusinessGroup = QuickStart_LS_HUM.checkPreTextAndBusinessGroup(idClassificationType);
            }
			if(sClassificationSelected !=Null && sIntentSelected != Null){
            	if(caseId != Null || caseId != ''){
                	lstCaseDetails = getClassificationIntentNames(caseId);
                }
                if(lstCaseDetails.size() > 0){
					for(Case c:lstCaseDetails){
						sClassificationName = c.Classification_Id__r.Name;
						sIntentName = c.Intent_Id__r.Name;
					}
                }else{
                	sClassificationName = sClassificationSelected;
					sIntentName = sIntentSelected;    
                }
            }
            supportData = new CaseMentorDocuments_LDTO_Hum(idClassificationType,sBusinessGroup,sClassificationName, sIntentName,sUserProfileName);
        } 
        catch(Exception exceptionToLog) {
            HUMExceptionHelper.logErrors(exceptionToLog, 'QuickStart_LC_HUM', 'getBusinessGroupName');
        }
        return JSON.serialize(supportData);
    }
	
    /*
    * Method to fetch selected ClassificationIntentNames for current caseID 
    * @param Id: caseId 
    * @return List<Case>: Return the ClassificationIntentNames with respective Case 
    */
	@AuraEnabled
    public static List<Case> getClassificationIntentNames(Id caseId) {
        List<Case> lstCase = [SELECT Id, CaseNumber,Classification_Id__r.Name, Intent_Id__r.Name FROM Case where Id = :caseId Limit 1];
        return lstCase;
    }
  
}