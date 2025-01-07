/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicUpdateController_LTNG_T_HUM
    @Version          : 1.0
    @Created Date     : May 13th, 2020
    @Function         : Test class for DemographicUpdateController_LTNG_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  13/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
public class DemographicUpdateController_LTNG_T_HUM
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
    * Method to test success response for Demographicupdate
    * @param        None
    * @return       None
    */
    @istest static void Demographicupdatecontrollerscnarios()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Case cseRecord = [SELECT Id, AccountId, RecordTypeId FROM Case where subject = 'Test Subject' limit 1];
        Account testAccount = [Select Id,Name,Enterprise_ID__c from Account where Name = 'Test Account' Limit 1];
        Policy_Member__c pmObj = [Select id from Policy_Member__c where Name = 'testpolmem11' limit 1];
        List<String> lstPMObj = new List<String>();
        lstPMObj.add(pmObj.id);
        system.runAs(testUser)
        {
            Test.startTest();
            string groupRecordObject = '{"productTypeCode":"PDP","productType":"MPD","product":"MED","platformCode":"LV","majorLOB":"","groupId":"237877","exchangeType":"","exchangeIndicator":"false","ediGroupIndicator":"false","dualDemoIndicator":"false","asoIndicator":"false"}';
            DemographicUpdateController_LTNG_C_HUM.getAccountDetail(cseRecord.id);
            DemographicUpdateController_LTNG_C_HUM.getPlanID(cseRecord.AccountId);
            String result = string.valueOf(DemographicUpdateController_LTNG_C_HUM.fetchStateNames());
            DemographicUpdateController_LTNG_C_HUM.getEligiblePolicyMemberIds(groupRecordObject,testAccount.Enterprise_ID__c);
            DemographicUpdateController_LTNG_C_HUM.getEligibleDetailsList(lstPMObj, true);
            system.assertNotEquals(null, result);
            Test.stopTest();
        }
	}

    /**
    * This method is used to Test get Eligible Polic Memver ids
    *
    * @param     NULL
    * @return    void 
    */
     @istest static void geteligiblepolicymemberid()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Case cseRecord = [SELECT Id, AccountId, RecordTypeId FROM Case where subject = 'Test Subject' limit 1];
        system.runAs(testUser)
        {
            Test.startTest();
            String Policymemberid=DemographicUpdateController_LTNG_C_HUM.getEligiblePolicyMemberId('Test String', cseRecord.id);
            system.assertNotEquals(Null, Policymemberid); 
            Test.stopTest();
        }
        
    }   
    
    /**
    * This method is used to Test Get Eligible Details
    *
    * @param     NULL
    * @return    void 
    */
    @istest static void GetEligibleDetails()
    {
        Account objMemAccount = HUMTestDataHelperTest.getAccount();
        objMemAccount.FirstName = 'Test FName';
        objMemAccount.LastName = 'LName'; 
        objMemAccount.source_platform_code__c = 'EM' ;
        objMemAccount.Enterprise_Id__c = String.valueof(2000);
        objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
        insert objMemAccount;
        system.assertEquals(objMemAccount.LastName == 'LName', true);
        // Policy
        Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
        oMedicalPolicy.Name = 'Medical Product';
        oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
        oMedicalPolicy.Group_Name__c = objMemAccount.Id;
        oMedicalPolicy.Major_LOB__c ='EPO';      
        oMedicalPolicy.Client_Number__c='15';
        insert oMedicalPolicy;
        system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
        // Policy Member
        Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
        objMedPolMem.Member__c = objMemAccount.Id;
        objMedPolMem.name = '12591';
        objMedPolMem.Policy__c = oMedicalPolicy.Id;
        objMedPolMem.Relationship__c = 'self';    
        objMedPolMem.end_date__c = '12/31/2015'; 
        objMedPolMem.Effective_Date__c='01/01/2015';       
        objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
        insert objMedPolMem;
        system.assertEquals(objMedPolMem.Relationship__c == 'self',true);
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
            
        system.runAs(testUser)
        {
            Test.startTest();
            Map<string,String>eligibledetails=DemographicUpdateController_LTNG_C_HUM.getEligibleDetails(objMedPolMem.id,true);
            DemographicUpdateController_LTNG_C_HUM.getGenericsForRequest(5);
            system.assertNotEquals(Null, testUser);
            Test.stopTest();
        }
    }
}