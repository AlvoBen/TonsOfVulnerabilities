/*******************************************************************************************************************************
Apex Class Name : DisplayCustomLinks_T_HUM
Version         : 1.0
Created On      : 10/23/2015
Function        : Test class for DisplayCustomLinks_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar              27484                       10/23/2015                 Original version
* T Prasanna Sai Kumar                                  03/22/2021                 Req 1912034 - Dynamic Links Vulnerabilities - DynamicCustomLinksHorizontal_CMP_HUM,DisplayCustomLinks_C_HUM,DisplayCustomLinks_T_HUM
*******************************************************************************************************************************/

@isTest
private class DisplayCustomLinks_T_HUM
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
    @isTest static void testCreateVariable()
    {
        User oUser = [Select Id, Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            DisplayCustomLinks_C_HUM oDisp = new DisplayCustomLinks_C_HUM();
            list<Sobject> lstOfSobject = new List<sobject>();
        
            DisplayCustomLinks_C_HUM oRetDisp = oDisp.getThis();
            System.assertEquals(oRetDisp, oDisp);
            
            list<sObject> lstObj = oDisp.getRecords();
            system.assertNotEquals(lstObj, null);
            System.assertEquals(lstObj, lstOfSobject);
            
            sObject sObj = oDisp.getRecord();
            System.assertEquals(sObj, NULL);
            
            PageReference oPageRef = Page.DisplayCustomLinks_VF_HUM;
            system.Test.setCurrentPage(oPageRef);
            
            
            Account oAcc = [Select Id, Name From Account Where Name = 'test'];
            System.assertEquals(oAcc.Name, 'test');
        }
    }
}