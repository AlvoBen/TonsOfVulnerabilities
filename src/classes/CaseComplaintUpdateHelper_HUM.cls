/*****************************************************************************************************
 Apex Class Name : CaseComplaintUpdateHelper_HUM
 Version         : 1.0 
 Created Date    : 03/05/2021
 Function        : Holds the logic which is called from Auto_Route_Case Process Builder
 Test Class      : CaseEdit_CE_HUM_Test
 Modification Log :

* Developer                 Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Kavya Bondada             04/14/2021                  User Story 2187149: T1PRJ0001827 - MF 1 - Quality - Auto route QAA cases to the G&A team (CRM)
* Amar Gadewar              05/14/2021                  User Story 2215231: T1PRJ0001827 - MF 1 - RF Clean Up - Was the Grievance Resolved during the Call? (RF)
* Bhumika S                 02/10/2023                  User Story 3621739: T1PRJ0393258- Complaint Routing - iZone 903- CRM FL MCD QAA Routing Changes (Classic)       
*******************************************************************************************************/
public with sharing class CaseComplaintUpdateHelper_HUM {
    @InvocableMethod
    public static void updateCaseComplaint(List<Id> lstCase){
        List<Case> listToUpdate = new List<Case>();
        for(Case caseRecord : [SELECT id,Complaint__c,Complaint_Reason__c,Complaint_Type__c,G_A_Rights_Given__c,G_A_Reason__c FROM Case WHERE id IN:lstCase]){
            
            caseRecord.Complaint__c = CaseAutoRouteConstant_C_HUM.COMPLAINT_NO;
            caseRecord.Complaint_Reason__c = null;
            caseRecord.Complaint_Type__c = null;
            caseRecord.G_A_Rights_Given__c = CaseAutoRouteConstant_C_HUM.GA_NO;
            caseRecord.G_A_Reason__c = null;
            listToUpdate.add(caseRecord);
        }
        if(!listToUpdate.isEmpty()){
            update listToUpdate;
        }
    }
}