/*******************************************************************************************************************************
Apex Class Name : CreateTask_C_LWC_HUM
Version         : 1.0
Created On      : 23/06/2022
Function        : This class is used for create, edit, view and task related operations
Test Class      : CreateTask_T_LWC_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Bhumika Dodiya                                    23/06/2022               Original Version
*    			   Raj Paliwal										 17/02/2023				  US: 4163250 -Task Type (Notification) NEW Task
*				   Vishal Shinde 								     17/02/2023				  US: 4184082 -Task Type (Notification) EDIT Task
*				   Prasuna Pattabhi 								 08/24/2023				  US: 4412371 -Market Credentialing Task Fields
*                  Vani Shrivastava                                  09/15/2023                US 4891201: T1PRJ0865978 C06- Case Management- Case Page- Need toast message for Task creation and close
*                  Prasuna Pattabhi                                      10/04/23                 US 4412371 Market Credentialing Task Fields Switch Changes
*******************************************************************************************************************************************************************/
public with sharing class CreateTask_C_LWC_HUM {
    private static Set<string> setServiceCenter = new Set<string>();
    private static Set<string> setDepartment = new Set<string>();
    private static Integer iLimitValue = 1000;
    private static Map<String,Work_Queue_Setup__c> mapServCenDeptWorkQueueSetup;
    public  static Map<String, Work_Queue_Setup__c> mapWorkQueueSetup ;
    public  static Boolean bIsPharmacyUser {get;set;}
	public  static List<String> lstSCenters = new List<String>{GLOBAL_CONSTANT_HUM.SERVICE_CENTER_HUMANA_PHARMACY,GLOBAL_CONSTANT_HUM.SERVICE_CENTER_RX};
    public  static List<String> lstWQueues = new List<String>{GLOBAL_CONSTANT_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,GLOBAL_CONSTANT_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,GLOBAL_CONSTANT_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public  static List<String> lstTopics = new List<String>{GLOBAL_CONSTANT_HUM.TOPIC_SPECIAL_INVESTIGATIONS,GLOBAL_CONSTANT_HUM.TOPIC_ORAL_GRIEVANCE,GLOBAL_CONSTANT_HUM.NONE_HUM};
    public static String sSelectedWorkQueue;
    public static Work_Queue_setup__c oWQS;
    public static String sUserName;
    public static String sUserID;
    private  static Case oRelCase = new Case();
	
    /*
    * Method Name : getWorkQueueDetail
    * Description : method used to get work queue detail
    * Return type : void
    */
    public static void getWorkQueueDetail()
    {  
        try{
            bIsPharmacyUser = Pharmacy_H_HUM.isPharmacyUser() == GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM ? false : true;
		    mapWorkQueueSetup = New Map < String, Work_Queue_Setup__c>([Select Name,id,Classification_Type__r.Name,Classification_Type__c,
                                                                CokY__c, 	 Department__c, DST__c, General__c, 
                                                                Contract_Protected_Data__c,HO_Dental__c, HO_Medical__c, Public_Group_Name__c,
                                                                Queue_Manager__c, 		
                                                                Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId from 
                                                                Work_Queue_Setup__c 
				                                                Where IsActive__c = TRUE limit :iLimitValue]);
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CreateTask_C_LWC_HUM', 'getWorkQueueDetail');
            throw new HUMCustomException(e.getMessage());
        }        
    }
    
    /*
    * Method Name : getServicecenterDeptRelation
    * Description : method used to get map of service center dept. 
    * Return type : void
    */
    @AuraEnabled
    public static void getServicecenterDeptRelation()
    {
        mapServCenDeptWorkQueueSetup =  new Map<String,Work_Queue_Setup__c>();
        if(mapWorkQueueSetup == null) {getWorkQueueDetail();}
            for(String sWorkQueue : mapWorkQueueSetup.keySet()){                
                mapServCenDeptWorkQueueSetup.put(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c +'#' + mapWorkQueueSetup.get(sWorkQueue).Department__c,mapWorkQueueSetup.get(sWorkQueue));				
            }
    }

    /*
    * Method Name : getCaseId
    * Description : method used to get caseId or taskId 
    * Return type : void
    */
    public static String getCaseId(String myRecId)
    {        
        Id recordID = Id.valueOf(myRecId);
        String caseOrTaskId;
        if(recordID.getsobjecttype() == Schema.Case.getSObjectType()){
            caseOrTaskId = recordID;          
        } else if(recordID.getsobjecttype() == Schema.Task.getSObjectType()){
            caseOrTaskId = [select Id, whatId from Task where id =: recordID].whatId;
        }
        return caseOrTaskId;
    }

    /*
    * Method Name : getLstServiceCenter
    * Description : method used to get service center
    * Return type : list<String>
    * Parameter : record id                                   
    */    
	@AuraEnabled
    public static list<String> getLstServiceCenter(String myRecId)
    {   
        try{
            String sWhatId = getCaseId(myRecId);
            if(NULL != sWhatId){
            oRelCase = [Select Recordtype.Name,Tenant_Id__c, CoKY_Case__c, HO_Dental_Case__c, DST_Case__c, HO_Medical_Case__c, General_Case__c,Contract_Protected_Case__c from case where id =: sWhatId];
            }
            setServiceCenter = new Set<String>();
            if(mapWorkQueueSetup == null) {getWorkQueueDetail();}
            for(String sWorkQueue : mapWorkQueueSetup.keySet()){ 
                if(getCaseIndicatorStatus(sWorkQueue)){
                    if(!bIsPharmacyUser){    
                        setServiceCenter.add(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c);
                    }else{  
                        if(lstWQueues.contains(mapWorkQueueSetup.get(sWorkQueue).Name) || lstSCenters.contains(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c)){
                            setServiceCenter.add(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c);
                        }
                    }
                }
            }
            return HUMUtilityHelper.doSort(setServiceCenter);
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CreateTask_C_LWC_HUM', 'getLstServiceCenter');
            throw new HUMCustomException(e.getMessage());
        }
    }

    public static boolean getCaseIndicatorStatus(String sQueueName)
    {
        return (mapWorkQueueSetup.get(sQueueName).General__c && oRelCase.General_Case__c ||
                mapWorkQueueSetup.get(sQueueName).General__c && oRelCase.Contract_Protected_Case__c == '1' ||
                mapWorkQueueSetup.get(sQueueName).DST__c && oRelCase.DST_Case__c ||
                mapWorkQueueSetup.get(sQueueName).HO_Dental__c && oRelCase.HO_Dental_Case__c ||   
                mapWorkQueueSetup.get(sQueueName).HO_Medical__c && oRelCase.HO_Medical_Case__c ||
                mapWorkQueueSetup.get(sQueueName).CokY__c &&  oRelCase.CoKY_Case__c) &&
                NULL != mapWorkQueueSetup.get(sQueueName).TenantID__c && NULL != oRelCase.Tenant_Id__c &&
                mapWorkQueueSetup.get(sQueueName).TenantID__c.equals(oRelCase.Tenant_Id__c);        
    }

    /*
    * Method Name : populateQueueName
    * Description : method used to get queue name
    * Return type : String
    * Parameter : sServiceCenter,sDepartment                                  
    */
    public static String populateQueueName(String sServiceCenter, String sDepartment)
    {
        try{
            if(mapServCenDeptWorkQueueSetup == null) {getServicecenterDeptRelation();}
            oWQS = new Work_Queue_setup__c();
            if(mapServCenDeptWorkQueueSetup.containsKey(sServiceCenter + '#' + sDepartment)){
                oWQS = mapServCenDeptWorkQueueSetup.get(sServiceCenter + '#' + sDepartment);
                sSelectedWorkQueue = oWQS.Work_Queue_Name__c;               
            }
            return sSelectedWorkQueue;
        }catch(Exception ex){
             HUMExceptionHelper.logErrors(ex, 'CreateTask_C_LWC', 'populateQueueName');
             throw new AuraHandledException(ex.getMessage());
        }        
    }

    /*
    * Method Name : getDepartments
    * Description : method used to get list of departments
    * Return type : String
    * Parameter : sServiceCenter,myRecId                                  
    */
    @AuraEnabled
    public static list<String> getDepartments(String sServiceCenter, String myRecId)
    {
        try{
            String sWhatId = getCaseId(myRecId);
            if(NULL != sWhatId){
            oRelCase = [Select Recordtype.Name,Tenant_Id__c, CoKY_Case__c, HO_Dental_Case__c, DST_Case__c, HO_Medical_Case__c, General_Case__c,Contract_Protected_Case__c from case where id =: sWhatId];
            }

            if(mapWorkQueueSetup == null) {getWorkQueueDetail();}
            setDepartment = new Set<String>();
            for(string sWorkQueue : mapWorkQueueSetup.keySet()) {
                if(sServiceCenter !=null && mapWorkQueueSetup.get(sWorkQueue).Service_Center__c.equalsIgnoreCase(sServiceCenter) &&
                !string.isBlank(mapWorkQueueSetup.get(sWorkQueue).Department__c)){   
                    if(getCaseIndicatorStatus(sWorkQueue)){ 
                        if(!bIsPharmacyUser) { 
                            setDepartment.add(mapWorkQueueSetup.get(sWorkQueue).Department__c);
                        } else {
                            if(lstWQueues.contains(mapWorkQueueSetup.get(sWorkQueue).Name) || lstSCenters.contains(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c)){
                                setDepartment.add(mapWorkQueueSetup.get(sWorkQueue).Department__c);
                            }
                        }
                    }
                }
            }  
            return HUMUtilityHelper.doSort(setDepartment);
        }catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CreateTask_C_LWC', 'getDepartments');
            throw new AuraHandledException(ex.getMessage());
        } 
    } 

    /*
    * Method Name : getTopics
    * Description : method used to get topics
    * Return type : String
    * Parameter : sServiceCenter,sDepartment                                  
    */
    @AuraEnabled
    public static TopicANdUserLookupWrapper getTopics(String sServiceCenter, String sDepartment)
    {
        try {
            TopicANdUserLookupWrapper topUserWp = new TopicANdUserLookupWrapper();
            if(mapWorkQueueSetup == null) {getWorkQueueDetail();}
            if(mapServCenDeptWorkQueueSetup == null) {getServicecenterDeptRelation();}
            String sWorkQueueSetUpName=null;
            Set<String> setTopic = new Set<String>();
            List<String> lstOptions = new List<String>();
            
            if((String.isBlank(sServiceCenter) && String.isBlank(sDepartment)) || (!sServiceCenter.equalsIgnoreCase('None') 
            && sDepartment.equalsIgnoreCase('None')))   return null;
        
            for(string sWorkQueue : mapWorkQueueSetup.keySet()){
                if(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c.equalsIgnoreCase(sServiceCenter) &&
                        mapWorkQueueSetup.get(sWorkQueue).Department__c.equalsIgnoreCase(sDepartment)){
                    sWorkQueueSetUpName = sWorkQueue;
                    break;
                }
            }
            
            List<Work_Queue_Junction__c> lstWorkQueueJunction = [select Work_Queue_Setup_Name__c, Work_Queue_Topic_Name__r.Name from Work_Queue_Junction__c where Work_Queue_Setup_Name__c =: sWorkQueueSetUpName and Work_Queue_Topic_Name__r.IsActive__c = true];
            if(!lstWorkQueueJunction.isEmpty() && lstWorkQueueJunction.size() > 0) {
                for(Work_Queue_Junction__c oWQJ : lstWorkQueueJunction) {
                    if(!bIsPharmacyUser) {  
                        setTopic.add(oWQJ.Work_Queue_Topic_Name__r.Name); 
                    } else {
                        if(lstSCenters.contains(sServiceCenter) || lstTopics.contains(oWQJ.Work_Queue_Topic_Name__r.Name)){
                            setTopic.add(oWQJ.Work_Queue_Topic_Name__r.Name); 
                        }                        
                    }
                }
                topUserWp.topicList = HUMUtilityHelper.doSort(setTopic);           
            } else{                
                topUserWp.topicList = null;
            }
            topUserWp.workQueue = populateQueueName(sServiceCenter, sDepartment);
            return topUserWp;
        }catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CreateTask_C_LWC', 'getTopics');
            throw new AuraHandledException(ex.getMessage());
        } 
    }

    /*
    * Method Name : getMedicareId
    * Description : method used to get medicare id
    * Return type : String
    * Parameter : myRecId                                  
    */
    @AuraEnabled
    public static string getMedicareId(String myRecId){
        try{
            String rRecordId = getCaseId(myRecId);
            List<Case> lstCase = [select id,Account.MedicareID__c from Case where id = :rRecordId limit 1];
            if(null != lstCase && !lstCase.isEmpty() && null != lstCase[0].Account.MedicareID__c) {
                return lstCase[0].Account.MedicareID__c;
            }
            return null;
        }catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CreateTask_C_LWC', 'getMedicareId');
            throw new AuraHandledException(ex.getMessage());
        } 
    }
    /*
    * Method Name : createTask
    * Description : method used to create task
    * Return type : String
    * Parameter : taskData                                  
    */
    @AuraEnabled
    public static String createTask(String taskData)
    {
        try{            
            List<SObject> sObjectToInsert = new List<SObject>();
            Map<String, Object> responseMap = (Map<String, Object>) JSON.deserializeUntyped(taskData);   
            getServicecenterDeptRelation();
           
            if(responseMap.keySet().size() > 1){
                //dependent task creation code
                Task oTask = (Task) JSON.deserialize( JSON.serialize(responseMap.get('taskFields')),Task.class);
                mapTaskOwnerFields(oTask);
                HUM_Task_Heirarchy__c  oTaskH = (HUM_Task_Heirarchy__c ) JSON.deserialize( JSON.serialize(responseMap.get('taskHistoryFields')),HUM_Task_Heirarchy__c.class);
                Task oTaskRec = [select whatId from task where Id =:oTaskH.HUM_Parent_TaskID__c limit 1];

                //make comment null
                String sComment = oTask.Description;
                oTask.RecordtypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId(); //temp. adding recordtype
                if(Schema.SObjectType.Task.fields.whatId.isCreateable() || Schema.SObjectType.Task.fields.whatId.isUpdateable()) oTask.WhatId =  oTaskRec.whatId;
                
                Database.upsertResult upsertResult = Database.upsert(oTask,false);
                sObjectToInsert.add(updateTaskSubject(oTask.id));

                if(sComment !=null){
                    Task_Field_History__c oTaskFH = new Task_Field_History__c ();
                    
                    if(Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isUpdateable()) oTaskFH.SObjID__c = oTask.Id;  
                    if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isUpdateable()) oTaskFH.SObjFieldName__c = GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM; 
                    if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isUpdateable()) oTaskFH.SObjFieldValue__c= sComment;    
                    if(Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isUpdateable()) oTaskFH.Created_At__c = System.now(); 
                    sObjectToInsert.add(oTaskFH);
                }
                oTaskH.HUM_Dependent_TaskID__c  = (string) oTask.id;
                oTaskH.Hum_CaseID__c =oTaskRec.whatID;
                sObjectToInsert.add(oTaskH);                
                upsert(sObjectToInsert);
                return oTask.id;                
            }
            else if(responseMap.keySet().size() == 1){
                //new task creation from case page
                TaskWrapper taskObjJsonData = (TaskWrapper) JSON.deserialize(JSON.serialize(responseMap.get('taskFields')),TaskWrapper.class);
                Task taskObj = new Task();
                taskObj.Priority = taskObjJsonData.Priority;
                taskObj.Status = taskObjJsonData.Status;
                taskObj.Type = taskObjJsonData.Type;
                taskObj.Description = taskObjJsonData.Description;                
                taskObj.RecordtypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId();
                
                taskObj.Service_Center__c = taskObjJsonData.ServiceCenter;
                taskObj.Department__c =  taskObjJsonData.Department;
                taskObj.Topic__c =  taskObjJsonData.Topic;
                taskObj.Work_Queue_View_Name__c = taskObjJsonData.workQueueName;
                taskObj.Callback_Requested__c = taskObjJsonData.CallbackRequested;
                taskObj.Time_Zone__c = taskObjJsonData.TimeZone;
                taskObj.Call_Back_End__c = taskObjJsonData.CallBackSlotEndTime;
                taskObj.Call_Back_Start__c = taskObjJsonData.CallBackSlotStartTime;
                if(taskObjJsonData.OwnerId != null && taskObjJsonData.OwnerId != '' ){
                    taskObj.OwnerId = taskObjJsonData.OwnerId;
                }
                taskObj.WhatId = taskObjJsonData.WhatId;
                if(taskObjJsonData.ActivityDate != null && taskObjJsonData.ActivityDate != ''){
                    taskObj.ActivityDate =  Date.valueOf(taskObjJsonData.ActivityDate);
                }
                taskObj.QueueOrUserId__c = taskObjJsonData.QueueOrUserId;
                taskObj.OwnerId = taskObjJsonData.OwnerId;
                taskObj.Credentialing_Task__c = taskObjJsonData.credentialingTask;
                taskObj.Task_Completion_Date__c = taskObjJsonData.taskCompletionDate;
                mapTaskOwnerFields(taskObj);
                insert taskObj;
                
                //insert comment
                if(taskObj.Description !=null && !String.isEmpty(taskObj.Description)){
                    Task_Field_History__c oTaskFH = new Task_Field_History__c ();                    
                    if(Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isUpdateable()) oTaskFH.SObjID__c = taskObj.Id;  
                    if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isUpdateable()) oTaskFH.SObjFieldName__c = GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM; 
                    if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isUpdateable()) oTaskFH.SObjFieldValue__c= taskObj.Description;    
                    if(Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isUpdateable()) oTaskFH.Created_At__c = System.now(); 
                    sObjectToInsert.add(oTaskFH);
                }
                //update task number               
                sObjectToInsert.add(updateTaskSubject(taskObj.id));
                upsert(sObjectToInsert);
                return taskObj.id;
            }
            return null;
        } catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CreateTask_C_LWC_HUM', 'createTask');            
            throw new AuraHandledException(ex.getMessage());          
        }  
    } 

    /*
    * Method Name : updateTaskSubject
    * Description : method used to update task subject
    * Return type : String
    * Parameter : taskId                                  
    */
    @AuraEnabled
    public static Task updateTaskSubject(String taskId)
    {
        String sParamTask = taskId;
        if(String.isNotBlank(sParamTask)){
            List<Task> taskList = [Select Task_Number__c, Subject from Task WHERE Id = :sParamTask order by createddate desc limit 1];
            if(taskList != null && taskList.size() > 0) {
                taskList[0].Subject = taskList[0].Task_Number__c;
                return taskList[0];
            }
        }
        return null;

    }

    /*
    * Method Name : editTask
    * Description : method used to edit task
    * Return type : String
    * Parameter : taskData                                  
    */
    @AuraEnabled
    public static String editTask(String taskData)
    {
        try{
            Map<String, Object> responseMap = (Map<String, Object>) JSON.deserializeUntyped(taskData);
            TaskWrapper taskObjJsonData = (TaskWrapper) JSON.deserialize(JSON.serialize(responseMap.get('taskFields')),TaskWrapper.class);
            getServicecenterDeptRelation();
            Task taskObj = new Task();
            taskObj.Id = taskObjJsonData.Id;
            taskObj.Priority = taskObjJsonData.Priority;
            taskObj.Status = taskObjJsonData.Status;
            taskObj.Type = taskObjJsonData.Type;
            taskObj.Description = taskObjJsonData.Description;            
            taskObj.Service_Center__c = taskObjJsonData.ServiceCenter;
            taskObj.Department__c =  taskObjJsonData.Department;
            taskObj.Topic__c =  taskObjJsonData.Topic;
            taskObj.Work_Queue_View_Name__c = taskObjJsonData.workQueueName;            
            taskObj.ActivityDate =  taskObjJsonData.ActivityDate != null ? Date.valueOf(taskObjJsonData.ActivityDate) : null;
            if(taskObjJsonData.OwnerId != null && taskObjJsonData.OwnerId != '' ){
                taskObj.OwnerId = taskObjJsonData.OwnerId; 
            }
            if(taskObjJsonData.WhatId != null && taskObjJsonData.WhatId != ''){
                taskObj.WhatId =  taskObjJsonData.WhatId;
            }
            if(taskObjJsonData.QueueOrUserId != null && taskObjJsonData.QueueOrUserId != ''){
                taskObj.QueueOrUserId__c = taskObjJsonData.QueueOrUserId;
            }
            taskObj.Time_Zone__c = taskObjJsonData.TimeZone;
            taskObj.Call_Back_Start__c = taskObjJsonData.CallBackSlotStartTime;
            taskObj.Call_Back_End__c = taskObjJsonData.CallBackSlotEndTime;
            taskObj.Callback_Requested__c = taskObjJsonData.CallbackRequested;
            taskObj.Credentialing_Task__c = taskObjJsonData.credentialingTask;
            taskObj.Task_Completion_Date__c = taskObjJsonData.taskCompletionDate;
            mapTaskOwnerFields(taskObj);
            List<SObject> sObjectToUpsert = new List<SObject>();
            if(taskObjJsonData.Description !=null && !String.isEmpty(taskObjJsonData.Description)){
                Task_Field_History__c oTaskFH = new Task_Field_History__c ();                
                if(Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isUpdateable()) oTaskFH.SObjID__c = taskObjJsonData.Id;  
                if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isUpdateable()) oTaskFH.SObjFieldName__c = GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM; 
                if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isUpdateable()) oTaskFH.SObjFieldValue__c= taskObjJsonData.Description;    
                if(Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isUpdateable()) oTaskFH.Created_At__c = System.now(); 
                sObjectToUpsert.add(oTaskFH);
            }            
            sObjectToUpsert.add(taskObj);
            upsert(sObjectToUpsert);
            return 'Success';
        } catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CreateTask_C_LWC_HUM', 'editTask');            
            throw new AuraHandledException(ex.getMessage());           
        } 
    }

    /*
    * Method Name : fetchTaskDetail
    * Description : method used to fetch task detail
    * Return type : String
    * Parameter : taskId, action                                  
    */
    @AuraEnabled
    public static Map<String,Object> fetchTaskDetail(String taskId,String action)
    {
        try {
            Task objTask = null;  
            Map<String,Object> taskDetailMap = new Map<String,Object>();
            String sParamTask = taskId;
            if(string.isNotBlank(sParamTask)) {
                //check if medicare id there
                String hasMedicareId  = getMedicareId(sParamTask);
                if(hasMedicareId != null)
                    taskDetailMap.put('Medicare', hasMedicareId);

                objTask = [select Work_Queue_View_Name__c,Last_Modified_Age__c ,Re_Open_Task_Age__c,CreatedDate,Created_DateTime__c,Date_Time_Reopned__c,Date_Time_Closed__c,CreatedBy.Name ,Created_By_Queue__c,LastModifiedBy.Name ,LastModifiedby_Queue__c,Service_Center__c,QueueOrUserId__c, Department__c, RecordTypeId, Owner_Queue__C, Topic__C,OwnerId, Task_Number__c,Task_Owner__C, WhatId,What.Name, Status, Type, Priority, ActivityDate, Description, Callback_Requested__c , Time_Zone__c, Call_Back_End__c,Call_Back_Start__c,Credentialing_Task__c, Task_Completion_Date__c  from Task where Id =:sParamTask];           
                taskDetailMap.put('Task',objTask);

                if(objTask.QueueOrUserId__c !=null && Id.valueOf(objTask.QueueOrUserId__c).getSobjectType() == Schema.User.sObjectType){
                    User usr = [SELECT id,name from User where Id =: objTask.QueueOrUserId__c];
                    if(usr !=null)
                        taskDetailMap.put('User',new Map<String, String>{'label' => usr.name, 'value' => usr.id});  
                }
                String sTaskOwnerValue = populateQueueName(objTask.Service_Center__c, objTask.Department__c);

                if(sTaskOwnerValue != null){
                    taskDetailMap.put('Ownerqueue',sTaskOwnerValue);
                }
                //get list of service center
                taskDetailMap.put('lstServiceCenter', getLstServiceCenter(sParamTask));
                //get  dept for service daprt
                if(taskDetailMap.containsKey('lstServiceCenter')){
                    taskDetailMap.put('lstDepartment', getDepartments(objTask.Service_Center__c, sParamTask));
                    if(taskDetailMap.containsKey('lstDepartment')){
                        taskDetailMap.put('topic', getTopics(objTask.Service_Center__c, objTask.Department__c));
                    }
                }
                //get topic for dept
                if(action == 'LeftPanel'){
                    List<HUM_Task_Heirarchy__c> lstparentTaskID = [select HUM_Parent_TaskID__c FROM HUM_Task_Heirarchy__c where (HUM_Dependent_TaskID__c 
                                                    =:taskId AND  HUM_Parent_TaskID__c != null) limit 1];  
                    String sParentId;
                    if( lstparentTaskID != null && lstparentTaskID.size() > 0) {
                        sParentId = lstparentTaskID[0].HUM_Parent_TaskID__c; 
                        List<Task> parentTaskNumber = [select Task_Number__c From Task where Id = :sParentId limit 1];
                        if(parentTaskNumber != null && parentTaskNumber.size() > 0) {
                            taskDetailMap.put('ParentTaskNumber',parentTaskNumber[0].Task_Number__c);
                        }
                    }
                    taskDetailMap.put('ParentTaskId',sParentId);
                }
            }
            return taskDetailMap;
        } catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CreateTask_C_LWC_HUM', 'fetchTaskDetail');            
            throw new AuraHandledException(ex.getMessage());           
        }      
    }


    @AuraEnabled
    public static void mapTaskOwnerFields(Task oTask){        
        sUserName = oTask.QueueOrUserId__c;
        sUserID = oTask.OwnerId;
        String sTaskOwnerValue = ''; 
        String sGrpName = '';

        if(!String.isBlank(oTask.Department__c) && !oTask.Department__c.equals(GLOBAL_CONSTANT_HUM.NONE_HUM) && mapServCenDeptWorkQueueSetup.containsKey(oTask.Service_Center__c + '#' + oTask.Department__c)){            sTaskOwnerValue = populateQueueName(oTask.Service_Center__c, oTask.Department__c);
            sGrpName = sTaskOwnerValue.replace('_',' ');
        }
        
        if( !oTask.Service_Center__c.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) && (string.isBlank(sUserName) || sUserName == null)) { 
            //if sUser is blank
            Group grp = [Select Id, Name from Group Where Name =: sGrpName LIMIT 1]; 
            if(grp != Null) {
                if(Schema.SObjectType.Task.fields.Owner_Queue__c.isCreateable() || Schema.SObjectType.Task.fields.Owner_Queue__c.isUpdateable()) oTask.Owner_Queue__c = grp.Name; 
                if(Schema.SObjectType.Task.fields.QueueOrUserId__c.isCreateable() || Schema.SObjectType.Task.fields.QueueOrUserId__c.isUpdateable()) oTask.QueueOrUserId__c = grp.Id; 
            } else {
                throw new HUMCustomException(System.Label.TASKEDIT_QUEUENOTEXISTS_HUM);
            }
            
            if(Schema.SObjectType.Task.fields.Task_Owner__c.isCreateable() || Schema.SObjectType.Task.fields.Task_Owner__c.isUpdateable()) oTask.Task_Owner__c = sTaskOwnerValue.replace('_',' ');               
            if(Schema.SObjectType.Task.fields.OwnerID.isCreateable() || Schema.SObjectType.Task.fields.OwnerID.isUpdateable())oTask.OwnerID = Userinfo.getUserId(); 
        } else if(!oTask.Service_Center__c.containsIgnoreCase(System.label.TASKEDIT_NONE_HUM) && !String.isBlank(sUserName)){
            //if sUser is there
            if(Schema.SObjectType.Task.fields.Owner_Queue__c.isCreateable() || Schema.SObjectType.Task.fields.Owner_Queue__c.isUpdateable())oTask.Owner_Queue__c = sGrpName; 
            if(Schema.SObjectType.Task.fields.Task_Owner__c.isCreateable() || Schema.SObjectType.Task.fields.Task_Owner__c.isUpdateable())oTask.Task_Owner__c = sUserName; 
           
            if(sUserID != NULL && !String.isBlank(sUserID) ){
                if(Schema.SObjectType.Task.fields.OwnerID.isCreateable() || Schema.SObjectType.Task.fields.OwnerID.isUpdateable())oTask.OwnerID = sUserID; 
                if(Schema.SObjectType.Task.fields.QueueOrUserId__c.isCreateable() || Schema.SObjectType.Task.fields.QueueOrUserId__c.isUpdateable())oTask.QueueOrUserId__c = sUserID;  
            }
        }
    }

    @TestVisible
    private class TaskWrapper{
        public String Priority;
        public String CallbackRequested;
        public String TimeZone;
        public Time CallBackSlotEndTime;
        public Time CallBackSlotStartTime;
        public String Status;
        public String Type;
        public String Description;
        public String ActivityDate;
        public String OwnerId;
        public String WhatId;
        public String ServiceCenter;
        public String Department;
        public String Topic;    
        public String Id;    
        public String workQueueName;
        public String QueueOrUserId;
        public String credentialingTask;
        public Date taskCompletionDate;
    }

    @TestVisible
    private class TopicANdUserLookupWrapper{
        @AuraEnabled
        public List<String> topicList;
        @AuraEnabled
        public String workQueue;
        
    }

    /*
        * Method Name : credentialingEligible
        * Description : Check if the case is Credentialing Eligible or not
        * Return type : boolean
        * Parameter   : String caseId
    */  
    @AuraEnabled 
    public static Boolean credentialingEligible(Id Id,String objType){
        boolean isEligible = false;
        if(HUMUtilityHelper.isCRMFunctionalityON('4412371')){
        Id caseId;
        if(objType == 'Task'){
            List<Task> lstTask = [select WhatId from Task where Id=:Id];
            if(!lstTask.isEmpty()){
                caseId = lstTask[0].WhatId;
            }
        }else{
            caseId = Id;
        }
        List<Case> lstCase = [select id,Owner_Queue__c,Classification_Id__r.name , Intent_Id__r.name from Case Where Id=:caseId];
        if(!lstCase.isEmpty()){
            if(lstCase[0].Owner_Queue__c == CaseEditConstants_HUM.MCD_OWNER_Q && 
                lstCase[0].Classification_Id__r.name == CaseEditConstants_HUM.MCD_Classification && 
                lstCase[0].Intent_Id__r.name == CaseEditConstants_HUM.MCD_Intent){
                isEligible = true;
            }
        }
        }
        return isEligible;
    }

    /*
        * Method Name : getCredentialingTaskPicklist
        * Description : get Credentialing Task Picklist
        * Return type : object
        * Parameter   : NA
    */  
    @AuraEnabled(cacheable=true) 
    public static List<classificationIntentModel.OptionModel> getCredentialingTaskPicklist(){
        List<classificationIntentModel.OptionModel> credentialingTaskPicklist = new List<classificationIntentModel.OptionModel>();
		Schema.DescribeFieldResult fieldResult = Task.Credentialing_Task__c.getDescribe();
		List<Schema.PicklistEntry> ple = fieldResult.getPicklistValues();
        credentialingTaskPicklist.add(new classificationIntentModel.OptionModel('--None--',''));
        for( Schema.PicklistEntry pickListVal : ple){
			credentialingTaskPicklist.add(new classificationIntentModel.OptionModel(pickListVal.getLabel(),pickListVal.getLabel()));
		}     
		return credentialingTaskPicklist;
    }
    /*
        * Method Name : getTaskNumber
        * Description : get  TaskNumber form give task id
        * Return type : String
        * Parameter   : Id task id
    */ 
    @AuraEnabled
    public static String getTaskNumber(string taskID)
    {
        Task sTaskRec;
        String taskNumber;
       sTaskRec = [select Id,Task_Number__c,Status from Task where Id =:taskID limit 1];
         taskNumber=sTaskRec.Task_Number__c;
         return taskNumber;  
        
    }
}