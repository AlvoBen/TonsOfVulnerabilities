/******************************************************************************
Apex Class Name  : SAILDeactivateUsers_SC_T_HUM
Version          : 1.0
Created Date     : Nov 09 2016
Function         : Test Class for the the SAILDeactivateUsers_SC_HUM Class
*******************************************************************************

Modification Log:

* Developer Name   Code Review    Date             Description
*------------------------------------------------------------------------------
* Kent Sanders       48578      11/09/2016     Initial creation of the class.
******************************************************************************/

@isTest
private class SAILDeactivateUsers_SC_T_HUM {
   
  public static String CRON_EXP = '0 0 0 15 3 ? 2024';
  
  /**
    *  CRON expression: midnight on March 15. Because this is a test, job executes immediately after Test.stopTest().
    *  @name testPostive
    *  @param NA
    *  @return NA
    *  @throws exception NA
    */ 

  static testmethod void testPostive() {
     
     Test.startTest();
            
     SAILDeactivateUsers_SC_HUM deactUsrSc = new SAILDeactivateUsers_SC_HUM();     
     String jobId = System.schedule('DeactivateScheduleApexClassTest',
                        CRON_EXP, 
                        deactUsrSc);
         
     // Get the information from the CronTrigger API object
     CronTrigger ct = [SELECT Id, CronExpression, TimesTriggered, 
                       NextFireTime
                       FROM CronTrigger WHERE id = :jobId];

      // Verify the expressions are the same
      System.assertEquals(CRON_EXP, ct.CronExpression);

      // Verify the job has not run
      System.assertEquals(0, ct.TimesTriggered);

      // Verify the next time the job will run
      System.assertEquals('2024-03-15 00:00:00', String.valueOf(ct.NextFireTime));

      Test.stopTest();

   }
}