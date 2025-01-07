/*******************************************************************************************************************************
Apex Class Name : CoachingUnknownMemberEdit_LT_HUM 
Version         : 1.0
Created On      : 06/04/2021
Function        : Test Class for CoachingUnknownMemberEdit_LC_HUM
                  
Modification Log: 
* Developer Name                    Code Review         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor                                         06/04/2021                 Initial version created for USER STORY 2081412.
************************************************************************************************************************************/
@isTest
private class CoachingUnknownMemberEdit_LT_HUM 
{
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('CoachUnknownMem','Humana Wellness Coach');        
        insert testUser;
        system.assertEquals(testUser.lastname =='test CoachUnknownMem',true);
    }

    static testMethod void testCreateUnknownMember()
    {
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test CoachUnknownMem'];
        System.assertEquals(testUser.LastName,'test CoachUnknownMem');

        System.runAs(testUser) 
        {
            Account acc = new Account();
            acc.FirstName = 'TestFN';
            acc.LastName = 'TestLN';
            acc.Birthdate__c = '01/01/2001';
            acc.PersonHomePhone = '(123) 456-7890';
            acc.PersonEmail = 'testemail@test.com';            
            acc.Company_Name__c = 'Test Company Name';
            acc.PersonMailingStreet = 'Test Street';
            acc.PersonMailingCity = 'Louisville';
            acc.PersonMailingStateCode = 'KY';
            acc.PersonMailingPostalCode = '12345';
            acc.PersonMailingCountryCode = 'US';
            String result = CoachingUnknownMemberEdit_LC_HUM.createUnknownMember(acc);
            system.assertNotEquals(null, result);

            CoachingUnknownMemberEdit_LC_HUM.ResponseWrapper response = new CoachingUnknownMemberEdit_LC_HUM.ResponseWrapper();
            response = (CoachingUnknownMemberEdit_LC_HUM.ResponseWrapper) JSON.deserialize(result, CoachingUnknownMemberEdit_LC_HUM.ResponseWrapper.class);
            system.assertEquals(true, response.status.equals('success'));
            system.assertNotEquals(null, response.accountId);

            List<Account> lstAcct = [SELECT Id, Account_External_Id__c, General_Account__c FROM Account WHERE ID = :response.accountId];
            System.assertEquals(false, lstAcct.isEmpty());
            System.assertEquals(true, String.isNotBlank(lstAcct[0].Account_External_Id__c));
            System.assertEquals(true, lstAcct[0].General_Account__c);
        }
    }

    static testMethod void testErrorScenario()
    {
        User testUser = [Select Id, Name, Current_Queue__C,LastName from User where LastName='test CoachUnknownMem'];
        System.assertEquals(testUser.LastName,'test CoachUnknownMem');

        System.runAs(testUser) 
        {
            Account acc = new Account();            
            String result = CoachingUnknownMemberEdit_LC_HUM.createUnknownMember(acc);
            system.assertNotEquals(null, result);

            CoachingUnknownMemberEdit_LC_HUM.ResponseWrapper response = new CoachingUnknownMemberEdit_LC_HUM.ResponseWrapper();
            response = (CoachingUnknownMemberEdit_LC_HUM.ResponseWrapper) JSON.deserialize(result, CoachingUnknownMemberEdit_LC_HUM.ResponseWrapper.class);
            system.assertEquals(true, response.status.equals('error'));
            system.assertNotEquals(null, response.errorMsg);            
        }
    }
}