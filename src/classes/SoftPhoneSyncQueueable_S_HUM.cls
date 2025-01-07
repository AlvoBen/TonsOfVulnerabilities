/*******************************************************************************************************************************
Apex Class Name     : SoftPhoneSyncQueueable_S_HUM
Version             : 1.0
Created Date        : June 12 2020
Function            : Queueable class to process sync softphone agents records
Test Class 			: SoftPhoneSyncQueueable_T_HUM

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Akshay Pai                                    		06/12/2020                  Initial Version
*******************************************************************************************************************************/
public with sharing class SoftPhoneSyncQueueable_S_HUM implements Queueable, Database.AllowsCallouts 
{    
	public List<Softphone_Agent__c> agentsToSync;
	public Set<Softphone_Agent__c> agentsToUpdateSet;
	public Set<Softphone_Agent__c> agentsFailedToInsertSet;
	public Integer totalNoOfAgents = 0;
    public Integer counter = 1;
    public Integer max = 20;
	public map<id,string> agentsSiteIdNameMap = new map<id,string>();
	public map<id,string> agentsLinkIdNameMap = new map<id,string>();
	public map<id,string> agentsLinkIdServerNameMap = new map<id,string>();
    
    public SoftPhoneSyncQueueable_S_HUM(List<Softphone_Agent__c> agentsToSync,Set<Softphone_Agent__c> agentsToUpdateSet,Set<Softphone_Agent__c> agentsFailedToInsertSet)
    {
        this.agentsToSync = agentsToSync;
		this.agentsToUpdateSet = agentsToUpdateSet;
		this.agentsFailedToInsertSet = agentsFailedToInsertSet;
		
		HUM_Webservice_Callout__c softphoneCallout = HUM_Webservice_Callout__c.getValues('AgentSoftPhoneGetDBAgents');
		if (softphoneCallout != null && String.isNotBlank(softphoneCallout.SampleParameter__c))
		this.max = Integer.valueOf(softphoneCallout.SampleParameter__c);
    }
    
	/*
    * Method Name : execute
    * Description : Execute method of Queueable interface
    * Return type : None
    * Parameter   : None
    */
    public void execute(QueueableContext context)
    {   
		SoftPhoneSyncQueueable_H_HUM oServiceHandler = new SoftPhoneSyncQueueable_H_HUM(this);
		oServiceHandler.init();       
    }
}