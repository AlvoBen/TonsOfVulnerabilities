/*****************************************************************************************
Apex class Name    : HUMCaseTaskHelper
Version            : 1.0
Function           : This wrapper class is used for holding all searched records for Case&Task.
Created On         : June 9 2014
Test Class         : HUMCaseTaskHelperTest

Modification Log:
*   Developer                   Code Review     Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Mrudula Jaddu                 17093         06/09/2014          Original Version
*   Mrudula Jaddu                 17492         08/21/2014          commented Date/Time Closed as per the change in requirement 1137
*   Raja Sekhara Reddy M V        17492         08/28/2014          Fixed code review comments
*   Mrudula Jaddu                 18811         10/28/2014          Modified interacting about with Account.Name as per defect 158714
*   Manish Kumar                  18933         11/04/2014          Code fix#159932,#159931
*   Manish Kumar                  19218         11/12/2014          Code fix for defect#160822, #160818
*   Manish Kumar                  22086         02/04/2015          Code fix as part of Case/Task optimization. Splitted constructors for Case and Task. Removed few more variables
* 	Sagar Tapadia				  25679			08/20/2015			Changed done on datetime field for Cases/Task to show in local time zone.Change is in Line #60 and #92.
*   Prachi Bhasin                 42551         07/18/2016          Changes made as part of REQ- 270349
*	Pradeepkumar Dani		      42551 		10/05/2016			REQ-00669, REQ-244446, REQ-244447, REQ-244449 Changes
********************************************************************************************************************************/

