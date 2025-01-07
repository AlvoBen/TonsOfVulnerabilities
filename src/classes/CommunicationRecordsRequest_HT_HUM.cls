/**************************************************************************** 
Apex Class Name  : CommunicationRecordsRequest_HT_HUM
Version          : 1.0  
Function         : Test class for CommunicationRecordsRequest_H_HUM

****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prasanthi Kandula                            12/30/2019                 Original Version
***************************************************************************************************/
@isTest
private Class CommunicationRecordsRequest_HT_HUM
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
            CommunicationRecordsRequest_H_HUM oRequest = new CommunicationRecordsRequest_H_HUM();
            CommunicationRecordsRequest_H_HUM.GetCommunicationRecordsRequest commRecsRequest = new CommunicationRecordsRequest_H_HUM.GetCommunicationRecordsRequest();
            CommunicationRecordsRequest_H_HUM.PageMetaData oPageMetaData = new CommunicationRecordsRequest_H_HUM.PageMetaData();
            CommunicationRecordsRequest_H_HUM.Filter oFilter = new CommunicationRecordsRequest_H_HUM.Filter();
            system.assert(oRequest != null);
            Test.StopTest();
        }
    }      
}