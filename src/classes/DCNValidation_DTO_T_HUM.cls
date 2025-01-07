/*
  Apex Class Name  : DCNValidation_DTO_T_HUM
  Version          : 1.0 
  Created Date     : Aug 28 2020
  Function         : Test Class for DCNValidation_DTO_T_HUM
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Pooja Kumbhar                                         08/28/2020                 Original Version
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
@isTest
Private class DCNValidation_DTO_T_HUM {
	
    /*
    * Method Name : TestData
    * Description : test Data
    * Return type : void
    * Paramater   : NA
    */
    
    @testSetup static void TestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('DTOUser1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test DTOUser1');
    }
    
    /*
    * Method Name : testDCNValidation_DTO_HUM
    * Description : Method to test DTO values of DCNValidation_DTO_HUM
    * Return type : void
    * Paramater   : NA
    */   
   testMethod static void testDCNValidation_DTO_HUM()
    {
       User oUser = [Select Id, LastName from User where LastName = 'test DTOUser1'];
        System.assertEquals('test DTOUser1',oUser.LastName);
        system.runAs(oUser)
        {
            Test.startTest();
            DCNValidation_DTO_HUM DCNVALDTO = new DCNValidation_DTO_HUM();
            DCNVALDTO.DCN = '1234567';
            DCNVALDTO.Source = 'Test';
            Test.stopTest();
        }
    }
    
}