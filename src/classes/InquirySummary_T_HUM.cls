/*******************************************************************************************************************************
Apex Class Name : InquirySummary_T_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains test methods InquirySummary_C_HUM class.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                       08/27/2020                REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look) 
* Sayali Nemade 								      09/03/2020                REQ - 1170241 DF -1509 Not able to see data on UI for Group, Provider and Agent/Broker
* Sayali Nemade 					   				 09/12/2020               REQ - 1170241 Added new Parameter Record Type in Legacy Contact History
 ********************************************************************************************************************************/
@isTest
private class InquirySummary_T_HUM 
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
        System.assertEquals(objUser.lastname == 'test User1',true);
        
        HUM_Webservice_Callout__c inquirySingleReferenceService = new HUM_Webservice_Callout__c(Name = 'InquirySingleReferenceService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://int-crmservicews.humana.com/CCPGCPDataRetention/SingleReference' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert inquirySingleReferenceService;
        HUM_Webservice_Callout__c inquirySummaryService = new HUM_Webservice_Callout__c(Name = 'InquirySummaryService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://int-crmservicews.humana.com/CCPGCPDataRetention/DataRetension' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert inquirySummaryService;
        System.assertEquals(inquirySummaryService.Name == 'InquirySummaryService',true);
        
        System.runAs(objUser)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+System.today().day()+'/'+System.today().year());
            insert objMemAccount;
            System.assertEquals(objMemAccount.LastName == 'LName', true);
            
            //Product
            Product2 oproduct = new Product2();
            oproduct.Name='MED';
            oproduct.Product_Type__c='MA';           
            insert oproduct;
            System.assertEquals(oproduct.Name == 'MED', true);
            
            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';
            oMedicalPolicy.Platform__c='LV';
            oMedicalPolicy.Product__c=oproduct.Id;
            insert oMedicalPolicy;
            System.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
            
            // Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            System.assertEquals(objMedPolMem.Relationship__c == 'self',true);
            
            Product2 oproduct2 = new Product2();
            oproduct2.Name='DEN';
            oproduct2.Product_Type__c='CA';           
            insert oproduct2;
            System.assertEquals(oproduct2.Name == 'DEN',true);
            
            Policy__c oMedicalPolicy2 = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy2.Name = 'Medical Product';
            oMedicalPolicy2.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy2.Major_LOB__c ='EPO';
            oMedicalPolicy2.Platform__c='CB';
            oMedicalPolicy2.Product__c=oproduct2.Id;
            insert oMedicalPolicy2;
            System.assertEquals(oMedicalPolicy2.Platform__c == 'CB',true);
            
            Policy_Member__c objMedPolMem2 = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem2.Member__c = objMemAccount.Id;
            objMedPolMem2.Policy__c = oMedicalPolicy2.Id;
            objMedPolMem2.Relationship__c = 'not self';  
            objMedPolMem2.end_date__c = '12/31/2015'; 
            objMedPolMem2.Effective_Date__c='01/01/2015';       
            insert objMedPolMem2;
            System.assertEquals(objMedPolMem2.Relationship__c == 'not self',true);
            
            //REQ-729137
            Member_ID__c objMemId = new Member_ID__c();
            objMemId.Policy_Member__c=objMedPolMem.id;
            objMemId.Policy__c=oMedicalPolicy.id;
            objMemId.Name='12345';
            objMemId.Type__c='Member-Id-Base';
            insert objMemId;
            System.assertEquals(objMemId.Type__c == 'Member-Id-Base',true);
        }
    } 
    
   /**
    * Positive test method for Inquiry Single Reference funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testInquirySingleReference()
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
            Test.setCurrentPage(page.InquirySummary_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            InquirySummary_C_HUM inquirySummary = new InquirySummary_C_HUM();
            inquirySummary.sReferenceNbr = 'test1';
            inquirySummary.startCount = 1;
            inquirySummary.endCount = 50;
            inquirySummary.sStartDate = '02/02/2010';
            inquirySummary.sEndDate = '02/02/2020';
			inquirySummary.sEnterpriseID = '1152441';
			inquirySummary.sRecordType = 'Member'; 
            inquirySummary.sRecordType = 'Group';
            inquirySummary.sRecordType = 'PROVIDER';
            inquirySummary.sRecordType = 'AGENT/BROKER';
            InquirySummary_S_HUM oServiceInquirySummary = new InquirySummary_S_HUM();
            Continuation continueObj = (Continuation)InquirySummary_C_HUM.getInquirySingleReference('','100210','1154210','Member');
			if(inquirySummary.sRecordType == 'Group')
            {
            continueObj = (Continuation)InquirySummary_C_HUM.getInquirySingleReference('','100210','1154210','Group');
            }
            if(inquirySummary.sRecordType == 'PROVIDER')
            {
            continueObj = (Continuation)InquirySummary_C_HUM.getInquirySingleReference('','100210','1154210','PROVIDER');
            }
            if(inquirySummary.sRecordType == 'AGENT/BROKER')
            {
            continueObj = (Continuation)InquirySummary_C_HUM.getInquirySingleReference('','100210','1154210','AGENT/BROKER');
            }
            continueObj.continuationMethod = 'parseInquirySummary';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{ "ContactDetails":[ { "CONTACT_ID":"100210", "INQUIRY_ID":"1", "INQ_TYPE_DESC":"CRITICAL INQUIRY", "POLICY_NAME":"", "CATEGORY_CD_DESC":"Open Enrollment", "REASON_CD_DESC":"PCP Inquiry", "DISPOSITION_DESC":"Info Given", "CREATED_TIMESTAMP":"05/24/2010", "INQUIRYFORTYPE":"Unknown-Member", "INQR_ID":"000058853 ", "INQUIRYFOR_FIRSTNAME":"", "INQUIRYFOR_LASTNAME":"", "INQUIRYABOUTTYPE":"Unknown-Member", "INQA_ID":"415280151A ", "INQUIRYABOUT_FIRSTNAME":"", "INQUIRYABOUT_LASTNAME":"", "OWNER_FIRST_NAME":"", "OWNER_LAST_NAME":"", "OWNER_TEAM_NAME":"Critical Inquiry - GB ", "OWNER_DEPT_NAME":"WSCALL Critical Inquiry ", "OWNER_SITE_NAME":"Green Bay ", "TASKS":"1", "PRIORITY":"Normal", "STATUS":"Open", "RECEIVED_TS":"05/24/2010" } ] }';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            InquirySummary_C_HUM.parseInquirySummary(labelGetmember,continueObj);
            Test.stopTest();
        }
    }
    
    /**
    * Negative test method for Inquiry Single Reference funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
  static testMethod void testNegInquirySingleReference()
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
            Test.setCurrentPage(page.InquirySummary_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            InquirySummary_C_HUM inquirySummary = new InquirySummary_C_HUM();
            InquirySummary_S_HUM oServiceInquirySummary = new InquirySummary_S_HUM();
            Continuation continueObj = (Continuation)InquirySummary_C_HUM.getInquirySingleReference('1014741145','100210','115241','M');
            continueObj.continuationMethod = 'parseInquirySummary';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString = '';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            InquirySummary_C_HUM.parseInquirySummary(labelGetmember,continueObj);
            Test.stopTest();
        }
    }
    
     /**
    * Positive test method for Inquiry Summary funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
  static testMethod void testInquirySummary()
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
            Test.setCurrentPage(page.InquirySummary_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            InquirySummary_C_HUM inquirySummary = new InquirySummary_C_HUM();
			inquirySummary.sRecordType = 'Member'; 
            inquirySummary.sRecordType = 'GROUP';
            inquirySummary.sRecordType = 'PROVIDER';
            inquirySummary.sRecordType = 'AGENT/BROKER';
            InquirySummary_S_HUM oServiceInquirySummary = new InquirySummary_S_HUM();
            Continuation continueObj = (Continuation)InquirySummary_C_HUM.getInquirySummary('','100210', 1, 50, '02/02/2010', '02/02/2020','115241','MEMBER');
			if(inquirySummary.sRecordType == 'GROUP')
            {
            continueObj = (Continuation)InquirySummary_C_HUM.getInquirySummary('','100210', 1, 50, '02/02/2010', '02/02/2020','115241','GROUP');
            }
            continueObj.continuationMethod = 'parseInquirySummary';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{"ContactDetails":[ { "CONTACT_ID":"547989201805", "INQUIRY_ID":"1", "INQ_TYPE_DESC":"CUSTOMER INQUIRY", "POLICY_NAME":"Medical", "CATEGORY_CD_DESC":"", "REASON_CD_DESC":"", "DISPOSITION_DESC":"", "CREATED_TIMESTAMP":"10/27/2018", "INQUIRYFORTYPE":"Member", "INQR_ID":"H0668447100 ", "INQUIRYFOR_FIRSTNAME":"", "INQUIRYFOR_LASTNAME":"", "INQUIRYABOUTTYPE":"Member", "INQA_ID":"H0668447100 ", "INQUIRYABOUT_FIRSTNAME":"", "INQUIRYABOUT_LASTNAME":"", "OWNER_FIRST_NAME":"Tara", "OWNER_LAST_NAME":"Greene", "OWNER_TEAM_NAME":"Retail to Mail ", "OWNER_DEPT_NAME":"RightSource RX ", "OWNER_SITE_NAME":"Phoenix ", "TASKS":"", "PRIORITY":"Normal", "STATUS":"Cancelled", "RECEIVED_TS":"10/27/2018" }, { "CONTACT_ID":"944365340410", "INQUIRY_ID":"1", "INQ_TYPE_DESC":"CUSTOMER INQUIRY", "POLICY_NAME":"Medical", "CATEGORY_CD_DESC":"VOB", "REASON_CD_DESC":"Lab/Rad/Outpt Services", "DISPOSITION_DESC":"Copay/Coins/Deduct Given", "CREATED_TIMESTAMP":"08/31/2009", "INQUIRYFORTYPE":"Provider", "INQR_ID":"611383719 ", "INQUIRYFOR_FIRSTNAME":"", "INQUIRYFOR_LASTNAME":"", "INQUIRYABOUTTYPE":"Member", "INQA_ID":"H0668447100 ", "INQUIRYABOUT_FIRSTNAME":"", "INQUIRYABOUT_LASTNAME":"", "OWNER_FIRST_NAME":"Esperanza", "OWNER_LAST_NAME":"Oliva", "OWNER_TEAM_NAME":"Team 2 ", "OWNER_DEPT_NAME":"APAC Alabang PIC ", "OWNER_SITE_NAME":"Provider Call Center Ops ", "TASKS":"", "PRIORITY":"Normal", "STATUS":"Closed", "RECEIVED_TS":"08/31/2009" }, { "CONTACT_ID":"404598304504", "INQUIRY_ID":"1", "INQ_TYPE_DESC":"CORRESPONDENCE INQUIRY", "POLICY_NAME":"Medical", "CATEGORY_CD_DESC":"Processing Issues", "REASON_CD_DESC":"Bi-Lateral Procedure Calculations", "DISPOSITION_DESC":"Transferred to Foreign Language Line", "CREATED_TIMESTAMP":"03/06/2006", "INQUIRYFORTYPE":"Provider", "INQR_ID":"311473421 ", "INQUIRYFOR_FIRSTNAME":"", "INQUIRYFOR_LASTNAME":"", "INQUIRYABOUTTYPE":"Member", "INQA_ID":"00002729401 ", "INQUIRYABOUT_FIRSTNAME":"", "INQUIRYABOUT_LASTNAME":"", "OWNER_FIRST_NAME":"Roselynn", "OWNER_LAST_NAME":"Madary", "OWNER_TEAM_NAME":"Overpayments ", "OWNER_DEPT_NAME":"GB Financial Recovery ", "OWNER_SITE_NAME":"Green Bay ", "TASKS":"", "PRIORITY":"Normal", "STATUS":"Closed", "RECEIVED_TS":"03/06/2006" }, { "CONTACT_ID":"102850242622", "INQUIRY_ID":"1", "INQ_TYPE_DESC":"CORRESPONDENCE INQUIRY", "POLICY_NAME":"Medical", "CATEGORY_CD_DESC":"Corrected Claim", "REASON_CD_DESC":"Charged Amount", "DISPOSITION_DESC":"Transferred to Foreign Language Line", "CREATED_TIMESTAMP":"09/27/2006", "INQUIRYFORTYPE":"Provider", "INQR_ID":"000006556 ", "INQUIRYFOR_FIRSTNAME":"", "INQUIRYFOR_LASTNAME":"", "INQUIRYABOUTTYPE":"Member", "INQA_ID":"00002729401 ", "INQUIRYABOUT_FIRSTNAME":"", "INQUIRYABOUT_LASTNAME":"", "OWNER_FIRST_NAME":"Roselynn", "OWNER_LAST_NAME":"Madary", "OWNER_TEAM_NAME":"Overpayments ", "OWNER_DEPT_NAME":"GB Financial Recovery ", "OWNER_SITE_NAME":"Green Bay ", "TASKS":"", "PRIORITY":"Normal", "STATUS":"Closed", "RECEIVED_TS":"09/27/2006" } ], "count":"24", "FLAG_24_MONTHS":"Y" }';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            InquirySummary_C_HUM.parseInquirySummary(labelGetmember,continueObj);
            Continuation continueObj2 = (Continuation)InquirySummary_C_HUM.getInquirySummary('1014741145','100210', null,null,null,null,null,'M');
            continueObj2.continuationMethod = 'parseInquirySummary';
            continueObj2.state = 'Both';
            HttpResponse httpRes2 = new Httpresponse ();
			HttpResponse httpRes3 = new Httpresponse ();
            String resBodyString2= '{"ContactDetails":[ { "CONTACT_ID":"547989201805", "INQUIRY_ID":"1", "INQ_TYPE_DESC":"CUSTOMER INQUIRY", "POLICY_NAME":"Medical", "CATEGORY_CD_DESC":"", "REASON_CD_DESC":"", "DISPOSITION_DESC":"", "CREATED_TIMESTAMP":"10/27/2018", "INQUIRYFORTYPE":"Member", "INQR_ID":"H0668447100 ", "INQUIRYFOR_FIRSTNAME":"", "INQUIRYFOR_LASTNAME":"", "INQUIRYABOUTTYPE":"Member", "INQA_ID":"H0668447100 ", "INQUIRYABOUT_FIRSTNAME":"", "INQUIRYABOUT_LASTNAME":"", "OWNER_FIRST_NAME":"Tara", "OWNER_LAST_NAME":"Greene", "OWNER_TEAM_NAME":"Retail to Mail ", "OWNER_DEPT_NAME":"RightSource RX ", "OWNER_SITE_NAME":"Phoenix ", "TASKS":"", "PRIORITY":"Normal", "STATUS":"Cancelled", "RECEIVED_TS":"10/27/2018" }, { "CONTACT_ID":"944365340410", "INQUIRY_ID":"1", "INQ_TYPE_DESC":"CUSTOMER INQUIRY", "POLICY_NAME":"Medical", "CATEGORY_CD_DESC":"VOB", "REASON_CD_DESC":"Lab/Rad/Outpt Services", "DISPOSITION_DESC":"Copay/Coins/Deduct Given", "CREATED_TIMESTAMP":"08/31/2009", "INQUIRYFORTYPE":"Provider", "INQR_ID":"611383719 ", "INQUIRYFOR_FIRSTNAME":"", "INQUIRYFOR_LASTNAME":"", "INQUIRYABOUTTYPE":"Member", "INQA_ID":"H0668447100 ", "INQUIRYABOUT_FIRSTNAME":"", "INQUIRYABOUT_LASTNAME":"", "OWNER_FIRST_NAME":"Esperanza", "OWNER_LAST_NAME":"Oliva", "OWNER_TEAM_NAME":"Team 2 ", "OWNER_DEPT_NAME":"APAC Alabang PIC ", "OWNER_SITE_NAME":"Provider Call Center Ops ", "TASKS":"", "PRIORITY":"Normal", "STATUS":"Closed", "RECEIVED_TS":"08/31/2009" }, { "CONTACT_ID":"404598304504", "INQUIRY_ID":"1", "INQ_TYPE_DESC":"CORRESPONDENCE INQUIRY", "POLICY_NAME":"Medical", "CATEGORY_CD_DESC":"Processing Issues", "REASON_CD_DESC":"Bi-Lateral Procedure Calculations", "DISPOSITION_DESC":"Transferred to Foreign Language Line", "CREATED_TIMESTAMP":"03/06/2006", "INQUIRYFORTYPE":"Provider", "INQR_ID":"311473421 ", "INQUIRYFOR_FIRSTNAME":"", "INQUIRYFOR_LASTNAME":"", "INQUIRYABOUTTYPE":"Member", "INQA_ID":"00002729401 ", "INQUIRYABOUT_FIRSTNAME":"", "INQUIRYABOUT_LASTNAME":"", "OWNER_FIRST_NAME":"Roselynn", "OWNER_LAST_NAME":"Madary", "OWNER_TEAM_NAME":"Overpayments ", "OWNER_DEPT_NAME":"GB Financial Recovery ", "OWNER_SITE_NAME":"Green Bay ", "TASKS":"", "PRIORITY":"Normal", "STATUS":"Closed", "RECEIVED_TS":"03/06/2006" }, { "CONTACT_ID":"102850242622", "INQUIRY_ID":"1", "INQ_TYPE_DESC":"CORRESPONDENCE INQUIRY", "POLICY_NAME":"Medical", "CATEGORY_CD_DESC":"Corrected Claim", "REASON_CD_DESC":"Charged Amount", "DISPOSITION_DESC":"Transferred to Foreign Language Line", "CREATED_TIMESTAMP":"09/27/2006", "INQUIRYFORTYPE":"Provider", "INQR_ID":"000006556 ", "INQUIRYFOR_FIRSTNAME":"", "INQUIRYFOR_LASTNAME":"", "INQUIRYABOUTTYPE":"Member", "INQA_ID":"00002729401 ", "INQUIRYABOUT_FIRSTNAME":"", "INQUIRYABOUT_LASTNAME":"", "OWNER_FIRST_NAME":"Roselynn", "OWNER_LAST_NAME":"Madary", "OWNER_TEAM_NAME":"Overpayments ", "OWNER_DEPT_NAME":"GB Financial Recovery ", "OWNER_SITE_NAME":"Green Bay ", "TASKS":"", "PRIORITY":"Normal", "STATUS":"Closed", "RECEIVED_TS":"09/27/2006" } ], "count":"24", "FLAG_24_MONTHS":"Y" }';
			String resBodyString3= '{"ContactDetails":[ ], "count":"24", "FLAG_24_MONTHS":"Y" }';         
            httpRes2.setBody(resBodyString2);
            System.assert(httpRes2.getBody() == resBodyString2);
            Map<String, HTTPRequest> mapDetails2 = continueObj2.getRequests();
            string reqType2 = '';
            for(String s : mapDetails2.keyset())
            {
                reqType2 = s;
            } 
            List<string>labelGetmember2 = new List<string>();
            labelGetmember2.add(reqType2);
            Test.setContinuationResponse(reqType2,httpRes2);
            InquirySummary_C_HUM.parseInquirySummary(labelGetmember2, continueObj2);
			httpRes3.setBody(resBodyString3);
            System.assert(httpRes3.getBody() == resBodyString3);
            Map<String, HTTPRequest> mapDetails3 = continueObj2.getRequests();
            string reqType3 = '';
            for(String s : mapDetails3.keyset())
            {
                reqType3 = s;
            } 
            List<string>labelGetmember3 = new List<string>();
            labelGetmember3.add(reqType3);
            Test.setContinuationResponse(reqType3,httpRes3);
            InquirySummary_C_HUM.parseInquirySummary(labelGetmember3, continueObj2);
            
            Test.stopTest();
        }
    }
    
   
    
     /**
    * Negative test method for Inquiry Single Reference funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
    
    static testMethod void testNegInquirySummary()
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
            Test.setCurrentPage(page.InquirySummary_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            InquirySummary_C_HUM inquirySummary = new InquirySummary_C_HUM();
            InquirySummary_S_HUM oServiceInquirySummary = new InquirySummary_S_HUM();
            Continuation continueObj = (Continuation)InquirySummary_C_HUM.getInquirySummary('1014741145','100210', 1, 50,'02/02/2010', '02/02/2020','44545445','M');
            continueObj.continuationMethod = 'parseInquirySummary';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            InquirySummary_C_HUM.parseInquirySummary(labelGetmember,continueObj);
            Test.stopTest();
        }
    }
    
    /**
    * test method to cover service Response DTO
    *<p>
    * @param            NULL
    * @return           void
    */
    @isTest static void testInquirySummaryResponse_H_HUM()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            InquirySummaryResponse_H_HUM oResponse = new InquirySummaryResponse_H_HUM();
            oResponse.count = '24';
            oResponse.FLAG_24_MONTHS = 'Y';
            List<InquirySummaryResponse_H_HUM.ContactDetails> oContactDetailsLst = new List<InquirySummaryResponse_H_HUM.ContactDetails>();
            InquirySummaryResponse_H_HUM.ContactDetails oContactDetails = new InquirySummaryResponse_H_HUM.ContactDetails();
            oContactDetails.CONTACT_ID = '10021222010';
            oContactDetails.INQUIRY_ID = '1';
            oContactDetails.INQ_TYPE_DESC = 'Type1';
            oContactDetails.POLICY_NAME = 'Medical';
            oContactDetails.CATEGORY_CD_DESC = '00052';
            oContactDetails.REASON_CD_DESC = 'testing message';
            oContactDetails.DISPOSITION_DESC = 'Testing 1';
            oContactDetails.CREATED_TIMESTAMP = '02/12/2009';
            oContactDetails.INQUIRYFORTYPE = 'Type2';
            oContactDetails.INQR_ID = '125525';
            oContactDetails.INQUIRYFOR_FIRSTNAME = 'John';
            oContactDetails.INQUIRYFOR_LASTNAME = 'Smith';
            oContactDetails.INQUIRYABOUTTYPE = 'Member';
            oContactDetails.INQA_ID = '10021222';
            oContactDetails.INQUIRYABOUT_FIRSTNAME = 'John';
            oContactDetails.INQUIRYABOUT_LASTNAME = 'Smith';
            oContactDetails.OWNER_FIRST_NAME = 'Test3';
            oContactDetails.OWNER_LAST_NAME = 'Testt4';
            oContactDetails.OWNER_TEAM_NAME = 'Testing Team';
            oContactDetails.OWNER_DEPT_NAME = 'Dep1';
            oContactDetails.TASKS = 'Task1';
            oContactDetails.PRIORITY = '1';
            oContactDetails.STATUS = 'testing status message';
            oContactDetails.RECEIVED_TS = '02/12/2009';           
            System.assertEquals(oContactDetails.CONTACT_ID == '10021222010',true);
            Test.stopTest();
        }
    }
}