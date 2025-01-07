/****************************************************************************************
Apex Class Name  : OmniEligibleCaseAssignment_SC_T_HUM
Version          : 1.0 
Created Date     : 02/06/2018
Function         : Test class for OmniEligibleCaseAssignment_SC_HUM Apex class
Modification Log :
 *  Developer         Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Mohammed Noor                             02/06/2018           Original Version   
 * Pooja Kumbhar							 05/17/2022           Changed the constant date to dynamic date to fix issue
 *******************************************************************************************/
@isTest
private class OmniEligibleCaseAssignment_SC_T_HUM {
   
  public static String CRON_EXP = Datetime.now().addDays(1).format('0 0 0 dd MM ? YYYY');
 
  /**
    *  CRON expression: Everyday at 2.00 AM. Because this is a test, job executes immediately after Test.stopTest().
    *  @name testPostive
    *  @param NA
    *  @return NA
    *  @throws exception NA
    */ 

  static testmethod void testPostive() {
     
     Test.startTest();
     OmniEligibleCaseAssignment_SC_HUM omniJob = new OmniEligibleCaseAssignment_SC_HUM();     
     String jobId = System.schedule('OmniBatchJob',
                        CRON_EXP, 
                        omniJob);
         
     // Get the information from the CronTrigger API object
     CronTrigger ct = [SELECT Id, CronExpression, TimesTriggered, 
                       NextFireTime
                       FROM CronTrigger WHERE id = :jobId];

      // Verify the expressions are the same
      System.assertEquals(CRON_EXP, ct.CronExpression);

      // Verify the job has not run
      System.assertEquals(0, ct.TimesTriggered);

      // Verify the next time the job will run
      System.assertEquals(String.valueOf(date.today().addDays(1))+' 00:00:00', String.valueOf(ct.NextFireTime));

      Test.stopTest();
   }
}