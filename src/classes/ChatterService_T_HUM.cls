/***************************************************************************************************************************************************************************
    Apex Class Name  : ChatterService_T_HUM
    Version          : 1.0
    Created Date     : May 13 2021
    Function         : This Rest Webservice is used to for all the chatter related services.
    ****************************************************************************************************************************************************************************

    Modification Log:

    * Developer Name                  Date                        Description
    *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    * Moshitha Gunasekaran            05/13/2021                  Original Version
    * Moshitha Gunasekaran            06/14/2021                  Made changes as per latest ICD to add FeedItemId and Date functionalities				
    * Moshitha Gunasekaran            07/27/2021                  Made changes as per latest ICD to replace CarePlanId with Subject
    * Moshitha Gunasekaran            08/04/2021                  User Story 2495909:  T1PRJ0002081 / PR00092569 **Coaching** Out Of Office feature in Chatter
    * Moshitha Gunasekaran            11/11/2021                  User Story 2864451 - T1PRJ0002081 / PR00092569 - PLT - TECH- UI - **Coaching ** - PLT - Mobile - Feed item not associated with the Coach
****************************************************************************************************************************************************************************/
@isTest
private class ChatterService_T_HUM {
    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
     @testSetup static void setup()
     {
        User oUser = HUMTestDataHelperTest.createTestUser('ChatterUser1','System Administrator');
        insert oUser;
        User oUser1 = HUMTestDataHelperTest.createTestUser('ChatterUser2','ETL API Access');
        insert oUser1;
        system.assertEquals(oUser1.lastname=='test ChatterUser2',true);
        User oUser2 = HUMTestDataHelperTest.createTestUser('CoachUser','System Administrator');
        oUser2.FirstName = 'firsttest CoachUser';
        insert oUser2;
        User oUser3 = HUMTestDataHelperTest.createTestUser('CoachOOOUser','System Administrator');
        oUser3.FirstName = 'secondtest CoachOOOUser';
        insert oUser3;
        Id sAccount1 ;
        Id sAccount2 ;
        
        string sFeedItem1 = '';

        System.runAs(oUser) 
        {
            Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
            insert acc;
            
            CollaborationGroup collGroup = new CollaborationGroup (Name = 'CoachingTesting', CollaborationType = 'Private');
            insert collGroup;

            Account acc1 = new Account( Enterprise_Id__c = '223300105', firstname = 'test12' ,lastname = 'method12' , Birthdate__c= '01/02/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'KY', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
            insert acc1;
            sAccount1 = acc1.Id;

            Account acc2 = new Account( Enterprise_Id__c = '223300109', firstname = 'test123' ,lastname = 'method123' , Birthdate__c= '03/01/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
            insert acc2;
            sAccount2 = acc2.Id;
                        
            FeedItem feedChatter = new FeedItem(ParentId = acc.Id, Type = 'TextPost', IsRichText = true, Body = 'Testing For Unit class. CarePlanSubject:Blood Pressure');
            insert feedChatter;
            
            FeedItem feedChatter1 = new FeedItem(ParentId = acc.Id, Type = 'TextPost', IsRichText = false, Body = 'Testing For Unit class coverage. CarePlanSubject:Blood Pressure');
            insert feedChatter1;
            
            FeedItem feedChatter2 = new FeedItem(ParentId = acc2.Id, Type = 'TextPost', IsRichText = false, Body = 'Testing For Unit class coverage. CarePlanSubject:Blood Pressure');
            insert feedChatter2;
            sFeedItem1 = feedChatter2.Id;
            
            list<FeedComment> feedComtlst = new list<FeedComment>();
            FeedComment feedCommentChatter1 = new FeedComment(FeedItemId = feedChatter.Id, IsRichText = false, CommentBody = 'Testing For class Commennts');
            feedComtlst.add(feedCommentChatter1);
            FeedComment feedCommentChatter2 = new FeedComment(FeedItemId = feedChatter.Id, IsRichText = false, CommentBody = '/Coach FirstName:- Testing For Unit class Commennts');
            feedComtlst.add(feedCommentChatter2);
            FeedComment feedCommentChatter3 = new FeedComment(FeedItemId = feedChatter1.Id, IsRichText = false, CommentBody = 'Testing For class Commennts');
            feedComtlst.add(feedCommentChatter3);
            FeedComment feedCommentChatter4 = new FeedComment(FeedItemId = feedChatter2.Id, IsRichText = false, CommentBody = 'Testing For class Commennts');
            feedComtlst.add(feedCommentChatter4);
            insert feedComtlst;
            
            OutOfOffice oOOOUser = new OutOfOffice();
            oOOOUser.Message = 'Not available during this period';
            oOOOUser.StartDate = system.Today();
            oOOOUser.EndDate = system.Today().addDays(1);
            oOOOUser.UserId = oUser3.Id;
            oOOOUser.IsEnabled = true;
            insert oOOOUser;

            insert new CRMFunctionality_ONOFF_Switch__c(Name = '2377268',IsON__c=true,Release_Name__c='07/23/2021',Scrum_Team__c='Transformers',Story_Title__c='User Story 2377268: T1PRJ0002081 / PR00092569 - MF #1799660  - SF - **Coaching ** - Push Notification to Go365');
            insert new HUM_Webservice_Callout__c(Name='PushNotificationService', Certificate_Name__c='Humana_GuidanceCare_MOCK', SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/PushNotificationService/SendMessage', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='application/json',Methods_Available__c='A;B;C',Enable_Service_Tester__c=true);
        }

        System.runAs(oUser2) 
        {
            FeedItem feedChatter = new FeedItem(ParentId = sAccount1, Type = 'TextPost', IsRichText = true, Body = 'Testing For Unit class. CarePlanSubject:Blood Pressure');
            insert feedChatter;
            
            Id caseRecordType = Schema.SObjectType.Case.getRecordTypeInfosByName().get('CarePlan').getRecordTypeId();
            Case caseObj1 = HUMTestDataHelperTest.getCase();
            caseObj1.Owner_Queue__c = 'Wellness Humana Wellness Solutions';
            caseObj1.Work_Queue_View_Name__c='Other';
            caseObj1.accountId = sAccount1;
            caseObj1.Interacting_With__c = sAccount1;
            caseObj1.Interacting_With_Name__c = 'Test Name';
            caseobj1.Interacting_With_Type__c = 'Member';
            caseobj1.RecordTypeId = caseRecordType;
            caseobj1.Status = 'In Progress';
            caseobj1.OwnerId = oUser2.Id;
            caseobj1.Area_of_Focus__c = 'Blood Pressure';
            insert caseObj1 ;

            Case caseObj2 = HUMTestDataHelperTest.getCase();
            caseObj2.Owner_Queue__c = 'Wellness Humana Wellness Solutions';
            caseObj2.Work_Queue_View_Name__c='Other';
            caseObj2.accountId = sAccount2;
            caseObj2.Interacting_With__c = sAccount2;
            caseObj2.Interacting_With_Name__c = 'Test Name';
            caseObj2.Interacting_With_Type__c = 'Member';
            caseObj2.RecordTypeId = caseRecordType;
            caseObj2.Status = 'In Progress';
            caseObj2.OwnerId = oUser2.Id;
            caseObj2.Area_of_Focus__c = 'Blood Pressure';
            insert caseObj2 ;

            Case caseObj3 = HUMTestDataHelperTest.getCase();
            caseObj3.Owner_Queue__c = 'Wellness Humana Wellness Solutions';
            caseObj3.Work_Queue_View_Name__c='Other';
            caseObj3.accountId = sAccount2;
            caseObj3.Interacting_With__c = sAccount2;
            caseObj3.Interacting_With_Name__c = 'Test Name';
            caseObj3.Interacting_With_Type__c = 'Member';
            caseObj3.RecordTypeId = caseRecordType;
            caseObj3.Status = 'In Progress';
            caseObj3.OwnerId = oUser3.Id;
            caseObj3.Area_of_Focus__c = 'Tobacco';
            insert caseObj3 ;
            

            FeedComment feedCommentChatter1 = new FeedComment(FeedItemId = sFeedItem1, IsRichText = false, CommentBody = 'Testing For class Commennts');
            insert feedCommentChatter1;

        }
     }
     
     
    /**
    * This is a test method for Post feed Item for Enrollment FeedType
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedItemEnrollment()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            ChatterRequestDTO chatterRequest = new ChatterRequestDTO();
            chatterRequest.FeedElementType = 'Enrollment & Generic';
            chatterRequest.PersonId = '223300104';
            chatterRequest.MessageText = 'Testing Unit test coverage';
                        
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(chatterRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/ChatterService/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            ChatterService.postFeedGroup();
            
            Test.stopTest();
        }
    }

    /**
    * This is a test method for Post feed comment for Enrollment FeedType
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedCommentEnrollment()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            FeedItem feedItemRequest = [SELECT Id,ParentId FROM FeedItem where ParentId in (Select Id FROM Account where Enterprise_Id__c = '223300104' ) LIMIT 1];

            ChatterRequestDTO chatterRequest1 = new ChatterRequestDTO();
            chatterRequest1.FeedElementType = 'Enrollment & Generic';
            chatterRequest1.PersonId = '223300104';
            chatterRequest1.FeedItemId = feedItemRequest.Id;
            chatterRequest1.MessageText = 'Testing Unit test coverage';
            
            RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(chatterRequest1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/ChatterService/*';  //Request URL
            req1.httpMethod = 'PUT';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            Test.startTest();
            ChatterService.postFeedGroup();
            
            Test.stopTest();
        }
    }

    /**
    * This is a test method for Post feed Item for Coach FeedType
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedItemCoach()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            ChatterRequestDTO chatterRequest = new ChatterRequestDTO();
            chatterRequest.FeedElementType = 'Coach Message';
            chatterRequest.PersonId = '223300105';
            chatterRequest.MessageText = 'Testing Unit test coverage';
            chatterRequest.Subject = 'Blood Pressure';
                        
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(chatterRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/ChatterService/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            ChatterService.postFeedGroup();
            
            Test.stopTest();
        }
    }

    /**
    * This is a test method for Post feed Item for Coach FeedType
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedItemCoachOOO()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            ChatterRequestDTO chatterRequest = new ChatterRequestDTO();
            chatterRequest.FeedElementType = 'Coach Message';
            chatterRequest.PersonId = '223300109';
            chatterRequest.MessageText = 'Testing Unit test coverage';
            chatterRequest.Subject = 'Tobacco';
                        
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(chatterRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/ChatterService/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            ChatterService.postFeedGroup();
            
            Test.stopTest();
        }
    }
    
    /**
    * This is a test method for Post feed Comment for Coach FeedType
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedCommentCoach()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            FeedItem feedItemRequest = [SELECT Id,ParentId FROM FeedItem where ParentId in (Select Id FROM Account where Enterprise_Id__c = '223300105' ) LIMIT 1];

            ChatterRequestDTO chatterRequest = new ChatterRequestDTO();
            chatterRequest.FeedElementType = 'Coach Message';
            chatterRequest.PersonId = '223300105';
            chatterRequest.MessageText = 'Testing Unit test coverage';
            chatterRequest.Subject = 'Blood Pressure';
            chatterRequest.FeedItemId = feedItemRequest.Id;
                        
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(chatterRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/ChatterService/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            ChatterService.postFeedGroup();
            
            Test.stopTest();
        }
    }

    /**
    * This is a test method for Post feed Comment for Coach FeedType
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedCommentCoachNoSub()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        System.runAs(oUser) 
        {
            FeedItem feedItemRequest = [SELECT Id,ParentId FROM FeedItem where ParentId in (Select Id FROM Account where Enterprise_Id__c = '223300105' ) LIMIT 1];
            ChatterRequestDTO chatterRequest = new ChatterRequestDTO();
            chatterRequest.FeedElementType = 'Coach Message';
            chatterRequest.PersonId = '223300105';
            chatterRequest.MessageText = 'Testing Unit test coverage';
            chatterRequest.FeedItemId = feedItemRequest.Id;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(chatterRequest);
            RestResponse res = new RestResponse();
            req.requestURI = '/ChatterService/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            ChatterService.postFeedGroup();
            Test.stopTest();
        }
    }
    /**
    * This is a test method for Post feed Comment for Coach FeedType
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedCommentCoachOOO()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            User oUser1 = [SELECT Id,lastname FROM User  WHERE Lastname='test CoachOOOUser' LIMIT 1];
            FeedItem feedItemRequest = [SELECT Id,ParentId FROM FeedItem where ParentId in (Select Id FROM Account where Enterprise_Id__c = '223300109' ) LIMIT 1];

            ChatterRequestDTO chatterRequest = new ChatterRequestDTO();
            chatterRequest.FeedElementType = 'Coach Message';
            chatterRequest.PersonId = '223300109';
            chatterRequest.MessageText = 'Testing Unit test coverage';
            chatterRequest.Subject = 'Tobacco';
            chatterRequest.FeedItemId = feedItemRequest.Id;
                        
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(chatterRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/ChatterService/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            ChatterFeed_D_HUM.postOOOFeedComment(feedItemRequest.ParentId,oUser1.Id,feedItemRequest.Id);
            
            Test.stopTest();
        }
    }

    /**
    * This is a test method for Post feed Comment for Coach FeedType
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedCommentCoachComment()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            FeedItem feedItemRequest = [SELECT Id,ParentId FROM FeedItem where ParentId in (Select Id FROM Account where Enterprise_Id__c = '223300109' ) LIMIT 1];

            ChatterRequestDTO chatterRequest = new ChatterRequestDTO();
            chatterRequest.FeedElementType = 'Coach Message';
            chatterRequest.PersonId = '223300109';
            chatterRequest.MessageText = 'Testing Unit test coverage';
            chatterRequest.FeedItemId = feedItemRequest.Id;
                        
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(chatterRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/ChatterService/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            ChatterService.postFeedGroup();
            
            Test.stopTest();
        }
    }

    /**
    * This is a test method for Post feed Comment for Coach different FeedItem and Coach eror
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedCommentCoachError()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            FeedItem feedItemRequest = [SELECT Id,ParentId FROM FeedItem where ParentId in (Select Id FROM Account where Enterprise_Id__c = '223300109' ) LIMIT 1];

            ChatterRequestDTO chatterRequest = new ChatterRequestDTO();
            chatterRequest.FeedElementType = 'Coach Message';
            chatterRequest.PersonId = '223300109';
            chatterRequest.MessageText = 'Testing Unit test coverage';
            chatterRequest.Subject = 'Blood Pressure';
            chatterRequest.FeedItemId = feedItemRequest.Id;
                        
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(chatterRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/ChatterService/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            ChatterService.postFeedGroup();
            
            Test.stopTest();
        }
    }

    /**
    * This is a test method for Post feed Comment
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedError()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            ChatterRequestDTO chatterRequest1 = new ChatterRequestDTO();
            chatterRequest1.FeedElementType = 'Feeds';
            chatterRequest1.PersonId = '223300104';
            chatterRequest1.MessageText = 'Testing Unit test coverage';
                                    
            RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(chatterRequest1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/ChatterService/*';  //Request URL
            req1.httpMethod = 'PUT';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            Test.startTest();
            ChatterService.postFeedGroup();
            
            Test.stopTest();
        }
    }

    /**
    * This is a test method for Post feed Comment
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedPersonIdError()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            ChatterRequestDTO chatterRequest1 = new ChatterRequestDTO();
            chatterRequest1.FeedElementType = 'Enrollment & Generic';
            chatterRequest1.PersonId = '22330104';
            chatterRequest1.MessageText = 'Testing Unit test coverage';
                                    
            RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(chatterRequest1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/ChatterService/*';  //Request URL
            req1.httpMethod = 'PUT';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            Test.startTest();
            ChatterService.postFeedGroup();
            
            Test.stopTest();
        }
    }

     /**
    * This is a test method for Post feed Comment
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_PostFeedPersonIdBlankError()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            ChatterRequestDTO chatterRequest1 = new ChatterRequestDTO();
            chatterRequest1.FeedElementType = 'Enrollment & Generic';
            chatterRequest1.PersonId = '';
            chatterRequest1.MessageText = 'Testing Unit test coverage';
                                    
            RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(chatterRequest1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/ChatterService/*';  //Request URL
            req1.httpMethod = 'PUT';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            Test.startTest();
            ChatterService.postFeedGroup();
            
            Test.stopTest();
        }
    }
    
     /**
    * This is a test method for Post feed Comment
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_Interaction()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            FeedItem feedItemRequest = [SELECT Id,ParentId FROM FeedItem LIMIT 1];
            Test.startTest();
            string feedItemParentId = feedItemRequest.ParentId;
            ChatterFeed_D_HUM.CreateDigitalInteraction(feedItemParentId);
            
            Test.stopTest();
        }
    }
    
    static testMethod void testChatterService_SearchFeed()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
       
            Account oAccount = [SELECT Id FROM Account WHERE Enterprise_Id__c ='223300104' LIMIT 1];
            list<FeedItem> chatterFeedlst = [SELECT Id,ParentId FROM FeedItem where ParentId = :oAccount.Id];
            //list<FeedItem> chatterFeedlst = [select Id,ParentId from FeedItem];
            // Build a simple feed item
            ConnectApi.RecordSummary chatterParent = new ConnectApi.RecordSummary();
            chatterParent.Id = oAccount.Id;
            set<string> setFeedItemIds = new set<string>();
            //Build a simple feed item page
            ConnectApi.FeedElementPage  testPage = new ConnectApi.FeedElementPage();
            List<ConnectApi.FeedItem> testItemList = new List<ConnectApi.FeedItem>();
            ConnectApi.FeedItem testFeedItem = new ConnectApi.FeedItem();
            testFeedItem.id = chatterFeedlst[0].Id;
            testFeedItem.parent = chatterParent;
            testFeedItem.CreatedDate = system.Today();
            testFeedItem.relativeCreatedDate = 'Just now.';
            setFeedItemIds.add(chatterFeedlst[0].Id);
            ConnectApi.FeedBody testFeedBody = new ConnectApi.FeedBody();
            List<ConnectApi.MessageSegment> testSegments = new List<ConnectApi.MessageSegment>();
            ConnectApi.TextSegment testSegment = new ConnectApi.TextSegment();
            testSegment.text = 'This is the feedBody.';
            testSegments.add(testSegment);
            testFeedBody.messageSegments = testSegments;
            testFeedBody.text = 'This is the feedBody.';
            testFeedItem.body = testFeedBody;
            testItemList.add(testFeedItem);
            testPage.elements = testItemList;

            ConnectApi.FeedElementPage  testPage1 = new ConnectApi.FeedElementPage();
            List<ConnectApi.FeedItem> testItemList1 = new List<ConnectApi.FeedItem>();
            ConnectApi.FeedItem testFeedItem1 = new ConnectApi.FeedItem();
            testFeedItem1.id = chatterFeedlst[1].Id;
            testFeedItem1.parent = chatterParent;
            testFeedItem1.CreatedDate = system.Today().addDays(-1);
            testFeedItem1.relativeCreatedDate = 'Just now.';
            setFeedItemIds.add(chatterFeedlst[1].Id);
            ConnectApi.FeedBody testFeedBody1 = new ConnectApi.FeedBody();
            List<ConnectApi.MessageSegment> testSegments1 = new List<ConnectApi.MessageSegment>();
            ConnectApi.TextSegment testSegment1 = new ConnectApi.TextSegment();
            testSegment1.text = 'Subject:- Wellness Coaching Feed This is the feedBody. for coverage';
            testSegments1.add(testSegment1);
            testFeedBody1.messageSegments = testSegments1;
            testFeedBody1.text = 'Subject:- Wellness Coaching Feed This is the feedBody. for coverage';
            testFeedItem1.body = testFeedBody1;
            testItemList1.add(testFeedItem1);
            testPage1.elements = testItemList1;
        
            // The method returns the test page, which we know has two items in it.
            ChatterGetFeedRequestDTO chatterRequest1 = new ChatterGetFeedRequestDTO();
            chatterRequest1.PersonId = '223300104';
            chatterRequest1.PageNo = '1';
            chatterRequest1.StartDate = System.Today().addDays(-2).format();
            chatterRequest1.EndDate = System.Today().addDays(1).format();
            
            ChatterGetFeedRequestDTO chatterRequest2 = new ChatterGetFeedRequestDTO();
            chatterRequest2.PersonId = '223300104';
            chatterRequest2.PageNo = '1';
            
            
            RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(chatterRequest1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/ChatterService/*';  //Request URL
            req1.httpMethod = 'POST';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            Test.startTest();
            ChatterService.getFeedItem();
            
            RestRequest req2 = new RestRequest();
            String JSONMsg2 = System.JSON.serialize(chatterRequest2);
            RestResponse res2 = new RestResponse();
        
            req2.requestURI = '/ChatterService/*';  //Request URL
            req2.httpMethod = 'POST';//HTTP Request Type
            req2.requestBody = Blob.valueof(JSONMsg2);
        
            RestContext.request = req2;
            RestContext.response= res2;
            ChatterService.getFeedItem();
            
            ChatterGetFeed_D_HUM.PopulateJoinFeedItems(testPage,testPage1,chatterRequest1);
            ChatterGetFeed_D_HUM.PopulateFeedComments(setFeedItemIds);
            ChatterGetFeed_D_HUM.PopulateFeedItem(testFeedItem1,chatterRequest1);
            
            Test.stopTest();
        }

    }
    
    /**
    * This is a test method for Get feed Comment
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_GetFeedError()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            // The method returns the test page, which we know has two items in it.
            ChatterGetFeedRequestDTO chatterRequest1 = new ChatterGetFeedRequestDTO();
            chatterRequest1.StartDate = '';
            chatterRequest1.EndDate = '';
            chatterRequest1.PersonId = '223300104';
            
                        
            RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(chatterRequest1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/ChatterService/*';  //Request URL
            req1.httpMethod = 'POST';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            Test.startTest();
            ChatterService.getFeedItem();
            
            ChatterGetFeedRequestDTO chatterRequest2 = new ChatterGetFeedRequestDTO();
            chatterRequest2.PersonId = '4567';
            
                        
            RestRequest req2 = new RestRequest();
            String JSONMsg2 = System.JSON.serialize(chatterRequest2);
            RestResponse res2 = new RestResponse();
        
            req2.requestURI = '/ChatterService/*';  //Request URL
            req2.httpMethod = 'POST';//HTTP Request Type
            req2.requestBody = Blob.valueof(JSONMsg2);
        
            RestContext.request = req2;
            RestContext.response= res2;
            
            ChatterService.getFeedItem();
            
            ChatterGetFeedRequestDTO chatterRequest3 = new ChatterGetFeedRequestDTO();
            chatterRequest3.PersonId = '';
            
                        
            RestRequest req3 = new RestRequest();
            String JSONMsg3 = System.JSON.serialize(chatterRequest3);
            RestResponse res3 = new RestResponse();
        
            req3.requestURI = '/ChatterService/*';  //Request URL
            req3.httpMethod = 'POST';//HTTP Request Type
            req3.requestBody = Blob.valueof(JSONMsg3);
        
            RestContext.request = req3;
            RestContext.response= res3;
            
            ChatterService.getFeedItem();
            
            Test.stopTest();
        }
    }
    
     /**
    * This is a test method for Get feed Comment
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testChatterService_GetFeedErrorException()
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test ChatterUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test ChatterUser2',true);
        
        System.runAs(oUser) 
        {
            // The method returns the test page, which we know has two items in it.
            ChatterGetFeedRequestDTO chatterRequest1 = new ChatterGetFeedRequestDTO();
            chatterRequest1.FeedItemId = 'All';
            chatterRequest1.PersonId = '223300104';
                        
            RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(chatterRequest1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/ChatterService/*';  //Request URL
            req1.httpMethod = 'POST';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            Test.startTest();
            ChatterService.getFeedItem();
            
            Test.stopTest();
        }
    }
}