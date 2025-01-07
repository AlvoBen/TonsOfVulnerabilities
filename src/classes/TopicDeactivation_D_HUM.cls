/****************************************************************************
Apex Class Name :  TopicDeactivation_D_HUM
Version: 1.0 
Created Date     : June 10 2016
Function         : Class for deactivating the active topics which includes validations To be done prior To deactivation
TestClass Name :  TopicDeactivation_T_HUM
Modification Log : 
 *  Modification ID     Developer              Code Review           Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *          1.0         Sharan Shanmugam                            06/10/2016           Original Version  
 * 			1.1			Harshith Mandya 		50014				11/30/2106		     Implemented REQ - 290990 -  removed all lookup related fields from 
  																					     Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View                                                                                    
 **********************************************************************************************************************************/
Public With sharing Class TopicDeactivation_D_HUM 
{
	/**
    * Returns Work_Queue_Setup__c details with the id from URL
    * <p>
    * Method to fetch Work_Queue_Setup__c details with the id from URL
    *
    * @param     sWQSId                	Work Queue id
    * @return    Work_Queue_Setup__c
    */
	Public Work_Queue_Setup__c wqInfo(id sWQSId)
    {
    	Return [select id,name from Work_Queue_Setup__c where id=: sWQSId];	
    } 
    
    /**
    * Returns List<Work_Queue_Junction__c> for the work queue id
    * <p>
    * Method to fetch Work_Queue_Junction__c details for the work queue id
    *
    * @param     sWQSId                			Work Queue id
    * @return    List<Work_Queue_Junction__c>
    */
    Public List<Work_Queue_Junction__c> getWQJ(id sWQSId)
    {
    	Return [Select Work_Queue_Topic_Name__c, Work_Queue_Topic_Name__r.Name, Deactivate_Topic__c from Work_Queue_Junction__c where Work_Queue_Setup_Name__c =: sWQSId 
    			And Work_Queue_Topic_Name__r.IsActive__c = true limit :(limits.getLimitQueryRows() - limits.getQueryRows())];
    } 
    
    /**
    * Returns Work_Queue_Topic__c details for the id provided
    * <p>
    * Method to fetch Work_Queue_Topic__c And Work_Queue_Juntion__c details
    *
    * @param     sTopic                	Work Queue Topic id
    * @return    Work_Queue_Topic__c
    */
    Public Work_Queue_Topic__c getWQT(id sTopic)
    {
    	Return [Select Name, IsActive__c, (Select Work_Queue_Setup_Name__c, Work_Queue_Setup_Name__r.Name from Work_Queue_Juntions__r where Work_Queue_Topic_Name__c =: sTopic) 
    			from Work_Queue_Topic__c where id =: sTopic];
    }   
    
    /**
    * Returns List<Case> for a particular work queue And topic
    * <p>
    * Method to get open cases for a particular work queue And topic
    *
    * @param     wqName                	Work Queue Name
    * @param     wqtName                Topic name
    * @return    List<Case>
    */
    Public List<Case> getCaseTopic(String wqName, String wqtName)
    {
    	Return [Select id, Topic__c from Case where Owner_Queue__c =: wqName And Topic__c =: wqtName And isClosed = false limit 1];
    }  
    
    /**
    * Returns List<Task> for a particular work queue And topic
    * <p>
    * Method to get open tasks for a particular work queue And topic
    *
    * @param     wqName                	Work queue name
    * @param     wqtName                Topic name
    * @return    List<Task>
    */
    Public List<Task> getTaskTopic(String wqName, String wqtName)
    {
    	Return [Select id, Topic__c from Task where Owner_Queue__c =: wqName And Topic__c =: wqtName And isClosed = false limit 1];
    } 
    
    /**
    * Returns List<Queue_View__c> to get the queue views related to work queue
    * <p>
    * Method to get the queue views related to work queue
    *
    * @param     id                	  work queue id
    * @return    List<Queue_View__c>
    */
    Public List<Queue_View__c> getQVFromWQ(id sWQSId)
    {
    	Return [Select id, Name,(Select id, Name, API_Name__c, Field_Operator__c, Field_Value__c from Queue_View_Items__r  
    			limit:(limits.getLimitQueryRows() - limits.getQueryRows())) from Queue_View__c where Work_Queue_Name__c =: sWQSId];
    }   
    
    /**
    * Method to delete queue view item
    *
    * @param     lstDeleteQvi               list of queue view items to delete
    * @return    void
    */
    Public void deleteQVI(List<Queue_View_Item__c> lstDeleteQvi)
    {
        SObjectType myObj = Schema.getGlobalDescribe().get('Queue_View_Item__c');
        if(myObj.getDescribe().isDeletable())
    	delete(lstDeleteQvi);
    } 
    
    /**
    * Method to update the work queue topic record after 
    *
    * @param     topic              Work queue topic to be updated
    * @return    void
    */
    Public void updateTopic(Work_Queue_Topic__c topic)
    {
    	update topic;
    } 
}