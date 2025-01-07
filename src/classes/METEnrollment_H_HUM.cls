/************************************************************************************************************************************************
  Apex Class Name  : METEnrollment_H_HUM
  Version          : 1.0
  Created Date     : June 13 2017
  Function         : Helper Class for Medicare Cases. 
  Test Class       : 
  *************************************************************************************************************************************************

  Modification Log:
  * Modification Id           Developer Name             Review Number               Date                       Description
  *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  * 1.0                       Prasad Jandhyala              114623                  06/13/2017                  Original Version : Modified for REQ - 308348 : MET Enrollment Section.
  * 1.1                       Prasad Jandhyala              118596                  08/10/2017                  Fix for 307280 : Alphabetical order For Task, Action, Source fields.
  * 1.2                       Prasad Jandhyala              119502                  08/14/2017                  Fix for 309120 : Is Active check on Task , Action  And Source objects.
  * 1.3                       Shruthi Karanth               122058                  08/23/2017                  Fix for Close Case issue to pre-populate Type field on MET Tasks to 'MET Task'
  * 1.4                       Mohammed Noor                 142298                  10/26/2017                  REQ - 335062 - CR 5317 Custom Policy Member Search & MET Section Save Order.
  * 1.5                       Manoj Chaurasia               202594                  03/29/2018                  REQ - 354470 Ability to display TAS combination in CRM
  * 1.6	   					Syed Mubarak											07/26/2018					REQ - 367878 MET Section CA ticket fix
  * 1.7                       Prajakta Deshpande                                    08/08/2018                  REQ 353087 - Task Action Source Inactive entry (CA - 7619235)
  * 1.8                       Samantha Tennyson                                     08/25/2018                  User Story 1318943: PR00091574 - MF 1 - AHT - Ability to create "Quick MET Entries" from MET Enrollment section on Case Edit page (RF)
  * 1.9                       Amar Gadewar                                          09/15/2020                  Bug 1501772: DF-1628 User Story: 1318943 - EMME application is not launched when MET Entry created upon selecting a value in "MET Entries Association" has "Launce EMME" checkbox checked at Source object record level.
  * 2.0						  Jasmeen Shangari                                      02/22/2021                  CheckMarx fix -update class with sharing																				 
  * 2.1                       Mithra Bharadwaj                                                                  US#2163290-Case Management Checkmarx Security violations - Case Edit 
  ******************************************************************************************************************************************************************************/

