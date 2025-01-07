/****************************************************************************************
Apex Class Name  : HUMPolicyMemberIdControllerTest
Version          : 1.0 
Created Date     : SEPTEMBER 18 2014
Function         : Test class to HUMPolicyMemberIdController Apex class
Modification Log :
 *  Developer               Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 *  Vishal Verma            17998                  09/15/2014            Original Version 
 *  Vishal Verma            19528                  11/25/2014            Updated since Member Card external id has been made mandatory on Member Id object 
 *  Raja Sekhar Reddy M V   20220                  01/03/2015            Modified code for REQ-88070.
 *  Santhi Mandava          233368                 24/05/2018            Created"ENCRYPTIONTYPE" custom setting value.
 *  Santhi Mandava                                 03/12/2019           Removed "DpaaSWebServiceMockImpl" usage.
 *  Santhi Mandava          355559                 05/10/2019           Removed "ENCRYPTIONTYPE" custom setting creation
 *******************************************************************************************/
@isTest
private class HUMPolicyMemberIdControllerTest
{
    static testMethod void memberTest() {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.runAs(u)
        {
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.FirstName = 'Test';
            acc.LastName ='Account Last';
            acc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            acc.OwnerId = u.Id;
            insert acc;
            system.assertEquals(acc.id!=null, true);
            
            Policy__c policy = HUMTestDataHelperTest.getPolicy();
            policy.Name = 'Test Policy';
            policy.Group_Name__c = acc.Id;
            policy.Platform__c = 'CB';
            insert policy;
            system.assertEquals(policy.id!=null, true);
            
            Policy_Member__c polMemObj = HUMTestDataHelperTest.getPolicyMember();
            polMemObj.Member__c = acc.Id;
            polMemObj.Policy__c = policy.Id;
            insert polMemObj;
            system.assertEquals(polMemObj.id!=null, true);

            Policy_Member__c polMem = HUMTestDataHelperTest.getPolicyMember();
            polMem.Member__c = acc.Id;
            polMem.Policy__c = policy.Id;
            polMem.Subscriber__c = polMemObj.Id; 
            insert polMem;
            system.assertEquals(polMem.id!=null, true);
            
            Member_ID__c memId = new Member_ID__c();
            memId.Policy__c = policy.Id;
            memId.Policy_Member__c = polMem.Id;
            memId.Name = 'Clear Text';
            memId.Type__c =  Label.HUMEESLegacyMemberSearchHelper_SSN;
            memId.Member_Card_External_ID__c = '1234';
            insert memId;
            system.assertEquals(memId.id!=null, true);
            
            HUMCipherCloudSettings__c cipherCloudSetting = new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME' , Value__c = 'test');
            insert cipherCloudSetting;
                        
            Test.startTest();
                ApexPages.StandardController stdCntr = new ApexPages.StandardController(polMem);
                HUMPolicyMemberIdController policyMemberId = new HUMPolicyMemberIdController(stdCntr);
                policyMemberId.retrieveMemberIds();
                system.assert(policyMemberId.policyMemberIdsList.size() > 0);
            Test.stopTest();
        }
    }  
}