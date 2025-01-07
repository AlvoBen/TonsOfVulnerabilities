/*******************************************************************************************************************************
Apex Class Name : GroupBillingProfile_T_HUM
Version         : 1.0
Created On      : 11/30/2015
Function        : Test class for GroupBillingProfile_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            29052                       11/30/2015                 Original version
* Santhi Mandava            29422                       01/24/2016                 Corrected class name as per standards
* Santhi Mandava                                        09/19/2016                 Implemented billing logging.
* Prudhvi Pamarthi          163987                      01/02/2018                 REQ - 328929 Fix for HUMExceptionHelper: DML
*******************************************************************************************************************************/

@isTest
private class GroupBillingProfile_T_HUM
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
            Account acc = [select id,Source_Platform_Code__c,Source_Customer_Key__c,name,Recordtypeid  from Account where name = 'test'];
            System.assertEquals('test',acc.name);
            
            PageReference pageRef = Page.GroupBillingProfile_VF_HUM;
            Test.setCurrentPage(pageRef);
            ApexPages.CurrentPage().getParameters().put('id',acc.id);
            
            ApexPages.StandardController oStdController = null;
            GroupBillingProfile_C_HUM obj = new GroupBillingProfile_C_HUM(oStdController);
            oStdController = new ApexPages.StandardController(acc);
            obj = new GroupBillingProfile_C_HUM(oStdController);
            System.assertEquals('test',((Account)obj.objRecordData).get('Name'));
            String sSRcCovId = obj.getsSrcCustKey();
            String sRecID = obj.getsRecId();
            GroupBillingProfile_C_HUM obj1 = obj.getThis();
            System.assertEquals('EM',((Account)obj.objRecordData).get('Source_Platform_Code__c'));
             List<Error_Log__c> lstLog = new List<Error_Log__c>();            
            Error_Log__c error = new Error_Log__c(Status__c = 'GroupBillingProfile');            
            lstLog.add(error);                        
            obj.listErrorLogs = lstLog;            
            System.assert(obj.listErrorLogs.size() > 0);
        }
    }
}