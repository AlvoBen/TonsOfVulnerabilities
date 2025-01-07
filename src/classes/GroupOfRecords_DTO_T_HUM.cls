/*****************************************************************************************************************************************************************************
    @Apex Class Name  : GroupOfRecords_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : July 24, 2020
    @Function         : Test class for GroupOfRecords_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  24/07/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
public class GroupOfRecords_DTO_T_HUM {
    
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
         account.Enterprise_ID__c = '1102283421';
         insert account;
         
         Case cse = HUMTestDataHelperTest.getCase();
         cse.subject = 'Test Subject';
         cse.Description = 'Test Description';
         cse.AccountId = account.id;
         cse.RecordTypeId = RecordTypeID;
         insert cse;
         
         Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
         insert oProd;
         System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
         
         Policy__c objpol = HUMTestDataHelperTest.getPolicy();
         objpol.Name='testPolicy';
         objpol.Group_Name__c = account.Id;
         objpol.Major_LOB__c = 'MER';
         objpol.Source_Cust_Cov_Key__c = '123456789';
         objpol.Product__c = oProd.Id;
         objpol.Product__r = oProd;
         objpol.Platform__c = 'LV';
         insert objpol;
         system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
         
         Policy_Member__c objPolMem = new Policy_Member__c();
         objPolMem.Name = 'testpolmem11';
         objPolMem.Relationship__c = 'Self';
         objPolMem.Relationship_Code__c = '00';
         objPolMem.Subscriber__c = objPolMem.Id;
         objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
         objPolMem.End_Date__c=Date.today().addDays(1).format();
         objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
         objPolMem.Member__c = account.Id;
         objPolMem.Policy__c = objpol.Id;
         objPolMem.Policy__r = objpol;
         insert objPolMem;
         system.assertEquals(objPolMem.Relationship__c, 'Self');
            
         
         User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
         insert oCCSUser;
         system.assertEquals(oCCSUser.LastName,'test User1');
    }
    /*
    * Method to test GroupOfRecords structure
    * @param        None
    * @return       None
    */
    Private Static testmethod void TestGroupOfRecords()
    {        
       	User testUser = [Select Id,Name,Lastname from User where LastName='test User1' Limit 1]; 
        Policy_Member__c pmObj = [Select id from Policy_Member__c where Name = 'testpolmem11' limit 1];
        
        system.assertEquals(testUser.Lastname == 'test User1',true); 
        system.runAs(testUser)
        {
            Test.startTest();
            
            EligiblityResponse_DTO_HUM.GroupRecord objGroupRecord = new EligiblityResponse_DTO_HUM.GroupRecord();
            objGroupRecord.groupId = 'Test groupId';
            objGroupRecord.dualDemoIndicator = 'Test dualDemoIndicator';
            objGroupRecord.platformCode = 'Test platformCode';
            objGroupRecord.ediGroupIndicator = 'Test ediGroupIndicator';
            objGroupRecord.exchangeIndicator = 'Test exchangeIndicator';
            objGroupRecord.exchangeType = 'Test exchangeType';
            objGroupRecord.product = 'Test product';
            objGroupRecord.productType = 'Test productType';
            objGroupRecord.productTypeCode = 'Test productTypeCode';
            objGroupRecord.asoIndicator = 'Test asoIndicator';
            objGroupRecord.majorLOB = 'Test majorLOB';
            
            GroupOfRecords_DTO_HUM objGroupOfRecords = new GroupOfRecords_DTO_HUM();
            GroupOfRecords_DTO_HUM.details objdetails = new GroupOfRecords_DTO_HUM.details();
            objDetails.platform = 'LV';
            objDetails.policyMemberId = pmObj.Id;
            objDetails.primary = true;
            objDetails.product = objGroupRecord;
            List<GroupOfRecords_DTO_HUM.details> lstDtls = new List<GroupOfRecords_DTO_HUM.details>();
            lstDtls.add(objDetails);
            
            objGroupOfRecords.Details = lstDtls;
            system.assertNotEquals(Null, objGroupOfRecords);
           Test.stopTest();
        }
    }
}