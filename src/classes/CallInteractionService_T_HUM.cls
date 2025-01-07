/***************************************************************************************************************************************************************************
    Apex Class Name  : CallInteractionService_T_HUM
    Version          : 1.0
    Created Date     : July 4 2021
    Function         : This Rest Webservice is used to for all the Call creation unit test related services.
    ****************************************************************************************************************************************************************************

    Modification Log:

    * Developer Name                    Date                        Description
    *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    * Moshitha Gunasekaran              07/04/2021                  Original Version
    * Moshitha Gunasekaran              11/09/2021                  Added additional paramters for CBIS
****************************************************************************************************************************************************************************/

@isTest
private class CallInteractionService_T_HUM {
    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
    {
    
        User oUser = HUMTestDataHelperTest.createTestUser('CallInteractUser1','System Administrator');
        insert oUser;
        User oUser1 = HUMTestDataHelperTest.createTestUser('CallInteractUser2','ETL API Access');
        oUser1.Network_User_Id__c = 'ETTEST1';
        insert oUser1;
        system.assertEquals(oUser.lastname == 'test CallInteractUser1',true);

        System.runAs(oUser) 
        {
            //Test Data Created For Agent
            RecordType AgentAccountRecordType = [Select Id from RecordType where Name = 'Agent/Broker'
            and sObjectType = 'Account' ];
            System.assert(AgentAccountRecordType != null);
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'test account';
            oAcc.Group_Number__c = '123456';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assert(oAcc.Name == 'test account');
                //Provider Account
                Account ProviderAcc = HUMTestDataHelperTest.getAccount();
                ProviderAcc.name = 'test12 provider12';
                ProviderAcc.Enterprise_ID__c = '12345678';
                ProviderAcc.NPI_ID__c = '1234567890';
                ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
                insert ProviderAcc;
                System.assert(ProviderAcc.name == 'test12 provider12');
                ConsumerID__c cons_queryBuildTestCase = new ConsumerID__c();
                cons_queryBuildTestCase.Consumer_ID__c = '555555555';
                cons_queryBuildTestCase.ID_Type__c = Label.HumServiceTaxIDtag;
                cons_queryBuildTestCase.ETL_Record_Deleted__c = false;
                cons_queryBuildTestCase.Account__c = ProviderAcc.id;
                insert cons_queryBuildTestCase;
                //Agent Account
                Account AgentAcc = new Account();
                AgentAcc = HUMTestDataHelperTest.getAccount();
                AgentAcc.Name = 'Agent Account';
                AgentAcc.Enterprise_ID__c = '1234567';
                AgentAcc.Agent_ID__c = '1234567';
                AgentAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
                insert AgentAcc;
                System.assert(AgentAcc.Name == 'Agent Account');
                //Member Account
                Account acc = new Account( Enterprise_Id__c = '223300104', Mbr_Gen_Key__c='2233001046935', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
                insert acc;
                Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
                insert testProduct;
                Policy__c testPolicy = new Policy__c(Group_Name__c = acc.ID, Major_LOB__c = 'MER', Product__c = testProduct.ID,Policy_Group_Number__c='11111');
                insert testPolicy;
                Policy_Member__c memeber = new Policy_Member__c(Policy__c = testPolicy.ID, Member__c = acc.ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
                insert memeber;
                Member_ID__c omemberId = new Member_ID__c(Policy__c = testPolicy.ID,Policy_Member__c = memeber.ID, Member_Card_External_ID__c= '123345' , name = 'H46738930', Member_Dependent_Code__c = '00', Type__c ='Member-Id-Base');
                insert omemberId;
                Interaction__c oInteraction = new Interaction__c(Interaction_Origin__c = 'InboundCall', UUID__c='128459461', Caller_Name__c='Guest Guestuser');
                insert oInteraction;

                HUMConstants__c test1  = new  HUMConstants__c();
                test1.name = 'UNKNOWN_MEMBER';
                test1.stringValue__C = 'Unknown Member';
                            
                insert test1;
                system.assertNotEquals(test1,null, 'Value not matched');

                HUMSearchLimit__c humSearch = new HUMSearchLimit__c();
                humSearch.Name = 'Member_SearchLimit';
                humSearch.Search_Limit__c = 101;
                insert humSearch;

                insert new CRMFunctionality_ONOFF_Switch__c(Name = '2854267',IsON__c=true,Release_Name__c='12/10/2021',Scrum_Team__c='Transformers',Story_Title__c='Add paramters for CBIS');
                //Custom Setting for EPASS Service which is used in provider search.
                HUM_Webservice_Callout__c EPSServiceDetails = new HUM_Webservice_Callout__c();
                EPSServiceDetails.Name = 'EPSServiceREST_V1';
                EPSServiceDetails.Certificate_Name__c = 'Humana_GuidanceCare_MOCK';
                EPSServiceDetails.Active__c = true;
                EPSServiceDetails.End_Point_URL__c = 'https://int-crmservicews.humana.com/EPSServiceREST_V1.0/EnterpriseProviderSearch';
                EPSServiceDetails.Timeout__c = '12000';
                EPSServiceDetails.Request_Type__c = 'POST';
                EPSServiceDetails.Content_Type__c = 'application/json';
                insert EPSServiceDetails;
                System.assert(EPSServiceDetails.id != null);
                //Custom Setting for Agency Service which is used in Agent search.
                HUM_Webservice_Callout__c AgencyServiceDetails = new HUM_Webservice_Callout__c();
                AgencyServiceDetails.Name = 'AgencySearchService';
                AgencyServiceDetails.Certificate_Name__c = 'Humana_GuidanceCare_MOCK';
                AgencyServiceDetails.Active__c = true;
                AgencyServiceDetails.End_Point_URL__c = 'https://int-crmservicews.humana.com/Producer/Entity/Producer.svc';
                AgencyServiceDetails.Timeout__c = '12000';
                AgencyServiceDetails.Request_Type__c = 'POST';
                AgencyServiceDetails.Content_Type__c = 'application/json';
                insert AgencyServiceDetails;
                System.assert(AgencyServiceDetails.id != null);
        }
    }
    
    /**
    * This is a test method for CallInteractionService for Member and Provider
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CallInteractionService_Member()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test CallInteractUser2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
            Member_Id__c oMember = [select id, Name from Member_Id__c where Name ='H46738930' limit 1];
            id[] fixedSearchResults = new id[1];
            fixedSearchResults[0] = oMember.id;
            Test.setFixedSearchResults(fixedSearchResults);
            Interaction__c oInteraction = [select id, UUID__c from Interaction__c where UUID__c='128459461' limit 1];
            CallInteractionRequest_DTO_HUM request = new CallInteractionRequest_DTO_HUM();
            request.InteractingWithType = 'Member';
            request.InteractingWithID = 'H46738930';
            request.InteractingWithDepCode = '00';
            request.InteractingWithFirstName = 'test1';
            request.InteractingWithLastName = 'method1';
            request.InteractingWithDOB = '03/07/2021';
            request.InteractingAboutType = 'Provider';
            request.InteractingAboutID = '555555555';
            request.InteractingAboutFirstName = '';
            request.InteractingAboutLastName = '';
            request.InteractingAboutDOB = '';
            request.InteractingAboutNPIID = '1234567890';
            request.InteractingFirstName = 'Guest';
            request.InteractingLastName = 'Guestuser';
            request.UUID = '4567894651321321231';
            request.CallCategory = 'GTesteCate';
            request.InteractionOrigin = 'InboundCall';
            request.CallUserId = 'ETTEST1';
            request.CallCategoryDescription = 'GEDescription';
            request.IsAuthenticated = false;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CallInteractionInfo/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CallInteractionService.CallCreateInteractionInfo();
            CaseInteractionSearch_H_HUM.GetCallAccountDetails(request,oInteraction);
            Test.StopTest();
        }
    }

    /**
    * This is a test method for CallInteractionService for Member 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CallInteractionService_MemberSame()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test CallInteractUser2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
            Member_Id__c oMember = [select id, Name from Member_Id__c where Name ='H46738930' limit 1];
            id[] fixedSearchResults = new id[1];
            fixedSearchResults[0] = oMember.id;
            Test.setFixedSearchResults(fixedSearchResults);
            Interaction__c oInteraction = [select id, UUID__c from Interaction__c where UUID__c='128459461' limit 1];
            CallInteractionRequest_DTO_HUM request = new CallInteractionRequest_DTO_HUM();
            request.InteractingWithType = 'Member';
            request.InteractingWithID = 'H4673893000';
            request.InteractingWithFirstName = 'test1';
            request.InteractingWithLastName = 'method1';
            request.InteractingWithDOB = '03/07/2021';
            request.InteractingAboutType = 'Member';
            request.InteractingAboutID = 'H4673893000';
            request.InteractingAboutFirstName = 'test1';
            request.InteractingAboutLastName = 'test1';
            request.InteractingAboutDOB = '';
            request.InteractingAboutNPIID = '';
            request.InteractingFirstName = 'Guest';
            request.InteractingLastName = 'Guestuser';
            request.UUID = '4567894651321321231';
            request.CallCategory = 'GTesteCate';
            request.InteractionOrigin = 'InboundCall';
            request.CallUserId = 'ETTEST1';
            request.CallCategoryDescription = 'GEDescription';
            request.IsAuthenticated = true;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CallInteractionInfo/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CallInteractionService.CallCreateInteractionInfo();
            CaseInteractionSearch_H_HUM.GetCallAccountDetails(request,oInteraction);
            Test.StopTest();
        }
    }

    /**
    * This is a test method for CallInteractionService for Provider and Agent
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CallInteractionService_Provider()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test CallInteractUser2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
            Interaction__c oInteraction = [select id, UUID__c from Interaction__c where UUID__c='128459461' limit 1];
            CallInteractionRequest_DTO_HUM request = new CallInteractionRequest_DTO_HUM();
            request.InteractingWithType = 'Provider';
            request.InteractingWithID = '555555555';
            request.InteractingWithFirstName = 'test12';
            request.InteractingWithLastName = 'provider12';
            request.InteractingWithDOB = '03/07/2021';
            request.InteractingWithNPIID = '1234567890';
            request.InteractingAboutType = 'Agent';
            request.InteractingAboutID = '1234567';
            request.InteractingAboutFirstName = '';
            request.InteractingAboutLastName = '';
            request.InteractingAboutDOB = '';
            request.InteractingAboutNPIID = '';
            request.InteractingFirstName = 'Guest';
            request.InteractingLastName = 'Guestuser';
            request.UUID = '4567894651321321231';
            request.CallCategory = 'GTesteCate';
            request.InteractionOrigin = 'InboundCall';
            request.CallUserId = 'ETTEST1';
            request.CallCategoryDescription = 'GEDescription';
            request.IsAuthenticated = false;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CallInteractionInfo/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CallInteractionService.CallCreateInteractionInfo();
            CaseInteractionSearch_H_HUM.GetCallAccountDetails(request,oInteraction);
            Test.StopTest();
        }
    }

    /**
    * This is a test method for CallInteractionService for Provider
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CallInteractionService_ProviderSame()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test CallInteractUser2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
            Interaction__c oInteraction = [select id, UUID__c from Interaction__c where UUID__c='128459461' limit 1];
            CallInteractionRequest_DTO_HUM request = new CallInteractionRequest_DTO_HUM();
            request.InteractingWithType = 'Provider';
            request.InteractingWithID = '555555555';
            request.InteractingWithFirstName = 'test12';
            request.InteractingWithLastName = 'provider12';
            request.InteractingWithDOB = '03/07/2021';
            request.InteractingWithNPIID = '1234567890';
            request.InteractingAboutType = 'Provider';
            request.InteractingAboutID = '555555555';
            request.InteractingAboutFirstName = 'test12';
            request.InteractingAboutLastName = 'provider12';
            request.InteractingAboutDOB = '';
            request.InteractingAboutNPIID = '1234567890';
            request.InteractingFirstName = 'Guest';
            request.InteractingLastName = 'Guestuser';
            request.UUID = '4567894651321321231';
            request.CallCategory = 'GTesteCate';
            request.InteractionOrigin = 'InboundCall';
            request.CallUserId = 'ETTEST1';
            request.CallCategoryDescription = 'GEDescription';
            request.IsAuthenticated = false;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CallInteractionInfo/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CallInteractionService.CallCreateInteractionInfo();
            CaseInteractionSearch_H_HUM.GetCallAccountDetails(request,oInteraction);
            Test.StopTest();
        }
    }

    /**
    * This is a test method for CallInteractionService for Agent and Group
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CallInteractionService_Agent()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test CallInteractUser2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
            Interaction__c oInteraction = [select id, UUID__c from Interaction__c where UUID__c='128459461' limit 1];
            CallInteractionRequest_DTO_HUM request = new CallInteractionRequest_DTO_HUM();
            request.InteractingWithType = 'Agent';
            request.InteractingWithID = '1234567';
            request.InteractingWithFirstName = '';
            request.InteractingWithLastName = '';
            request.InteractingWithDOB = '';
            request.InteractingWithNPIID = '';
            request.InteractingAboutType = 'Group';
            request.InteractingAboutID = '123456';
            request.InteractingAboutFirstName = '';
            request.InteractingAboutLastName = '';
            request.InteractingAboutDOB = '';
            request.InteractingAboutNPIID = '';
            request.InteractingFirstName = 'Guest';
            request.InteractingLastName = 'Guestuser';
            request.UUID = '4567894651321321231';
            request.CallCategory = 'GTesteCate';
            request.InteractionOrigin = 'InboundCall';
            request.CallUserId = 'ETTEST1';
            request.CallCategoryDescription = 'GEDescription';
            request.IsAuthenticated = false;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CallInteractionInfo/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CallInteractionService.CallCreateInteractionInfo();
            CaseInteractionSearch_H_HUM.GetCallAccountDetails(request,oInteraction);
            Test.StopTest();
        }
    }

    /**
    * This is a test method for CallInteractionService for Agent
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CallInteractionService_AgentSame()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test CallInteractUser2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
            Interaction__c oInteraction = [select id, UUID__c from Interaction__c where UUID__c='128459461' limit 1];
            CallInteractionRequest_DTO_HUM request = new CallInteractionRequest_DTO_HUM();
            request.InteractingWithType = 'Agent';
            request.InteractingWithID = '1234567';
            request.InteractingWithFirstName = '';
            request.InteractingWithLastName = '';
            request.InteractingWithDOB = '';
            request.InteractingWithNPIID = '';
            request.InteractingAboutType = 'Agent';
            request.InteractingAboutID = '1234567';
            request.InteractingAboutFirstName = '';
            request.InteractingAboutLastName = '';
            request.InteractingAboutDOB = '';
            request.InteractingAboutNPIID = '';
            request.InteractingFirstName = 'Guest';
            request.InteractingLastName = 'Guestuser';
            request.UUID = '4567894651321321231';
            request.CallCategory = 'GTesteCate';
            request.InteractionOrigin = 'InboundCall';
            request.CallUserId = 'ETTEST1';
            request.CallCategoryDescription = 'GEDescription';
            request.IsAuthenticated = false;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CallInteractionInfo/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CallInteractionService.CallCreateInteractionInfo();
            CaseInteractionSearch_H_HUM.GetCallAccountDetails(request,oInteraction);
            Test.StopTest();
        }
    }

    /**
    * This is a test method for CallInteractionService for Group and Member
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CallInteractionService_Group()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test CallInteractUser2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
            Member_Id__c oMember = [select id, Name from Member_Id__c where Name ='H46738930' limit 1];
            id[] fixedSearchResults = new id[1];
            fixedSearchResults[0] = oMember.id;
            Test.setFixedSearchResults(fixedSearchResults);
            Interaction__c oInteraction = [select id, UUID__c from Interaction__c where UUID__c='128459461' limit 1];
            CallInteractionRequest_DTO_HUM request = new CallInteractionRequest_DTO_HUM();
            request.InteractingWithType = 'Group';
            request.InteractingWithID = '123456';
            request.InteractingWithFirstName = '';
            request.InteractingWithLastName = '';
            request.InteractingWithDOB = '';
            request.InteractingWithNPIID = '';
            request.InteractingAboutType = 'Member';
            request.InteractingAboutID = 'H4673893000';
            request.InteractingAboutGenkey = '2233001046935';
            request.InteractingAboutFirstName = 'test1';
            request.InteractingAboutLastName = 'method1';
            request.InteractingAboutDOB = '';
            request.InteractingAboutNPIID = '';
            request.InteractingFirstName = 'Guest';
            request.InteractingLastName = 'Guestuser';
            request.UUID = '4567894651321321231';
            request.CallCategory = 'GTesteCate';
            request.InteractionOrigin = 'InboundCall';
            request.CallUserId = 'ETTEST1';
            request.CallCategoryDescription = 'GEDescription';
            request.IsAuthenticated = true;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CallInteractionInfo/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CallInteractionService.CallCreateInteractionInfo();
            CaseInteractionSearch_H_HUM.GetCallAccountDetails(request,oInteraction);
            Test.StopTest();
        }
    }

    /**
    * This is a test method for CallInteractionService for Group
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CallInteractionService_GroupSame()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test CallInteractUser2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
            Interaction__c oInteraction = [select id, UUID__c from Interaction__c where UUID__c='128459461' limit 1];
            CallInteractionRequest_DTO_HUM request = new CallInteractionRequest_DTO_HUM();
            request.InteractingWithType = 'Group';
            request.InteractingWithID = '123456';
            request.InteractingWithFirstName = '';
            request.InteractingWithLastName = '';
            request.InteractingWithDOB = '';
            request.InteractingWithNPIID = '';
            request.InteractingAboutType = 'Group';
            request.InteractingAboutID = '123456';
            request.InteractingAboutGenkey = '';
            request.InteractingAboutFirstName = '';
            request.InteractingAboutLastName = '';
            request.InteractingAboutDOB = '';
            request.InteractingAboutNPIID = '';
            request.InteractingFirstName = 'Guest';
            request.InteractingLastName = 'Guestuser';
            request.UUID = '4567894651321321231';
            request.CallCategory = 'GTesteCate';
            request.InteractionOrigin = 'InboundCall';
            request.CallUserId = 'ETTEST1';
            request.CallCategoryDescription = 'GEDescription';
            request.IsAuthenticated = true;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CallInteractionInfo/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CallInteractionService.CallCreateInteractionInfo();
            CaseInteractionSearch_H_HUM.GetCallAccountDetails(request,oInteraction);
            Test.StopTest();
        }
    }

    /**
    * This is a test method for CallInteractionService for MemberId and Genkey
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CallInteractionService_MemberId()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test CallInteractUser2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
            Member_Id__c oMember = [select id, Name from Member_Id__c where Name ='H46738930' limit 1];
            id[] fixedSearchResults = new id[1];
            fixedSearchResults[0] = oMember.id;
            Test.setFixedSearchResults(fixedSearchResults);
            Interaction__c oInteraction = [select id, UUID__c from Interaction__c where UUID__c='128459461' limit 1];
            CallInteractionRequest_DTO_HUM request = new CallInteractionRequest_DTO_HUM();
            request.InteractingWithType = 'Member';
            request.InteractingWithID = 'H4673893000';
            request.InteractingWithGenkey = '22330046935';
            request.InteractingWithFirstName = 'test1';
            request.InteractingWithLastName = 'method1';
            request.InteractingWithDOB = '';
            request.InteractingWithNPIID = '';
            request.InteractingAboutType = 'Member';
            request.InteractingAboutID = 'H46733000';
            request.InteractingAboutGenkey = '223300046935';
            request.InteractingAboutFirstName = 'test1';
            request.InteractingAboutLastName = 'method1';
            request.InteractingAboutDOB = '';
            request.InteractingAboutNPIID = '';
            request.InteractingFirstName = 'Guest';
            request.InteractingLastName = 'Guestuser';
            request.UUID = '4567894651321321231';
            request.CallCategory = 'GTesteCate';
            request.InteractionOrigin = 'InboundCall';
            request.CallUserId = 'ETTEST1';
            request.CallCategoryDescription = 'GEDescription';
            request.IsAuthenticated = true;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CallInteractionInfo/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CallInteractionService.CallCreateInteractionInfo();
            CaseInteractionSearch_H_HUM.GetCallAccountDetails(request,oInteraction);
            CaseInteractionSearch_H_HUM.getCallMemberRecords('H46738930', '00','test1', 'method1', 'CB', true);
            Test.StopTest();
        }
    }

    /**
    * This is a test method for CallInteractionService for Error
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CallInteractionService_Error()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test CallInteractUser2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
           
            CallInteractionRequest_DTO_HUM request = null;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CallInteractionInfo/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CallInteractionService.CallCreateInteractionInfo();
            Test.StopTest();
        }
    }
}