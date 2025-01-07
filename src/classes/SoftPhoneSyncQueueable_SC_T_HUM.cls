/*******************************************************************************************************************************
Apex Class Name     : SoftPhoneSyncQueueable_SC_T_HUM 
Version             : 1.0
Created Date        : June 12 2020
Function            : Test Class for SoftPhoneSyncQueueable_SC_HUM

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Akshay Pai                                    		06/12/2020                  Initial Version
*******************************************************************************************************************************/
@isTest
private class SoftPhoneSyncQueueable_SC_T_HUM {
   
  /**
    *  CRON expression: Everyday at 5.00 AM.
    *  @name testPostive
    *  @param NA
    *  @return NA
    *  @throws exception NA
    */ 
  static testmethod void testPostive() {
     
     String CRON_EXP = '0 0 5 * * ? *';
      
     Test.startTest();
            
     SoftPhoneSyncQueueable_SC_HUM softphoneSyncJob = new SoftPhoneSyncQueueable_SC_HUM();     
     String jobId = System.schedule('softphoneDailySyncJob',
                        CRON_EXP, 
                        softphoneSyncJob);
         
     // Get the information from the CronTrigger API object
     CronTrigger ct = [SELECT Id, CronExpression, TimesTriggered FROM CronTrigger WHERE id = :jobId];

      // Verify the expressions are the same
      System.assertEquals(CRON_EXP, ct.CronExpression);

      // Verify the job has not run
      System.assertEquals(0, ct.TimesTriggered);

      Test.stopTest();
   }
}