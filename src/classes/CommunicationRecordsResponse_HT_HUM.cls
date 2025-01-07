/**************************************************************************** 
Apex Class Name  : CommunicationRecordsResponse_HT_HUM
Version          : 1.0  
Function         : Test class for CommunicationRecordsResponse_H_HUM

****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prasanthi Kandula                            12/30/2019                 Original Version
***************************************************************************************************/
@isTest
private Class CommunicationRecordsResponse_HT_HUM
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
            CommunicationRecordsResponse_H_HUM oResponse = new CommunicationRecordsResponse_H_HUM();
            CommunicationRecordsResponse_H_HUM.CommunicationRecordsResponse commRecsResponse = new CommunicationRecordsResponse_H_HUM.CommunicationRecordsResponse();
            CommunicationRecordsResponse_H_HUM.CommunicationRecordsList lstCommRecs = new CommunicationRecordsResponse_H_HUM.CommunicationRecordsList();
            CommunicationRecordsResponse_H_HUM.CommunicationRecords oCommRec= new CommunicationRecordsResponse_H_HUM.CommunicationRecords();
            system.assert(oResponse != null);
            Test.StopTest();
        }
    }      
}