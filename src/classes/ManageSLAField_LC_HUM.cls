/*******************************************************************************************************************************
Apex Class Name : ManageSLAField_LC_HUM
Version         : 1.0
Created On      : 05/03/2021
Function        : To update SLA fields on work queue setup object

Modification Log: 
* Developer Name                         Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vijaykumar                                          05/06/2021                  US-2224460  T1PRJ0001827- IR 5050 - Create new " Manage SLA setup" page -WQ summary & Queue View (CRM)
*******************************************************************************************************************************/


public with sharing class ManageSLAField_LC_HUM {
     
     /*
    * This method is used get WQS record
    * @param Id recordID
    * @return WorkQueueSetup
    */
        @AuraEnabled(cacheable=true)
        public static Work_Queue_Setup__c getWQSData(Id recordID){
            Work_Queue_Setup__c objWQSRec = null;
            try {
                objWQSRec = ManageSLAField_LS_HUM.getWQSRecord(recordID);
            } catch(Exception e) {
               HUMExceptionHelper.logErrors(e, 'ManageSLAField_LC_HUM', 'getWQSData');
            }
            return objWQSRec;
        }
        
    /*
    * This method is used update WQS record
    * @param WorkQueueSetup objWQSData
    * @return Boolean
    */
        @AuraEnabled
        public static Boolean updateWQSRecord(Work_Queue_Setup__c objWQSData){
            Boolean bIsSuccess = false;
            try{
                 if(objWQSData!=null) {
                    bIsSuccess = ManageSLAField_LS_HUM.updateWQSRec(objWQSData);
                }
                }catch (Exception e) {
                    HUMExceptionHelper.logErrors(e, 'ManageSLAField_LC_HUM', 'updateWQSRecord');
                }
            return bIsSuccess;
        }
    
     /*
    * This method is used get Queue views
    * @param Id wqsId
    * @return QueueView list
    */
        @AuraEnabled(cacheable=true)
        public static List<Queue_View__c> getQueueViewData(Id wqsId){
            List<Queue_View__c> lstQVS = null;
            try{
                lstQVS = ManageSLAField_LS_HUM.getQueueViewsData(wqsId);
            }catch (Exception e) {
                HUMExceptionHelper.logErrors(e, 'ManageSLAField_LC_HUM', 'getQueueViewData');
            }
            return lstQVS;
        }
    
     /*
    * This method is used update Queue views
    * @param QueueView 
    * @return Boolean
    */
        @AuraEnabled
        public static Boolean saveQueueViewData(List<Queue_View__c> lstQueueViews){
            Boolean bIsSuccess = false;
            try{
                if(lstQueueViews!=null && !lstQueueViews.isEmpty()){
                    bIsSuccess = ManageSLAField_LS_HUM.saveQVRecords(lstQueueViews);
                }
            }catch (Exception e) {
                HUMExceptionHelper.logErrors(e, 'ManageSLAField_LC_HUM', 'saveQueueViewData');
            }
            return bIsSuccess;
        }
        
        public class ManageSLAFieldCustomException extends Exception { }

    }