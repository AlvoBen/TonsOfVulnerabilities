/*******************************************************************************************************************************
Apex Class Name : BillingProfileDetails_T_HUM
Version         : 1.0
Created On      : 12/03/2015
Function        : Test class for BillingProfileDetails_H_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            29052                       12/19/2015                 Original version
* Santhi Mandava            29422                       01/24/2016                 Renamed class and replaced BillingProfileWrapper_H_HUM class name with BillingProfile_DTO_HUM
*******************************************************************************************************************************/

@isTest
private class BillingProfileDetails_T_HUM
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
           // BillingProfileDetails_H_HUM obj = new BillingProfileDetails_H_HUM();
            List<SelectOption> lstStatus = BillingProfileDetails_H_HUM.getProfileStatusValues();
            System.assertequals(3,lstStatus.size());

            Account acc = [select id,Source_Platform_Code__c,name,Recordtypeid  from Account where name = 'test'];
            System.assertEquals('test',acc.name);
            
            PageReference pageRef = Page.GroupBillingProfile_VF_HUM;
            Test.setCurrentPage(pageRef);
            ApexPages.CurrentPage().getParameters().put('id',acc.id);
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(acc);
            GroupBillingProfile_C_HUM objMain = new GroupBillingProfile_C_HUM(oStdController);
            System.assertEquals('test',((Account)objMain.objRecordData).get('Name'));
            
            BillingProfile_DTO_HUM objWrapper = BillingProfileDetails_H_HUM.getBillingProfilesInput('Account',objMain ,'','');
            System.assertEquals('SXS2222',objWrapper.sUserID);
            
            List<BillingProfile_DTO_HUM> lstValues = BillingTestDataSetup_T_HUM.createBillingWrapperList();
            List<BillingProfile_DTO_HUM> lstTemp = BillingProfileDetails_H_HUM.Search('All','',lstValues );
            System.assertEquals(lstValues.size(),lstTemp.size());
            
            lstTemp  = BillingProfileDetails_H_HUM.Search('Active','',lstValues );
            System.assertEquals(4,lstTemp.size());
            
            lstTemp = BillingProfileDetails_H_HUM.Search('All','12/31/2014',lstValues );
            System.assertEquals(0,lstTemp .size());
            
            lstTemp = BillingProfileDetails_H_HUM.Search('Active','12/31/2014',lstValues );
            System.assertEquals(0,lstTemp.size());
            
            lstTemp = BillingProfileDetails_H_HUM.Search('Termed','12/31/2014',lstValues );
            System.assertEquals(0,lstTemp.size());
            
            objWrapper = BillingProfileDetails_H_HUM.getBillingProfilesInput('Account',objMain ,'12345','ACCOUNT');
            System.assertEquals('12345',objWrapper.sSearchID);
        }
    }
}