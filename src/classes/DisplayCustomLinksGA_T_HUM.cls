/*******************************************************************************************************************************
Apex Class Name : DisplayCustomLinksGA_T_HUM
Version         : 1.0
Created On      : 04/17/2020
Function        : Test class for DisplayCustomLinksGA_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Muraidhar Kollu                                      04/17/2020                 Original version
*******************************************************************************************************************************/

@isTest
private class DisplayCustomLinksGA_T_HUM
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
        User oUser = [Select Id, Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            DisplayCustomLinksGA_C_HUM oDisp = new DisplayCustomLinksGA_C_HUM();
            
            DisplayCustomLinksGA_C_HUM oRetDisp = oDisp.getThis();
            System.assertEquals(oRetDisp, oDisp);
            
            list<sObject> lstObj = oDisp.getRecords();
            System.assertEquals(lstObj, NULL);
            
            sObject sObj = oDisp.getRecord();
            System.assertEquals(sObj, NULL);
            
            PageReference oPageRef = Page.DisplayCustomLinksGA_VF_HUM;
            system.Test.setCurrentPage(oPageRef);
            
           
            Account oAcc = [Select Id, Name From Account Where Name = 'test'];
            System.assertEquals(oAcc.Name, 'test');
        }
    }
}