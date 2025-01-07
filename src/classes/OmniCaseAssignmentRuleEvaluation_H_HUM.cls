/*******************************************************************************************************************************
Apex Class Name : OmniCaseAssignmentRuleEvaluation_H_HUM
Version         : 1.0
Created On      : 12/08/2017
Function        : 1. When a Case is created or updated, if Omni_Eligible__c flag is true on the case, 
                The omni case assignment rules evaluation should happen
                2. Only the rules where the Work Queue and Queue View are matching with the case 
                should be considered for evaluation
                3. The staging queue corresponding to the first matching rule(based on 
                evaluation_order__c field) should be populated as OwnerId on the Case

Test Class      : OmniCaseAssignmentRuleEvaluation_T_HUM  

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Manoj Chaurasia             167654                     12/08/2017                  Original Version
* Manoj Chaurasia             172758                     01/22/2018                  Changed code  - fix for Null pointer exception
* Mohammed Noor               175528                     01/28/2018                  Fix for Defect# 340000
* Manoj Chaurasia             176091                     01/29/2018                  Fix for: Omni case assignment rule evaluation should not be executed if agent does not accept the case in omni widget 
* Mohammed Noor               179224                     02/05/2018                  Add Bulkified Logic for the Relationship field evaluation.
* Mohammed Noor               180627                     02/07/2018                  Fix for Defect# 341704.
* Manoj Chaurasia             180627                     02/08/2018                  Fix for Defect# 341704.
* Manoj Chaurasia             210171                     04/03/2018                  REQ-357261: Addition of “In” and "Not In" within the “Operator” pick list 
* Mohammed Noor               213637                     04/10/2018                  REQ - 359240 - Date fields available as Age criteria in Omni Channel
* Bharath Rajappan                                       06/07/2018                  REQ - 363649 - Handle String date field (Coverage Effective Date) Criteria
* Syed Mubarak T										03/19/2019			  REQ - 386810 - Omni Configuration Item changes
* Syed Mubarak T										04/01/2019			  REQ - 383800 - Add TRR Template Fields to Omni Evaluation
* Mohammed Noor                                         08/14/2019            Fix the TRR Template Evaluation logic for all Templates.       
* Mohammed Noor                                         09/05/2019            REQ - 408201 - Omni Case Assignment Rule Evaluation Optimization.
* Mohammed Noor                                         10/15/2019            Fix TRR Template Query Issue in Before Insert Trigger Context.
* Prasuna Pattabhi                                      01/04/2022            US 2990852 Checkmarx issue fix.
******************************************************************************************************************************/
public class OmniCaseAssignmentRuleEvaluation_H_HUM {
    Static final String FIELD_STRING ='string';
    Static final String FIELD_PICKLIST ='picklist';
    Static final String FIELD_BOOLEAN ='boolean';
    Static final String FIELD_DATE ='date';
    Static final String FIELD_DATETIME ='datetime';
    Static final String FIELD_DOUBLE ='double';
    Static final String FIELD_TODAY ='today';
    Static Double caseAge = 0.0;
    Static Map<String, SObject> mapRelatedObjects = new Map<String, SObject>();
            
