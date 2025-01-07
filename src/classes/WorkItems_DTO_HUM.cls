/******************************************************************************************************************
Apex class Name    : WorkItems_DTO_HUM
Version            : 1.0
Function           : Wrapper for Displaying Due Items on Home Page , it implements comparable interface to implement sorting.
Created On         : September 19 2016
Test Class         : WorkItems_T_HUM

Modification Log:
*   Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*   Vishal Verma					      40074            09/19/2016          Original Version -> REQ-244451 : Due items on Home Tab 
******************************************************************************************************************/
public with sharing class WorkItems_DTO_HUM implements comparable
{
	/**
	* Constructor for the class
	*
	* @param   String Case/Task Number of Case/Task
	* @param   Date Due Date of Case/Task
	* @param   String Id of Case/Task
	* @param   DateTime CreatedDate of Case/Task
	* @return  NA
	*/
	public WorkItems_DTO_HUM(String sId , Date dDDate , String sRecId ,Datetime dCrDate)
	{
		sCaseTaskId = sId;
		dDueDate = dDDate;
		sRecordId = sRecId;
		dCreatedDate= dCrDate;
	}
	
	public String sCaseTaskId {get;set;} // Case/Task Number of Case/Task
	public String sRecordId {get;set;} // Id of Case/Task
	public Date dDueDate {get;set;} // Due Date of Case/Task
	public DateTime dCreatedDate {get;set;} //CreatedDate of Case/Task
	
	/**
	* Returns 1 , 0 , -1 based on the sorting results
	* <p>
	* Method is used to sort the wrapper/DTO Class. Name of the method cant be changed as it is from Comparable interface. 
	* As the interface will always return 1 , 0 and -1 , hence the values are not moved to constants.
	*
	* @param   Object 	   Instance of the Wrapped which is being comapred
	* @return  Integer    
	*/
	public Integer compareTo(Object compareTo)
    {
        WorkItems_DTO_HUM workItem = (WorkItems_DTO_HUM)compareTo;
        if(dDueDate > workItem.dDueDate) 
        {
            return 1;
        }
        else if(dDueDate == workItem.dDueDate)
        {
        	return 0;
        }
        return -1;
    } 
}