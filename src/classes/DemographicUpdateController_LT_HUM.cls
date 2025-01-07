/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicUpdateController_LT_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @Test Class for   : DemographicUpdateController_LC_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar                                        06/09/2022                 Original Version
******************************************************************************************************************************************************************************/

@isTest
public class DemographicUpdateController_LT_HUM
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
         
         //Needs to be removed 
         Policy__c objpol1 = HUMTestDataHelperTest.getPolicy();
         objpol1.Name='testPolicy';
         objpol1.Group_Name__c = account.Id;
         objpol1.Major_LOB__c = 'MER';
         objpol1.Source_Cust_Cov_Key__c = '123456789';
         objpol1.Product__c = oProd.Id;
         objpol1.Product__r = oProd;
         objpol1.Platform__c = 'LV';
         insert objpol1;
         system.assertEquals(objpol1.Source_Cust_Cov_Key__c,'123456789');
         
         //new addition of PurchaserPlan in place of poliy__c
            PurchaserPlan objpol = new PurchaserPlan();
            //PolicyObjOI.Policy_Group_Number__c='11111';
            objpol.Name = 'testPolicy';
            objpol.Source_Cust_Cov_Key__c = '123456789' ; 
            objpol.PayerId = account.Id;
            objpol.Major_LOB__c ='MER';
            objpol.Platform__c = 'LV';
            objpol.ETL_Record_Deleted__c = false;
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Purchaser_Plan_External_ID__c='00|ODS|4900004374217|1|2021-11-01';
            objpol.Medicare_Segment_ID__c='000';
            objpol.Contract_Number__c='H1951';
            objpol.EffectiveFrom=Date.newInstance(2021,01,01);
            objpol.PBP_Code__c='801';
            
        

            insert objpol;  
         /*
      
         //below to be reomved
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
    */
         //new member plan
         //
         MemberPlan objPolMem = new MemberPlan();
         objPolMem.Name='testpolmem11';
         objPolMem.MemberId=account.id;
         objPolMem.RelationshipToSubscriber='Self';
         objPolMem.Relationship_Code__c = '00';
         objPolMem.SubscriberPlanId__c=objPolMem.Id;
         objPolMem.EffectiveTo= Date.parse(Date.today().addDays(20).format());
         objPolMem.EffectiveFrom=Date.parse(Date.today().addDays(10).format());
         objPolMem.Member_Plan_External_Id__c='00|ODS|1006495262|4900004374217|1|2021-11-01';
         //objPolMem.Member_Dependent_Code__c = '1234567890';
         //objPolMem.Member_Id_Base__c = 'test23456';
         objPolMem.GroupNumber = '737803';
         objPolMem.PlanId= objpol.Id;
         //objPolMem.Medicaid_Id__c='0008107741';
         objPolMem.ETL_Record_Deleted__c =false;
         //objPolMem.Status__c = 'Future';
         insert objPolMem;
         
         User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
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
        MemberPlan pmObj = [SELECT Id, Name, Member_Coverage_Status__c, IsDeleted,ETL_Record_Deleted__c,EffectiveFrom, EffectiveTo,Member_Plan_External_Id__c from MemberPlan where Name = 'testpolmem11' limit 1];
        system.debug('pmObj---'+pmObj);
        List<String> lstPMObj = new List<String>();
        lstPMObj.add(pmObj.id);
        system.runAs(testUser)
        {
            Test.startTest();
            string groupRecordObject = '{"productTypeCode":"PDP","productType":"MPD","product":"MED","platformCode":"LV","majorLOB":"","groupId":"237877","exchangeType":"","exchangeIndicator":"false","ediGroupIndicator":"false","dualDemoIndicator":"false","asoIndicator":"false"}';
            DemographicUpdateController_LC_HUM.getAccountDetail(cseRecord.id);
            DemographicUpdateController_LC_HUM.getPlanID(cseRecord.AccountId);
            String result = string.valueOf(DemographicUpdateController_LC_HUM.fetchStateNames());
            DemographicUpdateController_LC_HUM.getEligiblePolicyMemberIds(groupRecordObject,testAccount.Enterprise_ID__c);
            DemographicUpdateController_LC_HUM.getEligibleDetailsList(lstPMObj, true);
            DemographicUpdateController_LC_HUM.getEligibleDetailsList(null, false); 
            DemographicUpdateController_LC_HUM.preparePlanID(pmObj.Id);

            system.assertNotEquals(null, result);
            Test.stopTest();
        }
    }
     @istest static void Demographicupdatecontrollerscnarios1(){
      
        User testUser1 = [Select Id,Name from User where LastName='test User1' Limit 1];
        Case cseRecord1 = [SELECT Id, AccountId, RecordTypeId FROM Case where subject = 'Test Subject' limit 1];
        Account testAccount1 = [Select Id,Name,Enterprise_ID__c from Account where Name = 'Test Account' Limit 1];
        MemberPlan pmObj1 = [SELECT Id, Name, Member_Coverage_Status__c, IsDeleted,ETL_Record_Deleted__c,EffectiveFrom, EffectiveTo,Member_Plan_External_Id__c from MemberPlan where Name = 'testpolmem11' limit 1];
        system.debug('pmObj1---'+pmObj1);
        List<String> lstPMObj = new List<String>();
        lstPMObj.add(pmObj1.id);
        CRMFunctionality_ONOFF_Switch__c value1 = new CRMFunctionality_ONOFF_Switch__c();
        value1.Name = 'TemplateSwitch';
        value1.IsON__c = true;
        insert value1;
        CRMFunctionality_ONOFF_Switch__c value = CRMFunctionality_ONOFF_Switch__c.getInstance('TemplateSwitch');
        value.IsON__c = false;
        update value;
        Product2 oProd1 = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
         insert oProd1;
         Policy__c objpol1 = HUMTestDataHelperTest.getPolicy();
         objpol1.Name='testPolicy';
         objpol1.Group_Name__c = testAccount1.Id;
         objpol1.Major_LOB__c = 'MER';
         objpol1.Source_Cust_Cov_Key__c = '123456789';
         objpol1.Product__c = oProd1.Id;
         objpol1.Product__r = oProd1;
         objpol1.Platform__c = 'LV';
         objpol1.Medicare_Segment_ID__c = '000';
         objpol1.Contract_Number__c = 'H1951';
         objpol1.Coverage_Plan_Effective_Date__c =  '12/31/2015';
         objpol1.PBP_Code__c ='801';
         objpol1.Policy_External_ID__c = '00|ODS|4900004374217|1|2021-11-01';
         insert objpol1;
         system.assertEquals(objpol1.Source_Cust_Cov_Key__c,'123456789');
        system.runAs(testUser1)
        {
        DemographicUpdateController_LC_HUM.preparePlanID(pmObj1.id);
        DemographicUpdateController_LC_HUM.getGenericsForRequest(0);
         DemographicUpdateController_LC_HUM.getEligibleDetailsList(null, true);
          DemographicUpdateController_LC_HUM.getAccountDetail(null);
          }
        }
          @istest static void Demographicupdatecontrollerscnarios11(){
           DemographicUpdateController_LC_HUM.preparePlanID(null);
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
            String Policymemberid=DemographicUpdateController_LC_HUM.getEligiblePolicyMemberId('Test String', cseRecord.id);
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
        PurchaserPlan oMedicalPolicy = new PurchaserPlan();
        oMedicalPolicy.Name = 'Medical Product';
        oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
        //oMedicalPolicy.Group_Name__c = objMemAccount.Id;
        oMedicalPolicy.Major_LOB__c ='EPO';      
        oMedicalPolicy.Client_Number__c='15';
        insert oMedicalPolicy;
        system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
        // Policy Member
        MemberPlan objMedPolMem = new MemberPlan();
        objMedPolMem.MemberId = objMemAccount.Id;
        objMedPolMem.name = '12591';
        objMedPolMem.PlanId = oMedicalPolicy.Id;
        objMedPolMem.RelationshipToSubscriber = 'self';    
        objMedPolMem.EffectiveTo = Date.parse('12/31/2015'); 
        objMedPolMem.EffectiveFrom= Date.parse('01/01/2015');       
        objMedPolMem.Member_Plan_External_Id__c = '00|ODS|123456789|89|89|02-01-2001';
        insert objMedPolMem;
        system.assertEquals(objMedPolMem.RelationshipToSubscriber == 'self',true);
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
            
        system.runAs(testUser)
        {
            Test.startTest();
            Map<string,String>eligibledetails=DemographicUpdateController_LC_HUM.getEligibleDetails(objMedPolMem.id,true);
            Map<string,String>eligibledetails1=DemographicUpdateController_LC_HUM.getEligibleDetails('//',true);

            DemographicUpdateController_LC_HUM.getGenericsForRequest(5);
            DemographicUpdateController_LC_HUM.getGenericsForRequest(1000);

            system.assertNotEquals(Null, testUser);
            Test.stopTest();
        }
    }
}