    /*
    * This is the Main Method Used to evaluate omni case assignment rule
    * <p>
    * @ Param     :    List<Case>
    * @ Param     :    Boolean
    * @ Return    :    Void
    */     
    public static void EvaluateOmniCaseAssignmentRule(List<Case> lstCaseRecords, Boolean bBatchProcess)
    {   
        Boolean updateCase = true;
        String ERROR_APPLICATION= '';
        Group oGroup;
        List<Omni_Case_Assignment_Rule__c> lstCaseAssignmentRule;
        List<Omni_Rule_Criteria__c> lstRuleCriteria;
        List<OmniRuleCriteria_Fields__mdt> lstFieldsType;
        Map<String, OmniRuleCriteria_Fields__mdt> mapFieldType;
        Set<String> setOmniQueue;
        Map<String, Map<String,Omni_Eligible_Queue_Views__mdt>> mapWorkQueue = getOmniEligibleMetadata();
		map<string, set<string>> mapTSubByCases = new map<string, set<string>>(); 
        map<String, map<string, Template_Submission_Data__c>> mapTSubDataBySubmission = new map<String, map<string, Template_Submission_Data__c>>();
        
        try
        { 
            setOmniQueue = new Set<String>();
            List<Error_Log__c> lstErrorLog = new List<Error_Log__c>();
            ERROR_APPLICATION = HUMConstants__c.getInstance('ERROR_APPLICATION') != null ? HUMConstants__c.getInstance('ERROR_APPLICATION').StringValue__c : '';
            
            
            lstFieldsType = [Select Label, Field_APIName__c, Field_Type__c, ParentObject_APIName__c,Object_API_Name__c From OmniRuleCriteria_Fields__mdt];
            mapFieldType = new Map<String, OmniRuleCriteria_Fields__mdt>();
		   
		   // moved this to evaluate the template based fields
		   List<Omni_Case_Assignment_Rule__c> lstOmniCaseAssignmentRule = getCaseAssignmentRule(lstCaseRecords);
		   
			if(lstOmniCaseAssignmentRule!=null && !lstOmniCaseAssignmentRule.isEmpty())
			{
			   //get all the rule criteria for assignment rules
				List<Omni_Rule_Criteria__c> lstCaseRuleCriteria = new List<Omni_Rule_Criteria__c>();
				for(Omni_Case_Assignment_Rule__c assignmentRule: lstOmniCaseAssignmentRule)
				{
					lstCaseRuleCriteria.addAll(assignmentRule.Omni_Rule_Criteria_Assign_Rule__r);
				}
				 
				 // put all the mdt in Map
				 for(OmniRuleCriteria_Fields__mdt oFieldType: lstFieldsType)
				 {
					mapFieldType.put(oFieldType.Label, oFieldType);
				}
				 
				// check for template fields from the map using rule criteria fields
				Set<String> lstTemplateFieldNames = new Set<String>();
				for(Omni_Rule_Criteria__c rc:lstCaseRuleCriteria)
				{
					if(mapFieldType.containsKey(rc.Criteria_Field__c) && mapFieldType.get(rc.Criteria_Field__c).Object_API_Name__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TEMPLATE_SUBMISSON_DATA_OBJECT))
					{
						lstTemplateFieldNames.add(rc.Criteria_Field__c);
					}
				}
				list<Template_Submission__c> lstTemplateSub = new list<Template_Submission__c>(); 
				if(lstTemplateFieldNames != null && !lstTemplateFieldNames.isEmpty())
				{
					Set<String> setCaseIDs = new Set<String>();
				   for(Case c:lstCaseRecords)
				   {
						if(String.isNotBlank(c.ID) && String.isNotBlank(c.Origin) && GLOBAL_CONSTANT_HUM.TRR_ORIGIN.equals(c.Origin)
						&& String.isNotBlank(c.Type) && GLOBAL_CONSTANT_HUM.TRR_CASE_TYPE.equals(c.Type)
						&& String.isNotBlank(c.subtype__c) && GLOBAL_CONSTANT_HUM.TRR_CASE_SUBTYPE.equals(c.subtype__c))
						{
							setCaseIDs.add(c.Id);
						}
				   }
					if(setCaseIDs!=null && !setCaseIDs.isEmpty())
					{
						lstTemplateSub.addAll(getTemplateDataByCases(setCaseIDs,lstTemplateFieldNames));
					
						if(lstTemplateSub!=null && !lstTemplateSub.isEmpty())
						{
							for(Template_Submission__c tSubmission : lstTemplateSub)
							{
								if(tSubmission.Template_Submission_Data__r != null)
								{	            
									if(mapTSubByCases!=null && mapTSubByCases.containsKey(tSubmission.Submission_Owner__r.Object_Owner_ID__c))	            
										mapTSubByCases.get(tSubmission.Submission_Owner__r.Object_Owner_ID__c).add(tSubmission.Id);
									else
										mapTSubByCases.put(tSubmission.Submission_Owner__r.Object_Owner_ID__c, new set<string>{tSubmission.Id});
									for(Template_Submission_Data__c tSubData : tSubmission.Template_Submission_Data__r)
									{	
										if(mapTSubDataBySubmission!=null && mapTSubDataBySubmission.containsKey(tSubmission.Id))
											mapTSubDataBySubmission.get(tSubmission.Id).put(tSubData.Name__c, tSubData);	
										else
										{
											map<string, Template_Submission_Data__c> mapTSubData = new map<string, Template_Submission_Data__c>();
											mapTSubData.put(tSubData.Name__c, tSubData);
											mapTSubDataBySubmission.put(tSubmission.Id, mapTSubData);
										}
									}
								}           
							}			        
						}
					}
				}
			}
            if(lstCaseRecords.size() == 1 && !bBatchProcess){
                mapRelatedObjects = new Map<String, SObject>();
                if(lstCaseRecords[0].Omni_Eligible__c){
                    try{                        
                        calculateCaseAge(lstCaseRecords[0]);
                        
                        lstCaseAssignmentRule = getAssignmentRulesForCase(lstOmniCaseAssignmentRule, lstCaseRecords[0].Owner_Queue__c, lstCaseRecords[0].Work_Queue_View_Name__c);
                        
                        if(lstCaseAssignmentRule != null && !lstCaseAssignmentRule.isEmpty()){
                            for(Omni_Case_Assignment_Rule__c oCaseAssignmentRule: lstCaseAssignmentRule){
                                updateCase = true;
                                lstRuleCriteria = oCaseAssignmentRule.Omni_Rule_Criteria_Assign_Rule__r;
                                if(lstRuleCriteria != null && !lstRuleCriteria.isEmpty()){
                                    updateCase = validateRuleCriteria(lstCaseRecords[0], lstRuleCriteria, mapFieldType, false,oCaseAssignmentRule.Case_Assignment_Rule_Map__c, mapTSubByCases, mapTSubDataBySubmission); 
                                }
                                if(updateCase){
                                    lstCaseRecords[0].OwnerId = oCaseAssignmentRule.Omni_Queue__r.Staging_Queue_ID__c; 
                                    break;
                                } 
                            }
                        }
                        else{
                            updateCase = false ;
                        }
                        if(updateCase == false) {
                            if(mapWorkQueue.containsKey(lstCaseRecords[0].Owner_Queue__c)){
                                Map<String,Omni_Eligible_Queue_Views__mdt> mapWorkQView = mapWorkQueue.get(lstCaseRecords[0].Owner_Queue__c);
                                if(mapWorkQView.containsKey(lstCaseRecords[0].Work_Queue_View_Name__c))
                                {
                                	Omni_Eligible_Queue_Views__mdt qViewMdt = mapWorkQView.get(lstCaseRecords[0].Work_Queue_View_Name__c);
                                	if(!String.isBlank(qViewMdt.Configuration_Item_Name__c))
                                        {
                                            Omni_Configuration_Item__c ocItem = Omni_Configuration_Item__c.getValues(qViewMdt.Configuration_Item_Name__c);
                                            lstCaseRecords[0].OwnerId = ocItem.Default_Queue_Id__c;
                                        }
                                }
                                
                            }
                        } 
                    }
                   Catch(Exception error){
                    HUMExceptionHelper.bShowErrorOnPage = false;
                       HUMExceptionHelper.logErrors(error,'OmniCaseAssignmentRuleEvaluation_H_HUM','EvaluateOmniCaseAssignmentRule'); 
                  } 
                }
            }
            else{
                mapRelatedObjects = new Map<String, Sobject>();
                mapRelatedObjects = getRelationshipRecordsBulk(lstCaseRecords);
                List<Case> lstCasesToUpdate = new List<Case>();
                String sOldOwnerID = '';                
                Boolean bEvaluateRule = true;
                for(Case oCase:lstCaseRecords){
                   try{                        
                        if(String.isNotBlank(oCase.ownerID))
                        {
                            sOldOwnerID = oCase.ownerID;
                        }
                        updateCase = true;                        
                        if(oCase.Omni_Eligible__c){
                            if(!bBatchProcess  && oCase.Id != null && setOmniQueue.contains(oCase.OwnerId)){
                                bEvaluateRule = false;
                            } 
                            if(bEvaluateRule ){                           
                                calculateCaseAge(oCase);
                                lstCaseAssignmentRule = getAssignmentRulesForCase(lstOmniCaseAssignmentRule, oCase.Owner_Queue__c, oCase.Work_Queue_View_Name__c);
                                
                                if(lstCaseAssignmentRule != null && !lstCaseAssignmentRule.isEmpty()){
                                    for(Omni_Case_Assignment_Rule__c oCaseAssignmentRule: lstCaseAssignmentRule){
                                        updateCase = true;
                                        lstRuleCriteria = oCaseAssignmentRule.Omni_Rule_Criteria_Assign_Rule__r;
                                        if(lstRuleCriteria != null && !lstRuleCriteria.isEmpty()){
                                            updateCase = validateRuleCriteria(oCase, lstRuleCriteria, mapFieldType, true,oCaseAssignmentRule.Case_Assignment_Rule_Map__c, mapTSubByCases, mapTSubDataBySubmission); 
                                        }
                                        if(updateCase){
                                            if(bBatchProcess){
                                                if(oCase.OwnerId == oCaseAssignmentRule.Omni_Queue__r.Omni_Queue_ID__c){
                                                    break;
                                                }
                                                else{
                                                    oCase.OwnerId = oCaseAssignmentRule.Omni_Queue__r.Staging_Queue_ID__c;
                                                    break;
                                                }
                                            }
                                            else{
                                                oCase.OwnerId = oCaseAssignmentRule.Omni_Queue__r.Staging_Queue_ID__c;
                                                break;
                                            }
                                        } 
                                    } 
                                }
                                else{
                                    updateCase = false;
                                }
                                
                                if(updateCase == false) {
                                   if(mapWorkQueue.containsKey(oCase.Owner_Queue__c)){
		                                Map<String,Omni_Eligible_Queue_Views__mdt> mapWorkQView = mapWorkQueue.get(oCase.Owner_Queue__c);
		                                if(mapWorkQView.containsKey(oCase.Work_Queue_View_Name__c))
		                                {
		                                	Omni_Eligible_Queue_Views__mdt qViewMdt = mapWorkQView.get(oCase.Work_Queue_View_Name__c);
		                                	if(!String.isBlank(qViewMdt.Configuration_Item_Name__c))
		                                        {
		                                            Omni_Configuration_Item__c ocItem = Omni_Configuration_Item__c.getValues(qViewMdt.Configuration_Item_Name__c);
		                                            oCase.OwnerId = ocItem.Default_Queue_Id__c;
		                                        }
		                                }
		                                
		                            }
                                } 
                            }
                        }
                        if(bBatchProcess && String.isNotBlank(sOldOwnerID) && sOldOwnerID != oCase.OwnerID)
                        {
                            lstCasesToUpdate.add(oCase);
                        }                           
                        sOldOwnerID = '';
                  }
                    Catch(Exception error){
                        //Bulkified error logs
                        lstErrorLog.add(new Error_Log__c(Error_Message__c = error.getMessage(), class_name__c = 'OmniCaseAssignmentRuleEvaluation_H_HUM', Method_Name__c = 'EvaluateOmniCaseAssignmentRule', Object_Type__c = 'Case', Record_ID__c = lstCaseRecords[0].Id, Type__c = ERROR_APPLICATION));
                   }                    
                }
                if(lstErrorLog != null && !lstErrorLog.isEmpty())
                {   
                    insert lstErrorLog;
                }
                if(bBatchProcess && lstCasesToUpdate != null &&!lstCasesToUpdate.isEmpty())
                {
                    List<Database.UpsertResult> lstUpsertResults = Database.upsert(lstCasesToUpdate, false);
                    HUMExceptionHelper.processUpsertResults(lstUpsertResults, lstCaseRecords, 'OmniCaseAssignmentRuleEvaluation_H_HUM', 'EvaluateOmniCaseAssignmentRule', 'Case');
                } 
            }
        }
        catch(Exception e)
       {
           if (Schema.sObjectType.Error_Log__c.fields.Error_Message__c.isCreateable() && Schema.sobjectType.Error_Log__c.fields.class_name__c.isCreateable() && Schema.sObjectType.Error_Log__c.fields.Method_Name__c.isCreateable() && Schema.sobjectType.Error_Log__c.fields.Object_Type__c.isCreateable() && Schema.sobjectType.Error_Log__c.fields.Type__c.isCreateable()){
             Error_Log__c oErrorLog = new Error_Log__c(Error_Message__c = e.getMessage(), class_name__c = 'OmniCaseAssignmentRuleEvaluation_H_HUM', Method_Name__c = 'EvaluateOmniCaseAssignmentRule', Object_Type__c = 'Case', Type__c = ERROR_APPLICATION);
             insert oErrorLog;
           }
        }   
    }
    
    /*
    * This Method is used to calculate Case Age 
    * <p>
    * @ Param     :    Case
    * @ Return    :    void
    */
    @TestVisible
    private static void calculateCaseAge(Case oCase){
        if(oCase.Case_Age__c!= Null){
            caseAge = oCase.Case_Age__c;
        }
        else{
            if(oCase.createdDate != null){
                DateTime dtCurrent = DateTime.Now();
                DateTime dtCreated = oCase.createdDate;
                caseAge = dtCreated.Date().daysBetween(dtCurrent.Date());
            }
            else{
                caseAge  = 0.0;
            }
        }
    }
        /*
    * This Method is used to validate Age Fields and return the result.
    * <p>
    * @ Param     :    Date field from Case, Age value from the rule and the operator.
    * @ Return    :    Boolean
    */
    @TestVisible
    private static Boolean validateAgeFields(Date fieldValue, Integer ruleValue, String operation)
    {            
        Boolean bResult = false;
        DateTime dtCurrent = DateTime.Now();                    
        operation = operation.remove(' ');        
        Integer caseFieldAge;
        
        if(operation.equalsIgnoreCase('greaterorequalfromToday'))
        {                        
            caseFieldAge = calcualteAge(dtCurrent.Date(), fieldValue);            
            if(caseFieldAge >= ruleValue) bResult = true;            
        }
        else if(operation.equalsIgnoreCase('lessorequalfromToday'))
        {                       
            caseFieldAge = calcualteAge(dtCurrent.Date(), fieldValue);            
            if(caseFieldAge <= ruleValue) bResult = true;            
        }
        else if(operation.equalsIgnoreCase('greaterorequaluptoToday'))
        {                                   
            caseFieldAge = calcualteAge(fieldValue, dtCurrent.Date());            
            if(caseFieldAge >= ruleValue) bResult = true;            
        }
        else if(operation.equalsIgnoreCase('lessorequaluptoToday'))
        {
            caseFieldAge = calcualteAge(fieldValue, dtCurrent.Date());            
            if(caseFieldAge <= ruleValue) bResult = true;            
        }        
        return bResult;     
    }
    
    /*
    * This Method is used to calcualteAge between two dates.
    * <p>
    * @ Param     :    Start date and end date values.
    * @ Return    :    Integer age value.
    */
    @TestVisible
    private static Integer calcualteAge(Date startDate, Date endDate)
    {
        return startDate.daysBetween(endDate); 
    }

    /*
    * This Method is used validate Rule Criterias
    * <p>
    * @ Param     :    Case
    * @ Param     :    List<Omni_Rule_Criteria__c> 
    * @ Param     :    Map<String, OmniRuleCriteria_Fields__mdt> mapFieldType
    * @ Return    :    Boolean
    */
    @TestVisible
    private static Boolean validateRuleCriteria(Case oCase, List<Omni_Rule_Criteria__c> lstRuleCriteria,
                                                Map<String, OmniRuleCriteria_Fields__mdt> mapFieldType, Boolean bIsBulk, String ruleMapString, 
                                                map<string, set<string>> mapTSubByCases, map<string, map<string, Template_Submission_Data__c>> mapTSubDataBySubmission){
        Boolean bReturn = true;
		boolean bValidateRuleCriteria = true;
        String sCriteriaFieldValue = '';
        String[] dateParts;
        Date dtToday;
        
        //Instance of template Submission map
		Map<String,List<Template_Submission_Data__c>> templateSubmissionMap = new Map<String,List<Template_Submission_Data__c>>();
		Set<String> templateSubmissionSet = new Set<String>();
		
		// get the map using Case Id
		if(mapTSubByCases != null && !mapTSubByCases.isEmpty() && mapTSubByCases.containsKey(String.valueof(oCase.Id)))
		{			
			templateSubmissionSet = mapTSubByCases.get(String.valueof(oCase.Id));
		}
                                                    
        Map<String,Omni_Rule_Criteria__c> mapCriteria = new Map<String,Omni_Rule_Criteria__c>();
		for (Omni_Rule_Criteria__c rcList :lstRuleCriteria)
		{
			mapCriteria.put(rcList.Id,rcList);
		}
		List<String> andCombination = ruleMapString.split('OR');
		if(andCombination != null && !andCombination.isEmpty())
		{
			Boolean orFlag = true;
			for(String andRules : andCombination)
			{
				if(orFlag)
				{
					Boolean andFlag= true;
					for(String ruleCriteriaId : andRules.split(','))
					{
						bValidateRuleCriteria = true;
						if(andFlag)
						{
							Omni_Rule_Criteria__c oRule = mapCriteria.get(ruleCriteriaId.trim());
							// bReturn = evaluate rule criteria
							
							/* This is evaluation rule */
							
							OmniRuleCriteria_Fields__mdt oRuleCriteriaFields = mapFieldType.get(oRule.Criteria_Field__c);
							String sFieldType = oRuleCriteriaFields.Field_Type__c;
							String sAPIName = oRuleCriteriaFields.Field_APIName__c;
							
							String fieldValue ='';
							String CriteriaFieldValue = oRule.Criteria_Field_Value__c;                           
							String operation = oRule.Criteria_Operator__c;
							
							if(sAPIName.equalsIgnoreCase('Case_Age__c'))
							{
								fieldValue = String.valueOf(caseAge);
							}
							else if(sAPIName.contains('__r.')){
								String[] lstAPIName = sAPIName.split('\\.');
								String relatedFieldAPI = lstAPIName[0].replace('__r', '__c');
								String relatedObjectAPI = mapFieldType.get(oRule.Criteria_Field__c).ParentObject_APIName__c;                
								String relatedObjField = (mapFieldType.get(oRule.Criteria_Field__c).Field_APIName__c).substringAfter('__r.');
								String relatedObjId = String.valueOf(oCase.get(relatedFieldAPI));
								
								if(String.isNotBlank(relatedObjId) && !bIsBulk)
								{
									fieldValue = getRelatedFieldValue(relatedObjectAPI,relatedObjId, relatedObjField);
								}
								if(String.isNotBlank(relatedObjId) && bIsBulk)
								{
									if(mapRelatedObjects!= null && mapRelatedObjects.containsKey(relatedObjId))
									{
										if(relatedObjField.contains('__r.'))                           
											fieldValue = getFieldValue(mapRelatedObjects.get(relatedObjId), relatedObjField);
										else                                        
											fieldValue = String.valueOf(mapRelatedObjects.get(relatedObjId).get(relatedObjField));
									}                
								}               
							}
							else if(oRuleCriteriaFields.Object_API_Name__c.equals(GLOBAL_CONSTANT_HUM.TEMPLATE_SUBMISSON_DATA_OBJECT))
							{
								boolean bTemplateValRes = false;  
								if(templateSubmissionSet!=null && !templateSubmissionSet.isEmpty())
								{
									for(String templateSubmissionName: templateSubmissionSet)
									{
										if(mapTSubDataBySubmission!=null && mapTSubDataBySubmission.containsKey(templateSubmissionName) && mapTSubDataBySubmission.get(templateSubmissionName)!=null && mapTSubDataBySubmission.get(templateSubmissionName).containsKey(sAPIName))
										{
											fieldValue = mapTSubDataBySubmission.get(templateSubmissionName).get(sAPIName).value__c;											
											if(String.isNotBlank(fieldValue))
											{
												bTemplateValRes = validateRuleCriteriaByDataType(sFieldType, CriteriaFieldValue, fieldValue, Operation);
												if(bTemplateValRes)	break;	
											}
										}																			
									}
								}
								bReturn = bTemplateValRes;
								bValidateRuleCriteria = false;
							}
							else{
								fieldValue = String.valueOf(oCase.get(sAPIName));
							} 
							
							if(String.isBlank(fieldValue)){
								bReturn = false;
								bValidateRuleCriteria = false;								
							}
							
							if(bValidateRuleCriteria)  //bValidateRuleCriteria is true by default at entry. Call validation if bReturn not set as false by Template logic or blank check
							{
								bReturn = validateRuleCriteriaByDataType(sFieldType, CriteriaFieldValue, fieldValue, Operation);				
							}	
							
							// End of evaluation rcList
							
							if(bReturn)		//verify the result back from validation
							{
								orFlag = false;
								
							}
							else if(!bReturn)
							{
								andFlag = false;
								orFlag = true;
								
							}
							
						}
						else if(!andFlag)
						{
							break;
						}
						
					}
				}
				else if(!orFlag)
				{
					break;
				}
			}
			
		}
        
        return bReturn;
    }

    /*
    * This Method is used to get the value of the related Field of the Case record.
    * <p>
    * @ Param     :    Related Object API Name, Related Object recordID, Related Field Name.
    * @ Return    :    String (value of the related Field)
    */
    @TestVisible
    private Static String getRelatedFieldValue(String relatedObjectAPI,String relatedObjId, String relatedObjField)
    {
        String sRelatedFieldValue = '';
        SObject relatedObject;
        if(mapRelatedObjects.containsKey(relatedObjectAPI) && mapRelatedObjects.get(relatedObjectAPI)!= null)
        {           
            relatedObject = mapRelatedObjects.get(relatedObjectAPI);
            if(relatedObjField.contains('__r.'))                              
                sRelatedFieldValue = getFieldValue(relatedObject, relatedObjField);                         
            else
                sRelatedFieldValue = String.valueOf(relatedObject.get(relatedObjField));                                     
        }
        else
        {           
            relatedObject = getRelatedObject(relatedObjectAPI, relatedObjId);
            
            if(relatedObjField.contains('__r.'))                           
                sRelatedFieldValue = getFieldValue(relatedObject, relatedObjField);
            else            
                sRelatedFieldValue = String.valueOf(relatedObject.get(relatedObjField));
            
            mapRelatedObjects.put(relatedObjectAPI, relatedObject);     
        }   
        return sRelatedFieldValue;  
    }
    
    /*
    * This Method is used to get the value of the Multilevel Related Object. 
    * <p>
    * @ Param     :    Related SObject and Related Field API Name
    * @ Return    :    String.
    */
    @TestVisible
    private static String getFieldValue(SObject relatedObject, String relatedObjField)
    {                       
        String sfieldValue = '';                            
        SObject oSObject = relatedObject;
        String[] rLstAPIName = relatedObjField.split('\\.');                
        for(Integer index=0; index< rLstAPIName.size(); index++)
        {
            if(rLstAPIName[index].contains('__r'))          
                oSObject = (SObject)oSObject.getSObject(rLstAPIName[index]);                                            
            else            
                sfieldValue = String.valueOf(oSObject.get(rLstAPIName[index]));                                           
            
        }
        return sfieldValue;
    }        
    
    /*
    * This Method is used to get the Related Object instance through dynamic query. 
    * <p>
    * @ Param     :    Related Object API Name, Related Object recordID.
    * @ Return    :    SObject (Instance of the related Object)
    */
    @TestVisible
    private static SObject getRelatedObject(String relatedObjectAPI, String relatedObjId)
    {
        SObject relatedObj;
        List<OmniRuleCriteria_Fields__mdt> relatedFieldsMDT = [SELECT Label, Field_APIName__c, Field_Type__c, ParentObject_APIName__c FROM OmniRuleCriteria_Fields__mdt WHERE ParentObject_APIName__c = :relatedObjectAPI];
        if(relatedFieldsMDT!= null && !relatedFieldsMDT.isEmpty())
        {
            String sQuery = 'SELECT Id';
            for(OmniRuleCriteria_Fields__mdt rField: relatedFieldsMDT)
            {
                sQuery = sQuery + ', ' + (rField.Field_APIName__c).substringAfter('__r.');              
            }           
            sQuery += ' FROM ' + relatedObjectAPI + ' WHERE Id = \'' + relatedObjId + '\'';         
            relatedObj = Database.query(sQuery);            
        }
        return relatedObj;
    }
    
    /*
    * This Method is used to get list of Omni case assignment rule for a particular case
    * <p>
    * @ Param     :    Case
    * @ Return    :    List<Omni_Case_Assignment_Rule__c>
    */
    @TestVisible
    private static List<Omni_Case_Assignment_Rule__c> getAssignmentRulesForCase(List<Omni_Case_Assignment_Rule__c> pRules, String pOwnerQueue,
                                                                                String pQueueView){
        List<Omni_Case_Assignment_Rule__c> lstRule = new List<Omni_Case_Assignment_Rule__c>();
        for(Omni_Case_Assignment_Rule__c oRule: pRules){            
            if(String.isNotBlank(oRule.Work_Queue__c) && String.isNotBlank(oRule.Work_Queue_View__c) && pOwnerQueue.equalsIgnoreCase(oRule.Work_Queue__c) && pQueueView.equalsIgnoreCase(oRule.Work_Queue_View__c)) 
            {                
				lstRule.add(oRule);
            }   
            else if(String.isBlank(oRule.Work_Queue_View__c))
            {
            	if(String.isNotBlank(oRule.Work_Queue__c) && pOwnerQueue.equalsIgnoreCase(oRule.Work_Queue__c))
            		lstRule.add(oRule);
            	else if(String.isBlank(oRule.Work_Queue__c))
            		lstRule.add(oRule);
			}                                   
        }
        return lstRule;
    }
    
    /*
    * This Method is used to get list of Omni case assignment rule for a particular case
    * <p>
    * @ Param     :    Case
    * @ Return    :    List<Omni_Case_Assignment_Rule__c>
    */
    @TestVisible
    private static List<Omni_Case_Assignment_Rule__c> getCaseAssignmentRule(Case pCase){
        List<Omni_Case_Assignment_Rule__c> lstCaseAssignRule;        
        lstCaseAssignRule= [SELECT Id, Evaluation_Order__c  , Omni_Queue__r.Staging_Queue_ID__c, Omni_Queue__r.Omni_Queue_ID__c,Case_Assignment_Rule_Map__c, Work_Queue__c, Work_Queue_View__c, 
                            (SELECT Id, Criteria_Operator__c,
                             Criteria_Field_Value__c, Criteria_Field__c  FROM Omni_Rule_Criteria_Assign_Rule__r)
                            FROM Omni_Case_Assignment_Rule__c
                            WHERE Is_Active__c = true 
                            and ((WorkQueue__r.Name =: pCase.Owner_Queue__c 
                                  and WorkQueueView__r.Name =: pCase.Work_Queue_View_Name__c) OR
                                 ((WorkQueue__r.Name = null or WorkQueue__r.Name = '') and (WorkQueueView__r.Name = null or WorkQueueView__r.Name = '')) OR 
                                 (WorkQueue__r.Name =: pCase.Owner_Queue__c and (WorkQueueView__r.Name = null or WorkQueueView__r.Name = '')))
                            Order By Evaluation_Order__c]; 
        
        return lstCaseAssignRule;
    }
    
    /*
    * This Method is used to get list of Omni case assignment rules
    * <p>
    * @ Param     :    List<Case>
    * @ Return    :    List<Omni_Case_Assignment_Rule__c>
    */
    @TestVisible
    private static List<Omni_Case_Assignment_Rule__c> getCaseAssignmentRule(List<Case> lstCases){
        List<Omni_Case_Assignment_Rule__c> lstCaseAssignRule;
        List<String> lstWorkQueue = new List<string>();
        for(Case oCase: lstCases){
            if(!String.isBlank(oCase.Owner_Queue__c)) 
                lstWorkQueue.add(oCase.Owner_Queue__c);
        }
        
        lstCaseAssignRule= [SELECT Id, Evaluation_Order__c  , Omni_Queue__r.Staging_Queue_ID__c, Omni_Queue__r.Omni_Queue_ID__c,  Work_Queue__c, Work_Queue_View__c,
                            WorkQueue__r.Name, WorkQueueView__r.Name, WorkQueue__c, Case_Assignment_Rule_Map__c, 
                            (SELECT Id, Criteria_Operator__c,
                             Criteria_Field_Value__c, Criteria_Field__c  FROM Omni_Rule_Criteria_Assign_Rule__r)
                            FROM Omni_Case_Assignment_Rule__c
                            WHERE Is_Active__c = true 
                            and (WorkQueue__r.Name IN: lstWorkQueue OR WorkQueue__r.Name = null OR WorkQueue__r.Name = '')
                            Order By Evaluation_Order__c]; 
        return lstCaseAssignRule; 
    }
    
	/*
    * This Method is used to validate based on datatype
    * <p>
    * @ Param     :    sFieldType - type of field.
    * @ Param     :    sCriteriaFieldValue - value from Rule Criteria
    * @ Param     :    sFieldValue - actual value of the field
    * @ Param	  :	   sOperation - operator from Rule Criteria
    * @ Return    :    Boolean
    */
    @TestVisible
   	private static Boolean validateRuleCriteriaByDataType(string sFieldType, string sCriteriaFieldValue, string sFieldValue, string sOperation)
    {
    	boolean bValidationResult = false;
    	string sTempCriteriaFieldValue = '';
    	string[] dateParts;
    	Date dtToday;
    	if(sFieldType.equalsIgnoreCase(FIELD_STRING) || sFieldType.equalsIgnoreCase(FIELD_PICKLIST))
		{
			bValidationResult = validateString(sCriteriaFieldValue,sFieldValue,sOperation);
		}
		else if(sFieldType.equalsIgnoreCase(FIELD_BOOLEAN))
		{
			bValidationResult = validateBoolean(Boolean.valueOf(sCriteriaFieldValue),Boolean.valueOf(sFieldValue),sOperation);   
		}
		else if(sFieldType.equalsIgnoreCase(FIELD_DOUBLE))
		{
			if(sOperation.equalsIgnoreCase('in') || sOperation.equalsIgnoreCase('not in'))
			{
				Set<Double> setCriteriaFieldValues = new Set<Double>();
				String[] arrFieldValue;
				arrFieldValue = sCriteriaFieldValue.split(',');
				for(Integer index=0; index<arrFieldValue.size(); index++)
				{
					setCriteriaFieldValues.add(Double.valueOf(arrFieldValue[index].trim()));
				}
				if(sOperation.equalsIgnoreCase('in'))
					bValidationResult = setCriteriaFieldValues.contains(Double.valueOf(sFieldValue));
				else
					bValidationResult = !(setCriteriaFieldValues.contains(Double.valueOf(sFieldValue)));
			}
			else
			{                
				bValidationResult = validateDecimal(Decimal.valueOf(sCriteriaFieldValue),Decimal.valueOf(sFieldValue),sOperation);
			}
		}
		else if(sFieldType.equalsIgnoreCase(FIELD_DATE))
		{
			dtToday = Date.today();
			Date dtCriteriaFieldValue = null;
			Boolean bProcessAsDate = true;
			if(sCriteriaFieldValue.equalsIgnoreCase(FIELD_TODAY)) dtCriteriaFieldValue = dtToday;
			else 
			{
				sTempCriteriaFieldValue = '';
				dateParts = sCriteriaFieldValue.split('/');
				if(dateParts.size() == 3)
				{
					sTempCriteriaFieldValue = dateParts[2]+'-'+ dateParts[0]+'-'+ dateParts[1];
					dtCriteriaFieldValue = Date.valueOf(sTempCriteriaFieldValue);
				}
				else if(dateParts.size() == 1 && dateParts[0].isNumeric()) 
				{
					if(sOperation.equalsIgnoreCase('greater or equal upto Today') || sOperation.equalsIgnoreCase('less or equal upto Today') || 
					   sOperation.equalsIgnoreCase('greater or equal from Today') || sOperation.equalsIgnoreCase('less or equal from Today')) 
					{
						bValidationResult = validateAgeFields(Date.valueOf(sFieldValue), Integer.valueOf(dateParts[0]), sOperation);
					}
					else 
					{
						bValidationResult = false;
					}                                                                       
					bProcessAsDate = false; 
				}
			}
			if(bProcessAsDate) 
			{
				if(dtCriteriaFieldValue != null)
				{
					 sFieldValue = (sFieldValue.contains('/') && (dateParts = sFieldValue.split('/')).size()==3 ) ?  (dateParts[2]+'-'+ dateParts[0]+'-'+ dateParts[1]) : sFieldValue; 
					bValidationResult = validateDate(dtCriteriaFieldValue,Date.valueOf(sFieldValue),sOperation);
				}
				else
					bValidationResult = false;
			}
		}
		else if(sFieldType.equalsIgnoreCase(FIELD_DATETIME))
		{
			dtToday = Date.today();
			DateTime dtCriteriaFieldValue = null;
			Boolean bProcessAsDate = true;
			if(sCriteriaFieldValue.equalsIgnoreCase(FIELD_TODAY)) dtCriteriaFieldValue = dtToday;
			else 
			{
				sTempCriteriaFieldValue = '';
				dateParts = sCriteriaFieldValue.split('/');
				if(dateParts.size() == 3)
				{
					sTempCriteriaFieldValue = dateParts[2]+'-'+ dateParts[0]+'-'+ dateParts[1] + ' 00:00:00';
					dtCriteriaFieldValue = DateTime.valueOf(sTempCriteriaFieldValue);
				}
				else if(dateParts.size() == 1 && dateParts[0].isNumeric()) 
				{
					if(sOperation.equalsIgnoreCase('greater or equal upto Today') || sOperation.equalsIgnoreCase('less or equal upto Today') || 
					   sOperation.equalsIgnoreCase('greater or equal from Today') || sOperation.equalsIgnoreCase('less or equal from Today')) 
					{
						bValidationResult = validateAgeFields(Date.valueOf(sFieldValue), Integer.valueOf(dateParts[0]), sOperation);
					}
					else 
					{
						bValidationResult = false;
					}                                                                       
					bProcessAsDate = false; 
				}                   
			}
			if(bProcessAsDate) 
			{
				if(dtCriteriaFieldValue != null)
					bValidationResult = validateDateTime(dtCriteriaFieldValue,Date.valueOf(sFieldValue),sOperation);
				else
					bValidationResult = false;
			}
		}
		return bValidationResult;		
    }
    
    /*
    * This Method is used to validate string values
    * <p>
    * @ Param     :    String
    * @ Param     :    String
    * @ Param     :    String
    * @ Return    :    Boolean
    */
    @TestVisible
    private static Boolean validateString(String RHS, String LHS, String operation)
    {
        Boolean result = false;
        operation = operation.remove(' ');
        
        if(operation.equalsIgnoreCase('equals'))
        {
            if(LHS.equalsIgnoreCase(RHS)) result = true;
        }
        else if(operation.equalsIgnoreCase('notequalto'))
        {
            if(!LHS.equalsIgnoreCase(RHS)) result = true;
        }
        else if(operation.equalsIgnoreCase('startswith'))
        {
            if(LHS.startsWithIgnoreCase(RHS)) result = true;
        }
        else if(operation.equalsIgnoreCase('contains'))
        {
            if(LHS.containsIgnoreCase(RHS)) result = true;
        }
        else if(operation.equalsIgnoreCase('doesnotcontain'))
        {
            if(!LHS.containsIgnoreCase(RHS)) result = true;
        }
        else if(operation.equalsIgnoreCase('in') || operation.equalsIgnoreCase('notin')){
            Set<String> setCriteriaFieldValues = new Set<String>();
            String[] arrFieldValue;
            arrFieldValue = RHS.toLowerCase().split(',');
            for(Integer index=0; index<arrFieldValue.size(); index++){
                setCriteriaFieldValues.add(arrFieldValue[index].trim());
            }
            if(operation.equalsIgnoreCase('in'))
                result = setCriteriaFieldValues.contains(LHS.toLowerCase());
            else
                result = !(setCriteriaFieldValues.contains(LHS.toLowerCase()));
        }
        return result;              
    }
    
    /*
    * This Method is used to validate Boolean values
    * <p>
    * @ Param     :    Boolean
    * @ Param     :    Boolean
    * @ Param     :    String
    * @ Return    :    Boolean
    */
    @TestVisible
    private static Boolean validateBoolean(Boolean RHS, Boolean LHS, String operation)
    {
        Boolean result = false;
        operation = operation.remove(' ');
        
        if(operation.equalsIgnoreCase('equals'))
        {
            if(LHS == RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('notequalto'))
        {
            if(LHS != RHS) result = true;
        }
        return result;
    }
    
    /*
    * This Method is used to validate Decimal values
    * <p>
    * @ Param     :    Decimal
    * @ Param     :    Decimal
    * @ Param     :    String
    * @ Return    :    Boolean
    */
    @TestVisible
    private static Boolean validateDecimal(Decimal RHS, Decimal LHS, String operation)
    {
        Boolean result = false;
        operation = operation.remove(' ');
        
        if(operation.equalsIgnoreCase('equals'))
        {
            if(LHS == RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('notequalto'))
        {
            if(LHS != RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('lessthan'))
        {
            if(LHS < RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('greaterthan'))
        {
            if(LHS > RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('lessorequal'))
        {
            if(LHS <= RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('greaterorequal'))
        {
            if(LHS >= RHS) result = true;
        } 
        return result;      
    }  
    
    /*
    * This Method is used to validate Date values
    * <p>
    * @ Param     :    Date
    * @ Param     :    Date
    * @ Param     :    String
    * @ Return    :    Boolean
    */
    @TestVisible
    private static Boolean validateDate(Date RHS, Date LHS, String operation)
    {
        Boolean result = false;
        operation = operation.remove(' ');
        
        if(operation.equalsIgnoreCase('equals'))
        {
            if(LHS == RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('notequalto'))
        {
            if(LHS != RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('lessthan'))
        {
            if(LHS < RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('greaterthan'))
        {
            if(LHS > RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('lessorequal'))
        {
            if(LHS <= RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('greaterorequal'))
        {
            if(LHS >= RHS) result = true;
        }   
        return result;      
    } 
    
    /*
    * This Method is used to validate DateTime values
    * <p>
    * @ Param     :    DateTime
    * @ Param     :    DateTime
    * @ Param     :    String
    * @ Return    :    Boolean
    */
    @TestVisible
    private static Boolean validateDateTime(DateTime RHS, DateTime LHS, String operation)
    {
        Boolean result = false;
        operation = operation.remove(' ');
        
        if(operation.equalsIgnoreCase('equals'))
        {
            if(LHS == RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('notequalto'))
        {
            if(LHS != RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('lessthan'))
        {
            if(LHS < RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('greaterthan'))
        {
            if(LHS > RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('lessorequal'))
        {
            if(LHS <= RHS) result = true;
        }
        else if(operation.equalsIgnoreCase('greaterorequal'))
        {
            if(LHS >= RHS) result = true;
        }        
        return result;      
    }
        
    
    /**
    * getRelationshipRecordsBulk
    * <p>
    * This is the bulkified method used to get all Relationship fields for evaluation
    *
    * @return map<String, sObject>  (Map of ID, Instance of the related Objects)
    **/
    @TestVisible
    private static map<String, sObject> getRelationshipRecordsBulk(list<case> lstCaseRecs)
    {
        map<String, String> mapRelatedObjNames = new map<String, String>();   //map of ParentObject API name, relationshipfield API name
        
        map<String, set<String>> mapRelatedFields = new map<String, set<String>>();     //map of ParentObject API Name, set of fields to query
        
        map<String, set<String>> mapRelatedRecordIds = new map<String, set<String>>();   //map of ParentObject API Name, set of RelatedRecordIds to query
        
        map<String, sObject> mapRelatedObjData = new map<String, sObject>();    // map of recordID, Sobject --> this holds the related Sobject data.
        
        //get all the relationship objects from OmniRuleCriteria_Fields__mdt
        List<OmniRuleCriteria_Fields__mdt> lstCriteriaFields = [SELECT Label, Field_APIName__c, Field_Type__c, ParentObject_APIName__c FROM OmniRuleCriteria_Fields__mdt WHERE ParentObject_APIName__c != null];
        
        //start logic to get all the related objects and the related fields to query
        for(OmniRuleCriteria_Fields__mdt criteriaField : lstCriteriaFields)
        {
            if(mapRelatedObjNames != null && !mapRelatedObjNames.containsKey(criteriaField.ParentObject_APIName__c))
            {               
                String[] lstAPIName = criteriaField.Field_APIName__c.split('\\.');
                String relatedFieldAPI = lstAPIName[0].replace('__r', '__c');
                mapRelatedObjNames.put(criteriaField.ParentObject_APIName__c,relatedFieldAPI);
            }
            
            if(mapRelatedFields != null && !mapRelatedFields.containsKey(criteriaField.ParentObject_APIName__c))
            {
                set<String> relatedFields = new set<String>();
                //relatedFields.add('Id');
                relatedFields.add(criteriaField.Field_APIName__c.substringAfter('__r.'));
                mapRelatedFields.put(criteriaField.ParentObject_APIName__c, relatedFields);
            }
            else if(mapRelatedFields != null && mapRelatedFields.containsKey(criteriaField.ParentObject_APIName__c))
            {
                set<String> relatedFields = mapRelatedFields.get(criteriaField.ParentObject_APIName__c);
                relatedFields.add(criteriaField.Field_APIName__c.substringAfter('__r.'));
                mapRelatedFields.put(criteriaField.ParentObject_APIName__c, relatedFields);                 
            }                       
        }        
        //End logic to get all the related objects and the related fields to query
        
        //initialize mapRelatedRecordIds
        if(!mapRelatedObjNames.isEmpty())
        {
            for(String relatedObject : mapRelatedObjNames.keySet())
            {
                mapRelatedRecordIds.put(relatedObject, new set<String>());                
            }
        }        
        
        //start logic to get all the related References (recordIDs) to query     
        for(case caseRec : lstCaseRecs)
        {       
            if(!mapRelatedObjNames.isEmpty())
            {
                for(String relatedObject : mapRelatedObjNames.keySet())
                {
                    String relatedRecordId = String.valueOf(caseRec.get(mapRelatedObjNames.get(relatedObject))); 
                    if(String.isNotBlank(relatedRecordId))
                    {
                        set<String> relatedIds = mapRelatedRecordIds.get(relatedObject);
                        relatedIds.add(relatedRecordId);
                        mapRelatedRecordIds.put(relatedObject, relatedIds);
                    }   
                }
            }           
        }        
        //End logic to get all the related References (recordIDs) to query
        
        //Start logic to query the related records
        for(String relatedObject : mapRelatedFields.keySet())
        {
            String sRelQuery = 'Select Id';
            if(!mapRelatedRecordIds.get(relatedObject).isEmpty())
            {
                
                for(String sField : mapRelatedFields.get(relatedObject))
                {
                    sRelQuery = sRelQuery + ', ' + sField;
                }        
                sRelQuery = sRelQuery + ' FROM ' + relatedObject + ' WHERE ID IN ';
                
                String sWhereClause = '( ';
                for(String sId : mapRelatedRecordIds.get(relatedObject))
                {
                    sWhereClause = sWhereClause + '\'' + sId + '\'' + ',';  
                }
                sWhereClause = sWhereClause.substring(0, sWhereClause.length()-1);
                sWhereClause = sWhereClause + ' )';                
                sRelQuery = sRelQuery + sWhereClause;                                
                List<SObject> lstRelatedObjects = Database.query(sRelQuery);
                for(SObject relatedSObject : lstRelatedObjects)
                {
                    mapRelatedObjData.put(relatedSObject.Id, (SObject)relatedSObject);
                }                                       
            }
        }                
        //End logic to query the related records 
        
        return mapRelatedObjData;
    }
    
    
        /**
    *  getOmniEligibleMetadata
    * <p>
    * This method Is used for initializing Map of Work Queue and Set of View which are Omni eligible
    * @return  Map<String, Map<String,Omni_Eligible_Queue_Views__mdt>>
    * @see
    */  
    private static Map<String, Map<String,Omni_Eligible_Queue_Views__mdt>> getOmniEligibleMetadata()
    {  
    	Map<String, Map<String,Omni_Eligible_Queue_Views__mdt>> mapByWorkQueue = new Map<String, Map<String,Omni_Eligible_Queue_Views__mdt>>();
        Map<String,Omni_Eligible_Queue_Views__mdt> mapQueueView = new Map<String,Omni_Eligible_Queue_Views__mdt>();
        List<Omni_Eligible_Queue_Views__mdt> lstOmniEligible = [Select QueueViewName__c, WorkQueueName__c, Configuration_Item_Name__c from Omni_Eligible_Queue_Views__mdt];
        	for(Omni_Eligible_Queue_Views__mdt omniQV : lstOmniEligible)
            {
		        if(String.IsNotBlank(omniQV.WorkQueueName__c) && String.IsNotBlank(omniQV.QueueViewName__c))
		        {
		        	if(mapByWorkQueue != null && !mapByWorkQueue.isEmpty() && mapByWorkQueue.containsKey(omniQV.WorkQueueName__c))
		        	{
		        		mapQueueView = 	mapByWorkQueue.get(omniQV.WorkQueueName__c);
		        	}
	                if(mapQueueView == null || !mapQueueView.containsKey(omniQV.QueueViewName__c.trim()))
	                {
	                   mapQueueView.put(omniQV.QueueViewName__c,omniQV) ; 
	                }
		            mapByWorkQueue.put(omniQV.WorkQueueName__c,mapQueueView);
		        }
            }
                          
        return mapByWorkQueue;              
    }
    
	/*
    * This is the Main Method Used to get the omni eligible cases using set of Case ids from Template trigger 
    * <p>
    * @ Param     :    Set<Case>
    * @ Return    :    Void
    */ 
	
    public static void evaluateforTemplate(set<String> setCaseIDs)
    {
        List<Case> lstCases = new List<Case>();
		List<OmniRuleCriteria_Fields__mdt> lstFieldsType = [Select Label, Field_APIName__c, Field_Type__c From OmniRuleCriteria_Fields__mdt WHERE  Object_API_Name__c !=: GLOBAL_CONSTANT_HUM.TEMPLATE_SUBMISSON_DATA_OBJECT];
                String sCriteriaFields = '';
                for(OmniRuleCriteria_Fields__mdt  oField: lstFieldsType){
                    sCriteriaFields = sCriteriaFields +', '+ oField.Field_APIName__c;
                }
                 sCriteriaFields = sCriteriaFields.toLowerCase();
                 String query ='Select id';
                 if(!sCriteriaFields.contains('ownerid')) query = query + ', ownerid';
                 if(!sCriteriaFields.contains('omni_eligible__c')) query = query + ', omni_eligible__c';
                 if(!sCriteriaFields.contains('work_queue_view_name__c')) query = query + ', work_queue_view_name__c';
                 if(!sCriteriaFields.contains(' owner_queue__c')) query = query + ', Owner_Queue__c';
            	 if(!sCriteriaFields.contains('case_owner__c')) query = query + ', Case_Owner__c';
		
		query = query+sCriteriaFields+' FROM Case WHERE Id IN :';
		
        if(setCaseIDs!=null && !setCaseIDs.isEmpty())
        {
		   Set<Id> setCaseFieldsQueryId = new Set<Id>();
		   for(String s:setCaseIDs)
		   {
		   		setCaseFieldsQueryId.add(Id.valueOf(s));
		   }
		   lstCases = (List<Case>)HUMWithoutSharingQueryHelper.getListOfsObjects(query, setCaseFieldsQueryId, NULL, NULL);
		   
		   if(lstCases != null && !lstCases.isEmpty())
		   {
				//Call the existing method for further evaluation
				List<Case> lstOmniCase = new List<Case>();
				for(Case oCase: lstCases){
				   if(oCase.Omni_Eligible__c && !oCase.Status.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM) && !oCase.Status.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM) && oCase.Owner_Queue__c.equalsIgnoreCase(oCase.Case_Owner__c))
				   {   				
						lstOmniCase.add(oCase);								
				   }
				}
				if(lstOmniCase.size()>0)
				{
					EvaluateOmniCaseAssignmentRule(lstOmniCase, true);
				}
		   }
		   
        }
                                                                   
    }

	/*
    * This is the Main Method Used to generate Map of template submission data for omni evaluation 
    * <p>
    * @ Param     :    Set<String>,Set<String>
    * @ Return    :    list<Template_Submission__c>
    */
    @TestVisible
    private static list<Template_Submission__c> getTemplateDataByCases(Set<String> setCaseIDs,Set<String> lstTemplateFieldNames)
    {        
        list<Template_Submission__c> lstTSubmission = new list<Template_Submission__c>();         
        lstTSubmission = [SELECT Id, Submission_Owner__r.Object_Owner_ID__c, Submission_Owner__r.Object_Owner_Type__c, Template__c, 
        				 (SELECT Id, Name, Template_Field__c, Template_Field__r.Name, Name__c, Value__c FROM Template_Submission_Data__r 
        				 WHERE Template_Field__r.Name IN: lstTemplateFieldNames) FROM Template_Submission__c 
        				 WHERE Submission_Owner__r.Object_Owner_ID__c IN :setCaseIDs AND Template__r.Unique_Template_Name__c =: GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_NAME];
		return lstTSubmission;        				                
    }
}