/*******************************************************************************************************************************
Apex Class Name : MemberBillingProfile_T_HUM 
Version         : 1.0
Created On      : 11/30/2015
Function        : Test class for MemberBillingProfile_C_HUM 

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            29052                       12/22/2015                 Original version
* Santhi Mandava            29422                       01/24/2016                 Corrected class name as per standards
* Santhi Mandava            40488                       09/19/2016                 Implemented billing logging.
*******************************************************************************************************************************/

@isTest
private class MemberBillingProfile_T_HUM 
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
            Policy_Member__c objPolcyMem = [select id,Source_Coverage_ID__c ,Policy_Platform__c from Policy_Member__c where Source_Coverage_ID__c = '12345'];
            System.assertEquals('12345',objPolcyMem.Source_Coverage_ID__c );
            
            PageReference pageRef = Page.MemberBillingProfile_VF_HUM;
            Test.setCurrentPage(pageRef);
            ApexPages.CurrentPage().getParameters().put('id',objPolcyMem.id);
            
            ApexPages.StandardController oStdController = null;
            MemberBillingProfile_C_HUM obj = new MemberBillingProfile_C_HUM(oStdController);
            oStdController = new ApexPages.StandardController(objPolcyMem);
            obj = new MemberBillingProfile_C_HUM(oStdController);
            System.assertEquals('12345',((Policy_Member__c)obj.objRecordData).get('Source_Coverage_ID__c'));
            
            MemberBillingProfile_C_HUM obj1 = obj.getThis();
            String sRecId = obj.getsRecId();
            String SrcCovId = obj.getsSrcCoveId();
            System.assertEquals('LV',((Policy_Member__c)obj.objRecordData).get('Policy_Platform__c'));
            
            
        }
    }
}