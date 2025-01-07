/*****************************************************************************************
Apex class Name    : TaskHeirarchyCase_C_HUM
Version            : 1.0
Function           : Controller class for TaskHeirarchyCase_CMP_HUM component
Created On         : 15 Sept 2016
Test Class         : TaskHeirarchyCase_T_HUM

Modification Log:
*   Developer                   Code Review     	Date                         Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Avinash Choubey              40794              09/15/2016            Original Version REQ 252450
*   Avinash Choubey              42213              09/30/2016            fixed defect 243613
*   Avinash Choubey              42221              10/03/2016            added getTaskComment method REQ - 252573
*   Gargey Sharma	             112845             07/20/2017            REQ - 322649 Added condition in query, Not to display MET Tasks
*   Santhosh Ganji                                  04/19/2019            REQ - 386935 - Case Detail Restrictions (MED vs non-MED cases)—READ ONLY features
*   Santhosh Ganji                                  05/24/2019            REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
* 	Gowthami Thota & Manohar						08/06/2021			  US - 2543348 - T1PRJ0002606/PR00094268  - CR01 CSI 1922  - Performance improvement of Case detail and Edit page 
*   Anil Parvathaneni				                10/18/2022			  User Story 3176163 : Create New Task Type and fields for Market Credentialing
* ------------------------------------------------------------------------------------------------------------------------------  */
global with sharing  class TaskHeirarchyCase_C_HUM 
{
 
    static List<TaskHeirarchyWrapper_DTO_HUM> lstTHWrapper = new List<TaskHeirarchyWrapper_DTO_HUM>();   
    static List<ID> taskIDs = new List<ID>();
    static List<HUM_Task_Heirarchy__c> lstTH = new List<HUM_Task_Heirarchy__c>();
    static Integer rowCount = 0;
 	public string sTaskId {get; set;}
    public string sTaskComment {get; set;}
    public Case objCase{ get; set; }
	public boolean hideTheColumn{get;set;}
    public boolean bShowTaskEndDate{get;set;}
    public boolean bCredentialingswitch{get;set;}
    public boolean hideTheButton{ get; set;}
	public boolean hPharmacyUser{ get; set;}
	public Set<String> wqueues = new Set<String>{CaseDetailConstants_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,CaseDetailConstants_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,CaseDetailConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,CaseDetailConstants_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public Set<String> chatQueues = new Set<String>(CaseDetailConstants_HUM.WEB_CHAT_WQUEUES);
	public string objCaseID{get;
                          set {
                                if(objCaseID==Null)
                                {
                                    objCaseID = value;
                                    onload();
                                }
                              }  
                         }
    
    public void onload()
    {
        objCase= new case();
        objCase = [SELECT Id, Owner_Queue__c, RecordType.name, Policy_Member__c, Policy_Member__r.Product__c,Created_By_Queue__c,Origin,Classification_Id__r.name,Intent_Id__r.name FROM Case WHERE id = :objCaseId];
        hPharmacyUser = false;
		hideTheButton = false;
        hideTheColumn = false;
		bCredentialingswitch = HUMUtilityHelper.isCRMFunctionalityON('3176163');
        hPharmacyUser = (Pharmacy_H_HUM.isPharmacyUser() != CaseDetailConstants_HUM.NONPHARMACY_USER_HUM);
        if(objCase != null && (objCase.Recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType) || (objCase.Policy_Member__c != null && objCase.Policy_Member__r.Product__c == CaseDetailConstants_HUM.MEDICARE_POLICY) || (chatQueues.contains(objCase.Owner_Queue__c) && objCase.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN)))
        {
           if(objCase.Recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType) || wqueues.contains(objCase.Owner_Queue__c) || wqueues.contains(objCase.Created_By_Queue__c) || (chatQueues.contains(objCase.Owner_Queue__c) && objCase.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN))
           {
              hideTheButton = True;
           }
        }
		if(objCase.Classification_Id__r.name == 'Provider Requests' && objCase.Intent_Id__r.name == 'LA Medicaid Cred Workflow' && bCredentialingswitch ){
            hideTheColumn = true;
		}
    }
	
    /**
    * Returns void
    * <p>
    *
    * @pram  ID taskID
    * @pram  Integer level
    * @pram  Integer parentSequence
    * @return void
    * @see
    */ 
    private static void populateTHMap(ID taskID,Integer level, Integer parentSequence)
    {
        for(HUM_Task_Heirarchy__c objTH :lstTH)
        {   
            if(objTH.HUM_Parent_TaskID__c == taskID)
            {
                taskIDs.add(objTH.HUM_Dependent_TaskID__c);
                lstTHWrapper.add( new TaskHeirarchyWrapper_DTO_HUM(objTH.HUM_Dependent_TaskID__c, level,parentSequence));
                rowCount++;
                populateTHMap(objTH.HUM_Dependent_TaskID__c, level+1,rowCount);
            }
        }
    }
 
   /**
    * Returns void
    * <p>
    *
	* This populates task data into wrapper list
    * @return void
    * @see
    */ 
	private Static void updateTaskHistoryWrapper()
	{
		if(null != taskIDs && taskIDs.size() > 0)
		{            
			Map<ID,Task>   mapTasks = new  Map<ID,Task> ([SELECT Id, Task_Number__c,Subject, Status, Priority, OwnerId, Type, CreatedDate, Age_Since_Created__c, Owner_Queue__c,  Date_Time_Closed__c,ActivityDate,Task_Owner__c,Credentialing_Task__c,Task_Completion_Date__c,WhatId FROM Task where ID in :taskIDs ]);  
			for(TaskHeirarchyWrapper_DTO_HUM objTH : lstTHWrapper) 
			{
				Task objTask = mapTasks.get(objTH.taskID);
				objTH.TaskNumber = objTask.Task_Number__c;
				objTH.TaskType = objTask.Type;
				if(null != objTask.ActivityDate )
				{
					 objTH.Duedate = String.valueOF(objTask.ActivityDate.format());
				}
				else
				{
					objTH.Duedate = '';
				}
				objTH.Status = objTask.Status;
				objTH.Priority = objTask.Priority;
				objTH.TaskAge = (Integer)objTask.Age_Since_Created__c;
				objTH.DateTimeOpened = objTask.createdDate.format(CaseDetailConstants_HUM.CASEMGMT_DATE_TIME_FORMAT);
				if(null != objTask.Date_Time_Closed__c )
				{
					 objTH.DateTimeClosed = objTask.Date_Time_Closed__c.format(CaseDetailConstants_HUM.CASEMGMT_DATE_TIME_FORMAT);
				}
				else
				{
					objTH.DateTimeClosed = '';
				}
				objTH.TaskOwner = objTask.Task_Owner__c;
				objTH.OwnerQueue = objTask.Owner_Queue__c;
                
				case caseRec = [SELECT Id,Classification_Id__r.name, Intent_Id__r.name FROM Case WHERE Id  =: objTask.WhatId];
                if(caseRec.Classification_Id__r.name == 'Provider Requests' && caseRec.Intent_Id__r.name == 'LA Medicaid Cred Workflow' && HUMUtilityHelper.isCRMFunctionalityON('3176163')){
                    objTH.CredentialingTask = objTask.Credentialing_Task__c;
                    if(null != objTask.Task_Completion_Date__c){
                        objTH.TaskCompletionDate = objTask.Task_Completion_Date__c.format();
                    }else{
                        objTH.TaskCompletionDate = '';
                    } 
                }
			}   
		} 
	}
 
 /**
    * Returns an List of TaskHeirarchyWrapper_DTO_HUM
    * <p>
    * This method calls the accums web service
    *
    * @pram  String caseId
    * @return  List<TaskHeirarchyWrapper_DTO_HUM>
    * @see
    */    
