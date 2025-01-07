/*******************************************************************************************************************************
Apex Class Name : Variable_HT_HUM
Version         : 1.0
Created On      : 10/23/2015
Function        : Test class for Variable_H_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar              27372                       10/23/2015                 Original version
*******************************************************************************************************************************/

@isTest
private class Variable_HT_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        system.runAs(oUser)
        {
            TestDataSetup_H_HUM.setupCustomLinksData();
        }
    }
     
    /*
    * Method Name : testCreateVariable
    * Description : This method is used to test the main class functionality
    * Return type : void
    * Parameter   : NA
    */ 
    static testMethod void testCreateVariable()
    {
        User oUser = [Select Id,Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU',oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            Expression_DI_HUM oExpr;
            
            Variable_H_HUM oVarHUM = new Variable_H_HUM('Link1');
            
            Expression_DI_HUM oRetExpr1 = oVarHUM.add(oExpr);
            System.assertEquals(oVarHUM.evaluate(), null);
            
            Expression_DI_HUM oRetExpr2 = oVarHUM.set('Link1', true);
            System.assertEquals(oVarHUM.value, true);
            
            Boolean bRetExpr = oVarHUM.evaluate();
            System.assertEquals(bRetExpr,true);
        }
    }
}