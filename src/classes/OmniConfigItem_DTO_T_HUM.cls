/*
  Apex Class Name  : OmniConfigItem_DTO_T_HUM
  Version          : 1.0 
  Created Date     : Aug 28 2020
  Function         : Test Class for OmniConfigItem_DTO_HUM
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Mohammed Noor                                         08/28/2020                 Original Version
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
@isTest
private class OmniConfigItem_DTO_T_HUM 
{
    /*
    * Method Name : dataSetup
    * Description : Create common test Data
    * Return type : void
    * Paramater   : NA
    */
    
    @testSetup static void prepareTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('OmniConfigUser','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test OmniConfigUser');
    }
    
    /*
    * Method Name : testOmniConfigItemDTO
    * Description : Method to test values to OmniConfigItem_DTO_HUM
    * Return type : void
    * Paramater   : NA
    */   
   testMethod static void testOmniConfigItemDTO()
    {
       User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test OmniConfigUser'];
        System.assertEquals('test OmniConfigUser',oUser.LastName);
        system.runAs(oUser)
        {
            Test.startTest();
            OmniConfigItem_DTO_HUM oWrapper = new OmniConfigItem_DTO_HUM();
            oWrapper.BusinessConfigName = 'TestOmniConfigItem';
            oWrapper.workItemName = 'Case';
            oWrapper.maxNoOfWorkItems = 100;
            oWrapper.maxLimitofBusinessArea = 25000;
            oWrapper.DefaultOmniQueueName = 'TestDefaultOmniQueue'; 
            oWrapper.OverflowAssigneeQueueName = 'TestOverflowQueue';
            oWrapper.isUpdate = false;
            oWrapper.totalOrgLimit = 150000;
            Test.stopTest();
        }
    }
    
}