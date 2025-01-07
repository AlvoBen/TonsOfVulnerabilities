/*******************************************************************************************************************************
Apex Class Name :  QueueViewActivateDeactivate_D_HUM
Version: 1.0
Created On      : 06/13/2016 
Function        : Data layer for QueueViewActivateDeactivate_VF_HUM

Modification Log:  
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya          	  		                06/13/2016                   Original Version
* Harshith Mandya 			 50014					11/30/2106				  	 Implemented REQ - 290990 -  removed all lookup related fields from
																				 Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View
* Jasmeen Shangari                                  02/22/2021                   CheckMarx fix -update class with sharing																				 																				 
********************************************************************************************************************************/

public with sharing class QueueViewActivateDeactivate_D_HUM
{
    /**
    * Returns Queue View record
    * <p>
    * Method to fetch work 	queue records And related cases, Queue View Items.
    *
    * @param    sQVId 				Queue View Id
    * @return   Queue_View__c		Queue View record
    */ 
	Public Queue_View__c getQueueView(String sQVId)
	{
		Return [Select id,Name,IsActive__c,Activate_Deactivate__c from Queue_View__c where id =: sQVId limit 1];
	}
	
	/**
    * 
    * <p>
    * Method to update records - queue view records
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
    * Method to fetch Open Case related to Queue View.
    *
    * @param    sQVName 			Queue View Name
    * @return   List<Case>			Open Case record
    */ 
	Public List<Case> getOpenCases(String sQVName)
	{
		Return [Select id FROM Case Where Work_Queue_View_Name__c = :sQVName And isClosed = false limit 1];		
	}
	
	/**
    * Returns List<Task>
    * <p>
    * Method to fetch Open Task related to the Queue View.
    *
    * @param    sQVName 			Queue View Name
    * @return   List<Task>			Open task record
    */ 
	Public List<Task> getOpenTasks(String sQVName)
	{
		Return [Select id FROM Task Where Work_Queue_View_Name__c = :sQVName And isClosed = false limit 1];		
	}
}