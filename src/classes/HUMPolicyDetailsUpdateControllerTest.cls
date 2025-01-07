/***********************************************************************************************************
Apex Class Name  : HUMPolicyDetailsUpdateControllerTest
Version          : 1.0
Created Date     : Aug 4 2014
Function         : Test class for HUMPolicyDetailsUpdateController
 ************************************************************************************************************

Modification Log:

 * Developer Name           Review Number              Date                       Description
 *-----------------------------------------------------------------------------------------------------------
 * Shreya Choodamani         17432                   08/19/2014                   Original Version
 * Anil Pilaka               18484                   10/13/2014                   Updated test class for GBE call
 * Chaitanya Kumar           18889                   10/30/2014                   Created invokeGBE service,UpdatePolicyrecord
 * Ranjeeth Nagishetty       29395                   01/21/2016                   Created setupmethod and changed existing test methods and removed url in custom setting  value 
 * ChiranjeeviRao Ravuri     29201                   06/06/2016                   Updated test class as per REQ - 269948.
 * Pallavi R                                         03/25/2017                   Updated test class as per REQ 304134
 * Ajay Chakradhar                                   08/14/2020                   Updated test class to cover HUMPolicyDetailsUpdateController_R1 class 
 ********************************************************************************************************************************************************************/

@isTest
private class HUMPolicyDetailsUpdateControllerTest
{
     /*
     * Method Name : setup
     * Description : This method is used to insert the data only once and called automicticaly in all methods
     * Return type : NA
     * Paramater   : NA
     */
    @testSetup
    static void testSetup() 
    {  
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'?Value not matched');
         
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        system.assertNotEquals(lstApplicationName,null,'?Value not matched');
        
        Account acc = HUMTestDataHelperTest.getAccount();
        acc.OwnerId=oUser.id;
        acc.Name = 'Test Account Cases';
        acc.Enterprise_ID__c = '45678';
        acc.Group_Number__c = '12345678';
        acc.Source_Customer_Key__c ='234234324234232';
        insert acc;
        system.assertEquals(acc.id!=null,true);

        Product2 prod = new Product2();
        prod.Name='ProductType';
        prod.Sold_Product_Key_Value__c ='prodKey1234';
        insert prod;
        system.assertNotEquals(prod.Id,null,'?Value not matched');

        Policy__c policy=HUMTestDataHelperTest.getPolicy();
        policy.Name = 'TestPolicy';
        policy.Group_Name__c=acc.id;
        policy.Exchange_Indicator__c = true;
        policy.Source_Cust_Cov_Key__c='2234233432423423422323';
        insert policy;
        system.assertEquals(policy.id!=null,true);

        Policy_Member__c policymember = HUMTestDataHelperTest.getPolicyMember();
        policymember.Relationship__c ='Dependent';
        policymember.Effective_Date__c = '12/2/2014';
        policymember.End_Date__c = '12/12/2014';
        policymember.Member__c=acc.id;
        policymember.Policy__c=policy.id;
        policymember.Name='ABC12345';
        insert policymember;
        system.assertEquals(policymember.id!=null,true);
        
        Policy_Plan_Details__c policyplandetails1 = new Policy_Plan_Details__c();
        policyplandetails1.Policy_Plan_External_ID__c = '12345';
        policyplandetails1.Coverage_Plan_Effective_Date__c = '12/2/2014';
        policyplandetails1.Coverage_Plan_End_Date__c = '12/12/2014';
        policyplandetails1.Policy__c = policy.id;
        policyplandetails1.Name = 'policyplan1';
        insert policyplandetails1;
        system.assertEquals(policyplandetails1.id!=null,true);

