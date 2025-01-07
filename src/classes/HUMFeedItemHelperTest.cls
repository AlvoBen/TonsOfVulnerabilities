/*******************************************************************************************************************************
Apex Class Name : HUMFeedItemHelperTest 
Version         : 1.3
Created On      : 08/20/2014
Function        : This class is used for testing HUMFeedItemHelper class

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pranali Choudhari         17773                      08/20/2014                Original version
 * Pranali Choudhari         17773                      08/21/2014                Added additional methods
 * Pranali Choudhari         17773                      08/25/2014                Changed code according to best practices
 * Rakesh Patni				 350876						04/11/2019				  Commented References to Article Types before Migration
 * Rakesh Patni				 351706  					04/15/2019				  Updated Article Type Reference with Knowledge__Kav post migration.
 * Moshitha Gunasekaran                                 06/14/2021                Made changes as per latest ICD to add FeedItemId and Date functionalities				
 * Moshitha Gunasekaran           				        06/18/2021                User Story 2377268:  T1PRJ0002081 / PR00092569 - MF #1799660  - SF - **Coaching ** - Push Notification to Go365
 ****************************************************************************************************************************/

@isTest
private class HUMFeedItemHelperTest{
    /*  
     * Method name : testforDraftArticle
     * Description : This method tests the feed comment on draft article
     * Return Type : void
     * Parameter   : none
     */   
	private static testmethod void testforDraftArticle()
	{

		Knowledge__Kav kaArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaArticle;
		KnowledgeArticleVersion kavDraftArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Draft' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		FeedItem oFeedItem = new FeedItem();
		oFeedItem.ParentId = kavDraftArticle1.KnowledgeArticleId;
		oFeedItem.Body = '#publish publish this';
		test.starttest();
		insert oFeedItem;
		test.stoptest();
		Article_Feedback__c oArtFeed = [Select Id, User_Comments__c, ChatterFeedID__c from Article_Feedback__c where ChatterFeedID__c = :oFeedItem.Id];
		system.assertNotEquals(oArtFeed.Id, null);
		system.assertEquals(oArtFeed.User_Comments__c, oFeedItem.Body);
	}
	/*
	* Method name : testForPublishedConcept
	* Description : This method tests the feed comment on published concept article
	* Return Type : void
	* Parameter   : none
	*/
	private static testmethod void testForPublishedConcept()
	{
		Knowledge__Kav kaArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaArticle;
		knowledgeArticleVersion kavDraftArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Draft' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		//Publish the knowledge article
		KbManagement.PublishingService.publishArticle(kavDraftArticle1.KnowledgeArticleId, true);
		knowledgeArticleVersion kavPubArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Online' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		FeedItem oFeedItem = new FeedItem();
		oFeedItem.ParentId = kavPubArticle1.KnowledgeArticleId;
		oFeedItem.Body = '#feedback change this';
		test.starttest();
		insert oFeedItem;
		test.stoptest();
		Article_Feedback__c oArtFeed = [Select Id, User_Comments__c, ChatterFeedID__c from Article_Feedback__c where ChatterFeedID__c = :oFeedItem.Id];
		system.assertNotEquals(oArtFeed.Id, null);
		system.assertEquals(oArtFeed.User_Comments__c, oFeedItem.Body);
	}
	/*
	* Method name : testForPublishedError
	* Description : This method tests the wrong feed comment on published article logged in as Customer are SPecialist
	* Return Type : void
	* Parameter   : none
	*/
	private static testmethod void testForPublishedError()
	{
		Knowledge__Kav kaArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaArticle;
		knowledgeArticleVersion kavDraftArticle2 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Draft' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		KbManagement.PublishingService.publishArticle(kavDraftArticle2.KnowledgeArticleId, true);
		knowledgeArticleVersion kavPubArticle2 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Online' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		User u = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
		insert u;
		List<Article_Feedback__c> lstArtFeed;
		FeedComment oFeedCom;
		FeedItem oFeedItem2 = new FeedItem();
		system.runAs(u)
		{
			oFeedItem2.ParentId = kavPubArticle2.KnowledgeArticleId;
			oFeedItem2.Body = '#publish change this';
			test.starttest();
			insert oFeedItem2;
			test.stoptest();
			lstArtFeed = [Select Id, User_Comments__c, ChatterFeedID__c from Article_Feedback__c where ChatterFeedID__c = :oFeedItem2.Id];
		}
		oFeedCom = [Select Id, FeedItemId from FeedComment where FeedItemId = :oFeedItem2.Id];
		system.assertEquals(lstArtFeed.size(), 0);
		system.assertNotEquals(oFeedCom.Id, null);
	}
	/*
	* Method name : testCreateFeedbackNegative
	* Description : This method tests the wrong feed comment(#publish) on published article
	* Return Type : void
	* Parameter   : none
	*/
	private static testmethod void testCreateFeedbackNegative()
	{
		Knowledge__Kav kaArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaArticle;
		knowledgeArticleVersion kavDraftArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Draft' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		KbManagement.PublishingService.publishArticle(kavDraftArticle1.KnowledgeArticleId, true);
		knowledgeArticleVersion kavPubArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Online' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		FeedItem oFeedItem = new FeedItem();
		oFeedItem.ParentId = kavPubArticle1.KnowledgeArticleId;
		oFeedItem.Body = '#publish publish this';
		test.starttest();
		insert oFeedItem;
		test.stoptest();
		List<Article_Feedback__c> lstArtFeed = new List<Article_Feedback__c>();
		lstArtFeed = [Select Id, User_Comments__c, ChatterFeedID__c from Article_Feedback__c where ChatterFeedID__c = :oFeedItem.Id];
		system.assertEquals(lstArtFeed.size(), 0);
		FeedComment oFeedCom = [Select Id, FeedItemId from FeedComment where FeedItemId = :oFeedItem.Id];
		system.assertNotEquals(oFeedCom.Id, null);

	}
	/*
	* Method name : testWrongInput
	* Description : This method tests the wrong feed comment(#) on published article
	* Return Type : void
	* Parameter   : none
	*/
	private static testmethod void testWrongInput()
	{
		Knowledge__Kav kaArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaArticle;
		knowledgeArticleVersion kavDraftArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Draft' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		KbManagement.PublishingService.publishArticle(kavDraftArticle1.KnowledgeArticleId, true);
		knowledgeArticleVersion kavPubArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Online' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		FeedItem oFeedItem2 = new FeedItem();
		oFeedItem2.ParentId = kavPubArticle1.KnowledgeArticleId;
		oFeedItem2.Body = '# ';
		test.starttest();
		insert oFeedItem2;
		test.stoptest();
		List<Article_Feedback__c> lstArtFeed = new List<Article_Feedback__c>();
		lstArtFeed = [Select Id, User_Comments__c, ChatterFeedID__c from Article_Feedback__c where ChatterFeedID__c = :oFeedItem2.Id];
		system.assertEquals(lstArtFeed.size(), 0);
		FeedComment oFeedCom = [Select Id, FeedItemId from FeedComment where FeedItemId = :oFeedItem2.Id];
		system.assertNotEquals(oFeedCom.Id, null);
	}
	/*
	* Method name : testLargeBodyException
	* Description : This method tests the exception in insertion of feeditems of large size
	* Return Type : void
	* Parameter   : none
	*/
	private static testmethod void testLargeBodyException()
	{
		Knowledge__Kav kaArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaArticle;
		knowledgeArticleVersion kavDraftArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Draft' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		KbManagement.PublishingService.publishArticle(kavDraftArticle1.KnowledgeArticleId, true);
		knowledgeArticleVersion kavPubArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Online' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		FeedItem oFeedItem2 = new FeedItem();
		oFeedItem2.ParentId = kavPubArticle1.KnowledgeArticleId;
		String largeBody = 'This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.' +
			'This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.' +
			'This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.' +
			'This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.' +
			'This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.' +
			'This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.' +
			'This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.' +
			'This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.This is containing large body.';

		oFeedItem2.Body = '#Feedback ' + largeBody;
		test.starttest();
		insert oFeedItem2;
		test.stoptest();
		List<Article_Feedback__c> lstArtFeed = new List<Article_Feedback__c>();
		lstArtFeed = [Select Id, User_Comments__c, ChatterFeedID__c from Article_Feedback__c where ChatterFeedID__c = :oFeedItem2.Id];
		system.assertEquals(lstArtFeed.size(), 0);
		FeedComment oFeedCom = [Select Id, FeedItemId from FeedComment where FeedItemId = :oFeedItem2.Id];
		system.assertNotEquals(oFeedCom.Id, null);
	}
	/*
	* Method name : testForPublishedKnowledgeError
	* Description : This method tests the publishing of knowledge error
	* Return Type : void
	* Parameter   : none
	*/
	private static testmethod void testForPublishedKnowledgeError()
	{
		Knowledge__Kav kaArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaArticle;
		knowledgeArticleVersion kavDraftArticle2 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Draft' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		KbManagement.PublishingService.publishArticle(kavDraftArticle2.KnowledgeArticleId, true);
		knowledgeArticleVersion kavPubArticle2 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Online' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		User u = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
		insert u;
		List<Article_Feedback__c> lstArtFeed;
		FeedComment oFeedCom;
		FeedItem oFeedItem2 = new FeedItem();
		system.runAs(u)
		{
			oFeedItem2.ParentId = kavPubArticle2.KnowledgeArticleId;
			oFeedItem2.Body = '#publish change this';
			test.starttest();
			insert oFeedItem2;
			test.stoptest();
			lstArtFeed = [Select Id, User_Comments__c, ChatterFeedID__c from Article_Feedback__c where ChatterFeedID__c = :oFeedItem2.Id];
		}
		oFeedCom = [Select Id, FeedItemId from FeedComment where FeedItemId = :oFeedItem2.Id];
		system.assertEquals(lstArtFeed.size(), 0);
		system.assertNotEquals(oFeedCom.Id, null);
	}
	/*
	* Method name : testBulkKnowledgeRecords
	* Description : This method tests the bulk knowledge records
	* Return Type : void
	* Parameter   : none
	*/
	public static testmethod void testBulkKnowledgeRecords()
	{
		Knowledge__Kav kaArticle = HUMTestDataHelperTest.getKnowledgeArticle();
		insert kaArticle;
		knowledgeArticleVersion kavDraftArticle1 = [select id, KnowledgeArticleId from knowledgeArticleVersion where publishStatus = 'Draft' and language = 'en_US' and Title = 'test Knowledge' limit 1];
		List<FeedItem> lstFeedItem = new List<FeedItem>();
		for (Integer i = 0; i<100; i++)
		{
			FeedItem oFeedItem = new FeedItem();
			oFeedItem.ParentId = kavDraftArticle1.KnowledgeArticleId;
			oFeedItem.Body = '#publish';
			lstFeedItem.add(oFeedItem);
		}
		try
		{
			test.starttest();
			insert lstFeedItem;
			test.stoptest();
		}
		catch (Exception e)
		{
			System.assert(e.getMessage().contains(System.Label.HUMArtBulkError));
		}
	}