public with sharing class HUMCaseTaskHelper implements Comparable
{
    private static final String sPCritical    = GLOBAL_CONSTANT_HUM.CRITICAL_HUM;
    private static final String sPHigh        = GLOBAL_CONSTANT_HUM.HIGH_HUM;
    private static final String sPNormal      = GLOBAL_CONSTANT_HUM.NORMAL_HUM;
    private static final String sPriorityNAge = GLOBAL_CONSTANT_HUM.PRIORITYANDAGE_HUM;
    private static final String sAge          = GLOBAL_CONSTANT_HUM.AGE_HUM;
    private static final String sSPriority    = GLOBAL_CONSTANT_HUM.PRIORITY_HUM;
    private static final String sId           = GLOBAL_CONSTANT_HUM.ID2_HUM;
    private static final String sDueDate      = GLOBAL_CONSTANT_HUM.DUEDATE_HUM;
    
    
    /**
	* This is the constructor to the class used for instantiating Case 
	*
	* @param  Case, String, Boolean, String 	 	
	* @return  None
	*/
    public HUMCaseTaskHelper(Case objCase, string sInteractngWith, boolean bIsSelected, String sSortType, Boolean isRead, Boolean isMerge)
    {
        this.sCasetaskId             = objCase.Id;
        this.sStatus                 = objCase.Status;
        this.sPriority               = objCase.Priority;
        this.iPriorityNumber         = this.sPriority == sPCritical ? 0 : (this.sPriority == sPHigh ? 1 : (this.sPriority == sPNormal ? 2 : -1));
        this.sCaseTaskNumber         = objCase.CaseNumber;
        this.sInteractingWith        = sInteractngWith;
        this.sInteractingWithId      = objCase.Interacting_With__c;
        this.sInteractingAbout       = objCase.Account.Name;
        this.sSubject                = objCase.Subject;
        this.sQueueUserName          = objCase.Owner_Queue__c;
        this.dtDueDate               = objCase.Due_Date__c;
        this.decAge                  = objCase.Case_Age__c;
        this.bIsSelected             = bIsSelected;
        this.sOwnerId                = objCase.Case_Owner__c;
        if(NULL != objCase.Classification_Id__c) this.sCategory = objCase.Classification_Id__r.Name;
        if(NULL != objCase.Intent_Id__c) this.sReason = objCase.Intent_Id__r.Name;
        this.decCaseOrAge            = objCase.Re_Open_Case_Age__c;
        this.dtFollow_up_Date        = objCase.Follow_Up_Due_Date__c;
        if(objCase.CreatedDate!=null)
        {
        	this.dtCreatedDate = DateTime.newInstanceGMT(objCase.CreatedDate.date(),objCase.CreatedDate.time());
        }
        this.sSortType               = sSortType;
        this.bIsRead = isRead;
        this.bIsMerged = isMerge;
    }
    
    /**
	* This is the constructor for class used for instantiating Task 
	*
	* @param  Task, String, String, String, Date, Boolean, String 	 	
	* @return  None
	*/
    public HUMCaseTaskHelper(Task objTask, string sInteractngWith, string sInteractngWithId, string sInteractingAbout, Date dtFollowUpDate, boolean bIsSelected, String sSortType, Boolean isRead, Boolean isMerge)
    {
        this.sCasetaskId             = objTask.Id;
        this.sStatus                 = objTask.Status;
        this.sPriority               = objTask.Priority;
        this.iPriorityNumber         = this.sPriority == sPCritical ? 0 : (this.sPriority == sPHigh ? 1 : (this.sPriority == sPNormal ? 2 : -1));
        this.sCaseTaskNumber         = objTask.Task_Number__c;
        this.sInteractingWith        = sInteractngWith;
        this.sInteractingWithId      = sInteractngWithId;
        this.sInteractingAbout       = sInteractingAbout;
        this.sSubject                = objTask.Subject;
        this.sQueueUserName          = objTask.Owner_Queue__c;
        this.dtDueDate               = objTask.ActivityDate;
        this.decAge                  = objTask.Age_Since_Created__c;
        this.bIsSelected             = bIsSelected;
        this.sOwnerId                = objTask.Task_Owner__c;
        this.sCategory               = objTask.Classification__c;
        this.sReason                 = objTask.Intent__c;        
        this.decCaseOrAge            = NULL;
        this.dtFollow_up_Date        = dtFollowUpDate;
        if(objTask.CreatedDate!=null)
        {
        	this.dtCreatedDate = DateTime.newInstanceGMT(objTask.CreatedDate.date(),objTask.CreatedDate.time());
        } 
        this.sSortType               = sSortType;
        this.bIsRead = isRead;
        this.bIsMerged = isMerge;
    }
    
    /**
	* This is the default constructor for this class without parameter(s) 
	*
	* @param  None 	 	
	* @return  None
	*/
    public HUMCaseTaskHelper(){}
    
    public String   sCasetaskId{get; set;}    
    public String   sStatus{get; set;}
    public String   sPriority {get; set;}
    private Integer iPriorityNumber;
    public String   sCaseTaskNumber {get; set;}
    public String   sInteractingWithId {get;set;}
    public String   sInteractingWith {get;set;}
    public String   sInteractingAbout {get; set;}
    public String   sCategory{get; set;}
    public String   sReason{get; set;}
    public String   sSubject {get; set;}
    public String   sQueueUserName {get; set;}
    public Date     dtDueDate {get; set;}
    public Decimal  decAge {get; set;}
    public Boolean  bIsSelected {get;set;} 
    public String   sOwnerId {get;set;}
    public decimal  decCaseOrAge{get;set;}
    public date     dtFollow_up_Date{get;set;}
    public datetime dtCreatedDate{get;set;}
    public String   sSortType;
    public Boolean  bIsRead {get;set;}
    public Boolean  bIsMerged {get;set;}
    
    /**
	* This method is used to sort the list of this class 
	*
	* @param  Object 	 	
	* @return  Integer
	*/
    public Integer compareTo(Object compareTo)
    {
        HUMCaseTaskHelper compareToCaseTask = (HUMCaseTaskHelper)compareTo;
        
        if(this.sSortType == sPriorityNAge)
        {
            if (this.iPriorityNumber == compareToCaseTask.iPriorityNumber)
            {
                if (this.decAge == compareToCaseTask.decAge) return 0;
                else if (this.decAge < compareToCaseTask.decAge) return 1;
                return -1;
            }
            else if (this.iPriorityNumber > compareToCaseTask.iPriorityNumber) return 1;
            return -1;
        }
        else if(this.sSortType == sAge)
        {
            if (this.decAge == compareToCaseTask.decAge) return 0;
            else if (this.decAge < compareToCaseTask.decAge) return 1;
            return -1;
        }
        else if(this.sSortType == sDueDate)
        {
            if (this.dtDueDate == NULL)    return 1;
            else if (this.dtDueDate == compareToCaseTask.dtDueDate) return 0;
            else if (this.dtDueDate > compareToCaseTask.dtDueDate) return 1;
            return -1;
        }
        else if(this.sSortType == sSPriority)
        {
            if (this.iPriorityNumber == compareToCaseTask.iPriorityNumber) return 0;
            else if (this.iPriorityNumber > compareToCaseTask.iPriorityNumber) return 1;
            return -1;
        }
        else if(this.sSortType == sId)
        {
            if (this.sCaseTaskNumber == compareToCaseTask.sCaseTaskNumber) return 0;
            else if (this.sCaseTaskNumber > compareToCaseTask.sCaseTaskNumber) return 1;
            return -1;
        }
        return 0;
    }
}