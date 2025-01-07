/*******************************************************************************************************************************
Apex Class Name : WorkQueueDownload_C_HUM
Version         : 1.0
Created On      : 06/14/2016
Function        : Used to download file with details regarding the Work Queue Setup records
Test Class      : WorkQueueDownload_T_HUM

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*   Sharan Shanmugam                  					06/14/2016                Original Version
*******************************************************************************************************************************/

public with sharing class WorkQueueDownload_C_HUM
{    
	private WorkQueueDownload_S_HUM sLayer = new WorkQueueDownload_S_HUM();
	
    /**
    * invokes service layer to fetch work queue and queue view
    * <p>
    * Constructor Method to for the class
    *
    */
    public WorkQueueDownload_C_HUM() 
    {
        lstQueueView = new List<Queue_View__c>();
        mapGroups = new Map<Id,Group>();
        sLayer.initialize(this); 
    }
    
    //Contains Group Id as key and Corresponding Group as value
    public Map<Id,Group>  mapGroups {get;set;}
    //List of Queue View records with relationship to Work Queue
    public List<Queue_View__c> lstQueueView {get;set;}
    
}