        insert new HUM_Webservice_Callout__c( Name = 'GroupBusinessEntityService' , Certificate_Name__c = 'Humana_GuidanceCare', Active__c = true, End_Point_URL__c = 'https://www.google.com/', Timeout__c = '12000'); 
    }

    /*  
     * Method name : testupdatePolicyRecord
     * Description : This Method is used to invoke GBE Service and to update pocily and account details based on service output.
     * Return Type : Void 
     * Parameter   : NA
     */
    static testMethod void testupdatePolicyRecord()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'?Value not matched'); 

        System.runAs(oUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new GBEServiceMockResponse_T_HUM());
            
            List<Policy_Member__c> lstPolicyMembers = [select
                                                       Effective_Date__c,
                                                       End_Date__c,
                                                       Policy__r.Product__r.Sold_Product_Key_Value__c,
                                                       Policy__r.Group_Name__r.Source_Customer_Key__c,
                                                       Policy__r.Platform__c,Policy__r.Source_Cust_Cov_Key__c,
                                                       Group_Number__c,
                                                       Policy__r.Last_GBE_Call_Date__c,
                                                       Policy__c 
                                                       from Policy_Member__c 
                                                       where Name = 'ABC12345'];
            system.assertEquals(lstPolicyMembers !=null,true);
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',lstPolicyMembers[0].Id); 
            HUMPolicyDetailsUpdateController hum = new HUMPolicyDetailsUpdateController();
            hum.policyMember  = lstPolicyMembers[0];
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
            groupInfo.AsOfDate = '01/01/2016';
            groupInfo.EffectiveDate = '01/01/2015';
            groupInfo.Enddate = '01/01/2016';
                GBEServiceResponse_H_HUM.Exchange exchange = new GBEServiceResponse_H_HUM.Exchange();
                exchange.ExchangeName = 'abc';
                exchange.ExchangeType = 'TestType';
                exchange.ExchangeSegment  = 'LargeGroup';
                exchange.MetallicTier = 'Testmetalictier'; 
                exchange.LineOfBusiness = 'Commercial';  
                exchange.IsHumanaOne = 'false';
            groupInfo.Exchange = exchange;
            oResponse.GroupInfo = groupInfo;   
            hum.getGBEInformationAsync();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponse'; 
            hum.processResponse();
            hum.updateGBEPolicyData(oResponse);
            Test.stopTest();
        }
    }

    /*  
     * Method name : testupdatePolicyRecordR1
     * Description : This Method is used to invoke GBE Service and to update pocily and account details based on service output.
     * Return Type : Void 
     * Parameter   : NA
     */
    static testMethod void testupdatePolicyRecordR1()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'?Value not matched'); 

        System.runAs(oUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new GBEServiceMockResponse_T_HUM());
            
            List<Policy_Member__c> lstPolicyMembers = [select
                                                       Effective_Date__c,
                                                       End_Date__c,
                                                       Policy__r.Product__r.Sold_Product_Key_Value__c,
                                                       Policy__r.Group_Name__r.Source_Customer_Key__c,
                                                       Policy__r.Platform__c,Policy__r.Source_Cust_Cov_Key__c,
                                                       Group_Number__c,
                                                       Policy__r.Last_GBE_Call_Date__c,
                                                       Policy__c 
                                                       from Policy_Member__c 
                                                       where Name = 'ABC12345'];
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.StandardController scon2 = New ApexPages.StandardController(lstPolicyMembers[0]);
            ApexPages.currentPage().getParameters().put('Id',lstPolicyMembers[0].Id); 
            HUMPolicyDetailsUpdateController_R1 hum = new HUMPolicyDetailsUpdateController_R1(scon2);
            hum.policyMember  = lstPolicyMembers[0];
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
            groupInfo.AsOfDate = '01/01/2016';
            groupInfo.EffectiveDate = '01/01/2015';
            groupInfo.Enddate = '01/01/2016';
            GBEServiceResponse_H_HUM.Exchange exchange = new GBEServiceResponse_H_HUM.Exchange();
            exchange.ExchangeName = 'abc';
            exchange.ExchangeType = 'TestType';
            exchange.ExchangeSegment  = 'LargeGroup';
            exchange.MetallicTier = 'Testmetalictier'; 
            exchange.LineOfBusiness = 'Commercial';  
            exchange.IsHumanaOne = 'false';
            groupInfo.Exchange = exchange;
            oResponse.GroupInfo = groupInfo;   
            hum.getGBEInformationAsync();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponse'; 
            hum.processResponse();
            hum.updatePolicyDetails();
            hum.updateExchangeDetails(true);
            hum.updateGBEPolicyData11(oResponse);
            hum.updatePolicy(true);
            system.assertEquals(lstPolicyMembers !=null,true);
            Test.stopTest();
        }
    } 
}