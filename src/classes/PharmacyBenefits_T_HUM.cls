/*******************************************************************************************************************************
Apex Class Name : PharmacyBenefits_T_HUM.cls
Version         : 1.0
Created On      : June 28,2017
Function        : Class contains test methods PharmacyBenefits_C_HUM class.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Sharan Shanmugam                                 06/28/2017                   Original Version
 * Joel George              125710                  09/05/2017                   Req 322640 changes
 * Melkisan Selvaraj        224067	            04/24/2018			 REQ-335128 Added Event Type and Outcome Verbiage column
 * Lakshmi Madduri									10/30/2019				Added Incomm Service changes
 * Riya Agarwal									    30/04/2020				REQ - 729137 - MF 23 - Humana Pharmacy InComm- adding the hyperlink for the OTC ‘Benefit Type’ field when it equals ‘RxNova’
 * Sayali Nemade								    4/29/2020					REQ - 842472 - Pharmacy Incomm Service V2
 * Pinky Vijur                                           01/23/2023                User Story 3578587: T1PRJ0870026 Solutran Integrations - CRM FR5.01 - TECH - Remove CRM Service coding related to InComm OTC Balance
 ********************************************************************************************************************************/
@isTest
private class PharmacyBenefits_T_HUM 
{
  /**
    * @testSetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert objUser;
        system.assertEquals(objUser.lastname == 'test User1',true);
        HUM_Webservice_Callout__c PAAuthHubDetails = new HUM_Webservice_Callout__c(Name = 'PriorAuthHub',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://int-crmservicews.humana.com/PriorAuthHubService_V2.0/GetPriorAuthStatusHistory' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert PAAuthHubDetails;
        
        HUMTriggerSwitch__c btriggerSwitch = new HUMTriggerSwitch__c(Name='PharmacyBenefits',Exeute_Trigger__c=true);
        insert btriggerSwitch;
        system.assertEquals(btriggerSwitch.Name == 'PharmacyBenefits',true);
 
		system.runAs(objUser)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);
            //Product
            Product2 oproduct = new Product2();
            oproduct.Name='MED';
            oproduct.Product_Type__c='MA';           
            insert oproduct;
            system.assertEquals(oproduct.Name == 'MED', true);
            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';
            oMedicalPolicy.Platform__c='LV';
            oMedicalPolicy.Product__c=oproduct.Id;
            insert oMedicalPolicy;
            system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
            // Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            system.assertEquals(objMedPolMem.Relationship__c == 'self',true);
            
            Product2 oproduct2 = new Product2();
            oproduct2.Name='DEN';
            oproduct2.Product_Type__c='CA';           
            insert oproduct2;
            system.assertEquals(oproduct2.Name == 'DEN',true);
            
            Policy__c oMedicalPolicy2 = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy2.Name = 'Medical Product';
            oMedicalPolicy2.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy2.Major_LOB__c ='EPO';
            oMedicalPolicy2.Platform__c='CB';
            oMedicalPolicy2.Product__c=oproduct2.Id;
            insert oMedicalPolicy2;
            system.assertEquals(oMedicalPolicy2.Platform__c == 'CB',true);
            
			Policy_Member__c objMedPolMem2 = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem2.Member__c = objMemAccount.Id;
            objMedPolMem2.Policy__c = oMedicalPolicy2.Id;
            objMedPolMem2.Relationship__c = 'not self';  
            objMedPolMem2.end_date__c = '12/31/2015'; 
            objMedPolMem2.Effective_Date__c='01/01/2015';       
            insert objMedPolMem2;
            system.assertEquals(objMedPolMem2.Relationship__c == 'not self',true);
            
			//REQ-729137
			Member_ID__c objMemId = new Member_ID__c();
            objMemId.Policy_Member__c=objMedPolMem.id;
            objMemId.Policy__c=oMedicalPolicy.id;
            objMemId.Name='12345';
            objMemId.Type__c='Member-Id-Base';
            insert objMemId;
            system.assertEquals(objMemId.Type__c == 'Member-Id-Base',true);
			
            List<HUMConstants__c> lstConstanst = new List<HUMConstants__c>();
    
            lstConstanst.Add(new HUMConstants__c(Name='PromptPA URL',StringValue__c='ABC',IntegerValue__c=0));
            
            insert lstConstanst;
            System.assert(lstConstanst.size() > 0 );
        }
    } 
    
    /**
    * Test method for pharmacy data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testPolicyMember()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id,Relationship__c,name from policy_member__c where Relationship__c = 'self' limit 1];
        system.assertEquals(policyObj.Relationship__c,'self');  
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.PharmacyBenefits_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                PharmacyBenefits_C_HUM objPharmFin = new PharmacyBenefits_C_HUM();
                objPharmFin.getPolicyMemberData();
                system.assertEquals(policyObj.name,objPharmFin.objPolMem.name);  
            Test.stopTest();
        }
    }
    
    /**
    * Positive test method for Pharmacy Calls Authorization funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
  static testMethod void testPharmacyCallsAuthorization()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyBenefits_C_HUM pharmacyCreditCard = new PharmacyBenefits_C_HUM();
            PharmacyBenefits_S_HUM oServicePharmacy = new PharmacyBenefits_S_HUM();
            Continuation continueObj = (Continuation)PharmacyBenefits_C_HUM.invokeGetpAuthService('1014741145',accID);
            continueObj.continuationMethod = 'parseGetPAuthResponse';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            //String resBodyString= '{"GetPriorAuthStatusHistoryResponse":{"PriorAuthDetails":[{"ArgusAuthDetails":[{"AuthDescription":"PRE","AuthAddDate":"1/4/2012","AuthDecisionDate":"1/4/2012","DrugName":"SERTRALINEHCL"},{"AuthDescription":"PRE","AuthAddDate":"8/4/2010","AuthDecisionDate":"8/4/2010","DrugName":"HYDROCODONE/ACETAMINOPHEN"}]},{"AgadiaAuthDetails":[{"AuthDescription":"EOCABORTED","EOCCreationDate":"1/12/2012","EOCDecisionDate":"1/22/2012","DrugName":"BDINSULINSYR0.5ML30GX1/2""}]}]}}';
            String resBodyString= '{\"GetPriorAuthStatusHistoryResponse\":{\"PriorAuthDetails\":[{\"ArgusAuthDetails\":[{\"AuthDescription\":\"PRE\",\"AuthAddDate\":\"1/4/2012\",\"AuthDecisionDate\":\"1/4/2012\",\"DrugName\":\"SERTRALINEHCL\"},{\"AuthDescription\":\"PRE\",\"AuthAddDate\":\"8/4/2010\",\"AuthDecisionDate\":\"8/4/2010\",\"DrugName\":\"HYDROCODONE/ACETAMINOPHEN\"}]},{\"AgadiaAuthDetails\":[{\"AuthDescription\":\"EOCABORTED\",\"EOCCreationDate\":\"1/12/2012\",\"EOCDecisionDate\":\"1/22/2012\",\"DrugName\":\"BDINSULINSYR0.5ML30GX1/2\\\"\"}]}]}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            PharmacyBenefits_C_HUM.parseGetPAuthResponse(labelGetmember,continueObj);
            Test.stopTest();
        }
    }
    
    /**
    * Negative test method for Pharmacy Calls Authorization funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
  static testMethod void testCatchPharmacyCallsAuthorization()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyBenefits_C_HUM pharmacyCreditCard = new PharmacyBenefits_C_HUM();
            PharmacyBenefits_S_HUM oServicePharmacy = new PharmacyBenefits_S_HUM();
            delete lstWebCallout;
            Continuation continueObj = (Continuation)PharmacyBenefits_C_HUM.invokeGetpAuthService('1014741145',accID);
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{\"GetPriorAuthStatusHistoryResponse\":{\"PriorAuthDetails\":[{\"ArgusAuthDetails\":[{\"AuthDescription\":\"PRE\",\"AuthAddDate\":\"1/4/2012\",\"AuthDecisionDate\":\"1/4/2012\",\"DrugName\":\"SERTRALINEHCL\"},{\"AuthDescription\":\"PRE\",\"AuthAddDate\":\"8/4/2010\",\"AuthDecisionDate\":\"8/4/2010\",\"DrugName\":\"HYDROCODONE/ACETAMINOPHEN\"}]},{\"AgadiaAuthDetails\":[{\"AuthDescription\":\"EOCABORTED\",\"EOCCreationDate\":\"1/12/2012\",\"EOCDecisionDate\":\"1/22/2012\",\"DrugName\":\"BDINSULINSYR0.5ML30GX1/2\\\"\"}]}]}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            string reqType = '';
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            PharmacyBenefits_C_HUM.parseGetPAuthResponse(labelGetmember,continueObj);
            
            Test.stopTest();
        }
    }
  
	/**
    * Test method to cover GetPolicyMemberName() of PharmacyBenefits_C_HUM
    *<p>
    * @param            NULL
    * @return           void
    */
	@isTest static void testMemberData()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Member_ID__c memObj = [SELECT Id, Name FROM Member_ID__c WHERE Type__c ='Member-Id-Base' LIMIT 1];
        system.assertEquals(memObj.Name,'12345');  
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.PharmacyBenefits_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',memObj.Id);
                PharmacyBenefits_C_HUM objPharmFin = new PharmacyBenefits_C_HUM();
                objPharmFin.GetPolicyMemberName();
                objPharmFin.sPolicyMemberId='';
            system.assertEquals(objPharmFin.sPolicyMemberId='','');
            Test.stopTest();
        }
	}
}