/**************************************************************************** 
Apex Class Name  : LettersRecordsRequest_HT_HUM
Version          : 1.0  
Function         : Test class for LettersRecordsRequest_H_HUM

****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Imran Khan                                  12/30/2019                 Original Version
***************************************************************************************************/
@isTest
private Class LettersRecordsRequest_HT_HUM
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
            LettersRecordsRequest_H_HUM oRequest = new LettersRecordsRequest_H_HUM();
            LettersRecordsRequest_H_HUM.GetLettersRequest lettersRecsRequest = new LettersRecordsRequest_H_HUM.GetLettersRequest();
            LettersRecordsRequest_H_HUM.PageMetaData oPageMetaData = new LettersRecordsRequest_H_HUM.PageMetaData();
            LettersRecordsRequest_H_HUM.Filter oFilter = new LettersRecordsRequest_H_HUM.Filter();
            system.assert(oRequest != null);
            Test.StopTest();
        }
    }      
}