/**************************************************************************** 
Apex Class Name  : ClaimSendStmt_ServiceRequest_HT_HUM
Version          : 1.0  
Function         : Test class for ClaimSendStmt_ServiceRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Trina Ghosh                               07/28/2020                 Original Version
***************************************************************************************************/
@isTest
private Class ClaimSendStmt_ServiceRequest_HT_HUM
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
            ClaimSendStmt_ServiceRequest_H_HUM oRequest = new ClaimSendStmt_ServiceRequest_H_HUM();
            ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleDesigned oRecipientRoleDesigned = new ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleDesigned();
            ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleMember oRecipientRoleMember = new ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleMember();
            ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleOther oRecipientRoleOther = new ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleOther();
            ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleProvider oRecipientRoleProvider = new ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleProvider();
           
            System.Assert(oRequest != null);
            Test.StopTest();
        }
    }    
}