	/*  
     * Method name : testforChatter
     * Description : This method tests the feed item for chatter
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
            
			FeedItem feedChatter = new FeedItem(ParentId = oUser2.Id, Type = 'TextPost', IsRichText = true, Body = 'Testing For Unit class');
			test.starttest();
			
			insert new CRMFunctionality_ONOFF_Switch__c(Name = '2377268',IsON__c=true,Release_Name__c='07/23/2021',Scrum_Team__c='Transformers',Story_Title__c='User Story 2377268: T1PRJ0002081 / PR00092569 - MF #1799660  - SF - **Coaching ** - Push Notification to Go365');
			insert new HUM_Webservice_Callout__c(Name='PushNotificationService', Certificate_Name__c='Humana_GuidanceCare_MOCK', SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/PushNotificationService/SendMessage', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='application/json',Methods_Available__c='A;B;C',Enable_Service_Tester__c=true);                    
			insert feedChatter;
			
			ConnectApi.FeedItem testFeedItem = new ConnectApi.FeedItem();
			testFeedItem.id = feedChatter.Id;
			ConnectApi.RecordSummary chatterParent = new ConnectApi.RecordSummary();
			chatterParent.Id = oUser2.Id;
			testFeedItem.parent = chatterParent;
			testFeedItem.CreatedDate = system.Today();
			testFeedItem.relativeCreatedDate = 'Just now.';
			ConnectApi.FeedBody testFeedBody = new ConnectApi.FeedBody();
			List<ConnectApi.MessageSegment> testSegments = new List<ConnectApi.MessageSegment>();
			ConnectApi.TextSegment testSegment = new ConnectApi.TextSegment();
			testSegment.text = 'This is the feedBody.';
			testSegments.add(testSegment);
			ConnectApi.EntityLinkSegment entitytestSegment = new ConnectApi.EntityLinkSegment();
			ConnectApi.Reference refertest = new ConnectApi.Reference();
			refertest.id = acc.Id;
			entitytestSegment.reference = refertest;
			testSegments.add(entitytestSegment);
			ConnectApi.HashtagSegment hashtestSegment = new ConnectApi.HashtagSegment();
			hashtestSegment.tag = 'Taggingfor testing';
			testSegments.add(hashtestSegment);
			ConnectApi.MarkupBeginSegment markUpBegiSegment = new ConnectApi.MarkupBeginSegment();
			markUpBegiSegment.markupType = ConnectApi.MarkupType.Paragraph;
			testSegments.add(markUpBegiSegment);
			ConnectApi.MarkupEndSegment markUpEndSegment = new ConnectApi.MarkupEndSegment();
			markUpEndSegment.markupType = ConnectApi.MarkupType.Paragraph;
			testSegments.add(markUpEndSegment);
			ConnectApi.MentionSegment mentionTestSegment = new ConnectApi.MentionSegment();
			ConnectApi.RecordSummary actTestId = new ConnectApi.RecordSummary();
			actTestId.Id = '004jafafjsjfndlfm';
			mentionTestSegment.record = actTestId;
			testSegments.add(mentionTestSegment);
			testFeedBody.messageSegments = testSegments;
			testFeedBody.text = 'This is the feedBody.';
			testFeedItem.body = testFeedBody;

			HUMFeedItemHelper ofeedHelper = new HUMFeedItemHelper();
			ofeedHelper.postChatterWithSubject(testFeedItem, feedChatter.Id);
			test.stoptest();
		}
		
	}
}