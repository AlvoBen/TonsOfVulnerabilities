/****************************************************************************************
Apex Class Name  : DEAAProcess_Case_SC_T_HUM
Version          : 1.0 
Created Date     : 01/30/2020
Function         : Test class for DEAAProcess_Case_SC_HUM Apex class
Modification Log :
* Developer Name        Code Review     Date             Description
*------------------------------------------------------------------------------
* Moshitha Gunasekaran                  01/30/2020        Original Version
******************************************************************************/
@isTest
private class DEAAProcess_Case_SC_T_HUM {

   public static String CRON_EXP = '0 30 0 ? * *';
     
  
  /**
    *  CRON expression: Everyday at 12.30 AM. Because this is a test, job executes immediately after Test.startTest().
    *  @name testPostive
    *  @param NA
    *  @return NA
    *  @throws exception NA
    */ 

  static testmethod void testPostive() {
     
     Test.startTest();
            
     DEAAProcess_Case_SC_HUM deaaJob = new DEAAProcess_Case_SC_HUM();     
     String jobId = System.schedule('DEAABatchJob',
                        CRON_EXP, 
                        deaaJob);
         
     // Get the information from the CronTrigger API object
     CronTrigger ct = [SELECT Id, CronExpression, TimesTriggered, 
                       NextFireTime
                       FROM CronTrigger WHERE id = :jobId];

      // Verify the expressions are the same
      System.assertEquals(CRON_EXP, ct.CronExpression);

      // Verify the job has not run
      System.assertEquals(0, ct.TimesTriggered);

      Test.stopTest();
   }
}