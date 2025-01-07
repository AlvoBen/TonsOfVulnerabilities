/*
Component Name   : CRMRetail_Task_Import_Helper_HUM
Version          : 1.0
Created On       : 10/06/2021
Function         : this class is used as controller for Lightning Web Component importObjects_LWC_HUM

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*****************************************************************************************************************************
* Abhishek Maurya               	                   10/20/2021                    User Story - 2578417 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Add Task Validation
* Ekta Bist                                            12/14/2021                    User Story - 2956323 Task Import BreakFix
* Vinoth L                                             09/14/2022                    Task Import Case Sensitivity Fix
**************************************************************************************************************************** */
public with sharing class  CRMRetail_Task_Import_Helper_HUM {
    
    static Map<String,CRMRetail_Import_DTO_HUM> mapOfCsvColToErrorWrap = new Map<String,CRMRetail_Import_DTO_HUM>();
    @testVisible static Map<String,CRMRetail_Import_DTO_HUM> mapOfCsvColToWrap = new Map<String,CRMRetail_Import_DTO_HUM>();
    static Map<String,CRMRetail_Import_DTO_HUM> mapOfQurdIdToWrap = new Map<String,CRMRetail_Import_DTO_HUM>();
    static Map<String,set<String>> mapOfQuryParamToQurySet = new Map<String,set<String>>();
    @testVisible static Map<String,Map<String,String>> mpOfQueRec = new Map<String,Map<String,String>>();
    static Map<String,Date> mapOfMemIdToDueDate = new Map<String,Date>();
    static Map<String,Set<String>> mapOfMemIdToAccs =new Map<String,Set<String>>();
    static Map<String,Task> mapOftasksForDuplCheck = new Map<String,Task>(); 
    static Map<String,Map<String,Task>> mapOfTaskAfterDupCheck = new Map<String,Map<String,Task>>();
    static Map<String,String> mapOfAccIdToPerCnctId = new Map<String,String>();
    static List<CRMRetail_Import_DTO_HUM> lstOfDupRecFromFile = new List<CRMRetail_Import_DTO_HUM>();
    static Id crmRetailTask;
    @testVisible static Boolean isTestClassContext = false;
    static Map<integer,String> mapOfMemIds = new Map<integer,String>();
    static Boolean switch_3573183 = HUMUtilityHelper.isCRMFunctionalityON(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SWITCH_CASE_SENSITIVITY);
    /*
    * Method Name   :    fetchSwitchResults
    * Description   :    This method returns values of the switch records
    * Return Type   :    Map<String,Boolean>
    * Parameters    :    NA
    */
    @AuraEnabled(cacheable=true)
    public static Map<String,Boolean> fetchSwitchResults() 
    {
        Map<String,Boolean> switchMap = new Map<String,Boolean>();
        Map<String,Boolean> resultMap = new Map<String,Boolean>();
        try
        {
            switchMap = GenericHelper_CRMRetail_H_HUM.fetchSwitchResults();
            if (switchMap.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SWITCH_2578377))
            {  
                resultMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKBUTTON,switchMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SWITCH_2578377));
            }
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_IMPORT_CLASS_NAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_IMPORT_BUTTON_VISIBLE);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_IMPORT_CLASS_NAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_IMPORT_BUTTON_VISIBLE, ex.getMessage());
        }
        return resultMap;
    }
    /*
    * Method Name   :    validateTaskFieldsOnImport
    * Description   :    This method validates the individual values of the task records
    * Return Type   :    String
    * Parameters    :    Map<String,Object>
    */
    @AuraEnabled
    public static Map<String,Object> validateTaskFieldsOnImport(String jSONInput){
        try{
            Map<String,Object> finalWrap = new Map<String,Object>();
            List<CRMRetail_Import_DTO_HUM> lstOfImprtRows = new List<CRMRetail_Import_DTO_HUM>();
            lstOfImprtRows = (List<CRMRetail_Import_DTO_HUM>) JSON.deserialize(jSONInput,List<CRMRetail_Import_DTO_HUM>.class);        
            String key;
            Integer i = 1;
            Integer j = 1;
            boolean isMapPopulated = false;
            String searchStr = '{';
            mapOfQuryParamToQurySet.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MEMBERID,new set<String>{});
            mapOfQuryParamToQurySet.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_LOCATION,new set<String>{});
            mapOfQuryParamToQurySet.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ASSIGNEDTO,new set<String>{});
            mapOfQuryParamToQurySet.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SUBJECT,new set<String>{});
            crmRetailTask = Schema.SObjectType.Task.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRM_RETAIL_TASK).getRecordTypeId();			
			Date dt;
            for(CRMRetail_Import_DTO_HUM wrapRef : lstOfImprtRows){
                key = wrapRef.memberid+'@'+wrapRef.assignedto+'@'+wrapRef.location+'@'+wrapRef.subject+'@'+wrapRef.duedate;
                if(!mapOfCsvColToWrap.containsKey(key)){
                    wrapRef.error ='';
                    if(String.isBlank(wrapRef.memberid)){
                        wrapRef.error = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_MEMBERID;                    
                    }
                    if(String.isBlank(wrapRef.assignedto)){
                        wrapRef.error += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_ASSIGNEDTO;
                    }
                    if(String.isBlank(wrapRef.subject)){
                        wrapRef.error += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_SUBJECT;                    
                    }
                    if(String.isBlank(wrapRef.location)){
                        wrapRef.error += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_LOCATION;
                    }
                    if(String.isBlank(wrapRef.priority) || !GLOBAL_CONSTANT_STOREFRONT_HUM.SET_CRMRETAIL_PRIORITY.contains(wrapRef.priority.toUppercase())){
                        wrapRef.error += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_PRIORITY;                   
                    }                     
					if(String.isBlank(wrapRef.duedate)){
                        wrapRef.error += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_DUEDATE;                    
                    }
					else{
						
						try{
							dt = Date.parse(wrapRef.duedate.trim());
							if(dt < Date.today()){
								wrapRef.error += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_DUEDATE;
							}
						}catch(Exception e){
							wrapRef.error += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_DUEDATE;
						}						
					}					
                    mapOfCsvColToWrap.put(key,wrapRef);
                    if(!wrapRef.error.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_MEMBERID)){
                        if(!mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MEMBERID).contains(wrapRef.memberid.trim()))
                        {
                            isMapPopulated = false;
                            if(switch_3573183){
                            	wrapRef.memberid = wrapRef.memberid.toUppercase();
                            }
                            searchStr += wrapRef.memberid.trim() + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_OR;
                            if(i == GLOBAL_CONSTANT_STOREFRONT_HUM.INTEGER_LIMIT_200){
                                isMapPopulated = true;
                                searchStr = searchStr.removeEnd(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_OR)+'}';
                                mapOfMemIds.put(j,searchStr);
                                searchStr = '{';
                                i = 1;
                                j++;
                            }
                            else{
                                i++;
                            }
                            
                        }
                        mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MEMBERID).add(wrapRef.memberid.trim());
                    }
                    if(!wrapRef.error.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_LOCATION) ){
                        mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_LOCATION).add(wrapRef.location.replace('""','"').trim());
                    }
                    if(!wrapRef.error.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_ASSIGNEDTO)){
                        mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ASSIGNEDTO).add(wrapRef.assignedto.trim());
                    }
                    if(!wrapRef.error.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_SUBJECT)){
                        mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SUBJECT).add(wrapRef.subject.replace('""','"').trim());
                    }
                    if(!wrapRef.error.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_DUEDATE)){
                        mapOfMemIdToDueDate.put(key,date.parse(wrapRef.duedate.trim()));
                    }
                }
                else{
                    wrapRef.error = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DUPLICATE_TASK;
                    lstOfDupRecFromFile.add(wrapRef);
                }                                    
            }  
            
            if (!isMapPopulated){
                searchStr = searchStr.removeEnd(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_OR)+'}';
                mapOfMemIds.put(j,searchStr);
            }  
            
            
            if(mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MEMBERID).size()>0 || mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_LOCATION).size()>0 || mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ASSIGNEDTO).size()>0 ||  mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SUBJECT).size()>0 ){
                queryAllRecords();
            }
            if(mpOfQueRec.size() >0 || mapOfMemIdToAccs.size()>0){
                validateIndividualFields();
            }
            if(mapOftasksForDuplCheck.size() >0){            
                mapOfTaskAfterDupCheck = GenericHelper_CRMRetail_H_HUM.checkTaskDuplicates(mapOftasksForDuplCheck);
                if(mapOfTaskAfterDupCheck.size()>0){
                    insertTaskRecords();
                }
            }
            if(mapOfQurdIdToWrap.size() >0){
                Set<String> validMemId = new Set<String>();
                String str1;
                for(String keyVal:mapOfQurdIdToWrap.keySet()){
                    str1= mapOfQurdIdToWrap.get(keyVal).memberId+keyVal.subStringBefore('@') +keyVal.subStringAfter('@').subStringAfter('@');
                    validMemId.add(str1);
                }
                finalWrap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VALIDCOUNT,validMemId.size());
            }
            if(mapOfCsvColToErrorWrap.size() >0 || lstOfDupRecFromFile.size()>0){            
                lstOfDupRecFromFile.addAll(mapOfCsvColToErrorWrap.values());
                finalWrap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVLAIDRECORD,lstOfDupRecFromFile);
            }
            return finalWrap;
        }	
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_IMPORT_CLASS_NAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_VALIDATEMETHOD);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_IMPORT_CLASS_NAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_VALIDATEMETHOD, ex.getMessage());
        }
    }  
    /*
    * Method Name   :    insertTaskRecords
    * Description   :    This method validates the condition for inserting the task records
    * Return Type   :    NA
    * Parameters    :    NA
    */  
    static void insertTaskRecords(){
        try{
            String dupKey,nonDupKey,str1;
            Set<String> setOfUniqDupTask =new Set<String>();
            for(String key: mapOfQurdIdToWrap.keySet()){ 
                if(mapOfTaskAfterDupCheck.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DUPLICATE_KEY) && mapOfTaskAfterDupCheck.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DUPLICATE_KEY).containsKey(key)){
                    dupKey= mapOfQurdIdToWrap.get(key).memberId+key.subStringBefore('@')+key.subStringAfter('@').subStringAfter('@');
                    if(!setOfUniqDupTask.contains(dupKey)){
                        setOfUniqDupTask.add(dupKey);
                        CRMRetail_Import_DTO_HUM wrapRef = new CRMRetail_Import_DTO_HUM();
                        wrapRef = mapOfQurdIdToWrap.get(key);
                        wrapRef.error = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DUPLICATE_TASK;         
                        mapOfCsvColToErrorWrap.put(key,wrapRef); 
                    }
                    mapOfQurdIdToWrap.remove(key);
                }                
            }
            for(String key: mapOfQurdIdToWrap.keySet()){
               if(mapOfTaskAfterDupCheck.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NON_DUPLICATE_KEY) && mapOfTaskAfterDupCheck.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NON_DUPLICATE_KEY).containsKey(key)){
                    nonDupKey= mapOfQurdIdToWrap.get(key).memberId+key.subStringBefore('@')+key.subStringAfter('@').subStringAfter('@');
                    if(setOfUniqDupTask.contains(nonDupKey)){
                        mapOfTaskAfterDupCheck.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NON_DUPLICATE_KEY).remove(key);
                        mapOfQurdIdToWrap.remove(key);
                    }
                }  
            }
            if(mapOfTaskAfterDupCheck.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NON_DUPLICATE_KEY)){                
                if(Test.isRunningTest() && isTestClassContext)
                {
                    for(Task objTask : mapOfTaskAfterDupCheck.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NON_DUPLICATE_KEY).values())
                    {              
                        objTask.Status = '';
                    }
                }
                Database.SaveResult[] lstValidRecords = Database.insert(mapOfTaskAfterDupCheck.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NON_DUPLICATE_KEY).values(),false);
                Integer index = 0;
                String errorMesg = '';
                Set<String> setOfKeys =new Set<String>();
                for (Database.SaveResult validrec : lstValidRecords) 
                {
                    if (validrec.isSuccess()) 
                    {
                        index++;
                    }
                    else 
                    {                     
                        for(Database.Error err : validrec.getErrors())
                        {
                            errorMesg+= err.getMessage();
                        }
                        Task tkRef = mapOfTaskAfterDupCheck.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NON_DUPLICATE_KEY).values().get(index);
                        if(tkRef != NULL){
                            String key = tkRef.Subject+'@'+tkRef.WhatId+'@'+tkRef.CRM_Retail_Location__c+'@'+tkRef.OwnerId+'@'+tkRef.ActivityDate;
                            CRMRetail_Import_DTO_HUM wpRef = mapOfQurdIdToWrap.get(key);
                            str1= mapOfQurdIdToWrap.get(key).memberId+key.subStringBefore('@')+key.subStringAfter('@').subStringAfter('@');
                            if(wpRef != NULL){
                                if(!setOfKeys.contains(str1)){
                                    setOfKeys.add(str1);    
                                }else{
                                    mapOfQurdIdToWrap.remove(key);
                                    continue;
                                }
                                wpRef.error = errorMesg;
                                mapOfCsvColToErrorWrap.put(key,wpRef);
                                mapOfQurdIdToWrap.remove(key);
                            }
                        }
                        index++;
                    }
                }
            }
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_IMPORT_CLASS_NAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_INSERTMETHOD);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_IMPORT_CLASS_NAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_INSERTMETHOD, ex.getMessage());
        }
    }
    /*
    * Method Name   :    queryAllRecords
    * Description   :    This method performs query on individual fields to validate the input records
    * Return Type   :    NA
    * Parameters    :    NA
    */ 
    static void queryAllRecords(){
        try{
            List<String> lstOfKeys = new List<String>();
            if(mapOfQuryParamToQurySet.size()>0){
                mpOfQueRec.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_LOCATION,new Map<String,String>());
                mpOfQueRec.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SUBJECT,new Map<String,String>());
                mpOfQueRec.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ASSIGNEDTO,new Map<String,String>());
                Map<Id,Storefront_Location__c> mpOfLoc= GenericHelper_CRMRetail_H_HUM.getLocation(mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_LOCATION));
                if(mpOfLoc.size()>0){
                    for(Storefront_Location__c locRef:mpOfLoc.values()){                                        
                        mpOfQueRec.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_LOCATION).put(locRef.Name.toUppercase(),locRef.Id);                      
                    }
                }
                Map<Id,Storefront_I_Reason__C> mpOfIntReson= GenericHelper_CRMRetail_H_HUM.getIntReasonRecords(mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SUBJECT));                
                if(mpOfIntReson.size()>0){
                    for(Storefront_I_Reason__C resRef:mpOfIntReson.values()){     
                        if(switch_3573183){
                            mpOfQueRec.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SUBJECT).put(resRef.Name,resRef.Id);      
                        }
                        else{
                            mpOfQueRec.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SUBJECT).put(resRef.Name.toUppercase(),resRef.Id);  
                        }
                                    
                    }
                }            
                Map<Id,User> mpOfUser= GenericHelper_CRMRetail_H_HUM.getUserRecords(mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ASSIGNEDTO));
                if(mpOfUser.size()>0){
                    for(User usrRef:mpOfUser.values()){    
                        if(usrRef.PermissionSetAssignments.Size()>0){
                        	mpOfQueRec.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ASSIGNEDTO).put(usrRef.Alias.toUppercase(),usrRef.Id);                     
                        }                        
                    }
                }
                List<Member_ID__C> lstOfMemberId= GenericHelper_CRMRetail_H_HUM.getMemberRecords(mapOfMemIds);
                if(lstOfMemberId.size()>0){
                    Set<string> setOfMemberIdsLocal = mapOfQuryParamToQurySet.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MEMBERID);
                        for(Member_ID__C memRef:lstOfMemberId){
                            if (setOfMemberIdsLocal.contains(memRef.name))
                            {
                                if(!mapOfMemIdToAccs.containsKey(memRef.Name.toUppercase())){
                                    if(switch_3573183){
                                    	mapOfMemIdToAccs.put(memRef.Name, New Set<String>{memRef.Policy_Member__r.Member__c});
                                    }
                                    else{
                                        mapOfMemIdToAccs.put(memRef.Name.toUppercase(), New Set<String>{memRef.Policy_Member__r.Member__c});
                                    }
                                } 
                                else{
                                    mapOfMemIdToAccs.get(memRef.Name).add(memRef.Policy_Member__r.Member__c);
                                }
                                mapOfAccIdToPerCnctId.put(memRef.Policy_Member__r.Member__c,memRef.Policy_Member__r.Member__r.PersonContactId);                    
                            }
                        }
                }            
            }
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_IMPORT_CLASS_NAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_QUERYMETHOD);
            throw AuraErrorHandler_C_HUM.createAuraHandledException(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_IMPORT_CLASS_NAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_QUERYMETHOD, ex.getMessage());
        }
    }
    /*
    * Method Name   :    validateIndividualFields
    * Description   :    This method performs query on individual fields to validate the input records
    * Return Type   :    NA
    * Parameters    :    NA
    */ 
    static void validateIndividualFields(){		
		map<string,CRMRetail_Import_DTO_HUM> prepTaskForDupCheckMap = new map<string,CRMRetail_Import_DTO_HUM>();
        CRMRetail_Import_DTO_HUM wrapRef;       
        for(String key: mapOfCsvColToWrap.keySet()){
            wrapRef = mapOfCsvColToWrap.get(key);              
            wrapRef.LOCATION = wrapRef.LOCATION.replace('""','"');
            wrapRef.SUBJECT = wrapRef.SUBJECT.replace('""','"');
            String wrapSubject;
            if(switch_3573183){
               wrapSubject = wrapRef.subject;
            }
            else{
               wrapSubject = wrapRef.subject.toUppercase();
            }
            if(!mapOfMemIdToAccs.containsKey(wrapRef.memberId.toUppercase())){
                if( !wrapRef.error.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_MEMBERID)){
                    wrapRef.error += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_MEMBERID;  
                }
            }              
            if(!mpOfQueRec.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ASSIGNEDTO).containsKey(wrapRef.assignedTo.toUppercase())){
                if( !wrapRef.error.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_ASSIGNEDTO)){
                    wrapRef.error += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_ASSIGNEDTO;
                }
            }
            if(!mpOfQueRec.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_LOCATION).containsKey(wrapRef.location.toUppercase())){
                if( !wrapRef.error.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_LOCATION)){
                    wrapRef.error += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_LOCATION;
                }
            }
            if(!mpOfQueRec.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SUBJECT).containsKey(wrapSubject)){
                if( !wrapRef.error.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_SUBJECT)){
                    wrapRef.error += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALID_SUBJECT;
                }
            }
            if(String.isNotBlank(wrapRef.error)){
                if(!mapOfCsvColToErrorWrap.containsKey(key)){
                    wrapRef.error = wrapRef.error.substringBeforeLast(';');
                    mapOfCsvColToErrorWrap.put(key,wrapRef); 
                }
                
            }else{                
				prepTaskForDupCheckMap.put(key,wrapRef);
            }
        } 
		if (!prepTaskForDupCheckMap.isEmpty()){
			prepTaskForDupCheck(prepTaskForDupCheckMap);
		}
    }    
    /*
    * Method Name   :    prepTaskForDupCheck
    * Description   :    This method performs query on individual fields to check duplicates on database and the input file
    * Return Type   :    NA
    * Parameters    :    NA
    */ 	
	static void prepTaskForDupCheck(map<string,CRMRetail_Import_DTO_HUM> prepTaskForDupCheckMap){
        Task tkRef = new Task();
		CRMRetail_Import_DTO_HUM wrapRef;
		for (string key: prepTaskForDupCheckMap.keySet()){	
			tkRef = new Task();		
			wrapRef = prepTaskForDupCheckMap.get(key);
			tkRef.Priority = wrapRef.PRIORITY;
			tkRef.OwnerId = mpOfQueRec.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ASSIGNEDTO).get(wrapRef.assignedTo.toUppercase());
			tkRef.CRM_Retail_Location__c = mpOfQueRec.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_LOCATION).get(wrapRef.location.toUppercase());
			tkRef.subject = wrapRef.subject;
			tkRef.ActivityDate = mapOfMemIdToDueDate.get(key);
			integer count =0;
			String tempKey;
			for(String accId :mapOfMemIdToAccs.get(wrapRef.memberId.toUppercase())){
				if(count ==0){
					tkRef.WhatId = accId; 
					tkRef.RecordTypeId = crmRetailTask;
					tkRef.CRMRetail_Automated__c = true;
					tkRef.WhoId = mapOfAccIdToPerCnctId.get(accId);
					tempKey = tkRef.Subject+'@'+tkRef.WhatId+'@'+tkRef.CRM_Retail_Location__c+'@'+tkRef.OwnerId+'@'+tkRef.ActivityDate;
					mapOftasksForDuplCheck.put(tempkey,tkRef);
					mapOfQurdIdToWrap.put(tempKey,mapOfCsvColToWrap.get(key));
				}
				if(count >=1){
					Task ref = new Task();
					ref =tkRef.clone(false,true,false,false);
					ref.WhatId = accId;
					ref.CRMRetail_Automated__c = true;
					ref.WhoId = mapOfAccIdToPerCnctId.get(accId);
					tempKey = ref.Subject+'@'+ref.WhatId+'@'+ref.CRM_Retail_Location__c+'@'+ref.OwnerId+'@'+ref.ActivityDate;
					mapOftasksForDuplCheck.put(tempkey,ref);
					mapOfQurdIdToWrap.put(tempKey,mapOfCsvColToWrap.get(key));
				}
				count++;
			}
		}
    }	
}