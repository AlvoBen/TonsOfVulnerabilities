/************************************************************************************************************************************************
  Apex Class Name  : METEnrollment_LH_HUM
  Version          : 1.0
  Function         : Helper Class for MET Enrollment section on case edit page. 
  Test Class       : METEnrollment_LT_HUM
  *************************************************************************************************************************************************
  Modification Log:
  *Developer Name             Review Number               Date                       Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  *Pooja Kumbhar                                       12/22/2022                  Original Version : 
 ******************************************************************************************************************************************************************************/

public with sharing class METEnrollment_LH_HUM {
    
    /**
    * Method is used to get List of Tasks of Record Type MET Task related to Case Record 
    * @param  caseRecordID
    * @return METTaskList_LDTO_HUM
    */
    public static METTaskList_LDTO_HUM getTaskListDTO(String caseRecordId) {
        List<Task> tasklst = getTasksOfCase(caseRecordId);
        METTaskList_LDTO_HUM objMETTaskList = new METTaskList_LDTO_HUM(tasklst);
        return objMETTaskList;
    }
    
    /*
    * This method helps in fetching Task records of Record Type MET Task  related to the current Case Record
    * @param  caseRecord
    * @return List<Task>
    */
    public static List<Task> getTasksOfCase(String caseRecordId) {
        String metTaskType = HumConstants_LWC.MET_TASK_RECORDTYPE_HUM;
        List<Task> lstMETTask = [Select id,createdby.name,createdby.id,createddate,lastmodifieddate, Lastmodifiedby.id, 
                                  Lastmodifiedby.name,Lastmodifiedby.firstName ,Lastmodifiedby.LastName,
                                  MET_CancelledByUser__r.name,MET_CancelledByUser__r.id, MET_CancelledBydate__c,
                                  MET_Task__r.Name, MET_Action__r.Name, MET_Source__r.Name, Status, CreatedDate_Sort__c  
                                  from Task where Whatid = :caseRecordId And RecordType.DeveloperName =: metTaskType order by 
                                  createddate asc];
        if(lstMETTask.isEmpty()) {
            lstMETTask = new list<Task>();
        }
        return lstMETTask;
    }
    /*
    * This method helps in fetching MET Task records related to the current Case Subtype
    * @param  caseType
    * @param  caseSubType
    * @return List<MET_Task__c>
    */
    public static List<MET_Task__c> getMETTasks(String caseType, String caseSubType) {
        List<MET_Task__c> lstMETTask = [Select id, Name from MET_Task__c where Id in (Select Task__c from MET_Milestone__c where Case_Type__c =: caseType And Case_Subtype__c =: caseSubType And Is_Active__c = true) And Is_Active__c = true order by Name];
        return lstMETTask;
    }
    /*
    * This method helps in fetching MET Action records related to the current MET Task
    * @param  caseType
    * @param  caseSubType
    * @param  metTaskName
    * @return List<MET_Action__c>
    */
    public static List<MET_Action__c> getMETActions(String caseType, String caseSubType,String metTaskName) {
        List<MET_Action__c> metActionlst = [select Id, Name from MET_Action__c where Id in (Select Action__c from MET_Milestone__c where Case_Type__c =: caseType And Case_Subtype__c =: caseSubType And Task__c =: metTaskName And Is_Active__c = true) And Is_Active__c = true order by Name];
        return metActionlst;
    }
    /*
    * This method helps in fetching MET Source records related to the current MET Action
    * @param  caseType
    * @param  caseSubType
    * @param  metTaskName
    * @param  metAction
    * @return List<MET_Source__c>
    */
    public static List<MET_Source__c> getMETSources(String caseType, String caseSubType,String metTaskName, String metAction) {
        List<MET_Source__c> metSourcelst = [select Id, Name, Launch_EMME__c from MET_Source__c where Id in (select Source_Name__c from MET_Milestone__c where Case_Type__c =: caseType And Case_Subtype__c =: caseSubType And Task__c =: metTaskName And Action__c =:metAction And Is_Active__c = true ) And Is_Active__c = true order by Name] ;
        return metSourcelst;
    }
    
    /*
    * This method is used to Create Tasks and Update Tasks in MET Enrollment Section
    * @param  METTaskList_LDTO_HUM, Case
    * @return NA
    */
    public static List<Task> createTasks(METTaskList_LDTO_HUM metTaskListDTOInstance, Case caserecord) {
        List<Task> tasklst = new List<Task>();
        String metTaskType = HumConstants_LWC.METTASKTYPE;
        String cancelledMETStatus = HumConstants_LWC.MEDICARE_STATUS_CANCELLED_HUM;
        Task taskInst;
        Id metTaskRecordTypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get(metTaskType).getRecordTypeId();
        for(METTaskList_LDTO_HUM.METTask_LDTO_HUM metTaskDTOInst: metTaskListDTOInstance.listMETTaskDTO) {
            taskInst = new Task();
            if(String.isBlank(metTaskDTOInst.sTaskId) && String.isBlank(metTaskDTOInst.sTask) && String.isBlank(metTaskDTOInst.sAction) && String.isBlank(metTaskDTOInst.sSource)) {
                continue;
            }
            else if((metTaskDTOInst.sTaskId == null || String.isBlank(metTaskDTOInst.sTaskId))) {
            if(schema.SobjectType.Task.fields.RecordTypeId.isCreateable())
                taskInst.RecordTypeId = metTaskRecordTypeId;
                if(!String.isBlank(metTaskDTOInst.sTask)) {
                  if(schema.SobjectType.task.fields.MET_Task__c.isCreateable())
                    taskInst.MET_Task__c = metTaskDTOInst.sTask;
                }
                if(!String.isBlank(metTaskDTOInst.sAction)) {
                 if(schema.SobjectType.task.fields.MET_Action__c.isCreateable()) 
                    taskInst.MET_Action__c = metTaskDTOInst.sAction;
                }
                if(!String.isBlank(metTaskDTOInst.sSource)) {
                  if(schema.SobjectType.task.fields.MET_Source__c.isCreateable()) 
                    taskInst.MET_Source__c = metTaskDTOInst.sSource;
                }
                if(!String.isBlank(metTaskDTOInst.sStatus)) {
                   if(schema.SobjectType.task.fields.Status.isCreateable())
                    taskInst.Status = metTaskDTOInst.sStatus;
                }
                if(String.isNotBlank(metTaskDTOInst.sCreatedDate_Sort)) 
                {
                   if(schema.SobjectType.task.fields.CreatedDate_Sort__c.isCreateable())
                    taskInst.CreatedDate_Sort__c = metTaskDTOInst.sCreatedDate_Sort;   
                }
               if(schema.SobjectType.task.fields.Id.isAccessible())
                taskInst.Id = null;
            }
            if(metTaskDTOInst.sTaskId != null) {
                taskInst.Id = metTaskDTOInst.sTaskId;
            }
      if(metTaskDTOInst.sStatus == cancelledMETStatus)
            {
                taskInst.Status = metTaskDTOInst.sStatus;
               if(schema.SobjectType.task.fields.MET_CancelledByUser__c.isCreateable())
                taskInst.MET_CancelledByUser__c = UserInfo.getUserID();
               if(schema.SobjectType.task.fields.MET_CancelledBydate__c.isCreateable())
                taskInst.MET_CancelledBydate__c = System.Now();
            }
      if(schema.SobjectType.task.fields.WhatId.isCreateable())
            taskInst.WhatId = caserecord.Id;
         if(schema.SobjectType.task.fields.Type.isCreateable())
            taskInst.Type = HumConstants_LWC.METTASKTYPE;
            tasklst.add(taskInst);
            
        }
        return tasklst;
    }   
    
    /*
    * This method helps in fetching  "MET Entries Scenario" field options recordsfrom MET_Milestone__c object related to the current Case type and Subtype
    * @param  sCaseType
    * @param  sCaseSubType
    * @return List<String>
    */
    public static List<String> getMultipleMETEntries(String sCaseType, String sCaseSubType) {
        List<String> lstValue = new List<String>();
        
        Set<String> setValue = new Set<String>();
        for(MET_Milestone__c oMETMilestone : [select id, met_entries_assoc__c from MET_Milestone__c 
                                where Is_Active__c = true and met_entries_assoc__c != '' and Case_Type__c =: sCaseType AND Case_Subtype__c =: sCaseSubType]) {
            String sTempValue = oMETMilestone.met_entries_assoc__c;
            if(String.isNotBlank(sTempValue) && sTempValue.indexOf(';') != -1) {
                setValue.addAll(sTempValue.split(';'));
            } else {
                setValue.add(sTempValue);
            }
        }

        if(!setValue.isEmpty()) {
            lstValue.addAll(setValue);
            lstValue.sort();
        }

        return lstValue;    
    }
    
    /*
    * This method helps in fetching multiple Milestones records related to drop down selection and Case.Type & Case.SubType
    * @param sCaseType
    * @param sCaseSubType
    * @param sSelectedValue
    * @return List 
    */    
    public static List<MultipleMETTask_LDTO_HUM> getMultipleMETTasks(String sCaseType, String sCaseSubType, String sSelectedValue) {
        
        List<MultipleMETTask_LDTO_HUM> lstMETMilestone = new List<MultipleMETTask_LDTO_HUM>(); 
        List<MET_Milestone__c > lstMET = [select Task__c,Met_Entries_Assoc_Order__c, Task__r.name, Action__c, Action__r.name, Source_Name__c, Source_Name__r.name,Source_Name__r.Launch_EMME__c from MET_Milestone__c 
                        WHERE Is_Active__c = true And Case_Type__c =: sCaseType And Case_Subtype__c =: sCaseSubType And met_entries_assoc__c includes (:sSelectedValue)];
    
        if(null != lstMET && !lstMET.isEmpty()) {
            for(MET_Milestone__c oMETMilestone: lstMET){
                oMETMilestone.Met_Entries_Assoc_Order__c = determineOrder(oMETMilestone.Met_Entries_Assoc_Order__c,sSelectedValue);
                lstMETMilestone.add(new MultipleMETTask_LDTO_HUM(oMETMilestone));
            }
        }
        
        return lstMETMilestone;
    }
  
  
    /*
    * This method helps in get the Integer Value for the Order on this records
    * @param sOrderValues
    * @param checkingAgainst
    
    * @return String 
    */    
    public static String determineOrder(String sOrderValues, String checkingAgainst) {
  
    String sOrderValue = '0';
    
    try{
      if (null != sOrderValues && sOrderValues.contains(';')  && String.isNotBlank(checkingAgainst)) {
        List<String> lstOrderValues = sOrderValues.split(';');

        for(String checkString : lstOrderValues){
          
          if(checkString.contains(checkingAgainst)) {
            sOrderValue = checkString.substringAfterLast(checkingAgainst);
          }
        }
      } else if (null != sOrderValues  && String.isNotBlank(checkingAgainst) && sOrderValues.contains(checkingAgainst)) {
        sOrderValue = sOrderValues.substringAfterLast(checkingAgainst);
      }
      
      Integer checkValue = Integer.valueof(sOrderValue);
      sOrderValue = String.valueOf(checkValue);
      
    } catch (Exception e) {
      sOrderValue = '0';
    }
    
    return sOrderValue;
  }
 
}