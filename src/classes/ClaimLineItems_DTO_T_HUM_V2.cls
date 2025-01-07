/**************************************************************************** 
Apex Class Name  : ClaimLineItems_DTO_T_HUM_V2
Version          : 1.0  
Created Date     : March 06, 2018
Function         : Test class for ClaimLineItems_DTO_HUM_V2
******************************************************************************
Modification Log:
* Developer Name          Code Review         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prasanthi Kandula          212620           03/06/2018                  Original Version
***************************************************************************************************/
@isTest
private Class ClaimLineItems_DTO_T_HUM_V2
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
            ClaimLineItems_DTO_HUM_V2.ClaimDetailLine oClaimLineItem = new ClaimLineItems_DTO_HUM_V2.ClaimDetailLine();
            oClaimLineItem.sLineNbr = '123';
            System.assertEquals(oClaimLineItem.sLineNbr, '123');
            
            ClaimLineItems_DTO_HUM_V2.PaymentCode oClaimPay = new ClaimLineItems_DTO_HUM_V2.PaymentCode();
            oClaimPay.sLegacyCd = 'Cd123';
            System.assertEquals(oClaimPay.sLegacyCd, 'Cd123');
            Test.StopTest();
        }
        
    }    
}