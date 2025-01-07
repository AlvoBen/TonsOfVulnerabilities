/***************************************************************************************************************************************************
Apex Class Name  : Omni_CaseAssignmentRuleEdit_S_HUM
Version          : 1.0
Created Date     : 11/01/2017
Function         : Controller to create and Edit Omni Case Assignment Rules and Omni Rule Criteria                   
Test Class       : Omni_CaseAssignmentRuleEdit_T_HUM
****************************************************************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                 Description
----------------------------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor             163314                  12/27/2017            Original Version 
* Mohammed Noor             164665                  01/03/2018            Add Logic to update the Criteria Summary field on Omni Case Assignment Rule.
* Mohammed Noor             170613                  01/16/2017            Fix for Defect# 338205.
* Mohammed Noor             219044                  04/23/2017            REQ-357523 - Fix for Defect# 354993.
* Syed Mubarak T									03/19/2019			  REQ - 386810 - Omni Configuration Item changes
* Syed Mubarak T									03/31/2019			  REQ - 386735 - Adding OR Operator to Rule Criteria
* Sumanth Naredla                                   01/31/2020            REQ - 416751 - Check marx fix
****************************************************************************************************************************************************/
public with sharing class Omni_CaseAssignmentRuleEdit_S_HUM 
{
    private Omni_CaseAssignmentRuleEdit_D_HUM dLayer = new Omni_CaseAssignmentRuleEdit_D_HUM();
    
    private static String omniCaseAssignmentRule = 'Omni_Case_Assignment_Rule__c';
    
    private static String omniRuleCriteria = 'Omni_Rule_Criteria__c';
    
    /*    
    * This method is to initialize data needed for page
    *
    * @param    cls     Instance of Omni_CaseAssignmentRuleEdit_X_HUM Class
    * @return   Void
    */
    public void construct(Omni_CaseAssignmentRuleEdit_X_HUM cls)
    {
        if(cls.recId == null)   //new Record Creation
        {
            cls.omniRule = new Omni_Case_Assignment_Rule__c();
            cls.omniRule.Name = '';
            cls.omniRule.Is_Active__c = true;
            cls.omniRule.Omni_Queue__c = null;
            cls.omniRule.WorkQueue__c = null;
            cls.omniRule.WorkQueueView__c = null;
			cls.omniRule.Case_Assignment_Rule_Criteria__c = '';
			
            List<Omni_Case_Assignment_Rule__c> listAllRules = dLayer.getAllRules();
            if(listAllRules!=null && !listAllRules.isEmpty())
            {
                cls.omniRule.Evaluation_Order__c = listAllRules[0].Evaluation_Order__c + 1;
            }
            else
            {
                cls.omniRule.Evaluation_Order__c = 1;
            }
        }
        else   //edit of the Record
        {
            cls.omniRule = dLayer.getRuleRecord(cls.recId);                                    
        }
        
        Set<String> objectList = new Set<String>();

     	List<OmniRuleCriteria_Fields__mdt> lstFieldObj = dLayer.getFieldsFromMetaData();  
         
         //get all the objects       
        cls.fieldObjJSON = JSON.serialize(lstFieldObj);
        cls.fieldObjJSON = cls.fieldObjJSON.replace('\'', '\\\''); 
        
        //get map of object name and rule criteria list
        cls.fieldsMap = new Map<String,List<OmniRuleCriteria_Fields__mdt>>();
        
        // loop to create map of fields and set of objects
        for(OmniRuleCriteria_Fields__mdt rcFeilds: lstFieldObj)
        {
        	List<OmniRuleCriteria_Fields__mdt> rcFieldsList = new List<OmniRuleCriteria_Fields__mdt>();
			if(String.isNotBlank(rcFeilds.Object_Name__c))
			{
				if(cls.fieldsMap.containsKey(rcFeilds.Object_Name__c))
				{
					rcFieldsList = cls.fieldsMap.get(rcFeilds.Object_Name__c);
				}
				//create Map
				rcFieldsList.add(rcFeilds);
				cls.fieldsMap.put(rcFeilds.Object_Name__c,rcFieldsList);
				// Create Set
				objectList.add(rcFeilds.Object_Name__c);
			} 
        } 
        
        List<String> sortObjectJSON = new List<String>();
        sortObjectJSON.addAll(objectList);
        sortObjectJSON.sort();
		cls.objectJSON = JSON.serialize(sortObjectJSON);
	    cls.objectJSON = cls.objectJSON.replace('\'', '\\\'');
        
		cls.bNameError = false;
        cls.bEvalOrderError = false;
        cls.bError = false;
    }
    
    /**
    * <p>
    * This method is get all the Omni Rule Criteria releated to the Omni Case Assignment Rule
    *
    * @param    cls     Instance of Omni_CaseAssignmentRuleEdit_X_HUM Class
    * @return   Void
    */
    public void getRuleCriterias(Omni_CaseAssignmentRuleEdit_X_HUM cls)
    {
        cls.ruleEntries = new List<Omni_Rule_Criteria__c>();
                        
        if(cls.omniRule.Id != null)
        {
            cls.ruleEntries = dLayer.getCriterias(cls.omniRule.Id);                                                                                                                  
        }
                   
        cls.ruleCriteriaJSON = JSON.serialize(cls.ruleEntries);
        cls.ruleCriteriaJSON = '{"data":' + cls.ruleCriteriaJSON + '}';
        cls.ruleCriteriaJSON = cls.ruleCriteriaJSON.replace('\'', '\\\'');        
    }
    
	/**
    * <p>
    * This method is get all the OmniRuleCriteria_Fields__mdt
    *
    * @param    cls     Instance of Omni_CaseAssignmentRuleEdit_X_HUM Class
    * @return   Void
    */
  
    public void getFieldsList(Omni_CaseAssignmentRuleEdit_X_HUM cls)
    {
    	if(cls.fieldsMap.containsKey(cls.selectedObjectName))
    	 {
    	 	List<String> fieldsList = new List<String>();
    	 	List<OmniRuleCriteria_Fields__mdt> lstFieldObj = cls.fieldsMap.get(cls.selectedObjectName);        
	        for(OmniRuleCriteria_Fields__mdt fieldObj : lstFieldObj)
	        {                           
	           fieldsList.add(fieldObj.MasterLabel);              
	        }
	        
	        cls.fieldListJSON = JSON.serialize(lstFieldObj);
	        cls.fieldListJSON = cls.fieldListJSON.replace('\'', '\\\'');
   	 }
     	
    }
    
    
    /**
    * <p>
    * This method is get all the Omni Rule Criteria releated to the Omni Case Assignment Rule
    *
    * @param    cls     Instance of Omni_CaseAssignmentRuleEdit_X_HUM Class
    * @return   void
    */
    public void saveRecs(Omni_CaseAssignmentRuleEdit_X_HUM cls)
    {       
        try
		{
			cls.bError = false;
			Boolean bDMLError = false;
			Boolean bErrorFlag = false;
			if(cls.bNameError)
	        {
	        	ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, Label.NAMEERROR_OMNICASEASSIGNMENTRULE_HUM));
            	bErrorFlag = true;
            	cls.bNameError = false;
              
        	}        
	        if(cls.bEvalOrderError)
	        {
	        	ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, Label.EVAULATIONRODERERROR_OMNICASEASSIGNMENTRULE_HUM));
            	bErrorFlag = true;
            	cls.bEvalOrderError = false;
	        }
	        if(cls.bUnsavedRCError)
	        {
	        	ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, Label.UNSAVEDRCERROR_OMNICASEASSIGNMENTRULE_HUM));
            	bErrorFlag = true;
            	cls.bUnsavedRCError = false;
	        }
	        
	        if(!bErrorFlag)
	        {
	        	bErrorFlag = validateRequiredFields(cls.omniRule);
	        }
			
			if(!bErrorFlag)
			{
				List<Omni_Case_Assignment_Rule__c> listAllRules = dLayer.getAllRules();         
				Map<Integer, Omni_Case_Assignment_Rule__c> allRulesMap = new Map<Integer, Omni_Case_Assignment_Rule__c>();          
				Map<Id, Omni_Case_Assignment_Rule__c> allRulesMapById = new Map<Id, Omni_Case_Assignment_Rule__c>();            
				List<Omni_Case_Assignment_Rule__c> lstRulesToUpdt = new List<Omni_Case_Assignment_Rule__c>();           
				Integer maxExistingValue = 0;
				
				if(listAllRules!=null && !listAllRules.isEmpty())
				{
					maxExistingValue = (integer) listAllRules[0].Evaluation_Order__c;
					
					allRulesMapById.putAll(listAllRules);
					
					for(Omni_Case_Assignment_Rule__c rule : listAllRules)
					{
						allRulesMap.put((integer)rule.Evaluation_Order__c, rule);
					}
				}
								
				if(cls.omniRule.Id == null)  //new record creation 
				{
					if(cls.omniRule.Evaluation_Order__c == maxExistingValue + 1)
						{                       
							lstRulesToUpdt.add(cls.OmniRule);
						} 
						
						if(cls.omniRule.Evaluation_Order__c > maxExistingValue + 1)
						{
							cls.omniRule.Evaluation_Order__c = maxExistingValue + 1;                        
							lstRulesToUpdt.add(cls.OmniRule);
							
						}
						if(cls.omniRule.Evaluation_Order__c < maxExistingValue + 1)
						{                                               
							for(Integer i = (Integer)cls.omniRule.Evaluation_Order__c; i<= maxExistingValue; i++)
							{
								Omni_Case_Assignment_Rule__c ruleUpdt = allRulesMap.get(i);
								ruleUpdt.Evaluation_Order__c = i+1;
								lstRulesToUpdt.add(ruleUpdt);
							}
							lstRulesToUpdt.add(cls.OmniRule);                                    
						}                                     
				}
				else
				{
					Integer oldOrderValue = (Integer) allRulesMapById.get(cls.omniRule.Id).Evaluation_Order__c;
						
						if(cls.omniRule.Evaluation_Order__c != oldOrderValue)
						{
							if(cls.omniRule.Evaluation_Order__c < oldOrderValue)
							{
								for(Integer i = (Integer)cls.omniRule.Evaluation_Order__c; i< oldOrderValue; i++)
								{
									Omni_Case_Assignment_Rule__c ruleUpdt = allRulesMap.get(i);
									ruleUpdt.Evaluation_Order__c = i+1;
									lstRulesToUpdt.add(ruleUpdt);
								}
								lstRulesToUpdt.add(cls.OmniRule);                            
							}
						
							if(cls.omniRule.Evaluation_Order__c > oldOrderValue && cls.omniRule.Evaluation_Order__c >= maxExistingValue)
							{
								
								for(Integer i = oldOrderValue+1; i<=maxExistingValue; i++)
								{
									Omni_Case_Assignment_Rule__c ruleUpdt = allRulesMap.get(i);
									ruleUpdt.Evaluation_Order__c = i-1;
									lstRulesToUpdt.add(ruleUpdt);
								}
																													
								cls.omniRule.Evaluation_Order__c = maxExistingValue;                            
								lstRulesToUpdt.add(cls.OmniRule);                            
							}
							
							if(cls.omniRule.Evaluation_Order__c > oldOrderValue && cls.omniRule.Evaluation_Order__c < maxExistingValue)
							{
								
								for(Integer i = oldOrderValue+1; i<=(Integer)cls.omniRule.Evaluation_Order__c; i++)
								{
									Omni_Case_Assignment_Rule__c ruleUpdt = allRulesMap.get(i);
									ruleUpdt.Evaluation_Order__c = i-1;
									lstRulesToUpdt.add(ruleUpdt);
								}
								lstRulesToUpdt.add(cls.OmniRule);                        
							}           
						}
						else
						{
							lstRulesToUpdt.add(cls.OmniRule);
						}   
				}            
				bDMLError = dLayer.upsertsObject(lstRulesToUpdt, omniCaseAssignmentRule);
				if(!bDMLError)
				{
					List<Omni_Rule_Criteria__c> saveEntries = new List<Omni_Rule_Criteria__c>();
					List<Omni_Rule_Criteria__c> deleteEntries = new List<Omni_Rule_Criteria__c>();
					
					saveEntries = (List<Omni_Rule_Criteria__c>) JSON.deserialize(cls.updtCriteriaJSON, List<Omni_Rule_Criteria__c>.class);
					
					Map<Id, Omni_Rule_Criteria__c> criteriaMap = new Map<Id, Omni_Rule_Criteria__c>();
					List<Omni_Rule_Criteria__c> allCriteria = [Select Id, Name, Omni_Case_Assignment_Rule__c from Omni_Rule_Criteria__c where Omni_Case_Assignment_Rule__c =:cls.omniRule.Id];
					criteriaMap.putAll(allCriteria);            
									
					if(saveEntries != null && !saveEntries.isEmpty())
					{
						for(Omni_Rule_Criteria__c criteria : saveEntries)      
						{
							criteria.Omni_Case_Assignment_Rule__c = cls.omniRule.Id;
							if(String.isNotBlank(criteria.Id))
							{
								criteriaMap.remove(criteria.Id);
							} 
						}   
										
						bDMLError = dLayer.upsertsObject(saveEntries, omniRuleCriteria);
					}
					
					if(criteriaMap!= null && !criteriaMap.isEmpty() && !bDMLError)
					{
						deleteEntries = criteriaMap.values();
					
						dlayer.deleteRecords(deleteEntries,'Omni_Rule_Criteria__c');
					}
					
					if(!bDMLError)
		            {
		            	if(saveEntries != null && !saveEntries.isEmpty())
			            {			            	
			            	cls.omniRule = getCriteriaEvaluationRule(saveEntries,cls.omniRule);
			            	bDMLError = dLayer.upsertsObject(new List<Omni_Case_Assignment_Rule__c>{cls.omniRule}, omniCaseAssignmentRule);
			            }
			            else if((saveEntries == null || (saveEntries != null && saveEntries.isEmpty())) && String.isNotBlank(cls.omniRule.Case_Assignment_Rule_Criteria__c))
			            {
			            	cls.omniRule.Case_Assignment_Rule_Criteria__c = '';
			            	bDMLError = dLayer.upsertsObject(new List<Omni_Case_Assignment_Rule__c>{cls.omniRule}, omniCaseAssignmentRule);
			            }
			        }	            	          
				}
			}
			if(bErrorFlag || bDMLError)
			{           
				cls.bError = true;
			}			
		}
		catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Omni_CaseAssignmentRuleEdit_S_HUM','saveRecs');            
        }	                      
    }
	
	/**
	* <p>
    * This method is used to redirect to Detail page after save is successfull.
    * @param   cls (Instance of Omni_CaseAssignmentRuleEdit_X_HUM Class)
    * @return  PageReference
    */
    public PageReference saveRedirect(Omni_CaseAssignmentRuleEdit_X_HUM cls)
    {
        if(cls.bError) return null;
        else
        {
            PageReference pageRef = new PageReference('/'+cls.omniRule.Id);
	        pageRef.setRedirect(true);
	        return pageRef; 
        }       
    }
    
    /**
    * <p>
    * This method is to validate the mandatory input fields.
    *
    * @param    omniRule     Instance of Omni_Case_Assignment_Rule__c Object
    * @return   Boolean
    */
    private Boolean validateRequiredFields(Omni_Case_Assignment_Rule__c omniRule)
    {
        Boolean bError = false;
                        
        if(String.isBlank(omniRule.Name))
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, Label.NAMEERROR_OMNICASEASSIGNMENTRULE_HUM));
            bError = true;  
        }
        
        if(omniRule.Evaluation_Order__c == null || omniRule.Evaluation_Order__c <= 0)
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, Label.EVAULATIONRODERERROR_OMNICASEASSIGNMENTRULE_HUM));
            bError = true;
        }
        if(omniRule.Omni_Queue__c ==  null)
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, Label.OQERROR_OMNICASEASSIGNMENTRULE_HUM));
            bError = true;          
        }
        return bError;
    }
    
    /**
    * <p>
    * This method is to get the Picklist Values
    *
    * @param    omniRule     Instance of Omni_Case_Assignment_Rule__c Object
    * @return   Boolean
    */
    public static String getPicklistValues(String fieldName)
    {
    	String pickValJSON;
		List<String> picklistValues = new List<String>();
    	if(String.isNotBlank(fieldName))
    	{        		
    		OmniRuleCriteria_Fields__mdt fieldDetails = Omni_CaseAssignmentRuleEdit_D_HUM.getFieldDetails(fieldName);
    		String fieldAPI = fieldDetails.Field_APIName__c;
    		
    		if(fieldAPI.contains('__r.'))
    		{
            	String[] lstAPIName = fieldAPI.split('\\.');
                fieldAPI = lstAPIName[lstAPIName.size()-1];
                
    		}
    		    		
    		List<String> sObjectNames = new List<String>();
    		sObjectNames.add(fieldDetails.Object_API_Name__c);    		    	
    		Schema.DescribeSobjectResult[] dsor = Schema.describeSObjects(sObjectNames);
    		Map<String, Schema.SObjectField> sObjectFieldsMap = dsor[0].fields.getMap();    			
    		 	   		        
    		Schema.describeFieldResult dfr = sObjectFieldsMap.get(fieldAPI).getDescribe();    		        			           
	        for (Schema.PicklistEntry pickVal : dfr.getPicklistValues())
	        {
				picklistValues.add((String)pickVal.getValue());					
	        }	        
    	}
    	pickValJSON = JSON.serialize(picklistValues);
    	pickValJSON = pickValJSON.replace('\'', '\\\'');    	
    	return pickValJSON;
    }
    
    /**
    * <p>
    * This method is to display the Error Message if required fields are missing in rule criteria.
    *
    * @param    N/A
    * @return   PageReference   
    */
    public PageReference showErrorMessage()
    {
    	ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, Label.CRITERIAERR_OMNICASEASSIGNMENTRULE_HUM));
    	return null;   
    }
	
	/**
    * <p>
    * This method is to create a summarized string of all the Rule Criteria.
    *
    * @param    List<Omni_Rule_Criteria__c>,Omni_Case_Assignment_Rule__c
    * @return   Omni_Case_Assignment_Rule__c
    */
    @testVisible
    private Omni_Case_Assignment_Rule__c getCriteriaEvaluationRule(List<Omni_Rule_Criteria__c> lstCriteria,Omni_Case_Assignment_Rule__c omniRule)
    {
    	String CriteriaRule = '';
    	String CriteriaRuleMap = '';
    	Map<String, List<Omni_Rule_Criteria__c>> setValueMap = new Map<String, List<Omni_Rule_Criteria__c>>();
    	
		if(lstCriteria != null && !lstCriteria.isEmpty())
		{
			for(Omni_Rule_Criteria__c rCriteria: lstCriteria)
			{
				if(rCriteria.Criteria_Set__c != null && rCriteria.Criteria_Set__c.split(',').size() > 0)
				{
					for(String setVal : rCriteria.Criteria_Set__c.split(','))
					{
						List<Omni_Rule_Criteria__c> tempRCNames = new List<Omni_Rule_Criteria__c>();
						if(setValueMap.containskey(setVal))
						{
							tempRCNames = setValueMap.get(setVal);
						}
						tempRCNames.add(rCriteria);
						setValueMap.put(setVal,tempRCNames);
					}
				}
			
			}
			
			for(String index :setValueMap.keyset())
			{
				if(String.isNotBlank(CriteriaRule) && String.isNotBlank(CriteriaRuleMap))
				{
					CriteriaRule = CriteriaRule+' OR ';
					CriteriaRuleMap = CriteriaRuleMap+' OR ';
				} 
				CriteriaRule = CriteriaRule+' (';
				List<Omni_Rule_Criteria__c> eRCNameListTemp = setValueMap.get(String.valueOf(index));
				Integer eRCNameIndex = 1; 
				for(Omni_Rule_Criteria__c eRCName: eRCNameListTemp)
				{
					CriteriaRule = CriteriaRule+eRCName.Criteria_Field__c + ' '+ eRCName.Criteria_Operator__c+' '+eRCName.Criteria_Field_Value__c;
					CriteriaRuleMap += eRCName.Id;
					if(eRCNameIndex < eRCNameListTemp.size())
					{
						CriteriaRule = CriteriaRule + ' AND ';
						CriteriaRuleMap = CriteriaRuleMap + ',';
						eRCNameIndex++;
					}
				}
				CriteriaRule = CriteriaRule+')';
			}
		}
    	
    	
    	omniRule.Case_Assignment_Rule_Criteria__c = CriteriaRule;
    	omniRule.Case_Assignment_Rule_Map__c = CriteriaRuleMap;
    	
    	return omniRule;
    	
    }  
       
}