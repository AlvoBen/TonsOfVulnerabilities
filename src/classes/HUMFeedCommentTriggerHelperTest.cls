/****************************************************************************
Apex Class Name  : HUMFeedCommentTriggerHelperTest 
Version          : 1.0 
Created Date     : 10th Nov,2020
Function         : Test class to HUMFeedCommentTriggerHelper  Apex class

Modification Log :
 *   Modification ID     Developer          Code Review      Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *           1.0         Shreya Agrawal               		11/10/2020          Original Version  
 *           1.1         Moshitha Gunasekaran           	06/18/2021          User Story 2377268:  T1PRJ0002081 / PR00092569 - MF #1799660  - SF - **Coaching ** - Push Notification to Go365
 **********************************************************************************************************************************/

@IsTest
private class HUMFeedCommentTriggerHelperTest 
{    
    /*  
     * Method name : testDataMaskingControllerForFeedComments
     * Description : This method tests the PHI masking on Comments on Community
     * Return Type : void
     * Parameter   : none
     */ 
    @IsTest(SeeAllData=true)
     static  void testDirectMessageFeedComments()
    {
        Test.startTest();
        List<String> memberIds = new List<String>();
        profile pAdmin = [select id from profile where Name='System Administrator'];
        profile p= [select id from profile where Name='Humana Community Member'];

        User user = new User(alias = 'test1234', email='test1234@noemail.com',
                                  emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US',
                                  localesidkey='en_US', profileid = pAdmin.id, country='United States',IsActive =true,
                                  timezonesidkey='America/Los_Angeles', username='tester1234@noemail.com');
        insert user;
        User user2 = new User(alias = 'test123', email='test123@noemail.com',
                            emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US',
                            localesidkey='en_US', profileid = pAdmin.id, country='United States',IsActive =true,
                            
                            timezonesidkey='America/Los_Angeles', username='testerdirectmsg@noemail.com');
            
            insert user2;
        	System.runAs(user)
            {
  
            Id recordtypeId = Schema.SObjectType.Community_Setup__c.getRecordTypeInfosByDeveloperName().get('Direct_Message_Setup').getRecordTypeId();
            Community_Setup__c objCommunitySetup = new Community_Setup__c();
            objCommunitySetup.Moderator__c = user.Id;
            objCommunitySetup.Is_Active_Moderator__c = true;
            objCommunitySetup.RecordTypeId=recordtypeId;
            insert objCommunitySetup;
            System.assertEquals(UserInfo.getUserId(), objCommunitySetup.Moderator__c);
                
            Id recType = Schema.SObjectType.Account.getRecordTypeInfosbyDeveloperName().get('Member').getRecordTypeId();
            Account objAccount = new Account();
            objAccount.FirstName='Test Firstname';
            objAccount.LastName='Test LastName';
            objAccount.RecordTypeId =recType;
            insert objAccount;
            System.assertEquals('Test Firstname', objAccount.FirstName);
                
            NetworkMember objNetworkMem = [SELECT NetworkId,memberId from NetworkMember where memberId=:user.Id limit 1];
			
            
            //Direct message start
			ConnectApi.FeedItemInput feedItemInput = new ConnectApi.FeedItemInput();
            ConnectApi.TextSegmentInput textSegmentInput = new ConnectApi.TextSegmentInput();
            textSegmentInput.text = 'param';
            ConnectApi.MessageBodyInput messageBodyInput = new ConnectApi.MessageBodyInput();
            messageBodyInput.messageSegments = new List<ConnectApi.MessageSegmentInput>();
            messageBodyInput.messageSegments.add(textSegmentInput);
            feedItemInput.body = messageBodyInput;
            String strId = String.valueOf(user2.Id);
            memberIds.add(strId);

            ConnectApi.DirectMessageCapabilityInput dmInput = new ConnectApi.DirectMessageCapabilityInput();
            dmInput.subject = System.Label.Community_Moderator_Chat;
            dmInput.membersToAdd = memberIds;
            
            ConnectApi.FeedElementCapabilitiesInput feedElementCapabilitiesInput = new ConnectApi.FeedElementCapabilitiesInput();
            feedElementCapabilitiesInput.directMessage = dmInput;
            
            feedItemInput.capabilities = feedElementCapabilitiesInput;
            ConnectApi.FeedElement feedElement = ConnectApi.ChatterFeeds.postFeedElement(objNetworkMem.NetworkId,feedItemInput);
			FeedItem objFeedItem = [Select Id, parentId, Body FROM FeedItem where createdbyid = :Userinfo.getUserId() limit 1];
            //Create Content Version          
            ContentVersion contentVersion = new ContentVersion(
                Title = 'Penguins',
                PathOnClient = 'Penguins.jpg',
                VersionData = Blob.valueOf('Test Content'),
                IsMajorVersion = true
            );
            insert contentVersion;
            FeedComment Comment = new FeedComment(); 
            Comment.FeedItemId= objFeedItem.Id;
            Comment.CommentBody = 'test attachment';
            Comment.CommentType='ContentComment';
            Comment.RelatedRecordId = contentVersion.Id;  
                insert Comment;
                FeedComment objFeedComment = [SELECT Id,CommentType,FeedItemId,createdbyid,createddate from FeedComment where CommentType='ContentComment' and FeedItemId=:objFeedItem.Id and createdbyid = :Userinfo.getUserId() order by createddate desc limit 1];
                List<ComDirectMsg_Attachment__c> lstatt = [Select Id, Version_Id__c,DocId__c,Attachment_URL__c,Related_DirectMessage__c from ComDirectMsg_Attachment__c where Related_DirectMessage__c =  :objFeedComment.Id limit 1];
            if(!lstatt.isEmpty())
            {
                HUMFeedCommentTriggerHelper.sendRequestToEhub('attachmentStdId', lstatt[0].Id, 'sBody', 'sName', 'sUser', objFeedComment.Id, 'urlPath');
            }
            Test.stopTest();
        }
    }

