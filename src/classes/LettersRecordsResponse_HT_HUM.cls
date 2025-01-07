/**************************************************************************** 
Apex Class Name  : LettersRecordsResponse_HT_HUM
Version          : 1.0  
Function         : Test class for LettersRecordsResponse_H_HUM

****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Imran Khan                                 12/30/2019                 Original Version
***************************************************************************************************/
@isTest
private Class LettersRecordsResponse_HT_HUM
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
            LettersRecordsResponse_H_HUM oResponse = new LettersRecordsResponse_H_HUM();
            LettersRecordsResponse_H_HUM.LettersResponse letterResponse = new LettersRecordsResponse_H_HUM.LettersResponse();
            LettersRecordsResponse_H_HUM.LettersRecordsList lstLetterRecs = new LettersRecordsResponse_H_HUM.LettersRecordsList();
            LettersRecordsResponse_H_HUM.LettersRecords oletterRec= new LettersRecordsResponse_H_HUM.LettersRecords();
            system.assert(oResponse != null);
            Test.StopTest();
        }
    }      
}