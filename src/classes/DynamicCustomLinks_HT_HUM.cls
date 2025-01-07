/***************************************************************************************************
Apex Class Name  : DynamicCustomLinks_HT_HUM
Version          : 1.0
Created Date     : October 19 2015
Function         : Teat Class for DynamicCustomLinks_H_HUM 
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
* Ranjeeth Nagishetty                             10/19/2015                     Original Version
***************************************************************************************************/

@isTest
private class DynamicCustomLinks_HT_HUM
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
            DynamicCustomLinks_H_HUM oDynamiclink = new DynamicCustomLinks_H_HUM();
            system.assert(oDynamiclink!= null);
        }
    }
}