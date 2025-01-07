/*******************************************************************************************************************************
Apex Class Name : WorkQueueDownload_D_HUM
Version         : 1.0
Created On      : 06/14/2016
Function        : Used to download file with details regarding the Work Queue Setup records
Test Class      : WorkQueueDownload_T_HUM

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam                  					06/14/2016                Original Version
* Pradeepkumar Dani				98235					06/06/2017                Classification_Type_Old__c Clean up
*******************************************************************************************************************************/
public with sharing class WorkQueueDownload_D_HUM 
{
	/**
    * Returns List<Queue_View__c>
    * <p>
    * Method to fetch Work_Queue_Setup__c and Queue_View__c details
    *
    * @return    List<Queue_View__c>
    */
	public List<Queue_View__c> qvInfo()
    {
    	return [Select Work_Queue_Name__r.Name,Work_Queue_Name__r.Public_Group_Queue_Id__c ,Work_Queue_Name__r.Security_Group__c,Work_Queue_Name__r.CokY__c,
                Work_Queue_Name__r.DST__c,Work_Queue_Name__r.General__c, Work_Queue_Name__r.HO_Dental__c,Work_Queue_Name__r.HO_Medical__c,Work_Queue_Name__r.Service_Center__c,
                Work_Queue_Name__r.Department__c,Work_Queue_Name__r.Queue_Description__c, Work_Queue_Name__r.Classification_Type__c, Work_Queue_Name__r.Classification_Type__r.Name,Work_Queue_Name__r.IsActive__c,Name,
                Public_Group_View_Id__c,IsActive__c from Queue_View__c limit :(limits.getLimitQueryRows() - limits.getQueryRows())];	
    } 
    
    /**
    * Returns List<Group> Names for the ids in the set
    * <p>
    * Method to fetch Group Names for the ids in the set
    *
    * @param     setGroupIds         set of PGQ and PGV ids
    * @return    List<Group>
    */
	public List<Group> grpName(set<ID> setGroupIds)
    {
    	return [Select Name from Group where Id in : setGroupIds limit :(limits.getLimitQueryRows() - limits.getQueryRows())];	
    }    
}