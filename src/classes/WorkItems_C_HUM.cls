/******************************************************************************************************************
Apex class Name    : WorkItems_C_HUM
Version            : 1.0
Function           : Controller for Displaying Due Items on Home Page
Created On         : September 19 2016
Test Class         : WorkItems_T_HUM

Modification Log:
*   Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*   Vishal Verma			       40074          09/19/2016          Original Version -> REQ-244451 : Due items on Home Tab 
*   Vishal Verma					              10/13/2016          Defect 243988.0001 : Replaced Due_Date__c to Follow_up_date__c on Case  
*   Vamsi Kondragunta                             12/13/2016          Defect fix for 252935.0001
*   Gourab Tewary                                 03/10/2021          User Story 1896836: T1PRJ0001827- IR 5050 - Show last comment on Case&Task link hover on home page (Due Date) (CRM)
*	Karthik Varma N R							  07/23/2021	      User Story 2503627: T1PRJ0001827 - IR 5050 - Tech Debt - Clean up switch for past releases(CRM)
******************************************************************************************************************/
public with sharing class WorkItems_C_HUM 
{
	private List<String> lstValidCaseStatus = new List<String>{GLOBAL_CONSTANT_HUM.TASKLISTVIEW_INPROGRESS_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDING_HUM};
	private List<String> lstValidTaskStatus = new List<String>{GLOBAL_CONSTANT_HUM.TASKLISTVIEW_INPROGRESS_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGTASK_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGRESPONSE_HUM};
	private Integer dataLimit = GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_FIVE_HUM; // Data Limit , currently set to five
	private Integer queryLimit = 2*dataLimit; // Query Limit , set at twice the size of data limit
	/**
	* Constructor of the class
	*
	* @param   NA
	* @return  NA    
	*/
	public WorkItems_C_HUM()
	{
	 	init();
	}

	public list <WorkItems_DTO_HUM> OverDueList {get;set;} // Over Due Items List
	public list <WorkItems_DTO_HUM> NearDueList {get;set;} // Near Due Items List
	public String sErrorMessage {get;set;} // Error Message : Needs to be displayed in red on UI
	public String sMessage {get;set;} // Info Message
	public Integer iOverDueCount{get;set;}
	public Integer iNearDueCount{get;set;}
	public String sobjectId {get; set;}	
	/**
	* Method is used initialize variables
	*
	* @param   NA
	* @return  NA    
	*/
	public void init()
	{
		OverDueList = new List<WorkItems_DTO_HUM>();
		NearDueList = new List<WorkItems_DTO_HUM>();
		sMessage = '';
		sErrorMessage = '';
		iOverDueCount = 0;
		iNearDueCount = 0;
	}
	
	/**
	* Method is called from Action to load the values on load of page. It is also called on click of Refresh button to update the Due Items list.
	*
	* @param   NA
	* @return  NA    
	*/
	public void populateWorkItems()
	{
		try
		{
			sMessage = '';
			sErrorMessage = '';
			OverDueList = populateOverDueItems();
			NearDueList = populateNearDueItems();
			createMessages(OverDueList,NearDueList);
			prepareTable(); 
			TotalDueItemCount();
		}
		catch(Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'WorkItems_C_HUM', 'populateWorkItems');
		}
	}

	/**
	* Returns the list of Over Due Items . The list is sorted on the basis of due date and then created date
	* <p>
	* Method is used to query Case and Task records that are over due and is owned by the logged in user.
	*
	* @param   NA
	* @return  List<WorkItems_DTO_HUM> list of over due iitems    
	*/
	private List<WorkItems_DTO_HUM> populateOverDueItems()
	{
		List<WorkItems_DTO_HUM> lstOverDueWork = new List<WorkItems_DTO_HUM>();
		for(Task t:[Select Id,task_number__c,ActivityDate , createdDate from Task where QueueOrUserId__c=:userinfo.getuserid() and ActivityDate <= :System.Today() and Status In :lstValidTaskStatus order by ActivityDate ASC , Createddate ASC  limit : queryLimit])
		{
			lstOverDueWork.add(new WorkItems_DTO_HUM(t.task_number__c , t.ActivityDate , t.Id , t.createdDate));
		}
		for(Case c: [Select Id ,Follow_Up_Due_Date__c , CaseNUmber , createdDate from Case where ownerid=:userinfo.getuserid() and Follow_Up_Due_Date__c <= :System.Today() and Status IN :lstValidCaseStatus order by Follow_Up_Due_Date__c ASC , Createddate ASC limit : queryLimit])
		{
			lstOverDueWork.add(new WorkItems_DTO_HUM(c.CaseNumber , c.Follow_Up_Due_Date__c , c.Id , c.createdDate));
		}
		lstOverDueWork.sort();
		return lstOverDueWork;
	}
	
	/**
	* Returns the list of Near Due Items . The list is sorted on the basis of due date and then created date
	* <p>
	* Method is used to query Case and Task records that are near due (Due within 3 days) and is owned by the logged in user.
	*
	* @param   NA
	* @return  List<WorkItems_DTO_HUM> list of near due iitems    
	*/
	private List<WorkItems_DTO_HUM> populateNearDueItems()
	{
		List<WorkItems_DTO_HUM> lstNearDueWork = new List<WorkItems_DTO_HUM>();
		for(Task t:[Select Id,task_number__c,ActivityDate , createdDate from Task where QueueOrUserId__c=:userinfo.getuserid() and ActivityDate > :System.Today() And  ActivityDate <= :System.Today() + GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_THREE_HUM and Status In :lstValidTaskStatus order by ActivityDate ASC , Createddate ASC  limit : queryLimit])
		{
			lstNearDueWork.add(new WorkItems_DTO_HUM(t.task_number__c , t.ActivityDate , t.Id , t.createdDate));
		}
		for(Case c: [Select Id ,Follow_Up_Due_Date__c , CaseNUmber , createdDate from Case where ownerid=:userinfo.getuserid() and Follow_Up_Due_Date__c > :System.Today() And Follow_Up_Due_Date__c <= :System.Today() + GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_THREE_HUM  and Status In :lstValidCaseStatus order by Follow_Up_Due_Date__c ASC , Createddate ASC  limit : queryLimit])
		{
			lstNearDueWork.add(new WorkItems_DTO_HUM(c.CaseNumber , c.Follow_Up_Due_Date__c , c.Id , c.createdDate));
		}
		lstNearDueWork.sort();
		return lstNearDueWork;
	}
	
	/**
	* Method is used to build Error and Info messages that needs to be diaplayed on UI on the basis of Size of the Over Due and Near Due Items
	*
	* @param   list <WorkItems_DTO_HUM> Over Due List
	* @param   list <WorkItems_DTO_HUM> Near Due List
	* @return  NA    
	*/
	private void createMessages( list <WorkItems_DTO_HUM> OverDue,  list <WorkItems_DTO_HUM> NearDue)
    {
    	if(OverDue.size() == 0  && NearDue.size() == 0)
    	{
    		sMessage = system.label.NoDueItem_WorkItem_HUM;
    	}
    	else if(OverDue.size() > dataLimit  && NearDue.size() > dataLimit)
    	{
				sErrorMessage = system.label.OverDueNearDueMessage_WorkItem_HUM_V_1;
			   		
    	}
    	else if(OverDue.size() > dataLimit  && NearDue.size() <= dataLimit)
    	{
				sErrorMessage = system.label.OverDueMessage_WorkItem_HUM_V_1;
    		
    	}
    	else if(OverDue.size() <= dataLimit  && NearDue.size() > dataLimit)
    	{
				sErrorMessage = system.label.NearDueMessage_WorkItem_HUM_V_1;
    		
    	}
    }
    
    /**
	* Method is used to Prepare table that needs to be displayed on UI. Maximum of 5 OverDue and  Near Due items will be displayed.
	*
	* @param   NA
	* @return  NA    
	*/
	private void prepareTable()
	{
		if(!OverDueList.isEmpty() && OverDueList.size() > dataLimit)
		{
			OverDueList = WorkItems_C_HUM.buildTable(OverDueList,dataLimit);
		}
		if(!NearDueList.isEmpty() && NearDueList.size() > dataLimit)
		{
			NearDueList = WorkItems_C_HUM.buildTable(NearDueList,dataLimit);
		}
	}
	
	/**
	* Returns the list of Work Items after filtering out the records. Maximum of 5 Items will be displaed 
	* <p>
	* Method is used to build the lists after removming extra items if any in the list
	*
	* @param   List<WorkItems_DTO_HUM> list of DUe Items , it can be more than 5 items
	* @param   Integer Size of table , set to 5
	* @return  List<WorkItems_DTO_HUM> list of final work items  , it will always 5 or less   
	*/
	private Static List<WorkItems_DTO_HUM> buildTable(List<WorkItems_DTO_HUM> workItemList , Integer tableSize)
	{
		List<WorkItems_DTO_HUM> buildWorkItem = new List<WorkItems_DTO_HUM>();
		for(integer i=0 ; i <tableSize ; i++)
		{
			buildWorkItem.add(workItemList[i]);	
		}
		return buildWorkItem;
	}
	/*
     * Method Name : TotalDueItemCount
     * Description : Get the total number of due Items.
     * Return type : void
     * Parameter   : NA
    */      
	public void TotalDueItemCount()
	{
		Integer iOverDueCase =[Select count() from Case where ownerid=:userinfo.getuserid() and Follow_Up_Due_Date__c <= :System.Today() and Status IN :lstValidCaseStatus ];
		Integer iOverDueTask = [Select count() from Task where QueueOrUserId__c=:userinfo.getuserid() and ActivityDate <= :System.Today() and Status In :lstValidTaskStatus ];
		iOverDueCount = (iOverDueCase + iOverDueTask);
		Integer iNearDueCase = [Select count() from Case where ownerid=:userinfo.getuserid() and Follow_Up_Due_Date__c > :System.Today() And Follow_Up_Due_Date__c <= :System.Today() + GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_THREE_HUM  and Status In :lstValidCaseStatus ];
		Integer iNearDueTask = [Select count() from Task where QueueOrUserId__c=:userinfo.getuserid() and ActivityDate > :System.Today() And  ActivityDate <= :System.Today() + GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_THREE_HUM and Status In :lstValidTaskStatus ];
		iNearDueCount =(iNearDueCase + iNearDueTask);
	}
	/*
     * Method Name : getComments
     * Description : Get the sObjectId for the case/Task on which mouse is hover
     * Return type : void
     * Parameter   : NA
    */      
    public void getComments()    
    {        
		sobjectId = ApexPages.CurrentPage().getParameters().get('sobjectIdValue');       
    } 

}