    /*  
     * Method name : testforChatter
     * Description : This method tests the feed comment for chatter
     * Return Type : void
     * Parameter   : none
     */   
	private static testmethod void testforChatter()
	{
        User oUser2 = HUMTestDataHelperTest.createTestUser('CoachUser','System Administrator');
        oUser2.FirstName = 'firsttest CoachUser';
		insert oUser2;
		system.runAs(oUser2) 
		{
			Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
            insert acc;      
            
            CollaborationGroup collGroup = new CollaborationGroup (Name = 'CoachingTesting', CollaborationType = 'Private');
            insert collGroup;
            
			FeedItem feedChatter = new FeedItem(ParentId = oUser2.Id, Type = 'TextPost', IsRichText = true, Body = 'Testing For Unit class');
			test.starttest();
			
			insert new CRMFunctionality_ONOFF_Switch__c(Name = '2377268',IsON__c=true,Release_Name__c='07/23/2021',Scrum_Team__c='Transformers',Story_Title__c='User Story 2377268: T1PRJ0002081 / PR00092569 - MF #1799660  - SF - **Coaching ** - Push Notification to Go365');
			insert new HUM_Webservice_Callout__c(Name='PushNotificationService', Certificate_Name__c='Humana_GuidanceCare_MOCK', SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/PushNotificationService/SendMessage', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='application/json',Methods_Available__c='A;B;C',Enable_Service_Tester__c=true);                    
            insert feedChatter;
            FeedComment feedCommentChatter1 = new FeedComment(FeedItemId = feedChatter.Id, IsRichText = false, CommentBody = 'Testing For class Commennts');
        	insert feedCommentChatter1;
			
			ConnectApi.Comment testFeedComment = new ConnectApi.Comment();
			testFeedComment.id = feedCommentChatter1.Id;
			ConnectApi.Reference referFeedtest = new ConnectApi.Reference();
			referFeedtest.id = feedChatter.Id;
			testFeedComment.parent = referFeedtest;
			testFeedComment.CreatedDate = system.Today();
			testFeedComment.relativeCreatedDate = 'Just now.';
			ConnectApi.FeedBody testFeedBody = new ConnectApi.FeedBody();
			List<ConnectApi.MessageSegment> testSegments = new List<ConnectApi.MessageSegment>();
			ConnectApi.EntityLinkSegment entitytestSegment = new ConnectApi.EntityLinkSegment();
			ConnectApi.Reference refertest = new ConnectApi.Reference();
			refertest.id = acc.Id;
			entitytestSegment.reference = refertest;
			testSegments.add(entitytestSegment);
			testFeedBody.messageSegments = testSegments;
			testFeedBody.text = 'This is the feedBody.';
			testFeedComment.body = testFeedBody;

			HUMFeedCommentTriggerHelper.postChatterWithSubject(testFeedComment, feedCommentChatter1.Id);
			test.stoptest();
		}
		
	}
}