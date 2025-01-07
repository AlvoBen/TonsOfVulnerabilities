/***********************************************************************************************************
Apex Class Name  : PolicyDetailsUpdateService_T_HUM
Version          : 1.0
Created Date     : March 25th 2017
Function         : Test class for PolicyDetailsUpdate_S_HUM
 ************************************************************************************************************

Modification Log:

 * Developer Name           Review Number              Date                       Description
 *-----------------------------------------------------------------------------------------------------------
 * Pallavi R                                         03/16/2017               Original Version - REQ 304134
 ********************************************************************************************************************************************************************/

@isTest
private class PolicyDetailsUpdateService_T_HUM 
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

        insert new HUM_Webservice_Callout__c( Name = 'GroupBusinessEntityService' , Content_Type__c='application/json', Certificate_Name__c = 'Humana_GuidanceCare', Active__c = true, Request_Type__c='POST', End_Point_URL__c = 'https://www.google.com/', Timeout__c = '12000'); 
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
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c,
            Request_Type__c from HUM_Webservice_Callout__c where name='GroupBusinessEntityService' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'Humana_GuidanceCare');
            
            Policy_Plan_Details__c oPolicyPlans = [select id,Exchange__c,Business_Segment__c,Exchange_Indicator__c,Exchange_Type__c,
                                                           Metallic_Tier__c,Coverage_Plan_Effective_Date__c,Coverage_Plan_End_Date__c,
                                                           Product__r.Sold_Product_Key_Value__c,Policy__r.Group_Name__r.Source_Customer_Key__c,
                                                           Platform__c,Source_Cust_Cov_Key__c,Group_Number__c,Policy__r.Last_GBE_Call_Date__c,
                                                           Policy__c,Policy__r.Group_Name__c,Last_GBE_Call_Date__c from Policy_Plan_Details__c 
                                                           where Name = 'policyplan1' LIMIT 1];
             System.assert(oPolicyPlans.id!=null);                                              
            Test.setCurrentPage(Page.HUMPolicyMemberDetailPage);
            ApexPages.currentPage().getParameters().put('Id',lstPolicyMembers[0].Id);           
            HUMPolicyDetailsUpdateController hum = new HUMPolicyDetailsUpdateController();
            hum.policyMember  = lstPolicyMembers[0];
            
            PolicyDetailsUpdate_S_HUM oPolService = new PolicyDetailsUpdate_S_HUM();
            HttpRequest oRequest = new HttpRequest();
            oRequest = oPolService.setupRequest(orequest, oServiceCallout, oPolicyPlans, lstPolicyMembers[0] );
            Test.stopTest();
        }
    } 
}