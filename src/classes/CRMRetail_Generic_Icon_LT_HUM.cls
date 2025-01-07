/*******************************************************************************************************************************
Apex Class Name     : CRMRetail_Generic_Icon_LT_HUM
Version             : 1.0
Created Date        : 02/01/2023
Function            : This serves as the test class for CRMRetail_Generic_Icon_LC_HUM class.

Modification Log:  
* Developer Name           Code Review                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sahil Verma                                          	   01/25/2022            	  User Story 4231796: T1PRJ0154546 / DP / MF9 Storefront - Ability to View Deceased Information on Member Account
* Mohamed Thameem                                          04/26/2023                 User Story 4566290: T1PRJ0154546 / SF / MF3 Storefront: Display MyHumana Enrollment & Go365 Enrollment - Backend Logic
*******************************************************************************************************************************/
@isTest
public class CRMRetail_Generic_Icon_LT_HUM {
	/*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData() 
    {
        HUM_Webservice_Callout__c oHUMClaimSummaryService = new HUM_Webservice_Callout__c();
        oHUMClaimSummaryService.name = 'HUMMemberWebActivityDetailsService';
        oHUMClaimSummaryService.Active__c = true;
        oHUMClaimSummaryService.Certificate_Name__c = 'testts';
        oHUMClaimSummaryService.End_Point_URL__c = 'https://testst-cmservicews.hum.com/';
        oHUMClaimSummaryService.Timeout__c = '120000';
        oHUMClaimSummaryService.Content_Type__c = 'application/json';
        oHUMClaimSummaryService.Request_Type__c = 'POST';
        insert oHUMClaimSummaryService;
        system.assert(oHUMClaimSummaryService.id != null);
        system.assert(oHUMClaimSummaryService.name == 'HUMMemberWebActivityDetailsService');
        
        List<User> lstOfUsers = new List<User>();
        User u= HUMTestDataHelperTest.createTestUser('UserRetail','CRMRetail User');
        lstOfUsers.add(u);
        insert lstOfUsers;
        system.assertEquals('test UserRetail',lstOfUsers.get(0).lastname);
        
        Id MemberRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER).getRecordTypeId();
        
        system.runAs(u){
          	List<Account> lstOfAccounts = new List<Account>();
            Account oTaskAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(MemberRecordTypeId , '01/01/1998', 'Alex', 'Stewart', '56605', '', '', 'New York', false);
            oTaskAcc.MiddleName = 'M';
            oTaskAcc.Enterprise_ID__c='1030693189';
            oTaskAcc.Home_Office_Account__c = false;
            oTaskAcc.Deceased_Date__c = Date.today().format();
            lstOfAccounts.add(oTaskAcc);
            insert lstOfAccounts;
            system.assertEquals(1,lstOfAccounts.size());
            system.assertEquals('Stewart',lstOfAccounts[0].lastName); 
            
            List<Product2> lstOfProducts = new List<Product2>();                  
            Product2 prod2 = new Product2(ProductCode='MED', Name='MED',Product_Type__c='MA');
            lstOfProducts.add(prod2);
            insert lstOfProducts;
            lstOfProducts.clear();
            lstOfProducts = [select id,name from Product2];
            system.assertEquals(1,lstOfProducts.size()); 
            
            List<Policy__c> lstOfPol = new List<Policy__c>();
            Policy__c oPolicy = CRMRetail_TestDataFactory_H_HUM.createPolicyInstance(oTaskAcc.id,'89012',prod2.Id);
            oPolicy.Platform__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_NI_PLATFORM_KEY;
            lstOfPol.add(oPolicy);   
            insert lstOfPol;
            lstOfPol.clear();
            lstOfPol = [select id,name from Policy__c];
            system.assertEquals(1,lstOfPol.size());
            
            Date dToday = Date.valueOf(System.today());
            String sFormValidEndDate = String.valueOf(dToday.month())+'/'+String.valueOf(dToday.day())+'/'+String.valueOf(dToday.year()+1);
            List<Policy_Member__c> lstPlMem = new List<Policy_Member__c>();
            Policy_Member__c oPolicyMember = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(oTaskAcc.Id,oPolicy.Id,'750678903201',(system.now().addYears(-2)).format('MM/dd/yyyy'),sFormValidEndDate,false);                                                                        
            lstPlMem.add(oPolicyMember); 
            insert lstPlMem;   
            lstPlMem.clear();
            lstPlMem = [select id,name from Policy_Member__c];
            system.assertEquals(1,lstPlMem.size());
            
            List<Member_ID__c> lstMemIds = new List<Member_ID__c>();
            Member_ID__c oMemberActive = CRMRetail_TestDataFactory_H_HUM.createMemberIdInstance(oPolicyMember.Id,'7506789032', '01', oPolicy.Id, 'M750', '7506789032');                       
            oMemberActive.Type__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_VITALITY_ENTITY_TYPE;
            lstMemIds.add(oMemberActive);
            insert lstMemIds;
            oMemberActive = new Member_ID__c();
            oMemberActive = [select id,name from Member_ID__c];
            system.assertEquals('7506789032',oMemberActive.Name);
            
            List<CRMFunctionality_ONOFF_Switch__c> lstOfSwitches = new List<CRMFunctionality_ONOFF_Switch__c>();
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch1 = new  CRMFunctionality_ONOFF_Switch__c(Name = '4566290', IsON__c = True); 
            lstOfSwitches.add(csCRMOnOffSwitch1);
            insert lstOfSwitches;                        
            System.assertEquals(1,lstOfSwitches.size());
        }
    }
    
    /*
    * This method to cover showIcons
    * @param none
    * @return void
    */
    @isTest static void testShowIcons()
    {
        User oCCSUser = [Select id,LastName from User where LastName = 'test UserRetail' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test UserRetail');
        
        system.runAs(oCCSUser)
        {
            Map<string,string> headerMap = new Map<string,string>();
            headerMap.put('Content-Type', 'application/xml');
            String responseBody='{ "VitalityEntityID":"2693137974", "PolicyVitalityTrue":"Y", "PolicyVitalityFalse":"N", "MemberGenKey":"0784004454117", "LastLoginDateTime":"4/27/2023 6:47:44 AM", "IsWebRegistered":"true", "ListPoliciesWithVitalityProg":[ { "EffectiveDate":"01/01/2023", "EndDate":"12/31/9999", "MLOB":"MER", "productLineCd":"MED" }, { "EffectiveDate":"01/01/2022", "EndDate":"12/31/2022", "MLOB":"MEP", "productLineCd":"MED" } ], "ErrorMemberWebActivity":"" }';
            CRMRetail_GenericWebService_Mock_T_HUM mock = new CRMRetail_GenericWebService_Mock_T_HUM(200,'OK',responseBody,headerMap);
            
            Test.setMock(HttpCalloutMock.class,mock);
            List<Account> oAcc = [select Id, Deceased_Date__c from Account where FirstName = 'Alex' LIMIT 1];
            system.assertEquals(oAcc[0].Deceased_Date__c,Date.today().format());
            Test.startTest();
            CRMRetail_Result_DTO_HUM resWrapper = CRMRetail_Generic_Icon_LC_HUM.showIcons(string.valueOf(oAcc[0].Id));
            map<string,map<string,string>> mp = (map<string,map<string,string>>)JSON.deserialize(String.valueOf(resWrapper.sResult),map<string,map<string,string>>.class);            
            system.assertEquals(mp.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DECEASED).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SHOW), 'true');
            system.assertEquals(mp.get(System.Label.CRMRetail_Go365_Eligibility).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SHOW), 'true');
            system.assertEquals(mp.get(System.Label.CRMRetail_LastLogin_DateTime).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SHOW), 'true');
            system.assertEquals(mp.get(System.Label.CRMRetail_MyHumanaKey).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SHOW), 'true');
            Test.stopTest();
        }
    }
    
    
    /*
    * This method to cover showIcons
    * @param recId
    * @return map<String,Boolean>
    */
    @isTest static void testGo365WithErrorMemberWebActivity()
    {
        User oCCSUser = [Select id,LastName from User where LastName = 'test UserRetail' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test UserRetail');
        
        system.runAs(oCCSUser)
        {
            Map<string,string> headerMap = new Map<string,string>();
            headerMap.put('Content-Type', 'application/xml');
            String responseBody='{ "VitalityEntityID":"2693137974", "PolicyVitalityTrue":"Y", "PolicyVitalityFalse":"N", "MemberGenKey":"0784004454117", "LastLoginDateTime":"4/27/2023 6:47:44 AM", "IsWebRegistered":"true", "ListPoliciesWithVitalityProg":[ { "EffectiveDate":"01/01/2023", "EndDate":"12/31/9999", "MLOB":"MER", "productLineCd":"MED" }, { "EffectiveDate":"01/01/2022", "EndDate":"12/31/2022", "MLOB":"MEP", "productLineCd":"MED" } ], "ErrorMemberWebActivity":"error" }';
            CRMRetail_GenericWebService_Mock_T_HUM mock = new CRMRetail_GenericWebService_Mock_T_HUM(200,'OK',responseBody,headerMap);
            Test.setMock(HttpCalloutMock.class,mock);
                         
            List<Account> oAcc = [select Id, Deceased_Date__c from Account where FirstName = 'Alex' LIMIT 1];
            system.assertEquals(oAcc[0].Deceased_Date__c,Date.today().format());
            Test.startTest();
            CRMRetail_Result_DTO_HUM resWrapper = CRMRetail_Generic_Icon_LC_HUM.showIcons(string.valueOf(oAcc[0].Id));
            map<string,map<string,string>> mp = (map<string,map<string,string>>)JSON.deserialize(String.valueOf(resWrapper.sResult),map<string,map<string,string>>.class);                        
            system.assertEquals(mp.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DECEASED).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SHOW), 'true');
            system.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERROR_FETCHING_DETAILS,mp.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MEMBERACTIVITY_ERROR_NODE).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERRORMSG_KEY));
            Test.stopTest();
        }
    }
    
    /*
    * This method to cover showIcons
    * @param none
    * @return void
    */
    @isTest static void testGo365WithFault()
    {
        User oCCSUser = [Select id,LastName from User where LastName = 'test UserRetail' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test UserRetail');
        
        system.runAs(oCCSUser)
        {
            Map<string,string> headerMap = new Map<string,string>();
            headerMap.put('Content-Type', 'application/xml');
            String responseBody='{ "Fault":{ "faultcode":"\\n\\t\\t\\t\\t\\tCode-90.1.1", "detail":{ "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\\r\\nContent-Type: text/xml\\r\\nX-Backside-Transport: FAIL FAIL\\r\\nConnection: close\\r\\n\\r\\n", "error-message":"500Internal Server ErrorHTTP/1.1application/jsonFAIL FAILTue, 02 May 2023 11:17:42 GMT", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            CRMRetail_GenericWebService_Mock_T_HUM mock = new CRMRetail_GenericWebService_Mock_T_HUM(200,'OK',responseBody,headerMap);
            Test.setMock(HttpCalloutMock.class,mock);
            
            List<Account> oAcc = [select Id, Deceased_Date__c from Account where FirstName = 'Alex' LIMIT 1];
            system.assertEquals(oAcc[0].Deceased_Date__c,Date.today().format());
            Test.startTest();
            CRMRetail_Result_DTO_HUM resWrapper = CRMRetail_Generic_Icon_LC_HUM.showIcons(string.valueOf(oAcc[0].Id));
            map<string,map<string,string>> mp = (map<string,map<string,string>>)JSON.deserialize(String.valueOf(resWrapper.sResult),map<string,map<string,string>>.class);                        
            system.assertEquals(mp.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DECEASED).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SHOW), 'true');
            system.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERROR_FETCHING_DETAILS,mp.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MEMBERACTIVITY_ERROR_NODE).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERRORMSG_KEY));
            Test.stopTest();
        }
    }
    
	/*
    * This method to cover Go365 eligibility Without Vitality information
    * @param none
    * @return void
    */
    @isTest static void testGo365WithoutVitality()
    {
        User oCCSUser = [Select id,LastName from User where LastName = 'test UserRetail' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test UserRetail');
        
        system.runAs(oCCSUser)
        {            
            Map<string,string> headerMap = new Map<string,string>();
            headerMap.put('Content-Type', 'application/xml');
            String responseBody= '{ "VitalityEntityID":"", "PolicyVitalityTrue":"Y", "PolicyVitalityFalse":"N", "MemberGenKey":"0784004454117", "LastLoginDateTime":"4/27/2023 6:47:44 AM", "IsWebRegistered":"true", "ListPoliciesWithVitalityProg":[ { "EffectiveDate":"01/01/2023", "EndDate":"12/31/9999", "MLOB":"MER", "productLineCd":"MED" }, { "EffectiveDate":"01/01/2022", "EndDate":"12/31/2022", "MLOB":"MEP", "productLineCd":"MED" } ], "ErrorMemberWebActivity":"" }';
            CRMRetail_GenericWebService_Mock_T_HUM mock = new CRMRetail_GenericWebService_Mock_T_HUM(200,'OK',responseBody,headerMap);
            Test.setMock(HttpCalloutMock.class,mock);
            
            List<Account> oAcc = [select Id, Deceased_Date__c from Account where FirstName = 'Alex' LIMIT 1];
            system.assertEquals(oAcc[0].Deceased_Date__c,Date.today().format());
            Test.startTest();
            CRMRetail_Result_DTO_HUM resWrapper = CRMRetail_Generic_Icon_LC_HUM.showIcons(string.valueOf(oAcc[0].Id));
            map<string,map<string,string>> mp = (map<string,map<string,string>>)JSON.deserialize(String.valueOf(resWrapper.sResult),map<string,map<string,string>>.class);    
            system.assertEquals(mp.get(System.Label.CRMRetail_Go365_Eligibility).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SHOW), 'true');
            Test.stopTest();
        }
    }
    
    /*
    * This method to cover exceptions
    * @param none
    * @return void
    */
    @isTest static void testExceptions()
    {
        User oCCSUser = [Select id,LastName from User where LastName = 'test UserRetail' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test UserRetail');
        
        system.runAs(oCCSUser)
        {            
            Map<string,string> headerMap = new Map<string,string>();
            headerMap.put('Content-Type', 'application/xml');
            String responseBody='{ "VitalityEntityID":", "PolicyVitalityTrue":"Y", "PolicyVitalityFalse":"N", "MemberGenKey":"0784004454117", "LastLoginDateTime":"4/27/2023 6:47:44 AM", "IsWebRegistered":"true", "ListPoliciesWithVitalityProg":[ { "EffectiveDate":"01/01/2023", "EndDate":"12/31/9999", "MLOB":"MER", "productLineCd":"MED" }, { "EffectiveDate":"01/01/2022", "EndDate":"12/31/2022", "MLOB":"MEP", "productLineCd":"MED" } ], "ErrorMemberWebActivity":"" }';
            CRMRetail_GenericWebService_Mock_T_HUM mock = new CRMRetail_GenericWebService_Mock_T_HUM(200,'OK',responseBody,headerMap);
            Test.setMock(HttpCalloutMock.class,mock);
            
            List<Account> oAcc = [select Id, Deceased_Date__c from Account where FirstName = 'Alex' LIMIT 1];
            system.assertEquals(oAcc[0].Deceased_Date__c,Date.today().format());
            Test.startTest();
            CRMRetail_Result_DTO_HUM resWrapper = CRMRetail_Generic_Icon_LC_HUM.showIcons(string.valueOf(oAcc[0].Id));
            map<string,map<string,string>> mp = (map<string,map<string,string>>)JSON.deserialize(String.valueOf(resWrapper.sResult),map<string,map<string,string>>.class);			           
            system.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERROR_FETCHING_DETAILS,mp.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MEMBERACTIVITY_ERROR_NODE).get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERRORMSG_KEY));
            
            CRMRetail_Generic_Icon_LC_HUM.isMemberDeceased(null);
            List<Error_Log__c> lst = [SELECT id,Name,Error_Message__c,Class_Name__c,Method_Name__c from Error_Log__c where Createddate=today order by CreatedDate DESC Limit 1];            
            system.assertEquals('CRMRetail_Generic_Icon_LC_HUM',lst[0].Class_Name__c);
             
            Test.stopTest(); 
        }
    }
}