/*****************************************************************************************************
 Apex Class Name : CaseRoutingProcessHelper_HUM
 Version         : 1.0 
 Created Date    : 11/02/2020
 Function        : Holds the logic which is called from Auto_Route_Case Process Builder
 Test Class      : CaseTransferService_T_HUM
 Modification Log :

* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Sunil Kumar Reddy Y                        06/16/2020                 User Story 1641500: PR00091574 - MF 1 - Quality - Auto Routing logic change for MCD Grievances in the state of IL and KY (CRM)
*******************************************************************************************************/
public with sharing class CaseRoutingProcessHelper_HUM {
    @InvocableMethod
    public static void callService(List<Id> lstCase){
        for(Case caseRecord : [SELECT id,status FROM Case WHERE id IN:lstCase]){
            if(caseRecord.status == Label.External_Transfer_in_Progress){
                CaseTransferService_S_HUM.CaseTransferProcess(caseRecord.id);
            }
        }
            
    }
}