@RemoteAction 
    global static  List<TaskHeirarchyWrapper_DTO_HUM> getTreeDetails(String caseId)
    {
		try
		{
			set<ID> childTasks = new Set<ID>();
			lstTH = [select Id,Name,Immediate_Parent__c,Hum_CaseID__c,HUM_Parent_TaskID__c,HUM_Dependent_TaskID__c, Level__c   FROM HUM_Task_Heirarchy__c where HUM_CaseId__c = :caseId AND HUM_Parent_TaskID__c != null AND  HUM_Parent_TaskID__c != '' AND HUM_Parent_TaskID__c != 'null' order by createdDate desc ];
			for (HUM_Task_Heirarchy__c objTH :lstTH )
			{
				childTasks.add(objTH.HUM_Dependent_TaskID__c);
			}
			for (task objTask : [select id from Task where whatId =:caseId AND RecordType.DeveloperName != :CaseDetailConstants_HUM.MET_TASK_RECORDTYPE_HUM AND Id not in :childTasks  order by createdDate desc ])
			{
				taskIDs.add(objTask.Id);
				lstTHWrapper.add( new TaskHeirarchyWrapper_DTO_HUM(objTask.Id, 1,0));
				rowCount++;
				populateTHMap(objTask.Id, 2,rowCount);
			}
			updateTaskHistoryWrapper();
	        return  lstTHWrapper;
		}
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'TaskHeirarchyCase_C_HUM','getTreeDetails');
            return NULL;
        }
    }
      
    /*
    * Method Name : getComments
    * Description : Gets the Task Comments
    * Return type : void
    * Parameter   : NA
    */
    public void getComments()
    {
      try
	      {
	      	sTaskId = ApexPages.CurrentPage().getParameters().get(CaseDetailConstants_HUM.CASEMGMT_TASKID_HUM);
	      	String sTaskCommentl ;
	      	List<Task_Field_History__c> lstTFH =	 [SELECT SObjFieldValue__c,createdDate,Id FROM Task_Field_History__c  WHERE SObjID__c = :sTaskId AND SObjFieldName__c = :CaseDetailConstants_HUM.CASEMGMT_DESCRIPTION_HUM ORDER BY Created_At__c  DESC limit 1 ];
	      	if(null != lstTFH && lstTFH.size() > 0 &&  null != lstTFH[0].SObjFieldValue__c)
			{
					sTaskCommentl = lstTFH[0].SObjFieldValue__c;
			}
			 sTaskComment = String.isEmpty(sTaskCommentl) ? System.Label.TASK_NO_COMMENT_HUM : FixNewLine(sTaskCommentl);
		 }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'TaskHeirarchyCase_C_HUM','getComments');
        }
	}
	
	 /*
    * Method Name : FixNewLine
    * Description : replaces \r\n wih new line HTML tag
    * Return type : String
    * Parameter   : String
    */
	  private String FixNewLine(String sComment) 
	  {
            return sComment.replaceAll('\r\n', '<br/>').replaceAll('"', '\\"');
      }  
 }