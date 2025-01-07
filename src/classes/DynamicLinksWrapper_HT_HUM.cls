/***************************************************************************************************
Apex Class Name  : DynamicLinksWrapper_HT_HUM
Version          : 1.0
Created Date     : 11/04/2015
Function         : Teat Class for DynamicLinksWrapper_H_HUM
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
* Santhi Mandava                                    11/04/2015                  Original Version
***************************************************************************************************/

@isTest
private class DynamicLinksWrapper_HT_HUM
{
    @testSetup
    static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
    }
    
    /*
    * Method Name : testHelperMethod
    * Description : Test method to test wrapper class
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testHelperMethod()
    {
        User oUser = [Select Id, Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            DynamicLinksWrapper_H_HUM oDynamiclink = new DynamicLinksWrapper_H_HUM();
            system.assertEquals('',oDynamiclink.LinkLabel);
        }
    }
}