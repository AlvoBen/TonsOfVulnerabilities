/*******************************************************************************************************************************
Apex Class Name : Go365Links_XT_HUM
Version         : 1.0
Created On      : 10/23/2015
Function        : Test class for Go365Links_X_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar              27484                       10/23/2015                 Original version
*******************************************************************************************************************************/

@isTest
private class Go365Links_XT_HUM
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
            //Creating Account Record
                Account acc1 = HUMTestDataHelperTest.getAccount();
                acc1.Name = 'Test Account Cases1';
                acc1.Birthdate__c='01/15/1951';
                acc1.Enterprise_ID__c = '1005447107';
                insert acc1;
                system.assertEquals(acc1.Id != Null,true);
                system.assertEquals(acc1.Name == 'Test Account Cases1',true);

                //Creating Policy Record
                Policy__c pol = HUMTestDataHelperTest.getPolicy();
                pol.Group_Name__c = acc1.Id;
                pol.Name = 'Test Policy';
                insert pol;
                system.assertEquals(pol.Id != Null,true);
                system.assertEquals(pol.Name,'Test Policy');
                system.assertEquals(pol.Group_Name__c == acc1.Id,true);

                //Insert Policy Member
                Policy_Member__c polMember = HUMTestDataHelperTest.getPolicyMember();
                polMember.Name='Policy Member PM1';
                polMember.Policy__c=pol.Id;
                polMember.Member__c=acc1.Id;
                insert polMember;
                system.assertEquals(polMember.Id != Null,true);
                system.assertEquals(polMember.Name,'Policy Member PM1');
                system.assertEquals(polMember.Policy__c,pol.Id);
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
            Policy_Member__c polMember = [Select id,Name from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
       		Go365Links_X_HUM oDisp = new Go365Links_X_HUM(new ApexPages.StandardController(polMember));
            Go365Links_X_HUM oRetDisp = oDisp.getThis();
            System.assertEquals(oRetDisp, oDisp);
            
            list<sObject> lstObj = oDisp.getRecords();
            System.assertEquals(lstObj, NULL);
            
            sObject sObj = oDisp.getRecord();
            System.assertEquals(sObj, NULL);
            
            PageReference oPageRef = Page.Go365_VF_HUM;
            system.Test.setCurrentPage(oPageRef);
            
            String sPageName = oDisp.getCurrentPage();
            
            Account oAcc = [Select Id, Name From Account Where Name = 'test'];
            System.assertEquals(oAcc.Name, 'test');
        }
    }
}