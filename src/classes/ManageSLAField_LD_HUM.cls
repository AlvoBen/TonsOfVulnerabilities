/*******************************************************************************************************************************
Apex Class Name : ManageSLAField_LD_HUM
Version         : 1.0
Created On      : 05/05/2021
Function        : To update SLA fields on work queue setup and Queue view object

Modification Log: 
* Developer Name                         Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vijaykumar                                          05/06/2021                  US-2224460  T1PRJ0001827- IR 5050 - Create new " Manage SLA setup" page -WQ summary & Queue View (CRM)
*******************************************************************************************************************************/



public with sharing class ManageSLAField_LD_HUM {
   
     /*
    * This method is used when update WQS record
    * @param  WQS record
    * @return Boolean
    */
    @AuraEnabled
    public static Boolean updateWQSRecord(Work_Queue_Setup__c objWQSData){
        Boolean bIsSuccess = false;
        try{
            Database.SaveResult oUpdateSuccess = database.update(objWQSData, false);
                if(oUpdateSuccess.isSuccess()){
                  bIsSuccess =true;
                }else{
                  bIsSuccess =false;
                  throw new ManageSLAField_LC_HUM.ManageSLAFieldCustomException('Error in updating WorkQueueSetup Record'); 
                }
            }catch (Exception e) {
                HUMExceptionHelper.logErrors(e, 'ManageSLAField_LD_HUM', 'updateWQSRecord');
            }
        return bIsSuccess;
    }

     /*
    * This method is used update list of Queue views
    * @param  Queue_View__c List
    * @return Boolean
    */
    @AuraEnabled
    public static Boolean updateQVRecords(List<Queue_View__c> lstQueueViews){
        Boolean bIsSuccess = false;
        try{             
              Database.SaveResult[] srList = Database.update(lstQueueViews, false);              
              for (Database.SaveResult saveSucc : srList) {                  
                   if (saveSucc.isSuccess()){ 
                    bIsSuccess =true; 
                   }else{ 
                    bIsSuccess =false;
                    throw new ManageSLAField_LC_HUM.ManageSLAFieldCustomException('Error in updating Queue View Records'); 
                  }                  
               }               
            }catch (Exception e) {              
                HUMExceptionHelper.logErrors(e,'ManageSLAField_LD_HUM','updateQVRecords');
            }
        return bIsSuccess;
    }

}