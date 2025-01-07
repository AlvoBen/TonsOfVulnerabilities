/*******************************************************************************************************************************
Apex Class Name : OmniEligibleCaseAssignment_B_HUM
Version         : 1.0
Created On      : 12/19/2017
Function        : REQ-350420: Some of the case assignment rules are based on parameters such as Case age 
                    which will change on a daily basis. This batch job needs to run nightly to 
                    reevaluate all Open Omni Eligible cases (in both Omni and staging queues) 
                    which will reassign the cases to different staging queues wherever applicable.
Test Class      : OmniEligibleCaseAssignment_T_HUM 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Manoj Chaurasia              167654                     12/19/2017              Original Version
*Manoj Chaurasia              178236                     02/01/2018              For open cases, used status not in (Closed, Cancelled)
*Manoj Chaurasia              186768                     02/16/2018              Added changes to avoid query time out in production.
*Mohammed Noor                202879                     03/21/2018              REQ - 359092 - Remove Creditable coverage Work Queue View filter from the SOQL query.
* Syed Mubarak T										04/01/2019			  REQ - 383800 - Add TRR Template Fields to Omni Evaluation
*Rohit Borude 			      349590       				 04/08/2019				 REQ - 392899 - Add Queues to Batch job used for Omni Channel routing
*Sagar MN                                         		01/14/2019               REQ - 416054 Checmarx Sharing Vulnerability Fix  
*******************************************************************************************************************************/


public with sharing class OmniEligibleCaseAssignment_B_HUM implements Database.Batchable<sObject>
{    
	public DateTime startTime = System.now();
	public DateTime endTime = System.now().addHours(2);
    
	 	/*
       * Returns open cases that needs to be evaluated
       * <p>
       * Method to execute pre logic
       *
       * @param     BC                      Database.BatchableContext
       * @return    Database.QueryLocator            
    	*/     
    public Database.QueryLocator start(Database.BatchableContext BC)
    { 
        String ERROR_APPLICATION = HUMConstants__c.getInstance('ERROR_APPLICATION') != null ? HUMConstants__c.getInstance('ERROR_APPLICATION').StringValue__c : '';
            
        try
		{
			Integer iJobs = [Select count() from AsyncApexJob where JobType ='BatchApex'
								AND Status = 'Holding'
								AND Id != : BC.getJobId()
								];
				
            // Apex flex Queue can hold 100 batches on hold
            if (iJobs > 100)
            {
                return null;
            }
            else
            {   
				List<Omni_Eligible_Queue_Views__mdt> lstWQ = new List<Omni_Eligible_Queue_Views__mdt>();
				Set<String> setWQ = new Set<String>();
				lstWQ = [Select id, WorkQueueName__c from Omni_Eligible_Queue_Views__mdt];
				for(Integer i=0;i<lstWQ.size();i++){
					setWQ.add(lstWQ[i].WorkQueueName__c); 
				}
                List<OmniRuleCriteria_Fields__mdt> lstFieldsType = [Select Label, Field_APIName__c, Field_Type__c From OmniRuleCriteria_Fields__mdt WHERE  Object_API_Name__c !=: GLOBAL_CONSTANT_HUM.TEMPLATE_SUBMISSON_DATA_OBJECT];
                String sCriteriaFields = '';
                for(OmniRuleCriteria_Fields__mdt  oField: lstFieldsType){
                    sCriteriaFields = sCriteriaFields +', '+ oField.Field_APIName__c;
                }
                 sCriteriaFields = sCriteriaFields.toLowerCase();
                 String query ='Select id';
                 if(!sCriteriaFields.Contains('ownerid')) query = query + ', ownerid';
                 if(!sCriteriaFields.Contains('omni_eligible__c')) query = query + ', omni_eligible__c';
                 if(!sCriteriaFields.Contains('work_queue_view_name__c')) query = query + ', work_queue_view_name__c';
                 if(!sCriteriaFields.Contains(' owner_queue__c')) query = query + ', owner_queue__c';
            	 if(!sCriteriaFields.Contains('case_owner__c')) query = query + ', case_owner__c';
            	 
                query = query  + sCriteriaFields
                       + ' From Case WHERE IsClosed=false AND Owner_Queue__c IN: setWQ AND Case_Owner__c IN: setWQ AND Omni_Eligible__c=true ';
                       
                return Database.getQueryLocator(query);
            }
		}
		catch(Exception e)
		{
            Error_Log__c oErrorLog = new Error_Log__c(Error_Message__c = e.getMessage(), class_name__c = 'OmniEligibleCaseAssignment_B_HUM', Method_Name__c = 'start', Object_Type__c = 'Case', Type__c = ERROR_APPLICATION);
            insert oErrorLog;
            system.abortjob(BC.getJobID());
            return null;
        }
    }
    
    /**
       * Method to process the records
       *
       * @param     BC          Database.BatchableContext
       * @param     batch       List of case records
       * @return    void            
    */
    public void execute(Database.BatchableContext BC, List<Case> Cases)
    {
    	 if(endTime > System.now())
	        {
		        List<Case> lstCase = new List<Case>();
		        for(Case oCase : Cases)
		        {
		            if(oCase.Owner_Queue__c.equalsIgnoreCase(oCase.Case_Owner__c)) {
		                lstCase.add(oCase);
		            }
		        }	            
	        //pass lstCase to Assignment rule evaluation  
	        OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstCase, true);                      
        }
        else
        {
           	system.abortjob(BC.getJobID());
        }
    }
    
     /**
       * Method to execute post logic
       *
       * @param     BC          Database.BatchableContext
       * @return    void            
    */
    public void finish(Database.BatchableContext BC)
    {
        
    }
        
}