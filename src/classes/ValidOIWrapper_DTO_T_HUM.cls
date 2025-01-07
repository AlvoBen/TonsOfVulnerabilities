/**************************************************************************************************************************************************
    @Apex class Name    :  ValidOIWrapper_DTO_T_HUM
    @Function           : This class serves as test class for ValidOIWrapper_DTO_HUM
    @Version            : 1.0
    @reference          : ValidOIWrapper_DTO_HUM

Modification Log:
* Developer Name         Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Keerti Durbha                              06/05/2020                 Original Version 
************************************************************************************************************************************************* */

@isTest
public class ValidOIWrapper_DTO_T_HUM {
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
* Description : Method to assign values to ValidOIWrapper_DTO_HUM
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
        	ValidOIWrapper_DTO_HUM ValidOIWrapper= new  ValidOIWrapper_DTO_HUM();	
              ValidOIWrapper.sCarrierName = 'Test Name';
              ValidOIWrapper.sEffectiveDate = 'test';
              ValidOIWrapper.sEndDate = 'test';
              ValidOIWrapper.sPrimary = 'test';
              ValidOIWrapper.sOILastVerifiedDate = 'test';
			  ValidOIWrapper.sLastUpdatedDate = 'test';
            Test.stopTest();
        }
    }
}