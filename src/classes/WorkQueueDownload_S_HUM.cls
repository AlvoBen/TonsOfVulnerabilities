/*******************************************************************************************************************************
Apex Class Name : WorkQueueDownload_S_HUM
Version         : 1.0
Created On      : 06/14/2016
Function        : Used to download file with details regarding the Work Queue Setup records
Test Class      : WorkQueueDownload_T_HUM

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*   Sharan Shanmugam                  					06/14/2016                Original Version
*******************************************************************************************************************************/

public with sharing class WorkQueueDownload_S_HUM 
{
	private WorkQueueDownload_D_HUM dLayer = new WorkQueueDownload_D_HUM();
	
	/**
    * Method to fetch work queue and queue view details
    *
    * @return    void
    */
    public void initialize(WorkQueueDownload_C_HUM cls)
    {
        try
        {
	        //Store Group Ids 
	        set<ID> setGroupIds = new set<ID>();
	        cls.lstQueueView = dLayer.qvInfo();	        
	        for(Queue_View__c queueView : cls.lstQueueView)
	        {
	           setGroupIds.add(queueView.Public_Group_View_Id__c);
	           setGroupIds.add(queueView.Work_Queue_Name__r.Public_Group_Queue_Id__c);
	        }                         	       
	        //Get the Group ID Names 
	        cls.mapGroups = new Map<Id,Group>(dLayer.grpName(setGroupIds));                                                          
	        cls.mapGroups.put(null,new Group(Name = ''));   
	        //Remove any Groups that are deleted
	        for(ID groupId : setGroupIds)           
	        {
	            if(!cls.mapGroups.containsKey(groupId)) cls.mapGroups.put(groupId,new Group(Name = ''));
	        }  
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.bRestrictLog = true;
            HUMExceptionHelper.logErrors(ex,'WorkQueueDownload_S_HUM','initialize');
        }
    }    
}