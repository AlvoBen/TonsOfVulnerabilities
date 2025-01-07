/**************************************************************************** 
Apex Class Name  : LinkCase_Wrapper_T_HUM  
Version          : 1.0  
Created Date     : 07/20/2016
Function         : Test class for LinkCase_Wrapper_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat                             07/20/2016                  Original Version
***************************************************************************************************/
@isTest
private Class LinkCase_Wrapper_T_HUM 
{
    /*
     * Method Name : prepareTestData
     * Description : Create common test Data
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
            
            LinkCase_Wrapper_DTO_HUM oLinkCase = new LinkCase_Wrapper_DTO_HUM();
            
            oLinkCase.sCaseId = '500L0000008E5RmIAK';
            System.assertEquals(oLinkCase.sCaseId, '500L0000008E5RmIAK');
            oLinkCase.sCaseNumber = '1000000623145';
            System.assertEquals(oLinkCase.sCaseNumber, '1000000623145');
            oLinkCase.sInfo = 'Test User 02/04/2011';
            System.assertEquals(oLinkCase.sInfo , 'Test User 02/04/2011');
            oLinkCase.sWorkQueue = 'Phoenix_Calls';
            System.assertEquals(oLinkCase.sWorkQueue , 'Phoenix_Calls');
           
            Test.StopTest();
        }
        
    }    
}