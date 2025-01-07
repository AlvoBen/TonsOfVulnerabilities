/**************************************************************************** 
Apex Class Name  : Claim_Send_Statement_Response_T_HUM
Version          : 1.0  
Created Date     : October 05, 2018
Function         : Test class for Claim_Send_Statement_Response_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Vijaya Koriprolu                             10/05/2018             Original Version
***************************************************************************************************/
@isTest
private Class Claim_Send_Statement_Response_T_HUM
{

    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
    }
    
    /*
     * Method Name : createResponceRecord
     * Description : Test method for Related Case List functionality
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void createResponceRecord() 
    {
        User u = [SELECT Id, Lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertEquals(u.Lastname, 'test User1');
        system.runAs(u)
        {
            Test.startTest();                    
            String response ='{ "StandardizeAddressRequest": { "AddressLine1": "8900 shelbyville road", "AddressLine2": "apt 5", "City": "louisville", "StateCode": "ky", "ZipCode": "41105" } }'; 
            system.assertEquals(response != Null,true);
            Claim_Send_Statement_Response_H_HUM oGetHasArchivalResponse = Claim_Send_Statement_Response_H_HUM.parse(response);
            system.assertEquals(oGetHasArchivalResponse != Null,true);
            Test.stopTest();
        }
    }
 }