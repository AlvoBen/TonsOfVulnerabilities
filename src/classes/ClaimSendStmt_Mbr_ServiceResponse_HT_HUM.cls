/**************************************************************************** 
Apex Class Name  : ClaimSendStmt_Mbr_ServiceResponse_HT_HUM
Version          : 1.0  
Function         : Test class for ClaimSendStmt_Mbr_ServiceResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat           295053                09/28/2018                 Original Version
***************************************************************************************************/
@isTest
private Class ClaimSendStmt_Mbr_ServiceResponse_HT_HUM
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
            ClaimSendStmt_Mbr_ServiceResponse_H_HUM oReponse = new ClaimSendStmt_Mbr_ServiceResponse_H_HUM();
            ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResponse oReprintResponse = new ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResponse();
            ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResult oReprintResult = new ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResult();
            system.assert(oReponse != null);
            Test.StopTest();
        }
    }    
}