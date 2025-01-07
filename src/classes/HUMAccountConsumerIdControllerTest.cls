/****************************************************************************************
Apex Class Name  : HUMAccountConsumerIdControllerTest
Version          : 1.0 
Created Date     : October 28 2014
Function         : Test class to HUMAccountConsumerIdController Apex class
Modification Log :
 *  Developer       Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 *  Manuel Johnson   18823               10/28/2014             Original Version  
 *  Santhi Mandava                       03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
 *******************************************************************************************/
@isTest
private class HUMAccountConsumerIdControllerTest
{
    static testMethod void memberTest() {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.runAs(u)
        {
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.Name = 'Test';
            acc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HumProviderRecordType).getRecordTypeId();
            acc.OwnerId = u.Id;
            insert acc;
            system.assertEquals(acc.id!=null, true);
                        
            ConsumerID__c conId = new ConsumerID__c();
            conId.Account__c = acc.Id;
            conId.Consumer_ID__c = '123456789';
            conId.ID_Type__c = 'TAXID';
            insert conId;
            system.assertEquals(conId.id!=null, true);
            
            HUMCipherCloudSettings__c cipherCloudSetting = new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME' , Value__c = 'test');
            insert cipherCloudSetting;
             
            Test.startTest();
                ApexPages.StandardController stdCntr = new ApexPages.StandardController(acc);
                HUMAccountConsumerIdController ctrl = new HUMAccountConsumerIdController(stdCntr);
                ctrl.retrieveConsumerIds();
                system.assert(ctrl.consumerIdsList.size() > 0);
            Test.stopTest();
        
        }
    }  
}