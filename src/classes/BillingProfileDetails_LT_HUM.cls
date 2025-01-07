/*******************************************************************************************************************************
Apex Class Name : BillingProfileDetails_LT_HUM
Version         : 1.0
Created On      : 05/23/2022
Function        : Test class for BillingProfileDetails_LH_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sagar Gulleve	                                       05/23/2022                 Original version
* 
*******************************************************************************************************************************/

@isTest
private class BillingProfileDetails_LT_HUM
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
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        system.runAs(oUser)
        {
            BillingTestDataSetup_T_HUM.createTestData();
        }
    }
    
    /*
    * Method Name : testfunctionality
    * Description : This method is used to test the group billing functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testfunctionality()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
           // BillingProfileDetails_H_HUM obj = new BillingProfileDetails_LH_HUM();
            List<SelectOption> lstStatus = BillingProfileDetails_LH_HUM.getProfileStatusValues();
            System.assertequals(3,lstStatus.size());

            Account acc = [select id,Source_Platform_Code__c,name,Recordtypeid  from Account where name = 'test'];
            System.assertEquals('test',acc.name);
 
            BillingProfile_LDTO_HUM objWrapper = BillingProfileDetails_LH_HUM.getBillingProfilesInput('SXS2222','LV','12345','1','ACCOUNT');
            System.assertEquals('SXS2222',objWrapper.sUserID);
            
            List<BillingProfile_LDTO_HUM> lstValues = BillingTestDataSetup_LT_HUM.createBillingWrapperList();
            List<BillingProfile_LDTO_HUM> lstTemp = BillingProfileDetails_LH_HUM.Search('All','',lstValues );
            System.assertEquals(lstValues.size(),lstTemp.size());
            
            lstTemp  = BillingProfileDetails_LH_HUM.Search('Active','',lstValues );
            System.assertEquals(4,lstTemp.size());
            
            lstTemp = BillingProfileDetails_LH_HUM.Search('All','12/31/2014',lstValues );
            System.assertEquals(0,lstTemp .size());
            
            lstTemp = BillingProfileDetails_LH_HUM.Search('Active','12/31/2014',lstValues );
            System.assertEquals(0,lstTemp.size());
            
            lstTemp = BillingProfileDetails_LH_HUM.Search('Termed','12/31/2014',lstValues );
            System.assertEquals(0,lstTemp.size());
            
            objWrapper = BillingProfileDetails_LH_HUM.getBillingProfilesInput('SXS2222','LV','12345','','ACCOUNT');
            System.assertEquals('12345',objWrapper.sSearchID);
        }
    }
}