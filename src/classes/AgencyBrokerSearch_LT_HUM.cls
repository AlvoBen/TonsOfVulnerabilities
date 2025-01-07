/*******************************************************************************************************************************
Apex Class Name : AgencyBrokerSearch_LT_HUM
Version         : 1.0
Created On      : 11/2/2020
Function        : This class is used to test Agency Broken Search for Strides app
Test Class      : 

Modification Log: 
 * Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*      1.0                      Rahul Krishan                                                                                                                                                                 09/28/2020                                                                        Original Version
*      2.0                      Kajal Namdev                                         05/07/2021                 Replace HUMConstants with GLOBAL_SEARCH_CONSTANT_LH_HUM class
*      3.0                      Saikumar Boga                                        07/13/2021                 Changes for Unknown Agent/Broker
*      4.0                      Kajal Namdev                                         11/16/2021                 Test class fix
*      5.0                      Kajal Namdev                                         11/18/2021                 Increase Test Coverage
******************************************************************************************************************************************************************************/

@isTest
public class AgencyBrokerSearch_LT_HUM 
{
    /*
    * Method Name : prepareTestData
    * Description : Create common test Data
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        System.runAs (oCCSUser) 
        {
            RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
            RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
            RecordType oAgentBrokerRecType = [SELECT Id FROM RecordType WHERE Name = 'Agent/Broker' Limit 1];
            List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name='MembersearchConstantZero',StringValue__c='0',IntegerValue__c=0));
            lstConstants.add(new HUMConstants__c(Name='HUMServiceTimeOut',StringValue__c='12',IntegerValue__c=12));
            insert lstConstants;
            System.assertEquals(lstConstants.size(), 2);
            
            HUMSearchLimit__c oSearchLimit = new HUMSearchLimit__c(Name='Member_SearchLimit', Search_Limit__c=101);
            insert oSearchLimit;
            System.assertNotEquals(oSearchLimit, NULL, 'Value not matched');
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Individual_First_Name__c = 'Christopher';
            oAcc.Individual_Last_Name__c = 'SMESSAERT';
            oAcc.firstName = 'Daphiner';
            oAcc.lastName = 'Superman';
            oAcc.recordtypeid = oMemRecType.Id;
            oAcc.Agent_ID__c = '1734364';
            oAcc.Agent_Type__c = 'Broker';
            oAcc.BillingStreet = 'abc';
            oAcc.BillingPostalCode='53226';
            oAcc.BillingState = 'Wisconsin';
            insert oAcc;
            System.AssertNotEquals(oAcc, NULL);
            
            Account oAccObj = HUMTestDataHelperTest.getAccount();
            oAccObj.Individual_First_Name__c = 'Christopher';
            oAccObj.Individual_Last_Name__c = 'SMESSAERT';
            oAccObj.Name = 'testDaphiner';
            oAccObj.RecordTypeId = oAgentBrokerRecType.Id; 
            oAccObj.Agent_ID__c = '1111111';
            oAccObj.Agent_Type__c = 'Broker';
            oAccObj.BillingStreet = 'abc';
            oAccObj.BillingPostalCode='53226';
            oAccObj.BillingState = 'Wisconsin';
            insert oAccObj;
            System.AssertNotEquals(oAccObj, NULL);
            ConsumerID__c conIdObj = new ConsumerID__c();
            conIdObj.Account__c = oAccObj.ID;
            
            conIdObj.Consumer_ID__c = '9876543';
            conIdObj.ID_Type__c = 'TaxID';
            conIdObj.ETL_Record_Deleted__c = false;
            insert conIdObj;
            
            ConsumerID__c conIdObj1 = new ConsumerID__c();
            conIdObj1.Account__c = oAccObj.ID;
            
            conIdObj1.Consumer_ID__c = '123456';
            conIdObj1.ID_Type__c = 'TaxID';
            conIdObj1.ETL_Record_Deleted__c = false;
            insert conIdObj1;
            
            Account oAcc1 = HUMTestDataHelperTest.getAccount();
            oAcc1.Individual_First_Name__c = 'Ashish';
            oAcc1.Individual_Last_Name__c = '';
            oAcc1.Name = 'New Account';
            oAcc1.recordtypeid = oAgentBrokerRecType.Id;
            oAcc1.Agent_ID__c = '1111111';
            oAcc1.Agent_Type__c = 'Broker';
            oAcc1.BillingStreet = 'abc';
            oAcc1.BillingPostalCode='53226';
            oAcc1.BillingState = 'Wisconsin';
            insert oAcc1;
            
            Account oAcc2 = HUMTestDataHelperTest.getAccount();
            oAcc2.Individual_First_Name__c = 'Rahul';
            oAcc2.Individual_Last_Name__c = '';
            oAcc2.Name = 'Rahul';
            oAcc2.recordtypeid = oAgentBrokerRecType.Id;
            oAcc2.Agent_ID__c = '2222222';
            oAcc2.Agent_Type__c = 'Agency';
            oAcc2.BillingStreet = 'abc';
            oAcc2.BillingPostalCode='53226';
            oAcc2.BillingState = 'Wisconsin';
            oAcc2.Work_Email__c = 'test@gmail.com';
            oAcc2.ETL_Record_Deleted__c = false;
            insert oAcc2;
            
            Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
            Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
            Schema.RecordTypeInfo oMemberRecordType_Test =  accountRecordTypeMap.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.HumMemberRecordType);
            List<ConsumerID__c> lstConsumerId = new List<ConsumerID__c>(); 
            ConsumerID__c conId = new ConsumerID__c();
            conId.Account__c = oAcc.ID;
            conId.Consumer_External_ID__c = 'UKM|03|SFDC|HumanaId|a0uL00000058F50IAE';
            conId.Consumer_ID__c = '123456';
            conId.ID_Type__c = 'HumanaId';
            
            ConsumerID__c conId1 = new ConsumerID__c();
            conId1.Account__c = oAcc1.ID;
            
            conId1.Consumer_ID__c = '123456';
            conId1.ID_Type__c = 'TaxID';
            conId1.ETL_Record_Deleted__c = false;
            ConsumerID__c conId2 = new ConsumerID__c();
            conId2.Account__c = oAcc2.ID;
            
            conId2.Consumer_ID__c = '123456';
            conId2.ID_Type__c = 'TaxID';
            conId2.ETL_Record_Deleted__c = false;
            lstConsumerId.add(conId);
            lstConsumerId.add(conId1);
            lstConsumerId.add(conId2);
            insert lstConsumerId;
            Map<String,String> stateLst = SearchUtilty_H_HUM.getStateValues();
            HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='AgencySearchService',Timeout__c='12000',Request_Type__c='GET', Active__c=true,Certificate_Name__c = 'Test_Certificate', 
                                                                                     Content_Type__c = 'application/json',End_Point_URL__c = 'test_End_Point');
            insert mbeServiceRecord;
        }
    }
    
    /*
    * Method Name : prepareTestSearchResults()
    * Description : Test Search Criteria
    * Return type : void
    * Parameter   : NA
    */
    static testmethod void prepareTestSearchResults()
    {
        User SUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(SUser.UserName,'user1user1@humcrmstest.com');
        List<String> recTypesLst = new List<String>();
        
        AgencyBrokerSearch_DTO_HUM usewrapper = new AgencyBrokerSearch_DTO_HUM();
        usewrapper.isUnknownAgencySearch = true;
        usewrapper.sFirstName = '';
        usewrapper.sLastName = '';
        usewrapper.sAgencyName = '';
        usewrapper.sAgentId = '1734364';
        usewrapper.sTaxID = '';
        usewrapper.sAgentType = 'Broker';
        usewrapper.sState = '';
        
        AgencyBrokerSearch_DTO_HUM usewrapper1 = new AgencyBrokerSearch_DTO_HUM();
        usewrapper1.isUnknownAgencySearch = true;
        usewrapper1.sFirstName = '';
        usewrapper1.sLastName = '';
        usewrapper1.sAgencyName = '';
        usewrapper1.sAgentId = '';
        usewrapper1.sTaxID = '';
        usewrapper1.sAgentType = 'Broker';
        usewrapper1.sState = '';
        
        AgencyBrokerSearch_DTO_HUM usewrapper2 = new AgencyBrokerSearch_DTO_HUM();
        usewrapper2.isUnknownAgencySearch = true;
        usewrapper2.sFirstName = 'Ashish';
        usewrapper2.sLastName = '';
        usewrapper2.sAgencyName = '';
        usewrapper2.sAgentId = '1111111';
        usewrapper2.sTaxID = '5566778';
        usewrapper2.sAgentType = 'Broker';
        usewrapper2.sState = '';
        
        AgencyBrokerSearch_DTO_HUM usewrapper3 = new AgencyBrokerSearch_DTO_HUM();
        usewrapper3.isUnknownAgencySearch = true;
        usewrapper3.sFirstName = 'Rahul';
        usewrapper3.sLastName = '';
        usewrapper3.sAgencyName = '';
        usewrapper3.sAgentId = '2222222';
        usewrapper3.sTaxID = '1234567';
        usewrapper3.sAgentType = 'Agency';
        usewrapper3.sState = 'Wisconsin';
        
        AgencyBrokerSearch_DTO_HUM usewrapper4 = new AgencyBrokerSearch_DTO_HUM();
        usewrapper4.isUnknownAgencySearch = true;
        usewrapper4.sFirstName = '';
        usewrapper4.sLastName = '';
        usewrapper4.sAgencyName = '';
        usewrapper4.sAgentId = '';
        usewrapper4.sTaxID = '';
        usewrapper4.sAgentType = 'Agency';
        usewrapper4.sState = '';
        
        AgencyBrokerSearch_DTO_HUM usewrapper5 = new AgencyBrokerSearch_DTO_HUM();
        usewrapper5.isUnknownAgencySearch = null;
        usewrapper5.sFirstName = '';
        usewrapper5.sLastName = '';
        usewrapper5.sAgencyName = '';
        usewrapper5.sAgentId = '';
        usewrapper5.sTaxID = '';
        usewrapper5.sAgentType = 'Agency';
        usewrapper5.sState = '';
        AgencyBrokerSearch_DTO_HUM usewrapper6 = New AgencyBrokerSearch_DTO_HUM();
        usewrapper1.isUnknownAgencySearch = true;
        usewrapper1.sFirstName = '';
        usewrapper1.sLastName = '';
        usewrapper1.sAgencyName = '';
        usewrapper1.sAgentId = '';
        usewrapper1.sTaxID = '765656456';
        usewrapper1.sAgentType = 'Broker';
        usewrapper1.sState = 'Wisconsin';
        
        Id accRecTypId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        Account oAccount = new Account();
        oAccount.LastName = 'TestingAccount';
        oAccount.RecordTypeId =accRecTypId; 
        insert oAccount; 

        String jsonAccount=  JSON.serializePretty(oAccount);
              
        Test.startTest();
        //Kajal
        Account acc=[Select id,RecordTypeId,Work_Email__c from Account where Name =: 'Rahul' Limit 1];
        ConsumerID__c cons_queryBuildTestCase1 = new ConsumerID__c();
        cons_queryBuildTestCase1.Consumer_ID__c = '123456789';
        cons_queryBuildTestCase1.ID_Type__c = 'TaxID';
        cons_queryBuildTestCase1.ETL_Record_Deleted__c = false;
        cons_queryBuildTestCase1.Account__c = acc.id;            
        insert cons_queryBuildTestCase1;
		Id [] fixedSearchResults= new Id[1];
        fixedSearchResults[0] = cons_queryBuildTestCase1.Id;
        Test.setFixedSearchResults(fixedSearchResults);
        
        AgencyBrokerSearch_LC_HUM agency = new AgencyBrokerSearch_LC_HUM();
        AgencyBrokerSearch_LC_HUM.searchAgencyBroker(usewrapper);
        AgencyBrokerSearch_LC_HUM.unknownCreateSearchAgencyBroker(usewrapper);
        AgencyBrokerSearch_LC_HUM.searchAgencyBroker(usewrapper1);
        AgencyBrokerSearch_LC_HUM.unknownCreateSearchAgencyBroker(usewrapper1);
        AgencyBrokerSearch_LC_HUM.searchAgencyBroker(usewrapper2);
        AgencyBrokerSearch_LC_HUM.unknownCreateSearchAgencyBroker(usewrapper2);
        AgencyBrokerSearch_LC_HUM.searchAgencyBroker(usewrapper3);
        AgencyBrokerSearch_LC_HUM.unknownCreateSearchAgencyBroker(usewrapper3);
        AgencyBrokerSearch_LC_HUM.searchAgencyBroker(usewrapper4);
        AgencyBrokerSearch_LC_HUM.unknownCreateSearchAgencyBroker(usewrapper4);
        AgencyBrokerSearch_LC_HUM.searchAgencyBroker(usewrapper5);
        AgencyBrokerSearch_LC_HUM.unknownCreateSearchAgencyBroker(usewrapper5);
        AgencyBrokerSearch_LC_HUM.unknownCreateSearchAgencyBroker(usewrapper6);
        AgencyBrokerSearch_LC_HUM.insertAgentAccount('["somestring1", "somestring2"]',jsonAccount,'test');
        
        Test.stopTest();  
    }
    static @isTest void testAccountSOQL(){
        AgencyBrokerSearch_DTO_HUM usewrapper2 = new AgencyBrokerSearch_DTO_HUM();
        usewrapper2.isUnknownAgencySearch = true;
        usewrapper2.sFirstName = '';
        usewrapper2.sLastName = '';
        usewrapper2.sAgencyName = '';
        usewrapper2.sAgentId = '1111111';
        usewrapper2.sTaxID = '9876543';//9876543
        usewrapper2.sAgentType = 'Broker';
        usewrapper2.sState = ''; 
        
        ConsumerID__c cus = [Select id,Consumer_ID__c from ConsumerID__c where Consumer_ID__c = '9876543'];
        Id [] fixedSearchResults2= new Id[1];
        fixedSearchResults2[0] = cus.Id;
        Test.setFixedSearchResults(fixedSearchResults2); 
        AgencyBrokerSearch_LC_HUM.searchAgencyBroker(usewrapper2);
    }  
    /*
    * Method Name : testMBEPlusService
    * Description : Test method to cover invokeMBEPlusService 
    * Return Type : NA
    * Parameters : NA
    */
    static @isTest void testMBEPlusService()
    {
        AgencyBrokerSearch_DTO_HUM usewrapper2 = new AgencyBrokerSearch_DTO_HUM();
        usewrapper2.isUnknownAgencySearch = false;
        usewrapper2.sFirstName = '';
        usewrapper2.sLastName = '';
        usewrapper2.sAgencyName = '';
        usewrapper2.sAgentId = '1132016';
        usewrapper2.sTaxID = '';
        usewrapper2.sAgentType = 'Agency';
        usewrapper2.sState = '';
        
        Test.startTest();
        Continuation conti = (Continuation)AgencyBrokerSearch_LC_HUM.searchAgencyBroker(usewrapper2);
        Test.stopTest();
    
        HttpResponse response = new HttpResponse();
        response.setBody('<?xml version="1.0" encoding="UTF-8"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"><s:Header xmlns:func="http://exslt.org/functions" xmlns:u="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><o:Security s:mustUnderstand="1" xmlns:o="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"><wsu:Timestamp wsu:Id="Timestamp-92d42a93-3286-4453-a127-9ee274d53021" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:env="http://schemas.xmlsoap.org/soap/envelope/"><wsu:Created>2020-11-04T11:40:37Z</wsu:Created><wsu:Expires>2020-11-04T11:45:37Z</wsu:Expires></wsu:Timestamp></o:Security></s:Header><s:Body><SearchProducerResponse xmlns="http://schemas.humana.com/Producer/Business/Entity/V1"><SearchProducerResult xmlns:i="http://www.w3.org/2001/XMLSchema-instance"><ErrorMessage/><ErrorNumber>0</ErrorNumber><ProducerList><Producer><BaseDate>1991-11-05T00:00:00</BaseDate><BusinessAddress><AddressLine1>1100 EMPLOYERS BLVD</AddressLine1><AddressLine2/><CityName>GREEN BAY</CityName><County>BROWN</County><StateCode>WI</StateCode><ZipCode>543440002</ZipCode></BusinessAddress><CorrespondanceName/><County>BROWN</County><DateOfBirth>9999-12-31T00:00:00</DateOfBirth><Dba/><EmailId/><FaxNbr>000-000-0000</FaxNbr><FirstName/><Gender>Nothing</Gender><HireDate>9999-12-31T00:00:00</HireDate><IsTermed>false</IsTermed><LastName/><MiddleInitialName/><Name>AGENT SEMINAR LISTING</Name><Npn>0</Npn><PrimaryPhone>630-850-9820</PrimaryPhone><ProducerType>Agency</ProducerType><ResidenceAddress><AddressLine1/><AddressLine2/><CityName/><County/><StateCode/><ZipCode/></ResidenceAddress><Rso/><SecondaryPhone>000-000-0000</SecondaryPhone><ShippingAddress><AddressLine1>1100 EMPLOYERS BLVD</AddressLine1><AddressLine2/><CityName>GREEN BAY</CityName><County>BROWN</County><StateCode>WI</StateCode><ZipCode>543440002</ZipCode></ShippingAddress><StateCode>WI</StateCode><Status>CONTR SENT</Status><StatusCode>98</StatusCode><StatusDate>1991-11-05T00:00:00</StatusDate><SuffixName/><SysAssgnNbr>1132016</SysAssgnNbr><TaxName>AGENT SEMINAR LISTING</TaxName><TaxNbr>554411227</TaxNbr><TitleName/></Producer></ProducerList></SearchProducerResult></SearchProducerResponse></s:Body></s:Envelope>');
        Test.setContinuationResponse('Continuation-1', response);
        List<String> labels = new List<String>{'Continuation-1'};   
        AgencyBrokerSearch_LC_HUM.processResponse(labels, AgencyBrokerSearch_LC_HUM.conStateWrap);
    } 
      
