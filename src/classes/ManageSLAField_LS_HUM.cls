/*******************************************************************************************************************************
Apex Class Name : ManageSLAField_LS_HUM
Version         : 1.0
Created On      : 05/03/2021
Function        : To update SLA fields on work queue setup object

Modification Log: 
* Developer Name                         Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vijaykumar                                                  05/06/2021                  US-2224460  T1PRJ0001827- IR 5050 - Create new " Manage SLA setup" page -WQ summary & Queue View (CRM)
*******************************************************************************************************************************/


public with sharing class ManageSLAField_LS_HUM {

    /*
    * This method is used get WQS 
    * @param Id recordID
    * @return WorkQueueSetup
    */
    @AuraEnabled(cacheable=true)
    public static Work_Queue_Setup__c getWQSRecord(Id recordID){
        return ([select id, name, Department__c, Queue_Description__c, Service_Center__c,SLA_Email_Alert__c, SLA_In_Days__c, SLA_On_Case_Transfer__c from Work_Queue_Setup__c where id=:recordID limit 1]);
    }

    /*
    * This method is used get QueueViews
    * @param Id recordID
    * @return QueueView list
    */
    @AuraEnabled(cacheable=true)
    public static List<Queue_View__c> getQueueViewsData(Id wqsId){
        return ([select id, name, SLA_In_Days__c, SLA_On_Case_Transfer__c from Queue_View__c where Work_Queue_Name__c=:wqsId order by name]);
    }

    /*
    * This method is used update WorkQueueSetup 
    * @param WorkQueueSetup objWQSData
    * @return Boolean
    */
    @AuraEnabled
    public static Boolean updateWQSRec(Work_Queue_Setup__c objWQSData){
        Boolean bIsSuccess = ManageSLAField_LD_HUM.updateWQSRecord(objWQSData);
        return bIsSuccess;
    }

    /*
    * This method is used to update QueueViews
    * @param QueueViews lstQueueViews
    * @return Boolean
    */
    @AuraEnabled
    public static Boolean saveQVRecords(List<Queue_View__c> lstQueueViews){
        Boolean bIsSuccess = ManageSLAField_LD_HUM.updateQVRecords(lstQueueViews);
        return bIsSuccess;
    }

}