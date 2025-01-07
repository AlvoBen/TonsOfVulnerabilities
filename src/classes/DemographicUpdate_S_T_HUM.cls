/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicUpdate_S_T_HUM
    @Version          : 1.0
    @Created Date     : May 14th, 2020
    @Function         : Test class for DemographicUpdate_S_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  14/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class DemographicUpdate_S_T_HUM
{
    /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
     @testSetup static void prepareTestData() 
     {  
      	id RecordTypeID=Schema.SObjectType.case.getrecordtypeinfosByname().get('Medicare Case').getRecordtypeID();
         
        Account account = HUMTestDataHelperTest.getAccount();
        account.Name = 'Test Account';
        insert account;
         
        Case cse = HUMTestDataHelperTest.getCase();
        cse.subject = 'Test Subject';
        cse.Description = 'Test Description';
        cse.AccountId = account.id;
        cse.RecordTypeId = RecordTypeID;
        insert cse;
         
        User oSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oSUser;
      	System.runAs(osUser) 
        {
            HUM_Webservice_Callout__c objAddressStandardize = new HUM_Webservice_Callout__c();
            objAddressStandardize.Name = 'MBEPlusService';
            objAddressStandardize.Active__c = True;
            objAddressStandardize.Enable_Service_Tester__c = True;
            objAddressStandardize.Certificate_Name__c = 'Humana_GuidanceCare_Deimos';
            objAddressStandardize.Content_Type__c = 'application/json';
            objAddressStandardize.Request_Type__c = 'POST';
            objAddressStandardize.Timeout__c = '12000';
            objAddressStandardize.End_Point_URL__c = 'https://int-crmservicews.humana.com/MBEPLUSServiceREST/GetMember';
            insert objAddressStandardize;
        }
    }        
    /*
    * Method to test success response for DemographicUpdate
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessDemographicUpdate()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Case caserec=[select id,subject,description,Accountid from case where subject='Test Subject' limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            String personId = '';
            GenericHelper_CRMService_LTNG_H_HUM objGenericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
            personId = objGenericHelper.fetchPersonId(caserec.id);
            HttpRequest request = new HttpRequest();
            request = DemographicUpdate_S_HUM.getMemberMBERequest(personId);
            system.assertNotEquals(null, request);
            Test.StopTest();
        }
    }
}