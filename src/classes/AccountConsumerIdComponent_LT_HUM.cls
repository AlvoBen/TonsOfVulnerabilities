/**************************************************************************** 
Apex Class Name  : AccountConsumerIdComponent_LT_HUM 
Version          : 1.0  
Function         : This class serves as test class for AccountConsumerIdComponent_LC_HUM

 * Developer Name           Code Review                  Date                       Description
 *---------------------------------------------------------------------------------------------------------------------
 * Joel George                                           03/03/2021                 Original Version
********************************************************************************************/ 
@isTest
public class AccountConsumerIdComponent_LT_HUM
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

            String accId = acc.id;
             
            Test.startTest();
                list<ConsumerID__c> lstConsumer = AccountConsumerIdComponent_LC_HUM.retrieveConsumerIds(accId);
                system.assertEquals(lstConsumer!=null, true);   
            Test.stopTest();
        
        }
    } 
}