/*--
File Name        : LoggingCaseDataTable_T_HUM.cls
Version          : 1.0 
Created Date     : 01/04/2022   
Function         : Test Class for LoggingCaseDataTable_C_HUM

Modification Log :
* Developer                          Date                  Description
*************************************************************************************************
* Kiran Bhuvanagiri                  01/04/2022            User Story 2759501: Original Version
**************************************************************************************************/
@isTest
public class LoggingCaseDataTable_T_HUM {
    
    @isTest static void testQueryCases(){        
		LoggingCaseDataTable_C_HUM.CasesWrapper caseList = new LoggingCaseDataTable_C_HUM.CasesWrapper();
        
        Account acc = new Account();
        acc.Name = 'Test Account';
        insert acc;
        
        Policy__c newp = new Policy__c();
        newp.Group_Name__c = acc.Id;
        insert newp;
        
        Policy_Member__c newPolicy = new Policy_Member__c();
        newPolicy.Policy__c = newp.Id;
        insert newPolicy;
        //String startDate = '2020-01-01';
        String startDate = '2010-11-17T12312';
        String endDate = '2021-01-01T1212';
        String caseNumber = '123123';
        String policyid = newPolicy.Id;
        Test.startTest();
            caseList = LoggingCaseDataTable_C_HUM.queryCases(policyid, caseNumber, startDate, endDate);
        Test.stopTest();
        System.assertNotEquals(null, caseList, 'System should return caselist');
    }

}