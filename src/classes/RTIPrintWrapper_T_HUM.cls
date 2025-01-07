/**************************************************************************** 
Apex Class Name  : RTIPrintWrapper_T_HUM
Version          : 1.0  
Created Date     : 28th June 2017
Function         : Test class RTIPrintSummary_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
 Shivam Anand                106733             06/28/2017                  Original Version
 Prasanthi Kandula                              04/17/2020                  Improved code coverage            
***************************************************************************************************/
@isTest
private Class RTIPrintWrapper_T_HUM
{
   /*
    * Method Name : prepareTestData
    * Description : Used to create wrapper class data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
    }
    
   /*
    * Method Name : runPositiveData
    * Description : Used to run scenerios of test data
    * Return type : void
    * Paramater   : NA
    */ 
    static testMethod void runPositiveData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            RTIPrintSummary_DTO_HUM oRTISummary = new RTIPrintSummary_DTO_HUM();
            oRTISummary.sdocumentViewUrl = '';
            oRTISummary.sdocumentType = 'PRINT';
            System.assertEquals(oRTISummary.sdocumentViewUrl, '');
            Test.StopTest();
        }     
    }
}