/****************************************************************************
Apex Class Name  : TopicDeactivation_C_HUM
Version          : 1.0 
Created Date     : June 10 2016
Function         : Class for deactivating the active topics which includes validations to be done prior to deactivation
TestClass Name   : TopicDeactivation_T_HUM
Modification Log :
 *  Modification ID     Developer              Code Review           Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *          1.0         Sharan Shanmugam                            06/10/2016           Original Version                                                                                     
 **********************************************************************************************************************************/
public with sharing class TopicDeactivation_C_HUM 
{
	private TopicDeactivation_S_HUM sLayer = new TopicDeactivation_S_HUM();
    public Work_Queue_Topic__c topic = new Work_Queue_Topic__c();
    public ID sWQSId = NULL;
	
     /**
   * invokes service layer for constructing the variables.
   * <p>
   * Constructor Method to for the class , Initialize variables
   */
    public TopicDeactivation_C_HUM()
    {
        workQueueSetup = new  Work_Queue_Setup__c();
        lstTopic = new List<SelectOption>();
        bActiveTopic = false;
        sTopic = '';
        sLayer.initialize(this);        
    }

    public Work_Queue_Setup__c workQueueSetup{get;set;}
    public List<SelectOption> lstTopic{get;set;}
    public String sTopic{get;set;}
    public Boolean bActiveTopic {get;set;}    
    
     /**
    * Returns PageReference url when topic is deactivated
    * <p>
    * Method to deactivate topic.
    *
    * @param	NULL
    * @return	PageReference
    */ 
    public PageReference deactivateTopic()
    {
        return sLayer.deactivateTopic(this);
    }
    
     /**
    * Returns PageReference url when deactivation of topic is cancelled
    * <p>
    * Method to cancel the deactivation of topic.
    *
    * @param	NULL
    * @return	PageReference
    */ 
    public pageReference cancel()
    {
        return sLayer.cancel(this);
    }
}