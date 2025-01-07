/**************************************************************************** 
Apex Class Name  : QuickStartHPSpecialtyCalls_DTO_T_HUM
Version          : 1.0  
Created Date     : 09/01/2020
Function         : Test class for QuickStartHPSpecialtyCalls_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* AshokKumar Nutalapati                         05/10/2021                 Original Version
***************************************************************************************************/

@isTest
public class QuickStartHPSpecialtyCalls_DTO_T_HUM
{
    
    /*
    * Method Name : dataSetup
    * Description : Create common test Data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
    }
    
    /*
    * Method Name : createRecords
    * Description : Method to assign values to QuickStartHPSpecialtyCalls_DTO_T_HUM
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
            QuickStartHPSpecialtyCalls_DTO_HUM oHPSUser=new QuickStartHPSpecialtyCalls_DTO_HUM();
            oHPSUser.ServiceModelType = 'HP Specialty User';
            oHPSUser.GARights = true;
            oHPSUser.HideLogCode = true;         
            Test.stopTest();
        }
    }
}