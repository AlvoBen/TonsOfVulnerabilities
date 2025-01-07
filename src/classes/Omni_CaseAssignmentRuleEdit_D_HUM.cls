/***************************************************************************************************************************************************
Apex Class Name  : Omni_CaseAssignmentRuleEdit_D_HUM
Version          : 1.0
Created Date     : 11/01/2017
Function         : Controller to create and Edit Omni Case Assignment Rules and Omni Rule Criteria                   
Test Class       : Omni_CaseAssignmentRuleEdit_T_HUM
****************************************************************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                   Description
----------------------------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor              163314                 12/27/2017              Original Version 
* Mohammed Noor              170613                 01/16/2017              Fix for Defect# 338205.  
* Syed Mubarak T									03/19/2019				REQ - 386810 - Omni Configuration Item changes
* Syed Mubarak T									03/31/2019			  REQ - 386735 - Adding OR Operator to Rule Criteria
* Sumanth Naredla                                   01/31/2020            REQ - 416751 - Check marx fix
****************************************************************************************************************************************************/
public with sharing class Omni_CaseAssignmentRuleEdit_D_HUM 
{
	/*
    * Returns the list of all Omni Case Assignment Rules        
    * @param    NA
    * @return   List<Omni_Case_Assignment_Rule__c>
    */
	public List<Omni_Case_Assignment_Rule__c> getAllRules()
	{
		return [SELECT Id, Evaluation_Order__c FROM Omni_Case_Assignment_Rule__c ORDER BY Evaluation_Order__c desc];
	}
	
	/*
    * Returns the Omni Case Assignment Rule Object        
    * @param    RecordId
    * @return   Omni_Case_Assignment_Rule__c
    */
	public Omni_Case_Assignment_Rule__c getRuleRecord(Id recordId)
	{
		return [SELECT ID, Name, Is_Active__c, Omni_Queue__c, Evaluation_Order__c, WorkQueue__c, WorkQueueView__c, Case_Assignment_Rule_Criteria__c,Case_Assignment_Rule_Map__c FROM Omni_Case_Assignment_Rule__c where ID = :recordId];
	}
	
	/*
    * Returns the List of OmniRuleCriteria_Fields__mdt (custom metadata type)      
    * @param    RecordId
    * @return   List<OmniRuleCriteria_Fields__mdt>
    */
	public List<OmniRuleCriteria_Fields__mdt> getFieldsFromMetaData()
	{
		return [SELECT MasterLabel, Field_APIName__c, Field_Type__c, Object_Name__c FROM OmniRuleCriteria_Fields__mdt ORDER BY MasterLabel ];
	}
	
	
	/*
    * Returns the List of all Omni Rule Criteria for an Omni Case Assignment rule      
    * @param    RecordId of the Parent Omni Case Assignment Rule
    * @return   List<Omni_Rule_Criteria__c>
    */
	public List<Omni_Rule_Criteria__c> getCriterias(Id recordId)
	{
		return [Select Id, Name, Criteria_Field__c, Criteria_Field_Value__c, Criteria_Operator__c, Omni_Case_Assignment_Rule__c,Criteria_Set__c FROM 
	               		   Omni_Rule_Criteria__c WHERE Omni_Case_Assignment_Rule__c = :recordId ORDER BY Name];
	}
	
	/*
    * Returns the OmniRuleCriteria_Fields__mdt of the selected Field Name      
    * @param    String Field Name
    * @return   OmniRuleCriteria_Fields__mdt
    */
	public static OmniRuleCriteria_Fields__mdt getFieldDetails(String fieldName)
	{
		return [SELECT MasterLabel, Field_APIName__c, Field_Type__c,Object_API_Name__c FROM OmniRuleCriteria_Fields__mdt WHERE MasterLabel = :fieldName Limit 1];
	} 
	
	/*
    * Returns Boolean verifies whether record inserted is successful or not.    
    * This method is used to create/update sObjects.  
    *
    * @param    lstsObject	List of sObjects
    * @return   Boolean
    */
     public Boolean upsertsObject(List<sObject> lstsObject, String objectName)
    {
        SObjectType myObj = Schema.getGlobalDescribe().get(objectName);
        Database.UpsertResult[] lstUpsertResults = Database.upsert(lstsObject,false);        
        Boolean bError = HUMExceptionHelper.processUpsertResults(lstUpsertResults, lstsObject, 'Omni_CaseAssignmentRuleEdit_D_HUM', 'upsertsObject', objectName);
        return bError;
    }
    
    /*    
    * This method is used to delete records.
    *
    * @param    NULL
    * @return   Void
    */
     public void deleteRecords(List<sObject> lstSobject, string sobjectName)
    {
        SObjectType myObj = Schema.getGlobalDescribe().get(sobjectName);
        if(myObj.getDescribe().isDeletable()) 
        delete lstSobject;
    }     
    
}