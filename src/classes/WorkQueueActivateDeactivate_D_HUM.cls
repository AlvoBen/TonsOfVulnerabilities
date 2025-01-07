/*******************************************************************************************************************************
Apex Class Name :  WorkQueueActivateDeactivate_D_HUM
Version: 1.0
Created On      : 03/02/2016
Function        : Data layer for WorkQueueActivateDeactivate_C_HUM

Modification Log:  
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam            30706                 03/02/2016                  Original Version
* Harshith Mandya 			  50014					11/30/2106				  	Implemented REQ - 290990 -  removed all lookup related fields from
																				Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View
* Avinash Choubey 			  97207					05/23/2017				  		REQ - 304170 removed Created by Queue And Lastmodified by Queue for open cases/tasks 
* Mithra Bharadwaj                                                              US# 1990288    PR00094232 - 2021 MF6 - **P9 - Security** - Sharing  Remediate Checkmarx Security Scan results 
********************************************************************************************************************************/

Public with sharing Class WorkQueueActivateDeactivate_D_HUM
{
 	/**
    * Returns Work_Queue_Setup__c Work queue setup record along with case And queue views related to it.
    * <p>
    * Method to fetch work 	queue records And related cases, Queue View Items.
    *
    * @param    wqId 					Work queue Id
    * @return   Work_Queue_Setup__c		Work queue setup record And associated case, Queue View items
    */ 
	Public Work_Queue_Setup__c getWQViews(String wqId)
	{
		Return [Select id, Name, IsActive__c, Activate_Deactivate__c,(select id,Name,IsActive__c from Queue_Views__r) from Work_Queue_Setup__c where id=: wqId limit 1];		
	}
	
	/**
    * Returns List<user> list of users associated with the current queue.
    * <p>
    * Method to fetch users related to the current queue.
    *
    * @param    wqName 					Work queue Name
    * @return   List<user> 				list of users associated with the current queue.
    */ 
	Public List<User> getCurrentQueue_SOQL(String wqName)
	{
		Return [Select id, Current_Queue__c from User where Current_Queue__c =: wqName limit : (limits.getLimitQueryRows() - limits.getQueryRows())];
	} 
	
	
	/**
    * 
    * <p>
    * Method to update records - user, work queue And queue View items
    *
    * @param    lstSobjects 			List of records to be updated
    * @return   NULL
    */ 
	Public void updateRecords(List<sObject> lstSobjects)
	{
		update lstSobjects;
	} 
	
	/**
    * Returns List<Case>
    * <p>
    * Method to fetch Open Case related to the work queue.
    *
    * @param    sWQName 			Work Queue Name
    * @return   List<Case>			Open Case record
    */ 
	Public List<Case> getOpenCases(String sWQName)
	{
		Return [Select id FROM Case Where  Owner_Queue__c = :sWQName And isClosed = false limit 1];		
	}
	
	/**
    * Returns List<Task>
    * <p>
    * Method to fetch Open Task related to the work queue.
    *
    * @param    sWQName 			Work Queue Name
    * @return   List<Task>			Open task record
    */ 
	Public List<Task> getOpenTasks(String sWQName)
	{
		Return [Select id FROM Task Where  Owner_Queue__c = :sWQName And isClosed = false	limit 1];		
	}
	
}