    static @isTest void testMBEPlusServicefalse()
    {
             
        AgencyBrokerSearch_DTO_HUM usewrapper = new AgencyBrokerSearch_DTO_HUM();
        usewrapper.isUnknownAgencySearch = false;
        usewrapper.sFirstName = '';
        usewrapper.sLastName = '';
        usewrapper.sAgencyName = '';
        usewrapper.sAgentId = '1132016';
        usewrapper.sTaxID = '';
        usewrapper.sAgentType = 'Agency';
        usewrapper.sState = '';
        
        Test.startTest();
        Continuation con = (Continuation)AgencyBrokerSearch_LC_HUM.searchAgencyBroker(usewrapper);
        Test.stopTest();
    
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(400);
        String resBodyString = '';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);   
        AgencyBrokerSearch_LC_HUM.processResponse(labelGetmember, AgencyBrokerSearch_LC_HUM.conStateWrap);
    } 
    @isTest
    public static void testFunction(){
        List<Account> lstTempAccount = new List<Account>();
        List<Account> lstMembers = new List<Account>();
        User SUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(SUser.UserName,'user1user1@humcrmstest.com');
        Id accRecTypId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
        AgencyBrokerSearch_DTO_HUM usewrapper = new AgencyBrokerSearch_DTO_HUM();
        usewrapper.isUnknownAgencySearch = false;
        usewrapper.sFirstName = '';
        usewrapper.sLastName = 'test';
        usewrapper.sAgencyName = 'test';
        usewrapper.sAgentId = '1132016';
        usewrapper.sTaxID = '';
        usewrapper.sAgentType = 'All';
        usewrapper.sState = '';
        
        AgencyBrokerSearch_LD_HUM.createPlatformQueries(usewrapper);
            
        AgencyBrokerSearch_DTO_HUM usewrapper1 = New AgencyBrokerSearch_DTO_HUM();
        usewrapper.isUnknownAgencySearch = true;
        usewrapper.sFirstName = '';
        usewrapper.sLastName = 'test';
        usewrapper.sAgencyName = 'test';
        usewrapper.sAgentId = '1132016';
        usewrapper.sTaxID = '';
        usewrapper.sAgentType = 'Agency';
        usewrapper.sState = '';
        
        Account oAcc1 = HUMTestDataHelperTest.getAccount();
        oAcc1.Individual_First_Name__c = '';
        oAcc1.Individual_Last_Name__c = '';
        oAcc1.Name = 'New Account';
        oAcc1.recordtypeid = accRecTypId;
        oAcc1.Agent_ID__c = '1111111';
        oAcc1.Agent_Type__c = 'Broker';
        oAcc1.BillingStreet = 'abc';
        oAcc1.BillingPostalCode='53226';
        oAcc1.BillingState = 'Wisconsin';
        insert oAcc1;
        lstTempAccount.add(oAcc1);
        
        Account oAcc2 = HUMTestDataHelperTest.getAccount();
        oAcc2.Individual_First_Name__c = 'Ashish';
        oAcc2.Individual_Last_Name__c = '';
        oAcc2.Name = 'New Account';
        oAcc2.recordtypeid = accRecTypId;
        oAcc2.Agent_ID__c = '1111111';
        oAcc2.Agent_Type__c = 'Broker';
        oAcc2.BillingStreet = 'abc';
        oAcc2.BillingPostalCode='53226';
        oAcc2.BillingState = 'Wisconsin';
        insert oAcc2;
        lstMembers.add(oAcc2);
        
        AgencyBrokerSearch_DTO_HUM usewrapper6 = New AgencyBrokerSearch_DTO_HUM();
        usewrapper6.isUnknownAgencySearch = true;
        usewrapper6.sFirstName = 'first';
        usewrapper6.sLastName = 'last';
        usewrapper6.sAgencyName = '';
        usewrapper6.sAgentId = '';
        usewrapper6.sTaxID = '765656456';
        usewrapper6.sAgentType = 'Broker';
        usewrapper6.sState = 'Wisconsin';
        
        Test.startTest();
        AgencyBrokerSearch_LD_HUM.FilterSOSLResults(lstTempAccount,lstMembers,usewrapper6);
        AgencyBrokerSearch_LD_HUM.FilterSOSLResults(lstTempAccount,lstMembers,New AgencyBrokerSearch_DTO_HUM());
        
        AgencyBrokerSearch_LD_HUM.createPlatformQueries(usewrapper);
        UnknownAgencyBrokerSearch_LD_HUM.createPlatformQueries(usewrapper1);
        usewrapper.sFirstName = 'test';
        AgencyBrokerSearch_LD_HUM.createPlatformQueries(usewrapper);
        UnknownAgencyBrokerSearch_LD_HUM.createPlatformQueries(usewrapper1);
        AgencyBrokerSearch_LD_HUM.FilterSOSLResults(lstTempAccount,lstMembers,usewrapper);
        usewrapper.sAgentType = 'Agency';
        map<String,String> mapNormalAndEncryptedIds = new map<String,String>();
        mapNormalAndEncryptedIds.put('String1', 'String1');
        AgencyBrokerSearch_LD_HUM.upsertConsumerTaxIds(oAcc2.Id,mapNormalAndEncryptedIds);
        List<ConsumerID__c> consumerIds = [select id,Account__c from ConsumerID__c];
        AgencyBrokerSearch_LD_HUM.prepareConsumerIDMap(consumerIds);

        Account acc=[Select id,RecordTypeId,Work_Email__c from Account where Name =: 'Rahul' Limit 1];//New Account2
        
        AgencyBrokerSearch_LD_HUM.getEmailForAgentAccount(acc);
        Test.stopTest(); 
        
        
    }
}