public with sharing class METEnrollment_H_HUM {
    
    /**
    * Method is used to identify if the logged in user is a Medicare user. 
    * @param  NA
    * @return Boolean
    */
    public static Boolean isMedicareUser()
    {
        Boolean bIsMedicareUser = false;
        List<String> metMedicarePS = new List<String>{GLOBAL_CONSTANT_HUM.METENROLLMENTMEDICAREEDIT_PERMISSIONSET_HUM};
        List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name IN : metMedicarePS];
        if (NULL != lstPermissionSet && lstPermissionSet.size() > 0)
        {
            bIsMedicareUser = true;
        }
        return bIsMedicareUser;
    }
    /**
    * Method is used to get List of Tasks of Record Type MET Task related to Case Record 
    * @param  caseRecordID
    * @return METTaskList_DTO_HUM
    */
    public static METTaskList_DTO_HUM getTaskListDTO(String caseRecordId) {
        List<Task> taskList = getTasksOfCase(caseRecordId);
        METTaskList_DTO_HUM objMETTaskList = new METTaskList_DTO_HUM(taskList);
        return objMETTaskList;
    }
    
    /*
    * This method helps in fetching Task records of Record Type MET Task  related to the current Case Record
    * @param  caseRecord
    * @return List<Task>
    */
    public static List<Task> getTasksOfCase(String caseRecordId) {
        String metTaskType = GLOBAL_CONSTANT_HUM.MET_TASK_RECORDTYPE_HUM;
        List<Task> listMetTask = [Select id,createdby.name,createdby.id,createddate,lastmodifieddate, Lastmodifiedby.id, 
                                  Lastmodifiedby.name,Lastmodifiedby.firstName ,Lastmodifiedby.LastName,
                                  MET_CancelledByUser__r.name,MET_CancelledByUser__r.id, MET_CancelledBydate__c,
                                  MET_Task__r.Name, MET_Action__r.Name, MET_Source__r.Name, Status, CreatedDate_Sort__c  
                                  from Task where Whatid = :caseRecordId And RecordType.DeveloperName =: metTaskType order by 
                                  createddate asc];
        if(listMetTask.isEmpty()) {
            listMetTask = new list<Task>();
        }
        return listMetTask;
    }
    /*
    * This method helps in fetching MET Task records related to the current Case Subtype
    * @param  caseType
    * @param  caseSubType
    * @return List<MET_Task__c>
    */
    public static List<MET_Task__c> getMETTasks(String caseType, String caseSubType) {
        List<MET_Task__c> metTaskList = [Select id, Name from MET_Task__c where Id in (Select Task__c from MET_Milestone__c where Case_Type__c =: caseType And Case_Subtype__c =: caseSubType And Is_Active__c = true) And Is_Active__c = true order by Name];
        return metTaskList;
    }
    /*
    * This method helps in fetching MET Action records related to the current MET Task
    * @param  caseType
    * @param  caseSubType
    * @param  metTaskName
    * @return List<MET_Action__c>
    */
    public static List<MET_Action__c> getMETActions(String caseType, String caseSubType,String metTaskName) {
        List<MET_Action__c> metActionList = [select Id, Name from MET_Action__c where Id in (Select Action__c from MET_Milestone__c where Case_Type__c =: caseType And Case_Subtype__c =: caseSubType And Task__c =: metTaskName And Is_Active__c = true) And Is_Active__c = true order by Name];
        return metActionList;
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
        List<MET_Source__c> metSourceList = [select Id, Name, Launch_EMME__c from MET_Source__c where Id in (select Source_Name__c from MET_Milestone__c where Case_Type__c =: caseType And Case_Subtype__c =: caseSubType And Task__c =: metTaskName And Action__c =:metAction And Is_Active__c = true ) And Is_Active__c = true order by Name] ;
        return metSourceList;
    }
    
    /*
    * This method is used to Create Tasks and Update Tasks in MET Enrollment Section
    * @param  METTaskList_DTO_HUM, Case
    * @return NA
    */
    public static List<Task> createTasks(METTaskList_DTO_HUM metTaskListDTOInstance, Case caserecord) {
        List<Task> taskList = new List<Task>();
        String metTaskType = GLOBAL_CONSTANT_HUM.METTASKTYPE;
        String cancelledMETStatus = GLOBAL_CONSTANT_HUM.MEDICARE_STATUS_CANCELLED_HUM;
        Task taskInst;
        Id metTaskRecordTypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get(metTaskType).getRecordTypeId();
        for(METTaskList_DTO_HUM.METTask_DTO_HUM metTaskDTOInst: metTaskListDTOInstance.listMETTaskDTO) {
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
            taskInst.Type = GLOBAL_CONSTANT_HUM.METTASKTYPE;
            taskList.add(taskInst);
            
        }
        return taskList;
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
    public static List<MultipleMETTask_DTO_AHT_HUM> getMultipleMETTasks(String sCaseType, String sCaseSubType, String sSelectedValue) {
        
        List<MultipleMETTask_DTO_AHT_HUM> lstMETMilestone = new List<MultipleMETTask_DTO_AHT_HUM>(); 
        List<MET_Milestone__c > lstMET = [select Task__c,Met_Entries_Assoc_Order__c, Task__r.name, Action__c, Action__r.name, Source_Name__c, Source_Name__r.name,Source_Name__r.Launch_EMME__c from MET_Milestone__c 
                        WHERE Is_Active__c = true And Case_Type__c =: sCaseType And Case_Subtype__c =: sCaseSubType And met_entries_assoc__c includes (:sSelectedValue)];
		
        if(null != lstMET && !lstMET.isEmpty()) {
            for(MET_Milestone__c oMETMilestone: lstMET){
                oMETMilestone.Met_Entries_Assoc_Order__c = determineOrder(oMETMilestone.Met_Entries_Assoc_Order__c,sSelectedValue);
                lstMETMilestone.add(new MultipleMETTask_DTO_AHT_HUM(oMETMilestone));
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

	
	/**
     * DTO class to pass multiple Milestone details in vf page
     */
    public class MultipleMETTask_DTO_AHT_HUM implements Comparable {
        public String sTaskId{get; set;}
        public String sTaskName{get; set;}
        public String sActionId{get; set;}
        public String sActionName{get; set;}
        public String sSourceId{get; set;}
        public String sSourceName{get; set;}
        public Boolean bLaunchEMME {get; set;}
		public Integer iOrder {get; set;}

        public MultipleMETTask_DTO_AHT_HUM(MET_Milestone__c oMETMilestone) {
            this.sTaskId = oMETMilestone.Task__c;
            this.sTaskName = oMETMilestone.Task__r.name;
            this.sActionId = oMETMilestone.Action__c;
            this.sActionName = oMETMilestone.Action__r.name;
            this.sSourceId = oMETMilestone.Source_Name__c;
            this.sSourceName = oMETMilestone.Source_Name__r.name;
            this.iOrder = Integer.valueof(oMETMilestone.Met_Entries_Assoc_Order__c);
            this.bLaunchEMME = oMETMilestone.Source_Name__r.Launch_EMME__c;
        }
		
		
		public Integer compareTo(Object compareTo) {
			MultipleMETTask_DTO_AHT_HUM compareToMileStone = (MultipleMETTask_DTO_AHT_HUM)compareTo;
			if (this.iOrder == compareToMileStone.iOrder) return 0;
			if (this.iOrder > compareToMileStone.iOrder) return 1;
			return -1;
    
		}
    }
}