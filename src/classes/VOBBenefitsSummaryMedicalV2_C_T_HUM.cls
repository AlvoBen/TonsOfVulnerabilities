/******************************************************************************************************************
Apex class Name    : VOBBenefitsSummaryMedicalV2_C_T_HUM
Version            : 1.0
Function           : Test Class for VOBBenefitsSummaryMedicalV2_C_HUM
Created On         : May 7 2021

Modification Log:
*   Version        Developer                  Date                Description
* -----------------------------------------------------------------------------------------------------------------                
 *    1.0          Samruddhi Rathi             05/07/2021          Original Version 
 
******************************************************************************************************************/
@isTest
public class VOBBenefitsSummaryMedicalV2_C_T_HUM{
 /*
    * Method Name : setUpTestData
    * Description : This method creates test data For test Class
    * Return type : None
    * Parameter   : None
    */
    @testSetup static void setUpTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser; 
        System.assertEquals(oUser.LastName, 'test User1');
    }
    
  /*
* Method Name : createRecords
* Description : Method to assign values to VOBBenefitsSummaryMedicalV2_C
* Return type : void
* Paramater   : NA
*/   

   testMethod static void createRecords()
    {
       User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('test User1',oUser.LastName);
        system.runAs(oUser)
        {
            Test.startTest();
            VOBBenefitsSummaryMedicalV2_C_HUM BenefitSummaryMedicalV2 = new VOBBenefitsSummaryMedicalV2_C_HUM();
            BenefitSummaryMedicalV2.bOnAndOffFlag = true;
            Test.stopTest();
            
        }
      }
      
 }