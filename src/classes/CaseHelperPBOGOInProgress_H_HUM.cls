/*****************************************************************************************************
 Apex Class Name : CaseHelperPBOGOInProgress_H_HUM
 Version         : 1.0 
 Created Date    : 07/13/2020
 Function        : Holds the logic which is called from Auto_Route_Case Process Builder
 Test Class      : CaseEdit_CE_HUM_Test
 Modification Log :

* Developer                 Code Review      Date                    Description
* -----------------------------------------------------------------------------------------------------                 
* Sunil Kumar Reddy Y                        06/16/2020                 User Story 1122112: PR00091574 - MF 1 - Quality - OGO - Ability to automatically update case status to In Progress on a Medicare Case if it is a Complaint (CRM)
* Sunil Kumar Reddy Y                        11/16/2020                 User Story 1641500: PR00091574 - Added Grievence work queue and removed Auto route status from SOQL criteria.
* Sunil Kumar Reddy Y                        11/23/2020                 User Story 1641500 - Additional Changes - Make complaint as 'No' and clear the dependant fields
* Amar Gadewar                               05/14/2021                 User Story 2215231: T1PRJ0001827 - MF 1 - RF Clean Up - Was the Grievance Resolved during the Call? (RF)
* Pooja Kumbhar								 02/16/2023				    T1PRJ0170850 - Lightning- Case Management- G & A Complaints Routing Validations
* Pooja Kumbhar								 02/20/2023				    T1PRJ0170850 - Lightning- Case Management- G & A Complaints Routing Validations
*******************************************************************************************************/
public with sharing class CaseHelperPBOGOInProgress_H_HUM {
    @InvocableMethod
    public static void invokeFromPB(List<Case> caseList){
        try{
            set<id> caseIdSet = new set<id>();
            for(case caseRef : caseList)
            {
                caseIdSet.add(caseRef.id);     
            }
            if(!caseIdSet.isEmpty()){
                List<case> lstMyCases = [Select Id,RecordType.Name,Complaint__c,Owner_Queue__c,status  FROM Case where id IN:caseIdSet AND (Owner_Queue__c =:GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT OR Owner_Queue__c=:GLOBAL_CONSTANT_HUM.WORK_QUEUE_GREEN_BAY_GRIEVANCE_APPEALES OR Owner_Queue__c =:GLOBAL_CONSTANT_HUM.WORK_QUEUE_LV_Screening)];
                OGOCaseUpdate(lstMyCases); 
            }
        }
        catch (Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CaseHelperPBOGOInProgress_H_HUM', 'invokeFromPB');    
        }
    }

    public static void oGOCaseUpdate(List<Case> lstMyCases){
        Schema.DescribeSObjectResult dSResult = Schema.SObjectType.Case;  
        map<String,Schema.RecordTypeInfo> rtMapByName = dSResult.getRecordTypeInfosByName();
        List<Case> listToUpdate = new List<Case>();
        for(Case caseObj : lstMyCases){
            if(caseObj.Status != GLOBAL_CONSTANT_HUM.HUM_CLOSED && caseObj.Owner_Queue__c == GLOBAL_CONSTANT_HUM.WORK_QUEUE_GREEN_BAY_GRIEVANCE_APPEALES && String.isNotBlank(caseObj.Complaint__c ) && GLOBAL_CONSTANT_HUM.COMPLAINT_NO != caseObj.Complaint__c ){
                caseObj.Complaint__c = GLOBAL_CONSTANT_HUM.COMPLAINT_NO;
                caseObj.Complaint_Reason__c = null;
                caseObj.Complaint_Type__c = null;
            }
            if(caseObj.Status == GLOBAL_CONSTANT_HUM.HUM_CLOSED &&(caseObj.RecordType.Name == GLOBAL_CONSTANT_HUM.HUM_CLOSEDMEMBERCASE || caseObj.RecordType.Name == GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE || caseObj.RecordType.Name == GLOBAL_CONSTANT_HUM.CLOSEDHP_MEMBERCASE_HUM) ){
                if(caseObj.RecordType.Name == GLOBAL_CONSTANT_HUM.HUM_CLOSEDMEMBERCASE){
                    caseObj.status = GLOBAL_CONSTANT_HUM.CASESTATUS_HUM;
                    caseObj.recordtypeid = rtMapByName.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM).recordTypeId;
                }else if(caseObj.RecordType.Name == GLOBAL_CONSTANT_HUM.CLOSEDHP_MEMBERCASE_HUM)
                {
                    caseObj.status = GLOBAL_CONSTANT_HUM.CASESTATUS_HUM;
                    caseObj.recordtypeid = rtMapByName.get(GLOBAL_CONSTANT_HUM.HP_MEMBERCASE_HUM).recordTypeId;
                }
                else {
                    caseObj.status = GLOBAL_CONSTANT_HUM.CASESTATUS_HUM;
                    caseObj.recordtypeid = rtMapByName.get(GLOBAL_CONSTANT_HUM.MEDICARE_CASE).recordTypeId;
                }
             }
                listToUpdate.add(caseObj);
        }
        
        if(!listToUpdate.isEmpty()){
            update listToUpdate;
        }
    }
}