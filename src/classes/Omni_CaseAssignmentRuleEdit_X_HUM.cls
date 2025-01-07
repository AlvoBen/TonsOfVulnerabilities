/***************************************************************************************************************************************************
Apex Class Name  : Omni_CaseAssignmentRuleEdit_X_HUM
Version          : 1.0
Created Date     : 11/01/2017
Function         : Controller to create and Edit Omni Case Assignment Rules and Omni Rule Criteria                   
Test Class       : Omni_CaseAssignmentRuleEdit_T_HUM
****************************************************************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                 Description
----------------------------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor             163314                  12/27/2017            Original Version 
* Mohammed Noor             219044                  04/23/2017            REQ-357523 - Fix for Defect# 354993.
* Syed Mubarak T									03/19/2019			  REQ - 386810 - Omni Configuration Item changes
****************************************************************************************************************************************************/
public with sharing class Omni_CaseAssignmentRuleEdit_X_HUM 
{    
    private Omni_CaseAssignmentRuleEdit_S_HUM sLayer = new Omni_CaseAssignmentRuleEdit_S_HUM();
    
    public Omni_Case_Assignment_Rule__c omniRule {get; set;}
    
    public List<Omni_Rule_Criteria__c> ruleEntries = new List<Omni_Rule_Criteria__c>();
            
    public String ruleCriteriaJSON {get; set;}  

    public String objectJSON{get;set;}  
    public String selectedObjectName{get;set;}
    public Boolean bUnsavedRCError {get; set;}
	public Map<String,List<OmniRuleCriteria_Fields__mdt>> fieldsMap {get;set;}
           
    
    public String fieldListJSON {get; set;}
    
    public String fieldObjJSON {get; set;}
    
    public String updtCriteriaJSON {get; set;}
    
    public String recId {get; set;}
	
	public Boolean bNameError {get; set;}   // Error Flag for Invalid Name
     
    public Boolean bEvalOrderError {Get; Set;}  // Error Flag For Invalid Evaluation Order
    
    public Boolean bError {get; set;}  //Error Flag for any error during save
    
    /*    
    * Constructor
    * @param  NA
    * @return void     
    */
    public Omni_CaseAssignmentRuleEdit_X_HUM(ApexPages.StandardController controller) 
    {        
        recId = controller.getId();
        sLayer.construct(this);               
    }
    
	/*
    * This Method is used to get all the related Rule Criteria
    * @param    
    * @return void
    */
	public void getRuleCriterias()
	{	    
	    sLayer.getRuleCriterias(this);	    	   
	}
                        
    
   /*
    * This Method is used to get all the OmniRuleCriteria_Fields__mdt for the selected object
    * @param  null  
    * @return void
    */
    public void getFieldsListJSON()
    {
    
    	sLayer.getFieldsList(this);	
    }
    
    
    /*
    * This Method is used to get all the Picklist Values of the selected Field.
    * @param  String fieldName
    * @return String
    */
    @RemoteAction
    public static String getPicklistValues(String fieldName)
    {    	
    	return Omni_CaseAssignmentRuleEdit_S_HUM.getPicklistValues(fieldName);    
    }
      
        
    /*
    * This Method is used to create/update the records.
    * @param    
    * @return  void
    */
    public void saveRecord()
    {                   
    	sLayer.saveRecs(this);                           
    }
    
	
	/*
    * This Method is used to create/update the records.
    * @param    
    * @return  PageReference
    */
    public PageReference save()
    {    	    	
    	return sLayer.saveRedirect(this);                           
    } 
    
    /*    
    * This method is to display the Error Message if required fields are missing in rule criteria.    
    * @param    N/A
    * @return   PageReference   
    */
    public pageReference showErrorMessage()
    {    	
    	return sLayer.showErrorMessage();
    }   
}