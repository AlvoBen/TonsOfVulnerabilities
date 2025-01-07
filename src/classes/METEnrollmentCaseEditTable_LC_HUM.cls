/*******************************************************************************************************************************************************
Apex Class Name  : METEnrollmentCaseEditTable_LC_HUM
Version          : 1.0
Function         : This class Is controller for LWC component METEnrollmentEditHum
Test class       : METEnrollment_LT_HUM
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Pooja Kumbhar                             	  12/22/2022                  Original version
* Pooja Kumbhar                             	  08/25/2023                  US - 4957122 - AHTR - SF Security Remediation - Case Lgt - METEnrollmentCaseEditTable
************************************************************************************************************************************************/

public with sharing class METEnrollmentCaseEditTable_LC_HUM {

     public static Map<String, String> sourceIdLaunchEMMEMap = new Map<String, String>();
     public static string checkLaunchEMME = '';
     public static string launchEmmeURL = '';

    /*
    Method Name - getCaseRecordsData
    parameter - case record id
    Return - object - returns case object to get few case related data.
    */ 
    @AuraEnabled
    public static object getCaseRecordsData(String sCaseRecordId)
    {
        case CaseRec = [select id,Origin,Type,Subtype__c,Medicare_Track_ID__c,caseNumber from case where id=: sCaseRecordId limit 1];     
        return JSON.serialize(CaseRec);
    }

    /*
    Method Name - getlaunchEmmeURL
    parameter - list of tasks in form of string and case record id
    Return - object - returns launchEmmeURL to open it in sub tab once case save
    */
    @AuraEnabled
    public static string getlaunchEmmeURL(string lstTask,String sCaseRecordId)
    {
        case CaseRec = [select id,Policy_Member__c,Policy_Member__r.Group_Number__c,Interacting_About_Type__c,Policy_Member__r.Policy_Platform__c,Medicare_Track_ID__c,caseNumber from case where id=: sCaseRecordId limit 1];
        METTaskList_LDTO_HUM metTaskWrapperListInstance = (METTaskList_LDTO_HUM)JSON.deserialize(lstTask , METTaskList_LDTO_HUM.Class);
        for(METTaskList_LDTO_HUM.METTask_LDTO_HUM metTaskDTOInst: metTaskWrapperListInstance.listMETTaskDTO) 
        {
            if((metTaskDTOInst.sTaskId == null || String.isBlank(metTaskDTOInst.sTaskId)) && !String.isBlank(metTaskDTOInst.sSource) && metTaskDTOInst.isSourceEMMEMap == 'true') {
                 sourceIdLaunchEMMEMap.put(metTaskDTOInst.sSource,metTaskDTOInst.isSourceEMMEMap);
            }
        }
        List<Task> taskLst = METEnrollment_LH_HUM.createTasks(metTaskWrapperListInstance, CaseRec);
        if(taskLst.size()>0)
        {
            Member_ID__c memberNumber;
            if(HUMUtilityHelper.isCRMFunctionalityON('4957122')){
                if (CaseRec.Policy_Member__c != null && String.isNotBlank(CaseRec.Policy_Member__c))
                {
                    memberNumber = [SELECT Name, Member_Dependent_Code__c  FROM Member_ID__c WHERE Policy_Member__c = :caseRec.policy_member__c AND Type__c = : CaseEditConstants_HUM.HUMMEMBER_ID_TYPE LIMIT 1];  
                }
            }
            
            for (Task objTask : taskLst) {
            if (objTask.Id == null && sourceIdLaunchEMMEMap != null && sourceIdLaunchEMMEMap.containsKey(objTask.MET_Source__c)) {
                            checkLaunchEMME = sourceIdLaunchEMMEMap.get(objTask.MET_Source__c);
                            
                            String URLValue;
                            if (checkLaunchEMME == 'true')
                            {
                                if(!HUMUtilityHelper.isCRMFunctionalityON('4957122')){
                                    if (CaseRec.Policy_Member__c != null && String.isNotBlank(CaseRec.Policy_Member__c))
                                    {
                                            memberNumber = [SELECT Name, Member_Dependent_Code__c  FROM Member_ID__c WHERE Policy_Member__c = :caseRec.policy_member__c AND Type__c = : CaseEditConstants_HUM.HUMMEMBER_ID_TYPE LIMIT 1];  
                                    }
                                }
                               URLValue = MET_Enrollment_Launch_EMME_URL__c.getInstance('URL').Launch_EMME_URL__c;
                                List<String> urlValues = URLValue.split('\\?');
                                URLValue = urlValues[0] + '?';
                                Integer i = 0;
                                Boolean isMember = ((CaseRec.Policy_Member__c != null && CaseRec.Interacting_About_Type__c == CaseEditConstants_HUM.MEMBER) ? true : false);
                                for (String urlVal : urlValues[1].split('\\&'))
                                {
                                    if (memberNumber != null)
                                    {
                                        urlVal = urlVal.replace('V1', (String.isNotBlank(memberNumber.Member_Dependent_Code__c) && isMember) ? memberNumber.Member_Dependent_Code__c : '');
                                        urlVal = urlVal.replace('V2', (String.isnotblank(memberNumber.Name) && isMember) ? membernumber.Name : '');
                                        
                                    }
                                    else
                                    {
                                        urlVal = urlVal.replace('V1', '');
                                        urlVal = urlVal.replace('V2', '');
                                    }
                                    urlVal = urlVal.replace('V3', (string.IsNotBlank(CaseRec.Policy_Member__r.Group_Number__c) && isMember) ? CaseRec.Policy_Member__r.Group_Number__c : '');
                                    urlVal = urlVal.replace('V4', (String.IsnotBlank(CaseRec.Policy_Member__r.Policy_Platform__c) && isMember) ? CaseRec.Policy_Member__r.Policy_Platform__c : '');
                                    urlVal = urlVal.replace('V5', (String.isNotBlank(CaseRec.Medicare_Track_ID__c)) ? CaseRec.Medicare_Track_ID__c : '');
                                    urlVal = urlVal.replace('V6', (String.isNotBlank(CaseRec.CaseNumber)) ? CaseRec.CaseNumber : '');
                                    if (i != 0)
                                        URLValue = URLValue + '&' + urlVal;
                                    else
                                        URLValue = URLValue + urlVal;

                                    i++;
                                }
                                String ssoURL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;
                                launchEmmeURL = ssoURL + '?HIDDENTARGET=' + URLValue;
                                break;
                            }
                        }
                    }
                   
        }
        return launchEmmeURL; 
    }
    
    
     /*
    * This method is used to populate JSON for rendering MET Tasks as DataTable in MET Enrollment Section
    * @param  caseRecord
    * @return object
    */
    @AuraEnabled
    public static object getMETRecords(String sCaseRecordId)
    {
        METTaskList_LDTO_HUM objMETTaskLst = METEnrollment_LH_HUM.getTaskListDTO(sCaseRecordId);
           
        return JSON.serialize(objMETTaskLst.listMETTaskDTO);
        
    }

    /*
    * This method helps in fetching  "MET Entries Scenario" field options recordsfrom MET_Milestone__c object related to the current Case type and Subtype
    * @param  sCaseType
    * @param  sCaseSubType
    * @return List<String>
    */
    @AuraEnabled
    public static List<String> getMultipleMETEntries(String sCaseType, String sCaseSubType) {
        List<String> lstValue = METEnrollment_LH_HUM.getMultipleMETEntries(sCaseType,sCaseSubType);

        return lstValue;    
    }
    
    public static Map<id,string> METTaskJSON = new Map<id,string>();
    public static Map<id,string> METActionJSON = new Map<id,string>();
    public static Map<id,Map<string,boolean>> METSourceJSON = new Map<id,Map<string,boolean>>();
    public static String jsonMETTaskLst = '';
    /*
    * This method is used to populate existing tasks in MET Enrollment Section
    * @param  case Type and case Subtype
    * @return  Map<id,string>
    */
    @auraEnabled
    public static Map<id,string> loadMETTask(String caseType,String caseSubType)
    {
     List<MET_Task__c> lstMETTask = METEnrollment_LH_HUM.getMETTasks(caseType,caseSubType);
     for(Integer i=0; i<lstMETTask.size(); i++)
     {
     METTaskJSON.put(lstMETTask[i].id,lstMETTask[i].name);
     }
     return METTaskJSON;
    }

      /*
    * This method is used to populate existing actions related to MET Task that is selected in  MET Enrollment Section
    * @param  case type, subtype, selected task
    * @return  Map<id,string>
    */
     @auraEnabled    
     public static Map<id,string> loadMETAction(String caseType,String caseSubType,id selectedMETTaskId)
    {
       List<Met_Action__c> lstMETAction = METEnrollment_LH_HUM.getMETActions(caseType,caseSubType,selectedMETTaskId);
        for(Integer i=0; i<lstMETAction .size(); i++)
     {
     METActionJSON .put(lstMETAction[i].id,lstMETAction[i].name);
     }
     return METActionJSON ;
    }  
 
     /*
    * This method is used to populate existing sources related to MET Action that is selected in MET Enrollment Section
    * @param  case type, subtype, selected task, selected action
    * @return Map<id,Map<string,boolean>>
    */
    @auraEnabled
    public static Map<id,Map<string,boolean>> loadMETSource(String caseType,String caseSubType,id selectedMETTaskId, id selectedMETActionId)
    {
        Map<string,boolean> sourceEMMEMap;
        List<MET_Source__c> lstMETSource = METEnrollment_LH_HUM.getMETSources(caseType,caseSubType,selectedMETTaskId,selectedMETActionId);
       if (lstMETSource != null && !lstMETSource.isEmpty())
        {
            for (MET_Source__c src : lstMETSource ) {
                sourceEMMEMap= new Map<string,boolean>();
                sourceEMMEMap.put(src .name,src.Launch_EMME__c);
                METSourceJSON.put(src .id,sourceEMMEMap);
            }
        }
      
     return METSourceJSON ;
    }
    

    /*
    * This method helps in fetching multiple Milestones records related to drop down selection and Case.Type & Case.SubType
    * @param sCaseType
    * @param sCaseSubType
    * @param sSelectedValue
    * @return List 
    */   
    @AuraEnabled
    public static object getMultipleMETTasks(String sCaseType, String sCaseSubType, String sSelectedValue) {
        
        List<MultipleMETTask_LDTO_HUM> lstMultipleMETTask= METEnrollment_LH_HUM.getMultipleMETTasks(sCaseType,sCaseSubType,sSelectedValue);
        
		if(!lstMultipleMETTask.isEmpty())
		{
		lstMultipleMETTask.sort();
		}
                    
                  
        return JSON.serialize(lstMultipleMETTask);
    }
  
    
}