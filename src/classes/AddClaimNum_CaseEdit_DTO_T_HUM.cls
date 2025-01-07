/**************************************************************************** 
Apex Class Name  : AddClaimNum_CaseEdit_DTO_T_HUM
Version          : 1.0  
Created Date     : 11/18/2019
Function         : Test class for AddClaimNum_CaseEdit_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Vijaya Koriprolu                             11/18/2019                Wrapper Class test class for adding claim numbers
***************************************************************************************************/

@isTest
private class AddClaimNum_CaseEdit_DTO_T_HUM
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
* Description : Method to assign values to AddClaimNum_CaseEdit_DTO_HUM
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
            AddClaimNum_CaseEdit_DTO_HUM oDetails=new AddClaimNum_CaseEdit_DTO_HUM();
            oDetails.sClaimText ='Claim Number';
            oDetails.sClaimNumber   ='111111111';
			oDetails.sSecondCol    ='111111111';
			oDetails.sThirdCol    ='111111111';
			oDetails.sFourthCol     ='111111111';
			oDetails.sFifthCol      ='111111111';
            System.assertEquals('Claim Number',oDetails.sClaimText);         
            Test.stopTest();
        }
    }
}