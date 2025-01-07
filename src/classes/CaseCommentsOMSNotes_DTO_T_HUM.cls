/**************************************************************************** 
Apex Class Name  : CaseCommentsOMSNotes_DTO_T_HUM
Version          : 1.0  
Created Date     : 09/01/2020
Function         : Test class for CaseCommentsOMSNotes_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* AshokKumar Nutalapati                         09/01/2020                 Original Version
***************************************************************************************************/

@isTest
private class CaseCommentsOMSNotes_DTO_T_HUM
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
    * Description : Method to assign values to CaseCommentsOMSNotes_DTO_T_HUM
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
            CaseCommentsOMSNotes_DTO_HUM oComments=new CaseCommentsOMSNotes_DTO_HUM();
            oComments.CaseId='';
            oComments.CaseNumber='';
            oComments.CaseURL='';
            oComments.LogCode='PHARMACY';
            oComments.CreatedDate ='09/01/2020';
            oComments.SourceType='OMS';          
            Test.stopTest();
        }
    }
}