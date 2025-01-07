/*****************************************************************************************
Apex class Name    : TaskHeirarchyTask_C_HUM
Version            : 1.0
Function           : Controller class for TaskHeirarchyTask_CMP_HUM component
Created On         : 15 Sept 2016
Test Class         : TaskHeirarchyCase_T_HUM

Modification Log:
*   Developer                   Code Review     Date                         Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Avinash Choubey              40794              09/15/2016            Original Version REQ 252450
*   Avinash Choubey              42213              09/30/2016            fixed defect 243613
*   Avinash Choubey              42221              10/03/2016            added getTaskComment method REQ - 252573
* ------------------------------------------------------------------------------------------------------------------------------  */
public with sharing class TaskHeirarchyTask_C_HUM
{
    static List<TaskHeirarchyWrapper_DTO_HUM> lstTHWrapper = new List<TaskHeirarchyWrapper_DTO_HUM>();   
    static List<ID> taskIDs = new List<ID>();
    static List<HUM_Task_Heirarchy__c> lstTH;
    static Integer rowCount = 0;
    public string sTaskId {get; set;}
    public string sTaskComment {get; set;}
   
     /**
    * Returns void
    * <p>
    * This method calls the accums web service
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
	private static  void populateTHWrapper()
	{
		if(null != taskIDs && taskIDs.size() > 0)
		{            
			Map<ID,Task>   mapTasks = new  Map<ID,Task> ([SELECT Id, Task_Number__c,Subject, Status, Priority, OwnerId, Type, CreatedDate, Age_Since_Created__c, Owner_Queue__c,  Date_Time_Closed__c,ActivityDate,Task_Owner__c FROM Task where ID in :taskIDs order by createdDate desc]);  
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
				objTH.DateTimeOpened = objTask.createdDate.format(GLOBAL_CONSTANT_HUM.CASEMGMT_DATE_TIME_FORMAT);
				if(null != objTask.Date_Time_Closed__c )
				{
					objTH.DateTimeClosed = objTask.Date_Time_Closed__c.format(GLOBAL_CONSTANT_HUM.CASEMGMT_DATE_TIME_FORMAT);
				}
				else
				{
					objTH.DateTimeClosed = '';
				}
				objTH.TaskOwner = objTask.Task_Owner__c;
				objTH.OwnerQueue = objTask.Owner_Queue__c;
			}   
		}
	}
 
 /**
    * Returns an List of TaskHeirarchyWrapper_DTO_HUM
    * <p>
    * This method calls the accums web service
    *
    * @pram  String caseId
    * @param  String currentTaskID
    * @return  List<TaskHeirarchyWrapper_DTO_HUM>
    * @see
    */ 
@RemoteAction 
    public static  List<TaskHeirarchyWrapper_DTO_HUM> getTreeDetails(String caseId, String currentTaskID)
    {
     	try
     	{
	     	lstTH = [select Id,Name,Immediate_Parent__c,Hum_CaseID__c,HUM_Parent_TaskID__c,HUM_Dependent_TaskID__c, Level__c   FROM HUM_Task_Heirarchy__c where HUM_CaseId__c = :caseId and HUM_Parent_TaskID__c != null order by createdDate desc  ];
	        populateTHMap( currentTaskID,  1,0)  ;
	       	populateTHWrapper ();  
	        return  lstTHWrapper;
     	}
	    catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'TaskHeirarchyTask_C_HUM','getTreeDetails');
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
	      	sTaskId = ApexPages.CurrentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEMGMT_TASKID_HUM);
	      	String sTaskCommentl ;
	      	List<Task_Field_History__c> lstTFH =	 [SELECT SObjFieldValue__c,createdDate,Id FROM Task_Field_History__c  WHERE SObjID__c = :sTaskId AND SObjFieldName__c = :GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM ORDER BY Created_At__c  DESC limit 1 ];
	      	if(null != lstTFH && lstTFH.size() > 0 &&  null != lstTFH[0].SObjFieldValue__c)
			{
					sTaskCommentl = lstTFH[0].SObjFieldValue__c;
			}
			 sTaskComment = String.isEmpty(sTaskCommentl) ? System.Label.TASK_NO_COMMENT_HUM : FixNewLine(sTaskCommentl);
		 }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'TaskHeirarchyTask_C_HUM','getComments');
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