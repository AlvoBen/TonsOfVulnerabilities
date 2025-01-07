/*******************************************************************************************************************************
Apex Class Name :  SearchCodeSet_LT_HUM.cls
Version: 1.0
Created On      : Jan 06,2023
Function        : Class contains test methods for SearchCodeSet_LC_HUM Class.

Modification Log :  
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Aishwarya Pawar                             		   01/06/2023                US - 4080893 
********************************************************************************************************************************/

@isTest
Private Class SearchCodeSet_LT_HUM {
  @testSetup static void setUpTestData()
     {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
        insert objUser;
        
      	system.runAs(objUser)
        {
            HealthCareProcedure humCode = new HealthCareProcedure();
            humCode.Code='NBRE';
            humCode.CodeDescription='Add to pharmacy lifetime maximum';
            humCode.Name='C-2127';
            humCode.Status__c='Active';
            humCode.Code_External_ID__c = 'ExternalID test';
            insert humCode;
            system.assertEquals(humCode.Code == 'NBRE', true);
        }
 }
    
     Static testMethod void testsearchDetailsForCodesOnly()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        HealthCareProcedure humCodes = [SELECT Id, Name, Code, CodeDescription, Status__c FROM HealthCareProcedure limit 1]; 
        System.assertEquals(humCodes.Name,'C-2127');
        System.runAs(testUser)
        {
            Test.startTest();
            SearchCodeSet_LC_HUM searchCodeObj = new SearchCodeSet_LC_HUM();
            SearchCodeSet_LC_HUM.searchDetailsForCodesOnly('"NBRE"');
            SearchCodeSet_LC_HUM.searchDetailsForCodesOnly('NBRE');
            Test.stopTest();  
        }
    }
     Static testMethod void testsearchDetailsForAll()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        HealthCareProcedure humCodes = [SELECT Id, Name, Code, CodeDescription, Status__c FROM HealthCareProcedure limit 1]; 
        System.assertEquals(humCodes.Name,'C-2127');
        System.runAs(testUser)
        {
            Test.startTest();
            SearchCodeSet_LC_HUM.searchDetailsForAll('ExternalID');
            Test.stopTest();  
        }
    }
            

}